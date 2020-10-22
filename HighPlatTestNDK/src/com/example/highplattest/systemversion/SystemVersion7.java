package com.example.highplattest.systemversion;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 液晶型号、触摸屏厂家信息、前后置摄像头节点信息获取
 * file name 		: SystemVersion7.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20171102
 * directory 		: 
 * description 		: 液晶型号、触摸屏厂家及sensor id、前后置摄像头节点信息获取
 * history 		 	: author			date			remarks
 *			  		  xuess		   		20171102 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemVersion7 extends UnitFragment
{
	private final String TESTITEM = "液晶型号、触摸屏厂家信息、前后置摄像头节点信息获取";
	private String fileName=SystemVersion7.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);

	public void systemversion7()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		/*private & local definition*/
		String path = "/sys/class/graphics/fb0/msm_fb_panel_info";	//液晶型号接口节点解析路径
		String path2 = "/sys/class/touchscreen/sensor_info";		//触摸屏厂家及sensor id接口节点解析路径
		String path3 = "/sys/class/front_camera/camera_name";		//前置摄像头接口节点解析路径
		String path4 = "/sys/class/back_camera/camera0_name";		//后置摄像头接口节点解析路径
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		
		// case1:液晶型号接口节点
		String panel_info = LinuxCmd.readDevNode(path);
		String panel_name = readField(panel_info,"panel_name");
		if(gui.ShowMessageBox(("请确认液晶型号：" + panel_name).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,panel_name);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:触摸屏厂家及sensor id接口节点
		String sensor_info = LinuxCmd.readDevNode(path2);
		if(gui.ShowMessageBox(("请确认触摸屏厂家及sensorID：" + sensor_info).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,sensor_info);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1:前置摄像头接口节点、
		String temp = LinuxCmd.readDevNode(path3);
		String front_camera = temp.equals("") ? "未接前置摄像头" : temp;
		if(gui.ShowMessageBox(("请确认前置摄像头型号：" + front_camera).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,front_camera);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3.2:打开前置摄像头后获取接口节点
		gui.cls_show_msg("请确保已开启前置摄像头，完成任意键继续");
		temp = LinuxCmd.readDevNode(path3);
		front_camera = temp.equals("") ? "未接前置摄像头" : temp;
		if(gui.ShowMessageBox(("请确认前置摄像头型号：" + front_camera).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,front_camera);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4.1:后置摄像头接口节点
		temp = LinuxCmd.readDevNode(path4);
		String back_camera = temp.equals("") ? "未接后置摄像头" : temp;
		if(gui.ShowMessageBox(("请确认后置摄像头型号：" + back_camera).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,back_camera);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.2:打开后置摄像头获取接口节点
		gui.cls_show_msg("请确保已开启后置摄像头，完成任意键继续");
		temp = LinuxCmd.readDevNode(path4);
		back_camera = temp.equals("") ? "未接后置摄像头" : temp;
		if(gui.ShowMessageBox(("请确认后置摄像头型号：" + back_camera).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gKeepTimeErr,"line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,back_camera);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "systemversion7", gScreenTime,"液晶型号="+panel_name);
			gui.cls_show_msg1_record(fileName, "systemversion7", gScreenTime, "触摸屏厂家及sensor ID="+sensor_info);
			gui.cls_show_msg1_record(fileName, "systemversion7", gScreenTime, "前置摄像头型号="+front_camera);
			gui.cls_show_msg1_record(fileName, "systemversion7", gScreenTime,"后置摄像头型号="+back_camera);
		}
		gui.cls_show_msg1_record(fileName, "systemversion7", gScreenTime,"以上获取都正确则%s测试通过", TESTITEM);
				
	}
	
	//读取字段内容
	public  String readField(String source, String fieldname){
		String result = null;
		int index_start = source.indexOf(fieldname);	//查找需要的字段第一次出现的index
		if(index_start == -1) //没找到
			return result;
		String str = source.substring(index_start);
		int index_end = str.indexOf("\r\n");		//查找本行结束的位置
		if(index_end == -1) //没找到
			return result;
		result = str.substring(0,index_end);
		return result;
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
	}

}
