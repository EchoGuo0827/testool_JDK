package com.example.highplattest.other;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other4.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20170906
 * directory 		: 
 * description 		: 获取应用启动次数信息
 * related document :
 * history 		 	: author			date			remarks
 *			  	      zhangxinj		   20170906	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Other4 extends UnitFragment
{
	private final String TESTITEM = "获取应用启动次数信息";
	public final String TAG = Other4.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void other4()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other4", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		gui.cls_show_msg("按任意键跳转到设置-辅助功能界面。跳转后请按返回按钮。并查看com.android.settings启动次数是否+1。。");
		Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        myactivity.startActivity(intent);
        
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		try {					
			// 反射获取AppOpsManager.setMode方法
			AppOpsManager mAppOpsManager = (AppOpsManager) myactivity.getSystemService(Context.APP_OPS_SERVICE);
			Class<AppOpsManager> appOpsManagerClass;
			appOpsManagerClass = AppOpsManager.class;
			//  int code, int uid, String packageName, int mode
			Method method = appOpsManagerClass.getMethod("setMode", int.class, int.class, String.class, int.class);
			method.setAccessible(true);
			PackageInfo packageInfo = myactivity.getPackageManager().getPackageInfo(myactivity.getPackageName(), PackageManager.GET_META_DATA);
			method.invoke(mAppOpsManager, 43, packageInfo.applicationInfo.uid, myactivity.getPackageName(), AppOpsManager.MODE_ALLOWED);
			SimpleDateFormat time=new SimpleDateFormat("yyyy MM dd HH mm ss"); 
			
			Date date = new Date();
			Calendar calendarFrom = Calendar.getInstance();
			calendarFrom.setTime(date);
			calendarFrom.set(Calendar.HOUR_OF_DAY, 0);// 设置时为0点  
			calendarFrom.set(Calendar.MINUTE, 0);// 设置分钟为0分  
			calendarFrom.set(Calendar.SECOND, 0);// 设置秒为0秒  
			calendarFrom.set(Calendar.MILLISECOND, 000);// 设置毫秒为000
			long beginTime = calendarFrom.getTimeInMillis();
			Log.v("开始时间", time.format(beginTime));
			Calendar calendarEnd = Calendar.getInstance();
			calendarEnd.setTime(date);
			calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
			calendarEnd.set(Calendar.MINUTE, 59);
			calendarEnd.set(Calendar.SECOND, 59);
			calendarEnd.set(Calendar.MILLISECOND, 999);
			long endTime = calendarEnd.getTimeInMillis();
			Log.v("开始时间", time.format(endTime));
			// 先选中才 usageStatsList.size()才会大于0
			UsageStatsManager usageStaesM = (UsageStatsManager) myactivity.getSystemService("usagestats");
			List<UsageStats> usageStatsList = usageStaesM.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, beginTime, endTime);
			StringBuffer info=new StringBuffer();
			
			for (UsageStats usageStats : usageStatsList) {
				String packName = usageStats.getPackageName();
				Field launchCountField = usageStats.getClass().getDeclaredField("mLaunchCount");
				Log.v("my","包名 : " + packName);
				Log.v("my","次数 : " + String.valueOf(launchCountField.getInt(usageStats)));
				info.append(packName+"  次数 : " + String.valueOf(launchCountField.getInt(usageStats))+"\n");
			}
			info.append("获取信息为当天信息，信息获取正确则测试通过");
			gui.cls_show_msg1_record(TAG, "other4", gScreenTime, info.toString());
			if (usageStatsList.size() < 0) {
				gui.cls_show_msg1_record(TAG, "other4", gKeepTimeErr,"line %d:获取的应用列表为空(%d)", Tools.getLineInfo(),usageStatsList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
	}
	
}
