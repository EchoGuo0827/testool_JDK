package com.example.highplattest.other;

import android.content.Intent;
import android.provider.Settings;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: SystemConfig
 * file name 		: SystemConfig76.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200511
 * directory 		: 
 * description 		: 返回键测试(F10)
 * related document :  
 * history 		 	: 变更点										     变更人员				变更时间
 * 					  系统设置-辅助功能/网络界面按箭头应返回上一个界面，而不是设置界面  	陈丁			     20200511
 * 					 F7导入该变更点										郑薛晴			20200514
 * 					 由原systemconfig76搬移								陈丁				20200604
 *                  增加从设置—（显示，声音，连接设备，关于设备）返回第三方界面测试             郑佳雯                                 20200902
 *                  增加时间设置，返回第三方界面测试							郑薛晴			20200917
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other18 extends UnitFragment {
	private final String TESTITEM = "返回键测试(F10)";
	private String fileName="Other18";
	private Gui gui = null;
	//NetworkDashboardActivity  网络
	//AccessibilitySettingsActivity 辅助功能
	//com.android.settings.Settings.NetworkDashboardActivity
	public void other18(){
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		
		//case1  网络和互联网界面应正常返回案例
		gui.cls_show_msg("按任意键跳转到设置-网络和互联网界面。跳转后请按返回按钮。预期返回案例界面。");
//		Intent intent=new Intent();
//		intent.setClassName("com.android.settings", "com.android.settings.Settings.NetworkDashboardActivity");
//	    startActivity(intent); 
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
        myactivity.startActivity(intent);
	    


	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是，[其他]否") != ENTER) 
		{
	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:网络和互联网界面按返回按钮未返回案例", Tools.getLineInfo());
		}
	    //case2  辅助功能界面应正常返回案例
	    gui.cls_show_msg("按任意键跳转到设置-辅助功能界面。跳转后请按返回按钮。预期返回案例界面。");
		Intent intent2 = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        myactivity.startActivity(intent2);
	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是，[其他]否") != ENTER) 
		{
	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:辅助功能界面按返回按钮未返回案例", Tools.getLineInfo());
		}
	    
	  //case3  设置-声音设置界面应正常返回案例
	    gui.cls_show_msg("按任意键跳转到设置-声音设置界面。跳转后请按返回按钮。预期返回案例界面。");
	    Intent intent3 = new Intent(Settings.ACTION_SOUND_SETTINGS);
        myactivity.startActivity(intent3); 	 
  	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是,[其他]否") != ENTER) 
  		{
  	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:设置-声音设置界面按返回按钮未返回案例", Tools.getLineInfo());
  		}
	    
  	  //case4  设置-显示界面应正常返回案例
	    gui.cls_show_msg("按任意键跳转到设置-显示界面。跳转后请按返回按钮。预期返回案例界面。");
		Intent intent4 = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        myactivity.startActivity(intent4);
	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是，[其他]否") != ENTER) 
		{
	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:设置-显示界面按返回按钮未返回案例", Tools.getLineInfo());
		}
	    
	  //case5  设置-已连接设备界面应正常返回案例
	    gui.cls_show_msg("按任意键跳转到设置-已连接设备界面。跳转后请按返回按钮。预期返回案例界面。");
		Intent intent5 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        myactivity.startActivity(intent5);
	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是，[其他]否") != ENTER) 
		{
	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:设置-显示界面按返回按钮未返回案例", Tools.getLineInfo());
		}
	    
	  //case4  设置-关于设备界面应正常返回案例
	    gui.cls_show_msg("按任意键跳转到设置-关于设备界面。跳转后请按返回按钮。预期返回案例界面。");
		Intent intent6 = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
        myactivity.startActivity(intent6);
	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是，[其他]否") != ENTER) 
		{
	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:设置-关于设备界面按返回按钮未返回案例", Tools.getLineInfo());
		}
	    
	    // case5:时间设置界面应正常返回案例
	    gui.cls_show_msg("按任意键跳转到设置-时间设置界面。跳转后请按返回按钮。预期返回案例界面。");
		Intent intent7 = new Intent(Settings.ACTION_DATE_SETTINGS);
        myactivity.startActivity(intent7);
	    if (gui.cls_show_msg("是否跳转后，按返回按钮回到案例？,[确认]是，[其他]否") != ENTER) 
		{
	    	gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:设置-关于设备界面按返回按钮未返回案例", Tools.getLineInfo());
		}
	    
	    gui.cls_show_msg1_record(fileName,"other18",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
