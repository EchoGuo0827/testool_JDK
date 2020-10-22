package com.example.highplattest.main.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.RemoteException;

public class AppTool {
	static long mCacheSize;
	private static Object object = new Object();
	
	public static long getPackageSizeInfo(final Activity activity,final String mPackageName) {
		
		try {
			Method getPackageSizeInfo = activity.getPackageManager().getClass().getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class);

			getPackageSizeInfo.invoke(activity.getPackageManager(), mPackageName, new IPackageStatsObserver.Stub() {
				@Override
				public void onGetStatsCompleted(final PackageStats pStats, boolean succeeded) throws RemoteException {

					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCacheSize = pStats.cacheSize;
							synchronized (object) {
								object.notify();
							}
						}
					});
				}
			});
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		synchronized (object) {
			try {
				object.wait(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return mCacheSize;
	}

}
