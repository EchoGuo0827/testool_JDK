package com.example.highplattest.installapp;

import java.io.File;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: Android设定静默安装app
 * file name 		: InstallApp2.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150617
 * directory 		: 
 * description 		: 测试installApp
 * related document : 
 * history 		 	: author			date			remarks
 * 					 zhengxq		   20150617	 		created	
 *  				: 变更点											变更时间				变更人员
 * 					 全平台系统修改安装普通第三方应用均需要验签。案例返回值和测试APK修改      20200526                                    陈丁
 * 						(A5部分机型除外，无法确认具体A5哪些版本，故按照默认均有验签)
 * 						测试点不能因开启全平台验签而改变，应用安装返回值修改回原测试返回值            20200529                                    陈丁  
 * 					开发回复:现在只对应用做验签的返回值判断。其他错误返回只要不返回0即可   
 *						修复返回值错误不兼容问题。修复A1应用未获取到第一次广播返回值问题		20200922					陈丁  
 * 						测试apk后缀区分签名应用与未签名应用。方便测试人员辨认						20201009		陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp2 extends UnitFragment 
{
	private final String TESTITEM = "Google安装与卸载方式，隐性";
	public final String TAG = InstallApp2.class.getSimpleName();
	private ApkBroadCastReceiver apkReceiver;
	private final int BUFFERSIZE = 1024*200;
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	private final int APKTIME = 60; /**每个广播接收时间为1分钟*/
	private Gui gui = new Gui(myactivity, handler);
	String[][] apkPara = 
		{
			{GlobalVariable.sdPath+"apk/A1_unsign.apk",				"com.example.mainapp"},
			{GlobalVariable.sdPath+"apk/A2_sign.apk",				"com.example.k21testapp"},
			{GlobalVariable.sdPath+"apk/B1_unsign.apk",				"com.qualcomm.bluetoothclient"},
			{GlobalVariable.sdPath+"apk/B2_sign.apk",				"cn.com.feicui.assist"},
			{""								   ,				"com.newland.detectapp"},
			{""								  ,					"com.android.settings"},
			{GlobalVariable.sdPath+"apk/UCliulanqi_cmbc_sign.apk",	""}
		};
	
	public void installapp2() 
	{
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		byte[] writebuf = new byte[BUFFERSIZE];
		String fname = GlobalVariable.sdPath+"test.txt";
		Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
		long startTime;
		int time = 0;
		
		gui.cls_show_msg1(2,TESTITEM+"测试中...");
		
		/** A1:普通签名的与安全通信的apk
		 * A2:证书签名的与安全通信的apk
		 * B1:普通签名的普通apk
		 * B2:证书签名的普通apk
		 * 
		*/
		/* Process body*/
		// 确保测试文件存在
		gui.cls_show_msg("【A7以上平台请打开xml文件中的provider标签】【测试旧验签使用旧验签测试apk,测试新验签使用新验签测试apk】该用例需在控制台开启情况下测试，测试前请确保证书已更新与服务器一致并把测试apk放置到内置SD卡的apk/目录下(注意海外和国内版本证书和apk不一样)，各个应用均未安装，" +
				"放置完毕去apk目录查看是否有baidu_map_sign.apk，若有请将该apk文件重命名为地图.apk，并且测试apk均未安装(请将QQyinle_586_sign.apk和QQliulanqi_sign.apk放置在外置SD卡的apk目录下)");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// case2:参数异常，参数为null和apk路径包错误，抛出相应的异常
		if(gui.cls_show_msg1(2, "参数异常，参数为null和apk路径包错误，抛出相应的异常，【取消】退出测试")==ESC)
			return;
		expPackName = GlobalVariable.sdPath+"apk/unExist.apk";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//参数异常为null by20190925
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
//		intent.setDataAndType(null,"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装不存在apk测试失败(apk = %s,%s，ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case12:异常测试，卸载不存在的apk文件，返回-201   // 静默卸载不存在的应用，A5返回-201，现在7和9都是返回-101，建议案例中加一个-101兼容
		expPackName = "com.unexist.name";
		Uri uri12 = Uri.parse("package:com.unexist.name");
		Intent intent12 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri12);
		myactivity.startActivity(intent12);
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
		if ((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||((respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND&&(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_INVALID_APK)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败（apk = %s,%s,%d）", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case10:安装中文命名的apk文件应成功
		gui.cls_show_msg1(2, "安装地图，【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/地图.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待2分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME*2)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			//A7平台系统不够流畅导致地图APK静默安装过久。显示安装失败。开发提示后续优化系统。案例暂时先延时保证正常安装
//			if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
//				
//			}else {
				if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
					break;
//			}
		}
		
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装中文app失败(apk = %s,%s，%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(2, "等待地图安装，【取消】退出测试");
		time = 0;
		while(time<APKTIME*2)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if(Tools.isAppInstalled(myactivity.getApplicationContext(),"com.baidu.BaiduMap"))
				break;
		}
		gui.cls_show_msg1(2, "卸载地图，【取消】退出测试");
		//卸载地图
		Uri uri10 = Uri.parse("package:com.baidu.BaiduMap");
		Intent intent10 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri10);
		myactivity.startActivity(intent10);
		expPackName = "com.baidu.BaiduMap";
		time = 0;
		// 循环等待一分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
				break;
		}
		if ((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1：	对与安全通信的apk，选择静默安装，需apk签名检查
		// case3.1.1：	对普通签名的与安全通信的apk，进行静默安装的操作，签名验证不应通过，返回-104
		// case3.1.2：	对与证书签名一致的安全通信的apk，进行静默安装的操作，签名验证应通过,不应抛出异常
		gui.cls_show_msg1(2, "安装A1普通签名的与安全通信的apk，【取消】退出测试");
		expPackName= apkPara[0][0];
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "安装A2对与证书签名一致的安全通信的apk，【取消】退出测试");
		expPackName= apkPara[1][0];
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!= PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装app测试失败(apk = %s,%s，ret=%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "卸载A1，【取消】退出测试");
		expPackName = apkPara[0][1];
		Uri uri1 = Uri.parse("package:"+expPackName);
		LoggerUtil.i("expPackName="+expPackName);
		Intent intentDel1 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri1);
//		Intent intentDel1 = new Intent("android.intent.action.DELETE.HIDE",uri1);//坤坤的写法
//		intentDel1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		myactivity.startActivity(intentDel1);
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
		//(currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||
		if ((respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "卸载A2，【取消】退出测试");
		expPackName = apkPara[1][1];
		Uri uri2 = Uri.parse("package:"+expPackName);
		Intent intentDel2 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri2);
		myactivity.startActivity(intentDel2);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
				break;
//			else
//				Log.v("wangxiaoyu", "卸载监听中无A2包名");
		}
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3.2:对不与安全通信的apk，选择静默安装，不需签名验证
		// case3.2.1:对普通签名的非安全通信apk进行静默安装的操作，不需签名验证
		// case3.2.2:对与证书签名一致的非安全通信apk进行静默安装的操作，不需签名验证
		gui.cls_show_msg1(2, "安装B1，【取消】退出测试");
		expPackName = apkPara[2][0];
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
	//全系统验签。B1普通应用应失败 返回-104 需要验证该返回值 by20200601
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装app测试失败(apk = %s,%s,ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "安装B2，【取消】退出测试");
		expPackName = apkPara[3][0];
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装app测试失败(apk= %s,%s,ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "卸载B1，【取消】退出测试");
		expPackName = apkPara[2][1];
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
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s，ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(2, "卸载B2，【取消】退出测试");
		expPackName = apkPara[3][1];
		Uri uri4 = Uri.parse("package:"+expPackName);
		Intent intentDel4 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri4);
		myactivity.startActivity(intentDel4);
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
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s，%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case8:已经安装较高版本的应用，再安装低版本应用，应返回-107
		gui.cls_show_msg1(2, "安装A2H，【取消】退出测试");
		expPackName = GlobalVariable.sdPath + "apk/A2H_sign.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		apkReceiver.getResp(APK_INSTALL);
		expPackName = GlobalVariable.sdPath + "apk/A2_sign.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) == PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr, "line %d:静默安装app测试失败(apk = %s,%s,ret = %d)",Tools.getLineInfo(), expPackName,currentName, respCode);
			if (!GlobalVariable.isContinue) 
				return;
		}
		// 卸载该APP
		gui.cls_show_msg1(2, "卸载A2H，【取消】退出测试");
		expPackName = apkPara[1][1];
		Uri uri8 = Uri.parse("package:" + expPackName);
		Intent intent8 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri8);
		myactivity.startActivity(intent8);
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
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr, "line %d:静默卸载app测试失败(apk = %s,%s,ret = %d)",Tools.getLineInfo(), expPackName,currentName, respCode);
			if (!GlobalVariable.isContinue) 
				return;
		}
		
		// case7:安装没有MANAGE_NEWLAND，应返回安装权限不足-8
		gui.cls_show_msg1(2, "安装UdpTool.apk，【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/UdpTool_sign.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装无MANAGE_NEWLAND权限应用失败（apk = %s,%s,%d）", Tools.getLineInfo(), expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case11:安装不是apk格式的文件，应返回默认失败-101
		gui.cls_show_msg1(2, "安装不是apk格式的文件，【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/home.txt";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		time = 0;
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装系统应用失败（apk =%s, %s,%d）", Tools.getLineInfo(), expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case5:卸载自检应用，应卸载失败  //原生就是返回1 开发回复是属于正常现象
		gui.cls_show_msg1(2, "卸载自检，【取消】退出测试");
		expPackName = apkPara[4][1];
		Uri uri6 = Uri.parse("package:" + expPackName);
		Intent intent6 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri6);
		myactivity.startActivity(intent6);
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
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||
		(((respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_NO_PERMISSION)&&(respCode=apkReceiver.getResp(APK_UNINSTALL))!=1))
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载自检应用失败(apk = %s,%s,%d)", Tools.getLineInfo(), expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case6:卸载系统应用，应不能卸载成功  //原生就是返回1 开发回复是属于正常现象
		// 卸载系统应用
		gui.cls_show_msg1(2, "卸载系统应用，【取消】退出测试");
		expPackName = apkPara[5][1];
		Uri uri7 = Uri.parse("package:" + expPackName);
		Intent intent7 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri7);
		myactivity.startActivity(intent7);
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
		
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||
		(((respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_NO_PERMISSION)&&(respCode=apkReceiver.getResp(APK_UNINSTALL))!=1))
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载系统设置应用失败(apk =%s, %s,%d)", Tools.getLineInfo(), expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case13:安装外部TF卡的apk应安装成功    550不支持TF卡，跳过该测试
		String tfPath = Tools.getDesignPath(myactivity, DiskType.TFDSK);
		if(tfPath!=null)
		{
			gui.cls_show_msg1(2, "(TF卡)安装外部TF卡的apk应安装成功，【取消】退出测试");
			expPackName = GlobalVariable.TFPath+ "apk/QQyinle_586_sign.apk";
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			;
			myactivity.startActivity(intent);
			time = 0;
			// 循环等待2分钟
			startTime = System.currentTimeMillis();
			while (time < APKTIME * 2) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
					break;
			}
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装app失败(apk = %s,%s，%d)",Tools.getLineInfo(), expPackName, currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}

			gui.cls_show_msg1(2, "等待外部TF卡应用安装，【取消】退出测试");
			time = 0;
			while (time < APKTIME * 2) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (Tools.isAppInstalled(myactivity.getApplicationContext(),"com.tencent.qqmusic"))
					break;
			}
			gui.cls_show_msg1(2, "卸载外部TF卡，【取消】退出测试");
			// 卸载外部TF卡应用

			expPackName = "com.tencent.qqmusic";
			Uri uri20 = Uri.parse("package:" + expPackName);
			Intent intent20 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri20);
			myactivity.startActivity(intent20);
			time = 0;
			// 循环等待一分钟
			startTime = System.currentTimeMillis();
			while (time < APKTIME) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)",Tools.getLineInfo(), expPackName, currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		//case14:content Uri路径安装apk  550不支持TF卡 跳过该测试
		if (tfPath!=null) 
		{
			gui.cls_show_msg1(2, "(TF卡)content Uri方式安装apk应安装成功，【取消】退出测试");
			Log.d("eric_chen", "case14--");
			expPackName =GlobalVariable.TFPath+ "apk/QQliulanqi_sign.apk";
			Log.d("eric_chen", "expPackName--" + expPackName);
			String file = "file:///storage/sdcard1/apk/QQliulanqi_sign.apk";
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			if (Build.VERSION.SDK_INT >= 24) {
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}
			intent.setDataAndType(Uri.parse(file),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			startTime = System.currentTimeMillis();
			while (time < APKTIME * 2) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
					break;
			}
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装app失败(apk = %s,%s，%d)",Tools.getLineInfo(), expPackName, currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}

			gui.cls_show_msg1(2, "等待qq浏览器应用安装，【取消】退出测试");
			time = 0;
			while (time < APKTIME * 2) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (Tools.isAppInstalled(myactivity.getApplicationContext(),"com.tencent.mtt"))
					break;
			}
			gui.cls_show_msg1(2, "卸载qq浏览器应用，【取消】退出测试");
			// 卸载外部TF卡应用
			expPackName = "com.tencent.mtt";
			Uri uri21 = Uri.parse("package:" + expPackName);
			Intent intent21 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri21);
			myactivity.startActivity(intent21);
			time = 0;
			// 循环等待一分钟
			startTime = System.currentTimeMillis();
			while (time < APKTIME) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)",Tools.getLineInfo(), expPackName, currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		// case1:存储空间满，安装应用应失败
		//开发回复：由于不确定返回码。现在只对验签失败-104做验证。其他错误情况只要不返回成功即可
		gui.cls_show_msg("请先将外置TF卡片拔出---按确认键继续");
		if (gui.ShowMessageBox("是否立即进行写满SD卡测试，写满设备操作要等待5分钟以上".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			gui.cls_show_msg1(1,"正在写入......");
			while (true) {
				if (new FileSystem().JDK_FsWrite(fname, writebuf,writebuf.length, 2) != writebuf.length) 
				{
					break;
				}
			}
			expPackName = apkPara[1][0];//A2
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			time = 0;
			// 循环等待1分钟
			startTime = System.currentTimeMillis();
			while(time<APKTIME)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
					break;
			}
			//海外是新体系，国内普通固件可用旧体系测试（国内新旧体系都有）
			//N910_A7也是海外
			String path = "/proc/boot_ver";	//bootloader版本
			String bootloaderVersion = LinuxCmd.readDevNode(path);
			
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))== PACKAGE_INSTALL_SUCCESS) 
			{
				gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:%s空间不足时，安装A2.apk测试失败（apk=%s,%s，%d）",
						Tools.getLineInfo(), TESTITEM, expPackName,currentName,respCode);
				if (!GlobalVariable.isContinue) 
				{
					// 删除文件，释放空间
					new FileSystem().JDK_FsDel(fname);
					return;
				}
			}
//			if(bootloaderVersion.contains(".HY.")||GlobalVariable.currentPlatform==Model_Type.N910_A7){//海外版本
//				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!= ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED) 
//				{
//					gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:%s海外版本空间不足时，安装A2.apk测试失败（apk=%s,%s，%d）",
//							Tools.getLineInfo(), TESTITEM, expPackName,currentName,respCode);
//					if (!GlobalVariable.isContinue) 
//					{
//						// 删除文件，释放空间
//						new FileSystem().JDK_FsDel(fname);
//						return;
//					}
//				}
//			}else{
//				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!= ERROR_PACKAGE_INSTALL_FAILED_NO_SPACE) 
//				{
//					gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:%s空间不足时，安装A2.apk测试失败（apk=%s,%s，%d）",
//							Tools.getLineInfo(), TESTITEM, expPackName,currentName,respCode);
//					if (!GlobalVariable.isContinue) 
//					{
//						// 删除文件，释放空间
//						new FileSystem().JDK_FsDel(fname);
//						return;
//					}
//						
//				}
//			}
			
			// 删除文件，释放空间
			new FileSystem().JDK_FsDel(fname);
		}
		
		// 注销广播
		gui.cls_show_msg1_record(TAG, "installapp2", gScreenTime,"%s测试通过", TESTITEM);
	}
	
	public void apkDialog(String name)
	{
		gui.cls_show_msg(name);
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
	
}
