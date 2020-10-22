package com.example.highplattest.systemnode;

import android.util.Log;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: 本固件是否支持wifi探针
 * directory 		: 
 * history 		 	: 变更记录					变更时间			变更人员
 *			  		 本固件是否支持wifi探针		20171218 		郑薛晴
 *					  从SystemVersion8搬过来	20200609		郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode11 extends UnitFragment{
	private final String TESTITEM = "固件是否支持wifi探针";
	private String fileName=SystemNode11.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);

	public void systemnode11()
	{
		String funcName="systemnode11";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		// case1：判断本固件是否支持wifi探针功能


		String result = null;
		try{
			result = BaseFragment.getProperty("sys.epay.wifiprobe", "-10086");
		}catch(Exception e)
		{
			gui.cls_show_msg("该固件版本不支持wifi探针功能，请于开发确认是否导入wifi探针功能，任意键退出测试");
			unitEnd();
			return;
		}

		if(result.equalsIgnoreCase("true"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "本固件支持wifi探针功能");
		}
		else if(result.equalsIgnoreCase("false"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "本固件不支持wifi探针功能");
		}
		else if(result.equals("-10086"))// 固件不支持该属性
		{
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "本固件不支持wifi探针属性");
		}else {
			gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "其他错误");
		}
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"%s测试通过", TESTITEM);
	}
	

	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
	
}
