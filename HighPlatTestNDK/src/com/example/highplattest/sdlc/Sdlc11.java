package com.example.highplattest.sdlc;

import java.util.Arrays;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.util.Log;
/*************************************************************************
* module			: 同步modem模块
* file name			: Sdlc11.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141215
* directory 		: 
* description		: 同步Modem的exCommand，获取版本号
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141215 		created
************************************************************************/
public class Sdlc11 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc11.class.getSimpleName();
	private final int MAXLEN_PERATCOMMAND_SYS = 52;
	private String TESTITEM = "同步modem的exCommand";
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager  nlModemManager;
	
	
	public void sdlc11() 
	{
		/*private & local definition*/
		String funcName="sdlc11";
		int ret,ret1 = 0,ret2 = 0,unTimeout=30*1000;
		byte[] pucCmdstr = new byte[128];
		byte[] pszRespData = new byte[128];
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		
		// 测试前置
		modem_reset(nlModemManager);

		// case1:测试输入非法参数情况，应该返回NDK_ERR_PARA
		// 超时时间的单位是ms，文档写错
		pucCmdstr = "AT\r\0".getBytes();
		if ((ret = modem_exCommand(nlModemManager, null, pszRespData, unTimeout)) != NDK_ERR_PARA
				|| (ret1 = modem_exCommand(nlModemManager, pucCmdstr, null, unTimeout)) != NDK_ERR_PARA
				|| (ret2 = modem_exCommand(nlModemManager, pucCmdstr, pszRespData, -1)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d，ret1 = %d，ret2 = %d)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:测试AT命令格式不对的情况
		pucCmdstr = "B\n\0".getBytes();
		if ((ret = modem_exCommand(nlModemManager, pucCmdstr, pszRespData, unTimeout)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		pucCmdstr = "A\n\0".getBytes();
		if ((ret = modem_exCommand(nlModemManager, pucCmdstr, pszRespData, unTimeout)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:测试AT命令过长情况
		pucCmdstr = new byte[128];
		Arrays.fill(pucCmdstr, 0, MAXLEN_PERATCOMMAND_SYS + 1, (byte) '\r');
		pucCmdstr[0] = 'A';
		pucCmdstr[1] = 'T';
		pucCmdstr[MAXLEN_PERATCOMMAND_SYS + 2] = '\0';
		if ((ret = modem_exCommand(nlModemManager, pucCmdstr, pszRespData, unTimeout)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case4:测试正常情况
//		nlModemManager.sdlcInit(5);
		modem_clrbuf(nlModemManager);
		pucCmdstr[0] = 'A';
		pucCmdstr[1] = 'T';
		pucCmdstr[2] = 'E';
		pucCmdstr[3] = '1';
		pucCmdstr[4] = '\r';
		pucCmdstr[5] = '\0';
		if ((ret = modem_exCommand(nlModemManager, pucCmdstr, pszRespData, unTimeout)) < 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.e("TAg", new String(pszRespData) + ",rlen=" + ret);
		if ((new String(pszRespData).indexOf("OK")) == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,TESTITEM + "测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
		nlModemManager = null;
	}
}
