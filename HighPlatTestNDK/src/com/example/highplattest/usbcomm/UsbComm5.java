package com.example.highplattest.usbcomm;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Usb虚拟串口模块
 * file name 		: UsbComm5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150619
 * directory 		: 
 * description 		: usb虚拟串口模块的isValid
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150619	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm5 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = UsbComm5.class.getSimpleName();
	private AnalogSerialManager analogSerialManager = null;
	private final String TESTITEM = "外置串口isValid";
	private Gui gui = new Gui(myactivity, handler);
	
	public void usbcomm5() 
	{
		String funcName="usbcomm5";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			return;
		/*private & local definition*/
		boolean ret;
		int ret1,fd=-1;
		
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 关闭串口
		analogSerialManager.close();
		
		// case1:串口没有打开的时候，设备不可用
		if ((ret = analogSerialManager.isValid()) == true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		} 
		
		// case2:打开串口，设备可用
		if((fd = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,fd);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret=analogSerialManager.isValid()) == false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 测试后置
		if((ret1 = analogSerialManager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() {
		analogSerialManager =  (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
	}
	@Override
	public void onTestDown() {
		if(analogSerialManager!=null)
			analogSerialManager.close();
		gui = null;
		analogSerialManager = null;
		
	}

}
