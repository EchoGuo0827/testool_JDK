package com.example.highplattest.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import com.example.highplattest.R;
import com.example.highplattest.activity.TouchActivity;
import com.example.highplattest.main.bean.NlsPara;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.WifiApBean;
import com.example.highplattest.main.btutils.ClientAdapter;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.Pair_Result;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.TransStatus;
import com.example.highplattest.main.constant.ParaEnum.WIFI_SEC;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Enctyp;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.LayerBase;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DiskInfo;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.FelicaParam;
import com.newland.ndk.ISO15693MemBlock;
import com.newland.ndk.ISO15693ProxCard;
import com.newland.ndk.ISO15693SysInfo;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.newland.NLUART3Manager;
import android.newland.NlModemManager;
import android.newland.net.ethernet.NlEthernetManager;
import android.newland.os.NlBuild;
import android.newland.scan.ScanUtil;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.systest.SysTest2;

/************************************************************************
 * history 		 	: 变更记录							date			remarks
 *			  		 打印事件check增加0.5s的延时		   20200426     	郑薛晴
 ************************************************************************/
public class DefaultFragment extends BaseFragment 
{
	protected int g_time_0 = 0;
	protected LayerBase layerBase;
	protected int apduLen = 0;
	protected int byteLength = 1024;
	Handler childHandler,mainHandler;
	HandlerThread handlerThread = new HandlerThread("Camera2");
	protected final int ABILITY_VALUE = 20;// 交叉压力默认次数
	protected final int TIMEOUT_INPUT = 30;// 默认超时时间
	//910蓝牙底座
	protected String wifiOldPwd;
	protected int encMode=3;
	protected String EthMac;
	public WifiApBean WifiApSetting = new WifiApBean();
	public static ClientAdapter unpairAdapter;
	
	
	//ISO15693
	public ISO15693ProxCard[] Mcards = new ISO15693ProxCard[20];//用来存获取到的卡的uid的卡信息，可能有多张卡
	public ISO15693SysInfo[] sysInfo = new ISO15693SysInfo[1];//卡信息
	byte[] data_NDK = {0x11,0x22,0x33,0x44};
	public ISO15693MemBlock memBlock = new ISO15693MemBlock();
	public Random random = new Random();
	//安卓串口接扫描枪扫码的返回值
//	public int ret=-1;
	//SDK3.0兼容
	public final int MAXWAITTIME = 82800000;//开发说现在不支持<0代表永不超时的情况了，最大超时时间为 82800000ms，by 20200319 zhengxq
	public Object mRfObj = new Object(); 
	public int rfFlag=-1,iccFlag=-1,prnFlag=-1,magFlag=-1,pinFlag=-1;
	public NotifyEventListener rflistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.e(TAG+",notifyEvent===监听到射频"+eventNum);
			rfFlag = eventNum;
			synchronized (mRfObj) {
				mRfObj.notify();
			}
			return SUCC;
		}
	};
	public NotifyEventListener icclistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.e(TAG+",notifyEvent===监听到icc"+eventNum);
			iccFlag = eventNum;
			return SUCC;
		}
	};
	public NotifyEventListener maglistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.e(TAG+",notifyEvent===监听到mag"+eventNum);
			magFlag = eventNum;
			return SUCC;
		}
	};
	public NotifyEventListener pinlistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.e(TAG+",notifyEvent===监听到pin"+eventNum);
			pinFlag = eventNum;
			return SUCC;
		}
	};
	public NotifyEventListener prnlistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.e(TAG+",notifyEvent===监听到print"+",eventNum="+eventNum);
			prnFlag = eventNum;
			return SUCC;
		}
	};			
	
	/*private  class Fhandle extends Handler{
		 private WeakReference<Activity> reference;

	        // 在构造方法中传入需持有的Activity实例
	        public Fhandle(Activity activity) {
	            // 使用WeakReference弱引用持有Activity实例
	            reference = new WeakReference<Activity>(activity); }
	        @Override
	        public void handleMessage(Message msg) {
	        	switch (msg.what) {
				case HandlerMsg.SCAN_SURFACE_FLUSH:
					Log.d("eric", "解决内存泄漏。。。");
					setSurface();
					break;

				default:
					break;
				}
	        }

	}
	*/
	public  Handler handler = new Handler()
	{
	
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case 1122:
				 EthernetUtil.mEthernetManager = new NlEthernetManager(myactivity);
				break;
			
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mtvShow.setText((CharSequence) msg.obj);
				break;
				
			/*case HandlerMsg.SCAN_SURFACE_FLUSH:
				setSurface((int)msg.arg1);
				break;*/
				
			case HandlerMsg.DIALOG_SYSTEST_SURFACE_UI:
				myactivity.setContentView((View)msg.obj);
				break;
				
			case HandlerMsg.DIALOG_SYTEST_LCD_CONFIG:
				new ShowDialog().show_times_press(myactivity, (PacketBean) msg.obj);
				break;
				
			case HandlerMsg.SURFACEVIEW_VIEW:
				Log.d("eric", "发送消息通知VIEW！！！");
				layScanView.setVisibility(View.VISIBLE);
				break;
				
			case HandlerMsg.SURFACEVIEW_GONE:
				Log.d("eric", "发送消息通知GONE！！！");
				layScanView.setVisibility(View.GONE);
				break;
				
			case HandlerMsg.DIALOG_SYSTEST_BACK:
				imageBack.setBackgroundDrawable(new BitmapDrawable((Bitmap) msg.obj));
				
				break;
				
			case HandlerMsg.DIALOG_SYSTEST_IMG_DRAWABLE:// 用于测试Android 9 add by 20181120
				imageBack.setImageDrawable((Drawable) msg.obj);
				break;
				
			case HandlerMsg.DIALOG_SYSTEST_BACK_ID:
				imageBack.setBackgroundResource((Integer) msg.obj);
				break;

			case HandlerMsg.DIALOG_DATA_LENGTH:
				set_data_length((String) msg.obj);
				break;
				
			case HandlerMsg.DIALOG_ETH_MAC:
				setEthMac((String) msg.obj);
				break;
				
			case HandlerMsg.DIALOG_SYSTEST_MAG_CONFIG:
				new Config(myactivity,handler).set_mag_config((PacketBean) msg.obj, myactivity);
				break;
				
			default:
				break;
			}
		};
	};
	private Gui gui = null;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		gui = new Gui(myactivity, handler);
		//处理g_CycleTime和界面选择等待时常
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull){
//			g_CycleTime = myactivity.getParaInit().getCrossTime();
			WAITMAXTIME=2;
		}
		else
			WAITMAXTIME=30;
		return view;
		
		
		
	}
	
	
	public void handlerShowTime(int handlerMsg,String msg,int time)
	{
		handler.sendMessage(handler.obtainMessage(handlerMsg, msg));
		SystemClock.sleep(time*1000);
	}
	
	/*---------------constants/macro definition---------------------*/
	private final String  TAG = DefaultFragment.class.getSimpleName();
	private final String TESTFILE = "test.txt";
	
	/*----------global variables declaration------------------------*/
	int time = 0;
	public int g_keeptime = 5;
	public int long_keeptime = 10;
	// 默认交叉压力次数
	protected int g_CycleTime =20;
//	// 处理器
//	public static N900Device n900Device;

	public int getCycleValue() {
		return g_CycleTime;
	}
	
	/**
	 * 初始化layer对象
	 */
	protected void initLayer()
	{
		layerBase = Layer.getLayerBase(myactivity,handler);
	}
	
	/**
	 * 初始化modem的配置值
	 * @param packet
	 * @param buf
	 * @return
	 */
	protected PacketBean init_snd_packet(PacketBean packet,byte[] buf)
	{
		/*private & local definition*/
		
		/*process body*/
		packet.setHeader(buf);
		packet.setLen(0);
		packet.setOrig_len(0);
		packet.setLifecycle(0);
		packet.setForever(false);
		packet.setIsLenRec(false);
		packet.setIsDataRnd(true);
		return packet;
	}
	
	/**
	 * modem参数配置操作
	 * @param packet Packet对象
	 * @param buf	  随机数据/固定数据
	 * @return		 Packet对象
	 */
	public PacketBean set_snd_packet(final PacketBean packet,final LinkType type)
	{
		/*process body*/
		if(GlobalVariable.gSequencePressFlag)
		{
			packet.setLifecycle(getCycleValue());
			packet.setLen(getCommPackLen());
			packet.setOrig_len(getCommPackLen());
		}
		else
		{
			Log.e(TAG, "set_snd_packet");
			packet.setForever(false);
			myactivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					new ShowDialog().snd_packet(myactivity,packet, type);
				}
			});
			synchronized (type) {
				try {
					type.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(packet.getLifecycle()<0)
				return packet;
			// 添加同步头
			if(type == LinkType.SYNC)
				// 添加TPDU包头
				System.arraycopy(SDLCPCKTHEADER, 0, packet.getHeader(), 0,SDLCPCKTHEADER.length);
		}
		
		return packet;
	}
	

	//设置数据长度
	public void set_data_length(String title){
		
		
		final EditText etLength = new EditText(myactivity);
		etLength.setText("1024");
		new BaseDialog(myactivity,etLength,"设置收发数据长度","确定",false,new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View View, boolean isPositive) {
				if(isPositive){
					byteLength=Integer.parseInt(etLength.getText().toString());
					synchronized (g_lock) {
						g_lock.notify();
					}
				}
				
			}
		}).show();
	}
	
	/*SDK3.0注册事件机制的注册*/
	public int RegistEvent(int RegistEvent,NotifyEventListener listener){
		int ret=NDK_OK;
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			if(RegistEvent==EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()&&GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnableReg)==false)/*X5不支持打印事件机制，N700没有打印模块**/
				break;
			else
				ret = JniNdk.JNI_SYSRegisterEvent(RegistEvent,MAXWAITTIME,listener);
			break;
		default:
			return NDK_ERR;
		}
		return ret;
	}
	/*SDK3.0注册事件机制的注销*/
	public int UnRegistEvent(int RegistEvent){
		int ret=NDK_OK;
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			if(RegistEvent==EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()&&GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnableReg)==false)
				break;
			ret=JniNdk.JNI_SYSUnRegisterEvent(RegistEvent);
			break;
		default:
			return NDK_ERR;
		}
		return ret;
	}
	
	/**解绑全部事件,20200415*/
	public void UnRegistAllEvent(EM_SYS_EVENT[] eventNums)
	{
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			for(EM_SYS_EVENT eventNum:eventNums)
			{
				JniNdk.JNI_SYSUnRegisterEvent(eventNum.getValue());
			}
			break;
			
		default:
			break;
		}
	}
	public int SmartRegistEvent(_SMART_t  type){
		int ret=NDK_OK;
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			switch (type) {
			case CPU_A:
			case CPU_B:
			case MIFARE_0:   //开发回复现在卡片都走事件机制
			case MIFARE_1:
			case FELICA:
			case MIFARE_0_C:
//			case ISO15693:
	
				ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, rflistener);
				break;
		
//			case MIFARE_0://M0卡不支持事件机制
//			case MIFARE_1:
			case ISO15693:
			case SAM1:
			case SAM2:

				return NDK_NO_SUPPORT_LISTENER;
//				break;
			case IC:
				ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), MAXWAITTIME, icclistener);
				break;
			default:
				return NDK_ERR;
			}
			break;
		default:
			return NDK_ERR;
		}
		return ret;
	}
	public int SmartUnRegistEvent(_SMART_t  type){
		int ret=NDK_OK;
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			switch (type) 
			{
			case CPU_A:
			case CPU_B:
			case MIFARE_1:
			case FELICA:
			case ISO15693:
			case MIFARE_0:
			case MIFARE_0_C:
				ret=JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				break;
//			case MIFARE_0://M0卡不支持事件机制
			case SAM1:
			case SAM2:
				break;
			case IC:
				ret=JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				break;
			default:
				return NDK_ERR;
			}
			break;
		default:
			return NDK_ERR;
		}
		return ret;
	}

	public int SmartResume(_SMART_t type) {
		int ret = NDK_OK;
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			switch (type) 
			{
			case CPU_A:
			case CPU_B:
			case MIFARE_1:
			case MIFARE_0:
			case FELICA:
			case ISO15693:
			case MIFARE_0_C:
				ret = JniNdk.JNI_SYSResumeEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				break;
			case SAM1:
			case SAM2:
			case IC:
				break;
			default:
				return NDK_ERR;
			}
			break;
		default:
			return NDK_ERR;
		}
		return ret;

	}

	//获取pos当前时间
	public String getSysNowTime() 
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	// modem模块操作
	// add by 20150209
	// 清modem缓冲区
	public final int mdm_clrportbuf_all(NlModemManager nlModemManager) 
	{
		/*private & local definition*/
		LinkType type = ModemBean.type_MDM;
		int ret=-1;
		
		/*process body*/
		if(type == LinkType.ASYN|| type == LinkType.SYNC)
		{
			// 清modem缓冲区
			try {
				ret = nlModemManager.clrbuf();
			} catch (RemoteException e) {
				e.printStackTrace();
				ret = MODEM_EXCEPTION_THROW;
			}
		}
		return ret;
	}
	// end by 20150209
	
	// add by 20150209
	// modem的复位操作
	public final int mdm_reset(NlModemManager nlModemManager) 
	{
		/*private & local definition*/
		LinkType type = ModemBean.type_MDM;
		int ret = -1;
		
		/*process body*/
		if(type == LinkType.ASYN || type == LinkType.SYNC)
		{
			try {
				ret = nlModemManager.reset();
			} catch (RemoteException e) {
				e.printStackTrace();
				return MODEM_EXCEPTION_THROW;
			}
			SystemClock.sleep(2000);// 根据陈仕廉建议 modem复位后休眠2s
		}
			
		return ret;
	}
	// end by 20150209

	// add by 20150209
	// modem的初始化操作
	public final int mdm_init(NlModemManager nlModemManager) 
	{
		/*private & local definition*/
		LinkType type = ModemBean.type_MDM;
		
		/*process body*/
		if(type == LinkType.ASYN)
			try {
				return nlModemManager.asynInit(ModemBean.MDMPatchType);
			} catch (RemoteException e) {
				e.printStackTrace();
				return MODEM_EXCEPTION_THROW;
			}
		else if(type == LinkType.SYNC)
			try {
				return nlModemManager.sdlcInit(ModemBean.MDMPatchType);
			} catch (RemoteException e) {
				e.printStackTrace();
				return MODEM_EXCEPTION_THROW;
			}
		else
			return GlobalVariable.FAIL;
	}
	// end by 20150209
	
	// add by 20150316
	// modem拨号压力
	public int mdm_dial_press(NlModemManager nlModemManager,String dialStr,PacketBean sendPacket,boolean rst,float[] caltime) 
	{
		/*private & local definition*/
		LinkType type = ModemBean.type_MDM;
		int send_len = 0,rec_len = 0,ret = 0;
		byte[] rbuf = new byte[PACKMAXLEN];
		caltime[0] = caltime[1] = 0.0f;
		long startTime;
		Layer layer = new Layer(myactivity, handler);
		
		/*process body*/
		if(!rst)
		{
			// modem复位
			gui.cls_show_msg1(2, "复位modem中...");
//			input("复位modem中...");
			mdm_reset(nlModemManager);
		}
		// 初始化modem
		gui.cls_show_msg1(0, "初始化modem中...");
//		inputShort("初始化modem中...", Color.BLACK);
		if((ret = mdm_init(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_press", g_keeptime,"%s,line %d:初始化MODEM失败(%d)", TAG,Tools.getLineInfo(),ret);
			return ret;
		}
		// 拨号
		gui.cls_show_msg1(2, "modme拨%s号中...", ModemBean.MDMDialStr);
//		inputShort(String.format("MODEM拨%s中...", BpsSetting.type_MDM),Color.BLACK);
		startTime = System.currentTimeMillis();
		if((ret = layer.mdm_dial(dialStr, nlModemManager))!=NDK_OK)
		{
			layer.mdm_hangup(nlModemManager);
			gui.cls_show_msg1_record(TAG, "mdm_dial_press", g_keeptime,"%s,line %d:MODEM拨%s失败(ret = %d)", TAG,Tools.getLineInfo(),dialStr,ret);
			return ret;
		}
		caltime[0] = caltime[0]+Tools.getStopTime(startTime);
//		inputShort("MODEM数据通讯中...", Color.BLACK);
		Log.e(TAG, sendPacket.getLen()+"");
		// 发送数据
		if((send_len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
		{
			layer.mdm_hangup(nlModemManager);
			gui.cls_show_msg1_record(TAG, "mdm_dial_press", g_keeptime,"%s,line %d:发送数据失败(实际%d,预期%d)", TAG,Tools.getLineInfo(),send_len,sendPacket.getLen());
			return send_len;
		}
		// 发送与接收之间需要延时
		SystemClock.sleep(3000);
		// 接收
		if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 30,type))!= sendPacket.getLen())
		{
			layer.mdm_hangup(nlModemManager);
			gui.cls_show_msg1_record(TAG, "mdm_dial_press", g_keeptime,"%s,line %d:接收数据失败(实际%d,预期%d)", TAG,Tools.getLineInfo(),rec_len,sendPacket.getLen());
			return rec_len;
		}
		// 比较数据
		if(!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen()))
		{
			layer.mdm_hangup(nlModemManager);
			gui.cls_show_msg1_record(TAG, "mdm_dial_press",g_keeptime, "%s,line %d:数据校验失败", TAG,Tools.getLineInfo());
			return NDK_ERR;
		}
		// 挂断
		gui.cls_show_msg1(2, "modme挂断中...");
//		inputShort("MODEM挂断中", Color.BLACK);
		startTime = System.currentTimeMillis();
		if((ret = layer.mdm_hangup(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_press", g_keeptime,"%s,line %d:MODEM挂断失败", TAG,Tools.getLineInfo());
			return ret;
		}
		caltime[1] = caltime[1]+Tools.getStopTime(startTime)-(type == LinkType.SYNC?SLEEP_SDLC_HANGUP:0);
		return NDK_OK;
	}
	
	// add by 20150210
	// 添加包头
	public final int update_snd_packet(PacketBean packet,LinkType type)
	{
		/*process body*/
		if(!packet.isForever() && packet.getLifecycle()<=0)
			return NDK_ERR;
		// 是否递增
		if(packet.isIsLenRec())
			packet.setLen(packet.getLen()+1);
		if(packet.getLen()>PACKMAXLEN)
			packet.setLen((packet.getLen()>PACKMAXLEN)? PACKMAXLEN:packet.getLen());
		if((type == LinkType.BT) && packet.getLen()>BUFSIZE_BT)
		{
			packet.setLen(BUFSIZE_BT);
			packet.setOrig_len(BUFSIZE_BT);
		}
		if(type == LinkType.SYNC && packet.getOrig_len()>SDLCPCKTMAXLEN)
			packet.setLen(packet.getOrig_len()>SDLCPCKTMAXLEN? SDLCPCKTMAXLEN:packet.getOrig_len());
		if(type == LinkType.ASYN && packet.getLen()>ASYNPCKTMAXLEN)
			packet.setLen(packet.getOrig_len()>ASYNPCKTMAXLEN? ASYNPCKTMAXLEN:packet.getOrig_len());
		// 随机
		if(packet.isIsDataRnd())
		{
			byte[] ptr = new byte[packet.getLen()];
			for (int i = 0; i < packet.getLen(); i++) 
			{
				ptr[i] = (byte) (Math.random()*128);
			}
			packet.setHeader(ptr);
		}
		// 固定数据
		else
		{
			byte[] buf = new byte[packet.getLen()];
			Arrays.fill(buf, 0, packet.getLen(), packet.getDataFix());
			packet.setHeader(buf);
		}
		
		// 添加包头
		if(type == LinkType.SYNC)
			// 添加TPDU头
			System.arraycopy(SDLCPCKTHEADER, 0, packet.getHeader(), 0,
					SDLCPCKTHEADER.length);
		if(type == LinkType.ASYN)
			System.arraycopy(SDLCPCKTHEADER, 0, packet.getHeader(), 0,
					SDLCPCKTHEADER.length);
		if(!packet.isForever())
			packet.setLifecycle(packet.getLifecycle()-1);
		
		return NDK_OK;
	}
	// end by 20150210
	
	// add by 20150210
	// modem发送数据
	public final int mdm_send(NlModemManager nlModemManager,byte[] buf,int len) 
	{
		/*private & local definition*/
		int ret = 0;
		LinkType type = ModemBean.type_MDM;
		
		/*process body*/
		gui.cls_show_msg1(2, "modem数据发送中...");
//		input(String.format(Locale.CHINA, "数据发送中...POS->%s", BpsSetting.MDMDialStr));
		if(type == LinkType.ASYN || type == LinkType.SYNC)
			try {
				return ((ret = nlModemManager.write(buf, len)) == NDK_OK)? len:ret;
			} catch (RemoteException e) {
				e.printStackTrace();
				ret = MODEM_EXCEPTION_THROW;
			}
		return ret;
	}
	// end by 20150210
	
	// add by 20150210
	// modem的接收数据
	public final int mdm_rev(NlModemManager nlModemManager,byte[] buf,int len,int timeout,LinkType type) 
	{
		/*private & local definition*/
		int ret = 0;
		int rlen = len;
		
		/*process body*/
		gui.cls_show_msg1(2, "modme数据接收中...");
//		input(String.format(Locale.CHINA, "数据接收中...POS<-%s", BpsSetting.MDMDialStr));
		if(type == LinkType.ASYN)
		{
			if((ret = new Layer(myactivity,handler).getmodemreadlenN())<=0)
				return ret;
			try {
				return ((ret = nlModemManager.read(buf, rlen, timeout))==rlen)?rlen:ret;
			} catch (RemoteException e) {
				e.printStackTrace();
				return MODEM_EXCEPTION_THROW;
			}
		}
		else if(type == LinkType.SYNC)
		{
			try {
				return nlModemManager.read(buf, rlen, timeout);
			} catch (RemoteException e) {
				e.printStackTrace();
				return MODEM_EXCEPTION_THROW;
			}
		}
		else 
			return NDK_ERR;
	}
	// end by 20150210
//修改于2017/9/14 zhangxinj 因后台连续发数据8k，导致服务器压力过大，3k服务器较稳定，故先改成3k
	static int g_PackLen = 8*1024;
	
	public int getCommPackLen()
	{
		return g_PackLen;
	}
	

	// add by 20150211
	/**
	 * 读卡操作
	 * @param select_TK
	 * @param isDisplayed
	 * @param waittime 单位为s
	 * @return
	 */
	public int MagcardReadTest(int select_TK,boolean isDisplayed,final int waittime) 
	{
		Log.e("swip isDisplayed",isDisplayed+"");
		/*private & local definition*/
		int ret = 0;
		byte[] TK1_Buf = new byte[MAXTRACKLEN];
		byte[] TK2_Buf = new byte[MAXTRACKLEN];
		byte[] TK3_Buf = new byte[MAXTRACKLEN];
		int[] errCode = new int[1];
		
		/*process body*/
		Arrays.fill(TK1_Buf, (byte) 0);
		Arrays.fill(TK2_Buf, (byte) 0);
		Arrays.fill(TK3_Buf, (byte) 0);
		
		// poynt产品需要在刷卡之前要先关闭寻卡
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
		{
			if((ret=JniNdk.JNI_SetCheckinCardEventFlag(0))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "MagcardReadTest", g_keeptime, "line %d:关闭寻卡失败(ret=%d)",Tools.getLineInfo(), ret);
				return -1;
			}
		}
		
		gui.cls_printf(String.format("请刷卡(%ds)--->\n", waittime/1000).getBytes());
		ret = readMag(select_TK,waittime, TK1_Buf, TK2_Buf, TK3_Buf, errCode);
		
		switch (ret) 
		{
		case FAIL:
			gui.cls_show_msg1(2, "%s,line %d:刷卡失败", TAG,Tools.getLineInfo());
			break;
			
		case NDK_ERR_QUIT:
//			gui.cls_show_msg1(2, "%s,line %d:用户取消刷卡", TAG,Tools.getLineInfo());
			break;
			
		case NDK_ERR_TIMEOUT:
			gui.cls_show_msg1(2, "%s,line %d:刷卡超时", TAG,Tools.getLineInfo());
			break;
			
		case STRIPE:
			if((select_TK&TK1)==TK1)
			{
				if(isDisplayed)
				{
					String str_TK1 = new String(TK1_Buf);
					int index = str_TK1.indexOf("\0");
					
					if(index == -1)
						gui.cls_show_msg1(2, "1道无数据!", TAG,Tools.getLineInfo());
					else
					{
						String end_TK1 = str_TK1.substring(0, index);
						gui.cls_show_msg1(2, "1道数据(%d):%s\n", end_TK1.length(),end_TK1);
					}
						
				}
			}
			// TK2
			if(isDisplayed)
			{
				String str_TK2 = new String(TK2_Buf);
				int index = str_TK2.indexOf("\0");
				
				if(index == -1)
					gui.cls_show_msg1(2, "2道无数据!",TAG,Tools.getLineInfo());
				else
				{
					String end_TK2 = str_TK2.substring(0, index);
					gui.cls_show_msg1(2, "2道数据(%d):%s\n",end_TK2.length(), end_TK2);
				}
			}
			if(isDisplayed)
			{
				String str_TK3 = new String(TK3_Buf);
				int index = str_TK3.indexOf("\0");
				if(index == -1)
					gui.cls_show_msg1(2, "3道无数据!", TAG,Tools.getLineInfo());
				else
				{
					String end_TK3 = str_TK3.substring(0, index);
					gui.cls_show_msg1(2, "3道数据(%d):%s\n", end_TK3.length(),end_TK3);
				}
			}
			break;
			
		default:
			break;
		}
		return ret;
	}
	// end by 20150211
	
	// add by 20150616
	// 关闭读卡器
	public int magClose() 
	{
		return JniNdk.JNI_Mag_Close();
	}
	// end by 20150616
	
	// add by 20160913
	// 一个简易的刷卡操作
	public int readMag(int tracks,int waittime,byte[] TK1_Buf,byte[] TK2_Buf,byte[] TK3_Buf,int[] err) 
	{
		int nRet = SUCC,ret1= FAIL,diff=0;
		long oldTime = 0;
		byte[] swiped = new byte[1];
		
		// 读卡操作
		JniNdk.JNI_Mag_Reset();
		if((ret1 = JniNdk.JNI_Mag_Open())!=NDK_OK)
			return ret1;
		oldTime = System.currentTimeMillis();
		do
		{
			if(gui.wait_key(1)==ESC)
			{
				nRet = NDK_ERR_QUIT;
				break;
			}
			if(JniNdk.JNI_Mag_Swiped(swiped)==NDK_OK&&swiped[0]==1)// 已刷卡
			{
				//systest100、systest101、systest102、systest54、systest75也有涉及 ，但没有修改
				JniNdk.JNI_Sys_Delay(2);// 按照郑宁涵要求加0.2s的延时20200415，参照BUG2020040202371
//				if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false){
					// 读卡
					if((ret1= JniNdk.JNI_Mag_ReadNormal((tracks&TK1)==TK1?TK1_Buf:null, (tracks&TK2)==TK2?TK2_Buf:null, (tracks&TK3)==TK3?TK3_Buf:null, err))!=NDK_OK)
						nRet = ret1;
					else 
						nRet = STRIPE;
//				}else
//				{
//					// Poynt产品使用该接口
//					if((ret1=JniNdk.JNI_Mag_ReadDetect(err))!=NDK_OK)
//						nRet = ret1;
//					else 
//						nRet = STRIPE;
//				}
//				JniNdk.JNI_Mag_Reset();
				break;
			}
		}while(waittime==0||(diff = (int) Tools.getStopTime(oldTime))<waittime);
		
		if(waittime!=0&&diff>=waittime)
			nRet = NDK_ERR_TIMEOUT;
		switch (GlobalVariable.sdkType) 
		{
		case SDK2:
			break;
		case SDK3:
//			JniNdk.JNI_Sys_Delay (5);//延时0.5s , 开发梁璐说k21是100ms才进行一轮事件触发监听，故加200ms延时 by20180607 wangxy
			if (magFlag != EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue()) 
			{
				gui.cls_show_msg1_record(TAG, "readMag", g_keeptime, "line %d：没有监听到刷卡事件(%d)",Tools.getLineInfo(), magFlag);
				nRet = NDK_NO_LISTENER_MAG;
			}
			  magFlag = -1;
			break;
		default:
			return NDK_ERR;
		}
		if((ret1 = JniNdk.JNI_Mag_Close())!=NDK_OK)
			return ret1;
		
		return nRet;
	}
	// end by 20160913
	
	// 射频卡初始化
	public int rfidInit(_SMART_t type)
	{
		// 前置，事件机制注销事件，避免上次异常闪退时事件一直存在
		if (GlobalVariable.sdkType == SdkType.SDK3) 
			SmartUnRegistEvent(type);
		LoggerUtil.e("here");
		byte[]status=new byte[1];
		//初始化
		switch (type) 
		{
		case CPU_A:
		case MIFARE_0:
		case MIFARE_1:
		case MIFARE_0_C:
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);// 关闭射频场
			return JniNdk.JNI_Rfid_PiccType((byte) 0xcc);// M1卡操作时需要设置成Type_A模式
			
		case CPU_B:
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);// 关闭射频场
			return JniNdk.JNI_Rfid_PiccType((byte) 0xcb);
		
		case ISO15693:
			JniNdk.JNI_ISO15693_Deinit();//下电
			return JniNdk.JNI_ISO15693_init();
		case FELICA:
			JniNdk.JNI_Rfid_Init(status);
			return JniNdk.JNI_Rfid_PiccType((byte) 0xcf);
		default:
			break;
		}
		
		return NDK_OK;
	}
	
	// 射频卡检测
	public int rfid_detect(_SMART_t type,int[] UidLen,byte[] UidBuf1)
	{
		/*private & local definition*/
		int ret = -1;
		byte[] psPiccType = new byte[1];
		byte[] piccDetect = new byte[1];
		//byte[] felicauid=new byte[300];
		byte[] M0psSak=new byte[1];
		switch (GlobalVariable.sdkType) 
		{
		case SDK2:
			break;
		case SDK3:
//			if(type!=_SMART_t.MIFARE_0&&type!=_SMART_t.MIFARE_1&&type!=_SMART_t.ISO15693)
			if(type!=_SMART_t.ISO15693)
			{
				JniNdk.JNI_Sys_Delay(2);
				if (rfFlag != EM_SYS_EVENT.SYS_EVENT_RFID.getValue()) 
				{
					// gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "line%d:没有监听到rf事件(%d)",Tools.getLineInfo(), rfFlag);
					rfFlag = -1;
					return NDK_NO_LISTENER_RFID;
				}
				rfFlag = -1;
			}
			break;
		default:
			return -1;
		}
		
		switch (type) {
		case CPU_A:
		case CPU_B:
			if(GlobalVariable.sdkType==SdkType.SDK2) 
			{
//				if(type==_SMART_t.CPU_A)
//					JniNdk.JNI_Rfid_PiccType((byte) 0xcc);
//				else if(type==_SMART_t.CPU_B)
//					JniNdk.JNI_Rfid_PiccType((byte) 0xcb);
//				else if(type==_SMART_t.FELICA)
//					JniNdk.JNI_Rfid_PiccType((byte) 0xcf);
				switch (GlobalVariable.gCustomerID) 
				{
				case ChinaUms:
					// 该接口只有银商固件支持
					if((ret = JniNdk.JNI_Rfid_PiccDetectAtq(psPiccType, UidLen, UidBuf1))!=NDK_OK)
						return ret;
					// 显示获取到ATQA和ATQB的长度与内容
					gui.cls_show_msg1(1, "获取到%s卡的长度：%d，内容：%s", type,UidLen[0],ISOUtils.hexString(UidBuf1,2));
					break;

				default:// 非银商固件
					if((ret = JniNdk.JNI_Rfid_PiccDetect(piccDetect))!=NDK_OK)
						return ret;
					break;
				}
			}
			return SUCC;
		case FELICA:
			return SUCC;
		case MIFARE_0:
		case MIFARE_0_C:
				JniNdk.JNI_Rfid_PiccType((byte) 0xcc);//M0.M1.A卡的卡类型可以设置成0xcc
				byte[] pnUIDLen =new byte[1];// 开辟数组
				SystemClock.sleep(1);
				if((ret = JniNdk.JNI_MifareActive((byte)1,UidBuf1,pnUIDLen,M0psSak))!=NDK_OK)//MifareActive的接口集成了寻卡、防碰撞、选卡功能.
				{
					gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s寻卡失败123(%d)",  Tools.getLineInfo(),type,ret);
					return ret;
				}
				return SUCC;
				
		case MIFARE_1:
			int[] pnDataLen = new int[1];
			byte[] psDataBuf = new byte[20];
			JniNdk.JNI_Rfid_PiccType((byte) 0xcc);// 设置卡类型
			if((ret = JniNdk.JNI_Rfid_M1Request((byte)1, pnDataLen, psDataBuf))!=NDK_OK)
			{
//				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s寻卡失败(%d)", TAG, Tools.getLineInfo(),type,ret);
				return ret;
			}
			LoggerUtil.d("JNI_Rfid_M1Request:"+pnDataLen[0]+"==psDataBuf:"+ISOUtils.hexString(psDataBuf));
			if((ret = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf))!=NDK_OK)
			{
//				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s防冲突失败(%d)", TAG, Tools.getLineInfo(),type,ret);
				return ret;
			}
			LoggerUtil.d("JNI_Rfid_M1Anti:"+pnDataLen[0]+"==psDataBuf:"+ISOUtils.hexString(psDataBuf));
			byte[] UidBuf = new byte[pnDataLen[0]];
			byte[] psSakBuf = new byte[1];
			System.arraycopy(psDataBuf, 0, UidBuf, 0, pnDataLen[0]);
			System.arraycopy(psDataBuf, 0, UidBuf1, 0, pnDataLen[0]);
			UidLen[0] = pnDataLen[0];
			if((ret = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], UidBuf, psSakBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s寻卡失败(%d)", Tools.getLineInfo(),type,ret);
				return ret;
			}
			return SUCC;
		case ISO15693:
			//初始化
			if((ret=JniNdk.JNI_ISO15693_init())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),type,ret);
				return ret;
			}
			
			return SUCC;

		default:
			return FAIL;
		}
	}
/**
去除rfidActive_SDK2方法   20200703

	// add by 20150305
	// 非接寻卡的上电寻卡操作
	public int rfidActive_SDK2(_SMART_t type,int felicaChoose,int[] UidLen,byte[] UidBuf)
	{
	
		int ret = -1;
		byte[] psPiccType = new byte[1];
		byte[] seckey={0x49,0x45,0x4D,0x4B,0x41,0x45,0x52,0x42,0x21,0x4E,0x41,0x43,0x55,0x4F,0x59,0x46};
		switch (type) 
		{
		case CPU_A:
			if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, UidLen, UidBuf))!=NDK_OK||(psPiccType[0]!=(byte)0xCC))
			{
//				gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d,%s)", TAG,Tools.getLineInfo(),type,ret,ISOUtils.hexString(psPiccType));
				return (ret!=NDK_OK)?ret:psPiccType[0];
			}
			return SUCC;
			
		case CPU_B:
			if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, UidLen, UidBuf))!=NDK_OK||(psPiccType[0]!=(byte)0xCB))
			{
//				gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d,%d)", TAG,Tools.getLineInfo(),type,ret,ISOUtils.hexString(psPiccType));
				return (ret!=NDK_OK)?ret:psPiccType[0];
			}
			return SUCC;
		case MIFARE_0:
			return JniNdk.JNI_Rfid_M0Authen(seckey);//这里测试的是3次相互认证的接口,MifareActive接口在上面验证过了
		case FELICA:
			//普通felica
			FelicaParam felicaParam=new FelicaParam();
			if(felicaChoose==0){
				byte[] systemcode=new byte[2];
				systemcode[0]=(byte) 0xff;
				systemcode[1]=(byte) 0xff;
				felicaParam.jni_requset_code=0x01;
				felicaParam.jni_timeslot=0x00;
				felicaParam.jni_systemcode=systemcode;
			}else if(felicaChoose==1){
				byte[] systemcode2=new byte[2];
				systemcode2[0]=(byte) 0x80;
				systemcode2[1]=(byte) 0x08;
				felicaParam.jni_requset_code=0x01;
				felicaParam.jni_timeslot=0x00;
				felicaParam.jni_systemcode=systemcode2;
			}
			
			ret = JniNdk.JNI_FelicaPoll(felicaParam,UidBuf,UidLen);
			if(ret!=NDK_OK){
				if(ret==-10000){
					if((ret = JniNdk.JNI_RfidFelicaPoll(UidBuf,UidLen))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d)", TAG,Tools.getLineInfo(),type,ret);
						return ret;
					}
				}
				else
				{
					gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d)", TAG,Tools.getLineInfo(),type,ret);
					return ret;
				}
			}
			else{
				//返回成功后要校验
				if(UidBuf[1]!=0x01){
					gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s数据校验失败，激活失败(%s)", TAG,Tools.getLineInfo(),type,UidBuf[1]);
					return ret;
				}
			}
			return SUCC;
			
		case MIFARE_1:
			return JniNdk.JNI_Rfid_M1ExternalAuthen(UidLen[0], UidBuf, (byte)0x61, AUTHKEY, (byte)0x01);
		
		case ISO15693:
			//防冲突
			byte slotcnt = 0;//0代表ISO15693_NUM_SLOTS_1   1代表ISO15693_NUM_SLOTS_16
			byte maxCards = (byte)Mcards.length;
			byte[] cardCount = new byte[1];//获取到有效uid的卡张数,大于=0
			
			if((ret=JniNdk.JNI_ISO15693_Inventory(slotcnt, maxCards, cardCount, Mcards))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s防冲突失败(%d)", Tools.getLineInfo(),type,ret);
				return ret;
			}
			//选卡
			if(cardCount[0]>0){
				if((ret=JniNdk.JNI_iso15693SelectPicc(Mcards[0]))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s选卡失败(%d)", Tools.getLineInfo(),type,ret);
					return ret;
				}
				// 获取卡信息
				if((ret=JniNdk.JNI_iso15693GetPicc_SystemInfo(Mcards[0], sysInfo))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s获取卡信息失败(%d)", Tools.getLineInfo(),type,ret);
					return ret;
				}
			}else{
				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%未寻到卡", Tools.getLineInfo(),type);
				return FAIL;
			}
			
			return SUCC;
		
		default:
			break;
		}
		return FAIL;
	}
    */
	// add by 20150305
	// 非接寻卡的上电寻卡操作
	public int rfidActive(_SMART_t type,int felicaChoose,int[] UidLen,byte[] UidBuf)
	{
		/*private & local definition*/
		int ret = -1;
		byte[] psPiccType = new byte[1];
		byte[] M0psSak=new byte[1];
//		byte[] seckey={0x49,0x45,0x4D,0x4B,0x41,0x45,0x52,0x42,0x21,0x4E,0x41,0x43,0x55,0x4,0x59,0x46};
//		byte[] uidBuf = new byte[8];
//		System.arraycopy(UidBuf, 0, uidBuf, 0, UidLen[0]);
		switch (type) 
		{
		case CPU_A:
			if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, UidLen, UidBuf))!=NDK_OK||(psPiccType[0]!=(byte)0xCC))
			{
//				gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d,%s)", TAG,Tools.getLineInfo(),type,ret,ISOUtils.hexString(psPiccType));
				return (ret!=NDK_OK)?ret:psPiccType[0];
			}
			return SUCC;
			
		case CPU_B:
			if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, UidLen, UidBuf))!=NDK_OK||(psPiccType[0]!=(byte)0xCB))
			{
//				gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d,%d)", TAG,Tools.getLineInfo(),type,ret,ISOUtils.hexString(psPiccType));
				return (ret!=NDK_OK)?ret:psPiccType[0];
			}
			return SUCC;

		case MIFARE_1:
			//由于不知道4k卡的B密钥是多少,使用A密钥(经测试A密钥为全0xff)
			Log.d("eric", "MIFARE_1------");
			return JniNdk.JNI_Rfid_M1ExternalAuthen(UidLen[0], UidBuf, AUTHKEY_TYPE_A, AUTHKEY, (byte)0x01);
		case FELICA:
			//普通felica
			FelicaParam felicaParam=new FelicaParam();
			if(felicaChoose==0){
				byte[] systemcode=new byte[2];
				systemcode[0]=(byte) 0xff;
				systemcode[1]=(byte) 0xff;
				felicaParam.jni_requset_code=0x01;
				felicaParam.jni_timeslot=0x00;
				felicaParam.jni_systemcode=systemcode;
			}else if(felicaChoose==1){
				byte[] systemcode2=new byte[2];
				systemcode2[0]=(byte) 0x80;
				systemcode2[1]=(byte) 0x08;
				felicaParam.jni_requset_code=0x01;
				felicaParam.jni_timeslot=0x00;
				felicaParam.jni_systemcode=systemcode2;
			}
			
			ret = JniNdk.JNI_FelicaPoll(felicaParam,UidBuf,UidLen);
			if(ret!=NDK_OK){
				if(ret==-10000){
					if((ret = JniNdk.JNI_RfidFelicaPoll(UidBuf,UidLen))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d)", TAG,Tools.getLineInfo(),type,ret);
						return ret;
					}
					
				}
				else
				{
					gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s激活失败(%d)", TAG,Tools.getLineInfo(),type,ret);
					return ret;
				}
			}
			else{
				//返回成功后要校验
				if(UidBuf[1]!=0x01){
					gui.cls_show_msg1_record(TAG, "rfidActive", g_keeptime, "%s,line %d:%s数据校验失败，激活失败(%s)", TAG,Tools.getLineInfo(),type,UidBuf[1]);
					return ret;
				}
			}
			return SUCC;
		//M0带认证
		case MIFARE_0_C:
			byte[] seckey={0x49,0x45,0x4D,0x4B,0x41,0x45,0x52,0x42,0x21,0x4E,0x41,0x43,0x55,0x4F,0x59,0x46}; //M0认证用
			
			SystemClock.sleep(1);
		   if ((ret=JniNdk.JNI_Rfid_M0Authen(seckey))!=NDK_OK) {
			   gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s认证失败(%d)", Tools.getLineInfo(),type,ret);
			   return ret;
		}	
			SystemClock.sleep(1);
			return SUCC;
			
		case MIFARE_0:
//			Log.d("测试222。。。。。", type+"");
//			JniNdk.JNI_Rfid_PiccType((byte) 0xcc);//M0.M1.A卡的卡类型可以设置成0xcc
//			byte[] pnUIDLen =new byte[1];// 开辟数组
//		if((ret = JniNdk.JNI_MifareActive((byte)1,UidBuf,pnUIDLen,M0psSak))!=NDK_OK)//MifareActive的接口集成了寻卡、防碰撞、选卡功能.
//			{
//			gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s寻卡失败123(%d)",  Tools.getLineInfo(),type,ret);
//				return ret;
//			}
//			Log.d("打印数据M0", "UidBuf==="+UidBuf+"pnUIDLen===="+pnUIDLen);
			
			return SUCC;
		
		case ISO15693:
			//防冲突
			byte slotcnt = 0;//0代表ISO15693_NUM_SLOTS_1   1代表ISO15693_NUM_SLOTS_16
			byte maxCards = (byte)Mcards.length;
			byte[] cardCount = new byte[1];//获取到有效uid的卡张数,大于=0
			
			if((ret=JniNdk.JNI_ISO15693_Inventory(slotcnt, maxCards, cardCount, Mcards))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s防冲突失败(%d)", Tools.getLineInfo(),type,ret);
				return ret;
			}
			//选卡
			if(cardCount[0]>0){
				if((ret=JniNdk.JNI_iso15693SelectPicc(Mcards[0]))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s选卡失败(%d)", Tools.getLineInfo(),type,ret);
					return ret;
				}
				// 获取卡信息
				if((ret=JniNdk.JNI_iso15693GetPicc_SystemInfo(Mcards[0], sysInfo))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s获取卡信息失败(%d)", Tools.getLineInfo(),type,ret);
					return ret;
				}
			}else{
				gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%未寻到卡", Tools.getLineInfo(),type);
				return FAIL;
			}
			
			return SUCC;
		
		default:
			break;
		}
		return FAIL;
	}
	// end by 20150305
	
	// add by 20150305
	// apdu的读写
	public int rfidApduRw(_SMART_t type,int[] cpuRecvLen,byte[] uid) 
	{
		/*private & local definition*/
		int ret = -1;
		byte[] respCode = new byte[2];
		byte[] psRecvBuf = new byte[300];
		byte[] select_1pay = {0x00,(byte) 0x84,0x00,0x00,0x08};
//		byte[] DATA16 = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
		int[] revlen = new int[1];
		byte[] out=new byte[256];
		byte[] send_buf=new byte[16];
		byte[] rec_buf=new byte[300];
		byte[] psrec=new byte[8];
//		byte[] psrec2={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01};//send_buf从角标2-10的数据
//		send_buf[0]=16;
//		send_buf[10]=0x01;
		byte[] M0recdata=new byte[16];
		int [] M0writelen=new int[1];
//		byte[] M0SendData={0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
//		byte[] seckey={0x49,0x45,0x4D,0x4B,0x41,0x45,0x52,0x42,0x21,0x4E,0x41,0x43,0x55,0x4F,0x59,0x46}; //M0认证用
		/*process body*/
		switch (type) 
		{
		case CPU_A:
		case CPU_B:
			ret = JniNdk.JNI_Rfid_PiccApdu(select_1pay.length, select_1pay, cpuRecvLen, psRecvBuf);
			LoggerUtil.e("psRecvBuf:"+ISOUtils.hexString(psRecvBuf));
			// 若支持取随机数的话最后两个字节才是返回码 by 20170913
			if(ret == NDK_OK)
				System.arraycopy(psRecvBuf, cpuRecvLen[0]-2, respCode, 0, 2);
			// 支持随机数和不支持取随机数的卡都能测试通过
			if(ret !=NDK_OK||Tools.memcmp(respCode, new byte[]{(byte)0x90, 0x00},2)==false&&(respCode[0]&0x60)!=0x60)
			{
				// NDK_RfidPiccApdu apdu失败时将驱动返回的错误码通过psRecebuf返回给应用层 by 20200327
				ret = psRecvBuf[0];
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s读写失败(%d,%s)", TAG,Tools.getLineInfo(),type,ret,ISOUtils.hexString(respCode));
				return (ret!=NDK_OK)?ret:FAIL;
			}
			apduLen = cpuRecvLen[0];
			return SUCC;
		case FELICA:
			send_buf[0] = 16;
			send_buf[1] = 0x06;
			for(int i=2,j=2;i<10;i++,j++){
				send_buf[j]=uid[i];
			}
//			memcpy(&sendbuf[2], &UID[2], 8);
			send_buf[10] = 0x01;
			send_buf[11] = 0x09;
			send_buf[12] = 0x00;
			send_buf[13] = 0x01;
			send_buf[14] = (byte) 0x80;
			send_buf[15] = 0x00;
			
			if((ret=JniNdk.JNI_RfidFelicaApdu(send_buf.length,send_buf,cpuRecvLen,rec_buf))!=NDK_OK || rec_buf[0]!=cpuRecvLen[0])//判断
			{
//				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s读写失败(%d,%s)", TAG,Tools.getLineInfo(),type,ret,ISOUtils.hexString(psrec));
				return FAIL;
			}else{
				System.arraycopy(rec_buf, 2, psrec, 0, 8);	//将接收的rec_buf从角标2-10的位置复制到psrec
				if(Tools.memcmp(psrec,send_buf,8) || rec_buf[1]!=0x07){
//					gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s读写失败(%s)", TAG,Tools.getLineInfo(),type, rec_buf[1]);
					return FAIL;
				}
			}
			LoggerUtil.e("Felica:recLen:"+cpuRecvLen[0]+"rec_buf:"+Arrays.toString(rec_buf));//将接收到的rec_buf输出
			apduLen = cpuRecvLen[0];
			return SUCC;
		case MIFARE_1:
			// 之前认证的块为01，应该01-03块都可进行读写操作
			if((ret = JniNdk.JNI_Rfid_M1Read((byte)0x01, revlen, out))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s块读失败(%d)", TAG,Tools.getLineInfo(),type,ret);
				return ret;
			}
			Log.d("readLen", revlen[0]+"");
			if(Tools.memcmp(DATA16, out, revlen[0])==false)// 首次
				System.arraycopy(out, 0, DATA16, 0, revlen[0]);
			else if(Tools.memcmp(DATA16, out, revlen[0])==false)
			{
//				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s数据校验失败(%d)", TAG,Tools.getLineInfo(),type,revlen[0]);
				return ret;
			}
			return SUCC;
		case MIFARE_0:
		case MIFARE_0_C:
//			if((ret = JniNdk.JNI_Rfid_M0Write((byte)12, M0writelen, M0SendData))!=NDK_OK)//向第四页写入长度为16的数据,测试新增的write接口
//			{
//				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s卡写失败(%d)", TAG,Tools.getLineInfo(),type,ret);
//				return ret;
//			}
			if((ret = JniNdk.JNI_Rfid_M0Read((byte)12, M0writelen, M0recdata))!=NDK_OK)//读取长度为16的数据,测试新增的read接口
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s卡读失败(%d)", TAG,Tools.getLineInfo(),type,ret);
				return ret;
			}
			Log.d("m0读写测试。。。。。。。。", "M0writelen===="+M0writelen.toString()+"M0recdata======"+M0recdata.toString());
			
			if(Tools.memcmp(DATA16, M0recdata, M0writelen[0])==false)// 首次
			{
				System.arraycopy(M0recdata, 0, DATA16, 0, M0writelen[0]);
				Log.d("m0读写测试2222。。。。。。。。", "M0recdata===="+M0recdata.toString()+"M0writelen======"+M0writelen[0]);
			}
			else if(Tools.memcmp(DATA16, M0recdata, M0writelen[0])==false)
			{
				Log.d("m0读写测试333。。。。。。。。", "M0recdata===="+M0recdata.toString()+"M0writelen======"+M0writelen[0]);
				return ret;
			}
			
			apduLen = M0writelen[0];
			Log.d("apduLen====", "apduLen===="+apduLen);
			return SUCC;
		case ISO15693:
			// 写单块，不超过4字节，0x00-0xff
			int ucBlocknum = random.nextInt(Integer.valueOf(sysInfo[0].memNumBlocks));// 随机取块号，从0开始
//			for (int i = 0; i < data_NDK.length; i++) 
//				data_NDK[i]=(byte)random.nextInt(16);
			memBlock.memBlock_data(data_NDK);
			memBlock.actualSize =(char) data_NDK.length;
			
			if ((ret = JniNdk.JNI_iso15693Write_SingleBlock(Mcards[0], memBlock, ucBlocknum)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s单块写失败(%d)", TAG,Tools.getLineInfo(),type,ret);
				return ret;
			}
			// 读单块
			ISO15693MemBlock[] memBlock1 = new ISO15693MemBlock[1];
			if ((ret = JniNdk.JNI_iso15693Read_SingleBlock(Mcards[0], ucBlocknum, memBlock1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s单块读失败(%d)", TAG,Tools.getLineInfo(),type,ret);
				return ret;
			}
//			for (int j = 0; j < sysInfo[0].uid.length; j++) 
//				Log.v("wangxiaoyu",String.format("j=%d,memBlock1[0].data=%02x", j,memBlock1[0].data[j]));
			
			//数据校验
			if(!Tools.memcmp(data_NDK, memBlock1[0].data, data_NDK.length))
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s比较数据失败(预期=%s，实际=%s)", TAG,Tools.getLineInfo(),type,new String(data_NDK),new String(memBlock1[0].data));
				return FAIL;
			}
			apduLen=data_NDK.length;
			// 读多块
			int startblock = 0;
			int icount = 2;
			ISO15693MemBlock[] memBlock2 = new ISO15693MemBlock[2];
			if ((ret = JniNdk.JNI_iso15693Read_MultipleBlocks(Mcards[0], startblock, icount, memBlock2)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "rfidApduRw", g_keeptime, "%s,line %d:%s多块读失败(%d)", TAG,Tools.getLineInfo(),type,ret);
				return ret;
			}
			return SUCC;
			
		default:
			return FAIL;
		}
	}
	// end by 20150305
	
	/*// add by 20151013
	// 进行增量/减量操作
	public int rfidOperation()
	{
		int ret = -1;
		byte[]	writeData = {0x67,0x45,0x23,0x01,(byte)0x98,(byte)0xBA,(byte)0xDC,(byte)0xFE,0x67,0x45,0x23,0x01,0x02,(byte)0xFD,0x02,(byte)0xFD};
		byte[] readData = new byte[16];
		byte[] data = {0x01,0x02,0x03,0x04};
		
		try 
		{
			// 写入数据
			qpCard.writeDataBlock(2, writeData);
			// 获取原先的块数据
			qpCard.incrementOperation(2,data);
			// 读数据
			readData = qpCard.readDataBlock(2);
			for (int i = 0; i < 4; i++) 
			{
				if (readData[i] != writeData[i+8]+data[i]) 
				{
					ret = NDK_ERR;
					return ret;
				}
			}
			// 进行减量操作
			qpCard.decrementOperation(2, data);
			// 读数据
			readData = qpCard.readDataBlock(2);
			for (int i = 0; i < 4; i++) 
			{
				if (readData[i] != writeData[i+8]) 
				{
					ret = NDK_ERR;
					return ret;
				}
			}
			ret = NDK_OK;
			
			
		} catch (DeviceRTException e) 
		{
			ret = e.getCode();
		}
		return ret;
		
	}
	// end by 20151013
*/	
	// add by 20150305
	// 下电操作
	public int rfidDeactive(_SMART_t type,int ucDelayMs)
	{
		/*private & local definition*/
		/*process body*/
		switch (type) {
		case CPU_A:
		case CPU_B:
		case FELICA:
		case MIFARE_1:
		case MIFARE_0:
		case MIFARE_0_C:
			return JniNdk.JNI_Rfid_PiccDeactivate((byte) ucDelayMs);
			
		case ISO15693:
			return JniNdk.JNI_ISO15693_Deinit();
			
		default:
			return FAIL;
		}
	}
	// end by 20150305
	
	//add by 20150317
	// smart激活
	public int smartInit(_SMART_t type) 
	{
		int ret=-1;
 
		switch (type) 
		{
		case CPU_A:
		case CPU_B:
		case MIFARE_1:
		case ISO15693:
		case FELICA:
		case MIFARE_0:
		case MIFARE_0_C:
			ret = rfidInit(type);
			break;
			
		case SAM1:
		case SAM2:
			ret = NDK_OK;
			break;
			
		case IC:
			// 前置，事件机制注销事件，避免上次异常闪退时事件一直存在
			if (GlobalVariable.sdkType == SdkType.SDK3)
				SmartUnRegistEvent(type);
			ret = NDK_OK;
			break;
			
		default:
			return NDK_ERR;
		}
		return ret;
	}
	
	public int smart_detect(_SMART_t type,int[] UidLen,byte[] UidBuf)
	{
		int ret=-1;
		switch (type) {
		case CPU_A:
		case CPU_B:
		case MIFARE_1:
		case ISO15693:
		case MIFARE_0:
		case FELICA:
		case MIFARE_0_C:
			return rfid_detect(type, UidLen, UidBuf);
		
		case SAM1:
			return ret = iccDetect(EM_ICTYPE.ICTYPE_SAM1);
		case SAM2:
			return ret = iccDetect(EM_ICTYPE.ICTYPE_SAM2);
		case IC:
			return ret = iccDetect(EM_ICTYPE.ICTYPE_IC);

		default:
			return FAIL;
		}
	}
	
	//add by 20150317
	// smart激活
	public int smartActive_SDK2(_SMART_t type,int felicaChoose, int[] UidLen, byte[] UidBuf) 
	{
		byte[] atr = new byte[300];
		int[] len = new int[1];
		int ret;
		/* process body */
		switch (type) {
		case CPU_A:
		case CPU_B:
		case MIFARE_1:
		case ISO15693:
		case FELICA:
		case MIFARE_0:
		case MIFARE_0_C:
			ret = rfidActive(type, felicaChoose,UidLen, UidBuf);
			break;

		case SAM1:
			ret = iccPowerOn_SDK2(EM_ICTYPE.ICTYPE_SAM1, atr, len);
			break;

		case SAM2:
			ret = iccPowerOn_SDK2(EM_ICTYPE.ICTYPE_SAM2, atr, len);
			break;
		case IC:
			ret = iccPowerOn_SDK2(EM_ICTYPE.ICTYPE_IC, atr, len);
			break;

		default:
			return NDK_ERR;
		}
		return ret;
	}
	
	//add by 20150317
	// smart激活
	public int smartActive(_SMART_t type,int felicaChoose,int[] UidLen,byte[] UidBuf) 
	{
		byte[] atr = new byte[300];
		int[] len = new int[1];
		int ret;
		/*process body*/
		switch (type) 
		{
		case CPU_A:
		case CPU_B:
		case MIFARE_1:
		case ISO15693:
		case FELICA:
		case MIFARE_0:
		case MIFARE_0_C:
			ret = rfidActive(type,felicaChoose,UidLen,UidBuf);
			break;
			
		case SAM1:
			ret = iccPowerOn(EM_ICTYPE.ICTYPE_SAM1, atr, len);
			break;
			
		case SAM2:
			ret = iccPowerOn(EM_ICTYPE.ICTYPE_SAM2, atr, len);
			break;
		case IC:
			ret = iccPowerOn(EM_ICTYPE.ICTYPE_IC, atr, len);
			break;
			
		default:
			return NDK_ERR;
		}
		return ret;
	}

	// smart apdu的读写
	public int smartApduRw(_SMART_t type,byte[] req,byte[] uid) 
	{
		int ret;
		byte[] resultCode = new byte[2];
		int[] cpurevLen = new int[1];
		
		/*process body*/
		switch (type) 
		{
		case CPU_A:
		case CPU_B:
		case MIFARE_1:
		case ISO15693:
		case MIFARE_0:
		case FELICA:
		case MIFARE_0_C:
			ret = rfidApduRw(type,cpurevLen,uid);
			break;
			
		case SAM1:
			ret = iccRw(EM_ICTYPE.ICTYPE_SAM1,req,resultCode);
			break;
		case SAM2:
			ret = iccRw(EM_ICTYPE.ICTYPE_SAM2,req,resultCode);
			break;
		case IC:
			ret = iccRw(EM_ICTYPE.ICTYPE_IC,req,resultCode);
			break;
			
		default:
			return NDK_ERR;
		}
		return ret;
	}
	
	// 下电操作
	public int smartDeactive(_SMART_t type)
	{
		int ret = -1;
		
		/*process body*/
		switch (type) 
		{
		case CPU_A:
		case CPU_B:
		case MIFARE_1:
		case ISO15693:
		case FELICA:
		case MIFARE_0:
		case MIFARE_0_C:
			ret = rfidDeactive(type, (byte)0);
			break;
			
		case SAM1:
			ret = icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1);
			break;
		case SAM2:
			ret = icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2);
			break;
		case IC:
			ret = icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			break;
			
		default:
			return NDK_ERR;
		}
		return ret;
	}
	//end by 20150317
	
	public int iccDetect(EM_ICTYPE emIcType)
	{
		int ret = -1;
		int[] pnSta = new int[1];
		if((ret = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK)
			return ret;
		return ret;
	}
	
	// ic/sam卡上电
	public int iccPowerOn(EM_ICTYPE emIcType,byte[] psAtrBuf,int[] pnAtrLen)
	{
		int iRet=-1;
		switch(GlobalVariable.sdkType)
		{
		case SDK2:
			break;
		case SDK3:
			if(emIcType == EM_ICTYPE.ICTYPE_IC)
			{
				JniNdk.JNI_Sys_Delay(3);// 停留0.3s
				if (iccFlag != EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue()) 
				{
					//gui.cls_show_msg1_record(TAG, "iccPowerOn", g_keeptime, "line %d:没有监听到icc事件(%d)",Tools.getLineInfo(), iccFlag);
					iccFlag = -1;					
					return NDK_NO_LISTENER_ICC;
				}
				iccFlag = -1;	
			}
			break;
		default:
			return NDK_ERR;
		}
		  iRet = JniNdk.JNI_Icc_PowerUp(emIcType.ordinal(), psAtrBuf, pnAtrLen);
		return iRet;
	}
	
	
	// ic/sam卡上电
	public int iccPowerOn_SDK2(EM_ICTYPE emIcType,byte[] psAtrBuf,int[] pnAtrLen)
	{
		int iRet=-1;
		 iRet = JniNdk.JNI_Icc_PowerUp(emIcType.ordinal(), psAtrBuf, pnAtrLen);
		return iRet;
	}
	
	
	
	public int iccRw(EM_ICTYPE emIcType,byte[] psSendBuf,byte[] resultCode)
	{
		/*private & local definition*/
		int iRet = -1;
		byte[] mPsRecvBuf = new byte[10];
		int[] mPnRecvLen = new int[1];
		
		/*process body*/
		Arrays.fill(mPsRecvBuf, (byte) 0x00);
		Arrays.fill(mPnRecvLen, 0x00);
		if(resultCode!=null)
			Arrays.fill(resultCode, (byte) 0x00);
		//systest57、systest75也有JNI_Icc_Rw 但没有改
		/**Poynt产品也支持rw接口20200609*/
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false){
			iRet = JniNdk.JNI_Icc_Rw(emIcType.ordinal(), psSendBuf.length, psSendBuf, mPnRecvLen, mPsRecvBuf);
//		}
//		else{
//			iRet = JniNdk.JNI_Icc_getRandom(emIcType.ordinal(), mPnRecvLen, mPsRecvBuf);
//		}
		apduLen = mPnRecvLen[0];
		
		// 取的返回码
		if(resultCode!=null)
		{
			if(iRet == NDK_OK)
				System.arraycopy(mPsRecvBuf, apduLen-2, resultCode, 0, 2);
			// 粗略判断指令返回值
			if(iRet !=NDK_OK||Tools.memcmp(resultCode, new byte[]{(byte)0x90, 0x00},2)==false&&(resultCode[0]&0x60)!=0x60)
				return (iRet!=NDK_OK)?iRet:FAIL;
		}

		return iRet;
	}
	
	// ic/sam下电
	public int icSamPowerOff(EM_ICTYPE emIcType)
	{
		int iRet =-1;
		iRet = JniNdk.JNI_Icc_PowerDown(emIcType.ordinal());
		return iRet;
	}
	
	@SuppressLint("DefaultLocale")
	public void createPacket(PacketBean packet,byte[] buf)
	{
		/*private & local definition*/

		/*process body*/
		packet.setHeader(buf);
		packet.setLen(PACKMAXLEN);
		packet.setOrig_len(PACKMAXLEN);
		packet.setLifecycle(ABILITY_VALUE);
		packet.setForever(false);
		packet.setIsLenRec(false);
		packet.setIsDataRnd(true);
		
		return;
	}
	
	// 单向发送
	@SuppressLint("DefaultLocale")
	public void sendOnlyPress(SocketUtil socketUtil,LinkType type,NetWorkingBase netWorkingBase)
	{
		/*private & local definition*/
		int i=0,startLen;
		int send_succ = 0,slen = 0;
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/
		set_snd_packet(sendPacket,type);
		startLen = sendPacket.getLen();
		
		while(true)
		{
			if(gui.cls_show_msg2(0.1f, "开始第%d次通讯测试(已成功%d次),[取消]键退出",i+1 ,send_succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type) != NDK_OK)
				break;
			i++;
			// 发送数据
			if((slen = sockSend(socketUtil,sendPacket.getHeader(), startLen, SO_TIMEO,netWorkingBase))!= startLen)
			{
				gui.cls_show_msg1_record(TAG, "sendOnlyPress", g_keeptime, "line %d:第%d次发送数据失败(实际len = %d,预期len = %d)", Tools.getLineInfo(),i,slen,startLen);
				continue;
			}
			SystemClock.sleep(2000);
			send_succ++;
		}
		gui.cls_show_msg1_record(TAG, "sendOnlyPress", g_time_0,"单向发送通讯总次数%d,成功次数%d", i,send_succ);
	}
	
	/**
	 *  单向接收压力
	 * @param socketUtil
	 * @param type
	 * @param netWorkingBase
	 */
	public void recvOnlyPress(SocketUtil socketUtil,LinkType type,NetWorkingBase netWorkingBase) 
	{
		/*private & local definition*/
		int i=0,startLen;
		int rec_succ = 0,rlen = 0,slen;
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/
		set_snd_packet(sendPacket,type);
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		byte[] buf = new byte[startLen];
		
		System.arraycopy(sendPacket.getHeader(), 0, buf, 0, startLen);
		
		/*process body*/
		// 发送数据
		if((slen = sockSend(socketUtil,buf, startLen, SO_TIMEO,netWorkingBase))!= startLen)
		{
			gui.cls_show_msg1_record(TAG, "recvOnlyPress", g_keeptime, "line %d:发送数据失败(实际%d,预期%d)", Tools.getLineInfo(),slen,startLen);
			return;
		}
		int time=sendPacket.getLifecycle();
		while(true)
		{
			if(gui.cls_show_msg2(0.1f, "开始第%d次通讯测试(已成功%d次),[取消]键退出测试...", i+1,rec_succ)==ESC)
				break;
			//接收的不需要更新数据，服务器只会一直返回第一次发送的数据
//			if(update_snd_packet(sendPacket, type) != NDK_OK)
//				break;
			if(i>time){
				break;
			}
			i++;
			
			// 接收数据
			if((rlen = sockRecv(socketUtil,rbuf, startLen, SO_TIMEO,netWorkingBase)) != startLen)
			{
				gui.cls_show_msg1_record(TAG, "recvOnlyPress", g_keeptime,"line %d:第%d次接收失败(实际len=%d,预期len = %d)",Tools.getLineInfo(),i,rlen,startLen);
				continue;
			}
			
			
			LoggerUtil.v(TAG+",recvOnlyPress==="+ISOUtils.hexString(rbuf));
			LoggerUtil.v(TAG+",recvOnlyPress==="+ISOUtils.hexString(sendPacket.getHeader()));
			// 比较收发
			if(!Tools.memcmp(buf, rbuf, startLen))
			{
				gui.cls_show_msg1_record(TAG, "recvOnlyPress",g_keeptime, "line %d:第%d次:收发数据校验失败", Tools.getLineInfo(),i);
				continue;
			}
			rec_succ++;
		}
		
		gui.cls_show_msg1_record(TAG, "recvOnlyPress",g_time_0, "单向接收通讯总次数%d，成功次数%d", i,rec_succ);
	}
	
	/**
	 *  通讯数据收发压力  双向通过到
	 * @param socketUtil
	 * @param type
	 * @param netWorkingBase
	 */
	protected void send_recv_press(SocketUtil socketUtil,LinkType type,NetWorkingBase netWorkingBase)  
	{
		/*private & local definition*/
		byte[] buf = new byte[PACKMAXLEN];
		int i = 0,comm_succ_count = 0,timeout = SO_TIMEO;
		int slen = 0,rlen = 0,startLen = 0;
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
		//startLen在while循环外。导致数据递增的情况下。startLen只赋值一次。这里改为全部用getLen方法  20200813
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		while(true)
		{
			if(gui.cls_show_msg2(0.1f, "开始第%d次通讯测试(已成功%d次),[取消]退出测试",i+1,comm_succ_count)==ESC)
				break;
			if(update_snd_packet(sendPacket,type)!=NDK_OK)
				break;
			i++;
			// 发送数据
			if((slen = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,netWorkingBase))!= startLen)
			{
				gui.cls_show_msg1_record(TAG, "sen_recv_press", g_keeptime,"line %d:第%d次发送数据失败(实际len = %d,预期len = %d)", Tools.getLineInfo(),i,slen,startLen);
				// 重连socket
				Layer.transStatus = TransStatus.TRANSDOWN;
				new Layer(myactivity,handler).transUp(socketUtil, netWorkingBase.getSock_t());
				continue;
					
			}
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil,rbuf,sendPacket.getLen(), SO_TIMEO,netWorkingBase))!= startLen)
			{
				gui.cls_show_msg1_record(TAG, "sen_recv_press",g_keeptime, "line %d:第%d次接收数据失败(实际%d,预期%d)", Tools.getLineInfo(),i,rlen,startLen);
				continue;
			}
			// 比较数据
			if(Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())==false)
			{
				gui.cls_show_msg1_record(TAG, "sen_recv_press", g_keeptime,"line %d:第%d次检验收发数据失败", Tools.getLineInfo(),i);
				continue;
			}
			comm_succ_count++;
		}
		// 需要记录在测试报告，产线人员建立
		gui.cls_show_msg1_record(TAG,"sen_recv_press",g_time_0,"双向通讯总次数%d，成功次数%d", i,comm_succ_count);
	}
	
	/**
	 * 数据发送操作
	 * @param socketUtil
	 * @param buf
	 * @param len
	 * @param timeout
	 * @param netWorkingBase
	 * @return			发送的数据长度
	 */
	public int sockSend(SocketUtil socketUtil,byte[] buf,int len,int timeout,NetWorkingBase netWorkingBase)
	{
		/*private & local definition*/
		String ip = netWorkingBase.getServerIp();
		int port = netWorkingBase.getServerPort();
		
		/*process body*/
		// 目前的IP地址需要修改
		gui.cls_printf(String.format("数据发送中...\n%s:%d->%s:%d\n", netWorkingBase.getLocalIp(),netWorkingBase.getLocalPort(),ip,port).getBytes());
		
		return sockSendNoGui(socketUtil,buf,len,timeout,netWorkingBase);
	}

	/**
	 * 接收数据操作
	 * @param socketUtil
	 * @param rbuf
	 * @param len
	 * @param timeout
	 * @param netWorkingBase
	 * @return
	 */
	public int sockRecv(SocketUtil socketUtil,byte[] rbuf,int len,int timeout,NetWorkingBase netWorkingBase)
	{
	
		String ip = netWorkingBase.getServerIp();
		String localIp = netWorkingBase.getLocalIp();
		int serPort = netWorkingBase.getServerPort();
		int localPort = netWorkingBase.getLocalPort();
		/*process body*/
		gui.cls_printf(String.format("数据接收中...\n%s:%d<-%s:%d\n", localIp,localPort,ip,serPort).getBytes());
		
		return sockRecvNoGui(socketUtil,rbuf,len,timeout,netWorkingBase);
	}
	/**
	 * 数据发送操作 去掉gui显示
	 * @param socketUtil
	 * @param buf
	 * @param len
	 * @param timeout
	 * @param netWorkingBase
	 * @return			发送的数据长度
	 */
	public int sockSendNoGui(SocketUtil socketUtil,byte[] buf,int len,int timeout,NetWorkingBase netWorkingBase)
	{
		/*private & local definition*/
		int slen = 0;
		Sock_t sock_t = netWorkingBase.getSock_t();
		Log.d("eric_chen", "协议："+sock_t.ordinal()+"   "+sock_t.name());
		
		/*process body*/
		if(sock_t == Sock_t.SOCK_TCP||sock_t == Sock_t.SOCK_UDP)
		{
			try 
			{
				slen = socketUtil.send(sock_t, buf, len, timeout);
			} catch (IOException e) 
			{
				e.printStackTrace();
				return NDK_ERR;
			}
		}
		return slen;
	}

	/**
	 * 接收数据操作 去掉gui显示
	 * @param socketUtil
	 * @param rbuf
	 * @param len
	 * @param timeout
	 * @param netWorkingBase
	 * @return
	 */
	public int sockRecvNoGui(SocketUtil socketUtil,byte[] rbuf,int len,int timeout,NetWorkingBase netWorkingBase)
	{
		/*private & local definition*/
		int rlen = 0;
		Sock_t sock_t = netWorkingBase.getSock_t();
		
		/*process body*/
		if(sock_t == Sock_t.SOCK_TCP|| sock_t == Sock_t.SOCK_UDP)
		{
			try 
			{
				// 多次接收的处理机制
				rlen = socketUtil.receive(sock_t,rbuf,len, timeout);
			} catch (Exception e) {
				e.printStackTrace();
				return NDK_ERR;
			}
		}
		return rlen;
	}

	public int wireDialComm(SocketUtil socketUtil,MobilePara mobilePara,PacketBean sendPacket,int flag) 
	{
		/*private & local definition*/
		byte[] rbuf = new byte[PACKMAXLEN];
		int slen = 0,rlen =0,ret = 0;
		
		/*process body*/
		if((ret = layerBase.netUp(/*socketUtil,*/ mobilePara,mobilePara.getType()))!=SUCC)
		{
			gui.cls_show_msg1_record(TAG, "wireDialComm", g_keeptime, "%s, line %d:NetUp失败", TAG,Tools.getLineInfo());
			return ret;
		}
		if(flag == RESET_PPPOPEN)
		{
			gui.cls_show_msg1_record(TAG, "wireDialComm", 2, "PPP已打开(TCP未打开)...即将软重启...");
			// 软件重启
			Tools.reboot(myactivity);
		}
		if((ret = layerBase.transUp(socketUtil,mobilePara.getSock_t()))!=SUCC)
		{
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), mobilePara.getType());
			gui.cls_show_msg1_record(TAG, "wireDialComm", g_keeptime, "%s, line %d:TransUp失败", TAG,Tools.getLineInfo());
			return ret;
		}
		if(flag == RESET_TCPOPEN)
		{
			gui.cls_show_msg1_record(TAG, "wireDialComm", 2, "TCP已打开(PPP已打开)...即将软重启...");
			// 软件重启
			Tools.getLineInfo();
		}
		// 收发数据
		if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara))!= sendPacket.getLen())
		{
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), mobilePara.getType());
			gui.cls_show_msg1_record(TAG, "wireDialComm", g_keeptime, "%s, line %d:发送数据失败(实际%d, 预期%d)", TAG,Tools.getLineInfo(),slen,sendPacket.getLen());
			return FAIL;
		}
		
		Arrays.fill(rbuf, (byte) 0);
		if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara))!=sendPacket.getLen())
		{
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), mobilePara.getType());
			gui.cls_show_msg1_record(TAG, "wireDialComm", g_keeptime, "%s, line %d:接收数据失败(实际%d, 预期%d)", TAG,Tools.getLineInfo(),rlen,sendPacket.getLen());
			return FAIL;
		}
		
		if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
		{
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), mobilePara.getType());
			gui.cls_show_msg1_record(TAG, "wireDialComm", g_keeptime, "%s, line %d:校验失败", TAG,Tools.getLineInfo());
			return FAIL;
		}
		
		if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t()))!=NDK_OK)
		{
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), mobilePara.getType());
			gui.cls_show_msg1_record(TAG, "wireDialComm", g_keeptime, "%s, line %d:TransDown失败", TAG,Tools.getLineInfo());
			return ret;
		}
		if(flag == RESET_TCPCLOSE)
		{
			gui.cls_show_msg1_record(TAG, "wireDialComm", 2, "TCP已关闭(PPP未关闭)...即将软重启...");
			// 软件重启
			Tools.reboot(myactivity);
		}
		layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), mobilePara.getType());
		if(flag == RESET_PPPCLOSE)
		{
			gui.cls_show_msg1_record(TAG, "wireDialComm", 2, "PPP已关闭(TCP已关闭)...即将软重启...");
			// 重启操作
			Tools.reboot(myactivity);
		}
		return SUCC;
	}
	
	// add by 20150513
	// 循环检测无线的状态
	public int wireDetect(Context context,long startTime,int timeout)
	{
		int ret = -1;
		
		if(NetworkUtil.waitNetWorkConn(context, NetworkCapabilities.TRANSPORT_CELLULAR, timeout)==4)
			ret = NDK_OK;
		else
			ret = NDK_ERR_TIMEOUT;
		return ret;
	}
	
	
	public int select_rst_flag()
	{
		while(true)
		{
			int[] flg = {RESET_PPPOPEN,RESET_PPPCLOSE,RESET_TCPOPEN,RESET_TCPCLOSE};
			int nkeyIn = gui.cls_show_msg("选择复位类型\n0.PPP:打开  1.PPP:关闭\n2.TCP打开  2.TCP:关闭\n");
			
			switch (nkeyIn) 
			{
			case '0':
			case '1':
			case '2':
			case '3':
				return flg[nkeyIn-'0'];
				
			case ESC:
				handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "将不进行复位操作...", 2);
				return RESET_NO;
			}
		}
	}
		
	// SD卡
	public int systestSdCard(DiskType diskType) 
	{
		/* private & local definition */
		int bufferSize = 1024 * 200;
		String rootDir, fName;
		byte[] rBuf = new byte[bufferSize];
		byte[] wBuf = new byte[bufferSize];
		int ret = 0, loop = 0, cnt = (int) (Math.random() * 200) + 2, wrlen = bufferSize
				- (int) Math.random() * 8;
		long oldTime, oldTime1;
		FileSystem fileSystem = new FileSystem();

		/* process body */
		// 起始时间
		oldTime = System.currentTimeMillis();
		DiskInfo diskInfo = new DiskInfo();
		while (true) 
		{
			ret = diskInfo.NDK_DiskGetstate();
			// 代表SD卡挂载成功
			if (ret == NDK_OK)
				break;
			if (Tools.getStopTime(oldTime) > 30) 
			{
				gui.cls_show_msg1( g_keeptime, "%s, line %d:获取状态超时(%d)",TAG, Tools.getLineInfo(), ret);
				return NDK_ERR;
			}
			SystemClock.sleep(1000);
		}
		/**文件路径统一修改为Android原生方式 by 20200327*/
		
		rootDir = Tools.getDesignPath(myactivity, diskType)+"/";
		fName = rootDir + TESTFILE;
		LoggerUtil.i("test path="+fName);
		fileSystem.JDK_FsDel(fName);
		if ((ret = fileSystem.JDK_FsOpen(fName, "w")) != NDK_OK) 
		{
			gui.cls_show_msg1(g_keeptime, "%s, line %d:创建文件失败(%d)", TAG,Tools.getLineInfo(), ret);
			fileSystem.JDK_FsDel(fName);
			return NDK_ERR;
		}
		for (loop = 0; loop < wrlen; loop++)
			wBuf[loop] = (byte) (Math.random() * 256);
		
		for (loop = 0; loop < cnt; loop++) 
		{
			gui.cls_printf(String.format("生成%s文件中(约%dkB)已生成(约%dkB),请稍候...",(diskType == DiskType.SDDSK) ? "内部SD盘" : (diskType == DiskType.TFDSK)?"外部TF卡":"U盘", cnt * wrlen
							/ 1024, loop * wrlen / 1024).getBytes());
			if ((fileSystem.JDK_FsWrite(fName, wBuf, wrlen, 2)) != wrlen) 
			{
				gui.cls_show_msg1_record(TAG, "systestSdCard", g_keeptime,"%s, line %d:写文件失败(cnt=%d, loop=%d, ret=%d, wrlen=%d)",
						TAG, Tools.getLineInfo(), cnt, loop, ret, wrlen);
				fileSystem.JDK_FsDel(fName);
				return NDK_ERR;
			}
		}
		oldTime1 = System.currentTimeMillis();
		while (true) 
		{
			ret = diskInfo.NDK_DiskGetstate();
			if (ret == NDK_OK)
				break;
			if (Tools.getStopTime(oldTime1) - oldTime1 > 30) 
			{
				gui.cls_show_msg1(g_keeptime, "%s, line %d:获取状态超时(%d)",TAG, Tools.getLineInfo(), ret);
				fileSystem.JDK_FsDel(fName);
				return NDK_ERR;
			}
			SystemClock.sleep(1000);
		}
		if ((ret = fileSystem.JDK_FsOpen(fName, "r")) != NDK_OK) 
		{
			gui.cls_show_msg1( g_keeptime, "%s, line %d:打开文件失败(%d)", TAG,Tools.getLineInfo(), ret);
			fileSystem.JDK_FsDel(fName);
			return NDK_ERR;
		}
		// 文件大小是否发生改变
		if ((ret = fileSystem.JDK_FsFileSize(fName)) !=cnt*wrlen) {
			gui.cls_show_msg1(g_keeptime,"%s, line %d:文件大小校验失败(实测:%dB, 预期:%dB)", TAG, Tools.getLineInfo(),
					ret, cnt * wrlen);
			fileSystem.JDK_FsDel(fName);
			return NDK_ERR;
		}
		gui.cls_printf(String.format("校验%s文件中(约%dkB),请稍候...", (diskType == DiskType.UDISK) ? "U盘" : "SD卡",cnt * wrlen
				/ 1024).getBytes());
		oldTime = System.currentTimeMillis();
		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(new File(fName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (loop = 0; loop < cnt; loop++) 
		{
			if ((ret = fileSystem.JDK_FsRead(fileIn, rBuf,wrlen)) != wrlen) 
			{
				gui.cls_show_msg1( g_keeptime,"%s, line %d:数据读写不一致(cnt=%d, loop=%d, ret=%d, wrlen=%d)",
								TAG, Tools.getLineInfo(), cnt, loop, ret, wrlen);
//				new ShowDialog().showDialog(myactivity,"请保存数据并关闭串口", GlobalVariable.FLAG_FALSE);
//			GlobalVariable.PORT_FLAG = false;
//			while(GlobalVariable.PORT_FLAG)
//			{}
				gui.cls_show_msg("请保存数据并关闭串口，完成后任意键继续");
				fileSystem.JDK_FsDel(fName);
				return NDK_ERR;
			}
			gui.cls_printf(("文件校验时间："+(System.currentTimeMillis()-oldTime)).getBytes());
		}
		gui.cls_printf("".getBytes());
		// 显示时间在液晶屏上
		if ((ret = fileSystem.JDK_FsDel(fName)) != NDK_OK) 
		{
			gui.cls_show_msg1( g_keeptime, "%s, line %d:删除文件失败(%d)", TAG,
					Tools.getLineInfo(), ret);
		}
		return NDK_OK;
	}
	
	TextView tvSystest;
	boolean flag=true;
	
	// add by 20150416
	// 触屏校验
	public int systestTouch() 
	{
		// 保留有状态栏
		int xPoint,yPoint = 0,width=GlobalVariable.ScreenWidth/11,height=GlobalVariable.ScreenWidth/11;
		int screenWidth,screenHeight = 0;
		screenWidth = GlobalVariable.ScreenWidth;
		// 修改记录：之前N900底部黑块无法显示，故删除底部触屏操作 by zhengxq
		screenWidth = GlobalVariable.ScreenWidth;
		screenHeight = GlobalVariable.ScreenHeight;
		
		xPoint = (int) (Math.random()*(screenWidth-width-2));
		yPoint = (int) (Math.random()*(screenHeight-height-2-GlobalVariable.TitleBarHeight));
		
		// // 设置的触摸点:跳转到另外一个Activity进行触摸点击
		gui.cls_printf("请点击屏幕上显示的黑框中心".getBytes());
		Intent touchIntent = new Intent(myactivity, TouchActivity.class);
		touchIntent.putExtra("point_x", xPoint);
		touchIntent.putExtra("point_y", yPoint);
		myactivity.startActivity(touchIntent);
		
		LoggerUtil.d(TAG+",systestTouch====show black block");
		synchronized (TouchActivity.lockTouch) {
			try {
				TouchActivity.lockTouch.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LoggerUtil.d(TAG+",systestTouch===click black block");
		
		yPoint = yPoint+GlobalVariable.StatusHeight;
		
		Log.d(TAG, String.format("touch_X = %f,xpoint = %d,width = %d,touch_y = %f,ypoint=%d,height=%d", 
				GlobalVariable.gScreenX,xPoint,width,GlobalVariable.gScreenY,yPoint,height));
		if (GlobalVariable.gScreenX >= xPoint&& GlobalVariable.gScreenX <= (xPoint + width)
				&& GlobalVariable.gScreenY >= yPoint&& GlobalVariable.gScreenY <= (yPoint + height)) 
			return NDK_OK;
		else 
			return NDK_ERR;
	}
	// end by 20150416
	
	public int setWireType(final MobilePara mobilePara)
	{
		final MobileUtil mobileUtil = MobileUtil.getInstance(myactivity,handler);
		gui.cls_printf("正在获取网络类型，请稍等...".getBytes());
		if((mobileUtil.getSimState())!=NDK_OK)
		{
			return -1;
		}
		mobileUtil.openNet();
		NetworkUtil.waitNetWorkConn(myactivity, NetworkCapabilities.TRANSPORT_CELLULAR, 60*1000);
		
		TelephonyManager mTelephonyManager  = (TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		int typeSign = mTelephonyManager.getNetworkType();
		switch (typeSign) 
		{
		 case TelephonyManager.NETWORK_TYPE_GPRS:
         case TelephonyManager.NETWORK_TYPE_EDGE:
         case TelephonyManager.NETWORK_TYPE_CDMA:
         case TelephonyManager.NETWORK_TYPE_1xRTT:
         case TelephonyManager.NETWORK_TYPE_IDEN:
			mobilePara.setType(LinkType.GPRS);
			break;
			
         case TelephonyManager.NETWORK_TYPE_UMTS:
         case TelephonyManager.NETWORK_TYPE_EVDO_0:
         case TelephonyManager.NETWORK_TYPE_EVDO_A:
         case TelephonyManager.NETWORK_TYPE_HSDPA:
         case TelephonyManager.NETWORK_TYPE_HSUPA:
         case TelephonyManager.NETWORK_TYPE_HSPA:
         case TelephonyManager.NETWORK_TYPE_EVDO_B: 
         case TelephonyManager.NETWORK_TYPE_EHRPD:  
         case TelephonyManager.NETWORK_TYPE_HSPAP:
        	 mobilePara.setType(LinkType.CDMA);
        	 break;
        	 
         case TelephonyManager.NETWORK_TYPE_LTE:
        	 mobilePara.setType(LinkType.LTE);
        	 break;

		default:
			mobilePara.setType(LinkType.GPRS);
			// 有待操作
			break;
		}
		mobileUtil.closeNet();
		gui.cls_show_msg1(3, "获取到的网络类型为：%s",mobilePara.getType());
		return NDK_OK;
	}
	
	// add by 20150612
	// wifi热点开关 true 打开wifi ap false 关闭wifi ap
	public boolean setWifiApEnabled(boolean enabled, String ssid, String key,ParaEnum.Wifi_Ap_Enctyp sec, boolean isHid,boolean open) 
	{
		WifiManager wifiManager = (WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE); 
		if (enabled) { // disable WiFi in any case
			// wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
			wifiManager.setWifiEnabled(false);
		}
		//7.1版本需要系统应用才可以获取或者通过这种方式反射获取 
		if (Build.VERSION.SDK_INT >= 25) {
			if (open) {
				 if(gui.cls_show_msg("请到设置中打开热点按钮，任意键继续，[取消]退出")==ESC){
		               return false;
				 }
				else{
					 try {
						 Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
						 method.setAccessible(true);
						 WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
						 Log.e(TAG, config.preSharedKey);
						 Field[] fields = config.getClass().getFields();
						 for (Field field : fields) {
							 if (field.getName().equals("SSID")) {
								 ssid = field.get(config).toString();
								 WifiApSetting.setWifiApSsid(ssid);
							 } else if (field.getName().equals("preSharedKey")) {
								 key = field.get(config).toString();
								 WifiApSetting.setWifiApKey(key);
					
							 }
						 	}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
						 return true;
				}
			}else {
				 if(gui.cls_show_msg("请到设置中关闭热点按钮，任意键继续，[取消]退出")==ESC)
		               return false;
					  else
						 return true;
			}
		
		}else{
			try {
				// 热点的配置类
				WifiConfiguration apConfig = new WifiConfiguration();
				apConfig.hiddenSSID = isHid;
				// 配置热点的名称(可以在名字后面加点随机数什么的)
				apConfig.SSID = ssid;
				// 配置热点的密码
				apConfig.preSharedKey = key;
	
				apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
				// 加密模式
				// 安全：WPA2_PSK
				if (sec == Wifi_Ap_Enctyp.WIFI_NET_SEC_WEP_OPEN) {
					apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				} else if (sec == Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA|| sec == Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA2) {
					apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				}
				// 通过反射调用设置热点
				Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
				// 返回热点打开状态
				return (Boolean) method.invoke(wifiManager, apConfig, enabled);
			} catch (Exception e) {
				return false;
			}
		}
	}

	public void intentSys()
	{
		myactivity.finish();
		/*Log.d("end test", "退出测试");
		if (!(null == this || !this.isAdded())) 
			this.getActivity().finish();
		Intent intent = new Intent(this.getActivity(), SysTest.class);
		startActivity(intent);*/
	}
	
	
	// 蓝牙配对操作
	public boolean pair(String strAddr) 
	{
		boolean result = false;
		// 蓝牙设备适配器
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 取消发现当前设备的过程
		bluetoothAdapter.cancelDiscovery();
		if (!bluetoothAdapter.isEnabled()) 
		{
			bluetoothAdapter.enable();
		}
		if (!BluetoothAdapter.checkBluetoothAddress(strAddr)) 
		{ // 检查蓝牙地址是否有效
		}
		// 由蓝牙设备地址获得另一蓝牙设备对象
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);
		if (device.getBondState() != BluetoothDevice.BOND_BONDED) 
		{
			try {
				// ClsUtils.setPin(device.getClass(), device, strPsw); //
				// 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				long startTime = System.currentTimeMillis();
				while(Tools.getStopTime(startTime)<5)
				{
					SystemClock.sleep(300);
					if(GlobalVariable.pairResult == Pair_Result.BOND_BONDED)
					{
						result = true;
						break;
					}
				}
				return result;
				
			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			}

		} else {
			result = true;
		}
		return result;
	}
	/**
	 * 
	 * @param handleMsg
	 * @param obj
	 *//*
	public void show_flag(int handleMsg,Object obj)
	{
		switch (GlobalVariable.gAutoFlag) 
		{
		// 手动显示
		case HandFull:
		case AutoHand:
			handler.sendMessage(handler.obtainMessage(handleMsg, obj));
			
			break;

		// 自动显示
		case AutoFull:
			gui.cls_show_msg1(2,(String)obj);
			break;
			
		default:
			break;
		}
	}*/
	/*// 蓝牙配置操作
	public void btConfig(Config config,Handler mHander,BluetoothManager bluetoothManager,ArrayList<BluetoothDevice> pairList)
	{
		gui.cls_show_msg(2, "正在进行蓝牙扫描，请稍后");
		GlobalVariable.gDiscoveryFinished = false;
		pairList.clear();
		unPairList.clear();
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		bluetoothAdapter.enable();
		SystemClock.sleep(2000);
		Set<BluetoothDevice> pairedDevices = bluetoothManager.queryPairedDevices();
		for (BluetoothDevice device : pairedDevices) 
		{
			try 
			{
				ClsUtils.removeBond(device.getClass(), device);
				SystemClock.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 显示蓝牙列表
		mHander.sendEmptyMessage(0);
		detectBTDevice(bluetoothManager);
		GlobalVariable.PORT_FLAG = true;
		while (GlobalVariable.PORT_FLAG);
		if(config.getAddress()!=null)
		{
			pair(config.getAddress());
		}
		else if(unPairList.size()==0)
		{
			gui.cls_show_msg(1, "未搜索到任何蓝牙设备");
			mHander.sendEmptyMessage(1);
		}
	}*/
	
/*	public void appendInteractiveInfoAndShow(final String string,
			final int messageTag) {

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (messageTag) {
				case MessageTag.NORMAL:
					newstring = "<font color='black'>" + string + "</font>";
					break;
				case MessageTag.ERROR:
					newstring = "<font color='red'>" + string + "</font>";
					break;
				case MessageTag.TIP:
					newstring = "<font color='blue'>" + string + "</font>";
					break;
				case MessageTag.DATA:
					String arr[] = string.split(":");
					if (arr.length >= 2)
						newstring = "<font color='green'>" + arr[0] + "</font>"
								+ ":" + arr[1];
					else if (arr.length == 1)
						newstring = "<font color='green'>" + arr[0] + "</font>";
					break;
				default:
					break;
				}
				deviceInteraction = newstring;
				mtvShow.setText(Html.fromHtml(deviceInteraction));
			}
		});
	}*/
	//RGB解码结果存放
	public 	String scanResult;
	//RGB解码存放图片路径
	public String path=GlobalVariable.sdPath+"scan/";
	//RGB解码是否完成标志位
	public Boolean scanCount=false;
	public int position=0;
	
	protected ScanUtil scan_Domestic;
//	protected com.android.newland.scan.ScanUtil scan_OverSea;
	/**
	 * 扫码代码
	 */
	/**
	 * 初始化软硬解扫码
	 * @param scan_Mode 扫码模式
	 * @param cameraId 扫码摄像头
	 * @param timeout 扫码超时时间
	 * @return
	 */
	public void initScanMode(NlsPara nlsPara,Scan_Mode scan_Mode,int cameraId,int timeout)
	{
		
		switch (scan_Mode) 
		{
		case MODE_ONCE:
		case MODE_CONTINUALLY:
		case MODE_MANUALLY:
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			{
				scan_OverSea = new com.android.newland.scan.ScanUtil(myactivity);
				scan_OverSea.init(scan_Mode.ordinal(), timeout, ScanUtil.FOCUS_ON, true);;
			}
			else
			{*/
				scan_Domestic = new ScanUtil(myactivity);
				scan_Domestic.init(scan_Mode.ordinal(), timeout, ScanUtil.FOCUS_ON, true);
//			}
			break;
			
		case ZXING:
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)){
				scan_OverSea = new com.android.newland.scan.ScanUtil(myactivity, surfaceView, cameraId, true, timeout);
			}
			else{*/
				scan_Domestic = new ScanUtil(myactivity, surfaceView, cameraId, true, timeout);
//			}
			break;
			
		case NLS_0:
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				scan_OverSea = new com.android.newland.scan.ScanUtil(myactivity, surfaceView, cameraId, true, timeout,0);
			else{*/
				scan_Domestic = new ScanUtil(myactivity, surfaceView, cameraId, true, timeout,0);
//			}
			break;
			
		case NLS_1:
			/**
			 * nls方式在这里有设置surfaceView的大小,应该等待操作Camera结束之后再进行Camera的Init
			 *//*
			if(nlsPara.isPreview())
			{
				LoggerUtil.i("NLS|||修改预览画面框");
				Message msg = Message.obtain(handler, HandlerMsg.SCAN_SURFACE_FLUSH, cameraId);
				handler.sendMessage(msg);
				synchronized (SysTest56.lock) {
					try {
						SysTest56.lock.wait(timeout);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}*/
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			{
				scan_OverSea = new com.android.newland.scan.ScanUtil(myactivity, nlsPara.isPreview()?surfaceView:null, cameraId, true, timeout,1);
			}
			else
			{*/
			LoggerUtil.e("initScanMode isPreview="+nlsPara.isPreview());
			scan_Domestic = new ScanUtil(myactivity, nlsPara.isPreview()?surfaceView:null, cameraId, true, timeout,1);
			scanSet(nlsPara.getScanSet());
//				for (int i = 0; i < arr.length; i++) {
//					 ret=(scan_Domestic.setNlsScn(arr[i], "Enable", "1"));
//						if (ret!=1) {
//							 gui.cls_show_msg("开启%s失败",arr[i]); 
//							
//						}
//						
//					}
//					 scan_Domestic.setNlsScn("UPC/EAN", "Add-On", "Enable");
//					scan_Domestic.setNlsUPCEANSwitch(nlsPara.getUpcBtn());
//			}
			break;
			
		/*case NLS_picture:
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
			{
				scan_Domestic = new ScanUtil(myactivity);
				scan_Domestic.initDecode(new ResultCallBack() {
					
					@Override
					public void onResult(int eventCode, int codeType, byte[] data1, byte[] data2, int length) {
						 synchronized (g_lock) {
							LoggerUtil.e(TAG+",RGBCallback===result="+new String(data1));
							scanResult=new String(data1);
							scanCount=true;
							//结果返回后释放锁
							g_lock.notify();	
						 }
					}
				});
			}
			break;*/
		default:
			break;
		}
	}
	
	public void scanSet(int scanSet)
	{
		LoggerUtil.e("DefaultFragement->scanSet:"+scanSet);
		if(scanSet==2||scanSet==1)
		{
			scan_Domestic.setNlsScn("QR", "CodeNum", scanSet+"");
			scan_Domestic.setNlsScn("QR", "NumFixed", "0");
		}
	}
	
	/**
	 * 进行扫码操作
	 * @param scanUtil
	 * @return
	 */
	public int doScan(Scan_Mode scan_Mode,StringBuffer scanresult,NlsPara nlsPara)
	{
		int ret = -1;
		
		switch (scan_Mode) 
		{
		case MODE_ONCE:
		case ZXING:
		case NLS_0:
		case NLS_1:
//			deviceLogger.debug("start doscan");
			if(nlsPara.isLed)
				scan_Domestic.setLED(1);
			if(nlsPara.isRed)
				scan_Domestic.setRedLED(1);
			String result = null;
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				result = (String) scan_OverSea.doScan();
			else*/
				result = (String) scan_Domestic.doScan();
			LoggerUtil.i("scanResult="+result);
			if(nlsPara.isLed)
				scan_Domestic.setLED(0);
			if(nlsPara.isRed)
				scan_Domestic.setRedLED(0);
//			deviceLogger.debug("do scan result:"+scanResult);
			if(result==null||result.equals("null"))
				ret = NDK_SCAN_NO_RESULT;
			else if(result.startsWith("F"))
				ret = NDK_SCAN_FAULT;
			else
			{
				scanresult.delete(0, scanresult.length());
				scanresult.append(result.substring(1));
				if(nlsPara.isPreview())
				{
//					if(gui.cls_show_msg("扫码结果:"+scanresult+",[确认]扫码一致,[其他]扫码错误")==ENTER)
//					gui.cls_show_msg2(0.1f,"扫码结果:"+scanresult);
					Log.d("cd", "扫码结果:"+scanresult);
						ret = NDK_SCAN_OK;
//					else
//						ret = NDK_SCAN_FAULT;
				}
				else// 无预览画面直接返回0
				{
					gui.cls_printf(("扫码结果:"+scanresult.toString()).getBytes());
					ret = NDK_SCAN_OK;
				}
//				if(gui.cls_show_msg("扫码结果："+scanresult+",[确认]扫码一致,[其他]扫码错误")!=ENTER)
//					ret = NDK_SCAN_DATA_ERR;
//				else
//					ret = NDK_SCAN_OK;
			}
			break;
			
		case MODE_MANUALLY:
			String scanResult2=null;
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				scanResult2 = (String) scan_OverSea.doScan();
			else*/
				scanResult2 = (String) scan_Domestic.doScan();
			if(scanResult2.equals("null"))
				ret = NDK_SCAN_NO_RESULT;
			else
			{
				scanresult.delete(0, scanresult.length());
				scanresult.append(scanResult2);
				if(gui.cls_show_msg("扫码结果:"+scanresult+",[确认]扫码一致,[其他]扫码错误")==ENTER)
					return NDK_SCAN_OK;
				else
					ret = NDK_SCAN_DATA_ERR;
			}
			break;
		
		// 连续扫码
		case MODE_CONTINUALLY:
			String[] scanRe = null;
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				scanRe = (String[]) scan_OverSea.doScan();
			else*/
				scanRe = (String[])scan_Domestic.doScan();
			for (int i = 0; i < scanRe.length; i++) 
			{
				scanRe[i] = scanRe[i].substring(1);
			}
			scanresult.delete(0, scanresult.length());
			scanresult.append(Arrays.toString(scanRe));
			if(nlsPara.isPreview())
			{
				if(gui.cls_show_msg("扫码结果:"+scanresult+",[确认]扫码一致,[其他]扫码错误")==ENTER)
					ret = NDK_SCAN_OK;
				else
					ret = NDK_SCAN_FAULT;
			}
			else// 无预览画面直接返回0
			{
				gui.cls_printf(("扫码结果:"+scanresult.toString()).getBytes());
				ret = NDK_SCAN_OK;
			}
			break;
			
		/*case NLS_picture:不支持RGB解码方式
//			scan_Domestic.startYUVDecode(yuv, width, height)
			if(scan_Domestic.startRGBDecode(path+pic[position][0]) != NDK_SCAN_PARSE_SUCC) {
				gui.cls_show_msg1_record(TAG, "pictureDecodePress", g_keeptime, "line %d:图片传输失败(%d)", Tools.getLineInfo(),ret);
			}
			synchronized (g_lock) {
			 try { 
				 g_lock.wait();
				 scanCount = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			 }
			if(!scanResult.equals(pic[position][1])){
				gui.cls_show_msg1_record(TAG, "pictureDecodePress", g_keeptime, "line %d:RGB解码失败(预期:%s,实际:%s)", Tools.getLineInfo(),pic[position][1],scanResult);
				ret = NDK_SCAN_FAULT;
			}else
				ret = NDK_SCAN_OK;
			break;*/
		default:
			break;
		}
		return ret;
	}
	
	
	/**
	 * 进行扫码操作
	 * @param scanUtil
	 * @return
	 */
	public int robot_doScan(Scan_Mode scan_Mode,StringBuffer scan_result,NlsPara nlsPara)
	{
		int ret = -1;
		
		switch (scan_Mode) 
		{
		case MODE_ONCE:
		case ZXING:
		case NLS_0:
		case NLS_1:
			if(nlsPara.isLed)
				scan_Domestic.setLED(1);
			if(nlsPara.isRed)
				scan_Domestic.setRedLED(1);
			String result = null;
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				result = (String) scan_OverSea.doScan();
			else*/
				result = (String) scan_Domestic.doScan();
			LoggerUtil.v("cccx->result="+result==null?"null":result);
			
			if(nlsPara.isLed)// N700需要开启LED等和对焦灯,modify by zhengxq 20181206
				scan_Domestic.setLED(0);
			if(nlsPara.isRed)
				scan_Domestic.setRedLED(0);
			if(result==null||result.equals("null"))
				ret = NDK_SCAN_NO_RESULT;
			else if(result.startsWith("F"))
					ret = NDK_SCAN_FAULT;
			else
			{
				// 扫码成功
				scan_result.delete(0, scan_result.length());
				scan_result.append(result.substring(1));
				ret = NDK_SCAN_OK;
			}
			break;
			
		case MODE_MANUALLY: // 手动扫码不带‘S’或‘F’标志
			String scanResult2;
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				scanResult2 = (String) scan_OverSea.doScan();
			else*/
				scanResult2 = (String) scan_Domestic.doScan();
			if(scanResult2.equals("null"))
				ret = NDK_SCAN_NO_RESULT;
			else
			{
				// 扫码成功
				scan_result.delete(0, scan_result.length());
				scan_result.append(scanResult2);
				ret = NDK_SCAN_OK;
			}
			break;
		
		// 连续扫码
		case MODE_CONTINUALLY:
			String[] scanRe=null;
			/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
				scanRe = (String[]) scan_OverSea.doScan();
			else*/
				scanRe = (String[]) scan_Domestic.doScan();
			for (int i = 0; i < scanRe.length; i++) 
			{
				scanRe[i] = scanRe[i].substring(1);
			}
			// 扫码成功
			scan_result.delete(0, scan_result.length());
			scan_result.append(Arrays.toString(scanRe));
			ret = NDK_SCAN_OK;
			break;

		default:
			break;
		}
		return ret;
	}
	
	/**
	 * 扫码释放操作
	 * @param scanUtil
	 */
	public void releaseScan(Scan_Mode scan_Mode)
	{
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
		{
			if(scan_OverSea!=null)
				scan_OverSea.release();
		}
		else
		{*/
			if(scan_Domestic!=null)
			{
//				if(GlobalVariable.currentPlatform==Model_Type.N700)
//				{
//					scan_Domestic.setRedLED(0);
//					scan_Domestic.setLED(0);
//				}	
				if(scan_Mode==Scan_Mode.NLS_picture)
				{
					scan_Domestic.stopDecode();
				}else
				{
					if(GlobalVariable.currentPlatform==Model_Type.N900_3G){
						scan_Domestic.relese();
					}
					else{
						Log.d("eric===", "压力扫码释放！！！！");
						scan_Domestic.release();
					}
				}
			}
//		}

	}
	
	
	/**
	 * nfc配置
	 */
	public int reader_flag = NfcAdapter.FLAG_READER_NFC_B;
	public Nfc_Card nfc_config(Handler handler,String title)
	{
		Nfc_Card nfc_card = new Config(myactivity, handler).nfc_card_config();
		
		switch (nfc_card) 
		{
		case NFC_A:
			gui.cls_show_msg1(1, "目前不支持A卡测试，请重新配置");
			reader_flag = NfcAdapter.FLAG_READER_NFC_A;
			break;
			
		case NFC_B:
			gui.cls_show_msg1(1, "B卡测试可采用标准B卡或身份证测试");
			reader_flag = NfcAdapter.FLAG_READER_NFC_B;
			break;
			
		case NFC_M1:
			gui.cls_show_msg1(1, "目前不支持M1卡测试，请重新配置");
			reader_flag = NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
			break;

		default:
			break;
		}
		return nfc_card;
	}
	
	//910蓝牙底座wifi设置 by zhangxj
	public void set_dongle_wifi(final WifiPara wifiPara)
	{
		LayoutInflater inflater = LayoutInflater.from(myactivity);
		View view = inflater.inflate(R.layout.set_wifi_mes, null);
		
		final EditText oldPwd = (EditText) view.findViewById(R.id.oldPwd);
		final EditText newPwd = (EditText) view.findViewById(R.id.newPwd);
		final EditText name = (EditText) view.findViewById(R.id.name);
		final EditText dns = (EditText) view.findViewById(R.id.dns);
		final EditText channel = (EditText) view.findViewById(R.id.channel);
		name.setText(wifiPara.getSsid());
		dns.setText(wifiPara.getDns1());
		channel.setText(wifiPara.getChannel()+"");
		
		final RadioGroup wlanEncMode = (RadioGroup) view.findViewById(R.id.group_wlan_enc_mode);
		final RadioGroup rg_ssid = (RadioGroup) view.findViewById(R.id.rg_ssid_broad);
		RadioButton wpa1Button=(RadioButton) view.findViewById(R.id.wpa1);
		RadioButton wpaMixButton=(RadioButton) view.findViewById(R.id.wpa_mix);
		RadioButton wpa2Button=(RadioButton) view.findViewById(R.id.wpa2);
		RadioButton noneButton=(RadioButton) view.findViewById(R.id.none);
		RadioButton scanSsid=(RadioButton) view.findViewById(R.id.rb_ssid_zero);//true
		RadioButton hideSsid=(RadioButton) view.findViewById(R.id.rb_ssid_one);//false
		if(wifiPara.isScan_ssid()){
			scanSsid.setChecked(true);
		}else
			hideSsid.setChecked(true);
		if(encMode==1){
			wpa1Button.setChecked(true);
		}else if(encMode==2){
			wpa2Button.setChecked(true);
		}else if(encMode==3){
			wpaMixButton.setChecked(true);
		}else
			noneButton.setChecked(true);
		new BaseDialog(myactivity, view, "wifi信息设置", "确定", false, new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					wifiOldPwd=oldPwd.getText().toString();
					wifiPara.setPasswd(newPwd.getText().toString());
					wifiPara.setSsid(name.getText().toString());
					wifiPara.setScan_ssid(rg_ssid.getCheckedRadioButtonId()==R.id.rb_ssid_zero?true:false);
					wifiPara.setDns1(dns.getText().toString());
					wifiPara.setChannel(Integer.parseInt(channel.getText().toString()));
					//蛮设置 没有用
					wifiPara.setSec(wlanEncMode.getCheckedRadioButtonId()==R.id.none?WIFI_SEC.NOPASS:WIFI_SEC.WPA);
					synchronized (wifiPara) {
						wifiPara.notify();
					}
				}
				
			}
		}).show();
		wlanEncMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.wpa1) {
					encMode=1;
				} else if (checkedId == R.id.wpa2) {
					encMode=2;
				} else if (checkedId == R.id.wpa_mix) {
					encMode=3;
				}else if (checkedId == R.id.none) {
				    encMode=0;
				    //选择无密码 则把新密码的内容去掉
				    newPwd.setText("");
				    wifiPara.setPasswd("");
				}
			}
		});
	}
	
	/**data:2017/6/30 zhangxinj
	 * 解析DiskType传入字符串,为了自动化时可以同时运行多个DiskType
	 * @param 传入的DiskType字符串
	 * @return 返回一个 DiskType
	 */
	public DiskType getDiskType(String s){
		if (s.equals("UDISK")) {
			/*gui.cls_show_msg("请确保一定插入U盘，否则获取路径会出错，[确认]继续");
			if(GlobalVariable.currentPlatform==Model_Type.X5||GlobalVariable.currentPlatform==Model_Type.X3){
		    	File file=new File("/storage/");
		    	 File[] files = file.listFiles();
		    	 for(File f:files){
		    		 if(f.isDirectory()){
		    			 if(!f.getName().equals("emulated") && !f.getName().equals("self")  && !f.getName().equals("self") ){
		    				 GlobalVariable.uPath="/storage/"+f.getName()+"/";
		    				 LoggerUtil.e("U盘路径："+ GlobalVariable.uPath);
		    			 }
		    		 }
		    	 }
		    }*/
			return DiskType.UDISK;	
		}
		if (s.equals("SDDSK")) {
			return DiskType.SDDSK;
		}
		if (s.equals("TFDSK")) {
			return DiskType.TFDSK;
		}
		return DiskType.UDISK;	
	}
	
	// 输入字符串测试
	public int getKeyInput(byte[] pszBuf,int[] len)
	{
		return JniNdk.JNI_Kb_GetInput(pszBuf, 1, 100, len, (byte)0, TIMEOUT_KEYBOARD, (byte)1);
	}
	
	public int getInputHit(int[] pnCode)
	{
		return JniNdk.JNI_Kb_GetCode(TIMEOUT_KEYBOARD, pnCode);
	}
	
	// 读文件，返回字符串
	public String ReadFile(String path) {
		//File file = new File(path);
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "gbk");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr = laststr + tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return laststr;
	}
	public void setEthMac(String mac){
		final EditText addressText = new EditText(myactivity);
		addressText.setText(mac);
		new BaseDialog(myactivity, addressText, "修改以太网MAC地址", "确定", false,new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive)
				{
					EthMac=addressText.getText().toString();
					synchronized (g_lock) {
						g_lock.notify();
					}
				}
			}
			
		}).show();
	}
	
	/**获取密码键盘输入
	 * 非阻塞的方式实时显示密码键盘输入情况
	 * @return pinblock数据
	 * zhangxinj
	 * 20180306 新增pin压力 添加此方法 此方法UnitFragment有
	 */
	public static byte[] getPinInput(String str,Handler handler)
	{
//		Gui gui = new Gui(myactivity, handler);
		final int SEC_VPP_KEY_PIN = 0;// 有pin键密码按下，用*号显示
		final int SEC_VPP_KEY_BACKSPACE = 1;// 退格键按下
		final int SEC_VPP_KEY_CLEAR = 2;// 清除键按下
		final int SEC_VPP_KEY_ENTER = 3;// 确认键按下
		final int SEC_VPP_KEY_ESC = 4;// 取消键按下
//		final int SEC_VPP_KEY_NULL = 5;
		int iRet = -1,count = 0;
		StringBuffer strBuffer = new StringBuffer();
		int[] status = new int[]{0};
		byte[] pinBlock = new byte[20];
		//strBuffer.append(mtvShow.getText()+"\n");//获取此时textview中的内容，之后的密码*跟在后面
		//直接获取textview会出现内容更新不及时导致获取到空，改为传入字符串方式
		strBuffer.append(str+"\n");
		/*strBuffer.append("-----------------------------------\n");*/
		
		while(true)
		{
			iRet = JniNdk.JNI_Sec_GetPinResult(pinBlock, status);
			if (iRet != NDK_OK) {
//				gui.cls_show_msg1(2,"line %d:获取键盘输入状态失败(ret = %d)", Tools.getLineInfo(), iRet);
				break;
			}
			switch (status[0]) 
			{
			case SEC_VPP_KEY_PIN:
				count++;
				strBuffer.append("*");
				break;
				
			case SEC_VPP_KEY_ENTER:
				strBuffer.append("\n密码输入完毕！！！");
				break;
				
			case SEC_VPP_KEY_CLEAR:// 清除
				strBuffer.delete(strBuffer.length()-count, strBuffer.length());
				count = 0;
				break;
				
			case SEC_VPP_KEY_BACKSPACE:
				if(count>0)
				{
					strBuffer.delete(strBuffer.length()-1, strBuffer.length());
					count = count-1;
				}
				break;
				
			case SEC_VPP_KEY_ESC:
//					strBuffer.delete(strBuffer.length()-count, strBuffer.length());
//					count = 0;
				strBuffer.append("\n用户取消");
				break;

			default:
				break;
			}
			handler.sendMessage(handler.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, strBuffer));
			if (status[0]==SEC_VPP_KEY_ENTER||status[0]==SEC_VPP_KEY_ESC|| iRet != 0) {//pinBlock[0] == -122 
				if(iRet==-1122){
					strBuffer.append("\n密码键盘输入超时已退出");
				}
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		strBuffer.setLength(0);
		handler.sendMessage(handler.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, strBuffer));
		return pinBlock;
	}
	/**
	 * 打印判断是否监听到printer事件
	 */
	public int priEventCheck(){
		switch(GlobalVariable.sdkType){
		case SDK2:
			break;
		case SDK3:
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnableReg)==false)
				break;
			// 要添加打印状态监测，打印完毕后才去监测打印事件是否上送
			while(true)//PRN_STATUS_BUSY
			{
				if(JniNdk.JNI_Print_GetStatus()!=8)
					break;
			}
			/** 打印结束之后要等待多久才能监听到打印事件 by zhengxq 20200403根据郑宁涵建议，打印修改为打印状态不为busy之后再延时0.5s判断事件监听标记*/
			JniNdk.JNI_Sys_Delay(10);// 停留1s，此接口单位是100ms 20200623
			
			if (prnFlag != EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()) 
			{
//				gui.cls_show_msg1(2, "1111PrnFlagf=%d", prnFlag);
//				Log.d("priEventCheck", "prnFlag="+prnFlag);
//				gui.cls_show_msg1_record(TAG, "priEventCheck", g_keeptime, "line %d:没有监听到print事件(%d)",Tools.getLineInfo(), prnFlag);
				prnFlag = -1;
				return NDK_NO_LISTENER_PRINTER;
			}
			gui.cls_show_msg1(2, "222PrnFlagf=%d", prnFlag);
			prnFlag = -1;
			break;
		default:
			return NDK_ERR;
			
		}
		return NDK_OK;
		
	}
	//安卓串口扫描枪扫码
	public int  uart3ManagerReadForScan(final NLUART3Manager uart3Manager,final byte[] buf,final int timeoutSec){
		 int ret=RS232_TIMEOUT;
		ReadForScanThread readForScanThread=new ReadForScanThread(uart3Manager, buf,timeoutSec);
		readForScanThread.start();
		try {
			readForScanThread.join(15*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	// 扫描枪RS232安卓串口读扫描枪数据写数据线程
	private class ReadForScanThread extends Thread {
		NLUART3Manager uart3Manager;
		byte[] buf;
//		int lengthMax;
		int timeoutSec;
		int ret=-1;

		public ReadForScanThread(NLUART3Manager uart3Manager, byte[] buf,int timeoutSec) {
			this.uart3Manager = uart3Manager;
			this.buf = buf;
//			this.lengthMax = lengthMax;
			this.timeoutSec = timeoutSec;
		}

		@Override
		public void run() {
			//一字节一字节的读，以""为循环停止
			int i=0;
			byte[] mByte=new byte[20];
			//有读到1个数据
			while((ret=uart3Manager.read(mByte, 1, timeoutSec))>0)
			{	
				if (ISOUtils.byteToStr(mByte).trim().equals("F")||ISOUtils.byteToStr(mByte).trim().equals("S")) {
					continue;
				}else {
					buf[i]=mByte[0];
				}
//				buf[i]=mByte[0];
				if (i > 1 && ISOUtils.byteToStr(mByte).trim().equals("")) // 读到空字符
					break;
				else
					i++;
			}
		}
	}
	//模拟用户扫码方法  by huhj 20190909
	public int doScan_noconf(Scan_Mode scan_Mode,StringBuffer scanresult,NlsPara nlsPara)
	{
		int ret = -1;
		
		if(nlsPara.isLed)
			scan_Domestic.setLED(1);
		if(nlsPara.isRed)
			scan_Domestic.setRedLED(1);
		String result = null;
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			result = (String) scan_OverSea.doScan();
		else*/
			result = (String) scan_Domestic.doScan();
		if(nlsPara.isLed)
			scan_Domestic.setLED(0);
		if(nlsPara.isRed)
			scan_Domestic.setRedLED(0);
		if(result==null||result.equals("null"))
			ret = NDK_SCAN_NO_RESULT;
		else if(result.startsWith("F"))
				ret = NDK_SCAN_FAULT;
		else
		{
			scanresult.delete(0, scanresult.length());
			scanresult.append(result.substring(1));
			if(nlsPara.isPreview())
			{
//				gui.cls_show_msg("扫码结果:"+scanresult);
//				gui.cls_show_msg1_record(TAG, "scanPre4",g_keeptime,"扫码结果：%s", scanresult,1);
				gui.cls_show_msg1(1, "扫码结果：%s", scanresult);
				ret = NDK_SCAN_OK;
			}
			else// 无预览画面直接返回0
			{
				gui.cls_printf(("扫码结果:"+scanresult.toString()).getBytes());
				ret = NDK_SCAN_OK;
			}
		}
		return ret;
	}
	
	
/*	//根据硬件配置码来获取普通摄像头的cameraId
	public HashMap<String, Integer> cameraHardwareconifg()
	{
		*//**
		 * 0x00:无扫描头和无摄像头
		 * 12:前置和后置软解码扫描头+后置摄像头
		 * 13:只有后置摄像头+后置软解码扫描头
		 * *//*
		HashMap<String, Integer> scanInfo = new HashMap<>();
		scanInfo.put("SCAN_SUP", 0);
		String hardwareInfo = NlBuild.VERSION.NL_HARDWARE_CONFIG;
		LoggerUtil.i("002,硬件识别码="+hardwareInfo);
		String cameraStr = "";
		int iRet=0;
		// 判断摄像头是否可用
		try {
			File file_front = new File("/sys/class/front_camera/camera_name");
			if(file_front.exists()){
				iRet = iRet|2;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			File file_back = new File("/sys/class/back_camera/camera0_name");
			if(file_back.exists()){
				iRet = iRet|1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 不同产品硬件配置码有区别
		if(GlobalVariable.gCurPlatVer==Platform_Ver.A5||GlobalVariable.gCurPlatVer==Platform_Ver.A7)
		{
			cameraStr = hardwareInfo.substring(8, 10);// 针对A5和A7平台
		}
		else if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)// A9先等会，例如F7
		{
			cameraStr=hardwareInfo.substring(24, 26);
		}
		LoggerUtil.i("002,scanInfo="+cameraStr);
		
		if(cameraStr.equals("00")||cameraStr.equals("10")||cameraStr.equals("04"))
		{
			scanInfo.put("iRet", -1);
			scanInfo.put("SCAN_SUP", -1);
		}
		if(cameraStr.equals("01")||cameraStr.equals("11"))// 前置硬解码
		{
			scanInfo.put("iRet",0);
			if((iRet&0x02)==0x02)// 前置
			{
				scanInfo.put("SCAN_SUP", scanInfo.get("SCAN_SUP")|0x02);
			}
		}
		if(cameraStr.equals("03"))// 前置软解码
		{
			scanInfo.put("iRet",0);
			if((iRet&0x02)==0x02)// 前置
			{
				scanInfo.put("SCAN_SUP", scanInfo.get("SCAN_SUP")|0x02);
			}
		}
		if(cameraStr.equals("12"))// 前后置软解码
		{
			scanInfo.put("iRet",0);
			if((iRet&0x02)==0x02)// 前置
			{
				scanInfo.put("SCAN_SUP", scanInfo.get("SCAN_SUP")|0x02);
			}
			if((iRet&0x01)==0x01)// 后置
			{
				scanInfo.put("SCAN_SUP", scanInfo.get("SCAN_SUP")|0x01);
			}
		}
		if(cameraStr.equals("13"))// 后置软解码
		{
			scanInfo.put("iRet",0);
			if((iRet&0x01)==0x01)// 后置
			{
				scanInfo.put("SCAN_SUP", scanInfo.get("SCAN_SUP")|0x01);
			}
		}
		if(cameraStr.equals("20"))// 支付摄像头软解码
		{
			scanInfo.put("iRet",0);
			scanInfo.put("SCAN_SUP", 0x04);
		}
		return scanInfo;
	}*/
//	//根据硬件配置码来获取普通摄像头个数
//	public int cameraHardwareconifg(){
//		String dirName = "/newland/factory/DetectionAppDir/hardwareconifg.xml";
//		File file = new File(dirName);
//		//如果不存在
//		if (!file.exists()) {
//			  LoggerUtil.e("文件不存在---------");
//			return -1;
//		}else{
//			try {
//				  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
//				  DocumentBuilder builder;
//				  builder = factory.newDocumentBuilder();
//				  Document document = builder.parse(file);   
//				  NodeList hardwareInfo = document.getElementsByTagName("string");
//				  Node hardwareId = hardwareInfo.item(0);
//			      String hardwareString=hardwareId.getChildNodes().item(0).getTextContent();
//			      String caremaString=hardwareString.substring(24, 26);
//			      LoggerUtil.e("caremaString:"+caremaString);
//			      if (caremaString.equals("00")||caremaString==null||caremaString.equals("")) {
//			    	  LoggerUtil.e("摄像头参数有误-------");
//					return -2;
//				}else if (caremaString.equals("FF")) {
//					return 0;
//				}else if (caremaString.equals("01")) {
//					return 1;
//				}else if (caremaString.equals("02")) {
//					return 2;
//				}else if (caremaString.equals("03")) {
//					return 3;
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		return -3;
//	}

}
