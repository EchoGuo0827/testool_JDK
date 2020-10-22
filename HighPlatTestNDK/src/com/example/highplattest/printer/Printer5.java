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
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer5.java 
 * history 		 	: 变更记录					变更时间			变更人员
 *			  		 新增X5打印切纸案例			20200426		魏美杰		
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer5 extends UnitFragment
{
	private final String TESTITEM = "X5_ttf脚本打印(模拟家乐福客户)";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Printer5.class.getSimpleName();
	PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
	int height,width,printerResult;
	
	public void printer5()
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
		int ret =-1;
		String[] arrayWay = {"carrefour1","carrefour2","carrefour2","carrefour2"};
		long startTime = System.currentTimeMillis();
		LoggerUtil.d("CPOS X5,Print Start");
		for (int i = 0; i < 4; i++) {
			// 切刀初始化
				ret = JniNdk.JNI_PrnCutterInit();
				LoggerUtil.d("JNI_PrnCutterInit="+ret);
				String picPath = Environment.getExternalStorageDirectory().getPath()+"/picture/"+arrayWay[i]+".png";
				Bitmap bit = BitmapFactory.decodeFile(picPath);
				
				width = bit.getWidth();
				height = bit.getHeight();
				printerResult = printUtil.print_byttfScript(String.format("!NLFONT 9 12 3\n*text c 以下打印PNG图片\n*image l %s path:%s\n*line\n*feedline 1\n",width+"*"+height,picPath));
				if(printerResult!=NDK_OK)
				{
					gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s图片打印失败(ret=%d)", Tools.getLineInfo(),arrayWay[i],printerResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				ret = JniNdk.JNI_PrnCutterPerformance();
				LoggerUtil.d("JNI_PrnCutterPerformance="+ret);
		}
		LoggerUtil.d("CPOS X5,Print End="+(System.currentTimeMillis()-startTime)+"ms");
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"ttf脚本打印图片打印效果要与预期提示语一致");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}

}
