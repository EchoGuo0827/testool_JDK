package com.example.highplattest.other;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: Other28.java 
 * description 		: 指纹识别测试工具的使用
 * related document : 
 * history 		 	: 变更记录							变更时间			变更人员
 *			  		       新增指纹识别测试工具的使用		      20201009 		    陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other28 extends UnitFragment {
	private final String TESTITEM = "指纹识别测试";
	public final String TAG = Other28.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void other28()
	{
		gui.cls_show_msg("请到SVN目录下的Tools/指纹Demo测试/测试apk文件夹中提取BIOTest_Debug.apk，安装后进行指纹识别测试");
		
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
