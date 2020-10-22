package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig49.java 
 * Author 			: zhangxinj	
 * version 			: 
 * DATE 			: 20170301
 * directory 		: 
 * description 		: 设置home键是否有效
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhangxinj			20170301		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig49 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "设置home键是否有效";
	private String fileName="SystemConfig49";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig49()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig49",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		boolean flag = false;
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		/*process body*/
		// 测试前置:设置home键可用
		if((flag=settingsManager.setHomeKeyEnabled(true))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("此时home键有效，请按home键可以回到主界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case1:设置参数为false，预期返回true，此时按home键无效
		gui.cls_show_msg1(gScreenTime, "设置参数为false，此时按home键无效");
		if((flag=settingsManager.setHomeKeyEnabled(false))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr,"line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("此时home键无效，请按home键看是否可以回到主界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2：设置参数为true，预期返回true，此时按home键有效
		gui.cls_show_msg1(gScreenTime, "设置参数为true，此时按home键有效");
		if((flag=settingsManager.setHomeKeyEnabled(true))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("此时home键有效，请按home键可以回到主界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig49",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		
	}
	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}
	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：设置home键是可用的
			settingsManager.setHomeKeyEnabled(true);
		}
		settingsManager = null;
		gui = null;
	}
}
