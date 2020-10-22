package com.example.highplattest.mpos;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.CommandInvokeResult;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.ndk.JniNdk;

/************************************************************************
 * 
 * module 			: sys模块
 * file name 		: K21Sys.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180522
 * directory 		: 
 * description 		: 终端管理mpos指令
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj		   20180522         create
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos1 extends UnitFragment
{
	
	public final String TAG = Mpos1.class.getSimpleName();
	private final String TESTITEM = "终端管理(mpos)";
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	
	public void mpos1() throws UnsupportedEncodingException
	{
		Gui gui = new Gui(myactivity, handler);
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		String retCode;
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		byte[] retContent;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:抛出异常", Tools.getLineInfo());
		}
		
//		//蜂鸣
//		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.buzzerBuf), null);
//		byte[] retContent = response.getResponse();
//		retCode = ISOUtils.dumpString(retContent, 7, 2);
//		if(!retCode.equals("00"))
//		{
//			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:蜂鸣测试失败(%s)", Tools.getLineInfo(),retCode);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		//指示灯亮
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D12), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(gui.cls_show_msg("指示灯是否亮起,是[确认],否[取消]")==ESC)// 长度12字节
			{
				gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:指示灯亮测试失败", Tools.getLineInfo());
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:指示灯亮测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//指示灯灭
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D12_00), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(gui.cls_show_msg("指示灯是否灭掉,是[确认],否[取消]")==ESC)// 长度12字节
			{
				gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:指示灯灭测试失败", Tools.getLineInfo());
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:指示灯灭测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
//		指示灯闪烁
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D02),10,TimeUnit.SECONDS, null);
	    retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(gui.cls_show_msg("指示灯是否闪烁5次,是[确认],否[取消]")==ESC)// 长度12字节
			{
				gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:指示灯灭测试失败", Tools.getLineInfo());
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:指示灯灭测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//设置时间
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D04), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:设置时间测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//获取时间
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D05),null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(!ISOUtils.dumpString(retContent,9,14).equals("20251231131101"))// 长度12字节
			{
				gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:获取时间错误(%s)", Tools.getLineInfo(),ISOUtils.dumpString(retContent,9,14));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:获取时间测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//设置终端参数
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D06), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:设置终端参数测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//获取终端参数
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D07),10,TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			byte[] result=new byte[8];
			System.arraycopy(retContent, 14, result, 0, 8);
			if(!new String(result, "GB2312").equals("测试数据"))// 长度12字节
			{
				gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:获取终端参数错误(%s)", Tools.getLineInfo(),new String(result, "GB2312"));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:获取时间测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//回响测试 IM81
//		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Manage_1D0A), 30, TimeUnit.SECONDS, null);
//		retContent = response.getResponse();
//		
//		retCode = ISOUtils.dumpString(retContent, 7, 2);
//		if(retCode.equals("00"))
//		{
//			byte[] result=new byte[70];
//			
//			System.arraycopy(retContent, 11, result, 0, 70);
//			byte[] result_compare={0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,
//					0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,
//					0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A};
//			if(!Arrays.equals(result,result_compare))// 长度12字节
//			{
//				gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:回响测试失败", Tools.getLineInfo());
//				if(GlobalVariable.isContinue==false)
//					return;
//			}
//		}else
//		{
//			gui.cls_show_msg1_record(TAG, "k21sys12", gKeepTimeErr, "line %d:回响测试失败(%s)", Tools.getLineInfo(),retCode);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
		gui.cls_show_msg1_record(TAG, "k21sys12", gScreenTime, "测试通过");
	}
	
	
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		k21ControllerManager.close();
		k21ControllerManager = null;
	}
	
	
}
