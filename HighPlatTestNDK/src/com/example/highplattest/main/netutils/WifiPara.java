package com.example.highplattest.main.netutils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;



import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.WIFI_SEC;

//test--------//
/**
 * wifi参数类
 * @author zhengxq
 * 2016-4-6 下午4:52:39
 */
public class WifiPara extends NetWorkingBase
{
	/**
	 * 动态/静态ip设置:默认是静态
	 */
	
	/**
	 * WIFI热点名称
	 */
	private  String ssid;

	/**
	 * wifi的mac地址
	 */
	private String Bssid;
	
	/**
	 * WIFI密码
	 */
	private  String passwd;
	/**
	 * 认证方式
	 */
	private ParaEnum.WIFI_SEC sec=WIFI_SEC.WPA;
	
	/**
	 * scan_ssid:0 关闭ssid广播false
	 * scan_ssid:1 开启ssid广播true
	 * 默认情况下为1
	 */
	private boolean scan_ssid=true;
	
	/**
	 * 输入方式
	 * false:手动方式
	 * true:扫描方式
	 */
	private boolean input_way = false;
	
	/**
	 * 信道
	 */
	private int channel=6;
	
	
	private int excelsheet=4;
	
	private String other24wifi="192.168.1.104";
	
	
	public WifiPara() 
	{
		this.ssid = "002";
		this.passwd = "believe;";
		setType(LinkType.WLAN);
	}


	public String getSsid() 
	{
		return ssid;
	}
	public int getexcelsheet() 
	{
		return excelsheet;
	}
	
	public String getother24wifi() 
	{
		return other24wifi;
	}
	
	public String getBssid()
	{
		return Bssid;
	}
	
	public void setBssid(String Bssid)
	{
		this.Bssid = Bssid;
	}


	public void setSsid(String ssid) 
	{
		this.ssid = ssid;
	}
	
	public void setexcelsheet(int excelsheet) 
	{
		this.excelsheet = excelsheet;
	}
	
	public void setother24wifi(String other24wifi) 
	{
		this.other24wifi = other24wifi;
	}
	
	
	/**
	 * 判断字符串中是否含有中文
	 * @param str
	 * @return
	 */
	public boolean isChines(String str)
	{
		if(str.getBytes().length==str.length())// 这种情况就是数字和字符串或则特殊编码
			return false;
		else
			return true;// 包含中文字符
	}


	public String getPasswd() 
	{
		return passwd;
	}


	public void setPasswd(String passwd) 
	{
		this.passwd = passwd;
	}


	public ParaEnum.WIFI_SEC getSec() {
		return sec;
	}


	public void setSec(ParaEnum.WIFI_SEC sec) {
		this.sec = sec;
	}


	public boolean isScan_ssid() {
		return scan_ssid;
	}


	public void setScan_ssid(boolean scan_ssid) {
		this.scan_ssid = scan_ssid;
	}


	public boolean isInput_way() {
		return input_way;
	}


	public void setInput_way(boolean input_way) {
		this.input_way = input_way;
	}


	public int getChannel() {
		return channel;
	}


	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	
}
