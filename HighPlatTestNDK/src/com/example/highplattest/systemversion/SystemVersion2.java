package com.example.highplattest.systemversion;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/**
* * module 				: OTA包识别新增ro.product.model识别
* * history 		 	: 变更记录								    	变更时间			变更人员
*						 OTA包识别新增ro.product.model识别，N910_A7		20200509		郑薛晴
* 
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class SystemVersion2 extends UnitFragment
{
	private final String TESTITEM = "ro.product.model属性";
	private String fileName=SystemVersion2.class.getSimpleName();
	
	public void systemversion14()
	{
		Gui gui = new Gui(myactivity, handler);
		String nodeValue = getProperty("ro.product.model", "-10086");
		gui.cls_show_msg("获取到的产品型号=%s，获取到的产品型号是否与实际的产品型号一致", nodeValue);
		
		gui.cls_show_msg1_record(fileName, "systemversion14", 2,"测试通过-------------");
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
