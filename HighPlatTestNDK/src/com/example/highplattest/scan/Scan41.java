package com.example.highplattest.scan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.newland.scan.ScanUtil;
import android.newland.scan.SoftEngine;
import android.newland.scan.ScanUtil.ResultCallBack;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.view.SurfaceHolder;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Scan41.java 
 * directory 		: (SoftEngine)scanSet支持micro QR码的打开关闭
 * description 		: 
 * related document : 
 * history 		 	: 变更点														变更时间			变更人员
 *			  		  支持micro QR的打开与关闭(N910_A7_V1.0.05)				       20200917     	郑佳雯
 *					 默认MICROQR是关闭状态
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan41 extends UnitFragment
{
	private final String TESTITEM =  "(SoftEngine)scanSet+micro QR";
	private String fileName =Scan41.class.getSimpleName();	
	private Gui gui = new Gui(myactivity, handler);
	ScanDefineInfo scanDefineInfo;
	private SoftEngine mSoftEngine;
	private Camera mCamera;
	private SurfaceHolder mHolder;
	int mCameraId=-1;
	int mGetTimeout = 0;
	private final int  SCAN_TIMEOUT = 15*1000;
	private String mCodeType="UTF-8";
	private Object mLockObj = new Object();
	private boolean isTheTimeEnd = false;
	private boolean isScan = false;
	private int previewWidth=1280,previewHeight=960;
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

			LoggerUtil.v("onPreviewFrame|||"+isScan);
			if (mSoftEngine != null&&isScan==true) {
//				LoggerUtil.d("data's length=" + data.length + ",width="
//						+ previewWidth + ",height=" + previewHeight);
				mSoftEngine.startDecode(data, previewWidth, previewHeight);
			}
		}
	};
	
	private ScanningCallback mScanningCallback = new ScanningCallback() {
		
		@Override
		public void onScanningCallback(int eventCode, int codeType, byte[] data1, int length) {
			LoggerUtil.e("eventCode = " + eventCode + ",codeType=" + codeType + ",data=" + new String(data1) + ",length=" + length);
			if(eventCode==1)
			{
				
				try {
					mCodeResult = new String(data1,mCodeType);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					mCodeResult="UnsupportedEncodingException异常抛出";
				}
				synchronized (mLockObj) {
					mLockObj.notify();
				}
			}
			if(eventCode==-1&&isScan==false)
			{
				LoggerUtil.e("ResultCallBack:the time scan end="+eventCode);
				isTheTimeEnd=true;
			}
		}
	};
	public void scan41()
	{
		mGetTimeout = Tools.getSreenTimeout(myactivity);
		// 设置超时时间为永不休眠
		Tools.setSreenTimeout(myactivity, 30*1000*1000);
		/**测试前置*/
		scanDefineInfo = getCameraInfo();
		mCameraId = scanDefineInfo.getCameraId();
		if(mCameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}
		mSoftEngine = SoftEngine.getInstance(myactivity);		
		
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元测试\n1.重启验证", TESTITEM);
			switch (nkeyIn) {				
				
			case '0':
				unitTest();
				testEnd();
				break;
				
			case '1':
				rebootVerfity();
				break;
				
			case ESC:
				Tools.setSreenTimeout(myactivity, mGetTimeout);// 测试后置：设置回原来的时间
				isScan=false;
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	public void unitTest()
	{
		int mRet=-1;
		String funcName = "unitTest";
		String camTip = scanDefineInfo.getCameraInfo();
		LoggerUtil.d("unitTest->camTip"+camTip);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		// 测试前置:
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(mHolderCallback);
		initCamera(camTip,scanDefineInfo);
		// 初始化扫码
		mSoftEngine.setScanningCallback(mScanningCallback);	
			
		// case1:打开mirco QR的开关，可正常扫到micro的QR码值（1打开 0关闭）		
		if((mRet = mSoftEngine.scanSet("MICROQR", "Enable", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("点任意键继续测试,请放置micro QR码于后置摄像头");
		testCodeType(camTip, scanDefineInfo);	
		if(gui.cls_show_msg("扫码结果:%s;与实际码值一致？,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:关闭micro QR的开关，无法扫码micro QR的码值
		if((mRet = mSoftEngine.scanSet("MICROQR", "Enable", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("点任意键继续测试,请放置micro QR码于后置摄像头");
		testCodeType(camTip, scanDefineInfo);	
		if(gui.cls_show_msg("无法扫到micro QR码,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.2 重启后再进入扫码，MICROQR为默认关闭状态
		if(gui.cls_show_msg("重启后再进入扫码，MICROQR为默认关闭状态,是否立即重启(重启后要进入1.重启验证测试),[确认键]重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		gui.cls_show_msg1_record(fileName, funcName, 0, "子用例1：测试通过");
	}                                        
	
	public void rebootVerfity()
	{
		gui.cls_show_msg("重启验证:MICROQR码于摄像头处,放置完毕任意键继续");
		String camTip = scanDefineInfo.getCameraInfo();
		testCodeType(camTip, scanDefineInfo);	
		gui.cls_show_msg1_record(fileName, "rebootVerfity", 0, "无法扫出MICROQR码才可视为测试通过");		
	}
	
	/**
	 * 测试码
	 * @param isCompare 是否比较码值
	 * @param codeType 扫码的类型
	 * @param camTip 扫码的摄像头
	 * @param scanDefineInfo 摄像头信息
	 * @param codeValue 比较的码值
	 */
	private void testCodeType(String camTip,ScanDefineInfo scanDefineInfo)
	{
		gui.cls_printf("正在扫码".getBytes());
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
		mSoftEngine.stopDecode();
//		gui.cls_show_msg("扫码结果=%s", mCodeResult);
		LoggerUtil.v("testCodeType|||"+isScan+",isStartYuvDecode="+isTheTimeEnd);
//		testEnd();
	}

	private void testEnd()
	{
		LoggerUtil.v("testEnd||stopDecode");
		if(mSoftEngine!=null)
		{
			// 停止扫码操作
			mSoftEngine.stopDecode();
		}

		if(mCamera!=null)
		{
			// 测试后置：释放扫码
			mHolder.removeCallback(mHolderCallback);
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera=null;
		}
	}
/*	public void initCamera(String camTip,ScanDefineInfo scanDefineInfo)
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
		cameraManagerUtil.setCameraParams(camTip, scanDefineInfo);
		
		//previewWidth = previewSize.width;
		//previewHeight = previewSize.height;

		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.setPreviewCallback(MyPreviewCallback);
		mCamera.startPreview();
	}
*/
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
	}

};