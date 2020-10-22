package com.example.highplattest.systemconfig;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import android.annotation.SuppressLint;
import android.newland.SettingsManager;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig40.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150424
 * directory 		: 
 * description 		: 测试系统与K21通信不上时提示对话框，系统锁定，跟开发确认过setK21CommunicateWarnEnable是在开机的时候进行检测(只支持IM81产品)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150424			created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig40 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "setK21CommunicateWarnEnable";
	private String fileName="SystemConfig40";
	private SettingsManager settingsManager;
	private Gui gui = null;
	
	public void systemconfig40() 
	{
		String funcName="systemconfig40";
		if(GlobalVariable.currentPlatform.toString().contains("IM81"))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
				return;
			}
			
			/*private & local definition*/
			boolean ret = false;
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			
			/*process body*/
			gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		/*	20170209wangxiaoyu由于新81暂不支持setK21CommunicateWarnEnable()
			// case1:测试系统与K21通信不上时提示对话框，系统锁，预期结果：开机完成出现系统锁定的对话框
			try
			{
				if(!(ret = settingsManager.setK21CommunicateWarnEnable()))
				{
					gui.cls_show_msg1(2, SERIAL, "line %d:%s测试失败%d", Tools.getLineInfo(),TESTAPI,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg(2, "该用例不支持");
//				auto();20170119wangxiaoyu
				return;
			}
			*/
			gui.ShowMessageBox("重新开机，开机前先将K21与Android之间的连线断开，开机完成弹出系统锁定对话框则测试通过，任意键继续".getBytes(), (byte) 0, WAITMAXTIME);
		}
		else
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"%s不支持%s用例", GlobalVariable.currentPlatform,TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
