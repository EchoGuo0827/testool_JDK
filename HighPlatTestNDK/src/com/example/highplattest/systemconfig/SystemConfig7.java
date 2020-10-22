package com.example.highplattest.systemconfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.TimeNewland;

import android.annotation.SuppressLint;

/************************************************************************
 * 
 * module 			: Android系统设置测试
 * file name 		: SystemConfig7.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150506
 * directory 		: 
 * description 		: 测试修改Android时间后，K21时间是否同步修改，修改K21端时间，Android端是否同步修改
 * related document : 
 * history 		 	: 变更点												变更时间			变更人员
 *			  		 F7新版本修改为设置Android时间，不会同步刷新k21时间
 *					 旧版本设置android时间，会同步刷新K21时间						20200507		郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig7 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "测试系统时间同步更新";
	private String fileName="SystemConfig7";
	private Gui gui = null;
	public void systemconfig7() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig7",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		while(true)
		{
			int nkeyIn= gui.cls_show_msg("%s\n1.测试K21和Android端同步时间\n2.恢复K21端时间至最新\n3.获取K21端和android端时间\n", TESTITEM);
			switch (nkeyIn) {
			case '1':
				unitTest();
				break;
				
			case '2':
				restoreK21Time();
				break;
				
			case '3':
				TimeNewland getTimeNewland = new TimeNewland();
				JniNdk.JNI_Sys_GetPosTime(getTimeNewland);
				gui.cls_show_msg("Android端时间：%s，K21端时间：%s", getNowTime(),getTimeNewland.formatTime());
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}

	}
	
	private void unitTest()
	{
		String funcName = "unitTest";
		/*private & local definition*/
		String message;
		TimeNewland getTimeNewland = new TimeNewland();
		TimeNewland setTimeNewland = new TimeNewland();
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		
		// 测试前置，确保机器已经连接上网络
		if(gui.ShowMessageBox("Android端是否可以上网".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1(2, "测试前请确保Android端可以上网");
			if(!GlobalVariable.isContinue)
				return;
		}
			
		// case1:用手动设置的方式设置android端的时间，输出Android端和K21端的时间，预期应该一致,【F7系列新版本设置Android端时间，K21端时间不会修改】
		gui.cls_show_msg("进入设置->时间和日期->设置日期、时间，关闭自动确定日期和时间，【手动修改Android端时间,修改完成点击任意键】");
		JniNdk.JNI_Sys_GetPosTime(getTimeNewland);
//		if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
//		{
//			message = String.format("Android端时间：%s，K21端时间：%s，Android的时间和K21端的时间是否【不一致】", getNowTime(),getTimeNewland.formatTime());
//			if (gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
//			{
//				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%sAndroid和K21端的时间不一致（%s）", Tools.getLineInfo(),TESTITEM,GlobalVariable.FLAG_SYSTEM_SIGN);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//		}
//		else
//		{
			message = String.format("Android端时间：%s，K21端时间：%s，Android的时间和K21端的时间是否【一致】", getNowTime(),getTimeNewland.formatTime());
			if (gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%sAndroid和K21端的时间不一致（%s）", Tools.getLineInfo(),TESTITEM,GlobalVariable.FLAG_SYSTEM_SIGN);
				if(!GlobalVariable.isContinue)
					return;
			}
//		}
			
		
		// case2.1:将Android的时间修改为自动确定日期和时间，输出Android和K21的时间，预期应该不一致
		gui.cls_show_msg("进入设置->时间和日期->自动确定日期和时间,【等待状态栏时间更新为当前时间】后点击任意键");
		// 跳转到设置界面
		JniNdk.JNI_Sys_GetPosTime(getTimeNewland);
//		if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
//		{
			message = String.format("Android端时间：%s，K21端时间：%s,Android的时间和K21端的时间是否【不一致】", getNowTime(),getTimeNewland.formatTime());
			if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%sAndroid和K21端的时间不一致（%s）", Tools.getLineInfo(),TESTITEM,GlobalVariable.FLAG_SYSTEM_SIGN);
				if(!GlobalVariable.isContinue)
					return;
			}
//		}
//		else
//		{
//			message = String.format("Android端时间：%s，K21端时间：%s,Android的时间和K21端的时间是否【一致】", getNowTime(),getTimeNewland.formatTime());
//			if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
//			{
//				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%sAndroid和K21端的时间不一致（%s）", Tools.getLineInfo(),TESTITEM,GlobalVariable.FLAG_SYSTEM_SIGN);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//		}
			
//		
		// case2.2:使用NDK接口修改K21的时间，Android端的时间采用与网络时间同步的方式，此时Android端的时间和K21的时间不同步
		setTimeNewland.obj_year = 2000-1900;
		setTimeNewland.obj_mon = 1-1;
		setTimeNewland.obj_mday = 2;
		setTimeNewland.obj_hour = 3;
		setTimeNewland.obj_min = 4;
		setTimeNewland.obj_sec = 5;
		JniNdk.JNI_Sys_SetPosTime(setTimeNewland);
		JniNdk.JNI_Sys_GetPosTime(getTimeNewland);
		
		message = String.format("Android端时间：%s，K21端时间：%s,Android的时间和K21端的时间是否【不一致】", getNowTime(),getTimeNewland.formatTime());
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%sAndroid和K21端的时间不一致（%s）", Tools.getLineInfo(),TESTITEM,GlobalVariable.FLAG_SYSTEM_SIGN);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:已修改的K21时间，关闭网络，重启设备，Android端时间会与K21端时间同步
		if(gui.cls_show_msg("case3:已修改的K21时间，关闭网络，重启设备，Android端时间会与K21端时间同步,是否进行本case测试,是[确认],否[其他]")==ENTER)
		{
			gui.cls_show_msg("请先关闭网络，完成后点击任意键");
			message = String.format("Android端时间：%s，K21端时间：%s，重启后Android时间与K21一致则通过，反之不通过", getNowTime(),getTimeNewland.formatTime());
			if(gui.ShowMessageBox((message+"，是否立即重启机器").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
				Tools.reboot(myactivity);
		}
		
		// case4:，已修改的K21端时间，开启自动确定时间和日期，重启设备，Android端时间会与K21端时间同步，开机后Android端时间会与网络时间同步，K21不会改变
		if(gui.cls_show_msg("case4:已修改的K21端时间，开启自动确定时间和日期，重启设备，Android端时间会与K21端时间同步，开机后Android端时间会与网络时间同步，K21不会改变,是否进行本case测试,是[确认],否[其他]")==ENTER)
		{
			gui.cls_show_msg("请先确保网络可用，完成后点击任意键");
			message = String.format("Android端时间：%s，K21端时间：%s，重启后K21与重启前一致则通过，反之不通过", getNowTime(),getTimeNewland.formatTime());
			if(gui.ShowMessageBox((message+"，是否立即重启机器").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
				Tools.reboot(myactivity);
		}
		
		
		// case5:已修改的K21时间，关闭自动确定时间和日期，重启设备，Android端时间会与K21端时间同步
		if(gui.cls_show_msg("case5:已修改的K21时间，关闭自动确定时间和日期，重启设备，Android端时间会与K21端时间同步,是否进行本case测试,是[确认],否[其他]")==ENTER)
		{
			gui.cls_show_msg("请先关闭自动确定时间和日期，完成后点击任意键");
			message = String.format("Android端时间：%s，K21端时间：%s，重启后Android时间与K21一致则通过，反之不通过", getNowTime(),getTimeNewland.formatTime());
			if(gui.ShowMessageBox((message+"，是否立即重启机器").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
				Tools.reboot(myactivity);
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig7",gScreenTime, "%s测试结束，未重启(长按确认键退出测试)", TESTITEM);
	}
	
	/**测试完毕后要恢复K21端的时间为最新的Android端时间*/
	public void restoreK21Time()
	{
		gui.cls_show_msg("进入设置->时间和日期->设置日期、时间，开启自动确定日期和时间，Android端时间更新至最新后点击任意键");

		Calendar now = Calendar.getInstance();
		System.out.println("年: " + now.get(Calendar.YEAR));
		System.out.println("月: " + (now.get(Calendar.MONTH) + 1) + "");
		System.out.println("日: " + now.get(Calendar.DAY_OF_MONTH));
		System.out.println("时: " + now.get(Calendar.HOUR_OF_DAY));
		System.out.println("分: " + now.get(Calendar.MINUTE));

		
		TimeNewland getTimeNewland = new TimeNewland();
		TimeNewland setTimeNewland = new TimeNewland();
		setTimeNewland.obj_year = now.get(Calendar.YEAR)-1900;
		setTimeNewland.obj_mon = now.get(Calendar.MONTH);
		setTimeNewland.obj_mday = now.get(Calendar.DAY_OF_MONTH);
		setTimeNewland.obj_hour = now.get(Calendar.HOUR_OF_DAY);
		setTimeNewland.obj_min = now.get(Calendar.MINUTE);
		setTimeNewland.obj_sec = now.get(Calendar.SECOND);
		JniNdk.JNI_Sys_SetPosTime(setTimeNewland);
		JniNdk.JNI_Sys_GetPosTime(getTimeNewland);
		gui.cls_show_msg1(10,"Android端时间：%s，K21端时间：%s,K21端时间已恢复完毕", getNowTime(),getTimeNewland.formatTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getNowTime()
	{
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = time.format(new java.util.Date());
		return str;
	}
	

	@Override
	public void onTestUp() 
	{
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
	}
}
