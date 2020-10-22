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
 * file name 		: sec7
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180518
 * directory 		: 
 * description 		: mpos指令集MAC计算
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180518		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos15 extends UnitFragment
{
	private final String TESTITEM = "MAC计算(mpos)";
	public final String TAG = Mpos15.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos15()
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
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 删除所有密钥
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		// 安装TMK,ID=5
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02050016253C9D9D7C2FBBFADC2CC554AFF50A25ff000499BAB91E"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 安装TAK1(密钥8字节),ID=1,密文安装
		byte[] pack_TAK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0305010008E8AE27AFB890D5C30004265A7ABF00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 安装TAK2(密钥16字节),ID=2
		byte[] pack_TAK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("030502001617171717171717171919191919191919000454BA082201"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 安装TPK,ID=3
		byte[] pack_TPK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("02050300161919191919191919191919191919191900049DA493AA01"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 安装TAK3(24字节),ID=4
		byte[] pack_TAK3 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("03050400242121212121212121232323232323232325252525252525250004B9A32C4501"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK3), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.1:ucMod非法
		byte[] pack_MAC1=CalDataLrc.mposPack(new byte[]{0x1A,0x04}, ISOUtils.hex2byte("01000A030008010203040506070800000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("02")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.2:ucKeyIdx不存在
		byte[] pack_MAC2=CalDataLrc.mposPack(new byte[]{0x1A,0x04}, ISOUtils.hex2byte("AA0001030008010203040506070800000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("02")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:以SEC_MAC_X99模式,7字节数据输入,TAK1
		byte[] pack_MAC3 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("0100000300072020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC3), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			String mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("BC99606ABA33CC76")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		// case3:以SEC_MAC_X99模式,8字节数据输入,TAK2
		byte[] pack_MAC4 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("020000030008202020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC4), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("E8786990BB5192D2")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		// case4:以SEC_MAC_X919模式,13字节数据输入,TAK1
		byte[] pack_MAC5 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("0100010300132020202020202020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC5), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("B59F08A30E6A9345")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		// case5:以SEC_MAC_X919模式,16字节数据输入,TAK3
		byte[] pack_MAC6 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("0400010300162020202020202020202020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC6), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("269C3BECB2DAB182")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case6:以SEC_MAC_ECB模式,17字节数据输入,TAK1
		byte[] pack_MAC7 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("010002030017202020202020202020202020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC7), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("4636383430453532")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case7:以SEC_MAC_X9606模式,21字节数据输入,TAK1
		byte[] pack_MAC8 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("01000303002120202020202020202020202020202020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC8), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("50062F419900C431")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case8:以SEC_MAC_X9606模式,23字节数据输入,TAK3
		byte[] pack_MAC9 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("040003030023202020202020202020202020202020202020202020202000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC9), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			mac = ISOUtils.hexString(retContent, 12, 8);
			if(mac.equals("033474F4856C903F")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec7", gKeepTimeErr, "line %d:%s测试失败(mac=%s)",Tools.getLineInfo(),TESTITEM,mac);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		gui.cls_show_msg1_record(TAG, "sec7", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		k21ControllerManager.close();
	}

}
