package com.example.highplattest.main.tools;

import com.example.highplattest.R;
import com.example.highplattest.activity.PatternActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.MediaCodec.CryptoInfo.Pattern;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.ToggleButton;
@SuppressLint("NewApi") 
public class QuickSettingService extends TileService
{
	private final String TAG = "QuickSettingService";
	private final int STATE_OFF=0;
	private final int STATE_ON = 1;
	private int toggleState = STATE_ON;
	
	/**当用户从Edit栏添加到快速设定中调用*/
	@Override
	public void onTileAdded() {
		super.onTileAdded();
		Log.d(TAG, "onTileAdded");
	}
	
	/**当用户从快速设定栏中移除的时候调用*/
	@Override
	public void onTileRemoved() {
		super.onTileRemoved();
		Log.d(TAG, "onTileRemoved");
		Log.d(TAG, isSecure()+"==="+isLocked());
		if(isSecure()==false&&isLocked()==false)
		{
//			showDialog(new Dialog(getApplicationContext()));
		}
	}
	
	
	/**点击的时候*/
	@Override
	public void onClick() {
		super.onClick();
		Log.d(TAG, "onclick state="+Integer.toString(getQsTile().getState()));
		int state = getQsTile().getState();
		Icon icon;
		if(state == Tile.STATE_INACTIVE)
		{
			icon = Icon.createWithResource(getApplicationContext(), R.drawable.arr_expand);
			// 显示个通知
			getQsTile().setState(Tile.STATE_ACTIVE);//更改非活跃状态
			setNotification();
		}else
		{
			icon = Icon.createWithResource(getApplicationContext(), R.drawable.arr_back);
			getQsTile().setState(Tile.STATE_INACTIVE);//更改为活跃状态
		}
		getQsTile().setIcon(icon);// 设置图标
		getQsTile().updateTile();//更新Tile
	}
	
	/**打开下拉菜单的时候调用，当快速设置按钮没有在编辑栏拖到设置栏中不会调用*/
	@Override
	public void onStartListening() {
		super.onStartListening();
		Log.d(TAG, "onStartListening");
	}
	
	/**关闭下拉菜单的时候调用，当快速设置按钮并没有在编辑栏拖到设置栏中不会调用，
	 * 在onTileRemoved移除之前也会调用移除*/
	@Override
	public void onStopListening() {
		super.onStopListening();
		Log.d(TAG, "onStopListening");
	}
	
	// 悬挂通知，Android5.0后新加入的特性
	public void setNotification()
	{
		NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder builder1 = new Notification.Builder(getApplicationContext());
//		Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jianshu.com/p/82e249713f1b"));
		Intent intent1 = new Intent(Settings.ACTION_SETTINGS);
		PendingIntent pendingIntent1 = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 0);
		builder1.setContentIntent(pendingIntent1);
		builder1.setSmallIcon(R.drawable.pdf);// 设置小图标
		builder1.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pdf));// 设置大图标
		builder1.setAutoCancel(true);
		builder1.setContentTitle("悬挂通知");
		
		Intent XuanIntent = new Intent();
		XuanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		 设置点击跳转
//		ComponentName comName = new ComponentName("com.newland.detectappbak", "MenuItemListActivity");
//		XuanIntent.setComponent(comName);
//		XuanIntent.setClass(this, PatternActivity.class);
		
		PendingIntent xuanPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, XuanIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		builder1.setFullScreenIntent(xuanPendingIntent, true);
		mNotifyManager.notify(2, builder1.build());
	}
	
	
}
