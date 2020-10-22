package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
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
 * file name 		: SysTest46.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150414
 * directory 		: 
 * description 		: SD卡U盘/打印交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150414		created
 *  				:变更记录												变更时间			变更人员
*					打印交叉案例增加TTF交叉方式		 							20200528		陈丁
*					将TTF打印和NDK打印放在一起交叉        							20200601       	陈丁
*					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest46 extends DefaultFragment implements PrinterData
{
	/*------------global variables definition-----------------------*/
	private final String TAG = SysTest46.class.getSimpleName();
	private final String TESTITEM = "打印/SD卡(U盘)";
	private DiskType diskType;
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	private String diskString;
	
	public void systest46() 
	{
		String funcName="systest46";
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		printUtil = new PrintUtil( myactivity, handler,true);
		
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
			diskString = config.confSDU();
			String[] diskTypes=diskString.split(",");
			for (int i = 0; i < diskTypes.length; i++) {
				diskType=getDiskType(diskTypes[i]);
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
			}		
			return;
		}
		//测试程序入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("打印/SD卡(U盘)\n0.打印配置\n1.SD/U盘配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':
				//调用打印配置函数
				config.print_config();
				break;
				
			case '1':
				// SD卡U盘配置
				diskType =getDiskType(config.confSDU());
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) 
				{
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
		private & local definition
		int cnt = 0, bak = 0, ret = 0, succ=0;
		
		process body
		int prnStatus;
		
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		gui.cls_show_msg("测试前请确保,已放入打印纸和已安装%s,点任意键继续...",diskType);
		while(cnt > 0)
		{
			//测试退出点
			if(gui.cls_show_msg1(3,"打印/%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", diskType,bak-cnt,succ)==ESC)
				break;
			cnt--;
			// 打印
			prnStatus = printUtil.print_byttfScript(DATACOMM_SIGN);
			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(CUT_TEST);
			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			
			prnStatus = printUtil.print_byttfScript(DATAPIC_SIGN);
			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(CUT_TEST);
			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			if((ret = systestSdCard(diskType)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:第%d次：%s测试失败(ret = %d)", Tools.getLineInfo(),bak-cnt,diskType,ret);
				continue;
			}
			//打印
			for (int k = 0; k < 3; k++) {
				if (k==0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
						continue;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATAPIC);
				if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			
			//打印
			for (int k = 0; k < 3; k++) {
				if (k==0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
						continue;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATACOMM);
				if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			
			succ++;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM, g_time_0,"ttf打印/%s交叉测试完成，已执行次数为%d，成功为%d次",diskType,bak-cnt,succ);
	}*/


	public void cross_test() 
	{
		/*private & local definition*/
		String funcName = "cross_test";
		final int MAXWAITTIME = 60*1000;
		int cnt = 0, bak = 0, ret = 0, succ=0;
		
		/*process body*/
		int printerStatus;
		int prnStatus;
		boolean isForErr=false;// for循环中是否出错
		
		//设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		
		//提示信息
		gui.cls_show_msg("测试前请确保,已放入打印纸和已安装%s,点任意键继续...",diskType);
		if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:%s获取打印机状态异常(status = %d)", Tools.getLineInfo(),TAG,printerStatus);
			return;
		}
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(cnt > 0)
		{
			isForErr=false;
			//测试退出点
			if(gui.cls_show_msg1(3,"打印/%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", diskType,bak-cnt,succ)==ESC)
				break;
			cnt--;
			// 打印票据
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:第%d次:打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//TTF单次图片打印
			prnStatus = printUtil.print_byttfScript(DATAPIC_SIGN);
			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:第%d次，TTF打印测试失败(ret=%d)", Tools.getLineInfo(), bak-cnt,prnStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			//TTF连续指令打印
			for (int k = 0; k < 3; k++) {
				if (k == 0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) {
						gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:第%d次，TTF打印测试失败(ret=%d)",Tools.getLineInfo(), bak - cnt, prnStatus);
						isForErr=true;
						break;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATACOMM_SIGN);
				if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) {
					gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:第%d次,TTF打印测试失败(ret=%d)",Tools.getLineInfo(), bak - cnt, prnStatus);
					isForErr=true;
					break;
				}
			}
			if(isForErr)
				continue;
			if((ret = printUtil.print_png(gPicPath+"IHDR1.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:第%d次:打印图片失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//TTF单次指令打印
			prnStatus = printUtil.print_byttfScript(DATACOMM_SIGN);
			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:第%d次，TTF打印测试失败(ret=%d)", Tools.getLineInfo(), bak-cnt,prnStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			if((ret = systestSdCard(diskType)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:第%d次：%s测试失败(ret = %d)", Tools.getLineInfo(),bak-cnt,diskType,ret);
				continue;
			}
			//TTF连续图片打印
			for (int k = 0; k < 3; k++) {
				if (k == 0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) {
						gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:第%d次，TTF打印测试失败(ret=%d)",Tools.getLineInfo(), bak - cnt, prnStatus);
						isForErr=true;
						break;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATAPIC_SIGN);
				if((prnStatus =printUtil.getPrintStatus(MAXWAITTIME))!= NDK_OK) {
					gui.cls_show_msg1_record(TAG, funcName, 5,"line %d:第%d次,TTF打印测试失败(ret=%d)",Tools.getLineInfo(), bak - cnt, prnStatus);
					isForErr=true;
					break;
				}
			}
			if(isForErr)
				continue;
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,  funcName,g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:print事件解绑失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM, g_time_0,"(打印/%s)交叉测试完成，已执行次数为%d，成功为%d次",diskType,bak-cnt,succ);
	}
}
