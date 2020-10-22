package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig24.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161013
 * directory 		: 
 * description 		: 控制状态栏是否支持下拉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161013		    created
 *					 变更点								时间				变更人
 *					状态栏菜单提示语去除A7不支持项			20200708			陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig24 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "状态栏菜单下拉控制";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	private String fileName="SystemConfig24";
	public void systemconfig24()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
	
		MobileUtil mobileUtil = MobileUtil.getInstance(myactivity,handler);
		boolean flag = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		if(mobileUtil.getSimState()!=NDK_OK)
		{
			if(gui.cls_show_msg("未插入sim卡，是否要继续测试，【确认】是，其他【否】")!=ENTER)
				return;
		}

		/*process body*/
		// 测试前置：设置状态栏为关闭状态
		settingsManager.setStatusBarEnabled(1);
		settingsManager.setStatusBarAdbNotify(1);
		// case1:参数异常测试，设置为-1,10均应返回false，并且不应下拉成功
		gui.cls_show_msg("参数异常测试，状态栏不应下拉,按任意键继续");
		if((flag = settingsManager.setStatusBarEnabled(-1))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag = settingsManager.setStatusBarEnabled(10))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 异常测试前置，下拉选项为WiFi+移动网络+飞行模式 +位置信息
		String[] testPre = {"wifi","bt","cell","airplane","location"};
		settingsManager.setStatusBarQsTiles(testPre);
		
		// 变更点评审下拉状态栏增加“设置菜单”按钮，默认设置是没有“设置菜单” add by 20171225
		// case6:参数异常测试
		// case6.1:参数异常测试，字符串数组中的数据非可以设置的，应保持为上一次下拉栏状态
		if((flag = settingsManager.setStatusBarEnabled(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏下拉开启失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "参数异常测试1");
		String[] errMenu1 = {"11","22","33","44"};
		if((flag = settingsManager.setStatusBarQsTiles(errMenu1))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("下拉状态栏是否保持为亮度+音量+wifi+bt+移动网络+飞行模式+位置信息，插拔USB线不应影响状态栏显示".getBytes(),
				(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// case6.2:参数异常测试，字符串数组中包含null，应保持为上一次下拉栏状态
		gui.cls_show_msg1(1, "参数异常测试2");
		String[] errMenu2 = {null,"wifi","BT"};// 数组中包含null
		if((flag = settingsManager.setStatusBarQsTiles(errMenu2))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("下拉状态栏是否保持为亮度+音量+wifi+bt+移动网络+飞行模式+位置信息，插拔USB线不应影响状态栏显示".getBytes(),
				(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case6.3:参数异常测试，字符串数组中包含""，应保持默认设置
		gui.cls_show_msg1(1, "参数异常测试3");
		String[] errMenu4 = {"","wifi","bt","apn"};// 数组中包含null
		if((flag = settingsManager.setStatusBarQsTiles(errMenu4))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("下拉状态栏是否保持为亮度+音量+wifi+bt+移动网络+飞行模式+位置信息，插拔USB线不应影响状态栏显示".getBytes(),
				(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// 根据陈振龙反馈 设置为null返回默认状态 modify 20171228
		// case6.4:参数异常设置为null，返回应为true，设置参数异常后应恢复为默认的效果
		//A7设置为null返回fasle  by20200708
		gui.cls_show_msg1(1, "参数异常测试4");
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			if((flag = settingsManager.setStatusBarQsTiles(null))==true)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
				if(!GlobalVariable.isContinue)
					return;
			}
		}else {
			if((flag = settingsManager.setStatusBarQsTiles(null))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:参数异常测试失败(%s)", Tools.getLineInfo(),flag);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		if(gui.ShowMessageBox("下拉状态栏是否有亮度+音量+wifi+bt+移动网络+飞行模式+位置信息，插拔USB线不应影响状态栏显示".getBytes(),
				(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置0状态栏下拉开启，添加部分菜单，BT、wifi、flashLight、notifications、setting
		String[] statusMenu1 = {"wifi","bt","cell","notifications","setting"};
		if((flag = settingsManager.setStatusBarQsTiles(statusMenu1))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			if(gui.ShowMessageBox(("下拉状态栏是否可下拉并显示亮度+音量+wifi+bt+" +
					"移动网络，插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}else {
			if(gui.ShowMessageBox(("下拉状态栏是否可下拉并显示亮度+音量+wifi+bt+" +
					"移动网络+通知栏快捷开关+系统设置，插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case3:设置1状态栏下拉关闭，部分菜单仍应显示，无菜单显示并无法进行下拉操作
		if((flag = settingsManager.setStatusBarEnabled(1))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏下拉关闭失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		

		if(gui.ShowMessageBox("状态栏是否可下拉".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4:状态栏下拉开启，不添加任何选项。应显示亮度+音量+wifi+bt+移动网络+飞行模式+位置信息+系统设置
		if((flag = settingsManager.setStatusBarEnabled(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏下拉关闭失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		String[] statusMenu2 = {};
		// 据陈振龙反馈该种情况同null情况是一样的，应返回为true
		//A7返回false 且和上次设置的一致。
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			if((flag = settingsManager.setStatusBarQsTiles(statusMenu2))==true)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
				if(!GlobalVariable.isContinue)
					return;
			}
		}else {
			if((flag = settingsManager.setStatusBarQsTiles(statusMenu2))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			
			if(gui.ShowMessageBox("状态栏是否可下拉只显示亮度+音量+wifi+bt+移动网络，插拔USB线不应影响状态栏显示".getBytes(),
					(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			
		}else {
			if(gui.ShowMessageBox("状态栏是否可下拉只显示亮度+音量+wifi+bt+移动网络+飞行模式+位置信息+系统设置，插拔USB线不应影响状态栏显示".getBytes(),
					(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		// case9:状态栏下拉，添加飞行、wlan、apn
		String[] statusMenu9 = {"airplane","wifi","apn","setting"};
		if((flag = settingsManager.setStatusBarQsTiles(statusMenu9)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			if(gui.ShowMessageBox(("状态栏是否可下拉并可显示亮度+音量+飞行模式+wifi").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}else {
			if(gui.ShowMessageBox(("状态栏是否可下拉并可显示亮度+音量+飞行模式+wifi+apn+系统设置").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case5:状态栏开启，添加所有菜单选项，应均能正常显示
		String[] statusMenu3 = {"airplane","wifi","bt","location","notifications","cell","roaming","apn","setting"};
		if((flag = settingsManager.setStatusBarQsTiles(statusMenu3)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			if(gui.ShowMessageBox(("状态栏是否可下拉并可显示亮度+音量+wifi+bt+飞行模式" +
					"+位置信息+移动网络，插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			
		}else {
			if(gui.ShowMessageBox(("状态栏是否可下拉并可显示亮度+音量+wifi+bt+飞行模式" +
					"+位置信息+通知栏快捷开关+移动网络+漫游+apn+系统设置，插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case8:状态栏开启，添加部分菜单，移动、漫游、apn
		String[] statusMenu8 = {"cell","roaming","apn"};
		if((flag = settingsManager.setStatusBarQsTiles(statusMenu8)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			if(gui.ShowMessageBox(("状态栏是否可下拉并可显示亮度+音量+移动网络,插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}else {
			if(gui.ShowMessageBox(("状态栏是否可下拉并可显示亮度+音量+移动网络" +
					"+漫游+apn，插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		
		// case7:设置0状态栏下拉开启，添加部分菜单，BT、wifi、location
		String[] statusMenu7 = {"wifi","bt","location"};
		if((flag = settingsManager.setStatusBarQsTiles(statusMenu7))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		

		if(gui.ShowMessageBox(("状态栏是否可下拉并显示亮度+音量+wifi+bt+" +
				"位置信息，插拔USB线不应影响状态栏显示").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig24",gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 测试后置
		if(gui.ShowMessageBox("测试后置：是否要立即关闭状态栏".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			settingsManager.setStatusBarEnabled(1);
		}
		gui.cls_show_msg1_record(fileName,"systemconfig24",gScreenTime,"%s测试通过(长按确认键退出测试)",TESTITEM);
		
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
