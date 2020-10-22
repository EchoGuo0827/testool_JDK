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
 * file name 		: SystemConfig48.java 
 * Author 			: zhangxinj	
 * version 			: 
 * DATE 			: 20170301
 * directory 		: 
 * description 		: 设置支付证书升级功能是否显示
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhangxinj			20170301		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig48 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "设置支付证书升级选项开关";
	private String fileName="SystemConfig48";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig48()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig48",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证", TESTITEM);
		}
		
		boolean flag = false;
		gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
		 settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		/*process body*/
		// 测试前置:显示支付证书升级功能
		if((flag=settingsManager.setPaymentCertUpdateDisplay(0))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case1:参数设置为1，返回true，预期不显示支付证书升级功能
		gui.cls_show_msg1(2, "参数设置为1，预期不显示支付证书升级功能");
		if((flag=settingsManager.setPaymentCertUpdateDisplay(1))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("进入设置-安全中查看是否没有更新支付证书选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//不设置为0、1，返回false，不应改变支付证书升级功能，预期不显示支付证书升级功能
		gui.cls_show_msg1(2, "参数设置为-1，不应改变支付证书升级功能");
		if((flag=settingsManager.setPaymentCertUpdateDisplay(-1))!=false){
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("进入设置-安全中查看是否没有更新支付证书选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2:参数设置为0，返回true，预期显示支付证书升级功能
		gui.cls_show_msg1(2, "参数设置为0，预期显示支付证书升级功能");
		if((flag=settingsManager.setPaymentCertUpdateDisplay(0))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("进入设置-安全中查看是否有更新支付证书选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//不设置为0、1，返回false，不应改变支付证书升级功能，预期显示支付证书升级功能
		gui.cls_show_msg1(2, "参数设置为-1，不应改变支付证书升级功能");
		if((flag=settingsManager.setPaymentCertUpdateDisplay(-1))!=false){
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("进入设置-安全中查看是否有更新支付证书选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig48",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		
	}
	@Override
	public void onTestUp() {
		 gui = new Gui(myactivity, handler);
	
		
	}
	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：默认显示更新支付证书选项
			settingsManager.setPaymentCertUpdateDisplay(0);
		}
		settingsManager = null;
		gui = null;
	}
}
