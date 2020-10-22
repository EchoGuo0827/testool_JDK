package com.example.highplattest.other;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module           : 系统相关
 * file name        : Sys6.java 
 * Author           : zsh
 * version          : 
 * DATE             : 20190403
 * directory        : 
 * description      : 清除锁屏广播测试
 * related document :
 * history          : author            date            remarks
 *                    zsh        	201904003        	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other21 extends UnitFragment implements LocationListener 
{
	private final String TESTITEM = "清除锁屏广播";
	private String fileName="Other21";
	private Gui gui = new Gui(myactivity, handler);
	Context mcontext;
	
	public void other21()
	{	
		mcontext = myactivity.getApplicationContext();
		Intent mIntent = new Intent("android.newland.LOCKSCREEN.DEFAULT");
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		//异常测试,开机未设置锁屏状态下,发送广播不发生异常
		if(gui.ShowMessageBox(("即将进行前置测试,请确保当前无锁屏,需要重启后进入本案例,再次进入请取消,是否立即重启,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) == BTN_OK)
		{
			Tools.reboot(myactivity);
			return;
		}
		mcontext.sendBroadcast(mIntent);
		if(gui.ShowMessageBox(("清锁屏广播发送结束,请重开屏幕查看是否有锁屏,或发生其他异常,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"Other21",gKeepTimeErr,"line %d:%s测试失败,未设置锁屏状态下,发送广播发生异常", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case1 滑动锁屏方式清除
		gui.cls_show_msg("即将进行清除锁屏广播测试,请先进入设置修改锁屏方式为滑动,并重新打开屏幕确认生效,完成后任意键继续");
		mcontext.sendBroadcast(mIntent);
		if(gui.ShowMessageBox(("锁屏清除完成,请开关屏幕查看锁屏是否已经清除,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"Other21",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case2 图案锁屏方式清除
		gui.cls_show_msg("请进入设置修改锁屏方式为图案,并重新打开屏幕确认生效,完成后任意键继续");
		mcontext.sendBroadcast(mIntent);
		if(gui.ShowMessageBox(("锁屏清除完成,请开关屏幕查看锁屏是否已经清除,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"Other21",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case3 pin码锁屏方式清除
		gui.cls_show_msg("请进入设置修改锁屏方式为PIN码,并重新打开屏幕确认生效,完成后任意键继续");
		mcontext.sendBroadcast(mIntent);
		if(gui.ShowMessageBox(("锁屏清除完成,请开关屏幕查看锁屏是否已经清除,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"Other21",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case4 密码锁屏方式清除
		gui.cls_show_msg("请进入设置修改锁屏方式为密码,并重新打开屏幕确认生效,完成后任意键继续");
		mcontext.sendBroadcast(mIntent);
		if(gui.ShowMessageBox(("锁屏清除完成,请开关屏幕查看锁屏是否已经清除,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"Other21",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		gui.cls_show_msg1_record(fileName,"Other21",gScreenTime,"%s测试通过,长按确认退出测试", TESTITEM);
		
		//case5 清除锁屏再掉电后应保持清除状态
		if(gui.ShowMessageBox(("即将测试掉电后清除状态是否保持,开机后若无锁屏则测试通过,否则视为测试失败,是否立即重启,是[确定],否[取消]").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) == BTN_OK)
		{
			Tools.reboot(myactivity);
			return;
		}
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

}
