package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig59.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20181224
 * directory 		: 
 * description 		: 音量键唤醒系统功能(银商)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhush		   20181224	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig59 extends UnitFragment{
	private final String TESTITEM="setWakeUpSystemEnable音量键唤醒系统功能";
	private Gui gui = new Gui(myactivity, handler);
	private String filename="SystemConfig59";
	private SettingsManager settingsManager = null;
	public void systemconfig59()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N910)
		{
			gui.cls_show_msg1_record(filename, "systemconfig59", 1, "N910海外不支持setWakeUpSystemEnable");
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(filename,"systemconfig59",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
	
		/*private & local definition*/
		boolean flag = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		/*process body*/	
		//case 1:音量+-能唤醒系统功能
		gui.cls_printf((TESTITEM+"测试...").getBytes());
		gui.cls_printf("音量键+-唤醒系统".getBytes());
		settingsManager.setWakeUpSystemEnable(true);
		if(flag=settingsManager.getWakeUpSystemEnable()!=true){
			gui.cls_show_msg1_record(filename,"systemconfig59",gKeepTimeErr, "line %d: 前置配置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("让屏幕进入休眠,按音量键是否能唤醒设备,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(filename,"systemconfig59",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示,休眠时间设为15s后,自动休眠后短按音量键是否可唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(filename,"systemconfig59",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("测试短按音量键唤醒屏幕重启后是有效，是否立即重启，重启后设备进入休眠短按音量键唤醒设备视为测试通过,再次运行到此处请选择否执行后面的案例")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
		//case 2:音量+-不能唤醒系统功能
		gui.cls_printf("音量键+-不能唤醒系统".getBytes());
		settingsManager.setWakeUpSystemEnable(false);
		if(flag=settingsManager.getWakeUpSystemEnable()!=false){
			gui.cls_show_msg1_record(filename,"systemconfig59",gKeepTimeErr, "line %d: 前置配置失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("让屏幕进入休眠,按音量键是否不能唤醒设备,是[确认],否[其他]")!=ENTER)
		{
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示,休眠时间设为15s后,自动休眠后短按音量键是否无法唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(filename,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("测试短按音量键唤醒屏幕重启是否保持，是否立即重启，重启后设备进入休眠短按音量键不能唤醒设备视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
	}
	@Override
	public void onTestUp() {	
		
	}
	@Override
	public void onTestDown() {
	}
}
