package com.example.highplattest.net;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.newland.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.quectel.jni.QuecJNI;
/************************************************************************
 * module 			: wifi探针
 * file name 		: Net3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171109
 * directory 		: 设置wifi探针的开关
 * description 		: setMonitor(int status)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20171109     	created
 *						变更记录			时间				变更人
 *					wifiutil改为单例模式	20200727        陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net3 extends UnitFragment
{
	private final String TESTITEM = "wifi探针";
	private final String CLASS_NAME = Net3.class.getSimpleName();
	private WifiUtil wifiUtil;
	private WifiP2pManager mwifiP2PManager;
	private WifiManager mWifiProbe=null;
	private android.net.wifi.WifiManager mWifiManager;
	
	private Gui gui = new Gui(myactivity, handler);;
	private Channel mChannel;
	Set<String> probeDatas = new HashSet<String>();
	String dataResult;
	private WakeLock wakeLock;
	private p2pTimeThread  mP2PTimeThread = new p2pTimeThread();
	
	public void net3()
	{
		String funcName="net3";
		if(GlobalVariable.gAutoFlag==AutoFlag.AutoFull)
		{
			monitor_Test();
			gloMonitor_Test();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s接口测试已测试完毕，其他测试项请手动验证",TESTITEM);
			return;
		}
		
		// 根据刘坤坤反馈新增的机型均有导入wifi探针功能 modify by zhengxq
		try{
			String result = BaseFragment.getProperty("sys.epay.wifiprobe","-10086");
			if(result.equalsIgnoreCase("true")==false)
			{
				gui.cls_show_msg("本固件不支持wifi探针功能,任意键退出");
				unitEnd();
				return;
			}
		}catch(Exception e)
		{
			gui.cls_show_msg("该固件版本不支持wifi探针功能，请于开发确认是否导入wifi探针功能，任意键退出测试");
			unitEnd();
			return;
		}
		
		/*private & local definition*/
		mWifiProbe = new WifiManager(myactivity);
//		wifiUtil = new WifiUtil(myactivity);
		wifiUtil = WifiUtil.getInstance(myactivity,handler);
		mwifiP2PManager=(WifiP2pManager) myactivity.getSystemService(Context.WIFI_P2P_SERVICE);
		mWifiManager = (android.net.wifi.WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE);
		PowerManager powerManager=(PowerManager) myactivity.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"bright");
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("测试项\n0.恢复出厂设置测试实际场景\n1.插入sim卡开热点测试\n2.接口测试 \n3.功耗测试项\n4.总开关掉电测试\n5.获取总开关\n");
			switch (nkeyIn) 
			{
			case '0':
				testZero();
				break;
				
			case '1':
				testOne();
				break;
				
			case '2':
				probeInterface();
				break;
				
			case '3':
				testThree();
				break;	
				
			case '4':
				testFour();
				break;	
				
			case '5':
				getGlobalMonitor();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	/**
	 * 恢复出产设置进行开p2p方式测试
	 */

	public void testZero(){
		wifiUtil.closeOther();
		int nkeyIn = gui.cls_show_msg("p2p开启探针测试\n0.5分钟客流量测试\n");
		switch (nkeyIn) 
		{
		case '0':
			testOnePassengersFlow();
			break;
			
		case ESC:
			return;
		}

	}
	
	
//	/**
//	 * 搜索特定手机
//	 */
//	private void testOneAbility() 
//	{
//		String funcName="testOneAbility";
//		int iRet = -1;
//		long startTime;
//		
//		if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
//			if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
//				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s开启Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
//				if (!GlobalVariable.isContinue)
//					return;
//			}
//		}
//		// p2p模式下，输入mac地址，看多久可以搜索到
//		// 输入框输入要查找的mac地址
//		myactivity.runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				getWifiMac();
//			}
//		});
//		synchronized (g_lock) {
//			try {
//				g_lock.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		if(GlobalVariable.RETURN_VALUE!=SUCC)
//			return;
//		gui.cls_show_msg("关闭数据，确保要查找的设备wifi要开启，任意键继续");
//		if( mWifiProbe.getMonitor()!=0){
//			if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
//				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:开启wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
//				if (!GlobalVariable.isContinue)
//					return;
//			}
//			// 重新打开wifi
//			wifi_open_close();
//		}
//		
//
//		// 打开p2p模式
//		mChannel = mwifiP2PManager.initialize(myactivity,myactivity.getMainLooper(), null);
//		if (mChannel == null) {
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:打开p2p模式失败",Tools.getLineInfo(), iRet);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		new Thread(mP2PTimeThread).start();
//		gui.cls_show_msg1(2, "正在查找...有时时间会较长，请耐心等待");
//		// 开始时间
//		startTime = System.currentTimeMillis();
//		float endTime = 0;
//		// X5这个port是29
//		if(GlobalVariable.currentPlatform==Model_Type.X5)
//			QuecJNI.setNetPort(29);
//		else
//			QuecJNI.setNetPort(30);
//    	QuecJNI.openNode();// 打开节点
//		while (true) {
//			byte[] readWifi = new byte[2048];
//			QuecJNI.transferSwitch(1);
//			iRet = QuecJNI.wifiProbe(readWifi);
//			String strProbe = new String(readWifi);
//			String[] macDate = strProbe.split(" ");
//			if (macDate.length > 1) {
//				QuecJNI.transferSwitch(2);
//				if (seek(macDate) == 0) {
//					endTime = Tools.getStopTime(startTime);
//					break;
//				}
//				SystemClock.sleep(10 * 1000);
//			}
//
//		}
//		QuecJNI.closeNode();
//		gui.cls_show_msg1_record(CLASS_NAME,funcName,0, "p2p方式探针探索到%s所需时间为%fs", mac, endTime);
//		if ((iRet = mWifiProbe.setMonitor(1)) != WIFI_SUCC) {
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:关闭wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		mP2PTimeThread.setTimeStop(true);
//		wifi_open_close();
//		
//	}
	
	/**
	 * 五分钟客流量测试
	 */
	private void testOnePassengersFlow() 
	{
		String funcName="testOnePassengersFlow";
		int iRet = -1;
		long startTime;
		
		// case1:恢复出产设置后 进行开启p2p测试探针，模拟银商使用环境
		if (gui.ShowMessageBox("恢复出厂后进入本用例，【取消】退出，【确认】继续".getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) != BTN_OK) {
			return;
		}
		 // 出厂为总开关默认为0
		 if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s出厂总开关应为开启状态失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
			 if (!GlobalVariable.isContinue)
				 return;
		 }
		// 打开wifi探针
		gui.cls_show_msg1(1, "测试开wifi开启探针方式");
		if( mWifiProbe.getMonitor()!=0){
			if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:开启wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
			// 重新打开wifi
			wifi_open_close();
			gui.cls_show_msg1(1, "重启wifi成功");
		}
		if(mWifiManager.getWifiState()!=android.net.wifi.WifiManager.WIFI_STATE_ENABLED){
			wifi_open_close();
		}
		// 打开p2p模式
		mChannel = mwifiP2PManager.initialize(myactivity,myactivity.getMainLooper(), null);
		if (mChannel == null) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:打开p2p模式失败",
					Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg("准备多台不同型号设备，包括市面上主流的不同型号的安卓和ios设备，开启wifi，记录下mac地址，不少于5台，任意键继续");
		// 30s唤醒一次p2p
		new Thread(mP2PTimeThread).start();
		gui.cls_show_msg1(1, "模拟测试客流量，等待5分钟...");
		probeDatas.clear();
		startTime = System.currentTimeMillis();
		// X5和F7这个port是29，后续新平台应该都是这个节点 by zhengxq
		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
			QuecJNI.setNetPort(29);
		else
			QuecJNI.setNetPort(30);
    	QuecJNI.openNode();// 打开节点
		while (true) {
			byte[] readWifi = new byte[2048];
			QuecJNI.transferSwitch(1);
			iRet = QuecJNI.wifiProbe(readWifi);
			String strProbe = new String(readWifi);
			Log.v("内容", strProbe);
			String[] date = strProbe.split(" ");
			if (date.length > 1) {
				QuecJNI.transferSwitch(2);
				pushToSet(date);
			}
			if (Tools.getStopTime(startTime) > 300)
				break;
			SystemClock.sleep(1000 * 10);
		}
		QuecJNI.closeNode();
		dataResult = setsToString(probeDatas);
		// 写入文件
		writeResult("p2p模式下wifi探针客流量记录 size:" + probeDatas.size() + "个----"+ getNowTime() + "----" + "\n");
		writeResult(dataResult);
		gui.cls_show_msg("5分钟客流量为：%d，数据已写入sd卡目录下的wifiPrope.txt，任意键继续",probeDatas.size());
		if (gui.ShowMessageBox("请对比特意准备的多台型号设备的mac地址，是否都被写入文件".getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) != BTN_OK) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:p2p模式下wifi探针测试失败",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 后置 关闭节点 关闭探针 停止p2p 关闭wifi
		if ((iRet = mWifiProbe.setMonitor(1)) != WIFI_SUCC) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:关闭wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 定时唤醒p2p停止
		mP2PTimeThread.setTimeStop(true);
		wifi_open_close();
	}
	
	/**
	 * 插入sim卡开热点测试
	 */
	public void testOne(){
		if (gui.ShowMessageBox("插入sim卡后进入本用例，取消退出，确认继续".getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) != BTN_OK) {
			return;
		}
		int nkeyIn = gui.cls_show_msg("热点开启探针测试\n0.5分钟客流量测试\n1.搜索单台设备时间\n");
		switch (nkeyIn) 
		{
		case '0':
			testTwoPassengersFlow();
			break;
			
		case '1':
			testTwoAbility();
			break;
		case ESC:
			return;
		}
		
		
		
		
	}
	private void testTwoAbility() 
	{
		String funcName="testTwoAbility";
		
		float endTime = 0;
		int iRet = -1;
		long startTime;
		
		if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
			if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s开启Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				getWifiMac();
			}
		});
		synchronized (g_lock) {
			try {
				g_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return;
		if(mWifiProbe.getMonitor()!=0){
			if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:开启wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		gui.cls_show_msg("打开便携式wlan热点，已打开热点的请重新关闭打开一下，确保要查找的设备wifi要开启，任意键继续");
		gui.cls_show_msg1(2, "正在查找...有时时间会较长，请耐心等待");
		// 开始时间
		QuecJNI.closeNode();
		// X5这个port是29
		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
			QuecJNI.setNetPort(29);
		else
			QuecJNI.setNetPort(30);
    	QuecJNI.openNode();// 打开节点
		startTime = System.currentTimeMillis();
		while (true) {
			byte[] readWifi = new byte[2048];
			QuecJNI.transferSwitch(1);
			iRet = QuecJNI.wifiProbe(readWifi);
			String strProbe = new String(readWifi);

			String[] macDate = strProbe.split(" ");
			if (macDate.length > 1) {
				QuecJNI.transferSwitch(2);
				if (seek(macDate) == 0) {
					endTime = Tools.getStopTime(startTime);
					break;
				}
				SystemClock.sleep(10 * 1000);
			}

		}
		QuecJNI.closeNode();
		gui.cls_show_msg1_record(CLASS_NAME,funcName,0, "热点方式探针探索到%s所需时间为%fs,任意键继续", mac, endTime);
		if ((iRet = mWifiProbe.setMonitor(1)) != WIFI_SUCC) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:关闭wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg1(0,"测试通过，请关闭便携式热点");
		
		
	}
	private void testTwoPassengersFlow() 
	{
		String funcName="testTwoPassengersFlow";
		int iRet = -1;
		long startTime;
		
		if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
			if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s开启Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if(mWifiProbe.getMonitor()!=0){
			if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:开启wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		// 打开wifi探针
	
		gui.cls_show_msg("打开数据，打开便携式wlan热点，已打开热点的请重新关闭打开一下，准备多台不同型号设备，包括市面上主流的不同型号的安卓和ios设备，任意键继续");
		gui.cls_show_msg1(1,"模拟测试客流量，等待5分钟...");
		//清除集合数据
		probeDatas.clear();
		QuecJNI.closeNode();
		QuecJNI.openNode();
		startTime=System.currentTimeMillis();
		// X5这个port是29
		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
			QuecJNI.setNetPort(29);
		else
			QuecJNI.setNetPort(30);
    	QuecJNI.openNode();// 打开节点
		while(true){
			byte[] readWifi = new byte[2048];
			QuecJNI.transferSwitch(1);
			iRet = QuecJNI.wifiProbe(readWifi);
			String strProbe=new String(readWifi);
			Log.v("内容",strProbe );
			
			String [] macDate=strProbe.split(" ");
			if(macDate.length>1){
				QuecJNI.transferSwitch(2);
				pushToSet(macDate);//放入到集合中
				if(Tools.getStopTime(startTime)>300)
					 break;
				SystemClock.sleep(10*1000);
			}
			
		}
		QuecJNI.closeNode();
		dataResult=setsToString(probeDatas);
		//写入文件
		writeResult("热点模式下wifi探针客流量记录 size:"+probeDatas.size()+"个----"+getNowTime()+"----"+"\n");
		writeResult(dataResult);
		gui.cls_show_msg("5分钟客流量为：%d，数据已写入sd卡目录下的wifiPrope.txt，任意键继续",probeDatas.size());
		if(gui.ShowMessageBox("请对比特意准备的多台型号设备的mac地址，是否都被写入文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:热点模式下wifi探针测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		//关闭wifi探针
		if ((iRet = mWifiProbe.setMonitor(1)) != WIFI_SUCC) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:关闭wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(0,"测试通过，请关闭便携式热点");
	}
	
	/**
	 * 探针接口测试
	 */
	private void probeInterface()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("接口测试\n0.分开关接口\n1.总开关接口\n");
			switch (nkeyIn) {
			case '0':
				monitor_Test();
				break;
				
			case '1':
				gloMonitor_Test();
				break;

			case ESC:
				return;
			
			default:
				break;
			}
		}
	}
	
	/**
	 * 接口测试
	 */
	private void monitor_Test()
	{
		String funcName="monitor_Test";
		int iRet = -1;

		// case1.1:参数异常测试，设置status=-1，应返回参数异常（-4）
		// 操作探针之前先进行开总开关操作
		if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC&&iRet!=1) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((iRet = mWifiProbe.setMonitor(-1)) != WIFI_PARA_ERR) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor参数异常测试失败(%d)",Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case1.2:参数异常测试，设置status=2，应返回参数异常（-4）
		if ((iRet = mWifiProbe.setMonitor(2)) != WIFI_PARA_ERR) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor参数异常测试失败(%d)",Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case1.3:参数异常测试，设置status = 255，应返回参数异常（-4）
		if ((iRet = mWifiProbe.setMonitor(255)) != WIFI_PARA_ERR) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor参数异常测试失败(%d)",Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case2:开启探针后关闭探针 应成功(0:开启，1：关闭) 包含获取状态
		if(mWifiProbe.getMonitor()!=1){
			if ((iRet = mWifiProbe.setMonitor(1)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if ((iRet = mWifiProbe.getMonitor()) != 1) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 关闭wifi探针后再次关闭应返回1（输入的参数与底层的值原本就一样）
		if((iRet=mWifiProbe.setMonitor(1))!=WIFI_INPUT_SAME)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(mWifiProbe.getMonitor()!=0){
			if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		if ((iRet = mWifiProbe.getMonitor()) != 0) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:getMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 开启wifi探针后再次开启应返回1（输入的参数与底层的值原本就一样）
		if((iRet=mWifiProbe.setMonitor(0))!=WIFI_INPUT_SAME)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}

		// case3:总开关关闭后打开探针应返回总开关未开启
		if((iRet = mWifiProbe.setGlobalMonitor(1))!=WIFI_SUCC&&iRet!=WIFI_INPUT_SAME)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setGlobalMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.getMonitor())!=WIFI_GLO_CLAOSE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:getMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.setMonitor(0))!=WIFI_GLO_CLAOSE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:setMonitor测试失败(%d)",Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"探针分开关接口测试通过");
	}
	
	private void gloMonitor_Test()
	{
		String funcName="gloMonitor_Test";
		int iRet = -1;
		// 测试前置：总开关关闭
		mWifiProbe.setGlobalMonitor(1);
		// case1 设置WiFi探针的总开关 参数异常 返回-4
		// case1.1 参数异常测试，设置status=-1，应返回参数异常（-4）
		gui.cls_show_msg1(2,"总开关异常测试...");
		if ((iRet = mWifiProbe.setGlobalMonitor(-1)) != WIFI_PARA_ERR) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor参数异常测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case1.2:参数异常测试，设置status=2，应返回参数异常（-4）
		if ((iRet = mWifiProbe.setGlobalMonitor(2)) != WIFI_PARA_ERR) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor参数异常测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case1.3:参数异常测试，设置status = 255，应返回参数异常（-4）
		if ((iRet = mWifiProbe.setGlobalMonitor(255)) != WIFI_PARA_ERR) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor异常测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case2:打开总开关之后关闭总开关 应成功（0：开启 ，1：关闭）
		if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s获取Wifi探针总开关错误(%d)", Tools.getLineInfo(),TESTITEM, iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((iRet = mWifiProbe.setGlobalMonitor(1)) != WIFI_SUCC) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if ((iRet = mWifiProbe.getGlobalMonitor()) != 1) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s获取Wifi探针总开关错误(%d)", Tools.getLineInfo(),TESTITEM, iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case3:开启总开关后再次开启应返回WIFI_INPUT_SAME，关闭总开关后再次关闭应返回WIFI_INPUT_SAME
		if((iRet=mWifiProbe.setGlobalMonitor(1))!=WIFI_INPUT_SAME)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		mWifiProbe.setGlobalMonitor(0);
		if((iRet = mWifiProbe.setGlobalMonitor(0))!=WIFI_INPUT_SAME)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3:总开关与分开关联合使用
		// case3.1:打开总开关后关闭或开启分开关均应成功
		if((iRet=mWifiProbe.setGlobalMonitor(0))!=WIFI_INPUT_SAME)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.setMonitor(1))!=WIFI_SUCC&&iRet!=1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.getMonitor())!=1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:getMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet=mWifiProbe.setMonitor(0))!=WIFI_SUCC&&iRet!=1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet=mWifiProbe.getMonitor())!=0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:getMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case3.2:关闭总开关会主动关闭分开关且打开分开关应失败（-5）
		if((iRet = mWifiProbe.setGlobalMonitor(1))!=WIFI_SUCC)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.getMonitor())!=WIFI_GLO_CLAOSE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:getMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.setMonitor(0))!=WIFI_GLO_CLAOSE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setMonitor测试失败(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case3.3:总开关关闭后开启，分开关应为关闭状态
		if((iRet = mWifiProbe.setGlobalMonitor(0))!=WIFI_SUCC)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:setGlobalMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((iRet = mWifiProbe.getMonitor())!=1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:getMonitor测试失败(%d)", Tools.getLineInfo(),iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"探针总开关接口测试通过");
	}
	
	/**
	 * 功耗测试
	 */
	public void testThree()
	{
		String funcName="testThree";
		int iRet = -1;
			wifiUtil.closeOther();
			int nkeyIn = gui.cls_show_msg("功耗测试项\n0.开启总开关\n1.关闭总开关\n2.开启探针 \n3.关闭探针\n4.开启p2p\n5.关闭p2p");
			switch (nkeyIn) 
			{
			case '0':
				gui.cls_show_msg1(1,"总开关开启中...");
				if(mWifiProbe.getGlobalMonitor()!=0){
					if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s开启Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
						return;
					}
					wifi_open_close();
				}
				gui.cls_show_msg1(2,"总开关开启已生效，请熄屏测试功耗...应能进入深休眠");
				break;
				
			case '1':
				gui.cls_show_msg1(1,"总开关关闭中...");
				if(mWifiProbe.getGlobalMonitor()!=1){
					if ((iRet = mWifiProbe.setGlobalMonitor(1)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
						return;
					}
					wifi_open_close();
				}
				
				gui.cls_show_msg1(2,"总开关关闭已生效，请熄屏测试功耗...应能进入深休眠");
				break;
				
			case '2':
				gui.cls_show_msg1(1,"wifi探针开启中...");
				//总开关关闭时打开总开关
				if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
					if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
						return;
					}
				}
				if(mWifiProbe.getMonitor()!=0){
					if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:开启wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
						return;
					}
					wifi_open_close();
				}
				
				gui.cls_show_msg1(0,"开启探针已生效，请熄屏测试功耗...应能进入深休眠");
				break;
			case '3':
				gui.cls_show_msg1(1,"wifi探针关闭中...");
				//总开关关闭时打开总开关
				if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
					if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s开启Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
						return;
					}
				}
				if(mWifiProbe.getMonitor()!=1){
					if ((iRet = mWifiProbe.setMonitor(1)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:关闭wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
						return;
					}
					wifi_open_close();
				}
				
				gui.cls_show_msg1(0,"关闭探针已生效，请熄屏测试功耗...应能进入深休眠");
				break;	
			case '4':
				//开启p2p
				//总开关关闭时打开总开关
				if ((iRet = mWifiProbe.getGlobalMonitor()) != 0) {
					if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s开启Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
						return;
					}
				}
				if(mWifiProbe.getMonitor()!=0){
					if ((iRet = mWifiProbe.setMonitor(0)) != WIFI_SUCC) {
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:开启wifi探针测试失败(%d)",Tools.getLineInfo(), iRet);
						return;
					}
					wifi_open_close();
					
				}
				gui.cls_show_msg1(1,"wifi探针已开启");
				wakeLock.acquire();
				mChannel = mwifiP2PManager.initialize(myactivity,myactivity.getMainLooper(), null);
				if (mChannel == null) {
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:打开p2p模式失败",
							Tools.getLineInfo(), iRet);
					return;
				}
				new Thread(mP2PTimeThread).start();
				
				gui.cls_show_msg1(0,"p2p已开启，请熄屏测试功耗...应能进入深休眠");
				break;	
			case '5':
				mP2PTimeThread.setTimeStop(true);
				if(mChannel!=null){
					stopDiscovery();
					wakeLock.release();
				}
				gui.cls_show_msg1(0,"p2p已关闭，请熄屏测试功耗...（探针未关闭）应能进入深休眠");
				break;
			case ESC:
				
				return;
			}
		}

	
	/**
	 * 总开关掉电测试
	 */
	public void testFour()
	{
		String funcName="testFour";
		int iRet = -1;
		if (gui.ShowMessageBox("第一次进入本用例按确认，第二次进入本用例按取消".getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) != BTN_OK) {
			//第二次进入本用例
			if ((iRet = mWifiProbe.getGlobalMonitor()) != 1) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:掉电测试失败，预期总开关关闭，实际开启(%d)", Tools.getLineInfo(), iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
			//还原
			if ((iRet = mWifiProbe.setGlobalMonitor(0)) != WIFI_SUCC) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"掉电测试通过");
			return;
		}
		gui.cls_show_msg1(1,"设置总开关为关闭");
		if ((iRet = mWifiProbe.setGlobalMonitor(1)) != WIFI_SUCC) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭Wifi探针总开关失败(%d)", Tools.getLineInfo(),TESTITEM, iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		wifi_open_close();
		
		gui.cls_show_msg("已设置总开关为关闭状态,并且已生效，重启后第二次进入此用例验证开关为关闭即测试通过，任意键重启机器");
		Tools.reboot(myactivity);
	}
	
	/**
	 * 固件升级或恢复出厂设置总开关应为开启装填
	 */
	private void getGlobalMonitor()
	{
		String funcName="getGlobalMonitor";
		if(gui.cls_show_msg("请确保进入本用例为恢复出厂设置或固件升级，非此两种情况【取消】键退出")==ESC)
			return;
		int iRet = -1;
		if((iRet = mWifiProbe.getGlobalMonitor())!=0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:获取wifi总开关状态错误(%d)", Tools.getLineInfo(), iRet);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过", TESTITEM);
	}
	
	/**
	 * 写入文件
	 */
	public void writeResult(String s) 
	{
		String funcName="writeResult";
		String LOGFILE = GlobalVariable.sdPath + "wifiPrope.txt";
		if (new FileSystem().JDK_FsOpen(LOGFILE, "w") < 0) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d: 打开文件失败,请查看文件系统是否异常",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		} else {
			if (new FileSystem().JDK_FsWrite(LOGFILE, s.getBytes(),s.getBytes().length, 2) != s.getBytes().length) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写入文件失败...",Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}

		}
	}
	public void discovery() {
		mwifiP2PManager.discoverPeers(mChannel,
				new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {
						Log.e(TESTITEM, "discovery onSuccess !");
					}

					@Override
					public void onFailure(int reasonCode) {
						Log.e(TESTITEM, "discovery onFailure !");
					}
				});
	}
	@SuppressLint("NewApi")
	public void stopDiscovery() {
		mwifiP2PManager.stopPeerDiscovery(mChannel,
				new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.e(TESTITEM, "stopDiscovery onSuccess !");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.e(TESTITEM, "ostopDiscovery onFailure !");
			}
		});
	}
	/**
	 * 遍历hashset集合，转成字符串输出
	 */
	public String  setsToString(Set<String> probeDatas){
		StringBuffer s=new StringBuffer();
		for (String str : probeDatas) {
		      s.append(str+"\t\n");
		}
		return s.toString();
	}
	/**
	 * 记录时间
	 * @return
	 */
	private String getNowTime()
	{
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.CHINA);
		String str = time.format(new java.util.Date());
		return str;
	}
	public void pushToSet(String[] data){
		for(String s:data){
			
			if(s.length()<25){
				String[] mac=s.split(",");
				probeDatas.add(mac[0]);
			}
		}
	}
	public int seek(String[] data){
		for(String s:data){
			String[] temp=s.split(",");
			if(temp[0].equals(mac)){
				return 0;
			}
		}
		return -1;
	}
	/**
	 * 重开Wifi
	 */
	public void wifi_open_close(){
		mWifiManager.setWifiEnabled(false);// 关闭wifi
		synchronized (ReceiverTracker.lockListener) {
			try {
				ReceiverTracker.lockListener.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mWifiManager.setWifiEnabled(true);// 开启wifi
		synchronized (ReceiverTracker.lockListener) {
			try {
				ReceiverTracker.lockListener.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	class p2pTimeThread implements Runnable
	{
		private boolean istimeStop = false;
		@Override
		public void run() {
			while(istimeStop==false){
				try {
					discovery();
					Thread.sleep(30*1000);// 间隔30s进行一次discovery
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void setTimeStop(boolean isStop)
		{
			istimeStop = isStop;
		}
		
	}
	
	@Override
	public void onTestUp() {
		
		
	}

	@Override
	public void onTestDown() {
	}

}
