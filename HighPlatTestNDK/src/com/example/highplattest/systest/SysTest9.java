package com.example.highplattest.systest;

import java.io.File;
import java.util.concurrent.TimeUnit;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.printer.Printer2;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest9.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150309
 * directory 		: 
 * description 		: 打印综合测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150413		creat
 * 		 		 	: 变更记录						变更时间					变更人员
 *			  		TTF优化打印压力，异常测试增补		20200528				陈丁
 *					TTF优化打印性能测试增补			20200528				翁凯健
 *					新增招标使用性能测试项;				20200608				陈丁
 *					去除切纸打印判断;
 *					去除多余的走纸；
 *					异常测试新增开发需求，ttf循环打印长票据，验证打印票据无卡顿            20200610   陈丁
 *					修复异常测试打印填充单缺纸后打印测试死循环无法退出问题     
 *					数据连续打印添加设置字体行间距浓度	 	20200611                                               陈丁
 *					修改宋体字库放置位置，修改部分提示语
 *					修复异常测试切纸参数异常问题 
 *					TTF打印新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁    
 *					去除替换打印库的提示语		陈丁					20200710               
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest9 extends DefaultFragment implements PrinterData
{
	float tmp=0.0f;
	int ret = 0;
	private final String  TAG = SysTest9.class.getSimpleName();
	private final String TESTITEM = "打印性能、压力";
	private PrintUtil printUtil;
	private final int MAXWAITTIME = 10;
	private Gui gui = null;
	private Config config;
	private int DEFAULT_CNT_VLE2 = 20;
	
	//打印综合测试主程序
	public void systest9() 
	{
		String funcName ="systest9";
		gui = new Gui(myactivity, handler);
		printUtil = new PrintUtil(myactivity, handler,true);
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			// 设置灰度
			config.print_config();
			g_CycleTime=1;
			// 打印压力
			printPrecess();
			// 打印性能
			singlefun();
			return;
		}
//		/**X5产品需要替换libnlprintex.so*/
//		if(GlobalVariable.currentPlatform==Model_Type.X5)
//		{
//			if(gui.cls_show_msg("X5设备需要使用服务器上的libnlprintex.so替换HighPlattest工程下libs目录下的全部libnlprintex.so，已完成任意键继续测试，ESC键退出")==ESC)
//			{
//				intentSys();
//				return;
//			}
//		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		
		while(true)
		{
			//X5打印性能需和其他设备一样是2寸才有可比较性，其他压力仍为3寸add by wangxy 20180926 
			if(GlobalVariable.currentPlatform==Model_Type.X5)
			{
				if((ret=JniNdk.JNI_PrnSetPaperSize(3))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "singlefun", g_keeptime, "line %d:打印纸设置为3寸失败(ret=%s)",Tools.getLineInfo(), ret);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			int rturnValue=gui.cls_show_msg("打印性能、压力\n0.打印配置\n1.打印压力\n2.单项及性能\n3.异常\n4.银联打印标准测试");
			switch (rturnValue) 
			{	
			case '0':
				config.print_config();
				break;
				
			case '1':
				printPrecess();
				break;
				
			case '2':
				//X5打印性能需和其他设备一样是2寸才有可比较性add by wangxy 20180926 
				if(GlobalVariable.currentPlatform==Model_Type.X5)
				{
					if((ret=JniNdk.JNI_PrnSetPaperSize(2))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "singlefun", g_keeptime, "line %d:打印纸设置为2寸失败(ret=%s)",Tools.getLineInfo(), ret);
						if(!GlobalVariable.isContinue)
							return;
					}
				}
				singlefun();
				break;
				
			case '3':
				printAbnormal();
				break;
			
			case'4':
				int rturnValue3=gui.cls_show_msg("银联打印测试标准\n0.数据连续打印\n1.4张票据生成时间");
				switch (rturnValue3) 
				{	
				case'0':
					UnionPay_Print1();
					break;
					
				case'1':
					UnionPay_Print2();
					break;
					
				default:
					break;
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
	//打印四张家乐福图片，并切纸。联迪是6s   by chending20200608
	private void UnionPay_Print2() {
		// TODO Auto-generated method stub
		long oldtime;
		float endtime;
		int prnStatus;
		String feedline="*feedline p:48\n";
		String data1=
				"!NLFONT 9 12\n" +
				"*image l 576*961 path:/mnt/sdcard/picture/carrefour1.png\n" +
				"*line\n" +
				"*feedline p:200\n"+
				"*cut\n";
		 String data2=
				"!NLFONT 9 12\n" +
				"*image l 576*1126 path:/mnt/sdcard/picture/carrefour2.png\n" +
				"*line\n" +
				"*feedline p:200\n"+
				"*cut\n";
		 
		 String datafinal=feedline+data1+data2+data2+data2;
		gui.cls_show_msg("以下打印4张家乐福票据并切纸。联迪打印时间约为6s，请对比打印时间性能。按任意键继续");
		oldtime= System.currentTimeMillis();
		prnStatus = printUtil.print_byttfScript_ccb(datafinal);
		if (prnStatus != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "UnionPay_Print2", g_keeptime,"line %d:打印失败(ret=%d)", Tools.getLineInfo(), prnStatus);
		}
		
		endtime = Tools.getStopTime(oldtime);
		gui.cls_show_msg1_record(TAG, "UnionPay_Print2", 10,"测试结束，连续打印4张家乐福图片时间为%4.2fs", endtime);
	}

	//银联xx秒内连续打印数据，看能打印多少行   by chending20200608
	//先按照目前每个平台满电压情况下的打印行数 后续打印修改不得低于该行数就行了  目前没有标准，也不知道标准，正常情况下是跟其他厂商对比


	private void UnionPay_Print1() {
		// TODO Auto-generated method stub
		int cnt;
		int prnStatus;
		int count = 0;
		long oldtime;
		String data2="中国建设银行股份有限公司";
//		gui.cls_show_msg("请确保将svn上的simsun.ttc文件放入sdcard目录下，按任意键继续");
		gui.cls_show_msg("请先设置打印总时间(目前每个平台满电压情况下的打印行数 后续打印修改不得低于该行数就行,目前没有标准，和其他厂商对比即可),按任意键继续");
		cnt = gui.JDK_ReadData(TIMEOUT_INPUT, 30);
		float time=0;
		oldtime=System.currentTimeMillis();
		Log.d("eric_chen", "start-----------cnt: "+cnt);
		while (true) {
			time=Tools.getStopTime(oldtime);
			if (time>=cnt) {
				break;
			}
			//使用默认打印浓度  这里设置成6 和默认一致。
			prnStatus = printUtil.print_byttfScript_ccb(String.format("!font /sdcard/picture/simsun.ttc\n!gray 6\n!yspace 0\n*text l %s\n",(++count)+"   "+data2));
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "UnionPay_Print1", g_keeptime,"line %d:打印失败(ret=%d count=%d)", Tools.getLineInfo(), prnStatus,count);
			}
			
		}
		
		gui.cls_show_msg1_record(TAG, "UnionPay_Print1", 10,"打印结束,请确认打印行数是否符合测试标准");
		
		
		
		
		
		
		
	}

	public void printPrecess() 
	{
		int returnValue = 47;// 转换为ASCII码看
		int exitCode='2';
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
				{
					returnValue=returnValue+2;
					exitCode='3';
				}
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))
				{
					returnValue=returnValue+1;
					exitCode='2';
				}
				if(returnValue >= exitCode)
				{
					gui.cls_show_msg1_record(TAG, "printPrecess", 2, "打印压力连续测试结束");
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行连续打印压力测试,[取消]退出")==ESC)
					return;
			}
			else
			{
				returnValue=gui.cls_show_msg("打印压力\n0.jni打印压力\n%s%s\n3.TTF打印压力\n4.TTF流程打印压力",  
						GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"1.mpos打印压力\n":"",GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper)?"2.X5票据切纸压力":"");
			}	
			switch (returnValue) 
			{	
			case '0':
				printJNIPrecess();
				break;
				
			case '1':
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
					printMposPrecess();
				break;
				
			case '2':// X5设备打印压力添加切纸 add by 20190606
				if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
				{
					gui.cls_show_msg("非X5设备不支持该测试项目,任意键退出");
					break;
				}
				printCutPaperPre();
				break;
			case'3':
				printTTF();
				break;
			case'4':
				//去除切刀判断，N910也可以测切刀指令
//				if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
//				{
//					gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
//					break;
//				}
				printTTF2();
				break;
			
				
			
			case ESC:
				return;
			}
		}
	}

	//TTF流程打印压力
	private void printTTF2() {
		gui.cls_show_msg("请确保已放入打印纸，流程打印压力需要关注是否正常切刀");
		int cnt = 0, bak = 0, succ = 0;
		int prnStatus;
		String cut="*cut\n";
		String feedline="*feedline p:48\n";
		int printerStatus;
		String TTF2data1=
				"*feedline p:48\n"+
				DATA1+
				DATA2+
				"*feedline p:200\n";
		
		String TTF2data2=
				"*feedline p:48\n"+
				"!NLFONT 9 12\n" +
				"*text c 以下打印PNG图片\n" +
				"*image l 384*300 path:/mnt/sdcard/picture/color1.png\n" +
				"*line\n" +
				"*feedline p:200\n";
		
		String TTF2data3=
				DATA1+
				DATA2+
				"*feedline p:200\n"+
				"*cut\n";
		String TTF2data4=
				"!NLFONT 9 12\n" +
				"*text c 以下打印PNG图片\n" +
				"*image l 384*300 path:/mnt/sdcard/picture/color1.png\n" +
				"*line\n" +
				"*feedline p:200\n"+
				"*cut\n";
		
		String TTF2data5=
				"!NLFONT 9 12\n" +
				"*text c 以下打印PNG图片\n" +
				"*image l 576*961 path:/mnt/sdcard/picture/carrefour1.png\n" +
				"*line\n" +
				"*feedline p:200\n"+
				"*cut\n";
		
		String TTF2data6=DATACOMM_SIGN;
		
		String TTF2data7=DATACOMM;
				
				
		bak=cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		while(cnt>0){
			if(gui.cls_show_msg1(2, "正在进行第%d次%s(已成功%d次),[取消]键退出测试", bak-cnt+1, TESTITEM,succ)==ESC)
				break;
			cnt--;
			//单张指令打印切刀
			prnStatus = printUtil.print_byttfScript(TTF2data1);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:单张指令打印失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(cut);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:切刀失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//单张图片打印后切刀
			prnStatus = printUtil.print_byttfScript(TTF2data2);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:单张图片打印失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(cut);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:切刀失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//单张票据切刀
			prnStatus = printUtil.print_byttfScript(TTF2data6);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:单张指令打印失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(cut);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:切刀失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				continue;
			}
			//连续指令打印切刀
			prnStatus = printUtil.print_byttfScript(feedline);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:走纸打印失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			boolean flag=false;
			for (int i = 0; i < 3; i++) {
				
				prnStatus = printUtil.print_byttfScript(TTF2data3);
				if (prnStatus != NDK_OK) 
				{	
					flag=true;
					gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:连续指令打印失败(ret=%d,i=%d)", Tools.getLineInfo(), prnStatus,i);
					continue;
				}
				
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag=true;
					gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
			}
			if (flag) {
				continue;
			}
//			//连续图片打印切刀
//			prnStatus = printUtil.print_byttfScript(feedline);
//			for (int i = 0; i < 3; i++) {
//				prnStatus = printUtil.print_byttfScript(TTF2data4);
//				if (prnStatus != NDK_OK) 
//				{
//					gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:连续图片打印失败(ret=%d,i=%d)", Tools.getLineInfo(), prnStatus,i);
//					continue;
//				}
//				
//			}
			//家乐福图片连续切刀
			prnStatus = printUtil.print_byttfScript(feedline);
			for (int i = 0; i < 3; i++) {
				prnStatus = printUtil.print_byttfScript(TTF2data5);
				if (prnStatus != NDK_OK) 
				{	
					flag=true;
					gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:连续图片打印失败(ret=%d,i=%d)", Tools.getLineInfo(), prnStatus,i);
					continue;
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag=true;
					gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
				
			}
			if (flag) {
				continue;
			}
			//连续票据切刀
			prnStatus = printUtil.print_byttfScript(feedline);
			for (int i = 0; i < 3; i++) {
				prnStatus = printUtil.print_byttfScript(TTF2data7);
				if (prnStatus != NDK_OK) 
				{	
					flag=true;
					gui.cls_show_msg1_record(TAG, "printTTF2", 5,"line %d:连续图片打印失败(ret=%d,i=%d)", Tools.getLineInfo(), prnStatus,i);
					continue;
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag=true;
					gui.cls_show_msg1_record(TAG,"mag_printf",g_keeptime, "line %d:第%d次:获取打印机状态失败(status= %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
					continue;
				}
				
			}
			if (flag) {
				continue;
			}
			succ++;
				
		}
		gui.cls_show_msg1_record(TAG, "printTTF2",g_time_0, "TTF流程打印压力测试完成，已执行次数为%d,成功为%d次", bak-cnt,succ);
		
	}

	//TTF打印压力
	private void printTTF() {
		gui.cls_show_msg("请确保已放入循环打印纸，压力测试无需关注打印效果，关注打印正常即可,按任意键继续");
		String[] dataArray = {DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10};
		int cnt = 0, bak = 0, succ = 0;
		int prnStatus;
		bak=cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		
		while(cnt>0){
			if(gui.cls_show_msg1(2, "正在进行第%d次%s(已成功%d次),[取消]键退出测试", bak-cnt+1, TESTITEM,succ)==ESC)
				break;
			cnt--;
			boolean temflag=false;
			for (int i = 0; i < dataArray.length; i++) 
			{	
				prnStatus = printUtil.print_byttfScript(dataArray[i]);
				switch (prnStatus) {
				case NDK_OK:
					
					break;
					
				case  2:// 缺纸
					gui.cls_show_msg("打印机缺纸，请装纸...完成点任意键继续");
					break;

				default:
					gui.cls_show_msg1_record(TAG, "printTTF", 5, "line %d:第%d次：%s测试失败(%s)", Tools.getLineInfo(),i+1,TESTITEM,prnStatus);
					temflag=true;
					break;
				}
			}
			if (!temflag) {
				succ++;
			}
		}
		gui.cls_show_msg1_record(TAG, "printTTF",g_time_0, "TTF打印压力测试完成，已执行次数为%d,成功为%d次", bak-cnt,succ);
		
	}

	//打印压力
	public void printJNIPrecess() 
	{
		int cnt = 0, bak = 0, succ = 0;
		int prnStatus;
		String funName = Thread.currentThread().getStackTrace()[1].getMethodName();
				
		/*process body*/
		//设置压力次数
		if(GlobalVariable.gSequencePressFlag)
			cnt=getCycleValue();
		else
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		bak= cnt ;
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(3, "正在进行第%d次%s(已成功%d次),[取消]键退出测试", bak-cnt+1, TESTITEM,succ)==ESC)
				break;
			cnt--;
			//注册
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			// 把性能的部分全部都过一遍
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！",Tools.getLineInfo(),prnStatus);
				return;
			}
			
			printUtil.print_png(gPicPath+"IHDR7.png");
		
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例:png,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			
			//打印三角形
			printUtil.print_triangle();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例:三角形,ret=%d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			
			//打印测试页，按行传输
			printUtil.print_testpage();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例:测试页_按行传输,ret=%d)",Tools.getLineInfo(),prnStatus);
				return;
			}
		
			//打印随即数
			printUtil.print_rand();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例:随机数,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印“国”字
			printUtil.print_guo();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例:打印国字,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印填充的
			printUtil.print_fill();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例:填充单,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印票据
			printUtil.print_bill();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：票据，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印竖条纹
			printUtil.print_verticalbill();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：竖条文，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
		
		   printUtil.print_blank();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：空白单,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印各种字体
			printUtil.print_font();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：各种字体,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印基偶回车空白行
			printUtil.print_compress();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例：奇偶回车空白行,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			// 打印压缩
			printUtil.print_compress();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例：压缩,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印联迪单据
			printUtil.print_landi();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：联迪单据,ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印新国都单据
			printUtil.print_xinguodu();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：新国都单据，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			// 打印脚本
			printUtil.print_Script();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：脚本，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//  像素走纸
			printUtil.print_paperByPixel(20);
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：行空白走纸，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
		
			// 行间距
			printUtil.print_row_space();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例：行间距，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			// 字库
			printUtil.print_stock();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例：字库，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			// 测试页-一次性传输
			printUtil.print_testpage_new();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！(当前用例： 测试页-一次性传输，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			// 联迪单项
			printUtil.landi_single();
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例：联迪单项，ret = %d)",Tools.getLineInfo(),prnStatus);
				return;
			}
			//打印ttf脚本,poynt产品不支持ttf打印测试
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
			{
				printUtil.print_ttf_Script();
				if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
					gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:打印机状态异常！(当前用例：ttf脚本打印，ret = %d)",Tools.getLineInfo(),prnStatus);
					return;
				}
			}

			// 判断打印事件是否触发
			if((ret=priEventCheck())!=NDK_OK)
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName,g_keeptime, "line %d:没有监听到打印事件(ret = %d)",Tools.getLineInfo(),ret);
				return;
			}
			// 解绑事件
			if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "printJNIPrecess", g_keeptime, "line %d:printer事件解绑失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "printJNIPrecess", g_time_0,"压力测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
	}
	//mpos打印压力
	public void printMposPrecess(){
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		K21ControllerManager k21ControllerManager;
		K21DeviceResponse response;
		byte[] retContent;
		String retCode;
//		gui.cls_show_msg("请将xdl.png图片放在/data/share/printBitmap/路径下，完成后按任意键继续");
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		Bitmap bit = BitmapFactory.decodeFile("/mnt/sdcard/picture/xdl.png");
		bit = printUtil.gray2Binary(bit);
		int height = bit.getHeight();
		int width = bit.getWidth();
		byte[] imgBuf = printUtil.calcBuffer(bit, height, width);
		int position  = 0;
		byte[] print_1B50=ISOUtils.hex2byte("4DFF000000FFFFFFFFFFFFFFFF2236020500860000");//21
		byte[] print_zuhe=new byte[imgBuf.length+21+1];
		byte[] endByte={0x00};
		System.arraycopy(print_1B50, 0, print_zuhe, 0, print_1B50.length);
		position+=print_1B50.length;
		System.arraycopy(imgBuf, 0, print_zuhe, position, imgBuf.length);
		position+=imgBuf.length;
		System.arraycopy(endByte,0,print_zuhe,position,endByte.length);
		byte[] pack = CalDataLrc.mposPack(new byte[]{0x1B,0x50},print_zuhe);
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(2, "正在进行第%d次%s(已成功%d次),[取消]键退出测试", bak-cnt+1, TESTITEM,succ)==ESC)
				break;
			cnt--;
			//行走纸
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printFeedingHang), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime, "line %d:行走纸测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			//步走纸
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printFeedingBu), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime, "line %d:步走纸测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			//打印字符
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printBuf), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime, "line %d:打印字符测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			//打印图片
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printPicture), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime, "line %d:打印图片测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			
			//打印一维码
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printOneDimensionCode), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime, "line %d:打印一维码测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			//打印二维码
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printTwoDimensionCode), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime, "line %d:打印二维码测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			//打印票据
			response =k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printScript), 10, TimeUnit.SECONDS, null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime,  "line %d:打印票据测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			//打印图片  1B 50
			response =k21ControllerManager.sendCmd(new K21DeviceCommand(pack), 10, TimeUnit.SECONDS, null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "printMposPrecess",g_keeptime,  "line %d:打印图片测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "printMposPrecess", g_time_0,"压力测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
	}
	
	// X5票据切纸压力
	private void printCutPaperPre()
	{
		int cnt = 0, bak = 0, succ = 0;
		int prnStatus;
		String funName = Thread.currentThread().getStackTrace()[1].getMethodName();
				
		/*process body*/
		//设置压力次数
		if(GlobalVariable.gSequencePressFlag)
			cnt=getCycleValue();
		else
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		bak= cnt ;
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(3, "票据切纸压力第%d次%s(已成功%d次),[取消]键退出测试", bak-cnt+1, TESTITEM,succ)==ESC)
				break;
			cnt--;
			if((prnStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, funName, g_keeptime,"line %d:打印机状态异常！",Tools.getLineInfo(),prnStatus);
				return;
			}
			if((ret=JniNdk.JNI_PrnCutterInit())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funName, g_keeptime, "line %d:初始化切刀测试失败(ret = %s)",Tools.getLineInfo(),ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			//打印票据
			printUtil.print_bill();
			if((ret=JniNdk.JNI_PrnCutterPerformance())!=NDK_OK)
			{
				JniNdk.JNI_PrnCutterInit();
				gui.cls_show_msg1_record(TAG, funName, g_keeptime, "line %d:切刀结束测试失败(ret = %s)",Tools.getLineInfo(),ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			JniNdk.JNI_PrnCutterInit();
			succ++;
		}
		gui.cls_show_msg1_record(TAG, funName, g_time_0,"票据切纸压力测试完成，已执行次数为%d,成功为%d次,每次票据打印完毕会自动切刀", bak-cnt,succ);
	}
	
	//单项性能测试
	private void singlefun() 
	{
		long oldTime;
		float diff;
		StringBuffer strBuffer = new StringBuffer();
		if (printUtil.getPrintStatus(MAXWAITTIME)!=EM_PRN_STATUS.PRN_STATUS_OK.getValue()) {
			gui.cls_show_msg1(1, "line %d:打印机状态异常!", Tools.getLineInfo());
			return;
		}
	
		String[] listName = { "0.三角块", "1.测试页-按行传输", "2.随机数", "3.打印国字",
				"4.填充单", "5.票据", "6.竖纹", "7.空白单", "8.模式字", "9.Png",
				"10.压缩","11.联迪单据", "12.新国都", "13.打印脚本", "14.像素走纸",
				"15.行间距", "16.字库", "17.测试页-一次性传输", "18.联迪单项",
				"19.kissbaby面包屋测试页","20.单次ttf脚本打印","21.单次ttf图片打印","22.连续ttf脚本打印","23.连续ttf图片打印","24.单次流程ttf脚本打印","25.单次流程ttf图片打印","26.连续流程ttf脚本打印","27.连续流程ttf图片打印" };
		int line[] = { 0, 20, 0, 22, 0, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				20, 0, 0 ,0,0,0,0,0,0,0,0};
		
	
		int currentPage=1;
		int key=47;
		while(true){
			//注册
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				gui.cls_show_msg1_record(TAG, "singlefun", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++key == 0x45)
				{
					gui.cls_show_msg1_record(TAG, "singlefun", 2, "%s连续性能测试结束", TESTITEM);
					return;
				}
				if(gui.cls_show_msg1(2,"正在进行打印性能连续压力,[取消]退出")==ESC)
					return;
					
			}else{
				switch (currentPage) {
				case 1:
					key=gui.cls_show_msg("打印单项及性能\n0.三角块\n1.测试页-按行传输\n2.随机数\n3.打印国字\n4.填充单\n5.票据\n6.竖纹\n7.美团图片\n8.模式字\n9.Png(上下可翻页)");
					break;
				case 2:
					key = gui.cls_show_msg("打印单项及性能\n0.压缩\n1.联迪单据\n2.新国都\n3.打印脚本\n4.像素走纸\n5.行间距\n6.字库\n7.测试页-一次性传输\n8.联迪单项\n9.kissbaby面包屋测试页(上下可翻页)");
					if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
						key= 10 +key;
					break;
				case 3:
					key = gui.cls_show_msg("打印单项及性能\n0.单次ttf脚本打印\n1.单次ttf图片打印\n2.连续ttf脚本打印\n3.连续ttf图片脚本打印\n4.单次流程ttf脚本打印\n5.单次流程ttf图片打印\n6.连续流程ttf脚本打印\n7.连续流程ttf图片打印");
					if(key!=ESC && key!=KEY_DOWN && key!=KEY_UP)
						key= 20 +key;
					break;
				default:
					break;
				}
			}
				
			oldTime = System.currentTimeMillis();
			switch (key) 
			{
			case '0': // 打印三角形
				printUtil.print_triangle();
				break;
			case '1': // 打印测试页
				printUtil.print_bill_add_feeding();
//				printUtil.print_testpage();
				break;
			case '2': // 打印随即数
				printUtil.print_rand();
				break;
			case '3': // 打印国字
				printUtil.print_guo();
				break;
			case '4': // 打印填充单
				printUtil.print_fill();
				break;
			case '5': // 打印票据
				printUtil.print_bill();
				break;
			case '6': // 打印竖条纹
				printUtil.print_verticalbill();
				break;
			case '7': // 打印空白单
				printUtil.print_blank();
				break;
			case '8': // 打印各种字体
				printUtil.print_font();
				break;
			case '9': // 打印png
				printUtil.print_png(gPicPath+"IHDR7.png");
				break;
			case 0x3A: // 打印奇偶回车空白行
				printUtil.print_compress();
				break;
			case 0x3B: // 打印联迪单据
				printUtil.print_landi();
				break;
			case 0x3C: // 打印新国都单据
				printUtil.print_xinguodu();
				break;
			case 0x3D: // 打印脚本
				printUtil.print_Script();
				break;

			case 0x3E: // 像素空白走纸
				printUtil.print_paperByPixel(20);
				break;
//
//			case 'F': // 列空白走纸
//				printUtil.print_paperS();
//				break;

			case 0x3F: // 行间距
				printUtil.print_row_space();
				break;

			case 0x40: // 打印字库
				printUtil.print_stock();
				break;

			case 0x41: // 测试页-一次性传输
				printUtil.print_testpage_new();
				break;

			case 0x42:
				printUtil.landi_single();
				break;
			case 0x43:
				printUtil.print_kissbaby();
				break;
			case 0x44:
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
					printUtil.print_ttf_Script();
				break;
			case 0x45:
					printUtil.print_byttfScript(DATAPIC_SIGN);
				break;
			case 0x46:
				for (int i = 0; i < 3; i++) {
					printUtil.print_ttf_Script();
				}
				break;
			case 0x47:
				for (int i = 0; i < 3; i++) {
					printUtil.print_byttfScript(DATAPIC_SIGN);
				}
				break;
			case 0x48:
//				if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
//				{
//					gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
//					break;
//				}
				printUtil.print_byttfScript(DATACOMM_SIGN);
				printUtil.print_byttfScript(CUT_TEST);
				break;
			case 0x49:
//				if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
//				{
//					gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
//					break;
//				}
				printUtil.print_byttfScript(DATAPIC_SIGN);
				printUtil.print_byttfScript(CUT_TEST);
			break;
			case 0x4A:
//				if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
//				{
//					gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
//					break;
//				}
				for (int i = 0; i < 3; i++) {
					if (i==0) {
						printUtil.print_byttfScript(FEEDLINE);
					}
					printUtil.print_byttfScript(DATACOMM);
				}
			
			break;
			
			case 0x4B:
//				if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
//				{
//					gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
//					break;
//				}
				for (int i = 0; i < 3; i++) {
					if (i==0) {
						printUtil.print_byttfScript(FEEDLINE);
					}
					printUtil.print_byttfScript(DATAPIC);
				}
			
			break;
			
			case ESC:
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				return;
			case KEY_DOWN:
				if(currentPage++>=3)
					currentPage=1;
				break;
			case KEY_UP:
				if(currentPage--<=1)
					currentPage=3;
				break;
			}
			if(key != KEY_DOWN && key !=KEY_UP)
			{
				if (printUtil.getPrintStatus(MAXWAITTIME)!=EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
				{
					gui.cls_show_msg1(1, "line %d:打印机状态异常！", Tools.getLineInfo());
					UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
					return;
				}
				diff = Tools.getStopTime(oldTime);
				LoggerUtil.d("key:"+key);
				int num=key-0x30;
				LoggerUtil.d("num:"+num);
				strBuffer.append(listName[num] + "打印执行时间:" +  Tools.getDecimal2(diff) + "s\n");
				if (line[num] > 0) 
				{
					strBuffer.append("打印速度:" + Tools.getDecimal2(line[num] / diff) + "行/s");
				}
				
//				gui.cls_show_msg(strBuffer.append(",任意键继续").toString());
				gui.cls_show_msg1_record(TAG, "singlefun",g_time_0, strBuffer.append(",任意键继续").toString(),ret);
				strBuffer.delete(0, strBuffer.length());
				if((ret=priEventCheck())!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "singlefun",g_keeptime, "line %d:没有监听到打印事件(ret = %d)",Tools.getLineInfo(),ret);
				}
			}
		
			// 解绑事件
			if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "singlefun", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			
		}
		
	}

	
	// add by 20160603
	// 打印异常
	public void printAbnormal()
	{
		int returnValue=gui.cls_show_msg("打印性能、压力\n0.缺纸\n1.过热\n2.打印空走纸和卡顿检测\n3.打印填充单缺纸后打印票据\n4.TTF切刀异常测试\n5.TTF连续打印切刀异常测试\n6.TTF票据连续打印");// 第三点根据客诉增加 add by 20190606 
																																							//第六点根据开发需求增加，验证压测打印票据是否卡顿    by20200610
		switch (returnValue) 
		{
		case '0':
			outOfPaper();
			break;
			
		case '1':
			headLimited();
			break;
			
		case '2':
			emptyPaperCaton();
			break;
			
		case '3':
			fillOutPaparBill();
			break;
		case '4':
			if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
			{
				gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
				break;
			}
			cutabnormal();
			break;
		case '5':
			if(!GlobalVariable.gModuleEnable.get(Mod_Enable.CutPaper))
			{
				gui.cls_show_msg("无切刀功能设备不支持该测试项目,任意键退出");
				break;
			}
			cutabnormal2();
			break;
			
		case '6':	
			TTFabnormaltest();
			break;
		
//			3.未监听到事件机制
//		case '3':
//			noPriEvent();
//			break;
		default:
			break;
		}
	}
	
	private void TTFabnormaltest() {
		// TODO Auto-generated method stub
		int cnt = 0, bak = 0, succ = 0;
		int prnStatus;
		//测试人员需求，打印一个长票据验证是否卡顿
		String data=
				"*feedline p:48\n" +
				"!NLFONT 3 3 3\n"+
				"!yspace 9\n"+
				"*text l kissbaby面包屋（名城店）\n"+
				"!NLFONT 1 12 3\n"+
				"*line\n"+
				"*text l 商户实收：                   ￥5\n"+
				"*line\n"+
				"*text l 商户实付：                   ￥5\n"+
				"*line\n"+
				"*text l 支付方式：                支付宝\n"+
				"*line\n"+
				"!yspace 6\n"+
				"*text l 交易单号：2017020821001004920238\n" +
				"*text r 392810\n"+
				"*text l 商户名称：kissbaby面包屋（名城店\n"+
				"*text r ）\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 凑行数---------------\n"+
				"*text l 设备号：                83902148\n"+
				"*text l 交易时间：   2017/02/08 18:19:32\n"+
				"*text l 交易状态：              付款成功\n"+
				"!yspace 9\n"+
				"*line\n"+
				"!yspace 6\n"+
				"*text l 打印时间：      2017-02-08 18:19\n"+
				"*text l 备注：\n"+
				"*line\n"+
				"!yspace 0\n"+// 将行间距修改为打印初始化值
				"*feedline p:200\n" +
				"*cut\n";;
		/*process body*/
		//设置压力次数
		bak=cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		
		while (cnt>0) {
			if(gui.cls_show_msg1(2, "正在进行第%d次%s(已成功%d次),[取消]键退出测试", bak-cnt+1, TESTITEM,succ)==ESC)
				break;
			cnt--;
			prnStatus = printUtil.print_byttfScript(data);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "TTFabnormaltest", 5,"line %d:票据打印失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			succ++;
			
		}

		gui.cls_show_msg1_record(TAG, "TTFabnormaltest", g_time_0,"压力测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
		
	}
	// end by 20160603
	
	//连读打印切刀异常测试
	private void cutabnormal2() {
		int prnStatus;
		//走纸
		String feedline="*feedline p:48\n";
		//票据
		String datacomm2 =DATACOMM;
				
		//图片
		String datapic2=
				"!NLFONT 9 12\n" +
				"*text c 以下打印PNG图片\n" +
				"*image l 384*300 path:/mnt/sdcard/picture/color1.png\n" +
				"*line\n" +
				"*feedline p:200\n"+
				"*cut\n";

		gui.cls_show_msg("请放入打印纸，下面将进行连续打印切刀，切刀效果应正常，不应切错,按任意键继续");
		
		//连续打印3次并且切刀
		for (int i = 0; i < 3; i++) {
			//第一次需要走纸48像素
			if (i==0) {
				prnStatus = printUtil.print_byttfScript(feedline);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
				}
			}
			prnStatus = printUtil.print_byttfScript(datacomm2);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
			}
			prnStatus = printUtil.print_byttfScript(datapic2);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
			}
			prnStatus = printUtil.print_byttfScript(DATAPIC);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
			}
		}
		gui.cls_show_msg1_record(TAG, "cutabnormal2", 10,"连续打印切纸异常测试通过");	
	}

	//切刀异常测试  by chend 20200528
	private void cutabnormal() {
		int prnStatus;
		int Outpaper=2;   //缺纸返回值为2
		String cut="*cut\n";
		String datatest ="*feedline p:48\n"+DATACOMM;
		//case1.打印前缺纸进行切刀
		gui.cls_show_msg("请取出打印纸，构造缺纸状态，完成点击任意键");
		prnStatus = printUtil.print_byttfScript(cut);
		if (prnStatus != Outpaper) 
		{
			gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:切刀异常测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
		}
		//异常测试后执行一次正常打印和切刀
		gui.cls_show_msg("请放入打印纸，应能正常打印，且切刀正常。完成点击任意键");
		
		prnStatus = printUtil.print_byttfScript(datatest);
		if (prnStatus != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:正常打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
		}
		
		//case2.打印过程中缺纸进行切刀
		gui.cls_show_msg("请放入少量打印纸，构造打印过程中缺纸状态，完成点击任意键");
		String temdata[]={DATA1,DATA2,DATA3,DATA4,DATA5};
		for (int i = 0; i < temdata.length; i++) {
			prnStatus = printUtil.print_byttfScript(temdata[i]);
			if (prnStatus == Outpaper) 
			{
				//打印过程中缺纸，进行切刀
				prnStatus = printUtil.print_byttfScript(cut);
				if (prnStatus!=Outpaper) {
					gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:切刀异常测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
				}
				
			}
		}
		//异常测试后执行一次正常打印和切刀
		gui.cls_show_msg("请放入打印纸，应能正常打印，且切刀正常。完成点击任意键");
		
		prnStatus = printUtil.print_byttfScript(datatest);
		if (prnStatus != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:正常打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
		}
		
		//case3.打印完成后缺纸进行切刀
		gui.cls_show_msg("请放入打印纸。完成点击任意键");
		for (int i = 0; i < temdata.length; i++) {
			prnStatus = printUtil.print_byttfScript(temdata[i]);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:正常打印测试失败(ret=%d，i=%d)", Tools.getLineInfo(), prnStatus,i);	
			}
		}
		gui.cls_show_msg("打印完毕，请取出打印纸，构造打印完毕后缺纸。完成点击任意键");
		prnStatus = printUtil.print_byttfScript(cut);
		if (prnStatus != Outpaper) 
		{
			gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:切刀异常测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
		}
		gui.cls_show_msg("异常测试完毕请放入打印纸。完成点击任意键");
		//异常测试后执行一次正常打印和切刀
		prnStatus = printUtil.print_byttfScript(datatest);
		if (prnStatus != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cutabnormal", 5,"line %d:正常打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);	
		}
		
		gui.cls_show_msg1_record(TAG, "cutabnormal", 10,"切纸异常测试通过");	
	}

	// 缺纸状态
	public void outOfPaper() 
	{
		int printerStatus=-1;
		gui.cls_show_msg("请放置少量的打印纸打印脚本，构造缺纸状态，完成点击是");
		int i =3;
		//打印机状态从busy到缺纸状态改变会触发打印事件
		//注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "outOfPaper", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(i>0)
		{
			printUtil.print_bill();
//			Printer printer = n900Device.getPrinter();
			printerStatus = printUtil.getPrintStatus(MAXWAITTIME);
			if(printerStatus==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
			{
				gui.cls_show_msg1(g_keeptime, "打印机已缺纸，即将退出");
				break;
			}
			i--;
		}
		if(printerStatus!=EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:获取缺纸状态失败(status = %d)", Tools.getLineInfo(),printerStatus);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			return;
		}
		if((ret=priEventCheck())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "headLimited",g_keeptime, "line %d:没有监听到打印事件(ret = %d)",Tools.getLineInfo(),ret);
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "outOfPaper", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1(2, "异常测试通过");
	}
	
	// 过热状态
	public void headLimited()
	{
		int printerStatus ;
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < 5; i++) 
		{
			strBuffer.append("*image l 384*300 path:yz:50;"+gPicPath+"other2.png\n");
		}
		//注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "headLimited", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(true)
		{
			if(gui.cls_show_msg1(3, "打印过热测试中，[取消]键退出测试")==ESC)
				break;

			printUtil.print_byScript(strBuffer.toString());
			printerStatus = printUtil.getPrintStatus(0);
			if(printerStatus ==EM_PRN_STATUS.PRN_STATUS_OVERHEAT.getValue() )
			{
				gui.cls_show_msg1(g_keeptime, "打印机已过热，即将退出");
				break;
			}
			else if(printerStatus==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
			{
				gui.cls_show_msg("打印缺纸，请重新放入打印纸，完成任意键继续");
			}
		}
		if((ret=priEventCheck())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "headLimited",g_keeptime, "line %d:没有监听到打印事件(ret = %d)",Tools.getLineInfo(),ret);
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "headLimited", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		
	}
	//打印空走纸和卡顿问题
	public void emptyPaperCaton(){
		int printerStatus;
		int i=0;
		String message = "请准备好电量为70%左右的电池(电压不得超过8V!)进行测试，因测试次数较大，建议采用循环打印纸测试。注意：不得接入外电，测试过程未出现\"打印空走纸\"或\"卡顿\"，则为测试通过；空走纸和卡顿出现后需要重启设备";
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			return;
		}
		//注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "emptyPaperCaton", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(i<=1000)
		{
			if(gui.cls_show_msg1(3, "打印空走纸和卡顿测试中，[取消]键退出测试")==ESC)
				break;

			i++;
			printUtil.printOnlineOrder();
			//休眠5s，试验中发现3s容易出现报文信息无法返回，导致设备打印部分无法使用，故休眠5s
			SystemClock.sleep(5*1000);
			printerStatus = printUtil.getPrintStatus(0);
			if(printerStatus==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
			{
				gui.cls_show_msg("打印缺纸，请重新放入打印纸，完成点击任意键");
			}
		}
		if((ret=priEventCheck())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "emptyPaperCaton",g_keeptime, "line %d:没有监听到打印事件(ret = %d)",Tools.getLineInfo(),ret);
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "emptyPaperCaton", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1(2, "打印空走纸和卡顿测试通过，未检测到空走纸和卡顿！");	
	}
	
	public void noPriEvent(){
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
		{
			gui.cls_show_msg1(1, "poynt产品不支持该测试项，任意键退出");
			return;
		}
		int i=1;
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "headLimited", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(i<=3)
		{
			printUtil.print_ttf_Script();
			if (printUtil.getPrintStatus(MAXWAITTIME)!=EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
			{
				gui.cls_show_msg1(1, "line %d:打印机状态异常！", Tools.getLineInfo());
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				return;
			}
			SystemClock.sleep(2000);
			i++;
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "emptyPaperCaton", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
	}
	
	// 循环打印填充单等待缺纸后放纸打印票据 add by 20190606
	private void fillOutPaparBill()
	{
		int printerStatus ;
		String funName = Thread.currentThread().getStackTrace()[1].getMethodName();
		
		gui.cls_show_msg("无限循环打印填充单，直到缺纸后退出，再打印票据，预期票据第一行无黑线。按任意键继续");
		//注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			gui.cls_show_msg1_record(TAG, "headLimited", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(true)
		{
			printUtil.print_fill();// 打印填充单
//			printerStatus = printUtil.getPrintStatus(0);
//			if(printerStatus ==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue() )
//			{
//				gui.cls_show_msg("打印机已缺纸,请重新装纸,即将打印票据,装纸完毕点任意键继续");
//				break;
//			}
			printerStatus=printUtil.getPrintStatusOri();
			Log.d("eric_chen", "printerStatus :"+printerStatus);
			if (printerStatus==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue()) {
				gui.cls_show_msg("打印机已缺纸,请重新装纸,即将打印票据,装纸完毕点任意键继续");
				break;
			}
		}
		printUtil.print_bill();// 打印票据
		gui.cls_show_msg1_record(TAG, funName, g_time_0,"打印的票据第一行无打印一条黑线才可视为通过");
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "headLimited", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
	}
	
}
