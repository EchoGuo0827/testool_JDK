package com.example.highplattest.printer;

import android.util.Log;
import java.util.Locale;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer3.java 
 * Author 			: zhangxinj
 * DATE 			: 20170705
 * description 		: ttf打印字体测试
 * history 		 	: 变更记录			变更人员			变更时间
 *			  		 增加TTF打印案例			陈丁				20200426
 *					增加阿拉伯语的case		郑薛晴			20200515
 *					去除替换打印库的提示语		陈丁					20200710
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer3 extends UnitFragment
{
	private final String TESTITEM = "ttf字库打印";
	private final int PRN_HZ_FONT = ParaEnum.HZ_FONT.values().length;
	private final int PRN_ZM_FONT = ParaEnum.ZM_FONT.values().length;
	private final String HZ_ADD = "!NLFONT 1 12 0\n*text c 以横向放大2倍、纵向放大2倍PRN_HZ_FONT_24x24字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n"+
			"!NLFONT 2 12 1\n*text c 以横向放大2倍、纵向正常PRN_HZ_FONT_16x32字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n"+
			"!NLFONT 3 12 2\n*text c 以横向正常、纵向放大2倍PRN_HZ_FONT_32x32字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n"+
			"!NLFONT 1 12 4\n*text c 以横向放大3倍、纵向放大3倍PRN_HZ_FONT_24x24字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n"+
			"!NLFONT 2 12 5\n*text c 以横向放大3倍、纵向正常PRN_HZ_FONT_16x32字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n"+
			"!NLFONT 3 12 6\n*text c 以横向正常、纵向放大3倍PRN_HZ_FONT_32x32字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n";
	private final String ZM_ADD = "!NLFONT 1 1 0\n*text c 以西文横向放大2倍、纵向放大2倍PRN_ZM_FONT_8x16字体居中print dollar symbol：$$\n*line\n*feedline 1\n"+
			"!NLFONT 1 2 1\n*text c 以西文横向放大2倍、纵向正常PRN_ZM_FONT_16x16字体居中print dollar symbol：$$\n*line\n*feedline 1\n"+
			"!NLFONT 1 3 2\n*text c 以西文横向正常、纵向放大2倍PRN_ZM_FONT_16x32字体居中print dollar symbol：$$\n*line\n*feedline 1\n"+
			"!NLFONT 1 1 4\n*text c 以西文横向放大3倍、纵向放大3倍PRN_ZM_FONT_8x16字体居中print dollar symbol：$$\n*line\n*feedline 1\n"+
			"!NLFONT 1 2 5\n*text c 以西文横向放大3倍、纵向正常PRN_ZM_FONT_16x16字体居中print dollar symbol：$$\n*line\n*feedline 1\n"+
			"!NLFONT 1 3 6\n*text c 以西文横向正常、纵向放大3倍PRN_ZM_FONT_16x32字体居中print dollar symbol：$$\n*line\n*feedline 1\n";
	private String[][] ttf={
			{"系统默认英文字体","DroidSans.ttf","DroidSansDroidSans"},
			{"系统默认英文粗字体","DroidSans-Bold.ttf","DroidSansDroidSans"},
			{"系统默认中英文字体","DroidSansFallback.ttf","新大陆支付技术有限公司newlandpatment"},
			{"DroidaSansMono字体","DroidSansMono.ttf","newlandpatmentcompany"},  
			{"DancingScript粗字体","DancingScript-Bold.ttf","newlandpatmentcompany"},
			{"DancingScript常规字体","DancingScript-Regular.ttf","newlandpatmentcompany"},
			{"Roboto常规（英文）","Roboto-Regular.ttf","newlandpatmentcompany"},
			{"Roboto粗体（英文）","Roboto-Bold.ttf","newlandpatmentcompany"},
			{"Roboto粗斜体（英文）","Roboto-BoldItalic.ttf","newlandpatmentcompany"},
			{"Roboto斜体（英文）","Roboto-Italic.ttf","newlandpatmentcompany"},
			{"Roboto细字体（英文）","Roboto-Thin.ttf","newlandpatmentcompany"},
			{"Roboto细斜体（英文）","Roboto-ThinItalic.ttf","newlandpatmentcompany"},
			{"RobotoCondensed常规（英文）","RobotoCondensed-Regular.ttf","newlandpatmentcompany"},
			{"RobotoCondensed粗体（英文）","RobotoCondensed-Bold.ttf","newlandpatmentcompany"},
			{"RobotoCondensed粗斜体（英文）","RobotoCondensed-BoldItalic.ttf","newlandpatmentcompany"},
			{"RobotoCondensed细体（英文）","RobotoCondensed-Light.ttf","newlandpatmentcompany"},
			{"RobotoCondensed细斜体（英文）","RobotoCondensed-LightItalic.ttf","newlandpatmentcompany"},
			{"NotoSerif粗斜体（英文）","NotoSerif-BoldItalic.ttf","newlandpatmentcompany"},
			{"NotoSerif粗体（英文）","NotoSerif-Bold.ttf","newlandpatmentcompany"},
			{"NotoSerif斜体（英文）","NotoSerif-Italic.ttf","newlandpatmentcompany"},
			{"NotoSerif常规（英文）","NotoSerif-Regular.ttf","newlandpatmentcompany"},
			{"系统默认中英文字体，打印两横RMB符号","DroidSansFallback.ttf","￥￥￥￥￥￥￥￥￥￥￥"},
			{"NotoSerif常规字库，打印打印美元符号、欧元符号、英镑符号、瑞士符号、印尼盾符号、新基普符号","NotoSerif-Regular.ttf","$$€€££FrFr₨₨₭₭"},
			{"泰语字库（常规），打印泰铢符号","NotoSansThaiUI-Regular.ttf","฿฿฿฿฿฿฿฿฿฿฿฿฿฿฿฿฿฿"},
			{"老挝语字库（常规），打印老挝语","NotoSansLao-Regular.ttf","ສາທາລະນະລັດ ປະຊາທິປະໄຕ ປະຊາຊົນລາວ"},
			/*A7没有/system/fonts/NotoSansDevanagariUI-Regular.ttf文件*/
			{"印度语字库（常规），打印印度语","NotoSansDevanagariUI-Regular.ttf","भारत गणराज्य"},// 该字库X5产品无
			{"系统默认中英文字体，内容：恢复默认字体","DroidSansFallback.ttf","恢复默认字体"}};
	private String fileName=Printer3.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void printer3()
	{
		String funcName="printer3";
//		/**X5产品需要替换libnlprintex.so*/
//		if(GlobalVariable.currentPlatform==Model_Type.X5)
//		{
//			if(gui.cls_show_msg("X5设备需要使用服务器上的libnlprintex.so替换HighPlattest工程下libs目录下的全部libnlprintex.so，服务器路径为TestCase/高端/testool_JDK/others/ttf/X5_TTF专用的So,将64位和32位全部替换，已完成任意键继续测试，ESC键退出")==ESC)
//			{
//				unitEnd();
//				return;
//			}
//		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.unitPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s【iransans.ttf和DroidSansFallback.ttf文件放到/sdcard/picture目录下】,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		// 连接设备
		PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
		
		int printerResult;
		
		Log.e("start hz", PRN_HZ_FONT+"  "+PRN_ZM_FONT);
		if((printerResult=printUtil.getPrintStatus(MAXWAITTIME))!= EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:打印机状态异常(status=%d)", Tools.getLineInfo(),printerResult);
			return;
		}
		//case4 异常测试!font命令：传入不存在文件，返回-7
		String errString="!font err.ttf\n!";
		if((printerResult = printUtil.print_byttfScript(errString))!=NDK_PRINT_FILE_NOT_FIND){
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,printerResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case5 异常测试!font命令 ：传入其他路径ttf文件
		String otherPathString="!font /sdcard/picture/DroidSansFallback.ttf\n*text l 打印其他路径下ttf文件，本行文字要成功打出\n*line\n*feedline 1\n";
		if((printerResult = printUtil.print_byttfScript(otherPathString))!=NDK_OK){
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印sdcard下的%s字库测试失败（%s）", Tools.getLineInfo(),"DroidSansFallback.ttf",printerResult);
			if(!GlobalVariable.isContinue)
				return;
		}
//		{"阿拉伯语字库，打印阿拉伯语：你好，阿拉伯",".ttf","مرحبا عرب"}/*新增打印阿拉伯语*/};
		
		// case6:新增阿拉伯语测试 20200515
		String dataTip = "!NLFONT 1 12 3\n*text l 以下打印多行阿拉阿伯语，共五行内容\n";
		if((printerResult = printUtil.print_byttfScript(dataTip))!=NDK_OK){
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印提示语失败（%s）", Tools.getLineInfo(),printerResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		String arabString="!font /sdcard/picture/iransans.ttf\n!NLFONT 2 10 2\n*text r يرجي الاحتفاظ بالايصال\n"+"!NLFONT 2 10 2\n*text r شكرًا لاستخدامكم مدى\n"+"!NLFONT 2 10 2\n*text r شكرًا لاستخدامكم مدى\n"
        +"!NLFONT 2 10 2\n*text r شكرًا لاستخدامكم مدى\n"+"!NLFONT 2 10 2\n*text r يرجي الاحتفاظ بالايصال\n";
		if((printerResult = printUtil.print_byttfScript(arabString))!=NDK_OK){
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印sdcard下iransans.ttf的阿拉伯语测试失败（ret=%s）", Tools.getLineInfo(),printerResult);
			if(!GlobalVariable.isContinue)
				return;
		}
	
		// case1:进行各种中西文字体测试，用于测试字库，add by zhengxq
		for (int i = 1; i <=PRN_HZ_FONT; i++) 
		{
			String data;
			// 在打印粗体之后打印正常字体的横向放大，纵向正常
			if(ParaEnum.HZ_FONT.values()[i-1].toString().contains("BL"))
				data = String.format(Locale.CHINA,"!NLFONT %d 12 3\n*text c 以%s字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n"+HZ_ADD,i,ParaEnum.HZ_FONT.values()[i-1]);
			else{
				data = String.format(Locale.CHINA,"!NLFONT %d 12 3\n*text c 以%s字体居中打印两横RMB符号￥￥\n*line\n*feedline 1\n",i,ParaEnum.HZ_FONT.values()[i-1]);
			}
			if((printerResult = printUtil.print_byttfScript(data))!=NDK_OK)
			{
				// 缺纸
				if(printerResult==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
				{
					gui.cls_show_msg1_record(fileName, funcName, 0,  "打印机缺纸，装纸后点击是继续测试");
				}
				else
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:打印字体=%s，测试失败（ret=%d）", Tools.getLineInfo(),ParaEnum.HZ_FONT.values()[i-1].toString(),printerResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		// case2:打印剩余的西文字体，用于测试字库（增加其他国家货币符号测试，by zhangxj 20180312）【ttf字库的西文字体只支持到38 by 20200324 开发许世杰】
		for (int i = 1; i <=38; i++) 
		{
			String data1;
//			String data2;
//			String data3;
//			String data4;
//			String data5;
//			String data6;
//			String data7;
//			String data8;
			if(ParaEnum.ZM_FONT.values()[i-1].toString().contains("BL")){
				data1 = String.format(Locale.CHINA,"!NLFONT 1 %d 3\n*text c 以西文%s字体居中（美元）print dollar symbol：$$\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
				//data2 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（欧元）print Euro symbol：€€\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
//				data3 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（伊朗）print Euro symbol：﷼﷼\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
//				data4 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（英镑）print Pound sterling symbol：££\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
//				data5 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（印尼盾）print Indonesian rupiah symbol：₨₨\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
//				data6 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（瑞士）print Swiss franc symbol：FrFr\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
//				data7 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（泰铢）print Thai baht symbol：฿฿\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
//				data8 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（新基普）print Lao kip symbol：₭₭\n*line\n*feedline 1\n"+ZM_ADD,i,ParaEnum.ZM_FONT.values()[i-1]);
			}
			else{
				data1 = String.format(Locale.CHINA,"!NLFONT 1 %d 3\n*text c 以西文%s字体居中（美元）print dollar symbol：$$\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
				//data2 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（欧元）print Euro symbol：€€\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
//				data3 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（伊朗）print Iranian rial symbol：﷼﷼\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
//				data4 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（英镑）print Pound sterling symbol：££\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
//				data5 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（印尼盾）print Indonesian rupiah symbol：₨₨\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
//				data6 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（瑞士）print Swiss franc symbol：FrFr\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
//				data7 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（泰铢）print Thai baht symbol：฿฿\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
//				data8 = String.format("!NLFONT 1 %d 3\n*text c 以西文%s字体居中（新基普）print Lao kip symbol：₭₭\n*line\n*feedline 1\n",i,ParaEnum.ZM_FONT.values()[i-1]);
			}
		//	String[] printData={data1,data2,data3,data4,data5,data6,data7,data8};
			String[] printData={data1};
			for(int j=0;j<printData.length;j++)
			{
				if((printerResult = printUtil.print_byttfScript(printData[j]))!=NDK_OK)
				{
					// 缺纸
					if(printerResult==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
					{
						gui.cls_show_msg1_record(fileName, funcName, 0,"打印机缺纸，装纸后点击是继续测试");
						if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull){
							return;
						}
					}
					else
						gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:打印字体=%s,测试失败(ret=%s)", Tools.getLineInfo(),ParaEnum.ZM_FONT.values()[i-1].toString(),printerResult);
						if(!GlobalVariable.isContinue)
							return;
				}
			}
		}
		//case3  新增!font命令，用于加载/system/fonts/目录下的字体 20180703 zhangxinj
		for (int i = 0; i <ttf.length; i++) 
		{
			//X5设备不带NotoSansDevanagariUI-Regular.ttf字体 by 20200324 zhengxq,A7没有/system/fonts/NotoSansDevanagariUI-Regular.ttf这个文件 by 20200518 GlobalVariable.currentPlatform==Model_Type.X5||
			if(ttf[i][1].equals("NotoSansDevanagariUI-Regular.ttf")&&(GlobalVariable.gCurPlatVer==Platform_Ver.A7||GlobalVariable.gCurPlatVer==Platform_Ver.A9))
				continue;
			String ttfString= String.format("!font DroidSansFallback.ttf\n!NLFONT 9 12 3\n*text l 以下设置为%s\n!font %s\n!NLFONT 9 12 3\n*text l %s\n*feedline 1\n",ttf[i][0],ttf[i][1],ttf[i][2]);

			if((printerResult = printUtil.print_byttfScript(ttfString))!=NDK_OK)
			{
				// 缺纸
				if(printerResult==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
				{
					gui.cls_show_msg1_record(fileName, funcName, 0,"打印机缺纸，装纸后点击是继续测试");
				}
				else
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s测试失败(ttf=%s,result=%d)", Tools.getLineInfo(),TESTITEM,ttf[i][1],printerResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"打印效果与预期提示语一致");
	}
	@Override
	public void onTestUp() {}
	
	@Override
	public void onTestDown() {}
}
