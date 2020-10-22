package com.example.highplattest.android;

import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android9.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180507 
 * directory 		: 
 * description 		: 测试Android原生应用内存接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180507 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android9 extends UnitFragment {
	public final String TAG = Android9.class.getSimpleName();
	private String TESTITEM = "应用接口测试";
	private Gui gui = new Gui(myactivity, handler);
		
	@SuppressWarnings("static-access")
	public void android9(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		//case1：获取所以应用
		PackageManager packageManager = myactivity.getPackageManager();
		List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		List<PackageInfo> apps1 = new ArrayList<PackageInfo>();//第三方
		List<PackageInfo> apps2 = new ArrayList<PackageInfo>();//系统应用
		StringBuffer sb1=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		for (int i = 0; i < packageInfoList.size(); i++) {
			PackageInfo pak = (PackageInfo) packageInfoList.get(i);
			pak.applicationInfo.loadLabel(packageManager);
			// 判断是否为系统预装的应用
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 第三方应用
				apps1.add(pak);
				sb1.append(pak.applicationInfo.loadLabel(packageManager)+";");
			} else {
				// 系统应用
				apps2.add(pak);
				sb2.append(pak.applicationInfo.loadLabel(packageManager)+";");
			}
		}
		if (gui.cls_show_msg("查看设置--应用中的所有第三方应用是否为%s,[确认]是，[其他]否",sb1.toString()) != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, "android9", gKeepTimeErr, "line %d:%s获取第三方应用异常", Tools.getLineInfo(), TESTITEM);
		}
		if (gui.cls_show_msg("查看设置--应用中的所有系统应用是否为%s,[确认]是，[其他]否",sb2.toString()) != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, "android9", gKeepTimeErr, "line %d:%s获取系统应用异常", Tools.getLineInfo(), TESTITEM);
		}
		gui.cls_show_msg1_record(TAG, "android9",gScreenTime,"%s测试通过", TESTITEM);
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
