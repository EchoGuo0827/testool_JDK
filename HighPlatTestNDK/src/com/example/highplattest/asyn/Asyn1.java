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
/************************************************************************
* module			: 异步modem模块
* file name			: Asyn1.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141217
* directory 		: 
* description		: 测试asynInit能否对MODEM进行异步方式的初始化
* related document	: 
* history 		 	: author			date			remarks
*			  		  zhengxq		    20141217		created
************************************************************************/
public class Asyn1 extends UnitFragment
{
	
	private final String CLASS_NAME=Asyn1.class.getSimpleName();
	private String TESTITEM = "异步modem的asynInit";
	private final int MAXWAITTIME = 60;
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager nlModemManager = null;
	
	public void asyn1() 
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		/* private & local definition */
		int i=-1,ret = 0,rlen =0;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		
		/* process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		if((ret = modem_reset(nlModemManager)) != NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1:补丁包为任意值都应该初始化成功
		if(gui.cls_show_msg1(2, "补丁包为任意值都应该初始化成功,[取消]退出测试")==ESC)
			return;
		i = (int) (Math.random()*256);
		if((ret = modem_init(nlModemManager, i,LinkType.ASYN)) != NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:asyn初始化失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		modem_clrbuf(nlModemManager);
		
		if((ret = modem_dial(nlModemManager,ModemBean.MDMDialStr)) != NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号失败(DialNum = %s)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!=MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem状态未接通(DialNum = %s)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(buf, (byte) 0);
		for (int j = 0; j < buf.length; j++) 
		{
			buf[j] = (byte) (Math.random()*256);
		}
		
		if((ret = modem_write(nlModemManager, buf,buf.length))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = buf.length+1;
		if((ret = modem_read(nlModemManager, rbuf, rlen,MAXWAITTIME)) != buf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if( buf.length!=ret || !Tools.memcmp(buf, rbuf, ret))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:收发数据不一致(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = modem_hangup(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:挂断失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "%s测试通过",TESTITEM);
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
