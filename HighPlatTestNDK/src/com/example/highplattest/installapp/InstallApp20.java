package com.example.highplattest.installapp;

import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDataObserver.Stub;
import android.os.RemoteException;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.AppTool;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: InstallApp
 * file name 		: InstallApp20.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 
 * directory 		: 第三方清除应用缓存数据
 * description 		: 
 * related document : 
 * history 		 	: 变更点										变更时间			变更人员
 * 					由原Android31搬移(N920_A7移联支持)			    20200609		 陈丁						
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp20 extends UnitFragment {
	
	Gui gui = new Gui(myactivity, handler);
	public final String fileName = InstallApp20.class.getSimpleName();
	private String TESTITEM = "第三方清除应用缓存数据";
	
	public void installapp20(){
		String funcName = "InstallApp20";
		
		String qqMusicPackage = "com.tencent.qqmusic";
		String baiduMapPackage = "com.baidu.BaiduMap";
		String testPackage = "com.example.highplattest";
		gui.cls_show_msg("请先安装百度地图、QQ音乐并进入应用操作下使得应用有缓存数据，操作完毕后任意键继续");
		
		// 百度地图的包名：com.baidu.BaiduMap QQ音乐的包名：com.tencent.qqmusic
		long mCacheSizeQian = AppTool.getPackageSizeInfo(myactivity, baiduMapPackage);
		gui.cls_show_msg1(1,"清除百度地图的缓存数据中...");
		boolean res = deleteAppData(baiduMapPackage);
		if(res==false)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d：清除百度地图缓存失败，请查看具体的错误日志（ret=%s）", Tools.getLineInfo(),res);
			if(!GlobalVariable.isContinue)
				return;
		}
		long mCacheSizeHou = AppTool.getPackageSizeInfo(myactivity, baiduMapPackage);
		gui.cls_show_msg("清除缓存前的百度地图缓存大小=%d,清除缓存后的百度缓存大小=%d,清除缓存后的大小不等于12或0视为测试不通过", mCacheSizeQian,mCacheSizeHou);
		
		
		mCacheSizeQian = AppTool.getPackageSizeInfo(myactivity, qqMusicPackage);
		gui.cls_show_msg1(1, "清除QQ音乐缓存数据中...");
		res = deleteAppData(qqMusicPackage);
		if(res==false)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d：清除QQ音乐缓存失败，请查看具体的错误日志（ret=%s）", Tools.getLineInfo(),res);
			if(!GlobalVariable.isContinue)
				return;
		}
		mCacheSizeHou = AppTool.getPackageSizeInfo(myactivity, qqMusicPackage);
		gui.cls_show_msg("清除缓存前的QQ音乐缓存大小=%d,清除缓存后的QQ音乐地图缓存大小=%d,清除缓存后的大小不等于12或0视为测试不通过", mCacheSizeQian,mCacheSizeHou);
		
		
		/*// case2:清除自身应用的缓存数据,不能清除自身应用的data
		mCacheSizeQian = AppTool.getPackageSizeInfo(myactivity, testPackage);
		gui.cls_show_msg1(1, "清除自身应用缓存中...");
		res = deleteAppData(testPackage);
		if(res==false)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d：清除自身应用缓存失败，请查看具体的错误日志（ret=%s）", Tools.getLineInfo(),res);
			if(!GlobalVariable.isContinue)
				return;
		}
		mCacheSizeHou = AppTool.getPackageSizeInfo(myactivity, testPackage);
		gui.cls_show_msg("清除缓存前的HighPlattestNdk缓存大小=%d,清除缓存后的HighPlattestNdk缓存大小=%d,清除缓存后的大小不等于12或0视为测试不通过", mCacheSizeQian,mCacheSizeHou);*/
		
		
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	public boolean deleteAppData(String packageName)
	{
		boolean isSuc = false;
		Method clearMethod;
		
		Stub stub = new IPackageDataObserver.Stub() {
			
			@Override
			public void onRemoveCompleted(String packageName, boolean succeeded)
					throws RemoteException {
				
			}
		};
		ActivityManager am = (ActivityManager) myactivity.getSystemService(Context.ACTIVITY_SERVICE);
		try {
			clearMethod = am.getClass().getMethod("clearApplicationUserData", String.class,IPackageDataObserver.class);
			if(clearMethod!=null)
			{
				LoggerUtil.d("clearApplicationUserData 9.0");
				clearMethod.setAccessible(true);
				isSuc = (boolean) clearMethod.invoke(am, packageName,stub);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isSuc=false;
		} 
		return isSuc;
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
