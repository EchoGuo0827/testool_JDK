package com.example.highplattest.installapp;

import java.io.File;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;

/************************************************************************
 * 
 * module 			: InstallApp
 * file name 		: InstallApp17.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 
 * directory 		: 外置TF卡/U盘安装apk
 * description 		: 
 * related document : 
 * history 		 	: author			    date			      remarks
 *			  		     陈丁     					20200605	 			created
 * history 		 	: 变更点											变更时间			变更人员
 * 					 增补外置TF卡U盘安装apk，要求显性静默FileProvider三种方式	    20200605		 陈丁	
 * 					测试apk后缀名修改					20201010 陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp17 extends UnitFragment  
{
	private final String TESTITEM = "安装外置TF卡/U盘的apk";
	public final String TAG = InstallApp17.class.getSimpleName();
	private ApkBroadCastReceiver apkReceiver;
	private final int APKTIME = 60; /**每个广播接收时间为1分钟*/
	private Gui gui = new Gui(myactivity, handler);
	private DiskType diskType = DiskType.SDDSK;
	private String diskString;
	private String filepath;
	
	public void installapp17(){
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("%s\n0.配置\n1.安装测试\n",TESTITEM);
			switch (returnValue) 
			{
			
			case '0':
				// 配置
			    diskString = new Config(myactivity,handler).confSDU();
			    diskType=getDiskType(diskString);
				break;
				
			case '1':
				installapp();
				break;
				
			case ESC:
				unitEnd();
				return;
				
			default:
				break;
			}
		}
	}
	
	//安装apk
	private void installapp() 
	{
//		String apkfile="";
		String currentfile="";
//		String temfilename="";
		gui.cls_show_msg("请确保已插上%s,任意键继续", diskType == DiskType.UDISK?"U盘":diskType==DiskType.SDDSK?"SD卡":"TF卡");
		
		filepath=Tools.getDesignPath(myactivity, diskType)+"/apk/";
		if(filepath.contains("null"))
		{
			gui.cls_show_msg("未获取到对应的路径，设备未挂载，任意键退出");
			return;
		}
		Log.d("eric_chen", "filepath:"+filepath);
		String[][] apkPara = 
			{
				{filepath+"QQliulanqi_sign.apk",					"com.tencent.mtt"},
				{filepath+"QQyinle_586_sign.apk",				"com.tencent.qqmusic"},
				{filepath+"A2_sign.apk",						""},
				{filepath+"baidu_map_sign.apk",					""},
			};
		
		gui.cls_show_msg("【A7以上平台请打开xml文件中的provider标签】【测试旧验签使用旧验签测试apk,测试新验签使用新验签测试apk】该用例需在控制台开启情况下测试，测试前请确保证书已更新与服务器一致并把测试apk，QQliulanqi_sign,QQyinle_586_sign,baidu_map_sign,A2_sign,放置到%s目录下，各个应用均未安装",filepath);
		//case1 显性安装QQ浏览器
		gui.cls_show_msg1(2,"case1:显性方式安装:%s路径QQ浏览器应安装成功",diskType);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Log.d("eric_chen", "apkfile:"+apkPara[0][0]);
		intent.setDataAndType(Uri.fromFile(new File(apkPara[0][0])),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		gui.cls_show_msg("正在显性安装QQ浏览器,请耐心等待安装界面->QQ浏览器应用安装完毕时点击任意键继续");
		if((currentfile=apkReceiver.getPackName(APK_INSTALL)).equals(apkPara[0][0])==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp20", gKeepTimeErr,"line %d:测试失败(pack = %s,ret = %d)", Tools.getLineInfo(),currentfile,respCode);
		}
		Uri uri = Uri.parse("package:"+apkPara[0][1]);
		Intent intent2 = new Intent(Intent.ACTION_DELETE, uri);
		myactivity.startActivity(intent2);
		gui.cls_show_msg("QQ浏览器应用卸载完毕时点击任意键继续");
		if((currentfile=apkReceiver.getPackName(APK_UNINSTALL)).equals(apkPara[0][1])==false)
		{
			gui.cls_show_msg1_record(TAG, "installapp17", gKeepTimeErr,"line %d:卸载中文app失败(pack = %s)", Tools.getLineInfo(),TESTITEM,currentfile);
		}
		
		//case2  隐性安装QQ音乐
		gui.cls_show_msg1(2,"case2:隐性方式安装:%s路径QQ音乐应安装成功",diskType);
		int time;
		long startTime;
		Intent intent3 = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent3.setDataAndType(Uri.fromFile(new File(apkPara[1][0])),"application/vnd.android.package-archive");
		myactivity.startActivity(intent3);
		time = 0;
		// 循环等待2分钟
		startTime = System.currentTimeMillis();
		gui.cls_show_msg1(1,"正在安装QQ音乐请耐心等待");
		while(time<APKTIME*2)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentfile = apkReceiver.getPackName(APK_INSTALL)).equals(apkPara[1][0]))
					break;
		}
		if((currentfile = apkReceiver.getPackName(APK_INSTALL)).equals(apkPara[1][0])==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(TAG, "installapp17", gKeepTimeErr,"line %d:测试失败(apk = %s，%d)", Tools.getLineInfo(),currentfile,respCode);
		}
		
		gui.cls_show_msg1(2, "卸载QQ音乐");
		Uri uri2 = Uri.parse("package:"+apkPara[1][1]);
		Intent intent4 = new Intent(NlIntent.ACTION_DELETE_HIDE, uri2);
		myactivity.startActivity(intent4);
		time = 0;
		// 循环等待一分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentfile = apkReceiver.getPackName(APK_UNINSTALL)).equals(apkPara[1][1]))
				break;
		}
		if ((currentfile=apkReceiver.getPackName(APK_UNINSTALL)).equals(apkPara[1][1])==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp17", gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%d)", Tools.getLineInfo(),currentfile,respCode);
		}
		
		//case3  FileProvider方式隐式安装A2应用
		gui.cls_show_msg1(5,"case3:content uri隐性方式安装:%s路径A2应用应安装成功",diskType);
		Intent intent5 = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(apkPara[2][0]);
		Log.d("eric_chen","file = "+apkPara[2][0]);
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
		{
			 Uri contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
			 LoggerUtil.v("case3 uri="+contentUri.toString());
			 intent5.setDataAndType(contentUri,"application/vnd.android.package-archive");
			 intent5.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		else
		{
			 LoggerUtil.v("case3-----");
			 intent5.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		}
		myactivity.startActivity(intent5);
		time = 0;
		// 循环等待一分钟
		startTime = System.currentTimeMillis();
		while(time<APKTIME)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentfile = apkReceiver.getPackName(APK_INSTALL)).equals(apkPara[2][0]))
				break;
		}
		if ((currentfile=apkReceiver.getPackName(APK_INSTALL)).equals(apkPara[2][0])==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(TAG, "installapp17", gKeepTimeErr,"line %d:测试失败(apk = %s,%d)", Tools.getLineInfo(),currentfile,respCode);
		}
		
		//case4 FileProvider方式显式安装百度地图
		gui.cls_show_msg1(3,"case4:content uri显性方式安装:%s路径下百度地图应安装成功",diskType);
		File file2 = new File(apkPara[3][0]);
		Intent Intent6 = new Intent();
		Intent6.setAction(Intent.ACTION_VIEW);
		Intent6.addCategory(Intent.CATEGORY_DEFAULT);
		Intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
    	{
    		LoggerUtil.d("SDK26");
    		Uri contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file2);
    		LoggerUtil.v("case1 uri="+contentUri.toString());
    		Intent6.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    		Intent6.setDataAndType(contentUri, "application/vnd.android.package-archive");	
    	}
    	else
    	{
    		Intent6.setDataAndType(Uri.fromFile(file2), "application/vnd.android.package-archive");
    	}
		myactivity.startActivity(Intent6);
		
		gui.cls_show_msg("请耐心等待百度地图安装界面，安装成功后按任意键继续");
		
		
		gui.cls_show_msg1_record(TAG, "installapp17", gScreenTime,"%s路径测试通过,请验证机器上安装了百度地图和A2应用【U盘和TF卡均测试通过才可视为通过】", diskType);	
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
			Log.d("unregistApk", "delete");
			myactivity.unregisterReceiver(apkReceiver);
		}
	}


	public DiskType getDiskType(String s){
		if (s.equals("UDISK")) {
			return DiskType.UDISK;	
		}
		if (s.equals("SDDSK")) {
			return DiskType.SDDSK;
		}
		if (s.equals("TFDSK")) {
			return DiskType.TFDSK;
		}
		return DiskType.UDISK;	
	}

	@Override
	public void onTestUp() {
		apkReceiver = new ReceiverTracker().new ApkBroadCastReceiver();
		registApk();
		
	}

	@Override
	public void onTestDown() {
		unRegistApk();
		
	}

}
