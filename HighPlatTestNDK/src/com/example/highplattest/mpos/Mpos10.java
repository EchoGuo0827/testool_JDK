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
/************************************************************************
 * 
 * module 			: mpos指令集非接模块
 * file name 		: card23
 * Author 			: xuess 
 * version 			: 
 * DATE 			: 20180521
 * directory 		: 
 * description 		: mpos指令集非接M1卡操作指令,指令E205、E206、E207、E208、E209、
 * 					  E20A、E20B、E20C、E210、E211、E212、E213
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		  		20180521		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos10 extends UnitFragment {
	private final String TESTITEM = "非接M1卡操作指令(mpos)";
	private String fileName=Mpos10.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private Gui gui = new Gui(myactivity, handler);
	private byte[] retContent = new byte[1024];
	private byte[] pack;
	private String uidStr;//序列号
	
	public static byte[] rfidActivate = ISOUtils.hex2byte("020004e2132f0903d0");
	public static byte[] m1KeyStoreBuf = ISOUtils.hex2byte("020012e2052f1f6001ffffffffffff03a7");//Akey,块号01,ffffffffffff
	public static byte[] m1KeyLoadBuf = ISOUtils.hex2byte("020006e2062f216001038e");
	public static byte[] m1IncrementBuf = ISOUtils.hex2byte("020009e20b2f5f01010000000393");
	public static byte[] m1DecrementBuf = ISOUtils.hex2byte("020009e20c2f61010100000003aa");
	public static byte[] rfidPowerUpBuf_M1Card = ISOUtils.hex2byte("020007e2012f090c000a03c7");
	
	private String BLK02DATA_ORI = "6745230198badcfe6745230102fd02fd";
	private String BLK02DATA_INC1 = "6845230197badcfe6845230102fd02fd";
	
	public void mpos10(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		
		int ret = -1, len = 0;
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		//case1：M1卡标准三步寻卡
		gui.cls_show_msg("请在感应区放置一张Mifare-1卡,任意键继续");
		//E210寻卡指令的返回存在问题,待与开发确认
		//M1卡寻卡
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E210,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡寻卡测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//获取序列号长度
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		uidStr = ISOUtils.hexString(retContent, 12, len);
		//防冲突
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E211,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡防冲突测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//获取响应信息中序列号长度
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		//与寻卡操作的序列号进行校验
		if(ISOUtils.hexString(retContent, 11, len).equals(uidStr)==false)
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:M1卡防冲突响应信息校验不一致(%s,%s)", Tools.getLineInfo(),ISOUtils.hexString(retContent, 11, len),uidStr);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//选卡
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x12},ISOUtils.hex2byte(ISOUtils.hexString(retContent, 9, 2)+uidStr));
		if((ret = sendMposCmd(k21ControllerManager,pack,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡选卡测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case2：使用外部密钥认证,并进行读写、增减量操作
		//外部密钥认证
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x08},ISOUtils.hex2byte("60"+uidStr+"01ffffffffffff"));
		if((ret = sendMposCmd(k21ControllerManager,pack,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡外部密钥认证失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//写01块
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E20a,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡写块数据失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//读01块
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E209,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡读块数据失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.hexString(retContent, 9, 16).equals(BLK02DATA_ORI))
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:M1卡读块数据校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(retContent, 9, 16));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//增量操作
		if((ret = sendMposCmd(k21ControllerManager,m1IncrementBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡增量操作失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E209,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡读块数据失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.hexString(retContent, 9, 16).equals(BLK02DATA_INC1))
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:M1卡读块数据校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(retContent, 9, 16));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//减量操作
		if((ret = sendMposCmd(k21ControllerManager,m1DecrementBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡增量操作失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E209,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡读块数据失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.hexString(retContent, 9, 16).equals(BLK02DATA_ORI))
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:M1卡读块数据校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(retContent, 9, 16));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.rfcardPowerOffBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case3:使用加载的密钥认证,并读写
		//M1卡寻卡上电
		if((ret = sendMposCmd(k21ControllerManager,rfidPowerUpBuf_M1Card,retContent))!=SDK_OK|| ISOUtils.hexString(retContent, 16, 2).equals("0400")==false){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡上电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//取寻到的m1卡序列号
		uidStr = ISOUtils.hexString(retContent, 12, 4);
		//存储密钥
		if((ret = sendMposCmd(k21ControllerManager,m1KeyStoreBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡存储密钥失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//加载密钥
		if((ret = sendMposCmd(k21ControllerManager,m1KeyLoadBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡加载密钥失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//使用加载的密钥认证
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x07},ISOUtils.hex2byte("60"+uidStr+"01"));
		if((ret = sendMposCmd(k21ControllerManager,pack,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡加载密钥认证失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//写01块
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E20a,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡写块数据失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//读01块
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.Rfid_E209,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:M1卡读块数据失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.hexString(retContent, 9, 16).equals(BLK02DATA_ORI))
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:M1卡读块数据校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(retContent, 9, 16));
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.rfcardPowerOffBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		//case4：A卡寻卡-激活两步
		gui.cls_show_msg("请在感应区放置1张标准A卡,任意键继续");
		//A卡寻卡
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x10},ISOUtils.hex2byte("0a000a"));
		if((ret = sendMposCmd(k21ControllerManager,pack,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:A卡寻卡测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//获取序列号长度
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		uidStr = ISOUtils.hexString(retContent, 12, len);
		//激活
		if((ret = sendMposCmd(k21ControllerManager,rfidActivate,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:A卡激活测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//校验卡类型和序列号
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		if(ISOUtils.hexString(retContent, 9, 1).equals("0A")==false || ISOUtils.hexString(retContent, 12, len).equals(uidStr)==false)
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:A卡激活响应信息校验失败(%s,%s,%s)", Tools.getLineInfo(),
					ISOUtils.hexString(retContent, 9, 1),ISOUtils.hexString(retContent, 12, len),uidStr);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.rfcardPowerOffBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}	
		
		//case5：B卡寻卡-激活两步
		gui.cls_show_msg("请在感应区放置1张标准B卡,任意键继续");
		//B卡寻卡
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x10},ISOUtils.hex2byte("0b000a"));
		if((ret = sendMposCmd(k21ControllerManager,pack,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:B卡寻卡测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//获取序列号长度
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		uidStr = ISOUtils.hexString(retContent, 12, len);
		//激活
		if((ret = sendMposCmd(k21ControllerManager,rfidActivate,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:B卡激活测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//校验卡类型和序列号
		try {
		    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		if(ISOUtils.hexString(retContent, 9, 1).equals("0B")==false || ISOUtils.hexString(retContent, 12, len).equals(uidStr)==false)
		{
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr,"line %d:B卡激活响应信息校验失败(%s,%s,%s)", Tools.getLineInfo(),
					ISOUtils.hexString(retContent, 9, 1),ISOUtils.hexString(retContent, 12, len),uidStr);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//下电
		if((ret = sendMposCmd(k21ControllerManager,DataConstant.rfcardPowerOffBuf,retContent))!=SDK_OK){
			gui.cls_show_msg1_record(fileName, "card22", gKeepTimeErr, "line %d:非接下电测试失败(%d)", Tools.getLineInfo(),ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "card22", gScreenTime,"%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		sendMposCmd(k21ControllerManager,DataConstant.Rfid_E202,retContent);
		k21ControllerManager.close();
	}
}
