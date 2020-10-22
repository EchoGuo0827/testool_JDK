package com.example.highplattest.testpermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.service.FloatingButtonService;
/************************************************************************
 * 
 * module 			: Android权限测试
 * file name 		: TestPermission1.java 
 * history 		 	: 变更记录										变更时间			变更人员
 * 					  F7上第三方应用可以获取SYSTEM_ALERT_WINDOW权限		20200526		郑薛晴
 * 														   	 			
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class TestPermission1 extends UnitFragment
{
	public final String TAG = TestPermission1.class.getSimpleName();
	private String TESTITEM = "SYSTEM_ALERT_WINDOW和INTERNAL_SYSTEM_WINDOW权限";
	Gui gui = new Gui(myactivity, handler);
	
	@SuppressLint("NewApi") public void testpermission1()
	{
		String funcName="testpermission1";
		
		// case1:第三方应用申请权限操作
		if(myactivity.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED)
		{
			gui.cls_show_msg1(1, "获取%s成功", "SYSTEM_ALERT_WINDOW");
		}
		else
		{
			myactivity.requestPermissions(new String[] {Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);
			if(myactivity.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED)
			{
				gui.cls_show_msg1(1, "获取%s成功", "SYSTEM_ALERT_WINDOW");
			}
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:动态获取SYSTEM_ALERT_WINDOW权限失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		
		// case2:第三方应用使用该权限进行操作,N920 MuPay只支持android.permission.SYSTEM_ALERT_WINDOW权限
		if(GlobalVariable.currentPlatform!=Model_Type.N920_A7)
		{
			// 点击【按键1】操作开启悬浮窗
			if(gui.cls_show_msg("点击【按键1】可开启悬浮窗,其他按键退出测试")=='1')
			{
		        if (FloatingButtonService.isStarted) {
		        	gui.cls_printf("悬浮窗已经开启，不需再次开启".getBytes());
		            return;
		        }
		        myactivity.startService(new Intent(myactivity, FloatingButtonService.class));
			}
			if(gui.cls_show_msg("悬浮窗已经开启，【按键ESC】退出悬浮窗")==ESC)
			{
				FloatingButtonService.removeWindow();
			}
		}
		
		// case2:第三方应用申请android.permission.INTERNAL_SYSTEM_WINDOW权限
		if(GlobalVariable.currentPlatform!=Model_Type.N920_A7)
		{
			if(myactivity.checkSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == PackageManager.PERMISSION_GRANTED)
			{
				gui.cls_show_msg1(1, "获取%s成功", "android.permission.INTERNAL_SYSTEM_WINDOW");
			}
			else
			{
				myactivity.requestPermissions(new String[] {"android.permission.INTERNAL_SYSTEM_WINDOW"}, 1);
				if(myactivity.checkSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == PackageManager.PERMISSION_GRANTED)
				{
					gui.cls_show_msg1(1, "获取%s成功", "android.permission.INTERNAL_SYSTEM_WINDOW");
				}
				else
				{
					gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:动态获取INTERNAL_SYSTEM_WINDOW权限失败", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}

		gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "%s测试通过",TESTITEM);
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
