package com.example.highplattest.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

public class Android199 extends UnitFragment{
	public final String TAG = Android36.class.getSimpleName();
	private String TESTITEM = "CONNECTIVITY_ACTION的静态注册和动态注册(A7+)";
	private Gui gui = new Gui(myactivity, handler);
	
	String mTypeName=null;
	
	public Android199()
	{
		LoggerUtil.v("Android199无参数1");
	}
	
	public void android199()
	{
		while(true)
		{
			mTypeName=null;
			int nkey = gui.cls_show_msg("CONNECTIVITY_ACTION广播\n0.静态方式\n1.动态方式\n");
			switch (nkey) {
			case '0':
				staticReg();
				break;
				
			case '1':
				dynamicReg();
				break;

			default:
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	private void staticReg()
	{
		String funcName = "staticReg";
		
		if(gui.cls_show_msg("请确保AndroidManifest.xml中Android199相关标签打开,已打开[确认键],[其他]退出测试")!=ENTER)
		{
			gui.cls_show_msg1(1, "已退出测试");
			return;
		}
		// case1:静态注册CONNECTIVITY_ACTION广播方式
		gui.cls_show_msg1(1, "case1:静态注册CONNECTIVITY_ACTION广播,监听网络的变化");
		gui.cls_show_msg("请确保WIFI或无线从关闭到连网状态,操作完毕后任意键继续");
		if(mTypeName!=null)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "监听到网络状态改变,当前网络名称:%s,静态广播方式测试通过",mTypeName);
			
		}
		else
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:未监听到网络变化,测试失败", Tools.getLineInfo());
		}
	}
	
	private void dynamicReg()
	{
		String funcName = "dynamicReg";
		
		ConnectReceiver connReceiver = new ConnectReceiver();
		// case2:动态注册CONNECTIVITY_ACTION广播方式
		gui.cls_show_msg1(1, "case2:动态注册CONNECTIVITY_ACTION广播,监听网络的变化");
		myactivity.registerReceiver(connReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		gui.cls_show_msg("请确保WIFI或无线从关闭到连网状态,操作完毕后任意键继续");
		if(mTypeName!=null)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "监听到网络状态改变,当前网络名称:%s,动态广播方式测试通过",mTypeName);
			
		}
		else
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:未监听到网络变化,测试失败", Tools.getLineInfo());
		}
		myactivity.unregisterReceiver(connReceiver);
	}
	
    public class ConnectReceiver extends BroadcastReceiver{
    	
    	public ConnectReceiver()
    	{
    		LoggerUtil.v("Android199无参数");
    	}
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                LoggerUtil.d("网络状态已经改变");
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();  
                if(info != null && info.isAvailable()) {
                    mTypeName = info.getTypeName();
                    LoggerUtil.d("当前网络名称：" + mTypeName);
                } else {
                    LoggerUtil.d("没有可用网络");
                }
            }
        }
    }
    
	
	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {

	}
}