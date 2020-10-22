package com.example.highplattest.systemversion;

import android.annotation.SuppressLint;
import android.newland.os.NlBuild;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android版本号获取模块
 * file name 		: SystemVersion4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161130
 * directory 		: 
 * description 		: 触屏信息获取
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161130 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemVersion4 extends UnitFragment
{
	private final String TESTITEM = "触屏信息获取";
	private String fileName=SystemVersion4.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void systemversion4() 
	{
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		// 获取触屏分辨率
		String touchScreen = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
		if(gui.ShowMessageBox(("请确认获取触屏分辨率=" + touchScreen).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion4", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,touchScreen);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 获取触屏名称
		String touchName = NlBuild.VERSION.TOUCHSCREEN_NAME;
		if(gui.ShowMessageBox(("请确认触屏名称："+touchName).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion4", gKeepTimeErr,"line %d:%s测试失败（name = %s）", Tools.getLineInfo(),TESTITEM,touchName);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 获取触屏版本号
		String touchVersion =  NlBuild.VERSION.TOUCHSCREEN_VERSION;
		if(gui.ShowMessageBox(("请确认触屏版本："+touchVersion).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1(2, "line %d:%s测试失败（version = %s）", Tools.getLineInfo(),TESTITEM,touchVersion);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull){
			gui.cls_show_msg1_record(fileName, "systemversion4", gScreenTime,"获取触屏分辨率:%s,触屏名称:%s,触屏版本:%s", touchScreen,touchName,touchVersion);
		}
		gui.cls_show_msg1_record(fileName, "systemversion4", gScreenTime,"%s测试通过", TESTITEM);
		gui = null;
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
