package com.example.highplattest.sdlc;

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
* module			: 同步modem模块
* file name			: Sdlc7.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141211
* directory 		: 
* description		: 测试同步modem的hangup能否断开MODEM的同步通讯连接
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141211	 		created
************************************************************************/
public class Sdlc7 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = "Sdlc7";
	private final int MAXWAITTIME = 30;
	private final int PCKMAXLEN = 350;
	private NlModemManager nlModemManager;
	private String TESTITEM = "同步modem的hangup";
	private LinkType type = LinkType.SYNC;
	private Gui gui = new Gui(myactivity, handler);
	
	public int SdlcModemLink() 
	{
		String funcName="SdlcModemLink";
		/* private & local definition */
		int ret;

		/* process body */
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			return GlobalVariable.FAIL;
		}
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s拨号%s失败%d", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
			return GlobalVariable.FAIL;
		}
		if ((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			modem_hangup(nlModemManager);
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未能接通(DialNum = %s,Mdmstatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
			return GlobalVariable.FAIL;
		}
		return GlobalVariable.SUCC;
	}
	
	
	public void sdlc7() 
	{
		String funcName="sdlc7";
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/* private & local definition */
		int ret = 0, rlen = 0;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常，未初始化情挂断应该返回NDK_ERR_MODEM_INIT_NOT
		if ((ret = modem_hangup(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:modem收发之后挂断，应该返回NDK_OK
		if ((ret = SdlcModemLink()) != GlobalVariable.SUCC) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s创建链路失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < PCKMAXLEN; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		// 添加TPDU包头
		System.arraycopy(SDLCPCKTHEADER, 0, buf, 0, SDLCPCKTHEADER.length);
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen =PCKMAXLEN;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if (PCKMAXLEN != ret || !Tools.memcmp(buf, rbuf, PCKMAXLEN)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W=%d,R=%d)", Tools.getLineInfo(),TESTITEM,PCKMAXLEN,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s挂起测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:挂断成功后判断modem状态应该是MODEM_NOPREDIAL(挂断状态，或未拨号状态)
		SystemClock.sleep(5000);
		if ((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s猫状态异常(status = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if((ret = modem_hangup(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1(gKeepTimeErr, "line %d:modem挂断失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,TESTITEM + "测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(nlModemManager!=null)
			modem_hangup(nlModemManager);
		nlModemManager = null;
		gui = null;
	}
}
