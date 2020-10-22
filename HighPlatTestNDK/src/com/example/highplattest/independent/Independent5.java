package com.example.highplattest.independent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: Independent5.java 
 * description 		: WIFI认证服务
 * related document : 
 * history 		 	: 变更记录						变更时间			变更人员
 *			  		     开启WIFI认证服务		        20201021 		    郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Independent5 extends UnitFragment{
	private final String TESTITEM = "WIFI认证服务";
	public final String FILE_NAME = Independent5.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void independent5()
	{
		gui.cls_show_msg("请到SVN的JDK/Tools目录下安装WifiCertificate.apk,运行APK提示WIFI认证服务开启成功视为测试通过");
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
