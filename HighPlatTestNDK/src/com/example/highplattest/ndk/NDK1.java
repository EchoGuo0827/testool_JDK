package com.example.highplattest.ndk;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class NDK1 extends UnitFragment
{
	static{
		System.loadLibrary("jnindk");
	}
	private String fileName="NDK1";
	private final String TESTITEM = "testtool_NDK";
	private Gui gui = new Gui(myactivity, handler);

	public void ndk1()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"ndk1",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		Tools.setNdkStatus(myactivity, true);
		gui.testMain();
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}
}
