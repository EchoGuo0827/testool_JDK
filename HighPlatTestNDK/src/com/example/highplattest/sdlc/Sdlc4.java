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
* file name			: Sdlc4.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141205
* directory 		: 
* description		: 测试check能否正确检测modem状态
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141205	 		created
************************************************************************/
public class Sdlc4 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc4.class.getSimpleName();
	private final int MAXWAITTIME = 30;
	private final int PACKMAXLEN =350;
	private NlModemManager  nlModemManager;
	private String TESTITEM = "同步modem的check";
	private Gui gui = new Gui(myactivity, handler);
	private LinkType type = LinkType.SYNC;
	
	public void sdlc4() 
	{
		String funcName="sdlc4";
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		long startTime;
		int ret, rlen;
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

		// case1:流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT
		if ((ret = modem_check(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:测试modem为-11 MDMSTATUS_NOPREDIAL (并未拨号,或SDLC链接已终止,期待再次拨号)状态的情况
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if ((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s检测到异常(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:测试modem为-4 MDMSTATUS_MS_BUSY
		// case3.1:拨的号码不对,应该返回拨号成功
		if ((ret = modem_dial(nlModemManager, "123456")) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s拨号失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断状态
		startTime = System.currentTimeMillis();
		while (true) 
		{
			// case3.2:判断modem状态应该变化
			if ((ret = modem_check(nlModemManager)) != MODEM_NORETURN_AFTERPREDIAL) 
			{
				// modem状态是否为忙音
				if (ret == MODEM_MS_BUSY) 
				{
					modem_check(nlModemManager);
					break;
				} 
				else 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s猫状态异常(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
					if(!GlobalVariable.isContinue)
					{
						modem_check(nlModemManager);
						return;
					}
				}
			}
			// 60S超时未检测到拨通
			else if (Tools.getStopTime(startTime) > MAXWAITTIME) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未能拨通(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case4:测试modem为2 CONNECT_AFTERPREDIAL
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未能拨通(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未能拨通(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case5:读写后再判断状态应该还是CONNECT_AFTERPREDIAL
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		// 添加TPDU头
		System.arraycopy(SDLCPCKTHEADER, 0, buf, 0,
				SDLCPCKTHEADER.length);

		if ((ret = modem_write(nlModemManager, buf, PACKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = PACKMAXLEN;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PACKMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((PACKMAXLEN != ret) || !Tools.memcmp(buf, rbuf, ret)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W=%d,R=%d)", Tools.getLineInfo(),TESTITEM,PACKMAXLEN,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// 判断状态
		if ((ret = modem_check(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s猫状态异常(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case6:没插电话线,应该返回MODEM_MS_NOCARRIER(-3)
		gui.cls_show_msg("请拔掉电话线,完成点[确认]继续");
		if((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!= MODEM_MS_NOCARRIER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未能拨通(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("请插入电话线,完成点击是继续");
		// case7:挂机后取状态为MDMSTUTAS_NOPREDIAL(-11)
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s挂起失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if ((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s猫状态失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
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
		if(nlModemManager!=null)
			modem_hangup(nlModemManager);
		gui = null;
		nlModemManager = null;
		
	}
}
