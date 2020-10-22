package com.example.highplattest.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/**
* file name 		: Net12.java
* history 		 	: 变更记录											变更时间			变更人员
*			  		  获取双sim卡的IMEI号(N910_A7_V1.0.05导入)	       		20200908   		郑薛晴
* isAuto(Y)
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class Net12 extends UnitFragment{
	private final String TESTITEM = "获取双sim的IMEI号";
	private final String FILE_NAME = Net12.class.getSimpleName();
	private Gui gui;
	
	public void net12()
	{
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg("请确保插入双sim卡测试,完成任意键继续");
		// case1:获取双卡的IMEI号
		 android.telephony.TelephonyManager telephonyManager = (android.telephony.TelephonyManager)  myactivity.getSystemService(Context.TELEPHONY_SERVICE);
         Method method;
		try {
			method = telephonyManager.getClass().getMethod("getImei",int.class);
	         String  IMEI1  =(String) method.invoke(telephonyManager,0);
	         String  IMEI2  =(String) method.invoke(telephonyManager,1);
	         if(IMEI1!=null&&IMEI2!=null)
	         {
	        	 gui.cls_show_msg1_record(FILE_NAME, "net12", gKeepTimeErr, "IMEI1=%s,IME2=%s,与卡片实际的MEID一致视为测试通过",IMEI1,IMEI2);
	         }
	         else
	         {
	        	 gui.cls_show_msg1_record(FILE_NAME, "net12", gKeepTimeErr, "获取IMEI号错误");
	         }
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
