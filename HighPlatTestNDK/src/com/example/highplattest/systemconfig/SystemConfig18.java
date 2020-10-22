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
 * file name 		: SystemConfig18.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置是否显示开发者选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig18 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置开发者选项开关";
	private Gui gui = null;
	boolean ret = false;
	private String fileName="SystemConfig18";
	public void systemconfig18() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig18",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingDevelopmentSettingsDisplay\n1.setSettingDevelopmentSettingsDispley\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			developeDisplay();
			break;
			
		case '1':
			developeDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}
	}
	
	private void developeDisplay()
	{
		String funcName = "developeDisplay";
		/*process body*/
		// 测试前置，显示开发者选项
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置1，不显示开发者选项
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDisplay(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置的显示
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDisplay(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置0，显示开发者选项
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置的显示
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDisplay(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置是否显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认开启开发者选项
		settingsManager.setSettingDevelopmentSettingsDisplay(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void developeDispley()
	{
		String funcName = "developeDispley";
		/*process body*/
		// 测试前置，显示开发者选项
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置1，不显示开发者选项
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDispley(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置的显示
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDispley(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置0，显示开发者选项
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置的显示
		if(	(ret=settingsManager.setSettingDevelopmentSettingsDispley(-1))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置是否显示开发者选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认开启开发者选项
		settingsManager.setSettingDevelopmentSettingsDispley(0);
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
