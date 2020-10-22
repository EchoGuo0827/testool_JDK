package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig58.java 
 * description 		: 控制系统设置中日期与时间显示,A5巴西海外版本导入
 * related document : 
 * history 		 	: 变更说明										变更时间			变更人员
 *			  		  控制系统设置中日期与时间显示,A5巴西海外版本导入		    20181108	 	zhengxq
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig58 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TAG = "SystemConfig58";
	private final String TESTITEM = "setDateTimeSettingsDisplay";
	private SettingsManager settingsManager = null;
	private Gui gui = new Gui(myactivity, handler);
	
	public void systemconfig58()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.BRASIL)
		{
			gui.cls_printf("该版本非巴西分支，不支持该案例测试".getBytes());
			return;
		}
		boolean iRet;
		String funcName = "systemconfig58";
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		// case1.1:设置为1系统设置中日期与时间显示的菜单--"隐藏"
		if((iRet=settingsManager.setDateTimeSettingsDisplay(1))==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请去设置查看[日期和时间]菜单是否被隐藏,[确认]是,[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:隐藏[日期和时间]菜单功能未生效", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.2:设置为0系统设置日期与时间菜单--"显示",通过接口关闭该菜单后重新打开改菜单应正常
		if((iRet=settingsManager.setDateTimeSettingsDisplay(0))==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请去设置查看[日期和时间]菜单是否已显示,[确认]是,[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:显示[日期和时间]菜单功能未生效", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2.1:多次重复打开后再关闭,应可成功关闭[日期和时间]菜单
		int random = (int) (Math.random()*10+2);// 确保至少两次
		for(int i=0;i<random;i++)
		{
			if((iRet=settingsManager.setDateTimeSettingsDisplay(0))==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),i+1,TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// 关闭操作
		if((iRet=settingsManager.setDateTimeSettingsDisplay(1))==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请去设置查看[日期和时间]菜单是否被隐藏,[确认]是,[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:隐藏[日期和时间]菜单功能未生效", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2.2:多次重复关闭后再打开，应可成功打开[日期和时间]的菜单
		for(int i=0;i<random;i++)
		{
			if((iRet=settingsManager.setDateTimeSettingsDisplay(1))==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),i+1,TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// 关闭操作
		if((iRet=settingsManager.setDateTimeSettingsDisplay(0))==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请去设置查看[日期和时间]菜单是否显示,[确认]是,[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:显示[日期和时间]菜单功能未生效", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3:异常测试，传入的参数非0和1
		if((iRet=settingsManager.setDateTimeSettingsDisplay(-1))==true)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet=settingsManager.setDateTimeSettingsDisplay(2))==true)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet=settingsManager.setDateTimeSettingsDisplay(100))==true)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(TAG,"systemconfig58",gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
