package com.example.highplattest.main.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;

/**
 * 主要负责设置相机的参数信息，获取最佳的预览画面
 * @author Administrator
 *
 */
public class CameraConfiguration 
{
	private static final String TAG = "CameraConfiguration";
	
	private static final int MIN_PREVIEW_PIXELS = 480*320;/**最小预览画面*/
	private static final double MAX_ASPECT_DISTORATION = 0.15;
	
	private final Context context;
	
	//屏幕分辨率
	private Point screenResolution;
	// 相机分辨率
	private Point cameraResolution;
	
	public CameraConfiguration(Context context)
	{
		this.context = context;
	}
	
	public Point initFromCameraParameters(CameraManager cameraManager)
	{
//		Camera.Parameters parameters = camera.getParameters();
		// get screen pixels
		Point theScreenResolution = new Point();
		theScreenResolution = getDisplaySize();
		
		screenResolution = theScreenResolution;
		
		/**因为换成了竖屏，所以不替换宽高得出的预览图是变形的*/
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;
		
		// 竖屏
		if(screenResolution.x<screenResolution.y)
		{
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		
		 return cameraResolution = findBestPreviewSizeValue(cameraManager, screenResolutionForCamera);
		
	}
	
	/**
	 * 从相机支持的分辨率中计算最适合的预览尺寸
	 * @return
	 * @throws CameraAccessException 
	 */
	private Point findBestPreviewSizeValue(CameraManager manager,Point screenResolution) 
	{
		 // 获取指定摄像头的特性
        CameraCharacteristics characteristics=null;
		try {
			characteristics = manager.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT+"");
		} catch (CameraAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 获取摄像头支持的配置属性
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        
        // 获取最佳的预览尺寸
        Size previewSize = chooseOptimalSize(map.getOutputSizes( SurfaceTexture.class), screenResolution);
        
		Point bestFitSize = new Point(previewSize.getWidth(),previewSize.getHeight());
		
		return bestFitSize;
	}
	private static Size chooseOptimalSize(Size[] choices, Point screenResolution)
	    {
	        // 收集摄像头支持的大过预览Surface的分辨率
	        List<Size> bigEnough = new ArrayList<Size>();
	        LoggerUtil.e("size length:"+choices.length);
	        Size best=null;
	        // 得到比值
			double ScreenAspectRadio = (double)screenResolution.x/screenResolution.y;
	        for (Size option : choices)
	        {
	        	int realWidth = option.getWidth();
				int realHeight = option.getHeight();
				if(realWidth*realHeight<MIN_PREVIEW_PIXELS)// 移除小于最小预览画面的值
				{
					continue;
				}
				// 出现这种true的情况要反转
				boolean isCandidatePortrait = realWidth<realHeight;
				int maybeFlippedWidth = isCandidatePortrait?realHeight:realWidth;
				int maybeFlippedHeight = isCandidatePortrait?realWidth:realHeight;
				
				double aspectRatio = (double)maybeFlippedWidth/(double)maybeFlippedHeight;
				// 前一个是摄像头的分辨率比值，后一个是液晶的比值
				double distortion = Math.abs(aspectRatio-ScreenAspectRadio);
				if(distortion>MAX_ASPECT_DISTORATION)
				{
					continue;
				}
				bigEnough.add(option);
				if(maybeFlippedWidth == screenResolution.x||maybeFlippedHeight == screenResolution.y)
				{
						Log.i(TAG, "Found preview size exactly matching screen size:");
						return option;
				}
	        }
	        // 如果找到多个预览尺寸，获取其中面积最小的
	        if (bigEnough.size() > 0)
	        {
	        	LoggerUtil.e("bigEnough size:"+bigEnough.size());
	            return Collections.max(bigEnough, new CompareSizesByArea());
	        }
	        else
	        {
	            System.out.println("找不到合适的预览尺寸！！！");
	            return choices[0];
	        }
	      
	    }
	 // 为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs)
        {
            // 强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
        }
    }
	/**
	 * 获取液晶屏的宽高
	 * @param display
	 * @return
	 */
	private Point getDisplaySize()
	{
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		return new Point(w_screen, h_screen);
	}
}

