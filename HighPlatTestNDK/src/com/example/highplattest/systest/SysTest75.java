package com.example.highplattest.systest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_LED;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.tool.trade.TradeTool;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.newland.ndk.SecKcvInfo;
import com.newland.ndk.TimeNewland;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest75.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171220
 * directory 		: 
 * description 		: 实际应用流程测试（事件机制与非事件机制都应该有）
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq			20171220	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest75 extends DefaultFragment
{
	private Gui gui = new Gui(myactivity, handler);
	private  String TESTITEM = "实际应用流程测试";
	private final String TAG = "SysTest75";
	private final int wait_time = 2;/**显示等待时间*/
	private int rfstatus = 0;
	private int magstatus = 0;
	private int icstatus = 0;
	private Object object = new Object();
	private int ret=-1;
	public enum AppType
	{
		Union,Xing,TongL,SDK2;
	}
	
	public void systest75()
	{
		if(GlobalVariable.gSequencePressFlag)
		{
			rfid_powerUp(5*60);// 寻卡超时为5min
			if(GlobalVariable.gCustomerID==CUSTOMER_ID.SDK_3)
				OpenCard_Event(30);
			return;
		}
		
		while(true)
		{
			int nKeyIn = gui.cls_show_msg("实际应用流程测试\n1.非接卡流程测试(上电判断)\n2.读卡器流程测试(事件机制)\n3.实际-消费(SDK2.0-SAK)\n4.实际-消费(SDK3.0)\n");
			switch (nKeyIn) {
			case '1':
				rfid_powerUp(5*60);
				break;
				
			case '2':// 读卡器操作
				icstatus = 0;
				magstatus = 0;
				rfstatus = 0;
				OpenCard_Event(30);
				break;
				
				
			case '3':// 银商,SDK2.0
				try {
					TradeTool tradeTool = TradeTool.getInstance(myactivity, handler);
					tradeTool.trans_sdk2(5*60,false);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				break;
				
			/*case '4':// 通联
				icstatus = 0;
				magstatus = 0;
				rfstatus = 0;
				tongLProcess();
				break;

			case '5':// 银联云
				try {
					yinLianYunPOSProcess();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;*/
				
			case '4':// 快钱
				icstatus = 0;
				magstatus = 0;
				rfstatus = 0;
				kuaiQianProcess();
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}

	}
	
	/**
	 * 射频卡应用的流程,这种方式是通过JNI_Rfid_PiccActivate接口的返回值，判断目前是CPU卡还是M1卡
	 */
	private void rfid_powerUp(int nSec)
	{
		int iRet = -1;
		float time_use = 0;// 已使用的寻卡时间
		byte[] psPiccType = new byte[1];
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		byte[] select_1pay = {0x00,(byte) 0x84,0x00,0x00,0x08};
		
		
		// M1卡参数
		byte[] psSakBuf = new byte[1];
		byte[] key = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
		byte[] out=new byte[256];
		
		long startTime=0;
		
		gui.cls_printf("射频卡应用流程测试中,请挥卡...".getBytes());
		startTime = System.currentTimeMillis();
		if(GlobalVariable.sdkType==SdkType.SDK3)// 事件机制才需要注册 非事件机制不需要注册
		{
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(),10*1000,listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:射频模块注册事件失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}
		}
		
		if((iRet = JniNdk.JNI_Rfid_Init(null))!=0)
		{
			gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:射频模块初始化失败(%d)", Tools.getLineInfo(),iRet);
			return;
		}
		
		time_use = Tools.getStopTime(startTime);
		while(time_use<nSec)// 已使用的时间小于总寻卡的超时时间
		{
			rfstatus = 0;// 复位状态
			if((iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte) 10))!=0)
			{
				gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:射频模块下电失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}
			// 重启事件，判断下NDK的版本是否大于1.0.19（此版本增加了重启事件的接口）
//			JniNdk.JNI_Sys_Getlibver(ndkVer);
//			String strNdkVer= new String(ndkVer);
//			JniNdk.JNI_SYSResumeEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			// 表示A、B、M1卡
			if((iRet = JniNdk.JNI_Rfid_PiccType((byte) 0xcd))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidProcess", g_keeptime, "line %d:射频模块设置寻卡策略失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}
			if(nSec!=0&&GlobalVariable.sdkType==SdkType.SDK3)// 等待寻卡完成
			{
				synchronized (object) 
				{
					LoggerUtil.e("start lock");
					try {
						object.wait(4*1000);// 锁个4s要是还没寻到卡，重新开启事件寻卡？
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					LoggerUtil.e("end lock");
				}
				if(rfstatus!=1)// 未寻到卡
				{
					// 在监听的时间内未寻到卡进行重启事件的动作
					time_use = Tools.getStopTime(startTime);
					continue;
				}
			}
			if(GlobalVariable.sdkType == SdkType.SDK2)// 如果是SDK2.0增加个寻卡的操作
			{
				if((iRet = JniNdk.JNI_Rfid_PiccDetect(null))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:射频模块寻卡失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
			}
			
			// M1卡的流程
			if((iRet = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen, psDataBuf))==NDK_ERR_RFID_PROTOCOL)
			{
				if((iRet = JniNdk.JNI_Rfid_PiccType((byte) 0xcc))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1寻卡失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
					
				if((iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte) 10))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:射频模块下电失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				if((iRet = JniNdk.JNI_Rfid_M1Request((byte)1, pnDataLen, psDataBuf))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1卡request失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				if((iRet = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1卡anti失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				if((iRet = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], psDataBuf, psSakBuf))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1卡select失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				if((iRet = JniNdk.JNI_Rfid_M1ExternalAuthen(pnDataLen[0], psDataBuf, (byte)0x61, key, (byte)0x01))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1卡外部认证失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
					
				// 之前认证的块为01，应该01-04块都可进行读写操作
				if((iRet = JniNdk.JNI_Rfid_M1Read((byte)0x01, pnDataLen, out))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1卡块读失败(%d)",Tools.getLineInfo(),iRet);
					return;
				}
				LoggerUtil.d("out:"+ISOUtils.hexString(out, 16));
				if(Tools.memcmp(DATA16, out, pnDataLen[0])==false)// 首次
					System.arraycopy(out, 0, DATA16, 0, pnDataLen[0]);
				else if(Tools.memcmp(DATA16, out, pnDataLen[0])==false)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:M1卡数据校验失败(%d)",Tools.getLineInfo(),pnDataLen[0]);
					return;
				}
				{
					gui.cls_show_msg1_record(TAG, TAG, wait_time,"M1卡寻卡+读写操作成功");
					break;
				}
					
			}
			else if(iRet==NDK_OK)// CPU卡的流程
			{
				if((iRet = JniNdk.JNI_Rfid_PiccApdu(select_1pay.length, select_1pay, pnDataLen, psDataBuf))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_powerUp", g_keeptime, "line %d:CPU卡APDU失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				if(pnDataLen[0]==10)
				{
					// CPU卡操作
					gui.cls_show_msg1_record(TAG, TAG, wait_time,"CPU卡上电成功+取随机数操作成功");
					break;
				}
				else
				{
					gui.cls_show_msg1_record(TAG, TAG, wait_time,"CPU卡上电成功,支持取随机数");
					break;
				}
			}
			else
				gui.cls_show_msg1_record(TAG, TAG, wait_time,"line %d:射频卡上电失败(%d)", Tools.getLineInfo(),iRet);
		}
		
		JniNdk.JNI_Rfid_PiccDeactivate((byte) 10);
		
		if(GlobalVariable.sdkType==SdkType.SDK3)// 事件机制才需要注销 非事件机制不需要注销
		{
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
		}
	}
	
	/**
	 * 射频卡操作,使用SAK方式判断A卡和M1卡
	 */
	private int rfidRead(byte piccType)
	{
		int iRet = -1;
		byte[] psPiccType = new byte[1];
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		
		
		// M1卡参数
		byte[] psSakBuf = new byte[1];
		byte[] key = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
		byte[] out=new byte[256];
		
		gui.cls_printf("射频卡应用流程测试中...".getBytes());
		
		if((iRet = JniNdk.JNI_Rfid_Init(null))!=0)
		{
			gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:射频模块初始化失败(%d)", Tools.getLineInfo(),iRet);
			return iRet;
		}
		
		if((iRet = JniNdk.JNI_Rfid_PiccType(piccType))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:射频模块设置寻卡策略失败(%d)", Tools.getLineInfo(),iRet);
			return iRet;
		}
			
		if((iRet = JniNdk.JNI_Rfid_M1Request((byte)1, pnDataLen, psDataBuf))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:M1Request失败(%d)", Tools.getLineInfo(),iRet);
			return iRet;
		}
			
		if((iRet = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:M1Anti失败(%d)", Tools.getLineInfo(),iRet);
			return iRet;
		}
			
		if((iRet = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], psDataBuf, psSakBuf))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:M1Select失败(%d)", Tools.getLineInfo(),iRet);
			return iRet;
		}
		if((psSakBuf[0]&0x20)==0x20)//A卡
		{
			if((iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte)10))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_CloseRf())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_PiccType(piccType))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_PiccDetect(psPiccType))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_Init(null))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;	
			}
			if((iRet = JniNdk.JNI_Rfid_OpenRf())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen, psDataBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_PiccApdu(req.length, req, pnDataLen, psDataBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:CPU卡APDU失败（%d）", Tools.getLineInfo(),iRet);
				return iRet;
			}
			if((iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte)10))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return iRet;	
			}
			if(pnDataLen[0]==10)
			{
				// CPU卡操作
				gui.cls_show_msg("CPU卡上电成功+取随机数操作成功,任意键继续");
			}
			else
			{
				gui.cls_show_msg("CPU卡上电成功，不支持取随机数,任意键继续");
			}
		}
		else// M1卡
		{
			if((iRet = JniNdk.JNI_Rfid_M1ExternalAuthen(pnDataLen[0], psDataBuf, (byte)0x61, key, (byte)0x01))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:M1卡外部认证失败（%d）", Tools.getLineInfo(),iRet);
				return iRet;
			}
					
			// 之前认证的块为01，应该01-04块都可进行读写操作
			if((iRet = JniNdk.JNI_Rfid_M1Read((byte)0x01, pnDataLen, out))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:M1卡块读失败(%d)",Tools.getLineInfo(),iRet);
				return iRet;
			}
			LoggerUtil.d("out:"+ISOUtils.hexString(out, 16));
			if(Tools.memcmp(DATA16, out, pnDataLen[0])==false)// 首次
				System.arraycopy(out, 0, DATA16, 0, pnDataLen[0]);
			else if(Tools.memcmp(DATA16, out, pnDataLen[0])==false)
			{
				gui.cls_show_msg1_record(TAG, "rfidProcess", g_keeptime, "line %d:M1卡数据校验失败(%d)",Tools.getLineInfo(),pnDataLen[0]);
				return iRet;
			}
			{
				gui.cls_show_msg("M1卡寻卡+读写操作成功,任意键继续测试");
			}
		}
		return NDK_OK;
	}
	
	/**
	 * 密码键盘操作,非阻塞的密码键盘
	 */
	private void secPinInput(String szPan,int index)
	{
		int iRet;
		final int PINTIMEOUT_MAX = 200*1000;
		StringBuffer strBuffer = new StringBuffer();
		
		if((iRet= touchscreen_getnum(strBuffer))!=NDK_OK)// 密码键盘初始化
		{
			gui.cls_show_msg1_record(TAG, "secPinInput", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		strBuffer.append("尽快输入123456后并确认...");
		gui.cls_printf(strBuffer.toString().getBytes());
		if ((iRet = JniNdk.JNI_Sec_GetPin((byte) index, "8,6,10", szPan, null, (byte) 3, PINTIMEOUT_MAX)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "secPinInput", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		
		byte[] szPinOut = getPinInput(strBuffer.toString(),handler);
		if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("9B01DF2244D84792"), 8) == false) {
			gui.cls_show_msg1_record(TAG, "secPinInput", g_keeptime, "line %d:%s测试失败(szPinOut=%s)",Tools.getLineInfo(),TESTITEM,Dump.getHexDump(szPinOut));
			return;
		}
		gui.cls_show_msg("密码输入完毕,任意键继续");
	}
	
	/**gloable var*/
	private int cardstatus = 0;
	private int nReadCardMode = 0x07;
	/**
	 * 放置射频/刷卡/插入磁卡均应能检测到 SDK3.0
	 */
	private int OpenCard_Event(final int timeOut)
	{
		int tk2Validity = 0;
		int nOutState = 0;
		int stripeflag;
		int iRet = -1;
		int time_use = 0;
		/**测试前置，判断是否支持射频、磁卡、IC*/
		nReadCardMode=0;
		
		Set set=GlobalVariable.gModuleEnable.entrySet();
		 Iterator	iter = set.iterator();
		 
//		 if (GlobalVariable.gCurPlatVer==Platform_Ver.A9) {
//			 for(Object obj :GlobalVariable.gModuleEnable.entrySet()) {
//				  Map.Entry entry = (Entry) obj;  // obj 依次表示Entry
//				  Log.d("eric", "KEY==="+entry.getKey()+"     Value===="+entry.getValue());
//				  if (entry.getKey().toString().equals("RfidEnable")&&entry.getValue().toString().equals("true")) {
//					 	Log.d("eric", "RF----------" );
//						nReadCardMode = nReadCardMode|0x04;
//				}
//
//			 }
//		}else {
			 for(Object obj :GlobalVariable.gModuleEnable.entrySet()) {
				  Map.Entry entry = (Entry) obj;  // obj 依次表示Entry
				  Log.d("eric", "KEY==="+entry.getKey()+"     Value===="+entry.getValue());
				  
				  if (entry.getKey().toString().equals("RfidEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "RF----------" );
						nReadCardMode = nReadCardMode|0x04;
				}
				  if (entry.getKey().toString().equals("IccEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "IC----------" );
						nReadCardMode = nReadCardMode|0x02;
				}
				  if (entry.getKey().toString().equals("MagEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "MAG----------" );
						nReadCardMode = nReadCardMode|0x01;
				}

			 }
//		}
//		if (isAndroid9()) {			
//			if(GlobalVariable.gModuleEnable.get("RfidEnable")==true){
//				nReadCardMode = nReadCardMode|0x04;
//			}
//		}else {
//			if(GlobalVariable.gModuleEnable.get("IccEnable")==true)
//				nReadCardMode=nReadCardMode|0x02;
//			if(GlobalVariable.gModuleEnable.get("MagEnable")==true)
//				nReadCardMode=nReadCardMode|0x01;
//			if(GlobalVariable.gModuleEnable.get("RfidEnable")==true)
//				nReadCardMode = nReadCardMode|0x04;
//		}

			
		
		gui.cls_printf("请刷卡/插卡/放卡...".getBytes());
		
		long startTime = System.currentTimeMillis();
		new Thread()
		{
			public void run() 
			{
				int tempRet;
				// 读卡器初始化
				if ((nReadCardMode & 0x04) == 0x04) 
				{
					tempRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), TIMEOUT_REGISTER, listener);
					if (tempRet == 0) {
						tempRet = JniNdk.JNI_Rfid_PiccDeactivate((byte) 10);
						tempRet = JniNdk.JNI_Rfid_Init(null);
						tempRet = JniNdk.JNI_Rfid_PiccType((byte) 0xcd);
						if (tempRet == NDK_OK) 
						{
//							lock = lock + 0x04;
							cardstatus = cardstatus | 0x04;
							LoggerUtil.d("JNI_Rfid_PiccType:nRet=" + tempRet);
						}
					} else {
						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
						return;
					}
				}
				if ((nReadCardMode & 0x01) == 0x01) {
					tempRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), TIMEOUT_REGISTER, listener);
					if (tempRet == 0) {
//						lock = lock + 0x01;
						cardstatus = cardstatus | 0x01;
						JniNdk.JNI_Mag_Close();
						JniNdk.JNI_Mag_Open();
					} else {
						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
						return;
					}
				}
				if ((nReadCardMode & 0x02) == 0x02) {
					tempRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), TIMEOUT_REGISTER, listener);
					if (tempRet == 0) {
//						lock = lock + 0x02;
						cardstatus = cardstatus | 0x02;
						JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
					} else {
						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
						return;
					}
				}
			};
		}.start();

		// 循环检测是否监听到事件
		synchronized (object) {
			try {
				object.wait(timeOut*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LoggerUtil.v("检测到事件触发===========================");
		time_use = (int) Tools.getStopTime(startTime);
		// 等待事件被监听到
		if(time_use>=timeOut)
		{
			if((nReadCardMode&0x04)==0x04)
				JniNdk.JNI_Rfid_CloseRf();
			if((nReadCardMode&0x01)==0x01)
				JniNdk.JNI_Mag_Close();
			if((nReadCardMode&0x02)==0x02)
				JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			return -1;
		}
//		if((cardstatus&lock)==0x00)
//		{
//			if((nReadCardMode&0x04)==0x04)
//				JniNdk.JNI_Rfid_CloseRf();
//			if((nReadCardMode&0x01)==0x01)
//				JniNdk.JNI_Mag_Close();
//			if((nReadCardMode&0x02)==0x02)
//				JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
//			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
//			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
//			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
//			return -1;
//			
//		}
		

		/**读卡监听操作*/
		while(true)
		{
			if((nReadCardMode&0x02)==0x02&&icstatus==1)/**检测是否有IC卡插入*/
			{
				int[] pnSta = new int[1];
				if(JniNdk.JNI_Icc_Detect(pnSta)==0&&pnSta[0]==0x01)/**有IC卡插入*/
				{
					nOutState = nOutState|0x02;
					gui.cls_show_msg1(wait_time, "已插入IC卡");
					break;
				}
			}
			
			if((nReadCardMode&0x01)==0x01&&magstatus==1)/**检测是否刷卡*/
			{
				byte[] swiped = new byte[1];
				byte[] g_szTrack1 = new byte[128];
				byte[] g_szTrack2 = new byte[128];
				byte[] g_szTrack3 = new byte[128];
				int[] nErrorCode = new int[1];
				if((iRet = JniNdk.JNI_Mag_Swiped(swiped))==NDK_OK&&swiped[0]==1)/**已刷卡*/
				{
					stripeflag = (0x00F0&tk2Validity)>>4;
					iRet = JniNdk.JNI_Mag_ReadNormal(g_szTrack1, g_szTrack2, g_szTrack3, nErrorCode);
					LoggerUtil.d("stripeflag:"+stripeflag+" nErrorCode:"+nErrorCode[0]);
					String szTrack1 = new String(g_szTrack1);
					String szTrack2 = new String(g_szTrack2);
					String szTrack3 = new String(g_szTrack3);
					/**
					 *  0xC01(3073)第一磁道错 
					 *  0xC02(3074) 第二磁道错 
					 *  0xC08(3080)第三磁道错
					 *  0xC09(3081) 1、3磁道错
					 *  0xC0A(3082) 2、3磁道错
					 *  0xC03(3075)1、2磁道错
					 */
					
					if(stripeflag==0&&nErrorCode[0]!=0)
					{
						String headStr="";
						if(nErrorCode[0]==0xC01)
						{
							headStr="1磁道LRC校验错误";
						}
						else if(nErrorCode[0]==0xC02)
						{
							headStr="2磁道LRC校验错误";
						}
						else if(nErrorCode[0]==0xC08)
						{
							headStr="3磁道LRC校验错误";
						}
						else if(nErrorCode[0]==0xC09)
						{
							headStr="1、3磁道LRC校验错误";
						}
						else if(nErrorCode[0]==0xC0A)
						{
							headStr = "2、3磁道LRC校验错误";
						}
						else if(nErrorCode[0]==0xC03)
						{
							headStr = "1、2磁道LRC校验错误";
						}
						gui.cls_show_msg1(wait_time, "%s\n1磁道:%s;2磁道:%s;3磁道:%s;",headStr,szTrack1,szTrack2,szTrack3);
						nOutState = 0x11;
						break;
					}
					if(iRet!=NDK_OK)
					{
						JniNdk.JNI_Mag_Close();
						JniNdk.JNI_Mag_Open();
					}
					else if(0 == tk2Validity)
					{
						if((0x7E==g_szTrack2[0])||(0x7F==g_szTrack2[0]))
						{
							nOutState = 0x11;
							break;
						}
						nOutState = 0x01;
						gui.cls_show_msg1(1, "刷磁卡成功！！！\n1磁道:%s;2磁道:%s;3磁道:%s;",
								new String(g_szTrack1),new String(g_szTrack2),new String(g_szTrack3));
						break;
						
					}
					else if(1==tk2Validity)
					{
						if((0x7E==g_szTrack2[0])||(0x7F==g_szTrack2[0]))
						{
							nOutState = 0x11;
							break;
						}
						nOutState = 0x01;
						// 磁卡数据处理
						gui.cls_show_msg1(1, "刷磁卡成功！！！\n1磁道:%s;2磁道:%s;3磁道:%s;",
								new String(g_szTrack1),new String(g_szTrack2),new String(g_szTrack3));
						break;
					}
					else if(stripeflag!=0)
					{
						// 磁卡数据处理 需要校验磁道信息
						break;
					}
				}
				else if(iRet!=NDK_OK)
				{
					gui.cls_show_msg1(wait_time, "检测刷卡状态失败");
				}
			}
			/**非接卡监听操作*/
			if((nReadCardMode&0x04)==0x04&&rfstatus==1)
			{
				byte[] cardType = new byte[1];
				int[] bufLen = new int[1];
				byte[] buf = new byte[1024];
				int i = 0;
				for (i = 0; i < 1; i++) {
					// 这里判断卡片类型
					iRet = GetRfidCardInfo(cardType, bufLen, buf);
					if(iRet!=-1)
					{
						nOutState = nOutState|0x04;
						nOutState = nOutState|cardType[0];
						break;
					}
					else
						break;
//					time_use = (int) Tools.getStopTime(startTime);
//					if(time_use>timeOut)
//					{
//						if((nReadCardMode&0x04)==0x04)
//							JniNdk.JNI_Rfid_CloseRf();
//						if((nReadCardMode&0x01)==0x01)
//							JniNdk.JNI_Mag_Close();
//						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
//						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
//						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
//						return 0;
//					}
					
				}
				break;
//				if((nOutState&0x04)==0x04)// 跳出while循环
//					break;
			}
		}

		
		/**测试后置操作*/
		if((nReadCardMode&0x04)==0x04)
		{
			JniNdk.JNI_Rfid_CloseRf();
			iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			LoggerUtil.d("JNI_SYSUnRegisterEvent SYS_EVENT_RFID,ret = "+iRet);
		}
		if((nReadCardMode&0x01)==0x01)
		{
			JniNdk.JNI_Mag_Close();
			iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
			LoggerUtil.d("JNI_SYSUnRegisterEvent SYS_EVENT_MAGCARD,ret = "+iRet);
		}
		if((nReadCardMode&0x02)==0x02)
		{
			JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
			iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			LoggerUtil.d("JNI_SYSUnRegisterEvent SYS_EVENT_ICCARD,ret ="+iRet);
		}
		return 0;
	}
	
	/**
	 * 通联应用(事件机制)
	 * @return
	 */
	private int tongLProcess()
	{
		int nOutState = 0;
		int cardstatus = 0;
		int nReadCardMode = 0x07;
		int iRet = 0;
		int time_use = 0;
		
		/*磁卡*/
		byte[] pszTk1 = new byte[MAXTRACKLEN];
		byte[] pszTk2 = new byte[MAXTRACKLEN];
		byte[] pszTk3 = new byte[MAXTRACKLEN];
		String szPan = null;
		
		/*射频卡*/
		int[] errorCode = new int[1];
		
		// icc
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		int[] pnRecvLen = new int[1];
		byte[] psRecvBuf = new byte[8];
		
		// 测试前置
		JniNdk.JNI_Rfid_PiccDeactivate((byte)10);
		JniNdk.JNI_Rfid_CloseRf();
		gui.cls_printf("请刷卡/插卡/放卡...".getBytes());
		// 读卡器初始化
		long startTime = System.currentTimeMillis();
		if ((nReadCardMode & 0x04) == 0x04) 
		{
			iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), TIMEOUT_REGISTER, listener);
			if (iRet == 0) {
				iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte) 10);
				iRet = JniNdk.JNI_Rfid_Init(null);
				iRet = JniNdk.JNI_Rfid_PiccType((byte) 0xcd);
				if (iRet == NDK_OK) 
				{
					cardstatus = cardstatus | 0x04;
					LoggerUtil.d("JNI_Rfid_PiccType:nRet=" + iRet);
				}
			} else {
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				return 0;
			}
		}
		if ((nReadCardMode & 0x01) == 0x01) {
			iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), TIMEOUT_REGISTER, listener);
			if (iRet == 0) {
				cardstatus = cardstatus | 0x01;
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_Mag_Open();
			} else {
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
				return 0;
			}
		}
		if ((nReadCardMode & 0x02) == 0x02) {
			iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), TIMEOUT_REGISTER, listener);
			if (iRet == 0) {
				cardstatus = cardstatus | 0x02;
				JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
			} else {
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				return 0;
			}
		}
		// 循环检测是否监听到事件
		synchronized (object) {
			try {
				object.wait(TIMEOUT_REGISTER);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		time_use = (int) Tools.getStopTime(startTime);
		// 等待事件被监听到
		if(time_use>TIMEOUT_REGISTER)
		{
			if((nReadCardMode&0x04)==0x04)
				JniNdk.JNI_Rfid_CloseRf();
			if((nReadCardMode&0x01)==0x01)
				JniNdk.JNI_Mag_Close();
			if((nReadCardMode&0x02)==0x02)
				JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			return -1;
		}
		while(true)
		{
			/**读卡监听操作*/
			if((nReadCardMode&0x02)==0x02&&icstatus==1)/**检测是否有IC卡插入*/
			{
				
				JniNdk.JNI_Rfid_CloseRf();
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());// 注销磁卡、非接、IC
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				JniNdk.JNI_Icc_PowerDown(0);
				int[] pnSta = new int[1];
				if(JniNdk.JNI_Icc_Detect(pnSta)==0&&pnSta[0]==0x01)/**有IC卡插入*/
				{
					nOutState = nOutState|0x02;
				}

				// IC卡
				if ((iRet = JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),psAtrBuf, pnAtrLen)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "tongLProcess", g_keeptime,"line %d:IC上电失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				// 取随机数
				if ((iRet = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "tongLProcess", g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				// 下电
				if ((iRet = JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal())) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "tongLProcess", g_keeptime,"line %d:IC卡下电失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				gui.cls_show_msg("IC卡读写成功,请拔出IC卡,完成任意键继续");
				break;
			}
			
			if((nReadCardMode&0x01)==0x01&&magstatus==1)/**检测是否刷卡*/
			{
				nOutState = nOutState|0x01;
				if((iRet = JniNdk.JNI_Mag_ReadNormal(pszTk1, pszTk2, pszTk3, errorCode))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "tongLProcess", g_keeptime,"line %d:读磁道数据失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				// 显示磁卡数据-二磁道截取
				String str_TK2 = new String(pszTk2);
				int index = str_TK2.indexOf('=');

				if (index == -1)
				{
					gui.cls_show_msg1(2, "line %d:刷卡失败!", Tools.getLineInfo());
					break;
				}
				else 
				{
					szPan = str_TK2.substring(0, index);
					gui.cls_show_msg("卡号:%s,按任意键继续", szPan);
					break;
				}
			}
			/**非接卡监听操作*/
			if((nReadCardMode&0x04)==0x04&&rfstatus==1)
			{
				nOutState = nOutState|0x04;
				JniNdk.JNI_Rfid_CloseRf();
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());// 注销磁卡、非接、IC
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				iRet=rfidRead((byte)0xcc);
				break;
			}
		}
		/**测试后置操作*/
		if((nReadCardMode&0x04)==0x04)//非接
		{
			JniNdk.JNI_Rfid_CloseRf();
			iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			LoggerUtil.d("JNI_SYSUnRegisterEvent SYS_EVENT_RFID,ret = "+iRet);
		}
		if((nReadCardMode&0x01)==0x01)
		{
			JniNdk.JNI_Rfid_CloseRf();
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			LoggerUtil.d("JNI_SYSUnRegisterEvent SYS_EVENT_MAGCARD,ret = "+iRet);
		}
		if((nReadCardMode&0x02)==0x02)
		{
			JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
			iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			LoggerUtil.d("JNI_SYSUnRegisterEvent SYS_EVENT_ICCARD,ret ="+iRet);
		}


		// 密码键盘操作
		return NDK_OK;
	}
	
	/**
	 * 银联云pos应用(非事件机制)，多个线程操作
	 * @return
	 * @throws InterruptedException 
	 */
	private void yinLianYunPOSProcess() throws InterruptedException{
		/*private & local definition*/
		byte[] psStatus = new byte[1];
		CallBack value = new CallBack();
		value.setValue(0);
		
		/*process body*/
		gui.cls_show_msg("模拟银联云pos交易流程,请分别测试刷卡/插卡/挥卡流程,任意键继续");
		// 测试前置，安装TPK密钥密文形式，主密钥索引1,32个1
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		secKcvInfo.nCheckMode=0;
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA253C9D9D7C2FBBFA"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)3, 16, ISOUtils.hex2byte("950973182317F80BF679786E2411E3DE"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		
		//前置
		JniNdk.JNI_Mag_Close();
		JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
		JniNdk.JNI_Rfid_Init(psStatus);
	
		MagThread magThread = new MagThread(value);
        RfidThread rfidThread = new RfidThread(value);
        IccThread iccThread = new IccThread(value);
        magThread.start();
        rfidThread.start();
        iccThread.start();
		gui.cls_printf("请刷卡/插卡/挥卡...".getBytes());
		
		while(true)
		{
			// 非接
			if ((value.getValue()&0x04)==0x04) {
				magThread.cancel();
				iccThread.cancel();
				rfidThread.join(15*1000);
				
				//再起个线程读卡
				Thread apduThread = new Thread() {
					int ret = -1;
					byte[] psPiccType = new byte[1];
					int[] pnDataLen = new int[1];
					byte[] psDataBuf = new byte[10];
					byte[] select_1pay = {0x00,(byte) 0x84,0x00,0x00,0x08};
					
					@Override 
					public void run() {
						JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
						
						//获取系统时间和随机数
						TimeNewland timeNewland = new TimeNewland();
						byte[] arg1 = new byte[4];
						JniNdk.JNI_Sys_GetPosTime(timeNewland);
						LoggerUtil.d("time:"+timeNewland.formatTime());
						JniNdk.JNI_Sec_GetRandom(4, arg1);
						
						JniNdk.JNI_Rfid_PiccType((byte) 0xcd);
						if((ret = JniNdk.JNI_Rfid_PiccDetect(psPiccType))!=NDK_OK){
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime,"line %d:寻卡失败(%d)", Tools.getLineInfo(), ret);
							return;
						} 
						if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen, psDataBuf))!=NDK_OK){
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime,"line %d:激活失败(%d)", Tools.getLineInfo(), ret);
							return;
						} 
						if((ret = JniNdk.JNI_Rfid_PiccApdu(select_1pay.length, select_1pay, pnDataLen, psDataBuf))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime, "line %d:CPU卡APDU失败（%d）", Tools.getLineInfo(),ret);
							return;
						}
						if(pnDataLen[0]==10)
						{
							// CPU卡操作
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", wait_time,"CPU卡上电成功+取随机数操作成功");
						}
						else
						{
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", wait_time,"CPU卡上电成功，不支持取随机数");
						}
						//文件读写操作
						fsProcess_cloudPos();
						
						JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x0a);
						JniNdk.JNI_Rfid_CloseRf();
					}
				};
				apduThread.start();
				apduThread.join(15*1000);
				break;
			}
			
			// 磁卡
			if ((value.getValue()&0x01)==0x01) {
				iccThread.cancel();
				rfidThread.cancel();
				magThread.join(15*1000);
				break;
			}
			// IC卡
			if ((value.getValue()&0x02)==0x02) {
				magThread.cancel();
				rfidThread.cancel();
				//再起个线程读卡
				Thread iccrwThread = new Thread() {
					int ret = -1;
					byte[] psAtrBuf = new byte[20];
					int[] pnAtrLen = new int[1];
					int[] pnRecvLen = new int[1];
					byte[] psRecvBuf = new byte[8];
					
					@Override 
					public void run() {
						JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),psAtrBuf, pnAtrLen);
						JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
						JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x00);
						JniNdk.JNI_Rfid_CloseRf();
						
						//获取系统时间和随机数
						TimeNewland timeNewland = new TimeNewland();
						byte[] arg1 = new byte[4];
						JniNdk.JNI_Sys_GetPosTime(timeNewland);
						LoggerUtil.d("time:"+timeNewland.formatTime());
						JniNdk.JNI_Sec_GetRandom(4, arg1);
						
						if ((ret = JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),psAtrBuf, pnAtrLen)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime,"line %d:IC上电失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						// 取随机数
						if ((ret = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						//文件读写操作
						fsProcess_cloudPos();
						// 取随机数
						if ((ret = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						//密码输入操作
						if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable))//X5不支持密码键盘
						     secPinInput("6225885916163157",3);
						// 取随机数
						if ((ret = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, "yinLianYunPOSProcess", g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
					}
				};
				iccrwThread.start();
				iccrwThread.join(15*1000);
				break;
			}
		}
		
		if ((value.getValue()&0x04)==0x04 || (value.getValue()&0x01)==0x01) {
			//密码输入操作
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable))//X5不支持密码键盘
				secPinInput("6225885916163157",3);
		}
		//打印操作
		
		//后置
		JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
		JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x00);
		JniNdk.JNI_Rfid_CloseRf();
		
		gui.cls_show_msg("银联云pos交易模拟测试结束");
	}
	
	//快钱应用交易模拟SDK3.0事件机制
	private void kuaiQianProcess(){
		/*private & local definition*/
		int nOutState = 0;
		int iRet = 0,ret= -1;
		int time_use = 0;
		boolean flag = false;//卡操作是否完成
		byte[] psDataOut = new byte[16];
		byte[] szDataIn = new byte[31];
		byte[] szMac = new byte[16];
		/*磁卡*/
		byte[] swiped = new byte[1];
		byte[] pszTk1 = new byte[MAXTRACKLEN];
		byte[] pszTk2 = new byte[MAXTRACKLEN];
		byte[] pszTk3 = new byte[MAXTRACKLEN];
		String szPan = null;
		
		/*射频卡*/
		byte[] psStatus = new byte[1];
		int[] errorCode = new int[1];
		
		// icc
		int[] pnSta = new int[1];
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		int[] pnRecvLen = new int[1];
		byte[] psRecvBuf = new byte[8];
		/**测试前置，判断是否支持射频、磁卡、IC*/
		nReadCardMode=0;
		Set set=GlobalVariable.gModuleEnable.entrySet();
		 Iterator	iter = set.iterator();
		 
//		 if (GlobalVariable.gCurPlatVer==Platform_Ver.A9) {
//			 for(Object obj :GlobalVariable.gModuleEnable.entrySet()) {
//				  Map.Entry entry = (Entry) obj;  // obj 依次表示Entry
//				  Log.d("eric", "KEY==="+entry.getKey()+"     Value===="+entry.getValue());
//				  if (entry.getKey().toString().equals("RfidEnable")&&entry.getValue().toString().equals("true")) {
//					 	Log.d("eric", "RF----------" );
//						nReadCardMode = nReadCardMode|0x04;
//				}
//
//			 }
//		}else {
			 for(Object obj :GlobalVariable.gModuleEnable.entrySet()) {
				  Map.Entry entry = (Entry) obj;  // obj 依次表示Entry
				  Log.d("eric", "KEY==="+entry.getKey()+"     Value===="+entry.getValue());
				  
				  if (entry.getKey().toString().equals("RfidEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "RF----------" );
						nReadCardMode = nReadCardMode|0x04;
				}
				  if (entry.getKey().toString().equals("IccEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "IC----------" );
						nReadCardMode = nReadCardMode|0x02;
				}
				  if (entry.getKey().toString().equals("MagEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "MAG----------" );
						nReadCardMode = nReadCardMode|0x01;
				}
				  if (entry.getKey().toString().equals("PrintEnableReg")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "PrintEnableReg----" );
						nReadCardMode = nReadCardMode|0x08;
				}
				  if (entry.getKey().toString().equals("PinEnable")&&entry.getValue().toString().equals("true")) {
					 	Log.d("eric", "PinEnable----------" );
						nReadCardMode = nReadCardMode|0xF0;
				}

			 }
//		}
//		if(GlobalVariable.gModuleEnable.get("IccEnable")==true)
//			nReadCardMode=nReadCardMode|0x02;
//		if(GlobalVariable.gModuleEnable.get("MagEnable")==true)
//			nReadCardMode=nReadCardMode|0x01;
//		if(GlobalVariable.gModuleEnable.get("RfidEnable")==true)
//			nReadCardMode = nReadCardMode|0x04;
//		if(GlobalVariable.gModuleEnable.get("PrintEnableReg")==true)/**X5支持打印，但是不支持打印的事件机制，SDK3.0需要判断是否支持打印模块*/
//			nReadCardMode=nReadCardMode|0x08;
//		if(GlobalVariable.gModuleEnable.get("PinEnable"))
//			nReadCardMode=nReadCardMode|0xF0;
		
		/*process body*/
		gui.cls_show_msg("快钱应用交易模拟测试,请将SVN服务器上的picture文件夹导入内置SD卡,任意键继续");
		// 测试前置，安装TPK密钥密文形式，主密钥索引1,32个1
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		secKcvInfo.nCheckMode=0;
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA253C9D9D7C2FBBFA"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)3, 16, ISOUtils.hex2byte("950973182317F80BF679786E2411E3DE"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		// 应用前置，依次安装主密钥、PIN密钥、MAC密钥，应用每次重启安装的具体密钥内容都不同？
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("719ecadc663a8e09ff418ee9dfc66178"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		secKcvInfo.nCheckMode=1;
		secKcvInfo.nLen = 4;
		secKcvInfo.sCheckBuf = ISOUtils.hex2byte("0bc7967f02289fd1");
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)2, 16, ISOUtils.hex2byte("8dcf4ec8f608371c9a897b5891b41c24"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		secKcvInfo.sCheckBuf = ISOUtils.hex2byte("69dd6c1c4bdefd1c");
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)1, 16, ISOUtils.hex2byte("52b43452e02b329251f3754ac710d562"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		secKcvInfo.sCheckBuf = ISOUtils.hex2byte("8d8a2e00bdd0282b");
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)3, 16, ISOUtils.hex2byte("207e43dfd87b7d6766ee7410c048ac29"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		
		// 测试前置
		JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_BLUE_ON.led());
		StringBuffer strBufferTip = new StringBuffer();
		if((nReadCardMode&0x01)==0x01)
		{
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_Mag_Open();
			JniNdk.JNI_Mag_Swiped(swiped);
			strBufferTip.append("刷卡/");
		}
		if((nReadCardMode&0x04)==0x04)
		{
			JniNdk.JNI_Rfid_Init(psStatus);
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x0a);
			strBufferTip.append("挥卡");
		}
		if((nReadCardMode&0x02)==0x02)
		{
			JniNdk.JNI_Icc_PowerDown(0);
			JniNdk.JNI_Icc_Detect(pnSta);
			strBufferTip.append("插卡/");
		}

		gui.cls_printf(("请"+strBufferTip+"...").getBytes());
		// 读卡器初始化
		long startTime = System.currentTimeMillis();
		new Thread(){
			public void run() 
			{
				int tempRet;
				if ((nReadCardMode & 0x04) == 0x04) 
				{
					if ((tempRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), TIMEOUT_REGISTER, listener)) != 0) {
						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
						gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:非接事件注册失败(%d)", Tools.getLineInfo(),tempRet);
						return;
					} 
				}
				if ((nReadCardMode & 0x01) == 0x01) {
					if ((tempRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), TIMEOUT_REGISTER, listener)) != 0) {
						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
						gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:磁卡事件注册失败(%d)", Tools.getLineInfo(),tempRet);
						return;
					} 
				}
				if ((nReadCardMode & 0x02) == 0x02) {
					if ((tempRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), TIMEOUT_REGISTER, listener)) != 0) {
						JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
						gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:IC事件注册失败(%d)", Tools.getLineInfo(),tempRet);
						return;
					} 
				}
			};
		}.start();

		time_use = (int) Tools.getStopTime(startTime);
		// 循环检测是否监听到事件
		synchronized (object) {
			try {
				object.wait(TIMEOUT_REGISTER);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LoggerUtil.v("检测到事件触发============");
		// 未监听到事件
		if(time_use>=(TIMEOUT_REGISTER/1000))
		{
			JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led()|EM_LED.LED_RFID_YELLOW_OFF.led()|EM_LED.LED_RFID_GREEN_OFF.led()|EM_LED.LED_RFID_BLUE_OFF.led());
			if((nReadCardMode&0x01)==0x01)
			{
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
			}
			if((nReadCardMode&0x04)==0x04)
			{
				JniNdk.JNI_Rfid_CloseRf();
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			}
			if((nReadCardMode&0x02)==0x02)
			{
				JniNdk.JNI_Icc_PowerDown(0);
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			}
			gui.cls_show_msg("寻卡超时!任意键退出");
			return;
		}
		while(!flag)
		{
			if((nReadCardMode&0x01)==0x01&&magstatus==1)//检测到刷卡
			{
				nOutState = nOutState|0x01;
				JniNdk.JNI_Mag_Swiped(swiped);
				if((iRet = JniNdk.JNI_Mag_ReadNormal(pszTk1, pszTk2, pszTk3, errorCode))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:读磁道数据失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				// 显示磁卡数据-二磁道截取
				String str_TK2 = new String(pszTk2);
				int index = str_TK2.indexOf('=');
				if (index == -1)
				{
					gui.cls_show_msg1(2, "line %d:刷卡失败!", Tools.getLineInfo());
				}
				else 
				{
					szPan = str_TK2.substring(0, index);
					gui.cls_show_msg("卡号:%s,按任意键继续", szPan);
					flag = true;
				}
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_Rfid_CloseRf();
				
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());// 注销磁卡、非接、IC
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				JniNdk.JNI_Mag_Close();
				break;
			}
			
			if((nReadCardMode&0x02)==0x02&&icstatus==1)//检测到IC卡插入
			{
				JniNdk.JNI_Rfid_CloseRf();
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());// 注销磁卡、非接、IC
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				if(JniNdk.JNI_Icc_Detect(pnSta)==0&&pnSta[0]==0x01)/**有IC卡插入*/
				{
					nOutState = nOutState|0x02;
				}
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led()|EM_LED.LED_RFID_YELLOW_OFF.led()|EM_LED.LED_RFID_GREEN_OFF.led()|EM_LED.LED_RFID_BLUE_OFF.led());
				//获取系统时间和随机数
				TimeNewland timeNewland = new TimeNewland();
				byte[] arg1 = new byte[4];
				JniNdk.JNI_Sys_GetPosTime(timeNewland);
				LoggerUtil.d("time:"+timeNewland.formatTime());
				JniNdk.JNI_Sec_GetRandom(4, arg1);
				
				// IC卡
				if ((iRet = JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),psAtrBuf, pnAtrLen)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:IC上电失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				// 取随机数
				if ((iRet = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				// 此处不下电，等待密码键盘后再次读写才下电
				flag = true;
				gui.cls_show_msg1(2,"IC卡读写成功,按任意键继续");
				break;
			}
			
			if((nReadCardMode&0x04)==0x04&&rfstatus==1)//检测到挥卡
			{
				nOutState = nOutState|0x04;
				JniNdk.JNI_Rfid_CloseRf();
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());// 注销磁卡、非接、IC
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_YELLOW_ON.led());
				if ((iRet=rfidRead((byte)0xcc)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:射频卡应用流程测试失败(%d)", Tools.getLineInfo(), iRet);
					break;
				}
				JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_GREEN_FLICK.led());
				flag = true;
				break;
			}
			break;
		}
		if(flag){
			//解决BUG2020042202766，王凯 20200618；获取key_misuse的值
			String key_misuse = "0";// 默认值 是0，A5平台无此文件
	        String info = "";
	        info = LinuxCmd.readDevNode("/system/SecureModule/sec.conf");
            if (!info.equals("")) {
                int index = info.indexOf("key_misuse = ");
                int length = "key_misuse = ".length();
                key_misuse = info.substring(index+length,index+length+1);
                LoggerUtil.v("sec.conf->key_misuse = "+key_misuse);
            }
	        
			//SecCalcDes
            JniNdk.JNI_Sec_CalcDes((byte)3, (byte)3, ISOUtils.hex2byte("376227001823500158800d3902520947"), 16, psDataOut, (byte)0);
            //根据key_misuse的值判断返回值，key_misuse=0，返回值为0；key_misuse=1，返回值为-1703
            if(key_misuse.equals("0"))
            {
				if(ret != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
					return;
				}
            }
            else if(key_misuse.equals("1"))
            {
            	if(ret != -1703)
				{
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
					return;
				}
            }
            else
            {
            	gui.cls_show_msg("line %d:key_misuse="+key_misuse+",请确认案例是否正确",Tools.getLineInfo());
            }
			if((nOutState&0x04)!=0x04)//不是检测到挥卡
				JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led()|EM_LED.LED_RFID_YELLOW_OFF.led()|EM_LED.LED_RFID_GREEN_OFF.led()|EM_LED.LED_RFID_BLUE_OFF.led());
			//密码键盘
			if((nReadCardMode&0xF0)==0xF0)
			{
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable))
				{
					if ((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(), TIMEOUT_REGISTER, listener)) == 0) {
						secPinInput("6225885916163157",3);
					} else{
						gui.cls_show_msg1_record(TAG, "kuaioiQianProcess", g_keeptime,"line %d:PIN事件注册失败(%d)", Tools.getLineInfo(),iRet);
					}
					JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue());
				}
				
			}
			//密码输入后ic卡仍有读写操作
			if((nOutState&0x02)==0x02){
				// 取随机数
				if ((iRet = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), iRet);
				}
				// 下电
				if ((iRet = JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal())) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:IC卡下电失败(%d)", Tools.getLineInfo(), iRet);
				}
			}
			//文件
			JniNdk.JNI_FsExist("/appfs/ksn.in");
			//sec
			/*//根据log解包出的参数是com.newland.paylment_NL_TERM_MGR，但如此设置会影响后续，不执行这一步
			if((ret = JniNdk.JNI_Sec_SetKeyOwner("com.newland.paylment_NL_TERM_MGR"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
				return;
			}*/
			Arrays.fill(szDataIn, (byte) 0x20);//实际流程里每次数据不同
			if((ret = JniNdk.JNI_Sec_GetMac((byte)1, szDataIn, 8, szMac, (byte)1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			}

			if((nOutState&0x04)==0x04)//检测到挥卡
				JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_GREEN_ON.led());
			//打印
			if((nReadCardMode&0x08)==0x08)
			{
				if ((iRet = prnProcess_kuaiqian()) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "kuaiQianProcess", g_keeptime,"line %d:打印流程失败(%d)", Tools.getLineInfo(), iRet);
				}
			}
		}
		
		//后置操作
		JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led()|EM_LED.LED_RFID_YELLOW_OFF.led()|EM_LED.LED_RFID_GREEN_OFF.led()|EM_LED.LED_RFID_BLUE_OFF.led());
		if((nReadCardMode&0x02)==0x02)
		{
			JniNdk.JNI_Icc_PowerDown(0);
			JniNdk.JNI_Icc_Detect(pnSta);
			JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), TIMEOUT_REGISTER, listener);
			while(true){
				if(pnSta[0] == 0x01){
					gui.cls_printf("IC卡未取出,请取出IC卡...".getBytes());
				} else{
					break;
				}
				JniNdk.JNI_Icc_PowerDown(0);
				JniNdk.JNI_Icc_Detect(pnSta);
			} 
		}

		if((nReadCardMode&0x04)==0x04)
			JniNdk.JNI_Rfid_CloseRf();
		if((nReadCardMode&0x01)==0x01)
			JniNdk.JNI_Mag_Close();
		JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
		gui.cls_show_msg("快钱应用交易模拟测试结束");
		return;
	}
	
	
/*	*//**
	 * 文件和安全操作
	 * @return
	 *//*
	private int fileSecOperate()
	{
//		byte[] wBuf = {4e 4c 41 49 44 5f 56 33 03 fd 00 9f 06 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 9f 1b 04 00 00 00 00 df 01 01 00 df 27 01 00 9f 7b 06 00 00 00 05 00 00 df 20 06 00 00 00 05 00 00 df 19 06 00 00 00 05 00 00 df 21 06 00 00 00 05 00 00 df 2a 01 00 df 25 01 02 df 29 01 00 df 22 04 00 00 00 00 df 23 01 00 df 24 07 f4 c0 f0 f8 af 8e 60 9f 7a 01 01 9f 35 01 22 9f 33 03 e0 f1 c8 9f 40 05 e0 00 f0 a0 01 9f 1a 02 01 56 9f 1e 08 30 30 30 30 30 33 30 34 9f 66 04 76 00 00 80 9f 39 01 80 df 26 01 19 df 11 05 00 00 00 00 00 df 13 05 00 00 00 00 00 df 12 05 00 00 00 00 00 df 17 01 00 df 16 01 00 df 15 04 00 00 00 00 9f 09 02 00 00 9f 01 06 12 34 56 78 90 00 9f 15 02 12 34 9f 16 0f 31 32 33 34 35 36 37 38 39 30 31 32 33 34}
		int ret;
		TimeNewland timeNewland = new TimeNewland();
		JniNdk.JNI_FsExist("/appfs/kernel1.a");
		JniNdk.JNI_FsDel("/appfs/kernel1.a");
		
		if((ret = JniNdk.JNI_FsOpen("/appfs/kernel1.a", "w"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "fileSecOperate", g_keeptime,"line %d:打开文件测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
//		if((ret = JniNdk.JNI_FsWrite(arg0, arg1, arg2)))
		if((ret = JniNdk.JNI_FsClose(0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "fileSecOperate", g_keeptime,"line %d:关闭文件测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		JniNdk.JNI_Sys_GetPosTime(timeNewland);
		LoggerUtil.d("time:"+timeNewland.formatTime());
		JniNdk.JNI_Sec_GetRandom(4, arg1)
		JniNdk.JNI_Sys_GetPosInfo(arg0, arg1, arg2)
	}*/
	
	/**
	 * 获取打印机状态
	 * @return
	 */
	private int getPrnStatus()
	{
		int ret;
		if((ret = JniNdk.JNI_Print_Init(0))!=NDK_OK)
			return ret;
		if((ret = JniNdk.JNI_Print_GetStatus())!=NDK_OK)
			return ret;
		return 0;
	}
	
    NotifyEventListener listener=new NotifyEventListener() 
    {
    	
		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			if(eventNum==EM_SYS_EVENT.SYS_EVENT_RFID.getValue())
			{
				
				rfstatus = 1;
				LoggerUtil.e("--------rfstatus=1");
				synchronized(object)
				{
					object.notify();
				}
			}
			if(eventNum==EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())
			{
				magstatus = 1;
				LoggerUtil.d("--------magstatus=1");
				synchronized(object)
				{
					object.notify();
				}
			}
			if(eventNum == EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())
			{
				icstatus = 1;
				LoggerUtil.d("-------icstatus=1");
				synchronized(object)
				{
					object.notify();
				}
			}
			return 0;
		}
	};
	
	private int GetRfidCardInfo(byte[] cardType,int[] nLen,byte[] buf)
	{
		int ret = 0;
		byte[] picctype = new byte[1];
		byte[] atq = new byte[2];
		byte[] uidBuf = new byte[10];
		byte[] aum_read = new byte[2048];
		ret = JniNdk.JNI_Rfid_PiccType((byte) 0xcd);
		if(ret!=NDK_OK)
		{
			return -1;
		}
		LoggerUtil.e("rfstatus:"+rfstatus);;
//		ret = JniNdk.JNI_Rfid_M1Request((byte) 0x52, nLen, atq);
//		LoggerUtil.e("JNI_Rfid_M1Request:"+ret);
		if((ret=JniNdk.JNI_Rfid_PiccActivate(picctype, nLen, aum_read))!=NDK_OK)// 是M1卡
		{
			ret = JniNdk.JNI_Rfid_PiccType((byte) 0xcc);
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 10);
			ret = JniNdk.JNI_Rfid_M1Request((byte) 0x01, nLen, atq);
			if(ret!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TAG, wait_time, "line %d:M1卡request失败(%d)", Tools.getLineInfo(),ret);
				return -1;
			}
			ret = JniNdk.JNI_Rfid_M1Anti(nLen, uidBuf);
			if(ret!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TAG, wait_time, "line %d:M1卡Anti失败(%d)", Tools.getLineInfo(),ret);
				return -1;
			}
				
			ret = JniNdk.JNI_Rfid_M1Select(nLen[0], uidBuf, aum_read);
			if(ret!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TAG, wait_time, "line %d:M1卡Select失败(%d)", Tools.getLineInfo(),ret);
				return -1;
			}
			if(aum_read[0]!=0x20)
				cardType[0] = 0x44;
			else
				cardType[0] = 0x14;
			System.arraycopy(uidBuf, 0, buf, 0, nLen[0]);
			buf[nLen[0]] = aum_read[0];//?? 这里的意图
			gui.cls_show_msg1(wait_time, "检测到M1卡");
			return 0;
			
		}
		else 
		{
			if(picctype[0]==-52)
			{
				cardType[0] = 0x14;
				gui.cls_show_msg1(wait_time, "检测到CPU_A卡");
			}
			else if(picctype[0]==-53)
			{
				cardType[0] = 0x24;
				gui.cls_show_msg1(wait_time, "检测到CPU_B卡");
			}
			System.arraycopy(aum_read, 0, buf, 0, nLen[0]);
			buf[nLen[0]] = 0x00;
			return 0;
//			if(ret==-5)// 当回调走完NDK_RfidPiccDeactivate遇上NDK_M1Request会返回-5 触发保护机制
//				return -1;
		}

	}
	
	public int fsProcess_cloudPos(){
		int fd = -1, ret = -1;
		byte[] rbuf = new byte[4];
		byte[] wbuf = new byte[]{0x01,0x00,0x00,0x00};
		if((fd = JniNdk.JNI_FsOpen("/appfs/tsc","w"))<0)
		{
			gui.cls_show_msg1_record(TAG, "fsProcess_cloudPos", g_keeptime,"line %d:打开文件测试失败(%d)", Tools.getLineInfo(), fd);
			return fd;
		}
		if((ret = JniNdk.JNI_FsSeek(fd,0,0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "fsProcess_cloudPos", g_keeptime,"line %d:移动文件指针测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		/*if((ret = JniNdk.JNI_FsRead(fd,rbuf,4))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "fileSecOperate", g_keeptime,"line %d:读取文件测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		if((ret = JniNdk.JNI_FsWrite(fd,wbuf,4))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "fileSecOperate", g_keeptime,"line %d:写入文件测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}*/
		if((ret = JniNdk.JNI_FsClose(fd))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "fsProcess_cloudPos", g_keeptime,"line %d:关闭文件测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		return NDK_OK;
	}
	
	public int prnProcess_kuaiqian(){
		int ret = -1;
		int prnStatus;
		PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
		String path = GlobalVariable.sdPath + "picture/bill99_logo.png";
		String strData="!NLFONT 6 10 3\n"+
					//"!image!/storage/emulated/0/payment//bill99_logo.png!NLFONT 9 12 2\n"+
					"*image c 182*115 path:/mnt/sdcard/picture/bill99_logo.png\n"+
					"!yspace 2\n"+
					"!NLFONT 9 12 3\n"+
					"!yspace 2\n"+
					"!NLFONT 6 6\n"+
					"*text l ------------------------------------------------\n"+
					"!NLFONT null null 3\n"+
					"*text l 商户存根/MERCHANT COPY\n"+
					"!NLFONT 6 6\n"+
					"*text l ------------------------------------------------\n"+
					"!NLFONT 9 12 4\n"+
					"*text l 商户名称:ZOE集成环境测试商户\n"+
					"*text l 商户编号:1001281228\n"+
					"*text l 终端编号:20140502\n"+
					"!NLFONT 6 6\n"+
					"*text l ------------------------------------------------\n"+
					"!NLFONT 9 12 3\n"+
					"*text l 卡   别:工商银行  \n"+
					"!NLFONT 9 3 3\n"+
					"*text l 卡 号:621226******1348 /I\n"+
					"!NLFONT 9 12 3\n"+
					"*text l 交易类型:消费/SALE\n"+
					"!NLFONT 9 3 3\n"+
					"*text l 凭证号:000003\n"+
					"*text l 参考号:110004876591\n"+
					"*text l 收单机构:48120000\n"+
					"*text l 授权码:119785\n"+
					"!NLFONT 9 12 3\n"+
					"*text l 批次号:000001\n"+
					"*text l 票据号:000001\n"+
					"*text l 日期时间:2018/05/15 16:31:54\n"+
					"*text l 卡 组 织:内卡 借记卡\n"+
					"!NLFONT 9 3 3\n"+
					"*text l 金 额:RMB 0.01\n"+
					"!NLFONT 9 12 3\n"+
					"*text l 操作员号:001\n"+
					"*text l 程序版本:1.1.23\n"+
					"*text l 备注:\n"+
					"!NLFONT 6 19 2\n"+
					"!NLFONT 9 12 3\n"+
					"*text l 快钱商户号:812002145110302\n"+
					"!NLFONT 6 6 3\n"+
					"*text l ------------------------------------------------\n"+
					"!NLFONT 6 1 3\n"+
					"*text l 持卡人签名:\n"+
					"*text l  根据银联最新要求，此笔交易无须签名\n"+
					"*text l  \n"+
					"*text l  \n"+
					"!NLFONT 9 12 3\n"+
					"!NLFONT 8 6 3\n"+
					"*text l 本人确认以上交易，同意将其记入本卡账户/I ACKNOWKEDGE SATISFATORY RECEIPT OF RELATIVE GOODS/SERVICES\n"+
					"*text l \n"+
					"!NLFONT 9 12 3\n"+
					"!NLFONT 8 6 3\n"+
					"*text l - - - - - - - X - - - - - - - X - - - - - - - \n"+
					"!NLPRNOVER";
		
		FileSystem fileSystem = new FileSystem();
		if(fileSystem.JDK_FsExist(path) != SDK_OK)
		{
			gui.cls_show_msg("请先放置打印测试图片于%s目录下再进行测试,放置完毕任意键继续",path);
			if(fileSystem.JDK_FsExist(path) != SDK_OK)
			{
				gui.cls_show_msg1(2,"测试人员仍未放置图片,不进行打印，即将退出");
				return NDK_ERR;
			}
		}
		
		if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "prnProcess_kuaiqian", g_keeptime,"line %d:打印机状态异常！(ret = %d)", Tools.getLineInfo(), prnStatus);
			return prnStatus;
		}
		if((ret = JniNdk.JNI_Print_Init(1))!=NDK_OK)
			return ret;
		
		if((ret = JniNdk.JNI_Print_SetMode(3,0))!=NDK_OK)
			return ret;
		
		if((ret = JniNdk.JNI_Print_SetGrey(3))!=NDK_OK)
			return ret;
		
		if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "prnProcess_kuaiqian", g_keeptime,"line %d:打印机状态异常！(ret = %d)", Tools.getLineInfo(), prnStatus);
			return prnStatus;
		}
		
		if((ret=JniNdk.JNI_Print_Script(strData, Tools.getWordCount(strData)))!=NDK_OK)
			return ret;

		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;

		return NDK_OK;
	}
	
	private class CallBack  
	{  
	    private int value=0;  
	    public int getValue()  
	    {  
	        return value;  
	    }  
	    public synchronized void setValue(int value)  
	    {  
	        this.value=value;  
	    }  
	}  
	
	//磁卡操作线程
	private class MagThread extends Thread{
		//停止标志位
		volatile boolean flag = true;
		//回调参数
		private CallBack value;
		
		public MagThread(CallBack value){
			this.value = value;
		}
		
		@Override 
		public void run(){
			while(flag){
				JniNdk.JNI_Mag_Close();
				JniNdk.JNI_Mag_Open();
				//再起个线程swiped
				MagSwipeThread swipeThread = new MagSwipeThread(value);
				swipeThread.start();
				try {
					swipeThread.join(15*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				JniNdk.JNI_Mag_Close();
				if((value.getValue()&0x01)==0x01){
					JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
					JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x00);
					JniNdk.JNI_Rfid_CloseRf();
					break;
				}
			}
		}
		
		public void cancel(){
			flag = false;
		}
	}
	
	//磁卡刷卡线程
	private class MagSwipeThread extends Thread{
		byte[] pszTk1 = new byte[MAXTRACKLEN];
		byte[] pszTk2 = new byte[MAXTRACKLEN];
		byte[] pszTk3 = new byte[MAXTRACKLEN];
		int[] errorCode = new int[1];
		String szPan = "";
		//回调参数
		private CallBack value;
		
		public MagSwipeThread(CallBack value){
			this.value = value;
		}
		
		@Override 
		public void run() {
			int ret = -1, timeout = 2;
			byte[] swiped = new byte[1];
			long startTime = System.currentTimeMillis();
			while (Tools.getStopTime(startTime)<timeout) 
			{
				ret = JniNdk.JNI_Mag_Swiped(swiped);
				if (ret == NDK_OK && swiped[0] == 1)
					break;
				
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (ret == NDK_OK && swiped[0] == 1){
				// 磁卡寻卡成功
				value.setValue(0x01);
				LoggerUtil.d(value.getValue()+"");
				if ((ret = JniNdk.JNI_Mag_ReadNormal(pszTk1, pszTk2, pszTk3,errorCode)) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "magRunner", g_keeptime,"line %d:读磁道数据失败(%d)", Tools.getLineInfo(), ret);
				} else{
					// 显示磁卡数据-二磁道截取
					String str_TK2 = new String(pszTk2);
					int index = str_TK2.indexOf('=');
					if (index == -1){
						gui.cls_show_msg1(2, "line %d:刷卡失败!", Tools.getLineInfo());
					}
					else {
						szPan = str_TK2.substring(0, index);
						gui.cls_show_msg1(2,"卡号:%s", szPan);
					}
				}
			}
		}
	}
	
	//非接卡操作线程
	private class UnionRfThread extends Thread{
		int ret = -1;
		byte[] psPiccType = new byte[1];
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		//停止标志位
		volatile boolean flag = true;
		//回调参数
		private CallBack value;
		
		public UnionRfThread(CallBack value){
			this.value = value;
		}
		
		@Override 		
		public void run(){
			//寻卡操作
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x0a);// 调用三次
			JniNdk.JNI_Rfid_CloseRf();
			JniNdk.JNI_Rfid_Init(null);
			JniNdk.JNI_Rfid_OpenRf();
			JniNdk.JNI_Rfid_PiccType((byte) 0xcd);
			while(flag){
				ret = JniNdk.JNI_Rfid_PiccDetect(psPiccType);
				if (ret == NDK_OK){
					value.setValue(0x04);
					LoggerUtil.d(value.getValue()+"");
					break;
				}
			}	
		}
		
		public void cancel(){
			flag = false;
		}
	}
	
	//非接卡操作线程
	private class RfidThread extends Thread{
		int ret = -1;
		byte[] psPiccType = new byte[1];
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		//停止标志位
		volatile boolean flag = true;
		//回调参数
		private CallBack value;
		
		public RfidThread(CallBack value){
			this.value = value;
		}
		
		@Override 		
		public void run(){
			//寻卡操作
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x0a);// 调用三次
			JniNdk.JNI_Rfid_CloseRf();
			JniNdk.JNI_Rfid_Init(null);
			JniNdk.JNI_Rfid_OpenRf();
			JniNdk.JNI_Rfid_PiccType((byte) 0xcd);
			while(flag){
				ret = JniNdk.JNI_Rfid_PiccDetect(psPiccType);
				if (ret == NDK_OK){
					JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen, psDataBuf);
					JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x0a);
					value.setValue(0x04);
					LoggerUtil.d(value.getValue()+"");
					break;
				}
			}	
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 0x0a);
		}
		
		public void cancel(){
			flag = false;
		}
	}
	
	//ic卡操作线程
	private class IccThread extends Thread{
		int ret = -1;
		//停止标志位
		volatile boolean flag = true;
		//回调参数
		private CallBack value;
		
		public IccThread(CallBack value){
			this.value = value;
		}
		
		@Override 		
		public void run(){
			int[] pnSta = new int[1];
			JniNdk.JNI_Icc_PowerDown(0);
			while(flag){
				ret = JniNdk.JNI_Icc_Detect(pnSta);
				if(ret == NDK_OK && pnSta[0] == 0x01){
					value.setValue(0x02);
					LoggerUtil.d(value.getValue()+"");
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void cancel(){
			flag = false;
		}
	}
}
