package com.example.highplattest.main.netutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import com.example.highplattest.main.bean.ApplicationExceptionBean;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public abstract class NetworkUtil implements Lib
{
	private static final String TAG = NetworkUtil.class.getSimpleName();
	/**
	 * 打开网络
	 */
	public abstract void openNet();
	
	/**
	 * 关闭网络
	 */
	public abstract void closeNet();

	/**
	 * 关闭其他网络
	 */
	public abstract void closeOther();

	/**
	 * 获取信号强度
	 * 
	 * @return
	 */
	public abstract int getSignStrength();
//	/**
//	 * 是否连接网络
//	 */
//	public abstract boolean isConnected();
	public abstract String getIp();
	
	
	private static ConnectivityManager gConnManager=null;
	
	public static ConnectivityManager getInstanceConnManager(Context context) {
		if(gConnManager == null)
		{
			gConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return gConnManager;
	}
	
	
	
	public static int waitNetWorkConn(Context context,final int transportType,int millTime)
	{
		LoggerUtil.e("requestNetwork="+transportType);
		int netWorkType = -1;
		if (context != null) 
		{
			ConnectivityManager mConnectivityManager = getInstanceConnManager(context);
			long startTime = System.currentTimeMillis();
			while(true&&Tools.getStopTime(startTime)<(millTime/1000))
			{
				NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
				if (mNetworkInfo != null && mNetworkInfo.isConnected()) 
				{
					LoggerUtil.e("NetWorkType="+mNetworkInfo.getType());
					netWorkType = mNetworkInfo.getType();
					break;
				}
				SystemClock.sleep(100);
			}
		}
		return netWorkType;
		/*final Object lockObj = new Object();
		final String[] workType = {"",""};
		NetworkRequest.Builder netBuild = new NetworkRequest.Builder();
		
		netBuild.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);
		netBuild.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
		NetworkRequest nr = netBuild.build();
		ConnectivityManager.NetworkCallback nc = new ConnectivityManager.NetworkCallback()
		{
			@Override
			public void onAvailable(Network network) {
				super.onAvailable(network);
				LoggerUtil.e("type="+transportType+"||||"+network.toString());
				workType[0] = network.toString();
				synchronized (lockObj) {
					lockObj.notify();
				}
			}
		};
		LoggerUtil.e("requestNetwork="+transportType);
		getInstanceConnManager(context).requestNetwork(nr, nc);
		synchronized (lockObj) {
			try {
				lockObj.wait(millTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return workType[0];*/
	}
	
	/*  *//**
	   * 判断是否有网络连接
	   * @return
	  *//*
	public	static boolean isNetConnected(Context context) 
	{
		if (context == null)
        {
            return false;
        }
        ConnectivityManager connectivity = getInstanceConnManager(context);
        if (connectivity == null)
        {
            return false;
        } else
        {
            Network[] networks = connectivity.getAllNetworks();
            if (networks != null)
            {
            	
                for (Network mNetwork : networks)
                {
                    if (connectivity.getNetworkInfo(mNetwork).getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
	}*/
	
	// 检测当前网络是否连接
	public static int checkNet(Context context) 
	{
		int netWorkType =-1;
		if (context != null) 
		{
			ConnectivityManager mConnectivityManager = getInstanceConnManager(context);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isConnected()) 
			{
//				if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) 
//                    return true;  
				LoggerUtil.e("NetWorkType="+mNetworkInfo.getType());
				netWorkType= mNetworkInfo.getType();
			}
		}
		return netWorkType;
	}
	


	/**
	 * ping ip 地址
	 * 
	 * @param address
	 * @return 返回结果
	 * @throws IOException
	 */
	public static boolean ping(String address) throws IOException {
		boolean success = false;

		StringBuilder pingStr = new StringBuilder("ping -c 5 -i 0.5 -s 32 ");
		pingStr.append(address);
		Log.e(TAG, "{pingStr}" + pingStr);
		Process process = Runtime.getRuntime().exec(pingStr.toString());
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = br.readLine();	// 第一句显示：PING 61.135.169.125 (61.135.169.125) 32(60) bytes of data.

		while((line = br.readLine()) != null){
			Log.e(TAG, line);
			if(line.contains("bytes from "+address)){
				success = true;
				break;
			}else{
				success = false;
				break;
			}
//			SystemClock.sleep(100);
		}
		br.close();
		process.destroy();
		return success;
	}
	
	/**
	 * ping测试，返回字符串
	 * @param address	服务端ip地址
	 * @return
	 * @throws IOException
	 */
	public static String pingForString(String address) throws IOException{
		
		StringBuilder pingResult = new StringBuilder();
		StringBuilder pingStr = new StringBuilder("ping -c 5 -i 0.5 -s 32 ");
		pingStr.append(address);
		
		Process process = Runtime.getRuntime().exec(pingStr.toString());
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
//		String line = br.readLine();
//		if (line != null) {
//			Log.e(TAG, line);
//		}
		String line = null;
		while ((line = br.readLine()) != null) {
			pingResult.append(line).append("\n");
//			Log.e(TAG, line);
			SystemClock.sleep(100);
		}
		br.close();
		process.destroy();
		return pingResult.toString();
	}

	/**
	 * PING测试<p>
	 * 
	 * 3G上网卡拨号需要一段时间
	 * 
	 * @param serverIp	服务端IP地址
	 * @param timeout	超时时间
	 * @throws IOException 
	 */
	public static String pingTest(String serverIp, int timeout) throws ApplicationExceptionBean {
		try {
			Date startTime = new Date();
			Date endTime = null;
			long result = 0;
			do {
				if(NetworkUtil.ping(serverIp)){
					return NetworkUtil.pingForString(serverIp);
				}
				endTime = new Date();
				result = (endTime.getTime()-startTime.getTime())/1000;
				SystemClock.sleep(DefaultTimePiece);
			} while (result < timeout);
			throw new ApplicationExceptionBean(MOBILE_FAILED, "连接服务器失败！");
		} catch (Exception e) {
			throw new ApplicationExceptionBean(MOBILE_FAILED, e.getMessage());
		}
	}



	/**
     * 验证ip是否合法
     * 
     * @param text
     *            ip地址
     * @return 验证信息
     */
    @SuppressLint("NewApi")
	public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        // 返回判断信息
        return false;
    }
}
