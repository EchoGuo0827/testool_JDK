package com.example.highplattest.scan;

import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150725 
 * directory 		: 释放扫码,即中断扫码操作
 * description 		: relese()
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150725     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan5 extends UnitFragment
{
	private String fileName=Scan5.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+硬)release";
	String[] values;
	ScanUtil scanUtil = null;
//	boolean threadFlag = false;
	private int MAXWAITTIME = 5*1000;
	Gui gui = new Gui(myactivity, handler);
	@SuppressLint("NewApi")
	public void scan5() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		// 获取扫描的工具包
		int ret = NDK_ERR;
		ScanThread scanThread = new ScanThread();
		
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		try {
			scanUtil = new ScanUtil(myactivity);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "该用例不支持");
			return;
		}
		// 测试前置,先释放
		releaseScan(scanUtil);
		SystemClock.sleep(1000);
		// case1:单次扫码释放,扫码中释放以及扫码超时后释放都应能释放成功
		// case1.1:单次扫码释放,扫码中释放重新初始化应能扫码成功
		/*if(gui.cls_show_msg(0, "单次扫码释放测试,退出键退出测试")==true)
			return;*/
		if((ret = scanUtil.init(ScanUtil.MODE_ONCE, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 另外一个线程
		scanThread.start();
		try 
		{
			scanThread.join(5*1000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		scanThread.interrupt();
		scanThread = null;
		releaseScan(scanUtil);
		SystemClock.sleep(1000);
		// 释放完成后再进行扫码应失败
		if(gui.cls_show_msg("单次扫码中释放:请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtilDialog(scanUtil, "", handler))!=NDK_SCAN_COTINUE_NULL)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1.2:重新初始化应扫码成功,扫码结束后释放应扫码失败
		if((ret = scanUtil.init(ScanUtil.MODE_ONCE, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("单次扫码:请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtilDialog(scanUtil, "" ,handler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan(scanUtil);
		if(gui.cls_show_msg("单次扫码后释放:请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtilDialog(scanUtil, "", handler))!=NDK_SCAN_COTINUE_NULL)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:连续扫码释放,扫码中释放以及扫码超时后释放都应能释放成功
		// case2.1:连续扫码中释放,不应出现不能扫码的情况
		/*if(gui.cls_show_msg(0, "连续扫码释放测试,退出键退出测试")==true)
			return;*/
		if(gui.cls_show_msg("请将条形码或二维码移开,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(scanThread==null)
		{
			scanThread = new ScanThread();
			scanThread.start();
		}
		try 
		{
			scanThread.join(5*1000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		scanThread.interrupt();
		scanThread = null;
		releaseScan(scanUtil);
		SystemClock.sleep(1000);
		String[] values1 = (String[]) scanUtil.doScan();
		if(values1!=null)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s连续扫码释放失败(%s)", Tools.getLineInfo(),TESTITEM,values1);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 重新初始化扫码成功
		if((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("连续扫码：请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		String[] values2 = (String[]) scanUtil.doScan();
		
		for (int i = 0; i < values2.length; i++) 
		{
			if(values2[i].startsWith("F"))
			{
				gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码释放失败(%s)", Tools.getLineInfo(),TESTITEM,values2[i]==null?"null":values2[i]);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		releaseScan(scanUtil);
		String[] values3 = (String[]) scanUtil.doScan();
		if(values3!=null)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s连续扫码释放失败(%s)", Tools.getLineInfo(),TESTITEM,values3);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:手动扫码释放,扫码中释放以及扫码超时后释放都应能释放成功
		// case3.1:手动扫码释放,扫码中释放重新初始化应能扫码成功
		/*if(gui.cls_show_msg(0, "手动扫码释放测试,退出键退出测试")==true)
			return;*/
		if(gui.cls_show_msg("请将条形码或二维码移开,[取消]退出,[其他]完成")==ESC)
			return;
		releaseScan(scanUtil);
		if((ret = scanUtil.init(ScanUtil.MODE_MANUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 另外开一个线程
		if(scanThread==null)
		{
			scanThread = new ScanThread();
			scanThread.start();
		}
		try 
		{
			scanThread.join(5*1000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		scanThread.interrupt();
		scanThread = null;
		
		releaseScan(scanUtil);
		SystemClock.sleep(1000);
		if(gui.cls_show_msg("手动扫码中释放：请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtilDialog(scanUtil, "", handler))!=NDK_SCAN_COTINUE_NULL)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:手动扫码释放,扫码结束后释放
		if(gui.cls_show_msg("请将条形码或二维码移开,[取消]退出,[其他]完成")==ESC)
			return;
		releaseScan(scanUtil);
		if((ret = scanUtil.init(ScanUtil.MODE_MANUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr, "line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("手动扫码：请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtilDialog(scanUtil, "", handler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan(scanUtil);
		SystemClock.sleep(1000);
		if(gui.cls_show_msg("手动扫码：请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
			return;
		if((ret = scanUtilDialog(scanUtil, "", handler))!=NDK_SCAN_COTINUE_NULL)
		{
			gui.cls_show_msg1_record(fileName, "scan5", gKeepTimeErr,"line %d:%s扫码失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "scan5", gScreenTime,"%s测试通过", TESTITEM);
	}
	
	// 扫码线程
	class ScanThread extends Thread
	{
		public void run() 
		{
			try 
			{
				scanUtil.doScan();
				Thread.sleep(100);
			} catch (InterruptedException e) 
			{
				Log.d("scanThread", "interrupt");
			}
		};
	};
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(scanUtil!=null)
			scanUtil.relese();
		gui = null;
		scanUtil = null;
		
	}
}
