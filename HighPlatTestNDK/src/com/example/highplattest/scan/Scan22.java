package com.example.highplattest.scan;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan27.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20191029
 * directory 		: N700获取按键扫码值
 * description 		: 
 * related document : 
 * history 		 	: 变更点			变更人员			remarks
 *			  		  
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan22 extends UnitFragment{
	private String fileName=Scan22.class.getSimpleName();
	private final String TESTITEM = "获取按键扫码值(N700)";
	private Gui gui = new Gui(myactivity, handler);
	public void scan22()
	{
		if (GlobalVariable.currentPlatform!=Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N700_A7) {
			gui.cls_show_msg1_record(fileName, TESTITEM, gScreenTime, "line %d:该案例只支持N700设备",Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(1, "ro.epay.sidekey.type获取中。。。。。");
		String lineSep = getProperty("ro.epay.sidekey.type","100");
		
		
		gui.cls_show_msg1_record(fileName, TESTITEM, gScreenTime, "获取到的type值为:%s",lineSep);
		
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
