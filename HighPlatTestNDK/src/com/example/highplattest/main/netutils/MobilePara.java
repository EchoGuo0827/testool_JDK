package com.example.highplattest.main.netutils;


public class MobilePara extends NetWorkingBase 
{
	// 当前APN
	private String localApn = "CMNET";
	// PPP名字
	private String pppName = "card";
	// PPP密码
	private String pppPsw = "card";
	// 信号强度
	private int signStrength;
	
	
	public String getLocalApn() {
		return localApn;
	}
	public void setLocalApn(String localApn) {
		this.localApn = localApn;
	}
	public String getPppName() {
		return pppName;
	}
	public void setPppName(String pppName) {
		this.pppName = pppName;
	}
	public String getPppPsw() {
		return pppPsw;
	}
	public void setPppPsw(String pppPsw) {
		this.pppPsw = pppPsw;
	}
	public int getSignStrength() {
		return signStrength;
	}
	public void setSignStrength(int signStrength) {
		this.signStrength = signStrength;
	}
	
	
	
	
}
