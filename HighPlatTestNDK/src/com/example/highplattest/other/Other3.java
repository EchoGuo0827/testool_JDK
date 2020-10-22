package com.example.highplattest.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.newland.SettingsManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170331
 * directory 		: 
 * description 		: 获取GPS的开关状态
 * related document :
 * history 		 	: author			date			remarks
 *			  	      zhengxq		   20170331	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Other3 extends UnitFragment
{
	private final String TESTITEM = "获取GPS的开关状态";
	public final String TAG = Other3.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private boolean enabled = false;
	
	public void other3()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other3", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		boolean gps_status;
		LocationManager mLocationManager = (LocationManager) myactivity.getSystemService(Context.LOCATION_SERVICE);
		SettingsManager settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// case1:关闭GPS时，获取到的GPS应为关闭状态，enabled:true 代表开启了GPS ，false代表关闭了GPS
		gui.cls_show_msg1(gScreenTime, "获取GPS关闭状态下的返回值");
		gps_status = enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(enabled==true)
		{
			// 跳转到开启GPS导航的设置界面
			gui.cls_show_msg("请先去设置->位置信息中关闭GPS定位功能，完成点【确认】继续");
			// 再次获取GPS位置
			enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(enabled==true)
			{
				gui.cls_show_msg1_record(TAG, "other3", gKeepTimeErr,"line %d:关闭GPS操作失败（ret = %s）", Tools.getLineInfo(),enabled);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case2:开启GPS时，获取到的GPS应为关闭状态
		gui.cls_show_msg1(gScreenTime, "获取GPS打开状态下的返回值");
		gui.cls_show_msg("请先去设置->位置信息中打开GPS定位功能，完成点【确认】继续");
		enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(enabled==false)
		{
			gui.cls_show_msg1_record(TAG, "other3", gKeepTimeErr,"line %d:开启GPS操作失败（ret = %s）", Tools.getLineInfo(),enabled);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3：开启GPS时进入休眠，应可自动关闭GPS，此时获取GPS的状态应为关闭
		gui.cls_show_msg1(gScreenTime, "设备休眠GPS定位会自动关闭");
		settingsManager.setScreenTimeout(ONE_MIN);
		for (int j = 0; j <= 30; j++) 
		{
			gui.cls_show_msg1(2, "1分钟倒计时，请不要点击屏幕，休眠一分钟后请手动唤醒，剩余时间%ds", 60-j*2);
		}
		// 休眠的时候获取GPS开关应被关闭
		enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(enabled==true)
		{
			gui.cls_show_msg1_record(TAG, "other3", gKeepTimeErr,"line %d:设备休眠关闭GPS操作失败（ret = %s）", Tools.getLineInfo(),enabled);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		Log.d("gps case3", enabled+"");
		// case4:休眠唤醒后应可自动开启GPS开关，此时获取GPS的状态应为开启
		GlobalVariable.isWakeUp=false;
		while(!GlobalVariable.isWakeUp);
		gui.cls_show_msg1(1, "设备唤醒后GPS定位会自动开启");
		enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(enabled==false)
		{
			gui.cls_show_msg1_record(TAG, "other3", gKeepTimeErr,"line %d:设备唤醒开启GPS操作失败（ret = %s）", Tools.getLineInfo(),enabled);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 测试结束后恢复GPS默认状态
		if(gps_status!=enabled)
		{
			if(enabled==true)
				gui.cls_show_msg1_record(TAG, "other3", gScreenTime,"%s测试通过，请手动关闭GPS定位开关", TESTITEM);
			else
				gui.cls_show_msg1_record(TAG, "other3", gScreenTime,"%s测试通过，请手动打开GPS定位开关", TESTITEM);
		}
		gui.cls_show_msg1_record(TAG, "other3", gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
	}
	
}
