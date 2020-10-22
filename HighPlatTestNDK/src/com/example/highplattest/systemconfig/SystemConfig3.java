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
 * file name 		: SystemConfig3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160605
 * directory 		: 
 * description 		: 设置是否显示存储选项
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160605	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig3 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置存储选项开关(setSettingStorageDisplay)";
	private String fileName="SystemConfig3";
/*	boolean ret = false;*/
	private Gui gui = null;

	public void systemconfig3() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig3",gScreenTime, "%s自动测试不能作为最终测试结果，请结合手动测试验证",  SystemConfig3.class.getSimpleName());
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		int nkeyIn = gui.cls_show_msg("%s\n0.setSettingStorageDisplay\n1.setSettingStorageDispley", TESTITEM);
		switch (nkeyIn) {
		case '0':
			storageDisplay();
			break;
			
		case '1':
			storageDispley();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}

	}
	
	private void storageDisplay()
	{
		String funcName = "storageDisplay";
		// 测试前置，设置存储为不显示
		settingsManager.setSettingStorageDisplay(1);
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// case1:设置为0，设置显示存储选项
		settingsManager.setSettingStorageDisplay(0);

		if(gui.ShowMessageBox("进入设置，查看是否有存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 参数不为0,1，不应旋盖设置存储选项
		settingsManager.setSettingStorageDisplay(-1);

		if(gui.ShowMessageBox("进入设置，查看是否有存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置不显示存储选项
		settingsManager.setSettingStorageDisplay(1);

		if(gui.ShowMessageBox("进入设置，查看是否无存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 参数不为0,1，不应该设置存储选项
		settingsManager.setSettingStorageDisplay(-1);

		if(gui.ShowMessageBox("进入设置，查看是否无存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，显示存储选项
		settingsManager.setSettingStorageDisplay(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	private void storageDispley()
	{
		String funcName = "storageDispley";
		// 测试前置，设置存储为不显示
		settingsManager.setSettingStorageDispley(1);
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// case1:设置为0，设置显示存储选项
		settingsManager.setSettingStorageDispley(0);

		if(gui.ShowMessageBox("进入设置，查看是否有存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 参数不为0,1，不应旋盖设置存储选项
		settingsManager.setSettingStorageDispley(-1);

		if(gui.ShowMessageBox("进入设置，查看是否有存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置不显示存储选项
		settingsManager.setSettingStorageDispley(1);

		if(gui.ShowMessageBox("进入设置，查看是否无存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 参数不为0,1，不应该设置存储选项
		settingsManager.setSettingStorageDispley(-1);

		if(gui.ShowMessageBox("进入设置，查看是否无存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，显示存储选项
		settingsManager.setSettingStorageDispley(0);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
		
	}

	@Override
	public void onTestDown() {
		settingsManager = null;
		gui = null;
	}
}
