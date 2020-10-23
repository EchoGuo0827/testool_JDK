package com.example.highplattest.other;

import java.io.File;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other35.java 
 * description 		: N850(海外)文件路径测试
 * related document :
 * history 		 	: 变更记录							变更时间			   	变更人员
 * 					N850(海外)文件路径测试(V2.3.02)		20201021			chending		
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other35 extends UnitFragment {
	public final String TAG = Other6.class.getSimpleName();
	private final String TESTITEM = "N850(海外)posInfo文件路径测试";
	private Gui gui = new Gui(myactivity, handler);
	String path1="newland/appFsLocal/posInfo/posInfo.bin";
	String path2="newland/appFsLocal/posInfo";
	
	public void other35()
	{
		String funcName = "other35";
		gui.cls_show_msg("按任意键开始测试");
		
		File file2=new File(path2);
		if(!file2.exists()){
			gui.cls_show_msg1_record(TAG, funcName, 0,"%s路径不存在",  path2);
		}
		if (!file2.isDirectory()) {
			gui.cls_show_msg1_record(TAG, funcName, 0,"%s不是文件夹",  path2);
		}
		
		
		File file1=new File(path1);
		if(!file1.exists()){
			gui.cls_show_msg1_record(TAG, funcName, 0,"%s路径不存在",  path1);
		}
		gui.cls_show_msg1_record(TAG, funcName, 0,"测试通过");
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
