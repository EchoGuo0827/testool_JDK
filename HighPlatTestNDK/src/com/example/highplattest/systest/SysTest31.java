package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest31.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: SMART/打印交叉测试
 * related document :
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		 测试前置添加解绑事件		  	20200415		郑薛晴
 *					 打印交叉案例增加TTF交叉方式		 20200528		陈丁
 *					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
*					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
*					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁	
*					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest31 extends DefaultFragment implements PrinterData
{
	private final String TAG = SysTest31.class.getSimpleName();
	private final String TESTITEM = "SMART/打印";
	private _SMART_t type = _SMART_t.CPU_A;
	private final int MAXWAITTIME = 10;
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	private List<_SMART_t> typeList=new ArrayList<_SMART_t>();
	private int felicaChoose=0;
	private int ret=-1;
	public void systest31() 
	{
		String funcName="systest31";
		gui = new Gui(myactivity, handler);
		
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		printUtil = new PrintUtil(myactivity, handler,true);
		//初始化处理器，连接K21设备
		config = new Config(myactivity, handler);
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			typeList = config.smart_config();
			config.print_config();
			for(_SMART_t s:typeList){
				type=s;
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
			}
			return;
		}
		//主程序入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("SMART/打印\n0.SMART配置\n1.打印配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':
				type = config.smart_config().get(0);
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if((ret  = smartInit(type))!=NDK_OK)
				{
					gui.cls_show_msg1(g_keeptime, "line %d:初始化smart卡失败(%d)", Tools.getLineInfo(),ret);
				}
				break;
				
			case '1':
				config.print_config();
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
				
			default:
				break;
			}
		}
	}
	/*//新增TTF打印方式交叉
	private void cross_ttftest() {
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int prnStatus;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		gui.cls_show_msg("测试前请确保已放置打印纸和已安装射频卡%s,完成点任意键继续",type);
		// 注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER) 
		{
			if (ret == NDK_ERR_POSNDK_EVENT_REG_TWICE)
				SmartUnRegistEvent(type);
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件注册失败(%d)",Tools.getLineInfo(),type, ret);
			return;
		}
		while(cnt > 0)
		{
			//保护动作
			smartDeactive(type);
			if(gui.cls_show_msg1(2, "%s/TTF打印交叉测试,已执行%d次,成功%d次,[取消]退出测试", type,bak-cnt,succ)==ESC)
				break;
			cnt--;
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
			//上电
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK )
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡激活失败(ret = %d)", Tools.getLineInfo(),bak-cnt,type,ret);
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
			//读写
			if ((ret = smartApduRw(type,req,UidBuf)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡APDU失败(ret = %d)", Tools.getLineInfo(),bak - cnt,type, ret);
				break;
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
			
			//下电
			if((ret = smartDeactive(type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡关闭场失败(ret = %d)",Tools.getLineInfo(),bak-cnt,type, ret);
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
		
		smartDeactive(type);
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s/TTF打印交叉测试完成，已执行次数为%d，成功为%d次", type,bak-cnt,succ);
		
	}*/

	//SMART/打印交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int printerStatus;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		
		/*process body*/
		//设置压力次数
		PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag){
			packet.setLifecycle(getCycleValue());
		}else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
	
		//提示信息
		gui.cls_show_msg("测试前请确保已放置打印纸和已安装射频卡%s,完成点任意键继续",type);
		//打印初始化和获取空闲状态
		if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:%s获取打印机状态失败!(status = %d)", Tools.getLineInfo(),TAG,printerStatus);
			return;
		}
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			if (ret == NDK_ERR_POSNDK_EVENT_REG_TWICE)
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		// 注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER) 
		{
			if (ret == NDK_ERR_POSNDK_EVENT_REG_TWICE)
				SmartUnRegistEvent(type);
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件注册失败(%d)",Tools.getLineInfo(),type, ret);
			return;
		}
		while(cnt > 0)
		{
			//保护动作
			smartDeactive(type);
			if(gui.cls_show_msg1(2, "%s/打印交叉测试,已执行%d次,成功%d次,[取消]退出测试", type,bak-cnt,succ)==ESC)
				break;
			printUtil.print_triangle();
			if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡寻卡失败(ret = %d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//上电、打印
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK )
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡激活失败(ret = %d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			
			//TTF单次图片打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}			
//			prnStatus = printUtil.print_byttfScript(CUT_TEST);
//			if (prnStatus != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//				continue;
//			}
			while(cnt > 0)
			{
				//测试退出点
				if(gui.cls_show_msg1(3, "%s/打印交叉测试，已执行%d次，成功%d次，[取消]退出测试", type,bak-cnt,succ)==ESC)
					break;
				cnt--;
				printUtil.print_png(gPicPath+"IHDR1.png");
				//读写、打印
				if ((ret = smartApduRw(type,req,UidBuf)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡APDU失败(ret = %d)", Tools.getLineInfo(),bak - cnt,type, ret);
					break;
				}
				//TTF单次指令打印
				if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
					continue;
				}
//				prnStatus = printUtil.print_byttfScript(CUT_TEST);
//				if (prnStatus != NDK_OK) 
//				{
//					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
//					continue;
//				}
				if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					break;
				}
				//验证打印是否监听到
				if((ret = priEventCheck())!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				succ++;
			}
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
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
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
	
		}
			if (flag1) {
				continue;
			}
			//下电、打印
			if((ret = smartDeactive(type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s卡关闭场失败(ret = %d)",Tools.getLineInfo(),bak-cnt,type, ret);
				continue;
			}
			if((printerStatus =printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
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
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			
	
		}
			if (flag1) {
				continue;
			}
			
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:print事件解绑失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		smartDeactive(type);
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"(%s/打印)交叉测试完成，已执行次数为%d，成功为%d次", type,bak-cnt,succ);
	}
}
