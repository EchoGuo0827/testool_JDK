package com.example.highplattest.installapp;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;
import com.newland.ndk.JniNdk;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: 江西农信/通联总公司/招商银行信静默安装app
 * file name 		: InstallApp8.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180530
 * directory 		: 
 * description 		: 测试installApp
 * related document : 
 * history 		 	: author			date			remarks
 * 					 wangxy		       20180530	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp8 extends UnitFragment 
{
	private final String TESTITEM = "江西农信/通联总公司/招商银行安装与卸载方式，隐性";
	public final String TAG = InstallApp8.class.getSimpleName();
	private ApkBroadCastReceiver apkReceiver;
	private final int BUFFERSIZE = 1024*200;
	private String errPackName;/**错误无效的apk路径*/
	private String expPackPath;/**预期的apk路径*/
	private String expPackName;/**预期的包名*/
	private String currentName1,currentName2;/**实际得到的包名*/
	private final int APKTIME = 60; /**每个广播接收时间为1分钟*/
	private Gui gui = new Gui(myactivity, handler);
//	private boolean isJXNX=false,isTL=false,isZSYH=false;
	private String signName;
	Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
	long startTime;
	int time = 0;
	private float InstallAppTime;
	private int delay;
	int succ=0;
	int cnt=0;
	byte[] writebuf = new byte[BUFFERSIZE];
	String fname = GlobalVariable.sdPath+"test.txt";
	boolean isSecond=false;
	public void installapp8() 
	{
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		
		while(true)
		{
			
			int returnValue=gui.cls_show_msg("江西农信/通联总公司/招商银行安装与卸载方式，隐性\n0.配置验签体系\n1.静默安装与卸载\n2.重复多次静默安装");
			switch (returnValue) 
			{
			case '0':
				sign_config();
				break;
				
			case '1':
				installTest();
				break;
				
			case '2':
				MoreInstallTest();//add by wangxy20180629
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
				
			}
		}
		
	}
	/**
	 * 重复多次安装带签名的apk（客诉招行签名体系下，连续重复安装返回-3，其中返回-1是正常的情况,也存在返回-4的存储空间不足实际有空间的）
	 * @author wangxy by20180629
	 * 
	 */
	private void MoreInstallTest() {
		
		if(signName==null)
		{
			gui.cls_show_msg("未配置签名体系,请先进行配置,点任意键继续");
			return;
		}
		// 确保测试文件存在
		gui.cls_show_msg("该用例需在控制台开启情况下测试，测试前请确保证书已与%s一致,并把对应相关测试apk放置到内置SD卡的apk/目录下，各个应用均未安装",signName);
		int total = gui.JDK_ReadData(30, 20);// 默认次数100次
		succ = 0;
		Uri uri3;
		Intent intent3;
		float time1;

		for (cnt = 1; cnt <=total;) {
			if (gui.cls_show_msg1(3, "正在进行第%d次%s(已成功%d次),[取消]键退出测试", cnt, TESTITEM, succ) == ESC)
				break;
			isSecond = false;
			// 测试前置，经过一次安装卸载得到apk安装所需的时长，由于每台机具的安装时间有差异
			apkReceiver.setHideInstallCount(0);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackPath)), "application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			// 循环等待1分钟
			time = 0;
			startTime = System.currentTimeMillis();
			while (time < APKTIME) {
				time = (int) Tools.getStopTime(startTime);
				currentName1 = apkReceiver.getPackName(APK_INSTALL, 1);
				if (currentName1 != null && currentName1.equals(expPackPath))
					break;
			}
			InstallAppTime = Tools.getStopTime(startTime);
			if (currentName1.equals(expPackPath) == false|| (respCode = apkReceiver.getResp(APK_INSTALL, 1)) != PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "line %d:第%d次静默安装带%s签名的apk失败(apk = %s,%s，%d)", Tools.getLineInfo(),cnt,signName, expPackPath, currentName1, respCode);
				continue;
			}
			// 卸载，需apk包名
			uri3 = Uri.parse("package:" + expPackName);
			intent3 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri3);
			myactivity.startActivity(intent3);
			// 循环等待一分钟
			time = 0;
			startTime = System.currentTimeMillis();
			while (time < APKTIME) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(2000);
				if ((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "line %d:第%d次静默卸载带%s证书的apk测试失败(apk = %s,%s,%d)",Tools.getLineInfo(), cnt,signName, expPackName, currentName1, respCode);
				continue;
			}
			if (InstallAppTime < 3) {
				gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "第%d次,apk获取安装时长失败InstallAppTime=%f", cnt,InstallAppTime);
				continue;
			}
			delay = (int) ((InstallAppTime - 2) * 1000);//计算出来的第二次安装与第一次相比延迟的时间

			// 第一次主线程中安装apk
			gui.cls_show_msg1(2, "正在进行第%d次重复安装带%s证书的apk【取消】退出测试",cnt,signName);
			apkReceiver.setHideInstallCount(0);// 重置次数计数为0
			myactivity.startActivity(intent);// 第一次安装，预计14-18s安装完成

			// 开起多线程延时再次安装apk
			Thread thread = new Thread(run);
			thread.start();// 第二次的安装是第一次即将安装完成前的2-3.6s

			time = 0;
			startTime = System.currentTimeMillis();
			while (time < APKTIME) {// 循环等待1分钟
				time = (int) Tools.getStopTime(startTime);
				currentName1 = apkReceiver.getPackName(APK_INSTALL, 1);
				if (currentName1 != null && (currentName1).equals(expPackPath))
					break;
			}
			time1 = Tools.getStopTime(startTime);
			if (currentName1.equals(expPackPath) == false|| (respCode = apkReceiver.getResp(APK_INSTALL, 1)) != PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "line %d:第%d次静默安装带%s签名的apk失败第一次(apk = %s,%s，%d)",Tools.getLineInfo(), cnt,signName, expPackPath, currentName1, respCode);
				continue;
			}

			// 等待线程中的apk安装完成后继续向下执行
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 测试后置，卸载，需apk包名
			gui.cls_show_msg1(2, "第%d次测试后置,卸载带%s证书的apk,【取消】退出测试", cnt,signName);
			uri3 = Uri.parse("package:" + expPackName);
			intent3 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri3);
			myactivity.startActivity(intent3);
			// 循环等待一分钟
			time = 0;
			startTime = System.currentTimeMillis();
			while (time < APKTIME) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(2000);
				if ((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "line %d:第%d次静默卸载带%s证书的apk测试失败(apk = %s,%s,%d)",Tools.getLineInfo(), cnt,signName, expPackName, currentName1, respCode);
				continue;
			}
			
			// 重复安装已完成，判断当前次测试是否有效
			int subTime = (int) (time1 * 1000 - delay);
			// 2.0-3.6s间隔安装为有效测试
			if (subTime > 2000 && subTime < 3600) 
			{
				cnt++;
				if (isSecond) 
					succ++;
			} else 
				gui.cls_show_msg1(2, "%s当前次重复安装测试数据因时间间隔不在范围内，故无效不加入计数", signName);
		}
		gui.cls_show_msg1_record(TESTITEM, "MoreInstallTest", 0,"%s测试通过,当前签名体系为%s,测试总次数%d次,成功%d次", TESTITEM,signName,cnt-1,succ);
	}
	//配置签名体系
	public void sign_config()
	{
		while(true)
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				signName="江西农信";
				expPackPath = GlobalVariable.sdPath + "apk/jxnx_tframework_V1.1.12.1805152358_21_release_xgd_v2_basev2.apk";
				expPackName = "com.jxnx.deviceservice";
			}
			
			int nkeyIn = gui.cls_show_msg("签名体系配置\n0.江西农信\n1.通联总公司\n2.招商银行");
			switch (nkeyIn) 
			{
			case '0':
				signName="江西农信";
				expPackPath = GlobalVariable.sdPath + "apk/jxnx_tframework_V1.1.12.1805152358_21_release_xgd_v2_basev2.apk";
				expPackName = "com.jxnx.deviceservice";
				return;
				
			case '1':
				signName="通联总公司";
				expPackPath = GlobalVariable.sdPath + "apk/USDK_CASH_sign.apk";
				expPackName = "com.allinpay.usdk.cash";
				return;
				
			case '2':
				signName="招商银行";
				expPackPath = GlobalVariable.sdPath + "apk/CMBPayService_V0.5.8_B55_20180521-PROC-release_sign.apk";
				expPackName = "com.cmbchina.pospay";
				return;
				
			case ESC:
				break;

			default:
				break;
			}

		}
	}
	
	//各体系静默安装、卸载
	private void installTest(){	
		
		if(signName==null)
		{
			gui.cls_show_msg("未配置签名体系,请先进行配置,点任意键继续");
			return;
		}
		// 确保测试文件存在
		gui.cls_show_msg("该用例需在控制台开启情况下测试，测试前请确保证书已与%s一致,并把对应相关测试apk放置到内置SD卡的apk/目录下，各个应用均未安装",signName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// case1:参数异常，参数为null和apk路径包错误，抛出相应的异常
		if(gui.cls_show_msg1(2, "参数异常，参数为null和apk路径包错误，抛出相应的异常，【取消】退出测试")==ESC)
			return;
		errPackName = GlobalVariable.sdPath+"apk/unExist.apk";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(errPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(errPackName))
				break;
		}
		
		if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(errPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_INVALID_APK)
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默安装不存在apk测试失败(apk = %s,%s，ret = %d)", Tools.getLineInfo(),errPackName,currentName1,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:异常测试，卸载不存在的apk文件，返回-201
		errPackName = "com.unexist.name";
		Uri uri12 = Uri.parse("package:com.unexist.name");
		Intent intent12 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri12);
		myactivity.startActivity(intent12);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(errPackName))
				break;
		}
		if ((currentName1=apkReceiver.getPackName(APK_UNINSTALL)).equals(errPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND) 
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默卸载app测试失败（apk = %s,%s,%d）", Tools.getLineInfo(),errPackName,currentName1,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		/**
		 * 江西农信签名体系正确签名apk：jxnx_tframework_V1.1.12.1805152358_21_release_xgd_v2_basev2.apk--->包名：com.jxnx.deviceservice
		 * 通联总公司正确签名apk：USDK_CASH_sign.apk--->包名：com.allinpay.usdk.cash
		 * 招商银行正确签名apk：CMBPayApp_V1.0.3_B4_20180507-release_sign.apk--->包名：com.cmbchina.paylauncher
		 * 系统权限apk：STANDARD_MTMS-CLIENT_1.0.36_release_standard_user_issue_sign.apk--->包名：com.newland.pospp.mtms.client.n900
		 * 空白签名apk：N900Installer.apk:空签名的apk,-104--->包名：com.newland.n900installer
		 * 错误签名apk：yinsheng_0819_4_ysSigned.apk:其他验签体系签名的非江西农信签名的apk,-104--->包名：com.ysepay.pos.deviceservice
		 * 市面流行apk：QQliulanqi.apk:android签名的非江西农信签名的apk,-104--->包名：com.tencent.mtt
		*/
		
		//case3:安装带签名的apk，需apk路径
	    gui.cls_show_msg1(2, "安装带%s签名apk,【取消】退出测试",signName);
		intent.setDataAndType(Uri.fromFile(new File(expPackPath)), "application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while (time < APKTIME) {
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(2000);
			if ((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackPath))
				break;
		}
		if ((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackPath) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) {
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默安装带%s签名的apk失败(apk = %s,%s，%d)",Tools.getLineInfo(), signName,expPackPath, currentName1, respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		//卸载，需apk包名
		gui.cls_show_msg1(2, "卸载带%s证书的apk,【取消】退出测试",signName);
		Uri uri3 = Uri.parse("package:"+expPackName);
		Intent intent3 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri3);
		myactivity.startActivity(intent3);
		// 循环等待一分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while (time < APKTIME) {
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(2000);
			if ((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
				break;
		}
		if ((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) {
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默卸载带%s证书的apk测试失败(apk = %s,%s,%d)",Tools.getLineInfo(),signName, expPackName, currentName1, respCode);
			if (!GlobalVariable.isContinue)
				return;
		}

		// case4:系统权限apk：包名：com.newland.pospp.mtms.client.n900
		gui.cls_show_msg1(2, "安装STANDARD_MTMS-CLIENT_1.0.36_release_standard_user_issue_sign.apk,【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/STANDARD_MTMS-CLIENT_1.0.36_release_standard_user_issue_sign.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默安装STANDARD_MTMS-CLIENT_1.0.36_release_standard_user_issue_sign.apk失败(apk = %s,%s，%d)", Tools.getLineInfo(),expPackName,currentName1,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		//卸载
		gui.cls_show_msg1(2, "卸载STANDARD_MTMS-CLIENT_1.0.36_release_standard_user_issue_sign.apk,【取消】退出测试");
		Uri uri4 = Uri.parse("package:com.newland.pospp.mtms.client.n900");
		Intent intent4 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri4);
		myactivity.startActivity(intent4);
		expPackName = "com.newland.pospp.mtms.client.n900";
		// 循环等待一分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName1 = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
				break;
		}
		if ((currentName1=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName1,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}		
		
		//case5:空签名的apk,-104--->包名：com.newland.n900installer
		gui.cls_show_msg1(2, "安装空签名的apk，【取消】退出测试");
		expPackName= GlobalVariable.sdPath+"apk/N900Installer.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName,currentName1,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case6:市场流行apk,-104--->包名：com.tencent.mtt
		gui.cls_show_msg1(2, "安装市场上流行的但非江西农信签名的apk，【取消】退出测试");
		expPackName= GlobalVariable.sdPath+"apk/QQliulanqi.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
		{
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName,currentName1,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}		

		// case7:错误签名apk,-104--->包名：com.ysepay.pos.deviceservice
		gui.cls_show_msg1(2, "安装其他验签体系签名即错误签名的apk，【取消】退出测试");
		expPackName = GlobalVariable.sdPath + "apk/yinsheng_0819_4_ysSigned.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		time = 0;
		startTime = System.currentTimeMillis();
		while (time < APKTIME) {
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if ((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if ((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED) {
			gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName, currentName1, respCode);
			if (!GlobalVariable.isContinue)
				return;
		}	
		
		// case8:存储空间满，安装应用应失败
		if (gui.ShowMessageBox("是否立即进行写满SD卡测试，写满设备操作要等待5分钟以上".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			while (true) {
				if (new FileSystem().JDK_FsWrite(fname, writebuf,writebuf.length, 2) != writebuf.length) 
				{
					break;
				}
			}
			expPackName = GlobalVariable.sdPath+"apk/UCliulanqi_cmbc.apk";//安装UC浏览器
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			// 循环等待1分钟
			time = 0;
			startTime = System.currentTimeMillis();
			while(time<APKTIME)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
					break;
			}
				if ((currentName1 = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!= ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED) 
				{
					gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"line %d:%s空间不足时，安装UC浏览器测试失败（apk=%s,%s，%d）",Tools.getLineInfo(), TESTITEM, expPackName,currentName1,respCode);
					if (!GlobalVariable.isContinue) 
					{
						// 删除文件，释放空间
						new FileSystem().JDK_FsDel(fname);
						return;
					}
						
			}
			
			// 删除文件，释放空间
			new FileSystem().JDK_FsDel(fname);
		}
		gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr,"%s测试通过", TESTITEM);
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
	
	private void registApk()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.INSTALL_APP_HIDE");
		intentFilter.addAction("android.intent.action.DELETE_APP_HIDE");
		myactivity.registerReceiver(apkReceiver, intentFilter);
	}
	
	private void unRegistApk()
	{
		if(this != null)
		{
			myactivity.unregisterReceiver(apkReceiver);
		}
	}
	Runnable run=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		    JniNdk.JNI_Sys_MsDelay(delay);//第一次即将安装完成前的2-3秒开始下一次安装，必现-3bug
			myactivity.startActivity(intent);//预计14-17s安装完成
			// 循环等待1分钟
			time = 0;
			long startTime = System.currentTimeMillis();
			while (time < APKTIME) {
				time = (int) Tools.getStopTime(startTime);
				currentName2 = apkReceiver.getPackName(APK_INSTALL,2);
				if (currentName2!=null&&(currentName2).equals(expPackPath))
					break;
			}
//			float time=Tools.getStopTime(startTime);
			if (currentName2 .equals(expPackPath) == false|| ((respCode = apkReceiver.getResp(APK_INSTALL,2)) == -1||respCode==0)==false) {
				gui.cls_show_msg1_record(TAG, "installapp8", gKeepTimeErr, "line %d:多次静默安装带%s签名的apk失败，第2次未成功安装(apk = %s,%s,%d)", Tools.getLineInfo(),signName, expPackPath, currentName2, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}else{
				isSecond=true;
			}
		}
	};
	

}
