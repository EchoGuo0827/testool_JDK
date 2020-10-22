package com.example.highplattest.printer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.newland.SettingsManager;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DBHelper;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
/************************************************************************
 * 
 * module 			: 其他
 * file name 		: Printer1.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20171101
 * directory 		: 
 * description 		: 模拟蓝牙打印
 * related document :
 * history 		 	: 变更记录													变更时间			变更人员
 *			  	      测试人员要求添加模拟蓝牙地址比较00:01:02:03:0A:0B		   			20200513	 	 陈丁
 *				修改X5蓝牙打印,打印宽度域指令。区分大纸和小纸。修复部分条形码无法打印问题                             20200525       	  陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer1 extends UnitFragment
{
	
	private final String TESTITEM = "模拟蓝牙打印";
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private BluetoothAdapter bluetoothAdapter;
	private Gui gui = new Gui(myactivity, handler);
	
	private PrintUtil printUtil=new PrintUtil(myactivity, handler,true);
	private Config config;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private String fileName=Printer1.class.getSimpleName();
	int flag_abnormal = 0;//异常测试标志位
	
	Map<String, byte[]> command=new HashMap<String, byte[]>();
	/**
	 * 所有打印指令
	 */
	public void commandMap(){
		command.put("RESET", new byte[]{0x1b, 0x40});
		command.put("ALIGN_LEFT", new byte[]{0x1b, 0x61, 48}); //左对齐  n=48或0
		command.put("ALIGN_LEFT_SMALL_INT", new byte[]{0x1b, 0x61, 0});
		command.put("ALIGN_CENTER", new byte[]{0x1b, 0x61, 49});//中间对齐  n=49或1
		command.put("ALIGN_CENTER_SMALL_INT", new byte[]{0x1b, 0x61, 1});
		command.put("ALIGN_RIGHT", new byte[]{0x1b, 0x61, 50});// 右对齐 n=50或2
		command.put("ALIGN_RIGHT_SMALL_INT", new byte[]{0x1b, 0x61, 2});
		command.put("STANDARD_FONT", new byte[] { 0x1b, 0x4d, 48 });// 标准ASCII字体 n=48或0
		command.put("STANDARD_FONT_SMALL_INT", new byte[] { 0x1b, 0x4d, 0 });
		command.put("COMPRESSED_FONT", new byte[] { 0x1b, 0x4d, 49 });// 压缩ASCII字体 n=49或1
		command.put("COMPRESSED_FONT_SMALL_INT", new byte[] { 0x1b, 0x4d, 1 });
		command.put("FONT_DOUBLE", new byte[] { 0x1d, 0x21, 17 });//字符大小 此命令对ASCII和汉字都有效 2倍
		command.put("FONT_DOUBLE_WIDTH", new byte[] { 0x1d, 0x21, 16 });//字符大小 此命令对ASCII和汉字都有效 宽2倍
		command.put("FONT_DOUBLE_HIGHT", new byte[] { 0x1d, 0x21, 1 });	// 字符大小 此命令对ASCII和汉字都有效 高2倍
		command.put("FONT_DOUBLE_RANDOM", new byte[] { 0x1d, 0x21, 67 });// 字符大小 此命令对ASCII和汉字都有效 2倍 传入随意数据，按2倍处理
		// public byte[] FONT_FOUR = { 0x1d, 0x21, 0x33};//字符大小 此命令对ASCII和汉字都有效 4倍
		command.put("FONT_NORMAL", new byte[] { 0x1d, 0x21, 0 });// 字符大小 此命令对ASCII和汉字都有效
		command.put("RIGHT_SPACE", new byte[] { 0x1b, 0x20, 20 });// 设置字符右间距为20
		command.put("RIGHT_SPACE_10", new byte[] { 0x1b, 0x20, 10 });// 设置字符右间距为10，10的十六进制为0A，看是否会被判断为结束符
		command.put("RIGHT_SPACE_DEFALUT", new byte[] { 0x1b, 0x20, 0 });// 设置字符右间距为默认0
		command.put("CHINESE_SPACE", new byte[] { 0x1c, 0x53, 10, 50 });// 设置汉字字符左右边距
		command.put("CHINESE_SPACE_10", new byte[] { 0x1c, 0x53, 10, 10 });// 设置汉字字符左右边距
		command.put("CHINESE_SPACE_DEFALUT", new byte[] { 0x1c, 0x53, 0, 0 });// 设置汉字字符左右边距为默认
		command.put("CHINESE_DOUBLE_WIDTH", new byte[] { 0x1c, 0x21, 0x04 });// 汉字被宽
		command.put("CHINESE_DOUBLE_HEIGHT", new byte[] { 0x1c, 0x21, 0x08 });// 汉字倍高
		command.put("CHINESE_UNDERLINE", new byte[] { 0x1c, 0x21, (byte) 0x80 });// 汉字下划线
		command.put("CHINESE_FONT_MIX", new byte[] { 0x1c, 0x21, (byte) 140 });// 选择打印模式 倍高被宽 有下划线 汉字
		command.put("CHINESE_FONT_MIX_CANCEL", new byte[] { 0x1c, 0x21, 0 });// 选择打印模式 倍高被宽 有下划线 汉字
		command.put("BOLD", new byte[] { 0x1b, 0x45, 0x01 });// 选择加粗模式
		command.put("BOLD_CANCEL", new byte[] { 0x1b, 0x45, 0x00 });// 取消加粗模式
		command.put("DOUBLE_PRINTER", new byte[] { 0x1b, 0x47, 0x01 });// 选择双重打印模式
		command.put("DOUBLE_PRINTER_CANCEL", new byte[] { 0x1b, 0x47, 0x00 });// 取消双重打印模式
	    /**
	     * 英数部分
	     */
		command.put("ENG_font_COMPRESSED", new byte[] { 0x1b, 0x21, 0x01 });// 压缩字体
		command.put("ENG_UNDERLINE", new byte[] { 0x1b, 0x21, (byte) 0x80 });// 有下划线
		command.put("ENG_DOUBLE_WIDTH", new byte[] { 0x1b, 0x21, 0x20 });// 宽加倍
		command.put("END_DOUBLE_HEIGHT", new byte[] { 0x1b, 0x21, 0x10 });// 高加倍
		command.put("END_BLOD", new byte[] { 0x1b, 0x21, 0x08 });// 加粗
		command.put("FONT_MIX", new byte[] { 0x1b, 0x21, (byte) 185 });// 选择打印模式 压缩ASCII 加粗 倍高被宽 有下划线 英数 加粗对中文有效，其他只对英数有效
		command.put("FONT_MIX2", new byte[] { 0x1b, 0x21, (byte) 184 });// 选择打印模式 标准ASCII 加粗 倍高被宽 有下划线 英数 加粗对中文有效，其他只对英数有效
		command.put("FONT_MIX_CANCEL", new byte[] { 0x1b, 0x21, 0 });// 取消字符模式 标准ASCII
		command.put("FONT_MIX_CANCEL_2", new byte[] { 0x1b, 0x21, 1 });// 取消字符模式 压缩ASCII
		command.put("CHINESE_DOUBLE_HEIGHT_WIDTH", new byte[] { 0x1c, 0x57,0x01 });// 汉字倍高被宽
		command.put("CHINESE_DOUBLE_HEIGHT_WIDTH_CANCEL", new byte[] { 0x1c,0x57, 0x00 });// 取消汉字倍高被宽
		command.put("UNDERLINE", new byte[] { 0x1c, 0x2d, 49 });// 选择汉字下划线模式
		command.put("UNDERLINE_SMALL_INT", new byte[] { 0x1c, 0x2d, 1 });
		command.put("UNDERLINE_CANCEL", new byte[] { 0x1c, 0x2d, 48 });	// 取消汉字下划线模式
		command.put("UNDERLINE_CANCEL_SMALL_INT", new byte[] { 0x1c, 0x2d, 0 });
		command.put("UNDERLINE2", new byte[] { 0x1b, 0x2d, 49 });// 下划线模式 n=49或1
		command.put("UNDERLINE2_SMALL_INT", new byte[] { 0x1b, 0x2d, 1 });
		command.put("UNDERLINE2_CANCEL", new byte[] { 0x1b, 0x2d, 48 });// 取消下划线模式 n=48或0
		command.put("UNDERLINE2_CANCEL_SMALL_INT", new byte[] { 0x1b, 0x2d, 0 });
		command.put("ABSOLUTE_POSITION",new byte[] { 0x1b, 0x24, (byte) 200, 0 }); //设置绝对打印位置 当前位置设置到距离行首200个单位
		command.put("ABSOLUTE_POSITION_10", new byte[] { 0x1b, 0x24, (byte) 10,0 });// 设置绝对打印位置 当前位置设置到距离行首10个单位
		command.put("RELATION_POSITION", new byte[] { 0x1b, 0x5c, 60, 0 });// 设置相对打印位置 距离当前位置60
		command.put("RELATION_POSITION_10", new byte[] { 0x1b, 0x5c, 10, 0 });// 设置相对打印位置 距离当前位置10
		command.put("LEFT_SPACE", new byte[] { 0x1d, 0x4c, 20, 0 });// 设置行首左边距为20
		command.put("LEFT_SPACE_10", new byte[] { 0x1d, 0x4c, 10, 0 });// 设置行首左边距为10
		command.put("LEFT_SPACE_DEFALUT", new byte[] { 0x1d, 0x4c, 0, 0 });// 设置左边距为默认0
		command.put("NORMAL", new byte[] { 0x1d, 0x21, 0x00 }); //字体不放大
		command.put("LINE_SPACING_DEFAULT", new byte[] { 0x1b, 0x32 });// 设置默认行间距
		command.put("LINE_SPACING", new byte[] { 0x1b, 0x33, 80 }); // 设置行间距80像素
		command.put("LINE_SPACING_10", new byte[] { 0x1b, 0x33, 10 }); // 设置行间距10像素
		command.put("SPACE1", new byte[] { 0x1b, 0x44, 1, 0x00 });// 设置横向跳格位置
		command.put("SPACE25", new byte[] { 0x1b, 0x44, 25, 0x00 });// 设置横向跳格位置
		command.put("SPACE33", new byte[] { 0x1b, 0x44, 33, 0x00 });// 设置横向跳格位置
		command.put("SPACE40", new byte[] { 0x1b, 0x44, 40, 0x00 });// 设置横向跳格位置
		command.put("CANCEL_SPACE", new byte[] { 0x1b, 0x44, 0x00 });
		command.put("HT", new byte[] { 0x09 });
		command.put("PAPER_THROW_S", new byte[] { 0x1b, 0x4A, 100 });// 走纸100
		command.put("PAPER_THROW_S_10", new byte[] { 0x1b, 0x4A, 10 });// 走纸10
		command.put("PAPER_THROW_L", new byte[] { 0x1b, 0x64, 10 });// 打印并向前走纸
		command.put("ROTATE", new byte[] { 0x1b, 0x56, 49 });	//顺时针旋转90° n传49或者1
		command.put("ROTATE_SMALL_INT", new byte[] { 0x1b, 0x56, 1 });
		command.put("ROTATE_CANCEL", new byte[] { 0x1b, 0x56, 48 });// 取消顺时针旋转90° n传48或者0
		command.put("ROTATE_CANCEL_SMALL_INT", new byte[] { 0x1b, 0x56, 0 });
		command.put("UPDOWN", new byte[] { 0x1b, 0x7b, 0x01 });// 倒置打印
		command.put("UPDOWN_CANCEL", new byte[] { 0x1b, 0x7b, 0x00 });// 取消倒置打印
		command.put("BLACK", new byte[] { 0x1d, 0x42, 0x01 });// 黑白反显
		command.put("BLACK_CANCEL", new byte[] { 0x1d, 0x42, 0x00 });// 取消黑白反显
		command.put("UPCA", new byte[] { 0x1d, 0x6b, 65, 12, 48, 49, 50, 51,52, 53, 54, 55, 56, 57, 48, 49 });//UPC-A 
		command.put("UPCA_SMALL_INT", new byte[] { 0x1d, 0x6b, 0, 48, 49, 50,51, 52, 53, 54, 55, 56, 57, 48, 49, 0x00 });// UPC-E 012345
		command.put("UPCE", new byte[] { 0x1d, 0x6b, 66, 12, 48, 49, 50, 51,52, 53, 52, 53, 49, 49, 50, 50 });
		command.put("UPCE_SMALL_INT", new byte[] { 0x1d, 0x6b, 1, 48, 49, 50,51, 52, 53, 52, 53, 49, 49, 50, 50, 0x00 });
		command.put("EAN13", new byte[] { 0x1d, 0x6b, 67, 13, 48, 49, 50, 51,52, 53, 54, 55, 56, 57, 57, 57, 57 });// EAN13
		command.put("EAN13_SMALL_INT", new byte[] { 0x1d, 0x6b, 2, 48, 49, 50,51, 52, 53, 54, 55, 56, 57, 57, 57, 57, 0x00 });
		command.put("EAN8", new byte[] { 0x1d, 0x6b, 68, 8, 48, 49, 50, 51, 52,53, 54, 55 });// EAN8 01234567
		command.put("EAN8_SMALL_INT", new byte[] { 0x1d, 0x6b, 3, 48, 49, 50,51, 52, 53, 54, 55, 0x00 });
		command.put("CODE39", new byte[] { 0x1d, 0x6b, 69, 12, 42, 48, 49, 50,51, 52, 53, 54, 55, 56, 57, 42 });//CODE39 打印不出来
		command.put("CODE39_SMALL_INT", new byte[] { 0x1d, 0x6b, 4, 42, 48, 49,50, 51, 52, 53, 54, 55, 56, 57, 42, 0x00 });
		command.put("ITF", new byte[] { 0x1d, 0x6b, 70, 12, 48, 49, 50, 51, 52,51, 52, 53, 54, 55, 56, 57 });// ITF
		command.put("ITF_SMALL_INT", new byte[] { 0x1d, 0x6b, 5, 48, 49, 50,51, 52, 51, 52, 53, 54, 55, 56, 57, 0x00 });
		command.put("CODEBAR", new byte[] { 0x1d, 0x6b, 71, 7, 48, 49, 50, 51,52, 51, 52 });// CODEBAR
		command.put("CODEBAR_SMALL_INT", new byte[] { 0x1d, 0x6b, 6, 48, 49,50, 51, 52, 51, 52, 0x00 });
		command.put("CODE93", new byte[] { 0x1d, 0x6b, 72, 7, 48, 49, 50, 51,52, 51, 52 });// CODE93
		command.put("CODE128", new byte[] { 0x1d, 0x6b, 73, 10, 48, 49, 50, 51,52, 53, 54, 55, 56, 57 });// CODE128 0-9
		command.put("HIR_STANDARD_FONT", new byte[] { 0x1d, 0x66, 48 });// 选择HIR字体 标准ASCII字体 n=0/48
		command.put("HIR_STANDARD_FONT_SMALL_INT", new byte[] { 0x1d, 0x66, 0 });
		command.put("HIR_COMPRESSED_FONT", new byte[] { 0x1d, 0x66, 49 });// 选择HIR字体 压缩ASCII字体 n=1/49
		command.put("HIR_COMPRESSED_FONT_SMALL_INT",new byte[] { 0x1d, 0x66, 1 });
		command.put("HIR_UP", new byte[] { 0x1d, 0x48, 49 });// 选择HIR字符的打印位置1或49在条码上方 2在条码下方 0不打印 3上下方都打印
		command.put("HIR_UP_SMALL_INT", new byte[] { 0x1d, 0x48, 1 });
		command.put("HIR_BELOW", new byte[] { 0x1d, 0x48, 50 });// 选择HIR字符的打印位置2或50在条码下方
		command.put("HIR_BELOW_SMALL_INT", new byte[] { 0x1d, 0x48, 2 });
		command.put("HIR_NULL", new byte[] { 0x1d, 0x48, 48 });	// 选择HIR字符的打印位置0或48 上下都不打印
		command.put("HIR_NULL_SMALL_INT", new byte[] { 0x1d, 0x48, 0 });
		command.put("HIR_BELOW_UP", new byte[] { 0x1d, 0x48, 51 });//选择HIR字符的打印位置0或51 上下都不打印
		command.put("HIR_BELOW_UP_SMALL_INT", new byte[] { 0x1d, 0x48, 3 });
		command.put("BARCODEHEIGHT", new byte[] { 0x1d, 0x68, (byte) 162 });// 设置条码高度
		command.put("BARCODEHEIGHT_SISTY", new byte[] { 0x1d, 0x68, (byte) 60 });
		command.put("BARCODEHEIGHT_MAX", new byte[] { 0x1d, 0x68, (byte) 255 });
		command.put("BARCODEWIDTH2", new byte[] { 0x1d, 0x77, 2 });// 设置条码宽度
		command.put("BARCODEWIDTH3", new byte[] { 0x1d, 0x77, 3 });// 设置条码宽度
		command.put("BARCODEWIDTH4", new byte[] { 0x1d, 0x77, 4 });//设置条码宽度
		command.put("BARCODEWIDTH5", new byte[] { 0x1d, 0x77, 5 });// 设置条码宽度
		command.put("BARCODEWIDTH6", new byte[] { 0x1d, 0x77, 6 });	// 设置条码宽度
		command.put("WRAP", new byte[] { 0x0A });// 换行
		command.put("HOR_VER_UNIT", new byte[] { 0x1d, 0x50, (byte) 255,(byte) 255 });// 设置横向纵向移动位置
		command.put("HOR_VER_UNIT_10", new byte[] { 0x1d, 0x50, (byte) 10,(byte) 10 });
		command.put("HOR_VER_UNIT_Defult", new byte[] { 0x1d, 0x50, (byte) 200,(byte) 200 });
		command.put("PRINT_AREA_WIDTH",new byte[] { 0x1d, 0x57, (byte) 180, 0 });// 设置打印区域宽度180
		command.put("PRINT_AREA_WIDTH_10", new byte[] { 0x1d, 0x57, (byte) 10,0 });// 设置打印区域宽度10
		command.put("PRINT_AREA_WIDTH_X5", new byte[] { 0x1d, 0x57, (byte) 128,1}); //设置打印宽度为小纸
		command.put("PRINT_AREA_WIDTH_X5_DEFAULT", new byte[] { 0x1d, 0x57, 76,2}); //设置x5打印适配58mm打印纸    设置打印宽度为大纸
		command.put("PRINT_AREA_WIDTH_DEFAULT",new byte[] { 0x1d, 0x57, 76, 2 });// 设置打印区域宽度默认
		command.put("SETQRCODESIZE1", new byte[] { 0x1d, 0x28, 0x6b, 0x03,0x00, 0x31, 0x43, 0x01 });// 二维码指令 设置QRCODE模块大小最小1
		command.put("SETQRCODESIZE16", new byte[] {0x1d,0x28,0x6b,0x03,0x00,0x31,0x43,16});//二维码指令 设置QRCODE模块大小最大支16，
		command.put("SETQRCODESIZE3", new byte[] { 0x1d, 0x28, 0x6b, 0x03,0x00, 0x31, 0x43, 0x03 });// 二维码指令 设置QRCODE模块大小默认3
		command.put("PUTQRCODETOMEMORYAREA", new byte[] { 0x1d, 0x28, 0x6b,0x0b, 0x00, 0x31, 0x50, 0x30, 0x47, 0x70, 0x72, 0x69, 0x6e,0x74, 0x65, 0x72 });// 将QRCODE存入打印机
		command.put("PRINTERQRCODE", new byte[] { 0x1d, 0x28, 0x6b, 0x03, 0x00,0x31, 0x51, 0x30 });// 打印QRCODE
		command.put("PUTQRCODETOMEMORYAREA2", new byte[] { 0x1d, 0x28, 0x6b,
				0x1c, 0x00, 0x31, 0x50, 0x30, 0x41, 0x42, 0x43, 0x31, 0x32,
				0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x44, 0x45, 0x46,
				0x47, 0x48, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68 });// ABC123456789DEFGH abcdefgh
		
		//实时状态传送
		command.put("PRINTERSTATE1", new byte[] { 0x10, 0x04, 1 });// n=1传送打印机状态
		command.put("PRINTERSTATE2", new byte[] { 0x10, 0x04, 2 });// n=2传送脱机状态
		command.put("PRINTERSTATE3", new byte[] { 0x10, 0x04, 3 });// n=3传送错误状态
		command.put("PRINTERSTATE4", new byte[] { 0x10, 0x04, 4 });// n=4传送纸传感器状态

	}
	public void printer1()
	{
		String funcName="printer1";
		/* private & local definition */
		commandMap();
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.unitPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		bluetoothManager = BluetoothManager.getInstance(myactivity);//单例模式
		bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		config = new Config(myactivity, handler);
		config.btConfig(pairList, unPairList, bluetoothManager);
		dataService = new BluetoothService(getBtAddr());
		gui.cls_printf("正在连接蓝牙打印机，请稍等...".getBytes());
	
		bluetoothAdapter.enable();
		SystemClock.sleep(2000);
		// 建立连接
		if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:BT连接建立失败", Tools.getLineInfo());
			return;
		}
		//测试人员需求 无论哪种平台蓝牙打印地址都是一致的，需要增加获取到的蓝牙地址和00:01:02:03:0A:0B一致
		if (!getBtAddr().equals("00:01:02:03:0A:0B")) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:蓝牙打印地址错误，获取到的地址为%s", Tools.getLineInfo(),getBtAddr());
			return;
		}
		gui.cls_show_msg1(5, "连接到的蓝牙地址为："+getBtAddr());
		
	
		try {
			while(true)
			{
				int nkeyIn = gui.cls_show_msg("测试项\n0.蓝牙打印指令打印\n1.蓝牙打印票据打印\n2.打印各国货币符号\n3.打印机状态测试\n4.保存打印数据到数据库(阿里固件专用功能)\n5.异常测试\n");
				switch (nkeyIn) 
				{
				case '0':
					instructTest();
					break;
				case '1':
					printBill();
					break;
				case '2':
					printCurrencySymbol();
					break;
				case '3':
					printState();
					break;
				case '4':
					if(GlobalVariable.gCustomerID==CUSTOMER_ID.KouBei)// 口碑固件支持
					{
						setDataToDB();
					}
					else
					{
						gui.cls_show_msg("非口碑固件，不支持该测试项目，任意键退出");
					}
					break;
				case '5':
					abnormalTest();
					break;
				case ESC:
					unitEnd();
					return;
				}
			}
			
		
		} catch (Exception e) {
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "抛出%s异常", e.getMessage());
		}

	}
	
	/**
	 * X5打印数据数据库测试 add by 陈丁 20190814
	 */
	private void X5dbtest() 
	{
		gui.cls_show_msg1(2, "正在测试X5保存蓝牙打印数据到数据库...");
		SettingsManager settingsManager =  (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		String dbFileName="/data/share/EpayParameter/print_records.db";
		File dbFile=new File(dbFileName);
		boolean ret = false;
		//case1：打开数据库开关 进行蓝牙打印 会生成数据库db文件
		if(dbFile.exists()){
			dbFile.delete();
		}
		if((ret=settingsManager.setBluetoothCmdCollectEnable(true))!=true){
			gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr,"line %d:开启蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		commandText(command.get("PRINT_AREA_WIDTH_X5"));
		commandText(command.get("RIGHT_SPACE"));
		printText("这行文字设置字符右间距为20个单位：hello\n"); 
		if(!dbFile.exists()){
			gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr,"line %d:开启蓝牙打印数据收集后未生成数据库db文件", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case2：删除数据库文件，关闭数据库开关 进行蓝牙打印 不会再生成数据
		if (dbFile.exists()) 
		{
			dbFile.delete();
		}
		if ((ret = settingsManager.setBluetoothCmdCollectEnable(false)) != true) 
		{
			gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr,"line %d:关闭蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		commandText(command.get("RIGHT_SPACE_DEFALUT"));
		printText("这行文字设置字符右间距为默认：hello\n");
		printText("-------------------------------\n");
		if (dbFile.exists()) 
		{
			gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr,"line %d:关闭蓝牙打印数据收集后生成了数据库db文件", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// case3: 打印文字，图片，二维码，应能正确存入数据库
		List<byte[]> list = new ArrayList<byte[]>();
		list.add(command.get("ALIGN_CENTER"));
		list.add(getStringByte("本行文字居中打印\n"));
		list.add(getStringByte("-------------------------------\n"));
		list.add(command.get("ALIGN_LEFT_SMALL_INT"));
		list.add(getStringByte("本行文字居左打印\n"));
		list.add(getStringByte("-------------------------------\n"));
		list.add(getStringByte("以下打印EAN8条码\n"));
		list.add(command.get("EAN8"));
		list.add(getStringByte("\n"));
		list.add(getStringByte("-------------------------------\n"));
		list.add(getStringByte("以下打印图片\n"));
		Bitmap bit = BitmapFactory.decodeFile(GlobalVariable.sdPath+"picture/font.png");
		byte[] startByteNomal = { 0x1d, 0x76, 0x30, 0 };
		byte[] imageByte = getBitMapBytes(bit, startByteNomal);
		list.add(imageByte);
		list.add(getStringByte("\n"));
		// 打开收集数据开关
		if ((ret = settingsManager.setBluetoothCmdCollectEnable(true)) != true) 
		{
			gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr,"line %d:开启蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 进行蓝牙打印
		for (byte[] listItem : list) {
			commandText(listItem);
		}
		// 进行数据库操作
		DBHelper dbHelper = new DBHelper(myactivity,"/data/share/EpayParameter/print_records.db", null, 1);
		dbHelper.setWriteAheadLoggingEnabled(false);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM BluetoothPrinter", null);
		int countNum = cursor.getCount();
		if (countNum != list.size()) {
			gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr,"line %d:读取数据库内容条数有误(预期：%d，实际：%d)", Tools.getLineInfo(),list.size(), countNum);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (dbFile.exists()) 
		{
			dbFile.delete();
		}
		gui.cls_show_msg1_record(fileName, "X5dbtest", gKeepTimeErr, "测试通过");
	}
	/**
	 * 蓝牙指令打印
	 */
	public void instructTest(){
	
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("测试项\n0.其他指令\n1.1B2A打印图片指令\n2.一维码二维码打印指令\n3.光栅位图打印指令\n4.蓝牙打印数据库测试");
			switch (nkeyIn) 
			{
			case '0':
				OtherinstructTest();
				break;
			case '1':
				test1B2APrintBitmap();
				break;
			case '2':
				OneOrTwodimensionalcodeInstructTest();
				break;
			case '3':
				GratingBitmap();
				break;
			case '4':// 数据库保存功能只在特定固件上支持
				if(gui.cls_show_msg("请确保当前固件支持数据导入收集功能再测试，已导入[确认]，未导入[其他]")==ENTER)
				{
					X5dbtest();
				}
				break;
			case ESC:
				return;
			}
		}
	}
	public void OtherinstructTest(){
		String funcName="OtherinstructTest";
		//打印机复位
		commandText(command.get("RESET"));
		if (GlobalVariable.currentPlatform==Model_Type.X5) {
			Log.d("cd", "X5适配打印");
			if (gui.cls_show_msg("确认使用58mm打印纸？【是】点击确认【否】点击其他")==ENTER) {
				commandText(command.get("PRINT_AREA_WIDTH_X5_DEFAULT"));
			}else {
				commandText(command.get("PRINT_AREA_WIDTH_X5"));
			}
			
		}
		//居左
		commandText(command.get("ALIGN_LEFT"));
	
		printText("(1b 61 48)这行文字居左打印\non the left\n");
		printText("-------------------------------\n");
		//居中
		commandText(command.get("ALIGN_CENTER"));
		printText("(1b 61 49)这行文字居中打印\nin the middle\n");
		printText("-------------------------------\n");
		//居右
		commandText(command.get("ALIGN_RIGHT"));
		printText("(1b 61 50)这行文字居右打印\non the right\n");
		commandText(command.get("ALIGN_LEFT"));
		printText("-------------------------------\n");
		//居左
		commandText(command.get("ALIGN_LEFT_SMALL_INT"));
		printText("(1b 61 0)这行文字居左打印\non the left\n");
		printText("-------------------------------\n");
		//居中
		commandText(command.get("ALIGN_CENTER_SMALL_INT"));
		printText("(1b 61 1)这行文字居中打印\nin the middle\n");
		printText("-------------------------------\n");
		//居右
		commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
		printText("(1b 61 2)这行文字居右打印\non the right\n");
		commandText(command.get("ALIGN_LEFT_SMALL_INT"));
		printText("-------------------------------\n");
		//压缩ASCII字体
		commandText(command.get("COMPRESSED_FONT"));
		printText("(1b 4d 49)This text printer compressed font（英文为压缩ASCII字体9*17）\n");
		//标准ASCII字体
		commandText(command.get("STANDARD_FONT"));
		printText("(1b 4d 48) This text printer standard font size（英文为标准ASCII字体12*24）\n");
		//压缩ASCII字体
		commandText(command.get("COMPRESSED_FONT_SMALL_INT"));
		printText("(1b 4d 1)This text printer compressed font（英文为压缩ASCII字体9*17）\n");
		//标准ASCII字体
		commandText(command.get("STANDARD_FONT_SMALL_INT"));
		printText("(1b 4d 0) This text printer standard font size（英文为标准ASCII字体12*24）\n");
		printText("-------------------------------\n");
		//中文倍宽倍高
		commandText(command.get("CHINESE_DOUBLE_HEIGHT_WIDTH"));
		printText("(1c 57)这段文字中文设置倍高倍宽：this text Chinese set double height double width\n");
		printText("-------------------------------\n");
		//取消中文倍宽倍高
		commandText(command.get("CHINESE_DOUBLE_HEIGHT_WIDTH_CANCEL"));
		printText("(1c 57)这段文字取消中文倍高倍宽：this text cancel Chinese double height double width\n");
		printText("-------------------------------\n");
		//文字放大2倍
		commandText(command.get("FONT_DOUBLE"));
		printText("(1d 21)文字宽高放大2倍（中英文），HELLO\n");
		commandText(command.get("FONT_DOUBLE_WIDTH"));
		printText("(1d 21)文字宽度放大2倍（中英文），HELLO\n");
		commandText(command.get("FONT_DOUBLE_HIGHT"));
		printText("(1d 21)文字高度放大2倍（中英文），HELLO\n");
		commandText(command.get("FONT_DOUBLE_RANDOM"));
		printText("(1d 21)文字宽高放大大于2倍，与2倍效果一致（中英文），HELLO\n");
		//文字正常倍数
		commandText(command.get("FONT_NORMAL"));
		printText("(1d 21)文字正常倍数（中英文），HELLO\n");
		printText("-------------------------------\n");
		
		//设置字符右间距
		commandText(command.get("RIGHT_SPACE"));
		printText("(1b 20)这行文字设置字符右间距为20个单位：hello\n");
		//设置字符右间距为10，10的十六进制为0A，看是否会被判断为结束符，去掉最后的结束符
		commandText(command.get("RIGHT_SPACE_10"));
		printText("(1b 20)这行文字设置字符右间距为10个单位：hello\n");
		//设置字符右间距0
		commandText(command.get("RIGHT_SPACE_DEFALUT"));
		printText("(1b 20)这行文字设置字符右间距为默认：hello\n");
		printText("-------------------------------\n");
		//设置汉字左右边距
		commandText(command.get("CHINESE_SPACE"));
		printText("(1c 53)这段文字设置汉字左间距为10，右间距50\n");
		//设置汉字左右边距为10，10的十六进制为0A，看是否会被判断为结束符，去掉最后的结束符
		commandText(command.get("CHINESE_SPACE_10"));
		printText("(1c 53)这段文字设置汉字左间距为10，右间距10");
		//设置汉字左右边距为默认
		commandText(command.get("CHINESE_SPACE_DEFALUT"));
		printText("(1c 53)这段文字设置汉字左右边距为默认0\n");
		printText("-------------------------------\n");
		
		
		// 选择加粗模式
		commandText(command.get("BOLD"));
		printText("(1b 45)这行文字加粗\n");
		// 取消加粗模式
		commandText(command.get("BOLD_CANCEL"));
		printText("(1b 45)这行文字取消加粗\n");
		printText("-------------------------------\n");
		// 选择双重打印模式
		commandText(command.get("DOUBLE_PRINTER"));
		printText("(1b 47)这行文字双重打印模式\n");
		// 取消双重打印模式
		commandText(command.get("DOUBLE_PRINTER_CANCEL"));
		printText("(1b 47)这行文字取消双重打印模式\n");
		printText("-------------------------------\n");
		//下划线模式 汉字
		commandText(command.get("UNDERLINE"));
		printText("(1c 2d 49)这行汉字字有下划线\n");
		//取消下划线模式 汉字
		commandText(command.get("UNDERLINE_CANCEL"));
		printText("(1c 2d 48)这行汉字取消下划线\n");	
		printText("-------------------------------\n");
		//下划线模式 汉字
		commandText(command.get("UNDERLINE_SMALL_INT"));
		printText("(1c 2d 1)这行汉字字有下划线\n");
		//取消下划线模式 汉字
		commandText(command.get("UNDERLINE_CANCEL_SMALL_INT"));
		printText("(1c 2d 0)这行汉字取消下划线\n");	
		printText("-------------------------------\n");
		//下划线模式 
		commandText(command.get("UNDERLINE2"));
		printText("(1b 2d 49)这行英数有下划线，hello\n");
		//取消下划线模式 
		commandText(command.get("UNDERLINE2_CANCEL"));
		printText("(1b 2d 48)这行英数取消下划线，hello\n");
		printText("-------------------------------\n");
		//下划线模式 
		commandText(command.get("UNDERLINE2_SMALL_INT"));
		printText("(1b 2d 1)这行英数有下划线，hello\n");
		//取消下划线模式 
		commandText(command.get("UNDERLINE2_CANCEL_SMALL_INT"));
		printText("(1b 2d 0)这行英数取消下划线，hello\n");
		printText("-------------------------------\n");
//		1b 21指令与GP58中文编程手册说明有出入，与开发沟通改成与外置打印机一致效果：除了字体设置值针对英数部分，粗图下划线倍高倍宽同时对英数和汉子有效果
		printText("以下测试英数字符模式 (1b 21)\n");
		commandText(command.get("ENG_font_COMPRESSED"));
		printText("英数压缩ASCII字体9*17：compressedFont size 9*17\n");
		
		commandText(command.get("FONT_MIX_CANCEL"));
		printText("英数标准ASCII字体12*24：standardFont size 12*24\n");
		
		commandText(command.get("ENG_UNDERLINE"));
		printText("本行有下划线：underline 15 24 \n");
		
		commandText(command.get("FONT_MIX_CANCEL"));
		printText("本行取消下划线：underline cancel 15 24\n");
		
		commandText(command.get("ENG_DOUBLE_WIDTH"));
		printText("本行文字倍宽：double width 15 24\n");
		commandText(command.get("END_DOUBLE_HEIGHT"));
		printText("本行文字倍高：double height 15 24\n");
		commandText(command.get("END_BLOD"));
		printText("本行文字粗体：blod 15 24\n");
		commandText(command.get("FONT_MIX_CANCEL"));
		printText("本行文字取消粗体：blod\n");
	
		commandText(command.get("FONT_MIX"));
		printText("这段文字倍高倍宽，粗体，有下划线，英数部分为压缩ASCII字体9*17：Blod、doubleHight、doubleWidth、compressedFont and underLine\n");
		commandText(command.get("FONT_MIX2"));
		printText("这段文字倍高倍宽，粗体，有下划线，英数部分为标准ASCII字体12*24：Blod、doubleHight、doubleWidth、compressedFont and underLine\n");
		//去除打印模式
		commandText(command.get("FONT_MIX_CANCEL_2"));
		commandText(command.get("FONT_MIX_CANCEL"));
	
		printText("这段文字英数部分去除Blod、doubleHight、doubleWidth、compressedFont and underLine\n");
		printText("-------------------------------\n");
		printText("以下测试汉字模式 (1c 21)\n");
		commandText(command.get("CHINESE_UNDERLINE"));
		printText("汉字有下划线：underline\n");
		commandText(command.get("CHINESE_FONT_MIX_CANCEL"));
		printText("汉字取消下划线：underline cancel\n");
		commandText(command.get("CHINESE_DOUBLE_WIDTH"));
		printText("汉字倍宽：double width\n");
		commandText(command.get("CHINESE_DOUBLE_HEIGHT"));
		printText("汉字倍高：double height\n");
		//设置打印模式 加粗 压缩 有下划线 倍高被宽 除粗体外其他的只对英数字符有效
		commandText(command.get("CHINESE_FONT_MIX"));
		printText("这段文字为汉字倍高倍宽，有下划线\n");
		//去除打印模式
		commandText(command.get("CHINESE_FONT_MIX_CANCEL"));
		printText("这段文字为汉字去除倍宽倍高下划线\n");
		printText("-------------------------------\n");
		
		//设置绝对打印位置10个单位，10的十六进制为0A，看是否会被判断为结束符，去掉最后的结束符
		commandText(command.get("ABSOLUTE_POSITION_10"));
			
		printText("（1b 24）这行文字距离行首10个单位");// 之前这边没有换行
		 //设置绝对打印位置200个单位
		commandText(command.get("ABSOLUTE_POSITION"));
		printText("（1b 24）这行文字距离行首200个单位");
		//设置相对横向打印位置10个单位，10的十六进制为0A，看是否会被判断为结束符，去掉最后的结束符
		commandText(command.get("RELATION_POSITION_10"));
		printText("（ 1b 5c）这行文字距离前一段文字10个单位\n文字文字");
		//设置相对横向打印位置60个单位
		commandText(command.get("RELATION_POSITION"));
		printText("（ 1b 5c）这行文字距离前一段文字60个单位\n");
		printText("------------------------------\n");
		//设置距离行首字符左间距20个单位
		commandText(command.get("LEFT_SPACE"));
		printText("(1d 4c)这段文字设置字符距离行首左间距为20个单位：hello\n");
		//设置距离行首字符左间距10个单位，10的十六进制为0A，看是否会被判断为结束符，去掉最后的结束符
		commandText(command.get("LEFT_SPACE_10"));//(1d 4c)这段文字设置字符距离行
		printText("(1d 4c)这段文字设置字符距离行首左间距为10个单位:hello");
		//设置字符左间距0
		commandText(command.get("LEFT_SPACE_DEFALUT"));
		printText("(1d 4c)这段文字设置字符距离行首左间距为默认：hello\n");
		printText("-------------------------------\n");
		
		//设置行间距 80 (n表示行间距为n个像素点，最大值256)
		commandText(command.get("LINE_SPACING"));
		printText("(1b 33)行间距为80个像素点\n行间距为80个像素点\n行间距为80个像素点\n");
		printText("-------------------------------\n");
		//设置行间距 10 (n表示行间距为n个像素点，最大值256)
		commandText(command.get("LINE_SPACING_10"));
		printText("(1b 33)行间距为10个像素点，行间距为10个像素点，行间距为10个像素点。");
		printText("-------------------------------\n");
		//设置行间距默认
		commandText(command.get("LINE_SPACING_DEFAULT"));
		printText("(1b 32)行间距为默认\n行间距为默认\n行间距为默认\n");
		printText("-------------------------------\n");
		
		//设置跳格位置
		printText("(1b 44)以下设置跳隔位置，位置分别为1，25，33，40，为距离行首的距离\n");
		commandText(command.get("SPACE1"));
		commandText(command.get("HT"));
		printText("商品");
		commandText(command.get("SPACE25"));
		commandText(command.get("HT"));
		printText("单价");
		commandText(command.get("SPACE33"));
		commandText(command.get("HT"));
		printText("数量");
		commandText(command.get("SPACE40"));
		commandText(command.get("HT"));
		printText("合计");
		commandText(command.get("CANCEL_SPACE"));
		printText("\n-------------------------------\n");

		
	
		//顺时针旋转90°
		commandText(command.get("ROTATE"));
		printText("(1b 56 49)顺时针旋转90°\n");
		//取消顺时针旋转90°
		commandText(command.get("ROTATE_CANCEL"));
		printText("(1b 56 48)取消顺时针旋转90°\n");
		//顺时针旋转90°
		commandText(command.get("ROTATE_SMALL_INT"));
		printText("(1b 56 1)顺时针旋转90°\n");
		//取消顺时针旋转90°
		commandText(command.get("ROTATE_CANCEL_SMALL_INT"));
		printText("(1b 56 0)取消顺时针旋转90°\n");
		printText("-------------------------------\n");
		//倒置打印
		commandText(command.get("UPDOWN"));
		printText("(1b 7d)本行文字设置倒置打印\n");
		//取消倒置打印
		commandText(command.get("UPDOWN_CANCEL"));
		printText("(1b 7d)本行文字设置取消倒置打印\n");
		printText("-------------------------------\n");
		//黑白反显
		commandText(command.get("BLACK"));
		printText("(1d 42)本行文字设置黑白反显打印\n"); 
		commandText(command.get("BLACK_CANCEL"));
		printText("(1d 42)本行文字取消黑白反显打印\n"); 
		printText("-------------------------------\n");
		//设置打印区域宽度 
		commandText(command.get("PRINT_AREA_WIDTH"));
		printText("(1d 57)设置可打印区域为180，左边距为默认值，设置可打印区域为180，左边距为默认值，设置可打印区域为180，左边距为默认值\n"); 
		//设置打印区域宽度 
		commandText(command.get("PRINT_AREA_WIDTH_10"));
		printText("(1d 57)设置可打印区域为10，左边距为默认值\n"); 
		//设置打印区域宽度默认
		commandText(command.get("PRINT_AREA_WIDTH_DEFAULT"));
		printText("-------------------------------\n");
		//设置横向纵向移动单位 设置为最大255  要在其他使用到单位的指令中使用才会显示效果
		commandText(command.get("HOR_VER_UNIT"));
		commandText(command.get("CHINESE_SPACE_10"));
		commandText(command.get("LINE_SPACING_10"));
		printText("(1d 50)本段文字设置横向纵向移动单位为255，本段文字设置横向纵向移动单位为255，本段文字设置横向纵向移动单位为255\n");
		printText("-------------------------------\n");
		//设置横向纵向移动单位 设置为10 要在其他使用到单位的指令中使用才会显示效果
		commandText(command.get("HOR_VER_UNIT_10"));
		commandText(command.get("CHINESE_SPACE_10"));
		commandText(command.get("LINE_SPACING_10"));
		printText("(1d 50)本段文字设置横向纵向移动单位为10\n");
		printText("-------------------------------\n");
		//设置横向纵向移动单位 设置为默认200 要在其他使用到单位的指令中使用才会显示效果
		commandText(command.get("HOR_VER_UNIT_Defult"));
		commandText(command.get("CHINESE_SPACE_10"));
		commandText(command.get("LINE_SPACING_10"));
		printText("(1d 50)本段文字设置横向纵向移动单位为默认200，本段文字设置横向纵向移动单位为默认200，本段文字设置横向纵向移动单位为为默认200\n");
		printText("-------------------------------\n");	
		commandText(command.get("CHINESE_SPACE_DEFALUT"));
		commandText(command.get("LINE_SPACING_DEFAULT"));
		//走纸
		commandText(command.get("PAPER_THROW_S"));
		printText("(1b 4a)打印缓存区数据并走纸100个单位\n");
		printText("-------------------------------\n");
		//走纸
		commandText(command.get("PAPER_THROW_S_10"));
		printText("(1b 4a)打印缓存区数据并走纸10个单位\n");
		printText("-------------------------------\n");
		//走纸行
		commandText(command.get("PAPER_THROW_L"));
		printText("(1b 64)走纸10行\n");
		printText("-------------------------------\n");	
		//新增用例 可打印字符+结束符+可打印字符 客户出现的问题 20180714
		printText("本行打印不应有问\n题：中国我爱你");
		printText("爱你爱你2018年7月14日\n");
		printText("-------------------------------\n");
		
		
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "打印效果与预期一致，则测试通过");
	}
	/**
	 * 一维码二维码指令测试
	 */
	public void OneOrTwodimensionalcodeInstructTest(){
		if (GlobalVariable.currentPlatform==Model_Type.X5) {
			Log.d("cd", "X5适配打印");
			if (gui.cls_show_msg("确认使用58mm打印纸？【是】点击确认【否】点击其他")==ENTER) {
				commandText(command.get("PRINT_AREA_WIDTH_X5_DEFAULT"));
			}else {
				commandText(command.get("PRINT_AREA_WIDTH_X5"));
			}
			
		}
		printText("(1d 6b)以下打印条码，要对照条码是否打印正确并用扫码软件扫描，能正确扫描为通过\n"); 
		printText("\n"); 
		
		//打印EAN8
		commandText(command.get("ALIGN_CENTER"));
		printText("以下打印EAN8，条码注释为标准ASCII字体且在条码下方 ，条码高度60（01234567）， 宽度为模式2，居中\n"); 
		commandText(command.get("HIR_BELOW"));
		commandText(command.get("HIR_STANDARD_FONT"));
		commandText(command.get("BARCODEHEIGHT_SISTY"));
		commandText(command.get("BARCODEWIDTH2"));
		commandText(command.get("EAN8"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_RIGHT"));
		printText("以下打印EAN8，条码注释为压缩ASCII字体且在条码上方 ，条码高度60（01234567）， 宽度为模式2，居右\n"); 
		commandText(command.get("HIR_UP"));
		commandText(command.get("HIR_COMPRESSED_FONT"));
		commandText(command.get("BARCODEHEIGHT_SISTY"));
		commandText(command.get("BARCODEWIDTH2"));
		commandText(command.get("EAN8_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
	
	//	打印CODE93
		commandText(command.get("ALIGN_LEFT"));
		printText("以下打印CODE93，无条码注释，条码高度为默认162（0123434）， 宽度为模式3，比EAN8条码宽，居左\n"); 
		commandText(command.get("HIR_NULL"));
		commandText(command.get("BARCODEHEIGHT"));
		commandText(command.get("BARCODEWIDTH3"));
		commandText(command.get("CODE93"));
		printText("\n");
		printText("-------------------------------\n");

		commandText(command.get("ALIGN_CENTER_SMALL_INT"));
		printText("以下打印UPCA，条码注释为标准ASCII字体且在条码下方，条码高度为最大255（012345678901），宽度为模式4，比CODE93条码宽，居中\n"); 
		commandText(command.get("HIR_BELOW_SMALL_INT"));
		commandText(command.get("HIR_STANDARD_FONT_SMALL_INT"));
		commandText(command.get("BARCODEHEIGHT_MAX"));
//		if (x5flag) {
//		  commandText(command.get("BARCODEWIDTH3"));
//		}
		commandText(command.get("BARCODEWIDTH4"));
		commandText(command.get("UPCA"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
		printText("以下打印UPCA，条码注释为压缩ASCII字体且在条码上方，条码高度为最大255（012345678901），宽度为模式4，比CODE93条码宽，居右\n"); 
		commandText(command.get("HIR_UP_SMALL_INT"));
		commandText(command.get("HIR_COMPRESSED_FONT_SMALL_INT"));
		commandText(command.get("BARCODEHEIGHT_MAX"));
		commandText(command.get("BARCODEWIDTH4"));
		commandText(command.get("UPCA_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_CENTER_SMALL_INT"));
		printText("以下打印UPCE，条码注释为标准ASCII字体且在条码【上下方】，条码高度为默认162（012345451122）,条码宽度模式为5，比UPCA码宽，居中\n"); 
		commandText(command.get("HIR_BELOW_UP"));
		commandText(command.get("HIR_STANDARD_FONT"));
		commandText(command.get("BARCODEHEIGHT"));
		commandText(command.get("BARCODEWIDTH5"));
		commandText(command.get("UPCE"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
		printText("以下打印UPCE，条码注释为标准ASCII字体且在条码【上下方】，条码高度为默认162（012345451122）,条码宽度模式为5，比UPCA码宽，居右\n"); 
		commandText(command.get("HIR_BELOW_UP_SMALL_INT"));
		commandText(command.get("BARCODEHEIGHT"));
		commandText(command.get("BARCODEWIDTH5"));
		commandText(command.get("UPCE_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_LEFT_SMALL_INT"));
		printText("以下打印EAN13,条码注释为标准ASCII字体且在条码下方，条码高度为默认162（0123456789999）,条码宽度模式为6，比UPCE码宽，居左\n"); 
		commandText(command.get("HIR_BELOW_SMALL_INT"));
		commandText(command.get("BARCODEWIDTH6"));
		commandText(command.get("EAN13"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_CENTER"));
		printText("以下打印EAN13,条码注释为标准ASCII字体且在条码下方，条码高度为默认162（0123456789999）,条码宽度模式为6，比UPCE码宽，居中\n"); 
		commandText(command.get("BARCODEWIDTH6"));
		commandText(command.get("EAN13_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
		printText("以下打印CODE39,条码注释为标准ASCII字体且在条码下方,条码高度为默认162（*0123456789*）,条码宽度模式为默认3，居右\n"); 
		commandText(command.get("BARCODEWIDTH3"));
		commandText(command.get("CODE39"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_LEFT_SMALL_INT"));
		printText("以下打印CODE39,条码注释为标准ASCII字体且在条码下方,条码高度为默认162（*0123456789*）,条码宽度模式为默认3，居左\n"); 
		commandText(command.get("BARCODEWIDTH3"));
		commandText(command.get("CODE39_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
		
		
		//可打印字符+一维码数据+结束符
		commandText(command.get("ALIGN_CENTER"));
		printText("以下打印ITF，条码注释为标准ASCII字体且在条码下方,条码高度为默认162（012343456789）,条码宽度模式为默认3,居中"); 
		commandText(command.get("ITF"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_RIGHT"));
		printText("以下打印ITF，条码注释为标准ASCII字体且在条码下方,条码高度为默认162（012343456789）,条码宽度模式为默认3，居右"); 
		commandText(command.get("ITF_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
		
		printText("以下打印CODEBAR，条码注释为标准ASCII字体且在条码下方,条码高度为默认162（0123434）,条码宽度模式为默认3,居右\n"); 
		commandText(command.get("CODEBAR"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_LEFT"));
		printText("以下打印CODEBAR，条码注释为标准ASCII字体且在条码下方,条码高度为默认162（0123434）,条码宽度模式为默认3，居左\n"); 
		commandText(command.get("CODEBAR_SMALL_INT"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_LEFT_SMALL_INT"));
		printText("以下打印CODE128,无注释,条码高度为默认162（0123456789）,条码宽度模式为默认3，居左\n"); 
		commandText(command.get("HIR_NULL_SMALL_INT"));
		commandText(command.get("CODE128"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_LEFT"));
		printText("以下打印QRCODE,模块大小为默认3,内容为Gprinter），居左\n"); 
		commandText(command.get("SETQRCODESIZE3"));
		commandText(command.get("PUTQRCODETOMEMORYAREA"));
		commandText(command.get("PRINTERQRCODE"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_CENTER"));
		printText("以下打印QRCODE,模块大小为最小1,内容为Gprinter），居中\n"); 
		commandText(command.get("SETQRCODESIZE1")); 
		commandText(command.get("PUTQRCODETOMEMORYAREA"));
		commandText(command.get("PRINTERQRCODE"));
		printText("\n");
		printText("-------------------------------\n");
//		
		commandText(command.get("ALIGN_RIGHT"));
		printText("以下打印QRCODE,模块大小为最大16,内容为ABC123456789DEFGHabcdefgh），居右\n"); 
		commandText(command.get("SETQRCODESIZE16"));
		commandText(command.get("PUTQRCODETOMEMORYAREA2"));
		commandText(command.get("PRINTERQRCODE"));
		printText("\n");
		printText("-------------------------------\n");
		
		commandText(command.get("ALIGN_LEFT"));
	
	}
	//光栅位图打印指令
	public void GratingBitmap(){
		Bitmap bit=null;
		//开发回复：N700底座比较low 无法打印图片
		if(GlobalVariable.currentPlatform!=Model_Type.N700){
			//打印光删位图 1D 76 30 m值为0或者48 模式正常，结合对齐方式
			if (GlobalVariable.currentPlatform==Model_Type.X5) {
				Log.d("cd", "X5适配打印");
				if (gui.cls_show_msg("确认使用58mm打印纸？【是】点击确认【否】点击其他")==ENTER) {
					commandText(command.get("PRINT_AREA_WIDTH_X5_DEFAULT"));
				}else {
					commandText(command.get("PRINT_AREA_WIDTH_X5"));
				}
				
			}
			commandText(command.get("ALIGN_LEFT"));
			printText("（1D 76 30 48）以下打印png图片，正常模式，居左\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/font.png");
			byte[] startByteNomal={0x1d,0x76,0x30,48};
			byte[] imageByte=getBitMapBytes(bit,startByteNomal);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_CENTER"));
			printText("（1D 76 30 48）以下打印png图片，正常模式，居中\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/font.png");
			imageByte=getBitMapBytes(bit,startByteNomal);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_RIGHT"));
			printText("（1D 76 30 48）以下打印png图片，正常模式，居右\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/font.png");
			imageByte=getBitMapBytes(bit,startByteNomal);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为1或者49 模式倍宽
			commandText(command.get("ALIGN_LEFT"));
			printText("（1D 76 30 49）以下打印bmp图片，倍宽模式，居左\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/bmp01.bmp");
			byte[] startByteDoubleWidth={0x1d,0x76,0x30,49};
			imageByte=getBitMapBytes(bit,startByteDoubleWidth);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_CENTER"));
			printText("（1D 76 30 49）以下打印bmp图片，倍宽模式，居中\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/bmp01.bmp");
			imageByte=getBitMapBytes(bit,startByteDoubleWidth);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_RIGHT"));
			printText("（1D 76 30 49）以下打印bmp图片，倍宽模式，居右\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/bmp01.bmp");
			imageByte=getBitMapBytes(bit,startByteDoubleWidth);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为2或者50 模式倍高
			commandText(command.get("ALIGN_LEFT"));
			printText("（1D 76 30 50）以下打印bmp图片，倍高模式，居左\n"); 
			byte[] startByteDoubleHeight={0x1d,0x76,0x30,50};
			imageByte=getBitMapBytes(bit,startByteDoubleHeight);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_CENTER"));
			printText("（1D 76 30 50）以下打印bmp图片，倍高模式，居中\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleHeight);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_RIGHT"));
			printText("（1D 76 30 50）以下打印bmp图片，倍高模式，居右\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleHeight);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为3 或者51模式倍高倍宽，居左
			commandText(command.get("ALIGN_LEFT"));
			printText("（1D 76 30 51）以下打印bmp图片，倍高倍宽模式，居左\n"); 
			byte[] startByteDoubleWidthHeight={0x1d,0x76,0x30,51};
			imageByte=getBitMapBytes(bit,startByteDoubleWidthHeight);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为3 或者51模式倍高倍宽，居中
			commandText(command.get("ALIGN_CENTER"));
			printText("（1D 76 30 51）以下打印bmp图片，倍高倍宽模式，居中\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleWidthHeight);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为3 或者51模式倍高倍宽，居右
			commandText(command.get("ALIGN_RIGHT"));
			printText("（1D 76 30 51）以下打印bmp图片，倍高倍宽模式，居右\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleWidthHeight);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			
			//打印光删位图 1D 76 30 m值为0或者48 模式 正常
			commandText(command.get("ALIGN_LEFT_SMALL_INT"));
			printText("（1D 76 30 0）以下打印jpg图片，正常模式，居左\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/png.jpg");
			byte[] startByteNomalSmallInt={0x1d,0x76,0x30,0};
			imageByte=getBitMapBytes(bit,startByteNomalSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_CENTER_SMALL_INT"));
			printText("（1D 76 30 0）以下打印jpg图片，正常模式，居中\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/png.jpg");
			imageByte=getBitMapBytes(bit,startByteNomalSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			
			commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
			printText("（1D 76 30 0）以下打印jpg图片，正常模式，居右\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/png.jpg");
			imageByte=getBitMapBytes(bit,startByteNomalSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为1或者49 模式倍宽
			commandText(command.get("ALIGN_LEFT_SMALL_INT"));
			printText("（1D 76 30 1）以下打印bmp图片，倍宽模式，居左\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/bmp01.bmp");
			byte[] startByteDoubleWidthSmallInt={0x1d,0x76,0x30,1};
			imageByte=getBitMapBytes(bit,startByteDoubleWidthSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_CENTER_SMALL_INT"));
			printText("（1D 76 30 1）以下打印bmp图片，倍宽模式，居中\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/bmp01.bmp");
			imageByte=getBitMapBytes(bit,startByteDoubleWidthSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
			printText("（1D 76 30 1）以下打印bmp图片，倍宽模式，居右\n"); 
			bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/bmp01.bmp");
			imageByte=getBitMapBytes(bit,startByteDoubleWidthSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			//打印光删位图 1D 76 30 m值为2或者50 模式倍高
			commandText(command.get("ALIGN_LEFT_SMALL_INT"));
			printText("（1D 76 30 2）以下打印bmp图片，倍高模式，居左\n"); 
			byte[] startByteDoubleHeightSmallInt={0x1d,0x76,0x30,2};
			imageByte=getBitMapBytes(bit,startByteDoubleHeightSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_CENTER_SMALL_INT"));
			printText("（1D 76 30 2）以下打印bmp图片，倍高模式，居中\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleHeightSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
			printText("（1D 76 30 2）以下打印bmp图片，倍高模式，居右\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleHeightSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
		
			//打印光删位图 1D 76 30 m值为3 或者51模式倍高倍宽
			commandText(command.get("ALIGN_LEFT_SMALL_INT"));
			printText("（1D 76 30 3）以下打印bmp图片，倍高倍宽模式，居左\n"); 
			byte[] startByteDoubleWidthHeightSmallInt={0x1d,0x76,0x30,3};
			imageByte=getBitMapBytes(bit,startByteDoubleWidthHeightSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			

			commandText(command.get("ALIGN_CENTER_SMALL_INT"));
			printText("（1D 76 30 3）以下打印bmp图片，倍高倍宽模式，居中\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleWidthHeightSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
			
			commandText(command.get("ALIGN_RIGHT_SMALL_INT"));
			printText("（1D 76 30 3）以下打印bmp图片，倍高倍宽模式，居右\n"); 
			imageByte=getBitMapBytes(bit,startByteDoubleWidthHeightSmallInt);
			commandText(imageByte);
			printText("\n");
			printText("-------------------------------\n");
		}else{
			gui.cls_show_msg1_record(fileName, "GratingBitmap", gKeepTimeErr, "line %d：N700不支持打印图片", Tools.getLineInfo());
		}
		commandText(command.get("ALIGN_LEFT"));
	}
	public void test1B2APrintBitmap(){
		// 打印图片
		// 获取图片font
		// 开发回复：N700底座比较low 无法打印图片
		Bitmap bit = null;
		if (GlobalVariable.currentPlatform != Model_Type.N700) {
			
			if (GlobalVariable.currentPlatform==Model_Type.X5) {
				Log.d("cd", "X5适配打印");
				if (gui.cls_show_msg("确认使用58mm打印纸？【是】点击确认【否】点击其他")==ENTER) {
					commandText(command.get("PRINT_AREA_WIDTH_X5_DEFAULT"));
				}else {
					commandText(command.get("PRINT_AREA_WIDTH_X5"));
				}
				
			}
			printText("(1b 2a)以下打印png图片，换行为0A方式\n");
			bit = BitmapFactory.decodeFile(GlobalVariable.sdPath+ "picture/font.png");
			// 去除透明度
			bit = printUtil.compressPic(bit);
			printUtil.myOtsu(bit);
			// 处理数据，组装命令
			byte[] imgBuf = draw2PxPoint(bit, 1);
			// 发送命令
			commandText(imgBuf);
			printText("-------------------------------\n");
			// String s=Dump.getHexDump(imgBuf);
			// writeResult(s);
			printText("(1b 2a)以下打印jpg图片\n");
			bit = BitmapFactory.decodeFile(GlobalVariable.sdPath+ "picture/png.jpg");
			// 去除透明度
			bit = printUtil.compressPic(bit);
			printUtil.myOtsu(bit);
			// 处理数据，组装命令
			imgBuf = draw2PxPoint(bit, 1);
			// 发送命令
			commandText(imgBuf);
			printText("-------------------------------\n");
			// 新增测试：可打印字符+图片数据+结束符 20180714
			printText("(1b 2a)以下打印bmp图片");
			bit = BitmapFactory.decodeFile(GlobalVariable.sdPath+ "picture/bmp01.bmp");
			// 去除透明度
			bit = printUtil.compressPic(bit);
			printUtil.myOtsu(bit);
			// 处理数据，组装命令
			imgBuf = draw2PxPoint(bit, 1);
			// 发送命令
			commandText(imgBuf);

			printText("-------------------------------\n");

			// 新增测试点：结束符为0D 0A方式
			printText("(1b 2a)以下打印png图片，换行为0D 0A方式\n");
			bit = BitmapFactory.decodeFile(GlobalVariable.sdPath+ "picture/font.png");
			// 去除透明度
			bit = printUtil.compressPic(bit);
			printUtil.myOtsu(bit);
			// 处理数据，组装命令
			imgBuf = draw2PxPoint(bit, 2);
			// 发送命令
			commandText(imgBuf);
			printText("-------------------------------\n");
		}else
		{
			gui.cls_show_msg1_record(fileName, "test1B2APrintBitmap", gKeepTimeErr, "line %d：N700不支持打印图片", Tools.getLineInfo());
		}
		commandText(command.get("ALIGN_LEFT"));
	}
	public void printBill(){
		//打印机复位
		commandText(command.get("RESET"));
		//打印单据，分2段下发，看是否有丢失数据，客户报的bug，20180724
		 for(int i=0;i<3;i++){
			 prinBill(true);
			 SystemClock.sleep(3000);
		 }
		 gui.cls_show_msg1_record(fileName, "printBill", gScreenTime, "单据完整，无缺失或多余数据，则测试通过");
	}
	/**
	 * 打印各国货币符号
	 */
	public void printCurrencySymbol(){
		commandText(command.get("ALIGN_LEFT")); //左对齐
		printText("以下文字左对齐----------------\n");
		printText("本行显示美元符号：$$$$$$$$$$$$\n");
		printText("-------------------------------\n");
		printText("本行显示人民币符号：￥￥￥￥￥￥￥\n");
		printText("-------------------------------\n");
		printText("本行显示英镑符号：££££££££££££££\n");
		printText("-------------------------------\n");
		printText("本行显示欧元符号：€€€€€€€€€€€€€\n");
		printText("-------------------------------\n");
		printText("本行显示伊朗符号：﷼﷼﷼﷼﷼﷼﷼﷼﷼﷼\n");
		printText("-------------------------------\n");
		printText("本行显示印尼盾符号：₨₨₨₨₨₨₨₨₨₨₨₨₨₨\n");
		printText("-------------------------------\n");
		printText("本行显示瑞士符号：FrFrFrFrFrFrFr\n");
		printText("-------------------------------\n");
		printText("本行显示泰铢符号：฿฿฿฿฿฿฿฿฿฿฿฿฿฿\n");
		printText("-------------------------------\n");
		printText("本行显示老挝符号：₭₭₭₭₭₭₭₭₭₭₭₭₭₭\n");
	}
	public void printState(){
		//case1:打印机状态 
		//case1.1:钱箱？（没有测试）
		//case1.2:联机脱机？（没有测试）
		//case2:脱机状态
		//case2.1:上盖开上盖关（没有测试）
		//case2.2:未按走纸键 已按走纸键（没有测试）
		//case2.3:打印机缺纸 打印机不缺纸
		if(gui.cls_show_msg("请确保打印机无纸，[取消]退出，任意键继续")==ESC)
			return;
		//查询状态 单独发送应马上返回
		commandText(command.get("PRINTERSTATE2"));
		int iRet = getResult();
		if(iRet!=0x32){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr,"line %d：检测缺纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		//与其他指令一起使用，与结束符配合才能立即返回状态
		commandText(command.get("PRINTERSTATE2"));
		printText("正在打印，应检测到缺纸状态\n"); 
		iRet = getResult();
		if(iRet!=0x32){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr,"line %d：检测缺纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		if(gui.cls_show_msg("请确保打印机有纸，[取消]退出，任意键继续")==ESC)
			return;
		//查询状态 单独发送应马上返回
		commandText(command.get("PRINTERSTATE2"));
		iRet = getResult();
		if(iRet!=0x12){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr, "line %d：检测有纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		//与其他指令一起使用，与结束符配合才能立即返回状态
		printText("正在打印，应检测到有纸状态\n"); 
		commandText(command.get("PRINTERSTATE2"));
		iRet = getResult();
		if(iRet!=0x12){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr, "line %d：检测有纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		//case2.4:有错误情况 没有出错情况（无法构造）
		//case3:错误状态
		//case3.1:切刀无错误 切刀有错误（无法构造）
		//case3.2:无不可恢复错误 有不可恢复错误（无法构造）
		//case3.3:打印头温度和电压正常 打印头温度和电压超出范围（无设置灰度指令，打印过热比较难）
//		while(true){
//			commandText(PRINTERSTATE3);
//			prinBill(false);
//			if(getResult().equals("42")){
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "打印机已过热，即将退出");
//				break;
//			}
//		}
		//case4:传送纸状态 有纸或无纸
		if(gui.cls_show_msg("请确保打印机无纸，[取消]退出，任意键继续")==ESC)
			return;
		//查询状态 单独发送应马上返回
		commandText(command.get("PRINTERSTATE4"));
		iRet = getResult();
		if(iRet!=0x7E){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr, "line %d：检测缺纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		//与其他指令一起使用，与结束符配合才能立即返回状态
		printText("正在打印，应检测到缺纸状态\n"); 
		commandText(command.get("PRINTERSTATE4"));
		iRet = getResult();
		if(iRet!=0x7E){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr,"line %d：检测缺纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		if(gui.cls_show_msg("请确保打印机有纸，[取消]退出，任意键继续")==ESC)
			return;
		//查询状态 单独发送应马上返回
		commandText(command.get("PRINTERSTATE4"));
		iRet = getResult();
		if(iRet!=0x12){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr, "line %d：检测有纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		//与其他指令一起使用，与结束符配合才能立即返回状态
		printText("正在打印，应检测到有纸状态\n"); 
		commandText(command.get("PRINTERSTATE4"));
		iRet = getResult();
		if(iRet!=0x12){
			 gui.cls_show_msg1_record(fileName, "printState", gKeepTimeErr, "line %d：检测有纸状态失败(0x%x)", Tools.getLineInfo(),iRet);
		}
		 gui.cls_show_msg1_record(fileName, "printState", gScreenTime,"打印机状态检测测试通过");
	}
	/**
	 * add zhangxinj 2018/11/07
	 * 需求：新增保存打印数据到数据库、新增保存数据使能接口、数据库拥有者为mtms
	 */
	public void setDataToDB(){
		gui.cls_show_msg1(2, "正在测试保存蓝牙打印数据到数据库接口...");
		SettingsManager settingsManager =  (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		String dbFileName="/data/share/EpayParameter/print_records.db";
		File dbFile=new File(dbFileName);
		boolean ret = false;
		//case1：打开数据库开关 进行蓝牙打印 会生成数据库db文件
		if(dbFile.exists()){
			dbFile.delete();
		}
		if((ret=settingsManager.setBluetoothCmdCollectEnable(true))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:开启蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		commandText(command.get("RIGHT_SPACE"));
		printText("这行文字设置字符右间距为20个单位：hello\n"); 
		if(!dbFile.exists()){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:开启蓝牙打印数据收集后未生成数据库db文件", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case2：删除数据库文件，关闭数据库开关 进行蓝牙打印 不会再生成数据
		if(dbFile.exists()){
			dbFile.delete();
		}
		if((ret=settingsManager.setBluetoothCmdCollectEnable(false))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:关闭蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		commandText(command.get("RIGHT_SPACE_DEFALUT"));
		printText("这行文字设置字符右间距为默认：hello\n");
		printText("-------------------------------\n");
		if(dbFile.exists()){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:关闭蓝牙打印数据收集后生成了数据库db文件", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case3：重复打开数据库开关，应会生成数据库db文件
		if((ret=settingsManager.setBluetoothCmdCollectEnable(true))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:开启蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=settingsManager.setBluetoothCmdCollectEnable(true))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:重复开启蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		commandText(command.get("UPDOWN"));
		printText("本行文字设置倒置打印\n");
		printText("-------------------------------\n");
		if(!dbFile.exists()){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr, "line %d:开启蓝牙打印数据收集后未生成数据库db文件", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case4：重复关闭数据库开关，应不会生成db文件
		if(dbFile.exists()){
			dbFile.delete();
		}
		if((ret=settingsManager.setBluetoothCmdCollectEnable(false))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr, "line %d:关闭蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=settingsManager.setBluetoothCmdCollectEnable(false))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:重复关闭蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		commandText(command.get("UPDOWN_CANCEL"));
		printText("本行文字取消倒置打印\n");
		printText("-------------------------------\n");
		if(dbFile.exists()){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr, "line %d:关闭蓝牙打印数据收集后生成了数据库db文件", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case5: 打印文字，图片，二维码，应能正确存入数据库，条数内容应正确，不应有遗漏
		List<byte[]> list=new ArrayList<byte[]>();
		list.add(command.get("ALIGN_CENTER"));
		list.add(getStringByte("本行文字居中打印\n"));
		list.add(getStringByte("-------------------------------\n"));
		list.add(command.get("ALIGN_LEFT_SMALL_INT"));
		list.add(getStringByte("本行文字居左打印\n"));
		list.add(getStringByte("-------------------------------\n"));
		list.add(getStringByte("以下打印EAN8条码\n")); 
		list.add(command.get("EAN8"));
		list.add(getStringByte("\n")); 
		list.add(getStringByte("-------------------------------\n"));
		list.add(getStringByte("以下打印图片\n")); 
		Bitmap bit=BitmapFactory.decodeFile(GlobalVariable.sdPath + "picture/font.png");
		byte[] startByteNomal={0x1d,0x76,0x30,0};
		byte[] imageByte=getBitMapBytes(bit,startByteNomal);
		list.add(imageByte);
		list.add(getStringByte("\n"));
		//打开收集数据开关
		if((ret=settingsManager.setBluetoothCmdCollectEnable(true))!=true){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr, "line %d:开启蓝牙打印数据收集出错(ret=%s)", Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//进行蓝牙打印
		for(byte[] listItem:list){
			commandText(listItem);
		}
		//进行数据库操作
		DBHelper dbHelper=new DBHelper(myactivity, "/data/share/EpayParameter/print_records.db", null, 1);
		dbHelper.setWriteAheadLoggingEnabled(false);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM BluetoothPrinter", null);
		int countNum=cursor.getCount();
		if(countNum!=list.size()){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:读取数据库内容条数有误(预期：%d，实际：%d)", Tools.getLineInfo(),list.size(),countNum);
			if (!GlobalVariable.isContinue)
				return;
		}
		int i=0;
		while (cursor.moveToNext()) {
//			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			String callerPkgName = cursor.getString(cursor.getColumnIndex("callerPkgName"));
			if(!callerPkgName.equals("com.example.highplattest")){
				gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:（第%d条）读取数据库内容包名出错", Tools.getLineInfo(),i+1);
				if (!GlobalVariable.isContinue)
					return;
			}
//			String timeStamp = cursor.getString(cursor.getColumnIndex("timeStamp"));
			String CmdContent = cursor.getString(cursor.getColumnIndex("CmdContent"));
			if(!CmdContent.equals(Dump.getHexDump(list.get(i)))){
				gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr,"line %d:（第%d条）读取数据库内容出错", Tools.getLineInfo(),i+1);
				if (!GlobalVariable.isContinue)
					return;
			}
			i++;
		}
		cursor.close();
		dbHelper.close();
		//case6：查询数据库拥有者为mtms
		if(gui.cls_show_msg("adb进入/data/share/EpayParameter目录下，执行ls -l，查询出第二列是否为mtms（数据库拥有者），[确认]为正确，[取消]为错误")==ESC){
			gui.cls_show_msg1_record(fileName, "setDataToDB", gKeepTimeErr, "line %d:数据库拥有者为非mtms", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
	
		gui.cls_show_msg1_record(fileName, "setDataToDB", gScreenTime, "保存蓝牙打印数据到数据库测试通过");
	}
	/**
	 * 异常测试
	 */
	public void abnormalTest(){
		String funcName="abnormalTest";
		flag_abnormal = 1;
		//case1:异常测试 连接上蓝牙后再断开蓝牙应不能打印
		prinBill(false);
		if(gui.cls_show_msg("是否有正常打印出单据，[确定]是，[取消]否")==ESC)
		{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:蓝牙打印单据失败", Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
		}
		bluetoothAdapter.disable();
		SystemClock.sleep(500);
			
		prinBill(false);
		if(gui.cls_show_msg("蓝牙已关闭，不应打印出单据，是否打印出单据，[确定]否，[取消]是")==ESC)
		{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:蓝牙已关闭还可以进行打印，出错", Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
		}
			
		bluetoothAdapter.enable();
		SystemClock.sleep(2000);
		// 建立连接
		if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:BT连接建立失败", Tools.getLineInfo());
			return;
		}
		flag_abnormal = 0;
		gui.cls_show_msg1_record(fileName,"abnormalTest", gScreenTime,"异常测试结束");
	}
	 /**
     * 图片灰度的转化
     */
    private  int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }

	  public  byte px2Byte(int x, int y, Bitmap bit) {
	        if (x < bit.getWidth() && y < bit.getHeight()) {
	            byte b;
	            int pixel = bit.getPixel(x, y);
	            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
	            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
	            int blue = pixel & 0x000000ff; // 取低两位
	            int gray = RGB2Gray(red, green, blue);
	            if (gray < 200) {
	                b = 1;
	            } else {
	                b = 0;
	            }
	            return b;
	        }
	        return 0;
	    }
	    /**
	     * 位图模式指令
	     * 指令：1B 2A
	     * @param bmp
	     * @return
	     */
	    public  byte[] draw2PxPoint(Bitmap bmp,int endType) {
	        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法      
	        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
	        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
	        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
	    	
	        int size = bmp.getWidth() * bmp.getHeight() / 8 +1000;
	        byte[] data = new byte[size];
	        int k = 0;
	        //设置行距为0的指令
	        data[k++] = 0x1B;
	        data[k++] = 0x33;
	        data[k++] = 0x00;
	        // 逐行打印
	        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
	            //打印图片的指令
	            data[k++] = 0x1B;
	            data[k++] = 0x2A;
	            data[k++] = 33; 
	            data[k++] = (byte) (bmp.getWidth() % 256); //nL
	            data[k++] = (byte) (bmp.getWidth() / 256); //nH
	            //对于每一行，逐列打印
	            for (int i = 0; i < bmp.getWidth(); i++) {
	                //每一列24个像素点，分为3个字节存储
	                for (int m = 0; m < 3; m++) {
	                    //每个字节表示8个像素点，0表示白色，1表示黑色
	                    for (int n = 0; n < 8; n++) {
	                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
	                        data[k] += data[k] + b;
	                    }
	                    k++;
	                }
	            }
	            //兼容外面打印机，新增0D 0A换行方式，zhangixnj 2019/1/21
	            if(endType==1)
	            	data[k++] = 10;//换行
	            else{
	            	data[k++] =0x0D;
	            	data[k++] =0x0A;
	            }
	            
	        }
	        return data;
	    }

	/**
	 * 打印光栅位图 指令：1D 76 30 m xL xH yL yH
	 * 
	 * @param img
	 * @return
	 */
	public byte[] getBitMapBytes(Bitmap img, byte[] startByte) {
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		int[] gray = new int[height * width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];
				int red = ((grey & 0x00FF0000) >> 16);
				gray[width * i + j] = red;
			}
		}

		int e = 0;
		int byteLen = width / 8;
		byte[] result = new byte[byteLen * height];
		int k = 0;
		for (int i = 0; i < height; i++) {
			byte[] bt = new byte[byteLen];
			for (int j = 0; j < width; j++) {
				int g = gray[width * i + j];
				int byteIndex = j / 8;
				int bitIndex = j % 8;
				if (g >= 128) {
					pixels[width * i + j] = 0xffffffff;
					e = g - 255;
				} else {
					pixels[width * i + j] = 0xff000000;
					if (j < byteLen * 8) {
						bt[byteIndex] += 1 << (7 - bitIndex);
					}
					e = g - 0;
				}

				if (j < width - 1 && i < height - 1) {// 右边像素处理
					gray[width * i + j + 1] += 3 * e / 8; // 下
					gray[width * (i + 1) + j] += 3 * e / 8;// 右下
					gray[width * (i + 1) + j + 1] += e / 4;
				} else if (j == width - 1 && i < height - 1) {// 靠右或靠下边s的像素的情况
					// 下方像素处理
					gray[width * (i + 1) + j] += 3 * e / 8;
				} else if (j < width - 1 && i == height - 1) {
					// 右边像素处理
					gray[width * (i) + j + 1] += e / 4;
				}
			}
			for (int m = 0; m < bt.length; m++) {
				result[k++] = bt[m];
			}
		}
		byte xl = (byte) (byteLen % 256);
		byte xh = (byte) (byteLen / 256);
		byte yl = (byte) (height % 256);
		byte yh = (byte) (height / 256);
		byte[] imageWH = { xl, xh, yl, yh };
		byte[] allByte = byteMergerAll(startByte, imageWH, result);
		return allByte;
	}
    /**
     * 命令和文字拼接 
     * @param escCommand
     * @param text
     * @return 返回byte数组
     */
    public byte[] pinjie(byte[] escCommand,String text){
    	byte[] data;
        byte[] b=null;
        try {
        	data = text.getBytes("gbk");
        	b=new byte[escCommand.length+data.length];
            int position  = 0;
            System.arraycopy(escCommand, 0, b, position, escCommand.length);
	        position += escCommand.length;
	        System.arraycopy(data, 0, b, position, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
    /**
     * 单独下发命令
     * @param escCommand
     */
    public  void commandText(byte[] escCommand) {
    	boolean result=bluetoothManager.writeComm(dataService, escCommand);
		if(!result){
			String escString=Dump.getHexDump(escCommand);
			if(escCommand.length>11){
				escString=(String) escString.subSequence(0, 11);
			}
			gui.cls_show_msg1_record(fileName, "commandText", gKeepTimeErr,"line %d：蓝牙打印失败(指令：%s)", Tools.getLineInfo(),escString);
			if(GlobalVariable.isContinue==false)
				return;
		}
    }
    public int getResult(){
    	byte[] tempBuf = new byte[1];
    	if(bluetoothManager.readComm(dataService,tempBuf)==false)
    	{
    		gui.cls_show_msg1_record(fileName, "getResult", gKeepTimeErr, "line %d：蓝牙打印接收失败", Tools.getLineInfo());
			return -1;
    	}
    	LoggerUtil.e("返回的byte:"+tempBuf[0]);		
    	return tempBuf[0];
    }
    /**
     * 单独下发文字
     * @param text
     */
    public  void printText(String text) {
    	
        try {
        	byte[] data = text.getBytes("gbk");
        	boolean result=bluetoothManager.writeComm(dataService, data);
        	if(!result){
        		gui.cls_show_msg1_record(fileName, "printText", gKeepTimeErr, "line %d：蓝牙打印失败(%s)", Tools.getLineInfo(),result);
    			if(GlobalVariable.isContinue==false)
    				return;
    		}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 返回字符串的byte数组数据
     * @param text
     * @return
     */
    public byte[] getStringByte(String text){
		byte[] data = null;
		try {
			data = text.getBytes("gbk");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
    }
 
    /** * 打印三列时，中间一列的中心线距离打印纸左侧的距离 */ private static final int LEFT_LENGTH = 16;
    /** * 打印三列时，中间一列的中心线距离打印纸右侧的距离 */ private static final int RIGHT_LENGTH = 16; 
    /** * 打印三列时，第一列汉字最多显示几个文字 */ private static final int LEFT_TEXT_MAX_LENGTH = 7;
    private  int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }
    /**
     * 打印菜单项，行间距5，第一列要倍高，第二、三、四列取消倍高
     * @param leftText
     * @param middleText
     * @param middleText1
     * @param rightText
     * @return 返回byte数组
     */
	public  byte[] printFourData(String leftText, String middleText,String middleText1,
			String rightText) {

		// 左边最多显示 LEFT_TEXT_MAX_LENGTH个汉字 + 两个点
		if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
			leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
		}
		int leftTextLength = getBytesLength(leftText);
		int middleTextLength = getBytesLength(middleText);
		int middleTextLenth1=getBytesLength(middleText1);
		int rightTextLength = getBytesLength(rightText);
		
		byte[] left={0x1b, 0x21, 0x10,0x1c, 0x21,0x08,0x1b, 0x33,5};
		byte[] leftByte=pinjie(left,leftText);
		
		// 计算左侧文字和第二列文字的空格长度
		StringBuilder sb = new StringBuilder(); 
		int marginBetweenLeftAndSecond = LEFT_LENGTH - leftTextLength- middleTextLength;
		for (int i = 0; i < marginBetweenLeftAndSecond; i++) {
			sb.append(" ");
		}
		sb.append(middleText); 
    	byte[] right={0x1b, 0x61, 0x00,0x1d, 0x21, 0x00};

		//计算第二列和第三列之间的空格长度
		int marginTwoAndThree=RIGHT_LENGTH/2-middleTextLenth1;
		
		for (int i = 0; i < marginTwoAndThree; i++) {
			sb.append(" ");
		}
		sb.append(middleText1);
		// 计算右侧文字和第三列文字的空格长度
		int marginBetweenThreeAndRight = RIGHT_LENGTH/2 -rightTextLength;
		for (int i = 0; i < marginBetweenThreeAndRight; i++) {
			sb.append(" ");
		}
		sb.append(rightText);
		byte[] rightByte=pinjie(right,sb.toString());

		byte[] menu=byteMergerAll(leftByte,rightByte);
		return menu;
	}
	/**
	 * 打印菜单第一行4列 "商品名称", "单价", "数量","金额"
	 * @param leftText 
	 * @param middleText
	 * @param middleText1
	 * @param rightText
	 * @return
	 */
	public  String printFourData2(String leftText, String middleText,String middleText1,
			String rightText) {
		StringBuilder sb = new StringBuilder(); 
		// 左边最多显示 LEFT_TEXT_MAX_LENGTH个汉字 + 两个点
		if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
			leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
		}
		int leftTextLength = getBytesLength(leftText);
		int middleTextLength = getBytesLength(middleText);
		int middleTextLenth1=getBytesLength(middleText1);
		int rightTextLength = getBytesLength(rightText);

		sb.append(leftText);
		// 计算左侧文字和第二列文字的空格长度
		int marginBetweenLeftAndSecond = LEFT_LENGTH - leftTextLength- middleTextLength;
		for (int i = 0; i < marginBetweenLeftAndSecond; i++) {
			sb.append(" ");
		}
		sb.append(middleText); 
		//计算第二列和第三列之间的空格长度
		int marginTwoAndThree=RIGHT_LENGTH/2-middleTextLenth1;
		for (int i = 0; i < marginTwoAndThree; i++) {
			sb.append(" ");
		}
		sb.append(middleText1);
		// 计算右侧文字和第三列文字的空格长度
		int marginBetweenThreeAndRight = RIGHT_LENGTH/2 -rightTextLength;
		for (int i = 0; i < marginBetweenThreeAndRight; i++) {
			sb.append(" ");
		}
		sb.append(rightText);
		return sb.toString();
	}
	/**
	 * 一行打印3列
	 * @param leftText 最左边文字
	 * @param middleText 中间文字
	 * @param rightText 最右边文字
	 * @return
	 */
	public  String printThreeData(String leftText, String middleText,String rightText) {
		StringBuilder sb = new StringBuilder(); 
		int leftTextLength = getBytesLength(leftText);
		int middleTextLength = getBytesLength(middleText);
		int rightTextLength = getBytesLength(rightText);
		sb.append(leftText); 
		// 计算左侧文字和中间文字的空格长度
		int marginBetweenLeftAndMiddle = LEFT_LENGTH +RIGHT_LENGTH/2- leftTextLength- middleTextLength;
		for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
			sb.append(" ");
		}
		sb.append(middleText); 
		// 计算右侧文字和中间文字的空格长度
		int marginBetweenMiddleAndRight = RIGHT_LENGTH/2 - rightTextLength;
		for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
			sb.append(" ");
		}
		sb.append(rightText);
		return sb.toString();
	}


  
    /**
     * 打印美团轻收银点单单据
     */
    public void prinBill(boolean sleep){
    	//单据头部 字体双倍放大 居中
    	byte[] title={0x1b, 0x61, 0x01,0x1d, 0x21, 0x11};   
    	String titleText="测试\n\n收款单\n\n取餐号：020\n\n" ;
    	//单据内容头部，字体不放大，居左
//		// 选择加粗模式
//		commandText(command.get("BOLD"));
//		printText("(1b 45)这行文字加粗\n");
//		// 取消加粗模式
//		commandText(command.get("BOLD_CANCEL"));
//		printText("(1b 45)这行文字取消加粗\n");
//		printText("-------------------------------\n");
		
    	byte[] contentTitle={0x1b, 0x61, 0x00,0x1d, 0x21, 0x00};
    	String contentTitleText="订单号：4612700331796610863\n收款时间：2018-07-23 11:09:27\n收银员：店长\n\n" +
    			printFourData2("商品名称", "单价", "数量","金额\n")+
    			"--------------------------------\n";
    	//单据菜单项，第一列是高倍高，第二、三、四列取消倍高，行间距设为5
    	byte[] menu1=printFourData("吧测迪迪", "222", "1","222\n");
		byte[] menu2=printFourData("广告高校", "555", "1","555\n");
		byte[] menu3=printFourData("粑粑", "22", "1","22\n");
		byte[] menu4=printFourData("农家小炒肉", "25", "1","25\n");
		byte[] menu5=printFourData("糖醋里脊", "20", "1","20\n");
		byte[] menu6=printFourData("麻辣香锅", "20", "1","20\n");
		byte[] menu7=printFourData("宫保鸡丁", "20", "1","20\n");
		byte[] menu8=printFourData("珊姐姐", "3", "1","3\n");
		byte[] menu9=printFourData("5556", "2222", "1","2222\n");
		byte[] menu10=printFourData("222", "555", "1","555\n");
		//单据尾部，打印3列，居左，默认行间距
    	byte[] end={0x1b, 0x61, 0x00,0x1d, 0x21, 0x00,0x1b, 0x32};
    	String endText=	"-------------------------------\n" +
    			printThreeData("合计 ","10","3662.0\n") +
    			"                              0\n\n\n" +
    			"------------------------------\n" +
    			"应收：3662.00\n" +
    			"现金支付：3662.00\n" +
    			"实收：3662.00\n" +
    			"支付方式：现金支付\n" +
    			"------------------------------\n" +
    			"设备编号：N4NL00000162\n\n";
    	//单据最尾部，居中
    	byte[] end2={0x1b, 0x61, 0x01};
    	String endText2="谢谢惠顾，欢迎下次光临\n" +
    			"由美团轻收银提供技术支持\n\n\n\n\n\n";
    	byte[] titleByte=pinjie(title,titleText);
    	byte[] contentTitleByte=pinjie(contentTitle, contentTitleText);
    	byte[] endByte=pinjie(end, endText);
    	byte[] endByte2=pinjie(end2,endText2);
    	byte[] all=byteMergerAll(titleByte,contentTitleByte,menu1,menu2,menu3,menu4,menu5,menu6,menu7,menu8,menu9,menu10,endByte,endByte2);
    	
		int position = 0;
		//单据仿照美团打印，分为2部分打印
		byte[] onceByte = new byte[all.length/2];
		int leftLength = all.length % 2+all.length/2;

		byte[] left = new byte[leftLength];
		System.arraycopy(all, position, onceByte, 0, onceByte.length);
		bluetoothManager.writeComm(dataService, onceByte);
		position += onceByte.length;
		//是否休眠
		if(sleep){
			//休眠2秒打印另一半单据
			SystemClock.sleep(2000);
		}
		System.arraycopy(all, position, left, 0, leftLength);
		bluetoothManager.writeComm(dataService, left);
	//验证1b 45 bug 华燕要求增加在单据部分

		if(flag_abnormal == 0)//异常测试不验证1b 45，不然会报错
		{
			printText("下面验证1b45------------------------\n");
			commandText(command.get("BOLD"));
			printText("(1b 45 01)现金支付：3662.00\n,本行文字加粗效果");
			// 取消加粗模式
			commandText(command.get("BOLD_CANCEL"));
			printText("(1b 45 00)现金支付：3662.00\n,本行文字取消加粗效果");
		}    	


    	

    }
 
    	
    /**
     * 多个byte组数拼接成一个byte数组
     * @param values 多个byte数组
     * @return
     */
    private static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
            for (int i = 0; i < values.length; i++) {
                length_byte += values[i].length;
            }
            byte[] all_byte = new byte[length_byte];
            int countLength = 0;
            for (int i = 0; i < values.length; i++) {
                byte[] b = values[i];
                System.arraycopy(b, 0, all_byte, countLength, b.length);
                countLength += b.length;
            }
            return all_byte;
        }
//	public void writeResult(String s) {
//		String LOGFILE = GlobalVariable.sdPath + "imgdata.txt";
//		if (new FileSystem().JDK_FsOpen(LOGFILE, "w") < 0) {
//			gui.cls_show_msg1(2, SERIAL, "line %d: 打开文件失败,请查看文件系统是否异常",Tools.getLineInfo());
//			if (!GlobalVariable.isContinue)
//				return;
//		} else {
//			if (new FileSystem().JDK_FsWrite(LOGFILE, s.getBytes(),s.getBytes().length, 2) != s.getBytes().length) {
//				gui.cls_show_msg1(2, SERIAL, "line %d:写入文件失败...",Tools.getLineInfo());
//				if (!GlobalVariable.isContinue)
//					return;
//			}
//
//		}
//	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
		if(bluetoothAdapter!=null)
			bluetoothAdapter.disable();
	}
}

