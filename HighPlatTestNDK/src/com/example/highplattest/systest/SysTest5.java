package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.newland.SettingsManager;
import android.os.SystemClock;
import android.provider.Settings;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest5.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150417
 * directory 		: 
 * description 		: BT综合测试
 * related document :
 * history 		 	: 变更记录														变更时间			变更人员
 *			  		 修改蓝牙流程压力测试，增加1s休眠。兼容不同平台的文件写入速度。防止蓝牙连接失败		20200513		陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest5 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	public final String TAG = SysTest5.class.getSimpleName();
	private final int DEFAULT_CNT_VLE1 = 500;/**读写压力次数*/
	private final int DEFAULT_CNT_VLE2 = 200;/**流程压力次数*/
	private final int DEFAULT_CNT_VLE3 = 1000;/**开关压力次数*/
	private final String TESTITEM = "BT性能、压力";
	private final int BUFSIZE_BT = 2048;/**2K的数据包*/
	private Gui gui = null;
	
	/*----------global variables declaration------------------------*/
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private BluetoothAdapter bluetoothAdapter;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private Config config;
	
	//BT综合测试主程序
	@SuppressLint("DefaultLocale") 
	public void systest5() 
	{
		gui = new Gui(myactivity, handler);
		SettingsManager settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		// 由于蓝牙所需要的权限包含Dangerous Permissions，所以需要动态授权处理
		config = new Config(myactivity, handler);
		bluetoothManager = BluetoothManager.getInstance(myactivity);//单例模式
		bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			
			//蓝牙配置
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr().toUpperCase().trim());// 蓝牙的MAC地址要为大写的状态传给底层
			//开关压力
			g_CycleTime =DEFAULT_CNT_VLE3;
			switchPre();
			//读写压力
			g_CycleTime =DEFAULT_CNT_VLE1;
			rwPre();
			//流程压力 (流程压力测试过程中时常会弹出配对框,故不放在自动化部分 modify by zhengxq 20180814)
			g_CycleTime =DEFAULT_CNT_VLE2;
			flowPre();
			//性能
			btFunction();
			return;
			
		}
		while(true)
		{
			int returnValue=gui.cls_show_msg("BT综合测试\n0.BT配置\n1.BT压力\n2.BT性能\n3.BT异常");	
			switch (returnValue) 
			{	
			case '0':
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				if(gui.cls_show_msg("选中的蓝牙：%s,是否要开启下拉状态栏，因为蓝牙配对对话框有时无法弹出,【确认】开启，【其他】关闭", DefaultFragment.g_btName)==ENTER)
				{
					/**BUG2020040902511:修改测试人员反馈有时无法配对的问题 20200615*/
					settingsManager.setStatusBarEnabled(0);
				}
				break;
				
			case '1':
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙配置");
					return;
				}
				btPrecess();
				break;
				
			case '2':
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙配置");
					return;
				}
				btFunction();
				break;
				
			case '3':// 蓝牙异常测试
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙配置");
					return;
				}
				btAbnormal();
				break;			
				
			case ESC:
				if(GlobalVariable.gCustomerID!=CUSTOMER_ID.BRASIL)
				{
					settingsManager.setStatusBarEnabled(1);/**测试后置，禁止下拉，巴西的默认下拉，不需要关闭*/
				}
				intentSys();
				return;
			}
		}
	}
	
	//蓝牙压力
	private void btPrecess() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("BT压力测试\n0.开关压力\n1.读写压力\n2.流程压力");
			switch (returnValue) 
			{
			
			case '0':
				switchPre();
				break;
				
			case '1':
				rwPre();
				break;
				
			case '2':
				flowPre();
				break;
				
			case ESC:
				return;

			}
		}
	}
	
	// add by 20150419
	//开关压力
	private void switchPre() 
	{
		/*private & local definition*/
		int succ = 0,cnt=0,bak =0;
		boolean ret;
		
		/*process body*/
		// 设置开关压力的次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag){
			packet.setLifecycle(getCycleValue());
		}else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE3));
		bak = cnt = packet.getLifecycle();
		// 如果蓝牙已启动，关闭重启
		if(bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "BT开关压力测试中...\n还剩%d次(已成功%d次),[取消]退出测试...", cnt,succ)==ESC)
				break;
			cnt--;
			// 打开蓝牙操作
			if((ret = bluetoothAdapter.enable())!=true)
			{
				gui.cls_show_msg1_record(TAG, "switchPre", g_keeptime,"line %d:第%d次:BT打开失败(ret = %s)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			SystemClock.sleep(3000);
			if((ret = bluetoothAdapter.disable())!=true)
			{
				bluetoothManager.setDownBluetooth();
				gui.cls_show_msg1_record(TAG, "switchPre", g_keeptime,"line %d:第%d次:BT打开失败(ret = %s)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			SystemClock.sleep(2000);
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "switchPre", g_time_0,"开关压力测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
	}
	// end by 20150419
	
	
	/**
	 * 蓝牙读写压力
	 */
	private void rwPre() 
	{
		/*private & local definition*/
		int succ = 0,cnt=0,bak =0;
		byte[] rbuf = new byte[512];
		byte[] wbuf = new byte[512];
		boolean ret;
		
		/*process body*/
		// 设置开关压力的次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag){
			packet.setLifecycle(getCycleValue());
		}else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE1));
		bak = cnt = packet.getLifecycle();
		// 测试前置，关闭蓝牙
		if(bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		// 打开蓝牙以及配对操作
		if ((ret = bluetoothAdapter.enable()) != true) 
		{
			gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:第%d次:BT打开失败(ret = %s)",Tools.getLineInfo(), bak - cnt, ret);
			return;
		}
		SystemClock.sleep(3000);
		if(GlobalVariable.gAutoFlag != ParaEnum.AutoFlag.AutoFull)
			gui.cls_show_msg("请先打开已配置手机端的bluetoothServer蓝牙工具，完成点任意键继续");
		
		int time = 3;
		while(time>=0)/**真正与服务端连接成功才可视为连接上*/
		{
			time--;
			byte[] tempBuf = new byte[5];
			// 建立连接
			if((ret = bluetoothManager.connComm(dataService,CHANEL_DATA))==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line:%d:BT连接建立失败", Tools.getLineInfo());
				continue;
			}
			
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:客户端接收服务器hello数据失败", Tools.getLineInfo());
				continue;
			}
			
			if(new String(tempBuf).contains("hello"))
			{
				break;
			}
		}
		
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "BT读写压力测试中，还剩%d次(已成功%d次),[取消]退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
				
			for (int i = 0; i < wbuf.length; i++) 
			{
				wbuf[i] = (byte) (Math.random()*128);
			}
			// 写数据到服务器端
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			
			if(Tools.memcmp(wbuf, rbuf, BUFSIZE_BT)==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line:%d:第%d次:BT比较数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			succ++;
		}
		// 断开Socket连接
		bluetoothManager.cancel(dataService);
		if(bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		gui.cls_show_msg1_record(TAG, "rwPre",g_time_0, "读写压力测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
	}
	
	//流程压力
	private void flowPre()
	{
		/*private & local definition*/
		int i =0,cnt = 0,succ = 0;
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/	
		if(GlobalVariable.gSequencePressFlag)
		{
			sendPacket.setLifecycle(getCycleValue());
		}else
			sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2));
		cnt = sendPacket.getLifecycle();
		gui.cls_show_msg("请先打开已配置手机端的bluetoothServer蓝牙工具，完成点任意键继续");
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "BT流程压力测试,第%d次压力，成功为%d次,[取消]退出测试...", ++i,succ)==ESC)
				break;
			cnt--;
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(5000);
			}
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG, "flowPre", g_keeptime, "line %d:第%d次打开蓝牙串口失败", Tools.getLineInfo(),i);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "flowPre",g_keeptime, "line %d:第%d次:BT连接建立失败",Tools.getLineInfo(), i);
				continue;
			}
			
			byte[] tempBuf = new byte[5];
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),i);
				continue;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
				continue;
			}
			
			
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG,"flowPre",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),i);
				bluetoothManager.cancel(dataService);
				continue;
			}
			
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG,"flowPre",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),i);
				bluetoothManager.cancel(dataService);
				continue;
			}
			
			if(Tools.memcmp(wbuf, rbuf, BUFSIZE_BT)==false)
			{
				gui.cls_show_msg1_record(TAG, "flowPre", g_keeptime, "line:%d:第%d次数据校验失败,发送数据(%s),接收数据(%s)", Tools.getLineInfo(),i,
						Arrays.toString(wbuf),Arrays.toString(rbuf));
				bluetoothManager.cancel(dataService);
				continue;
			}
			succ++;
			// 断开socket的连接，服务器也会相应断开
			bluetoothManager.cancel(dataService);
			
//			BluetoothDevice blueDevice = bluetoothAdapter.getRemoteDevice(getBtAddr());
//			// 移除配对操作
//			try {
//				ClsUtils.removeBond(blueDevice.getClass(),blueDevice);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			//根据开发建议，不同平台写入文件速度不一致，休眠1s，防止文件未写入完毕就执行到断开蓝牙连接  by chending 20200506
			SystemClock.sleep(1000);
			// 断开蓝牙连接
			bluetoothAdapter.disable();
			SystemClock.sleep(5000);
		}
		gui.cls_show_msg1_record(TAG, "flowPre", g_time_0,"蓝牙流程压力测试完成，执行次数:%d次，成功%d次", i,succ);
	}
	
	private void createBtAbilityPacket(PacketBean sendPacket,byte[] buf)
	{
		sendPacket.setHeader(buf);
		sendPacket.setLen(buf.length);
		sendPacket.setOrig_len(buf.length);
		sendPacket.setLifecycle(DEFAULT_CNT_VLE1);
		sendPacket.setForever(false);
		sendPacket.setIsLenRec(false);
		sendPacket.setIsDataRnd(true);
	}
	
	/**
	 * 蓝牙性能
	 */
	private void btFunction() 
	{
		/*private & local definition*/
		long startTime;
		float commtimes = 0,rate = 0;
		byte[] rbuf = new byte[2048];
		byte[] wbuf = new byte[2048];
		
		/*process body*/
		// 重新进行蓝牙的开启动作
		final PacketBean packet = new PacketBean();
		createBtAbilityPacket(packet, rbuf);
		if(!bluetoothAdapter.isEnabled())
		{
			if(!bluetoothAdapter.enable())
			{
				gui.cls_show_msg1_record(TAG, "btFunction",g_keeptime,"line %d:打开蓝牙失败(ret = %s)", Tools.getLineInfo(),false);
				return;
			}
		}
		int time = 3;
		SystemClock.sleep(3000);
		if(GlobalVariable.gAutoFlag != ParaEnum.AutoFlag.AutoFull)
			gui.cls_show_msg("请先打开已配置手机端的bluetoothServer蓝牙工具，完成点任意键继续");
		
		boolean ret;
		while(time>=0)/**真正与服务端连接成功才可视为连接上*/
		{
			time--;
			byte[] tempBuf = new byte[5];
			// 建立连接
			if((ret = bluetoothManager.connComm(dataService,CHANEL_DATA))==false)
			{
				gui.cls_show_msg1_record(TAG, "btFunction",g_keeptime, "line:%d:BT连接建立失败(ret = %s)", Tools.getLineInfo(),ret);
				continue;
			}
			
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "btFunction",g_keeptime, "line %d:客户端接收服务器hello数据失败", Tools.getLineInfo());
				continue;
			}
			
			if(new String(tempBuf).contains("hello"))
			{
				break;
			}
		}
		gui.cls_printf("进行蓝牙读写性能测试,请耐心等待".getBytes());
		while(true)
		{
			if(update_snd_packet(packet, LinkType.BT)!=NDK_OK)
				break;
			// 等待rfcomm连接建立
			startTime = System.currentTimeMillis();
			for (int j = 0; j < wbuf.length; j++) 
				wbuf[j] = 0x49;
			// 写数据操作
			bluetoothManager.writeComm(dataService,wbuf);
			// 读数据操作
			bluetoothManager.readComm(dataService,rbuf);
			if(Tools.memcmp(wbuf, rbuf, wbuf.length)==false)
			{
				gui.cls_show_msg1_record(TAG, "btFunction",g_keeptime, "line:%d比较数据失败(%s)", Tools.getLineInfo(),false);
				return;
			}
			commtimes = (float) (commtimes+Tools.getStopTime(startTime));
		}
		bluetoothManager.cancel(dataService);
		if(bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		// 包括了发送和接收
		rate = packet.getLen()*2*DEFAULT_CNT_VLE1/(1024*commtimes);
		gui.cls_show_msg1_record(TAG, "btFunction", g_time_0, "蓝牙读写通讯速率为%4.2fKB/S", rate);
	}
	
	//蓝牙异常
	private void btAbnormal() 
	{
		int sleepTime = Tools.getSreenTimeout(myactivity);
//		SettingsManager settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("BT异常测试\n1.异常1\n2.异常2\n3.异常3\n4.异常4\n5.异常5\n6.异常6");
			switch (returnValue) 
			{
			case '1':
				abnormal1();
				break;
				
			case '2':
				abnormal2();
				break;
				
			case '3':
				abnormal3();
				break;
				
			case '4':
				abnormal4();
				break;
				
			case '5':
				abnormal5();
				break;
				
			case '6':
				abnormal6();
				break;
		
			case ESC:
				Tools.setSreenTimeout(myactivity, sleepTime);
//				settingsManager.setScreenTimeout(-1);
//				Settings.System.putInt(myactivity.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,-1);
				return;
			}
		}
	}
	
	// case1:取消配对，n900只支持SSP模式，再次配对能够连接上
	private void abnormal1() 
	{
		int ret = -1;
		
		gui.cls_printf("case1:请打开已配置的手机端蓝牙,并与本设备进行蓝牙配对,出现配对对话框时点取消,当提示未连接时继续".getBytes());
		openBTRemove();
		pair(getBtAddr());
		// 取消配对
		gui.cls_show_msg("请点击POS端与手机端配对框的[取消]按钮,完成任意键继续");
		if((ret = bluetoothManager.getBondState(getBtAddr()))==BluetoothDevice.BOND_BONDED)
		{
			gui.cls_show_msg1_record(TAG, "abnormal1", g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			postEnd();
			return;
		}
		
		// 配对
		gui.cls_printf("case2:请打开已配置的手机端蓝牙,并与本设备进行蓝牙配对,当设备弹出配对对话框请进行点击配对按钮".getBytes());
		pair(getBtAddr());
		gui.cls_show_msg("请点击配对对话框的[配对]按钮,完成点任意键继续");
		if((ret = bluetoothManager.getBondState(getBtAddr())) != BluetoothDevice.BOND_BONDED)
		{
			gui.cls_show_msg1_record(TAG, "abnormal1", g_keeptime,  "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			postEnd();
			return;
		}
		postEnd();
		gui.cls_show_msg1_record(TAG, "abnormal1",g_time_0, "蓝牙异常测试1测试通过");
	}
	
	// 异常测试后置的操作
	private void postEnd()
	{
		bluetoothAdapter.disable();
        SystemClock.sleep(2000);
	}
	
	// case2:在有效通信范围内，长时间放置机器后，机器进入休眠，再使用蓝牙功能，应能连接成功；
	private void abnormal2() 
	{
		int ret = -1;
		byte[] wbuf = new byte[BUFSIZE_BT];
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] tempBuf = new byte[5];
		
		/*process body*/
		openBTRemove();
		// 无法获取到目前的休眠时间，故无法进行测试后置
		// 设置一分钟自动进入休眠
//		settingsManager.setScreenTimeout(ONE_MIN);
		Tools.setSreenTimeout(myactivity, ONE_MIN);
//		Settings.System.putInt(myactivity.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,ONE_MIN);
		// 蓝牙配对
		gui.cls_printf("case1:请与本设备进行蓝牙配对,当设备弹出配对对话框请进行点击配对按钮".getBytes());
		pair(getBtAddr());
		gui.cls_show_msg("请点击POS端和手机端配对对话框的[配对]按钮,完成点任意键继续");
		if((ret = bluetoothManager.getBondState(getBtAddr())) != BluetoothDevice.BOND_BONDED)
		{
			gui.cls_show_msg1_record(TAG, "abnormal2", g_keeptime,  "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			postEnd();
			return;
		}
		gui.cls_show_msg("请打开手机端的bluetoothServer蓝牙工具,长时间放置机器等待设备休眠，休眠后进行蓝牙收发测试\n操作完毕任意键继续");
		gui.cls_printf("等待设备自动休眠中...".getBytes());
		// 休眠后进行BT连接操作
		GlobalVariable.isWakeUp=true;
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<60)
		{
			if(GlobalVariable.isWakeUp==false)
				break;
			SystemClock.sleep(100);
		}
		gui.cls_show_msg2(0.5f, "设备已休眠：连接服务器中...");
		LoggerUtil.d(TAG+"->abnormal2==="+GlobalVariable.isWakeUp);
		if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:BT建立连接失败", Tools.getLineInfo());
			return;
		}
		
		// 等待服务器端发送的hello数据后才往下走
		gui.cls_show_msg2(0.5f, "设备已休眠：等待服务器的数据...");
		if(bluetoothManager.readComm(dataService,tempBuf)==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:客户端接收服务器hello数据失败", Tools.getLineInfo());
			return;
		}
		
		if(new String(tempBuf).contains("hello")==false)
		{
			return;
		}
        // 配对完成，进行数据收发测试
        for (int i = 0; i < wbuf.length; i++) 
        {
        	wbuf[i] = (byte) (Math.random()*256);
		}
        if( bluetoothManager.writeComm(dataService,wbuf)==false)
        {
        	gui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:BT写数据失败", Tools.getLineInfo());
        	return;
        }
		if(bluetoothManager.readComm(dataService,rbuf)==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:BT读数据失败", Tools.getLineInfo());
			return;
		}
		if(!Tools.memcmp(wbuf, rbuf, 512))
		{
			gui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line:%d:比较数据失败，发送数据（%s），接收数据（%s）", 
					Tools.getLineInfo(),Arrays.toString(wbuf),Arrays.toString(rbuf));
			return;
		}
		bluetoothManager.cancel(dataService);
		postEnd();
        gui.cls_show_msg1_record(TAG, "abnormal2", g_time_0,"蓝牙异常测试2通过");
	}
	
	// case3:出现配对提示框时将POS机重启或关机,再次运行蓝牙用例不应出现异常
	private void abnormal3() 
	{
		/*process body*/
		openBTRemove();
		gui.cls_show_msg1(2, "请打开已配置的手机端蓝牙,并与本设备进行蓝牙配对,弹出配对对话框时请将机器重启或关机,重启后执行正常蓝牙测试应正常");
		pair(getBtAddr());
		while(true)
		{
			if(bluetoothManager.getBondState(getBtAddr())==BluetoothDevice.BOND_BONDING)
			{
				gui.cls_show_msg1(2, "请立刻重启或关机,关机后执行正常用例正常视为本条通过");
				break;
			}
		}
	}
	
	// case4::在成功连接到手机后，POS重启或关机，重启后蓝牙应仍能正常工作；
	private void abnormal4() 
	{
		int ret = -1;

		/*process body*/
		openBTRemove();
		// 蓝牙配对
		gui.cls_printf("case1:请打开已配置的手机端蓝牙,并与本设备进行蓝牙配对,当设备弹出配对对话框请进行点击配对按钮".getBytes());
		pair(getBtAddr());
		gui.cls_show_msg("请点击POS端和手机端配对对话框的[配对]按钮,任意键继续");
		if((ret = bluetoothManager.getBondState(getBtAddr())) != BluetoothDevice.BOND_BONDED)
		{
			gui.cls_show_msg1_record(TAG, "abnormal4", g_keeptime,"line %d:%sBT配对连接测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			postEnd();
			return;
		}
		postEnd();
		gui.cls_show_msg1(2, "机器将重启,重启后执行正常蓝牙测试应正常");
		Tools.reboot(myactivity);
	}
	
	// case5:整机休眠后（不论整机休眠是否关闭蓝牙），（手机端）应能搜索到设备（蓝牙），
	// 并成功进行配对与连接 或 （手机端）通过与蓝牙模块的各种交互（包括：（配对）连接、数据通讯、断开等方式）应能唤醒整机；
	public void abnormal5() 
	{
		byte[] rbuf = new byte[12];
		byte[] sbuf = new byte[12];
		byte[] tempBuf = new byte[5];
		
		/*process body*/
		openBTRemove();
		// 设置休眠时间1分钟
//		settingsManager.setScreenTimeout(ONE_MIN);
		Settings.System.putInt(myactivity.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,ONE_MIN);
		// 休眠进行配对应能唤醒机器，配对时间限制为60s，配对、收发数据操作无法唤醒设备 by 20170320
		gui.cls_show_msg("请打开已配置手机端的bluetoothServer蓝牙工具,机器1分钟后自动休眠后,休眠后使用手机端[设置]与本设备进行蓝牙配对操作");
		// 进行收发数据测试，应接收到12个8
		if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "line %d:BT建立连接失败", Tools.getLineInfo());
			postEnd();
			return;
		}
		
		// 等待服务器端发送的hello数据后才往下走
		if(bluetoothManager.readComm(dataService,tempBuf)==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "line %d:客户端接收服务器hello数据失败", Tools.getLineInfo());
			return;
		}
		
		if(new String(tempBuf).contains("hello")==false)
		{
			return;
		}
		gui.cls_show_msg("使用手机端服务器发送12个8给POS端,完成任意键继续");
		Arrays.fill(sbuf, (byte) 0x38);
		if(bluetoothManager.readComm(dataService,rbuf)==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "line %d:BT接收数据失败", Tools.getLineInfo());
			postEnd();
			return;
		}
		if(!Tools.memcmp(sbuf, rbuf, sbuf.length))
		{
			gui.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "line %d:蓝牙异常测试5测试失败%s", Tools.getLineInfo(),ISOUtils.hexString(rbuf));
			postEnd();
			return;
		}
		gui.cls_show_msg1(1, "子用例2测试通过");
		postEnd();
		gui.cls_show_msg1_record(TAG, "abnormal5",g_time_0, "蓝牙异常测试5通过,请将POS机重启或者关机操作");
	}
	
	// case6:整机休眠唤醒后(不论是何种唤醒方式,如:按键唤醒、蓝牙唤醒等),再重新进行蓝牙连接应成功(整机休眠关闭蓝牙的情况) 或 直接进行蓝牙数据通讯成功(整机休眠不关闭蓝牙的情况)
	public void abnormal6() 
	{
		byte[] wbuf = new byte[12];
		byte[] rbuf = new byte[12];
		byte[] tempBuf = new byte[5];
		int ret=-1;
		
		/*process body*/
		// 测试前置，配对操作
		gui.cls_show_msg("请打开已配置手机端的bluetoothServer蓝牙工具\n完成[确认]");
		openBTRemove();
		pair(getBtAddr());
		
		gui.cls_show_msg("请点击POS端与手机端的配对对话框的[配对]按钮,完成任意键继续");
		if((ret = bluetoothManager.getBondState(getBtAddr()))!=BluetoothDevice.BOND_BONDED)
		{
			gui.cls_show_msg1_record(TAG, "abnormal6",g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			postEnd();
			return;
		}
		
		/*String[] listName = {"0.按键唤醒","1.蓝牙唤醒"};
		title = "BT异常6";
		list_press(HandlerMsg.DIALOG_SYSTEST_LIST, listName);
		// 按键唤醒、蓝牙唤醒
		switch (GlobalVariable.RETURN_VALUE) 
		{
		case 0:*/
			// 只有按键唤醒的功能
			gui.cls_printf("等待POS端自动进入休眠,休眠后按电源键唤醒".getBytes());
			GlobalVariable.isWakeUp = false;
			while(!GlobalVariable.isWakeUp);
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal6",g_keeptime, "line %d:BT建立连接失败", Tools.getLineInfo());
				postEnd();
				return;
			}
			
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "line %d:客户端接收服务器hello数据失败", Tools.getLineInfo());
				return;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
				return;
			}
	        // 配对完成，进行数据收发测试
	        Arrays.fill(wbuf, (byte) 0x38);
	        if(bluetoothManager.writeComm(dataService,wbuf)==false)
	        {
	        	gui.cls_show_msg1_record(TAG, "abnormal6",g_keeptime, "line %d:BT发送数据失败", Tools.getLineInfo());
	        	postEnd();
	        	return;
	        }
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal6",g_keeptime, "line %d:BT接收数据失败", Tools.getLineInfo());
				postEnd();
				return;
			}
			if(!Tools.memcmp(wbuf, rbuf, 12))
			{
				gui.cls_show_msg1_record(TAG, "abnormal6",g_keeptime, "line:%d:比较数据失败，发送数据(%s),接收数据(%s)", Tools.getLineInfo(),
						ISOUtils.hexString(wbuf),ISOUtils.hexString(rbuf));
				postEnd();
				return;
			}
//			break;
			
		/*case 1:
			gui.cls_show_msg(1, "请打开已配置手机端的bluetoothServer蓝牙工具，按电源键进入休眠，休眠用bluetoothServer工具发送12个8，数据发送完毕应可唤醒设备");
			GlobalVariable.isWakeUp = true;
			while(GlobalVariable.isWakeUp);
			if(bluetoothManager.connCommOnly(getBtAddr())==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:BT建立连接失败", Tools.getLineInfo());
				postEnd();
				return;
			}
			GlobalVariable.isWakeUp = true;
			if(bluetoothManager.readComm(rbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:BT接收数据失败", Tools.getLineInfo());
				postEnd();
				return;
			}
			
			Arrays.fill(memBuf, (byte) 0x38);
			if(!Tools.memcmp(rbuf, memBuf, memBuf.length))
			{
				gui.cls_show_msg1(g_keeptime, "line %d:BT数据校验失败，发送数据（%s），接收数据（%s）", Tools.getLineInfo(),
						Arrays.toString(memBuf),Arrays.toString(rbuf));
				postEnd();
				return;
			}
			show_flag(HandlerMsg.DIALOG_COM_SYSTEST, "BT数据收发是否可唤醒已休眠的设备");
			if(!GlobalVariable.FLAG_SYSTEM_SIGN)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:收发数据无法唤醒已休眠的设备", Tools.getLineInfo());
				postEnd();
				return;
			}
			break;

		default:
			break;
		}*/
		
		bluetoothManager.cancel(dataService);
		postEnd();
		gui.cls_show_msg1_record(TAG, "abnormal6",g_time_0, "子用例测试通过");
	}
	
	/**
	 * 打开蓝牙设备并且移除已配对设备
	 */
	public void openBTRemove()
	{
		// 确保蓝牙是打开的
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		bluetoothAdapter.enable();
		SystemClock.sleep(2000);
		// 测试前置：删除原先的配对
		Set<BluetoothDevice> pairedDevices = bluetoothManager.queryPairedDevices();
		for (BluetoothDevice device : pairedDevices) {
			try 
			{
				ClsUtils.removeBond(device.getClass(), device);
				SystemClock.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void onDestroy() 
	{
		super.onDestroy();
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		bluetoothAdapter.disable();
	};
}
