package com.example.highplattest.main.tools;

import java.util.HashMap;
import java.util.List;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

public class CameraManagerUtil implements Lib
{
	
    // USB亮度
    String USB_BRIGHT = "luma-adaptation";
    String USB_BRIGHT_MIN = "min-brightness";
    String USB_BRIGHT_MAX = "max-brightness";
    // USB锐化
    String USB_SHARPESS = "sharpness";
    String USB_SHARPESS_MIN = "min-sharpness";
    String USB_SHARPESS_MAX = "max-sharpness";
    // USB对比
    String USB_CONTRAST = "contrast";
    String USB_CONTRAST_MIN = "min-contrast";
    String USB_CONTRAST_MAX = "max-contrast";
    
    //usb摄像头默认参数
    String defSharpness, defBright, defContrast;
    int defExposureCompensation;
    
    private Context mContext;
    private Camera mCamera;
    
    
    
    public CameraManagerUtil(Context context, Camera camera) {
        this.mContext = context;
        this.mCamera = camera;
    }
	
	
	/**设置摄像头画面的预览角度*/
	public Size setCameraParams(String campTip,ScanDefineInfo scanDefineInfo) 
	{
		Size size = mCamera.new Size(640,480);
		Parameters params = mCamera.getParameters();
//		List<Camera.Size> previewSizes = params.getSupportedVideoSizes();
//		for(Camera.Size supSize:previewSizes)
//		{
//			LoggerUtil.d("initCamera,width="+supSize.width+",height="+supSize.height);
//		}
		
		/** 分辨率默认修改为低于1280*960，X5没有导入百万库，目前分辨率支持640*480 20200702*/
		params.setPreviewSize(640, 480);
		/**目前部分码制无法解析，根据春霞建议关闭摄像头的锐化*/
		params.set("sharpness", 0);
//		for (Size tempSize:previewSizes) {
//			if(tempSize.width<=1280&&tempSize.height<=960)
//			{
//				params.setPreviewSize(tempSize.width, tempSize.height);
//				size = tempSize;
//				break;
//			}
//		}
		
		if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {// 竖屏
			LoggerUtil.d("setCameraDisplayOrientation  set portrait");
			params.set("orientation", "portrait");
			// 这边设置摄像头的旋转角度，要根据前后摄像头进行旋转
			LoggerUtil.e("setCameraDisplayOrientation="+campTip);
			if(campTip.contains("前置"))
			{
				if(GlobalVariable.currentPlatform==Model_Type.F7||GlobalVariable.currentPlatform==Model_Type.F10)
				{
					LoggerUtil.d("setCameraDisplayOrientation  rotation 180");
//					params.set("rotation", 180);
					mCamera.setDisplayOrientation(180);
				}
				else
				{
					LoggerUtil.d("setCameraDisplayOrientation  rotation 90");
//					params.set("rotation", 270);
					mCamera.setDisplayOrientation(270);
				}

			} 
			else if(campTip.contains("USB"))
			{
				LoggerUtil.e("setCameraDisplayOrientation|||USB="+scanDefineInfo.getUsbModule());
				switch (scanDefineInfo.getUsbModule()) {
				case HuaJie:
//					params.set("rotation", 0);
					mCamera.setDisplayOrientation(0);
					params.setPreviewSize(1280, 720);
					size.width = 1280;
					size.height=720;
//					// 设置曝光和亮度
					setUsbCameraParamters(params);
					break;
					
				case YuCong:
//					params.set("rotation", 90);
					mCamera.setDisplayOrientation(90);
					params.setPreviewSize(1280, 960);
					size.width = 1280;
					size.height=960;
					break;
					
				case AoBi:
//					params.set("rotation", 0);
					mCamera.setDisplayOrientation(0);
					params.setPreviewSize(1280, 960);
					size.width = 1280;
					size.height=960;
					break;

				default:
					break;
				}
			}
			else {
				LoggerUtil.d("setCameraDisplayOrientation  rotation 90");
//				params.set("rotation", 90);
				mCamera.setDisplayOrientation(90);
			}
		} else {
			LoggerUtil.d("setCameraDisplayOrientation  set landscape");
			params.set("orientation", "landscape");
//			if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) 
			if(campTip.contains("前置")||campTip.contains("USB"))
			{
//				params.set("rotation", 180);
				mCamera.setDisplayOrientation(180);
			} else {
//				params.set("rotation", 0);
				mCamera.setDisplayOrientation(0);
			}
		}
		
		mCamera.setParameters(params);
		
		return size;
	}
	
    /**
     * 设置USB摄像头参数
     *
     * @param parameters
     */
    private void setUsbCameraParamters(Parameters parameters) {

        defSharpness = parameters.get(USB_SHARPESS);
        defBright = parameters.get(USB_BRIGHT);
        defContrast = parameters.get(USB_CONTRAST);
        defExposureCompensation = parameters.getExposureCompensation();

//        DEBUG("default  sharpness=" + defSharpness);
//        DEBUG("default  bright=" + defBright);
//        DEBUG("default  contrast=" + defContrast);
//        DEBUG("default  exposure-compensation=" + defExposureCompensation);

        String minSharpness = parameters.get(USB_SHARPESS_MIN);
        String maxSharpness = parameters.get(USB_SHARPESS_MAX);

        String minBright = parameters.get(USB_BRIGHT_MIN);
        String maxBright = parameters.get(USB_BRIGHT_MAX);
        

        String minContrast = parameters.get(USB_CONTRAST_MIN);
        String maxContrast = parameters.get(USB_CONTRAST_MAX);

        int minExposureCompensation = parameters.getMinExposureCompensation();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();

        if (minExposureCompensation == 0 && maxExposureCompensation == 0) {
            defExposureCompensation = -1;
        }

        LoggerUtil.v("sharpness :" + "[" + minSharpness + "," + maxSharpness + "]");
        LoggerUtil.v("bright :" + "[" + minBright + "," + maxBright + "]");
        LoggerUtil.v("contrast :" + "[" + minContrast + "," + maxContrast + "]");
        LoggerUtil.v("exposure-compensation :" + "[" + minExposureCompensation + "," + maxExposureCompensation + "]");


        parameters.set(USB_BRIGHT, "10");// 亮度最小
        parameters.set(USB_SHARPESS, minSharpness);// 关闭锐化
        parameters.set(USB_CONTRAST, maxContrast);// 对比度最大
        if (defExposureCompensation != -1) {
            parameters.setExposureCompensation(maxExposureCompensation);// 背光补偿最大
        }
    }
}
