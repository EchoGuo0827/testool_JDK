package com.example.highplattest.systest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.aidl.OnSearchListener;
import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.NlBluetooth.util.LogUtil;

/************************************************************************
 * 
 * file name : SysTest103.java description : 蓝牙底座数据到命令通道流程和速率 history : 变更记录
 * 变更时间 变更人员 蓝牙底座新增配置模式2-Numeric Comparison。 20200506 魏美杰 蓝牙底座从模式连接增加过滤默认地址
 * 20200518 魏美杰 修改提示语 20200521 陈丁 蓝牙底座新增WifiAP休眠功能 20200715 翁凯健
 * 新增蓝牙底座休眠唤醒测试，根据蓝牙底座休眠时电流与唤醒后电流差值来判断蓝牙底座进入休眠模式
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

@SuppressLint("NewApi")
public class SysTest103 extends DefaultFragment {

	private final String TESTITEM = "全功能蓝牙底座补充";
	private final String TAG = SysTest103.class.getSimpleName();
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	// 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Config config;
	// 公共界面显示
	private Gui gui;
//		private final int DATA_TIMEOUT = 15;//透传接收超时时间
	private int DATA_TIMEOUT = 10;// 透传接收超时时间,根据波特率大小与数据量计算，4k+115200=10s
	private int COM_TIMEOUT = 15;// 串口接收超时时间,根据波特率大小与数据量计算
	int isopen; // me66 1-false:串口打开失败 2-true 串口打开成功
	boolean IS_DUAL = true;
	int i = 0;
	int len = 0;
	int[] bps = { 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400 };
	int DATA_kb = 1024;
	public final int DATA_BUFFER = 1024 * 4;
	byte[] g_readbuf = new byte[DATA_kb];
	byte[] hidData = new byte[DATA_kb];
	int iscon = 0;
	String data;
	int pairmode = 0;// 蓝牙底座配对模式，0-Just Works，2-Numeric Comparison，新增20200506 魏美杰

	public final int BUFSIZE_SERIAL = 1024 * 16;// 串口通讯最大16k

	boolean isSleep = false;
	int ret = 0;
//		String data2;
//		DateFormat df = new SimpleDateFormat("HH:mm:ss");
//		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");  
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

	public void systest103() {
		gui = new Gui(myactivity, handler);
		// 全局变量初始化操作
		nlBluetooth = BluetoothController.getInstance();

		OnSearchListener onSearchListener = new OnSearchListener.Stub() {
			@Override
			public void onFinish() throws RemoteException {
			}

			@Override
			public void onDeviceFound(String arg0, String arg1) throws RemoteException {
			}

			@SuppressLint("NewApi")
			@Override
			public void onStatusChange(int arg0, int arg1, String arg2, String arg3) throws RemoteException {
				iscon = arg0;
				if (iscon == 7) {
//						Date curDate =  new Date(System.currentTimeMillis()); 
//						data=formatter.format(curDate);
					LoggerUtil.e("当前时间为===" + data);

				}
			}

			@Override
			public void onDataReceive(int arg0, byte[] data) throws RemoteException {
				LoggerUtil.e("接收数据广播,双通道");
//	            	len=len+data.length;
//	        		System.arraycopy(data, 0,g_readbuf, i, data.length);  
//	        		i=i+data.length;
				if (data != null) {
					g_readbuf = data;
//						String data2=new String(g_readbuf);
//						LoggerUtil.e("g_readbuf======"+data2);
				}
//					g_readbuf=data;
//					String data2=new String(g_readbuf);
//					LoggerUtil.e("g_readbuf======"+data2);

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
		config = new Config(myactivity, handler);
		GlobalVariable.isDongle = true;
		initLayer();

		while (true) {

			int nkeyIn = gui.cls_show_msg(
					"N910蓝牙底座测试\n0.配置\n1.数据到命令通道流程性能\n2.USB外设插拔的恢复时间\n3.Pos端蓝牙断开连接恢复时间\n4.底座断电引起的POS重新恢复连接的成功率\n5.日志功能测试\n6.数据到命令通道流程压力\n7.USB-Hub功能测试"
					+ "\n8.蓝牙底座休眠测试");
			switch (nkeyIn) {
			case '0':
				int nKeyConfig = gui
						.cls_show_msg("配置\n0.蓝牙选择\n1.底座从模式连接\n2.设置波特率\n3.设置蓝牙底座配对模式\n4.设置蓝牙AP开关\n5.设置蓝牙底座休眠模式");
				switch (nKeyConfig) {
				case '0':
					nlBluetooth.setLog(true);
					// Log.d("APStatus: ", nlBluetooth.wifiApGetStatus(new StringBuffer("APStatus:
					// "))+"");
					dongleConfig();
					break;
				case '1':
					if ("".equals(getBtAddr()) || "34:87:3d:14:95:06".equals(getBtAddr())) // 蓝牙地址为默认地址时应提示重新配置
					{
						gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
						break;
					}
					// 判断连接状态是否true
					if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
						gui.cls_show_msg1_record(TAG, "systest103", g_keeptime, "line %d:连接蓝牙失败(false)",
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
						gui.cls_show_msg1_record(TAG, "systest103", g_keeptime, "line %d:获取蓝牙连接状态失败（false）",
								Tools.getLineInfo());
						break;
					}

					// 获取bps
					StringBuffer sb = new StringBuffer();
					if ((ret = nlBluetooth.btGetTransPortA(sb)) != NDK_OK) {
						gui.cls_show_msg1_record(TAG, "systest103", g_keeptime, "line %d:获取蓝牙波特率失败(ret=%d)",
								Tools.getLineInfo(), sb.toString());
						break;
					}
					LoggerUtil.e("btGetTransPortA()=" + sb);
					BpsBean.bpsValue = Integer.valueOf(sb.toString());// 初始波特率
					// 设置超时时间
					DATA_TIMEOUT = (DATA_BUFFER * 8 % BpsBean.bpsValue == 0 ? DATA_BUFFER * 8 / BpsBean.bpsValue
							: DATA_BUFFER * 8 / BpsBean.bpsValue + 1) + 10;
					float endtime = Tools.getStopTime(starttime2);
					LoggerUtil.e("endtime" + endtime);
					gui.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,确认请按任意键继续.连接时长为%4f" + "s", endtime);
					break;
				case '2':// 设置波特率
					setBps();
					break;
				case '3':
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
						gui.cls_show_msg1_record(TAG, "systest103", g_keeptime, "line %d:更改配对模式失败(ret=%d)",
								Tools.getLineInfo(), ret);
						return;
					}
					if ((removePair(getBtAddr())) == false) {
						gui.cls_show_msg1_record(TAG, "systest103", g_keeptime, "line %d:取消配对失败", Tools.getLineInfo());
						return;
					}
					SystemClock.sleep(2 * 1000);
					if ((nlBluetooth.isConnectedA()) == true) {
						gui.cls_show_msg1_record(TAG, "systest103", g_keeptime, "line %d:连接状态与预期不符",
								Tools.getLineInfo());
						return;
					}
					gui.cls_show_msg("蓝牙底座连接方式已更改，请重新连接配对");
				case '4':// 设置wifiAP是否开启
					boolean wifiApOpen = false;
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
							gui.cls_show_msg("wifiAP已关闭，按确认退出");
							break;
						} else if (res.toString().equals("1")) {
							gui.cls_show_msg("wifiAP已开启，按确认退出");
							break;
						} else {
							gui.cls_show_msg1(2, "AP状态错误(%s)", res);
							return;
						}
					case ESC:
						break;
					}
					break;

				case '5':
					wifiAPSleep();// ap休眠功能,by wengkj
					break;
				}
				break;

			case '1':
				datatocmd();// 压力
				break;
			case '2':
				// usb拔插
				usbpull();// 性能
				break;

			case '3':
//				blconanddiscon();// 性能
				break;

			case '4':
				bluetoothrebootcon();// 性能
				break;
			case '5':// 日志功能
				Logfunctiontest();// 已移植
				break;
			case '6':
				datatocmd2();// 压力
				break;

			case '7':// 独立apk已有
				usbFunction();// USB功能,by weimj
				break;
			case '8':
				gui.cls_show_msg("等待30s后看到电流表值下降，按确认继续");
				nlBluetooth.wifiApSetSleep(1);
				if(gui.cls_show_msg("电流表值提高，蓝牙休眠测试结束，按确认退出") != ENTER) {
					gui.cls_show_msg1(2,"蓝牙底座未唤醒");
				}
				
				break;
			default:
				break;

			case ESC:
				intentSys();
				return;
			}
		}
	}

	// 独立apk已添加
	public void wifiAPSleep() {
		while (true) {
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
				gui.cls_show_msg1(4, "已开启休眠功能,休眠状态下电流与唤醒状态下差值在50-60mA");
				

				break;
			case '1':
				if (nlBluetooth.wifiApSetSleep(0) != NDK_OK) {
					gui.cls_show_msg1(2, "关闭休眠失败");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}
				res = new StringBuffer();
				if (nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
					gui.cls_show_msg1(2, "获取休眠状态失败");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
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
					gui.cls_show_msg1(2, "获取休眠状态失败");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}

				if (res.toString().equals("0")) {
					gui.cls_show_msg("已关闭休眠功能,按确认退出");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					break;
				} else if (res.toString().equals("1")) {
					gui.cls_show_msg("已开启休眠功能,按确认退出");
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					break;
				} else {
					gui.cls_show_msg1(2, "休眠状态错误(%s)", res);
					// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
					return;
				}

			case ESC:
				break;
			}
			break;
		}

	}

//		gui.cls_show_msg1(2, "此时底座已进入休眠，底座无法主动发送数据，必须由N910主动" + "唤醒");
//
//		gui.cls_show_msg("按确认键唤醒蓝牙");
//		if ((nlBluetooth.wifiApEnable(1)) != NDK_OK) {
//			// Log.d("ApState: ", a+"");
//			gui.cls_show_msg1(2, "蓝牙唤醒失败");
//			// Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//			return;
//		}
//
//		gui.cls_show_msg("底座休眠测试通过，按确认继续");
//
//	}

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

	/** USB口hid设备数据透传功能 by 20200325 weimj */
	private void usbFunction() {
		String funcName = "usbFunction";
		int portType = 256, availCount = 0;
		int usbInterface = 0;
		int total = 1;
		StringBuffer info = new StringBuffer();
		StringBuffer outLen = new StringBuffer();
		int readLen = 0;
		byte[] recvBuf;

		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg1(2, "测试USB-HUB功能");

//			gui.cls_show_msg("等待30秒后按确认继续，蓝牙底座应进入休眠状态");
//			StringBuffer res = new StringBuffer();
//			if(nlBluetooth.wifiApGetSleepStatus(res) != NDK_OK) {
//				gui.cls_show_msg1(2, "获取休眠状态失败");
//				//Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//				return;
//			}
//			//Log.d("state: ", res.toString());
//			if(!res.toString().equals("1")) {
//				gui.cls_show_msg1(2, "底座休眠状态异常");
//				//Log.v("ap: ", nlBluetooth.wifiApGetStatus(new StringBuffer("ApStatus"))+"");
//				return;
//			}

		// 获取可用接口
		if ((ret = nlBluetooth.usbGetAvailableInfo(info)) != 0) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取可用接口状态错误(ret=%d)", Tools.getLineInfo(), ret);
			return;
		}

		if (info != null)
			availCount = Integer.valueOf(info.toString());
		if (gui.cls_show_msg("可用接口数=%d，测试人员确认是否一致", availCount) != ENTER) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取可用接口错误(count=%d)", Tools.getLineInfo(),
					availCount);
			return;
		}
		LoggerUtil.e(funcName + "可用接口数为" + availCount);

		// 获取指定串口信息
		info = new StringBuffer();
//		if ((ret = nlBluetooth.usbGetInfo(portType, info)) != 0) {
//			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取指定串口信息状态错误(ret=%d)", Tools.getLineInfo(),
//					ret);
//			return;
//		}
		boolean isPort = false;
		int index = 1;
		while (index <= portType) {
			ret = nlBluetooth.usbGetInfo(index, info);
			if (!info.toString().equals("")) {
				Log.d("portType: ", index + "");
				isPort = true;
				break;
			}
			index++;
		}
		if (!isPort) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取指定串口信息状态错误(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		LoggerUtil.e("info=" + info.toString());
		String info1 = null, info2 = null, info3 = null, info4 = null, info5 = null, info6 = null, info7 = null,
				info8 = null, info9 = null, info10 = null;
		if (info.length() > 0) {
			info1 = info.substring(0, 1);// USB设备类型
			info2 = info.substring(1, 2);// USB接口数量
			info3 = info.substring(2, 3);// USB厂家id长度
			info4 = info.substring(3, 3 + Integer.valueOf(info3));// USB厂家id
			info5 = info.substring(3 + Integer.valueOf(info3), 4 + Integer.valueOf(info3));// USB供应商id长度
			if (Integer.valueOf(info5) != 0) {
				info6 = info.substring(4 + Integer.valueOf(info3), 4 + Integer.valueOf(info3) + Integer.valueOf(info5));// USB供应商id
			} else {
				info6 = null;
			}
			info7 = info.substring(4 + Integer.valueOf(info3) + Integer.valueOf(info5),
					5 + Integer.valueOf(info3) + Integer.valueOf(info5));// USB制造商名长度
			if (Integer.valueOf(info7) != 0) {
				info8 = info.substring(5 + Integer.valueOf(info3) + Integer.valueOf(info5),
						5 + Integer.valueOf(info3) + Integer.valueOf(info5) + Integer.valueOf(info7));// USB制造商名
			} else {
				info8 = null;
			}
			info9 = info.substring(5 + Integer.valueOf(info3) + Integer.valueOf(info5) + Integer.valueOf(info7),
					6 + Integer.valueOf(info3) + Integer.valueOf(info5) + Integer.valueOf(info7));// USB厂家名长度
			if (Integer.valueOf(info9) != 0) {
				info10 = info.substring(6 + Integer.valueOf(info3) + Integer.valueOf(info5) + Integer.valueOf(info7),
						6 + Integer.valueOf(info3) + Integer.valueOf(info5) + Integer.valueOf(info7)
								+ Integer.valueOf(info9));// USB厂家名
			} else {
				info10 = null;
			}
			String portInfo = "info——\n USB设备类型:" + info1 + "\nUSB接口数量:" + info2 + "\nUSB厂家id:" + info4 + "\nUSB供应商id:"
					+ info6 + "\nUSB制造商名:" + info8 + "\nUSB厂家名" + info10 + "\n";
			if (gui.cls_show_msg("USB设备信息为%s，测试人员确认串口信息是否正确", portInfo) != ENTER) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取串口信息错误(%s)", Tools.getLineInfo(),
						portInfo);
				return;
			}
			LoggerUtil.e(funcName + ",info=" + portInfo);
		} else {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取指定串口信息状态错误", Tools.getLineInfo());
			return;
		}
		// 扫码枪使用到的接口
		portType = index;
		// 判断是否连接外设
		if ((ret = nlBluetooth.usbIsOnline(portType)) != 0) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:判断是否连接外设状态错误(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}

		// 可用接口轮询
		for (int i = 0; i < total; i++) {
			usbInterface = i;
			LoggerUtil.e("usbInterface=" + usbInterface);
			// 前置关闭串口
			nlBluetooth.usbClosePort(portType, usbInterface);
			nlBluetooth.usbCloseHid(portType, usbInterface);

			// 初始化串口
			if ((ret = nlBluetooth.usbOpenPort(portType, usbInterface)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:初始化串口失败（port=%d,ret=%d）",
						Tools.getLineInfo(), portType, ret);
				LoggerUtil.e("usbOpenPort=" + usbInterface + "," + ret);
				continue;
			}
			gui.cls_show_msg1(1, "USB接口=%d测试", usbInterface);

			gui.cls_show_msg("使用扫码枪扫码，扫码完毕后点击任意键继续");
			int time = 0;
			long outTime = System.currentTimeMillis();
			while (time < COM_TIMEOUT * 1000) {
				time = (int) Tools.getStopTime(outTime);
				outLen = new StringBuffer();
				if ((ret = nlBluetooth.usbPortReadLen(portType, usbInterface, outLen)) != 0) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:获取输入缓存长度状态错误(ret=%d)",
							Tools.getLineInfo(), ret);
					return;
				}

				readLen = Integer.parseInt(outLen.toString());
				Log.e("readLen", readLen + "");
				if (readLen != 0) {
					recvBuf = new byte[readLen];
					// usbPortRead
					if ((ret = nlBluetooth.usbPortRead(portType, usbInterface, readLen, 10 * 1000, outLen,
							recvBuf)) != 0) {
						gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:读数据状态错误(ret=%d)",
								Tools.getLineInfo(), ret);
						return;
					}
					String result = new String(recvBuf);
					Log.e("result", result + "");
					if (gui.cls_show_msg("扫码结果=%s,测试人员确认是否正确", result) != ENTER) {
						gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:读数据结果错误(%s)", Tools.getLineInfo(),
								result);
						return;
					}
					break;
				}
				if ((ret = nlBluetooth.usbPortClrBuf(portType, usbInterface)) != 0) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:清串口缓存状态错误(ret=%d)",
							Tools.getLineInfo(), ret);
					Log.e("usbClosePort" + usbInterface, "" + ret);
				}
			}

			// 关闭串口
			if ((ret = nlBluetooth.usbClosePort(portType, usbInterface)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:关闭串口状态错误(ret=%d)", Tools.getLineInfo(),
						ret);
				Log.e("usbClosePort" + usbInterface, "" + ret);
			}

			gui.cls_show_msg1(2, "数据透传测试");

			// 开启指定USB口hid设备数据透传功能
			if ((ret = nlBluetooth.usbOpenHid(portType, 0)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:开启指定USB口hid设备数据透传功能状态错误(ret=%d)",
						Tools.getLineInfo(), ret);
				return;
			}

			if ((gui.ShowMessageBox(("在文件管理中新建文件夹，输入文件名时光标停留在输入框中，此时扫描枪扫条形码，是否在输入框中显示码值且码值一致？").getBytes(),
					(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME)) != BTN_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%sHID-USB模式扫条形码失败", Tools.getLineInfo(),
						TESTITEM);
				return;
			}

			// 关闭指定USB口hid设备数据透传功能
			if ((ret = nlBluetooth.usbCloseHid(portType, usbInterface)) != 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:关闭指定USB口hid设备数据透传功能状态错误(ret=%d)",
						Tools.getLineInfo(), ret);
				return;
			}
		}
		gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "测试通过");
	}

	// 日志功能测试
	private void Logfunctiontest() {
		String logfilepath = "mnt/sdcard/time.log";
		File file = new File(logfilepath);
		StringBuffer sB = new StringBuffer();
		int ret = -100;
		// 测试前置判断蓝牙以及文件
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		if (file.exists()) {
			file.delete();
		}
		gui.cls_show_msg1(2, "测试蓝牙底座日志功能");
		// 测试前置。关闭底座日志，防止开启过日志，影响到测试
		if ((ret = nlBluetooth.btCloseLog()) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:关闭底座日志失败(ret=%d)",
					Tools.getLineInfo(), ret);
			return;

		}
		// 关闭状态下获取日志打开状态 应该返回0
		if ((ret = nlBluetooth.btGetBtLogState(sB)) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:获取日志打开状态错误(ret=%d)",
					Tools.getLineInfo(), ret);
			return;

		}
		// sB应返回0-未开启
		if (!(sB.toString().equals("0"))) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:获取日志打开状态错误,sB===" + sB,
					Tools.getLineInfo());
			return;

		}
		LogUtil.d("sB=" + sB);
		sB = new StringBuffer();
		LogUtil.d("sB==" + sB);
		// 打开蓝牙底座日志
		if ((ret = nlBluetooth.btOpenLog(1 | 2, 1)) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:打开日志失败(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		// 再次打开不同蓝牙底座日志
		if ((ret = nlBluetooth.btOpenLog(10 | 8, 2)) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:打开日志失败(ret=%d)", Tools.getLineInfo(),
					ret);
			return;
		}
		// 打开蓝牙底座日志后再获取日志状态
		if ((ret = nlBluetooth.btGetBtLogState(sB)) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:获取日志打开状态错误(ret=%d)",
					Tools.getLineInfo(), ret);
			return;

		}
		LogUtil.d("ret==" + ret);
		// sB应返回1-已打开
		if (!(sB.toString().equals("1"))) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:获取日志打开状态错误,sB===" + sB,
					Tools.getLineInfo());
			return;

		}
		// 获取底座日志
		if ((ret = nlBluetooth.btGetLog(logfilepath)) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:获取日志失败(ret=%d)", Tools.getLineInfo(),
					ret);
			return;

		}
		// 关闭底座日志
		if ((ret = nlBluetooth.btCloseLog()) != 0) {
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:关闭底座日志失败(ret=%d)",
					Tools.getLineInfo(), ret);
			return;

		}
		if (!file.exists()) {
			// 文件不存在
			gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "line %d:日志文件不存在");
			return;
		}

		gui.cls_show_msg1_record(TAG, "Logfunctiontest", g_keeptime, "测试通过。。。。。");

	}

	// usb拔插恢复时间
	private void usbpull() {
		int cnt = 0, bak = 0, succ = 0;
		int ret;
		float ratetime = 0;
		float rate;
		float frate;
		int portType = 10;
		long endfailtime = 0;
		float endftime = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 测试前置
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg("即将进行Usb拔插恢复测试。请确保usb与外设处于连接状态。按任意键继续。。。");
		// 打开串口
		// 0:成功 其他:失败
		if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) != 0) {
			gui.cls_show_msg1_record(TAG, "usbpull", g_keeptime, "line %d:串口打开失败", Tools.getLineInfo());
			Log.d("ericret=====", ret + " ");
			return;

		}
		Log.d("ericret2=====", ret + " ");
		while (cnt > 0) {
			cnt--;
			gui.cls_show_msg("请拔插usb。。。。。。按任意键继续");
			long outtime = System.currentTimeMillis();
			gui.cls_show_msg1(1, "测试中");
			long starttime = System.currentTimeMillis();
			int time = 0;
			while (time <= 20) {
				time = (int) Tools.getStopTime(outtime);
				if ((ret = nlBluetooth.portOpen(portType, BpsBean.bpsValue + ",8,N,1")) == 0) {
					Log.d("time=====", time + " ");
					break;

				}
				SystemClock.sleep(10);

			}
			long endtime = System.currentTimeMillis();
			if (time >= 20) {
				endfailtime = System.currentTimeMillis();
				endftime = endftime + (endfailtime - starttime);
				gui.cls_show_msg1_record(TAG, "usbpull", g_keeptime, "line %d:串口打开超时", Tools.getLineInfo());
				continue;
			}
//				long endtime=System.currentTimeMillis();
			Log.d("ericret=====", ret + " ");
			ratetime = ratetime + (endtime - starttime);
			succ++;
			gui.cls_show_msg1(2, "测试总次数为%d次，成功%d次，已执行次数为%d次", bak, succ, bak - cnt);
		}
		rate = ratetime / succ;
		gui.cls_show_msg("测试完成总次数为%d次，成功%d次,平均耗时%4fms,失败耗时为%4fms", bak, succ, rate, endftime);

	}

	// 底座异常重启后连接终端 by chending 20190905
	private void bluetoothrebootcon() {
		float timeover = 0;
		float endclosetime = 0;
		float closerate = 0;
		float rate = 0;
		int cnt = 0, bak = 0, succ = 0, close_succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 测试前置
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		gui.cls_show_msg("请确保底座已连接继电器，并且设置好断电供电时间，15s的断开时间。60s的连接时间。按任意键进行底座断电连接。。。。。。。。");

		while (cnt > 0) {
			cnt--;
			gui.cls_show_msg1(2, "当前执行次数为第%d次,已成功%d次", bak - cnt, succ);
			int time = 0;
			long closestarttime = System.currentTimeMillis();
			long outtime = System.currentTimeMillis();
			nlBluetooth.disconnect2();
			while (time < 30) {
				time = (int) Tools.getStopTime(outtime);
				if (!(nlBluetooth.isConnectedA())) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (time >= 30) {
				gui.cls_show_msg1(1, "断开失败");
				continue;
			}
			endclosetime = endclosetime + (System.currentTimeMillis() - closestarttime);
			close_succ++;
			gui.cls_show_msg1(1, "断开。。。。。。即将判断连接。time=%d", time);
//				nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr());
			int time2 = 0;
			while (time2 < 105) {
				time = (int) Tools.getStopTime(outtime);
				if (nlBluetooth.isConnectedA()) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (time >= 105) {
				gui.cls_show_msg1(1, "连接失败");
				continue;
			}
			gui.cls_show_msg1(1, "连接。。。。。。即将判断断开。time2=%d", time2);
			succ++;

		}
		closerate = endclosetime / close_succ;
		gui.cls_show_msg1_record(TAG, "bluetoothrebootcon", long_keeptime, "底座断电重连pos总共测试了%d,成功了%d次,底座断电关闭蓝牙平均耗时%4fms",
				bak, succ, closerate);

	}

	// 数据到命令通道流程性能 by chending 20190904
	private void datatocmd() {

		int porttype = 10;
		float timeover = 0;
		float rate = 0;
		int cnt = 0, bak = 0, succ = 0;
		String patten = "yyyy-MM-dd HH:mm:ss.SSS";
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 测试前置
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 扫码数据
		String sendData1 = "<STX><0055><SET><05><00><PACK=ON><AMOUNT=0.01><MODE=ONCE><SWITCH=ON><TIMEOUT=0060><ETX><2F>";
		String ack1 = "<STX><0009><SET><00><01><00><ETX><5E>";
		String backData1 = "<STX><0032><GET><01><01><00><DATA=135772821147209347><EXT><6B>";
		// 非接数据
		String sendData2 = "<STX><0093><SET><05><00><PACK=ON><AMOUNT=0.01><SYS_TIME=20190423155600><PBOC_QPBOC=00099F0206000000000001><TIMEOUT=0060><ETX><54>";
		String ack2 = "<STX><0009><SET><00><01><00><ETX><5E>";
		String failData = "<STX><0018><SET><01><02><06><QPBOC=404><ETX><1A>";
		String succData = "<STX><0557><SET><01><02><00><QPBOC=02695A0862109468880800069F1E0830303030303030315F3401019F26088757785A9E15DF729F2701809F101307010103A00000010A010000000000A8DC622E9F37042F9C37489F360210C3950500000000009A031905159C01009F02060000000000015F2A02015682027C009F1A0201569F03060000000000009F4005FF80F0B0019F3501228408A0000003330101029F090200209F4104000000049F79060000000000009F3303E0E9C857136210946888080006D30102010000000000000F4F08A0000003330101029F21031521539F3901079B0200009F120B5549435320435245444954500B55494353204352454449549F0608A0000003330101025F2002202FDF75010FDF760400000000><ETX><20>";
		// 扫码非接都用10口 返回数据
		nlBluetooth.startSingleChannelThread(porttype);

		gui.cls_show_msg(
				"即将开始数据到命令通道流程和速率测试--请打开PC端串口工具并且用RS232连接底座，设置好自动发送，并将底座连接ME66。将<STX><0055><SET><05><00><PACK=ON><AMOUNT=0.01><MODE=ONCE><SWITCH=ON><TIMEOUT=0060><ETX><2F>发送。。。。");
		while (cnt > 0) {
			cnt--;
			gui.cls_show_msg1(2, "正在进行数据到命令通道流程和速率测试，总次数%d次，当前第%d次,成功%d次", bak, bak - cnt, succ);
			if (isopen == 0) {
				SystemClock.sleep(1000);
				Log.d("cdcd", "isopenisopen-------------==0");
				continue;
			} else if (isopen == 1) {
				gui.cls_show_msg1(3, "打开串口失败---请重新拔插USB口并且重新进入案例");
				return;
			}
			// 获取从pc端发到底座，底座再发回的扫码数据sendData1
			long starttime = System.currentTimeMillis();
			int time = 0;
			String recDataout1 = "";
			LoggerUtil.e("String g_readbuf=" + recDataout1);
			long outtime = System.currentTimeMillis();
			while (time < COM_TIMEOUT) {
				String recData = new String(g_readbuf);
				recDataout1 = recData;
				time = (int) Tools.getStopTime(outtime);
				if (recData.contains(sendData1)) {
//						LoggerUtil.e("recData="+recData);
					break;

				}
				SystemClock.sleep(10);

			}
			// 从pc接收到数据的计时
			SimpleDateFormat format1 = new SimpleDateFormat(patten);
			String datetime1 = format1.format(new Date());
			// 判断是否真的接收到底座信息，排除由于超时跳出while循环
			if (time >= COM_TIMEOUT) {

				gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:扫码数据从pc端转发到底座。底座发送到pos端超时",
						Tools.getLineInfo());
				continue;
			}

			// 确认接收到扫码数据以后，再由POS发给底座。。底座通过USB口发给Me66
			nlBluetooth.singleSend(recDataout1.getBytes());
			// 发送后计时
			SimpleDateFormat format2 = new SimpleDateFormat(patten);
			String datetime2 = format2.format(new Date());

			LoggerUtil.e("Send recData=" + recDataout1);

			// Me66将数据处理以后再将返回值发给底座，底座通过蓝牙发给POS
			// 重新赋值data

			int time2 = 0;
			String recDataout2 = "";
			long outtime2 = System.currentTimeMillis();
			while (time2 < COM_TIMEOUT) {
				String recData2 = new String(g_readbuf);
				recDataout2 = recData2;
				time = (int) Tools.getStopTime(outtime2);
				if (recData2.contains("DATA=")) {
//						LoggerUtil.e("DATA=="+recData2);
					break;

				}
				SystemClock.sleep(10);
			}
			LoggerUtil.e("recDataout2=" + recDataout2);
			// me66将数据返回计时
			SimpleDateFormat format3 = new SimpleDateFormat(patten);
			String datetime3 = format3.format(new Date());
			// 判断是否真的接收到ME66信息，排除超时原因

			if (time2 >= COM_TIMEOUT) {

				gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:扫码数据从ME66处理完毕发送到POS超时",
						Tools.getLineInfo());
				continue;
			}
			LoggerUtil.e("g_readbuf=" + g_readbuf);
			// pos再将ME66处理完毕的数据通过蓝牙发给底座，底座通过RS232发给PC
			if (!(nlBluetooth.sendDataA(g_readbuf))) {
				gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:ME66处理完毕的数据通过蓝牙发给底座失败",
						Tools.getLineInfo());
				continue;
			}
			// pos发到pc
			SimpleDateFormat format4 = new SimpleDateFormat(patten);
			String datetime4 = format4.format(new Date());
			long endtime = System.currentTimeMillis();
			timeover = timeover + (endtime - starttime);
//					gui.cls_show_msg1(3,"流程..............成功");
			succ++;
			gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime,
					"第%d次。。pc->pos:%s,pos->me66:%s,me66->pos:%s,pos->pc:%s", bak - cnt, datetime1, datetime2, datetime3,
					datetime4);
			if (gui.cls_show_msg("请确认是否进行下一次数据收发。。。。。是[确认],否[其他]") != ENTER) {

				return;
			}

		}
		rate = timeover / bak;
		if (gui.cls_show_msg("请确认串口工具是否有数据返回。。。。。是[确认],否[其他]") != ENTER) {
			gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:串口工具无数据.......", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "datatocmd", long_keeptime, "数据到命令通道流程性能为%4fms\n其中总共测试了%d,成功了%d次", rate, bak,
				succ);

	}

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

	// 流程压力
	private void datatocmd2() {
		int porttype = 10;
		float timeover = 0;
		float rate = 0;
		int cnt = 0, bak = 0, succ = 0;
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		bak = cnt = packet.getLifecycle();// 交叉次数获取
		// 测试前置
		if ("".equals(nlBluetooth.getConnectedDeviceAddressA())) {
			gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		// 扫码数据
		String sendData1 = "<STX><0055><SET><05><00><PACK=ON><AMOUNT=0.01><MODE=ONCE><SWITCH=ON><TIMEOUT=0060><ETX><2F>";
		String ack1 = "<STX><0009><SET><00><01><00><ETX><5E>";
		String backData1 = "<STX><0032><GET><01><01><00><DATA=135772821147209347><EXT><6B>";
		// 非接数据
		String sendData2 = "<STX><0093><SET><05><00><PACK=ON><AMOUNT=0.01><SYS_TIME=20190423155600><PBOC_QPBOC=00099F0206000000000001><TIMEOUT=0060><ETX><54>";
		String ack2 = "<STX><0009><SET><00><01><00><ETX><5E>";
		String failData = "<STX><0018><SET><01><02><06><QPBOC=404><ETX><1A>";
		String succData = "<STX><0557><SET><01><02><00><QPBOC=02695A0862109468880800069F1E0830303030303030315F3401019F26088757785A9E15DF729F2701809F101307010103A00000010A010000000000A8DC622E9F37042F9C37489F360210C3950500000000009A031905159C01009F02060000000000015F2A02015682027C009F1A0201569F03060000000000009F4005FF80F0B0019F3501228408A0000003330101029F090200209F4104000000049F79060000000000009F3303E0E9C857136210946888080006D30102010000000000000F4F08A0000003330101029F21031521539F3901079B0200009F120B5549435320435245444954500B55494353204352454449549F0608A0000003330101025F2002202FDF75010FDF760400000000><ETX><20>";
		// 扫码非接都用10口 返回数据
		nlBluetooth.startSingleChannelThread(porttype);

		gui.cls_show_msg(
				"即将开始数据到命令通道流程和速率测试--请打开串口工具并且用RS232连接底座，设置好自动发送，并将蓝牙底座连接ME66。将<STX><0055><SET><05><00><PACK=ON><AMOUNT=0.01><MODE=ONCE><SWITCH=ON><TIMEOUT=0060><ETX><2F>发送。。。。");
		while (cnt > 0) {
			cnt--;
			gui.cls_show_msg1(2, "正在进行数据到命令通道流程和速率测试，总次数%d次，当前第%d次,成功%d次", bak, bak - cnt, succ);
			if (isopen == 0) {
				SystemClock.sleep(1000);
				Log.d("cdcd", "isopenisopen-------------==0");
				continue;
			} else if (isopen == 1) {
				gui.cls_show_msg1(3, "打开串口失败---请重新拔插USB口并且重新进入案例");
				return;
			}
			// 获取从pc端发到底座，底座再发回的扫码数据sendData1
			long starttime = System.currentTimeMillis();
			int time = 0;
			String recDataout1 = "";
			LoggerUtil.e("String g_readbuf=" + recDataout1);
			long outtime = System.currentTimeMillis();
			while (time < COM_TIMEOUT) {
				String recData = new String(g_readbuf);
				recDataout1 = recData;
				time = (int) Tools.getStopTime(outtime);
				if (recData.contains(sendData1)) {
//						LoggerUtil.e("recData="+recData);
					break;

				}
				SystemClock.sleep(10);

			}
			// 判断是否真的接收到底座信息，排除由于超时跳出while循环
			if (time >= COM_TIMEOUT) {

				gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:扫码数据从pc端转发到底座。底座发送到pos端超时",
						Tools.getLineInfo());
				continue;
			}

			// 确认接收到扫码数据以后，再由POS发给底座。。底座通过USB口发给Me66
			nlBluetooth.singleSend(recDataout1.getBytes());
			// 发送后计时

			LoggerUtil.e("Send recData=" + recDataout1);

			// Me66将数据处理以后再将返回值发给底座，底座通过蓝牙发给POS
			// 重新赋值data

			int time2 = 0;
			String recDataout2 = "";
			long outtime2 = System.currentTimeMillis();
			while (time2 < COM_TIMEOUT) {
				String recData2 = new String(g_readbuf);
				recDataout2 = recData2;
				time = (int) Tools.getStopTime(outtime2);
				if (recData2.contains("DATA=")) {
//						LoggerUtil.e("DATA=="+recData2);
					break;

				}
				SystemClock.sleep(10);
			}
			LoggerUtil.e("recDataout2=" + recDataout2);

			// 判断是否真的接收到ME66信息，排除超时原因

			if (time2 >= COM_TIMEOUT) {

				gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:扫码数据从ME66处理完毕发送到POS超时",
						Tools.getLineInfo());
				continue;
			}
			LoggerUtil.e("g_readbuf=" + g_readbuf);
			// pos再将ME66处理完毕的数据通过蓝牙发给底座，底座通过RS232发给PC
			if (!(nlBluetooth.sendDataA(g_readbuf))) {
				gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:ME66处理完毕的数据通过蓝牙发给底座失败",
						Tools.getLineInfo());
				continue;
			}

			long endtime = System.currentTimeMillis();
			timeover = timeover + (endtime - starttime);
			succ++;
			gui.cls_show_msg1(3, "流程..............成功");

		}
		rate = timeover / bak;
		if (gui.cls_show_msg("请确认串口工具是否有数据返回。。。。。是[确认],否[其他]") != ENTER) {
			gui.cls_show_msg1_record(TAG, "datatocmd", g_keeptime, "line %d:串口工具无数据.......", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "datatocmd", long_keeptime, "总共测试了%d,成功了%d次", bak, succ);

	}

}
