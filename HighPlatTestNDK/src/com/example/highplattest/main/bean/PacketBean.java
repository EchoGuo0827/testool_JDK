package com.example.highplattest.main.bean;

public class PacketBean 
{
	// 初始长度
	byte[] header;
	// 包长
	int orig_len = 4*1024;
	int len = 4*1024;
	// 设置要刷几张卡
	int card_number = 1;

	// 压力次数
	int lifecycle = 20;
	
	int show_time = 2;
	
	boolean forever;
	// 是否递增
	boolean IsLenRec=false;
	// 是否随机
	boolean IsDataRnd=true;
	// 固定数据
	byte dataFix;
	
	public PacketBean()
	{
	}
	
	
	
	public byte getDataFix() {
		return dataFix;
	}

	public void setDataFix(byte dataFix) {
		this.dataFix = dataFix;
	}

	public byte[] getHeader() {
		return header;
	}
	public void setHeader(byte[] header) {
		this.header = header;
	}
	public int getOrig_len() {
		return orig_len;
	}
	public void setOrig_len(int orig_len) {
		this.orig_len = orig_len;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public int getLifecycle() {
		return lifecycle;
	}
	public void setLifecycle(int lifecycle) {
		this.lifecycle = lifecycle;
	}
	public boolean isForever() {
		return forever;
	}
	public void setForever(boolean forever) {
		this.forever = forever;
	}
	public boolean isIsLenRec() {
		return IsLenRec;
	}
	public void setIsLenRec(boolean isLenRec) {
		IsLenRec = isLenRec;
	}
	public boolean isIsDataRnd() {
		return IsDataRnd;
	}
	public void setIsDataRnd(boolean isDataRnd) {
		IsDataRnd = isDataRnd;
	}

	public int getShow_time() {
		return show_time;
	}

	public void setShow_time(int show_time) {
		this.show_time = show_time;
	}
	
	public int getCard_number() {
		return card_number;
	}

	public void setCard_number(int card_number) {
		this.card_number = card_number;
	}
	
	
}
