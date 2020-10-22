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
* file name			: Sdlc8.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141212
* directory 		: 
* description		: 测试同步modem的clrbuf能否清空同步通讯缓冲区
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141212 		created
************************************************************************/
public class Sdlc8 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc8.class.getSimpleName();
	private final int MAXWAITTIME = 30;
	private final int PCKMAXLEN = 350;
	private NlModemManager nlModemManager;
	private LinkType type = LinkType.SYNC;
	private String TESTITEM = "同步modem的clrbuf";
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc8()
	{
		String funcName="sdlc8";
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/* private & local definition */
		int ret = 0, rlen = 0, retRead;
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
		for (int i = 0; i < PCKMAXLEN; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		// 添加TPDU头
		System.arraycopy(SDLCPCKTHEADER, 0, buf, 0, SDLCPCKTHEADER.length);

		// case1:流程异常，未初始化，应该返回NDK_ERR_MODEM_INIT_NOT
		if ((ret = modem_clrbuf(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:modem初始化后，清缓冲区应该返回NDK_OK
		if ((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化错(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = modem_clrbuf(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s清空缓存失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_readLen(nlModemManager)) != 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s获取modem长度错误(len = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:写数据成功之后调用清空缓冲区操作，读数据应该返回超时NDK_ERR_TIMEOUT
		if ((ret = new Layer(myactivity,handler).linkUP(nlModemManager,type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s建立链路失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(buf, (byte) 0);
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if ((ret = modem_clrbuf(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s清空缓存失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		Arrays.fill(rbuf, (byte) 0);
		rlen = rbuf.length;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_TIMEOUT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case4:进行正常数据的收发操作，清空缓存区，不影响数据的读写操作
		if ((ret = modem_clrbuf(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s清空缓存失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = PCKMAXLEN;
		if ((retRead = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(retRead = %d)", Tools.getLineInfo(),TESTITEM,retRead);
			if(!GlobalVariable.isContinue)
				return;
		}
		if (PCKMAXLEN != retRead || !Tools.memcmp(buf, rbuf, PCKMAXLEN)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W=%d,R=%d)", Tools.getLineInfo(),TESTITEM,PCKMAXLEN,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = new Layer(myactivity,handler).linkDown(nlModemManager,type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s断开链路失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
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
