package com.example.highplattest.installapp;

import java.io.File;
import java.util.Random;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: Google安装app
 * file name 		: InstallApp3.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170829
 * directory 		: 
 * description 		: 测试installApp
 * related document : 
 * history 		 	: author			date			remarks
 * 					 wangxy		   20170829	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp200 extends UnitFragment {
	private final String TESTITEM = "Google安装模块内随机";
	public final String TAG = InstallApp200.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private String ApnFunArr[] = {"installApp1","deleteApp1","installApp2","deleteApp2"};
	private Random random = new Random();//显式隐式安装app
	boolean installFlag=false;
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	private int respCode;		/**返回码*/
	private final int APKTIME = 60; /**每个广播接收时间为1分钟*/
	private Intent intent = new Intent(Intent.ACTION_VIEW);//显式意图
	private Intent intent2 = new Intent(NlIntent.ACTION_VIEW_HIDE);//隐式意图
	private long startTime;
	private int time = 0;
	private ApkBroadCastReceiver apkReceiver;
	private int id;
	private String funcStr1,funcStr2 ;
	String[][] apkPara = 
		{
			{GlobalVariable.sdPath+"apk/A1_unsign.apk","com.example.mainapp","-104","0"},
			{GlobalVariable.sdPath+"apk/A2_sign.apk","com.example.k21testapp","0","0"},
			{GlobalVariable.sdPath+"apk/B1_unsign.apk","com.qualcomm.bluetoothclient","0","0"},
			{GlobalVariable.sdPath+"apk/B2_sign.apk","cn.com.feicui.assist","0","0"},
			{GlobalVariable.sdPath+"apk/地图.apk","com.baidu.BaiduMap","0","0"}
		};
	
	
	public void installapp200() {
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp200", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		gui.cls_show_msg("该用例需在控制台开启情况下测试，测试前请确保证书已更新与服务器一致并把测试apk放置到内置SD卡的apk/目录下，各个应用均未安装，" +
				"放置完毕去apk目录查看是否有乱码文件，若有乱码请将该apk文件重命名为地图，并且测试apk均未安装");
		// 设置次数
		int succ = 0, cnt = g_RandomTime, bak = g_RandomTime;
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "APN模块内随机组合测试中...\n还剩%d次（已成功%d次），按【取消】退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=ApnFunArr[random.nextInt(ApnFunArr.length)];
			}
			funcStr1 = "";
			funcStr2 = "";
			for(int i=0;i<g_RandomCycle;i++){
				if(i<10){
					funcStr1 = funcStr1 + func[i] + "-->\n";
				}else{
					funcStr2 = funcStr2 + func[i] + "-->\n";
				}
				
			}
			gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr1,bak-cnt+1);
			gui.cls_show_msg1(gScreenTime, funcStr2);
			cnt--;
			boolean ret=false;
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"正在测试%s",func[i]);
				InstallFuncName fname = InstallFuncName.valueOf(func[i]);
				if(!(ret=RandomTest(fname)))
					break;
			}
			if(ret)
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "installapp200", gScreenTime, "Google安装模块内随机组合测试测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);

	}
   private boolean RandomTest(InstallFuncName fname) {
		
	// TODO Auto-generated method stub
	boolean is =true;
	switch (fname) {
	case installApp1:
		id=random.nextInt(apkPara.length);
		expPackName = apkPara[id][0];
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("该应用安装完毕时点击任意键继续，请耐心等待安装界面");
		if((currentName=apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=Integer.valueOf(apkPara[id][2]))
		{
			gui.cls_only_write_msg(TAG, "installapp200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
			gui.cls_show_msg1_record(TAG, "installapp200", gKeepTimeErr,"line %d:installApp1显示安装app失败(pack = %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
			is=false;
	        installFlag=false;
		}else{
			installFlag=true;
		}
		break;
	case deleteApp1:
		if(installFlag){
			expPackName = apkPara[id][1];
			Uri uri2 = Uri.parse("package:"+apkPara[id][1]);
			Intent intent2 = new Intent(Intent.ACTION_DELETE, uri2);
			getActivity().startActivity(intent2);
			gui.cls_show_msg("该应用卸载完毕点击任意键");
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != Integer.valueOf(apkPara[id][3]))
			{
				gui.cls_only_write_msg(TAG, "installapp200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(TAG, "installapp200", gKeepTimeErr,"line %d:%sdeleteApp1显示卸载app测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
				is=false;
			}else{
				installFlag=false;
			}
		}else{
			Uri uri12 = Uri.parse("package:com.unexist.apk");
			Intent intent12 = new Intent(Intent.ACTION_DELETE, uri12);
			myactivity.startActivity(intent12);
			expPackName = "com.unexist.apk";
			gui.cls_show_msg("提示卸载应用不存在按任意键继续");
			if((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND)
			{
				gui.cls_only_write_msg(TAG, "installapp200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(TAG, "installapp200", gKeepTimeErr,"line %d:deleteApp1显示卸载不存在的app失败(pack = %s,ret = %d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
				is=false;
			}else{
				installFlag=false;
			}
		}
		
		break;
	case installApp2:
			id = random.nextInt(apkPara.length);
			expPackName = apkPara[id][0];
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.setDataAndType(Uri.fromFile(new File(expPackName)), "application/vnd.android.package-archive");
			myactivity.startActivity(intent2);
			// 循环等待2分钟
			startTime = System.currentTimeMillis();
			while (time < APKTIME * 2) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if ((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName))
					break;
			}
			if((currentName=apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=Integer.valueOf(apkPara[id][2]))
			{
				gui.cls_only_write_msg(TAG, "installapp200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(TAG, "installapp200", gKeepTimeErr,"line %d:installApp2静默安装app失败(pack = %s,ret = %d)", Tools.getLineInfo(),currentName,respCode);
		        installFlag=false;
		        is=false;
			}else{
				installFlag=true;
			}
		break;
	case deleteApp2:
		if(installFlag){
			expPackName = apkPara[id][1];
			Uri uri2 = Uri.parse("package:"+apkPara[id][1]);
			Intent intent2 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri2);
			getActivity().startActivity(intent2);
			// 循环等待一分钟
			startTime = System.currentTimeMillis();
			while(time<APKTIME)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL)) != Integer.valueOf(apkPara[id][3]))
			{
				gui.cls_only_write_msg(TAG, "installapp200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(TAG, "installapp200", gKeepTimeErr,"line %d:%sdeleteApp2静默卸载app测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
				is=false;
			}else{
				installFlag=false;
			}
		}else{
			expPackName = "com.unexist.name";
			Uri uri12 = Uri.parse("package:com.unexist.name");
			Intent intent12 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri12);
			myactivity.startActivity(intent12);
			// 循环等待1分钟
			startTime = System.currentTimeMillis();
			while(time<APKTIME)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
					break;
			}
			if ((currentName=apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=ERROR_PACKAGE_DELETE_FAILED_APP_NOT_FOUND) 
			{
				gui.cls_only_write_msg(TAG, "installapp200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(TAG, "installapp200", gKeepTimeErr,"line %d:deleteApp2静默卸载app测试失败（apk = %s,%d）", Tools.getLineInfo(),currentName,respCode);
				is=false;
			
			}else{
				installFlag=false;
			}
		}
	    break;

	default:
		break;
	}
		return is;
	}
    private enum InstallFuncName{installApp1,deleteApp1,installApp2,deleteApp2};
	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		apkReceiver = new ReceiverTracker().new ApkBroadCastReceiver();
		registApk();
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		unRegistApk();
		gui = null;
		apkReceiver = null;
	}
	private void registApk()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.INSTALL_APP");
		intentFilter.addAction("android.intent.action.DELETE_APP");
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
