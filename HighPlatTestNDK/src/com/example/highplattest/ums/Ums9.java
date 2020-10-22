package com.example.highplattest.ums;

import android.newland.ums.UmsApi;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums9.java 
 * history 		 	:          变更点				                                        变更时间	    	变更人员
 * 					 获取当前SElinux的状态getSELinuxStatus()	   	  20200813	 		郑佳雯
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums9 extends UnitFragment
{
	private final String TESTITEM = "getSELinuxStatus()(银商)";
	public final String FILE_NAME = Ums9.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums9()
	{
		try {
			testUms9();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums9", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
	}
	
	public void testUms9()
	{
		boolean iRet=false;
		String funcName = "testUms9";
		//彭方禄:对于selinux开关测试的问题，由于ChinaUms正式版是没有root权限的，无法更改selinux状态。直接测试selinux开启即可
		// case3.1:使用shell命令 [setenforce 1]构造SELinux关闭，获取SELinux状态,返回false
	/*	gui.cls_show_msg("case3.1:使用shell命令 [setenforce 1]构造SELinux关闭,完成按任意键继续...");
		if((iRet = umsApi.getSELinuxStatus())!=false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
	*/
		// case3.2:使用shell命令 [setenforce 0]构造SELinux开启，获取SELinux状态,返回true
		gui.cls_show_msg("case3.2:使用shell命令 [setenforce 0]构造SELinux开启,完成按任意键继续...");
		if((iRet = umsApi.getSELinuxStatus())!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
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
