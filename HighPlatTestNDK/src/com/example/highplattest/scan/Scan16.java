package com.example.highplattest.scan;

import android.annotation.SuppressLint;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan16.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160920
 * directory 		: 获取自动识别解码库版本
 * description 		: getNlsVersion()，要先烧fuse
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20160920    	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan16 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil)getNlsVersion()";
	private String fileName=Scan16.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void scan16()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		/*private & local definition*/
		gui.cls_show_msg1(1, TESTITEM+"测试中...");
		initScanMode(Scan_Mode.NLS_1, myactivity, null, 0, true, 10*1000);
		String version = scanNlsVersion();
		
		/*process body*/
		if(gui.ShowMessageBox(("NLS解码库版本为:"+version).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan16", gKeepTimeErr, "line %d:获取解码库版本失败(version=%s)", Tools.getLineInfo(),version);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "scan16", gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
		gui = null;
		
	}
}
