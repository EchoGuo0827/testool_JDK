package com.example.highplattest.ums;

import java.io.IOException;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.nlscan.SDL.SoftEngine;
import com.nlscan.SDL.SoftEngine.ScanningCallback;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.newland.AnalogSerialManager;
import android.newland.scan.ScanUtil.MyPreviewCallback;
import android.newland.ums.UmsApi;
import android.view.SurfaceHolder;

/************************************************************************
 * 
 * module 			: 银商安全模块
 * file name 		: Ums2.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200813
 * directory 		: 
 * description 		: 测试enableCamera启用摄像头
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200813	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums2 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String FILE_NAME = Ums2.class.getSimpleName();
	private final String TESTITEM = "enableCamera(银商)";
	private Gui gui = new Gui(myactivity, handler);
	private UmsApi umsApi;
	private Camera mCamera;
	
	public void ums2()
	{
		try {
			testUms2();
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(FILE_NAME, "ums1", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
		}
	}
	
	public void testUms2()
	{
		String funcName = "testUms2";
		umsApi = new UmsApi(myactivity);
		ScanDefineInfo mScanDefineInfo = getCameraInfo();
	
		//case3.1：启用摄像头后打开系统Camera，应正常打开
		gui.cls_show_msg1(3, "case3.1：启用摄像头后手动打开系统摄像头");
		if(!umsApi.enableCamera()) 
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请手动打开系统摄像头，正常打开【确认】，无法打开【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3.2：启用摄像头后使用代码方式打开摄像头，应正常打开
		gui.cls_show_msg1(3, "case3.2：启用摄像头后用代码打开摄像头");
		mCamera = Camera.open(mScanDefineInfo.getCameraId());
		if(mCamera == null)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		else
		{
			gui.cls_show_msg1(3, "开启摄像头后可以正常开启相机，case3.2测试通过");
			mCamera.release();
		}
		
		//case3.3：多次调用启用摄像头，打开摄像头，应正常打开
		gui.cls_show_msg1(3, "case3.3：多次启用摄像头后手动打开摄像头");
		if(!umsApi.enableCamera()) 
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请手动打开系统摄像头，正常打开【确认】，无法打开【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.1：从禁用摄像头状态到启用摄像头状态
		gui.cls_show_msg1(3, "case4.1：禁用摄像头后再启用摄像头");
		//禁用摄像头
		if(!umsApi.disableCamera())
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:禁用摄像头失败(false)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		//开启摄像头
		if(!umsApi.enableCamera())
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请手动打开系统摄像头，正常打开【确认】，无法打开【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}	

		//case4.2：摄像头启用，重启后打开摄像头
		if (gui.cls_show_msg("开启摄像头，重启后打开系统摄像头，是否重启？是【确认】，否【其他】") == ENTER)
			Tools.reboot(myactivity);
		else {
			if(gui.cls_show_msg("系统摄像头可以正常开启【确认】，无法开启【其他】") != ENTER)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case4.3：摄像头启用，使用扫码模块进行扫码操作
		if(gui.cls_show_msg("请在自检中扫码，可以正常扫码。是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.4：摄像头启用，休眠唤醒后查看摄像头状态
		if(gui.cls_show_msg("请将机器休眠，唤醒后手动打开系统摄像头，正常打开【确认】，无法打开【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(FILE_NAME, funcName, gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() 
	{
		
	}
	@Override
	public void onTestDown() {
		mCamera = null;
		gui = null;
		umsApi = null;
	}
}

