package com.example.highplattest.android;

import java.util.Locale;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.LocaleList;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android18.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180821
 * directory 		: Android7.0 多语言支持
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhangxinj		   20180821	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android18 extends UnitFragment
{
	public final String TAG = Android18.class.getSimpleName();
	private final String TESTITEM = "多语言支持(A7)";
	Gui gui = new Gui(myactivity, handler);
	
	public void android18()
	{
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N)
		{
			try {
				testAndroid18();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android18", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
		}
		else
		{
			gui.cls_show_msg1_record(TAG, "android18", gKeepTimeErr, "SDK版本低于24，不支持该案例");
		}
	}
	
	
	@TargetApi(24)
	public void testAndroid18()
	{
		//获取系统为 App 调整后的默认语言：
		Locale locale = Locale.getDefault();
		LoggerUtil.e(locale.getLanguage() + "-" + locale.getCountry());
		if(gui.cls_show_msg("获取当前的手机系统语言为:%s,否[取消],是[其他]",locale.getLanguage() + "-" + locale.getCountry())==ESC)
		{
			gui.cls_show_msg1_record(TAG, "android18", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置在语言与输入法中另外添加3种语言，[取消]退出")==ESC){
			return;
		}
		//
		LocaleList localeList = LocaleList.getDefault();
	
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < localeList.size(); i++) {
			s.append(localeList.get(i).getLanguage() + "-"+ localeList.get(i).getCountry() + "\n");
		}
		if (gui.cls_show_msg("获取当前的手机系统语言顺序为:%s否[取消],是[其他]",s.toString()) == ESC) {
			gui.cls_show_msg1_record(TAG, "android18", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//
		if(gui.cls_show_msg("请进入设置在语言与输入法中打乱已经添加的3种语言的顺序，[取消]退出")==ESC){
			return;
		}
		localeList = LocaleList.getDefault();
		
		s = new StringBuffer();
		for (int i = 0; i < localeList.size(); i++) {
			s.append(localeList.get(i).getLanguage() + "-"+ localeList.get(i).getCountry() + "\n");
		}
		if (gui.cls_show_msg("获取当前的手机系统语言顺序为:%s否[取消],是[其他]",s.toString()) == ESC) {
			gui.cls_show_msg1_record(TAG, "android18", gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(TAG, "android18",gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
