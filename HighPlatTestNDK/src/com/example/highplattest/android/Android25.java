package com.example.highplattest.android;
import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android25.java 
 * Author 			: chencm
 * version 			: 
 * DATE 			: 20180823 
 * directory 		: 
 * description 		: Android5.1运营商服务
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  chencm	       20180823 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android25 extends UnitFragment{
	public final String TAG = Android25.class.getSimpleName();
	private String TESTITEM = "运营商服务";
	private Gui gui = new Gui(myactivity, handler);

	@SuppressLint("NewApi")
	public void android25()
	{
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		 //获得系统提供的TelphonyManager对象的实例
		TelephonyManager tManager = (TelephonyManager)myactivity.getSystemService(Context.TELEPHONY_SERVICE); 
		boolean isCarrier=tManager.hasCarrierPrivileges();
		try {
				if (isCarrier)
				{			
					//暂时配置不到运营商配置任务
					gui.cls_show_msg1_record(TAG, "android25",gScreenTime,"%s该应用具有访问权限", TESTITEM);
					   
				} 
				else
				{
					gui.cls_show_msg1_record(TAG, "android25",gScreenTime,"%s该应用不具有访问权限", TESTITEM);
				}
		} catch (SecurityException e) {
			 e.printStackTrace();
		}		 
	
		gui.cls_show_msg1_record(TAG, "android25",gScreenTime,"%s测试通过", TESTITEM);
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
