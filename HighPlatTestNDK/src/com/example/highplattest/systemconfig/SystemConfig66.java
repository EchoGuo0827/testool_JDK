package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig66.java 
 * description 		: 设置底部虚拟按键(导航栏状态)
 * 
 * history 		 	: 变更记录							变更时间			变更人员
 *			  		   设置底部虚拟按键(导航栏状态)			20200108		魏美杰
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig66  extends UnitFragment {
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "设置底部虚拟按键(导航栏状态)";
	private String fileName="SystemConfig68";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig66(){
		gui=new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig49",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		boolean flag1 = false;
		boolean flag2 = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		
		/*process body*/
		// 测试前置:设置导航栏开启
		if((flag1=settingsManager.setNavigationBarOpen(true))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM,flag1);
			if(!GlobalVariable.isContinue)
				return;
		}

		//case1:设置参数为false，此时导航栏关闭
		gui.cls_show_msg1(gScreenTime, "此时虚拟按键可见，设置参数为false，按任意键底部虚拟按键(导航栏状态)关闭");
		if((flag1=settingsManager.setNavigationBarOpen(false))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr,"line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时导航栏应关闭，是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2:设置参数为true，此时导航栏开启
		gui.cls_show_msg1(gScreenTime, "设置参数为true，此时导航栏开启");
		if((flag1=settingsManager.setNavigationBarOpen(true))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时导航栏应开启，是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3:设置导航栏不自动隐藏
		gui.cls_show_msg1(gScreenTime, "设置参数为false，此时导航栏不隐藏");
		if((flag2=settingsManager.setNavigationBarAutoHide(false))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag2);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时导航栏应不隐藏，是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4:设置导航栏自动隐藏
		gui.cls_show_msg1(gScreenTime, "设置参数为true，此时导航栏自动隐藏");
		if((flag2=settingsManager.setNavigationBarAutoHide(true))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig49",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag2);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("此时导航栏应自动隐藏，是【确定】否【其他】")!=ENTER) {
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//测试后置  去掉自动隐藏  主动显示虚拟按键
		gui.cls_show_msg1(2,"测试后置。。。开启底部虚拟按键(导航栏)");
		if((flag1=settingsManager.setNavigationBarOpen(true))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s开启失败(%s)", Tools.getLineInfo(),TESTITEM,flag1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((flag2=settingsManager.setNavigationBarAutoHide(false))!=true){
			gui.cls_show_msg1_record(fileName,"systemconfig68",gKeepTimeErr, "line:%d:%s设置失败(%s)", Tools.getLineInfo(),TESTITEM,flag2);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig68",gScreenTime,"以上测试均通过则%s测试通过(长按确认键退出测试)", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
