package com.example.highplattest.android;

import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.SoundPool;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android35.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200103
 * directory 		: 
 * description 		: X5DisplayMetrics 获取密度比
 * related document : 
 * history 		 	: author			date			remarks
 *			  		
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android35 extends UnitFragment {
	public final String TAG = Android35.class.getSimpleName();
	private String TESTITEM = "X5DisplayMetrics获取密度比";
	private Object lockObj = new Object();
	private Gui gui ;
	private DifferentDisplay presentation;
	private Display[] presentationDisplays;
	
	public void android35()
	{
		DisplayManager displayManager = (DisplayManager)myactivity.getSystemService(Context.DISPLAY_SERVICE);
	        //获取屏幕数量
	    presentationDisplays = displayManager.getDisplays();
		gui = new Gui(myactivity, handler);
		DisplayMetrics dis=myactivity.getResources().getDisplayMetrics();//主屏
		LoggerUtil.e("主屏密度比："+dis.densityDpi+" ，宽度："+dis.widthPixels+" 高度："+dis.heightPixels);
        int len=presentationDisplays.length;
        //获取的屏幕数量<1说明没有多个屏幕
        if(len<1)
        {
        	 gui.cls_show_msg1(2,"当前设备只有一个屏幕");
        	 return;        			 
        }
        //双屏异显
//        myactivity.runOnUiThread(new Runnable() {
//			
//			@SuppressWarnings("deprecation")
//			@Override
//			public void run() 
//			{
//				presentation = new DifferentDisplay(myactivity, presentationDisplays[1]);
//		        presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
//			    presentation.show();
//			}
//		});
		gui.cls_show_msg("计算中。请确保X5是双屏幕异显.......按任意键继续");
		DisplayMetrics dis2=new DisplayMetrics();//副屏
		presentationDisplays[1].getRealMetrics(dis2);
		LoggerUtil.e("副屏密度比："+dis2.densityDpi+" ，宽度："+dis2.widthPixels+" 高度："+dis2.heightPixels);
		float density=dis.density;
	    int densddp=dis.densityDpi;
	    float density2=dis2.density;
	    int densddp2=dis2.densityDpi;
		gui.cls_show_msg1_record(TESTITEM,"Android35",gScreenTime, "当前X5密度比为%f,像素密度为%d,X5副屏密度比为%f,像素密度为%d",density,densddp,density2,densddp2);
//		gui.cls_show_msg("关闭双屏异显");
//		presentation.dismiss();
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
	
	//副屏的显示内容
	public class DifferentDisplay extends Presentation {

		public DifferentDisplay(Context outerContext, Display display) {
			super(outerContext, display);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.presentation_content);

		}

	}

}
