package com.example.highplattest.main.bean;

public class CheckBean{
	String item1;
	boolean isChecked;
	public CheckBean(String item1){
		this.item1=item1;
		this.isChecked=false;
	}
	public String getItem1() {
		return item1;
	}
	public void setItem1(String item1) {
		this.item1 = item1;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}