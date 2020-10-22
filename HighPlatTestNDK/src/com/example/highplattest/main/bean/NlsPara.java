package com.example.highplattest.main.bean;

public class NlsPara 
{
	// 默认为后置，附加码打开
	private int cameraId = 0;
	private String cameraMsg="后置";

	private int upcBtn = 0;
	// 是否开启LED的背光灯，默认关闭，N700才支持
	public boolean isLed = false;
	// 是否开启对焦灯，默认关闭，N700才支持
	public boolean isRed = false;
	
	public int scanSet;/**设置多码模式，20200715，目前A7支持*/
	
	private boolean isConfig = false;
	
	private boolean isPreview = true;
	
	
	
	public int getScanSet() {
		return scanSet;
	}
	public void setScanSet(int scanSet) {
		this.scanSet = scanSet;
	}
	public boolean isConfig() {
		return isConfig;
	}
	public void setConfig(boolean isConfig) {
		this.isConfig = isConfig;
	}
	
	public int getCameraId() {
		return cameraId;
	}
	public void setCameraId(int cameraPara) {
		this.cameraId = cameraPara;
	}
	public int getUpcBtn() {
		return upcBtn;
	}
	public void setUpcBtn(int upcBtn) {
		this.upcBtn = upcBtn;
	}
	public boolean isLed() {
		return isLed;
	}
	public void setLed(boolean isLed) {
		this.isLed = isLed;
	}
	public boolean isRed() {
		return isRed;
	}
	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}
	public boolean isPreview() {
		return isPreview;
	}
	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}
	public String getCameraMsg() {
		return cameraMsg;
	}
	public void setCameraMsg(String cameraMsg) {
		this.cameraMsg = cameraMsg;
	}
	
}