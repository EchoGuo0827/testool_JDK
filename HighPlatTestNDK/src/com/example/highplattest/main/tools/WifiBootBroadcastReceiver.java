package com.example.highplattest.main.tools;

import com.example.highplattest.activity.Wlanreboot2Activity;
import com.example.highplattest.activity.WlanrebootActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class WifiBootBroadcastReceiver  extends BroadcastReceiver{
	private final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private SharedPreferences sp2;
	private SharedPreferences.Editor mEditor2;
	int wifireboottem=0;
	int wifirebootfirst=0;
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		mContext = context;
		sp2 = context.getSharedPreferences("WifiReboot", Context.MODE_PRIVATE);
		mEditor2 = sp2.edit();
		Log.d("eric_chen", "WifiBootBroadcastReceiver-------------");
		Log.e("---------------------------------", action);
		if ((action.equals(ACTION_BOOT_COMPLETED))&&sp2.getInt("wifireboot", -100)>0) 
		{
			Log.d("eric_chen", "进入WifiBootBroadcastReceiver-------------");
			if (wifirebootfirst==0) {
				wifireboottem=sp2.getInt("wifireboot", -100);
				wifireboottem=wifireboottem-1;
				Log.d("eric_chen", "执行压测减一----------------");
				mEditor2.putInt("wifireboot", wifireboottem);
				mEditor2.commit();
			}
			wifirebootfirst=1;
			Log.d("eric_chen", "wifireboottem==="+wifireboottem);
			Intent toIntent = new Intent(mContext, WlanrebootActivity.class);
			toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(toIntent);
		}
		
		if ((action.equals(ACTION_BOOT_COMPLETED))&&sp2.getInt("wifireboot2", -100)>0) 
		{
			Log.d("eric_chen", "进入WifiBootBroadcastReceiver-------------wifireboot2");
			if (wifirebootfirst==0) {
				wifireboottem=sp2.getInt("wifireboot2", -100);
				wifireboottem=wifireboottem-1;
				Log.d("eric_chen", "执行压测减一----------------");
				mEditor2.putInt("wifireboot2", wifireboottem);
				mEditor2.commit();
			}
			wifirebootfirst=1;
			Log.d("eric_chen", "wifireboottem2==="+wifireboottem);
			Intent toIntent = new Intent(mContext, Wlanreboot2Activity.class);
			toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(toIntent);
		}
		
	}

}
