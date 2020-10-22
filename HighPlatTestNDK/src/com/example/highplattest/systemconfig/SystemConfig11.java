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
 * file name 		: SystemConfig11.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160605
 * directory 		: 
 * description 		: 设置是否显示状态栏中电池电量百分比
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160605	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig11 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置状态栏中电池电量百分比开关";
	private Gui gui= null;
	private boolean memory;
	private String fileName="SystemConfig11";
	public void systemconfig11() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig11",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		/*private & local definition*/
		boolean ret = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		memory = settingsManager.isShowBatteryPercent();
		
		/*process body*/
		// 测试前置，显示电量百分比
		settingsManager.setShowBatteryPercent(true);
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
		// case1:设置不显示电量百分比
		settingsManager.setShowBatteryPercent(false);
		if(gui.ShowMessageBox("状态栏是否显示电池百分比".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK||(ret = settingsManager.isShowBatteryPercent()))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig11",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:显示电量百分比
		// 显示电池百分比
		settingsManager.setShowBatteryPercent(true);
		if(gui.ShowMessageBox("状态栏是否显示电池百分比".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_CANCEL||!(ret = settingsManager.isShowBatteryPercent()))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig11",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig11",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui=new Gui(myactivity,handler);
		
	}

	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			// 测试后置，恢复成进入设置的样子
			if(memory)
				settingsManager.setShowBatteryPercent(true);
			else
				settingsManager.setShowBatteryPercent(false);
		}
		settingsManager = null;
		gui = null;
	}
}
