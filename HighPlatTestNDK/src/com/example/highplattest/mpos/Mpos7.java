package com.example.highplattest.mpos;

import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
/************************************************************************
 * 
 * module 			: mpos指令集IC模块
 * file name 		: card20
 * Author 			: xuess 
 * version 			: 
 * DATE 			: 20180518
 * directory 		: 
 * description 		: mpos指令集IC卡通讯,指令E105
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		  		20180518		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos7 extends UnitFragment {
	private final String TESTITEM = "IC卡通讯(mpos)";
	private String fileName=Mpos7.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private Gui gui = new Gui(myactivity, handler);
	private byte[] retContent = new byte[1024];
	
	public static byte[] IccPowerUpBuf_at24 = ISOUtils.hex2byte("020006e1032f2a000603e4");
	public static byte[] IccPowerDownBuf_at24 = ISOUtils.hex2byte("020006e1042f2d000603e4");
	public static byte[] IccRWBuf_at24_01 = ISOUtils.hex2byte("020017e1052f200006000900d0000104313233340321");
	public static byte[] IccRWBuf_at24_02 = ISOUtils.hex2byte("020013e1052f250006000500b00001040348");
	
	List<EM_ICTYPE> ic_sam_card = GlobalVariable.cardNo;
	ArrayList<byte[]> powerUpList = new ArrayList<byte[]>();
	ArrayList<byte[]> powerDownList = new ArrayList<byte[]>();
	ArrayList<byte[]> rwList = new ArrayList<byte[]>();
	
	public void mpos7(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		
		int ret = -1,i = 0,len=0;
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		powerUpList.add(DataConstant.Icc_E103);
		powerUpList.add(DataConstant.Icc_E103_sam1);
		powerDownList.add(DataConstant.Icc_E104);
		powerDownList.add(DataConstant.Icc_E104_sam1);
		powerDownList.add(IccPowerDownBuf_at24);
		rwList.add(DataConstant.Icc_E105);
		rwList.add(DataConstant.Icc_E105_sam1);
		// 测试前置 下电
		for(i = 0;i<powerDownList.size();i++) {
			sendMposCmd(k21ControllerManager,powerDownList.get(i),retContent);
		}
		
		//case1:正常测试
		gui.cls_show_msg("请插入接触式IC卡和SAM卡槽的所有SAM卡,任意键继续");
		i=0;
		do {
			//上电
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:%s上电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			// 取随机数
			if((ret = sendMposCmd(k21ControllerManager,rwList.get(i),retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:%s读写测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			//获取应答数据长度
			try {
			    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
			} catch (NumberFormatException e) {
			    e.printStackTrace();
			}
			if(ISOUtils.hexString(retContent, 9+len, 2).equals("9000")==false && ISOUtils.hexString(retContent, 9+len, 2).equals("6D00")==false)
			{
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr,"line %d:%s取随机数失败(sw = %s)", Tools.getLineInfo(), ic_sam_card.get(i), ISOUtils.hexString(retContent, 11, 2));
				if (GlobalVariable.isContinue == false)
					return;
			}
			// case1.2：读写后再次上电下电的子用例
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:%s上电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			if((ret = sendMposCmd(k21ControllerManager,powerDownList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:%s下电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		} while (++i<powerUpList.size());
		
		// case2:卡不在位进行读写
		//上电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E103,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:IC卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("IC卡上电成功！请拨插下IC,点【确认】继续");
		// 取随机数
		//下电成功后读写应该失败,返回30 36,即06
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E105,retContent))!=SDK_ERR_INVOKE_FAILED){
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:IC卡读写测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		// IC下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E104,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:IC下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case3:测试NDK_Iccrw函数可以对memory卡进行读写
		gui.cls_show_msg("请插入at24Cxx的memory卡,任意键继续");
		//上电
		if((ret = sendMposCmd(k21ControllerManager,IccPowerUpBuf_at24,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:memory卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//用{0x00,(byte) 0xD0,0x00,0x01,0x04,0x31,0x32,0x33,0x34}指令读写,预期应答数据2位,90 00
		if((ret = sendMposCmd(k21ControllerManager,IccRWBuf_at24_01,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:memory卡读写测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.hexString(retContent, 11, 2).equals("9000")==false)
		{
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:memory卡读写校验失败(sw=%s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 11, 2));
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//用{0x00,(byte) 0xB0,0x00,0x01,0x04}指令读写,预期应答数据6位,31 32 33 34 90 00
		if((ret = sendMposCmd(k21ControllerManager,IccRWBuf_at24_02,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:memory卡读写测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.hexString(retContent, 15, 2).equals("9000")==false || ISOUtils.hexString(retContent, 11, 4).equals("31323334")==false)
		{
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:memory卡读写校验失败(%s,%s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 15, 2),ISOUtils.hexString(retContent, 11, 4));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,IccPowerDownBuf_at24,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:memory卡下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case4:不符合EMV标准但符合ISO07816标准的特殊IC卡测试
		if(gui.ShowMessageBox("是否进行不符合EMV标准但符合ISO07816标准的特殊IC卡测试?".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg("请插入只符合ISO7816特殊IC卡,点[确认]继续");
			//上电
			if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E103,retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:IC卡上电测试失败(%d)", Tools.getLineInfo(),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
			// 取随机数
			if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E105,retContent))!=SDK_OK){
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:IC卡读写测试失败(%d)", Tools.getLineInfo(),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v(ISOUtils.hexString(retContent));
			if(ISOUtils.hexString(retContent, 19, 2).equals("9000")==false)
			{
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr,"line %d:IC卡取随机数失败(sw=%s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 19, 2));
				if (GlobalVariable.isContinue == false)
					return;
			}
			// IC下电
			if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E104,retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card20", gKeepTimeErr, "line %d:IC下电测试失败(%d)", Tools.getLineInfo(),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		
		gui.cls_show_msg1_record(fileName, "card20", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		//下电
		for(int i = 0;i<powerDownList.size();i++) {
			sendMposCmd(k21ControllerManager,powerDownList.get(i),retContent);
		}
		k21ControllerManager.close();
	}
}
