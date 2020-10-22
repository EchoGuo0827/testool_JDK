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
 * file name 		: sec8
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180518
 * directory 		: 
 * description 		: mpos指令集数据加密/解密(mpos)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180518		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos16 extends UnitFragment{
	private final String TESTITEM = "数据加密/解密(mpos)";
	public final String TAG = Mpos16.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos16()
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
		byte[] retContent;
		String encrypt;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 测试前置:擦除所有密钥
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		// 安装TMK,ID=5,密钥明文16个11
		// 安装TMK,ID=5
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02050016253C9D9D7C2FBBFA253C9D9D7C2FBBFAff000482E13665"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 明文安装TDK1(8字节),ID=1,密钥明文16个13
		byte[] pack_TDK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0105010016131313131313131313131313131313130004A8B7B5BD01"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:安装TDK1失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// TMK发散,安装TDK2(16字节),ID=2,密钥明文15151515151515151717171717171717
		byte[] pack_TDK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0105020016145F5C6E3D91445738BEDB24A6D3801800045244255000"));
//		byte[] pack_TDK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("01050200161515151515151515171717171717171700045244255001"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:安装TDK2失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 明文安装TDK3(24字节),ID=3,密钥明文171717171717171719191919191919191B1B1B1B1B1B1B1B
		byte[] pack_TDK3 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0105030024171717171717171719191919191919191B1B1B1B1B1B1B1B000428C43B2101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK3), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:安装TDK3失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 明文安装TAK,ID=4,密钥明文19191919191919191B1B1B1B1B1B1B1B
		byte[] pack_TAK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("030504001619191919191919191B1B1B1B1B1B1B1B0004056AE3BD01"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 明文安装TPK,ID=8,16个1B
		byte[] pack_TPK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("02050800161B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B00047EED30F701"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:安装TPK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.1:ucMod非法(170)
		byte[] pack_calc1 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("01AA00104246323031353039303400000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("02")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.2:ucKeyIdx不存在(10)
		byte[] pack_calc2 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("0A0100104246323031353039303400000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("02")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2:以SEC_DES_ENCRYPT|SEC_DES_KEYLEN_8|SEC_DES_ECBMODE模式，8字节数据输入，TDK1
		byte[] pack_calc3 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("01020008202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc3), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 8)).equals("38774893F55FF4C9")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case3:以SEC_DES_ENCRYPT|SEC_DES_KEYLEN_16|SEC_DES_ECBMODE模式，16字节数据输入，TDK1
		byte[] pack_calc4 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("010200162020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc4), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 16)).equals("38774893F55FF4C938774893F55FF4C9")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case4:以SEC_DES_ENCRYPT|SEC_DES_KEYLEN_24|SEC_DES_ECBMODE模式，24字节数据输入，TDK1
		byte[] pack_calc5 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("0102002420202020202020202020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc5), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 24)).equals("38774893F55FF4C938774893F55FF4C938774893F55FF4C9")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case5:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_8|SEC_DES_CBCMODE模式，8字节数据输入，TDK2
		byte[] pack_calc6 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("02030008202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc6), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 8)).equals("1F90C6C72BBDCDA6")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case6:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_16|SEC_DES_CBCMODE模式，16字节数据输入，TDK2
		byte[] pack_calc7 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("020300162020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc7), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 16)).equals("1F90C6C72BBDCDA63EB1E7E60A9CEC87")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case8:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_24|SEC_DES_CBCMODE模式，24字节数据输入，TDK2
		byte[] pack_calc9 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("0203002420202020202020202020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc9), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 24)).equals("1F90C6C72BBDCDA63EB1E7E60A9CEC873EB1E7E60A9CEC87")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case9:以SEC_DES_ENCRYPT|SEC_DES_KEYLEN_8|SEC_DES_ECBMODE模式，8字节数据输入，TDK3
		byte[] pack_calc10 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("03020008202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc10), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 8)).equals("51BAAFE808FA2737")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case10:以SEC_DES_ENCRYPT|SEC_DES_KEYLEN_16|SEC_DES_ECBMODE模式，16字节输入数据，TDK3
		byte[] pack_calc11 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("030200162020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc11), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 16)).equals("51BAAFE808FA273751BAAFE808FA2737")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case11:以SEC_DES_ENCRYPT|SEC_DES_KEYLEN_24|SEC_DES_ECBMODE模式。24字节数据输入，TDK3
		byte[] pack_calc12 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("0302002420202020202020202020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc12), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 24)).equals("51BAAFE808FA273751BAAFE808FA273751BAAFE808FA2737")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case12:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_DEFAULT|SEC_DES_CBCMODE模式，ucKeyIdx = TAK's id,uckey=TAK,应成功
		byte[] pack_calc13 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("040300162020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc13), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			
			if((encrypt = ISOUtils.hexString(retContent, 14, 16)).equals("3EFB7D9CEAD9D41D1FDA5CBDCBF8F53C")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,encrypt);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case13:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_DEFAULT|SEC_DES_CBCMODE模式，ucKeyIdx = TPK's id,ucKey = TPK，应成功
		byte[] pack_calc14 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("080300162020202020202020202020202020202000000101010101010101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc14), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("02")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec8", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case14:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_DEFAULT|SEC_DES_CBCMODE模式，ucKeyIdx = RSA's id,ucKeyType = TDK，应失败
		// case15:以SEC_DES_DECRYPT|SEC_DES_KEYLEN_DEFAULT|SEC_DES_CBCMODE模式，ucKeyIdx = dukpt's id,ucKeyType = TDK，应失败
		gui.cls_show_msg1_record(TAG, "sec8", gScreenTime, "%s测试通过",TESTITEM);
	}
	
	@Override
	public void onTestUp() 
	{
		
	}
	@Override
	public void onTestDown() 
	{
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		k21ControllerManager.close();
	}
}
