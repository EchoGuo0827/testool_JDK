package com.example.highplattest.systemconfig;

import java.util.Arrays;

import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig63.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 
 * directory 		: 设置X5副屏触摸关闭
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  weimj		      20190905	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig63 extends UnitFragment{

	private final String TESTITEM = "设置X5副屏触摸关闭";
	private String fileName = "SystemConfig63";
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager settingsManager = null;
	
	public void systemconfig63() 
	{
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		boolean flag = false;
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		
		//case1:设置X5副屏触摸关闭
		gui.cls_show_msg1(gScreenTime, "已设置为副屏触摸关闭");
		if((flag=settingsManager.setSecPanelTouch(false))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig63",gKeepTimeErr, "line:%d:%s失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
		}
		if(gui.cls_show_msg("点击副屏，应没有反应,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig63",gKeepTimeErr, "line %d:%s失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
		}
		
		//测试后置：恢复副屏触摸功能
		settingsManager.setSecPanelTouch(true);
		if(gui.cls_show_msg("恢复副屏触摸功能，点击副屏，应可以正常操作,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig63",gKeepTimeErr, "line %d:重置失败(%s)", Tools.getLineInfo(),flag);
		}
		gui.cls_show_msg1_record(fileName,"systemconfig63",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
