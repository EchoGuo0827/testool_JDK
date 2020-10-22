package com.example.highplattest.systemversion;

import java.util.HashMap;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: F7摄像头信息获取
 * file name 		: SystemVersion13.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20171102
 * directory 		: 
 * description 		: F7摄像头信息获取
 * history 		 	: author			date			remarks
 *
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class SystemVersion13 extends UnitFragment {
	
	//F7使用
	
	private final String TESTITEM = "F7摄像头信息获取";
	private String fileName=SystemVersion13.class.getSimpleName();
	private Gui gui;
	public void systemversion13()
	{
		 gui = new Gui(myactivity, handler);
		UsbManager mUsbManager = (UsbManager) myactivity.getSystemService(Context.USB_SERVICE);
		

		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        for (UsbDevice usbDevice : deviceList.values()) {
            if (usbDevice.getDeviceClass() == 239 &&
                    usbDevice.getDeviceSubclass() == 2) {
            	Log.d("eric_chen", "getProductName=="+usbDevice.getProductName());
               gui.cls_show_msg("当前摄像头信息为%s(人脸识别可能会有多个摄像头--------)", usbDevice.getProductName());
            }
        }
		
        gui.cls_show_msg1_record(fileName, "SystemVersion13", 5,"测试通过-------------");
	}
	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
