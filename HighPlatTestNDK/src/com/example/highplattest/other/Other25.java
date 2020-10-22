package com.example.highplattest.other;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other25.java 
 * history 		 	: 变更记录															变更时间			变更人员
 *			  	    设定data/app下不可卸载的系统应用，同时要设定data/app下的应用为覆盖系统应用的属性 						20200916  		陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other25 extends UnitFragment {
	public final String TAG = Other25.class.getSimpleName();
	private final String TESTITEM = "系统应用重启文件异常验证(CPOSX5)";
	Gui gui = new Gui(myactivity, handler);
	//test----//
	public void other25(){
		gui.cls_show_msg("本测试需要去svn/Tool/X5系统应用重启后Uid变化测试 目录安装Systestapp_debug.apk,并按照Systestapp使用方法.txt方法上操作。");
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
