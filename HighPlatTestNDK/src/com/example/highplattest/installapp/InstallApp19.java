package com.example.highplattest.installapp;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
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
import android.support.v4.content.FileProvider;
import android.util.Log;
/************************************************************************
 * 
 * module 			: Android设定静默安装app
 * file name 		: InstallApp19.java 
 * history 		 	: 变更记录									变更时间			变更人员
 * 					 F7/F10平台以上安装apk改为FileProvider		20200429		郑薛晴
 * 					测试apk后缀修改							20201010		陈丁
 * 														   	 			
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp19 extends UnitFragment 
{
	private final String TESTITEM = "Google安装与卸载方式，隐性(FileProvider方式)";
	public final String TAG = InstallApp19.class.getSimpleName();
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

	public void installapp19() 
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
		String funcName = "installapp19";
		
		File file = null;
		Uri contentUri = null;
		
		gui.cls_show_msg1(2,TESTITEM+"测试中...");
		
		/** A1:普通签名的与安全通信的apk
		 * A2:证书签名的与安全通信的apk
		 * B1:普通签名的普通apk
		 * B2:证书签名的普通apk
		 * 
		*/
		/* Process body*/
		// 确保测试文件存在
		gui.cls_show_msg("【测试A7及以上平台需要把manifest文件的provider标签打开】【测试旧验签使用旧验签测试apk,测试新验签使用新验签测试apk】该用例需在控制台开启情况下测试，测试前请确保证书已更新与服务器一致并把测试apk放置到内置SD卡的apk/目录下，各个应用均未安装，" +
				"放置完毕去apk目录查看是否有乱码文件，若有乱码请将该apk文件重命名为地图，并且测试apk均未安装(请注意，海外版本与国内版测试所需的证书和apk不同)");
		
		
//		// case13:content uri方式安装
//		if(gui.cls_show_msg( "file Uri方式安装apk应安装成功，【取消】退出测试")==ESC)
//			return;
//		expPackName =GlobalVariable.sdPath+"apk/QQyinyue.apk";
//		String fileContent = "file:///mnt/shell/emulated/0/apk/QQyinyue.apk";
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		
//	    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//	    
//	    Uri fileUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", new File(fileContent));
//		intent.setDataAndType(fileUri,"application/vnd.android.package-archive");
//		myactivity.startActivity(intent);
//		
//		startTime = System.currentTimeMillis();
//		Log.d("eric_chen", "expPackName: "+expPackName);
//		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS) 
//		{
//			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:安装内置SD卡应用失败(apk = %s,ret = %d)", Tools.getLineInfo(), currentName,respCode);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		expPackName = "com.tencent.mtt";
//		Uri uri14 = Uri.parse("package:"+expPackName);
//		Intent intent14 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri14);
//		myactivity.startActivity(intent14);
//		if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!= PACKAGE_DELETE_SUCCESS)
//		{
//			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载测试失败（apk = %s，%d）", Tools.getLineInfo(),TESTITEM,currentName,respCode);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		
		// case2:参数异常，参数为null和apk路径包错误，抛出相应的异常
		if(gui.cls_show_msg1(2, "参数异常，参数为null和apk路径包错误，抛出相应的异常，【取消】退出测试")==ESC)
			return;
		expPackName = GlobalVariable.sdPath+"apk/unExist.apk";
		//参数异常为null by20190925
		
		/**Android7.0行为变更，通过FileProvider在应用间共享文件*/
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
	    LoggerUtil.v("case2 uri="+contentUri.getPath());
	   
	    intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		
		/**安装无法比较路径，因为FileProvider方式安装的路径是系统决定*/
		respCode=apkReceiver.getResp(APK_INSTALL);
		if(currentName.equals(expPackName)==false||(respCode!=ERROR_PACKAGE_INSTALL_FAILED_INVALID_APK&&respCode!=-2))
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:静默安装不存在apk测试失败(apk_path = %s，ret = %d)", Tools.getLineInfo(),currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case12:异常测试，卸载不存在的apk文件，返回-201
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
		respCode=apkReceiver.getResp(APK_UNINSTALL);
		if ((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode!=ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND&&respCode!=-101)) 
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:静默卸载不存在的app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case10:安装中文命名的apk文件应成功
		gui.cls_show_msg1(2, "安装地图，【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/地图.apk";
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
	    
	    LoggerUtil.d("case10 uri="+contentUri.toString());
		intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
//				if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
//					break;
//			}
		}
		
		/**安装无法比较路径，因为FileProvider方式安装的路径是系统决定*/
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装【百度.apk】失败(apk_path = %s,%d)", Tools.getLineInfo(),currentName,respCode);
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
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载【百度app】测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1：	对与安全通信的apk，选择静默安装，需apk签名检查
		// case3.1.1：	对普通签名的与安全通信的apk，进行静默安装的操作，签名验证不应通过，返回-104
		// case3.1.2：	对与证书签名一致的安全通信的apk，进行静默安装的操作，签名验证应通过,不应抛出异常
		gui.cls_show_msg1(2, "安装A1普通签名的与安全通信的apk，【取消】退出测试");
		expPackName= apkPara[0][0];
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
	    
	    intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		/**安装无法比较路径，因为FileProvider方式安装的路径是系统决定*/
		if(currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:静默安装【A1.apk】失败(apk_path = %s，%d)", Tools.getLineInfo(),currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "安装A2对与证书签名一致的安全通信的apk，【取消】退出测试");
		expPackName= apkPara[1][0];
		
		file = new File(expPackName);
		contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
		
		expPackName = contentUri.getPath();
		intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		/**安装无法比较路径，因为FileProvider方式安装的路径是系统决定*/
		if(currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!= PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装【A2.apk】失败(apk_path = %s，ret=%d)", Tools.getLineInfo(),currentName,respCode);
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
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载【A1的app】失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
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
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载【A2的app】失败(apk = %s,%s,ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3.2:对不与安全通信的apk，选择静默安装，不需签名验证
		// case3.2.1:对普通签名的非安全通信apk进行静默安装的操作，不需签名验证
		// case3.2.2:对与证书签名一致的非安全通信apk进行静默安装的操作，不需签名验证
		gui.cls_show_msg1(2, "安装B1，【取消】退出测试");
		expPackName = apkPara[2][0];
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
	    
		intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		if (currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装【B1.apk】失败(apk_path = %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "安装B2，【取消】退出测试");
		expPackName = apkPara[3][0];
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
	    
	    intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		if (currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装【B2.apk】失败(apk_path= %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
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
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载【B1的APP】失败(apk = %s,%s，ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
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
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载【B2的app】测试失败(apk = %s,%s，%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case8:已经安装较高版本的应用，再安装低版本应用，应返回-107
		gui.cls_show_msg1(2, "安装A2H，【取消】退出测试");
		expPackName = GlobalVariable.sdPath + "apk/A2H_sign.apk";
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
	    
	    intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		respCode=apkReceiver.getResp(APK_INSTALL);
		expPackName = GlobalVariable.sdPath + "apk/A2_sign.apk";
		
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
		intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		
		respCode=apkReceiver.getResp(APK_INSTALL);
		if (currentName.equals(expPackName)==false||(respCode!= ERROR_PACKAGE_INSTALL_VERSION_DOWNGRADE&&respCode!=4)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr, "line %d:静默安装【A2的app】失败(apk = %s,ret = %d)",Tools.getLineInfo(),currentName, respCode);
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
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr, "line %d:静默卸载【A2H的APP】失败(apk = %s,%s,ret = %d)",Tools.getLineInfo(), expPackName,currentName, respCode);
			if (!GlobalVariable.isContinue) 
				return;
		}
		
		// case7:安装没有MANAGE_NEWLAND，应返回安装权限不足-8
		gui.cls_show_msg1(2, "安装UdpTool_sign.apk，【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/UdpTool_sign.apk";
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
		intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		
		respCode=apkReceiver.getResp(APK_INSTALL);
		if (currentName.equals(expPackName)==false||(respCode!=ERROR_PACKAGE_INSTALL_NO_MANAGE_NEWLAND&&respCode!=5)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装无MANAGE_NEWLAND权限的UdpTool.apk应用失败(apk_path = %s,%d)", Tools.getLineInfo(),currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case11:安装不是apk格式的文件，应返回默认失败-101
		gui.cls_show_msg1(2, "安装不是apk格式的文件，【取消】退出测试");
		expPackName = GlobalVariable.sdPath+"apk/home.txt";
		
		file = new File(expPackName);
	    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
	    expPackName = contentUri.getPath();
		intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
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
		
		respCode=apkReceiver.getResp(APK_INSTALL);
		if (currentName.equals(expPackName)==false||(respCode!=ERROR_PACKAGE_INSTALL_FAILED_INVALID_APK&&respCode!=-2)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默安装home.txt文件失败(apk_path = %s,%d)", Tools.getLineInfo(),currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case5:卸载自检应用，应卸载失败
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
		
		respCode=apkReceiver.getResp(APK_UNINSTALL);
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode!=ERROR_PACKAGE_DELETE_FAILED_NO_PERMISSION&&respCode!=1)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载自检应用失败(apk = %s,%s,%d)", Tools.getLineInfo(), expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case6:卸载系统应用，应不能卸载成功
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
		
		respCode=apkReceiver.getResp(APK_UNINSTALL);
		if (currentName.equals(expPackName)==false||(respCode!=ERROR_PACKAGE_DELETE_FAILED_NO_PERMISSION&&respCode!=1)) 
		{
			gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:静默卸载系统设置应用失败(apk =%s, %s,%d)", Tools.getLineInfo(), expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case1:存储空间满，安装应用应失败
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
			
			
			file = new File(expPackName);
		    contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
		    expPackName = contentUri.getPath();
			intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			
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
			String path = "/proc/boot_ver";	//bootloader版本
			String bootloaderVersion = LinuxCmd.readDevNode(path);
			if(bootloaderVersion.contains(".HY.")){//海外版本
				if (currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==0) 
				{
					gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:%s海外版本空间不足时，安装A2.apk测试失败(apk_path=%s，%d)",Tools.getLineInfo(), TESTITEM,currentName,respCode);
					if (!GlobalVariable.isContinue) 
					{
						// 删除文件，释放空间
						new FileSystem().JDK_FsDel(fname);
						return;
					}
				}
			}else{
				if (currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==0) 
				{
					gui.cls_show_msg1_record(TAG, "installapp2", gKeepTimeErr,"line %d:%s空间不足时，安装A2.apk测试失败(apk_path=%s，%d)",Tools.getLineInfo(), TESTITEM,currentName,respCode);
					if (!GlobalVariable.isContinue) 
					{
						// 删除文件，释放空间
						new FileSystem().JDK_FsDel(fname);
						return;
					}
				}
			}
			
			// 删除文件，释放空间
			new FileSystem().JDK_FsDel(fname);
		}
		
		// 注销广播
		gui.cls_show_msg1_record(TAG, funcName, gScreenTime,"%s测试通过", TESTITEM);
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
