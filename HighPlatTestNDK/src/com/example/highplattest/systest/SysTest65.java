package com.example.highplattest.systest;

import java.io.IOException;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest65.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160901
 * directory 		: 
 * description 		: NFC/打印交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160901	 	    created
 *  				 : 变更记录				变更时间			变更人员
 *					 打印交叉案例增加TTF交叉方式		 20200528		陈丁	
 *					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
 *					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
 *					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest65 extends DefaultFragment implements PrinterData
{
	private final String TAG = SysTest65.class.getSimpleName();
	private final String TESTITEM = "NFC/打印";
	private Nfc_Card nfc_Card = Nfc_Card.NFC_B;
	private int MAXWAITTIME = 10*1000;
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	
	public void systest65()
	{
		String funcName = "systest65";
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
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
			nfc_Card = nfc_config(handler, TESTITEM);
			config.print_config();
			try {
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		// 测试主入口
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/打印\n0.NFC配置\n1.打印配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			case '0':
				nfc_Card = nfc_config(handler, TESTITEM);
				break;
				
			case '1':
				config.print_config();
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) {
					gui.cls_show_msg1_record(TAG, TAG,0, "line %d:抛出异常(%s)",Tools.getLineInfo(), e.getMessage());
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
	
	/*private void cross_ttftest() {
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int prnStatus;
		int printerResult;
		
		process body
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		NfcTool nfcTool = new NfcTool(myactivity);
		gui.cls_show_msg("请放置%s卡并放入打印纸,完成任意键继续",nfc_Card);
		
		while(cnt>0)
		{
			// 保护
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(3, "%s/打印交叉测试，已执行%d次，成功%d次，[取消]退出测试", nfc_Card,bak-cnt,succ)==ESC)
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
			// 上电
			if((ret = nfcTool.nfcConnect(reader_flag))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s连接失败(ret = %d)", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			//打印
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
			// 读写
			try 
			{
				if((ret = nfcTool.nfcRw(nfc_Card))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s读写失败(ret = %d)", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
					continue;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s读写失败(ret = %s)", Tools.getLineInfo(),bak-cnt,nfc_Card,e.getMessage());
				continue;
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
				prnStatus = printUtil.print_byttfScript(DATACOMM);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			
			// 下电
			nfcTool.nfcDisEnableMode();
			
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
		}
		
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s/打印交叉测试完成,已执行次数为%d,成功为%d次", nfc_Card,bak-cnt,succ);
	}*/

	/**
	 * 交叉测试
	 */
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int printerStatus;
		int printerResult;
		
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		NfcTool nfcTool = new NfcTool(myactivity);
		
		if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1(2, "line %d:打印机状态异常", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg("请放置%s卡,完成任意键继续",nfc_Card);
		// 注册打印事件
		if (RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(),prnlistener) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:print事件注册失败", Tools.getLineInfo());
			return;
		}
		while(cnt>0)
		{
			// 保护
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(3, "%s/打印交叉测试，已执行%d次，成功%d次，[取消]退出测试", nfc_Card,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 上电
			if((ret = nfcTool.nfcConnect(reader_flag))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s连接失败(ret = %d)", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			if((printerResult = printUtil.print_bill_add_feeding())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,printerResult);
				continue;
			}
			//TTF单次图片打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((printerResult = printUtil.print_png(gPicPath+"other1.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印单据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,printerResult);
				continue;
			}
			//TTF单次指令打印
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
				
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			// 读写
			try 
			{
				if((ret = nfcTool.nfcRw(nfc_Card))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s读写失败(ret = %d)", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
					continue;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s读写失败(ret = %s)", Tools.getLineInfo(),bak-cnt,nfc_Card,e.getMessage());
				continue;
			}
			if((printerResult = printUtil.print_bill_add_feeding())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,printerResult);
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
				if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag1=true;
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
				
			}
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				flag1=true;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			
		}
			if (flag1) {
				continue;
			}
			if((printerResult = printUtil.print_png(gPicPath+"other2.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印单据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,printerResult);
				continue;
			}
			
			// 下电
			nfcTool.nfcDisEnableMode();
			if((printerResult = printUtil.print_bill_add_feeding())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,printerResult);
				continue;
			}
			if((printerResult = printUtil.print_png(gPicPath+"other3.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印单据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,printerResult);
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
				if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag1=true;
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
				
			
			}
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				flag1=true;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			
		}
			if (flag1) {
				continue;
			}
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,  "cross_test",g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		// 解绑事件
		if (UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:print事件解绑失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s/打印交叉测试完成,已执行次数为%d,成功为%d次", nfc_Card,bak-cnt,succ);
	}
}
