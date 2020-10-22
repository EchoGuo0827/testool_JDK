package com.example.highplattest.psbc;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.psbc.getImei.GetImei;
/************************************************************************
 * 
 * module 			: 邮储固件专用
 * file name 		: Psbc1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180504
 * directory 		: 
 * description 		: 邮储固件--获取IMEI号
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20180504		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Psbc1 extends UnitFragment
{
	/*private & local definition*/
	private final String CLASS_NAME = Psbc1.class.getSimpleName();
	private final String TESTITEM = "获取IMEI号";
	private Gui gui = new Gui(myactivity, handler);
	
	public void psbc1()
	{
		String funcName="psbc1";
		// case1:未插入SIM卡获取IMEI号，获取的是设备号
		if(gui.cls_show_msg("请确保未插入Sim卡,[确认]测试本case,[其他]调过本case")==ENTER)
		{
			String IMEI =GetImei.getImei(myactivity);
			if(gui.cls_show_msg("获取的IMEI号:%s,请于设备里的IMEI号对比\n一致[确认],不一致[取消]",IMEI)!=ENTER)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,IMEI);
				return;
			}
		}

		// case2:插入SIM卡获取IMEI号，获取的是IMEI号
		if(gui.cls_show_msg("请确保已插入Sim卡,[确认]测试本case,[其他]调过本case")==ENTER)
		{
			String IMEI =GetImei.getImei(myactivity);
			if(gui.cls_show_msg("获取的IMEI号:%s,请于设备里的IMEI号对比\n一致[确认],不一致[取消]",IMEI)!=ENTER)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,IMEI);
				return;
			}
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
