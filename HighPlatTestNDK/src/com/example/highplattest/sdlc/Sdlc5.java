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
* file name			: Sdlc5.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141210
* directory 		: 
* description		: 测试同步modem的write能否正确写入数据
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141210	 		created
************************************************************************/
public class Sdlc5 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc5.class.getSimpleName();
	private final int MAXWAITTIME = 30;
	private final int PCKMAXLEN = 350;
	// 经过实际测试只能写入1K的数据
	private final int MAX_SDLC_WRITEDATALEN = 1024;
	private NlModemManager nlModemManager;
	private LinkType type = LinkType.SYNC;
	private String TESTITEM = "同步modem的write";
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc5() 
	{
		String funcName="sdlc5";
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
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = 0, ret1 = 0, rlen = 0;
		byte[] buf = new byte[1024 + 1];
		byte[] rbuf = new byte[512];

		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < rbuf.length; i++) 
		{
			buf[i] = (byte) (Math.random() * 256);
		}
		// 添加TPDU包头
		System.arraycopy(SDLCPCKTHEADER, 0, buf, 0, SDLCPCKTHEADER.length);
		if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case1:流程异常，未初始化进行写操作，应该返回NDK_ERR_MODEM_INIT_NOT
		gui.cls_show_msg1(2, "流程异常:未初始化进行读写操作");
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_ERR_MODEM_INIT_NOT) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据返回值错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		
		// case2:初始化，未拨号，应该返回NDK_ERR_MODEM_NOPREDIAL，初始化失败直接return
/*		// 初始化前将propSet接口设置LineVolt为0 by 陈仕廉建议
		if((ret = modem_propSet(nlModemManager, "LineVolt",0))!=NDK_OK)
		{
			gui.cls_show_msg1(2, "line %d:modem初始化线压值失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}*/

		if((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type)) != NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s初始化失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		
		if((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_ERR_MODEM_NOPREDIAL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
			
		// 拨号连接
		if ((ret = new Layer(myactivity,handler).linkUP(nlModemManager,type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s创建链路失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:参数错误:发送数据长度为0，发送数据指针为null，应该返回NDK_ERR_PARA|| (ret2 = modem_write(nlModemManager, buf, 1025)
		if ((ret = modem_write(nlModemManager, buf, 0)) != NDK_ERR_PARA
				|| (ret1 = modem_write(nlModemManager, null, PCKMAXLEN)) != NDK_ERR_PARA) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据返回值错误(ret = %d，ret1 = %d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
			if(!GlobalVariable.isContinue)
				// 每次挂断需要判断返回值 by 陈仕廉建议
				return;
		}

		// case3:正常读写操作
		// buf不能从0开始复制，前5个字节是TPDU头部
		for (int i = 5; i <PCKMAXLEN; i++) 
		{
			buf[i] = (byte) (Math.random()*256);
		}
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(rbuf, (byte) 0);
		rlen = PCKMAXLEN;
		if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKMAXLEN) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
//		Log.e(TAG, "write data:"+Arrays.toString(buf));
//		Log.e(TAG, "read data:"+Arrays.toString(rbuf));
//		!Tools.memcmp(buf, rbuf, PCKMAXLEN)
		if (PCKMAXLEN != ret || !Tools.byteCompare(buf, rbuf, 5, PCKMAXLEN-1, 5, PCKMAXLEN-1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W = %d,R = %d)", Tools.getLineInfo(),TESTITEM,PCKMAXLEN,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case4:写1K数据成功，缓冲区是几K不知？？
		if ((ret = modem_write(nlModemManager, buf, MAX_SDLC_WRITEDATALEN)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据返回值错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case5:挂机之后写数据应返回未拨号NDK_ERR_MODEM_NOPREDIAL
		if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s挂机失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(5000);
		if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_ERR_MODEM_NOPREDIAL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据返回值错误(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if((ret = new Layer(myactivity,handler).linkDown(nlModemManager,type))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem链路断开失败(ret = %d)", Tools.getLineInfo(),ret);
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
