package com.example.highplattest.ums;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.hardware.Camera;
import android.newland.ums.UmsApi;

/************************************************************************
 * 
 * module 			: 银商安全模块
 * file name 		: Ums1.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200812
 * directory 		: 
 * description 		: 测试disableCamera禁用摄像头
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200812	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums1 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String FILE_NAME = Ums1.class.getSimpleName();
	private final String TESTITEM = "disableCamera(银商)";
	private Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	private Camera mCamera;
	String funcName = "testUms1";
		
	public void ums1()
	{
		try {
			testUms1();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums1", 0, "line %d:抛出异常(%s)，任意键继续", Tools.getLineInfo(), e.getMessage());
			test_continue();
		}
	}
	
	public void testUms1()
	{
		umsApi = new UmsApi(myactivity);
		ScanDefineInfo mScanDefineInfo = getCameraInfo();
	
		//case3.1：禁用摄像头后打开系统Camera，应无法打开
		gui.cls_show_msg1(2, "case3.1：禁用摄像头后手动打开系统摄像头");
		if(!umsApi.disableCamera()) 
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请手动打开系统摄像头，无法开启【确认】，可以开启【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3.2：禁用摄像头后使用代码方式打开摄像头，应无法打开
		gui.cls_show_msg1(2, "case3.2：禁用摄像头后用代码打开摄像头");
		mCamera = Camera.open(mScanDefineInfo.getCameraId());//禁用摄像头后调用open会抛异常，黄伟航解释：就是这样设计的，属于正常现象     20200908
		if(mCamera != null)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
			mCamera.release();
			if(!GlobalVariable.isContinue)
				return;
		}
	}
	
	public void test_continue() {
		gui.cls_show_msg1(3, "禁用摄像头后无法开启相机，case3.2测试通过");
		
		//case3.3：多次调用禁用摄像头，打开摄像头，应无法打开
		gui.cls_show_msg1(2, "case3.3：多次禁用摄像头后用手动打开摄像头");
		if(!umsApi.disableCamera()) 
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请手动打开系统摄像头，无法开启【确认】，可以开启【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.1：从启用摄像头状态到禁用摄像头状态
		gui.cls_show_msg1(1, "case4.1：启用摄像头后再禁用摄像头");
		//开启摄像头
		if(!umsApi.enableCamera())
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:开启摄像头失败(false)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		//禁用摄像头
		if(!umsApi.disableCamera())
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请手动打开系统摄像头，无法开启【确认】，可以开启【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}	

		//case4.2：摄像头被禁用，重启后打开摄像头
		if (gui.cls_show_msg("禁用摄像头，重启后打开系统摄像头，预期无法开启，是否重启？是【确认】，否【其他】") == ENTER)
			Tools.reboot(myactivity);
		else {
			if(gui.cls_show_msg("系统摄像头无法开启【确认】，可以开启【其他】") != ENTER)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case4.3：摄像头被禁用，使用扫码模块进行扫码操作
		if(gui.cls_show_msg("请在自检中扫码，应该无法扫码。是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.4：摄像头被禁用，休眠唤醒查看摄像头状态
		if(gui.cls_show_msg("请将机器休眠，唤醒后手动打开系统摄像头，无法开启【确认】，可以开启【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败", Tools.getLineInfo(), TESTITEM);
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

