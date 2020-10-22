package com.example.highplattest.event;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 多应用使用事件机制场景测试
 * file name 		: Event7.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200820
 * directory 		: 
 * description 		: 多应用使用事件机制场景测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200820	 	    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event8 extends UnitFragment{
	private Gui gui = new Gui(myactivity, handler);
	public final String TAG = Event8.class.getSimpleName();
	private final String TESTITEM = "多应用事件机制";
	
	public void event8()
	{
		gui.cls_show_msg("测试前请先安装/SVN/doc/事件机制多应用/app_A.apk和app_B.apk两个应用");
		
		//case1:A应用注册刷卡事件的永不超时，B应用注册插卡事件的永不超时，刷卡只能A应用监听到，插卡只能B应用监听到
		if(gui.cls_show_msg("进行刷卡和插卡操作，预期A应用监听到刷卡事件，B应用监听到插卡事件，是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "event8", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if(GlobalVariable.isContinue)
				return;
		}
		
		//case2:A应用注册刷卡事件的永不超时，B应用无法注册刷卡事件
		if(gui.cls_show_msg("A应用注册刷卡事件后，B应用无法注册刷卡事件，是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "event8", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if(GlobalVariable.isContinue)
				return;
		}
		
		//case3:A应用注册刷卡事件的永不超时，B应用注册插卡事件的永不超时，A应用注销刷卡事件，B应用的事件不应该被注销
		if(gui.cls_show_msg("A应用注销刷卡事件后，B应用的插卡事件不会被注销，是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "event8", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if(GlobalVariable.isContinue)
				return;
		}
		
		//case4:A应用注册刷卡事件的永不超时，B应用注册插卡事件的永不超时，A应用奔溃，B应用的事件不应该被注销
		if(gui.cls_show_msg("A应用崩溃后，B应用的插卡事件不会被注销，是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "event8", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if(GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(TAG, "event8", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
