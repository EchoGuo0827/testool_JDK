package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest44.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: 打印/MDM交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *				:		变更记录				变更时间			变更人员
 *					打印交叉案例增加TTF交叉方式		 20200528		陈丁			  		 
 * 					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
*					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
*					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest44 extends DefaultFragment implements PrinterData
{

	/*------------global variables definition-----------------------*/
	private final String TAG = SysTest44.class.getSimpleName();
	private final String TESTITEM = "打印/MDM交叉";
	private NlModemManager nlModemManager;
	private Gui gui = null;
	Config config=new Config(myactivity, handler);
	
	public void systest44() 
	{
		String funcName="systest44";
		gui = new Gui(myactivity, handler);
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
		nlModemManager =  (NlModemManager) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("打印/MDM交叉\n0.打印配置\n1.MDM配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':
				//调用打印配置函数
				config.print_config();
				break;
				
			case '1':
				config.config_para();
				break;
				
			case '2':
				cross_test();
				break;
			
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	/*private void cross_ttftest() {
		int ret = -1;
		LinkType type = ModemBean.type_MDM;
		int i = 0, succ = 0, send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		int prnStatus;
		PacketBean sendPacket = new PacketBean();
		PrintUtil printUtil = new PrintUtil(myactivity, handler);
		Layer layer = new Layer(myactivity, handler);
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		mdm_clrportbuf_all(nlModemManager);
		
		
		// 测试前置，modem复位
		if((ret = mdm_reset(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:MDM复位失败（%d）", Tools.getLineInfo(),ret);
			return;
		}
		while(true)
		{
			//保护动作
			mdm_clrportbuf_all(nlModemManager);
			layer.mdm_hangup(nlModemManager);
			
			Log.e("i"+"  len", i+" "+sendPacket.getLifecycle());
			//测试退出点
			if(gui.cls_show_msg1(3, "正在进行第%d次%s交叉测试(已成功%d次),【取消】退出测试", i+1, TESTITEM, succ)==ESC)
				break;
			
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			//初始化MODEM
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", i);
			if((ret=mdm_init(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM初始化失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
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
			//拨号
			gui.cls_show_msg1(2,"MODEM拨%s中（第%d次）...", ModemBean.MDMDialStr, i);
			if((ret=layer.mdm_dial(ModemBean.MDMDialStr, nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM拨号失败(%d)", Tools.getLineInfo(), i, ret);
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
			if((send_len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
				continue;
			}
			
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
			if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 20, type))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}
			
			if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(), type)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM数据校验失败", Tools.getLineInfo(), i);
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
			
			//挂断
			gui.cls_show_msg1(2, "MODEM挂断中（第%d次）...", i);
			if((ret=layer.mdm_hangup(nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM挂断失败(%d)", Tools.getLineInfo(), i, ret);
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
		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "TTF打印/modem交叉测试完成,已执行次数为%d,成功为%d次", i, succ);
	}*/

	//交叉测试具体实现函数
	public void cross_test() 
	{
		int ret = -1;
		LinkType type = ModemBean.type_MDM;
		int i = 0, succ = 0, send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
		Layer layer = new Layer(myactivity, handler);
		int printerStatus;
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		mdm_clrportbuf_all(nlModemManager);
		
		// 测试前置，modem复位
		if((ret = mdm_reset(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:MDM复位失败（%d）", Tools.getLineInfo(),ret);
			return;
		}
		// 注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//后续等薛晴封装好打印单项函数和MDM流程函数后再行实现
		while(true)
		{
			//保护动作
			mdm_clrportbuf_all(nlModemManager);
			layer.mdm_hangup(nlModemManager);
			
			Log.e("i"+"  len", i+" "+sendPacket.getLifecycle());
			//测试退出点
			if(gui.cls_show_msg1(3, "正在进行第%d次%s交叉测试(已成功%d次),【取消】退出测试", i+1, TESTITEM, succ)==ESC)
				break;
			
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			//初始化MODEM
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", i);
			if((ret=mdm_init(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM初始化失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			printUtil.print_bill();
			//TTF单次图片打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			//拨号
			gui.cls_show_msg1(2,"MODEM拨%s中（第%d次）...", ModemBean.MDMDialStr, i);
			if((ret=layer.mdm_dial(ModemBean.MDMDialStr, nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM拨号失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			printUtil.print_bill();
			//TTF单次指令打印
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			//数据通讯
			//发送数据
			if((send_len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
				continue;
			}
			printUtil.print_bill();
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
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
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
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
		
		}
			if (flag1) {
				continue;
			}
			//接收数据
			if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 20, type))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}
			printUtil.print_bill();
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
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
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
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
		}
			if (flag1) {
				continue;
			}
			//比较数据
			if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(), type)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			printUtil.print_bill();
			
			//挂断
			gui.cls_show_msg1(2, "MODEM挂断中（第%d次）...", i);
			if((ret=layer.mdm_hangup(nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM挂断失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,  "cross_test",g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			succ++;
			
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件解绑失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "交叉测试完成,已执行次数为%d,成功为%d次", i, succ);
	}
	
}
