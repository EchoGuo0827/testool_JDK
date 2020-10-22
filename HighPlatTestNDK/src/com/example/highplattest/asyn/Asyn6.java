package com.example.highplattest.asyn;

import java.util.Arrays;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
/************************************************************************
* module			: 异步modem模块
* file name			: Asyn6.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141218
* directory 		: 
* description		: 测试异步modem的hangup能否断开MODEM的异步通讯连接
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141218	 		created
************************************************************************/
public class Asyn6 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Asyn6.class.getSimpleName();
	private final int MAXWAITTIME = 60;
	private NlModemManager nlModemManager;
	private Gui gui = new Gui(myactivity, handler);
	private String TESTITEM = "异步modem的hangup";
	
	
	public int AsynModemLink() 
	{
		String funcName="AsynModemLink";
		/* private & local definition */
		int ret = 0;

		/* process body */
		
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错(ret = %d)", Tools.getLineInfo(),ret);
			return GlobalVariable.FAIL;
		}
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号(%d)失败(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
			modem_hangup(nlModemManager);
			return GlobalVariable.FAIL;
		}
		if ((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!=MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未能接通(ret = %d)", Tools.getLineInfo(),ret);
			modem_hangup(nlModemManager);
			return GlobalVariable.FAIL;
			
		}
		return GlobalVariable.SUCC;
	}
	
	public void asyn6() 
	{
		String funcName="asyn6";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = 0, rlen = 0;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}

		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常,未初始化情挂断应该返回NDK_ERR_MODEM_INIT_NOT
		if(gui.cls_show_msg1(2, "流程异常,未初始化情挂断应该返回NDK_ERR_MODEM_INIT_NOT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_hangup(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:modem收发之后挂断,应该返回NDK_OK
		if(gui.cls_show_msg1(2, "modem收发之后挂断,应该返回NDK_OK,[取消]退出测试")==ESC)
			return;
		if ((ret = AsynModemLink()) != GlobalVariable.SUCC) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:创建链路失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		if ((ret = modem_write(nlModemManager, buf, buf.length)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = buf.length+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != buf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if (buf.length != ret || !Tools.memcmp(buf, rbuf, buf.length)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),ret,buf.length);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:挂起测试失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:挂断成功后判断modem状态应该是MODEM_NOPREDIAL(挂断状态,或未拨号状态)
		if(gui.cls_show_msg1(2, "挂断成功后判断modem状态应该是MODEM_NOPREDIAL(挂断状态,或未拨号状态),[取消]退出测试")==ESC)
			return;
		SystemClock.sleep(5000);
		if ((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:猫状态异常(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,TESTITEM+"测试通过...");
	}


	@Override
	public void onTestUp() 
	{
		
	}


	@Override
	public void onTestDown() {
		if(nlModemManager!=null)
			modem_hangup(nlModemManager);
		gui = null;
		nlModemManager = null;
		
	}
	
}
