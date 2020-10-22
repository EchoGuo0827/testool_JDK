package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.TimeNewland;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest14.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150310
 * directory 		: 
 * description 		: RTC综合
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest14 extends DefaultFragment 
{
	private final String TAG = SysTest14.class.getSimpleName();
	private final String TESTITEM = "RTC综合";
	Gui gui = new Gui(myactivity, handler);
	
	public void systest14() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("RTC综合\n0.运行");
			switch (nkeyIn) 
			{
			case '0':
				rtcTest();
				break;
				
			case ESC:
				intentSys();
				return;
			}

		}
	}	
	
	public void rtcTest() 
	{
		TimeNewland oldTime = new TimeNewland();
		TimeNewland newTime = new TimeNewland();
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
		// 设置时间为2015年12月31号 13:11:01
		newTime.obj_year = 2015-1900;
		newTime.obj_mon = 12-1;
		newTime.obj_mday = 31;
		newTime.obj_hour = 13;
		newTime.obj_min = 11;
		newTime.obj_sec = 01;
		
		// 获取K21端时间
		if(JniNdk.JNI_Sys_GetPosTime(oldTime)!=NDK_OK)
		{
			gui.cls_show_msg("line %d:获取时间失败", Tools.getLineInfo());
			return;
		}

		if(oldTime.obj_year==newTime.obj_year)
		{
			gui.cls_show_msg("请先将系统时间改为非%4d年，任意键退出...", newTime.obj_year+1900);
		}
		
		if(JniNdk.JNI_Sys_SetPosTime(newTime)!=NDK_OK)
		{
			gui.cls_show_msg("line %d:设置时间失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg("时间已调整为%s，任意键继续",newTime.formatTime());
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("请选择动作\n0.关机后再重启机器\n1.重启机器");
			switch (returnValue) 
			{
				
			case '0':
				// 关机需要系统权限，只能手动关机
				gui.cls_show_msg("请手动关机并重启设备后，人工校验所设时间！");
				return;
				
			case '1':
				gui.cls_show_msg("点任意键后将重启设备，重启设备后请人工校验所设时间！");
				Tools.reboot(myactivity);
				break;

			case ESC:
				return;
				
			default:
				break;
			}
		}
	}
}
