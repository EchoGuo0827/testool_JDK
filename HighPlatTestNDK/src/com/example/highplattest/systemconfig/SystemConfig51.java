package com.example.highplattest.systemconfig;

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
 * file name 		: SystemConfig51.java 
 * Author 			: zhengxq	
 * version 			: 
 * DATE 			: 20170502
 * directory 		: 
 * description 		: 设置launcher，无返回值
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20170502		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig51 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "setLauncher";
	private String fileName="SystemConfig51";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig51()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig51",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// 测试前置
		gui.cls_show_msg("测试前请确保安装iLauncher和NovaLauncher，安装完毕点任意键继续");
	
		// case1:设置launcher为null,"",不存在的字符串"com.unexit.name"
		gui.cls_show_msg1(gScreenTime, "参数异常测试");
		settingsManager.setLauncher(null);
		settingsManager.setLauncher("");
		settingsManager.setLauncher("com.unexit.name");
		gui.cls_show_msg("参数异常测试不影响原先Launcher，可进入[设置]-[主屏幕]查看，任意键继续");
		// case2.1:设置为其他launcher，点击home键可生效
		gui.cls_show_msg1(gScreenTime, "设置为iLauncher测试");
		settingsManager.setLauncher("com.launcher.il");
		if(gui.ShowMessageBox("目前Launcher是否被设置为iLauncher，可进入[设置]-[主屏幕]查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig51",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2.2:重启后仍应为之前的launcher
		if(gui.ShowMessageBox("是否立即重启，[是]重启，[否]不重启，重启后应为iLauncher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);
		
		// case3.1:从其他launcher切换为原生launhcer，重启后生效
		gui.cls_show_msg1(gScreenTime, "设置为原生Laucnher测试");
		settingsManager.setLauncher("com.android.launcher");
		if(gui.ShowMessageBox("目前launcher是否被设置为原生Launcher，可进入[设置]-[主屏幕]查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig51",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3.2:重启后仍应为之前的Launcher
		if(gui.ShowMessageBox("是否立即重启，[是]重启，[否]不重启，重启后应为原生Launcher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);
		
		// case4.1:切换为NovaLauncher应成功
		gui.cls_show_msg1(gScreenTime, "目前Launcher是否被设置为NovaLauncher，可进入【设置】-【主屏幕】查看");
		settingsManager.setLauncher("com.teslacoilsw.launcher");
		if(gui.ShowMessageBox("设置Launcher是否被设置为NovaLauncher，可进入【设置】-【主屏幕】查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig51",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4.2:重启后仍应为NovaLauncher
		if(gui.ShowMessageBox("是否立即重启，[是]重启，[否]不重启，重启后应为NovaLauncher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);
		
		gui.cls_show_msg1_record(fileName,"systemconfig51",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置
			settingsManager.setLauncher("com.android.launcher");
		}
		settingsManager = null;
		gui = null;
	}
	
}
