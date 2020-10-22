package com.example.highplattest.systest;

import java.util.ArrayList;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.BatteryReceiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest51.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150606
 * directory 		: 
 * description 		: 取电量/蓝牙交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  zhengxq			20150606		create
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest51 extends DefaultFragment
{
	final String TAG = SysTest51.class.getSimpleName();
	final String TESTITEM = "电池信息/BT";
	final int BUFSIZE_BT = BT_BUF_SIZE;
	final IntentFilter intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private Gui gui = null;
	private BatteryReceiver mBatteryReceiver;
	
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private Config config;
	
	public void systest51() throws Exception
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		mBatteryReceiver = new ReceiverTracker().new BatteryReceiver();
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr());
			try 
			{
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("取电量/BT\n0.BT配置\n1.交叉测试");
			switch (returnValue) 
			{
				
			case '0':
				// 蓝牙配置
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				break;
				
			case '1':
				cross_test();
				break;
			
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0,succ = 0,bak = 0;
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/	
		if(GlobalVariable.gSequencePressFlag)
			sendPacket.setLifecycle(getCycleValue());
		else
			sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		while(cnt>0)
		{

			if(gui.cls_show_msg1(3, "请打开BluetoothServer工具，%s交叉测试,已执行%d次,成功%d次,[取消]退出测试" ,TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(3000);
			}
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT建立连接失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			byte[] tempBuf = new byte[5];
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
				continue;
			}
			// 取电量的广播注册
			myactivity.registerReceiver(mBatteryReceiver, intent);
			SystemClock.sleep(1000L);
			
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			// 取电量的广播注册
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line:%d:第%d次:数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			// 取电量的广播注册
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			succ++;
			postEnd(bluetoothAdapter);
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s交叉测试完成,已执行次数为%d,成功为%d次",TESTITEM,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		myactivity.unregisterReceiver(mBatteryReceiver);
		// 发送结束标志
		bluetoothManager.cancel(dataService);
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
	}
}
