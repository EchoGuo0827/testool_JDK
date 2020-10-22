package com.example.highplattest.other;

import java.util.HashMap;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 系统相关
 * file name 		: Sys2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170331
 * directory 		: 
 * description 		: 接收系统开机广播
 * related document :
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   	20170331	 		created
 * 					修改提示语 				20200709		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other31 extends UnitFragment
{
	private final String TESTITEM = "接收系统开机广播";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Other31.class.getSimpleName();
	HashMap<String, String> keyMap= new HashMap<String,String>();
	
	public void other31()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"other31",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		String action;
		/* private & local definition */
		while(true)
		{
			int nKey = gui.cls_show_msg("开机广播测试\n1.接收原生系统开机广播\n2.接收阿里定制开机广播\n");
			switch (nKey) {
			case '1':// 系统
				if(gui.ShowMessageBox("接收系统开机广播测试，设备重启后才能接收该广播，是否立即重启，已重启请忽略(若未报错，显示测试通过，则说明正常接收到重启广播)".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
					Tools.reboot(myactivity);
				else
				{
					if((action = Tools.getData(myactivity, "ACTION_System_BOOT")).equals("android.intent.action.BOOT_COMPLETED")==false)
					{
						gui.cls_show_msg1(0, "line %d:重启后未接收到对应的广播(ret=%s)", Tools.getLineInfo(),action);
						if(!GlobalVariable.isContinue)
							return;
					}
					gui.cls_show_msg1_record(fileName,"other31",gScreenTime,"%s测试通过", TESTITEM);
				}
				break;
				
			case '2':// 阿里
				if(gui.ShowMessageBox("接收系统开机广播测试，设备重启后才能接收该广播，是否立即重启，已重启请忽略".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
					Tools.reboot(myactivity);
				else
				{
					// 接收重启广播的标志位
					if((action = Tools.getData(myactivity, "ACTION_COMMING_NOISY")).equals("android.media.AUDIO_BECOMING_NOISY")==false)
					{
						gui.cls_show_msg1(0, "line %d:重启后未接收到对应的广播(ret=%s)", Tools.getLineInfo(),action);
						if(!GlobalVariable.isContinue)
							return;
					}
					gui.cls_show_msg1_record(fileName,"other31",gScreenTime,"%s测试通过", TESTITEM);
				}
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	}

	@Override
	public void onTestUp() 
	{
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
		// 测试后置，清空SharedPreferences中的数据
		Tools.savaData(myactivity, "ACTION_COMMING_NOISY", "");
	}
}
