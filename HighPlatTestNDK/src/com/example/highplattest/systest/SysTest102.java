package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.newland.scan.ScanUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.AYSNCTASK_LIST_K21;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_LED;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_MAC;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_HWINFO;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.newland.ndk.SecKcvInfo;
import com.newland.ndk.TimeNewland;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest101.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20171018
 * directory 		: 
 * description 		: K21模块深交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  wangxy          20171018         created
 * 					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 * 					修复M0卡未寻卡问题		20200828		陈丁
 ************************************************************************ 
 */
public class SysTest102 extends DefaultFragment
{
	private final String TESTITEM = "K21多模块并发(深并发+浅并发)";
	public final String CLASS_NAME = SysTest102.class.getSimpleName();
	private FileSystem fileSystem = new FileSystem();
	public static ExecutorService exec = Executors.newFixedThreadPool(30); 
	private Config config;
	private StringBuffer strBuffer = new StringBuffer();
	private HashSet<String> checkName = new HashSet<String>();
	private int cycletime = 0;	//自定义任务循环次数,默认5000
	private int taskStatus = 0;	//已完成任务
	private int count = 0;
	//配置参数
	private _SMART_t rfidType = _SMART_t.CPU_A;
	private _SMART_t samType = _SMART_t.SAM1;
	private int felicaChoose=0;
	private static final String fileName = "/appfs/file2";//浅并发路径为file1
	private final int BUFLEN = 5*1024;
	private final int sleepTime = 5*1000;
	private PrintUtil printUtil;
//	private final int MAXWAITTIME = 82800000;
	private final int MAXWAITTIME2 = 10;//打印
	private boolean sleeptest = false;
	private int SLEEPTIME = 25800; //25.8s，略早于k21进入休眠的时间26s左右
	private boolean isCancel = false;
	private boolean initFlag=false;
	//设置
	private SettingsManager settingsManager = null;
	
	public static final int TEXTVIEW_SHOW_K21 = 200;
	public static final int TEXTVIEW_SHOW_RFIDREGISTERTASK = TEXTVIEW_SHOW_K21+1;/**RFID模块SDK3.0*/
	public static final int TEXTVIEW_SHOW_ICCREGISTERTASK = TEXTVIEW_SHOW_K21+2;/**IC模块SDK3.0*/
	public static final int TEXTVIEW_SHOW_MAGREGISTERTASK = TEXTVIEW_SHOW_K21+3;/**MAG模块SDK3.0*/
	public static final int TEXTVIEW_SHOW_PRNREGISTERTASK = TEXTVIEW_SHOW_K21+4;/**PRN模块SDK3.0*/
	public static final int TEXTVIEW_SHOW_PINREGISTERTASK = TEXTVIEW_SHOW_K21+5;/**密码键盘模块SDK3.0*/
	public static final int TEXTVIEW_SHOW_SECTASK = TEXTVIEW_SHOW_K21+7;/**SEC模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_FSTASK = TEXTVIEW_SHOW_K21+8;/**FS模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_KEYBOARDTASK = TEXTVIEW_SHOW_K21+9;/**预留，暂时无用*/
	public static final int TEXTVIEW_SHOW_LEDTASK = TEXTVIEW_SHOW_K21+10;/**LED模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_SYSDELAYTASK = TEXTVIEW_SHOW_K21+11;/**延时模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_SYSMSDELAYTASK =TEXTVIEW_SHOW_K21+12;/**延时模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_SYSPOSTIMETASK = TEXTVIEW_SHOW_K21+13;/**POS事件模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_SYSTIMETASK = TEXTVIEW_SHOW_K21+14;/**POS时间模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_SYSGETPOSINFOTASK = TEXTVIEW_SHOW_K21+15;/**POS信息模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_PINREGISTERINPUT = TEXTVIEW_SHOW_K21+16;
	public static final int TEXTVIEW_SHOW_MAGTASK = TEXTVIEW_SHOW_K21+17;/**MAG模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_ICTASK = TEXTVIEW_SHOW_K21+18;/**IC模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_RFIDTASK = TEXTVIEW_SHOW_K21+19;/**RFID模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_PRNTASK = TEXTVIEW_SHOW_K21+20;/**PRN模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_PINTASK = TEXTVIEW_SHOW_K21+21;/**PIN模块SDK2.0*/
	public static final int TEXTVIEW_SHOW_SAMTASK = TEXTVIEW_SHOW_K21+22;/**SAM模块*/
	public static final int TEXTVIEW_SHOW_SYSVERTASK = TEXTVIEW_SHOW_K21+23;/**Sys Version模块*/
	public static final int TEXTVIEW_SHOW_THK88TASK = TEXTVIEW_SHOW_K21+24;/**add by zhengxq 20181221*/
	
	
	//隐藏界面控件
	public static final int TEXTVIEW_SHOW_GONET = TEXTVIEW_SHOW_K21-1;

	//界面显示
	public static final int TEXTVIEW_VIEW_BASE = 2;
	public static final int TEXTVIEW_RF_REG_VIEW = TEXTVIEW_VIEW_BASE+1;
	public static final int TEXTVIEW_ICC_REG_VIEW = TEXTVIEW_VIEW_BASE+2;
	public static final int TEXTVIEW_SAM_VIEW = TEXTVIEW_VIEW_BASE+3;
	public static final int TEXTVIEW_MAG_REG_VIEW = TEXTVIEW_VIEW_BASE+4;
	public static final int TEXTVIEW_PRN_REG_VIEW = TEXTVIEW_VIEW_BASE+5;
	public static final int TEXTVIEW_SEC_VIEW = TEXTVIEW_VIEW_BASE+6;
	public static final int TEXTVIEW_PIN_REG_VIEW = TEXTVIEW_VIEW_BASE+7;
	public static final int TEXTVIEW_FS_VIEW = TEXTVIEW_VIEW_BASE+8;
	public static final int TEXTVIEW_KEYBOARD_REG_VIEW = TEXTVIEW_VIEW_BASE+9;
	public static final int TEXTVIEW_LED_VIEW = TEXTVIEW_VIEW_BASE+10;
	public static final int TEXTVIEW_SYS_DELAY_VIEW = TEXTVIEW_VIEW_BASE+11;
	public static final int TEXTVIEW_SYS_MSDELAY_VIEW =TEXTVIEW_VIEW_BASE+12;
	public static final int TEXTVIEW_SYS_POSTIME_VIEW = TEXTVIEW_VIEW_BASE+13;
	public static final int TEXTVIEW_SYS_TIME_VIEW = TEXTVIEW_VIEW_BASE+14;
	public static final int TEXTVIEW_SYS_GETPOSINFO_VIEW = TEXTVIEW_VIEW_BASE+15;
	public static final int TEXTVIEW_MAG_VIEW = TEXTVIEW_VIEW_BASE+16;
	public static final int TEXTVIEW_ICC_VIEW = TEXTVIEW_VIEW_BASE+17;
	public static final int TEXTVIEW_RFID_VIEW = TEXTVIEW_VIEW_BASE+18;
	public static final int TEXTVIEW_PRN_VIEW = TEXTVIEW_VIEW_BASE+19;
	public static final int TEXTVIEW_PIN_VIEW = TEXTVIEW_VIEW_BASE+20;
	public static final int TEXTVIEW_KEYBOARD_VIEW = TEXTVIEW_VIEW_BASE+21;
	public static final int TEXTVIEW_SYS_VERSION = TEXTVIEW_VIEW_BASE+22;
	public static final int TEXTVIEW_THK88 = TEXTVIEW_VIEW_BASE+23;
	
	
	public static final int IMAGEBACK_GONE = 1220;
	
	private ArrayList<AsyncBean> list;
	private Myadapter adapter=new Myadapter();
//	private int rfFlag=-1,iccFlag=-1,prnFlag=-1,magFlag=-1,pinFlag=-1;
	//安全
	private SecKcvInfo kcvInfo = new SecKcvInfo();
	/*重写界面提示语部分*/
	private TextView mtvShow2_rf,mtvShow2_icc,mtvshow2_sam,mtvShow2_prn,mtvShow2_mag,mtvShow2_led,mtvShow2_sys_delay,mtvShow2_sec,mtvShow2_sys_msdelay
	                ,mtvShow2_sys_postime,mtvShow2_sys_time,mtvShow2_sys_getposinfo,mtvShow2_fs,mtvShow2_pin,mtvShow_getpin_input,mtvShow2_mag2,
	                mtvShow2_icc2,mtvShow2_rfid2,mtvShow2_prn2,mtvShow2_pin2,mtvShow2_sys_version,mtvShow_thk88;
	//checkFlag当前操作是否为校验操作
	private boolean checkFlag=false;
	NotifyEventListener rflistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.d("监听到rf");
			rfFlag = eventNum;
			return SUCC;
		}
	};
	NotifyEventListener icclistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.d("监听到icc");
			iccFlag = eventNum;
			return SUCC;
		}
	};
	NotifyEventListener maglistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.d("监听到mag");
			magFlag = eventNum;
			return SUCC;
		}
	};
	NotifyEventListener pinlistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.d("监听到pin");
			pinFlag = eventNum;
			return SUCC;
		}
	};
	NotifyEventListener prnlistener = new NotifyEventListener() {

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) {
			LoggerUtil.d("监听到print");
			prnFlag = eventNum;
			return SUCC;
		}
	};		
	Handler myHandler = new Handler()
	{
		@SuppressLint("HandlerLeak")
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
				
			case TEXTVIEW_MAG_REG_VIEW :
				mtvShow2_mag.setVisibility(View.VISIBLE);
				MagRegisterEventTask magRegTask = new MagRegisterEventTask();
				magRegTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_MAG_VIEW:
				mtvShow2_mag2.setVisibility(View.VISIBLE);
				MagTask magTask = new MagTask();
				magTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_RF_REG_VIEW :
				mtvShow2_rf.setVisibility(View.VISIBLE);
				RfidRegisterEventTask rfidRegTask = new RfidRegisterEventTask();
				rfidRegTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_RFID_VIEW:
				mtvShow2_rfid2.setVisibility(View.VISIBLE);
				SmartTask rfidTask = new SmartTask();
				rfidTask.executeOnExecutor(exec, rfidType);
				count++;
				break;
				
			case TEXTVIEW_ICC_REG_VIEW :
				mtvShow2_icc.setVisibility(View.VISIBLE);
				IccRegisterEventTask iccRegTask = new IccRegisterEventTask();
				iccRegTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_ICC_VIEW:
				mtvShow2_icc2.setVisibility(View.VISIBLE);
				SmartTask iccTask = new SmartTask();
				iccTask.executeOnExecutor(exec, _SMART_t.IC);
				count++;
				break;
				
			case TEXTVIEW_SAM_VIEW:
				mtvshow2_sam.setVisibility(View.VISIBLE);
				SmartTask samTask = new SmartTask();
				samTask.executeOnExecutor(exec, samType);
				count++;
				break;
				
			case TEXTVIEW_PIN_REG_VIEW :
				imageBack.setVisibility(View.VISIBLE);
				mtvShow2_pin.setVisibility(View.VISIBLE);
				mtvShow_getpin_input.setVisibility(View.VISIBLE);
				PinRegisterTask pinRegTask = new PinRegisterTask();
				pinRegTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_PIN_VIEW:
				imageBack.setVisibility(View.VISIBLE);
				mtvShow2_pin2.setVisibility(View.VISIBLE);
				mtvShow_getpin_input.setVisibility(View.VISIBLE);
				PinTask pinTask = new PinTask();
				pinTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_SYS_VERSION:
				mtvShow2_sys_version.setVisibility(View.VISIBLE);
				SysVersionTask sysVersionTask = new SysVersionTask();
				sysVersionTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_PRN_REG_VIEW :
				mtvShow2_prn.setVisibility(View.VISIBLE);
				PrnRegisterEventTask prnRegTask = new PrnRegisterEventTask();
				prnRegTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_PRN_VIEW:
				mtvShow2_prn2.setVisibility(View.VISIBLE);
				PrnTask prnTask = new PrnTask();
				prnTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_KEYBOARD_VIEW :
				break;
			case TEXTVIEW_SEC_VIEW :
				mtvShow2_sec.setVisibility(View.VISIBLE);
				SecTask secTask = new SecTask();
				secTask.executeOnExecutor(exec);
				count++;
				
				break;
			case TEXTVIEW_FS_VIEW:
				mtvShow2_fs.setVisibility(View.VISIBLE);
				FsTask fsTask = new FsTask();
				fsTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_THK88:
				mtvShow_thk88.setVisibility(View.VISIBLE);
				THK88Task thk88Task = new THK88Task();
				thk88Task.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_LED_VIEW :
				mtvShow2_led.setVisibility(View.VISIBLE);
				LedTask ledTask = new LedTask();
				ledTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_SYS_DELAY_VIEW :
				mtvShow2_sys_delay.setVisibility(View.VISIBLE);
				SysDelayTask sysdelayTask = new SysDelayTask();
				sysdelayTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_SYS_MSDELAY_VIEW :
				mtvShow2_sys_msdelay.setVisibility(View.VISIBLE);
				SysMsDelayTask sysmsdelayTask = new SysMsDelayTask();
				sysmsdelayTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_SYS_POSTIME_VIEW :
				mtvShow2_sys_postime.setVisibility(View.VISIBLE);
				SysPosTimeTask syspostimeTask = new SysPosTimeTask();
				syspostimeTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_SYS_GETPOSINFO_VIEW :
				mtvShow2_sys_getposinfo.setVisibility(View.VISIBLE);
				SysGetPosInfoTask sysgetposinfoTask = new SysGetPosInfoTask();
				sysgetposinfoTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_SYS_TIME_VIEW :
				mtvShow2_sys_time.setVisibility(View.VISIBLE);
				SysTimeTask systimeTask = new SysTimeTask();
				systimeTask.executeOnExecutor(exec);
				count++;
				break;
				
			case TEXTVIEW_SHOW_RFIDREGISTERTASK:
				mtvShow2_rf.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_MAGTASK:
				mtvShow2_mag2.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_ICTASK:
				mtvShow2_icc2.setText((CharSequence) msg.obj);
				break;
				
				
			case TEXTVIEW_SHOW_SAMTASK:
				mtvshow2_sam.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_RFIDTASK:
				mtvShow2_rfid2.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_PRNTASK:
				mtvShow2_prn2.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_PINTASK:
				mtvShow2_pin2.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_ICCREGISTERTASK:
				mtvShow2_icc.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_PRNREGISTERTASK:
				mtvShow2_prn.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_MAGREGISTERTASK:
				mtvShow2_mag.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_LEDTASK:
				mtvShow2_led.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSDELAYTASK:
				mtvShow2_sys_delay.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSMSDELAYTASK:
				mtvShow2_sys_msdelay.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSPOSTIMETASK:
				mtvShow2_sys_postime.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSTIMETASK:
				mtvShow2_sys_time.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SYSGETPOSINFOTASK:
				mtvShow2_sys_getposinfo.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_SYSVERTASK:
				mtvShow2_sys_version.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_THK88TASK:
				mtvShow_thk88.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_SECTASK:
				mtvShow2_sec.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_PINREGISTERTASK:
				mtvShow2_pin.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_PINREGISTERINPUT:
				mtvShow_getpin_input.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_FSTASK:
				mtvShow2_fs.setText((CharSequence) msg.obj);
				break;
				
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mtvShow.setText((CharSequence) msg.obj);
				break;
			case  TEXTVIEW_SHOW_GONET:
				mtvShow2_rf.setVisibility(View.GONE);
				mtvShow2_icc.setVisibility(View.GONE);
				mtvShow2_prn.setVisibility(View.GONE);
				mtvShow2_mag.setVisibility(View.GONE);
				mtvShow2_led.setVisibility(View.GONE);
				mtvShow2_sys_delay.setVisibility(View.GONE);
				mtvShow2_sys_msdelay.setVisibility(View.GONE);
				mtvShow2_sys_postime.setVisibility(View.GONE);
				mtvShow2_sys_time.setVisibility(View.GONE);
				mtvShow2_sys_getposinfo.setVisibility(View.GONE);
				mtvShow2_sec.setVisibility(View.GONE);
				mtvShow2_pin.setVisibility(View.GONE);
				mtvShow_getpin_input.setVisibility(View.GONE);
				mtvShow2_fs.setVisibility(View.GONE);
				mtvShow2_mag2.setVisibility(View.GONE);
				mtvShow2_icc2.setVisibility(View.GONE);
				mtvshow2_sam.setVisibility(View.GONE);
				mtvShow2_rfid2.setVisibility(View.GONE);
				mtvShow2_prn2.setVisibility(View.GONE);
				mtvShow2_pin2.setVisibility(View.GONE);
				mtvShow2_sys_version.setVisibility(View.GONE);
				break;
				
			default:
				break;
			}
			
		};
	};
	private Gui gui = new Gui(myactivity, myHandler);
	private Gui gui_reg_rfid = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_RFIDREGISTERTASK);
	private Gui gui_rfid = new Gui(myactivity, myHandler, TEXTVIEW_SHOW_RFIDTASK);
	private Gui gui_reg_icc = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_ICCREGISTERTASK);
	private Gui gui_reg_mag = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_MAGREGISTERTASK);
	private Gui gui_mag = new Gui(myactivity, myHandler, TEXTVIEW_SHOW_MAGTASK);
	private Gui gui_reg_prn = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_PRNREGISTERTASK);
	private Gui gui_prn = new Gui(myactivity, myHandler, TEXTVIEW_SHOW_PRNTASK);
	private Gui gui_sec = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SECTASK);
	private Gui gui_fs = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_FSTASK);
	private Gui gui_led= new Gui(myactivity, myHandler,TEXTVIEW_SHOW_LEDTASK);
	private Gui gui_sys_delay=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSDELAYTASK);
	private Gui gui_sys_msdelay=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSMSDELAYTASK);
	private Gui gui_sys_postime=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSPOSTIMETASK);
	private Gui gui_sys_time=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSTIMETASK);
	private Gui gui_sys_getposinfo=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SYSGETPOSINFOTASK);
	private Gui gui_reg_pin=new Gui(myactivity, myHandler,TEXTVIEW_SHOW_PINREGISTERTASK);
	private Gui gui_pin = new Gui(myactivity, myHandler, TEXTVIEW_SHOW_PINTASK);
	private Gui gui_sys_ver = new Gui(myactivity, myHandler, TEXTVIEW_SHOW_SYSVERTASK);
	private Gui gui_thk88 = new Gui(myactivity, myHandler, TEXTVIEW_SHOW_THK88TASK);

	/*界面部分结束*/
	private int ret=-1;
	private View view ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		myactivity = (IntentActivity) getActivity();
		view = inflater.inflate(R.layout.aysnctask_view, container, false);
		mtvShow = (TextView) view.findViewById(R.id.textView1);
		//gui更新界面控件
		mtvShow2_mag = (TextView) view.findViewById(R.id.tv2_reg_mag);
		mtvShow2_mag2 = (TextView) view.findViewById(R.id.tv2_mag);
		mtvShow2_rf = (TextView) view.findViewById(R.id.tv2_reg_rf);
		mtvShow2_rfid2 = (TextView) view.findViewById(R.id.tv2_rf);
		mtvShow2_icc = (TextView) view.findViewById(R.id.tv2_reg_icc);
		mtvShow2_icc2 = (TextView) view.findViewById(R.id.tv2_icc);
		mtvshow2_sam = (TextView) view.findViewById(R.id.tv2_sam);
		mtvShow2_prn = (TextView) view.findViewById(R.id.tv2_reg_prn);
		mtvShow2_prn2 = (TextView) view.findViewById(R.id.tv2_prn);
		mtvShow2_led= (TextView) view.findViewById(R.id.tv2_led);
		mtvShow2_sys_delay= (TextView) view.findViewById(R.id.tv2_sys_delay);
		mtvShow2_sys_msdelay= (TextView) view.findViewById(R.id.tv2_sys_msdelay);
		mtvShow2_sec= (TextView) view.findViewById(R.id.tv2_sec);
		mtvShow2_sys_postime= (TextView) view.findViewById(R.id.tv2_sys_postime);
		mtvShow2_sys_time= (TextView) view.findViewById(R.id.tv2_sys_time);
		mtvShow2_sys_getposinfo= (TextView) view.findViewById(R.id.tv2_sys_posinfo);
		mtvShow2_fs= (TextView) view.findViewById(R.id.tv2_fs);
		mtvShow2_pin= (TextView) view.findViewById(R.id.tv2_reg_pin);
		mtvShow2_pin2 = (TextView) view.findViewById(R.id.tv2_pin);
		mtvShow_getpin_input = (TextView) view.findViewById(R.id.tv_getpin_input);
		mtvShow2_sys_version = (TextView) view.findViewById(R.id.tv2_sys_version);
		mtvShow_thk88 = (TextView) view.findViewById(R.id.tv2_keyboard);
		
		// LCD图片切换
		imageBack = (ImageView) view.findViewById(R.id.image_back);
		init();
		return view;
	}
	//初始化控件
	public void init(){
		//键盘
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
		view.findViewById(R.id.btn_key_enter2).setOnClickListener(listener);;
	}
	
	public void systest102()
	{
		if(GlobalVariable.sdkType!=SdkType.SDK3)
		{
			gui.cls_show_msg1_record(CLASS_NAME, CLASS_NAME, g_keeptime,"%s非事件机制固件，不支持该用例", TESTITEM);
			intentSys();
		}
		initFlag=false;
		if (gui.cls_show_msg("多模块并发测试，需要手动配置请点[确定],取消[其他]")==ENTER){
			
			while(true){
			myHandler.sendEmptyMessage(0);//弹框多选测试模块
			synchronized(g_lock)
			{
				try {
					g_lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//N910机具mag和prn模块不能同时进行（会报错）优先选择打印（因为支付应用中打印是必须的）
			if(GlobalVariable.currentPlatform == Model_Type.N910){
				if(checkName.contains("MagTask")&&checkName.contains("PrnTask")){
					gui.cls_show_msg("N910打印模块与磁卡模块并发测试存在问题,不能同时进行！任意键重新选择");
					continue;
				}
			}
			//休眠模块最好与单个k21模块进行并发，否则较难定位问题
			if(sleeptest && checkName.size() > 1){
				gui.cls_show_msg("加入休眠间隔时应只选择一个k21模块进行测试！任意键重新选择");
				continue;
			}
			//已选中要执行的模块
			if(checkName.size() != 0){
				for (int i = 0; i < checkName.size(); i++) 
					strBuffer.append(checkName.toArray()[i]+"\n");
			}else{
				if(gui.cls_show_msg("未选择并发任务,按照默认配置运行,[取消]退出测试,[其他]继续运行")==ESC){
					intentSys();
					return;
				}else{
					//未选择任务则默认全选(910不选磁卡，会与打印冲突)
					 for (AYSNCTASK_LIST_K21 s : AYSNCTASK_LIST_K21.values())
					 {
						 //物理键盘仅支持81，但该模块未实现（已和开发王震确认）
						 if((s.equals(AYSNCTASK_LIST_K21.KeyBoardTask_SDK2)||s.equals(AYSNCTASK_LIST_K21.KeyBoardRegTask_SDK3))&&GlobalVariable.gModuleEnable.get(Mod_Enable.KeyBoardEnable)==false)
								continue;
						 //刷卡和打印不可一同进行，选择执行打印
						 if((s.equals(AYSNCTASK_LIST_K21.MagTask_SDK2)||s.equals(AYSNCTASK_LIST_K21.MagRegTask_SDK3))&&GlobalVariable.currentPlatform==Model_Type.N910)
								continue;
						 // 去除samTask
						 if(s.equals(AYSNCTASK_LIST_K21.SamTask))
							 continue;
						 strBuffer.append(s.toString()+"\n");
					 }
				}
			}
			break;
		}	
		}
		String str = cycletime == 0 ? "默认":cycletime+""; 
		gui.cls_show_msg1(3,"将进行并发的任务：\n" + strBuffer.toString()+"任务循环次数："+str);
		while(true)
		{
			myHandler.sendEmptyMessage(TEXTVIEW_SHOW_GONET);
			
			int returnValue=gui.cls_show_msg("多模块并发(深并发)\n0.测试前配置\n1.开始运行\n2.后置验证");
			switch (returnValue) 
			{
			case '0':
				preConfig();
				initFlag=true;
				break;
				
			case '1':
				if(!initFlag)
				{
					gui.cls_show_msg("未成功初始化，请先进行初始化！点任意键继续");
					break;
				}
				taskTest();
				break;
				
			case '2':
				if(!initFlag)
				{
					gui.cls_show_msg("未成功初始化，请先进行初始化！点任意键继续");
					break;
				}
				taskCheck();
				break;
			
			case ESC:
				intentSys();
				return;
			}
		}


	}
	//测试后需进行check
	private void taskCheck() {
		gui.cls_show_msg1(1, "%s单次测试结束后，进行校验中...",TESTITEM);
		checkFlag=true;
		cycletime=3;
		taskStatus=0;
		count=0;
		isCancel=false;
		for (int j = 0; j < checkName.size(); j++) {
				myHandler.sendEmptyMessage(AYSNCTASK_LIST_K21.valueOf(checkName.toArray()[j]+"").getValue());
			}
		Log.e("cycletime","cycletime:"+cycletime);
		while(true){
			if(taskStatus == count){
				break;
			}
		}
		gui.cls_show_msg("校验测试完成，按任意键退出");
		return;
	}
   //进行任务测试
	private void taskTest() {
        //开始运行选中的任务	
		//gui.cls_show_msg1(1, "%s测试中...",TESTITEM);
		gui.cls_show_msg1_record(CLASS_NAME, "systest101",1, "%s测试中...",TESTITEM);
		count=0;
		taskStatus=0;
		checkFlag=false;
		isCancel=false;
		for (int j = 0; j < checkName.size(); j++) {
			myHandler.sendEmptyMessage(AYSNCTASK_LIST_K21.valueOf(checkName.toArray()[j]+"").getValue());
		}
		Log.e("cycletime","cycletime:"+cycletime);
		while(true){
			if(!isCancel && (gui.cls_show_msg1(0, "%s测试中...各模块报错时,请进行[校验],重启设备后再次进行[校验],点击[取消]退出测试",TESTITEM)==ESC)){
				if (gui.ShowMessageBox("确认退出本用例？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) {
					isCancel = true;
					gui.cls_show_msg1(1, "正在停止测试...");
				}
			}
			if(taskStatus == count){
				break;
			}
		}
		//安全模块后置
		if(strBuffer.toString().contains(AYSNCTASK_LIST_K21.SecTask.toString()) || strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinTask_SDK2.toString())||strBuffer.toString().contains(AYSNCTASK_LIST_K21.PinRegTask_SDK3.toString())){
			JniNdk.JNI_Sec_KeyErase();
		}
		gui.cls_show_msg("测试完成,请进行[校验],重启设备后再次进行[校验],按任意键退出");
		return;
	}
   //配置各模块
	private void preConfig() {

		config = new Config(myactivity, handler);
		gui.cls_show_msg1(1,"各模块配置中...");
		boolean initRfid = false,initPin = false,initPrn = false;
		
		for (int i = 0; i < checkName.size(); i++) {
			switch (AYSNCTASK_LIST_K21.valueOf(checkName.toArray()[i]+"")) {//handle对应的message
			case RfidTask_SDK2:
			case RfidRegTask_SDK3:
				if(initRfid==true)
					break;
				rfidType = config.rfid_config();
				if(rfidType==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(rfidInit(rfidType)!=NDK_OK)
					gui.cls_show_msg1_record(CLASS_NAME, "systest101",g_keeptime, "line %d:初始化失败！请检查配置是否正确", Tools.getLineInfo());
				else
					gui.cls_show_msg1(2,"%s初始化成功!!!", rfidType);
				initRfid =true;
				break;
				
            case IccTask_SDK2:
            	int nkeyIn = gui.cls_show_msg("SAM配置\n0.SAM1卡\n1.SAM2卡\n");
    			switch (nkeyIn) 
    			{
    			case '0':
    				samType = _SMART_t.SAM1;
    				break;
    				
    			case '1':
    				samType = _SMART_t.SAM2;
    				break;

    			default:
    				break;
    			}
            	
    			gui.cls_show_msg1(2,"%s配置成功!", samType);
				break;
            case PrnTask_SDK2:
            case PrnRegTask_SDK3:
            	if(initPrn==true)
            		break;
            	gui.cls_show_msg("确认导入picture文件夹，任意键继续");
				break;
            case SecTask:
            	// 测试前置，擦除全部密钥操作，不要去擦除密钥，会影响到pinTask的任务
//    			JniNdk.JNI_Sec_KeyErase();
    			// 以Sec_Kcv_None方式安装TLK，ID=1
    			kcvInfo.nCheckMode = 0;
    			kcvInfo.nLen = 4;
    			if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte) 0,
    					(byte) 1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo)) != NDK_OK) {
    				gui.cls_show_msg1_record(CLASS_NAME, "SecTask", g_keeptime, "line %d:装载TLK密钥失败(%d)", Tools.getLineInfo(),ret);
    			}
    			gui.cls_show_msg1(2,"sec密钥初始化成功!");
				break;
				
            case PinTask_SDK2:
            case PinRegTask_SDK3:
            	if(initPin==true)
            		break;
            	String mainKey = "11111111111111111111111111111111"; //String tmkCmp = "82E13665B4624DF5";// 密钥明文为32个1
    			String tpk1Key = "145F5C6E3D914457145F5C6E3D914457";// 明文为16个字节的15
    			// 安装TMK, ID=5
    			kcvInfo.nCheckMode = 0;
    			if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte) 0,
    					(byte) 5, 16, ISOUtils.hex2byte(mainKey), kcvInfo)) != NDK_OK) 
    				gui_pin.cls_show_msg1_record(CLASS_NAME, "systest101",g_keeptime, "line %d:getPin初始化，安装TMK失败(%d)", Tools.getLineInfo(),ret);

    			// 安装TPK1(16bytes), ID=2,密文安装
    			if ((ret = JniNdk.JNI_Sec_LoadKey((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(),
    					(byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 5, (byte) 6, 16,ISOUtils.hex2byte(tpk1Key), kcvInfo)) != NDK_OK) 
    				gui_pin.cls_show_msg1_record(CLASS_NAME, "systest101",g_keeptime, "line %d:getPin初始化，安装TPK1失败(%d)", Tools.getLineInfo(),ret);

    			// 表示明文安装
    			if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 0,
    					(byte) 7, 16, ISOUtils.hex2byte("17171717171717171919191919191919"), kcvInfo)) != NDK_OK) 
    				gui_pin.cls_show_msg1_record(CLASS_NAME, "systest101",g_keeptime, "line %d:getPin初始化，明文安装TPK2失败(%d)", Tools.getLineInfo(),ret);
    				
    			gui.cls_show_msg1(2,"Pin初始化成功!");
				break;
				
			default:
				break;
			}
		}
		//事件注册前置
		 for (EM_SYS_EVENT s : EM_SYS_EVENT.values())  
		  JniNdk.JNI_SYSUnRegisterEvent(s.ordinal());
		 //休眠
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
		 gui.cls_show_msg1(2,"各模块配置成功！！！");
	}

	/**密码键盘任务SDK3.0*/
	@SuppressLint("DefaultLocale")
	class PinRegisterTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			LoggerUtil.i("PinTask_SDK3.0 onPreExecute");
			//注册事件
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(),MAXWAITTIME,pinlistener))!=NDK_OK)
			{
				gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",g_keeptime, "line %d:PIN事件注册失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			StringBuffer str=new StringBuffer();
			byte[] szPinOut=new byte [9];
			byte[] PinDesOut=new byte [9];
			for (int j = 0; j < szPinOut.length; j++) {
				szPinOut[j]=0;
				PinDesOut[j]=0;
			}
			int PINTIMEOUT_MAX = 20*1000;
			String szPan = "6225885916163157";
			
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue());
				pinFlag=-1;
				if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(),MAXWAITTIME,pinlistener))!=NDK_OK)
				{
					gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",g_keeptime, "line %d:第%d次:PIN事件注册失败(%d)", Tools.getLineInfo(), bak-cnt,ret);
					break;
				}
				gui_reg_pin.cls_printf(String.format("PinRegisterTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				
				if ((ret = touchscreen_getnum(str)) != NDK_OK) {
					gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",g_keeptime, "line %d:第%d次:随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				str.append("尽快输入4321并确认...");
				gui_reg_pin.cls_printf(str.toString().getBytes());
				if ((ret = JniNdk.JNI_Sec_GetPin((byte) 7, "4", szPan, null, (byte) 3, PINTIMEOUT_MAX)) != NDK_OK) {
					gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",g_keeptime, "line %d:第%d次:获取PIN Block失败(ret = %d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				JniNdk.JNI_Sys_Delay(5);
				if(pinFlag==EM_SYS_EVENT.SYS_EVENT_PIN.getValue()){
					szPinOut = getPinInput(TEXTVIEW_SHOW_PINREGISTERTASK);
					if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("35279af714dace0d"), 8) == false) {
						gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",5, "line %d:第%d次:校验失败(PinDesOut = %s)", Tools.getLineInfo(), bak-cnt, Dump.getHexDump(szPinOut));
						continue;
					}
					LoggerUtil.d("i="+(bak-cnt)+",重新注册");
					
				}else{
					gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",g_keeptime, "line %d:第%d次：没有监听到pin事件（%d）", Tools.getLineInfo(),bak-cnt,pinFlag);
					continue;
				}
				
				succ++;
				publishProgress(bak-cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			String result = String.format("PinRegisterTask任务结束,已执行%d次,成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i("PinRegisterTask_SDK3.0 onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        myHandler.sendEmptyMessage(IMAGEBACK_GONE);
	        gui_reg_pin.cls_show_msg1_record(CLASS_NAME, "PinRegisterTask",1, result);
	        LoggerUtil.i( "PinRegisterTask end");
	        taskStatus++;
	    }
	}
	
	/**密码键盘任务SDK2.0*/
	/**密码键盘任务*/
	@SuppressLint("DefaultLocale")
	class PinTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			LoggerUtil.i("PinTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int ret = -1;
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			StringBuffer str=new StringBuffer();
			byte[] szPinOut=new byte [9];
			byte[] PinDesOut=new byte [9];
			for (int j = 0; j < szPinOut.length; j++) {
				szPinOut[j]=0;
				PinDesOut[j]=0;
			}
			int PINTIMEOUT_MAX = 20*1000;
			String szPan = "6225885916163157";
			
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_pin.cls_printf(String.format("PinTask%d...已成功%d",bak-cnt,succ).getBytes());
				
				if ((ret = touchscreen_getnum(str)) != NDK_OK) {
					gui_pin.cls_show_msg1_record(CLASS_NAME, "PinTask",g_keeptime, "line %d:第%d次:随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				str.append("尽快输入4321并确认...");
				gui_pin.cls_printf(str.toString().getBytes());
				if ((ret = JniNdk.JNI_Sec_GetPin((byte) 7, "4", szPan, null, (byte) 3, PINTIMEOUT_MAX)) != NDK_OK) {
					gui_pin.cls_show_msg1_record(CLASS_NAME, "PinTask",g_keeptime, "line %d:第%d次:获取PIN Block失败(ret = %d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				}
				szPinOut = getPinInput(TEXTVIEW_SHOW_PINTASK);
				if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("35279af714dace0d"), 8) == false) {
					gui_pin.cls_show_msg1_record(CLASS_NAME, "PinTask",5, "line %d:第%d次:校验失败(PinDesOut = %s)", Tools.getLineInfo(), bak-cnt, Dump.getHexDump(szPinOut));
					continue;
				}
				
				succ++;
				publishProgress(bak-cnt);
				
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			String result = String.format("PinTask任务结束,已执行%d次,成功%d次",bak-cnt,succ);
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i("PinTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        myHandler.sendEmptyMessage(IMAGEBACK_GONE);
	        gui_pin.cls_show_msg1_record(CLASS_NAME, "PinTask",1, result);
	        LoggerUtil.i( "PinTask end");
	        taskStatus++;
	    }
	}

	/**射频卡任务SDK3.0*/
	@SuppressLint("DefaultLocale")
	class RfidRegisterEventTask extends AsyncTask<Integer, Integer, String>{
		

		@Override
		protected void onPreExecute() {
			LoggerUtil.d("RfidRegisterEventTask_SDK3.0 onPreExecute");
			//注册非接事件
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(),MAXWAITTIME,rflistener))!=NDK_OK)
			{
				gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask",g_keeptime, "line %d:非接事件注册失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			int[] UidLen = new int[1];
			byte[] UidBuf = new byte[20];
			while(cnt>0)
			{
				if(isCancel) break;
				cnt--;
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				rfFlag=-1;
				if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(),MAXWAITTIME,rflistener))!=NDK_OK)
				{
					gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask",g_keeptime, "line %d:第%d次：非接事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				
				gui_reg_rfid.cls_printf(String.format("RfidRegisterEventTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				JniNdk.JNI_Sys_Delay (3);//0.3s
				if(rfFlag==EM_SYS_EVENT.SYS_EVENT_RFID.getValue())
				{		
					//事件回调后，才射频卡其他操作，因为下电会导致取消事件,监听到事件后需重新注册，否则不在监听
					//寻卡
					if((ret = smart_detect(rfidType, UidLen, UidBuf))!=NDK_OK)
					{
						gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, "line %d:第%d次:寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					
					// 激活
					if((ret = smartActive(rfidType,felicaChoose,UidLen,UidBuf))!=NDK_OK)
					{
						gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						smartDeactive(rfidType);
						continue;
					}
					//读写
					if((ret = smartApduRw(rfidType, UidBuf,UidBuf))!=NDK_OK)
					{
						if(ret == NDK_ERR)
							gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime,"line %d:第%d次：返回码错误或数据校验失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						else
							gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, "line %d:第%d次：取随机数或读块数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						
						smartDeactive(rfidType);
						continue;
					}
//					//下电
					if((ret = smartDeactive(rfidType)) != NDK_OK)
					{
						gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask",g_keeptime, "line %d:第%d次：smart卡关闭场失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
						smartDeactive(rfidType);
						continue;
					}
					LoggerUtil.d("i="+(bak-cnt)+",下电后重新注册");
				succ++;
				publishProgress(bak-cnt);
					if (sleeptest) {
						SystemClock.sleep(SLEEPTIME);
					}
				}
			}
			smartDeactive(rfidType);
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			String result = String.format("RfidRegisterEventTask任务结束,已执行%d次,成功%d次",bak-cnt,succ);
	       
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i("RfidRegisterEventTask_SDK3.0 onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_reg_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, result);
	        LoggerUtil.i( "RfidRegisterEventTask_SDK3.0 end");
	        taskStatus++;
	        this.cancel(true);
	    }
	}
	
	
	/**SMART卡任务(SDK2.0)*/
	class SmartTask extends AsyncTask<_SMART_t, Integer, String>{
		@Override
		protected void onPreExecute() {
			Log.i("SmartTask_SDK2.0", "SmartTask onPreExecute");
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
				case FELICA:
				case MIFARE_0:
				case MIFARE_0_C:
				case MIFARE_1:
				case ISO15693:
					taskname = "RfidTask";
					gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_RFIDTASK);
					break;
				case SAM1:
				case SAM2:
					taskname = "SamTask";
					gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_SAMTASK);
					break;
				case IC:
					taskname = "IccTask";
					gui_smart = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_ICTASK);;
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
				gui_smart.cls_printf(String.format("%s(%d)...已成功%d",taskname,bak-cnt,succ).getBytes());
				
				//寻卡
				if((ret = smart_detect(smartType, UidLen, UidBuf))!=NDK_OK)
				{
					gui_smart.cls_show_msg1_record(CLASS_NAME, taskname, g_keeptime, "line %d:第%d次:%s寻卡失败(%d)",Tools.getLineInfo(),bak-cnt,taskname,ret);
					continue;
				}
				
				//激活,要使用SDK2的代码
				if((ret = smartActive_SDK2(smartType,felicaChoose,UidLen,UidBuf)) != NDK_OK )
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
	        Log.i("SmartTask_SDK2.0", "SmartTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        Log.i("SmartTask_SDK2.0", "SmartTask end");
	        taskStatus++;
	    }
	}
	
	/**icc任务*/
	@SuppressLint("DefaultLocale")
	class IccRegisterEventTask extends AsyncTask<Integer, Integer, String>{
		

		@Override
		protected void onPreExecute() {
			LoggerUtil.i( "IccRegisterEventTask onPreExecute");
			//注册非接事件
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),MAXWAITTIME,icclistener))!=NDK_OK)
			{
				gui_reg_icc.cls_show_msg1_record(CLASS_NAME, "IccRegisterEventTask",g_keeptime, "line %d:icc事件注册失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			byte[] buf = new byte[128];
			byte[] resultCode = new byte[2];
			byte[] result1 = {0x6d,0x00};
			byte[] result2 = {(byte) 0x90,0x00};
			byte[] psAtrBuf = new byte[20];
			int[] pnAtrLen = new int[1];
			while(cnt>0)
			{
				if(isCancel) break;
				cnt--;
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				iccFlag=-1;
				if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),MAXWAITTIME,icclistener))!=NDK_OK)
				{
					gui_reg_icc.cls_show_msg1_record(CLASS_NAME, "IccRegisterEventTask",g_keeptime, "line %d:第%d次：icc事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				gui_reg_icc.cls_printf(String.format("IccRegisterEventTask(%d)...已成功%d",bak-cnt,succ).getBytes());
					// 上电
					if((ret=iccPowerOn(EM_ICTYPE.ICTYPE_IC, psAtrBuf,pnAtrLen)) != NDK_OK)
					{
						gui_reg_icc.cls_show_msg1_record(CLASS_NAME, "IccRegisterEventTask", g_keeptime,"line %d:第%d次：ICC上电失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
						icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
						continue;
					}
					// 读写测试
					Arrays.fill(buf, (byte) 0);
					if((ret = iccRw(EM_ICTYPE.ICTYPE_IC,req,resultCode))!=NDK_OK|| (Arrays.equals(resultCode, result1)==false&& Arrays.equals(resultCode, result2)==false))
					{
						gui_reg_icc.cls_show_msg1_record(CLASS_NAME, "IccRegisterEventTask", g_keeptime,"line %d:第%d次：ICC读卡失败"+ISOUtils.hexString(resultCode), Tools.getLineInfo(),bak-cnt);
						icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
						continue;
					}
					if((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_IC)) != NDK_OK){
						gui_reg_icc.cls_show_msg1_record(CLASS_NAME, "IccRegisterEventTask",g_keeptime, "line %d:第%d次：icc卡关闭场失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
						icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
						continue;
					}
					LoggerUtil.d("i="+(bak-cnt)+",ic下电后重新注册");
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			String result = String.format(Locale.CHINA,"icc任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	       
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i("IccRegisterEventTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_reg_icc.cls_show_msg1_record(CLASS_NAME, "IccRegisterEventTask", g_keeptime, result);
	        LoggerUtil.i( "IccRegisterEventTask end");
	        taskStatus++;
	        this.cancel(true);
	    }
	}
	
	/**打印任务(SDK3.0)*/
	@SuppressLint("DefaultLocale")
	class PrnRegisterEventTask extends AsyncTask<Integer, Integer, String>{

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("PrnRegisterEventTask onPreExecute");
			//注册printer事件
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(),MAXWAITTIME,prnlistener))!=NDK_OK)
			{
				gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask",g_keeptime, "line %d:printer事件注册失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		
		@SuppressLint("DefaultLocale")
		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			printUtil = new PrintUtil(myactivity, myHandler,true);
			
			String picPath = GlobalVariable.sdPath+"picture/logo.png";
			while(cnt>0)
			{
				if(isCancel) break;
				cnt--;
				//解绑再注册
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
				prnFlag=-1;
				if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(),MAXWAITTIME,prnlistener))!=NDK_OK)
				{
					gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask",g_keeptime, "line %d:第%d次：printer事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				gui_reg_prn.cls_printf(String.format("PrnRegisterEventTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				//状态
				if((ret = printUtil.getPrintStatus(MAXWAITTIME2,TEXTVIEW_SHOW_PRNREGISTERTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask", g_keeptime,"line %d:第%d次：打印机状态异常(%d)！", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				if((ret = printUtil.printPng(picPath,0))!=NDK_OK)
				{
					gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask", g_keeptime,"line %d:第%d次：打印图片失败(%d)", Tools.getLineInfo(),bak-cnt, ret);
					continue;
				}
				if((ret = printUtil.getPrintStatus(MAXWAITTIME2,TEXTVIEW_SHOW_PRNREGISTERTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask",g_keeptime, "line %d:第%d次：打印机状态异常(%d)！（当前用例：png）",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				//打印脚本
				printUtil.print_Script();
				if((ret = printUtil.getPrintStatus(MAXWAITTIME2,TEXTVIEW_SHOW_PRNREGISTERTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask", g_keeptime,"line %d:第%d次：打印机状态异常(%d)！（当前用例：脚本）",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				JniNdk.JNI_Sys_Delay (3);//延时0.3s , 没有这个延时flag值不能改变
				
				if(prnFlag!=EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()){
					gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask",g_keeptime, "line %d:第%d次：没有监听到printer事件(%d)", Tools.getLineInfo(),bak-cnt,prnFlag);
					continue;
				}
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue());
			String result = String.format(Locale.CHINA,"PrnRegisterEventTask任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	       
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i( "PrnRegisterEventTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_reg_prn.cls_show_msg1_record(CLASS_NAME, "PrnRegisterEventTask", g_keeptime, result);
	        LoggerUtil.i("PrnRegisterEventTask end");
	        taskStatus++;
	        this.cancel(true);
	    }
	}
	
	/**打印任务(SDK2.0)*/
	class PrnTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("PrnTask SDK2.0", "PrnTask onPreExecute");
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
				gui_prn.cls_printf(String.format("PrnTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				
				//状态
				if((ret = printUtil.getPrintStatus(MAXWAITTIME,TEXTVIEW_SHOW_PRNREGISTERTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
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
				if((ret = printUtil.getPrintStatus(MAXWAITTIME,TEXTVIEW_SHOW_PRNREGISTERTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask",g_keeptime, "line %d:第%d次:打印机状态异常(%d)！(当前用例:png)",Tools.getLineInfo(), bak-cnt,ret);
					continue;
				}
				
				// 打印脚本
				printUtil.print_Script();
				if((ret = printUtil.getPrintStatus(MAXWAITTIME,TEXTVIEW_SHOW_PRNREGISTERTASK))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
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
	        Log.i("PrnTask SDK2.0", "PrnTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_prn.cls_show_msg1_record(CLASS_NAME, "PrnTask",1, result);
	        Log.i("PrnTask", "PrnTask end");
	        taskStatus++;
	    }
	}
	
	/**磁卡任务SDK2.0*/
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
				gui_mag.cls_printf(String.format("MagTask(%d)...已成功%d",bak-cnt,succ).getBytes());
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
	
	/**磁卡任务SDK3.0*/
	 class MagRegisterEventTask extends AsyncTask<Integer, Integer, String>{

			@Override
			protected void onPreExecute() {
				LoggerUtil.i("MagRegisterEventTask onPreExecute");
				//注册刷卡事件
				if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),MAXWAITTIME,maglistener))!=NDK_OK)
				{
					gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:刷卡事件注册失败(%d)", Tools.getLineInfo(),ret);
					return;
				}
			}
			
			@Override
			protected String doInBackground(Integer... values) {
				int succ = 0,cnt = 0,bak = 0;
				bak = cnt = cycletime;
				byte[] swiped = new byte[1];
				byte[] TK2_Buf  = new byte[100];
				int[] errorCode = new int[1];
				int waittime = 5, diff = 0;
				int ret2=-1;
				//前置
				JniNdk.JNI_Mag_Close();
				while(cnt>0)
				{
					if(isCancel) break;
					cnt--;
					gui_reg_mag.cls_printf(String.format("MagRegisterEventTask(%d)...已成功%d",bak-cnt,succ).getBytes());
					
					if((ret = JniNdk.JNI_Mag_Open())!= NDK_OK)
					{
						gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:第%d次:Mag_Open失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					//解绑再注册
					JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
					magFlag=-1;
					if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),MAXWAITTIME,maglistener))!=NDK_OK)
					{
						gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:第%d次:刷卡事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						break;
					}
					//提示5秒内刷卡的方式
					gui_reg_mag.cls_printf(String.format("MagRegisterEventTask%d...已成功%d:请在5秒内刷磁卡",bak-cnt,succ).getBytes());
					long startTime = System.currentTimeMillis();
					do
					{
						if(gui_reg_mag.wait_key(1)==ESC)
						{
							ret = NDK_ERR_QUIT;
							break;
						}
						if(JniNdk.JNI_Mag_Swiped(swiped)==NDK_OK&&swiped[0]==1)// 已刷卡
						{
							// 读卡
							if((ret2 = JniNdk.JNI_Mag_ReadNormal(null, TK2_Buf, null, errorCode))!=NDK_OK)
							{
								gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:第%d次:Mag_ReadNormal失败(%d)", Tools.getLineInfo(),bak-cnt,ret2);
								break;
							} else{
								String str_TK2 = new String(TK2_Buf);
								int index = str_TK2.indexOf("\0");
								
								if(index == -1)
									gui_reg_mag.cls_show_msg1(1, "2道无数据!",CLASS_NAME,Tools.getLineInfo());
								else
								{
									String end_TK2 = str_TK2.substring(0, index);
									gui_reg_mag.cls_show_msg1(1, "2道数据(%d):%s\n",end_TK2.length(), end_TK2);
								}
							}
							JniNdk.JNI_Mag_Reset();
							break;
						}
					}while(waittime==0||(diff = (int) Tools.getStopTime(startTime))<waittime);
					
					if(waittime!=0&&diff>=waittime){
						gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:第%d次:刷卡超时或刷卡失败(%d，%d)", Tools.getLineInfo(),bak-cnt,ret,swiped[0]);
						JniNdk.JNI_Mag_Close();
						continue;
					}
					JniNdk.JNI_Sys_Delay (2);//延时0.2s , 开发梁璐说k21是100ms才进行一轮事假触发监听，故加200ms延时 by20180607 wangxy
					if(magFlag==EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue()){
						
						if((ret = JniNdk.JNI_Mag_Close())!= NDK_OK)
						{
							gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:第%d次:Mag_Close失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
							continue;
						}
						
						
					}else{
						gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask",g_keeptime, "line %d:第%d次：没有监听到刷卡事件(%d)", Tools.getLineInfo(),bak-cnt,magFlag);
						JniNdk.JNI_Mag_Close();
						continue;
					}
					
					succ++;
					publishProgress(bak-cnt);
					if(sleeptest){
						SystemClock.sleep(SLEEPTIME);
					}
				}
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
				String result = String.format(Locale.CHINA,"刷卡任务结束，已执行%d次，成功%d次",bak-cnt,succ);
		        
		        return result;
			}
			
			@Override
		    protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
		        int progress = values[0];
		        LoggerUtil.i("MagRegisterEventTask onProgressUpdate progress---->" + progress);
			}
			
			@Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		        gui_reg_mag.cls_show_msg1_record(CLASS_NAME, "MagRegisterEventTask", g_keeptime, result);
		        LoggerUtil.i("MagRegisterEventTask_SDK3.0 end");
		        taskStatus++;
		        this.cancel(true);
		    }
		}
     /**LED灯任务 */
	 class LedTask extends AsyncTask<Integer, Integer, String>{


			@Override
			protected void onPreExecute() {
				LoggerUtil.i("LedTask onPreExecute");
				//测试前置
				if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led())) != NDK_OK)
				{
					gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask",g_keeptime, "line %d:led测试失败(%d)", Tools.getLineInfo(),ret);
					JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led());
				}
			}
			
			@Override
			protected String doInBackground(Integer... values) {
				int succ = 0,cnt = 0,bak = 0;
				bak = cnt = cycletime;
				
				while(cnt>0){
					if(isCancel) break;
					cnt--;
					gui_led.cls_printf(String.format("LedTask(%d)...led全亮--全闪--全灭，已成功%d",bak-cnt,succ).getBytes());
					//case1: led灯全亮
					if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_ON.led() | EM_LED.LED_RFID_YELLOW_ON.led() | EM_LED.LED_RFID_GREEN_ON.led() | EM_LED.LED_RFID_BLUE_ON.led())) != NDK_OK)//后面有测通讯灯和联机灯 这里可不要也为了适应ME30MH
					{
						gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask",g_keeptime, "line %d:第%d次：led测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						//后置全灭
						JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led());
						continue;
					}
					SystemClock.sleep(1000);
					//case2: led灯全闪
					if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_FLICK.led() | EM_LED.LED_RFID_YELLOW_FLICK.led() | EM_LED.LED_RFID_GREEN_FLICK.led() | EM_LED.LED_RFID_BLUE_FLICK.led() )) != NDK_OK)
					{
						gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask",g_keeptime, "line %d:第%d次：led测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led());
						continue;
					}
					SystemClock.sleep(1000);
					//case3: led灯全灭
					if((ret=JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led())) != NDK_OK)
					{
						gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask",g_keeptime, "line %d:第%d次：led测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led());
						continue;
					}
					succ++;
					publishProgress(bak-cnt);
					if(sleeptest){
						SystemClock.sleep(SLEEPTIME);
					}
				}
				JniNdk.JNI_Sys_LedStatus(EM_LED.LED_RFID_RED_OFF.led() | EM_LED.LED_RFID_YELLOW_OFF.led() | EM_LED.LED_RFID_GREEN_OFF.led() | EM_LED.LED_RFID_BLUE_OFF.led());
		       
				String result = String.format(Locale.CHINA,"LedTask任务结束,已执行%d次,成功%d次",bak-cnt,succ);
		        return result;
			}
			
			@Override
		    protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
		        int progress = values[0];
		        LoggerUtil.i("LedTask onProgressUpdate progress---->" + progress);
			}
			
			@Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		        gui_led.cls_show_msg1_record(CLASS_NAME, "LedTask", g_keeptime, result);
		        LoggerUtil.i("LedTask end");
		        taskStatus++;
		        this.cancel(true);
		    }
		} 
     /**SysDelay任务 */
	 class SysDelayTask extends AsyncTask<Integer, Integer, String>{


			@Override
			protected void onPreExecute() {
				LoggerUtil.i("SysDelayTask onPreExecute");
			}
			
			@Override
			protected String doInBackground(Integer... values) {
				int succ = 0,cnt = 0,bak = 0;
				bak = cnt = cycletime;
				int[] punTime =new int[1];
				float tapTime;
				 int TESTTIME = 6;
				while(cnt>0){
					if(isCancel) break;
					cnt--;
					gui_sys_delay.cls_printf(String.format("SysDelayTask(%d)...已成功%d",bak-cnt,succ).getBytes());
					JniNdk.JNI_Sys_StartWatch();
					JniNdk.JNI_Sys_Delay(TESTTIME*10);
					JniNdk.JNI_Sys_StopWatch(punTime);
					if((tapTime = (float) Math.abs(punTime[0]/1000.0-TESTTIME))>=0.03f)//0.03为偏差值
					{
						gui_sys_delay.cls_show_msg1_record(CLASS_NAME, "SysDelayTask",g_keeptime, "line %d:第%d次:SysDelay延时测试失败(%f)", Tools.getLineInfo(),bak-cnt,tapTime);
						continue;
					}
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
		        LoggerUtil.i("SysDelayTask onProgressUpdate progress---->" + progress);
			}
			
			@Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		        gui_sys_delay.cls_show_msg1_record(CLASS_NAME, "SysDelayTask", g_keeptime, result);
		        LoggerUtil.i("SysDelayTask end");
		        taskStatus++;
		        this.cancel(true);
		    }
		} 	 
	/** SysMsDelay任务 */
	 class SysMsDelayTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("SysMsDelay onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			int[] punTime = new int[1];
			float tapTime;
			int TESTTIME = 6;
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_msdelay.cls_printf(String.format("SysMsDelay(%d)...已成功%d",bak-cnt,succ).getBytes());
				JniNdk.JNI_Sys_StartWatch();
				JniNdk.JNI_Sys_MsDelay(TESTTIME*1000);
				JniNdk.JNI_Sys_StopWatch(punTime);
				if((tapTime = (float) Math.abs(punTime[0]/1000.0-TESTTIME))>=0.03)
				{
					gui_sys_msdelay.cls_show_msg1_record(CLASS_NAME, "SysMsDelayTask", g_keeptime, "line %d:第%d次：SysMsDelay延时测试失败(%f)",Tools.getLineInfo(),bak-cnt,tapTime);
					continue;
				}
				succ++;
				publishProgress(bak - cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}

			String result = String.format(Locale.CHINA,"SysMsDelayTask任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			LoggerUtil.i("SysMsDelayTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_msdelay.cls_show_msg1_record(CLASS_NAME, "SysMsDelay", g_keeptime, result);
			LoggerUtil.i("SysMsDelay end");
			taskStatus++;
			this.cancel(true);
		}
	} 
	 /**  SysPosTime任务 */
	 class SysPosTimeTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("SysPosTimeTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			TimeNewland stGetPosTime = new TimeNewland();
			TimeNewland stSetPosTime = new TimeNewland();
			TimeNewland stOldPosTime = new TimeNewland();
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_postime.cls_printf(String.format("SysPosTimeTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				if((ret = JniNdk.JNI_Sys_GetPosTime(stOldPosTime))!=NDK_OK)
				{
					gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, "line %d:第%d次：SysPosTimeTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
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
					gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, "line %d:第%d次：SysPosTimeTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				JniNdk.JNI_Sys_GetPosTime(stGetPosTime);
				if(stSetPosTime.obj_year!=stGetPosTime.obj_year||stSetPosTime.obj_mon!=stGetPosTime.obj_mon
					||stSetPosTime.obj_mday!=stGetPosTime.obj_mday||stSetPosTime.obj_hour!=stGetPosTime.obj_hour
					||stSetPosTime.obj_min!=stGetPosTime.obj_min)
				{
					gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, "line %d:第%d次：SysPosTimeTask测试失败",Tools.getLineInfo(),bak-cnt);
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
			LoggerUtil.i("SysPosTimeTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_postime.cls_show_msg1_record(CLASS_NAME, "SysPosTimeTask", g_keeptime, result);
			LoggerUtil.i("SysPosTimeTask end");
			taskStatus++;
			this.cancel(true);
		}
	} 	 
	 /**  SysTime任务 */
	 class SysTimeTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("SysTimeTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			long[] ultime=new long[2], ultime1=new long[2], subtime=new long[2];
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_time.cls_printf(String.format("SysTimeTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				
				if((ret = JniNdk.JNI_Sys_Time(ultime)) != NDK_OK)//返回经过的秒
				{
					gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, "line %d:第%d次：SysTimeTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				SystemClock.sleep(sleepTime);
				if((ret = JniNdk.JNI_Sys_Time(ultime1)) != NDK_OK)//返回经过的秒
				{
					gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, "line %d:第%d次：SysTimeTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				if((subtime[0]=ultime1[0]-ultime[0])!=sleepTime/1000)
				{
					gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, "line %d:第%d次：SysTimeTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,subtime[0]);
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
			LoggerUtil.i("SysTimeTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_time.cls_show_msg1_record(CLASS_NAME, "SysTimeTask", g_keeptime, result);
			LoggerUtil.i("SysTimeTask end");
			taskStatus++;
			this.cancel(true);
		}
	} 	 
	 /**  SysGetPosInfo任务 */
	 class SysGetPosInfoTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("SysGetPosInfoTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			int unLen=0;
			byte[] sBuf=new byte[128];
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_sys_getposinfo.cls_printf(String.format("SysGetPosInfoTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				Arrays.fill(sBuf, (byte)0xff);
				if((ret=JniNdk.JNI_Sys_GetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_HARDWARE_INFO.secsyshwinfo(), unLen, sBuf)) != NDK_OK)
				{
					gui_sys_getposinfo.cls_show_msg1_record(CLASS_NAME, "SysGetPosInfoTask", g_keeptime, "line %d:第%d次：SysGetPosInfoTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
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
			LoggerUtil.i("SysGetPosInfoTask onProgressUpdate progress---->" + progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_getposinfo.cls_show_msg1_record(CLASS_NAME, "SysGetPosInfoTask", g_keeptime, result);
			LoggerUtil.i("SysGetPosInfoTask end");
			taskStatus++;
			this.cancel(true);
		}
	} 		
	 
	/** SysVersionTask任务 */
	class SysVersionTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("SysVersionTask onPreExecute");
		}

		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			byte[] ver = new byte[20];
			byte[] szDate = new byte[20];
			byte[] szTime = new byte[10];
			while (cnt > 0) {
				if (isCancel)
					break;
				cnt--;
				gui_sys_ver.cls_show_msg1(1, "SysVersionTask(%d)...已成功%d",bak - cnt, succ);
				if((ret = JniNdk.JNI_Sys_Getlibver(ver))!=NDK_OK)
				{
					gui_sys_ver.cls_show_msg1_record(CLASS_NAME, "SysVersionTask", g_keeptime, "line %d:第%d次:SysVersionTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				gui_sys_ver.cls_show_msg1(2, "NDK版本:%s",ISOUtils.ASCII2String(ver));
				if((ret=JniNdk.JNI_Sys_GetK21Version(ver))!=NDK_OK)
				{
					gui_sys_ver.cls_show_msg1_record(CLASS_NAME, "SysVersionTask", g_keeptime, "line %d:第%d次:SysVersionTask测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				gui_sys_ver.cls_show_msg1(2, "K21版本:%s",ISOUtils.ASCII2String(ver));
				JniNdk.JNI_Sys_szGetBuildingDate(szDate);
				JniNdk.JNI_Sys_szGetBuildingTime(szTime);
				gui_sys_ver.cls_show_msg1(1,"NDK库编译日期:%s,NDK库编译时间:%s", new String(szDate),new String(szTime));
				succ++;
				publishProgress(bak - cnt);
				if (sleeptest) {
					SystemClock.sleep(SLEEPTIME);
				}
			}

			String result = String.format(Locale.CHINA,"SysVersionTask任务结束,已执行%d次,成功%d次", bak - cnt, succ);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			LoggerUtil.i("SysVersionTask onProgressUpdate progress---->"+ progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gui_sys_ver.cls_show_msg1_record(CLASS_NAME, "SysVersionTask", g_keeptime,result);
			LoggerUtil.i("SysVersionTask end");
			taskStatus++;
			this.cancel(true);
		}
	}
	 
	 
	 /**安全任务*/
	class SecTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			LoggerUtil.i("SecTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			byte[] szDataIn = new byte[31];
			byte[] random = new byte[1024];
			byte[] szMac = new byte[16];
			Arrays.fill(szDataIn, (byte) 0x20);
			
			kcvInfo.nCheckMode = 1;
        	kcvInfo.nLen = 4;
        	kcvInfo.sCheckBuf = ISOUtils.hex2byte("99BAB91E2EC60818");
        	
			while(cnt>0){
				if(isCancel) break;
				cnt--;
				gui_sec.cls_printf(String.format("SecTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				if(checkFlag){//用getMac校验,装载的密钥要是ＭＡＣ密钥
					if((ret = JniNdk.JNI_Sec_GetMac((byte)5, szDataIn, 7, szMac, (byte)EM_SEC_MAC.SEC_MAC_X99.ordinal()))!=NDK_OK)
					{
						gui_sec.cls_show_msg1_record(CLASS_NAME, "cross_test", g_keeptime, "line %d:第%d次：计算MAC失败(%d)，", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					if(Tools.memcmp(szMac, ISOUtils.hex2byte("2AE06A66C9D8DEBB"), 8)==false)
					{
						gui_sec.cls_show_msg1_record(CLASS_NAME, "cross_test", g_keeptime, "line %d:第%d次：校验索引5的mac值失败(%s)", Tools.getLineInfo(),bak-cnt,Dump.getHexDump(szMac));
						continue;
					}
					
				}else
				{
					if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal(), (byte) 0,
		    				(byte) 5, 16, ISOUtils.hex2byte("11111111111111111313131313131313"), kcvInfo)) != NDK_OK) 
					{
						gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime , "line %d:第%d次:装载索引ID=5的Mac密钥失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
		    			continue;
		    		}
					// 获取1K随机数测试
					if((ret = JniNdk.JNI_Sec_GetRandom(1024, random))!=NDK_OK)
					{
						gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime , "line %d:第%d次:获取随机数测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
		    			continue;
					}
				}
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			String result = String.format(Locale.CHINA,"SecTask任务结束,已执行%d次,成功%d次",bak-cnt,succ);;
	        return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i("SecTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_sec.cls_show_msg1_record(CLASS_NAME, "SecTask",g_keeptime, result);
	        LoggerUtil.i("SecTask end");
	        taskStatus++;
	        this.cancel(true);
	    }
	}
	/**文件任务 modify by zhengxq 20181221 文件系统循环流程修改*/
	class FsTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			LoggerUtil.i("FsTask onPreExecute");
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			int succ = 0,cnt = 0,bak = 0;
			bak = cnt = cycletime;
			int fd = -1;
			byte[] defineBuf = new byte[BUFLEN];
			byte[] readbuf = new byte[BUFLEN+1];
			// 测试前置：写入5K的文件
			if((ret = JniNdk.JNI_FsExist(fileName))==NDK_OK){
				if((ret = JniNdk.JNI_FsDel(fileName))!=NDK_OK){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:删除%s文件失败(%d)", Tools.getLineInfo(),fileName,ret);
					return "删除文件失败";
				}
				if((ret = JniNdk.JNI_FsExist(fileName))==NDK_OK){
					gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:%s文件仍存在(%d)", Tools.getLineInfo(),fileName,ret);
					return "文件仍存在";
				}
			}
			if((fd = JniNdk.JNI_FsOpen(fileName, "w"))<0){
				gui_fs.cls_show_msg1_record(CLASS_NAME,"FsTask",g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,fileName,fd);
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
				gui_fs.cls_printf(String.format("FsTask(%d)...已成功%d",bak-cnt,succ).getBytes());
				if((fd = JniNdk.JNI_FsOpen(fileName, "w")) < 0)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:第%d次:打开文件失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					continue;
				} 
				/**modify by zhengxq 20181221修改100字节的文件数据再读**/
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
				if((ret = JniNdk.JNI_FsClose(fd)) != NDK_OK)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", 1, "line %d:第%d次:关闭文件失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					JniNdk.JNI_FsDel(fileName);
					continue;
				} 
				if((fd = JniNdk.JNI_FsOpen(fileName, "r")) < 0)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask",g_keeptime, "line %d:第%d次:打开文件失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					JniNdk.JNI_FsDel(fileName);
					continue;
				} 
					
				Arrays.fill(readbuf, (byte) 0);
				if((ret = (JniNdk.JNI_FsRead(fd, readbuf, BUFLEN))) !=BUFLEN)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:第%d次:读文件失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					JniNdk.JNI_FsClose(fd);
					JniNdk.JNI_FsDel(fileName);
					continue;
				}
				LoggerUtil.d("read="+ISOUtils.hexString(readbuf));
				if(!Tools.memcmp(readbuf,defineBuf,BUFLEN))
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", g_keeptime, "line %d:第%d次:文件校验错误", Tools.getLineInfo(), bak-cnt);
					JniNdk.JNI_FsClose(fd);
					JniNdk.JNI_FsDel(fileName);
					continue;
				}
				if((ret = JniNdk.JNI_FsClose(fd)) != NDK_OK)
				{
					gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", 1, "line %d:第%d次:关闭文件失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
					JniNdk.JNI_FsDel(fileName);
					continue;
				} 
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			if((ret = JniNdk.JNI_FsDel(fileName)) != NDK_OK)
			{
				gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask", 1, "line %d:第%d次:文件删除失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
				JniNdk.JNI_FsDel(fileName);
			} 
			String result = String.format(Locale.CHINA,"FsTask系统任务结束,已执行%d次,成功%d次",bak-cnt,succ);
	        return result;
		}
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i("FsTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_fs.cls_show_msg1_record(CLASS_NAME, "FsTask",g_keeptime, result);
	        LoggerUtil.i("FsTask end");
	        taskStatus++;
	        this.cancel(true);
	    }
	}
	
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
	private void showDialog()
	{
		LayoutInflater layout = LayoutInflater.from(myactivity);
		View view = layout.inflate(R.layout.aysnctask_choose3, null);
		final ListView lv=(ListView) view.findViewById(R.id.aysnc_task_lv);
		list=new ArrayList<AsyncBean>();
		 for (AYSNCTASK_LIST_K21 s : AYSNCTASK_LIST_K21.values())  
		 {
			 if((s==AYSNCTASK_LIST_K21.KeyBoardTask_SDK2||s==AYSNCTASK_LIST_K21.KeyBoardRegTask_SDK3)&&GlobalVariable.gModuleEnable.get(Mod_Enable.KeyBoardEnable)==false)
				 continue;
			 
			// N700没有打印模块
			 if((s==AYSNCTASK_LIST_K21.PrnTask_SDK2||s==AYSNCTASK_LIST_K21.PrnRegTask_SDK3)&&GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnable)==false)
				continue;
			 // X5不支持打印事件机制
			 if(s==AYSNCTASK_LIST_K21.PrnRegTask_SDK3&&GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnableReg)==false)
				 continue;
				
			if((s==AYSNCTASK_LIST_K21.MagTask_SDK2||s==AYSNCTASK_LIST_K21.MagRegTask_SDK3)&&GlobalVariable.gModuleEnable.get(Mod_Enable.MagEnable)==false)
				continue;
				
			if((s==AYSNCTASK_LIST_K21.IccTask_SDK2||s==AYSNCTASK_LIST_K21.IccRegTask_SDK3)&&GlobalVariable.gModuleEnable.get(Mod_Enable.IccEnable)==false)
				continue;
			if(s==AYSNCTASK_LIST_K21.SamTask&&GlobalVariable.gModuleEnable.get(Mod_Enable.SamEnable)==false)
				continue;
			
			// N550和X5不支持密码键盘
			if((s==AYSNCTASK_LIST_K21.PinRegTask_SDK3||s==AYSNCTASK_LIST_K21.PinTask_SDK2)&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable)==false)
				continue;
			
			 AsyncBean bean=new AsyncBean(s.toString(),false);
			 list.add(bean);
			 
	    }
		final EditText et_cycletimes = (EditText) view.findViewById(R.id.et2_aysnc_cycletimes);
		final RadioGroup rg_sleepcontrol = (RadioGroup) view.findViewById(R.id.rg2_sleep_control); 
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
		
		new BaseDialog(myactivity, view, "K21多模块深并发任务配置界面", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					//是否加入休眠
					sleeptest=rg_sleepcontrol.getCheckedRadioButtonId()== R.id.rb2_normaltest?false:true;
					// 每次确定需要清除数据
					checkName.clear();
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i).isChecked())
						checkName.add(list.get(i).getItem1());
					}
	                //次数
					if(et_cycletimes.getText().toString().equals("")||(Integer.parseInt(et_cycletimes.getText().toString())==0)){
						cycletime = 5000;
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.aysnc_item, null);
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
	public class AsyncBean {
		String item1;
		boolean isChecked;

		public AsyncBean(String item1, boolean isChecked) {
			this.item1 = item1;
			this.isChecked = isChecked;
		}

		public String getItem1() {
			return item1;
		}

		public void setItem1(String item1) {
			this.item1 = item1;
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

	}
	// 密码键盘输入
	private byte[] getPinInput(int handlerMsg) {
		final int SEC_VPP_KEY_PIN = 0;// 有pin键密码按下，用*号显示
		final int SEC_VPP_KEY_BACKSPACE = 1;// 退格键按下
		final int SEC_VPP_KEY_CLEAR = 2;// 清除键按下
		final int SEC_VPP_KEY_ENTER = 3;// 确认键按下
		final int SEC_VPP_KEY_ESC = 4;// 取消键按下
		// final int SEC_VPP_KEY_NULL = 5;
		int iRet = -1, count = 0;
		StringBuffer strBuffer = new StringBuffer();
		int[] status = new int[] { 0 };
		byte[] pinBlock = new byte[20];
		// 弄个分支
		Gui gui_pin_temp=new Gui(myactivity, myHandler, handlerMsg);
		
		myHandler.sendMessage(myHandler.obtainMessage(TEXTVIEW_SHOW_PINREGISTERINPUT, strBuffer));
		while (true) {
			iRet = JniNdk.JNI_Sec_GetPinResult(pinBlock, status);
			if (iRet != 0) {
				gui_pin_temp.cls_show_msg1_record(CLASS_NAME, "PinTask", g_keeptime, "line %d:获取键盘输入状态失败(ret = %d)",
						Tools.getLineInfo(), iRet);
				break;
			}
			switch (status[0]) {
			case SEC_VPP_KEY_PIN:
				count++;
				strBuffer.append("*");
				break;

			case SEC_VPP_KEY_ENTER:
				strBuffer.append("\n密码输入完毕！！！");
				break;

			case SEC_VPP_KEY_CLEAR:// 清除
				strBuffer.delete(strBuffer.length() - count, strBuffer.length());
				count = 0;
				break;

			case SEC_VPP_KEY_BACKSPACE:
				if (count > 0) {
					strBuffer.delete(strBuffer.length() - 1, strBuffer.length());
					count = count - 1;
				}
				break;

			case SEC_VPP_KEY_ESC:
				// strBuffer.delete(strBuffer.length()-count,
				// strBuffer.length());
				// count = 0;
				strBuffer.append("\n用户取消");
				break;

			default:
				break;
			}
			myHandler.sendMessage(myHandler.obtainMessage(TEXTVIEW_SHOW_PINREGISTERINPUT, strBuffer));
			if (status[0] == SEC_VPP_KEY_ENTER || status[0] == SEC_VPP_KEY_ESC || iRet != 0) {// pinBlock[0]
																								// ==
																								// -122
				if (iRet == -1122) {
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
		myHandler.sendMessage(myHandler.obtainMessage(TEXTVIEW_SHOW_PINREGISTERINPUT, strBuffer));
		return pinBlock;
	}
}
