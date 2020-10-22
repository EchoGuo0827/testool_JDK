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
 * file name 		: SystemConfig28.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 语言与输入法个人字典选项显示/隐藏
 * 					  setSettingLanguageUserDictionaryDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig28 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "语言与输入法个人字典选项显示/隐藏";
	private String fileName="SystemConfig28";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig28()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		boolean flag,flag2;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// case1:设置0，显示语言输入法个人词典选项
		gui.cls_show_msg1(1, "将显示语言与输入法字典选项");
		if((flag = settingsManager.setSettingLanguageUserDictionaryDisplay(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("查看语言输入法-个人词典选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:参数异常测试，设置-1,2,不应影响当前显示状态
		gui.cls_show_msg1(1, "将显示语言与输入法字典选项");
		if((flag = settingsManager.setSettingLanguageUserDictionaryDisplay(-1))==true|(flag2 = settingsManager.setSettingLanguageUserDictionaryDisplay(-1)==true))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gKeepTimeErr, "line %d:%s测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,flag,flag2);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("查看语言输入法-个人词典选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:设置1，隐藏语言输入法个人词典选项
		gui.cls_show_msg1(1, "将不显示语言与输入法字典选项");
		if((flag = settingsManager.setSettingLanguageUserDictionaryDisplay(1))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
	
		if(gui.ShowMessageBox("查看语言输入法-个人词典选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig28",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig28",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：个人词典选项默认隐藏
			settingsManager.setSettingLanguageUserDictionaryDisplay(1);
		}
		gui = null;
		settingsManager = null;
		
	}
}
