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
 * file name 		: SystemConfig13.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置是否显示流量使用情况选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig13 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置流量使用情况选项开关";
	Gui gui = null;
	boolean ret = false;
	private String fileName="SystemConfig13";

	public void systemconfig13() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig13",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingDataUsageDisplay\n1.setSettingDataUsageDispley\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			dataDisplay();
			break;
			
		case '1':
			dataDispley();
			break;
			
		case ESC:
			unitEnd();
			return;
			
		default:
			break;
		}

	}
	
	private void dataDisplay()
	{
		String funcName = "dataDisplay";
		/*process body*/
		// 测试前置，显示流量使用情况选项
		if(	(ret=settingsManager.setSettingDataUsageDisplay(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
		// case1:参数设置为1，设置不显示流量使用情况选项
		if(	(ret=settingsManager.setSettingDataUsageDisplay(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 参数异常，不影响设置应用菜单
		if(	(ret=settingsManager.setSettingDataUsageDisplay(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:参数设置为0，设置显示流量使用情况选项
		if(	(ret=settingsManager.setSettingDataUsageDisplay(0))==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 参数异常，不影响设置应用菜单
		if(	(ret=settingsManager.setSettingDataUsageDisplay(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认不显示
		settingsManager.setSettingDataUsageDisplay(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void dataDispley()
	{
		String funcName = "dataDispley";
		/*process body*/
		// 测试前置，显示流量使用情况选项
		if(	(ret=settingsManager.setSettingDataUsageDispley(0))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
		// case1:参数设置为1，设置不显示流量使用情况选项
		if(	(ret=settingsManager.setSettingDataUsageDispley(1))==false){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 参数异常，不影响设置应用菜单
		if(	(ret=settingsManager.setSettingDataUsageDispley(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否不显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:参数设置为0，设置显示流量使用情况选项
		if(	(ret=settingsManager.setSettingDataUsageDispley(0))==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 参数异常，不影响设置应用菜单
		if(	(ret=settingsManager.setSettingDataUsageDispley(2))==true){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("[设置]是否显示流量使用情况".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认不显示
		settingsManager.setSettingDataUsageDispley(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		settingsManager = null;
		gui = null;
	}
}
