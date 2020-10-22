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
* file name			: Asyn2.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141217
* directory 		: 
* description		: 测试dial能否进行拨号的操作
* related document	: 
* history 		 	: author			date			remarks
*			  		  zhengxq		    20141217		created
************************************************************************/
public class Asyn2 extends UnitFragment
{
	private final String CLASS_NAME = Asyn2.class.getSimpleName();
	private String TESTITEM = "异步modem的dial";
	private final int MAXWAITTIME = 60;
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager nlModemManager;
	
	public void asyn2() 
	{
		String funcName="asyn2";
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
		long startTime;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		
		/* process body */
		gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
		// 初始化发送缓冲区
		Arrays.fill(buf, (byte) 0);
		for (int k = 0; k < buf.length; k++) 
		{
			buf[k] = (byte) (Math.random() * 256);
		}

		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常,未初始化拨号应该返回NDK_ERR_MODEM_INIT_NOT
		if(gui.cls_show_msg1(2, "流程异常,未初始化拨号应该返回NDK_ERR_MODEM_INIT_NOT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:错误号码（拨号成功,但不能建立连接,异步应检测到无载波）
		if(gui.cls_show_msg1(2, "错误号码(拨号成功,但不能建立连接,异步应检测到无载波),[取消]退出测试")==ESC)
			return;
		if ((ret = modem_dial(nlModemManager, "123456")) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号失败(ret = %d)", Tools.getLineInfo(),ret);
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
				if (ret == MODEM_MS_NOCARRIER) 
				{
					modem_hangup(nlModemManager);
					break;
				} 
				else 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:猫状态异常(DialNum = %s)(ret = %d)",Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			} 
			else if (Tools.getStopTime(startTime) > MAXWAITTIME) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:猫检测状态超时(ret = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		// case3:参数错误,拨号号码为null或拨号号码长度超过25,应该返回NDK_ERR_PARA
		if(gui.cls_show_msg1(2, "参数错误,拨号号码为null或拨号号码长度超过25,应该返回NDK_ERR_PARA,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_dial(nlModemManager, null)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号应失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 拨号号码长度超过25就返回参数错误,文档上没有注明
		String dialNum = "12345678901234567890123456";
		if((ret = modem_dial(nlModemManager, dialNum)) != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号应失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("请并机(接入电话并摘机)后点任意键继续");
		// case4:测试拨号时并机,应检测到并机,应该返回NDK_ERR_MODEM_OTHERMACHINE
		if(gui.cls_show_msg1(2, "测试拨号时并机,应检测到并机,应该返回NDK_ERR_MODEM_OTHERMACHINE,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_OTHERMACHINE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:并机状态下拨号应失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("子用例测试通过,请将电话挂机后点任意键继续");
		if(false)
		{
			// case5:在成功拨号后,进行并机,再进行数据发送,应检测到并机
			if(gui.cls_show_msg1(2, "成功拨号后,进行并机,再进行数据发送,应检测到并机,[取消]退出测试")==ESC)
				return;
			if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:初始化失败(ret = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if ((ret = modem_clrbuf(nlModemManager)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:清缓冲区失败(ret = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:拨号失败(ret = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if ((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!=MODEM_CONNECT_AFTERPREDIAL)	
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:modem状态未接通(DialNum = %d)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("请并机(接入电话并摘机),后按任意键继续");
			if ((ret = modem_write(nlModemManager, buf,buf.length)) != NDK_ERR_MODEM_OTHERMACHINE) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("子用例测试通过,请将电话挂机后点是继续");
			modem_hangup(nlModemManager);
		}

		// case6:测试未插电话线的情况,应该返回NDK_ERR_MODEM_NOLINE
		if(gui.cls_show_msg1(2, "测试未插电话线的情况,应该返回NDK_ERR_MODEM_NOLINE,【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg("请拔下POS上的电话线后点任意键继续");
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_NOLINE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未插电话线拨号(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("子用例测试通过,点任意键继续");
		// case7:正确拨号
		if(gui.cls_show_msg1(2, "正确拨号,[取消]退出测试")==ESC)
		{
			unitEnd();
			return;
		}
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType,LinkType.ASYN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:初始化错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		modem_hangup(nlModemManager);
		if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未插电话线拨号(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!=MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:猫未能拨通(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_write(nlModemManager, buf,buf.length)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = buf.length+1;
		if ((ret = modem_read(nlModemManager, rbuf,rlen, MAXWAITTIME)) != buf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (buf.length != ret || !Tools.memcmp(buf, rbuf, ret)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:收发数据不一致", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:挂起失败(ret = %d)", Tools.getLineInfo(),ret);
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
