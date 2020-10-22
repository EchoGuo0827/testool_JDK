package com.example.highplattest.mpos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
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
 * module 			: 磁条卡模块
 * file name 		: card17
 * Author 			: wangxy 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集磁条卡
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 wangxy		  20180517		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos4 extends UnitFragment{
	private final String TESTITEM = "磁条卡信息(mpos)";
	private String fileName=Mpos4.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	private CommandInvokeResult ret;
	private byte[] retContent;
	private Map<String, String> map=new HashMap<String, String>();
	private final int MAXWAITTIME = 30;
	private int len1=0,len2=0,len3=0,begin1=0,begin2=0,begin3=0;
	private String content1,content2,content3;
	
	public void mpos4()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		gui.cls_show_msg("请确定已准备好磁卡、A、B、M1射频卡、IC卡,完成任意键继续");
		int back1=-1;
		String back2;
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		map.put("A", "14");
		map.put("B", "24");
		map.put("M1", "44");  
		
		//case1：开启读卡器/磁卡
		gui.cls_printf(String.format("请刷银行卡(%ds)--->\n", MAXWAITTIME).getBytes());
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D101), MAXWAITTIME, TimeUnit.SECONDS,  null);
		if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) 
		{
			retContent = response.getResponse();
			if((back1=ISOUtils.hexInt(retContent, 9, 1))!=1)//1=磁卡；2=IC；射频=4
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s响应读卡方式错误(%s,%d)", Tools.getLineInfo(),TESTITEM,ret,back1);
				if (GlobalVariable.isContinue == false)
					return;
			}
			//01刷卡成功、11刷卡完成但失败，需要银行卡磁条，带校验
			if(!(back2=ISOUtils.hexString(retContent, 12, Integer.valueOf(ISOUtils.hexString(retContent, 10, 2)))).equals("01"))
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s刷卡结果错误(%s,%s)", Tools.getLineInfo(),TESTITEM,ret,back2);
				if (GlobalVariable.isContinue == false)
					return;
			}
			// case5:关闭读卡器
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D102), null);
			if ((ret = response.getInvokeResult()) != CommandInvokeResult.SUCCESS) {
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s关闭读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s刷卡失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case2：开启读卡器/IC
		gui.cls_show_msg("请插入IC卡,完成任意键继续");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D101), null);
		if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) 
		{
			retContent = response.getResponse();
			if((back1=ISOUtils.hexInt(retContent, 9, 1))!=2)//1=磁卡；2=IC；射频=4
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s响应读卡方式错误(%s,%d)", Tools.getLineInfo(),TESTITEM,ret,back1);
				if (GlobalVariable.isContinue == false)
					return;
			}
			// case5:关闭读卡器
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D102), null);
			if ((ret = response.getInvokeResult()) != CommandInvokeResult.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s关闭读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}else
		{
			gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%sIC卡测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case3：开启读卡器/射频A B M1
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			gui.cls_show_msg("请放置%s卡,完成任意键继续", key);
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D101),null);
			if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) 
			{
				retContent = response.getResponse();
				if ((back1 = ISOUtils.hexInt(retContent, 9, 1)) != 4) // 1=磁卡；2=IC；射频=4
				{
					gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s%s卡响应读卡方式错误(%s,%d)", Tools.getLineInfo(), TESTITEM,key,ret,back1);
					if (GlobalVariable.isContinue == false)
						return;
				}
				// 14=A；24=B；44=M1
				if (!(back2 = ISOUtils.hexString(retContent, 12,Integer.valueOf(ISOUtils.hexString(retContent, 10, 2)))).equals(value)) 
				{
					gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s%s卡结果错误(%s,%s)", Tools.getLineInfo(), TESTITEM, key,ret,back2);
					if (GlobalVariable.isContinue == false)
						return;
				}
				// 仅射频卡有序列号
				String serial = ISOUtils.hexString(retContent, 15,Integer.valueOf(ISOUtils.hexString(retContent, 13, 2)));
				if (gui.cls_show_msg("%s卡序列号:%s,是[确认],否[取消]", key, serial) == ESC) 
				{
					gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s获取%s卡序列号错误(%s,%s)", Tools.getLineInfo(), TESTITEM,key,ret,serial);
					if (GlobalVariable.isContinue == false)
						return;
				}
				// case5:关闭读卡器
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D102), null);
				if ((ret = response.getInvokeResult()) != CommandInvokeResult.SUCCESS) 
				{
					gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s关闭读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
					if (GlobalVariable.isContinue == false)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s%s卡测试失败(%s)", Tools.getLineInfo(), TESTITEM, key,ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		
		//case4：读磁条卡（明文）
		int len=7;//1=第一磁道，2=第二磁道，4=第三磁道
		byte[] pack = CalDataLrc.mposPack(new byte[]{(byte) 0xD1,0x04},new byte[]{(byte) len});
		gui.cls_printf(String.format("读磁条卡明文,请刷银行卡(%ds)--->\n", MAXWAITTIME).getBytes());
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D101),MAXWAITTIME,TimeUnit.SECONDS, null);
		if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) {
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack),MAXWAITTIME,TimeUnit.SECONDS, null);
			if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) 
			{
				retContent = response.getResponse();
				begin1 = 41;
				if ((len1 = Integer.valueOf(ISOUtils.hexString(retContent, begin1, 2))) != 0) 
				{// 一磁道有数据
					content1 = ISOUtils.dumpString(retContent, begin1 + 2, len1);
				} else 
				{
					len1 = 0;
					content1 = "";
				}
				begin2 = begin1 + 2 + len1;
				if ((len2 = Integer.valueOf(ISOUtils.hexString(retContent, begin2, 2))) != 0) 
				{
					content2 = ISOUtils.dumpString(retContent, begin2 + 2, len2);
				} else 
				{
					len2 = 0;
					content2 = "";
				}
				begin3 = begin2 + 2 + len2;
				if ((len3 = Integer.valueOf(ISOUtils.hexString(retContent, begin3, 2))) != 0) 
				{
					content3 = ISOUtils.dumpString(retContent, begin3 + 2, len3);
				} else 
				{
					len3 = 0;
					content3 = "";
				}

				if (gui.cls_show_msg("1道数据(%d):%s\n2道数据(%d):%s\n3道数据(%d):%s\n,是[确认],否[取消]", len1, content1, len2,content2, len3, content3) == ESC)
				{
					gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s读文件内容不一致(%s,%d,%s,%d,%s,%d,%s)",Tools.getLineInfo(), TESTITEM, ret, len1, content1, len2, content2, len3, content3);
					if (GlobalVariable.isContinue == false)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s明文刷磁卡测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			// case5:关闭读卡器
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D102), null);
			if ((ret = response.getInvokeResult()) != CommandInvokeResult.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s关闭读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		} else 
		{
			gui.cls_show_msg1_record(fileName, "card17", gKeepTimeErr, "line %d:%s打开读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(fileName, "card17", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}

}
