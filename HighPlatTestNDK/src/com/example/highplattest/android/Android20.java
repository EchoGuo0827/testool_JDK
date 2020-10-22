package com.example.highplattest.android;

import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android20.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180820 
 * directory 		: 
 * description 		: Android原生7.0通知接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180820 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android20 extends UnitFragment {
	public final String TAG = Android20.class.getSimpleName();
	private String TESTITEM = "通知原生接口测试(A8)";
	private Gui gui = new Gui(myactivity, handler);
	public  String REPLY_ACTION = "com.xamarin.directreply.REPLY";
	public String KEY_TEXT_REPLY = "key_text_reply";
	public String REQUEST_CODE = "request_code";
	
	public void android20()
	{
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O)
		{
			try {
				testAndroid20();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
		}
		else
		{
			gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "SDK版本低于26，不支持该案例");
		}
	}
	
	@TargetApi(26)
	public void testAndroid20(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
        NotificationManager notificationManager = (NotificationManager) myactivity.getSystemService(Context.NOTIFICATION_SERVICE);
		
        // case1 最高优先级+消息类型为闹钟
		Intent intent = new Intent(Settings.ACTION_SETTINGS);
		PendingIntent pendingIntent = PendingIntent.getActivity(myactivity, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		Resources resources = myactivity.getResources();
		Notification.Builder notification = new Notification.Builder(myactivity);
		notification.setContentTitle("7.0 title1");
		notification.setContentText("第一则消息已发出   android 7.0 test 点击跳转至设置界面");
		notification.setSmallIcon(R.drawable.ic_launcher);
		notification.setLargeIcon(BitmapFactory.decodeResource(myactivity.getResources(), R.drawable.picture1));
		notification.setCategory(Notification.CATEGORY_ALARM);// 将消息进行分类，当设备处于免打扰模式时，进行过滤，可能只有为某种类型的消息可以进行通知
		notification.setPriority(Notification.PRIORITY_DEFAULT); // 通知的优先级,-2到2
		notification.addPerson(resources.getString(R.drawable.picture2));// API要28，目前为25估计没生效
		notification.setContentIntent(pendingIntent);
		notificationManager.notify(0, notification.build());// id用于区分每条消息
		if (gui.cls_show_msg("第一条通知是否已发出,是则请按确定键继续") == ESC)
			gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "line %d:%s第一条通知发送失败", Tools.getLineInfo(), TESTITEM);

		// case2:优先级最低+消息组别为来电
		Notification notification2 = new Notification.Builder(myactivity)
				.setContentTitle("7.0 title2")
				.setContentText("第二则消息已发出   android 7.0 test 点击跳转至设置界面")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(myactivity.getResources(), R.drawable.picture2))
				.setCategory(Notification.CATEGORY_CALL).setPriority(Notification.PRIORITY_MIN) // 通知的优先级,-2到2
				.addPerson(resources.getString(R.drawable.picture1))
				.setContentIntent(pendingIntent).build();
		notificationManager.notify(11, notification2);
		if (gui.cls_show_msg("第二条通知是否已发出且显示在第一条消息的下面,是则请按确定键继续") == ESC)
			gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "line %d:%s第二条通知发送失败", Tools.getLineInfo(), TESTITEM);
     
		// case3 带回复的消息
		CharSequence title = "请在此处点击输入要恢复的消息";
		Intent quickIntent = new Intent();
		quickIntent.setAction("quick.reply.input");

		Notification notification3 = new Notification.Builder(myactivity)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentText("You can reply on notification.")
				.setContentTitle("收到一条可回复的消息").setAutoCancel(true)
				.addAction(new Notification.Action.Builder(R.drawable.arr_back, title,
						PendingIntent.getBroadcast(myactivity, 1, quickIntent, PendingIntent.FLAG_ONE_SHOT))
								.addRemoteInput(new RemoteInput.Builder("quick_notification_reply")
										.setLabel("Please input here!").build())
								.build())
				.build();
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addCategory(myactivity.getPackageName());
		filter.addAction("quick.reply.input");
		myactivity.registerReceiver(br, filter);
		notificationManager.notify(12, notification3);

		if (gui.cls_show_msg("第三条通知是否已发出,此消息带有回复功能，输入任意内容回复发送后，回到应用应看到弹框提示已回复消息，是则请按确定键继续") == ESC)
			gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "line %d:%s第三条带回复功能的通知发送失败", Tools.getLineInfo(), TESTITEM);
		
		// case4消息捆绑
		String key="消息捆绑";
		//创建消息摘要概要，当子消息只有一条时不显示概要
		Notification summaryNotification = new Notification.Builder(myactivity)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setStyle(new Notification.InboxStyle()
		                .setBigContentTitle("消息摘要")
		                .setSummaryText("高端平台测试脚本"))
		        .setGroup(key)
		        .setGroupSummary(true)
		        .build();
		notificationManager.notify(111, summaryNotification);
		
		Notification notification4 = new Notification.Builder(myactivity)
				.setContentTitle("消息捆绑1")
				.setContentText("android 7.0 test 点击跳转至设置界面")
				.setSmallIcon(R.drawable.ic_launcher)
				.setGroup(key)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(12, notification4);
		gui.cls_show_msg("预期已收到第一条捆绑消息，任意键继续");

		Notification notification5 = new Notification.Builder(myactivity)
				.setContentTitle("消息捆绑2")
				.setContentText("android 7.0 test 点击跳转至设置界面")
				.setSmallIcon(R.drawable.ic_launcher)
				.setGroup(key)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(13, notification5);
		gui.cls_show_msg("预期已收到第二条捆绑消息，且与第一条显示在同一组消息中，任意键继续");

		Notification notification6 = new Notification.Builder(myactivity)
				.setContentTitle("消息捆绑3")
				.setContentText("android 7.0 test 点击跳转至设置界面")
				.setSmallIcon(R.drawable.ic_launcher)
				.setGroup(key)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(14, notification6);
		gui.cls_show_msg("预期已收到第三条捆绑消息，且与第一条和第二条显示在同一组消息中，任意键继续");
		
		Notification notification8 = new Notification.Builder(myactivity)
				.setContentTitle("消息捆绑4")
				.setContentText("android 7.0 test 点击跳转至设置界面")
				.setSmallIcon(R.drawable.ic_launcher)
				.setGroup(key)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(15, notification8);
		gui.cls_show_msg("预期已收到第四条捆绑消息，且与前三条显示在同一组消息中，任意键继续");

		if (gui.cls_show_msg("连续发出4条捆绑的消息,是否下拉状态栏中只占用一栏位置，越后面发送的消息显示在越前面，且展开可查看各条的细节，,是则请按确定键继续") == ESC)
			gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "line %d:%s捆绑消息通知发送失败", Tools.getLineInfo(), TESTITEM);

		gui.cls_show_msg1_record(TAG, "android20",gScreenTime, "%s测试通过", TESTITEM);
	}

	
	
	
	
	BroadcastReceiver br = new BroadcastReceiver() {
	    @SuppressLint("NewApi")
		@Override
	    public void onReceive(Context context, Intent intent) {
	        Bundle results = RemoteInput.getResultsFromIntent(intent);
	        if (results != null) {
	            CharSequence result = results.getCharSequence("quick_notification_reply");
	            
	            if (TextUtils.isEmpty(result)) {
	            	Toast.makeText(myactivity, "消息已回复，回复的消息为空", Toast.LENGTH_LONG).show();
	            } else {
	            	Toast.makeText(myactivity, "消息已回复，回复的消息为="+result, Toast.LENGTH_LONG).show();
	            }

	        }
//	        nm.cancelAll();
	        myactivity.unregisterReceiver(this);
	    }
	};
	@Override
	public void onTestUp() 
	{
		
	}

	@Override
	public void onTestDown() 
	{
		
	}


	

}
