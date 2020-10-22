package com.example.highplattest.android;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android4.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180409 
 * directory 		: 
 * description 		: 测试Android原生屏幕亮度接口
 * related document : 
 * history 		 	: 变更记录			   				变更时间				变更人员
 *			  		  亮度显示时间修改为100ms		   		20200426	 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android4 extends UnitFragment {
	public final String TAG = Android4.class.getSimpleName();
	private String TESTITEM = "屏幕亮度原生接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private int defaultLight;
	private MyHandler mHandler=new MyHandler();
	private Message msg;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			setScreenBrightness(myactivity, msg.what);
		} 
		
	}
	public void android4(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		defaultLight=getScreenBrightness(myactivity);
		
		//case1：屏幕亮度递增显示效果
		for (int i = 0; i <=255; i++) 
		{
//			gui.cls_show_msg1(gScreenTime, "当前亮度为"+i);
			gui.cls_show_msg2(0.1f,"当前亮度为"+i);
			msg=Message.obtain();
			msg.what=i;
			mHandler.sendMessage(msg);
		}
		if(gui.cls_show_msg("查看屏幕亮度是否正常，[确认]是，[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android4", gKeepTimeErr,"line %d:%s屏幕亮度递增效果异常", Tools.getLineInfo(), TESTITEM);
		}
		//恢复初始亮度
		msg=Message.obtain();
		msg.what=defaultLight;
		mHandler.sendMessage(msg);
		
		gui.cls_show_msg1_record(TAG, "android4",gScreenTime,"%s测试通过", TESTITEM);
	}


	//获取屏幕亮度
	public static int getScreenBrightness(Activity activity) {
	    int value = 0;
	    ContentResolver cr = activity.getContentResolver();
	    try {
	        value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
	    } catch (SettingNotFoundException e) {
	        
	    }
	    return value;
	}
	//设置屏幕亮度
	public static void setScreenBrightness(Activity activity, int value) {
	    WindowManager.LayoutParams params = activity.getWindow().getAttributes();
	    params.screenBrightness = value / 255f;
	    activity.getWindow().setAttributes(params);
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
