package com.example.highplattest.systest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.newland.SettingsManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.highplattest.R;
import com.example.highplattest.activity.CrossActivity;
import com.example.highplattest.activity.TestActivity;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest15.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150225
 * directory 		: 
 * description 		: LCD屏幕测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq     	  20150225	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest15 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "LCD屏幕测试";
	@SuppressLint("SdCardPath")
	private String PNGPATH = GlobalVariable.sdPath+"share/";
	private String TAG = SysTest15.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private boolean flag_goast = false;
	private SettingsManager settingsManager;
	
	/*------------global variables definition-----------------------*/
	private int RunNum = 1;
	private int RunTime =5;
	private int unWidth,unHeight;
	TextView mTvShow;
	
	public void systest15() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		unHeight = GlobalVariable.ScreenHeight;
		unWidth = GlobalVariable.ScreenWidth;
		while(true)
		{
			int returnValue=gui.cls_show_msg("LCD综合测试\n0.像素点测试\n1.灰度测试\n2.对比度测试\n3.残影测试\n4.彩条测试\n5.几何测试\n6.等待时间设置");
			switch (returnValue) 
			{
			case '0':
				Screen_Pixel();
				break;
				
			case '1':
				Screen_Gray();
				break;
				
			case '2':
				screen_contrast();
				break;
				
			case '3':
				Screen_Ghosting();
				break;
				
			case '4':
				ColorBar();
				break;
				
			case '5':
				Geometry();
				break;
				
			case '6':
				Scr_Config();
				break;
				
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	//配置显示每张图片的时间,0为死等
	private void Scr_Config() 
	{
		final PacketBean packet = new PacketBean();
		
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().show_times_press(myactivity, packet);
			}
		});
		synchronized (packet) {
			try {
				packet.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		RunTime = packet.getShow_time();
		RunNum = packet.getLifecycle();
	}
	
	// 坏点测试
	private void Dead_Pixel() 
	{
		gui.cls_show_msg1(2, "坏点/暗点测试,左右滑动屏幕可调节屏幕亮度");
		String[] szPng = {"red.png","blue.png","cyan.png","yellow.png","pink.png","green.png","black.png","grey.png","white.png"};
		String szBuf;
		int i = 0,RealRunNum = 0;
		RealRunNum = RunNum;
//		if (GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N850) {
//			 PNGPATH = GlobalVariable.sdPath+"share/纯色/";
//			
//		}
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i == szPng.length)
				{
					i = 0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				Log.e("szBuf", szBuf);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				System.gc();
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		// 出现坏点
		if(gui.cls_show_msg("在上面纯色图片切换过程中是否发现坏点或暗点,是[确认],否[取消]")==ENTER)
		{
			gui.cls_show_msg1_record(TAG, "Dead_Pixel", g_keeptime, "%s,line %d测试失败,发现屏幕坏点或坏点", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Dead_Pixel", g_time_0, "测试通过,未发现坏点和暗点");
	}
	
	// 亮点测试
	private void Light_Pixel() 
	{
		gui.cls_show_msg1(2, "亮点测试,左右滑动屏幕可调节屏幕亮度");
		String[] szPng = {"red.png","blue.png","cyan.png","yellow.png","pink.png","green.png","black.png","grey.png","white.png"};
		String szBuf;
		int i = 0,RealRunNum = 0;
		RealRunNum = RunNum;
//		if (GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N850) {
//			 PNGPATH = GlobalVariable.sdPath+"share/纯色/";
//			
//		}
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i == szPng.length)
				{
					i = 0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				System.gc();
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		// 出现坏点
		if(gui.cls_show_msg("在上面纯色图片切换过程中是否发现其比较亮的点,是[确认],否[取消]")==ENTER)
		{
			gui.cls_show_msg1_record(TAG, "Light_Pixel", g_keeptime,"%s,line %d测试失败,发现屏幕亮点", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Light_Pixel", g_time_0, "测试通过,未发现亮点");
	}
	
	//屏幕像素点测试:测试坏点,暗点,亮点
	private void Screen_Pixel() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("屏幕像素点测试\n0.坏点_暗点测试\n1.亮点测试");
			switch (returnValue) 
			{
			case '0':
				Dead_Pixel();
				break;
				
			case '1':
				Light_Pixel();
				break;

			case ESC:
				return;
			}
		}
	}
	
	//灰度渐变测试:从0递增到100再递减到0为一轮测试
	private void Gray_Gradient_Test()
	{
		gui.cls_show_msg1(2, "灰度渐变测试,左右滑动屏幕可调节屏幕亮度");
		
		String[] szPng={"huidu_0.png","huidu_25.png","huidu_50.png","huidu_75.png","huidu_100.png","huidu_75.png","huidu_50.png","huidu_25.png"};
		String szBuf;
		int i=0,RealRunNum = 0;
		RealRunNum = RunNum;
//		if (GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N850) {
//			 PNGPATH = GlobalVariable.sdPath+"share/";
//			
//		}
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i==szPng.length)
				{
					i=0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		if(gui.cls_show_msg("灰度渐变是否正常,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "Gray_Gradient_Test",g_keeptime,  "%s,line %d测试失败,灰度渐变不正常", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Gray_Gradient_Test",g_time_0, "灰度渐变测试通过");
		
	}
	
	//灰度等级测试
	private void Grey_Scale_Test() 
	{
		gui.cls_show_msg1(2, "灰度等级测试,左右滑动屏幕可调节屏幕亮度");
		String[] szPng={"gray_01.png","gray_02.png","gray_03.png","gray_04.png","gray_05.png","gray_06.png",
				"gray_07.png","gray_08.png","gray_09.png","gray_10.png","gray_11.png","gray_12.png",
				"gray_13.png","gray_14.png","gray_15.png","gray_16.png","gray_17.png","gray_18.png",
				"gray_19.png"};
		String szBuf;
		int i=0,RealRunNum = 0;
		RealRunNum = RunNum;
//		if (GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N850) {
//			 PNGPATH = GlobalVariable.sdPath+"share/";
//			
//		}
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i==szPng.length)
				{
					i=0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		if(gui.cls_show_msg("灰度等级图片显示是否正常,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "Grey_Scale_Test",g_keeptime, "%s,line %d测试失败,灰度等级图片显示不正常", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Grey_Scale_Test",g_time_0, "灰度等级图片测试通过");
	}
	
	//屏幕灰度测试
	private void Screen_Gray() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("屏幕灰度测试\n0.灰度渐变测试\n1.灰度等级测试");
			switch (returnValue) 
			{
			case '0':
				Gray_Gradient_Test();
				break;
				
			case '1':
				Grey_Scale_Test();
				break;
				
			case ESC:
				return;
			}
		}
	}
	
	// 对比度测试
	private void screen_contrast()
	{
		gui.cls_show_msg1(2, "对比度测试,左右滑动屏幕可调节屏幕亮度");
		
		String[] szPng={"contrast_1.png","contrast_2.png","contrast_3.png","contrast_4.png","contrast_5.png","contrast_6.png","contrast_7.png","contrast_8.png"};
		String szBuf;
		int i=0,RealRunNum = 0;
		RealRunNum = RunNum;
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i==szPng.length)
				{
					i=0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		if(gui.cls_show_msg("对比度图片显示是否正常,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "screen_contrast",g_keeptime,  "%s,line %d测试失败,对比度不正常", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "screen_contrast",g_time_0, "对比度测试通过");
	}
	
	// 彩条测试
	private void ColorBar()
	{
		gui.cls_show_msg1(2, "彩条测试,左右滑动屏幕可调节屏幕亮度");
		
		String[] szPng={"colorbar_1.png","colorbar_2.png","colorbar_3.png","colorbar_4.png","colorbar_5.png","colorbar_6.png","colorbar_7.png"};
		String szBuf;
		int i=0,RealRunNum = 0;
		RealRunNum = RunNum;
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i==szPng.length)
				{
					i=0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		if(gui.cls_show_msg("彩条测试图片显示是否正常,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "ColorBar",g_keeptime,  "%s,line %d彩条测试失败", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "ColorBar",g_time_0, "彩条测试通过");
	}
	
	// 几何测试
	private void Geometry()
	{
		gui.cls_show_msg1(2, "几何测试,左右滑动屏幕可调节屏幕亮度");
		
		String[] szPng={"geometry_01.png","geometry_02.png","geometry_03.png",
				"geometry_04.png","geometry_05.png","geometry_06.png","geometry_07.png",
				"geometry_08.png","geometry_09.png"};
		String szBuf;
		int i=0,RealRunNum = 0;
		RealRunNum = RunNum;
//		if (GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N850) {
//			 PNGPATH = GlobalVariable.sdPath+"share/几何测试/";
//			
//		}
		while(RealRunNum-->0)
		{
			while(true)
			{
				if(i==szPng.length)
				{
					i=0;
					break;
				}
				szBuf = String.format("%s%s", PNGPATH,szPng[i]);
				imageBack.setVisibility(View.VISIBLE);
				final Bitmap bm = BitmapFactory.decodeFile(szBuf);
				handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
				SystemClock.sleep(RunTime*1000);
				i++;
			}
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		if(gui.cls_show_msg("几何测试图片显示是否正常,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "Geometry",g_keeptime,  "%s,line %d几何测试失败", TESTITEM,Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Geometry",g_time_0, "几何测试通过");
	}
	
	//串扰测试:测试过程中按确认键使屏幕暂停后观察屏幕黑白边界处是否存在干扰
	private void CrossTalk()
	{
		gui.cls_show_msg1(2, "正在进行crossTalk测试");
		int start_x1_point = 0;
		int start_y1_point = 0;
		int start_x_len = 0;
		int start_y_len = 0;
		Log.e("width+height", unWidth+"  "+unHeight);
		if(unHeight>=unWidth)
		{
			start_x1_point = (unWidth/2)-1;
			start_y1_point = start_x1_point;
			start_x_len = 2;
			start_y_len = unHeight-(unWidth-2);
		}
		else
		{
			start_x1_point = (unHeight/2)-1;
			start_y1_point = start_x1_point;
			start_x_len = unWidth-(unHeight-2);
			start_y_len = 2;
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		gui.cls_show_msg("点击返回键回到该测试用例,任意键继续");
		Intent intent = new Intent(myactivity, CrossActivity.class);
		intent.putExtra("value", new int[]{start_x1_point,start_y1_point,start_x_len,start_y_len});
		startActivity(intent);
		 if(gui.cls_show_msg("crossTalk测试是否存在串扰,是[确认],否[取消]")==ENTER)
		 {
			gui.cls_show_msg1_record(TAG, "CrossTalk",g_keeptime, "%s,line %d测试失败",TESTITEM, Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "CrossTalk",g_time_0,  "CrossTalk测试通过");
	}
	
	//测试图案为“棋盘格”,将该画面显示12小时以上,再切换成全屏50%灰度的画面,看是否有残留“棋盘格”。
	private void Ghosting_Test1() 
	{
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		gui.cls_show_msg1(2, "正在进行残影测试");
		int i = 0;
		String[] szPng = {"qipan.png","huidu_50.png"};
		String szBuf;
		gui.cls_show_msg("本测试请关闭休眠开关为避免误操作,棋盘到灰度图片长按[取消]完成跳转,完成任意键继续");
		while(true)
		{
			if(i== szPng.length)
				break;
			szBuf = String.format("%s%s", PNGPATH,szPng[i]);
			Log.e("szBuf", szBuf);
			imageBack.setVisibility(View.VISIBLE);
			final Bitmap bm = BitmapFactory.decodeFile(szBuf);
			handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK,bm));
			// 标志位控制
			while(!flag_goast){
				SystemClock.sleep(100);
			}			i++;
		}
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		 if(gui.cls_show_msg("Ghosting_Test1测试是否存在残影,是[确认],否[取消]")==ENTER)
		 {
			gui.cls_show_msg1_record(TAG, "CrossTalk",g_keeptime, "%s,line %d测试失败",TESTITEM, Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Ghosting_Test1",g_time_0,  "Ghosting_Test1测试通过");
	}
	
	//拖影测试:白色小方块移动过程中屏幕上不应该出现白色拖尾
	private void Ghosting_Test2() 
	{
		// 设置为背景图片
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyback));
		gui.cls_show_msg("点击返回键回到该测试用例,任意键继续");
		Intent intent = new Intent(myactivity, TestActivity.class);
		startActivity(intent);
		if(gui.cls_show_msg("白色小方块移动过程中屏幕是否出现拖影,是[确认],否[取消]")==ENTER)
		{
			gui.cls_show_msg1_record(TAG, "Ghosting_Test2",g_keeptime, "%s,line %d测试失败",TESTITEM, Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "Ghosting_Test2",g_time_0, "Ghosting_Test2测试通过");
	}
	
	// 残影测试
	public void Screen_Ghosting() 
	{
		while(true)
		{
			flag_goast = false;
			int returnValue=gui.cls_show_msg("残影测试\n0.Crosstalk测试\n1.残影测试\n2.拖影测试");
			switch (returnValue) 
			{
			case '0':
				CrossTalk();
				break;
				
			case '1':
				Ghosting_Test1();
				break;
				
			case '2':
				Ghosting_Test2();
				break;
				
			case ESC:
				return;
			}
		}
	}
	
	@Override
	public boolean onLongClick(View view) 
	{
		super.onLongClick(view);
		switch (view.getId()) 
		{
		case R.id.btn_key_esc:
			flag_goast = true;
			break;

		default:
			break;
		}
		return true;
	}

	float mPosx;
	float mPosY;
	float mCurPosX;
	float mCurPosY;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		switch (event.getAction()) 
		{
		case MotionEvent.ACTION_DOWN:
			mPosx = event.getX();
			mPosY = event.getY();
			break;
			
		case MotionEvent.ACTION_MOVE:
			mCurPosX = event.getX();
			mCurPosY = event.getY();
			break;
		
		case MotionEvent.ACTION_UP:
			if(mCurPosX-mPosx>0&&(Math.abs(mCurPosX-mPosx)>25))
			{
				// 向右滑动
				
				settingsManager.setScreenBrightness(settingsManager.getScreenBrightness()+20);
			}
			else if(mCurPosX-mPosx<0&&(Math.abs(mCurPosX-mPosx)>25))
			{
				// 向左滑动
				settingsManager.setScreenBrightness(settingsManager.getScreenBrightness()-20);
			}
			break;

		default:
			break;
		}
		return true;
	}
	
	
	
}
