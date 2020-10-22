package com.example.highplattest.scan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.newland.scan.ScanUtil;
import android.newland.scan.ScanUtil.ResultCallBack;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Scan33.java 
 * description 		: initDecode(ResultCallBack callback)、stopDecode()(美团专用，A7和A9支持)
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *					     重新梳理案例设计后增加单元测试点		20200521		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan30 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil+Camera1)initDecode和stopDecode";
	private String fileName=Scan30.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	private ScanUtil mScanUtil;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private int previewWidth=1280,previewHeight=960;
	private boolean isScan = false;
	private boolean mIsPlaceCode=true;
	
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

			/**测试超时的情况，只需要进入一次即可*/
			if (mScanUtil != null&&isScan==true) {
				LoggerUtil.d("data's length=" + data.length + ",width=" + previewWidth + ",height=" + previewHeight);
				mScanUtil.startYUVDecode(data, previewWidth, previewHeight);
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
	

	
	private Object mLockObj = new Object();
	private String mCodeResult="";
	private int mEventCode=100;
	
	
	public void scan30()
	{
		while(true)
		{
			int nkey = gui.cls_show_msg("%s\n0.前置摄像头测试\n1.后置摄像头测试\n2.支付摄像头测试\n3.USB摄像头测试\n(根据产品支持的摄像头自行测试)",TESTITEM);
			switch (nkey) {
			
			case '0':
				unitTest("前置摄像头");
				break;
			
			case '1':
				unitTest("后置摄像头");
				break;
				
			case '2':
				unitTest("支付摄像头");
				break;
				
			case '3':
				unitTest("USB摄像头");
				break;

			default:
				unitEnd();
				return;
			}
		}
	}
	
	public void unitTest(String cameraMsg)
	{
		int cameraId=-1;
		int iRet;
		String funcName = "unitTest";
		
		/**测试前置*/
		ScanDefineInfo scanDefineInfo = getCameraInfo();
		cameraId = scanDefineInfo.getCameraId();
		if(cameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}
		// 旧的Camera方式测试，只支持带预览画面的摄像头
		
		mScanUtil = new ScanUtil(myactivity);

		LoggerUtil.e("BACK_CAMERA,"+scanDefineInfo.cameraReal.get(BACK_CAMERA));
		LoggerUtil.e("FONT_CAMERA,"+scanDefineInfo.cameraReal.get(FONT_CAMERA));
		LoggerUtil.e("EXTERNAL_CAMERA,"+scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA));
		LoggerUtil.e("USB_CAMERA,"+scanDefineInfo.cameraReal.get(USB_CAMERA));
		
		// case1.1:正常解析码测试
		gui.cls_show_msg1(1,"case1.1:放置二维码或一维码于%s处",cameraMsg);
		testCameraAny(mIsPlaceCode=true, cameraMsg, scanDefineInfo,1);
		mScanUtil.stopDecode();
		releaseCamera();
		// 最后发现根本不会进入到回调接口中 mEventCode==-1 无法构造？？
		if(mEventCode!=1)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:正常解码测试失败(eventCode=%d)", Tools.getLineInfo(),mEventCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1(1,"case2.2测试完毕");
		
		// case2.1:initDecode的callback=null;错误返回-1,已添加异常处理，20200727
		gui.cls_show_msg1(1, "case2.1:initDecode的callback=null测试");
		if((iRet = mScanUtil.initDecode(null))==0)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:initDecode的callback=null测试失败(ret=%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		LoggerUtil.v("iRet="+iRet);
		gui.cls_show_msg1(1,"case2.1测试完毕");
		
		// case2.2:未放置二维码或一维码于Camera处，解码超时
		gui.cls_show_msg1(1,"case2.2:未放置二维码或一维码于%s处，解码超时->超时时间1min",cameraMsg);
		testCameraAny(mIsPlaceCode=false, cameraMsg, scanDefineInfo,1);
		mScanUtil.stopDecode();
		releaseCamera();
		if(mEventCode!=-1)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:解码超时场景测试失败(%d)", Tools.getLineInfo(),mEventCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1(1,"case2.2测试完毕");
		
		//case3.1:初始化回调，camera多次调用预览画面，多次解码后释放回调接口，可多次接收到扫码结果
		gui.cls_show_msg1(1,"case3.1:初始化回调，camera多次调用预览画面，多次解码后释放回调接口，可多次接收到扫码结果");
		testCameraAny(mIsPlaceCode=true, cameraMsg, scanDefineInfo,5);
		mScanUtil.stopDecode();
		releaseCamera();
		
		gui.cls_show_msg1(1,"case3.1测试完毕");
		
		// case3.2:初始化回调，camera调用预览画面，解码成功后调用解析停止解析数据后无法再解析数据，在stopDecode之后去调用startYuv会导致程序直接奔溃，是否是正常现象
		gui.cls_show_msg1(1, "case3.2:初始化回调，camera调用预览画面，解码成功后调用解析停止解析数据后无法再解析数据");
		testCameraAny(mIsPlaceCode=true, cameraMsg, scanDefineInfo,1);
		mScanUtil.stopDecode();
		
		gui.cls_show_msg("请放置二维码于%s20-30cm处，放置完毕点击【确认键】",cameraMsg);
		
		isScan = true;
		mCodeResult="";
		synchronized (mLockObj) {
		try {
				mLockObj.wait(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//isScan值变化说明进入到callBack的监听
		if(isScan==false)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:无法解码测试失败(eventCode=%d)", Tools.getLineInfo(),mEventCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		releaseCamera();
		
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"%s测试通过", TESTITEM);
	}
	
	private void testCameraAny(boolean isPlaceCode,String camTip,ScanDefineInfo scanDefineInfo,int scanTime)
	{
		/**放到方法里，放在公共区域 850会报错*/
		ResultCallBack mResultCallback = new ResultCallBack() {

			@Override
			public void onResult(int eventCode, int codeType, byte[] data1,
					byte[] data2, int length) {
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
		
		String funcName="CameraAny";
		int ret;
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(mHolderCallback);
		initCamera(camTip,scanDefineInfo);
		// 初始化扫码
		if((ret = mScanUtil.initDecode(mResultCallback))!=0)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		LoggerUtil.d("initDecode:"+ret);
		
		for (int i = 0; i < scanTime; i++) {
			if(isPlaceCode)
				gui.cls_show_msg("请放置二维码于%s20-30cm处，放置完毕点击【确认键】",camTip);
			else
				gui.cls_show_msg1(1,"不需要放置任何的码，耐心等待解码超时");
			
			
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
			/*// 说明此时没有进入到callBack的回调接口
			if(mEventCode==100)
			{
				// 流程上控制不要去解码
				isScan=false;
			}*/
			LoggerUtil.v("LockObject end");
			if(isPlaceCode)
			{
				if(gui.cls_show_msg("扫码结果=%s，是否跟实际码值一致",mCodeResult)!=ENTER)
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次解码失败(码值=%s)", Tools.getLineInfo(),i+1,mCodeResult);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
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
