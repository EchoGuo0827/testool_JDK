package com.example.highplattest.systemconfig;

import android.provider.Settings;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: SystemConfig70.java 
 * description 		: adb安全加固测试
 * related document :  
 * history 		 	: 变更记录			变更时间			变更人员
 * 					N700海外支持		20200326		陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig70  extends UnitFragment {
	private final String TESTITEM = " adb安全加固测试";
	private Gui gui = null;
	private String fileName="SystemConfig70";

	public void systemconfig70(){
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(2,"%s测试中",TESTITEM);
		//case1 授权开启情况下 测试adb加固。 应可以正常打开关闭
		if (gui.cls_show_msg("是否要跳过开启授权状态测试adb加固,【确定】跳过 其他【继续】")!=ENTER) {
			
			gui.cls_show_msg("请先确保机器有授权且测试应用为系统权限应用。可以使用adb shell  adb logcat等命令");
			
			Settings.Global.putInt(myactivity.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
			if(gui.cls_show_msg("已将adb总阀关闭。请确认是否无法执行adb shell adb logcat adb root等命令")!=ENTER){
				gui.cls_show_msg1_record(fileName,TESTITEM,gKeepTimeErr, "line %d: 关闭adb 总阀失败",  Tools.getLineInfo(),TESTITEM);
			}
			Settings.Global.putInt(myactivity.getContentResolver(), Settings.Global.ADB_ENABLED, 1);
			if(gui.cls_show_msg("已将adb总阀开启。请确认是否可以执行adb shell adb logcat adb root等命令")!=ENTER){
				gui.cls_show_msg1_record(fileName,TESTITEM,gKeepTimeErr, "line %d: 开启adb 总阀失败",  Tools.getLineInfo(),TESTITEM);
			}
		}
		//case1 授权关闭情况下 测试adb加固。 应无法打开
		gui.cls_show_msg("请先确保机器授权关闭。。。。。");
		if (gui.cls_show_msg("是否要跳过关闭授权状态测试adb加固,【确定】跳过 其他【继续】")!=ENTER) {
			Settings.Global.putInt(myactivity.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
			if(gui.cls_show_msg("已将adb总阀关闭。请确认是否无法执行adb shell adb logcat adb root等命令")!=ENTER){
				gui.cls_show_msg1_record(fileName,TESTITEM,gKeepTimeErr, "line %d: adb安全加固失败",  Tools.getLineInfo(),TESTITEM);
			}
			Settings.Global.putInt(myactivity.getContentResolver(), Settings.Global.ADB_ENABLED, 1);
			if(gui.cls_show_msg("已将adb总阀开启。请确认是否还是无法执行adb shell adb logcat adb root等命令")!=ENTER){
				gui.cls_show_msg1_record(fileName,TESTITEM,gKeepTimeErr, "line %d: adb安全加固失败",  Tools.getLineInfo(),TESTITEM);
			}
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
