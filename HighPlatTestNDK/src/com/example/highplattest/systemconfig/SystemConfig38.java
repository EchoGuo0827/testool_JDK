package com.example.highplattest.systemconfig;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig38.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141118
 * directory 		: 
 * description 		: 测试setPowerKeySleepEnable和 setPowerKeySleepDisable(只支持IM81产品)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20141118			created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig38 extends UnitFragment
{
	private final String TESTITEM = "setPowerKeySleepEnable和setPowerKeySleepDisable";
	private String fileName="SystemConfig38";
	private Gui gui = null;

	public void systemconfig38() 
	{
		String funcName = "systemconfig38";
		if(GlobalVariable.currentPlatform.toString().contains("IM81"))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
				return;
			}
			
			/*private & local definition*/
			boolean ret;
			SettingsManager settingsManager = null;
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			
			
			/*process body*/
			gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
			/*20170209wangxiaoyu由于新81暂不支持setPowerKeySleepEnable()
			// case1 设置为短按power键能够进行休眠
			try
			{
				if (!(ret = settingsManager.setPowerKeySleepEnable())) 
				{
					gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTAPI,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg(2, "line %d:未找到该方法，抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
				return;
			}*/
			
			// 确认power键是否具有休眠功能
			if (gui.ShowMessageBox(("请短按电源键,短按电源键能进入休眠").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			/*20170209wangxiaoyu由于新81暂不支持setPowerKeySleepDisable()
			// case2 设置为短按power键不能进行休眠
			if (!(ret = settingsManager.setPowerKeySleepDisable())) 
			{
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTAPI,ret);
				if(!GlobalVariable.isContinue)
					return;
			}*/
			
			// 确认短按power键能够进入休眠
			if (gui.ShowMessageBox("请短按电源键,短按电源键不能够进行休眠".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
		}
		else
			gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s不支持%s用例", GlobalVariable.currentPlatform,TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		// 测试后置：设置为按电源键能进入休眠
		gui = null;
	}
}
