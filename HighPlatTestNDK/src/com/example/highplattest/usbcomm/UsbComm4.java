package com.example.highplattest.usbcomm;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: usb模拟串口模块 
 * file name 		: UsbComm4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150619
 * directory 		: 
 * description 		: usb模拟串口模块的getVersion
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150619	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm4 extends UnitFragment 
{
	private final String CLASS_NAME = UsbComm4.class.getSimpleName();
	private AnalogSerialManager analogSerialManager = null;
	private final String TESTITEM = "虚拟串口getVersion";
	private Gui gui = new Gui(myactivity, handler);
	public void usbcomm4() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			return;
		
		/*private & local definition*/
		
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 获取JIN的版本信息，判断获取到的版本号是否正确
			
//		String message = "请确认获取的JNI版本信息="+analogSerialManager.getVersion()+" 是否正确";
		String version = analogSerialManager.getVersion();
		
		gui.cls_show_msg1_record(CLASS_NAME,"usbcomm4",gScreenTime,"%s测试通过(ver = %s)", TESTITEM,version);
	}
	@Override
	public void onTestUp() 
	{
		analogSerialManager = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
	}
	@Override
	public void onTestDown() 
	{
		if(analogSerialManager!=null)
			analogSerialManager.close();
		gui = null;
		analogSerialManager = null;
	} 
	
}
