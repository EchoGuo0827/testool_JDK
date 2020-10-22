package com.example.highplattest.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.example.highplattest.R;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.control.BluetoothController;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;


public class WlanrebootActivity extends Activity {
	private final String TAG="eric_chen";
	SharedPreferences sp;
	private SharedPreferences.Editor mEditor;
	private Object lockObject = new Object();
	private TextView mTvShow;
	private Gui mGui;	
	private  int  wifistatic=-10086;
	private WifiUtil wifiUtil;
	private WifiManager  wifimanager;
	private int wifireboottem;
	
	
	public Handler eHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mTvShow.setText("获取wifi状态异常");
				break;
			case 2:
				mTvShow.setText("获取wifi状态为打开状态。。1分钟后重启");
				break;
			case 3:
				mTvShow.setText("测试结束----请查看sdcard/result.txt文件");
				break;
			default:
				break;
			}
			
		};
	};
	   @Override
	protected void onCreate( Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifireboot);
		wifiUtil=WifiUtil.getInstance(this,null);
		wifimanager=(WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		mTvShow = (TextView) findViewById(R.id.info);
		mGui = new Gui();
		sp = this.getSharedPreferences("WifiReboot", Context.MODE_PRIVATE);
		mEditor = sp.edit();
		wifireboottem=sp.getInt("wifireboot", -100);
		mTvShow.setText("接收到POS重启广播---启动WlanActivity---正在循环判断wifi状态请耐心等待");
		//开启线程判断wifi状态
		Thread thread = new Thread()
		{
			public void run() 
			{
				wlan_test();
			};
		};
		thread.start();
		
	}
	   
	 public void  wlan_test() {
			Log.d(TAG, "进入wlantest---线程开启--");
			//判断wifi状态
			int time=0;
			long startTime = System.currentTimeMillis();
			while(time<150)
			{
				time = (int) Tools.getStopTime(startTime);
				wifistatic=wifiUtil.checkState();
				Log.d(TAG, "wifistatic==="+wifistatic);
				SystemClock.sleep(1000);
				if (wifistatic==wifimanager.WIFI_STATE_ENABLED) {
					Message message=new Message();
					message.what=2;
					eHandler.sendMessage(message);
					mGui.cls_only_write_msg("WlanrebootActivity", "wifi","当前还有%d次重启,wifi状态值为%d，是打开状态(time=%d)------",wifireboottem,wifistatic,time);
					  break;
				}

			}
			if (time>=150) {
				Message message=new Message();
				message.what=1;
				eHandler.sendMessage(message);
				Log.d(TAG, "状态异常----");
				mGui.cls_only_write_msg("WlanrebootActivity", "wifi","当前还有%d次重启,wifi状态值为%d(time=%d)，超时----------退出重启",wifireboottem,wifistatic,time);
				return;
			}
			//
			if (wifireboottem>0) {
				int sec=1;//1分钟后重启
				mGui.cls_only_write_msg("WlanrebootActivity", "wifi","1分钟后即将重启------------");
				//定时重启
				final Timer timer = new Timer();
				 timer.schedule(new TimerTask(){
				      public void run(){
				          Log.d(TAG, "POS-reboot!!!!");
				          reboot(WlanrebootActivity.this);
				          timer.cancel();
				       }
				   }, sec*60*1000);
			}else {
				Message message=new Message();
				message.what=3;
				eHandler.sendMessage(message);
				Log.d(TAG, "测试结束");
				mGui.cls_only_write_msg("WlanrebootActivity", "wifi","当前还有%d次重启,wifi状态值为%d，测试结束---",wifireboottem,wifistatic);
			}
//			int sec=1;//1分钟后重启
//			mGui.cls_only_write_msg("WlanrebootActivity", "wifi","1分钟后即将重启------------",sec);
//			//定时重启
//			final Timer timer = new Timer();
//			 timer.schedule(new TimerTask(){
//			      public void run(){
//			          Log.d(TAG, "POS-reboot!!!!");
//			          reboot(WlanrebootActivity.this);
//			          timer.cancel();
//			       }
//			   }, sec*60*1000);
			
			
			
		
	}
		//重启方法
		public void reboot(Context context)
		
		{	
			PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
			pm.reboot(null); 

		}
}
