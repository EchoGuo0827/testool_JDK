package com.example.highplattest.other;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module           : 系统相关
 * file name        : Sys5.java 
 * Author           : zhangxinj
 * version          : 
 * DATE             : 20170725
 * directory        : 
 * description      : GPS
 * related document :
 * history          : author            date            remarks
 *                    zhangxinj         20170725        created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other33 extends UnitFragment implements LocationListener
{
	private final String TESTITEM = "GPS";
	private LocationManager locationManager;
	private String provider;
	private Gui gui=null;
	private String fileName=Other33.class.getSimpleName();
	
	public void other33()
	{
		gui= new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"other33",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		gui.cls_show_msg1(2, "切换各种位置选项，走出室内，走动不同的地方，经纬度不断变化则测试通过");
	}

	private void showLocation(final Location location) {
		if(location==null){
			Log.v("未搜索到", "location=null");
			gui.cls_show_msg1(1, "未获取到位置信息，可走出室内，走动不同的地方尝试");
		}else
		{
			String currentPosition="纬度"+location.getLatitude()+"\n"+"经度"+location.getLongitude();
			Log.v("位置是:", currentPosition);
			gui.cls_show_msg1(1,currentPosition);
		}
		
	}
	@Override
	public void onTestUp() 
	{
		locationManager = (LocationManager)myactivity.getSystemService(Context.LOCATION_SERVICE);
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			return;
		}
		locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 1000, 1,this);
	}
	
	

	@Override
	public void onTestDown() 
	{
		gui = null;
		locationManager.removeUpdates(this);
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
	} 

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onLocationChanged(Location location) {
		// 更新当前设备的位置信息
		showLocation(location);
	}
}
