package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest59.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20151102
 * directory 		: 
 * description 		: 打印/键盘
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20151102		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest59 extends DefaultFragment
{
	private final String TAG = SysTest59.class.getSimpleName();
	private final String TESTITEM = "打印/键盘";
	private final int MAXWAITTIME = 10;
	private Gui gui = new Gui(myactivity, handler);
	private Config config;
	private int ret=-1;
	public void systest59() 
	{
		String funcName = "systest59";
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
		config = new Config(myactivity, handler);
		while(true)
		{
			int returnValue=gui.cls_show_msg("打印/键盘\n0.运行\n");
			switch (returnValue) 
			{
			case '0':
				try
				{
					prt_input();
				}catch(Exception e){
					gui.cls_show_msg1_record(TAG, TESTITEM, 2, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
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
	
	public void prt_input()
	{
		gui.cls_show_msg1(2, "%s测试,请在打印时连续按键,并注意打印效果", TESTITEM);
		config.print_config();
		try 
		{
			print_kb();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void print_kb()
	{
		int cnt = 0, bak = 0, succ = 0;
		int printerStatus;
		PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
		/* process body */
		// 设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "print_kb", g_keeptime, "line %d:print事件注册失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		// 提示信息
		while (cnt> 0) {
			// 测试退出点
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，【取消】退出测试", TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM, "print_kb",g_keeptime,"line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}       
			// 打印三角形
			printUtil.print_triangle();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次:获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印测试页
			printUtil.print_testpage();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印随即数
			printUtil.print_rand();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印“国”字
			printUtil.print_guo();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:获取打印机状态失败%s", printerStatus);
				continue;
			}
			// 打印填充的
			printUtil.print_fill();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印票据
			printUtil.print_bill();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印竖条纹
			printUtil.print_verticalbill();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印空美团图片
			printUtil.print_blank();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印各种字体
			printUtil.print_font();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印回车符
			printUtil.print_enter();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印基偶回车空白行
			printUtil.print_compress();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime,"line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印联迪单据
			printUtil.print_landi();
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TESTITEM,"print_kb",g_keeptime, "line %d:第%d次：获取打印机状态失败（status = %d）", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			// 打印新国都单据
			printUtil.print_xinguodu();
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,  "print_kb",g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "print_kb", g_keeptime, "line %d:print事件解绑失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1_record(TESTITEM, "printPrecess",g_time_0,"%s交叉测试完成，已执行次数为%d，成功为%d次",TESTITEM, bak - cnt, succ);
	}
}
