package com.example.highplattest.nfc;

import android.nfc.NfcAdapter;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * file name 		: Nfc6.java 
 * directory 		: 
 * description 		: 复现客诉：nfc未放置后再放置无法读卡，实际应该能读卡
 * related document : 
 * history 		 	: 变更记录									变更时间			变更人员
 * 					  复现客诉：nfc未放置后再放置无法读卡，实际应该能读卡		20200110		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class Nfc6 extends UnitFragment  {
	private final String TESTITEM = "复现客诉：nfc未放置后再放置无法读卡，实际应该能读卡";
	private Gui gui = new Gui(myactivity, handler);
	private NfcTool nfcTool =null;
	private String fileName="Nfc6";

	public void nfc6()
	{
		int ret = -1;
		String funcName = "nfc6";

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		//case1: 上电后长时间不放置卡，再正常放置卡 应正常调用回调
		try 
		{
			nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B);
		}catch(Exception e) {
			e.printStackTrace();
		}

	
		for (int i = 0; i < 15; i++) {
			gui.cls_show_msg1(1, "请不要放置身份证或其他射频卡片..............还有%ds", 15 - i);
		}
		nfcTool.nfcDisEnableMode();
		gui.cls_show_msg("请放置身份证.............任意键继续");

		if ((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		try {
			if ((ret = nfcTool.nfcRw(Nfc_Card.NFC_B)) != NDK_OK) {
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s读B卡数据异常抛出", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();
			
		gui.cls_show_msg("读取身份证成功。请移开身份证.............任意键继续");
		// case2: 快速放置身份证后马上移开再放置身份证 应正常调用回调

		gui.cls_show_msg("请快速放置身份证后马上移开............任意键后开始");
		if ((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B)) != NDK_OK) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		try {
			if ((ret = nfcTool.nfcRw(Nfc_Card.NFC_B)) != NDK_OK) {
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s读B卡数据异常抛出", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();

		gui.cls_show_msg("请再正常放置身份证............任意键继续");
		if ((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B)) != NDK_OK) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		try {
			if ((ret = nfcTool.nfcRw(Nfc_Card.NFC_B)) != NDK_OK) {
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s读B卡数据异常抛出", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();

		gui.cls_show_msg1_record(fileName, funcName, 5, "测试通过------");
	}
	@Override
	public void onTestUp() {
		 nfcTool = new NfcTool(myactivity);
		
	}

	@Override
	public void onTestDown() {
		nfcTool.nfcDisEnableMode();
		
	}

}
