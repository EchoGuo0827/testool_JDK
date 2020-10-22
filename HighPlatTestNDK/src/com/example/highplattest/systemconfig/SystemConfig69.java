package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SystemConfig模块
 * file name 		: SystemConfig69.java 
 * description 		: isSystemLogEnabled获取系统日志开启/关闭
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 * 						创建						20200720        	郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig69 extends UnitFragment{
	private final String TESTITEM = "isSystemLogEnabled获取系统日志开启/关闭";
	private String fileName=SystemConfig69.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	SettingsManager settingsManager=null;
	
	public void systemconfig69()
	{
		String funcName = "systemconfig69";
		boolean ret=false;
		settingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		boolean preStatus = settingsManager.isSystemLogEnabled();
		gui.cls_show_msg("此时系统日志状态=%s", preStatus?"开启":"关闭");
		
		// case1.1:系统日志打开，获取此时状态->系统日志状态开
		gui.cls_show_msg1(1, "case1.1系统日志打开，获取此时状态...");
		if((ret = settingsManager.setSystemLogEnabled(true))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = settingsManager.isSystemLogEnabled())!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case1.2:系统日志关闭，获取此时状态->系统日志状态关
		gui.cls_show_msg1(1, "case1.2系统日志打关，获取此时状态...");
		if((ret = settingsManager.setSystemLogEnabled(false))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = settingsManager.isSystemLogEnabled())!=false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4.1:系统日志打开，重启，获取此时的日志状态->系统日志状态开
		if(gui.cls_show_msg("case4.1系统日志打开,是否立即重启,获取重启的的日志状态,是[确认],否[其他]")==ENTER)
		{
			if((ret = settingsManager.setSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			Tools.reboot(myactivity);
		}
		// case4.2:系统日志关闭，重启，获取此时的日志状态->系统日志状态关
		if(gui.cls_show_msg("case4.2系统日志关闭,是否立即重启,获取重启的日志状态,是[确认],否[其他]")==ENTER)
		{
			if((ret = settingsManager.setSystemLogEnabled(false))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			Tools.reboot(myactivity);
		}
		
		// 测试后置，恢复系统日志状态
		settingsManager.setSystemLogEnabled(preStatus);
		gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "%s测试通过,重启后与重启前状态一致视为测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
