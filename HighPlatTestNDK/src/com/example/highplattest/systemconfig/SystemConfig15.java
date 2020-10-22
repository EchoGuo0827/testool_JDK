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
 * file name 		: SystemConfig15.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置全局底部任务键是否有效
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig15 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置recentApp开关";
	private Gui gui = null;
	private String fileName="SystemConfig15";
	public void systemconfig15() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig15",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
	
		boolean ret = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		/*process body*/
		// 测试全部底部recentApp有效
		settingsManager.setAppSwitchKeyEnabled(true);
		gui.cls_show_msg1(gKeepTimeErr, "%s测试中...", TESTITEM);
		
		// case1:设置全局底部任务键无效
		gui.cls_show_msg("即将设置底部recentApp键无效。。点任意键继续");
		ret = settingsManager.setAppSwitchKeyEnabled(false);
		if(!ret||gui.ShowMessageBox("点击底部recentApp键是否有效".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig15",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
				
		// case2:设置全部底部任务键有效
		gui.cls_show_msg("即将设置底部recentApp键有效。。点任意键继续");
		ret = settingsManager.setAppSwitchKeyEnabled(true);
		if(!ret || gui.ShowMessageBox("点击底部recentApp键是否有效".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig15",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig15",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
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
			// 测试后置：全部底座任务键有效
			settingsManager.setAppSwitchKeyEnabled(true);
		}
		settingsManager = null;
		gui = null;
	}
}
