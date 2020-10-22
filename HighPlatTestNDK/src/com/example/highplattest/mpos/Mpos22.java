package com.example.highplattest.mpos;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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
 * module 			: 存储模块
 * file name 		: storage1
 * Author 			: wangxy 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集读取设备存储信息
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 wangxy		  20180517		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos22 extends UnitFragment{
	private final String TESTITEM = "读取设备存储信息(mpos)";
	public final String TAG = Mpos22.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	private byte[] pack;
	private int back;
	private byte[] dest = new byte[4000];
	
	public void mpos22()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);

		// case1:初始化存储类Auth_C101
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_C101), null);
		byte[] retContent = response.getResponse();
		String retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s初始化存储类测试失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case2:增加存储记录
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_C103), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if((back=ISOUtils.hexInt(retContent, 9, 1))!=NDK_OK)//0成功
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s增加存储记录失败(%s,%d)", Tools.getLineInfo(), TESTITEM, retCode,back);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}else{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s增加存储记录失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case3：获取存储记录数
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_C102), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if(gui.cls_show_msg("存储信息的条数:%d,是[确认],否[取消]", ISOUtils.hexInt(retContent, 9, 4))==ESC)// 长度12字节
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s获取的设备存储信息条数错误(%s,%d)", Tools.getLineInfo(), TESTITEM, retCode,ISOUtils.hexInt(retContent, 9, 4));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s获取存储记录数测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case4：获取存储记录
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_C105), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if (gui.cls_show_msg("存储信息的第一条记录:%s,是[确认],否[取消]",ISOUtils.hexString(retContent, 11, Integer.valueOf(ISOUtils.hexString(retContent, 9, 2)))) == ESC) {
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s获取存储记录第一条内容错误(%s,%s)", Tools.getLineInfo(), TESTITEM,retCode,ISOUtils.hexString(retContent, 11, Integer.valueOf(ISOUtils.hexString(retContent, 9, 2))));
				if (GlobalVariable.isContinue == false)
					return;
			}
		} else 
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s获取存储记录第一条内容测试失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//case5：更新存储记录
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_C104), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if((back=ISOUtils.hexInt(retContent, 9, 1))!=NDK_OK)//0成功
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s更新存储记录失败(%s,%d)", Tools.getLineInfo(), TESTITEM, retCode,back);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
		}else
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s更新存储记录测试失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if(GlobalVariable.isContinue==false)
			return;
		}
		
		//更新后获取存储记录
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_C105), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if(gui.cls_show_msg("更新后，存储信息的第一条记录:%s,是[确认],否[取消]", ISOUtils.hexString(retContent, 11, Integer.valueOf(ISOUtils.hexString(retContent, 9, 2))))==ESC)
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s获取的设备存储信息第一条内容错误(%s,%s)", Tools.getLineInfo(),TESTITEM,retCode,ISOUtils.hexString(retContent, 11, Integer.valueOf(ISOUtils.hexString(retContent, 9, 2))));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s获取存储记录内容测试失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//case6：写文件
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xC1,0x07},ISOUtils.hex2byte("00124D706F73B2E2CAD4CEC4BCFE000000000027CEC4BCFEC4DAC8DDCEAAA3BA313233616263B2E2CAD4B2E2CAD47E"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00")) 
		{
			retContent = response.getResponse();
			if((back=Integer.valueOf(ISOUtils.dumpString(retContent, 9, 2)))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s写文件失败(%s,%d)", Tools.getLineInfo(), TESTITEM, retCode,back);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
		}else
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s写文件失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//case7：读文件
		 pack = CalDataLrc.mposPack(new byte[]{(byte) 0xC1,0x08},ISOUtils.hex2byte("00124D706F73B2E2CAD4CEC4BCFE000000000027"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if((back=Integer.valueOf(ISOUtils.dumpString(retContent, 9, 2)))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s读文件失败(%s,%d)", Tools.getLineInfo(), TESTITEM, retCode,back);
			}
			Arrays.fill(dest, (byte) 0);
			System.arraycopy(retContent, 17, dest, 0, Integer.valueOf(ISOUtils.hexString(retContent, 15, 2)));
			try {
				if (gui.cls_show_msg("文件长度:%d,文件内容:\"%s\",是[确认],否[取消]",ISOUtils.hexInt(retContent, 11, 4),new String(dest,"GBK").trim()) == ESC) {
					gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s读文件内容不一致(%s,%d,%s)", Tools.getLineInfo(), TESTITEM,retCode,ISOUtils.hexInt(retContent, 11, 4),new String(dest,"GBK").trim());
					if (GlobalVariable.isContinue == false)
						return;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s读文件失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case8:删除文件
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xC1,0x09},ISOUtils.hex2byte("00124D706F73B2E2CAD4CEC4BCFE"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
		retContent = response.getResponse();
		ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			retContent = response.getResponse();
			if((back=Integer.valueOf(ISOUtils.dumpString(retContent, 9, 2)))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s删除文件失败(%s,%d)", Tools.getLineInfo(), TESTITEM, retCode,back);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
		}else
		{
			gui.cls_show_msg1_record(TAG, "storage1", gKeepTimeErr, "line %d:%s删除文件失败(%s)", Tools.getLineInfo(), TESTITEM, retCode);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(TAG, "storage1",gScreenTime, "%s测试通过", TESTITEM);
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}

}
