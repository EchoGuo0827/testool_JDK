package com.example.highplattest.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.highplattest.R;
import com.example.highplattest.main.FragmentCollector;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.LoggerUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.util.Log;
public class ServiceActivity extends Service {

	private boolean mReflectFlg = false;
	private final String TAG = "ServiceActivity";
	private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	
	private ServerReceiver serverReceiver; 

	String SWITCH_FRAGMENT_INTENT="com.example.highplattest.activity.serverToIntent";
	String EXCEPTION_INTENT="com.example.highplattest.activity.exceptionIntent";
	private Intent intent = new Intent(SWITCH_FRAGMENT_INTENT); 
	private Intent exceptIntent = new Intent(EXCEPTION_INTENT); 
	private  int runId;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return new MsgBinder();
	}
	 public class MsgBinder extends Binder{  
	        /** 
	         * 获取当前Service的实例 
	         * @return 
	         */  
	        public ServiceActivity getService(){  
	            return ServiceActivity.this;  
	        }  
	    }  
	 

	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	public void noticeShow(){
		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			mStartForeground = ServiceActivity.class.getMethod("startForeground", mStartForegroundSignature);
			mStopForeground = ServiceActivity.class.getMethod("stopForeground",mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}

		try {
			mSetForeground = getClass().getMethod("setForeground",mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
		}
		
		@SuppressWarnings("deprecation")
		Notification.Builder builder = new Notification.Builder(this);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);
		builder.setContentIntent(contentIntent);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker("Foreground Service Start");
		builder.setContentTitle("highplattest");
		//String message=FragmentCollector.real_test_name.get(runId).substring(FragmentCollector.real_test_name.get(runId).lastIndexOf(".") + 1).toLowerCase();
		builder.setContentText("highPlatTest正在运行...");
		Notification notification = builder.build();
		startForegroundCompat(NOTIFICATION_ID, notification);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		runId=0;
		if(serverReceiver==null){
			 //动态注册广播接收器  
			serverReceiver = new ServerReceiver();  
	        IntentFilter intentFilter = new IntentFilter();  
	        //IntentActivity发送过来的广播
	        intentFilter.addAction("com.example.highplattest.activity.intentToServer");  
	        registerReceiver(serverReceiver, intentFilter); 
		}
    	startRun();
		return super.onStartCommand(intent, flags, startId);
	
	}

	public void startRun() {
		//显示在界面
		noticeShow();
		// 应该是示例坏成功的次数
		GlobalVariable.isInterrupt = false;
		// 是否显示返回键的标志
		GlobalVariable.isShowBack = false;
		// 发送广播给IntentActivity告诉它跳转到相应fragment
		intent.putExtra("runId", runId);
		sendBroadcast(intent);

	}
	
	public void runTest(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try 
				{
					String method_name = FragmentCollector.real_test_name.get(runId).substring(FragmentCollector.real_test_name.get(runId).lastIndexOf(".") + 1).toLowerCase();
					LoggerUtil.d("method_name="+method_name);
					final Method test_method = FragmentCollector.listTestClass.get(runId).getMethod(method_name);
					test_method.invoke(FragmentCollector.fragmentList.get(runId), null);
				} 
				catch(NoSuchMethodException e)
				{
					exceptIntent.putExtra("exception", e.getMessage());
					sendBroadcast(exceptIntent);
					e.printStackTrace();
				}
				catch (Exception e) 
				{
					exceptIntent.putExtra("exception", e.getCause().getMessage() );
					sendBroadcast(exceptIntent);
					e.printStackTrace();
				}
				runId++;
				if(runId<FragmentCollector.real_test_name.size())
					startRun();
				else
				{
					if(GlobalVariable.gAutoFlag==ParaEnum.AutoFlag.AutoFull&&FragmentCollector.mModuleNames.size()>0)
					{
						LoggerUtil.e(TAG+",runTest===大小："+FragmentCollector.mModuleNames.size());
						intent.putExtra("autoHandFlag", 1);
						sendBroadcast(intent);
					}
				}
				GlobalVariable.isShowBack = true;

			}
		}).start();
	}
	
	/** 
     * 广播接收器 
     * @author 
     * 
     */  
    public class ServerReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            	//执行具体用例
        	runTest();
			
        }  
          
    }  
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(serverReceiver!=null){
			unregisterReceiver(serverReceiver);
			serverReceiver=null;
		}
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		if (mReflectFlg) {
			// If we have the new startForeground API, then use it.
			if (mStartForeground != null) {
				mStartForegroundArgs[0] = Integer.valueOf(id);
				mStartForegroundArgs[1] = notification;
				invokeMethod(mStartForeground, mStartForegroundArgs);
				return;
			}

			// Fall back on the old API.
			mSetForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
			mNM.notify(id, notification);
		} else {
			/*
			 * 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法startForeground设置前台运行，
			 * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground设置前台运行
			 */

			if (VERSION.SDK_INT >= 5) {
				startForeground(id, notification);
			} else {
				// Fall back on the old API.
				mSetForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
				mNM.notify(id, notification);
			}
		}
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		if (mReflectFlg) {
			// If we have the new stopForeground API, then use it.
			if (mStopForeground != null) {
				mStopForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mStopForeground, mStopForegroundArgs);
				return;
			}

			// Fall back on the old API. Note to cancel BEFORE changing the
			// foreground state, since we could be killed at that point.
			mNM.cancel(id);
			mSetForegroundArgs[0] = Boolean.FALSE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
		} else {
			/*
			 * 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法stopForeground停止前台运行， 否则调用反射取得的sdk
			 * level 5（对应Android 2.0）以下才有的旧方法setForeground停止前台运行
			 */

			if (VERSION.SDK_INT >= 5) {
				stopForeground(true);
			} else {
				// Fall back on the old API. Note to cancel BEFORE changing the
				// foreground state, since we could be killed at that point.
				mNM.cancel(id);
				mSetForegroundArgs[0] = Boolean.FALSE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
			}
		}
	}
	

	
}
