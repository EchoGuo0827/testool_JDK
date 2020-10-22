package com.example.highplattest.other;

import android.content.Intent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Sys
 * file name 		: Sys9.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200604
 * directory 		: 
 * description 		: X1应用权限验证
 * related document :  
 * history 		 	: 变更点																	变更时间			变更人员
 * 					 X1允许第三方应用使用关机权限，允许系统接收mtms发送的恢复出厂设置广播 (由原Systemconfig71搬移)     20200604		 陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other34 extends UnitFragment {
	private final String TESTITEM = " X1应用权限验证";
	private Gui gui = null;
	private String fileName="Sys9";
	String MTMS="android.intent.action.FACTORY_RESET";
	
	
	public void other34()
	{
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(2,"%s测试中",TESTITEM);
		
		//case1 验证第三方应用可以关机权限
		if (gui.cls_show_msg("是否要测试X1第三方应用的关机权限(请先确保案例mainfest.xml文件无MANAGE_NEWLAND权限),【确定】继续  【其他】跳过")==ENTER) {
			
			gui.cls_show_msg("按任意键关机。。。若未关机则不通过。关机则正常通过");
			
			Tools.reboot(myactivity);

		}
		
		//case2  验证mtms应用发送恢复出厂设置广播。系统可以接收
		if (gui.cls_show_msg("是否要测试X1系统可以接收到mtms应用发送的恢复出厂设置广播(请先确保案例mainfest.xml文件有MANAGE_NEWLAND权限以及mtms权限),【确定】继续  【其他】跳过")==ENTER) {
			
			gui.cls_show_msg("按任意键发送恢复出厂设置广播。。。若未恢复出厂设置则不通过。恢复出厂设置则正常通过");
			
			 Intent resetIntent=new Intent(MTMS);
			 resetIntent.setPackage("android");
			 myactivity.sendBroadcast(resetIntent);
			 gui.cls_show_msg1(1,"已发送广播----");
		}
		
		
		gui.cls_show_msg1_record(fileName,TESTITEM,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {
		
	}

}
