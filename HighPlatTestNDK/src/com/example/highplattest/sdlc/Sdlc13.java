package com.example.highplattest.sdlc;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
/*************************************************************************
* module			: 同步modem模块
* file name			: Sdlc13.java
* Author 			: huangjianb
* version			: 
* DATE				: 20141215
* directory 		: 
* description		: 同步Modem的powerCtrl函数测试
* related document	: 
* history 		 	: author			date			remarks
*			  		 huangjianb		   20141215 		created
************************************************************************/
public class Sdlc13 extends UnitFragment
{
	private final String CLASS_NAME = Sdlc13.class.getSimpleName();
	private NlModemManager  nlModemManager;
	private String TESTITEM = "同步modem的powerCtrl函数";
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc13() 
	{
		/*private & local definition*/
		String funcName="sdlc13";
		int ret = -1;
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		
		//case1:下电操作
		if((ret = modem_powerCtrl(nlModemManager, 0)) != 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//读取当前状态应该为下电状态
		if((ret = modem_powerCtrl(nlModemManager, -1)) != 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2:上电操作
		if((ret = modem_powerCtrl(nlModemManager, 1)) != 1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//读取当前状态应该为上的状态
		if((ret = modem_powerCtrl(nlModemManager, 2)) != 1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//测试结束,对MODEM进行下电操作
		if((ret = modem_powerCtrl(nlModemManager, 0)) != 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,TESTITEM+"测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		nlModemManager = null;
		gui = null;
	}
}
