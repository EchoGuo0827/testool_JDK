package com.example.highplattest.installapp;

import android.content.Intent;
import android.net.Uri;
import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: 验签方案测试
 * file name 		: InstallApp15.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20191017
 * directory 		: 
 * description 		: 验证卸载mtms权限应用(x5)
 * related document : 
 * history 		 	: author			date			remarks
 * 					 	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp15 extends UnitFragment {
	private final String CLASS_NAME =InstallApp15.class.getSimpleName();
	private final String TESTITEM = "验证卸载mtms权限应用(x5)";
	private  Gui gui = new Gui(myactivity, handler);
	private SettingsManager settingsManager;
	@SuppressWarnings("deprecation")
	public void installapp15()
	{
		gui.cls_show_msg("请先安装第三方应用ETest.apk与mtms权限应用mtms_test.apk按任意键开始测试。。。。" );
		gui.cls_show_msg("先打开应用ETest.apk进行卸载mtms_test.apk按任意键继续" );
		if ((gui.cls_show_msg("mtms_test.apk是否被卸载。没有被卸载按【确认】，否则按其他" ))!=ENTER) {
			gui.cls_show_msg1_record(TESTITEM, "installapp15",10,"第三方应用卸载mtms应用成功，预期应该卸载失败");
		}
		gui.cls_show_msg1(2,"正在卸载mtms_test.apk。。。。。。。" );
		Uri packageUri = Uri.parse("package:"+ "com.eric.mtms_test");
		Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
		startActivity(intent);
		if ((gui.cls_show_msg("mtms_test.apk是否被卸载。被卸载按【确认】，否则按其他" ))!=ENTER) {
			gui.cls_show_msg1_record(TESTITEM, "installapp15",10,"权限应用卸载mtms应用失败，预期应该卸载成功");
		}
		gui.cls_show_msg1_record(TESTITEM, "installapp15",5,"测试通过");
	}
	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}
}
