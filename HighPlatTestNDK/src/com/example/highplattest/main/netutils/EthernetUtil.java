package com.example.highplattest.main.netutils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;

import android.content.Context;
import android.net.ConnectivityManager;
import android.newland.net.ethernet.NlEthernetManager;
import android.os.Handler;
import android.os.SystemClock;

/************************************************************************
 * 
 * module 			: main
 * file name 		: EthernetUtil.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 以太网工具类
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class EthernetUtil extends NetworkUtil
{

	private static EthernetUtil ethernetUtil = null;
	
	public static NlEthernetManager mEthernetManager;
	private Context mContext;
	private static Handler mHandler;
	
	public static EthernetUtil getInstance(Context context,Handler handler) {
		if(ethernetUtil == null)
		{
			ethernetUtil = new EthernetUtil(context,handler);
			mHandler = handler;
		}
		return ethernetUtil;
		
	}
	
	private EthernetUtil(Context context,Handler handler) {
		mContext = context;
		handler.sendEmptyMessage(1122);
	}
	
	/**
	 * 打开以太网
	 */
	public void openNet() {
		closeOther();
		if(mEthernetManager != null)
		{
			mEthernetManager.start();
			SystemClock.sleep(500);
		}
	}
	/**
	 * 关闭其他网络
	 */
	public void closeOther()
	{
		if(NetworkUtil.checkNet(mContext)==ConnectivityManager.TYPE_WIFI)
			WifiUtil.getInstance(mContext,mHandler).closeNet();
		
		if(NetworkUtil.checkNet(mContext)==ConnectivityManager.TYPE_MOBILE)
		{
//			if (GlobalVariable.currentPlatform==Model_Type.X1) {
//				//   X1不支持该方法 会崩溃 MobileUtil.getInstance(mContext).closeNet();
//			}else {
				MobileUtil.getInstance(mContext,mHandler).closeNet();
//			}
		}
	}
	
	/**
	 * 关闭以太网
	 */
	public void closeNet() 
	{
		if(mEthernetManager != null)
		{
			mEthernetManager.stop();
		}
/*//		NetworkUtil.enableEthernet(mContext, false);//20170209wangxiaoyu
		if(mEthernetManager != null)
//			mEthernetManager.setEthernetEnabled(false);
			mEthernetManager.stop();*/
	}
	
	/**获取以太网状态值*/
	public int getStatus()
	{
		int status = mEthernetManager.getStatus();
		return status;
	}
	
	/**获取以太网状态值*/
	public int getEthernetStatus()
	{
		int status = mEthernetManager.getEthernetStatus();
		return status;
	}
	
	public void setEthIp(EthernetPara ethernetPara)
	{
		
		if(ethernetPara.isDHCPenable())
			mEthernetManager.setDhcp();
		else
		{
			
		}
	}


	@Override
	public String getIp() {
		return "";
	}

	@Override
	public int getSignStrength() {
		return 0;
	}
	/**
	 * 重新打开以太网
	 */
	public void reOpenEthernet() {
//		closeEthernet();
//		SystemClock.sleep(1000);
//		openEthernet();
	}
	
	/**
	 * 设置以太网参数
	 * 
	 * @param context
	 * @param ethernetPara
	 */
	public void setEthernetIp(String ip,String gateway,String netMask,String dns1,String dns2) 
	{
		/*if(mEthernetManager == null)
			return;
     需要测试人员手动在系统设置中设置以太网参数，如ip，usb ip，网关等20170209wangxiaoyu
 		System.putInt(mContext.getContentResolver(),
				System.ETHERNET_USE_STATIC_IP, 1);
		System.putString(mContext.getContentResolver(),
				System.ETHERNET_STATIC_IP, ip);
		System.putString(mContext.getContentResolver(),
				System.ETHERNET_STATIC_GATEWAY, gateway);
		System.putString(mContext.getContentResolver(),
				System.ETHERNET_STATIC_NETMASK, netMask);
		System.putString(mContext.getContentResolver(),
				System.ETHERNET_STATIC_DNS1, dns1);
		System.putString(mContext.getContentResolver(),
				System.ETHERNET_STATIC_DNS2, dns2);
//		mEthernetManager.setEthernetEnabled(false);
		mEthernetManager.stop();
		SystemClock.sleep(500);
//		mEthernetManager.setEthernetEnabled(true);
		mEthernetManager.start();*/
	}

	/**
	 * 设置以太网静态IP地址
	 * 
	 * @param context
	 * @param ethernetPara
	 *//*
	public static void setEthernetIp(Context context,String ip,String gateway,String netMask,String dns1,String dns2) {
		EthernetManager mEthernetManager = (EthernetManager) context
				.getSystemService(Context.ETHERNET_SERVICE);
		System.putInt(context.getContentResolver(),
				System.ETHERNET_USE_STATIC_IP, 1);
		System.putString(context.getContentResolver(),
				System.ETHERNET_STATIC_IP, ip);
		System.putString(context.getContentResolver(),
				System.ETHERNET_STATIC_GATEWAY, gateway);
		System.putString(context.getContentResolver(),
				System.ETHERNET_STATIC_NETMASK, netMask);
		System.putString(context.getContentResolver(),
				System.ETHERNET_STATIC_DNS1, dns1);
		System.putString(context.getContentResolver(),
				System.ETHERNET_STATIC_DNS2, dns2);
		mEthernetManager.setEthernetEnabled(false);
		SystemClock.sleep(500);
		mEthernetManager.setEthernetEnabled(true);
	}*/

	
//	private void registerEthernet() {
//	    IntentFilter ethFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
//	    ethFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
//	    registerReceiver(mEthConnectReceiver, ethFilter);
//	}
//
//	private BroadcastReceiver mEthConnectReceiver = new BroadcastReceiver() {
//	    @Override
//	    public void onReceive(Context context, Intent intent) {
//	        String action = intent.getAction();
//	        Log.d(TAG, " Ethernet onRecevice action = " + action);
//	        if (action.equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
//	            int msg = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, -1);
//	            switch (msg) {
//	            case EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED:
//	                break;
//	            case EthernetDataTracker.EVENT_DHCP_DISCONNECT_FAILED:
//	                break;
//	            case EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED:
//	                break;
//	            case EthernetDataTracker.EVENT_STATIC_DISCONNECT_FAILED:
//	                break;
//	            case EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED:
//	                pingEthGateway(getEthGateWay());//ping命令DHCP连接网络是否成功
//	                break;
//	            case EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED:
//	                break;
//	            case EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED:
//	                pingEthGateway(getEthGateWay());//ping命令测试静态IP连接网络是否成功
//	                break;
//	            case EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED:
//	                break;
//	            case EthernetDataTracker.EVENT_PHY_LINK_UP:
//	                setDhcp();//网线插上后自己进行DHCP
//	                break;
//	            case EthernetDataTracker.EVENT_PHY_LINK_DOWN:
//	                Log.d(TAG, "EVENT_PHY_LINK_DOWN ");//网线拔出
//	                break;
//	            default:
//	                break;
//	            }
//	        }
//	    }
//	};
	
//	public void setDhcp() {
//	    mEthernetManager.setEthernetEnabled(false);
//	    Log.i(TAG, "getEthernetState=" + mEthernetManager.getEthernetState());
//	    mEthernetManager.setEthernetDefaultConf();
//	    mEthernetManager.setInterfaceName("eth0");
//	    mEthernetManager.setEthernetEnabled(true);
//	    Log.i(TAG, "getEthernetState=" + mEthernetManager.getEthernetState());
//	}

}
