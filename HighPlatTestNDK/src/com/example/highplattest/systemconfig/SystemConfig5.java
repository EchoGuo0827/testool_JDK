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
 * file name 		: SystemConfig5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150119
 * directory 		: 
 * description 		: 测试系统设置是否显示"应用"菜单setSettingAppDispley
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150119 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig5 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager=null;
	private final String TESTITEM = "设置应用选项开关";
	private String fileName="SystemConfig5";
	private Gui gui = null;
	
	public void systemconfig5() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig5",gScreenTime, "%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingAppDisplay\n1.setSettingAppDispley\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			appDisplay();
			break;
			
		case '1':
			appDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}

	}
	
	private void appDisplay()
	{
		String funcName = "appDisplay";
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM + "测试中...");
		
		// case1:参数正确，0：显示应用菜单 1：不显示应用菜单
		// case1.1:参数设置为0，应该显示应用菜单
		settingsManager.setSettingAppDisplay(0);
		if(gui.ShowMessageBox("设置中查看是否有应用菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:设置显示应用选项测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1.2:参数设置为1，应该不显示应用菜单
		settingsManager.setSettingAppDisplay(1);
		if(gui.ShowMessageBox("设置中查看是否无应用菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:设置不显示应用选项测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：设置应用选项为显示
		settingsManager.setSettingAppDisplay(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void appDispley()
	{
		String funcName = "appDispley";
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM + "测试中...");
		
		// case1:参数正确，0：显示应用菜单 1：不显示应用菜单
		// case1.1:参数设置为0，应该显示应用菜单
		settingsManager.setSettingAppDispley(0);
		if(gui.ShowMessageBox("设置中查看是否有应用菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:设置显示应用选项测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1.2:参数设置为1，应该不显示应用菜单
		settingsManager.setSettingAppDispley(1);
		if(gui.ShowMessageBox("设置中查看是否无应用菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:设置不显示应用选项测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：设置应用选项为显示
		settingsManager.setSettingAppDispley(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
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
