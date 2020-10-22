package com.example.highplattest.ums;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.newland.content.NlIntent;
import android.newland.scan.ScanUtil;
import android.newland.ums.UmsApi;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums7.java 
 * history 		 	: 变更点								变更时间				变更人员
 * 					 运行关闭应用closeApplication	   	    20200818	 		郑佳雯
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums7 extends UnitFragment
{
	private final String TESTITEM = "closeApplication(银商)";
	public final String FILE_NAME = Ums7.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums7()
	{
		try {
			while(true)
			{
				int nkeyIn = gui.cls_show_msg("%s\n测试前置：请保证测试时，已安装SVN/testool_JDK/others/test_apk/银商测试apk的应用 \n0.单元参数异常测试\n1.正常测试\n2并发关闭应用", TESTITEM);
				switch (nkeyIn) {
					
				case '0':
					unitParaTest();
					break;
					
				case '1':
					unitTest();
					break;
					
				case '2':
					closeTest();
					break;
					
				case ESC:
					unitEnd();
					return;

				default:
					break;
				}
			}
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums7", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
	}
	
	public void unitParaTest()
	{
		boolean iRet=false;
		String funcName = "unitParaTest";
	
		//参数异常测试
		// case2.1:参数异常测试：packageName=NULL,预期返回false
		gui.cls_show_msg1(1, "case2.1:参数异常测试：packageName=NULL");
		if((iRet = umsApi.closeApplication(null))!=false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg( "case2.1:参数异常测试通过,任意键继续...");
		
		// case2.2:参数异常测试：packageName="",预期返回false
		gui.cls_show_msg1(1, "case2.2:参数异常测试：packageName=空");
		if((iRet = umsApi.closeApplication(""))!=false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg( "case2.2:参数异常测试通过,任意键继续...");
		
		// case2.3:参数异常测试:参数异常测试:packageName=错误的包名,预期返回false
		gui.cls_show_msg1(1, "case2.3:参数异常测试：packageName=错误包名");
		if((iRet = umsApi.closeApplication("no.exist.com"))!=false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg( "case2.3:参数异常测试通过,任意键继续...");
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	public void unitTest()
	{
		boolean iRet=false;
		String funcName = "unitTest";
		
		// case3.1:正常测试：打开系统应用，返回false，可正常自检关闭
		gui.cls_show_msg( "case3.1:请打开系统应用【自检应用】,完成按任意键继续...");
		
		if((iRet = umsApi.closeApplication("com.newland.detectapp"))!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg1(2, "case3.1:测试通过");
	
		// case3.2:正常测试：打开mtms应用，返回false，可正常关闭mtms应用
		gui.cls_show_msg( "case3.2:请打开mtms应用,完成按任意键继续...");
		if((iRet = umsApi.closeApplication("com.eric.mtms_test"))!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg1(2, "case3.2:测试通过");
		
		// case3.3:正常测试：打开预装应用，返回false，可正常关闭预装应用
		gui.cls_show_msg( "case3.3:请打开【Settings】,完成按任意键继续...");
		if((iRet = umsApi.closeApplication("com.android.settings"))!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg1(2, "case3.3:测试通过");
		
		// case3.4:正常测试：打开第三方应用，返回true，可正常关闭第三方应用
		gui.cls_show_msg("case3.4:请打开第三方应用【百度地图】,完成按任意键继续...");
		if((iRet = umsApi.closeApplication("com.baidu.BaiduMap"))!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.4测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg1(2, "case3.4:测试通过");
		
		// case4.1:卸载存在的应用，然后再关闭该应用，返回false
		gui.cls_show_msg1(1, "case4.1:卸载存在的应用，然后再关闭该应用");
		gui.cls_show_msg("请卸载【百度地图】,卸载完毕按任意键继续");
		if((iRet = umsApi.closeApplication("com.baidu.BaiduMap"))!=false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		else 
			gui.cls_show_msg1(2, "case4.1:测试通过");
		
		// case3.5:关闭正在运行的前台应用，返回true，可正常关闭正在运行的应用
		if(gui.cls_show_msg( "case3.5:检测是否可正常关闭正在运行的前台应用,关闭即为测试通过,现在关闭[确认],不关闭[其他]")==ENTER)
		{
			if((iRet = umsApi.closeApplication("com.example.highplattest"))!=true)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.5测试失败(ret=%s)", Tools.getLineInfo(),iRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			else
				gui.cls_show_msg1(2, "case3.5:测试通过");
		}
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	public void closeTest()
	{	
		boolean iRet=false;
		String funcName = "closeTest";
		// case5.1:连续打开10应用后，并发关闭所有应用，可正常关闭所有应用，无任何异常，可在多任务键栏查看
		gui.cls_show_msg("case5.1:请打开【自检应用】【QQ浏览器】【图库】【时钟】【美团】【支付宝】【饿了么】【settings】【百度地图】【高德地图】,在多任务键栏查看是否已打开10个应用，按任意键继续...");
		zjThread.start();
		qqThread.start();
		xcThread.start();
		shzhThread.start();
		mtThread.start();
		aliThread.start();
		eleThread.start();
		amThread.start();
		seThread.start();
		bdmThread.start();
		
		if(gui.cls_show_msg( "case5.1:在多任务键栏查看是否已关闭打开的10个应用,关闭即为测试通过,关闭[确认],未关闭[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	// 线程1关闭【自检应用】
	Thread zjThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.newland.detectapp");
		};
	};
	// 线程2关闭【QQ浏览器】
	Thread qqThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.tencent.mtt");
		};
	};
	// 线程3关闭【相册】
	Thread xcThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.android.gallery3d");
		};
	};
	// 线程4关闭【时钟】
	Thread shzhThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.android.deskclock");
		};
	};
	// 线程5关闭【美团】
	Thread mtThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.sankuai.meituan.takeoutnew");
		};
	};
	// 线程6关闭【支付宝】
	Thread aliThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.eg.android.AlipayGphone");
		};
	};
	// 线程7关闭【饿了么】
	Thread eleThread = new Thread() {
		public void run() {
			umsApi.closeApplication("me.ele");
		};
	};
	// 线程8关闭【高德地图】
	Thread amThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.autonavi.minimap.custom");
		};
	};
	// 线程9关闭【settings】
	Thread seThread = new Thread() {
		public void run() {
			umsApi.closeApplication("com.android.settings");
		};
	};
	// 线程10关闭【百度地图】
	Thread bdmThread = new Thread() {
		public void run() {			
			umsApi.closeApplication("com.baidu.BaiduMap");			
		};
	};
	

	@Override
	public void onTestUp() {
		umsApi = new UmsApi(myactivity);
	}

	@Override
	public void onTestDown() {
	}

}
