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
 * file name 		: SystemConfig10.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160605
 * directory 		: 
 * description 		: 设置是否显示备份和重置选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160605	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig10 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置备份和重置选项开关(setSettingPrivacyDisplay)";
	private Gui gui = null;
	private String fileName="SystemConfig10";
	public void systemconfig10() 
	{
	
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig10",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		/*process body*/
		 settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		 int nkeyIn = gui.cls_show_msg("%s\n0.setSettingPrivacyDisplay\n1.setSettingPrivacyDispley\n", TESTITEM);
		 switch (nkeyIn) {
		case '0':
			privacyDisplay();
			break;
			
		case '1':
			privacyDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}

	}
	
	private void privacyDisplay()
	{
		String funcName = "privacyDisplay";
		// 测试前置，备份和重置按钮显示
		settingsManager.setSettingPrivacyDisplay(0);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置为1，不显示备份和重置选项，预期不显示备份和重置选项
		settingsManager.setSettingPrivacyDisplay(1);
		if(gui.ShowMessageBox("[设置]是否不显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不为0,1，不应影响设置选项的显示
		settingsManager.setSettingPrivacyDisplay(2);
		if(gui.ShowMessageBox("[设置]是否不显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，显示备份和重置选项，预期显示备份和重置选项
		settingsManager.setSettingPrivacyDisplay(0);
		if(gui.ShowMessageBox("[设置]是否显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//不设置为0,1,不应影响备份和重置选项
		settingsManager.setSettingPrivacyDisplay(-10);
		if(gui.ShowMessageBox("[设置]是否显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 默认为开启状态
		settingsManager.setSettingPrivacyDisplay(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void privacyDispley()
	{
		String funcName = "privacyDisplay";
		// 测试前置，备份和重置按钮显示
		settingsManager.setSettingPrivacyDispley(0);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置为1，不显示备份和重置选项，预期不显示备份和重置选项
		settingsManager.setSettingPrivacyDispley(1);
		if(gui.ShowMessageBox("[设置]是否不显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不为0,1，不应影响设置选项的显示
		settingsManager.setSettingPrivacyDispley(2);
		if(gui.ShowMessageBox("[设置]是否不显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，显示备份和重置选项，预期显示备份和重置选项
		settingsManager.setSettingPrivacyDispley(0);
		if(gui.ShowMessageBox("[设置]是否显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//不设置为0,1,不应影响备份和重置选项
		settingsManager.setSettingPrivacyDispley(-10);
		if(gui.ShowMessageBox("[设置]是否显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 默认为开启状态
		settingsManager.setSettingPrivacyDispley(0);
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
