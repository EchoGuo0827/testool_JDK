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

/************************************************************************
* module			: 同步modem模块
* file name			: Sdlc3.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141204
* directory 		: 
* description		: 测试dial能否进行拨号的操作
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141204		    created
************************************************************************/
public class Sdlc3 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc3.class.getSimpleName();
	private final int MAXWAITTIME = 30;
	private final int PCKTMAXLEN = 350;
	private NlModemManager  nlModemManager;
	private String TESTITEM = "同步modem的dial";
	private Gui gui = new Gui(myactivity, handler);
	private LinkType type = LinkType.SYNC;
	
	public void sdlc3() 
	{
		String funcName="sdlc3";
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
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = 0, rlen = 0;
		long startTime;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];

		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		// 初始化发送缓冲区
		Arrays.fill(buf, (byte) 0);
		for (int k = 0; k < buf.length; k++) 
		{
			buf[k] = (byte) (Math.random() * 256);
		}
		// 添加TPDU包头
		System.arraycopy(SDLCPCKTHEADER, 0, buf, 0,SDLCPCKTHEADER.length);

		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常,未初始化拨号应该返回NDK_ERR_MODEM_INIT_NOT
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:拨错误号码(拨号成功,但不能建立连接,应检测到忙音MODEM_MS_BUSY)
		if ((ret = modem_dial(nlModemManager, "123456")) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s拨号失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		startTime = System.currentTimeMillis();
		while (true) 
		{
			// 未能正确获取modem状态
			if ((ret = modem_check(nlModemManager)) != MODEM_NORETURN_AFTERPREDIAL) 
			{
				// 判断modem状态是否为忙音
				if (ret == MODEM_MS_BUSY) 
				{
					if((ret = modem_hangup(nlModemManager))!=NDK_OK)
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem挂断失败(ret = %d)", Tools.getLineInfo(),ret);
					}
					break;
				} 
				else 
				{
					
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s猫状态异常(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
					if(GlobalVariable.isContinue == false)
						return;
				}
			} 
			else if (Tools.getStopTime(startTime) > MAXWAITTIME) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s猫检测状态超时(DialNum = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}

		// case3:参数错误,拨号号码为null或拨号号码长度大于25,应该返回NDK_ERR_PARA
		if ((ret = modem_dial(nlModemManager, null)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s拨号失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// 拨号号码长度大于25时返回参数错误,文档没有描述,文档应该增加描述
		String dialNum = "12345678901234567890123456";
		if((ret = modem_dial(nlModemManager, dialNum)) != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s拨号失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请并机(接入电话并摘机)后按【确认】");
		// case4:测试拨号时并机,应检测到并机 NDK_ERR_MODEM_OTHERMACHINE
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_OTHERMACHINE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s并机状态下拨号应失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		gui.cls_show_msg("子用例测试通过,请将电话挂机后点【确认】继续");
		// case5:测试未插电话线的情况,应该返回NDK_ERR_MODEM_NOLINE
		gui.cls_show_msg("请拔下POS上的电话线后点【确认】继续");
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_NOLINE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未插电话线拨号(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("子用例测试通过,请插上电话线后按是继续");

		// case6:正确拨号,应该返回MODEM_CONNECT_AFTERPREDIAL
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		modem_clrbuf(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未插电话线拨号(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s猫未能拨通(DialNmu = %s,MdmStatus = %d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		if ((ret = modem_write(nlModemManager, buf, PCKTMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = PCKTMAXLEN;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKTMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if (PCKTMAXLEN != ret || !Tools.memcmp(buf, rbuf, PCKTMAXLEN)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W = %d,R=%d)", Tools.getLineInfo(),TESTITEM,PCKTMAXLEN,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s挂断失败(ret = %d)", Tools.getLineInfo(),TESTITEM,PCKTMAXLEN,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,TESTITEM + "测试通过");
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		if(nlModemManager!=null)
			modem_hangup(nlModemManager);
		gui = null;
		nlModemManager = null;
		
	}
}