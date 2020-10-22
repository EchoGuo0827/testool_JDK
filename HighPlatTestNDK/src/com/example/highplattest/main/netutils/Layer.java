package com.example.highplattest.main.netutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkStatus;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.NetStatus;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.TransStatus;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.quectel.jni.QuecJNI;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiConfiguration;
import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * module 			: main
 * file name 		: Layer.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141027
 * directory 		: 
 * description 		: 通讯层实现类
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Layer implements LayerBase,Lib,NDK
{
	/*------------global variables definition-----------------------*/ 
	private final String TAG = Layer.class.getSimpleName();
    public static boolean TCPRESETFLAG = false;
    final int SLEEP_SDLC_HANGUP = 5;
    private final int timeout = 60;
    private Gui gui;
    
    // 网络
    public static LinkStatus linkStatus = LinkStatus.linkdown;
    NetStatus netStatus = NetStatus.NETDOWN;
    public static TransStatus transStatus = TransStatus.TRANSDOWN;
    BpsBean bpsSetting= new BpsBean();
    static LayerBase instance;
    private Context context;
    private Handler handler;
    WifiUtil wifiUtil;
	OutputStream mOutputStream;
    InputStream mInputStream;
    
    public Layer(Context context,Handler handler)
    {
    	this.context = context;
    	this.handler = handler;
    	wifiUtil=WifiUtil.getInstance(context,handler);
    	gui = new Gui(context, handler);
    	gui.setOverFlag(false);
    }
    
	public static LayerBase getLayerBase(Context context,Handler handler)
    {
    	instance = new Layer(context,handler);
    	return instance;
    }
    
    /**构造方法给多模块并发用例使用*/		//add by xuess 20171013
    public Layer(Context context,Handler handler,int handlerMsg)
    {
    	wifiUtil=WifiUtil.getInstance(context,handler);
    	this.context = context;
    	this.handler = handler;
    	gui = new Gui(context, handler, handlerMsg);
    }
    public static LayerBase getLayerBase(Context context,Handler handler,int handlerMsg)
    {
    	instance = new Layer(context,handler,handlerMsg);
    	return instance;
    }
    
    /*---------------functions definition---------------------------*/
 	
	// 检测modem的连接状态
	public int mdm_detectConnect(NlModemManager modem) 
	{
		/*private & local definition*/
		int times = 0,ret = -1;
		long oldtime =0;
		
		/*process body*/
		// 拨号与建立连接要有延时，延时2s by 陈仕廉
		gui.cls_show_msg1(2, "检测modem状态中");
		oldtime = System.currentTimeMillis();
		while(true)
		{
			try 
			{
				// 循环监测modem的状态，
				if((ret = modem.check())<MODEM_NORETURN_AFTERPREDIAL||Tools.getStopTime(oldtime)>MAXTIMEOUT)
				{
//					// 间隔1s的时间后继续监测modem的状态，
//					SystemClock.sleep(1000);
//					continue;
					break;
				}
			} catch (RemoteException e) 
			{
				e.printStackTrace();
				gui.cls_show_msg1(2, "%s，line %d:modem检测状态失败，抛出异常（%s）", TAG,Tools.getLineInfo(),e.getMessage());
				return MODEM_EXCEPTION_THROW;
			}
			Log.e(TAG, "modem status:"+ret);
			if(ret == MODEM_CONNECT_AFTERPREDIAL)
			{
				// 连续两次都监测为连接状态视为通过
				if(++times== TIME4KEEPSTATE)
					break;
				else 
				{
					// 间隔2s后再次监测modem的状态
					SystemClock.sleep(2000);
					continue;	
				}
			}
			else
			{
				// 间隔1s检测1次modem的状态
				SystemClock.sleep(1000);
				continue;
			}
		}
		return ret;
	}
	
	
	// 建立连接
	public int linkUP(Object object,LinkType type)
	{
		/*process body*/
		if(linkStatus ==LinkStatus.linkup)
		{
			return SUCC;
		}
		transStatus = TransStatus.TRANSDOWN;
		netStatus = NetStatus.NETDOWN;
		return _LinkUp(object,type);
	}
	
	
	
	// 建立同步modem连接
	public int linkUp_SDLC(NlModemManager modem) 
	{
		/*private & local definition*/
		int ret = -1,ret1=-1;
		
		/*process body*/
		gui.cls_printf("建立同步拨号连接中".getBytes());
		try {
			// 初始化
			if((ret = modem.sdlcInit(ModemBean.MDMPatchType))!=NDK_OK)
			{
				gui.cls_show_msg1(2,"%s,line %d:同步modem初始化失败(ret = %d)",TAG,Tools.getLineInfo(),ret);
				return ret;
			}
			modem.clrbuf();
			
			// 拨号
			if((ret = modem.dial(ModemBean.MDMDialStr))!=NDK_OK)
			{
				if((ret1 = modem.hangup())!=NDK_OK)
				{
					gui.cls_show_msg1(2, "%s,line %d:同步拨号失败，挂断失败（ret = %d,ret1=%d）", TAG,Tools.getLineInfo(),ret,ret1);
					return ret;
				}
				gui.cls_show_msg1(2, "%s,line %d:建立同步拨号（ATDT%s连接失败%d）", TAG,Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
				return ret;
			}
			ret = modem.clrbuf();
			if ((ret = mdm_detectConnect(modem)) != MODEM_CONNECT_AFTERPREDIAL) 
			{
				gui.cls_show_msg1(2, "line %d:猫未能拨通（DialNum = %s,MdmStatus = %d）", Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
				return ret;
			}
		} catch (RemoteException e) 
		{
			gui.cls_show_msg1(2, "%s，line %d:modem测试失败，抛出异常（%s）", TAG,Tools.getLineInfo(),e.getMessage());
			return MODEM_EXCEPTION_THROW;
		}

		ParaEnum.linkStatus=LinkStatus.linkup;
		return GlobalVariable.SUCC;
	}
	
	// 建立以太网网络连接
	public int netUp_WLM(/*SocketUtil socketUtil,*/MobilePara mobilePara) 
	{
		/*private & local definition*/
		
		/*process body*/
		gui.cls_printf("建立无线网络中。。。".getBytes());
		// 建立socket的网络连接
		netStatus = NetStatus.NETUP;
		return NDK_OK;
	}
	
	// 断开以太网网络连接
	int netDown_WLM(MobilePara mobilePara)
	{
		
		/*process body*/
		gui.cls_printf("断开无线网网络中...".getBytes());
		if( linkDown_WLM(mobilePara)!=NDK_OK)
		{
			netStatus = NetStatus.NETDOWN;
			return NDK_ERR;
		}
		linkStatus = LinkStatus.linkdown;
		netStatus = NetStatus.NETDOWN;
		return NDK_OK;
	}
	
	// 建立无线连接
	int linkUp_WLM(MobilePara mobilePara,LinkType type)
	{
		/*private & local definition*/
		int ret = 0;
		MobileUtil mobileUtil = MobileUtil.getInstance(context,handler);
		
		// 测试前置，关闭其他的网络
		mobileUtil.closeOther();
		
		// 要先关闭其他的网络
//		Log.e(TAG, "enter linkUp_WLM");
		/*process body*/
		// 建立连接
		switch (type) 
		{
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case LTE:
			gui.cls_printf("连接移动网络中...".getBytes());
			mobileUtil.openNet();
			
			int netType = NetworkUtil.waitNetWorkConn(context, NetworkCapabilities.TRANSPORT_CELLULAR,timeout*1000);
			
			if(netType!=ConnectivityManager.TYPE_MOBILE)// 需要修改
			{
				ret = NDK_ERR_TIMEOUT;
				mobileUtil.closeNet();
			}
			else
			{
				ret = NDK_OK;
				gui.cls_printf("移动网络链路层建立成功！！！".getBytes());
			}
				
			break;

		default:
			ret = NDK_ERR;
			break;
		}
		linkStatus = LinkStatus.linkup;
		// 设置好本地ip
		mobilePara.setLocalIp(mobileUtil.getIp());
		return ret;
	}
	
	// 断开无线连接
	int linkDown_WLM(MobilePara mobilePara)
	{
		/*private & local definition*/
		int ret = 0;
		LinkType type = mobilePara.getType();
		MobileUtil mobileUtil = MobileUtil.getInstance(context,handler);
		
		/*process body*/
		linkStatus = LinkStatus.linkdown;
		switch (type) 
		{
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case LTE:
			gui.cls_printf(String.format("断开%s连接中...", type).getBytes());
			mobileUtil.closeNet();
			long startTime = System.currentTimeMillis();
			int netType = -100;
			
			// 循环检测60s
			while(Tools.getStopTime(startTime)<timeout)
			{
				if((netType = NetworkUtil.checkNet(context))==-1)
				{
					gui.cls_printf(String.format("断开%s成功", type).getBytes());
					break;
				}
			}
			if(netType!=-1)
			{
				gui.cls_show_msg1(1, "断开%s失败", type);
				ret = NDK_ERR;
			}
			else
			{
				ret = NDK_OK;
			}
			break;

		default:
			ret = NDK_ERR;
			break;
		}
		linkStatus = LinkStatus.linkdown;
		return ret;
	}
	
	// 建立异步modem连接
	int linkUp_Asyn(NlModemManager modem) 
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		gui.cls_printf(String.format("建立异步拨号连接。。。").getBytes());
		try {
			if((ret = modem.asynInit(ModemBean.MDMPatchType))!=NDK_OK)
			{
				gui.cls_show_msg1(2, "%s,line %d:异步modem初始化失败（ret = %d）", TAG,Tools.getLineInfo(),ret);
				return ret;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1(2, "%s,line %d:异步modem初始化抛出异常（%s）", TAG,Tools.getLineInfo(),e.getMessage());
			return GlobalVariable.FAIL;
		}
		try {
			modem.clrbuf();
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1(2, "%s,line %d:异步modem清空缓存抛出异常（%s）", TAG,Tools.getLineInfo(),e.getMessage());
			return GlobalVariable.FAIL;
		}
		// 拨号
		try {
			if((ret = modem.dial(ModemBean.MDMDialStr))!= GlobalVariable.SUCC)
			{
				modem.hangup();
				gui.cls_show_msg1(2, "%s,line %d:建立异步拨号(ATDT"+ModemBean.MDMDialStr+")连接失败！",TAG,Tools.getLineInfo());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			gui.cls_show_msg1(2, "%s,line %d:异步modem拨号抛出异常（%s）", TAG,Tools.getLineInfo(),e.getMessage());
			return GlobalVariable.FAIL;
		}
		if ((ret = mdm_detectConnect(modem)) != MODEM_CONNECT_AFTERPREDIAL) 
		{
			try {
				modem.hangup();
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1(2, "%s,line %d:异步modem挂断抛出异常（%s）", TAG,Tools.getLineInfo(),e.getMessage());
				return GlobalVariable.FAIL;
			}
			gui.cls_show_msg1(2, "%s,line %d:未能接通(DialNum=%s,MdmStatus=%d)", TAG,Tools.getLineInfo(),ModemBean.MDMDialStr,ret);
			return GlobalVariable.FAIL;
		}
		ParaEnum.linkStatus = LinkStatus.linkup;
		return GlobalVariable.SUCC;
	}
	
	// 断开异步modem连接
	int _linkDown_Asyn(NlModemManager modem) throws RemoteException 
	{
		ParaEnum.linkStatus = LinkStatus.linkdown;
		gui.cls_printf(String.format("断开异步拨号连接中...").getBytes());
		return modem.hangup();
	}
	
	public int linkUp_Eth(EthernetPara ethernetPara)
	{		/*private & local definition*/
		EthernetUtil ethernetUtil = EthernetUtil.getInstance(context,handler);
		
		// 测试前置：关闭其他网络
		ethernetUtil.closeOther();
		

		
		/*process body*/
		gui.cls_printf("建立以太网连接中...".getBytes());
		ethernetUtil.openNet();
		// 判断以太网是否连接成功 20200826
		int netTypeStr = NetworkUtil.waitNetWorkConn(context, NetworkCapabilities.TRANSPORT_ETHERNET, timeout*1000);
		gui.cls_printf(String.format("目前的网络类型=%d",netTypeStr).getBytes());
		if(netTypeStr==ConnectivityManager.TYPE_ETHERNET)
		{
			ethernetPara.setLocalIp(ethernetUtil.getIp());
			linkStatus = LinkStatus.linkup;
			return NDK_OK;
		}
		else
		{
			linkStatus = LinkStatus.linkdown;
			return NDK_ERR;
		}
	}
	
	// add by 20150313
	// 断开以太网链路
	int linkDown_ETH() 
	{
		 /*private & local definition*/

		/* process body */
		gui.cls_printf("断开以太网连接中。。。".getBytes());
		EthernetUtil ethernetUtil = EthernetUtil.getInstance(context,handler);
		int ret;
		
		// 断开以太网
		ethernetUtil.closeNet();
		long startTime = System.currentTimeMillis();
		int netType=-100;
		// 循环检测60s
		while(Tools.getStopTime(startTime)<timeout)
		{
			if((netType = NetworkUtil.checkNet(context))==-1)
			{
				gui.cls_printf(String.format("断开以太网成功").getBytes());
				break;
			}
		}
		if(netType!=-1)
		{
			gui.cls_show_msg1(1, "断开以太网失败");
			ret = NDK_ERR;
		}
		else
		{
			ret = NDK_OK;
		}
		linkStatus = LinkStatus.linkdown;
		return ret;
	}
	// end by 20150313
	
	// add by 20150317
	// 建立以太网的网络层
	public int netUpEth(/*SocketUtil socketUtil,*/EthernetPara ethernetPara) 
	{
		/*private & local definition*/
		/*process body*/
		gui.cls_printf("建立以太网网络中。。。".getBytes());
		
		netStatus = NetStatus.NETUP;
		return NDK_OK;
	}
	// end by 20150317
	
	// add by 20150317
	// 挂断以太网的网络层
	public int netDownEth()
	{
		/*process body*/
		gui.cls_printf("断开以太网网网络中...".getBytes());
		if( linkDown_ETH()!=NDK_OK)
		{
			netStatus = NetStatus.NETDOWN;
			return NDK_ERR;
		}
		netStatus = NetStatus.NETDOWN;
		return NDK_OK;
	}
	// end by 20150317
	/**
	 * 打开wifi 为了测试wifi性能更加准确 应陈镇江要求 把原来打开——扫描——连接 的操作分开计算时间
	 */
	@Override
	public int wifiOpen() {
		int status = 0;
		int ret = 0;
		long startTime = 0;
//		WifiUtil wifiUtil = new WifiUtil(context);
		wifiUtil.openNet();
		gui.cls_printf(String.format("打开wifi完毕").getBytes());
		status = wifiUtil.checkState();
		startTime = System.currentTimeMillis();
		// 还未打开wifi
		switch (wifiUtil.checkState()) {
		case WIFI_STATE_DISABLED:
			while (Tools.getStopTime(startTime) < 10) {
				if ((status = wifiUtil.checkState()) != WIFI_STATE_DISABLED)
					break;
			}
			if (status == WIFI_STATE_DISABLED) {
				gui.cls_show_msg1_record(TAG, "wifiOpen", 10,"line %d:wifi打开失败（status = %d）", Tools.getLineInfo(),ret);
				return FAIL;
			}
			startTime = System.currentTimeMillis();
			// 循环检测wifi是否打开成功
			if (status != WIFI_STATE_ENABLED) {
				// 正在打开wifi
				if (status == WIFI_STATE_ENABLING) {
					while (Tools.getStopTime(startTime) < 10) {
						if ((status = wifiUtil.checkState()) == WIFI_STATE_ENABLED) {
							if(GlobalVariable.isWifiNode==false)
							{
								QuecJNI.openNode();// 打开节点
								GlobalVariable.isWifiNode=true;
							}
							gui.cls_printf("AP打开成功！！！".getBytes());
							break;
						}
					}
					if (status != WIFI_STATE_ENABLED) {
						gui.cls_show_msg1_record(TAG, "wifiOpen", 2,"line %d:wifi打开失败（status = %d）",Tools.getLineInfo(), ret);
						return FAIL;
					}
				}
			}
			break;

		case WIFI_STATE_ENABLED:
			if(GlobalVariable.isWifiNode==false)
			{
				QuecJNI.openNode();// 打开节点
				GlobalVariable.isWifiNode=true;
			}
			ret = SUCC;
			break;
		}
		return 0;
	}


	@Override
	public int wifiNet(WifiPara wifiPara) {
		int netId;
		// 连接wifi ap的操作
		gui.cls_printf(String.format("接入AP（%s）中...", wifiPara.getSsid()).getBytes());
		
		if(wifiPara.isDHCPenable())
		{
			wifiUtil.setSsid(wifiPara.getSsid());
			 WifiConfiguration tempConfig = wifiUtil.IsExsits(wifiPara.getSsid()); 
			 if(tempConfig==null)
			 {
					// 添加wifi ap的网络
				WifiConfiguration wifiConfiguration = wifiUtil.CreateWifiInfo(wifiPara);
				netId=wifiUtil.addNet(wifiConfiguration);
			 }
			 else
			 {
				 netId=tempConfig.networkId;
			 }
			// 动态
			 LoggerUtil.e("wifi id ="+netId);
			try
			{
				boolean connect=wifiUtil.addNetwork(netId,wifiPara);
				if(!connect)
				{
					gui.cls_show_msg1_record(TAG, "linkUp_WLAN", 10, "line %d:连接AP失败(%s)", Tools.getLineInfo(),connect);
					return FAIL;
				}
			}
			catch(Exception e)
			{
				gui.cls_show_msg1_record(TAG, "linkUp_WLAN", 10, "line %d:连接AP失败，抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return FAIL;
			}
			
		}
		else if(!wifiPara.isDHCPenable())
		{
			// 静态
			try 
			{
				boolean connect=wifiUtil.saveStaticWifiConfig(wifiPara, 24);
				if(!connect)
				{
					gui.cls_show_msg1_record(TAG, "linkUp_WLAN", 10, "line %d:连接AP失败(%s)", Tools.getLineInfo(),connect);
					return FAIL;
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "linkUp_WLAN", 10, "line %d:连接AP失败，抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return FAIL;
			} 
		}
		linkStatus = LinkStatus.linkup;
		gui.cls_printf(String.format("wlan链接层建立成功！！！").getBytes());
		return SUCC;
	}

	/**
	 *  建立wifi的连接
	 * @param wifiPara
	 * @return wifi目前的状态
	 */
	public int linkUp_WLAN(WifiPara wifiPara)
	{
		// 测试前置，关闭其他网络
		wifiUtil.closeOther();
		
		
		/*private & local definition*/
		if(wifiOpen()!=SUCC)
			return FAIL;
//		wifiUtil.scanBroadCast(); // Android7.0后开启wifi未接收到扫描广播
		// 连接wifi ap的操作
		if(wifiNet(wifiPara)!=SUCC)
			return FAIL;
		
		wifiPara.setLocalIp(wifiUtil.getIp());
		return SUCC;
	}
	
	

	@Override
	public int wifiDisconnet() {
//		WifiUtil wifiUtil = new WifiUtil(context);
		boolean result=wifiUtil.disconnectWifi();
		if(result!=true){
			gui.cls_printf(String.format("断开wifi失败").getBytes());
			return NDK_ERR;
		}else
			gui.cls_printf(String.format("断开wifi成功").getBytes());
		return SUCC;
	}
	public int WifiClose(){
		long startTime;
		int ret = 0;
//		WifiUtil wifiUtil = new WifiUtil(context);
		//A9 ￥
		SystemClock.sleep(1000);
		/*process body*/
		wifiUtil.closeNet();
		gui.cls_printf(String.format("关闭wifi完毕").getBytes());
		// 检测wifi的当前状态
		switch (wifiUtil.checkState()) 
		{
		case WIFI_STATE_DISABLED:
			gui.cls_printf(String.format("关闭wifi成功").getBytes());
			if(GlobalVariable.isWifiNode==true)
			{
				QuecJNI.closeNode();// 打开节点
				GlobalVariable.isWifiNode = false;
			}
				
			ret = NDK_OK;
			break;

		default:
			startTime = System.currentTimeMillis();
			if(wifiUtil.checkState() != WIFI_STATE_ENABLING)
			{
				while(Tools.getStopTime(startTime)<10)
				{
					if(wifiUtil.checkState() == WIFI_STATE_DISABLED)
					{
						gui.cls_printf(String.format("关闭wifi成功").getBytes());
						ret = NDK_OK;
						break;
					}
					
				}
				if(wifiUtil.checkState() != WIFI_STATE_DISABLED)
				{
					gui.cls_show_msg1(2, "%s,line %d:关闭wifi失败（%d）", TAG,Tools.getLineInfo(),ret);
					ret = NDK_ERR;
				}
			}else{
				gui.cls_show_msg1(2, "%s,line %d:关闭wifi失败（%d）", TAG,Tools.getLineInfo(),ret);
				ret = NDK_ERR;
			}
			break;
		}
		return 0;
	}
	// add by 20150318
	// 断开以太网的链路连接
	public int linkDownWLAN()
	{
		/*private & local definition*/
		if(WifiClose()!=SUCC)
			return FAIL;
		// wifi关闭成功
		linkStatus = LinkStatus.linkdown;
		return SUCC;

	}
	// end by 20150318
	
	// add by 20150318
	// 建立wifi的网络层
	public int netUpWLAN(/*SocketUtil socketUtil,*/WifiPara wifiPara)
	{
		/*private & local definition*/
//		WifiUtil wifiUtil = new WifiUtil(context);
		
		/*process body*/
		gui.cls_printf("建立WLAN网络中...".getBytes());
		// 动态ip
		if(wifiPara.isDHCPenable()==true)
		{
			wifiUtil.getWifiMsg(wifiPara);
		}
		netStatus = NetStatus.NETUP;
		return SUCC;
	}
	// end by 20150318
	
	// add by 20150318
	// 断开wifi的链路层
	public int netDownWLAN(WifiPara wifipara)
	{
		/*private & local definition*/
		int ret = NDK_OK;
		
		/*process body*/
		gui.cls_printf("断开WLAN网络中...".getBytes());
		if(linkDownWLAN()!=NDK_OK)
			ret = NDK_ERR;
		netStatus = NetStatus.NETDOWN;
		return ret;
	}
	// end by 20150318
	
	// 断开连接
	public int linkDown(Object object,LinkType type) 
	{
		/*process body*/
		if(ParaEnum.linkStatus==LinkStatus.linkdown)
			return GlobalVariable.SUCC;
		return _LinkDown(object,type);
	}
	
	// 具体建立哪种连接
	public int _LinkUp(Object object,LinkType type) 
	{
		/* private & local definition */
		int ret = SUCC;
		
		/* process body */
		switch (type) 
		{
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case LTE:
			ret = linkUp_WLM((MobilePara) object,type);
			break;
			
		case ASYN:
			ret =  linkUp_Asyn((NlModemManager) object);
			break;
			
		case SYNC:
			ret = linkUp_SDLC((NlModemManager) object);
			break;
			
		case ETH:
			ret =  linkUp_Eth((EthernetPara) object);
			break;
			
		case WLAN:
			ret =  linkUp_WLAN((WifiPara) object);
			break;
			
		default:
			gui.cls_show_msg1_record(TAG, "_LinkUp", 10,"line %d:建立未知类型（%s）的连接失败", Tools.getLineInfo(),type);
			return FAIL;
		}
		/*if(ret==SUCC)
			gui.cls_printf("链接层建立成功！".getBytes());
		else 
			gui.cls_printf("链接层建立失败！".getBytes());*/
		return ret;
	}
	
	int _LinkDown(Object object,LinkType type) 
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		switch (type) 
		{
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case LTE:
			ret = linkDown_WLM((MobilePara) object);
			break;
		case ASYN:
			try {
				ret = linkDown_ASYN((NlModemManager) object);
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1(2, "%s，line %d:异步modem挂断失败（%s）", TAG,Tools.getLineInfo(),e.getMessage());
			}
			break;
		case SYNC:
			try {
				ret = linkDown_SDLC((NlModemManager) object);
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1(2, "%s，line %d:同步modem挂断失败（%s）", TAG,Tools.getLineInfo(),e.getMessage());
			}
			break;
		case ETH:
			ret = linkDown_ETH();
			break;
		case WLAN:
			ret = linkDownWLAN();
			break;
		default:
//			input(TAG+",line"+Tools.getLineInfo()+":断开未知类型("+type+")的连接失败");
			return GlobalVariable.FAIL;
		}
		gui.cls_printf("断开链路成功".getBytes());
		return ret;
	}
	
	// 断开异步modem连接
	int linkDown_ASYN(NlModemManager modem) throws RemoteException 
	{
		/*private & local definition*/
		
		/*process body*/
		ParaEnum.linkStatus = LinkStatus.linkdown;
		gui.cls_printf("断开异步拨号连接中。。。".getBytes());
		return modem.hangup();
	}
	
	// 断开同步modem连接
	int linkDown_SDLC(NlModemManager modem) throws RemoteException
	{
		/*private & local definition*/
		
		/*process body*/
		ParaEnum.linkStatus = LinkStatus.linkdown;
		gui.cls_printf("断开同步拨号连接中。。。".getBytes());
		return modem.hangup();
	}
	
	
	public int mdm_dial(String phonenum,NlModemManager nlModemManager) 
	{
		/*private & local definition*/
		int ret = 0;
		LinkType type = ModemBean.type_MDM;
		
		/*process body*/
		if(type == LinkType.SYNC || type == LinkType.ASYN)
			gui.cls_printf(String.format("%s步猫拨%s中。。。", (type == LinkType.SYNC)?"同":"异",phonenum).getBytes());
		else
			return NDK_ERR;
		try {
			if((ret = nlModemManager.dial(phonenum))!=NDK_OK)
			{
				gui.cls_show_msg1(10, "%s line %d:dial失败!(%d)", TAG,Tools.getLineInfo(),ret);
				return NDK_ERR;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
			return ret;
		}
		try {
			nlModemManager.clrbuf();
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
			return ret;
		}
		if((ret = mdm_detectConnect(nlModemManager))!= MODEM_CONNECT_AFTERPREDIAL)
		{
			gui.cls_show_msg1(10, "%s line %d:检测到异常状态（%d）", TAG,Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	public int mdm_hangup(NlModemManager nlModemManager) 
	{
		/*private & local definition*/
		int ret = 0;
		LinkType type = ModemBean.type_MDM;
		
		/*process body*/
		if(type == LinkType.SYNC || type == LinkType.ASYN)
			gui.cls_printf(String.format("%s步猫挂机中。。。", (type == LinkType.SYNC)?"同":"异").getBytes());
		else
			return NDK_ERR;
		try {
			ret = nlModemManager.hangup();
		} catch (RemoteException e1) {
			e1.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		if(type == LinkType.SYNC)
			SystemClock.sleep(2000);
		return ret;
	}
	
	public int getmodemreadlenN() 
	{
		/*private & local definition*/
		int oldlen = 0;
		int rdlen = 0;
		long oldtime = 0,interval= 2;
		NlModemManager nlModemManager;
		Gui gui = new Gui(context, handler);
		
		nlModemManager = (NlModemManager) context.getSystemService(NlContext.NLMODEM_SERVICE);
		
		/*process body*/
		oldtime = System.currentTimeMillis();
		while(true)
		{
			try {
				if((rdlen = nlModemManager.getreadlen())<0)
				{
					gui.cls_show_msg1(10, "%s, line %d:接收缓冲区异常(%d)!", TAG,Tools.getLineInfo(),rdlen);
					return rdlen;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				return MODEM_EXCEPTION_THROW;
			}
			
			if(oldlen <rdlen)
			{
				oldlen =rdlen;
				oldtime = System.currentTimeMillis();
			}
			else if(oldlen == rdlen)
			{
				if(Tools.getStopTime(oldtime)>interval)
					return rdlen;
			}
			else
			{
				gui.cls_show_msg1(10, "%s, line %d:接收缓冲区异常!", TAG,Tools.getLineInfo());
				return rdlen;
			}
		}
	}
	
	// add by 20140312
	// 建立连接
	@Override
	public int netUp(/*SocketUtil socketUtil,*/Object object,LinkType type)
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		if((type != LinkType.ASYN && (ret = linkUP(object,type))!= NDK_OK)||(type == LinkType.ASYN && (ret = linkUp4AsynPPP(type))!= NDK_OK))
		{
			gui.cls_show_msg1(10, "%s,line %d:LinkUp失败（%d）", TAG,Tools.getLineInfo(),ret);
			return ret;
		}
		if(NetworkUtil.checkNet(context)==ConnectivityManager.TYPE_WIFI){
			netStatus = NetStatus.NETUP;
			return NDK_OK;
		}
//		if(netStatus == NetStatus.NETUP)
//		{
//			return NDK_OK;
//		}
//		
		transStatus = TransStatus.TRANSDOWN;
		return _netUp(object,type);
	}
	// end by 20150312
	
	

	@Override
	public int linkUp4AsynPPP(LinkType type)
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		if(linkStatus == LinkStatus.linkup)
			return NDK_OK;
		transStatus = TransStatus.TRANSDOWN;
		netStatus = NetStatus.NETDOWN;
		
		if((ret = asynDial4PPP(type))!=NDK_OK)
			return ret;
		linkStatus = LinkStatus.linkup;
		return NDK_OK;
	}

	/*@Override
	public int asynDial4PPP(LinkType type) throws Exception
	{
		private & local definition
		int ret = 0;
		NlModemManager nlModemManager;
		nlModemManager = (NlModemManager) context.getSystemService(Activity.NLMODEM_SERVICE);
		
		process body
//		input("建立异步拨号连接中...");
		
		// 初始化
		if((ret = nlModemManager.asynInit(BpsSetting.MDMPatchType))!=NDK_OK)
		{
			new Gui(context,handler).cls_show_msg1(10, "%s,line %d:异步modem初始化失败！", TAG,Tools.getLineInfo());
			return ret;
		}
		nlModemManager.clrbuf();
		
		// 拨号
		if((ret = mdm_dial_nocheck(BpsSetting.MDMDialStr,type))!=NDK_OK)
		{
			new Gui(context,handler).cls_show_msg1(10, "%s,line %d:建立异步拨号（ATDT%s）连接失败!", TAG,Tools.getLineInfo(),BpsSetting.MDMDialStr);
			nlModemManager.hangup();
			return ret;
		}
		return NDK_OK;
	}*/
	
	/*public int mdm_dial_nocheck(String phonenum,LinkType type) throws Exception
	{
		private & local definition
		int ret = 0;
		NlModemManager nlModemManager;
		nlModemManager = (NlModemManager) context.getSystemService(Activity.NLMODEM_SERVICE);
		
		process body
		if(type == LinkType.SYNC || type == LinkType.ASYN)
		{}
//			input(String.format("%s步猫拨中...", (type == LinkType.SYNC)?"同":"异",phonenum));
		else
			return NDK_ERR;
		
		if((ret = nlModemManager.dial(phonenum))!=NDK_OK)
		{
			new Gui(context,handler).cls_show_msg1(10, "%s,line %d:dial失败！（%d）", TAG,Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
		
		return NDK_OK;
		
	}
*/
	public int _netUp(/*SocketUtil socketUtil,*/Object object,LinkType type) 
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		switch (type) 
		{
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case LTE:
			ret = netUp_WLM(/*socketUtil,*/(MobilePara) object);
			break;
			
		case ASYN:
		case SYNC:
			
			break;
			
		case ETH:
			ret = netUpEth(/*socketUtil,*/(EthernetPara) object);
			break;
			
		case WLAN:
			ret = netUpWLAN(/*socketUtil,*/(WifiPara) object);
			break;
			
		default:
			gui.cls_show_msg1(10, "%s,line %d:建立未知类型（%d）的网络失败！ ", TAG,Tools.getLineInfo(),type);
			return NDK_ERR;
		}
//		inputShort("建立网络层成功", Color.BLACK);
		return ret;
	}

	public int transUp(SocketUtil socketUtil,Sock_t sock_t) 
	{
		/* private & local definition */
		
		/* process body */
		if (transStatus == TransStatus.TRANSUP)
			return NDK_OK;
		return _transUp(socketUtil,sock_t);
	}

	// 传输
	public int _transUp(SocketUtil socketUtil,Sock_t sock_t) 
	{
		/*private & local definition*/
		int ret = NDK_OK;
		
		/*process body*/
		switch (sock_t) 
		{
		case SOCK_TCP:
		case SOCK_UDP:
			try 
			{
				ret = ndkTransUp(socketUtil,sock_t);
			} catch (Exception e) 
			{
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "_transUp", 2, "line %d:建立socket连接抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				return FAIL;
			}
			break;

		default:
			gui.cls_show_msg1_record(TAG, "_transUp",2,"line %d:建立未知类型（%d）传输层失败！",Tools.getLineInfo(),sock_t);
			return FAIL;
		}
		if(ret == SUCC)
		{
			gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "建立传输层成功！！！");
			transStatus = TransStatus.TRANSUP;
		}
		return ret;
	}
	
	public int ndkTransUp(SocketUtil socketUtil,Sock_t sock_t) 
	{
		/*private & local definition*/
		int ret;
		
		/*process body*/
		gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "正在建立socket连接...");
		if(sock_t != Sock_t.SOCK_TCP&& sock_t != Sock_t.SOCK_UDP)
		{
			gui.cls_show_msg1(10, "%s,line %d:套接字类型错误！", TAG,Tools.getLineInfo());
			return NDK_ERR;
		}
		
		if((ret = socketUtil.setSocket(sock_t,gui))!=NDK_OK)
		{
			return ret;
		}
		gui.cls_printf(String.format("WIFI传输层（%s）建立中...", sock_t==Sock_t.SOCK_TCP? "TCP":"UDP").getBytes());
		return NDK_OK;
	}
	
	// 断开socket的连接
	public int ndkTransDown(SocketUtil socketUtil,Sock_t sock_t) throws IOException
	{
		/*private & local definition*/
		int ret = 0;
		/*process body*/
//		inputShort(String.format("NDK传输层（%s）断开中...", sock_t), Color.BLACK);
		if(TCPRESETFLAG == false)
		{
			if(socketUtil!=null)
			{
				if((ret = socketUtil.close(sock_t))!=NDK_OK)
				{
					gui.cls_show_msg1(10, "%s,line %d:tcpClose失败（%d）", TAG,Tools.getLineInfo(),ret);
					return NDK_ERR;
				}
				gui.cls_printf("断开传输层成功中...".getBytes());
				/**socket在close之后服务器并不会马上剔除该Socket,需要延时5s之后等待socket完全的close才可以再次建立连接*/
				/*gui.cls_printf("断开传输层成功!!!等待5s关闭socket中".getBytes());
				SystemClock.sleep(5000);*/
				
			}
		}
		transStatus = TransStatus.TRANSDOWN;
		return ret;
	}

	@Override
	public int netDown(SocketUtil socketUtil,Object object,Sock_t sock_t,LinkType type)
	{
		/*private & local definition*/

		/*process body*/
		transDown(socketUtil,sock_t);
		transStatus = TransStatus.TRANSDOWN;
		
		if(netStatus == NetStatus.NETDOWN)
			return NDK_OK;
		
		return _netDown(object,type);
	}
	
	public int transDown(SocketUtil socketUtil,Sock_t sock_t)
	{
		/*private & local definition*/
		
		/*process body*/
		if(transStatus == TransStatus.TRANSDOWN)
			return NDK_OK;
		
		return _transDown(socketUtil,sock_t);
	}
	
	public int _transDown(SocketUtil socketUtil,Sock_t sock_t)
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		switch (sock_t) 
		{
		case SOCK_TCP:
		case SOCK_UDP:
			try 
			{
				ndkTransDown(socketUtil,sock_t);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		
		case SOCK_SSL:
			
			break;

		default:
			break;
		}
		
		if(ret == NDK_OK)
		{
//			input("断开传输层成功！");
		} else
		{
			gui.cls_show_msg1(5, "%s,line %d:传输层断开失败！", TAG,Tools.getLineInfo());
			return ret;
		}
		transStatus = TransStatus.TRANSDOWN;
		//A9  延时500ms后再关闭WIFI。
		SystemClock.sleep(500);
		return ret;
	}
	
	public int _netDown(Object object,LinkType type)
	{
		/*private & local definition*/
		int ret = 0;
		
		/*process body*/
		switch (type) 
		{
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case LTE:
			ret = netDown_WLM((MobilePara) object);
			break;
		case WLAN:
			ret = netDownWLAN((WifiPara) object);
			break;
		case ASYN:
		case SYNC:
			break;
		case ETH:
			ret= netDownEth();
			break;
					
		default:
			gui.cls_show_msg1(5 ,"%s,line %d:断开未知类型（%s）的网络失败", TAG,Tools.getLineInfo(),type);
			ret = NDK_ERR;
			break;
		}
		if(ret == NDK_OK)
		{
			netStatus = NetStatus.NETDOWN;
			gui.cls_printf("断开网络层成功".getBytes());
		}
		return ret;
	}

	@Override
	public int asynDial4PPP(LinkType type) 
	{
		return 0;
	}
}
