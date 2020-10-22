package com.example.highplattest.android;

import android.content.Context;
import android.os.Vibrator;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android33.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 20190827
 * directory 		: 设备震动器测试
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 weimj		      20190827	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android33 extends UnitFragment{
	public final String TAG = Android33.class.getSimpleName();
	private String TESTITEM = "设备震动器测试";
	private Gui gui = new Gui(myactivity, handler);
	
	public void android33(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		Vibrator vibrator = (Vibrator)myactivity.getSystemService(Context.VIBRATOR_SERVICE);
		
		//case1:设备震动器不存在时调用vibrator应返回false
		gui.cls_show_msg("设备震动器不存在时调用vibrator应返回false，任意键继续");
        boolean value = vibrator.hasVibrator();
        vibrator.vibrate(2000);
        vibrator.cancel();
        gui.cls_show_msg1(gScreenTime, "value的输出结果是%s",value);
        if (gui.cls_show_msg("value的结果是\ntrue[确定]\nfalse[其他]") == ENTER) 
        {
        	gui.cls_show_msg1_record(TAG, "android33", gKeepTimeErr, "%s震动器返回异常", Tools.getLineInfo(), TESTITEM);
        }
        
        gui.cls_show_msg1_record(TAG, "android33",gScreenTime,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
