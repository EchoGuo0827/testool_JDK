package com.example.highplattest.sdlc;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.RemoteException;

/*************************************************************************
* module			: 同步modem模块
* file name			: Sdlc1.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141203
* directory 		: 
* description		: 同步Modem的getVersion，获取版本号
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141203 		created
************************************************************************/
public class Sdlc1 extends UnitFragment
{
	private final String CLASS_NAME = Sdlc1.class.getSimpleName();
	private String TESTITEM = "同步modem的getVersion";
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc1() 
	{
		/*private & local definition*/
		String funcName="sdlc1";
		int guiCode;
		String version;
		NlModemManager  nlModemManager;
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
		
		// case1:获取NDK版本号
		try 
		{
			version = nlModemManager.getVersion();
			guiCode = gui.ShowMessageBox(("请确认获取的NDK版本号="+version+" 是否正确").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME);
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取版本失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
		if (guiCode!=BTN_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(version = %s)", Tools.getLineInfo(),TESTITEM,version);
			if(!GlobalVariable.isContinue)
				return;
			
		}  
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过(version = %s)",TESTITEM,version);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
