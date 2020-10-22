package com.example.highplattest.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import com.example.highplattest.R;
import com.example.highplattest.activity.ActivityManager;
import com.example.highplattest.activity.PatternActivity;
import com.example.highplattest.main.DefineListener;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.ndk.JniNdk;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.newland.NLUART3Manager;
import android.newland.NlModemManager;
import android.newland.net.ethernet.NlEthernetManager;
import android.newland.scan.ScanUtil;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public abstract class UnitFragment extends BaseFragment implements DefineListener.TestConfigListener
{
	private String TAG = UnitFragment.class.getSimpleName();
	public String mCodeResult;
	public final String QR_UTF8="QR Code(UTF-8):中国1A2B3C4D\n孙二婶\n福建新大陆支付技术有限公司。。。。。\n83979400\nsunly@newlandpayment.com";
	public final String QR_GBK = "QR Code(GBK):中国1A2B3C4D\r\n孙二婶\r\n福建新大陆支付技术有限公司。。。。。\r\n83979400\r\nsunly@newlandpayment.com";
	// 超时时间设置为1分钟
	public final int MAXWAITTIME = 60*1000;
	
	
//	public String currentPackName;
	public int respCode;
//	public String expPackName;
	public int gKeepTimeErr = 5;// 出错提示时间
	public int gScreenTime = 2; // 界面显示时间
	
	public DefineListener.TestConfigListener mTestConfigListener;
	
	// 默认随机测试次数
	protected int g_RandomTime =50;//50;//外层
	protected int g_RandomCycle =20;//内层
	public int getRandomTime() {
		return g_RandomTime;
	}
	public int getRandomCycle() {
		return g_RandomCycle;
	}
	//wifi探针输入
	public String mac;
	private boolean isRead;
	//RS232串口读写线程
	private ReadThread readThread;
	
	
	private WriteThread writeThread;
	int ret=RS232_TIMEOUT;
	public Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) {
			case 1122:
				 EthernetUtil.mEthernetManager = new NlEthernetManager(myactivity);
				break;
				
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mtvShow.setText((CharSequence) msg.obj==null?"null":(CharSequence) msg.obj);
				break;
				
			case HandlerMsg.SURFACEVIEW_VIEW:
				layScanView.setVisibility(View.VISIBLE);
				break;
				
			case HandlerMsg.SURFACEVIEW_GONE:
				layScanView.setVisibility(View.GONE);
				break;
				
			case HandlerMsg.DIALOG_SHOW_NET_TRANS:
				new ShowDialog().setConfigNetTrans(myactivity, (WifiPara) msg.obj);
				break;
				
			case HandlerMsg.SCAN_SURFACE_FLUSH:
				setSurface(lp);
				break;
				
			case HandlerMsg.TEXTVIEW_COLOR_RED:
				mtvShow.setTextColor(Color.RED);
				break;
				
			case HandlerMsg.TEXTVIEW_COLOR_BLACK:
				mtvShow.setTextColor(Color.BLACK);
				break;
			default:
				break;
			}
		};
	};
	// 读数据线程
	private class ReadThread extends Thread {
		NLUART3Manager uart3Manager;
		byte[] buf;
		int lengthMax;
		int timeoutSec;

		public ReadThread(NLUART3Manager uart3Manager, byte[] buf,int lengthMax, int timeoutSec) {
			this.uart3Manager = uart3Manager;
			this.buf = buf;
			this.lengthMax = lengthMax;
			this.timeoutSec = timeoutSec;
		}

		@Override
		public void run() {
			LoggerUtil.d("start read");
			ret = uart3Manager.read(buf, lengthMax, timeoutSec);
			LoggerUtil.d("end read:"+ret);
			readThread.interrupt();
		}
	}
	


	// 写数据线程
	private class WriteThread extends Thread {
		NLUART3Manager uart3Manager;
		byte[] buf;
		int lengthMax;
		int timeoutSec;

		public WriteThread(NLUART3Manager uart3Manager, byte[] buf,
				int lengthMax, int timeoutSec) {
			this.uart3Manager = uart3Manager;
			this.buf = buf;
			this.lengthMax = lengthMax;
			this.timeoutSec = timeoutSec;
		}

		@Override
		public void run() {
			ret = uart3Manager.write(buf, lengthMax, timeoutSec);
			writeThread.interrupt();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		// 测试前置动作
		mTestConfigListener = this;
		mTestConfigListener.onTestUp();
		//界面选择等待时常
		if(GlobalVariable.gAutoFlag==ParaEnum.AutoFlag.AutoFull)
			WAITMAXTIME=2;
		else
			WAITMAXTIME=30;
		return view;
		
	}
	
	public void unitEnd()
	{
		myactivity.finish();
	}
	

	/**
	 * 扫码操作公共方法
	 * @param scanUtil
	 * @param message1
	 * @param message2
	 * @param gui
	 * @return
	 * @throws InterruptedException 
	 */
	public int scanDialog(String message2,Handler handler) 
	{	
		Log.d("cheneric", "1280*960==2");
		Gui gui = new Gui(myactivity, handler);
		StringBuffer result = new StringBuffer();
		result.delete(0, result.length());
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			result.append(scan_oversea.doScan());
		else*/
			result.append(scan_Domestic.doScan());
		mCodeResult="";
		if(result.toString().startsWith("F"))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_FAULT;
		}
		else if(result.toString().equals("null"))
		{
			return NDK_SCAN_COTINUE_NULL;
		}
		else 
		{
			// 获取扫码的结果
			mCodeResult = result.toString().startsWith("S")==true?  result.toString().substring(1):result.toString();
//			//如果识别结果含有“%”，替换为转义字符"%%"
//			mCodeResult = mCodeResult.replace("%", "%%");
			if(gui.cls_show_msg(message2+"码值:"+mCodeResult+",与实际码值是否一致,[确认]是,[取消]否")==ENTER)
				return NDK_SCAN_OK;
			else
				return NDK_SCAN_DATA_ERR;
		}
	}
	
	/**
	 * 扫码操作公共方法
	 * @param scanUtil
	 * @param message1
	 * @param message2
	 * @param gui
	 * @return
	 * @throws InterruptedException 
	 */
	//使用doScan()方法解码
	public int scanUtilDialog(ScanUtil scanUtil,String message2,Handler handler) 
	{
		Gui gui = new Gui(myactivity, handler);
		StringBuffer result = new StringBuffer();
		result.delete(0, result.length());
		
		String resulttemString=(String) scanUtil.doScan();
		result.append(resulttemString);
		Log.d("eric_chen", "result==="+result);
		mCodeResult="";
		if(result.toString().startsWith("F"))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_FAULT;
		}
		else if(result.toString().equals("null"))
		{
			return NDK_SCAN_COTINUE_NULL;
		}
		else 
		{
			// 获取扫码的结果
			mCodeResult = result.toString().startsWith("S")==true?  result.toString().substring(1):result.toString();
			if(gui.cls_show_msg(message2+"码值:"+mCodeResult+",与实际码值是否一致,[确认]是,[取消]否")==ENTER)
				return NDK_SCAN_OK;
			else
				return NDK_SCAN_DATA_ERR;
		}
	}
	
	//使用doScanWithRawByte()方法,比较原生数据
	public int scanRaw(String message,Gui gui,byte[] code,String encode)
	{
		gui.cls_show_msg("移开之前的条码,"+message);
		byte[] mCodeget=null;
		try{
			mCodeget=(byte[]) scan_Domestic.doScanWithRawByte();//单次扫码的原生数据
			if(mCodeget!=null)// 扫出码值才转换
			{
				mCodeResult = new String(mCodeget,encode);
				LoggerUtil.d("扫码结果="+mCodeResult);
			}
		}catch(Exception e){
			Log.d("eric", "err:"+e);
		}
		
		
		if(mCodeget==null)//超时返回null
		{
			mCodeResult="null";
			LoggerUtil.d("mCodeget==null");
			return NDK_SCAN_COTINUE_NULL;
		}else{    
			if(Tools.memcmp(code, mCodeget, code.length)){
				return NDK_SCAN_OK;
			}else{
				return NDK_SCAN_FAULT;		
			}
		}
	}
	
	public int scanTip(String message,Gui gui,String code)
	{
		String result;
		gui.cls_show_msg("移开之前的条码,"+message);
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			result = (String) scan_oversea.doScan();
		else*/
			result = (String) scan_Domestic.doScan();
		if(result==null)
		{
			return NDK_SCAN_NO_RESULT;
		}
		else if(result.startsWith("F"))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_FAULT;
		}
			
		else if(!result.substring(1).equals(code))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_DATA_ERR;
		}
		return NDK_SCAN_OK;
	}
	
	/**
	 * 传入ScanUtil带码值校验功能
	 * @param scanUtil
	 * @param message
	 * @param gui
	 * @param code
	 * @return
	 */
	public int scanUtilCheck(ScanUtil scanUtil,String message,Gui gui,String code)
	{
		String result;
		gui.cls_show_msg("移开之前的条码，"+message);
		result = (String) scanUtil.doScan();
		if(result==null)
		{
			return NDK_SCAN_NO_RESULT;
		}
		else if(result.startsWith("F"))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_FAULT;
		}
			
		else if(!result.substring(1).equals(code))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_DATA_ERR;
		}
		return NDK_SCAN_OK;
	}
	
	
	public void stopScan()
	{
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			scan_oversea.stopScan();
		else*/
			scan_Domestic.stopScan();
	}
	
	public void setNlsUpcEnable(int key)
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
			scan_Domestic.setNlsUPCEANSwitch(key);
	}
	
	public void scanOpenLight()
	{
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			scan_oversea.openLight();
		else*/
			scan_Domestic.openLight();
	}
	
	public void scanCloseLight()
	{
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			scan_oversea.closeLight();
		else*/
			scan_Domestic.closeLight();
	}
	
	public void scanDecodeScreenResolution(Rect rect)
	{
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			scan_oversea.setDecodeScreenResolution(rect);
		else*/
			scan_Domestic.setDecodeScreenResolution(rect);
	}
	
	private void setSurface(LayoutParams lp)
	{
//		LayoutParams lp = surfaceView.getLayoutParams();
//		lp.height = 640;
//		lp.width = 480;
		Log.d("chen------", "setLayoutParams"+lp.width+"---"+lp.height);
		surfaceView.setLayoutParams(lp);
	}
	
	protected ScanUtil scan_Domestic;/**国内扫码接口*/
//	protected com.android.newland.scan.ScanUtil scan_oversea;/**国外扫码接口*//**现在国内国外统一了*/
	/**
	 * 初始化软硬解扫码
	 * @param scan_Mode 扫码模式
	 * @param cameraId 扫码摄像头
	 * @param timeout 扫码超时时间
	 * @return
	 */
	public int initScanMode(Scan_Mode scan_Mode,Context myactivity,SurfaceView surfaceView,int cameraId,boolean soundEnable,int timeout)
	{
		int ret=ScanUtil.SUCCESS;
		switch (scan_Mode) 
		{
		case MODE_ONCE:
		case MODE_CONTINUALLY:
		case MODE_MANUALLY:
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
//			{
//				scan_oversea = new com.android.newland.scan.ScanUtil(myactivity);
//				ret = scan_oversea.init(scan_Mode.ordinal(), timeout, ScanUtil.FOCUS_ON, true);
//			}
//			else
//			{
				scan_Domestic = new ScanUtil(myactivity);
				ret = scan_Domestic.init(scan_Mode.ordinal(), timeout, ScanUtil.FOCUS_READING, true);
//			}
			break;
			
		case ZXING:
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
//			{
//				scan_oversea = new com.android.newland.scan.ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout);
//			}
//			else
//			{
				scan_Domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout);
//			}
			break;
			
		case ZXING_MANUALLY:
			if(surfaceView!=null)
			{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			}
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
//				scan_oversea = new com.android.newland.scan.ScanUtil(myactivity, surfaceView, cameraId, soundEnable);
//			else
				scan_Domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable);
			break;
			
		case NLS_0:
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
//			{
//				scan_oversea = new com.android.newland.scan.ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout,0);
//			}
//			else
//			{
				scan_Domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout,0);
//			}
			break;
			
		case NLS_1:
			if(surfaceView!=null)
			{
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			}
//			handler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
//			{
//				scan_oversea = new com.android.newland.scan.ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout,1);
//			}
//			else
//			{   
				Log.d("eric", "扫码初始化");
				scan_Domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout,1);
//			}
			
			break;

		default:
			break;
		}
		return ret;
	}
	
	/**
	 * 释放扫码
	 */
	public void releaseScan()
	{
		if(scan_Domestic!=null)
		{
			if(GlobalVariable.currentPlatform==Model_Type.N900_3G){
				scan_Domestic.relese();
			}
			else{
				scan_Domestic.release();
			}
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			
		/*if(scan_oversea!=null){
			scan_oversea.release();
		}*/
	}
	
	/**
	 * 释放扫码
	 */
	public void releaseScan(ScanUtil scanUtil)
	{
		if(scanUtil!=null)
		{
			if(GlobalVariable.currentPlatform==Model_Type.N900_3G)
				scanUtil.relese();
			else
				scanUtil.release();
		}
	}
	
	public String scanNlsVersion()
	{
		String nlsVersion = null;
		/*if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
			nlsVersion = scan_oversea.getNLSVersion();
		else*/
			nlsVersion = scan_Domestic.getNLSVersion();
		return nlsVersion;
	}
	
	/**sec模块的公共方法
	 * 控制随机键盘位置，目前支持传液晶坐标或者触摸屏的坐标，故使用传入液晶坐标
	 * @return
	 */
	public byte[] getLoadRandomKeyBoard()
	{
		/*int screenWidth = GlobalVariable.TouchWidth;
		
		// 标题栏的高度
		int titleBar = GlobalVariable.TitleBarHeight;
		// 状态栏的高度
		int statusBar = GlobalVariable.StatusBarHeight;
		
		int contentArea = GlobalVariable.TouchHeight;
		titleBar = (int) (titleBar*1.0/(GlobalVariable.ScreenHeight+titleBar)*contentArea);
		statusBar = (int) (statusBar*1.0/(GlobalVariable.ScreenHeight+titleBar)*contentArea);
		contentArea = contentArea - titleBar-statusBar;*/
		int screenWidth = GlobalVariable.ScreenWidth;

//		// 状态栏的高度
//		int statusBar = GlobalVariable.StatusBarHeight;
		
		int contentArea = GlobalVariable.ScreenHeight;
		
		Log.e("Sec status", "-----"+contentArea);
		int y0=0,y1=0,y2=0,y3=0,y4=0;
		// 坐标基点
		int x0 = 0, x1 = screenWidth / 4, x2 = screenWidth / 2, x3 = screenWidth / 4 * 3, x4 = screenWidth;
		
		y0 = (int) (contentArea*1.0/ 2);
		y1 = (int) (contentArea*1.0/ 8 * 5);
		y2 = (int) (contentArea*1.0/ 4 * 3);
		y3 = (int) (contentArea*1.0/ 8 * 7);
		y4 = contentArea;
		
		Log.e("x px:", x0+" "+x1+" "+x2+" "+x3+"  "+x4);
		Log.e("y px:", y0+" "+y1+" "+y2+" "+y3+"  "+y4);
		int[] coordinateInt = new int[] 
				{ x0, y0, x1, y1, x1, y0, x2, y1, x2,
				y0, x3, y1, x3, y0, x4, y1, x0, y1, x1, y2, x1, y1, x2, y2, x2,
				y1, x3, y2, x3, y1, x4, y2, x0, y2, x1, y3, x1, y2, x2, y3, x2,
				y2, x3, y3, x0, y3, x1, y4, x1, y3, x2, y4, x2, y3, x3, y4, x3,
				y2, x4, y4 };
		//初始坐标集合
		byte[] initCoordinate = new byte[coordinateInt.length * 2];
		for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) 
		{
				initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
				j++;
				initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
		}
		return initCoordinate;
	}
	
	// 根据孙雯茜的建议修改随机密码显示格式,修改为 3 3 1格式 by 20170315
	public String parseNum(byte[] num)
	{
		StringBuilder sb = new StringBuilder();
		byte[] numserial=new byte[10];//获取数字键
		int d=0;
		for (int i = 0; i < num.length; i++) 
		{
			if (i == 3 || i == 7 || i == 11 || i == 13 || i == 14) {
				continue;
			}
			numserial[d]=(byte) (num[i]&0x0f);
			if(i==0||i==4||i==8)
			{
				sb.append("\n"+numserial[d]);
			}
			else if(i==12)
			{
				sb.append("\n  "+numserial[d]);
			}
			else
				sb.append(numserial[d]);
			d++;
		}
		return sb.toString();
	}
	
	// 固定密码键盘的显示
	public String fixNum()
	{
		return "\n123\n456\n789\n  0";
	}
	
	/** 
	 * 获取可输入密码的长度范围
	 * @param pinMinLen 允许输入最小长度
	 * @param pinMaxLen 允许输入最大长度
	 * @return
	 */
	public byte[] getPinLengthRange(int pinMinLen,int pinMaxLen){
		byte[] sumPinLen = new byte[]{0x00,0x00,0x00,0x00,0x04,0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C};
		byte[] pinLen = new byte[pinMaxLen-pinMinLen+1]; 
		System.arraycopy(sumPinLen, pinMinLen, pinLen, 0, pinLen.length);
//		byte[] pinLen = {0x00,0x01,0x06};
		return pinLen;
	}
	
	NfcAdapter.ReaderCallback nfcCallBack;
	Nfc_Card nfc_Card;
	protected Nfc_Card ex_nfc_card;
	byte[] data ={0x00,(byte) 0x84,0x00,0x00,0x08};
	byte[] m1Key = {(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff};
	byte[] back;
	byte[] respCode_nfc = new byte[2];
	// 返回码
	byte[] code1 = {(byte) 0x90,0x00};
	byte[] code2 = {0x6D,0x00};
//	Gui gui = new Gui(this, handler);
	byte[] DATA16 = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
	byte[] readData = new byte[16];
	
	@SuppressLint("NewApi")
	public void initReaderCallback() {

		try {

			nfcCallBack = new NfcAdapter.ReaderCallback() {
				@Override
				public void onTagDiscovered(final Tag tag) {
					
					Log.d("eric_chen", "onTagDiscovered回调---------");
					String[] s = tag.getTechList();
					for (int i = 0; i < s.length; i++) {
						if (s[i].equals(NfcB.class.getName())) {
							nfc_Card = Nfc_Card.NFC_B;
							break;
						} else if (s[i].equals(NfcA.class.getName())) {
							nfc_Card = Nfc_Card.NFC_A;
							break;
						}

						else if (s[i].equals(MifareClassic.class.getName())) {
							nfc_Card = Nfc_Card.NFC_M1;
							break;
						}

					}
					switch (nfc_Card) {
					case NFC_A:
						try {
							Log.d("eric_chen", "onTagDiscovered回调---------A");
							NfcA mNfcA = NfcA.get(tag);
							mNfcA.connect();
							back = mNfcA.transceive(data);
							System.arraycopy(back, back.length - 2, respCode,0, 2);
						} catch (IOException e) {
							e.printStackTrace();
						}

					case NFC_B:
						try {
							Log.d("eric_chen", "onTagDiscovered回调---------B");
							NfcB mNfcB = NfcB.get(tag);
							mNfcB.connect();
							back = mNfcB.transceive(data);
							System.arraycopy(back, back.length - 2, respCode,0, 2);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;

					case NFC_M1:
						// 认证操作
						try 
						{
							Log.d("eric_chen", "onTagDiscovered回调---------C");
							MifareClassic mifareClassic = MifareClassic.get(tag);
							mifareClassic.connect();
							// 扇区15
							mifareClassic.authenticateSectorWithKeyA(15,m1Key);
							mifareClassic.writeBlock(60, DATA16);
							readData = mifareClassic.readBlock(60);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;

					default:
						break;
					}
				}
			};
		} catch (NoClassDefFoundError e) {
		}
	}
	
	
	/**
	 * 将IC/SAM卡下电
	 * @param icCardModule 模块
	 */
	public void icSamOff()
	{
		
		for (int i = 0; i < GlobalVariable.cardNo.size(); i++) 
		{
			JniNdk.JNI_Icc_PowerDown(GlobalVariable.cardNo.get(i).ordinal());
		}
	}
	
	
	// modem对应的方法
	// modem复位
	public int modem_reset(NlModemManager asynModem)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try 
		{
			ret = asynModem.reset();
			// 复位之后modem要休眠3s，by 陈仕廉建议
			SystemClock.sleep(3000);
		} catch (RemoteException e) 
		{
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// 设置modem链路参数
	public int modem_propSet(NlModemManager nlModemManager,String name,int i)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = nlModemManager.propSet(name,i);
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem初始化
	public int modem_init(NlModemManager asynModem,int i,LinkType type)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		switch (type) {
		case ASYN:
			try {
				ret = asynModem.asynInit(i);
				// 初始化休息2s
				SystemClock.sleep(2000);
			} catch (RemoteException e1) {
				e1.printStackTrace();
				ret = MODEM_EXCEPTION_THROW;
			}
			break;
			
		case SYNC:
			try {
				ret = asynModem.sdlcInit(i);
				// 初始化休息2s
				SystemClock.sleep(2000);
			} catch (RemoteException e1) {
				e1.printStackTrace();
				ret = MODEM_EXCEPTION_THROW;
			}
			break;
			
		default:
			break;
		}
		return ret;
	}
	
	// 清空缓存区
	public int modem_clrbuf(NlModemManager asynModem)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.clrbuf();
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem拨号操作
	public int modem_dial(NlModemManager asynModem,String dial)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.dial(dial);
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem写数据
	public int modem_write(NlModemManager asynModem,byte[] buf,int len)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.write(buf, len);
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem读数据
	public int modem_read(NlModemManager asynModem,byte[] rbuf,int rlen,int time)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.read(rbuf, rlen, time);
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem挂断操作
	public int modem_hangup(NlModemManager asynModem)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.hangup();
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem检测状态
	public int modem_check(NlModemManager asynModem)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.check();
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem获取长度
	public int modem_readLen(NlModemManager asynModem)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = asynModem.getreadlen();
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// modem执行命令
	public int modem_exCommand(NlModemManager nlModemManager,byte[] buf1,byte[] buf2,int timeout)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = nlModemManager.exCommand(buf1, buf2, timeout);
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// mode上下电操作
	public int modem_powerCtrl(NlModemManager nlModemManager,int cmd)
	{
		/*private & local definition*/
		int ret = -1;
		
		/*process body*/
		try {
			ret = nlModemManager.powerCtrl(cmd);
		} catch (RemoteException e) {
			e.printStackTrace();
			ret = MODEM_EXCEPTION_THROW;
		}
		return ret;
	}
	
	// IC SAM卡模块
	// 对所有ic/sam卡下电
	public void ic_sam_poweroff()
	{
		for (int i = 0; i < GlobalVariable.cardNo.size(); i++) {
			JniNdk.JNI_Icc_PowerDown(GlobalVariable.cardNo.get(i).ordinal());
		}
	}
	
	// 进行飞行模式的设置 add by zhengxq 20171019
	public void setAirPlaneMode(Context context,boolean enable,int value)
	{
		// <4.2
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			Settings.System.putInt(myactivity.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, value);
		}
		else
		{
			Settings.System.putInt(myactivity.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, value);
		}
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("statu", enable);
		myactivity.sendBroadcast(intent);
	}
	public void getWifiMac(){
		final EditText addressText = new EditText(myactivity);
		new BaseDialog(myactivity, addressText, "输入要搜索的mac地址", "确定", "取消",new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive)
				{
					mac=addressText.getText().toString();
					synchronized (g_lock) {
						g_lock.notify();
					}
				}
			}
			
		}).show();
	}
	
	public int  uart3ManagerWrite(final NLUART3Manager uart3Manager,byte[] buf,int lengthMax,int timeoutSec){
		ret=RS232_TIMEOUT;
		writeThread=new WriteThread(uart3Manager, buf, lengthMax, timeoutSec);
		writeThread.start();
		try {
			writeThread.join(30*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	public int  uart3ManagerRead(final NLUART3Manager uart3Manager,final byte[] buf,final int lengthMax,final int timeoutSec){
		ret=RS232_TIMEOUT;
		readThread=new ReadThread(uart3Manager, buf, lengthMax, timeoutSec);
		readThread.start();
		try {
			readThread.join(40*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	
	@Override
	public boolean onLongClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_key_enter2:
			LoggerUtil.e("长按确认键退出测试");
			if (GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull ) {
				ActivityManager.getActivityManager().popAllActivity();
				Intent intent = new Intent(myactivity,PatternActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				myactivity.onDestroy();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			if (GlobalVariable.isShowBack) {
				
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
	
	/**
	 * 进行后置操作
	 */
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		Log.d("UnitF", "onDestroy");
		mTestConfigListener.onTestDown();
	}
	
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	/**
	 * mpos指令操作
	 */
	public int sendMposCmd(K21ControllerManager k21ControllerManager,byte[] cmdByte,byte[] retContent){
		K21DeviceResponse response;
		byte[] retBuf = new byte[1024];
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(cmdByte), null);
		Arrays.fill(retContent, (byte) 0);
		retBuf = response.getResponse();
		for(int i = 0; i<retBuf.length;i++){
			retContent[i] = retBuf[i];
		}
		String retCode = ISOUtils.dumpString(retContent, 7, 2);
		int ret = Integer.parseInt(retCode);
		LoggerUtil.e("ret:"+ret);
		switch(ret){
			case 0:
				return SDK_OK;
				
			case 2:
				return SDK_ERR;
				
			case 6:
				return SDK_ERR_INVOKE_FAILED;
				
			case 7:
				return SDK_ERR_TIMEOUT;
				
			default:
				//gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
				LoggerUtil.e("SDK_ERR:"+retCode);
				return ret;
		}
	}
	
	/**判断当前网路是否可用*/
	public boolean isNetAvailable(String netFileUrl){
		InputStream netFileInputStream =null;
		try{
		     URL url= new URL(netFileUrl);   
		     URLConnection urlConn= url.openConnection();   
		     netFileInputStream = urlConn.getInputStream(); 
		 }catch (IOException e)
		 {
		     return false;
		 }
		 if(null!=netFileInputStream){
		      return true;
		 }else{
		     return false;
		 }
	}
}
