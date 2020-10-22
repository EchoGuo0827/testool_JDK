package com.example.highplattest.installapp;

import java.io.File;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;

/**description 		: InstallApp18
* history 		 	: 变更记录																			变更时间				变更人员
* 					   创建：移除静默安装（targetSDK >= 26）对于REQUEST_INSTALL_PACKAGES权限的检查。(Android版本大于26需测试)		   		20200429	 		郑薛晴	
* 					测试apk后缀名修改					20201010  				陈丁
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class InstallApp18 extends UnitFragment
{
	private final String TESTITEM = "REQUEST_INSTALL_PACKAGES权限测试";
	public final String TAG = InstallApp18.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private ApkBroadCastReceiver apkReceiver;

	public void installapp18()
	{
		if(Build.VERSION.SDK_INT<26)
		{
			gui.cls_show_msg2(0.1f,"当前SDK版本小于26,不支持该测试用例(SDK=%s)",Build.VERSION.SDK_INT);
			return;
		}
		
		int time = 0;
		long startTime=0;
		final int APKTIME = 60; /**每个广播接收时间为1分钟*/
		String currentName=null;/**实际得到的包名*/
		String funcName = "installapp18";
		
		if(myactivity.getApplicationInfo().targetSdkVersion<26)
			gui.cls_show_msg("【A7以上平台请打开xml文件中的provider标签】请确保测试apk放置到内置SD卡的apk/目录下，各个应用均未安装\nmanifest的target值要修改到26以上");
		else
			gui.cls_show_msg("【A7以上平台请打开xml文件中的provider标签】请确保测试apk放置到内置SD卡的apk/目录下，各个应用均未安装");
		
		
		gui.cls_show_msg1(1,"隐式安装:QQ浏览器安装成功视为测试通过");
		Intent intent_A = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent_A.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent_A.setDataAndType(Uri.fromFile(new File(GlobalVariable.sdPath+"apk/QQliulanqi_sign.apk")),"application/vnd.android.package-archive");
		myactivity.startActivity(intent_A);
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(GlobalVariable.sdPath+"apk/QQliulanqi_sign.apk"))
				break;
		}
		if(currentName.equals(GlobalVariable.sdPath+"apk/QQliulanqi_sign.apk")==false||(respCode=apkReceiver.getResp(APK_INSTALL)) != 0)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试失败(pack = %s,ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
				
		gui.cls_show_msg1(1,"显式安装:QQ音乐安装成功视为测试通过");
		Intent intent_B = new Intent(Intent.ACTION_VIEW);
		intent_B.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent_B.setDataAndType(Uri.fromFile(new File(GlobalVariable.sdPath+"apk/QQyinle_586_sign.apk")),"application/vnd.android.package-archive");
		myactivity.startActivity(intent_B);
		gui.cls_show_msg1_record(TAG, funcName, gScreenTime, "QQ音乐和QQ浏览器均安装成功视为测试通过,请手动卸载QQ音乐和QQ浏览器", TESTITEM);
		
	}
	
	private void registApk()
	{
		LoggerUtil.d("registApk");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.INSTALL_APP_HIDE");
//		intentFilter.addAction("android.intent.action.DELETE_APP");
//		intentFilter.addAction("android.intent.action.DELETE_APP_HIDE");
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
	
	@Override
	public void onTestUp() {
		apkReceiver = new ReceiverTracker().new ApkBroadCastReceiver();
		registApk();
	}

	@Override
	public void onTestDown() {
		unRegistApk();
		gui = null;
		apkReceiver = null;
	}

}
