package com.example.highplattest.webview;

import android.content.Intent;

import com.example.highplattest.activity.WebViewActivity3;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: Webview
 * file name 		: Webview4.java 
 * history 		 	: 变更点										    			 变更时间				变更人员				
 * 					  验证修复webview，键盘调用text属性，弹出的还是数字键盘(N920_A7)	  		20200522			陈丁			     
 * 					巴西固件有校验ACCESS_WEBVIEW权限									20200715			郑薛晴
 * 				
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Webview4  extends UnitFragment {
	public final String TAG = Webview4.class.getSimpleName();
	private final String TESTITEM = "验证拼音输入法键盘弹出键盘问题";
	private Gui gui;
	
	public void webview4()
	{
		// 巴西需要在manifest中开启权限<uses-permission android:name="android.permission.ACCESS_WEBVIEW"/>  
		if(GlobalVariable.gCustomerID==CUSTOMER_ID.BRASIL)
		{
			if(gui.cls_show_msg("请确保AndroidManifest.xml文件中有 android.permission.ACCESS_WEBVIEW 权限,有权限[确认],无权限请添加权限后再进入案例")!=ENTER)
				return;
		}
		gui = new Gui(myactivity, handler);
		Intent intent = new Intent(myactivity, WebViewActivity3.class);
		myactivity.startActivity(intent);
		if (gui.cls_show_msg("是否修复webview，键盘调用text属性，弹出的还是数字键盘,【确定】是  【其他】否")==ENTER) {
			gui.cls_show_msg1_record(TAG, "webview4",gScreenTime, "%s测试通过", TESTITEM);
		}else {
			gui.cls_show_msg1_record(TAG, "webview4",gScreenTime, "%s测试不通过", TESTITEM);
		}
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
