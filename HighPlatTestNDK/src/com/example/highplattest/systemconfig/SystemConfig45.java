package com.example.highplattest.systemconfig;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig45.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170218
 * directory 		: 设置、获取设备USB的主、从模式状态
 * description 		: setUsbPortMode、getUsbPortModem(只支持IM81)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20170220 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig45 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private String fileName="SystemConfig45";
	String TESTITEM = "USB主、从模式开关";
	private Gui gui = null;
	
	public void systemconfig45() 
	{
		if(GlobalVariable.currentPlatform.toString().contains("IM81"))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig45",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
				return;
			}
			/*private & local definition
			SettingsManager settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			boolean ret1,ret2 = false;
			int preUsbMode,getUsbMode;
			// 测试前置
			preUsbMode = settingsManager.getUsbPortMode();
			// case1:异常测试，设置参数不为0,1，设置为-1,256，应返回false，异常测试不应改变原先的主、从模式
			gui.cls_show_msg(2, "usb主、从模式异常测试");
			if((ret1 = settingsManager.setUsbPortMode(-1))==true||(ret2 = settingsManager.setUsbPortMode(256))==true)
			{
				gui.cls_show_msg1(2, "line %d:%s测试失败（%s，%s）", Tools.getLineInfo(),TESTITEM,ret1,ret2);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if((getUsbMode = settingsManager.getUsbPortMode())!=preUsbMode)
			{
				gui.cls_show_msg1(2, "line %d:%s测试失败（%d，%d）", Tools.getLineInfo(),TESTITEM,getUsbMode,preUsbMode);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case2:正常测试，设置为从模式（0），获取的模式也应为从模式，可使用adb功能
			gui.cls_show_msg(2, "usb模式将设置为从模式");
			if((ret1 = settingsManager.setUsbPortMode(0))==false)
			{
				gui.cls_show_msg1(2, "line %d:设置usb从模式失败（%s）", Tools.getLineInfo(),TESTITEM,ret1);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if((getUsbMode = settingsManager.getUsbPortMode())!=0)
			{
				gui.cls_show_msg1(2, "line %d:获取usb模式错误（%d，0）", Tools.getLineInfo(),TESTITEM,getUsbMode);
				if(!GlobalVariable.isContinue)
					return;
			}
			show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "进入设置-辅助功能- USB HOST，usb模式是否被设置为从模式并且adb命令可正常使用");
			if(!GlobalVariable.FLAG_SYSTEM_SIGN)
			{
				gui.cls_show_msg1(2, "line %d:设置usb从模式失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case3:正常测试，设置为主模式（1），获取的模式应为主模式，无法使用adb功能，插入U盘可以识别到
			gui.cls_show_msg(2, "usb模式将设置为主模式");
			if((ret1 = settingsManager.setUsbPortMode(1))==false)
			{
				gui.cls_show_msg1(2, "line %d:设置usb主模式失败（%s）", Tools.getLineInfo(),TESTITEM,ret1);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if((getUsbMode = settingsManager.getUsbPortMode())!=1)
			{
				gui.cls_show_msg1(2, "line %d:获取usb模式错误（%d，1）", Tools.getLineInfo(),TESTITEM,getUsbMode);
				if(!GlobalVariable.isContinue)
					return;
			}
			show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "进入设置-辅助功能- USB HOST，usb模式是否被设置为主模式，并且adb命令无法使用");
			if(!GlobalVariable.FLAG_SYSTEM_SIGN)
			{
				gui.cls_show_msg1(2, "line %d:设置usb主模式失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case4:从主模式设置为从模式，可自动重新枚举，adb命令应能正常使用，不需要进行插拔操作
			gui.cls_show_msg(2, "usb模式将重新设置为从模式");
			if((ret1 = settingsManager.setUsbPortMode(0))==false)
			{
				gui.cls_show_msg1(2, "line %d:设置usb从模式失败（%s）", Tools.getLineInfo(),TESTITEM,ret1);
				if(!GlobalVariable.isContinue)
					return;
			}
			show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "进入设置-辅助功能- USB HOST，usb模式是否被设置为从模式并且adb命令可正常使用");
			if(!GlobalVariable.FLAG_SYSTEM_SIGN)
			{
				gui.cls_show_msg1(2, "line %d:设置usb从模式失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// 恢复默认的模式
			settingsManager.setUsbPortMode(preUsbMode);
			gui.cls_show_msg1(2, SERIAL,"%s测试通过(长按确认键退出测试)", TESTITEM);*/
		}
		else
			gui.cls_show_msg1_record(fileName,"systemconfig45",gKeepTimeErr,"%s不支持%s用例", GlobalVariable.currentPlatform,TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
