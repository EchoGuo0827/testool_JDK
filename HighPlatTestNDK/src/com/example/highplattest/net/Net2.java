package com.example.highplattest.net;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.SSLHandshakeException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.newland.telephony.TelephonyManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
/************************************************************************
 * module 			: 网络
 * file name 		: Net2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170706 
 * directory 		: 
 * description 		: wlan与移动网络并存测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20170706	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Net2 extends UnitFragment
{
	private final String TESTITEM = "wlan与移动网络共存";
	private final String CLASS_NAME = Net2.class.getSimpleName();
	private final int WIFI_WAY = 1;
	private final int MOBILE_WAY = 2;
	private Gui gui = new Gui(myactivity, handler);
//	private boolean isWifiEnd = false,isMobileEnd=false;
	private boolean isWifiEnd = false,isMobileEnd=false,isWifi=false,isMobile=false;
	String funcName="net2";
	private HandlerThread wifiHandler = new HandlerThread("using wifi");
	private HandlerThread mobileHandler = new HandlerThread("using mobile");
	private Object lockWifi = new Object();
	private Object lockMobile = new Object();

	private Object lockWifi_pro = new Object();
	private Object lockMobile_pro = new Object();
	private int mWifiCount = 1,mMobileCount=1;// 默认次数设置
	
	public void net2()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"%s不支持自动化测试",  TESTITEM);
			return;
		}
		
		/* private & local definition*/
		wifiHandler.start();
		mobileHandler.start();
		final Handler netHandler_wifi = new Handler(wifiHandler.getLooper())
		{
			public void handleMessage(android.os.Message msg) 
			{
				Network network = (Network) msg.obj;
				switch (msg.what) {
				case WIFI_WAY://wifi
					LoggerUtil.v("enter wifi net operate");
					// 设置该线程需要绑定的网络
					TelephonyManager.setThreadDefaultNetwork(network);
					while(mWifiCount>0)
					{
						LoggerUtil.i("wifi count="+mWifiCount);
						// HttpURLConnection有缓存机制，无线访问成功后，会缓存下来，wifi也可使用，所以修改为不同网址
						isWifiEnd = get_request("https://www.taobao.com","淘宝",true)==SSL_Handshake_Exception&&get_request("https://www.baidu.com/","百度一下",true)==SUCC?true:false;
						if(isWifiEnd==false)
							break;
						mWifiCount--;
					}
					LoggerUtil.i("exit wifi");
					synchronized (lockWifi) {
						lockWifi.notify();
					}
					// 使用结束后，适当位置解绑此线程对应的网络
					TelephonyManager.unBindNetworkForThread();
					break;
					
				default:
					break;
				}
			};
		};
		final Handler netHandler_mobile = new Handler(mobileHandler.getLooper())
		{
			public void handleMessage(android.os.Message msg) 
			{
				Network network = (Network) msg.obj;
				switch (msg.what) {
					
				case MOBILE_WAY://mobile
					LoggerUtil.v("enter mobile net operate");
					TelephonyManager.setThreadDefaultNetwork(network);
					while(mMobileCount>0)
					{
						isMobileEnd = get_request("https://www.jd.com/","京东JD.COM",false)!=SUCC?false:true;
						if(isMobileEnd==false)
							break;
						mMobileCount--;
					}
					LoggerUtil.v("exit mobile");
					synchronized (lockMobile) {
						lockMobile.notify();
					}
					TelephonyManager.unBindNetworkForThread();
					break;

				default:
					break;
				}
			};
		};
		final ConnectivityManager.NetworkCallback wifiCallBack = new ConnectivityManager.NetworkCallback()
		{
			public void onAvailable(Network network) 
			{
				// 网络可用时回调
				super.onAvailable(network);
				Message msg = Message.obtain();
				msg.what = WIFI_WAY;
				msg.obj = network;
				netHandler_wifi.sendMessage(msg);// 通知子线程可以绑定该网络了
			};
		};
		final ConnectivityManager.NetworkCallback mobileCallback = new ConnectivityManager.NetworkCallback()
		{
			public void onAvailable(Network network) 
			{
				// 网络可用时回调
				super.onAvailable(network);
				Message msg = Message.obtain();
				msg.what = MOBILE_WAY;
				msg.obj = network;
				netHandler_mobile.sendMessage(msg);// 通知子线程可以绑定该网络了
			};
		};
		
		final ConnectivityManager connManager = (ConnectivityManager) myactivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// 测试前置，wlan与移动网络均为连接状态
		gui.cls_show_msg("请保证wlan与移动网络均为已连接状态");
		
		// case1:未开启wifi和移动网络，直接request能成功吗？
		
		// case2.1:线程设置netId为移动网络
		gui.cls_printf("网络共存mobile线程通讯测试".getBytes());
		// 构建请求网络需要的参数
		mMobileCount=1;
		NetworkRequest.Builder builer_process = new NetworkRequest.Builder();
		builer_process.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);// 指定为蜂窝类型(4G)
		builer_process.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 指定internet承载
		NetworkRequest request_mobile = builer_process.build();
		connManager.requestNetwork(request_mobile, mobileCallback);// 请求建立该网络（4G）
		// 等待"进程"请求完毕
		synchronized (lockMobile) {
			isMobileEnd = false;
			try {
				lockMobile.wait(30*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		connManager.unregisterNetworkCallback(mobileCallback);
		LoggerUtil.v("net end");
		SystemClock.sleep(2000);
		if(isMobileEnd==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:线程netID=移动网络通讯失败（%s）", Tools.getLineInfo(),isMobileEnd);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:线程设置netId为wifi
		gui.cls_printf("网络共存wifi线程通讯测试".getBytes());
		mWifiCount=1;
		NetworkRequest.Builder build_thread = new NetworkRequest.Builder();
		build_thread.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
		build_thread.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
		NetworkRequest request_wifi = build_thread.build();
		connManager.requestNetwork(request_wifi, wifiCallBack);
		
		synchronized (lockWifi) {
			isWifiEnd = false;
			try {
				lockWifi.wait(30*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		connManager.unregisterNetworkCallback(wifiCallBack);
		SystemClock.sleep(2000);
		if(isWifiEnd==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:线程netID=wifi网络通讯失败(%s)", Tools.getLineInfo(),isWifiEnd);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:开启两个线程，线程开启wifi，一个线程开启移动网络
		gui.cls_printf("网络共存wifi和mobile线程并发通讯测试".getBytes());
		Thread wifiThread = new Thread()
		{
			public void run() 
			{
				isWifiEnd=false;
				mWifiCount = 20;
				NetworkRequest.Builder build_thread = new NetworkRequest.Builder();
				build_thread.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
				build_thread.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
				NetworkRequest request_wifi = build_thread.build();
				connManager.requestNetwork(request_wifi, wifiCallBack);
			};
		};
		
		Thread mobileThread = new Thread()
		{
			public void run() 
			{
				isMobileEnd=false;
				mMobileCount=20;
				NetworkRequest.Builder build_thread = new NetworkRequest.Builder();
				build_thread.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
				build_thread.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
				NetworkRequest request_wifi = build_thread.build();
				connManager.requestNetwork(request_wifi, mobileCallback);
			};
		};
		wifiThread.start();
		mobileThread.start();
		synchronized (lockMobile) {
			try {
				lockMobile.wait(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized (lockWifi) {
			try {
				lockWifi.wait(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		SystemClock.sleep(2000);
		if(isMobileEnd==false||isWifiEnd==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:网络通讯失败(wifi=%s,mobile=%s)", Tools.getLineInfo(),isWifiEnd,isMobileEnd);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 注销wifi和移动网络
		connManager.unregisterNetworkCallback(wifiCallBack);
		connManager.unregisterNetworkCallback(mobileCallback);
		
		//case3.2:线程网络优先级通讯测试 //by weimj
				gui.cls_printf("线程网络优先级通讯测试".getBytes());
				final ConnectivityManager.NetworkCallback pro_callBack = new ConnectivityManager.NetworkCallback()
				{
					public void onAvailable(Network network) 
					{
						super.onAvailable(network);
						connManager.setProcessDefaultNetwork(network);
						LoggerUtil.v("enter wifi net operate");
						isWifi=false;
						mWifiCount=1;
						while(mWifiCount>0)
						{
							LoggerUtil.i("wifi count="+mWifiCount);
							isWifi = get_request("https://www.taobao.com","淘宝",true)==SSL_Handshake_Exception&&get_request("https://www.baidu.com/","百度一下",true)==SUCC?true:false;
							LoggerUtil.e("isWifi ="+isWifi);
							if(isWifi==false)
								break;
							mWifiCount--;
						}
						LoggerUtil.i("exit wifi");
						synchronized (lockWifi_pro) {
							lockWifi_pro.notify();
						}
					};
				};
				NetworkRequest.Builder build_pro = new NetworkRequest.Builder();
				build_pro.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
				build_pro.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
				NetworkRequest request_pro = build_pro.build();
				connManager.requestNetwork(request_pro, pro_callBack);
				
				final ConnectivityManager.NetworkCallback thr_callBack = new ConnectivityManager.NetworkCallback()
				{
					public void onAvailable(final Network network) 
					{
						super.onAvailable(network);

						isMobile=false;
						mMobileCount=1;
						LoggerUtil.v("enter mobile net operate");
						TelephonyManager.setThreadDefaultNetwork(network);
						while(mMobileCount>0)
						{
							LoggerUtil.i("mobile count="+mMobileCount);
							isMobile = get_request("https://www.jd.com/","京东JD.COM",false)!=SUCC?false:true;
							LoggerUtil.i("isMobile="+isMobile);
							if(isMobile==false)
								break;
							mMobileCount--;
						}
						TelephonyManager.unBindNetworkForThread();
						LoggerUtil.i("exit mobile");
						synchronized (lockMobile_pro) {
							lockMobile_pro.notify();
						}	
					};
				};
				
				// thread线程请求移动网络
				new Thread()
				{
					@Override
					public void run() 
					{
						super.run();
						NetworkRequest.Builder builer_thr = new NetworkRequest.Builder();
						builer_thr.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
						builer_thr.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
						NetworkRequest request_thr = builer_thr.build();
						connManager.requestNetwork(request_thr, thr_callBack);
					}
				}.start();
				synchronized (lockMobile_pro) {
					try {
						lockMobile_pro.wait(60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				connManager.unregisterNetworkCallback(pro_callBack);
				SystemClock.sleep(2000);
				LoggerUtil.i("isMobile="+isMobile);
				if( isMobile==false||isWifi==false)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:网络通讯失败(wifi=%s,mobile=%s)", Tools.getLineInfo(),isWifi,isMobile);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case4:重复为线程绑定时取最新的值,先绑定为wifi，再绑定为mobile，线程应使用mobile通讯
				gui.cls_printf("网络共存wifi和mobile线程重复绑定测试".getBytes());
				
				final ConnectivityManager.NetworkCallback wCallBack = new ConnectivityManager.NetworkCallback()
				{
					public void onAvailable(Network network) 
					{
						// 网络可用时回调
						super.onAvailable(network);
//						Message msg = Message.obtain();
//						msg.what = WIFI_WAY;
//						msg.obj = network;
//						netHandler_wifi.sendMessage(msg);// 通知子线程可以绑定该网络了
						LoggerUtil.v("enter wifi net operate");
						// 设置该线程需要绑定的网络
						TelephonyManager.setThreadDefaultNetwork(network);
						while(mWifiCount>0)
						{
							LoggerUtil.i("wifi count="+mWifiCount);
							// HttpURLConnection有缓存机制，无线访问成功后，会缓存下来，wifi也可使用，所以修改为不同网址
							isWifiEnd = get_request("https://www.taobao.com","淘宝",true)==SSL_Handshake_Exception&&get_request("https://www.baidu.com/","百度一下",true)==SUCC?true:false;
							isMobileEnd = false;
							if(isWifiEnd==false)
								break;
							mWifiCount--;
						}
						LoggerUtil.i("exit wifi");
						synchronized (lockWifi) {
							lockWifi.notify();
						}
						// 使用结束后，适当位置解绑此线程对应的网络
						TelephonyManager.unBindNetworkForThread();
					};
				};
				final ConnectivityManager.NetworkCallback mCallBack = new ConnectivityManager.NetworkCallback()
				{
					public void onAvailable(Network network) 
					{
						// 网络可用时回调
						super.onAvailable(network);
//						Message msg = Message.obtain();
//						msg.what = MOBILE_WAY;
//						msg.obj = network;
//						netHandler_mobile.sendMessage(msg);// 通知子线程可以绑定该网络了
						LoggerUtil.v("enter mobile net operate");
						TelephonyManager.setThreadDefaultNetwork(network);
						while(mMobileCount>0)
						{
							isMobileEnd = get_request("https://www.jd.com/","京东JD.COM",false)!=SUCC?false:true;
							isWifiEnd = false;
							if(isMobileEnd==false)
								break;
							mMobileCount--;
						}
						LoggerUtil.v("exit mobile");
						synchronized (lockMobile) {
							lockMobile.notify();
						}
						TelephonyManager.unBindNetworkForThread();
					};
				};
				Thread connectThread = new Thread()
				{
					public void run() 
					{
						//设置线程网络为WLAN
						isWifiEnd=false;
						mWifiCount = 1;
						NetworkRequest.Builder build_thread = new NetworkRequest.Builder();
						build_thread.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
						build_thread.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 设置感兴趣的网络功能
						NetworkRequest request_wifi = build_thread.build();
						connManager.requestNetwork(request_wifi, wCallBack);
						
						//设置线程网络为移动网
						isMobileEnd=false;
						mMobileCount=1;
						NetworkRequest.Builder builer_process = new NetworkRequest.Builder();
						builer_process.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);// 指定为蜂窝类型(4G)
						builer_process.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);// 指定internet承载
						NetworkRequest request_mobile = builer_process.build();
						connManager.requestNetwork(request_mobile, mCallBack);// 请求建立该网络（4G）
						
					};
					
				};
				connectThread.start();
				// 等待"进程"请求完毕
				synchronized (lockMobile) {
					isMobileEnd = false;
					try {
						lockMobile.wait(30*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				synchronized (lockWifi) {
					try {
						lockWifi.wait(30*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				LoggerUtil.v("net end");
				if(isMobileEnd==false||isWifiEnd==true)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:线程重复绑定更新网络通讯失败（isMobileEnd=%s,isWifiEnd=%s）", Tools.getLineInfo(),isMobileEnd,isWifiEnd);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				connManager.unregisterNetworkCallback(wCallBack);
				connManager.unregisterNetworkCallback(mCallBack);
		
		
		
		
		
		
		// case5:通讯过程中应有4G图标显示 add by 20190724 zhengxq
		gui.cls_show_msg("并发通讯测试完毕,即将开始4G图标显示测试,点任意键开始测试");
		NetworkRequest request_mobible_test = builer_process.build();
		connManager.requestNetwork(request_mobible_test, mobileCallback);// 请求建立该网络（4G）
		if(gui.cls_show_msg("是否有4G图标显示")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:并发过程中无4G图标显示", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		connManager.unregisterNetworkCallback(mobileCallback);
		
		// case4:wifi进行长时间请求网页测试，蜂窝进行下载文件操作
		
//		// case3.1:线程结束后进程设置的netID应生效（上一个进程netID=wlm应生效）20170803wangxy
//		if(get_request("京东网上商城")!=SUCC)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:线程结束后应返回进程的netID通道", Tools.getLineInfo());
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
		// case2.2:Process设置netId=wlan，Thread设置netid=移动网络，应线程设置的网络优先有效
		// 本onavailable为进程设置netId为wlan
//		ConnectivityManager.NetworkCallback pro_wifi_callBack2_2 = new ConnectivityManager.NetworkCallback()
//		{
//			public void onAvailable(Network network) 
//			{
//				super.onAvailable(network);
//				gui.cls_show_msg1(2, "2.1进程为wifi", TESTITEM);
//				
//				// 进程请求的时移动网络
//				ConnectivityManager.setProcessDefaultNetwork(network);
//				if(get_request("Your browse does not support frame!")!=SUCC)
//				{
//					isPro1 = FAIL;
//					return;
//				}
//				isPro1=10;
//				connManager.unregisterNetworkCallback(this);
//			};
//		};
//		connManager.requestNetwork(request_wifi, pro_wifi_callBack2_2);
//		// 等待"进程"请求完毕
//		while(isPro1==SUCC);
//		
//		if(isPro1==FAIL)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:进程netID=Wlan网络通讯失败（%d）", Tools.getLineInfo(),isPro1);
//			return;
//		}
//		// 本onavailable为线程设置netId=移动网络
//		ConnectivityManager.NetworkCallback thr_callBack2_2 = new ConnectivityManager.NetworkCallback()
//		{
//			public void onAvailable(final Network network) 
//			{
//				super.onAvailable(network);
//				gui.cls_show_msg1(2, "2.2线程为无线", TESTITEM);
//				
//				// thread1线程请求移动网络
//				new Thread()
//				{
//					@Override
//					public void run() 
//					{
//						super.run();
//						ConnectivityManager.setThreadDefaultNetwork(network);
//						if(get_request("京东网上商城")!=SUCC)
//						{
//							isThread1 = FAIL;
//							return;
//						}
//						ConnectivityManager.unBindNetworkForThread();// 清除线程绑定的网络
//						isThread1=10;
//					}
//				}.start();
//				
//				// thread2线程请求Wlan
//				new Thread()
//				{
//					@Override
//					public void run() 
//					{
//						super.run();
//						if(get_request("Your browse does not support frame!")!=SUCC)
//						{
//							isThread2 = FAIL;
//							return;
//						}
//						isThread2=10;
//					}
//				}.start();
//				
//			};
//		};
//		connManager.requestNetwork(request_mobile, thr_callBack2_2);
//		// 等待“线程”请求完毕
//		while(isThread1==SUCC||isThread2==SUCC);
//		//开发范建斌建议在此处再解绑，而非线程请求结束后就解绑
//		connManager.unregisterNetworkCallback(thr_callBack2_2);
//		if(isThread1==FAIL||isThread2==FAIL)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:进程netID=Wlan网络通讯失败（thread1=%d，thread2 = %d）", Tools.getLineInfo(),isThread1,isThread2);
//			return;
//		}
//		
//		// case3.2:线程结束后进程设置的netID应生效（上一个进程netID=wlan应生效）
//		if(get_request("Your browse does not support frame!")!=SUCC)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:线程结束后应返回进程的netID通道", Tools.getLineInfo());
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		// case4:网络共存时，使用wifi时图标应变为wifi，使用移动网络时图标应变为移动网络
//		// 设置Process的netID = 移动网络
//		isPro1 = SUCC;
//		ConnectivityManager.NetworkCallback pro_wlm_callBack4 = new ConnectivityManager.NetworkCallback()
//		{
//			@Override
//			public void onAvailable(Network network) 
//			{
//				super.onAvailable(network);
//				gui.cls_show_msg1(2, "3.1进程为无线", TESTITEM);
//
//				ConnectivityManager.setProcessDefaultNetwork(network);
//				if(get_request("京东网上商城")!=SUCC)
//				{
//					isPro1 = FAIL;
//					return;
//				}
//				isPro1 = 10;
////				connManager.unregisterNetworkCallback(this);//在循环结束后再解绑
//
//			}
//		};
//		connManager.requestNetwork(request_mobile, pro_wlm_callBack4);
//		
//		while(isPro1==SUCC);
//		if(isPro1==FAIL)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:进程netID=无线网络通讯失败（%d）", Tools.getLineInfo(),isPro1);
//			return;
//		}
//
//		//线程锁
//		final Num num=new Num();
//		isThread1 = SUCC;
//		isThread2 = SUCC;
//		// 本onavailable为线程设置netId为wifi
//		ConnectivityManager.NetworkCallback thr_callBack4 = new ConnectivityManager.NetworkCallback() {
//			public void onAvailable(final Network network) {
//				super.onAvailable(network);
//				gui.cls_show_msg1(2, "3.2线程为wifi", TESTITEM);
//				
//				// thread1线程请求wifi
//				new Thread() {
//
//					@Override
//					public void run() {
//						super.run();
//						
//						while (num.i <= 100) {
//							synchronized (num) {
//								if (num.flag) {
//									try {
//										num.wait();
//									} catch (Exception e) {
//									}
//								} else {
//									
//									if (gui.cls_show_msg1(1,"第" + num.i + "次,wlan网络图标测试，应只看到wlan网络图标有上下行指标图标，【取消】退出循环") == ESC) {
//										isThread1 = 10;
//										num.i = 101;
//									}
//									ConnectivityManager.setThreadDefaultNetwork(network);
//									if (get_request("Your browse does not support frame!") != SUCC) {
//										gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:第%d次访问wlan失败",Tools.getLineInfo(), num.i);
//										isThread1 = FAIL;
//										return;
//									}
//									try {
//										Thread.sleep(5000);
//
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//									num.i++;
//									num.flag = true;
//									num.notify();
//								}
//							}
//						}
//						
//						isThread1 = 10;
//					}
//				}.start();
//
//				// thread2 netID = wlm
//				new Thread() {
//					@Override
//					public void run() {
//						super.run();
//						while (num.i <= 100) {
//							synchronized (num) //必须要用一把锁对象，这个对象是num  
//							{
//								if (!num.flag) {
//									try {
//										num.wait(); // 操作wait()函数的必须和锁是同一个
//									} catch (Exception e) {
//									}
//								} else {
//									if (gui.cls_show_msg1(1,"第" + num.i + "次,移动网络图标测试，应只看到移动网络图标有上下行指标图标，【取消】退出循环") == ESC) {
//										isThread2 = 10;
//										num.i = 101;
//									}
//
//									if (get_request("京东网上商城") != SUCC) {
//										gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:第%d次访问移动网络失败",Tools.getLineInfo(), num.i);
//										isThread2 = FAIL;
//										return;
//									}
//									try {
//										Thread.sleep(5000);
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//									num.i++;
//									num.flag = false;
//									num.notify();
//								}
//							}
//
//						}
//						isThread2 = 10;
//					}
//				}.start();
//				
//			}
//		};
//		connManager.requestNetwork(request_wifi, thr_callBack4);
//		// 等待“线程”请求完毕
//		while(isThread1==SUCC||isThread2==SUCC);
//		//在100次测试结束后做关闭操作
//		TelephonyManager.unBindNetworkForThread();// 清除线程绑定的网络
//		connManager.unregisterNetworkCallback(pro_wlm_callBack4);
//		connManager.unregisterNetworkCallback(thr_callBack4);
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"请分别使用高、中、低电量的电池测试均无问题视为测试通过", TESTITEM);
	}
	
	
	/**
	 * 无线通讯 使用内网禁止的网络只有无线方式可以连接成功
	 */
	public int get_request(String url_name,String preData,boolean isWifi)
	{
		LoggerUtil.i("get_request=url"+url_name+","+Thread.currentThread().getName()+":"+Thread.currentThread().getId());
		int ret = SUCC;
		URL url;
		String html = null;
		try 
		{
//			url = new URL("http://union.click.jd.com/sem.php?source=baidu-pinzhuan&unionId=288551095&siteId=baidupinzhuan_0f3d30c8dba7459bb52f2eb5eba8ac7d&to=http%3a%2f%2fwww.jd.com");// 通过网络地址创建URL对象
			url = new URL(url_name);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");// 设置URL的请求类别为GET
			conn.setConnectTimeout(5000);// 设置从主机读取数据超时
			conn.setReadTimeout(5000);//设置连接主机超时
//			conn.setDoOutput(true);
			conn.setUseCaches(false);// 清除连接的缓存
			conn.setDefaultUseCaches(false);
			InputStream in = conn.getInputStream();
			byte[] data = readInputStream(in);
			html = new String(data, "utf-8");
			LoggerUtil.v(html);
			if(html.contains(preData)==false&& html.contains("302 Found")==false)
			{
				ret= FAIL;
			}
		} catch (MalformedURLException e) 
		{
			e.printStackTrace();
			ret = Malformed_URL_Exception;
		} 
		catch (SSLHandshakeException e) {
			e.printStackTrace();
			ret = SSL_Handshake_Exception; 
		}
		catch (IOException e) {
			ret = IO_Exception;
		}
		return ret;
	}
	
	public byte[] readInputStream(InputStream in)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while((len = in.read(buffer))!=-1)
			{
				out.write(buffer,0,len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
