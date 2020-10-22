package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.systemversion.SystemVersion1;

/************************************************************************
 * 
 * module 			: Android版本号获取模块
 * file name 		: SystemConfig67.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20190926 
 * directory 		: 
 * description 		: 获取设备控制文件内容
 * related document : 
 * history 		 	: author			date			remarks
 * history 		 	: 变更点						变更时间			变更人员
 * 					由原Systemversion11搬移		20200609		 陈丁					  		  
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig67 extends UnitFragment {
	private final String TESTITEM = "获取设备控制文件内容";
	private String fileName=SystemConfig67.class.getSimpleName();
	private String info="";
	private Gui gui = new Gui(myactivity, handler);
	SettingsManager settingsManager=null;
	
	public void systemconfig67(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N910)
		{
			gui.cls_show_msg1_record(fileName, "systemconfig60", 1, "N910海外不支持getDeviceOptConfig");
			return;
		}
		
		settingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		gui.cls_show_msg1(1, "%s获取中...", TESTITEM);
		
		info=settingsManager.getDeviceOptConfig();
		Log.d("info", "info==="+info);
		gui.cls_show_msg1_record(TESTITEM,"systemconfig67",gScreenTime,"获取完成：%s。可以不插入usb与pos升级工具获取的值进行对比确认", info);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
