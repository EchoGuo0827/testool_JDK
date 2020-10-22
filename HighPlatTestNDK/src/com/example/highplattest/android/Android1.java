package com.example.highplattest.android;

import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android1.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180409 
 * directory 		: 
 * description 		: 测试Android原生短信和电话接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180409	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android1 extends UnitFragment 
{
	public final String TAG = Android1.class.getSimpleName();
	private String TESTITEM = "短信、拨号原生接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private SmsStatusReceiver mSmsStatusReceiver;
	private SmsDeliveryStatusReceiver mSmsDeliveryStatusReceiver;
    private String SentPhone="15859144037";
    private String ReceivePhone="17759475146";
    private String SentMessage="sent a message!!!";
    private String ReceiveMessage="receive a message~~~";
    private TelephonyManager tm=null;
	
    public void android1(){
    	
		if (gui.cls_show_msg("测试前请确保已经安装可用的sim卡且在自检-移动模块中可查看到电话号码，[确认]是，[其他]否") != ENTER) 
		{
			gui.cls_show_msg1(2, "请安装sim卡!!!");
			return;
		}
		tm=(TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		if(tm.getLine1Number()==null||tm.getLine1Number().equals("")){
			gui.cls_show_msg1_record(TAG, "android1", gKeepTimeErr, "line %d:插入的sim卡获取不到电话号码，请换张电话卡测试", Tools.getLineInfo());
			return;
		}
		ReceivePhone=tm.getLine1Number();//获取本机手机号;
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		//case1：系统调用的方式发短信，因开发已裁剪短信模块，预期失败
		try {
			Uri uri = Uri.parse("smsto:"+SentPhone);
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra("sms_body",SentMessage);
			myactivity.startActivity(intent);
			gui.cls_show_msg1_record(TAG, "android1", gKeepTimeErr, "line %d:%s测试不通过，预期固件已裁剪掉短信功能，短信发送不应成功", Tools.getLineInfo(), TESTITEM);
		} catch (Exception e) {
			e.getMessage();
			gui.cls_show_msg1(gScreenTime, "系统调用的方式发短信失败，与预期一致");
		}
		
		//case2:SmsManager短信管理器发送短信，因开发已裁剪短信模块，预期失败
		sendSMS(SentPhone,SentMessage);
		if(gui.cls_show_msg("查看手机"+SentPhone+"是否收到短信，[确认]是，[其他]否")==ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android1", gKeepTimeErr, "line %d:%s测试不通过，预期固件已裁剪掉短信功能，发送短信不应成功", Tools.getLineInfo(), TESTITEM);
		}
		
		//case3:给本机sim卡发送短信，因开发已裁剪短信模块，预期失败
		sendSMS(ReceivePhone,ReceiveMessage);
		if(gui.cls_show_msg("查看手机"+ReceivePhone+"是否收到短信，[确认]是，[其他]否")==ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android1", gKeepTimeErr, "line %d:%s测试不通过，预期固件已裁剪掉短信功能，发送短信不应成功", Tools.getLineInfo(), TESTITEM);
		}
		
		//case4：系统调用的方式拨打号码，因开发已裁剪拨号模块，预期失败
		try {
			Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + SentPhone));
			myactivity.startActivity(intentPhone);
		} catch (Exception e) {
			e.getMessage();
			gui.cls_show_msg1(gScreenTime,"系统调用的方式拨号失败，与预期一致");
		}

		// case5：系统调用的方式跳转到拨号界面，因开发已裁剪拨号模块，预期失败
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + SentPhone));  
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			myactivity.startActivity(intent); 
			gui.cls_show_msg1_record(TAG, "android1", gKeepTimeErr, "line %d:%s测试不通过，预期固件已裁剪掉拨打功能，跳转到拨号界面不应成功", Tools.getLineInfo(), TESTITEM);
		} catch (Exception e) {
			e.getMessage();
			gui.cls_show_msg1(gScreenTime, "系统调用的方式拨号失败，与预期一致");
		}
		
		gui.cls_show_msg1_record(TAG, "android1", gScreenTime, "%s测试通过", TESTITEM);

	}
	
	/**
     * 直接调用短信接口发短信
     * 
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        // 获取短信管理器
       SmsManager smsManager = SmsManager.getDefault();
       Intent intent1 = new Intent("SMS_SEND_ACTIOIN");
       PendingIntent sentIntent = PendingIntent.getBroadcast(myactivity, 0, intent1, 0);
       Intent intent2 = new Intent("SMS_DELIVERED_ACTION");
       PendingIntent deliveryIntent = PendingIntent.getBroadcast(myactivity, 0, intent2, 0);
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sentIntent, deliveryIntent);
        }
    }
    
    /**
     * 我方是否发送成功
     * @author wangxy
     *
     */
	public class SmsStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(myactivity, "短信发送成功", Toast.LENGTH_LONG).show();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE://通用的失败
				Toast.makeText(myactivity, "RESULT_ERROR_GENERIC_FAILURE", Toast.LENGTH_LONG).show();
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE://无服务
				Toast.makeText(myactivity, "RESULT_ERROR_NO_SERVICE", Toast.LENGTH_LONG).show();
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU://无PDU
				Toast.makeText(myactivity, "RESULT_ERROR_NULL_PDU", Toast.LENGTH_LONG).show();
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF://无线信号关闭
				Toast.makeText(myactivity, "RESULT_ERROR_RADIO_OFF", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}
	
	/**
	 * 对方是否接收成功
	 * @author wangxy
	 *
	 */
	public class SmsDeliveryStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(myactivity, "对方接收短信已成功", Toast.LENGTH_LONG).show();
				break;
			case Activity.RESULT_CANCELED:
				Toast.makeText(myactivity, "RESULT_CANCELED", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	
	}

	@Override
	public void onPause() {
		super.onPause();
		//解绑注册
		myactivity.unregisterReceiver(mSmsStatusReceiver);
		myactivity.unregisterReceiver(mSmsDeliveryStatusReceiver);

	}

	@Override
	public void onResume() {
		super.onResume();
		//动态注册广播
		mSmsStatusReceiver = new SmsStatusReceiver();
		myactivity.registerReceiver(mSmsStatusReceiver, new IntentFilter("SMS_SEND_ACTIOIN"));
		mSmsDeliveryStatusReceiver = new SmsDeliveryStatusReceiver();
		myactivity.registerReceiver(mSmsDeliveryStatusReceiver, new IntentFilter("SMS_DELIVERED_ACTION"));
		
	}

	

}
