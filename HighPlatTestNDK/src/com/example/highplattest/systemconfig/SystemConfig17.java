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
 * file name 		: SystemConfig17.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置是否显示辅助功能选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig17 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置辅助功能选项开关";
	private Gui gui = null;
	boolean ret = false;
	private String fileName="SystemConfig17";
	public void systemconfig17()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig17",gScreenTime, "%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		 settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		 int nkeyIn = gui.cls_show_msg("%s\n0.setSettingAccessibilitySettingsDisplay\n1.setSettingAccessibilitySettingsDispley\n", TESTITEM);
		 switch (nkeyIn) {
		case '0':
			accessDisplay();
			break;
		
		case '1':
			accessDispley();
			break;

		case ESC:
			unitEnd();
			return;
			
		default:
			break;
		}
	}
	
	private void accessDisplay()
	{
		String funcName = "accessDisplay";
		/*process body*/
		// 测试前置，显示辅助功能选项
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置1，不显示辅助功能选项
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDisplay(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 设置不在范围内，不影响原先显示
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDisplay(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置0，显示辅助功能选项
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响原先显示
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDisplay(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.ShowMessageBox("[设置]是否显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认开启辅助功能选项
		settingsManager.setSettingAccessibilitySettingsDisplay(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void accessDispley()
	{
		String funcName = "accessDispley";
		/*process body*/
		// 测试前置，显示辅助功能选项
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置1，不显示辅助功能选项
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDispley(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 设置不在范围内，不影响原先显示
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDispley(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置0，显示辅助功能选项
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响原先显示
		if(	(ret=settingsManager.setSettingAccessibilitySettingsDispley(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.ShowMessageBox("[设置]是否显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认开启辅助功能选项
		settingsManager.setSettingAccessibilitySettingsDispley(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		 gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() 
	{
		settingsManager = null;
		gui = null;
	}
}
