package com.example.highplattest.other;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class Other32 extends UnitFragment
{
	private final String TESTITEM = "阿里开机广播";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Other32.class.getSimpleName();
	
	public void other32()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"other32",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/* private & local definition */
		String action;
		gui.cls_show_msg1(gKeepTimeErr, "%s测试中...", TESTITEM);
		// case1:设备开机后才能接收到阿里开机广播
		gui.cls_show_msg1(gKeepTimeErr, "接收系统开机广播BOOT_COMPLETED");
		
		if(gui.ShowMessageBox("接收阿里开机广播测试，设备重启后才能接收该广播，是否立即重启，已重启请忽略".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			Tools.reboot(myactivity);
		}
		else
		{
			// 接收重启广播的标志位
			if((action = Tools.getData(myactivity, "ACTION_ALIBABA")).equals("android.intent.action.custom.BOOT_COMPLETED")==false)
			{
				gui.cls_show_msg1(gKeepTimeErr, "line %d:重启后未接收到对应的广播（ret=%s）", Tools.getLineInfo(),action);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName,"other32",gScreenTime,"%s测试通过", TESTITEM);
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
		Tools.savaData(myactivity, "ACTION_ALIBABA", "");
	}
}
