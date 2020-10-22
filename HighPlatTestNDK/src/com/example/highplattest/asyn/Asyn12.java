package com.example.highplattest.asyn;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.RemoteException;

/*************************************************************************
* module			: 异步modem模块
* file name			: Asyn12.java
* Author 			: zhengxq
* version			: 
* DATE				: 20151204
* directory 		: 
* description		: 异步Modem的powerCtrl函数测试
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20151204 		created
************************************************************************/
public class Asyn12 extends UnitFragment
{
	private final String CLASS_NAME=Asyn12.class.getSimpleName();
	private NlModemManager  nlModemManager;
	private String TESTITEM = "异步modem的powerCtrl函数";
	private Gui gui = new Gui(myactivity, handler);
	
	public void asyn12() 
	{
		String funcName="aysn12";
		/*private & local definition*/
		int ret = -1;
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		
		//case1:下电操作
		if(gui.cls_show_msg1(2, "下点操作,[取消]退出测试")==ESC)
			return;
		try {
			if((ret = nlModemManager.powerCtrl(0)) != 0)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem下电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
		//读取当前状态应该为下电状态
		try {
			if((ret = nlModemManager.powerCtrl(-1)) != 0)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem下电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		//case2:上电操作
		if(gui.cls_show_msg1(2, "上电操作,[取消]退出测试")==ESC)
			return;
		try {
			if((ret = nlModemManager.powerCtrl(1)) != 1)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem上电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
		//读取当前状态应该为上的状态
		try {
			if((ret = nlModemManager.powerCtrl(2)) != 1)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem上电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		//测试结束,对MODEM进行下电操作
		try {
			if((ret = nlModemManager.powerCtrl(0)) != 0)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem下电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(nlModemManager!=null)
			modem_hangup(nlModemManager);
		gui = null;
		nlModemManager = null;
	}
}
