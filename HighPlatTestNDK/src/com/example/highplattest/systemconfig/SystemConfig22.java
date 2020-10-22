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
 * file name 		: SystemConfig22.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160815
 * directory 		: 
 * description 		: 设置深浅休眠开关
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160815		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig22 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置3G深浅休眠开关";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName="SystemConfig22";
	public void systemconfig22() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		boolean ret = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		/*process body*/
		// 测试前置，测试功耗要先将机子进行恢复出厂设置 by zhengxq 20170223
		if(gui.ShowMessageBox("是否已恢复出厂设置".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gScreenTime, "测试功耗前要确保机子已恢复出厂设置，未恢复出厂设置请先恢复再进入本用例");
			return;
		}
		// case1.1:设置为浅休眠，手动进入休眠，功耗应为50-70mA
		if((ret = settingsManager.setDeepSleepEnabled(false))!= true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr,"line %d:%s设置浅休眠失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "请手动按电源键进入浅休眠，休眠中使用功耗仪测量休眠时功耗");

		if(gui.ShowMessageBox("测量的功耗是否在50-70mA左右".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr,"line %d:%s功耗预期不符", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1.2:设置为浅休眠，自动进入休眠，功耗应为50-70mA左右
		if((ret = settingsManager.setDeepSleepEnabled(false))!= true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr,"line %d:%s设置浅休眠失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "请进入设置将休眠时间设置为1分钟，等待自动进入休眠，休眠中使用功耗仪测量休眠时功耗");

		if(gui.ShowMessageBox("测量的功耗是否在50-70mA左右".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1(2, "line %d:%s功耗预期不符", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.1:设置为深休眠，手动进入休眠，功耗应该为15mA以下
		if((ret = settingsManager.setDeepSleepEnabled(true))!= true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr,"line %d:%s设置浅休眠失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "请手动按电源键进入浅休眠，使用功耗仪测量休眠时功耗");

		if(gui.ShowMessageBox("测量的功耗是否在15mA以下".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr,"line %d:%s功耗预期不符", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
				
		}
		
		// case2.2:设置为深休眠，自动进入休眠，功耗应该为10mA左右
		if((ret = settingsManager.setDeepSleepEnabled(true))!= true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr, "line %d:%s设置浅休眠失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "请进入设置将休眠时间设置为1分钟，等待自动进入休眠，休眠中使用功耗仪测量休眠时功耗");

		if(gui.ShowMessageBox("测量的功耗是否大致为8-10mA".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig22",gKeepTimeErr,"line %d:%s功耗预期不符", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
				
		}
		gui.cls_show_msg1_record(fileName,"systemconfig22",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			// 测试后置，默认设置为浅休眠
			settingsManager.setDeepSleepEnabled(false);
		}
		settingsManager = null;
		gui = null;
	}
}
