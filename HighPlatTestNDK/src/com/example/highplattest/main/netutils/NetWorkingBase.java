package com.example.highplattest.main.netutils;

import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;


public class NetWorkingBase 
{
	// 设置动态、静态ip，false：静态 true：动态
	private  boolean DHCPenable = false;
	
	// 本地IP地址
	private String localIp = "192.168.4.152";
	// 本地端口号
	private int localPort = 8080;
	// 网关
	private String gateWay = "192.168.4.254";
	// 子网掩码
	private String netMask = "255.255.255.0";
	// 主DNS
	private String dns1 = "8.8.8.8"; //8.8.8.8
	// 从DNS
	private String dns2 = "114.114.114.114";//114.114.114.114
	
	// 服务器IP
	private String serverIp = "218.66.48.230";
	// 服务器端口号
	private int serverPort = 3459;
	// 传输类型
	private Sock_t sock_t = Sock_t.SOCK_TCP;
	// 链接类型
	private LinkType type = LinkType.ASYN;
	
	
	public Sock_t getSock_t() 
	{
		return sock_t;
	}
	public boolean isDHCPenable() {
		return DHCPenable;
	}
	public void setDHCPenable(boolean dHCPenable) {
		DHCPenable = dHCPenable;
	}
	public void setSock_t(Sock_t sock_t) 
	{
		this.sock_t = sock_t;
	}
	public String getLocalIp() 
	{
		return localIp;
	}
	public void setLocalIp(String localIp) 
	{
		this.localIp = localIp;
	}
	public int getLocalPort() 
	{
		return localPort;
	}
	public void setLocalPort(int localPort) 
	{
		this.localPort = localPort;
	}
	public String getGateWay() 
	{
		return gateWay;
	}
	public void setGateWay(String gateWay) 
	{
		this.gateWay = gateWay;
	}
	public String getNetMask() {
		return netMask;
	}
	public void setNetMask(String netMask) 
	{
		this.netMask = netMask;
	}
	public String getServerIp() 
	{
		return serverIp;
	}
	public void setServerIp(String serverIp) 
	{
		this.serverIp = serverIp;
	}
	public int getServerPort() 
	{
		return serverPort;
	}
	public void setServerPort(int serverPort) 
	{
		this.serverPort = serverPort;
	}
	public LinkType getType() 
	{
		return type;
	}
	public void setType(LinkType type) 
	{
		this.type = type;
	}
	public String getDns1() {
		return dns1;
	}
	public void setDns1(String dns1) {
		this.dns1 = dns1;
	} 
	
	public String getDns2() {
		return dns2;
	}
	public void setDns2(String dns2) {
		this.dns2 = dns2;
	} 
	
}
