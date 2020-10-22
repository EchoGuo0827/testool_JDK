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
 * file name 		: SystemConfig42.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150504
 * directory 		: 触摸唤醒函数测试
 * description 		: 测试系统设置函数getTouchWakceUpMode、setTouchWakeUpMode(只支持IM81产品)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20150504 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig42 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	SettingsManager settingsManager;
	private final String TESTITEM = "getTouchWakceUpMode、setTouchWakeUpMode";
	private String fileName="SystemConfig42";
	private Gui gui = null;
	
	public void systemconfig42() 
	{
		String funcName = "systemconfig42";
		if(GlobalVariable.currentPlatform.toString().contains("IM81"))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig42",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
				return;
			}
			
			/*private & local definition*/
			int ret;
			boolean tmp;
			String message;
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			
			/*process body*/
			gui.cls_show_msg1(gScreenTime, TESTITEM + "测试中...");
//			case1,case2,	20170209wangxiaoyu由于新81暂不支持setTouchWakeUpMode(1)getTouchWakceUpMode()
			/*//case1:允许触摸唤醒
			try
			{
				settingsManager.setTouchWakeUpMode(1);
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg(2, "line %d:未找到该接口（%s）",Tools.getLineInfo(),e.getMessage());
				return;
			}
			if((ret = settingsManager.getTouchWakceUpMode()) != 1)
			{
				settingsManager.setScreenTimeout(-1);
				gui.cls_show_msg1(2, SERIAL,"line %d:%s获取是否允许触摸唤醒功能与预期不同（%d）", Tools.getLineInfo(),TESTAPI,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			message = "已经设置为允许触摸唤醒，点击是一分钟后进入休眠，休眠后尝试触摸唤醒。。。";
			handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, message));
			GlobalVariable.PORT_FLAG = true;
			while (GlobalVariable.PORT_FLAG);
			
			// 一分钟后进入休眠
			if ((tmp = settingsManager.setScreenTimeout(1)) == false) 
			{
				settingsManager.setScreenTimeout(-1);
				gui.cls_show_msg1(2, SERIAL,"line %d:%s休眠函数调用失败（%s）", Tools.getLineInfo(),TESTAPI,tmp);
				if(!GlobalVariable.isContinue)
					return;
			} 
			
			//判断是否能触摸唤醒
			message =  "一分钟后进入休眠，是否能正常触摸唤醒...";
			handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST, message));
			GlobalVariable.PORT_FLAG = true;
			while (GlobalVariable.PORT_FLAG) 
			{}
			if ((tmp = GlobalVariable.FLAG_SYSTEM_SIGN)==false) 
			{
				settingsManager.setScreenTimeout(-1);
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTAPI,tmp);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case2:禁止触摸唤醒
			settingsManager.setTouchWakeUpMode(0);
			if((ret = settingsManager.getTouchWakceUpMode()) != 0)
			{
				settingsManager.setScreenTimeout(-1);
				gui.cls_show_msg1(2, SERIAL,"line %d:%s获取是否允许触摸唤醒功能与预期不同（%d）", Tools.getLineInfo(),TESTAPI,ret);
				if(!GlobalVariable.isContinue)
					return;
			}*/
			message = "已经设置为禁止触摸唤醒，点击是一分钟后进入休眠，休眠尝试后触摸唤醒，若失败可按键唤醒...";
			gui.cls_show_msg(message);
			
			// 一分钟后进入休眠
			if ((tmp = settingsManager.setScreenTimeout(1)) == false) 
			{
				settingsManager.setScreenTimeout(-1);
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s休眠函数调用失败（%s）", Tools.getLineInfo(),TESTITEM,tmp);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//判断是否能触摸唤醒
			if (gui.cls_show_msg("是否能触摸唤醒，能【确认】，不能【其他】")==ENTER) 
			{
				settingsManager.setScreenTimeout(-1);
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,tmp);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case3:测试结束，设置回永不休眠
			if ((tmp = settingsManager.setScreenTimeout(-1))==false) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败（tmp）", Tools.getLineInfo(),TESTITEM,tmp);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
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
