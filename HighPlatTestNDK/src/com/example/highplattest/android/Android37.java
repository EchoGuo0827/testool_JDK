package com.example.highplattest.android;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android37.java 
 * directory 		: 
 * history 		 	: 变更点						        变更时间			变更人员
 * 					闪光灯常亮=手电筒					20200729          	  郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android37 extends UnitFragment{
	public final String TAG = Android36.class.getSimpleName();
	private String TESTITEM = "闪光灯常亮=手电筒";
	private Gui gui = new Gui(myactivity, handler) ;
	
	
	public void android37()
	{
		// case1:打开闪关灯5分钟
		gui.cls_show_msg1(1, "case1:打开闪光灯测试中,打开闪关灯5分钟...");
		Camera camera = Camera.open();
		camera.startPreview();
		Parameters parameters = camera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameters);
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<5*60)
		{
			if(gui.cls_show_msg1(10, "闪光灯已打开,取消键可退出闪光灯常亮状态")==ESC)
			{
				break;
			}
			int time = (int) Tools.getStopTime(startTime);
			gui.cls_show_msg1(3, "闪光灯已开启%ds",time);
		}
		// case2:关闭闪关灯
		gui.cls_show_msg1(1, "case2:关闭闪光灯测试中...");
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
		// case3:再次打开闪光灯1分钟
		gui.cls_show_msg1(1, "case3:打开闪光灯测试中,打开闪关灯1分钟...");
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameters);
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<60)
		{
			if(gui.cls_show_msg1(10, "闪光灯已打开,取消键可退出闪光灯常亮状态")==ESC)
			{
				break;
			}
			int time = (int) Tools.getStopTime(startTime);
			gui.cls_show_msg1(3, "闪光灯已开启%ds",time);
		}
		// case4:关闭闪光灯
		gui.cls_show_msg1(1, "case2:关闭闪光灯测试中...");
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
		
		// 测试后置，释放camera
		camera.release();
		gui.cls_show_msg1_record(TAG, "android37", 5, "闪关灯处于关闭状态才可视为测试通过" );
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
