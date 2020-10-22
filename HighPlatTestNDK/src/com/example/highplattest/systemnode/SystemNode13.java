package com.example.highplattest.systemnode;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: SystemNode
 * file name 		: SystemNode13.java 
 * directory 		: 
 * description 		: Emv优化-蜂鸣器响测试
 * history 		 	: 变更记录								变更时间					变更人员
 *			  		  添加硬件蜂鸣器的添加，(欧洲导入)		    	20200805				陈丁 		
 * 					 N910_A7(V1.0.05版本导入)				20200908				郑薛晴		
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode13 extends UnitFragment {
	
	private final String TESTITEM = "Emv优化-蜂鸣器响测试";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=SystemNode13.class.getSimpleName();
	private String beepnode="/sys/class/paymodule_k21/beep";
	private int  gScreenTime_0=0;
	
	public void systemnode13()
	{
		
		gui.cls_show_msg("按任意键继续，听到蜂鸣器响则测试通过。(后续升降级固件过程中，不应出现蜂鸣器响)");
		if (BaseFragment.setNodeFile(beepnode, "200")!=0) {
			gui.cls_show_msg1_record(fileName, "systemnode13", gScreenTime_0, "line %d:蜂鸣器节点写入失败",Tools.getLineInfo());
		}else {
			gui.cls_show_msg1_record(fileName,"SystemNode13",gScreenTime_0, "%s测试通过(长按确认键退出测试)", TESTITEM);
		}
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
