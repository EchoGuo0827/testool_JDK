package com.example.highplattest.systemnode;

import java.io.IOException;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: SystemNode15.java 
 * Author 			: zhengjw
 * version 			: 
 * DATE 			: 20201015
 * directory 		: 
 * description 		: persist.sys.ipstat属性设置
 * related document :  
 * history 		 	: 变更点							变更人员				变更时间
 * 					     新建 ,CPOS_V1.0.43版本导入                              郑佳雯                           20201015
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class SystemNode15 extends UnitFragment {
	private final String TESTITEM = "persist.sys.ipstat属性设置";
	private String fileName="SystemNode15";
	Gui gui = new Gui(myactivity, handler);
	
	public void systemnode15 (){
		try {
			testSystemNode15();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void testSystemNode15() throws IOException
	{
		while(true)
		{
			int nkey = gui.cls_show_msg("%s\n0.单元测试\n1.重启验证测试\n2.恢复出厂设置验证测试", TESTITEM);
			switch (nkey) {
			case '0':
				unitTest();				
				break;
			case '1':				
				test_reboot();
				break;
			case '2':
				test_restore();
				break;
	
			default:
				unitEnd();
				return;
			}			
		}
	}
	
	public void unitTest()
	{		
		final String funcName = "testSystemNode15";		
		gui.cls_show_msg("请确保设备联网后按任意键继续");
		
		// case1:默认情况下persist.sys.ipstat属性为false关闭
		gui.cls_show_msg1(1, "case1:默认情况下persist.sys.ipstat属性为false关闭");
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令无法查看到51222的端口号,是[确认],否[其他]")==ENTER)
		{			
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"case1测试通过");			
		}
		else
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:case1测试失败", Tools.getLineInfo());
		
		// case2:设置persist.sys.ipstat属性为true，重启后生效
		gui.cls_show_msg1(1, "case2:设置persist.sys.ipstat属性为true，重启后生效");
		BaseFragment.setProperty("persist.sys.ipstat","true");
		if(gui.cls_show_msg("重启后可查看到51222的端口号,是否立即重启(重启后要进入1.重启验证测试),[确认键]重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
		// case3:恢复出厂设置后persist.sys.ipstat属性为false
		gui.cls_show_msg1(1, "case3:恢复出厂设置后persist.sys.ipstat属性为false");
		gui.cls_show_msg("恢复出厂设置后无法查看到51222的端口号,请手动恢复出厂设置(恢复出厂设置后要进入2.恢复出厂设置验证测试)");
		
	}
	 public void test_reboot()
	 {
		final String funcName = "重启验证";
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令可以查看到51222的端口号,是[确认],否[其他]")==ENTER)
		{			
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"重启验证通过");
			if(!GlobalVariable.isContinue)
				return;
		}
		else
			 gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:重启验证测试失败", Tools.getLineInfo());
	 }
	 public void test_restore()
	 {
		final String funcName = "恢复出厂设置验证";
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令无法查看到51222的端口号,是[确认],否[其他]")==ENTER)
		{			
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"恢复出厂设置验证通过");
			if(!GlobalVariable.isContinue)
				return;
		}
		else
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:恢复出厂设置验证测试失败", Tools.getLineInfo());
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
	