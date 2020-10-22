package com.example.highplattest.systemversion;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;

/**
* * module 				: 触摸屏、液晶型号信息获取(X1)
* * history 		 	: 变更记录								    	变更时间			变更人员
*						 X1触摸屏、液晶型号信息的获取							20200513		陈丁
* 
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class SystemVersion6 extends UnitFragment {
	private final String TESTITEM = "触摸屏、液晶型号信息获取(X1)";
	private String fileName=SystemVersion6.class.getSimpleName();
	private Gui gui ;
	
	//X1不支持摄像头信息获取
	//开发建议 主屏和副屏信息读到什么就显示什么。无需转换
	String path1 ="/sys/class/panel_info/panel_main_factory"; //主屏厂家和IC
	String path2 ="/sys/class/panel_info/panel_custom_factory"; //副屏厂家和IC
	String path3 ="/sys/class/touchscreen/sensor_info"; //触摸屏厂家及sensor id接口节点解析路径
	
	public void systemversion15()
	{
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		//case1 获取触摸屏信息
		String sensor_info = LinuxCmd.readDevNode(path3);
		if(gui.ShowMessageBox(("请确认触摸屏厂家及sensorID：" + sensor_info).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion15", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,sensor_info);
		
		}
		//case2 主屏信息
		String panel_info = LinuxCmd.readDevNode(path1);
		if(gui.ShowMessageBox(("请确认主屏信息：" + panel_info).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion15", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,panel_info);
		
		}
		
		//case3 副屏信息
		String panel_info2 = LinuxCmd.readDevNode(path2);
		if(gui.ShowMessageBox(("请确认副屏信息：" + panel_info2).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion15", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,panel_info2);
		
		}
		
		gui.cls_show_msg1_record(fileName, "systemversion15", gScreenTime,"以上获取都正确则%s测试通过", TESTITEM);
		
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
