package com.example.highplattest.ums;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.newland.ums.UmsApi;

/************************************************************************
 * 
 * module 			: 银商安全模块
 * file name 		: Ums3.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200813
 * directory 		: 
 * description 		: 测试isCameraDisable查询摄像头禁用状态
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200813	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums3 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String FILE_NAME = Ums3.class.getSimpleName();
	private final String TESTITEM = "isCameraDisable(银商)";
	private UmsApi umsApi;
	
	private Gui gui = new Gui(myactivity, handler);
	boolean mCameraStatus = true;
	String funcName = "testUms3";
	
	public void ums3()
	{
		try {
			testUms3();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums1", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
		}
	}
	
	public void testUms3()
	{
		umsApi = new UmsApi(myactivity);
		while(true)
		{
			int nkey = gui.cls_show_msg("%s\n0.启用或禁用摄像头，获取摄像头禁用状态\n1.启用摄像头重启后获取摄像头禁用状态\n2.禁用摄像头重启后获取摄像头禁用状态", TESTITEM);
			switch (nkey) {
			case '0':
				//case2.1：启用摄像头，获取摄像头禁用状态
				gui.cls_show_msg1(2, "case2.1：启用摄像头，获取摄像头禁用状态");
				//启用摄像头
				if(!umsApi.enableCamera()) 
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:启用摄像头失败(false)", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
				//查询摄像头状态
				if(umsApi.isCameraDisable())
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(true)", Tools.getLineInfo(), TESTITEM);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(2,"摄像头已启用");

				//case2.2：禁用摄像头，获取摄像头禁用状态
				gui.cls_show_msg1(2, "case2.2：禁用摄像头，获取摄像头禁用状态");
				//禁用摄像头
				if(!umsApi.disableCamera()) 
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:禁用摄像头失败(false)", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
				//查询摄像头状态
				if(!umsApi.isCameraDisable())
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s失败(false)", Tools.getLineInfo(), TESTITEM);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(2,"摄像头已禁用");
				break;
			case '1':
				//case4.1：启用摄像头重启后获取摄像头禁用状态
				gui.cls_show_msg1(2, "case4.1：启用摄像头重启后获取摄像头禁用状态");
				test_reboot("启用");
				break;
			case '2':
				//case4.2：禁用摄像头重启后获取摄像头禁用状态
				gui.cls_show_msg1(2, "case4.2：禁用摄像头重启后获取摄像头禁用状态");
				test_reboot("禁用");
				break;
	
			default:
				unitEnd();
				return;
			}
			gui.cls_show_msg1_record(FILE_NAME, funcName, gScreenTime,"%s测试通过", TESTITEM);
		}
	}
	
	public void test_reboot(String string) {
		if(gui.cls_show_msg("重启后再次进入此case测试。是否重启，是【确认】，否【其他】") == ENTER)
		{
			if (string.equals("启用")) {
				//启用摄像头
				if(!umsApi.enableCamera()) 
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:启用摄像头失败(false)", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			else if (string.equals("禁用")) {
				//禁用摄像头
				if(!umsApi.disableCamera())
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:禁用摄像头失败(false)", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			//重启
			Tools.reboot(myactivity);
		}	
		//查询摄像头状态
		if(umsApi.isCameraDisable())
			gui.cls_show_msg1(2,"摄像头已禁用");
		else
			gui.cls_show_msg1(2,"摄像头已启用");
			
	}
	
	@Override
	public void onTestUp() 
	{
		
	}
	@Override
	public void onTestDown() {
		gui = null;
		umsApi = null;
	}
}

