package com.example.highplattest.main.tools;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

public class SimBroadcastReceiver extends BroadcastReceiver {
  
	private final String ACTION_LOCKED = "android.intent.sim.locked";  //sim卡锁定广播
	private final String ACTION_UNLOCKED = "android.intent.sim.locked.unlock";//sim卡解锁请求广播
	private final String ACTION_SIM_RESULT = "android.intent.sim.locked.result";//sim卡解锁结果广播

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("eric_chen", "---SIM卡解锁pin广播---");
		String action = intent.getAction();
		Log.d("eric_chen", "action = " + action);
		if (action.equals(ACTION_LOCKED)) 
		{
			Intent intent1 = new Intent(ACTION_UNLOCKED);
			intent1.putExtra("pin", "1234");
			context.sendBroadcast(intent1);
		} else if (action.equals(ACTION_SIM_RESULT)) 
		{
			int result = intent.getIntExtra("result", -1);
			Log.d("eric_chen", "result===" + result);
			if (result == 0) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setTitle("sim卡广播");
				dialogBuilder.setMessage("解锁成功！！！");
				AlertDialog alertDialog = dialogBuilder.create();
				alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				alertDialog.show();
			}
		}
	}
}


