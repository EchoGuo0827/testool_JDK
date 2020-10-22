package com.example.highplattest.other;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: Other26.java 
 * Author 			: zhengjiaw
 * version 			: 
 * DATE 			: 20200921
 * directory 		: 
 * description 		: 反射调用隐藏API时应没有系统的警告弹窗
 * related document : 
 * history 		 	: author			date			remarks
 *			  		       郑佳雯		      20200921 		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other26 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "反射调用隐藏API时应没有系统的警告弹窗";
	public final String TAG = Other26.class.getSimpleName();
	private String fileName="Other26";
	Gui gui = new Gui(myactivity, handler);
	private WifiManager wifiManager = null;
	
	public void other26()
	{
		wifiManager = (WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE);
		getWifiApState();
		gui.cls_show_msg1(0, "获取到的wifi ap状态为%s",getWifiApState());
		gui.cls_show_msg("如果没有出现系统的警告弹窗，则该案例通过。。");
		gui.cls_show_msg1_record(TAG, "Other26",gScreenTime, "%s测试结束", TESTITEM);
	}
	
	private WIFI_AP_STATE getWifiApState()
	{
		int tmp;
		try {
			//反射调用
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			tmp = ((Integer) method.invoke(wifiManager));
			if (tmp > 10) {
				tmp = tmp - 10;
			}
			return WIFI_AP_STATE.class.getEnumConstants()[tmp];
		} catch (Exception e) {
			e.printStackTrace();
			return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
		}
	}
	
	public enum WIFI_AP_STATE 
	{
		WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}
	

}
	
