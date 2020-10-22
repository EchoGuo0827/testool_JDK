package com.example.highplattest.systemconfig;

import java.util.concurrent.TimeUnit;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.newland.SettingsManager;

/************************************************************************
 * 
 * module 		: Android系统设置相关的接口 
 * file name 	: SystemConfig1.java
 * Author 		: zhengxq
 * version 		: 
 * DATE 		: 20141201 
 * directory 	: 
 * description 	:测试系统设置setScreenBrightness和getScreenBrightness related 
 * document 	: 
 * history 		:author 		date 		remarks 
 * 				zhengxq 		20141201 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig1 extends UnitFragment {
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME="SystemConfig1";
	private SettingsManager settingsManager = null;
	private final String TESTITEM = "setScreenBrightness和getScreenBrightness";
	int lightValue;
	boolean ret = false;
	private Gui gui=null;

	public void systemconfig1() 
	{
	  AudioManager mAudioManager = (AudioManager)myactivity.getSystemService(Context.AUDIO_SERVICE);
		  
		  //测试前置，获取媒体音量、铃声音量的当前音量    
		  int currentVolume1 = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  
		  int currentVolume2 = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);  
		  
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		/* private & local definition */
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
			return;
		}
		int middleValue = 0;
		
		/* process body */
		gui.cls_show_msg1(2, TESTITEM + "测试中...");
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gScreenTime, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		// 测试前置，获取亮度值，测试完成之后还原亮度值
		lightValue = settingsManager.getScreenBrightness();
		
		
        if (gui.cls_show_msg("是否要查看当前的音量和屏幕亮度，是【确认】否【其他】") == ENTER) 
        {
      	  gui.cls_show_msg("当前媒体音量为%d，铃声音量为%d,当前亮度%d", currentVolume1, currentVolume2,lightValue);
		  }
		// 确认亮度是否为默认亮度102，通用默认亮度为40%
		if(lightValue != 102)
		{
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gScreenTime, "line %d:%s获取默认亮度值错误(预期=153，实际=%d)", Tools.getLineInfo(),TESTITEM,lightValue);
			if (!GlobalVariable.isContinue)
				return;
		}
		

		// case1:选择一些值进行亮度测试：0-255轮询一遍，应该返回true，可以看到亮度逐渐增强
		for (int i = 0; i <= 255; i++) {
			if ((ret = settingsManager.setScreenBrightness(i)) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// case1.1:setScreenBrightness和getScreenBrightness的数值应该相等
			int value = settingsManager.getScreenBrightness();
			gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "设置的屏幕亮度值=%d,获取的屏幕亮度值=%d", i, value);
			if (value != i) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(预期=%d，实际=%d)", Tools.getLineInfo(), TESTITEM,i,value);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		/*// 确认亮度是否增强，过去测试无问题，不需要改判断操作
		show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "看到的屏幕亮度是递增的吗？");

		if ((ret = GlobalVariable.FLAG_SYSTEM_SIGN) == false) {
			postTest(Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}*/

		// case2:参数不在范围之内，选择 -1 256边界值进行测试，应该返回false，并且亮度还为原先的亮度值255
		if (ret = settingsManager.setScreenBrightness(-1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if ((middleValue = settingsManager.getScreenBrightness()) != 255) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM,middleValue);
			if (!GlobalVariable.isContinue)
				return;
		}

		if (ret = settingsManager.setScreenBrightness(256)) {
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if ((middleValue = settingsManager.getScreenBrightness()) != 255) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM,middleValue);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，重新设置为POS原先的亮度值
		if ((ret = settingsManager.setScreenBrightness(lightValue)) == false) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,"systemconfig1",gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}


	@Override
	public void onTestUp() 
	{
		gui=new Gui(myactivity,handler);
	}

	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			if ((ret = settingsManager.setScreenBrightness(lightValue)) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,"onTestDown",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM, ret);
			}
		}
		settingsManager = null;
		gui = null;
	}
}
