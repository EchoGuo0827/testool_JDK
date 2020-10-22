package com.example.highplattest.systest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.newland.SettingsManager;
import android.newland.content.NlIntent;
import android.newland.net.ethernet.NlEthernetManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug.FlagToString;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.adapter.DeviceAdapater;
import com.example.highplattest.main.bean.ApplicationExceptionBean;
import com.example.highplattest.main.bean.NlsPara;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_MODE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_DES;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_ALG;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_MAC;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.ChangeWireType;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.ExcelUtils;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;
import com.example.highplattest.tool.trade.TradeTool;
import com.newland.k21controller.util.Dump;
//import com.example.highplattest.tool.trade.TradeTool;
import com.newland.ndk.JniNdk;
import com.newland.ndk.RsaPrivateKey;
import com.newland.ndk.RsaPublicKey;
import com.newland.ndk.SecKcvInfo;

/************************************************************************
 * 
 * module : SysTest综合模块 file name : SysTest300.java description : 高端性能测试 related
 * document : history : 变更记录 变更时间 变更人员 高端平台性能测试 20200622 郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest300 extends DefaultFragment {
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "高端全部性能";
	Gui gui = new Gui(myactivity, handler);
	final String  TEST_EXLFILE = "/sdcard/ability_V1.xls";
	final String TAG = SysTest300.class.getSimpleName();
	
	/**
	 * BLE蓝牙使用
	 */
	String blutoothName;
	BluetoothAdapter mBluetoothAdapter;
	/** 蓝牙适配器 */
	BluetoothLeScanner mScanner;
	/** BLE蓝牙扫描 */
	ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	/** 扫描到的蓝牙设备 */
	List<BluetoothGattService> mGattServices = new ArrayList<BluetoothGattService>();
	/** 获取到的蓝牙服务 */
	BluetoothGattService mGattDeivceService;
	HashMap<String, BluetoothGattCharacteristic> mCharateristicMap;
	DeviceAdapater mDeviceAdapater;
	BluetoothDevice mBluetoothDevice;
	BluetoothGatt mBluetoothGatt;
	BluetoothGattCharacteristic writeCharacteristic;
	BluetoothGattCharacteristic readCharacteristic;
	final int WAITIMEOUT = 10 * 1000;
	boolean gScanflag = false;
	boolean isServiceCom = false;
	boolean isReadStart = false;
	boolean isWriteStart = false;
	boolean isNotifyStart = false;
	byte[] rbuf = new byte[16];
	byte[] wbuf = new byte[16];
	boolean BLERFLAG=false;
	

	// IC_SAM的性能测试
	public void icSamAbility(ExcelUtils excelUtils,String model) {
		/** 要添加写表格操作 */
		int ICSAM_NUM = GlobalVariable.cardNo.size();
		EM_ICTYPE[] slot = new EM_ICTYPE[ICSAM_NUM];
		for (int i = 0; i < ICSAM_NUM; i++) {
			slot[i] = GlobalVariable.cardNo.get(i);
		}

		/* private & local definition */
		String funcName = "icSamAbility";
		final int N_TEST_TIME = 10;
		int ret = -1, i = 0, count = 0;
		byte[] buf = new byte[128];
		float time = 0;
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		float fTimeElapsed;

		/* process body */
		// Poynt产品要先关闭检卡
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
		{
			JniNdk.JNI_SetCheckinCardEventFlag(0);
		}
		do {
			//-5一般是未插卡
			if ((ret = JniNdk.JNI_Icc_PowerUp(slot[i].ordinal(), psAtrBuf,pnAtrLen)) == -5) {
				gui.cls_show_msg("请先插入%s卡,放置完毕任意键继续", slot[i]);
			}
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 2, slot[i] == EM_ICTYPE.ICTYPE_IC ? "IC": slot[i] == EM_ICTYPE.ICTYPE_SAM1 ? "SAM1": "SAM2", model);
			gui.cls_printf(String.format("正在测试%s上电性能...", slot[i]).getBytes());
			// 上电
			count = 0;
			fTimeElapsed = 0.0f;
			long startTime = System.currentTimeMillis();
			while (true) {
				if ((ret = JniNdk.JNI_Icc_PowerUp(slot[i].ordinal(), psAtrBuf,pnAtrLen)) == NDK_OK) {
					LoggerUtil.d("icSamAbility iccPowerOn");
					count++;
					if ((fTimeElapsed = Tools.getStopTime(startTime)) > N_TEST_TIME) // 上电时间超过10s退出
						break;
				}
				/** 累计一分钟还未成功，可认为是未插卡的情况，退出*/
				if(Tools.getStopTime(startTime)>60)
					break;
			}
			if (fTimeElapsed > N_TEST_TIME) {
				time = (float) (count * 1.0 / fTimeElapsed);
//				excelUtils.writeDataXls(TEST_EXLFILE, searchMap, digFlo(time));
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"%s上电性能为%s次/s", slot[i], time);
			} else
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"line %d:累积成功读写时间不足10秒（%s卡，fTimeElapsed = %f）",Tools.getLineInfo(), slot[i], fTimeElapsed);

			// 测试iccrw性能
			gui.cls_show_msg1(2, "正在测试NDK_Iccrw对%s读写速度...", slot[i]);
			count = 0;
			fTimeElapsed = 0;
			Arrays.fill(buf, (byte) 0);
			startTime = System.currentTimeMillis();
			while (true) {
				if ((ret = JniNdk.JNI_Icc_Rw(slot[i].ordinal(), req.length,req, pnAtrLen, psAtrBuf)) == NDK_OK) {
					count++;
					if ((fTimeElapsed = Tools.getStopTime(startTime)) > N_TEST_TIME)// 读写时间超过10s退出
						break;
				}
				/** 累计一分钟还未成功，可认为是未插卡的情况，退出*/
				if(Tools.getStopTime(startTime)>60)
					break;
			}
			if (fTimeElapsed > N_TEST_TIME) {
				LoggerUtil.d("icSamAbility->JNI_Icc_Rw=" + pnAtrLen[0]);
				time = (float) (count * pnAtrLen[0] * 1.0 / fTimeElapsed);
				excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(time)});
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"NDK_Iccrw每秒读%s卡:%f字节", slot[i], time);
			} else
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"line %d:累积成功读写时间不足10秒（%s卡，fTimeElapsed = %f）",Tools.getLineInfo(), slot[i], fTimeElapsed);

			// 下电
			if ((ret = icSamPowerOff(slot[i])) != NDK_OK) // 不关注成功与否，由压力测试去关注
			{
				icSamPowerOff(slot[i]);
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"line %d:%s下电失败(%d)", Tools.getLineInfo(), slot[i], ret);
				continue;
			}

		} while (++i != ICSAM_NUM);
	}

	// 打印性能测试：要分别测试电量100%，电量50%，电量20%的情况
	public void printAbility(ExcelUtils excelUtils,String model) {
		
		int ret;
		if (GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnable) == false) {
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "满电", model);
			String datas[] = {"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"};
			excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
			gui.cls_show_msg("%s产品不支持打印模块,无需测试该项性能,任意键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName = "printAbility";
		// 测试前置要放置打印图片
		if(new File("/sdcard/picture/blank_bill.bmp").exists()==false||new File("/sdcard/picture/standard_bill.bmp").exists()==false||
				new File("/sdcard/picture/carrefour1.png").exists()==false||new File("/sdcard/picture/carrefour2.png").exists()==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, mScrTime, "请先将打印图片放置到/sdcard/picture目录下,完成任意键继续");
		}
		PrintUtil printUtil = new PrintUtil(myactivity,handler,true);
		long startTime;
		int num = 0;

		float[] printmms = { 253f,15f,139f,165f, 0, 662f };// 空白单的打印长度25.3，票据的打印长度13.9mm，家乐福的打印长度，单位是mm
		List<String> batteryValues = new ArrayList<String>();// 对应的电压值要给出，因为此项测试暂时是需要更换电池，还是设计成稳压源测试？？
		String[] volValues = { "8.4V", "7.34V", "7.11V" };
		if (GlobalVariable.gModuleEnable.get(Mod_Enable.Battery))// 手持机需要测试多种电量情况
		{
			batteryValues.add("满电");
			batteryValues.add("50%电量");
			batteryValues.add("20%电量");
		} else// 台式机不需要测试不同电量之间的差异，高端是X5、N850
		{
			batteryValues.add("满电");
		}
		float blankTime, fillBillTime,standardBillTime, shopBillTime, carrefourTime = 0;

		JniNdk.JNI_Print_SetGrey(3);
		if ((ret = JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnSetMode测试失败(ret=%d)", Tools.getLineInfo(),ret);
			return;
		}

		do {
			String valueBat = batteryValues.get(num);
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.Battery))
			{
				gui.cls_show_msg("请将稳压源的电压调至%s，操作完毕任意键开始测试，台式机直接插入电源测试即可====",volValues[num]);
			}
			gui.cls_printf(String.format("case1:【%s】打印空白单测试中...", valueBat).getBytes());
			Bitmap bit = BitmapFactory.decodeFile("/sdcard/picture/blank_bill.bmp");
			byte[] imgBuf = bitmapToBuf(bit, printUtil);

			startTime = System.currentTimeMillis();
			// 1.使用NDK_PrnImage的打印空白单
			if ((ret = JniNdk.JNI_Print_Image(bit.getWidth(), bit.getHeight(),0, imgBuf)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印空白单失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印空白单失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			blankTime = Tools.getStopTime(startTime);
			
			// 2.打印填充单的耗时
			gui.cls_printf(String.format("case2:【%s】打印填充单测试中...", valueBat).getBytes());
			Bitmap fillBillBmp = BitmapFactory.decodeFile("/sdcard/picture/fill_bill.bmp");
			byte[] fillBillBuf = bitmapToBuf(fillBillBmp, printUtil);

			startTime = System.currentTimeMillis();
			// 1.使用NDK_PrnImage的打印填充单
			if ((ret = JniNdk.JNI_Print_Image(fillBillBmp.getWidth(), fillBillBmp.getHeight(),0, fillBillBuf)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印填充单失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印填充单失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			fillBillTime = Tools.getStopTime(startTime);
			
			// 3.使用NDK_PrnImage打印票据【印字率低】
			gui.cls_printf(String.format("case3:【%s】打印standard_bill.bmp测试中[印字率低]...", valueBat).getBytes());
			Bitmap bit2 = BitmapFactory.decodeFile("/sdcard/picture/standard_bill.bmp");
			byte[] imgBuf2 = bitmapToBuf(bit2, printUtil);
			startTime = System.currentTimeMillis();
			if ((ret = JniNdk.JNI_Print_Image(bit2.getWidth(),bit2.getHeight(), 0, imgBuf2)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印票据失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印票据失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			standardBillTime = Tools.getStopTime(startTime);
			

			// 4.单行内容大于12.5%【印字率高】
			gui.cls_printf(String.format("case4:【%s】打印shop_bill.bmp测试中[印字率高]...", valueBat).getBytes());
			Bitmap bit3 = BitmapFactory.decodeFile("/sdcard/picture/shop_bill.bmp");
			byte[] shopBillBuf = bitmapToBuf(bit3, printUtil);
			startTime = System.currentTimeMillis();
			if ((ret = JniNdk.JNI_Print_Image(bit3.getWidth(),bit3.getHeight(), 0, shopBillBuf)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印shop_bill.bmp失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NDK_PrnImage打印票据失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if ((ret = JniNdk.JNI_Print_Start()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:JNI_Print_Str打印票据失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			shopBillTime = Tools.getStopTime(startTime);

			// 5.统计30s内打印单行脚本的总行数
			gui.cls_printf(String.format("case5:【%s】统计30s内打印单行脚本的总行数...", valueBat).getBytes());
			String data = "中国建设银行股份有限公司";
			int count = 0;
			startTime = System.currentTimeMillis();
			while (Tools.getStopTime(startTime) < 30) {
				// 使用默认打印浓度 这里设置成6 和默认一致。
				ret = printUtil.print_byttfScript_ccb(String.format("!font /sdcard/picture/simsun.ttc\n!gray 6\n!yspace 0\n*text l %s\n",(++count) + "   " + data));
				if (ret != NDK_OK) {
					gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:打印失败(ret=%d count=%d)",Tools.getLineInfo(), ret, count);
				}
			}
			

			// 6.打印家乐福图片并切纸，只有CPOS支持切刀
			if (GlobalVariable.currentPlatform == Model_Type.X5) {
				int prnStatus;
				String feedline = "*feedline p:48\n";
				String data1 = "!NLFONT 9 12\n"
						+ "*image l 576*961 path:/mnt/sdcard/picture/carrefour1.png\n"
						+ "*line\n" + "*feedline p:200\n" + "*cut\n";
				String data2 = "!NLFONT 9 12\n"
						+ "*image l 576*1126 path:/mnt/sdcard/picture/carrefour2.png\n"
						+ "*line\n" + "*feedline p:200\n" + "*cut\n";

				String datafinal = feedline + data1 + data2 + data2 + data2;
				gui.cls_printf(String.format("case6:【%s】以下打印4张家乐福票据并切纸。联迪打印时间约为6s，请对比打印时间性能。", valueBat).getBytes());
				startTime = System.currentTimeMillis();
				prnStatus = printUtil.print_byttfScript_ccb(datafinal);
				if (prnStatus != NDK_OK) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打印失败(%d)", Tools.getLineInfo(),prnStatus);
				}
				carrefourTime = Tools.getStopTime(startTime);
			}

			blankTime = printmms[0] / blankTime;
			fillBillTime = printmms[1]/fillBillTime;
			standardBillTime = printmms[2] / standardBillTime;
			shopBillTime = printmms[3] / shopBillTime;
			String carreTra="NA";
			if(GlobalVariable.currentPlatform==Model_Type.X5)
			{
				carrefourTime = printmms[5] / carrefourTime;
				carreTra = digFlo(carrefourTime);
			}
			
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, valueBat, model);
			String datas[] = {digFlo(blankTime),digFlo(fillBillTime),digFlo(standardBillTime),digFlo(shopBillTime),digFlo(count),carreTra};
			excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
			
			gui.cls_show_msg1_record(TAG,funcName,mScrTime,"【%s】打印空白单速率:%3.2fmm/s,打印填充单的速率:%3.2fmm/s,打印standard_bill的速率:%3.2fmm/s,打印shop_bill的速率:%3.2fmm/s",
					valueBat, blankTime, fillBillTime, standardBillTime, shopBillTime);
			gui.cls_show_msg1_record(TAG,funcName,mScrTime,"【%s】30s内打印单行脚本的总行数:%d行,打印家乐福速率:%smm/s",valueBat,count,carreTra);
		} while (++num != batteryValues.size());
	}

	// 打印图片 偏移为0 传Bitmap
	public byte[] bitmapToBuf(Bitmap bit, PrintUtil printUtil) {
		bit = printUtil.gray2Binary(bit);
		byte[] imgBuf = printUtil.calcBuffer(bit, bit.getHeight(),bit.getWidth());
		return imgBuf;
	}

	public void fsAbility(ExcelUtils excelUtils,String model) 
	{
		String funcName = "fsAbility";
		final String FILETESTG8 = "/appfs/FTG8";
		final String RENAMEFILE = "/appfs/rename";
		final int BUFLEN = 1024;
		int times = 255, fp/** 文件句柄 */
		, writelen, readlen, tempRet = -1;
		float createTime=0.0f,writeTime = 0, readTime = 0, updateTime = 0, searchTime = 0, delTime = 0,renameTime=0;
		byte[] writebuf = new byte[BUFLEN];
		byte[] readbuf = new byte[BUFLEN];

		// 测试前置，删除文件
		JniNdk.JNI_FsDel(FILETESTG8);
		
		gui.cls_printf("创建文件中...".getBytes());
		long startTime=0;
		for (int j = 0; j < times; j++) {
			JniNdk.JNI_FsDel(FILETESTG8);
			startTime = System.currentTimeMillis();
			if ((fp = JniNdk.JNI_FsOpen(FILETESTG8, "w")) < 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开文件失败(%d,%d)", Tools.getLineInfo(), j,fp);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			createTime = createTime+(System.currentTimeMillis()-startTime);
			JniNdk.JNI_FsClose(fp);
		}
		createTime = createTime/times;
		
		Arrays.fill(writebuf, (byte) (Math.random() * 255));
		if ((fp = JniNdk.JNI_FsOpen(FILETESTG8, "w")) < 0) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开文件失败(%d)", Tools.getLineInfo(), fp);
			JniNdk.JNI_FsClose(fp);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}

		// 写文件->1K*255次
		gui.cls_printf("写文件中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int j = 0; j < times; j++) {
			if ((writelen = JniNdk.JNI_FsWrite(fp, writebuf, BUFLEN)) != BUFLEN) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:写文件失败(%d,%d)", Tools.getLineInfo(), j,writelen);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
		}
		writeTime = times/(writeTime + Tools.getStopTime(startTime));

		if ((tempRet = JniNdk.JNI_FsClose(fp)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d：关闭文件失败(%d)", Tools.getLineInfo(), tempRet);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}
		if ((fp = JniNdk.JNI_FsOpen(FILETESTG8, "r")) < 0) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开文件失败(%d)", Tools.getLineInfo(), fp);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}

		// 读文件->1K*255
		gui.cls_printf("读文件中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int j = 0; j < times; j++) {
			if ((readlen = JniNdk.JNI_FsRead(fp, readbuf, BUFLEN)) != BUFLEN) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:读文件失败(%d)", Tools.getLineInfo(), readlen);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
		}
		readTime = times/(readTime + Tools.getStopTime(startTime));
		
		if ((tempRet = JniNdk.JNI_FsClose(fp)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d：关闭文件失败(%d)", Tools.getLineInfo(), tempRet);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}
		if ((tempRet = JniNdk.JNI_FsDel(FILETESTG8)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:删除文件失败(%d)", Tools.getLineInfo(), tempRet);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}
		// 更新文件（1K）文件更新就是写文件
		fp = JniNdk.JNI_FsOpen(FILETESTG8, "w");
		gui.cls_printf("更新文件中...".getBytes());
		Arrays.fill(writebuf, (byte) (Math.random() * 256));
		startTime = System.currentTimeMillis();
		for (int j = 0; j < times; j++) {
			if ((tempRet = JniNdk.JNI_FsSeek(fp, BUFLEN * j, 1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:移动文件指针失败(%d,%d)", Tools.getLineInfo(), j,tempRet);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			if ((writelen = JniNdk.JNI_FsWrite(fp, writebuf, BUFLEN)) != BUFLEN) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:更新文件失败(%d,%d)", Tools.getLineInfo(), j,
						writelen);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
		}
		updateTime = times/(updateTime + Tools.getStopTime(startTime));
		// 查找文件
		gui.cls_printf("查找文件中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int j = 0; j < times; j++) {
			if ((tempRet = JniNdk.JNI_FsSeek(fp, BUFLEN * j, 1)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:移动文件指针失败(%d,%d)", Tools.getLineInfo(), j,tempRet);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			if ((readlen = JniNdk.JNI_FsRead(fp, readbuf, BUFLEN)) != BUFLEN) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:查找文件失败(%d,%d)", Tools.getLineInfo(), j,readlen);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
		}
		searchTime = times/(searchTime + Tools.getStopTime(startTime));
		
		if ((tempRet = JniNdk.JNI_FsClose(fp)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d：关闭文件失败(%d)", Tools.getLineInfo(), tempRet);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}
		
		// 重命名时间计算
		gui.cls_printf("重命名文件中...".getBytes());
		for (int j = 0; j < times; j++) {
			startTime = System.currentTimeMillis();
			if ((tempRet = JniNdk.JNI_FsRename(FILETESTG8, RENAMEFILE)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:删除文件失败(%d,%d)", Tools.getLineInfo(), j,tempRet);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			renameTime = renameTime+(System.currentTimeMillis()-startTime)*1.0f;
			if ((tempRet = JniNdk.JNI_FsRename(RENAMEFILE,FILETESTG8)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:删除文件失败(%d)", Tools.getLineInfo(),tempRet);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
		}
		renameTime = renameTime/times;
		if ((tempRet = JniNdk.JNI_FsDel(FILETESTG8)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:删除文件失败(%d)", Tools.getLineInfo(), tempRet);
			JniNdk.JNI_FsDel(FILETESTG8);
			return;
		}
		
		// 删除文件(1K)
		gui.cls_printf("删除文件中...".getBytes());
		Arrays.fill(writebuf, (byte) (Math.random() * 256));
		for (int j = 0; j < times; j++) {
			if ((fp = JniNdk.JNI_FsOpen(FILETESTG8, "w")) < 0) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开文件失败(%d,%d)", Tools.getLineInfo(), j, fp);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			if ((writelen = JniNdk.JNI_FsWrite(fp, writebuf, BUFLEN)) != BUFLEN) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:写文件失败(%d,%d)", Tools.getLineInfo(), j,
						writelen);
				JniNdk.JNI_FsClose(fp);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			if ((tempRet = JniNdk.JNI_FsClose(fp)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:关闭文件失败(%d,%d)", Tools.getLineInfo(), j,tempRet);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			startTime = System.currentTimeMillis();
			if ((tempRet = JniNdk.JNI_FsDel(FILETESTG8)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:删除文件失败(%d,%d)", Tools.getLineInfo(), j,tempRet);
				JniNdk.JNI_FsDel(FILETESTG8);
				return;
			}
			delTime = (delTime + Tools.getStopTime(startTime));
		}
		delTime = times/delTime;

		/**写入性能值到Excel表格*/
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "FS", model);
		String[] datas = {digFlo(createTime),digFlo(delTime),digFlo(updateTime),
				digFlo(searchTime),digFlo(renameTime),digFlo(readTime),digFlo(writeTime)};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		// 创建文件耗时
		// 删除文件耗时
		// 文件更新耗时
		// 文件查找耗时
		// 重命名文件耗时
		// 读文件速度
		// 写文件速度
		// 最后统计速度
		gui.cls_show_msg1_record(TAG,funcName,mScrTime,
				"写文件速度%3.2fkb/s,读文件速度%3.2fkb/s,更新文件速度%3.2fkb/s,查找文件速度%3.2fkb/s," +
				"删除文件速度%3.2fkb/s,创建文件性能:%.2fms/次,重命名文件性能:%.2fms/次",
				 writeTime, readTime, updateTime, searchTime, delTime,createTime,renameTime);
	}

	// SD卡|TF卡|U盘的性能
	public void andFileAbility(ExcelUtils excelUtils,String model) {
		/* private & local definition */
		String funcName = "andFileAbility";
		final int BUFFERSIZE = 1024 * 200;
		int testDataSize = 10 * 1024 * 1024;// 总公读写10M
		byte[] writebuf = new byte[BUFFERSIZE];// 每次读写200K
		byte[] readbuf = new byte[BUFFERSIZE];

		byte[] readTotalBuf = new byte[testDataSize];
		byte[] writeTotalBuf = new byte[testDataSize];
		int cnt = testDataSize / BUFFERSIZE, ABILITYNUM = 20;
		long ret = -1;
		float openTime = 0.0f, writetime = 0.0f, readtime = 0.0f, renameTime = 0.0f, deltime = 0.0f;
		boolean isErrExit = false;
		FileSystem fileSystem = new FileSystem();
		int equipNum = GlobalVariable.currentPlatform==Model_Type.N550?2:3;// 暂时屏蔽，不然无法测试
//		int equipNum = 2;

		int num = 0;
		List<String> tempDisk = new ArrayList<String>();
		List<String> tempPaths = new ArrayList<String>();

		gui.cls_show_msg("请确保已插上TF卡和U盘,TF卡和U盘挂载完毕任意键继续");

		List<List<String>> totalStorages = Tools.getStoragePath(myactivity);
		tempDisk = totalStorages.get(0);
		tempPaths = totalStorages.get(1);
		if (tempPaths.size() != equipNum) {
			gui.cls_show_msg1_record(TAG, funcName, 0, "部分文件系统未挂载成功,请确认挂载成功后进入本案例，已挂载设备个数=%d", tempDisk.size());
			return;
		}

		do {
			String diskType = tempDisk.get(num);
			// 获取目录
			String fname = tempPaths.get(num) + "test.txt";

			int i = 0;
			while (i < ABILITYNUM) {
				/** 每次10M，共20次（10M） */
				isErrExit = false;
				if (gui.cls_show_msg1(2, "开始第%d次%s读写,[取消]退出测试...", i + 1,diskType) == ESC)
					break;
				i++;
				// 测试前置删除文件
				if (fileSystem.JDK_FsExist(fname) == JDK_OK) {
					fileSystem.JDK_FsDel(fname);
				}

				LoggerUtil.d("andFileAbility->fname=" + fname);
				// 打开文件
				long startTime = System.currentTimeMillis();
				for (int j = 0; j < writeTotalBuf.length; j++) {
					
				}
				if ((ret = fileSystem.JDK_FsOpen(fname, "w")) < 0) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次创建测试文件失败(%d)", Tools.getLineInfo(), i,ret);
					break;
				}
				openTime = openTime + (System.currentTimeMillis()-startTime);
				// 写文件
				gui.cls_printf(String.format("第%d次生成测试文件约%dKB", i,cnt * BUFFERSIZE / 1024).getBytes());
				for (int j = 0; j < BUFFERSIZE; j++)
					writebuf[j] = (byte) (Math.random() * 256);

				startTime = System.currentTimeMillis();
				LoggerUtil.e("andFileAbility->cnt=" + cnt);
				for (int j = 0; j < cnt; j++) {
					// 此为有追加的模式
					if ((ret = fileSystem.JDK_FsWrite(fname, writebuf,BUFFERSIZE, 2)) != BUFFERSIZE) {
						gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次写测试文件失败(%d)", Tools.getLineInfo(),i, ret);
						isErrExit = true;
						break;
					}
					System.arraycopy(writebuf, 0, writeTotalBuf,j * BUFFERSIZE, BUFFERSIZE);
				}
				if (isErrExit)// 本次出错退出
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次测试写文件操作失败", Tools.getLineInfo(), i + 1);
					break;
				}

				writetime = writetime + Tools.getStopTime(startTime);

				// 文件大小是否发生改变
				if ((ret = fileSystem.JDK_FsFileSize(fname)) != cnt* BUFFERSIZE) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"%s, line %d:文件大小校验失败(实测:%dB, 预期:%dB)", TAG,Tools.getLineInfo(), ret, cnt * BUFFERSIZE);
					break;
				}

				// 读文件
				gui.cls_printf(String.format("第%d次校验%s文件中(约%dKB),请稍后...", i,diskType, cnt * BUFFERSIZE / 1024).getBytes());
				startTime = System.currentTimeMillis();
				for (int j = 0; j < cnt; j++) {
//					 LoggerUtil.e("loop"+j+"===========offset="+j*BUFFERSIZE);
					// 注意偏移值的变化
					if ((ret = fileSystem.JDK_FsReadOffeset(fname,readbuf,j*BUFFERSIZE,BUFFERSIZE,2)) != BUFFERSIZE) {
						gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次读文件失败(%d)", Tools.getLineInfo(),i, ret);
						isErrExit = true;
						break;
					}
					System.arraycopy(readbuf, 0, readTotalBuf, j * BUFFERSIZE,BUFFERSIZE);
				}
				if (isErrExit) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次测试写文件操作失败", Tools.getLineInfo(), i + 1);
					break;
				}
				readtime = readtime + Tools.getStopTime(startTime);
				
				// 重命名测试文件
				startTime = System.currentTimeMillis();
				fileSystem.JDK_FsRename(tempPaths.get(num),"test.txt", "renametxt.txt");
				renameTime = renameTime + (System.currentTimeMillis()-startTime);
				// 删除测试文件
				startTime = System.currentTimeMillis();
				fileSystem.JDK_FsDel(fname);
				deltime = deltime + Tools.getStopTime(startTime);
			}

			openTime = openTime/ABILITYNUM;
			renameTime = renameTime/ABILITYNUM;
			deltime = cnt*200*ABILITYNUM/deltime/1024;
			writetime = cnt*200*ABILITYNUM/writetime/1024;
			readtime = cnt*200*ABILITYNUM/readtime/1024;
			LoggerUtil.e(funcName + "openTime=" + openTime + ",renameTime=" + renameTime + ",delTime=" + deltime);
			if (i == ABILITYNUM) {
				gui.cls_show_msg1_record(TAG,funcName,mScrTime,
						"(%s)创建文件性能:%3.2fms/次,重命名文件性能:%3.2fms/次,删除文件性能:%3.2fMB/s,写数据速度:%3.2fMB/s,读数据速度:%3.2fMB/s",
						diskType, openTime, renameTime, deltime,  writetime,readtime);
			}
			// 查找表格中的位置
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, diskType, model);
			String datas[] = {"NA","NA",digFlo(openTime),digFlo(deltime),"NA","NA",
					digFlo(renameTime),digFlo(readtime),digFlo(writetime)};
			excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
			// 设备文件时间，高端不支持
			// 设备关闭事件，高端不支持
			// 创建文件耗时
			 // 删除文件耗时
			 // 文件更新耗时
			 // 文件查找耗时
			 // 重命名文件耗时
			 // 读文件速度
			 // 写文件速度
		} while (++num != tempDisk.size());

		LoggerUtil.d("readTime：" + readtime + "\nwriteTime:" + writetime);
	}

	public void rfidAbility(_SMART_t type,ExcelUtils excelUtils,String model)
	{
		String funcName ="rfidAbility";
		byte[] uid = new byte[20];
		int count=0;
		int[] uidLen = new int[1];
		int[] rcvlen = new int[1];
		final int nTestTime = 10;
		float detectTime = 0,fTimeElapsed=0;
		String rfidType;
		switch (type) {
		case MIFARE_1:
			rfidType = "Mifare_1";
			break;
			
		case MIFARE_0:
		case MIFARE_0_C:
			rfidType="Mifare_0";
			break;
			
		case FELICA:
			rfidType = "Felica";
			break;

		default:
			rfidType = type.toString();
			break;
		}
		// Poynt产品在测试性能前要关闭检卡接口
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
		{
			JniNdk.JNI_SetCheckinCardEventFlag(0);
		}
		
		gui.cls_show_msg("【请使用性能专用卡测试】请在感应区放置%s卡,任意键继续...",type);
		// 寻卡，仅计算一次寻卡的时间
		gui.cls_printf("正在测试寻卡速度...".getBytes());
		long startTime = System.currentTimeMillis();
		
		if(rfid_detect(type, uidLen, uid)!=SUCC)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:寻卡失败", Tools.getLineInfo());
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);//退出前关闭场
			return;
		}
		detectTime = detectTime+Tools.getStopTime(startTime);
		// 激活
		if(rfidActive(type, 0, uidLen, uid)!=SUCC)// felica的类型需要处理下
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:激活失败", Tools.getLineInfo());
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);//退出前关闭场
			return;
		}
		// 测试iccrw性能
		gui.cls_printf("正在测试读写速度...".getBytes());
		while(true)
		{
			startTime = System.currentTimeMillis();
			if(rfidApduRw(type, rcvlen, uid)==SUCC)
			{
				count++;//累加成功次数
				fTimeElapsed = fTimeElapsed +Tools.getStopTime(startTime);
				if(fTimeElapsed>nTestTime)// 累计成功超过10秒则退出
					break;
			}
		}
		if(fTimeElapsed>nTestTime)
		{
			LoggerUtil.d("rfidAbility->apduLen="+apduLen+"   count="+count+" time="+fTimeElapsed);
			float apduTime = count*((type==_SMART_t.MIFARE_0||type==_SMART_t.MIFARE_1)?LEN_BLKDATA:apduLen)/fTimeElapsed;
			gui.cls_show_msg1_record(TAG, funcName, mScrTime, "(%s卡)寻卡耗时:%.2fs,每秒读卡%.2f字节", type,detectTime,apduTime);
			/**写入性能值到Excel表格*/
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 2, rfidType, model);
			// APDU读写速度
			excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(apduTime)});
		}
			
		else
			gui.cls_show_msg1_record(TAG, funcName, mScrTime,"line %d:累计成功读写时间不足10秒(fTimeElapsed=%f)\n", Tools.getLineInfo(), fTimeElapsed);
		
		// 关闭场
		if(rfidDeactive(type, 0)!=SUCC)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:关闭场失败", Tools.getLineInfo());
			return;
		}
	}

	final int DATA_SIZE = 1*1024;
	public void secAbility(ExcelUtils excelUtils,String model,boolean isAuto)
	{
		/*String key_len_limit = "0";// 默认值 是0，A5平台无此文件
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth))
		{
	        String info = "";
	        info = LinuxCmd.readDevNode("/system/SecureModule/sec.conf");
            if (!info.equals("")) {
                int index = info.indexOf("key_len_limit = ");
                int length = "key_len_limit = ".length();
                key_len_limit = info.substring(index+length,index+length+1);
                LoggerUtil.v("sec.conf->key_len_limit = "+key_len_limit);
            }
		}
		boolean isSingle = true;
		if(key_len_limit.equals("1")||GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)
			isSingle=false;*/
		if(isAuto)
		{
			desTest(excelUtils,model,false);
			dukptTest(excelUtils,model);
			sm4SecTest(excelUtils,model);
//			SM4_Alg_Test(excelUtils,model);
			aesTest(excelUtils,model);
			rsaTest(excelUtils,model);
		}
		else
		{
			int nkeyIn = gui.cls_show_msg("安全性能\n0.DES性能\n1.dukpt性能\n2.SM4性能\n3.aes性能\n4.rsa性能\n");
			switch (nkeyIn) {
			case '0':
				desTest(excelUtils, model, false);
				break;
				
			case '1':
				dukptTest(excelUtils, model);
				break;
				
			case '2':
				sm4SecTest(excelUtils, model);
				break;
				
			case '3':
				aesTest(excelUtils, model);
				break;
				
			case '4':
				rsaTest(excelUtils, model);
				break;

			default:
				break;
			}
		}

		// 测试后置，擦除密钥
		JniNdk.JNI_Sec_KeyErase();
		
	}
	
	private void desTest(ExcelUtils excelUtils,String model,boolean isSingle)
	{
		String funcName="desTest";
		int bak=200;
		int ret=-1;
		byte[] buf1_tmp = ISOUtils.hex2byte("3672c2bc7f17f29c65873586bc7f17f23672c2bc7f17f29c");
		byte[] buf1_in = ISOUtils.hex2byte("313131313131313131313131313131313131313131313131");
		byte[] buf2_tak = ISOUtils.hex2byte("ABABABABABABABABABABABABABABABABABABABABABABABAB");
		byte[] buf3_in = ISOUtils.hex2byte("1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B");
		byte[] udesin = new byte[DATA_SIZE];
		byte[] udesout = new byte[DATA_SIZE];
		
		for (int i = 0; i < udesin.length; i++) {
			udesin[i] = (byte) (Math.random()*255);
		}
		
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		// tdk1Time=0,ecbEn=0,tdes1=0,tdes2=0,tdes3=0,
		float tmkTime=0,tdk2Time=0,tdk3Time=0,ecbEn=0,cbcEn=0,x99Time=0,x919Time=0,m9609Time = 0,ecbTime=0;
		int succ1 = 0,succ2 = 0,succ3 = 0,succ4 = 0,succ5=0,succ6=0,succ7=0,succ8=0,succ9=0,succ10=0,succ11=0,succ12=0,succ13=0,succ14=0;
		String tmkTimeStr,tdk1TimeStr="NA",tdk2TimeStr,tdk3TimeStr,ecbEnStr,cbcEnStr,x99TimeStr,x919TimeStr,m9609TimeStr,ecbTimeStr;
		
		// 测试前置，擦除密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		// 明文安装密钥
		gui.cls_printf("计算NDK_SecLoadKey明文装载执行时间中...".getBytes());
		secKcvInfo.nCheckMode=0;
		secKcvInfo.nLen=4;
		long startTime;
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)0, (byte)1, 24, buf1_tmp, secKcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				continue;
			}
			succ1++;
			tmkTime = tmkTime+(System.currentTimeMillis()-startTime);
		}
		if(succ1==0)
			tmkTimeStr="err";
		else
			tmkTimeStr = digFlo(tmkTime*1.0f/succ1);
		
		/*// 单倍长,Forth和巴西固件不支持8字节密钥，8字节密钥不安全，后续会慢慢废弃
		if(isSingle)
		{
			gui.cls_printf("计算NDK_SecLoadKey装载单倍长TDK执行时间中...".getBytes());
			
			for (int i = 0; i < bak; i++) {
				startTime = System.currentTimeMillis();
				if((ret=JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)1, (byte)4, 
						8, buf1_in, secKcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
					continue;
				}
				tdk1Time = tdk1Time+(System.currentTimeMillis()-startTime);
				succ2++;
			}
			if(succ2==0)
				tdk1TimeStr="err";
			else
				tdk1TimeStr = digFlo(tdk1Time*1.0f/bak);
		}*/

		// 双倍长
		gui.cls_printf("计算NDK_SecLoadKey装载双倍长TDK执行时间中...".getBytes());
		
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if((ret=JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)1, (byte)5, 
					16, buf1_in, secKcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				continue;
			}
			tdk2Time = tdk2Time+(System.currentTimeMillis()-startTime);
			succ3++;
		}
		if(succ3==0)
			tdk2TimeStr="err";
		else
			tdk2TimeStr = digFlo(tdk2Time*1.0f/bak);
		// 三倍长
		gui.cls_printf("计算NDK_SecLoadKey装载三倍长TDK执行时间中...".getBytes());
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if((ret=JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)1, (byte)6, 
					24, buf3_in, secKcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				continue;
			}
			tdk3Time = tdk3Time+(System.currentTimeMillis()-startTime);
			succ4++;
		}
		if(succ4==0)
			tdk3TimeStr = "err";
		else
			tdk3TimeStr = digFlo(tdk3Time*1.0f/bak);
		
	/*	if(isSingle)//0为不限制，1为限制
		{
			// 加解密测试次数为200次
			gui.cls_printf("8字节密钥ECB模式NDK_SecCalcDes加密执行中...".getBytes());
			Arrays.fill(udesin, (byte)0x20);
			for (int i = 0; i < bak; i++) {
				startTime = System.currentTimeMillis();
				if((ret=JniNdk.JNI_Sec_CalcDes((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)4, udesin, (byte)8, udesout,(byte) (0|(1<<1))))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s加密测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
					continue;
				}
				ecbEn = ecbEn+Tools.getStopTime(startTime);
				succ5++;
			}
			if(succ5==0)
				ecbEnStr="err";
			else
				ecbEnStr = digFlo(succ5*1.0f/ecbEn);
		}*/
		
		gui.cls_printf("16字节密钥ECB模式计算2K数据的NDK_SecCalcDes加密执行中...".getBytes());
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if((ret=JniNdk.JNI_Sec_CalcDes((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)5, udesin, DATA_SIZE, udesout,(byte) (0|(2<<1))))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次->%s加密测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,ret);
				continue;
			}
			ecbEn = ecbEn+Tools.getStopTime(startTime);
			succ6++;
		}
		if(succ6==0)
			ecbEnStr = "err";
		else
			ecbEnStr = digFlo(succ6*1.0f/ecbEn);
		
		
		gui.cls_printf("24字节密钥CBC模式计算2K数据的NDK_SecCalcDesCBC加密执行中...".getBytes());
		byte[] iv = ISOUtils.hex2byte("1122334455667788");
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if((ret=JniNdk.JNI_Sec_CalcDesCBC((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(),(byte)6, iv,8,udesin, DATA_SIZE, udesout,(byte) (0|(2<<1))))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次->%s加密测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,ret);
				continue;
			}
			cbcEn = cbcEn+Tools.getStopTime(startTime);
			succ7++;
		}
		if(succ7==0)
			cbcEnStr="err";
		else
			cbcEnStr = digFlo(succ7*1.0f/cbcEn);
		
		/*bak=10000;
		// （T）Des加密
		gui.cls_printf("NDK_AlgTDes使用8字节密钥软加密执行中...".getBytes());
		Arrays.fill(key8, (byte)0x11);
		
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_Alg_TDes(udesin, udesout, key8, 8, 0)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s加密测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			tdes1 += Tools.getStopTime(startTime);
			succ8++;
		}
		if(succ8==0)
			tdes1Str="err";
		else
			tdes1Str=digFlo(succ8*1.0f/tdes1);
		
		
		gui.cls_printf("NDK_AlgTDes使用16字节密钥软加密执行中...".getBytes());
		Arrays.fill(key16, (byte)0x11);
		
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_Alg_TDes(udesin, udesout, key16, 16, 0)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s加密测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			tdes2 += Tools.getStopTime(startTime);
			succ9++;
		}
		if(succ9==0)
			tdes2Str="err";
		else
			tdes2Str=digFlo(succ9*1.0f/tdes2);
		
		gui.cls_printf("NDK_AlgTDes使用24字节密钥软加密执行中...".getBytes());
		Arrays.fill(key24, (byte)0x11);
		for (int i = 0; i < bak; i++) {
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_Alg_TDes(udesin, udesout, key24, 24, 0)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s加密测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			tdes3 += Tools.getStopTime(startTime);
			succ10++;
		}
		if(succ10==0)
			tdes3Str="err";
		else
			tdes3Str=digFlo(succ10*1.0f/tdes3);*/
		
		bak=200;
		// 安装TAK，Id=2
		if((ret=JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal(), (byte)1, (byte)2, 16, buf2_tak, secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		
		byte[] szDataIn = new byte[DATA_SIZE];
		byte[] szMac = new byte[16];
		Arrays.fill(szDataIn, (byte)0x20);
		// 16字节数据MAC_9606运算性能
		gui.cls_printf("2K数据MAC_9606运算执行中...".getBytes());
		for (int j = 0; j < bak; j++) {
			startTime =System.currentTimeMillis();
			if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, DATA_SIZE, szMac, (byte)EM_SEC_MAC.SEC_MAC_9606.ordinal())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%sSEC_MAC_9606测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			m9609Time = m9609Time+Tools.getStopTime(startTime);
			succ11++;
		}
		if(succ11==0)
			m9609TimeStr="err";
		else
			m9609TimeStr=digFlo(succ11*1.0f/m9609Time);
		
		// 16字节数据MAC_ECB运算耗时
		gui.cls_printf("2K数据MAC_ECB运算执行中...".getBytes());
		for (int j = 0; j < bak; j++) {
			startTime =System.currentTimeMillis();
			if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, DATA_SIZE, szMac, (byte)EM_SEC_MAC.SEC_MAC_ECB.ordinal())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%sSEC_MAC_ECB测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			ecbTime = ecbTime+Tools.getStopTime(startTime);
			succ12++;
		}
		if(succ12==0)
			ecbTimeStr="err";
		else
			ecbTimeStr=digFlo(succ12*1.0f/ecbTime);
		
		
		// 16字节数据MAC_X919运算耗时
		gui.cls_printf("2K数据MAC_X919运算执行中...".getBytes());
		for (int j = 0; j < bak; j++) {
			startTime =System.currentTimeMillis();
			if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, DATA_SIZE, szMac, (byte)EM_SEC_MAC.SEC_MAC_X919.ordinal())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%sSEC_MAC_9606测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			x919Time =x919Time+Tools.getStopTime(startTime);
			succ13++;
		}
		if(succ13==0)
			x919TimeStr="err";
		else
			x919TimeStr=digFlo(succ13*1.0f/x919Time);
		
		// 16字节数据MAC_X99运算耗时
		gui.cls_printf("2K数据MAC_X99运算执行中...".getBytes());
		for (int j = 0; j < bak; j++) {
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, DATA_SIZE, szMac, (byte)EM_SEC_MAC.SEC_MAC_X99.ordinal())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%sSEC_MAC_X99测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			x99Time = x99Time+Tools.getStopTime(startTime);
			succ14++;
		}
		if(succ14==0)
			x99TimeStr="err";
		else
			x99TimeStr=digFlo(succ14*1.0f/x99Time);
		
		
		/**写入DES性能值到Excel表格*/
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "DES", model);/**从第一列的值中寻找*/
		String[] datas={tmkTimeStr,tdk1TimeStr,tdk2TimeStr,tdk3TimeStr,
				ecbEnStr,cbcEnStr,m9609TimeStr,ecbTimeStr,x919TimeStr,x99TimeStr};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		LoggerUtil.e(funcName+"->tdes="+succ8+"|||"+succ9+"|||"+succ10);
//		LoggerUtil.e(funcName+"->tdes="+tdes1+"|||"+tdes2+"|||"+tdes3);
		
//		LoggerUtil.d("des1="+succ8/tdes1);
		// 装载24字节TMK耗时
		// 装载16字节TDK耗时
		// 装载24字节TDK耗时
		// 8字节密钥ECB模式计算8字节数据的DES加密耗时
		// 16字节密钥CBC模式计算8字节数据的DES加密耗时
		// 24字节密钥空模式计算8字节数据的DES加密耗时
		// NDK_AlgTDes:计算32字节数据与8字节密钥Des计算的耗时
		// NDK_AlgTDes:计算32字节数据与16字节密钥Des计算的耗时
		// NDK_AlgTDes:计算32字节数据与24字节密钥Des计算的耗时
		// 16字节数据MAC_9606运算耗
		// 16字节数据MAC_ECB运算耗时
		// 16字节MAC_X919运算耗时
		// 16字节MAC_X99运算耗时
		
		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"TMK装载性能:%sms,双倍长TDK装载性能:%sms,三倍长TDK装载性能:%sms\n" +
				"ECB模式加密2K数据性能:%s次/s,CBC模式加密2K数据性能:%s次/s\n" +
				"2K数据MAC_9606运算性能:%s次/s,2K数据MAC_ECB运算性能:%s次/s,2K数据MAC_X919运算性能:%s次/s,2K数据MAC_X99运算性能:%s次/s\n", 
				tmkTimeStr,tdk2TimeStr,tdk3TimeStr,ecbEnStr,cbcEnStr,m9609TimeStr,ecbTimeStr,x919TimeStr,x99TimeStr);
	}
	
	private void dukptTest(ExcelUtils excelUtils,String model)
	{
		String funcName="dukptTest";
		
		byte[] ksnIn = new byte[17];
		byte[] udesin = new byte[DATA_SIZE];
		byte[] udesOut = new byte[DATA_SIZE];
		byte[] keyValueIn = new byte[17];
		byte[] sIV = new byte[9];
		int bak =20;
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		float tikTime,ecbTime,cbcTime;
		
		if(JniNdk.JNI_Sec_KeyErase()!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		Arrays.fill(ksnIn, (byte)0xff);
		Arrays.fill(keyValueIn, (byte)0x11);
		secKcvInfo.nCheckMode=0;
		// 明文方式安装
		long startTime = System.currentTimeMillis();
		if(JniNdk.JNI_Sec_LoadTIK((byte)2, (byte)0, (byte)16, keyValueIn, ksnIn, secKcvInfo)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		tikTime = (System.currentTimeMillis()-startTime)*1.0f/bak;
		
		
		for (int i = 0; i < udesin.length; i++) {
			udesin[i] = (byte) (Math.random()*255);
		}
		// 1.以 SEC_DES_ENCRYPT|SEC_DES_KEYLEN_16|SEC_DES_ECBMODE模式, 16字节数据输入,TDK1
		gui.cls_printf("ECB模式计算2K数据NDK_SecCalcDesDukpt加密执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int j = 0; j < bak; j++) {
			if(JniNdk.JNI_Sec_CalcDesDukpt((byte)2, (byte)4, sIV, DATA_SIZE, udesin, udesOut, ksnIn, (byte)(0|(2<<1)|(0<<3)))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次%s测试失败", Tools.getLineInfo(),TESTITEM,j);
				return;
			}
		}
		ecbTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		// 1.以 SEC_DES_ENCRYPT|SEC_DES_KEYLEN_16|SEC_DES_ECBMODE模式, 16字节数据输入,TDK1
		gui.cls_printf("CBC模式计算2K数据NDK_SecCalcDesDukpt加密执行中...".getBytes());
		sIV = ISOUtils.hex2byte("1122334455667788");
		startTime = System.currentTimeMillis();
		for (int j = 0; j < bak; j++) {
			if(JniNdk.JNI_Sec_CalcDesDukpt((byte)2, (byte)4, sIV, DATA_SIZE, udesin, udesOut, ksnIn, (byte)(0|(2<<1)|(0<<3)))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次%s测试失败", Tools.getLineInfo(),TESTITEM,j);
				return;
			}
		}
		cbcTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		/*gui.cls_printf("空模式计算2K数据NDK_SecCalcDesDukpt加密执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int j = 0; j < bak; j++) {
			if(JniNdk.JNI_Sec_CalcDesDukpt((byte)2, (byte)4, sIV, DATA_SIZE, udesin, udesOut, ksnIn, (byte)(0|(3<<1)))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次%s测试失败", Tools.getLineInfo(),TESTITEM,j);
				return;
			}
		}
		noTime = (float) (bak*1.0/Tools.getStopTime(startTime));*/
		
		/**写入性能值到Excel表格*/
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 2, "DUKPT", model);
		String datas[] = {digFlo(tikTime),digFlo(ecbTime),digFlo(cbcTime)};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		// 装载16字节DUKPT性能
		// NDK_SecCalcDesDukpt:8字节密钥ECB模式计算8字节数据的加密耗时
		// NDK_SecCalcDesDukpt:16字节密钥CBC模式计算16字节数据的加密耗时
		// NDK_SecCalcDesDukpt:24字节密钥空模式计算24字节数据的加密耗时
		
		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"装载16字节DUKPT性能:%.3fms/次" +
				"ECB模式计算2K数据NDK_SecCalcDesDukpt加密性能:%.3f次/s,CBC模式计算2K数据NDK_SecCalcDesDukpt加密性能:%.3f次/s" ,tikTime,ecbTime,cbcTime);
		
	}
	
	/*private void SM4_Alg_Test(ExcelUtils excelUtils,String model)
	{
		String funcName="SM4_Alg_Test";
		int bak = 20;
		byte[] udesin = new byte[16];
		byte[] udesout = new byte[16];
		byte[] key16 = new byte[16];
		byte[] CBC_vector = ISOUtils.hex2byte("2c5c0e042c8f06d233ffd0258365df49");
		float ecbTime=0.0f,cbcTime=0.0f;
		
		bak = 10000;
		gui.cls_printf("NDK_AlgSM4Compute使用16字节密钥ECB模式软加密执行中...".getBytes());
		Arrays.fill(key16, (byte)0x11);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Alg_SM4Compute(key16, null, (byte)16, udesin, udesout, (byte) 0*//**ALG_SM4_ENCRYPT_ECB*//*)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		ecbTime = (System.currentTimeMillis()-startTime)*1.0f/bak;
		
		gui.cls_printf("NDK_AlgSM4Compute使用16字节密钥CBC模式软加密执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Alg_SM4Compute(key16, CBC_vector, (byte)16, udesin, udesout, (byte) 2*//**ALG_SM4_ENCRYPT_CBC*//*)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		cbcTime = (System.currentTimeMillis()-startTime)*1.0f/bak;
		gui.cls_show_msg1_record(TAG, funcName, mScrTime, "NDK_AlgSM4Compute使用16字节密钥,ECB模式软加密性能:%.3fms/次,CBC模式软加密性能:%.3fms/次", ecbTime,cbcTime);
	}*/
	
	public void aesTest(ExcelUtils excelUtils,String model)
	{
		int ret=-1;
		String funcName = "aesTest";
		int bak =20;
		byte[] buf1_tmp =ISOUtils.hex2byte("2121212121212121212121212121212124242424242424242525252525252525");
		byte[] buf1_in = ISOUtils.hex2byte("F5F6D151AD4FCFE96C2FDFF66CAA0242F5F6D151AD4FCFE96C2FDFF66CAA0242");
		byte[] buf3_in = ISOUtils.hex2byte("1F2F3F4F5F6F7F8F1F2F3F4F5F6F7F8F1F2F3F4F5F6F7F8F");
		byte[] buf_tak = ISOUtils.hex2byte("FD5E76D2F07EBD04F0AA2D966781C673");
		byte[] udesin = new byte[DATA_SIZE];
		byte[] udesout = new byte[DATA_SIZE];
		float tmkTime = 0.0f,tdk1Time=0.0f,tdk2Time=0.0f,tdk3Time=0.0f,ecbTime=0.0f,cbcTime,macTime=0.0f;
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		
		for (int i = 0; i < udesin.length; i++) {
			udesin[i] = (byte) (Math.random()*255);
		}
		
		// 测试前置，擦除所有密钥
		if(JniNdk.JNI_Sec_KeyErase()!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		// 明文安装TMK密钥
		gui.cls_printf("计算NDK_SecLoadKey明文装载执行时间中...".getBytes());
		secKcvInfo.nCheckMode=0;
		secKcvInfo.nLen=4;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_LoadKey((byte)(0|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), 
					(byte)0, (byte)1, 32, buf1_tmp, secKcvInfo)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		tmkTime = (float) ((System.currentTimeMillis()-startTime)*1.0/bak);
		
		// 密文安装双倍长TDK
		gui.cls_printf("计算NDK_SecLoadKey装载密文双倍长TDK执行时间中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()),
					(byte)1, (byte)5, 16, buf1_in, secKcvInfo)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		tdk1Time = (float) ((System.currentTimeMillis()-startTime)*1.0/bak);
		
		// 安装三倍长TDK
		gui.cls_printf("计算NDK_SecLoadKey装载密文三倍长TDK执行时间中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if((ret=JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()),
					(byte)1, (byte)6, 24, buf3_in, secKcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
		}
		tdk2Time = (float) ((System.currentTimeMillis()-startTime)*1.0/bak);
		
		// 安装32字节TDK
		gui.cls_printf("计算NDK_SecLoadKey装载密文三倍长TDK执行时间中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()),
					(byte)1, (byte)7, 32, buf1_in, secKcvInfo)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		tdk3Time = (float) ((System.currentTimeMillis()-startTime)*1.0/bak);
		
		// ECB加解密测试次数为200次
		bak =200;
		gui.cls_printf("ECB加密模式NDK_SecCalcDes加密2K数据执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_CalcDes((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()), (byte)5, udesin, DATA_SIZE, udesout, (byte)EM_SEC_DES.SEC_AES_ENCRYPT.secdes()/**SEC_AES_ENCRYPT*/)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		ecbTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		// CBC加解密测试次数为200次
		bak =200;
		byte[] iv = ISOUtils.hex2byte("11223344556677889900112233445566");
		gui.cls_printf("CBC加密模式NDK_SecCalcDes加密2K数据执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if((ret=JniNdk.JNI_Sec_CalcDesCBC((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()), (byte)5, iv,16,udesin, DATA_SIZE, udesout, (byte)EM_SEC_DES.SEC_AES_ENCRYPT.secdes())/**SEC_AES_ENCRYPT*/)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
		}
		cbcTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		// 密文安装双倍长TAK
		if((ret=JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()),
				(byte)1, (byte)3, 16, buf_tak, secKcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		byte[] szDataIn = new byte[256];
		byte[] szMac = new byte[16];
		Arrays.fill(szDataIn, (byte)0x20);
		gui.cls_printf("MAC_AES计算2K数据执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int j = 0; j < bak; j++) {
			if(JniNdk.JNI_Sec_GetMac((byte)3, szDataIn, DATA_SIZE, szMac, (byte)5)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		macTime = (float) (bak*1.0/Tools.getStopTime(startTime));

		/**写入性能值到Excel表格*/
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "AES", model);
		String datas[] = {digFlo(tmkTime),digFlo(tdk1Time),digFlo(tdk2Time),digFlo(tdk3Time),digFlo(ecbTime),digFlo(cbcTime),digFlo(macTime)};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		// 装载32字节TMK耗时
		// 装载24字节TDK耗时
		// 装载32字节TDK耗时
		// 16字节密钥ECB模式计算16字节数据的AES加密耗时
		// 16字节密钥CCB模式计算16字节数据的AES加密耗时
		// 16字节密钥CFB模式计算16字节数据的AES加密耗时
		// 16字节密钥OFB模式计算16字节数据的AES加密耗时
		// 16字节密钥ECB模式计算16字节数据软加密耗时
		// 16字节密钥CBC模式计算16字节数据软加密耗时
		gui.cls_show_msg1_record(TAG, funcName, mScrTime, "NDK_SecLoadKey算法类型为AES情况下," +
				"TMK装载性能:%.2fms,双倍长TDK装载性能:%.2fms,24字节TDK装载性能:%.2fms," +
				"32字节TDK装载性能:%.2fms,ECB模式AES硬加密2K数据性能:%.2f次/s,CBC模式AES硬加密2K数据性能:%.2f次/s,MAC_AES运算2K数据耗时:%.2f次/s", tmkTime,tdk1Time,tdk2Time,tdk3Time,ecbTime,cbcTime,macTime);
	}
	
	public void sm4SecTest(ExcelUtils excelUtils,String model)
	{
		// SM4是国内的，海外不支持
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)
		{
			gui.cls_show_msg1(mScrTime, "海外固件不支持SM4");
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "SM4", model);
			String datas[] = {"NA","NA","NA","NA","NA"};
			excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
			return;
		}
		String funcName = "sm4SecTest";
		byte[] buf1_tmp = ISOUtils.hex2byte("31313131313131313131313131313131");
		byte[] buf1_in = ISOUtils.hex2byte("90929CA41B8DD3B287090DD56F3C388D");
		byte[] buf_tak= ISOUtils.hex2byte("1626E401BFF11B1B64F74D2139EF27FA");
		byte[] udesin = new byte[DATA_SIZE];
		byte[] udesout = new byte[DATA_SIZE];
		int bak=20;
		float tmkTime,tdk2Time,sm4EcbEnTime,sm4CbcEnTime,sm4Time,unionTime;
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		
		for (int i = 0; i < udesin.length; i++) {
			udesin[i] = (byte) (Math.random()*255);
		}
		
		// 测试前置，查出密钥
		if(JniNdk.JNI_Sec_KeyErase()!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		// 安装TMK密钥
		gui.cls_printf("计算NDK_SecLoadKey明文装载执行时间中...".getBytes());
		secKcvInfo.nCheckMode=0;
		secKcvInfo.nLen=4;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_LoadKey((byte)(0|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), 
					(byte)0, (byte)1, 16, buf1_tmp, secKcvInfo)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		tmkTime = (float) ((System.currentTimeMillis()-startTime)*1.0/bak);
		// 密文安装TDK密钥
		gui.cls_printf("计算NDK_SecLoadKey装载密文双倍长TDK执行时间中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), 
					(byte)1, (byte)5, 16, buf1_in, secKcvInfo)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		tdk2Time = (float) ((System.currentTimeMillis()-startTime)*1.0/bak);
		// ECB加解密测试次数为200次
		bak=200;
		gui.cls_printf("(SM4)ECB模式NDK_SecCalcDes加密2K数据执行中...".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_CalcDes((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)5, udesin, DATA_SIZE, udesout, (byte)EM_SEC_DES.SEC_SM4_ENCRYPT.secdes())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s加密测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		sm4EcbEnTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		// CBC加解密测试次数为200次
		gui.cls_printf("(SM4)CBC加密模式NDK_SecCalcDes加密2K数据执行中...".getBytes());
		byte[] iv = ISOUtils.hex2byte("1122334455667788");
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_CalcDesCBC((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)5, iv,8,udesin, DATA_SIZE, udesout, (byte)EM_SEC_DES.SEC_SM4_ENCRYPT.secdes())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s加密测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		sm4CbcEnTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		// 16字节MAC_SM4运算耗时
		gui.cls_printf("2K数据MAC_SM4运算执行中...".getBytes());
		byte[] szDataIn = new byte[DATA_SIZE];
		byte[] szMac = new byte[16];
		Arrays.fill(szDataIn, (byte)0x20);
		if(JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), 
				(byte)1, (byte)2, 16, buf_tak, secKcvInfo)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, DATA_SIZE, szMac, (byte)EM_SEC_MAC.SEC_MAC_SM4.ordinal())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		sm4Time = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		// 16字节MAC_SM4_UNIONPAY运算耗时
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, DATA_SIZE, szMac, (byte)6)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		unionTime = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		/**写入性能值到Excel表*/
		// 装载16字节TMK耗时
		// 装载16字节TDK耗时
		// 16字节密钥SM4模式计算16字节数据的SM4加密耗时
		// 16字节MAC_SM4运算耗时
		// 16字节MAC_SM4_UNIONPAY运算耗时
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "SM4", model);
		String datas[] = {digFlo(tmkTime),digFlo(tdk2Time),digFlo(sm4EcbEnTime),digFlo(sm4CbcEnTime),digFlo(sm4Time),digFlo(unionTime)};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		
		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"NDK_SecLoadKey算法类型为SM4情况下,TMK统计值每次时间:%.2fms,双倍长TDK统计值每次时间:%.2fms," +
				"2K数据ECB模式SM4硬加密性能:%.3f次/s,2K数据ECB模式SM4硬加密性能:%.3f次/s,2K数据MAC_SM4运算性能:%.3f次/s,2K数据MAC_SM4_UNIONPAY运算性能:%.3f次/s", 
				tmkTime,tdk2Time,sm4EcbEnTime,sm4CbcEnTime,sm4Time,unionTime);
	}
	
	private void shaTest(ExcelUtils excelUtils,String model)
	{
		String funcName = "shaTest";
		byte[] Testsha = new byte[1024];
		byte[] resultSha = new byte[64];
		int bak=2000;
		float sha1Time,sha2Time,sha3Time;
		
		// SHA1/SHA256/SHA512同-1K数据
		for (int i = 0; i < 1024; i++) {
			Testsha[i] = (byte)(Math.random()*256);
		}
		gui.cls_printf("SHA1执行中".getBytes());
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) {
			if(JniNdk.JNI_AlgSHA1(Testsha, 1024, resultSha)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		sha1Time = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		gui.cls_printf("SHA256执行中".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 2000; i++) {
			if(JniNdk.JNI_AlgSHA256(Testsha, 1024, resultSha)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		sha2Time = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		gui.cls_printf("SHA512执行中".getBytes());
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 2000; i++) {
			if(JniNdk.JNI_AlgSHA512(Testsha, 1024, resultSha)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		sha3Time = (float) (bak*1.0/Tools.getStopTime(startTime));
		
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 2, "SHA1", model);
		/**写入性能值到Excel表格*/
		// 1024字节SHA1计算耗时
		// 1024字节SHA512计算耗时
		String datas[] = {digFlo(sha1Time),digFlo(sha2Time),digFlo(sha3Time)};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);

		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"NDK_AlgSHA1性能%.3f次/s,NDK_AlgSHA256性能%.3f次/s,NDK_AlgSHA512性能%.3f次/s", 
				sha1Time,sha2Time,sha3Time);
	}
	
	public void rsaTest(ExcelUtils excelUtils,String model)
	{
		String funcName="rsaTest";
		int bak =200;
		int ret=-1;
		
		float rsa1Time=0,rsa2Time=0,r_1024En = 0,r_1024De=0,r_2048En=0,r_2048De=0;
		int succ1=0,succ2=0,succ3=0,succ4=0,succ5=0,succ6=0;
		String rsa1Str,rsa2Str,r_1024EnStr,r_1024DeStr,r_2048EnStr,r_2048DeStr;
		
//		byte[] sDataIn = new byte[512];
//		byte[] sDataOut = new byte[512];
//		byte[] sTempOut=new byte[512];
//		byte[] sTempIn129=new byte[129];
//		byte[] sTempIn257 = new byte[257];
		RsaPrivateKey[] priKey1024 = new RsaPrivateKey[1];
		RsaPublicKey[] pubKey1024 = new RsaPublicKey[1];
		
		RsaPrivateKey[] priKey2048 = new RsaPrivateKey[1];
		RsaPublicKey[] pubKey2048 = new RsaPublicKey[1];
		
		// 测试前置：擦除所有密钥
		if(JniNdk.JNI_Sec_KeyErase()!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		gui.cls_printf("计算生成RSA1024对执行时间中...".getBytes());
		long startTime;
		for (int j = 0; j < bak; j++) {
			gui.cls_printf(String.format("第%d次生成RSA1024公私钥中,请耐心等待", j+1).getBytes());
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_AlgRSAKeyPairGen(1024, 0x10001, pubKey1024, priKey1024)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			succ1++;
			rsa1Time = rsa1Time+(System.currentTimeMillis()-startTime);
		}
		if(succ1==0)
			rsa1Str = "err";
		else
			rsa1Str = digFlo(rsa1Time/succ1);
		
		gui.cls_printf("计算生成RSA2048对执行时间中...".getBytes());
		for (int j = 0; j < bak; j++) {
			gui.cls_printf(String.format("第%d次生成RSA2048公私钥中,请耐心等待", j+1).getBytes());
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_AlgRSAKeyPairGen(2048, 0x10001, pubKey2048, priKey2048)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
			rsa2Time = rsa2Time+(System.currentTimeMillis()-startTime);
			succ2++;
			LoggerUtil.e("ras2Time:"+rsa2Time);
		}
		if(succ2==0)
			rsa2Str = "err";
		else
			rsa2Str= digFlo(rsa2Time/succ2);
		
		
		/*// 软件RSA加密及解密(加密是指使用Exp为3或0x10001的钥(Kpub)进行运算,解密是指使用Kpub对应的Kpri进行运算),alg的设备不支持
		// RSA1024长运算
		bak=10000;
		gui.cls_printf("RSA1024软加密执行中...".getBytes());
		Arrays.fill(sDataIn, (byte)'a');
		System.arraycopy(sTempIn129, 0,sDataIn, 0, 128);
		sTempIn129[0] = 0;
		sTempIn129[1] = 0;
		for (int j = 0; j < bak; j++) {
			Arrays.fill(sDataOut, (byte)0x00);
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_AlgRSARecover(pubKey1024[0].modulus, pubKey1024[0].bits/8, 
					pubKey1024[0].exponent, sTempIn129, sDataOut)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
			r_1024En = r_1024En+Tools.getStopTime(startTime);
			succ3++;
		}
		LoggerUtil.e(funcName+"->r_1024En="+r_1024En);
		if(succ3==0)
			r_1024EnStr = "err";
		else
			r_1024EnStr = digFlo((float) (succ3*1.0/r_1024En));
		
		gui.cls_printf("RSA1024软解密执行中...".getBytes());
		for (int j = 0; j < bak; j++) {
			Arrays.fill(sTempOut, (byte)0x00);
			startTime = System.currentTimeMillis();
			if((ret=JniNdk.JNI_AlgRSARecover(priKey1024[0].modulus, priKey1024[0].bits/8, priKey1024[0].exponent, sDataOut, sTempOut))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			r_1024De = r_1024De+Tools.getStopTime(startTime);
			succ4++;
		}
		LoggerUtil.e(funcName+"->r_1024De="+r_1024De);
		if(succ4==0)
			r_1024DeStr="err";
		else
			r_1024DeStr = digFlo((float) (succ4*1.0/r_1024De));
		if(Tools.memcmp(sTempOut, sTempIn129, 128)==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		// RSA2048长运算
		gui.cls_printf("RSA2048软加密执行中...".getBytes());
		System.arraycopy(sTempIn257, 0,sDataIn, 0, 256);
		sTempIn257[0] = 0;
		sTempIn257[1] = 0;
		
		for (int j = 0; j < bak; j++) {
			Arrays.fill(sDataOut, (byte)0x00);
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_AlgRSARecover(pubKey2048[0].modulus, pubKey2048[0].bits/8, 
					pubKey2048[0].exponent, sTempIn257, sDataOut)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
			r_2048En = r_2048En+Tools.getStopTime(startTime);
			succ5++;
		}
		
//		LoggerUtil.e(funcName+"->r_2048En="+r_2048En);
		if (succ5==0) 
			r_2048EnStr = "err";
		else
			r_2048EnStr = digFlo((float) (succ5*1.0/r_2048En));
		
		gui.cls_printf("RSA2048软解密执行中...".getBytes());
		for (int j = 0; j < bak; j++) {
			Arrays.fill(sTempOut, (byte)0x00);
			startTime = System.currentTimeMillis();
			if(JniNdk.JNI_AlgRSARecover(priKey2048[0].modulus, priKey2048[0].bits/8, 
					priKey2048[0].exponent, sDataOut, sTempOut)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
			succ6++;
			r_2048De = r_2048De+Tools.getStopTime(startTime);
		}
//		LoggerUtil.e(funcName+"->r_2048De="+r_2048De);
		if(succ6==0)
			r_2048DeStr="err";
		else
			r_2048DeStr = digFlo((float) (succ6*1.0/r_2048De));
		if(Tools.memcmp(sTempOut, sTempIn257, 256)==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}*/
		
		/**写入性能值到Excel表格*/
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 2, "RSA", model);
		// 生成高端RSA512密钥对的耗时，不支持
		// 生成RSA1024密钥对的耗时
		// 生成2048密钥对的耗时
		// 装载RSA512长公钥的耗时，不支持
		// 装载RSA512长私钥的耗时，不支持
		// 装载RSA1024长公钥的耗时，不支持
		// 装载RSA1024长私钥的耗时，不支持
		// 装载RSA2048长公钥的耗时，不支持
		// 装载RSA2048长私钥的耗时，不支持
		// NDK_SecRecover:64字节RSA512加密耗时，不支持
		// NDK_SecRecover:64字节RSA512解密耗时，不支持
		// NDK_SecRecover:128字节RSA512加密耗时，不支持
		// NDK_SecRecover:128字节RSA512解密耗时，不支持
		// NDK_SecRecover:256字节RSA512加密耗时，不支持
		// NDK_SecRecover:256字节RSA512解密耗时，不支持
		// NDK_AlgRSARecover:64字节RSA512加密耗时,不支持
		// NDK_AlgRSARecover:64字节RSA512解密耗时，不支持
		// NDK_AlgRSARecover:128字节RSA1024加密耗时
		// NDK_AlgRSARecover:128字节RSA1024解密耗时
		// NDK_AlgRSARecover:256字节RSA2048加密耗时
		// NDK_AlgRSARecover:256字节RSA2048解密耗时
		String[] datas = {"NA",rsa1Str,rsa2Str,"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA",};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);

		
		gui.cls_show_msg1_record(TAG, funcName, mScrTime, 
				"生成RSA1024密钥对时间(统计值):%sms,生成RSA2048密钥对时间(统计值):%sms\n",rsa1Str,rsa2Str);
	}
	
	// wifi String model, ExcelUtils excelUtils
	public void WifiAbility(ExcelUtils excelUtils,String model,WifiPara wifiPara) 
	{
		String funcName = "WifiAbility";
		long netStartTime, disconnetStartTime, scanStartTime;
		float netEndTime, disconnetEndTime, scanEndTime = 0.0f;
		float nettime = 0.0f, disconnettime = 0.0f, scantime = 0.0f;
		
		float nettimefinish = 0.0f, disconnettimefinish = 0.0f, scantimefinish = 0.0f;
		WifiUtil wifiUtil=WifiUtil.getInstance(myactivity,handler);
		int ret = -1;
		int succ = 0;
		String wifichoose;
		
		if (gui.cls_show_msg("wifi配置为5G还是2.4G？确定[2.4G],其他[5G]") == ENTER) {
			wifichoose = "信号好(2.4G)";
		} else {
			wifichoose = "信号好(5G)";
		}
		Log.d("eric_chen", "当前选择=" + wifichoose);
		// 配置wifi操作
		initLayer();
		g_CycleTime = 20; // 连接 断开 扫描时间
		SocketUtil socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();

		// 计算连接 断开 扫描时间
		// 先连接一次
		if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) {
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetUp失败(ret=%d)",Tools.getLineInfo(), ret);
			layerBase.linkDown(wifiPara, type);
			return;
		}
		if ((ret = layerBase.netDown(socketUtil, wifiPara, sock_t, type)) != SUCC) {
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetDown失败(ret=%d)",Tools.getLineInfo(), ret);
			return;
		}
		wifiUtil.setSsid(wifiPara.getSsid());
		wifiUtil.registWifiConnect();
		int failcount=0;
		while (g_CycleTime > 0) {
			g_CycleTime--;
			// 打开wifi
			if ((ret = layerBase.wifiOpen()) != SUCC) {
				failcount++;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:open失败(ret = %d)", Tools.getLineInfo(), ret);
				continue;
			}
			//清除wifi保存
			wifiUtil.clearAllSavaWifi();
			// 扫描wifi时间计算
			scanStartTime = System.currentTimeMillis();
			while (true) {
				wifiUtil.startScan(wifiPara);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<ScanResult> wifiList = wifiUtil.getWifiList();
				Log.d("eric", "wifi寻找---");
				if (wifiList.size() > 0) {
					// 找到
					if (seekWifi(wifiList, wifiPara,0) == 0) {
						Log.d("eric", "wifi找到---");
						break;
					}
				}
			}
			scanEndTime = Tools.getStopTime(scanStartTime);
			// wifi连接时间
			netStartTime = System.currentTimeMillis();
			Log.d("eric_chen", "netStartTime---"+netStartTime);
			if ((ret = layerBase.netUp(wifiPara, type)) != SUCC) {
				failcount++;
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetUp失败(ret = %d)",Tools.getLineInfo(), ret);
				layerBase.linkDown(wifiPara, type);
				continue;
			}
			netEndTime =  Tools.getStopTime(netStartTime);
			
			// wifi断开时间计算
			disconnetStartTime = System.currentTimeMillis();
			// 链路层断开没有显示耗时 1s
			if ((ret = layerBase.wifiDisconnet()) != SUCC) {
				failcount++;
				layerBase.linkDown(wifiPara, type);
				continue;
			}
			int time=0;
			long oldtime=System.currentTimeMillis();
			while(time<5){
				time=(int) Tools.getStopTime(oldtime);
				if (NetworkUtil.checkNet(myactivity)!=ConnectivityManager.TYPE_WIFI) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (time>=5) {
				failcount++;
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:网络连接",Tools.getLineInfo());
				layerBase.linkDown(wifiPara, type);
				continue;	
			}
			disconnetEndTime = Tools.getStopTime(disconnetStartTime);
			
			if ((ret = layerBase.linkDown(wifiPara, type)) != SUCC) {
				failcount++;
				continue;
			}
			// 每次性能结束后休息1s
			SystemClock.sleep(1000);
			succ++;
			nettime += netEndTime;
			disconnettime += disconnetEndTime;
			scantime += scanEndTime;
		}
		// 性能汇总
		nettimefinish = nettime / succ; // 连接性能
		scantimefinish = scantime / succ; // 扫描性能
		disconnettimefinish = disconnettime / succ; // 断开性能
		//失败超过5次就认为本次性能测试失败
		if (failcount>5) {
			nettimefinish=-1;
			scantimefinish=-1;
			disconnettimefinish=-1;
		}
		
		gui.cls_show_msg("带宽使用iperf工具测试,使用文档在SVN的doc下");
		// 写入excel表格
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, wifichoose, model);
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(nettimefinish),
				digFlo(disconnettimefinish),digFlo(scantimefinish),"iperf工具"});
		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"wifi性能测试数据如下,连接wifi为%s,连接性能为%s,扫描性能为%s,断开性能为%s,"
				, wifiPara.getSsid(),digFlo(nettimefinish),digFlo(scantimefinish),digFlo(disconnettimefinish));
	}
	
	
	// sim卡工具方法
	// 打开关闭飞行模式 广播方式。 实测使用系统权限可以设置
	@SuppressWarnings({ "deprecation", "unused" })
	private void setAirPlaneMode(Context context, boolean enable) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, enable ? 1 : 0);
		} else {
			Settings.Global.putInt(context.getContentResolver(),
					Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
		}
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enable);
		context.sendBroadcast(intent);
	}
	
	public void wlmAbility(ExcelUtils excelUtils,String model,MobilePara mobilePara) {
		String funcName = "wlmAbility";
		boolean mobilestate;
		boolean A5module = false;
		int count1 = 20; // 拨号 挂断次数20次测试性能
		int bak=count1;
		int succ1 = 0;
		long netUpStartTime,netDownStartTime;
		float netUpEndTime = 0.0f,netDownEndTime = 0.0f;
		float netUptime = 0.0f,netDowntime = 0.0f;
		long starttime = 0;
		SettingsManager mSettingsManager = null;
		
		ChangeWireType change = new ChangeWireType(myactivity);
		if (GlobalVariable.gCurPlatVer == Platform_Ver.A5) {
			A5module = true;
		} else {
			A5module = false;
		}
		Log.d("eric_chen", "A5module=" + A5module);
		setWireType(mobilePara);// 获取网络类型
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
			mSettingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		initLayer();
		MobileUtil mobileUtil = MobileUtil.getInstance(myactivity,handler);
		mobilestate = mobileUtil.getMobileDataState(myactivity);
		mobileUtil.closeOther();
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		mobileUtil.setMobileData(myactivity, mobilestate);
		int wlmfailcount=0;
		//获取当前运营商
		String Operatorname=getProvidersName(myactivity);
		//当前网络类型
		String wlmtype=mobilePara.getType() == LinkType.GPRS ? "2G": mobilePara.getType() == LinkType.CDMA ? "3G": "4G";
		while (count1 > 0) {
			count1--;
			if (gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"开始第%d次拨号挂断性能测试(已成功%d次),[取消]退出测试...",bak - count1, succ1) == ESC)
				return;

			netUpStartTime = System.currentTimeMillis();
			if (layerBase.netUp(mobilePara, mobilePara.getType()) != NDK_OK) {
				wlmfailcount++;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次无线拔号失败", Tools.getLineInfo(), bak - count1);
				continue;
			}
			netUpEndTime = Tools.getStopTime(netUpStartTime);
			

			netDownStartTime = System.currentTimeMillis();
			if (layerBase.netDown(socketUtil, mobilePara,mobilePara.getSock_t(), mobilePara.getType()) != NDK_OK) {
				wlmfailcount++;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次无线挂断失败", Tools.getLineInfo(), bak - count1);
				continue;
			}
			netDownEndTime = Tools.getStopTime(netDownStartTime);
			succ1++;
			netUptime = netUptime + netUpEndTime;
			netDowntime = netDownEndTime + netDowntime;
		}
		float netUpAbility = (float) (netUptime / succ1); // 拨号性能值
		float netDownAbility = (float) (netDowntime / succ1); // 挂断性能值
		if (wlmfailcount>5) {
			netUpAbility=-1;
			netDownAbility=-1;
			
		}
		
		float airplaneEndTime;
		float airplanetime = 0;
		boolean Airplaneflag=false;
		LinkType type = mobilePara.getType();
		if (gui.cls_show_msg("是否要测试计算飞行模式到建立网络时间？(需要系统权限，建议手动测试)。确认[是],其他[否]") == ENTER) {
			try {
				setAirPlaneMode(myactivity, true); // 打开飞行模式
				starttime = System.currentTimeMillis();
				setAirPlaneMode(myactivity, false); // 关闭飞行模式
			} catch (Exception e) {
				e.printStackTrace();
				Airplaneflag=true;
			}
			if (layerBase.netUp(mobilePara, type) != NDK_OK) {
				Airplaneflag=true;
				layerBase.linkDown(mobilePara, type);
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			}
			airplaneEndTime = Tools.getStopTime(starttime);
			airplanetime = airplaneEndTime;// 飞行模式性能值
		}//N700支持接口，无需系统权限
		else if(GlobalVariable.currentPlatform==Model_Type.N700) {
			
			if(!mSettingsManager.setAirplaneModeEnabled(true))
			{	
				Airplaneflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开飞行模式失败", Tools.getLineInfo());
			}
			starttime = System.currentTimeMillis();
			if(!mSettingsManager.setAirplaneModeEnabled(false))
			{	
				Airplaneflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:关闭飞行模式失败", Tools.getLineInfo());
			}
			if (layerBase.netUp(mobilePara, type) != NDK_OK) {
				Airplaneflag=true;
				layerBase.linkDown(mobilePara, type);
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			}
			airplaneEndTime = Tools.getStopTime(starttime);
			airplanetime = airplaneEndTime;// 飞行模式性能值
			
		}
		if (Airplaneflag) {
			airplanetime=-1;
		}
		int time = 0;
		gui.cls_show_msg1(2, "下面开始计算3G切换到4G时间---");
		float Threeto4G=0;
		boolean changewireflag=false;
		if (A5module) {
			if (gui.cls_show_msg("是否要测试计算3G切换到4G网络时间？(需要系统权限，建议手动测试)。确认[是],其他[否]") == ENTER) {
			// 先切换到3G
			if (!change.changeWire(21)) {
				changewireflag=true;
				gui.cls_show_msg1(1, "切换3G网络失败，测试失败");
			}
			time = 0;
			long tempTime = System.currentTimeMillis();
			while (time < 30) {
				time = (int) Tools.getStopTime(tempTime);
				if (change.getWire() == 21) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (change.getWire() != 21) {
				changewireflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:切换3G失败", Tools.getLineInfo());
			}
			// 开始计算3G切换到4G性能
			starttime = System.currentTimeMillis();
			if (!change.changeWire(22)) {
				changewireflag=true;
				gui.cls_show_msg1(1, "切换4G网络失败，测试失败");
			}
			time = 0;
			tempTime = System.currentTimeMillis();
			while (time < 30) {
				time = (int) Tools.getStopTime(tempTime);
				if (change.getWire() == 22) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (change.getWire() != 22) {
				changewireflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:切换4G失败", Tools.getLineInfo());
			}
			if (layerBase.netUp(mobilePara, type) != NDK_OK) {
				changewireflag=true;
				layerBase.linkDown(mobilePara, type);
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			}
			Threeto4G = Tools.getStopTime(starttime);
			if (changewireflag) {
				Threeto4G=0;
			}
			}
		} else {
			// 先切换到3G
			if (mSettingsManager.setPreferredNetworkType(3) != true) {
				changewireflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:设置网络失败", Tools.getLineInfo());
			}
			time = 0;
			long tempTime = System.currentTimeMillis();
			while (time < 30) {
				time = (int) Tools.getStopTime(tempTime);
				if ( mSettingsManager.getPreferredNetworkType() == 3) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (mSettingsManager.getPreferredNetworkType() != 3) {
				changewireflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:获取网络错误", Tools.getLineInfo());
			}
			// 开始计算3G切换到4G性能
			starttime = System.currentTimeMillis();
			if (mSettingsManager.setPreferredNetworkType(4) != true) {
				changewireflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:设置网络失败", Tools.getLineInfo());
			}
			time = 0;
			tempTime = System.currentTimeMillis();
			while (time < 30) {
				time = (int) Tools.getStopTime(tempTime);
				if ( mSettingsManager.getPreferredNetworkType() == 4) {
					break;
				}
				SystemClock.sleep(10);
			}

			if (mSettingsManager.getPreferredNetworkType() != 4) {
				changewireflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:获取网络错误", Tools.getLineInfo());
			}
			if (layerBase.netUp(mobilePara, type) != NDK_OK) {
				changewireflag=true;
				layerBase.linkDown(mobilePara, type);
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			}

			Threeto4G = Tools.getStopTime(starttime);
			if (changewireflag) {
				Threeto4G=-1;
			}

		}
		// 性能值写入excel文档中
		String rowname = "拨号";
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 2, rowname, model);
		String[] datas = new String[]{digFlo(netUpAbility),digFlo(netDownAbility),"SpeedTest工具","SpeedTest工具","不需测试","",Operatorname+"("+wlmtype+")"+":"+digFlo(Threeto4G)};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"无线性能测试数据如下,运营商为%s,网络类型为%s,拨号性能值%sS,挂断性能值%sS,飞行模式到建立网络时间为%sS,3G切换4G性能值:%sS",
				Operatorname,wlmtype,digFlo(netUpAbility),digFlo(netDownAbility),digFlo(airplanetime),digFlo(Threeto4G));
	}
	
	public void close() {
		if (mBluetoothGatt == null)
			return;
		LoggerUtil.e("close");
		mBluetoothGatt.close();
		mBluetoothGatt.disconnect();
		mBluetoothGatt = null;
	}
	
	public Dialog showBleScan(Context context) {
		ListView listView = new ListView(context);
		listView.setAdapter(mDeviceAdapater);
		final BaseDialog dialog = new BaseDialog(BaseFragment.myactivity, listView, "蓝牙显示列表");
		dialog.show();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/** 选中BLE的服务之后要停止扫描 */
				boolean writeReady = false;
				boolean readReady = false;
				mBluetoothDevice = devices.get(position);
				blutoothName = mBluetoothDevice.getName();
				mScanner.stopScan(scanCallback);
				dialog.dismiss();

				mBluetoothGatt = mBluetoothDevice.connectGatt(myactivity, false, mBluetoothCallBack);
				long startTime = System.currentTimeMillis();
				while(isServiceCom==false&&Tools.getStopTime(startTime)<10)
				{
					SystemClock.sleep(100);
				}
				
				// mGattServices = mBluetoothGatt.getServices();
				if(mGattServices != null) {
					LoggerUtil.v("showBleScan->PROPERTY_WRITE and PROPERTY_NOTIFY");
					for (BluetoothGattService mGattDeivceService : mGattServices) {
						for (BluetoothGattCharacteristic mCharacteristic : mGattDeivceService.getCharacteristics()) {
							if (mCharacteristic.getProperties() == (BluetoothGattCharacteristic.PROPERTY_WRITE +
									BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
								mCharateristicMap.put("WRITE_CH", mCharacteristic);
								Log.d("eric_chen", "writeReady----");
								 writeCharacteristic = mCharateristicMap.get("WRITE_CH");
								writeReady = true;
//								BLERFLAG=writeReady;
							}
							if (mCharacteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
								mCharateristicMap.put("READ_CH", mCharacteristic);
								 readCharacteristic = mCharateristicMap.get("READ_CH");
								Log.d("eric_chen", "readReady----");
								readReady = true;
							}

						}
					}
				}
				
				if (readReady == true && writeReady == true)
					gui.cls_printf(("蓝牙："+ blutoothName +"读写特征配置完毕").getBytes());	
				if(readReady == false || writeReady == false) 
					gui.cls_printf(("蓝牙："+ blutoothName + "不支持读写特征请重新选择蓝牙").getBytes());
					
				writeReady = false;
				readReady = false;
				synchronized (g_lock) {
					g_lock.notify();
				}
			}

		});
		dialog.setOnKeyListener(keylistener);
		Log.d("eric_chen", "mCharateristicMap2==="+mCharateristicMap.size());
		return dialog;
		
	}
	
	// 对back键和home键进行监听
	static OnKeyListener keylistener = new DialogInterface.OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_HOME) {
				Log.e("dialoghome", "home");
				return true;
			} else {
				return false;
			}
		}
	};
	
	BluetoothGattCallback mBluetoothCallBack = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			Log.v("conn", newState + "");
			//修改BLE蓝牙为高速传输
			gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
//			gatt.requestMtu(400);
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				gatt.discoverServices();// 连接成功，开始搜索服务，一定要调用此方法，否则获取不到服务
				gui.cls_printf("已连接上设备".getBytes());
			} 
		};
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				isServiceCom = true;
				mGattServices = gatt.getServices();
				synchronized (g_lock) {
					g_lock.notify();
				}
				LoggerUtil.v("onServicesDiscovered->"+mGattServices.size());
			} else {
				Log.v(TAG, "onServicesDiscovered received:" + status);
				gui.cls_show_msg("line %d:获取BLE状态错误(ret = %d)", Tools.getLineInfo(), status);
				synchronized (g_lock) {
					g_lock.notify();
				}
			}

		};

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			LoggerUtil.v("onCharacteristicRead:" + status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				isReadStart = true;
			} else {
				Log.v(TAG, "onServicesDiscovered received:" + status);
				gui.cls_show_msg("line %d:获取BLE状态错误(ret = %d)", Tools.getLineInfo(), status);
				synchronized (g_lock) {
					g_lock.notify();
				}
			}
		};

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			LoggerUtil.v("onCharacteristicWrite:" + status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
//				mGattCharacteristic = characteristic;
				isWriteStart = true;

			}
		};
		
		public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
			Log.d("eric_chen", "mtu=="+mtu);
//			gatt.discoverServices();
			 
		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			LoggerUtil.v("onCharacteristicChanged");
			rbuf = characteristic.getValue();
			Log.d("eric_chen","rbuf3=="+Dump.getHexDump(rbuf));
			isNotifyStart = true;
		};
	};
	
	ScanCallback scanCallback=new ScanCallback() {
		@Override
		public void onScanResult(int callbackType,android.bluetooth.le.ScanResult result) {
			super.onScanResult(callbackType, result);
			BluetoothDevice device = result.getDevice();
			if (devices.contains(device) == false) {
				devices.add(device);
				Log.d("eric_chen", device.getAddress() + "===" + device.getName());
				if (device.getName()!=null) {
					if (device.getName().equals("13579")) {
						Log.d("eric_chen","找到约定的BLE蓝牙");
						gScanflag=true;
//						mScanner.stopScan(scanCallback);
					}
				}
			}
				mDeviceAdapater.notifyDataSetChanged();
		}
		
		public void onScanFailed(int errorCode) {
			Log.e("eric_chen", "search fail:errorCode=="+errorCode);
		};
	};
	
	
	private void createBtAbilityPacket(PacketBean sendPacket, byte[] buf,int count) {
		sendPacket.setHeader(buf);
		sendPacket.setLen(buf.length);
		sendPacket.setOrig_len(buf.length);
		sendPacket.setLifecycle(count);
		sendPacket.setForever(false);
		sendPacket.setIsLenRec(false);
		sendPacket.setIsDataRnd(true);
	}
	// 蓝牙性能
	public void btAbility(ExcelUtils excelUtils,String model){

		String btchoose = "";
		String funcName="BtAbility";
		SettingsManager settingsManager = null;
		boolean isBle = true;
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)
			isBle=false;
		
		if (gui.cls_show_msg("测试BT为SPPorBLE？。确定[SPP],其他[BLE]") == ENTER) {
			btchoose = "SPP";
		} else {
			btchoose = "BLE";
		}
		if (btchoose.equals("SPP")) {
			long startbttime = 0;
			float endbttime = 0;
			long startscantime = 0;
			float endscantime = 0;
			long stopbttime = 0;
			float stopendtime = 0;
			int time = 0;
			long oldtime;
			GlobalVariable.BtAbility=true;
			ArrayList<BluetoothDevice> btList = new ArrayList<BluetoothDevice>();
			ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
			ArrayList<String> btdata = new ArrayList<>();
			gui.cls_show_msg("选择SPP蓝牙,请将需要连接的蓝牙名称改为BtTest,并打开该蓝牙");
			gui.cls_show_msg1(1,"测试中...");
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
			{
				settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			}
			// 由于蓝牙所需要的权限包含Dangerous Permissions，所以需要动态授权处理
			Config	config = new Config(myactivity, handler);
			BluetoothService dataService;
			BluetoothManager bluetoothManager = BluetoothManager.getInstance(myactivity);
			BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
			boolean Sppbtflag=false;
			// 打开蓝牙耗时
			startbttime = System.currentTimeMillis();
			Log.d("eric_chen", "startbttime=="+startbttime);
			if (!bluetoothAdapter.enable()) {
				Sppbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开蓝牙失败", Tools.getLineInfo());
			}
			oldtime = System.currentTimeMillis();
			while (time < 10) {
				time = (int) Tools.getStopTime(oldtime);
				if (bluetoothAdapter.isEnabled()) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (!bluetoothAdapter.isEnabled()) {
				Sppbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,
						"line %d:蓝牙未打开", Tools.getLineInfo());
			}
			endbttime = Tools.getStopTime(startbttime);
			Log.d("eric_chen", "endbttime=="+endbttime);
			bluetoothManager.regist();
			SystemClock.sleep(2000);
			// 扫描耗时
			startscantime = System.currentTimeMillis();
			Log.d("eric_chen", "startscantime=="+startscantime);
			Set<BluetoothDevice> pairedDevices = bluetoothManager.queryPairedDevices();

			try {
				config.bluetoothDetect(bluetoothManager, unPairList, 12);
			} catch (ApplicationExceptionBean e1) {
				e1.printStackTrace();
			}
			time = 0;
			oldtime = System.currentTimeMillis();
			while (time < 10) {
				if (unPairList.size() >= 10) {
					break;
				}
				time = (int) Tools.getStopTime(oldtime);
				SystemClock.sleep(10);
			}
			for (int i = 0; i < unPairList.size(); i++) {
				Log.d("eric_chen", "unPairList=="+ unPairList.get(i).getAddress());
				Log.d("eric_chen", "unPairList==" + unPairList.get(i).getName());
				if (unPairList.get(i).getName() != null) {
					if (unPairList.get(i).getName().equals("BtTest")) {
						Log.d("eric_chen", "搜索到约定好的bt");
						endscantime = Tools.getStopTime(startscantime);
						Log.d("eric_chen", "endscantime=="+endscantime);
					}
				}
			}
			stopbttime = System.currentTimeMillis();
			Log.d("eric_chen", "stopbttime=="+stopbttime);
			if (!bluetoothAdapter.disable()) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:蓝牙关闭失败", Tools.getLineInfo());
			}
			time = 0;
			oldtime = System.currentTimeMillis();
			while (time < 10) {
				time = (int) Tools.getStopTime(oldtime);
				if (!bluetoothAdapter.isEnabled()) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (bluetoothAdapter.isEnabled()) {
				Sppbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:蓝牙未关闭", Tools.getLineInfo());
			}
			stopendtime = Tools.getStopTime(stopbttime);
			Log.d("eric_chen", "stopendtime=="+stopendtime);
			//注销广播再重新注册
			bluetoothManager.unRegist();
			//这里需要重新让服务进行刷新界面。
			GlobalVariable.BtAbility=false;
			SystemClock.sleep(2000);
			bluetoothManager.regist();
			if (Sppbtflag||endscantime==0) {
				endscantime=-1;
				endbttime=-1;
				stopendtime=-1;
			}
			gui.cls_show_msg("下面开始验证蓝牙收发数据的通讯速度【30M】按任意键开始配置蓝牙");
			config.btConfig(btList, unPairList, bluetoothManager);
			dataService = new BluetoothService(getBtAddr());
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
			{
				if (gui.cls_show_msg("选中的蓝牙：%s,是否要开启下拉状态栏，因为蓝牙配对对话框有时无法弹出,【确认】开启，【其他】关闭",DefaultFragment.g_btName) == ENTER) {
					settingsManager.setStatusBarEnabled(0);
				}
			}

			int totalByte = 30 * 1024 * 1024; // 30M
			int onceByte = 2 * 1024; // 每次收发2K
			int count = totalByte / onceByte;
			int testCount = count;
			long startTime;
			float commtimes = 0, rate = 0;
			byte[] rbuf = new byte[onceByte];
			byte[] wbuf = new byte[onceByte];
			final PacketBean packet = new PacketBean();
			createBtAbilityPacket(packet, rbuf,count);
			if (!bluetoothAdapter.isEnabled()) {
				if (!bluetoothAdapter.enable()) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开蓝牙失败(ret = %s)", Tools.getLineInfo(),false);
				}
			}
			int time2 = 3;
			int succ = 0;
			SystemClock.sleep(3000);
			gui.cls_show_msg("请先打开已配置手机端的bluetoothServer蓝牙工具，完成点任意键继续");
			boolean ret;
			while (time2 >= 0)/** 真正与服务端连接成功才可视为连接上 */
			{
				time2--;
				byte[] tempBuf = new byte[5];
				// 建立连接
				if ((ret = bluetoothManager.connComm(dataService, CHANEL_DATA)) == false) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line:%d:BT连接建立失败(ret = %s)", Tools.getLineInfo(),ret);
					continue;
				}

				// 等待服务器端发送的hello数据后才往下走
				if (bluetoothManager.readComm(dataService, tempBuf) == false) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:客户端接收服务器hello数据失败", Tools.getLineInfo());
					continue;
				}

				if (new String(tempBuf).contains("hello")) {
					break;
				}
			}
			gui.cls_printf("进行蓝牙读写性能测试,请耐心等待".getBytes());
			int bak=testCount;
			while (testCount > 0) {
				if (gui.cls_printf(String.format("SPP读写压力测试中,已执行%d次,成功%d次,总次数%d次,总数据30M,当前剩余%dKB[取消]退出测试", bak - testCount,succ,bak,(30*1024*1024-((bak-testCount)*2048))/1024).getBytes()) == ESC)
					break;
				Log.d("eric_chen", "btcount==" + testCount);
				if (update_snd_packet(packet, LinkType.BT) != NDK_OK)
					break;
				// 等待rfcomm连接建立
				startTime = System.currentTimeMillis();
				for (int j = 0; j < wbuf.length; j++)
					wbuf[j] = 0x49;
				// 写数据操作
				bluetoothManager.writeComm(dataService, wbuf);
				// 读数据操作
				bluetoothManager.readComm(dataService, rbuf);
				if (Tools.memcmp(wbuf, rbuf, wbuf.length) == false) {
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line:%d比较数据失败(%s)", Tools.getLineInfo(), false);
					continue;
				}
				testCount--;
				succ++;
				commtimes = (float) (commtimes + Tools.getStopTime(startTime));
			}
			bluetoothManager.cancel(dataService);
			if (bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.disable();
				SystemClock.sleep(500);
			}
			// 包括了发送和接收
			rate = totalByte / (1024 * commtimes);
			String SPP = "SPP";
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, SPP, model);
			Log.d("eric_chen", "endscantime="+endscantime+"endbttime="+endbttime+"stopendtime="+stopendtime+"rate="+rate);
	    	excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(endscantime),digFlo(endbttime),digFlo(stopendtime),digFlo(rate)});
			gui.cls_show_msg1_record(TAG, funcName, mScrTime,"SPP蓝牙测试数据如下,扫描性能为%sS,打开性能为%sS,关闭性能为%sS,(30M)蓝牙读写通讯速率为%sKB/S",
					digFlo(endscantime),digFlo(endbttime),digFlo(stopendtime),digFlo(rate));
		}
		//poynt机器不支持BLE
		else if (btchoose.equals("BLE")&&isBle) {
			long startbttime = 0;
			float endbttime = 0;
			long startscantime = 0;
			float endscantime = 0;
			long stopbttime = 0;
			float stopendtime = 0;
			int time = 0;
			long oldtime;
			gui.cls_show_msg("选择BLE蓝牙,请寻找一个支持BLE的中低端机器，重命名为'13579',并打开该蓝牙。按任意键继续");
			mDeviceAdapater = new DeviceAdapater(myactivity, devices);
			mCharateristicMap = new HashMap<String, BluetoothGattCharacteristic>();
			android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) myactivity.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			mGattServices = null;
			boolean BLEbtflag=false;
			//测试开启蓝牙性能
			startbttime = System.currentTimeMillis();
			Log.d("eric_chen", "startbttime=="+startbttime);
			if (!mBluetoothAdapter.enable()) {
				BLEbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:打开蓝牙失败", Tools.getLineInfo());
			}
			oldtime = System.currentTimeMillis();
			while (time < 10) {
				time = (int) Tools.getStopTime(oldtime);
				if (mBluetoothAdapter.isEnabled()) {
					break;
				}
				SystemClock.sleep(10);

			}
			if (!mBluetoothAdapter.isEnabled()) {
				BLEbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:蓝牙未打开", Tools.getLineInfo());
			}
			endbttime = (float) ((System.currentTimeMillis() - startbttime)/1000.0);
			Log.d("eric_chen", "endbttime=="+(System.currentTimeMillis()-startbttime));
			Log.d("eric_chen", "endbttime=="+endbttime);
			SystemClock.sleep(1000);
			//测试扫描蓝牙性能
			startscantime=System.currentTimeMillis();
			devices.clear(); //清除
			mScanner = mBluetoothAdapter.getBluetoothLeScanner();
			mScanner.startScan(scanCallback);
			time=0;
			oldtime=System.currentTimeMillis();
			while (time<10) {
				time = (int) Tools.getStopTime(oldtime);
				if (gScanflag) {
					endscantime=(float) ((System.currentTimeMillis()-startscantime)/1000.0);
					//停止扫描
					Log.d("eric_chen", "stop---scan");
					mScanner.stopScan(scanCallback);
					break;
				}
				SystemClock.sleep(10);
			}
			
			//测试蓝牙关闭性能
			stopbttime=System.currentTimeMillis();
			if (!mBluetoothAdapter.disable()) {
				BLEbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:蓝牙关闭失败", Tools.getLineInfo());
			}
			time=0;
			oldtime=System.currentTimeMillis();
			while (time<10) {
				time = (int) Tools.getStopTime(oldtime);
				if (!mBluetoothAdapter.isEnabled()) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (mBluetoothAdapter.isEnabled()) {
				BLEbtflag=true;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,
						"line %d:蓝牙未关闭", Tools.getLineInfo());
			}
			stopendtime=(float) ((System.currentTimeMillis()-stopbttime)/1000.0);
			
			if (BLEbtflag||endscantime==0) {
				endscantime=-1;
				endbttime=-1;
				stopendtime=-1;
			}
			gui.cls_show_msg("下面开始验证蓝牙收发数据的通讯速度【1M】按任意键开始配置蓝牙");
			close();
			
			int totalByte = 1 * 1024 * 1024 ; // 1M
			int onceByte = 16; //找到的中低端机器只支持16字节
			int count = totalByte / onceByte;  //次数
			int bak=count;
			int  succ = 0;
			long startTime;
			float endtime;
			float BLEAbility=0;
			//打开蓝牙
			if (!mBluetoothAdapter.isEnabled())
				mBluetoothAdapter.enable();
			SystemClock.sleep(2000);
			//开始扫描
			mScanner = mBluetoothAdapter.getBluetoothLeScanner();
			mScanner.startScan(scanCallback);
			myactivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showBleScan(myactivity);
				}
			});
			synchronized (g_lock) {
			try {
				g_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

			startTime = System.currentTimeMillis();
			Log.d("eric_chen", "mCharateristicMap=="+mCharateristicMap.size());
			Set<String> keys = mCharateristicMap.keySet();   //此行可省略，直接将map.keySet()写在for-each循环的条件中
			for(String key:keys){
				Log.d("eric_chen","key值："+key+" value值："+mCharateristicMap.get(key));
			}

	
			Log.d("eric_chen", "准备读写性能测试");
			SystemClock.sleep(2000);
			// 1.写1K数据到串口
			Arrays.fill(wbuf, (byte) 0x31);
			while (count>0) {	
				gui.cls_printf("BLE读写压力测试中,大约3个多小时。请耐心等待。。。。。。".getBytes());
//				if (gui.cls_show_msg2(0.0001f, "BLE读写压力测试中,已执行%d次,成功%d次,总次数%d次,总数据1M,当前剩余%dK[取消]退出测试", bak - count,succ,bak,(10*1024*1024-((bak-count)*16))/1024) == ESC)
//					break;
				count--;
				isWriteStart = false;
				startTime = System.currentTimeMillis();
				writeCharacteristic.setValue(wbuf);
				if (!mBluetoothGatt.writeCharacteristic(writeCharacteristic)) {
					gui.cls_only_write_msg(TAG, TESTITEM, "line %d:第%d次:写数据到从设备失败", Tools.getLineInfo(),
							bak - count);
				}
				while (isWriteStart == false && Tools.getStopTime(startTime) < 10) {
					SystemClock.sleep(10);
				}
				if (Tools.getStopTime(startTime) > 10) {
					gui.cls_only_write_msg(TAG, TESTITEM, "line %d:第%d次:写数据到设备超时", Tools.getLineInfo(),
							bak - count);
					continue;
				}
				//读数据
				Arrays.fill(rbuf, (byte) 0x00);
				isNotifyStart = false;
				startTime = System.currentTimeMillis();
				if (!mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true)) {
					gui.cls_only_write_msg(TAG, TESTITEM, "line %d:第%d次:设备读数据失败", Tools.getLineInfo(),
							bak - count);
				}
				/** 超时时间为10s */
				while (isNotifyStart == false && Tools.getStopTime(startTime) < 10) {
					SystemClock.sleep(10);
				}
				if (Tools.getStopTime(startTime) > 10) {
					gui.cls_only_write_msg(TAG, TESTITEM, "line %d:第%d次:读数据到设备超时", Tools.getLineInfo(),
							bak - count);
					continue;
				}
				// 3.比较读写数据
				if (rbuf.length != wbuf.length || Tools.memcmp(rbuf, wbuf, wbuf.length) == false) {
					gui.cls_only_write_msg(TAG, TESTITEM, "line %d:第%d次:数据比较失败(%d,%s)", Tools.getLineInfo(),
							bak - count, rbuf.length, rbuf == null ? "null" : ISOUtils.hexString(rbuf));
					SystemClock.sleep(10);
					continue;
				}
				endtime = Tools.getStopTime(startTime);
				BLEAbility = BLEAbility + endtime;
				succ++;
			}
			String BLE = "BLE";
			HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, BLE, model);
	    	excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(endscantime),digFlo(endbttime),digFlo(stopendtime),digFlo(succ * onceByte/ BLEAbility/1024)});
			gui.cls_show_msg1_record(TAG, funcName, mScrTime,"BLE蓝牙测试数据如下,扫描性能为%sS,打开性能为%sS,关闭性能为%sS,(1M)蓝牙读写通讯速率为%sKB/S",
					digFlo(endscantime),digFlo(endbttime),digFlo(stopendtime),digFlo(succ * onceByte/ BLEAbility/1024));
		}
	}
	
	// 以太网性能[以太网要先进行账户登录]
	public void ethAbility(ExcelUtils excelUtils,String model,EthernetPara ethernetPara) 
	{
		String funcName="EthAbility";
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.EthEnable)==false)
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s不支持以太网", Tools.getLineInfo(),model);
			return;
		}
		int cnt1 = 10; // 连接断开次数10次
		long startTime;
		float EthUpEndTime = 0.0f,EthDownEndTime = 0.0f;
		float EthUptime = 0.0f,EthDowntime = 0.0f;
		int succ1 = 0;
		
		initLayer();
		SocketUtil socketUtil = new SocketUtil(ethernetPara.getServerIp(),ethernetPara.getServerPort());
		Sock_t sock_t = ethernetPara.getSock_t();
		LinkType type = ethernetPara.getType();
		Log.e(TAG, type + " " + sock_t);
		int failethcount=0;
		while (cnt1 > 0) {
			cnt1--;
			if (gui.cls_show_msg1(1, "开始第%d次拨号挂断性能测试(已成功%d次),[取消]退出测试...",10 - cnt1, succ1) == ESC)
				return;
			startTime = System.currentTimeMillis();
			// 建立网络连接
			if (layerBase.netUp(ethernetPara, type) != NDK_OK) {
				failethcount++;
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"line %d:NetUp失败", Tools.getLineInfo());
				continue;
			}
			if (NetworkUtil.checkNet(myactivity)!=ConnectivityManager.TYPE_ETHERNET) {
				failethcount++;
				gui.cls_show_msg1_record(TAG, funcName, mScrTime,"line %d:网络连接失败", Tools.getLineInfo());
				continue;
			}
			EthUpEndTime = Tools.getStopTime(startTime);
			
			startTime = System.currentTimeMillis();
			if (layerBase.netDown(socketUtil, ethernetPara, sock_t, type) != NDK_OK) {
				failethcount++;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:netDown失败", Tools.getLineInfo());
				continue;
			}
			int time=0;
			long oldtime=System.currentTimeMillis();
			while(time<10){
				time=(int)Tools.getStopTime(oldtime);
				if (NetworkUtil.checkNet(myactivity)!=ConnectivityManager.TYPE_WIFI) {
					break;
				}
				SystemClock.sleep(10);
			}
			if (time>=10) {
				failethcount++;
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:网络连接成功", Tools.getLineInfo());
				continue;
				
			}
			EthDownEndTime = Tools.getStopTime(startTime);
			succ1++;
			EthUptime = EthUptime + EthUpEndTime;
			EthDowntime = EthDowntime + EthDownEndTime;
		}
		float EthUpAbility = EthUptime / succ1; // 连接性能值
		float EthDownAbility = EthDowntime / succ1; // 断开性能值
		if (failethcount>3) {
			EthUpAbility=-1;
			EthDownAbility=-1;
		}
		/*gui.cls_show_msg1(2, "下面开始计算双向通讯性能--");
		int totalByte = 30 * 1024 * 1024; // 收发总数据30M
		int onceByte = 4 * 1024; // 每次收发4K
		byte[] buf = new byte[onceByte];
		byte[] rbuf = new byte[onceByte];
		int cnt2 = totalByte / onceByte;
		int i = 0;
		int j = 0;
		double time = 0.0;
		int rlen = 0,succ2 = 0;
		double endTime = 0.0;
		if (layerBase.netUp(ethernetPara, type) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
		}
		if (GlobalVariable.currentPlatform == Model_Type.N850) {
			SystemClock.sleep(10000);
		}
		if (layerBase.transUp(socketUtil, sock_t) != NDK_OK) {
			layerBase.netDown(socketUtil, ethernetPara, sock_t, type);
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:TransUp失败", Tools.getLineInfo());
		}
		gui.cls_show_msg1(2, "30M数据双向通讯中...");
		while (cnt2 > 0) {
			i++;
			startTime = System.currentTimeMillis();
			for (j = 0; j < buf.length; j++) {
				buf[j] = (byte) (Math.random() * 128);
			}
			// 接收
			if ((rlen = sockSend(socketUtil, buf, onceByte, SO_TIMEO,ethernetPara)) != buf.length) {
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime,
						"line %d:第%d次接收数据失败(实际len = %d,预期len = %d)",Tools.getLineInfo(), i, rlen, buf.length);
				continue;
			}
			if ((rlen = sockRecv(socketUtil, rbuf, onceByte, SO_TIMEO,ethernetPara)) != rbuf.length) {
				gui.cls_show_msg1_record(TAG, funcName,
						g_keeptime,"line %d:第%d次接收数据失败(实际len = %d,预期len = %d)",Tools.getLineInfo(), i, rlen, rbuf.length);
				continue;
			}
			cnt2--;
			succ2++;
			endTime = Tools.getStopTime(startTime);
			time = endTime + time;

		}
		if (layerBase.transDown(socketUtil, sock_t) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:transDown失败", Tools.getLineInfo());
		}
		if (layerBase.netDown(socketUtil, ethernetPara, sock_t, type) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:netDown失败", Tools.getLineInfo());
		}*/
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "ETH", model);
		String[] datas = new String[]{digFlo(EthUpAbility),digFlo(EthDownAbility),"iperf工具"};
		excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, datas);
		gui.cls_show_msg1_record(TAG, funcName, mScrTime,"以太网性能测试数据如下,连接性能为%sS,断开性能为%sS,(30M)双向通讯性能为%sKB/S"
				,digFlo(EthUpAbility),digFlo(EthDownAbility),"iperf工具");
	}
	
	// 测试扫码性能
	public void scanAbility(ExcelUtils excelUtils,String model,int timeoutMs)
	{
		String funcName = "scanAbility";
		ScanDefineInfo scanDefineInfo = getCameraInfo();
		int cameraId = scanDefineInfo.getCameraId();
		int bak =50;
		int succ=0;
		float /*scanTime1,scanTime2,*/scanTime3 = 0,scanTime4=0,scanTime5=0;
		NlsPara nlsPara = new NlsPara();
		nlsPara.setPreview(true);
		Scan_Mode scanMode = Scan_Mode.NLS_1;
		StringBuffer scanResult = new StringBuffer();
		String[] codeStrs = {"134602075442138498","288977834490689855","方式","9876543210321","新大陆_N910"};
		
		
	/*	// 最近距离->10cm，支付宝码
		initScanMode(nlsPara, scanMode, cameraId, timeoutMs);
		gui.cls_show_msg("【摄像头距离二维码10cm】请使用/SVN/scan/QR_AB1.PNG图片测试,放置完毕任意键继续");
		long startTime = System.currentTimeMillis();
		for (int j = 0; j < bak; j++) {
			gui.cls_printf("正在扫码".getBytes());
			doScan(scanMode, scanResult, nlsPara);
			if(scanResult==null||scanResult.toString().equals(codeStrs[0])==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s扫码测试失败(%s)", Tools.getLineInfo(),TESTITEM,scanResult);
				releaseScan(scanMode);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				return;
			}
		}
		scanTime1 = (float) ((System.currentTimeMillis()-startTime)/(bak*1.0));*/
		
		/*// 最远距离->20cm，微信码
		gui.cls_show_msg("【摄像头距离二维码20cm】请使用/SVN/scan/QR_AB2.PNG图片测试,放置完毕任意键继续");
		startTime = System.currentTimeMillis();
		for (int j = 0; j < bak; j++) {
			doScan(scanMode, scanResult, nlsPara);
			if(scanResult==null||scanResult.toString().equals(codeStrs[1])==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s扫码测试失败(%s)", Tools.getLineInfo(),TESTITEM,scanResult);
				releaseScan(scanMode);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				return;
			}
		}
		scanTime2 = (float) ((System.currentTimeMillis()-startTime)/(bak*1.0));*/
		
		//正常距离->15cm，普通二维码
		gui.cls_show_msg("【摄像头距离二维码15cm左右】请使用/SVN/scan/QR_AB1.PNG图片测试,放置完毕任意键继续");
		succ=0;
		for (int j = 0; j < bak; j++) {
			long startTime = System.currentTimeMillis();
			initScanMode(nlsPara, scanMode, cameraId, timeoutMs);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			doScan(scanMode, scanResult, nlsPara);
			if(scanResult==null||scanResult.toString().equals(codeStrs[0])==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s扫码测试失败(%s)", Tools.getLineInfo(),TESTITEM,scanResult);
				releaseScan(scanMode);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				continue;
			}
			releaseScan(scanMode);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			scanTime3 = scanTime3 + (System.currentTimeMillis()-startTime);
			succ++;
		}
		scanTime3 = scanTime3*1.0f/succ;
		
		// 10mil的条形码
		gui.cls_show_msg("【摄像头距离二维码10cm】请使用/SVN/scan/ability.btw文件的条形码测试【需打印出来测试】,放置完毕任意键继续");
		succ=0;
		for (int j = 0; j < bak; j++) {
			long startTime = System.currentTimeMillis();
			initScanMode(nlsPara, scanMode, cameraId, timeoutMs);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			doScan(scanMode, scanResult, nlsPara);
			if(scanResult==null||scanResult.toString().equals(codeStrs[3])==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s扫码测试失败(%s)", Tools.getLineInfo(),TESTITEM,scanResult);
				releaseScan(scanMode);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				continue;
			}
			releaseScan(scanMode);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			scanTime4 = scanTime4+(System.currentTimeMillis()-startTime);
			succ++;
		}
		scanTime4 = scanTime4*1.0f/bak;
		
		// 10mil的二维码
		gui.cls_show_msg("【摄像头距离二维码10cm】请使用/SVN/scan/ability.btw文件的二维码测试【需打印出来测试】,放置完毕任意键继续");
		
		for (int j = 0; j < bak; j++) {
			long startTime = System.currentTimeMillis();
			initScanMode(nlsPara, scanMode, cameraId, timeoutMs);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			doScan(scanMode, scanResult, nlsPara);
			if(scanResult==null||scanResult.toString().equals(codeStrs[4])==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s扫码测试失败(%s)", Tools.getLineInfo(),TESTITEM,scanResult);
				releaseScan(scanMode);
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				continue;
			}
			releaseScan(scanMode);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			scanTime5 = scanTime5+(System.currentTimeMillis()-startTime);
		}
		scanTime5 = scanTime5*1.0f/bak;
		
		
		releaseScan(scanMode);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		
		HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 1, "景深", model);
    	excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(scanTime3),digFlo(scanTime4),digFlo(scanTime5)});
		
    	gui.cls_show_msg1_record(TAG, funcName, mScrTime, "正常距离扫码性能:%sms,10mil条形码扫码性能:%sms,10mil二维码扫码性能:%sms", digFlo(scanTime3),digFlo(scanTime4),digFlo(scanTime5));
	}
	
	// 安装100M左右的APP，多少次
	public void installApp(ExcelUtils excelUtils,String model)
	{
		String funcName="installApp";
		String packageName="com.ztgame.bob";
		String currentName = null;
		int respCode=-1,bak=20;
		float apkTime = 0;
    	Intent hideIntent = new Intent(NlIntent.ACTION_VIEW_HIDE);
    	hideIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	String expPackName = GlobalVariable.sdPath+"apk/qqdzz.apk";
    	File file = new File(expPackName);
    	
    	if(file.exists()==false)
    		gui.cls_show_msg("测试前置:先放置qqzzz.apk于内置SD卡,放置完毕任意键继续");
    	
    	// 注册apk安装删除的监听
    	registApk();
    	for (int j = 0; j < bak; j++) {
    		gui.cls_printf(String.format("第%d次->正在安装球球大作战的测试apk", j+1).getBytes());
    		long apkStartTime = System.currentTimeMillis();
    		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
    		{
    			 Uri contentUri = FileProvider.getUriForFile(myactivity, "com.example.highplattest.fileprovider", file);
    			 // /data/user_de/0/com.android.packageinstaller/cache/package708594952.apk
    			 if(GlobalVariable.gCurPlatVer==Platform_Ver.A7)// A7暂时写死，后续固件无问题再修改
    				 expPackName = "/data/user_de/0/com.android.packageinstaller/cache/package";
    			 else
    			 	expPackName = contentUri.getPath();
    			 LoggerUtil.v("case1 uri="+expPackName);
    			 hideIntent.setDataAndType(contentUri,"application/vnd.android.package-archive");
    			 hideIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    		}
    		else
    		{
    			hideIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    		}
    		myactivity.startActivity(hideIntent);
    		// 等待安装完成的广播
    		long tapTime=0;
    		long startTime = System.currentTimeMillis();
    		while(tapTime<60)
    		{
    			tapTime = (int) Tools.getStopTime(startTime);
    			SystemClock.sleep(1000);
    			currentName = apkReceiver.getPackName(APK_INSTALL);
    			LoggerUtil.v("aaaa="+currentName);
    			if(GlobalVariable.gCurPlatVer==Platform_Ver.A7)
    			{
            		if(currentName.contains(expPackName)&&apkReceiver.getResp(APK_INSTALL)==PACKAGE_INSTALL_SUCCESS)
            			break;
    			}
    			else
    			{
            		if(currentName.equals(expPackName)&&apkReceiver.getResp(APK_INSTALL)==PACKAGE_INSTALL_SUCCESS)
            			break;
    			}
    		}
    		respCode=apkReceiver.getResp(APK_INSTALL);
			if (respCode!= PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次静默安装app失败(apk = %s，%d)",Tools.getLineInfo(), j+1, currentName, respCode);
				return;
			}
			apkTime = apkTime + Tools.getStopTime(apkStartTime);
    		// 删除安装的apk，包名com.ztgame.bob
			gui.cls_printf(String.format("第%d次正在卸载球球大作战的测试apk", j+1).getBytes());
    		Uri delUri = Uri.parse("package:com.ztgame.bob");
    		Intent intentDel = new Intent(NlIntent.ACTION_DELETE_HIDE,delUri);
    		myactivity.startActivity(intentDel);
    		// 等待删除完毕的广播
    		tapTime=0;
    		while(tapTime<60)
    		{
    			tapTime = (int) Tools.getStopTime(startTime);
    			SystemClock.sleep(1000);
    			
    			currentName = apkReceiver.getPackName(APK_UNINSTALL);
    			LoggerUtil.v("bbb="+currentName);
    			if(currentName.equals(packageName)&&apkReceiver.getResp(APK_UNINSTALL)==PACKAGE_INSTALL_SUCCESS)
    				break;
    		}
			if (currentName.equals(packageName) == false|| (respCode = apkReceiver.getResp(APK_UNINSTALL)) != PACKAGE_INSTALL_SUCCESS) {
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次卸载app失败(apk = %s,%s，%d)",Tools.getLineInfo(), j+1,packageName, currentName, respCode);
				return;
			}
			
			apkReceiver.resetPackName();
		}
    	// 注销apk安装删除的监听
    	unRegistApk();
    	apkTime = (float) (100*bak*1.0/apkTime);
    	
    	HashMap<String, Integer> searchMap = excelUtils.searchLocatXls(TEST_EXLFILE, 0, "软件安装", model);
    	excelUtils.writeDataXlsArrays(TEST_EXLFILE, searchMap, new String[]{digFlo(apkTime)});
    	gui.cls_show_msg1_record(TAG, funcName, mScrTime, "安装100M应用的速度:%fMB/s", apkTime);
	}
	
	/**电池性能测试*/
	private void batteryAbility()
	{
		TradeTool tradeTool = TradeTool.getInstance(myactivity, handler);
		// 恢复出厂后就是恢复到默认亮度
		// 默认亮度,模拟交易后休眠唤醒的电流
		// 默认亮度,连续模拟交易时的平均电流
		// 默认亮度,卡类全开时的电流
		// 默认亮度,卡类关开关闭后的电流值
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("电池性能【不要插入USB线】\n0.默认亮度,模拟交易后休眠唤醒的电流\n1.默认亮度,连续模拟交易时的平均电流\n2.默认亮度,卡类全开时的电流\n3.默认亮度,卡类关开关闭后的电流值\n");
			switch (nkeyIn) {
			case '0':
			case '1':
				continueTrande(tradeTool, 5, nkeyIn);
				break;
				
			case '2':
			case '3':
				allCardOpen(nkeyIn);
				break;
				
			case ESC:
				return;

			default:
				break;
			}
		}
	}
	
	/**连续模拟交易后休眠唤醒的电流值*/
	private void continueTrande(TradeTool tradeTool,int count,int flag)
	{
		gui.cls_show_msg("请将设备调至默认亮度后任意键开始测试");
		if(flag=='1')
		{
			gui.cls_show_msg("模拟交易5次过程中取电流的最大值,在模拟交易过程中进行电流检测【默认亮度,连续模拟交易时的平均电流】,记录下电流值,已知悉按任意键继续");
		}
		for (int j = 0; j < count; j++) {
			try {
				tradeTool.trans_sdk2(5*60,true);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		if(flag=='0')
			gui.cls_show_msg("模拟交易5次后手动进入休眠,休眠唤醒30s,可进行电流检测【默认亮度,模拟交易后休眠唤醒的电流】,记录下电流值,记录完毕任意键退出");
	}
	
	
	
	/**卡类全开*/
	private void allCardOpen(int flag)
	{
		gui.cls_show_msg("请将设备调至默认亮度后任意键开始测试");
		String funcName = "openCardAll";
		int ret = -1;
		int[] pnSta  = new int[1];
		byte[] psPiccType = new byte[1];
		
		JniNdk.JNI_Rfid_Init(null);
		if((ret = JniNdk.JNI_Mag_Open())!=NDK_OK)// 磁卡多次打开会报错
		{
			gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			allCardClose();
			return;
		}
		while(true)
		{
			// 打开射频，磁卡，IC
			if((ret = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				allCardClose();
				return;
			}
			if((ret = JniNdk.JNI_Rfid_PiccDetect(psPiccType))!=NDK_ERR_RFID_NOCARD)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				allCardClose();
				return;
			}
			if(flag=='2')
			{
				if(gui.cls_show_msg1(200, TimeUnit.MILLISECONDS, "卡类已全开,不要放置任何卡片,可进行电流检测【默认亮度,模拟交易后休眠唤醒的电流】,记录下电流值,ESC键回到主菜单")==ESC)
				{
					allCardClose();
					return;
				}
			}
			else if(flag=='3')
				break;
		}
		if(flag=='3')
		{
			allCardClose();
			gui.cls_show_msg("卡类开启后关闭,可进行电流检测【默认亮度,卡类全开关闭后的电流值】,记录下电流值,记录完毕任意键退出");
		}
	}
	
	private void allCardClose()
	{
		JniNdk.JNI_Mag_Close();
		JniNdk.JNI_Rfid_PiccDeactivate((byte) 0);
		JniNdk.JNI_Icc_PowerDown(0);
	}
	
	ApkBroadCastReceiver apkReceiver = new ReceiverTracker().new ApkBroadCastReceiver();
	private void registApk()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.INSTALL_APP_HIDE");
		intentFilter.addAction("android.intent.action.DELETE_APP_HIDE");
		myactivity.registerReceiver(apkReceiver, intentFilter);
	}
	
	private void unRegistApk()
	{
		if(this != null)
		{
			myactivity.unregisterReceiver(apkReceiver);
		}
	}
	
	public String digFlo(float value)
	{
		if(value==0)
			return "NA";
		if(value==-1)
			return "ERR";
		else
			return String.format("%10.2f", value);
	}
	
	// wifi工具方法
	public int seekWifi(List<ScanResult> wifiList, WifiPara wifiPara,int i) {
		for (; i < wifiList.size(); i++) {
			if (wifiList.get(i).SSID.equals(wifiPara.getSsid())) {
				Log.d("eric_chen", "找到上次连接的wifi");
				return 0;
			}
		}
		return -1;
	}
	
	public String getProvidersName(Context context){
		String ProvidersName = null;
	   TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    String IMSI = telephonyManager.getSubscriberId();
	    if( IMSI == null){
	    	return "unknow";
	    }
	    if(IMSI.startsWith("46000") || IMSI.startsWith("46002")){
	        ProvidersName = "中国移动";
	    }else if(IMSI.startsWith("46001")){
	        ProvidersName = "中国联通";
	    }else if (IMSI.startsWith("46003")) {
	        ProvidersName = "中国电信";
	    }else {
	    	 ProvidersName = "未知";
		}
	    Log.e("eric_chen", "==== 当前卡为："+ProvidersName);
		return ProvidersName;
	}

	int mScrTime=0;/**默认值为5,代表5s的等待，自动测试时值设置为3*/
	boolean isAuto = false;
	public void systest300() 
	{
		SdkType defultType = GlobalVariable.sdkType;
		
		/** SDK2.0是不需要变化的，性能测试将SDK类型的标志位修改为SDK2,测试完毕后会修改为SDK3*/
		if(defultType==SdkType.SDK3)
			GlobalVariable.sdkType = SdkType.SDK2;
		
		ExcelUtils excelUtils = new ExcelUtils();
		String model = GlobalVariable.currentPlatform.toString();
		WifiPara wifiPara = new WifiPara();
		MobilePara mobilePara = new MobilePara();
		EthernetPara ethernetPara = new EthernetPara();
		Config config = new Config(myactivity, handler);

		while(true)
		{
			mScrTime = 0;
			isAuto = false;
			int nkey = gui.JDK_ReadData(10*60, -1, "性能测试\n0.全自动\n1.打印\n2.IC\n3.射频卡\n4.安全\n6.WIFI\n7.无线\n8.蓝牙\n" +
					"9.以太网\n11.扫码性能\n12.FS\n13.SD/TF/U盘\n14.安装100M左右应用的速度\n15.电池性能\n");
			
			switch (nkey) {
			case 0:
				isAuto=true;
				// 放置打印的图片、接好U盘、TF、插入IC SAM,无线，放置测试APK
				gui.cls_show_msg("测试前请确保以下操作已执行:\n1.将ability_V1.xls文件放置到/sdcard目录\n2.将/SVN/picture和ttf字库的图片放置到/sdcard/picture\n3.已插入U盘、TF卡\n4.已插入IC和SAM卡\n5.已插入sim卡\n" +
						"6.已将qqdzz.apk放置到/sdcard/apk下,需下载对应的证书以及放置对应签名的qqdzz.apk\n7.打印测试需要使用稳压源控制(手动测试)\n");
				// 配置射频卡
				_SMART_t type = config.rfid_config();
				// 配置wifi
				config.confConnWlan(wifiPara);
				// 配置无线
				config.confConnWLM(true, mobilePara);
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.EthEnable))
				{
					// 配置以太网
					config.confConnEth(ethernetPara);
				}
				if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
				{
					gui.cls_show_msg("【A7以上产品需要将AndroidManifest.xml的FileProvide标签打开】");
				}
				mScrTime = 3;
				gScanflag = false;
				long startTime = System.currentTimeMillis();
				gui.cls_show_msg1_record(TAG, "systest300", 1, "开始时间%d", startTime);
				printAbility(excelUtils, model);
				icSamAbility(excelUtils, model);
				rfidAbility(type, excelUtils, model);
				secAbility(excelUtils, model,true);
//				shaTest(excelUtils, model);// 纯软的算法不需测试
				WifiAbility(excelUtils, model,wifiPara);
				wlmAbility(excelUtils, model, mobilePara);
				ethAbility(excelUtils, model, ethernetPara);
//				btAbility(excelUtils, model);
				fsAbility(excelUtils, model);
				andFileAbility(excelUtils,model);
				installApp(excelUtils,model);
				scanAbility(excelUtils, model, 15*1000);
				gui.cls_show_msg1_record(TAG, "systest300", 1, "总共时间时间%fs", Tools.getStopTime(startTime));
				gui.cls_show_msg("性能自动化测试完毕,任意键继续其他测试");
				break;
				
			case 1:
				printAbility(excelUtils,model);
				break;
				
			case 2:
				icSamAbility(excelUtils, model);
				break;
				
			case 3:
				_SMART_t type1 = new Config(myactivity, handler).rfid_config();
				rfidAbility(type1, excelUtils, model);
				break;
				
			case 4:
				secAbility(excelUtils, model,false);
				break;
				
		/*	case 5:
				shaTest(excelUtils, model);
				break;*/
				
			case  6:// wifi
				config.confConnWlan(wifiPara);
				WifiAbility(excelUtils, model, wifiPara);
				break;
				
			case 7:// 无线
				// 配置无线
				config.confConnWLM(true, mobilePara);
				wlmAbility(excelUtils, model, mobilePara);
				break;
				
			case 8:// 蓝牙
				gScanflag = false;
				btAbility(excelUtils, model);
				break;
				
			case 9:// 以太网
				config.confConnEth(ethernetPara);
				ethAbility(excelUtils, model, ethernetPara);
				break;
				
			case 11:// 扫码
				scanAbility(excelUtils, model, 15*1000);
				break;
				
			case 12:// FS
				fsAbility(excelUtils, model);
				break;
				
			case 13:// SD卡/U盘/TF卡
				andFileAbility(excelUtils,model);
				break;
				
			case 14:// 隐性安装apk
				if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
				{
					gui.cls_show_msg("【A7以上产品需要将AndroidManifest.xml的FileProvide标签打开】");
				}
				installApp(excelUtils,model);
				break;
				
				
			case 15:// 电池性能
				batteryAbility();
				break;
				
			case ESC:
				/**测试完毕修改为SDK3*/
				if(defultType==SdkType.SDK3)
					GlobalVariable.sdkType = SdkType.SDK3;
				intentSys();
				return;
			}
		}
	}
}
