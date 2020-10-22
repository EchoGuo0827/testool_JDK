package com.example.highplattest.digled;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.digled.Digled;
/************************************************************************
 * 
 * module 			: N550的数码管显示模块
 * file name 		: DigLed3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180815
 * directory 		: 
 * description 		: brightDigLed和clrDigLed：设置亮度及开启扫描和数码管全灭
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180815         create
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class DigLed3 extends UnitFragment
{
	private final String CLASS_NAME = "DigLed3";
	private final String TESTITEM = "brightDigLed和clrDigLed(N550)";
	
	public void digled3()
	{
		/*private & local definition*/
		Digled digled = new Digled();
		Gui gui = new Gui(myactivity, handler);
		int ret = -1;
		
		// case1:参数异常测试，设置不在范围的亮度应失败
		if((ret = digled.brightDigLed(-1))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = digled.brightDigLed(8))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:不设置亮度，直接显示数码管数字应正常
		if((ret = digled.showDigLed("1.23456"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为1.23456,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3:设置各个亮度后在数码管显示金额应正常
		int[] brights = {0,1,2,3,4,5,6,7};
		String[] showLeds = {"1.1123","2.3456","3.45","0.89","1.09","0.675","1234.56","1256.89"};
		for (int i = 0; i < showLeds.length; i++) {
			if((ret = digled.brightDigLed(brights[i]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:第%d次,%s测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,ret);
				continue;
			}
			if((ret = digled.showDigLed(showLeds[i]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:第%d次,%s测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,ret);
				continue;
			}
			if(gui.cls_show_msg("数码管是否显示为%s,循环显示888888是无问题的,是[确认],否[取消]", showLeds[i])==ESC)
			{
				gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:第%d次,%s测试失败(%s)", Tools.getLineInfo(),i+1,TESTITEM,showLeds[i]);
				continue;
			}
			if((ret = digled.clrDigLed())!=NDK_OK)
			{
				gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:第%d次,%s测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,ret);
				continue;
			}
		}
		// case4:设置完亮度后直接让数码管熄灭应正常
		if((ret = digled.brightDigLed(0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = digled.clrDigLed())!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5:数码管熄灭后再设置亮度应正常
		if((ret = digled.brightDigLed(7))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case6:数码管熄灭后在显示数字应正常
		if((ret = digled.clrDigLed())!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = digled.showDigLed("1.55555"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为1.55555,循环显示88888888是无问题的,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled3", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME, "digled3", gScreenTime,"%s测试通过", TESTITEM);
		// 测试后置，熄灭数码管
		digled.clrDigLed();
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
