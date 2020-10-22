package com.example.highplattest.systemconfig;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.newland.SettingsManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig61.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 
 * directory 		: setDefaultInputMethod设置输入法
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20161224	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig61 extends UnitFragment
{
	private final String TESTITEM = "setDefaultInputMethod";
	private String fileName="SystemConfig61";
	
	public void systemconfig61() 
	{
		Gui gui = new Gui(myactivity, handler);
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
		{
			gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		/* private & local definition */
		boolean ret = false;
		
		SettingsManager	settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		/* Process body */
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		
		//case1:设置成不存在的输入法(123)应返回失败实际成功
		if ((ret = settingsManager.setDefaultInputMethod("123")) != false) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line:%d,%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 动态获取输入法的服务名
		InputMethodManager mInputMethodManager = (InputMethodManager) myactivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> mInputMethodInfo = mInputMethodManager .getInputMethodList();
		 
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = mInputMethodInfo.iterator(); iterator.hasNext();) 
		{
		  InputMethodInfo inputMethodInfo = (InputMethodInfo) iterator.next();
		  //获取应用的label标签中文名
		  CharSequence name = inputMethodInfo.loadLabel(myactivity.getPackageManager());
		  LoggerUtil.v("serviceName="+inputMethodInfo.getServiceName());
		  if((ret = settingsManager.setDefaultInputMethod(inputMethodInfo.getServiceName()))!= true)
		  {
			  gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line:%d %s测试失败(%s,服务名=%s)", Tools.getLineInfo(),TESTITEM,ret,name);
			  if(!GlobalVariable.isContinue)
				  return;
		  }
		  String showTip = "输入法将设置成"+name+"，设置-语言和输入法-默认中查看是否一致，查看完成返回本用例，一致【确认】，不一致【取消】";
		  if(gui.ShowMessageBox(showTip.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		  {
			  gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line:%d 设置默认输入法失败", Tools.getLineInfo());
			  if(!GlobalVariable.isContinue)
				  return;
		  }
		}
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}

}
