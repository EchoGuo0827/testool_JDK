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
 * module 			: 安全模块
 * file name 		: sec9
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180521
 * directory 		: 
 * description 		: mpos指令集装载DUKP的KSN和初始密钥(mpos)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180521		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos17 extends UnitFragment
{
	private final String TESTITEM = "装载DUKPT的KSN和初始密钥(mpos)";
	public final String TAG = Mpos17.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos17()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		// 测试前置：删除所有密钥装载各种密钥
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		byte[] retContent = new byte[128];
		String kcv;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 测试前置：擦除所有密钥，装载TLK和TMK  mpos会自动装TLK
		// 擦除密钥
//		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
//		retContent = response.getResponse();
//		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
//		{
//			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:擦除所有密钥失败(%s)",Tools.getLineInfo(),retCode);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		// 安装TLK,ID=1,16个31
//		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null);
//		retContent = response.getResponse();
//		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
//		{
//			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:安装TLK密钥失败(%s)",Tools.getLineInfo(),retCode);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		// 安装TMK,ID=5,8个15+8个17
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02},ISOUtils.hex2byte("02050016CB62659448AC8A721E28428CFDB2FD30FF000452442550"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:安装TMK密钥失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.1:装载DUKPT密钥,传输密钥加密DUKPT密钥的方式，初始密钥8字节,应失败,至少需要16字节的密钥(DUKPT密钥明文8个15)
		byte[] pack_dukpt1 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("0201FFFFFFFFFFFFFFE000060008CB62659448AC8A720100081111111111111111"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("02")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.2:错误的checkValue值应返回密钥校验值错误(不支持checkValue校验)
		byte[] pack_dukpt2 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("020AFFFFFFFFFFFFFFE000060016CB62659448AC8A721E28428CFDB2FD30010016CEB1BBCA24CFEADBE8FE4896E9D122AA"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.3:错误的主密钥索引，应返43,实际返回02
		byte[] pack_dukpt3 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("050AFFFFFFFFFFFFFFE000060016CB62659448AC8A721E28428CFDB2FD3001001600000000000000000000000000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt3), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("02")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:安装DUKPT密钥失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2:装载DUKPT密钥,传输密钥加密DUKPT密钥的方式，初始密钥16字节,应成功(8个15+8个17)
		byte[] pack_dukpt4 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("020AFFFFFFFFFFFFFFE000060016CB62659448AC8A721E28428CFDB2FD30010016CEB1BBCA24CFEADBE8FE4896E9D1228D"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt4), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:安装DUKPT密钥失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 校验返回报文的kcv值
		if((kcv = ISOUtils.hexString(retContent, 11,16)).equals("CEB1BBCA24CFEADBE8FE4896E9D1228D")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:检验kcv值错误(%s)", Tools.getLineInfo(),kcv);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
//		// case3:装载DUKPT密钥,主密钥加密DUKPT密钥的方式，初始密钥16字节,应成功(8个11+8个13)该方式不支持
//		byte[] pack_dukpt5 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("050AFFFFFFFFFFFFFFE000060016DFD7B9CEE61C1EFE3CAD8E1F32EB7EBE05001693DF5A71D2F88404B50A338221DFED04"));
//		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt5), null);
//		retContent = response.getResponse();
//		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
//		{
//			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:安装DUKPT密钥失败(%s)",Tools.getLineInfo(),retCode);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
//		// case4:装载DUKPT密钥,传输密钥加密DUKPT密钥的方式,初始密钥24字节，应成功(DUKPT密钥明文8个15+8个17+8个1B)该方式不支持
//		byte[] pack_dukpt6 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("020AFFFFFFFFFFFFFFE000060024CB62659448AC8A721E28428CFDB2FD30BFBF17A7D14D857E010016B11EF9A3E7D180205ECA8CFA96C7804E"));
//		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt6), null);
//		retContent = response.getResponse();
//		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
//		{
//			gui.cls_show_msg1_record(TAG, "sec9", gKeepTimeErr, "line %d:安装DUKPT密钥失败(%s)",Tools.getLineInfo(),retCode);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
		gui.cls_show_msg1_record(TAG, "sec9", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		k21ControllerManager.close();
	}

}
