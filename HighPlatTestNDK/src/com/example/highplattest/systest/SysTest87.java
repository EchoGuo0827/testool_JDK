package com.example.highplattest.systest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.newland.scan.SoftEngine;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.NlsPara;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.Beep;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest104.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20191030
 * directory 		: 
 * description 		: X5_Poynt新扫码压力性能验证
 * related document :
 * history 		 	: author			date			remarks
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class SysTest87 extends DefaultFragment {
	
	private final String TESTITEM = "(SoftEngine)startDecode方式扫码综合";
	private final String TAG = SysTest87.class.getSimpleName();
	private final int SCAN_TIMEOUT = 15*1000;

	private final int MSG_STOP_DECODE = 1000;
	private final int MSG_DECODE = 1001;
	int mSavaCount = 0;
	private int ret=-1;
	private Handler mMessageHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_STOP_DECODE:
				if (mSoftEngine != null) {
					Log.d("eric", "MSG_SEND_SCAN_RESULT-结束解码-----");
					mSoftEngine.stopDecode();
				}
				break;

			case MSG_DECODE:
				byte[] data = (byte[]) msg.obj;
				if (mSoftEngine != null) {
					if(abnarmalExit)// 退出测试
					{
						isAbnormalTest=false;
						mSoftEngine.stopDecode();
						destoryCamera();
					}
					else
					{
						mSoftEngine.startDecode(data, previewWidth, previewHeight);
						// 异常测试的时候需要保存data的数据，间隔2s保存一次，100条数据的时候删除前50条
						if(isAbnormalTest)
						{
//							SystemClock.sleep(2000);
							saveyuvRawData(data);
							if(mSavaCount==100)
							{
								deleteSomeFile(false);
							}
						}
					}
						
				}
				break;

			}
		}
	};
	 
	 
	ScanningCallback mScanningCallback = new ScanningCallback() {
		
		@Override
		public void onScanningCallback(int eventCode, int arg1, byte[] data1, int arg3) {
			LoggerUtil.e("onResult,event="+eventCode+"|||isScan="+isScan);
			if(eventCode==1&& isScan)// 规避扫一次码响两声的情况，扫到一次码就不要再进入
			{
				try {
					mCodeResult = new String(data1, "UTF-8");
					mBeep.play();
					synchronized (mLockObj) {
						mLockObj.notify();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (isAbnormalTest) {
					abnarmalExit = true;
					gui.cls_show_msg("scanningCallback 异常测试时扫到码值，错误情况发生"+ mCodeResult);
				}

			}
		}
	};
	 
	private void saveyuvRawData(byte[] data) 
	{
		String savePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "img" + File.separator  + "scan_data"+System.currentTimeMillis()+".txt";

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
		} else {
			Log.e("tag", "saveBitmap failure : sdcard not mounted");
			return;
		}
		File fileRaw = null;
		// 创建FileOutputStream对象
		FileOutputStream outputStream = null;
		// 创建BufferedOutputStream对象
		BufferedOutputStream bufferedOutputStream = null;
		try {
			fileRaw = new File(savePath);
			if (!fileRaw.exists()) {
				fileRaw.getParentFile().mkdirs();
				fileRaw.createNewFile();
			}
			// 获取FileOutputStream对象
			outputStream = new FileOutputStream(fileRaw);
			// 获取BufferedOutputStream对象
			bufferedOutputStream = new BufferedOutputStream(outputStream);
			// 往文件所在的缓冲输出流中写byte数据
			bufferedOutputStream.write(data);
			// 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
			bufferedOutputStream.flush();
		} catch (IOException e) {
			Log.e("tag", "saveraw: " + e.getMessage());
			return;
		}
		mSavaCount++;
		Log.i("tag", "saveraw success: " + fileRaw.getAbsolutePath());
	}
	
	public void deleteSomeFile(boolean deleteAll)
	{
		String saveDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "img";
		File file = new File(saveDir);
		File[] destFiles = file.listFiles();
		
		if(deleteAll)
		{
			if(destFiles!=null)
			{
				for(int i=0;i<destFiles.length;i++)
				{
					destFiles[i].delete();
				}
			}

		}
		else
		{
			if(destFiles!=null)
			{
				for(int i=0;i<destFiles.length*(3.0/4.0);i++)
				{
					destFiles[i].delete();
				}
				LoggerUtil.v("deleteSomeFile->"+destFiles.length*(1.0/4.0));
			}
		}
		//重新将计数清零
		mSavaCount=0;
	}
	
	
	private ScanDefineInfo mScanDefineInfo;
	private int mCameraId;
	private String mCameraMsg;/**指示当前扫码的摄像头*/
	private boolean mIsPreview;
	private Gui gui;
	private SoftEngine mSoftEngine;
	private Camera mCamera;
	private int previewWidth;
	private int previewHeight;
	private SurfaceHolder mSurfaceHolder = null;    // SurfaceHolder对象：(抽象接口)SurfaceView支持类 
	private boolean isScan=false;
	private Object mLockObj = new Object();
	private String mCodeResult="";
	private Beep mBeep;
	
	private boolean abnarmalExit=false;
	private boolean isAbnormalTest = false;
	
	public void systest87() 
	{	
		gui = new Gui(myactivity, handler);
		gui.cls_printf("初始化扫码配置中,耐心等待3-5s".getBytes());
		mScanDefineInfo = getCameraInfo();
		LoggerUtil.e("BACK_CAMERA,"+mScanDefineInfo.cameraReal.get(BACK_CAMERA));
		LoggerUtil.e("FONT_CAMERA,"+mScanDefineInfo.cameraReal.get(FONT_CAMERA));
		LoggerUtil.e("EXTERNAL_CAMERA,"+mScanDefineInfo.cameraReal.get(EXTERNAL_CAMERA));
		LoggerUtil.e("USB_CAMERA,"+mScanDefineInfo.cameraReal.get(USB_CAMERA));
		mBeep = new Beep(myactivity);
//		mSoftEngine = SoftEngine.getInstance(myactivity);
		mSoftEngine = SoftEngine.getInstance();
		
		mSurfaceHolder = surfaceView.getHolder();
		mSurfaceHolder.addCallback(mHolderCallback);
		
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("startDecode方式扫码压力综合\n0.扫码配置\n1.扫码压力\n2.扫码性能\n3.长时间未放置码异常测试");
			switch (nkeyIn) 
			{
			case '0':
				cameIdConfig();
				break;
			
			case '1':
				if(nlsPara.isConfig()==false)
				{
					gui.cls_show_msg("请先进行【扫码配置】，任意键继续");
					break;
				}
				int nkeyIn2 = gui.cls_show_msg("startDecode方式扫码压力\n0.无释放Camera扫码压力\n1.释放Camera扫码压力\n2.休眠唤醒扫码压力\n");
				switch (nkeyIn2) {
				case '0':
					noReleaseScanPre();
					break;
				case'1':
					releaseScanPre();
					break;
					
				case '2':
					sleepWakePre();
					break;
					
					default:
						break;
				}
			
				break;
			case '2':
				if(nlsPara.isConfig()==false)
				{
					gui.cls_show_msg("请先进行【扫码配置】，任意键继续");
					break;
				}
				int nkeyIn3 = gui.cls_show_msg("startDecode方式扫码性能\n0.反复扫码性能\n1.释放Camera解析码性能\n");
				switch (nkeyIn3) 
				{
				case '0':
					noReleaseScanAbility();
					break;
					
				case'1':
					releaseCameraAbility();
					break;
					
				}
				break;
				
			case '3':
				if(nlsPara.isConfig()==false)
				{
					gui.cls_show_msg("请先进行【扫码配置】，任意键继续");
					break;
				}
				scanAbnormal();
				break;
				
			case 9:/**长时间未放置码退出键*/
				LoggerUtil.e("systest88，按键数字键9");
				abnarmalExit=true;
				break;
				
			case ESC:
				intentSys();
				return;
			
			}
		}
		
	}
	
	private NlsPara nlsPara = new NlsPara();
	
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
		nlsPara.setConfig(true);
		
		// 支持双码模式 20200714
		int scanSet = 0;// 0不支持，1：支持单码，2：支持双码
		int scanKey = gui.cls_show_msg("测试人员确认固件是否支持多码同图时可扫出多码,，支持的话，(测试[确认]，不测试[其他])，不支持[其他]");
		switch (scanKey) {
		case ENTER:
			scanSet = 2;
			break;
			
		case ESC:
			scanSet = 0;
			break;
			
		default:
			scanSet = 1;
			break;
		}
		if(scanSet==2||scanSet==1)
		{
			LoggerUtil.e("cameIdConfig->scanSet:"+scanSet);
			mSoftEngine.scanSet("QR", "CodeNum", scanSet+"");
			mSoftEngine.scanSet("QR", "NumFixed", "0");
		}
	}

	// 释放Camera的扫码压力
	private void releaseScanPre() 
	{
		String funcName="releaseScanPre";
		boolean isCompareScan = false;
		/**测试前置,释放扫码资源*/
		releaseAll();
		
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 100,"默认扫码次数100，测试人员自行修改"));
		bak = cnt = packet.getLifecycle();
		
		// 是否需要自行校验扫码结果
		if(gui.cls_show_msg("测试人员是否手动校验扫码结果")==ENTER)
		{
			isCompareScan = true;
		}
		while (cnt > 0) {

			cnt--;
			if(gui.cls_show_msg2(0.5f, "测试中。。。请将码放在%s摄像头位置[配置为多码同图请放置/SVN/scan/mul_qr.png]-------", mCameraMsg)==ESC)
			{
				releaseAll();
				break;
			}
			initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
			mSoftEngine.setScanningCallback(mScanningCallback);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
			isScan = true; 
			mCodeResult = "";
//			isTheTimeEnd = false;
			synchronized (mLockObj) {
				try {
					mLockObj.wait(SCAN_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isScan=false;
			LoggerUtil.e("isScan=" + isScan + "|||" + mCodeResult);
			if(mCodeResult.equals(""))
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:扫码超时,当前次数%d,成功%d次", Tools.getLineInfo(), bak - cnt, succ);
				releaseAll();
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				continue;
			}
			else if (isCompareScan) {
				if(gui.cls_show_msg("扫码结果=%s,是否与实际码值一致", mCodeResult)==ENTER)
					succ++;
				else
				{
					gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:扫码结果与实际码值不一致，扫码结果=%s,当前次数%d,成功%d次", Tools.getLineInfo(), mCodeResult,bak - cnt, succ);
					releaseAll();
					handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
					continue;
				}
			}
			else {
				gui.cls_show_msg1_record(TAG, funcName, 1,"line %d:不校验扫码结果，扫码结果=%s，记录到result.txt", Tools.getLineInfo(), mCodeResult);
				succ++;
			}
			releaseAll();
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);// 调用解析框
			gui.cls_show_msg1(1, "当前成功%d次 ,总次数为%d次", succ, bak);
		}
		
		releaseAll();
		gui.cls_show_msg1_record(TAG, funcName, g_time_0, "%s压力测试完毕,共扫码%d次,成功%d次", funcName,bak-cnt,succ);
	}


	// 无释放Camera的扫码压力
	private void noReleaseScanPre() 
	{
		String funcName = "noReleaseScanPre";
		boolean isCompareScan=false;
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 100,"默认扫码次数100，测试人员自行修改"));
		bak = cnt = packet.getLifecycle();
		
		// 是否需要自行校验扫码结果
		if(gui.cls_show_msg("测试人员是否手动校验扫码结果")==ENTER)
		{
			isCompareScan = true;
		}
		
		/**测试前置,释放扫码资源*/
		releaseAll();
		
		initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
		mSoftEngine.setScanningCallback(mScanningCallback);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
		while (cnt > 0) {
			cnt--;
			if(gui.cls_show_msg2(0.5f, "第%d次测试,请将条形码或二维码放在%s摄像头位置[配置为多码同图请放置/SVN/scan/mul_qr.png]-------ESC键退出测试",bak-cnt,mCameraMsg)==ESC)
			{
				releaseAll();
				break;
			}
			isScan = true;
			mCodeResult ="";
			synchronized (mLockObj) {
				try {
					mLockObj.wait(SCAN_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isScan = false;
			
			LoggerUtil.e("isScan="+isScan+"|||"+mCodeResult);
			if(mCodeResult.equals(""))
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:扫码超时,当前次数%d,成功%d次", Tools.getLineInfo(), bak - cnt, succ);
				continue;
			}
			else if (isCompareScan) {
				if(gui.cls_show_msg("扫码结果=%s,是否与实际码值一致", mCodeResult)==ENTER)
					succ++;
				else
				{
					gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:扫码结果与实际码值不一致，扫码结果=%s,当前次数%d,成功%d次", Tools.getLineInfo(), mCodeResult,bak - cnt, succ);
					continue;
				}
			}
			else {
				gui.cls_show_msg1_record(TAG, funcName, 1,"line %d:不校验扫码结果，扫码结果=%s，记录到result.txt", Tools.getLineInfo(), mCodeResult);
				succ++;
			}
			gui.cls_show_msg1(1, "noReleaseScanPre剩余%d次，成功%d次", cnt, succ);
			
			// 每次扫完要停止扫码

		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseAll();
		gui.cls_show_msg1_record(TAG, funcName, g_time_0, "%s压力测试完毕,共扫码%d次,成功%d次",funcName,bak-cnt, succ);
	}
	
	
	//反复扫码性能
	private void noReleaseScanAbility() 
	{
		
		/**测试前置，释放扫码资源*/
		releaseAll();
		
		String funcName="noReleaseScanAbility";
		int cnt = 0, bak = 0, succ = 0;
		float timeTotal=0.0f;
		float timeAverage=0.0f;
		
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 50,"性能默认次数50，可根据测试修改"));
		bak = cnt = packet.getLifecycle();
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
		mSoftEngine.setScanningCallback(mScanningCallback);
		gui.cls_show_msg2(0.1f,"等待摄像头对焦。。。。。。。。。。。。。");
		SystemClock.sleep(2900);

		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
		while(cnt > 0)
		{   
			if(gui.cls_show_msg2(0.5f, "测试中。。。请将码放在%s摄像头位置[配置为多码同图请放置/SVN/scan/mul_qr.png]-------ESC键退出测试",mCameraMsg)==ESC)
			{
				releaseAll();
				break;
			}
			cnt--;
			long startTime = System.currentTimeMillis();
			isScan = true;
			mCodeResult="";
			synchronized (mLockObj) {
				try {
					mLockObj.wait(SCAN_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isScan=false;
			LoggerUtil.e("isScan="+isScan+"|||"+mCodeResult);
			
			if(mCodeResult.equals(""))
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:扫码超时,当前次数%d,成功%d次", Tools.getLineInfo(), bak - cnt, succ);
				continue;
			}
			else {
				gui.cls_printf(String.format("line %d:不校验扫码结果，扫码结果=%s", Tools.getLineInfo(), mCodeResult).getBytes());
				succ++;
			}
			
			float stoptime=Tools.getStopTime(startTime);
			
			
			
			// 测试完一次必须StopDecode下次才可正常扫码
			// Message message = Message.obtain(mMessageHandler, MSG_STOP_DECODE);
			// message.sendToTarget();
			
			if(!mCodeResult.equals(""))// 扫码成功才计算时间
				timeTotal = timeTotal + stoptime;
			gui.cls_show_msg1(1, "%s性能测试剩余%d次，成功%d次", funcName,cnt, succ);
			
			Log.d("eric", "stoptime=="+stoptime);
			Log.d("eric", "timeAility=="+timeTotal);
			
		}
		// 测试后置
		releaseAll();
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		timeAverage= timeTotal/succ;
		gui.cls_show_msg1_record(TAG, funcName, g_time_0, "%s测试完毕，总扫码时间=%.2fms，平均每次扫码时间%.2fms/次,成功%s次", funcName,timeTotal,timeAverage,succ);
	}
	
	/***
	 * 释放Camera方式解析码的性能
	 */
	private void releaseCameraAbility() 
	{
		/**测试前置，释放扫码资源*/
		releaseAll();
		
		String funcName = "releaseCameraAbility";
		float timeAverage;
		float timeAility = 0.0f;
		float stopTime = 0.0f;
		
		int cnt = 0,bak,succ=0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 50,"性能默认次数50，可根据测试修改"));
		bak = cnt = packet.getLifecycle();// 压力测试一次书设置
		
		while (cnt > 0) {
			if(gui.cls_show_msg2(0.5f, "测试中。。。请将码放在%s摄像头位置[配置为多码同图请放置/SVN/scan/mul_qr.png]-------ESC可退出测试", mCameraMsg)==ESC)
			{
				// 测试后置操作
				releaseAll();
				break;
			}
			long startTime = System.currentTimeMillis();
			
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
			initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
			mSoftEngine.setScanningCallback(mScanningCallback);
			cnt--;
			isScan = true;
			mCodeResult="";
			synchronized (mLockObj) {
				try {
					mLockObj.wait(SCAN_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isScan=false;
			LoggerUtil.e("isScan="+isScan+"|||"+mCodeResult);
			
			if(mCodeResult.equals(""))
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:扫码超时,当前次数%d,成功%d次", Tools.getLineInfo(), bak - cnt, succ);
				releaseAll();
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);// 调用解析框
				continue;
			}
			else {
				gui.cls_printf(String.format("line %d:不校验扫码结果，扫码结果=%s", Tools.getLineInfo(), mCodeResult).getBytes());
				succ++;
			}
			// 测试完一次必须StopDecode下次才可正常扫码
			releaseAll();
			
			stopTime = System.currentTimeMillis()-startTime;
			if(!mCodeResult.equals(""))// 扫码成功才计算时间
				timeAility = timeAility + stopTime;
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);// 调用解析框
			
			gui.cls_show_msg1(1, "%s性能测试剩余%d次，成功%d次", funcName,cnt, succ);

		}
		/**测试后置*/
		releaseAll();
		timeAverage = (timeAility) / succ;
		gui.cls_show_msg1_record(TAG, funcName, g_time_0,"%s测试完毕，总扫码时间=%.2fms,平均每次扫码时间%.2fms/次,成功%s次", funcName,timeAility,timeAverage, succ);
	}
	
	/*
	 * 休眠唤醒压力
	 */
	private void sleepWakePre() 
	{
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
		/**测试前置，释放扫码资源*/
		releaseAll();
		
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
				
			
			initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
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
				mSoftEngine.setScanningCallback(mScanningCallback);
				initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
				isScan = true; 
				mCodeResult = "";
				synchronized (mLockObj) {
					try {
						mLockObj.wait(SCAN_TIMEOUT);
						isScan=false;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				LoggerUtil.e("isScan=" + isScan + "|||" + mCodeResult);
				if(mCodeResult.equals(""))
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:%s第(%d)次休眠唤醒第(%d)次扫码测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM,j,i, ret);
					scanFalse_Times++;
				}
				else
					gui.cls_show_msg1_record(TAG, funcName, 2,"扫码结果=%s,扫码结果记录到result.txt",mCodeResult);
				LoggerUtil.i("for,i="+i);
			}
			
			
			/**扫码结束后进行休眠唤醒,15s后进入休眠*/
			int total = (int)(1+Math.random()*SleepTime)*60;//休眠10~SleepTime内的随机时长(min)
			LoggerUtil.d("sleepWakePre,total="+total);
			
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
			initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
			if(mIsPreview){
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//后置有预览
			}else{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
			gui.cls_show_msg1(1,"正在进行第(%d)次切换扫码",j);
			mSoftEngine.setScanningCallback(mScanningCallback);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
			isScan = true; 
			mCodeResult = "";
			synchronized (mLockObj) {
				try {
					mLockObj.wait(SCAN_TIMEOUT);
					isScan=false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LoggerUtil.e("isScan=" + isScan + "|||" + mCodeResult);
			if(mCodeResult.equals(""))
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:%s休眠唤醒切换扫码测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM, ret);
				scanFalse_Times++;
			}
			else
				gui.cls_show_msg1_record(TAG, funcName, 2,"扫码结果=%s,记录到result.txt", mCodeResult);
			releaseAll();
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//恢复界面显示	
		gui.cls_show_msg1_record(TAG, funcName, g_time_0,"休眠唤醒压力扫码测试结束,共进行(%d)次扫码,失败(%d)次",allscan_times,scanFalse_Times);
		
		/**测试后置,设置为永不休眠*/
		releaseAll();
		Tools.setSreenTimeout(myactivity, preSreenTimeout);
	}

	public void scanAbnormal()
	{
		// 测试前置会删除全部的文件
		deleteSomeFile(true);
		
		abnarmalExit=false;
		isAbnormalTest=true;
		// 长时间未放置条码的异常测试
		/**测试前置，释放扫码资源*/
		releaseAll();
		
		String funcName = "scanAbnormal";
		
		isScan=true;
		gui.cls_show_msg("【设备处于永不休眠状态，外接电源】长时间未放置条码测试中。。专项测试时至少要测试一晚上，任意键开始测试-------【数字9】可退出测试");
		mSoftEngine.setScanningCallback(mScanningCallback);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);// 调用解析框
		initCamera(mCameraId,mCameraMsg, mScanDefineInfo);
	}
	

	public PreviewCallback MyPreviewCallback = new PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (mSoftEngine != null && isScan == true) {
				LoggerUtil.d("data's length=" + data.length + ",width="+ previewWidth + ",height=" + previewHeight);
				/** 要改为根据Camera的分辨率进行测试 */
				Message msg = Message.obtain(mMessageHandler, MSG_DECODE, data);
				msg.sendToTarget();
			}
		}
	};

	public void releaseAll()
	{
		Message message = Message.obtain(mMessageHandler, MSG_STOP_DECODE);
		message.sendToTarget();
		destoryCamera();
	}
	
	public void destoryCamera() {
		if (mCamera != null) {
			Log.d("eric", "destoryCamera-----");
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}
	
	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mCamera != null) {
				try {
					mCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
    
	
	public void initCamera(int cameraId,String camTip,ScanDefineInfo scanDefineInfo)
	{
		mCamera = Camera.open(cameraId);
		Parameters parameters = mCamera.getParameters();
		List<Camera.Size> previewSizes = parameters.getSupportedVideoSizes();
		for(Camera.Size supSize:previewSizes)
		{
			LoggerUtil.d("initCamera,width="+supSize.width+",height="+supSize.height);
		}
		
		CameraManagerUtil cameraManagerUtil = new CameraManagerUtil(myactivity, mCamera);
		Size previewSize = cameraManagerUtil.setCameraParams(camTip, scanDefineInfo);
		
		previewWidth = previewSize.width;
		previewHeight = previewSize.height;

		LoggerUtil.v("initCamera=width="+previewWidth+"===height="+previewHeight);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.setPreviewCallback(MyPreviewCallback);
		mCamera.startPreview();
	}
	

    @Override
	public void onDestroy() {
    	super.onDestroy();
        if (mSoftEngine != null)
        	mSoftEngine.stopDecode();
		destoryCamera();
    }
    @Override
	public void onPause() {
        super.onPause();
        if (mSoftEngine != null)
        	mSoftEngine.stopDecode();
		destoryCamera();
    }

}
