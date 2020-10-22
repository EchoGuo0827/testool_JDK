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
 * file name 		: SystemConfig35.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161128
 * directory 		: 
 * description 		: wifi高级选项安装证书选项的显示/隐藏
 * 					  setWifiInstallCedentialDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161128 	    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig35 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "wifi高级选项中安装证书选项的显示与隐藏";
	private String fileName="SystemConfig35";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig35()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig35",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		
		boolean ret = false,ret1 = false,ret2 = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// case1：参数异常测试，参数设置为-1,2，200，应返回参数错误
		gui.cls_show_msg1(1, "参数异常测试");
		if((ret = settingsManager.setWifiInstallCedentialDisplay(-1))==true||(ret1 = settingsManager.setWifiInstallCedentialDisplay(2))==true||(ret2 = settingsManager.setWifiInstallCedentialDisplay(200))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig35",gKeepTimeErr, "line %d:%s测试失败(%s,%s,%s)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:正常测试，参数设置为0，应能显示安装证书选项，并能进行安装证书操作
		gui.cls_show_msg1(1, "将显示wifi高级选项中的安装证书选项");
		if((ret = settingsManager.setWifiInstallCedentialDisplay(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig35",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("wifi高级选项中是否显示安装证书选项并可进行证书安装操作".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig35",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:正常测试，参数设置为1，应能隐藏安装证书选项
		gui.cls_show_msg1(1, "将隐藏wifi高级选项中的安装证书选项");
		if((ret = settingsManager.setWifiInstallCedentialDisplay(1))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig35",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("wifi高级选项中是否隐藏安装证书选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig35",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，隐藏该选项
		gui.cls_show_msg1_record(fileName,"systemconfig35",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：关闭wifi高级中的证书安装选项
			settingsManager.setWifiInstallCedentialDisplay(1);
		}
		settingsManager = null;
		gui = null;
	}
}
