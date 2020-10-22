package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig29.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 声音中音频显示/隐藏
 * 					  setSettingNotificationItemsDisplay(String value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig29 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "声音中音频显示/隐藏";
	private String fileName="SystemConfig29";
	private final String setDefault = "00001111";
	private Gui gui = null;
	private SettingsManager settingsManager;
	
	public void systemconfig29()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig29",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);	
		// case13:参数异常测试，设置为null、“”、“0”、“111”，不影响原先显示
		gui.cls_show_msg1(1, "参数异常测试，应影响原先显示");
		settingsManager.setSettingNotificationItemsDisplay(null);
		settingsManager.setSettingNotificationItemsDisplay("");
		settingsManager.setSettingNotificationItemsDisplay("0");
		settingsManager.setSettingNotificationItemsDisplay("111");

		if(gui.ShowMessageBox("音量选项中是否显示声音-媒体音量、铃声音量、设备铃声、默认通知铃声".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
			{
				settingsManager.setSettingNotificationItemsDisplay(setDefault);
				return;
			}
		}
		
		//case1:只显示媒体音量选项“01111111”
		gui.cls_show_msg1(1, "将只显示声音-媒体音量选项");
		settingsManager.setSettingNotificationItemsDisplay("01111111");

		if(gui.ShowMessageBox("音量选项中是否只显示声音-媒体音量选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:只显示铃声音量选项“10111111”
		gui.cls_show_msg1(1, "将只显示声音-铃声音量选项");
		settingsManager.setSettingNotificationItemsDisplay("10111111");

		if(gui.ShowMessageBox("音量选项中是否只显示声音-铃声音量选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:只显示设备铃声选项“11011111”
		gui.cls_show_msg1(1, "将只显示声音-设备铃声选项");
		settingsManager.setSettingNotificationItemsDisplay("11011111");

		if(gui.ShowMessageBox("音量选项中是否只显示声音-设备铃声选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4:只显示默认铃声选项“11101111”
		gui.cls_show_msg1(1, "将只显示声音-默认音量选项");
		settingsManager.setSettingNotificationItemsDisplay("11101111");
	
		if(gui.ShowMessageBox("音量选项中是否只显示声音-默认铃声音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 针对Android7.1平台，后四个功能已经统一划入提示音了，没有震动等功能 by 20200318 zhengxq
		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A7)
		{
			// case5:只显示拨号键盘提示音选项“11110111”
			gui.cls_show_msg1(1, "将只显示声音-拨号键盘提示音选项");
			settingsManager.setSettingNotificationItemsDisplay("11110111");

			if(gui.ShowMessageBox("音量选项中是否只显示声音-拨号键盘提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case6:只显示屏幕锁定提示音选项“11111011”
			gui.cls_show_msg1(1, "将只显示声音-屏幕锁定提示音选项");
			settingsManager.setSettingNotificationItemsDisplay("11111011");

			if(gui.ShowMessageBox("音量选项中是否只显示声音-屏幕锁定提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case7:只显示触摸提示音选项“11111101”
			gui.cls_show_msg1(1, "将只显示声音-触屏提示音选项");
			settingsManager.setSettingNotificationItemsDisplay("11111101");

			if(gui.ShowMessageBox("音量选项中是否只显示声音-触屏提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case8:只显示触摸时振动选项“11111110”
			gui.cls_show_msg1(1, "将只显示声音-触屏时振动选项");
			settingsManager.setSettingNotificationItemsDisplay("11111110");

			if(gui.ShowMessageBox("音量选项中是否只显示声音-触屏时振动选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case9:关闭所有音量选项
			gui.cls_show_msg1(1, "关闭声音下所有选项");
			settingsManager.setSettingNotificationItemsDisplay("11111111");

			if(gui.ShowMessageBox("音量选项中是否无选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case10:三种组合在一起显示“00011111”
			gui.cls_show_msg1(1, "将显示声音-媒体音量、声音-铃声音量、声音-设备铃声选项");
			settingsManager.setSettingNotificationItemsDisplay("00011111");

			if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case11:五种组合在一起显示
			gui.cls_show_msg1(1, "将显示声音-媒体音量、铃声音量、设备铃声选项、拨号键盘提示音、屏幕锁定提示音");
			settingsManager.setSettingNotificationItemsDisplay("00010011");

			if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声、拨号键盘提示音、屏幕锁定提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case12:全部选项都显示
			gui.cls_show_msg1(1, "将显示声音-媒体音量、铃声音量、设备铃声选项、默认通知铃声、拨号键盘提示音、屏幕锁定提示音、触摸提示音、触摸时振动");
			settingsManager.setSettingNotificationItemsDisplay("00000000");

			if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声选项、默认通知铃声、拨号键盘提示音、屏幕锁定提示音、触摸提示音、触摸时振动选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig29",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		
		gui.cls_show_msg1_record(fileName,"systemconfig29",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
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
			// 测试后置
			settingsManager.setSettingNotificationItemsDisplay(setDefault);
		}
		settingsManager = null;
		gui = null;
	}
}
