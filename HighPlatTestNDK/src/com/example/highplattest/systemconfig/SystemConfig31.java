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
 * file name 		: SystemConfig31.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: OTA升级显示/不显示
 * 					  setSettingOtaUpdateEnabled(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig31 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "OTA升级显示/不显示";
	private String fileName="SystemConfig31";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig31()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig31",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		boolean flag;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// 测试前置：先安装MTMS，在关于设备下显示
		gui.cls_show_msg("请先安装了MTMS应用，安装完毕后重启设备后再进入本用例，任意键继续");
		
		// case1:设置OTA升级选项显示
		gui.cls_show_msg1(1, "将显示关于设备-系统更新选项");
		if((flag = settingsManager.setSettingOtaUpdateEnabled(true)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig31",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置中是否显示关于设备-系统更新选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig31",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置OTA升级选项隐藏
		gui.cls_show_msg1(1, "将隐藏关于设备-系统更新");
		if((flag = settingsManager.setSettingOtaUpdateEnabled(false)) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig31",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置中是否不显示关于设备-系统更新选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig31",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig31",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		 gui = new Gui(myactivity, handler);
		
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：默认不显示
			settingsManager.setSettingOtaUpdateEnabled(false);
		}
		settingsManager = null;
		gui = null;
	}
}
