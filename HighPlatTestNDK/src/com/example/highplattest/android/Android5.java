package com.example.highplattest.android;

import java.io.IOException;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android5.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180410 
 * directory 		: 
 * description 		: 测试Android原生壁纸接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180410 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android5 extends UnitFragment {
	public final String TAG = Android5.class.getSimpleName();
	private String TESTITEM = "壁纸原生接口测试";
	private Gui gui = new Gui(myactivity, handler);
		
	public void android5(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(myactivity);
		//测试前置， 获取当前壁纸
		Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		// 将Drawable,转成Bitmap
		Bitmap beforBm = ((BitmapDrawable) wallpaperDrawable).getBitmap();  
		
		//case1:方法1
		Bitmap bitmap = BitmapFactory.decodeResource(myactivity.getResources(), R.drawable.background);
		try {
			wallpaperManager.setBitmap(bitmap);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (gui.cls_show_msg("查看壁纸是否第一次改变，[确认]是，[其他]否") != ENTER) {
			gui.cls_show_msg1_record(TAG, "android5", gKeepTimeErr, "line %d:%s设置壁纸效果异常", Tools.getLineInfo(), TESTITEM);
		}
		//case2:方法2
		try {
			wallpaperManager.setResource(R.drawable.ic_launcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (gui.cls_show_msg("查看壁纸是否第二次改变，[确认]是，[其他]否") != ENTER) {
			gui.cls_show_msg1_record(TAG, "android5", gKeepTimeErr, "line %d:%s设置壁纸效果异常", Tools.getLineInfo(), TESTITEM);
		}
		
		//case3:调用系统自带的壁纸选择功能,不需要添加权限
        Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        myactivity.startActivity(Intent.createChooser(intent, "选择壁纸"));
        if (gui.cls_show_msg("查看壁纸是否设置3，[确认]是，[其他]否") != ENTER) {
        	gui.cls_show_msg1_record(TAG, "android5", gKeepTimeErr, "line %d:%s设置壁纸效果异常", Tools.getLineInfo(), TESTITEM);
		}
        
		// 测试后置，设回之前的壁纸
		try {
			wallpaperManager.setBitmap(beforBm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1_record(TAG, "android5",gScreenTime,"%s测试通过", TESTITEM);
	}


	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
