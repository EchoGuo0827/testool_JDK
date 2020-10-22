package com.example.highplattest.event;

import android.os.SystemClock;

import com.example.highplattest.activity.CaseActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
/************************************************************************
 * module 			: 事件机制优化 NDK_SYS_RegisterEvent(永不休眠)
 * file name 		: Event5.java 
 * description 		: 事件机制优化 NDK_SYS_RegisterEvent(永不休眠)，插卡或刷卡后会自动唤醒屏幕
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   	20200820	 	NDK_SYS_RegisterEvent(永不休眠)，新建
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event5 extends UnitFragment{
	private Gui gui = new Gui(myactivity, handler);
	private final String TESTITEM = "NDK_SYS_RegisterEvent(永不休眠)";
	public final String FILE_NAME = Event5.class.getSimpleName();
	int mNotifyNum =-1;
	final int NEVER_TIMEOUT = 0;// 时间=0,是永不超时
	String mCaseTip ="";
	String mViewTip;
	
	private NotifyEventListener listener1=new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int arg0, int arg1, byte[] arg2) {
			LoggerUtil.e(Tools.getSysNowTime()+"->监听到事件="+arg0);
			gui.cls_printf(String.format(mCaseTip+"\n监听到事件触发(num=%d,4是检测到磁卡事件,8是检测到IC卡事件,16是非接卡事件)",arg0).getBytes());
			gui.cls_only_write_msg(FILE_NAME, "NotifyEventListener", "%s->监听到事件触发(num=%d,4是检测到磁卡事件,8是检测到IC卡事件,16是非接卡事件)", mCaseTip,arg0);
			mNotifyNum = arg0;
//			gui.cls_show_msg1_record(FILE_NAME, "NotifyEventListener", 1, "%s->监听到事件触发(num=%d,4是检测到磁卡事件,8是检测到IC卡事件,16是非接卡事件)", mCaseTip,arg0);
			return SUCC;
		}
	};
	
	
//	private NotifyEventListener listener2=new NotifyEventListener() {
//		
//		@Override
//		public int notifyEvent(int arg0, int arg1, byte[] arg2) {
//			LoggerUtil.e(Tools.getSysNowTime()+"->监听到事件="+arg0);
//			mNotifyNum = arg0;
//			return SUCC;
//		}
//	};
	
	public void event5()
	{
		// 目前先不配置案例
		int[] registerNum ={0x04,0x08,0x10};
		try {
			testEvent5(registerNum);
			for (int i:registerNum) {
				JniNdk.JNI_SYSUnRegisterEvent(i);
				SystemClock.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(FILE_NAME, "event5", 1, "line %d：抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			for (int i:registerNum) {
				JniNdk.JNI_SYSUnRegisterEvent(i);
				SystemClock.sleep(100);
			}
		}
		
	}
	
	private void testEvent5(int[] registerNum)
	{
		while(true)
		{
			// 测试前置  注销事件
			for (int i:registerNum) {
				JniNdk.JNI_SYSUnRegisterEvent(i);
				SystemClock.sleep(100);
			}
			String funcName = "testEvent5";
			int iRet=-1;
			int nkeyIn = gui.cls_show_msg("%s\n0.深休眠时的功耗\n1.事件机制+机械臂的刷卡压力\n2.单元测试\n3.放置一晚触发事件\n", TESTITEM);
			switch (nkeyIn) {
			case '0':
				mCaseTip = "0.深休眠功耗";
				// 设置为永不休眠
				for (int i :registerNum) {
					if((iRet = JniNdk.JNI_SYSRegisterEvent(i, NEVER_TIMEOUT,listener1))!=NDK_OK)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}
				gui.cls_show_msg("设备已开启事件机制-深休眠模式,让设备进入休眠状态后请使用功耗仪器测试3小时左右，平均功耗低于10mA才可视为测试通过");
				break;
				
			case '1':
				mCaseTip = "1.事件机制+机械臂的刷卡压力";
				// 设置为永不休眠
				for (int i :registerNum) {
					if((iRet = JniNdk.JNI_SYSRegisterEvent(i, NEVER_TIMEOUT,listener1))!=NDK_OK)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}
				gui.cls_show_msg("设备已开启事件机制-深休眠模式,自动休眠时间设置为30s【关闭24小时重启机制，让设备进入休眠状态后】请使用机械臂进行刷卡压力测试，需分别测试时间间隔为655s和30s的两种压力测试,【压力测试8小时后请去result文件查看,事件是否都正常接收到】");
				break;
				
			case '2':
				mCaseTip = "2.单元测试";
				unitTest(registerNum);
				break;
				
			case '3':
				mCaseTip = "3.放置一晚触发事件";
				// 设置为永不休眠
				for (int i:registerNum) {
					if((iRet = JniNdk.JNI_SYSRegisterEvent(i,NEVER_TIMEOUT, listener1))!=NDK_OK)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}
				gui.cls_show_msg("设备已开启事件机制-深休眠模式,【让设备进入休眠状态后】放置设备24小时后再进行刷卡或插卡操作应该能监听到事件,刷卡或插卡完毕之后再唤醒设备和按任意键继续");
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}

	}
	
	private void unitTest(int[] registerNum)
	{
		String funcName = "unitTest";
		int iRet=-1;
		int screenTimeOut = Tools.getSreenTimeout(myactivity);
		
		// case3:只注册磁卡时可正常监听到磁卡事件
		gui.cls_show_msg1(1, "case3:只注册磁卡时可正常监听到磁卡事件");
		mNotifyNum = -1;
		if((iRet = JniNdk.JNI_SYSRegisterEvent(0x04,NEVER_TIMEOUT, listener1))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(iRet=%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}

		mCaseTip="设备深休眠1min后刷卡,可正常监听到刷卡事件并且点亮屏幕视为本case3通过,测试完毕任意键继续";
		gui.cls_show_msg(mCaseTip);
		
		JniNdk.JNI_SYSUnRegisterEvent(0x04);
		SystemClock.sleep(100);
		// case4:只注册IC卡时可正常监听到IC卡事件
		gui.cls_show_msg1(1, "case4:只注册IC卡时可正常监听到IC卡事件");
		mNotifyNum = -1;
		if((iRet = JniNdk.JNI_SYSRegisterEvent(0x08,NEVER_TIMEOUT, listener1))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(iRet=%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}

		mCaseTip="设备深休眠1min后插IC卡,可正常监听到插卡事件并且点亮屏幕视为本case4通过,测试完毕任意键继续";
		gui.cls_show_msg(mCaseTip);
		
		JniNdk.JNI_SYSUnRegisterEvent(0x08);
		SystemClock.sleep(100);
		
		// case1:设备刚熄屏时，刷卡|插卡|挥卡，刷卡和插卡可唤醒设备，点亮屏幕，应用监听到事件触发，挥卡不行
		gui.cls_show_msg1(1, "case1:设备刚熄屏时，刷卡|插卡|挥卡，刷卡和插卡可唤醒设备，点亮屏幕，应用监听到事件触发，挥卡不行");
		mNotifyNum = -1;
		for (int i:registerNum) {
			if((iRet = JniNdk.JNI_SYSRegisterEvent(i,NEVER_TIMEOUT, listener1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		mCaseTip="设备的自动休眠时间为30s,请在屏幕自动休眠后5s内刷卡|插卡|挥卡,刷卡和插卡可正常监听到和点亮屏幕,挥卡无法监听到和电量屏幕视为本case1通过,测试完毕任意键继续";
		gui.cls_show_msg(mCaseTip);
		
		for (int i:registerNum) {
			JniNdk.JNI_SYSUnRegisterEvent(i);
			SystemClock.sleep(100);
		}
		
		// case2:设备熄屏1min进入深休眠时，刷卡|插卡|挥卡，刷卡和插卡可唤醒设备，点亮屏幕，应用监听到事件触发，挥卡不行
		gui.cls_show_msg1(1, "case2:设备熄屏1min进入深休眠时，刷卡|插卡|挥卡，刷卡和插卡可唤醒设备，点亮屏幕，应用监听到事件触发，挥卡不行");
		mNotifyNum = -1;
		for (int i:registerNum) {
			if((iRet = JniNdk.JNI_SYSRegisterEvent(i, NEVER_TIMEOUT,listener1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:%s测试失败(registerNum=%d,iRet=%d)", Tools.getLineInfo(),TESTITEM,i,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		mCaseTip = "设备的自动休眠时间为30s,请在屏幕自动休眠后1min后刷卡|插卡|挥卡,刷卡和插卡可正常监听到和点亮屏幕,挥卡无法监听到和电量屏幕视为本case2通过,测试完毕任意键继续";
		gui.cls_show_msg(mCaseTip);

		
		for (int i:registerNum) {
			JniNdk.JNI_SYSUnRegisterEvent(i);
			SystemClock.sleep(100);
		}
		
		Tools.setSreenTimeout(myactivity, screenTimeOut);
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", funcName);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
