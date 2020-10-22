package com.example.highplattest.event;

import java.util.MissingFormatArgumentException;

import android.newland.SettingsManager;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
/************************************************************************
 * module 			: 事件机制优化 NDK_SYS_RegisterEvent(永不休眠)
 * file name 		: Event6.java 
 * description 		: 事件机制优化 NDK_SYS_RegisterEvent(永不休眠)，插卡或刷卡后会自动唤醒屏幕
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   	20200820	 	NDK_SYS_RegisterEvent(永不休眠)，新建(先在A7导入)
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event6 extends UnitFragment{
	private Gui gui = new Gui(myactivity, handler);
	private final String TESTITEM = "NDK_SYS_RegisterEvent(永不休眠-异常测试)";
	public final String FILE_NAME = Event6.class.getSimpleName();
	int mNotifyNum =-1;
	int eventCount=0;
	final int NEVER_TIMEOUT = 0;/**A7暂定0代表的是永不超时*/
	
	private NotifyEventListener listener2=new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int arg0, int arg1, byte[] arg2) {
			LoggerUtil.d("eventNum="+arg0);
			mNotifyNum = arg0;
			if(mNotifyNum==0x08)// 监听到IC卡事件
				eventCount++;
			return SUCC;
		}
	};
	
	public void event6()
	{
		int[] registerNum = {0x04,0x08,0x10};
		try {
			testEvent6(registerNum);
		} catch (MissingFormatArgumentException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(FILE_NAME, "event6", gKeepTimeErr, "line %d：抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
		}
	}
	
	private void testEvent6(int[] registerNum)
	{
		while(true)
		{
			for (int i:registerNum) {
				JniNdk.JNI_SYSUnRegisterEvent(i);
				SystemClock.sleep(100);
			}
			String funcName="testEvent6";
			int iRet=-1;
			int nkeyIn = gui.cls_show_msg("%s\n1.对讲机干扰磁道信号\n2.应用奔溃异常\n3.重启异常\n4.IC卡未拔异常\n", TESTITEM);
			switch (nkeyIn) {
				
			case '1':
				testCase1(registerNum);
				break;
				
			case '2':
				for (Integer i:registerNum) {
					if((iRet = JniNdk.JNI_SYSRegisterEvent( i, NEVER_TIMEOUT,listener2))!=NDK_OK)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iret=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}

				gui.cls_show_msg("构造应用奔溃情况,应用奔溃后重新进入Event5测试，可正常测试视为测试通过,任意键后应用奔溃");
				SettingsManager settingsManager = null;
				settingsManager.getAllowReplaceApp();
				break;
				
			case '3':
				for (Integer i:registerNum) {
					if((iRet = JniNdk.JNI_SYSRegisterEvent( i,NEVER_TIMEOUT, listener2))!=NDK_OK)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iret=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}
				gui.cls_show_msg("重启后进入Event5测试，可正常测试视为测试通过,任意键后重启设备");
				Tools.reboot(myactivity);
				break;
				
			case '4':
				testCase2(registerNum);
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}

	}
	
	private void testCase1(int[] registerNum)
	{
		// case1:1.应用注册事件为永不超时，使用对讲机干扰磁道信号，辅CPU会被唤醒，主CPU仍是休眠
		// 测试点:测试设备的功耗，应用是否接收到事件上送
		int iRet=-1;
		String funcName = "testCase1";
		mNotifyNum = -1;
		for (int i :registerNum) {
			if((iRet = JniNdk.JNI_SYSRegisterEvent(i, NEVER_TIMEOUT,listener2))!=NDK_OK)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		gui.cls_show_msg("请使用对讲机干扰磁道信号【使用功耗仪测试功耗值，应只有K21唤醒的功耗，无整机功耗】,此时K21的CPU会不断的被唤醒,但是Android的CPU不会被唤醒,屏幕不会亮,对讲机信号干扰准备完毕任意键继续");
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<3*60)
		{
			if(mNotifyNum!=-1)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(num=%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				break;
			}
		}
		gui.cls_show_msg1(1, "case1测试通过");
		
		// case2:2.应用注册事件为永不超时，使用对讲机干扰磁道信号，干扰信号消失后，辅CPU休眠，主CPU休眠
		// 测试点:测试设备的功耗值
		mNotifyNum = -1;
		gui.cls_show_msg("请关闭对讲机的干扰信号【使用功耗仪测试功耗值，此时的功耗应低于10mA】,此时进入低功耗状态,讲机信号关闭后任意键继续");
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<3*60)
		{
			if(mNotifyNum!=-1)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(num=%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				break;
			}
		}
		gui.cls_show_msg1(1, "case2测试通过");
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", funcName);
	}

	private void testCase2(int[] registerNum)
	{
		// case1.5.应用注册事件为永不超时，插入IC卡未拔
		// 测试点:应用只能监听到一次的IC卡事件
		int iRet=-1;
		eventCount=0;
		String funcName = "testCase2";
		mNotifyNum = -1;
		for (int i:registerNum) {
			if((iRet = JniNdk.JNI_SYSRegisterEvent(i, NEVER_TIMEOUT,listener2))!=NDK_OK)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		gui.cls_show_msg("请插入IC卡或插入拔出,此时只能检测到一次的IC卡事件,IC卡插入或拔出完毕任意键继续");
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<3*60)
		{
			if(eventCount>2)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(num=%d,count=%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum,eventCount);
				break;
			}
		}
		gui.cls_show_msg1(1, "case1测试通过");
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", funcName);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
