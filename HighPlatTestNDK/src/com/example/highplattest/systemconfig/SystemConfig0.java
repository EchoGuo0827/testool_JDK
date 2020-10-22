package com.example.highplattest.systemconfig;

import com.example.highplattest.main.tools.LoggerUtil;
import com.newland.ndk.JniNdk;

public class SystemConfig0 {
	/**
	 * Android系统设置相关的接口模块用例说明
	 * SystemConfig1：设置和获取屏幕亮度(setScreenBrightness和getScreenBrightness)
	 * SystemConfig2：设置Android端休眠时间(setScreenTimeout)
	 * SystemConfig3：设置存储选项开关(setSettingStorageDisplay)
	 * SystemConfig4：Apk验签控制(setAllApkVerifyEnable和setAllApkVerifyDisable)
	 * SystemConfig5：设置应用选项开关(setSettingAppDisplay)
	 * SystemConfig6：开机Launcher启动规则(PowerManager.reboot)
	 * SystemConfig7：测试系统时间同步更新(JniNdk.JNI_Sys_GetPosTime)
	 * SystemConfig8：设置应用密码登录开关(setSettingApkNeedLogin)
	 * SystemConfig9：设置主屏幕选项开关(setSettingHomeDisplay)
	 * SystemConfig10：设置备份和重置选项开关(setSettingPrivacyDisplay)
	 * SystemConfig11：设置状态栏中电池电量百分比开关(setShowBatteryPercent)
	 * SystemConfig12：设置电池选项开关(setSettingBatteryDisplay)
	 * SystemConfig13：设置流量使用情况选项开关(setSettingDataUsageDisplay)
	 * SystemConfig14：设置打印选项开关(setSettingPrintSettingsDisplay)
	 * SystemConfig15：设置recentApp开关(setAppSwitchKeyEnabled)
	 * SystemConfig16：设置底部返回键位置(relayoutNavigationBar)
	 * SystemConfig17：设置辅助功能选项开关(setSettingAccessibilitySettingsDisplay)
	 * SystemConfig18：设置开发者选项开关(setSettingDevelopmentSettingsDisplay)
	 * SystemConfig19：设置位置信息选项开关(setSettingLocationSettingsDisplay)
	 * SystemConfig20：设置安全选项开关(setSettingSecuritySettingsDisplay)
	 * SystemConfig21：设置VPN选项开关(setSettingVpnDisplay)
	 * SystemConfig22：设置3G深浅休眠开关(setDeepSleepEnabled)
	 * SystemConfig23：获取MAC地址(getMacAddress)
	 * SystemConfig24：状态栏菜单下拉控制(setStatusBarEnabled)
	 * SystemConfig25：ADB调试信息(setStatusBarAdbNotify)
	 * SystemConfig26：语言输入法-拼写检查工具显示/隐藏(setSettingLanguageSpellCheckerDisplay)
	 * SystemConfig27：屏幕锁定方式显示/隐藏(setSettingLockScreenDisplay)
	 * SystemConfig28：语言与输入法个人字典选项显示/隐藏(setSettingLanguageUserDictionaryDisplay)
	 * SystemConfig29：声音中音频显示/隐藏(setSettingNotificationItemsDisplay)
	 * SystemConfig30：语言中语言种类显示接口(setSettingLocales)
	 * SystemConfig31：OTA升级显示/不显示(setSettingOtaUpdateEnabled)
	 * SystemConfig32：壁纸显示/不显示(setSettingWallpaperDisplay)
	 * SystemConfig33：状态信息的显示接口/隐藏(setSettingDeviceInfoItemsDisplay)
	 * SystemConfig34：网络共享的便携式热点选项显示/隐藏(setTetherDisplay）
	 * SystemConfig35：wifi高级选项中安装证书选项的显示与隐藏(setWifiInstallCedentialDisplay)
	 * SystemConfig36：处理器信息选项隐藏/显示(setSettingProcessorDisplay)
	 * SystemConfig37：测试系统设置usbEnable和usbDisable(只支持81设备)
	 * SystemConfig38：测试setPowerKeySleepEnable和 setPowerKeySleepDisable(只支持IM81产品)
	 * SystemConfig39：测试触控虚拟按键的LED模式setLedMode和getLedMode
	 * SystemConfig40：测试系统与K21通信不上时提示对话框，系统锁定，跟开发确认过setK21CommunicateWarnEnable是在开机的时候进行检测(只支持IM81产品)
	 * SystemConfig41：测试系统与K21通信不上不提示对话框，跟开发确认过setK21CommunicateWarnDisable在开机的时候才进行检测(只支持IM81产品)
	 * SystemConfig42：触摸唤醒函数测试(setTouchWakeUpMode)
	 * SystemConfig43：设置钱箱工作电压，延迟(setVoltage、setTimeSec)
	 * SystemConfig44：测试系统设置函数OpenCashBox，打开钱箱(OpenCashBox)
	 * SystemConfig45：USB主、从模式开关(setUsbPortMode)
	 * SystemConfig46：禁止App连网(disableAppCommunication)
	 * SystemConfig47：设置当前设备安装App时使用的签名验证方案(setAppSignatureVerificationScheme)
	 * SystemConfig48：设置支付证书升级选项开关(setPaymentCertUpdateDisplay)
	 * SystemConfig49：设置home键是否有效(setHomeKeyEnabled)
	 * SystemConfig50：设置禁止蓝牙传输文件(setBluetoothFileTransfer)
	 * SystemConfig51：测试设置开机Laucher,setLauncher
	 * SystemConfig52：配置产品型号(getSharedPreferences、setProductModel)
	 * SystemConfig53：自定义菜单键键值(setMenuKeyValue)
	 * SystemConfig54：设置系统设置项的值（美团需求）(setSystemSetting)
	 * SystemConfig55：设置应用的备份和恢复(backupAppData、restoreAppData)
	 * SystemConfig56：设置是否开启USB主模式和获取当前USB主模式状态(setOtgMode、getOtgMode)
	 * SystemConfig57：阿里固件获取状态栏状态(isStatusBarExpandable)
	 * SystemConfig58：控制系统设置中日期与时间显示(setDateTimeSettingsDisplay)
	 * SystemConfig59：音量键唤醒系统功能(setWakeUpSystemEnable)
	 * SystemConfig60：设置屏蔽长按电源键功能(海外)(setpowerLongPressStatus)
	 * SystemConfig61：setDefaultInputMethod设置输入法(setDefaultInputMethod)
	 * SystemConfig62：设置和获取客户标记(setPackageFlag)
	 * SystemConfig63：设置X5副屏触摸关闭(setSecPanelTouch)
	 * SystemConfig64:setAirPlaneModeEnabled(boolean enabled)：开启或关闭飞行模式
	 * SystemConfig65:setSystemSetting(String setting,String SettingValue):设置和获取系统属性
	 * SystemConfig66:setNavigationBarOpen(boolean enabled):设置底部虚拟按键
	 * SystemConfig67:getDeviceOptConfig():获取设备控制文件内容
	 * SystemConfig68:setSystemLogEnabled(boolean enabled):设置系统日志开启或 关闭
	 * SystemConfig69:isSystemLogEnabled():获取系统日志开启/关闭
	 * SystemConfig70:adb安全加固测试
	 * SystemConfig71:exportSystemLog()导出系统日志请求
	 * 
	 */
	
	public static void genLog1M(int cycleTime)
	{
		String funcName = "genLog1M";
		LoggerUtil.e(funcName+"->start");
		for (int i = 0; i < 1024*3*cycleTime; i++) {
			LoggerUtil.v(funcName+"->1111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
			LoggerUtil.d(funcName+"->2222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
			LoggerUtil.e(funcName+"->3333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
			LoggerUtil.i(funcName+"->4444444444444444444444444444444444444444444444444444444444444444444444444444444444444");
			LoggerUtil.w(funcName+"->5555555555555555555555555555555555555555555555555555555555555555555555555555555555555");
		}
		LoggerUtil.e(funcName+"->end");
	}
}
