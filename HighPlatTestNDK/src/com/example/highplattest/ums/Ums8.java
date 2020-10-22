package com.example.highplattest.ums;

import android.newland.ums.UmsApi;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums8.java 
 * history 		 	:          变更点								                     变更时间	    	变更人员
 * 					 查询security-boot的状态getSecurityBootStatus()	   	  20200813	 		郑佳雯
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums8 extends UnitFragment
{
	private final String TESTITEM = "getSecurityBootStatus()(银商)";
	public final String FILE_NAME = Ums8.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums8()
	{
		try {
			testUms8();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums8", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
	}
	
	public void testUms8()
	{
		int iRet = -1; 
		String funcName = "testUms8";
		
		// case3.1:构造Security-Boot功能关闭，获取Security-Boot状态
		gui.cls_show_msg1(1, "case3.1:获取Security-Boot状态");
		if((iRet = umsApi.getSecurityBootStatus())==0)
		{
			gui.cls_show_msg( "【错误值，不能发布】");
		}
		else if((iRet = umsApi.getSecurityBootStatus())==1)
		{
			gui.cls_show_msg( "【默认值，正常固件】");		
		}
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
		 
	}

	@Override
	public void onTestUp() {
		umsApi = new UmsApi(myactivity);
	}

	@Override
	public void onTestDown() {
	}

}
