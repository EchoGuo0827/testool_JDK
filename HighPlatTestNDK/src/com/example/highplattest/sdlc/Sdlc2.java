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
* file name			: Sdlc2.java
* Author 			: zhengxq
* version			: 
* DATE				: 20141203
* directory 		: 
* description		: 测试sdlcInit能否对有线MODEM进行同步方式的初始化
* related document	: 
* history 		 	: author			date			remarks
*			  		 zhengxq		   20141203 		created
************************************************************************/
public class Sdlc2 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = Sdlc2.class.getSimpleName();
	private NlModemManager  nlModemManager;
	private int PCKTMAXLEN = 350;
	private int MAXWAITTIME = 30;
	private String TESTITEM = "同步modem的sdlcInit";
	private LinkType type = LinkType.SYNC;
	private Gui gui = new Gui(myactivity, handler);
	
	public void sdlc2() 
	{
		String funcName="sdlc2";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int i = -1, j = 0, ret = 0, rlen = 0;
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		/* process body */
		try {
			gui.cls_show_msg1(2, TESTITEM + "测试中...");
			// 测试前置，复位
			if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}

			while (true) 
			{
				gui.cls_show_msg1(1, TESTITEM + "测试中(i=" + i + ")");
				ret = modem_init(nlModemManager, i, type);
				
				// case1:测试EM_MDM_PatchType类型，判断是否返回不成功
				if (ret != NDK_OK) 
				{
					// case1.1:正确的EM_MDM_PatchType类型(i=0 1 2 3 4 5)，应返回成功，否则报错
					if ((i >= GlobalVariable.MDM_PatchType0) && (i <= GlobalVariable.MDM_PatchType5)) 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
						return;
					}

					// case1.2错误的EM_MDM_PatchType(i=-1 6 7)
					else if (ret != NDK_ERR_PARA) 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d，%d)", Tools.getLineInfo(),TESTITEM,i,ret);
						return;
					}
				} 
				else 
				{
					// case2:选取各种正确EM_MDM_PatchType类型时，继续判断是否sdlcInit起作用
					if ((i >= GlobalVariable.MDM_PatchType0) && (i <= GlobalVariable.MDM_PatchType5)) 
					{
						// case2.1:是否拨号成功
						ret = modem_clrbuf(nlModemManager);
						if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
						{
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s第%d次拨号失败(DialNum:%s,ret = %d)", Tools.getLineInfo(),TESTITEM,i,ModemBean.MDMDialStr,ret);
							if(GlobalVariable.isContinue==false)
								return;
						}

						// case2.2:判断modem状态，是否连接成功
						if ((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
						{
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%smodem状态未接通(DialNum:%s,MdmStatus:%d)", Tools.getLineInfo(),TESTITEM,ModemBean.MDMDialStr,ret);
							if(GlobalVariable.isContinue==false)
								return;
						}

						// 2.3初始化发送缓冲区buf
						ret = modem_clrbuf(nlModemManager);
						Arrays.fill(buf, (byte) 0);
						for (j = 0; j < PCKTMAXLEN; j++) 
						{
							buf[j] = (byte) (Math.random() * 256);
						}
						// 添加TPDU的包头
						System.arraycopy(SDLCPCKTHEADER, 0, buf, 0, SDLCPCKTHEADER.length);

						// case2.4:能否正常的写数据
						if ((ret = modem_write(nlModemManager, buf, PCKTMAXLEN)) != NDK_OK) 
						{
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s第%d次写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,i,ret);
							if(GlobalVariable.isContinue==false)
								return;
						}

						// case2.5:是否能正常读数据
						Arrays.fill(rbuf, (byte) 0);
						rlen = PCKTMAXLEN;
						if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKTMAXLEN) 
						{
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s第%d次读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,i,ret);
							if(GlobalVariable.isContinue==false)
								return;
						}

						// case2.6:比较读写数据是否一致
						if ((PCKTMAXLEN != ret) || !Tools.memcmp(buf, rbuf, PCKTMAXLEN)) 
						{
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s收发数据不一致(W= %d,R = %d)", Tools.getLineInfo(),TESTITEM,PCKTMAXLEN,ret);
							if(GlobalVariable.isContinue==false)
								return;
						}

						// case2.7:是否能正常挂机
						if ((ret = modem_hangup(nlModemManager)) != NDK_OK) 
						{
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s第%d次挂断失败(ret = %d)", Tools.getLineInfo(),TESTITEM,i,ret);
							return;
						}
						SystemClock.sleep(3000);
					}
					// 错误的MDM_PatchType类型
					else 
					{
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d，%d)", Tools.getLineInfo(),TESTITEM,i,ret);
						if(!GlobalVariable.isContinue)
							return;
					}
				}
				if (++i > 7) 
				{
					break;
				}
			}
			if((ret = modem_hangup(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem挂断失败(ret = %d)", Tools.getLineInfo(),ret);
				return;
			}
		} catch (Exception e) 
		{
			gui.cls_show_msg1(gKeepTimeErr, "line %d:%s测试失败，抛出异常(%s)", Tools.getLineInfo(),TESTITEM,e.getMessage());
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
