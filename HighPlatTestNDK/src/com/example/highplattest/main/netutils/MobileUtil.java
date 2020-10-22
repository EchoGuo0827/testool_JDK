package com.example.highplattest.main.netutils;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.LoggerUtil;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;

/**
 *  无线工具类
 * 
 */
public class MobileUtil extends NetworkUtil implements NDK
{

	private static MobileUtil mobileUtil = null;

	private TelephonyManager mTelephonyManager = null;
	private Context context = null;
	private static Handler mHandler = null;
	int signStrength;
	
	public static MobileUtil getInstance(Context context,Handler handler) 
	{
		if (mobileUtil == null) {
			mobileUtil = new MobileUtil(context);
			mHandler = handler;
		}
		return mobileUtil;
	}

	public MobileUtil(Context context) 
	{
		this.context=context;
		mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @return
	 */
	/*
	public boolean isConnected() 
	{

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {   
            return true;   
        }   
        return false;
	}*/
	/**
	 * 打开网络
	 */
	public void openNet(){
		if(android.os.Build.VERSION.SDK_INT>19)
		{
			android.newland.telephony.TelephonyManager teleManager = new android.newland.telephony.TelephonyManager(context);
			teleManager.setMobileDataEnabled(true);
		}
		else
		{
			try {
				ConnectivityManager mConnectivityManager = getInstanceConnManager(context);

				Class<?> ownerClass = mConnectivityManager.getClass();

				Class<?>[] argsClass = new Class[1];
				argsClass[0] = boolean.class;
				Method method = ownerClass.getMethod("setMobileDataEnabled",argsClass);
				method.invoke(mConnectivityManager, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 关闭网络
	 */
	public void closeNet(){
		if(android.os.Build.VERSION.SDK_INT>19)
		{
			android.newland.telephony.TelephonyManager teleManager = new android.newland.telephony.TelephonyManager(context);
			teleManager.setMobileDataEnabled(false);
		}
		else
		{
			try {
				ConnectivityManager mConnectivityManager = getInstanceConnManager(context);
				Class<?> ownerClass = mConnectivityManager.getClass();
				Class<?>[] argsClass = new Class[1];
				argsClass[0] = boolean.class;
				Method method = ownerClass.getMethod("setMobileDataEnabled",argsClass);
				method.invoke(mConnectivityManager, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 关闭其他网络
	 */
	public void closeOther()
	{
		// 关闭wifi
		if(NetworkUtil.checkNet(context)==ConnectivityManager.TYPE_WIFI)
			WifiUtil.getInstance(context,mHandler).closeNet();
		
		if(NetworkUtil.checkNet(context)==ConnectivityManager.TYPE_ETHERNET)
			EthernetUtil.getInstance(context, mHandler).closeNet();
		// 关闭以太网
	}

    /**
     * 获取移动网络的ip
     * @return
     */
    public  String getIp() 
    { 
        try { 
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
                NetworkInterface intf = en.nextElement(); 
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
                    InetAddress inetAddress = enumIpAddr.nextElement(); 
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) 
                    { 
                        // if (!inetAddress.isLoopbackAddress() && inetAddress 
                        // instanceof Inet6Address) { 
                        return inetAddress.getHostAddress().toString(); 
                    } 
                } 
            } 
        } catch (Exception e) { 
        } 
        return ""; 
    }
	/**
	 * SIM卡是否存在
	 * 
	 * @return true 存在且可用 false 不存在或者存在不可用
	 */
	public boolean isSimReady() {
		int simState = mTelephonyManager.getSimState();
		if (simState == TelephonyManager.SIM_STATE_READY)
			return true;
		return false;
	}

	/**
	 * 获取SIM卡状态的描述
	 * 
	 * @return
	 */
	public int getSimState() {
		int simStateNo = mTelephonyManager.getSimState();
		switch (simStateNo) {
		case 0:
			return NDK_ERR_SIM_UNKnow;
		case 1:
			return NDK_ERR_SIM_NO_USE;
		case 2:
			return NDK_ERR_SIM_UNKnow;
		case 3:
			return NDK_ERR_SIM_LOCK_USE_PIN;
		case 4:
			return NDK_ERR_SIM_LOCK_NET_PIN;
		case 5:
			return NDK_OK;
		default:
			return NDK_ERR_SIM_UNKnow;
		}
	}
	
//	public String getSignalType(){
//		int type = mTelephonyManager.getNetworkType();
//      
//		StringBuffer sb = new StringBuffer();
//		sb.append(mTelephonyManager.getSimOperatorName());
//		
//		 switch (type) {
//         case TelephonyManager.NETWORK_TYPE_GPRS:
//         case TelephonyManager.NETWORK_TYPE_EDGE:
//         case TelephonyManager.NETWORK_TYPE_CDMA:
//         case TelephonyManager.NETWORK_TYPE_1xRTT:
//         case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
//        	 sb.append(" 2G");
//             break;
//         case TelephonyManager.NETWORK_TYPE_UMTS:
//         case TelephonyManager.NETWORK_TYPE_EVDO_0:
//         case TelephonyManager.NETWORK_TYPE_EVDO_A:
//         case TelephonyManager.NETWORK_TYPE_HSDPA:
//         case TelephonyManager.NETWORK_TYPE_HSUPA:
//         case TelephonyManager.NETWORK_TYPE_HSPA:
//         case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
//         case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
//         case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
//             sb.append(" 3G");
//             break;
//         case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
//             sb.append(" 4G");
//             break;
//         default:
//             break;
//      }
//		return sb.toString();
//	}
	public int getSignStrength() 
	{
		return signStrength;
	}
	
//	PhoneStateListener phoneStateListener = new PhoneStateListener()
//	{
//		public void onSignalStrengthsChanged(SignalStrength signalStrength) 
//		{
//			signStrength=signalStrength.getGsmSignalStrength();
//		};
//	};
	
	//反射方式获取当前卡片网络是否打开   chend 20200527
	public boolean getMobileDataState(Context pContext) 
	{
		try {
			ConnectivityManager mConnectivityManager = getInstanceConnManager(context);
			Class<?> ownerClass = mConnectivityManager.getClass();
			Method method = ownerClass.getMethod("getMobileDataEnabled", null);
			boolean isOpen = (boolean) method.invoke(mConnectivityManager);
			LoggerUtil.d("getMobileDataState->" + isOpen);
			return isOpen;

		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.d("eric_chen->获取移动数据状态异常: " + e.toString());
			return false;
		}
	}
	  
	// 反射方式设置当前卡片网络 chend20200527
	public void setMobileData(Context pContext, boolean pBoolean) {
		if (android.os.Build.VERSION.SDK_INT > 19) {
			android.newland.telephony.TelephonyManager teleManager = new android.newland.telephony.TelephonyManager(context);
			teleManager.setMobileDataEnabled(pBoolean);
		} else {
			try {

				ConnectivityManager mConnectivityManager = getInstanceConnManager(context);
				Class<?> ownerClass = mConnectivityManager.getClass();
				Class<?>[] argsClass = new Class[1];
				argsClass[0] = boolean.class;
				Method method = ownerClass.getMethod("setMobileDataEnabled",argsClass);
				method.invoke(mConnectivityManager, pBoolean);
			} catch (Exception e) {
				e.printStackTrace();
				LoggerUtil.d("eric_chen->获取移动数据状态异常:" + e.toString());
			}
		}
	}
	
}
