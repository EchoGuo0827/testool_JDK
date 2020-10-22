package com.example.highplattest.other;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: Other11.java 
 * history 		 	: 变更点													变更时间				变更人员
 * 					 N920A7支持第三方应用查看打开应用(原Systemconfig72搬移)    	   20200604				陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other11 extends UnitFragment  {
	private final String TESTITEM = "第三方应用查看打开应用(N920A7)";
	private Gui gui = null;
	private String fileName="Other11";
	  String   packagename;
	public void other11(){
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg1(2,"%s测试中",TESTITEM);		
		gui.cls_show_msg("请先确保Androidmainfest.xml文件无新大陆权限，确保测试案例为第三方应用。");
		PackageManager packageManager = myactivity.getPackageManager();
		List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		ActivityManager am = (ActivityManager) myactivity.getSystemService(Context.ACTIVITY_SERVICE);
		List<PackageInfo> app = new ArrayList<PackageInfo>();//第三方
		List<PackageInfo> app2 = new ArrayList<PackageInfo>();//系统应用
		StringBuffer sb1=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		
		//case1:获取所有应用 包括第三方应用和系统应用
		for (int i = 0; i < packageInfoList.size(); i++) {
			PackageInfo pak = (PackageInfo) packageInfoList.get(i);
			pak.applicationInfo.loadLabel(packageManager);
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 第三方应用
				app.add(pak);
				sb1.append(pak.applicationInfo.loadLabel(packageManager)+";");
			} else {
				// 系统应用
				app2.add(pak);
				sb2.append(pak.applicationInfo.loadLabel(packageManager)+";");
			}
		}
		if (gui.cls_show_msg("第三方应用是否为%s,[确认]是，[其他]否",sb1.toString()) != ENTER) 
		{
			gui.cls_show_msg1_record(TESTITEM, "Other23", gKeepTimeErr, "line %d:%s获取第三方应用异常", Tools.getLineInfo(), TESTITEM);
		}
		if (gui.cls_show_msg("系统应用是否为%s,[确认]是，[其他]否",sb2.toString()) != ENTER) 
		{
			gui.cls_show_msg1_record(TESTITEM, "Other23", gKeepTimeErr, "line %d:%s获取系统应用异常", Tools.getLineInfo(), TESTITEM);
		}
		
		//case2: 获取当前正在运行的应用
		UsageStatsManager usage = (UsageStatsManager) myactivity.getSystemService(Context.USAGE_STATS_SERVICE);
		long time = System.currentTimeMillis();
		// 获取1分钟内正在运行的应用
		List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 6000 * 10, time);
		if (stats != null) {
			SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
			for (UsageStats usageStats : stats) {
				runningTask.put(usageStats.getLastTimeUsed(), usageStats);
			}
			if (runningTask.isEmpty()) {
				gui.cls_show_msg1_record(TESTITEM, "Other11", gKeepTimeErr,"%s本次获取为空", TESTITEM);
			}

			packagename = runningTask.get(runningTask.lastKey()).getPackageName();
			gui.cls_show_msg("获取到的正在运行的应用%s", packagename);
		}
		
		gui.cls_show_msg1_record(fileName,TESTITEM,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
		
		
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
