package com.example.highplattest.systest;
import android.annotation.SuppressLint;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
/*import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.newland.SettingsManager;
import android.util.Log;
import com.example.highplattest.main.Layer;
import com.example.highplattest.main.LayerBase;
import com.example.highplattest.main.MobileUtil;
import com.example.highplattest.main.Packet;
import com.example.highplattest.main.ParaEnum.Sock_t;
import com.example.highplattest.main.SocketUtil;
import com.example.highplattest.main.Tools;
import com.example.highplattest.main.ParaEnum.LinkType;*/
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest34.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170705
 * directory 		: 
 * description 		: WIFI/无线深交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		    20170705
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest34 extends DefaultFragment
{
	private final String TAG = SysTest34.class.getSimpleName();
	private final String TESTITEM = "WLAN/无线";
//	private WifiPara wifiPara = new WifiPara();
//	private MobilePara mobilePara = new MobilePara();
//	private Gui gui = new Gui(myactivity, handler);
//	private Config config;
//	private boolean init_wlan = false; // wifi初始化标志
//	int  isThread1,isThread2,isThread3,isThread4,isThread5;
//	int ava_count=10;
//	private SocketUtil socket_wifi;
	
//	public void systest34()
//	{
		/*initLayer();
		config = new Config(myactivity, handler);
		// 支持自动化的
		
		while(true)
		{
			int nkeyIn=gui.cls_show_msg("WLAN/无线\n0.WLAN配置\n1.交叉测试\n2.异常测试");
			switch (nkeyIn) 
			{
			
			case '0':
				// wifi配置
				switch (config.confConnWlan(wifiPara)) 
				{
				case NDK_OK:
					socket_wifi = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
					gui.cls_show_msg1(2, "wlan配置成功！！！");
					init_wlan = true;
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1(2, "line %d:wlan配置失败！！！");
					break;
				
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				cross_test();
				break;
				
			case '2':// 异常测试
				wlm_wlan_abnormal();
				break;
			
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	*//**
	 * 初始化layer对象
	 *//*
	protected void initLayer()
	{
		layerBase = Layer.getLayerBase(myactivity,handler);
	}
	// 无线与wifi交叉的异常测试
	public void wlm_wlan_abnormal()
	{
		if(init_wlan==false)
		{
			gui.cls_printf("请先进行wifi的配置".getBytes());
			return;
		}
		
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("无线与wlan交叉异常\n0.断电异常\n1.休眠异常\n2.多apk异常\n");
			switch (nkeyIn) 
			{
			case '0':
				power_off_abnormal();
				break;
				
			case '1':
				sleep_abnormal();
				break;
			case '2':
				more_apk_abnormal();
				break;	
			case ESC:
				return;

			default:
				break;
			}
		}
	}
	
	// 断电异常 add by 20170725
	private void power_off_abnormal()
	{
		 private & local definition 
		ConnectivityManager connManager = (ConnectivityManager) myactivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		int nkeyIn=0;
		NetworkRequest requestNetwork = null;
		final LayerBase layer_wifi = Layer.getLayerBase(myactivity,handler);
		isThread1 = SUCC;isThread2=SUCC;
		int ret = -1;
		
		process body
		while(true)
		{
			nkeyIn = gui.cls_show_msg("选择断电时刻\n0.WLAN通讯\n1.无线通讯\n");
			switch (nkeyIn) 
			{
			case '0':
				NetworkRequest.Builder build_wlan = new NetworkRequest.Builder();
				build_wlan.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
				build_wlan.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
				requestNetwork = build_wlan.build();
				break;
				
			case '1':
				NetworkRequest.Builder build_wlm = new NetworkRequest.Builder();
				build_wlm.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
				build_wlm.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
				requestNetwork = build_wlm.build();
				break;
				
			case ESC:
				return;
			}
			if(nkeyIn=='0'||nkeyIn=='1')
				break;
		}
		
		// 测试前置，要先将wifi连接上
		if((ret = layer_wifi.netUp(wifiPara, LinkType.WLAN))!=SUCC)
		{
			gui.cls_show_msg1_record(TAG, "power_off_abnormal", g_keeptime, "line %d:wlan连接失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}

		// 在进行wifi或者无线通讯的时候进行断电操作
		ConnectivityManager.NetworkCallback callBack = new ConnectivityManager.NetworkCallback()
		{
			public void onAvailable(final Network network) 
			{
				super.onAvailable(network);
				new Thread()
				{
					@Override
					public void run() 
					{
						super.run();
						ConnectivityManager.setThreadDefaultNetwork(network);
						for (int i = 0; i < 2; i++) 
						{
							gui.cls_show_msg1(2, "设备即将重启，重启后进入本用例交叉运行正常才可视为通过");
							get_request("Your browse does not support frame!");
							Tools.reboot(myactivity);
						}
						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
						isThread1=10;
					}
				}.start();
				
				new Thread()
				{
					@Override
					public void run() 
					{
						super.run();
						for (int i = 0; i < 2; i++) 
						{
							get_request("Your browse does not support frame!");
						}
						isThread2=10;
					}
				}.start();
			}
		};
		connManager.registerNetworkCallback(requestNetwork, callBack);
		// 等待“线程”请求完毕
		while(isThread1==SUCC||isThread2==SUCC);
	}
	
	// 休眠异常 add by 20170725
	private void sleep_abnormal()
	{
		SettingsManager setManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		setManager.setScreenTimeout(ONE_MIN);
		gui.cls_show_msg("设备休眠时间已设置为1分钟，点击任意键进入交叉测试，交叉测试次数设为50次，在交叉测试通讯进入休眠后，等待2分钟后唤醒");
		// 等待设备主动进入休眠，休眠2分钟后手动唤醒
		// 唤醒之后在进行无线和wifi的同时通讯操作则视为通过
		cross_test();
		gui.cls_show_msg("wlan_wlm休眠唤醒测试通过（交叉测试休眠唤醒前后不影响通讯成功率才可视为测试通过）");
	}

	
	*//**
	 * 无线和wlan交叉
	 *//*
	public void cross_test()
	{
		if(init_wlan==false)
		{
			gui.cls_printf("请先进行wifi的配置".getBytes());
			return;
		}
		 private & local definition 
		int cnt,succ = 0,i=0;
		final LayerBase layer_wifi = Layer.getLayerBase(myactivity,handler);
		LayerBase layer_mobile = Layer.getLayerBase(myactivity, handler);
		final Packet sendPacket = new Packet();
		byte[] buf = new byte[PACKMAXLEN];
		
		// 连接服务
		final ConnectivityManager connectivityManager = (ConnectivityManager) myactivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		MobileUtil mobileUtil = MobileUtil.getInstance(myactivity);
		int ret = -1;
		
		 process body 
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, LinkType.WLAN);
		cnt = sendPacket.getLifecycle();//交叉测试次数
		
		// 测试前置，判断是否插入sim卡
		// 先判断是否插入Sim卡
		if((ret = mobileUtil.getSimState())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime,"line %d:未插入sim卡，请插卡后再进入（ret = %d）",Tools.getLineInfo(),ret);
			return;
		}
		// 设置好要获取的网络
		NetworkRequest.Builder builder = new NetworkRequest.Builder();
		builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);// 蜂窝移动网络
		builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
		NetworkRequest request = builder.build();
		ConnectivityManager.NetworkCallback callBack = new ConnectivityManager.NetworkCallback()
		{
			@Override
			public void onAvailable(final Network network) 
			{
				super.onAvailable(network);
				ava_count++;
				savaData(myactivity, ava_count);
				// 只有第一次onavailable才走thread
				if(getData(myactivity)>1)
					return;
				// thread_1 wlan
				new Thread()
				{
					public void run() 
					{
						// wifi通讯操作
						if(wifi_comm(sendPacket.getHeader(),layer_wifi)!=SUCC)
						{
							ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
							isThread1 = FAIL;
							return;
						}
						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
						isThread1=10;
					};
				}.start();
				
				// 在请求到制定网络后开启线程并调用接口设置线程的网络通道
				// thread_2 移动网络
				new Thread()
				{
					public void run() 
					{
						ConnectivityManager.setThreadDefaultNetwork(network); // 使得无线网络均可用
						if(get_request("京东网上商城")!=SUCC)
						{
							ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
							isThread2 = FAIL;
							return;
						}
						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
						isThread2 = 10;
					};
				}.start();
				
				// 在请求到制定网络后开启线程并调用接口设置线程的网络通道
				// thread_4 移动网络
				new Thread()
				{
					public void run() 
					{
						ConnectivityManager.setThreadDefaultNetwork(network); // 使得无线网络均可用
						if(get_request("京东网上商城")!=SUCC)
						{
							ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
							isThread3 = FAIL;
							return;
						}
						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
						isThread3 = 10;
					};
				}.start();
				
				// 在请求到制定网络后开启线程并调用接口设置线程的网络通道
				// thread_4 移动网络
				new Thread()
				{
					public void run() 
					{
						ConnectivityManager.setThreadDefaultNetwork(network); // 使得无线网络均可用
						if(get_request("京东网上商城")!=SUCC)
						{
							ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
							isThread4 = FAIL;
							return;
						}
						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
						isThread4 = 10;
					};
				}.start();
				
				// thread_5 wifi
				new Thread()
				{
					public void run() 
					{
						// wifi通讯操作
						if(wifi_comm(sendPacket.getHeader(),layer_wifi)!=SUCC)
						{
							ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
							isThread5 = FAIL;
							return;
						}
						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
						isThread5 = 10;
					};
				}.start();
			}
		};
		
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "%s交叉测试，已执行%d次，成功%d次，点【取消】键退出测试...", TESTITEM,i, succ)==ESC)
				break;
			Log.v("wang", "第"+i+"次wifi");
			cnt--;
			i++;
			// 建立移动网络连接
			if((ret = layer_mobile.netUp(mobilePara, LinkType.LTE))!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次打开移动网络失败（ret = %d）", Tools.getLineInfo(),i,ret);
				return;
			}
			
			// 建立wifi连接
			if((ret = layer_wifi.netUp(wifiPara, LinkType.WLAN))!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次开启并连接wlan失败（ret = %d）", Tools.getLineInfo(),i,ret);
				layer_wifi.linkDown(wifiPara, LinkType.WLAN);
//				layer_wifi.netDown(socket_wifi, wifiPara, Sock_t.SOCK_TCP, LinkType.WLAN);
				return;
			}
			// 控制available的调用
			ava_count = 0;
			isThread1 = SUCC;
			isThread2 = SUCC;
			isThread3 = SUCC;
			isThread4 = SUCC;
			isThread5 = SUCC;
			savaData(myactivity, ava_count);
			// 请求网络
			connectivityManager.requestNetwork(request, callBack);
			// 判断available是否调用完毕
			while(isThread1==0||isThread2==0||isThread3==0||isThread4 == 0||isThread5 == 0);
			//解绑
			connectivityManager.unregisterNetworkCallback(callBack);
			if(isThread1==FAIL||isThread2==FAIL||isThread3==FAIL||isThread4==FAIL||isThread5==FAIL)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次wlan或无线通讯操作失败(wifi = %d,无线 = %d,无线=%d,无线=%d,wifi=%d)", Tools.getLineInfo(),i,
						isThread1,isThread2,isThread3,isThread4,isThread5);
				continue;
			}
			if(layer_wifi.linkDown(wifiPara, LinkType.WLAN)!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次断开wlan连接失败", Tools.getLineInfo(),i);
				continue;
			}
			if(layer_wifi.netDown(socket_wifi, wifiPara, Sock_t.SOCK_TCP, LinkType.WLAN)!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次断开wlan连接失败", Tools.getLineInfo(),i);
				continue;
			}
			succ++;
		}
		// 测试后置：断开wifi
		layer_wifi.linkDown(wifiPara, LinkType.WLAN);
//		layer_wifi.netDown(socket_wifi, wifiPara, Sock_t.SOCK_TCP, LinkType.WLAN);
		
		gui.cls_show_msg1_record(TAG, "cross_test", 0, "%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,i, succ);
	}
	

	*//**
	 * 存储数据，用于保存onvailble的调用次数
	 * @param context
	 * @param count
	 *//*
	private void savaData(Context context,int count)
	{
		SharedPreferences sp = context.getSharedPreferences("availble_count", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("count", count);
		editor.commit();
		
	}
	
	private int getData(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("availble_count", Context.MODE_PRIVATE);
		return sp.getInt("count", 0);
	}
	
	*//**
	 * wifi通讯操作
	 * @param sendBuf
	 *//*
	private int wifi_comm(byte[] sendBuf,LayerBase layer_wifi)
	{
		 private & local definition 
		int wlanRet = -1,slen,rlen,startLen=sendBuf.length;
		byte[] recvBuf = new byte[sendBuf.length];
		SocketUtil  my_socket_wifi = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
//		SocketUtil  my_socket_wifi = new SocketUtil(wifiPara.getServerIp(), port);
		 process body 
		if(((wlanRet = layer_wifi.transUp(my_socket_wifi, Sock_t.SOCK_TCP)))!=SUCC)
		{
			gui.cls_show_msg1_record(TAG, "wifi_comm", g_keeptime, "line %d:wlan的TransUp失败(ret = %d)", Tools.getLineInfo(),wlanRet);
			return FAIL;
		}
		
		// 收发数据
		if ((slen = sockSend(my_socket_wifi,sendBuf,startLen, SO_TIMEO, wifiPara)) != startLen) 
		{
			layer_wifi.transDown(my_socket_wifi, Sock_t.SOCK_TCP);
			gui.cls_show_msg1_record(TAG, "wifi_comm", g_keeptime,"line %d:发送数据失败（实际len=%d，预期len=%d）", Tools.getLineInfo(), slen, startLen);
			return FAIL;
		}

		// 接收
		if ((rlen = sockRecv(my_socket_wifi, recvBuf, startLen,SO_TIMEO, wifiPara)) != startLen) 
		{
			layer_wifi.transDown(my_socket_wifi, Sock_t.SOCK_TCP);
			gui.cls_show_msg1_record(TAG, "wifi_comm", g_keeptime,"line %d:接收数据失败（实际len = %d，预期len = %d）", Tools.getLineInfo(),rlen, startLen);
			return FAIL;
		}
		// 比较收发
		if (Tools.memcmp(sendBuf, recvBuf,startLen)==false) 
		{
			layer_wifi.transDown(my_socket_wifi, Sock_t.SOCK_TCP);
			gui.cls_show_msg1_record(TAG, "wifi_comm", g_keeptime, "line %d:校验数据失败",Tools.getLineInfo());
			return FAIL;
		}
		
		layerBase.transDown(my_socket_wifi,Sock_t.SOCK_TCP);
//		//断开
//		if(layer_wifi.netDown(my_socket_wifi, wifiPara, Sock_t.SOCK_TCP, LinkType.WLAN)!=SUCC)
//		{
//			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:断开wlan连接失败", Tools.getLineInfo());
//			return FAIL;
//		}
		return SUCC;
	}
	
	*//**
	 * 无线通讯 使用内网禁止的网络只有无线方式可以连接成功
	 *//*
	public int get_request(String preData)
	{
		URL url;
		String html = null;
		try 
		{
			url = new URL("http://union.click.jd.com/sem.php?source=baidu-pinzhuan&unionId=288551095&siteId=baidupinzhuan_0f3d30c8dba7459bb52f2eb5eba8ac7d&to=http%3a%2f%2fwww.jd.com");// 通过网络地址创建URL对象
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");// 设置URL的请求类别为GET
			conn.setConnectTimeout(5000);// 设置从主机读取数据超时
			conn.setReadTimeout(5000);//设置连接主机超时
			InputStream in = conn.getInputStream();
			byte[] data = readInputStream(in);
			html = new String(data, "utf-8");
			if(html.contains(preData)==false)
			{
//				gui.cls_show_msg1_record(TAG, "get_request", g_keeptime, "line %d:网络访问失败", Tools.getLineInfo());
				return FAIL;
			}
		} catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SUCC;
	}
	
	
	// 多apk异常 add by 20170807wangxy
	private void more_apk_abnormal()
	{
		gui.cls_show_msg("确保wlan与移动网络均为已连接状态，打开MobileSocket.apk同时进行,观察wlan和移动网络的图标，应均显示上下行图标（结束请按取消键）");
		NetworkRequest.Builder build_thread = new NetworkRequest.Builder();
		build_thread.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
		build_thread.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
		NetworkRequest request_wifi = build_thread.build();
		
		ConnectivityManager connManager = (ConnectivityManager) myactivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		ConnectivityManager.NetworkCallback pro_more_callBack = new ConnectivityManager.NetworkCallback()
		{
			public void onAvailable(Network network) 
			{
				super.onAvailable(network);
				savaData(myactivity, ava_count);
				ava_count++;
				if(getData(myactivity)>1)
					return;
				// 进程请求的时wlan
				ConnectivityManager.setProcessDefaultNetwork(network);
				while(true){
					if(gui.cls_show_msg1(1, "进程设置的netId=wlan，请求网页，【取消】退出循环")==ESC)
					{
						isThread1 = 10;
						return;
					}
					if(get_request("Your browse does not support frame!")!=SUCC)
					{
//						isPro1 = FAIL;
						return;
					}
				}
//				isPro1=10;
			};
		};
		
		connManager.requestNetwork(request_wifi, pro_more_callBack);
		gui.cls_show_msg("与MobileSocket.apk同时进行,wlan和移动网络的图标应均显示上下行图标，则测试通过");*/
//	}
//	public byte[] readInputStream(InputStream in)
//	{
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int len = 0;
//		try {
//			while((len = in.read(buffer))!=-1)
//			{
//				out.write(buffer,0,len);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		try {
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return out.toByteArray();
//	}
}
