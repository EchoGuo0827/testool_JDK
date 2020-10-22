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
/************************************************************************
 * 
 * module 			: mpos指令集非接模块
 * file name 		: card22
 * Author 			: xuess 
 * version 			: 
 * DATE 			: 20180518
 * directory 		: 
 * description 		: mpos指令集非接CPU卡通讯指令,指令E203
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		  		20180518		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos9 extends UnitFragment {
	private final String TESTITEM = "非接CPU卡通讯指令(mpos)";
	private String fileName=Mpos9.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private Gui gui = new Gui(myactivity, handler);
	private byte[] retContent = new byte[1024];
	
	public static byte[] rfidPowerUpBuf_ACard = ISOUtils.hex2byte("020007e2012f050a000a03cd");
	public static byte[] rfidPowerUpBuf_BCard = ISOUtils.hex2byte("020007e2012f070b000a03ce");
	
	
	public void mpos9(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		
		int ret = -1, len = 0;
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		// case1:A卡Apdu交互
		gui.cls_show_msg("请在感应区放置1张标准A卡,任意键继续");
		//寻卡上电
		if((ret = sendMposCmd(k21ControllerManager,rfidPowerUpBuf_ACard,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:A卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//APDU
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E203,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:A卡Apdu交互失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		if(ISOUtils.hexString(retContent, 9+len, 2).equals("9000")==false && ISOUtils.hexString(retContent, 9+len, 2).equals("6D00")==false)
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:A卡Apdu交互校验失败(sw = %s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 9+len, 2));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E202,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		// case2:B卡APDU交互
		gui.cls_show_msg("请在感应区放置1张标准B卡,任意键继续");
		//寻卡上电
		if((ret = sendMposCmd(k21ControllerManager,rfidPowerUpBuf_BCard,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:B卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//APDU
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E203,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:B卡Apdu交互测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//获取响应报文数据域长度
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		if(ISOUtils.hexString(retContent, 9+len, 2).equals("9000")==false && ISOUtils.hexString(retContent, 9+len, 2).equals("6D00")==false)
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:B卡Apdu交互校验失败(sw = %s)", Tools.getLineInfo(), ISOUtils.hexString(retContent, 9+len, 2));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E202,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "card22", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		sendMposCmd(k21ControllerManager,DataConstant.Rfid_E202,retContent);
		k21ControllerManager.close();
	}
}
