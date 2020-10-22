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
* file name			: Sdlc12.java
* Author 			: huangjianb
* version			: 
* DATE				: 20141215
* directory 		: 
* description		: 同步Modem的propGet和propSet函数测试
* related document	: 
* history 		 	: author			date			remarks
*			  		 huangjianb		   20141215 		created
************************************************************************/
public class Sdlc12 extends UnitFragment
{
	private final String CLASS_NAME = Sdlc12.class.getSimpleName();
	private NlModemManager  nlModemManager;
	private String TESTITEM = "同步modem的propGet和propSet函数";
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc12() 
	{
		
		/*private & local definition*/
		String funcName="sdlc12";
		int ret = -1;
		String tConfname[] = {"line_volt","choose_country","modem_volt","modem_voice","frame_s7",
				"frame_s10","frame_rst","frame_data","baud_freq","ccitt_bell","modem_dtmf","modem_voice_time"};
		int[] iValue = {1,1,10,1,50,100,8,18,1,1,100,2};
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		
		//设置并获取modem参数的值
		for(int i=0; i<12; i++)
		{
			try {
				if((ret = nlModemManager.propSet(tConfname[i],iValue[i])) != NDK_OK)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem配置参数设置失败(%d,%s)", Tools.getLineInfo(),TESTITEM,ret,tConfname[i]);
					if(!GlobalVariable.isContinue)
						return;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem配置参数设置失败，抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return;
			}
			
			try {
				if((ret = nlModemManager.propGet(tConfname[i])) != iValue[i])
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem配置参数获取失败(%d,%s)", Tools.getLineInfo(),TESTITEM,ret,tConfname[i]);
					if(!GlobalVariable.isContinue)
						return;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem配置参数设置失败，抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return;
			}
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
