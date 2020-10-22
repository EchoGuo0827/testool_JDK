package com.example.highplattest.installapp;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.newland.content.NlIntent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Google方式安装apk
 * file name 		: InstallApp7.java 
 * Author 			: zhangxinj	
 * version 			: 
 * DATE 			: 20180529
 * directory 		: 
 * description 		: 多线程同时安装
 * related document : 
 * history 		 	: author			date			remarks
 * 					 zhangxinj		   20180529	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp7 extends UnitFragment
{
	private final String TESTITEM = "多线程同时安装apk";
	public final String TAG = InstallApp7.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private HashMap<String, String[]> interAppPath = new HashMap<String, String[]>();
	@SuppressLint({ "SdCardPath", "NewApi" })
	public void installapp7() 
	{
		if(gui.cls_show_msg("【A7以上平台请打开xml文件中的provider标签】【测试旧验签使用旧验签测试apk,测试新验签使用新验签测试apk】请把SVN服务器上的Psbc文件夹里的mapGD_signed.apk、alipay_signed.apk、meituan_high.apk、elme_signed.apk和game_400M_signed.apk5个apk push到内置SD卡apk目录下\n是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1(2, "line %d:请先完成测试前置操作再进入本测试用例",Tools.getLineInfo());
			return;
		}
		interAppPath.put("mapGD", new String[]{GlobalVariable.sdPath+"apk/mapGD_signed.apk","com.autonavi.minimap.custom"});
		interAppPath.put("alipay", new String[]{GlobalVariable.sdPath+"apk/alipay_signed.apk","com.eg.android.AlipayGphone"});
		interAppPath.put("meituan_high", new String[]{GlobalVariable.sdPath+"apk/meituan_high.apk","com.sankuai.meituan.takeoutnew"});
		interAppPath.put("elme", new String[]{GlobalVariable.sdPath+"apk/elme_signed.apk","me.ele"});
		interAppPath.put("bigApp", new String[]{GlobalVariable.sdPath+"apk/game_400M_signed.apk","com.netease.ldxy.baidu"});
		mtThread.start();
		eleThread.start();
		baiduThread.start();
		aliThread.start();
		GDThread.start();
		gui.cls_show_msg1_record(TAG, "installapp7", gScreenTime,"已开启5个线程安装美团外卖、饿了吗、支付包、乱斗西游2、高德地图,过5-6分钟去应用桌面查看应存在这几个APP才可视为测试通过");
	}

	// 线程1安装美团外卖
	Thread mtThread = new Thread() {
		public void run() {
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName =interAppPath.get("meituan_high")[0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		};
	};
	// 线程2安装饿了么
	Thread eleThread = new Thread() {
		public void run() {
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName=interAppPath.get("elme")[0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		};
	};
	// 线程3安装大应用乱斗西游2
	Thread baiduThread = new Thread() {
		public void run() {
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName=interAppPath.get("bigApp")[0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		};
	};
	// 线程4安装支付宝
	Thread aliThread = new Thread() {
		public void run() {
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName=interAppPath.get("alipay")[0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		};
	};
	// 线程5安装高德地图
	Thread GDThread = new Thread() {
		public void run() {
			Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String expPackName=interAppPath.get("mapGD")[0];
			intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
			myactivity.startActivity(intent);
		};
	};

	@Override
	public void onTestUp() {
	
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
	
	
	
	
	
}
