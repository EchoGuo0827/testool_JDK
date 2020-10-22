package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DesTool;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.aidl.OnSearchListener;
import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.k21controller.util.Dump;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest85.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20190124
 * directory 		: 
 * description 		: Dongle综合测试-纯蓝牙底座(单通道)
 * related document :
 * history 		 	: author			date			remarks
 * 					  wangxy			20190124
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest85 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "纯蓝牙(单通道)";
	public final String TAG = SysTest85.class.getSimpleName();
	private final int PACKLEN = 1024;
	private final int BACK_TIMEOUT = 15;
	private final int DATA_TIMEOUT = 15;//透传接收超时时间
	/*----------global variables declaration------------------------*/
	// 蓝牙设备
	private BluetoothController nlBluetooth=null;
	private int i=0,len=0;
	private byte[] rbuf=new byte[PACKLEN];
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	// 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Config config;
	private Gui gui = new Gui(myactivity, handler);
	private int ret=-1;
	public void systest85()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "systest42", g_keeptime,"%s不支持自动测试,请使用手动测试验证", TESTITEM);
			return;
		}
		// 全局变量初始化操作
		nlBluetooth=BluetoothController.getInstance();
		//addby wangxy 20190123,开发新增服务初始化接口
		nlBluetooth.initService();
		OnSearchListener onSearchListener=new OnSearchListener.Stub() {
			@Override
			public void onDataReceive(int portCom,byte[] data) throws RemoteException {
				LoggerUtil.e("接收数据广播");
            	len=len+data.length;
        		System.arraycopy(data, 0,rbuf, i, data.length);  
        		i=i+data.length;				
			}
			@Override
			public void onFinish() throws RemoteException {
			}
			@Override
			public void onDeviceFound(String arg0, String arg1) throws RemoteException {
			}
			@Override
			public void onStatusChange(int arg0, int arg1, String arg2, String arg3) throws RemoteException {
			}
			@Override
			public void onOpenPortStatus(int arg0, boolean arg1)
					throws RemoteException {
				// TODO Auto-generated method stub
				
			}
		};
		nlBluetooth.init(myactivity, onSearchListener);//不使用此处OnSearchListener搜索蓝牙、蓝牙状态改变的监听
		bluetoothManager = BluetoothManager.getInstance(myactivity,this);
		GlobalVariable.isDongle = true;
		config = new Config(myactivity, handler);
		while(true)
		{
			int returnValue=gui.cls_show_msg("Dongle综合测试\n0.配置\n1.功能\n2.压力\n3.性能\n4.异常\n5.退出测试");
			switch (returnValue) 
			{
			// Dongle配置-蓝牙配对
			case '0':
				dongleConfig();
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
				if("".equals(getBtAddr()))
				{
					gui.cls_show_msg1(2, "请先进行蓝牙底座配置");
					return;
				}
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
	
	public void dongleConfig()
	{
		int returnValue=gui.cls_show_msg("底座配置\n0.蓝牙选择\n1.连接底座连接\n2.蓝牙配对后自动连接");
		switch (returnValue) 
		{
		case '0':
			config.btConfig(pairList, unPairList, bluetoothManager);
			if(unPairList.size()==0)	
			{
				gui.cls_show_msg1(2, "未搜索到蓝牙底座");
				return;
			}
			break;
			
		// 连接底座连接
		case '1':
			if ("".equals(getBtAddr())) 
			{
				gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				break;
			}
			Thread t=new Thread(new Runnable() {
				@Override
				public void run() {
					if (!nlBluetooth.startBluetoothConnA(getBtName(), getBtAddr())) {
						gui.cls_show_msg1_record(TAG, "dongleConfig", g_keeptime, "line %d:连接蓝牙失败（false）",Tools.getLineInfo());
						return;
					}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					LoggerUtil.e("nlBluetooth.isConnectedA()=" + nlBluetooth.isConnectedA());
					// add by wangxy 20181228 判断POS与底座的连接状态
					if (!nlBluetooth.isConnectedA()) {
						gui.cls_show_msg1_record(TAG, "dongleConfig", g_keeptime, "line %d:获取蓝牙连接状态失败(false)",Tools.getLineInfo());
						return;
					}
				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			gui.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,其他POS搜索到该底座视为【测试不通过】,任意键继续测试");
			break;
		
		case '2':
			if ("".equals(getBtAddr())) 
			{
				gui.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				break;
			}
			gui.cls_show_msg("请点击复位并在设置-蓝牙中清除底座的配对信息，完成后点击任意键继续");
			gui.cls_printf("正在配对中。。。".getBytes());
			if(bluetoothManager.pair(getBtAddr(), "0")==false)
			{
				gui.cls_show_msg1_record(TAG, "dongleConfig", g_keeptime, "line %d:蓝牙配对失败（ret=%s）", Tools.getLineInfo(),ret);
				return;
			}
			SystemClock.sleep(5*1000);
			if (nlBluetooth.isConnectedA() == false) 
			{
				gui.cls_show_msg1_record(TAG, "dongleConfig", g_keeptime, "line %d:连接状态与预期不符（ret=%s）", Tools.getLineInfo(),ret);			
				return;
			}
			gui.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,其他POS搜索到该底座视为【测试不通过】,任意键继续测试");
			break;
		
		/*开发燕清说纯蓝牙不支持主动断开addbywangxy20190125
		 * case '2':
			nlBluetooth.disconnect() ;
			long startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<30)
			{
				if(!nlBluetooth.isConnectedA())
					break;
			}
			if(nlBluetooth.isConnectedA())
			{
				gui.cls_show_msg1_record(TAG, "systest74",g_keeptime, "line %d:获取蓝牙连接状态失败（true）", Tools.getLineInfo());
				break;
			}
			gui.cls_show_msg("蓝牙已断开,其他POS应可以搜索到该底座,确认请按任意键继续");*/
			
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
	
	// Dongle功能
	public void dongleFunction() 
	{
		while(true)
		{
		int nkeyIn = gui.cls_show_msg("Dongle功能测试\n0.回连功能\n1.重配对功能\n2.数据转发\n");
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
			
		// 数据转发
		case '2':
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
		switch (nkeyIn) 
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
		// 确认底座是否连接
		if (nlBluetooth.isConnectedA() == false) 
		{
			gui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		// 此时已具有回连功能,复位重新配对,应无法回连
		gui.cls_show_msg("点击复位按钮");
		// case1:复位后应断开与N900手机的连接
		Arrays.fill(wbuf, (byte) 0x38);
		if(nlBluetooth.sendDataA(wbuf)==true)
		{
			gui.cls_show_msg1_record(TAG, "repairDongle",g_keeptime, "line %d:数据通道发送数据预期应失败", Tools.getLineInfo());
			return;
		}
		// case2:复位后连接状态为false且pos无法主动回连
		if(gui.ShowMessageBox(("观察15s,当前底座指示灯一直为500ms快闪状态,确认蓝灯快闪").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "repairDongle", g_keeptime, "line %d:指示灯亮灭时间与预期不符", Tools.getLineInfo());
			return;
		}
		if(nlBluetooth.isConnectedA())
		{
			gui.cls_show_msg1_record(TAG, "repairDongle",g_keeptime, "line %d:底座复位后，连接状态应为false", Tools.getLineInfo());
			return;
		}
		
		// case3:复位后可实现重配对
		gui.cls_show_msg1(1,"复位后重新连接底座应成功");
		if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
		{
			gui.cls_show_msg1_record(TAG, "repairDongle",g_keeptime, "line %d:连接蓝牙失败（false）", Tools.getLineInfo());
			return;
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// add by wangxy 20181228 判断POS与底座的连接状态
		if (!nlBluetooth.isConnectedA()) 
		{
			gui.cls_show_msg1_record(TAG, "repairDongle", g_keeptime, "line %d:获取蓝牙连接状态失败(false)", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg("重配对子用例1测试通过");
		
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
			gui.cls_show_msg1(2,"即将进行蓝牙连接。。。");
			if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
			{
				gui.cls_show_msg1_record(TAG, "repairN900",g_keeptime, "line %d:连接蓝牙失败（false）", Tools.getLineInfo());
				return;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// add by wangxy 20181228 判断POS与底座的连接状态
			if (!nlBluetooth.isConnectedA()) 
			{
				gui.cls_show_msg1_record(TAG, "repairN900", g_keeptime, "line %d:获取蓝牙连接状态失败(false)",Tools.getLineInfo());
				return;
			}
			/*if(pair(getBtAddr())==false)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:蓝牙配对失败", Tools.getLineInfo());
				continue;
			}*/
			// 移除配对
			gui.cls_show_msg1(1,"清除蓝牙配对信息。。。");
			removePair();
		}
		gui.cls_show_msg1(1, "重配对子用例2测试通过");
	}
	
	/**
	 * POS与底座数据收发测试
	 * @return
	 */
	public boolean dataSenRecv()
	{
		/* private & local definition */
		rbuf = new byte[2047];
		byte[] wbuf = new byte[2047];
		boolean ret;
		gui.cls_show_msg("请将comEcho串口工具打开,2047数据转发测试中,完成后按任意键继续");
		// 收发数据
		Arrays.fill(wbuf, (byte) 0x33);
		gui.cls_show_msg1(2, "发送数据中,请耐心等待...");
		if((ret=nlBluetooth.sendDataA(wbuf))==false)
		{
			gui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:POS发送数据失败(ret = %s)", Tools.getLineInfo(),ret);
			return ret;
		}
		//接收数据
		int time = 0;
		i = 0;
		len = 0;
		gui.cls_show_msg1(1, "将从底座接收2047字节数据");
		long startTime = System.currentTimeMillis();
		while (time < DATA_TIMEOUT) {
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if (len == 2047)
				break;
		}
		if (len != 2047) {
			LoggerUtil.e("len=" + len);
			LoggerUtil.e("wbuf=" + ISOUtils.hexString(wbuf));
			LoggerUtil.e("rbuf=" + ISOUtils.hexString(rbuf));
			gui.cls_show_msg1_record(TAG, "dataSendRecv", g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)",Tools.getLineInfo(), len);
			return false;
		} else {
			gui.cls_show_msg1(2, "收发数据完毕,校验数据中");
			// 比较收发数据
			if (Tools.memcmp(wbuf, rbuf, 2047) == false) {
				LoggerUtil.e("wbuf=" + ISOUtils.hexString(wbuf));
				LoggerUtil.e("rbuf=" + ISOUtils.hexString(rbuf));
				gui.cls_show_msg1_record(TAG, "dataSendRecv", g_keeptime, "line %d:数据通道收发数据失败(ret = %d)",Tools.getLineInfo(), BT_COMPARE_FAILED);
				return false;

			}
		}
		gui.cls_printf("数据收发成功".getBytes());
		return true;
	}
	
	// add by 20160513
	// 数据转发
	public void dongleData() 
	{
		/* private & local definition */
		
		/* process body */
		// 确认底座是否连接
		if (nlBluetooth.isConnectedA() == false) 
		{
			gui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		gui.cls_show_msg1(2, "PC串口工具将收到2047B数据");
		if(!dataSenRecv())
		{
			gui.cls_show_msg1_record(TAG, "dongleData",g_keeptime, "line %d:纯蓝牙底座数据通讯失败(false)", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg( "数据转发测试通过");
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
//		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		if(nlBluetooth.isConnectedA()==false)
		{
			gui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		/* process body */
		// 设置压力的次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		
		gui.cls_show_msg("请使comEcho串口工具或短接头连接串口,完成按任意键继续");
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
			if(nlBluetooth.sendDataA(wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "dongleRwPre",g_keeptime, "line %d:POS发数据给底座失败", Tools.getLineInfo());
				continue;
			}
			gui.cls_show_msg1(2, "第%d次接收数据",(bak-cnt));
			//接收数据
			int time=0;
			i=0;len=0;
			rbuf=new byte[PACKLEN];
			long startTime = System.currentTimeMillis();
			gui.cls_show_msg1(1, "将从底座接收"+PACKLEN+"字节数据");
			while(time<DATA_TIMEOUT)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if(len==PACKLEN)
					break;
			}
			if(len!=PACKLEN)
			{
				LoggerUtil.e("len="+len);
				LoggerUtil.e("wbuf="+ISOUtils.hexString(wbuf));
				LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
				gui.cls_show_msg1_record(TAG, "dongleRwPre",g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)", Tools.getLineInfo(),len);
				continue;	
			}else{
				// 比较收发数据
				if(Tools.memcmp(wbuf, rbuf, PACKLEN)==false)
				{
					LoggerUtil.e("sbuf="+ISOUtils.hexString(wbuf));
					LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
					gui.cls_show_msg1_record(TAG, "dongleRwPre",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
					continue;	
					
				}
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "dongleRwPre",g_keeptime,"压力测试完成,已执行次数为%d,成功为%d次", bak - cnt, succ);
	}
	
	// add 20160303
	// 流程压力,利用回连测试,主动断开
	public void dongleProPre()
	{
		/* private & local definition */
		int bak = 0, cnt = 0, succ = 0;
		byte[] wbuf = new byte[PACKLEN];
		Gui gui = new Gui(getActivity(), handler);
		
		/* process body */
		// 设置压力的次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		//BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		gui.cls_show_msg("底座接入串口线并短接23脚,先将蓝牙底座复位并数字信号发生器间隔输出高-低电平给底座,高电平1min,低电平10s,完毕按任意键继续");
		//建立双通道
		if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
		{
			gui.cls_show_msg1_record(TAG, "dongleProPre",g_keeptime, "line %d:连接蓝牙失败(false)", Tools.getLineInfo());
			return;
		}
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<30)
		{
			if(nlBluetooth.isConnectedA())
				break;
		}
		// add by wangxy 20181228 判断POS与底座的连接状态
		if(!nlBluetooth.isConnectedA())
		{
			gui.cls_show_msg1_record(TAG,"dongleProPre",g_keeptime, "line %d:获取蓝牙连接状态失败(false)", Tools.getLineInfo());
			return;
		}
		// 收发数据测试
		for (int j = 0; j < wbuf.length; j++) {
			wbuf[j] = (byte) (Math.random() * 256);
		}
		// 底座具有自动重连机制
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(3, "Dongle流程压力,还剩%d次,已成功%d次,【取消】键退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			// 命令通道断开 回连操作
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<60)
			{
				SystemClock.sleep(1000);
				if(nlBluetooth.isConnectedA()==false)
					break;
			}
			gui.cls_show_msg1(1, "命令通道、数据通道已断开...");
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<40)
			{
				if(nlBluetooth.isConnectedA()==true)
				{
					gui.cls_show_msg1(2, "命令通道、数据通道回连成功");
					break;
				}
			}
			if(nlBluetooth.isConnectedA()==false)
			{
				gui.cls_show_msg1_record(TAG, "dongleProPre", g_keeptime, "line %d:第%d次命令通道、数据通道回连失败", Tools.getLineInfo(),bak-cnt);
				// 将监听的给断开
				continue;
			}
			
			// 写数据到服务器端
			if (nlBluetooth.sendDataA(wbuf) == false) 
			{
				gui.cls_show_msg1_record(TAG, "dongleProPre", g_keeptime, "line %d:POS发数据给底座失败", Tools.getLineInfo());
				continue;
			}
			gui.cls_show_msg1(2, "第%d次接收数据", (bak - cnt));
			// 接收数据
			int time = 0;
			i = 0;
			len = 0;
			rbuf = new byte[PACKLEN];
			startTime = System.currentTimeMillis();
			gui.cls_show_msg1(1, "将从底座接收" + PACKLEN + "字节数据");
			while (time < DATA_TIMEOUT) {
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if (len == PACKLEN)
					break;
			}
			if (len != PACKLEN) {
				LoggerUtil.e("len=" + len);
				LoggerUtil.e("wbuf=" + ISOUtils.hexString(wbuf));
				LoggerUtil.e("rbuf=" + ISOUtils.hexString(rbuf));
				gui.cls_show_msg1_record(TAG, "dongleProPre", g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)",
						Tools.getLineInfo(), len);
				continue;
			} else {
				// 比较收发数据
				if (Tools.memcmp(wbuf, rbuf, PACKLEN) == false) {
					LoggerUtil.e("sbuf=" + ISOUtils.hexString(wbuf));
					LoggerUtil.e("rbuf=" + ISOUtils.hexString(rbuf));
					gui.cls_show_msg1_record(TAG, "dongleProPre", g_keeptime, "line %d:数据通道收发数据失败(ret = %d)",
							Tools.getLineInfo(), BT_COMPARE_FAILED);
					continue;

				}
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "dongleProPre",g_keeptime,"蓝牙底座数传+连接压力测试完成,执行次数:%d次,成功%d次", bak-cnt, succ);
	}
	
	// add 20160303
	// 性能,进行100次1024固定字节(0x49)收发测试
	public void dongleAbility() 
	{
		/* private & local definition */
		int succ = 0, cnt = 0, bak = 0;
//		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		long readTimeStart,writeTimeStart;
		long readTime = 0,writeTime = 0;
		float readRate,writeRate;
		bak = cnt = 100;

		/* process body */
		if(nlBluetooth.isConnectedA()==false)
		{
			gui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		for (int i = 0; i < wbuf.length; i++) 
		{
			wbuf[i] = 0x49;
		}
		gui.cls_show_msg1(2, "%s性能测试中...",TESTITEM);
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(3, "Dongle性能测试中,发送1024字节0x49给底座...\n还剩%d次(已成功%d次)【取消】键退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			
			writeTimeStart = System.currentTimeMillis();
			// 发送1024字节数据给底座
			if(nlBluetooth.sendDataA(wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "dongleAbility",g_keeptime, "line %d:数据通道发送数据失败", Tools.getLineInfo());
				return ;
			}
			writeTime += (System.currentTimeMillis()-writeTimeStart);
			readTimeStart = System.currentTimeMillis();
//			gui.cls_show_msg1(1, "第%d次接收数据",(100-cnt));
			gui.cls_printf(("第"+(100-cnt)+"次接收数据").getBytes());
			//接收数据
			int time=0;
			i=0;len=0;
			rbuf=new byte[PACKLEN];
			long startTime = System.currentTimeMillis();
			while(time<DATA_TIMEOUT)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if(len==PACKLEN)
					break;
			}
			if(len!=PACKLEN)
			{
				LoggerUtil.e("len="+len);
				LoggerUtil.e("wbuf="+ISOUtils.hexString(wbuf));
				LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
				gui.cls_show_msg1_record(TAG, "dongleAbility",g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)", Tools.getLineInfo(),len);
				return;	
			}else{
				readTime += (System.currentTimeMillis() - readTimeStart);
				// 比较收发数据
				if(Tools.memcmp(wbuf, rbuf, PACKLEN)==false)
				{
					LoggerUtil.e("wbuf="+ISOUtils.hexString(wbuf));
					LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
					gui.cls_show_msg1_record(TAG, "dongleAbility",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
					return ;
					
				}
			}
			succ++;
		}
		readRate = (float) (rbuf.length/(readTime/1000.0/bak));
		writeRate = (float) (wbuf.length/(writeTime/1000.0/bak));
		Log.d("rate", readRate+"  "+readRate);
		gui.cls_show_msg1_record(TAG, "backCommPre",g_keeptime,"蓝牙读通讯速率为%fB/S\n蓝牙写通讯速率为%fB/S\n", readRate,writeRate);
	}
	
	// 回连有两种方式,一种是主动,一种是被动
	public void dongleBack() 
	{
		int returnValue =gui.cls_show_msg("Dongle回连测试\n0.底座上下电断开回连\n1.回连距离");
		switch (returnValue) 
		{
		case '0':
			dongleBackPassive();
			break;
			
		case '1':
			dongleBackDis();
			break;
			
		case ESC:
			break;
		}
	}
	
	// add by 20160505
	// 被动断开连接,从底座断电
	public void dongleBackPassive()
	{
		/* private & local definition */
		Gui gui = new Gui(getActivity(), handler);
		int bak = 50,cnt = 50,suc = 0,i =0;
		long startTime;
		
		/* process body */
		//确认底座是否连接
		if(nlBluetooth.isConnectedA()==false)
		{
			gui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "Dongle回连测试第%d次,已成功%d次,【取消】键退出测试...",++i,suc)==ESC)
				break;
			cnt--;
			// 底座断电断开连接,上电后应能自动回连
			gui.cls_show_msg("第%d次底座断电再上电,手动插拔底座电源，操作完毕点击任意键",i);
			gui.cls_show_msg1(2, "数据通道回连中......");
			// (1)命令通道、数据通道回连操作
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
			{
				if(nlBluetooth.isConnectedA()==true)
				{
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if(nlBluetooth.isConnectedA()==false)
			{
				gui.cls_show_msg1_record(TAG, "dongleBackPassive", g_keeptime, "line %d:数据通道回连失败", Tools.getLineInfo());
				return;
			}
			suc++;
		}
		gui.cls_show_msg1_record(TAG, "dongleBackPassive",g_keeptime, "Dong底座上下电断开连接后回连测试共%d次,已成功%d次", bak-cnt,suc);
	}
	
	// add by 20160509
	// 测试回连距离,默认距离为10M
	public void dongleBackDis() 
	{
		/* private & local definition */
		Gui gui = new Gui(getActivity(), handler);
		int suc = 0;
//		byte[] rbuf = new byte[PACKLEN];
		byte[] wbuf = new byte[PACKLEN];
		long startTime;
		/* process body */
		// 确认底座是否连接
		if (nlBluetooth.isConnectedA() == false) 
		{
			gui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		// 测试前置,复位重新连接
		gui.cls_show_msg("请用短接头短接底座串口或comEcho串口工具,完成按任意键继续");
		for (int j = 0; j < 10; j++) 
		{
			if(gui.cls_show_msg1(3, "蓝牙回连距离测试,进行第%d次,已成功%d次,[取消]键退出测试...",(j+1),suc)==ESC)
				break;
			gui.cls_show_msg("设备和蓝牙底座的距离为"+(j+1)+"m,完成点任意键");
			// 底座断电断开连接,上电后应能自动回连
			gui.cls_show_msg("底座断电再上电,操作完毕点击任意键");
			gui.cls_show_msg1(1, "数据通道回连中......");
			// 数据通道回连操作
			startTime = System.currentTimeMillis();
			while (Tools.getStopTime(startTime) < BACK_TIMEOUT) {
				if (nlBluetooth.isConnectedA() == true) {
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if (nlBluetooth.isConnectedA() == false) {
				gui.cls_show_msg1_record(TAG, "dongleBackDis", g_keeptime, "line %d:数据通道回连失败", Tools.getLineInfo());
				return;
			}
			gui.cls_show_msg1(2, "正在进行数据收发测试,长时间未收到数据,可将底座复位,退出当前阻塞");
			// 收发数据测试
			Arrays.fill(wbuf, (byte) 0x38);
			if(nlBluetooth.sendDataA(wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "dongleBackDis",g_keeptime, "line %d:数据通道发送数据失败", Tools.getLineInfo());
				return ;
			}
			//接收数据
			int time=0;
			i=0;len=0;
			rbuf=new byte[PACKLEN];
			startTime = System.currentTimeMillis();
			gui.cls_show_msg1(1, "将从底座接收"+PACKLEN+"字节数据");
			while(time<DATA_TIMEOUT)
			{
				time = (int) Tools.getStopTime(startTime);
				SystemClock.sleep(1000);
				if(len==PACKLEN)
					break;
			}
			if(len!=PACKLEN)
			{
				LoggerUtil.e("len="+len);
				LoggerUtil.e("sbuf="+ISOUtils.hexString(wbuf));
				LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
				gui.cls_show_msg1_record(TAG, "dongleBackDis",g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)", Tools.getLineInfo(),len);
				continue;	
			}else{
				// 比较收发数据
				if(Tools.memcmp(wbuf, rbuf, PACKLEN)==false)
				{
					LoggerUtil.e("sbuf="+ISOUtils.hexString(wbuf));
					LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
					gui.cls_show_msg1_record(TAG, "dongleBackDis",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
					continue;
					
				}
			suc++;
		}
		}
		gui.cls_show_msg( "回连距离测试通过");
	}
	
	// add by 20160830
	// 蓝牙底座处于连接状态时复位不应异常
	public void dongleAbnormal() 
	{
		int returnValue =gui.cls_show_msg("异常测试\n0.连接-复位异常");
		switch (returnValue) {
		case '0':
			dongleAbnormal1();
			break;
			
		case ESC:
			break;
		}
	}
	
	// 蓝牙底座处于连接状态时复位不应异常
	public void dongleAbnormal1()
	{
		
		gui.cls_show_msg("点击复位键,完成按任意键继续");
		Gui gui = new Gui(myactivity, handler);
		long startTime;
		for (int i = 0; i < 2; i++) 
		{
			gui.cls_show_msg("请先将底座复位,已复位请忽略,完成按任意键继续");
			if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
			{
				gui.cls_show_msg1_record(TAG, "dongleAbnormal1",g_keeptime, "line %d:连接蓝牙失败(false)", Tools.getLineInfo());
				return;
			}
			gui.cls_show_msg("底座断电再上电,完成按任意键继续");
			gui.cls_show_msg1(1, "数据通道回连中......");
			// 数据通道回连操作
			startTime = System.currentTimeMillis();
			while (Tools.getStopTime(startTime) < BACK_TIMEOUT) {
				if (nlBluetooth.isConnectedA() == true) {
					gui.cls_show_msg1(2, "数据通道回连成功");
					break;
				}
			}
			if (nlBluetooth.isConnectedA() == false) {
				gui.cls_show_msg1_record(TAG, "dongleAbnormal1", g_keeptime, "line %d:数据通道回连失败", Tools.getLineInfo());
				return;
			}
			
			if(gui.ShowMessageBox(( "重新建立连接成功,请点击复位键,指示灯是否转变为闪烁状态").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(TAG, "dongleAbnormal1", g_keeptime, "line %d:%s连接状态转变为复位状态失败", Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		gui.cls_show_msg1(2, "BT_Dongle异常测试1通过");
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}
	
}
