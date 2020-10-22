package com.example.highplattest.main.bean;

import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.WIFI_SEC;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;

/**
 * 自动化测试的初始化参数
 * @author zhengxq
 *
 */
/**
 * @author Administrator
 *
 */
public class AnuoParaBean {
	// 无线参数
	private String WLMType = "GPRS";
	private String WlmSvrIP = "218.66.48.230";
	private int WlmSvr_PORT = 3459;
	private String SockType = "SOCK_TCP";

	// wifi参数
	private String WlanPwd = "123456789";
	private String WlanSsid = "002";
	private String WlanSec = "WPA";
	private boolean WifiDHCPenable = true;
	private String WifiLocalIP = "192.168.33.254";
	private String WifiLocalMask = "255.255.255.0";
	private String WifiLocalGateWay = "192.168.1.1";
	private String WifiSvrIP = "192.168.33.84";
	private int WifiSvrPort = 3456;
	// 蓝牙
	private String BTAddress = "BT_TEST";
	// diskType
	private String DiskType = "TFDSK";
	// SmartType
	private String SmartType = "CPU_A";
	// ic/sam type
	private String IcSamType = "IC1";
	// rf type
	private String rfType = "CPU_A";
	// nfc type
	private String NfcType = "NFC_B";
	
	//交叉测试次数
	private int CrossTime=20;

	// printer dentisty
	private int PrinterDentisty = 5;

	public String toString() {
		String str = getWLMType()+"\n"+getWlmSvrIP()+"\n"+getWlmSvrPORT()+"\n"+getSockType()+"\n"+getWlanPwd() + "\n" + getWlanSsid() + "\n" + isWifiDHCPenable() + "\n" + getWifiLocalIP() + "\n"
				+ getWifiLocalMask() + "\n" + getWifiLocalGateWay() + "\n" + getWifiSvrIP() + "\n" + getWifiSvrPort()
				+ "\n" + getBTAddress()+"\n"+getDiskType()+"\n"+getSmartType()+"\n"+getIcSamType()+"\n"+getRfType()+"\n"+getNfcType()+"\n"+getPrinterDentisty()+"\n"+getCrossTime();

		return str;
	}

	public WIFI_SEC getWlanSec() 
	{
		if(WlanSec.equals("WPA"))
			return WIFI_SEC.WPA;
		else if(WlanSec.equals("WEP"))
			return WIFI_SEC.WEP;
		else if(WlanSec.equals("NOPASS"))
			return WIFI_SEC.NOPASS;
		return WIFI_SEC.WPA;
	}

	public void setWlanSec(String wlanSec) {
		WlanSec = wlanSec;
	}



	public LinkType getWLMType() {
		if (WLMType.equals("GPRS")) {
			return LinkType.GPRS;
		}
		if (WLMType.equals("WCDMA")) {
			return LinkType.WCDMA;
		}
		if (WLMType.equals("CDMA")) {
			return LinkType.CDMA;
		}
		if (WLMType.equals("TD")) {
			return LinkType.TD;
		}
		if (WLMType.equals("LTE")) {
			return LinkType.LTE;
		}

		return LinkType.GPRS;
	}

	public void setWLMType(String wLMType) {
		WLMType = wLMType;
	}

	public Sock_t getSockType() {
		if (SockType.equals("SOCK_DEFAULT")) {
			return Sock_t.SOCK_DEFAULT;
		}
		if (SockType.equals("SOCK_TCP")) {
			return Sock_t.SOCK_TCP;
		}
		if (SockType.equals("SOCK_UDP")) {
			return Sock_t.SOCK_UDP;
		}
		if (SockType.equals("SOCK_SSL")) {
			return Sock_t.SOCK_SSL;
		}
		return Sock_t.SOCK_DEFAULT;
	}

	public void setSockType(String sockType) {
		SockType = sockType;
	}

	
	public String getDiskType() {
		return DiskType;
	}

	public void setDiskType(String diskType) {
		DiskType = diskType;
	}

	public String getSmartType() {
//		if (SmartType.equals("CPU_A")) {
//			return _SMART_t.CPU_A;
//		}
//		if (SmartType.equals("CPU_B")) {
//			return _SMART_t.CPU_B;
//		}
//		if (SmartType.equals("MIFARE_1")) {
//			return _SMART_t.MIFARE_1;
//		}
//		if (SmartType.equals("ISO15693")) {
//			return _SMART_t.ISO15693;
//		}
//		if (SmartType.equals("SAM1")) {
//			return _SMART_t.SAM1;
//		}
//		if (SmartType.equals("SAM2")) {
//			return _SMART_t.SAM2;
//		}
//		if (SmartType.equals("IC")) {
//			return _SMART_t.IC;
//		}
//
//		return _SMART_t.CPU_A;
		return SmartType;
	}

	public void setSmartType(String smartType) {
		SmartType = smartType;
	}

	// ??getIcSamType
	public String getIcSamType() {
//		if (IcSamType.equals("IC1")) {
//			return EM_ICTYPE.ICTYPE_IC;
//		}
//		if (IcSamType.equals("IC2")) {
//			return EM_ICTYPE.ICTYPE_IC;
//		}
//		if (IcSamType.equals("SAM1")) {
//			return EM_ICTYPE.ICTYPE_SAM1;
//		}
//		if (IcSamType.equals("SAM2")) {
//			return EM_ICTYPE.ICTYPE_SAM2;
//		}
//		return EM_ICTYPE.ICTYPE_IC;
		return IcSamType;
	}

	public void setIcSamType(String icSamType) {
		IcSamType = icSamType;
	}

	public _SMART_t getRfType() {
		if (rfType.equals("CPU_A")) {
			return _SMART_t.CPU_A;
		}
		if (rfType.equals("CPU_B")) {
			return _SMART_t.CPU_B;
		}
		if (rfType.equals("MIFARE_1")) {
			return _SMART_t.MIFARE_1;
		}
		if (rfType.equals("ISO15693")) {
			return _SMART_t.ISO15693;
		}
		if (rfType.equals("SAM1")) {
			return _SMART_t.SAM1;
		}
		if (rfType.equals("SAM2")) {
			return _SMART_t.SAM2;
		}
		if (rfType.equals("IC")) {
			return _SMART_t.IC;
		}
		if (rfType.equals("MIFARE_0")) {
			return _SMART_t.MIFARE_0;
		}
		if (rfType.equals("FELICA")) {
			return _SMART_t.FELICA;
		}
		if (rfType.equals("FELICA2")) {
			return _SMART_t.FELICA;
		}
		return _SMART_t.CPU_A;
	}

	public void setRfType(String rfType) {
		this.rfType = rfType;
	}

	public Nfc_Card getNfcType() {

		if (NfcType.equals("NFC_A")) {
			return Nfc_Card.NFC_A;
		}
		if (NfcType.equals("NFC_B")) {
			return Nfc_Card.NFC_B;
		}
		if (NfcType.equals("NFC_M1")) {
			return Nfc_Card.NFC_M1;
		}
		return Nfc_Card.NFC_B;
	}

	public void setNfcType(String nfcType) {
		NfcType = nfcType;
	}

	public int getPrinterDentisty() {
		return PrinterDentisty;
	}

	public void setPrinterDentisty(int printerDentisty) {
		PrinterDentisty = printerDentisty;
	}

	

	public String getBTAddress() {
		return BTAddress;
	}

	public void setBTAddress(String bTAddress) {
		BTAddress = bTAddress;
	}

	public String getWlmSvrIP() {
		return WlmSvrIP;
	}

	public void setWlmSvrIP(String wlmSvrIP) {
		WlmSvrIP = wlmSvrIP;
	}

	public int getWlmSvrPORT() {
		return WlmSvr_PORT;
	}

	public void setWlmSvrPORT(int wlmSvr_PORT) {
		WlmSvr_PORT = wlmSvr_PORT;
	}

	public String getWlanPwd() {
		return WlanPwd;
	}

	public void setWlanPwd(String wlanPwd) {
		WlanPwd = wlanPwd;
	}

	public String getWlanSsid() {
		return WlanSsid;
	}

	public void setWlanSsid(String wlanSsid) {
		WlanSsid = wlanSsid;
	}

	public boolean isWifiDHCPenable() {
		return WifiDHCPenable;
	}

	public void setWifiDHCPenable(boolean wifiDHCPenable) {
		WifiDHCPenable = wifiDHCPenable;
	}

	public String getWifiLocalIP() {
		return WifiLocalIP;
	}

	public void setWifiLocalIP(String wifiLocalIP) {
		WifiLocalIP = wifiLocalIP;
	}

	public String getWifiLocalMask() {
		return WifiLocalMask;
	}

	public void setWifiLocalMask(String wifiLocalMask) {
		WifiLocalMask = wifiLocalMask;
	}

	public String getWifiLocalGateWay() {
		return WifiLocalGateWay;
	}

	public void setWifiLocalGateWay(String wifiLocalGateWay) {
		WifiLocalGateWay = wifiLocalGateWay;
	}

	public String getWifiSvrIP() {
		return WifiSvrIP;
	}

	public void setWifiSvrIP(String wifiSvrIP) {
		WifiSvrIP = wifiSvrIP;
	}

	public int getWifiSvrPort() {
		return WifiSvrPort;
	}

	public void setWifiSvrPort(int wifiSvrPort) {
		WifiSvrPort = wifiSvrPort;
	}

	public int getCrossTime() {
		return CrossTime;
	}

	public void setCrossTime(int crossTime) {
		CrossTime = crossTime;
	}

}
