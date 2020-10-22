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
 * file name 		: SystemConfig21.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置是否显示VPN选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig21 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置VPN选项开关";
	private Gui gui = null;
	private String fileName="SystemConfig21";
	public void systemconfig21() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig21",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingVpnDisplay\n1.setSettingVpnDispley\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			vpnDisplay();
			break;
			
		case '1':
			vpnDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}
	}
	
	private void vpnDisplay()
	{
		String funcName = "vpnDisplay";
		/*process body*/
		// 测试前置，隐藏vpn选项
		settingsManager.setSettingVpnDisplay(1);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置为0，显示vpn选项，显示锁屏选项
		settingsManager.setSettingVpnDisplay(0);
		settingsManager.setSettingLockScreenDisplay(0);
		gui.cls_show_msg1(1, "VPN、锁屏选项为显示状态，设置中应可查看到vpn、锁屏选项，对vpn设置锁屏，休眠唤醒后机器应正常使用");

		if(gui.ShowMessageBox("对VPN选项进行操作是否无问题".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置显示
		settingsManager.setSettingVpnDisplay(-1);
		gui.cls_show_msg1(1, "参数异常，设置中应可查看到VPN、锁屏选项");

		if(gui.ShowMessageBox("设置是否显示VPN".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN应显示，实际未显示", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，隐藏vpn选项
		settingsManager.setSettingVpnDisplay(1);
		gui.cls_show_msg1(1, "设置VPN选项为隐藏状态，设置中无法查看到VPN选项");
	
		if(gui.ShowMessageBox("设置是否不显示VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN应隐藏，实际显示",  Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置显示
		settingsManager.setSettingVpnDisplay(2);
		gui.cls_show_msg1(1, "参数异常，设置中无法查看到VPN选项");

		if(gui.ShowMessageBox("设置是否不显示VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN应隐藏，实际显示，测试失败",  Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，隐藏vpn选项
		settingsManager.setSettingVpnDisplay(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void vpnDispley()
	{
		String funcName = "vpnDispley";
		/*process body*/
		// 测试前置，隐藏vpn选项
		settingsManager.setSettingVpnDispley(1);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case1:设置为0，显示vpn选项，显示锁屏选项
		settingsManager.setSettingVpnDispley(0);
		settingsManager.setSettingLockScreenDisplay(0);
		gui.cls_show_msg1(1, "VPN、锁屏选项为显示状态，设置中应可查看到vpn、锁屏选项，对vpn设置锁屏，休眠唤醒后机器应正常使用");

		if(gui.ShowMessageBox("对VPN选项进行操作是否无问题".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置显示
		settingsManager.setSettingVpnDispley(-1);
		gui.cls_show_msg1(1, "参数异常，设置中应可查看到VPN、锁屏选项");

		if(gui.ShowMessageBox("设置是否显示VPN".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN应显示，实际未显示", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，隐藏vpn选项
		settingsManager.setSettingVpnDispley(1);
		gui.cls_show_msg1(1, "设置VPN选项为隐藏状态，设置中无法查看到VPN选项");
	
		if(gui.ShowMessageBox("设置是否不显示VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN应隐藏，实际显示",  Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置不在范围内，不影响设置显示
		settingsManager.setSettingVpnDispley(2);
		gui.cls_show_msg1(1, "参数异常，设置中无法查看到VPN选项");

		if(gui.ShowMessageBox("设置是否不显示VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:VPN应隐藏，实际显示，测试失败",  Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，隐藏vpn选项
		settingsManager.setSettingVpnDispley(1);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
		
	}

	@Override
	public void onTestDown() {
		gui = null;
		settingsManager = null;
	}
}
