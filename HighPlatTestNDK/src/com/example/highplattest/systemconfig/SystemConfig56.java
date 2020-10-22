package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig56.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20180716 
 * directory 		: 
 * description 		: 设置是否开启USB主模式和获取当前USB主模式状态
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		   		20180716	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig56 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "setOtgMode和getOtgMode";
	private String fileName="SystemConfig56";
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager settingsManager = null;
	
	public void systemconfig56()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		boolean flag = false;
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		/*process body*/
		//case1:开启USB主模式
		if((flag = settingsManager.setOtgMode(true)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s设置USB主模式失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag = settingsManager.getOtgMode()) != true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s获取到的当前USB主模式状态与预期不符(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("请验证设置-辅助功能中USB HOST是否开启,并且adb命令无法使用、插入U盘等可识别、接入外设如扫描枪等可以使用".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2:关闭USB主模式
		if((flag = settingsManager.setOtgMode(false)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s设置USB主模式失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag = settingsManager.getOtgMode()) != false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s获取到的当前USB主模式状态与预期不符(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("请验证设置-辅助功能中USB HOST是否关闭,并且adb命令可正常使用、插入U盘等不可识别、接入外设如扫描枪等无法使用".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3:关闭后再开启USB主模式预期正常
		if((flag = settingsManager.setOtgMode(true)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s设置USB主模式失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag = settingsManager.getOtgMode()) != true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s获取到的当前USB主模式状态与预期不符(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("请验证设置-辅助功能中USB HOST是否开启,并且adb命令无法使用、插入U盘等可识别、接入外设如扫描枪等可以使用".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//后置，恢复关闭状态
		if((flag = settingsManager.setOtgMode(false)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s设置USB主模式失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag = settingsManager.getOtgMode()) != false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig56",gKeepTimeErr,"line %d:%s获取到的当前USB主模式状态与预期不符(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig56",gScreenTime,"%s测试通过(长按确认键退出测试)",TESTITEM);
	}

	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
