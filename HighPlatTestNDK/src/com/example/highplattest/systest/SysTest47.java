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
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest47.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20151223
 * directory 		: 
 * description 		: 触屏/打印交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  zhengxq          20151223         created
 *   				 : 变更记录				变更时间			变更人员
 *					 打印交叉案例增加TTF交叉方式		 20200528		陈丁	
 *					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
*					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
*					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)*/
public class SysTest47 extends DefaultFragment implements PrinterData
{
	private final String TAG = SysTest47.class.getSimpleName();
	private final String TESTITEM = "触屏/打印";
	private final int MAXWAITTIME = 10;
	Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	
	public void systest47()
	{
		String funcName="systest47";
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		//初始化处理器，连接K21设备
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
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while (true) 
		{
			int returnValue=gui.cls_show_msg("触屏/打印\n0.打印配置\n1.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':
				config.print_config();
				break;
			
			case '1':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					e.printStackTrace();
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				} 
				break;
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	
	/*private void cross_ttftest() {
		int bak = 0,succ = 0, cnt,ret=0;
		int prnStatus;
		// 设置压力次数
		PacketBean sendPacket = new PacketBean();
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		
		while(cnt>0)
		{	
			if(gui.cls_show_msg1(3,"%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", TESTITEM,bak-cnt, succ)==ESC)
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
			// 触屏测试
			if((ret = systestTouch())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:触屏测试失败,实际(%d，%d)",
						Tools.getLineInfo(),bak-cnt,(int)GlobalVariable.gScreenX,(int)GlobalVariable.gScreenY);
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
		gui.cls_show_msg1_record(TAG,"cross_test",g_time_0, "交叉测试完成,已执行次数为%d,成功为%d次",bak-cnt,succ);
	}*/


	public void cross_test() 
	{
		/*private & local definition*/
		int bak = 0,succ = 0, cnt,ret=0;
		int printerStatus;
		int prnStatus;
		// 设置压力次数
		PacketBean sendPacket = new PacketBean();
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		/*process body*/
		while(cnt>0)
		{	
			if(gui.cls_show_msg1(3,"%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", TESTITEM,bak-cnt, succ)==ESC)
				break;
			cnt--;
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			//TTF单次图片打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			
	//TTF单次指令打印
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((ret = printUtil.print_png(gPicPath+"IHDR2.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			//TTF连续图片打印
			boolean flag1=false;
			for (int k = 0; k < 3; k++) {
			if (k==0) {
				if ((prnStatus = printUtil.print_byttfScript(FEEDLINE))!=NDK_OK) {
					flag1=true;
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag1=true;
					gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
				
			}
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				flag1=true;
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
					gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}

		}
		if (flag1) {
			continue;
		}
			// 触屏测试
			if((ret = systestTouch())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:触屏测试失败,实际(%d，%d)",
						Tools.getLineInfo(),bak-cnt,(int)GlobalVariable.gScreenX,(int)GlobalVariable.gScreenY);
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
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:print事件解绑失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG,"cross_test",g_time_0, "交叉测试完成,已执行次数为%d,成功为%d次",bak-cnt,succ);
	}
}
