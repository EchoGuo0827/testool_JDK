package com.example.highplattest.systemconfig;

import java.io.File;

import android.newland.SettingsManager;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SystemConfig模块
 * file name 		: SystemConfig68.java 
 * description 		: setSystemLogEnabled设置系统日志开启 关闭
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 * 						创建						20200720        	郑薛晴
 *                   增加日志生成后1s判断日志大小                     20200907           郑佳雯
 ************************************************************************ 
 *
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig68 extends UnitFragment{
	private final String TESTITEM = "setSystemLogEnabled设置系统日志开启或 关闭";
	private String fileName=SystemConfig68.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	SettingsManager settingsManager=null;
	
	public void systemconfig68()
	{
		String funcName = "systemconfig68";
		boolean ret=false;
		
		FileSystem fileSystem = new FileSystem();
		File file = new File("/data/log");
		long size;
		float fileSize=0;
		
		if(gui.cls_show_msg("是否进行重启验证")==ENTER)
		{
			size = fileSystem.JDK_FsFileSizes(file);
			// 生成日志
			SystemConfig0.genLog1M(1);
			SystemClock.sleep(1000);
			size = fileSystem.JDK_FsFileSizes(file)-size;
			fileSize =  (float) (size*1.0/1024/1024);
			boolean status = settingsManager.isSystemLogEnabled();
			if(status)
			{
				if(fileSize<1)
				{
					gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			else
			{
				if(fileSize>1)
				{
					gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			gui.cls_show_msg1(1, "重启验证通过");
		}
		
		// case1.1:系统日志打开,系统日志生成到对应的目录/data/log
		LoggerUtil.e(funcName+"->case1.1系统日志打开,系统日志生成到对应的目录/data/log...");
		gui.cls_show_msg1(1, "case1.1系统日志打开,系统日志生成到对应的目录/data/log...");
		if((ret=defineSetSystemLogEnabled(true))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		size = fileSystem.JDK_FsFileSizes(file);
		// 生成日志
		SystemConfig0.genLog1M(1);
		SystemClock.sleep(1000);
		size = fileSystem.JDK_FsFileSizes(file)-size;
		fileSize =  (float) (size*1.0/1024/1024);
		gui.cls_show_msg1(2, "case1.1生成成日志大小size="+fileSize);
		
		if(fileSize<1)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1.2:系统日志关闭,系统日志无法生成到对应的文件，日志文件无变化
		LoggerUtil.e(funcName+"->case1.2系统日志打开,系统日志生成到对应的目录/data/log...");
		gui.cls_show_msg1(1, "case1.2系统日志打开,系统日志生成到对应的目录/data/log...");
		if((ret=defineSetSystemLogEnabled(false))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		size = fileSystem.JDK_FsFileSizes(file);
		// 生成日志
		SystemConfig0.genLog1M(1);
		SystemClock.sleep(1000);
		size = fileSystem.JDK_FsFileSizes(file)-size;
		fileSize =  (float) (size*1.0/1024/1024);
		gui.cls_show_msg1(2, "case1.2生成成日志大小size="+fileSize);
		
		if(fileSize>1)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.1:系统日志打开时设备重启后，保持原有的日志开关状态,系统日志生成到对应的目录/data/log
		LoggerUtil.e(funcName+"->case4.1:系统日志打开时设备重启后，保持原有的日志开关状态,系统日志生成到对应的目录/data/log");
		int nkeyIn = gui.cls_show_msg("case4.1:系统日志打开时设备重启后，保持原有的日志开关状态,系统日志生成到对应的目录/data/log,是否立即重启,是[确认],否[其他按键]");
		if(nkeyIn==ENTER)
		{
			if((ret=defineSetSystemLogEnabled(true))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			Tools.reboot(myactivity);
		}
		// case4.2:系统日志打开时设备关闭后，保持原有的日志开关状态,系统日志无法生成到对应的文件，日志文件无变化
		LoggerUtil.e(funcName+"->case4.2:系统日志关闭时设备重启后，保持原有的日志开关状态,系统日志无法生成到对应的目录/data/log");
		nkeyIn = gui.cls_show_msg("case4.2:系统日志关闭时设备重启后，保持原有的日志开关状态,系统日志无法生成到对应的目录/data/log,是否立即重启,是[确认],否其他按键]");
		if(nkeyIn==ENTER)
		{
			if((ret=defineSetSystemLogEnabled(false))!=true)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			Tools.reboot(myactivity);
		}

		// case5.1:系统日志关闭，案例打10M左右的日志，系统日志打开，APP导出日志,导出日志后可以看到案例打的10M日志信息（日志信息会保存在缓存里，日志关闭再打开会生成一个新的日志文件）
		LoggerUtil.e(funcName+"->case5.1系统日志关闭，案例打10M左右的日志，系统日志打开，APP导出日志,导出日志后可以看到案例打的10M日志信息...日志关闭再打开会生成一个新的日志文件");
		gui.cls_show_msg1(1, "case5.1系统日志关闭，案例打10M左右的日志，系统日志打开，APP导出日志,导出日志后可以看到案例打的10M日志信息...日志关闭再打开会生成一个新的日志文件");
		if((ret=defineSetSystemLogEnabled(false))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		fileSize = (float) (fileSystem.JDK_FsFileSizes(file)*1.0/1024/1024);
		// 生成日志
		gui.cls_printf("10M日志生成中...".getBytes());
		SystemConfig0.genLog1M(10);
		if((ret=defineSetSystemLogEnabled(true))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line  %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请使用自检应用导出日志,未打案例日志前的日志文件大小="+fileSize+"M,请将导出日志大小与未打案例日志前大小对比,前后大小应大于10M,并且会生成一个新的日志文件,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case5.2:系统日志打开，写日志,再次调用系统日志打开，APP导出日志，不会生成新的日志文件 20200807
		if((ret=defineSetSystemLogEnabled(true))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("case5.2 系统日志打开，写日志,再次调用系统日志打开，APP导出日志，不会生成新的日志文件");
		SystemConfig0.genLog1M(1);
		if((ret=defineSetSystemLogEnabled(true))!=true)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请使用自检应用导出日志,是否未生成新的日志文件,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	private boolean defineSetSystemLogEnabled(boolean isStatus)
	{
		boolean ret = settingsManager.setSystemLogEnabled(isStatus);
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
