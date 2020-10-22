package com.example.highplattest.printer;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer5.java 
 * history 		 	: 变更记录						变更时间					变更人员
 *			  		 X5新增*cut的切刀命令			20200514				郑薛晴
 *					打印过程中切刀case，在切刀前添加走纸        20200611 				陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer8 extends UnitFragment
{
	private final String TESTITEM = "*cut命令方式切刀(X5)";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Printer5.class.getSimpleName();
	PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
	int height,width,printerResult;
	
	public void printer8()
	{
		String funcName="printer5";
		/**目前该案例只有X5支持*/
		if(GlobalVariable.currentPlatform!=Model_Type.X5)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"%s设备不支持切刀测试",GlobalVariable.currentPlatform);
			return;
		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.unitPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		
		// case1:连续打印图片过程中每张图片都执行切刀操作，使用NDK的切刀方式
		String[] arrayWay = {"carrefour1","carrefour2","carrefour2","carrefour2"};
		long startTime = System.currentTimeMillis();
		LoggerUtil.d("CPOS X5,Case1 Print Start");
		for (int i = 0; i < 4; i++) 
		{
			gui.cls_show_msg2(0.5f,"case1:正在第%d次打印%s图片中====",i+1,arrayWay[i]);
			String picPath = Environment.getExternalStorageDirectory().getPath() + "/picture/" + arrayWay[i] + ".png";
			Bitmap bit = BitmapFactory.decodeFile(picPath);

			width = bit.getWidth();
			height = bit.getHeight();
			if(i==0)
			{
				printerResult = printUtil.print_byttfScript(String.format("*feedline p:48\n"));
				if (printerResult != NDK_OK) {
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次走纸失败(ret=%d)", Tools.getLineInfo(),i + 1, printerResult);

				}
			}
			printerResult = printUtil.print_byttfScript(String.format("!NLFONT 9 12 3\n*text c 以下打印PNG图片[使用NDK切刀接口]\n*image l %s path:%s\n*line\n*feedline p:200\n",width + "*" + height, picPath));
			if (printerResult != NDK_OK) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s图片打印失败(ret=%d)", Tools.getLineInfo(),arrayWay[i], printerResult);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg2(0.5f, "case1:票据切刀中===");
			// 切刀操作
			printerResult = printUtil.print_byttfScript(String.format("*cut\n"));
			if (printerResult != NDK_OK) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s图片切刀失败(ret=%d)", Tools.getLineInfo(),arrayWay[i], printerResult);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		LoggerUtil.d("CPOS X5,Case1 Print End="+(System.currentTimeMillis()-startTime)+"ms");
		
		// case4 ：连续图片切纸，使用*cut指令方式\n
		String datapic_prn8 = "!NLFONT 9 12\n*text c 以下打印PNG图片[使用*cut指令方式]\n*image l 576*1126 path:/mnt/sdcard/picture/carrefour2.png\n*line\n*feedline 1\n"
				+ "*feedline p:200\n" + "*cut\n";
		
		String datafeed = "*feedline p:48\n";
		for (int i = 0; i < 3; i++) {
			gui.cls_show_msg2(0.5f, "case4:正在第%d次打印票据", i + 1);
			if (i == 0) {
				printerResult = printUtil.print_byttfScript(datafeed);
				if (printerResult != NDK_OK) {
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次走纸失败(ret=%d)", Tools.getLineInfo(),i + 1, printerResult);

				}
			}
			printerResult = printUtil.print_byttfScript(datapic_prn8);
			if (printerResult != NDK_OK) {
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次打印图片失败(ret=%d)", Tools.getLineInfo(),i + 1, printerResult);
			}
		}
		
		// case2:连续打印三张票据，每张票据都执行切刀操作
		startTime = System.currentTimeMillis();
		LoggerUtil.d("CPOS X5,Case2 Print Start");
		String data = 
				"*feedline p:48\n"+
				"!NLFONT 9 12 3\n"+
				"!yspace 6\n"+
				"*text l 备份电池：4.333V\n"+
				"*text l 电池供电：7.666666V\n"+
				"*text l 打印浓度：9\n"+
				"*text l WELCOME TO POS EQUITPMENT\n"+
				"*text l 国国国国国国国国国国国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n*feedline 1\n"+
				"*text l WELCOME TO POS EQUITPMENT\n"+
				"*text l 国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n"+
				"*text l 国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n*feedline 1\n"+
				"*text l WELCOME TO POS EQUITPMENT\n"+
				"*text l 国国国国国国\n"+
				"*text l 国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n"+
				"*text l 国国国国国国国国国国国国国国国国\n"+
				"!yspace 0\n"+//将行间距设置为打印初始化值0
				"*feedline p:200\n";
		for (int i = 0; i < 3; i++) 
		{
			gui.cls_show_msg2(0.5f,"case2:正在第%d次打印票据",i+1);
			printerResult = printUtil.print_byttfScript(data);
			if (printerResult != NDK_OK) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次打印票据失败(ret=%d)", Tools.getLineInfo(), i+1,printerResult);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg2(0.5f, "case2:票据切刀中===");
			// 切刀操作，切刀之前要走纸
			printerResult = printUtil.print_byttfScript(String.format("*cut\n"));
			if (printerResult != NDK_OK) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次切刀失败(ret=%d)", Tools.getLineInfo(),i+1, printerResult);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		LoggerUtil.d("CPOS X5,Case2 Print End="+(System.currentTimeMillis()-startTime)+"ms");
		
		// case3:切刀指令之前走纸一行，*cut命令放在最后
		startTime = System.currentTimeMillis();
		LoggerUtil.d("CPOS X5,Case3 Print Start");
		data =	"*feedline p:48\n" +
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
				"*cut\n";
		for (int i = 0; i < 3; i++) 
		{
			gui.cls_show_msg2(0.5f,"case3:正在第%d次打印票据",i+1);
			printerResult = printUtil.print_byttfScript(data);
			if (printerResult != NDK_OK) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:第%d次打印票据失败(ret=%d)", Tools.getLineInfo(), i+1,printerResult);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		LoggerUtil.d("CPOS X5,Case3 Print End="+(System.currentTimeMillis()-startTime)+"ms");
		
		//case5 切纸指令在打印指令之间  
		gui.cls_show_msg2(0.5f,"case5:正在打印票据，设备号和交易时间中应切刀");
		String data_5 = 
				"*feedline p:48\n" + "!NLFONT 3 3 3\n" + "!yspace 9\n"
				+ "*text l kissbaby面包屋（名城店）\n" + "!NLFONT 1 12 3\n" + "*line\n"
				+ "*text l 商户实收：                   ￥5\n" + "*line\n"
				+ "*text l 商户实付：                   ￥5\n" + "*line\n"
				+ "*text l 支付方式：                支付宝\n" + "*line\n"
				+ "!yspace 6\n"
				+ "*text l 交易单号：2017020821001004920238\n"
				+ "*text r 392810\n"
				+ "*text l 商户名称：kissbaby面包屋（名城店\n"
				+ "*text r ）\n"
				+ "*text l 设备号：                83902148\n"
				+"*feedline p:200\n"    //切刀前需要走纸
				+ "*cut\n"
				+ // 指令中切刀
				"*text l 交易时间：   2017/02/08 18:19:32\n"
				+ "*text l 交易状态：              付款成功\n" + "!yspace 9\n"
				+ "*line\n" + "!yspace 6\n"
				+ "*text l 打印时间：      2017-02-08 18:19\n" + "*text l 备注：\n"
				+ "*line\n" + "!yspace 0\n" + // 将行间距修改为打印初始化值
				"*feedline p:200\n";
		printerResult = printUtil.print_byttfScript(data_5);
	    if (printerResult != NDK_OK) {
	        gui.cls_show_msg1_record(fileName, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), printerResult);	
	    }

		String data2 = "*feedline p:48\n" + "!NLFONT 9 12\n*text c 以下打印PNG图片\n"
				+"*feedline p:200\n"  //切刀前需要走纸
				+ "*cut\n"
				+ // 指令中切刀
				"*image l 576*961 path:/mnt/sdcard/picture/carrefour1.png\n"
				+ "*line\n" + "*feedline p:200\n";
		// 图片
		gui.cls_show_msg2(0.5f, "case5:文字以下打印PNG图片和打印的图片中应切刀");
		printerResult = printUtil.print_byttfScript(data2);

		if (printerResult != NDK_OK) {
			gui.cls_show_msg1_record(fileName, funcName, 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(),printerResult);
		}

		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"ttf脚本打印图片打印效果要与预期提示语一致,打印脚本指令过程中切刀应在设备号和交易时间中，打印图片过程中切刀在提示以下打印PNG图片与图片中。");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}

}
