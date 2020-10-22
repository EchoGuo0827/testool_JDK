package com.example.highplattest.installapp;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;

/************************************************************************
 * 
 * module 			: 验签方案测试
 * file name 		: InstallApp16.java 
 * Author 			: weimj
 * version 			: 
 * DATE 			: 20191219
 * directory 		: 
 * description 		: 验证农商银行安装
 * related document : 
 * history 		 	: author			date			remarks
 * 					 	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp16 extends UnitFragment{

	private final String TAG =InstallApp16.class.getSimpleName();
	private final String TESTITEM = "验证农商银行安装";
	private  Gui gui = new Gui(myactivity, handler);
	private String expPackName;/**预期的包名*/
	private String currentName;/**实际得到的包名*/
	public void installapp16() {
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp1",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/* Process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		gui.cls_show_msg("测试前请先将农商apk导入根目录并命名为LoginActivity-test1.6.7.apk，确认应用未安装");
		
		//case1:安装apk
		Intent intent = new Intent(Intent.ACTION_VIEW);
		expPackName = GlobalVariable.sdPath+"LoginActivity-test1.6.7.apk";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		
		if(gui.ShowMessageBox("应用安装完毕点击确认键".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "installapp16", gKeepTimeErr,"line %d:%s安装农商apk测试失败(apk = %s，%d)", Tools.getLineInfo(),TESTITEM,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(TAG, "installapp16", gScreenTime, "%s测试通过", TESTITEM);
		
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

	
}
