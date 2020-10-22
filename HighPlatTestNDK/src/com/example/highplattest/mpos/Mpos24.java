package com.example.highplattest.mpos;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
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
 * file name 		: Auth4
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集获取随机数
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180517		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos24 extends UnitFragment{
	private final String TESTITEM = "获取随机数(mpos)";
	private String fileName=Mpos24.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos24()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		byte[] retContent;
		// case1:读取设备信息
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		// case1:获取8字节的随机数
		gui.cls_printf("获取8字节的随机数".getBytes());
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_F102), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(gui.cls_show_msg("获取到8字节的随机数为%s,是[确认],否[取消]",ISOUtils.hexString(retContent, 9, 8))==ESC)
			{
				gui.cls_show_msg1_record(fileName, "auth4", gKeepTimeErr, "line %d:获取8字节随机数失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 9, 8));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "auth4", gKeepTimeErr, "line %d:获取8字节随机数失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}

			
		// case2:获取任意字节长度的随机数(0-255长度),暂定长度100
		gui.cls_printf("获取任意字节长度的随机数".getBytes());
		int len = (int) (Math.random()*255+1);
		byte[] pack = CalDataLrc.mposPack(new byte[]{(byte) 0xF1,0x04},new byte[]{(byte) len});
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if(gui.cls_show_msg("获取到%d字节的随机数为%s,是[确认],否[取消]", len,ISOUtils.hexString(retContent, 9, len))==ESC)
			{
				gui.cls_show_msg1_record(fileName, "auth4", gKeepTimeErr, "line %d:获取任意字节随机数失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 9, len));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "auth4", gKeepTimeErr, "line %d:获取任意字节随机数失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "auth4", gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}

}
