package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig64.java 
 * description 		: setAirPlaneModeEnabled(boolean enabled)：开启或关闭飞行模式
 * history 		 	: 变更记录			  		  				变更时间			      变更人员
 *			  		  N700_巴西（V2.3.05）导入                                     		20200623                           郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig64 extends UnitFragment {
	private final String TESTITEM = "setAirPlaneModeEnabled:开启或关闭飞行模式";
	private String fileName = "SystemConfig64";
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager settingsManager = null;
	
	public void systemconfig64()
	{
		String funcName = "systemconfig64";
		gui.cls_show_msg("测试前请确保设备已连接上AP或无线网络可用，两种网络方式均需测试");
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		boolean iRet = false;
		
		// case1：打开飞行模式,网络一次性关闭
		gui.cls_printf("case1:打开飞行模式测试...".getBytes());
		if((iRet = settingsManager.setAirplaneModeEnabled(true))==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:打开飞行模式失败(%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("【飞行模式已开启】网络是否处于已关闭的状态")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:打开飞行模式测试失败,网络仍可以使用", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:关闭飞行模式，之前关闭网络会恢复
		gui.cls_printf("case2:关闭飞行模式测试...".getBytes());
		if((iRet = settingsManager.setAirplaneModeEnabled(false))==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:关闭飞行模式失败(%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("【飞行模式已关闭】网络是否处于已开启的状态，可正常访问网页")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:关闭飞行模式测试失败,网络不可使用", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:飞行模式状态关机，重启之后飞行模式能保持；
		gui.cls_printf("case3:重启测试飞行模式可保持...".getBytes());
		if((iRet = settingsManager.setAirplaneModeEnabled(true))==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line:%d:关闭飞行模式失败(%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("是否立即重启测试,【确认】键重启,重启后飞行模式是开启并且网络无法使用视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
	}


	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// 测试后置，关闭飞行模式
		settingsManager.setAirplaneModeEnabled(false);
	}

}
