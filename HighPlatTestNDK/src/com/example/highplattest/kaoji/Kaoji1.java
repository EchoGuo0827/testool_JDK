package com.example.highplattest.kaoji;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;


public class Kaoji1 extends UnitFragment{
	
	private final String TESTITEM = "拷机相关案例";
	private Gui gui = null;
	
	// 手动测试案例
	public void kaoji1(){
		gui = new Gui(myactivity,handler);
		gui.cls_show_msg1(gScreenTime, "请到SVN上的高端/Tool/拷机测试相关目录下/测试apk/安装对应摄像头测试apk进行测试,测试说明参照SVN的《案例使用说明》");

	}

	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
