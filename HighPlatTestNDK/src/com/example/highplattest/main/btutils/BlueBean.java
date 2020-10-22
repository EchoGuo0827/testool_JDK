package com.example.highplattest.main.btutils;

public class BlueBean {
	private boolean iscmdback;
	private boolean isdateback;
	public BlueBean(boolean iscmdback,boolean isdateback){
		this.iscmdback=iscmdback;
		this.isdateback=isdateback;
	}
	public BlueBean(){
		this.iscmdback=false;
		this.isdateback=false;
	}
	public boolean isIscmdback() {
		return iscmdback;
	}
	public void setIscmdback(boolean iscmdback) {
		this.iscmdback = iscmdback;
	}
	public boolean isIsdateback() {
		return isdateback;
	}
	public void setIsdateback(boolean isdateback) {
		this.isdateback = isdateback;
	}

}
