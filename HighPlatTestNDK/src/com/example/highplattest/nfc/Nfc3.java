package com.example.highplattest.nfc;

import android.annotation.SuppressLint;
import android.nfc.NfcAdapter;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: NFC模块
 * file name 		: Nfc3.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 20160830 
 * directory 		: 
 * description 		: connect()
 * related document : 连接射频卡
 * history 		 	: author			date			remarks
 *			  		  		   	 		
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Nfc3 extends UnitFragment
{
	private final String TESTITEM = "connect";
	private Gui gui = new Gui(myactivity, handler);
	private NfcTool nfcTool=null;
	private String fileName="Nfc3";
	public void nfc3()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		
		int ret = -1;
		
		/*process body*/
		// case1:未连接就进行发指令操作，应返回失败
		gui.cls_show_msg("请放置标准B卡，任意键继续");
		try {
			nfcTool.nfcRw(Nfc_Card.NFC_B);
			ret = NDK_ERR;
		} catch (Exception e) 
		{
			ret = NDK_OK;
		}
		if(ret !=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s异常测试失败（%d）", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		/*// case2:A卡，connect，发随机数指令
		gui.cls_show_msg("请放置支持取随机数的标准A卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_A))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_A))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读A卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();*/
		
		// case3:B卡，connect，发随机数指令
		gui.cls_show_msg("请放置支持取随机数的标准B卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_B))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读B卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();
		
		/*// case4:1K的M1卡，connect，读写块操作
		gui.cls_show_msg("请放置1K的M1卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_M1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读M1卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();*/
		
		/*// case5:4K的M1卡，connect，读写块操作
		gui.cls_show_msg("请放置4K的M1卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_M1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc3",gKeepTimeErr,"line %d:%s读M1卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		
		gui.cls_show_msg1_record(fileName,"nfc3",gScreenTime,"%s测试通过",TESTITEM);
		gui = null;
	}
	@Override
	public void onTestUp() {
		nfcTool = new NfcTool(myactivity);
	}
	@Override
	public void onTestDown() {
		// 测试后置
		nfcTool.nfcDisEnableMode();
	}
	
}
