package com.example.highplattest.psbc;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: 邮储白名单需求 
 * file name 		: Psbc5.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180828
 * directory 		: 增加应用白名单
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 * 					 wangxy		       20180828 	  created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Psbc5 extends UnitFragment 
{
	private final String CLASS_NAME = Psbc5.class.getSimpleName();
	private final String TESTITEM = "邮储证书自更新白名单";
	private ApkBroadCastReceiver apkReceiver;
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	private final int APKTIME = 120; /**每个广播接收时间为1分钟*/
	private Gui gui = new Gui(myactivity, handler);
	long startTime;
	int time = 0;
	int succ=0;
	int cnt=0;
	boolean isSecond=false;
	Uri uri =null;
	Intent intent2 = null;
	String[][] apkPara = 
		{
			{GlobalVariable.sdPath+"apk/DroidClient_21009_20180713_signedpro_2.10.09.apk",  "com.gsc.mdm"},//低版本带签名+自更新白名单
			{GlobalVariable.sdPath+"apk/NEWLAND_PSBCBANK_20180530_V1.0.0_nl_signd.apk",  "com.newland.psbc.payment"},//带签名+不是自更新白名单
		};
	String[][] whiteInstallApk = 
		{
			{GlobalVariable.sdPath+"apk/DroidClient_21009_20180713_2.10.09.apk", "0" },//不带签名+同本版本+自更新白名单
			{GlobalVariable.sdPath+"apk/DroidClient_21009_20180713_signedpro_2.10.09.apk", "0" },//带签名+同版本+自更新白名单
			{GlobalVariable.sdPath+"apk/MyDroidClient_2.10.09.apk", "-7" },//不带签名+同版本+自构造+自更新白名单，显示-7，静默-104

		};
	String[][] noWhiteInstallApk = 
		{
			{GlobalVariable.sdPath+"apk/NEWLAND_PSBCBANK_20180530_V1.0.1.apk",  "-104"},//未签名的不在自更新白名单的高版本apk,关控制台情况下，提示签名错误安装失败，开控制台情况下返回-104但可以安装成功，因为是开发调试样机
			{GlobalVariable.sdPath+"apk/NEWLAND_PSBCBANK_20180530_V1.0.1_nl_signd.apk","0"} //带签名的不在自更新白名单的高版本apk，即使不在白名单中也应成功
		};
	
	public void psbc5() 
	{
		String funcName = "psbc5";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*if(GlobalVariable.gCustomerID != CUSTOMER_ID.PSBC)// 只有PSBC的固件才支持该接口
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "%s固件不支持%s接口", GlobalVariable.gCustomerID,TESTITEM);
			return;
		}*/
		gui.cls_show_msg("该用例需在控制台关闭和开启情况下都进行测试，测试前请确保证书为邮储证书并把测试apk放置到内置SD卡的apk/目录下，请确保各个应用均未安装，确认则点击任意键继续");
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("邮储测试\n0.显式自更新\n1.隐式自更新\n2.卸载白名单卸载\n");
			switch (returnValue) 
			{
			case '0':
				install();
				break;
			case '1':
				hideInstall();
				break;
			case '2':
				uninstall();
				break;
			case ESC://取消键
				unitEnd();
				return;
			}
		}
	}
	//卸载卸载白名单中的应用应失败并返回-203，但设备恢复出厂可成功卸载卸载白名单
	private void uninstall() 
	{
		String funcName="uninstall";
		gui.cls_show_msg("请进入桌面--应用查看，确保各个应用均未安装，确认则点击任意键继续");
		// 测试前置，显示安装卸载白名单中的应用DroidClient_21009_20180713_signedpro_2.10.09.apk
		Intent intent = new Intent(Intent.ACTION_VIEW);
		expPackName = apkPara[0][0];
		gui.cls_show_msg1(2, "正在安装卸载白名单中的中国邮政存储银行MDM.apk");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		String name = expPackName.split("apk/")[1];
		gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面", name);
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)", Tools.getLineInfo(), name,currentName, respCode);
			if (!GlobalVariable.isContinue)
				return;
		}

		// case1:显式卸载，卸载卸载白名单中的apk，应失败--->DroidClient_21009_20180713_signedpro_2.10.09.apk，包名：com.gsc.mdm
		if (gui.cls_show_msg("是否显式进行卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			expPackName = apkPara[0][1];
			uri = Uri.parse("package:" + expPackName);
			intent2 = new Intent(Intent.ACTION_DELETE, uri);
			getActivity().startActivity(intent2);
			gui.cls_show_msg("中国邮政存储银行MDM应用卸载完毕时点击任意键继续");
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_APP_WHITE_UNINSTALL) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:卸载appmarket_v2.0.2_20170718_double_signe app失败(pack = %s,resp=%s)",Tools.getLineInfo(), currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 手动卸载
			if (gui.cls_show_msg("请手动卸载中国邮政存储银行MDM.apk，是否提示白名单应用不允许卸载，是点击[确认]，其他点击[取消]") != ENTER) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:手动卸载appmarket_v2.0.2_20170718_double_signe app成功，预期失败(pack = %s,resp=%s)",Tools.getLineInfo(), currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}

		// case2:隐式卸载,卸载卸载白名单中的apk，应失败--->DroidClient_21009_20180713_signedpro_2.10.09.apk，包名：com.gsc.mdm
		if (gui.cls_show_msg("是否隐式进行卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			expPackName = apkPara[0][1];
			uri = Uri.parse("package:" + expPackName);
			intent2 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri);
			myactivity.startActivity(intent2);
			time = 0;
			// 循环等待一分钟
			startTime = System.currentTimeMillis();
			while (time < APKTIME) 
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_APP_WHITE_UNINSTALL) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName, currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}

		// case3恢复出厂卸载，应该成功
		if (gui.cls_show_msg("是否进行通过设备恢复出厂来卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			gui.cls_show_msg("请进入设置-备份和重置中手动进行恢复出厂设置操作，设备恢复出厂后，查看桌面--应用，卸载白名单中的邮储银行MDM.apk预期应被卸载成功，完成点击任意键继续");
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	//显式安装更新自更新白名单中的应用
	public void install()
	{
		String funcName="install";
		gui.cls_show_msg("请进入桌面--应用查看，确保各个应用均未安装，确认则点击任意键继续");
		//测试前置,安装低版本带签名的apk
		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (gui.cls_show_msg("是否进行前置低版本测试应用安装（默认情况应进行），是点击[确认]，其他点击[取消]") == ENTER) 
		{
			for (int i = 0; i < apkPara.length; i++) 
			{
				expPackName = apkPara[i][0];
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				String name = expPackName.split("apk/")[1];
				gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面", name);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)", Tools.getLineInfo(),name, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
		}
		//case1.1：显性安装,已安装低版本的自更新白名单中的应用，使用同版本的未签名的apk进行自更新应成功更新（返回0）
		//case1.2：显性安装,已安装低版本的自更新白名单中的应用，使用同版本的带签名的apk进行自更新应成功更新（返回0）
		//case1.3：显性安装,已安装低版本的自更新白名单中的应用，使用同版本的未签名的自构造的同包名的apk进行自更新应成功更新（返回-7，预期更新失败）
		if (gui.cls_show_msg("是否进行自更新白名单中的应用自更新测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			whiteInstallApk[2][1]="-7";
			for (int i = 0; i < whiteInstallApk.length; i++) 
			{
				expPackName = whiteInstallApk[i][0];
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				String name = expPackName.split("apk/")[1];
				gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面,安装结束后请查看应用版本是否一致", name);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != Integer.valueOf(whiteInstallApk[i][1])) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)", Tools.getLineInfo(),name, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
		}

		
		//case2.1：显性安装,已安装低版本带签名的不在自更新白名单中的应用，高版本不带验签的自更新预期应失败
		//case2.2：显性安装,已安装低版本带签名的不在自更新白名单中的应用，高版本带验签的自更新预期应成功(因为带了验签)
		if (gui.cls_show_msg("是否进行更新非白名单应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			for (int i = 0; i < noWhiteInstallApk.length; i++) 
			{
				expPackName = noWhiteInstallApk[i][0];
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				String name = expPackName.split("apk/")[1];
				gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面", name);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != Integer.valueOf(noWhiteInstallApk[i][1])) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)", Tools.getLineInfo(),name, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
				// 查看是否版本变高
				if (expPackName.equals(GlobalVariable.sdPath + "apk/NEWLAND_PSBCBANK_20180530_V1.0.1.apk")) 
				{
					if (gui.cls_show_msg("请查看邮储银行POS应用的版本号是否为1.0.0，预期不应变高，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:版本号预期不应提高", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
				if (expPackName.equals(GlobalVariable.sdPath + "apk/NEWLAND_PSBCBANK_20180530_V1.0.1_nl_signd.apk")) 
				{
					if (gui.cls_show_msg("请查看邮储银行POS应用的版本号是否为1.0.1，预期版本号应变高，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:版本号预期应提高", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
			}
		}
		//case3:卸载已安装的应用后，再次安装，依然需要验签。安装、卸载后，再安装未验签但存在于白名单中的同一个应用，应安装失败
		//卸载原有DroidClient_21009_20180713_signedpro_2.10.09.apk,包名：com.gsc.mdm
		if (gui.cls_show_msg("是否进行自更新白名单应用安装、卸载后再安装的测试，预期使用未签名的应用再次自更新白名单应失败，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			gui.cls_show_msg("请手动将设备进行恢复出厂,恢复出厂开机后重新进入该用例且跳过其他步骤直接进入此子用例进行测试,完毕时点击任意键继续");
			// 再次安装不带签名的DroidClient_21009_20180713_2.10.09.apk,包名：com.gsc.mdm，预期安装失败-104
			expPackName = whiteInstallApk[0][0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			gui.cls_show_msg("未签名中国邮政储蓄银行MDM应用安装完毕时点击任意键继续，请耐心等待安装界面,预期安装失败，安装完毕后请查看应用中是否安装");
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:安装中国邮政储蓄银行MDM失败(pack = %s,ret = %d)",Tools.getLineInfo(), currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}

	//隐形安装更新自更新白名单中的应用
	public void hideInstall()
	{
		String funcName="hideInstall";
		gui.cls_show_msg("请进入桌面--应用，确保各个应用均未安装，确认则点击任意键继续");
		//测试前置
		Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//测试前置，隐式安装带签名的低版本的应用
		if (gui.cls_show_msg("是否进行前置低版本测试应用安装（默认情况应进行），是点击[确认]，其他点击[取消]") == ENTER) 
		{
			for (int i = 0; i < apkPara.length; i++) 
			{
				expPackName = apkPara[i][0];
				gui.cls_show_msg1(2, "正在安装第%d个应用,应用=%s", i, expPackName.split("apk/")[1]);
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				time = 0;
				// 循环等待1分钟
				startTime = System.currentTimeMillis();
				while (time < APKTIME) 
				{
					time = (int) Tools.getStopTime(startTime);
					SystemClock.sleep(1000);
					if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
						break;
				}
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:i=%d,静默安装app失败（apk = %s,%s，%d）",
							Tools.getLineInfo(), i, expPackName, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
		}
		// case1.1：隐式安装,已安装低版本的自更新白名单中的应用，使用同版本的未签名的apk进行自更新应成功更新（返回0）
		// case1.2：隐式安装,已安装低版本的自更新白名单中的应用，使用同版本的带签名的apk进行自更新应成功更新（返回0）
		// case1.3：隐式安装,已安装低版本的自更新白名单中的应用，使用同版本的未签名的自构造的同包名的apk进行自更新应成功更新（返回-104，预期更新失败）
		if (gui.cls_show_msg("是否进行自更新白名单中的应用自更新测试，是点击[确认]，其他点击[取消]") == ENTER)
		{
			whiteInstallApk[2][1]="-104";
			for (int i = 0; i < whiteInstallApk.length; i++) 
			{
				expPackName = whiteInstallApk[i][0];
				gui.cls_show_msg1(10, "正在自更新白名单第%d个应用,应用=%s", i, expPackName.split("apk/")[1]);
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				time = 0;
				// 循环等待1分钟
				startTime = System.currentTimeMillis();
				while (time < APKTIME) 
				{
					time = (int) Tools.getStopTime(startTime);
//					 SystemClock.sleep(1000);
					if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
						break;
				}
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != Integer.valueOf(whiteInstallApk[i][1])) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:i=%d,静默安装app失败（apk = %s,%s，%d）",Tools.getLineInfo(), i, expPackName, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
		}
		
		// case2.1：隐式安装,已安装低版本带签名的不在自更新白名单中的应用，高版本不带验签的自更新预期应失败
		// case2.2：隐式安装,已安装低版本带签名的不在自更新白名单中的应用，高版本带验签的自更新预期应成功(因为带了验签)
		if (gui.cls_show_msg("是否进行更新非白名单应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			for (int i = 0; i < noWhiteInstallApk.length; i++) 
			{
				expPackName = noWhiteInstallApk[i][0];
				gui.cls_show_msg1(10, "正在自更新不在白名单中的第%d个应用,应用=%s", i, expPackName.split("apk/")[1]);
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				time = 0;
				// 循环等待1分钟
				startTime = System.currentTimeMillis();
				while (time < APKTIME) 
				{
					time = (int) Tools.getStopTime(startTime);
					SystemClock.sleep(1000);
					if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
						break;
				}
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != Integer.valueOf(noWhiteInstallApk[i][1])) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:i=%d,静默安装app失败（apk = %s,%s，%d）",Tools.getLineInfo(), i, expPackName, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
				// 等待1min，再次查看是否版本变高
				if (expPackName.equals(GlobalVariable.sdPath + "apk/NEWLAND_PSBCBANK_20180530_V1.0.1.apk")) 
				{
					if (gui.cls_show_msg("等待1min后，请查看邮储银行POS应用的版本号是否为1.0.0，预期不应变高，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:版本号预期不应提高", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
				if (expPackName.equals(GlobalVariable.sdPath + "apk/NEWLAND_PSBCBANK_20180530_V1.0.1_nl_signd.apk")) 
				{
					if (gui.cls_show_msg("请查看邮储银行POS应用的版本号是否为1.0.1，预期带签名应更新成功，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:版本号预期应版本更新成功", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
			}
		}
		
		//case3:隐式，卸载已安装的应用后，再次安装，依然需要验签。安装、卸载后，再安装未验签但存在于白名单中的同一个应用，应安装失败--->
		//卸载原有DroidClient_21009_20180713_signedpro_2.10.09.apk,包名：com.gsc.mdm
		if (gui.cls_show_msg("是否进行自更新白名单应用安装、卸载后再安装的测试，预期使用未签名的应用再次自更新白名单应失败，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			gui.cls_show_msg("请手动将设备进行恢复出厂,恢复出厂开机后重新进入该用例且跳过其他步骤直接进入此子用例进行测试,完毕时点击任意键继续");
			// 再次安装不带签名的DroidClient_21009_20180713_2.10.09.apk,包名：com.gsc.mdm，预期安装失败-104
			gui.cls_show_msg1(1,"正在安装不带邮储签名的中国邮政储蓄银行MDM应用，预期安装失败，安装结束后请查看桌面--应用中是否安装");
			expPackName = whiteInstallApk[0][0];
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
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName,currentName,respCode);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
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
