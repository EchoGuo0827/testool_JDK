package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141028
 * directory 		: 
 * description 		: 测试系统设置setScreenTimeout
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20141028	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig2 extends UnitFragment
{
	private final String TESTITEM = "设置Android端休眠时间";
	private Gui gui = null;
	private SettingsManager settingsManager;
	private int timeValue;
	private String fileName="SystemConfig2";
	public void systemconfig2() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		boolean ret;
		int total = 60,sub=0;
		long startTime = 0;
		int screenOffTime = 0;
		//获取休眠时间(ms)，测试完成之后还原休眠设置
		timeValue = getScreenOffTime(2); 
		
		/*process body*/
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		try {
			settingsManager = (SettingsManager) myactivity
					.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1(2, "该用例不支持");
			return;
		}
		// case1:设置休眠时间为15s，30s，单位为ms
		int[] timeCase1 = { 15*1000, 30*1000 };
		for (int j = 0; j < timeCase1.length; j++) 
		{
			total = timeCase1[j]/1000;
			gui.cls_show_msg("休眠时间将设置为%ds",total);
			if ((ret = settingsManager.setScreenTimeout(timeCase1[j]))==false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			while (true) 
			{
				gui.cls_show_msg1(1, total+"s后休眠，休眠后短按电源键唤醒");
				total = total -1;
				if(total == 1)
				{
					gui.cls_show_msg1(1, "马上休眠，休眠后短按电源键唤醒");
					break;
				}
			}
			if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case1.1:setScreenTimeout和安卓原生方法获取到的休眠的数值应该相等
			screenOffTime = getScreenOffTime(0);
			total = timeCase1[j]/1000;
			gui.cls_show_msg1(gScreenTime, "设置的休眠时间=%ds,获取的休眠时间=%ds", total, screenOffTime);
			if (screenOffTime != total) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr, "line %d:%s测试失败(预期=%ds，实际=%ds)", Tools.getLineInfo(), TESTITEM,total,screenOffTime);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case2:设置休眠时间为1分钟、5分钟，单位为ms，应该返回true，能够进行相应时间的休眠操作
		int[] timeCase2 = { ONE_MIN, FIVE_MIN };
		total = 60;
		for (int i = 0; i < timeCase2.length; i++) 
		{
			int sleeptime = timeCase2[i]/1000/60;
			gui.ShowMessageBox(String.format("休眠设置为%d分钟，点击任意键后开始计时", sleeptime).getBytes(), (byte) 0, WAITMAXTIME);
			if ((ret = settingsManager.setScreenTimeout(timeCase2[i]))==false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			} 
			else 
			{
				while (true) 
				{
					int time = sleeptime - sub;
					if(total==0)
					{
						total = 60;
						break;
					}
					else if (sleeptime==1||sub==sleeptime-1) 
					{
						// inputShort中有休眠2S的操作
						gui.cls_show_msg1(2, total+"s后休眠，休眠后短按电源键唤醒");
						total = total -2;
						if(total == 0)
							gui.cls_show_msg1(2, "马上休眠，休眠后短按电源键唤醒");
					}
					else if (sub < sleeptime) 
					{
						startTime = System.currentTimeMillis();
						gui.cls_show_msg1(2, time + "分钟后休眠，休眠后短按电源键唤醒");
						while(Tools.getStopTime(startTime)!=60);
						sub++;
					} 
				}
			
				if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case2.1:setScreenTimeout和安卓原生方法获取到的休眠的数值应该相等
				screenOffTime = getScreenOffTime(1);
				sleeptime = timeCase2[i]/1000/60;
				gui.cls_show_msg1(gScreenTime, "设置的休眠时间=%dmin,获取的休眠时间=%dmin", sleeptime, screenOffTime);
				if (screenOffTime != sleeptime) 
				{
					gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr, "line %d:%s测试失败(预期=%dmin，实际=%dmin)", Tools.getLineInfo(), TESTITEM,sleeptime,screenOffTime);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
		}
		gui.cls_show_msg("test---异常测试");
		// case3:设置不在范围的休眠时间 -5,7,35，负数应该返回true，正数的休眠时间往在范围的相邻休眠值靠（1分钟、2分钟、5分钟、10分钟、30分钟）
		if (ret = settingsManager.setScreenTimeout(-5)) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = settingsManager.setScreenTimeout(7))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = settingsManager.setScreenTimeout(35))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5:设置为永不休眠放置一分钟后，设置为1分钟不应立马进入休眠，休眠计时为从设置休眠时间的那一瞬间开始计时
		if ((ret = settingsManager.setScreenTimeout(-1))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 倒计时操作1分钟后开始设置休眠时间为1分钟
		for (int j = 0; j <= 30; j++) 
		{
			gui.cls_show_msg1(2, "1分钟倒计时，请不要点击屏幕，剩余时间%ds", 60-j*2);
		}
		
		gui.cls_show_msg("休眠时间将设置为1分钟，1分钟后将休眠");
		if ((ret = settingsManager.setScreenTimeout(1*60*1000))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		while (true) 
		{
			if(total==0)
			{
				total = 60;
				break;
			}
			else
			{
				gui.cls_show_msg1(2, total+"s后休眠，休眠后短按电源键唤醒");
				total = total -2;
				if(total == 2)
					gui.cls_show_msg1(2, "马上休眠，休眠后短按电源键唤醒");
			}
		}
		if(gui.ShowMessageBox("休眠时间是否正确".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:休眠时间到未进入休眠或过早进入休眠", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4: 设置为永不休眠-1，POS应该不会进入休眠
		if ((ret = settingsManager.setScreenTimeout(-1))==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig2",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1_record(fileName,"systemconfig2",gScreenTime,"%s测试通过，默认为永不休眠，后续如果进入休眠则为异常(长按确认键退出测试)", TESTITEM);
	}
	
	/** 
	 * 原生方法获取锁屏时间，单位为ms
	 */  
	private int getScreenOffTime(int unit){
		int screenOffTime=0;  
	    try{  
	        screenOffTime = Settings.System.getInt(myactivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);  
	    }catch (SettingNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
	    
	    switch(unit){
	    case 0:
	    	screenOffTime = screenOffTime/1000;		//返回秒为单位的休眠时间
	    	break;
	    case 1:
	    	screenOffTime = screenOffTime/1000/60;	//返回分钟为单位的休眠时间
	    	break;
	    default:
	    	break;
	    }
	    
	    return screenOffTime;  
	} 
	
	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
		
	}
	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			// 测试后置：将超时时间设置为进入用例前的值
			settingsManager.setScreenTimeout(timeValue);
		}
		gui = null;
		settingsManager = null;
	}
}
