package com.example.highplattest.systest;

import java.util.ArrayList;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest19.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150420
 * directory 		: 
 * description 		: 磁卡/蓝牙交叉测试
 * related document :
 * history 		 	: 变更记录					变更时间			变更人员
 *			  		 测试前置添加磁卡解绑事件			20200415	 	郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest19 extends DefaultFragment 
{
	public final String TAG = SysTest19.class.getSimpleName();
	private final String TESTITEM = "磁卡/BT";
	private final int BUFSIZE_BT = BT_BUF_SIZE;
	SocketUtil socketUtil;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private Gui gui = new Gui(myactivity, handler);
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	Config config;
	
	public void systest19() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 测试前置：解绑磁卡事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_MAGCARD});
		config = new Config(myactivity, handler);
		//初始化处理器，连接K21设备
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("磁卡/BT\n0.BT配置\n1.交叉测试");
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
		int bak =0,cnt = 0,succ = 0,ret = 0;
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
	
		bak = cnt = sendPacket.getLifecycle();
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "请打开BluetoothServer工具，%s交叉测试,已执行%d次，成功%d次，[取消]退出测试", TESTITEM,bak-cnt,succ)==ESC)
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
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
				continue;
			}
			
			
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line:%d:第%d次数据校验失败%s", Tools.getLineInfo(),bak-cnt,false);
				postEnd(bluetoothAdapter);
				continue;
			}
			// 断开蓝牙连接
			postEnd(bluetoothAdapter);
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			succ++;
		}
		postEnd(bluetoothAdapter);
		// 解绑磁卡事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		bluetoothManager.cancel(dataService);
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
	}
}
