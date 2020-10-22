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
 * file name 		: Scan3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150725 
 * directory 		: 设置连续扫码的时间间隔与扫码次数
 * description 		: setModeContinuous(int scanGapTime, int scanCount)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150725     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan3 extends UnitFragment
{
	private String fileName=Scan3.class.getSimpleName();	
	private final String TESTITEM = "(ScanUtil+硬)setModeContinuous";
	private int MAXWAITTIME = 10*1000;
	private final int TAPTIME = 200;
	private String tipMsg = "请将条形码或二维码放在前置摄像头20-30处";
	private ScanUtil scanUtil;
	long startTime;
	float time;
	Gui gui = new Gui(myactivity, handler);
	public void scan3() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		// 获取扫描的工具包
		int ret = NDK_ERR;
		String[] values;
		
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		gui.cls_show_msg("测试过程中机具不应进入休眠,请先将休眠时间设大,点[确认]继续");
		
		// case1:参数异常,因开发那边有参数异常处理机制,会被设置为默认参数
		try 
		{
			scanUtil = new ScanUtil(myactivity);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1(2, "该用例不支持");
			return;
		}
		if((ret = scanUtil.setModeContinuous(-1, ScanUtil.CONTINUOUS_COUNT))!=ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s参数错误(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		if((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!=ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		gui.cls_show_msg("连续扫码,扫码10次:"+tipMsg+",任意键继续");
		values =  (String[]) scanUtil.doScan();
		for (int i = 0; i < values.length; i++) 
		{
			if(values[i].startsWith("F"))
			{
				gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s第%d次扫码失败(%s)", Tools.getLineInfo(),TESTITEM,i,values[i]==null?"null":values[i]);
				if(!GlobalVariable.isContinue)
				{
					return;
				}
			}
			else
			{
				values[i] = values[i].substring(1);
			}
		}
		

		if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致？").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s扫描错误", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		//参数异常,因开发那边有参数异常处理机制,会被设置为默认
		releaseScan(scanUtil);
		scanUtil = new ScanUtil(myactivity);
		if ((ret = scanUtil.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, -1)) != ScanUtil.SUCCESS) 
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s参数错误(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}

		if ((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY,MAXWAITTIME, ScanUtil.FOCUS_READING, true)) != ScanUtil.SUCCESS) 
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}

		gui.cls_show_msg("连续扫码,扫码10次:"+tipMsg+",点任意键继续");
		values = (String[]) scanUtil.doScan();
		for (int i = 0; i < values.length; i++) 
		{
			if (values[i].startsWith("F")) 
			{
				gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s第%d次扫码超时(%s)", Tools.getLineInfo(), TESTITEM,i,values[i]==null?"null":values[i]);
				if (!GlobalVariable.isContinue)
				{
					return;
				}
			}
			else
			{
				values[i] = values[i].substring(1);
			}
		}

		if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致？").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s扫描错误", Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		releaseScan(scanUtil);
		// case2:实例化调用,初始化为单次,应不起任何效果
		if((ret = scanUtil.init(ScanUtil.MODE_ONCE, MAXWAITTIME, ScanUtil.FOCUS_ON, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		if((ret = scanUtil.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		gui.cls_show_msg("请将条形码或二维码移开,点任意键继续");
		startTime = System.currentTimeMillis();
		scanUtil.doScan();
		time = System.currentTimeMillis() - startTime;
		
		if(time>MAXWAITTIME+TAPTIME||time<MAXWAITTIME-TAPTIME)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(),TESTITEM,time);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		// case3:时间间隔设置为默认,默认为1s
		if(gui.cls_show_msg1(1, "连续扫码时间间隔1s测试,共10次,请移开条码,请耐心等待,[取消]可退出测试")==ESC)
			return;
		if((ret = scanUtil.setModeContinuous(ScanUtil.MODE_CONTINUALLY, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s初始化失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		startTime = System.currentTimeMillis();
		values =  (String[]) scanUtil.doScan();
		time = System.currentTimeMillis() - startTime;
		
		if (time > MAXWAITTIME*10+9*1000 + TAPTIME*19| time < MAXWAITTIME*10+9*1000 - TAPTIME*19) 
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(), TESTITEM,time);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		// case4:一分钟扫一次码,会被设置为默认
		if(gui.cls_show_msg1(1, "连续扫码间隔异常测试,共10次,请移开条码,请耐心等待,[取消]可退出测试")==ESC)
			return;
		if((ret = scanUtil.setModeContinuous(60*1000, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		startTime = System.currentTimeMillis();
		values =  (String[]) scanUtil.doScan();
		time = System.currentTimeMillis() -startTime;
		
		if(time>MAXWAITTIME*10+9*1000+TAPTIME*19|time<MAXWAITTIME*10+9*1000-TAPTIME*19)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(), TESTITEM,time);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		// case5:时间间隔设置为最小精度100ms
		if(gui.cls_show_msg1(1, "连续扫码时间间隔100ms,移开条码,耐心等待,[取消]可退出测试")==ESC)
			return;
		if((ret = scanUtil.setModeContinuous(100, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr, "line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		startTime = System.currentTimeMillis();
		values =  (String[]) scanUtil.doScan();
		time = System.currentTimeMillis() -startTime;
		
		if(time>MAXWAITTIME*10+1000+TAPTIME*10|time<MAXWAITTIME*10+1000-TAPTIME*10)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(), TESTITEM,time);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		// case6:测试时间小于最小精度0.1s,设置为0.01s,应被设置为默认值1s
		if(gui.cls_show_msg1(1, "连续扫码间隔小于最小值边界测试,共10次,请移开条码,耐心等待,[取消]可退出测试")==ESC)
			return;
		if((ret = scanUtil.setModeContinuous(10, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		startTime = System.currentTimeMillis();
		values =  (String[]) scanUtil.doScan();
		time = System.currentTimeMillis() -startTime;
		
		if(time>MAXWAITTIME*10+1000*10+TAPTIME*19|time<MAXWAITTIME*10+1000*10-TAPTIME*19)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(), TESTITEM,time);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		// case7:时间间隔设置为边界值25500,不应出现异常
		if(gui.cls_show_msg1(1, "连续扫码间隔大于最大边界值测试,共10次,请移开条码,耐心等待,[取消]可退出测试")==ESC)
			return;
		if((ret = scanUtil.setModeContinuous(25500, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				return;
			}
		}
		startTime = System.currentTimeMillis();
		values =  (String[]) scanUtil.doScan();
		time = System.currentTimeMillis() -startTime;
		
		if(time>MAXWAITTIME*10+25500*9+TAPTIME*19|time<MAXWAITTIME*10+25500*9-TAPTIME*19)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(), TESTITEM,time);
			if (!GlobalVariable.isContinue)
			{
				return;
			}
		}
		
		// case8:扫码次数应该与设置的扫码次数一致
		if(gui.cls_show_msg1(1, "连续扫码次数测试,[取消]退出测试")==ESC)
			return;
		if((ret = scanUtil.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s模式设置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				scanUtil.release();
				return;
			}
		}
		gui.cls_show_msg("连续扫码,共10次"+tipMsg+",任意键继续");
		values =  (String[]) scanUtil.doScan();
		if(values.length != ScanUtil.CONTINUOUS_COUNT)
		{
			gui.cls_show_msg1_record(fileName, "scan3", gKeepTimeErr,"line %d:%s扫码次数不一致(预期=%d,实际=%d)", Tools.getLineInfo(),TESTITEM,ScanUtil.CONTINUOUS_COUNT,values.length);
			if(!GlobalVariable.isContinue)
			{
				scanUtil.release();
				return;
			}
		}
		
		// 测试后置
		releaseScan(scanUtil);
		scanUtil.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, ScanUtil.CONTINUOUS_COUNT);
		gui.cls_show_msg1_record(fileName, "scan3", gScreenTime,"%s测试通过", TESTITEM);
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan(scanUtil);
		scanUtil = null;
		gui = null;
		
	}
}
