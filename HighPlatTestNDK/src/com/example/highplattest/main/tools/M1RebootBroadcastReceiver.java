package com.example.highplattest.main.tools;

import com.example.highplattest.activity.M1rebootActivity;
import com.example.highplattest.activity.WlanrebootActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class M1RebootBroadcastReceiver extends BroadcastReceiver{
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
		sp2 = context.getSharedPreferences("RFCard", Context.MODE_PRIVATE);
		mEditor2 = sp2.edit();
		if ((action.equals(ACTION_BOOT_COMPLETED))&&sp2.getBoolean("isRFrboot", false)) 
		{
			Intent toIntent = new Intent(mContext, M1rebootActivity.class);
			toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(toIntent);
		}
		
	}

}
