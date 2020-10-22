package com.example.highplattest.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.R;
import com.example.highplattest.main.FragmentCollector;
import com.example.highplattest.main.adapter.CheckAdapter;
import com.example.highplattest.main.bean.AnuoParaBean;
import com.example.highplattest.main.bean.CheckBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.SYSTEST_LIST_CONFIG;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.example.highplattest.nfc.Nfc5;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.newland.content.NlIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
* related document 		: 
* history 		 		: 变更记录															变更时间					变更人员
* 					 	从 Android N 开始，将不允许在 App 间，使用 file:// 的方式，传递一个 File	   		20200429				郑薛晴
* 
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
@SuppressWarnings("deprecation")
public class IntentActivity extends Activity implements NDK ,Camera.  PreviewCallback
{
	private final String TAG = "IntentActivity";
	// fragment的跳转
	private FragmentManager fm;
	private FragmentTransaction ft;
	private AnuoParaBean paraInit;
	private ServerReceiver serverReceiver;  
	private Intent serverIntent = new Intent("com.example.highplattest.activity.intentToServer"); 
	private int runId;
	private StringBuffer strBuffer = new StringBuffer();
	private SharedPreferences mSp;
	private SharedPreferences.Editor mEditor;
	
	Handler uiHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) {
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				Toast.makeText(IntentActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
				break;
				
			case 1:
				GlobalVariable.gAutoFlag = AutoFlag.AutoFull;
				GlobalVariable.gSequencePressFlag = true;
				intentFragment();
				break;

			default:
				break;
			}
		};
	};
//	private Config config = new Config(this, uiHandler);
	private Config config;
	//自定义Service广播
	String SWITCH_FRAGMENT_INTENT="com.example.highplattest.activity.serverToIntent";
	String EXCEPTION_INTENT="com.example.highplattest.activity.exceptionIntent";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		config = new Config(this, uiHandler);
		/**从 Android N 开始，将不允许在 App 间，使用 file:// 的方式，传递一个 File*/
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
		// 按键的功能暂时舍弃下
//		getWindow().addFlags(3);
//		getWindow().addFlags(5);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除actionBar这样可以显示状态栏图标 modify by 20170712
		setContentView(R.layout.inten_layout);
		
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoHand)
		{
			
			// 优先从SD卡当中去找自动化配置文件，若没有找到则使用类自带的默认值
//			String jsonString = ReadFile(GlobalVariable.sdPath+"configpara.json");// 获得json配置文件的内容
//			Gson gson = new Gson();
//			paraInit = gson.fromJson(jsonString, AnuoParaBean.class);// 自动化测试的配置参数
			intentFragment();
		}
		else if(FragmentCollector.fragments.get(0).contains("systest")&&GlobalVariable.gAutoFlag==AutoFlag.MulAuto)// 综合模块
		{
			paraInit =new AnuoParaBean();
			mSp = IntentActivity.this.getPreferences(MODE_PRIVATE);
			mEditor = mSp.edit();
			showConfig();
		}
		else
		{
			intentFragment();
		}
		LoggerUtil.e(TAG+",onCreate");
	}
	

	/**
		 * 显示checkBox的对话框,有哪些模块需要配置
	*/
	private void showConfig()
	{
		ListView lv=new ListView(this);
		final List<CheckBean> modules = new ArrayList<CheckBean>();
		for (SYSTEST_LIST_CONFIG s : SYSTEST_LIST_CONFIG.values()) 
		{
			CheckBean bean = new CheckBean(s.toString());
			modules.add(bean);
		}
		 CheckAdapter checkAdapter = new CheckAdapter(modules, this);
		lv.setAdapter(checkAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				CheckBox ck = (CheckBox) arg1.findViewById(R.id.cb_aysnc_task);
				if(ck.isChecked()==false)
				{
					ck.setChecked(true);
					modules.get(arg2).setChecked(true);
				}
				else
				{
					ck.setChecked(false);
					modules.get(arg2).setChecked(false);
				}
			}
		});
		
		new BaseDialog(this, lv, "自动化配置", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					LoggerUtil.v(TAG+",showConfig===btn sure");
					for (CheckBean ckBean:modules) {
						if(ckBean.isChecked()==true)
							strBuffer.append(ckBean.getItem1());
					}
					LoggerUtil.v(TAG+",showConfig===strBuffer.toString()");// 最终的选择对话框
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							final Context context = IntentActivity.this;
							if(strBuffer.toString().contains("WLANConfig"))
							{
								final WifiPara wifiPara = new WifiPara();
								config.confConnWlan(wifiPara);
								paraInit.setWifiDHCPenable(wifiPara.isDHCPenable());
								paraInit.setWifiLocalGateWay(wifiPara.getGateWay());
								paraInit.setWifiLocalIP(wifiPara.getLocalIp());
								paraInit.setWifiLocalMask(wifiPara.getNetMask());
								paraInit.setWifiSvrIP(wifiPara.getServerIp());
								paraInit.setWifiSvrPort(wifiPara.getServerPort());
								paraInit.setWlanPwd(wifiPara.getPasswd());
								paraInit.setWlanSec(wifiPara.getSec().toString());
								paraInit.setWlanSsid(wifiPara.getSsid());
								paraInit.setSockType(wifiPara.getSock_t().toString());
								// 保存数据到SharedPreferences
								mEditor.putBoolean("WifiDHCPenable", paraInit.isWifiDHCPenable());
								mEditor.putString("WifiLocalGateWay", paraInit.getWifiLocalGateWay());
								mEditor.putString("WifiLocalIP", paraInit.getWifiLocalIP());
								mEditor.putString("WifiLocalMask", paraInit.getWifiLocalMask());
								mEditor.putString("WifiSvrIP", paraInit.getWifiSvrIP());
								mEditor.putInt("WifiSvrPort", paraInit.getWifiSvrPort());
								mEditor.putString("WlanPwd", paraInit.getWlanPwd());
								mEditor.putString("WlanSec", paraInit.getWlanSec().toString());
								mEditor.putString("WlanSsid", paraInit.getWlanSsid());
								mEditor.putString("SockType", paraInit.getSockType().toString());
								mEditor.commit();
							}
							else// 获取上一次设置值
							{
								paraInit.setWifiDHCPenable(mSp.getBoolean("WifiDHCPenable", paraInit.isWifiDHCPenable()));
								paraInit.setWifiLocalGateWay(mSp.getString("WifiLocalGateWay", paraInit.getWifiLocalGateWay()));
								paraInit.setWifiLocalIP(mSp.getString("WifiLocalIP", paraInit.getWifiLocalIP()));
								paraInit.setWifiLocalMask(mSp.getString("WifiLocalMask", paraInit.getWifiLocalMask()));
								paraInit.setWifiSvrIP(mSp.getString("WifiSvrIP", paraInit.getWifiSvrIP()));
								paraInit.setWifiSvrPort(mSp.getInt("WifiSvrPort", paraInit.getWifiSvrPort()));
								paraInit.setWlanPwd(mSp.getString("WlanPwd", paraInit.getWlanPwd()));
								paraInit.setWlanPwd(mSp.getString("WlanSec", paraInit.getWlanSec().toString()));
								paraInit.setWlanPwd(mSp.getString("WlanSsid", paraInit.getWlanSsid()));
								paraInit.setWlanPwd(mSp.getString("SockType", paraInit.getSockType().toString()));
							}
							if(strBuffer.toString().contains("WLMConfig"))
							{
								final MobilePara mobilePara = new MobilePara();
								
								config.confConnWLM(false, mobilePara);
								paraInit.setWLMType("GPRS");
								paraInit.setWlmSvrIP(mobilePara.getServerIp());
								paraInit.setWlmSvrPORT(mobilePara.getServerPort());
								// 存储数据到SharedPreferences
								mEditor.putString("WLMType", paraInit.getWLMType().toString());
								mEditor.putString("WlmSvrIP", paraInit.getWlmSvrIP());
								mEditor.putInt("WlmSvrPORT", paraInit.getWlmSvrPORT());
								mEditor.commit();
							}
							else
							{
								paraInit.setWLMType(mSp.getString("WLMType", paraInit.getWLMType().toString()));
								paraInit.setWlmSvrIP(mSp.getString("WlmSvrIP", paraInit.getWlmSvrIP()));
								paraInit.setWlmSvrPORT(mSp.getInt("WlmSvrPORT", paraInit.getWlmSvrPORT()));
							}
							if(strBuffer.toString().contains("PrnConfig"))
							{
								int grey = config.print_config();
								paraInit.setPrinterDentisty(grey);
								// 存储数据到SharedPreferences
								mEditor.putInt("PrinterDentisty", paraInit.getPrinterDentisty());
								mEditor.commit();
							}
							else
							{
								paraInit.setPrinterDentisty(mSp.getInt("PrinterDentisty", paraInit.getPrinterDentisty()));
							}
							if(strBuffer.toString().contains("SmartConfig"))
							{
								IntentActivity.this.runOnUiThread(new Runnable() {
									
									@Override
									public void run() 
									{
										showSmartListDialog(context);
									}
								});
								synchronized (context) {
									try {
										context.wait();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							else
							{
								paraInit.setIcSamType(mSp.getString("RfType", paraInit.getIcSamType().toString()));
								paraInit.setNfcType(mSp.getString("NfcType", paraInit.getNfcType().toString()));
								paraInit.setSmartType(mSp.getString("SmartType", paraInit.getSmartType().toString()));
								paraInit.setRfType(mSp.getString("RfType", paraInit.getRfType().toString()));
							}
							if(strBuffer.toString().contains("DiskConfig"))
							{
								DiskType[] diskType = {DiskType.SDDSK,DiskType.TFDSK,DiskType.UDISK};
								final List<String> arr_type = new ArrayList<String>();
								for (DiskType s : diskType) 
								{
									arr_type.add(s.toString());
								}
								LoggerUtil.d("disk config start");
								IntentActivity.this.runOnUiThread(new Runnable() {
									
									@Override
									public void run() 
									{
										showSDCardListDialog(context);
									}
								});
								synchronized (context) {
									try {
										context.wait();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								LoggerUtil.d("disk config end");
								
							}
							else
							{
								paraInit.setDiskType(mSp.getString("DiskType", paraInit.getDiskType()));
							}
							if(strBuffer.toString().contains("BTConfig"))// 蓝牙MAC地址配置
							{
								StringBuffer strBtMac = new StringBuffer();
								config.Bt_Mul_Config(strBtMac);
								paraInit.setBTAddress(strBtMac.toString().toUpperCase());
								// 存储数据到SharedPreferences
								mEditor.putString("BTAddress", paraInit.getBTAddress());
								mEditor.commit();
							}
							else
							{
								paraInit.setBTAddress(mSp.getString("BTAddress", paraInit.getBTAddress().toUpperCase()));
							}
							/*// 蓝牙地址提前进行一次配置 modify by zhengxq 20180814
							BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
							bluetoothAdapter.enable();
							SystemClock.sleep(2000);
							BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(paraInit.getBTAddress());
							try {
								ClsUtils.createBond(btDevice.getClass(), btDevice);
							} catch (Exception e) {
								e.printStackTrace();
							}
							bluetoothAdapter.disable();*/
							// 需要等待用例配置完成，开始运行用例
							uiHandler.sendEmptyMessage(1);
							
						}
					}).start();
				}
			}
			
		}).show();
	}
	
	private void showSDCardListDialog(final Context context){
		final StringBuffer diskString=new StringBuffer();
		DiskType[] diskType = {DiskType.SDDSK,DiskType.TFDSK,DiskType.UDISK};
		ListView lv=new ListView(this);
		final List<CheckBean> modules = new ArrayList<CheckBean>();
		for (DiskType s : diskType) 
		{
			CheckBean bean = new CheckBean(s.toString());
			modules.add(bean);
		}
		 CheckAdapter checkAdapter = new CheckAdapter(modules, this);
		lv.setAdapter(checkAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				CheckBox ck = (CheckBox) arg1.findViewById(R.id.cb_aysnc_task);
				if(ck.isChecked()==false)
				{
					ck.setChecked(true);
					modules.get(arg2).setChecked(true);
				}
				else
				{
					ck.setChecked(false);
					modules.get(arg2).setChecked(false);
				}
			}
		});
		new BaseDialog(context, lv, "自动化配置", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					for (CheckBean ckBean:modules) {
						if(ckBean.isChecked()==true)
							diskString.append(ckBean.getItem1()+",");
					}
					paraInit.setDiskType(diskString.toString());
					// 存储数据到SharedPreferences
					mEditor.putString("DiskType", paraInit.getDiskType());
					mEditor.commit();
					synchronized (context) {
						context.notify();
					}
				}
			}
		}).show();
	}
	private void showSmartListDialog(final Context context){
		final StringBuffer smartType=new StringBuffer();
		final StringBuffer icSamType=new StringBuffer();
		_SMART_t[] smartTypeList = {_SMART_t.CPU_A,_SMART_t.CPU_B,_SMART_t.MIFARE_1,_SMART_t.ISO15693,_SMART_t.MIFARE_0,_SMART_t.FELICA,_SMART_t.IC,_SMART_t.SAM1};
		ListView lv=new ListView(this);
		final List<CheckBean> modules = new ArrayList<CheckBean>();
		for (_SMART_t s : smartTypeList) 
		{
			CheckBean bean = new CheckBean(s.toString());
			modules.add(bean);
		}
		 CheckAdapter checkAdapter = new CheckAdapter(modules, this);
		lv.setAdapter(checkAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				CheckBox ck = (CheckBox) arg1.findViewById(R.id.cb_aysnc_task);
				if(ck.isChecked()==false)
				{
					ck.setChecked(true);
					modules.get(arg2).setChecked(true);
				}
				else
				{
					ck.setChecked(false);
					modules.get(arg2).setChecked(false);
				}
			}
		});
		new BaseDialog(context, lv, "smart卡自动化配置", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					for (CheckBean ckBean:modules) {
						if(ckBean.isChecked()==true){
							smartType.append(ckBean.getItem1()+",");
							if(ckBean.getItem1().equals("IC") || ckBean.getItem1().equals("SAM1")){
								icSamType.append(ckBean.getItem1()+",");
							}
							if(ckBean.getItem1().equals("CPU_A") || ckBean.getItem1().equals("CPU_B")  || ckBean.getItem1().equals("MIFARE_1") 
									|| ckBean.getItem1().equals("ISO15693") || ckBean.getItem1().equals("MIFARE_0") || ckBean.getItem1().equals("FELICA")){
								paraInit.setRfType(ckBean.getItem1());
								paraInit.setNfcType(ckBean.getItem1());
							}
						}
					}
//					
					LoggerUtil.e("smart:"+smartType.toString());
					LoggerUtil.e("icSam:"+icSamType.toString());
					paraInit.setSmartType(smartType.toString());
					paraInit.setIcSamType(icSamType.toString());
					
					// 存储数据到SharedPreferences
					mEditor.putString("SmartType", paraInit.getSmartType());
					mEditor.putString("IcSamType", paraInit.getIcSamType());
					mEditor.putString("RfType", paraInit.getRfType().toString());
					mEditor.putString("NfcType", paraInit.getNfcType().toString());
					mEditor.commit();
					synchronized (context) {
						context.notify();
					}
				}
			}
		}).show();
	}
	@Override
	protected void onResume() {
		LoggerUtil.e(TAG+",onResume");
		super.onResume();
		regist();
	}
		
	
	public AnuoParaBean getParaInit() {
		return paraInit;
	}
	
	@Override
	public  void onDestroy() {
		super.onDestroy();
		if(serverReceiver!=null){
			unregisterReceiver(serverReceiver);
			serverReceiver=null;
		}
		if(SleepBroadcastReceiver!=null)
		{
			unRegist();
		}
		Intent stopIntent = new Intent(IntentActivity.this, ServiceActivity.class);  
        stopService(stopIntent); 
	}




	// 读文件，返回字符串
	public String ReadFile(String path) {
		File file = new File(path);
		BufferedReader reader = null;
		String laststr = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr = laststr + tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(IntentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					Toast.makeText(IntentActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		return laststr;
	}
	
	// 菜单切换
	// 从activity切花到fragment
	public void switchFragment(final int viewContainer, final Fragment newFragment) {
		new Thread(new Runnable() {

			@Override
			public void run() 
			{
				fm = getFragmentManager();
				ft = fm.beginTransaction();
				ft.replace(viewContainer, newFragment);
				//ft.commit();
				ft.commitAllowingStateLoss();
				sendBroadcast(serverIntent);
			}
		}).start();
		
	}
	
	public void intentFragment() 
	{
		 //动态注册广播接收器  
		serverReceiver = new ServerReceiver();  
        IntentFilter intentFilter = new IntentFilter();  
      //  intentFilter.addAction("com.example.highplattest.activity.serverToIntent"); 
        intentFilter.addAction(SWITCH_FRAGMENT_INTENT);
        intentFilter.addAction(EXCEPTION_INTENT);
        registerReceiver(serverReceiver, intentFilter);  
        
        FragmentCollector.addTestData();
		//开启服务
		Intent mIntent = new Intent(IntentActivity.this,ServiceActivity.class);  
        startService(mIntent); 
	}
	
	
	public void autoIntent(){
		try {
			Class<?> firstModule = Class.forName(FragmentCollector.mModuleNames.get(0));
			FragmentCollector.mModuleNames.remove(0);
			Intent Intent = new Intent(IntentActivity.this, firstModule);
			startActivity(Intent);
			IntentActivity.this.onDestroy();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(IntentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	// 获取到点击home键和rencentapp键的动作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		LoggerUtil.d(TAG+",onKeyDown:"+keyCode);
		GlobalVariable.virtualKey = keyCode;
		// 获取home键
		switch (keyCode) 
		{
			case KeyEvent.KEYCODE_BACK:
				GlobalVariable.isBackkey=true;
				return false;
			case  KeyEvent.KEYCODE_MENU:
				return true;
				
			case KeyEvent.KEYCODE_APP_SWITCH:
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
/*	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		GlobalVariable.TOUCH_FLAG = false;
		GlobalVariable.gScreenX = event.getX();
		GlobalVariable.gScreenY = event.getY();
		return super.onTouchEvent(event);
	}*/
	
	/** 
     * 广播接收器 接收到server传过来的id 进行fragment的切换
     * @author 
     * 
     */  
    public class ServerReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) 
        {  
        	String action = intent.getAction();
        	if(action.equals(SWITCH_FRAGMENT_INTENT))
        	{
        		int autoHandFlag=intent.getIntExtra("autoHandFlag", -1);
            	if(autoHandFlag==1){
            		autoIntent();
            	}
                //id 切换fragment  
            	runId=intent.getIntExtra("runId", 0); 
            	switchFragment(R.id.frame_content, FragmentCollector.fragmentList.get(runId));
        	}
        	if(action.equals(EXCEPTION_INTENT)){
        		String exception=intent.getStringExtra("exception");
        		Toast.makeText(IntentActivity.this, "抛出"+exception+"异常", Toast.LENGTH_SHORT).show();
//        		if(GlobalVariable.gSequencePressFlag){
//	        		SharedPreferences preferences=IntentActivity.this.getApplicationContext().getSharedPreferences("AutoFileName", Context.MODE_PRIVATE);
//					String autoFileName=preferences.getString("antoFileName","result.txt");
//	        		new FileSystem().JDK_FsWriteToFile(autoFileName, "抛出"+exception+"异常");
//        		}else
        			new FileSystem().JDK_FsWriteToFile(GlobalVariable.sdPath+"result.txt", "抛出"+exception+"异常\n");
        	}
        }  
    }  
    
	/**
	 * 注册广播
	 */
	private void regist() {
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SLEEP");  
		intentFilter.addAction("android.intent.action.WAKEUP");
//		intentFilter.addAction(Intent.ACTION_SCREEN_ON);  
//		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(SleepBroadcastReceiver, intentFilter);
//		LoggerUtil.d("002-regist broad");
	}
	
	/**
	 * 取消广播注册
	 */
	public void unRegist() 
	{
		if(SleepBroadcastReceiver != null)
		{
			unregisterReceiver(SleepBroadcastReceiver);
		}
	}
	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LoggerUtil.e(TAG+",onNewIntent");
		onNfcCallBack.nfcStart(intent);
	}
	
	private OnNfcCallBack onNfcCallBack = new Nfc5();
	
	public interface OnNfcCallBack
	{
		public void nfcStart(Intent intent);
	}
    
    /**
     * 广播：用于监听休眠与唤醒状态
     */
	private BroadcastReceiver SleepBroadcastReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent) 
		{
			LoggerUtil.d("002-action="+intent.getAction());
			String action = intent.getAction();
			if(action.equals("android.intent.action.WAKEUP"))
			{
				GlobalVariable.isWakeUp = true;
				LoggerUtil.i("IntentActivity->onReceive===Intent.ACTION_SCREEN_ON");
				
			}
			else if(action.equals("android.intent.action.SLEEP"))
			{
				GlobalVariable.isWakeUp = false;
				LoggerUtil.i("IntentActivity->onReceive===Intent.ACTION_SCREEN_OFF");
			}
		};
	};
	
	
//	@Override
//	public void onPointerCaptureChanged(boolean hasCapture) {
//		// TODO Auto-generated method stub
//		Log.d("IntentAcitivity", "进入父类：onPointerCaptureChanged");
//	}





	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		Log.d("IntentAcitivity", "进入父类：onPreviewFrame");
	}
	
}
