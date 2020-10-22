package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.WifiApBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Create;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.util.Log;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest53.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150612
 * directory 		: 
 * description 		: wifi/Ap交叉用例
 * related document : 
 * history 		 	: 变更记录													变更时间			变更人员
 *			  		  修改wifiAP封装方法,增加参数判断显示不同提示语,区分打开和关闭。		   20200426	 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest53 extends DefaultFragment
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest53.class.getSimpleName();
	private final String TESTITEM = "WLAN/AP";
	private final int DEFAULT_CNR_STR = 10;
	
	/*----------global variables declaration------------------------*/
	 private WifiApBean wifiApSetting = new WifiApBean();
	 private EthernetPara ethernetPara = new EthernetPara();
	 private MobilePara mobilePara = new MobilePara();
	 private WifiPara wifiPara = new WifiPara();
	 Gui gui = null;
	 private Config config;
	 
	public void systest53() 
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		initLayer();
		while(true)
		{
			int returnValue=gui.cls_show_msg("WLAN/AP\n0.wifi/Ap交叉\n1.Ap参数设置\n2.共享网络设置\n3.WLAN设置");
			switch (returnValue) 
			{
			case '0':
				if(wifiApSetting.getWifiApShareDev()==Wifi_Ap_Create.ETH)
				{
					wlanApCross(LinkType.ETH);
				}
				else if (wifiApSetting.getWifiApShareDev()==Wifi_Ap_Create.WLM)
				{
					wlanApCross(LinkType.GPRS);
				}
				
				break;
				
			case '1':
				config.confWifiAp(wifiApSetting);
				break;
				
			case '2':
				switch (config.confConnAp(wifiApSetting, ethernetPara, mobilePara)) 
				{
				case NDK_OK:
					
					break;
					
				case NDK_ERR:
				//	handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "line %d:网络未连通", 2);
					gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:网络未连通", Tools.getLineInfo());
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '3':
				switch (config.confConnWlan(wifiPara)) 
				{
				case NDK_OK:
					
					break;
					
				case NDK_ERR:
				//	handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "line %d:网络未连通", 2);
					gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:网络未连通", Tools.getLineInfo());
					break;

				default:
					break;
				}
				break;

			case ESC:
				intentSys();
				return;
			}
		}
	}
		
	// add by 20150612
	// ap test
	public int apTest(LinkType type) 
	{
		/*private & local definition*/
		Object object = null;
		boolean back = false;;
		int ret = -1;
		Log.e("type", type+"");
		
		/*process body*/
		//打开共享网络，建立ETH或者WLM连接
		if(type == LinkType.ETH)
		{
			object = ethernetPara;
		}
		else
		{
			object = mobilePara;
		}
		
		if((ret=layerBase.netUp(object, type))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "apTest",g_keeptime, "line %d:NetUp失败(%d)", Tools.getLineInfo(),ret);
			layerBase.netDown(null, object, null, type);
			return NDK_ERR;
		}
		
		// 打开ap
		if(!(back = setWifiApEnabled(true, wifiApSetting.getWifiApSsid(), wifiApSetting.getWifiApKey(), wifiApSetting.getWifiApSecMode(),wifiApSetting.isWifiApHidden(),true)))
		{
			gui.cls_show_msg1_record(TAG, "apTest",g_keeptime, "line %d:smart激活失败(%d)", Tools.getLineInfo(),back);
			setWifiApEnabled(false, wifiApSetting.getWifiApSsid(), wifiApSetting.getWifiApKey(), wifiApSetting.getWifiApSecMode(),wifiApSetting.isWifiApHidden(),false);
			layerBase.netDown(null, object, null, type);
			return NDK_ERR;
		}
		gui.cls_show_msg("wifi ap打开成功,使用另一台设备进行一次WIFI数据通信后继续测试(APSSID,APKEY):" + wifiApSetting.getWifiApSsid() + wifiApSetting.getWifiApKey()+"，点任意键继续");
		// 关闭wifi热点
		if(!(back = setWifiApEnabled(false, wifiApSetting.getWifiApSsid(), wifiApSetting.getWifiApKey(), wifiApSetting.getWifiApSecMode(),wifiApSetting.isWifiApHidden(),false)))
		{
			gui.cls_show_msg1_record(TAG, "apTest",g_keeptime, "line %d:%s测试失败%s", Tools.getLineInfo(),TAG,back);
			setWifiApEnabled(false, wifiApSetting.getWifiApSsid(), wifiApSetting.getWifiApKey(), wifiApSetting.getWifiApSecMode(),wifiApSetting.isWifiApHidden(),false);
			layerBase.netDown(null, object, null, type);
			return NDK_ERR;
		}
		layerBase.netDown(null, object, null, type);
		return NDK_OK;
	}
	
	public int wifiTest() 
	{
		/*private & local definition*/
		int ret = -1;
		int slen = 0,rlen = 0;
		byte[] buf = new byte[8*1024];
		byte[] rbuf = new byte[8*1024];
		LinkType type = LinkType.WLAN;
		Sock_t sock_t = wifiPara.getSock_t();
		SocketUtil socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		
		/*process body*/
		// 链路连接
		if ((ret=layerBase.netUp(wifiPara, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "wifiTest",g_keeptime, "line %d:NetUp失败(%d)", Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
		// transup
		if ((ret=layerBase.transUp(socketUtil, sock_t)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "wifiTest",g_keeptime, "line %d:TransUp失败(%d)", Tools.getLineInfo(),ret);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			return NDK_ERR;
		}
		
		Arrays.fill(buf, (byte) 0);
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = (byte) (Math.random()*256);
		}
		// 收发数据
		if((slen = sockSend(socketUtil, buf, buf.length, SO_TIMEO, wifiPara))!= buf.length)
		{
			gui.cls_show_msg1_record(TAG, "wifiTest",g_keeptime, "line %d:发送失败（实际%d，预期%d）", Tools.getLineInfo(),slen,PACKMAXLEN);
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			return NDK_ERR;
		}
		
		// 接收数据
		Arrays.fill(rbuf, (byte) 0);
		if((rlen = sockRecv(socketUtil, rbuf, rbuf.length, SO_TIMEO, wifiPara))!=slen)
		{
			gui.cls_show_msg1_record(TAG, "wifiTest",g_keeptime, "line %d:接收失败（实际%d，预期%d）", Tools.getLineInfo(),rlen,slen);
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			return NDK_ERR;
		}
		
		// 比较收发
		if(!Tools.memcmp(buf, rbuf, buf.length))
		{
			gui.cls_show_msg1_record(TAG, "wifiTest",g_keeptime, "line %d:校验失败", Tools.getLineInfo());
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			return NDK_ERR;
		}
		
		//断开传输层
		if((ret=layerBase.transDown(socketUtil, sock_t))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wifiTest",g_keeptime, "line %d:TransDown失败(%d)", Tools.getLineInfo(),ret);
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			return NDK_ERR;
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		return NDK_OK;
	}
	
	public void wlanApCross(LinkType type) 
	{
		/*private & local definition*/
		int bak,succ = 0,i = 0,cnt = DEFAULT_CNR_STR;
		
		/*process body*/		
		// 设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		cnt = bak = packet.getLifecycle();
		
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "正在进行交叉测试,【取消】退出测试...,已成功次数：" + succ, 2)==ESC)
				break;
			cnt--;
			i++;
			// 进行ap测试
			if(apTest(type)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "wlanApCross",g_keeptime, "line %d:第%d次%s测试失败", Tools.getLineInfo(),i,TESTITEM);
				continue;
			}
			// 进行wifi功能测试
			if(wifiTest()!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "wlanApCross",g_keeptime, "line %d:第%d次%s测试失败", Tools.getLineInfo(),i,TESTITEM);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "wlanApCross",g_time_0, "%s测试完成\n总共%d次（成功%d次）", TESTITEM,bak,succ);
	}
	
}
