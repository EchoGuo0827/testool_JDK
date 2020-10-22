package com.example.highplattest.nfc;

import android.annotation.SuppressLint;
import android.nfc.NfcAdapter;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: NFC模块
 * file name 		: Nfc2.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 20160830 
 * directory 		: 
 * description 		: isEnabled()
 * related document : 是否支持NFC
 * history 		 	: author			date			remarks
 *			  		  		   	 		
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Nfc2 extends UnitFragment
{
	private final String TESTITEM = "isEnabled";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName="Nfc2";
	public void nfc2() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
					
		/*private & local definition*/
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(myactivity);
		boolean ret = false;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// case1:isEnabled始终返回true
		if(!(ret = nfcAdapter.isEnabled()))
		{
			gui.cls_show_msg1_record(fileName,"nfc2",gScreenTime,"line %d:%s不支持NFC(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(!(ret = nfcAdapter.isEnabled()))
		{
			gui.cls_show_msg1_record(fileName,"nfc2",gKeepTimeErr,"line %d:%s不支持NFC(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"nfc2",gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
