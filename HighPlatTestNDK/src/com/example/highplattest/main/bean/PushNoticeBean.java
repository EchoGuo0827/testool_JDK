package com.example.highplattest.main.bean;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class PushNoticeBean {
	private String serviceIp="218.66.48.230";
	private int servicePort=3460;
	private String content="你站在桥上看风景,看风景的人在楼上看你。明月装饰了你的窗子,你装饰了别人的梦。明月装饰了你的窗子,你装饰了别人的梦。";
	private int totalTime=100;
	private int interval=4;
	private boolean sleep=true;//是否休眠 默认休眠
	private boolean randOrRegular=true;//true是随机，false是固定
	//存放开始时间 结束时间 延时差等信息
	private List<Delayinfo> delayInfoList;
	//记录当前最后一条消息的编号
	private int lastSeq;
	//当前推送消息的编号
	private int currentSeq;
	//推送内容长度
	private int contentLength=0;
	//网络信息
	private String netInfo;
	public PushNoticeBean(){
		delayInfoList=new ArrayList<Delayinfo>();
		lastSeq=0;
	}
	public int getLastSeq() {
		return lastSeq;
	}
	public void setLastSeq(int lastSeq) {
		this.lastSeq = lastSeq;
	}
	
	public int getCurrentSeq() {
		return currentSeq;
	}
	public void setCurrentSeq(int currentSeq) {
		this.currentSeq = currentSeq;
	}
	
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
	public String getNetInfo() {
		return netInfo;
	}
	public void setNetInfo(String netInfo) {
		this.netInfo = netInfo;
	}
	public String getServiceIp() {
		return serviceIp;
	}
	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}

	public int getServicePort() {
		return servicePort;
	}
	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public boolean isSleep() {
		return sleep;
	}
	public void setSleep(boolean sleep) {
		this.sleep = sleep;
	}
	public boolean isRandOrRegular() {
		return randOrRegular;
	}
	public void setRandOrRegular(boolean randOrRegular) {
		this.randOrRegular = randOrRegular;
	}
	public void addItem(Delayinfo delayInfo){
		delayInfoList.add(delayInfo);
//		lastSeq=delayInfo.getSeq();
		currentSeq=delayInfo.getSeq();
		Log.v("加入到NoticeList", "当前编号："+currentSeq);
//		file.writeResult("第" + currentSeq + "次 dif=" + delayInfo.getDif() + " pos接收时间：" + delayInfo.getReceiveTime()+ " 服务器发送时间：" + delayInfo.getSendTime().substring(0,19) + "\n");
	}
	public List<Delayinfo> getDelayInfoList() {
		return delayInfoList;
	}
	
}
