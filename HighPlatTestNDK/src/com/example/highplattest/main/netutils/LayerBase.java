package com.example.highplattest.main.netutils;

import java.util.List;

import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.newland.NlModemManager;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.tools.SocketUtil;

/************************************************************************
 * module 			: main
 * file name 		: LayerBase.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141113
 * directory 		: 
 * description 		: 通讯层接口类
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public interface LayerBase 
{
	/**Modem**/
	// sdlc
	public final int SLEEP_SDLC_HANGUP = 5;
	public final int[] SDLCPCKTHEADER = {0x60,0x80,0x00,0x80,0x00};
	public final int SDLCPCKTHEADERLEN=5;
	
	// asyn
	public final int ASYNPCKTHEADERLEN = 5;
	public final int ASYNRESPMAXLEN = 256;
	public final int ASYNPCKTMAXLEN = 4*1024;
	
	public final int MAXTIMEOUT = 10; // for up/down的超时时间
	public final int TIME4KEEPSTATE = 2;// 确认状态保持的次数
	public final int MAXTIMEOUT_PING =5;
	
	public int netUp(/*SocketUtil socketUtil,*/Object object,LinkType type); 
	public int linkUP(Object object,LinkType type);
	public int linkDown(Object object,LinkType type);
	public int linkUp4AsynPPP(LinkType type);
	public int asynDial4PPP(LinkType type);
	public int transUp(SocketUtil socketUtil,Sock_t sock_t);
	public int transDown(SocketUtil socketUtil,Sock_t sock_t);
	public int netDown(SocketUtil socketUtil,Object object,Sock_t sock_t,LinkType type);
	
	// modem相关的操作
	public int mdm_hangup(NlModemManager nlModemManager);
	public int mdm_dial(String phonenum,NlModemManager nlModemManager);
	public int getmodemreadlenN();
	
	//wifi连接
	public int wifiOpen();
//	//wifi扫描
//	public int scanWifi();
	//wifi连接
	public int wifiNet(WifiPara wifiPara);
	
	public int wifiDisconnet();
}
