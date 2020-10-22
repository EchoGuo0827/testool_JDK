package com.example.highplattest.installapp;

import java.io.File;
import android.content.Intent;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
/************************************************************************
 * 
 * module 			: Android原生安装apk测试
 * file name 		: InstallApp12.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20181214
 * directory 		: 
 * description 		: 兼容不同平台安装apk
 * related document : 
 * history 		 	: author									date			remarks
 * 					 zhengxq	   								20181214 		created	
 * 					 验证FileProvider静默安装奔溃问题F7 V1.0.06导入        	20200417		chending
 * 					修改应用后缀名
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp12 extends UnitFragment
{
	private final String CLASS_NAME =InstallApp12.class.getSimpleName();
	private final String TESTITEM = "FileProvider方式显性和隐性测试";
	private Gui gui = new Gui(myactivity, handler);
	
    public void installapp12()
    {
    	gui.cls_show_msg("【测试旧验签使用旧验签测试apk,测试新验签使用新验签测试apk】请先把QQ音乐和QQ浏览器放置到/sdcard/apk/目录下,确保设备未安装QQ音乐和QQ浏览器,并将mainfest.xml文件中的FileProvider权限打开");
    	
    	String apkPath = GlobalVariable.sdPath+"apk/QQyinle_586_sign.apk";
    	File apkFile = new File(apkPath);
    	boolean isSucc = apkFile.getParentFile().mkdirs();
    	LoggerUtil.d("InstallApp12:"+isSucc);
    	
    	// case2:FileProvider方式隐性安装，QQ浏览器，新大陆自定义的隐性方式
    	gui.cls_show_msg1(2, "FileProvider隐性方式->安装QQ浏览器");
    	Intent hideIntent = new Intent(NlIntent.ACTION_VIEW_HIDE);
    	hideIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	
		File file = new File(GlobalVariable.sdPath+"apk/QQliulanqi_sign.apk");
		LoggerUtil.d("file = "+GlobalVariable.sdPath+"apk/QQliulanqi_sign.apk");
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
		{
			 Uri contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
			 LoggerUtil.v("case1 uri="+contentUri.toString());
			 hideIntent.setDataAndType(contentUri,"application/vnd.android.package-archive");
			 hideIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		else
		{
			hideIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		}
		myactivity.startActivity(hideIntent);
		
		
		gui.cls_show_msg("第一次测试->2min以内设备桌面上应该出现QQ浏览器图标,等待桌上上出现QQ浏览器图标点击任意键继续");
		
    	// case1:FileProvider方式显性测试，百度APP
		gui.cls_show_msg1(2, "FileProvider显性方式->安装QQ音乐");
    	Intent dominantIntent = new Intent();
    	dominantIntent.setAction(Intent.ACTION_VIEW);
    	dominantIntent.addCategory(Intent.CATEGORY_DEFAULT);
    	dominantIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
    	{
    		LoggerUtil.d("SDK26");
    		Uri contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", apkFile);
    		LoggerUtil.v("case1 uri="+contentUri.toString());
    		dominantIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    		dominantIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");	
    	}
    	else
    	{
    		dominantIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
    	}
		myactivity.startActivity(dominantIntent);
		
		
		// case3:第二次重复安装相同的应用
		gui.cls_show_msg("第二次测试->等待第一次的QQ音乐安装完任意键继续");
		myactivity.startActivity(hideIntent);
		
		gui.cls_show_msg("第二次测试->隐性安装QQ浏览器,等待1min后点击确认");
		
		myactivity.startActivity(dominantIntent);
		
    	gui.cls_printf("测试完毕后请手动卸载QQ音乐和QQ浏览器成功才可视为测试通过(需测试Android7.0平台以上和以下)".getBytes());
    }
    

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
