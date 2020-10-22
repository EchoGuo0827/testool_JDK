package com.example.highplattest.android;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android6.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180410 
 * directory 		: 
 * description 		: 测试Android原生休眠接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180410 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android6 extends UnitFragment {
	public final String TAG = Android6.class.getSimpleName();
	private String TESTITEM = "休眠原生接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private int defaultScreenOffTime=-1;
	private int screenOffTime=-1;
	private int myTime=-1;
	int[] time={15*1000,30*1000,60*1000,60*2*1000,60*5*1000,60*10*1000,60*30*1000,Integer.MAX_VALUE};//休眠时间，单位为ms
		
	public void android6()
	{
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		try 
		{
			defaultScreenOffTime = Settings.System.getInt(myactivity.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) 
		{
			e.printStackTrace();
		}
		LoggerUtil.v(TAG+",android6===defaultScreenOffTime="+defaultScreenOffTime);
        //case1:循环设置各种休眠时长，可跳过某种休眠时长
		for (int i = 0; i < time.length; i++) 
		{
			myTime=time[i]/1000;
			if(gui.cls_show_msg("是否将安卓端休眠时间设置为"+time[i]/1000+"s，[确认]是，[其他]否")==ENTER)
			{
				Settings.System.putInt(myactivity.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT,time[i]);
					while (true) 
					{
						gui.cls_show_msg1(1, myTime+"s后休眠，休眠后短按电源键唤醒");
						myTime = myTime -1;
						if(myTime == 1)
						{
							gui.cls_show_msg1(1, "马上休眠，休眠后短按电源键唤醒");
							break;
						}
					}
					if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
					{
						gui.cls_show_msg1_record(TAG, "android6", gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
					}
					
					try 
					{
						screenOffTime = Settings.System.getInt(myactivity.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT);
					} catch (SettingNotFoundException e)
					{
						e.printStackTrace();
					}
					gui.cls_show_msg1(gScreenTime, "设置的休眠时间=%ds,获取的休眠时间=%ds", time[i]/1000, screenOffTime/1000);
					if (screenOffTime/1000 != time[i]/1000) 
					{
						gui.cls_show_msg1_record(TAG, "android6", gKeepTimeErr, "line %d:%s测试失败(预期=%ds，实际=%ds)", Tools.getLineInfo(), TESTITEM,time[i]/1000,screenOffTime/1000);
						if (!GlobalVariable.isContinue)
							return;
					}
			}
			
		}
		//测试后置，休眠恢复为测试前的休眠时长
		Settings.System.putInt(myactivity.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT,defaultScreenOffTime);		
		gui.cls_show_msg1_record(TAG, "android6",gScreenTime,"%s测试通过", TESTITEM);
	}


	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
