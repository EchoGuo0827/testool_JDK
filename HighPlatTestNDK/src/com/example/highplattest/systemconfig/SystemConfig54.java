package com.example.highplattest.systemconfig;

import java.util.Random;

import android.newland.SettingsManager;
import android.newland.content.NlContext;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig54.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180111 
 * directory 		: 
 * description 		: 设置系统设置项的值（美团需求）
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180111	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig54 extends UnitFragment {
	private final String TESTITEM = "setSystemSetting";
	private String fileName=SystemConfig54.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager mSettingsManager;
	
	public void systemconfig54()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N910)
		{
			gui.cls_show_msg1_record(fileName, "systemconfig54", 1, "N910海外不支持power_short_press_status属性");
			return;
		}
		
		mSettingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		int nKeyIn = gui.cls_show_msg("%s\n0.单元测试\n1.power_short_press_status短按电源键测试\n2.异常测试\n", TESTITEM);
		switch (nKeyIn) {
		case '0':
			unitTest();
			break;
			
		case '1':
			shortPowerTest();
			break;
			
		case '2':
			abnormalTest();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}
	}
	
	public void abnormalTest()
	{
		boolean iRet = false;
		// case1:参数异常测试，开发反馈当前仅对null和""参数异常判断20190718
		gui.cls_printf("异常测试1".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("", "1"))==true)// 测试失败返回true
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_printf("异常测试2".getBytes());
		if((iRet = mSettingsManager.setSystemSetting(null, "0"))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_printf("异常测试3".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("setting_short_press_power_key", ""))==true)// 测试失败返回true
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_printf("异常测试4".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("setting_short_press_power_key", null))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
//		gui.cls_printf("异常测试5".getBytes());
//		if((iRet = settings.setSystemSetting("setting_err_press_power_key", "1"))==true)// 测试失败返回true
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		gui.cls_printf("异常测试6".getBytes());
//		if((iRet = settings.setSystemSetting("setting_short_press_power_key", "err test"))==true)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		gui.cls_printf("异常测试7".getBytes());
//		if((iRet = settings.setSystemSetting("err para test", "err para test"))==true)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		gui.cls_printf("异常测试8".getBytes());
//		if((iRet = settings.setSystemSetting("setting_short_press_power_key", "2"))==true)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
	}
	
	public void unitTest()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		
		/*以下为局部变量 */
		boolean iRet = false;
		
		/*以下为函数体*/
		gui.cls_printf((TESTITEM+"测试...").getBytes());
		//case6:电源键长按，power_long_press_enable(默认为1，允许长按，设置0位屏蔽长按)
		gui.cls_printf("电源键长按功能测试".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("power_long_press_enable", "0"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("长按电源键，预期不可跳出菜单，是否可以跳出菜单,是[确认],否[其他]")==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// setSystemSetting系统设置接口修改为重启恢复默认20190718
//		if(gui.cls_show_msg("测试电源键长按功能掉电是否保持，是否立即重启，重启后设备长按电源键不可跳出菜单为测试通过")==ENTER)
//		{
//			Tools.reboot(myactivity);
//		}
		//case7:电源键长按，power_long_press_enable(默认为1，允许长按，设置0位屏蔽长按)
		gui.cls_printf("电源键长按功能测试".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("power_long_press_enable", "1"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("长按电源键，预期可以跳出菜单，是否可以跳出菜单,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
//		if(gui.cls_show_msg("测试电源键长按功能掉电是否保持，是否立即重启，重启后设备长按电源键可以成功跳出菜单为测试通过")==ENTER)
//		{
//			Tools.reboot(myactivity);
//		}
		// case4:音量+按键唤醒系统功能 (1:能唤醒,0:不能唤醒)add by zhengxq 20181221
		gui.cls_printf("音量键唤醒系统功能".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("wakeup_system_enable", "1"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("让屏幕进入休眠,按音量键是否能唤醒设备,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示,休眠时间设为15s后,自动休眠后短按音量键是否可唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
//		if(gui.cls_show_msg("测试短按音量键掉电掉电是否保持，是否立即重启，重启后设备进入休眠短按音量键唤醒设备视为测试通过")==ENTER)
//		{
//			Tools.reboot(myactivity);
//		}
		// case5:音量+按键唤醒系统功能 (1:能唤醒,0:不能唤醒)add by zhengxq 20181221
		gui.cls_printf("音量键屏蔽系统功能".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("wakeup_system_enable", "0"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("让设备进入休眠,按音量键是否不能唤醒设备,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示,休眠时间设为15s后,自动休眠后短按音量键是否无法唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
//		if(gui.cls_show_msg("测试短按音量键掉电掉电是否保持，是否立即重启，重启后设备进入休眠短按音量键无法唤醒设备视为测试通过")==ENTER)
//		{
//			Tools.reboot(myactivity);
//		}
		//case2:设置系统设置项为"1"，应能屏蔽短按电源键进入休眠的功能，长按键的功能仍应有效
		gui.cls_printf("设置系统设置项为屏蔽".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("setting_short_press_power_key", "1"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("短按电源键是否不进入休眠并且长按电源键可调出相应菜单，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示，休眠时间设为15s后，自动休眠后短按电源键是否可唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
//		if(gui.cls_show_msg("测试短按电源键掉电是否保持，是否立即重启，重启后短按电源键未进入休眠视为测试通过")==ENTER)
//		{
//			Tools.reboot(myactivity);
//		}
		// case3:设置系统设置项为"0"，短按电源键应能进入休眠，长按键的功能也应生效
		gui.cls_printf("设置系统设置项目未屏蔽".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("setting_short_press_power_key", "0"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("短按电源键是否进入休眠并且长按电源键可调出相应菜单，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示，休眠时间设为15s后，自动休眠后短按电源键是否可唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
//		if(gui.cls_show_msg("测试短按电源键掉电掉电是否保持，是否立即重启，重启后短按电源键进入休眠视为测试通过")==ENTER)
//		{
//			Tools.reboot(myactivity);
//		}
		gui.cls_show_msg1_record(fileName,"systemconfig54",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	public void shortPowerTest()
	{
		String funcName = "shortPowerTest";
		boolean iRet = false;
		String randValue = new Random().nextBoolean()==true?"1":"0";
		// case1.1:电源键短按，power_short_press_status控制电源键短按休眠，设置为1时开启电源键短按休眠
		gui.cls_printf("设置系统设置项未屏蔽".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("power_short_press_status", "1"))==false)
		{
			gui.cls_show_msg1_record(fileName,"",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("短按电源键是否进入休眠，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示，休眠时间设为15s后，自动休眠后短按电源键是否可唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.2:设置为0时屏蔽电源键短按休眠
		gui.cls_printf("设置系统设置项屏蔽".getBytes());
		if((iRet = mSettingsManager.setSystemSetting("power_short_press_status", "0"))==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("短按电源键是否不进入休眠，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-显示，休眠时间设为15s后，自动休眠后短按电源键是否可唤醒屏幕")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:重启电源键能够正常使用
		if((iRet = mSettingsManager.setSystemSetting("power_short_press_status", randValue))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("设置的值设置为%s,按[确认]键设备即将重启,[其他]跳过本测试项,重启后电源键能够正常使用视为测试通过",randValue.equals("1")?"开启短按电源键":"屏蔽短按电源键")==ENTER)
			Tools.reboot(myactivity);
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
