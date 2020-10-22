package com.example.highplattest.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module           : 系统相关
 * file name        : Sys6.java 
 * Author           : zsh
 * version          : 
 * DATE             : 20190408
 * directory        : 
 * description      : 系统休眠广播测试
 * related document :
 * history          : author            date            remarks
 *                    zsh        	20190408        	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class Other15 extends UnitFragment {
	private final String TESTITEM = "系统休眠广播";
	private String fileName="Other15";
	private Gui gui = new Gui(myactivity, handler);
	private IntentFilter mIntentFilter;
	private BroadcastChangeReceiver re;
	private SettingsManager settingsManager;
	public int TAG=0;
	Context mContext;
	public void other15()
	{	
		//前置配置
		mIntentFilter=new IntentFilter();
		mIntentFilter.addAction("android.intent.action.SLEEP");
		re=new BroadcastChangeReceiver();
		myactivity.registerReceiver(re,mIntentFilter);
		mContext = myactivity.getApplicationContext();
		settingsManager = (SettingsManager)mContext.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		boolean ret;
		int total = 60;
		
		//case1 手动关闭屏幕进入休眠,能够接收到休眠广播
		TAG=0;
		gui.cls_show_msg("即将进行系统休眠广播测试,请手动关闭屏幕进入休眠,然后唤醒屏幕任意键继续");
		if(TAG==0){
			gui.cls_show_msg1_record(fileName,"other15",gKeepTimeErr,"line %d:%s测试失败,未接收到系统休眠广播", Tools.getLineInfo(),TESTITEM);
			return;
		}else{
			gui.cls_show_msg1(1,"监听到系统休眠广播,即将进入下个case");
		}
		
		//case2 设置休眠时间15,30s 能够接收到休眠广播
		TAG=0;
		int[] timeCase1 = { 15*1000, 30*1000 };
		for (int j = 0; j < timeCase1.length; j++) 
		{
			total = timeCase1[j]/1000;
			if ((ret = settingsManager.setScreenTimeout(timeCase1[j]))==false) 
			{
				gui.cls_show_msg1_record(fileName,"other15",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			while (true) 
			{
				gui.cls_show_msg1(1, total+"s后休眠，休眠后短按电源键唤醒");
				total = total -1;
				if(total == 1)
				{
					gui.cls_show_msg1(1, "马上休眠，休眠后短按电源键唤醒");
					if(TAG==0){
						gui.cls_show_msg1_record(fileName,"other15",gKeepTimeErr,"line %d:%s测试失败,未接收到系统休眠广播", Tools.getLineInfo(),TESTITEM);
						return;
					}else{
						gui.cls_show_msg("监听到系统休眠广播,任意键进入下个case");
					}
					break;
				}
			}
	
		}
		
		//case2 设置休眠时间1min 5min, 能够接收到休眠广播
		TAG=0;
		int[] timeCase2 = { ONE_MIN, FIVE_MIN };
		total = 60;
		for (int i = 0; i < timeCase2.length; i++) 
		{
			total = timeCase2[i]/1000;
			if ((ret = settingsManager.setScreenTimeout(timeCase2[i]))==false) 
			{
				gui.cls_show_msg1_record(fileName,"other15",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			} 
			while (true) 					
			{
				gui.cls_show_msg1(2, total+"s后休眠，休眠后短按电源键唤醒");
				total = total -2;
				if(total == 2)
				{
					gui.cls_show_msg1(2, "马上休眠，休眠后短按电源键唤醒");
					if(TAG==0){
						gui.cls_show_msg1_record(fileName,"other15",gKeepTimeErr,"line %d:%s测试失败,未接收到系统休眠广播", Tools.getLineInfo(),TESTITEM);
						return;
					}else{
						gui.cls_show_msg("监听到系统休眠广播,任意键进入下个case");
					}
					break;
				} 
			}
		}
		
		//case3 设置为永不休眠,不应接收到休眠广播
		TAG=0;
		settingsManager.setScreenTimeout(-1);
		gui.cls_show_msg("设置为永不休眠,将pos连接电源放置一晚,不应接收到休眠广播,测试后任意键继续...");
		if(TAG==1){
			gui.cls_show_msg1_record(fileName,"other15",gKeepTimeErr,"line %d:%s测试失败,未休眠状态下接收到系统休眠广播", Tools.getLineInfo(),TESTITEM);
			return;
		}else{
			gui.cls_show_msg1_record(fileName,"other15",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
		}
		
	}

	class BroadcastChangeReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			TAG=1;
		}
		
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		myactivity.unregisterReceiver(re);
		
	}
	

}
