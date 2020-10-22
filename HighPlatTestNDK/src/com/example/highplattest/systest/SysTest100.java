package com.example.highplattest.systest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.newland.SettingsManager;
import android.newland.scan.ScanUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.CheckBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.AYSNCTASK_LIST_ANDROID;
import com.example.highplattest.main.constant.ParaEnum.AYSNCTASK_LIST_K21;
import com.example.highplattest.main.constant.ParaEnum.EM_LED;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_DES;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_MAC;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_HWINFO;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
import com.newland.ndk.TimeNewland;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest100.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20171018
 * directory 		: 
 * description 		: K21及安卓端模块浅并发测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  xuess          20170919         	created
 * 					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 * 					SMART测试新增M0 M1卡        20200828 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest100 extends DefaultFragment 
{
	private final String TESTITEM = "多模块并发(浅并发)";
	public final String CLASS_NAME = SysTest100.class.getSimpleName();
	public static ExecutorService exec = Executors.newFixedThreadPool(20); 
	private Config config;
//	private boolean configFlag = false; //是否自定义执行任务
	private StringBuffer strBuffer = new StringBuffer();
	private ArrayList<CheckBean> list = new ArrayList<CheckBean>();
	private Myadapter adapter = new Myadapter();
	private HashSet<String> checkName = new HashSet<String>();
	private int cycletime = 500;	//自定义任务循环次数,默认500
	private int taskStatus = 0;	//已完成任务
	private int count = 0;
	private Random random = new Random();
	private boolean sleeptest = false;
	private int SLEEPTIME = 25800; //25.8s，略早于k21进入休眠的时间26s左右
	private boolean isCancel = false;
	private boolean isInit = false;
	//卡类
	private _SMART_t rfidType = _SMART_t.CPU_A;
	private _SMART_t iccType = _SMART_t.IC;
	private _SMART_t samType = _SMART_t.SAM1;
	private int felicaChoose=0;
	//安全
	private final String TRANSMIT_KEY = "31313131313131313131313131313131";// 传输密钥
	private String shareKey = "*";
	private String TESTA = new String(new byte[]{0x00});
	private String keyOwner = "default";
	//打印
	private PrintUtil printUtil;
	private final int MAXWAITTIME = 10;
	//ndk文件系统
	private final int BUFLEN = (5*1024);
	private final String file10path = "/appfs/file10";
	private final String[] filepath = new String[] { "/appfs/file0",
			"/appfs/file1", "/appfs/file2"};
	//非ndk文件系统
	private final int BUFLEN_SD = (1024*1024);
	private FileSystem fileSystem = new FileSystem();
	private final String SDFILETEST = GlobalVariable.sdPath+"filetest1";
	//数据通讯
	private int PACKMAXLEN = 10*1024;
	private int linkType = 0;//0为wlan，1为wlm
	private SocketUtil socketUtil;
	private MobilePara mobilePara = new MobilePara();
	private WifiPara wifiPara = new WifiPara();
	private PacketBean sendPacket;
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	//设置
	private SettingsManager settingsManager = null;
	//sys
	private final int TESTTIME = 6;
	private final int sleepTime = 3*1000;
	
	/*重写界面部分*/
	private TextView mtvShow_rfid,mtvShow_icc,mtvShow_mag,mtvShow_prn,mtvShow_sec,mtvShow_fs,mtvShow_wlm_wlan,mtvShow_bt,mtvShow_sdfs,mtvShow_sysconfig,mtvShow_getpin,
					mtvShow_led,mtvShow_sys_delay,mtvShow_sys_msdelay,mtvShow_sys_postime,mtvShow_sys_time,mtvShow_sys_getposinfo,mtvShow_sam,mtvShow_thk88,mtvShow_getpin_input;
	public static final int TEXTVIEW_SHOW_K21 = 200;
	public static final int TEXTVIEW_SHOW_RFIDTASK = TEXTVIEW_SHOW_K21+1;
	public static final int TEXTVIEW_SHOW_ICCTASK = TEXTVIEW_SHOW_K21+2;
	public static final int TEXTVIEW_SHOW_SAMTASK = TEXTVIEW_SHOW_K21+3;
	public static final int TEXTVIEW_SHOW_MAGTASK = TEXTVIEW_SHOW_K21+4;
	public static final int TEXTVIEW_SHOW_PRNTASK = TEXTVIEW_SHOW_K21+5;
	public static final int TEXTVIEW_SHOW_SECTASK = TEXTVIEW_SHOW_K21+6;
	public static final int TEXTVIEW_SHOW_GETPINTASK = TEXTVIEW_SHOW_K21+7;
	public static final int TEXTVIEW_SHOW_FSTASK = TEXTVIEW_SHOW_K21+8;
	public static final int TEXTVIEW_SHOW_THK88 = TEXTVIEW_SHOW_K21+9;/**THK88模块 add by zhengxq 20181221*/
	public static final int TEXTVIEW_SHOW_LED = TEXTVIEW_SHOW_K21+10;
	public static final int TEXTVIEW_SHOW_SYSDELAY = TEXTVIEW_SHOW_K21+11;
	public static final int TEXTVIEW_SHOW_SYSMSDELAY =TEXTVIEW_SHOW_K21+12;
	public static final int TEXTVIEW_SHOW_SYSPOSTIME = TEXTVIEW_SHOW_K21+13;
	public static final int TEXTVIEW_SHOW_SYSTIME = TEXTVIEW_SHOW_K21+14;
	public static final int TEXTVIEW_SHOW_SYSGETPOSINFO = TEXTVIEW_SHOW_K21+15;
	public static final int TEXTVIEW_SHOW_WLM_WLANTASK = TEXTVIEW_SHOW_K21+16;
	public static final int TEXTVIEW_SHOW_BTTASK = TEXTVIEW_SHOW_K21+17;
	public static final int TEXTVIEW_SHOW_SDFSTASK = TEXTVIEW_SHOW_K21+18;
	public static final int TEXTVIEW_SHOW_SYSCONFIGTASK = TEXTVIEW_SHOW_K21+19;
	public static final int TEXTVIEW_SHOW_GETPININPUT = TEXTVIEW_SHOW_K21+20;
	//界面显示
	public static final int TEXTVIEW_VIEW_BASE = 2;
	public static final int TEXTVIEW_RFID_VIEW = TEXTVIEW_VIEW_BASE+18;
	public static final int TEXTVIEW_ICC_VIEW = TEXTVIEW_VIEW_BASE+17;
	public static final int TEXTVIEW_SAM_VIEW = TEXTVIEW_VIEW_BASE+3;
	public static final int TEXTVIEW_MAG_VIEW = TEXTVIEW_VIEW_BASE+16;
	public static final int TEXTVIEW_PRN_VIEW = TEXTVIEW_VIEW_BASE+19;
	public static final int TEXTVIEW_SEC_VIEW = TEXTVIEW_VIEW_BASE+6;
	public static final int TEXTVIEW_GETPIN_VIEW = TEXTVIEW_VIEW_BASE+20;
	public static final int TEXTVIEW_FS_VIEW = TEXTVIEW_VIEW_BASE+8;
	public static final int TEXTVIEW_THK88_VIEW = TEXTVIEW_VIEW_BASE+23;/**THK88模块 add by zhengxq 20181221*/
	public static final int TEXTVIEW_LED_VIEW = TEXTVIEW_VIEW_BASE+10;
	public static final int TEXTVIEW_SYS_DELAY_VIEW = TEXTVIEW_VIEW_BASE+11;
	public static final int TEXTVIEW_SYS_MSDELAY_VIEW =TEXTVIEW_VIEW_BASE+12;
	public static final int TEXTVIEW_SYS_POSTIME_VIEW = TEXTVIEW_VIEW_BASE+13;
	public static final int TEXTVIEW_SYS_TIME_VIEW = TEXTVIEW_VIEW_BASE+14;
	public static final int TEXTVIEW_SYS_GETPOSINFO_VIEW = TEXTVIEW_VIEW_BASE+15;
	public static final int TEXTVIEW_WLM_WLAN_VIEW = TEXTVIEW_VIEW_BASE+28;
	public static final int TEXTVIEW_BT_VIEW = TEXTVIEW_VIEW_BASE+29;
	public static final int TEXTVIEW_SDFS_VIEW = TEXTVIEW_VIEW_BASE+30;
	public static final int TEXTVIEW_SYSCONFIG_VIEW = TEXTVIEW_VIEW_BASE+31;
	//运行后隐藏textview
	public static final int TEXTVIEW_ALL_GONE = TEXTVIEW_VIEW_BASE-10;
	//密码键盘运行结束后隐藏键盘
	public static final int IMAGEBACK_GONE = TEXTVIEW_VIEW_BASE-20;
	
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) {
			case 0:
				showDialog();
				break;
			case IMAGEBACK_GONE :
				imageBack.setVisibility(View.GONE);
				mtvShow_getpin_input.setVisibility(View.GONE);
				break;
			case TEXTVIEW_ALL_GONE:
				mtvShow_rfid.setVisibility(View.GONE);
				mtvShow_icc.setVisibility(View.GONE);
				mtvShow_sam.setVisibility(View.GONE);
				mtvShow_mag.setVisibility(View.GONE);
				mtvShow_prn.setVisibility(View.GONE);
				mtvShow_sec.setVisibility(View.GONE);
				mtvShow_getpin.setVisibility(View.GONE);
				mtvShow_fs.setVisibility(View.GONE);
				mtvShow_led.setVisibility(View.GONE);
				mtvShow_sys_delay.setVisibility(View.GONE);
				mtvShow_sys_msdelay.setVisibility(View.GONE);
				mtvShow_sys_postime.setVisibility(View.GONE);
				mtvShow_sys_time.setVisibility(View.GONE);
				mtvShow_sys_getposinfo.setVisibility(View.GONE);
				mtvShow_thk88.setVisibility(View.GONE);
				mtvShow_wlm_wlan.setVisibility(View.GONE);
				mtvShow_bt.setVisibility(View.GONE);
				mtvShow_sdfs.setVisibility(View.GONE);
				mtvShow_sysconfig.setVisibility(View.GONE);
				break;
			case TEXTVIEW_RFID_VIEW :
				mtvShow_rfid.setVisibility(View.VISIBLE);
				SmartTask rfidTask = new SmartTask();
				rfidTask.executeOnExecutor(exec,rfidType);
				break;
			case TEXTVIEW_ICC_VIEW :
				mtvShow_icc.setVisibility(View.VISIBLE);
				SmartTask iccTask = new SmartTask();
				iccTask.executeOnExecutor(exec,iccType);
				break;
			case TEXTVIEW_SAM_VIEW :
				mtvShow_sam.setVisibility(View.VISIBLE);
				SmartTask samTask = new SmartTask();
				samTask.executeOnExecutor(exec,samType);
				break;
			case TEXTVIEW_MAG_VIEW :
				mtvShow_mag.setVisibility(View.VISIBLE);
				MagTask magTask = new MagTask();
				magTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_PRN_VIEW :
				mtvShow_prn.setVisibility(View.VISIBLE);
				PrnTask prnTask = new PrnTask();
				prnTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SEC_VIEW :
				mtvShow_sec.setVisibility(View.VISIBLE);
				SecTask secTask = new SecTask();
				secTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_FS_VIEW :
				mtvShow_fs.setVisibility(View.VISIBLE);
				FsTask fsTask = new FsTask();
				fsTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_WLM_WLAN_VIEW :
				mtvShow_wlm_wlan.setVisibility(View.VISIBLE);
				DataCommTask commTask = new DataCommTask();
				commTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_BT_VIEW :
				mtvShow_bt.setVisibility(View.VISIBLE);
				BTTask btTask = new BTTask();
				btTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SDFS_VIEW :
				mtvShow_sdfs.setVisibility(View.VISIBLE);
				SdFsTask sdfsTask = new SdFsTask();
				sdfsTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SYSCONFIG_VIEW :
				mtvShow_sysconfig.setVisibility(View.VISIBLE);
				SysConfigTask sysConfigTask = new SysConfigTask();
				sysConfigTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_GETPIN_VIEW :
				imageBack.setVisibility(View.VISIBLE);
				mtvShow_getpin.setVisibility(View.VISIBLE);
				mtvShow_getpin_input.setVisibility(View.VISIBLE);
				GetPinTask getpinTask = new GetPinTask();
				getpinTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_LED_VIEW :
				mtvShow_led.setVisibility(View.VISIBLE);
				LedTask ledTask = new LedTask();
				ledTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SYS_DELAY_VIEW :
				mtvShow_sys_delay.setVisibility(View.VISIBLE);
				SysDelayTask sysdelayTask = new SysDelayTask();
				sysdelayTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SYS_MSDELAY_VIEW :
				mtvShow_sys_msdelay.setVisibility(View.VISIBLE);
				SysMsDelayTask sysmsdelayTask = new SysMsDelayTask();
				sysmsdelayTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SYS_POSTIME_VIEW :
				mtvShow_sys_postime.setVisibility(View.VISIBLE);
				SysPosTimeTask syspostimeTask = new SysPosTimeTask();
				syspostimeTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SYS_GETPOSINFO_VIEW :
				mtvShow_sys_getposinfo.setVisibility(View.VISIBLE);
				SysGetPosInfoTask sysgetposinfoTask = new SysGetPosInfoTask();
				sysgetposinfoTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SYS_TIME_VIEW :
				mtvShow_sys_time.setVisibility(View.VISIBLE);
				SysTimeTask systimeTask = new SysTimeTask();
				systimeTask.executeOnExecutor(exec);
				break;
			case TEXTVIEW_THK88_VIEW:
				mtvShow_thk88.setVisibility(View.VISIBLE);
				THK88Task thk88Task = new THK88Task();
				thk88Task.executeOnExecutor(exec);
				break;
			case TEXTVIEW_SHOW_RFIDTASK:
				mtvShow_rfid.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_ICCTASK:
				mtvShow_icc.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SAMTASK:
				mtvShow_sam.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_MAGTASK:
				mtvShow_mag.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_PRNTASK:
				mtvShow_prn.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SECTASK:
				mtvShow_sec.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_FSTASK:
				mtvShow_fs.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_WLM_WLANTASK:
				mtvShow_wlm_wlan.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_BTTASK:
				mtvShow_bt.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SDFSTASK:
				mtvShow_sdfs.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSCONFIGTASK:
				mtvShow_sysconfig.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_GETPINTASK:
				mtvShow_getpin.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_GETPININPUT:
				mtvShow_getpin_input.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_LED:
				mtvShow_led.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSDELAY:
				mtvShow_sys_delay.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSMSDELAY:
				mtvShow_sys_msdelay.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSPOSTIME:
				mtvShow_sys_postime.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSTIME:
				mtvShow_sys_time.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSGETPOSINFO:
				mtvShow_sys_getposinfo.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_THK88:
				mtvShow_thk88.setText((CharSequence) msg.obj);
				break;
				
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mtvShow.setText((CharSequence) msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		myactivity =  (IntentActivity) getActivity();
		View view = inflater.inflate(R.layout.aysnctask_view, container, false);
		mtvShow = (TextView) view.findViewById(R.id.textView1);
		mtvShow_rfid = (TextView) view.findViewById(R.id.textView_rfid);
		mtvShow_icc = (TextView) view.findViewById(R.id.textView_icc);
		mtvShow_mag = (TextView) view.findViewById(R.id.textView_mag);
		mtvShow_prn = (TextView) view.findViewById(R.id.textView_prn);
		mtvShow_sec = (TextView) view.findViewById(R.id.textView_sec);
		mtvShow_fs = (TextView) view.findViewById(R.id.textView_fs);
		mtvShow_wlm_wlan = (TextView) view.findViewById(R.id.textView_wlm_wlan);
		mtvShow_bt = (TextView) view.findViewById(R.id.textView_bt);
		mtvShow_sdfs = (TextView) view.findViewById(R.id.textView_sdfs);
		mtvShow_sysconfig = (TextView) view.findViewById(R.id.textView_sysconfig);
		mtvShow_getpin = (TextView) view.findViewById(R.id.tv2_reg_pin);
		mtvShow_getpin_input = (TextView) view.findViewById(R.id.tv_getpin_input);
		mtvShow_led= (TextView) view.findViewById(R.id.tv2_led);
		mtvShow_sys_delay= (TextView) view.findViewById(R.id.tv2_sys_delay);
		mtvShow_sys_msdelay= (TextView) view.findViewById(R.id.tv2_sys_msdelay);
		mtvShow_sec= (TextView) view.findViewById(R.id.tv2_sec);
		mtvShow_sys_postime= (TextView) view.findViewById(R.id.tv2_sys_postime);
		mtvShow_sys_time= (TextView) view.findViewById(R.id.tv2_sys_time);
		mtvShow_sys_getposinfo= (TextView) view.findViewById(R.id.tv2_sys_posinfo);
		mtvShow_sam= (TextView) view.findViewById(R.id.tv2_sam);
		mtvShow_thk88= (TextView) view.findViewById(R.id.tv2_keyboard);
		// LCD图片切换
		imageBack = (ImageView) view.findViewById(R.id.image_back);
		
		view.findViewById(R.id.btn_key_0).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_1).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_2).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_3).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_4).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_5).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_6).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_7).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_8).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_9).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_point).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_on).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_under).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_esc).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_back).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_enter2).setOnClickListener(listener);
		return view;
	}
	
	private Gui gui = new Gui(myactivity, myHandler);
	private Gui gui_mag = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_MAGTASK);
	private Gui gui_prn = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_PRNTASK);
	private Gui gui_sec = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SECTASK);
	private Gui gui_fs = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_FSTASK);
	private Gui gui_datacomm = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_WLM_WLANTASK);
	private Gui gui_bt = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_BTTASK);
	private Gui gui_sdfs = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SDFSTASK);
	private Gui gui_sysconfig = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSCONFIGTASK);
	private Gui gui_getpin = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_GETPINTASK);
	private Gui gui_led= new Gui(myactivity, myHandler,TEXTVIEW_SHOW_LED);
	private Gui gui_sys_delay=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSDELAY);
	private Gui gui_sys_msdelay=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSMSDELAY);
	private Gui gui_sys_postime=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSPOSTIME);
	private Gui gui_sys_time=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSTIME);
	private Gui gui_sys_getposinfo=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSGETPOSINFO);
	private Gui gui_thk88=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_THK88);
	private SecKcvInfo kcvInfo = new SecKcvInfo();
	/*界面部分结束*/
	
	private SdkType sdkType;
	
	public void systest100(){
		// 进入本用例将SDK修改为SDK2.0，退出用例修改未原先的值
		sdkType = GlobalVariable.sdkType; 
		GlobalVariable.sdkType= SdkType.SDK2;
		
		if (gui.cls_show_msg("多模块并发测试，需要手动配置请点[确定],取消[其他]")==ENTER){
			while(true){
				myHandler.sendEmptyMessage(0);
				synchronized(g_lock)
				{
					try {
						g_lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Log.e("checkName", checkName.size()+"");
				
				if(GlobalVariable.currentPlatform == Model_Type.N910){
					if(checkName.contains("Mag")&&checkName.contains("Prn")){
						gui.cls_show_msg("N910打印模块与磁卡模块并发测试存在问题,不能同时进行！任意键重新选择");
						continue;
					}
				}
				
				if(sleeptest && checkName.size() > 1){
					gui.cls_show_msg("加入休眠间隔时应只选择一个k21模块进行测试！任意键重新选择");
					continue;
				}
				
				if(checkName.size() != 0)
				{
					for (int i = 0; i < checkName.size(); i++) 
					{
						strBuffer.append(checkName.toArray()[i]+"\n");
					}
				} else{
					if(gui.cls_show_msg("未选择并发任务,按照默认配置运行,[取消]退出测试,[其他]继续运行")==ESC){
						intentSys();
						return;
					} else{
						for (AYSNCTASK_LIST_K21 s : AYSNCTASK_LIST_K21.values())  {
							if((s.toString().equals(AYSNCTASK_LIST_K21.KeyBoardTask_SDK2.toString())||s.toString().equals(AYSNCTASK_LIST_K21.KeyBoardRegTask_SDK3.toString()))&&
									GlobalVariable.gModuleEnable.get(Mod_Enable.KeyBoardEnable)==false)
								continue;
							if((s.toString().equals(AYSNCTASK_LIST_K21.MagTask_SDK2.toString())||s.toString().equals(AYSNCTASK_LIST_K21.MagRegTask_SDK3.toString()))&&(GlobalVariable.currentPlatform==Model_Type.N910)){
								continue;
							}
							strBuffer.append(s.toString()+"\n");
						}
						for (AYSNCTASK_LIST_ANDROID s : AYSNCTASK_LIST_ANDROID.values())  {
							strBuffer.append(s.toString()+"\n");
						}
					}
				}
				break;
			}
		}	
		
		gui.cls_show_msg1(3,"将进行并发的任务:\n" + strBuffer.toString()+"任务循环次数:"+cycletime);
		
		while(true)
		{
			myHandler.sendEmptyMessage(TEXTVIEW_ALL_GONE);
			int returnValue=gui.cls_show_msg("多模块并发(浅并发)\n0.测试前配置\n1.开始运行");
			switch (returnValue) 
			{
			case '0':
				// 前置擦除密钥一次
				if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.SecTask.toString()) || strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinTask_SDK2.toString())||strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinRegTask_SDK3.toString()))
				{
					JniNdk.JNI_Sec_KeyErase();
				}
				isInit = preConfig();	
				break;
				
			case '1':
				if(!isInit)
				{
					gui.cls_show_msg("未成功初始化,请先进行初始化!点任意键继续");
					break;
				}
				taskTest();
				break;
			
			case ESC:
				intentSys();
				GlobalVariable.sdkType = sdkType;
				return;
			}
		}
	}
	
	//运行
	public void taskTest(){
		//gui.cls_show_msg1(1, "%s测试中...",TESTITEM);
		gui.cls_show_msg1_record(CLASS_NAME, "systest100",1, "%s测试中...",TESTITEM);
		count = 0;
		taskStatus = 0;
		isCancel = false;
		for (AYSNCTASK_LIST_K21 s : AYSNCTASK_LIST_K21.values()){
			if(strBuffer.toString().contains(s.toString())){
//				myHandler.sendEmptyMessage(i+TEXTVIEW_VIEW_BASE);
				myHandler.sendEmptyMessage(s.getValue());
				count++;
			}
		}
		for (AYSNCTASK_LIST_ANDROID s : AYSNCTASK_LIST_ANDROID.values()){
			if(strBuffer.toString().contains(s.toString())){
				myHandler.sendEmptyMessage(s.getValue());
				count++;
			}
		}
		
		while(true){
			if(!isCancel && (gui.cls_show_msg1(1, "%s测试中...点击[取消]退出测试",TESTITEM)==ESC)){
				if (gui.ShowMessageBox("确认退出本用例？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) {
					isCancel = true;
					gui.cls_show_msg1(1, "正在停止测试...");
					//SystemClock.sleep(2000);
				}
			}
			if(taskStatus == count){
				break;
			}
		}
		//安全模块后置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.SecTask.toString()) || strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinTask_SDK2.toString())||strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinRegTask_SDK3.toString())){
			JniNdk.JNI_Sec_KeyErase();
			JniNdk.JNI_Sec_SetKeyOwner(shareKey);//设置密钥属主为k21端
		}
		gui.cls_show_msg1(5,"%s测试完成",TESTITEM);
		gui.cls_show_msg("测试完成,按任意键退出");
		return;
	}
	
	//前置
	public boolean preConfig(){
		int ret = -1;
		config = new Config(myactivity, handler);
		gui.cls_show_msg1(1,"各模块配置中...");
		//数据通讯配置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_ANDROID.WLM_WLANTask.toString())){
			int i = 0;
			while(i < 100){
				int nkeyIn = gui.cls_show_msg("数据通讯任务配置\n0.Wlan通讯\n1.WLM无线通讯\n");
				switch (nkeyIn) {
				case '0':
					linkType = 0;
					if((config.confConnWlan(wifiPara)) == NDK_OK){
						socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
						gui.cls_show_msg1(1, "wlan参数配置完毕！！！");
					} else{
						gui.cls_show_msg1(2, "wlan参数配置失败!");
						return false;
					}
					i = 100;
					break;
				case '1':
					linkType = 1;
					setWireType(mobilePara);
					if((config.confConnWLM(true,mobilePara)) == NDK_OK){
						socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
						gui.cls_show_msg1(2, "无线网络参数配置成功!");
					} else{
						gui.cls_show_msg1(2, "无线网络参数配置失败!");
						return false;
					}
					i = 100;
					break;
				default:
					gui.cls_show_msg1(1, "请输入正确序号！");
					break;
				}
			}
		}
		//蓝牙配置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_ANDROID.BTTask.toString())){
			gui.cls_show_msg("请打开已配置手机端的bluetoothServer蓝牙工具,完成点任意键继续");
			bluetoothManager = BluetoothManager.getInstance(myactivity);//单例模式
			config.btConfig(pairList, unPairList, bluetoothManager);
			dataService = new BluetoothService(getBtAddr());
			if(!(bluetoothManager.connComm(dataService,CHANEL_DATA))){
				gui.cls_show_msg1(2, "蓝牙配对失败!");
				return false;
			}
			gui.cls_show_msg1(2,"蓝牙配置成功!，配对蓝牙="+DefaultFragment.g_btName);
		}
		//射频卡配置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.RfidTask_SDK2.toString())||strBuffer.toString().contains(AYSNCTASK_LIST_K21.RfidRegTask_SDK3.toString())){
			rfidType = config.rfid_config();
			if(rfidType==_SMART_t.FELICA){
				felicaChoose=config.felica_config();
			}
			if((ret =rfidInit(rfidType))!=NDK_OK){
				gui.cls_show_msg1_record(CLASS_NAME, "systest100",g_keeptime, "line %d:%s卡初始化失败(%d)", Tools.getLineInfo(),rfidType,ret);
				return false;
			}
			else
				gui.cls_show_msg1(2,"%s初始化成功!!!", rfidType);
		}
		//sam卡配置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.SamTask.toString())&&(GlobalVariable.currentPlatform).toString().contains("IM81")){//IM81、N550、N850有两个SAM卡槽
			int nkeyIn = gui.cls_show_msg("SAM配置\n0.SAM1卡\n1.SAM2卡\n");
			switch (nkeyIn) {
			case '0':
				samType = _SMART_t.SAM1;
				break;
			case '1':
				samType = _SMART_t.SAM2;
				break;
			default:
				break;
			}
			gui.cls_show_msg1(1, "SAM配置成功！",samType.toString());
		}
		//sec、pin前置，擦除全部密钥操作
		if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.SecTask.toString()) || strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinTask_SDK2.toString())||strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinRegTask_SDK3.toString())){
			
			int nkeyIn = gui.cls_show_msg("sec任务keyowner设置：\n0.不设置setKeyOwner\n1.setKeyOwner(\"\")Android端\n2.setKeyOwner(\"*\")K21端\n3.setKeyOwner(appname)\n");
			switch (nkeyIn) 
			{
			case '0':// 不设置keyowner密钥保存在K21端
				keyOwner = "default";
				gui.cls_show_msg1(1, "选择不设置keyowner");
				break;
				
			case '1':// 设置keyowner为""，密钥保存在android端
				keyOwner = TESTA;
				gui.cls_show_msg1(1, "选择设置keyowner为Android端");
				break;
				
			case '3':
				keyOwner = packName;
				gui.cls_show_msg1(1, "选择设置keyowner为appname");
				break;
				
			case '2':// 设置keyowner为*，密钥保存在K21端	
			default:
				keyOwner = shareKey;
				gui.cls_show_msg1(1, "选择设置keyowner为K21端");
				break;
			}
			if(!keyOwner.equals("default"))
				JniNdk.JNI_Sec_SetKeyOwner(keyOwner);
			kcvInfo.nCheckMode=0;
			kcvInfo.nLen=4;
			byte[] pszOwner=new byte[100];
			JniNdk.JNI_Sec_GetKeyOwner(pszOwner.length, pszOwner);
			//设置为* 不需要安装TLK
			if( !ISOUtils.byteToStr(pszOwner).equals("*")){
				JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo);
			}
			gui.cls_show_msg1(2,"keyOwner初始化成功!");
		}
		//打印配置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.PrnTask_SDK2.toString())||strBuffer.toString().contains(AYSNCTASK_LIST_K21.PrnRegTask_SDK3.toString())){
			gui.cls_show_msg("确认导入picture文件夹，任意键继续");
		}
		if(sleeptest){
			try 
			{
				settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
				settingsManager.setScreenTimeout(15000);//设置系统休眠时间15s
			} catch (NoClassDefFoundError e) 
			{
				e.printStackTrace();
				gui.cls_show_msg1(g_keeptime,"line %d:找不到该类，抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			}
		}
		gui.cls_show_msg1(2,"配置完成!");
		return true;
	}
	
	/**SMART卡任务*/
	class SmartTask extends AsyncTask<_SMART_t, Integer, String>{
		@Override
		protected void onPreExecute() {
			Log.i("SmartTask", "SmartTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(_SMART_t... type) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			int[] UidLen = new int[1];
			byte[] UidBuf = new byte[20];
			_SMART_t smartType = _SMART_t.CPU_A;
			String taskname = "RfidTask";
			Gui gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_RFIDTASK);
			if(type.length>0){
				smartType = type[0];
				switch(type[0]){
				case CPU_A:
				case CPU_B:
				case MIFARE_1:
				case ISO15693:
				case MIFARE_0:
				case MIFARE_0_C:
				case FELICA:
					taskname = "RfidTask";
					gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_RFIDTASK);
					break;
				case SAM1:
				case SAM2:
					taskname = "SAMTask";
					gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SAMTASK);
					break;
				case IC:
					taskname = "IccTask";
					gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_ICCTASK);;
					break;
				default:
					break;
				}
			}
			//保护动作
			smartDeactive(smartType);
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_smart.cls_printf(String.format("%s%d...已成功%d",taskname,bak-cnt,succ).getBytes());
				
				//寻卡
				if((ret = smart_detect(smartType, UidLen, UidBuf))!=NDK_OK)
				{
					gui_smart.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime, "line %d:第%d次:%s寻卡失败(%d)",Tools.getLineInfo(),bak-cnt,taskname,ret);
					continue;
				}
				
				//激活
				if((ret = smartActive(smartType,felicaChoose,UidLen,UidBuf)) != NDK_OK )
				{
					gui_smart.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime, "line %d:第%d次:%s激活失败(%d)",Tools.getLineInfo(),bak-cnt,taskname,ret);
					continue;
				}
				
				//APDU读写
				try{
					if((ret = smartApduRw(smartType,req,UidBuf)) != NDK_OK)
					{
						gui_smart.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime, "line %d:第%d次:%s读写失败(%d)",Tools.getLineInfo(),bak-cnt,taskname,ret);
						continue;
					}
				} catch (Exception e) {
					gui_smart.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime, "line %d:第%d次:%s读写抛出异常(%s)",Tools.getLineInfo(),bak-cnt,taskname,e.getMessage());
				}
				
				//下电
				if((ret = smartDeactive(smartType)) != NDK_OK)
				{
					gui_smart.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime,"line %d:第%d次:%s下电失败(%d)",Tools.getLineInfo(),bak-cnt,taskname,ret);
					continue;
				}
				
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			String result = String.format(Locale.CHINA,"%s结束,已执行%d次,成功%d次",taskname,bak-cnt,succ);
			gui_smart.cls_show_msg1_record(CLASS_NAME, "SmartTask", 1, result);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("SmartTask", "SmartTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        Log.i("SmartTask", "SmartTask end");
	        taskStatus++;
	    }
	}
	
	/**磁卡任务*/
	class MagTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("MagTask", "MagTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int ret2 = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			while(cnt>0){
				if(isCancel) break;
				//前置
				JniNdk.JNI_Mag_Close();
				
				byte[] swiped = new byte[1];
				byte[] TK2_Buf  = new byte[100];
				int[] errorCode = new int[1];
				int waittime = 5, diff = 0;
				
				cnt--;
				gui_mag.cls_printf(String.format("MagTask%d...已成功%d",bak-cnt,succ).getBytes());
				if((ret = JniNdk.JNI_Mag_Open())!= NDK_OK)
				{
					gui_mag.cls_show_msg1_record(CLASS_NAME, "MagTask",g_keeptime, "line %d:第%d次:Mag_Open失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				} 
				//提示5秒内刷卡的方式
				gui_mag.cls_printf(String.format("MagTask%d:请在5秒内刷磁卡",bak-cnt).getBytes());
				long startTime = System.currentTimeMillis();
				do
				{
					if(gui_mag.wait_key(1)==ESC)
					{
						ret = NDK_ERR_QUIT;
						break;
					}
					if((ret = JniNdk.JNI_Mag_Swiped(swiped))==NDK_OK&&swiped[0]==1)// 已刷卡
					{
						// 读卡
						if((ret2 = JniNdk.JNI_Mag_ReadNormal(null, TK2_Buf, null, errorCode))!=NDK_OK)
						{
							gui_mag.cls_show_msg1_record(CLASS_NAME, "MagTask",g_keeptime, "line %d:第%d次:Mag_ReadNormal失败(%d)", Tools.getLineInfo(),bak-cnt,ret2);
							break;
						} else{
							String str_TK2 = new String(TK2_Buf);
							int index = str_TK2.indexOf("\0");
							
							if(index == -1)
								gui_mag.cls_show_msg1(1, "2道无数据!",CLASS_NAME,Tools.getLineInfo());
							else
							{
								String end_TK2 = str_TK2.substring(0, index);
								gui_mag.cls_show_msg1(1, "2道数据(%d):%s\n",end_TK2.length(), end_TK2);
							}
						}
						JniNdk.JNI_Mag_Reset();
						break;
					}
				}while(waittime==0||(diff = (int) Tools.getStopTime(startTime))<waittime);
				
				if(waittime!=0&&diff>=waittime){
					gui_mag.cls_show_msg1_record(CLASS_NAME, "MagTask",g_keeptime, "line %d:第%d次:刷卡超时或刷卡失败(%d，%d)", Tools.getLineInfo(),bak-cnt,ret,swiped[0]);
					JniNdk.JNI_Mag_Close();
					continue;
				}
				
				if((ret = JniNdk.JNI_Mag_Close())!= NDK_OK)
				{
					gui_mag.cls_show_msg1_record(CLASS_NAME, "MagTask",g_keeptime, "line %d:第%d次:Mag_Close失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				} 
				Log.w("MagTask_Close", "ret="+ret);
				
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"磁卡任务结束,已执行%d次,成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("MagTask", "MagTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_mag.cls_show_msg1_record(CLASS_NAME, "MagTask",1, result);
	        Log.i("MagTask", "MagTask end");
	        taskStatus++;
	    }
	}
	
	/**打印任务*/
	class PrnTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("PrnTask", "PrnTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			printUtil = new PrintUtil(myactivity, myHandler,true);
			String picPath = GlobalVariable.sdPath+"picture/logo.png";
			if((ret = fileSystem.JDK_FsExist(picPath))!=NDK_OK){
				String result = String.format(Locale.CHINA,"line %d:测试终止,未导入picture文件夹!",Tools.getLineInfo());
		        return result;
			}
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_prn.cls_printf(String.format("PrnTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				//状态
				if((ret = printUtil.getPrintStatus(MAXWAITTIME,TEXTVIEW_SHOW_PRNTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask", g_keeptime,"line %d:第%d次:打印机状态异常(%d)！", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				//打印图片
				if((ret = printUtil.printPng(picPath,0))!=NDK_OK)
				{
					gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask", g_keeptime,"line %d:第%d次:打印图片失败(%d)", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				if((ret = printUtil.getPrintStatus(MAXWAITTIME,TEXTVIEW_SHOW_PRNTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask",g_keeptime, "line %d:第%d次:打印机状态异常(%d)！(当前用例:png)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				
				// 打印脚本
				printUtil.print_Script();
				if((ret = printUtil.getPrintStatus(MAXWAITTIME,TEXTVIEW_SHOW_PRNTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask", g_keeptime,"line %d:第%d次:打印机状态异常(%d)！(当前用例:脚本)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"打印任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("PrnTask", "PrnTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask",1, result);
	        Log.i("PrnTask", "PrnTask end");
	        taskStatus++;
	    }
	}
	
	/**安全任务*/
	class SecTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("SecTask", "SecTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			String mainKey_b = "11111111111111111111111111111111"; // 密钥明文为32个1
			String macKey = "C0B757413B847925C0B757413B847925";// 明文为16个13（13131313131313131313131313131313）
			String keyStr_TDK1 = "38BEDB24A6D38018563E884E285E1350";// 明文为8个17+8个18（17171717171717171818181818181818）
			String initkey = "00010203040505080001020304050508";
			byte[] version = new byte[20];
			int[] tamper = new int[]{0};
			int[] prVal = new int[]{0};
			byte[] psMacOut = new byte[16];
			byte[] getRandom = new byte[8];
			byte[] desOut = new byte[8];
			byte[] ksn = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xEE,0x00,0x06};
			byte[] psKsnOut = new byte[10];
			byte ucGroupIdx = (byte)0x10;			
			
			// 测试前置，擦除全部密钥操作
			//JniNdk.JNI_Sec_KeyErase();
			
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_sec.cls_printf(String.format("SecTask%d...已成功%d",bak-cnt,succ).getBytes());
				/*以下sec接口功能都用到*/
				//获取安全版本
				if((ret = JniNdk.JNI_Sec_GetVersion(version))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime,"line %d:第%d次:获取安全版本失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				//取8字节随机数
				if((ret = JniNdk.JNI_Sec_GetRandom(8, getRandom))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime,"line %d:第%d次:取随机数测试失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				//取安全触发状态
				if((ret = JniNdk.JNI_Sec_GetTamperStatus(tamper))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime,"line %d:第%d次:取安全触发状态失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				//获取安全寄存器状态
				if((ret = JniNdk.JNI_Sec_GetDrySR(prVal))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime,"line %d:第%d次:获取安全寄存器状态失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				/*//清安全寄存器
				if((ret = JniNdk.JNI_Sec_Clear())!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(TAG, "SecTask", g_keeptime,"line %d:第%d次:清安全寄存器失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}*/
				//设置密钥主属
				if(!keyOwner.equals("default"))
				{
					if((ret = JniNdk.JNI_Sec_SetKeyOwner(keyOwner))!=NDK_OK)
					{
						gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime,"line %d:第%d次:设置keyowner为%s失败(%d)", Tools.getLineInfo(), bak-cnt,keyOwner, ret);
						continue;
					}
				}
				
				//明文下装主密钥TMK,index = 1
				kcvInfo.nCheckMode = 1;
				kcvInfo.nLen = 4;
				kcvInfo.sCheckBuf = ISOUtils.hex2byte("82E13665B4624DF5");
				if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte(mainKey_b),kcvInfo))!=NDK_OK)
				{
			        gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:装载主密钥失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				//装载MAC工作密钥TAK,index = 3
				kcvInfo.nCheckMode = 1;
				kcvInfo.sCheckBuf = ISOUtils.hex2byte("A8B7B5BD4A67D640");
				if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal(), 
						(byte)1, (byte)3, 16, ISOUtils.hex2byte(macKey), kcvInfo))!=NDK_OK)
				{
			        gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:装载MAC工作密钥失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				// 安装TDK1(16字节)，index = 4
				kcvInfo.nCheckMode = 1;
				kcvInfo.sCheckBuf = ISOUtils.hex2byte("54BA0822AAE5EB1D");
				if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), 
						(byte)1, (byte)4, 16, ISOUtils.hex2byte(keyStr_TDK1), kcvInfo))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:装载TDK(16字节)失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				//MAC_9606运算
				if((ret = JniNdk.JNI_Sec_GetMac((byte)3, ISOUtils.hex2byte("00052D0A"), 4, psMacOut, (byte)EM_SEC_MAC.SEC_MAC_9606.ordinal()))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:MAC运算错误(%d)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				if(Tools.memcmp(ISOUtils.hex2byte("95D2B7B12C03B05C"), psMacOut, 8)==false)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:MAC运行校验错误(%s)",Tools.getLineInfo(), bak-cnt, ISOUtils.hexdump(psMacOut));
					continue;
				}
				//计算DES
				if((ret = JniNdk.JNI_Sec_CalcDes((byte)4, (byte)4, ISOUtils.hex2byte("1313131313131313"), 8, desOut, (byte)EM_SEC_DES.SEC_DES_ENCRYPT.ordinal()))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime,"line %d:第%d次:DES加密测试失败(%d)", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				//5CF306FEB7CD3F64
				if(Tools.memcmp(desOut, ISOUtils.hex2byte("61E5C263E2667090"), 8)==false)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:des加密校验错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.hexdump(desOut));
					continue;
				}
				//计算kcv
				kcvInfo.nCheckMode = 1;
				/*if((ret = JniNdk.JNI_Sec_GetKcv((byte)4, (byte)4, kcvInfo))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(TAG, "SecTask",g_keeptime,"line %d:第%d次:计算工作密钥失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}*/
				if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte("54BA0822AAE5EB1D"), 4)==false)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:data密钥下装校验错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.hexdump(kcvInfo.sCheckBuf));
					continue;
				}
				//清单独密钥
				if((ret = JniNdk.JNI_Sec_KeyDelete((byte)1, (byte)0))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime,"line %d:第%d次:清单独密钥失败（ret = %d）", Tools.getLineInfo(),ret);
					continue;
				}
				//明文装载TLK
				kcvInfo.nCheckMode = 0;
				if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime,"line %d:第%d次:装载TLK失败（ret = %d）", Tools.getLineInfo(),ret);
					continue;
				}
			    //DUKPT装载 	index = 10
				if((ret = JniNdk.JNI_Sec_LoadTIK(ucGroupIdx, (byte)1, (byte)16, ISOUtils.hex2byte(initkey), ksn, kcvInfo))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime,"line %d:第%d次:装载TIK失败（ret = %d）", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				//ksn值校验
				if((ret = JniNdk.JNI_Sec_GetDukptKsn(ucGroupIdx, psKsnOut))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime,"line %d:第%d次:获取的ksn值失败（ret = %d）", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				if(Tools.memcmp(psKsnOut,ISOUtils.hex2byte("FFFFFFFFFFFFFFE00001"), 10)==false)		//最后两个字节会被重置为0x0001，倒数第三字节的bit0~bit4重置为0
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:ksn值校验错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.hexString(psKsnOut));
					continue;
				}
				LoggerUtil.d("psKsnOut:"+ISOUtils.hexString(psKsnOut));
				//计算DUKPT密钥MAC
				Arrays.fill(psMacOut, (byte) 0);
				Arrays.fill(psKsnOut, (byte) 0);
				if((ret = JniNdk.JNI_Sec_GetMacDukpt(ucGroupIdx, ISOUtils.hex2byte("1313131313131313"), 8, psMacOut, psKsnOut, (byte)EM_SEC_MAC.SEC_MAC_X99.ordinal()))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime,"line %d:第%d次:计算DUKPT密钥MAC失败（ret = %d）", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				LoggerUtil.d("psMacOut:"+ISOUtils.hexString(psMacOut)+"\npsKsnOut:"+ISOUtils.hexString(psKsnOut));
				// 校验输出的dukpt密钥的mac值，只有在初始ksn的情况下该mac值才是有效的
				if(Tools.memcmp(psKsnOut, ISOUtils.hex2byte("FFFFFFFFFFFFFFE00001"), 10)==false)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:初始ksn错误，测试过程中测试人员修改了ksn值", Tools.getLineInfo(),bak-cnt);
					continue;
				}
				else
				{
					// ksn为初始ksn可进行mac校验
					if(Tools.memcmp(psMacOut, ISOUtils.hex2byte("67197743A2AD6F2B"), 8)==false)
					{
						gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, "line %d:第%d次:mac校验失败（预期 = 67197743A2AD6F2B，实际=%s）", Tools.getLineInfo(),bak-cnt,ISOUtils.hexString(psMacOut));
						continue;
					}
				}
				//KSN号增加 
				if((ret = JniNdk.JNI_Sec_IncreaseDukptKsn(ucGroupIdx))!=NDK_OK)
				{
					gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime,"line %d:第%d次:KSN号增加失败（ret = %d）", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				succ++;
				publishProgress(bak-cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"安全任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("SecTask", "SecTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",1, result);
	        Log.i("SecTask", "SecTask end");
	        taskStatus++;
	    }
	}
	
	/**密码键盘任务*/
	class GetPinTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("GetPinTask", "GetPinTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			// A7安装密钥值每个索引要不同，相同密钥不能在同个索引重复安装
			String mainKey_a = "22222222222222222222222222222222"; //String tmkCmp = "82E13665B4624DF5";// 密钥明文为32个2
			String tpk1Key = "145F5C6E3D914457145F5C6E3D914457";// 明文BF25B997F74BC2C1BF25B997F74BC2C1
			StringBuffer str=new StringBuffer();
			byte[] szPinOut=new byte [9];
			byte[] PinDesOut=new byte [9];
			for (int j = 0; j < szPinOut.length; j++) {
				szPinOut[j]=0;
				PinDesOut[j]=0;
			}
			byte[] PinKeyValue={0x15,0x15,0x15,0x15,0x15,0x15,0x15,0x15};
			int PINTIMEOUT_MAX = 20*1000;
			String szPan = "6225885916163157";
//			int[] nStatus={0};
			// 测试前置，擦除所有密钥
			/*if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK){
				String result = String.format("line %d:测试终止，擦除所有密钥失败(%d)",Tools.getLineInfo(),ret);
		        return result;
			}*/
			//安装TMK, ID=5，明文22222222222222222222222222222222
			kcvInfo.nCheckMode = 0;
			if ((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte) 0, (byte) 5, 16,
					ISOUtils.hex2byte(mainKey_a), kcvInfo)) != NDK_OK) {
				String result = String.format(Locale.CHINA,"line %d:测试终止，安装TMK失败(%d)",Tools.getLineInfo(),ret);
		        return result;
			}
			//安装TPK1(16bytes), ID=6,密文安装，明文BF25B997F74BC2C1BF25B997F74BC2C1
			if ((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 5, (byte) 6, 16,
					ISOUtils.hex2byte(tpk1Key), kcvInfo)) != NDK_OK) {
				String result = String.format(Locale.CHINA,"line %d:测试终止，安装TPK1失败(%d)",Tools.getLineInfo(),ret);
		        return result;
			}
			/**因为SecTask安装的TDK密钥为17171717171717171919191919191919,这里的密钥值修改为17171717171717172020202020202020 by 20200421*/
			//表示明文安装，明文17171717171717172020202020202020
			if ((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 0, (byte) 7, 16,
					ISOUtils.hex2byte("17171717171717172020202020202020"), kcvInfo)) != NDK_OK) {
				String result = String.format(Locale.CHINA,"line %d:测试终止，明文安装TPK2失败(%d)",Tools.getLineInfo(),ret);
		        return result;
			}
			
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_getpin.cls_printf(String.format("GetPinTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				// case9:以SEC_PIN_ISO9564_0,pszExpPinLenIn="4",pszDataIn!=NULL,psPinBlockOut=NULL,NDK_SecGetPinResult()函数获取
				// PINBLOCK
				if ((ret = touchscreen_getnum(str)) != NDK_OK) {
					gui_getpin.cls_show_msg1_record(CLASS_NAME, "GetPinTask",g_keeptime, "line %d:第%d次:随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				str.append("尽快输入4321并确认...");
				gui_getpin.cls_printf(str.toString().getBytes());
				if ((ret = JniNdk.JNI_Sec_GetPin((byte) 7, "4", szPan, null, (byte) 3, PINTIMEOUT_MAX)) != NDK_OK) {
					gui_getpin.cls_show_msg1_record(CLASS_NAME, "GetPinTask",g_keeptime, "line %d:第%d次:获取PIN Block失败(ret = %d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				szPinOut = getPinInput();
				if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("35279af714dace0d"), 8) == false) {
					gui_getpin.cls_show_msg1_record(CLASS_NAME, "GetPinTask",5, "line %d:第%d次:校验失败(PinDesOut = %s)", Tools.getLineInfo(), bak-cnt, Dump.getHexDump(szPinOut));
					continue;
				}
				succ++;
				publishProgress(bak-cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			String result = String.format(Locale.CHINA,"密码键盘任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("GetPinTask", "GetPinTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        myHandler.sendEmptyMessage(IMAGEBACK_GONE);
	        gui_getpin.cls_show_msg1_record(CLASS_NAME, "GetPinTask",1, result);
	        Log.i("GetPinTask", "GetPinTask end");
	        taskStatus++;
	    }
	}
	
	/**数据通讯任务*/
	class DataCommTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("DataCommTask", "DataCommTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			int slen = 0, rlen = 0, timeout = SO_TIMEO;
			byte[] rbuf = new byte[PACKMAXLEN];
			byte[] buf = new byte[PACKMAXLEN];
			sendPacket = new PacketBean();
			init_snd_packet(sendPacket, buf);
			//set_snd_packet(sendPacket,wifiPara.getType());
			sendPacket.setLifecycle(cycletime);
			sendPacket.setLen(PACKMAXLEN);
			sendPacket.setOrig_len(PACKMAXLEN);
			
			layerBase = Layer.getLayerBase(myactivity,myHandler,TEXTVIEW_SHOW_WLM_WLANTASK);
			Object linkObject = wifiPara;
			Sock_t sock_t = wifiPara.getSock_t();
			LinkType type = wifiPara.getType();
			String taskname = "WLANTask";
			if(linkType == 1){
				linkObject = mobilePara;
				sock_t = mobilePara.getSock_t();
				type = mobilePara.getType();
				taskname = "WLMTask";
			}
			
			try{
				if ((ret = layerBase.netUp(linkObject, type)) != SUCC) 
				{
					String result = String.format(Locale.CHINA,"line %d:测试终止，%s_NetUp失败(ret = %d)",Tools.getLineInfo(),taskname,ret);
			        return result;
				}
			} catch (NullPointerException e){
				e.printStackTrace();
				String result = String.format(Locale.CHINA,"line %d:测试终止，%s_NetUp抛出异常(ret = %d)",Tools.getLineInfo(),taskname,ret);
		        return result;
			}

			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_datacomm.cls_printf(String.format("%s%d...已成功%d",taskname,bak-cnt,succ).getBytes());
				
				if((ret = update_snd_packet(sendPacket, type))!= NDK_OK){
					break;
				}

				if ((ret = layerBase.transUp(socketUtil, sock_t)) != SUCC) 
				{
					gui_datacomm.cls_show_msg1_record(CLASS_NAME,taskname, g_keeptime, "line %d:第%d次:%s_TransUp失败(ret = %d)",Tools.getLineInfo(), bak-cnt,taskname,ret);
					continue;
				}
				
				// 发送数据
				try 
				{
					if((slen = socketUtil.send(sock_t, sendPacket.getHeader(), sendPacket.getLen(), timeout)) != sendPacket.getLen())
					{
						layerBase.transDown(socketUtil, sock_t);
						gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname,g_keeptime, "line %d:第%d次:%s发送数据失败(%d)",Tools.getLineInfo(), bak-cnt, taskname, slen);
						continue;
					} 
				} catch (IOException e) 
				{
					e.printStackTrace();
					layerBase.transDown(socketUtil, sock_t);
					gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname,g_keeptime, "line %d:第%d次:%s发送数据抛出异常(%d)",Tools.getLineInfo(), bak-cnt, taskname, slen);
					break;
				}
				
				//接收数据
				try 
				{
					// 多次接收的处理机制
					if((rlen = socketUtil.receive(sock_t,rbuf,sendPacket.getLen(), timeout)) != sendPacket.getLen())
					{
						layerBase.transDown(socketUtil, sock_t);
						gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname,g_keeptime, "line %d:第%d次:%s接收数据失败(%d)",Tools.getLineInfo(),bak-cnt,taskname, rlen);
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
					layerBase.transDown(socketUtil, sock_t);
					gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname,g_keeptime, "line %d:第%d次:%s接收数据抛出异常(%d)",Tools.getLineInfo(), bak-cnt,taskname,rlen);
					continue;
				}
				
				// 比较数据
				if(Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())==false)
				{
					layerBase.transDown(socketUtil, sock_t);
					gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime,"line %d:第%d次:%s检验收发数据失败", Tools.getLineInfo(), bak-cnt,taskname);
					continue;
				}
				
				//TransDown
				if((ret = layerBase.transDown(socketUtil, sock_t)) != NDK_OK)
				{
					gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname,g_keeptime, "line %d:第%d次:%s_TransDown失败(%d)", Tools.getLineInfo(),bak-cnt,taskname,ret);
					continue;
				} 
			
				succ++;
				publishProgress(bak-cnt);
			}
			if((ret = layerBase.netDown(socketUtil, linkObject, sock_t, type)) != NDK_OK)
			{
				gui_datacomm.cls_show_msg1_record(CLASS_NAME, taskname,g_keeptime, "line %d:%s_NetDown失败(%d)", Tools.getLineInfo(),taskname,ret);
			}
				
			String result = String.format(Locale.CHINA,"%s结束，已执行%d次，成功%d次",taskname,bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("DataCommTask", "DataCommTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_datacomm.cls_show_msg1_record(CLASS_NAME, "DataCommTask",1, result);
	        Log.i("DataCommTask", "DataCommTask end");
	        taskStatus++;
	    }
	}
	
	/**蓝牙通讯任务*/
	class BTTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("BTTask", "BTTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
//			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			
			byte[] rbuf = new byte[BUFSIZE_BT];
			byte[] wbuf = new byte[BUFSIZE_BT];
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			// 测试前置，关闭蓝牙
			if(bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.disable();
				SystemClock.sleep(500);
			}
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_bt.cls_printf(String.format("BTTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				gui_bt.cls_printf("打开蓝牙串口中...".getBytes());
				if(!bluetoothAdapter.isEnabled())
				{
					bluetoothAdapter.enable();
					SystemClock.sleep(5000);
				}
				if(!bluetoothAdapter.isEnabled())
				{
					gui_bt.cls_show_msg1_record(CLASS_NAME, "BTTask", g_keeptime, "line %d:第%d次打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
					continue;
				}
				// 蓝牙设备配对
				gui_bt.cls_printf("蓝牙连接建立中...".getBytes());
				if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
				{
					gui_bt.cls_show_msg1_record(CLASS_NAME, "BTTask",g_keeptime, "line %d:第%d次：BT连接建立失败",Tools.getLineInfo(), bak-cnt);
					continue;
				}
				
				byte[] tempBuf = new byte[5];
				// 等待服务器端发送的hello数据后才往下走
				if(bluetoothManager.readComm(dataService,tempBuf)==false)
				{
					gui.cls_show_msg1_record(CLASS_NAME, "rwPre",g_keeptime, "line %d:第%d次:BT接收hello数据失败", Tools.getLineInfo(), bak-cnt);
					continue;
				}
				if(new String(tempBuf).contains("hello")==false)
				{
					continue;
				}
				
				// 蓝牙发送数据
				gui_bt.cls_printf("蓝牙数据发送中...".getBytes());
				for (int j = 0; j < wbuf.length; j++) 
				{
					wbuf[j] = (byte) (Math.random()*128);
				}
				if(bluetoothManager.writeComm(dataService,wbuf)==false)
				{
					gui_bt.cls_show_msg1_record(CLASS_NAME,"BTTask",g_keeptime, "line %d:第%d次：BT发送数据失败", Tools.getLineInfo(),bak-cnt);
					bluetoothManager.cancel(dataService);
					continue;
				}
				// 蓝牙接收数据
				gui_bt.cls_printf("蓝牙数据接收中...".getBytes());
				if(bluetoothManager.readComm(dataService,rbuf)==false)
				{
					gui_bt.cls_show_msg1_record(CLASS_NAME,"BTTask",g_keeptime, "line %d:第%d次：BT接收数据失败", Tools.getLineInfo(),bak-cnt);
					bluetoothManager.cancel(dataService);
					continue;
				}
				
				if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
				{
					gui_bt.cls_show_msg1_record(CLASS_NAME, "BTTask", g_keeptime, "line:%d:第%d次数据校验失败,收发数据过长无法显示,见log日志", Tools.getLineInfo(),bak-cnt);
					LoggerUtil.d("第"+(bak-cnt)+"次发送数据:"+Arrays.toString(wbuf));
					LoggerUtil.d("第"+(bak-cnt)+"次接收数据:"+Arrays.toString(rbuf));
					bluetoothManager.cancel(dataService);
					continue;
				}
				succ++;
				gui_bt.cls_printf("断开连接...".getBytes());
				// 断开socket的连接，服务器也会相应断开
				bluetoothManager.cancel(dataService);
				// 断开蓝牙连接
				bluetoothAdapter.disable();
				SystemClock.sleep(2000);
				
				publishProgress(bak-cnt);
			}
			String result = String.format(Locale.CHINA,"蓝牙任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("BTTask", "BTTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_bt.cls_show_msg1_record(CLASS_NAME, "BTTask",1, result);
	        Log.i("BTTask", "BTTask end");
	        taskStatus++;
	    }
	}
	
	/**文件任务 modify by zhengxq20181221 循环的流程修改 写-读*/
	class FsTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("FsTask", "FsTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			int fd = -1;
			/**每个文件都是5K*/
			byte[] writebuf = new byte[BUFLEN];
			byte[] defineBuf = new byte[BUFLEN];
			byte[] memBuf = new byte[BUFLEN];

			//对10个文件写
			Arrays.fill(writebuf, (byte)0x39);
			for (String fileName : filepath) {
				writeFile(fileName,writebuf);
			}
			// 测试前置：写入5K的文件
			if((ret = JniNdk.JNI_FsExist(file10path))==NDK_OK){
				if((ret = JniNdk.JNI_FsDel(file10path))!=NDK_OK){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:删除%s文件失败(%d)", Tools.getLineInfo(),file10path,ret);
					return "删除文件失败";
				}
				if((ret = JniNdk.JNI_FsExist(file10path))==NDK_OK){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:%s文件仍存在(%d)", Tools.getLineInfo(),file10path,ret);
					return "文件仍存在";
				}
			}
			if((fd = JniNdk.JNI_FsOpen(file10path, "w"))<0){
				gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,file10path,fd);
				return "打开文件失败";
			}
			for(int i=0;i<5;i++)
			{
				byte[] sBuf = new byte[1024];
				Arrays.fill(sBuf, (byte)(i+0x30));
				if((ret = JniNdk.JNI_FsWrite(fd, sBuf, sBuf.length))!=1024)// 文件写操作是追加的方式 不需要seek
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:写文件失败(%d)", Tools.getLineInfo(),ret);
					return "写文件失败";
				}
				System.arraycopy(sBuf, 0, defineBuf, i*1024, 1024);
			}
			
			if((ret = JniNdk.JNI_FsClose(fd))!=NDK_OK)
			{
				gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:关闭文件失败(%d)", Tools.getLineInfo(),ret);
				return "文件关闭失败";
			}
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_fs.cls_printf(String.format("FsTask%d...已成功%d",bak-cnt,succ).getBytes());
				if((fd = JniNdk.JNI_FsOpen(file10path, "w"))<0){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,file10path,fd);
					continue;
				}
				// 当前位置
				int offset = 100;/*(int) (Math.random()*1024);*/
				if((ret = JniNdk.JNI_FsSeek(fd, offset, 2))!=NDK_OK){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:Seek文件失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				// 重新写100B的数据到特定位置
				byte[] tempW = new byte[100];
				for (int i = 0; i < tempW.length; i++) {
					tempW[i] = (byte) (Math.random()*256);
				}
				if((ret = JniNdk.JNI_FsWrite(fd, tempW, tempW.length))!=100){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:写文件失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				System.arraycopy(tempW, 0, defineBuf, offset, 100);
				JniNdk.JNI_FsClose(fd);// 关闭写模式的文件
				if((fd = JniNdk.JNI_FsOpen(file10path, "r"))<0){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,path,ret);
					continue;
				}
				Arrays.fill(memBuf, (byte)0x00);
				if((ret = JniNdk.JNI_FsRead(fd, memBuf, memBuf.length))!=memBuf.length){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:读文件失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				// 比较文件的内容
				if(Tools.memcmp(defineBuf, memBuf, 5*1024)==false){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:比较读写文件内容错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.ASCII2String(memBuf));
					continue;
				}
				// 测试后置
				if((ret = JniNdk.JNI_FsClose(fd))!=NDK_OK)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:关闭文件失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				//读和比较前10个文件
				succ++;
				publishProgress(bak-cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			// 测试后置:比较文件 删除文件
			boolean is = fileCompare(writebuf);
			if(!is){
				gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:第%d次：十个文件内容变化", Tools.getLineInfo(), bak-cnt);
			}
			if((ret = JniNdk.JNI_FsDel(file10path))!=NDK_OK){
				gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:删除%s文件失败(%d)", Tools.getLineInfo(),file10path,ret);
			}
			String result = String.format(Locale.CHINA,"文件系统任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("FsTask", "FsTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask",1, result);
	        taskStatus++;
	    }
	}
	
	/**本地文件读写任务*/
	class SdFsTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("SdFsTask", "SdFsTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			
			byte[] writebuf = new byte[BUFLEN_SD];
			byte[] readbuf = new byte[BUFLEN_SD];
			Arrays.fill(writebuf, (byte) 0);
			
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_sdfs.cls_printf(String.format("SdFsTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				if((ret = fileSystem.JDK_FsOpen(SDFILETEST, "w")) != NDK_OK)
				{
					gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask", 1, "line %d:%s第%d次:开打文件失败(%d)", Tools.getLineInfo(), TESTITEM, bak-cnt, ret);
					continue;
				}
				for (int j = 0; j < writebuf.length; j++) 
				{
					writebuf[j] = (byte) (Math.random() * 100);
				}
				if((fileSystem.JDK_FsWrite(SDFILETEST, writebuf, writebuf.length, 0)) != writebuf.length)
				{
					gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask", 1, "line %d:%s第%d次:写文件失败", Tools.getLineInfo(), TESTITEM, bak-cnt);
					continue;
				}
				
				if((ret = fileSystem.JDK_FsOpen(SDFILETEST, "r")) != NDK_OK)
				{
					gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask",1, "line %d:%s第%d次:打开文件失败(%d)", Tools.getLineInfo(), TESTITEM, bak-cnt, ret);
					continue;
				}
				
				Arrays.fill(readbuf, (byte) 0);
				FileInputStream fileIn = null;
				try {
					fileIn = new FileInputStream(new File(SDFILETEST));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if((ret = (fileSystem.JDK_FsRead(fileIn, readbuf,readbuf.length))) != readbuf.length)
				{
					gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask", 1, "line %d:%s第%d次:文件读失败(%d)", Tools.getLineInfo(), TESTITEM, bak-cnt, ret);
					continue;
				}
				
				if(!Arrays.equals(writebuf, readbuf))
				{
					gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask", 1, "line %d:%s第%d次:文件校验错误", Tools.getLineInfo(), TESTITEM, bak-cnt);
					continue;
				}
				
				if((fileSystem.JDK_FsDel(SDFILETEST)) < 0)
				{
					gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask", 1, "line %d:%s第%d次:文件读失败(%d)", Tools.getLineInfo(), TESTITEM, bak-cnt, ret);
					continue;
				}
				
				succ++;
				publishProgress(bak-cnt);
			}
			String result = String.format(Locale.CHINA,"本地文件读写任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("SdFsTask", "SdFsTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_sdfs.cls_show_msg1_record(CLASS_NAME, "SdFsTask",1, result);
	        Log.i("SdFsTask", "SdFsTask end");
	        taskStatus++;
	    }
	}
	
	/**系统设置任务*/
	class SysConfigTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("SysConfigTask", "SysConfigTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
//			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			try 
			{
				settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			} catch (NoClassDefFoundError e) 
			{
				e.printStackTrace();
				String result = String.format(Locale.CHINA,"line %d:测试终止，找不到该类，抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
		        return result;
			}
			int lightValue = settingsManager.getScreenBrightness();
			int screenOffTime = getScreenOffTime();
			boolean memory = settingsManager.isShowBatteryPercent();
			String[] language = {"zh-CN","zh-HK","ja-JP","ko-KR"};
			String errorValue[]=new String[]{"err1","err2"};
			int brightness = random.nextInt(256);
			int rand = random.nextInt(2);
			boolean rand2 = true;
			while(cnt>1){
				if(isCancel) break;
				cnt--;
				gui_sysconfig.cls_printf(String.format("SysConfigTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				//设随机
				brightness = random.nextInt(256);
				rand = random.nextInt(2);
				rand2 = rand == 0 ? true : false;
				
				settingsManager.setScreenBrightness(brightness);
				settingsManager.setScreenTimeout(-1);
				settingsManager.setSettingStorageDispley(rand);
				settingsManager.setAllApkVerifyEnable();
				settingsManager.setSettingAppDispley(rand);
				settingsManager.setSettingApkNeedLogin(rand);
				settingsManager.setSettingHomeDispley(rand);
				settingsManager.setSettingPrivacyDispley(rand);
				settingsManager.setShowBatteryPercent(rand2);
				settingsManager.setSettingBatteryDispley(rand);
				settingsManager.setSettingDataUsageDispley(rand);
				settingsManager.setSettingPrintSettingsDispley(rand);				
				settingsManager.setSettingAccessibilitySettingsDispley(rand);
				settingsManager.setSettingDevelopmentSettingsDispley(rand);
				settingsManager.setSettingLocationSettingsDispley(rand);
				settingsManager.setSettingSecuritySettingsDispley(rand);
				
				if(GlobalVariable.currentPlatform==Model_Type.N900_3G){
					settingsManager.setDeepSleepEnabled(rand2);
					try
					{
						settingsManager.setBluetoothFileTransfer(rand);
					} catch (NoSuchMethodError e) 
					{
						gui_sysconfig.cls_only_write_msg(CLASS_NAME,"SysConfigTask","此版本不支持设置禁止蓝牙传输文件");
					}
				} else{
					settingsManager.setSettingVpnDispley(rand);
					settingsManager.setStatusBarEnabled(rand);
					settingsManager.setStatusBarAdbNotify(rand);
					settingsManager.setSettingLanguageSpellCheckerDisplay(rand);
					settingsManager.setSettingLockScreenDisplay(rand);
					settingsManager.setSettingLanguageUserDictionaryDisplay(rand);
					settingsManager.setSettingNotificationItemsDisplay("00000000");
					settingsManager.setSettingLocales(language);
					settingsManager.setSettingOtaUpdateEnabled(rand2);
					settingsManager.setSettingWallpaperDisplay(rand);
					settingsManager.setSettingDeviceInfoItemsDisplay("11111");
					settingsManager.setTetherDisplay(rand);
					settingsManager.setWifiInstallCedentialDisplay(rand);
					settingsManager.setSettingProcessorDisplay(rand);
					if(GlobalVariable.gCurPlatVer==Platform_Ver.A5)
						settingsManager.setAppSignatureVerificationScheme(errorValue);// A7这个接口不支持
				}
				
				// 只有虚拟的home键支持
				if(GlobalVariable.currentPlatform == Model_Type.N900_3G || GlobalVariable.currentPlatform == Model_Type.N900_4G ){
					settingsManager.setAppSwitchKeyEnabled(rand2);
					settingsManager.relayoutNavigationBar(rand);
				}
				
				// 
				if(GlobalVariable.currentPlatform == Model_Type.N910 || GlobalVariable.currentPlatform == Model_Type.N900_4G ){
					settingsManager.setPaymentCertUpdateDisplay(rand);
					settingsManager.disableAppCommunication(errorValue);
					settingsManager.setHomeKeyEnabled(rand2);
				}
				
				succ++;
				publishProgress(bak-cnt);
			}
			//最后一次执行恢复默认设置
			cnt--;
			gui_sysconfig.cls_printf(String.format( "SysConfigTask%d...",bak-cnt).getBytes());
			
			settingsManager.setScreenBrightness(lightValue);
			settingsManager.setScreenTimeout(screenOffTime);
			settingsManager.setSettingStorageDispley(0);
			settingsManager.setAllApkVerifyDisable();
			settingsManager.setSettingAppDispley(0);
			settingsManager.setSettingApkNeedLogin(0);
			settingsManager.setSettingHomeDispley(1);
			settingsManager.setSettingPrivacyDispley(0);
			if(memory)
				settingsManager.setShowBatteryPercent(true);
			else
				settingsManager.setShowBatteryPercent(false);
			settingsManager.setSettingBatteryDispley(0);
			settingsManager.setSettingDataUsageDispley(1);
			settingsManager.setSettingPrintSettingsDispley(1);				
			settingsManager.setSettingAccessibilitySettingsDispley(0);
			settingsManager.setSettingDevelopmentSettingsDispley(0);
			settingsManager.setSettingLocationSettingsDispley(0);
			settingsManager.setSettingSecuritySettingsDispley(0);
			
			if(GlobalVariable.currentPlatform==Model_Type.N900_3G){
				settingsManager.setDeepSleepEnabled(false);
				try
				{
					settingsManager.setBluetoothFileTransfer(0);
				} catch (NoSuchMethodError e) 
				{
					gui.cls_only_write_msg(CLASS_NAME, "SysConfigTask","此版本不支持设置禁止蓝牙传输文件");
				}
			} else{
				settingsManager.setSettingVpnDispley(1);
				settingsManager.setStatusBarEnabled(1);
				settingsManager.setStatusBarAdbNotify(1);
				settingsManager.setSettingLanguageSpellCheckerDisplay(1);
				settingsManager.setSettingLockScreenDisplay(0);
				settingsManager.setSettingLanguageUserDictionaryDisplay(1);
				settingsManager.setSettingNotificationItemsDisplay("00001111");
				settingsManager.setSettingLocales(language);
				settingsManager.setSettingOtaUpdateEnabled(false);
				settingsManager.setSettingWallpaperDisplay(0);
				settingsManager.setSettingDeviceInfoItemsDisplay("00000");
				settingsManager.setTetherDisplay(1);
				settingsManager.setWifiInstallCedentialDisplay(1);
				settingsManager.setSettingProcessorDisplay(1);
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A5)
					settingsManager.setAppSignatureVerificationScheme(null);//A7平台不支持该接口
			}
			
			// 虚拟的home键支持
			if(GlobalVariable.currentPlatform == Model_Type.N900_3G || GlobalVariable.currentPlatform == Model_Type.N900_4G ){
				settingsManager.setAppSwitchKeyEnabled(true);
				settingsManager.relayoutNavigationBar(0);
			}
			
			if(GlobalVariable.currentPlatform == Model_Type.N910 || GlobalVariable.currentPlatform == Model_Type.N900_4G ){
				settingsManager.setPaymentCertUpdateDisplay(0);
				settingsManager.disableAppCommunication(null);
				settingsManager.setHomeKeyEnabled(true);
			}
			succ++;
			publishProgress(bak-cnt);
			
			String result = String.format(Locale.CHINA,"系统设置任务结束，已执行%d次，成功%d次，请查看系统设置和相关用例是否有异常",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("SysConfigTask", "SysConfigTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_sysconfig.cls_show_msg1_record(CLASS_NAME, "SysConfigTask",1, result);
	        Log.i("SysConfigTask", "SysConfigTask end");
	        taskStatus++;
	    }
	}
	
	/**LED灯任务*/
	class LedTask extends AsyncTask<Integer, Integer, String>{

		@Override
		protected void onPreExecute() {
			Log.i("LedTask", "LedTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_led.cls_printf(String.format("LedTask%d...led全亮--全闪--全灭...已成功%d",bak-cnt,succ).getBytes());
				
				//case1: led灯全亮
				if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_ON.led() | EM_LED.LED_RFID_YELLOW_ON.led() | EM_LED.LED_RFID_GREEN_ON.led() | EM_LED.LED_RFID_BLUE_ON.led())) != NDK_OK)
				{
					gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask", 1, "line %d:第%d次:led灯全亮测试失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				SystemClock.sleep(1000);

				//case2: led灯全闪
				if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_FLICK.led() | EM_LED.LED_RFID_YELLOW_FLICK.led() | EM_LED.LED_RFID_GREEN_FLICK.led() | EM_LED.LED_RFID_BLUE_FLICK.led())) != NDK_OK)
				{
					gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask", 1, "line %d:第%d次:led灯全闪测试失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				SystemClock.sleep(1000);

				//case3: led灯全灭
				if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led())) != NDK_OK)
				{
					gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask", 1, "line %d:第%d次:led灯全灭测试失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				SystemClock.sleep(1000);
				
				succ++;
				publishProgress(bak-cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"LED灯任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("LedTask", "LedTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask", 1, result);
	        Log.i("LedTask", "LedTask end");
	        taskStatus++;
	    }
	} 

	/**SysDelay任务 */
	class SysDelayTask extends AsyncTask<Integer, Integer, String>{


			@Override
			protected void onPreExecute() {
				Log.i("SysDelayTask", "SysDelayTask onPreExecute");
			}
			
			@Override
			protected String doInBackground(Integer... values) {
				int succ = 0,cnt = 0,bak = 0;
				bak = cnt = cycletime;
				int[] punTime =new int[1];
				while(cnt>0){
					if(isCancel) break;
					cnt--;
					gui_sys_delay.cls_printf(String.format( "SysDelayTask%d...已成功%d",bak-cnt,succ).getBytes());
					JniNdk.JNI_Sys_StartWatch();
					JniNdk.JNI_Sys_Delay(TESTTIME*10);
					JniNdk.JNI_Sys_StopWatch(punTime);
					/**SysDelay延时有一定的误差，保证正常误差范围内即可，每次误差20ms by20200421,多个模块并发无法计算调用了多少次ndk接口，每次ndk接口要加20ms的延时*/
					/*if(Math.abs(punTime[0]-TESTTIME*10)>=20)//0.03为偏差值
					{
						gui_sys_delay.cls_show_msg1_record(CLASS_NAME, "SysDelayTask",g_keeptime, "line %d:第%d次：SysDelay延时测试失败", Tools.getLineInfo(),bak-cnt);
						continue;
					}*/
					succ++;
					publishProgress(bak-cnt);
					
					if(sleeptest){
						SystemClock.sleep(SLEEPTIME);
					}
				}
				
				String result = String.format(Locale.CHINA,"SysDelay任务结束，已执行%d次，成功%d次",bak-cnt,succ);
		        return result;
			}
			
			@Override
		    protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
		        int progress = values[0];
		        Log.i("SysDelayTask", "SysDelayTask onProgressUpdate progress---->" + progress);
			}
			
			@Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		        gui_sys_delay.cls_show_msg1_record(CLASS_NAME, "SysDelayTask", 1, result);
		        Log.i("SysDelayTask", "SysDelayTask end");
		        taskStatus++;
		    }
		} 	 
	/** SysMsDelay任务 */
	class SysMsDelayTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("SysMsDelay", "SysMsDelay onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			int[] punTime = new int[1];
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_msdelay.cls_printf(String.format("SysMsDelay%d...已成功%d",bak-cnt,succ).getBytes());
				JniNdk.JNI_Sys_StartWatch();
				JniNdk.JNI_Sys_MsDelay(TESTTIME*1000);
				JniNdk.JNI_Sys_StopWatch(punTime);
				/**SysDelay延时有一定的误差，保证正常误差范围内即可，每次误差20ms by20200421，并发测试无法计算有多少个ndk接口调用，每个ndk接口每次调用延时要加20ms*/
				/*if(Math.abs(punTime[0]-TESTTIME*1000)>=20)
				{
					gui_sys_msdelay.cls_show_msg1_record(CLASS_NAME, "SysMsDelayTask", g_keeptime, "line %d:第%d次：SysMsDelay延时测试失败",Tools.getLineInfo(), bak - cnt);
					continue;
				}*/
				succ++;
				publishProgress(bak - cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}

			String result = String.format(Locale.CHINA,"SysMsDelay任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			Log.i("SysMsDelay", "SysMsDelay onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_msdelay.cls_show_msg1_record(CLASS_NAME, "SysMsDelay", 1, result);
			Log.i("SysMsDelay", "SysMsDelay end");
			taskStatus++;
		}
	} 
	/**  SysPosTime任务 */
	class SysPosTimeTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("SysPosTimeTask", "SysPosTimeTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			int ret = -1;
			TimeNewland stGetPosTime = new TimeNewland();
			TimeNewland stSetPosTime = new TimeNewland();
//			TimeNewland stSetPosTime0 = new TimeNewland();
			TimeNewland stOldPosTime = new TimeNewland();
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_postime.cls_printf(String.format("SysPosTimeTask%d...已成功%d",bak-cnt,succ).getBytes());
				if((ret = JniNdk.JNI_Sys_GetPosTime(stOldPosTime))!=NDK_OK)
				{
					gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, "line %d:第%d次：SysPosTimeTask测试失败(%d)",Tools.getLineInfo(), bak - cnt,ret);
					continue;
				}
				// 设置POS时间2011:11:11.11:11:11
				stSetPosTime.obj_year = 2011-1900;
				stSetPosTime.obj_mon = 11-1;
				stSetPosTime.obj_mday =11;
				stSetPosTime.obj_hour = 11;
				stSetPosTime.obj_min = 11;
				stSetPosTime.obj_sec = 11;
				if((ret = JniNdk.JNI_Sys_SetPosTime(stSetPosTime))!=NDK_OK)
				{
					gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, "line %d:第%d次：SysPosTimeTask测试失败(%d)",Tools.getLineInfo(), bak - cnt,ret);
					continue;
				}
				JniNdk.JNI_Sys_GetPosTime(stGetPosTime);
				if(stSetPosTime.obj_year!=stGetPosTime.obj_year||stSetPosTime.obj_mon!=stGetPosTime.obj_mon
					||stSetPosTime.obj_mday!=stGetPosTime.obj_mday||stSetPosTime.obj_hour!=stGetPosTime.obj_hour
					||stSetPosTime.obj_min!=stGetPosTime.obj_min)
				{
					gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, "line %d:第%d次：SysPosTimeTask测试失败",Tools.getLineInfo(), bak - cnt);
					continue;
				}
				succ++;
				publishProgress(bak - cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"SysPosTimeTask任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			Log.i("SysPosTimeTask", "SysPosTimeTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", 1, result);
			Log.i("SysPosTimeTask", "SysPosTimeTask end");
			taskStatus++;
		}
	} 	 
	/**  SysTime任务 */
	class SysTimeTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("SysTimeTask", "SysTimeTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			int ret = -1;
			long[] ultime=new long[2], ultime1=new long[2], subtime=new long[2];
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_time.cls_printf(String.format("SysTimeTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				if((ret = JniNdk.JNI_Sys_Time(ultime)) != NDK_OK)//返回经过的秒
				{
					gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, "line %d:第%d次：SysTimeTask测试失败(%d)",Tools.getLineInfo(), bak - cnt,ret);
					continue;
				}
				SystemClock.sleep(sleepTime);
				if((ret = JniNdk.JNI_Sys_Time(ultime1)) != NDK_OK)//返回经过的秒
				{
					gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, "line %d:第%d次：SysTimeTask测试失败(%d)",Tools.getLineInfo(), bak - cnt,ret);
					continue;
				}
				if((subtime[0]=ultime1[0]-ultime[0])!=sleepTime/1000)
				{
					gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, "line %d:第%d次：SysTimeTask校验失败(%d)",Tools.getLineInfo(), bak - cnt,Long.valueOf(subtime[0]).intValue());
					continue;
				}
				succ++;
				publishProgress(bak - cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"SysTimeTask任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			Log.i("SysTimeTask", "SysTimeTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", 1, result);
			Log.i("SysTimeTask", "SysTimeTask end");
			taskStatus++;
		}
	} 	 
	/**  SysGetPosInfo任务 */
	class SysGetPosInfoTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("SysGetPosInfoTask", "SysGetPosInfoTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			int ret = -1;
			int unLen=0;
			byte[] sBuf=new byte[128];
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_getposinfo.cls_printf(String.format("SysGetPosInfoTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				//读取pos机器类型
				Arrays.fill(sBuf, (byte)0);
				if((ret=JniNdk.JNI_Sys_GetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_TYPE.secsyshwinfo(), unLen, sBuf)) != NDK_OK)
				{
					gui_sys_getposinfo.cls_show_msg1_record(CLASS_NAME, "SysGetPosInfoTask", g_keeptime, "line %d:第%d次：读取pos机器类型失败(%d)",Tools.getLineInfo(), bak - cnt,ret);
					continue;
				}


				//读取pos硬件信息
				Arrays.fill(sBuf, (byte)0xff);
				if((ret=JniNdk.JNI_Sys_GetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_HARDWARE_INFO.secsyshwinfo(), unLen, sBuf)) != NDK_OK)
				{
					gui_sys_getposinfo.cls_show_msg1_record(CLASS_NAME, "SysGetPosInfoTask", g_keeptime, "line %d:第%d次：读取pos硬件信息失败(%d)",Tools.getLineInfo(), bak - cnt,ret);
					continue;
				}
				
				succ++;
				publishProgress(bak - cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"SysGetPosInfoTask任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			Log.i("SysGetPosInfoTask", "SysGetPosInfoTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_getposinfo.cls_show_msg1_record(CLASS_NAME, "SysGetPosInfoTask", 1, result);
			Log.i("SysGetPosInfoTask", "SysGetPosInfoTask end");
			taskStatus++;
		}
	} 		 
	
//	 /**81键盘任务*/
//	class KeyBoardTask extends AsyncTask<Integer, Integer, String> {
//		@Override
//		protected void onPreExecute() {
//			Log.i("KeyBoardTask", "KeyBoardTask onPreExecute");
//		}
//		
//		@Override
//		protected String doInBackground(Integer... values) {
////			int ret = -1;
//			int succ = 0,cnt = 0,bak = 0;
//			bak = cnt = cycletime;
//			
//			while(cnt>0){
//				if(isCancel) break;
//				cnt--;
//				gui_keyboard.cls_printf(String.format("KeyBoardTask%d...已成功%d",bak-cnt,succ).getBytes());
//				
//				succ++;
//				publishProgress(bak-cnt);
//			}
//			String result = String.format(Locale.CHINA,"键盘任务结束，已执行%d次，成功%d次",bak-cnt,succ);
//	        return result;
//		}
//		
//		@Override
//	    protected void onProgressUpdate(Integer... values) {
//			super.onProgressUpdate(values);
//	        int progress = values[0];
//	        Log.i("KeyBoardTask", "KeyBoardTask onProgressUpdate progress---->" + progress);
//		}
//		
//		@Override
//	    protected void onPostExecute(String result) {
//	        super.onPostExecute(result);
//	        gui_keyboard.cls_show_msg1_record(CLASS_NAME, "KeyBoardTask",1, result);
//	        Log.i("KeyBoardTask", "KeyBoardTask end");
//	        taskStatus++;
//	    }
//	}
	
	class THK88Task extends AsyncTask<Integer, Integer, String>
	{
		@Override
		protected void onPreExecute() {
			Log.i("THK88Task", "THK88Task onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0,cnt = 0,bak = 0,iRet=-1;
			byte[] sn_32=new byte[32];
			bak = cnt = cycletime;
			ScanUtil softManager1 = new ScanUtil(myactivity);
			while(cnt>0){
				if(isCancel) 
					break;
				cnt--;
				gui_thk88.cls_printf(String.format("THK88Task%d...已成功%d",bak-cnt,succ).getBytes());
				if((iRet = softManager1.setThk88Power(1))!=SDK_OK)
				{
					gui_thk88.cls_show_msg1_record(CLASS_NAME,"THK88Task",g_keeptime, "line %d:第%d次:THK88上电失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				
				if((iRet = softManager1.getThk88ID(sn_32))!=SDK_OK)
				{
					gui_thk88.cls_show_msg1_record(CLASS_NAME,"THK88Task",g_keeptime, "line %d:第%d次:THK88读取SN号失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				
				// case2:thk88下电后并读取SN号进行文件的读操作
				if((iRet = softManager1.setThk88Power(0))!=SDK_OK)
				{
					gui_thk88.cls_show_msg1_record(CLASS_NAME,"THK88Task",g_keeptime, "line %d:第%d次:THK88上电失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				
				if((iRet = softManager1.getThk88ID(sn_32))!=SDK_OK)
				{
					gui_thk88.cls_show_msg1_record(CLASS_NAME,"THK88Task",g_keeptime, "line %d:第%d次:THK88读取SN号失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				succ++;
				publishProgress(bak-cnt);
			}
			String result = String.format(Locale.CHINA,"THK88Task任务结束,已执行%d次,成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("THK88Task", "THK88Task onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_thk88.cls_show_msg1_record(CLASS_NAME, "THK88Task",1, result);
	        Log.i("THK88Task", "THK88Task end");
	        taskStatus++;
	    }
	}
	 
	 /**
		 * 显示checkBox的对话框
		 */
	@SuppressLint("InflateParams")
	private void showDialog()
	{
		LayoutInflater layout = LayoutInflater.from(myactivity);
		View view = layout.inflate(R.layout.aysnctask_choose, null);
		final ListView lv=(ListView) view.findViewById(R.id.aysnc_task_lv);
		//list = new ArrayList<AsyncBean>();	//为了重新选择时记住已勾选项，移到前面
		// N700机型没有打印模块，N510机型没有磁卡和IC卡模块
		if(list.isEmpty()){
			for (AYSNCTASK_LIST_K21 s : AYSNCTASK_LIST_K21.values())  {
				// 浅并发测试，去除SDK3.0的测试选项
				if(s==AYSNCTASK_LIST_K21.KeyBoardRegTask_SDK3||s==AYSNCTASK_LIST_K21.MagRegTask_SDK3||s==AYSNCTASK_LIST_K21.PrnRegTask_SDK3
						||s==AYSNCTASK_LIST_K21.IccRegTask_SDK3||s==AYSNCTASK_LIST_K21.RfidRegTask_SDK3||s==AYSNCTASK_LIST_K21.PinRegTask_SDK3)
					continue;
				if(s==AYSNCTASK_LIST_K21.KeyBoardTask_SDK2&&GlobalVariable.gModuleEnable.get(Mod_Enable.KeyBoardEnable)==false)
					continue;
				
				if(s==AYSNCTASK_LIST_K21.PrnTask_SDK2&&GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnable)==false)
					continue;
				
				if(s==AYSNCTASK_LIST_K21.MagTask_SDK2&&GlobalVariable.gModuleEnable.get(Mod_Enable.MagEnable)==false)
					continue;
				
				if(s==AYSNCTASK_LIST_K21.IccTask_SDK2&&GlobalVariable.gModuleEnable.get(Mod_Enable.IccEnable)==false)
					continue;
				
				if(s==AYSNCTASK_LIST_K21.SamTask&&GlobalVariable.gModuleEnable.get(Mod_Enable.SamEnable)==false)
					continue;
				
				// N550和X5不支持密码键盘
				if(s==AYSNCTASK_LIST_K21.PinTask_SDK2&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable)==false)
					continue;
				
				CheckBean bean=new CheckBean(s.toString());
				//bean.setChecked(true);
				list.add(bean);
		    }
			for (AYSNCTASK_LIST_ANDROID s : AYSNCTASK_LIST_ANDROID.values())  {
				CheckBean bean=new CheckBean(s.toString());
				//bean.setChecked(true);
				list.add(bean);
		    }
		}
		
		final EditText et_cycletimes = (EditText) view.findViewById(R.id.et_aysnc_cycletimes);
		final RadioGroup rg_sleepcontrol = (RadioGroup) view.findViewById(R.id.rg_sleep_control); 
		//et_cycletimes.setText(cycletime);
		
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ViewHold holder=(ViewHold) arg1.getTag();
				// 改变CheckBox的状态
                holder.cb.toggle();
                if(list.get(arg2).isChecked()){
                	list.get(arg2).setChecked(false);
                }else{
                	list.get(arg2).setChecked(true);
                }
			}
		});
		
		new BaseDialog(myactivity, view, "K21多模块浅并发任务配置界面", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					//是否休眠
					sleeptest=rg_sleepcontrol.getCheckedRadioButtonId()==R.id.rb_normaltest?false:true;
					checkName.removeAll(checkName);
					// 选择获取的check
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i).isChecked())
						checkName.add(list.get(i).getItem1());
					}
	                //次数
					if(et_cycletimes.getText().toString().equals("") || (Integer.parseInt(et_cycletimes.getText().toString())==0)){
						cycletime = 500;
						Log.e("cycletime","cycletime0:"+cycletime);
					}else{
						cycletime = Integer.parseInt(et_cycletimes.getText().toString());
						Log.e("cycletime","cycletime1:"+cycletime);
					}
					
					synchronized (g_lock) {
						g_lock.notify();
					}
				}
				
			}
			
		}).show();
	}
	
	class Myadapter extends BaseAdapter{
		
		public Myadapter() {

		}
		
		@Override
		public int getCount() {
			if(list==null){
				return 0;
			}
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHold holder=null;
			if (null == convertView) {
				holder=new ViewHold();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.aysnc_item, parent,false);
				holder.cb=(CheckBox) convertView.findViewById(R.id.cb_aysnc_task);
				// 为view设置标签
	            convertView.setTag(holder);
			}else{
				 // 取出holder
	            holder = (ViewHold) convertView.getTag();
			}
			holder.cb.setText((CharSequence) list.get(position).getItem1());
			holder.cb.setChecked(list.get(position).isChecked());
			return convertView;
		}
		
	}
	class ViewHold {
		CheckBox cb;
	}
	
	/** 
	 * 原生方法获取锁屏时间，单位为ms
	 */  
	private int getScreenOffTime(){
		int screenOffTime=0;  
	    try{  
	        screenOffTime = Settings.System.getInt(myactivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);  
	    }catch (SettingNotFoundException e) {  
            e.printStackTrace();  
        }  
	    return screenOffTime;  
	} 
	
	//比较前10个文件内容
	private boolean  fileCompare(byte[] writebuf) {
		int fd = -1;
		int ret=-1;
		byte[] readbuf2 = new byte[BUFLEN];
		for (String path : filepath) {
			try {
				if((fd = JniNdk.JNI_FsOpen(path, "r")) < 0){
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask",g_keeptime, "line %d:打开文件失败(%d,%s)", Tools.getLineInfo(), ret,path);
					return false;
				}
				Arrays.fill(readbuf2, (byte)0);
				if((ret = (JniNdk.JNI_FsRead(fd, readbuf2, BUFLEN))) < 0)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:读文件失败(%d,%s)", Tools.getLineInfo(), ret,path);
					JniNdk.JNI_FsClose(fd);
					return false;
				}
				if((ret = JniNdk.JNI_FsClose(fd)) != NDK_OK){
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", 1, "line %d:关闭文件失败(%d,%s)", Tools.getLineInfo(),  ret,path);
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if(!Tools.memcmp(readbuf2,writebuf,BUFLEN)){
				gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:文件校验错误(%s)", Tools.getLineInfo(),path);
				return false;
			}
		}
		return true;
	}
	//写文件操作
	public int writeFile(String path , byte[] writebuf){
		int fd = -1;
		int ret=-1;
		try {
			if((fd = JniNdk.JNI_FsOpen(path, "w")) < 0)
			{
				gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:打开文件失败(%d,%s)", Tools.getLineInfo(), ret,path);
				return NDK_ERR;
			} 
			if((ret = JniNdk.JNI_FsWrite(fd, writebuf, writebuf.length)) != BUFLEN)
			{
				gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:写文件失败(%d,%s)", Tools.getLineInfo(), ret,path);
				JniNdk.JNI_FsClose(fd);
				return NDK_ERR;
			} 
			if((ret = JniNdk.JNI_FsClose(fd)) != NDK_OK)
			{
				gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:关闭文件失败(%d,%s)", Tools.getLineInfo(), ret,path);
				return NDK_ERR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:写文件抛出异常", Tools.getLineInfo());
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	//密码键盘输入
	private byte[] getPinInput()
	{
		final int SEC_VPP_KEY_PIN = 0;// 有pin键密码按下，用*号显示
		final int SEC_VPP_KEY_BACKSPACE = 1;// 退格键按下
		final int SEC_VPP_KEY_CLEAR = 2;// 清除键按下
		final int SEC_VPP_KEY_ENTER = 3;// 确认键按下
		final int SEC_VPP_KEY_ESC = 4;// 取消键按下
//		final int SEC_VPP_KEY_NULL = 5;
		int iRet = -1,count = 0;
		StringBuffer strBuffer = new StringBuffer();
		int[] status = new int[]{0};
		byte[] pinBlock = new byte[20];
		myHandler.sendMessage(myHandler.obtainMessage(TEXTVIEW_SHOW_GETPININPUT, strBuffer));
		while(true)
		{
			iRet = JniNdk.JNI_Sec_GetPinResult(pinBlock, status);
			if (iRet != 0) {
				gui_getpin.cls_show_msg1_record(CLASS_NAME, "GetPinTask",g_keeptime, "line %d:获取键盘输入状态失败(ret = %d)", Tools.getLineInfo(), iRet);
				break;
			}
			switch (status[0]) 
			{
			case SEC_VPP_KEY_PIN:
				count++;
				strBuffer.append("*");
				break;
				
			case SEC_VPP_KEY_ENTER:
				strBuffer.append("\n密码输入完毕！！！");
				break;
				
			case SEC_VPP_KEY_CLEAR:// 清除
				strBuffer.delete(strBuffer.length()-count, strBuffer.length());
				count = 0;
				break;
				
			case SEC_VPP_KEY_BACKSPACE:
				if(count>0)
				{
					strBuffer.delete(strBuffer.length()-1, strBuffer.length());
					count = count-1;
				}
				break;
				
			case SEC_VPP_KEY_ESC:
//				strBuffer.delete(strBuffer.length()-count, strBuffer.length());
//				count = 0;
				strBuffer.append("\n用户取消");
				break;

			default:
				break;
			}
			myHandler.sendMessage(myHandler.obtainMessage(TEXTVIEW_SHOW_GETPININPUT, strBuffer));
			if (status[0]==SEC_VPP_KEY_ENTER||status[0]==SEC_VPP_KEY_ESC|| iRet != 0) {//pinBlock[0] == -122 
				if(iRet==-1122){
					strBuffer.append("\n密码键盘输入超时已退出");
				}
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		strBuffer.setLength(0);
		myHandler.sendMessage(myHandler.obtainMessage(TEXTVIEW_SHOW_GETPININPUT, strBuffer));
		return pinBlock;
	}
}
