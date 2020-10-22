package com.example.highplattest.main.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;

public class ChangeWireType {
	private TelephonyManager telephonyManager = null;
	
	public ChangeWireType(Context context){
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public boolean changeWire(int type){
		if (android.os.Build.VERSION.SDK_INT>22){
			int mSubId = getmSubId();
			Method method;
	        String status = null;
	        try {
	            method = telephonyManager.getClass().getMethod("setPreferredNetworkType",int.class,int.class);
	            method.setAccessible(true);
	            status = String.valueOf(method.invoke(telephonyManager, mSubId,type));
	            LoggerUtil.e("setPreferredNetworkType: "+status);
	            return true;
	        } catch (NoSuchMethodException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("NoSuchMethodException: "+e1.toString());
	            return false;
	        } catch (SecurityException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("SecurityException: "+e1.toString());
	            return false;
	        } catch (IllegalArgumentException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalArgumentException: "+e1.toString());
	            return false;
	        } catch (IllegalAccessException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalAccessException: "+e1.toString());
	            return false;
	        } catch (InvocationTargetException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalAccessException: "+e1.toString());
	            return false;
	        }
        }else {
        	 Method method;
             String status = null;
             try {
                 method = telephonyManager.getClass().getMethod("setPreferredNetworkType",int.class);
                 method.setAccessible(true);
                 status = String.valueOf(method.invoke(telephonyManager,type));
                 LoggerUtil.e("setPreferredNetworkType: "+status);
                 return true;
             } catch (NoSuchMethodException e1) {
                 e1.printStackTrace();
                 LoggerUtil.e("NoSuchMethodException: "+e1.toString());
                 return false;
             } catch (SecurityException e1) {
                 e1.printStackTrace();
                 LoggerUtil.e("SecurityException: "+e1.toString());
                 return false;
             } catch (IllegalArgumentException e1) {
                 e1.printStackTrace();
                 LoggerUtil.e("IllegalArgumentException: "+e1.toString());
                 return false;
             } catch (IllegalAccessException e1) {
                 e1.printStackTrace();
                 LoggerUtil.e("IllegalAccessException: "+e1.toString());
                 return false;
             } catch (InvocationTargetException e1) {
                 e1.printStackTrace();
                 LoggerUtil.e("IllegalAccessException: "+e1.toString());
                 return false;
             }
        }
	}
	
	public int getWire(){
		int netType = 0;
		if (android.os.Build.VERSION.SDK_INT>22)
		{
			try {
				int mSubId = getmSubId();
				Method method = telephonyManager.getClass().getMethod("getPreferredNetworkType",int.class);
	            method.setAccessible(true);
	            netType = (int) method.invoke(telephonyManager, mSubId);
	            LoggerUtil.e("getPreferredNetworkType: "+netType);
	        } catch (NoSuchMethodException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("NoSuchMethodException: "+e1.toString());
	            netType = -1;
	        } catch (SecurityException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("SecurityException: "+e1.toString());
	            netType = -1;
	        } catch (IllegalArgumentException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalArgumentException: "+e1.toString());
	            netType = -1;
	        } catch (IllegalAccessException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalAccessException: "+e1.toString());
	            netType = -1;
	        } catch (InvocationTargetException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalAccessException: "+e1.toString());
	            netType = -1;
	        }
		}
		else
		{
			try {
	            Method method = telephonyManager.getClass().getMethod("getPreferredNetworkType",int.class);
	            method.setAccessible(true);
	            netType = (int) method.invoke(telephonyManager);
	        } catch (NoSuchMethodException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("NoSuchMethodException: "+e1.toString());
	            netType = -1;
	        } catch (SecurityException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("SecurityException: "+e1.toString());
	            netType = -1;
	        } catch (IllegalArgumentException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalArgumentException: "+e1.toString());
	            netType = -1;
	        } catch (IllegalAccessException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalAccessException: "+e1.toString());
	            netType = -1;
	        } catch (InvocationTargetException e1) {
	            e1.printStackTrace();
	            LoggerUtil.e("IllegalAccessException: "+e1.toString());
	            netType = -1;
	        }
		}
		return netType;
		
	}
	
	private int getmSubId(){
		int mSubId = 0;
        try {
            Field field1 = telephonyManager.getClass().getDeclaredField("mSubId");
            field1.setAccessible(true);
            mSubId = (int) field1.get(telephonyManager);
            LoggerUtil.e("mSubId："+mSubId);
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        }catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return mSubId;
	}
}
