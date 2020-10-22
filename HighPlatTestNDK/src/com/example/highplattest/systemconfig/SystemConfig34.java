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
 * file name 		: SystemConfig34.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161128
 * directory 		: 
 * description 		: 网络共享的便携式热点选项显示/隐藏
 * 					  setTetherDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161128		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig34 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "网络共享的便携式热点选项显示/隐藏";
	private String fileName="SystemConfig34";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig34()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig34",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}

		boolean ret = false,ret1 = false,ret2= false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// 测试前置
		/*process body*/
		/*if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg(1, "未插入sim卡，请先插卡");
			return;
		}*/
		
		// case1：参数异常测试，参数设置为-1,2，200，应返回参数错误
		gui.cls_show_msg1(1, "参数异常测试");
		if((ret = settingsManager.setTetherDisplay(-1))==true||(ret1 = settingsManager.setTetherDisplay(2))==true||(ret2 = settingsManager.setTetherDisplay(200))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig34",gKeepTimeErr, "line %d:%s测试失败(%s,%s,%s)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:正常测试，参数设置为0，插入sim卡，应能网络共享的便携式热点选项显示，并可正常开启热点
		gui.cls_show_msg1(1, "将显示网络共享的便携式热点选项，开启便携式热点可被其他设备搜索并连接上网，关闭便携式热点其他设备应无法搜索到");
		if((ret = settingsManager.setTetherDisplay(0))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig34",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.ShowMessageBox("设置是否显示网络共享的便携式热点选项，开启便携式热点，其他设备可连接上网，关闭便携式热点其他设备应无法搜索到".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig34",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:正常测试，参数设置为1，应能网络共享的便携式热点选项隐藏
		gui.cls_show_msg1(1, "将隐藏网络共享的便携式热点选项，开启的便携式热点隐藏后应不可被其他设备搜索使用");
		if((ret = settingsManager.setTetherDisplay(1))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig34",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置是否隐藏网络共享的便携式热点选项，开启的便携式热点隐藏后应不可被其他设备搜索使用".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig34",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig34",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
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
			// 测试后置：便携式热点选项关闭
			settingsManager.setTetherDisplay(1);
		}
		settingsManager = null;
		gui = null;
	}
}
