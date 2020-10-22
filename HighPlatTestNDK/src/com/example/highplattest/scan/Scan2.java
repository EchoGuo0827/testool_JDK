package com.example.highplattest.scan;

import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150725 
 * directory 		: 初始化解码器,默认超时时间为3s
 * description 		: init()
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150725     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan2 extends UnitFragment
{
	
	private String fileName=Scan2.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+硬)init";
	private int MAXTAPTIME = 200;
	private int MAXWAITTIME = 3*1000;
	private ScanUtil scanUtil;
	private String resultCode;
	private Gui gui;
	
	public void scan2() 
	{
		/*private & local definition*/
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag== ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		// 获取扫描的工具包
		int ret = NDK_ERR;
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		try 
		{
			// case1:进行扫码操作
			scanUtil = new ScanUtil(myactivity);
			if((ret = scanUtil.init())!=ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成")==ESC)
				return;
			if((ret = scanUtilDialog(scanUtil, "单次扫码对焦灯在读的时候亮,蜂鸣器会响,", handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr,"line %d:%s扫码失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// case2:测试默认的3S的超时时间
			if(gui.cls_show_msg("请将条形码或二维码移开,[取消]退出,[其他]完成")==ESC)
				return;
			scanUtil.init();
			long startTime = System.currentTimeMillis();
			resultCode = (String) scanUtil.doScan();
			long time = System.currentTimeMillis() - startTime;
			if(resultCode.startsWith("F")==false|time>MAXWAITTIME+MAXTAPTIME|time<MAXWAITTIME-MAXTAPTIME)
			{
				gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr,"line %d:%s超时时间错误%s,"+time, Tools.getLineInfo(),TESTITEM,resultCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg1_record(fileName, "scan2", gScreenTime,"%s测试通过", TESTITEM);

		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan2", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan(scanUtil);
		gui = null;
		scanUtil = null;
		
	}
}
