package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig50.java 
 * Author 			: zhangxinj	
 * version 			: 
 * DATE 			: 20170328
 * directory 		: 
 * description 		: 设置禁止蓝牙传输文件
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhangxinj			20170328		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig50 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "设置禁止蓝牙传输文件";
	private String fileName="SystemConfig50";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig50()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig50",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		 settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		
		boolean flag = false;
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		/*process body*/
		// 测试前置:设置可以传输蓝牙文件
		if((flag=settingsManager.setBluetoothFileTransfer(0))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("请到设置验证是否可以传输蓝牙文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case1:参数异常测试，设置参数为2，不影响上次设置
		gui.cls_show_msg1(gScreenTime, "异常测试，设置参数为2，应设置失败");
		if((flag=settingsManager.setBluetoothFileTransfer(2))!=false){
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s参数异常失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gScreenTime, "异常测试后不应影响之前的设置");
		if(gui.ShowMessageBox("请到设置验证是否可以传输蓝牙文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s参数异常测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2：设置参数为1，预期返回true，此时不可以传输蓝牙文件
		gui.cls_show_msg1(gScreenTime, "设置参数为1，此时不可以传输蓝牙文件");
		if((flag=settingsManager.setBluetoothFileTransfer(1))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("请到设置验证是否可以传输蓝牙文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		/*//测试后置
		gui.cls_show_msg(gScreenTime, "设置参数为0，此时可以传输蓝牙文件");
		if((flag=settingsManager.setBluetoothFileTransfer(0))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("请到设置验证是否可以传输蓝牙文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig50",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		gui.cls_show_msg1_record(fileName,"systemconfig50",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		
	}
	@Override
	public void onTestUp() 
	{
		 gui = new Gui(myactivity, handler);

	}
	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			// 默认设置是可传输蓝牙数据
			settingsManager.setBluetoothFileTransfer(0);
		}
		settingsManager = null;
		gui = null;
	}
}
