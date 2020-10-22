package com.example.highplattest.installapp;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
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
 * module 			: 美团固件白名单需求 
 * file name 		: InstallApp9.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180718
 * directory 		: 增加应用白名单
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 * 					 wangxy		       20180718 	  created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp9 extends UnitFragment 
{
	private final String TESTITEM = "美团证书自更新白名单与卸载白名单";
	public final String TAG = InstallApp9.class.getSimpleName();
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
			{GlobalVariable.sdPath+"apk/ele_7.5.1_signed.apk",  "me.ele.napos"},//带签名+自更新白名单
			{GlobalVariable.sdPath+"apk/meituan.apk",            "com.sankuai.meituan.meituanwaimaibusiness"},//带签名+自更新白名单
			{GlobalVariable.sdPath+"apk/nld_jxyh_320007_sign.apk",     "com.newland.payment"},//带签名+不是自更新白名单
			{GlobalVariable.sdPath+"apk/appmarket_v2.0.2_20170718_double_signed.apk",   "com.landicorp.zacloud.appmarket"},//带签名+卸载白名单
		};
	String[][] whiteInstallApk = 
		{
			{GlobalVariable.sdPath+"apk/ele_7.6.5.apk", "0" },//第一层验签失败-108，但是是自更新白名单中的应用且已安装低版本，故第二层返回0；则最后结果只返回0
			{GlobalVariable.sdPath+"apk/ele_7.6.5_signed.apk", "0" },
			{GlobalVariable.sdPath+"apk/meituan2.apk", "-7" }//自行构造的不带验签的自更新白名单中同包名apk，预期失败,第一层验签失败-108，但第二层静默安装返回-104，显式第二层返回-7，故最后结果只返回第二层；
		};
	String[][] noWhiteInstallApk = 
		{
			{GlobalVariable.sdPath+"apk/nld_jxyh_320008.apk",  "-108"},//未签名的不在自更新白名单的高版本apk，关控制台情况下，提示签名错误安装失败，开控制台情况下返回-108但可以安装成功，因为是开发调试样机
			{GlobalVariable.sdPath+"apk/nld_jxyh_320008_sign.apk","0"} //带签名的不在自更新白名单的高版本apk，即使不在白名单中也应成功
		};
	
	public void installapp9() 
	{
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp9", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		if(GlobalVariable.gCustomerID != CUSTOMER_ID.MeiTuan)// 只有MeiTuan的固件才支持该接口
		{
			gui.cls_show_msg1_record(TAG, "installapp9", gScreenTime, "%s固件不支持%s接口", GlobalVariable.gCustomerID,TESTITEM);
			return;
		}
		gui.cls_show_msg("该用例需在控制台关闭和开启情况下都进行测试，测试前请确保证书为美团证书并把测试apk放置到内置SD卡的apk/目录下，请确保各个应用均未安装，确认则点击任意键继续");
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("美团测试\n0.显式自更新\n1.隐式自更新\n2.卸载白名单卸载\n");
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
		gui.cls_show_msg("请进入桌面--应用查看，确保各个应用均未安装，确认则点击任意键继续");
		//测试前置，显示安装卸载白名单中的应用appmarket_v2.0.2_20170718_double_signed.apk
		Intent intent = new Intent(Intent.ACTION_VIEW);
		expPackName = apkPara[3][0];
		gui.cls_show_msg1(2,"正在安装卸载白名单中的应用市场.apk");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		String name=expPackName.split("apk/")[1];
		gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面",name);
		if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)",Tools.getLineInfo(), name,currentName, respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case1:显式卸载，卸载卸载白名单中的apk，应失败--->appmarket_v2.0.2_20170718_double_signed.apk，包名：com.landicorp.zacloud.appmarket
		if (gui.cls_show_msg("是否显式进行卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			expPackName = apkPara[3][1];
			uri = Uri.parse("package:"+expPackName);
			intent2 = new Intent(Intent.ACTION_DELETE, uri);
			getActivity().startActivity(intent2);
			gui.cls_show_msg("appmarket_v2.0.2_20170718_double_signe应用卸载完毕时点击任意键继续");
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != ERROR_PACKAGE_DELETE_FAILED_APP_WHITE_UNINSTALL) 
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:卸载appmarket_v2.0.2_20170718_double_signe app失败(pack = %s,resp=%s)",Tools.getLineInfo(), currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			//手动卸载
			if (gui.cls_show_msg("请手动卸载应用市场.apk，是否提示白名单应用不允许卸载，是点击[确认]，其他点击[取消]") != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:手动卸载appmarket_v2.0.2_20170718_double_signe app成功，预期失败(pack = %s,resp=%s)",Tools.getLineInfo(), currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case2:隐式卸载,卸载卸载白名单中的apk，应失败--->appmarket_v2.0.2_20170718_double_signed.apk，包名：com.landicorp.zacloud.appmarket
		if (gui.cls_show_msg("是否隐式进行卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			expPackName = apkPara[3][1];
			uri = Uri.parse("package:"+expPackName);
			intent2 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri);
			myactivity.startActivity(intent2);
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
			if ((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_WHITE_UNINSTALL) 
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		//add by wangxy20180919
		// case3:sdk服务有权限卸载白名单应用,sdk服务卸载卸载白名单中的apk，应成功--->appmarket_v2.0.2_20170718_double_signed.apk，包名：com.landicorp.zacloud.appmarket
		if (gui.cls_show_msg("是否进行sdk服务卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			expPackName = apkPara[3][1];
			uri = Uri.parse("package:" + expPackName);
			intent2 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri);
			//将sdk的包名一块传过去就可以卸载卸载白名单中的应用
			intent2.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,"com.meituan.newland.service");
			myactivity.startActivity(intent2);
			apkReceiver.setUnintallName(null);
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
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_DELETE_SUCCESS) {
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName, currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			//sdk服务卸载卸载白名单中的应用，预期应卸载成功
			if ((gui.ShowMessageBox(("请确认设备中的应用市场.apk是否已卸载成功").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case4恢复出厂卸载，应该成功
		if (gui.cls_show_msg("是否进行通过设备恢复出厂来卸载卸载白名单中应用的测试，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			if ((gui.ShowMessageBox(("请确认设备中是否已安装应用市场.apk（如未安装，用例后续将进行安装）").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
			{
				//未安装卸载白名单中的应用，则安装
				expPackName = apkPara[3][0];
				gui.cls_show_msg1(2,"正在安装卸载白名单中的应用市场.apk");
				intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
				myactivity.startActivity(intent);
				name=expPackName.split("apk/")[1];
				gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面",name);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
				{
					gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)",Tools.getLineInfo(), name,currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			gui.cls_show_msg("请进入设置-备份和重置中手动进行恢复出厂设置操作，设备恢复出厂后，查看桌面--应用，卸载白名单中的市场应用.apk预期应被卸载成功，完成点击任意键继续");
		}
		
		gui.cls_show_msg1_record(TAG, "installapp9", gScreenTime,"%s测试通过", TESTITEM);
	}

	//显式安装更新自更新白名单中的应用
	public void install()
	{
		gui.cls_show_msg("请进入桌面--应用查看，确保各个应用均未安装，确认则点击任意键继续");
		//测试前置,安装低版本带签名的apk
		Intent intent = new Intent(Intent.ACTION_VIEW);
		for (int i = 0; i < apkPara.length-1; i++) 
		{
			expPackName = apkPara[i][0];
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			String name=expPackName.split("apk/")[1];
			gui.cls_show_msg("%s应用安装完毕时点击任意键继续，请耐心等待安装界面",name);
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)",Tools.getLineInfo(), name,currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case1.1：显性安装,已安装低版本的自更新白名单中的应用，使用其高版本的未签名的apk进行自更新应成功更新（未签名的会返回-108，预期更新成功）
		//case1.2：显性安装,已安装低版本的自更新白名单中的应用，使用其高版本的带签名的apk进行自更新应成功更新（返回0）
		//case1.3：显性安装,已安装低版本的自更新白名单中的应用，使用其高版本的未签名的自构造的同包名的apk进行自更新应成功更新（返回-108，预期更新失败）
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
					gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)", Tools.getLineInfo(),name, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
				// 查看不带验签的ele_7.6.5.apk是否安装成功
				if (expPackName.equals(GlobalVariable.sdPath+"apk/ele_7.6.5.apk")) 
				{
					if (gui.cls_show_msg("请查看饿了么版本号是否更新成功为7.6.5，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:用未签名的高版本的apk来更新已安装自更新白名单中应用失败", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
				// 查看自构造美团2是否安装失败
				if (expPackName.equals(GlobalVariable.sdPath + "apk/meituan2.apk")) 
				{
					if (gui.cls_show_msg("请查看美团应用的版本号是否为3.8.0.326，预期不应更新为自构造的meituan2应用，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:自构造的应用预期不应自更新成功", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
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
					gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:安装%s失败(pack = %s,ret = %d)", Tools.getLineInfo(),name, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
				// 查看江西银行是否版本变高
				if (expPackName.equals(GlobalVariable.sdPath + "apk/nld_jxyh_320008.apk")) 
				{
					if (gui.cls_show_msg("请查看江西银行应用的版本号是否为320007，预期不应变高，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:版本号预期不应提高", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
			}
		}
		
		//case3:卸载已安装的应用后，再次安装，依然需要验签。安装、卸载后，再安装未验签但存在于白名单中的同一个应用，应安装失败--->ele
		//卸载原有ele_7.6.5_signed.apk,包名：me.ele.napos
		if (gui.cls_show_msg("是否进行自更新白名单应用安装、卸载后再安装的测试，预期使用未签名的应用再次自更新白名单应失败，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			uri = Uri.parse("package:me.ele.napos");
			intent2 = new Intent(Intent.ACTION_DELETE, uri);
			getActivity().startActivity(intent2);
			expPackName = "me.ele.napos";
			gui.cls_show_msg("ele_7.6.5_signed应用卸载完毕时点击任意键继续");
			if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName) == false) 
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:卸载ele_7.6.5_signed app失败(pack = %s)",Tools.getLineInfo(), TESTITEM, currentName);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 再次安装不带签名的ele_7.6.5.apk,包名：me.ele.napos，预期安装失败-108
			expPackName = GlobalVariable.sdPath + "apk/ele_7.6.5.apk";
			intent.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
			myactivity.startActivity(intent);
			gui.cls_show_msg("未签名ele_7.6.5应用安装完毕时点击任意键继续，请耐心等待安装界面,预期安装失败，安装完毕后请查看应用中是否安装");
			if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED2)
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:安装ele_7.6.5app失败(pack = %s,ret = %d)",Tools.getLineInfo(), currentName, respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
		}

		//测试后置，卸载
		for (int i = 1; i < apkPara.length-1; i++) 
		{
			expPackName = apkPara[i][1];
			uri = Uri.parse("package:"+expPackName);
			intent2 = new Intent(Intent.ACTION_DELETE, uri);
			getActivity().startActivity(intent2);
			gui.cls_show_msg("测试后置，正在卸载第%d个应用卸载完毕时点击任意键继续",i);
			if((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false)
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:卸载包名为%s的app失败(pack = %s)", Tools.getLineInfo(),expPackName,currentName);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		gui.cls_show_msg1_record(TAG, "installapp9", gScreenTime,"%s测试通过", TESTITEM);
	}

	//隐形安装更新自更新白名单中的应用
	public void hideInstall()
	{
		gui.cls_show_msg("请进入桌面--应用，确保各个应用均未安装，确认则点击任意键继续");
		//测试前置
		Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//测试前置，隐式安装带签名的低版本的应用
		for (int i = 0; i < apkPara.length-1; i++) 
		{
			expPackName = apkPara[i][0];
			gui.cls_show_msg1(2,"正在安装第%d个应用,应用=%s",i,expPackName.split("apk/")[1]);
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
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:i=%d,静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),i,expPackName,currentName,respCode);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case1.1：隐式安装,已安装低版本的自更新白名单中的应用，使用其高版本的未签名的apk进行自更新应成功更新（未签名的会返回-108，预期更新成功）
		// case1.2：隐式安装,已安装低版本的自更新白名单中的应用，使用其高版本的带签名的apk进行自更新应成功更新（返回0）
		// case1.3：隐式安装,已安装低版本的自更新白名单中的应用，使用其高版本的未签名的自构造的同包名的apk进行自更新应成功更新（返回-108，预期更新失败）
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
					// SystemClock.sleep(1000);
					if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
						break;
				}
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != Integer.valueOf(whiteInstallApk[i][1])) 
				{
					gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:i=%d,静默安装app失败（apk = %s,%s，%d）",Tools.getLineInfo(), i, expPackName, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
				// 未签名的已安装的高版本白名单apk，虽然返回-108，但要等待它安装完毕，再发出广播0
				if (expPackName.equals(GlobalVariable.sdPath + "apk/ele_7.6.5.apk")) 
				{
					/*time = 0;
					// 循环等待2分钟
					startTime = System.currentTimeMillis();
					while (time < APKTIME) 
					{
						time = (int) Tools.getStopTime(startTime);
						SystemClock.sleep(1000);
						if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)&& (respCode = apkReceiver.getResp(APK_INSTALL)) == PACKAGE_INSTALL_SUCCESS)
							break;
					}
					if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != PACKAGE_INSTALL_SUCCESS) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:i=%d,静默安装app失败，第二次接收广播返回值（apk = %s,%s，%d）",Tools.getLineInfo(), i, expPackName, currentName, respCode);
						if (!GlobalVariable.isContinue)
							return;
					}*/
					if (gui.cls_show_msg("请查看当前饿了么版本号是否更新为7.6.5，是点击[确认]，其他点击[取消]") != ENTER)
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:第%d次,隐式更新未签名的高版本的自更新白名单中的app失败",Tools.getLineInfo(), i);
						if (!GlobalVariable.isContinue)
							return;
					}
				}
				// 自构造的带白名单包名的apk，进行第二次新大陆验签，返回-104
				if (expPackName.equals(GlobalVariable.sdPath + "apk/meituan2.apk")) 
				{
					/*time = 0;
					// 循环等待1分钟
					startTime = System.currentTimeMillis();
					while (time < APKTIME) 
					{
						time = (int) Tools.getStopTime(startTime);
						SystemClock.sleep(1000);
						if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)&& (respCode = apkReceiver.getResp(APK_INSTALL)) == ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED)
							break;
					}
					if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName) == false|| (respCode = apkReceiver.getResp(APK_INSTALL)) != ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:i=%d,静默安装app失败，第二次接收广播返回值（apk = %s,%s，%d）",Tools.getLineInfo(), i, expPackName, currentName, respCode);
						if (!GlobalVariable.isContinue)
							return;
					}*/
					// 查看自构造美团2是否安装失败
					if (gui.cls_show_msg("请查看美团应用的版本号是否为3.8.0.326，预期不应更新为自构造的meituan2应用，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:第%d次,自构造的应用预期不应自更新成功", Tools.getLineInfo(),i);
						if (!GlobalVariable.isContinue)
							return;
					}
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
					gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:i=%d,静默安装app失败（apk = %s,%s，%d）",Tools.getLineInfo(), i, expPackName, currentName, respCode);
					if (!GlobalVariable.isContinue)
						return;
				}
				// 等待1min，再次查看江西银行是否版本变高
				if (expPackName.equals(GlobalVariable.sdPath + "apk/nld_jxyh_320008.apk")) 
				{
					if (gui.cls_show_msg("等待1min后，请查看江西银行应用的版本号是否为320007，预期不应变高，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:版本号预期不应提高", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
				if (expPackName.equals(GlobalVariable.sdPath + "apk/nld_jxyh_320008_sign.apk")) 
				{
					if (gui.cls_show_msg("请查看江西银行应用的版本号是否为320008，预期带签名应更新成功，是点击[确认]，其他点击[取消]") != ENTER) 
					{
						gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr, "line %d:版本号预期应版本更新成功", Tools.getLineInfo());
						if (!GlobalVariable.isContinue)
							return;
					}
				}
			}
		}
		
		//case3:隐式，卸载已安装的应用后，再次安装，依然需要验签。安装、卸载后，再安装未验签但存在于白名单中的同一个应用，应安装失败--->ele
		//卸载原有ele再次安装不带签名的ele_7.6.5.apk，包名：me.ele.napos
		if (gui.cls_show_msg("是否进行自更新白名单应用安装、卸载后再安装的测试，预期使用未签名的应用再次自更新白名单应失败，是点击[确认]，其他点击[取消]") == ENTER) 
		{
			uri = Uri.parse("package:me.ele.napos");
			intent2 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri);
			myactivity.startActivity(intent2);
			expPackName = "me.ele.napos";
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
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),expPackName,currentName,respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			//再次安装不带签名的ele_7.6.5.apk,包名：me.ele.napos，预期安装失败-108
			gui.cls_show_msg1(10,"正在安装不带美团签名的elm应用，预期安装失败，安装结束后请查看桌面--应用中是否安装");
			expPackName = GlobalVariable.sdPath+"apk/ele_7.6.5.apk";
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
			if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=ERROR_PACKAGE_INSTALL_FAILED_SIGNATURE_FAILED2)
			{
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName,currentName,respCode);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		//测试后置，卸载
		for (int i = 1; i < apkPara.length-1; i++) 
		{
			expPackName = apkPara[i][1];
			gui.cls_show_msg1(10,"正在卸载第%d个应用,包名=%s",i,expPackName);
			uri = Uri.parse("package:"+expPackName);
			intent2 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri);
			myactivity.startActivity(intent2);
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
				gui.cls_show_msg1_record(TAG, "installapp9", gKeepTimeErr,"line %d:i=%d,静默卸载app测试失败(apk = %s,%s,%d)", Tools.getLineInfo(),i,expPackName,currentName,respCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			
		}
		gui.cls_show_msg1_record(TAG, "installapp9", gScreenTime,"%s测试通过", TESTITEM);
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
