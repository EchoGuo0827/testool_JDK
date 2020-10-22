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
 * file name 		: SystemConfig36.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161206
 * directory 		: 
 * description 		: 是否显示关于设备的处理器信息选项
 * 					  setSettingProcessorDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161205 	    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig36 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "处理器信息选项隐藏/显示";
	private String fileName="SystemConfig36";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	public void systemconfig36()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
	
		boolean ret = false,ret1 = false,ret2 = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// case1:参数异常测试，分别设置为-1,2,100，应返回false，且不影响CPU型号选项的显示
		gui.cls_show_msg1(1, "参数异常测试");
		if((ret = settingsManager.setSettingProcessorDisplay(-1))||(ret1 = settingsManager.setSettingProcessorDisplay(2))||(ret2 = settingsManager.setSettingProcessorDisplay(200)))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gKeepTimeErr,"line %d:%s参数异常测试失败（%s,%s,%s）", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:显示CPU型号并对比
		if((ret = settingsManager.setSettingProcessorDisplay(0))!=true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置-关于设备是否显示处理器信息选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:隐藏CPU型号
		if((ret = settingsManager.setSettingProcessorDisplay(1))!=true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置-关于设备是否不显示处理器信息选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gKeepTimeErr,"line %d:%sCPU测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，隐藏CPU型号
		if((ret = settingsManager.setSettingProcessorDisplay(1))!=true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig36",gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig36",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);

		
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：隐藏处理器选项
			settingsManager.setSettingProcessorDisplay(1);
		}
		settingsManager = null;
		gui = null;
	}
}
