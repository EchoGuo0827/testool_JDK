package com.example.highplattest.main.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraManager;
import android.view.Display;
import android.view.WindowManager;

public class CalRect 
{
	private static final String TAG = "CalRect";
	// 设置屏幕解析框在全屏的比例
	private static final double HORIZONE_RATIO = (double)1/(double)2;
	public static float RATIO = 1f / 2;
	private static final int MIN_FRAME_WIDTH = 0;
	private static final int MIN_FRAME_HEIGHT = 0;
	private static final int MAX_FRAME_WIDTH = 675; // = 5/8 * 1920
	private static final int MAX_FRAME_HEIGHT = 1200; // = 5/8 * 1080
	public static int leftOffset = 50;
	public static int topOffset = 166;
	
	private Context mContext; 
	private Point endDecode;
	public CalRect(Context context)
	{
		mContext = context;
		
	}

	/**
	 * 设置屏幕中解析框的大小
	 * 
	 * @return
	 */
	public static Rect getDecodeRect(Context context) 
	{
		android.graphics.Point point = getScreenResolution(context);
		int width = findDesiredDimensionInRange(point.x, MIN_FRAME_WIDTH,MAX_FRAME_WIDTH);
		int height = findDesiredDimensionInRange(point.y, MIN_FRAME_HEIGHT,MAX_FRAME_HEIGHT);
		return new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
	}
	
	/**
	 * 设置默认的解析框
	 * @return
	 */
	public Point setDecodeDefault(CameraManager manager)
	{
		CameraConfiguration camearaConfiguration = new CameraConfiguration(mContext);
		Point cameraResolution = camearaConfiguration.initFromCameraParameters(manager);
		double aspectRatio = (double)cameraResolution.y/(double)cameraResolution.x;
		// 宽度为整个屏幕的2/3
		int width = (int) (cameraResolution.x * HORIZONE_RATIO);
		int height = (int) (width*aspectRatio);
		LoggerUtil.d(TAG+"width:"+width+"====height:"+height);
		endDecode = new Point(width, height);
		return endDecode;
	}
	public Point getEndDecode(){
		return endDecode;
	}
	private static int findDesiredDimensionInRange(int resolution, int hardMin,
			int hardMax) {
		int dim = (int) (RATIO * resolution);
		/*
		 * if (dim < hardMin) { return hardMin; } if (dim > hardMax) { return
		 * hardMax; }
		 */
		return dim;
	}
	
	@SuppressLint("NewApi")
	/**
	 * 获取到液晶的分辨率
	 * @param context
	 * @return
	 */
	public static android.graphics.Point getScreenResolution(Context context) 
	{
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		android.graphics.Point theScreenResolution = new android.graphics.Point();
		display.getSize(theScreenResolution);
		return new android.graphics.Point(theScreenResolution.x,theScreenResolution.y);
	}

}
