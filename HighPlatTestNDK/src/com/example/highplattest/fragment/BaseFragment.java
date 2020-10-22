package com.example.highplattest.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import com.example.highplattest.R;
import com.example.highplattest.activity.ActivityManager;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.activity.PatternActivity;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.Mifare_1;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.UsbModule;
import com.example.highplattest.main.netutils.LayerBase;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.util.LogUtil;
import com.newland.ndk.JniNdk;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.SoundPool;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseFragment extends Fragment implements OnClickListener,OnTouchListener,Lib,NDK,Mifare_1,OnLongClickListener
{
	private final String TAG = "BaseFragment";
	protected  static TextView mtvShow;/**用于界面显示的textView*/
	protected static TextView mTvCmd;/** 用于蓝牙底座命令通道的界面显示*/
	protected static TextView mTvData;/** 用于蓝牙底座数据通道的界面显示*/
	protected ImageView imageBack;/**LCD图片切换*/
	protected Button btnEsc;
	protected Button btnKey8;
	protected Button btnCancel;
	protected RelativeLayout layScanView;
	protected LayerBase layerBase;
	protected SurfaceView surfaceView;
	public static IntentActivity myactivity;
	public int WAITMAXTIME;
	protected LayoutParams lp;/**用于修改surfaceview的大小*/
	public int setscanlayout=-100;
	//按钮按键音
	protected SoundPool soundPool;//声明一个SoundPool
	protected int soundID;//创建某个声音对应的音频ID
	// 蓝牙
//    public static ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
    public Dialog dialog;
    public static String g_btAddr = "34:87:3d:14:95:06";
    public static String g_btName = "N900-BTDESK-12345678";
    protected String gPicPath = GlobalVariable.sdPath+"picture/";
    public static Object g_lock = new Object();
    
	public static Handler uiHandle = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) {
				
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mtvShow.setText((CharSequence) msg.obj);
				break;
				
//			case HandlerMsg.SCAN_BTN_SET_TEXT:
//				btnScanLight.setText((CharSequence) msg.obj);
//				break;
//				
			default:
				break;
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		
		myactivity = (IntentActivity) getActivity();

		View view = inflater.inflate(R.layout.unin2_layout, container, false);
			 LogUtil.d("setscanlayout==="+setscanlayout);
			 Log.d("setscanlayout", "setscanlayout=="+setscanlayout);
		
//		View view = inflater.inflate(R.layout.unin2_layout, container, false);
		surfaceView = (SurfaceView) view.findViewById(R.id.surface_main);
		lp = surfaceView.getLayoutParams();
		
		mtvShow = (TextView) view.findViewById(R.id.textView1);
		// 用于蓝牙底座命令通道和数据通道界面显示
		mTvCmd = (TextView) view.findViewById(R.id.cmd_textview);
		mTvData = (TextView) view.findViewById(R.id.data_textview);
		
		
		layScanView = (RelativeLayout) view.findViewById(R.id.lay_scan_view);
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
		view.findViewById(R.id.btn_key_point).setOnClickListener(listener);/** 此为 ‘*’号*/
		view.findViewById(R.id.btn_key_on).setOnClickListener(listener);/**此为上翻页*/
		view.findViewById(R.id.btn_key_under).setOnClickListener(listener);/**此为下翻页*/
		view.findViewById(R.id.btn_key_esc).setOnClickListener(listener);/**此为退格键**/
		view.findViewById(R.id.btn_key_back).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_enter2).setOnClickListener(listener);
//		view.findViewById(R.id.btn_inter).setOnClickListener(listener);
		btnEsc = (Button) view.findViewById(R.id.btn_key_enter2);
		btnEsc.setOnLongClickListener(this);
		
		btnKey8=(Button) view.findViewById(R.id.btn_key_8);
		btnKey8.setOnLongClickListener(this);
		
		btnCancel = (Button) view.findViewById(R.id.btn_key_esc);
		btnCancel.setOnLongClickListener(this);
		imageBack.setOnTouchListener(this);
//		//界面选择等待时常
//		if(GlobalVariable.AUTOHANDFLAG==1)
//			WAITMAXTIME=2;
//		else
//			WAITMAXTIME=30;
		
		soundPool = new SoundPool.Builder().build();
	    soundID = soundPool.load(myactivity, R.raw.btn_sound, 1);
		return view;
	}

	
	public OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			playSound();
			switch (v.getId()) 
			{
				
			case R.id.btn_key_0:
				GlobalVariable.g_nkeyIn = '0';
				break;
				
			case R.id.btn_key_1:
				GlobalVariable.g_nkeyIn ='1';
				break;
				
			case R.id.btn_key_2:
				GlobalVariable.g_nkeyIn = '2';
				break;
				
			case R.id.btn_key_3:
				GlobalVariable.g_nkeyIn = '3';
				break;
			
			case R.id.btn_key_4:
				GlobalVariable.g_nkeyIn = '4';
				break;
				
			case R.id.btn_key_5:
				GlobalVariable.g_nkeyIn = '5';
				break;
				
			case R.id.btn_key_6:
				GlobalVariable.g_nkeyIn = '6';
				break;
				
			case R.id.btn_key_7:
				GlobalVariable.g_nkeyIn = '7';
				break;
				
			case R.id.btn_key_8:
				GlobalVariable.g_nkeyIn = '8';
				break;
				
			case R.id.btn_key_9:
				GlobalVariable.g_nkeyIn = '9';
				break;
				
			case R.id.btn_key_point:
				GlobalVariable.g_nkeyIn = '.';
				break;
				
			case R.id.btn_key_esc:
				 GlobalVariable.g_nkeyIn = ESC;     
				 
				 	
				break;
				
			case R.id.btn_key_back:
				GlobalVariable.g_nkeyIn = BACKSPACE;
				break;
			
			case R.id.btn_key_on:
				GlobalVariable.g_nkeyIn = KEY_UP;
				break;
				
			case R.id.btn_key_under:
				GlobalVariable.g_nkeyIn = KEY_DOWN;
				break;
				
			case R.id.btn_key_enter2:
				GlobalVariable.g_nkeyIn = ENTER;
				break;
				
//			case R.id.btn_key_esc:
//				
//				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
	
	/**
	 *随机或固定密码键盘布局，密码键盘的位置显示在下半部分，修改为动态获取
	 * @param str
	 * @return
	 */
	public static int touchscreen_getnum(StringBuffer str) 
	{	
		
		Log.d("eric", "这里进入获取密码键盘------");
		int ret = 0;
		byte[] numKey = new byte[80];
		byte[] funKey = new byte[36];
		byte[] numSerial = new byte[11];
		int x0 = 0, x1 = 0, x2 = 0, x3 = 0, x4 = 0, y0 = 0, y1 = 0, y2 = 0, y3 = 0, y4 = 0;
		int heightTap,widthTap=0,touchHeight = 0,statusBar = GlobalVariable.StatusHeight;
		try {
			String touch = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
//			LoggerUtil.d(TAG+",touchscreen_getnum:"+touch);
			int index = touch.indexOf('x');
			// 触屏值控制
			touchHeight = Integer.parseInt(touch.substring(0, index));
			widthTap = Integer.parseInt(touch.substring(index+1))/4;
		} catch (Exception e) {
			e.getMessage();
			return NDK_ERR;
		}
		
//		String touch = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
//		LoggerUtil.d(TAG+",touchscreen_getnum:"+touch);
//		int index = touch.indexOf('x');
//		// 触屏值控制
//		touchHeight = Integer.parseInt(touch.substring(0, index));
//		widthTap = Integer.parseInt(touch.substring(index+1))/4;
		
		
		
		heightTap = (touchHeight-GlobalVariable.StatusHeight-GlobalVariable.TitleBarHeight)/8;
		
		x0 = 0;x1 = widthTap;x2 = widthTap*2;x3 = widthTap*3;x4 = widthTap*4;
		y0 = statusBar+heightTap*4;y1 = statusBar+heightTap*5;y2 = statusBar+heightTap*6;y3 = statusBar+heightTap*7;y4 = statusBar+heightTap*8;
		
		Log.d("eric", "获取密码键盘------------");
		LoggerUtil.e(String.format(Locale.CHINA,"x0 = %d,x1 = %d,x2= %d,x3 = %d,x4 = %d",x0,x1,x2,x3,x4));
		
		LoggerUtil.e(String.format(Locale.CHINA,"y0 = %d,y1 = %d,y2= %d,y3 = %d,y4 = %d",y0,y1,y2,y3,y4));

		int[] numInt = { 
				x0, y0, x1, y1, x1, y0, x2, y1, x2, y0, x3, y1, 
				x0, y1, x1, y2, x1, y1, x2, y2, x2, y1, x3, y2,
				x0, y2, x1, y3, x1, y2, x2, y3, x2, y2, x3, y3, 
				x1, y3, x2, y4 };
		
		int[] funInt = 
			{ 	0x1B, 0, x3, y0, x4, y1, 
				0x0A, 0, x3, y1, x4, y2, 
				0x0D, 0, x3, y2, x4, y4 };
		
		for (int i = 0, j = 0; i < 40; i++, j++) 
		{
			numKey[j] = (byte) (numInt[i] & 0xff);
			j++;
			numKey[j] = (byte) ((numInt[i] >> 8) & 0xff);
		}
		for (int i = 0, j = 0; i < 18; i++, j++) 
		{
			funKey[j] = (byte) (funInt[i] & 0xff);
			j++;
			funKey[j] = (byte) ((funInt[i] >> 8) & 0xff);
		}
		
		Arrays.fill(numSerial, (byte) 0x00);
		ret = JniNdk.JNI_Sec_VppTpInit(numKey, funKey, numSerial);
		if(str!=null)
		{
			String keyValue = ISOUtils.ASCII2String(numSerial);
			if(ret==NDK_OK)
			{
				str.delete(0, str.length());
				str.append(String.format("随机密码键盘的顺序为：\n%s\n%s\n%s\n %s\n", keyValue.subSequence(0, 3),
						keyValue.subSequence(3, 6),keyValue.subSequence(6, 9),keyValue.subSequence(9, 10)));
			}
		}
		return ret;
	}
	
	 private void playSound() {
	        soundPool.play(
	                soundID,
	                1f,      //左耳道音量【0~1】
	                1f,      //右耳道音量【0~1】
	                0,         //播放优先级【0表示最低优先级】
	                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
	                1          //播放速度【1是正常，范围从0~2】
	        );
	  }
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public boolean onLongClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_key_enter2:
			LoggerUtil.e("长按确认键退出测试");
			
			if (GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull ) {
				//取出自动化测试记录文件名称
//				SharedPreferences preferences=myactivity.getSharedPreferences("AutoFileName", Context.MODE_PRIVATE);
//				String autoFileName=preferences.getString("antoFileName","result.txt");
				new BaseDialog(myactivity, "退出测试", "自动化测试已结束，测试结果记录在:\n"+GlobalVariable.sdPath+"result.txt，点击是回到主界面", "是", false, new OnDialogButtonClickListener() {
					
					@Override
					public void onDialogButtonClick(View view, boolean isPositive) {
						if(isPositive){
							ActivityManager.getActivityManager().popAllActivity();
							Intent intent = new Intent(myactivity,PatternActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							myactivity.onDestroy();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
						
					}
				}).show();
			}
			if (GlobalVariable.isShowBack && GlobalVariable.gAutoFlag != ParaEnum.AutoFlag.AutoFull) {
				
				new BaseDialog(myactivity, "测试中断", "真的要退出测试吗？", "是", "否", new OnDialogButtonClickListener() {
					
					@Override
					public void onDialogButtonClick(View view, boolean isPositive) {
						if(isPositive){
							myactivity.finish();
						}
						
					}
				}).show();
			} else
				GlobalVariable.isInterrupt = true;
			break;

		default:
			break;
		}
		
		return true;
	}
	
	//根据硬件配置码来获取普通摄像头个数
	public ScanDefineInfo getCameraInfo()
	{
		/**
		 * 0x00:无扫描头和无摄像头
		 * 12:前置和后置软解码扫描头+后置摄像头
		 * 13:只有后置摄像头+后置软解码扫描头
		 * */
		ScanDefineInfo scanInfo = new ScanDefineInfo();
		String hardwareInfo = NlBuild.VERSION.NL_HARDWARE_CONFIG;
		LoggerUtil.i("002,硬件识别码="+hardwareInfo);
		int cameraId=-1;/**默认值为-1代表不支持*/
		int cameraCnt=0;
		int usbCamera=-1;
		int iRet=0;
		String cameraStr = null;
		
		scanInfo.cameraReal.put(FONT_CAMERA, -1);
		scanInfo.cameraReal.put(BACK_CAMERA, -1);
		scanInfo.cameraReal.put(EXTERNAL_CAMERA, -1);
		scanInfo.cameraReal.put(USB_CAMERA, -1);
		
		/**从硬件配置码得到的Cnt无用，要从节点得到的才有用*/
		try {
			File file_front = new File("/sys/class/front_camera/camera_name");
			if(file_front.exists()){
				iRet = iRet|2;
				cameraCnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			File file_back = new File("/sys/class/back_camera/camera0_name");
			if(file_back.exists()){
				iRet = iRet|1;
				cameraCnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			File file_pay = new File("/sys/class/pay_camera/paycamera_name");
			if(file_pay.exists()){
				iRet = iRet|4;
				cameraCnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LoggerUtil.i("getCameraInfo,iRet="+iRet);
		
		cameraStr = hardwareInfo.substring(8, 10);// 针对A5和A7平台
		// 如果是X5设备是可以从硬件配置码中得到CameraCnt值
		if (GlobalVariable.gCurPlatVer == Platform_Ver.A9) 
		{
			LoggerUtil.d("getCameraInfo,enter "+ GlobalVariable.currentPlatform);
			/** 从硬件配置码得到的Cnt无用，要从节点得到的才有用 */
			/*
			 * try { cameraCnt = Integer.parseInt(hardwareInfo.substring(24,
			 * 26)); } catch (NumberFormatException e) { e.printStackTrace();
			 * cameraCnt=-1; }
			 */
			if (GlobalVariable.currentPlatform == Model_Type.F7|| GlobalVariable.currentPlatform == Model_Type.F10) {
				try {
					usbCamera = Integer.parseInt(hardwareInfo.substring(44, 46));
					LoggerUtil.e("usbCamera=" + usbCamera);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					usbCamera = -1;
				}
			}
		}
		LoggerUtil.i("002,scanInfo="+cameraStr+",cameraCnt="+cameraCnt);
		
		if(cameraStr.equals("00")||cameraStr.equals("10"))
		{
			cameraId=-1;
		}
		if(cameraStr.equals("01")||cameraStr.equals("11")||cameraStr.equals("03")||cameraStr.equals("04"))// 前置硬解码  03前置软解码,04只有前置摄像头
		{
			cameraId = cameraCnt>0?cameraCnt-1:-1;
			scanInfo.cameraReal.put(FONT_CAMERA, cameraId);
			scanInfo.setCameraInfo("前置摄像头");
		}
		
		if(cameraStr.equals("12"))// 前后置软解码,优先后置
		{
			if((iRet&0x02)==0x02)// 前置
			{
				cameraId=1;
				scanInfo.cameraReal.put(FONT_CAMERA, cameraId);
				scanInfo.setCameraInfo("前置摄像头");
			}
			if((iRet&0x01)==0x01)// 后置
			{
				cameraId=0;
				scanInfo.cameraReal.put(BACK_CAMERA, cameraId);
				scanInfo.setCameraInfo("后置摄像头");
			}
		}
		if(cameraStr.equals("13"))// 后置软解码
		{
			if((iRet&0x01)==0x01)// 后置
			{
				cameraId=0;
				scanInfo.cameraReal.put(BACK_CAMERA, cameraId);
				scanInfo.setCameraInfo("后置摄像头");
			}
		}
		if(cameraStr.equals("20"))// 支付摄像头软解码,针对X5
		{
			cameraId = cameraCnt>0?cameraCnt-1:-1;
			scanInfo.cameraReal.put(EXTERNAL_CAMERA, cameraId);
			scanInfo.setCameraInfo("支付摄像头");
		}
		
		/**F7优先使用USB摄像头*/
		if(usbCamera!=-1)//01 3D摄像头模组A200，02双目摄像头模组2051A，03 3D摄像头模组dabai
		{
			/**需要判断是哪一种USB摄像头，预览画面旋转角度不同*/
			switch (usbCamera) {
			case 1:// 华捷摄像头
				scanInfo.setUsbModule(UsbModule.HuaJie);
				break;
				
			case 2:// 云从摄像头
				scanInfo.setUsbModule(UsbModule.YuCong);
				break;
				
			case 3:// 奥比摄像头
				scanInfo.setUsbModule(UsbModule.AoBi);
				break;

			default:
				break;
			}
			if(scanInfo.cameraReal.get(FONT_CAMERA)!=-1)
			{
				scanInfo.cameraReal.put(USB_CAMERA, 1);
				cameraId=1;
			}
			else
			{
				scanInfo.cameraReal.put(USB_CAMERA, 0);
				cameraId=0;
			}
//			cameraCnt++;
			scanInfo.setCameraInfo("USB摄像头");
		}
		scanInfo.setCameraCnt(cameraCnt);
		scanInfo.setCameraId(cameraId);
		
		LoggerUtil.v("getCameraInfo->cameraId="+cameraId);
		return scanInfo;
	}
	
	
//	/**
//	 * 是否有前置摄像头:只要一个摄像头，系统分配摄像头ID的时候会分配为0
//	 */
//	@SuppressLint("NewApi") 
//	public HashMap<String, Boolean>  cameraCount()
//	{
//		HashMap<String, Boolean> cameraCount = new HashMap<String, Boolean>();
//		if(GlobalVariable.currentPlatform==Model_Type.X5||GlobalVariable.currentPlatform==Model_Type.X3)/**X5默认使用后置摄像头*/
//		{
//			cameraCount.put("back", true);
//			cameraCount.put("font", false);
//			cameraCount.put("external", false);
//		}
//		else if(GlobalVariable.currentPlatform==Model_Type.F7)
//		{
//			cameraCount.put("back", false);
//			cameraCount.put("font", true);
//			cameraCount.put("external", false);
//		}
//		else
//		{
//			cameraCount.put("back", false);
//			cameraCount.put("font", false);
//			cameraCount.put("external", false);
//			CameraManager manager = (CameraManager) myactivity.getSystemService(Context.CAMERA_SERVICE);
//			try {
//				for (String cameraId : manager.getCameraIdList()) {
//					LoggerUtil.i("camera:" + cameraId);
//					/**如果是N550跟其他的机型是相反的，主板无法修改 modify by zhengxq 20181030*/
//					if(GlobalVariable.currentPlatform==Model_Type.N550)
//					{
//						cameraId = Integer.parseInt(cameraId)==0?"1":"0";
//						LoggerUtil.i("camera:" + cameraId);
//					}
//					switch (Integer.parseInt(cameraId)) {
//					case CameraCharacteristics.LENS_FACING_BACK:// 前置
//						cameraCount.put(FONT_CAMERA, true);
//						break;
//
//					case CameraCharacteristics.LENS_FACING_FRONT:// 后置
//						cameraCount.put(BACK_CAMERA, true);
//						break;
//						
//					default:
//						break;
//					}
//				}
//			} catch (CameraAccessException e) {
//				e.printStackTrace();
//			}
//		}
//	    return cameraCount;
//	}
	
	
	
	/**
	 * A9读取系统属性
	 * */
	public static String getProperty(String key, String defaultValue) {    
	    String value = defaultValue;
	    try {
	        Class<?> c = Class.forName("android.os.SystemProperties");
	        Method get = c.getMethod("get", String.class, String.class);
	        value = (String)(get.invoke(c, key, defaultValue));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return value;
	    }
	    return value;
	}
	/**
	 * A9设置系统属性
	 * */
	public static int setProperty(String key, String value) {
		int ret=0;
	    try {
	        Class<?> c = Class.forName("android.os.SystemProperties");
	        Method set = c.getMethod("set", String.class, String.class);
	        set.invoke(c, key, value);
	    } catch (Exception e) {
	        e.printStackTrace();
	        ret =-1;
	    }
	    return ret;
	}
	
    public static String getNodeFile(String nodeStr,String defval){
    	int iRet=0;
    	char[] readData = new char[128];
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(nodeStr));
            iRet = bufReader.read(readData);
            bufReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            iRet=-1;
        }
        if(iRet<0)
        	return "err";
        return new String(readData);
    }
	
    public static int setNodeFile(String nodeStr,String value){
    	int iRet=0;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(nodeStr));
            writer.write("none");
            writer.flush();
            writer.close();
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(nodeStr));
            writer2.write(value);
            writer2.flush();
            writer2.close();
        } catch (Exception e) {
            e.printStackTrace();
            iRet=-1;
        }
        return iRet;
    }
    
    
	
	/**
	 * DefaultFragment和UniFragment共同用到的蓝牙列表的弹框
	 */
	public String getBtAddr() {
		return g_btAddr;
	}
	public String getBtName() {
		return g_btName;
	}
	
}
