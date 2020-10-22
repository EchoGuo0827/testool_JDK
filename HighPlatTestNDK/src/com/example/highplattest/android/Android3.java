package com.example.highplattest.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import android.content.Context;
import android.content.Intent;
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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android3.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180409 
 * directory 		: 
 * description 		: 测试Android原生相机接口
 * related document : 
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		  修改Camera图片旋转		   20200415	 		魏美杰
 *                   增加FileProvider方式保存       20200526         魏美杰
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android3 extends UnitFragment {
	public final String TAG = Android3.class.getSimpleName();
	private String TESTITEM = "相机原生接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private SurfaceHolder mSurfaceHolder =null;
	private Handler childHandler,mainHandler;
	private CameraManager manager;
	private CameraDevice cameraDevice;
	private CaptureRequest.Builder previewRequestBuilder;
	private CameraCaptureSession captureSession;
    // 定义用于预览照片的捕获请求
    private CaptureRequest previewRequest;
	private String mCameraId=""+CameraCharacteristics.LENS_FACING_FRONT;
	private ImageReader imageReader;
	private Size mImageSize;
	
	private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
			//摄像头被打开时激活该方法
			@Override
			public void onOpened(CameraDevice camera) {
				cameraDevice = camera;
				//开始预览
				LoggerUtil.e("开始预览");
				createCameraPreviewSession();  
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
		
	private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    
	@SuppressWarnings("deprecation")
	public void android3() 
	{
		String funcName="android3";
		manager = (CameraManager)myactivity.getSystemService(Context.CAMERA_SERVICE);
		gui.cls_show_msg("测试前请确保设备有相机硬件且相机可正常使用，完成任意键继续");
		// 是否有摄像头硬件
		ScanDefineInfo scanInfo=getCameraInfo();
		if (scanInfo.getCameraCnt() <= 0) {
			gui.cls_show_msg1_record(TAG, "android3",gScreenTime, "当前设备中无相机硬件,请更换设备", TESTITEM);
			return;
		}
		
		// 在Androd7以上才支持FileProvider
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
		{
			gui.cls_show_msg("请开启AndroidManifest中的Provider组件，开启后任意键继续");
		}
		
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		mSurfaceHolder=surfaceView.getHolder(); 
		//显示预览框控件
	
		// case1.1:系统调用相机,预期成功
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
		{
			//case1.2:系统调用相机使用FileProvider保存到指定位置,预期成功
			gui.cls_show_msg("打开原生相机时拍照完毕记得点底部的确认按钮才会保存图片，任意键开始测试");
		    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if(pictureIntent.resolveActivity(myactivity.getPackageManager()) != null){ 
		        File photoFile = null;
		        try {
		            photoFile = createImageFile();
		        } catch (IOException e) {
		            e.printStackTrace();
		            return;
		        }      
		        if (photoFile != null) {
		            Uri photoURI = FileProvider.getUriForFile(myactivity,"com.example.highplattest.fileprovider",photoFile);
		            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
		            startActivityForResult(pictureIntent,REQUEST_CAPTURE_IMAGE);
		        }
		    }
			if(gui.cls_show_msg("是否打开系统相机，且进行拍照录像且保存到/Android/data/com.example.highplattest/files，[确认]是，[其他]否")!=ENTER)
			{
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试不通过，预期原生的相机拍照FileProvider保存可以正常实现", Tools.getLineInfo(), TESTITEM);
			}
		}
		else
		{
			try {
				Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
				myactivity.startActivity(intent);
			} catch (Exception e) {
				e.getMessage();
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:系统调用相机失败",Tools.getLineInfo());
			}
			if(gui.cls_show_msg("是否打开系统相机，且进行拍照录像且保存等正常，[确认]是，[其他]否")!=ENTER)
			{
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试不通过，预期原生的相机可以正常使用", Tools.getLineInfo(), TESTITEM);
			}
		}
		
		//case2：Camera2方式打开相机
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		initCamera();
		mSurfaceHolder.addCallback(mcaCallback); // SurfaceHolder加入回调接口
	
		// 10s后自动捕捉画面，保存在路径
		gui.cls_show_msg1(10, "请调整拍照预览页面，10s后捕捉画面保存", TESTITEM);
		takePicture();
		if(gui.cls_show_msg("相机是否能拍照且图片保存等正常，[确认]是，[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android3", gKeepTimeErr,"line %d:%s测试不通过，预期原生的相机可以正常使用", Tools.getLineInfo(), TESTITEM);
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		stopCamera();
		gui.cls_show_msg1(gScreenTime, "%s测试通过", TESTITEM);
	}
	
	private File createImageFile() throws IOException{
	    String imageFileName = "PicTest_"+System.currentTimeMillis();
	    File storageDir = myactivity.getExternalFilesDir("");
	    Log.e("filepath",storageDir.toString()+"");
	    File image = File.createTempFile(imageFileName, ".jpg", storageDir);
	    return image;
	}
	
	
	// SurfaceHolder的回调接口
	private SurfaceHolder.Callback mcaCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			LoggerUtil.v(TAG+",surfaceDestroyed");
			

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			LoggerUtil.v(TAG+",surfaceCreated");
			
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			LoggerUtil.v(TAG+",surfaceCreated");
		}
	};

	/**
	 * 相机初始化
	 * 
	 */
	public void initCamera() {
		try {
			 CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
			  //支持的STREAM CONFIGURATION
	          StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
	          //摄像头支持的预览Size数组
	          mImageSize = map.getOutputSizes(ImageFormat.JPEG)[0];
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		HandlerThread handlerThread = new HandlerThread("Camera2");
	    handlerThread.start();
	    childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
   
		try {
            manager.openCamera(mCameraId, stateCallback, mainHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} 
		
	}
	public void stopCamera(){
		 if (null != captureSession) {
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
	
	 private void createCameraPreviewSession() {
			setupImageReader();
	        try {
	            // 创建作为预览的CaptureRequest.Builder
	            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
	            // 将surfaceView的surface作为CaptureRequest.Builder的目标
	            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
	            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
	            cameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(),imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
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
//	                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
	                                // 开始显示相机预览
	                                previewRequest = previewRequestBuilder.build();
	                                // 设置预览时连续捕获图像数据
	                                captureSession.setRepeatingRequest(previewRequest,null, childHandler); 
	                           
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

	public void takePicture() {
	
		if (cameraDevice == null)
			return;
		// 创建拍照需要的CaptureRequest.Builder
		final CaptureRequest.Builder captureRequestBuilder;
		try {
			captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			// 将imageReader的surface作为CaptureRequest.Builder的目标
			captureRequestBuilder.addTarget(imageReader.getSurface());
			// 自动对焦
			captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			// 自动曝光
			captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			// 获取手机方向
			int rotation = myactivity.getWindowManager().getDefaultDisplay().getRotation();
			LoggerUtil.i("rotation="+rotation);
			// 根据设备方向计算设置照片的方向
			captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));
			// 拍照
			CaptureRequest mCaptureRequest = captureRequestBuilder.build();
			captureSession.capture(mCaptureRequest, null, childHandler);
			captureSession.stopRepeating();
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}
	    

	 private void setupImageReader() {
		 final String fileName=Environment.getExternalStorageDirectory().toString()
                  +File.separator
                  +"AppTest"
                  +File.separator
                  +"PicTest_"+System.currentTimeMillis()+".jpg";
	      final File mfile=new File(fileName);
	      if(!mfile.getParentFile().exists()){
	    	  mfile.getParentFile().mkdir();//创建文件夹
          }
		 //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据，本例的2代表ImageReader中最多可以获取两帧图像流
		 imageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(),  ImageFormat.JPEG, 2);
		 //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
		 imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
		        @Override
		        public void onImageAvailable(ImageReader reader) {
		        	Image image = reader.acquireLatestImage();
		        	ByteBuffer buffer = image.getPlanes()[0].getBuffer();
		            byte[] bytes = new byte[buffer.remaining()];
		            buffer.get(bytes);
		            FileOutputStream output = null;
		            try {
		                output = new FileOutputStream(mfile);
		                output.write(bytes);
		                Toast.makeText(myactivity, "拍照成功，照片保存在"+fileName+"文件之中！", Toast.LENGTH_LONG).show();
		            } catch (IOException e) {
		                e.printStackTrace();
		                Toast.makeText(myactivity, "拍照失败！"+e.toString(), Toast.LENGTH_LONG).show();
		            } finally {
		            	image.close();
		                if (null != output) {
		                    try {
		                        output.close();
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    }
		                }
		            }

		        }
		    }, mainHandler);
		}
	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {
	
	}

}
