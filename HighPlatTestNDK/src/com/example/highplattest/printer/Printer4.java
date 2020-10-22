package com.example.highplattest.printer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
import com.newland.print.DeviceModel;
import com.newland.print.Printer;
import com.newland.print.PrinterResult;
import com.newland.print.PrinterStatus;

/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer16.java 
 * Author 			: chending
 * DATE 			: 20190718
 * description 		: N910_poynt打印
 * history 		 	: author			date			remarks
 *					  chending			20190729        created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class Printer4 extends UnitFragment 
{
	public final String TESTITEM = "N910_Poynt打印";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Printer4.class.getSimpleName();
	public String OUTPAPER="Out of paper";
	public String NORMAL="Normal";
	String path = GlobalVariable.sdPath + "picture/";
	String [] picdata={"color4.png","abclogo.bmp","zx.jpg"};
	
	public void printer4() 
	{
		String funcName = "printer4";
		/**目前该案例只有N910_Poynt支持*/
		if(GlobalVariable.currentPlatform!=Model_Type.N910_Poynt)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"%s设备不支持，此为N910_Poynt专用",GlobalVariable.currentPlatform);
			return;
		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.unitPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		gui.cls_show_msg("请构造打印机缺纸状态。。。。按任意键开始获取打印机状态");		
		//case1 获取打印机状态测试
		//case1.1 缺纸状态测试
		PrinterStatus printerStatus = Printer.getInstance().getPrinterStatus();
		if(!(printerStatus.toString().equals(OUTPAPER)))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:获取打印机状态异常!", Tools.getLineInfo());
			return;
		}
		//case1.2 正常状态测试
		gui.cls_show_msg("打印机缺纸状态获取通过，请构造打印机正常状态。。。。按任意键开始获取打印机状态");
		PrinterStatus printerStatus2 = Printer.getInstance().getPrinterStatus();
		if(!(printerStatus2.toString().equals(NORMAL)))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:获取打印机状态异常!", Tools.getLineInfo());
			return;
		}
		//case2  打印测试
		gui.cls_show_msg("获取打印机状态测试通过。。。。按任意键开始打印机测试");	
		//case2.1 打印开发提供的图片
		Printer.getInstance(DeviceModel.MODEL_N910).setReceiptMarginLeftAndRight(0);
        Printer.getInstance(DeviceModel.MODEL_N910).setReceiptMargineBottom(5);
        Bitmap bitmap = BitmapFactory.decodeResource(myactivity.getResources(), R.drawable.receipt_img);
        final PrinterResult printerResult = Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap);
        if (!(printerResult.equals(PrinterResult.SUCCESS))) 
        {
       	 	gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印返回数据异常!", Tools.getLineInfo());
            return;
        } 
        // case2.2 打印目前已收集的全部PNG格式打印数据
        //case 2.2.1 打印color
        for (int i = 1; i <= 6; i++) 
        {
       	 	Bitmap bitmap2 = BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/"+"color"+i+".png");
       	 	Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap2);
		}
        //case 2.2.2 打印ysz
    	for (int i = 1; i <=7; i++) 
    	{
    		Bitmap bitmap3 = BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/"+"ysz"+i+".png");
      	    Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap3);
		}
    	//case 2.2.3 打印头文件格式系类的12张PNG图片
    	for (int i = 1; i <= 15; i++) 
    	{
    		Bitmap bitmap4 = BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/"+"IHDR"+i+".png");
      	    Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap4);
		}
    	//case 2.2.4 打印头other系类图片
    	for (int i = 1; i <= 15; i++) 
    	{
    		Bitmap bitmap5 = BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/"+"other"+i+".png");
      	    Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap5);
    	}
    	//case2.3 打印各个格式图片测试 img png jpg
    	for (int i = 0; i < picdata.length; i++) 
    	{
    		Bitmap bitmap6 = BitmapFactory.decodeFile(GlobalVariable.sdPath +"picture/"+ picdata[i]);
      	    Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap6);
    	}
    	//case2.4 设置间距为无
    	gui.cls_show_msg("下面测试无间距打印，按任意键继续");	
		Printer.getInstance(DeviceModel.MODEL_N910).setReceiptMarginLeftAndRight(10);
		Printer.getInstance(DeviceModel.MODEL_N910).setReceiptMargineBottom(0);
		Bitmap bitmap9 = BitmapFactory.decodeResource(myactivity.getResources(), R.drawable.receipt_img);//         
	    Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap9); 
		for (int i = 0; i < picdata.length; i++) 
		{
    		Bitmap bitmap7 = BitmapFactory.decodeFile(GlobalVariable.sdPath +"picture/"+ picdata[i]);
      	    Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap7);
        }
		
		// case3:打印过热测试，过热测试不要放在单元
//		gui.cls_show_msg("下面测试过热打印，按任意键继续");
//	     while (true) 
//	     {
//	        PrinterStatus printerStatus5 = Printer.getInstance().getPrinterStatus();
//	        PrinterResult printerResult5 = Printer.getInstance(DeviceModel.MODEL_N910).printByBitmap(bitmap);
//	        if (printerResult5.equals(PrinterResult.HEAT_LIMITED)||printerStatus5.equals(PrinterStatus.HEAT_LIMITED)){
//	        	gui.cls_show_msg("检测到打印机过热...测试通过");
//	        	return;
//	        }
//	     }
	     gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "%s测试通过，注意观察打印出的图片与实际图片之间的效果", TESTITEM);
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}


}
