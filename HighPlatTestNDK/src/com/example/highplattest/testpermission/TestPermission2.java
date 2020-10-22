package com.example.highplattest.testpermission;

import java.io.File;
import java.io.IOException;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android37.java 
 * Author 			: weimj
 * version 			: 
 * DATE 			: 20200603 
 * directory 		: 
 * description 		: WRITE_SETTINGS权限检查
 * related document : 
 * history 		 	: 变更点						                                        变更时间			变更人员
 * 					N920_A7关闭动态授权，保证所有应用只要申请权限，就可以使用  20200629       	郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class TestPermission2 extends UnitFragment{
	public final String TAG = TestPermission2.class.getSimpleName();
	private String TESTITEM = "WRITE_SETTINGS权限和关闭动态授权验证";
	private Gui gui = new Gui(myactivity, handler) ;
	private static final String WRITE_PERMISSION = "android.permission.WRITE_SETTINGS"; 
	private int perm;
	
	@SuppressLint("NewApi") public void testpermission2(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// case2:关闭动态授权，保证所有应用只要申请权限，就可以使用
		gui.cls_show_msg("请确保manifest文件中android:targetSdkVersion大于23，并且没有android.permission.MANAGE_NEWLAND权限");
		// 进行创建文件的操作
		File file = new File(GlobalVariable.sdPath+"testpermission2.txt");
		try {
			file.createNewFile();
			if(file.exists())
			{
				gui.cls_show_msg1_record(TAG, "testpermission2", gKeepTimeErr, "关闭动态授权测试通过");
			}
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(TAG, "testpermission2", gKeepTimeErr, "line %d:关闭动态授权测试失败，无创建文件的权限",Tools.getLineInfo());
		}
		
		
		// case1:第三方应用申请权限操作
		if(myactivity.checkSelfPermission(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED)
		{
			gui.cls_show_msg1(1, "获取%s成功", "WRITE_SETTINGS");
		}
		else
		{
			myactivity.requestPermissions(new String[] {Manifest.permission.WRITE_SETTINGS}, 1);
			if(myactivity.checkSelfPermission(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED)
			{
				gui.cls_show_msg1(1, "获取%s成功", "WRITE_SETTINGS");
			}
			else
			{
				gui.cls_show_msg1_record(TESTITEM, "testpermission2", gKeepTimeErr, "line %d:动态获取testpermission2权限失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		gui.cls_show_msg1_record(TAG,"testpermission2",gScreenTime,"%s测试通过", TESTITEM);
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
