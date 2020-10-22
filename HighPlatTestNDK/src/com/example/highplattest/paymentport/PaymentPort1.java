package com.example.highplattest.paymentport;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import android.newland.NlManager;
/************************************************************************
 * 
 * module 			: Android系统和支付模块通信串口
 * file name 		: PaymentPort1.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141118 
 * directory 		: 
 * description 		: 测试Android系统和支付模块通信串口的getVersion，获取版本号
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  huangjianb		20141118	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort1 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = PaymentPort1.class.getSimpleName();
	private NlManager nlManager = null;
	String TESTITEM = "通信串口getVersion";
	private Gui gui = new Gui(myactivity, handler);
	
	public void paymentport1()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName="paymentport1";
		//实例化接口对象
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		/* private & local definition */
		String version;
		gui.cls_show_msg1(gScreenTime,"%s测试中...",TESTITEM);
		
		/* process body */
		//获取JNI版本信息
		version = nlManager.getVersion();
		gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"%s测试通过(ver=%s)", TESTITEM,version);
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
