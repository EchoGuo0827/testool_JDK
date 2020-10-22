package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest23.java
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150413
 * directory 		: 
 * description 		: 磁卡/打印卡交叉测试用例
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  huangjianb		20140413	 	created
 *		 		 	: 变更记录							变更时间			变更人员
 *			  		 打印交叉案例增加TTF交叉方式				 20200528		陈丁
 *					将TTF打印和NDK打印放在一起交叉       		 20200601                    陈丁
 *					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁	
 *					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest23 extends DefaultFragment implements PrinterData
{
	private final String TAG = SysTest23.class.getSimpleName();
	private final String TESTITEM = "磁卡/打印";
	private PrintUtil printUtil;
	private final int MAXWAITTIME = 10;
	Gui gui = new Gui(myactivity, handler);

	//测试主程序
	public void systest23() 
	{
		String funcName="systest23";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("磁卡/打印(注意:K21D存在磁卡与打印共用缓存区,此用例必须测试K21D的情况)\n0.交叉压力\n");
			switch (returnValue) 
			{	
			case '0':
				try
				{
					mag_printf();
				}catch(Exception e){
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;
				
			}
		}
		
	}
	
	/*//新增TTF打印方式与磁卡交叉 by chend 20200528
	private void mag_ttfprintf() {
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int prnStatus;
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		// 磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "mag_printf", g_keeptime, "line %d:mag事件注册失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg("请确保已装入打印纸并在打印时连续刷卡，并注意打印效果,按任意键继续");
		while(cnt > 0)
		{
			//测试退出点
			if(gui.cls_show_msg1(3, "%s交叉测试,已执行%d次,成功%d次,[取消]退出测试",TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//TTF打印
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
			
			
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
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
			
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
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
			
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
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
				prnStatus = printUtil.print_byttfScript(DATAPIC);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			succ++;
	
			
		}
		gui.cls_show_msg1_record(TAG, "mag_ttfprintf",g_time_0, "TTF方式交叉测试完成，已执行次数为%d，成功为%d次",TESTITEM, bak-cnt,succ);
		
		
	}*/

	//磁卡打印交叉测试
	public void mag_printf() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		/*process body*/
		printUtil = new PrintUtil( myactivity, handler,true);
		int printerStatus;
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());//因picture文件夹导致打印闪退事件未解绑
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
			gui.cls_show_msg1_record(TAG, "mag_printf", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		// 磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "mag_printf", g_keeptime, "line %d:mag事件注册失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		//提示信息
		if(gui.cls_show_msg("请确保已装入打印纸并在打印时连续刷卡，并注意打印效果，完成[确认]，退出[取消]")==ENTER)
		{
			handler.sendEmptyMessage(HandlerMsg.DIALOG_SYSTEST_BACK);
		}
		
		while(cnt > 0)
		{
			//测试退出点
			if(gui.cls_show_msg1(3, "%s交叉测试,已执行%d次,成功%d次,[取消]退出测试",TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//出现的客诉 ，先打印带走纸的脚本，再刷卡，再打印带走纸的脚本，会出现卡住的问题 20180427 zhangxinj
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印测试页
			if((ret = printUtil.print_testpage())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印随机数
			if((ret = printUtil.print_rand())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印随机数失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印20行"国"字
			if((ret = printUtil.print_guo())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印国字失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印填充单
			if((ret = printUtil.print_fill())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status=%d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印三角形
			if((ret = printUtil.print_triangle())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印三角形失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印竖条纹
			if((ret = printUtil.print_verticalbill())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印竖条纹失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印美团图片
			if((ret = printUtil.print_blank())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印美团图片失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret=%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印各种字体
			if((ret = printUtil.print_font())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印各种字体失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印回车
			if((ret = printUtil.print_enter())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印回车失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %s)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印奇偶回车空白行
			if((ret = printUtil.print_compress())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印压缩单失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印联迪单据
			if((ret = printUtil.print_landi())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印联迪票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//打印新国都单据
			if((ret = printUtil.print_xinguodu())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:%s打印新国都票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//TTF打印
			if((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			
			boolean flag1=false;
			//TTF连续图片打印
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
						gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
					gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
			
			}
			if (flag1) {
				continue;	
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//TTF打印
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
						gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
					gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
		
			}
			if (flag1) {
				continue;
			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			//TTF打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
		
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:刷卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mag_printf",g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "mag_printf", g_keeptime,"line %d:print事件解绑失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "mag_printf", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "mag_printf",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次",TESTITEM, bak-cnt,succ);
	}
}
