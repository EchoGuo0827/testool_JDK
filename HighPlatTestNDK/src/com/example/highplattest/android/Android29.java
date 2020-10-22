package com.example.highplattest.android;

import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android29.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180926 
 * directory 		: 
 * description 		: X5双屏异显
 * related document : 
 * history 		 	: 变更点						                                                 变更时间			变更人员
 *			  		  增加休眠后查看双屏异显		 	           			20200408 		chendin
 *                   X1强制双屏同显方法已弃用，改为Intent方式设置       			20200512         weimj
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android29 extends UnitFragment {
	public final String TAG = Android29.class.getSimpleName();
	private String TESTITEM = "X系列双屏异显";
	private Gui gui = new Gui(myactivity, handler);
	private DifferentDisplay presentation;
    private Display[] presentationDisplays;
	private SettingsManager settingsManager = null;
		
	public void android29(){
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		int nKeyIn = gui.cls_show_msg("%s\n1.双屏异显测试\n2.默认值测试\n3.休眠唤醒测试", TESTITEM);
		switch (nKeyIn){
		case '1':
			normalTest();
			break;
			
		case '2':
			defaultTest();
			break;
			
		case '3':
			sleepTest();
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}
		
        gui.cls_show_msg1_record(TAG, "android29",gScreenTime,"%s测试通过", TESTITEM);
		
	}
	
	//双屏异显示休眠唤醒测试
	private void sleepTest() {
		DisplayManager displayManager = (DisplayManager)myactivity.getSystemService(Context.DISPLAY_SERVICE);
		//case1: 打开双屏异显
        presentationDisplays = displayManager.getDisplays();
		gui.cls_show_msg("双屏异显休眠测试中...任意键开启双屏异显");
        boolean ret = false;
        //双屏异显
        myactivity.runOnUiThread(new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() 
			{
				presentation = new DifferentDisplay(myactivity, presentationDisplays[1]);
		        presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
			    presentation.show();
			}
		});
        if ((gui.ShowMessageBox(("请确认是否双屏异显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
 		{
         	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
 			if (!GlobalVariable.isContinue)
 				return;
 		}
         //case2: 设置接口自动休眠后唤醒
        gui.cls_show_msg("设置15s进入休眠状态，休眠后请手动点击屏幕查看是否还是双屏异显");
		if ((ret = settingsManager.setScreenTimeout(15000))==false) 
		{
			gui.cls_show_msg1_record(TAG,"android29",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		for (int i = 0; i < 15; i++) {
			gui.cls_show_msg1(1, "倒计时%ds。请不要点击屏幕",15-i );
			
		}
	      
		if ((gui.ShowMessageBox(("请确认是否双屏异显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
	 	{
	         	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
	 			if (!GlobalVariable.isContinue)
	 				return;
	 		}
		 //case3:手动进入休眠后唤醒
		  gui.cls_show_msg1(2,"请手动进入休眠后唤醒。。。。。。。");
			if ((gui.ShowMessageBox(("请确认是否双屏异显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		 	{
		         	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
		 			if (!GlobalVariable.isContinue)
		 				return;
		 		}
			  gui.cls_show_msg("测试后置，恢复同显。。。。。。。按任意键继续");
			//测试后置 恢复双屏同显
			  presentation.dismiss();
			  settingsManager.setScreenTimeout(-1);
				if ((gui.ShowMessageBox(("请确认是否双屏同显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
			 	{
			         	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
			 			if (!GlobalVariable.isContinue)
			 				return;
			 		}
			 gui.cls_show_msg1_record(TAG,"android29",gScreenTime,"%s测试通过", TESTITEM);
	}

	public void normalTest(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
        boolean ret = false;
        DisplayManager displayManager = (DisplayManager)myactivity.getSystemService(Context.DISPLAY_SERVICE);
        //获取屏幕数量
        presentationDisplays = displayManager.getDisplays();
        int len=presentationDisplays.length;
        //获取的屏幕数量<1说明没有多个屏幕
        if(len<1)
        {
        	 gui.cls_show_msg1(2,"当前设备只有一个屏幕");
        	 return;        			 
        }
        
        //双屏异显
        myactivity.runOnUiThread(new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() 
			{
				presentation = new DifferentDisplay(myactivity, presentationDisplays[1]);
		        presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
			    presentation.show();
			}
		});
        
        if ((gui.ShowMessageBox(("请确认是否双屏异显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
        	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
        gui.cls_show_msg("请点击屏幕home键-------回来后按确认键");
        if ((gui.ShowMessageBox(("请确认是否还是双屏异显【确认】【其他 】").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
        	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
        
        /**开发回复X1设备不提供setSecPanelTouch、getSecPanelTouch、setSecPanelMirro、getSecPanelMirro接口*/
        if(GlobalVariable.currentPlatform!=Model_Type.X1)
        {
        	settingsManager.setSecPanelMirro(true);
        }
        else
        {
            Intent intent = new Intent();
            intent.setAction("presentation.show.disable");//取消异显
            myactivity.sendBroadcast(intent);
            if ((gui.ShowMessageBox(("强制双屏同显已开启，请确认是否双屏同显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
    		{
            	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s强制双屏同显测试失败",Tools.getLineInfo(), TESTITEM);
    			if (!GlobalVariable.isContinue)
    				return;
    		}
        }
        
        if(GlobalVariable.currentPlatform!=Model_Type.X1)
        {
        	settingsManager.setSecPanelMirro(false);
        }
        else
        {
            Intent intent = new Intent();
            intent.setAction("presentation.show.enable");//副屏使用异显
            myactivity.sendBroadcast(intent);
            if ((gui.ShowMessageBox(("强制双屏同显已关闭，请确认是否双屏异显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
    		{
            	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s强制双屏同显测试失败",Tools.getLineInfo(), TESTITEM);
    			if (!GlobalVariable.isContinue)
    				return;
    		}
        }

        //同显
        presentation.dismiss();
        if ((gui.ShowMessageBox(("请确认是否双屏同显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
        	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏同显测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
        
	}
	public void defaultTest(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
        DisplayManager displayManager = (DisplayManager)myactivity.getSystemService(Context.DISPLAY_SERVICE);
        //获取屏幕数量
        presentationDisplays = displayManager.getDisplays();
        int len=presentationDisplays.length;
        //获取的屏幕数量<1说明没有多个屏幕
        if(len<1)
        {
        	 gui.cls_show_msg1(2,"当前设备只有一个屏幕");
        	 return;        			 
        }
        
        if(gui.cls_show_msg("请确认是否已恢复出厂设置,是[确定],否[其他]")!=ENTER){
        	return;
        }
        
        //双屏异显
        myactivity.runOnUiThread(new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() 
			{
				presentation = new DifferentDisplay(myactivity, presentationDisplays[1]);
		        presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
			    presentation.show();
			}
		});
        if ((gui.ShowMessageBox(("请确认双屏异显是否设置成功").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
        	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
        
//        gui.cls_show_msg("请点击屏幕home键-------回来后按确认键");
//        if ((gui.ShowMessageBox(("请确认是否还是双屏异显【确认】【其他 】").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
//		{
//        	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏异显测试失败",Tools.getLineInfo(), TESTITEM);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
        
        //后置：恢复双屏同显
        presentation.dismiss();
        if ((gui.ShowMessageBox(("请确认是否双屏同显").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
        	gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr, "line %d:%s双屏同显测试失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}  
	}
	
    //副屏的显示内容
	public class DifferentDisplay extends Presentation {

		public DifferentDisplay(Context outerContext, Display display) {
			super(outerContext, display);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.presentation_content);

		}

	}
	
	@Override
	public void onTestUp() {
		
	}
	
	@Override
	public void onStart() {
//		gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr,"调用了onStart()方法");
		Log.d("eric", "调用了onStart()方法");
		super.onStart();
	}
	@Override
	public void onStop() {
//		gui.cls_show_msg1_record(TAG, "android29", gKeepTimeErr,"调用了onStop()方法");
		Log.d("eric", "调用了onStop()方法");
		super.onStop();
	}

	@Override
	public void onTestDown() {
		
	}

}
