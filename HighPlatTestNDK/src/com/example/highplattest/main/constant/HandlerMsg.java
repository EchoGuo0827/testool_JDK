package com.example.highplattest.main.constant;

/************************************************************************
 * 
 * module 			: main
 * file name 		: HandlerMsg.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 
 * directory 		: 
 * description 		: Handler控制参数
 *history 		 	: author			date			remarks
 *			  		  zhengxq			20141028	 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class HandlerMsg 
{
	// wifi综合
//	public static final int DIALOG_SHOW_WLAN_INPUT = 1004;// wifi Ap的手动输入
//	public static final int DIALOG_SHOW_WLAN_SCAN = 1005; // wifi AP的扫码输入
	public static final int DIALOG_SHOW_NET_TRANS = 1006;
//	public static final int DIALOG_SYSTEST_WLAN_PWD = 1007;// 输入wifi密码
	
	// 默认输入法
	public static final int DEFULT_INPUT_DIALOG = 2001;
	public static final int DEFAULT_INPUT_SINGLE_DIALOG = 2002;
	
	// 静默安装app
	public static final int DIALOG_INSTALL_APP = 3001;
	public static final int DIALOG_INSTALL_APP_SINGLE = 3002;
	
	// 串口
	public static final int DIALOG_COM_SINGLE = 4001;
	public static final int DIALOG_COM = 4002;
	public static final int DIALOG_COM_BPS = 4003;
	public static final int DIALOG_COM_ADAPTER = 4004;
	public static final int DIALOG_FLUSH_UI = 4005;
	
	// 综合部分
//	public static final int DIALOG_SYSTEST_LIST = 100;
//	public static final int DIALOG_SYSTEST_PRESS = 101;
//	public static final int DIALOG_COM_SYSTEST_SINGLE = 120;
//	public static final int DIALOG_COM_SYSTEST = 121;
	public static final int DIALOG_SYSTEST_BACK = 122;
	public static final int DIALOG_SYSTEST_IMG_DRAWABLE = 123;
//	public static final int DIALOG_SYSTEST_DISMISS = 123;
//	public static final int DIALOG_SYSTEST_MAG = 124;
	public static final int DIALOG_SYSTEST_FRAME_CONTENT = 125;
//	public static final int DIALOG_SYSTEST_BT = 126;
//	public static final int DIALOG_SYSTEST_WIRE = 127;
	public static final int DIALOG_SYSTEST_BPS = 128;
	public static final int DIALOG_SYSTEST_BACK_ID = 129;
	public static final int DIALOG_SYSTEST_SND_PACKET = 130;
//	public static final int DIALOG_SYSTEST_WIFI_AP = 131;
//	public static final int DIALOG_SYSTEST_WIFI_PARA = 132;
	public static final int DIALOG_SYSTEST_SURFACE_UI = 133;
	public static final int DIALOG_SYTEST_LCD_CONFIG = 134;
//	public static final int RECT_VIEW_BLACK = 135;
//	public static final int VIEW_RESET = 136;
	public static final int DIALOG_SYSTEST_MAG_CONFIG = 135;
	
	// 界面显示部分
	public static final int TEXTVIEW_SHOW = 1;
	public static final int TEXTVIEW_SHOW_PUBLIC = 2;
	public static final int TEXTVIEW_DONGLE_CMD =3;
	public static final int TEXTVIEW_DONGLE_DATA = 4;
//	public static final int TEXTVIEW_APPEND = 0003;
	
	// modem:
	public static final int DIALOG_MODEM_CONFIG = 2001;
	public static final int DIALOG_SYSTEST_MODEM_CHOOSE = 2002;
	public static final int DIALOG_SYSTEST_MODEM_ISCONFIG = 2003;
	
	// 自动化部分
	public static final int AUTO_INTENT_OTHER = 3001;
	// 设置surfaceView可见
	public static final int SURFACEVIEW_VIEW = 4001;
	public static final int SURFACEVIEW_GONE = 4002;
	
	// 设置NLS参数配置对话框
	public static final int NLS_PARA_DIALOG = 4003;
//	public static final int SCAN_BTN_SET_TEXT = 4004;
	
	//设置验签参数
//	public static final int SIGNATURE_PARA_VIEW = 4005;
	
	
	// 设置蓝牙底座相关
	// 合法性验证
	public static final int LEGAL_AUTH_VIEW = 5001;
	public static final int DONGLE_CMD_BACK = 5002;
	public static final int DONGLE_DATA_BACK = 5003;
	//设置数据收发长度
	public static final int DIALOG_DATA_LENGTH=5004;
	
	//910蓝牙底座wifi设置
	public static final int DIALOG_DONGLE_WIFI=5005;
	
	//wifi无线性能测试单向发送接收ip和端口配置
	public static final int DIALOG_SEND_RECV_CONFIG=5006;
	
//	public static final int WIFI_MAC=5007;
	// 扫码部分
	public static final int SCAN_SURFACE_FLUSH = 6001;
	//蓝牙底座输入以太网mac地址
	public static final int DIALOG_ETH_MAC =6002;
	//推送消息配置
	public static final int DIALOG_PUSH_NOTICE=6003;
	
	public static final int TEXTVIEW_COLOR_RED = 7001;
	public static final int TEXTVIEW_COLOR_BLACK = 7002;
	
	/**
	 * 操作提示信息类别
	 * 
	 */
	public static class MessageTag {
		/**
		 * 正常消息<tt>tag</tt>
		 */
		public static final int NORMAL = 0;
		/**
		 * 错误消息<tt>tag</tt>
		 */
		public static final int ERROR = 1;
		/**
		 * 提示消息<tt>tag</tt>
		 */
		public static final int TIP = 2;
		/**
		 * 数据<tt>tag</tt>
		 */
		public static final int DATA = 3;
	}
//	// 其他部分
//	public static final int UI_TOAST_SHOW = 402;
}
