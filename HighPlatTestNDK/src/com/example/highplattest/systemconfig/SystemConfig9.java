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
 * file name 		: SystemConfig9.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160605
 * directory 		: 
 * description 		: 设置是否显示主屏幕选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160605	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig9 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置主屏幕选项开关";
	private String fileName="SystemConfig9";
	private Gui gui = null;

	public void systemconfig9() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig9",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingHomeDisplay\n1.setSettingHomeDispley\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			homeDisplay();
			break;
			
		case '1':
			homeDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}

	}
	
	private void homeDisplay()
	{
		String funcName = "homeDisplay";
		/*process body*/
		// 测试前置，显示主屏幕选项
		settingsManager.setSettingHomeDisplay(0);
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		gui.cls_show_msg("请先将服务器的adwLauncher的Launcher应用安装到设备上，已安装可忽略，后按【确认】继续");
	
		// case1:设置为1为不显示主屏幕选项，预期应不显示主屏幕选项
		settingsManager.setSettingHomeDisplay(1);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不为0,1，不应改变主屏幕选项显示状态，预期应不显示主屏幕选项
		settingsManager.setSettingHomeDisplay(-1);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0为显示主屏幕选项，预期应显示主屏幕选项
		settingsManager.setSettingHomeDisplay(0);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 设置不为0,1，不应改变主屏幕选项显示状态，预期应显示主屏幕选项
		settingsManager.setSettingHomeDisplay(-1);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 该选项默认为关闭状态
		settingsManager.setSettingHomeDisplay(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void homeDispley()
	{
		String funcName = "homeDispley";
		/*process body*/
		// 测试前置，显示主屏幕选项
		settingsManager.setSettingHomeDispley(0);
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		gui.cls_show_msg("请先将服务器的adwLauncher的Launcher应用安装到设备上，已安装可忽略，后按【确认】继续");
	
		// case1:设置为1为不显示主屏幕选项，预期应不显示主屏幕选项
		settingsManager.setSettingHomeDispley(1);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不为0,1，不应改变主屏幕选项显示状态，预期应不显示主屏幕选项
		settingsManager.setSettingHomeDispley(-1);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0为显示主屏幕选项，预期应显示主屏幕选项
		settingsManager.setSettingHomeDispley(0);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 设置不为0,1，不应改变主屏幕选项显示状态，预期应显示主屏幕选项
		settingsManager.setSettingHomeDispley(-1);
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 该选项默认为关闭状态
		settingsManager.setSettingHomeDispley(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() 
	{
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() 
	{
		settingsManager = null;
		gui = null;
	}
}
