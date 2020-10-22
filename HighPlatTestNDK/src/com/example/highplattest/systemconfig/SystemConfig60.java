package com.example.highplattest.systemconfig;

import java.util.Random;

import android.newland.SettingsManager;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 屏蔽长按电源键功能
 * file name 		: SystemConfig60.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190710
 * directory 		: 
 * description 		: 设置电源键长按关机是否生效
 * related document : 
 * history 		 	: author			date			remarks
 * 					 	zsh		   20190710	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 **************************************************s**********************/
public class SystemConfig60 extends UnitFragment 
{
	private final String TESTITEM = "setpowerLongPressStatus设置屏蔽长按电源键功能(海外)";
	private String fileName = SystemConfig60.class.getName();
	private Gui gui = new Gui(myactivity, handler);
	boolean ret=false;//当前电源键的状态
	private SettingsManager mSettingsManager;
	
	public void systemconfig60(){
		
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.overseas)
		{
			gui.cls_show_msg("非海外固件不支持该用例，任意键退出");
			return;
		}
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N910)
		{
			gui.cls_show_msg1_record(fileName, "systemconfig60", 1, "N910海外不支持setpowerLongPressStatus");
			return;
		}
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("%s\n0.长按关机选项测试\n1.恢复关机选项显示",TESTITEM);
			mSettingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			switch (returnValue) 
			{
			case '0':
				powerLongPressTest();
				break;
			case '1':
				recoverLongPress();
				break;		
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	public void powerLongPressTest(){
		/*//case1:前置测试:默认状态下,长按应生效,调用过这个接口会导致误判
		gui.cls_show_msg1(2,"case1:前置测试:默认状态下,长按应生效");
		if(gui.cls_show_msg("长按电源键3s,是否显示关机选项,是[确定],否[其他]")!=ENTER){
			gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败,默认状态下无关机选项",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=mSettingsManager.getpowerLongPressstatus())!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,当前电源键长按关机的状态为:%s",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		//case2:正常流程测试
		//设置为不显示
		gui.cls_show_msg1(1,"case2:正常流程测试,设置为不显示关机选项");
		if((ret=mSettingsManager.setpowerLongPressStatus(false))!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:设置电源键屏蔽失败(ret=%s)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("长按电源键3s,是否显示关机选项,是[确定],否[其他]")==ENTER){
			gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败,应不显示关机菜单",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=mSettingsManager.getpowerLongPressstatus())!=false){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败(预期=false,实际=%s)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//设置为显示
		gui.cls_show_msg1(1,"case2:正常流程测试,设置为显示关机选项");
		if((ret=mSettingsManager.setpowerLongPressStatus(true))!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,解除电源键长按屏蔽失败(ret=%s)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("长按电源键3s,是否显示关机选项,是[确定],否[其他]")!=ENTER){
			gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败,应显示关机选项",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=mSettingsManager.getpowerLongPressstatus())!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败(预期=true,实际=%s)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3:关机菜单设置为不显示重启后,关机菜单仍应为不显示
		gui.cls_show_msg1(1,"case3:重启后,保持当前设置,设置为不显示关机选项");
		if((ret=mSettingsManager.setpowerLongPressStatus(false))!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,设置电源键屏蔽失败(ret=%s)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("长按电源键,是否显示关机选项,是[确定],否[其他]")==ENTER){
			gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败,应不显示关机选项",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=mSettingsManager.getpowerLongPressstatus())!=false){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败(预期=false,实际=%s)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("测试重启后关机选项是否保持不变,若重启后长按电源键关机选项不存在,则测试通过,完成后请进入恢复选项,是否立即重启,是[确认],否[其他]")==ENTER){
			Tools.reboot(myactivity);
		}
		
		// case5:一个线程设置home键和recent_app键生效，一个线程设置长按电源键生效，电源键的设置应生效
		gui.cls_show_msg1(1, "case5:多线程时设置长按电源键应生效");
		boolean enabled = new Random().nextBoolean();// 取boolean的随机值
		testCase5(enabled);
		SystemClock.sleep(2000);
		if(gui.cls_show_msg("长按电源键,是否%s关机选项,是[确定],否[其他]",enabled==true?"显示":"不显示")!=ENTER){
			gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败(预期=%s,实际=%s)",Tools.getLineInfo(),enabled,!enabled);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=mSettingsManager.getpowerLongPressstatus())!=enabled){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,当前电源键长按关机的状态为:%s",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置
		mSettingsManager.setHomeKeyEnabled(true);
		
		// case6:一个线程构造奔溃条件，一个线程设置长按电源键不生效,app奔溃之后长按电源键进行重启操作会起不来？
		if(gui.cls_show_msg("case6:构造app异常奔溃,用例奔溃后测试人员手动长按电源键8秒以上设备应可正常重启,进行app奔溃测试按[确认],跳过本case[其他]")==ENTER)
		{
			enabled = new Random().nextBoolean();// 取boolean的随机值
			testCase6(enabled);
			SystemClock.sleep(2000);
			if(gui.cls_show_msg("长按电源键,是否%s关机选项,是[确定],否[其他]",enabled==true?"显示":"不显示")!=ENTER){
				gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败(预期=%s,实际=%s)",Tools.getLineInfo(),enabled,!enabled);
				if (!GlobalVariable.isContinue)
					return;
			}
			if((ret=mSettingsManager.getpowerLongPressstatus())!=enabled){
				gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,当前电源键长按关机的状态为:%s",Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		// case4:屏蔽长按关进选项菜单，原有的长按8秒的重启机制仍应存在
		gui.cls_show_msg1(2, "case4:8秒重启机制测试(已测试过本case可忽略),请长按电源键8秒以上，预期设备会自动重启，设备自动重启视为本case测试通过，否则视为本case测试不通过");
		
	}
	
	// 一个线程设置home键和recent_app键生效，一个线程设置长按电源键生效，均应成功
	private void testCase5(final boolean is_true)
	{
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				boolean enabled = new Random().nextBoolean();// 取boolean的随机值
				mSettingsManager.setHomeKeyEnabled(enabled);
				mSettingsManager.setMenuKeyValue(82);
			}
		}.start();
		
		new Thread()
		{
			public void run() 
			{
				mSettingsManager.setpowerLongPressStatus(is_true);
			};
		}.start();
	}
	
	// 一个线程构造奔溃条件，一个线程设置长按电源键不生效
	private void testCase6(final boolean is_true)
	{
		new Thread()
		{
			public void run() 
			{
				mSettingsManager.setpowerLongPressStatus(is_true);
			};
		}.start();
		
		new Thread()// 会奔溃的case
		{
			public void run() 
			{
				int x=5/0;
			};
		}.start();
	}
	
	private void recoverLongPress(){
		if((ret=mSettingsManager.setpowerLongPressStatus(true))!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,解除电源键屏蔽失败",Tools.getLineInfo());
			return;
		}
		if(gui.cls_show_msg("长按电源键3s,是否显示关机选项,是[确定],否[其他]")!=ENTER){
			gui.cls_show_msg1_record(TESTITEM,fileName,gKeepTimeErr, "line %d:测试失败,应显示关机选项",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=mSettingsManager.getpowerLongPressstatus())!=true){
			gui.cls_show_msg1_record(TESTITEM,fileName,gScreenTime, "line %d:测试失败,当前电源键长按关机的状态为:%s",Tools.getLineInfo(),ret);
			return;
		}
		if(gui.cls_show_msg("关机选项已恢复,若重启后长按电源键3s关机选项存在,则测试通过,是否立即重启,是[确认],否[其他]")==ENTER){
			Tools.reboot(myactivity);
		}
		return;
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
