package com.example.highplattest.installapp;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;
/************************************************************************
 * 
 * module 			: 验签方案测试
 * file name 		: InstallApp13.java 
 * Author 			:  zhushaoh
 * version 			: 
 * DATE 			: 20181113
 * directory 		: 
 * description 		: 测试不同条件下安装apk
 * related document : 
 * history 		 	: author			date			remarks
 * 					 	zsh	   			20181113 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")

public class InstallApp11 extends UnitFragment
{
	private final String CLASS_NAME =InstallApp11.class.getSimpleName();
	private final String TESTITEM = "ruiyinxin验签库应用安装测试";
	private ApkBroadCastReceiver apkReceiver;
	private  Gui gui = new Gui(myactivity, handler);
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	private int respCode;		/**返回码*/
	private Object lock=new Object();
	private String[][] apkPara = 
		{
			{GlobalVariable.sdPath+"apk/smartpos_app_v0.0.68_2018-08-15_sign.apk",											"com.ryx.ruipay"},//(V1)签名正确,有权限 ruiyinxin
			{GlobalVariable.sdPath+"apk/CpayAutoActive_V2.3.7-RC2_B145_20180806-CpayRYX_2018-08-15_sign.apk",				"com.centerm.cpay.autoactive"},//(V2)签名正确,有权限ruiyinxin 
			{GlobalVariable.sdPath+"apk/(V1)app-signed.apk",																"com.newland.security"},//错误签名apk1
			{GlobalVariable.sdPath+"apk/CiticSdkDemo-debug_sign_allpermission_2018071301.apk",								"cn.citic.sdkdemo"},//错误签名apk2
			{"smartpos_app_v0.0.68_2018-08-15_sign",														""},//V1名称
			{"CpayAutoActive_V2.3.7-RC2_B145_20180806-CpayRYX_2018-08-15_sign",							""},//V2名称
			{"apk/(V1)app-signed",																		""},//错误签名apk名称
			{GlobalVariable.sdPath+"无权限的apk地址",				""},//无权限的apk开发暂时无法提供,此项保留
		};
	
	@SuppressLint({ "SdCardPath", "NewApi" })
	public void installapp11()
	{
		String funcName= "installapp11";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
//		byte[] writebuf = new byte[BUFFERSIZE];
//		String fname = GlobalVariable.sdPath+"test.txt";
		/* Process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		gui.cls_show_msg("该用例需在设备已安装验签库的情况下测试，测试前请确保验签库已安装并把测试apk放置到内置SD卡的apk目录下，且各个应用均未安装" );
		while(true)
		{
			int returnValue=gui.cls_show_msg("1.apk安装测试\n2.多应用同时安装测试");
			switch (returnValue) 
			{
			case '1':
				installtapp();//apk安装测试
				break;
			case '2':
				intallmultiapp();//多应用同时安装测试
				break;
			case ESC://取消键
				unitEnd();
				return;
			}
		}
	}
	
//	Thread threadA=new Thread(){
//		public void run() {
//			try {
//				synchronized (lock) {
//					if(gui.cls_show_msg1(4, "1.apk文件不存在的情况下预期应该安装失败，返回-101，任意键继续,【取消】退出测试")==ESC)
//						return;
//					expPackName = GlobalVariable.sdPath+"apk/unExist.apk";
//					Intent intent = new Intent(Intent.ACTION_VIEW);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
//					myactivity.startActivity(intent);
//					lock.wait();
//					if(currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_INVALID_APK)
//					{
//						gui.cls_show_msg1_record(CLASS_NAME,"run",gKeepTimeErr,"line %d:%s测试失败(pack = %s,ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
//						if(!GlobalVariable.isContinue)
//							return;
//					}
//					else
//					{
//						gui.cls_show_msg1(5,"空文件安装测试成功,即将进入下个子用例");
//					}
//				}
//			} catch (Exception e) {
//			}
//		};
//	};
//	Thread threadB=new Thread(){
//		@Override
//		public void run() {
//			synchronized (lock) {
//				if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
//				{
//					gui.cls_show_msg("调试语句2,进入判断,");
//				lock.notify();
//				}
//			}
//		}	
//	};

	//case1 apk安装测试
	public  void installtapp()
	{	
//		threadA.start();
//		threadB.start();
		
		//1.apk文件不存在的情况下预期应该安装失败，返回-101
		if(gui.cls_show_msg1(4, "1.apk文件不存在的情况下预期应该安装失败，返回-101，任意键继续,【取消】退出测试")==ESC)
			return;
		expPackName = GlobalVariable.sdPath+"apk/unExist.apk";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		try {
			gui.cls_show_msg1(5,"空文件安装测试中,等待5s");
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
			{
				if(currentName.equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_INVALID_APK)
				{
					gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:%s测试失败(pack = %s,ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
					if(!GlobalVariable.isContinue)
						return;
				}else{
					gui.cls_show_msg("空文件安装测试成功,任意键将进入下个子用例");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//2. 安装正确签名的V1,V2应用应安装成功,且正确解析权限文件,产生对应后缀的文件
		if(gui.cls_show_msg1(5, "2.安装正确签名的V1 v2应用应安装成功，且正确解析权限文件,产生对应后缀的文件,安装结束后验签产生的临时文件夹自动删除,【取消】退出测试")==ESC)
			return;
		String expPackName1,expPackName2,expPackpackage1,expPackpackage2,endname1,endname2;
		expPackName1=apkPara[0][0];//v1的地址
		expPackName2=apkPara[1][0];//v2的地址
		expPackpackage1=apkPara[0][1];//v1的包名地址
		expPackpackage2=apkPara[1][1];//v2的报名地址
		endname1=".apply";//后缀类型1
		endname2=".state";//后缀类型2
		intent.setDataAndType(Uri.fromFile(new File(expPackName1)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("请耐心等待安装界面弹出...V1安装完毕时点击任意键继续");
		if((currentName=apkReceiver.getPackName(APK_INSTALL)).equals(expPackName1)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:安装V1失败(pack = %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		File file5=new File("/data/share/"+apkPara[4][0]);//V1apk名称命名的文件
		File file6=new File("/data/share/"+apkPara[5][0]);//V2名称命名的文件夹,用以生成尾缀_01的文件夹
		File file601=new File("/data/share/"+apkPara[5][0]+"_01");//V2名称+"_01"命名的文件夹,用以验证尾缀_01的文件夹
		file6.mkdir();
		intent.setDataAndType(Uri.fromFile(new File(expPackName2)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("请耐心等待安装界面弹出....V2安装完毕时点击任意键继续");
		if((currentName=apkReceiver.getPackName(APK_INSTALL)).equals(expPackName2)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:安装V2失败(pack = %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		File file1=new File("/data/share/"+expPackpackage1+endname1);
		File file2=new File("/data/share/"+expPackpackage1+endname2);
		File file3=new File("/data/share/"+expPackpackage2+endname1);
		File file4=new File("/data/share/"+expPackpackage2+endname2);

		//此处验证以V1名称命名的临时文件夹是否消失
		if (!file5.exists()) {
			gui.cls_show_msg("V1(签名正确的apk)验签生成的临时文件夹已删除");
		}else{
			gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:V1(签名正确的apk)验签生成的临时文件未删除,请进入data/share查看",Tools.getLineInfo());
			return;
		}
		//此处验证以V2名称+"_01"命名的临时文件夹是否消失
		if (!file601.exists()) {
			gui.cls_show_msg("V2(签名正确的apk)验签生成的带后缀'_01'临时文件夹已删除");
		} else {
			gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:V2(签名正确的apk)验签生成的带后缀'_01'临时文件件未删除,请进入data/share查看",Tools.getLineInfo());
			return;
		}
		//验证后缀文件是否存在
		if(file1.exists()&&file2.exists())
		{
			gui.cls_show_msg("V1应用正确生成了后缀文件,任意键继续");	
		}else
		{
			gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:V1应用未正确生成后缀文件,测试失败请,进入data/share查看",Tools.getLineInfo());
			return;
		}
		if(file3.exists()&&file4.exists())
		{
			gui.cls_show_msg("V2应用正确生成了后缀文件,任意键继续");	
		}else
		{
			gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d:V2应用未正确生成后缀文件,测试失败,请进入data/share查看",Tools.getLineInfo());
			return;
		}
		try {
			gui.cls_show_msg1(5,"请等待5s...将验证后缀的文件是否仍然存在");
			if(file1.exists()&&file2.exists()&&file3.exists()&&file4.exists()){
				gui.cls_show_msg("测试通过,尾缀文件仍存在..将自动删除尾缀文件");
			}else
			{
				gui.cls_show_msg1_record(CLASS_NAME,"installtapp",gKeepTimeErr,"line %d测试不通过,后缀文件消失,请进入data/share查看",Tools.getLineInfo());
				return;
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		file1.delete();
		file2.delete();
		file3.delete();
		file4.delete();
		Uri uri1 = Uri.parse("package:"+expPackpackage1);
		Uri uri2 = Uri.parse("package:"+expPackpackage2);
		Intent intent1 = new Intent(Intent.ACTION_DELETE, uri1);
		gui.cls_show_msg1(2,"即将卸载V1应用...");
		myactivity.startActivity(intent1);
		Intent intent2 = new Intent(Intent.ACTION_DELETE, uri2);
		gui.cls_show_msg1(2,"即将卸载V2应用...");
		myactivity.startActivity(intent2);
		gui.cls_show_msg1(5,"V1 V2应用卸载完毕,本项测试通过,即将进入下个子用例");
		
		//3.安装错误签名的apk时应安装失败
		if(gui.cls_show_msg1(4, "3.安装错误签名的sapk时应安装失败,返回-104，且验签结束时产生的临时文件夹自动删除,【取消】退出测试")==ESC)
			return;
		expPackName=apkPara[2][0];//错误签名的apk地址
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("请等待安装界面弹出....安装完成后任意键继续");
		File file7=new File("data/share/"+apkPara[6][0]);//错误签名的apk文件夹地址
		if (!file7.exists()) {
			gui.cls_show_msg("签名错误的apk,验签生成的临时文件已删除,任意键继续");
		} else {
			gui.cls_show_msg1_record(CLASS_NAME, "installtapp",gKeepTimeErr,"line %d:签名错误的apk验签生成的临时文件未删除,请进入data/share查看",Tools.getLineInfo());
			return;
		}
		if((respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "installtapp",gKeepTimeErr,"line %d:%s测试失败(apk = %s，ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}else
		{
			gui.cls_show_msg1(5,"返回值校验成功,若应用安装失败则测试通过,反之则测试失败");
		}
	}	

	//case2 验证多应用同时安装
	public void intallmultiapp()
	{
		gui.cls_show_msg("开始验证多应用同时安装.....请确保各应用均未安装,任意键继续" );
		smartpos.start();
		CpayAutoActive.start();
		app_sign.start();
		CiticSdkDemo.start();
		gui.cls_show_msg1_record(CLASS_NAME,"intallmultiapp",gScreenTime,"已同时开始安装,请返回桌面,5-6分钟后桌面查看有ruiyinxin签名的两个app全部安装成功才可测试通过,若验证成功请手动卸载应用后,重新进入本案例进行其他测试");	
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
		intentFilter.addAction("android.intent.action.DELETE_APP");
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
//	线程1 安装V1
	Thread smartpos =new Thread()
	{
		public void run()
		{
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName = apkPara[0][0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		}
	};
//线程2 安装V2
	Thread CpayAutoActive =new Thread()
	{
		public void run()
		{
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName =apkPara[1][0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		}
	};
//线程3 安装错误签名的apk1
	Thread app_sign =new Thread()
	{
		public void run()
		{
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName =apkPara[2][0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		}
	};
//线程4 安装错误签名的apk2
	Thread CiticSdkDemo =new Thread()
	{
		public void run()
		{
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName =apkPara[3][0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		}
	};
}

