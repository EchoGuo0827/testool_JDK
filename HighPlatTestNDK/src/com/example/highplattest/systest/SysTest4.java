 package com.example.highplattest.systest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hpsf.Util;

import android.R.integer;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ApplicationExceptionBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.TransStatus;
import com.example.highplattest.main.constant.ParaEnum.WIFI_SEC;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.ExcelUtils;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newland.k21controller.util.Dump;

/**
* related document :
* history 		 	: 变更记录								变更时间			变更人员
*			  		 兼容性测试新增兼容性环境确认，陈长威提出的需求		20200509		郑薛晴
*                   新增获取扫描到的路由器mac地址,陈长威提出的需求            20200515         陈丁
*                   wifi兼容测试新增将测试结果写入excel文件中                 20200616         陈丁
*                   解决X1wifi断开返回-1问题					20200630                      陈丁
*                   根据测试人员建议，将自动填写修改为人工选择并将写入的sheet改为动态获取  20200708 陈丁
*                   根据开发建议，将wifiutil操作全部放到layer中，确保都是同一个对象 20200708 陈丁
*                   解决A9机型扫描wifi无法自动连接问题 				20200730		陈丁
*                   根据测试人员建议修改wifi兼容提示语。去除弹框选择sheet。 20200824    陈丁
* 
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class SysTest4 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest4.class.getSimpleName();
	// wifi名和密码
	private final String TESTITEM = "WLAN性能、压力";
	/*----------global variables declaration------------------------*/
	private int scanPress = 2000;
	private EthernetPara ethernetPara = new EthernetPara();
	private WifiPara wifiPara = new WifiPara();
	private WifiUtil wifiUtil;
	NetWorkingBase[] netWorkingBases = {wifiPara,ethernetPara};
	SocketUtil socketUtil;
	private Gui gui = null;
	Config config=null; 
	AlarmManager am ;
	PowerManager mPowerManager;
    PowerManager.WakeLock mWakeLock;  
    private int flag=-1;   // 0代表是wifi兼容
    int excelsheet=4;  //默认写在2.4HZ的标签页中
    //重启保存使用
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private boolean con=true;
	int ssidrow; //ssid 行数
	private boolean excelflag=false;
	ArrayList<Integer> exceldata=new ArrayList();  
	ArrayList<String> exceldataDouble=new ArrayList();  
	ExcelUtils excelUtils;
	 NumberFormat nf;
	 private int ret=-1;
	public void systest4()
	{
		gui= new Gui(myactivity, handler);
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		excelUtils=new ExcelUtils();
		initLayer();
		wifiUtil=WifiUtil.getInstance(myactivity,handler);
		config=new Config(myactivity,handler);
		am =(AlarmManager)myactivity.getSystemService(Context.ALARM_SERVICE);
		// 电源管理器  
	    mPowerManager=(PowerManager) myactivity.getSystemService(Context.POWER_SERVICE);  
	    
	    sharedPreferences = myactivity.getSharedPreferences("WifiReboot", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			// 配置wifi操作
			config.confConnWlan(wifiPara);
			g_CycleTime = 100;
			socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
			wlan_press();
			return;
		}
		while (true) 
		{
			while(true)
			{
				// 扫描存在的wifi热点;
				int nKeyIn = gui.cls_show_msg("Wlan综合测试\n0.Wlan配置\n1.Wlan压力测试\n2.Wlan性能测试\n3.Wlan异常测试\n4.AP信号强度\n5.AP兼容性测试\n6.漫游测试\n7.信号强度+丢包率(硬件)\n8.获取扫描到的路由器mac地址(无需测试)\n");
				switch (nKeyIn) 
				{
				case '0':
					int nWlanInput = gui.cls_show_msg("Wlan配置\n0.Wlan手动输入\n1.Wlan自动扫描\n");
					switch (nWlanInput) 
					{
					case '0':
						wifiPara.setInput_way(true);
						break;
						
					case '1':
						wifiPara.setInput_way(false);
						wifiPara.setScan_ssid(false);
						break;
					default:
						break;
					}
					switch (new Config(myactivity,handler).confConnWlan(wifiPara)) 
					{
					case NDK_OK:
						socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
						gui.cls_show_msg1(1, "wlan参数配置完毕！！！");
						break;

					case NDK_ERR:
						break;

					case NDK_ERR_QUIT:
					default:
						break;
					}
//					gui.cls_show_msg("AP配置完成");
					break;
					
				case '1':
					wlan_press();
					break;
					
				case '2':
					wlan_ability();
					break;
					
				case '3':
					wlan_abnormity();
					break;
					
				case '4':
					ap_stre_conn();
					break;
					
				case '5':
					int nkey = gui.cls_show_msg("0.兼容性AP测试\n1.AP兼容性环境确认");
					switch (nkey) {
					case '0':
						AP_Compatibility_test();
						break;
						
					case '1':
						compatibilityDataTest();
						break;

					default:
						break;
					}
					break;
					
				case '6':
					manyou_press();
					break;
				case '7':
					wlan_Packetlossrate();
					break;
				case'8':
					get_wifimac();
					break;
					
				case ESC:
					intentSys();
					return;
					
				}
			}
		}
	}

//黑盒人员需求。添加路由器时需要查看该路由器mac地址。无需测试。 by chending 20200515
	private void get_wifimac() {
		// 测试前置，关闭wifi
		wifiUtil.closeNet();
		
		//打开wifi
		wifiUtil.openNet();
		
		gui.cls_show_msg("已打开wifi.请确认wifi已经打开后，按任意键继续");
		
		List<ScanResult> mWifiList_eric=wifiUtil.getWifiList();
		for (int k = 0; k < mWifiList_eric.size(); k++) {
			ScanResult result = mWifiList_eric.get(k);
			String wifimac=result.BSSID;
			String wifiname=result.SSID;
			Log.d("eric_chen", "SSID==="+wifiname);
			Log.d("eric_chen", "BSSID==="+wifimac);
			gui.cls_show_msg("wifi名： %s  wifimac地址： %s", wifiname,wifimac);
		}
		gui.cls_show_msg1_record(TAG,"get_wifimac",g_keeptime, "测试结束。。。");
		
	}


	private void wlan_Packetlossrate() {
		/* private & local definition */
		int i = 0, succ_count = 0, j = 0,ret=0,startLen=0;
		boolean relaykey;
		int slen = 0, rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		int loop=0;
		PacketBean sendPacket = new PacketBean();
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		// 获取信号强度
		int signalStrength = 1;
		int signalStrengthTotal = 0;
		/* process body */
//		if(GlobalVariable.gSequencePressFlag==true)// 判断统一模块内是否连续进行压力测试
//			relaykey = gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "压力中是否要有间隔：[确认]是  [其他]否")==ENTER?true:false;
//		else
		relaykey = gui.cls_show_msg("压力是否要有间隔：[确认]是 [其他]否")==ENTER?true:false;
			
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		int netTime=-1;
//		while(true){
//			if(netTime>5){
//				return;
//			}
      int count=3;
      while (count>0) {
			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
			{
				netTime++;
				count--;
				gui.cls_show_msg1_record(TAG,"connTransPre",g_keeptime, "line %d:第%d次NetUp失败(ret = %d)",Tools.getLineInfo(),netTime,ret);
				continue;
			}else {
				break;
			}
		
	}

//			else
//				break;
//		}
		while (true) 
		{
			
			if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "正在进行第%d次建链/通讯混合压力（已成功%d次），本次将收发%d回，【取消】退出测试...", i + 1,succ_count,(loop = (int) (Math.random()*10+1)))==ESC)
				break;
			
			if (update_snd_packet(sendPacket, type) != SUCC)
				break;
			i++;
			//超过20次失败就不测
			if ((i-succ_count)==20) {
				break;
			}
			if(relaykey==true)
			{
				if(i%5==0)
				{
					j = (int) (Math.random()*4);
					if(j%3==1)
						gui.cls_show_msg1(120, "等待120秒");
					else if(j%3 == 2)
						gui.cls_show_msg1(180, "等待180秒");
					else
						gui.cls_show_msg1(240, "等待240秒");
				}
			}
			// 建立连接
			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次TransUp失败(ret = %d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			for (j = 0; j < loop; j++) 
			{
				// 收发数据
				if ((slen = sockSend(socketUtil, sendPacket.getHeader(),startLen, SO_TIMEO, wifiPara)) != startLen) 
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:第%d次第%d轮发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(),i,j, slen, startLen);
					break;
//					continue;
				}

				// 接收
				if ((rlen = sockRecv(socketUtil, rbuf, startLen,SO_TIMEO, wifiPara)) != startLen) 
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:第%d次第%d轮接收数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),i,j,rlen, startLen);
					break;
//					continue;
				}
				// 比较收发
				if (Tools.memcmp(sendPacket.getHeader(), rbuf,sendPacket.getLen())==false) 
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次第%d轮校验数据失败",Tools.getLineInfo(),i,j);
					break;
//					continue;
				}
			}
			signalStrength = wifiUtil.getSignStrength();
			gui.cls_show_msg1(2, "当前连接的AP的信号强度=" + signalStrength + "范围0到-100；0到-50最好；-50到-70偏差；小于-70最差");
			if(signalStrength<0){
		    signalStrengthTotal=signalStrengthTotal+signalStrength;
			}
			if (slen != sendPacket.getLen() || rlen != sendPacket.getLen()) 
			{
				continue;
			}
			// 挂断
			if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次TransDown失败(ret = %d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			succ_count++;
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		gui.cls_show_msg1_record(TAG,"connTransPre",g_time_0, "连接+数传总次数:%d,成功次数:%d,平均信号强度%ddBm", i, succ_count,signalStrengthTotal/succ_count);
		
	}


	private void manyou_press(){
		/* private & local definition */
		int i = 0, ret = 0;
//		int totalByte=10*1024*1024;
		int onceByte=8*1024;
		int j;
		byte[] buf = new byte[onceByte];
//		int cnt=totalByte/onceByte;
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		/* process body */
		int rlen;
		byte[] rbuf = new byte[onceByte];
		String BSSID="";
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"manyou_press",g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo(),ret);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		// 建立socket这边有1s的硬性时间
		if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"manyou_press",g_keeptime,"line %d:TransUp失败(ret = %d)", Tools.getLineInfo(),ret);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		while (true) 
		{
			for(j=0;j<buf.length;j++){
				buf[j] = (byte) (Math.random()*128);
			}
			if(!BSSID.equals(wifiUtil.getConnetWifiBSSID()))
			{	
				BSSID=wifiUtil.getConnetWifiBSSID();
			}
			// 接收
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"数据发送中...当前路由器MAC:%s【取消键退出】",BSSID)==ESC)
				break;
			if ((rlen = sockSendNoGui(socketUtil, buf, onceByte,SO_TIMEO, wifiPara)) != buf.length) {
				gui.cls_show_msg1_record(TAG, "manyou_press", g_keeptime,"line %d:接收数据失败（实际len = %d，预期len = %d）",Tools.getLineInfo(), rlen, buf.length);
				layerBase.transDown(socketUtil, sock_t);
				while(true)
				{
					if ((ret = layerBase.transUp(socketUtil, sock_t)) == SUCC) 
					{
						if(gui.cls_show_msg1_record(TAG,"manyou_press",g_keeptime,"line %d:TransUp失败(ret = %d)【取消键退出】", Tools.getLineInfo(),ret)==ESC)
							break;
						break;
					}
				}
				continue;
			}
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"数据接收中...当前路由器MAC:%s【取消键退出】",BSSID)==ESC)
				break;
			if((rlen = sockRecvNoGui(socketUtil, rbuf, onceByte, SO_TIMEO, wifiPara))!=rbuf.length){
				gui.cls_show_msg1_record(TAG, "manyou_press", g_keeptime,"line %d:接收数据失败（实际len = %d，预期len = %d）",Tools.getLineInfo(), rlen, rbuf.length);
				layerBase.transDown(socketUtil, sock_t);
				while(true)
				{
					if ((ret = layerBase.transUp(socketUtil, sock_t)) == SUCC) 
					{
						if(gui.cls_show_msg1_record(TAG,"manyou_press",g_keeptime,"line %d:TransUp失败(ret = %d)【取消键退出】", Tools.getLineInfo(),ret)==ESC)
							break;
						break;
					}
				}
				continue;
			}
//			cnt--;
		}
		
		if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"manyou_press",g_keeptime,"line %d:第%d次TransDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		if ((ret = layerBase.netDown(socketUtil, wifiPara, sock_t, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"manyou_press",g_keeptime,"line %d:第%d次NetDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
			return;
		}
		gui.cls_show_msg1_record(TAG,"manyou_press",g_time_0,"漫游测试通过");
	}
	// WIFI压力listview
	private void wlan_press()  
	{
		/*private & local definition*/
		int nkeyIn = 47;
		
		while(true)
		{
			// 连续压力测试
			if(GlobalVariable.gSequencePressFlag)
			{
				if(nkeyIn++ == '7')
				{
					gui.cls_show_msg1_record(TAG, "wlan_press", 2, "%s连续压力测试结束", TESTITEM);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行连续压力测试，【取消】键退出")==ESC)
					return;
			}
			else
			{
				nkeyIn = gui.cls_show_msg("0.开关压力\n1.数传\n2.连接\n3.连接+数传\n4.建链压力\n5.ping压力\n6.扫描AP\n7.Wifi重启连接压力\n8.Wifi重启判断连接通讯压力");
			}
			switch (nkeyIn) 
			{

			case '0':
				switchPre();
				break;
			
			case '1':
				dataTransPre();
				break;

			case '2':
				connPre();
				break;

			case '3':
				connTransPre();
				break;

			case '4':
				setConnPre();
				break;

			case '5':
				pingPre();
				break;

			case '6':
				scanApPre();
				break;
			
			case '7':
				wlanreboot();
				break;
			case '8':
				wlanreboot2();
				break;

			case ESC:
				return;
				
			default:
				break;
			}
		}
	}
/**
 * Wifi重启判断连接通讯压力
 * 
 */
	private void wlanreboot2() {
		// TODO Auto-generated method stub
		int i = 0, succ_count = 0, j = 0,ret=0,startLen=0;
		boolean relaykey;
		int slen = 0, rlen = 0;
		String SSID="";
		String  IP="";
	
		byte[] buf = new byte[PACKMAXLEN];
		int loop=0;
		PacketBean sendPacket = new PacketBean();
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		init_snd_packet(sendPacket, buf);
//		set_snd_packet(sendPacket, type);
		
		sendPacket.setLifecycle(getCycleValue());
		sendPacket.setLen(getCommPackLen());
		sendPacket.setOrig_len(getCommPackLen());
		
		
		sendPacket.setLen(PACKMAXLEN);
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		//重启次数获取
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		int rebootcount2 = packet.getLifecycle();
		gui.cls_show_msg( "先进行一次wifi连接通讯后机器重启。请确保已经进行过wifi配置。任意键继续");
		//连接网络
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlanreboot2",g_keeptime, "line %d:NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
			return;
		}
		if (update_snd_packet(sendPacket, type) != SUCC){
			gui.cls_show_msg1_record(TAG,"wlanreboot2",g_keeptime, "line %d:添加包头失败(ret = %d)",Tools.getLineInfo(),ret);
			return;
		}
		// 建立连接
		if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlanreboot2", g_keeptime, "line %d:TransUp失败(ret = %d)",Tools.getLineInfo(),i,ret);
			return;
		}
		// 收发数据
		if ((slen = sockSend(socketUtil, sendPacket.getHeader(),startLen, SO_TIMEO, wifiPara)) != startLen) 
		{
			layerBase.transDown(socketUtil, sock_t);
			gui.cls_show_msg1_record(TAG,"wlanreboot2", g_keeptime,"line %d:发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(),i,j, slen, startLen);
			return;
		}
		
		// 接收
		if ((rlen = sockRecv(socketUtil, rbuf, startLen,SO_TIMEO, wifiPara)) != startLen) 
		{
			layerBase.transDown(socketUtil, sock_t);
			gui.cls_show_msg1_record(TAG,"wlanreboot2", g_keeptime,"line %d:接收数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),i,j,rlen, startLen);
			return;
		}
		LoggerUtil.e("sendPacket.getHeader():"+Dump.getHexDump(sendPacket.getHeader()));
		LoggerUtil.e("rbuf:"+Dump.getHexDump(rbuf));
		LoggerUtil.e("sendPacket.getLen():"+sendPacket.getLen());
		
		// 比较收发
		if (Tools.memcmp(sendPacket.getHeader(), rbuf,sendPacket.getLen())==false) 
		{
			layerBase.transDown(socketUtil, sock_t);
			gui.cls_show_msg1_record(TAG,"wlanreboot2", g_keeptime, "line %d:校验数据失败",Tools.getLineInfo(),i,j);
			return;
		}
		gui.cls_show_msg1_record(TAG,"wlanreboot2", 2, "当前选择的重启压力次数为%d(当前执行--Wifi重启判断连接通讯压力)",rebootcount2);
		//记录连接的SSID和IP和压力次数   
		//测试人员需求：不需要记录连接的SSID 和IP进行对比
//		WifiUtil wifiUtil2=new WifiUtil(myactivity);
//		SSID= wifiUtil2.getSSID();
//		IP=wifiUtil2.getIp();
		Log.d("eric_chen", "SSID==="+SSID);
		Log.d("eric_chen", "IP==="+IP);
		editor.putInt("wifireboot2", rebootcount2);
		editor.commit();
//		editor.putString("SSID", SSID);
//		editor.commit();
//		editor.putString("IP", IP);
//		editor.commit();
	      for (int k = 5; k >0; k--) {
	 		 gui.cls_show_msg1(1, "即将自动重启-----还有%d秒",k);
	 	}
	 		reboot(myactivity);
		
		
	}


	/**
	 * 机器重启判断wifi状态压力
	 */
	private void wlanreboot() {
		// TODO Auto-generated method stub
		int i = 0, succ_count = 0, j = 0,ret=0,startLen=0;
		boolean relaykey;
		int slen = 0, rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		int loop=0;
		PacketBean sendPacket = new PacketBean();
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		Log.d("eric_chen", "执行连接+数传压力---------------------");
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		//重启次数获取
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		int rebootcount = packet.getLifecycle();
		
		gui.cls_show_msg( "先进行一次wifi连接后机器重启。请确保已经进行过wifi配置。任意键继续");
		//连接网络
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlanreboot",g_keeptime, "line %d:NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
			return;
		}
		if (update_snd_packet(sendPacket, type) != SUCC){
			gui.cls_show_msg1_record(TAG,"wlanreboot",g_keeptime, "line %d:添加包头失败(ret = %d)",Tools.getLineInfo(),ret);
			return;
		}
		// 建立连接
		if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlanreboot", g_keeptime, "line %d:TransUp失败(ret = %d)",Tools.getLineInfo(),i,ret);
			return;
		}
		// 收发数据
		if ((slen = sockSend(socketUtil, sendPacket.getHeader(),startLen, SO_TIMEO, wifiPara)) != startLen) 
		{
			layerBase.transDown(socketUtil, sock_t);
			gui.cls_show_msg1_record(TAG,"wlanreboot", g_keeptime,"line %d:发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(),i,j, slen, startLen);
			return;
		}
		
		// 接收
		if ((rlen = sockRecv(socketUtil, rbuf, startLen,SO_TIMEO, wifiPara)) != startLen) 
		{
			layerBase.transDown(socketUtil, sock_t);
			gui.cls_show_msg1_record(TAG,"wlanreboot", g_keeptime,"line %d:接收数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),i,j,rlen, startLen);
			return;
		}
		// 比较收发
		if (Tools.memcmp(sendPacket.getHeader(), rbuf,sendPacket.getLen())==false) 
		{
			layerBase.transDown(socketUtil, sock_t);
			gui.cls_show_msg1_record(TAG,"wlanreboot", g_keeptime, "line %d:校验数据失败",Tools.getLineInfo(),i,j);
			return;
		}
		//将重启次数写入文件
		gui.cls_show_msg1_record(TAG,"wlanreboot", 2, "当前选择的重启压力次数为%d",rebootcount);
		//将数据存入数据库
		editor.putInt("wifireboot", rebootcount);
		editor.commit();
		
      for (int k = 5; k >0; k--) {
		 gui.cls_show_msg1(1, "即将自动重启-----还有%d秒",k);
	}
		reboot(myactivity);
	}

	//重启方法
	public void reboot(Context context)
	
	{	
		PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
		pm.reboot(null); 

	}
	/**
	 *  wifi开关压力
	 */
	private void switchPre()
	{
		/*private & local definition*/
		int cnt = 0,i = 0,succ = 0;
		PacketBean packet = new PacketBean();
		int status = -10086;
		
		/* process body */
		if(GlobalVariable.gSequencePressFlag)
			cnt = getCycleValue();
		else
		{
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 100));// 默认次数100次
			cnt = packet.getLifecycle();
		}
		// 测试前置，关闭wifi
		wifiUtil.closeNet();
		
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "正在进行第%d次开关压力(已成功%d次),[取消]退出测试...", i + 1, succ)==ESC)
				break;
			cnt--;
			i++;
			wifiUtil.openNet();
			int time=0;
//			long oldtime2=SystemClock.currentThreadTimeMillis();
			while (time++<100) {
//				time=(int) Tools.getStopTime(oldtime2);
				if ((status = wifiUtil.checkState())==WifiManager.WIFI_STATE_ENABLED) {
					break;
				}
				SystemClock.sleep(100);
			}
			
			if (time>=100) {
			gui.cls_show_msg1_record(TAG, "switchPre",g_keeptime, "line %d:第%d次打开wifi超时失败(status = %s)", Tools.getLineInfo(),i,status);
				continue;
				
			}
//			if((status = wifiUtil.checkState())!=WifiManager.WIFI_STATE_ENABLED)
//			{
//				gui.cls_show_msg1_record(TAG, "switchPre",g_keeptime, "line %d:第%d次打开wifi失败(status = %s)", Tools.getLineInfo(),i,status);
//				continue;
//			}
			status=-10086;
			wifiUtil.closeNet();
//			SystemClock.sleep(2000);
			
			
			time=0;
//			long oldtime=SystemClock.currentThreadTimeMillis();
			
			while (time++<100) {
//				time=(int) Tools.getStopTime(oldtime);
				if ((status = wifiUtil.checkState())==WifiManager.WIFI_STATE_DISABLED) {
					break;
				}
				SystemClock.sleep(100);
			}
			
			if (time>=100) {
				gui.cls_show_msg1_record(TAG, "switchPre",g_keeptime, "line %d:第%d次关闭wifi超时失败", Tools.getLineInfo(),i);
				continue;
				
			}
//			if((status = wifiUtil.checkState())!=WifiManager.WIFI_STATE_DISABLED)
//			{
//				gui.cls_show_msg1_record(TAG, "switchPre",g_keeptime, "line %d:第%d次关闭wifi失败(status = %s)", Tools.getLineInfo(),i,status);
//				continue;
//			}
			
			succ++;
		}
		// 测试后置：关闭wlan
		wifiUtil.closeNet();
		gui.cls_show_msg1_record(TAG, "switchPre",g_time_0, "Wlan开关压力总次数:%d，成功次数%d", i,succ);
		
	}

	/**
	 *  数传压力
	 */
	private void dataTransPre()  
	{
		/*private & local definition*/
		int nkeyin = 47,ret = 0;
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();

		/* process body */
		while (true) 
		{
			if (GlobalVariable.gSequencePressFlag) 
			{
				/*if (++nkeyin == '3')
					return;*/
			//	nkeyin='0';
				if(++nkeyin > '0'){
					return;
				}
				gui.cls_show_msg1(1, "要测试单向发送、单向接收压力需关闭连续压力开关进行");
			} else 
			{
				nkeyin = gui.cls_show_msg("数传压力\n0.双向收发压力\n1.单向接收压力\n2.单向发送压力\n");
			}
			switch (nkeyin) 
			{
			case '0':
				// 链路连接
				if ((ret = layerBase.netUp(wifiPara, wifiPara.getType())) != SUCC) 
				{
					gui.cls_show_msg1_record(TAG,"dataTrans", g_keeptime, "line %d:WIFI_NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
					continue;
				}
				
				LoggerUtil.d("385----netUp");

				if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
				{
					layerBase.netDown(socketUtil, wifiPara, sock_t, type);
					gui.cls_show_msg1_record(TAG,"dataTrans", g_keeptime, "line %d:WIFI_TransUp失败(ret = %d)",Tools.getLineInfo(),ret);
					continue;
				}
				send_recv_press(socketUtil, type, wifiPara);
				layerBase.transDown(socketUtil, sock_t);
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				break;
				
			case '1':
				// 链路连接
				if ((ret = layerBase.netUp(wifiPara, wifiPara.getType()))!= SUCC) 
				{
					gui.cls_show_msg1_record(TAG,"dataTrans", g_keeptime, "line %d:WIFI_NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
					continue;
				}
				if ((ret = layerBase.transUp(socketUtil, sock_t))!= SUCC) 
				{
					layerBase.netDown(socketUtil, wifiPara, sock_t, type);
					gui.cls_show_msg1_record(TAG,"dataTrans", g_keeptime, "line %d:WIFI_TransUp失败(ret = %d)",Tools.getLineInfo(),ret);
					continue;
				}
				recvOnlyPress(socketUtil, type, wifiPara);
				layerBase.transDown(socketUtil, sock_t);
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				break;
				
			case '2':
				// 链路连接
				if ((ret = layerBase.netUp(wifiPara, wifiPara.getType())) != SUCC) 
				{
					gui.cls_show_msg1_record(TAG,"dataTrans", g_keeptime, "line %d:WIFI_NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
					continue;
				}

				if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
				{
					layerBase.netDown(socketUtil, wifiPara, sock_t, type);
					gui.cls_show_msg1_record(TAG,"dataTrans", g_keeptime, "line %d:TransUp失败(ret = %d)",Tools.getLineInfo(),ret);
					continue;
				}
				sendOnlyPress(socketUtil, type, wifiPara);
				layerBase.transDown(socketUtil, sock_t);
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				break;
				
			case ESC:
				return;

			default:
				break;
			}

		}
	}
	
	/**
	 *  连接压力
	 */
	private void connPre()  
	{
		/* private & local definition */
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();
		int i = 0, succ_count = 0,ret = 0;

		/* process body */
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"connPre",g_keeptime, "line %d:NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
			return;
		}
		while (true) 
		{
			if(gui.cls_show_msg1(1, "正在进行第%d次连接压力（已成功%d次），【退出】退出测试...", i + 1, succ_count)==ESC)
				break;

			if (GlobalVariable.gSequencePressFlag&&getCycleValue()==i) // 自动压力测试次数达到后退出
					break;
			i++;
			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"connPre", g_keeptime, "line %d:第%d次TransUp失败(ret = %d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"connPre", g_keeptime, "line %d:第%d次TransDown失败(ret = %d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			succ_count++;
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		gui.cls_show_msg1_record(TAG, "connPre",g_time_0,"连接压力总次数：%d  成功次数：%d", i, succ_count);
	}

	/**
	 *  连接+数传压力
	 */
	private void connTransPre()  
	{
		/* private & local definition */
		int i = 0, succ_count = 0, j = 0,ret=0,startLen=0;
		boolean relaykey;
		int slen = 0, rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		int loop=0;
		PacketBean sendPacket = new PacketBean();
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		Log.d("eric_chen", "执行连接+数传压力---------------------");
		/* process body */
//		if(GlobalVariable.gSequencePressFlag==true)// 判断统一模块内是否连续进行压力测试
//			relaykey = gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "压力中是否要有间隔：[确认]是  [其他]否")==ENTER?true:false;
//		else
			relaykey = gui.cls_show_msg("压力是否要有间隔：[确认]是 [其他]否")==ENTER?true:false;
			
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		
		//测试人员反馈只需要测试4K数据     by chending 20200426
		sendPacket.setLen(PACKMAXLEN);
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		int netTime=-1;
//		while(true){
//			if(netTime>5){
//				return;
//			}
      int count=3;
      while (count>0) {
			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
			{
				netTime++;
				count--;
				gui.cls_show_msg1_record(TAG,"connTransPre",g_keeptime, "line %d:第%d次NetUp失败(ret = %d)",Tools.getLineInfo(),netTime,ret);
				continue;
			}else {
				break;
			}
			
		
	}
      if (count<=0) {
    	  con=false;
    	  return;
	}

		while (true) 
		{
			
			if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "正在进行第%d次建链/通讯混合压力（已成功%d次），本次将收发%d回，【取消】退出测试...", i + 1,succ_count,(loop = (int) (Math.random()*10+1)))==ESC)
				break;
			
			if (update_snd_packet(sendPacket, type) != SUCC)
				break;
			i++;
			//超过20次失败就不测
			if ((i-succ_count)==20) {
				break;
			}
			if(relaykey==true)
			{
				if(i%5==0)
				{
					j = (int) (Math.random()*4);
					if(j%3==1)
						gui.cls_show_msg1(120, "等待120秒");
					else if(j%3 == 2)
						gui.cls_show_msg1(180, "等待180秒");
					else
						gui.cls_show_msg1(240, "等待240秒");
				}
			}
			// 建立连接
			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次TransUp失败(ret = %d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			for (j = 0; j < loop; j++) 
			{	
				
				Log.d("eric_chen", "startLen:  "+startLen);
				Log.d("eric_chen", "sendPacket.getHeader():  "+sendPacket.getHeader().length);
				LoggerUtil.e("eric_chen:"+Dump.getHexDump(sendPacket.getHeader()));
				// 收发数据
				if ((slen = sockSend(socketUtil, sendPacket.getHeader(),startLen, SO_TIMEO, wifiPara)) != startLen) 
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:第%d次第%d轮发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(),i,j, slen, startLen);
					break;
//					continue;
				}

				// 接收
				if ((rlen = sockRecv(socketUtil, rbuf, startLen,SO_TIMEO, wifiPara)) != startLen) 
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:第%d次第%d轮接收数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),i,j,rlen, startLen);
					break;
//					continue;
				}
				// 比较收发
				if (Tools.memcmp(sendPacket.getHeader(), rbuf,sendPacket.getLen())==false) 
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次第%d轮校验数据失败",Tools.getLineInfo(),i,j);
					break;
//					continue;
				}
			}
			if (slen != sendPacket.getLen() || rlen != sendPacket.getLen()) 
			{
				continue;
			}
			// 挂断
			if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次TransDown失败(ret = %d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			succ_count++;
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		if (excelflag) {
			exceldata.add(succ_count);
		}
		gui.cls_show_msg1_record(TAG,"connTransPre",g_time_0, "连接+数传总次数:%d,成功次数:%d", i, succ_count);
	}

	/**
	 *  建链压力
	 */
	private void setConnPre()  
	{
		/* private & local definition */
		int i = 0, succ = 0,ret = 0;
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();
		Log.d("eric_chen", "执行建链压力---------------------");
		/* process body */
		while (true) 
		{
			if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS,"正在进行第%d次建链压力（已成功%d次），【取消】退出测试...", i + 1, succ)==ESC)
				break;
			if (GlobalVariable.gSequencePressFlag&&getCycleValue()==i) 
					break;
			i++;
			if ((i-succ)==20) {
				break;
			}
			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"setConnPre", g_keeptime, "line %d:第%d次NetUp失败(ret = %d)",Tools.getLineInfo(), i,ret);
				continue;
			}
			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"setConnPre", g_keeptime, "line %d:第%d次TransUp失败(ret = %d)",Tools.getLineInfo(), i,ret);
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				continue;
			}
			if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"setConnPre", g_keeptime, "line %d:第%d次TransDown失败(ret = %d)",Tools.getLineInfo(), i,ret);
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				continue;
			}
			if ((ret = layerBase.netDown(socketUtil, wifiPara, sock_t, type)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"setConnPre", g_keeptime, "line %d:第%d次NetDown失败(ret = %d)",Tools.getLineInfo(), i,ret);
				continue;
			}
			succ++;
		}
		if (excelflag) {
			exceldata.add(succ);
		}
		gui.cls_show_msg1_record(TAG,"setConnPre",g_time_0, "建链压力总次数：%d，成功次数：%d", i, succ);
	}

	/**
	 *  ping压力
	 */
	private void pingPre() 
	{
		int cnt, succ = 0, i = 0,ret = 0;
		float time;
		final PacketBean packet = new PacketBean();
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();

		if(GlobalVariable.gSequencePressFlag)
			cnt = getCycleValue();
		else
		{
			// 设置压力次数
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 100));
			cnt = packet.getLifecycle();
		}

		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"pingPre", g_keeptime, "line %d:NetUp失败(ret = %d)",Tools.getLineInfo(),ret);
			return;
		}
		while (cnt > 0) {
			if(gui.cls_show_msg1(1, "ping压力测试中...\n还剩%d次（已成功%d次），【取消】退出测试...", cnt, succ)==ESC)
				break;

			cnt--;
			i++;
			// ping 压力测试，ping百度的IP
		
			try 
			{
				NetworkUtil.pingTest("202.108.22.5",TIMEOUT_NET);
			} catch (ApplicationExceptionBean e) 
			{
				gui.cls_show_msg1_record(TAG, "pingPre", g_keeptime, "line %d:第%d次ping网络失败（%s）", Tools.getLineInfo(),i,e.getMessage());
				continue;
			}
			
			succ++;
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		gui.cls_show_msg1_record(TAG, "wifi_press",g_time_0,"ping压力测试%d次,成功%d次", i, succ);
	}

	/**
	 *  扫描压力
	 */
	private void scanApPre() {
		/* private & local definition */
		int succ = 0, i = 0, cnt = scanPress, j = 0;
		PacketBean sendPacket = new PacketBean();

		/* process body */
		// 设置压力次数
		if(GlobalVariable.gSequencePressFlag)
			cnt = getCycleValue();
		else
		{
			sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, scanPress));
			cnt = sendPacket.getLifecycle();
		}

		// 测试前置
		wifiUtil.openNet();
		wifiUtil.closeOther();
		SystemClock.sleep(3000);
		i = 0;
		while (true) 
		{
			if(gui.cls_show_msg1(1,"Wifi扫描压力测试中\n总共%d次，还剩%d次，成功%d次，【取消】退出测试...", cnt, cnt- i, succ)==ESC)
				break;
			if (i++ == cnt)
				break;
			for (j = 0; j < 3; j++) 
			{
				if (wifiUtil.startScan(wifiPara))
					break;
				SystemClock.sleep(1000);
			}
			// 连续三次失败则退出
			if (j == 3) 
			{
				if(gui.cls_show_msg1_record(TAG,"scanApPre", 3, "line %d:%s第%d次测试失败，[取消]退出测试，[其他]继续扫描",Tools.getLineInfo(), TESTITEM, i)==ESC);
					return;
			}
			succ++;
		}
		wifiUtil.closeNet();
		gui.cls_show_msg1_record(TAG,"scanApPre",g_time_0, "wifi扫描压力测试%d次,成功%d次", i - 1, succ);
	}

	public void createWlanAbilityPacket(PacketBean packet, byte[] buf) 
	{
		/* private & local definition */

		/* process body */
		packet.setHeader(buf);
		packet.setLen(PACKMAXLEN);
		packet.setOrig_len(PACKMAXLEN);
		packet.setLifecycle(20);
		packet.setForever(false);
		packet.setIsLenRec(false);
		packet.setIsDataRnd(true);
		return;
	}
	/**
	 * 发送速率改成一次发送10M测试
	 * modifed by zhangxinj on 2017/10/26
	 * @param type
	 * @param sendPacket
	 */
	private void wlan_ability_send(LinkType type){
		int i;
		float writeTime = 0;
		long startTime = 0;
		//10M的数据
		int startLen=10*1024*1024;
		int slen=0;
		byte[] buf = new byte[8*1024];
	
		//配置单向发送IP和端口号
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().configSendRecv(myactivity, wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return;
		int time = startLen / buf.length;//1280次
		
		//绑定服务器IP和端口
		SocketUtil socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		if(layerBase.netUp(wifiPara, type)!=NDK_OK)
		{
			layerBase.linkDown(wifiPara, type);
			gui.cls_show_msg1_record(TAG, "wlan_ability_send", g_keeptime,"line %d:单向发送NetUp失败", Tools.getLineInfo());
			return;
		}
		if(layerBase.transUp(socketUtil, wifiPara.getSock_t())!=NDK_OK)
		{
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), type);
			gui.cls_show_msg1_record(TAG, "wlan_ability_send", g_keeptime,"line %d:单向发送TransUp失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(2,"正在进行单向发送性能测试...10M数据测试时间较长,请耐心等待");
		startTime = System.currentTimeMillis();
		for(int j=0;j<time;j++){
			//初始化发送数据
			byte[] ptr = new byte[buf.length];
			for (i = 0; i < buf.length; i++) 
			{
				ptr[i] = (byte) (Math.random()*128);
			}
			// 收发数据
			if ((slen = sockSend(socketUtil, ptr,buf.length, SO_TIMEO, wifiPara)) != buf.length) 
			{
				layerBase.transDown(socketUtil, wifiPara.getSock_t());
				gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(), slen, buf.length);
				return;
			}
		}
		
		writeTime=writeTime+Tools.getStopTime(startTime);
		//速率单位KB/s
		float sendRate=startLen/1024/writeTime;
		if(layerBase.transDown(socketUtil, wifiPara.getSock_t())!=NDK_OK)
		{
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), type);
			gui.cls_show_msg1_record(TAG, "wlan_ability_send", g_keeptime,"line %d单向发送TransDown失败", Tools.getLineInfo());
			return;
		}
		if(layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wlan_ability_send", g_keeptime,"line %d:NetDown失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "wlan_ability_send", g_time_0,"发送数率:%fKB/s", sendRate);
	}

	private void wlan_ability_recv(LinkType type) {
		int j;
		long startTime;
		float readTime = 0;
		int startLen=10*1024*1024;
		int rlen=0;
		float recvRate=0;
		//配置单向发送IP和端口号
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().configSendRecv(myactivity, wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return;
		
		//单向接收数据的时候，第一次发送给服务器的数据
		byte[] buf = new byte[4*1024];
		byte[] rbuf = new byte[4*1024];
	
		for(j=0;j<buf.length;j++){
			buf[j] = (byte) (Math.random()*128);
		}
		//绑定服务器IP和端口
		SocketUtil socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		
		if(layerBase.netUp(wifiPara, type)!=NDK_OK)
		{
			layerBase.linkDown(wifiPara, type);
			gui.cls_show_msg1_record(TAG, "wlan_ability_recv", g_keeptime,"line %d:单向发送NetUp失败", Tools.getLineInfo());
			return;
		}
		
		if (layerBase.transUp(socketUtil, wifiPara.getSock_t()) != NDK_OK) {
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(),type);
			gui.cls_show_msg1_record(TAG, "wlan_ability_recv", g_keeptime,"line %d:单向接收TransUp失败", Tools.getLineInfo());
			return;
		}
		// 开始通讯
		// 先发送数据到服务器，服务器会回数据回来
		sockSend(socketUtil, buf, buf.length, 0, wifiPara);
		gui.cls_show_msg1(1, "正在进行单向接收接收性能测试...测试10M数据时间较长，请耐心等待");
		
		int onceTime = startLen / rbuf.length;
		int onceLeft = startLen % rbuf.length;
		Arrays.fill(rbuf, (byte) 0);
		gui.cls_show_msg1(1, "数据接收中...\n%s:%d<-%s:%d",wifiPara.getLocalIp(),wifiPara.getLocalPort(),wifiPara.getServerIp(),wifiPara.getServerPort());
		startTime = System.currentTimeMillis();
		for (j = 0; j < onceTime; j++) {
			// 接收
			if ((rlen = sockRecv(socketUtil, rbuf, rbuf.length,0, wifiPara)) != rbuf.length) {
				layerBase.netDown(socketUtil, wifiPara,wifiPara.getSock_t(), type);
				gui.cls_show_msg1_record(TAG, "wlan_ability_recv", g_keeptime,"line %d:接收数据失败（实际len = %d，预期len = %d）",Tools.getLineInfo(), rlen, startLen);
				return;
			}
		}

		if (onceLeft > 0) {
			byte[] left = new byte[onceLeft];
			sockRecv(socketUtil, left, left.length, 0, wifiPara);
		}
		readTime = readTime + Tools.getStopTime(startTime);
		recvRate = startLen/1024/ readTime ;
		if (layerBase.transDown(socketUtil, wifiPara.getSock_t()) != NDK_OK) {
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(),type);
			gui.cls_show_msg1_record(TAG, "wlan_ability_recv", g_keeptime,"line %d:单向接收TransDown失败", Tools.getLineInfo());
			return;
		}
		if (layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(),type) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wlan_ability_recv", g_keeptime,"line %d:NetDown失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "wlan_ability_recv", g_time_0,"接收速率:%fKB/s\n", recvRate);
	}
	/**
	 *  wifi性能测试
	 */
	private void wlan_ability() 
	{
		//获取wifi type
		LinkType type = wifiPara.getType();
		int nKeyIn = gui.cls_show_msg("Wifi性能测试\n0.发送速率\n1.接收速率\n2.双向通讯速率\n3.打开连接扫描断开关闭速率\n");
		switch (nKeyIn) 
		{
		case '0':
			wlan_ability_send(type);
			break;
			
		case '1':
			wlan_ability_recv(type);
			break;
		case '2':
			//双向通讯速率
			 wlan_ability_send_recv();
			 break;
		case '3':
			wlan_ability_open_net_scan_disnet_close();
			break;
//		case '4':
//			wlan_ability_scan();
//			break;
		case ESC:
			return;
		}
	}

	/**
	 *  wifi性能测试 暂时留着 最原始的
	 */
//	private void wlan_ability_send_recv() 
//	{
//		/* private & local definition */
//		int i = 0, SO_TIMEO = SO_TIMEO, ret = 0;
//		float recvtimes = 0, preconnectTime = 0, freetimes = 0,sendtimes = 0;
//		byte[] buf = new byte[PACKMAXLEN];
//	
//		Packet sendPacket = new Packet();
//		long startTime;
//		LinkType type = wifiPara.getType();
//		Sock_t sock_t = wifiPara.getSock_t();
//		/* process body */
//		createWlanAbilityPacket(sendPacket, buf);
//		set_snd_packet(sendPacket, type);
//		//临时
//		sendPacket.setLen(8*1024);
//		sendPacket.setOrig_len(8*1024);
//		//临时
//		int startLen = sendPacket.getLen();
//		byte[] rbuf = new byte[startLen];
//		while (true) 
//		{
//			if (update_snd_packet(sendPacket, type) != NDK_OK)
//				break;
//			i++;
//			startTime = System.currentTimeMillis();
//			// linkUp显示耗时1s
//			if ((ret = layerBase.linkUP(wifiPara, type)) != SUCC) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次LinkUp失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				layerBase.linkDown(wifiPara, type);
//				return;
//			}
//			preconnectTime = preconnectTime + Tools.getStopTime(startTime);
//			// netUp没有显示耗时
//			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次NetUp失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				layerBase.linkDown(wifiPara, type);
//				return;
//			}
//			// 建立socket这边有1s的硬性时间
//			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次TransUp失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
//				layerBase.linkDown(wifiPara, type);
//				return;
//			}
//
//			// 数据发送显示耗时1s
//			startTime = System.currentTimeMillis();
//			sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(),SO_TIMEO, wifiPara);
//			Arrays.fill(rbuf, (byte) 0);
//			//sendtimes = sendtimes+Tools.getStopTime(startTime)-1;
//			
//			// 接收显示耗时1s
//			//startTime = System.currentTimeMillis();
//			sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, wifiPara);
//			recvtimes = (float) (recvtimes + Tools.getStopTime(startTime)-0.2);
//
//			if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlanAbilityczj",g_keeptime,"line %d:第%d次TransDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
//				layerBase.linkDown(wifiPara, type);
//				return;
//			}
//
//			startTime = System.currentTimeMillis();
//			// 链路层断开没有显示耗时 1s
//			if ((ret = layerBase.linkDown(wifiPara, type)) != SUCC) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlanAbilityczj",g_keeptime,"line %d:第%d次LinkDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
//				return;
//			}
//			freetimes = freetimes + Tools.getStopTime(startTime);
//
//			if ((ret = layerBase.netDown(socketUtil, wifiPara, sock_t, type)) != SUCC) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlanAbilityczj",g_keeptime,"line %d:第%d次NetDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				return;
//			}
//			// 每次性能结束后休息1s
//			SystemClock.sleep(1000);
//		}
//		gui.cls_show_msg1_record(TAG,"wlanAbilityczj",g_time_0,"预连接%s耗时%fs\n通讯速率%fKB/s\nfree耗时%fs\n", wifiPara.getSsid(),
//				(preconnectTime) / i,(sendPacket.getLen()*2/ 1024.0)* i / recvtimes, freetimes / i);
//	}
	private void wlan_ability_send_recv() 
	{
		/* private & local definition */
		Log.d("eric_chen", "执行双向通讯速率---------------------");
		int ret = 0;
		int totalByte=10*1024*1024;
		int onceByte=4*1024; //测试人员需求 只需要4K
		int j;
		byte[] buf = new byte[onceByte];
		int cnt=totalByte/onceByte;
		double startTime;
		double endTime;
		double time = 0;
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		int succ=0;
		/* process body */
		int rlen;
		byte[] rbuf = new byte[onceByte];
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlan_ability_send_recv",g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo(),ret);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		// 建立socket这边有1s的硬性时间
		if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlan_ability_send_recv",g_keeptime,"line %d:TransUp失败(ret = %d)", Tools.getLineInfo(),ret);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			layerBase.linkDown(wifiPara, type);
			if (excelflag) {
				
				
				exceldataDouble.add("0");
			}
			return;
		}
		gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"10M数据双向通讯中...\n%s:%d<->%s:%d",wifiPara.getLocalIp(),wifiPara.getLocalPort(),wifiPara.getServerIp(),wifiPara.getServerPort());
		int i=0;
		int failTime=0;
		while (cnt>0) 
		{
			if(failTime>20){
				gui.cls_show_msg1_record(TAG, "wlan_ability_send_recv", g_keeptime,"line %d:通讯失败超过20次，wifi太差，退出不测了",Tools.getLineInfo());
				break;
			}
			i++;
			startTime = System.currentTimeMillis();
			for(j=0;j<buf.length;j++){
				buf[j] = (byte) (Math.random()*128);
			}
			// 接收
			if ((rlen = sockSend(socketUtil, buf, onceByte,SO_TIMEO, wifiPara)) != buf.length) {
				gui.cls_show_msg1_record(TAG, "wlan_ability_send_recv", g_keeptime,"line %d:第%d次接收数据失败(实际len = %d,预期len = %d)",Tools.getLineInfo(),i, rlen, buf.length);
//				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				failTime++;
				continue;
			}
			if((rlen = sockRecv(socketUtil, rbuf, onceByte, SO_TIMEO, wifiPara))!=rbuf.length){
				gui.cls_show_msg1_record(TAG, "wlan_ability_send_recv", g_keeptime,"line %d:第%d次接收数据失败(实际len = %d,预期len = %d)",Tools.getLineInfo(),i, rlen, rbuf.length);
//				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				failTime++;
				continue;
			}
			cnt--;
			succ++;
			endTime=System.currentTimeMillis()-startTime;
			time=endTime+time;
		}
	
		time=time/1000.0;
		if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlan_ability_send_recv",g_keeptime,"line %d:TransDown失败(ret = %d)", Tools.getLineInfo(),ret);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		if ((ret = layerBase.netDown(socketUtil, wifiPara, sock_t, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlan_ability_send_recv",g_keeptime,"line %d:NetDown失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if (excelflag) {
			
			
			exceldataDouble.add(nf.format(succ*onceByte/time/1024));
		}
		gui.cls_show_msg1_record(TAG,"wlan_ability_send_recv",g_time_0,"%s双向通讯速率%fKB/s（成功%d次）\n", wifiPara.getSsid(),succ*onceByte/time/1024,succ);
	}
	
	private void wlan_ability_open_net_scan_disnet_close() 
	{
		/* private & local definition */
		Log.d("eric_chen", "执行打开扫描连接关闭---------------------");
		int i = 0,  ret = 0;
		double  openStartTime,netStartTime,disconnetStartTime,closeStartTime,scanStartTime,netOutTime;
		double  openEndTime,netEndTime,disconnetEndTime,closeEndTime,scanEndTime=0.0;
		double  opentime=0.0,nettime=0.0,disconnettime=0.0,closetime=0.0,scantime=0.0;
		PacketBean sendPacket = new PacketBean();
		//long startTime;
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		int cnt;
		if(GlobalVariable.gSequencePressFlag)
			cnt = getCycleValue();
		else
		{
			sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
			cnt = sendPacket.getLifecycle();
		}
	
		int succ=0;
//		wifiUtil.clearAllSavaWifi();
		
		//先连接第一次
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlan_ability_open_scan_net",g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo(), ret);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		if ((ret = layerBase.netDown(socketUtil, wifiPara, sock_t, type)) != SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"wlan_ability_open_scan_net",g_keeptime,"line %d:NetDown失败(ret = %d)", Tools.getLineInfo(), ret);
			return;
		}
		//A9 $
		//WifiConfiguration temconfig=wifiUtil.IsExsits(wifiPara.getSsid());
		wifiUtil.setSsid(wifiPara.getSsid());
		wifiUtil.registWifiConnect();
		Log.d("eric_chen", "测试开始---");
		while (i<cnt) 
		{
			i++;
			GlobalVariable.isWifiConnected=false;
			//打开
			openStartTime=System.currentTimeMillis();
			Log.d("eric", "即将打开wifi---------------");
			if((ret=layerBase.wifiOpen())!=SUCC){
				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次open失败(ret = %d)", Tools.getLineInfo(), i,ret);
				Log.d("eric", "打开wifi失败---------------");
				continue;
			}
			openEndTime=(System.currentTimeMillis()-openStartTime)/1000.0;
			Log.d("eric", "wifi打开---");
			//如果是隐藏的 手动连接 并且没有扫描时间
			if(wifiPara.isScan_ssid()){
			
				LoggerUtil.e("隐藏");
				netStartTime=System.currentTimeMillis();
				if ((ret = layerBase.wifiNet(wifiPara)) != SUCC) 
				{
					gui.cls_show_msg1_record(TAG,"wlan_ability_open_scan_net",g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo(), ret);
					layerBase.linkDown(wifiPara, type);
					continue;
				}
				//存在8秒的获取已连接记录时间要去掉
				netEndTime=(System.currentTimeMillis()-netStartTime)/1000.0-8;
			}else//非隐藏wifi 打开wifi后有扫描时间 60秒没有连接上就认为连接失败
			{
				scanStartTime=System.currentTimeMillis();
				Log.d("eric_chen", "扫描一次...");
				wifiUtil.startScan(wifiPara);
				while(true){
						wifiUtil.startScan(wifiPara);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					List<ScanResult> wifiList=wifiUtil.getWifiList();
					Log.d("eric", "wifi寻找---");
					if(wifiList.size()>0){
						//找到
						if(seekWifi(wifiList, 0)==0)
							break;
					}
				};
				scanEndTime=(System.currentTimeMillis()-scanStartTime)/1000.0;
				
				netStartTime=System.currentTimeMillis();
				netOutTime=System.currentTimeMillis();
				int counts = 60;
				while(GlobalVariable.isWifiConnected == false && counts > 0){
					Log.d("eric", "wifi持续连接---");
					//if(System.currentTimeMillis()-netOutTime>60*1000)
					//	break;
					if (counts % 10 == 0) {
							wifiUtil.startScan(wifiPara);
					}
					try {
						Thread.sleep(1000);
						counts--;
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				};
				//60秒还是没有连上 就继续下一轮
				if(!GlobalVariable.isWifiConnected){
					gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次连接失败(ret = %d)", Tools.getLineInfo(), i,ret);
					layerBase.linkDown(wifiPara, type);
					Log.d("eric", "wifi60s没连上---");
					continue;
				}
				netEndTime=(System.currentTimeMillis()-netStartTime)/1000.0;
			}
			
			
			disconnetStartTime=System.currentTimeMillis();
			// 链路层断开没有显示耗时 1s
			Log.d("eric", "wifi断开中---");
			if ((ret = layerBase.wifiDisconnet()) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次断开wifi失败(ret = %d)", Tools.getLineInfo(), i,ret);
				layerBase.linkDown(wifiPara, type);
				continue;
			}
			//by20200630 chending  android9上面网络ID的排序与其他机器不一样。所以其他机器之前不报断开wifi异常问题
//			if (!wifiUtil.disconnectWifi()) 
//			{
//				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次断开wifi失败(ret = %d)", Tools.getLineInfo(), i,ret);
//				layerBase.linkDown(wifiPara, type);
//				continue;
//			}
			Log.d("eric", "wifi断开---");
			disconnetEndTime =(System.currentTimeMillis() -disconnetStartTime)/1000.0;
			
			
			closeStartTime = System.currentTimeMillis();
			// 链路层断开没有显示耗时 1s
			Log.d("eric", "wifi链路层断开中---");
			if ((ret = layerBase.linkDown(wifiPara, type)) != SUCC) 
			{
				gui.cls_show_msg1_record(TAG,"wlan_ability",g_keeptime,"line %d:第%d次LinkDown失败(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			closeEndTime = (System.currentTimeMillis()-closeStartTime)/1000.0;
			Log.d("eric", "wifi链路层断开---");
			Log.d("eric_chen", "本次测试结束----------");
			// 每次性能结束后休息1s
			SystemClock.sleep(1000);
			succ++;
			opentime+=openEndTime;
			nettime+=netEndTime;
			disconnettime+=disconnetEndTime;
			closetime+=closeEndTime;
			scantime+=scanEndTime;

		}
		wifiUtil.unRegistWifiBroadCast();
		if (excelflag) {
			exceldata.add(2, succ);
			exceldataDouble.add(nf.format(opentime/succ));
			exceldataDouble.add(nf.format(scantime/succ));
			exceldataDouble.add(nf.format(nettime/succ));
			exceldataDouble.add(nf.format(disconnettime/succ));
			exceldataDouble.add(nf.format(closetime/succ));
		}
		gui.cls_show_msg1_record(TAG,"wlan_ability",g_time_0,"成功%d次,失败%d次\n打开Wifi平均时间:%fs\n扫描平均时间:%fs\n连接Wifi平均时间:%fs\n断开Wifi平均时间:%fs\n" +
				"关闭Wifi平均时间:%fs\n",succ,cnt-succ,opentime/succ,scantime/succ,nettime/succ,disconnettime/succ,closetime/succ);
	}
	public int seekWifi(List<ScanResult> wifiList,int i){
		for(;i<wifiList.size();i++){
			if(wifiList.get(i).SSID.equals(wifiPara.getSsid())){
				Log.d("eric_chen", "找到上次连接的wifi");
					return 0;
			}
		}
		return -1;
	}
	/*private void wlanAbility2() 
	{
		 private & local definition 
		int ret = 0;
		long startTime;
		float inittimes = 0.0f;

		 process body 
		if (gui.ShowMessageBox("是否为机器上电后首次运行本用例？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1(2, "请重新开机后直接运行本用例");
			return;
		}

		startTime = System.currentTimeMillis();
		// 初始化wifi
		wifiUtil.openWifi();
		gui.cls_show_msg1(2, "接入AP（%s）中...",wifiPara.getSsid());
		startTime = System.currentTimeMillis();
		// 连接wifi网络
		if ((ret = layerBase.linkUP(wifiPara, LinkType.WLAN)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG,"wlanity2",g_keeptime,"line %d:打开wifi失败%s", Tools.getLineInfo(), ret);
			return;
		}
		inittimes = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
		gui.cls_show_msg1_record(TAG,"wlanAbility2",g_time_0,"开机首次连接%s耗时%fs\n", wifiPara.getSsid(),inittimes);
		wifiUtil.closeWifi();
	}*/


	/**
	 * wifi异常测试
	 */
	private void wlan_abnormity() 
	{
		int nkeyin = '0';
		while (true) 
		{

			nkeyin = gui.cls_show_msg("WIFI异常测试\n0.跨AP\n1.连接异常\n2.禁止DHCP\n");
			switch (nkeyin) 
			{
			case '0':
				wlanCrossAp();
				break;

			case '1':
				gui.cls_show_msg1_record(TAG,"wlan_abnormity",g_time_0, "请配置未开启后台的服务器地址，应不能连通！");
				break;
			
			case '2':
				gui.cls_show_msg1_record(TAG,"wlan_abnormity",g_time_0, "请将一个AP的DHCP设置成禁止的，使用静态连接应该能够成功！");
				break;
				
			case ESC:
				return;

			default:
				break;
			}
		}
	}

	/**
	 *  跨AP测试
	 */
	private void wlanCrossAp()  
	{
		/* private & local definition */
		int[] total = new int[2];
		int[] succ = new int[2];
		int i;
		byte[] buf = new byte[5120];
		int  startLen;
		int loop=5;
		int slen = 0, rlen = 0;
		WifiPara wifiPara1 = new WifiPara();
		WifiPara wifiPara2 = new WifiPara();
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = LinkType.WLAN;

		
		PacketBean sendPacket = new PacketBean();
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		sendPacket.setLen(5120);
		startLen = sendPacket.getLen();
		
		NetWorkingBase  netWorkingBase = new NetWorkingBase();
		netWorkingBase.setServerPort(3466);
//		sendPacket.set
		byte[] rbuf = new byte[startLen];
		
		
		// 备份第一个Ap
		wifiPara1.setSsid(wifiPara.getSsid());
		wifiPara1.setPasswd(wifiPara.getPasswd());
		wifiPara1.setSec(wifiPara.getSec());
		wifiPara1.setBssid(wifiPara.getBssid());
		
		// 配置第二个AP
		while (true) 
		{
			if (config.confConnWlan(wifiPara2) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG,"wlanCrossAp",g_keeptime,"line %d:配置链路参数失败", Tools.getLineInfo());
				break;
			}
			while (true) 
			{
				if ((total[0] + total[1]) % 2 > 0) 
				{
					i = 1;
					wifiPara.setSsid(wifiPara1.getSsid());
					wifiPara.setPasswd(wifiPara1.getPasswd());
					wifiPara.setSec(wifiPara1.getSec());
					wifiPara.setBssid(wifiPara1.getBssid());
					LoggerUtil.e("wifi1:"+wifiPara.getSsid()+" "+wifiPara.getSec());
				} else 
				{
					i = 0;
					wifiPara.setSsid(wifiPara2.getSsid());
					wifiPara.setPasswd(wifiPara2.getPasswd());
					wifiPara.setSec(wifiPara2.getSec());
					wifiPara.setBssid(wifiPara2.getBssid());
					LoggerUtil.e("wifi2:"+wifiPara.getSsid()+" "+wifiPara.getSec());
				}
				if(gui.cls_show_msg1(3,"正在进行第%d次接入%s(已成功%d次),[取消]退出测试...", total[i] + 1,wifiPara.getSsid(), succ[i])==ESC)
					break;
				total[i]++;
				if (layerBase.netUp(wifiPara, type) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG,"wlanCrossAp",g_keeptime,"line %d:第%d次NetUp失败", Tools.getLineInfo(), total[i] + 1);
					layerBase.netDown(socketUtil, wifiPara, sock_t, type);
					continue;
				}
				
				// 建立连接
				if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
				{
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次TransUp失败(ret = %d)",Tools.getLineInfo(),i,ret);
					continue;
				}
				
				
				for (int j = 0; j < 1; j++) 
				{	
					
					Log.d("eric_chen", "startLen:  "+startLen);
					Log.d("eric_chen", "sendPacket.getHeader():  "+sendPacket.getHeader().length);
					LoggerUtil.e("eric_chen:"+Dump.getHexDump(sendPacket.getHeader()));
					// 收发数据
					if ((slen = sockSend(socketUtil, sendPacket.getHeader(),startLen, SO_TIMEO, wifiPara)) != startLen) 
					{
						layerBase.transDown(socketUtil, sock_t);
						gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:第%d次第%d轮发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(),i,j, slen, startLen);
						break;
//						continue;
					}

					// 接收
					if ((rlen = sockRecv(socketUtil, rbuf, startLen,SO_TIMEO, wifiPara)) != startLen) 
					{
						layerBase.transDown(socketUtil, sock_t);
						gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime,"line %d:第%d次第%d轮接收数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),i,j,rlen, startLen);
						break;
//						continue;
					}
					// 比较收发
					if (Tools.memcmp(sendPacket.getHeader(), rbuf,sendPacket.getLen())==false) 
					{
						layerBase.transDown(socketUtil, sock_t);
						gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次第%d轮校验数据失败",Tools.getLineInfo(),i,j);
						break;
//						continue;
					}
				}
				if (slen != sendPacket.getLen() || rlen != sendPacket.getLen()) 
				{
					continue;
				}
				// 挂断
				if ((ret = layerBase.transDown(socketUtil, sock_t)) != SUCC) 
				{
					gui.cls_show_msg1_record(TAG,"connTransPre", g_keeptime, "line %d:第%d次TransDown失败(ret = %d)",Tools.getLineInfo(),i,ret);
					continue;
				}
				if (layerBase.netDown(socketUtil, wifiPara, sock_t, type) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG,"wlanCrossAp",g_keeptime, "line %d:第%d次NetDown失败",Tools.getLineInfo(), total[i] + 1);
					continue;
				}
				succ[i]++;
			}
			gui.cls_show_msg1_record(TAG,"wlanCrossAp", g_time_0,"接入AP1(%s)次数:%d\n成功次数:%d",wifiPara1.getSsid(), total[0], succ[0]);
			SystemClock.sleep(3000);
			gui.cls_show_msg1_record(TAG,"wlanCrossAp",g_time_0, "接入AP2(%s)次数:%d\n成功次数:%d",wifiPara2.getSsid(), total[1], succ[1]);
			break;
		}
		// 恢复为第一个AP
		wifiPara.setSsid(wifiPara1.getSsid());
		wifiPara.setPasswd(wifiPara1.getPasswd());
	}

	/**
	 *  wifi复位操作
	 */
	/*private void wlanReset() 
	{
		 private & local definition 
		Packet sendPacket = new Packet();
		byte[] buf = new byte[PACKMAXLEN];
		int rst_flag = RESET_NO;
		LinkType type = wifiPara.getType();

		 process body 
		if ((rst_flag = select_rst_flag()) == RESET_NO)
			return;
		createPacket(sendPacket, buf);
		update_snd_packet(sendPacket, type);
		// 拨号操作
		try 
		{
			wlanDialComm(sendPacket, rst_flag);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		gui.cls_show_msg1(2, "WIFI复位测试失败");
	}*/

	/**
	 *  wifi通讯函数
	 * @param sendPacket
	 * @param flag
	 * @return
	 */
	/*private int wlanDialComm(Packet sendPacket, int flag) 
	{
		 private & local definition 
		byte[] rbuf = new byte[PACKMAXLEN];
		int slen = 0, rlen = 0, ret = 0, SO_TIMEO = SO_TIMEO;
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();

		 process body 
		// 测试前置
		if ((ret = layerBase.netUp(wifiPara, type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG,"wlanDialComm", SO_TIMEO, "%s, line %d:NetUp失败", TAG,Tools.getLineInfo());
			return ret;
		}

		if (flag == RESET_PPPOPEN) 
		{
			gui.cls_show_msg1_record(TAG,"wlanDialComm", 2, "WLAN已连接(TCP未打开)...即将软重启...");
			Tools.reboot(myactivity);
		}
		if ((ret = layerBase.transUp(socketUtil, sock_t)) != NDK_OK) 
		{
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			gui.cls_show_msg1_record(TAG,"wlanDialComm", g_keeptime, "%s, line %d:TransUp失败", TAG,Tools.getLineInfo());
			return ret;
		}
		if (flag == RESET_TCPOPEN) 
		{
			gui.cls_show_msg1_record(TAG,"wlanDialComm", 2, "TCP已打开(WLAN已连接)...即将软重启...");
			// 重启操作
			Tools.reboot(myactivity);
		}
		// 发送数据
		if ((slen = sockSend(socketUtil, sendPacket.getHeader(),sendPacket.getLen(), SO_TIMEO, wifiPara)) != sendPacket.getLen()) 
		{
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			gui.cls_show_msg1_record(TAG,"wlanDialComm", g_keeptime,"%s, line %d:发送数据失败(实际%d, 预期%d)", TAG, Tools.getLineInfo(),slen, sendPacket.getLen());
			return NDK_ERR;
		}

		// 接收数据
		Arrays.fill(rbuf, (byte) 0);
		if ((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO,wifiPara)) != sendPacket.getLen()) 
		{
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			gui.cls_show_msg1_record(TAG,"wlanDialComm", g_keeptime,"%s, line %d:接收数据失败(实际%d, 预期%d)", TAG, Tools.getLineInfo(),
					rlen, sendPacket.getLen());
			return NDK_ERR;
		}

		// 比较数据
		if (Tools.memcmp(sendPacket.getHeader(), rbuf, rlen)) 
		{
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			gui.cls_show_msg1_record(TAG,"wlanDialComm", g_keeptime, "%s, line %d:校验失败", TAG,Tools.getLineInfo());
			return NDK_ERR;
		}

		if ((ret = layerBase.transDown(socketUtil, sock_t)) != NDK_OK) 
		{
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			gui.cls_show_msg1_record(TAG,"wlanDialComm", g_keeptime, "%s, line %d:TransDown失败", TAG,Tools.getLineInfo());
			return ret;
		}

		if (flag == RESET_TCPCLOSE) 
		{
			gui.cls_show_msg1_record(TAG,"wlanDialComm", 2, "TCP已关闭(WLAN未关闭)...即将软重启...");
			// 重启
			Tools.reboot(myactivity);

		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		if (flag == RESET_PPPCLOSE) {
			gui.cls_show_msg1_record(TAG,"wlanDialComm", 2, "WLAN已关闭(TCP已关闭)...即将软重启...");
			// 重启
			Tools.reboot(myactivity);
		}
		return NDK_OK;
	}*/

	public void createWlanResetPacket(PacketBean packet, byte[] buf) 
	{
		packet.setHeader(buf);
		packet.setLen(PACKMAXLEN);
		packet.setOrig_len(PACKMAXLEN);
		packet.setLifecycle(0);
		packet.setForever(true);
		packet.setIsLenRec(false);
		packet.setIsDataRnd(true);
	}

	/**
	 *  wifi休眠测试
	 */
	/*private void wlanSleep() 
	{
		 private & local definition 
		int i = 2;
		int slen;
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		SettingsManager settingsManager = (SettingsManager) getActivity().getSystemService(SETTINGS_MANAGER_SERVICE);
		int sleepFlag;

		 process body 
		sleepFlag=gui.cls_show_msg("wifi休眠异常测试\n0.深休眠\n1.浅休眠");
		// 深休眠接口只有3G机器支持
		switch (GlobalVariable.currentPlatform) 
		{
		case N900_3G:
			if(sleepFlag=='0')
				settingsManager.setDeepSleepEnabled(true);
			else if(sleepFlag=='1')
				settingsManager.setDeepSleepEnabled(false);
			else
				break;
			break;

		case N900_4G:
		case N910:
		case IM81_New:
		case IM81_Old:
			break;
		}
		while (i > 0) 
		{
			if (layerBase.netUp(wifiPara, type) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG,"wlanSleep",g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
				return;
			}
			if(layerBase.transUp(socketUtil, sock_t)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"wlanSleep",g_keeptime,"line %d:transUp失败", Tools.getLineInfo());
				return;
			}
			if(i==2)
			{
				gui.cls_show_msg1(2, "网络层已建立！确保未有任何应用跟K21通信，电源键进入休眠，若是深休眠，请休眠4分钟后唤醒，若是浅休眠，请休眠2分钟后唤醒");
			}
			else 
			{
				// 设置1分钟休眠
				settingsManager.setScreenTimeout(ONE_MIN);
				gui.cls_show_msg1(2, "网络层已建立！等待POS自动进入休眠，若是深休眠，请休眠4分钟后唤醒，若是浅休眠，请休眠2分钟后唤醒");
			}
			gui.cls_show_msg("已退出休眠，任意键继续");

			// 深休眠
			if (sleepFlag == 0) 
			{
				Packet sendPacket = new Packet();
				byte[] buf = new byte[PACKMAXLEN];
				init_snd_packet(sendPacket, buf);
				set_snd_packet(sendPacket,type);
				// 收发数据失败
				if((slen = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), 10*1000,wifiPara))!=0)
				{
					gui.cls_show_msg1_record(TAG,"wlanSleep",g_keeptime,"line %d:发送失败（实际%d，预期%d）", Tools.getLineInfo(),slen,0);
					layerBase.transDown(socketUtil, sock_t);
					layerBase.netDown(socketUtil, wifiPara, sock_t, type);
					return;
				}
				layerBase.transDown(socketUtil, sock_t);
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			}
			// 浅休眠，链路未断开
			else if (sleepFlag == 1) 
			{
				// 做10次的通讯测试
				send_recv_press(socketUtil, type, wifiPara);
				if(layerBase.transDown(socketUtil, sock_t)!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG,"wlanSleep",g_keeptime,"line %d:transDown失败", Tools.getLineInfo());
					return;
				}
				if(layerBase.netDown(socketUtil, wifiPara, sock_t, type)!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG,"wlanSleep",g_keeptime,"line %d:netDown失败", Tools.getLineInfo());
					return;
				}
			}
			i--;
		}
		gui.cls_show_msg1(2,"WLAN休眠测试成功");
		settingsManager.setScreenTimeout(FIVE_MIN);
	}*/
	
	/**
	 * 获取信号强度以及通讯操作
	 */
	private void ap_stre_conn() 
	{
		/* private & local definition */
		int succ=0,cnt = 0,i=0;
		int slen = 0,rlen = 0,ret = 0;
		wifiUtil.openNet();
		final PacketBean sendPacket = new PacketBean();
		sendPacket.setLen(5000);
		byte[] buf = new byte[5000];
		Arrays.fill(buf, (byte) 0x38);
		sendPacket.setHeader(buf);
		byte[] rbuf;
		
		/* process body */
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 100));
		rbuf = new byte[sendPacket.getLen()];
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();
		cnt = sendPacket.getLifecycle();
		// 获取信号强度
		int signalStrength = 1;
		int signalStrengthTotal = 0;
		while(cnt>0)
		{
			// 链路连接
			i++;
			if(gui.cls_show_msg1(3, "AP强度获取+通讯第%d次测试,已成功%d次,[取消]退出测试...", i,succ)==ESC)
				break;
			cnt--;
			if (layerBase.netUp( wifiPara, wifiPara.getType()) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "ap_stre_conn", g_keeptime,"line %d:第%d次NetUp失败\n", Tools.getLineInfo(),i);
				continue;
			}
			if (layerBase.transUp(socketUtil, sock_t) != NDK_OK) 
			{
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "ap_stre_conn", g_keeptime,"line %d:第%d次TransUp失败", Tools.getLineInfo(),i);
				continue;
			}
			
//			while (wifiUtil.isWifiConnected(myactivity)) 
//			{
				signalStrength = wifiUtil.getSignStrength();
//				break;
//			}
			gui.cls_show_msg1(2, "当前连接的AP的信号强度=" + signalStrength + "范围0到-100；0到-50最好；-50到-70偏差；小于-70最差");
			if(signalStrength<0)
			    signalStrengthTotal=signalStrengthTotal+signalStrength;
			// 发送数据
			if((slen = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,wifiPara))!= sendPacket.getLen())
			{
				ret = gui.cls_show_msg1_record(TAG, "sen_recv_press", g_keeptime,"line %d:第%d次发送失败（实际%d，预期%d）", Tools.getLineInfo(),i,slen,sendPacket.getLen());
				if(ret == NDK_ERR_QUIT)
					break;
				else 
				{
					// 重连socket
					Layer.transStatus = TransStatus.TRANSDOWN;
					new Layer(myactivity,handler).transUp(socketUtil, wifiPara.getSock_t());
					continue;
				}
					
			}
			// 接收数据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil,rbuf,sendPacket.getLen(), SO_TIMEO,wifiPara))!= sendPacket.getLen())
			{
				ret = gui.cls_show_msg1_record(TAG, "sen_recv_press",g_keeptime, "line %d:第%d次接收失败（实际%d，预期%d）", Tools.getLineInfo(),i,rlen,sendPacket.getLen());
				if(ret == NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			// 比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen()))
			{
				ret = gui.cls_show_msg1_record(TAG, "sen_recv_press", g_keeptime,"line %d:第%d次检验失败", Tools.getLineInfo(),i);
				if(ret == NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			if (layerBase.transDown(socketUtil, sock_t) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "sen_recv_press", g_keeptime, "line %d:第%d次transDown失败", Tools.getLineInfo(),i);
				continue;
			}
			if (layerBase.netDown(socketUtil, wifiPara, sock_t, type) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "sen_recv_press", g_keeptime, "line %d:第%d次netDown失败", Tools.getLineInfo(),i);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "ap_stre_conn", g_time_0,"AP强度获取+通讯总次数%d，成功次数%d，平均信号强度%ddBm", i,succ,signalStrengthTotal/succ);
	}
	
	/**
	 * 获取信号强度
	 */
	/*private void sign_stre()  
	{
		 private & local definition 
		Sock_t sock_t = wifiPara.getSock_t();
		LinkType type = wifiPara.getType();
		System.out.println("王 wifiPara.getSsid()=" + wifiPara.getSsid());
		int ret = 0;
		
		 process body 
		// 初始化wifi
		wifiUtil.closeWifi();
		wifiUtil.openWifi();
		gui.cls_show_msg1(2, "接入AP（%s）中...", wifiPara.getSsid());
		// 连接wifi网络
		if ((ret = layerBase.linkUP(wifiPara, LinkType.WLAN)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "sign_stre", g_keeptime, "line %d:打开wifi失败%s", Tools.getLineInfo(), ret);
			return;
		}
		// 链路连接
		if (layerBase.netUp(wifiPara, wifiPara.getType()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dataTrans", g_keeptime, "line %d:NetUp失败\n",
					Tools.getLineInfo());
		}
		int signalStrength = 1;
//		while (wifiUtil.isWifiConnected(getActivity())) 
//		{
			System.out.println("王 isWifiConnected=" + wifiUtil.isWifiConnected(getActivity()));
			signalStrength = wifiUtil.getWifiApSignalStrength();
//			break;
//		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		gui.cls_show_msg1_record(TAG, "sign_stre", g_keeptime, "当前连接的AP的信号强度=" + signalStrength + "范围0到-100；0到-50最好；-50到-70偏差；小于-70最差");
	}	*/
	//AP兼容性测试
		private void  AP_Compatibility_test(){
			int encMode;
			int DHCPenable;
			GlobalVariable.gSequencePressFlag=true;
			flag=0;
			gui.cls_show_msg("请确保SD卡目录下放置paraconf.json;设备如果是A或者AU模块需要插入可上网的sim卡.任意键继续");
			
			if(gui.cls_show_msg("是否使用自动填写到Excel文档功能。是[确认],否[其他]")==ENTER){
				
				excelflag=true;
				gui.cls_show_msg("请确保将wifi兼容结果文档名称修改为<wifiV1.6>，并另存为xls格式，然后将该文档放置sdcard目录下,结果将自动填写到文档中。");
				int returnValue=gui.cls_show_msg("Excel_sheet选择\n0.2.4G精简版\n1.2.4G+5G精简版\n");
				switch (returnValue) {
				case '0':
					excelsheet=4;
					break;
					
				case '1':
					excelsheet=5;
					break;

				default:
					break;
				}
				if (excelsheet==4) {
					gui.cls_show_msg1(2,"当前选择2.4G精简版");
				}else if (excelsheet==5) {
					gui.cls_show_msg1(2,"当前选择2.4G+5G精简版");
				}
//				new Config(myactivity,handler).set_wifi_excelsheet(wifiPara);
//				excelsheet=wifiPara.getexcelsheet();
				Log.d("eric_chen", "excelsheet"+excelsheet);
			
			}
			
			//从json文件中获取所有wifiPare对象
			String jsonString = ReadFile(GlobalVariable.sdPath+"paraconf.json");// 获得json配置文件的内容
			JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
			//服务器地址
			String wifiSvrIP = jsonObject.get("WifiSvrIP").getAsString();
			//服务器端口
			String wifiSvrPORT =jsonObject.get("WifiSvrPORT").getAsString();
			String sockType=jsonObject.get("WifiSockType").getAsString();
			//连接+数传 次数
			int connTransTime=jsonObject.get("ConnTranTime").getAsInt();
			//建链压力 次数
			int setConnTime=jsonObject.get("SetConnTime").getAsInt();
			JsonArray wifiList = jsonObject.get("WifiList").getAsJsonArray();
			long oldTime = System.currentTimeMillis();
			for(JsonElement element : wifiList){  
				con=true;
		            JsonObject wifiObject = element.getAsJsonObject();  
		           if( wifiObject.has("note"))
		        	   continue;
		           if( wifiObject.has("IsScanSsid"))
		           {
		        	   wifiPara.setScan_ssid(true);
		        	   Log.v("yincang", "yincang");
		           }
		           else
		        	   wifiPara.setScan_ssid(false);
		           wifiPara.setType(LinkType.WLAN);
		            //设置DHCPenable属性
		            DHCPenable=wifiObject.get("WifiDHCPenable").getAsInt();
		            //如果是静态
		            if(DHCPenable==0){
		            	wifiPara.setDHCPenable(false);
		            	//获取本机地址
		            	wifiPara.setLocalIp(wifiObject.get("WifiLocalIP").getAsString());
		            	//获取子网掩码
		            	wifiPara.setNetMask(wifiObject.get("WifiLocalMask").getAsString());
		            	//获取网关
		            	wifiPara.setGateWay(wifiObject.get("WifiLocalGatway").getAsString());
		            }else
		            	wifiPara.setDHCPenable(true);
		           //设置WIFI热点名称
		           
		            wifiPara.setSsid(wifiObject.get("WlanEssid").getAsString());
		            
		            wifiPara.setPasswd(wifiObject.get("WlanPwd").getAsString());
		            //设置认证方式
		            encMode=wifiObject.get("WlanEncMode").getAsInt();         
		            if(encMode==0){
		            	wifiPara.setSec(WIFI_SEC.NOPASS);
		            }else if(encMode==1 || encMode==2){
		            	wifiPara.setSec(WIFI_SEC.WEP);
		            }else if(encMode==3 || encMode==4){
		            	wifiPara.setSec(WIFI_SEC.WPA);
		            }
		            //设置服务器地址和服务器端口
		            wifiPara.setServerIp(wifiSvrIP);
		            wifiPara.setServerPort(Integer.parseInt(wifiSvrPORT));
		            //设置传输类型
		            if(sockType.equals("TCP")){
		            	wifiPara.setSock_t(Sock_t.SOCK_TCP);
		            }else if(sockType.equals("UDP")){
		            	wifiPara.setSock_t(Sock_t.SOCK_UDP);
		            }else if(sockType.equals("SSL")){
		            	wifiPara.setSock_t(Sock_t.SOCK_SSL);
		            }
		            //获取路由器的行数
		            if (excelflag) {
		            	 ssidrow=excelUtils.readxlsx2(wifiPara.getSsid(),excelsheet);
					}
		            gui.cls_show_msg1_record(TAG,"AP_Compatibility_test", g_keeptime,"将进行:%s的兼容性测试,其结果如下:",wifiPara.getSsid());
		            
//		            wifiUtil.clearAllSavaWifi();
		            //运行相关操作
		            socketUtil = new SocketUtil(wifiPara.getServerIp(),
							wifiPara.getServerPort());
		            
		            //连接+数传 100次
		            g_CycleTime=connTransTime;
		            connTransPre();
		            if (con) {
		            	   //建链压力 30次
			            g_CycleTime=setConnTime;
			            setConnPre();
			            g_CycleTime=30;
			            wlan_ability_send_recv();
			            wlan_ability_open_net_scan_disnet_close();
					}else if (excelflag) {
						
						exceldata.add(-100);
					} 
//		            //建链压力 30次
//		            g_CycleTime=setConnTime;
//		            setConnPre();
//		            g_CycleTime=30;
//		            wlan_ability_send_recv();
//		            wlan_ability_open_net_scan_disnet_close();
		            
//		            写入excel文档
		            if (excelflag) {
		            		excelUtils.insertExceldata(ssidrow,exceldata,excelsheet);
				            excelUtils.insertExceldata2(ssidrow,exceldataDouble,excelsheet);
				            exceldata.clear();
				            exceldataDouble.clear();
					}
		  
		        } 
			//还原配置
			GlobalVariable.gSequencePressFlag=false;
			float diff = Tools.getStopTime(oldTime);
			long hour=(long) (diff/3600);
			long second=(long) (diff%3600)/60;
			gui.cls_show_msg1_record(TAG,"AP_Compatibility_test", g_keeptime,"测试时间：%d小时%d分钟",hour,second);
		} 
	
	/**新增兼容性环境确认20200509郑薛晴*/
	private void compatibilityDataTest() {
		/* private & local definition */
		int slen = 0, rlen = 0;
		byte[] sbuf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];

		int encMode;
		int DHCPenable;
		GlobalVariable.gSequencePressFlag = true;
		gui.cls_show_msg("请确保SD卡目录下放置paraconf.json,任意键继续");
		flag = 0;
		// 从json文件中获取所有wifiPare对象
		String jsonString = ReadFile(GlobalVariable.sdPath + "paraconf.json");// 获得json配置文件的内容
		JsonObject jsonObject = new JsonParser().parse(jsonString)
				.getAsJsonObject();
		// 服务器地址
		String wifiSvrIP = jsonObject.get("WifiSvrIP").getAsString();
		// 服务器端口
		String wifiSvrPORT = jsonObject.get("WifiSvrPORT").getAsString();
		String sockType = jsonObject.get("WifiSockType").getAsString();
		JsonArray wifiList = jsonObject.get("WifiList").getAsJsonArray();

		for (JsonElement element : wifiList) {
			con = true;

			JsonObject wifiObject = element.getAsJsonObject();
			if (wifiObject.has("note"))
				continue;
			if (wifiObject.has("IsScanSsid")) {
				wifiPara.setScan_ssid(true);
				Log.v("yincang", "yincang");
			} else
				wifiPara.setScan_ssid(false);
			wifiPara.setType(LinkType.WLAN);
			// 设置DHCPenable属性
			DHCPenable = wifiObject.get("WifiDHCPenable").getAsInt();
			// 如果是静态
			if (DHCPenable == 0) {
				wifiPara.setDHCPenable(false);
				// 获取本机地址
				wifiPara.setLocalIp(wifiObject.get("WifiLocalIP").getAsString());
				// 获取子网掩码
				wifiPara.setNetMask(wifiObject.get("WifiLocalMask").getAsString());
				// 获取网关
				wifiPara.setGateWay(wifiObject.get("WifiLocalGatway").getAsString());
			} else
				wifiPara.setDHCPenable(true);
			// 设置WIFI热点名称

			wifiPara.setSsid(wifiObject.get("WlanEssid").getAsString());

			wifiPara.setPasswd(wifiObject.get("WlanPwd").getAsString());
			// 设置认证方式
			encMode = wifiObject.get("WlanEncMode").getAsInt();
			if (encMode == 0) {
				wifiPara.setSec(WIFI_SEC.NOPASS);
			} else if (encMode == 1 || encMode == 2) {
				wifiPara.setSec(WIFI_SEC.WEP);
			} else if (encMode == 3 || encMode == 4) {
				wifiPara.setSec(WIFI_SEC.WPA);
			}
			// 设置服务器地址和服务器端口
			wifiPara.setServerIp(wifiSvrIP);
			wifiPara.setServerPort(Integer.parseInt(wifiSvrPORT));
			// 设置传输类型
			if (sockType.equals("TCP")) {
				wifiPara.setSock_t(Sock_t.SOCK_TCP);
			} else if (sockType.equals("UDP")) {
				wifiPara.setSock_t(Sock_t.SOCK_UDP);
			} else if (sockType.equals("SSL")) {
				wifiPara.setSock_t(Sock_t.SOCK_SSL);
			}
			socketUtil = new SocketUtil(wifiPara.getServerIp(),
					wifiPara.getServerPort());

			Sock_t sock_t = wifiPara.getSock_t();
			LinkType type = wifiPara.getType();
			// 链路连接
			if ((ret = layerBase.netUp(wifiPara, wifiPara.getType())) != SUCC) {
				gui.cls_show_msg1_record(TAG, "compatibilityDataTest",g_keeptime, "line %d:WIFI_NetUp失败(ret = %d)",Tools.getLineInfo(), ret);
				if(!GlobalVariable.isContinue)
					return;
			}

			LoggerUtil.d("385----netUp");

			if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) {
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "compatibilityDataTest",g_keeptime, "line %d:WIFI_TransUp失败(ret = %d)",Tools.getLineInfo(), ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// 发送数据
			if ((slen = sockSend(socketUtil, sbuf, PACKMAXLEN, SO_TIMEO,wifiPara)) != PACKMAXLEN) {
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "compatibilityDataTest",g_keeptime, "line %d:发送数据失败(实际len = %d,预期len = %d)",Tools.getLineInfo(), slen, PACKMAXLEN);
				// 重连socket
				Layer.transStatus = TransStatus.TRANSDOWN;
				if(!GlobalVariable.isContinue)
					return;

			}
			Arrays.fill(rbuf, (byte) 0);
			if ((rlen = sockRecv(socketUtil, rbuf, PACKMAXLEN, SO_TIMEO,wifiPara)) != PACKMAXLEN) {
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "compatibilityDataTest",g_keeptime, "line %d:接收数据失败(实际%d,预期%d)",Tools.getLineInfo(), rlen, PACKMAXLEN);
				if(!GlobalVariable.isContinue)
					return;
			}
			// 比较数据
			if (Tools.memcmp(sbuf, rbuf, sbuf.length) == false) {
				layerBase.netDown(socketUtil, wifiPara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "compatibilityDataTest",g_keeptime, "line %d:检验收发数据失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, wifiPara, sock_t, type);
			gui.cls_show_msg1_record(TAG, "compatibilityDataTest", g_keeptime,"兼容性环境验证测试通过，%s路由器可正常收发数据", wifiPara.getSsid());
		}
		gui.cls_show_msg1_record(TAG, "compatibilityDataTest", g_keeptime,"=================全部路由器的兼容性环境验证测试完毕====================", wifiPara.getSsid());
	}
		

		// 读文件，返回字符串
		public String ReadFile(String path) {
			//File file = new File(path);
			BufferedReader reader = null;
			String laststr = "";
			try {
				FileInputStream fileInputStream = new FileInputStream(path);
	            InputStreamReader inputStreamReader = new InputStreamReader(
	                    fileInputStream, "gbk");
				reader = new BufferedReader(inputStreamReader);
				String tempString = null;
				while ((tempString = reader.readLine()) != null) {
					laststr = laststr + tempString;
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
					}
				}
			}
			return laststr;
		}
//	
}
