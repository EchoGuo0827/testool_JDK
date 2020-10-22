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
 * file name 		: SystemConfig26.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 语言输入法-拼写检查工具显示/隐藏
 * 					  setSettingLanguageSpellCheckerDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig26 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "语言输入法-拼写检查工具显示/隐藏";
	private Gui gui = null;
	private SettingsManager settingsManager;
	private String fileName="SystemConfig26";
	public void systemconfig26()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
	
		
		boolean flag = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// case1:设置0，显示语言与输入法-拼写检查工具选项
		gui.cls_show_msg1(1, "设置语言输入发-拼写检查工具选项将显示");
		if((flag = settingsManager.setSettingLanguageSpellCheckerDisplay(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置语言输入发-拼写检查工具选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:参数异常，设置-1,100时，不应改变原先的状态
		gui.cls_show_msg1(1, "参数异常测试，不应改变原先的显示状态");
		if((flag = settingsManager.setSettingLanguageSpellCheckerDisplay(-1))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag = settingsManager.setSettingLanguageSpellCheckerDisplay(100))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		

		if(gui.ShowMessageBox("设置语言输入发-拼写检查工具选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:设置1，不显示语言与输入法-拼写检查工具选项
		gui.cls_show_msg1(1, "设置语言输入发-拼写检查工具选项将隐藏");
		if((flag = settingsManager.setSettingLanguageSpellCheckerDisplay(1)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置语言输入发-拼写检查工具选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 测试后置，显示
		if((flag = settingsManager.setSettingLanguageSpellCheckerDisplay(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig26",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig26",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置，拼写检查工具默认隐藏
			settingsManager.setSettingLanguageSpellCheckerDisplay(1);
		}
		settingsManager = null;
		gui = null;
	}
	
	
}
