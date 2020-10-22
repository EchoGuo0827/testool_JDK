package com.example.highplattest.activity;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import com.example.highplattest.R;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.control.BluetoothController;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.TextView;

public class BtBackActivity extends Activity
{
	private BluetoothController mNlBluetooth=null;
	SharedPreferences sp;
	private Object lockObject = new Object();
	private TextView mTvShow;
	private Gui mGui;
	private byte[] rbuf=new byte[1024];//全功能底座透传最大4k
	private int len=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bt_back_layout);
		mTvShow = (TextView) findViewById(R.id.tv_back_show);
		mGui = new Gui();
		mTvShow.setText("接收到POS重启广播");
		Thread thread = new Thread()
		{
			public void run() 
			{
				btBack();
			};
		};
		thread.start();
	}
	
	
	public void btBack()
	{
		LoggerUtil.e("进入重启的操作");
		// 重新开个Activity 解决燕清说的无法在广播绑定服务
        final PowerManager pm = (PowerManager) this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        sp = this.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        //获取蓝牙mac地址
        mNlBluetooth = BluetoothController.getInstance();
        mNlBluetooth.init(this, null);//不使用此处OnSearchListener搜索蓝牙、蓝牙状态改变的监听
        
        String btMac = sp.getString("btMac", "");
		mGui.cls_only_write_msg("BtBackActivity", "btBack","POS重启开机后即将回连底座,mac=%s",btMac);
		LoggerUtil.d("等待回连操作");
		synchronized (lockObject) {
			try {
				lockObject.wait(15*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(mNlBluetooth.isConnectedA()==false)
		{
			mGui.cls_only_write_msg("BtBackActivity", "btBack","回连失败");
			return;
		}
		else
			mGui.cls_only_write_msg("BtBackActivity", "btBack","回连成功");
		
		//开个子线程
		Thread t=new Thread(new Runnable(){

			@Override
			public void run() {
				//case1:命令通道通讯 ，获取蓝牙版本
				String curVer=mNlBluetooth.pGetBluetoothVersion();
				String version = sp.getString("btVersion", "");
				if(curVer.equals(version)==false)
				{
					mGui.cls_only_write_msg("BtBackActivity", "btBack","line %d:上下电获取蓝牙版本不一致(curVer=%s,version=%s)", Tools.getLineInfo(),curVer,version);
				}
				// case2:获取蓝牙底座mac地址
				String  curMac = mNlBluetooth.getConnectedDeviceAddressA();
				String btMac = sp.getString("btMac", "");
				if(curMac.equals(btMac)==false)
				{
					mGui.cls_only_write_msg("BtBackActivity", "btBack","line %d:上下电获取蓝牙mac不一致(curMac=%s,btMac=%s)", Tools.getLineInfo(),curMac,btMac);
				}
				// case3:获取蓝牙底座的名字
				String curName=mNlBluetooth.getConnectedDeviceNameA();
				String btName = sp.getString("btName", "");
				if(curName.equals(btName)==false)
				{
					mGui.cls_only_write_msg("BtBackActivity", "btBack","line %d:上下电获取蓝牙名字不一致(curName=%s,btName=%s)", Tools.getLineInfo(),curName,btName);
				}
				// case4:获取蓝牙底座的波特率
				StringBuffer curBps=new StringBuffer();
				mNlBluetooth.btGetTransPortA(curBps);
				String btBps = sp.getString("btBps", "");
				if(curBps.toString().equals(btBps)==false)
				{
					mGui.cls_only_write_msg("BtBackActivity", "btBack","line %d:上下电获取蓝牙底座波特率不一致(curBps=%s,btBps=%s)", Tools.getLineInfo(),curBps.toString(),btBps);
				}
				// 收发100字节无问题，1024字节收发数据通道会断开
			    byte[] sBuf = new byte[1024];
			    byte[] rBuf = new byte[1024];
			    Arrays.fill(rBuf,(byte)0x11);
				for (int j = 0; j < sBuf.length; j++) 
					sBuf[j] = (byte) (Math.random()*256);
				if(mNlBluetooth.sendDataA(sBuf)==false)
				{
					mGui.cls_only_write_msg("BtBackActivity", "btBack", "%s--->line %d:数据通道发送数据失败(ret = %d)", Tools.getLineInfo(),-302);
				}
				int time=0;
				len=0;
				//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
				//mNlBluetooth.setDataListener(listener);
				long startTime = System.currentTimeMillis();
				while(time<15000)
				{
					time = (int) Tools.getStopTime(startTime);
					SystemClock.sleep(1000);
					if(len==1024)
						{
						break;
						}
				}
				if(len!=1024)
				{
					LoggerUtil.e("sbuf="+ISOUtils.hexString(sBuf));
					LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
					mGui.cls_only_write_msg("BtBackActivity", "btBack","line %d:数据通道接收数据失败(实际接收到的长度=%d)",Tools.getLineInfo(),len);
				}else{
					// 比较收发数据
					if(Tools.memcmp(sBuf, rbuf, 1024)==false)
					{
						LoggerUtil.e("sbuf="+ISOUtils.hexString(sBuf));
						LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
						mGui.cls_only_write_msg("BtBackActivity", "btBack", "line %d:数据通道比较数据失败",Tools.getLineInfo());
						
					}
				}
				mGui.cls_only_write_msg("BtBackActivity", "btBack","双通道数据收发成功");
			}
		});
		t.start();

		int sec=(int)(30+Math.random()*(60-30+1));//30-60，间隔一段时间定时重启
		mGui.cls_only_write_msg("BtBackActivity", "btBack","%dmin后重启POS进行下一次回连",sec);
		//定时重启
		final Timer timer = new Timer();
		 timer.schedule(new TimerTask(){
		      public void run(){
		          System.out.println("POS-reboot!!!!");
		  		  pm.reboot(null); 
		          timer.cancel();
		       }
		   }, sec*60*1000);
	}
	//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
	/*OnDataReceiveListener listener = new OnDataReceiveListener() {
        @Override
        public void onDataReceive(byte[] data) {
        	LoggerUtil.e("接收数据广播:"+data.length);
        	len=len+data.length;
    		System.arraycopy(data, 0,rbuf, i, data.length);  
    		i=i+data.length;
        }
	};*/

}
