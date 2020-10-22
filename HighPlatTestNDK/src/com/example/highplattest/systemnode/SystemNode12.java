package com.example.highplattest.systemnode;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 本固件是否支持硬件加速(银商固件支持)
 * file name 		: SystemVersion9.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180119
 * directory 		: 
 * description 		: 本固件是否支持硬件加速(该功能开启会导致功耗加大，默认情况下要关闭)
 * history 		 	: author			date			remarks
 *			  		  zhengxq		    20180119 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode12 extends UnitFragment{
	private final String TESTITEM = "固件是否支持硬件加速";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=SystemNode12.class.getSimpleName();
	
	public void systemnode12()
	{
		String funcName = "systemnode12";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		String result;
		gui.cls_show_msg("如果返回的值是默认值-10086，请执行 adb shell getprop %s 确认","persist.sys.ui.hw");
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
		// case1:判断本固件是否支持硬件加速功能
		result = getProperty("persist.sys.ui.hw","-10086");
		if(result.equalsIgnoreCase("true"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"本固件【已开启】硬件加速器result=%s",result);
		}
		else if(result.equalsIgnoreCase("false"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"本固件【未开启】硬件加速器result=%s",result);
		}
		else
		{
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"本固件不支持硬件加速器,result=%s",result);
		}
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "%s测试通过", TESTITEM);
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
