package com.example.highplattest.sdlc;

import java.util.Arrays;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
/*************************************************************************
* module			: 同步modem模块
* file name			: Sdlc200.java
* Author 			: xuess
* version			: 
* DATE				: 20170803
* directory 		: 
* description		: Sdlc模块内随机
* related document	: 
* history 		 	: author			date			remarks
*			  		  xuess		        20170823 		created
************************************************************************/
public class Sdlc200 extends UnitFragment
{
	private final String CLASS_NAME = Sdlc200.class.getSimpleName();
	private String TESTITEM = "同步modem模块内随机";
	private Gui gui = new Gui(myactivity, handler);	
	private NlModemManager  nlModemManager;
	private LinkType type = LinkType.SYNC;
	private final int PCKMAXLEN = 350;
	private String sdlcFuncArr[] = {"getVersion","sdlcInit","dial","check","write","read","hangup","clrbuf","getreadlen","reset","exCommand","propGet","propSet","powerCtrl"};
	private int len = sdlcFuncArr.length;
	private boolean initflag = false;
	private boolean dialflag = false;
	private boolean dataflag = false;
	private String tConfname[] = {"line_volt","choose_country","modem_volt","modem_voice","frame_s7",
			"frame_s10","frame_rst","frame_data","baud_freq","ccitt_bell","modem_dtmf","modem_voice_time"};
	private int[] iValue = {1,1,10,1,50,100,8,18,1,1,100,2};
	private int tConfIndex = 0;
	
    Random random = new Random();
	
	public void sdlc200() 
	{
		String funcName="sdlc200";
		/*private & local definition*/
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);//不确定
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "line %d:未找到该类,抛出异常(%s),%s设备不支持同步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		int ret2 = -1;
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		//前置
		if ((ret2 = modem_reset(nlModemManager)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}		
		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "Sdlc模块内随机组合测试中...\n还剩%d次(已成功%d次),按[取消]退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			StringBuilder funcStr = new StringBuilder();
			for(int i=0;i<g_RandomCycle;i++){
				func[i] = sdlcFuncArr[random.nextInt(len)];
				funcStr.append(func[i]).append("-->\n");
				if((i+1)%10 == 0 || i == g_RandomCycle-1){
					gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为:\n" + funcStr.toString(),bak-cnt+1);
					funcStr.setLength(0);
				}
			}
			//每次测试前置
			if(initflag){
				try {
					nlModemManager.hangup();
					dialflag = false;
	    			dataflag = false;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			cnt--;
			boolean ret=false;
			
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"随机第%d组第%d项,正在测试%s",bak-cnt,i+1,func[i]);
				SdlcFuncName fname = SdlcFuncName.valueOf(func[i]);
				if(!(ret=test(fname))){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s第%d组第%d项,%s方法测试失败",TESTITEM,bak-cnt,i+1,func[i]);
					break;
				}
			}
			if(!ret){
				for(int i=0;i<g_RandomCycle;i++){
					funcStr.append(func[i]).append("-->");
				}
				gui.cls_only_write_msg(CLASS_NAME,funcName,"第%d组随机测试失败,测试顺序为:%s",bak-cnt,funcStr.toString());
				funcStr.setLength(0);
			} else{
				succ++;
			}
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试完成,已执行次数为%d,成功为%d次\n请检查Sdlc模块内其他用例是否能正常使用！", TESTITEM, bak-cnt,succ);
		
		//测试结束,下电
		if((ret2 = modem_powerCtrl(nlModemManager, 0)) != 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}
	}

    private boolean test(SdlcFuncName fname)
    {
    	String funcName="test";
    	String version;
    	int ret = -1;
    	int rlen =0 ;
    	byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		int unTimeout=30*1000;
		byte[] pucCmdstr = new byte[128];
		byte[] pszRespData = new byte[128];
		int rand = 0;
    	
    	switch(fname){
    	case getVersion:
    		try 
    		{
    			version = nlModemManager.getVersion();
    			gui.cls_show_msg1(gScreenTime,"获取版本成功(ver = %s)",version);
    		} catch (RemoteException e) {
    			e.printStackTrace();
    			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取版本失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
    			return false;
    		}
    		break;
    	case sdlcInit:
    		if((ret = modem_init(nlModemManager, ModemBean.MDMPatchType, type))!= NDK_OK)
    		{
    			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:同步拨号初始化失败(ret = %d)", Tools.getLineInfo(),ret);
    			initflag = false;
    			return false;
    		}
    		initflag = true;
    		dialflag = false;
    		SystemClock.sleep(5000);
    		break;   	
    	case dial:
    		if(initflag){
    			if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_OK) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:拨号失败(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    			dialflag = true;
    			dataflag = false;
    			SystemClock.sleep(5000);
    		} else{
    			if ((ret = modem_dial(nlModemManager, ModemBean.MDMDialStr)) != NDK_ERR_MODEM_INIT_NOT) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化情况返回错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case check:
    		if(initflag && dialflag){
    			if ((ret = modem_check(nlModemManager)) != MODEM_CONNECT_AFTERPREDIAL) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:猫状态异常(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		} else{
    			//流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT、挂断状态,或未拨号状态,判断modem状态应该是MODEM_NOPREDIAL
    			ret = modem_check(nlModemManager);
    			if (ret != NDK_ERR_MODEM_INIT_NOT && ret != MODEM_NOPREDIAL) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:流程异常时check返回值错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case write:
    		if(initflag && dialflag){    	
				// 发送缓冲区初始化
				Arrays.fill(buf, (byte) 0);
				for (int i = 0; i < PCKMAXLEN; i++) 
				{
					buf[i] = (byte) (Math.random() * 256);
				}
				// 添加TPDU包头
				System.arraycopy(SDLCPCKTHEADER, 0, buf, 0,SDLCPCKTHEADER.length);
				if ((ret = modem_write(nlModemManager, buf, PCKMAXLEN)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					return false;
				}
				dataflag = true;        	
    		} else{
    			//流程异常,未初始化进行写操作,应该返回NDK_ERR_MODEM_INIT_NOT;初始化,未拨号,应该返回NDK_ERR_MODEM_NOPREDIAL
    			ret = modem_write(nlModemManager, buf, PCKMAXLEN);
    			if (ret != NDK_ERR_MODEM_INIT_NOT && ret != NDK_ERR_MODEM_NOPREDIAL) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:流程异常时写数据返回值错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case read:
    		if(initflag && dialflag && dataflag){
    			Arrays.fill(rbuf, (byte) 0);
    			rlen = PCKMAXLEN;
				if ((ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME)) != PCKMAXLEN) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
					return false;
				}
				dataflag = false;
    		} else{
    			//流程异常,未初始化时进行读操作,应该返回NDK_ERR_MODEM_INIT_NOT;初始化,未拨号,应该返回NDK_ERR_MODEM_NOPREDIAL；没有数据应该返回超时NDK_ERR_TIMEOUT
    			rlen = rbuf.length;
    			ret = modem_read(nlModemManager, rbuf, rlen, MAXWAITTIME);
    			if(ret != NDK_ERR_MODEM_INIT_NOT &&  ret != NDK_ERR_MODEM_NOPREDIAL && ret != NDK_ERR_TIMEOUT) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:流程异常时返回值错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case hangup:
    		if(initflag){
    			if((ret = modem_hangup(nlModemManager))!=NDK_OK)
    			{
    				gui.cls_show_msg1(gKeepTimeErr, "line %d:modem挂断失败(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    			dialflag = false;
    			dataflag = false;
    			SystemClock.sleep(5000);
    		} else{
    			//流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT
    			if ((ret = modem_hangup(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化挂断返回值错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case clrbuf:
    		if(initflag){
    			if((ret = modem_clrbuf(nlModemManager)) != NDK_OK)
    			{
    				gui.cls_show_msg1(gKeepTimeErr, "line %d:清空缓存失败(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    			dataflag = false;
    		} else{
    			//流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT
    			if ((ret = modem_clrbuf(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化清空返回值错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case getreadlen:
    		if(initflag){
    			if(dataflag)
    			{
    				//有数据时读取的长度应该等于写入的数据长度
    				if ((ret = modem_readLen(nlModemManager)) != PCKMAXLEN) 
        			{
        				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:获取modem长度错误(ret = %d)", Tools.getLineInfo(),ret);
        				return false;
        			}
    			}else{
    				if ((ret = modem_readLen(nlModemManager)) != 0) 
        			{
        				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:获取modem长度错误(ret = %d)", Tools.getLineInfo(),ret);
        				return false;
        			}
    			}
    		} else{
    			//流程异常,未初始化应该返回NDK_ERR_MODEM_INIT_NOT
    			if ((ret = modem_readLen(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) 
    			{
    				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:未初始化读取长度返回值错误(ret = %d)", Tools.getLineInfo(),ret);
    				return false;
    			}
    		}
    		break;
    	case reset:
    		if(dialflag){
    			// sdlc10  目前的NDK不支持在拨号之后直接复位?
    			
    		} else{
    			if ((ret = modem_reset(nlModemManager)) != NDK_OK) 
        		{
        			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s复位失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
        			return false;
        		}
    			initflag = false;
    			dialflag = false;
    			dataflag = false;
    		}
    		break;
    	case exCommand:
    		pucCmdstr[0] = 'A';
    		pucCmdstr[1] = 'T';
    		pucCmdstr[2] = 'E';
    		pucCmdstr[3] = '1';
    		pucCmdstr[4] = '\r';
    		pucCmdstr[5] = '\0';
    		if ((ret = modem_exCommand(nlModemManager, pucCmdstr, pszRespData, unTimeout)) < 0) 
    		{
    			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:AT命令交互测试失败(ret = %d)", Tools.getLineInfo(),ret);
    			return false;
    		}
    		Log.e("TAg", new String(pszRespData) + ",rlen=" + ret);
    		if ((new String(pszRespData).indexOf("OK")) == -1) 
    		{
    			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
    			return false;
    		}
    		break;
    	case propGet: 
    	case propSet:
    		try {
				if((ret = nlModemManager.propSet(tConfname[tConfIndex],iValue[tConfIndex])) != NDK_OK)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:Modem配置参数设置失败(%d,%s)", Tools.getLineInfo(),ret,tConfname[tConfIndex]);
					return false;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem配置参数设置失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return false;
			}
    		try {
				if((ret = nlModemManager.propGet(tConfname[tConfIndex])) != iValue[tConfIndex])
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sModem配置参数获取失败(%d,%s)", Tools.getLineInfo(),TESTITEM,ret,tConfname[tConfIndex]);
					return false;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:modem配置参数设置失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return false;
			}
    		tConfIndex++;
    		if(tConfIndex >= 12) tConfIndex = 0;
    		break;
    	case powerCtrl:
    		rand = random.nextInt(2);	//随机上下电
    		if(rand == 0){
    			//下电
    			if((ret = modem_powerCtrl(nlModemManager, 0)) != 0)
        		{
        			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:Modem下电失败(ret = %d)", Tools.getLineInfo(),ret);
        			return false;
        		}
    			initflag = false;
    			dialflag = false;
    			dataflag = false;
    		} else if(rand == 1){
    			if((ret = modem_powerCtrl(nlModemManager, 1)) != 1)
        		{
        			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:Modem上电失败(ret = %d)", Tools.getLineInfo(),ret);
        			return false;
        		}
    		}   		
    		break;
    	default:
    		break;
    	}
    	return true;
    }
	
	public enum SdlcFuncName
	{
		getVersion,
		sdlcInit,
		dial,
		check,
		write,
		read,
		hangup,
		clrbuf,
		getreadlen,
		reset,
		exCommand,
		propGet,
		propSet,
		powerCtrl
		
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
