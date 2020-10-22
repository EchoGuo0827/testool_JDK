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
 * file name 		: SystemConfig16.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160606
 * directory 		: 
 * description 		: 设置底部按键的部件
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160606	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig16 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置底部返回键位置";
	private Gui gui = null;
	private String fileName="SystemConfig16";
	public void systemconfig16() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig16",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		/*process body*/
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("返回键位置\n0.返回键在左侧\n1.返回键在右侧\n2.隐藏虚拟按键\n");
			switch (nkeyIn) 
			{
			case '0':
				settingsManager.relayoutNavigationBar(0);
				if(gui.ShowMessageBox("该接口重启后才生效，是否立即重启".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
				{
					Tools.reboot(myactivity);
					gui.cls_show_msg1(1, "即将进行重启，重启后查看是否生效");
				}
				gui.cls_show_msg1(2, "返回键位置已设置，要重启后才生效，注意重启后验证选择的位置");
				break;
				
			case '1':
				settingsManager.relayoutNavigationBar(1);
				if(gui.ShowMessageBox("该接口重启后才生效，是否立即重启".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
				{
					Tools.reboot(myactivity);
					gui.cls_show_msg1(1, "即将进行重启，重启后查看是否生效");
				}
				gui.cls_show_msg1(2, "返回键位置已设置，要重启后才生效，注意重启后验证选择的位置");
				break;
				
			case '2':
				settingsManager.relayoutNavigationBar(2);
				if(gui.ShowMessageBox("该接口重启后才生效，是否立即重启".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
				{
					Tools.reboot(myactivity);
					gui.cls_show_msg1(1, "即将进行重启，重启后查看是否生效");
				}
				gui.cls_show_msg1(2, "返回键位置已设置，要重启后才生效，注意重启后验证选择的位置");
				break;
				
			case ESC:
				if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.MulAuto)
					return;
				else
				{
					unitEnd();
				}
				return;
				
			}
		}
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
		settingsManager = null;
	}
}
