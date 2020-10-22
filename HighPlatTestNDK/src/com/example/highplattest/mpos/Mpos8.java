package com.example.highplattest.mpos;

import java.util.ArrayList;
import java.util.List;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
/************************************************************************
 * 
 * module 			: mpos指令集非接模块
 * file name 		: card21
 * Author 			: xuess 
 * version 			: 
 * DATE 			: 20180518
 * directory 		: 
 * description 		: mpos指令集非接寻卡上电、下电、在位检测,指令E201、E202、E214
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		  		20180518		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos8 extends UnitFragment {
	private final String TESTITEM = "非接寻卡上电、下电、在位检测(mpos)";
	private String fileName=Mpos8.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private Gui gui = new Gui(myactivity, handler);
	private byte[] retContent = new byte[1024];
	
	public static byte[] rfidPowerUpBuf_ACard = ISOUtils.hex2byte("020007e2012f050a000a03cd");
	public static byte[] rfidPowerUpBuf_BCard = ISOUtils.hex2byte("020007e2012f070b000a03ce");
	public static byte[] rfidPowerUpBuf_M1Card = ISOUtils.hex2byte("020007e2012f090c000a03c7");
	public static byte[] rfidCheckBuf = ISOUtils.hex2byte("020007e2142f1b03006403a1");	//寻卡3次、间隔100ms
	
	public void mpos8(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		
		int ret = -1,i = 0;
		List<_SMART_t> cardList = new ArrayList<_SMART_t>();
		cardList.add(_SMART_t.CPU_A);
		cardList.add(_SMART_t.CPU_B);
		cardList.add(_SMART_t.MIFARE_1);
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		//case1:组合寻卡指令,所有类型都应寻卡上电成功
		for (i = 0; i < 3; i++) {
			gui.cls_show_msg("请在感应区放置一张标准%s卡,任意键继续",cardList.get(i));
			//寻卡上电
			if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E201,retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:%s卡上电测试失败(%d)", Tools.getLineInfo(),cardList.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			//case1.1 连续上电
			if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E201,retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:%s卡上电测试失败(%d)", Tools.getLineInfo(),cardList.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			//case6：非接下电
			if((ret = sendMposCmd(k21ControllerManager,/*DataConstant.Rfid_E202*/DataConstant.rfcardPowerOffBuf,retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		
		
		//case2:A卡激活,在位检测
		gui.cls_show_msg("请在感应区放置一张标准A卡,任意键继续");
		//寻卡上电
		if((ret = sendMposCmd(k21ControllerManager,rfidPowerUpBuf_ACard,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:A卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//在位检测
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:A卡在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请移开感应区的标准A卡,任意键继续");
		//移开后在位检测应返回30 36
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_ERR_INVOKE_FAILED){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case3:B卡激活,在位检测
		gui.cls_show_msg("请在感应区放置一张标准B卡,任意键继续");
		//寻卡上电
		if((ret = sendMposCmd(k21ControllerManager,rfidPowerUpBuf_BCard,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:B卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//在位检测
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:B卡在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请移开感应区的标准B卡,任意键继续");
		//移开后在位检测应返回30 36
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_ERR_INVOKE_FAILED){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		
		//case4:M1卡激活,在位检测
		gui.cls_show_msg("请在感应区放置一张标准M1卡,任意键继续");
		//寻卡上电
		if((ret = sendMposCmd(k21ControllerManager,rfidPowerUpBuf_M1Card,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:M1卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//在位检测
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:M1卡在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请移开感应区的标准M1卡,任意键继续");
		//移开后在位检测应返回30 36
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_ERR_INVOKE_FAILED){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case5:多张卡存在时检测不到
		gui.cls_show_msg("请在感应区放置A卡和B卡,任意键继续");
		if((ret = sendMposCmd(k21ControllerManager,rfidCheckBuf,retContent))!=SDK_ERR_INVOKE_FAILED){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:在位检测测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//非接下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E202,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card21", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "card21", gScreenTime,"%s测试通过", TESTITEM);
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
