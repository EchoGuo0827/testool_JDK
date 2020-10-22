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
 * module 			: 设备认证模块
 * file name 		: Auth3
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180516
 * directory 		: 
 * description 		: mpos指令集读取设备信息
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180516		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos23 extends UnitFragment
{
	private final String TESTITEM = "读取设备信息(mpos)";
	private String fileName=Mpos23.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos23()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		// case1:读取设备信息
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_F101), null);
		byte[] retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(gui.cls_show_msg("设备硬件编号(SN码):%s,与设备的SN码是否一致,是[确认],否[取消]", ISOUtils.dumpString(retContent,9,12))==ESC)// 长度12字节
			{
				gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:SN号错误()", Tools.getLineInfo(),ISOUtils.dumpString(retContent,9,12));
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg1_record(fileName, "auth3", gScreenTime,"设备个人化状态:%s", retContent[21]==0xFF?"默认初始化状态":"个人化完成");
			if(gui.cls_show_msg("MAPP版本:%s,与设备显示的版本是否一致,是[确认],否[取消]",ISOUtils.dumpString(retContent, 22, 16))==ESC)// 应用版本
			{
				gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:应用版本错误(%s)", Tools.getLineInfo(),ISOUtils.dumpString(retContent, 22, 16));
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg1_record(fileName, "auth3", gScreenTime, "设备状态:%X", retContent[48]);
			if(gui.cls_show_msg("固件版本:%s,与自检显示的固件版本是否一致,是[确认],否[取消]",ISOUtils.dumpString(retContent, 49, 16))==ESC)
			{
				gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:固件版本错误(%s)",Tools.getLineInfo(), ISOUtils.dumpString(retContent, 49, 16));
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			int csnIndex = 67;
			int csnLen = retContent[csnIndex-1]/16*10+retContent[csnIndex-1]%16;//最大值是24
			if(gui.cls_show_msg("客户序列号(CSN):%s,与设备的CSN是否一致,是[确认],否[取消]", ISOUtils.dumpString(retContent, csnIndex,csnLen))==ESC)
			{
				gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:CSN错误(%s)",Tools.getLineInfo(), ISOUtils.dumpString(retContent, csnIndex,csnLen));
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			int ksnIndex = csnIndex+csnLen+2;
			int ksnLen = retContent[ksnIndex-1]/16*10+retContent[ksnIndex-1]%16;// 最大值40
			if(gui.cls_show_msg("客户序列号(KSN):%s,与设备的KSN是否一致,是[确认],否[取消]", ISOUtils.dumpString(retContent, csnIndex,ksnLen))==ESC)
			{
				gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:CSN错误(%s)",Tools.getLineInfo(), ISOUtils.dumpString(retContent, csnIndex,ksnLen));
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			int productIndex = ksnIndex+ksnLen;
			gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr,"产品ID:%s", ISOUtils.hexString(retContent, productIndex,2));
			gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr,"厂商ID:%s", ISOUtils.hexString(retContent, productIndex+2,2));
			int snIndex = productIndex+6;
			int snLen = retContent[snIndex-1]/16*10+retContent[snIndex-1]%16;// 最大长度40
			gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr,"生产SN号:%s", ISOUtils.dumpString(retContent, snIndex, snLen));
			
			int bootIndex = snIndex+snLen+2;
			int bootLen = retContent[bootIndex-1]/16*10+retContent[bootIndex-1]%16;
			if(gui.cls_show_msg("Boot版本:%s,与自检显示的Boot版本是否一致,是[确认],否[取消]",ISOUtils.dumpString(retContent, bootIndex, bootLen))==ESC)
			{
				gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:Boot版本错误(%s)",Tools.getLineInfo(), ISOUtils.dumpString(retContent, bootIndex, bootLen));
				if(GlobalVariable.isContinue==false)
					return;
			}
			
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "auth3", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
			return;
		}
		gui.cls_show_msg1_record(fileName, "auth3", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}

}
