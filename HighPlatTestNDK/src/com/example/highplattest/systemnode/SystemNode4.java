package com.example.highplattest.systemnode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: SystemNode
 * file name 		: SystemNode4.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200515
 * directory 		: 
 * description 		: 串口文件权限验证(X5)
 * related document :  
 * history 		 	: 变更点										     变更人员				变更时间
 * 					  验证第三方应用是否有权限访问特定的串口文件  					陈丁			     20200515
 * 					修改提示语                                                                                                                            陈丁					20200521
 * 					由原systemconfig77搬移								陈丁					20200604
 * 				
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode4 extends UnitFragment {
	private final String TESTITEM = "串口文件权限验证(X5)";
	private String fileName="SystemConfig77";
	private Gui gui = null;
	String path1="/dev/ttyACM0";
	String path2="/dev/ttyHSL3";
	byte[] readtest=new byte[5];
	FileInputStream  fileinput;
	public void systemnode4(){
		
		String funcName="systemnode4";
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		gui.cls_show_msg("验证可以打开/dev/ttyACM0。请先确保案例Androidmainfest.xml无新大陆权限，测试应用为第三方应用，机器打开辅助功能-USBHOST，并且已插入外接密码键盘sp100。按任意键继续");
		//case1:验证第三方应用可以打开/dev/ttyACM0
		File file1=new File(path1);
		if (!file1.exists()) {
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s文件不存在", Tools.getLineInfo(),path1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (!file1.canRead()) {
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s文件不可读", Tools.getLineInfo(),path1);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		try {
//			 StringBuilder stringBuilder = new StringBuilder();
			  fileinput=new FileInputStream(file1);
			  gui.cls_show_msg1(3, "%s文件打开正常，case1测试通过", path1);
		} catch (FileNotFoundException e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:文件异常--",Tools.getLineInfo());
			e.printStackTrace();
		} catch (IOException e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:打开异常--",Tools.getLineInfo());
			e.printStackTrace();
		}
		finally{
			if (fileinput !=null) {
				try {
					fileinput.close();
				} catch (IOException e) {
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"关闭异常--");
					e.printStackTrace();
				}
			}
		}	
		
		//case2 验证第三方应用无法打开/dev/ttyHSL3
		gui.cls_show_msg("验证无法打开/dev/ttyHSL3。请先确保案例Androidmainfest.xml无新大陆权限，测试应用为第三方应用。按任意键继续");
		File file2=new File(path2);
		if (!file2.exists()) {
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s文件不存在", Tools.getLineInfo(),path2);
		}
		if (file2.canRead()) {
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s文件可读", Tools.getLineInfo(),path2);
		}

		try {
//			 StringBuilder stringBuilder = new StringBuilder();
			  fileinput=new FileInputStream(file2);
		}  catch (IOException e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"%s文件打开异常(正常现象)，case2测试通过",path2);
			e.printStackTrace();
		}finally{
			if (fileinput !=null) {
				
				try {
					fileinput.close();
				} catch (IOException e) {
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:关闭异常--");
					e.printStackTrace();
				}
			}
			
		}
		
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime,"%s测试通过(长按确认键退出测试)",TESTITEM);
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
