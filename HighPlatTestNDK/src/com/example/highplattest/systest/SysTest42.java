package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.AcceptThread;
import com.example.highplattest.main.btutils.BlueBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Dis_Type;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DesTool;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest42.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160303
 * directory 		: 
 * description 		: Dongle综合测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  zhengxq			20160303
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest42 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "Dongle综合测试(单通道)";
	public final String TAG = SysTest42.class.getSimpleName();
	private final int PACKLEN = 1024;
//	private String dongleAddr = "";
	private boolean isSleepAbnormal = false;
	
	/*----------global variables declaration------------------------*/
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	// 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Config config;
//	private Dialog dialogBtList;
	private Gui gui = new Gui(myactivity, handler);
	private BluetoothService dataService;
	private BlueBean bean;
/*	Handler mHander = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case 0:
				dialogBtList = config.showBtList(pairList);
				break;

			default:
				break;
			}
		};
	};*/
	
	public void systest42()
	{
//		gui.cls_show_msg1(0, "%s测试中...",TESTITEM);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "systest42", g_keeptime,"%s不支持自动测试,请使用手动测试验证", TESTITEM);
			return;
		}
		bean=new BlueBean();
		bluetoothManager = BluetoothManager.getInstance(myactivity,this);
		GlobalVariable.isDongle = true;
		config = new Config(myactivity, handler);
		while(true)
		{
			int returnValue=gui.cls_show_msg("Dongle综合测试\n0.配置\n1.功能\n2.压力\n3.性能\n4.异常\n5.退出测试");
			switch (returnValue) 
			{
			// Dongle配置
			case '0':
				// 配置之前先移除配对
				config.btConfig(pairList, unPairList, bluetoothManager);
				if(unPairList.size()!=0)	{
					// 这个时候已经选择好蓝牙底座了,配置完毕直接进行配对
					dataService = new BluetoothService(getBtAddr());
					bluetoothManager.pair(getBtAddr(), "0");		
				}	
				else
				{
					gui.cls_show_msg1(2, "未搜索到蓝牙底座");
					dialog.dismiss();
					return;
				}
				break;
				
			// dongle功能
			case '1':
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙底座配置");
					return;
				}
				dongleFunction();
				break;
				
			// dongle压力
			case '2':
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙底座配置");
					return;
				}
				preFun();
				break;
				
				// dongle性能
			case '3':
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙底座配置");
					return;
				}
				dongleAbility();
				break;
				
			case '4':
				dongleAbnormal();
				break;
				
			case '5':
				GlobalVariable.isDongle = false;
				intentSys();
				break;
				
				
			case ESC:
				intentSys();
				return;
				
			}
		}
		
	}
	
	
	// 移除蓝牙配对
	public void removePair()
	{
		Set<BluetoothDevice> devicesPairs = bluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : devicesPairs) 
		{
			try 
			{
				ClsUtils.removeBond(device.getClass(), device);
				SystemClock.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*// 蓝牙配置操作
	public boolean btConfig() 
	{
		Gui gui = new Gui(getActivity(), handler);
		gui.cls_show_msg(0, "正在进行蓝牙设备搜索,请先对蓝牙底座复位");
		if(!bluetoothAdapter.isEnabled())
			bluetoothAdapter.enable();
		SystemClock.sleep(2000);
		unPairList.clear();
		
		// 移除配对设备
		Set<BluetoothDevice> devicesPairs = bluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : devicesPairs) 
		{
			try 
			{
				ClsUtils.removeBond(device.getClass(), device);
				SystemClock.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mHander.sendEmptyMessage(0);
		detectBTDevice(bluetoothManager);
		GlobalVariable.PORT_FLAG = true;
		while(GlobalVariable.PORT_FLAG);
		if(unPairList.size()>=1)
		{
			dongleAddr = config.getAddress();
			return pair(dongleAddr);
		}
		else if(unPairList.size()==0)
		{
			gui.cls_show_msg(1, "未搜索到匹配蓝牙底座");
			dialogBtList.dismiss();
		}
		return false;
	}*/
	
	// Dongle功能
	public void dongleFunction() 
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("Dongle功能测试\n0.回连功能\n1.重配对功能\n2.合法性验证\n3.状态指示\n4.数据转发\n");
		switch (nkeyIn) 
		{
		// 回连功能
		case '0':
			dongleBack();
			break;
			
		// 重配对
		case '1':
			dongleRepair();
			break;
			
		// 合法性验证
		case '2':
			dongleLegality("请先将蓝牙底座复位");
			break;
			
		// 状态指示
		case '3':
			dongleState();
			break;
		
		// 数据转发
		case '4':
			dongleData();
			break;
			
		case ESC:
			return;
		}
		}
	}
	
	// add by 20160506
	// 重配对
	public void dongleRepair() 
	{
		int nkeyIn = gui.cls_show_msg("Donglec重配对\n0.底座\n1.N900\n");
		switch (GlobalVariable.RETURN_VALUE) 
		{
		case '0':
			repairDongle();
			break;
			
		case '1':
			repairN900();
			break;

		case ESC:
			return;
		}
	}
	
	// add by 20160506
	public void repairDongle() 
	{
		/* private & local definition */
		byte[] wbuf = new byte[PACKLEN];
		
		/* process body */
		// 测试前置,复位之前建立连接
		// 进行第一次配对连接
		dongleLegality("请将蓝牙底座复位,已复位可忽略");
		// 此时已具有回连功能,复位重新配对,应无法回连
		gui.cls_show_msg("再次点击复位按钮");
		// case1:复位后应断开与N900手机的连接
		Arrays.fill(wbuf, (byte) 0x38);
		bluetoothManager.writeComm(dataService,wbuf);
		
		// case2:复位后停止自动回连   
		new AcceptThread(dataService,UUID_DATA,bean).start();
		gui.cls_show_msg1(0, "若提示回连成功则视为测试不通过");
		
		// case3:复位后可实现重配对
		dongleLegality("请将蓝牙底座复位,已复位可忽略");
		gui.cls_show_msg1(1, "重配对子用例1测试通过");
		
	}
	
	// add by 20160506
	// 蓝牙底座默认通讯距离为10M
	public void repairN900() 
	{
		/* private & local definition */
		int len = 10;
		
		/* process body */
		// case1:复位之后重新搜索蓝牙,应能成功搜索并配对成功(需验证不同距离就能搜索成功)
		for (int i = 0; i < len; i++) 
		{
			gui.cls_show_msg("点击复位按钮,设备和蓝牙底座的距离为"+(i+1)+"m,完成点任意键");
			if(pair(getBtAddr())==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:蓝牙配对失败", Tools.getLineInfo());
				continue;
			}
			// 移除配对
			removePair();
		}
		gui.cls_show_msg1(1, "重配对子用例2测试通过");
	}
	
	// add by 20160506
	// 状态指示变化
	public boolean dongleState()
	{
		/* private & local definition */
		
		/* process body */
		// case1:广播状态：蓝灯每隔0.5s闪烁一次
		gui.cls_show_msg("未复位点击复位按钮,已复位忽略");

		if(gui.ShowMessageBox(("蓝灯是否每隔0.5s闪烁一次").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:指示灯亮灭时间与预期不符", Tools.getLineInfo());
			return false;
		}
		
		// case2:连接状态,蓝灯常亮,可以和N900进行数据通信
		gui.cls_show_msg1(1, "正在进行连接");
		dongleLegality("未复位点击复位按钮,已复位忽略");
		if(gui.ShowMessageBox(("蓝灯常亮是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s状态指示测试失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		
		// case3:连接状态复位应变为广播状态
		gui.cls_show_msg1(1, "正在进行复位操作");
		if(gui.ShowMessageBox(("请点击复位按钮,蓝灯是否每隔0.5s闪烁一次").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s状态指示测试失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		
		// case4:回连状态点击复位应变为广播状态
		gui.cls_show_msg1(1, "正在进行连接-复位操作");
		dongleLegality("未复位点击复位按钮,已复位忽略");
		bluetoothManager.cancel(dataService);
		if(gui.ShowMessageBox(("蓝灯每隔2s亮一次后点击复位按钮,蓝灯是否变为每隔0.5s闪烁一次").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s状态指示测试失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		
		// case5:回连状态：蓝灯每隔2s亮一次,主动连接N900
		gui.cls_show_msg1(1, "正在POS端主动断开蓝牙连接");
		dongleLegality("未复位点击复位按钮,已复位忽略");
		// 主动断开连接
		bluetoothManager.cancel(dataService);
		if(gui.ShowMessageBox(("蓝灯是否每隔2s亮一次").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s状态指示测试失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		// case6:回连成功后变为连接状态,连接状态后底座断开
		// 休眠异常操作 by zhengxq 20170222
		if(isSleepAbnormal)
		{
			gui.cls_show_msg("请手动进入休眠,休眠放置3分钟后,手动唤醒并继续测试,完成点任意键");
		}
		gui.cls_show_msg1(1, "正在进行回连测试,长时间未回连即为失败");
		long startTime = System.currentTimeMillis();
		new AcceptThread(dataService,UUID_DATA,bean).start();
		while(Tools.getStopTime(startTime)<30)
		{
			if(bean.isIsdateback() == true)
			{
				gui.cls_show_msg1(2, "数据通道回连成功");
				break;
			}
		}
		if(bean.isIsdateback()==false)
		{
			gui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
			return false;
		}
		if(gui.ShowMessageBox(("蓝灯常亮是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s状态指示测试失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		// 连接状态下进行简单的数据收发操作,验证此连接状态是可靠的  by zhengxq 20170222
		if(dataSenRecv()==false)
		{
			return false;
		}
		// 连接状态下进入休眠,休眠3分钟唤醒,仍可以进行数据收发操作 by zhengxq 20170222
		if(isSleepAbnormal)
		{
			gui.cls_show_msg("请手动进入休眠,休眠放置3分钟后,手动唤醒并继续测试,完成点任意键");
		}
		if(dataSenRecv()==false)
		{
			return false;
		}
		// case7:进入工厂模式状态
		gui.cls_show_msg1(2, "工厂模式状态测试...");
		if(gui.ShowMessageBox(("连接MICRO USB_ID线,进入工厂模式,是否为蓝灯亮2s,灭2s,重复闪烁").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s状态指示测试失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		gui.cls_show_msg1(1, "状态指示测试通过");
		return true;
	}
	
	/**
	 * POS与底座数据收发测试
	 * @return
	 */
	public boolean dataSenRecv()
	{
		/* private & local definition */
		byte[] rbuf = new byte[2047];
		byte[] wbuf = new byte[2047];
		boolean ret;
		gui.cls_show_msg("请将comEcho串口工具打开,2047数据转发测试中");
		// 收发数据
		Arrays.fill(wbuf, (byte) 0x33);
		gui.cls_show_msg1(2, "发送数据中,请耐心等待...");
		if((ret =bluetoothManager.writeComm(dataService,wbuf))==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:POS发送数据失败(ret = %s)", Tools.getLineInfo(),ret);
			return ret;
		}
		gui.cls_show_msg1(2, "接收数据中,请耐心等待...");
		if((ret = bluetoothManager.readComm(dataService,rbuf))==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:POS接收数据失败(ret = %s)", Tools.getLineInfo(),ret);
			return ret;
		}
		gui.cls_show_msg1(2, "收发数据完毕,校验数据中");
		if(!Tools.memcmp(wbuf, rbuf, wbuf.length))
		{
			gui.cls_show_msg1(g_keeptime, "line %d:%s比较数据失败", Tools.getLineInfo(),TESTITEM);
			return false;
		}
		return true;
	}
	
	// add by 20160513
	// 数据转发
	public void dongleData() 
	{
		/* private & local definition */
		byte[] rbuf = new byte[2047];
		byte[] wbuf = new byte[2047];
		
		/* process body */
		// 配对连接操作
		dongleLegality("请将comEcho串口工具打开,2047数据转发测试中,未复位请先复位");
		gui.cls_show_msg1(2, "PC串口工具将收到2047B数据");
		if(dataSenRecv())
		{
			bluetoothManager.cancel(dataService);
			return;
		}
		bluetoothManager.cancel(dataService);
		gui.cls_show_msg1(g_keeptime, "数据转发测试通过");
	}
	
	// 合法性验证
	public void dongleLegality(String msgTip)
	{
		/* private & local definition */
		byte[] rbuf = new byte[8];
		byte[] backBuf = new byte[10];
		byte[] timeBuf = new byte[8];
		byte[] helloBuf = {0x68,0x65,0x6C,0x6C,0x6F,0x00,0x00,0x00};
		byte[] okBuf = {0x6F,0x6B,0x21,0x00,0x00,0x00,0x00,0x00};
		
		/* process body */
		gui.cls_show_msg(msgTip);
		// 连接蓝牙底座
		// 配对连接操作
		if(pair(getBtAddr())==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:蓝牙配对失败",Tools.getLineInfo());
			return;
		}
		if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:POS与底座建立链路失败", Tools.getLineInfo());
			return;
		}
		/*if (bluetoothManager.getConnStatus() != BluetoothDevice.ACTION_ACL_CONNECTED) 
		{
			gui.cls_show_msg1(g_keeptime, "line %d:建立连接失败", Tools.getLineInfo());
			return;
		}*/
		gui.cls_show_msg1(1, "等待底座主动发送数据");
		// 收发数据
		if(bluetoothManager.readComm(dataService,backBuf)==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:POS发数据给底座失败", Tools.getLineInfo());
			return;
		}
		// 比较接收到的数据是不是以 0xfe 0x01
		if(!Tools.memcmp(backBuf, new byte[]{(byte) 0xFE,0x01}, 2))
		{
			gui.cls_show_msg1(g_keeptime, "line %d:从底座接收的数据格式错误ret = %s", Tools.getLineInfo(),Dump.getHexDump(backBuf));
			return;
		}
		
		/*// 休眠唤醒异常测试,唤醒后再发加密数据
		if(isSleepAbnormal)
		{
			show_flag(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, "手动快速休眠唤醒设备,完成点击是");
		}*/
		LoggerUtil.e(Dump.getHexDump(backBuf));
		// 生成密钥以及加密的hello键
		System.arraycopy(backBuf, 2, timeBuf, 0, 4);
		byte[] desKey = generalDesKey(timeBuf);
		gui.cls_printf("发送加密的hello数据".getBytes());
		byte[] helloData = DesTool.encrypt(desKey, helloBuf);
		byte[] sendData = new byte[9];
		System.arraycopy(helloData, 0, sendData, 0, 8);
		LoggerUtil.e("sendData:"+Dump.getHexDump(sendData));
		bluetoothManager.writeComm(dataService,sendData);
		SystemClock.sleep(1000);
		bluetoothManager.readComm(dataService,rbuf);
		LoggerUtil.e("rBuf:"+Dump.getHexDump(rbuf));
		// 加密的OK数据
		okBuf = DesTool.encrypt(desKey, okBuf);
		if(Tools.memcmp(okBuf, rbuf, okBuf.length)==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:%s比较数据失败,预期=%s,实际=%s", Tools.getLineInfo(),TESTITEM,
					Dump.getHexDump(okBuf),Dump.getHexDump(backBuf));
			return;
		}
		gui.cls_show_msg1(g_keeptime, "合法性验证测试通过");
		
	}
	
	public byte[] generalDesKey(byte[] timeBuf)
	{
		LoggerUtil.e("timeBuf:"+Dump.getHexDump(timeBuf));
		byte[] initKey = new byte[8];
		byte[] endKey = new byte[8];
		byte[] backTimeBuf = new byte[8];
		Arrays.fill(initKey, (byte) 0x11);
		byte[] encrypt1 = DesTool.encrypt(initKey, timeBuf);
		LoggerUtil.e("encrypt1:"+Dump.getHexDump(encrypt1));
		for (int i = 0; i < 4; i++) 
		{
			backTimeBuf[i] = (byte) ~timeBuf[i];
		}
		LoggerUtil.e("backTime:"+Dump.getHexDump(backTimeBuf));
		byte[] encrypt2 = DesTool.encrypt(initKey, backTimeBuf);
		LoggerUtil.e("encrypt2:"+Dump.getHexDump(encrypt2));
		for (int i = 0; i < 8; i++) 
		{
			endKey[i] = (byte) (encrypt1[i]^encrypt2[i]);
		}
		LoggerUtil.e("endKey:"+Dump.getHexDump(endKey));
		return endKey;
	}
	
	
	// Dongle压力
	public void preFun() 
	{
		int returnValue =gui.cls_show_msg("Dongle综合测试\n0.连接压力\n1.回连压力");
		switch (returnValue) 
		{
		case '0':
			dongleRwPre();
			break;
			
		case '1':
			dongleProPre();
			break;
			
			
		case ESC:
			break;
		}
	}
	
	// add 20160303
	// Dongle读写压力
	public void dongleRwPre() 
	{
		/* private & local definition */
		int succ = 0, cnt = 0, bak = 0;
		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		boolean ret;

		/* process body */
		// 设置压力的次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		// 测试前置,关闭蓝牙,底座点击复位按钮
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		if (bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		// 打开蓝牙以及配对操作
		if ((ret = bluetoothAdapter.enable()) != true) 
		{
			gui.cls_show_msg1(2,"line %d:BT打开失败ret = %s", Tools.getLineInfo(), ret);
			return;
		}
		SystemClock.sleep(3000);
		// 合法性认证
		dongleLegality("请使comEcho串口工具或短接头连接串口,请先将底座复位,已复位忽略");
		for (int i = 0; i < wbuf.length; i++) 
		{
			wbuf[i] = (byte) (Math.random()*128);
		}
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(3, "Dongle读写压力测试中...\n还剩%d次(已成功%d次)【取消】键退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			// 写数据到服务器端
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:POS发数据给底座失败", Tools.getLineInfo());
				continue;
			}
			gui.cls_show_msg1(2, "第%d次接收数据",(bak-cnt));
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:POS从底座接收数据失败", Tools.getLineInfo());
				continue;
			}
			if (!Tools.memcmp(wbuf, rbuf, wbuf.length)) 
			{
				gui.cls_show_msg1(g_keeptime,"line:%d:比较数据失败%s", Tools.getLineInfo(), false);
				continue;
			}
			succ++;
		}
		// 断开Socket连接
		bluetoothManager.cancel(dataService);
		if (bluetoothAdapter.isEnabled()) 
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime,"压力测试完成,已执行次数为%d,成功为%d次", bak - cnt, succ);
	}
	
	// add 20160303
	// 流程压力,利用回连测试,主动断开
	public void dongleProPre()
	{
		/* private & local definition */
		int bak = 0, cnt = 0, succ = 0;
		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		Gui gui = new Gui(getActivity(), handler);
		Dis_Type dis_Type = null;
		
		// 选择断开方式
		int returnValue =gui.cls_show_msg("Dongle流程压力\n0.N900断开\n1.底座断开");
		switch (returnValue) 
		{
		case '0':
			dis_Type = Dis_Type.N900_DIS;
			break;
			
		case '1':
			dis_Type = Dis_Type.Dongle_DIS;
			break;
		case ESC:
			break;
		}

		/* process body */
		// 设置压力的次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 测试前置,打开蓝牙,连接Dongle
		if (!bluetoothAdapter.isEnabled()) 
		{
			bluetoothAdapter.enable();
			SystemClock.sleep(3000);
		}
		if (!bluetoothAdapter.isEnabled()) 
		{
			gui.cls_show_msg1_record(TAG,"flowPre", g_keeptime, "line %d:打开蓝牙串口失败",Tools.getLineInfo());
		}
		// 合法性认证
		dongleLegality("请使comEcho串口工具或短接头连接串口,请先将底座复位,已复位忽略");
		// 底座具有自动重连机制
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(3, "Dongle流程压力,还剩%d次,已成功%d次,【取消】键退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			// 收发数据测试
			for (int j = 0; j < wbuf.length; j++) {
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:POS发送数据给底座失败", Tools.getLineInfo());
				continue;
			}
			gui.cls_show_msg1(2, "第%d次接收数据",(bak-cnt));
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:POS从底座接收数据失败", Tools.getLineInfo());
				continue;
			}
			// 比较数据
			if(!Tools.memcmp(wbuf, rbuf, rbuf.length))
			{
				gui.cls_show_msg1(g_keeptime,"line:%d:比较数据失败%s", Tools.getLineInfo(), false);
				continue;
			}
			succ++;
			// 断开socket的连接
			if(dis_Type == Dis_Type.N900_DIS)
				bluetoothManager.cancel(dataService);
			else
			{
				gui.cls_show_msg1(2, "将底座断电再上电");
			}
			long startTime = System.currentTimeMillis();
			new AcceptThread(dataService,UUID_DATA,bean).start();
			while(Tools.getStopTime(startTime)<30)
			{
				if(bean.isIsdateback() == true)
				{
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if(bean.isIsdateback()==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
				return;
			}
		}
		gui.cls_show_msg1_record(TAG, "dongleProPre",g_keeptime,"蓝牙底座数传+连接压力测试完成,执行次数:%d次,成功%d次", bak-cnt, succ);
	}
	
	// add 20160303
	// 性能,进行100次1024固定字节(0x49)收发测试
	public void dongleAbility() 
	{
		/* private & local definition */
		int succ = 0, cnt = 0, bak = 0;
		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		long readTimeStart,writeTimeStart;
		long readTime = 0,writeTime = 0;
		float readRate,writeRate;
		bak = cnt = 100;
		boolean ret;

		/* process body */
		// 测试前置,关闭蓝牙,复位底座
		gui.cls_show_msg1(0, "%s性能测试中...",TESTITEM);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		if (bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		// 打开蓝牙以及配对操作
		if ((ret = bluetoothAdapter.enable()) != true) 
		{
			gui.cls_show_msg1(2,"line %d:第%d次：BT打开失败ret = %s", Tools.getLineInfo(), bak- cnt, ret);
			return;
		}
		SystemClock.sleep(3000);
		// 合法性认证
		dongleLegality("请使comEcho串口工具或短接头连接串口,请先将底座复位,已复位忽略");
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(3, "Dongle性能测试中,发送1024字节0x49给底座...\n还剩%d次(已成功%d次)【取消】键退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			for (int i = 0; i < wbuf.length; i++) 
			{
				wbuf[i] = 0x49;
			}
			writeTimeStart = System.currentTimeMillis();
			// 发送1024字节数据给底座
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:POS发送数据失败", Tools.getLineInfo());
				bluetoothManager.cancel(dataService);
				if (bluetoothAdapter.isEnabled()) 
				{
					bluetoothAdapter.disable();
					SystemClock.sleep(500);
				}
				return;
			}
			writeTime += (System.currentTimeMillis()-writeTimeStart);
			readTimeStart = System.currentTimeMillis();
			gui.cls_show_msg1(2, "第%d次接收数据",(100-cnt));
			// 接收数据
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:POS从底座接收数据失败", Tools.getLineInfo());
				bluetoothManager.cancel(dataService);
				if (bluetoothAdapter.isEnabled()) 
				{
					bluetoothAdapter.disable();
					SystemClock.sleep(500);
				}
				return;
			}
			readTime += (System.currentTimeMillis() - readTimeStart);
			// 进行数据的比较
			if (!Tools.memcmp(wbuf, rbuf, wbuf.length)) 
			{
				gui.cls_show_msg1(g_keeptime,"line:%d:比较数据失败%s", Tools.getLineInfo(), false);
				bluetoothManager.cancel(dataService);
				if (bluetoothAdapter.isEnabled()) 
				{
					bluetoothAdapter.disable();
					SystemClock.sleep(500);
				}
				return;
			}
			succ++;
		}
		// 断开Socket连接
		bluetoothManager.cancel(dataService);
		if (bluetoothAdapter.isEnabled()) 
		{
			bluetoothAdapter.disable();
			SystemClock.sleep(500);
		}
		readRate = (float) (rbuf.length/(readTime/1000.0/bak));
		writeRate = (float) (wbuf.length/(writeTime/1000.0/bak));
		Log.d("rate", readRate+"  "+readRate);
		gui.cls_show_msg1(2,"蓝牙读通讯速率为%fB/S\n蓝牙写通讯速率为%fB/S\n", readRate,writeRate);
	}
	
	// 回连有两种方式,一种是主动,一种是被动
	public void dongleBack() 
	{
		int returnValue =gui.cls_show_msg("Dongle回连测试\n0.POS断开\n1.底座断开\n2.回连距离");
		switch (returnValue) 
		{
		case '0':
			dongleBackActive();
			break;
			
		case '1':
			dongleBackPassive();
			break;
			
		case '2':
			dongleBackDis();
			break;
			
		case ESC:
			break;
		}
	}
	
	// add 20160505
	// 主动断开连接
	public void dongleBackActive() 
	{
		/* private & local definition */
		int bak = 100,cnt = 100,suc = 0,i =0;
		
		/* process body */
		// 测试前置,复位重新连接,先确保跟Dongle连接成功
		bluetoothManager.regist();
		// 合法性验证
		dongleLegality("先将底座复位,已复位忽略");
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "Dongle回连测试第%d次,已成功%d次,【取消】键退出测试...",++i,suc)==ESC)
				break;
			cnt--;
			// 设备主动断开连接,底座应能自动回连
			bluetoothManager.cancel(dataService);
			long startTime = System.currentTimeMillis();
			new AcceptThread(dataService,UUID_DATA,bean).start();
			while(Tools.getStopTime(startTime)<30)
			{
				if(bean.isIsdateback() == true)
				{
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if(bean.isIsdateback()==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
				return;
			}
			suc++;
			SystemClock.sleep(500);
		}
		
		gui.cls_show_msg1(1, "Dong主动断开连接测试共%d次,已成功%d次", bak-cnt,suc);
	}
	
	// add by 20160505
	// 被动断开连接,从底座断电
	public void dongleBackPassive()
	{
		/* private & local definition */
		Gui gui = new Gui(getActivity(), handler);
		int bak = 50,cnt = 50,suc = 0,i =0;
		
		/* process body */
		// 测试前置,复位重新连接,先确保跟Dongle连接成功
		dongleLegality("请先将底座复位,已复位请忽略");
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "Dongle回连测试第%d次,已成功%d次,【取消】键退出测试...",++i,suc)==ESC)
				break;
			cnt--;
			// 底座断电断开连接,上电后应能自动回连
			gui.cls_show_msg1(1, "第%d次底座断电再上电",i);
			
			long startTime = System.currentTimeMillis();
			new AcceptThread(dataService,UUID_DATA,bean).start();
			while(Tools.getStopTime(startTime)<30)
			{
				if(bean.isIsdateback() == true)
				{
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if(bean.isIsdateback()==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
				return;
			}
			suc++;
		}
		gui.cls_show_msg1(1, "Dong被动断开连接测试共%d次,已成功%d次", bak-cnt,suc);
	}
	
	// add by 20160509
	// 测试回连距离,默认距离为10M
	public void dongleBackDis() 
	{
		/* private & local definition */
		Gui gui = new Gui(getActivity(), handler);
		int suc = 0,i=0;
		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		
		/* process body */
		// 测试前置,复位重新连接
		dongleLegality("请用短接头短接底座串口或comEcho串口工具,请先将底座复位,已复位请忽略");
		for (int j = 0; j < 10; j++) 
		{
			if(gui.cls_show_msg1(3, "蓝牙回连距离测试,进行第%d次,已成功%d次,[取消]键退出测试...",(j+1),suc)==ESC)
				break;
			gui.cls_show_msg("设备和蓝牙底座的距离为"+(++i)+"m,完成点任意键");
			
			bluetoothManager.cancel(dataService);
			SystemClock.sleep(1000);
			long startTime = System.currentTimeMillis();
			AcceptThread acceptThread = new AcceptThread(dataService,UUID_DATA, bean);
			acceptThread.start();
			while(Tools.getStopTime(startTime)<30)
			{
				if(bean.isIsdateback() == true)
				{
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if(bean.isIsdateback()==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
				return;
			}
			gui.cls_show_msg1(0, "正在进行数据收发测试,长时间未收到数据,可将底座复位,退出当前阻塞");
			// 收发数据测试
			Arrays.fill(wbuf, (byte) 0x38);
			bluetoothManager.writeComm(dataService,wbuf);
			SystemClock.sleep(2000);
			bluetoothManager.readComm(dataService,rbuf);
			// 比较数据
			if(!Tools.memcmp(wbuf, rbuf, wbuf.length))
			{
				gui.cls_show_msg1(1, "line %d:%s比较数据失败", Tools.getLineInfo(),TESTITEM);
				continue;
			}
			suc++;
		}
		gui.cls_show_msg1(1, "回连距离测试通过");
	}
	
	// add by 20160830
	// 蓝牙底座处于连接状态时复位不应异常
	public void dongleAbnormal() 
	{
		int returnValue =gui.cls_show_msg("异常测试\n0.连接-复位异常\n1.合法性验证异常\n2.休眠唤醒异常测试");
		switch (returnValue) {
		case '0':
			dongleAbnormal1();
			break;
			
		case '1':
			dongleAbnormal2();
			break;
			
		case '2':
			dongleAbnormal3();
			break;

		case ESC:
			break;
		}
	}
	
	// 蓝牙底座处于连接状态时复位不应异常
	public void dongleAbnormal1()
	{
		// 配对的时候点击复位按钮
		Gui gui = new Gui(myactivity, handler);
		long startTime;
		for (int i = 0; i < 2; i++) 
		{
			// 配对连接操作
			dongleLegality("请先将底座复位,已复位请忽略");
			gui.cls_show_msg1(2, "底座断电再上电");
			startTime = System.currentTimeMillis();
			new AcceptThread(dataService,UUID_DATA,bean).start();
			
			while(Tools.getStopTime(startTime)<30)
			{
				if(bean.isIsdateback() == true)
				{
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if(bean.isIsdateback()==false)
			{
				gui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
				return;
			}
			
			if(gui.ShowMessageBox(( "重新建立连接成功,请点击复位键,指示灯是否转变为闪烁状态").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:%s连接状态转变为复位状态失败", Tools.getLineInfo(),TESTITEM);
					return;
			}
		}
		gui.cls_show_msg1(2, "BT_Dongle异常测试1通过");
	}
	
	// 合法性验证异常,传入错误的数据以及5s后回数据均连接均应断开
	public void dongleAbnormal2()
	{
		/* private & local definition */
		byte[] backBuf = new byte[10];
		byte[] timeBuf = new byte[8];
		byte[] helloBuf = {0x68,0x65,0x6C,0x6C,0x6F,0x00,0x00,0x00};
		
		/* process body */
		
		gui.cls_show_msg("BT_Dongle异常测试2,请先点击复位按钮");
		// 配置蓝牙
		if(pair(getBtAddr())==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d：蓝牙配对失败", Tools.getLineInfo());
			return;
		}
		bluetoothManager.connComm(dataService, CHANEL_DATA);
		if (bluetoothManager.getConnStatus() != BluetoothDevice.ACTION_ACL_CONNECTED) 
		{
			gui.cls_show_msg1(g_keeptime, "line %d:建立连接失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(1, "等待底座主动发送数据");
		// 收发数据
		bluetoothManager.readComm(dataService,backBuf);
		LoggerUtil.e(Dump.getHexDump(backBuf));
		// 生成密钥以及加密的hello键
		System.arraycopy(backBuf, 2, timeBuf, 0, 4);
		byte[] desKey = generalDesKey(timeBuf);
		int returnValue =gui.cls_show_msg("合法性异常\n0.5s后发数据\n1.发错误数据");
		switch (returnValue) 
		{
		case '0':
			gui.cls_printf("等待5s后发送加密的hello数据给底座".getBytes());
			SystemClock.sleep(5000);
			helloBuf = DesTool.encrypt(desKey, helloBuf);
			byte[] sendBuf = new byte[9];
			System.arraycopy(helloBuf, 0, sendBuf, 0, 8);
			bluetoothManager.writeComm(dataService,sendBuf);
			break;

		case '1':
			gui.cls_printf("发送错误数据给底座".getBytes());
			bluetoothManager.writeComm(dataService,new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
			break;
		case ESC:
			break;
		}
		if(gui.ShowMessageBox(("底座指示灯是否从蓝灯常亮转变为快速闪烁s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:合法性异常测试失败", Tools.getLineInfo());
				return;
		}
		gui.cls_show_msg1(0, "BT_Dongle异常测试2子用例通过");
		
	}
	
	// 回连状态下休眠唤醒后应能自动重连--异常测试
	public void dongleAbnormal3()
	{
		/* process body */
		// 设置一个标志位,只有进行休眠异常测试的时候才设置为true,测试完毕设置为false
		isSleepAbnormal = true;
		if(dongleState()==false)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:休眠唤醒异常测试失败", Tools.getLineInfo());
			isSleepAbnormal = false;
			return;
		}
		isSleepAbnormal = false;
		gui.cls_show_msg1(2, "回连状态(连接)休眠异常测试通过");
	}

	// 重写返回键
/*	@Override
	public void onBackDown() 
	{
		gui.cls_show_msg1(2, "即将退出测试");
		GlobalVariable.EXIT_TEST = true;
	}*/
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}
	
}
