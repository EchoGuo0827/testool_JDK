package com.example.highplattest.other;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 杀后台进程
 * file name 		: other2.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20161016
 * directory 		: 
 * description 		: 杀后台进程
 * related document :
 * history 		 	: author			date			remarks
 *			  	      wangxy		   20161016	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other2 extends UnitFragment
{
	private final String TESTITEM = "杀后台进程";
	public final String TAG = Other2.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	private UsageStatsManager mUsageStatsManager;
	private long mCurrentTime;
	public boolean ret = false;
	
	public void other2(){
		/***
		 * other2读取进程列表的getRunningAppProcesses()在android9中只能获取到自身进程，增加A9适配，在判断为android9平台时改用queryUsageStats()获取最近60s内有数据记录的进程列表，
		 * Android9安全性升级，案例自身进程和系统应用无法kill，第三方软件进程可以正常杀掉，改用forceStopPackage()关闭进程-陈丁 20200728
		 */
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A9) {
			killProcessA9();
		}else{
			killProcess();
		}
	}
	
	public void killProcessA9(){
		/* private & local definition */
		
//		StringBuffer viewStr = new StringBuffer();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other2", gScreenTime,"%s用例不支持自动化测试，请手动验证",  TESTITEM);
			return;
		}
		
		/*process body*/
        //检查数据统计权限是否开启
		while(!ret){
        ret = checkUsageStateAccessPermission(myactivity);
		}
        
		gui.cls_show_msg1_record(TAG, "other2", gScreenTime,"正在杀死当前后台存在的进程...后台进程杀死后，请查看recent键");
		//获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) myactivity.getSystemService(Context.ACTIVITY_SERVICE);
        
        //获取系统中所有正在运行的进程
        mUsageStatsManager = (UsageStatsManager) myactivity.getSystemService(Context.USAGE_STATS_SERVICE);
        if(mUsageStatsManager != null) {
            mCurrentTime = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, mCurrentTime - 60 * 1000, mCurrentTime);
            for (UsageStats appProcessInfo : stats){
            	String processName=appProcessInfo.getPackageName();
            	activityManager.killBackgroundProcesses(processName);
            	Log.e("processName",""+processName);
            }
        }
        activityManager.killBackgroundProcesses("com.example.highplattest");
        gui.cls_show_msg1_record(TAG, "other2", gScreenTime,"后台进程已杀死，请点击recent键查看");
	}
	
	public void killProcess()
	{
		/* private & local definition */
		
//		StringBuffer viewStr = new StringBuffer();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other2", gScreenTime,"%s用例不支持自动化测试，请手动验证",  TESTITEM);
			return;
		}
		/*process body*/
		gui.cls_show_msg1_record(TAG, "other2", gScreenTime,"正在杀死当前后台存在的进程...后台进程杀死后，请查看recent键");
		//获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) myactivity.getSystemService(Context.ACTIVITY_SERVICE);
      //获取系统中所有正在运行的进程
        List<RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
      //获取当前activity所在的进程
//        String currentProcess = myactivity.getApplicationInfo().processName;
        //根据包名清后台
        for (RunningAppProcessInfo appProcessInfo : appProcessInfos) {
        String processName=appProcessInfo.processName;
        	 activityManager.killBackgroundProcesses(processName);
        	 Log.e("processName",""+processName);
       }
        gui.cls_show_msg1_record(TAG, "other2", gScreenTime,"后台进程已杀死，请点击recent键查看");
		
	}
	
	//获取后台进程包名
	public static String getRunningServicesInfo(Context context) {  
        StringBuffer serviceInfo = new StringBuffer();  
        final ActivityManager activityManager = (ActivityManager) context  
                        .getSystemService(Context.ACTIVITY_SERVICE);  
        @SuppressWarnings("deprecation")
		List<RunningServiceInfo> services = activityManager.getRunningServices(100);  

        Iterator<RunningServiceInfo> l = services.iterator();  
        while (l.hasNext()) {  
                RunningServiceInfo si = (RunningServiceInfo) l.next();  
//                serviceInfo.append("pid: ").append(si.pid);  
                serviceInfo.append(si.process+",");   
//                serviceInfo.append("\nservice: ").append(si.service);  
//                serviceInfo.append("\ncrashCount: ").append(si.crashCount);  
//                serviceInfo.append("\nclientCount: ").append(si.clientCount);  
//                serviceInfo.append(";")
                ;  
        }  
        
        return serviceInfo.toString();  
}
	
    public static boolean checkUsageStateAccessPermission(Context context) {
        if(!checkAppUsagePermission(context)) {
            requestAppUsagePermission(context);
            return false;
        }
        return true;
    }
    
    public static boolean checkAppUsagePermission(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if(usageStatsManager == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        // try to get app usage state in last 1 min
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 60 * 1000, currentTime);
        if (stats.size() == 0) {
            return false;
        }

        return true;
    }
    
    public static void requestAppUsagePermission(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.i("TAG","Start usage access settings activity fail!");
        }
    }
    

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
		
	}  
}
