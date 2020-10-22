package com.example.highplattest.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.example.highplattest.R;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

public class Wlanreboot2Activity extends Activity {
	private final String TAG="eric_chen";
	HttpURLConnection connection = null;
    BufferedReader reader = null;
	SharedPreferences sp;
	private SharedPreferences.Editor mEditor;
	private Object lockObject = new Object();
	private TextView mTvShow,mSSIDShow,mSSIP;
	private Gui mGui;	
	private  int  wifistatic=-10086;
	private WifiUtil wifiUtil;
	private WifiManager  wifimanager;
	private int wifireboottem;
	private String sSIDTEM="";
	private String IPTEM="";
	
	private String nowsSID="";
	private String nowIP="";
	float timetem=0;
	
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
			case 4:
				mTvShow.setText("wifi连接异常");
				break;
			case 5:
				mSSIDShow.setText("当前连接的ssid是"+nowsSID+"\t第一次连接的ssid是"+sSIDTEM);
				break;
			case 6:
				mSSIP.setText("当前连接的ip是"+nowIP+"\t第一次连接的ssid是"+IPTEM);
				break;
			case 7:
				mTvShow.setText("当前wifi访问百度异常---------------");
				break;
			case 8:
				mSSIDShow.setText("wifi打开性能: "+timetem+"s");
				break;
			case 9:
				mSSIP.setText("wifi连接性能"+timetem+"s");
				break;
			case 10:
				mTvShow.setText("wifi正常，请耐心等待重启");
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
		setContentView(R.layout.wifireboot2);
		wifiUtil=WifiUtil.getInstance(this,null);
		wifimanager=(WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		mGui = new Gui();
		mTvShow = (TextView) findViewById(R.id.info2);
		mSSIDShow=(TextView) findViewById(R.id.ssid);
		mSSIP=(TextView) findViewById(R.id.ip);
		sp = this.getSharedPreferences("WifiReboot", Context.MODE_PRIVATE);
		mEditor = sp.edit();
		
		wifireboottem=sp.getInt("wifireboot2", -100);
		sSIDTEM=sp.getString("SSID", "null");
		IPTEM=sp.getString("IP", "null");
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
public void wlan_test(){
	Log.d(TAG, "进入wlantest---线程开启--");
	//判断wifi状态
	int time=0;
	long startTime = System.currentTimeMillis();
	while(time<150)
	{
		time = (int) Tools.getStopTime(startTime);
		wifistatic=wifiUtil.checkState();
//		SystemClock.sleep(1000);
		if (wifistatic==wifimanager.WIFI_STATE_ENABLED) {
			Message message=new Message();
			message.what=2;
			eHandler.sendMessage(message);
			timetem = Tools.getStopTime(startTime);
			Message message2=new Message();
			message.what=8;
			eHandler.sendMessage(message2);
			mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前还有%d次重启,wifi状态值为%d，是打开状态(time=%d)------wifi打开性能%4.2fs",wifireboottem,wifistatic,time,timetem);
			  break;
		}

	}
	
	if (time>=150) {
		Message message=new Message();
		message.what=1;
		eHandler.sendMessage(message);
		Log.d(TAG, "状态异常----");
		mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前还有%d次重启,wifi状态值为%d(time=%d)，超时----------退出重启",wifireboottem,wifistatic,time);
		return;
	}
	//判断wifi连接
	
				timetem=0;
//				SystemClock.sleep(25000);
				time=0;
				long startTime2 = System.currentTimeMillis();
				while(time<40)
				{
					time = (int) Tools.getStopTime(startTime2);
//					SystemClock.sleep(1000);
					if (NetworkUtil.checkNet(this)==ConnectivityManager.TYPE_WIFI) {
						timetem = Tools.getStopTime(startTime2);
						Message message=new Message();
						message.what=9;
						eHandler.sendMessage(message);
						mGui.cls_only_write_msg("WlanrebootActivity", "wifi","当前还有%d次重启,当前判断wifi已连接(time=%d)------wifi连接性能%4.2fs",wifireboottem,time,timetem);
						  break;
					}

				}
				if (time>=40) {
					Message message=new Message();
					message.what=4;
					eHandler.sendMessage(message);
					Log.d(TAG, "连接异常----");
					mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前wifi连接异常，超时----------退出重启");
					return;
					
				}
				//进行通讯 访问百度
				try {
					URL url=new URL("https://www.baidu.com");
					 connection = (HttpURLConnection)url.openConnection();
					 connection.setRequestMethod("GET");
					 connection.setConnectTimeout(2000);
					 connection.setReadTimeout(2000);
					 connection.setUseCaches(false);
					 InputStream in=connection.getInputStream();
					 reader=new BufferedReader(new InputStreamReader(in));
					 StringBuffer response =new StringBuffer();
					 String line;
					 while ((line=reader.readLine())!=null) {
						  response.append(line);
						
					}
					 Log.d(TAG, response.toString());
					 
					 if (response.toString().equals("")||response.toString()==null) {
							Message message=new Message();
							message.what=7;
							eHandler.sendMessage(message);
							mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前wifi访问百度异常-----------");
							return;
						
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					if (reader!=null) {
						try {
							reader.close();
						} catch (Exception e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}
						
					}
					if (connection!=null) {
						connection.disconnect();
						
					}
				}
//测试人员需求,不需要对比连接的wifi
				
//				//连接成功获取 IP和SSID 对比
//				WifiUtil wifiUtil2=new WifiUtil(this);
//				nowsSID=wifiUtil2.getSSID();
//				nowIP=wifiUtil2.getIp();
//				Log.d(TAG, nowsSID);
//				Log.d(TAG, nowIP);
//				if (!(nowsSID.equals(sSIDTEM))) {
//					mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前连接SSID与第一次连接不相符。第一次为%s,当前为%s",sSIDTEM,nowsSID);
//					return;
//				}else {
//					Message message=new Message();
//					message.what=5;
//					eHandler.sendMessage(message);
//				}
//				if (!(nowIP.equals(IPTEM))) {
//					mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前连接IP与第一次连接不相符。第一次为%s,当前为%s",IPTEM,nowIP);
//					return;
//				}else {
//					Message message=new Message();
//					message.what=6;
//					eHandler.sendMessage(message);
//				}
				Message message10=new Message();
				message10.what=10;
				eHandler.sendMessage(message10);
				if (wifireboottem>0) {
					int sec=1;//1分钟后重启
					mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","1分钟后即将重启------------");
					//定时重启
					final Timer timer = new Timer();
					 timer.schedule(new TimerTask(){
					      public void run(){
					          Log.d(TAG, "POS-reboot!!!!");
					          reboot(Wlanreboot2Activity.this);
					          timer.cancel();
					       }
					   }, sec*20*1000);
				}else {
					Message message=new Message();
					message.what=3;
					eHandler.sendMessage(message);
					Log.d(TAG, "测试结束");
					mGui.cls_only_write_msg("Wlanreboot2Activity", "wifi","当前还有%d次重启，测试结束---",wifireboottem);
				}
      }
//重启方法
public void reboot(Context context)

{	
	PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
	pm.reboot(null); 

}
}
