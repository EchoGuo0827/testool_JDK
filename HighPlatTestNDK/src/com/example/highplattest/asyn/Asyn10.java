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
* file name			: Asyn10.java
* Author 			: zhengxq
* version			: 
* DATE				: 20151204
* directory 		: 
* description		: 异步Modem的getVersion，获取版本号
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141203 		created
************************************************************************/
public class Asyn10 extends UnitFragment
{
	private String CLASS_NAME= Asyn10.class.getSimpleName();
	private String TESTITEM = "异步modem的getVersion";
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager  nlModemManager = null;
	
	public void asyn10() 
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		/*private & local definition*/
		String version = null;
		
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
		// case1:获取NDK版本号
		try 
		{
			version = "NDK版本号="+nlModemManager.getVersion();
		} catch (RemoteException e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过(ver = %s)",TESTITEM,version);
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
