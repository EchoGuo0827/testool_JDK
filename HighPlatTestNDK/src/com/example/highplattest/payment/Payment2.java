package com.example.highplattest.payment;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;

import android.newland.os.NlRecovery;
/************************************************************************
 * 
 * module 			: Android更新支付证书
 * file name 		: payment2.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20160302 
 * directory 		: 
 * description 		: 恢复出厂设置接口(农行版本支持)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj		   20160302	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Payment2 extends UnitFragment	
{
	private final String TESTITEM = "恢复出厂设置接口(农行版本)";
	Gui gui = new Gui(myactivity, handler);
	private String fileName="Payment2";
	public void payment2() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"payment2",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/* private & local definition */
		NlRecovery nlRecovery=new NlRecovery(myactivity);
		/* Process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
	
		
		if(!nlRecovery.canRecovery())
		{
			gui.cls_show_msg("当前终端不支持恢复出产设置功能，点任意键后请退出用例");
			return;
		}
		
		if(gui.ShowMessageBox("是否立即恢复出厂设置".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			nlRecovery.recovery();
		}
	
		
		//gui.cls_show_msg1(2, SERIAL,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
	}
}
