package com.example.highplattest.tool.trade;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_MODE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
import com.newland.ndk.TimeNewland;

public class TradeTool implements NDK,Lib
{
	private final String TAG = "TradeTool";
	private int nReadCardMode = 0x07;
	private static TradeTool mTradeTool;
	private Gui gui;
	private Context mContext;
	private Handler mHandler;
	private int g_keeptime = 2;
	private String TESTITEM="SDK2.0模拟交易";
	
	private TradeTool(Context context,Handler handle)
	{
		mContext = context;
		mHandler = handle;
		gui = new Gui(context, handle);
	}
	
	public static TradeTool getInstance(Context context,Handler handle)
	{
		if(mTradeTool==null)
			mTradeTool = new TradeTool(context,handle);
		return mTradeTool;
	}
	/**
	 * 消费流程(非事件机制，支持SDK2.0、银商应用(不带打印)),使用SAK方式判断CPU卡和M1卡
	 * @throws InterruptedException 
	 */
	public int trans_sdk2(int timeout,boolean isPrn) throws InterruptedException
	{
		/*private & local definition*/
		final String funcName = "trans_sdk2";
		int ret = 0;
		PrintUtil printUtil = new PrintUtil(mContext,mHandler,true);
		// 非接信息
		final byte[] psSakBuf = new byte[1];
		// 获取pos信息
		byte[] sBuf = new byte[128];
		
		CallBack value = new CallBack();
		value.setValue(0);
		
		/**测试前置，判断是否支持射频、磁卡、IC*/
		nReadCardMode = 0;
		Set set = GlobalVariable.gModuleEnable.entrySet();
		Iterator iter = set.iterator();
		for (Object obj : GlobalVariable.gModuleEnable.entrySet()) {
			Map.Entry entry = (Entry) obj; // obj 依次表示Entry
			Log.d("eric","KEY===" + entry.getKey() + "     Value====" + entry.getValue());

			if (entry.getKey().toString().equals("RfidEnable")&& entry.getValue().toString().equals("true")) 
			{
				Log.d("eric", "RF----------");
				nReadCardMode = nReadCardMode | 0x04;
			}
			if (entry.getKey().toString().equals("IccEnable")&& entry.getValue().toString().equals("true")) 
			{
				Log.d("eric", "IC----------");
				nReadCardMode = nReadCardMode | 0x02;
			}
			if (entry.getKey().toString().equals("MagEnable")&& entry.getValue().toString().equals("true")) 
			{
				Log.d("eric", "MAG----------");
				nReadCardMode = nReadCardMode | 0x01;
			}
			if (entry.getKey().toString().equals("PrintEnable")&& entry.getValue().toString().equals("true")) 
			{
				Log.d("eric", "PrintEnable----------");
				nReadCardMode = nReadCardMode | 0x08;
			}
			if (entry.getKey().toString().equals("PinEnable")&& entry.getValue().toString().equals("true")) 
			{
				Log.d("eric", "PinEnable----------");
				nReadCardMode = nReadCardMode | 0xF0;
			}
		}
			
		
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)
		{
			// 安装TLK
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), secKcvInfo);
		}
		/*process body*/ 
		JniNdk.JNI_Sec_SetKeyOwner("*");
		// 装载TPK和TAK密钥
		// 测试前置，安装TPK密钥密文形式，主密钥索引1,32个1
		secKcvInfo.nCheckMode=0;
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA253C9D9D7C2FBBFA"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return ret;
		}
		// 调用下NDK_SecCalcDes
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)3, 16, ISOUtils.hex2byte("950973182317F80BF679786E2411E3DE"), secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			return ret;
		}
		if((nReadCardMode&0x08)==0x08)/**支持打印机，打印机状态判断*/
		{
			
			if((ret = printUtil.getPrintStatusOri())!=NDK_OK)
				gui.cls_show_msg("打印机缺纸,请装纸");
		}

		//----银行卡收款------
		// 获取BIOS版本号，获取SN号、TUSN、UTSN(UTSN=00000304N7NL00072365, nLen=20)
		JniNdk.JNI_Sys_GetPosInfo(ParaEnum.EM_SYS_HWINFO.SYS_HWINFO_GET_BIOS_VER.secsyshwinfo(), 0, sBuf);
		JniNdk.JNI_Sys_GetPosInfo(ParaEnum.EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), 0, sBuf);
		JniNdk.JNI_Sys_GetPosInfo(ParaEnum.EM_SYS_HWINFO.SYS_HWINFO_GET_POS_TUSN.secsyshwinfo(), 0, sBuf);
		// 设置KeyOwner为_NL_TERM_MGR
		JniNdk.JNI_Sec_SetKeyOwner("_NL_TERM_MGR");
		// dd 00 00 00 73 0c 00 00 00 5f 4e 4c 5f 54 45 52 4d 5f 4d 47 52 63 44 63 ff 73 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 64 10 00 00 00 63 10 76
//		JniNdk.JNI_Sec_CalcDes(0x44, 0xff, arg2, 16, arg4, arg5)// 返回-1209
		JniNdk.JNI_Sec_SetKeyOwner("*");
		
		MagThread magThread = new MagThread(value);
		UnionRfThread rfidThread = new UnionRfThread(value);
        IccThread iccThread = new IccThread(value);
        StringBuffer strbuBufferTip = new StringBuffer();
		if((nReadCardMode&0x01)==0x01)
		{
			strbuBufferTip.append("刷卡/");
			magThread.start();
		}
		if((nReadCardMode&0x02)==0x02)
		{
			strbuBufferTip.append("插卡/");
			iccThread.start();
		}
			
		if((nReadCardMode&0x04)==0x04)
		{
			strbuBufferTip.append("挥卡");
			 rfidThread.start();
		}
			
		gui.cls_printf(("请"+strbuBufferTip+"...").getBytes());
		
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
						JniNdk.JNI_Mag_Close();
						
						// 寻卡类型必须已经设置为TYPE_A
						if((ret = JniNdk.JNI_Rfid_PiccType((byte) 0xcc))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:设置寻卡类型失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						if((ret = JniNdk.JNI_Rfid_M1Request((byte) 1, pnDataLen, psDataBuf))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:M1Request失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						if((ret = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:M1Anti失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						if((ret = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], psDataBuf, psSakBuf))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:M1Request失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						// 取到SAK
						if((psSakBuf[0]&0x20)==0x20)// A卡
						{
							JniNdk.JNI_Rfid_PiccDeactivate((byte) 0);
							unionFile();
							
							TimeNewland timeNewland = new TimeNewland();
							JniNdk.JNI_Sys_GetPosTime(timeNewland);
							gui.cls_show_msg("获取到的K21端时间:%s", timeNewland.formatTime());
							
							byte[] getRandom = new byte[4];
							if((ret = JniNdk.JNI_Sec_GetRandom(4, getRandom))!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:GetRandom失败(%d)", Tools.getLineInfo(), ret);
								return;
							}
							if((ret = JniNdk.JNI_Rfid_Init(null))!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:GetRandom失败(%d)", Tools.getLineInfo(), ret);
								return;
							}
							if((ret = JniNdk.JNI_Rfid_OpenRf())!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:OpenRf失败(%d)", Tools.getLineInfo(), ret);
								return;
							}
							if((ret = JniNdk.JNI_Rfid_PiccType((byte) 0xcd))!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:PiccType失败(%d)", Tools.getLineInfo(), ret);
								return;
							}
							if((ret = JniNdk.JNI_Rfid_PiccDetect(null))!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:PiccDetect失败(%d)", Tools.getLineInfo(), ret);
								return;
							}
							if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen, psDataBuf))!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:激活失败(%d)", Tools.getLineInfo(), ret);
								return;
							} 
							// 实际交易有N次的APDU
							if((ret = JniNdk.JNI_Rfid_PiccApdu(select_1pay.length, select_1pay, pnDataLen, psDataBuf))!=NDK_OK)
							{
								gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:CPU卡APDU失败（%d）", Tools.getLineInfo(),ret);
								return;
							}
							if(pnDataLen[0]==10)
							{
								// CPU卡操作
								gui.cls_show_msg1_record(TAG, funcName, 0,"CPU卡上电成功+取随机数操作成功");
							}
							else
							{
								gui.cls_show_msg1_record(TAG, funcName, 0,"CPU卡上电成功,不支持取随机数");
							}
						}
						else//M1卡
						{
							gui.cls_show_msg1_record(TAG, funcName, 0,"不支持M1卡操作");
						}

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
						gui.cls_show_msg("获取到的K21端时间:%s", timeNewland.formatTime());
						JniNdk.JNI_Sec_GetRandom(4, arg1);
						
						if ((ret = JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),psAtrBuf, pnAtrLen)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:IC上电失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						// 取随机数
						if ((ret = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
						//文件读写操作
						unionFile();
						// 取随机数
						if ((ret = JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), 5,req, pnRecvLen, psRecvBuf)) != NDK_OK) {
							gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:IC卡APDU失败(%d)", Tools.getLineInfo(), ret);
							return;
						}
					}
				};
				iccrwThread.start();
				iccrwThread.join(15*1000);
				break;
			}
		}
		
		
		// 获取机器号
		JniNdk.JNI_Sys_GetPosInfo(ParaEnum.EM_SYS_HWINFO.SYS_HWINFO_GET_POS_TYPE.secsyshwinfo(), 0, sBuf);
		JniNdk.JNI_Sec_SetKeyOwner("*");
		
		if((nReadCardMode&0xF0)==0xF0)
		{
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable))//X5不支持密码键盘
				secPinInput("6225885916163157",3);// 密码键盘流程
		}
		
		// 打印操作
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnable)&&isPrn)
		{
			// 打印操作
			JniNdk.JNI_Print_SetGrey(3);
			if ((ret = JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnSetMode测试失败(ret=%d)", Tools.getLineInfo(),ret);
				return ret;
			}
			printUtil.print_bill();
			printUtil.print_bill();
		}
			
		if((nReadCardMode&0x02)==0x02)
			JniNdk.JNI_Icc_PowerDown(0);
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
		
		if((iRet= BaseFragment.touchscreen_getnum(strBuffer))!=NDK_OK)// 密码键盘初始化
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
		
		byte[] szPinOut = DefaultFragment.getPinInput(strBuffer.toString(),mHandler);
		if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("9B01DF2244D84792"), 8) == false) {
			gui.cls_show_msg1_record(TAG, "secPinInput", g_keeptime, "line %d:%s测试失败(szPinOut=%s)",Tools.getLineInfo(),TESTITEM,Dump.getHexDump(szPinOut));
			return;
		}
		gui.cls_show_msg("密码输入完毕,任意键继续");
	}
	
	private void unionFile()
	{
		int fd,ret=-1;
		
		JniNdk.JNI_FsExist("/appfs/kernel1.a");
		JniNdk.JNI_FsDel("/appfs/kernerl1.a");
		if((fd = JniNdk.JNI_FsOpen("/appfs/kernel1.a", "w"))<0)
		{
			gui.cls_show_msg1_record(TAG, "unionFile", g_keeptime,"line %d:FsOpen失败(%d)", Tools.getLineInfo(), fd);
			return;
		}
		byte[] wBuf = new byte[4000];// 只模拟了大小，内容并没摘取
		Arrays.fill(wBuf, (byte)0x33);
		JniNdk.JNI_FsWrite(fd, wBuf, wBuf.length);
		byte[] wbuf1 = new byte[551];
		Arrays.fill(wbuf1, (byte)0x55);
		JniNdk.JNI_FsWrite(fd, wbuf1, wbuf1.length);
		if((ret = JniNdk.JNI_FsClose(fd))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "unionFile", g_keeptime,"line %FsClose失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
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
