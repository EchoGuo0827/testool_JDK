package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.AYSNCTASK_LIST_EMV;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_DES;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_ALG;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_MAC;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.EmvUtils;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.newland.ndk.SecKcvInfo;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest109.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200810
 * directory 		: 
 * description 		: Emv相关模块并发测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  chending          20200810         created
 * 
 * 					变更说明						变更时间			变更人
 * 				根据开发建议增加海外加解密算法为DES。使用CalcDes接口 		20200821 	陈丁	
 * 				修复gui界面显示问题					20200821         	陈丁	
 * 				去除MAC_SM4_UNIONPAY运算，不再区分国内和海外版本  20201009    陈丁	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)*/
@SuppressLint("HandlerLeak")
public class SysTest109 extends DefaultFragment{
	private final String TESTITEM = "Emv相关模块并发测试";
	public final String CLASS_NAME = SysTest109.class.getSimpleName();
	public static ExecutorService exec = Executors.newFixedThreadPool(20); 
	private Config config;
	private StringBuffer strBuffer = new StringBuffer();
	private HashSet<String> checkName = new HashSet<String>();
	private int cycletime = 5000;	//自定义任务循环次数,默认5000
	private int taskStatus = 0;	//已完成任务
	private int count = 0;
	String beepnode="/sys/class/paymodule_k21/beep";
	//配置参数
	private _SMART_t type = _SMART_t.CPU_A;
	private int felicaChoose=0;
	private boolean isCancel = false;
	private boolean initFlag=false;
	private boolean sleeptest = false;
	private int SLEEPTIME = 25800; //25.8s，略早于k21进入休眠的时间26s左右
	public static final int TEXTVIEW_SHOW_K21 = 200;
	public static final int TEXTVIEW_SHOW_RFIDTASK = TEXTVIEW_SHOW_K21+1;
	public static final int TEXTVIEW_SHOW_BEEPTASK = TEXTVIEW_SHOW_K21+2;
	public static final int TEXTVIEW_SHOW_SECSM4TASK = TEXTVIEW_SHOW_K21+3;
	//隐藏界面控件
	public static final int TEXTVIEW_SHOW_GONET = TEXTVIEW_SHOW_K21-1;
	//界面显示
	public static final int TEXTVIEW_VIEW_BASE = 2;
	public static final int TEXTVIEW_RF_VIEW = TEXTVIEW_VIEW_BASE+1;
	public static final int TEXTVIEW_BEEP_VIEW = TEXTVIEW_VIEW_BASE+2;
	public static final int TEXTVIEW_SECSM4_VIEW = TEXTVIEW_VIEW_BASE+3;
	
	public static final int IMAGEBACK_GONE = 1220;
	private ArrayList<AsyncBean> list;
	private Myadapter adapter=new Myadapter();
	//安全
	private SecKcvInfo secKcvInfo = new SecKcvInfo();
	byte[] buf1_tmp = ISOUtils.hex2byte("31313131313131313131313131313131");
	byte[] buf1_in = ISOUtils.hex2byte("90929CA41B8DD3B287090DD56F3C388D");
	byte[] buf_tak= ISOUtils.hex2byte("1626E401BFF11B1B64F74D2139EF27FA");
	byte[] udesin = new byte[32];
	byte[] udesout = new byte[32];
	boolean isoverseas=false;
	
	/*重写界面提示语部分*/
	private TextView mtvShow2_rf,mtvShow2_beep,mtvShow2_secsm4;
	
	NotifyEventListener rflistener = new NotifyEventListener(){

		@Override
		public int notifyEvent(int arg0, int arg1, byte[] arg2) {
			LoggerUtil.d("监听到rf");
			rfFlag = arg0;
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
			case TEXTVIEW_RF_VIEW :
				mtvShow2_rf.setVisibility(View.VISIBLE);
				RfidRegisterEventTask rfidTask = new RfidRegisterEventTask();
				rfidTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_BEEP_VIEW:
				mtvShow2_beep.setVisibility(View.VISIBLE);
				BeepTask beepTask = new BeepTask();
				beepTask.executeOnExecutor(exec);
				count++;
				break;
			case TEXTVIEW_SECSM4_VIEW:
				mtvShow2_secsm4.setVisibility(View.VISIBLE);
				SecSm4Task secsm4Task=new SecSm4Task();
				secsm4Task.executeOnExecutor(exec);
				count++;
				break;
				/////////////////////////
			case TEXTVIEW_SHOW_RFIDTASK:
				mtvShow2_rf.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_BEEPTASK:
				mtvShow2_beep.setText((CharSequence) msg.obj);
				break;
			case TEXTVIEW_SHOW_SECSM4TASK:
				mtvShow2_secsm4.setText((CharSequence) msg.obj);
				break;
				////////////////
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mtvShow.setText((CharSequence) msg.obj);
				break;
			case  TEXTVIEW_SHOW_GONET:
				mtvShow2_rf.setVisibility(View.GONE);
				mtvShow2_beep.setVisibility(View.GONE);
				mtvShow2_secsm4.setVisibility(View.GONE);
				break;
			default:
				break;
				
				
			}
		};
	};
	
	private Gui gui = new Gui(myactivity, myHandler);
	private Gui gui_rfid = new Gui(myactivity, myHandler,TEXTVIEW_SHOW_RFIDTASK);
	private Gui gui_beep=new Gui(myactivity, myHandler, TEXTVIEW_SHOW_BEEPTASK);
	private Gui gui_secsm4=new Gui(myactivity, myHandler, TEXTVIEW_SHOW_SECSM4TASK);
	/*界面部分结束*/
	private int ret=-1;
	private View view ;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		myactivity =  (IntentActivity) getActivity();
		view = inflater.inflate(R.layout.aysnctask_view, container, false);
		mtvShow = (TextView) view.findViewById(R.id.textView1);
		mtvShow2_rf=(TextView) view.findViewById(R.id.tv2_reg_rf);
		mtvShow2_beep=(TextView) view.findViewById(R.id.tv2_beep);
		mtvShow2_secsm4=(TextView) view.findViewById(R.id.tv2_secsm4);
		init();
		return view;
		
	};
	
	//初始化控件
	private void init() 
	{
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
		view.findViewById(R.id.btn_key_enter2).setOnClickListener(listener);
	}
	
	// 测试主函数
	public void systest109() 
	{
		initFlag = false;
		if (gui.cls_show_msg("EMV多模块并发测试，需要手动配置请点[确定],取消[其他]") == ENTER) {
			while (true) {
				myHandler.sendEmptyMessage(0);// 弹框多选测试模块
				synchronized (g_lock) {
					try {
						g_lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// 已选中要执行的模块
				if (checkName.size() != 0) {
					for (int i = 0; i < checkName.size(); i++)
						strBuffer.append(checkName.toArray()[i] + "\n");
					break;
				}
			}
		}
		String str = cycletime == 0 ? "默认" : cycletime + "";
		gui.cls_show_msg1(3, "将进行并发的任务：\n" + strBuffer.toString() + "任务循环次数：" + str);

		while (true) {
			myHandler.sendEmptyMessage(TEXTVIEW_SHOW_GONET);
			int returnValue = gui.cls_show_msg("EMV多模块并发\n0.测试前配置\n1.开始运行\n");
			switch (returnValue) {
			case '0':
				preConfig();
				initFlag = true;
				break;

			case '1':
				if (!initFlag) {
					gui.cls_show_msg("未成功初始化，请先进行初始化！点任意键继续");
					break;
				}
				taskTest();
				break;
			case ESC:
				intentSys();
				return;
			}
		}
	}
	//开始运行
	private void taskTest() 
	{
		gui.cls_show_msg1_record(CLASS_NAME, "systest109", 1, "%s测试中...",TESTITEM);
		count = 0;
		taskStatus = 0;
		isCancel = false;
		for (int j = 0; j < checkName.size(); j++) {
			myHandler.sendEmptyMessage(AYSNCTASK_LIST_EMV.valueOf(checkName.toArray()[j] + "").getValue());
			Log.d("eric_chen", "systest109---" + checkName.toArray()[j] + "");
		}
		Log.e("cycletime", "cycletime:" + cycletime);
		while (true) {
			if (!isCancel
					&& (gui.cls_show_msg1(0,"%s测试中...各模块报错时，请进行【校验】，重启设备后再次进行【校验】，点击【取消】退出测试",TESTITEM) == ESC)) {
				if (gui.ShowMessageBox("确认退出本用例？".getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) == BTN_OK) {
					isCancel = true;
					gui.cls_show_msg1(1, "正在停止测试...");
				}
			}
			Log.d("eric_chen", "taskStatus==" + taskStatus + "count==" + count);
			if (taskStatus == count) {
				break;
			}
		}
		gui.cls_show_msg("测试完成，按任意键退出");
		return;
	}

	// 测试前配置
	private void preConfig() {
		config = new Config(myactivity, handler);
		gui.cls_show_msg1(1, "各模块配置中。。。");

		for (int i = 0; i < checkName.size(); i++) {
			switch (AYSNCTASK_LIST_EMV.valueOf(checkName.toArray()[i] + "")) {
			case RfidRegTask_SDK3:
				type = config.rfid_config();
				if (type == _SMART_t.FELICA) {
					felicaChoose = config.felica_config();
				}
				if (rfidInit(type) != NDK_OK)
					gui.cls_show_msg1_record(CLASS_NAME, "systest101",g_keeptime, "line %d:初始化失败！请检查配置是否正确",Tools.getLineInfo());
				else
					gui.cls_show_msg1(2, "%s初始化成功!!!", type);
				break;
				
			case BeepTask:
				gui.cls_show_msg("按任意键确认蜂鸣器是否会响");
				EmvUtils.setNodeString(beepnode, "200");
				break;
			case SecSm4Task:
				// int nkeyin3=gui.cls_show_msg("海内外\n0.国内\n1.海外\n");
				// switch (nkeyin3) {
				// case '0':
				// isoverseas=false;
				// gui.cls_show_msg1(1, "当前选择国内--安装SM4");
				// break;
				//
				// case '1':
				// isoverseas=true;
				// gui.cls_show_msg1(1, "当前选择海外--安装DES");
				// break;
				// default:
				// break;
				// }
				if ((ret = JniNdk.JNI_Sec_KeyErase()) != NDK_OK) {
					gui.cls_show_msg1_record(CLASS_NAME, "systest109",g_keeptime, "line %d:擦除密钥失败,ret=%d",Tools.getLineInfo(), ret);
					return;
				}
				// if (!isoverseas) {
				gui.cls_show_msg1(1, "安装SM4密钥中。。。");
				secKcvInfo.nCheckMode = 0;
				secKcvInfo.nLen = 4;
				if ((ret = JniNdk.JNI_Sec_LoadKey((byte) (0 | EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()),(byte) (EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal() | EM_SEC_KEY_ALG.SEC_KEY_SM4
										.seckeyalg()), (byte) 0, (byte) 1, 16,buf1_tmp, secKcvInfo)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(CLASS_NAME, "systest109",g_keeptime, "line %d:安装TMK密钥失败,ret=%d",Tools.getLineInfo(), ret);
					return;
				}
				// TDK
				if ((ret = JniNdk.JNI_Sec_LoadKey((byte) (EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal() | EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()),
								(byte) (EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal() | EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte) 1, (byte) 5, 16,buf1_in, secKcvInfo)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(CLASS_NAME, "systest109",g_keeptime, "line %d:安装TDK密钥失败,ret=%d",Tools.getLineInfo(), ret);
					return;
				}
				gui.cls_show_msg1(1, "安装SM4密钥完毕。。。");
				// }else {
				gui.cls_show_msg1(1, "安装DES密钥中。。。");
				byte[] buf1_tmp = ISOUtils.hex2byte("3672c2bc7f17f29c65873586bc7f17f23672c2bc7f17f29c");
				byte[] buf1_in = ISOUtils.hex2byte("313131313131313131313131313131313131313131313131");
				byte[] buf3_in = ISOUtils.hex2byte("1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B");

				secKcvInfo.nCheckMode = 0;
				secKcvInfo.nLen = 4;
				if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0,(byte) (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte) 0, (byte) 1, 24, buf1_tmp,secKcvInfo)) != NDK_OK) {
					gui.cls_show_msg1_record(CLASS_NAME, "systest109",g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM, ret);
					return;
				}
				// 双倍长TDK
				if ((ret = JniNdk.JNI_Sec_LoadKey((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(),(byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(),(byte) 1, (byte) 5, 16, buf1_in, secKcvInfo)) != NDK_OK) {
					gui.cls_show_msg1_record(CLASS_NAME, "systest109",g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM, ret);
					return;
				}
				// 三倍长TDK
				if ((ret = JniNdk.JNI_Sec_LoadKey((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(),(byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(),
						(byte) 1, (byte) 6, 24, buf3_in, secKcvInfo)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(CLASS_NAME, "systest109",g_keeptime, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM, ret);
					return;
				}
				gui.cls_show_msg1(1, "安装DES密钥完毕。。。");
				// }

				break;

			default:
				break;

			}

		}
		// 事件注册前置
		for (EM_SYS_EVENT s : EM_SYS_EVENT.values()) {
			JniNdk.JNI_SYSUnRegisterEvent(s.ordinal());
		}
		gui.cls_show_msg1(2, "各模块配置成功！！！");
	}

	/**SM4算法加解密任务*/
	class SecSm4Task extends AsyncTask<Integer, Integer, String>{
		@Override
		protected void onPreExecute() {
			Log.d("eric_chen", "---SecSm4Task---");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {
			// 加解密
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_secsm4.cls_printf(String.format("SecSm4Task%d...已成功%d",bak-cnt,succ).getBytes());
				
//				if (!isoverseas) {
					if(JniNdk.JNI_Sec_CalcDes((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)5, udesin, 16, udesout, (byte)EM_SEC_DES.SEC_SM4_ENCRYPT.secdes())!=NDK_OK)
					{
						gui_secsm4.cls_show_msg1_record(CLASS_NAME, "systest109", g_keeptime, "line %d:加解密失败", Tools.getLineInfo());
						continue;
					}
					// 16字节MAC_SM4运算耗时
					if(JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), 
							(byte)1, (byte)2, 16, buf_tak, secKcvInfo)!=NDK_OK)
					{
						gui_secsm4.cls_show_msg1_record(CLASS_NAME, "systest109", g_keeptime, "line %d:密钥安装失败", Tools.getLineInfo());
						continue;
					}
					byte[] szDataIn = new byte[1024];
					byte[] szMac = new byte[16];
					Arrays.fill(szDataIn, (byte)0x20);
					if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, 1024, szMac, (byte)EM_SEC_MAC.SEC_MAC_SM4.ordinal())!=NDK_OK)
					{
						gui_secsm4.cls_show_msg1_record(CLASS_NAME, "systest109", g_keeptime, "line %d:16字节MAC_SM4失败", Tools.getLineInfo());
						continue;
					}
					//N910欧洲不支持该MAC计算方式
//					if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, 16, szMac, (byte)6)!=NDK_OK)
//					{
//						gui_secsm4.cls_show_msg1_record(CLASS_NAME, "systest109", g_keeptime, "line %d:16字节MAC_SM4_UNIONPAY失败", Tools.getLineInfo());
//						continue;
//					}
//				}
//				else {
					if((ret=JniNdk.JNI_Sec_CalcDes((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)5, udesin, (byte)8, udesout,(byte) (0|(2<<1))))!=NDK_OK)
					{
						gui_secsm4.cls_show_msg1_record(CLASS_NAME, "systest109", g_keeptime, "line %d:%s加密测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
						continue;
					}
					
					if((ret=JniNdk.JNI_Sec_CalcDes((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)6, udesin, (byte)8, udesout,(byte) (0|(3<<1))))!=NDK_OK)
					{
						gui_secsm4.cls_show_msg1_record(CLASS_NAME, "systest109", g_keeptime, "line %d:%s加密测试失败(%d)", Tools.getLineInfo(),TESTITEM);
						continue;
					}
//				}
				succ++;
				publishProgress(bak - cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}

			String result = String.format(Locale.CHINA,"SecSm4Task任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i( "SecSm4Task onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_secsm4.cls_show_msg1_record(CLASS_NAME, "SecSm4Task", g_keeptime, result);
	        LoggerUtil.i("PrnRegisterEventTask end");
	        taskStatus++;
	        this.cancel(true);
	    }
		
	}
	
	/**蜂鸣器任务*/
	class BeepTask extends AsyncTask<Integer, Integer, String>{

		@Override
		protected void onPreExecute() {
			Log.d("eric_chen", "---BeepTask---");
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(Integer... params) {
			int succ = 0, cnt = 0, bak = 0;
			bak = cnt = cycletime;
			while (cnt > 0) {
				if(isCancel) break;
				cnt--;
				gui_beep.cls_printf(String.format("BeepTask%d...已成功%d",bak-cnt,succ).getBytes());
				EmvUtils.setNodeString(beepnode, "200");
				succ++;
				publishProgress(bak - cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			}
			
			String result = String.format(Locale.CHINA,"BeepTask任务结束，已执行%d次，成功%d次", bak - cnt, succ);
			return result;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i( "BeepTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_beep.cls_show_msg1_record(CLASS_NAME, "BeepTask", g_keeptime, result);
	        LoggerUtil.i("BeepTask end");
	        taskStatus++;
	        this.cancel(true);
	    }
		
	}
	/**射频卡任务*/
	@SuppressLint("DefaultLocale")
	class RfidRegisterEventTask extends AsyncTask<Integer, Integer, String>{

		@Override
		protected void onPreExecute() {
			Log.d("eric_chen", "---RfidRegisterEventTask---");
			//注册非接事件
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(),MAXWAITTIME,rflistener))!=NDK_OK)
			{
				gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask",g_keeptime, "line %d:非接事件注册失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(Integer... arg0) {
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
					gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask",g_keeptime, "line %d:第%d次：非接事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				gui_rfid.cls_printf(String.format("RfidRegisterEventTask%d...已成功%d",bak-cnt,succ).getBytes());
				JniNdk.JNI_Sys_Delay (3);//0.3s
				if(rfFlag==EM_SYS_EVENT.SYS_EVENT_RFID.getValue())
				{	
					//事件回调后，才射频卡其他操作，因为下电会导致取消事件,监听到事件后需重新注册，否则不在监听
					//寻卡
					if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
					{
						gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, "line %d:第%d次:寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					
					// 激活
					if((ret = smartActive(type,felicaChoose,UidLen,UidBuf))!=NDK_OK)
					{
						gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						smartDeactive(type);
						continue;
					}
					//读写
					if((ret = smartApduRw(type, UidBuf,UidBuf))!=NDK_OK)
					{
						if(ret == NDK_ERR)
							gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime,"line %d:第%d次：返回码错误或数据校验失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						else
							gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask", g_keeptime, "line %d:第%d次：取随机数或读块数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						
						smartDeactive(type);
						continue;
					}
//					//下电
					if((ret = smartDeactive(type)) != NDK_OK){
						gui_rfid.cls_show_msg1_record(CLASS_NAME, "RfidRegisterEventTask",g_keeptime, "line %d:第%d次：smart卡关闭场失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
						smartDeactive(type);
						continue;
					}
					LoggerUtil.d("i="+(bak-cnt)+",下电后重新注册");
				succ++;
				publishProgress(bak-cnt);
				if(sleeptest){
					SystemClock.sleep(SLEEPTIME);
				}
			    }
			}
			smartDeactive(type);
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			String result = String.format("射频任务结束，已执行%d次，成功%d次",bak-cnt,succ);
	       
	        return result;
		
		}
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        LoggerUtil.i( "射频任务 onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        gui_rfid.cls_show_msg1_record(CLASS_NAME, "射频任务", g_keeptime, result);
	        LoggerUtil.i("射频任务 end");
	        taskStatus++;
	        this.cancel(true);
	    }
		
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
	
	/**
	 * 显示checkBox的对话框
	 */
	private void showDialog()
	{
		LayoutInflater layout = LayoutInflater.from(myactivity);
		View view = layout.inflate(R.layout.aysnctask_choose3, null);
		final ListView lv=(ListView) view.findViewById(R.id.aysnc_task_lv);
		list=new ArrayList<AsyncBean>();
		 for (AYSNCTASK_LIST_EMV s : AYSNCTASK_LIST_EMV.values())  
		 {

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
	
	
}
