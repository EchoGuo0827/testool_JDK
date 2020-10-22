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
 * module 			: Android系统设置测试
 * file name 		: SystemConfig8.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160603
 * directory 		: 
 * description 		: 设置 设置应用是否需要通过管理员密码登录来启动
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160603 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig8 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "设置应用密码登录开关";
	private String fileName="SystemConfig8";
	private SettingsManager settingsManager = null;
	private Gui gui = null;
	boolean ret = false;
	
	public void systemconfig8() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig8",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证", TESTITEM);
		}
		/*private & local definition*/
		try
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "该类未找到");
			return;
		}
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
	
		
		// case1:设置应用不需要通过管理员密码登录来启动
		if ((ret = settingsManager.setSettingApkNeedLogin(0)) == false) {
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr, "line %d:%s测试失败(ret=%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("结束设置应用重新进入设置应用，是否不需要输入密码".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置的值不在范围内测试，预期不会改变原先的设置
		if(	(ret=settingsManager.setSettingApkNeedLogin(-200))==true){
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("结束设置应用重新进入设置应用，是否不需要输入密码".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置应用需要通过管理员密码登录来启动，输入密码可启动设置应用
		// 设置登录密码
		if(	(ret=settingsManager.setLoginPassword("123456"))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(	(ret=settingsManager.setSettingApkNeedLogin(1))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("结束设置应用重新进入，输入密码123789是否不能够进入设置应用".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("结束设置应用重新进入，输入密码123456是否能够进入设置应用".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 设置的值不在范围内测试，预期不会改变原先的设置
		if(	(ret=settingsManager.setSettingApkNeedLogin(-200))==true){
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("结束设置应用重新进入，进入设置应用是否需要输入密码".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig8",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig8",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
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
			// 测试后置，设置为进入设置应用不需要密码
			settingsManager.setSettingApkNeedLogin(0);
		}
		settingsManager = null;
		gui = null;
	}
}
