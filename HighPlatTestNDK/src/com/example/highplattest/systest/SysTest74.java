package com.example.highplattest.systest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.R.integer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.R.string;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.systest.SysTest83.CmdRun;
import com.example.highplattest.systest.SysTest83.DataRun;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newland.NlBluetooth.aidl.OnSearchListener;
import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.NlBluetooth.listener.AidlBindListener;
import com.newland.NlBluetooth.listener.OnDataReceiveListener;
import com.newland.NlBluetooth.util.LogUtil;

/************************************************************************
 * 
 * file name : SysTest74.java description : N910蓝牙底座综合测试 related document :
 * history : 变更记录 变更时间 变更人员 蓝牙底座新增配置模式2-Numeric Comparison。 20200506 魏美杰
 * BluetoothManager去掉蓝牙列表筛选 (BUG2020051103080)
 * 功能-补丁包下载与安装测试去掉中间步骤return(BUG2020051803177) 从模式连接增加过滤默认地址 (BUG2020051203091)
 * 扫描枪测试去掉portOpen和portClose，读取结果转为gbk编码 (BUG2020051303114)
 * 读写压力-单线程通讯压力修改超时时间，去掉portOpen和portClose 20200518 魏美杰
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest74 extends DefaultFragment /* implements OnDataReceiveListener */
{
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "wifi底座测试";
	private final String TAG = SysTest74.class.getSimpleName();
	private final int PACKLEN = 1024;
	private byte[] rbuf = new byte[PACKLEN]; // 接收蓝牙底座回传数据 即蓝牙底座写数据
	int ret =-1;
	/*----------global variables declaration------------------------*/
	private WifiPara wifiPara = new WifiPara();
	private EthernetPara ethernetPara = new EthernetPara();
	boolean nextsend = true;
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	// 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Config config;
	// 公共界面显示
	private Gui gui;
	// 数据通道界面显示
	private Gui dataGui;
	// 命令通道界面显示
	private Gui cmdGui;
	// usb串口
	public int DATA_kb;
	public final int BUFSIZE_SERIAL = 1024 * 16;// 串口通讯最大16k
	public final int DATA_BUFFER = 1024 * 4;// 透传的最大数据量4k
	private final int DATABIT_NUM = 4;
	private final int CHECKBIT_NUM = 3;
	private final int STOPBIT_NUM = 2;
	private final int IR_EN = 2;
	private final int BLOCK_EN = 2;
	private final int MAXWAITTIME = 30 * 1000;// 超时时间是ms
	private int DATA_TIMEOUT = 10;// 透传接收超时时间,根据波特率大小与数据量计算，4k+115200=10s
	private int COM_TIMEOUT = 15;// 串口接收超时时间,根据波特率大小与数据量计算
	int[] bps = { 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400 };
	int[] kb = { 2048, 4096 };
	private byte[] g_readBuf = new byte[DATA_BUFFER];// 全功能底座透传最大4k
	private int i = 0, len = 0;
	int flag = 1;
	byte[] l_readBuf;
	private boolean IS_DUAL = true;// 是否是双通道，旧版本为单通道，需要兼容
	private boolean IS_CUR = false; // 是否开启并发读取数据通道和命令通道数据
	private boolean wifiApOpen = false; // 判断wifiAp是否开启
	private byte[] dul_readbufCmd = new byte[DATA_kb];// 双通道并发 命令通道获取数据
	private byte[] dul_readbufdata = new byte[DATA_kb];// 双通道并发 数据通道获取数据
	private int i_dulCmd = 0, len_dulCmd = 0;
	private int i_duldata = 0, len_duldata = 0;
	int isopen; // me66 1-false:串口打开失败 2-true 串口打开成功
	int pairmode = 0;// 蓝牙底座配对模式，0-Just Works，2-Numeric Comparison
	// NlBluetooth(蓝牙底座工具类)
	private BluetoothController nlBluetooth = null;
	static Handler dataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mTvData.setText("数据通道：" + (CharSequence) msg.obj);
				break;

			default:
				break;
			}
		};
	};

	static Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:// 命令通道显示界面
				mTvCmd.setText("命令通道:" + (CharSequence) msg.obj);
				break;

			default:
				break;
			}
		};
	};
//	Gui mDataGui = new Gui(myactivity, dataHandler,HandlerMsg.TEXTVIEW_DONGLE_DATA);
//	Gui mCmdGui = new Gui(myactivity, myHandler,HandlerMsg.TEXTVIEW_DONGLE_CMD);
	boolean error = false;

	public void systest74() {
		Log.d("Thread:", Thread.currentThread().getName());
		gui = new Gui(myactivity, handler);
		dataGui = new Gui(myactivity, dataHandler);
		cmdGui = new Gui(myactivity, myHandler);
		if (GlobalVariable.gSequencePressFlag) {
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime, "%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		// 全局变量初始化操作

		nlBluetooth = BluetoothController.getInstance();

		// nlBluetooth.initService();
		// addby wangxy 20190123,开发新增服务初始化接口

		OnSearchListener onSearchListener = new OnSearchListener.Stub() {
			@Override
			public void onFinish() throws RemoteException {
			}

			@Override
			public void onDeviceFound(String arg0, String arg1) throws RemoteException {
			}

			@Override
			public void onStatusChange(int arg0, int arg1, String arg2, String arg3) throws RemoteException {
			}

			@Override
			public void onDataReceive(int arg0, byte[] data) throws RemoteException {
				// gui.cls_show_msg("发送的字节长度为" + data.length);
				LoggerUtil.e("接收数据广播,双通道=" + IS_DUAL);
				len = len + data.length;
				l_readBuf = data;
				System.arraycopy(data, 0, g_readBuf, i, data.length);
				i = i + data.length;

				if (IS_CUR) {
					if (arg0 == 0) {
						// 并发数据通道
						len_duldata = len_duldata + data.length;
						System.arraycopy(data, 0, dul_readbufdata, i_duldata, data.length);
						i_duldata = i_duldata + data.length;
						LoggerUtil.e("并发数据通道--数据通道" + "dul_readbufdata" + dul_readbufdata + "i_duldata" + i_duldata
								+ "len_duldata" + len_duldata);
					} else if (arg0 == 8) {
						// 并发命令通道
						len_dulCmd = len_dulCmd + data.length;
						System.arraycopy(data, 0, dul_readbufCmd, i_dulCmd, data.length);
						i_dulCmd = i_dulCmd + data.length;
						LoggerUtil.e("并发数据通道--命令通道" + "dul_readbufdata" + dul_readbufdata + "i_dulCmd" + i_dulCmd
								+ "len_dulCmd" + len_dulCmd);
					}

				}

			}

			@Override
			public void onOpenPortStatus(int arg0, boolean arg1) throws RemoteException {
				if (arg1) {
					isopen = 2;
				} else {
					isopen = 1;
				}

			}
		};

		nlBluetooth.init(myactivity, onSearchListener);

		bluetoothManager = BluetoothManager.getInstance(myactivity);

//		t.start();
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		config = new Config(myactivity, handler);
		GlobalVariable.isDongle = true;

//		nlBluetooth.setLog(true);
		initLayer();
//		Log.d("Hid: ",nlBluetooth.hi)
		// gui.cls_show_msg("按确认开始测试");

		while (true) {
			int nkeyIn = gui.cls_show_msg("N910蓝牙底座测试\n0.配置\n1.功能\n2.压力\n3.异常\n4.兼容性测试\n5.底座信息获取\n6.性能\n");

			switch (nkeyIn) {
			// 配置
			case '0':
				int nKeyConfig = gui.cls_show_msg(
						"配置\n0.蓝牙选择\n1.底座从模式连接\n2.POS主动断开连接\n3.设置波特率\n4.设置蓝牙底座配对模式" + "\n5.设置wifiAP开关\n6.设置蓝牙底座休眠模式");

				switch (nKeyConfig) {
				case '0':
					// nlBluetooth.disconnect();
					nlBluetooth.setLog(true);
					dongleConfig();
					break;

				case '1':
					SystemClock.sleep(2000);
					if ("".equals(getBtAddr()) || "34:87:3d:14:95:06".equals(getBtAddr())) {
						gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
						break;
					}
					// 判断连接状态是否true

					if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:连接蓝牙失败(false)",
								Tools.getLineInfo());
						break;
					}

					// 连接蓝牙,MAC地址getBtAddr()
					LoggerUtil.e("getBtAddr()=" + getBtAddr());
					LoggerUtil.e("getBtName()=" + getBtName());
					// 判断是否是双通道，走不同的代码
					if (nlBluetooth.isDualChannel())
						IS_DUAL = true;
					else
						IS_DUAL = false;
					if (!IS_DUAL) {
						BpsBean.bpsValue = 115200;
						// 设置超时时间
						COM_TIMEOUT = (BUFSIZE_SERIAL * 8 % BpsBean.bpsValue == 0
								? BUFSIZE_SERIAL * 8 / BpsBean.bpsValue
								: BUFSIZE_SERIAL * 8 / BpsBean.bpsValue + 1) + 10;
						gui.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,确认请按任意键继续");
						break;
					}

					long startTime = System.currentTimeMillis();
					while (Tools.getStopTime(startTime) < 30) {
						if (nlBluetooth.isConnectedA())
							break;
					}
					long starttime2 = System.currentTimeMillis();
					if (!nlBluetooth.isConnectedA()) {
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:获取蓝牙连接状态失败（false）",
								Tools.getLineInfo());
						break;
					}

					// 获取bps
					StringBuffer sb = new StringBuffer();
					if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:获取蓝牙波特率失败(ret=%d)",
								Tools.getLineInfo(), ret);
						break;
					}
					LoggerUtil.e("btGetTransPortA()=" + sb);
					BpsBean.bpsValue = Integer.valueOf(sb.toString());// 初始波特率
					// 设置超时时间
					DATA_TIMEOUT = (DATA_BUFFER * 8 % BpsBean.bpsValue == 0 ? DATA_BUFFER * 8 / BpsBean.bpsValue
							: DATA_BUFFER * 8 / BpsBean.bpsValue + 1) + 10;
					/*
					 * //注册透传接收数据广播 listener=new OnDataReceiveListener() {
					 * 
					 * @Override public void onDataReceive(byte[] data) { LoggerUtil.e("接收数据广播");
					 * len=len+data.length; System.arraycopy(data, 0,rbuf, i, data.length);
					 * i=i+data.length; } }; nlBluetooth.setDataListener(listener);
					 */
					float endtime = Tools.getStopTime(starttime2);
					LoggerUtil.e("endtime" + endtime);
					gui.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,确认请按任意键继续.连接时长为" + endtime + "s");

					break;

				/*
				 * case '2': // if("".equals(getBtAddr())) // { // gui.cls_show_msg1(2,
				 * "请先进行蓝牙底座选择-配置操作"); // break; // } // // 复位底座 // if
				 * (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) // { //
				 * gui.cls_show_msg1(2, SERIAL,"line %d:连接蓝牙失败（%d）", Tools.getLineInfo(),ret);
				 * //// break; // } gui.cls_show_msg("请让蓝牙底座处于等待回连状态[如:蓝牙底座下电在上电],操作完毕任意键继续");
				 * // 回连操作 nlBluetooth.startAccept();//
				 * 会线程阻塞，直到有蓝牙设备连接进来才会往下走，并关闭BluetoothServerSocket
				 * gui.cls_show_msg("蓝牙底座已回连成功,其他POS应搜索不到该底座,确认请按任意键继续"); break;
				 */

				case '2':
					nlBluetooth.disconnect();
					if (!IS_DUAL) {
						gui.cls_show_msg("蓝牙已断开,其他POS应可以搜索到该底座,确认请按任意键继续");
						break;
					}
					startTime = System.currentTimeMillis();
					while (Tools.getStopTime(startTime) < 30) {
						if (!nlBluetooth.isConnectedA())
							break;
					}
					if (nlBluetooth.isConnectedA()) {
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:获取蓝牙连接状态失败（true）",
								Tools.getLineInfo());
						break;
					}
					gui.cls_show_msg("蓝牙已断开,其他POS应可以搜索到该底座,确认请按任意键继续");
					break;

				case '3':// 设置波特率
					if (IS_DUAL)
						setBps();
					else
						RS232Config();
					break;

				case '4':
					int nKeyMode = gui.cls_show_msg("设置蓝牙底座配对模式\n0.Just Works\n1.Numeric Comparison\n");
					switch (nKeyMode) {
					case '0':
						pairmode = 0;
						break;
					case '1':
						pairmode = 2;
						break;
					}
					if ((ret = nlBluetooth.btSetBtPairMode(pairmode)) != 0) {
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:更改配对模式失败(ret=%d)",
								Tools.getLineInfo(), ret);
						return;
					}
					if ((removePair(getBtAddr())) == false)/** 设置配对模式之后要移除配对信息: */
					{
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:取消配对失败", Tools.getLineInfo());
						return;
					}
					SystemClock.sleep(2 * 1000);
					if ((nlBluetooth.isConnectedA()) == true) {
						gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:连接状态与预期不符",
								Tools.getLineInfo());
						return;
					}
					gui.cls_show_msg("蓝牙底座连接方式已更改，请重新连接配对");
					break;

				case '5':
					int nKeyMode1 = gui.cls_show_msg("设置蓝牙底座AP开关\n0.开启AP\n1.关闭AP\n2.查询当前AP状态");
					switch (nKeyMode1) {
					case '0':
						if (nlBluetooth.wifiApEnable(1) != NDK_OK) {
							gui.cls_show_msg1(2, "开启AP失败");
							return;
						}
						StringBuffer res = new StringBuffer();
						if (nlBluetooth.wifiApGetStatus(res) != NDK_OK) {
							gui.cls_show_msg1(2, "获取AP状态失败");
							return;
						}
						if (!res.toString().equals("1")) {
							wifiApOpen = true;
							gui.cls_show_msg1(2, "AP状态异常");
							return;
						}
						gui.cls_show_msg1(2, "wifiAP已开启");
						break;
					case '1':
						if (nlBluetooth.wifiApEnable(0) != NDK_OK) {
							gui.cls_show_msg1(2, "关闭AP失败");
							return;
						}
						res = new StringBuffer();
						if (nlBluetooth.wifiApGetStatus(res) != NDK_OK) {
							gui.cls_show_msg1(2, "获取AP状态失败");
							return;
						}
						if (!res.toString().equals("0")) {
							wifiApOpen = false;
							gui.cls_show_msg1(2, "AP状态异常");
							return;
						}
						gui.cls_show_msg1(2, "wifiAP已关闭");
						break;
					case '2':
						res = new StringBuffer();
						if (nlBluetooth.wifiApGetStatus(res) != NDK_OK) {
							gui.cls_show_msg1(2, "获取AP状态失败");
							return;
						}
						if (res.toString().equals("0")) {
							gui.cls_show_msg("wifiAP已经关闭，按确认退出");
							break;
						} else if (res.toString().equals("1")) {
							gui.cls_show_msg("wifiAP已经开启，按确认退出");
							break;
						} else {
							gui.cls_show_msg1(2, "AP状态错误");
						}
					case ESC:
						break;
					}
					break;

				case '6':
					wifiAPSleep();
					break;

				default:
					break;
				}
				break;

			// 功能
			case '1':
				if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
					gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,需使之处于已连接状态");
					break;
				}

				dongleFunction();
				break;

			// 压力
			case '2':
				if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
					gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,需使之处于已连接状态");
					break;
				}

				pressFun();
				break;

			// 异常
			case '3':
				if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
					gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
					break;
				}

				dongleAbnormal();
				break;

			// 兼容性
			case '4':
				if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
					gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
					break;
				}

				dongleCompatibility();
				break;

			case '5':// 信息获取

				getBtMsg();
				break;
			case '6':// 性能
//				if("".equals(nlBluetooth.getConnectedDeviceAddressA() ))
//				{
//					gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
//					break;
//				}

				int nAbilityConfig = gui
						.cls_show_msg("配置\n0.性能\n1.终端和底座连接成功率\n2.RS232和USB串口的读写压力速率(双通道)\n3.USB串口通讯流程压力测试速率(ME66)");
				switch (nAbilityConfig) {
				case '0':
					RS232Ability();
					break;
				case '1':
					ConnSuccrate();
					break;
				case '2':
					int USBorRs232Config = gui.cls_show_msg("选择\n0.usb\n1.Rs232\n2.双通道并发速率(数据)\n3.设置通讯传输数\n");
					switch (USBorRs232Config) {
					case '0':
						usbchannel();
						break;
					case '1':
						RS232Ability();
						break;
					case '2':
						dualdataConcurrent();
						break;
					case '3':
						setkb();
						break;
					}
					break;
				case '3':
					me66Processpressurerate();
					break;

				default:
					break;
				}
				break;
			case ESC:
				// 关闭蓝牙
				LoggerUtil.e("退出74号用例,disconnect断开蓝牙");
				nlBluetooth.disconnect();
				intentSys();
				return;

			}
		}
	}

	public void wifiAPSleep() {
//		StringBuffer res = new StringBuffer();
//		// int a = 0;
//		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
//			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
//			return;
//		}
//		// Log.v("ap: ", nlBluetooth.wifiApGetSleepStatus(new
//		// StringBuffer("ApStatus"))+"");
//		if ((nlBluetooth.wifiApEnable(1)) != NDK_OK) {
//			// Log.d("ApState: ", a+"");
//			gui.cls_show_msg1(2, "AP开启失败");
//			// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//			return;
//		}
//		if (nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
//			gui.cls_show_msg1(2, "获取休眠状态失败");
//			// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//			return;
//		}
//
//		if (nlBluetooth.wifiApSetSleep(1) != NDK_OK) {
//			gui.cls_show_msg1(2, "底座设置休眠失败");
//			// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//			return;
//		}
//		res = new StringBuffer();
//		if (nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
//			gui.cls_show_msg1(2, "获取休眠状态失败");
//			// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//			return;
//		}
//		// Log.d("state: ", res.toString());
//		if (!res.toString().equals("1")) {
//			gui.cls_show_msg1(2, "底座休眠状态异常");
//			// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//			return;
//		}
		while (true) {
			boolean isSleep = false;
			int key = gui.cls_show_msg("休眠开关测试\n0.开启休眠\n1.关闭休眠\n2.查询休眠状态");
			switch (key) {
			case '0':

				if (nlBluetooth.wifiApSetSleep(1) != NDK_OK) {
					gui.cls_show_msg1(2, "开启休眠失败");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}
				StringBuffer res = new StringBuffer();
				if (nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
					gui.cls_show_msg1(2, "获取休眠状态失败");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}
				// Log.d("state: ", res.toString());
				if (!res.toString().equals("1")) {
					gui.cls_show_msg1(2, "底座休眠状态异常");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}
				isSleep = true;
				gui.cls_show_msg1(4, "已开启休眠功能,休眠状态下电流与唤醒状态下差值在50-60mA");
				gui.cls_show_msg("等待30s后看到电流表值下降，按确认继续");

				nlBluetooth.wifiApSetSleep(1);

				break;
			case '1':

				if (nlBluetooth.wifiApSetSleep(0) != NDK_OK) {
					gui.cls_show_msg1(2, "关闭休眠失败");
					return;
				}
				res = new StringBuffer();
				if (nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
					gui.cls_show_msg1(2, "获取休眠状态失败(%s)", res);
					return;
				}

				if (!res.toString().equals("0")) {
					gui.cls_show_msg1(2, "底座休眠状态异常");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}
				gui.cls_show_msg1(2, "已关闭休眠功能");
				break;

			case '2':
				res = new StringBuffer();
				if (nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
					gui.cls_show_msg1(2, "获取休眠状态失败(%s)", res);

					return;
				}
				if (res.toString().equals("0")) {
					gui.cls_show_msg("底座休眠关闭，按确认退出");
					break;
				} else if (res.toString().equals("1")) {
					gui.cls_show_msg("底座休眠开启，按确认退出");
					break;
				} else {
					gui.cls_show_msg1(2, "底座休眠状态错误(%s)", res);
					return;
				}
			case ESC:
				break;
			}
			break;
		}

	}

	// me66 usb通讯流程压力速率
	private void me66Processpressurerate() {
		boolean nextsend = true;
		int porttype = 10;
		float writetime = 0;
		float readtime = 0;
		float writerate;
		float readrate;
		int cnt = 0, bak = 0, succ = 0;
//		l_readBuf=new byte[512];  //读取的backData
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 扫码数据
		String sendData1 = "<STX><0055><SET><05><00><PACK=ON><AMOUNT=0.01><MODE=ONCE><SWITCH=ON><TIMEOUT=0060><ETX><2F>";
		String ack1 = "<STX><0009><SET><00><01><00><ETX><5E>";
		String backData1 = "<STX><0032><GET><01><01><00><DATA=135772821147209347><EXT><6B>";
		// 非接数据
		String sendData2 = "<STX><0093><SET><05><00><PACK=ON><AMOUNT=0.01><SYS_TIME=20190423155600><PBOC_QPBOC=00099F0206000000000001><TIMEOUT=0060><ETX><54>";
		String ack2 = "<STX><0009><SET><00><01><00><ETX><5E>";
		String failData = "<STX><0018><SET><01><02><06><QPBOC=404><ETX><1A>";
		String succData = "<STX><0557><SET><01><02><00><QPBOC=02695A0862109468880800069F1E0830303030303030315F3401019F26088757785A9E15DF729F2701809F101307010103A00000010A010000000000A8DC622E9F37042F9C37489F360210C3950500000000009A031905159C01009F02060000000000015F2A02015682027C009F1A0201569F03060000000000009F4005FF80F0B0019F3501228408A0000003330101029F090200209F4104000000049F79060000000000009F3303E0E9C857136210946888080006D30102010000000000000F4F08A0000003330101029F21031521539F3901079B0200009F120B5549435320435245444954500B55494353204352454449549F0608A0000003330101025F2002202FDF75010FDF760400000000><ETX><20>";
//		    //线程池
//		    ExecutorService service = new ThreadPoolExecutor(1,1,60L, TimeUnit.MICROSECONDS,new LinkedBlockingDeque<Runnable>(1024),
//		            Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

		// 测试前置
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 扫码非接都用10口 返回数据
		nlBluetooth.startSingleChannelThread(porttype);

		gui.cls_show_msg1(2, "即将开始进行me66扫码流程测试-----------请将码放在机器上方");
		while (cnt > 0) {

			if (isopen == 0) {
				SystemClock.sleep(1000);
				Log.d("cdcd", "isopenisopen-------------==0");
				continue;
			} else if (isopen == 1) {
				gui.cls_show_msg1(3, "打开串口失败---请重新拔插USB口并且重新进入案例");
				return;
			}

			cnt--;
			// 发送扫码数据 发送数据为秒发。统计出的时间非常小。安卓端实际无法获取真实的发送时间，要PC端才可以
			long readstarttime = System.currentTimeMillis();
			nlBluetooth.singleSend(sendData1.getBytes());
			Log.d("打印时间点", "readstarttime" + readstarttime);
			while (nextsend) {
				if (l_readBuf != null) {

					String recData = new String(l_readBuf);
					if (recData.contains(ack1)) {
						Log.d("eric_recData", recData + "");
						break;

					}
				}
				SystemClock.sleep(10);

			}
			while (nextsend) {
				String recData2 = new String(l_readBuf);
				if (recData2.contains("DATA=")) {
					Log.d("eric_recData2", recData2 + "");
					long readendtime = System.currentTimeMillis();
					readtime = readtime + (readendtime - readstarttime);
					Log.d("打印时间点", "readtime" + readtime);
					break;
				}
			}
			succ++;
			gui.cls_show_msg1(5, "总次数为%d,成功%d次", bak, succ);
		}
		Log.d("writetime---readtime", writetime + "----" + readtime + "=========" + bak);
//			writerate=writetime/bak;
		readrate = readtime / bak;
//			Log.d("writerate---readrate", writerate+"$$$$$$$$"+readrate);
		gui.cls_show_msg1_record(TAG, "me66Processpressurerate", long_keeptime, "Me66USB流程耗时为%4fms\n其中总共测试了%d,成功了%d次",
				readrate, bak, succ);

	}

	// 双通道数据并发速率 by chending 2019 0903
	private void dualdataConcurrent() {
		// TODO Auto-generated method stub
		int cnt = 0, bak = 0, succ = 0, cnt2 = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = cnt2 = packet.getLifecycle();// 交叉次数获取
		int portType = 8; // ubs的portType为8
		if (DATA_kb == 0) {

			gui.cls_show_msg1(3, "当前传输数为%d,请先设置要传输的kb数", DATA_kb);
			return;
		}
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		IS_CUR = true; // 并发读取数据通道开启
		gui.cls_show_msg("请将蓝牙底座的与PC用RS232和USB连接。按任意键继续");
		// 打开USB串口
		if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dualdataConcurrent", g_keeptime, "line %d:打开串口%d失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), portType, ret, BpsBean.bpsValue + ",8,N,1");

//			if(!GlobalVariable.isContinue)
			return;
		}

		nlBluetooth.startSingleChannelThread(portType);

		new Thread(new CmdRun(cnt)).start();
		new Thread(new DataRun(cnt2)).start();
		// 测试后置关闭usb串口通道读取线程

//		nlBluetooth.singleCancel();
	}

	// 命令通道收发数据线程
	class CmdRun implements Runnable {
		int mTotalCnt;

		public CmdRun(int cnt) {
			mTotalCnt = cnt;

		}

		@Override
		public void run() {
			LoggerUtil.e("CmdRun线程开启------------");
			int bak, cnt, succ = 0;
			long writetime = 0;
			long readtime = 0;
			byte[] writebuf = new byte[DATA_kb];// 设置为2K或者4K
			float writeRate;
			float readRate;
			bak = cnt = mTotalCnt;
//			dul_readbufCmd= new byte[DATA_kb];// 设置为2K或者4K

			for (int i = 0; i < writebuf.length; i++) {
				writebuf[i] = 0x49;
			}
			while (cnt > 0) {
				i = 0;
				cmdGui.cls_show_msg1(1, "双通道通讯并发压力,命令通道正在进行第%d次测试(已成功%d次)", bak - cnt, succ);
				cnt--;
//				//先开启读数据线程
//				nlBluetooth.startSingleChannelThread(portType);
				// 发送数据
				long writestarttime = System.currentTimeMillis();
				nlBluetooth.singleSend(writebuf);
				long writeendtime = System.currentTimeMillis();
				writetime = writetime + (writeendtime - writestarttime);

				// 接收数据
				int time_m = 0;
				i_dulCmd = 0;
				len_dulCmd = 0;
				dul_readbufCmd = new byte[DATA_kb];
				long startreadtime = System.currentTimeMillis();
				LoggerUtil.e("startreadtime=" + startreadtime);
				long outTime = System.currentTimeMillis();
				// 读取数据等于写入数据长度才退出或者超时
				while (time_m < COM_TIMEOUT) {
					time_m = (int) Tools.getStopTime(outTime);
					SystemClock.sleep(1000);
					if (len_dulCmd == DATA_kb)
						break;
				}

				if (len_dulCmd != DATA_kb) {
					LoggerUtil.e("DATA_BUFFERCmd=" + DATA_kb);
					LoggerUtil.e("len_dulCmd=" + len_dulCmd);
					LoggerUtil.e("wbufCmd=" + ISOUtils.hexString(writebuf));
					LoggerUtil.e("dul_readbufCmd=" + ISOUtils.hexString(dul_readbufCmd));
					cmdGui.cls_show_msg1_record(TAG, "usbchannel", g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)",
							Tools.getLineInfo(), len);
					return;
				} else {

					long endreadtime = System.currentTimeMillis();
					LoggerUtil.e("endreadtime=" + endreadtime);
					readtime = readtime + (endreadtime - startreadtime);
					LoggerUtil.e("cdwbuf=" + ISOUtils.hexString(writebuf));
					LoggerUtil.e("cddata=" + ISOUtils.hexString(dul_readbufCmd));
					// 比较收发数据
					if (Tools.memcmp(writebuf, dul_readbufCmd, DATA_kb) == false) {
						LoggerUtil.e("wbufCmd=" + ISOUtils.hexString(writebuf));
						LoggerUtil.e("rbufCmd=" + ISOUtils.hexString(dul_readbufCmd));
						cmdGui.cls_show_msg1_record(TAG, "usbchannel", g_keeptime, "line %d:数据通道收发数据失败(ret = %d)",
								Tools.getLineInfo(), BT_COMPARE_FAILED);
						return;

					}

				}

				succ++;
			}
			nlBluetooth.singleCancel();
			writeRate = (float) (writebuf.length / (writetime / 1000.0 / bak));
			readRate = (float) (dul_readbufCmd.length / (readtime / 1000.0 / bak));
			Log.d("time", writetime + "  " + readtime);
			Log.d("Rate", writeRate + "  " + readRate);
			Log.d("length", writebuf.length + "  " + dul_readbufCmd.length);
			cmdGui.cls_show_msg1_record(TAG, "CmdRun", g_time_0,
					"命令通道并发压力测试完成，已执行次数为%d,成功为%d次,usb命令通道读速率为%4fB/S,写速率为%4fB/S", bak - cnt, succ, writeRate, readRate);

		}

	};

	// 数据通道收发数据线程
	class DataRun implements Runnable {
		int mTotalCnt;

		public DataRun(int cnt) {
			mTotalCnt = cnt;
		}

		@Override
		public void run() {
			LoggerUtil.e("DataRun线程开启------------");
			long startTime;
			int bak, cnt, succ = 0;
			long readtime = 0;
			long writetime = 0;
			float writeRate;
			float readRate;
			byte[] writebuf = new byte[DATA_kb];// 设置为2K或者4K
			bak = cnt = mTotalCnt;
//			dul_readbufdata= new byte[DATA_kb];// 设置为2K或者4K

			for (int i = 0; i < writebuf.length; i++) {
				writebuf[i] = 0x49;
			}
			while (cnt > 0) {
				i = 0;
				dataGui.cls_show_msg1(1, "双通道通讯并发压力,数据通道通道正在进行第%d次测试(已成功%d次)", bak - cnt, succ);
				cnt--;
				// 发送数据
				long writestarttime = System.currentTimeMillis();
				if (!nlBluetooth.sendDataA(writebuf)) {
					dataGui.cls_show_msg1_record(TAG, "DataRun", g_keeptime, "line %d:第%d次，数据透传失败（false,%s）",
							Tools.getLineInfo(), bak - cnt, ISOUtils.hexString(writebuf));
					continue;
				}
				long writeendtime = System.currentTimeMillis();
				writetime = writetime + (writeendtime - writestarttime);
				// 接收数据
				long readstarttime = System.currentTimeMillis();
				int time = 0;
				i_duldata = 0;
				len_duldata = 0;
				dul_readbufdata = new byte[DATA_kb];
				long outtime = System.currentTimeMillis();
				while (time < DATA_TIMEOUT) {
					time = (int) Tools.getStopTime(outtime);
					SystemClock.sleep(1000);
					if (len_duldata == DATA_kb)
						break;
				}
				if (len_duldata != DATA_kb) {

					LoggerUtil.e("lendata=" + len_duldata);
					LoggerUtil.e("sbufdata=" + ISOUtils.hexString(writebuf));
					LoggerUtil.e("rbufdata=" + ISOUtils.hexString(dul_readbufdata));
					dataGui.cls_show_msg1_record(TAG, "DataRun", g_keeptime, "line %d:第%d次:数据通道收发数据失败(ret = %d)",
							Tools.getLineInfo(), BT_COMPARE_FAILED);
					return;
				} else {
					long readendtime = System.currentTimeMillis();
					readtime = readtime + (readendtime - readstarttime);
					LoggerUtil.e("cdwbufdata=" + ISOUtils.hexString(writebuf));
					LoggerUtil.e("cddatadata=" + ISOUtils.hexString(dul_readbufdata));
					// 比较收发数据
					if (Tools.memcmp(writebuf, dul_readbufdata, DATA_kb) == false) {
						LoggerUtil.e("wbufdata=" + ISOUtils.hexString(writebuf));
						LoggerUtil.e("rbufdata=" + ISOUtils.hexString(dul_readbufdata));
						dataGui.cls_show_msg1_record(TAG, "DataRun", g_keeptime, "line %d:数据通道收发数据失败(ret = %d)",
								Tools.getLineInfo(), BT_COMPARE_FAILED);
						return;

					}
//					succ++;
				}
				succ++;
			}
			writeRate = (float) (writebuf.length / (writetime / 1000.0 / bak));
			readRate = (float) (dul_readbufdata.length / (readtime / 1000.0 / bak));
			Log.d("time", writetime + "  " + readtime);
			Log.d("Rate", writeRate + "  " + readRate);
			Log.d("length", writebuf.length + "  " + dul_readbufdata.length);
			dataGui.cls_show_msg1_record(TAG, "DataRun", g_time_0,
					"数据透传通道并发压力测试完成，已执行次数为%d,成功为%d次,蓝牙写速率为%4fB/S,蓝牙读速率为%4fB/S", bak - cnt, succ, readRate, writeRate);
		}

	};

	private void setkb() {
		int returnValue = gui.cls_show_msg("传输数配置\n0.2048\n1.4096");
		Log.d("chend", returnValue + "");
		int num = returnValue - 48;
		DATA_kb = kb[num];
		gui.cls_show_msg1(3, "通讯传输数配置为%d", DATA_kb);

	}

	// usb读写速率获取 by chending 20190830
	private void usbchannel() {
		byte[] writebuf = new byte[DATA_kb];
		g_readBuf = new byte[DATA_kb];
		int portType = 8; // ubs的portType为8
		float endtime = 0;
		float endRate;
		float endfailtime = 0;
		if (DATA_kb == 0) {

			gui.cls_show_msg1(3, "当前传输数为%d,请先设置要传输的kb数", DATA_kb);
		}
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 判断连接
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg1(1, "打开串口...");
		if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbchannel", g_keeptime, "line %d:打开串口%d失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), portType, ret, BpsBean.bpsValue + ",8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		// 发送0x49 2k或者4k
		for (int i = 0; i < writebuf.length; i++) {
			writebuf[i] = 0x49;

		}
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "usb读写速率获取,正在进行第%d次测试(已成功%d次),[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			// 发送数据
			i = 0;
			len = 0;
			if (succ == 0) {
				gui.cls_show_msg("请确保POS和PC已通过USB线连接,设置波特率为%d,并开启PC端的AccessPort工具,完成任意键继续", BpsBean.bpsValue);
			}
			gui.cls_printf("写数据...".getBytes());
			// 先开启读数据线程
			nlBluetooth.startSingleChannelThread(portType);
			// usb用命令通道发送数据
			long sendtime = System.currentTimeMillis();
			nlBluetooth.singleSend(writebuf);
			// 接收数据
//			gui.cls_show_msg1(2, "将从底座接收"+DATA_kb+"字节数据");
			int time = 0;
			long outTime = System.currentTimeMillis();
			// 读取数据等于写入数据长度才退出或者超时
			while (time < COM_TIMEOUT) {
				time = (int) Tools.getStopTime(outTime);
				if (len == DATA_kb) {
					break;
				}
				SystemClock.sleep(10);
			}
			endtime = endtime + (System.currentTimeMillis() - sendtime);

			if (len != DATA_kb || Tools.memcmp(writebuf, g_readBuf, DATA_kb) == false) {
				endfailtime = endfailtime + (System.currentTimeMillis() - sendtime);
				LoggerUtil.e("DATA_BUFFER=" + DATA_kb);
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("wbuf=" + ISOUtils.hexString(writebuf));
				LoggerUtil.e("data=" + ISOUtils.hexString(g_readBuf));
				gui.cls_show_msg1_record(TAG, "usbchannel", g_keeptime, "line %d:数据校验失败.接收到的长度为%d", Tools.getLineInfo(),
						len);
				continue;
			}
//			else{
//				// 比较收发数据
//				if(Tools.memcmp(writebuf, g_readBuf, DATA_kb)==false)
//				{
//					LoggerUtil.e("wbuf="+ISOUtils.hexString(writebuf));
//					LoggerUtil.e("rbuf="+ISOUtils.hexString(g_readBuf));
//					gui.cls_show_msg1_record(TAG, "usbchannel",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
//					return ;
//					
//				}
//				endtime=endtime+(System.currentTimeMillis()-sendtime);
//			}

			succ++;

		}
		// 测试后置关闭usb串口通道读取线程

		nlBluetooth.singleCancel();
		endRate = endtime / succ;
		gui.cls_show_msg1_record(TAG, "usbchannel", g_keeptime, "当前共执行了%d次，成功了%d次,数据收发的平均耗时为%4fms,失败总耗时为%4fms", bak,
				succ, endRate, endfailtime);

	}

	// 测试连接蓝牙底座的成功率以及速率 by chending 20190830
	private void ConnSuccrate() {
		// 搜索蓝牙
		int bak, count;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = count = packet.getLifecycle();// 交叉次数获取
		config.btConfig(pairList, unPairList, bluetoothManager);
		gui.cls_show_msg("请测试人员自行保证距离。点击任意键继续");
		int succ = 0;
		long startTime;
		float endTime = 0;
		float timerate;
		while (bak > 0) {
			// 从模式连接
			bak--;
			startTime = System.currentTimeMillis();
			if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
				gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:连接蓝牙失败(false)", Tools.getLineInfo());
				continue;
			}

			SystemClock.sleep(6000);
			if (!nlBluetooth.isConnectedA()) {
				gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:蓝牙未连接(false)", Tools.getLineInfo());
				continue;

			}
			endTime += (System.currentTimeMillis() - startTime - 6000) / 1000;
			// 连接蓝牙,MAC地址getBtAddr()
			LoggerUtil.e("getBtAddr()=" + getBtAddr());
			LoggerUtil.e("getBtName()=" + getBtName());

			// 断开连接
			nlBluetooth.disconnect();
			// 休眠10s。因为断开连接会阻塞。需要时间
			gui.cls_show_msg1(10, "关闭蓝牙连接操作需要10s。。。。重新开启蓝牙，连接蓝牙是耗时操作请耐心等待几分钟");
			succ++;

			Log.d("cd", bak + "bak");
			Log.d("cd", succ + "succ");

		}
		timerate = endTime / count;
		Log.d("cd", endTime + "endTime");
		Log.d("cd", timerate + "timerate");
		// 测试人员要求用确认之后才跳出界面的操作
		gui.cls_show_msg("测试连接成功率完成。总次数为%d次，其中成功%d次，平均连接用时%4f" + "s", count, succ, timerate);

	}

	private void RS232Ability() {
		byte[] writebuf = new byte[DATA_kb];
//		g_readBuf=new byte[DATA_kb];
//		float readRate;
//		float writeRate;
		float endtime = 0;
		float endrate;
		float endfailtime = 0;
//		long writetime = 0;
//		long startwritetime;
//		long startreadtime;
//		long endreadtime= 0;
//		long endwritetime= 0;
//		long readtime= 0;
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 测试前置
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}

		if (DATA_kb == 0) {

			gui.cls_show_msg1(3, "当前传输数为%d,请先设置要传输的kb数", DATA_kb);
		}
		// 发送2k或者4k的0x49
		for (int i = 0; i < writebuf.length; i++) {
			writebuf[i] = 0x49;
		}
		gui.cls_show_msg1(2, "%s性能测试中...", TESTITEM);
		while (cnt > 0) {
			i = 0;
			len = 0;
			if (gui.cls_show_msg1(3, "性能测试中,发送%d的0x49给底座...\n还剩%d次(已成功%d次)【取消】键退出测试...", DATA_kb, cnt, succ) == ESC)
				break;
			cnt--;

			long starttime = System.currentTimeMillis();
			// 发送数据给底座
			if (nlBluetooth.sendDataA(writebuf) == false) {
				gui.cls_show_msg1_record(TAG, "Rs232Ability", g_keeptime, "line %d:数据通道发送数据失败", Tools.getLineInfo());
				return;
			}
//			gui.cls_show_msg1(1, "将从底座接收"+DATA_kb+"字节数据");
			// 接收数据
			int time = 0;
//			i=0;len=0;
//			g_readBuf=new byte[DATA_kb];
			long startTime = System.currentTimeMillis();
			while (time < DATA_TIMEOUT) {
				time = (int) Tools.getStopTime(startTime);
				if (len == DATA_kb) {
					LogUtil.d("eric...len==" + len);
					break;
				}
				SystemClock.sleep(10);
			}
			endtime = endtime + (System.currentTimeMillis() - starttime);
			LoggerUtil.e("cdwbuf=" + ISOUtils.hexString(writebuf));
			LoggerUtil.e("cddata=" + ISOUtils.hexString(g_readBuf));
			if (len != DATA_kb || Tools.memcmp(writebuf, g_readBuf, DATA_kb) == false) {
				endfailtime = endfailtime + (System.currentTimeMillis() - starttime);
				LoggerUtil.e("DATA_kb=" + DATA_kb);
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("wbuf=" + ISOUtils.hexString(writebuf));
				LoggerUtil.e("data=" + ISOUtils.hexString(g_readBuf));
				gui.cls_show_msg1_record(TAG, "Rs232Ability", g_keeptime, "line %d:数据校验失败,长度为%d", Tools.getLineInfo(),
						len);
				continue;
			}
//			else{
//				// 比较收发数据
//				if(Tools.memcmp(writebuf, g_readBuf, DATA_kb)==false)
//				{
//					LoggerUtil.e("wbuf="+ISOUtils.hexString(writebuf));
//					LoggerUtil.e("rbuf="+ISOUtils.hexString(g_readBuf));
//					gui.cls_show_msg1_record(TAG, "Rs232Ability",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
//					continue ;
//					
//				}
//			}
			succ++;

		}
		endrate = endtime / succ;
		gui.cls_show_msg1_record(TAG, "Rs232Ability", long_keeptime, "总次数为%d次，其中成功%d次,数据收发平均耗时%4fms,失败总耗时%4fms", bak,
				succ, endrate, endfailtime);

	}

	private void getBtMsg() {
		int nkeyIn = gui.cls_show_msg("底座信息获取\n0.信道值\n1.底座连接状态\n");
		switch (nkeyIn) {
		case '0':
			if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
				gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
				break;
			}
			StringBuffer strChannel = new StringBuffer();
			nlBluetooth.wifiApGetChannel(strChannel);
			gui.cls_show_msg("信道值:%s", strChannel.toString());
			break;

		case '1':
			boolean isConn = nlBluetooth.isConnectedA();
			gui.cls_show_msg("底座的连接状态:%s", isConn);
			break;

		default:
			break;
		}
	}

	// 设置波特率addby wangxy20181227
	private void setBps() {
		int returnValue = gui.cls_show_msg(
				"波特率配置\n0.300\n1.1200\n2.2400\n3.4800\n4.9600\n5.19200\n6.38400\n7.57600\n8.115200\n9.230400");
		Log.d("chend", returnValue + "   bps");
		int num = returnValue - 48;
		StringBuffer sb = new StringBuffer();
		int ret = -1;
		gui.cls_show_msg1(2, "即将将波特率设置为" + bps[num]);
		if ((ret = nlBluetooth.btSetTransPort(bps[num] + "")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "setBps", g_keeptime, "line %d:设置波特率%d失败（%d）", Tools.getLineInfo(), bps[num],
					ret);
			return;
		}
		BpsBean.bpsValue = bps[num];
		if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "setBps", g_keeptime, "line %d:获取波特率失败（ret=%d）", Tools.getLineInfo(), ret);
			return;
		}
		if (!sb.toString().equals(bps[num] + "")) {
			gui.cls_show_msg1_record(TAG, "setBps", g_keeptime, "line %d:获取的波特率与设置的不一致（预期%d,实际%s）", Tools.getLineInfo(),
					bps[num], sb);
			return;
		}
		// 设置超时时间
		DATA_TIMEOUT = (DATA_BUFFER * 8 % BpsBean.bpsValue == 0 ? DATA_BUFFER * 8 / BpsBean.bpsValue
				: DATA_BUFFER * 8 / BpsBean.bpsValue + 1) + 10;
		gui.cls_show_msg1(2, "设置波特率%s成功，请将电脑串口波特率修改为对应值才可进行数据通道测试", sb);
	}

	public void dongleConfig() {
		config.btConfig(pairList, unPairList, bluetoothManager);
		if (unPairList.size() == 0) {
			gui.cls_show_msg1(2, "未搜索到蓝牙底座");
			return;
		}
	}

	/**
	 * 蓝牙底座功能测试（串口部分：王小钰 其他：张鑫锯）
	 */
	public void dongleFunction() {
		while (true) {
			gui.cls_show_msg("底座功能\n0.以太网\n1.wifiAp\n2.蓝牙\n4.USB虚拟串口\n"
					+ "5.wifiAp+以太网\n6.获取POS信息\n7.扫描枪(USB+RS232)\n8.钱箱\n9.绑定与解绑\n10.补丁包下载与安装\n"
					+ "11.波特率轮询(RS232透传方式)\n12.配对与取消配对\n13.旧底座测试项\n");

			int mKey = gui.JDK_ReadData(30, 0, "请输入测试项,默认按键值:");
//			int nkeyIn = gui.cls_show_msg("底座功能\n0.以太网\n1.wifiAp\n2.蓝牙\n3.RS232物理串口\n4.USB虚拟串口\n5.wifiAp+以太网\n6.获取POS信息\n7.扫描枪\n8.钱箱\n9.绑定与解绑");
			switch (mKey) {
			case 0:
				ETHFun();
				break;

			case 1:
				wifiAp();
				break;

			case 2:
				blueToothFun();
				break;

			case 4:// USB模拟串口
				usbAndRS232(8);
				break;

			case 5:// wifiAp+以太网，有断开蓝牙的操作
				WifiApAndEth();
				break;

			case 6:// 获取POS信息
				getPosInfo();
				break;

			case 7:// 扫码枪
				ScanFunction();
				break;

			case 8:// 钱箱USB
				MoneyBox();
				break;

			case 9:// 绑定与解绑POS设备
				bundlePos();
				break;

			case 10:// 补丁下载与安装
				if (IS_DUAL)
					patches();
				else
					gui.cls_show_msg("此版本为单通道固件,不支持补丁下载与安装,确认请按任意键继续");
				break;

			case 11:// 波特率轮询+波特率上下电保存
				if (IS_DUAL)
					allBpsTest();
				else
					gui.cls_show_msg("此版本为单通道固件，不支持波特率轮询,确认请按任意键继续");
				break;

			case 12:
				/** V1.0.8aar包新增功能--蓝牙配对后Pos自动连接底座，取消配对后，，蓝牙断开连接addbywangxy20190126 */
				PairAndRemovePair();
				break;

			case 13:
				/** 旧版本底座兼容测试点 */
				oldDongleFun();
				break;

			case ESC:
				return;
			}
		}
	}

	/** 将旧版底座的测试项目分离出来 modify by 20190215 */
	private void oldDongleFun() {
		while (true) {
			int nkeyIn = gui.cls_show_msg("旧版底座测试项\n1.RS232串口(单通道)\n");
			switch (nkeyIn) {
			case '1':// RS232物理串口
				usbAndRS232(0);
				break;

			case ESC:
				return;

			default:
				break;
			}
		}

	}

	/** V1.0.8aar包新增功能--蓝牙配对后Pos自动连接底座，取消配对后，，蓝牙断开连接addbywangxy20190126 */
	private void PairAndRemovePair() {
		boolean ret;
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg1(2, "即将取消蓝牙配对...");
		// 测试前置，清除配对信息
		if ((ret = removePair(getBtAddr())) == false) {
			gui.cls_show_msg1_record(TAG, "PairAndRemovePair", g_keeptime, "line %d:取消配对失败(ret=%s)",
					Tools.getLineInfo(), ret);
			return;
		}
		SystemClock.sleep(2 * 1000);
		if ((ret = nlBluetooth.isConnectedA()) == true) {
			gui.cls_show_msg1_record(TAG, "PairAndRemovePair", g_keeptime, "line %d:连接状态与预期不符(ret=%s)",
					Tools.getLineInfo(), ret);
			return;
		}
		if (gui.ShowMessageBox(("指示灯是否转变为500ms闪烁状态").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				GlobalVariable.WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "PairAndRemovePair", g_keeptime, "line %d:%s连接状态转变为广播状态失败",
					Tools.getLineInfo(), TESTITEM);
			return;
		}
		gui.cls_show_msg1(2, "即将进行蓝牙配对...");
		if ((ret = bluetoothManager.pair(getBtAddr(), "0")) == false) {
			gui.cls_show_msg1_record(TAG, "PairAndRemovePair", g_keeptime, "line %d:蓝牙配对失败(ret=%s)",
					Tools.getLineInfo(), ret);
			return;
		}
		SystemClock.sleep(5 * 1000);
		if ((ret = nlBluetooth.isConnectedA()) == false) {
			gui.cls_show_msg1_record(TAG, "PairAndRemovePair", g_keeptime, "line %d:连接状态与预期不符(ret=%s)",
					Tools.getLineInfo(), ret);
			return;
		}
		if (gui.ShowMessageBox(("指示灯是否转变为常亮状态").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				GlobalVariable.WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "PairAndRemovePair", g_keeptime, "line %d:%s连接状态转变为复位状态失败",
					Tools.getLineInfo(), TESTITEM);
			return;
		}
		gui.cls_show_msg("蓝牙配对后自动连接,蓝牙取消配对后断开,现象一致则通过");
	}

	// 取消蓝牙配对
	public boolean removePair(String strAddr) {
		boolean is = false;
		BluetoothDevice mydevice = bluetoothAdapter.getRemoteDevice(strAddr);
		try {
			is = ClsUtils.removeBond(mydevice.getClass(), mydevice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	// 波特率轮询addby wangxy20181227
	private void allBpsTest() {

		int ret = -1;
		int time = 0, nowBps = 0;
		byte[] sBuf = new byte[DATA_BUFFER];
		long startTime;
		StringBuffer sb = new StringBuffer();

		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// nlBluetooth.setDataListener(listener);
		for (int mbps : bps) {
			gui.cls_show_msg1(2, "即将将波特率设置为" + mbps);
			if ((ret = nlBluetooth.btSetTransPort(mbps + "")) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:设置波特率失败（%d）", Tools.getLineInfo(),
						ret);
				continue;
			}
			nowBps = mbps;// 当前bps
			sb.setLength(0);
			;
			if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:获取波特率失败（ret=%d）", Tools.getLineInfo(),
						ret);
				continue;
			}
			if (!sb.toString().equals(mbps + "")) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:获取的波特率与设置的不一致（预期%d,实际%s）",
						Tools.getLineInfo(), mbps, sb);
				continue;
			}
			gui.cls_show_msg("当前波特率为:" + sb + ",请打开AccessPort设置成对应的波特率,设置完毕并接入RS232串口线后按任意键继续");
			// 设置超时时间
			DATA_TIMEOUT = (DATA_BUFFER * 8 % BpsBean.bpsValue == 0 ? DATA_BUFFER * 8 / BpsBean.bpsValue
					: DATA_BUFFER * 8 / BpsBean.bpsValue + 1) + 10;
			// 进行数据透传
			for (int j = 0; j < sBuf.length; j++)
				sBuf[j] = (byte) (Math.random() * 256);
			// 发送
			if (!nlBluetooth.sendDataA(sBuf)) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:数据透传失败(false,bps=%s)",
						Tools.getLineInfo(), sb);
				continue;
			}
			// 接收
			time = 0;
			i = 0;
			len = 0;
			g_readBuf = new byte[DATA_BUFFER];
			startTime = System.currentTimeMillis();
			gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并发送，完成任意键继续");
			while (time < DATA_TIMEOUT) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (len == DATA_BUFFER)
					break;
			}
			if (len != DATA_BUFFER) {
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("sbuf=" + ISOUtils.hexString(sBuf));
				LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,
						"line %d:数据通道接收数据失败(ret = %d,sbuf=%s,rbuf=%s,实际len=%d)", Tools.getLineInfo(), BT_COMPARE_FAILED,
						ISOUtils.hexString(sBuf), ISOUtils.hexString(g_readBuf), len);
				continue;
			} else {
				// 比较收发数据
				if (Tools.memcmp(sBuf, g_readBuf, DATA_BUFFER) == false) {
					LoggerUtil.e("sbuf=" + ISOUtils.hexString(sBuf));
					LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
					gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,
							"line %d:数据通道收发数据失败(ret = %d,sbuf=%s,rbuf=%s)", Tools.getLineInfo(), BT_COMPARE_FAILED,
							ISOUtils.hexString(sBuf), ISOUtils.hexString(g_readBuf));
					continue;

				}
			}
			gui.cls_show_msg("请手动上下电底座（预期底座上下电后波特率不变,仍为%d），完成后按任意键继续", mbps);
			// 底座上下电后，aar包中已封装，POS会主动连接底座
			if (!nlBluetooth.isConnectedA()) {
				// 重新连接底座
				if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
					BpsBean.bpsValue = nowBps;
					gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,
							"line %d:连接蓝牙失败，此时波特率已设置为%d,后续请根据测试需要自行在配置-设置波特率中修改", Tools.getLineInfo(), nowBps);
					return;
				}
			}
			if (gui.ShowMessageBox(("确认蓝牙底座已连接，指示灯蓝灯是否常亮").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
					GlobalVariable.WAITMAXTIME) != BTN_OK) {
				BpsBean.bpsValue = nowBps;
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,
						"line %d:指示灯与预期不符，此时波特率已设置为%d,后续请根据测试需要自行在配置-设置波特率中修改", Tools.getLineInfo(), nowBps);
				return;
			}
			/*
			 * //注册透传接收数据广播,重新上下电后需重新注册 nlBluetooth.setDataListener(new
			 * OnDataReceiveListener() {
			 * 
			 * @Override public void onDataReceive(byte[] data) { LoggerUtil.e("接收数据广播2");
			 * len=len+data.length; System.arraycopy(data, 0,rbuf, i, data.length);
			 * i=i+data.length; } });
			 */
			sb.setLength(0);
			;
			if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:获取波特率失败（ret=%d）", Tools.getLineInfo(),
						ret);
				continue;
			}
			if (!sb.toString().equals(mbps + "")) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:获取的波特率与设置的不一致（预期%d,实际%s）",
						Tools.getLineInfo(), mbps, sb);
				continue;
			}
			gui.cls_show_msg("当前波特率为%s,预期与下电前设置的波特率一致:请打开AccessPort设置成对应的波特率,设置完毕并接入RS232串口线后按任意键继续", sb);
			if (!nlBluetooth.sendDataA(sBuf)) {
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:上下电后进行数据透传失败（false,%s）",
						Tools.getLineInfo(), ISOUtils.hexString(sBuf));
				continue;
			}
			// 接收
			time = 0;
			i = 0;
			len = 0;
			g_readBuf = new byte[DATA_BUFFER];
			startTime = System.currentTimeMillis();
			gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并发送，完成任意键继续");
			while (time < DATA_TIMEOUT) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (len == DATA_BUFFER)
					break;
			}
			if (len != DATA_BUFFER) {
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("sbuf=" + ISOUtils.hexString(sBuf));
				LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
				gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,
						"line %d:数据通道接收数据失败(ret = %d,sbuf=%s,rbuf=%s,实际len=%d)", Tools.getLineInfo(), BT_COMPARE_FAILED,
						ISOUtils.hexString(sBuf), ISOUtils.hexString(g_readBuf), len);
				continue;
			} else {
				// 比较收发数据
				if (Tools.memcmp(sBuf, g_readBuf, DATA_BUFFER) == false) {
					LoggerUtil.e("sbuf=" + ISOUtils.hexString(sBuf));
					LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
					gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,
							"line %d:数据通道收发数据失败(ret = %d,sbuf=%s,rbuf=%s)", Tools.getLineInfo(), BT_COMPARE_FAILED,
							ISOUtils.hexString(sBuf), ISOUtils.hexString(g_readBuf));
					continue;

				}
			}
		}
		// 测试后置，恢复波特率BpsBean.bpsValue
		gui.cls_show_msg1(2, "测试后置，将波特率恢复为之前设置的%d", BpsBean.bpsValue);
		if ((ret = nlBluetooth.btSetTransPort(BpsBean.bpsValue + "")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:测试后置，设置波特率失败（%d）", Tools.getLineInfo(),
					ret);
			return;
		}
		sb.setLength(0);
		;
		if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:测试后置，获取波特率失败（ret=%d）", Tools.getLineInfo(),
					ret);
			return;
		}
		if (!sb.toString().equals(BpsBean.bpsValue + "")) {
			gui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:测试后置，获取的波特率与设置的不一致（预期%d,实际%s）",
					Tools.getLineInfo(), BpsBean.bpsValue, sb);
			return;
		}
		// 设置超时时间
		DATA_TIMEOUT = (DATA_BUFFER * 8 % BpsBean.bpsValue == 0 ? DATA_BUFFER * 8 / BpsBean.bpsValue
				: DATA_BUFFER * 8 / BpsBean.bpsValue + 1) + 10;

		gui.cls_show_msg("当前波特率恢复为%s，波特率轮询通讯与波特率修改后上下电不变测试通过，按任意键继续", sb);
	}

	// 补丁包的下载与安装addby wangxy20181227
	private void patches() {
		int ret = -1;
		// String[] typeList =
		// {"机器类型","支持的硬件类型","BIOS版本信息","机器序列号","机器机器号","主板号","刷卡总数","打印总长度",
		// "开机运行时间","按键次数","CPU类型","BOOT版本","BIOS版本补丁号","公钥版本信息","固件版本时间","补丁版本时间"};
		StringBuffer outInfo = new StringBuffer();
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 测试前置，补丁号为0
		if ((ret = nlBluetooth.sysGetPosInfo(12, outInfo)) != NDK_OK)// 补丁号
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取pos信息测试(ret=%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请确认获取的补丁号：%s，预期测试前补丁号应该为0，否则请重新刷底座固件\n[确认]正确，[其他]错误", outInfo) != ENTER) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:测试前置获取补丁号非0，请重新刷底座固件再进行本项测试(%s)",
					Tools.getLineInfo(), outInfo);
			return;
		}
		// case3：错误补丁包下载成功但安装应失败
		gui.cls_show_msg("请在POS端的mnt/sdcard目录下放置补丁包文件(错误补丁),且将补丁包改名为error_patch.NLD，确认按任意键继续");
		if ((ret = nlBluetooth.btDownloadPack("mnt/sdcard/error_patch.NLD")) != NDK_OK1) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:补丁包下载失败(%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1(2, "即将安装补丁包(错误补丁，预期安装失败)...");
		if ((ret = nlBluetooth.btInstallPact(0)) != -821) // 补丁包版本校验失败|修改检验失败的返回值为-821 add by 20190610
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:错误补丁包预期安装失败(%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		outInfo.setLength(0);
		if ((ret = nlBluetooth.sysGetPosInfo(12, outInfo)) != NDK_OK)// 补丁号
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取pos信息测试(ret=%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请确认获取的补丁号：%s，预期安装错误补丁包失败，补丁号不变预期为0\n[确认]正确，[其他]错误", outInfo) != ENTER) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:错误补丁安装失败，补丁号应不变为0(%s)", Tools.getLineInfo(),
					outInfo);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case1：需底座重启才可生效的补丁包
		gui.cls_show_msg("请在POS端的mnt/sdcard目录下放置补丁包文件(补丁号为1),且将补丁包改名为patch1.NLD，确认按任意键继续");
		// 下载补丁包
		if ((ret = nlBluetooth.btDownloadPack("mnt/sdcard/patch1.NLD")) != NDK_OK1) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:补丁包下载失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1(2, "即将安装补丁包(补丁号为1,底座重启)...");
		// 安装补丁包,0-不重启，1重启
//		int reboot=1;
		if ((ret = nlBluetooth.btInstallPact(1)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:补丁包安装失败(%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.ShowMessageBox(("补丁包安装结束后,底座是否重启,预期指示灯经历灭一段时间后变成已连接状态(需等待一段时间观察现象)").getBytes(),
				(byte) (BTN_OK | BTN_CANCEL), 0) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:安装补丁包重启，指示灯亮灭时间与预期不符", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// POS重新连接底座
		/*
		 * if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) {
		 * gui.cls_show_msg1_record(TAG, "patches",g_keeptime, "line %d:连接蓝牙失败（false）",
		 * Tools.getLineInfo()); return; }
		 */
		/*
		 * if(!nlBluetooth.isConnectedA()) { gui.cls_show_msg1_record(TAG,
		 * "patches",g_keeptime, "line %d:获取蓝牙连接状态失败（false）", Tools.getLineInfo());
		 * return; }
		 */
		// 获取补丁号和补丁时间
		outInfo.setLength(0);
		;
		if ((ret = nlBluetooth.sysGetPosInfo(12, outInfo)) != NDK_OK)// 补丁号
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取pos信息测试(ret=%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请确认获取的补丁号:%s\n[确认]正确,[其他]错误", outInfo) != ENTER) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取补丁号测试失败(%s)", Tools.getLineInfo(), outInfo);
			if (GlobalVariable.isContinue == false)
				return;
		}
		outInfo.setLength(0);
		;
		if ((ret = nlBluetooth.sysGetPosInfo(15, outInfo)) != NDK_OK)// 补丁时间
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取pos信息测试(ret=%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请确认获取的补丁时间：%s\n[确认]正确，[其他]错误", outInfo) != ENTER) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取补丁时间号测试失败(%s)", Tools.getLineInfo(),
					outInfo);
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2：无需底座重启即可生效的补丁包
		gui.cls_show_msg("请在POS端的mnt/sdcard目录下放置补丁包文件(补丁号为2),且将补丁包改名为patch2.NLD,确认按任意键继续");
		// 下载补丁包
		if ((ret = nlBluetooth.btDownloadPack("mnt/sdcard/patch2.NLD")) != NDK_OK1) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:补丁包下载失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1(2, "即将安装补丁包(补丁号为2,底座不重启)...");
		// 安装补丁包,0-不重启，1重启
//		reboot=0;
		if ((ret = nlBluetooth.btInstallPact(0)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:补丁包安装失败(%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.ShowMessageBox(("底座指示灯是否一直常亮？？").getBytes(), (byte) (BTN_OK | BTN_CANCEL), 0) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:安装补丁包不重启,指示灯亮灭时间与预期不符", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 获取补丁号和补丁时间
		outInfo.setLength(0);
		;
		if ((ret = nlBluetooth.sysGetPosInfo(12, outInfo)) != NDK_OK) // 补丁号
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取pos信息测试(ret=%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请确认获取的补丁号：%s\n[确认]正确，[其他]错误", outInfo) != ENTER) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取补丁号测试失败(%s)", Tools.getLineInfo(), outInfo);
			if (GlobalVariable.isContinue == false)
				return;
		}
		outInfo.setLength(0);
		;
		if ((ret = nlBluetooth.sysGetPosInfo(15, outInfo)) != NDK_OK) // 补丁时间
		{
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取pos信息测试(ret=%d)", Tools.getLineInfo(), ret);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请确认获取的补丁时间：%s\n[确认]正确，[其他]错误", outInfo) != ENTER) {
			gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "line %d:获取补丁时间号测试失败(%s)", Tools.getLineInfo(),
					outInfo);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(TAG, "patches", g_keeptime, "现象与提示一致，则测试通过");
	}

	// 解绑或绑定设备addbywangxy
	private void bundlePos() {
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		while (true) {
			int nkeyIn = gui.cls_show_msg("底座绑定与解绑\n0.绑定\n1.解绑");
			switch (nkeyIn) {
			case '0':
				bundle();
				break;

			case '1':
				UnBundle();
				break;

			case ESC:
				return;
			}
		}

	}

	// 解绑设备
	private void UnBundle() {
		StringBuffer bound = new StringBuffer();
		int ret = -1;
		if ((ret = nlBluetooth.btGetBoundState(bound)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "UnBundle", g_keeptime, "line %d:获取绑定状态失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("当前状态为：" + bound.toString() + "，请确保当前设备为已绑定状态，[确认]正确，[其他]错误") != ENTER) {
			gui.cls_show_msg1(2, "请先进行设备绑定操作。。。");
			return;
		}

		gui.cls_show_msg1(2, "即将解绑当前连接的设备。。。");
		if ((ret = nlBluetooth.btSetBoundState(0)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "UnBundle", g_keeptime, "line %d:设置绑定状态0失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		bound.setLength(0);
		;
		if ((ret = nlBluetooth.btGetBoundState(bound)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "UnBundle", g_keeptime, "line %d:获取绑定状态失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("获取当前绑定状态为：" + bound.toString() + "，预期设备解绑成功，指示灯仍为常亮，[确认]正确，[其他]错误") != ENTER) {
			gui.cls_show_msg1_record(TAG, "UnBundle", g_keeptime, "line %d:解绑设备失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(TAG, "UnBundle", g_keeptime, "解绑现象与提示一致，则测试通过，后续保证各功能和压力应能正常使用，且其他任意POS设备可成功接入该底座");
	}

	// 绑定设备
	private void bundle() {
		StringBuffer bound = new StringBuffer();
		int ret = -1;
		if ((ret = nlBluetooth.btGetBoundState(bound)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:获取绑定状态失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		if (gui.cls_show_msg("当前状态为：" + bound.toString() + "，请确保当前设备为未绑定状态，[确认]正确，[其他]错误") != ENTER) {
			gui.cls_show_msg1(2, "请先进行设备解绑操作。。。");
			return;
		}
		gui.cls_show_msg1(2, "即将绑定当前连接的设备。。。");
		if ((ret = nlBluetooth.btSetBoundState(1)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:设置绑定状态1失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		bound.setLength(0);
		;
		if ((ret = nlBluetooth.btGetBoundState(bound)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:获取绑定状态失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("获取当前绑定状态为：" + bound.toString() + "，预期设备绑定成功,指示灯仍为常亮，[确认]正确，[其他]错误") != ENTER) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:绑定设备失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// 已绑定，断开连接后，其他设备无法接入
		if (IS_DUAL) {
			gui.cls_show_msg("全功能底座中POS会主动连接底座，请在设置-蓝牙中取消配对，底座重新上下电来确保蓝牙连接断开，请准备另一台POS设备，预期其他POS无法接入已绑定的底座，完成任意键继续");

		} else {
			gui.cls_show_msg("绑定设备即将主动断开蓝牙连接，请准备另一台POS设备，预期其他POS无法接入已绑定的底座，完成任意键继续");
			gui.cls_show_msg1(2, "即将主动断开蓝牙连接...");
			nlBluetooth.disconnect();
		}
		if (gui.cls_show_msg("使用另一台POS接入该底座，预期蓝牙连接失败，指示灯仍为绑定状态等，[确认]正确，[其他]错误") != ENTER) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:已绑定的底座，其他POS预期无法接入", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1(2, "即将进行蓝牙连接...");
		if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:连接蓝牙失败（false）", Tools.getLineInfo());
			return;
		}
		if (gui.cls_show_msg("使用绑定的POS接入该底座，已绑定的底座只能成功接入绑定的POS设备，预期蓝牙连接成功，指示灯变为常亮，[确认]正确，[其他]错误") != ENTER) {
			gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "line %d:已绑定的底座，绑定的POS预期可成功接入", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(TAG, "bundle", g_keeptime, "绑定现象与提示一致，则测试通过，后续各功能和压力应能正常使用");
	}

	// 钱箱
	private void MoneyBox() {
		int ret = -1;
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// case1:钱箱处于关闭状态时，打开钱箱应成功
		gui.cls_show_msg("请确保钱箱已接入底座USB主模式串口且钱箱处于关闭状态，完成任意键继续");
		if ((ret = nlBluetooth.openBoxA()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MoneyBox", g_keeptime, "line %d:钱箱打开失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		} else {
			if (gui.cls_show_msg("请确认钱箱已处于开启状态：[确认]正确，[其他]错误") != ENTER) {
				gui.cls_show_msg1_record(TAG, "MoneyBox", g_keeptime, "line %d:开启钱箱失败", Tools.getLineInfo());
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		// case2:钱箱处于已开启状态时，打开钱箱应成功
		gui.cls_show_msg("请确保钱箱已接入底座USB主模式串口且钱箱处于已开启状态，完成任意键继续");
		if ((ret = nlBluetooth.openBoxA()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MoneyBox", g_keeptime, "line %d:钱箱打开失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		} else {
			if (gui.cls_show_msg("请确认钱箱已处于开启状态：[确认]正确，[其他]错误") != ENTER) {
				gui.cls_show_msg1_record(TAG, "MoneyBox", g_keeptime, "line %d:开启钱箱失败", Tools.getLineInfo());
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		// case3:钱箱处于未接入状态时，打开钱箱应失败
		gui.cls_show_msg("请确保钱箱未接入底座USB主模式串口，完成任意键继续");
		if ((ret = nlBluetooth.openBoxA()) == NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MoneyBox", g_keeptime, "line %d:钱箱打开预期失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(TAG, "blueToothFun", g_keeptime, "钱箱现象与提示一致，则测试通过");
	}

	/**
	 * 扫描枪配置 王小钰
	 */
	public void ScanFunction() {
		while (true) {
			int nkeyIn = gui.cls_show_msg("扫描枪配置\n0.USB串口组合\n1.Com串口\n2.异常测试");
			switch (nkeyIn) {
			case '0':
				MyScan(10);
				break;

			case '1':
				MyScan(0);
				break;

			case '2':
				MyScanAbnormal();
				break;
			case ESC:
				return;
			}
		}
	}

	/**
	 * 扫码异常，USB口与Com口同时接上 王小钰
	 */
	public void MyScanAbnormal() {
		/* private & local definition */
		int ret;
		StringBuffer outReceiveLen1 = new StringBuffer();
		StringBuffer outReceiveLen2 = new StringBuffer();
		StringBuffer outReceiveLen3 = new StringBuffer();
		StringBuffer outReceiveLen4 = new StringBuffer();
		byte[] rbuf1 = new byte[BUFSIZE_SERIAL];
		byte[] rbuf2 = new byte[BUFSIZE_SERIAL];

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg("扫描枪异常测试，请同时接入COM和USB线的两把扫描枪，完成任意键继续");
		if ((ret = nlBluetooth.portOpen(10, "115200,8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:打开扫描枪USB串口失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), ret, "115200,8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlBluetooth.portOpen(0, "115200,8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:打开扫描枪RS232串口失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), ret, "115200,8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		// 请扫码
		gui.cls_printf("请使用接入USB线的扫描枪扫码...".getBytes());
		while (true) {
			// 取缓冲区数据大小
			outReceiveLen1.setLength(0);
			;
			if ((ret = nlBluetooth.portReadLen(10, outReceiveLen1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:扫描枪USB串口取缓存区数据失败(%d)",
						Tools.getLineInfo(), ret);
				continue;
			}
			// 有数据，读出来
			if (Integer.valueOf(outReceiveLen1.toString()) != 0)
				break;

		}
		gui.cls_printf("请使用RS232口的扫描枪扫码...".getBytes());
		while (true) {
			// 取缓冲区数据大小
			outReceiveLen2.setLength(0);
			;
			if ((ret = nlBluetooth.portReadLen(0, outReceiveLen2)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:扫描枪RS232串口取缓存区数据失败(%d)",
						Tools.getLineInfo(), ret);
				continue;
			}
			// 有数据，读出来
			if (Integer.valueOf(outReceiveLen2.toString()) != 0)
				break;

		}
		// 有数据
		// USB
		Arrays.fill(rbuf1, (byte) 0);
		if ((ret = nlBluetooth.portRead(10, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen3, rbuf1)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:USB串口读数据失败(%d)", Tools.getLineInfo(),
					ret);
		}
		// 比较所取得的输入缓冲的数据长度
		if (!((outReceiveLen3.toString()).equals(outReceiveLen1.toString()))) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:USB串口取缓存区数据与预期大小不符合",
					Tools.getLineInfo());
		}
		// COM
		Arrays.fill(rbuf2, (byte) 0);
		if ((ret = nlBluetooth.portRead(0, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen4, rbuf2)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:USB串口读数据失败（%d）", Tools.getLineInfo(),
					ret);
		}
		// 比较所取得的输入缓冲的数据长度
		if (!((outReceiveLen4.toString()).equals(outReceiveLen2.toString()))) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:USB串口取缓存区数据与预期大小不符合",
					Tools.getLineInfo());
		}
		// 弹框显示读到的rbuf的数据
		String res1 = new String(rbuf1);
		String res2 = new String(rbuf2);
		if (gui.ShowMessageBox(("接USB线的扫描枪的码值为：" + res1 + "，与条形码或二维码是否一致？").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				GlobalVariable.WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:%s扫描错误,扫到码值为(%s)", Tools.getLineInfo(),
					TESTITEM, res1);
			return;
		}
		if (gui.ShowMessageBox(("接RS232线的扫描枪的码值为：" + res2 + "，与条形码或二维码是否一致？").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				GlobalVariable.WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:%s扫描错误,扫到码值为(%s)", Tools.getLineInfo(),
					TESTITEM, res2);
			return;
		}
		// 清缓冲
		gui.cls_printf("串口请缓存...".getBytes());
		if ((ret = nlBluetooth.portClrBuf(10)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:USB串口清缓存失败(%d)", Tools.getLineInfo(),
					ret);
		}
		if ((ret = nlBluetooth.portClrBuf(0)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_keeptime, "line %d:USB串口清缓存失败(%d)", Tools.getLineInfo(),
					ret);
		}
		gui.cls_show_msg1_record(TAG, "MyScanAbnormal", g_time_0, "扫描枪异常测试通过");
	}

	/**
	 * 扫描枪扫码(王小钰)
	 * 
	 * @param portType 串口类型
	 */
	public void MyScan(int portType) {
		/* private & local definition */
		int ret = -1;
//		l_readBuf=new byte[];
		String funcName = "usbFunction";
		StringBuffer outReceiveLen2 = new StringBuffer();
		StringBuffer outReceiveLen1 = new StringBuffer();
		StringBuffer outLen = new StringBuffer();
		int readLen = 0;
		byte[] recvBuf;
		int BUFSIZE_SERIAL2 = BUFSIZE_SERIAL;
		l_readBuf = new byte[BUFSIZE_SERIAL];
		if (portType == 10) { // USB扫描枪只有4k

			BUFSIZE_SERIAL2 = 4 * 1024;
		}
		byte[] rbuf = new byte[BUFSIZE_SERIAL];
		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		/** 扫描枪扫码不需要打开串口 20200519 魏美杰 */
//		gui.cls_printf("扫描枪配置初始化...".getBytes());
//		if ((ret = nlBluetooth.portOpen(portType, "115200,8,N,1")) != NDK_OK) 
//		{
//			gui.cls_show_msg1_record(TAG,"MyScan",g_keeptime,"line %d:打开串口%d失败(%d),串口配置为(%s)", Tools.getLineInfo(),portType,ret, "115200,8,N,1");
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		int i = 1;
		int succ = 1;
		long startTime = 0l;
		int time = 0;
		// 缓冲区的数据不为0，则说明扫描枪有扫到码的数据
		while (true) {
			if (gui.cls_show_msg1(2, "请直接使用蓝牙底座USB接口接上扫码枪，使用扫描枪扫码，第%d次测试，[取消]键退出测试...", i) == ESC) {
				// 关闭串口
//				gui.cls_printf("关闭串口...".getBytes());
//				if ((ret = nlBluetooth.portClose(portType)) != NDK_OK) 
//				{
//					gui.cls_show_msg1_record(TAG,"MyScan",g_keeptime,"line %d:串口%d关闭失败(%d)", Tools.getLineInfo(), portType,ret);
//					if (!GlobalVariable.isContinue)
//						return;
//				}
				gui.cls_show_msg1_record(TAG, "MyScan", g_keeptime, "扫描枪+USB口组合测试完成，已执行次数为%d，成功为%d次", i, succ);
				break;
			}
			i++;
			// 开启读线程
			// nlBluetooth.startSingleChannelThread(portType);
			StringBuffer info = new StringBuffer();
//			ret = nlBluetooth.usbGetInfo(1, info);
			// 直接使用蓝牙底座自带的USB串口连接扫码枪

			nlBluetooth.usbClosePort(1, 0);
			nlBluetooth.usbCloseHid(1, 0);
			if ((ret = nlBluetooth.usbOpenPort(1, 0)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:初始化串口失败（port=%d,ret=%d）",
						Tools.getLineInfo(), 1, ret);
				LoggerUtil.e("usbOpenPort=" + 0 + "," + ret);
				continue;
			}

//			outReceiveLen1.setLength(0);
			// 请扫码
			gui.cls_show_msg("扫码后按确认...");
			time = 0;
			// 循环等待1分钟
			startTime = System.currentTimeMillis();
			while (time < 30) {
				time = (int) Tools.getStopTime(startTime);
				// 休眠2s
				SystemClock.sleep(1000);
				// 取缓冲区数据大小
//				outReceiveLen2.setLength(0);
//				if ((ret = nlBluetooth.portReadLen(portType, outReceiveLen2)) != NDK_OK) 
//				{	
//					LoggerUtil.d("outReceiveLen2==="+outReceiveLen2.toString());
//					LoggerUtil.d("rbuf==="+ISOUtils.hexString(rbuf));
//					gui.cls_show_msg1_record(TAG,"MyScan",g_keeptime,"line %d:串口%d取缓存区数据失败(%d)", Tools.getLineInfo(),portType,ret);
//					continue;
//				}
//				// 有数据，读出来
//				if (Integer.valueOf(outReceiveLen2.toString()) > 0)
//					break;
				outLen = new StringBuffer();
				if ((ret = nlBluetooth.usbPortReadLen(1, 0, outLen)) != 0) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取输入缓存长度状态错误(ret=%d)",
							Tools.getLineInfo(), ret);
					return;
				}
				readLen = Integer.parseInt(outLen.toString());
				if (readLen > 0) {
					Log.e("readLen", readLen + "");
					break;
				}
//				if (l_readBuf.length > 0) {
//					LoggerUtil.d("l_readBuf===" + ISOUtils.hexString(l_readBuf));
//					break;
//
//				}

			}
			recvBuf = new byte[readLen];
			if ((ret = nlBluetooth.usbPortRead(1, 0, readLen, 10 * 1000, outLen, recvBuf)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:读数据状态错误(ret=%d)", Tools.getLineInfo(),
						ret);
				return;
			}
//			LoggerUtil.d("rbuffill=="+ISOUtils.hexString(rbuf));
//			if ((ret = nlBluetooth.portRead(portType, BUFSIZE_SERIAL2,MAXWAITTIME, outReceiveLen1, rbuf)) != NDK_OK) 
//			{	
//				LoggerUtil.d("outReceiveLen1==="+outReceiveLen1.toString());
//				LoggerUtil.d("rbuf==="+ISOUtils.hexString(rbuf));
//				gui.cls_show_msg1_record(TAG,"MyScan", g_keeptime, "line %d:串口%d读数据失败(%d)",Tools.getLineInfo(),portType,ret);
//				continue;
//			}
//			// 比较所取得的输入缓冲的数据长度
//			if (!((outReceiveLen2.toString()).equals(outReceiveLen1.toString()))) 
//				
//			{	LoggerUtil.d("outReceiveLen2==="+outReceiveLen2.toString());
//			LoggerUtil.d("outReceiveLen1==="+outReceiveLen1.toString());
//				gui.cls_show_msg1_record(TAG,"MyScan",g_keeptime,"line %d:串口%d取缓存区数据与预期大小不符合,预期%s,实际%s", Tools.getLineInfo(),portType,outReceiveLen2.toString(),outReceiveLen1.toString());
//				continue;
//			}
			// 弹框显示读到的rbuf的数据
			SystemClock.sleep(2000);
			String result = new String(recvBuf);
			Log.e("result", result + "");
			if (gui.cls_show_msg("扫码结果=%s,测试人员确认是否正确", result) != ENTER) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:读数据结果错误(%s)", Tools.getLineInfo(), result);
				return;
			}
//			String res = new String(l_readBuf);
//			try {
//				res = new String(l_readBuf, "gbk");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			LoggerUtil.d("res===" + res);
//			if (gui.ShowMessageBox((res.trim() + "与条形码是否一致？").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
//					GlobalVariable.WAITMAXTIME) != BTN_OK) {
//				gui.cls_show_msg1_record(TAG, "MyScan", g_keeptime, "line %d:%s扫描错误,扫到码值为(%s)", Tools.getLineInfo(),
//						TESTITEM, res);
//				return;
//			}
			gui.cls_printf("串口请缓存...".getBytes());
			if ((ret = nlBluetooth.usbPortClrBuf(1, 0)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:清串口缓存状态错误(ret=%d)", Tools.getLineInfo(),
						ret);
				Log.e("usbClosePort" + 0, "" + ret);
			}
//			if ((ret = nlBluetooth.portClrBuf(portType)) != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "MyScan",g_keeptime, "line %d:串口%d清缓存失败(%d)",Tools.getLineInfo(),portType,ret);
//				continue;
//			}
			// 清空 res
//			res="";
//			l_readBuf = new byte[BUFSIZE_SERIAL];
			succ++;
		}
		// 关闭串口
		if ((ret = nlBluetooth.usbClosePort(1, 0)) != 0) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:关闭串口状态错误(ret=%d)", Tools.getLineInfo(), ret);
			Log.e("usbClosePort" + 1, "" + ret);
		}
		// 测试后置关闭读写线程
		// nlBluetooth.singleCancel();
	}

	/**
	 * 蓝牙功能 zhangxinj
	 */
	private void blueToothFun() {
		String btName;
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		btName = nlBluetooth.getConnectedDeviceAddressA();
		gui.cls_show_msg1_record(TAG, "blueToothFun", 0, "获取的蓝牙地址为%s，获取正确则通过", btName);

	}

	/**
	 * 获取pos信息 xuess
	 */
	public void getPosInfo() {
		/* private & local definition */
		int ret = -1;
		String[] typeList = { "机器类型", "支持的硬件类型", "BIOS版本信息", "机器序列号", "机器机器号", "主板号", "刷卡总数", "打印总长度", "开机运行时间", "按键次数",
				"CPU类型", "BOOT版本", "BIOS版本补丁号", "公钥版本信息", "固件版本时间", "补丁版本时间" };
//		int[] getType = {0,2,3,4,5,10,11,12,13,14,15};//与潘浩确认，其余为预留信息，底座暂无相关硬件支持，先不测试
		StringBuffer outInfo = new StringBuffer();

		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		/* process body */
		int len = typeList.length;
		// 单通道则去掉最后两个
		if (!IS_DUAL)
			len = len - 2;
		for (int i = 0; i < len; i++) {
			outInfo.setLength(0);
			/** 不支持获取按键次数，硬件识别码为乱码也是正常 modify by zhengxq 20190214 */
			if ((ret = nlBluetooth.sysGetPosInfo(i, outInfo)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "getPosInfo", g_keeptime, "line %d:获取pos信息测试,获取%s出错(i=%d,ret=%d)",
						Tools.getLineInfo(), typeList[i], i, ret);
				continue;
			}
			// 将硬件信息转为16进制输出
			if (i == 1) {
				StringBuffer mes = new StringBuffer();
				try {
					byte[] res1 = outInfo.toString().getBytes("GBK");
					for (int j = 0; j < res1.length; j++) {
						String tempStr = Integer.toHexString(res1[j]);
						if (tempStr.length() < 2)
							tempStr = "0" + tempStr;
						mes.append(tempStr);
					}
					outInfo = mes;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if (gui.cls_show_msg("请确认获取的%s：%s\n[确认]正确，[其他]错误", typeList[i], outInfo) != ENTER) {
				gui.cls_show_msg1_record(TAG, "getPosInfo", g_keeptime, "line %d:获取pos信息测试失败,获取%s不一致(outInfo=%s)",
						Tools.getLineInfo(), typeList[i], outInfo);
				if (GlobalVariable.isContinue == false)
					return;
			}

		}
		gui.cls_show_msg("获取pos信息测试结束,任意键退出");
	}

	/**
	 * 以太网的功能测试
	 */
	private void ETHFun() {
		/* private & local definition */
		int ret = -1;
		int tcp;
		byte[] sendByte = { 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38 };
		byte[] recByte = new byte[10];
		StringBuffer sendLen = new StringBuffer();
		StringBuffer recLen = new StringBuffer();
		StringBuffer tcpHandle = new StringBuffer();
		StringBuffer ipAddr = new StringBuffer();
		StringBuffer gateway = new StringBuffer();
		StringBuffer subnetMask = new StringBuffer();
		StringBuffer dns = new StringBuffer();
		final StringBuffer macAddr = new StringBuffer();
		StringBuffer baiduIpAddr = new StringBuffer();
		StringBuffer isConnected = new StringBuffer();

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg("请确未连接以太网，完成任意键继续");
		if ((ret = nlBluetooth.ethGetConnected(isConnected)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取网线是否连接失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (!isConnected.toString().equals("0")) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:网线连接状态获取失败(%s)", Tools.getLineInfo(),
					isConnected.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请确保连接以太网，完成任意键继续");
		isConnected.delete(0, isConnected.length());
		if ((ret = nlBluetooth.ethGetConnected(isConnected)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取网线是否连接失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (!isConnected.toString().equals("1")) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:网线连接状态获取失败(%s)", Tools.getLineInfo(),
					isConnected.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 以太网设置的对话框
		config.netTransConfig(ethernetPara);
//		show_flag(HandlerMsg.DIALOG_SHOW_NET_TRANS, ethernetPara);
		// 获取网络的mac地址
		gui.cls_printf("获取以太网mac地址".getBytes());
		if ((ret = nlBluetooth.ethGetMacAddr(macAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取以太网MAC地址参数失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 以太网mac地址设置的对话框
		myactivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				setEthMac(macAddr.toString());
			}
		});
		synchronized (g_lock) {
			try {
				g_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if ((ret = nlBluetooth.ethSetMacAddr(EthMac)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:设置以太网MAC地址参数失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("获取以太网mac地址".getBytes());
		macAddr.delete(0, macAddr.length());
		if ((ret = nlBluetooth.ethGetMacAddr(macAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取以太网MAC地址参数失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (ethernetPara.isDHCPenable()) {
			// 动态
			// 使用DHCP获取网络地址
			if ((ret = nlBluetooth.netDHCP()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:使用DHCP获取网络地址失败(%d)", Tools.getLineInfo(),
						ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 获取网络地址
			if ((ret = nlBluetooth.ethGetNetAddr(ipAddr, gateway, subnetMask, dns)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取网络地址失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		} else {
			// 静态 "192.168.30.1;192.168.30.4;192.168.30.5"
			ret = nlBluetooth.ethSetAddress(ethernetPara.getLocalIp(), ethernetPara.getNetMask(),
					ethernetPara.getGateWay(), ethernetPara.getDns1());
			if (ret != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:静态设置网络地址失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 获取网络地址
			if ((ret = nlBluetooth.ethGetNetAddr(ipAddr, gateway, subnetMask, dns)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取网络地址失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 校验
			if (!ipAddr.toString().equals(ethernetPara.getLocalIp())
					|| !subnetMask.toString().equals(ethernetPara.getNetMask())
					|| !gateway.toString().equals(ethernetPara.getGateWay())
					|| !dns.toString().equals(ethernetPara.getDns1())) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:获取网络地址错误(%s,%s,%s,%s)",
						Tools.getLineInfo(), ipAddr, gateway, subnetMask, dns);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		// 域名解析
		gui.cls_printf("域名解析百度地址".getBytes());
		if ((ret = nlBluetooth.getDnsIp("www.baidu.com", baiduIpAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:解析域名失败(%d,%s)", Tools.getLineInfo(), ret,
					baiduIpAddr.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		// ping
		gui.cls_printf("ping百度地址".getBytes());
		if ((ret = nlBluetooth.netPing("14.215.177.37")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:ping操作失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 通讯 打开TCP通讯通道
		gui.cls_printf("打开TCP通讯通道".getBytes());
		if ((ret = nlBluetooth.tcpOpen(tcpHandle)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:打开TCP通讯通道失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		tcp = Integer.parseInt(tcpHandle.toString());
		// 绑定本地IP地址
		gui.cls_show_msg1(2, "绑定本地IP地址：%s", ipAddr.toString());
		if ((ret = nlBluetooth.tcpBind(tcp, ipAddr.toString(), (short) 8888)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:绑定本地IP地址失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 连接服务器
		gui.cls_printf("连接服务器".getBytes());
		if ((ret = nlBluetooth.tcpConnect(tcp, ethernetPara.getServerIp(), (short) ethernetPara.getServerPort(),
				15000)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:连接服务器失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		// 发送数据
		gui.cls_printf("发送数据".getBytes());
		if ((ret = nlBluetooth.tcpWrite(tcp, sendByte.length, sendByte, 15000, sendLen)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:发送数据失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		// 接收数据
		gui.cls_printf("接收数据".getBytes());
		if ((ret = nlBluetooth.tcpRead(tcp, Integer.parseInt(sendLen.toString()), 15000, recLen, recByte)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:接收数据失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		LoggerUtil.d("send data:" + ISOUtils.hexString(sendByte));
		LoggerUtil.d("recv data:" + ISOUtils.hexString(recByte));
		// 比较
		if (!Tools.memcmp(sendByte, recByte, Integer.parseInt(recLen.toString()))) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:数据校验失败", Tools.getLineInfo());
			return;
		}
		// 关闭TCp
		if ((ret = nlBluetooth.tcpReset(tcp)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:关闭tcp失败(%d)", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "ETHFun", 0, "获取的网络mac地址为%s,正确则测试通过", macAddr);
	}

	/**
	 * wifi ap的功能测试
	 */
	private void wifiAp() {
		while (true) {
			int nkeyIn = gui.cls_show_msg("底座功能\n0.wifi功能\n1.获取连接WifiAp的设备信息\n");
			switch (nkeyIn) {
			case '0':
				wifiApFun();
				break;

			case '1':
				WifiApDeviceInfo();
				break;

			case ESC:
				return;
			}
		}
	}

	/**
	 * wifiAp功能 zhangxinj
	 */
	private int wifiApFun() {
		/* private & local definition */
		int ret = -1;/* 错误返回码 */
		StringBuffer ssid = new StringBuffer();
		StringBuffer wpa = new StringBuffer();
		StringBuffer channel = new StringBuffer();
		StringBuffer dns = new StringBuffer();
		StringBuffer isHide = new StringBuffer();
		StringBuffer macAddr = new StringBuffer();
		StringBuffer wifiInfo = new StringBuffer();

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return -10086;
		}
		// 初始化就有ssid和密码
		gui.cls_printf("初始化wifi".getBytes());
		if ((ret = nlBluetooth.wifiApInit("192.168.2.1", "255.255.255.0")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:初始化wifi出错(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		// 获取WifiAp的mac地址
		if ((ret = nlBluetooth.wifiApGetMacAddr(macAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取wifi mac地址出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		ssid.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetSsid(ssid)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取wifi名称出错(%d,%s)", Tools.getLineInfo(),
					ret, ssid.toString());
			if (!GlobalVariable.isContinue)
				return ret;
		}
		LoggerUtil.e("ssid1=" + ssid);
		wifiPara.setSsid(ssid.toString());
		myactivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				set_dongle_wifi(wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 有修改密码就可以 名称 dns 信道一起修改
		if (!wifiPara.getPasswd().equals("") || !wifiOldPwd.equals("")) {
			gui.cls_printf("修改名称、wifi密码、信道、dns".getBytes());
			if ((ret = nlBluetooth.wifiApSetInfo(wifiPara.getSsid(), wifiOldPwd, wifiPara.getPasswd(),
					wifiPara.getDns1(), wifiPara.getChannel())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:修改wifi密码、名称、dns、信道出错(%d)",
						Tools.getLineInfo(), ret);
				return ret;
			}
		} else {
			// 修改wifi名称 新密码和旧密码为空 则修改名称
			gui.cls_printf(("修改名称：" + wifiPara.getSsid()).getBytes());
			if ((ret = nlBluetooth.wifiApSetInfo(wifiPara.getSsid(), "", "")) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:修改wifi名称出错(%d)", Tools.getLineInfo(),
						ret);
				if (!GlobalVariable.isContinue)
					return ret;

			}
		}
		// 修改信道
		if ((ret = nlBluetooth.wifiApSetChannel(wifiPara.getChannel())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置信道出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		// 修改DNS
		if ((ret = nlBluetooth.wifiApSetDns(wifiPara.getDns1())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置DNS出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		// 设置是否隐藏 0-不隐藏 1-隐藏
		int hideFlag = wifiPara.isScan_ssid() == true ? 0 : 1;
		if ((ret = nlBluetooth.wifiApSetHideSsid(hideFlag)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置是否隐藏出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		// 修改加密模式
		gui.cls_printf(("设置加密模式：" + encMode).getBytes());
		Log.v("wxy", "设置加密模式：" + encMode);
		if ((ret = nlBluetooth.wifiApSetWpa(encMode)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置加密模式出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		// 设置完毕需重启wifi
		gui.cls_printf("重启wifi".getBytes());
		if ((ret = nlBluetooth.wifiApReboot()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:重启wifi出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		// 验证名称
		ssid.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetSsid(ssid)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取wifi名称出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		LoggerUtil.e("ssid2=" + ssid);
		if (!wifiPara.getSsid().equals(ssid.toString())) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:校验wifi名称出错(%d,%s)", Tools.getLineInfo(),
					ret, ssid.toString());
			if (!GlobalVariable.isContinue)
				return ret;
		}
		wifiInfo.append("ssid:").append(ssid.toString()).append("\n");
		// 校验加密模式
		if ((ret = nlBluetooth.wifiApGetWpa(wpa)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取加密模式出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		if (Integer.parseInt(wpa.toString()) != encMode) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:加密模式错误(%s,%d)", Tools.getLineInfo(),
					wpa.toString(), encMode);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		wifiInfo.append("加密模式:").append(wpa.toString()).append("\n");
		// 校验信道
		if ((ret = nlBluetooth.wifiApGetChannel(channel)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取加密模式出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		if (Integer.parseInt(channel.toString()) != wifiPara.getChannel()) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置信道校验错误，预期：%d，实际：%s", Tools.getLineInfo(),
					wifiPara.getChannel(), channel.toString());
			if (!GlobalVariable.isContinue)
				return ret;
		}
		wifiInfo.append("channel:").append(channel.toString()).append("\n");
		// 验证DNS
		if ((ret = nlBluetooth.wifiApGetDns(dns)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取dns出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		if (!dns.toString().equals(wifiPara.getDns1())) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置dns校验错误(%s)", Tools.getLineInfo(),
					dns.toString());
			if (!GlobalVariable.isContinue)
				return ret;
		}
		wifiInfo.append("DNS:").append(dns.toString()).append("\n");
		// 校验是否隐藏
		if ((ret = nlBluetooth.wifiApGetHideSsid(isHide)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:获取是否隐藏出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return ret;
		}
		if (Integer.parseInt(isHide.toString()) != hideFlag) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置dns校验错误", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return ret;
		}

		gui.cls_show_msg("已设置的wifiAp的信息如下：\n%s任意键继续", wifiInfo.toString());
		gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "获取到的wifiAp的mac地址为%s，wifiAp功能测试通过", macAddr.toString());
		return NDK_OK;

	}

	/**
	 * 获取WIFI AP 连接的设备信息(薛斯斯)
	 */
	public void WifiApDeviceInfo() {
		/* private & local definition */
		int ret = -1;
		StringBuffer infoLen = new StringBuffer();
		StringBuffer outInfo = new StringBuffer();

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 开启WifiAp
		gui.cls_show_msg("进入0.wifi功能配置完WifiAP后，使用多台设备连接开启的WifiAP，完成后按任意键继续");
		if ((ret = nlBluetooth.wifiApGetDevice(infoLen, outInfo)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "WifiApDeviceInfo", g_keeptime, "line %d:获取WIFI AP连接的设备信息失败（%d）",
					Tools.getLineInfo(), ret);
			return;
		}

		if ((ret = writeDesInfo(outInfo)) == NDK_OK) {
			gui.cls_show_msg("获取的设备信息已写入pos根目录的wifiApDevicesInfo.txt文件中，请将信息与实际连接情况核对,可改变连接的设备数量，多次进入本用例获取连接信息");
		}
	}

	/**
	 * wifiAp+以太网（张鑫锯）
	 */
	public void WifiApAndEth() {
		/* private & local definition */
		// int ret = -1;
		SocketUtil socketUtil = null;

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 以太网
		ETHFun();
		// wifiAp
		wifiApFun();
		// 关闭蓝牙
		gui.cls_show_msg1(2, "断开蓝牙连接中...");
		nlBluetooth.disconnect();
		gui.cls_show_msg1(2, "请选择wifi名称为:%s,输入对应密码为:%s", wifiPara.getSsid(), wifiPara.getPasswd());
		// 扫描配置wifi列表
		switch (config.confConnWlan(wifiPara)) {
		case NDK_OK:
			socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
			break;

		case NDK_ERR:
			break;

		case NDK_ERR_QUIT:
		default:
			break;
		}
		// 连接wifiAp的热点来进行通讯
		WifiApSoc(socketUtil);
	}

	/**
	 * wifiAp 建链压力
	 * 
	 * @param socketUtil
	 */
	public void WifiApSoc(SocketUtil socketUtil) {
		/* private & local definition */
		// socket通讯
		int cnt = 0, succ = 0, ret = -1, bak = 0;
		PacketBean sendPacket = new PacketBean();
		int send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufwifi = new byte[PACKMAXLEN];

		/* process body */
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, wifiPara.getType());
		bak = cnt = sendPacket.getLifecycle();
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "wifiAp+以太网建链压力测试,已执行%d次，成功%d次，点击[取消]键退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			// 传输层建立
			if ((ret = layerBase.netUp(wifiPara, wifiPara.getType())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:NetUp失败(%d)", Tools.getLineInfo(),
						bak - cnt, ret);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			if ((ret = layerBase.transUp(socketUtil, wifiPara.getSock_t())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:transUp失败(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 发送数据
			if ((send_len = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,
					wifiPara)) != sendPacket.getLen()) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)",
						Tools.getLineInfo(), bak - cnt, sendPacket.getLen(), send_len);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 接收数据
			Arrays.fill(rbufwifi, (byte) 0);
			if ((rec_len = sockRecv(socketUtil, rbufwifi, sendPacket.getLen(), SO_TIMEO, wifiPara)) != sendPacket
					.getLen()) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)",
						Tools.getLineInfo(), bak - cnt, sendPacket.getLen(), rec_len);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbufwifi, sendPacket.getLen())) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(),
						bak - cnt);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			if ((ret = layerBase.transDown(socketUtil, wifiPara.getSock_t())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:transDown失败（%d）",
						Tools.getLineInfo(), bak - cnt, ret);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			if ((ret = layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApSoc", g_keeptime, "line %d:第%d次:netDown失败（%d）",
						Tools.getLineInfo(), bak - cnt, ret);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "WifiApSoc", g_time_0, "以太网+wifiAp建链测试完成，已执行次数为%d，成功为%d次", bak - cnt, succ);
	}

	public void postEnd(BluetoothAdapter bluetoothAdapter) {
		SystemClock.sleep(2000);
	}

	public void RS232Config() {

		int nkeyIn = gui.cls_show_msg(
				"波特率\n0.300  1.1200  2.2400\n3.4800  4.9600  5.19200\n6.38400  7.57600  8.115200\n9.230400");
		switch (nkeyIn) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			BpsBean.bpsValue = bps[nkeyIn - '0'];
			break;

		case ESC:
			break;
		}
		// 设置超时时间
		COM_TIMEOUT = (BUFSIZE_SERIAL * 8 % BpsBean.bpsValue == 0 ? BUFSIZE_SERIAL * 8 / BpsBean.bpsValue
				: BUFSIZE_SERIAL * 8 / BpsBean.bpsValue + 1) + 10;
		gui.cls_show_msg1(2, "设置波特率%s成功,请将电脑串口波特率修改为对应值才可进行串口通讯测试", BpsBean.bpsValue);

	}

	// USB串口配置，USB不要波特率
	public void usbConfig(int portType) {
		/* private & local definition */
		// "115200,8,N,1"
		int ret = -1;
		String[] dataBit = { "8", "7", "6", "5" };
		String[] checkBit = { "N", "O", "E" };
		String[] stopBit = { "1", "2" };

		/* process body */
		for (int j = 0; j < DATABIT_NUM; j++) {
			for (int k = 0; k < CHECKBIT_NUM; k++) {
				for (int n = 0; n < STOPBIT_NUM; n++) {
					for (int l = 0; l < IR_EN; l++) {
						for (int m = 0; m < BLOCK_EN; m++) {
							String szTemp = BpsBean.bpsValue + "," + dataBit[j] + "," + checkBit[k] + "," + stopBit[n];
							if ((ret = nlBluetooth.portOpen(portType, szTemp)) != NDK_OK) {
								gui.cls_show_msg1_record(TAG, "usbConfig", g_keeptime,
										"line %d:打开USB串口失败（%d），串口配置为（%s）", Tools.getLineInfo(), ret, szTemp);
								return;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * usb和RS232串口（王小钰）
	 * 
	 * @param portType 串口类型
	 */
	public void usbAndRS232(int portType) {
		/* private & local definition */
		int type = -100;// U口等于0 Rs232等于1
		byte[] buf = new byte[BUFSIZE_SERIAL];
		byte[] rbuf = new byte[BUFSIZE_SERIAL];
		g_readBuf = new byte[BUFSIZE_SERIAL];
		StringBuffer readLen1 = new StringBuffer();
		StringBuffer readLen2 = new StringBuffer();
		int ret = -1;

		/* process body */
		/*
		 * if("".equals(nlBluetooth.getConnectedDeviceAddressA() )) {
		 * gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态"); return; }
		 */
		// 打开串口，各种情况组合，usb不需要波特率
//		gui.cls_printf("串口多种配置初始化...".getBytes());
//		usbConfig(portType);
		if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAndRS232", g_keeptime, "line %d:打开串口%d失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), portType, ret, BpsBean.bpsValue + ",8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("串口写数据...".getBytes());
		// 写数据（发送数据）
		gui.cls_show_msg("当前波特率为%d,请确保POS和PC已通过USB/RS232线连接,并开启PC端的AccessPort工具,完成任意键继续", BpsBean.bpsValue);
		for (int j = 0; j < buf.length; j++) {
			buf[j] = (byte) (Math.random() * 256);
		}
		if (gui.cls_show_msg("请确认是否用USB口。是[确认],否[其他]") == ENTER) {
			type = 0;
			gui.cls_show_msg1(1, "type====%d,当前用U口", type);

		}
//		//开启串口读写线程
//		nlBluetooth.startSingleChannelThread(portType);
		// i 和len 置为0

		if (type == 0) {
			// 开启串口读写线程
			nlBluetooth.startSingleChannelThread(portType);
			nlBluetooth.singleSend(buf);
		} else {
			if (nlBluetooth.sendDataA(buf) == false) {
				gui.cls_show_msg1_record(TAG, "Rs232", g_keeptime, "line %d:数据通道发送数据失败", Tools.getLineInfo());
				return;
			}
		}
//		if((ret=nlBluetooth.portWrite(portType, BUFSIZE_SERIAL, buf))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d写数据失败(%d)", Tools.getLineInfo(),portType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		// 判断指定串口发送缓冲区是否为空
//		if((ret=nlBluetooth.portTxSendOver(portType))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d发送缓冲区预期为空,实际(%d)", Tools.getLineInfo(),portType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		gui.cls_printf("串口读数据...".getBytes());
		// 读数据（接收数据）
		gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并发送,完成任意键继续");
		int time = 0;
		long outTime = System.currentTimeMillis();
		// 读取数据等于写入数据长度才退出或者超时
		while (time < COM_TIMEOUT) {
			time = (int) Tools.getStopTime(outTime);
			if (len == BUFSIZE_SERIAL) {
				break;
			}
			SystemClock.sleep(10);
		}

		if (len != BUFSIZE_SERIAL || Tools.memcmp(buf, g_readBuf, BUFSIZE_SERIAL) == false) {

			LoggerUtil.e("DATA_BUFFER=" + DATA_kb);
			LoggerUtil.e("len=" + len);
			LoggerUtil.e("wbuf=" + ISOUtils.hexString(buf));
			LoggerUtil.e("data=" + ISOUtils.hexString(g_readBuf));
			gui.cls_show_msg1_record(TAG, "usbchannel", g_keeptime, "line %d:数据校验失败.接收到的长度为%d", Tools.getLineInfo(),
					len);
			return;
		}
//		//取输入缓存,最大4095,所以循环取出
//		if((ret=nlBluetooth.portReadLen(portType, readLen1))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d取缓存区数据失败(%d)", Tools.getLineInfo(),portType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		LoggerUtil.i("readLen:"+readLen1);
//		// 读数据(接收数据)
//		if((ret=nlBluetooth.portRead(portType, BUFSIZE_SERIAL, MAXWAITTIME, readLen2, rbuf))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d读数据失败(%d)", Tools.getLineInfo(),portType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		// 比较所取得的输入缓冲的数据长度
//		if (!((readLen1.toString()).equals(readLen2.toString()))) 
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime, "line %d:串口%d取缓存区数据与预期大小不符合(预期%s,实际%s)", Tools.getLineInfo(),portType,readLen1.toString(),readLen2.toString());
//			if (!GlobalVariable.isContinue)
//				return;
//		}

		LoggerUtil.i("rbuf:" + ISOUtils.hexString(rbuf));
//		//比较读写数据数据内容
//		if(!Tools.memcmp(buf, g_readBuf, BUFSIZE_SERIAL))
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d读写数据不一致", Tools.getLineInfo(),portType);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		//清USB串口缓存
//		gui.cls_printf("串口清缓存...".getBytes());
//		if((ret=nlBluetooth.portClrBuf(portType))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d清缓存失败(%d)", Tools.getLineInfo(),portType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		// 关闭串口
//		gui.cls_printf("关闭串口...".getBytes());
//		if((ret=nlBluetooth.portClose(portType))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "usbAndRS232",g_keeptime,"line %d:串口%d关闭失败(%d)", Tools.getLineInfo(),portType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		// usb通道需要关闭读写线程
		if (type == 0) {

			nlBluetooth.singleCancel();
		}
		gui.cls_show_msg1_record(TAG, "usbAndRS232", 0, "%s测试通过", TESTITEM);
	}

	/**
	 * wifi相关：张鑫锯 其他：王小钰
	 */
	public void pressFun() {
		SocketUtil socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		while (true) {
			int nkeyIn = gui.cls_show_msg("底座压力\n0.配置\n1.读写压力\n2.流程压力\n3.ping压力\n4.并发压力(透传方式)\n5.回连压力\n");
			switch (nkeyIn) {
			case '0':// 配置wifi
				wifiConf(socketUtil);
				break;

			case '1':// 读写压力测试
				readWritePress(socketUtil);
				break;

			case '2':// 流程压力测试
				processPress();
				break;

			case '3':// ping压力测试
				pingPre();
				break;

			case '4':// 双通道并发通讯压力测试
				if (IS_DUAL)
					CmdDataPre();
				else
					gui.cls_show_msg("此版本为单通道固件，不支持双通道并发通讯压力测试,确认请按任意键继续");
				break;

			case '5':
				backlinkPre();
				break;
			case ESC:
				return;
			}
		}
	}

	/** 回连测试压力 add by 20190218 */
	private void backlinkPre() {
		int cnt, bak, succ = 0;
		long startTime;
		StringBuffer bpsBuffer = new StringBuffer();
		StringBuffer macBuffer = new StringBuffer();
		StringBuffer bpsBuffer1 = new StringBuffer();
		StringBuffer macBuffer1 = new StringBuffer();
		// 要分别测试绑定和未绑定状态
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 压力测试前获取底座的信息
		nlBluetooth.btGetTransPortA(bpsBuffer);
		nlBluetooth.btGetMacAddr(macBuffer);
		cnt = bak = gui.JDK_ReadData(10, 200, "请输入压力次数");
		gui.cls_show_msg("将蓝牙底座接入数字信号发生器间隔输出高-低电平给底座,高电平1min,低电平15s,完毕按任意键继续");
		while (cnt > 0) {
			cnt--;
			gui.cls_show_msg1(1, "底座上下电回连正在进行第%d次测试,成功%d次", bak - cnt, succ);
			// 等待底座的断开
			startTime = System.currentTimeMillis();
			while (Tools.getStopTime(startTime) < 60) {
				if (nlBluetooth.isConnectedA() == false)
					break;
			}
			gui.cls_printf("底座已断开,等待回连...".getBytes());
			startTime = System.currentTimeMillis();
			while (Tools.getStopTime(startTime) < 60) {
				if (nlBluetooth.isConnectedA() == true)
					break;
			}
			if (nlBluetooth.isConnectedA() == false) {
				gui.cls_show_msg1_record(TAG, "backlinkPre", g_keeptime, "line %d:第%d次:全功能底座回连失败", Tools.getLineInfo(),
						bak - cnt);
				continue;
			}
			// 燕清判断的回连成功是数据路回连成功就算回连上，命令通道需要再经过1-2s的延时 modify by zhengxq
			SystemClock.sleep(2000);
			if (cnt == 0)
				break;
			// 回连成功获取POS的信息
			nlBluetooth.btGetTransPortA(bpsBuffer1);
			nlBluetooth.btGetMacAddr(macBuffer1);
			// 比较回连前后数据是否一致
			if (bpsBuffer.equals(bpsBuffer1)) {
				gui.cls_show_msg1_record(TAG, "backlinkPre", g_keeptime, "line %d:第%d次:全功能底座波特率比较失败(预期:%s,实际:%s)",
						Tools.getLineInfo(), bak - cnt, bpsBuffer.toString(), bpsBuffer1.toString());
				continue;
			}
			if (macBuffer.equals(macBuffer1)) {
				gui.cls_show_msg1_record(TAG, "backlinkPre", g_keeptime, "line %d:第%d次:全功能底座mac比较失败(预期:%s,实际:%s)",
						Tools.getLineInfo(), bak - cnt, macBuffer.toString(), macBuffer1.toString());
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "backlinkPre", g_keeptime, "全功能底座上下电回连共测试%d次,成功%d次", bak, succ);
	}

	// 双通道并发通讯压力测试addbywangxy20181228
	private void CmdDataPre() {
		// 设置压力次数
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		final int datacnt = cnt;
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// nlBluetooth.setDataListener(listener);
		StringBuffer sb = new StringBuffer();
		if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:获取波特率失败(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		LoggerUtil.e("nlBluetooth.btGetTransPortA()=" + sb);
		if (BpsBean.bpsValue != Integer.valueOf(sb.toString())) {
			gui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:当前波特率与预期的不一致(预期=%d,实际%s)",
					Tools.getLineInfo(), BpsBean.bpsValue, sb);
			return;
		}
		gui.cls_show_msg("请确保POS和PC已通过RS232线连接,并开启PC端的ComTest工具设置波特率为%d并开启自发自收功能,完成任意键继续", BpsBean.bpsValue);
		while (cnt > 0) {
			cmdGui.cls_show_msg1(2, "双通道通讯并发压力,命令通道正在进行第%d次测试(已成功%d次)", bak - cnt, succ);
			cnt--;
			// 进行获取蓝牙名字/mac/bps的命令通讯
			if (!getBtName().equals(nlBluetooth.getConnectedDeviceNameA())) {
				cmdGui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:第%d次,获取蓝牙名字不一致(预期%s,实际%s)",
						Tools.getLineInfo(), bak - cnt, getBtName(), nlBluetooth.getConnectedDeviceNameA());
				continue;
			}
			if (!getBtAddr().equals(nlBluetooth.getConnectedDeviceAddressA())) {
				cmdGui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:第%d次,获取蓝牙MAC地址不一致(预期%s,实际%s)",
						Tools.getLineInfo(), bak - cnt, getBtAddr(), nlBluetooth.getConnectedDeviceAddressA());
				continue;
			}
			sb.delete(0, sb.length());
			if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
				cmdGui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:第%d次,获取蓝牙波特率失败(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				continue;
			}
			// 进行异常命令通道后开启透传并发
			// 并发子线程中进行数据透传
			if (bak - cnt == 1)// 进行一次命令通道通讯后，开启并发数据通道
			{
				new Thread(new Runnable() {

					@Override
					public void run() {
						int cnt = 0, bak = 0, succ = 0;
						byte[] sbuf = new byte[DATA_BUFFER];// 开发潘浩说数据透传最大为4k
						bak = cnt = datacnt;
						for (int j = 0; j < sbuf.length; j++)
							sbuf[j] = (byte) (Math.random() * 256);
						int time = 0;
						long startTime;
						while (cnt > 0) {
							dataGui.cls_show_msg1(2, "双通道通讯并发压力,数据通道通道正在进行第%d次测试(已成功%d次)", bak - cnt, succ);
							cnt--;
							dataGui.cls_show_msg1(1, "将发送" + DATA_BUFFER + "字节数据给底座");
							if (!nlBluetooth.sendDataA(sbuf)) {
								dataGui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime,
										"line %d:第%d次,数据透传失败(false,%s)", Tools.getLineInfo(), bak - cnt,
										ISOUtils.hexString(sbuf));
								continue;
							}
							// 接收
							time = 0;
							i = 0;
							len = 0;
							g_readBuf = new byte[DATA_BUFFER];
							startTime = System.currentTimeMillis();
							dataGui.cls_show_msg1(1, "将从底座接收" + DATA_BUFFER + "字节数据");
							while (time < DATA_TIMEOUT) {
								time = (int) Tools.getStopTime(startTime);
								if (len == DATA_BUFFER)
									break;
							}
							if (len != DATA_BUFFER) {
								LoggerUtil.e("len=" + len);
								LoggerUtil.e("sbuf=" + ISOUtils.hexString(sbuf));
								LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
								gui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime,
										"line %d:数据通道接收数据失败(ret = %d,sbuf=%s,rbuf=%s,实际len=%d)", Tools.getLineInfo(),
										BT_COMPARE_FAILED, ISOUtils.hexString(sbuf), ISOUtils.hexString(g_readBuf),
										len);
								continue;
							} else {
								// 比较收发数据
								if (Tools.memcmp(sbuf, g_readBuf, DATA_BUFFER) == false) {
									LoggerUtil.e("sbuf=" + ISOUtils.hexString(sbuf));
									LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
									gui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime,
											"line %d:数据通道收发数据失败(ret = %d,sbuf=%s,rbuf=%s)", Tools.getLineInfo(),
											BT_COMPARE_FAILED, ISOUtils.hexString(sbuf), ISOUtils.hexString(g_readBuf));
									continue;
								}
							}
							succ++;
						}
						dataGui.cls_show_msg1_record(TAG, "CmdDataPre", g_time_0, "数据透传通道并发压力测试完成,已执行次数为%d,成功为%d次",
								bak - cnt, succ);

					}
				}).start();
			}
			succ++;
		}
		cmdGui.cls_show_msg1_record(TAG, "CmdDataPre", g_time_0, "命令通道并发压力测试完成,已执行次数为%d,成功为%d次", bak - cnt, succ);
	}

	/**
	 * 压力配置中所需的wifi zhangxj
	 */
	public void wifiConf(SocketUtil socketUtil) {

		// int ret =-1;

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 以太网
		ETHFun();
		// wifiAp
		wifiApFun();

		// wifi列表
		gui.cls_show_msg1(2, "请选择wifi名称为:%s,输入对应密码为%s", wifiPara.getSsid(), wifiPara.getPasswd());
		// 扫描配置wifi列表
		switch (config.confConnWlan(wifiPara)) {
		case NDK_OK:
			socketUtil.setServerIP(wifiPara.getServerIp());
			socketUtil.setPort(wifiPara.getServerPort());
			break;

		case NDK_ERR:
			break;

		case NDK_ERR_QUIT:
		default:
			break;
		}
	}

	/**
	 * 读写压力测试（串口通讯、wifiAP）
	 */
	public void readWritePress(SocketUtil socketUtil) {
		while (true) {
			int nkeyIn = gui.cls_show_msg(
					"读写压力\n0.串口通讯(RS232+USB,命令方式)\n1.wifiAp+以太网建链压力\n2.wifiAp+以太网通讯压力\n3.数据通道透传压力\n4.单线程通讯压力");
//			int nkeyIn = gui.cls_show_msg("读写压力\n0.串口通讯（RS232+USB）\n1.wifiAp+以太网建链压力\n2.wifiAp+以太网通讯压力\n3.数据通道透传压力");
			switch (nkeyIn) {
			case '0':
				RS232UsbRWPre();
				break;

			case '1':
				WifiApEthRWPre(socketUtil);
				break;

			case '2':
				wifiApEthTransPre(socketUtil);
				break;

			case '3':// 数据通道透传压力
				if (IS_DUAL)
					DataTransPre();
				else
					gui.cls_show_msg("此版本为单通道固件，不支持数据通道透传压力测试,确认请按任意键继续");
				break;
			case '4':// 单线程数据读写压力
				if (!IS_DUAL)// 单通道支持USB和RS232串口
					SingleRWPre(-1, "USB/RS232");
				else
					SingleRWPre(8, "USB");// 双通道只支持USB口
				break;
			case ESC:
				return;
			}
		}
	}

	// 单线程透传压力，兼容旧固件addbywangxy20190107
	private void SingleRWPre(int portType, String portTip) {
		byte[] sbuf = new byte[1024 * 4];
		// 设置压力次数
		int cnt = 0, bak = 0, succ = 0;
		int tempType = 8;
		int time = 0;
		long startTime;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		for (int j = 0; j < sbuf.length; j++)
			sbuf[j] = (byte) (Math.random() * 256);

		// 单通道固件要选择此时测试的是USB还是RS232
		if (!IS_DUAL) {
			if (gui.cls_show_msg("单通道请选择是进行USB/RS232串口测试,USB串口[确认],RS232串口[其他]") == ENTER)
				tempType = 8;
			else
				tempType = 0;
		}
//		if((ret=nlBluetooth.portOpen(tempType, BpsBean.bpsValue+",8,N,1"))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "SingleRWPre",g_keeptime,"line %d:打开串口%d失败(%d),串口配置为(%s)", Tools.getLineInfo(),tempType,ret,BpsBean.bpsValue+",8,N,1");
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		nlBluetooth.startSingleChannelThread(portType);// -1表示USB和RS232均循环读取，应用也多用-1
		/*
		 * nlBluetooth.setOneDataListener(new OnDataReceiveListener() {
		 * 
		 * @Override public void onDataReceive(byte[] arg0) {
		 * LoggerUtil.e("单线程---接收数据广播"); len2=len2+arg0.length; System.arraycopy(arg0,
		 * 0,rbuf2, i2, arg0.length); i2=i2+arg0.length;
		 * 
		 * } });
		 */

		gui.cls_show_msg("请确保POS和PC已通过%s串口线连接，并开启PC端的ComTest工具设置波特率为%d并开启自发自收功能,完成任意键继续", portTip, BpsBean.bpsValue);
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "单通道数据读写压力，正在进行第%d次测试(已成功%d次),[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			i = 0;
			len = 0;
			gui.cls_show_msg1(1, "将发送" + sbuf.length + "字节数据给底座");
			nlBluetooth.singleSend(sbuf);
			// nlBluetooth.sendDataA(sbuf);
			gui.cls_show_msg1(1, "将从底座接收" + sbuf.length + "字节数据");

			// 30s等待AccessPort工具发送出数据
			time = 0;
			startTime = System.currentTimeMillis();
			while (time < MAXWAITTIME / 1000)// 超时时间设置错误，等待时间过长MAXWAITTIME
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (len > 0)
					break;
			}
			time = 0;
			startTime = System.currentTimeMillis();
			while (time < 20)// 接收时间15秒时接收的数据不全
			{
				time = (int) Tools.getStopTime(startTime);
				if (len == sbuf.length) {
					LoggerUtil.e("接收完毕len=" + len);
					break;
				}

			}
			LoggerUtil.e("接收完毕len=" + len + ",接收time=" + time);
			if (len != sbuf.length) {
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("sbuf.length=" + sbuf.length);
				LoggerUtil.e("sbuf=" + ISOUtils.hexString(sbuf));
				LoggerUtil.e("rbuf2=" + ISOUtils.hexString(g_readBuf));
				gui.cls_show_msg1_record(TAG, "SingleRWPre", g_keeptime, "line %d:单通道数据通道接收数据失败(实际len=%d)",
						Tools.getLineInfo(), len);
				continue;
			} else {
				// 比较收发数据
				if (Tools.memcmp(sbuf, g_readBuf, sbuf.length) == false) {
					LoggerUtil.e("sbuf=" + ISOUtils.hexString(sbuf));
					LoggerUtil.e("rbuf2=" + ISOUtils.hexString(g_readBuf));
					gui.cls_show_msg1_record(TAG, "SingleRWPre", g_keeptime, "line %d:单通道数据通道收发数据失败",
							Tools.getLineInfo());
					continue;
				}
			}
			succ++;
		}
		nlBluetooth.singleCancel();
//		if((ret=nlBluetooth.portClose(tempType))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "SingleRWPre",g_keeptime,"line %d:串口%d关闭失败(%d)", Tools.getLineInfo(),tempType,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		gui.cls_show_msg1_record(TAG, "SingleRWPre", g_time_0, "单通道数据读写压力测试完成,已执行次数为%d,成功为%d次", bak - cnt, succ);
	}

	// 数据通道透传压力addbywangxy20181227
	private void DataTransPre() {
		byte[] sbuf = new byte[DATA_BUFFER];// 开发潘浩说数据透传最大为4k
		// 设置压力次数
		int cnt = 0, bak = 0, succ = 0;
		int time = 0;
		long startTime;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		int ret = -1;
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// nlBluetooth.setDataListener(listener);
		for (int j = 0; j < sbuf.length; j++)
			sbuf[j] = (byte) (Math.random() * 256);
		StringBuffer sb = new StringBuffer();
		if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "DataTransPre", g_keeptime, "line %d:获取波特率失败(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		LoggerUtil.e("nlBluetooth.btGetTransPortA()=" + sb);
		if (BpsBean.bpsValue != Integer.valueOf(sb.toString())) {
			gui.cls_show_msg1_record(TAG, "DataTransPre", g_keeptime, "line %d:当前波特率与预期的不一致(预期=%d,实际%s)",
					Tools.getLineInfo(), BpsBean.bpsValue, sb);
			return;
		}
		gui.cls_show_msg("请确保POS和PC已通过RS232串口线连接,并开启PC端的ComTest工具设置波特率为%d并开启自发自收功能,完成任意键继续", BpsBean.bpsValue);
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "串口透传读写压力,正在进行第%d次测试(已成功%d次),[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			gui.cls_show_msg1(1, "将发送" + DATA_BUFFER + "字节数据给底座");
			if (!nlBluetooth.sendDataA(sbuf)) {
				gui.cls_show_msg1_record(TAG, "DataTransPre", g_keeptime, "line %d:第%d次,数据透传失败(false)",
						Tools.getLineInfo(), bak - cnt);
				continue;
			}
			// 接收
			time = 0;
			i = 0;
			len = 0;
			g_readBuf = new byte[DATA_BUFFER];
			startTime = System.currentTimeMillis();
			gui.cls_show_msg1(1, "将从底座接收" + DATA_BUFFER + "字节数据");
			while (time < DATA_TIMEOUT) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (len == DATA_BUFFER)
					break;
			}
			if (len != DATA_BUFFER) {
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("sbuf=" + ISOUtils.hexString(sbuf));
				LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
				gui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:数据通道接收数据失败(实际len=%d)",
						Tools.getLineInfo(), len);
				continue;
			} else {
				// 比较收发数据
				if (Tools.memcmp(sbuf, g_readBuf, DATA_BUFFER) == false) {
					LoggerUtil.e("sbuf=" + ISOUtils.hexString(sbuf));
					LoggerUtil.e("rbuf=" + ISOUtils.hexString(g_readBuf));
					gui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime, "line %d:数据通道收发数据失败", Tools.getLineInfo());
					continue;
				}
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "DataTransPre", g_time_0, "数据透传读写压力测试完成,已执行次数为%d,成功为%d次", bak - cnt, succ);
	}

	/**
	 * RS232串口和USB串口的压力(王小钰)
	 */
	public void RS232UsbPress() {
		/* private & local definition */
		byte[] buf1 = new byte[BUFSIZE_SERIAL + 1];
		byte[] rbuf1 = new byte[BUFSIZE_SERIAL + 1];
		int portType = -1;
		StringBuffer outReceiveLen1 = new StringBuffer();
		StringBuffer outReceiveLen2 = new StringBuffer();
		int ret = -1;

		/* process body */
		int nkeyIn = gui.cls_show_msg("串口通讯\n0.USB\n1.RS232\n");
		switch (nkeyIn) {
		case '0':
			portType = 8;
//				BpsBean.bpsValue=115200;
			gui.cls_printf("已选择USB读写压力".getBytes());
			break;
		case '1':
		default:
			portType = 0;
			// 随机设置波特率
			/*
			 * int[] bps = { 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200,
			 * 230400 }; BpsBean.bpsValue=bps[new Random().nextInt(10)];
			 */
//				BpsBean.bpsValue=115200;
			gui.cls_printf("已选择RS232读写压力".getBytes());
			break;
		}

		// 设置压力次数
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取

		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		for (int j = 0; j < buf1.length; j++)
			buf1[j] = (byte) (Math.random() * 256);

		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "串口通讯(RS232+USB)读写压力,正在进行第%d次测试(已成功%d次),[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			outReceiveLen1.setLength(0);
			;

			// 打开串口
			gui.cls_printf("打开串口...".getBytes());
			if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:打开串口%d失败(%d),串口配置为(%s)",
						Tools.getLineInfo(), portType, ret, BpsBean.bpsValue + ",8,N,1");
				if (!GlobalVariable.isContinue)
					return;
			}
			// 写数据（发送数据）
			if (succ == 0) {
				gui.cls_show_msg("请确保POS和PC已通过USB/RS232线连接,设置波特率为%d,并开启PC端的AccessPort工具，完成任意键继续", BpsBean.bpsValue);
			}
			gui.cls_printf("写数据...".getBytes());
			if ((ret = nlBluetooth.portWrite(portType, BUFSIZE_SERIAL, buf1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:第%d次串口%d写数据失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 读数据（接收数据）
			if (succ == 0) {
				gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并设置25s自动发送，等待第一次发送完成后，任意键继续");
			} else {
				gui.cls_printf("等待接收数据".getBytes());
				while (true) {
					outReceiveLen2.setLength(0);
					;
					if ((ret = nlBluetooth.portReadLen(portType, outReceiveLen2)) != NDK_OK) {
						gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:串口%d取缓存区数据失败(%d)",
								Tools.getLineInfo(), portType, ret);
						if (!GlobalVariable.isContinue)
							return;
					}
					int len = 0;
					try {
						len = Integer.parseInt(outReceiveLen2.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (len > 0) {
						gui.cls_show_msg1(5, "接收到数据");// 给了等待时间
						break;
					}
				}
			}

			gui.cls_printf("读数据...".getBytes());
			Arrays.fill(rbuf1, (byte) 0);
			if ((ret = nlBluetooth.portRead(portType, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen1, rbuf1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:第%d次串口%d读数据失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			LoggerUtil.i("readLen:" + outReceiveLen1);
			LoggerUtil.i("readBuf:" + ISOUtils.hexString(rbuf1));

			// 清USB串口缓存
			gui.cls_printf("清串口缓存...".getBytes());
			if ((ret = nlBluetooth.portClrBuf(portType)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:第%d次串口%d缓存失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 比较读写数据数据内容
			if (!Tools.memcmp(buf1, rbuf1, BUFSIZE_SERIAL)) {
				gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:第%d次串口%d读写数据不一致", Tools.getLineInfo(),
						bak - cnt, portType);
				continue;
			}
			// 关闭串口
			gui.cls_printf("关闭串口...".getBytes());
			if ((ret = nlBluetooth.portClose(portType)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_keeptime, "line %d:第%d次串口%d关闭失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			succ++;
		}
		// 关闭蓝牙
//		gui.cls_printf("断开蓝牙...".getBytes());
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg1_record(TAG, "RS232UsbPre", g_time_0, "RS232+USB串口读写压力测试完成,已执行次数为%d,成功为%d次", bak - cnt, succ);
	}

	/**
	 * RS232和USB串口的读写压力
	 */
	public void RS232UsbRWPre() {
		/* private & local definition */
		byte[] buf1 = new byte[BUFSIZE_SERIAL + 1];
		byte[] rbuf1 = new byte[BUFSIZE_SERIAL + 1];
		int portType = -1;
		StringBuffer outReceiveLen1 = new StringBuffer();
		StringBuffer outReceiveLen2 = new StringBuffer();
		int ret = -1;

		/* process body */
		int nkeyIn = gui.cls_show_msg("串口通讯\n0.USB\n1.RS232\n");
		switch (nkeyIn) {
		case '0':
			portType = 8;
			gui.cls_printf("已选择USB读写压力".getBytes());
			break;

		case '1':
			portType = 0;
			gui.cls_printf("已选择RS232读写压力".getBytes());
			break;

		case ESC:
			return;
		}

		// 设置压力次数
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		StringBuffer beforerom = new StringBuffer();
		StringBuffer afterrom = new StringBuffer();
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 测试前置，获取串口压力测试前的底座内存情况
		if (IS_DUAL) {
			if ((ret = nlBluetooth.btGetRomState(beforerom)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:蓝牙底座内存情况获取失败(%d)",
						Tools.getLineInfo(), ret);
				return;
			}
			LoggerUtil.e("beforerom=" + beforerom);
		}
//		String[] before = beforerom.toString().split(";");//以分号分割 内存总数；使用数；空余数
		// 打开串口
		gui.cls_show_msg1(1, "打开串口...");
		if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:打开串口%d失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), portType, ret, BpsBean.bpsValue + ",8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		for (int j = 0; j < buf1.length; j++)
			buf1[j] = (byte) (Math.random() * 256);

		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "串口通讯(RS232+USB)读写压力,正在进行第%d次测试(已成功%d次),[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			// 写数据（发送数据）
			if (succ == 0) {
				gui.cls_show_msg("请确保POS和PC已通过USB/RS232线连接,设置波特率为%d,并开启PC端的AccessPort工具,完成任意键继续", BpsBean.bpsValue);
			}

			gui.cls_printf("写数据...".getBytes());
			if ((ret = nlBluetooth.portWrite(portType, BUFSIZE_SERIAL, buf1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:第%d次串口%d写数据失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 读数据（接收数据）
			if (succ == 0) {
				gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并设置25s自动发送,等待第一次发送完成后,任意键继续");
			} else {
				gui.cls_printf("等待接收数据".getBytes());
				while (true) {
					outReceiveLen2.setLength(0);
					if ((ret = nlBluetooth.portReadLen(portType, outReceiveLen2)) != NDK_OK) {
						gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:串口%d取缓存区数据失败（%d）",
								Tools.getLineInfo(), portType, ret);
						if (!GlobalVariable.isContinue)
							return;
					}
					int len = 0;
					try {
						len = Integer.parseInt(outReceiveLen2.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (len > 0) {
						gui.cls_show_msg1(5, "接收到数据");// 给了等待时间
						break;
					}
				}
			}
			gui.cls_printf("读数据...".getBytes());
			Arrays.fill(rbuf1, (byte) 0);
			outReceiveLen1.setLength(0);
			;
			if ((ret = nlBluetooth.portRead(portType, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen1, rbuf1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:第%d次串口%d读数据失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 清USB串口缓存
			gui.cls_printf("清串口缓存...".getBytes());
			if ((ret = nlBluetooth.portClrBuf(portType)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:第%d次串口%d缓存失败(%d)",
						Tools.getLineInfo(), bak - cnt, portType, ret);
				if (!GlobalVariable.isContinue)
					return;
			}

			// 比较读写数据数据内容
			if (!Tools.memcmp(buf1, rbuf1, BUFSIZE_SERIAL)) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:第%d次串口%d读写数据不一致",
						Tools.getLineInfo(), bak - cnt, portType);
				continue;
			}
			succ++;
		}

		// 关闭串口
		gui.cls_printf("关闭串口...".getBytes());
		if ((ret = nlBluetooth.portClose(portType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:串口%d关闭失败(%d)", Tools.getLineInfo(),
					portType, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 测试后置，获取串口压力测试后的底座内存情况进行对比
		if (IS_DUAL) {
			gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_time_0,
					"RS232+USB串口读写压力测试完成，已执行次数为%d，成功为%d次，点击任意键后进行内存大小的比较", bak - cnt, succ);
			if ((ret = nlBluetooth.btGetRomState(afterrom)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:蓝牙底座内存情况获取失败(%d)",
						Tools.getLineInfo(), ret);
				return;
			}
			LoggerUtil.e("afterrom=" + afterrom);
			// String[] after = afterrom.toString().split(";");//以分号分割
			// 内存总数；使用数；空余数
			if (gui.ShowMessageBox(
					("串口压力测试前，底座内存情况对应为" + beforerom + "(内存总数+使用数+空余数)\n串口压力测试后,底座内存情况对应为" + afterrom
							+ "(内存总数+使用数+空余数)\n"
							+ "请根据实际数值对比，确认底座在串口压力测试后是否不存在底座内存泄露的情况（使用数前后相差过大）\n未泄露点击[确认],泄露点击[其他]").getBytes(),
					(byte) (BTN_OK | BTN_CANCEL), GlobalVariable.WAITMAXTIME) != BTN_OK) {
				gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_keeptime, "line %d:底座在进行串口读写压力测试后,存在内存泄露的情况",
						Tools.getLineInfo());
				return;
			}
		}
		// 关闭蓝牙
//		gui.cls_printf("断开蓝牙...".getBytes());
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg1_record(TAG, "RS232UsbRWPre", g_time_0, "RS232+USB串口读写压力测试完成,已执行次数为%d,成功为%d次,内存未泄露,测试通过",
				bak - cnt, succ);
	}

	public void processPress() {
		while (true) {
			int nkeyIn = gui.cls_show_msg("流程压力\n0.串口通讯(RS232+USB,命令方式)\n1.wifiAp+以太网\n");
			switch (nkeyIn) {
			case '0':
				RS232UsbPress();
				break;

			case '1':
				wifiApEthPress();
				break;

			case ESC:
				return;
			}
		}

	}

	/**
	 * 以太网+wifiAp流程压力(张鑫锯)
	 */
	public void wifiApEthPress() {
		/* private & local definition */
		SocketUtil socketUtil;
		// socket通讯
		int cnt = 0, succ = 0, ret = -1, bak = 0;
		PacketBean sendPacket = new PacketBean();

		int send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufwifi = new byte[PACKMAXLEN];
		socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, wifiPara.getType());
		bak = cnt = sendPacket.getLifecycle();
		// 蓝牙是否已连接
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		/* process body */
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "wifiAp+以太网流程测试,已执行%d次，成功%d次，[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			// 默认以太网动态
			gui.cls_printf("打开以太网".getBytes());
			if (ethernetPara.isDHCPenable()) {
				// 动态
				// 使用DHCP获取网络地址
				if ((ret = nlBluetooth.netDHCP()) != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line:%d,第%d次:使用DHCP获取网络地址失败(%d)",
							Tools.getLineInfo(), bak - cnt, ret);
					if (!GlobalVariable.isContinue)
						return;
				}
			} else {
				// 静态 "192.168.30.1;192.168.30.4;192.168.30.5"
				ret = nlBluetooth.ethSetAddress(ethernetPara.getLocalIp(), ethernetPara.getNetMask(),
						ethernetPara.getGateWay(), ethernetPara.getDns1());
				if (ret != NDK_OK) {
					gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:第%d次:静态设置网络地址失败(%d)",
							Tools.getLineInfo(), bak - cnt, ret);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			// ping,验证以太网是否通
			if ((ret = nlBluetooth.netPing("14.215.177.37")) != 0) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:ping操作失败(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				continue;
			}
			// wifi初始化
			gui.cls_show_msg1(2, "打开wifi");
			if ((ret = nlBluetooth.wifiApInit("192.168.2.1", "255.255.255.0")) != 0) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:初始化wifi出错(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				continue;
			}

			// 传输层建立
			if ((ret = layerBase.netUp(wifiPara, wifiPara.getType())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:NetUp失败（%d）",
						Tools.getLineInfo(), bak - cnt, ret);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			if ((ret = layerBase.transUp(socketUtil, wifiPara.getSock_t())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:transUp失败(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 发送数据
			if ((send_len = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,
					wifiPara)) != sendPacket.getLen()) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)",
						Tools.getLineInfo(), bak - cnt, sendPacket.getLen(), send_len);
				postEnd(bluetoothAdapter);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 接收数据
			Arrays.fill(rbufwifi, (byte) 0);
			if ((rec_len = sockRecv(socketUtil, rbufwifi, sendPacket.getLen(), SO_TIMEO, wifiPara)) != sendPacket
					.getLen()) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)",
						Tools.getLineInfo(), bak - cnt, sendPacket.getLen(), rec_len);
				postEnd(bluetoothAdapter);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbufwifi, sendPacket.getLen())) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(),
						bak - cnt);
				postEnd(bluetoothAdapter);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			if ((ret = layerBase.transDown(socketUtil, wifiPara.getSock_t())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:transDown失败（%d）",
						Tools.getLineInfo(), bak - cnt, ret);
				postEnd(bluetoothAdapter);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			if ((ret = layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:netDown失败（%d）",
						Tools.getLineInfo(), bak - cnt, ret);
				postEnd(bluetoothAdapter);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 断开wifiAp
			gui.cls_printf("断开wifi".getBytes());
			if ((ret = nlBluetooth.wifiApClose()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:断开wifiAp失败(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				continue;
			}
			// 断开以太网
			gui.cls_printf("断开以太网".getBytes());
			if ((ret = nlBluetooth.ethDisConnect()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:第%d次:断开以太网失败(%d)",
						Tools.getLineInfo(), bak - cnt, ret);
				continue;
			}
			succ++;
			postEnd(bluetoothAdapter);

		}
		// 测试后置，由于上述流程测试的最后一步是断开以太网和wifi，需重新连接以太网和wifi，保证测试结束后，wifi可以正常使用
		gui.cls_printf("测试后置，重新打开以太网".getBytes());
		if (ethernetPara.isDHCPenable()) {
			if ((ret = nlBluetooth.netDHCP()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line:%d,使用DHCP获取网络地址失败(%d)", Tools.getLineInfo(),
						ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		} else {
			// 静态 "192.168.30.1;192.168.30.4;192.168.30.5"
			ret = nlBluetooth.ethSetAddress(ethernetPara.getLocalIp(), ethernetPara.getNetMask(),
					ethernetPara.getGateWay(), ethernetPara.getDns1());
			if (ret != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "ETHFun", g_keeptime, "line %d:静态设置网络地址失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		// ping,验证以太网是否通
		if ((ret = nlBluetooth.netPing("14.215.177.37")) != 0) {
			gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:ping操作失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// wifi初始化
		gui.cls_show_msg1(2, "测试后置，重新打开wifi");
		if ((ret = nlBluetooth.wifiApInit("192.168.2.1", "255.255.255.0")) != 0) {
			gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_keeptime, "line %d:初始化wifi出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 关闭蓝牙
//		gui.cls_printf("断开蓝牙...".getBytes());
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg1_record(TAG, "WifiApEthPre", g_time_0, "以太网+wifiAp流程压力测试完成，已执行次数为%d，成功为%d次", bak - cnt, succ);

	}

	/**
	 * 以太网+wifiAp读写压力 ：建链压力
	 * 
	 * @param socketUtil
	 */
	public void WifiApEthRWPre(SocketUtil socketUtil) {
		/* private & local definition */
		int ret = -1;

		// 进行wifi的socket数据通讯
		WifiApSoc(socketUtil);

		// 断开wifiAp
		if ((ret = nlBluetooth.wifiApClose()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "WifiApEthRWPre", g_keeptime, "line %d:断开wifiAp失败(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 断开以太网
		if ((ret = nlBluetooth.ethDisConnect()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "WifiApEthRWPre", g_keeptime, "line %d:断开以太网失败(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 蓝牙断开，由于wifi被关闭以太网被断开，wifi需重启才能后续正常使用，所以，此处加入断开蓝牙的操作，避免后续测试中异常 addby
		// wangxiaoyu 20181101
		gui.cls_printf("断开蓝牙...".getBytes());
		nlBluetooth.disconnect();
	}

	/**
	 * 以太网+wifiAp读写压力 :通讯压力
	 * 
	 * @param socketUtil
	 */
	public void wifiApEthTransPre(SocketUtil socketUtil) {
		// socket通讯
		int cnt = 0, succ = 0, ret = -1, bak = 0;
		PacketBean sendPacket = new PacketBean();
		int send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufwifi = new byte[PACKMAXLEN];

		/* process body */
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, wifiPara.getType());
		bak = cnt = sendPacket.getLifecycle();
		// 传输层建立
		if ((ret = layerBase.netUp(wifiPara, wifiPara.getType())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:第%d次:NetUp失败(%d)",
					Tools.getLineInfo(), bak - cnt, ret);
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
			return;
		}
		if ((ret = layerBase.transUp(socketUtil, wifiPara.getSock_t())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:第%d次:transUp失败(%d)",
					Tools.getLineInfo(), bak - cnt, ret);
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
			return;
		}
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "wifiAp+以太网通讯压力测试,已执行%d次，成功%d次，点击[取消]键退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;

			// 发送数据
			if ((send_len = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,
					wifiPara)) != sendPacket.getLen()) {
				gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)",
						Tools.getLineInfo(), bak - cnt, sendPacket.getLen(), send_len);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 接收数据
			Arrays.fill(rbufwifi, (byte) 0);
			if ((rec_len = sockRecv(socketUtil, rbufwifi, sendPacket.getLen(), SO_TIMEO, wifiPara)) != sendPacket
					.getLen()) {
				gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)",
						Tools.getLineInfo(), bak - cnt, sendPacket.getLen(), rec_len);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			// 比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbufwifi, sendPacket.getLen())) {
				gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:第%d次:数据校验失败",
						Tools.getLineInfo(), bak - cnt);
				layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
				continue;
			}
			succ++;

		}
		if ((ret = layerBase.transDown(socketUtil, wifiPara.getSock_t())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:transDown失败（%d）",
					Tools.getLineInfo(), ret);
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
			return;
		}
		if ((ret = layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_keeptime, "line %d:netDown失败（%d）", Tools.getLineInfo(),
					ret);
			layerBase.netDown(socketUtil, wifiPara, wifiPara.getSock_t(), wifiPara.getType());
			return;
		}
		gui.cls_show_msg1_record(TAG, "wifiApEthTransPre", g_time_0, "以太网+wifiAp通讯压力测试完成，已执行次数为%d，成功为%d次", bak - cnt,
				succ);
	}

	/**
	 * ping压力测试，薛斯斯
	 */
	public void pingPre() {
		/* private & local definition */
		// 设置压力次数
		int cnt = 0, succ = 0, i = 0, ret;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		cnt = packet.getLifecycle();// 交叉次数获取
		StringBuffer isConnected = new StringBuffer();

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 确保连接以太网
		gui.cls_show_msg("请确保连接以太网，完成任意键继续");
		if ((ret = nlBluetooth.ethGetConnected(isConnected)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "pingPre", g_keeptime, "line:%d,获取网线是否连接失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (!isConnected.toString().equals("1")) {
			gui.cls_show_msg1_record(TAG, "pingPre", g_keeptime, "line:%d,网线连接状态获取失败(%s)", Tools.getLineInfo(),
					isConnected.toString());
			if (!GlobalVariable.isContinue)
				return;
		}

		while (cnt > 0) {
			if (gui.cls_show_msg1(1, "ping压力测试中...\n还剩%d次（已成功%d次），[取消]退出测试...", cnt, succ) == ESC)
				break;

			cnt--;
			i++;
			// ping 压力测试，ping百度的IP
			if ((ret = nlBluetooth.netPing("14.215.177.37")) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "pingPre", g_keeptime, "line %d:第%d次ping网络失败(ret = %d)",
						Tools.getLineInfo(), i, ret);
				continue;
			}
			succ++;
		}
		// 蓝牙断开
//		gui.cls_printf("断开蓝牙...".getBytes());
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg1_record(TAG, "pingPre", g_time_0, "ping压力测试%d次,成功%d次", i, succ);

	}

	/**
	 * 异常测试 张鑫锯
	 */
	public void dongleAbnormal() {
		while (true) {
			int nkeyIn = gui.cls_show_msg(
					"底座异常\n0.断电异常\n1.蓝牙底座重命名\n2.未接入串口、网线\n3.蓝牙未连接\n4.蓝牙操作中断开\n5.多手机连接一底座\n6.多台底座的蓝牙mac、以太网mac和WifiAp mac各不相同\n7.USB主从异常测试\n8.信道异常测试");
			switch (nkeyIn) {
			case '0':
				powerAbnormal();
				break;

			case '1':
				renameAbnormal();
				break;

			case '2':
				linkAbnormal();
				break;

			case '3':
				BTdisconnectAbnormal();
				break;

			case '4':
				BTdisconnectAbnormal2();
				break;

			case '5':
				moreToOneAbnormal();
				break;

			case '6':
				oneToMoreMacAbnormal();
				break;

			case '7':
				usbAbnormal();
				break;

			case '8':
				channelAbnormal();
				break;

			case ESC:
				return;
			}
		}
	}

	// 单次USB扫码枪扫码
	public void ScanOne() {
		int ret = -1;
		StringBuffer outReceiveLen2 = new StringBuffer();
		StringBuffer outReceiveLen1 = new StringBuffer();
		// byte[] buf = new byte[BUFSIZE_SERIAL];
		byte[] rbuf = new byte[BUFSIZE_SERIAL];
		int BUFSIZE_SERIAL = 1024 * 4;
		// 打开蓝牙
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// usb主模式---扫描枪
		gui.cls_show_msg("USB主模式扫描枪，请将扫描枪接上USB串口线，完成点击任意键继续");
		if ((ret = nlBluetooth.portOpen(10, "115200,8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:打开USB串口失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), ret, "115200,8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		// 请扫码
		gui.cls_printf("请扫条形码...".getBytes());
		while (true) {
			outReceiveLen2.setLength(0);
			;
			// 取缓冲区数据大小
			if ((ret = nlBluetooth.portReadLen(10, outReceiveLen2)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:USB串口取缓存区数据失败(%d)",
						Tools.getLineInfo(), ret);
				continue;
			}
			if (Integer.valueOf(outReceiveLen2.toString()) != 0)
				break;

		}
		// 有数据
		Arrays.fill(rbuf, (byte) 0);
		if ((ret = nlBluetooth.portRead(10, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen1, rbuf)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:USB串口读数据失败(%d)", Tools.getLineInfo(),
					ret);
		}
		// 比较所取得的输入缓冲的数据长度
		if (!((outReceiveLen2.toString()).equals(outReceiveLen1.toString()))) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:USB串口取缓存区数据与预期大小不符合",
					Tools.getLineInfo());
		}
		// 弹框显示读到的rbuf的数据
		String res = new String(rbuf);
		if (gui.ShowMessageBox((res.trim() + "与条形码是否一致？").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				GlobalVariable.WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:%s扫描错误,扫到码值为(%s)", Tools.getLineInfo(),
					TESTITEM, res);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("串口请缓存...".getBytes());
		if ((ret = nlBluetooth.portClrBuf(10)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:USB串口清缓存失败(%d)", Tools.getLineInfo(),
					ret);
		}
		// 关闭串口
		gui.cls_printf("关闭串口...".getBytes());
		if ((ret = nlBluetooth.portClose(10)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:USB串口关闭失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
	}

	// USB 主从模式切换异常测试
	public void usbAbnormal() {
		/* private & local definition */
		// int i = 0;
		/* process body */
		// 打开蓝牙
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 主扫描枪-从-主钱箱-从-主扫描枪-主钱箱-主扫描枪
		// USB主模式扫描枪
		ScanOne();
		// usb从模式
		gui.cls_show_msg("USB从模式，请接入USB虚拟串口线,按任意键继续");
		BpsBean.bpsValue = 115200;
		usbAndRS232(8);
		// 钱箱
		gui.cls_show_msg("USB主模式钱箱，请接入钱箱的USB线,按任意键继续");
		if ((ret = nlBluetooth.openBoxA()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:钱箱打开失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		} else {
			if (gui.cls_show_msg("请确认钱箱已处于开启状态：[确认]正确，[其他]错误") != ENTER) {
				gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:开启钱箱失败", Tools.getLineInfo());
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		// usb从模式
		gui.cls_show_msg("USB从模式,请接入两头均大头的USB线,按任意键继续");
		BpsBean.bpsValue = 115200;
		usbAndRS232(8);
		// USB主模式扫描枪
		ScanOne();
		// 钱箱
		gui.cls_show_msg("USB主模式钱箱,请接入钱箱的USB线,按任意键继续");
		if ((ret = nlBluetooth.openBoxA()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:钱箱打开失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		} else {
			if (gui.cls_show_msg("请确认钱箱已处于开启状态：[确认]正确，[其他]错误") != ENTER) {
				gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "line %d:开启钱箱失败", Tools.getLineInfo());
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		// USB主模式扫描枪
		ScanOne();

		gui.cls_show_msg1_record(TAG, "usbAbnormal", g_keeptime, "USB主从切换异常测试通过");

	}

	/**
	 * 蓝牙底座设置非0-13的信道异常(郑薛晴)
	 */
	public void channelAbnormal() {
		/* private & local definition */
		int ret = -1;

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_printf("信道异常测试...".getBytes());

		// 动态，使用DHCP获取网络地址
		if ((ret = nlBluetooth.netDHCP()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line:%d,使用DHCP获取网络地址失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		// 初始化就有ssid和密码
		if ((ret = nlBluetooth.wifiApInit("192.168.2.1", "255.255.255.0")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:初始化wifi出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		// 密码的框框
		myactivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				set_dongle_wifi(wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if ((ret = nlBluetooth.wifiApSetInfo(wifiPara.getSsid(), wifiOldPwd, wifiPara.getPasswd(), wifiPara.getDns1(),
				-1)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:信道异常测试失败(%d)", Tools.getLineInfo(),
					ret);
			return;
		}

		if (gui.cls_show_msg("请用设备查看SSID:%s的AP是否可以上网，是[确认]，否[取消]", wifiPara.getSsid()) == ENTER) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:信道异常测试失败", Tools.getLineInfo());
			return;
		}

		if ((ret = nlBluetooth.wifiApReboot()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:重启wifi AP失败(%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		// 修改信道为14，应返回非法
		if ((ret = nlBluetooth.wifiApSetInfo(wifiPara.getSsid(), wifiOldPwd, wifiPara.getPasswd(), wifiPara.getDns1(),
				14)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:信道异常测试失败(%d)", Tools.getLineInfo(),
					ret);
			return;
		}

		if ((ret = nlBluetooth.wifiApReboot()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:重启wifi AP失败(%d)", Tools.getLineInfo(),
					ret);
			return;
		}

		if (gui.cls_show_msg("请用设备查看SSID:%s的AP是否可以上网，是[确认]，否[取消]", wifiPara.getSsid()) == ENTER) {
			gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "line %d:信道异常测试失败", Tools.getLineInfo());
			return;
		}
//		gui.cls_printf("断开蓝牙...".getBytes());
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg1_record(TAG, "channelAbnormal", g_keeptime, "信道异常测试通过");

	}

	/**
	 * 蓝牙底座重启后原先设置不应改变 如wifi信息、蓝牙名称
	 */
	public void powerAbnormal() {
		// 2017/6/13 暂不支持 以后会支持
		gui.cls_show_msg("蓝牙底座断电重启后,设备能搜索到底座释放的wifi,并且稳定上网使用,运行通过视为本条通过,按任意键继续");
	}

	/**
	 * //蓝牙重命名后仍可使用，之后断电具有记忆功能
	 */
	public void renameAbnormal() {
		/* private & local definition */
		int ret = -1;

		/* process body */
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		if ((ret = nlBluetooth.btSetLocalName("N7NL55555555")) != 0) {
			gui.cls_show_msg1_record(TAG, "renameAbnormal", g_keeptime, "line %d:蓝牙重命名出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("蓝牙底座重命名为N910-BTDESK-55555555,运行压力部分应成功,异常断电后重启,查看蓝牙命名是否成功,运行功能部分应无异常 ,按任意键继续");

	}

	/**
	 * 未接入物理链路，该模块操作应失败，如串口、网线
	 */
	public void linkAbnormal() {
		gui.cls_show_msg("不插入串口时,运行功能RS232物理串口和USB虚拟串口应失败;不插入网线,运行功能以太网和以太网+wifiAp应失败;插入串口或网线,则应成功 ,按任意键继续");
	}

	/**
	 * 链路未连接操作各模块应失败(软件连) RS232 蓝牙链路未连接，应报失败
	 */
	public void BTdisconnectAbnormal() {
		gui.cls_show_msg1(g_keeptime, "蓝牙链路未连接或处于回连状态,RS233波特率选择115200,进行RS233物理串口操作应失败，反之测试不通过");
		gui.cls_printf("断开蓝牙...".getBytes());
		nlBluetooth.disconnect();
		BpsBean.bpsValue = 115200;
		usbAndRS232(0);
	}

	/**
	 * 链路断开后，操作各模块应失败 ,USB模块
	 */
	public void BTdisconnectAbnormal2() {
		/* private & local definition */
		byte[] buf = new byte[BUFSIZE_SERIAL];
		byte[] rbuf = new byte[BUFSIZE_SERIAL];

		StringBuffer outReceiveLen1 = new StringBuffer();
		StringBuffer outReceiveLen2 = new StringBuffer();
		int ret = -1;
		int portType = 8;
		BpsBean.bpsValue = 115200;

		// 蓝牙连接
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg1(1, "读写过程中，蓝牙断开，断开后续操作应失败，插入USB串口线，选择波特率115200");
		gui.cls_show_msg1(1, "串口多种配置初始化...");
		usbConfig(portType);
		if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:打开USB串口失败(%d),串口配置为(%s)",
					Tools.getLineInfo(), ret, BpsBean.bpsValue + ",8,N,1");
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("串口写数据...".getBytes());
		// 写数据（发送数据）
		gui.cls_show_msg("请确保POS和PC已通过USB/RS232线连接，并开启PC端的AccessPort工具，完成任意键继续");
		for (int j = 0; j < buf.length; j++)
			buf[j] = (byte) (Math.random() * 256);

		if ((ret = nlBluetooth.portWrite(portType, BUFSIZE_SERIAL, buf)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:写数据失败(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 判断指定串口发送缓冲区是否为空
		if ((ret = nlBluetooth.portTxSendOver(portType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口发送缓冲区预期为空，实际(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		gui.cls_printf("串口读数据...".getBytes());
		// 读数据（接收数据）
		gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并发送，完成任意键继续");
		// 取输入缓存
		if ((ret = nlBluetooth.portReadLen(portType, outReceiveLen2)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口取缓存区数据失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 读数据（接收数据）
		Arrays.fill(rbuf, (byte) 0);
		// 断开蓝牙
		gui.cls_printf("断开蓝牙操作".getBytes());
		nlBluetooth.disconnect();
		gui.cls_show_msg1(2, "串口读数据应失败");
		if ((ret = nlBluetooth.portRead(portType, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen1, rbuf)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口读数据失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 比较所取得的输入缓冲的数据长度
		if (!((outReceiveLen2.toString()).equals(outReceiveLen1.toString()))) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口取缓存区数据与预期大小不符合,预期%s,实际%s",
					Tools.getLineInfo(), outReceiveLen2.toString(), outReceiveLen1.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 比较读写数据数据内容
		if (!Tools.memcmp(buf, rbuf, BUFSIZE_SERIAL)) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口读写数据不一致",
					Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 清USB串口缓存
		gui.cls_show_msg1(1, "串口清缓存...");
		if ((ret = nlBluetooth.portClrBuf(portType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口清缓存失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 关闭串口
		gui.cls_show_msg1(1, "关闭串口...");
		if ((ret = nlBluetooth.portClose(portType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "line %d:USB串口关闭失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "蓝牙断开后,后续操作应失败,反正测试不通过");
	}

	/**
	 * 多设备连接一底座,应只有一个手机连接成功
	 */
	private void moreToOneAbnormal() {
		int ret = -1;

		// case1: 2台POS同时连接一台未连接的底座
		if (gui.cls_show_msg("准备好两台Android设备，两台设备均配置为同一蓝牙底座且底座处于未连接状态，两台设备进入本case，已完成[确认]，退出[取消]") == ENTER) {
			gui.cls_show_msg1(1, "连接蓝牙...");
			if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
				gui.cls_show_msg1_record(TAG, "BTdisconnectAbnormal2", g_keeptime, "连接蓝牙底座失败(%d)", Tools.getLineInfo(),
						ret);
				if (!GlobalVariable.isContinue)
					return;
			} else
				gui.cls_show_msg1(2, "本设备连接蓝牙底座成功");
			gui.cls_show_msg("一台Android设备建立连接成功，一台Android设备建立连接失败，则视为测试通过，反之测试不通过");
		}

		/*
		 * // case2: 2台POS同时回连一台底座 if (gui.cls_show_msg(
		 * "准备好两台Android设备，两台设备均曾与同一蓝牙底座连接成功,且构造底座处于回连状态，两台设备进入本case，已完成[确认]，退出[取消]") ==
		 * ENTER) { gui.cls_show_msg1(1, "回连连接中..."); if ((ret =
		 * nlBluetooth.startAccept()) != NDK_OK) { gui.cls_show_msg1_record(TAG,
		 * "BTdisconnectAbnormal2", g_keeptime, "蓝牙底座回连失败(%d)",
		 * Tools.getLineInfo(),ret); if (!GlobalVariable.isContinue) return; } else
		 * gui.cls_show_msg1(2, "本设备回连蓝牙底座成功");
		 * gui.cls_show_msg("一台Android设备回连成功，一台Android设回连失败，则视为测试通过，反之测试不通过"); }
		 */

		gui.cls_show_msg("多设备连接或回连一底座，则视为测试通过，反之测试不通过");
	}

	/**
	 * 一设备搜索多底座，多台以太网mac、WifiAp mac、蓝牙mac都不相同 zhangxj
	 */
	public void oneToMoreMacAbnormal() {
		/* private & local definition */
		int ret = -1;
		StringBuffer ipAddr = new StringBuffer();
		StringBuffer gateway = new StringBuffer();
		StringBuffer subnetMask = new StringBuffer();
		StringBuffer dns = new StringBuffer();
		StringBuffer ethMacAddr = new StringBuffer();
		StringBuffer wifiApMacAddr = new StringBuffer();

		/* process body */
		gui.cls_show_msg("对两个及其以上的底座进行本测试点测试，预期不同的蓝牙底座，其以太网mac、WifiAp mac、蓝牙mac都不相同，完成任意键继续");
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		String mac = nlBluetooth.getConnectedDeviceAddressA();
		gui.cls_show_msg("请确保蓝牙底座连接以太网线，完成任意键继续");
		config.netTransConfig(ethernetPara);
		if (ethernetPara.isDHCPenable()) {
			// 动态，使用DHCP获取网络地址
			if ((ret = nlBluetooth.netDHCP()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:使用DHCP获取网络地址失败(%d)",
						Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 获取网络地址
			if ((ret = nlBluetooth.ethGetNetAddr(ipAddr, gateway, subnetMask, dns)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:获取网络地址失败(%d)",
						Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		} else {
			// 静态 "192.168.30.1;192.168.30.4;192.168.30.5"
			ret = nlBluetooth.ethSetAddress(ethernetPara.getLocalIp(), ethernetPara.getNetMask(),
					ethernetPara.getGateWay(), ethernetPara.getDns1());
			if (ret != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:静态设置网络地址失败(%d)",
						Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 获取网络地址
			if ((ret = nlBluetooth.ethGetNetAddr(ipAddr, gateway, subnetMask, dns)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:获取网络地址失败(%d)",
						Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 校验
			if (!ipAddr.toString().equals(ethernetPara.getLocalIp())
					|| !subnetMask.toString().equals(ethernetPara.getNetMask())
					|| !gateway.toString().equals(ethernetPara.getGateWay())
					|| !dns.toString().equals(ethernetPara.getDns1())) {
				gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line:%d,获取网络地址错误(%s,%s,%s,%s)",
						Tools.getLineInfo(), ipAddr, gateway, subnetMask, dns);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		// 获取以太网mac
		if ((ret = nlBluetooth.ethGetMacAddr(ethMacAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line:%d,获取以太网MAC地址参数失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		StringBuffer ssid = new StringBuffer();
		// 初始化就有ssid和密码
		if ((ret = nlBluetooth.wifiApInit("192.168.2.1", "255.255.255.0")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:初始化wifi出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		// 获取WifiAp的mac地址
		if ((ret = nlBluetooth.wifiApGetMacAddr(wifiApMacAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:获取wifi mac地址出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if ((ret = nlBluetooth.wifiApGetSsid(ssid)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:获取wifi名称出错(%d,%s)",
					Tools.getLineInfo(), ret, ssid.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		wifiPara.setSsid(ssid.toString());
		myactivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				set_dongle_wifi(wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 设置加密模式
		gui.cls_show_msg1(1, "加密模式：" + encMode);
		if ((ret = nlBluetooth.wifiApSetWpa(encMode)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:设置加密模式出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 设置是否隐藏 0-不隐藏 1-隐藏
		int hideFlag = wifiPara.isScan_ssid() == true ? 0 : 1;
		if ((ret = nlBluetooth.wifiApSetHideSsid(hideFlag)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wifiApFun", g_keeptime, "line %d:设置是否隐藏出错(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 设置完毕需重启wifi
		gui.cls_show_msg1(1, "重启wifi");
		if ((ret = nlBluetooth.wifiApReboot()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:重启wifi出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 设置名称 新密码 旧密码 dns 信道
		if ((ret = nlBluetooth.wifiApSetInfo(ssid.toString(), wifiOldPwd, wifiPara.getPasswd(), wifiPara.getDns1(),
				wifiPara.getChannel())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:设置WifiAp出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 获取WifiAp mac
		wifiApMacAddr.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetMacAddr(wifiApMacAddr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_keeptime, "line %d:获取wifi mac地址出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 断开蓝牙
//		gui.cls_show_msg1(1, "断开蓝牙");
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg1_record(TAG, "oneToMoreMacAbnormal", g_time_0,
				"此蓝牙底座的蓝牙mac为%s\n以太网mac为%s\nWifiAp mac为%s\n打开result记录对比多台蓝牙底座的蓝牙mac、以太网mac和WifiAp mac各不相同则通过，反之视为不通过，任意键退出",
				mac, ethMacAddr, wifiApMacAddr);

	}

	/**
	 * 兼容性测试，wifiAP设置黑白名单过滤功能 xuess
	 */
	public void dongleCompatibility() {
		int ret = -1;
		/* private & local definition */
		List<String> wifiBlackList = new ArrayList<String>();
		List<String> wifiWhiteList = new ArrayList<String>();
		int infoType = -1; // 设置黑白名单,0白名单,1黑名单
		int modeType = -1; // 设置过滤模式,0黑名单,1白名单，2关闭
		int type = -1; // 添加或删除 0-添加 1-删除
		int mCount = 967; // 希望获取的设备个数(传递967则获取全部黑名单)
		int number = 0; // 设备个数
		StringBuffer filter = new StringBuffer();// 过滤模式
		StringBuffer mSum = new StringBuffer(); // 黑白名单个数(输出参数)
		StringBuffer outInfo = new StringBuffer();// 黑白名单列表(输出参数)
		/* process body */
		gui.cls_show_msg("测试前请长按底座reset键初始化底座，清除黑白名单及过滤模式设置，待蓝灯灭后又开始闪烁，完成任意键继续");
		// 蓝牙连接
		if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:连接蓝牙失败（%d）", Tools.getLineInfo(),
					ret);
			return;
		}
		/*
		 * if(!nlBluetooth.isConnectedA()) { gui.cls_show_msg1_record(TAG,
		 * "dongleCompatibility",g_keeptime, "line %d:获取蓝牙连接状态失败（false）",
		 * Tools.getLineInfo()); return; }
		 */
		// wifiAp
		gui.cls_show_msg("请确保蓝牙底座已插入[网线]，完成任意键继续");
		// 使用DHCP获取网络地址
		if ((ret = nlBluetooth.netDHCP()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line:%d:使用DHCP获取网络地址失败(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		if ((ret = wifiApFun()) != NDK_OK) {
			if (!GlobalVariable.isContinue)
				return;
		}

		// 直接用文件方式判断黑白名单文件是否已放置
		File fileBlack = new File(Environment.getExternalStorageDirectory().getPath() + "/wifiblacklist.json");
		File fileWhite = new File(Environment.getExternalStorageDirectory().getPath() + "/wifiwhitelist.json");
		if (fileBlack.exists() == false || fileWhite.exists() == false) {
			gui.cls_show_msg("请先在SD卡根目录下放置wifiblacklist.json和wifiwhitelist.json，完成后按任意键继续");
		}

		// 获取黑白名单
		wifiBlackList = getInfoFromJson(GlobalVariable.sdPath + "wifiblacklist.json");
		wifiWhiteList = getInfoFromJson(GlobalVariable.sdPath + "wifiwhitelist.json");
		// 0前置，将要测试的黑白名单设置进底座
		// 将设备MAC添加至黑名单
		type = 0;
		infoType = 1;
		number = wifiBlackList.size() - 1;
		String blackMacStr = wifiBlackList.get(wifiBlackList.size() - 1);
		gui.cls_printf("将wifiblacklist文件中的MAC地址加入黑名单...".getBytes());
		if ((ret = nlBluetooth.wifiApSetBlackList(infoType, type, number, blackMacStr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:添加MAC到黑名单出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 将设备MAC添加至白名单
		infoType = 0;
		number = wifiWhiteList.size() - 1;
		String whiteMacStr = wifiWhiteList.get(wifiWhiteList.size() - 1);
		gui.cls_printf("将wifiwhitelist文件中的MAC地址加入白名单...".getBytes());
		if ((ret = nlBluetooth.wifiApSetBlackList(infoType, type, number, whiteMacStr)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:添加MAC到白名单出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}

		// case1 设备恢复出厂后默认应是未开启过滤模式
		gui.cls_printf("初始为未开启过滤模式，获取当前过滤模式...".getBytes());
		if ((ret = nlBluetooth.wifiApGetFilter(filter)) != NDK_OK || !("2".equals(filter.toString()))) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取过滤模式出错(ret=%d,filter=%s)",
					Tools.getLineInfo(), ret, filter.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 修改为具体的AP名字
		if (gui.cls_show_msg("请测试任意设备是否均能正常连接AP:%s，密码：%s，[确认]是，[其他]否", wifiPara.getSsid(),
				wifiPara.getPasswd()) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:过滤模式未开启测试失败",
					Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2.1 设置黑名单，进行连接测试
		// 显示列表无误
		infoType = 1;
		gui.cls_printf("获取当前黑名单列表...".getBytes());
		if ((ret = nlBluetooth.wifiApGetBlackWhiteList(infoType, mCount, mSum, outInfo)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取黑名单出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("获取的黑名单设备MAC列表为：\n%s\n[确认]是，[其他]否", separateMacList(mSum, outInfo)) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取黑名单列表测试失败",
					Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// 设置过滤模式为禁止黑名单里的设备上网
		modeType = 0;
		gui.cls_printf("设置过滤模式为禁止黑名单里的设备上网...".getBytes());
		if ((ret = nlBluetooth.wifiApSetFilter(modeType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:设置过滤模式为禁止黑名单里的设备上网出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 获取过滤状态
		gui.cls_printf("获取当前过滤模式...".getBytes());
		filter.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetFilter(filter)) != NDK_OK || !("0".equals(filter.toString()))) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取过滤模式出错(ret=%d,filter=%s)",
					Tools.getLineInfo(), ret, filter.toString());
			if (!GlobalVariable.isContinue)
				return;
		}

		if (gui.cls_show_msg("请测试wifiblacklist文件内的设备是否均无法连接AP:%s，密码：%s，不在黑名单的其余设备正常连接，[确认]是，[其他]否", wifiPara.getSsid(),
				wifiPara.getPasswd()) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:设置黑名单测试失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case2.2 从黑名单移除设备，进行连接测试
		infoType = 1;
		type = 1;
		number = 1;
		gui.cls_printf("将wifiblacklist文件中的第一个MAC地址从黑名单中移除...".getBytes());
		if ((ret = nlBluetooth.wifiApSetBlackList(infoType, type, number, wifiBlackList.get(0).toString())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:移除MAC出黑名单出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 显示列表无误
		outInfo.setLength(0);
		gui.cls_printf("获取当前黑名单列表...".getBytes());
		mSum.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetBlackWhiteList(infoType, mCount, mSum, outInfo)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取黑名单出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("已从黑名单中移除wifiblacklist文件内的第一个设备地址，此时获取的黑名单设备MAC列表为：\n%s\n[确认]是，[其他]否",
				separateMacList(mSum, outInfo)) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取黑名单列表测试测试失败",
					Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}

		if (gui.cls_show_msg("请测试wifiblacklist文件内的设备，是否第一个正常连接AP:%s，密码：%s，其余均失败，且不在黑名的其他设备均正常连接，[确认]是，[其他]否",
				wifiPara.getSsid(), wifiPara.getPasswd()) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:从黑名单设备测试失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case3.1 设置白名单，进行连接测试
		// 显示列表无误
		infoType = 0;
		outInfo.setLength(0);
		gui.cls_printf("获取当前白名单列表...".getBytes());
		mSum.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetBlackWhiteList(infoType, mCount, mSum, outInfo)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取白名单出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("获取的白名单设备MAC列表为：\n%s\n[确认]是，[其他]否", separateMacList(mSum, outInfo)) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取白名单列表测试失败",
					Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// 设置过滤模式为只允许白名单里的设备上网
		modeType = 1;
		gui.cls_printf("设置过滤模式为只允许白名单里的设备上网...".getBytes());
		if ((ret = nlBluetooth.wifiApSetFilter(modeType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:设置过滤模式为只允许白名单里的设备上网出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 获取过滤状态
		gui.cls_printf("获取当前过滤模式...".getBytes());
		filter.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetFilter(filter)) != NDK_OK || !("1".equals(filter.toString()))) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取过滤模式出错(ret=%d,filter=%s)",
					Tools.getLineInfo(), ret, filter.toString());
			if (!GlobalVariable.isContinue)
				return;
		}

		if (gui.cls_show_msg("请测试wifiwhitelist文件内的设备是否正常连接AP:%s，密码：%s，不在白名单的其余设备均无法连接，[确认]是，[其他]否", wifiPara.getSsid(),
				wifiPara.getPasswd()) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:设置白名单测试失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case3.2 从白名单移除设备，进行连接测试
		infoType = 0;
		type = 1;
		number = 1;
		gui.cls_printf("将wifiwhitelist文件中的第一个MAC地址从白名单中移除...".getBytes());
		if ((ret = nlBluetooth.wifiApSetBlackList(infoType, type, number, wifiWhiteList.get(0).toString())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:移除MAC出白名单出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 显示列表无误
		outInfo.setLength(0);
		gui.cls_printf("获取当前白名单列表...".getBytes());
		mSum.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetBlackWhiteList(infoType, mCount, mSum, outInfo)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取白名单出错(%d)", Tools.getLineInfo(),
					ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("已从白名单中移除wifiwhitelist文件内的第一个设备地址，此时获取的白名单设备MAC列表为：\n%s\n【确认】是，【其他】否",
				separateMacList(mSum, outInfo)) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取白名单列表测试测试失败",
					Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		if (gui.cls_show_msg("请测试wifiwhitelist文件内的设备，是否第一个无法连接wifi，其余均能正常连接，且不在白名单的其余设备均无法连接【确认】是，【其他】否") != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:从白名单移除设备测试失败",
					Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// case4 关闭过滤模式，进行连接测试
		// 设置过滤模式
		modeType = 2;
		gui.cls_printf("设置过滤模式为关闭过滤...".getBytes());
		if ((ret = nlBluetooth.wifiApSetFilter(modeType)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:关闭过滤功能出错(%d)",
					Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 获取过滤状态
		gui.cls_printf("获取当前过滤模式...".getBytes());
		filter.setLength(0);
		;
		if ((ret = nlBluetooth.wifiApGetFilter(filter)) != NDK_OK || !("2".equals(filter.toString()))) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:获取过滤模式出错(ret=%d,filter=%s)",
					Tools.getLineInfo(), ret, filter.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		if (gui.cls_show_msg("请测试任意设备是否均能正常连接AP:%s，密码:%s，[确认]是，[其他]否", wifiPara.getSsid(),
				wifiPara.getPasswd()) != ENTER) {
			gui.cls_show_msg1_record(TAG, "dongleCompatibility", g_keeptime, "line %d:关闭过滤模式测试失败", Tools.getLineInfo());
			if (GlobalVariable.isContinue == false)
				return;
		}
		// 关闭蓝牙
//		gui.cls_printf("断开蓝牙...".getBytes());
//		nlBluetooth.disconnectA() ;
		gui.cls_show_msg("兼容性测试结束，前述案例点全部通过才视为测试通过");

	}

	// 将返回的sb中的分割成每个长度为17的MAC地址
	public static String separateMacList(StringBuffer outSum, StringBuffer outInfo) {
		StringBuffer macList = new StringBuffer();
		int mCount = Integer.valueOf(outSum.toString());
		String outData = outInfo.toString();
		Log.e("MacList", "maclist:" + outData);
		outData = "0" + outData;
		for (int i = 0; i < mCount; i++) {
			outData = outData.substring(1, outData.length());
			String mac = outData.substring(17 * i, 17 * (i + 1));

			macList.append(mac).append("\n");
			Log.e("MacList", "mac" + i + ":" + mac);
		}
		return macList.toString();
	}

	// 将获取的连接设备信息写入txt文件
	public int writeDesInfo(StringBuffer sb) {
		String nowtime = Tools.getSysNowTime();
		sb.insert(0, "IP address        MAC address            Device    Device name             Online time\r\n");
		sb.insert(0, "\r\n\r\n---------------" + nowtime + "---------------\r\n");
		String str = sb.toString();
		str = str.replace("\n", "\r\n");
		FileSystem fileSystem = new FileSystem();
		String FILE = GlobalVariable.sdPath + "wifiApDevicesInfo.txt";
		if (fileSystem.JDK_FsOpen(FILE, "w") < 0) {
			gui.cls_show_msg1_record(TAG, "writeDesInfo", g_keeptime, "line %d: 打开文件失败,请查看文件系统是否异常",
					Tools.getLineInfo());
			return NDK_ERR;
		} else {
			if (fileSystem.JDK_FsWrite(FILE, str.getBytes(), str.getBytes().length, 2) != str.getBytes().length) {
				gui.cls_show_msg1_record(TAG, "writeDesInfo", g_keeptime, "line %d:写入文件失败...", Tools.getLineInfo());
				return NDK_ERR;
			}

		}
		return NDK_OK;
	}

	// 从json文件中获取所有对象,返回字符串
	public List<String> getInfoFromJson(String path) {
		List<String> macList = new ArrayList<String>();
		StringBuffer mac = new StringBuffer();

		String jsonString = ReadFile(path);// 获得json配置文件的内容
		JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
		JsonArray blackmacList = jsonObject.get("MacList").getAsJsonArray();
		for (JsonElement element : blackmacList) {
			JsonObject macObject = element.getAsJsonObject();
			macList.add(macObject.get("MAC").getAsString());
			mac.append(macObject.get("MAC").getAsString());
		}
		macList.add(mac.toString());
		return macList;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (nlBluetooth != null) {
			nlBluetooth.disconnect();
			nlBluetooth.release(myactivity, null, true);
		}

	}

	// 从模式连接
	public void conn() {
		long startTime = System.currentTimeMillis();
		while (Tools.getStopTime(startTime) < 30) {
			if (nlBluetooth.isConnectedA())
				break;
		}
		if (!nlBluetooth.isConnectedA()) {
			gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:获取蓝牙连接状态失败（false）", Tools.getLineInfo());
			return;
		}

		// 获取bps
		StringBuffer sb = new StringBuffer();
		if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "systest74", g_keeptime, "line %d:获取蓝牙波特率失败(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		LoggerUtil.e("btGetTransPortA()=" + sb);
		BpsBean.bpsValue = Integer.valueOf(sb.toString());// 初始波特率
		// 设置超时时间
		DATA_TIMEOUT = (DATA_BUFFER * 8 % BpsBean.bpsValue == 0 ? DATA_BUFFER * 8 / BpsBean.bpsValue
				: DATA_BUFFER * 8 / BpsBean.bpsValue + 1) + 10;

	}

	//
	private static Timer mTimer; // 计时器，每1秒执行一次任务
	private static MyTimerTask mTimerTask; // 计时任务，判断是否未操作时间到达5s

	private long interval = 62 * 1000;// 两分钟后改为一分钟一次

	// 开始计时
	protected void startTimer() {
		mTimer = new Timer();
		mTimerTask = new MyTimerTask();

		mTimer.schedule(mTimerTask, interval);
	}

	private class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			nextsend = true;
			stopTimer();
		}
	}

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

}
