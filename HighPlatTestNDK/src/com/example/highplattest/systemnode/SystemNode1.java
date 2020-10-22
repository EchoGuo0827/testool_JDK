package com.example.highplattest.systemnode;


import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: SystemNode1.java 
 * description 		: fuse标识获取
 * history 		 	: 变更点						变更时间			变更人员
 * 					 poynt支持通过属性获取fuse标识      	20200603			陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode1 extends UnitFragment 
{
	private final String TESTITEM = "fuse标识获取";
	private Gui gui = null;
	private String fileName="SystemNode1";
	String Attribute="ro.boot.fuse";
	String DEFAUL_ERR="-10000";
	String Value;
	
	public void systemnode1()
	{
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(2,"%s测试中",TESTITEM);
		
		//case1:应能正常获取到fuse标识    1烧写  0未烧写
		Value=getProperty(Attribute,DEFAUL_ERR);
		if (Value.equals("1")) {
			gui.cls_show_msg1_record(fileName, "SystemNode1", gKeepTimeErr, "烧写了fuse");
		}else if (Value.equals("0")) {
			gui.cls_show_msg1_record(fileName, "SystemNode1", gKeepTimeErr, "未烧写fuse");
		}else {
			gui.cls_show_msg1_record(fileName, "SystemNode1", gKeepTimeErr, "未获取到fuse标识");
		}
	}
	
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
}
