package com.example.highplattest.asyn;

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
* module			: 异步modem模块
* file name			: Asyn5.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141218
* directory 		: 
* description		: 测试异步modem的read能否正确读取数据
* related document	: 
* history 		 	: author			date			remarks
*			  		  zhengxq		    20141218	 	created
************************************************************************/
public class Asyn5 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Asyn5.class.getSimpleName();
	private final int MAXWAITTIME = 60;
	private final float WUCHASEC = 0.03f;
	private LinkType type = LinkType.ASYN;
	private Gui gui = new Gui(myactivity, handler);
	private String TESTITEM = "异步modem的read";
	private NlModemManager nlModemManager = null;
	
	public void asyn5()
	{
		String funcName="asyn5";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = 0, ret1 = 0, ret2 = 0,ret3 =0, rlen = 0,rlenother = 0;
		byte[] buf = new byte[ASYNPCKTMAXLEN+1];
		byte[] rbuf = new byte[ASYNPCKTMAXLEN];
		float tmp = 0.0f;

		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}

		/* process body */
		gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常,未初始化是进行读操作,应该返回NDK_ERR_MODEM_INIT_NOT
		if(gui.cls_show_msg1(2, "流程异常,未初始化是进行读操作,应该返回NDK_ERR_MODEM_INIT_NOT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据返回值错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// 拨号连接
		if ((ret = new Layer(myactivity,handler).linkUP(nlModemManager,type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:创建链路失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if((ret = new Layer(myactivity,handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:检测状态失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 发送缓冲区初始化
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}

		// case2:参数错误的情况,应该返回NDK_ERR_PARA
		if(gui.cls_show_msg1(2, "参数错误的情况,应该返回NDK_ERR_PARA,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_read(nlModemManager, null, rlen, MAXWAITTIME)) != NDK_ERR_PARA
				|| (ret1 = modem_read(nlModemManager, rbuf, 0, MAXWAITTIME))!=NDK_ERR_PARA
				|| (ret2 = modem_read(nlModemManager, rbuf, -1, MAXWAITTIME)) != NDK_ERR_PARA
				|| (ret3 = modem_read(nlModemManager, rbuf, rlen, -1)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败(%d,%d,%d,%d)", Tools.getLineInfo(),ret,ret1,ret2,ret3);
			if(!GlobalVariable.isContinue)
				return;
		}

		
		// case3:进行正常读测试
		// case3.1:读等待时间为60S
		if(gui.cls_show_msg1(2, "读等待时间为60S,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_write(nlModemManager, buf,rbuf.length)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		Arrays.fill(rbuf, (byte) 0);
		rlen = rbuf.length+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != rbuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if (rbuf.length != ret || !Tools.memcmp(buf, rbuf, rbuf.length)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),ret,rbuf.length);
			if(!GlobalVariable.isContinue)
				return;
		}

		 // case3.2:超时时间为0
		if(gui.cls_show_msg1(2, "超时时间为0,[取消]退出测试")==ESC)
			return;
		 if((ret =modem_write(nlModemManager, buf,rbuf.length))!=NDK_OK)
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
		 	if(!GlobalVariable.isContinue)
				return;
		 }
		 SystemClock.sleep(1000);
		 Arrays.fill(rbuf, (byte) 0);
		 rlen = rbuf.length+1;
		 if((ret = modem_read(nlModemManager, rbuf, rlen, 0))<0)
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		 }
		 
		 if(ret<ASYNPCKTMAXLEN)
		 {
			int rotherlen = ASYNPCKTMAXLEN - ret;
			byte[] rOtherBuf = new byte[rotherlen];
			if ((ret2 = modem_read(nlModemManager, rOtherBuf, rotherlen+1, MAXWAITTIME)) != rotherlen) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret2 = %d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			for (int i = 0; i < rOtherBuf.length; i++) 
			{
				rbuf[i + ret] = rOtherBuf[i];
			}
		}
		if (ASYNPCKTMAXLEN != (ret + ret2)|| !Tools.memcmp(buf, rbuf, ASYNPCKTMAXLEN)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),rbuf.length,(ret+ret2));
			if(!GlobalVariable.isContinue)
				return;
		}
		 
		 // case4:测试希望读取长度小于发送长度
		if(gui.cls_show_msg1(2, "读取长度小于发送长度,[取消]退出测试")==ESC)
			return;
		 if((ret=modem_write(nlModemManager, buf, rbuf.length))!=NDK_OK)
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			 if(!GlobalVariable.isContinue)
					return;
		 }
		 Log.e("write", Arrays.toString(buf));
		 Arrays.fill(rbuf, (byte) 0);
		 rlen = rbuf.length-1;
		 if((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME))!= rlen)
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			 if(!GlobalVariable.isContinue)
					return;
		 }
		 
		 if(rbuf.length-1 != ret || !Tools.memcmp(buf, rbuf, ret))
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),ret,rbuf.length);
			 if(!GlobalVariable.isContinue)
					return;
		 }
		 // 读取剩余的数据
		 Arrays.fill(rbuf, (byte) 0);
		 rlenother = rbuf.length - ret;
		 if((ret = modem_read(nlModemManager, rbuf, rlenother, MAXWAITTIME))!=rlenother)
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			 if(!GlobalVariable.isContinue)
					return;
		 }
		 
		 if(rlenother!=ret || !Tools.byteCompare(buf, rbuf, rlen+1, rlen+rlenother+1, 1, rlenother+1))
		 {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),ret,rlenother);
			 if(!GlobalVariable.isContinue)
					return;
		 }

		// case5:缓冲区没有数据,进行读数据操作,应该返回NDK_ERR_TIMEOUT
		 if(gui.cls_show_msg1(2, "缓冲区没有数据,进行读数据操作,应该返回NDK_ERR_TIMEOUT,[取消]退出测试")==ESC)
			return;
		long startTime = System.currentTimeMillis();
		rlen = buf.length+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_TIMEOUT
				|| (tmp = Tools.getStopTime(startTime) - MAXWAITTIME) > WUCHASEC) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据测试超时失败(ret = %d,tmp = %f)", Tools.getLineInfo(),ret,tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case6:测试读取的等待时间为0且没有数据应该返回超时(NDK_ERR_TIMEOUT)
		 if(gui.cls_show_msg1(2, "读取的等待时间为0且没有数据应该返回超时(NDK_ERR_TIMEOUT),[取消]退出测试")==ESC)
			return;
		if((ret = modem_read(nlModemManager, buf, rlen, 0))!=NDK_ERR_TIMEOUT)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据测试超时失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case7:测试异步链路断开,应该返回NDK_ERR_TIMEOUT
		if(gui.cls_show_msg1(2, "异步链路断开,应该返回NDK_ERR_TIMEOUT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:挂机失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != NDK_ERR_TIMEOUT) 
		{
			Log.e("read", Arrays.toString(rbuf));
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据返回值错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		new Layer(myactivity,handler).linkDown(nlModemManager,type);
		
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
