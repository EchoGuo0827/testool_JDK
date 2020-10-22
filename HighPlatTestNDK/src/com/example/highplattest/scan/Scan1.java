package com.example.highplattest.scan;

import java.util.Arrays;
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
 * file name 		: Scan1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150725 
 * directory 		:  可设置扫码模式、扫码超时时间、对焦灯模式，蜂鸣器开关，本用例默认前置
 * description 		: init(int scanMode, int scanTime, int scanFocusMode,boolean soundSwitcher)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150725     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ******************
 *******************************************************/
@SuppressLint("NewApi")
public class Scan1 extends UnitFragment
{
	private final String TESTITEM = "(ScanUtil+硬)init(scanMode,scanTime,scanFocusMode,soundSwitcher)";
	final int MAXTAPTIME = 200;
	private int MINTIME = 1000;
	private int MAXTIME = 25400;
	private ScanUtil scanUtil;
	private Gui gui;
	private int MAXWAITTIME=15*1000;
	/**
	 * 扫码结果存储
	 */
	String resultCode;
	String[] values;
	
	private final String tipMsg = "请将条形码或二维码放在前置摄像头20-30处,[取消]退出,[其他]完成";
	private String fileName=Scan1.class.getSimpleName();
	public void scan1() 
	{
		/*private & local definition*/
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan1", gScreenTime,"%s用例不支持自动化测试,请手动验证",  TESTITEM);
			return;
		}
		// 获取扫描的工具包
		int ret = NDK_ERR;
		long startTime;
		long time;
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		// 设置为永不休眠
		gui.cls_show_msg("测试过程中机具不应进入休眠,请先将休眠时间设大,完成任意键继续");
		
		try 
		{
			// case1:实例化scanUtil,参数错误会设置为默认的情况
			scanUtil = new ScanUtil(myactivity);
			if((ret = scanUtil.init(-1, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// 默认为单次扫码
			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil, "", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
			
			if((ret = scanUtil.init(ScanUtil.MODE_ONCE, 0, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
					
			}
			// 默认为单次扫码
			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
				
			ret = scanUtilDialog(scanUtil, "", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			if((ret = scanUtil.init(ScanUtil.MODE_ONCE, MAXWAITTIME, -1, true))!= ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// 默认为单次扫码
			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil,  "", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// case2:单次扫码操作，初始化为识读时闪，扫码的时候对焦灯会亮
			if((ret = scanUtil.init(ScanUtil.MODE_ONCE,MAXWAITTIME,ScanUtil.FOCUS_READING, true))!=ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil,  "对焦灯是否在识读时闪烁,", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// 初始化关闭对焦灯，扫码时对焦灯常灭
			if ((ret = scanUtil.init(ScanUtil.MODE_ONCE,MAXWAITTIME, ScanUtil.FOCUS_OFF, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil,  "对焦灯是否常灭，", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// 初始化单次扫码，对焦灯常亮
			if ((ret = scanUtil.init(ScanUtil.MODE_ONCE,MAXWAITTIME, ScanUtil.FOCUS_ON, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil,  "对焦灯是否常亮,", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// 设置单次扫码，识读时闪，蜂鸣器不响
			if ((ret = scanUtil.init(ScanUtil.MODE_ONCE,MAXWAITTIME, ScanUtil.FOCUS_READING, false)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("单次扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil, "蜂鸣器是否不会响,", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			//case3: 测试超时时间，超时时间范围1000ms-25400ms，超时应返回F
			if(gui.cls_show_msg("超时测试,最小超时时间1s:请将条形码或二维码移开,[取消]退出测试")==ESC)
				return;
			// 超时时间设置为最小值1S
			if ((ret = scanUtil.init(ScanUtil.MODE_ONCE,MINTIME, ScanUtil.FOCUS_READING, false)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			startTime = System.currentTimeMillis();
			resultCode = (String) scanUtil.doScan();
			time = System.currentTimeMillis()-startTime;
			if(resultCode==null||resultCode.startsWith("S")||time>MINTIME+MAXTAPTIME||time<MINTIME-MAXTAPTIME)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码超时时间错误%s,time="+time, Tools.getLineInfo(),TESTITEM,resultCode==null?"null":resultCode);
				if (!GlobalVariable.isContinue)
					return;
					
			}
			releaseScan(scanUtil);
			// 超时时间设置为25400
			if(gui.cls_show_msg("超时时间测试,最大超时时间25.4s,退出键退出测试,[取消]退出,[其他]继续")==ESC)
				return;
			if ((ret = scanUtil.init(ScanUtil.MODE_ONCE,MAXTIME, ScanUtil.FOCUS_READING, false)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			startTime = System.currentTimeMillis();
			resultCode = (String) scanUtil.doScan();
			time = System.currentTimeMillis()-startTime;
			if(resultCode==null||resultCode.startsWith("S")||time>MAXTIME+MAXTAPTIME*10||time<MAXTIME-MAXTAPTIME*10)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s超时时间错误%s,time="+time, Tools.getLineInfo(),TESTITEM,resultCode==null?"null":resultCode);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// case4:连续扫码，初始化为识读闪
			if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY,MAXWAITTIME, ScanUtil.FOCUS_READING, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("连续扫码,扫码次数10次:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			values = (String[]) scanUtil.doScan();
			for (int i = 0; i < values.length; i++) 
			{
				if(values[i]==null||values[i].startsWith("F")) 
				{
					gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s第%d次扫码失败(%s)", Tools.getLineInfo(),TESTITEM,i+1,values[i]==null? "null":values[i]);
					if (!GlobalVariable.isContinue)
						return;
				}
				else
				{
					// 获取扫码结果
					values[i] = values[i].substring(1);
				}
			}
			
			if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致,并且对焦灯在识读时闪烁").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码失败", Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// 初始化连续扫码，对焦灯不亮
			if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY,MAXWAITTIME, ScanUtil.FOCUS_OFF, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			gui.cls_show_msg("连续扫码,扫码次数10次："+tipMsg+",任意键继续");
			values = (String[]) scanUtil.doScan();
			for (int i = 0; i < values.length; i++) 
			{
				if(values[i]==null||values[i].startsWith("F")) 
				{
					gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s第%d次扫码失败(%s)", Tools.getLineInfo(),TESTITEM,i+1,values[i]==null? "null":values[i]);
					if (!GlobalVariable.isContinue)
						return;
				}
				else
				{
					values[i] = values[i].substring(1);
				}
			}

			
			if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致,并且常灭").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码错误", Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// 初始化连续扫码，对焦灯常亮
			if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY,MAXWAITTIME, ScanUtil.FOCUS_ON, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("连续扫码，扫码次数10次"+tipMsg)==ESC)
				return;
			values = (String[]) scanUtil.doScan();
			
			for (int i = 0; i < values.length; i++) 
			{
				if(values[i]==null||values[i].startsWith("F")) 
				{
					gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s第%d次扫码超时(%s)", Tools.getLineInfo(),TESTITEM,i+1,values[i]==null?"null":values[i]);
					if (!GlobalVariable.isContinue)
						return;
				}
				else
				{
					values[i] = values[i].substring(1);
				}
			}

			if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致,并且常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码错误", Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// 初始化连续扫码，对焦灯常亮
			if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY,MAXWAITTIME, ScanUtil.FOCUS_ON, false)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("连续扫码,扫码次数10次:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			values = (String[]) scanUtil.doScan();
			for (int i = 0; i < values.length; i++) 
			{
				if(values[i]==null||values[i].startsWith("F")) 
				{
					gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s第%d次扫码失败(%s)", Tools.getLineInfo(),TESTITEM,i+1,values[i]==null?"null":values[i]);
					if (!GlobalVariable.isContinue)
						return;
				}
				else
				{
					values[i] = values[i].substring(1);
				}
			}

			if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致,并且蜂鸣器不会响").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫描错误", Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			
			// case5:连续扫码的超时时间
			/*if(gui.cls_show_msg("超时测试，超时时间1s，共10次，请耐心等待，退出键退出测试")==true)
				return;*/
			if(gui.cls_show_msg("请将条形码或二维码移开,[取消]退出,[其他]完成")==ESC)
				return;
			// 超时时间设置为最小值1S
			if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MINTIME,ScanUtil.FOCUS_READING, false)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 设置连续扫码间距
			scanUtil.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, 10);
			startTime = System.currentTimeMillis();
			values = (String[]) scanUtil.doScan();
			time = System.currentTimeMillis() - startTime;
			for (int i = 0; i < values.length; i++) 
			{
				if(values[i]==null||values[i].startsWith("S"))
				{
					gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr, "line %d:第%d次扫码结果错误(%s)", Tools.getLineInfo(), i,values[i]==null?"null":values[i]);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if (time > MINTIME*19 + MAXTAPTIME*19| time < MINTIME*19 - MAXTAPTIME*19) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr, "line %d:%s超时时间错误" + time, Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			// 超时时间设置为25400
			if(gui.cls_show_msg("超时测试,超时时间25.4S,共10次,请耐心等待,[取消]退出,[其他]继续")==ESC)
				return;
			if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MAXTIME,ScanUtil.FOCUS_READING, false)) != ScanUtil.SUCCESS) {
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			startTime = System.currentTimeMillis();
			values = (String[]) scanUtil.doScan();
			time = System.currentTimeMillis() - startTime;
			for (int i = 0; i < values.length; i++) 
			{
				if(values[i]==null||values[i].startsWith("S"))
				{
					gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr, "line %d:第%d次扫码结果错误(%s)", Tools.getLineInfo(), i,values[i]==null?"null":values[i]);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if (time > MAXTIME*10+9000 + MAXTAPTIME * 19| time < MAXTIME*10+9000 - MAXTAPTIME * 19) {
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr, "line %d:%s超时时间错误" + time, Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue) 
					return;
			}
			releaseScan(scanUtil);
			
			// case6:手动扫码，识读时闪，手动扫码没有"S"和"F"之分
			if((ret = scanUtil.init(ScanUtil.MODE_MANUALLY,MAXWAITTIME,ScanUtil.FOCUS_READING, true))!=ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// 进行扫码操作
			if(gui.cls_show_msg("手动扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil,"对焦灯是否在识读时闪烁", handler);
			if(ret !=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码错误(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			// 初始化手动扫码，对焦灯常灭
			if ((ret = scanUtil.init(ScanUtil.MODE_MANUALLY,MAXWAITTIME, ScanUtil.FOCUS_OFF, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("手动扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil, "对焦灯是否常灭", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码错误(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			
			releaseScan(scanUtil);
			// 初始化手动扫码，对焦灯常亮
			if ((ret = scanUtil.init(ScanUtil.MODE_MANUALLY,MAXWAITTIME, ScanUtil.FOCUS_ON, true)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("手动扫码:%s,[取消[退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil, "对焦灯是否常亮", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码错误(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan(scanUtil);
			// 初始化手动扫码，识读时闪
			if ((ret = scanUtil.init(ScanUtil.MODE_MANUALLY,MAXWAITTIME, ScanUtil.FOCUS_READING, false)) != ScanUtil.SUCCESS) 
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 进行扫码操作
			if(gui.cls_show_msg("手动扫码:%s,[取消]退出测试",tipMsg)==ESC)
				return;
			ret = scanUtilDialog(scanUtil,  "蜂鸣器是否不会响", handler);
			if(ret!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s扫码错误(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName, "scan1", gScreenTime,"%s测试通过", TESTITEM);
		} catch (NoClassDefFoundError e) 
		{
			e.getMessage();
			gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"%s不支持该用例",GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			e.getMessage();
			gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"%s不支持该用例",GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			e.getMessage();
			gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	
	@Override
	public void onTestUp() 
	{
	}

	@Override
	public void onTestDown() 
	{
		releaseScan(scanUtil);
		gui = null;
		scanUtil = null;
	}

}
