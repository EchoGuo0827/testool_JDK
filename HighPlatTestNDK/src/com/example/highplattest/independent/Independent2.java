package com.example.highplattest.independent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: Independent2.java 
 * description 		: 移除启动target SDKO+后台服务的限制
 * related document : 
 * history 		 	: 变更记录													 变更时间			变更人员
 *			  		      移除启动target SDKO+后台服务的限制(F7_V1.0.06)		        20201021 		    郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Independent2 extends UnitFragment{
	private final String TESTITEM = "移除启动target SDKO+后台服务的限制";
	private final String FILE_NAME = Independent2.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void independent2()
	{
		gui.cls_show_msg("请到SVN目录下的Tools/安装F7Servicetest_high.apk,该APK能正常启动后台服务视为测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
