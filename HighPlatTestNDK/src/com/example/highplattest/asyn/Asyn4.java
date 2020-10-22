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
* file name			: Asyn4.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141217
* directory 		: 
* description		: 测试异步modem的write能否正确写入数据
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141217	 		created
************************************************************************/
public class Asyn4 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Asyn4.class.getSimpleName();
	private String TESTITEM = "异步modem的write";
	private final int MAXWAITTIME = 60;
	private final int PCKMAXLEN = 350;
	private LinkType type = LinkType.ASYN;
	private Gui gui = new Gui(myactivity, handler);
	private NlModemManager nlModemManager = null;
	
	public void asyn4() 
	{
		String funcName="asyn4";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = 0, ret1 = 0,ret2=0, rlen = 0;
		byte[] buf = new byte[ASYNPCKTMAXLEN + 1];
		byte[] rbuf = new byte[ASYNPCKTMAXLEN];
		
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
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

		// case1:流程异常,未初始化进行写操作,应该返回NDK_ERR_MODEM_INIT_NOT
		if(gui.cls_show_msg1(2, "流程异常,未初始化进行写操作,应该返回NDK_ERR_MODEM_INIT_NOT,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据返回值错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// 拨号连接
		if ((ret = new Layer(myactivity, handler).linkUP(nlModemManager,type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:创建链路失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!=MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem状态未接通(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:参数错误：发送数据长度为0,发送数据指针为null,应该返回NDK_ERR_PARA
		if(gui.cls_show_msg1(2, "参数错误:发送数据长度为0,发送数据指针为null,应该返回NDK_ERR_PARA,[取消]退出测试")==ESC)
			return;
		Arrays.fill(buf, (byte) 0);
		if ((ret = modem_write(nlModemManager, buf, 0)) != NDK_ERR_PARA
					|| (ret1 = modem_write(nlModemManager, null, PCKMAXLEN)) != NDK_ERR_PARA
					|| (ret2 = modem_write(nlModemManager, buf, ASYNPCKTMAXLEN+1))!=NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据返回值错误(%d,%d,%d)", Tools.getLineInfo(),ret,ret1,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case3:正常读写操作
		if(gui.cls_show_msg1(2, "正常读写操作,[取消]退出测试")==ESC)
			return;
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		Log.e("length", rbuf.length+"");
		if ((ret = modem_write(nlModemManager, buf,rbuf.length)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:modem写数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = rbuf.length+1;
		if ((ret = modem_read(nlModemManager, rbuf, rlen,MAXWAITTIME)) != rbuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if (rbuf.length != ret || !Tools.memcmp(buf, rbuf, ret)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:收发数据不一致(%d,%d)", Tools.getLineInfo(),ret,rbuf.length);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case4:写4K数据是否成功
		if(gui.cls_show_msg1(2, "写4K数据是否成功,[取消]退出测试")==ESC)
			return;
		if ((ret = modem_write(nlModemManager, buf, 4096)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据返回错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);

		// case5:挂机之后写数据应返回未拨号NDK_ERR_MODEM_NOPREDIAL
		if(gui.cls_show_msg1(2, "挂机之后写数据应返回未拨号NDK_ERR_MODEM_NOPREDIAL,[取消]退出测试")==ESC)
		{
			unitEnd();
			return;
		}
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:挂机失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if((ret = modem_check(nlModemManager)) != MODEM_NOPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:状态出错(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_ERR_MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据返回值错误(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		new Layer(myactivity,handler).linkDown(nlModemManager,type);
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
