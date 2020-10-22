package com.example.highplattest.main.constant;

import java.util.UUID;
import android.annotation.SuppressLint;
import android.newland.content.NlContext;

/************************************************************************
 * module 			: main
 * file name 		: Lib.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141113
 * directory 		: 
 * description 		: 各模块参数控制
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public interface Lib 
{	
	
	
	@SuppressLint("SdCardPath")
	//public final String LOAGFILE = GlobalVariable.sdPath+"result.txt";
	// 20M
	public final int PACKMAXLEN = 4*1024; //测试人员需求，只要4K数据 无需8K by chending20200424
	
	// 设备
	public final String DRIVER_NAME = "com.newland.me.K21Driver";
	
	// 磁卡
	public final int MAXTRACKLEN = 128;
	public final int TK1 = 0x08;// 一磁道
	public final int TK2 = 0x02;// 二磁道
	public final int TK3 = 0x04;// 只读三道
	public final int TK1_2_3 = (TK1|TK2|TK3);// 一二三磁道
	public final int TK2_3 = (TK2|TK3);// 0x06读二三磁道
	public final int TK1_3 = (TK1|TK3);// 0x0C 读一三磁道
	public final int TK1_2 = (TK1|TK2);// 0x0A 读一二磁道
	public final int STRIPE = 0x01;// 刷磁卡
	
	// IC/Sam卡
	
	// 无线
	public final int SQ_2G_MIN = 0;
	public final int SQ_2G_MAX = 31;
	public final int SQ_3G_MIN = 100;
	public final int SQ_3G_MAX = 199;
	
	// 蓝牙
	public final int BUFSIZE_BT = 1024*4;
	
	public final int DRIVER_NOT_FOUND = 1000;
	
	public final int INIT_DRIVER_FAIED = 1001;
	
	public final int CONNECT_DEVICE_FAILED = 1002;
	
	public final int GET_TRACKTEXT_FAILED = 1003;
	
	
	
	/**
	 * 休眠挂断时间2s
	 */
	public final int SLEEP_SDLC_HANGUP = 2;
	// 同步modem
	public final byte[] SDLCPCKTHEADER = {0x60,0x00,0x00,0x00,0x00};
	public final int SDLCPCKTHEADERLEN = 5;
	public final int SDLCPCKTMAXLEN = 1024;
	
	// 异步modem
	public final int ASYNRESPMAXLEN = 256;
	public final int ASYNPCKTMAXLEN = 4*1024;
	
	// 串口
	public final int BUFSIZE_SERIAL = 1024*4;
	
	public final String REPORTHEAD = "*******************测试报告\t%s" +
			"************************************************\r\n";
	
	// HandlerMsg
	public static final int KEYCODE_START = 1001;
	public static final int KEYCODE_GET = 1002;
	public static final int KEYCODE_FAILED = 1003;
	public static final int KEYCODE_SUCCESS = 1004;
	
	public static final int MAGCARD_START = 2001;
	public static final int MAGCARD_SUCCESS = 2002;
	public static final int MAGCARD_FAILED = 2003;
	public static final int MAGCARD_CANCEL = 2004;
	public static final int MAGCARD_LOOP = 2005;
	
	public static final int RFCARD_START = 3001;
	public static final int RFCARD_FOUND = 3002;
	public static final int RFCARD_NOTFOUND = 3003;
	public static final int RFCARD_END = 3004;
	public static final int RFCARD_SUCCESS = 3005;
	public static final int RFCARD_FAILED = 3006;
	
	public static final int LED_START = 4001;
	public static final int LED_SUCCESS = 4002;
	public static final int LED_FAILED = 4003;
	public static final int LED_END = 4004;

	public static final int ICCARD_START = 5001;
	public static final int ICCARD_SUCCESS = 5002;
	public static final int ICCARD_FAILED = 5003;
	
	public static final int SAM1_START = 6001;
	public static final int SAM1_SUCCESS = 6002;
	public static final int SAM1_FAILED = 6003;
	
	public static final int SAM2_START = 7001;
	public static final int SAM2_SUCCESS = 7002;
	public static final int SAM2_FAILED = 7003;
	
	public static final int FIBONACCI_START = 8001;
	public static final int FIBONACCI_END = 8002;
	
	public static final int PRINT_FAILED = 9001;
	public static final int PRINT_SUCCESS = 9002;
	public static final int PRINT_START = 9003;
	
	/** 一组测试结束 */
	public static final int LOOP_ONE_END = 1101;
	/** 所有测试结束 */
	public static final int LOOP_END = 1102;
	/** 循环烤机异常 */
	public static final int LOOP_FAILED = 1103;
	
	public static final int RTC_START = 1201;
	public static final int RTC_SUCCESS = 1202;
	public static final int RTC_FAILED = 1203;
	public static final int RTC_END = 1204;
	
	public static final int KERNEL_START = 1301;
	public static final int KERNEL_SUCCESS = 1302;
	public static final int KERNEL_FAILED = 1303;
	
	public static final int UART3_START = 1401;
	public static final int UART3_SUCCESS = 1402;
	public static final int UART3_FAILED = 1403;
	
	public static final int USB_UP_START = 1501;
	public static final int USB_UP_SUCCESS = 1502;
	public static final int USB_UP_FAILED = 1503;
	public static final int USB_LEFT_START = 1504;
	public static final int USB_LEFT_SUCCESS = 1505;
	public static final int USB_LEFT_FAILED = 1506;
	public static final int USB_START = 1507;
	public static final int USB_SUCCESS = 1508;
	public static final int USB_FAILED = 1509;
	
	public static final int TFCARD_START = 1601;
	public static final int TFCARD_SUCCESS = 1602;
	public static final int TFCARD_FAILED = 1603;
	
	public static final int SERIAL_START = 1701;
	public static final int SERIAL_SUCCESS = 1702;
	public static final int SERIAL_FAILED = 1703;
	
	public static final int MOBILE_START = 1801;
	public static final int MOBILE_SUCCESS = 1802;
	public static final int MOBILE_FAILED = 1803;
	
	public static final int WIFI_START = 1901;
	public static final int WIFI_SUCCESS = 1902;
	public static final int WIFI_FAILED = 1903;
	
	public static final int ETHERNET_START = 2001;
	public static final int ETHERNET_SUCCESS = 2002;
	public static final int ETHERNET_FAILED = 2003;
	
	public static final int LCD_START = 2101;
	public static final int LCD_SUCCESS = 2102;
	public static final int LCD_FAILED = 2103;
	
	public static final int TOUCH_START = 2201;
	public static final int TOUCH_SUCCESS = 2202;
	public static final int TOUCH_FAILED = 2203;
	
	public static final int AUDIO_START = 2301;
	public static final int AUDIO_SUCCESS = 2302;
	public static final int AUDIO_FAILED = 2303;
	
	public static final int HDMI_START = 2401;
	public static final int HDMI_SUCCESS = 2402;
	public static final int HDMI_FAILED = 2403;
	
	public static final int BLUETOOTH_START = 2501;
	public static final int BLUETOOTH_SUCCESS = 2502;
	public static final int BLUETOOTH_FAILED = 2503;
	
	public static final int CAMERA_START = 2601;
	public static final int CAMERA_SUCCESS = 2602;
	public static final int CAMERA_FAILED = 2603;
	
	public static final int MODEM_START = 2701;
	public static final int MODEM_SUCCESS = 2702;
	public static final int MODEM_FAILED = 2703;
	public static final int MODEM_PARA_FAILED = 2704;
	
	public static final int FOREGROUND_NOT_FINISHED = 2801;
	
	public static final int LOG_BUGREPORT_SAVE = 2901;
	public static final int LOG_BUGREPORT_SHOW = 2902;
	
	
	// Constant
	// 获取按键值超时时间
	public static final int TIMEOUT_KEYBOARD = 30;// 单位为s

	// 刷卡超时时间
	public static final int TIMEOUT_CARDREADER = 10 * 1000;
	
	// 刷卡超时时间, 循环测试时用
	public static final int TIMEOUT_CARDREADER_LOOP = 3 * 1000;

	// 闪烁次数
	public static final int LED_TIMES = 5;

	// 闪烁间隔
	public final int LED_INTERVAL = 500;

	// 非接卡超时时间
	public final int TIMEOUT_RFCARD = 10;

	// IC卡超时时间
	public final int TIMEOUT_ICCARD = 10;
	
	// 打印超时时间
	public final int TIMEOUT_PRINT = 30 * 1000;
	
	// socket的超时时间
	public final int TIMEOUT_SOCKET = 30*1000;
	
	// 扫码超时时间
	public final int TIMEOUT_SCAN = 15*1000;
	// 注册事件超时
	public final int TIMEOUT_REGISTER = 30*1000;
	
	// 按键类---取消
	public final byte ESC = 0x1B;
	public final byte ENTER = 0x0D; // 确认
	public final byte BACKSPACE  = 0x0a;// 退格键
	public final byte KEY_UP = 0x01;
	public final byte KEY_DOWN = 0x02;
	
	// 检测结果保存文件
	public static final String DETECT_RESULT_FILE_NAME = "DetectResult.log";
	
	public static final String APPLICATION_RESULT = "ApplicationResult.log";
	
	public static final String DETECT_RESULT_FILE_NAME_ANDROID = "DetectZhengjiResult.log";
	
	// 串口测试超时时间 
	public static final int UART_TIME_OUT = 3;
	
	// 默认时间片 
	public static final int DefaultTimePiece = 100;
	
	// 网络超时时间,单位：秒
	public static final int TIMEOUT_NET = 60;
	
	// 查看安全状态
	public static final byte GET_SECURITY_STATE = (byte)0x06;
	
	// 查看安全寄存器
	public static final byte GET_SECURITY_REGISTER = (byte)0x07;
	
	
	// 清安全寄存器
	public static final byte SET_SECURITY_REGISTER = (byte)0x06;
	
	// wifi状态
	public final int WIFI_STATE_DISABLING = 0;
	public final int WIFI_STATE_DISABLED = 1;
	public final int WIFI_STATE_ENABLING =2;
	public final int WIFI_STATE_ENABLED = 3;
	public final int WIFI_STATE_UNKNOW = 4;
	
	// 网络
	public final int SO_TIMEO = 40*1000;
	
	public final int ENABLE = 1;
	public final int DISABLE = 0;
	
	// PPP
	public final int K21_ENABLE = DISABLE;
	public final int TD_ENABLE= DISABLE;
	
	// 每个模块接口
	// 系统设置
	public final int SYSTEMCONFIG = 5;
	// 开机动画和开机声音
	public final int BOOTSET = 6;
	// 默认launcher
	public final int LAUCHER = 7;
	// 静默安装app
	public final int INSTALLAPP = 8;
	// 更新支付证书
	public final int UPDATE_PAYMENT = 9;
	// 默认输入法
	public final int DEFAULT_INPUT = 10;
	// 虚拟按键
	public final int VIRTUAL_KEY = 11;
	// 获取系统的版本号
	public final int SYSTEM_VERSION = 12;
	// 获取硬件识别码
	public final int HARD_WARE = 13;
	// android和K21通讯模
	public final int K21_ANDROID = 14;
	// 综合部分用例
	public final int SYSTEST_SIVE = 15;
	
	
	// 休眠超时时间值
	public final int ONE_MIN = 1*60*1000;
	public final int FIVE_MIN = 5*60*1000;
	
	// 手自动测试标志
	public final int RUN_BY_HAND = 0;
	public final int RUN_BY_AUTO = 1;
	
	// 蓝牙相关UUID通道
	public final UUID UUID_DATA= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public final UUID UUID_CMD = UUID.fromString("0000D5D5-0000-1000-8000-00805F9B34FB");
	public final int CHANEL_DATA = 1;
	public final int CHANEL_CMD = 2;
	
	// 是否支持dukpt密钥体系
	public final boolean DUKPT_ENABLE = true;
	// 是否支持RSA，master改为不支持RSA算法，标志位置为false modify by 20170808
	public final boolean RSA_ENABLE = false;
	
	public final byte BTN_OK = (1<<0);
	public final byte BTN_CANCEL = (1<<1);
	
	public final int APK_INSTALL = 100;
	public final int APK_UNINSTALL = 101;
	
	
	public final int SUCC=0;
	public final int FAIL = -1;
	public final int SSL_Handshake_Exception=-2;
	public final int Malformed_URL_Exception=-3;
	public final int IO_Exception=-4;
	
	public final String ANALOG_SERIAL_SERVICE  =  NlContext.ANALOG_SERIAL_SERVICE;  	/**USB串口服务*/
	public final String PINPAD_SERIAL_SERVICE = NlContext.K21_SERVICE;					/**PINPAD串口服务*/
	public final String SETTINGS_MANAGER_SERVICE = NlContext.SETTINGS_MANAGER_SERVICE;	/**设置的服务*/
	public final String RS232_SERIAL_SERVICE = NlContext.UART3_SERVICE;					/**R232串口服务*/
	public final String VIRTUAL_COM_SERVER = "virtual_com_service";						/**USB虚拟串口的服务，只支持N910_HY*/

	
	public final byte[] DATA16 = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};/**RF认证数据*/
	public final byte[] req =  new byte[] { (byte) 0x00, (byte) 0x84, (byte) 0x00,(byte) 0x00, (byte) 0x08 };/**IC/SAM/RF取随机的命令*/
	
	/**各种PPP模式*/
	public final int RESET_NO = 0x00;
	public final int RESET_PPPOPEN = 0x01;
	public final int RESET_TCPOPEN = 0x02;
	public final int RESET_TCPCLOSE = 0x03;
	public final int RESET_PPPCLOSE = 0x04;
	
	public final String[] kbName = {"退格","确认","取消"};
	
	public final byte[] kbCode = {0x0A,0x0D,0x1B};
	
	public final String packName = "com.example.highplattest";
	
	public final int BT_BUF_SIZE = 2048;
	
	public final String FONT_CAMERA = "font";/**前置摄像头*/
	public final String BACK_CAMERA = "back";/**后置摄像头*/
	public final String EXTERNAL_CAMERA = "external";/**支付扫描头*/
	public final String USB_CAMERA = "usb";/**USB摄像头*/
	
	public final int RED_COLOR = 1;
	public final int BLACK_COLOR=0;
	
}
