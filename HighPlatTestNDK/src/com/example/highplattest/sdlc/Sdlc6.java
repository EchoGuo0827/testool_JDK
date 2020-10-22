package com.example.highplattest.sdlc;

import java.util.Arrays;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
* module			: 同步modem模块
* file name			: Sdlc6.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141211
* directory 		: 
* description		: 测试同步modem的read能否正确读取数据
* related document	: 
* history 		 	: author			date			remarks
*			  		  zhengxq		    20141211	 	created
************************************************************************/
public class Sdlc6 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc6.class.getSimpleName();
	private final int MAXWAITTIME = 30;
	private final float WUCHASEC = 0.03f;
	private final int PCKMAXLEN = 350;
	private NlModemManager nlModemManager;
	private LinkType type = LinkType.SYNC;
	private String TESTITEM = "同步modem的read";
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc6() 
	{
		String funcName="sdlc6";
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
		int ret = 0, ret1 = 0, ret2 = 0, rlen = 0,rotherlen=0;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		float tmp = 0.0f;
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常，未初始化是进行读操作，应该返回NDK_ERR_MODEM_INIT_NOT
		rlen = rbuf.length;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据返回值错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// 拨号连接
		if ((ret = new Layer(myactivity,handler).linkUP(nlModemManager,type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s创建链路失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 发送缓冲区初始化
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < PCKMAXLEN; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		// 添加TPDU包头
		System.arraycopy(SDLCPCKTHEADER, 0, buf, 0,SDLCPCKTHEADER.length);

		// case2:参数错误的情况，应该返回NDK_ERR_PARA
		rlen = rbuf.length;
		if ((ret = modem_read(nlModemManager, null, rlen, MAXWAITTIME)) != NDK_ERR_PARA
				|| (ret1 = modem_read(nlModemManager, rbuf, -1, MAXWAITTIME)) != NDK_ERR_PARA
				|| (ret2 = modem_read(nlModemManager, rbuf, rlen, -1)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case3:进行正常读测试
		// case3.1:读等待时间为60S
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		SystemClock.sleep(5000);
		Arrays.fill(rbuf, (byte) 0);
		rlen = PCKMAXLEN;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}

		if (PCKMAXLEN != ret || !Tools.memcmp(buf, rbuf, PCKMAXLEN)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W=%d,R=%d)", Tools.getLineInfo(),TESTITEM,PCKMAXLEN,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}

		 // case3.2:超时时间为0，应该能读到一部分数据
		if((ret = modem_write(nlModemManager, buf, PCKMAXLEN))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		SystemClock.sleep(5000);
		Arrays.fill(rbuf, (byte) 0);
		rlen = rbuf.length;
		if((ret1 = modem_read(nlModemManager, rbuf, rlen, 0))<=0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.e("read1", ret1+"");
		ret2 = 0;
		if(ret1<PCKMAXLEN)
		{
			rotherlen = PCKMAXLEN - ret1;
			byte[] rOtherBuf = new byte[rotherlen];
			if((ret2= modem_read(nlModemManager, rOtherBuf, rlen, MAXWAITTIME))!=rotherlen)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret2);
				if(GlobalVariable.isContinue==false)
					return;
			}
			for (int i = 0; i < rOtherBuf.length; i++) 
			{
				rbuf[i+ret1] = rOtherBuf[i]; 
			}
		}
		Log.e("read2", (ret1+ret2)+"");
		if(PCKMAXLEN!= (ret1+ret2)|| !Tools.memcmp(buf, rbuf, PCKMAXLEN))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W = %d,R=%d)", Tools.getLineInfo(),TESTITEM,PCKMAXLEN,(ret1+ret2));
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case4:等待超时
		long startTime = System.currentTimeMillis();
		rlen = rbuf.length;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_TIMEOUT
				|| (tmp = Tools.getStopTime(startTime) - MAXWAITTIME) > WUCHASEC) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据测试超时失败(ret = %d,tm = %f)", Tools.getLineInfo(),TESTITEM,ret,tmp);
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case5:测试同步链路断开，应该返回NDK_ERR_MODEM_NOPREDIAL(SDLC链路断开)
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s挂机失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		SystemClock.sleep(5000);
		rlen = rbuf.length;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据返回值错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}

		if((ret = new Layer(myactivity,handler).linkDown(nlModemManager,type))!=NDK_OK)
		{
			gui.cls_show_msg1(gKeepTimeErr, "line %d:modem链路挂断失败(ret = %d)", Tools.getLineInfo(),ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, TESTITEM + "测试通过");
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
