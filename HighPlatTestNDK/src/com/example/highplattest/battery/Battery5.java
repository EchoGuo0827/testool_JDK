package com.example.highplattest.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 电池模块
 * file name 		: Battery9.java 
 * description 		: 设备休眠|唤醒广播获取
 * related document : 
 * history 		 	: 变更点                              变更时间		案例人员
 *			  		  F7 V1.0.06导入        20200514		zhengxq
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Battery5 extends UnitFragment
{
	private final String TESTITEM = "休眠唤醒广播接收(F7和F10)";
	private String CLASS_NAME = Battery5.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private boolean isNewlandSleep,isAndroidSleep,isNewlandWake,isAndroidWake;
	public Object sleepObj = new Object();
	public Object wakeObj = new Object();
	
	public int WAIT_TIME= 5*60*1000;
	
	String NEWLAND_SLEEP="android.intent.action.SLEEP";
	String NEWLAND_WAKEUP="android.intent.action.WAKEUP";
	String ANDROID_SLEEP=Intent.ACTION_SCREEN_ON;
	String ANDROID_WAKEUP=Intent.ACTION_SCREEN_OFF;
	
	public void battery5()
	{
		String funcName = "battery9";
		gui.cls_show_msg2(0.3f,"%s测试中...",TESTITEM);
		// case1.1:自动休眠应可接收到休眠广播，android原生的应该也可接收到
		gui.cls_show_msg("case1.1:确保屏幕是唤醒的，【设置自动休眠时间后，等待设备自动休眠】，休眠1min后手动唤醒设备，操作完毕后点任意键继续测试");
		gui.cls_show_msg2(0.3f, "case1.1:耐心等待自动休眠");
		resetFlag();
		synchronized (sleepObj) {
			try {
				sleepObj.wait(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isAndroidSleep&&isNewlandSleep)
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "Android原生的休眠和新大陆自定义的休眠广播均成功接收到");
		}
		else
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "line %d:接收休眠广播失败,true则接收到(%s=%s,%s=%s)",Tools.getLineInfo(),ANDROID_SLEEP,isAndroidSleep,NEWLAND_SLEEP,isNewlandSleep);
		}
		
		// case1.2：自动休眠后唤醒可接收到唤醒广播，android原生的应该也可接收到
		gui.cls_show_msg2(0.3f,"case1.2 等待手动唤醒");
		resetFlag();
		synchronized (wakeObj) {
			try {
				wakeObj.wait(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isAndroidWake&&isNewlandWake)
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "Android原生的休眠和新大陆自定义的唤醒广播均成功接收到");
		}
		else
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "line %d:接收唤醒广播失败,true则接收到(%s=%s,%s=%s)",Tools.getLineInfo(),ANDROID_WAKEUP,isAndroidWake,NEWLAND_WAKEUP,isNewlandWake);
		}
		
		
		// case2.1:手动休眠应可接收到休眠广播，android原生的应该也可接收到
		gui.cls_show_msg("case2.1:确保屏幕是唤醒的，【手动休眠】，休眠1min后手动唤醒设备，操作完毕后点任意键继续测试");
		gui.cls_show_msg2(0.3f,"case2.1:请等待一会手动休眠");
		resetFlag();
		synchronized (sleepObj) {
			try {
				sleepObj.wait(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isAndroidSleep&&isNewlandSleep)
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "Android原生的休眠和新大陆自定义的休眠广播均成功接收到");
		}
		else
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "line %d:接收休眠广播失败,true则接收到(%s=%s,%s=%s)",Tools.getLineInfo(),ANDROID_SLEEP,isAndroidSleep,NEWLAND_SLEEP,isNewlandSleep);
		}
		
		// case2.2：手动休眠后唤醒可接收到唤醒广播，android原生的应该也可接收到
		gui.cls_show_msg2(0.3f,"case2.1 等待手动唤醒");
		resetFlag();
		synchronized (wakeObj) {
			try {
				wakeObj.wait(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isAndroidWake&&isNewlandWake)
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "Android原生的休眠和新大陆自定义的唤醒广播均成功接收到");
		}
		else
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "line %d:接收唤醒广播失败,true则接收到(%s=%s,%s=%s)",Tools.getLineInfo(),ANDROID_WAKEUP,isAndroidWake,NEWLAND_WAKEUP,isNewlandWake);
		}
		gui.cls_show_msg1_record(CLASS_NAME, funcName,1, "%s测试通过，结果可在/sdcard/result.txt文件中查看");
	}
	
	private void resetFlag()
	{
		isAndroidSleep=false;
		isAndroidWake=false;
		isNewlandSleep=false;
		isNewlandWake=false;
	}

	@Override
	public void onTestUp() {
		registBroad();
	}

	@Override
	public void onTestDown() {
		unRegistBroad();
	}
	
	private void registBroad()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		intentFilter.addAction("android.intent.action.WAKEUP");// 新大陆自定义
		
		intentFilter.addAction("android.intent.action.SCREEN_OFF");
		
		intentFilter.addAction("android.intent.action.SLEEP");// 新大陆自定义
		myactivity.registerReceiver(SleepBroadcastReceiver, intentFilter);
	}
	
	private void unRegistBroad()
	{
		if(this != null)
		{
			Log.d("unregistApk", "delete");
			myactivity.unregisterReceiver(SleepBroadcastReceiver);
		}
	}
	
	
	private BroadcastReceiver SleepBroadcastReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent) 
		{
			LoggerUtil.d("battery9-action="+intent.getAction());
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_SCREEN_ON))
			{
				isAndroidWake=true;
			}
			else if(action.equals(Intent.ACTION_SCREEN_OFF))
			{
				isAndroidSleep=true;
			}
			else if(action.equals(NEWLAND_SLEEP))
			{
				isNewlandSleep=true;
			}
			else if(action.equals(NEWLAND_WAKEUP))
			{
				isNewlandWake=true;
			}
			LoggerUtil.e("isNewlandWake="+isNewlandWake+"|||isAndroidWake="+isAndroidWake);
			if(isNewlandWake&&isAndroidWake)
			{
				synchronized (wakeObj) {
					wakeObj.notify();
				}
			}
			
			LoggerUtil.e("isNewlandSleep="+isNewlandSleep+"|||isAndroidSleep="+isAndroidSleep);
			if(isNewlandSleep&&isAndroidSleep)
			{
				synchronized (sleepObj) {
					sleepObj.notify();
				}
			}
			
		};
	};

}
