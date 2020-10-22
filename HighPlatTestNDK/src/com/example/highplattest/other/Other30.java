package com.example.highplattest.other;

import android.content.Intent;
import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 系统相关
 * file name 		: Sys1.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170330
 * directory 		: 
 * description 		: 浅休眠广播唤醒
 * related document :
 * history 		 	: author			date			remarks
 *			  		 wangxy		   	20170330	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other30 extends UnitFragment {
	private final String TESTITEM = "浅休眠唤醒";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Other30.class.getSimpleName();
	
	public void other30() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"other30",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		SettingsManager settingsManager;
		boolean ret;
		int total = 60;
		Intent intent = new Intent("com.android.newland.wakeupscreen");
		try {
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"other30",gScreenTime, "line %d:抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
			return;
		}
		// 打印使设备调用k21端，一直处于浅休眠中
		gui.cls_show_msg("请在自检中进行无限循环打印后进入该用例，完成任意键继续");

		// case1.1设置休眠时间1分钟，自动进入浅休眠后，广播唤醒屏幕
		if(gui.cls_show_msg1(gKeepTimeErr, "设置休眠时间1分钟，自动进入浅休眠后，广播唤醒屏幕，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg1(gKeepTimeErr, "休眠时间将设置为1分钟，1分钟后将休眠");
		if ((ret = settingsManager.setScreenTimeout(ONE_MIN)) == false) {
			gui.cls_show_msg1_record(fileName,"sys1",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gKeepTimeErr, "设备进入休眠后10s内，请观察屏幕是否自动唤醒，如果未自动唤醒，请手动唤醒");
		total = 60;
		while (true) {
			if (total == 0) {

				// 进入休眠
				break;
			} else {
				 gui.cls_show_msg1(2, "设备进入休眠后10s内，请观察屏幕是否自动唤醒，如果未自动唤醒，请手动唤醒");
				total = total - 2;
			}
		}
		// 发送广播唤醒屏幕
		myactivity.sendBroadcast(intent);
		if (gui.ShowMessageBox("是否在10s内自动唤醒".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"other30",gKeepTimeErr, "line %d:浅休眠时模拟开关机键唤醒屏幕失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}

		// case 1.2 在浅休眠时，未唤醒屏幕，不应唤醒
		if(gui.cls_show_msg1(gKeepTimeErr, "设置休眠时间1分钟，自动进入浅休眠后，未广播唤醒屏幕，不应唤醒，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg1(gKeepTimeErr, "休眠时间设置为1分钟，1分钟后将休眠");
		if ((ret = settingsManager.setScreenTimeout(ONE_MIN)) == false) {
			gui.cls_show_msg1_record(fileName,"sys1",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gKeepTimeErr, "设备休眠1min后，请观察屏幕是否自动唤醒，如果未自动唤醒，请手动唤醒");
		total = 120;
		while (true) {
			if (total == 0) {
				break;
			} else {
				 gui.cls_show_msg1(2,"设备休眠1min后，请观察屏幕是否自动唤醒，如果未自动唤醒，请手动唤醒");
				total = total - 2;
			}
		}
		if (gui.ShowMessageBox("是否在休眠1min后自动唤醒".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"other30",gKeepTimeErr, "line %d:未进行浅休眠屏幕唤醒操作，如自动唤醒则异常", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 停止自检打印，可成功进入深休眠
		gui.cls_show_msg("请停止自检中的无线循环打印后继续该用例，完成任意键继续");

	/*暂时无法构造深休眠，深休眠时，代码无法继续向下执行
	 * 	// case 2.1 手动进入休眠，等待2分钟进入深休眠，进行广播屏幕唤醒，不应唤醒
		if(gui.cls_show_msg(2, "手动进入休眠，等待2分钟进入深休眠，进行广播屏幕唤醒，不应唤醒，退出键退出测试")==true)
		{
			unitEnd();
			return;
		}
		gui.cls_show_msg(2, "请手动进入休眠，等待2分钟后进入深休眠，进行广播屏幕唤醒，不应唤醒");
		show_flag(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, "请手动进入休眠，休眠2min后，如果未自动唤醒，请手动唤醒");
		total = 120;
		while (true) {
			if (total == 0) {
				// 进入深休眠
				break;
			} else {
				 gui.cls_show_msg(2,"请手动进入休眠，等待2分钟后进入深休眠，进行广播屏幕唤醒，不应唤醒");
				total = total - 2;
				Log.v("wangxiaoyu", "total="+total);
			}
		}
		// 发送广播唤醒屏幕
		myactivity.sendBroadcast(intent);
		Log.v("wangxiaoyu", "计时结束");
		show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "是否在休眠2min后自动唤醒");
		if (GlobalVariable.FLAG_SYSTEM_SIGN == true) {
			gui.cls_show_msg1_record(fileName,"sys1",gKeepTimeErr, "line %d:深休眠进行广播屏幕唤醒不应成功，如自动唤醒则异常", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}

		// case2.2 手动进入休眠，等待2分钟进入深休眠，不进行广播屏幕唤醒，不应唤醒
		if(gui.cls_show_msg(2, "手动进入休眠，等待2分钟进入深休眠，不进行广播屏幕唤醒，不应唤醒，退出键退出测试")==true)
		{
			unitEnd();
			return;
		}
		gui.cls_show_msg(2, "手动进入休眠，等待2分钟进入深休眠，不进行广播屏幕唤醒，不应唤醒");
		show_flag(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, "请手动进入休眠，观察进入休眠后2分钟后，设备是否自动唤醒屏幕，如果未自动唤醒，请手动唤醒");
		total = 120;
		while (true) {
			if (total == 0) {
				// 进入深休眠
				break;
			} else {

				 gui.cls_show_msg(2,"手动进入休眠，等待2分钟进入深休眠，不进行广播屏幕唤醒，不应唤醒");
				 total = total - 2;
			}
		}
		
		show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "在2min后是否自动唤醒屏幕");
		if (GlobalVariable.FLAG_SYSTEM_SIGN == true) {
			gui.cls_show_msg1_record(fileName,"sys1",gKeepTimeErr, "line %d:深休眠不进行广播屏幕唤醒不应成功，如自动唤醒则异常", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}*/

		// case3.1 设备处于亮屏状态，广播唤醒屏幕，屏幕不应发生变化且无其他异常
		if(gui.cls_show_msg1(gKeepTimeErr, "设备处于亮屏状态，广播唤醒屏幕，屏幕不应发生变化且无其他异常，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg1(gKeepTimeErr, "设备处于亮屏状态，广播唤醒屏幕，屏幕不应发生变化且无其他异常");
		gui.cls_show_msg("请手动设置永不休眠，完成任意键继续");
		
		// 发送广播唤醒屏幕
		myactivity.sendBroadcast(intent);
		if (gui.ShowMessageBox("观察屏幕2min，屏幕是否发生变化或其他异常".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"other30",gKeepTimeErr, "line %d:亮屏状态进行广播屏幕唤醒不应发生变化且无其他异常", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}

		// case3.2 设备处于亮屏状态，不唤醒屏幕，屏幕不应发生变化且无其他异常
		if(gui.cls_show_msg1(gKeepTimeErr, "设备处于亮屏状态，不进行广播唤醒屏幕，屏幕不应发生变化且无其他异常，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg1(gKeepTimeErr, "设备处于亮屏状态，不进行广播唤醒屏幕，屏幕不应发生变化且无其他异常");
		gui.cls_show_msg("请手动设置永不休眠，完成任意键继续");
		
		if (gui.ShowMessageBox("观察屏幕2min，屏幕是否发生变化或其他异常".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"other30",gKeepTimeErr, "line %d:亮屏状态不进行广播屏幕唤醒不应发生变化且无其他异常", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}

		
		
		// 打印使设备调用k21端，一直处于浅休眠中
		gui.cls_show_msg("请在自检中进行无限循环打印后，继续该用例，完成任意键继续");
				
		// case4 休眠锁，暂时用k21端使之一直处于浅休眠
		if(gui.cls_show_msg1(2, "设备长时间处于浅休眠状态，进行广播唤醒屏幕，应成功，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg("点击任意键后，请手动进入休眠");
		total = 300;
		while (true) {
			if (total == 0) {
				// 进入深休眠
				break;
			} else {
				gui.cls_show_msg1(2,"请手动进入休眠，打印使设备一直处于浅休眠，5min后，进行广播屏幕唤醒，应唤醒成功");
				total = total - 2;
			}
		}
				
		// 发送广播唤醒屏幕
		myactivity.sendBroadcast(intent);
		if (gui.ShowMessageBox("在5min后是否自动唤醒屏幕".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"other30",gKeepTimeErr, "line %d:长时间处于浅休眠后，进行广播屏幕唤醒应成功，如未自动唤醒则异常", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 停止自检打印
		gui.cls_show_msg("请停止自检中的无线循环打印后继续该用例，完成任意键继续");
		gui.cls_show_msg1_record(fileName,"other30",gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
