package com.example.highplattest.printer;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.me.module.printer.TTFPrint;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer9.java 
 * history 		 	: 变更记录										变更时间					变更人员
 *			  		 去除非阻塞状态打印时获取打印机状态为正在打印的测试点			20200814				郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer9 extends UnitFragment
{
	private final String TESTITEM = "ttf的PrintScipt";
	private final String fileName = Printer9.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	
	public void printer9()
	{
		String funcName="printer9";
		int ret=-1;
		String data="";
		// case1:flushFlag为int值，设置为int值的边界范围 (-2147483648 ~ 2147483647)
		gui.cls_show_msg2(0.5f,"case1:flushFlag=-2147483648测试...");
		data="!NLFONT 9 12\n*text c flushFlag=-2147483648测试\n*text c 以下打印汉字样式小字体西方样式大字体居左带下划线\n!hz s\n!asc l\n!gray 5\n!yspace 6\n*underline l aBc34国国国国aBc34\n*line\n*feedline 1\n";
		ret = TTFPrint.PrintScipt(data.getBytes(), data.getBytes().length,-2147483648);
		if(ret!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName,gKeepTimeErr,"line %d:flushFlag设置为-2147483648打印失败(ret=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg2(0.5f,"case1:flushFlag=2147483648测试...");
		data="!NLFONT 9 12\n*text c flushFlag=2147483648测试\n*text c 以下打印汉字样式小字体西方样式大字体居左带下划线\n!hz s\n!asc l\n!gray 5\n!yspace 6\n*underline l aBc34国国国国aBc34\n*line\n*feedline 1\n";
		ret = TTFPrint.PrintScipt(data.getBytes(), data.getBytes().length,2147483647);
		if(ret!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName,gKeepTimeErr,"line %d:flushFlag设置为2147483647打印失败(ret=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:设置flushFlag=0,脚本命令处理完成后即返回，打印未完全结束，此时获取打印机状态可能为打印机正在打印。(PRN_STATUS_BUSY = 8,		/**<打印机正在打印*/)
		gui.cls_show_msg2(0.5f,"case2:flushFlag=0测试...");
		data="!NLFONT 9 12\n*text c flushFlag=0测试\n*text c 以下打印汉字样式小字体西方样式大字体居左带下划线\n!hz s\n!asc l\n!gray 5\n!yspace 6\n" +
				"*underline l aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34\n*line\n*feedline 1\n";
		ret = TTFPrint.PrintScipt(data.getBytes(), data.getBytes().length,0);
		if(ret!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName,gKeepTimeErr,"line %d:flushFlag设置为0打印失败(ret=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		/*// 获取打印机状态应为正在打印  此时获取的打印状态只是可能为正在打印，也有可能出现打印机状态为0的情况 20200814
		if((ret = JniNdk.JNI_Print_GetStatus())!=8)
		{
			gui.cls_show_msg1_record(fileName, funcName,gKeepTimeErr,"line %d:flushFlag设置为0,获取打印状态错误(status=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		LoggerUtil.d("print status = "+ret);
		
		// case3:设置flushFlag!=0,ttf打印会阻塞等待打印完全结束再返回，获取打印状态应为打印机正常
		gui.cls_show_msg2(0.5f,"case1:flushFlag=1测试...");
		data="!NLFONT 9 12\n*text c flushFlag=1测试\n*text c 以下打印汉字样式小字体西方样式大字体居左带下划线\n!hz s\n!asc l\n!gray 5\n!yspace 6\n" +
				"*underline l aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34aBc34国国国国aBc34\n*line\n*feedline 1\n";
		ret = TTFPrint.PrintScipt(data.getBytes(), data.getBytes().length,1);
		if(ret!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName,gKeepTimeErr,"line %d:flushFlag设置为1打印失败(ret=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = JniNdk.JNI_Print_GetStatus())!=0)
		{
			gui.cls_show_msg1_record(fileName, funcName,gKeepTimeErr,"line %d:flushFlag设置为1,获取打印状态错误(status=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.d("print status = "+ret);
		
		gui.cls_show_msg1_record(fileName, funcName,gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
