package com.example.highplattest.main.netutils;

public class WifiProbe {

	private String macAddr;// MAC地址
	private String rssi;	//RSSI信号
	private String sysTimeNow;// 搜索到的系统时间
	private int standTime;// 搜索到的时间
	
	public WifiProbe(String macString,String rssi)
	{
		this.macAddr = macString;
		this.rssi = rssi;
	}
	
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	public String getSysTimeNow() {
		return sysTimeNow;
	}
	public void setSysTimeNow(String sysTimeNow) {
		this.sysTimeNow = sysTimeNow;
	}
	public int getStandTime() {
		return standTime;
	}
	public void setStandTime(int standTime) {
		this.standTime = standTime;
	}
	
	
}
