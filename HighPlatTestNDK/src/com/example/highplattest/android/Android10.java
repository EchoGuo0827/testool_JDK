package com.example.highplattest.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.app.AlarmManager;
import android.content.Context;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android10.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180507 
 * directory 		: 
 * description 		: 测试Android原生日期内存接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180507 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android10 extends UnitFragment {
	public final String TAG = Android10.class.getSimpleName();
	private String TESTITEM = "时间日期接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private AlarmManager alarmManager;
		
	public void android10(){
		alarmManager= (AlarmManager)myactivity.getSystemService(Context.ALARM_SERVICE);
		// 测试前置，确保机器已经连接上网络
		if (gui.ShowMessageBox("Android端是否可以上网".getBytes(), (byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1(2, "测试前请确保Android端可以上网");
			return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		//case1:获取当前时间
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String now = formatter.format(curDate);
		if (gui.cls_show_msg("当前设备时间为%s,[确认]是，[其他]否",now) != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, "android10", gKeepTimeErr, "line %d:%s获取当前设备时间日期异常", Tools.getLineInfo(), TESTITEM);
		}
		//case2:设置为199312101415的时间
		setSysDate(1993,12,10);
		setSysTime(14,15,0);
		if (gui.cls_show_msg("当前设备时间为1993年12月10日14时15分,[确认]是，[其他]否") != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, "android10", gKeepTimeErr, "line %d:%s设置设备时间日期异常", Tools.getLineInfo(), TESTITEM);
		}
		//case3：重启时间应不变，需关闭自动确实日期和时间的功能，否则无法保留设置的日期和时间,由于自动确认日期的原生接口需要系统权限，故采用人工切换
		if(gui.ShowMessageBox("关闭设置-日期和时间中的自动确定日期和时间，重启设备后日期时间应不变，是否立即重启，已重启请忽略".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			Tools.reboot(myactivity);
		}
		//测试后置，恢复为自动获取的北京时间
		gui.cls_show_msg("开启设置-日期和时间中的自动确定日期和时间，完成任意键继续");
		curDate = new Date(System.currentTimeMillis());//获取当前时间
		now = formatter.format(curDate);
		if (gui.cls_show_msg("当前设备时间为%s,[确认]是，[其他]否",now) != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, "android10", gKeepTimeErr, "line %d:%s获取当前设备时间日期异常", Tools.getLineInfo(), TESTITEM);
		}
		gui.cls_show_msg1_record(TAG, "android10", gScreenTime,"%s测试通过", TESTITEM);
	}

	/**
	 * 设置系统日期
	 */
	public void setSysDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			alarmManager.setTime(when);
		}
	}

	/**
	 * 设置系统时间
	 */
	public void setSysTime(int hour, int minute,int second) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, 0);
		long when = c.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			alarmManager.setTime(when);
		}
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
