package com.example.highplattest.other;

import android.content.Context;
import android.os.PowerManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: Other27.java 
 * description 		: BUG2020091703354修复验证，设备频繁休眠唤醒设备不会出现重启等异常现象
 * related document : 
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		       郑佳雯		      20200921 		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other27 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "休眠唤醒压力";
	public final String TAG = Other27.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void other27()
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("休眠唤醒测试\n0.休眠后随机时间唤醒\n1.休眠后立即唤醒\n");
			switch (returnValue) 
			{
					
			case '0':
				randomWeak();
				break;

			case '1':
				imWeak();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}

	
	public void randomWeak()
	{
		String funcName="randomWeak";
		int preSreenTimeout = Tools.getSreenTimeout(myactivity);
		int SleepWake_Times=20;//休眠唤醒次数
		int SleepTime=30;//单次休眠时长
		int SleepSucTime=0;//成功休眠唤醒次数
				
		PowerManager pm = (PowerManager) myactivity.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright"); //唤醒屏幕
			
		gui.cls_show_msg("【休眠后随机时间唤醒】请确保未插入usb线,本案例测试时长较长,点击任意键继续");
		SleepTime=20;//设置单次休眠时长(20min)
		LoggerUtil.i("sleepWakePre,SleepTime="+SleepTime);
		
		/**休眠唤醒次数*/
		SleepWake_Times = gui.JDK_ReadData(30, 20, "请输入休眠唤醒的压测次数");
		LoggerUtil.i("sleepWakePre,SleepWake_Times="+SleepWake_Times);
				
		for(int j=1;j<=SleepWake_Times;j++)
		{
			/**测试前置，休眠设置为永不休眠*/
			Tools.setSreenTimeout(myactivity, 30*60*1000);
			LoggerUtil.v("getScreen Time="+Tools.getSreenTimeout(myactivity));
							
			/**开始休眠唤醒,15s后进入休眠*/
			int total = (int)(1+Math.random()*SleepTime)*60;//休眠10~SleepTime内的随机时长(min)
			LoggerUtil.d("sleepWakePre,total="+total);
			
			Tools.setSreenTimeout(myactivity, 15*1000);

			gui.cls_show_msg1(15, "15s后进入休眠,%d分钟后自动唤醒屏幕【不必纠结休眠时间的准确性】",total/60);
			//根据获取的随机时间唤醒屏幕
			while (true) {
				if (total <= 0) {
					wakeLock.acquire();	// 唤醒屏幕，释放wakeLock
					wakeLock.release();
					SleepSucTime++;
					break;
				} else {
					 gui.cls_show_msg1(60, "即将唤醒屏幕"+total+"s");
					 total = total - 60;
				}
			}
			
			Tools.setSreenTimeout(myactivity, 30*60*1000);
			LoggerUtil.i("休眠唤醒屏幕");
		}
		/**测试后置,恢复测试前的休眠时间*/
		Tools.setSreenTimeout(myactivity, preSreenTimeout);
		gui.cls_show_msg1_record(TAG, funcName,0,"休眠唤醒压力测试结束,共进行(%d)次休眠唤醒,成功休眠唤醒(%d)次",SleepWake_Times,SleepSucTime);
	}
	
	public void imWeak()
	{
		String funcName="imWeak";
		
		int preSreenTimeout = Tools.getSreenTimeout(myactivity);
		int SleepWake_Times=20;//休眠唤醒次数
		int SleepSucTime=0;//成功休眠唤醒次数
				
		PowerManager pm = (PowerManager) myactivity.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright"); //唤醒屏幕
			
		gui.cls_show_msg("【休眠后立即唤醒】请确保未插入usb线,点击任意键继续");		
		/**休眠唤醒次数作用:*/
		SleepWake_Times = gui.JDK_ReadData(30, 20, "请输入休眠唤醒的压测次数");
		LoggerUtil.i("sleepWakePre,SleepWake_Times="+SleepWake_Times);
				
		for(int j=1;j<=SleepWake_Times;j++)
		{
			/**测试前置，休眠设置为永不休眠*/
			Tools.setSreenTimeout(myactivity, 30*60*1000);
			LoggerUtil.v("getScreen Time="+Tools.getSreenTimeout(myactivity));
							
			/**开始休眠唤醒,15s后进入休眠*/		
			Tools.setSreenTimeout(myactivity, 15*1000);

			gui.cls_show_msg1(17, "15s后进入休眠,并立刻自动唤醒屏幕");
			//根据获取的随机时间唤醒屏幕
			wakeLock.acquire();	// 唤醒屏幕，释放wakeLock
			wakeLock.release();
			SleepSucTime++;
			LoggerUtil.i("休眠唤醒屏幕");
		}
		/**测试后置,设置为永不休眠*/
		Tools.setSreenTimeout(myactivity, preSreenTimeout);
		gui.cls_show_msg1_record(TAG, funcName,0,"休眠唤醒压力测试结束,共进行(%d)次休眠唤醒,成功休眠唤醒(%d)次",SleepWake_Times,SleepSucTime);
	}
	
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
	
}