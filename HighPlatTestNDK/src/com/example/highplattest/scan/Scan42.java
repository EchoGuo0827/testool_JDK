package com.example.highplattest.scan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.newland.scan.ScanUtil;
import android.newland.scan.SoftEngine;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan42.java 
 * Author 			: zhengjiaw
 * version 			: 
 * DATE 			: 20200928
 * directory 		: 多应用初始化扫码
 * description 		: 
 * related document : 
 * history 		 	: 变更记录						变更时间			变更人员
 *			  		 F10 V1.0.1导入		       20200928       	郑佳雯
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ******************
 *******************************************************/
@SuppressLint("NewApi")
public class Scan42 extends UnitFragment
{
	private final String TESTITEM = "多应用初始化扫码";
	private String fileName=Scan42.class.getSimpleName();
	final int MAXTAPTIME = 200;
	private SoftEngine mSoftEngine;
	private boolean mIsPlaceCode=true;
	private Gui gui;
	private Camera mCamera;
	private SurfaceHolder mHolder;
	private int previewWidth=1280,previewHeight=960;
	private boolean isScan = false;
	
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
	
	public PreviewCallback MyPreviewCallback = new PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

			if (mSoftEngine != null&&isScan==true) {
				LoggerUtil.d("data's length=" + data.length + ",width=" + previewWidth + ",height=" + previewHeight);
				mSoftEngine.startDecode(data, previewWidth, previewHeight);
				if(mIsPlaceCode==false)
				{
					LoggerUtil.v("PreviewCallback|||mIsPlaceCode="+mIsPlaceCode);
					if(mCamera!=null)
					{
						SystemClock.sleep(2000);
					}
				}
			}
		}
	};
	
	private ScanningCallback mScanCallBack = new ScanningCallback() {
		
		@Override
		public void onScanningCallback(int eventCode, int codeType, byte[] data1, int length) {
			LoggerUtil.e("eventCode = " + eventCode + ",codeType=" + codeType + ",data=" + new String(data1) + ",length=" + length);
			mEventCode = eventCode;
			if(eventCode==1)// 解码成功进行码值对比
			{
				try {
					mCodeResult = new String(data1,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					mCodeResult="UnsupportedEncodingException异常抛出";
				}
			}
			if(eventCode==1||eventCode==-1)
			{
				isScan=false;
				synchronized (mLockObj) {
					mLockObj.notify();
				}
			}
		}
	}; 
		
	private Object mLockObj = new Object();
	private String mCodeResult="";
	private int mEventCode=100;
	
	public void scan42() 
	{		
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag== ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan42", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		gui.cls_show_msg1(2, "请先安装/SVN/Tool/部分案例APK/IndependendApkTest.apk, %s测试中", TESTITEM);				
		String cameraMsg="USB摄像头";
		int cameraId=-1;
		int iRet;
		
		/**测试前置*/
		ScanDefineInfo scanDefineInfo = getCameraInfo();
		cameraId = scanDefineInfo.getCameraId();
		if(cameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}
		// 旧的Camera方式测试，只支持带预览画面的摄像头
		
		mSoftEngine = SoftEngine.getInstance(myactivity);
		LoggerUtil.e("USB_CAMERA,"+scanDefineInfo.cameraReal.get(USB_CAMERA));
		
		gui.cls_show_msg1(1,"case1.1:放置二维码或一维码于%s处",cameraMsg);
		testCameraAny(mIsPlaceCode=true, cameraMsg, scanDefineInfo,1);
		mSoftEngine.stopDecode();
		releaseCamera();
		if(mEventCode!=1)
		{
			gui.cls_show_msg1_record(fileName, TESTITEM, gKeepTimeErr,"line %d:扫码失败(eventCode=%d)", Tools.getLineInfo(),mEventCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "scan42", gScreenTime,"扫码成功,请打开IndependendApkTest应用并进入IndependendApk1进行测试，该案例通过则测试通过", TESTITEM);	
	}
			
	private void testCameraAny(boolean isPlaceCode,String camTip,ScanDefineInfo scanDefineInfo,int scanTime)
	{
		String funcName="CameraAny";
		int ret;
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(mHolderCallback);
		initCamera(camTip,scanDefineInfo);
		// 初始化扫码
		mSoftEngine.setScanningCallback(mScanCallBack);
		
		for (int i = 0; i < scanTime; i++) {
			if(isPlaceCode)
				gui.cls_show_msg("请放置二维码于%s20-30cm处，放置完毕点击【确认键】",camTip);
			else
				gui.cls_show_msg1(1,"不需要放置任何的码，耐心等待解码超时");
			
			gui.cls_printf("正在扫码".getBytes());
			isScan = true;
			mCodeResult="";
			mEventCode=100;
			synchronized (mLockObj) {
			try {
					mLockObj.wait(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LoggerUtil.v("LockObject end");
			if(isPlaceCode)
			{
				if(gui.cls_show_msg("扫码结果=%s，是否跟实际码值一致",mCodeResult)!=ENTER)
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次解码失败(码值=%s)", Tools.getLineInfo(),i+1,mCodeResult);
					mSoftEngine.stopDecode();
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
			mSoftEngine.stopDecode();
		}
	}
	
	private void releaseCamera()
	{
		if(mCamera!=null)
		{
			// 测试后置：释放扫码
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera=null;
		}
	}
	
	public void initCamera(String camTip,ScanDefineInfo scanDefineInfo)
	{
		LoggerUtil.e("initCamera,camTip="+camTip);
		if(camTip.contains("USB"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(USB_CAMERA));
		}
		else if(camTip.contains("前置"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(FONT_CAMERA));
		}
		else if(camTip.contains("后置"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(BACK_CAMERA));
		}
		else if(camTip.contains("支付"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA));
		}
		
		CameraManagerUtil cameraManagerUtil = new CameraManagerUtil(myactivity, mCamera);
		Size previewSize = cameraManagerUtil.setCameraParams(camTip, scanDefineInfo);
		
		previewWidth = previewSize.width;
		previewHeight = previewSize.height;

		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.setPreviewCallback(MyPreviewCallback);
		mCamera.startPreview();
	}	
	
	@Override
	public void onTestUp() {
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_RED);
		
	}
	@Override
	public void onTestDown() {
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_BLACK);
		releaseCamera();
	}
}