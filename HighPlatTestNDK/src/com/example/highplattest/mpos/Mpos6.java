package com.example.highplattest.mpos;

import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
/************************************************************************
 * 
 * module 			: mpos指令集IC模块
 * file name 		: card19
 * Author 			: xuess 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集IC卡上电、下电,指令E103、E104
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		  		20180517		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos6 extends UnitFragment{
	private final String TESTITEM = "IC卡上电、下电(mpos)";
	private String fileName=Mpos6.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private Gui gui = new Gui(myactivity, handler);
	private byte[] retContent = new byte[1024];
	
	List<EM_ICTYPE> ic_sam_card = GlobalVariable.cardNo;
	ArrayList<byte[]> powerUpList = new ArrayList<byte[]>();
	ArrayList<byte[]> powerDownList = new ArrayList<byte[]>();
	ArrayList<byte[]> rwList = new ArrayList<byte[]>();
	
	public void mpos6()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		
		int ret = -1,i = 0;
		
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
		rwList.add(DataConstant.Icc_E105);
		rwList.add(DataConstant.Icc_E105_sam1);
		
		//case1:IC/SAM卡正常测试上电、下电
		gui.cls_show_msg1(3, "请插入接触式IC卡和SAM卡槽的所有SAM卡,任意键继续");
		i = 0;
		do {
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:%s上电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			if((ret = sendMposCmd(k21ControllerManager,powerDownList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:%s下电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
			//下电成功后读写应该失败
			if((ret = sendMposCmd(k21ControllerManager,rwList.get(i),retContent))!=SDK_ERR_INVOKE_FAILED)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:%s下电读写测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
		} while (++i<powerUpList.size());
		
		//case2:IC卡上电后,SAM卡也上电,接着IC卡读写,然后SAM卡读写操作（所有SAM卡操作完毕,IC卡下电）
		//IC、SAM上电
		i = 0;
		do 
		{
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:%s上电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		} while (++i<powerUpList.size());
		//IC、SAM读写
		i = 0;
		do 
		{
			if((ret = sendMposCmd(k21ControllerManager,rwList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:%s读写测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		} while (++i<powerDownList.size());
		//SAM下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E104_sam1,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:SAM卡下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//IC下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E104,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:IC卡下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case3:IC、SAM均上电,SAM卡读写并下电,然后,IC卡读写并下电
		//IC、SAM上电
		i = 0;
		do 
		{
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:%s上电测试失败(%d)", Tools.getLineInfo(),ic_sam_card.get(i),ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		} while (++i<powerUpList.size());
		//SAM卡读写并下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E105_sam1,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:SAM卡读写测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E104_sam1,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:SAM卡读写测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//IC读写并下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E105,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:IC卡读写测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E104,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:IC卡下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case4:因增加IC卡上电保护机制,故IC卡上电后进行拔卡后继续上电不应出现问题
		gui.cls_printf("异常测试:IC连续上电测试".getBytes());
		gui.cls_show_msg("请插入IC和SAM卡,任意键继续");
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E103,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:IC卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// 进行拔插IC卡的操作
		gui.cls_show_msg("请进行拔插IC卡的操作,任意键继续");
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Icc_E103,retContent))!=SDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:IC卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		i=1;
		do 
		{
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:SAM卡上电测试失败(%d,%d)", Tools.getLineInfo(),i,ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			if((ret = sendMposCmd(k21ControllerManager,powerUpList.get(i),retContent))!=SDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "card19", gKeepTimeErr, "line %d:SAM卡再次上电测试失败(%d,%d)", Tools.getLineInfo(),i,ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		} while (++i<powerUpList.size());
		
		gui.cls_show_msg1_record(fileName, "card19", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		//下电
		for(int i = 0;i<powerDownList.size();i++) 
		{
			sendMposCmd(k21ControllerManager,powerDownList.get(i),retContent);
		}
		k21ControllerManager.close();
	}
}
