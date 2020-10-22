package com.example.highplattest.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.example.highplattest.R;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class M1rebootActivity  extends Activity implements Lib, OnClickListener{
	private final String TAG="eric_chen";
	SharedPreferences sp;
	private SharedPreferences.Editor mEditor;
	Button  bt1;
	private TextView t1,t2;
	private int count;
	int ret=-100;
	int rfFlag=-1;
	public Object mRfObj = new Object(); 
	
	public Handler eHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				t1.setText("寻卡成功------当前第"+count+"次"+"15s后重启");
				break;
			case 2:
				t2.setText("事件机制注册失败。当前第"+count+"次");
				break;
			case 3:
				t2.setText("寻卡M1卡失败----------当前第"+count+"次");
				break;
			case 4:
				t2.setText("M1Request失败----------当前第"+count+"次");
				break;
			case 5:
				t2.setText("M1Anti失败----------当前第"+count+"次");
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
		setContentView(R.layout.m1reboot);
		t1=(TextView) findViewById(R.id.m1textView1);
		t2=(TextView) findViewById(R.id.m1textView2);
		bt1=(Button) findViewById(R.id.m1button1);
		bt1.setOnClickListener(this);
		sp = this.getSharedPreferences("RFCard", Context.MODE_PRIVATE);
		mEditor = sp.edit();
		count=sp.getInt("RFrboot", -10086);
		count++;
		mEditor.putInt("RFrboot", count);
		mEditor.commit();
		t1.setText("正在寻卡---------当前第"+count+"次");
		
		//开启线程判断wifi状态
		Thread thread = new Thread()
		{
			public void run() 
			{
				M1test();
			};
		};
		thread.start();
		
		
		
	}
	
	public void M1test() {

		int ret = -1;
		byte[] psPiccType = new byte[1];
		byte[] piccDetect = new byte[1];
		//byte[] felicauid=new byte[300];
		byte[] M0psSak=new byte[1];
		
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[20];
		
		byte[] UidBuf1 = new byte[10];
		int[] UidLen = new int[1];

		ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), -1, rflistener);
		
		JniNdk.JNI_Rfid_PiccType((byte) 0xcc);// 设置卡类型
		if((ret = JniNdk.JNI_Rfid_M1Request((byte)1, pnDataLen, psDataBuf))!=0)
		{
			Message msgMessage=new Message();
			msgMessage.what=2;
			eHandler.sendMessage(msgMessage);
			  return;
		}
		if((ret = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf))!=0)
		{
			Message msgMessage=new Message();
			msgMessage.what=5;
			eHandler.sendMessage(msgMessage);
//			gui.cls_show_msg1_record(TAG, "rfid_detect",g_keeptime,"line %d:%s防冲突失败(%d)", TAG, Tools.getLineInfo(),type,ret);
			return ;
		}
		byte[] UidBuf = new byte[pnDataLen[0]];
		byte[] psSakBuf = new byte[1];
		System.arraycopy(psDataBuf, 0, UidBuf, 0, pnDataLen[0]);
		System.arraycopy(psDataBuf, 0, UidBuf1, 0, pnDataLen[0]);
		 UidLen[0] = pnDataLen[0];
		if((ret = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], UidBuf, psSakBuf))!=0)
		{
			Message msgMessage=new Message();
			msgMessage.what=3;
			eHandler.sendMessage(msgMessage);
			return ;
		}
		Message msgMessage=new Message();
		msgMessage.what=1;
		eHandler.sendMessage(msgMessage);
		
		int sec=1;
		final Timer timer = new Timer();
		 timer.schedule(new TimerTask(){
		      public void run(){
		          Log.d(TAG, "POS-reboot!!!!");
		          reboot(M1rebootActivity.this);
		          timer.cancel();
		       }
		 }, sec*15*1000);
		
	}
	
	public NotifyEventListener rflistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.d(TAG+",notifyEvent===监听到射频");
			rfFlag = eventNum;
			synchronized (mRfObj) {
				mRfObj.notify();
			}
			return 0;
		}
	};
	// 重启机器
	public void reboot(Context context)
	
	{	
		PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
		pm.reboot(null); 

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.m1button1:
			reset();
			break;

		default:
			break;
		}
		
	}

	private void reset() {
		// TODO Auto-generated method stub
		Log.d(TAG, "reset---------");
		sp = this.getSharedPreferences("RFCard", Context.MODE_PRIVATE);
		mEditor = sp.edit();
		mEditor.clear();
		
	}
}
