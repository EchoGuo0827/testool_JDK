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
 * file name 		: SystemConfig33.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161116
 * directory 		: 
 * description 		: 状态信息的显示/隐藏接口
 * 					  setSettingDeviceInfoItemsDisplay(String value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161116		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig33 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "状态信息的显示接口/隐藏";
	private String fileName="SystemConfig33";
	private final String setDedault = "00000";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig33()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}

		boolean ret = false;
		 settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		// case1:异常测试 字符串为null，“”，-1,32
		gui.cls_show_msg1(1, "参数异常测试");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay(null))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay(""))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("0"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("111"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:只显示状态信息01111
		gui.cls_show_msg1(1, "将显示关于设备-状态信息，不显示法律信息、内核版本、bootloader版本、基带版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("01111")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否只显示状态信息，不显示法律信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:只显示法律信息10111
		gui.cls_show_msg1(1, "将显示关于设备-法律信息，不显示状态信息、内核版本、bootloader版本、基带版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("10111")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否只显示法律信息，不显示状态信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4:只显示内核版本
		gui.cls_show_msg1(1, "将显示关于设备-内核版本，不显示状态信息、法律信息、bootloader版本、基带版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("11011")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否只显示内核版本，不显示状态信息、法律信息、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5:只显示bootloader版本
		gui.cls_show_msg1(1, "将显示关于设备-bootloader版本，不显示状态信息、法律信息、内核版本、基带版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("11101")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否只显示bootloader版本，不显示状态信息、法律信息、内核版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case6:只显示基带版本
		gui.cls_show_msg1(1, "将显示关于设备-基带版本，不显示状态信息、法律信息、内核版本、bootloader版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("11110")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否只显示基带版本，不显示状态信息、法律信息、内核版本、bootloader版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case7:两种混合显示，显示基带版本和状态信息
		gui.cls_show_msg1(1, "将显示关于设备-基带版本、状态信息，不显示法律信息、内核版本、bootloader版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("01110")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否只显示基带版本、状态信息，不显示法律信息、内核版本、bootloader版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case8:全部隐藏
		gui.cls_show_msg1(1, "关于设备不显示状态信息、法律信息、内核版本、bootloader版本、基带版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("11111")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否不显示状态信息、法律信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case9：全部显示
		gui.cls_show_msg1(1, "关于设备显示状态信息、法律信息、内核版本、bootloader版本、基带版本");
		if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("00000")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("关于设备是否显示状态信息、法律信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig33",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig33",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	@Override
	public void onTestUp() {
		 gui = new Gui(myactivity, handler);		
	}
	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置，状态信息接口恢复默认
			settingsManager.setSettingDeviceInfoItemsDisplay(setDedault);
		}
		settingsManager = null;
		gui = null;
		
	}
}
