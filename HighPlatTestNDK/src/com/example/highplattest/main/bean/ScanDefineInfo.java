package com.example.highplattest.main.bean;

import java.util.HashMap;

import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.UsbModule;

public 	class ScanDefineInfo 
{
	String cameraInfo;
	int cameraId;
	int cameraCnt=1;
	UsbModule usbModule = UsbModule.HuaJie;
	
	public UsbModule getUsbModule() {
		return usbModule;
	}
	public void setUsbModule(UsbModule usbModule) {
		this.usbModule = usbModule;
	}
	//	boolean isBackPreview =true;
//	boolean isFontPreview = true;
	public HashMap<String, Integer> cameraReal = new HashMap<>();
	
	
	
	public int getCameraCnt() {
		return cameraCnt;
	}
	public void setCameraCnt(int cameraCnt) {
		this.cameraCnt = cameraCnt;
	}
	public String getCameraInfo() {
		return cameraInfo;
	}
	public void setCameraInfo(String cameraInfo) {
		this.cameraInfo = cameraInfo;
	}
	public int getCameraId() {
		return cameraId;
	}
	public void setCameraId(int cameraId) {
		this.cameraId = cameraId;
	}
	/*public boolean isBackPreview() {
		return isBackPreview;
	}
	public void setBackPreview(boolean isBackPreview) {
		this.isBackPreview = isBackPreview;
	}
	public boolean isFontPreview() {
		return isFontPreview;
	}
	public void setFontPreview(boolean isFontPreview) {
		this.isFontPreview = isFontPreview;
	}*/
	
	
	
}
