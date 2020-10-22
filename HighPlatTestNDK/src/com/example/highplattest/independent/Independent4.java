package com.example.highplattest.independent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: Independent4.java 
 * description 		: 银商验签压力测试
 * related document : 
 * history 		 	: 变更记录						变更时间			 变更人员
 *			  		      银商验签压力测试		        20201021 		    郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Independent4 extends UnitFragment{
	private final String TESTITEM = "银商验签压力测试";
	public final String FILE_NAME = Independent4.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void independent4()
	{
		gui.cls_show_msg("请到SVN的JDK/Tools目录下的安装VerifySign.apk,按照APK提示进行银商验签压力测试");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
