package com.example.highplattest.printer;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.me.module.printer.TTFPrint;
/************************************************************************
 * module 			: 打印类
 * file name 		: Printer10.java 
 * Author 			: zhangxinj
 * description 		: TTF 获取字符串打印宽高度的接口
 * history 		 	: 变更记录			                  变更人员			变更时间
 *			  		     新增				   谢志兴			20200818
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer10 extends UnitFragment{
	private final String TESTITEM = "GetStrPrnSize(TTF)";
	private final String FILE_NAME = "Printer10";
	private Gui gui = new Gui(myactivity, handler);
	
	public void printer10()
	{
		try {
			ttfGetStrPrnSize();
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(FILE_NAME, "printer10", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
		
	}
	
	private void ttfGetStrPrnSize() {
		int[] width = new int[1];
    	int[] height = new int[1];
    	int[] width2 = new int[1];
    	int[] height2 = new int[1];
    	int[] width3 = new int[1];
    	int[] height3 = new int[1];
    	int[] width4 = new int[1];
    	int[] height4 = new int[1];
    	int ret = -1;
		
    	PrintUtil printUtil = new PrintUtil(myactivity,handler,true);
        String scriptBuffer = "开始Innovation in China 0123456789!结束";//"Innovation in China 0123456789!";

        //设置测试字体:中文为PRN_HZ_FONT_24x24A <自有宋体 24x24 点阵>，英文为PRN_ZM_FONT_12x24A <Gulimche 字体 12x24 点阵>
        String scriptSetBuffer = "!NLFONT 9 12 3\n";
        printUtil.print_byttfScript(scriptSetBuffer);
          	
    	//case2.1:异常参数str=null
    	ret = TTFPrint.GetStrPrnSize(null, scriptBuffer.getBytes().length, width, height);
		if(ret != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
    	
		//case2.2:异常参数strlen=0
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), 0, width, height);
		if(ret != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
		
		//case2.3:异常参数strlen=-2147483648
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), -2147483648, width, height);
		if(ret != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
			
		//case2.4:异常参数width=null
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length, null, height);
		if(ret != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
			
		//case2.5:异常参数height=null
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length, width, null);
		if(ret != NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
			
		//4.1:str实际长度<strlen
//		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length+12, width, height);
//		if(ret != 100)
//		{
//			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d, width:%d, height:%d)", Tools.getLineInfo(), TESTITEM, ret, width[0], height[0]);
//		}
		
		//4.2:str实际长度>strlen
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length-10, width4, height4);
		if(ret != NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d, width:%d, height:%d)", Tools.getLineInfo(), TESTITEM, ret, width4[0], height4[0]);
		}
			
		//1.1：获取字符串打印宽高度
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length, width, height);
		if(ret != NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
		//str实际长度<strlen获得宽要更小
		if(width[0] <= width4[0])
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
		}
		
		//5.1修改字体大小，获取打印字符串宽高
		//设置测试字体大小:中文为PRN_HZ_FONT_16x32，英文为PRN_ZM_FONT_16x32A <MSGothic 粗体 16x32 点阵>
        scriptSetBuffer = "!NLFONT 2 13 3\n";
        printUtil.print_byttfScript(scriptSetBuffer);
        
      	ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length, width2, height2);
      	if(ret != NDK_OK)
      	{
      		gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
      	}
		
		if((width2[0] <= width[0]) || (height2[0] <= height[0]))
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
		}
		
		//5.2修改打印放大倍数，获取打印字符串宽高
		//设置打印放大倍数为：横向2倍放大、纵向2倍放大
		scriptSetBuffer = "!NLFONT 2 13 0\n";
		printUtil.print_byttfScript(scriptSetBuffer);
		        
		ret = TTFPrint.GetStrPrnSize(scriptBuffer.getBytes(), scriptBuffer.getBytes().length, width3, height3);
		if(ret != NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
		}
				
		if((width3[0] <= width2[0]) || (height3[0] <= height2[0]))
		{
			gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
		}
		
		gui.cls_show_msg1_record(FILE_NAME, "GetStrPrnSize",gScreenTime,"获取字符串打印宽高度测试通过");
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
