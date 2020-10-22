package com.example.highplattest.main.bean;

import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Create;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Enctyp;

public class WifiApBean {
	// 静态/动态
		public static boolean DHCPenable = false;
		
		// wifi热点接入的网络
		private  ParaEnum.Wifi_Ap_Create wifiApShareDev = Wifi_Ap_Create.WLM;
		// wifi热点安全加密模式
		private  ParaEnum.Wifi_Ap_Enctyp wifiApSecMode = Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA;
		// wifi热点ssid
		private  String wifiApSsid;
		
		// 是否隐藏ssid
		private boolean wifiApHidden;
		// wifi热点密码
		private  String wifiApKey;
		public ParaEnum.Wifi_Ap_Create getWifiApShareDev() 
		{
			return wifiApShareDev;
		}
		public void setWifiApShareDev(ParaEnum.Wifi_Ap_Create wifiApShareDev) 
		{
			this.wifiApShareDev = wifiApShareDev;
		}
		public ParaEnum.Wifi_Ap_Enctyp getWifiApSecMode() 
		{
			return wifiApSecMode;
		}
		public void setWifiApSecMode(ParaEnum.Wifi_Ap_Enctyp wifiApSecMode) 
		{
			this.wifiApSecMode = wifiApSecMode;
		}
		public String getWifiApSsid() 
		{
			return wifiApSsid;
		}
		public void setWifiApSsid(String wifiApSsid) 
		{
			this.wifiApSsid = wifiApSsid;
		}
		public String getWifiApKey() 
		{
			return wifiApKey;
		}
		public void setWifiApKey(String wifiApKey) 
		{
			this.wifiApKey = wifiApKey;
		}
		public boolean isWifiApHidden() 
		{
			return wifiApHidden;
		}
		public void setWifiApHidden(boolean wifiApHidden) 
		{
			this.wifiApHidden = wifiApHidden;
		}
		
}
