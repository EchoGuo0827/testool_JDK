package com.example.highplattest.systest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.newland.AnalogSerialManager;
import android.newland.SettingsManager;
import android.newland.os.NdkToJni;
import android.newland.scan.ScanUtil;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.NlsPara;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.CameraConfiguration;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest56.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150729
 * directory 		: 
 * description 		: 扫码综合
 * related document :
 * history 		 	: author									date			remarks
 *			  		 创建											20150729		郑薛晴
 * 					修复BUG2020101903773和BUG2020101503739		20201021		郑佳雯
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest56 extends DefaultFragment 
{
	private final String TAG = SysTest56.class.getSimpleName();
	private final String TESTITEM = "扫码综合测试";
	private String fileName=SysTest56.class.getSimpleName();
	
	private final int TIMEOUT = 10*1000;
	private final int DEFAULT_COUTN = 100;
	private boolean sleepAbnormal = false;
	private Scan_Mode scan_Mode = Scan_Mode.ZXING;
	private int mCameraId = 0;
	private String mCameraMsg="";
	private boolean initFlag = false;
//	private boolean mScanSet = false;
	
	public boolean robotFlag = false;//连接机械臂标志位
	private Gui gui = new Gui(myactivity, handler);
	private NlsPara nlsPara = new NlsPara();

	
	private int ret=-1;
	private boolean mIsPreview = false;
//	public static int isnewscan=-100;  //1代表旧扫码 0代表新扫码
	protected RelativeLayout layScanView;

	//auto
	private boolean autoFlag = false;
	private EthernetPara ethernetPara = new EthernetPara();
	private WifiPara wifiPara = new WifiPara();
	NetWorkingBase[] netWorkingBases = {wifiPara,ethernetPara};
	SocketUtil socketUtil;
	Config config=null; 
	public static Object lock =new Object();
	
	private ScanDefineInfo mScanDefineInfo;

	public void systest56() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		int ret = -1;
		//获取摄像头分辨率的时候注释掉这行  
		mScanDefineInfo = getCameraInfo();
		if(mScanDefineInfo.getCameraId()==-1)
		{
			gui.cls_show_msg("未找到支持的摄像头");
			return;
		}
		//初始化layer对象
		layerBase = Layer.getLayerBase(myactivity,handler);
		
		while (true) 
		{
			int nkeyIn = gui.cls_show_msg("扫码综合\n0.配置\n1.压力测试\n2.性能测试\n3.异常测试\n4.机器人\n5.机械臂测试\n");
			switch (nkeyIn) 
			{
			case '0':
				if((ret = gui.cls_show_msg("是否选择python脚本辅助的自动测试,[确认]选择自动并进入网络配置,[取消]恢复默认手动,[其他]跳过保持上一次设置")) == ENTER){
					autoFlag = true;
					wifiPara.setInput_way(false);
					switch (new Config(myactivity,handler).confConnWlan(wifiPara)) 
					{
					case NDK_OK:
						socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
						gui.cls_show_msg1(1, "wlan参数配置完毕！！！");
						break;
					case NDK_ERR:
						break;
					case NDK_ERR_QUIT:
					default:
						break;
					}
				} else if(ret == ESC){
					autoFlag = false;
				}
				scanConfig();
				break;
				
			case '1':
				scanPreChoose();
				break;
				
			case '2':
				ability();
				break;
				
			case '3':
				scanAbnormal();
				break;
				
			case '4':
				robot_test();
				break;
				
			case '5':/**机械臂扫码20200513 weimj*/
				auto_test();
				break;
				
			case ESC:
				intentSys();
				return;

			default:
				continue;
			}
		}
	}
	
	/**
	 * 扫码配置
	 */
	public void scanConfig() 
	{
		if(GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N700_A7)
		{
			if(gui.cls_show_msg("是否要开启背光灯,是[确认],否[其他]")==ENTER)
				nlsPara.setLed(true);
			if(gui.cls_show_msg("是否要开启对焦灯,是[确认],否[其他]")==ENTER)
				nlsPara.setRed(true);
		}
		//4.Nls图片解码方式\n 1.zxing方式\n2.Nls兼容zxing\n 20200713 zxing方式不需测试
		int nkeyin = gui.cls_show_msg("0.硬解方式\n3.Nls方式\n5.获取摄像头分辨率(测试开发用)");;
		switch (nkeyin) 
		{
		case '0':
			// 默认前置摄像头
			mCameraId = 1;
			config_Hard_Mode();
			break;
			
		/*case '1':全平台不需要测试zxing方式
			if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
			{
				gui.cls_show_msg("A9平台不支持zxing旧接口测试");
				break;
			}
			scan_Mode = Scan_Mode.ZXING;
			cameIdConfig();
			break;*/
				
		/*case '2':
			scan_Mode = Scan_Mode.NLS_0;
			cameIdConfig();
			break;*/
		
		case '3':
			scan_Mode = Scan_Mode.NLS_1;
			cameIdConfig();
			break;
			
		/*case '4':
			scan_Mode= Scan_Mode.NLS_picture;
			position=chooseRGBCode();
			LoggerUtil.e("position："+position);
			break;*/
		case '5':
			Camera  camera=Camera.open(0);
			Camera.Parameters params = camera.getParameters();

	        List<android.hardware.Camera.Size> pictureSizes = params.getSupportedPictureSizes();
	        int length = pictureSizes.size();
	        for (int i = 0; i < length; i++) {
	            Log.e("SupportedPictureSizes","SupportedPictureSizes : " + pictureSizes.get(i).width + "x" + pictureSizes.get(i).height);
	        }

	        List<android.hardware.Camera.Size> previewSizes = params.getSupportedPreviewSizes();
	        length = previewSizes.size();
	        for (int i = 0; i < length; i++) {
	            Log.e("SupportedPreviewSizes","SupportedPreviewSizes : " + previewSizes.get(i).width + "x" + previewSizes.get(i).height);
	        }

			break;
			
		case ESC:
			return;
		}
		// 支持双码模式 20200714 0不支持，1：支持单码，2：支持双码
		int scanKey = gui.cls_show_msg("测试人员确认固件是否支持多码同图时可扫出多码,，支持的话，(测试[确认]，不测试[其他])，不支持[其他]");
		switch (scanKey) {
		case ENTER:
			nlsPara.setScanSet(2);
			break;
			
		case ESC:
			nlsPara.setScanSet(0);
			break;
			
		default:
			nlsPara.setScanSet(1);
			break;
		}
		// 配置的标志
		initFlag = true;
	}

	/**
	 * 配置硬解模式：单次、连续、手动
	 */
	public void config_Hard_Mode()
	{
		
		int nkeyIn = gui.cls_show_msg("0.单次扫码\n1.连续扫码\n2.手动扫码\n");
		switch (nkeyIn) 
		{
		case '0':
			scan_Mode = Scan_Mode.MODE_ONCE;
			break;
				
		case '1':
			scan_Mode = Scan_Mode.MODE_CONTINUALLY;
			break;
				
		case '2':
			scan_Mode = Scan_Mode.MODE_MANUALLY;
			break;
				
		case ESC:
			return;

		default:
			return;
		}

	}
	
	/**
	 * NLS参数配置：前 后置摄像头，编码格式以及附加码开关
	 */
	public void cameIdConfig()
	{
		myactivity.runOnUiThread(new Runnable() 
		{
			
			@Override
			public void run() {
				new ShowDialog().configNlsPara(myactivity, (NlsPara) nlsPara,mScanDefineInfo.cameraReal);
			}
		});
		synchronized (nlsPara) {
			try {
				nlsPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mCameraId = nlsPara.getCameraId();
		mCameraMsg=nlsPara.getCameraMsg();
		LoggerUtil.d("mCameraId="+mCameraId);
		/**控制是否有预览画面*/
		if(mCameraId==mScanDefineInfo.cameraReal.get(BACK_CAMERA)||mCameraId==mScanDefineInfo.cameraReal.get(EXTERNAL_CAMERA))
		{
			LoggerUtil.d("cameIdConfig Back Preview,cameraId="+mCameraId);
			mIsPreview=true;
		}	
		if((mCameraId==mScanDefineInfo.cameraReal.get(FONT_CAMERA)&&mScanDefineInfo.getCameraCnt()==1)||mCameraId==mScanDefineInfo.cameraReal.get(USB_CAMERA))
		{
			LoggerUtil.d("cameIdConfig Font Preview,cameraId="+mCameraId);
			mIsPreview=true;
		}
			
		nlsPara.setPreview(mIsPreview);
		LoggerUtil.e("BACK_CAMERA,"+mScanDefineInfo.cameraReal.get(BACK_CAMERA));
		LoggerUtil.e("FONT_CAMERA,"+mScanDefineInfo.cameraReal.get(FONT_CAMERA));
		LoggerUtil.e("EXTERNAL_CAMERA,"+mScanDefineInfo.cameraReal.get(EXTERNAL_CAMERA));
		LoggerUtil.e("USB_CAMERA,"+mScanDefineInfo.cameraReal.get(USB_CAMERA));
	}
	
	/**
	 * 压力选择
	 */
	public void scanPreChoose() 
	{
		while(true)
		{
			//3.扫码压力无预览框\n
			int nKeyin = gui.cls_show_msg("0.扫码压力\n1.流程压力\n2.休眠唤醒压力\n4.模拟用户扫码压力\n5.机械臂扫码压力\n6.机械臂流程压力\n");
			switch (nKeyin) 
			{
			case '0':
//				gui.cls_show_msg1(3, "压测不要超过300次");
				scanPre();
				break;
				
			case '1':
//				gui.cls_show_msg1(3, "压测不要超过300次");
				scanFlowPre();
				break;
			case '2':
				//20190614 zsh 新增
				gui.cls_show_msg("本测试项前后置均需要测试,任意键继续.");
				sleepWakePre();
				break;
				
		/*	case '3':
				scanPre();
				break;*/
			case '4':
				scanPre4();
				break;
			case '5':
				scanPre5();
				break;
			case '6':
				scanPre6();
				break;
			case ESC:
				return;

			default:
				continue;
			}
		}
	}
	/**
	 * 机械臂流程压力 by weimj 20191017
	 */
	private void scanPre6()
	{
		String funcName = "scanPre6";
		int tempCameraId = mCameraId;
		long startTime;
		StringBuffer scanresult = new StringBuffer();
		byte[] recbuf;
        recbuf = new byte[8];
        byte[] sendbuf = {0xF,0xF,0xF,0xF};
        String str = null;
        AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
        
        /*测试前置*/
		if(initFlag==false)
		{
			gui.cls_show_msg1(g_keeptime, "参数未配置");
			return;
		}
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");

		}*/
//		releaseScan(scan_Mode);
	
		// 打开串口
		if((ret = usbComm.open())<=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}		
		
		gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处，任意键继续", mCameraMsg);
		gui.cls_show_msg1(2, "打开PC中透传工具发送指令");
		
		int j = 0;
		while(true){
		Arrays.fill(recbuf, (byte) 0x00);
		int ret = 0;
		usbComm.read(recbuf, 15, 10);
		String data = new String(recbuf);
		Log.e("data",""+data);
		if("10109900".equals(data)){
			str = new String("ffff");
			sendbuf=str.getBytes();
			if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
					usbComm.close();
					return;
				}
		}else if ("101099".equals(data.substring(0, 6))){	
			gui.cls_show_msg1(1, "正在进行第%d次扫码", j+1);
			int timeout = 0;
			try {
                 if (!TextUtils.isEmpty(data.substring(6, 8))) {
                     timeout = Integer.valueOf(data.substring(6, 8));
                     Log.e(TAG, "设置超时时间：" + timeout + "s");
                 }
             } catch (NumberFormatException e) {
                 e.printStackTrace();
             } 
			releaseScan(scan_Mode);
			// 扫码初始化
			initScanMode(nlsPara, scan_Mode, tempCameraId, timeout*1000);
			if(mScanDefineInfo.cameraReal.get(BACK_CAMERA)==tempCameraId||(mScanDefineInfo.cameraReal.get(FONT_CAMERA)==tempCameraId&&mScanDefineInfo.getCameraCnt()==1))
			{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//有预览画面
			}else{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
			 startTime = System.currentTimeMillis();
			 if((ret = doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
				{
				 	str = new String("ffff");
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次扫描失败：%d", Tools.getLineInfo(),j,ret);
				}
				else{
					Tools.getStopTime(startTime);
					str = new String(scanresult);
//					gui.cls_show_msg1_record(TAG, "scanPre2", g_keeptime, "line %d:第%d次扫码结果：%s，扫码时间：%fs", Tools.getLineInfo(),j,scanresult,time);
				}
			 if(mIsPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			 releaseScan(scan_Mode);//释放扫码
			 sendbuf=str.getBytes();
			 if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
					usbComm.close();
					return;
				}
				j++;
			}
//		else if("101000".equals(data.substring(0, 6))){
//				break;
//			}
		}	
	}
	/*
	 * 机械臂扫码压力 by weimj 20190926
	 */
	private void scanPre5()
	{
		String funcName = "scanPre5";
		int tempCameraId=mCameraId;
		long startTime;
		StringBuffer scanresult = new StringBuffer();
		byte[] recbuf;
        recbuf = new byte[8];
        byte[] sendbuf = {0xF,0xF,0xF,0xF};
        String str = null;
        AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
		/*测试前置*/
		if(initFlag==false)
		{
			gui.cls_show_msg1(g_keeptime, "参数未配置");
			return;
		}
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");

		}*/
		releaseScan(scan_Mode);
		// 扫码初始化
		initScanMode(nlsPara, scan_Mode, tempCameraId, 30*1000);//超时时间设为30s
		if(scan_Mode!=Scan_Mode.NLS_picture){
			gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处，任意键继续", mCameraMsg);
			if(mIsPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		}
		
		// 打开串口
		if((ret = usbComm.open())<=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}	
		
		gui.cls_show_msg1(2, "打开PC中透传工具发送指令");
		int j = 0;
		while(true){
			
		Arrays.fill(recbuf, (byte) 0x00);
		int ret = 0;
		usbComm.read(recbuf, 15, 10);
		String data = new String(recbuf);
		Log.e("data",""+data);
		if("10109900".equals(data)){
			str = new String("ffff");
			sendbuf=str.getBytes();
			if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, "scanPre6", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
					usbComm.close();
					return;
				}
		}else if ("101099".equals(data.substring(0, 6))){
			j++;
			gui.cls_show_msg1(1, "正在进行第%d次扫码", j);
//			 int timeout = 8;
//			 try {
//                 if (!TextUtils.isEmpty(data.substring(6, 8))) {
//                     timeout = Integer.valueOf(data.substring(6, 8));
//                     Log.e(TAG, "设置超时时间：" + timeout + "s");
//                 }
//             } catch (NumberFormatException e) {
//                 e.printStackTrace();
//             } 
			 startTime = System.currentTimeMillis();
			 if((ret = doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
				{
				 	str = new String("ffff");
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次扫描失败：%d", Tools.getLineInfo(),j,ret);
				}
				else{
					Tools.getStopTime(startTime);
					str = new String(scanresult);
//					gui.cls_show_msg1_record(TAG, "scanPre1", g_keeptime, "line %d:第%d次扫码结果：%s，扫码时间：%fs", Tools.getLineInfo(),j,scanresult,time);
				}
			 sendbuf=str.getBytes();
			 if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, "scanPre5", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
					usbComm.close();
					return;
				}
			}else if("101000".equals(data.substring(0, 6))){
				break;
			}
		}
		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseScan(scan_Mode);//释放扫码
		scanresult = null;
		nlsPara.setPreview(true);// 测试后置开启预览扫码
	}
	/*
	 * 休眠唤醒压力
	 */
	private void sleepWakePre() {
		String funcName="sleepWakePre";
		int tempCameraId = mCameraId;
		
		int preSreenTimeout = Tools.getSreenTimeout(myactivity);
		
		/**单个区间扫码次数*/
		int scanTest_Times=0,allscan_times=0;
		
		int SleepWake_Times=100;//休眠唤醒次数
		
		int SleepTime=30;//单次休眠时长
		
		int scanFalse_Times=0;//前置失败次数统计
//		int changeScanFalse_Times=0;//后置失败次数统计
		
		PowerManager pm = (PowerManager) myactivity.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright"); //唤醒屏幕
//		SettingsManager settingsManager = (SettingsManager)myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		StringBuffer scanresult = new StringBuffer();
		
		
		gui.cls_show_msg("休眠唤醒/前置扫码自动压力测试,本案例测试时长较长,建议用自检调整固定设备在选择的摄像头可正常扫码(任意二维码均可,[配置为多码同图请放置/SVN/scan/mul_qr.png])的位置后进入案例,完成后任意键继续");
		SleepTime=(gui.JDK_ReadData(TIMEOUT_INPUT, 30,"设置最大单次休眠时长(1-60min,默认30min)"));//设置单次休眠时长(默认30min)
		LoggerUtil.i("sleepWakePre,SleepTime="+SleepTime);
		
		/**休眠唤醒次数作用:*/
		SleepWake_Times=(gui.JDK_ReadData(TIMEOUT_INPUT, 50,"设置休眠唤醒次数(1-100,默认50),请结合休眠时长设置此值,控制最大测试时间,夜间测试请保证光线充足,电量充足,完成后任意键开始自动测试,"));//设置休眠唤醒次数(默认50)
		LoggerUtil.i("sleepWakePre,SleepWake_Times="+SleepWake_Times);
		
		if(mScanDefineInfo.getCameraId()==-1)
		{
			gui.cls_show_msg1(3,"该设备无可用摄像头可扫码,即将退出测试");
			return;
		}
		//进行扫码
		releaseScan(scan_Mode);//释放扫码
		for(int j=1;j<=SleepWake_Times;j++)
		{
			/**测试前置，休眠设置为永不休眠【Poynt产品不支持该接口】*/
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
//			{
//				settingsManager.setScreenTimeout(-1);// 设置为永不休眠
//			}
//			else
//				gui.cls_show_msg("【Poynt】请手动将休眠时间设置为永不休眠,设置完毕任意键继续");
			Tools.setSreenTimeout(myactivity, 30*60*1000);
			LoggerUtil.v("getScreen Time="+Tools.getSreenTimeout(myactivity));
				
			
			initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
			if(mIsPreview)
			{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//后置有预览
			}else{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}

			//单个休眠唤醒区间多次扫码
			scanTest_Times=(int)(1+Math.random()*SleepTime)*2;//单个休眠唤醒区间扫码的次数,随机(1~SleepTime)*2
			LoggerUtil.i("sleepWakePre,scanTest_Times="+scanTest_Times);
			
			allscan_times+=scanTest_Times;//统计累计扫码次数
			
			for(int i=1;i<=scanTest_Times;i++){
				gui.cls_printf(String.format("共%d次扫码测试，正在进行第%d次", scanTest_Times,i).getBytes());
				ret=robot_doScan(scan_Mode, scanresult, nlsPara);
				if(ret!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:%s第(%d)次休眠唤醒第(%d)次扫码测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM,j,i, ret);
					scanFalse_Times++;
				}
				SystemClock.sleep(1000);//每次扫码之间间隔1s
				LoggerUtil.i("for,i="+i);
			}
			releaseScan(scan_Mode);//释放扫码	
			
			/**扫码结束后进行休眠唤醒,15s后进入休眠*/
			int total = (int)(1+Math.random()*SleepTime)*60;//休眠10~SleepTime内的随机时长(min)
			LoggerUtil.d("sleepWakePre,total="+total);
			boolean ret1;
			
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
//			{
//				if ((ret1 = settingsManager.setScreenTimeout(15)) == false) {
//					gui.cls_show_msg1_record(TAG,funcName,g_keeptime, "line %d:%s设置休眠时间失败(%s)", Tools.getLineInfo(), TESTITEM, ret1);
//				}
//			}
//			else
//				gui.cls_show_msg("【Poynt】请手动将休眠时间设置为15s,设置完毕后任意键继续");
			Tools.setSreenTimeout(myactivity, 15*1000);

			gui.cls_show_msg1(15, "15s后进入休眠,%d分钟后自动唤醒屏幕【不必纠结休眠时间的准确性】",total/60);
			//根据获取的随机时间唤醒屏幕
			while (true) {
				if (total <= 0) {
					wakeLock.acquire();	// 唤醒屏幕，释放wakeLock
					wakeLock.release();
					break;
				} else {
					 gui.cls_show_msg1(60, "即将唤醒屏幕"+total+"s");
					 total = total - 60;
				}
			}
			
			Tools.setSreenTimeout(myactivity, 30*60*1000);
			LoggerUtil.i("休眠唤醒屏幕");
			// 休眠唤醒后继续扫码
			initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
			if(mIsPreview){
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//后置有预览
			}else{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
			gui.cls_show_msg1(1,"正在进行第(%d)次切换扫码",j);
			if((ret=robot_doScan(scan_Mode, scanresult, nlsPara))!=NDK_SCAN_OK){
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:%s休眠唤醒切换扫码测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM, ret);
				scanFalse_Times++;
			}
			releaseScan(scan_Mode);//释放扫码
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//恢复界面显示	
		gui.cls_show_msg1_record(TAG, funcName, g_time_0,"休眠唤醒压力扫码测试结束,共进行(%d)次扫码,失败(%d)次",allscan_times,scanFalse_Times);
		
		/**测试后置,设置为永不休眠*/
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
//			settingsManager.setScreenTimeout(-1);
//		else
//			gui.cls_show_msg("[Poynt]请手动恢复设备休眠时间");
		Tools.setSreenTimeout(myactivity, preSreenTimeout);
	}

	/*
	 * 扫码压力
	 */
	public void scanPre() 
	{
		/*private & local definition*/
		int tempCameraId=mCameraId;
		int cnt = 0,i=0,succ = 0,ret = -1;
		StringBuffer scanresult = new StringBuffer();
		//自动化参数
		byte[] header = new byte[1024];
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();
		byte[] rbuf = new byte[200];
		int rlen = 0;
		String str = null;
		String temp = null;
		
		/*process body*/
		// 设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUTN));
		cnt = packet.getLifecycle();
		
		if(autoFlag){
			//选择自动
			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
			{
				layerBase.linkDown(wifiPara, type);
				gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:WIFI_NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
			{
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:TransUp失败(ret = %d)",Tools.getLineInfo(),ret);
				return;
			}
			header = "start".getBytes();
			if(sendByte(header,sock_t,type)==NDK_ERR){
				gui.cls_show_msg("line %d:socket通讯错误,任意键退出测试", Tools.getLineInfo());
				return;
			}
			temp = cnt+"";
			header = temp.getBytes();
			sendByte(header,sock_t,type);
		}
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");
		}*/
		
		if(scan_Mode!=Scan_Mode.NLS_picture){
			gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处[配置为多码同图请放置/SVN/scan/mul_qr.png]，任意键继续", mCameraMsg);
			// 因为前置摄像头没有预览画面故去除前置摄像头的预览画面 modify by zhengxq 20170704
			if(mIsPreview){
				LoggerUtil.d("mIsPreview="+mIsPreview);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			}
		}
		releaseScan(scan_Mode);
		LoggerUtil.e("scanPre1=当前cameraid=="+tempCameraId);
		initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "正在进行第%d次扫码,已扫码成功%d次,[取消]退出测试", i+1,succ)==ESC)
				break;
			i++;
			cnt--;
			scanresult.setLength(0);
			if(autoFlag){
				// 自动扫码操作
				if((ret = robot_doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:%s扫码失败 (扫码结果：%s，ret = %d)", Tools.getLineInfo(),TESTITEM,scanresult,ret);
					//continue;
				}
				
				temp = i+"";
				header = temp.getBytes();
				if(sendByte(header,sock_t,type)==NDK_ERR){
					gui.cls_show_msg("line %d:socket通讯错误,任意键退出测试", Tools.getLineInfo());
					break;
				}
				
				//接收数据
				Arrays.fill(rbuf, (byte) 0x00);
				try 
				{
					gui.cls_show_msg1(1,"数据接收中...\n%s:%d->%s:%d\n",wifiPara.getServerIp(),wifiPara.getServerPort(),wifiPara.getLocalIp(),wifiPara.getLocalPort());
					if((rlen = socketUtil.receiveScan(sock_t,rbuf)) < 0)
					{
						layerBase.transDown(socketUtil, sock_t);
						gui.cls_show_msg1_record(TAG, "scanPre11",g_keeptime,"line %d:接收数据失败(rlen = %d)",Tools.getLineInfo(),rlen);
						break;
					}
					str = new String(rbuf, "gbk").trim();
				} catch (Exception e) {
					e.printStackTrace();
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:接收数据失败(rlen = %d)",Tools.getLineInfo(),rlen);
					break;
				}
				if (!str.equals(scanresult.toString())){
					gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:扫码结果对比失败 (扫码结果：%s，接受结果 = %s)", Tools.getLineInfo(),scanresult,str);
					continue;
				} 
			} else{
				// 手动扫码操作
				if((ret = doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:%s扫码失败 (扫码结果：%s，ret = %d)", Tools.getLineInfo(),TESTITEM,scanresult,ret);
					continue;
				}
			}
			succ++;
		}
		// 测试后置
		if(autoFlag){
			//选择自动
			header = ("exit").getBytes();;
			sendByte(header,sock_t,type);
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		}
		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		
		releaseScan(scan_Mode);
		gui.cls_show_msg1_record(TAG, "scanPre1",g_time_0,"扫码压力测试完成，已执行次数为%d，成功为%d次", i,succ);
		scanresult = null;
		nlsPara.setPreview(true);// 测试后置开启预览扫码
	}
	
	/**
	 * 流程压力
	 */
	public void scanFlowPre() 
	{	
	
		/*private & local definition*/
		int tempCameraId = mCameraId;
		int cnt = 0,i=0,succ = 0,ret = -1;
		StringBuffer scanresult = new StringBuffer();
		//自动化参数
		byte[] header = new byte[1024];
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();
		byte[] rbuf = new byte[200];
		int rlen = 0;
		String str = null;
		String temp = null;
		
		/*process body*/
		// 设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUTN));
		cnt = packet.getLifecycle();
		
		Log.d("eric", "1");
		if(autoFlag){
			//选择自动
			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
			{
				layerBase.linkDown(wifiPara, type);
				gui.cls_show_msg1_record(TAG, "scanPre11",g_keeptime,"line %d:WIFI_NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
			{
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "scanPre11",g_keeptime,"line %d:TransUp失败(ret = %d)",Tools.getLineInfo(),ret);
				return;
			}
			header = "start".getBytes();
			if(sendByte(header,sock_t,type)==NDK_ERR){
				gui.cls_show_msg("line %d:socket通讯错误,任意键退出测试", Tools.getLineInfo());
				return;
			}
			temp = cnt+"";
			header = temp.getBytes();
			sendByte(header,sock_t,type);
		}
		if(scan_Mode!=Scan_Mode.NLS_picture)
		{	// 硬解码放置前置摄像头，软解码放置后置摄像头
			gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处[配置为多码同图请放置/SVN/scan/mul_qr.png]，任意键继续", mCameraMsg);
		}
		
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");

		}*/
//		handler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "正在进行第%d次扫码，已扫码成功%d次，【取消】退出测试", i+1,succ)==ESC)
				break;
				
			i++;
			cnt--;
			initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
			gui.cls_printf("扫码初始化完毕".getBytes());
			if(mIsPreview)
			{
				// 打开解析框
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
				LoggerUtil.d( "scanPre2====2");
			}
			scanresult.setLength(0);
			if(autoFlag){
				// 自动扫码操作
				if((ret = robot_doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(TAG, "scanPre1",g_keeptime,"line %d:%s扫码失败 (扫码结果：%s，ret = %d)", Tools.getLineInfo(),TESTITEM,scanresult,ret);
					//continue;
				}
				
				temp = i+"";
				header = temp.getBytes();
				if(sendByte(header,sock_t,type)==NDK_ERR){
					gui.cls_show_msg("line %d:socket通讯错误,任意键退出测试", Tools.getLineInfo());
					break;
				}
				
				//接收数据
				Arrays.fill(rbuf, (byte) 0x00);
				try 
				{
					gui.cls_show_msg1(1,"数据接收中...\n%s:%d->%s:%d\n",wifiPara.getServerIp(),wifiPara.getServerPort(),wifiPara.getLocalIp(),wifiPara.getLocalPort());
					if((rlen = socketUtil.receiveScan(sock_t,rbuf)) < 0)
					{
						layerBase.transDown(socketUtil, sock_t);
						gui.cls_show_msg1_record(TAG, "scanPre11",g_keeptime,"line %d:接收数据失败(rlen = %d)",Tools.getLineInfo(),rlen);
						break;
					}
					str = new String(rbuf, "gbk").trim();
				} catch (Exception e) {
					e.printStackTrace();
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG, "scanPre11",g_keeptime,"line %d:接收数据失败(rlen = %d)",Tools.getLineInfo(),rlen);
					break;
				}
				if (!str.equals(scanresult.toString())){
					gui.cls_show_msg1_record(TAG, "scanPre11",g_keeptime,"line %d:扫码结果对比失败 (扫码结果：%s，接受结果 = %s)", Tools.getLineInfo(),scanresult,str);
					releaseScan(scan_Mode);
					continue;
				} 
			} else{
			// 扫码操作 
				if((ret = doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(TAG, "scanPre2",g_keeptime,"line %d:%s扫码失败(扫码结果：%s，ret = %d)", Tools.getLineInfo(),TESTITEM,scanresult,ret);
					releaseScan(scan_Mode);
					continue;
				}
			}
			if(mIsPreview)
			{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
//			layScanView.setVisibility(View.GONE);
			Log.d("eric", "3");

			releaseScan(scan_Mode);
			succ++;
		}
		// 测试后置
		if(autoFlag){
			//选择自动
			header = ("exit").getBytes();
			sendByte(header,sock_t,type);
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		}
//		releaseScan(scan_Mode);
//		if(cameraId==0)
//			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//		releaseScan(scanUtil,scan_Mode);
		gui.cls_show_msg1_record(TAG, "scanPre2",g_time_0,"流程压力测试完成，已执行次数为%d，成功为%d次", i,succ);
		scanresult = null;
	}
	
	public void ability(){
		if(gui.cls_show_msg("请确保机具不会进入休眠，[确定]继续，[取消]退出")==ESC)
			return;
		while(true)
		{
			int nKeyin = gui.cls_show_msg("0.反复扫码性能\n1.打开关闭摄像头性能（新API）\n2.打开关闭摄像头性能（旧API）\n3.扫码流程性能\n" +
					"4.前置切换后置测试扫码性能\n5.机械臂扫码性能\n6.机械臂扫码流程性能\n7.开机第一次扫码性能\n");
			switch (nKeyin) 
			{
			case '0':
				scanAbility();
				break;
				
			case '1':
				openCloseCameraAbilityNew();
				break;
				
			case '2':
				openCloseCameraAbilityOld();
				break;
				
			case '3':
				//20190614 zsh 新增
//				if(chooseCameraId()==ESC){
//					return;
//				}
				openCameraDoScanAbility();
				break;
				
			case'4':
				backAbility();
				break;
				
			case'5':
				//by wmj
				robotScanAbility();
				break;
				
			case'6':
				//by wmj
				robotScanProcessAbility();
				break;
				
			case '7':
				firstBootAbility();
				break;
				
			case ESC:
				return;
				
			default:
				continue;
			}
		}
	}
	
	// 机械臂扫码流程性能
	private void robotScanProcessAbility() {

		/* private & local definition */
		int tempCameraId=mCameraId;
		long startTime = 0;
		long start = 0;
		int begin = 5;
		int cnt = 10;
		float delay = 0;
		int maxdst = 54;// 最大距离
		int maxDelay = 50;// 超时时间

		byte[] recbuf;
		recbuf = new byte[8];
		byte[] sendbuf = { 0xF, 0xF, 0xF, 0xF };
		String str = null;
		AnalogSerialManager usbComm = (AnalogSerialManager) myactivity
				.getSystemService(ANALOG_SERIAL_SERVICE);

		/* process body */
		/* 测试前置 */
		if (initFlag == false) {
			gui.cls_show_msg1(g_keeptime, "参数未配置");
			return;
		}
		if (robotFlag == false) {
			gui.cls_show_msg1(g_keeptime, "机械臂未连接");
			return;
		}
		
		// 打开串口
		// if((ret = usbComm.open())<=NDK_OK)
		// {
		// gui.cls_show_msg1_record(TAG, "robotScanProcessAbility", g_keeptime,
		// "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
		// usbComm.close();
		// return;
		// }
		// if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		// {
		// gui.cls_show_msg1_record(TAG, "robotScanProcessAbility", g_keeptime,
		// "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
		// usbComm.close();
		// return;
		// }

		// gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处，完成点任意键",
		// cameraId==0?"后":"前");
		// 设置初始距离
		gui.cls_show_msg2(1f, "设置初始距离,单位cm");
		final PacketBean packet1 = new PacketBean();
		packet1.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, begin));
		begin = packet1.getLifecycle();

		gui.cls_show_msg2(1f, "设置最大距离,单位cm");
		final PacketBean packet2 = new PacketBean();
		packet2.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, maxdst));
		maxdst = packet2.getLifecycle();

		cnt = maxdst - begin;

		while (true) {

			ArrayList<Float> average = new ArrayList<>();
			StringBuffer scanresult = new StringBuffer();
			gui.cls_show_msg1(2, "打开PC中透传工具发送指令");

			int i = 0;
			while (i <= cnt) {
				float time = 0;
				Arrays.fill(recbuf, (byte) 0x00);
				int ret = 0;
				usbComm.read(recbuf, 15, 10);
				String data = new String(recbuf);
				Log.e("data", "" + data);

				// if("10109900".equals(data)){
				// str = new String("ffff");
				// sendbuf=str.getBytes();
				// if((ret = usbComm.write(sendbuf, sendbuf.length,
				// 15))!=sendbuf.length)
				// {
				// gui.cls_show_msg1_record(TAG, "robotScanProcessAbility",
				// g_keeptime, "line %d:usb串口发送数据失败(%d)",
				// Tools.getLineInfo(),ret);
				// // usbComm.close();
				// // return;
				// }
				// }else
				if ("101099".equals(data.substring(0, 6))) {
					start = System.currentTimeMillis();
					int count = 20;
					int bak = 20;
					int succ = 0;

					while (count > 0) {
						count--;
						gui.cls_show_msg2(0.1f, "距离%d cm第%d次扫码", begin + i, bak
								- count);
						startTime = System.currentTimeMillis();
						// 初始化
						initScanMode(nlsPara, scan_Mode, tempCameraId, TIMEOUT);
						if (mIsPreview) {
							handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
						}
						if ((ret = robot_doScan(scan_Mode, scanresult, nlsPara)) != NDK_SCAN_OK) {
							str = new String("ffff");
							// gui.cls_show_msg1_record(TAG,
							// "robotScanProcessAbility", g_keeptime,
							// "line %d:第%d次连续扫码失败(扫码结果：%s,ret = %d)",
							// Tools.getLineInfo(),bak-count,scanresult,ret);
							// releaseScan(scan_Mode);
							// if(cameraId==0||GlobalVariable.currentPlatform ==
							// Model_Type.F7)
							// handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
							// return;
						} else {
							str = new String(scanresult);
							succ++;
						}

						if (mIsPreview)
							handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
						releaseScan(scan_Mode);
						LoggerUtil.i("scan time:"
								+ Tools.getStopTime(startTime));
						time += Tools.getStopTime(startTime);
						delay = Tools.getStopTime(start);
						if (delay > maxDelay) {
							gui.cls_only_write_msg(TAG,"robotScanProcessAbility","距离%d cm扫码超时，实际扫码%d次,扫码成功%d次", begin + i,bak - count, succ);
							str = "ffff";
							break;
						}
					}
					average.add(time / succ);
					// gui.cls_show_msg1(2, "%d cm处平均每次扫码时间%fs/次",
					// begin+i,time/20);
					gui.cls_only_write_msg(TAG, "robotScanProcessAbility","距离%d cm处平均每次扫码时间%fs/次", begin + i, time / succ);

					i++;
					sendbuf = str.getBytes();
					if ((ret = usbComm.write(sendbuf, sendbuf.length, 15)) != sendbuf.length) {
						gui.cls_show_msg1_record(TAG,"robotScanProcessAbility", g_keeptime,"line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
						// usbComm.close();
						// return;
						continue;
					}

				}
			}

			// String s = "\n";
			gui.cls_show_msg1(2, "测试结束");
			// for(int j=0;j<average.size();j++){
			// int dep = begin+j;
			// LoggerUtil.e(""+average.get(j));
			// s = s+"距离"+dep+"cm平均扫码时间"+average.get(j)+"s\n";
			// // gui.cls_show_msg1_record(TAG, "robotScanProcessAbility",
			// g_time_0, "位置%d平均每次扫码时间%fs/次", j+1,average.get(j));
			// }
			// // gui.cls_show_msg1_record(TAG, "robotScanProcessAbility",
			// g_time_0, s);
			// gui.cls_only_write_msg(TAG, "robotScanProcessAbility", s);
			scanresult = null;
		}
	}


	//机械臂扫码性能
	private void robotScanAbility(){
		
		/*private & local definition*/
		int tempCameraId = mCameraId;
		long startTime = 0;
		long start = 0;
		int cnt = 10;
		int begin = 5;
		float delay = 0;
		int maxdst = 54;//最大距离
		int maxDelay = 59;//超时时间
		
		
		byte[] recbuf;
        recbuf = new byte[8];
        byte[] sendbuf = {0xF,0xF,0xF,0xF};
        String str = null;
        AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
        
		/*process body*/
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");

		}*/
		
		// 打开串口
		if((ret = usbComm.open())<=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robotScanAbility", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robotScanAbility", g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}	
		
		// 设置初始距离
		gui.cls_show_msg2(2f,"设置初始距离,单位cm");
		final PacketBean packet1 = new PacketBean();
		packet1.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, begin));
		begin = packet1.getLifecycle();
		
		gui.cls_show_msg2(2f,"设置最大距离,单位cm");
		final PacketBean packet2 = new PacketBean();
		packet2.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, maxdst));
		maxdst = packet2.getLifecycle();
		cnt = maxdst-begin;
		
		while(true){
		// 初始化
		initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
		if(mIsPreview){
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		}
		
		StringBuffer scanresult = new StringBuffer();
		ArrayList<Float> average = new ArrayList<>();
		
		gui.cls_show_msg1(2, "打开PC中透传工具发送指令");
		int i = 0;
		while(i<=cnt){
			float time = 0;
			Arrays.fill(recbuf, (byte) 0x00);
			int ret = 0;
			usbComm.read(recbuf, 15, 10);
			String data = new String(recbuf);
			Log.e("data",""+data);
			if("10109900".equals(data)){
				str = new String("ffff");
				sendbuf=str.getBytes();
				if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
					{
						gui.cls_show_msg1_record(TAG, "robotScanAbility", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
//						usbComm.close();
//						if(cameraId==0||GlobalVariable.currentPlatform == Model_Type.F7)
//							handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//						releaseScan(scan_Mode);
//						return;
					}
			}else if ("101099".equals(data.substring(0, 6))){
				start = System.currentTimeMillis();
				int count=20;
				int bak=20;
				int succ=0;
				
				while (count>0) {
					count--;
				
				gui.cls_show_msg2(0.1f,"距离%d cm第%d次扫码",begin+i,bak-count);
				startTime = System.currentTimeMillis();
				if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
				{
					str = new String("ffff");
					LoggerUtil.i("ffff");
//					gui.cls_show_msg1_record(TAG, "robotScanAbility", g_keeptime, "line %d:第%d次连续扫码失败(扫码结果：%s,ret = %d)", Tools.getLineInfo(),bak-count,scanresult,ret);
//					releaseScan(scan_Mode);
//					if(cameraId==0||GlobalVariable.currentPlatform == Model_Type.F7)
//						handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//					return;
				}else{
					str = new String(scanresult);
					succ++;
					LoggerUtil.i("scan time:"+Tools.getStopTime(startTime));
					time +=Tools.getStopTime(startTime);
				}

				delay = Tools.getStopTime(start);
				if(delay>maxDelay)
					{
					gui.cls_only_write_msg(TAG, "robotScanAbility", "距离%d cm扫码超时，实际扫码%d次,扫码成功%d次",begin+i,bak-count,succ);
					str = "ffff";
						break;
					}
				}
				
				
				average.add(time/succ);
//				gui.cls_show_msg1(2, "%d cm处平均每次扫码时间%fs/次", begin+i,time/20);
				gui.cls_only_write_msg(TAG, "robotScanAbility", "距离%d cm处平均每次扫码时间%fs/次", begin+i,time/succ);
				 sendbuf=str.getBytes();
				 if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, "robotScanAbility", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
//					usbComm.close();
//					if(cameraId==0||GlobalVariable.currentPlatform == Model_Type.F7)
//						handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//					releaseScan(scan_Mode);
//					return;
				}
				i++;
			}
		}

		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseScan(scan_Mode);
		
//		String s = "\n";
		gui.cls_show_msg1(2, "测试结束");
//		for(int j=0;j<average.size();j++){
//			int dep = begin + j;
//			LoggerUtil.e(""+average.get(j));
//			s = s+"距离"+dep+"cm平均扫码时间"+average.get(j)+"s\n";
//		}
////		gui.cls_show_msg1_record(TAG, "robotScanAbility", g_time_0, s);
//		gui.cls_only_write_msg(TAG, "robotScanAbility", s);
		scanresult = null;
		
	}
		
	}
	//前置切换成后置以后的扫码性能
	private void backAbility() {
		if(mScanDefineInfo.getCameraCnt()<2||GlobalVariable.currentPlatform==Model_Type.X5)
		{
			gui.cls_show_msg1(3, "该产品不支持扫描头切换功能");
			return;
		}
		StringBuffer scanresult = new StringBuffer();
		int ret;
		long startTime;
		int cnt = 50;
		float time = 0;
//		if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
//			 isnewscan=0;	
//			 gui.cls_show_msg1(1,"修改成功");
//
//		}
		// 测试前置：释放扫码
		releaseScan(scan_Mode);
		gui.cls_show_msg("请将条形码或二维码放置于前置摄像头20-30cm处，完成点任意键");
		initScanMode(nlsPara,scan_Mode, 1, TIMEOUT);
		if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(TAG, "backAbility", g_keeptime, "line %d:扫码失败(扫码结果：%s,ret = %d)", Tools.getLineInfo(),scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		gui.cls_show_msg2(0.1f,"前置扫码成功---结果为%s",scanresult);
		releaseScan(scan_Mode);
		
		gui.cls_show_msg("请将条形码或二维码放置于后置摄像头20-30cm处，完成点任意键");
		// 初始化
		initScanMode(nlsPara,scan_Mode, 0, TIMEOUT);
		if(mIsPreview){
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		}
		Log.d("eric", "cameraId===="+mCameraId);
		gui.cls_show_msg2(0.1f,"等待摄像头对焦。。。。。。。。。。。。。");
		SystemClock.sleep(2900);
					
		for (int i = 0; i < cnt; i++) 
		{
			gui.cls_show_msg2(0.1f,"第%d次扫码",i+1);
			startTime = System.currentTimeMillis();
			if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(TAG, "backAbility", g_keeptime, "line %d:第%d次连续扫码失败(扫码结果：%s,ret = %d)", Tools.getLineInfo(),i+1,scanresult,ret);
				releaseScan(scan_Mode);
				if(mIsPreview){
					handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				}
				return;
			}
			LoggerUtil.i("scan time:"+Tools.getStopTime(startTime));
			// 连续扫码需要减去每次间隔的1s时间，其他扫码是不需要减的
			time +=scan_Mode==Scan_Mode.MODE_CONTINUALLY? Tools.getStopTime(startTime)-9:Tools.getStopTime(startTime);
		}
		time = scan_Mode==Scan_Mode.MODE_CONTINUALLY?time/(cnt*10):time/cnt; // 每次扫码时间
		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseScan(scan_Mode);
		gui.cls_show_msg1_record(TAG, "backAbility", g_time_0, "平均每次扫码时间%fs/次", time);
	}

	//扫码性能测试(开启摄像头,到扫码结束释放的时间)
	private void openCameraDoScanAbility() {
		int tempCameraId = mCameraId;
		int ret=-1;
		int cnt = 0, bak = 0;
		float Scantime2=0.0f;
//		int DoScanTime;//设置扫码次数
		StringBuffer scanresult = new StringBuffer();
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");

		}*/
//		if(gui.cls_show_msg("请确认是否用640*480扫码框。是[确认],否[其他]")==ENTER){
//			 isnewscan=1;	
//			 gui.cls_show_msg1(1,"修改成功");
//
//		}
		gui.cls_show_msg("请将任意码制放在摄像头20-30cm处(此时无预览,点击后显示),完成后任意键继续进行扫码");
		while (cnt>0) {
			cnt--;
			gui.cls_show_msg1(1,"正在进行第%d次自动扫码,剩余%d次",bak-cnt,cnt);
			long oldtime2 = System.currentTimeMillis();//获取当前时间
			initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
			if(mIsPreview){
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			}else{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
			Log.d("eric", "cameraId==="+tempCameraId);

			ret=robot_doScan(scan_Mode, scanresult, nlsPara);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "openCameraDoScanAbility()", g_keeptime,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}

			releaseScan(scan_Mode);//释放扫码
			Scantime2=Scantime2+Tools.getStopTime(oldtime2);
			if(mIsPreview){
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
			SystemClock.sleep(1000);
		}
//		if(mIsPreview){
//			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//		}
//		for(int i=1;i<=DoScanTime;i++){
//			//初始化扫码为后置,打开摄像头
//			oldtime = System.currentTimeMillis();//获取当前时间
//			initScanMode(nlsPara,scan_Mode, cameraId, TIMEOUT);
//			if(cameraId==0){
//				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
//			}else{
//				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//			}
//			gui.cls_show_msg1(1,"正在进行第%d次自动扫码,剩余%d次",i,DoScanTime-i);
//			ret=robot_doScan(scan_Mode, scanresult, nlsPara);
//			if(ret!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1_record(fileName, "openCameraDoScanAbility()", g_keeptime,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			releaseScan(scan_Mode);//释放扫码
//			if(i!=1){
//				Scantime=Scantime+Tools.getStopTime(oldtime);//获取所耗时间,累加,排除第一次因为无预览导致的时间差
//				}
//			SystemClock.sleep(1000);//休眠1s
//		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//恢复白色背景显示
		gui.cls_show_msg1_record(fileName, "openCameraDoScanAbility()", g_keeptime,"扫码性能测试结束,扫码性能为(%f)s/次",Scantime2/bak);
//		LoggerUtil.e("后置扫码性能为"+(Scantime-DoScanTime+1)/(DoScanTime-1)+"ms/次");//(总时间-1s提示的总时间+首次多减1s)/(总次数-第一次)
	
	}
	
	
	/*
	 * 扫码性能
	 */
	public void scanAbility() 
	{
		/*private & local definition*/
		int  tempCameraId = mCameraId;
		StringBuffer scanresult = new StringBuffer();
		int ret;
		long startTime;
		int cnt = 50;
		float time = 0;
		int succ=0;
		/*process body*/
		
		// 连续扫码
		switch (scan_Mode) {
//		case MODE_CONTINUALLY:
//			gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处,每次扫码后移开再放置,完成点任意键", mCameraMsg);
//			// 初始化
//			initScanMode(nlsPara,scan_Mode, cameraId, TIMEOUT);
//			if(cameraId==0)
//				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
//			for (int i = 0; i < 5; i++) 
//			{
//				gui.cls_show_msg("第%d次扫码,请移开再移入条形码/二维码,放置完毕点任意键",i+1);
//				initScanMode(nlsPara,scan_Mode, cameraId, TIMEOUT);
//				startTime = System.currentTimeMillis();
//				if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
//				{
//					gui.cls_show_msg1_record(TAG, "scanAbility", g_keeptime, "line %d:第%d次连续扫码失败（扫码结果：%s，ret = %d）", Tools.getLineInfo(),i+1,scanresult,ret);
//					releaseScan(scan_Mode);
//					if(cameraId==0)
//						handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
//					return;
//				}
//				time = time + Tools.getStopTime(startTime);
//			}
//			
//			time = (time-(5*9))/cnt;
//			break;
		case MODE_CONTINUALLY:
			cnt = 5;
		case MODE_MANUALLY:
		case MODE_ONCE:
		case ZXING:
		case NLS_0:
		case NLS_1:
			/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
				 isnewscan=0;	
				 gui.cls_show_msg1(1,"修改成功");

			}*/
			gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处，完成点任意键", mCameraMsg);
			// 初始化
			initScanMode(nlsPara,scan_Mode, mCameraId, TIMEOUT);
			if(mIsPreview){
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			}
		
			Log.d("eric", "cameraId===="+tempCameraId);
			gui.cls_show_msg2(0.1f,"等待摄像头对焦。。。。。。。。。。。。。");
			SystemClock.sleep(2900);
			
			for (int i = 0; i < cnt; i++) 
			{
				gui.cls_show_msg2(0.1f,"第%d次扫码",i+1);
				startTime = System.currentTimeMillis();
				if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(TAG, "scanAbility", g_keeptime, "line %d:第%d次连续扫码失败(扫码结果：%s,ret = %d)", Tools.getLineInfo(),i+1,scanresult,ret);
					releaseScan(scan_Mode);
					if(mIsPreview)
						handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
					return;
				}
				LoggerUtil.i("scan time:"+Tools.getStopTime(startTime));
				// 连续扫码需要减去每次间隔的1s时间，其他扫码是不需要减的
				time +=scan_Mode==Scan_Mode.MODE_CONTINUALLY? Tools.getStopTime(startTime)-9:Tools.getStopTime(startTime);
			}
			time = scan_Mode==Scan_Mode.MODE_CONTINUALLY?time/(cnt*10):time/cnt; // 每次扫码时间
			break;
			
		/*case NLS_picture:
			initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
			for (int i = 0; i < cnt; i++) 
			{
				gui.cls_show_msg1(1, "第%d次图片解码", i+1);
				startTime = System.currentTimeMillis();
				if((ret = doScan(scan_Mode, scanresult,nlsPara))!= NDK_SCAN_PARSE_SUCC) {
					gui.cls_show_msg1_record(TAG, "pictureDecodePress", g_keeptime, "line %d:第%d次图片传输失败（%d）", Tools.getLineInfo(),i+1,ret);
					continue;
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
					gui.cls_show_msg1_record(TAG, "pictureDecodePress", g_keeptime, "line %d:第%d次RGB解码失败（预期：%s，实际：%s）", Tools.getLineInfo(),i+1,pic[position][1],scanResult);
					continue;
				}
				succ++;
				time = time+Tools.getStopTime(startTime);
			}
			time = time/succ; 
			break;*/
		default:
			break;
		}
		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseScan(scan_Mode);
		gui.cls_show_msg1_record(TAG, "scanAbility", g_time_0, "平均每次扫码时间%fs/次", time);
		scanresult = null;
	}
	
	/**
	 * 开机第一次扫码性能
	 */
	public void firstBootAbility()
	{
		int tempCameraId = mCameraId;
		int ret = -1;
		float firstScanTime = 0.0f;
		StringBuffer scanresult = new StringBuffer();
		
		gui.cls_show_msg("【请确保是刚开机后运行本用例】请将任意码制放在摄像头20-30cm处(此时无预览,点击后显示),完成后任意键继续进行扫码");
		long startTime = System.currentTimeMillis();// 获取当前时间
		initScanMode(nlsPara, scan_Mode, tempCameraId, TIMEOUT);
		if (mIsPreview) {
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		} 

		ret = robot_doScan(scan_Mode, scanresult, nlsPara);
		if (ret != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "openCameraDoScanAbility()",g_keeptime, "line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		firstScanTime = Tools.getStopTime(startTime);
		releaseScan(scan_Mode);// 释放扫码
		if (mIsPreview) {
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		}
		gui.cls_show_msg1_record(fileName, "firstBootAbility",g_time_0, "开机第一次扫码性能=%4.4fs", firstScanTime);
	}
	
	/*
	 * 扫码异常:条形码和二维码切换 休眠异常
	 */
	public void scanAbnormal() 
	{
		while(true)
		{
			int nKeyin = gui.cls_show_msg("0.扫码休眠\n1.休眠扫码\n2.多线程_扫码释放\n3.前置扫码异常[硬解]\n4.切换扫码异常");

			switch (nKeyin) 
			{
			case '0':
				if(mIsPreview)
					handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
				abnomarl1();
				break;
				
			case '1':
				if(mIsPreview)
					handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
				abnomarl2();
				sleepAbnormal = false;
				break;
				
			case '2':
				// 多线程扫码释放异常
				thread_abnormal();
				break;
				
			case '3':
				hardSol_abnormal();
				break;
			//切换扫码异常 add by zsh 20190617
			case '4':
				if(mIsPreview)
					handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
				gui.cls_show_msg("切换扫码异常测试,前后置均需测试,任意键继续");
				change_abnormal();
				break;
			case ESC:
				if(mIsPreview)
					handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				return;
				
			default:
				continue;
			}
			if(mIsPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		}
		
	}
	/**
	 * 切换前后置扫码异常，切换摄像头不release,无法调用camera
	 */
	private void change_abnormal() 
	{
		int tempCameraId = mCameraId;
		StringBuffer scanresult = new StringBuffer();
		if(mScanDefineInfo.getCameraCnt()<2||GlobalVariable.currentPlatform==Model_Type.X5){
			gui.cls_show_msg("该设备不支持扫描头切换测试,即将退出");
			return;
		}
		gui.cls_show_msg("case1.1:切换前后置扫码异常异常测试中，请将任意二维码放置%s置摄像头处，预期应扫码成功,任意键继续",mCameraMsg);
//		//进行扫码
//		releaseScan(scan_Mode);//释放扫码
		//初始化
		initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
		//扫码
		ret=robot_doScan(scan_Mode, scanresult, nlsPara);
		if(ret!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(TAG, "change_abnormal", g_keeptime,"line %d:%s扫码测试失败", Tools.getLineInfo(), TESTITEM,mCameraMsg);
			releaseScan(scan_Mode);
			return;
		}
		SystemClock.sleep(1000);//每次扫码之间间隔1s
//			releaseScan(scan_Mode);//不进行释放,直接切换
		//切换摄像头扫码一次
		tempCameraId=tempCameraId==0?1:0;
		initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);//切换摄像头
		gui.cls_show_msg("case1.2:即将进行切换扫码,预期应扫码失败,请将任意二维码放置%s置摄像头处，任意键继续",tempCameraId==0?"后":"前");	
		if((ret=robot_doScan(scan_Mode, scanresult, nlsPara))==NDK_SCAN_OK){
			gui.cls_show_msg1_record(TAG, "change_abnormal", g_keeptime,"line %d:%s切换扫码测试失败,预期应扫码失败", Tools.getLineInfo(),TESTITEM);
			releaseScan(scan_Mode);
			return;
		}
		releaseScan(scan_Mode);//释放扫码
		if(mIsPreview){
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//恢复界面显示	
		gui.cls_show_msg1_record(TAG, "change_abnormal", g_keeptime,"切换扫码异常测试通过,请配置为%s再次测试本案例,已测过忽略",mCameraMsg);
	}

	/**
	 * 多线程扫码释放异常，复现成功
	 */
	public void thread_abnormal()
	{
		LoggerUtil.d("thread_abnormal,scan_Mode="+scan_Mode);
		// 超时时间设置小些，概率会大些，故超时时间设置为3s
		gui.cls_show_msg("多线程-扫码释放异常测试中，请将条码方式与%s置摄像头处，多次进入本子项测试，首次不放置条码可扫到码值视为测试不通过，任意键继续",mCameraMsg);
		if(mIsPreview){
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		}
		initScanMode(nlsPara,scan_Mode, mCameraId, 3000);
		ScanThread scanThread = new ScanThread();
		scanThread.start();
		try 
		{
			scanThread.join(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 释放扫码
		scanThread.toStop = true;
		releaseScan(scan_Mode);
		if(mIsPreview){
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		}
		gui.cls_show_msg1(2,"扫码已释放，可重新进入本子项");
	}
	
	/**
	 * 前置摄像头[硬件]异常测试
	 */
	private void hardSol_abnormal()
	{
		/**这里判断应该修改为具有两个以上的摄像头20200506*/
		int camCnt = mScanDefineInfo.getCameraCnt();
		if(camCnt<2)
		{
			gui.cls_show_msg1(3, "不支持测试硬解码扫码，即将退出");
			return;
		}
		// 无预览画面(nls扫码方式兼容硬解？)
		// 修改为带预览画面
//		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		ScanUtil softManager = new ScanUtil(myactivity, null, 1, false, 500, 1);
		softManager.doScan();
		softManager.release();
		// 清除前置扫码缓存
		softManager = null;
//		gui.cls_show_msg1(1, "前置摄像头[硬解]异常测试完毕");
		// 切换到硬解方式
		ScanUtil hardManager = new ScanUtil(myactivity);
		hardManager.init();
		gui.cls_printf("请放置二维码于前置摄像头".getBytes());
		String result = (String) hardManager.doScan();
		gui.cls_show_msg("扫码结果为：%s，任意键回到主菜单", result);
	}
	
	
	public void abnomarl1()
	{
		while(true)
		{
			int nkey2 = gui.cls_show_msg("0.软解码设备\n1.硬解码设备\n");
			switch (nkey2) 
			{
			case '0':
				abnomarl_soft1();
				sleepAbnormal = false;
				break;
				
			case '1':
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				try {
					abnomarl_hard1();
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(fileName, "abnomarl1", g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				sleepAbnormal = false;
				break;
				
			case ESC:
				return;

			default:
				continue;
			}
			
		}
	}
	
	/**
	 *扫码过程中进入休眠：软解码会接收到屏幕关闭状态后无法扫码屏幕唤醒后若doscan的超时时间还未结束则可继续扫码，若doscan的超时时间已结束则无法扫码，硬解码会在休眠的时候把串口直接关闭
	 */
	// 软解码设备
	public void abnomarl_soft1() 
	{
		int tempCameraId = mCameraId;
		sleepAbnormal = true;
		StringBuffer scanresult = new StringBuffer();
		int ret = -1;
		// 测试前置，开启预览画面以及释放扫码，注册休眠的广播
		// case1:休眠唤醒后doscan动作已经结束，应不影响下次扫码动作
		releaseScan(scan_Mode);
		initScanMode(nlsPara,scan_Mode, tempCameraId, 20*1000);
		gui.cls_show_msg1(1, "请移开条码，正在扫码手动进入休眠，请休眠2分钟后手动唤醒");
		// 返回值为F
		if ((ret = doScan( scan_Mode,scanresult,nlsPara)) != NDK_SCAN_FAULT) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_soft1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		while (!GlobalVariable.isWakeUp);
		// 唤醒
		gui.cls_show_msg1(1, "【屏幕已唤醒】将条形码或二维码放置在%s置摄像头20-30cm处",mCameraMsg);
		if ((ret = doScan( scan_Mode,scanresult,nlsPara)) != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_soft1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		releaseScan(scan_Mode);
		// case2:休眠唤醒后doscan超时时间未结束，仍可以继续扫码，并且不影响下次扫码
		initScanMode(nlsPara,scan_Mode, tempCameraId, 30*1000);
		gui.cls_show_msg1(1, "正在扫码手动进入休眠，请休眠10s后手动唤醒，唤醒后请放置条码在%s置摄像头处",mCameraMsg);
		// 应返回S
		if ((ret = doScan(scan_Mode,scanresult,nlsPara)) != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_soft1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s，ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		// 唤醒
		gui.cls_show_msg1(1, "将条形码或二维码放置在%s置摄像头20-30cm处",mCameraMsg);
		if ((ret = doScan(scan_Mode,scanresult,nlsPara)) != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_soft1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s，ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		releaseScan(scan_Mode);
		gui.cls_show_msg1_record(TAG, "abnomarl_soft1",g_time_0, "扫码休眠（软解码设备）异常测试通过");
		scanresult = null;
	}
	
	// 扫码休眠，硬解码设备  硬解码的休眠唤醒有点问题，需要修改
	public void abnomarl_hard1()
	{
		int camCnt = mScanDefineInfo.getCameraCnt();
		if(camCnt<2)
		{
			gui.cls_show_msg1(3, "不支持测试硬解码扫码，即将退出");
			return;
		}
		Scan_Mode tScan_Mode = Scan_Mode.MODE_ONCE;
		sleepAbnormal = true;
		StringBuffer scanresult = new StringBuffer();
		int ret = -1;
		// 测试前置，开启预览画面以及释放扫码，注册休眠的广播
		// case1:休眠唤醒后doscan动作已经结束，应不影响下次扫码动作
		releaseScan(tScan_Mode);
		initScanMode(nlsPara,tScan_Mode, 1, 20*1000);
		gui.cls_show_msg1(1, "请移开条码，正在扫码耐心等待5s后手动进入休眠，休眠2分钟后手动唤醒");
		// 返回值为null
		if ((ret = doScan(tScan_Mode,scanresult,nlsPara)) != NDK_SCAN_FAULT) /**NDK_SCAN_NO_RESULT,原先预期值是null,现在预期值是F,进入休眠扫码会主动释放*/
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_hard1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(tScan_Mode);
			return;
		}
		while (!GlobalVariable.isWakeUp);
		// 唤醒后给与初始化的时间，不要同时都从串口中读数据
		gui.cls_show_msg1(4, "【设备已唤醒】耐心等待4s后将条形码或二维码放置在前置摄像头20-30cm处");
		if ((ret = doScan(tScan_Mode,scanresult,nlsPara)) != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_hard1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(tScan_Mode);
			return;
		}
		releaseScan(tScan_Mode);
		
		// case2:休眠唤醒后doscan超时时间未结束，仍可以继续扫码，并且不影响下次扫码
		gui.cls_show_msg1(4, "正在扫码手动进入休眠，耐心等待4s后手动休眠10s后唤醒");
		initScanMode(nlsPara,tScan_Mode, 1, 30*1000);
		// 应返回S
		if ((ret = doScan(tScan_Mode,scanresult,nlsPara)) != NDK_SCAN_FAULT) /**NDK_SCAN_NO_RESULT,原先预期值是null,现在预期值是F，进入休眠扫码会主动释放*/
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_hard1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(tScan_Mode);
			return;
		}
		while (!GlobalVariable.isWakeUp);
		// 唤醒
		gui.cls_show_msg1(4, "耐心等待2s后将条形码或二维码放置在前置摄像头20-30cm处");
		if ((ret = doScan(tScan_Mode,scanresult,nlsPara)) != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl_hard1",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(tScan_Mode);
			return;
		}
		releaseScan(tScan_Mode);
		gui.cls_show_msg1_record(TAG, "abnomarl_hard1",g_time_0, "扫码休眠异常测试通过");
		scanresult = null;
	}
	
	/**
	 * 休眠异常测试:休眠不放置条码
	 */
	public void abnomarl2()
	{
		int tempCameraId = mCameraId;
		sleepAbnormal = true;
		StringBuffer scanresult = new StringBuffer();
		int ret = -1;
		
		gui.cls_show_msg1(1, "手动进入休眠，休眠后放置条形码或二维码于%s置摄像头20-30cm处，休眠2分钟后手动唤醒",mCameraMsg);
		initScanMode(nlsPara,scan_Mode, tempCameraId, 15*1000);
		// 休眠
		GlobalVariable.isWakeUp = true;
		while (GlobalVariable.isWakeUp);
		GlobalVariable.isWakeUp = false;
		// 应返回F
		if ((ret = doScan(scan_Mode,scanresult,nlsPara)) != NDK_SCAN_FAULT) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl2",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		// 唤醒
		while (!GlobalVariable.isWakeUp);
		gui.cls_show_msg1(2, "【屏幕已唤醒】，将条形码或二维码放置在%s置摄像头20-30cm处",mCameraMsg);
		if ((ret = doScan(scan_Mode,scanresult,nlsPara)) != NDK_SCAN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnomarl2",g_keeptime, "line %d:%s扫码失败(扫码结果：%s,ret = %d)",Tools.getLineInfo(), TESTITEM, scanresult,ret);
			releaseScan(scan_Mode);
			return;
		}
		releaseScan(scan_Mode);
		gui.cls_show_msg1_record(TAG, "abnomarl2",g_time_0, "休眠扫码异常测试通过");
		scanresult = null;
	}
	
	
	/**
	 * 机器人测试
	 */
	public void robot_test()
	{
		int nKeyin = 0;
		int tnum = 0,tcnt = 0,cardnum = 0;
		
		while(true)
		{
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			nKeyin = gui.cls_show_msg("1.景深测试\n2.角度测试\n3.速度测试\n4.一键测试\n");
			switch (nKeyin) 
			{
			case '1':
				cardnum = testcardnum();
				tnum = testposition();
				tcnt = testnum();
				robot_scan_press(cardnum,tnum,tcnt);
				
				break;
			case '2':
				robot_angle_test();
				break;
				
			case '3':
				cardnum = testcardnum();
				tnum = testposition();
				tcnt = testnum();
				robot_v_scan_press(cardnum,tnum,tcnt);
				break;
				
			case '4':
				robot_scan_press(1,16,10);// 景深测试
				robot_scan_press(1,13,10);// 水平旋转角度测试
				robot_scan_press(1,90,10);// 适读视场角度测试
				robot_v_scan_press(1,9,10);// 速度测试
				break;
				
			case ESC:
				return;

			default:
				break;
			}
		}
	}
	
	/***
	 * 机械臂测试 20200513 weimj
	 */
	public void auto_test(){
		if(gui.cls_show_msg("请确保机具不会进入休眠，[确定]继续，[取消]退出")==ESC)
			return;
		while(true)
		{
			int nKeyin = gui.cls_show_msg("0.连接机械臂\n1.扫码压力\n2.流程压力\n3.扫码性能\n4.流程性能\n");
			switch (nKeyin) 
			{
			case '0':
				robotConnect();
				break;
			case '1':
				robotPress();
				break;
			case '2':
				robotProcessPress();
				break;			
			case '3':
				robotScanAbility();
				break;
			case '4':
				robotScanProcessAbility();
				break;
			case ESC:
				return;

			default:
				continue;
			}
			
		}
	}
	
	//连接机械臂
	public void robotConnect()
	{
		byte[] recbuf;
        recbuf = new byte[8];
        byte[] sendbuf = {0xF,0xF,0xF,0xF};
        String str = null;
		AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
		// 打开串口
		if((ret = usbComm.open())<=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robotConnect", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		
		if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robotConnect", g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		
		gui.cls_show_msg1(2, "打开PC中透传工具连接机械臂");
		while(true){
			Arrays.fill(recbuf, (byte) 0x00);
			int ret = 0;
			usbComm.read(recbuf, 15, 10);
			String data = new String(recbuf);
			if("10109900".equals(data)){
				str = new String("ffff");
				sendbuf=str.getBytes();
				if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
					{
						gui.cls_show_msg1_record(TAG, "robotConnect", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
						usbComm.close();
						return;
					}
				gui.cls_show_msg1(2, "连接成功");
				robotFlag = true;
				return;
			}
		}
	}
	
	/*
	 * 机械臂扫码压力 by weimj 20190926
	 */
	private void robotPress() {
		int tempCameraId = mCameraId;
		StringBuffer scanresult = new StringBuffer();
		byte[] recbuf;
		recbuf = new byte[8];
		byte[] sendbuf = { 0xF, 0xF, 0xF, 0xF };
		String str = null;
		int i = 0;
		int timeout = 30 * 1000;
		AnalogSerialManager usbComm = (AnalogSerialManager) myactivity
				.getSystemService(ANALOG_SERIAL_SERVICE);

		/* 测试前置 */
		if (initFlag == false) {
			gui.cls_show_msg1(g_keeptime, "参数未配置");
			return;
		}
		if (robotFlag == false) {
			gui.cls_show_msg1(g_keeptime, "机械臂未连接");
			return;
		}

		// 打开串口
		// if((ret = usbComm.open())<=NDK_OK){
		// gui.cls_show_msg1_record(TAG, "scanPre5", g_keeptime,
		// "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
		// usbComm.close();
		// return;
		// }
		//
		// if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK){
		// gui.cls_show_msg1_record(TAG, "scanPre6", g_keeptime,
		// "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
		// usbComm.close();
		// return;
		// }

		releaseScan(scan_Mode);
		// 扫码初始化
		initScanMode(nlsPara, scan_Mode, tempCameraId, timeout);// 超时时间设为30s
		if (mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);

		gui.cls_show_msg1(2, "打开PC中透传工具发送指令");
		while (true) {
			Arrays.fill(recbuf, (byte) 0x00);
			int ret = 0;
			usbComm.read(recbuf, 15, 10);
			String data = new String(recbuf);
			Log.e("data", "" + data);
			if ("101099".equals(data.substring(0, 6))) {
				gui.cls_show_msg1(1, "第%d次扫码......", ++i);
				if ((ret = doScan(scan_Mode, scanresult, nlsPara)) != NDK_SCAN_OK) {
					str = new String("ffff");
				} else {
					str = new String(scanresult);
				}
				sendbuf = str.getBytes();
				if ((ret = usbComm.write(sendbuf, sendbuf.length, 15)) != sendbuf.length) {
					gui.cls_show_msg1_record(TAG, "robotPress", g_keeptime,"line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(), ret);
					continue;
				}
			}
		}
	}
	
	/**
	 * 机械臂流程压力 by weimj 20191017
	 */
	private void robotProcessPress(){
		int tempCameraId = mCameraId;
		StringBuffer scanresult = new StringBuffer();
		byte[] recbuf;
        recbuf = new byte[8];
        byte[] sendbuf = {0xF,0xF,0xF,0xF};
        String str = null;
        int i = 0;
        int timeout = 30*1000;
        AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
        
        /*测试前置*/
		if(initFlag==false)
		{
			gui.cls_show_msg1(g_keeptime, "参数未配置");
			return;
		}
		if(robotFlag==false)
		{
			gui.cls_show_msg1(g_keeptime, "机械臂未连接");
			return;
		}
		// 打开串口
//		if((ret = usbComm.open())<=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "scanPre6", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
//			usbComm.close();
//			return;
//		}
//		if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "scanPre6", g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
//			usbComm.close();
//			return;
//		}
        
        gui.cls_show_msg1(2, "打开PC中透传工具发送指令");
        while(true){
            Arrays.fill(recbuf, (byte) 0x00);
            int ret = 0;
            usbComm.read(recbuf, 15, 10);
            String data = new String(recbuf);
            Log.e("data",""+data);
            if ("101099".equals(data.substring(0, 6))){	
            	gui.cls_show_msg1(1,"第%d次扫码......",++i);
                releaseScan(scan_Mode);
                // 扫码初始化
                initScanMode(nlsPara, scan_Mode, tempCameraId, timeout);
                if(mIsPreview){
                    handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//后置有预览
                }else{
                    handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
                }
                
                if((ret = doScan(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
                {
                    str = new String("ffff");
                }
                else{
                    str = new String(scanresult);
                }
                if(mIsPreview)
                    handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
                releaseScan(scan_Mode);//释放扫码
                sendbuf=str.getBytes();
                if((ret = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
                {
                    gui.cls_show_msg1_record(TAG, "robotProcessPress", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),ret);
                    continue;
                }
            }
        }	
	}
	
	
	
	/**
	 * 测试的卡片数量
	 * @return
	 */
	public int testcardnum()
	{
		/*private &local definition*/
		int cnt = 10/*默认为10张卡片*/,ret=0;
		byte[] pszBuf = new byte[4];
		
		/*process body*/
		/*while(true)
		{
			gui.cls_printf("输入需要测试的卡片张数：\n".getBytes());
			if((ret = gui.lib_kbgetinput(pszBuf, pszBuf.length, 10))==NDK_ERR||ret == NDK_ERR_TIMEOUT)
				cnt = 10;
			else
				cnt = Integer.parseInt(gui.pszBufNDK);
			if(cnt>0)
				break;
			else
				gui.cls_show_msg1(2, "输入应该大于0！\n");
		}
		return cnt;*/
		return gui.JDK_ReadData(TIMEOUT_INPUT, cnt, "输入需要测试的卡片张数：");
	}
	
	/**
	 * 设置景深的位置
	 * @return
	 */
	public int testposition()
	{
		/*private &local definition*/
		int cnt = 15,/*默认为15个景深位置*/ret = 0;
		byte[] pszBuf = new byte[4];
		
		/*process body*/
		/*while(true)
		{
			gui.cls_printf("输入需要测试的位置（速度）数：\n".getBytes());
			if((ret = gui.lib_kbgetinput(pszBuf, pszBuf.length, 10))==NDK_ERR||ret == NDK_ERR_TIMEOUT)
				cnt = 15;
			else
				cnt = Integer.parseInt(gui.pszBufNDK);
			if(cnt>0)
				break;
			else
				gui.cls_show_msg1(2, "输入应该大于0！\n");
				
		}
		return cnt;*/
		return gui.JDK_ReadData(TIMEOUT_INPUT, cnt, "输入需要测试的位置（速度）数：");
	}
	
	/**
	 * 每个景深的扫码测试次数
	 * @return
	 */
	public int testnum()
	{
		/*private &local definition*/
		int cnt = 10,/*每个位置默认扫码10次*/ret = 0;
		byte[] pszBuf = new byte[4];
		
		/*process body*/
		/*while(true)
		{
			gui.cls_printf("输入每个位置（速度）需要扫码的次数：\n".getBytes());
			if((ret = gui.lib_kbgetinput(pszBuf, pszBuf.length, 10))==NDK_ERR||ret == NDK_ERR_TIMEOUT)
				cnt = 10;
			else
				cnt = Integer.parseInt(gui.pszBufNDK);
			if(cnt>0)
				break;
			else
				gui.cls_show_msg1(2, "输入应该大于0！\n");
		}
		return cnt;*/
		return gui.JDK_ReadData(TIMEOUT_INPUT, cnt, "输入每个位置（速度）需要扫码的次数：");
	}
	
	/**
	 * 机器人扫码压力测试
	 * @param cardnum
	 * @param ptnum
	 * @param ptcnt
	 */
	public void robot_scan_press(int cardnum,int ptnum,int ptcnt)
	{
		/*private &local definition*/
		int tempCameraId = mCameraId;
		int ret = 0,i = 0,j = 0,succ = 0,len = 0,k =0,num = 0,cnt = 0;
		byte[] sendbuf = {0x02,0x01,(byte) 0xDF,0x00,0x03};
		byte[] recbuf = new byte[16];
		byte[] ok_buf = {0x02,0x02,(byte) 0xDF,0x01};
		AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		StringBuffer scanresult = new StringBuffer();
		long startTime;
		float time = 0;
		
		/*测试前置*/
		if(initFlag==false)
		{
			gui.cls_show_msg1(g_keeptime, "参数未配置！");
			return;
		}
		/*process body*/
		initScanMode(nlsPara, scan_Mode, tempCameraId, 10*1000);
		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		
		// 打开USB串口，打开成功的返回值？
		if((ret = usbComm.open())<=0)
		{
			gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}	
		
		if((ret = usbComm.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		gui.cls_show_msg1(30, "打开PC中透传工具后任意键继续...");
		while(cardnum>0)// cardnum表示要测试多少张码制
		{
			if(gui.cls_show_msg1(1, "第%d个码制进行扫码测试，【取消】退出测试", k+1)==ESC)
				break;
			k++;
			cardnum--;
			num = ptnum;
			cnt = ptcnt;
			i = 0;// 新的一张卡重新清零
			succ = 0;
			while(num>0)// num表示测试的位置，景深\角度位置不同
			{
				if(gui.cls_show_msg1(1, "要扫描第%d个位置，已扫描到%d个位置，【取消】退出", i+1,succ)==ESC)
					break;
				i++;
				num--;
				// 往USB发送"0201DF0003"
				if((len = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:usb串口发送数据失败(%d)", Tools.getLineInfo(),len);
					usbComm.close();
					return;
				}
				Arrays.fill(recbuf, (byte) 0x00);
				if((len = usbComm.read(recbuf, 6, 15))!=6)
				{
					gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:usb串口读数据失败(%d)", Tools.getLineInfo(),len);
					usbComm.close();
					return;
				}
				cnt = ptcnt;
				j = 0;
				if(Tools.memcmp(recbuf, ok_buf, 4)==true)// 收到指令"0202DF01"进行扫码10次
				{
					while(cnt>0)// cnt表示每个位置扫码次数
					{
						j++;
						cnt--;
						if(gui.cls_show_msg1(1, "正在进行第%d次扫码，【取消】退出测试", j)==ESC)
							break;
						startTime = System.currentTimeMillis();
						if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
						{
							gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:第%d个码制，第%d个位置，第%d次，扫描失败：%d", Tools.getLineInfo(),k,i,j,ret);
							continue;
						}
						else{
							time = Tools.getStopTime(startTime);
							gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:第%d个码制，第%d个位置，第%d次，扫码结果：%s，扫码时间：%fs", Tools.getLineInfo(),k,i,j,scanresult,time);
						}
					}
				}
				succ++;// 位置加1
			}
		}
		// 测试完成发送指令移走并到卡槽放卡
		// 往USB发送“0201DF0003”
		if((len = usbComm.write(sendbuf, sendbuf.length, 15*1000))!=sendbuf.length)
		{
			gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:往串口发送数据失败（%d）", Tools.getLineInfo(),len);
			usbComm.close();
			return;
		}
		releaseScan(scan_Mode);
		gui.cls_show_msg1(1, "景深或角度测试完成");
	}
	
	/**
	 * 
	 * @param cardnum
	 * @param vnum
	 * @param vcnt
	 */
	public void robot_v_scan_press(int cardnum,int vnum,int vcnt)
	{
		/*private & local definition*/
		int tempCameraId = mCameraId;
		int ret = 0,i = 0,j = 0,succ = 0,len = 0,k = 0,num = 0,cnt = 0;
		byte[] sendbuf = {0x02,0x01,(byte) 0xDF,0x00,0x03};
		byte[] recbuf = new byte[16];
		byte[] ok_buf = {0x02,0x02,(byte) 0xDF,0x01};
		AnalogSerialManager usbComm = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		StringBuffer scanresult = new StringBuffer();
		long startTime;
		float time = 0;
		
		/*测试前置*/
		if(initFlag==false)
		{
			gui.cls_show_msg1(g_keeptime, "参数未配置");
			return;
		}
		/*process body*/
		// 扫码初始化
		initScanMode(nlsPara, scan_Mode, tempCameraId, 15*1000);
		if(mIsPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		
		// 打开串口
		if((ret = usbComm.open())<=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robot_v_scan_press", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		if((ret = usbComm.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:配置usb串口失败(%d)", Tools.getLineInfo(),ret);
			usbComm.close();
			return;
		}
		gui.cls_show_msg1(30, "打开PC中透传工具后任意键继续...");
		while(cardnum>0)// cardnum表示要测试多少张码制
		{
			if(gui.cls_show_msg1(1, "第%d个码制进行扫码测试，【取消】退出测试",k+1)==ESC)
				break;
			k++;
			cardnum--;
			num = vnum;
			cnt = vcnt;
			i= 0; // 换一张卡片测试时需清零
			succ = 0;
			while(num>0)// num表示测试的不同速度
			{
				if(gui.cls_show_msg1(1, "要扫描第%d种速度，已扫码%d种速度，【取消】退出测试",i+1,succ)==ESC)
					break;
				i++;
				num--;
				cnt = vcnt;
				j = 0;// 换一种速度测试时需清零
				while(cnt>0)//cnt表示扫码次数
				{
					j++;
					cnt--;
					if(gui.cls_show_msg1(1, "正在进行第%d次扫码，【取消】退出测试", j)==ESC)
						break;
					// 往USB发送"0201DF0003"
					if((len = usbComm.write(sendbuf, sendbuf.length, 15))!=sendbuf.length)
					{
						gui.cls_show_msg1_record(TAG, "robot_v_scan_press", g_keeptime, "line %d:串口发送数据失败（%d）", Tools.getLineInfo(),len);
						usbComm.close();
						return;
					}
					// 串口的开启与配置是无问题的，能扫码
					gui.cls_printf(String.format("正在进行第%d次扫码...", j).getBytes());
					startTime = System.currentTimeMillis();
					if((ret = robot_doScan(scan_Mode, scanresult,nlsPara))!=NDK_SCAN_OK)
					{
						gui.cls_show_msg1_record(TAG, "robot_v_scan_press", g_keeptime, "line %d:第%d个码制，第%d种速度，第%d次，扫描失败：%d", Tools.getLineInfo(),k,i,j,ret);
						
					} else{
						time = Tools.getStopTime(startTime);
						gui.cls_show_msg1_record(TAG, "robot_v_scan_press", g_keeptime, "line %d:第%d个码制，第%d种速度，第%d次，扫码结果：%s，扫码时间：%fs", Tools.getLineInfo(),k,i,j,scanresult,time);
					}
					
					// 测试后置,END的位置
					Arrays.fill(recbuf, (byte) 0x00);
					if((len = usbComm.read(recbuf, 6, 15))!=6)
					{
						gui.cls_show_msg1_record(TAG, "robot_v_scan_press",g_keeptime,"line %d:串口读取数据失败（%d）",Tools.getLineInfo(),len);
						usbComm.close();
						return;
					}
					if(Tools.memcmp(recbuf, ok_buf, 4)==false)
						continue;
				}
			}
			succ++;
		}
		// 测试完成发送指令移走并到卡槽放卡
		// 往USB发送“0201DF0003”
		if((len = usbComm.write(sendbuf, sendbuf.length, 15*1000))!=sendbuf.length)
		{
			gui.cls_show_msg1_record(TAG, "robot_scan_press", g_keeptime, "line %d:往串口发送数据失败（%d）", Tools.getLineInfo(),len);
			usbComm.close();
			return;
		}
		releaseScan(scan_Mode);
		gui.cls_show_msg1(1, "速度测试完成");
	}
	
	/**
	 * 扫描角度测试
	 */
	public void robot_angle_test()
	{
		int nKeyin = 0;
		int tnum = 0,tcnt = 0,cardnum = 0;
		
		while(true)
		{
			nKeyin = gui.cls_show_msg("1.水平旋转\n2.倾斜角度\n3.识读视场角\n");
			switch (nKeyin) 
			{
			case '1':
			case '2':
			case '3':
				cardnum = testcardnum();
				tnum = testposition();
				tcnt = testnum();
				robot_scan_press(cardnum, tnum, tcnt);
				break;
			case ESC:
				return;

			default:
				break;
			}
		}
	}
	
	class ScanThread extends Thread
	{
		private volatile boolean toStop = false;
		
		@Override
		public void run() 
		{
			try 
			{
				while(true)
				{
					String scan_result;
					
					scan_result = (String) scan_Domestic.doScan();
					if(toStop)
					{
						LoggerUtil.d("已经退出扫码线程");
						return;
					}
					if(scan_result!=null&&scan_result.startsWith("S"))
					{
						scan_result=scan_result.substring(1);/*按照测试人员要求与单元用例移植，裁剪第一个S字符*/
					}
					gui.cls_printf(String.format("扫码结果："+scan_result).getBytes());
					Thread.sleep(10);
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			/*finally
			{
				try
				{
					deviceLogger.error("----ScannerThread-----InterruptedException------------!");
					scanUtil.relese();
				}catch(Exception e){
					deviceLogger.error("stop ScanThread failed!",e);
				}
			}*/
		}
	}
	
	

	
	private int sendByte(byte[] header,Sock_t sock_t,LinkType type){
		int ret = 0,slen = 0;
		int startLen = header.length;
		
		if((slen = sockSend(socketUtil,header, startLen, SO_TIMEO,wifiPara))!= startLen)
		{
			gui.cls_show_msg1_record(TAG, "sendByte", g_keeptime, "line %d:发送数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),slen,startLen);
			return NDK_ERR;
		}
		SystemClock.sleep(1000);
		return NDK_OK;
	}
	/**
	 * 图片解码,RGB解码方式后续不测试20200416
	 *//*
	public int chooseRGBCode(){
		int currentPage=1;
		int key='0';
		int num=0;
		gui.cls_show_msg("请在%s下导入SVN上scan文件夹，完成后，任意键继续",path);
		while(true){
			switch (currentPage) {
			case 1:
				key=gui.cls_show_msg("选择码制\n0.QR_UTF8_1\n1.QR_UTF8_2\n2.QR_GBK\n3.QR_ECI\n4.PDF417\n5.CodeBar\n6.Code39\n7.Code93\n8.Code128\n9.EAN_8(上下可翻页)");
				break;
			case 2:
				key = gui.cls_show_msg("选择码制\n0.EAN_13\n1.EAN_128\n2.ITF\n3.UPC_A\n4.UPC_E\n5.ISBN_ISSN\n6.UCC_EAN_128(上下可翻页)");
				if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
					key= 17 +key;
				break;
			default:
				break;
			}
			switch (key) {
			case ESC:
				return num;
			case KEY_DOWN:
				if(currentPage++>=2)
					currentPage=1;
				break;
			case KEY_UP:
				if(currentPage--<=1)
					currentPage=2;
				break;
			}
			if(key != KEY_DOWN && key !=KEY_UP)
			{
				
				switch (currentPage) {
				case 1:
					num=key-48;
					break;
				case 2:
					num=key-55;
					break;
				default:
					break;
				}
				return num;
			}
			
		}
	}*/
	private CameraManager manager;
	private CameraDevice cameraDevice;
	private Handler childHandler;
	private HandlerThread handlerThread;
	private CaptureRequest.Builder previewRequestBuilder;
	// 定义用于预览照片的捕获请求
	private CaptureRequest previewRequest;
	// 定义CameraCaptureSession成员变量
	private CameraCaptureSession captureSession;
	private double openCameraStartTime,closeCameraStartTime;
	private double openCameraEndTime,closeCameraEndTime;
	private double openCameraTime,closeCameraTime;
	private Size mPreviewSize;
	private Size mimageSize;
	
	
	private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
		//摄像头被打开时激活该方法
		@Override
		public void onOpened(CameraDevice camera) {
			openCameraEndTime=System.currentTimeMillis()-openCameraStartTime;
			cameraDevice = camera;
			createCameraPreviewSession();
			LoggerUtil.e("相机打开");
		}
		@Override
		public void onError(CameraDevice camera, int error) {
			
		}
		@Override
		public void onDisconnected(CameraDevice camera) {
		}
		@Override
		public void onClosed(CameraDevice camera) {
			super.onClosed(camera);
			closeCameraEndTime=System.currentTimeMillis()-closeCameraStartTime;
			LoggerUtil.e("关闭摄像头");
			openCameraTime=openCameraTime+openCameraEndTime;
			closeCameraTime=closeCameraTime+closeCameraEndTime;
			synchronized (lock) 
			{
				
				lock.notify();	
				LoggerUtil.e("close lock notify");
			}
		}
		
	};
	public void resetTime(){
		openCameraStartTime=0.0;
		openCameraEndTime=0.0;
		openCameraTime=0.0;
		closeCameraStartTime=0.0;
		closeCameraEndTime=0.0;
		closeCameraTime=0.0;
	}
	public void openCameraNew(String cameraDescribe){
		LoggerUtil.e("open open open ");
	    manager = (CameraManager)myactivity.getSystemService(Context.CAMERA_SERVICE);
		try {
			 CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDescribe);
			  //支持的STREAM CONFIGURATION
	          StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
	          //摄像头支持的预览Size数组
	          mimageSize = setPictureSize(map.getOutputSizes(ImageFormat.JPEG));
	          //这个类里面有计算最佳尺寸的代码
	          CameraConfiguration camearaConfiguration = new CameraConfiguration(myactivity);
	  		  Point cameraResolution = camearaConfiguration.initFromCameraParameters(manager);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		
		handlerThread = new HandlerThread("Camera2");
	    handlerThread.start();
	    childHandler = new Handler(handlerThread.getLooper());
//        mainHandler = new Handler(Looper.getMainLooper());
    
		try {
            manager.openCamera(cameraDescribe, stateCallback, childHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} 
	  
	}
	//寻找size最大尺寸
	private Size setPictureSize(Size[] sizes ) {
		int maxSize = 0;
		int width = 0;
		int height = 0;
		for (int i = 0; i < sizes.length; i++) {
			Size size = sizes[i];
			int pix = size.getWidth() * size.getHeight();
			if (pix > maxSize) {
				maxSize = pix;
				width = size.getWidth();
				height = size.getHeight();
			}
		}
		return new Size(width,height);
	}
	 private void createCameraPreviewSession() {
	        try {
	        	SurfaceHolder mSurfaceHolder=surfaceView.getHolder();
	            // 创建作为预览的CaptureRequest.Builder
	            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
	            // 将surfaceView的surface作为CaptureRequest.Builder的目标
	            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
	            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
	            cameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface()), new CameraCaptureSession.StateCallback() // ③
	                    {
	                        @Override
	                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
	                        	
	                            // 如果摄像头为null，直接结束方法
	                            if (null == cameraDevice) {
	                                return;
	                            }

	                            // 当摄像头已经准备好时，开始显示预览
	                            captureSession = cameraCaptureSession;
	                            try {
	                                // 设置自动对焦模式
	                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
	                                // 设置自动曝光模式
	                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
	                                // 开始显示相机预览
	                                previewRequest = previewRequestBuilder.build();
	                                // 设置预览时连续捕获图像数据
//	                                previewFlag=false;
	                                captureSession.setRepeatingRequest(previewRequest,null, childHandler);  // ④
	                                synchronized (lock) 
	                     			{
	                                 	LoggerUtil.e("preview lock notify");
//	                                 	previewFlag=true;
	                     				lock.notify();	
	                     			}
	                            } catch (CameraAccessException e) {
	                                e.printStackTrace();
	                            }
	                           
	                        }

	                        @Override
	                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
	                        	LoggerUtil.e("配置失败");
	                        }
	                        
	                    }, childHandler
	            );
	        } catch (CameraAccessException e) {
	            e.printStackTrace();
	        }
	    }
//扫码崩溃修改  by weimj 20190909
		public void stopCameraNew(){
			
			 if (null != captureSession) {
				 try {
					captureSession.stopRepeating();
				} catch (CameraAccessException e) {
					e.printStackTrace();
				}
				 captureSession.close();
				 captureSession = null;
	         }
	   	 	if (cameraDevice!=null) {
		        cameraDevice.close();
		        cameraDevice = null;
	   	 	}
	   	 if (handlerThread != null) {
				handlerThread.quitSafely();
//		        try {
//		        	handlerThread.join();
		        	handlerThread = null;
		        	childHandler = null;
//		        } catch (InterruptedException e) {
//		            e.printStackTrace();
//		        }
	     }
		}
		
	//模拟用户扫码 by huhj 20190909
	private void scanPre4() {
		int tempCameraId = mCameraId;
		StringBuffer scanresult = new StringBuffer();
		int cnt = 0,i=0,succ = 0,ret = -1;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUTN));
		cnt = packet.getLifecycle();
		
		
		releaseScan(scan_Mode);
		
		/*if(gui.cls_show_msg("请确认是否用1280*720扫码框。是[确认],否[其他]")==ENTER){
			 isnewscan=0;	
			 gui.cls_show_msg1(1,"修改成功");

		}*/
//		if(scan_Mode!=Scan_Mode.NLS_picture){
//			gui.cls_show_msg("请将条形码或二维码放置于%s摄像头20-30cm处，任意键继续", mCameraMsg);
//			// 因为前置摄像头没有预览画面故去除前置摄像头的预览画面 modify by zhengxq 20170704
//			if(cameraId==0&&mIsPreview)
//				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
//		}
//		initScanMode(nlsPara,scan_Mode, cameraId, TIMEOUT);
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "正在进行第%d次扫码,已扫码成功%d次,[取消]退出测试", i+1,succ)==ESC)
				break;
			initScanMode(nlsPara,scan_Mode, tempCameraId, TIMEOUT);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			i++;
			cnt--;
			
			if((ret = doScan_noconf(scan_Mode,scanresult,nlsPara))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(TAG, "scanPre4",g_keeptime,"line %d:%s扫码失败 (扫码结果：%s，ret = %d)", Tools.getLineInfo(),TESTITEM,scanresult,ret);

				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				continue;
			}

			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			succ++;
			releaseScan(scan_Mode);
		}
//		releaseScan(scan_Mode);
		gui.cls_show_msg1_record(TAG, "scanPre4",g_time_0,"模拟用户扫码压力测试完成，已执行次数为%d，成功为%d次", i,succ);
	}
	
	
	 public void openCloseCameraAbilityNew(){
		int tempCameraId = mCameraId;
		resetTime();
		for(int i=0;i<50;i++){
			gui.cls_show_msg1(2, "正在进行第%d次打开关闭摄像头",i+1);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			openCameraStartTime=System.currentTimeMillis();
			openCameraNew(tempCameraId+"");
			 synchronized (lock) {
				 try {
					lock.wait();
					LoggerUtil.e("open wait");
					SystemClock.sleep(5000);
					closeCameraStartTime=System.currentTimeMillis();
					stopCameraNew();
					synchronized (lock) {
						 try {
							lock.wait();
							LoggerUtil.e("close wait");
							handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 }
			
		}
		
		gui.cls_show_msg1_record(TAG, "openCloseCameraAbility", g_time_0, "(新API)%s平均每次打开摄像头时间%.1fms/次，关闭摄像头时间%.1fms/次", mCameraMsg,openCameraTime/50,closeCameraTime/50);
		
	}
	private Camera mCamera;
	private Parameters mParameters;
	private SurfaceHolder mHolder;
	public void openCloseCameraAbilityOld(){
		int tempCameraId = mCameraId;
		resetTime();
		//不需要自己的缓冲区
		mHolder=surfaceView.getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.addCallback(new SurfaceHolder.Callback() {
	            @Override
	            public void surfaceCreated(SurfaceHolder holder) {
	            	
	            }

	            @Override
	            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	            }

	            @Override
	            public void surfaceDestroyed(SurfaceHolder holder) {
	               
	            }
	        });
		for(int i=0;i<50;i++){
			gui.cls_show_msg1(2, "正在进行第%d次打开关闭摄像头",i+1);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			openCameraStartTime=System.currentTimeMillis();
			openCameraOld(mCameraId);
	        openCameraEndTime=System.currentTimeMillis()-openCameraStartTime;
	        SystemClock.sleep(5000);
	        closeCameraStartTime=System.currentTimeMillis();
	        closeCameraOld();
	        closeCameraEndTime=System.currentTimeMillis()-closeCameraStartTime;
	        openCameraTime=openCameraTime+openCameraEndTime;
			closeCameraTime=closeCameraTime+closeCameraEndTime;
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		}
	
		gui.cls_show_msg1_record(TAG, "openCloseCameraAbility", g_time_0, "(旧API)%s平均每次打开摄像头时间%.1fms/次，关闭摄像头时间%.1fms/次", mCameraMsg,openCameraTime/50,closeCameraTime/50);
	
	}
	@SuppressWarnings("deprecation")
	public void openCameraOld(int cameraId){
		mCamera=Camera.open(cameraId);
		Parameters parameters = mCamera.getParameters();
		parameters.set("orientation", "portrait");
		mCamera.setDisplayOrientation(90);
        parameters.setRotation(90);
        setPictureSize(parameters);
		// 需要判断支持的预览
		setPreviewSize(parameters);
        mCamera.setParameters(parameters);
        try {
			mCamera.setPreviewDisplay(surfaceView.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
        mCamera.startPreview();
	}
	public void closeCameraOld(){
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	private void setPictureSize(Parameters parameters) {
		List<android.hardware.Camera.Size> sizes = parameters.getSupportedPictureSizes();
		if (sizes == null) {
			return;
		}
		int maxSize = 0;
		int width = 0;
		int height = 0;
		for (int i = 0; i < sizes.size(); i++) {
			android.hardware.Camera.Size size = sizes.get(i);
			int pix = size.width * size.height;
			if (pix > maxSize) {
				maxSize = pix;
				width = size.width;
				height = size.height;
			}
		}

		parameters.setPictureSize(width, height);
	}

	private void setPreviewSize(Parameters parameters) {
		// 根据照片大小设置预览框长宽比
		android.hardware.Camera.Size size = parameters.getPictureSize();
		// 设置预览大小使之最接近取景镜头高度并且有正确的长宽比
		List<android.hardware.Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		android.hardware.Camera.Size optimalSize = getOptimalPreviewSize(sizes, (double) size.width / size.height);
		if (optimalSize != null) {
			parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		}
	}

	private android.hardware.Camera.Size getOptimalPreviewSize(List<android.hardware.Camera.Size> sizes, double targetRatio) {
		final double aspectTolerance = 0.05;
		if (sizes == null) {
			return null;
		}
		android.hardware.Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		Display display = myactivity.getWindowManager().getDefaultDisplay();
		int targetHeight = Math.min(display.getHeight(), display.getWidth());

		if (targetHeight <= 0) {
			// We don't know the size of SurefaceView, use screen height
			WindowManager windowManager = (WindowManager) myactivity.getSystemService(Context.WINDOW_SERVICE);
			targetHeight = windowManager.getDefaultDisplay().getHeight();
		}

		// Try to find an size match aspect ratio and size
		for (android.hardware.Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > aspectTolerance) {
				continue;
			}
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {

			minDiff = Double.MAX_VALUE;
			for (android.hardware.Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	public void onResume() 
	{
		super.onResume();
//		deviceLogger.debug("systest56 onresume");
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
		if(!sleepAbnormal)
		{
//			deviceLogger.debug("systest56 onpause");
			releaseScan(scan_Mode);
		}
	}
	
	@Override
	public void onStart() 
	{
		super.onStart();
//		deviceLogger.debug("systest56 onstart");
		if(!sleepAbnormal&&initFlag)
		{
//			initScanMode(nlsPara,scan_Mode, cameraId, TIMEOUT);
		}
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		if(!sleepAbnormal)
		{
			releaseScan(scan_Mode);
		}
	}
}
