package com.example.highplattest.other;

import java.io.File;
import com.adobe.mps.MPS;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other8.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170329
 * directory 		: 将PDF图片转换为jpg或png图片
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20171218	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other8 extends UnitFragment
{
	private final String TESTITEM = "文件格式转换";
	public final String TAG = Other8.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void other8()
	{
		String[] filePaths = {GlobalVariable.sdPath+"pdf/Font1.pdf",GlobalVariable.sdPath+"pdf/Font2.pdf",GlobalVariable.sdPath+"pdf/Font3.pdf"};
		String desPath = GlobalVariable.sdPath+"pdf";
		// 测试前置：放置测试文件
		for(String strPath:filePaths)
		{
			File file = new File(strPath);
			if(file.exists()==false)
			{
				gui.cls_show_msg1_record(TAG, "other8", gScreenTime,"line %d:%s文件不存在，请先放置测试文件",Tools.getLineInfo(),strPath);
				return;
			}
			
			// case3:将图片转换为bmp格式
			gui.cls_printf("case1:将pdf格式文件转换为bmp格式，请耐心等待".getBytes());
			try {
				MPS.getInstance().PDFtoBMP(myactivity, strPath,desPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(gui.cls_show_msg("%s文件转为bmp格式已完成，请去pdf目录下查看Fontx_x.bmp图片是否与pdf内容一致，一致[确认]，不一致[取消]",strPath)!=ENTER)
			{
				gui.cls_show_msg1_record(TAG, "other8", gKeepTimeErr,"line %d:%s文件转为bmp格式失败",Tools.getLineInfo(),strPath);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
		}

		
		// case1:将图片转换为jpg格式，客户给的程序只支持转为bmp格式
		/*gui.cls_printf("case1:将pdf格式文件转换为jpg格式，请耐心等待".getBytes());
		try {
			MPS.getInstance().PDFtoJPG(myactivity, filePath, 1,desPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(gui.cls_show_msg("pdf格式转为jpg格式已完成，请去pdf目录下查看invoice_x.jpg图片是否与pdf内容一致，一致[确认]，不一致[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "other8", gKeepTimeErr,"line %d:pdf格式转为jpg格式失败",Tools.getLineInfo());
			if(GlobalVariable.isContinue == false)
				return;
		}*/
		
		// case2:将图片转换为png格式，客户给的程序只支持转为bmp格式
		/*gui.cls_printf("case2:将pdf格式文件转换为PNG格式，请耐心等待".getBytes());
		try {
			MPS.getInstance().PDFtoPNG(myactivity, filePath,1,desPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(gui.cls_show_msg("pdf格式转为PNG格式已完成，请去pdf目录下查看invoice_x.png图片是否与pdf内容一致，一致[确认]，不一致[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "other8", gKeepTimeErr,"line %d:pdf格式转为png格式失败",Tools.getLineInfo());
			if(GlobalVariable.isContinue == false)
				return;
		}*/
		

		
		gui.cls_show_msg1_record(TAG, "other8", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
