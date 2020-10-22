package com.example.highplattest.systemconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig200.java 
 * Author 			: xuess	
 * version 			: 
 * DATE 			: 20170817
 * directory 		: 
 * description 		: 设置模块内随机
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess			    20170817		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig200 extends UnitFragment{
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "设置模块内随机";
	Gui gui = null;
	private int lightValue;//保留初始亮度值
	private int screenOffTime;//保留初始休眠时间
	private boolean isShowBatteryPercent;//记录初始时电量储是否显示
	String[] language = {"zh-CN","zh-HK","ja-JP","ko-KR"};
	private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	private List<String> keyList = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
	{add("Switch Key");add("Menu Key");add("Volume_Up Key");add("Volume_Down Key");}};
	Random random = new Random();
	private String fileName="SystemConfig200";
	public void systemconfig200()
	{
			
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		switch (GlobalVariable.currentPlatform) 
		{
		case IM81_New:// 有带以太网和串口
		case IM81_Old:
		case N850:
		case N850_A7:
		case X5:
		case X3:
		case X1:
		case F7:
			String SystemConfigFunArr1[] = {"setScreenBrightness","setScreenTimeout","setSettingStorageDispley","setAllApkVerifyEnable","setAllApkVerifyDisable",
					"setSettingAppDispley","setSettingApkNeedLogin","setLoginPassword","setSettingHomeDispley","setSettingPrivacyDispley",
					"setShowBatteryPercent","setSettingBatteryDispley","setSettingDataUsageDispley","setSettingPrintSettingsDispley",
					"setSettingAccessibilitySettingsDispley","setSettingDevelopmentSettingsDispley","setSettingLocationSettingsDispley",
					"setSettingSecuritySettingsDispley","setSettingVpnDispley","setStatusBarEnabled","setStatusBarAdbNotify",
					"setSettingLanguageSpellCheckerDisplay","setSettingLockScreenDisplay","setSettingLanguageUserDictionaryDisplay","setSettingNotificationItemsDisplay",
					"setSettingLocales","setSettingOtaUpdateEnabled","setSettingWallpaperDisplay","setSettingDeviceInfoItemsDisplay","setTetherDisplay",
					"setWifiInstallCedentialDisplay","setSettingProcessorDisplay","setAppSignatureVerificationScheme",
					"setLauncher","setProductModel","setOtgMode"};
			int len1=SystemConfigFunArr1.length;
			Random_Test(SystemConfigFunArr1,len1);
			break;
			
		case N900_4G:// 键盘是虚拟的
		case N550:
		case N920:
		case N920_A7:
			String SystemConfigFunArr2[] = {"setScreenBrightness","setScreenTimeout","setSettingStorageDispley","setAllApkVerifyEnable","setAllApkVerifyDisable",
					"setSettingAppDispley","setSettingApkNeedLogin","setLoginPassword","setSettingHomeDispley","setSettingPrivacyDispley",
					"setShowBatteryPercent","setSettingBatteryDispley","setSettingDataUsageDispley","setSettingPrintSettingsDispley","setAppSwitchKeyEnabled",
					"relayoutNavigationBar","setSettingAccessibilitySettingsDispley","setSettingDevelopmentSettingsDispley","setSettingLocationSettingsDispley",
					"setSettingSecuritySettingsDispley","setSettingVpnDispley","setStatusBarEnabled","setStatusBarAdbNotify",
					"setSettingLanguageSpellCheckerDisplay","setSettingLockScreenDisplay","setSettingLanguageUserDictionaryDisplay","setSettingNotificationItemsDisplay",
					"setSettingLocales","setSettingOtaUpdateEnabled","setSettingWallpaperDisplay","setSettingDeviceInfoItemsDisplay","setTetherDisplay",
					"setWifiInstallCedentialDisplay","setSettingProcessorDisplay","disableAppCommunication","setAppSignatureVerificationScheme","setPaymentCertUpdateDisplay",
					"setHomeKeyEnabled","setLauncher","setProductModel","setSystemSetting","backupAppData"};
			int len2=SystemConfigFunArr2.length;
			Random_Test(SystemConfigFunArr2,len2);
			break;
			
		case N910:// home、back等是物理按键
		case N910_A7:
		case N700:
		case N700_A7:
		case N510:
			String SystemConfigFunArr3[] = {"setScreenBrightness","setScreenTimeout","setSettingStorageDispley","setAllApkVerifyEnable","setAllApkVerifyDisable",
					"setSettingAppDispley","setSettingApkNeedLogin","setLoginPassword","setSettingHomeDispley","setSettingPrivacyDispley",
					"setShowBatteryPercent","setSettingBatteryDispley","setSettingDataUsageDispley","setSettingPrintSettingsDispley",
					"setSettingAccessibilitySettingsDispley","setSettingDevelopmentSettingsDispley","setSettingLocationSettingsDispley",
					"setSettingSecuritySettingsDispley","setSettingVpnDispley","setStatusBarEnabled","setStatusBarAdbNotify",
					"setSettingLanguageSpellCheckerDisplay","setSettingLockScreenDisplay","setSettingLanguageUserDictionaryDisplay","setSettingNotificationItemsDisplay",
					"setSettingLocales","setSettingOtaUpdateEnabled","setSettingWallpaperDisplay","setSettingDeviceInfoItemsDisplay","setTetherDisplay",
					"setWifiInstallCedentialDisplay","setSettingProcessorDisplay","disableAppCommunication","setAppSignatureVerificationScheme","setPaymentCertUpdateDisplay",
					"setHomeKeyEnabled","setLauncher","setProductModel","setMenuKeyValue","setSystemSetting","backupAppData"};
			int len3=SystemConfigFunArr3.length;
			Random_Test(SystemConfigFunArr3,len3);
			break;

		case N900_3G:
			String SystemConfigFunArr4[] = {"setScreenBrightness","setScreenTimeout","setSettingStorageDispley","setAllApkVerifyEnable","setAllApkVerifyDisable",
					"setSettingAppDispley","setSettingApkNeedLogin","setLoginPassword","setSettingHomeDispley","setSettingPrivacyDispley",
					"setShowBatteryPercent","setSettingBatteryDispley","setSettingDataUsageDispley","setSettingPrintSettingsDispley","setAppSwitchKeyEnabled",
					"relayoutNavigationBar","setSettingAccessibilitySettingsDispley","setSettingDevelopmentSettingsDispley","setSettingLocationSettingsDispley",
					"setSettingSecuritySettingsDispley","setDeepSleepEnabled","setBluetoothFileTransfer","setLauncher","setProductModel","setMenuKeyValue"};
			int len4=SystemConfigFunArr4.length;
			Random_Test(SystemConfigFunArr4,len4);
			break;
			
		default:
			gui.cls_show_msg1(gScreenTime, "当前产品型号有误:"+Build.MODEL);
			settingsManager.setProductModel("");
			break;
		}
		
	}
	
	public void Random_Test( String[] ApnFunArr,int len){
		//部分设置的前置
			lightValue = settingsManager.getScreenBrightness();
			isShowBatteryPercent = settingsManager.isShowBatteryPercent();
			screenOffTime = getScreenOffTime();
			// 添加键-值对
			hashMap.put("Switch Key", 187);
			hashMap.put("Menu Key", 82);
			hashMap.put("Volume_Up Key", 24);
			hashMap.put("Volume_Down Key", 25);
			
			/* process body */
	    	int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
	    	gui.cls_show_msg("请先在manifest配置文件中添加mtms权限，完成点任意键继续");
	    	gui.cls_show_msg("注意：测试过程中调用到setLauncher方法时将返回桌面，并非异常奔溃，不影响测试进程，重新点开测试程序即可。按任意键继续");
			gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中,请不要强制退出，否则无法恢复默认设置！");
			
			
			while(cnt > 0)
			{
				if(gui.cls_show_msg1(gScreenTime, "设置模块内随机组合测试中...\n还剩%d次（已成功%d次），请勿强制退出，按[取消]退出测试...",cnt,succ)==ESC)
					break;			
				String[] func = new String[len];	
				StringBuilder funcStr = new StringBuilder();
				for(int i=0;i<len;i++){
					func[i] = ApnFunArr[random.nextInt(len)];
					funcStr.append(func[i]).append("-->\n");	
					if((i+1)%10 == 0 || i == len-1){
						gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr.toString(),bak-cnt+1);
						funcStr.setLength(0);
					}
				}
				cnt--;
				boolean ret=false;
				for(int i=0;i<len;i++){
					gui.cls_show_msg1(gScreenTime,"随机第%d组第%d项，正在测试%s",bak-cnt,i+1,func[i]);
					SystemConfigFuncName fname = SystemConfigFuncName.valueOf(func[i]);
					if(!(ret=test(fname))){
						gui.cls_show_msg1_record(fileName,"systemconfig200",gScreenTime,"%s第%d组第%d项,%s方法测试失败",TESTITEM,bak-cnt,i+1,func[i]);
						break;
					}
				}
				if(!ret){
					for(int i=0;i<len;i++){
						funcStr.append(func[i]).append("-->");
					}
					gui.cls_show_msg1_record(fileName,"systemconfig200",gScreenTime,"第%d组随机测试失败，测试顺序为：%s",bak-cnt,funcStr.toString());
					funcStr.setLength(0);
				} else{
					succ++;
				}
			}
			gui.cls_show_msg1_record(fileName,"systemconfig200",gScreenTime, "%s测试完成，已执行次数为%d,成功为%d次",TESTITEM, bak-cnt,succ);
			
			//测试后置
			gui.cls_show_msg1(gScreenTime," 恢复默认设置中...");
			settingsManager.setScreenBrightness(lightValue);
			settingsManager.setScreenTimeout(screenOffTime);
			settingsManager.setSettingStorageDispley(0);
			settingsManager.setAllApkVerifyDisable();
			settingsManager.setSettingAppDispley(0);
			settingsManager.setSettingApkNeedLogin(0);
			settingsManager.setSettingHomeDispley(1);
			settingsManager.setSettingPrivacyDispley(0);
			if(isShowBatteryPercent)
				settingsManager.setShowBatteryPercent(true);
			else
				settingsManager.setShowBatteryPercent(false);
			settingsManager.setSettingBatteryDispley(0);
			settingsManager.setSettingDataUsageDispley(1);
			settingsManager.setSettingPrintSettingsDispley(1);				
			settingsManager.setSettingAccessibilitySettingsDispley(0);
			settingsManager.setSettingDevelopmentSettingsDispley(0);
			settingsManager.setSettingLocationSettingsDispley(0);
			settingsManager.setSettingSecuritySettingsDispley(0);
			settingsManager.setProductModel("");//型号设为""可以恢复默认值
			settingsManager.setMenuKeyValue(hashMap.get("Menu Key"));
			
			if(GlobalVariable.currentPlatform==Model_Type.N900_3G){
				settingsManager.setDeepSleepEnabled(false);
				try
				{
					settingsManager.setBluetoothFileTransfer(0);
				} catch (NoSuchMethodError e) 
				{
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"此版本不支持设置禁止蓝牙传输文件");
				}
			} else{
				settingsManager.setSettingVpnDispley(1);
				settingsManager.setStatusBarEnabled(1);
				settingsManager.setStatusBarAdbNotify(1);
				settingsManager.setSettingLanguageSpellCheckerDisplay(1);
				settingsManager.setSettingLockScreenDisplay(0);
				settingsManager.setSettingLanguageUserDictionaryDisplay(1);
				settingsManager.setSettingNotificationItemsDisplay("00001111");
				settingsManager.setSettingLocales(language);
				settingsManager.setSettingOtaUpdateEnabled(false);
				settingsManager.setSettingWallpaperDisplay(0);
				settingsManager.setSettingDeviceInfoItemsDisplay("00000");
				settingsManager.setTetherDisplay(1);
				settingsManager.setWifiInstallCedentialDisplay(1);
				settingsManager.setSettingProcessorDisplay(1);
				settingsManager.setAppSignatureVerificationScheme(null);
			}
			
			if(GlobalVariable.currentPlatform==Model_Type.N550||GlobalVariable.currentPlatform == Model_Type.N900_3G || GlobalVariable.currentPlatform == Model_Type.N900_4G || GlobalVariable.currentPlatform==Model_Type.N850 || GlobalVariable.currentPlatform==Model_Type.X5||GlobalVariable.currentPlatform==Model_Type.X3){
				settingsManager.setAppSwitchKeyEnabled(true);
				settingsManager.relayoutNavigationBar(0);
			}
			
			if(GlobalVariable.currentPlatform==Model_Type.N550||GlobalVariable.currentPlatform == Model_Type.N910 || GlobalVariable.currentPlatform == Model_Type.N900_4G ||
					GlobalVariable.currentPlatform == Model_Type.N700||GlobalVariable.currentPlatform == Model_Type.N700_A7||GlobalVariable.currentPlatform == Model_Type.N510
					||GlobalVariable.currentPlatform==Model_Type.N920||GlobalVariable.currentPlatform==Model_Type.N920_A7){
				settingsManager.setPaymentCertUpdateDisplay(0);
				settingsManager.disableAppCommunication(null);
				settingsManager.setHomeKeyEnabled(true);
				try
				{
					settingsManager.setSystemSetting("setting_short_press_power_key", "0");
				} catch (NoSuchMethodError e) 
				{
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "%s固件不支持setSystemSetting接口", GlobalVariable.gCustomerID);
				}
			}

			if(gui.cls_show_msg("已恢复默认设置，请检测系统设置应用及其他SystemConfig用例能否正常使用；部分设置重启后才生效，[确认]立即重启，[其他]取消")==ENTER)
			{
				Tools.reboot(myactivity);
			}
			gui.cls_show_msg1(gScreenTime,"%s测试完成,已执行次数为%d,成功为%d次。\n请检查SystemConfig模块内其他用例是否能正常使用！(长按确认键退出测试)",TESTITEM, bak-cnt,succ);
	}
	
	public boolean test(SystemConfigFuncName fname){
		boolean is =true;
		boolean ret = false;
		int rand = random.nextInt(2);
		boolean rand2 = true;
		switch(fname){
		case setScreenBrightness:
			int brightness = random.nextInt(256);
			gui.cls_show_msg1(gScreenTime,"随机(0~255)设置亮度为：%d",brightness);
			if ((ret = settingsManager.setScreenBrightness(brightness)) == false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:随机设置亮度失败(%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setScreenTimeout:
			gui.cls_show_msg1(gScreenTime,"设置为永不休眠");
			if ((ret = settingsManager.setScreenTimeout(-1))==false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置永不休眠失败(%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingStorageDispley:
			settingsManager.setSettingStorageDispley(rand);
			break;
		case setAllApkVerifyEnable:
			try
			{
				if((ret = settingsManager.setAllApkVerifyEnable())==false)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置Apk验签控制开启失败(%s)", Tools.getLineInfo(),ret);
					settingsManager.setAllApkVerifyEnable();
					is = false;
				}
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg1(gScreenTime, "不支持设置Apk验签控制");
				is = false;
			}
			break;
		case setAllApkVerifyDisable:
			try
			{
				if((ret = settingsManager.setAllApkVerifyDisable())==false)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置Apk验签控制关闭失败(%s)", Tools.getLineInfo(),ret);
					settingsManager.setAllApkVerifyEnable();
					is = false;
				}
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg1(gScreenTime, "不支持设置Apk验签控制");
				is = false;
			}
			break;
		case setSettingAppDispley:
			settingsManager.setSettingAppDispley(rand);
			break;
		case setSettingApkNeedLogin:
			if(	(ret=settingsManager.setSettingApkNeedLogin(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置应用是否需要通过管理员密码登录来启动失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setLoginPassword:
			if(	(ret=settingsManager.setLoginPassword("123456"))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置应用管理员密码失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingHomeDispley:
			settingsManager.setSettingHomeDispley(rand);
			break;
		case setSettingPrivacyDispley:
			settingsManager.setSettingPrivacyDispley(rand);
			break;
		case setShowBatteryPercent:
			rand2 = rand == 0 ? true : false;
			settingsManager.setShowBatteryPercent(rand2);
			break;
		case setSettingBatteryDispley:
			if(	(ret=settingsManager.setSettingBatteryDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置电池选项开关失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingDataUsageDispley:
			if(	(ret=settingsManager.setSettingDataUsageDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置流量使用情况选项开关失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingPrintSettingsDispley:
			if(	(ret=settingsManager.setSettingPrintSettingsDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置打印选项开关失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setAppSwitchKeyEnabled:
			rand2 = rand == 0 ? true : false;
			if(	(ret=settingsManager.setAppSwitchKeyEnabled(rand2))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置全局底部任务键是否有效失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case relayoutNavigationBar:
			settingsManager.relayoutNavigationBar(rand);
			break;
		case setSettingAccessibilitySettingsDispley:
			if(	(ret=settingsManager.setSettingAccessibilitySettingsDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置是否显示辅助功能选项失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingDevelopmentSettingsDispley:
			if(	(ret=settingsManager.setSettingDevelopmentSettingsDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置是否显示开发者选项失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingLocationSettingsDispley:
			if(	(ret=settingsManager.setSettingLocationSettingsDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置是否显示位置信息选项失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingSecuritySettingsDispley:
			if(	(ret=settingsManager.setSettingSecuritySettingsDispley(rand))==false){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置是否显示安全选项失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingVpnDispley:
			settingsManager.setSettingVpnDispley(rand);
			break;
		case setDeepSleepEnabled:
			rand2 = rand == 0 ? true : false;
			if(	(ret=settingsManager.setDeepSleepEnabled(rand2))!=true){
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"line %d:设置是否显示安全选项失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setStatusBarEnabled:
			if((ret = settingsManager.setStatusBarEnabled(0))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:状态栏下拉开启失败(%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			String[] statusMenu = {"airplane","wifi","bt","location","notifications","cell","roaming","apn"};
			if((ret = settingsManager.setStatusBarQsTiles(statusMenu)) == false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:状态栏快捷开关设置失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setStatusBarAdbNotify:
			if((ret = settingsManager.setStatusBarAdbNotify(rand)) == false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:ADB调试信息开关失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingLanguageSpellCheckerDisplay:
			if((ret = settingsManager.setSettingLanguageSpellCheckerDisplay(rand)) == false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置语言输入法-拼写检查工具是否显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingLockScreenDisplay:
			settingsManager.setSettingLockScreenDisplay(rand);
			break;
		case setSettingLanguageUserDictionaryDisplay:
			if((ret = settingsManager.setSettingLanguageUserDictionaryDisplay(rand)) == false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:语言与输入法个人字典选项是否显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingNotificationItemsDisplay:
			settingsManager.setSettingNotificationItemsDisplay("00000000");
			break;
		case setSettingLocales:
			gui.cls_show_msg1(1, "设置-语言和输入法，语言中只包含中文");
			if((ret = settingsManager.setSettingLocales(new String[]{"zh-CN"}))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置-语言和输入法-只包含中文失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingOtaUpdateEnabled:
			rand2 = rand == 0 ? true : false;
			if((ret = settingsManager.setSettingOtaUpdateEnabled(rand2))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置OTA升级显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingWallpaperDisplay:
			settingsManager.setSettingWallpaperDisplay(rand);
			break;
		case setSettingDeviceInfoItemsDisplay:
			if(ret = settingsManager.setSettingDeviceInfoItemsDisplay("11111")==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置状态信息全部隐藏失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setTetherDisplay:
			if(ret = settingsManager.setTetherDisplay(rand)==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置网络共享的便携式热点选项是否显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setWifiInstallCedentialDisplay:
			if(ret = settingsManager.setWifiInstallCedentialDisplay(rand)==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:wifi高级选项安装证书选项是否显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setSettingProcessorDisplay:
			if(ret = settingsManager.setSettingProcessorDisplay(rand)==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:处理器信息选项是否显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case disableAppCommunication:
			String[] errpackNames={"err1","err2"};
			if((ret = settingsManager.disableAppCommunication(errpackNames))!=false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:禁止App连网参数异常测试失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setAppSignatureVerificationScheme:
			String errorValue[]=new String[]{"err1","err2"};
			if((ret = settingsManager.setAppSignatureVerificationScheme(errorValue))!=false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置当前设备签名验证方案异常测试失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setPaymentCertUpdateDisplay:
			if((ret = settingsManager.setPaymentCertUpdateDisplay(rand))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:支付证书升级选项隐藏失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setHomeKeyEnabled:
			rand2 = rand == 0 ? true : false;
			if((ret = settingsManager.setHomeKeyEnabled(rand2))!=true)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置home键无效失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setBluetoothFileTransfer:
			try
			{
				if((ret = settingsManager.setBluetoothFileTransfer(rand))!=true)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置禁止蓝牙传输文件失败(ret=%s)", Tools.getLineInfo(),ret);
					is = false;
				}
			} catch (NoSuchMethodError e) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr,"此版本不支持设置禁止蓝牙传输文件");
			}
			break;
		case setLauncher:
			settingsManager.setLauncher("com.android.launcher");
			break;
		case setProductModel:
			if ((ret = settingsManager.setProductModel(Tools.getRandomString(90)))==false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置产品型号测试失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
		case setMenuKeyValue:
			rand = random.nextInt(4);
			if((ret = settingsManager.setMenuKeyValue(hashMap.get(keyList.get(rand))))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:将Menu键设置为%s失败(ret=%s)", Tools.getLineInfo(),keyList.get(rand),ret);
				is = false;
			}
			break;
		case setSystemSetting:
			try
			{
				if((ret = settingsManager.setSystemSetting("setting_short_press_power_key", "1"))==false)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置屏蔽短按电源键进入休眠的功能失败(ret=%s)", Tools.getLineInfo(),ret);
					is = false;
				}
			} catch (NoSuchMethodError e) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "%s固件不支持setSystemSetting接口", GlobalVariable.gCustomerID);
			}
			break;
			
		case setOtgMode:
			boolean isUSB = false;
			if(settingsManager.getOtgMode()==false)
			{
				settingsManager.setOtgMode(true);
				if((isUSB = settingsManager.getOtgMode())==false)
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置USB主模式失败(ret = %s)",Tools.getLineInfo(),isUSB);
				is=false;
			}
			else
			{
				settingsManager.setOtgMode(false);
				if((isUSB = settingsManager.getOtgMode())==true)
					gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:设置USB主模式失败(ret = %s)",Tools.getLineInfo(),isUSB);
				is=false;
			}
			break;
			
		case setDateTimeSettingsDisplay:// add by zhengxq 20181126
			if(ret = settingsManager.setDateTimeSettingsDisplay(rand)==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig200",gKeepTimeErr, "line %d:系统设置中日期与时间是否显示失败(ret=%s)", Tools.getLineInfo(),ret);
				is = false;
			}
			break;
			
		default:
			break;
		}
		return is;
	}
	

	public enum SystemConfigFuncName
	{
		setScreenBrightness,		//1设置亮度，配合getScreenBrightness获取亮度，恢复默认要最开始取一个值存着
		setScreenTimeout,			//2设置休眠时间，恢复默认应设为1分钟(ONE_MIN)
		setSettingStorageDispley,	//3设置存储选项开关，恢复默认应设为显示(0)
		setAllApkVerifyEnable,
		setAllApkVerifyDisable,		//4设置Apk验签控制，
		setSettingAppDispley,		//5设置应用选项开关，恢复默认应设为显示(0)
		setSettingApkNeedLogin,		//8设置应用密码登陆开关，恢复默认应设为显示(0)
		setLoginPassword,			//8设置登录密码
		setSettingHomeDispley,		//9设置是否显示主屏幕，恢复默认应设为关闭(1)
		setSettingPrivacyDispley,	//10设置备份和重置选项开关，恢复默认应设为显示(0)
		setShowBatteryPercent,		//11设置状态栏中电池电量百分比开关，配合isShowBatteryPercent()获取前置状态
		setSettingBatteryDispley,	//12设置是否显示电池选项，恢复默认应设为关闭(0)，与systemconfig12不一样？
		setSettingDataUsageDispley,	//13设置流量使用情况选项开关，恢复默认应设为关闭(1)
		setSettingPrintSettingsDispley,	//14设置打印选项开关，恢复默认应设为关闭(1)
		setAppSwitchKeyEnabled,		//15设置全局底部任务键是否有效，恢复默认应设为有效(true)
		relayoutNavigationBar,		//16设置底部返回键位置，恢复默认应设为在左侧(0)，要重启
		setSettingAccessibilitySettingsDispley,		//17设置辅助功能选项开关，恢复默认应设为开启(0)
		setSettingDevelopmentSettingsDispley,		//18设置开发者选项开关，恢复默认应设为开启(0);
		setSettingLocationSettingsDispley,			//19设置位置信息选项开关，恢复默认应设为开启(0)
		setSettingSecuritySettingsDispley,			//20设置是否显示安全选项，恢复默认应设为开启(0);
		setSettingVpnDispley,		//21设置VPN选项开关，恢复默认应设为关闭(1)
		setDeepSleepEnabled,		//22设置3G深浅休眠开关，恢复默认应设为关闭(false)
		setStatusBarEnabled,		//24状态栏菜单下拉控制，恢复默认应设为关闭(1)
		setStatusBarAdbNotify,		//25设置状态栏是否显示ADB调试信息，恢复默认应设为关闭(1)
		setSettingLanguageSpellCheckerDisplay,		//26语言输入法-拼写检查工具显示/隐藏，恢复默认应设为关闭(1)
		setSettingLockScreenDisplay,				//27屏幕锁定方式默认显示，恢复默认应设为开启(0)
		setSettingLanguageUserDictionaryDisplay,	//28语言与输入法个人字典选项显示/隐藏，恢复默认应设为关闭(1)
		setSettingNotificationItemsDisplay,			//29声音中音频显示/隐藏，恢复默认应设为默认("00001111")
		setSettingLocales,			//30语言中语言种类显示接口,恢复默认应设为中文、香港、日文、韩文
		setSettingOtaUpdateEnabled,	//31设置OTA升级显示/不显示,恢复默认应设为不显示(false)
		setSettingWallpaperDisplay,	//32设置壁纸显示/不显示，恢复默认应设为开启(0)
		setSettingDeviceInfoItemsDisplay,			//33状态信息的显示/隐藏接口，恢复默认应设为默认("00000")
		setTetherDisplay,			//34设置网络共享的便携式热点选项显示/隐藏，恢复默认应设为关闭(1)
		setWifiInstallCedentialDisplay,				//35设置wifi高级选项安装证书选项的显示/隐藏，恢复默认应设为关闭(1)
		setSettingProcessorDisplay,				//36设置是否显示关于设备的处理器信息选项，恢复默认应设为关闭(1)
		disableAppCommunication,	//46禁止App连网、获取被禁用网络的App，恢复默认应所有app均可联网(null)
		setAppSignatureVerificationScheme,		//47设置当前设备安装App时使用的签名验证方案，恢复默认应使用新大陆验签体系(null)
		setPaymentCertUpdateDisplay,//48设置支付证书升级功能是否显示，恢复默认应设为开启(0)
		setHomeKeyEnabled,			//49设置home键是否有效，恢复默认应设为有效(true)
		setBluetoothFileTransfer,	//50设置禁止蓝牙传输文件，恢复默认应设为开启(0);
		setLauncher,				//51设置launcher，无返回值，恢复默认应设("com.android.launcher")
		setProductModel,			//52配置产品型号
		setMenuKeyValue,			//53自定义菜单键键值
		setSystemSetting,			//54设置系统设置项的值（美团需求）
		setOtgMode,                  //56设置USB的主从模式
		setDateTimeSettingsDisplay	// 58设置日期和时间是否显示
	}
	
	/** 
	 * 原生方法获取锁屏时间，单位为ms
	 */  
	private int getScreenOffTime()
	{
		int screenOffTime=0;  
	    try{  
	        screenOffTime = Settings.System.getInt(myactivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);  
	    }catch (SettingNotFoundException e) {  
            e.printStackTrace();  
        }  
	    return screenOffTime;  
	} 
	
	@Override
	public void onTestUp() 
	{
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() 
	{
		settingsManager = null;
		gui = null;
	}
}
