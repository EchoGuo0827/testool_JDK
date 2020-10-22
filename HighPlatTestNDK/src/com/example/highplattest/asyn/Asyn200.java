package com.example.highplattest.asyn;

import java.util.Arrays;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.RemoteException;
/*************************************************************************
* module			: 异步modem模块
* file name			: Asyn13.java
* Author 			: wangxy
* version			: 
* DATE				: 20170829
* directory 		: 
* description		: 模块内随机测试
* related document	: 
* history 		 	: author			date			remarks
*			  		 wangxy		   20170829 		created
************************************************************************/
public class Asyn200 extends UnitFragment
{
	private final String TESTITEM = "异步modem模块内随机";//需专门设备
	private final String CLASS_NAME=Asyn200.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private final int MAXWAITTIME = 60;
	private NlModemManager nlModemManager = null;
	private String nlModemFunArr[] = {"reset","asyninit","clrbuf","write","dial","read","hangup","check","readlen","getVersion","propset","propget","powerCtrl"};
	Random random = new Random();
	private boolean initFlag = false;	//是否初始化
	private boolean writeFlag = false;	//是否已写
	private boolean connectFalg=false;//是否已check连通
	private boolean propsetFalg=false;//是否已propset
	String tConfname[] = {"line_volt","choose_country","modem_volt","modem_voice","frame_s7",
			"frame_s10","frame_rst","frame_data","baud_freq","ccitt_bell","modem_dtmf","modem_voice_time"};
	int[] iValue = {1,1,10,1,50,100,8,18,1,1,100,2};
	private String funcStr1,funcStr2 ;

	public void asyn200() 
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
		try 
		{
			nlModemManager = (NlModemManager ) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未找到该类,抛出异常(%s),%s设备不支持异步MODEM",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "异步model模块内随机组合测试中...\n还剩%d次(已成功%d次),按【取消】退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String [g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=nlModemFunArr[random.nextInt(nlModemFunArr.length)];
			}
			funcStr1 = "";
			funcStr2 = "";
			for(int i=0;i<g_RandomCycle;i++){
				if(i<10){
					funcStr1 = funcStr1 + func[i] + "-->\n";
				}else{
					funcStr2 = funcStr2 + func[i] + "-->\n";
				}
				
			}
			gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为:\n" + funcStr1,bak-cnt+1);
			gui.cls_show_msg1(gScreenTime, funcStr2);
			cnt--;
			boolean ret=false;
			//每次测试前置
			if(initFlag){
				try {
					nlModemManager.hangup();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				initFlag = false;
			}
			 writeFlag = false;
			 connectFalg=false;
			 propsetFalg=false;
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"正在测试%s",func[i]);
				nlModemFuncName fname = nlModemFuncName.valueOf(func[i]);
				if(!(ret=RandomTest(fname,nlModemManager)))
					break;
			}
			if(ret)
			succ++;
		}
		//测试后置
		try {
			nlModemManager.hangup();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "异步model模块内随机组合测试测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
	}

	private boolean RandomTest(nlModemFuncName fname, NlModemManager nlModemManager) 
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		byte[] buf = new byte[512];
		byte[] rbuf = new byte[512];
		int ret;
		int i = (int) (Math.random()*256);
		String version;
		
		boolean is =true;
		switch(fname){
		case reset:
				if((ret= modem_reset(nlModemManager))!=NDK_OK){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:复位失败(ret = %d)", Tools.getLineInfo(),ret);
				   is=false;
				}

			break;
		case asyninit:
				if((ret= modem_init(nlModemManager, i,LinkType.ASYN))!=NDK_OK){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:asyn初始化失败(ret = %d)", Tools.getLineInfo(),ret);
					is=false;
					initFlag=false;
				}else{
					initFlag=true;
				}
			
			break;
		case clrbuf:
			if(initFlag){
					if((ret=modem_clrbuf(nlModemManager))!=NDK_OK){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:清缓存失败(ret = %d)", Tools.getLineInfo(),ret);
						is=false;
					}
			}else{
					if((ret=modem_clrbuf(nlModemManager))==NDK_OK){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:清缓存失败(ret = %d)", Tools.getLineInfo(),ret);
						is=false;
					}
			}
			break;
		case write:
			Arrays.fill(buf, (byte) 0);
			for (int j = 0; j < buf.length; j++) 
			{
				buf[j] = (byte) (Math.random()*256);
			}
			if(initFlag&&connectFalg){//初始化且联连通
					if((ret = modem_write(nlModemManager, buf,buf.length))!=NDK_OK){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
						is=false;
						writeFlag=false;
					}else{
						writeFlag=true;
					}
			}else{
					if((ret = modem_write(nlModemManager, buf,buf.length))==NDK_OK){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:写数据失败(ret = %d)", Tools.getLineInfo(),ret);
						is=false;
						writeFlag=true;
					}else{
						writeFlag=false;
					}
			}
			
			break;
		case dial:
			ModemBean.MDMDialStr="7289";
			if(initFlag){
					if((ret = modem_dial(nlModemManager,ModemBean.MDMDialStr))!=NDK_OK){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:拨号失败(DialNum = %s)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
						is=false;
					}
			}else{
					if((ret = modem_dial(nlModemManager,ModemBean.MDMDialStr))==NDK_OK){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:拨号失败(DialNum = %s)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
						is=false;
					}
			}
			break;
		case read:
			Arrays.fill(rbuf, (byte) 0);
			int rlen = buf.length+1;
			if(writeFlag&&connectFalg&&initFlag){
					if((ret = modem_read(nlModemManager, rbuf, rlen,MAXWAITTIME))!=buf.length){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:读数据失败(ret = %d)", Tools.getLineInfo(),ret);
						is=false;
					}
			}else{
					if((ret = modem_read(nlModemManager, rbuf, rlen,MAXWAITTIME))==buf.length){
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						is=false;
					}
			}
			
			break;
		case hangup:
				if((ret =  modem_hangup(nlModemManager))!=NDK_OK){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:挂断失败(ret = %d)", Tools.getLineInfo(),ret);
					is=false;
				}
			break;
		case check:
			if(initFlag){
				if((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))!=MODEM_CONNECT_AFTERPREDIAL){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem状态未接通(DialNum = %s)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
					is=false;
					connectFalg=false;
				}else{
					connectFalg=true;
				}
			}else{
				if((ret = new Layer(myactivity, handler).mdm_detectConnect(nlModemManager))==MODEM_CONNECT_AFTERPREDIAL){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem状态异常接通(DialNum = %s)(ret = %d)", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
					is=false;
					connectFalg=true;
				}else{
					connectFalg=false;
				}
			}
			break;
		case readlen:
			if(initFlag){
				if(writeFlag){
						if ((ret =modem_readLen(nlModemManager)) != buf.length) {
							gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
							gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:获取modem长度错误(ret = %d)", Tools.getLineInfo(),ret);
							is=false;
						}
				}else{
						if ((ret =modem_readLen(nlModemManager)) != 0) {
							gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
							gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:获取modem长度错误(ret = %d)", Tools.getLineInfo(),ret);
							is=false;
						}
				}
			}else{
					if ((ret =modem_readLen(nlModemManager)) != NDK_ERR_MODEM_INIT_NOT) {
						gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:未初始化返回值错误(ret = %d)", Tools.getLineInfo(),ret);
						is=false;
					}
						
			}
			break;
		case getVersion:
			try 
			{
				if ((version = nlModemManager.getVersion()) != null && !version.equals("")) {
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime, "获取的NDK版本号为:%s", version);
				} else {
					is = false;
					gui.cls_only_write_msg(CLASS_NAME,funcName, "%s模块内测试顺序为:\n" + funcStr1 + funcStr2, TESTITEM);// 只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gKeepTimeErr, "line %d:%s获取NDK版本号测试失败", Tools.getLineInfo(), TESTITEM);
				}

			} catch (RemoteException e) 
			{
				e.printStackTrace();
				is=false;
				gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%s获取NDK版本号测试失败", Tools.getLineInfo(),TESTITEM);
			}
			break;
		case propset:
			for (int j = 0; j < iValue.length; j++) {
				try {
					if((ret = nlModemManager.propSet(tConfname[j],iValue[j])) != NDK_OK)
					{
						gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%sModem配置参数设置失败(%d,%s)", Tools.getLineInfo(),TESTITEM,ret,tConfname[j]);
						is=false;
						propsetFalg=false;
						break;
					}else{
						propsetFalg=true;
					}
				} catch (RemoteException e) {
					e.printStackTrace();
					propsetFalg=false;
					is=false;
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem配置参数设置失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
					break;
					
				}
			}
			break;
		case propget:
			if(propsetFalg){
				for (int j = 0; j < iValue.length; j++) {
					try {
						if((ret = nlModemManager.propGet(tConfname[j])) != iValue[j])
						{
							gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
							gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%sModem配置参数获取失败(%d,%s)", Tools.getLineInfo(),TESTITEM,ret,tConfname[j]);
							is=false;
							break;
						}
					} catch (RemoteException e) {
						e.printStackTrace();
						is=false;
						gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem获取配置参数失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
						break;
					}
				}
			}else{
				for (int j = 0; j < iValue.length; j++) {
					try {
						if((ret = nlModemManager.propGet(tConfname[j])) == iValue[j])
						{
							gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
							gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%sModem配置参数获取失败(%d,%s)", Tools.getLineInfo(),TESTITEM,ret,tConfname[j]);
							is=false;
							break;
						}
					} catch (RemoteException e) {
						e.printStackTrace();
						is=false;
						gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
						gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem获取配置参数失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
						break;
					}
				}
			}
			break;
		case powerCtrl:
			try {
				if((ret = nlModemManager.powerCtrl(0)) != 0)
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					is=false;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				is=false;
				gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"line %d:modem下电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			try {
				if((ret = nlModemManager.powerCtrl(2)) != 1)
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%sModem上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					is=false;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				is=false;
				gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem上电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			try {
				if((ret = nlModemManager.powerCtrl(0)) != 0)
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:%sModem下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					is=false;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				is=false;
				gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName, gScreenTime,"line %d:modem下电失败,抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			break;
		default:
			break;
		}
		return is;
	}

	private enum nlModemFuncName {
		reset, asyninit, clrbuf, write, dial, read, hangup, check, readlen, getVersion, propset, propget, powerCtrl
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
