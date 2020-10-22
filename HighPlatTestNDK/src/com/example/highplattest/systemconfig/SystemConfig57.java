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
 * module 		: 阿里需求--状态栏开启状态 
 * file name 	: SystemConfig57.java
 * Author 		: wangxy
 * version 		: 
 * DATE 		: 20181108 
 * directory 	: 
 * description 	:阿里需求--状态栏开启状态 
 * document 	: 
 * history 		:author 		date 		remarks 
 * 				wangxy 		20181108 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig57 extends UnitFragment {
	/*------------global variables definition-----------------------*/
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "阿里固件获取状态栏状态";
	private String fileName="SystemConfig57";
	boolean ret = false;
	private Gui gui=null;
	public void systemconfig57() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		/* private & local definition */
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
			return;
		}
		/* process body */
		if ((gui.ShowMessageBox(("是否为设备下载固件后第一次进入本用例测试").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg("请重新下载固件后进入本用例");
			return;
		}
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1(2, "line %d:抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
			return;
		}
		//测试前置：
		ret = settingsManager.isStatusBarExpandable();
		if ((gui.ShowMessageBox(("请确认当前设备状态栏状态,不可下拉点击【确认】，可下拉点击【取消】").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=(!ret?BTN_OK:BTN_CANCEL)) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:状态栏状态不符(%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case1:设置状态栏状态为1，关闭
		settingsManager.setStatusBarAdbNotify(1);//设置adb干扰
		if ((ret = settingsManager.setStatusBarEnabled(1)) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:状态栏下拉关闭失败(%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//状态栏关闭，则状态为false
		if (ret = settingsManager.isStatusBarExpandable()) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:状态栏状态不一致(%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((gui.ShowMessageBox(("请确认当前状态栏是否为不可下拉状态").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:%s状态栏状态不一致",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置状态栏状态为0，开启
		settingsManager.setStatusBarAdbNotify(0);
		if ((ret = settingsManager.setStatusBarEnabled(0)) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:状态栏下拉开启失败(%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 状态栏开启，则状态为true
		if (!(ret = settingsManager.isStatusBarExpandable())) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:状态栏状态不一致(%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((gui.ShowMessageBox(("请确认当前状态栏是否为可下拉状态").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME)) != BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig57",gKeepTimeErr, "line %d:%s状态栏状态不一致", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		//测试后置：关闭状态栏下拉
		settingsManager.setStatusBarEnabled(1);

		gui.cls_show_msg1_record(fileName,"systemconfig57",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}


	@Override
	public void onTestUp() 
	{
		gui=new Gui(myactivity,handler);
	}

	@Override
	public void onTestDown() 
	{
		settingsManager = null;
		gui = null;
	}
}
