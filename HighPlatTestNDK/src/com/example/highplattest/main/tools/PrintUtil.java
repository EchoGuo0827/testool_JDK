package com.example.highplattest.main.tools;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_MODE;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.newland.me.module.printer.TTFPrint;
import com.newland.ndk.JniNdk;

/**
 * 打印工具类
 * @author zhengxq
 * 2016-4-6 下午4:53:42
 */
public class PrintUtil implements NDK,Lib
{
	//private Printer printer;
	private Context context;
	private Handler handler;

	public PrintUtil(Context context,Handler handler,boolean isInit) 
	{
		this.context = context;
		this.handler = handler;
		if(isInit)
		{
			switch (GlobalVariable.currentPlatform) 
			{
			case X5:
				//打印机初始化
				JniNdk.JNI_PrnModuleInit();
				break;
				
			default:
				JniNdk.JNI_Print_Init(1);
				break;
			}
		}
	}


	// 获取打印状态类，打印结果共有11种		//增加可选参数给给多模块并发用例使用   add by xuess 20171013
	public int getPrintStatus(int timeout,int... values) 
	{
		Gui gui;
		if( values!=null && values.length >0){
			int handlerMsg = values[0];
			gui = new Gui(context, handler, handlerMsg);
		}else{
			gui = new Gui(context, handler);
		}
		boolean busy = false;
		long oldTime = 0;
		String className = this.getClass().getSimpleName();
		String funName = Thread.currentThread().getStackTrace()[1].getMethodName();
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<timeout)
		{
			int printerStatus=JniNdk.JNI_Print_GetStatus();
			if(printerStatus == EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				return printerStatus;
			}
			else if(printerStatus == EM_PRN_STATUS.PRN_STATUS_BUSY.getValue())
			{
				gui.cls_printf("正在打印，请稍后...".getBytes());
				if(!busy)
				{
					oldTime = System.currentTimeMillis();
					busy = true;
				}
				if(Tools.getStopTime(oldTime)>timeout)
				{
					gui.cls_show_msg1_record(className, funName, 2,"%s.line %d打印机处于忙状态时间过长，请检查\n", funName,Tools.getLineInfo());
					return printerStatus;
				}
				continue;
			}
			else if(printerStatus == EM_PRN_STATUS.PRN_STATUS_OVERHEAT.getValue())
			{
				gui.cls_show_msg1_record(className, funName,2, "%s,line %d:打印机过热\n", funName,Tools.getLineInfo());
				return printerStatus;
			}
			else if(printerStatus == EM_PRN_STATUS.PRN_STATUS_NOPAPER.getValue())
			{
				// 这边缺纸会一直往result文件写数据，有待优化
				if(gui.cls_show_msg("打印机缺纸!装纸后,完成点[确认]\n若是缺纸测试则不需放纸任意键继续")!=ENTER)
				{
					return printerStatus;
				}
				else
					return JniNdk.JNI_Print_GetStatus();
			}
			else if(printerStatus == EM_PRN_STATUS.PRN_STATUS_VOLERR.getValue())
			{
				gui.cls_show_msg1_record(className, funName,2, "打印机电压异常");
				return printerStatus;
			}
			else if(printerStatus == EM_PRN_STATUS.PRN_STATUS_PPSERR.getValue())
			{
				gui.cls_show_msg1_record(className, funName,2, "打印机轴不在位");
				return printerStatus;
			}
		}
		return EM_PRN_STATUS.PRN_STATUS_OK.getValue();
	}
	
	public int getPrintStatusOri() 
	{
		return JniNdk.JNI_Print_GetStatus();
	}

	// 模拟自检打印流程，打印字符串失败后缓存异常 by wangxy20180704
	public int printStrForDetect(String content) {
		int ret = -1;
		JniNdk.JNI_Print_Init(1);
		if ((ret = JniNdk.JNI_Print_SetForm(0, 0, 4)) != NDK_OK)
			return ret;
		//设置成中文24*24，英文12*24C的字体，打印中途缺纸后，缓存异常的主要步骤
		if ((ret = JniNdk.JNI_Print_SetFont(1, 18)) != NDK_OK)
			return ret;
		if ((ret = JniNdk.JNI_Print_Str(content)) != NDK_OK)
			return ret;
		if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK)
			return ret;
		return NDK_OK;

	}
	
	// 打印字符串
	public int printStr(String content) 
	{
		int ret=-1;
		//设置成默认字体，以防其他项干扰
		if((ret=JniNdk.JNI_Print_SetFont(6, 1))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Str(content))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		 return NDK_OK;
			
	}
	
	// 打印字符串(参数设置)字体和模式都可以设置
	public int printStr(String content,int emHZFont,int emZMFontint ,int emMode,int unSigOrDou) 
	{
		int ret;
		if((ret=JniNdk.JNI_Print_SetFont(emHZFont,emZMFontint))!=NDK_OK)
			return ret;
//		打印单双向选择0--单向 1--双向
		if((ret=JniNdk.JNI_Print_SetMode(emMode, unSigOrDou))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Str(content))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		return NDK_OK;
	}
	// 打印字符串(参数设置) 字体默认 设置模式
	public int printStr(String content,int emMode,int unSigOrDou) 
	{
		int ret;
		if((ret=JniNdk.JNI_Print_SetFont(6,1))!=NDK_OK)
			return ret;
//		打印单双向选择0--单向 1--双向
		if((ret=JniNdk.JNI_Print_SetMode(emMode, unSigOrDou))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Str(content))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		return NDK_OK;
		
		
	}
	

	// 该函数实现对图像进行二值化处理
	public Bitmap gray2Binary(Bitmap graymap) 
	{
	 	// 得到图形的宽度和长度
	 	int width = graymap.getWidth();
	 	int height = graymap.getHeight();
	 	
	 	/*去除图片的透明度*/
	 	graymap=compressPic(graymap);
	 	int t=myOtsu(graymap);
	 	LoggerUtil.d("yuzhi:"+t);
	 	// 创建二值化图像
	 	Bitmap binarymap = null;
	 	binarymap = graymap.copy(Config.ARGB_8888, true);
	 	// 依次循环，对图像的像素进行处理
	 	for (int i = 0; i < width; i++) {
	 		for (int j = 0; j < height; j++) {
	 			// 得到当前像素的值
	 			int col = binarymap.getPixel(i, j);
	 			// 得到alpha通道的值
	 			int alpha = col & 0xFF000000;
	 			// 得到图像的像素RGB的值
	 			int red = (col & 0x00FF0000) >> 16;
	 			int green = (col & 0x0000FF00) >> 8;
	 			int blue = (col & 0x000000FF);
	 			// 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
	 			int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
	 			// 对图像进行二值化处理
	 			if (gray <= t) {
	 				gray =0;
	 			} else {
	 				gray = 255;
	 			}
	 			
	 			// 新的ARGB
	 			int newColor = alpha | (gray << 16) | (gray << 8) | gray;
//	 			LoggerUtil.d("newColor:"+"("+i+","+j+","+newColor+")");
	 			// 设置新图像的当前像素值
	 			binarymap.setPixel(i, j, newColor);
	 		}
	 	}
	 	return binarymap;
	 }
	 	  
	 /**
	   * 对图片进行压缩（去除透明度）
	   * @param bitmapOrg
	 */
	public Bitmap compressPic(Bitmap bitmapOrg) {
		// 获取这个图片的宽和高
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		Bitmap targetBmp = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		Canvas targetCanvas = new Canvas(targetBmp);
		targetCanvas.drawColor(0xffffffff);
		targetCanvas.drawBitmap(bitmapOrg, new Rect(0, 0, width, height),new Rect(0, 0, width, height), null);
		return targetBmp;
	}

	/**
	 * 大津法求阈值 自适应阈值
	 * 
	 * @param graymap
	 * @return
	 */
	public int myOtsu(Bitmap graymap) {
		int y;
		int width = graymap.getWidth();
		int height = graymap.getHeight();
		int pixelCount[] = new int[256];
		float pixelPro[] = new float[256];
		int i, j, pixelSum = width * height, threshold = 0;
		int[] pixels = new int[width * height]; // 存放像素RGB值
		graymap.getPixels(pixels, 0, width, 0, 0, width, height);
		int data[] = new int[width * height * 3];
		int kk = 0;
		for (j = 0; j < width; j++) {
			for (i = 0; i < height; i++) {
				int col = graymap.getPixel(j, i);
				int r = (col & 0x00FF0000) >> 16;
				int g = (col & 0x0000FF00) >> 8;
				int b = (col & 0x000000FF);
				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				y = y < 255 ? y : 255;
				y = y > 0 ? y : 0;
				data[kk] = y;
				kk++;
			}
		}
		// 统计每个灰度级中像素的个数
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				pixelCount[data[i * width + j]]++;
			}
		}

		// 计算每个灰度级的像素数目占整幅图像的比例
		for (i = 0; i < 256; i++) {
			pixelPro[i] = (float) pixelCount[i] / pixelSum;
		}

		// 遍历灰度级[0,255],寻找合适的threshold
		float w0, w1, u0tmp, u1tmp, u0, u1, deltaTmp, deltaMax = 0;
		for (i = 0; i < 256; i++) {
			w0 = w1 = u0tmp = u1tmp = u0 = u1 = deltaTmp = 0;
			for (j = 0; j < 256; j++) {
				if (j <= i) // 背景部分
				{
					w0 += pixelPro[j];
					u0tmp += j * pixelPro[j];
				} else // 前景部分
				{
					w1 += pixelPro[j];
					u1tmp += j * pixelPro[j];
				}
			}
			u0 = u0tmp / w0;
			u1 = u1tmp / w1;
			deltaTmp = (float) (w0 * w1 * (u0 - u1) * (u0 - u1));
			if (deltaTmp > deltaMax) {
				deltaMax = deltaTmp;
				threshold = i;
			}
		}
		return threshold;
	}

	private int getWidthBytesLen(int width) {
		if (width % 8 == 0) {
			return width / 8;
		} else {
			return width / 8 + 1;
		}
	}

	   
	private int getByteOffset(int x, int y, int width) {
		return y * getWidthBytesLen(width) + x / 8;
	}

	private int getBitOffset(int x) {
		return 7 - (x % 8);
	}

	public byte[] calcBuffer(Bitmap bitmap, int height, int width) {
		int WHITE = 0x00FFFFFF;
		byte[] buffer = new byte[getWidthBytesLen(width) * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int byteoffset = getByteOffset(x, y, width);
				int bitoffset = getBitOffset(x);
				int c = bitmap.getPixel(x, y) & WHITE;
				if (c != WHITE) // 颜色为00 代表黑色
					buffer[byteoffset] = (byte) (((1 << bitoffset) | buffer[byteoffset]) & 0xff); // 点阵1代表黑色

			}
		}
		return buffer;
	}

	public int printPng(String path, int position) {
		// 设置图片为单倍宽、单倍高
		int ret;
		Bitmap bit = BitmapFactory.decodeFile(path);
		bit = gray2Binary(bit);
		int height = bit.getHeight();
		int width = bit.getWidth();
		byte[] imgBuf = calcBuffer(bit, height, width);
		if ((ret = JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0)) != NDK_OK)
			return ret;
		if ((ret = JniNdk.JNI_Print_Image(width, height, position, imgBuf)) != NDK_OK)
			return ret;
		if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK)
			return ret;
		return NDK_OK;
	}

	// 打印图片 偏移为0 传Bitmap
	public int printImg(Bitmap bitmap) {
		int ret;
		bitmap = gray2Binary(bitmap);
		// 设置单倍宽、单倍高
		if ((ret = JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0)) != NDK_OK)
			return ret;
		byte[] imgBuf = calcBuffer(bitmap, bitmap.getHeight(),bitmap.getWidth());
		if ((ret = JniNdk.JNI_Print_Image(bitmap.getWidth(),bitmap.getHeight(), 0, imgBuf)) != NDK_OK)
			return ret;
		if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK)
			return ret;
		return NDK_OK;
	}

	// 打印图片信息(参数设置)
	public int printImg(int position, Bitmap bitmap) 
	{
		int ret;
		bitmap=gray2Binary(bitmap);
		// 设置单倍宽、单倍高
		if((ret=JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0))!=NDK_OK)
			return ret;
		byte[] imgBuf=calcBuffer(bitmap,bitmap.getHeight(),bitmap.getWidth());
		if((ret=JniNdk.JNI_Print_Image(bitmap.getWidth(), bitmap.getHeight(), position, imgBuf))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		return NDK_OK;
	}
	
	// 打印三角形
	public int print_triangle() 
	{
		printStr("---以下打印左右大斜三角---\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(), 0);
		Bitmap zxbmp = BitmapFactory.decodeFile(GlobalVariable.sdPath+"picture/other13.png");
		printImg(zxbmp);
		printStr("\n");
		Bitmap yxbmp = BitmapFactory.decodeFile(GlobalVariable.sdPath+"picture/other14.png");
		printImg(yxbmp);
		printStr("\n");
		return printStr("\n");
	}

	// 打印测试页
	public int print_testpage() 
	{
		print_png(GlobalVariable.sdPath+"picture/other12.png");/**建设银行图标*/
//		printStr("---以下打印测试页---\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
//		printStr("\n");
//		printStr("欢迎使用福建新大陆电脑公司NL系列POS热敏打印机\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
//		printStr("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
//		printStr("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
//		printStr("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n",EM_PRN_MODE.PRN_MODE_HEIGHT_DOUBLE.getValue(),0);
//		printStr("\n 惜缘 惜福 惭愧 感恩!\n");
//		printStr("\n 惜缘 惜福 惭愧 感恩!\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
//		return printStr("\n 惜缘 惜福 惭愧 感恩!\n",EM_PRN_MODE.PRN_MODE_HEIGHT_DOUBLE.getValue(),0);
		printStr("---以下打印测试页---\n");
		printStr("\n");
		printStr("欢迎使用福建新大陆电脑公司NL系列POS热敏打印机\n");
		printStr("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
		printStr("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
		printStr("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
		printStr("\n 惜缘 惜福 惭愧 感恩!\n");
		printStr("\n 惜缘 惜福 惭愧 感恩!\n");
		return printStr("\n 惜缘 惜福 惭愧 感恩!\n");
	}
	
	// 打印各种钱币符号
	public int print_coin_sym()
	{
		printStr("---以下打印各种钱币符号---\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		printStr("\n打印美元符号：$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");
		printStr("\n打印人民币符号：￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥\n");
		printStr("\n本行打印英镑符号：￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡￡\n");
		// 欧元符号有点问题，确认后还是有问题
		return printStr("\n本行打印欧元符号：€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€\n\n");
	}
	
	// 打印测试页新增
	public int print_testpage_new()
	{
		print_png(GlobalVariable.sdPath+"picture/other12.png");/**建设银行图标*/
		StringBuffer printResult = new StringBuffer();
		printResult.append("---以下打印测试页---\n");
		printResult.append("\n");
		printResult.append("欢迎使用福建新大陆电脑公司NL系列POS热敏打印机\n");
		printResult.append("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
		printResult.append("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
		printResult.append("\n !\"#$%&()*./0123456789;<=>?@ABCDEFG\\^_`abcdejfgz{|}~\n");
		printResult.append("\n 惜缘 惜福 惭愧 感恩!\n");
		printResult.append("\n 惜缘 惜福 惭愧 感恩!\n");
		printResult.append("\n 惜缘 惜福 惭愧 感恩!\n");
		return printStr(printResult.toString());
	}
	
	// 打印联迪单项，与联迪的速度进行比较
	public int landi_single()
	{
		String data = "*feedline 5\n"+
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
				"*image l 384*83 path:yz:50;/mnt/shell/emulated/0/picture/other15.png\n"+/**联迪黑块*/
				"!yspace 0\n"+//将行间距设置为打印初始化值0
				"*feedline 5\n";// 打印png图片
		return print_byScript(data);
	}
	
	//打印随即数
	public int print_rand() 
	{
		int i, hightPos;
		String str = null;
		Random random = new Random();
		byte[] buf = new byte[64];
		for (i = 0; i < buf.length; i++) 
		{
			// lowPos = (161 + Math.abs(random.nextInt(93)));//获取低位值
			hightPos = (176 + Math.abs(random.nextInt(39)));// 获取高位值
			buf[i] = (Integer.valueOf(hightPos).byteValue());
		}
		try {
			str = new String(buf, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		printStr("---以下打印32个随机数---\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0 );
		printStr(str);
		return printStr("\n\n\n");
	}

	// 打印20行"国"字
	public int print_guo() {
		StringBuilder resultContent = new StringBuilder();
		int GUO = 320;
		printStr("---以下打印320个国字---\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		for (int i = 0; i < GUO; i++) 
		{
			resultContent.append("国");
		}
		resultContent.append("\n\n\n");
		return printStr(resultContent.toString());
	}
		
	// 打印填充单
	public int print_fill() 
	{
		printStr("---以下打印填充单---\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		return print_png(GlobalVariable.sdPath+"picture/other11.png");/**填充单*/
	}
	
	// 打印票据
	public int print_bill() 
	{
		String data = 
		"!NLFONT 9 12 2\n"+
		"!yspace 3\n"+
		"*text c 银联POS签购单\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 持卡人存根            CUSTOMER COPY\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 商户名称:测试商户名称\n"+
		"*text l 商户号:123456789012345\n"+
		"*text l 终端编号:12345678  操作员号:01\n"+
		"!NLFONT 6 6\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 收 单 行:收单银行 12345678\n"+
		"*text l 发 卡 行:发卡银行 12345678\n"+
		"*text l 卡    号:\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  62001*******12345 /S\n"+
		"!NLFONT 9 12 2\n"+
		"*text l  消费/SALE\n"+
		"!NLFONT 9 12 3\n"+
		"*text l 批次号:000003  凭证号:000934\n"+
		"*text l 参考号:046879087564\n"+
		"*text l 授权码:012345\n"+
		"*text l 日期时间:2015/06/12 20:21:53\n"+
		"*text l 金 额:\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  RMB 12345678.90\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 应用标识(AID)1234567890123456\n"+
		"*text l 这里是发卡银行备注信息\n"+
		"*text l 郑重申明:该笔交易为测试状态,属无效交易!\n"+
		"*text l 备注:\n"+
		"!NLFONT 6 1\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 持卡人签名:\n"+
		"*text l\n"+  
		"*text l\n"+  
		"!NLFONT 6 1 3\n"+
		"*text l 本人确认以上交易，同意将其记入本卡账户\n"+
		"*text l I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICE\n"+
		"*line\n"+
		"!yspace 0\n"+//将行间距设置为打印初始化值0
		"!NLPRNOVER\n";
		return print_byScript(data);
	}
	// 打印票据,x5打印尺寸为3寸 add by wangxy 20180925
	public int print_for_x5() 
	{
		String data = "!NLFONT 1 12\n*text c 以下打印汉字,如果打印纸尺寸设置为默认2寸则第一行应有24个中文国字；"
				+ "设置为3寸则第一行应有36个中文国字;其余国字换行打印;\n!hz s\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n";
		return print_byScript(data);
	}
	//打印竖条纹
	public int print_verticalbill() 
	{
		printStr("---以下打印竖条纹---\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		return print_png(GlobalVariable.sdPath+"picture/other10.png"); /**竖条纹*/
	}

	public int print_blank() 
	{
		printStr("---以下打印美团图片---\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		return print_png(GlobalVariable.sdPath+"picture/other5.png"); /**美团图片*/
	}
	
	//模式字
	public int print_font() 
	{
		printStr("---以下打印各种字体---\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		printStr("以下先打图形再打文字\n");
		print_png(GlobalVariable.sdPath+"picture/other12.png");/**新大陆图片*/
		print_png(GlobalVariable.sdPath+"picture/other3.png");/**建设银行图片*/
	
		printStr("01234新大陆欢迎您\n0123456NewLandComputer Welcome you!\n~!@#$%^&*()_+|{[}]:;'<,>.?/\\=-dsfh\n");
		printStr(
				"01234新大陆欢迎您\n0123456NewLandComputer Welcome you!\n~!@#$%^&*()_+|{[}]:;'<,>.?/\\=-dsfh\n",
				EM_PRN_MODE.PRN_MODE_NORMAL.getValue(),0);
		printStr(
				"01234新大陆欢迎您\n0123456NewLandComputer Welcome you!\n~!@#$%^&*()_+|{[}]:;'<,>.?/\\=-dsfh\n",
				EM_PRN_MODE.PRN_MODE_NORMAL.getValue(),0);
		printStr(
				"01234新大陆欢迎您\n0123456NewLandComputer Welcome you!\n~!@#$%^&*()_+|{[}]:;'<,>.?/\\=-dsfh\n",
				EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		printStr(
				"01234新大陆欢迎您\n0123456NewLandComputer Welcome you!\n~!@#$%^&*()_+|{[}]:;'<,>.?/\\=-dsfh\n",
				EM_PRN_MODE.PRN_MODE_HEIGHT_DOUBLE.getValue(),0);
		printStr("android端时间：" + Tools.getSysNowTime()+"\n");
//		printStr("K21端时间：" + posController.getK21Date().toString()+"\n");
		printStr(Tools.getSysNowTime()+"\n", EM_PRN_MODE.PRN_MODE_NORMAL.getValue(),0);
		printStr(Tools.getSysNowTime()+"\n", EM_PRN_MODE.PRN_MODE_NORMAL.getValue(),0);
		printStr(Tools.getSysNowTime()+"\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		printStr(Tools.getSysNowTime()+"\n", EM_PRN_MODE.PRN_MODE_HEIGHT_DOUBLE.getValue(),0);
		return printStr("\n\n\n");
	}

	// 打印回车
	public int  print_enter() {
		StringBuilder resultContent = new StringBuilder();
		printStr("---以下连续打印5个回车---\n",  EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		for (int i = 0; i < 5; i++) {
			resultContent.append("\r");
		}
		printStr(resultContent.toString());
		resultContent.delete(0, resultContent.length());
		printStr("---以下连续打印20个回车---\n",  EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		for (int i = 0; i < 20; i++) {
			resultContent.append("\r");
		}
		printStr(resultContent.toString());
		resultContent.delete(0, resultContent.length());
		return printStr("---打印回车结束---\n\n\n",  EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);

	}

	// 打印奇偶回车空白行
	public int print_compress() {
		StringBuilder resultContent = new StringBuilder();
		printStr("---以下打印压缩效果---\n",  EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);

		resultContent.append("          \n");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");

		resultContent.append("          \n");
		resultContent.append("          \n");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");

		resultContent.append("          \n");
		resultContent.append("          \n");
		resultContent.append("          \n");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");

		resultContent.append("\n打印前奇偶空白行\n");
		resultContent.append("行间奇偶空格\n");
		resultContent.append("国  国   国国国国  国国 \n");
		resultContent.append(" 国 国  国国国  国国 \n");

		resultContent.append("\r");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");
		resultContent.append("打印前一个回车行\n");

		resultContent.append("\r\r\r");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");
		resultContent.append("打印前三个回车行\n");

		resultContent.append("\r\r\r\r");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");
		resultContent.append("打印前四个回车行\n");
		return printStr(resultContent.toString());
//		resultContent.delete(0, resultContent.length());
	}

	// 打印联迪单据
	public int print_landi() {
		String data = 
		"!NLFONT 9 12 2\n"+
		"!yspace 3\n"+
		"*text c ---以下打印联迪单据---\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 商户存根                          MERCHANT COPY\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 备份电池：3.02V\n"+
		"*text l WELCOME TO POS EQUIPMENT\n"+
		"*text l 国国国国国国国国国国国国国国国国国国国国国国\n"+
		"*text l 国国国国国国国国国国国国国国国国国国国国国国\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  国国国国国国国国国国国国国国国国国国国国国国\n"+
		"!NLFONT 9 12 2\n"+
		"*text l  国国国国国国国国国国国国国国国国国国国国国国\n"+
		"!NLFONT 9 12 3\n"+
		"*text l 档位打印效果：\n"+
		"*text l 档位4(#2)\n"+
		"*text l ||  ||  ||  ||  ||\n"+
		"*text l ||  ||  ||  ||  ||\n"+
		"!yspace 0\n"+//将行间距设置为打印初始化值0
		"!NLPRNOVER\n";
		return print_byScript(data);
	}
	
	// 打印新国都单据
	public int  print_xinguodu() {
		
		String data = 
		"!NLFONT 9 12 2\n"+
		"!yspace 3\n"+
		"*text c ---以下打印新国都单据---\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 商户存根                          MERCHANT COPY\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 商户名称:福州市仓山区个性服饰店\n"+
		"*text l 商户编号:309350156911410\n"+
		"*text l 终端编号:11100495   操作员号:01\n"+
		"!NLFONT 6 6\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 收 单 行:兴业银行\n"+
		"*text l 发 卡 行:兴业银行\n"+
		"*text l 卡    号:\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  622200*********7950 /S\n"+
		"!NLFONT 9 12 2\n"+
		"*text l  消费/SALE\n"+
		"!NLFONT 9 12 3\n"+
		"*text l 批次号:000003  凭证号:000934\n"+
		"*text l 参考号:046879087564\n"+
		"*text l 授权码:012345\n"+
		"*text l 日期时间:2015/06/12 20:21:53\n"+
		"*text l 金 额:\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  RMB 123.4\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 备注:\n"+
		"!NLFONT 6 1\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 持卡人签名:\n"+
		"*text l\n"+  
		"*text l\n"+  
		"!NLFONT 6 1\n"+
		"*text l 本人确认以上交易，同意将其记入本卡账户\n"+
		"*line\n"+
		"!yspace 0\n"+//将行间距设置为打印初始化值0
		"!NLPRNOVER\n";

		return print_byScript(data);
	}

	public int print_paperByPixel(int unPixel){
		int ret;
		printStr("---以下打印像素走纸---\n\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		if((ret=JniNdk.JNI_Print_FeedByPix(unPixel))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		return NDK_OK;
	}
	// 打印脚本指令
	public int print_Script() {
		printStr("---以下打印脚本---\n\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		String data ="!NLFONT 9 12 2\n"+
		"!yspace 3\n"+
		"*text c 银联POS\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 商户存根                          MERCHANT COPY\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 商户名称:中国银联\n"+
		"*text l 商户编号:123456789012345\n"+
		"*text l 终端编号:12345676  操作员号:01\n"+
		"!NLFONT 6 6\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 收 单 行:中行中国银行\n"+
		"*text l 发 卡 行:中行中国银行\n"+
		"*text l 卡    号:\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  622810*******0382 /S\n"+
		"!NLFONT 9 12 3\n"+
		"*text l 交易类型:\n"+
		"!NLFONT 9 12 2\n"+
		"*text l  消费/SALE\n"+
		"!NLFONT 9 12 3\n"+
		"*text l 批次号:000003  凭证号:000082\n"+
		"*text l 参考号:046879087564\n"+
		"*text l 授权码:123564\n"+
		"*text l 日期时间:2015/06/12 20:21:53\n"+
		"*text l 卡 组 织:CUP\n"+
		"*text l 金 额:\n"+
		"!NLFONT 6 19 2\n"+
		"*text l  RMB 85669.58\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"!NLFONT 9 12\n"+
		"*text l 备注:\n"+
		"!NLFONT 6 1\n"+
		"!NLFONT 6 6 3\n"+
		"*line\n"+
		"*text l 持卡人签名:\n"+
		"*text l\n"+  
		"*text l\n"+  
		"!NLFONT 6 1\n"+
		"*text l 本人确认以上交易，同意将其记入本卡账户\n"+
		"*text l 服务热线:\n"+
		"*line\n"+
		"!yspace 0\n"+// 将行间距修改为打印初始化值
		"!NLPRNOVER\n";

		 return print_byScript(data);
	}
	// 打印ttf脚本指令
	public int print_ttf_Script() {
	        //ttf脚本不能跟旧的脚本混合使用
//			printStr("---以下打印ttf脚本---\n\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		String data ="!NLFONT 9 12 2\n"+
				"!yspace 3\n"+
				"*text c 银联POS\n"+
				"!NLFONT 6 6 3\n"+
				"*text l ------------------------------------------------\n"+
				"*text l 商户存根                          MERCHANT COPY\n"+
				"*text l ------------------------------------------------\n"+
				"!NLFONT 9 12\n"+
				"*text l 商户名称:中国银联\n"+
				"*text l 商户编号:123456789012345\n"+
				"*text l 终端编号:12345676  操作员号:01\n"+
				"!NLFONT 6 6\n"+
				"*text l ------------------------------------------------\n"+
				"!NLFONT 9 12\n"+
				"*text l 收 单 行:中行中国银行\n"+
				"*text l 发 卡 行:中行中国银行\n"+
				"*text l 卡    号:\n"+
				"!NLFONT 6 19 2\n"+
				"*text l  622810*******0382 /S\n"+
				"!NLFONT 9 12 3\n"+
				"*text l 交易类型:\n"+
				"!NLFONT 9 12 2\n"+
				"*text l  消费/SALE\n"+
				"!NLFONT 9 12 3\n"+
				"*text l 批次号:000003  凭证号:000082\n"+
				"*text l 参考号:046879087564\n"+
				"*text l 授权码:123564\n"+
				"*text l 日期时间:2015/06/12 20:21:53\n"+
				"*text l 卡 组 织:CUP\n"+
				"*text l 金 额:\n"+
				"!NLFONT 6 19 2\n"+
				"*text l  RMB 85669.58\n"+
				"!NLFONT 6 6 3\n"+
				"*text l ------------------------------------------------\n"+
				"!NLFONT 9 12\n"+
				"*text l 备注:\n"+
				"!NLFONT 6 1\n"+
				"!NLFONT 6 6 3\n"+
				"*text l ------------------------------------------------\n"+
				"*text l 持卡人签名:\n"+
				"*text l\n"+  
				"*text l\n"+  
				"!NLFONT 6 1\n"+
				"*text l 本人确认以上交易，同意将其记入本卡账户\n"+
				"*text l 服务热线:\n"+
				"*text l - - - - - - - X - - - - - - - X - - - - - - - \n"+
				"!yspace 0\n"+// 将行间距修改为打印初始化值
				"!NLPRNOVER\n";
			 return print_byttfScript(data);
	}
	// 打印各种行间距
	public int print_row_space() {
		printStr("---以下测试行间距---\n\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		JniNdk.JNI_Print_SetForm(0, 0, 8);
		printStr("这是第一行\n这是第二行\n");
		JniNdk.JNI_Print_SetForm(0, 0, 4);
		printStr("这是第一行\n这是第二行\n");
		// 恢复默认的行间距，默认行间距为0
		return 	JniNdk.JNI_Print_SetForm(0, 0, 0);
	}

	// 测试各种字库
	public int print_stock() {
		printStr("---以下测试字库---\n", EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		// 目前只支持24点阵
		//printer.setWordStock(WordStockType.PIX_24);
		StringBuilder resultContent = new StringBuilder();
		resultContent.append("\n\n");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");
		resultContent.append("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n");
		return printStr(resultContent.toString());
//		// 默认行间距为0
//		printer.setLineSpace(0);
//		resultContent.delete(0, resultContent.length());
	}
	//kissbaby面包屋测试单据
	public int print_kissbaby() {
		printStr("---以下打印kissbaby面包屋测试单据---\n\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		String data ="!NLFONT 3 3 3\n"+
		"!yspace 9\n"+
		"*text l kissbaby面包屋（名城店）\n"+
		"!NLFONT 1 12 3\n"+
		"*line \n"+
		"*text l 商户实收：                   ￥5\n"+
		"*line \n"+
		"*text l 商户实付：                   ￥5\n"+
		"*line \n"+
		"*text l 支付方式：                支付宝\n"+
		"*line \n"+
		"!yspace 6\n"+
		"*text l 交易单号：2017020821001004920238\n" +
		"*text r 392810\n"+
		"*text l 商户名称：kissbaby面包屋（名城店\n"+
		"*text r ）\n"+
		"*text l 设备号：                83902148\n"+
		"*text l 交易时间：   2017/02/08 18:19:32\n"+
		"*text l 交易状态：              付款成功\n"+
		"!yspace 9\n"+
		"*line \n"+
		"!yspace 6\n"+
		"*text l 打印时间：      2017-02-08 18:19\n"+
		"*text l 备注：\n"+
		"*line \n"+
		"!yspace 0\n"+// 将行间距修改为打印初始化值
		"!NLPRNOVER\n";

		return print_byScript(data);
	}
	
	// 打印各种格式的图片
	public int  print_bitmap(String path,String format)
	{
		printStr("---以下打印"+format+"格式图片---\n",EM_PRN_MODE.PRN_MODE_WIDTH_DOUBLE.getValue(),0);
		Bitmap zxbmp = BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/"+path);
		return printImg(zxbmp);
	}
	
	/**
	 * 通莞金服空走纸问题
	 */
	public int printOnlineOrder() {
		StringBuilder scriptBuilder = new StringBuilder();
		scriptBuilder.append("!hz l\n !asc l\n !yspace 15\n !gray 10\n");// 设置标题字体为大号
		scriptBuilder.append("*text c 通莞金服 \n");
		scriptBuilder.append("!hz s\n !asc s\n !yspace 5\n !gray 5\n");// 设置内容字体为小号
		scriptBuilder.append("*line \n" + "");// 打印虚线
		/*
		 * if (printUser == 0) {
		 * scriptBuilder.append("*text l 商户存根                             请妥善保管\n "
		 * ); } else {
		 * scriptBuilder.append("*text l 顾客存根                             请妥善保管\n "
		 * ); }
		 */
		scriptBuilder
				.append("*text l 商户存根                             请妥善保管\n ");
		scriptBuilder.append("*line \n");// 打印虚线
		scriptBuilder.append("!hz n\n !asc n\n !yspace 10\n !gray 5\n");// 设置内容字体为中号
		scriptBuilder.append("*text l 商户名称:").append("银联商务有限公司福州分公司")
				.append("\n");
		scriptBuilder.append("*text l 商户编号:").append("899330160120021")
				.append("\n");
		scriptBuilder.append("*text l 终端编号:").append("05315830").append("\n");
		// String payTypeText =
		// AppConstants.getPayTypeText(transInfo.getChannelId());
		scriptBuilder.append("*text l 交易渠道:").append("现金消费").append("\n");
		scriptBuilder.append("*text l 交易类型:").append("消费").append("\n");
		scriptBuilder.append("*text l 交易时间:").append("2017/02/09 15:51:55").append("\n");
		/*
		 * if (isCoupon) {
		 * scriptBuilder.append("*text l 订单金额:").append(158).append("\n");
		 * scriptBuilder.append("*text l 优惠金额:").append(50).append("\n"); }
		 */
		scriptBuilder.append("*text l 支付金额:\n");
		scriptBuilder.append("!hz l\n !asc l\n !yspace 15\n !gray 10\n");// 设置标题字体为大号
		scriptBuilder.append("*text l     RMB ").append(108).append("\n");
		scriptBuilder.append("!hz n\n !asc n\n !yspace 10\n !gray 5\n");// 设置标题字体为大号
		scriptBuilder.append("*text l 交易状态:").append("付款成功").append("\n");
		scriptBuilder.append("*text l 订单号:").append("9847355443331596288").append("\n");
		scriptBuilder.append("*barcode c ").append("29847355443331596288").append("\n");
		scriptBuilder.append("!hz n\n !asc s\n !yspace 5\n !gray 5\n");// 设置标题字体为大号
		scriptBuilder.append("*text l \n *text l 微支付服务商:通莞金服\n");
		return print_byScript(scriptBuilder.toString());
	}
	 
	//打印脚本
	public int print_byScript(String strData)
	{
		int ret;
		if((ret=JniNdk.JNI_Print_Script(strData, Tools.getWordCount(strData)))!=NDK_OK)
			return ret;
		
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		return NDK_OK;
	}
	/**
	 * ttf脚本打印 add zhangxinj 
	 * data 20180705
	 * @param strData
	 * @return
	 */
	public int print_byttfScript(String strData){
		//旧脚本是GBK编码 ttf脚本打印采用的是UTF-8编码，UTF-8编码每个字符长度不固定，不能采用Tools.getWordCount()函数计算长度
		return TTFPrint.PrintScipt(strData.getBytes(), strData.getBytes().length,1);
	}
	//ttf脚本打印,ccb建行招标固件需要传0 by chending  
	public int print_byttfScript_ccb(String strData){
		//旧脚本是GBK编码 ttf脚本打印采用的是UTF-8编码，UTF-8编码每个字符长度不固定，不能采用Tools.getWordCount()函数计算长度
		return TTFPrint.PrintScipt(strData.getBytes(), strData.getBytes().length,0);
	}
	/**
	 *  应用那边打印png图片接口
	 * @param pngPath 打印的图片路径
	 * @return
	 */
	public int print_png(String pngPath)
	{
		int ret;
//		Bitmap bitMap = BitmapFactory.decodeFile(pngPath);
		JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
		// 阈值自动
//		if((ret = JniNdk.JNI_PrnPngFile(pngPath, bitMap.getWidth(),bitMap.getHeight(), (byte) 'l', 0, 256))!=NDK_OK)
//			return ret;
		if((ret=JniNdk.JNI_Print_Png(pngPath,0,1))!=NDK_OK)
			return ret;
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK)
			return ret;
		return NDK_OK;
	}
	/**
	 * 打印票据，最前面加走纸 add by zhangxj 20180427
	 */
	public int print_bill_add_feeding(){
		String data="*feedline 3\n"+
				"!NLFONT 3 13 3\n"+
				"!yspace 6\n"+
				" *text c 中国银联特约商户签购单\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text c 持卡人存单 请妥善保管\n"+
				" *line\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				" *text l 商户名称 测试一下99\n"+
				" !NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 商户编码 829581148160502\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 终端编号 60845516\n"+
				"*line\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 发卡银行 招商银行\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 卡    号 621483******6284\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 卡有效期 2405\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 凭 证 号 010204\n"+
				" !NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 交易类型 消费\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 参 考 号 231812316870\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 批 次 号 000100\n"+
				"!NLFONT 1 12 3\n"+
				"!yspace 6\n"+
				"*text l 日期时间 20180423181240\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l 金    额 RMB 0.01\n"+
				"*line\n"+
				" !NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l 备    注\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l AQRC:\n"+
				" !NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l AID:\n"+
				" !NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l CSN: CUM:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l TSI: TUR:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l ATC: UNPR NO:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l AIP: TermCap:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l IAD:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l APPLAB:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l APPNAME:\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				" *text l 打印时间 2018-04-23 18:12:41\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l 持卡人签名 \n"+
				"*feedline 3\n"+
				"!NLFONT 6 1 3\n"+
				"!yspace 6\n"+
				"*text l 本人确认以上交易，同意将其计入本卡账户\n "+
				"*feedline 7\n";
		 return print_byScript(data);
	}
}
