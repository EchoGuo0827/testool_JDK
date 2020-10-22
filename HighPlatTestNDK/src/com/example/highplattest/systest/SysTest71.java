package com.example.highplattest.systest;

import java.io.IOException;
import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest71.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160902
 * directory 		: 
 * description 		: NFC/BT
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160902	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest71 extends DefaultFragment
{
	private final String TAG = SysTest71.class.getSimpleName();
	private final String TESTITEM = "NFC/BT";
	Nfc_Card nfc_card = Nfc_Card.NFC_B;
	private Gui gui = null;
	private Config config;
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	
	public void systest71()
	{
		gui = new Gui(myactivity, handler);
		// 测试前置
		config = new Config(myactivity, handler);

		bluetoothManager = BluetoothManager.getInstance(myactivity);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr());
			nfc_config(handler,TESTITEM);
			try 
			{
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG,TAG,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		// 测试主入口
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/BT\n0.NFC配置\n1.BT配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 配置nfc
				nfc_config(handler,TESTITEM);
				break;
				
			case '1':
				// 配置BT
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				break;
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG,TAG,2, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
		
	}
	
	/**
	 * NFC/BT交叉
	 */
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0,succ = 0,ret = -1,bak = 0;
		byte[] rbuf = new byte[BT_BUF_SIZE];
		byte[] wbuf = new byte[BT_BUF_SIZE];
		PacketBean sendPacket = new PacketBean();
		NfcTool nfcTool = new NfcTool(myactivity);
		
		/*process body*/	
		if(GlobalVariable.gSequencePressFlag)
			sendPacket.setLifecycle(getCycleValue());
		else
			sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//提示信息
		gui.cls_show_msg("测试前请确保，已安装%s卡,完成任意键继续",nfc_card);
		
		while(cnt>0)
		{
			//保护动作
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(3, "请打开BluetoothServer工具，%s/BT交叉测试,已执行%d次,成功%d次,[取消]退出测试",nfc_card,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(3000);
			}
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:BT建立连接失败", Tools.getLineInfo(),bak-cnt);
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
			//NFC卡连接
			if((ret = nfcTool.nfcConnect(reader_flag)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:%s卡激活失败(%d)", Tools.getLineInfo(),bak-cnt,nfc_card,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			//NFC读写
			try {
				if((ret = nfcTool.nfcRw(nfc_card)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:%s卡APDU失败(ret = %d)", Tools.getLineInfo(),bak-cnt,nfc_card,ret);
					postEnd(bluetoothAdapter);
					continue;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:%s卡APDU失败(ret = %d)", Tools.getLineInfo(),bak-cnt,nfc_card,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line:%d:第%d次数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			nfcTool.nfcDisEnableMode();
			succ++;
			postEnd(bluetoothAdapter);
		}
		gui.cls_show_msg1_record(TAG,"cross_test", g_time_0,"%s/BT交叉测试完成,已执行次数为%d,成功为%d次", nfc_card,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		// 发送结束标志
		bluetoothManager.cancel(dataService);
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
	}
}
