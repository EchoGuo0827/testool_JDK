package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig25.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 设置状态栏是否显示ADB调试信息
 * 					  setStatusBarAdbNotify(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig25 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "ADB调试信息";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	private String fileName="SystemConfig25";
	public void systemconfig25()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
	
		boolean flag = false,flag2=false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// 测试前置，开启状态栏下拉功能
		settingsManager.setStatusBarEnabled(0);
		
		// case1:设置0，ADB开启插入USB线应有相应图标，拔下USB线相应图标应消失
		gui.cls_show_msg1(1, "状态栏ADB调试信息设置为打开");
		if((flag = settingsManager.setStatusBarAdbNotify(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gKeepTimeErr, "line %d:ADB调试信息开启失败ret = %s", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("请插拔USB线，是否为USB线插入状态栏下拉显示已连接USB调试，拔下状态栏未提示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置1，ADB关闭插拔USB线，状态栏通知栏图标均不应显示
		gui.cls_show_msg1(1, "状态栏ADB调试信息设置为关闭");
		if((flag = settingsManager.setStatusBarAdbNotify(1))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gKeepTimeErr, "line %d:ADB调试信息开启失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("是否为USB线插拔状态栏均不显示已连接USB调试".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为-1、10均应失败，并且不应改变原先的状态
		// 原先设置为开启，仍应是开启状态
		if((flag = settingsManager.setStatusBarAdbNotify(-1))==true|(flag2 = settingsManager.setStatusBarAdbNotify(10))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gKeepTimeErr, "line %d:参数异常测试失败(%s,%s)", Tools.getLineInfo(),flag,flag2);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("是否为USB线插拔状态栏均不显示已连接USB调试".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig25",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig25",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() 
	{
		gui = new Gui(myactivity, handler);

	}

	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			// 测试后置，关闭ADB调试开关
			settingsManager.setStatusBarAdbNotify(1);
		}
		settingsManager = null;
		gui = null;
	}
}
