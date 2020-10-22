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
 * file name 		: Nfc4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170109 
 * directory 		: 
 * description 		: disableReaderMode()
 * related document : NFC下电操作
 * history 		 	: author			date			remarks
 *			  		  zhengxq	   	 	20160831 	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Nfc4 extends UnitFragment
{
	private final String TESTITEM = "disableReaderMode";
	private Gui gui = new Gui(myactivity, handler);
	private NfcTool nfcTool=null;
	private String fileName="Nfc4";
	public void nfc4()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"nfc4",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		int ret = -1;
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(myactivity);
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		// case1:正常流程，上电读写，下电操作
		gui.cls_show_msg("请放置支持取随机数的标准B卡，任意键继续");
		ex_nfc_card = Nfc_Card.NFC_B;
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%snfc连接测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		try 
		{
			ret = nfcTool.nfcRw(ex_nfc_card);
			if(ret !=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%snfc读写测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%s读写异常抛出", Tools.getLineInfo(),TESTITEM);
			return;
		}
		nfcTool.nfcDisEnableMode();
		// case2:异常流程，未下电重复上电会应成功
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%snfc连接测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%snfc连接测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			ret = nfcTool.nfcRw(ex_nfc_card);
			if(ret !=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%snfc读写测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%s读写异常抛出", Tools.getLineInfo(),TESTITEM);
			nfcTool.nfcDisEnableMode();
			return;
		}
		// case3：异常流程，下电后未上电进行读写操作应失败
//		马鑫汶回复：流程优化成未上电抛出系统异常transceive failed ；zhangxinju修改于2019/4/24
		nfcAdapter.disableReaderMode(myactivity);
		try {
			ret = nfcTool.nfcRw(ex_nfc_card);
			if(ret == NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%snfc读写测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
//			gui.cls_show_msg1_record(fileName,"nfc4",gKeepTimeErr,"line %d:%s读写异常抛出", Tools.getLineInfo(),TESTITEM);
//			if(!GlobalVariable.isContinue)
//				return;
		}
		gui.cls_show_msg1_record(fileName,"nfc4",gScreenTime,"%s测试通过", TESTITEM);
		gui = null;
	}

	@Override
	public void onTestUp() 
	{
		 nfcTool = new NfcTool(myactivity);
	}

	@Override
	public void onTestDown() 
	{
		nfcTool.nfcDisEnableMode();
	}
}
