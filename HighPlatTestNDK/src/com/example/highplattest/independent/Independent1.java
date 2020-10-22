package com.example.highplattest.independent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: Independent1.java 
 * description 		: EMV优化测试材料包提示
 * related document : 
 * history 		 	: 变更记录													变更时间			变更人员
 *			  		      新增EMV优化测试材料包提示(N910_欧洲和N910_A7导入)		        20201009 		    陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Independent1 extends UnitFragment {
	private final String TESTITEM = "EMV优化测试材料包";
	public final String FILE_NAME = Independent1.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void independent1()
	{
		gui.cls_show_msg("请到SVN的JDK/Tools/EMV优化测试材料包,文件夹中提取相关测试材料，并按照测试说明进行EMV优化相关测试");
		
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
