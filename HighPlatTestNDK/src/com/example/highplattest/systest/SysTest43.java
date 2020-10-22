package com.example.highplattest.systest;

import java.util.Arrays;
import android.os.SystemClock;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest43.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150414
 * directory 		: 
 * description 		: 无线/打印交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150414		created
 * 					:变更记录												变更时间			变更人员
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200527                          陈丁
*					打印交叉案例增加TTF交叉方式		 							20200528		陈丁
*					将TTF打印和NDK打印放在一起交叉        							20200601       	陈丁
*					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 					20200609		陈丁
*					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    	陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest43 extends DefaultFragment implements PrinterData
{
	/*------------global variables definition-----------------------*/
	private final String TAG = SysTest43.class.getSimpleName();
	private MobilePara mobilePara = new MobilePara();
	private final String TESTITEM = "打印/WLM";
	private final int MAXWAITTIME = 10;
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	
	public void systest43() 
	{
		String funcName = "systest43";
		gui = new Gui(myactivity, handler);
		
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		//无线初始化
		initLayer();
		config = new Config(myactivity, handler);
		printUtil = new PrintUtil(myactivity, handler,true);
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		boolean mobilestate=mobileUtil.getMobileDataState(myactivity);
		mobileUtil.closeOther();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			config.print_config();
			if(config.confConnWLM(true,mobilePara)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:网络配置失败!!!", Tools.getLineInfo());
				return;
			}
			else 
				gui.cls_show_msg1(2, "网络配置成功!!!");
			try {
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			mobileUtil.setMobileData(myactivity, mobilestate);
			return;
		}
		
		//测试程序入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("打印/WLM\n0.打印配置\n1.无线配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':
				//调用打印配置函数
				config.print_config();
				break;
				
			case '1':
				// 无线配置
				switch (config.confConnWLM(true,mobilePara)) 
				{
				case NDK_OK:
					gui.cls_show_msg1(2, "网络配置成功!!!");
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1(0,"line %d:网络未接通！！！",Tools.getLineInfo());
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) {
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				
				break;
			case ESC:
				mobileUtil.setMobileData(myactivity, mobilestate);
				intentSys();
				return;
			}
		}
	}
	
	/*private void cross_ttftest() {
		int i = 0, ret = -1, succ=0, slen = 0, rlen = 0;
		PacketBean sendPacket = new PacketBean();
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		int prnStatus;
		if(setWireType(mobilePara)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:%s未插入sim卡", Tools.getLineInfo(),TAG);
			return;
		}
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		
		process body
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		while(true)
		{
			//保护动作
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
			SystemClock.sleep(5000);//每次挂断之后重新连接要等待5秒,减轻绎芯片的压力
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，[取消]退出测试", TESTITEM,i,succ)==ESC)
				break;
			
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
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
		
			if((ret = layerBase.netUp( mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetUp失败(%d)", Tools.getLineInfo(),i,ret);
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
			
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransUp失败(ret = %d)", Tools.getLineInfo(),i,ret);
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
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(ret = %d)", Tools.getLineInfo(),i,slen);
				continue;
			}
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(ret = %d)", Tools.getLineInfo(),i,rlen);
				continue;
			}
			//比较数据、打印票据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:校验数据失败", Tools.getLineInfo(),i);
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
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransDown失败(ret = %d)", Tools.getLineInfo(),i,ret);
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
		
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetDown失败(ret = %d)", Tools.getLineInfo(),i,ret);
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
		
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次", TESTITEM,i,succ);
		
	}*/

	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int i = 0, ret = -1, succ=0, slen = 0, rlen = 0;
		PacketBean sendPacket = new PacketBean();
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		int printerStatus;
		int prnStatus;
		if(setWireType(mobilePara)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:%s未插入sim卡", Tools.getLineInfo(),TAG);
			return;
		}
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:%s获取打印机状态异常(status = %d)", Tools.getLineInfo(),TAG,printerStatus);
			return;
		}
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(true)
		{
			//保护动作
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
			SystemClock.sleep(5000);//每次挂断之后重新连接要等待5秒,减轻绎芯片的压力
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，[取消]退出测试", TESTITEM,i,succ)==ESC)
				break;
			
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
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
			if((ret = printUtil.print_png(gPicPath+"/ysz1.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			//Netup、打印票据
			if((ret = layerBase.netUp( mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetUp失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			if((ret = printUtil.print_png(gPicPath+"/ysz2.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
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
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//TransUp、打印票据
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransUp失败(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
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
			
			if((ret = printUtil.print_png(gPicPath+"/ysz3.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			
			//发送数据、打印票据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(ret = %d)", Tools.getLineInfo(),i,slen);
				continue;
			}
			printUtil.print_png(gPicPath+"/ysz4.png");
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
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
			//接收数据、打印票据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(ret = %d)", Tools.getLineInfo(),i,rlen);
				continue;
			}
			//比较数据、打印票据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:校验数据失败", Tools.getLineInfo(),i);
				continue;
			}
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
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			if((ret = printUtil.print_png(gPicPath+"/ysz5.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//TransDown、打印票据
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransDown失败(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			if((ret = printUtil.print_png(gPicPath+"ysz6.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
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
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//NetDown、打印票据
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetDown失败(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
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
			if((ret = printUtil.print_png(gPicPath+"ysz7.png"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印图片失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态异常(status = %d)", Tools.getLineInfo(),i,printerStatus);
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
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:print事件解绑失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次", TESTITEM,i,succ);
	}
}
