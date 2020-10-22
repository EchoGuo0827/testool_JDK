package com.example.highplattest.main.bean;

public class Delayinfo {
	private int seq;
	private long dif;
	private String receiveTime;
	private String sendTime;
	public Delayinfo(int seq,long dif,String receiveTime,String sendTime){
		this.seq=seq;
		this.dif=dif;
		this.receiveTime=receiveTime;
		this.sendTime=sendTime;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public long getDif() {
		return dif;
	}
	public void setDif(long dif) {
		this.dif = dif;
	}
	public String getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
}
