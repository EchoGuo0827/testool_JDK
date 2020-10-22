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
 * file name 		: sec5
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集工作密钥安装
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180517		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos13 extends UnitFragment
{
	private final String TESTITEM = "工作密钥安装(mpos)";
	public final String TAG = Mpos13.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos13()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		byte[] retContent;
		String kcv;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		/*process body*/
		// 测试前置：擦除所有密钥
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02020016A67E197C00818E9BA67E197C00818E9B"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case5:工作错误测试，密钥的长度比实际安装的密钥长度长，或未添加checkValue的校验值,服务不奔溃即可 add by 20190626 zhengxq
		byte[] tpk_no_checkValue = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202070016A69FC51F503CEDE6A69FC51F503CEDE601"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(tpk_no_checkValue), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("41")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 密钥长度和密钥值均错误的情况,服务不奔溃即可
		byte[] tpk_err_len1 = ISOUtils.hex2byte("0200341A052F30020000000067F7FF39FBFF3DFBFFEBF7FF6EF1FFD2F1FF2CF8FF64F3FF000342");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(tpk_err_len1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 长度指令规定是按照BCD码格式，直接用16进制格式，服务不奔溃即可
		byte[] tpk_err_len2 = ISOUtils.hex2byte("0200261A052F97020A0A000A91BA2BF865A8B2142AB13B897DD5EDF800038D");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(tpk_err_len2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.1:密钥校验值错误，应返回41
		byte[] pack_TPK_err = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202070016A69FC51F503CEDE6A69FC51F503CEDE600041111111101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK_err), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("41")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.2:工作密钥数据长度错误，应返回42
		byte[] pack_TPK_err2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("020207001669FC51F503CEDE6A69FC51F503CEDE600041111111101"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK_err2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("42")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.3:无安装任何密钥,读密钥记录错误,应返回47
		byte[] pack_kcv_err = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202070016A69FC51F503CEDE6A69FC51F503CEDE60008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_kcv_err), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("47")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:测试失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.1:明文安装TPK密钥并校验KCV,ID=7
		byte[] pack_TPK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202070016A69FC51F503CEDE6A69FC51F503CEDE600061DFB5845B5DD01"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TPK明文失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		byte[] pack_kcv = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202070016A69FC51F503CEDE6A69FC51F503CEDE60008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_kcv), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:计算工作密钥KCV失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((kcv=ISOUtils.hexString(retContent, 11, 4)).equals("1DFB5845")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:校验KCV失败(%s)",Tools.getLineInfo(),kcv);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2.2:密钥安装TPK密钥并校验KCV,ID=6
		byte[] pack_TPK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202060016B4C3887A6C6EA53DB4C3887A6C6EA53D000621F11CD0901800"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TPK密文失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		byte[] pack_kcv1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202060016A69FC51F503CEDE6A69FC51F503CEDE60008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_kcv1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:计算工作密钥KCV失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((kcv=ISOUtils.hexString(retContent, 11, 4)).equals("21F11CD0")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:校验KCV失败(%s)",Tools.getLineInfo(),kcv);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3.1:明文安装TDK密钥并校验KCV,ID=64
		byte[] pack_TDK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102400016793CA1EA4FC85CFB793CA1EA4FC85CFB0006EB5AC58A59C301"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TPK明文失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3.2:密文安装TDK密钥并校验KCV,ID=70
		byte[] pack_TDK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102460016578DAD0EE8AAF8B0578DAD0EE8AAF8B000066CB45B1D7BD8"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TPK密文失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case4.1:明文安装TAK密钥并校验KCV,ID=4
		byte[] pack_TAK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302040016477CD6842043FEC0477CD6842043FEC0000623B9839EFD3301"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TPK明文失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case4.2:密文安装TAK密钥并校验KCV,ID=2
		byte[] pack_TAK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302020016F5285F116A86E18EF5285F116A86E18E0006A8B7B5BD4A6700"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec5", gKeepTimeErr, "line %d:安装TPK密文失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(TAG, "sec5", gScreenTime, "%s测试通过", TESTITEM);
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
