package com.example.highplattest.psbc;

import java.util.HashMap;
import java.util.Map;
import android.app.enterpriseadmin.ApplicationPolicy;
import android.app.enterpriseadmin.ExDevicePolicyManager;
import android.content.ComponentName;
import android.os.SystemClock;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.AppTool;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module             : 邮储固件专用 file 
 * name               : Psbc3.java 
 * Author             : zhengxq 
 * version            : 
 * DATE               :20180507 
 * directory          : 
 * description        : 邮储固件--应用程序管理 related 
 * document           : 
 * history            :author          date           remarks 
 *                     zhengxq       20180507         created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Psbc3 extends UnitFragment {
	/* private & local definition */
	private final String CLASS_NAME = Psbc3.class.getSimpleName();
	private final String TESTITEM = "应用程序管理";
	private Gui gui = new Gui(myactivity, handler);
	private ExDevicePolicyManager mExDevicePolicyManager;
	Map<String, String> mMap = new HashMap<String, String>();

	public void psbc3() {
		mExDevicePolicyManager = (ExDevicePolicyManager) myactivity.getSystemService("ex_device_policy");
		mMap.put("setting", "com.android.settings");
		mMap.put("detect", "com.newland.detectapp");
		mMap.put("highPlat", "com.example.highplattest");
		mMap.put("file", "com.android.qrdfileexplorer");
		mMap.put("aliPay", "com.eg.android.AlipayGphone");
		mMap.put("elme", "me.ele");
		mMap.put("camera", "com.android.camera2");
		mMap.put("Nova", "com.teslacoilsw.launcher");

		while (true) {
			int nKeyIn = gui.cls_show_msg("%s\n0.终端应用程序管理\n1.清理应用缓存\n2.Launcher控制\n3.重启测试\n", TESTITEM);
			switch (nKeyIn) {
			case '0':
				appManage();
				break;

			case '1':
				appCache();
				break;

			case '2':
				systemLauncher();
				break;

			case '3':
				RebootTest();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}

	/**
	 * 设置应用禁用或可用后重启设备
	 */
	private void RebootTest() 
	{
		String funcName="RebootTest";
		gui.cls_show_msg("请确保已安装支付宝和饿了么应用,完成任意键继续");
		boolean ret=false;
		ApplicationPolicy appPolicy = mExDevicePolicyManager.getApplicationPolicy();
		//case1:设置图标全隐藏后重启设备
		if (gui.cls_show_msg("是否进行禁用多个应用测试，点击是则重启设备，重启后预期效果为支付宝、饿了么、NovaLauncher应用、设置、相机、自检、文件管理的桌面图标均会隐藏；如果已经测过，可点击取消跳过\n是[确认],否[其他]") == ENTER) {
			for (String value : mMap.values()) {
				if(value.equals("com.example.highplattest"))//自身的应用在重启中不测
					continue;
				if ((ret = appPolicy.setDisableApplication(value)) == false) {
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s设置隐藏图标%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,value, ret);
					if (GlobalVariable.isContinue == false)
						return;
				}
			}
			Tools.reboot(myactivity);
		}
	  
		//case2:设置图标全显示后重启设备
		if (gui.cls_show_msg("是否进行设置多个应用可用的测试，点击是则设备重启，重启后预期效果为支付宝、饿了么、NovaLauncher应用、设置、相机、自检、文件管理的桌面图标均会显示且功能正常使用；如果已经测过，可点击取消跳过\n是[确认],否[其他]") == ENTER) {
			for (String value : mMap.values()) {
				if(value.equals("com.example.highplattest"))//自身的应用在重启中不测
					continue;
				if ((ret = appPolicy.setEnableApplication(value)) == false) {
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s设置显示图标%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,value, ret);
					if (GlobalVariable.isContinue == false)
						return;
				}
			}
			Tools.reboot(myactivity);
			}
		
		// case3:设置第三方launcher后重启设备
		if (gui.cls_show_msg("是否进行设置第三方桌面Nova Launcher测试，点击是则设备重启，重启后预期点击Home键,桌面效果为Nova Launcher且进入设置-主屏幕选中的启动器是Nova Launcher；如果已经测过，可点击取消跳过\n是[确认],否[其他]") == ENTER) {
			gui.cls_show_msg("请先安装NovaLauncher,安装完毕任意键继续");
			if ((ret = appPolicy.setDefaultLauncher(new ComponentName("com.teslacoilsw.launcher", "com.teslacoilsw.launcher.MainActivity"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			SystemClock.sleep(9000);//未重启之前桌面已设置成功，但8s内重启后失效，8s后正常
			Tools.reboot(myactivity);
		}
		
		// case4:设置原生launcher后重启设备
		if (gui.cls_show_msg("是否进行设置原生Launcher测试，点击是则设备重启，重启后预期点击Home键,桌面效果为原生桌面且进入设置-主屏幕选中的是启动器；如果已经测过，可点击取消跳过\n是[确认],否[其他]") == ENTER) {
			if ((ret = appPolicy.setDefaultLauncher(new ComponentName("com.android.launcher", "com.android.launcher.MainActivity"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			SystemClock.sleep(9000);
			Tools.reboot(myactivity);
		}
		
	}

	/**
	 * Launcher控制
	 */
	public void systemLauncher() 
	{
		String funcName="systemLauncher";
		boolean ret;
		ApplicationPolicy appPolicy = mExDevicePolicyManager.getApplicationPolicy();

		// 测试前置
		gui.cls_show_msg("请先安装NovaLauncher、adwLauncher、AviateLauncher,安装完毕任意键继续");

		// case1:参数异常测试,不存在、""格式的包名应无法设置成功
		if ((ret = appPolicy.setDefaultLauncher(null)) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.setDefaultLauncher(new ComponentName("", ""))) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.setDefaultLauncher(new ComponentName("com.unexist", "com.unexist.MainActivity"))) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2.1:设置为第三方的Launcher
		if ((ret = appPolicy.setDefaultLauncher(new ComponentName("org.adwfreak.launcher", "org.adwfreak.launcher.MainActivity"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请点击Home键,进入设置-主屏幕查看是否选择了ADW 启动器增强版\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置Launcher为ADWLauncher失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = appPolicy.setDefaultLauncher(new ComponentName("com.tul.aviate", "com.tul.aviate.MainActivity"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请点击Home键,进入设置-主屏幕查看是否选择了Aviate Launcher\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置Launcher为Aviate Launcher失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2.2:切换回系统原生的Launcher应成功
		if ((ret = appPolicy.setDefaultLauncher(new ComponentName("com.android.launcher", "com.android.launcher.MainActivity"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请点击Home键,进入设置-主屏幕查看是否选择了启动器\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置Launcher为原生Launcher失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case3:多次设置同一个Launcher应成功
		int count = (int) (Math.random() * 10 + 1);
		for (int i = 0; i < count; i++) {
			if ((ret = appPolicy.setDefaultLauncher(new ComponentName("com.teslacoilsw.launcher", "com.teslacoilsw.launcher.MainActivity"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}

		if (gui.cls_show_msg("请点击Home键,进入设置-主屏幕查看是否选择Nova Launcher\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置Launcher为Nova Laucnher失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}

		// case4.1:移除Nova Launcher,该Launcher不会被删除
		count = (int) (Math.random() * 10 + 1);
		for (int i = 0; i < count; i++) {
			if ((ret = appPolicy.removeDefaultLauncher("com.teslacoilsw.launcher")) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}

		if (gui.cls_show_msg("请点击Home键,进入应用桌面查看NovaLauncher应用是否存在并且设置-主屏幕选项的默认Launcher不是该Launcher\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "Launcher设置测试通过");
	}

	/**
	 * 终端应用程序管理
	 */
	public void appManage() 
	{
		String funcName="appManage";
		boolean ret, ret1 = false;
		ApplicationPolicy appPolicy = mExDevicePolicyManager.getApplicationPolicy();

		// 测试前置
		gui.cls_show_msg("请确保已安装支付宝应用,完成任意键继续");
		// case1:参数异常测试,不存在应用以及null字符串应返回失败
		if ((ret = appPolicy.setDisableApplication("com.unexist")) == true|| (ret1 = appPolicy.setEnableApplication("com.unexist")) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s,%s)", Tools.getLineInfo(), TESTITEM, ret, ret1);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.setDisableApplication(null)) == true|| (ret1 = appPolicy.setEnableApplication(null)) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret, ret1);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.setDisableApplication("")) == true|| (ret1 = appPolicy.setEnableApplication("")) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret, ret1);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case1.1:禁止自身应用在桌面显示应成功
		if (gui.cls_show_msg("是否禁用HighPlatTest应用，点击是，则HighPlatTest应用在桌面、应用、设置-应用和后台进程中均不存在[HighPlatTest]图标，如果该测试点已通过，则点击取消跳过，是[确认],否[取消]") == ENTER) {
			if ((ret = appPolicy.setDisableApplication(mMap.get("highPlat"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}

		// case1.2:禁止系统应用在桌面显示应成功(设置、自检)
		if ((ret = appPolicy.setDisableApplication(mMap.get("setting"))) == false) // 卸载设置
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("在桌面、应用、设置-应用和后台进程中查看是否不存在[系统设置]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:系统设置图标仍显示在桌面", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.setDisableApplication(mMap.get("detect"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("在桌面、应用、设置-应用和后台进程中查看是否不存在[自检]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:自检图标仍显示在桌面", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case1.3:禁止第三方应用在桌面显示应成功
		// case3:多次隐藏同一个应用仍为隐藏状态
		int count = (int) (Math.random() * 10 + 1);
		for (int i = 0; i < count; i++) {
			if ((ret = appPolicy.setDisableApplication(mMap.get("aliPay"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}

		if (gui.cls_show_msg("在桌面、应用、设置-应用和后台进程中查看是否不存在[支付宝]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:支付宝图标仍显示在桌面", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case1.4:应用实现拖曳到首页，此时禁止或显示应用应可看到多个图标被禁止或显示
		gui.cls_show_msg("请先将相机应用拖曳到桌面,完成任意键继续");
		if ((ret = appPolicy.setDisableApplication("com.android.camera2")) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请在桌面、应用、设置-应用和后台进程中查是否不存在[相机]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:相机图标仍显示", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}

		// case2.1:显示相机应用应成功
		if ((ret = appPolicy.setEnableApplication(mMap.get("camera"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请在桌面、应用、设置-应用和后台进程中查看是否显示[相机]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:相机图标未显示", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		} 
		// case2.2:第三方应用,显示支付宝应用应成功
		if ((ret = appPolicy.setEnableApplication(mMap.get("aliPay"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请在桌面、应用、设置-应用和后台进程中查看是否显示[支付宝]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:支付宝图标未显示", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2.3:显示设置应用应成功
		if ((ret = appPolicy.setEnableApplication(mMap.get("setting"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue = false)
				return;
		}
		if (gui.cls_show_msg("请在桌面、应用、设置-应用和后台进程中查看是否显示[设置]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置图标未显示", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2.4:显示自检应用应成功
		// case4:多次显示同一个应用仍为显示
		count = (int) (Math.random() * 10 + 1);
		for (int i = 0; i < count; i++) {
			if ((ret = appPolicy.setEnableApplication(mMap.get("detect"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
				if (GlobalVariable.isContinue = false)
					return;
			}
		}

		if (gui.cls_show_msg("请在桌面、应用、设置-应用和后台进程中查看是否显示[自检]图标\n是[确认],否[取消]") != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:自检图标未显示", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过", TESTITEM);

	}

	/**
	 * 清理app缓存
	 */
	public void appCache() {
		String funcName="appCache";
		boolean ret = false;
		long size = 0;
		ApplicationPolicy appPolicy = mExDevicePolicyManager.getApplicationPolicy();

		// 测试前置
		gui.cls_show_msg("测试前请先安装饿了么和Nova Launcher应用,并且确保饿了么、Nova Laucnher、自检、设置、文件管理应用均有缓存数据,完成任意键继续");
		// case1:参数异常测试,清除不存在以及null应用包名不应闪退
		if ((ret = appPolicy.wipeApplicationData("com.unexist")) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.wipeApplicationData(null)) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = appPolicy.wipeApplicationData("")) == true) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2:清除系统自带应用数据应成功(自检、文件管理、设置等)
		if ((ret = appPolicy.wipeApplicationCache(mMap.get("detect"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除自检应用数据失败(%s)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// 需要对比清除前后的缓存数据大小,清除完缓存应为0B或12k
		if ((size = AppTool.getPackageSizeInfo(myactivity,mMap.get("detect"))) != 0&& (size = AppTool.getPackageSizeInfo(myactivity,mMap.get("detect"))) != 12 * 1024) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除自检缓存失败(%d)", Tools.getLineInfo(), size);
			if (GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = appPolicy.wipeApplicationData(mMap.get("setting"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除设置应用缓存失败(%s)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}

		if ((size = AppTool.getPackageSizeInfo(myactivity,mMap.get("setting"))) != 0&& (size = AppTool.getPackageSizeInfo(myactivity,mMap.get("setting"))) != 12 * 1024) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除设置应用缓存失败(%d)", Tools.getLineInfo(), size);
			if (GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = appPolicy.wipeApplicationData(mMap.get("file"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除文件系统应用数据失败(%s)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}

		if ((size = AppTool.getPackageSizeInfo(myactivity,mMap.get("file"))) != 0&& (size = AppTool.getPackageSizeInfo(myactivity,mMap.get("file"))) != 12 * 1024) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除文件系统应用缓存失败(%d)", Tools.getLineInfo(), size);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case3:清除第三方应用的数据应成功
		if ((ret = appPolicy.wipeApplicationData(mMap.get("elme"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除饿了吗应用数据失败(%s)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((size = AppTool.getPackageSizeInfo(myactivity,mMap.get("elme"))) != 0&& (size = AppTool.getPackageSizeInfo(myactivity,mMap.get("elme"))) != 12 * 1024) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除饿了么应用缓存失败(%d)", Tools.getLineInfo(), size);
			if (GlobalVariable.isContinue == false)
				return;
		}

		// case4:清除Launcher应用的数据应成功
		if ((ret = appPolicy.wipeApplicationCache(mMap.get("Nova"))) == false) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除Nova应用缓存失败(%s)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((size = AppTool.getPackageSizeInfo(myactivity,mMap.get("Nova"))) != 0&& (size = AppTool.getPackageSizeInfo(myactivity,mMap.get("Nova"))) != 12 * 1024) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除Nova应用缓存失败(%d)", Tools.getLineInfo(), size);
			if (GlobalVariable.isContinue == false)
				return;
		}

		// case5:清除自身应用数据
		if (gui.cls_show_msg("是否清除自身应用数据，点击是则自身应用缓存被清除，如果该测试点已通过，则点击取消跳过\n是[确认],否[取消]") == ENTER) {
			if ((ret = appPolicy.wipeApplicationData(mMap.get("highPlat"))) == false) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除自身应用数据失败(%s)", Tools.getLineInfo(), ret);
				if (GlobalVariable.isContinue == false)
					return;
			}
			if ((size = AppTool.getPackageSizeInfo(myactivity,mMap.get("highPlat"))) != 0&& (size = AppTool.getPackageSizeInfo(myactivity,mMap.get("highPlat"))) != 12 * 1024) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清除自身应用缓存失败(%d)", Tools.getLineInfo(), size);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {

	}


}
