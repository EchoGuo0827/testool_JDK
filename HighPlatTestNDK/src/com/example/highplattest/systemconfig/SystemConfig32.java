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
 * file name 		: SystemConfig32.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 壁纸显示/不显示
 * 					  setSettingWallpaperDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig32 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "壁纸显示/不显示";
	private String fileName="SystemConfig32";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig32()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig32",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证", TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// case1:设置0，显示壁纸
		gui.cls_show_msg1(1, "设置显示-壁纸选项将显示");
		settingsManager.setSettingWallpaperDisplay(0);
		
		if(gui.ShowMessageBox("设置中显示-壁纸选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig32",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:参数异常测试，设置为-1,2,应不影响当前显示效果
		gui.cls_show_msg1(1, "参数异常测试，将不影响之前显示效果，设置显示-壁纸选项将显示");
		settingsManager.setSettingWallpaperDisplay(-1);
		settingsManager.setSettingWallpaperDisplay(2);
	
		if(gui.ShowMessageBox("设置中显示-壁纸选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig32",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:设置1，不显示壁纸
		gui.cls_show_msg1(1, "设置显示-壁纸选项将隐藏");
		settingsManager.setSettingWallpaperDisplay(1);
		if(gui.ShowMessageBox("设置中显示-壁纸选项是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig32",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig32",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	@Override
	public void onTestUp() 
	{
		gui = new Gui(myactivity, handler);
	
	}
	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			settingsManager.setSettingWallpaperDisplay(0);
		}
		settingsManager = null;
		gui = null;
		
	}
}
