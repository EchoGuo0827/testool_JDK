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
 * file name 		: SystemConfig14.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置是否显示打印选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig14 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置打印选项开关";
	private Gui gui = null;
	boolean ret = false;
	private String fileName="SystemConfig14";
	
	public void systemconfig14() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig14",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingPrintSettingsDisplay\n1.setSettingPrintSettingsDispley\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			printDisplay();
			break;
			
		case '1':
			printDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}
	}
	
	private void printDisplay()
	{
		String funcName = "printDisplay";
		/*process body*/
		// 测试前置，显示打印选项
		if(	(ret=settingsManager.setSettingPrintSettingsDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gKeepTimeErr, "%s测试中...", TESTITEM);
		
		// case1:设置为1，不显示打印选项
		if(	(ret=settingsManager.setSettingPrintSettingsDisplay(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置菜单显示
		if(	(ret=settingsManager.setSettingPrintSettingsDisplay(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置为0，显示打印选项
		if(	(ret=settingsManager.setSettingPrintSettingsDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置菜单显示
		if(	(ret=settingsManager.setSettingPrintSettingsDisplay(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：打印选项默认为不显示
		settingsManager.setSettingPrintSettingsDisplay(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void printDispley()
	{
		String funcName = "printDispley";
		/*process body*/
		// 测试前置，显示打印选项
		if(	(ret=settingsManager.setSettingPrintSettingsDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gKeepTimeErr, "%s测试中...", TESTITEM);
		
		// case1:设置为1，不显示打印选项
		if(	(ret=settingsManager.setSettingPrintSettingsDispley(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置菜单显示
		if(	(ret=settingsManager.setSettingPrintSettingsDispley(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否不显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置为0，显示打印选项
		if(	(ret=settingsManager.setSettingPrintSettingsDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("[设置]是否显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置菜单显示
		if(	(ret=settingsManager.setSettingPrintSettingsDispley(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：打印选项默认为不显示
		settingsManager.setSettingPrintSettingsDispley(1);
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
