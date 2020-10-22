package com.example.highplattest.independent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * file name 		: Independent2.java 
 * description 		: MobileAbility.apk（网络性能测试APK）
 * related document : 
 * history 		 	: 变更记录													变更时间			变更人员
 *			  		  MobileAbility.apk（网络性能测试APK）		        		20201021 		    郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Independent3 extends UnitFragment{
	private final String TESTITEM = "网络性能耗时测试";
	public final String FILE_NAME = Independent3.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void independent3()
	{
		gui.cls_show_msg("请到SVN的JDK/Tools目录下安装MobileAbility.apk,分别使用WIFI网络,无线的联通、移动、电信卡测试开机网络耗时");
	}

	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
