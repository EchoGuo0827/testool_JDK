package com.example.highplattest.android;

import java.io.File;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.newland.SettingsManager;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android32.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 20190723
 * directory 		: DownloadManager权限测试
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 weimj		      20190723	 		created
 * 					变更记录			变更时间				变更人
 * 				修复单线程下载无法下载apk问题  20200706          	陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android32 extends UnitFragment{
	public final String FILE_NAME = Android32.class.getSimpleName();
	private final String TESTITEM = "DownloadManager下载测试";
	private Object lockObj = new Object();
	private long mReceiveId = 0;
	private int MAX_TIMEOUT = 10*60*1000;// 下载最大的超时时间为10min
	private DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
	private SettingsManager settingsManager;
	Gui gui = new Gui(myactivity, handler);
	private DownloadManager mDownloadManager;
	boolean flag1 =false;
	boolean flag2=false;
	
	// 单线程下载和多线程下载
	public void android32()
	{
		mDownloadManager = (DownloadManager) myactivity.getSystemService(Context.DOWNLOAD_SERVICE);//获取下载管理器服务的实例, 添加下载任务
		int nkeyIn = gui.cls_show_msg("%s\n1.单线程下载测试\n2.多线程下载测试", TESTITEM);
		switch (nkeyIn) {
		case '1':
			singleThread();
			break;
			
		case '2':
			mulThread();
			break;

		case ESC:
			unitEnd();
			return;
		}
	}
	
	public void singleThread()
	{
		String funcName = "singleThread";
		// 测试前置，开启下拉状态栏，启用downLoadManager,网络要是可用的
		gui.cls_show_msg("请确保wifi或移动网络其中一个是已连接状态,确保后任意键继续");
		if(gui.cls_show_msg("即将跳转到DownLoadManager界面,请确保DownLoadManager下载已启用,[确认键]开始跳转")==ENTER)
		{
		    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		    intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
		    myactivity.startActivity(intent);
		    gui.cls_show_msg("保证DownLoadManager已启用后点任意键继续");
		}

		
		// case1:进行apk文件下载操作应问问题
	
//		String downloadUrl = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk";
//		String downloadUrl = "http://gdown.baidu.com/data/wisegame/55dc62995fe9ba82/jinritoutiao_448.apk";// http 下载链接	
		String downloadUrl = "http://p.gdown.baidu.com/878f7a01e239b0551d7716a1c4e762c39c865e024cddeb6cefb09393a96d2bc26f6b5c6dfb1d60fd66d62fb8856aed69c828f2ecffded078bd53cf08ea2ae515565b66e443fbc7ca01f6894e2d89080942cdd2297462a0f960032355712233750ed349b1e3ef269f15d3d5bf417c188547496ae431e2aa8791108f73f60def6707c99cfd3f3df899365610c51364f20ad4af9f6ec6b5e3d1c8dc9051d450cb5c6ba6422d2b462ae59e1fd5fa87b193dac2d2f6d04a7a509c13809f8c37646b6b7844004b4dd83d0515a21ed3e3ff3bffb53dbbf7c64c7809bd40198de3e8a83bf877ca47ed7857efd8be1656606b1e9154cb235ca6648dfba30c7ac26c9527f74f786949117cb9df3bf57d9a2929b9db";
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));// 创建下载请求
		request.allowScanningByMediaScanner();// 设置可以被扫描到
	    request.setVisibleInDownloadsUi(true);// 设置下载可见
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//通知栏下载过程中和下载完成后均可见
		request.setTitle("case1测试");
		request.setDescription("今日头条正在下载...");
		File saveFile = new File(Environment.getExternalStorageDirectory()+"/test", "jinritoutiao.apk");// 设置下载文件的保存位置
		request.setDestinationUri(Uri.fromFile(saveFile));
		long downloadId = mDownloadManager.enqueue(request);// 将下载请求加入下载队列, 返回一个下载ID
		gui.cls_show_msg1(1, "正在下载apk文件,文件下载ID=%d,请耐心等待,可在状态栏查看下载进度...",downloadId);
		synchronized (lockObj) {// 使用同步锁，等待文件下载完成
			try {
				lockObj.wait(MAX_TIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(downloadId==mReceiveId)//下载完成
		{
			if(gui.cls_show_msg("今日头条的apk已下载完成，请去/sdcard/test/jinritoutiao.apk查看,一致[确认],不一致[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:%s测试失败,下载的文件存在问题", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:下载jinritoutiao的apk文件失败(downId=%d)", Tools.getLineInfo(),downloadId);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:进行图片文件的下载操作应无问题
		String downloadUrl_pic = "http://www.posvv.com/images/image/20180516131917421742.png";// http 下载链接	
		DownloadManager.Request request_pic = new DownloadManager.Request(Uri.parse(downloadUrl_pic));// 创建下载请求
		request_pic.allowScanningByMediaScanner();// 设置可以被扫描到
		request_pic.setVisibleInDownloadsUi(true);// 设置下载可见
		request_pic.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//通知栏下载过程中和下载完成后均可见
		request_pic.setTitle("case2测试");
		request_pic.setDescription("图片正在下载...");
		saveFile = new File(Environment.getExternalStorageDirectory()+"/test", "test.png");// 设置下载文件的保存位置
		request_pic.setDestinationUri(Uri.fromFile(saveFile));
		downloadId = mDownloadManager.enqueue(request_pic);// 将下载请求加入下载队列, 返回一个下载ID
		gui.cls_show_msg1(1, "正在下载png图片文件,文件下载ID=%d,请耐心等待,可在状态栏查看下载进度...",downloadId);
		synchronized (lockObj) {// 使用同步锁，等待文件下载完成
			try {
				lockObj.wait(MAX_TIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(downloadId==mReceiveId)//下载完成
		{
			if(gui.cls_show_msg("png图片已下载完成，请去/sdcard/test/test.png查看,一致[确认],不一致[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:%s测试失败,下载的文件存在问题", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:下载test.png文件失败(downId=%d)", Tools.getLineInfo(),downloadId);
			if(GlobalVariable.isContinue==false)
				return;
		}
			
		// case3:多次下载同一个文件命名为不同名字应下载成功
		request.setTitle("case3测试");
		request.setDescription("今日头条正在下载...");
		saveFile = new File(Environment.getExternalStorageDirectory()+"/test", "copyJin.apk");// 设置下载文件的保存位置
		request.setDestinationUri(Uri.fromFile(saveFile));
		downloadId = mDownloadManager.enqueue(request);// 将下载请求加入下载队列, 返回一个下载ID
		gui.cls_show_msg1(1, "正在下载apk文件,文件下载ID=%d,请耐心等待,可在状态栏查看下载进度...",downloadId);
		synchronized (lockObj) {// 使用同步锁，等待文件下载完成
			try {
				lockObj.wait(MAX_TIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(downloadId==mReceiveId)//下载完成
		{
			if(gui.cls_show_msg("今日头条的apk已下载完成，请去/sdcard/test/copyJin.apk查看,一致[确认],不一致[其他]")!=ENTER)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:%s测试失败,下载的文件存在问题", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:下载jinritoutiao的apk文件失败(downId=%d)", Tools.getLineInfo(),downloadId);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(FILE_NAME, funcName,gScreenTime, "单线程下载测试通过");
	}
	
	private void mulThread()
	{
		final String funcName = "singleThread";
		new Thread(){
			public void run() 
			{
				String url_1 = "http://gdown.baidu.com/data/wisegame/490d7edff73935cf/tengxunxinwen_5844.apk";
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_1));
				request.allowScanningByMediaScanner();// 设置可以被扫描到
			    request.setVisibleInDownloadsUi(true);// 设置下载可见
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//通知栏下载过程中和下载完成后均可见
				request.setTitle("线程1下载测试");
				request.setDescription("腾讯新闻正在下载...");
				File saveFile = new File(Environment.getExternalStorageDirectory()+"/test", "tengxunxinwen.apk");// 设置下载文件的保存位置
				request.setDestinationUri(Uri.fromFile(saveFile));
				long downloadId = mDownloadManager.enqueue(request);// 将下载请求加入下载队列, 返回一个下载ID
				gui.cls_show_msg1(1, "正在下载腾讯新闻的apk文件,文件下载ID=%d,请耐心等待,可在状态栏查看下载进度...",downloadId);
				synchronized (lockObj) {// 使用同步锁，等待文件下载完成
					try {
						lockObj.wait(MAX_TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(downloadId==mReceiveId)//下载完成
				{	
					flag1=true;
					if(gui.cls_show_msg("腾讯新闻的apk已下载完成，请去/sdcard/test/tengxunxinwen.apk查看,一致[确认],不一致[其他]")!=ENTER)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:%s测试失败,下载的文件存在问题", Tools.getLineInfo(),TESTITEM);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}
				else
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:下载腾讯新闻的apk文件失败(downId=%d)", Tools.getLineInfo(),downloadId);
					if(GlobalVariable.isContinue==false)
						return;
				}
			};
		}.start();
		
		new Thread(){
			public void run() 
			{
				String url_2 = "http://gdown.baidu.com/data/wisegame/ab30ed59c5f341f4/baidushoujizhushou_16797445.apk";
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_2));
				request.allowScanningByMediaScanner();
			    request.setVisibleInDownloadsUi(true);
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//通知栏下载过程中和下载完成后均可见
				request.setTitle("线程2下载测试");
				request.setDescription("百度手机助手正在下载...");
				File saveFile = new File(Environment.getExternalStorageDirectory()+"/test", "baidushoujizhushou.apk");// 设置下载文件的保存位置
				request.setDestinationUri(Uri.fromFile(saveFile));
				long downloadId = mDownloadManager.enqueue(request);// 将下载请求加入下载队列, 返回一个下载ID
				gui.cls_show_msg1(1, "正在下载百度手机助手的apk文件,文件下载ID=%d,请耐心等待,可在状态栏查看下载进度...",downloadId);
				synchronized (lockObj) {// 使用同步锁，等待文件下载完成
					try {
						lockObj.wait(MAX_TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(downloadId==mReceiveId)//下载完成
				{
					flag2=true;
					if(gui.cls_show_msg("百度手机助手的apk已下载完成，请去/sdcard/test/baidushoujizhushou.apk查看,一致[确认],不一致[其他]")!=ENTER)
					{
						gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:%s测试失败,下载的文件存在问题", Tools.getLineInfo(),TESTITEM);
						if(GlobalVariable.isContinue==false)
							return;
					}
				}
				else
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:下载百度手机助手的apk文件失败(downId=%d)", Tools.getLineInfo(),downloadId);
					if(GlobalVariable.isContinue==false)
						return;
				}
			};
		}.start();
		while (true) {
			  if (flag1&&flag2) {
				  break;
			}
			  SystemClock.sleep(100);
			
		}
		gui.cls_printf("多线程下载腾讯新闻和百度助手的apk,耐心等待,两个apk均下载完成才可视为测试通过,apk存放与/sdcard/test/目录下".getBytes());
	}
	
	class DownloadCompleteReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
			{
				mReceiveId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				LoggerUtil.v("downLoad id="+mReceiveId);
				synchronized (lockObj) {
					lockObj.notify();
				}
			}
		}
	}
	
	
	@Override
	public void onTestUp() {
		
		myactivity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		// 测试前置,打开状态栏
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		settingsManager.setStatusBarEnabled(0);
		String[] statusMenu1 = {"wifi","bt","cell","notifications","setting"};
		settingsManager.setStatusBarQsTiles(statusMenu1);
		
	}

	@Override
	public void onTestDown() {
		if(receiver!=null)
			myactivity.unregisterReceiver(receiver);
		settingsManager.setStatusBarEnabled(1);
		// 删除sdcard/test目录下的所有测试文件
		new Thread()
		{
			public void run() 
			{
				File file = new File("/sdcard/test");
				File[] testFiles = file.listFiles();
				for (File test:testFiles) {
					test.delete();
				}
			};
		}.start();
	}

}
