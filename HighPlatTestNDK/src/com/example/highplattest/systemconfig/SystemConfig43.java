package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlCashBoxManager;
import android.newland.content.NlContext;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig43.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150505
 * directory 		: 设置钱箱工作电压，延迟
 * description 		: 测试系统设置函数setVoltage、 getVoltage、setTimeSec、getTimeSec
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20150505 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig43 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private String fileName="SystemConfig43";
	NlCashBoxManager nlCashBoxManager;
	final String TESTITEM = "setVoltage、 getVoltage、setTimeSec、getTimeSec";
	private Gui gui = null;
	
	@SuppressLint("NewApi")
	public void systemconfig43() 
	{
		String funcName = "systemconfig43";
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.CashBox))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
				return;
			}
			
			/*private & local definition*/
			int ret;
			long lgt;
			nlCashBoxManager = (NlCashBoxManager) myactivity.getSystemService(NlContext.CASHBOX_SERVICE);
			
			/*process body*/
			gui.cls_show_msg1(gScreenTime, TESTITEM + "测试中...");
			
			//X1默认电压。底层无该文件节点，测试会抛异常。X1屏蔽电压案例
			//case1:获取默认电压，预期为0（12v）
			if (GlobalVariable.currentPlatform==Model_Type.X1) {
				gui.cls_show_msg("X1默认电压12V。不测试电压相关。否则会抛出异常,任意键继续");
			}
			else {
				
		
				if((ret = nlCashBoxManager.getVoltage()) != 0)
				{
					gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s获取到的钱箱工作电压与预期不符(%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			
				//设置钱箱工作电压为1（24v）
				nlCashBoxManager.setVoltage(1);
				if((ret = nlCashBoxManager.getVoltage()) != 1)
				{
					gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s获取到的钱箱工作电压与预期不符（%d）", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			
				//电压测试结束设置钱箱工作电压为默认值0（12v）
				nlCashBoxManager.setVoltage(0);
				if((ret = nlCashBoxManager.getVoltage()) != 0)
				{
					gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s获取到的钱箱工作电压与预期不符（%d）", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			//case2
			// 获取默认延时值预期为500ms
			if((lgt = nlCashBoxManager.getTimeSec()) != 500)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:获取到的当前的延时与预期不符"+lgt, Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//设置、获取当前的延时值1000
			nlCashBoxManager.setTimeSec(1000);
			if((lgt = nlCashBoxManager.getTimeSec()) != 1000)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:获取到的当前的延时与预期不符"+lgt, Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//延迟值测试结束将延迟值设置回默认值500ms
			nlCashBoxManager.setTimeSec(500);
			if((lgt = nlCashBoxManager.getTimeSec()) != 500)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:获取到的当前的延时与预期不符"+lgt, Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		}
		else
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"%s不支持%s用例", GlobalVariable.currentPlatform,TESTITEM);
	}

	@Override
	public void onTestUp() {
		 gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
