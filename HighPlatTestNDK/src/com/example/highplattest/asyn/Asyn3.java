package com.example.highplattest.asyn;

import java.util.Arrays;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
* module			: 异步modem模块
* file name			: Asyn3.java
* Author 			: zhengxq
* version			: 
* directory 		: 
* description		: 测试check能否正确检测modem状态
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141217	 		created
************************************************************************/
public class Asyn3 extends UnitFragment
{
	private final String CLASS_NAME = Asyn3.class.getSimpleName();
	private String TESTITEM = "异步modem的check";
	private final int MAXWAITTIME = 60;
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager nlModemManager;
	
	public void asyn3() 
	{
		String funcName="asyn3";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		long startTime;
		int ret, rlen;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName, gKeepTimeErr,"line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}

		/* process body */
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT
		if(gui.cls_show_msg1(2, "流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_check(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:测试modem为-11 MDMSTATUS_NOPREDIAL (并未拨号,或链接已终止,期待再次拨号)状态的情况
		if(gui.cls_show_msg1(2, "测试modem为-11 MDMSTATUS_NOPREDIAL (并未拨号,或链接已终止,期待再次拨号)状态的情况,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:检测到异常状态(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:拨错误号码(拔号成功,但不能建立连接,异步检测到无载波)
		// case3.1:拨的号码不对,应该返回拨号成功
		if(gui.cls_show_msg1(2, "拨错误的号码,应返回拨号成功,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_dial(nlModemManager, "123456")) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断状态
		Log.i("check", "check status");
		startTime = System.currentTimeMillis();
		while (true) 
		{
			// case3.2:判断modem状态应该变化
			if ((ret = modem_check(nlModemManager)) != MODEM_NORETURN_AFTERPREDIAL) 
			{
				// modem状态是否为忙音
				if (ret == MODEM_MS_NOCARRIER) 
				{
					modem_hangup(nlModemManager);
					break;
				} 
				else 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:Modem状态异常(ret = %d)", Tools.getLineInfo(),ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			// 60S超时未检测到拨通
			else if (Tools.getStopTime(startTime) > MAXWAITTIME) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未能拨通(ret = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		Log.i("check", "check end");
		
		// case4:测试modem为2 CONNECT_AFTERPREDIAL
		if(gui.cls_show_msg1(2, "测试modem为2 ,应返回CONNECT_AFTERPREDIAL状态,[取消]退出测试")==ESC)
			return;
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未能拨通(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = modem_check(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:检测到异常状态(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case5:读写后再判断状态应该还是CONNECT_AFTERPREDIAL
		if(gui.cls_show_msg1(2, "读写后再判断状态应该还是CONNECT_AFTERPREDIAL,[取消]退出测试")==ESC)
			return;
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}

		if ((ret = modem_write(nlModemManager, buf,buf.length)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = buf.length+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen,MAXWAITTIME)) != buf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((buf.length != ret) || !Tools.memcmp(buf, rbuf, ret)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),buf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// 判断状态
		if ((ret = modem_check(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:猫状态异常(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case6:挂机后取状态为MDMSTUTAS_NOPREDIAL(-11)
		if(gui.cls_show_msg1(2, "挂机后取状态为MDMSTUTAS_NOPREDIAL(-11),【取消】退出测试")==ESC)
			return;
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:挂起失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if ((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:猫状态异常(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
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
