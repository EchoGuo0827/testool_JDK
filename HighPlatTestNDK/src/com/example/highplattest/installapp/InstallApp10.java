package com.example.highplattest.installapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.SettingsManager;
import android.newland.content.NlIntent;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: 阿里固件应用卸载黑名单接口
 * file name 		: InstallApp10.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20181106
 * directory 		: 增加阿里固件设置/获取应用卸载黑名单
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 * 					 wangxy		       20181106   	    created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp10 extends UnitFragment 
{
	private final String TESTITEM = "阿里固件设置/获取应用卸载黑名单";
	public final String TAG = InstallApp10.class.getSimpleName();
	private ApkBroadCastReceiver apkReceiver;
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	private final int APKTIME = 60; /**每个广播接收时间为1分钟*/
	private Gui gui = new Gui(myactivity, handler);
	long startTime;
	int time = 0;
	int succ=0;
	int cnt=0;
	boolean isSecond=false;
	Uri uri =null;
	Intent intent2 = null;
	private SettingsManager settingsManager = null;
	/*private String[] apkPath ={GlobalVariable.sdPath+"apk/淘宝.apk",
			GlobalVariable.sdPath+"apk/支付宝.apk",
			GlobalVariable.sdPath+"apk/ali-mtms-1.0.17_sig.apk",
			GlobalVariable.sdPath+"apk/QQ.apk",
			GlobalVariable.sdPath+"apk/UC浏览器.apk",
			GlobalVariable.sdPath+"apk/大鱼吃小鱼.apk",
			GlobalVariable.sdPath+"apk/钉钉.apk",
		};*/
	Map<String, String> mMap = new HashMap<String, String>();
	Map<String, String> mMap2 = new HashMap<String, String>();
	private List<String> getblackList=null;
	private List<String> setblackList=null;
	boolean ret=false;
	private String blackStr="淘宝;支付宝;MTSM;QQ;UC浏览器;文件管理;自检";
	
	public void installapp10() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		if(GlobalVariable.gCustomerID != CUSTOMER_ID.AliBaBa)// 只有阿里的固件才支持该接口
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gScreenTime, "%s固件不支持%s接口", GlobalVariable.gCustomerID,TESTITEM);
			return;
		}
		
		if(gui.cls_show_msg("该用例需要在manifest中添加mtms权限,已添加[确认],未添加[其他]")!=ENTER)
		{
			gui.cls_printf("请先在manifest添加mtms权限再进入本用例".getBytes());
			return;
		}
		
		mMap.put("淘宝","com.taobao.taobao");
		mMap.put("支付宝","com.eg.android.AlipayGphone");
		mMap.put("MTMS","com.newland.pospp.mtms.client.n900");
		mMap.put("QQ","com.tencent.mobileqq");
		mMap.put("UC浏览器","com.uc.browser.hd");
		mMap.put("文件管理","com.android.qrdfileexplorer");
		mMap.put("自检","com.newland.detectapp");
		
		setblackList=new ArrayList<String>(mMap.values());
		//用于adb命令验证
		mMap2.put("淘宝","adb shell am start com.taobao.taobao/com.taobao.tao.homepage.MainActivity3");
		mMap2.put("支付宝","adb shell am start com.eg.android.AlipayGphone/.AlipayLogin");
		mMap2.put("MTMS","adb shell am start com.newland.pospp.mtms.client.n900/.activity.MtmsActivity");
		mMap2.put("QQ","adb shell am start com.tencent.mobileqq/.activity.SplashActivity");
		mMap2.put("UC浏览器","adb shell am start com.uc.browser.hd/com.UCMobile.main.UCMobile");
		mMap2.put("文件管理","adb shell am start com.android.qrdfileexplorer/.MainActivity");
		mMap2.put("自检","adb shell am start com.newland.detectapp/.MenuItemListActivity");
		
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1(2, "line %d:抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
			return;
		}
		gui.cls_show_msg("该用例需在控制台关闭和开启情况下都进行测试，测试前请确对应apk文件夹下的各个应用均未安装，确认则点击任意键继续");
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("阿里固件\n0.卸载相关应用\n1.应用卸载黑名单\n2.应用禁用启用");
			switch (returnValue) 
			{
			case '0':
				beforeUninstall();//测试前置，用于卸载测试时相关apk，构造环境
				break;
			case '1':
				installBlack();
				break;
			case '2':
				AppBlockUp();
				break;
			case ESC://取消键
				unitEnd();
				return;
			}
		}
	}
	
	private void installBlack() {
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("应用卸载黑名单\n0.安装-设置黑名单-卸载\n1.设置黑名单-安装-卸载\n");
			switch (nkeyIn) 
			{
			case '0':
				installBlack(true);
				break;
			case '1':
				installBlack(false);
				break;
			case ESC:
				return;
			}
		}
	}

	//卸载所有相关测试apk
	private void beforeUninstall() {
		// TODO Auto-generated method stub
		if ((gui.ShowMessageBox(("此case用于卸载当前设备中的测试相关的应用，构造未安装过测试黑名单中的apk的环境，如需此操作则点击【确认】，否则点击【取消】跳出此前置操作").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1(2,"即将退回上一级菜单");
			return;
		}
		//卸载测试时应用
		gui.cls_show_msg1(2,"测试相关应用卸载中，请耐心等待");
		for (String key :mMap.keySet()) 
		{
			expPackName = mMap.get(key);
			Uri uri3 = Uri.parse("package:"+expPackName);
			Intent intentDel3 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri3);
			myactivity.startActivity(intentDel3);
			time = 0;
			// 循环等待1分钟
			startTime = System.currentTimeMillis();
			while(time<APKTIME)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
		}
		gui.cls_show_msg1(2,"已卸载当前设备中的测试相关的应用,可返回桌面或设置-应用中查看");
	}

	//应用禁用启用
	private void AppBlockUp() {
		gui.cls_show_msg("请确认apk文件夹下的应用已全部安装，如未安装可通过下载工具进行安装，完成后按任意键继续");
		WAITMAXTIME=0;
		//正常测试
		//case1:应用禁用
		gui.cls_show_msg1(2,"即将进行设置应用禁用。。。");
		for(String key :mMap.keySet())
		{
			gui.cls_show_msg1(2,"设置"+key+"应用禁用。。。");
			if(!(ret=settingsManager.disableAPK(mMap.get(key), true)))//应用是否禁用，根据包名,true为禁用状态
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置应用禁用测试失败(package = %s)",Tools.getLineInfo(), TESTITEM,mMap.get(key));
				if (!GlobalVariable.isContinue)
					return;
			}
			if(!(ret=settingsManager.getAPKDisableState(mMap.get(key))))//获取应用状态,禁用状态则返回true
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s应用禁用状态不一致(package = %s)",Tools.getLineInfo(), TESTITEM,mMap.get(key));
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if ((gui.ShowMessageBox(("请在桌面、应用、设置-应用和后台进程中查看是否不存在"+blackStr+"图标\n是[确认],否[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s应用停用效果不一致",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		//通过adb命令查看
		for(String key :mMap2.keySet())
		{
			if ((gui.ShowMessageBox(("通过命令"+mMap2.get(key)+"查看是否开启"+key+"应用失败，预期应打开失败\n是[确认],否[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s应用禁用后启动测试异常",Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		//case2：应用启用
		gui.cls_show_msg1(2,"即将进行设置应用启用。。。");
		for(String key :mMap.keySet())
		{
			gui.cls_show_msg1(2,"设置"+key+"应用启用。。。");
			if(!(ret=settingsManager.disableAPK(mMap.get(key), false)))//应用启用，根据包名,false为启用
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置应用启用测试失败(package = %s)",Tools.getLineInfo(), TESTITEM,mMap.get(key));
				if (!GlobalVariable.isContinue)
					return;
			}
			if((ret=settingsManager.getAPKDisableState(mMap.get(key))))//获取应用状态,启用状态则返回false
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s应用状态不一致(package = %s)",Tools.getLineInfo(), TESTITEM,mMap.get(key));
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if ((gui.ShowMessageBox(("请在桌面、应用、设置-应用和后台进程中查看是否存在"+blackStr+"图标且各应用可正常使用\n是[确认],否[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s应用启用效果不一致",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 参数异常测试
		gui.cls_show_msg1(2, "参数异常测试。。。");
		if (ret = settingsManager.disableAPK(null, true)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置应用禁用参数异常测试失败", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (ret = settingsManager.disableAPK("", false)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置应用禁用参数异常测试失败", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 传入不存在的包名返回false
		if (ret = settingsManager.disableAPK("com.example.unexit", true)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置应用禁用参数异常测试失败", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(TAG, "installapp10", gScreenTime,"%s测试通过", TESTITEM);
	}

	//设置/获取应用卸载黑名单
	private void installBlack(boolean isInstallBefore) {
		//isInstallBefore为true则安装-设置黑名单-卸载，false则为设置黑名单-安装-卸载
		//前置获取应用卸载黑名单
		WAITMAXTIME=0;
		getblackList=settingsManager.getUninstallAPKBlackList();
		if ((gui.ShowMessageBox(("请确认获取到的应用卸载黑名单列表为：" +ISOUtils.listToStr(getblackList,';')+"\n预期首次进入本用例测试，获得的列表为空").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败(blackList = %s)",Tools.getLineInfo(), TESTITEM,ISOUtils.listToStr(getblackList,';'));
			if (!GlobalVariable.isContinue)
				return;
		}
		//case1:设置应用卸载黑名单，阿里相关应用,淘宝，支付宝;市场常用应用,QQ，UC浏览器;MTSM实际应用;系统应用		
		if (isInstallBefore) 
		{
			gui.cls_show_msg("通过下载工具将apk文件夹中的所以应用进行下载安装，完成后点击任意键继续");
		} else {
			if ((gui.ShowMessageBox(("请确认当前设备未安装apk文件夹中的黑名单测试apk，是则点击【确定】，否则点击【取消】将返回上一级，可进入前置中清除测试应用或测试人员手动卸载应用").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME)) != BTN_OK) 
			{
				gui.cls_show_msg1(2,"将返回上一级，可进入前置中清除测试应用或测试人员手动卸载应用后再次进入本case");
				return;
			}
		}			
		gui.cls_show_msg1(2,"即将设置应用卸载黑名单...");
		if(!settingsManager.setUninstallAPKBlackList(setblackList, false))//设置为false则加入黑名单，不允许卸载，设置为true则踢出黑名单
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置黑名单失败失败(blackList = %s)",Tools.getLineInfo(), TESTITEM,ISOUtils.listToStr(getblackList,';'));
			if (!GlobalVariable.isContinue)
				return;
		}
		getblackList=settingsManager.getUninstallAPKBlackList();
		if ((gui.ShowMessageBox(("请确认获取到的应用卸载黑名单列表为：" +getblackList+"\n预期列表中包名对应的应用为"+blackStr).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败(blackList = %s,blackStr=%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.listToStr(getblackList,';'),blackStr);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (!isInstallBefore) 
		{
			gui.cls_show_msg("通过下载工具将apk文件夹中的所以应用进行下载安装，完成后点击任意键继续");
		}
		
		//case1.1手动无法卸载卸载黑名单中的应用
		if ((gui.ShowMessageBox(("请确认应用卸载黑名单中的应用"+blackStr+"等是否无法手动卸载，而其他非黑名单应用可以卸载").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case1.2显示无法卸载卸载黑名单中的应用
		gui.cls_show_msg1(2, "即将显式卸载应用卸载黑名单中的应用，预期应卸载失败...");
        StringBuffer blackAppName = new StringBuffer();
		for (String key :mMap.keySet()) 
		{
			blackAppName.append(key+";");
			expPackName = mMap.get(key);
			Uri uri2 = Uri.parse("package:" + expPackName);
			Intent intent2 = new Intent(Intent.ACTION_DELETE, uri2);
			myactivity.startActivity(intent2);
			gui.cls_show_msg(key+"应用卸载完毕点击任意键");
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_APP_WHITE_UNINSTALL) 
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s卸载%s测试失败(apk = %s，%d)", Tools.getLineInfo(), TESTITEM,expPackName,currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if ((gui.ShowMessageBox(("请确认"+blackAppName+"等应用是否没有被卸载掉").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case1.3隐式无法卸载卸载黑名单中的应用
		gui.cls_show_msg1(2,"即将隐式卸载应用卸载黑名单中的应用，预期应卸载失败...");
		for (String key :mMap.keySet()) {
			expPackName = mMap.get(key);
			gui.cls_show_msg1(2, "卸载"+key+"应用，【取消】退出测试");
			Uri uri3 = Uri.parse("package:"+expPackName);
			Intent intentDel3 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri3);
			myactivity.startActivity(intentDel3);
			time = 0;
			// 循环等待1分钟
			startTime = System.currentTimeMillis();
			while(time<APKTIME)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_WHITE_UNINSTALL) 
			{
				gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr,"line %d:卸载禁止卸载黑名单中的app测试失败(apk = %s,%s，ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if ((gui.ShowMessageBox(("请确认"+blackAppName+"等应用是否没有被卸载掉，预期卸载失败").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case1.4:清空应用禁止卸载黑名单
		gui.cls_show_msg1(2,"即将清空设置应用卸载黑名单...");
		if(!settingsManager.setUninstallAPKBlackList(setblackList,true))//设置为false则加入黑名单，不允许卸载，设置为true则踢出黑名单
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置黑名单失败失败(blackList = %s)",Tools.getLineInfo(), TESTITEM,ISOUtils.listToStr(getblackList,';'));
			if (!GlobalVariable.isContinue)
				return;
		}
		getblackList=settingsManager.getUninstallAPKBlackList();
		if ((gui.ShowMessageBox(("请确认获取到的应用卸载黑名单列表为：" +getblackList+"\n预期列表为空").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败(blackList = %s)",Tools.getLineInfo(), TESTITEM,ISOUtils.listToStr(getblackList,';'));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		if ((gui.ShowMessageBox(("请确认当前设备中除了系统应用外是否均可卸载").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case2：异常参数测试
		gui.cls_show_msg1(2, "参数异常测试...");
		if (settingsManager.setUninstallAPKBlackList(null, false)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置黑名单参数异常失败", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 空的list
		if (settingsManager.setUninstallAPKBlackList(new ArrayList<String>(), true)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp10", gKeepTimeErr, "line %d:%s设置黑名单参数异常失败", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
	}

	@Override
	public void onTestUp() 
	{
		apkReceiver = new ReceiverTracker().new ApkBroadCastReceiver();
		registApk();
	}

	@Override
	public void onTestDown() 
	{
		unRegistApk();
		gui = null;
		apkReceiver = null;
	}
	
	private void registApk()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.INSTALL_APP");
		intentFilter.addAction("android.intent.action.INSTALL_APP_HIDE");
		intentFilter.addAction("android.intent.action.DELETE_APP");
		intentFilter.addAction("android.intent.action.DELETE_APP_HIDE");
		myactivity.registerReceiver(apkReceiver, intentFilter);
	}
	
	private void unRegistApk()
	{
		if(this != null)
		{
			Log.d("unregistApk", "delete");
			myactivity.unregisterReceiver(apkReceiver);
		}
	}
}
