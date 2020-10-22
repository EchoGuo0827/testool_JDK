package com.quectel.modem;

import java.lang.String;

public class ModemHelper {
	static {
		System.loadLibrary("quectel");
	}

	public int setImei(String imei) {
		return native_setImei(imei);
	}

	public String getImei() {
		return native_getImei();
	}

	public String getMacAddress() {
		return native_getMacAddress();
	}

	public int setMacAddress(String macaddress) {
		return native_setMacAddress(macaddress);
	}

	private native String native_getImei();

	private native int native_setImei(String imei);

	private native String native_getMacAddress();

	private native int native_setMacAddress(String macaddress);

	public static void main(String[] args) {
		// ImsiManager imsmanager = new ImsiManager();
		// String str = imsmanager.getCdmaImsi();
		// System.out.println(str);
	}
}
