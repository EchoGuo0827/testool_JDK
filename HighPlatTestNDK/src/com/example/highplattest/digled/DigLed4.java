package com.example.highplattest.digled;

import java.io.IOException;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 
 * file name 		: DigLed4.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190517
 * directory 		: 
 * description 		: N550扫码LED测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zsh		   20190517	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class DigLed4 extends UnitFragment{
	private final String TESTITEM = "N550扫码LED测试";
	private String fileName=DigLed4.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private String mStrLEDPath;// 读取LED接口路径
	private String mStrLEDVolDev;// 读取LED接口的节点
	private Boolean ret=false;
	private String status_0="echo 0 > ";
	private String status_1="echo 1 > ";
	
	public void digled4()
	{
		if(GlobalVariable.currentPlatform==Model_Type.N550)
		{
			mStrLEDPath="/sys/class/scan_ctrl/";
			mStrLEDVolDev="/sys/class/scan_ctrl/scan_status_led";
		}else
		{
			gui.cls_show_msg1(1, "该设备不支持本用例");
			return;
		}
		
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("0.手动设置节点值\n1.单元测试");
			switch (nkeyIn) 
			{
			case '0':
				//case 0:手动测试,测试人员根据实际需求自己设定LED的状态
				int nkey = gui.cls_show_msg("0:灭,其他:亮");
				switch (nkey) {
				case '0':
					SetLed(status_0);
					gui.cls_show_msg("此时LED指示灯为灭,任意键退出,可重启获取进行其他操作查看LED灯状态是否保持");
					break;

				default:
					SetLed(status_1);
					gui.cls_show_msg("此时LED指示灯为亮,任意键退出,可重启获取进行其他操作查看LED灯状态是否保持");
					break;
				}
				break;
				
			case '1':
				unitTest();
				break;

			case ESC:
				unitEnd();
				break;
			}
		}

	}
	
	public void unitTest()
	{
		//case 1:基础流程设置,设置节点为0,1,对应指示灯为灭亮
		if(ret=(SetLed(status_1))==false)
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:设置LED亮失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		if ((gui.ShowMessageBox(("指示灯状态为,亮[确认],灭[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:指示灯显示状态错误,预期为亮", Tools.getLineInfo());
			return;
		}
		if(ret=(SetLed(status_0))==false)
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:设置LED灭失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		if ((gui.ShowMessageBox(("指示灯状态为,亮[确认],灭[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))==BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:指示灯显示状态错误,预期为灭", Tools.getLineInfo());
			return;
		}
		
		//case 2:重复设置,应设置成功,且符合预期
		if(ret=(SetLed(status_1))==false)
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:设置LED亮失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		if ((gui.ShowMessageBox(("指示灯状态为,亮[确认],灭[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:指示灯显示状态错误,预期为亮", Tools.getLineInfo());
			return;
		}
		if(ret=(SetLed(status_1))==false){
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:设置LED亮失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		if ((gui.ShowMessageBox(("指示灯状态为,亮[确认],灭[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:指示灯显示状态错误,预期为亮", Tools.getLineInfo());
			return;
		}
		if(ret=(SetLed(status_0))==false){
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:设置LED灭失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		if ((gui.ShowMessageBox(("指示灯状态为,亮[确认],灭[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))==BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:指示灯显示状态错误,预期为灭", Tools.getLineInfo());
			return;
		}
		if(ret=(SetLed(status_0))==false){
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:设置LED灭失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		if ((gui.ShowMessageBox(("指示灯状态为,亮[确认],灭[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))==BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "digled4", gKeepTimeErr, "line %d:指示灯显示状态错误,预期为灭", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(gScreenTime, "单元测试测试通过");
	}
	
	private boolean SetLed(String status){
		try {
			LinuxCmd.execCmd(mStrLEDPath, status+mStrLEDVolDev);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
