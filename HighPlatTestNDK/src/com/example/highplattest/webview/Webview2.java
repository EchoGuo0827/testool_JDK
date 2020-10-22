package com.example.highplattest.webview;

import android.content.Intent;
import com.example.highplattest.activity.WebViewActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: AndroidWebview原生接口模块 
 * file name 		: webview2.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180820
 * directory 		: webview
 * description 		: 
 * related document :
 * history 		 	: 变更记录							变更时间				变更人员
 *			  		 created	   					20180820	 		zhangxinj
 *					巴西固件有校验ACCESS_WEBVIEW权限		20200714			郑薛晴
 *					 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Webview2 extends UnitFragment
{
	public final String TAG = Webview2.class.getSimpleName();
	private final String TESTITEM = "webview简单测试";
	private Gui gui = new Gui(myactivity, handler);
	public void webview2()
	{
		// 巴西需要在manifest中开启权限<uses-permission android:name="android.permission.ACCESS_WEBVIEW"/>  
		if(GlobalVariable.gCustomerID==CUSTOMER_ID.BRASIL)
		{
			if(gui.cls_show_msg("请确保AndroidManifest.xml文件中有 android.permission.ACCESS_WEBVIEW 权限,有权限[确认],无权限请添加权限后再进入案例")!=ENTER)
				return;
		}
		Intent intent = new Intent(myactivity, WebViewActivity.class);
		myactivity.startActivity(intent);
		gui.cls_show_msg1_record(TAG, "webview2",gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}

