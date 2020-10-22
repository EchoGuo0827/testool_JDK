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
 * file name 		: SystemConfig27.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 开关锁屏选项
 * 					  setSettingLockScreenDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig27 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "屏幕锁定方式显示/隐藏";
	private String fileName="SystemConfig27";
	private Gui gui = null;
	private SettingsManager settingsManager;
	
	public void systemconfig27()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig27",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// 测试前置：设置VPN为关
		settingsManager.setSettingVpnDispley(1);
		
		// case1:设置0，开启锁频功能
		gui.cls_show_msg1(1, "将显示设置的锁屏选项");
		settingsManager.setSettingLockScreenDisplay(0);

		if(gui.ShowMessageBox("打开设置，查看是否有屏幕锁定方式选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig27",gKeepTimeErr, "line %d:打开锁频选项失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1:参数异常测试，设置为-1,100时，应无法改变原先的显示状态
		gui.cls_show_msg1(1, "参数异常测试，不应改变原先的锁屏显示状态");
		settingsManager.setSettingLockScreenDisplay(-1);
		settingsManager.setSettingLockScreenDisplay(100);
	
		if(gui.ShowMessageBox("打开设置，查看是否有屏幕锁定方式选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig27",gKeepTimeErr, "line %d:打开锁频选项失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置1，关闭锁屏选项
		gui.cls_show_msg1(1, "将关闭锁屏选项");
		settingsManager.setSettingLockScreenDisplay(1);
		if(gui.ShowMessageBox("打开设置，查看是否无屏幕锁定方式选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig27",gKeepTimeErr, "line %d:打开锁频选项失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig27",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：屏幕锁定方式默认显示
			settingsManager.setSettingLockScreenDisplay(0);
		}
		settingsManager = null;
		gui = null;
	}
}
