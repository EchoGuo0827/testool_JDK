package com.example.highplattest.printer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer2.java 
 * Author 			: zhangxinj
 * DATE 			: 20180705
 * description 		: 新NDK ttf脚本打印接口测试
 * history 		 	:  变更记录			                  变更人员			变更时间
 *			  		 修改ttf打印最大值为50个		chending		20200426
 *					修改提示语断句，让测试人员容易理解   chending         20200610
 *					去除提示每行打印XX字符             
 *					新增无阻塞TTF打印。(目前客户无使用该流程。只用于验证是否卡顿)     
 *					二维码打印修复部分西班牙语打印不出问题   chending      20200611
 *					部分提示语修改
 *					新增空格模式打印                                      chending			20200630
 *					新增打印数字3打印成数字8问题验证   chending	       20200703
 *					新增异常测试后不恢复默认打印的验证 chending			20200721
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer2 extends UnitFragment{

	private final String TESTITEM = "ttf脚本打印";
	private Gui gui = new Gui(myactivity, handler);

	//与开发沟通TTF每行打印字符数量根据机型 字体不同，打印数量也不同，故去除提示每行打印XX字符。改为提示字体大小对比有变化即可  
	private String strData1 = 
				// 新增二维码支持400长度 存在bug：返回-1 开发暂时无法解决 会影响到后续测试 暂时屏蔽
//				"!NLFONT 9 12\n*text c 以下打印二维码(高度100居中，内容400)请扫描打印出的二维码内容是否为:更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录" +
//						"更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记" +
//						"录更新更新记录更新记录更新更新记录更新记录更新更新记录\n!qrcode 100 2\n*qrcode c 更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更" +
//						"新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新记录更新记录更新更新" +
//						"记录更新记录更新更新记录更新记录更新更新记录\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式小字体西方样式大字体居左带下划线\n!hz s\n!asc l\n!gray 5\n!yspace 6\n*underline l aBc34国国国国aBc34\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式标准字体西方样式标准字体居中带下划线\n!hz n\n!asc n\n!gray 5\n!yspace 6\n*underline c aBc1234国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国aBc1234\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式大字体西方样式小字体居右带下划线\n!hz l\n!asc s\n!gray 5\n!yspace 6\n*underline r aBc1234国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国aBc1234\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式大字体西方样式小字体偏移16带下划线\n!hz l\n!asc s\n!gray 5\n!yspace 6\n*underline x:16 aBc1234国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国aBc1234\n*line\n*feedline 1\n"+//新增偏移量打印x:16
				"!NLFONT 9 12\n*text c 以下打印汉字样式小字体,字体大小对比有变化即可,居左\n!hz s\n!gray 5\n!yspace 6\n*text l 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式小字体,字体大小对比有变化即可,偏移120像素\n!hz s\n!gray 5\n!yspace 6\n*text x:120 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+//新增偏移量打印x:120
				"!NLFONT 9 12\n*text c 以下打印汉字样式标准字体,字体大小对比有变化即可,居中\n!hz n\n!gray 5\n!yspace 6\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式大字体,字体大小对比有变化即可,居右\n!hz l\n!gray 5\n!yspace 6\n*text r 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式,小字体宽度,标准字体高度,居左\n!hz sn\n!gray 5\n!yspace 6\n*text l 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+	
				"!NLFONT 9 12\n*text c 以下打印汉字样式,小字体宽度,大字体高度,居中\n!hz sl\n!gray 5\n!yspace 6\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字样式,标准字体宽度,大字体高度,居右\n!hz nl\n!gray 5\n!yspace 6\n*text r 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n";
				
	// 打印英文
	private String strData2 = 
				"!NLFONT 9 12\n*text c 以下打印西方样式小字体,字体大小对比有变化即可,居左\n!asc s\n!gray 5\n!yspace 6\n*text l sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印西方样式标准字体,字体大小对比有变化即可,居中\n!asc n\n!gray 5\n!yspace 6\n*text c sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印西方样式大字体,字体大小对比有变化即可,居右\n!asc l\n!gray 5\n!yspace 6\n*text r sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印西方样式,小字体宽度,标准字体高度,居左\n!asc sn\n!gray 5\n!yspace 6\n*text l sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印西方样式,小字体宽度,大字体高度,居中\n!asc sl\n!gray 5\n!yspace 6\n*text c sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印西方样式,标准字体宽度,大字体高度,居右\n!asc nl\n!gray 5\n!yspace 6\n*text r sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n";
		
	// 分隔符
	private String strData3 = "!hz n\n!asc n\n!gray 7\n!yspace 6\n*text c 以下打印两条分隔符\n*line\n*line\n*feedline 1\n";
	// 条形码
	private String strData4 = 
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度120居右)请扫描打印出的条形码内容是否为:0123456789ABC\n!barcode 2 120\n*barcode r 0123456789ABC\n*line\n*feedline 1\n"+//测试第一次调用脚本打印直接打印条码,中间不应该有空白行
				//改成320 下一个版本改成384
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度8高度320居右)请扫描打印出的条形码内容是否为:0123456789ABC\n!barcode 8 320\n*barcode r 0123456789ABC\n*line\n*feedline 1\n"+//测试条码宽度正好384
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度16居右)请扫描打印出的条形码内容是否为:0123456789ABC\n!barcode 2 16\n*barcode r 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度1高度128居中)请扫描打印出的条形码内容是否为:123456\n!barcode 1 128\n*barcode c 123456\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度4高度128偏移20)请扫描打印出的条形码内容是否为:123456\n!barcode 4 128\n*barcode x:20 123456\n*line\n*feedline 1\n"+//偏移量打印x:200
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度8高度320居左)请扫描打印出的条形码内容是否为:123\n!barcode 8 320\n*barcode l 123\n*line\n*feedline 1\n"+
				//新增可以扫描的字符数的边界测试,宽度为1:纯字符31个,纯数字60,宽度为2:纯字符14个,纯数字28个
				//TTF打印最多50个字符  by 20200426 chending
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度1高度64居中,纯字符31个),不要求能够扫描出来\n!barcode 1 64\n*barcode c AAAAAAAAAABBBBBBBBBBCCCCCCCCCCD\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度1高度64居中,纯数字50个),不要求能够扫描出来\n!barcode 1 64\n*barcode c 01234567890123456789012345678901234567890123456789\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,纯数字28个)请扫描打印出的条形码内容是否为:0123456789012345678901234567\n!barcode 2 64\n*barcode c 0123456789012345678901234567\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,纯字母14个)请扫描打印出的条形码内容是否为:AAAAAAAAAABBBB\n!barcode 2 64\n*barcode c AAAAAAAAAABBBB\n*line\n*feedline 1\n"+	 						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,字母与数字组合)请扫描打印出的条形码内容是否为:01AB23BC34C\n!barcode 2 64\n*barcode c 01AB23BC34C\n*line\n*feedline 1\n"+	 						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,字母与数字组合)请扫描打印出的条形码内容是否为:A1B2C3D4E5F6G7\n!barcode 2 64\n*barcode c A1B2C3D4E5F6G7\n*line\n*feedline 1\n"+	 						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,字母与数字组合)请扫描打印出的条形码内容是否为:ABC123CDE4567\n!barcode 2 64\n*barcode c ABC123CDE4567\n*line\n*feedline 1\n"+	
				//恢复默认宽度2,高度64(超过28个,宽度会自动降为1)
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,纯数字32个(超过28,宽度会自动降为1)),不要求能够扫描出来\n!barcode 2 64\n*barcode c 01234567890123456789012345678912\n*line\n*feedline 1\n"+
				//ttf特有：!BARCODE命令和*BARCODE命令，支持多种编码，前2个参数跟!barcode一样，第三个参数表示是否显示下方提示信息，第四个参数表示条码编码类型 
				//0 codebar; 1 code39;2 code93;3 code128;4 EAN8/EAN13/JAN8/JAN13;5 ITF;6 UPCA;7 UPCE;
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的Codebar条形码内容是否为:0123456789，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 0\n*BARCODE c A0123456789C\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的Code39条形码内容是否为:01A，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 1\n*BARCODE c 01A\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的Code93条形码内容是否为:CD 45%+67$/，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 2\n*BARCODE c CD 45%+67$/\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的Code128条形码内容是否为:A0123456C%$#@，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 3\n*BARCODE c A0123456C%$#@\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的EAN-8条形码内容是否为:12345670，并且条码下方【无】显示条码信息\n!BARCODE 6 120 0 4\n*BARCODE c 1234567\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的EAN-13条形码内容是否为:1234567890128，并且条码下方【无】显示条码信息\n!BARCODE 6 120 0 4\n*BARCODE c 123456789012\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的EAN-8条形码内容是否为:00001236，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 4\n*BARCODE c 123\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的EAN-13条形码内容是否为:0000123456784，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 4\n*BARCODE c 12345678\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的ITF条形码内容是否为:01234567890128，并且条码下方【无】显示条码信息\n!BARCODE 6 120 0 5\n*BARCODE c 0123456789012\n*line\n*feedline 1\n"+						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的ITF条形码内容是否为:00123456789050，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 5\n*BARCODE c 12345678905\n*line\n*feedline 1\n"+	 						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的UPC-A条形码内容是否为:123456789012，并且条码下方【无】显示条码信息\n!BARCODE 6 120 0 6\n*BARCODE c 12345678901\n*line\n*feedline 1\n"+	 						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的UPC-A条形码内容是否为:000765432108，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 6\n*BARCODE c 76543210\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的UPC-E条形码内容是否为:01234565，并且条码下方【无】显示条码信息\n!BARCODE 6 120 0 7\n*BARCODE c 0123456\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的UPC-E条形码内容是否为:11234562，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 7\n*BARCODE c 1123456\n*line\n*feedline 1\n";
	// 二维码
	private String strData5 =
				"!NLFONT 9 12\n*text c 以下打印二维码(高度384居右)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 384 3\n*qrcode r ABC123456789DEFGH\n*line\n*feedline 1\n"+//用例测试二维码宽度正好384
				"!NLFONT 9 12\n*text c 以下打印二维码(高度10居左)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 10 0\n*qrcode l ABC123456789DEFGH\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度50居中)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 50 1\n*qrcode c ABC123456789DEFGH\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度50偏移50)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 50 1\n*qrcode x:50 ABC123456789DEFGH\n*line\n*feedline 1\n"+//偏移量打印x:50
				"!NLFONT 9 12\n*text c 以下打印二维码(高度200居右)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 200 3\n*qrcode r ABC123456789DEFGH\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度100居中)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 100 2\n*qrcode c ABC123456789DEFGH\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度300居中)请扫描打印出的二维码内容是否为:400字节的A\n!qrcode 300 1\n*qrcode c AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度300居中)请扫描打印出的二维码内容是否为:401字节的A\n!qrcode 300 1\n*qrcode c AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n*line\n*feedline 1\n"+
				//ttf特有：!QRCODE命令和*QRCODE命令， 支持多种编码，前2个参数跟!qrcode一样，第二个参数仅对QRCODE有效，第三个参数 表示二维码编码类型
			    //0 Data Matrix;1 Maxicode;2 PDF417;3 QRCODE
			    																				
			    "!font /sdcard/picture/simsun.ttc\n!NLFONT 9 12\n*text c 以下打印二维码(高度200居中)请扫描打印出的二维码内容是否为(有空格是正常现象):Aa12%#@*&αΒγàÚǒ\n!QRCODE 200 0 0\n*QRCODE c Aa12%#@*&αΒγàÚǒ\n*line\n*feedline 1\n"+
			    "!NLFONT 9 12\n*text c 以下打印二维码(高度200居中)请扫描打印出的二维码内容是否为:ABC~*&%#!56789DEFGH\n!QRCODE 200 0 1\n*QRCODE c ABC~*&%#!56789DEFGH\n*line\n*feedline 1\n"+
			    "!NLFONT 9 12\n*text c 以下打印二维码(高度200居中)请扫描打印出的二维码内容是否为:PDF测试支持中文较差\n!QRCODE 200 0 2\n*QRCODE c PDF测试支持中文较差\n*line\n*feedline 1\n"+
			    "!NLFONT 9 12\n*text c 以下打印二维码(高度200居中)请扫描打印出的二维码内容是否为:QRCODE测试支持中文好\n!QRCODE 200 2 3\n*QRCODE c QRCODE测试支持中文好\n*line\n*feedline 1\n";		
	// 灰度时时生效
	// 灰度1
	private String strData6_1 = "!hz n\n!gray 1\n*text c 本行字灰度为1\n*line\n*feedline 1\n";
	// 灰度3
	private String strData6_2 = "!hz n\n!gray 3\n*text c 本行字灰度为3\n*line\n*feedline 1\n";
	// 灰度7
	private String strData6_3 = "!hz n\n!gray 7\n*text c 本行字灰度为7\n*line\n*feedline 1\n";
	// 灰度10
	private String strData6_4 = "!hz n\n!gray 10\n*text c 本行字灰度为10\n*line\n*feedline 1\n";
	//默认灰度5
	private String strData6_5 = "!hz n\n!gray 5\n*text c 本行字灰度为5\n*line\n*feedline 1\n";//灰度值5放后面是为了恢复默认值5
	// 间距
	private String strData7 = 
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为0\n!hz n\n!yspace 0\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为10\n!hz n\n!yspace 10\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为20\n!hz n\n!yspace 20\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为30\n!hz n\n!yspace 30\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为40\n!hz n\n!yspace 40\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为50\n!hz n\n!yspace 50\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为60\n!hz n\n!yspace 60\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距为6\n!hz n\n!yspace 6\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n";//恢复成默认值行间距6
	
	//ttf特有：*TEXT指令，用法与*text指令一样，区别是*text会换行，*TEXT不会换行；
	//ttf特有：*UNDERLINE指令，用法与*underline指令一样，区别是*underline会换行，*UNDERLINE不会换行；
	private String strData8=
				"!NLFONT 2 1 3\n*TEXT l 本行文字Test\n!NLFONT 9 12 3\n*text r 不换行Test\n"+
				"!NLFONT 9 12 3\n*UNDERLINE l 本行文字带下划线\n*underline r 并且不换行Test\n"+
				"!NLFONT 9 12 3\n*TEXT l 左边left\n!NLFONT 2 1 3\n*UNDERLINE c 下划线center\n!NLFONT 6 4 3\n*text r 右边right\n*line\n*feedline 1\n";
	// 打印PNG图片 
	// 变更测试图片放置路径由原先的/data/share/ 目录变更为/mnt/sdcard/目录，因为海外固件后续将只开放/mnt/sdcard/目录 modify by zhengxq 20171031
	private String strData9 = 
				"!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用大津算法(居左),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image l 384*300 path:/mnt/sdcard/picture/color1.png\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用大津算法(居中),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image c 384*300 path:/mnt/sdcard/picture/color2.png\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用大津算法(居右),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image r 384*300 path:/mnt/sdcard/picture/color3.png\n*line\n*feedline 1\n"+
				 //ttf特有：使用WellnerAdaptiveThreshold阈值算法 yz为0
				 "!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用WellnerAdaptiveThreshold算法(居左),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image l 384*300 path:yz:0;/mnt/sdcard/picture/color1.png\n*line\n*feedline 1\n"+
				 "!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用WellnerAdaptiveThreshold算法(居中),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image c 384*300 path:yz:0;/mnt/sdcard/picture/color2.png\n*line\n*feedline 1\n"+
				 "!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用WellnerAdaptiveThreshold算法(居右),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image r 384*300 path:yz:0;/mnt/sdcard/picture/color3.png\n*line\n*feedline 1\n"+
				 "!NLFONT 9 12\n*text c 以下打印PNG图片,阈值255，打印效果较深\n*image r 384*300 path:yz:255;/mnt/sdcard/picture/color4.png\n*line\n*feedline 1\n"+  //新增用例测试png图片功能
				 "!NLFONT 9 12\n*text c 以下打印PNG图片,阈值0，打印效果较浅\n*image r 384*300 path:yz:0;/mnt/sdcard/picture/color4.png\n*line\n*feedline 1\n"+  
				 //新增纯黑纯白打印测试点，纯黑图片不论设置阈值为多少都是全黑，纯白图片不论设置阈值多少都是全白 add by zhangxinju 2019.3.6 
				 "!NLFONT 9 12\n*text c 以下打印纯白PNG图片,阈值设置为0，打印效果为全白\n*image r 384*300 path:yz:0;/mnt/sdcard/picture/kbd.png\n*line\n*feedline 1\n"+  
				 "!NLFONT 9 12\n*text c 以下打印纯黑PNG图片,阈值设置为255，打印效果为全黑\n*image r 384*300 path:yz:255;/mnt/sdcard/picture/landi_black.png\n*line\n*feedline 1\n"+  
				"!NLFONT 9 12\n*text c 以下打印压缩PNG图片,大津算法阈值自动，(384*242，居左)，不必关注打印效果\n*image l 384*242 path:/mnt/sdcard/picture/IHDR6.png\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印压缩PNG图片,大津算法阈值自动，(384*75，居中)，不必关注打印效果\n*image c 384*75 path:/mnt/sdcard/picture/IHDR5.png\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印压缩PNG图片,大津算法阈值自动，(384*144，居右)，不必关注打印效果\n*image r 384*144 path:/mnt/sdcard/picture/IHDR8.png\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印压缩PNG图片,WellnerAdaptiveThreshold算法阈值自动，(384*144，居右)，不必关注打印效果\n*image r 384*144 path:yz:0;/mnt/sdcard/picture/IHDR8.png\n*feedline 1\n";
	
	//暂停时间:pause命令、NLPRNOVER命令 、ums图片(ums数据路径必须在/data/share/路径)
	private String strData10=						
			"!NLFONT 9 12\n*text l 以下打印图片及文字,将暂停5s后打印\n!NLPRNOVER\n*pause 5\n!hz n\n!asc n\n!gray 5\n!yspace 6\n*image c 128*64 #ums\n*text c 银联商务-全民付交易凭条\n!hz l\n!asc l\n*text c (大字体)客服电话:95534\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text l 以偏移256打印图片\n!NLPRNOVER\n!hz n\n!asc n\n!gray 5\n!yspace 6\n*image x:256 128*64 #ums\n*line\n*feedline 1\n";
	// 退纸
	private String strData11 = 
				"!NLFONT 9 12\n*text l 进退纸测试第一行文字\n*feedline 1\n!gray 5\n!yspace 6\n*text c 第二行文字.打印的文字没有相互覆盖\n*line\n*feedline 1\n"+//*feedline -1\n
				"!gray 5\n!yspace 6\n*text c 本行文字应完全在纸槽外\n*feedline 5\n";//!NLPRNOVER\n;
	//英文横向放大纵向放大之前存在一个英文字母会分开2边的问题 
	private String strData12 = 
				"!NLFONT 9 12 0\n*text r 靠右打印文字，横向放大、纵向放大，国国国国国国国国国国国国国国国国国国\n*line\n"+
				"!NLFONT 9 12 0\n*text r To print text, transverse magnification, vertical magnification\n*line\n";
	
	//新增空格模式打印
	private String strData13 =
			"!NLFONT 9 12\n*text l 字体大小默认空格模式默认\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 0 0\n*text l 字体横纵二倍放大空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 0 1\n*text l 字体横纵二倍放大空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 1 0\n*text l 字体横向二倍放大纵向正常空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 1 1\n*text l 字体横向二倍放大纵向正常空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 2 0\n*text l 字体横向正常纵向二倍放大空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 2 1\n*text l 字体横向正常纵向二倍放大空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 3 0\n*text l 字体横纵正常空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 3 1\n*text l 字体横纵正常空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 4 0\n*text l 字体横纵三倍放大空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 4 1\n*text l 字体横纵三倍放大空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 5 0\n*text l 字体横向三倍放大纵向正常空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 5 1\n*text l 字体横向三倍放大纵向正常空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 6 0\n*text l 字体横向正常纵向三倍放大空格模式关闭\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"+
			"!NLFONT 9 12 6 1\n*text l 字体横向正常纵向三倍放大空格模式打开\n*text l 本行打印3个空格:   空格打印结束\n*line\n*feedline 1\n"; 
	
	//新增BUG验证  打印3像8
	private String strData14 ="*text c 以下打印最后一位不应看起来像8\n*text c 023-63393333\n";	
	
	//新增打印异常不恢复默认格式打印变更  20200721
	private String strData15 =
				"!NLFONT 10 20 2 0\n!gray 10\n!yspace 30\n*text l 打印汉字样式粗体,横向正常纵向2倍放大,行间距30,灰度10,居左\n*line\n"+
				"!NLFONT 10 20 2 0\n!gray 10\n!yspace 30\n*text l 打印英文样式粗体,横向正常纵向2倍放大,行间距30,灰度10,居左abcdefghijklmnABCDEFGHIJKLMN\n*line\n"+
				"*text l 以下打印条形码，高度128\n!barcode 2 128\n*barcode c 123456\n*line\n"+
				"*text l 以下打印二维码，宽度128\n!qrcode 200 2\n*qrcode c ABC123456789DEFGH\n*line\n";
	
	private String strData16=
				"*text l 打印汉字样式粗体,横向正常纵向2倍放大,行间距30,灰度10,居左\n*line\n"+
				"*text l 打印英文样式粗体,横向正常纵向2倍放大,行间距30,灰度10,居左abcdefghijklmnABCDEFGHIJKLMN\n*line\n"+
				"*text l 以下打印条形码，高度128\n*barcode c 123456\n*line\n"+
				"*text l 以下打印二维码，宽度128\n*qrcode c ABC123456789DEFGH\n*line\n";
					
	
						
	
	//新脚本字体样式不可设置默认值0
	
	// 异常测试
	private String errString =
				"!NLFONT 9 12\n*text c 以下进行异常测试,忽略打印效果,不跑飞即可:打印汉字灰度值设置0\n!hz n\n!gray 0\n!yspace 10\n*text l 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字居右设置rr\n*text rr 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印汉字行间距设置70\n!hz n\n!gray 5\n!yspace 70\n*text l 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度0高度64居中)\n!gray 5\n!yspace 6\n!barcode 0 64\n*barcode c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度9高度64居中)\n!barcode 9 64\n*barcode c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度4高度0居中)\n!barcode 4 0\n*barcode c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度4高度328居中)\n!barcode 4 328\n*barcode c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度128居中,设置高度后还有空格)\n!barcode 2 128 \n*barcode c 0123456789ABC\n*line\n*feedline 1\n"+//设置高度之后还有空格
				"!NLFONT 9 12\n*text c 以下打印二维码(纠错级别参数为4)\n!qrcode 100 4\n*qrcode c ABC123456789DEFGH\n*line\n*feedline 1\n"+					
				"!NLFONT 9 12\n*text c 以下打印二维码(纠错级别参数为-1)\n!qrcode 100 -1\n*qrcode c ABC123456789DEFGH\n*line\n*feedline 1\n!"+
				//新增BARCODE和QRCODE异常情况
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度0高度64居中)\n!gray 5\n!yspace 6\n!BARCODE 0 64 3\n*BARCODE c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度9高度64居中)\n!BARCODE 9 64 3\n*BARCODE c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度4高度0居中)\n!BARCODE 4 0 3\n*BARCODE c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度4高度328居中)\n!BARCODE 4 328 3\n*BARCODE c 0123456789ABC\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度128居中,设置编码方式后还有空格)\n!BARCODE 2 128 3 \n*BARCODE c 0123456789ABC\n*line\n*feedline 1\n"+//设置高度之后还有空格
				"!NLFONT 9 12\n*text c 以下打印二维码(纠错级别参数为4)\n!QRCODE 100 4 3\n*QRCODE c ABC123456789DEFGH\n*line\n*feedline 1\n"+					
				"!NLFONT 9 12\n*text c 以下打印二维码(纠错级别参数为-1)\n!QRCODE 100 -1 3\n*QRCODE c ABC123456789DEFGH\n*line\n*feedline 1\n!NLPRNOVER\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式CODEBAR,数据格式不符合编码规范)\n!BARCODE 2 64 0\n*BARCODE c 123456784\n*line\n*feedline 1\n"+						
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式CODE39,数据格式不符合编码规范)\n!BARCODE 2 64 1\n*BARCODE c !#*123456784\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式CODE93,数据格式不符合编码规范)\n!BARCODE 2 64 2\n*BARCODE c !#*123456784\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式EAN-8/JAN-8,数据校验位错误)\n!BARCODE 2 64 4\n*BARCODE c 34569837\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式EAN-13/JAN-13,数据校验位错误)\n!BARCODE 2 64 4\n*BARCODE c 0123456789019\n*line\n*feedline 1\n"+			
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式EAN/JAN,数据长度错误)\n!BARCODE 2 64 4\n*BARCODE c 012345678901234567\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式ITF,数据长度错误)\n!BARCODE 2 64 5\n*BARCODE c 3801234567899123456\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式UPC-A,数据长度错误)\n!BARCODE 2 64 6\n*BARCODE c 3801234567899123456\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式UPC-E,数据长度错误)\n!BARCODE 2 64 7\n*BARCODE c 01234567890\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式UPC-E,第一位数据内容错误)\n!BARCODE 2 64 7\n*BARCODE c 21234569\n*line\n*feedline 1\n"+				
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式设置错误)\n!BARCODE 2 64 8\n*BARCODE c 21234569\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印条形码(宽度2高度64居中,编码方式设置错误)\n!BARCODE 2 64 -1\n*BARCODE c 21234569\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度200居右,编码方式设置错误)\n!QRCODE 200 3 4\n*QRCODE r ABC123456789DEFGH\n*line\n*feedline 1\n"+
				"!NLFONT 9 12\n*text c 以下打印二维码(高度200居右,编码方式设置错误)\n!QRCODE 200 3 -1\n*QRCODE r ABC123456789DEFGH\n*line\n*feedline 1\n";
				
	
	String datatest= "!font /sdcard/simsun.ttc\n!NLFONT 9 12\n*text c 以下打印二维码(高度200居中)请扫描打印出的二维码内容是否为:Aa12%#@*&αΒγàÚǒ\n!QRCODE 200 0 0\n*QRCODE c Aa12%#@*&αΒγàÚǒ\n*line\n*feedline 1\n";
	PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
	int height,width,printerResult;
	int printerStatus;
	private String fileName=Printer2.class.getSimpleName();
	public void printer2()
	{
		String funcName="printer2";
//		/**X5产品需要替换libnlprintex.so*/
//		if(GlobalVariable.currentPlatform==Model_Type.X5)
//		{
//			if(gui.cls_show_msg("X5设备需要使用服务器上的libnlprintex.so替换HighPlattest工程下libs目录下的全部libnlprintex.so，已完成任意键继续测试，ESC键退出")==ESC)
//			{
//				unitEnd();
//				return;
//			}
//		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.unitPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s【默认放到/sdcard/picture目录下，ums文件需要放到/data/share目录下】,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("测试项\n0.ttf脚本打印\n1.ttf脚本PNG图片测试\n2.打印机状态检测\n3.异常测试\n4.无阻塞ttf脚本打印\n5.无阻塞ttf图片打印\n6.异常打印变更验证");
			switch (nkeyIn) 
			{
			case '0':
				ttfScriptPrint();
				break;
			case '1':
				picPrint();
				break;
				
			case '2':
				stateTest();
				break;
			case '3':
				exceptTest();
				break;
		
			case '4':
				ttfScriptPrint_2(); 		//开发需求，使用非阻塞TTF打印即多张连续打印，打印到最后一张才使用阻塞打印。目前客户并未使用该流程，用于验证是否卡顿 
				break;
			case '5':
				ttfScriptPrint_3(); 		//开发需求，使用非阻塞TTF打印即多张连续打印，打印到最后一张才使用阻塞打印。目前客户并未使用该流程，用于验证是否卡顿 
				break;
			case'6':
				exceptTest2();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	//验证异常打印后，打印不恢复默认，而是跟上一次设置的一致
	private void exceptTest2() {
		String outpaper=
				"*text c 缺纸测试--\n*line\n"+
				"*text c 缺纸测试--\n*line\n"+
				"*text c 缺纸测试--\n*line\n"+
				"*text c 缺纸测试--\n*line\n"+
				"*text c 缺纸测试--\n*line\n"+
				"*text c 缺纸测试--\n*line\n";
		gui.cls_show_msg("打印异常后，打印不应恢复默认格式，应与上次正常打印格式一致，按任意键继续");
		printerResult = printUtil.print_byttfScript(strData15);
//		gui.cls_show_msg("", args)
		//case1 传入图片格式错误异常  返回-1
		printerResult = printUtil.print_byttfScript("*text c 打印图片类型非法测试，应打不出图片\n*image c 361*361 path:/mnt/sdcard/picture/xdl.jpg\n*line\n");
		if(printerResult!=NDK_ERR_DECODE_IMAGE)
		{
			gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,printerResult);
		}
		gui.cls_show_msg("case1.本次打印内容格式请与第一次打印对比，应一致按确认键继续");
		printerResult = printUtil.print_byttfScript(strData16);
	
		//case2 参数错误 返回-6
		printerResult = printUtil.print_byttfScript("*text c 参数非法测试");
		if(printerResult!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,printerResult);
		}
		gui.cls_show_msg("case2.本次打印内容格式请与第一次打印对比，应一致按确认键继续");
		printerResult = printUtil.print_byttfScript(strData16);
	
		//case3 文件路径非法 返回-7
		printerResult = printUtil.print_byttfScript("*text c 打印图片路径错误测试，应打不出图片\n*image c 361*361 path:/mnt/sdcard/data/picture/xdl.png\n*line\n");
		if(printerResult!=NDK_ERR_PATH)
		{
			gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,printerResult);
		}
		gui.cls_show_msg("case3.本次打印内容格式请与第一次打印对比，应一致按确认键继续");
		printerResult = printUtil.print_byttfScript(strData16);
		//case4  缺纸 返回2
		gui.cls_show_msg("case4.请放置少量打印纸，构造缺纸。确认键继续");
		while (true) {
			printerResult = printUtil.print_byttfScript(outpaper);
			if (printerResult==2) {
				gui.cls_show_msg( "缺纸");
				break;
			}
			
		}
		gui.cls_show_msg("case4.请放入打印纸。本次打印内容格式请与第一次打印对比，应一致按确认键继续");
		printerResult = printUtil.print_byttfScript(strData16);
		gui.cls_show_msg1_record(fileName, "ttfScriptPrint_3", gKeepTimeErr,"异常打印后，打印不恢复默认，而是跟上一次设置的一致,测试通过");
		
	}
	
	private void ttfScriptPrint_3() {
		if(printUtil.getPrintStatusOri()==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg("打印机缺纸，请放入打印纸，任意键继续");
		}
		String[][] arrayWay = {{"l","ysz","居左"},{"c","color","居中"},{"r","IHDR","居右"},{"l","other","居左"}};
		int[] arrayLen = {7/**艺术字系类（居左）*/,6/**color系类（居中）*/,15/**IHDR系类（居右）*/,16/**其他系类（居左）*/};
		for (int i = 0; i < 4; i++) {
			for (int j = 1; j <=arrayLen[i]; j++) {
				String picPath = Environment.getExternalStorageDirectory().getPath()+"/picture/"+arrayWay[i][1]+j+".png";
				Bitmap bit = BitmapFactory.decodeFile(picPath);
				
				width = bit.getWidth();
				if(width>384)
				{
					float multiple = (float) (width/384.0);
					height = (int) (bit.getHeight()/multiple);
					width = (int) (width/multiple);
				}
				else
					height = bit.getHeight();
				
				if (i==3&&j==16) {
					printerResult = printUtil.print_byttfScript(String.format("!NLFONT 9 12 3\n*text c 以下打印PNG图片,阈值自动(%s),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image %s %s path:%s\n*line\n*feedline 1\n",arrayWay[i][2],arrayWay[i][0],width+"*"+height,picPath));
				}else {
					printerResult = printUtil.print_byttfScript_ccb(String.format("!NLFONT 9 12 3\n*text c 以下打印PNG图片,阈值自动(%s),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image %s %s path:%s\n*line\n*feedline 1\n",arrayWay[i][2],arrayWay[i][0],width+"*"+height,picPath));
				}
//				printerResult = printUtil.print_byttfScript(String.format("!NLFONT 9 12 3\n*text c 以下打印PNG图片,阈值自动(%s),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image %s %s path:%s\n*line\n*feedline 1\n",arrayWay[i][2],arrayWay[i][0],width+"*"+height,picPath));
				if(printerResult!=NDK_OK)
				{
					gui.cls_show_msg1_record(fileName, "ttfScriptPrint_3", gKeepTimeErr,"line %d:%s图片打印失败(ret=%d)", Tools.getLineInfo(),arrayWay[i][1]+j,printerResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		gui.cls_show_msg1_record(fileName, "ttfScriptPrint_3", gKeepTimeErr,"ttf脚本打印图片打印效果要与预期提示语一致，并且跟printer3脚本打印效果对比一致，视为通过，反之不通过");
		
	}
	private void ttfScriptPrint_2() {
		if(printUtil.getPrintStatusOri()==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg("打印机缺纸，请放入打印纸，任意键继续");
		}
		String[] strArray = {strData1,strData2,strData3,strData4,strData5,strData6_1,strData6_2,strData6_3,strData6_4,strData6_5
				,strData7,strData8,strData9,strData10,strData11,strData12};
		// case1:进行脚本打印测试
		for (int i = 0; i < strArray.length; i++) 
		{	
			if (i==15) {
				printerResult = printUtil.print_byttfScript(strArray[i]);
			}else {
				printerResult = printUtil.print_byttfScript_ccb(strArray[i]);
			}
			switch (printerResult) {
			case NDK_OK:
				
				break;
				
			case  2:// 缺纸
				gui.cls_show_msg("打印机缺纸，请装纸...完成点任意键继续");
				break;

			default:
				gui.cls_show_msg1_record(fileName, "ttfScriptPrint", gKeepTimeErr, "line %d:第%d次：%s测试失败(%s)", Tools.getLineInfo(),i+1,TESTITEM,printerResult);
				if(!GlobalVariable.isContinue)
					return;
				break;
			}
		}
		gui.cls_show_msg1_record(fileName, "ttfScriptPrint", gKeepTimeErr,"ttf脚本打印效果与预期提示语一致，并且跟printer3脚本打印效果对比一致，视为通过，反之不通过");
		
	}
	public void ttfScriptPrint(){
		if(printUtil.getPrintStatusOri()==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg("打印机缺纸，请放入打印纸，任意键继续");
		}
//		gui.cls_show_msg("请确保已放入打印纸，并使用cpoyfile.apk将sdcard/pictures/ums文件推到data/share目录下任意键继续");
		String[] strArray = {strData1,strData2,strData3,strData4,strData5,strData6_1,strData6_2,strData6_3,strData6_4,strData6_5
				,strData7,strData8,strData9,strData10,strData11,strData12,strData13,strData14};
		// case1:进行脚本打印测试
		for (int i = 0; i < strArray.length; i++) 
		{	
			
			printerResult = printUtil.print_byttfScript(strArray[i]);
			switch (printerResult) {
			case NDK_OK:
				
				break;
				
			case  2:// 缺纸
				gui.cls_show_msg("打印机缺纸，请装纸...完成点任意键继续");
				break;

			default:
				gui.cls_show_msg1_record(fileName, "ttfScriptPrint", gKeepTimeErr, "line %d:第%d次：%s测试失败(%s)", Tools.getLineInfo(),i+1,TESTITEM,printerResult);
				if(!GlobalVariable.isContinue)
					return;
				break;
			}
		}
		gui.cls_show_msg1_record(fileName, "ttfScriptPrint", gKeepTimeErr,"ttf脚本打印效果与预期提示语一致，并且跟printer3脚本打印效果对比一致，视为通过，反之不通过");
		
	}
	public void picPrint(){
		// case4:进行目前收集到的全部PNG图片测试（包括color系类、IHDR系类、艺术字系类，其他系类）
//		gui.cls_show_msg("请确保已放入打印纸，任意键继续");
		if(printUtil.getPrintStatusOri()==EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg("打印机缺纸，请放入打印纸，任意键继续");
		}
		String[][] arrayWay = {{"l","ysz","居左"},{"c","color","居中"},{"r","IHDR","居右"},{"l","other","居左"}};
		int[] arrayLen = {7/**艺术字系类（居左）*/,6/**color系类（居中）*/,15/**IHDR系类（居右）*/,16/**其他系类（居左）*/};
		for (int i = 0; i < 4; i++) {
			for (int j = 1; j <=arrayLen[i]; j++) {
				String picPath = Environment.getExternalStorageDirectory().getPath()+"/picture/"+arrayWay[i][1]+j+".png";
				Bitmap bit = BitmapFactory.decodeFile(picPath);
				
				width = bit.getWidth();
				if(width>384)
				{
					float multiple = (float) (width/384.0);
					height = (int) (bit.getHeight()/multiple);
					width = (int) (width/multiple);
				}
				else
					height = bit.getHeight();
				printerResult = printUtil.print_byttfScript(String.format("!NLFONT 9 12 3\n*text c 以下打印PNG图片,阈值自动(%s),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image %s %s path:%s\n*line\n*feedline 1\n",arrayWay[i][2],arrayWay[i][0],width+"*"+height,picPath));
				if(printerResult!=NDK_OK)
				{
					gui.cls_show_msg1_record(fileName, "picPrint", gKeepTimeErr,"line %d:%s图片打印失败(ret=%d)", Tools.getLineInfo(),arrayWay[i][1]+j,printerResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		gui.cls_show_msg1_record(fileName, "picPrint", gKeepTimeErr,"ttf脚本打印图片打印效果要与预期提示语一致，并且跟printer3脚本打印效果对比一致，视为通过，反之不通过");

	}
	public void stateTest(){
		gui.cls_show_msg("请确保未放入打印纸，任意键继续");
		if((printerStatus = printUtil.getPrintStatusOri())!= EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg1_record(fileName, "stateTest", gKeepTimeErr,"line %d:%s测试失败(status = %d)", Tools.getLineInfo(),TESTITEM,printerStatus);
			return;
		}
		gui.cls_show_msg1_record(fileName, "stateTest", gKeepTimeErr,"测试通过");
		
	}
	public void  exceptTest(){
		// case5.1:传入的图片非PNG类型（比如bmp或jpg），应返回失败（-1）
		//2019/2/1 改成 返回图片解码失败（-8）
		printerResult = printUtil.print_byttfScript("!NLFONT 9 12\n*text c 打印图片类型非法测试，应打不出图片\n*image c 361*361 path:/mnt/sdcard/picture/xdl.jpg\n*line\n*feedline 1\n");
		if(printerResult!=NDK_ERR_DECODE_IMAGE)
		{
			gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,printerResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5.2:设置打印的宽度大于384(X5为576)，应返回失败0
		//x5为576 add 2019/2/1 
		if(GlobalVariable.currentPlatform==Model_Type.X5){
			printerResult = printUtil.print_byttfScript("!NLFONT 9 12\n*text c 打印图片宽度>576异常测试，按576处理，应可以打印出图片\n*image c 577*361 path:/mnt/sdcard/picture/IHDR1.png\n*line\n*feedline 1\n");
			if(printerResult!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,printerResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}else
		{
			printerResult = printUtil.print_byttfScript("!NLFONT 9 12\n*text c 打印图片宽度>384异常测试，按384处理，应可以打印出图片\n*image c 385*361 path:/mnt/sdcard/picture/IHDR1.png\n*line\n*feedline 1\n");
			if(printerResult!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,printerResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case2:进行异常测试
		printUtil.print_byScript(errString);
		
		// case3:打印条形码或二维码的时候进入缺纸应返回缺纸
		gui.cls_show_msg("请装入2-3cm的纸后点任意键继续");
		printerResult = printUtil.print_byttfScript("!NLFONT 9 12\n*text c 以下打印条形码(宽度8高度320居左)请扫描打印出的条形码内容是否为:123\n!barcode 8 320\n*barcode l 123\n*line\n*feedline 1\n");
		if(printerResult!= EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s获取缺纸状态失败(status = %d)", Tools.getLineInfo(),TESTITEM,printerResult);
			return;
		}
			
		gui.cls_show_msg("请再装入2-3cm的纸后点任意键继续");
		printerResult = printUtil.print_byttfScript("!NLFONT 9 12\n*text c 以下打印二维码(高度200居右)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 200 3\n*qrcode r ABC123456789DEFGH\n*line\n*feedline 1\n");
		if(printerResult!= EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
		{
			gui.cls_show_msg1_record(fileName, "exceptTest",gKeepTimeErr,"line %d:%s获取缺纸状态失败(status = %d)", Tools.getLineInfo(),TESTITEM,printerResult);
			return;
		}
		gui.cls_show_msg1_record(fileName, "exceptTest",gScreenTime,"异常测试通过");
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
