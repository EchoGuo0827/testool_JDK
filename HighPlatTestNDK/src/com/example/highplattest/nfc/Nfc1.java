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
 * file name 		: Nfc1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160831 
 * directory 		: 
 * description 		: enableReaderMode(Activity activity, NfcAdapter.ReaderCallback callback, int flags, Bundle extras)
 * related document : 接入K21非接
 * history 		 	: author			date			remarks
 *			  		  zhengxq	   	 	20160831 	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Nfc1 extends UnitFragment
{
	private final String TESTITEM = "enableReaderMode";
	private Gui gui = new Gui(myactivity, handler);
	private NfcTool nfcTool =null;
	private String fileName="Nfc1";
	/**
	 * 具体的测试case
	 */
	public void nfc1()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		/*// case1.1：卡类型不匹配应失败：接入B卡，放置A卡应失败
		gui.cls_show_msg("请放置标准A卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		// case1.2:未上电成功就进行读数据操作，应失败
		try 
		{
			nfcTool.nfcRw(Nfc_Card.NFC_B);
			ret = NDK_ERR;
		} catch (Exception e) 
		{
			e.printStackTrace();
			ret = NDK_OK;
		}
		if(ret!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读数据测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2：超时时间过，未放置卡应返回失败，目前不支持：一直检测放卡
		gui.cls_show_msg("请移开射频卡,任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s上电测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3：放置标准B卡，取随机数应成功
		gui.cls_show_msg("请放置支持取随机数的标准B卡,任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s上电测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_B))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读数据测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读B卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();
		
		// case4：放置身份证，取随机数应成功
		gui.cls_show_msg("请放置身份证B卡,任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_B))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_B))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读B卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();
		
		/*// case5：放置标准A卡，取随机数应成功
		gui.cls_show_msg("请放置标准A卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_NFC_A))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_A))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读A卡数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		nfcTool.nfcDisEnableMode();*/
		
		/*// case6：放置1K的M1卡，认证读写应成功
		gui.cls_show_msg("请放置1K的M1卡，任意键继续");
		if((ret = nfcTool.nfcConnect(NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s上电测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		try {
			if((ret = nfcTool.nfcRw(Nfc_Card.NFC_M1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读数据测试失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName,"nfc1",gKeepTimeErr,"line %d:%s读写M1数据异常抛出", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		gui.cls_show_msg1_record(fileName,"nfc1",gScreenTime,"%s测试通过", TESTITEM);
		gui = null;
	}
	@Override
	public void onTestUp() {
		 nfcTool = new NfcTool(myactivity);
	}
	@Override
	public void onTestDown() 
	{
		// 测试后置
		nfcTool.nfcDisEnableMode();
	}
}
