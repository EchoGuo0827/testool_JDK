package com.example.highplattest.paymentport;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlManager;
/************************************************************************
 * 
 * module 			: Android系统和支付模块通信串口
 * file name 		: PaymentPort2.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141118 
 * directory 		: 
 * description 		: 测试Android系统和支付模块通信串口isValid，串口是否可用
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  huangjianb		20141118	 	created
 *
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort2 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private NlManager nlManager = null;
	String TESTITEM = "通信串口isValid";
	private final String CLASS_NAME = PaymentPort2.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void paymentport2()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName="paymentport2";
		/*private & local definition*/
		boolean ret = false;
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		
		/*process body*/
		//判断JNI通信是否可用
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		nlManager.disconnect();
		if ((ret = nlManager.isValid()) != true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,TESTITEM+"测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		nlManager = null;
		gui = null;
	}
}
