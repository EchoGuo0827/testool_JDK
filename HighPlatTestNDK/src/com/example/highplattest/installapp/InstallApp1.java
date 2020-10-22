package com.example.highplattest.installapp;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
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
/************************************************************************
 * 
 * module 			: Google方式安装apk
 * file name 		: InstallApp1.java 
 * history 		 	: 变更点											变更时间				变更人员
 * 					 F7/F10暂时不加显式安装广播接收的功能，客户暂时无需求		   	20200429	 		郑薛晴	
 * 					 全平台系统修改安装普通第三方应用均需要验签。案例返回值和测试APK修改      20200526                                    陈丁
 * 						(A5部分机型除外，无法确认具体A5哪些版本，故按照默认均有验签)     
 * 					测试点不能因开启全平台验签而改变，应用安装返回值修改回原测试返回值            20200529                                    陈丁  
 * 					开发回复:现在只对应用做验签的返回值判断。其他错误返回只要不返回0即可
 * 					测试apk后缀区分签名应用与未签名应用。方便测试人员辨认						20201009		陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp1 extends UnitFragment
{
	private final String TESTITEM = "Google安装与卸载方式，显性";
	public final String TAG = InstallApp1.class.getSimpleName();
	// 一次写入200K
	private final int BUFFERSIZE = 1024*200;
	private ApkBroadCastReceiver apkReceiver;
	private Gui gui = new Gui(myactivity, handler);
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	private int respCode;		/**返回码*/
	private final int APKTIME = 60; /**每个广播接收时间为1分钟*/
	
	@SuppressLint({ "SdCardPath", "NewApi" })
	public void installapp1() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp1",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		byte[] writebuf = new byte[BUFFERSIZE];
		String fname = GlobalVariable.sdPath+"test.txt";
		long startTime;
		int time = 0;
		
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
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
//		//前置，不需要去判断是否挂在了tf卡，产品之间存在差异，不是 所有产品都支持tf卡
//		if(!((tfcardDetect())>0f))//是否挂载tf
//		{
//			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr, "line %d:%s外部SD卡状态异常", Tools.getLineInfo(), TESTITEM);
//			return;
//		}
		
		/* Process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		
		gui.cls_show_msg("【A7以上平台请打开xml文件中的provider标签】【测试旧验签使用旧验签测试apk,测试新验签使用新验签测试apk】该用例需在控制台开启情况下测试，测试前请确保证书已更新与服务器一致并把测试apk放置到内置SD卡的apk/和外置TF卡的apk目录下，各个应用均未安装，" +
				"放置完毕去apk目录查看是否有baidu_map_sign.apk，若有请将该apk文件重命名为地图.apk，并且测试apk均未安装(请将QQyinle_586_sign.apk和QQliulanqi_sign.apk放置在外置SD卡的apk目录下)");
		
		//case2:apk文件不存在的情况下预期应该安装失败，返回-101
		if(gui.cls_show_msg1(2, "apk文件不存在的情况下预期应该安装失败，返回-101，【取消】退出测试")==ESC)
			return;
		Log.d("eric_chen", "Start1---------------");
		expPackName = GlobalVariable.sdPath+"apk/unExist.apk";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		Log.d("eric_chen", "End1---------------");
//		gui.cls_show_msg(0, "应用不存在测试");
		// 循环等待1分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
				break;
		}
		if(currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) == PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s测试失败(pack = %s,ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case12:卸载不存在的apk文件，应返回-201
		if(gui.cls_show_msg1(2, "卸载不存在的apk文件，应返回-201，【取消】退出测试")==ESC)
			return;
		Log.d("eric_chen", "Start2---------------");
		Uri uri12 = Uri.parse("package:com.unexist.apk");
		Intent intent12 = new Intent(Intent.ACTION_DELETE, uri12);
		myactivity.startActivity(intent12);
		Log.d("eric_chen", "End2---------------");
		expPackName = "com.unexist.apk";
		gui.cls_show_msg("提示卸载应用不存在按任意键继续");
		if((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:显示卸载不存在的app失败(pack = %s,ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case10:安装中文命名的apk文件应成功  
		//地图apk属于第三方应用，需要验签。广播第一次返回-104
		if(gui.cls_show_msg( "安装中文命名的地图.apk文件应成功，【取消】退出测试")==ESC)
			return;
		expPackName = GlobalVariable.sdPath+"apk/地图.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("地图应用安装完毕时点击任意键继续，请耐心等待安装界面");
		if((currentName=apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:安装中文app失败(pack = %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.d("eric_chen", "Start---------------");
		Uri uri10 = Uri.parse("package:com.baidu.BaiduMap");
		Intent intent10 = new Intent(Intent.ACTION_DELETE, uri10);
		myactivity.startActivity(intent10);
		expPackName = "com.baidu.BaiduMap";
		Log.d("eric_chen", "End---------------");
		gui.cls_show_msg("地图应用卸载完毕时点击任意键继续");
		if((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:卸载中文app失败(pack = %s)", Tools.getLineInfo(),TESTITEM,currentName);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1:普通签名的与安全通信的apk应该安装失败，返回-104，进行卸载应抛出卸载包不存在异常，返回-201
		if(gui.cls_show_msg( "普通签名的与安全通信的apk应该安装失败，返回-104，进行卸载应抛出卸载包不存在异常，返回-201，【取消】退出测试")==ESC)
			return;
		intent.setDataAndType(Uri.fromFile(new File(apkPara[0][0])),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		expPackName = apkPara[0][0];
		gui.cls_show_msg("A1应用安装完毕点击任意键");
		if((currentName=apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) == PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s安装测试失败(apk = %s，ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		expPackName = apkPara[0][1];
		Uri uri1 = Uri.parse("package:"+expPackName);
		Intent intent1 = new Intent(Intent.ACTION_DELETE, uri1);
		myactivity.startActivity(intent1);
		
		gui.cls_show_msg("A1应用卸载完毕点击任意键");
		// 这里不必判断A1的返回值，因为开控制台情况A1实际是能安装成功的
/*		if((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		
		// case3.2:支付签名的与安全通信的apk应该安装成功，进行卸载应成功
		if(gui.cls_show_msg( "支付签名的与安全通信的apk应该安装成功，进行卸载应成功，【取消】退出测试")==ESC)
			return;
		expPackName = apkPara[1][0];
	    intent.setDataAndType(Uri.fromFile(new File(apkPara[1][0])),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("A2应用安装完毕点击任意键");
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s安装A2测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		expPackName = apkPara[1][1];
		Uri uri2 = Uri.parse("package:"+apkPara[1][1]);
		Intent intent2 = new Intent(Intent.ACTION_DELETE, uri2);
		myactivity.startActivity(intent2);
		gui.cls_show_msg("A2应用卸载完毕点击任意键");
		if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载A2测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		
		//case4.1:普通签名的普通应用的apk应该安装成功
		//全系统验签。普通应用应失败 返回-104 需要验证该返回值 by20200601
		if(gui.cls_show_msg("普通签名的普通应用的apk应该返回-104，【取消】退出测试")==ESC)
			return;
		expPackName = apkPara[2][0];
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		startTime = System.currentTimeMillis();
		gui.cls_show_msg("B1应用安装完毕点任意键");
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode= apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s安装B1测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		expPackName = apkPara[2][1];
		Uri uri3 = Uri.parse("package:"+expPackName);
		Intent intent3 = new Intent(Intent.ACTION_DELETE, uri3);
		myactivity.startActivity(intent3);
		gui.cls_show_msg("B1应用卸载完毕点任意键");
		if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!= PACKAGE_DELETE_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载B1测试失败（apk = %s，%d）", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.2:支付签名的普通应用的apk应该安装成功
		if(gui.cls_show_msg("支付签名的普通应用的apk应该安装成功，【取消】退出测试")==ESC)
			return;
		expPackName = apkPara[3][0];
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("B2应用安装完毕点任意键");
		
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!= PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s安装测试失败（apk = %s，%d）", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		expPackName = apkPara[3][1];
		Uri uri4 = Uri.parse("package:"+expPackName);
		Intent intent4 = new Intent(Intent.ACTION_DELETE, uri4);
		myactivity.startActivity(intent4);
		gui.cls_show_msg("B2卸载完成点任意键");
		if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载测试失败（apk = %s，%d）", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case8:已经安装较高版本的应用，再安装低版本应用，应返回-107
		if(gui.cls_show_msg("已经安装较高版本的应用，再安装低版本应用，应返回-107，【取消】退出测试")==ESC)
			return;
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		expPackName = GlobalVariable.sdPath + "apk/A2H_sign.apk";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("安装较高版本A2H完成点任意键");
		respCode=apkReceiver.getResp(APK_INSTALL);
		
		expPackName = GlobalVariable.sdPath + "apk/A2_sign.apk";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("安装较低版本A2后点任意键");
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) == PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr, "line %d:%s安装测试失败（apk = %s,%d）",Tools.getLineInfo(), TESTITEM, currentName, respCode);
			if (!GlobalVariable.isContinue) 
				return;
		}
		
		expPackName = apkPara[1][1];
		Uri uri7 = Uri.parse("package:" + expPackName);
		Intent intent7 = new Intent(Intent.ACTION_DELETE, uri7);
		myactivity.startActivity(intent7);
		gui.cls_show_msg("A2卸载完成后点任意键");
		
		
		// case7:安装没有MANAGE_NEWLAND，应返回-8
		if(gui.cls_show_msg("安装没有MANAGE_NEWLAND，应返回-8，【取消】退出测试")==ESC)
			return;
		expPackName = GlobalVariable.sdPath+"apk/UdpTool_sign.apk";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(GlobalVariable.sdPath+"apk/UdpTool_sign.apk")),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("安装UdpTool后点任意键继续");
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))==PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:静默安装无MANAGE_NEWLAND权限应用失败(apk = %s,%d)", Tools.getLineInfo(), currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case11:安装不是apk格式的文件，应返回默认失败-101
		if(gui.cls_show_msg("安装不是apk格式的文件，应返回默认失败-101，【取消】退出测试")==ESC)
			return;
		expPackName = GlobalVariable.sdPath+"apk/home.txt";
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
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
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:静默安装系统应用失败(apk = %s,ret = %d)", Tools.getLineInfo(), currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		String tfPath = Tools.getDesignPath(myactivity, DiskType.TFDSK);
		LoggerUtil.d("tfPath:"+tfPath);
		//case13:安装外部TF卡的apk应安装成功，N550不支持外部TF卡，跳过测试 20200605
		if(tfPath!=null)
		{
			//外部TF卡 QQyinle_586属于第三方应用，需要验签，故返回-104
			if(gui.cls_show_msg("安装外部TF卡的apk应安装成功，【取消】退出测试")==ESC)
				return;
			Log.d("eric_chen", "case13--");
		
			expPackName = GlobalVariable.TFPath+"apk/QQyinle_586_sign.apk";
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			Log.e("uri",""+Uri.fromFile(new File(expPackName)));
			myactivity.startActivity(intent);
			startTime = System.currentTimeMillis();
			gui.cls_show_msg("应用安装完毕点击任意键");
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS) 
			{
				gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:安装外置TF卡应用失败(apk = %s,ret = %d)", Tools.getLineInfo(), currentName,respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			
			expPackName = "com.tencent.qqmusic";
			Uri uri13 = Uri.parse("package:"+expPackName);
			Log.e("uri",""+uri13);
			Intent intent13 = new Intent(Intent.ACTION_DELETE, uri13);
			myactivity.startActivity(intent13);
			gui.cls_show_msg("应用卸载完毕点任意键");
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!= PACKAGE_DELETE_SUCCESS)
			{
				gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载测试失败（apk = %s，%d）", Tools.getLineInfo(),TESTITEM,currentName,respCode);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		//case14:content Uri路径安装apk
		if(GlobalVariable.currentPlatform!=Model_Type.N550)
		{
			if(gui.cls_show_msg( "content Uri方式安装apk应安装成功，【取消】退出测试")==ESC)
				return;
			Log.d("eric_chen", "case14--");
			expPackName = GlobalVariable.TFPath+"apk/QQliulanqi_sign.apk";
			Log.d("eric_chen", "expPackName--"+expPackName);
			String file = "file:///storage/sdcard1/apk/QQliulanqi_sign.apk";
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			 if (Build.VERSION.SDK_INT >= 24) {
		           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		       }
			intent.setDataAndType(Uri.parse(file),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			startTime = System.currentTimeMillis();
			gui.cls_show_msg("应用安装完毕点击任意键");
			Log.d("eric_chen", "expPackName: "+expPackName);
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS) 
			{
				Log.d("eric_chen", "currentName :"+currentName);
				Log.d("eric_chen", "expPackName :"+expPackName);
				Log.d("eric_chen", "respCode :"+respCode);
				gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:安装外置TF卡应用失败(apk = %s,ret = %d)", Tools.getLineInfo(), currentName,respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			expPackName = "com.tencent.mtt";
			Uri uri14 = Uri.parse("package:"+expPackName);
			Intent intent14 = new Intent(Intent.ACTION_DELETE, uri14);
			myactivity.startActivity(intent14);
			gui.cls_show_msg("应用卸载完毕点任意键");
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!= PACKAGE_DELETE_SUCCESS)
			{
				gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载测试失败（apk = %s，%d）", Tools.getLineInfo(),TESTITEM,currentName,respCode);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case5：卸载比自身权限高的应用，应返回-1
		if(gui.cls_show_msg("卸载比自身权限高的应用，应返回-1，【取消】退出测试")==ESC)
			return;
		// 卸载自检
		expPackName = apkPara[4][1];
		Uri uri5 = Uri.parse("package:"+expPackName);
		Intent intent5 = new Intent(Intent.ACTION_DELETE, uri5);
		myactivity.startActivity(intent5);
		gui.cls_show_msg("提示自检应用卸载失败后点任意键继续");
		if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_NO_PERMISSION)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载自检失败(apk = %s，ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case6:卸载系统应用，应返回-1
		if(gui.cls_show_msg("卸载系统应用，应返回-1，【取消】退出测试")==ESC)
			return;
		expPackName = apkPara[5][1];
		// 卸载系统应用
		Uri uri6 = Uri.parse("package:"+expPackName);
		Intent intent6 = new Intent(Intent.ACTION_DELETE, uri6);
		myactivity.startActivity(intent6);
		gui.cls_show_msg("卸载系统应用失败后点任意键继续");
		if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_NO_PERMISSION)
		{
			gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr,"line %d:%s卸载系统应用失败(apk = %s， %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1:存储空间满，安装应用应失败，返回-4
		//由于不同平台返回值不一。开发回复：存储空间满，返回值不为0就算过
		gui.cls_show_msg("请先将外置TF卡片拔出---按确认键继续");
		if(gui.cls_show_msg( "存储空间满，安装应用应失败，【取消】退出测试")==ESC)
			return;
		if (gui.ShowMessageBox("是否立即进行写满SD卡测试，写满设备操作要等待5分钟以上".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			gui.cls_show_msg1(1,"正在写入。。。。。。。。");
			while (true) 
			{
				if (new FileSystem().JDK_FsWrite(fname, writebuf,writebuf.length, 2) != writebuf.length) 
				{
					break;
				}
			}
			expPackName = apkPara[6][0];
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			
			gui.cls_show_msg("已写满设备，安装UC浏览器，安装失败后任意键继续");
			//海外是新体系，国内普通固件可用旧体系测试（国内新旧体系都有）
			String path = "/proc/boot_ver";	//bootloader版本
			String bootloaderVersion = LinuxCmd.readDevNode(path);
			// 海外版本返回-104，国内版本返回-4
			//N910_A7是海外版本 
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) == PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp1", gKeepTimeErr, "line %d:%s写满空间安装apk测试失败(apk=%s，ret = %d)", Tools.getLineInfo(),TESTITEM, currentName, respCode);
			}
			// 删除文件，释放空间
			new FileSystem().JDK_FsDel(fname);
		}
		gui.cls_show_msg1_record(TAG, "installapp1", gScreenTime, "%s测试通过", TESTITEM);
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
//	// 获取TF是否挂载及大小
//	public float tfcardDetect() {
//		String tfcardPath = "/storage/sdcard1/";
//		File tfcardDir = new File(tfcardPath);
//		// TF卡已经挂载
//		if (tfcardDir.getTotalSpace() > 0) {
//			double f = (double) tfcardDir.getTotalSpace() / (1024 * 1024 * 1024);
//			BigDecimal b = new BigDecimal(f);
//			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
//			float f1 = b.floatValue();
//			return f1;
//		} else {
//			return 0f;
//		}
//
//	}
	
	
}
