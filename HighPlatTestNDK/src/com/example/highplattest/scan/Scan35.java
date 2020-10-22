package com.example.highplattest.scan;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.newland.scan.SoftEngine;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan20.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180322 
 * directory 		: 数据解析模块 (NLS)(美团专用，A7和A9支持)
 * description 		: startDecode
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		  RGB解析方式改为不支持			20200408     	zhengxq
 *					      该用例是A7和A9产品以及美团固件支持	20200423		zhengxq
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan35 extends UnitFragment
{
	private String fileName=Scan32.class.getSimpleName();
	private final String TESTITEM = "(SoftEngine+Camera2)startDecode";
	private SoftEngine mSoftEngine;
	private SurfaceHolder mSurfaceHolder = null;  // SurfaceHolder对象：(抽象接口)SurfaceView支持类 
	//控制是否扫到码
	Boolean mIsScanSuc=false;
	//控制是否超时
	Boolean timeFlag=false;
	int frontFlag=-1;
	String scanResult="";
	
	int timeOut=30*1000;//超时时间20s,有线程控制超时时间
	String path=GlobalVariable.sdPath+"scan/";
	Object lock =new Object();
	
	private ScanDefineInfo mScanDefineInfo;


	// 定义代表摄像头的成员变量
    private CameraDevice cameraDevice;

    private CaptureRequest.Builder previewRequestBuilder;
    // 定义用于预览照片的捕获请求
    private CaptureRequest previewRequest;
    // 定义CameraCaptureSession成员变量
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private CameraManager cameraManager;
	Handler childHandler,mainHandler;
	String[][] pic=
			{{"QR_UTF8_1.png","方式",	"UTF-8"},
			{"QR_GBK.png",QR_GBK,	"GBK"},
			{"Code39.png","Co39","UTF-8"},
			{"Code93.png","ABCabc123","UTF-8"},
			{"PDF417.png","PDF417:1A2B3C4D","UTF-8"}
			};
	
	private SurfaceHolder.Callback 	surfaceCallback = new Callback() 
	{

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        	LoggerUtil.e("SurfaceHolder.surfaceDestroyed");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	LoggerUtil.e("SurfaceHolder.surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        	LoggerUtil.e("SurfaceHolder.surfaceChanged");
        	
        }
    };
	
    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
		//摄像头被打开时激活该方法
		@Override
		public void onOpened(CameraDevice camera) {
			cameraDevice = camera;
			//开始预览
			LoggerUtil.e("开始预览");
			createCameraPreviewSession();  // ②
		}
		//摄像头断开连接时激活该方法
		@Override
		public void onError(CameraDevice camera, int error) {
			
		}
		//打开摄像头出现错误时激活该方法
		@Override
		public void onDisconnected(CameraDevice camera) {
			
		}
	};
	
	private ScanningCallback mScanningCallback = new ScanningCallback() {
		
		@Override
		public void onScanningCallback(int eventCode, int codeType, byte[] data1, int length) {
			LoggerUtil.e("mCallback,eventCode="+eventCode+"|||result="+ISOUtils.hexString(data1));
			if(!timeFlag && mIsScanSuc==false){
				LoggerUtil.e("result="+new String(data1));
				try {
					scanResult=new String(data1,strCode);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					scanResult="err:抛出UnsupportedEncodingException异常";
				}
				mIsScanSuc=true;
				synchronized (lock) {
					lock.notify();
				}
			}
			
		}
	};
	
    private Size mPreviewSize;
    private String strCode="UTF-8";
    
	public void scan35() 
	{
		/*private & local definition*/
		int cameraId=-1;
		String funcName = "scan20";
		Gui gui = new Gui(myactivity, handler);
		boolean isPreview = false;
//		mSoftEngine=SoftEngine.getInstance(myactivity);
		mSoftEngine=SoftEngine.getInstance();
		
		mSurfaceHolder = surfaceView.getHolder();
		cameraManager = (CameraManager) myactivity.getSystemService(Context.CAMERA_SERVICE);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		/*process body*/
		
		// 判断设备是否支持前置摄像头
		mScanDefineInfo=getCameraInfo();
		
		cameraId = mScanDefineInfo.getCameraId();
		if(cameraId==mScanDefineInfo.cameraReal.get(BACK_CAMERA)||cameraId==mScanDefineInfo.cameraReal.get(EXTERNAL_CAMERA))
			isPreview=true;
		if((cameraId==mScanDefineInfo.cameraReal.get(FONT_CAMERA)&&mScanDefineInfo.getCameraCnt()==1)||cameraId==mScanDefineInfo.cameraReal.get(USB_CAMERA))
			isPreview=true;
		
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		
		// case1.1:Camera2方式，后置摄像头,设置为摄像头不同的分辨率均可成功扫出码值
		/**SotEngine这个类支持1280*960以下分辨率的扫码，以上的高清分辨率暂不支持，魏春霞确认，20200701*/
		if((cameraId=mScanDefineInfo.cameraReal.get(BACK_CAMERA))!=-1)
		{
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			List<Size> mCameraSizes = getCameraSize(cameraId+"");// 分辨率较多，选部分测试
			strCode = pic[0][2];
			for(int i=0;i<mCameraSizes.size()/3;i++)
			{
				gui.cls_show_msg("请放置%s码与%s摄像头，操作完毕任意键继续",pic[0][0],"后置");/**提示具体的分辨率*/
				String resolution = String.format(Locale.CHINA,"%d*%d", mCameraSizes.get(i*3).getWidth(),mCameraSizes.get(i*3).getHeight());
				gui.cls_printf(String.format("分辨率设置为%s", resolution).getBytes());
				mSoftEngine.setScanningCallback(mScanningCallback);

				cameraScan(mCameraSizes.get(i*3),cameraId);//扫码
				if (!scanResult.equals(pic[0][1])) 
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次，%s扫码失败，分辨率=%s(预期:%s,实际:%s)", Tools.getLineInfo(),i+1,pic[0][0],resolution,pic[0][1], scanResult);
					LoggerUtil.d("调用stopDecode");
					mSoftEngine.stopDecode();
					if (!GlobalVariable.isContinue)
						return;
				}
				// 最后释放
				mSoftEngine.stopDecode();
			}
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		// case1.2:Camera2方式，前置摄像头,设置为摄像头不同的分辨率均可成功扫出码值
		if((cameraId=mScanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
		{
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			List<Size> mCameraSizes = getCameraSize(cameraId+"");// 分辨率较多，选部分测试
			strCode = pic[1][2];
			for(int i=0;i<mCameraSizes.size()/3;i++)
			{
				gui.cls_show_msg("请放置%s码于%s摄像头20-30cm，操作完毕任意键继续",pic[1][0],"前置");/**提示具体的分辨率*/
				String resolution = String.format(Locale.CHINA,"%d*%d", mCameraSizes.get(i*3).getWidth(),mCameraSizes.get(i*3).getHeight());
				gui.cls_printf(String.format("分辨率设置为%s", resolution).getBytes());
				mSoftEngine.setScanningCallback(mScanningCallback);

				cameraScan(mCameraSizes.get(i*3),cameraId);//扫码
				if (!scanResult.equals(pic[1][1])) 
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次，%s扫码失败,分辨率=%s(预期:%s,实际:%s)", Tools.getLineInfo(),i+1,pic[1][0],resolution,pic[1][1], scanResult);
					LoggerUtil.d("调用stopDecode");
					mSoftEngine.stopDecode();
					if (!GlobalVariable.isContinue)
						return;
				}
				// 最后释放
				mSoftEngine.stopDecode();
			}
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		
		// case1.3：Camera2方式，支付摄像头,设置为摄像头不同的分辨率均可成功扫出码值
		if((cameraId=mScanDefineInfo.cameraReal.get(EXTERNAL_CAMERA))!=-1)
		{
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			List<Size> mCameraSizes = getCameraSize(cameraId+"");// 分辨率较多，选部分测试
			strCode = pic[2][2];
			for(int i=0;i<mCameraSizes.size()/3;i++)
			{
				gui.cls_show_msg("请放置%s码与%s摄像头，操作完毕任意键继续",pic[2][0],"支付");/**提示具体的分辨率*/
				String resolution = String.format(Locale.CHINA,"%d*%d", mCameraSizes.get(i*3).getWidth(),mCameraSizes.get(i*3).getHeight());
				gui.cls_printf(String.format("分辨率设置为%s", resolution).getBytes());
				mSoftEngine.setScanningCallback(mScanningCallback);

				cameraScan(mCameraSizes.get(i*3),cameraId);//扫码
				if (!scanResult.equals(pic[2][1])) 
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次，%s扫码失败，分辨率=%s(预期:%s,实际:%s)", Tools.getLineInfo(),i+1,pic[2][0],resolution,pic[2][1], scanResult);
					LoggerUtil.d("调用stopDecode");
					mSoftEngine.stopDecode();
					if (!GlobalVariable.isContinue)
						return;
				}
				// 最后释放
				mSoftEngine.stopDecode();
			}
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		
		// case1.4:Camera2方式，USB摄像头,设置为摄像头不同的分辨率均可成功扫出码值,USB摄像头的分辨率较少，采用全测的方式
		if((cameraId=mScanDefineInfo.cameraReal.get(USB_CAMERA))!=-1)
		{
			LoggerUtil.e("enter USB Camera测试");
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			List<Size> mCameraSizes = getCameraSize(cameraId+"");// 分辨率较多，选部分测试
			strCode = pic[3][2];
			for(int i=0;i<mCameraSizes.size();i++)
			{
				gui.cls_show_msg("请放置%s码与%s摄像头，操作完毕任意键继续",pic[3][0],"USB");/**提示具体的分辨率*/
				String resolution = String.format(Locale.CHINA,"%d*%d", mCameraSizes.get(i).getWidth(),mCameraSizes.get(i).getHeight());
				gui.cls_printf(String.format("分辨率设置为%s", resolution).getBytes());
				mSoftEngine.setScanningCallback(mScanningCallback);

				cameraScan(mCameraSizes.get(i),cameraId);//扫码
				if (scanResult==null||!scanResult.equals(pic[3][1])) 
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次，%s扫码失败，分辨率=%s(预期:%s,实际:%s)", Tools.getLineInfo(),i+1,pic[3][0],resolution,pic[3][1], scanResult==null?"null":scanResult);
					LoggerUtil.d("调用stopDecode");
					mSoftEngine.stopDecode();
					if (!GlobalVariable.isContinue)
						return;
				}
				// 最后释放
				mSoftEngine.stopDecode();
			}
			if(isPreview)
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		}
		
		// case2:测试GBK编码解析应无问题
		cameraId = mScanDefineInfo.getCameraId();
		List<Size> mCameraSizes = getCameraSize(cameraId+"");
		if(isPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		strCode = pic[1][2];
		gui.cls_show_msg("请放置%s码与%s，操作完毕任意键继续",pic[1][0],mScanDefineInfo.getCameraInfo());/**提示具体的分辨率*/
		mSoftEngine.setScanningCallback(mScanningCallback);
		
		cameraScan(mCameraSizes.get(0),cameraId);//扫码
		if (!scanResult.equals(pic[1][1])) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s扫码失败(预期:%s,实际:%s)", Tools.getLineInfo(),pic[1][0],pic[1][1], scanResult);
			LoggerUtil.d("调用stopDecode");
			mSoftEngine.stopDecode();
			if (!GlobalVariable.isContinue)
				return;
		}
		if(isPreview)
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		
		/**测试后置*/
		stopScan();
		
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"%s测试通过", TESTITEM);
	}
	
	
	/**获取摄像头分辨率*/
	public List<Size> getCameraSize(String mCameraId)
	{
		List<Size> sizes = new ArrayList<Size>();
		int count = 0;
	    LoggerUtil.e("getCameraSize="+mCameraId);
		try {
//			CameraCharacteristics.LENS_FACING_FRONT;// 前置呀
			 CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
			 int faceAngle = characteristics.get(CameraCharacteristics.LENS_FACING);/*代表了相机的朝向*/
			 LoggerUtil.e("faceAngle="+faceAngle);
			 
			  //支持的STREAM CONFIGURATION
	          StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
	          //摄像头支持的预览Size数组
	          Size[] tempSizes = map.getOutputSizes(ImageFormat.YUV_420_888);
	        	  
//	          size = map.getOutputSizes(ImageFormat.YUV_420_888).length;
	          for(Size xx:tempSizes)
	          {
//	        	  LoggerUtil.d("getCameraSize1,width="+xx.getWidth()+"==="+xx.getHeight());
	        	  if(xx.getWidth()<=1280&&xx.getHeight()<=960)
	        	  {
	        		  sizes.add(xx);
	        		  count++;
//	        		  LoggerUtil.v("getCameraSize3,width="+xx.getWidth()+"==="+xx.getHeight());
	        	  }
	          }
	          LoggerUtil.v("getCameraSize===count="+count+"===sizes="+sizes.size());
//	          mPreviewSize= map.getOutputSizes(SurfaceTexture.class)[0];
	          
//	          LoggerUtil.d("initCamera,mPreviewSize="+mPreviewSize.getWidth()+"==="+mPreviewSize.getHeight());

		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		return sizes;
	}
	
	
	/**
	 * 初始化摄像头摄像头
	 */
	public void initCamera(Size size,String mCameraId)
	{
		try {
			 CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
			  //支持的STREAM CONFIGURATION
	          StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
	          
	          int angle = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
				 
			LoggerUtil.e("angle="+angle);
			
	          //摄像头支持的预览Size数组
	          mPreviewSize = size;
//	          mPreviewSize = map.getOutputSizes(ImageFormat.YUV_420_888)[0];
	          LoggerUtil.d("initCamera,mimageSize="+mPreviewSize.getWidth()+"==="+mPreviewSize.getHeight());
	     
//	          mPreviewSize= map.getOutputSizes(SurfaceTexture.class)[0];
	          
//	          LoggerUtil.d("initCamera,mPreviewSize="+mPreviewSize.getWidth()+"==="+mPreviewSize.getHeight());

		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		HandlerThread handlerThread = new HandlerThread("Camera2");
	    handlerThread.start();
	    childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
   
		try {
            cameraManager.openCamera(mCameraId, stateCallback, mainHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} 
	}

	 private void createCameraPreviewSession() {
	        try {
	        	setupImageReader();
	            // 创建作为预览的CaptureRequest.Builder
	            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
	            // 将surfaceView的surface作为CaptureRequest.Builder的目标
	            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
	            previewRequestBuilder.addTarget(imageReader.getSurface());
	            
	            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
	            cameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface() , imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
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
	                                // 设置保存照片角度
//	                                previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
	                                // 设置自动曝光模式
//	                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
	                                // 开始显示相机预览
	                                previewRequest = previewRequestBuilder.build();
	                                // 设置预览时连续捕获图像数据
	                                captureSession.setRepeatingRequest(previewRequest,null, childHandler);  // ④
	                           
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

	 private void setupImageReader() {
		
		 //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据，本例的2代表ImageReader中最多可以获取两帧图像流
		 imageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),  ImageFormat.YUV_420_888, 2);
		 //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
		 imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
		        @Override
		        public void onImageAvailable(ImageReader reader) {
//		         LoggerUtil.e("setupImageReader="+scanCount);
		        	if(mSoftEngine!=null&&mIsScanSuc==false)
					{
						  Image image = reader.acquireLatestImage();
						  if(image==null)
						  {
							  LoggerUtil.d("setupImageReader,image==null");
							  return;
						  }
				          //我们可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
//				          ByteBuffer buffer = image.getPlanes()[0].getBuffer();
				          int ylen = mPreviewSize.getWidth()*mPreviewSize.getHeight();
				          byte[] data = new byte[(int) (ylen*1.5)];
				          byte[] yuvData = new byte[ylen];
				          int remainlen = image.getPlanes()[0].getBuffer().remaining();
				          image.getPlanes()[0].getBuffer().get(yuvData);
				          
				          System.arraycopy(yuvData, 0, data, 0, ylen);
				          LoggerUtil.d("data len ="+data.length);
				          LoggerUtil.e("data="+remainlen+"|||width="+mPreviewSize.getWidth()+"|||height="+mPreviewSize.getHeight());
				          mSoftEngine.startDecode(data, mPreviewSize.getWidth(),mPreviewSize.getHeight());
				          image.close();
					}
		        }
		    }, mainHandler);
		}
	/**
	 * YUV调用相机预览解析
	 */
	public void cameraScan(Size size,int cameraId){
		timeFlag=false;
		mIsScanSuc = false;

		mSurfaceHolder.setKeepScreenOn(true);
		mSurfaceHolder.addCallback(surfaceCallback);
		initCamera(size,cameraId+"");
	
		// 设置超时时间
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				 synchronized (lock) {
					 // 设定超时任务
					mIsScanSuc = true;
					scanResult = "";
					timeFlag=true;
					LoggerUtil.e("超时");
				 }
			}
		}, timeOut);
		 synchronized (lock) {
			 try {
				lock.wait(timeOut);
				LoggerUtil.e("lock wait end");
				stopCamera();// 类似于释放扫码操作
				timer.cancel();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
	}
/*	*//**
	 *等待图片解析结果
	 *//*
	public void waitScanParse(){
//		 synchronized (lock) {
//			 try { 
//				 lock.wait();
//				 scanCount = false;
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		 }
		timeFlag=false;
		mIsScanSuc = false;
		while(!mIsScanSuc){
			SystemClock.sleep(100);
		}
	}*/
	/**
	 * 释放摄像头
	 */
	public void stopCamera(){
		 if (null != captureSession) {
			 try {
				captureSession.stopRepeating();
			} catch (CameraAccessException e) {
				e.printStackTrace();
			}
			 captureSession.close();
			 captureSession = null;
         }
         if (null != cameraDevice) {
        	 cameraDevice.close();
        	 cameraDevice = null;
         }
         if (null != imageReader) {
        	 imageReader.close();
        	 imageReader = null;
         }
	}
	
	public void stopScan()
	{
		if(null!=mSoftEngine)
		{
			mSoftEngine.stopDecode();
			mSoftEngine=null;
		}
	}
	
	@Override
	public void onTestUp() {
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_RED);
		
	}

	@Override
	public void onTestDown() {
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_BLACK);
		//多次stopDecode会抛出异常
		if(mSoftEngine!=null)
			mSoftEngine.stopDecode();
	}
}

