package com.example.highplattest.android;

import java.util.Random;
import java.util.TimeZone;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.Context;

import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android36.java 
 * Author 			: weimj
 * version 			: 
 * DATE 			: 20200603 
 * directory 		: 
 * description 		: 设置系统时间、时区
 * related document : 
 * history 		 	: 变更点						        变更时间			变更人员
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android36 extends UnitFragment{
	
	public final String TAG = Android36.class.getSimpleName();
	private String TESTITEM = "设置系统时间、时区";
	private Gui gui = new Gui(myactivity, handler) ;
	private AlarmManager alarmManager = null;
	private long time;
	private long startTime ;
	
	public void android36(){
		alarmManager = (AlarmManager) myactivity.getSystemService(Context.ALARM_SERVICE);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		//case1:设置系统时间
		gui.cls_show_msg("即将进行设置系统时间测试，请记录当前时间，任意键继续");
		startTime = System.currentTimeMillis();
		time = 30*60*1000;
		Log.e("startTime",startTime+"");
		
		alarmManager.setTime(startTime + time);
		if ((gui.ShowMessageBox(("请确认系统时间是否为记录时间半个小时后的时间").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
	 	{
	         	gui.cls_show_msg1_record(TAG, "android36", gKeepTimeErr, "line %d:%s设置时间测试失败",Tools.getLineInfo(), TESTITEM);
	 			if (!GlobalVariable.isContinue)
	 				return;
	 	}
		alarmManager.setTime(startTime);
		if ((gui.ShowMessageBox(("请确认系统时间是否为原来记录的时间").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
	 	{
	         	gui.cls_show_msg1_record(TAG, "android36", gKeepTimeErr, "line %d:%s设置时间测试失败",Tools.getLineInfo(), TESTITEM);
	 			if (!GlobalVariable.isContinue)
	 				return;
	 	}
		
		//case2:设置系统时区
		gui.cls_show_msg("即将进行设置系统时区测试，任意键继续");
		String[] zone = TimeZone.getAvailableIDs();
//		int num = 0;
//		for(String tz:zone){
//			num++;
//			Log.e("timezone"+num,tz+"");
//		}
		Random r = new Random();
//		Log.e("length",zone.length+"");
		for(int i=0 ; i<2 ;  i++)
		{
			int ran = r.nextInt(zone.length);
			Log.e("ran",ran+"");
			alarmManager.setTimeZone(zone[ran]);
			if ((gui.ShowMessageBox(("请确认是否设置为"+zone[ran]+"对应的时区").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		 	{
		         	gui.cls_show_msg1_record(TAG, "android36", gKeepTimeErr, "line %d:%s设置时区测试失败",Tools.getLineInfo(), TESTITEM);
		 			if (!GlobalVariable.isContinue)
		 				return;
		 	}
		}
		alarmManager.setTimeZone("Asia/Shanghai");
		if ((gui.ShowMessageBox(("请确认是否设置为Asia/Shanghai对应的时区").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
	 	{
	         	gui.cls_show_msg1_record(TAG, "android36", gKeepTimeErr, "line %d:%s设置时区测试失败",Tools.getLineInfo(), TESTITEM);
	 			if (!GlobalVariable.isContinue)
	 				return;
	 	}
		gui.cls_show_msg1_record(TAG,"android36",gScreenTime,"%s测试通过", TESTITEM);
	}

	@TargetApi(24) @Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
