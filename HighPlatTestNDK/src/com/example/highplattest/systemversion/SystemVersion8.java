package com.example.highplattest.systemversion;

import android.newland.os.NlBuild;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 系统版本
 * file name 		: SystemVersion10.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180508
 * directory 		: 
 * description 		: 获取硬件配置码，此种方式提供给NDK调用
 * history 		 	: author			date			remarks
 *			  		  zhengxq		    20180508 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemVersion8 extends UnitFragment
{
	private final String TESTITEM = "获取硬件配置码";
	private String fileName=SystemVersion8.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void systemversion8()
	{
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		// case1:通过反射方式获取硬件识别码与Java方式获取的硬件识别码应一致
//		String hardConfig1 = getSystemProperty("ro.epay.hardwareconfig");
		String hardConfig1=getProperty("ro.epay.hardwareconfig","-10086");
		String hardConfig2 = NlBuild.VERSION.NL_HARDWARE_CONFIG;
		if(hardConfig1.equals(hardConfig2)==false)
		{
			gui.cls_show_msg1_record(fileName, "systemversion10", gKeepTimeErr, "line %d:%s测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,hardConfig1,hardConfig2);
			return;
		}
		if(gui.cls_show_msg("获取到的硬件配置码:%s,与设备的硬件配置码是否一致\n是[确认],否[取消]",hardConfig1)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "systemversion10", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,hardConfig1);
			return;
		}
		gui.cls_show_msg1_record(fileName, "systemversion10", gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
