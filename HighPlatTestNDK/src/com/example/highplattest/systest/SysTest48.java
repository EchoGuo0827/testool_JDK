package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.BatteryReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest48.java
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150606
 * directory 		: 
 * description 		: 取电量/打印交叉测试用例
 * related document : 
 * history 		 	: author			date			remarks
 *  				 : 变更记录				变更时间			变更人员
 *					 打印交叉案例增加TTF交叉方式		 20200528		陈丁	
 *					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
 *					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
 *					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest48 extends DefaultFragment implements PrinterData
{
	final String TAG = SysTest48.class.getSimpleName();
	final String TESTITEM = "电池信息/打印";
	final int MAXWAITTIME = 10;
	final IntentFilter intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private BatteryReceiver mBatteryReceiver;
	
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	
	//测试主程序
	public void systest48() 
	{
		String funcName = "systest48";
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		mBatteryReceiver = new ReceiverTracker().new BatteryReceiver();
		printUtil = new PrintUtil(myactivity, handler,true);
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			config.print_config();
			try {
				bat_printf();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("取电量/打印\n0.打印配置\n1.交叉测试\n");
			switch (returnValue) 
			{	
			
			case '0':
				config.print_config();
				break;
				
			case '1':
				try {
					bat_printf();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				break;
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	/*private void bat_ttfprintf() {
		int cnt = 0, bak = 0, succ=0;
		int prnStatus= -1;
		int ret=-1;
		process body
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		
		while(cnt > 0)
		{
			//测试退出点
			if(gui.cls_show_msg1(3, "%s交叉测试,已执行%d次,成功%d次,[取消]退出测试" ,TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			//打印
			prnStatus = printUtil.print_byttfScript(DATACOMM_SIGN);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(CUT_TEST);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			
			prnStatus = printUtil.print_byttfScript(DATAPIC_SIGN);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(CUT_TEST);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			myactivity.registerReceiver(mBatteryReceiver, intent);
			SystemClock.sleep(1000L);
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			//打印
			for (int k = 0; k < 3; k++) {
				if (k==0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if (prnStatus != NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
						continue;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATACOMM);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			
			
			//打印
			for (int k = 0; k < 3; k++) {
				if (k==0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if (prnStatus != NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
						continue;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATAPIC);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			succ++;
			myactivity.unregisterReceiver(mBatteryReceiver);*//**注销广播*//*
		}
		gui.cls_show_msg1_record(TAG, "bat_ttfprintf",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次", TESTITEM,bak-cnt,succ);
	}*/

	//取电量和打印交叉
	public void bat_printf() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, succ=0;
		int printerStatus,prnRet = -1;
		int prnStatus;
		int ret=-1;
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:%s获取打印机状态异常(status = %d)", Tools.getLineInfo(),TAG,printerStatus);
			return;
		}
		// 注册打印事件
		if (RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(),prnlistener) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "bat_printf", g_keeptime,"line %d:print事件注册失败", Tools.getLineInfo());
			return;
		}
		while(cnt > 0)
		{
			//测试退出点
			if(gui.cls_show_msg1(3, "%s交叉测试,已执行%d次,成功%d次,[取消]退出测试" ,TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			//打印票据
			if((prnRet = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,prnRet);
				continue;
			}
			//TTF单次图片打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((prnRet = printUtil.print_png(gPicPath+"IHDR10.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,prnRet);
				continue;
			}
			//TTF单次指令打印
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,  "bat_printf",g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//TTF连续图片打印
			boolean flag1=false;
			for (int k = 0; k < 3; k++) {
			if (k==0) {
				if ((ret = printUtil.print_byttfScript(FEEDLINE))!=NDK_OK) {
					flag1=true;
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
					continue;
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag1=true;
					gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
			
			}
			if ((prnStatus = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				flag1=true;
				gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			
		}
			if (flag1) {
				continue;
			}

			//TTF连续指令打印
			for (int k = 0; k < 3; k++) {
			if (k==0) {
				if ((ret = printUtil.print_byttfScript(FEEDLINE))!=NDK_OK) {
					flag1=true;
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
					continue;
					
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag1=true;
					gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
				
			}
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				flag1=true;
				gui.cls_show_msg1_record(TAG, "bat_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			
		}
			if (flag1) {
				continue;
			}
			myactivity.registerReceiver(mBatteryReceiver, intent);
			SystemClock.sleep(1000L);
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			succ++;
			myactivity.unregisterReceiver(mBatteryReceiver);/**注销广播*/
		}
		// 解绑事件
		if (UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "bat_printf", g_keeptime,"line %d:print事件解绑失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "bat_printf",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次", TESTITEM,bak-cnt,succ);
	}
}
