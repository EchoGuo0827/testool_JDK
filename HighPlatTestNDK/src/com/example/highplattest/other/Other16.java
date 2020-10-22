package com.example.highplattest.other;

import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: SystemConfig73.java 
 * history 		 	: 变更点							变更时间			变更人员
* 					N920_A7,验证libpng在白名单内    				20200427		陈丁
 * 					N920_A7,验证libft2在白名单内					20200427		郑薛晴							
 * 					原systemconfig73搬移						20200604   		 陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other16 extends UnitFragment {
	private final String TESTITEM = "验证libpng.so和libft2.so在白名单内";
	private Gui gui = null;
	private String fileName="Other24";	
	
	public void other24(){
		gui = new Gui(myactivity, handler);
		try {
			gui.cls_show_msg1(2, "libpng.so加载中，案例不抛出异常即可");
			{
				Log.d("eric", "加载png------------");
				System.loadLibrary("png");
			}
			
			gui.cls_show_msg1(2, "libft2.so加载中，案例不抛出异常即可");
			{
				Log.d("eric", "加载png------------");
				System.loadLibrary("ft2");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1(2, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
		}
		gui.cls_show_msg1_record(fileName,TESTITEM,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
