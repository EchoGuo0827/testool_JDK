package com.example.highplattest.scan;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import android.R.integer;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.newland.scan.ScanUtil;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan30.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200219
 * directory 		: scan-enable参数测试
 * description 		: 
 * related document : 
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		  	创建				20200219		陈丁
 *					增加摄像头打开失败后释放	20200904		郑薛晴	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan27  extends UnitFragment  implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.PreviewCallback {
	private String fileName=Scan27.class.getSimpleName();
	//by chending
	private final String TESTITEM = "(百万库验证)scan-enable参数测试";
	private Gui gui ;
//	private int cam;
	private int cameraid;
	private Camera mCamera;
	private int mCurrZoom = 0;
	 private int mCurrExposure = -11;
	 private SurfaceHolder mSurfaceHolder = null;  
	 private int scanenable=2;
	 private Handler mMessageHandler = new Handler(){
		 public void handleMessage(android.os.Message msg) 
		 {
			  switch (msg.what)
	            {
	                case 1:
	                	surfaceView.setVisibility(View.GONE);
	                	break;
	                case 2:
	                	surfaceView.setVisibility(View.VISIBLE);
	                	break;
	            }
		 }
	 };
	 
	String fontPath = "/sys/class/front_camera/camera_name";		//前置摄像头接口节点解析路径
	String backPath = "/sys/class/back_camera/camera0_name";		//后置摄像头接口节点解析路径
	public void scan27()
	{
		gui=new Gui(myactivity, handler);
		int camCount;
		
		String fontName = LinuxCmd.readDevNode(fontPath);
		LoggerUtil.d("002-font name="+fontName);
		String backName = LinuxCmd.readDevNode(backPath);
		LoggerUtil.d("002-back name="+fontName);
		// 增加修改控制ov5675、gc5024、gc5025、gc5035曝光行
		if(fontName.equalsIgnoreCase("ov5675")||fontName.equalsIgnoreCase("gc5024")
				||fontName.equalsIgnoreCase("gc5025")||fontName.equalsIgnoreCase("gc5035"));
		else if(backName.equalsIgnoreCase("ov5675")||backName.equalsIgnoreCase("gc5024")
				||backName.equalsIgnoreCase("gc5025")||backName.equalsIgnoreCase(""));
		else
		{
			int nkey = gui.cls_show_msg("非优化的摄像头型号，仍要测试请点击确定，测试过程中未优化的摄像头出现的问题请自行判断，其他按键退出本案例");
			if(nkey!=ENTER)
			{
				unitEnd();
				return;
			}
		}
		
		
		camCount = Camera.getNumberOfCameras();
		ScanDefineInfo scanDefineInfo = getCameraInfo();
		gui.cls_show_msg1(5, "测试中-----当前机器共有%d个摄像头", camCount);
		mSurfaceHolder = surfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		
		//case1:不设置scan-enable参数。打开前后置摄像头观察预览,预期无优化
		if((cameraid = scanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
		{
			testCameraAny("前置摄像头");
		}
		
		if((cameraid = scanDefineInfo.cameraReal.get(BACK_CAMERA))!=-1)
		{
			testCameraAny("后置摄像头");
		}
		
		if((cameraid = scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA))!=-1)
		{
			testCameraAny("支付摄像头");
		}
		
		if((cameraid = scanDefineInfo.cameraReal.get(USB_CAMERA))!=-1)
		{
			testCameraAny("USB摄像头");
			
		}
		
		// case2:构建打开摄像头失败后去做释放动作 20200904 N910_邮储_V2.2.58导入
		if(GlobalVariable.gCustomerID==CUSTOMER_ID.PSBC)
		{
			gui.cls_show_msg1(5, "case2:异常测试->设置scan-enable参数为1(扫码模式)，构建打开摄像头失败后去做释放动作");
			ScanUtil scanUtil = new ScanUtil(myactivity, surfaceView, 10, true, TIMEOUT_SCAN, 1);
			for (int i = 0; i < 5; i++) {
				gui.cls_show_msg1(1, "还剩%ds",5-i);
				
			}
			scanUtil.release();
		}
		
		// 测试后置
		gui.cls_show_msg1(1, "即将恢复摄像头为拍照模式");
		testDownManul(scanDefineInfo);
		gui.cls_show_msg1_record(TESTITEM, "Scan30",5, "测试通过");
	}
	
	private void testCameraAny(String camTip)
	{
		//case3:不设置scan-enable参数为1(扫码模式)。打开支付扫描头观察预览，预期无优化
		gui.cls_show_msg1(5, "不设置scan-enable参数，即将打开%s,请观察预览.5s后切回，预期无优化",camTip);
		scanenable=2;
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		mMessageHandler.sendEmptyMessage(1);
		mMessageHandler.sendEmptyMessage(2);

		for (int i = 0; i < 5; i++) {
			gui.cls_show_msg1(1, "还剩%ds",5-i);
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框

		//case4:设置scan-enable参数为1(扫码模式)。打开后置摄像头观察预览，预期有优化
		gui.cls_show_msg1(5, "设置scan-enable参数为1(扫码模式)，即将打开%s,请观察预览.5s后切回，预期有优化",camTip);
		scanenable=1;
		mSurfaceHolder.addCallback(this);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		mMessageHandler.sendEmptyMessage(1);
		mMessageHandler.sendEmptyMessage(2);
		for (int i = 0; i < 5; i++) {
			gui.cls_show_msg1(1, "还剩%ds",5-i);
			
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
	}
	
	private void testDownManul(ScanDefineInfo scanDefineInfo)
	{
		// 测试后置，默认设置 拍照模式2
		// 后置设置为默认拍照模式
		if((cameraid=scanDefineInfo.cameraReal.get(BACK_CAMERA))!=-1)
		{
			scanenable=2;
			mSurfaceHolder.addCallback(this);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			mMessageHandler.sendEmptyMessage(1);
			mMessageHandler.sendEmptyMessage(2);
			for (int i = 0; i < 5; i++) {
				gui.cls_show_msg1(1, "还剩%ds",5-i);
				
			}
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}

		// 前置设置为默认拍照模式
		if((cameraid=scanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
		{
			scanenable=2;
			mSurfaceHolder.addCallback(this);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			mMessageHandler.sendEmptyMessage(1);
			mMessageHandler.sendEmptyMessage(2);
			for (int i = 0; i < 5; i++) {
				gui.cls_show_msg1(1, "还剩%ds",5-i);
				
			}
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		
		// 支付摄像头设置为默认拍照模式
		if((cameraid=scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA))!=-1)
		{
			scanenable=2;
			mSurfaceHolder.addCallback(this);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			mMessageHandler.sendEmptyMessage(1);
			mMessageHandler.sendEmptyMessage(2);
			for (int i = 0; i < 5; i++) {
				gui.cls_show_msg1(1, "还剩%ds",5-i);
				
			}
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		
		//USB摄像头设置为默认拍照模式
		if((cameraid=scanDefineInfo.cameraReal.get(USB_CAMERA))!=-1)
		{
			scanenable=2;
			mSurfaceHolder.addCallback(this);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			mMessageHandler.sendEmptyMessage(1);
			mMessageHandler.sendEmptyMessage(2);
			for (int i = 0; i < 5; i++) {
				gui.cls_show_msg1(1, "还剩%ds",5-i);
				
			}
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		destoryCamera();
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) 
	{
	      if(mCamera!=null){
	    	  	Log.d("eric", "mCamera!=null");
	            mCamera.stopPreview();
	            mCamera.startPreview();
	        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		Log.d("eric_chen", "进入surfaceCreated");
		openCameraOld(cameraid,scanenable);
		mCamera.cancelAutoFocus();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) 
	{
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		Log.d("eric_chen", "进入surfaceDestroyed");
		destoryCamera();
		
	}
	public void openCameraOld(int cameraId,int scanenable)
	{
		try {
			mCamera=Camera.open(cameraId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parameters parameters = mCamera.getParameters();
		parameters.set("orientation", "portrait");
		parameters.set("scan-enable", scanenable);
		if (cameraId==0) {
			mCamera.setDisplayOrientation(90);
		}else {
			mCamera.setDisplayOrientation(270);
		}
		
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
	
    public void createCamera(IntentActivity myactivity, int cameraId, int PreviewWidth, int PreviewHeight,int scanenable)
    {
        try {
            mCamera = Camera.open(cameraId);// 
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                Camera.Parameters parameters = mCamera.getParameters();
                
                parameters.setPreviewSize(PreviewWidth, PreviewHeight);
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                //设置对焦
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                //设置锐化
                parameters.set("sharpness", String.valueOf(0));
                //设置scan-enable参数
                parameters.set("scan-enable",scanenable);
                int nRet = parameters.getMinExposureCompensation();


                nRet = parameters.getMaxExposureCompensation();

//                
//                int MaxZoom = parameters.getMaxZoom();
//                if (parameters.isZoomSupported())
//                {
//                    if (mCurrZoom == -1)
//                    {
//                        mCurrZoom = (int)(MaxZoom*0.3);
//                    }
//                    parameters.setZoom(mCurrZoom);
//                }

                //if(nMaxSharpness!=-1)
                //	parameters.setSharpness(mCurrSharpness);
                //if(nMaxContrast!=-1)
                //	parameters.setContrast(mCurrContrast);
                parameters.setExposureCompensation(5);
                List <Size> PreviewSizeList = parameters.getSupportedPreviewSizes();
                for (Size size : PreviewSizeList)
                {
                    Log.d("eric", "width:"+size.width + " height:"+size.height);
                }
                //mCamera.startPreview();
                
                mCamera.setParameters(parameters);// 
                mCamera.setPreviewCallback(myactivity);
                mCamera.cancelAutoFocus();
                
//                setCameraDisplayOrientation(activity, cameraId, mCamera);
                //if(mDecodeMode == CONTINUE_MODE)
                	mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void destoryCamera()
    {
        if (mCamera != null)
        {	
        	Log.d("eric", "destoryCamera-----");
        	mCamera.stopPreview();
        	mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
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
}
