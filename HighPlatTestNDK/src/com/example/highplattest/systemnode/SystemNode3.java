package com.example.highplattest.systemnode;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: SystemConfig75.java 
 * description 		: 验证语言和时区属性(N910_A7)
 * history 		 	: 变更点										变更人员			变更时间
 * 					测试修复语言和时区未设置成功，且恢复出厂后配置的属性均未生效    	陈丁			     20200428
 * 					由原systemconfig75搬移							陈丁				20200604
 * 					开发回复目前只支持获取persist.sys.locale，且必须导入install.xml文件才可测试  陈丁 20200814
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode3 extends UnitFragment {
	private final String TESTITEM = "验证语言和时区属性(N910_A7)";
	private String fileName="SystemNode3";
	private Gui gui = null;
	String value1;
	String value2;
	String value3;
	
	public void systemnode3(){
		
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		gui.cls_show_msg("目前只支持获取地区属性，请确保固件已导入install.xml文件,按任意键继续");
		value1=getProperty("persist.sys.language","-10086");// 语言属性
		value2=getProperty("persist.sys.country","-10086");// 市区属性
		value3=getProperty("persist.sys.locale","-10086");// 地区属性
		gui.cls_show_msg1(5,"当前语言属性值为%s,时区属性值为%s,地区属性值为%s，请恢复出厂设置。恢复出厂设置后，再次进入该案例获取到的语言、时区和地区属性不应被清除",value1,value2,value3);
		
		gui.cls_show_msg1_record(fileName,"SystemNode3",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
