package com.example.highplattest.systemversion;

import java.util.Arrays;

import android.annotation.SuppressLint;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.CommandInvokeResult;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
/************************************************************************
 * 
 * module 			: Android版本号获取模块
 * file name 		: SystemVersion3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160616
 * directory 		: 
 * description 		: 指令集版本号获取
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160616 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemVersion3 extends UnitFragment
{
	private K21ControllerManager k21ControllerManager;
	private K21DeviceCommand getZljVersion = new K21DeviceCommand(DataConstant.getZljVersion);
	private final String TESTITEM = "指令集版本号获取";
	private String fileName=SystemVersion3.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	String version;
	public void systemversion3() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		try {
			k21ControllerManager.connect();
		} catch (ControllerException e) 
		{
			gui.cls_show_msg1(gKeepTimeErr, "line %d:连接K21端失败", Tools.getLineInfo());
			return;
		}
		K21DeviceResponse resp = k21ControllerManager.sendCmd(getZljVersion, null);
		
//		int len = ((k21DeviceResponse.getResponse()[9]&0xf0)>>4)*1000+(k21DeviceResponse.getResponse()[9]&0x0f)*100+
//				((k21DeviceResponse.getResponse()[10]&0xf0)>>4)*10+(k21DeviceResponse.getResponse()[10]&0x0f);
		if(resp.getInvokeResult() == CommandInvokeResult.SUCCESS)
		{
//			 version = ISOUtils.ASCII2String(k21DeviceResponse.getResponse()).substring(11, 11+len);
			version = ISOUtils.ASCII2String(Arrays.copyOfRange(resp.getResponse(), 11, resp.getResponse().length-2));

			if(gui.ShowMessageBox(("指令集版本号为："+version).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName, "systemversion3", gKeepTimeErr,"line %d:%s测试失败(version = %s)", Tools.getLineInfo(),TESTITEM,version);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "systemversion3", gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,resp.getInvokeResult());
				return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull){
			gui.cls_show_msg1_record(fileName, "systemversion3", gScreenTime,"指令集版本号为："+version);
		}
		gui.cls_show_msg1_record(fileName, "systemversion3", gScreenTime,"%s测试通过",TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}
}
