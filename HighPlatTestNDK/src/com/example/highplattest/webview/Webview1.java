package com.example.highplattest.webview;

import android.content.Intent;
import com.example.highplattest.activity.WebViewActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: AndroidWebview原生接口模块 
 * file name 		: webview1.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20180820
 * directory 		: webview使用权限测试
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zsh		   20190522	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Webview1 extends UnitFragment
{
	public final String TAG = Webview1.class.getSimpleName();
	private final String TESTITEM = "webview使用权限测试(巴西)";
	private Gui gui = new Gui(myactivity, handler);
	public void webview1()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.BRASIL)
		{
			gui.cls_show_msg1(1, "非巴西固件，长按确认键退出");
			return;
		}
		//配置文件的Webview权限初始为不生效(注释掉)
		//case 1:未配置权限,Webview接口无法使用,无内容显示
		if(gui.cls_show_msg("Webview使用权限测试中..,即将跳转到Webview界面,预期应无法显示内容,首次执行[确认],跳过进入下个case[取消]")!=ESC){
			Intent intent = new Intent(myactivity, WebViewActivity.class);
			myactivity.startActivity(intent);
			if(gui.cls_show_msg("Webview界面内是否无内容显示,是[确认],否[取消]")==ESC){
				gui.cls_show_msg1_record(TAG, "webview1", gKeepTimeErr,"line %d:测试失败,未配置权限时,Webview仍可访问", Tools.getLineInfo());
				return;
			}
		}
		//case 2:配置权限,Webview接口可以使用,且可以正常访问
		if(gui.cls_show_msg("请取消AndroidManifest.xml文件中<Webview权限设置>标签下的注释使权限生效,并重新安装案例,pos连入wifi,完成上述配置后进入本case,完成[确认],否[取消]")==ESC){
			gui.cls_show_msg1_record(TAG, "webview1", gKeepTimeErr,"line %d:请先完成前置配置", Tools.getLineInfo());
			return;
		}else{
			gui.cls_show_msg("即将跳转到Webview界面,预期有内容显示,且点击第一个链接可以正常访问,任意键跳转..");
			Intent intent = new Intent(myactivity, WebViewActivity.class);
			myactivity.startActivity(intent);
			if(gui.cls_show_msg("Webview界面内是否有内容显示,且第一个链接可以正常访问,是[确认],否[取消]")==ESC){
				gui.cls_show_msg1_record(TAG, "webview1", gKeepTimeErr,"line %d:测试失败,配置权限后,Webview无法正常访问", Tools.getLineInfo());
				return;
			}
		}
		//测试后置,恢复权限的注释.
		gui.cls_show_msg1_record(TAG, "webview1",gScreenTime, "%s测试通过,请恢复AndroidManifest.xml文件中<Webview权限设置>标签下的注释后进行其他测试", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
