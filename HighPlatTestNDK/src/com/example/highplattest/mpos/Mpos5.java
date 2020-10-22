package com.example.highplattest.mpos;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
/************************************************************************
 * 
 * module 			: mpos指令集IC模块
 * file name 		: card18
 * Author 			: xuess 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集IC卡在位检测,指令E101
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		  		20180517		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos5 extends UnitFragment{
	private final String TESTITEM = "IC卡在位检测(mpos)";
	private String fileName=Mpos5.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos5(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		byte[] retContent = new byte[1024];
		String retCode;
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) 
		{
			e.printStackTrace();
		}
		
		//case1  已插入IC卡,预期获取卡状态返回0x01(不支持获取IC卡插卡状态之外的状态)
		gui.cls_show_msg("请确保插入IC卡,任意键继续");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Icc_E101), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if((retContent[9]&0xff)!=0x01){
				gui.cls_show_msg1_record(fileName, "card18", gKeepTimeErr,"line %d:IC卡在位检测失败(0x%02d)", Tools.getLineInfo(),retContent[9]&0xff);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
		} else{
			gui.cls_show_msg1_record(fileName, "card18", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case2  未插入IC卡,预期获取卡状态返回0x00
		gui.cls_show_msg("确保IC卡已移除,任意键继续");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Icc_E101), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if((retContent[9]&0xff)!=0x00){
				gui.cls_show_msg1_record(fileName, "card18", gKeepTimeErr,"line %d:IC卡在位检测失败(0x%02d)", Tools.getLineInfo(),retContent[9]&0xff);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
		} else{
			gui.cls_show_msg1_record(fileName, "card18", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "card18", gScreenTime, "%s测试通过", TESTITEM);
		
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}
}
