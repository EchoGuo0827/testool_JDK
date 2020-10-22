package com.example.highplattest.systemconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Build;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig65.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 
 * directory 		: 设置和获取系统属性
 * description 		: 
 * related document : 
 * history 		 	: author			    date			      remarks
 *			  		           weimj     20191220	 		created
 * history 		 	: 变更点																			变更人员			变更时间
 * 					将原systemconfig64案例的setting_disable_wifi属性设置搬移到systemconfig65		    	陈丁			     20200604
 * 					将原systemconfig67案例的setting_home_double_click_enable属性设置搬移到systemconfig65
 * 					将原systemconfig69案例的获取系统数据库属性(F7)搬移到systemconfig65
 * 					将原systemconfig54案例 设置系统设置项的值（美团需求）搬移到systemconfig65													
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig65 extends UnitFragment{
	
	private final String TESTITEM = "设置和获取系统属性";
	private String fileName = "SystemConfig65";
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager settingsManager = null;
	private int mKey;
	public void systemconfig65(){
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		int currentPage=1;
		int key=47;
		while(true)
		{
			
			switch (currentPage){
			case 1:
				key=gui.cls_show_msg("系统属性(任意键继续)\n" 
						+"0.全部属性测试\t" 
						+"1.设置系统的屏幕亮度\n" 
						+"2.设置设备休眠时间\t" 
						+"3.设置系统是否对所有待安装的APK进行签名验证\n" 
						+"4.设置系统是否显示“应用”菜单\t" 
						+"5.设置是否显示存储选项\n" 
						+"6.设置是否显示主屏幕选项\t" 
						+"7.设置是否显示VPN选项\n"
						+"8.设置应用中是否支持并显示锁屏选项\t" 
						+"9.设置是否显示显示-壁纸选项(上下可翻页)\n" );
				break;
				
			case 2:
				key=gui.cls_show_msg("系统属性(任意键继续)\n" 
						+"0.设置声音中 媒体音量/铃声音量/设备铃声/默认提示音/拨号键盘提示音/屏幕锁定提示音/触摸提示音/触摸时振动 显示与否\t "
						+"1.设置是否显示备份和重置选项 \n" 
						+"2.设定默认输入法\t"
						+"3.设置是否显示状态栏中电池电量百分比\n"
						+"4.设置应用是否需要通过管理员密码登录来启动\t" 
						+"5.设置是否显示流量使用情况选项\n" 
						+"6.设置是否显示电池选项\t" 
						+"7.设置是否显示辅助功能选项\n"
						+"8.设置是否显示开发者选项选项\t"
						+"9.设置是否显示位置信息选项(上下可翻页)\n" );
				if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
					key= 10 +key;
				break;
				
			case 3:
				key=gui.cls_show_msg("系统属性(任意键继续)\n" 
						+"0.设置是否显示安全选项\t" 
						+"1.设置是否显示打印选项\n"
						+"2.设置全局底部任务键是否有效\t" 
						+"3.设置底部按键的部件\n" 
						+"4.设置默认Launcher\t"
						+"5.设置模拟蓝牙打印功能开启/关闭\n"
						+"6.设置关于设备是否显示处理器信息\t"
						+"7.设置设备是否深度休眠\n" 
						+"8.设置设置应用中的语言种类\t"
						+"9.设置关于设备是否显示系统更新功能即OTA升级功能(上下可翻页)\n");
				if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
					key= 20 +key;
				break;
				
			case 4:
				key=gui.cls_show_msg("系统属性(任意键继续)\n" 
						+"0.设置声音-其他提示音选项显示与否\t"
						+"1.设置语言与输入法-拼写检查工具选项显示与否\n"
						+"2.设置语言与输入法-个人字典选项显示与否\t"
						+"3.设置状态栏是否开启下拉\n"
						+"4.设置状态栏是否显示ADB调试信息\t"
						+"5.设置关于设备中 状态信息/法律信息/内核版本/Bootloader版本/基带版本 显示与否\n"
						+"6.设置状态栏的设置快捷键是否显示\t"
						+"7.设置开启热点界面\n"
						+"8.设置wifi高级选项是否显示安装证书\t"
						+"9.设置禁止蓝牙传输文件(上下可翻页)\n" );
				
				if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
					key= 30 +key;
				break;
				
			case 5:
				key=gui.cls_show_msg("系统属性(任意键继续)\n" 
						+"0.设置设置中支付证书升级功能是否显示\t" 
						+"1.设置当前设备安装APP时使用的签名验证方案\n"
						+"2.设置HOME键是否有效\t"
						+"3.设置是产品型号\n"
						+"4.自定义菜单键键值\t"
						+"5.设置屏蔽电源键长按\n"
						+"6.设置、获取 OTG属性\n"
						+"7.控制是否使用wifi\n"
						+"8.Home键双击功能\n"
						+"9.F7数据库属性获取(上下可翻页)\n");

				if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
					key= 40 +key;
				break;
			case 6:
				key=gui.cls_show_msg("系统属性(任意键继续)\n" 
						+"0.设置设置中支付证书升级功能是否显示\n");

				if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
					key= 50 +key;
				break;
				
			}


			
			switch (key) 
			{
			case '0':
				conf01();
				conf02();
				conf03();
				conf04();
				conf05();
				conf06();
				conf07();
				conf08();
				conf09();
				conf10();
				conf11();
				conf12();
				conf13();
				conf14();
				conf15();
				conf16();
				conf17();
				conf18();
				conf19();
				conf20();
				conf21();
				conf22();
				conf23();
				conf24();
				conf25();
				conf26();
				conf27();
				conf28();
				conf29();
				conf30();
				conf31();
				conf32();
				conf33();
				conf34();
				conf35();
				conf36();
				conf37();
				conf38();
				conf39();
				conf40();
				conf41();
				conf42();
				conf43();
				conf44();
				conf45();
				conf46();
				break;
				
			case '1':
				conf01();
				break;
				
			case '2':
				conf02();
				break;
				
			case '3':
				conf03();
				break;
				
			case '4':
				conf04();
				break;
				
			case '5':
				conf05();
				break;
				
			case '6':
				conf06();
				break;
				
			case '7':
				conf07();
				break;
				
			case '8':
				conf08();
				break;
				
			case '9':
				conf09();
				break;
				
			case 0x3A:
				conf10();
				break;
				
			case 0x3B:
				conf11();
				break;
				
			case 0x3C:
				conf12();
				break;
				
			case 0x3D:
				conf13();
				break;
				
			case 0x3E:
				conf14();
				break;
				
			case 0x3F:
				conf15();
				break;
				
			case 0x40:
				conf16();
				break;
				
			case 0x41:
				conf17();
				break;
				
			case 0x42:
				conf18();
				break;
				
			case 0x43:
				conf19();
				break;
				
			case 0x44:
				conf20();
				break;
				
			case 0x45:
				conf21();
				break;
				
			case 0x46:
				conf22();
				break;
				
			case 0x47:
				conf23();
				break;
				
			case 0x48:
				conf24();
				break;
				
			case 0x49:
				conf25();
				break;
				
			case 0x4A:
				conf26();
				break;
				
			case 0x4B:
				conf27();
				break;
				
			case 0x4C:
				conf28();
				break;
				
			case 0x4D:
				conf29();
				break;
				
			case 0x4E:
				conf30();
				break;
			
			case 0x4F:
				conf31();
				break;
				
			case 0x50:
				conf32();
				break;
				
			case 0x51:
				conf33();
				break;
				
			case 0x52:
				conf34();
				break;
				
			case 0x53:
				conf35();
				break;
				
			case 0x54:
				conf36();
				break;
				
			case 0x55:
				conf37();
				break;
				
			case 0x56:
				conf38();
				break;
				
			case 0x57:
				conf39();
				break;
				
			case 0x58:
				conf40();
				break;
				
			case 0x59:
				conf41();
				break;
				
			case 0x5A:
				conf42();
				break;
				
			case 0x5B:
				conf43();
				break;
				
			case 0x5C:
				conf44();
				break;
				
			case 0x5D:
				conf45();
				break;
				
			case 0x5E:
				conf46();
				break;
				
			case 0x5F:
				conf47();
				break;
				
			case 0x60:
				conf48();
				break;
			case 0x61:
				conf49();
				break;
			case 0x62:
				conf50();
				break;
				

			case ESC:
				unitEnd();
				return;
				
			case KEY_DOWN:
				if(currentPage++>=5)
					currentPage=1;
				break;
			case KEY_UP:
				if(currentPage--<=1)
					currentPage=5;
				break;
			}
		}
	}
	//设置系统设置项的值（美团需求） by chending 20200604
	private void conf50() {
		// TODO Auto-generated method stub
		gui.cls_show_msg1(gScreenTime, "设置系统设置项的值（美团需求)");
		//异常测试
		boolean iRet = false;
		// case1:参数异常测试，开发反馈当前仅对null和""参数异常判断20190718
		gui.cls_printf("异常测试1".getBytes());
		if((iRet = settingsManager.setSystemSetting("", "1"))==true)// 测试失败返回true
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_printf("异常测试2".getBytes());
		if((iRet = settingsManager.setSystemSetting(null, "0"))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_printf("异常测试3".getBytes());
		if((iRet = settingsManager.setSystemSetting("setting_short_press_power_key", ""))==true)// 测试失败返回true
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_printf("异常测试4".getBytes());
		if((iRet = settingsManager.setSystemSetting("setting_short_press_power_key", null))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s参数异常测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		//单元测试
		//case6:电源键长按，power_long_press_enable(默认为1，允许长按，设置0位屏蔽长按)
				gui.cls_printf("电源键长按功能测试".getBytes());
				if((iRet = settingsManager.setSystemSetting("power_long_press_enable", "0"))==false)
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
//				if(gui.cls_show_msg("测试电源键长按功能掉电是否保持，是否立即重启，重启后设备长按电源键不可跳出菜单为测试通过")==ENTER)
//				{
//					Tools.reboot(myactivity);
//				}
				//case7:电源键长按，power_long_press_enable(默认为1，允许长按，设置0位屏蔽长按)
				gui.cls_printf("电源键长按功能测试".getBytes());
				if((iRet = settingsManager.setSystemSetting("power_long_press_enable", "1"))==false)
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
//				if(gui.cls_show_msg("测试电源键长按功能掉电是否保持，是否立即重启，重启后设备长按电源键可以成功跳出菜单为测试通过")==ENTER)
//				{
//					Tools.reboot(myactivity);
//				}
				// case4:音量+按键唤醒系统功能 (1:能唤醒,0:不能唤醒)add by zhengxq 20181221
				gui.cls_printf("音量键唤醒系统功能".getBytes());
				if((iRet = settingsManager.setSystemSetting("wakeup_system_enable", "1"))==false)
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
//				if(gui.cls_show_msg("测试短按音量键掉电掉电是否保持，是否立即重启，重启后设备进入休眠短按音量键唤醒设备视为测试通过")==ENTER)
//				{
//					Tools.reboot(myactivity);
//				}
				// case5:音量+按键唤醒系统功能 (1:能唤醒,0:不能唤醒)add by zhengxq 20181221
				gui.cls_printf("音量键屏蔽系统功能".getBytes());
				if((iRet = settingsManager.setSystemSetting("wakeup_system_enable", "0"))==false)
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
//				if(gui.cls_show_msg("测试短按音量键掉电掉电是否保持，是否立即重启，重启后设备进入休眠短按音量键无法唤醒设备视为测试通过")==ENTER)
//				{
//					Tools.reboot(myactivity);
//				}
				//case2:设置系统设置项为"1"，应能屏蔽短按电源键进入休眠的功能，长按键的功能仍应有效
				gui.cls_printf("设置系统设置项为屏蔽".getBytes());
				if((iRet = settingsManager.setSystemSetting("setting_short_press_power_key", "1"))==false)
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
//				if(gui.cls_show_msg("测试短按电源键掉电是否保持，是否立即重启，重启后短按电源键未进入休眠视为测试通过")==ENTER)
//				{
//					Tools.reboot(myactivity);
//				}
				// case3:设置系统设置项为"0"，短按电源键应能进入休眠，长按键的功能也应生效
				gui.cls_printf("设置系统设置项目未屏蔽".getBytes());
				if((iRet = settingsManager.setSystemSetting("setting_short_press_power_key", "0"))==false)
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
			//短按测试
				String funcName = "shortPowerTest";
				String randValue = new Random().nextBoolean()==true?"1":"0";
				// case1.1:电源键短按，power_short_press_status控制电源键短按休眠，设置为1时开启电源键短按休眠
				gui.cls_printf("设置系统设置项未屏蔽".getBytes());
				if((iRet = settingsManager.setSystemSetting("power_short_press_status", "1"))==false)
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
				if((iRet = settingsManager.setSystemSetting("power_short_press_status", "0"))==false)
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
				if((iRet = settingsManager.setSystemSetting("power_short_press_status", randValue))==false)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig54",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
				if(gui.cls_show_msg("设置的值设置为%s,按[确认]键设备即将重启,[其他]跳过本测试项,重启后电源键能够正常使用视为测试通过",randValue.equals("1")?"开启短按电源键":"屏蔽短按电源键")==ENTER)
					Tools.reboot(myactivity);
		
		
		
		
	}
	//F7 数据库属性获取 by chending 20200604
	private void conf49() {
		// TODO Auto-generated method stub
		gui.cls_show_msg1(gScreenTime, "测试F7 数据库属性获取。。。");
		String value = null;
		String[] key = {
				"accelerometer_rotation",
				"alarm_alert",
				"alarm_alert_set",
				"dim_screen",
				"dtmf_tone",
				"dtmf_tone_type",
				"haptic_feedback_enabled",
				"hearing_aid",
				"hide_rotation_lock_toggle_for_accessibility",
				"lockscreen.disabled",
				"lockscreen_sounds_enabled",
				"mode_ringer_streams_affected",
				"mute_streams_affected",
				"navigation_bar_auto_hide",
				"navigation_bar_open",
				"notification_light_pulse",
				"notification_sound",
				"notification_sound_set",
				"pci_reboot",
				"pointer_speed",
				"ringtone",
				"ringtone_set",
				"screen_brightness",
				"screen_brightness_for_vr",
				"screen_brightness_mode",
				"screen_off_timeout",
				"setting_simulate_printer",
				"sound_effect_enabled",
				"tty_mode",
				"user_rotation",
				"vibrate_when_ringing",
				"volume_alarm",
				"volume_biuetooth_sco",
				"volume_music",
				"volume_music_usb_headset",
				"volume_notification",
				"volume_ring",
				"volume_system",
				"volume_voice"
		};
		// 获case1:取不存在的属性，应返回unknown
				gui.cls_show_msg1(gScreenTime, "获取不存在的属性，应返回unknown");
				if(!(value =settingsManager.getSystemSetting(null)).equals("unknown")){
					gui.cls_show_msg1_record(fileName,"conf49",gKeepTimeErr, "line %d:%s测试失败(预期=unknown，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
					if (!GlobalVariable.isContinue)
						return;
				}
				
				if(!(value =settingsManager.getSystemSetting("")).equals("unknown")){
					gui.cls_show_msg1_record(fileName,"conf49",gKeepTimeErr, "line %d:%s测试失败(预期=unknown，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
					if (!GlobalVariable.isContinue)
						return;
				}
				
				if(!(value =settingsManager.getSystemSetting("abc123")).equals("unknown")){
					gui.cls_show_msg1_record(fileName,"conf49",gKeepTimeErr, "line %d:%s测试失败(预期=unknown，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
					if (!GlobalVariable.isContinue)
						return;
				}
				
				// case2:随机获取三项系统属性
				gui.cls_show_msg1(gScreenTime, "随机获取三项系统属性");
				Random r = new Random();
				int num = 0;
				for(int i=0 ; i<3 ;  i++)
				{
					num = r.nextInt(key.length);
					gui.cls_show_msg1(gScreenTime, "获取%s属性",key[num]);
					value =settingsManager.getSystemSetting(key[num]);
					gui.cls_show_msg("获取属性值为%s=%s，按任意键继续",key[num],value);
					if(gui.ShowMessageBox("获取的结果是否与adb获取的一致".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
					{
						gui.cls_show_msg1_record(fileName,"conf49",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
						if(!GlobalVariable.isContinue)
							return;
					}
					
				}
				if (gui.cls_show_msg( "是否进行获取所有系统属性测试 是【确定】否【其他】")==BTN_OK) {
					 for (int i = 0; i < key.length; i++) {
						 value =settingsManager.getSystemSetting(key[i]);
						 gui.cls_show_msg1(gScreenTime, "获取属性值为%s=%s",key[i],value);
							if(gui.ShowMessageBox("若显示为unknow,则获取的结果是否与adb获取的一致?".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
							{
								gui.cls_show_msg1_record(fileName,"conf49",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
								if(!GlobalVariable.isContinue)
									return;
							}
					}
				}
				gui.cls_show_msg1_record(fileName,"conf49",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
		
		
	}
	//Home键双击功能
	private void conf48() {
		// TODO Auto-generated method stub
		gui.cls_show_msg1(gScreenTime, "测试home键双击功能。。。");
		if (!(settingsManager.setSystemSetting("setting_home_double_click_enable", "true"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!(settingsManager.getSystemProperty("setting_home_double_click_enable").equals("true"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时Home键双击功能应有效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//禁用
		if (!(settingsManager.setSystemSetting("setting_home_double_click_enable", "false"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!(settingsManager.getSystemProperty("setting_home_double_click_enable").equals("false"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if (gui.cls_show_msg("此时Home键双击功能应无效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//异常参数	
		gui.cls_show_msg1(gScreenTime, "home键双击功能异常参数(传null)。。。");
		if ((settingsManager.setSystemSetting("setting_home_double_click_enable", null))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时Home键双击功能应无效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(gScreenTime, "测试一次组合情况(禁用home键+home键双击)");
		if (!(settingsManager.setSystemSetting("setting_home_double_click_enable", "true"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!(settingsManager.getSystemProperty("setting_home_double_click_enable").equals("true"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!(settingsManager.setSystemSetting("setting_disable_home_key", "1"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!(settingsManager.getSystemProperty("setting_disable_home_key").equals("1"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时Home键应无效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//测试后置。。恢复
		gui.cls_show_msg1(gScreenTime, "测试后置。恢复Home键功能和home键双击");
		if (!(settingsManager.setSystemSetting("setting_home_double_click_enable", "true"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!(settingsManager.setSystemSetting("setting_disable_home_key", "0"))) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时Home键应有效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"conf48",gKeepTimeErr, "测试通过-------");
		
	}

	//控制是否使用wifi by chending 20200604
	private void conf47() {
		// TODO Auto-generated method stub
		boolean iRet = false;

		gui.cls_show_msg1(gScreenTime, "测试控制是否wifi");
		//case1：设置为0.系统设置中隐藏wifi选项
		if (gui.cls_show_msg("是否执行case1-设置为0.系统设置中隐藏wifi选项,是[确认],否[其他]")==ENTER) {
			
	
			if((iRet = settingsManager.setSystemSetting("setting_disable_wifi", "0"))==false)
			{
				gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					return;
			}
			gui.cls_show_msg("重启后生效------重启后预期设置中隐藏wlan选项，且执行高端案例wifi相关案例也无法打开使用wifi");
			gui.cls_show_msg1(3,"请重启------------共4个case");
			return;
		}
			//case2：设置为1.系统设置中开启wifi选项
		if (gui.cls_show_msg("是否执行case2-设置为1.系统设置中开启wifi选项,是[确认],否[其他]")==ENTER) {
			
	
			if((iRet = settingsManager.setSystemSetting("setting_disable_wifi", "1"))==false)
			{
				gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					return;
			}
			gui.cls_show_msg("重启后生效------重启后预期设置中显示wlan选项，且执行高端案例wifi相关案例正常打开使用wifi");
			gui.cls_show_msg1(3,"请重启------------共4个case");
			return;
		}
			//case3:重复进行设置，应该为最后一次设置的值
		if (gui.cls_show_msg("是否执行case3-重复进行设置，应该为最后一次设置的值,是[确认],否[其他]")==ENTER) {
			
	
			if((iRet = settingsManager.setSystemSetting("setting_disable_wifi", "1"))==false)
			{
				gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					return;
			}
			if((iRet = settingsManager.setSystemSetting("setting_disable_wifi", "1"))==false)
			{
				gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					return;
			}
			if((iRet = settingsManager.setSystemSetting("setting_disable_wifi", "0"))==false)
			{
				gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					return;
			}
			gui.cls_show_msg("重启后生效------重启后预期设置中隐藏wlan选项，且执行高端案例wifi相关案例也无法打开使用wifi");
			gui.cls_show_msg1(3,"请重启------------共4个case");
			return;
			
		}
		//测试后置  恢复wifi状态变为显示。
		if (gui.cls_show_msg("是否执行case4-测试后置  恢复wifi状态变为显示,是[确认],否[其他]")==ENTER) {
			if((iRet = settingsManager.setSystemSetting("setting_disable_wifi", "1"))==false)
			{
				gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
					return;
			}
		}
		gui.cls_show_msg1_record(fileName,"conf47",gKeepTimeErr, "测试通过-----请重启。重启后应显示wlan选项且wifi正常使用");
		
	}

	//设置系统的屏幕亮度
	public void conf01(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
			return;
		}
		
		/* private & local definition */
		int lightValue;
		boolean ret = false;
		int middleValue = 0;
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，获取亮度值，测试完成之后还原亮度值
		lightValue = Integer.parseInt(settingsManager.getSystemProperty("screen_brightness"));
		
		// case1:选择一些值进行亮度测试：0-255轮询一遍，应该返回true，可以看到亮度逐渐增强
		gui.cls_show_msg1(1, "0-255轮询一遍，应该返回true，可以看到亮度逐渐增强");
		for (int i = 0; i <= 255; i++) {
			if ((ret = settingsManager.setSystemSetting("screen_brightness",i+"")) == false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			int value = Integer.parseInt(settingsManager.getSystemProperty("screen_brightness"));
			gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "设置的屏幕亮度值=%s,获取的屏幕亮度值=%s", i, value);
			if (value != i) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=%d，实际=%d)", Tools.getLineInfo(), TESTITEM,i,value);
				if (!GlobalVariable.isContinue)
					return;
			}
		}

		/**setSystemSetting接口不支持非null异常参数测试 by 20200319 郑薛晴*/
		// case2:参数不在范围之内，选择 -2 256边界值进行测试，应该返回false
		/*gui.cls_show_msg1(1, "参数不在范围之内，应该返回false");
		if ( !(ret = settingsManager.setSystemSetting("screen_brightness","-2"))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		middleValue =  Integer.parseInt(settingsManager.getSystemProperty("screen_brightness"));
		Log.e("middle",middleValue+"");

		if ((middleValue =  Integer.parseInt(settingsManager.getSystemProperty("screen_brightness"))) != -2) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM,middleValue);
			if (!GlobalVariable.isContinue)
				return;
		}

		if (!(ret = settingsManager.setSystemSetting("screen_brightness","256"))) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		middleValue =  Integer.parseInt(settingsManager.getSystemProperty("screen_brightness"));
		Log.e("middle",middleValue+"");
		
		if ((middleValue = Integer.parseInt(settingsManager.getSystemProperty("screen_brightness"))) != 256) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM,middleValue);
			if (!GlobalVariable.isContinue)
				return;
		}*/
		// 测试后置，重新设置为POS原先的亮度值
		if ((ret = settingsManager.setSystemSetting("screen_brightness",lightValue+"")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设备休眠时间
	public void conf02(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;
		int total = 60,sub=0;
		long startTime = 0;
		int screenOffTime = 0;

		/*process body*/
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		try {
			settingsManager = (SettingsManager) myactivity
					.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1(2, "该用例不支持");
			return;
		}
	
		// case1:设置休眠时间为15s，30s，单位为ms
		int[] timeCase1 = { 15*1000, 30*1000 };
		for (int j = 0; j < timeCase1.length; j++) 
		{
			total = timeCase1[j]/1000;
			gui.cls_show_msg("休眠时间将设置为%ds,点击任意键开始计时",total);
			if ((ret = settingsManager.setSystemSetting("screen_off_timeout",""+timeCase1[j]))==false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
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
					break;
				}
			}
			if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case1.1:setScreenTimeout和安卓原生方法获取到的休眠的数值应该相等
			screenOffTime = Integer.parseInt(settingsManager.getSystemProperty("screen_off_timeout"))/1000;  
			total = timeCase1[j]/1000;
			gui.cls_show_msg1(gScreenTime, "设置的休眠时间=%ds,获取的休眠时间=%ds", total, screenOffTime);
			if (screenOffTime != total) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=%ds，实际=%ds)", Tools.getLineInfo(), TESTITEM,total,screenOffTime);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case2:设置休眠时间为1分钟、5分钟，单位为ms，应该返回true，能够进行相应时间的休眠操作
		int[] timeCase2 = { ONE_MIN, FIVE_MIN };
		total = 60;
		for (int i = 0; i < timeCase2.length; i++) 
		{
			int sleeptime = timeCase2[i]/1000/60;
			gui.ShowMessageBox(String.format("休眠设置为%d分钟，点击任意键后开始计时", sleeptime).getBytes(), (byte) 0, WAITMAXTIME);
			if ((ret = settingsManager.setSystemSetting("screen_off_timeout",""+timeCase2[i]))==false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			} 
			else 
			{
				while (true) 
				{
					int time = sleeptime - sub;
					if(total==0)
					{
						total = 60;
						break;
					}
					else if (sleeptime==1||sub==sleeptime-1) 
					{
						// inputShort中有休眠2S的操作
						gui.cls_show_msg1(2, total+"s后休眠，休眠后短按电源键唤醒");
						total = total -2;
						if(total == 2)
							gui.cls_show_msg1(2, "马上休眠，休眠后短按电源键唤醒");
					}
					else if (sub < sleeptime) 
					{
						startTime = System.currentTimeMillis();
						gui.cls_show_msg1(2, time + "分钟后休眠，休眠后短按电源键唤醒");
						while(Tools.getStopTime(startTime)!=60);
						sub++;
					} 
				}
			
				if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case2.1:setScreenTimeout和安卓原生方法获取到的休眠的数值应该相等
				screenOffTime = Integer.parseInt(settingsManager.getSystemProperty("screen_off_timeout"))/1000/60; 
				sleeptime = timeCase2[i]/1000/60;
				gui.cls_show_msg1(gScreenTime, "设置的休眠时间=%dmin,获取的休眠时间=%dmin", sleeptime, screenOffTime);
				if (screenOffTime != sleeptime) 
				{
					gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=%dmin，实际=%dmin)", Tools.getLineInfo(), TESTITEM,sleeptime,screenOffTime);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
		}

		/**setSystemSetting只对传入的null进行判断即可，其他的没有做限制 by 20200316 zhengxq*/
//		// case3:设置不在范围的休眠时间 -5,7,35，负数应该返回true，正数的休眠时间往在范围的相邻休眠值靠（1分钟、2分钟、5分钟、10分钟、30分钟）
//		if ((ret = settingsManager.setSystemSetting("screen_off_timeout","-5"))==false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}

//		if ((ret = settingsManager.setSystemSetting("screen_off_timeout","7"))==false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}

//		if ((ret = settingsManager.setSystemSetting("screen_off_timeout","35"))==false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		
		// case5:设置为永不休眠放置一分钟后，设置为1分钟不应立马进入休眠，休眠计时为从设置休眠时间的那一瞬间开始计时
		gui.cls_show_msg("设置为永不休眠放置1分钟,点击任意键开始计时");
		if ((ret = settingsManager.setSystemSetting("screen_off_timeout","-1"))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 倒计时操作1分钟后开始设置休眠时间为1分钟
		for (int j = 0; j <= 30; j++) 
		{
			gui.cls_show_msg1(2, "1分钟倒计时，请不要点击屏幕，剩余时间%ds", 60-j*2);
		}
		
		gui.cls_show_msg("休眠时间将设置为1分钟，点击任意键开始计时");
		if ((ret = settingsManager.setSystemSetting("screen_off_timeout",""+1*60*1000))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		while (true) 
		{
			if(total==0)
			{
				total = 60;
				break;
			}
			else
			{
				gui.cls_show_msg1(2, total+"s后休眠，休眠后短按电源键唤醒");
				total = total -2;
				if(total == 2)
					gui.cls_show_msg1(2, "马上休眠，休眠后短按电源键唤醒");
			}
		}
		if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4: 设置为永不休眠-1，POS应该不会进入休眠
		if ((ret = settingsManager.setSystemSetting("screen_off_timeout","-1"))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime,"%s测试通过，默认为永不休眠，后续如果进入休眠则为异常(长按确认键退出测试)", TESTITEM);
	}
	
	//设置系统是否对所有待安装的APK进行签名验证
	public void conf03(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置为不进行签名验证
		settingsManager.setSystemSetting("apk_verify","0");
		
		// case1:设置为1，设置对所有待安装的APK进行签名验证
		gui.cls_show_msg1(1, "设置对所有待安装的APK进行签名验证");
		if ((ret = settingsManager.setSystemSetting("apk_verify","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("apk_verify");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("安装apk，查看是否进行签名验证".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置不对所有待安装的APK进行签名验证
		gui.cls_show_msg1(1, "设置不对所有待安装的APK进行签名验证");
		if ((ret = settingsManager.setSystemSetting("apk_verify","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("apk_verify");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不进行签名验证".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret=settingsManager.setSystemSetting("apk_verify",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if (!(ret = settingsManager.setSystemSetting("apk_verify","")))
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if (!(ret = settingsManager.setSystemSetting("apk_verify","-1"))) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		if(gui.ShowMessageBox("进入设置，查看是否不进行签名验证".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置系统设置是否显示“应用”菜单
	public void conf04(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置系统显示“应用”菜单
		settingsManager.setSystemSetting("setting_app_display","0");
		
		// case1:设置为1，设置系统不显示“应用”菜单
		gui.cls_show_msg1(1, "设置系统不显示“应用”菜单");
		if ((ret = settingsManager.setSystemSetting("setting_app_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_app_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示设置“应用”菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置0，设置系统显示“应用”菜单
		gui.cls_show_msg1(1, "设置系统显示“应用”菜单");

		if ((ret = settingsManager.setSystemSetting("setting_app_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_app_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示设置“应用”菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_app_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if (!(ret = settingsManager.setSystemSetting("setting_app_display",""))) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if (!(ret = settingsManager.setSystemSetting("setting_app_display","-1")) ) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示设置“应用”菜单".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置是否显示存储选项
	public void conf05(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置存储为显示
		settingsManager.setSystemSetting("setting_storage_display","0");
		
		// case1:设置为1，设置不显示存储选项
		gui.cls_show_msg1(1, "设置不显示存储选项");
		if ((ret = settingsManager.setSystemSetting("setting_storage_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_storage_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示存储选项
		gui.cls_show_msg1(1, "设置显示存储选项");
		if ((ret = settingsManager.setSystemSetting("setting_storage_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_storage_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
			
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_storage_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_storage_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_storage_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示存储选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置是否显示主屏幕选项
	public void conf06(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，显示主屏幕选项
		settingsManager.setSystemSetting("setting_home_display","0");
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		gui.cls_show_msg("请先将服务器的adwLauncher的Launcher应用安装到设备上，已安装可忽略，后按【确认】继续");
	
		// case1:设置为1,不显示主屏幕选项，预期应不显示主屏幕选项
		gui.cls_show_msg1(1, "不显示主屏幕选项");
		if ((ret = settingsManager.setSystemSetting("setting_home_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_home_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示主屏幕选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0,显示主屏幕选项，预期应显示主屏幕选项
		gui.cls_show_msg1(1, "显示主屏幕选项");
		if ((ret = settingsManager.setSystemSetting("setting_home_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_home_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示主屏幕选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_home_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_home_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_home_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		if(gui.ShowMessageBox("设置应用中主屏幕选项是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 该选项默认为关闭状态
		settingsManager.setSystemSetting("setting_home_display","1");
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示VPN选项
	public void conf07(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置VPN选项为显示
		settingsManager.setSystemSetting("setting_vpn_display","1");
		
		// case1:设置为1，设置不显示VPN选项
		gui.cls_show_msg1(1, "设置不显示VPN选项");
		if ((ret = settingsManager.setSystemSetting("setting_vpn_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_vpn_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否无VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示VPN选项
		gui.cls_show_msg1(1, "设置显示VPN选项");
		if ((ret = settingsManager.setSystemSetting("setting_vpn_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_vpn_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否有VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_vpn_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_vpn_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_vpn_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否有VPN选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置应用中是否支持并显示锁屏选项
	public void conf08(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置锁屏选项为显示
		settingsManager.setSystemSetting("lockscreen.disabled","0");
		
		// case1:设置为1，设置不显示锁屏选项
		gui.cls_show_msg1(1, "设置不显示锁屏选项");
		if ((ret = settingsManager.setSystemSetting("lockscreen.disabled","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("lockscreen.disabled");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否无锁屏选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示锁屏选项
		gui.cls_show_msg1(1, "设置显示锁屏选项");
		if ((ret = settingsManager.setSystemSetting("lockscreen.disabled","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("lockscreen.disabled");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否有锁屏选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("slockscreen.disabled",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("lockscreen.disabled","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("lockscreen.disabled","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否有锁屏选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置显示-壁纸选项显示与否
	public void conf09(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置壁纸选项为不显示
		settingsManager.setSystemSetting("setting_wallpaper_display","0");
		
		// case1:设置为0，设置不显示壁纸选项
		gui.cls_show_msg1(1, "设置不显示壁纸选项");
		if ((ret = settingsManager.setSystemSetting("setting_wallpaper_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_wallpaper_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示壁纸选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示壁纸选项
		gui.cls_show_msg1(1, "设置显示壁纸选项");
		if ((ret = settingsManager.setSystemSetting("setting_wallpaper_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_wallpaper_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示壁纸选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_wallpaper_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_wallpaper_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_wallpaper_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示壁纸选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置声音中 媒体音量/铃声音量/设备铃声/默认提示音/拨号键盘提示音/屏幕锁定提示音/触摸提示音/触摸时振动 显示与否 
	public void conf10(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		String value ;
		boolean ret = false;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// case0:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试 ");
		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
//		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display",""))==false ) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display","-1"))==false ) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display","111"))==false ) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		
		//case1:只显示媒体音量选项“01111111”
		gui.cls_show_msg1(1, "将只显示声音-媒体音量选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "01111111");

		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("01111111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=01111111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-媒体音量选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:只显示铃声音量选项“10111111”
		gui.cls_show_msg1(1, "将只显示声音-铃声音量选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "10111111");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("10111111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=10111111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-铃声音量选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:只显示设备铃声选项“11011111”
		gui.cls_show_msg1(1, "将只显示声音-设备铃声选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11011111");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11011111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11011111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-设备铃声选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4:只显示默认铃声选项“11101111”
		gui.cls_show_msg1(1, "将只显示声音-默认音量选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11101111");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11101111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11101111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-默认铃声音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5:只显示拨号键盘提示音选项“11110111”
		gui.cls_show_msg1(1, "将只显示声音-拨号键盘提示音选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11110111");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11110111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11110111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-拨号键盘提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case6:只显示屏幕锁定提示音选项“11111011”
		gui.cls_show_msg1(1, "将只显示声音-屏幕锁定提示音选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11111011");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11111011")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11111011，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-屏幕锁定提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case7:只显示触摸提示音选项“11111101”
		gui.cls_show_msg1(1, "将只显示声音-触屏提示音选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11111101");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11111101")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11111101，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-触屏提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case8:只显示触摸时振动选项“11111110”
		gui.cls_show_msg1(1, "将只显示声音-触屏时振动选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11111110");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11111110")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11111110，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否只显示声音-触屏时振动选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case9:关闭所有音量选项
		gui.cls_show_msg1(1, "关闭声音下所有选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "11111111");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("11111111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11111111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否无选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case10:三种组合在一起显示“00011111”
		gui.cls_show_msg1(1, "将显示声音-媒体音量、声音-铃声音量、声音-设备铃声选项");
		settingsManager.setSystemSetting("setting_notification_items_display", "00011111");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("00011111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=00011111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case11:五种组合在一起显示
		gui.cls_show_msg1(1, "将显示声音-媒体音量、铃声音量、设备铃声选项、拨号键盘提示音、屏幕锁定提示音");
		settingsManager.setSystemSetting("setting_notification_items_display", "00010011");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("00010011")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=00010011，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声、拨号键盘提示音、屏幕锁定提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case12:全部选项都显示
		gui.cls_show_msg1(1, "将显示声音-媒体音量、铃声音量、设备铃声选项、默认通知铃声、拨号键盘提示音、屏幕锁定提示音、触摸提示音、触摸时振动");
		settingsManager.setSystemSetting("setting_notification_items_display", "00000000");
		value = settingsManager.getSystemProperty("setting_notification_items_display");
		if (!value.equals("00000000")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=00000000，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声选项、默认通知铃声、拨号键盘提示音、屏幕锁定提示音、触摸提示音、触摸时振动选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "参数异常测试，应显示媒体音量、铃声音量、设备铃声选项、默认通知铃声");
		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display","")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		if ((ret = settingsManager.setSystemSetting("setting_notification_items_display","-1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		 if(gui.ShowMessageBox("音量选项中是否显示了媒体音量、铃声音量、设备铃声选项、默认通知铃声".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);

	}
	
	//设置是否显示备份和重置选项
	public void conf11(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置备份和重置选项为显示
		settingsManager.setSystemSetting("setting_privacy_display","0");
		
		// case1:设置为1，设置不显示备份和重置选项
		gui.cls_show_msg1(1, "设置不显示备份和重置选项");
		if ((ret = settingsManager.setSystemSetting("setting_privacy_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_privacy_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示备份和重置选项
		gui.cls_show_msg1(1, "设置显示备份和重置选项");
		if ((ret = settingsManager.setSystemSetting("setting_privacy_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_privacy_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_privacy_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_privacy_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_privacy_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示备份和重置选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设定默认输入法
	public void conf12(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		String value ;
		boolean ret = false;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		//case1:设置成不存在的输入法(123)
		gui.cls_show_msg1(1, "设置成不存在的输入法(123)");
		if (!(ret = settingsManager.setSystemSetting("default_input_method","123"))) 
		{
			gui.cls_show_msg1_record(fileName, "systemconfig65", gKeepTimeErr,"line:%d,%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 动态获取输入法的服务名
		InputMethodManager mInputMethodManager = (InputMethodManager) myactivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> mInputMethodInfo = mInputMethodManager .getInputMethodList();
		
//		  com.android.inputmethod.latin.LatinIME
//		  com.android.inputmethod.pinyin.PinyinIME
		 
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = mInputMethodInfo.iterator(); iterator.hasNext();) 
		{
		  InputMethodInfo inputMethodInfo = (InputMethodInfo) iterator.next();
		  //获取应用的label标签中文名
		  CharSequence name = inputMethodInfo.loadLabel(myactivity.getPackageManager());
		  LoggerUtil.v("serviceName="+inputMethodInfo.getServiceName());
		  if((ret = settingsManager.setSystemSetting("default_input_method",""+inputMethodInfo.getServiceName()))!= true)
		  {
			  gui.cls_show_msg1_record(fileName, "systemconfig65", gKeepTimeErr,"line:%d %s测试失败(%s,服务名=%s)", Tools.getLineInfo(),TESTITEM,ret,name);
			  if(!GlobalVariable.isContinue)
				  return;
		  }
		  value = settingsManager.getSystemProperty("default_input_method");
		  gui.cls_show_msg1(1, value);
		  String showTip = "输入法将设置成"+name+"，设置-语言和输入法-默认中查看是否一致，查看完成返回本用例，一致【确认】，不一致【取消】";
		  if(gui.ShowMessageBox(showTip.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		  {
			  gui.cls_show_msg1_record(fileName, "systemconfig65", gKeepTimeErr,"line:%d 设置默认输入法失败", Tools.getLineInfo());
			  if(!GlobalVariable.isContinue)
				  return;
		  }
		}

		gui.cls_show_msg1_record(fileName, "systemconfig65", gKeepTimeErr,"%s测试通过", TESTITEM);

	}
	
	//设置是否显示状态栏中电池电量百分比
	public void conf13(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置状态栏中电池电量百分比选项为显示
		settingsManager.setSystemSetting("status_bar_show_battery_percent","true");
		
		// case1:设置为false，设置不显示状态栏中电池电量百分比选项
		gui.cls_show_msg1(1, "设置不显示状态栏中电池电量百分比选项");
		if ((ret = settingsManager.setSystemSetting("status_bar_show_battery_percent","false")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("status_bar_show_battery_percent");
		if (!value.equals("false")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=false，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看状态栏中是否不显示电池电量百分比".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为true，设置显示状态栏中电池电量百分比选项
		gui.cls_show_msg1(1, "设置显示状态栏中电池电量百分比选项");
		if ((ret = settingsManager.setSystemSetting("status_bar_show_battery_percent","true")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("status_bar_show_battery_percent");
		if (!value.equals("true")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=true，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看状态栏中是否显示电池电量百分比".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("status_bar_show_battery_percent",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("status_bar_show_battery_percent","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("status_bar_show_battery_percent","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("查看状态栏中是否显示电池电量百分比".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置应用是否需要通过管理员密码登录来启动
	public void conf14(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;
		String value;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置不需要通过管理员密码登录来启动
		settingsManager.setSystemSetting("setting_apk_need_login","0");
		
		// case1:设置为1，设置需要通过管理员密码登录来启动
		gui.cls_show_msg1(1, "设置管理员登陆密码为00000000");
		settingsManager.setSystemSetting("setting_apk_login_passwd", "00000000");
		value = settingsManager.getSystemProperty("setting_apk_login_passwd");
		if (!value.equals("00000000")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=00000000，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "设置需要通过管理员密码登录来启动");
		if ((ret = settingsManager.setSystemSetting("setting_apk_need_login","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		value = settingsManager.getSystemProperty("setting_apk_need_login");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看是否需要通过管理员密码登录来启动".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("使用管理员登陆密码是否成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置不需要通过管理员密码登录来启动
		gui.cls_show_msg1(1, "设置不需要通过管理员密码登录来启动");
		if ((ret = settingsManager.setSystemSetting("setting_apk_need_login","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_apk_need_login");
		if (!value.equals("0"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看是否不需要通过管理员密码登录来启动".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_apk_need_login",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_apk_need_login","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_apk_need_login","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("查看是否不需要通过管理员密码登录来启动".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示流量使用情况选项
	public void conf15(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置流量使用情况选项为不显示
		settingsManager.setSystemSetting("setting_data_usage_display","1");
		
		// case1:设置为0，设置显示流量使用情况选项
		gui.cls_show_msg1(1, "设置显示流量使用情况选项");
		if ((ret = settingsManager.setSystemSetting("setting_data_usage_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_data_usage_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示流量使用情况选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置不显示流量使用情况选项
		gui.cls_show_msg1(1, "设置不显示流量使用情况选项");
		if ((ret = settingsManager.setSystemSetting("setting_data_usage_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_data_usage_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示流量使用情况选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
			
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_data_usage_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_data_usage_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_data_usage_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示流量使用情况选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，显示流量使用情况选项
		settingsManager.setSystemSetting("setting_data_usage_display","1");
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示电池选项
	public void conf16(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置电池选项为显示
		settingsManager.setSystemSetting("setting_battery_display","0");
		
		// case1:设置为1，设置不显示电池选项
		gui.cls_show_msg1(1, "设置不显示电池选项");
		if ((ret = settingsManager.setSystemSetting("setting_battery_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_battery_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示电池选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示电池选项
		gui.cls_show_msg1(1, "设置显示电池选项");
		if ((ret = settingsManager.setSystemSetting("setting_battery_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_battery_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示电池选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_battery_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_battery_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_battery_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示电池选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示辅助功能选项
	public void conf17(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置辅助功能选项为不显示
		settingsManager.setSystemSetting("setting_accessibility_settings_display","0");
		
		// case1:设置为1，设置不显示辅助功能选项
		gui.cls_show_msg1(1, "设置不显示辅助功能选项");
		if ((ret = settingsManager.setSystemSetting("setting_accessibility_settings_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_accessibility_settings_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示辅助功能选项
		gui.cls_show_msg1(1, "设置显示辅助功能选项");
		if ((ret = settingsManager.setSystemSetting("setting_accessibility_settings_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_accessibility_settings_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_development_settings_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_development_settings_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示辅助功能选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示开发者选项选项
	public void conf18(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置开发者选项选项为显示
		settingsManager.setSystemSetting("setting_development_settings_display","0");
		
		// case1:设置为1，设置不显示开发者选项选项
		gui.cls_show_msg1(1, "设置不显示开发者选项选项");
		if ((ret = settingsManager.setSystemSetting("setting_development_settings_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_development_settings_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示开发者选项选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示开发者选项选项
		gui.cls_show_msg1(1, "设置显示开发者选项选项");
		if ((ret = settingsManager.setSystemSetting("setting_development_settings_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_development_settings_display");
		if (!value.equals("0"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示开发者选项选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_development_settings_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_development_settings_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示开发者选项选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示位置信息选项
	public void conf19(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置位置信息选项为显示
		settingsManager.setSystemSetting("setting_location_settings_display","0");
		
		// case1:设置为1，设置不显示位置信息选项
		gui.cls_show_msg1(1, "设置不显示位置信息选项");
		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_location_settings_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示位置信息选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示位置信息选项
		gui.cls_show_msg1(1, "设置显示位置信息选项");
		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_location_settings_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=%s，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示位置信息选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_location_settings_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示位置信息选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示安全选项
	public void conf20(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置安全选项为显示
		settingsManager.setSystemSetting("setting_security_settings_display","0");
		
		// case1:设置为1，设置不显示安全选项
		gui.cls_show_msg1(1, "设置不显示安全选项");
		if ((ret = settingsManager.setSystemSetting("setting_security_settings_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_security_settings_display");
		if (!value.equals("1"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示安全选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置显示安全选项
		gui.cls_show_msg1(1, "设置显示安全选项");
		if ((ret = settingsManager.setSystemSetting("setting_security_settings_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_security_settings_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示安全选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_security_settings_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_security_settings_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_security_settings_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示安全选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置是否显示打印选项
	public void conf21(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置打印选项为显示
		settingsManager.setSystemSetting("setting_print_settings_display","0");
		
		// case1:设置为1，设置不显示打印选项
		gui.cls_show_msg1(1, "设置不显示打印选项");
		if ((ret = settingsManager.setSystemSetting("setting_print_settings_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_print_settings_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示打印选项
		gui.cls_show_msg1(1, "设置显示打印选项");
		if ((ret = settingsManager.setSystemSetting("setting_print_settings_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_print_settings_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_print_settings_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_print_settings_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_print_settings_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示打印选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置全局底部任务键是否有效
	public void conf22(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置全局底部任务键有效
		settingsManager.setSystemSetting("setting_disable_app_switch_key","true");
		
		// case1:设置为false，设置全局底部任务键无效
		gui.cls_show_msg1(1, "设置全局底部任务键无效");
		if ((ret = settingsManager.setSystemSetting("setting_disable_app_switch_key","false")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_disable_app_switch_key");
		if (!value.equals("false")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=false，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否全局底部任务键无效".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为false，设置全局底部任务键有效
		gui.cls_show_msg1(1, "设置全局底部任务键有效");
		if ((ret = settingsManager.setSystemSetting("setting_disable_app_switch_key","true")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_disable_app_switch_key");
		if ( !value.equals("true") ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=true，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否全局底部任务键有效".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1,"参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_disable_app_switch_key",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_disable_app_switch_key","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		if ((ret = settingsManager.setSystemSetting("setting_disable_app_switch_key","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否全局底部任务键有效".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置底部按键的部件
	public void conf23(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置底部按键的部件在右
		settingsManager.setSystemSetting("relayout_navigation_bar","1");
		
		// case1:设置为0，设置底部按键的部件在左
		gui.cls_show_msg1(1, "设置底部按键的部件在左");
		if ((ret = settingsManager.setSystemSetting("relayout_navigation_bar","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("relayout_navigation_bar");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看是否设置底部按键的部件在左".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置底部按键的部件在右
		gui.cls_show_msg1(1, "设置底部按键的部件在右");
		if ((ret = settingsManager.setSystemSetting("relayout_navigation_bar","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("relayout_navigation_bar");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看是否设置底部按键的部件在右".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("relayout_navigation_bar",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("relayout_navigation_bar","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("relayout_navigation_bar","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("查看是否设置底部按键的部件在左".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	
	//设置设置默认Launcher
	public void conf24(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret = false;
		String value ;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置
		gui.cls_show_msg("测试前请确保安装iLauncher和NovaLauncher，安装完毕点任意键继续");
	
		// case1:设置launcher为null,"",不存在的字符串"com.unexit.name"
		gui.cls_show_msg1(gScreenTime, "参数异常测试");
		settingsManager.setLauncher(null);
		settingsManager.setLauncher("");
		settingsManager.setLauncher("com.unexit.name");
		gui.cls_show_msg("参数异常测试不影响原先Launcher，可进入[设置]-[主屏幕]查看，任意键继续");
		
		// case2.1:设置为其他launcher，点击home键可生效
		gui.cls_show_msg1(gScreenTime, "设置为iLauncher测试");
		if ((ret = settingsManager.setSystemSetting("sys.default_home","com.launcher.il")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("sys.default_home");
		if (!value.equals("com.launcher.il") ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=com.launcher.il，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("目前Launcher是否被设置为iLauncher，可进入[设置]-[主屏幕]查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2.2:重启后仍应为之前的launcher
		if(gui.ShowMessageBox("是否立即重启，[是]重启，[否]不重启，重启后应为iLauncher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);
		
		// case3.1:从其他launcher切换为原生launhcer，重启后生效
		gui.cls_show_msg1(gScreenTime, "设置为原生Laucnher测试");
		if ((ret = settingsManager.setSystemSetting("sys.default_home","com.android.launcher")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("sys.default_home");
		if (!value.equals("com.android.launcher") ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=com.android.launcher，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("目前launcher是否被设置为原生Launcher，可进入[设置]-[主屏幕]查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3.2:重启后仍应为之前的Launcher
		if(gui.ShowMessageBox("是否立即重启，[是]重启，[否]不重启，重启后应为原生Launcher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);
		
		// case4.1:切换为NovaLauncher应成功
		gui.cls_show_msg1(gScreenTime, "设置为NovaLauncher测试");
		if ((ret = settingsManager.setSystemSetting("sys.default_home","com.teslacoilsw.launcher")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("sys.default_home");
		if (!value.equals("com.teslacoilsw.launcher") ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=com.teslacoilsw.launcher，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置Launcher是否被设置为NovaLauncher，可进入【设置】-【主屏幕】查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4.2:重启后仍应为NovaLauncher
		if(gui.ShowMessageBox("是否立即重启，[是]重启，[否]不重启，重启后应为NovaLauncher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);
		
		//后置，恢复原生Launcher
		settingsManager.setSystemSetting("sys.default_home","com.android.launcher");
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置模拟蓝牙打印功能开启/关闭
	public void conf25(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置模拟蓝牙打印功能开启
		settingsManager.setSystemSetting("setting_simulate_printer","true");
		
		// case1:设置为false，设置模拟蓝牙打印功能关闭
		gui.cls_show_msg1(1, "设置模拟蓝牙打印功能关闭");
		if ((ret = settingsManager.setSystemSetting("setting_simulate_printer","false")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_simulate_printer");
		if (!value.equals("false") ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=false，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否模拟蓝牙打印功能关闭".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为true，设置模拟蓝牙打印功能开启
		gui.cls_show_msg1(1, "设置模拟蓝牙打印功能开启");
		if ((ret = settingsManager.setSystemSetting("setting_simulate_printer","true")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_simulate_printer");
		if (!value.equals("true")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=true，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否模拟蓝牙打印功能开启".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_simulate_printer",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_simulate_printer","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_simulate_printer","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否模拟蓝牙打印功能开启".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置关于设备是否显示处理器信息
	public void conf26(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置处理器信息为显示
		settingsManager.setSystemSetting("setting_processor_display","0");
		
		// case1:设置为1，设置不显示处理器信息
		gui.cls_show_msg1(1, "设置不显示处理器信息");

		if ((ret = settingsManager.setSystemSetting("setting_processor_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_processor_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示处理器信息".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示处理器信息
		gui.cls_show_msg1(1, "设置显示处理器信息");
		if ((ret = settingsManager.setSystemSetting("setting_processor_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_processor_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示处理器信息".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1,"参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_processor_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_processor_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_processor_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示处理器信息".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设备是否深度休眠
	public void conf27(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置不深度休眠
		settingsManager.setSystemSetting("persist.sys.deep_sleep","off");
		
		// case1:设置为on，设置深度休眠
		gui.cls_show_msg1(1, "设置深度休眠");
		if ((ret = settingsManager.setSystemSetting("persist.sys.deep_sleep","on")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("persist.sys.deep_sleep");
		if (!value.equals("on")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=on，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否深度休眠".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置不深度休眠
		gui.cls_show_msg1(1, "设置不深度休眠");
		if ((ret = settingsManager.setSystemSetting("persist.sys.deep_sleep","off")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("persist.sys.deep_sleep");
		if (!value.equals("off")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=off，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否无深度休眠".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("persist.sys.deep_sleep",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("persist.sys.deep_sleep","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("persist.sys.deep_sleep","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否深度休眠".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，显示深度休眠
		settingsManager.setSystemSetting("persist.sys.deep_sleep","on");
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置应用中的语言种类
	public void conf28(){
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);

		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.异常测试\n1.设置为简体中文\n2.设置为繁体\n3.设置为日语\n4.设置为韩文\n5.其他测试点", TESTITEM);
			switch (nkeyIn) 
			{
			case '0':
				abnormal();
				break;
				
			case '1':
			case '2':
			case '3':
			case '4':
				setLan(nkeyIn-'1');
				Tools.killPro();
				break;
				
			case '5':
				OtherTest();
				Tools.killPro();
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	private void abnormal()
	{
		boolean flag,flag1;
//		String[] errLan = {"zz","pp","oo","zh"};
		String errLan = "zz;pp;oo;zh";
		gui.cls_printf("参数异常测试，设置-语言和输入法，语言中应包含多种语言".getBytes());
		// case1:参数异常测试，设置为null，以及不存在的字符串，应不影响原先添加的语言
		//根据开发马鑫汶说，设置为null时预期返回true且设置语言栏中的是系统默认语言  
		if((flag = settingsManager.setSystemSetting("setting_locales",null))==false|(flag1 = settingsManager.setSystemSetting("setting_locales",errLan))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s参数异常测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,flag,flag1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置-语言和输入法选项，语言中是否包含多种语言且不会影响到系统设置的默认语言".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s异常测试通过", TESTITEM);
	}
	
	private void setLan(int i)
	{
		boolean flag;
		String[] showLan = {"简体中文","繁体中文","日语","韩语"};
		List<String[]> languages = new ArrayList<String[]>();
		languages.add(new String[]{"zh-CN"});
		languages.add(new String[]{"zh-HK"});
		languages.add(new String[]{"ja-JP"});
		languages.add(new String[]{"ko-KR"});
		// case2:添加中文，应只显示中文
		// case3:添加繁体，应只显示繁体
		// case4:添加日文
		// case5:添加韩文
		gui.cls_show_msg1(1, "设置-语言和输入法，语言中只包含%s",showLan[i]);
		if((flag = settingsManager.setSystemSetting("setting_locales",""+languages.get(i)))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "语言测试为%s,请去设置-语言和输入法中查看,即将杀死进程并非闪退", showLan[i]);
	}
	
	
	private void OtherTest()
	{
		boolean flag;
//		String[] language = {"zh-CN","zh-HK","ja-JP","ko-KR"};
		String language = "zh-CN;zh-HK;ja-JP;ko-KR";
		//增加一种语言
//		String[] fiveLanguage={"zh-CN","zh-HK","ja-JP","ko-KR","zh-TW"};
		String fiveLanguage = "zh-CN;zh-HK;ja-JP;ko-KR;zh-TW";
		
		// case6:添加全部，系统当前的语言是其他语言，则应默认选择添加的第一个语言
		//case6.1:系统当前的语言是其他语言，设置后应变成第一个中文
		if((flag = settingsManager.setSystemSetting("setting_locales",fiveLanguage))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s添加所有语言失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请将系统当前语言设置为除中文、香港、日文、韩文以外的其他语言，按任意键继续");
		gui.cls_show_msg1(1, "设置-语言和输入法，语言中包含中文、香港、日文、韩文");
		if((flag = settingsManager.setSystemSetting("setting_locales",language))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置-语言和输入法选项，语言中包含中文、香港、日文、韩文且系统当前选中的语言为中文,请去设置-语言和输入法中查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case6.2系统中的语言是其中一种语言香港，设置后仍为香港
		gui.cls_show_msg("请将系统当前语言设置为香港，按任意键继续");
		gui.cls_show_msg1(1, "设置-语言和输入法，语言中包含中文、香港、日文、韩文");
		if((flag = settingsManager.setSystemSetting("setting_locales",language))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置-语言和输入法选项，语言中包含中文、香港、日文、韩文且系统当前选中的语言为香港,请去设置-语言和输入法中查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "语言测试为中文、香港、日文、韩文,请去设置-语言和输入法中查看,即将杀死进程并非杀退");
	}
	
	//设置关于设备是否显示系统更新功能即OTA升级功能
	public void conf29(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置：先安装MTMS，在关于设备下显示
		gui.cls_show_msg("请先安装了MTMS应用，安装完毕后重启设备后再进入本用例，任意键继续");
		
		// case1:设置OTA升级选项显示
		gui.cls_show_msg1(1, "将显示关于设备-系统更新选项");
		if((ret = settingsManager.setSystemSetting("setting_ota_update","true")) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置中是否显示关于设备-系统更新选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig31",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置OTA升级选项隐藏
		gui.cls_show_msg1(1, "将隐藏关于设备-系统更新");
		if((ret = settingsManager.setSystemSetting("setting_ota_update","false")) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("设置中是否不显示关于设备-系统更新选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_ota_update",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_ota_update","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_ota_update","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		if(gui.ShowMessageBox("设置中是否显示关于设备-系统更新选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试后置：默认不显示
		settingsManager.setSystemSetting("setting_ota_update","false");
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置声音-其他提示音选项显示与否
	public void conf30(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置声音-其他提示音选项为显示
		settingsManager.setSystemSetting("setting_sound_other_sound_display","0");
		
		// case1:设置为1，设置不显示声音-其他提示音选项
		gui.cls_show_msg1(1, "设置不显示声音-其他提示音选项");
		if ((ret = settingsManager.setSystemSetting("setting_sound_other_sound_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_sound_other_sound_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示声音-其他提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示声音-其他提示音选项
		gui.cls_show_msg1(1, "设置显示声音-其他提示音选项");
		if ((ret = settingsManager.setSystemSetting("setting_sound_other_sound_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_sound_other_sound_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示声音-其他提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_sound_other_sound_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_sound_other_sound_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_sound_other_sound_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示声音-其他提示音选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置语言与输入法-拼写检查工具选项显示与否
	public void conf31(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置语言与输入法-拼写检查工具选项为显示
		settingsManager.setSystemSetting("setting_language_sepll_checker_display","0");
		
		// case1:设置为1，设置不显示语言与输入法-拼写检查工具选项
		gui.cls_show_msg1(1, "设置不显示语言与输入法-拼写检查工具选项");
		if ((ret = settingsManager.setSystemSetting("setting_language_sepll_checker_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_language_sepll_checker_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否有不显示语言与输入法-拼写检查工具选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示语言与输入法-拼写检查工具选项
		gui.cls_show_msg1(1, "设置显示语言与输入法-拼写检查工具选项");
		if ((ret = settingsManager.setSystemSetting("setting_language_sepll_checker_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_language_sepll_checker_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示语言与输入法-拼写检查工具选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_language_sepll_checker_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_language_sepll_checker_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_language_sepll_checker_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否无语言与输入法-拼写检查工具选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置语言与输入法-个人字典选项显示与否
	public void conf32(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置语言与输入法-个人字典选项为显示
		settingsManager.setSystemSetting("setting_language_user_dictionary_display","0");
		
		// case1:设置为1，设置不显示语言与输入法-个人字典选项
		gui.cls_show_msg1(1, "设置不显示语言与输入法-个人字典选项");
		if ((ret = settingsManager.setSystemSetting("setting_language_user_dictionary_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_language_user_dictionary_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否有语言与输入法-个人字典选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示语言与输入法-个人字典选项
		gui.cls_show_msg1(1, "设置显示语言与输入法-个人字典选项");
		if ((ret = settingsManager.setSystemSetting("setting_language_user_dictionary_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_language_user_dictionary_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示语言与输入法-个人字典选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_language_user_dictionary_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_language_user_dictionary_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_language_user_dictionary_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看是否显示语言与输入法-个人字典选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置状态栏是否开启下拉
	public void conf33(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置状态栏开启下拉
		settingsManager.setSystemSetting("status_bar_enabled","0");
		
		// case1:设置为1，设置状态栏禁止下拉
		gui.cls_show_msg1(1, "设置状态栏禁止下拉");
		if ((ret = settingsManager.setSystemSetting("status_bar_enabled","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("status_bar_enabled");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看状态栏是否禁止下拉".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置状态栏开启下拉
		gui.cls_show_msg1(1, "设置状态栏开启下拉");
		if ((ret = settingsManager.setSystemSetting("status_bar_enabled","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("status_bar_enabled");
		if (!value.equals("0"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看状态栏是否开启下拉".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("status_bar_enabled",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("status_bar_enabled","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("status_bar_enabled","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		if(gui.ShowMessageBox("查看状态栏是否开启下拉".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置状态栏是否显示ADB调试信息
	public void conf34(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，开启状态栏下拉功能
//		settingsManager.setStatusBarEnabled(0);
		settingsManager.setSystemSetting("status_bar_enabled","0");
		
		// case1:设置0，ADB开启插入USB线应有相应图标，拔下USB线相应图标应消失
		gui.cls_show_msg1(1, "状态栏ADB调试信息设置为打开");
		if((ret = settingsManager.setSystemSetting("status_bar_adb_notify","0"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:ADB调试信息开启失败ret = %s", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("请插拔USB线，是否为USB线插入状态栏下拉显示已连接USB调试，拔下状态栏未提示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置1，ADB关闭插拔USB线，状态栏通知栏图标均不应显示
		gui.cls_show_msg1(1, "状态栏ADB调试信息设置为关闭");
		if((ret = settingsManager.setSystemSetting("status_bar_adb_notify","1"))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:ADB调试信息开启失败(%s)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if(gui.ShowMessageBox("请插拔USB线，是否为USB线插拔状态栏均不显示已连接USB调试".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("status_bar_adb_notify",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("status_bar_adb_notify","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("status_bar_adb_notify","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("请插拔USB线，是否为USB线插入状态栏下拉显示已连接USB调试，拔下状态栏未提示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置关于设备中 状态信息/法律信息/内核版本/Bootloader版本/基带版本 显示与否
	public void conf35(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		String value ;
		boolean ret = false;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// case1:异常测试 字符串为null
		gui.cls_show_msg1(1, "参数异常测试");
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display",null))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
//		if((ret = settingsManager.setSystemSetting("setting_device_info_items_display",""))==false)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if((ret = settingsManager.setSystemSetting("setting_device_info_items_display","0"))==false)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if((ret = settingsManager.setSystemSetting("setting_device_info_items_display","111"))==false)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		
		// case2:只显示状态信息01111
		gui.cls_show_msg1(1, "将显示关于设备-状态信息，不显示法律信息、内核版本、bootloader版本、基带版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","01111")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("01111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=01111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否只显示状态信息，不显示法律信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:只显示法律信息10111
		gui.cls_show_msg1(1, "将显示关于设备-法律信息，不显示状态信息、内核版本、bootloader版本、基带版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","10111")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("10111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=10111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否只显示法律信息，不显示状态信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4:只显示内核版本
		gui.cls_show_msg1(1, "将显示关于设备-内核版本，不显示状态信息、法律信息、bootloader版本、基带版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","11011")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("11011")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11011，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否只显示内核版本，不显示状态信息、法律信息、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5:只显示bootloader版本
		gui.cls_show_msg1(1, "将显示关于设备-bootloader版本，不显示状态信息、法律信息、内核版本、基带版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","11101")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("11101")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11101，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否只显示bootloader版本，不显示状态信息、法律信息、内核版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case6:只显示基带版本
		gui.cls_show_msg1(1, "将显示关于设备-基带版本，不显示状态信息、法律信息、内核版本、bootloader版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","11110")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("11110")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11110，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否只显示基带版本，不显示状态信息、法律信息、内核版本、bootloader版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case7:两种混合显示，显示基带版本和状态信息
		gui.cls_show_msg1(1, "将显示关于设备-基带版本、状态信息，不显示法律信息、内核版本、bootloader版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","01110")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("01110")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=01110，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否只显示基带版本、状态信息，不显示法律信息、内核版本、bootloader版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case8:全部隐藏
		gui.cls_show_msg1(1, "关于设备不显示状态信息、法律信息、内核版本、bootloader版本、基带版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","11111")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("11111")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=11111，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否不显示状态信息、法律信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case9：全部显示
		gui.cls_show_msg1(1, "关于设备显示状态信息、法律信息、内核版本、bootloader版本、基带版本");
		
		if(ret = settingsManager.setSystemSetting("setting_device_info_items_display","00000")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_device_info_items_display");
		if (!value.equals("00000")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=00000，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("关于设备是否显示状态信息、法律信息、内核版本、bootloader版本、基带版本".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置状态栏的设置快捷键是否显示
	public void conf36(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置状态栏的设置快捷键显示
		settingsManager.setSystemSetting("status_bar_setting_enabled","0");
		
		// case1:设置为1，设置状态栏的设置快捷键不显示
		gui.cls_show_msg1(1, "设置状态栏的设置快捷键不显示");
		if ((ret = settingsManager.setSystemSetting("status_bar_setting_enabled","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("status_bar_setting_enabled");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("查看状态栏的设置快捷键是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置状态栏的设置快捷键显示
		gui.cls_show_msg1(1, "设置状态栏的设置快捷键显示");
		if ((ret = settingsManager.setSystemSetting("status_bar_setting_enabled","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("status_bar_setting_enabled");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=%s，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看状态栏的设置快捷键是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("status_bar_setting_enabled",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("status_bar_setting_enabled","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("status_bar_setting_enabled","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		if(gui.ShowMessageBox("进入设置，查看状态栏的设置快捷键是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	
	//设置开启热点界面
	public void conf37(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置开启热点界面为显示
		settingsManager.setSystemSetting("tether_display","0");
		
		// case1:设置为1，设置不显示开启热点界面
		gui.cls_show_msg1(1, "设置不显示开启热点界面");
		if ((ret = settingsManager.setSystemSetting("tether_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("tether_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否不显示开启热点界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置显示开启热点界面
		gui.cls_show_msg1(1, "设置显示开启热点界面");
		if ((ret = settingsManager.setSystemSetting("tether_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("tether_display");
		if (!value.equals("0"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否显示开启热点界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("tether_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("tether_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("tether_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		
		if(gui.ShowMessageBox("进入设置，查看是否显示开启热点界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置wifi高级选项是否显示安装证书
	public void conf38(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置wifi高级选项显示安装证书
		settingsManager.setSystemSetting("wifi_install_credentials_display","0");
		
		// case1:设置为1，设置wifi高级选项不显示安装证书
		gui.cls_show_msg1(1, "设置wifi高级选项不显示安装证书");
		if ((ret = settingsManager.setSystemSetting("wifi_install_credentials_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("wifi_install_credentials_display");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看wifi高级选项是否不显示安装证书".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置wifi高级选项显示安装证书
		gui.cls_show_msg1(1, "设置wifi高级选项显示安装证书");
		if ((ret = settingsManager.setSystemSetting("wifi_install_credentials_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("wifi_install_credentials_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看wifi高级选项是否显示安装证书".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("wifi_install_credentials_display",null)) ) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("wifi_install_credentials_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("wifi_install_credentials_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		
		if(gui.ShowMessageBox("进入设置，查看wifi高级选项是否显示安装证书".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置禁止蓝牙传输文件
	public void conf39(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置允许蓝牙传输文件
		settingsManager.setSystemSetting("setting_bluetooth_file_transfer","0");
		
		// case1:设置为1，设置禁止蓝牙传输文件
		gui.cls_show_msg1(1, "设置禁止蓝牙传输文件");
		if ((ret = settingsManager.setSystemSetting("setting_bluetooth_file_transfer","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_bluetooth_file_transfer");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否禁止蓝牙传输文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置允许蓝牙传输文件
		gui.cls_show_msg1(1, "设置允许蓝牙传输文件");
		if ((ret = settingsManager.setSystemSetting("setting_bluetooth_file_transfer","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_bluetooth_file_transfer");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否允许蓝牙传输文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_bluetooth_file_transfer",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_bluetooth_file_transfer","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_bluetooth_file_transfer","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		
		if(gui.ShowMessageBox("进入设置，查看是否不允许蓝牙传输文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		settingsManager.setSystemSetting("setting_bluetooth_file_transfer","0");
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置设置中支付证书升级功能是否显示
	public void conf40(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置设置中支付证书升级功能显示
		settingsManager.setSystemSetting("setting_payment_cert_update_display","0");
		
		// case1:设置为1，设置设置中支付证书升级功能不显示
		gui.cls_show_msg1(1, "设置设置中更新支付证书功能不显示");
		if ((ret = settingsManager.setSystemSetting("setting_payment_cert_update_display","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_payment_cert_update_display");
		if (!value.equals("1"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看更新设置中支付证书功能是否不显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为0，设置设置中支付证书升级功能显示
		gui.cls_show_msg1(1, "设置设置中更新支付证书功能显示");
		if ((ret = settingsManager.setSystemSetting("setting_payment_cert_update_display","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_payment_cert_update_display");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看设置中更新支付证书功能是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null
		gui.cls_show_msg1(1,"参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("setting_payment_cert_update_display",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_payment_cert_update_display","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		if ((ret = settingsManager.setSystemSetting("setting_payment_cert_update_display","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		if(gui.ShowMessageBox("进入设置，查看设置中更新支付证书功能是否显示".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	//设置当前设备安装APP时使用的签名验证方案
	public void conf41(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		String value ;
		boolean flag = false;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置
		gui.cls_show_msg("确保本机上没有安装任何证书、卸载所有相关测试apk，用升级工具安装SVN上验签测试apk文件夹下的apk，完成任意键继续");
		//case1:异常测试
		gui.cls_show_msg1(1, "异常测试");
		if((flag = settingsManager.setSystemSetting("setting_app_signature_scheme","err"))!=false){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d: 异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2：设置为newland，使用新大陆验签体系
		gui.cls_show_msg1(1, "使用新大陆验签体系");
		if((flag = settingsManager.setSystemSetting("setting_app_signature_scheme","newland"))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d: 设置新大陆验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_app_signature_scheme");
		if (!value.equals("newland")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=newland，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("安装新大陆验签.apk应成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:新大陆验签下安装失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3：设置为allinpay，使用通联验签体系
		gui.cls_show_msg1(1, "使用通联验签体系");
		if((flag = settingsManager.setSystemSetting("setting_app_signature_scheme","allinpay"))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d: 设置通联验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_app_signature_scheme");
		if (!value.equals("allinpay")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=allinpay，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("安装通联验签.apk应成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:通联验签下安装失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4：设置为meituan，使用美团验签体系
		gui.cls_show_msg1(1, "使用美团验签体系");
		if((flag = settingsManager.setSystemSetting("setting_app_signature_scheme","meituan"))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d: 设置美团验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_app_signature_scheme");
		if (!value.equals("meituan")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=meituan，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("安装美团验签.apk应成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:美团验签下安装失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case5：设置为liandong，使用联动验签体系
		gui.cls_show_msg1(1, "使用联动验签体系");
		if((flag = settingsManager.setSystemSetting("setting_app_signature_scheme","liandong"))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d: 设置联动验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_app_signature_scheme");
		if (!value.equals("liandong")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=liandong，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("安装联动验签.apk应成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:联动验签下安装失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case6：设置为yinsheng，使用银盛验签体系
		gui.cls_show_msg1(1, "使用银盛验签体系");
		if((flag = settingsManager.setSystemSetting("setting_app_signature_scheme","yinsheng"))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d: 设置银盛验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_app_signature_scheme");
		if (!value.equals("yinsheng")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=yinsheng，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("安装银盛验签.apk应成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:银盛验签下安装失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
	}
	
	
	//设置HOME键是否有效，效果是针对全局
	public void conf42(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置:设置home键可用
		if((ret = settingsManager.setSystemSetting("setting_disable_home_key","true"))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("此时home键有效，请按home键可以回到主界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case1:设置参数为false，预期返回true，此时按home键无效
		gui.cls_show_msg1(gScreenTime, "设置参数为false，此时按home键无效");
		if((ret = settingsManager.setSystemSetting("setting_disable_home_key","false"))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr,"line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("此时home键无效，请按home键看是否可以回到主界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2：设置参数为true，预期返回true，此时按home键有效
		gui.cls_show_msg1(gScreenTime, "设置参数为true，此时按home键有效");
		if((ret = settingsManager.setSystemSetting("setting_disable_home_key","true"))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("此时home键有效，请按home键可以回到主界面".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		//异常测试  传Null 值或者传 -1。应不起效。和上一次设置保持一致   传Null 返回 false  其他值返回true
		gui.cls_show_msg1(gScreenTime, "设置参数为异常(null或者-1)，此时设置接口不起效，效果与上一次一致");
	
		if ((settingsManager.setSystemSetting("setting_disable_home_key", null))) {
			gui.cls_show_msg1_record(fileName,"systemconfig67",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时Home键应有效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"systemconfig67",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if (!(settingsManager.setSystemSetting("setting_disable_home_key", "-1"))) {
			gui.cls_show_msg1_record(fileName,"systemconfig67",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时Home键应有效。是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"systemconfig67",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	//设置产品型号
	public void conf43(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		String product;
		SharedPreferences sp ;
		Editor editor ;
		String[] strArray={",福建新大陆支付技术有限公司","AaBbCcABCabc","123","N900","NL-N900","NL-N910"
				,"`中文ABCabc~!\"@$#[^%]*() -_=+|\\&{}:;?,/><.",Tools.getRandomString(90),Tools.getRandomString(91)};

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		//测试前置，保留初始型号
		gui.cls_show_msg("请先在manifest配置文件中添加mtms权限，完成点任意键继续");
		sp = myactivity.getSharedPreferences("my_product", Context.MODE_PRIVATE);
		editor = sp.edit();
		if(gui.cls_show_msg("是否首次进入本用例，是[确认]，否[其他]")==ENTER)
		{
			editor.putString("product", Build.MODEL);
			editor.commit();
		}
		product=sp.getString("product", null);
		
		//case1.1 参数异常
		gui.cls_printf("参数异常测试，产品型号设置为null，预期失败".getBytes());
		if(settingsManager.setSystemSetting("setting_product_model",null)==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:设置产品型号为null，预期(false)，实际(true)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2 参数为“”，预期成功，为初始默认型号，已与开发确认，如N910等
		gui.cls_printf(("产品型号设置为空字符串，预期为"+product).getBytes());
		if(settingsManager.setSystemSetting("setting_product_model","")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:设置产品型号为空，预期(true),实际(false)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("是否立即重启，重启后，执行用例到此，产品型号应显示为%s[确认]重启，[取消]继续",product)==ENTER)
		{
			Tools.reboot(myactivity);
			return;
		}	
		MyProduct();
		
		for (int i = 0; i < strArray.length; i++) 
		{
			if (settingsManager.setSystemSetting("setting_product_model",""+strArray[i]) == false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d设置产品型号%s测试失败(false)", Tools.getLineInfo(), strArray[i]);
				continue;
			}
			if (gui.cls_show_msg("是否立即重启，重启后，执行用例到此，产品型号应显示为%s，[确认]重启，[取消]继续", strArray[i]) == ENTER) 
			{
				Tools.reboot(myactivity);
				return;
			}
			MyProduct();
		}
		
		//测试后置，恢复为原有型号
		gui.cls_printf(("测试后置，恢复初始默认产品型号，设置为"+product+"，预期成功").getBytes());
		if (settingsManager.setSystemSetting("setting_product_model",""+product)==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d设置产品型号测试失败(false)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
			{
				settingsManager.setProductModel(product);
				return;
			}
		}
		if (gui.cls_show_msg("测试后置，是否立即重启，重启后，执行用例到此，产品型号应显示为初始默认型号"+product+"，[确认]重启，[取消]继续")==ENTER)
		{
			Tools.reboot(myactivity);
			return;
		}	
		MyProduct();
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime,"以上测试均通过则%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	public void MyProduct() {
		// 获取产品型号
	    String model = Build.MODEL;
		// 获取产品型号 add by 20170515
		String model_2 = NlBuild.VERSION.MODEL;
		String str3,str4;
		
		model = Build.MODEL;
		model_2 = NlBuild.VERSION.MODEL;
		str3 = model == null ? "null" : model;
		if (gui.cls_show_msg("(1)获取产品型号=%s?[确认]是，[取消]否", str3) != ENTER) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:获取产品型号测试失败(%s)", Tools.getLineInfo(), str3);
			if (!GlobalVariable.isContinue)
				return;
		}
		str4 = model_2 == null ? "null" : model_2;
		if (gui.cls_show_msg("(2)获取产品型号=%s?[确认]是，[取消]否", str4) != ENTER) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:获取产品型号测试失败(%s)", Tools.getLineInfo(), str4);
			if (!GlobalVariable.isContinue)
				return;
		}
	}
	
	//自定义菜单键键值
	public void conf44(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		String value ;
		boolean ret1 = false,ret2=false,ret3=false,ret4=false;
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		List<String> keyList = new ArrayList<String>()
				{
					private static final long serialVersionUID = 1L;
					{add("Switch Key");add("Menu Key");add("Volume_Up Key");add("Volume_Down Key");}
				};

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 添加键-值对
		hashMap.put("Switch Key", 187);
		hashMap.put("Menu Key", 82);
		hashMap.put("Volume_Up Key", 24);
		hashMap.put("Volume_Down Key", 25);
		
		// case1:参数异常测试，不在键值的范围内-1,2,55,255
		gui.cls_printf("参数异常测试中...".getBytes());
		
		if(((ret1 = settingsManager.setSystemSetting("persist.sys.menu_key","-1"))==true)
				||((ret2=settingsManager.setSystemSetting("persist.sys.menu_key","2"))==true)
				||((ret3=settingsManager.setSystemSetting("persist.sys.menu_key","55"))==true)
				||((ret4=settingsManager.setSystemSetting("persist.sys.menu_key","255"))==true))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:%s测试失败(%s,%s,%s,%s)", Tools.getLineInfo(),TESTITEM,ret1,ret2,ret3,ret4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:menu键设置为switch key、menu key、VOLUM_UP、VOLUME_DOWN，应设置成功
		for (String key:keyList) 
		{
			gui.cls_printf(("Menu键将设置为"+key).getBytes());
			if((ret1=settingsManager.setSystemSetting("persist.sys.menu_key",""+hashMap.get(key)))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:将Menu键设置为%s失败(%s)", Tools.getLineInfo(),TESTITEM,key,ret1);
				if(GlobalVariable.isContinue==false)
					return;
			}
			value = settingsManager.getSystemProperty("persist.sys.menu_key");
			if (!value.equals(""+hashMap.get(key))) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=%s，实际=%s)", Tools.getLineInfo(), TESTITEM,hashMap.get(key),value);
				if (!GlobalVariable.isContinue)
					return;
			}else if(gui.cls_show_msg("去桌面点击Menu键是否效果为%s，【确认】是，【其他】否",key)!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr,"line %d:将Menu键设置为%s未生效", Tools.getLineInfo(),TESTITEM,key);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case3:重启后设置的按键应保持
		if(gui.cls_show_msg("目前Menu键设置为Volume_Down Key，重启后也应为Volume_Down Key，是否立即重启，【确认】重启，【其他】不重启")==ENTER)
			Tools.reboot(myactivity);
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过，Menu键设置立即生效，只有恢复出厂设置才会变为默认状态", TESTITEM);
	}
	
	//设置屏蔽电源键长按
	public void conf45(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置不屏蔽电源键长按
		settingsManager.setSystemSetting("power_long_press_status","1");
		
		// case1:设置为0，设置屏蔽电源键长按
		gui.cls_show_msg1(2, "设置屏蔽电源键长按");
		if ((ret = settingsManager.setSystemSetting("power_long_press_status","0")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("power_long_press_status");
		if (!value.equals("0")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=0，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("长按电源键3s,是否不显示关机选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置为1，设置允许电源键长按
		gui.cls_show_msg1(2, "设置允许电源键长按");
		if ((ret = settingsManager.setSystemSetting("power_long_press_status","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("power_long_press_status");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("长按电源键3s,是否显示关机选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试,应不改变先前设置");
		if ((ret = settingsManager.setSystemSetting("power_long_press_status",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("power_long_press_status","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//
//		if ((ret = settingsManager.setSystemSetting("power_long_press_status","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		if(gui.ShowMessageBox("长按电源键3s,是否显示关机选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	//设置OTG
	public void conf46(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;

		/*process body*/ 
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		// 测试前置，设置关闭 OTG属性
		settingsManager.setSystemSetting("setting_otg_charge","0");
		
		// case1:设置为1，设置开启 OTG属性选项
		gui.cls_show_msg1(2, "设置开启 OTG属性选项");
		if ((ret = settingsManager.setSystemSetting("setting_otg_charge","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		String value = settingsManager.getSystemProperty("setting_otg_charge");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否开启设置 OTG属性选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置为1，设置关闭 OTG属性选项
		gui.cls_show_msg1(2, "设置关闭 OTG属性选项");
		if ((ret = settingsManager.setSystemSetting("setting_otg_charge","1")) == false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		value = settingsManager.getSystemProperty("setting_otg_charge");
		if (!value.equals("1")) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(预期=1，实际=%s)", Tools.getLineInfo(), TESTITEM,value);
			if (!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置，查看是否关闭OTG属性选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:参数异常测试，设置为null 
		gui.cls_show_msg1(1, "参数异常测试，设置异常参数应和上次设置保持一致");
		if ((ret = settingsManager.setSystemSetting("setting_otg_charge",null))) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

//		if ((ret = settingsManager.setSystemSetting("setting_otg_charge","")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//
//		if ((ret = settingsManager.setSystemSetting("setting_otg_charge","-1")) == false) 
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		
		if(gui.ShowMessageBox("进入设置，查看是否关闭OTG属性选项".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig65",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig65",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
