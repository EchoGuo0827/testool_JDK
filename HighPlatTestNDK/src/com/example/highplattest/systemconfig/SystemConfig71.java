package com.example.highplattest.systemconfig;


import android.newland.SettingsManager;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SystemConfig模块
 * file name 		: SystemConfig71.java 
 * description 		: exportSystemLog()导出系统日志请求
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 * 						创建						20200720        	郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig71 extends UnitFragment{
	private final String TESTITEM = "exportSystemLog()导出系统日志请求";
	private String fileName=SystemConfig68.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	SettingsManager settingsManager=null;
	
	public void systemconfig71()
	{
		String funcName="systemconfig71";
		boolean ret;
		
		// case5.2：系统日志打开情况下，写10M日志，调用导出日志接口，APP导出日志，最新的日志文件不包含任何日志
		if(gui.cls_show_msg("是否测试case5.2:系统日志打开情况下，写10M日志，调用导出日志接口，APP导出日志，最新的日志文件不包含任何日志,是[确认],否[其他]")==ENTER)
		{
			LoggerUtil.e(funcName+"->case5.2:系统日志打开情况下，写10M日志，调用导出日志接口，APP导出日志，最新的日志文件不包含任何日志");
			gui.cls_show_msg1(1, "case5.2:系统日志打开情况下，写10M日志，调用导出日志接口，APP导出日志，最新的日志文件不包含任何日志");
			if((ret=defineSetSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			SystemConfig0.genLog1M(10);
			LoggerUtil.e(funcName+"->case5.2 end");
			SystemClock.sleep(1000);
			if((ret = defineExportSystemLog())!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(gui.cls_show_msg("请使用自检导出日志,最新的日志文件是否为空,是[确认],否[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case1.1:系统日志打开，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时导出的日志文件包含刚写入的LOG信息
		if(gui.cls_show_msg("是否测试case1.1:系统日志打开，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时导出的日志文件包含刚写入的LOG信息,是[确认],否[其他]")==ENTER)
		{
			LoggerUtil.e(funcName+"->case1.1:系统日志打开，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时导出的日志文件包含刚写入的LOG信息...");
			gui.cls_show_msg1(1, "case1.1:系统日志打开，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时导出的日志文件包含刚写入的LOG信息...");
			if((ret=defineSetSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = defineExportSystemLog())!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			SystemClock.sleep(1000);
			SystemConfig0.genLog1M(1);
			LoggerUtil.e(funcName+"->case1.1 end");
			if(gui.cls_show_msg("请使用自检导出日志,案例打的日志开始是:genLog->start,中间数据是:111一大行，222一大行，333一行，444一大行，555一大行,结束是：genLog>end,请查看最新的日志文件是否只包含这些日志,是[确认],否[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		// case1.2:系统日志关闭，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时exportSystemLog不生效，直接返回true
		if(gui.cls_show_msg("是否测试case1.2:系统日志关闭，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时exportSystemLog不生效，直接返回true,是[确认],否[其他]")==ENTER)
		{
			LoggerUtil.e(funcName+"->case1.2:系统日志关闭，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时不生成新的日志文件,最新的文件里也没有刚才写的日志内容");
			gui.cls_show_msg1(1, "case1.2:系统日志关闭，案例写不同等级的日志，调用导出系统日志接口，APP导出日志文件,此时不生成新的日志文件,最新的文件里也没有刚才写的日志内容");
			if((ret=defineSetSystemLogEnabled(false))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// 关闭日志不是一调用马上就完成
			if((ret = defineExportSystemLog())!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//按照马鑫汶建议在此case打印日志之前加上1s的休眠,20200804
			SystemClock.sleep(1000);
			SystemConfig0.genLog1M(1);
			LoggerUtil.e(funcName+"->case1.2 end");
			if(gui.cls_show_msg("请使用自检导出日志,未生成新的日志文件,最新的文件里也没有刚才写的日志内容,是[确认],否[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		// case4.1:系统日志打开，当前日志文件达到19M左右，调用导出系统日志接口，案例打LOG，APP导出日志文件.此时会新生成一个日志文件，导出日志文件，案例打的日志信息是在一个新文件生成
		if(gui.cls_show_msg("case4.1:系统日志打开，当前日志文件达到19M左右，调用导出系统日志接口，案例打LOG，APP导出日志文件.此时会新生成一个日志文件，导出日志文件，案例打的日志信息是在一个新文件生成,是[确认],否[其他]")==ENTER)
		{
			LoggerUtil.e(funcName+"->case4.1系统日志打开，当前日志文件达到19M左右，调用导出系统日志接口，案例打LOG，APP导出日志文件.此时会新生成一个日志文件，导出日志文件，案例打的日志信息是在一个新文件生成...");
			gui.cls_show_msg1(1, "case4.1系统日志打开，当前日志文件达到19M左右，调用导出系统日志接口，案例打LOG，APP导出日志文件.此时会新生成一个日志文件，导出日志文件，案例打的日志信息是在一个新文件生成...");
			if((ret=defineSetSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_printf("生成19M日志文件...".getBytes());
			if((ret = defineExportSystemLog())!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			SystemConfig0.genLog1M(19);
			SystemClock.sleep(1000);// 加个延时
			gui.cls_printf("新生成日志文件...".getBytes());
			if((ret = defineExportSystemLog())!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			LoggerUtil.d(funcName+"->case4.1新生成日志文件测试");
			
			if(gui.cls_show_msg("请使用自检导出日志,案例打的日志[case4.1新生成日志文件测试],请查看最新的日志文件包含该日志信息,是[确认],否[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case4.2:系统日志打开，调用导出系统日志接口，案例打LOG为1M左右，再次调用导出系统日志接口，重复10次左右，APP导出日志文件,可以看到导出的日志有10个连续的1M左右的日志文件
		if(gui.cls_show_msg("case4.2:系统日志打开，调用导出系统日志接口，案例打LOG为1M左右，再次调用导出系统日志接口，重复10次左右，APP导出日志文件,可以看到导出的日志有10个连续的1M左右的日志文件,是[确认],否[其他]")==ENTER)
		{
			LoggerUtil.e(funcName+"->case4.2:系统日志打开，调用导出系统日志接口，案例打LOG为1M左右，再次调用导出系统日志接口，重复10次左右，APP导出日志文件,可以看到导出的日志有10个连续的1M左右的日志文件...");
			gui.cls_show_msg1(1, "case4.2:系统日志打开，调用导出系统日志接口，案例打LOG为1M左右，再次调用导出系统日志接口，重复10次左右，APP导出日志文件,可以看到导出的日志有10个连续的1M左右的日志文件...");
			if((ret=defineSetSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_printf("正在写日志中...".getBytes());
			for (int j = 0; j < 10; j++) {
				if((ret = defineExportSystemLog())!=true)
				{
					gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				SystemConfig0.genLog1M(1);
			}

			if(gui.cls_show_msg("请使用自检导出日志,请查看最新10个日志文件是否为1M左右,是[确认],否[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		// case5.2:系统日志关闭，案例打10M左右的日志，系统日志打开，调用系统日志导出接口，APP导出日志,案例打的10M的日志会保存在之前的日志文件，不在当前的日志文件
		if(gui.cls_show_msg("case5.2:case5.2:系统日志关闭，案例打10M左右的日志，系统日志打开，调用系统日志导出接口，APP导出日志,案例打的10M的日志会保存在之前的日志文件，不在当前的日志文件...,是[确认],否[其他]")==ENTER)
		{
			LoggerUtil.e(funcName+"->case5.2:系统日志关闭，案例打10M左右的日志，系统日志打开，调用系统日志导出接口，APP导出日志,案例打的10M的日志会保存在之前的日志文件，不在当前的日志文件...");
			gui.cls_show_msg1(1, "case5.2:系统日志关闭，案例打10M左右的日志，系统日志打开，调用系统日志导出接口，APP导出日志,案例打的10M的日志会保存在之前的日志文件，不在当前的日志文件...");
			if((ret=defineSetSystemLogEnabled(false))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			SystemConfig0.genLog1M(10);
			if((ret=defineSetSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			SystemClock.sleep(1000);
			if((ret = defineExportSystemLog())!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}

			LoggerUtil.e(funcName+"->case5.2 end");
			if(gui.cls_show_msg("请使用自检导出日志,最新文件的日志信息只存在[case5.2 end],是[确认],否[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	private boolean defineSetSystemLogEnabled(boolean isStatus)
	{
		boolean ret = settingsManager.setSystemLogEnabled(isStatus);
		SystemClock.sleep(1000);
		return ret;
	}
	
	private boolean defineExportSystemLog()
	{
		boolean ret = settingsManager.exportSystemLog();
		SystemClock.sleep(1000);
		return ret;
	}

	boolean mPreLogStatus;
	@Override
	public void onTestUp() {
		settingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		mPreLogStatus = settingsManager.isSystemLogEnabled();
	}

	@Override
	public void onTestDown() {
		settingsManager.setSystemLogEnabled(mPreLogStatus);
	}

}
