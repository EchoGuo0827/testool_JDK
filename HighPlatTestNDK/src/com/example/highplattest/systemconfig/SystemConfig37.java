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
 * file name 		: SystemConfig37.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141028
 * directory 		: 
 * description 		: 测试系统设置usbEnable和usbDisable(只支持81设备)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20141028	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig37 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	boolean ret;
	private String TESTITEM = "usbEnable和usbDisable";
	private String fileName="SystemConfig37";
	private Gui gui = null;
	
	public void systemconfig37() 
	{
		String funcName = "systemconfig37";
		if(GlobalVariable.currentPlatform.toString().contains("IM81"))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
				return;
			}
			/*private & local definition*/
			String message = "POS是否能通过USB线连接PC";
			SettingsManager settingsManager;
//			title = "POS连接PC确认";
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			
			/*process body*/
			gui.cls_show_msg1(2, TESTITEM + "测试中...");
			/*20170209wangxiaoyu由于新81暂不支持usbEnable())方法
			//case1:正常调用函数应该能够成功，并连接电脑操作应该成功
			try
			{
				if ((ret = settingsManager.usbEnable()) == false) 
				{
					gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg(2, "line %d:未找到该接口，抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
				return;
			}*/
			//显示对话框，人工确认POS的连接情况
			if (gui.ShowMessageBox("POS端usb的接口已经打开，请将POS通过USB线连接到PC，并在PC端查看连接情况(可以通过手机助手或者PC资源管理器查看),是否连接成功".getBytes(), 
					(byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败%s", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			/*20170209wangxiaoyu由于新81暂不支持usbDisable()
			// case2:关闭usb接口查看POS是否能够连接上PC端，通过对话框进行确认操作
			if (!(ret = settingsManager.usbDisable())) 
			{
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}*/
			if (gui.ShowMessageBox("POS端usb的接口已经关闭，查看POS是否能够连接上PC(可以通过手机助手或者PC资源管理器查看)，是否断开成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败%s", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			/*20170209wangxiaoyu由于新81暂不支持usbEnable()
			// 测试后置，重新打开POS端的usb接口
			if((ret = settingsManager.usbEnable())== false)
			{
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}*/

			gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
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
		gui = null;
	}
}
