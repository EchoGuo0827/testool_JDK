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
* file name			: Asyn9.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141219
* directory 		: 
* description		: 测试异步modem的reset能否对modem进行有效复位
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141219 		created
************************************************************************/
public class Asyn9 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Asyn9.class.getSimpleName();
	private final int MAXWAITTIME = 60;
	private final int PCKMAXLEN = 350;
	private String TESTITEM = "异步modem的reset";
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager nlModemManager = null;
	
	public void asyn9() 
	{
		String funcName="asyn9";
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
		int ret = 0, rlen = 0;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "line %d:抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
			return;
		}

		/* process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		// case1:复位后modem状态应该为NDK_ERR_MODEM_INIT_NOT
		if(gui.cls_show_msg1(2, "复位后modem状态应该为NDK_ERR_MODEM_INIT_NOT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_check(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:验证拨号连接后复位,应该返回NDK_OK
		if(gui.cls_show_msg1(2, "验证拨号连接后复位,应该返回NDK_OK,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = new Layer(myactivity,handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未能接通(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_check(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = PCKMAXLEN+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:验证case2的复位操作不影响后续modem的正常使用
		if(gui.cls_show_msg1(2, "验证case2的复位操作不影响后续modem的正常使用,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = new Layer(myactivity,handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未能接通(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = PCKMAXLEN+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (PCKMAXLEN != ret || !Tools.memcmp(buf, rbuf, PCKMAXLEN)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:收发数据不一致(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:挂起测试失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过",TESTITEM);

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
