package com.example.highplattest.receiver;

import com.example.highplattest.main.tools.LoggerUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            LoggerUtil.d("网络状态已经改变");
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();  
            if(info != null && info.isAvailable()) {
                String mTypeName = info.getTypeName();
                LoggerUtil.d("当前网络名称：" + mTypeName);
            } else {
                LoggerUtil.d("没有可用网络");
            }
        }
    }

}
