package com.example.highplattest.systest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.newland.SettingsManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.DefineListener;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.AcceptThread;
import com.example.highplattest.main.btutils.BlueBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Command;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DesTool;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest62.java 
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
public class SysTest62 extends DefaultFragment implements DefineListener.BackListener
{
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "蓝牙底座测试(双通道)";
	public final String TAG = SysTest62.class.getSimpleName();
	private final int PACKLEN = 50;
	
	/*----------global variables declaration------------------------*/
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private SettingsManager settingsManager;
	private final int BACK_TIMEOUT = 15;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	 // 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Config config;
	// 公共界面显示
	private Gui commGUi = new Gui(myactivity, handler);
	private BluetoothService cmdService;
	private BluetoothService dataService;
//	private boolean cmdConnect = false;
//	private boolean dataConenct = false;
	private int uiTime = 2;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private BlueBean bean;
	byte[][] bps_list = {{(byte) 0x80,0x25,0x00,0x00}/*9600*/,
			{0x00,0x4B,0x00,0x00}/*19200*/,
			{0x00,(byte) 0x96,0x00,0x00}/*38400*/,
			{0x00,(byte) 0xE1,0x00,0x00}/*57600*/,
			{0x00,(byte) 0xC2,0x01,0x00}/*115200*/,
			{0x00,(byte) 0xE8,0x03,0x00}/*25600*/,
			{(byte) 0xF0,(byte) 0xCD,0x05,0x00}/*380400*/};
	private String mBtMac;
	private String mBtName;
	private String mBtBps;
	private String mBtVersion;
	int count=0 ;// pos总数
	int fail=0;//pos回连失败数
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case 0:// 合法性验证
				new ShowDialog().configAuthChoose(myactivity, "合法性选择");
				break;
				
			/*case HandlerMsg.DONGLE_CMD_BACK:// 命令通道回连成功
				cmdConnect = true;
				break;
				
			case HandlerMsg.DONGLE_DATA_BACK:// 数据通道回连成功
				dataConenct = true;
				break;*/
				
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:// 命令通道显示界面
				mTvCmd.setText("命令通道："+(CharSequence) msg.obj);
				break;
				
			default:
				break;
			}
		};
	};
	
	Handler dataHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) {
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				mTvData.setText("数据通道："+(CharSequence) msg.obj);
				break;

			default:
				break;
			}
		};
	};
	
	
	public void systest62()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			commGUi.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		bean=new BlueBean();
		sharedPreferences = myactivity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		GlobalVariable.isDongle = true;
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		config = new Config(myactivity, handler);
		while(true)
		{
			int returnValue=commGUi.cls_show_msg("蓝牙底座测试\n0.配置\n1.功能\n2.压力\n3.异常\n4.断开选择");
			switch (returnValue) 
			{
			// Dongle配置
			case '0':
				dongleConfig();
				break;
				
			// dongle功能
			case '1':
				if("".equals(getBtAddr()))
				{
					commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
					return;
				}
				dongleFunction();
				break;
				
			// dongle压力
			case '2':
				if("".equals(getBtAddr()))
				{
					commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
					return;
				}
				preFun();
				break;
				
			// dongle异常
			case '3':
				dongleAbnormal();
				break;
				
			case '4':// 断开选择
				cancelChoose();
				break;
				
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	
	// 蓝牙底座配置操作 add by zhengxq 20170405
	public void dongleConfig()
	{
		int returnValue=commGUi.cls_show_msg("底座配置\n0.选择-配对\n1.合法性控制\n2.数据收发长度设置\n3.波特率配置\n4.提交pos重启次数到数据库的前置");
		switch (returnValue) 
		{
		case '0':
			config.btConfig(pairList, unPairList, bluetoothManager);
			if(unPairList.size()!=0)	{
				// 这个时候已经选择好蓝牙底座了，配置完毕直接进行配对
				cmdService = new BluetoothService(getBtAddr());
				dataService = new BluetoothService(getBtAddr());
				bluetoothManager.pair(getBtAddr(), "0");	
				editor.putString("btAddr", getBtAddr());
				editor.putBoolean("isReboot", false);
				editor.commit();//提交修改
			}	
			else
			{
				commGUi.cls_show_msg1(2, "未搜索到蓝牙底座");
				dialog.dismiss();
				return;
			}
			
			break;
		
		// 合法性控制
		case '1':
			if("".equals(getBtAddr()))
			{
				commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				return;
			}
			myactivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					new ShowDialog().configAuthChoose(myactivity, "合法性选择");
				}
			});
			synchronized (myactivity) {
				try {
					myactivity.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			degitalChoose();
			break;
		//数据收发长度设置
		case '2':
			myactivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					set_data_length("数据收发长度设置");
				}
			});
			synchronized(g_lock)
			{
				try {
					g_lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
			
		// 波特率配置
		case '3':
			bpsConfig();
			break;
		case '4':
			setsp();
			break;
		default:
			break;
		}
	}
	//设置数据库的初始数值。用于计算Pos重启回连的成功率
	private void setsp() {
		// TODO Auto-generated method stub
		editor.putInt("fail", fail);
		editor.commit();
		editor.putInt("count", count);
		editor.commit();
	}

	/**
	 * 波特率配置 add by zhengxq20170516
	 */
	public void bpsConfig()
	{
		Gui gui = new Gui(myactivity, myHandler);
		//只支持这几种波特率
		int returnValue=gui.cls_show_msg("波特率配置\n0.9600\n1.19200\n2.38400\n3.57600\n4.115200\n5.256000\n6.380400");
		int num =returnValue-48 ;
		int ret1;
	
		byte[] rBuf =new byte[50];
		
		/*process body*/
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		
		// 设置波特率好像不用读返回值的
		if(bluetoothManager.writeComm(cmdService,Command.generateCommand(0x04, 0x00, bps_list[num]).pack())==false)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,  "line %d:%s测试失败(bps = %s)",Tools.getLineInfo(),TESTITEM,Dump.getHexDump(bps_list[num]));
			return;
		}
		// 读取波特率值
		if((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,  "line %d:%s测试失败(ret = %d)",Tools.getLineInfo(),TESTITEM,ret1);
			return;
		}
		Command bpsCommand = Command.decode(rBuf);
		if(Tools.memcmp(bpsCommand.getData(), bps_list[num], 4)==false)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:%s测试失败(bps预期=%s，bps实际=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(bps_list[num]),Dump.getHexDump(bpsCommand.getData()));
			return;
		}
		gui.cls_show_msg1(uiTime, "设置波特率%d成功，请将电脑串口波特率修改为对应值才可进行数据通道测试", bpsTransmit(bps_list[num]));
	}
	
	
	// 是否进行合法性验证 add by zhengxq 20170405
	public void degitalChoose()
	{
		// 测试前置先获取目前的合法性状态
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		byte[] sbuf = Command.generateCommand(0x05, 0xE0, new byte[]{(byte) (GlobalVariable.Auth_Control&0x01)}).pack();
		bluetoothManager.writeComm(cmdService,sbuf);
		// 读命令通道只需要读一次即可
		byte[] rbuf = new byte[sbuf.length];
		bluetoothManager.readComm(cmdService,rbuf);
		Command command = Command.decode(rbuf);
		int getAuth = command.getData()[0];
		// 返回0代表成功，返回1代表失败
		if(getAuth==1)
		{
			commGUi.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:设置合法性认证失败(ret = %d)", Tools.getLineInfo(),getAuth);
			return;
		}
		commGUi.cls_show_msg1(2, "合法性认证设置成功");
	}
	
	// 获取目前回连和认证状态 add by zhengxq 20170406
	public int getStatus()
	{
		Gui cmdGui = new Gui(myactivity, myHandler);
		// 这个里面要么是回连，要么是复位操作，二选一，使用复位操作
		commGUi.cls_show_msg("请先对底座进行复位操作并插入钱箱，完成任意键继续");
		// 先获取目前的合法性状态
		int ret;
		
		// 发送的报文内容
		byte[] sbuf = Command.generateCommand(0x06, 0xE0, null).pack();
		// 接收缓冲区
		byte[] rbuf = new byte[PACKLEN];
		// 建立命令通道
		if(bluetoothManager.connComm(cmdService,CHANEL_CMD)==false)
		{
			cmdGui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:建立命令通道连接失败", Tools.getLineInfo());
			return BT_CONNECT_FAILED;
		}
		// 认证是底座发起的，5s内未读数据看底座此时是否有数据传输给我，未传输给我就关闭socket，然后重新建立
		// 先发送个获取蓝牙版本的命令 看底座是否有返回数据 来判断是否为需要认证状态 false表示需要进行认证，true为不需要 add by zhengxq 20170406
		ret = legalityComm(cmdService,cmdGui);
		if(ret!=BT_OK)
		{
			if(ret==BT_READ_FAILED)
			{
				// 关闭socket以及重新建立socket的连接
				bluetoothManager.cancel(cmdService);
				commGUi.cls_show_msg("请将蓝牙底座复位，点任意键继续");
				// 回连功能不在这里测试，直接复位连接
				if(bluetoothManager.connComm(cmdService,CHANEL_CMD)==false)
				{
					cmdGui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:建立命令通道连接失败", Tools.getLineInfo());
					return BT_CONNECT_FAILED;
				}
			}
			else
				return ret;
		}
		
		if((ret = cmdSendRecv(sbuf, rbuf)) != BT_OK)
			return ret;
		
		Command command = Command.decode(rbuf);
		// 按位与，右移一位
		int authBit = command.getData()[0]>>1;
		return authBit;
	}
	
	// 命令通道的收发数据操作
	public int cmdSendRecv(byte[] sBuf,byte[] rBuf)
	{
		
		if( bluetoothManager.writeComm(cmdService,sBuf)==false)
			return BT_WRITE_FAILED;
		Arrays.fill(rBuf, (byte) 0x00);
		if( bluetoothManager.readComm(cmdService,rBuf)==false)
			return BT_READ_FAILED;
		// 读缓冲区不是以0x19开头的数据都是错误数据，返回false
		if( Tools.memcmp(rBuf, new byte[]{0x19}, 1)==false)
			return BT_COMPARE_FAILED;
		return BT_OK;
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
	
	// Dongle功能修改 modify by zhengxq 20170405
	public void dongleFunction() 
	{		
		int returnValue =commGUi.cls_show_msg("底座功能\n0.数据路连接-回连\n1.命令通道连接-回连\n2.数据路连接、命令路连接\n3.数据路连接、命令路回连\n4.数据路回连、命令路连接\n5.数据路回连、命令路回连\n6.指示灯切换\n7.轮询波特率进行数据通讯\n");
		int num=returnValue-48;
		switch (returnValue) 
		{
		// 数据单通道
		case '0':
			dataSingle();
			break;
			
		// 命令单通道
		case '1':
			cmdSingle();
			break;
			
		// 数据、命令双通道1
		case '2':
		// 数据、命令双通道2
		case '3':
		// 数据、命令双通道3
		case '4':
		// 数据、命令双通道4
		case '5':
			cmdDataCh1(num);
			break;
			
		//各指示灯判断
		case '6':
			lightTest();
			break;

		// 轮询波特率进行数据通讯
		case '7':
			allBpsTest();
			break;
			
		case ESC:
			break;
			
		}
	}
	// 轮询波特率进行数据通讯
	private void allBpsTest() {
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		int authBit=-1;
		int ret1=-1;
		byte[] rBuf =new byte[50];
		// 建立命令通道，getStatus()中已打开命令通道
		authBit = getStatus();
		if (authBit == BT_CONNECT_FAILED || authBit == BT_COMPARE_FAILED)
			return;
		// 建立数据通道连接
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) 
		{
			dataGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			return;
		}
		if (authBit == 1) // 是否进行合法性认证
		{
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		if(commGUi.ShowMessageBox(("当前为双通道,确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
			return;
		}
		for (byte[] bps:bps_list)
		{
			commGUi.cls_show_msg1(2,"即将将波特率设置为"+bpsTransmit(bps));
			// 设置波特率好像不用读返回值的
			if(bluetoothManager.writeComm(cmdService,Command.generateCommand(0x04, 0x00, bps).pack())==false)
			{
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,  "line %d:%s测试失败(bps = %s)",Tools.getLineInfo(),TESTITEM,bpsTransmit(bps));
				continue;
			}
			// 读取波特率值
			if((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,  "line %d:%s测试失败(ret = %d)",Tools.getLineInfo(),TESTITEM,ret1);
				continue;
			}
			Command bpsCommand = Command.decode(rBuf);
			if(Tools.memcmp(bpsCommand.getData(), bps, 4)==false)
			{
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s测试失败(bps预期=%s，bps实际=%s)", Tools.getLineInfo(),TESTITEM,bpsTransmit(bps),bpsTransmit(bpsCommand.getData()));
				continue;
			}
			commGUi.cls_show_msg("当前波特率已设置为:"+bpsTransmit(bps)+",请打开AccessPort设置成对应的波特率,设置完毕并接入串口线后按任意键继续");
			if(dataSendRecv()!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s数据收发测试失败(bps=%s)", Tools.getLineInfo(),TESTITEM,bpsTransmit(bpsCommand.getData()));
				continue;
			}
			
			commGUi.cls_show_msg("请手动上下电底座（预期底座上下电后波特率恢复为默认值115200），完成后按任意键继续");
			AcceptThread cmdAccept = new AcceptThread(cmdService,UUID_CMD,bean);
			cmdAccept.start();
			AcceptThread dataAccept = new AcceptThread(dataService,UUID_DATA,bean);
			dataAccept.start();
			long startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<30)
			{
				if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
				{
					commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
					break;
				}
			}
			if(bean.isIscmdback() == false||bean.isIsdateback()==false)
			{
				cmdAccept.cancel();
				dataAccept.cancel();
				commGUi.cls_show_msg1_record(TAG, "allBpsTest",g_keeptime, "line %d:命令通道、数据通道回连失败(数据通道=%s，命令通道=%s)", Tools.getLineInfo(),bean.isIsdateback(),bean.isIscmdback());
				continue;
			}
			if(commGUi.ShowMessageBox(("当前为双通道,确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
			{
				commGUi.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
			}
			// 读取波特率值
			if ((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf)) != BT_OK) {
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s测试失败(ret = %d)",Tools.getLineInfo(), TESTITEM, ret1);
				continue;
			}
			bpsCommand = Command.decode(rBuf);
			if (Tools.memcmp(bpsCommand.getData(), bps_list[4], 4) == false) {
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s测试失败(bps预期=%s，bps实际=%s)",Tools.getLineInfo(), TESTITEM, bpsTransmit(bps_list[4]), bpsTransmit(bpsCommand.getData()));
				continue;
			}
			commGUi.cls_show_msg("当前波特率已恢复为默认值:"+bpsTransmit(bps_list[4])+",请打开AccessPort设置成对应的波特率,设置完毕并接入串口线后按任意键继续");
			if(dataSendRecv()!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s数据收发测试失败(bps=%s)", Tools.getLineInfo(),TESTITEM,bpsTransmit(bpsCommand.getData()));
				continue;
			}

		}
		
		/*//测试后置，波特率恢复为115200
		commGUi.cls_show_msg1(2,"测试后置，波特率恢复为115200");
		if(bluetoothManager.writeComm(cmdService,Command.generateCommand(0x04, 0x00, bps_list[4]).pack())==false)
		{
			cmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime,  "line %d:%s测试失败(bps = %s)",Tools.getLineInfo(),TESTITEM,bpsTransmit(bps_list[4]));
			return;
		}*/
		commGUi.cls_show_msg("轮询设置波特率进行数据通道通讯测试通过");
	}

	//底座设置波特率后，重新上下电后，波特率值是否保存且生效 addby wangxy20181213
	private void DonglePowerPre() {
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		int authBit=-1;
		int ret1=-1;
		byte[] rBuf =new byte[50];
		int cnt,succ = 0,bak;
		cnt=bak=commGUi.JDK_ReadData(10, 200, "请输入压力次数");
		// 先恢复为广播状态
		commGUi.cls_show_msg("先将蓝牙底座复位并数字信号发生器间隔输出高-低电平给底座,完毕按任意键继续");
		//建立命令通道，getStatus()中已打开命令通道
		authBit= getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		// 建立数据通道连接
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) 
		{
			dataGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			return;
		}
		if (authBit == 1) // 是否进行合法性认证
		{
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		
		if(commGUi.ShowMessageBox(("当前为双通道,确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
			return;
		}
		
		// 将当前的蓝牙mac、名字、波特率、版本记录在数据库中，后续底座断电回连成功后进行比较,
		getBtMac(null);
		getBtName(null);
//		getBps(null);
		getBtVersion(null);
		editor.putString("btMac", mBtMac);
		editor.commit();
		editor.putString("btName", mBtName);
		editor.commit();
		editor.putString("btBps", "115200");//掉电后bps恢复为默认值115200
		editor.commit();
		editor.putString("btVersion", mBtVersion);
		editor.commit();
		
		/*// 读取设置前的波特率值
		if ((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf)) != BT_OK) 
		{
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:%s测试失败(ret = %d)",Tools.getLineInfo(), TESTITEM, ret1);
			return;
		}
		Command bpsCommand = Command.decode(rBuf);
		cmdGui.cls_show_msg1(1,"当前波特率为"+bpsTransmit(bpsCommand.getData()));
		
		int bpsNum = (int) (Math.random()*6);//从当前支持的波特率中随机选取一个
		// 设置波特率好像不用读返回值的
		if(bluetoothManager.writeComm(cmdService,Command.generateCommand(0x04, 0x00, bps_list[bpsNum]).pack())==false)
		{
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime,  "line %d:%s测试失败(bps = %s)",Tools.getLineInfo(),TESTITEM,bpsTransmit(bps_list[bpsNum]));
			return;
		}
		// 读取波特率值
		if((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime,  "line %d:%s测试失败(ret = %d)",Tools.getLineInfo(),TESTITEM,ret1);
			return;
		}
		bpsCommand = Command.decode(rBuf);
		if(Tools.memcmp(bpsCommand.getData(), bps_list[bpsNum], 4)==false)
		{
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:%s测试失败(bps预期=%s，bps实际=%s)", Tools.getLineInfo(),TESTITEM,bpsTransmit(bps_list[bpsNum]),bpsTransmit(bpsCommand.getData()));
			return;
		}*/
		//底座重新上下电
//		cmdGui.cls_show_msg("当前波特率已设置为:%s",bpsTransmit(bpsCommand.getData()));
		int timeout = 200;
		String version,btMac,btName,btBps = null;
		
		while(true)
		{
			cnt--;
			commGUi.cls_show_msg1(1, "底座上下电回连正在进行第%d次测试,成功%d次", bak-cnt,succ);
			bean=new BlueBean();
			AcceptThread cmdAccept = new AcceptThread(cmdService,UUID_CMD,bean);
			cmdAccept.start();
			AcceptThread dataAccept = new AcceptThread(dataService,UUID_DATA,bean);
			dataAccept.start();
			long startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<timeout)
			{
				if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
				{
					commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
					break;
				}
			}
			if(bean.isIscmdback() == false||bean.isIsdateback()==false)
			{
				cmdAccept.cancel();
				dataAccept.cancel();
				commGUi.cls_show_msg1_record(TAG, "DonglePowerPre",g_keeptime, "line %d:第%d次:命令通道、数据通道回连失败(数据通道=%s，命令通道=%s)", Tools.getLineInfo(),bak-cnt,bean.isIsdateback(),bean.isIscmdback());
				continue;
			}
			if(cnt==0)
				break;
			//底座断电回连成功后，与之前的蓝牙底座数据进行比较应一致
			//case1:命令通道通讯 ，获取蓝牙版本
			getBtVersion(null);
			version = sharedPreferences.getString("btVersion", "");
			if(mBtVersion.equals(version)==false)
			{
				commGUi.cls_only_write_msg("DonglePowerPre", "onReceive","line %d:上下电获取蓝牙版本不一致(curVer=%s,version=%s)", Tools.getLineInfo(),mBtVersion,version);
			}
			// case2:获取蓝牙底座mac地址
			getBtMac(null);
			btMac = sharedPreferences.getString("btMac", "");
			if(mBtMac.equals(btMac)==false)
			{
				commGUi.cls_only_write_msg("DonglePowerPre", "onReceive","line %d:上下电获取蓝牙mac不一致(curMac=%s,btMac=%s)", Tools.getLineInfo(),mBtMac,btMac);
			}
			// case3:获取蓝牙底座的名字
			getBtName(null);
			btName = sharedPreferences.getString("btName", "");
			if(mBtName.equals(btName)==false)
			{
				commGUi.cls_only_write_msg("DonglePowerPre", "onReceive","line %d:上下电获取蓝牙名字不一致(curName=%s,btName=%s)",Tools.getLineInfo(),mBtName,btName);
			}
			// case4:获取蓝牙底座的波特率,应该为115200，不管之前设置为多少，重新上下电后均为默认值115200
			getBps(null);
			btBps = sharedPreferences.getString("btBps", "");
			if(mBtBps.equals(btBps)==false)
			{
				commGUi.cls_only_write_msg("DonglePowerPre", "onReceive","line %d:上下电获取蓝牙底座波特率不一致，掉电后默认波特率为115200(curBps=%s,btBps=%s)",Tools.getLineInfo(),mBtBps,btBps);
			}
			/*// 上下电后重新读取波特率值,即使读到的波特率值和上一次不一致 这次也要重新写入新的波特率值 modify by zhengxq
			Arrays.fill(rBuf,(byte)0);
			if((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime,  "line %d:第%d次:%s测试失败(ret = %d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret1);
//				continue;
			}
			bpsCommand = Command.decode(rBuf);
			if(Tools.memcmp(bpsCommand.getData(), bps_list[bpsNum], 4)==false)
			{
				cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:第%d次:%s测试失败(bps预期=%s，bps实际=%s)", Tools.getLineInfo(),bak-cnt,TESTITEM,bpsTransmit(bps_list[bpsNum]),bpsTransmit(bpsCommand.getData()));
//				continue;
			}
			else
				commGUi.cls_show_msg1(1, "此次获取的波特率与上一次一致");
			
			bpsNum=cnt==1?0:(int) (Math.random()*6);//从当前支持的波特率中随机选取一个,最后一次测试将波特率设置为9600

			// 设置波特率好像不用读返回值的
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:第%d次:%s测试即将将bps设置为%s)", Tools.getLineInfo(),bak-cnt,TESTITEM,bpsTransmit(bps_list[bpsNum]));
			if(bluetoothManager.writeComm(cmdService,Command.generateCommand(0x04, 0x00, bps_list[bpsNum]).pack())==false)
			{
				cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime,  "line %d:第%d次:%s测试失败(bps = %s)",Tools.getLineInfo(),bak-cnt,TESTITEM,bpsTransmit(bps_list[bpsNum]));
				continue;
			}
			// 读取波特率值
			if((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime,  "line %d:第%d次:%s测试失败(ret = %d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret1);
				continue;
			}
			bpsCommand = Command.decode(rBuf);
			if(Tools.memcmp(bpsCommand.getData(), bps_list[bpsNum], 4)==false)
			{
				cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:第%d次:%s测试失败(bps预期=%s，bps实际=%s)", Tools.getLineInfo(),bak-cnt,TESTITEM,bpsTransmit(bps_list[bpsNum]),bpsTransmit(bpsCommand.getData()));
				continue;
			}
			commGUi.cls_printf(String.format("当前波特率已设置为:"+bpsTransmit(bpsCommand.getData())).getBytes());*/
			succ++;
		}
		cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "底座上下电回连共测试%d次,成功%d次", bak,succ);
		
		//上下电压测后置确认波特率不受影响
		commGUi.cls_show_msg("请把底座接入正常的电源,操作完毕点击任意键继续");
		AcceptThread cmdAccept = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdAccept.start();
		AcceptThread dataAccept = new AcceptThread(dataService,UUID_DATA,bean);
		dataAccept.start();
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<timeout)
		{
			if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
				break;
			}
		}
		if(bean.isIscmdback() == false||bean.isIsdateback()==false)
		{
			cmdAccept.cancel();
			dataAccept.cancel();
			commGUi.cls_show_msg1_record(TAG, "DonglePowerPre",g_keeptime, "line %d:第%d次:命令通道、数据通道回连失败(数据通道=%s，命令通道=%s)", Tools.getLineInfo(),bak-cnt,bean.isIsdateback(),bean.isIscmdback());
			return;
		}
		// 读取波特率值
		if((ret1 = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime,  "line %d:%s测试失败(ret = %d)",Tools.getLineInfo(),TESTITEM,ret1);
			return;
		}
		Command bpsCommand = Command.decode(rBuf);
		String curBps = bpsTransmit(bpsCommand.getData())+"";
		if(curBps.equals("115200")==false)
		{
			commGUi.cls_only_write_msg(TAG,"DonglePowerPre","line %d:上下电获取蓝牙底座波特率应为默认值115200(curBps=%s)",Tools.getLineInfo(),curBps);
		}
		commGUi.cls_show_msg("当前波特率为:%s（由于底座上下电后，波特率恢复为默认值115200）,请打开AccessPort设置成对应的波特率",bpsTransmit(bpsCommand.getData()));
		if(dataSendRecv()!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:%s数据收发测试失败(bps=%s)", Tools.getLineInfo(),TESTITEM,bpsTransmit(bpsCommand.getData()));
			return;
		}
		cmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "底座上下电回连测试通过");
	}

	private void PosRebootPre() {
		Gui dataGui = new Gui(myactivity, dataHandler);
		int authBit=-1;
		String mac=null;
		int failtem;
		int counttem;
		
		if((mac=sharedPreferences.getString("btAddr","")).equals(""))
		{
			dataGui.cls_show_msg1_record(TAG, "reboot", g_keeptime, "line %d:数据库获取蓝牙mac地址失败(%s)", Tools.getLineInfo(),mac);
			failtem=sharedPreferences.getInt("fail", 0);
			failtem++;
			editor.putInt("fail", failtem);
			return;
		}
		
		// 先恢复为广播状态
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		authBit = getStatus();//打开了命令通道
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		// 建立数据通道连接
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "reboot", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			failtem=sharedPreferences.getInt("fail", 0);
			failtem++;
			editor.putInt("fail", failtem);
			return;
		}
		// 是否进行合法性认证
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		if(commGUi.ShowMessageBox(("当前为双通道，确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "reboot", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
			failtem=sharedPreferences.getInt("fail", 0);
			failtem++;
			editor.putInt("fail", failtem);
			
			return;
		}
		//将当前的蓝牙mac、名字、波特率、版本记录在数据库中，后续重启后进行比较
		getBtMac(null);
		getBtName(null);
		getBps(null);
		getBtVersion(null);
		counttem=sharedPreferences.getInt("count", 0);
		counttem++;
		editor.putInt("count", counttem);
		editor.commit();
		editor.putString("btMac", mBtMac);
		editor.commit();
		editor.putString("btName", mBtName);
		editor.commit();
		editor.putString("btBps", mBtBps);
		editor.commit();
		editor.putString("btVersion", mBtVersion);
		editor.commit();
		//确认POS重启后进行回连操作
		editor.putBoolean("isReboot", true);
		editor.commit();//提交修改
		commGUi.cls_show_msg("建议将本用例用于夜间压力测试，期间POS将自动间隔一段时间（30-60min）后重启POS回连底座，请确保底座一直处于上电状态且接入RS232线并短接2/3口\n"
				+ "之后请将POS接入充电设备后测试一晚，第二天查看result文件，第一次须手动重启POS，测试结束后请卸载HighPlatTest应用避免后续继续重启测试");
		
	}

	//判断各个状态的指示灯
/*	（1）从双通路连接状态复位到广播状态
	（2）只连接命令路状态复位到广播状态
	（3）只连接数据路状态复位到广播状态
	（4）回连（单、双路回连）状态复位到广播状态
	（5）分别从广播状态、回连状态到双通路连接状态
	（6）从连接状态-回连状态-连接状态（单通道、双通道），对命令通道、数据通道收发数据
	*/
	private void lightTest(){
	//双通路（蓝灯常亮）、仅数据路（蓝灯亮1.75s，灭0.25s）、仅命令路（蓝灯亮1s，灭1s）、双路回连、仅数据路回连、仅命令路回连（双单回连均蓝灯亮1.75s，灭0.25s，重复闪烁） 六种情况切换到广播状态
		 /*private & local definition*/
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		int authBit=-1;
		long startTime;
		/*process body*/
		//仅数据-->广播
		authBit= getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		// 先恢复为广播状态
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		// 建立数据通道连接
		if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "dataSingle", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			return;
		}
		if(authBit==1)// 是否进行合法性认证
		{
			if(legalityComm(dataService,dataGui)!=BT_OK)
				return;
		}
		
		if(commGUi.ShowMessageBox(("当前为仅连接数据通道，秒表确认蓝灯是否亮1.75s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//建立命令通道，getStatus()中已打开命令通道
		authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		//仅命令-->广播
		if(commGUi.ShowMessageBox(("当前为仅连接命令通道，秒表确认蓝灯是否亮1s，灭1s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接命令通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//双通道-->广播
		authBit = getStatus();//打开了命令通道
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		// 建立数据通道连接
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			return;
		}
		// 是否进行合法性认证
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		if(commGUi.ShowMessageBox(("当前为双通道，确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//双通道回连-->广播状态  PS:先双通道连接，再断开通道，等待自动回连
		// 获取合法性认证状态
		
		authBit = getStatus();
		if (authBit == BT_CONNECT_FAILED || authBit == BT_COMPARE_FAILED) {
			return;
		}
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		// 双通道已开启，均断开后，自动进行双通道回连
		if(commGUi.ShowMessageBox(("当前为双通道，确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座重新上电，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为双通道回连，秒表计时确认蓝灯是否亮0.25s,灭1.75s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:双通道回连时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//仅数据通道回连-->广播
		authBit= getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		// 先恢复为广播状态
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		// 建立数据通道连接
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "dataSingle", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			return;
		}
		// 是否进行合法性认证
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		if(commGUi.ShowMessageBox(("当前为仅连接数据通道，秒表确认蓝灯是否亮1.75s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座重新上电，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为单通道（数据通道）回连，秒表计时确认蓝灯是否亮0.25s,灭1.75s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:数据通道回连时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//命令通道回连-->广播
		authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			return;
		if(commGUi.ShowMessageBox(("当前为仅连接命令通道，秒表确认蓝灯是否亮1s，灭1s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接命令通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座重新上电，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为单通道（命令通道）回连，秒表计时确认蓝灯是否亮0.25s,灭1.75s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:命令通道回连时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//双回连-->双通道
		authBit = getStatus();
		if (authBit == BT_CONNECT_FAILED || authBit == BT_COMPARE_FAILED) {
			return;
		}
		dataGui.cls_show_msg1(2, "建立数据通道链路中...");
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "cmdDataCh1", g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		if(commGUi.ShowMessageBox(("当前为双通道，确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
//		cmdConnect =false;
//		dataConenct = false;
		bean=new BlueBean();
		AcceptThread cmdAccept = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdAccept.start();
		AcceptThread dataAccept = new AcceptThread(dataService,UUID_DATA,bean);
		dataAccept.start();
		//双通道连续断开服务会异常，故加了延时   20180315已修复该bug
		commGUi.cls_show_msg1(2, "命令通道、数据通道即将断开。。。");
		bluetoothManager.cancel(cmdService);
		bluetoothManager.cancel(dataService);
		
		commGUi.cls_show_msg1(2, "命令通道、数据通道回连中...");
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
				break;
			}
		}
		if(bean.isIscmdback() == false||bean.isIsdateback()==false)
		{
			cmdAccept.cancel();
			dataAccept.cancel();
			commGUi.cls_show_msg1_record(TAG, "cmdDataCh4",g_keeptime, "line %d：命令通道、数据通道回连失败(数据通道=%s，命令通道=%s)", Tools.getLineInfo(),bean.isIsdateback(),bean.isIscmdback());
			return;
		}
		//回连成功
		if(commGUi.ShowMessageBox(("当前为双通道，确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			commGUi.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为广播状态，秒表计时来确认蓝灯是否亮0.25s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		//命令连接-->通讯-->回连-->命令连接-->通讯-->广播
		authBit = getStatus();
		if (authBit == BT_CONNECT_FAILED || authBit == BT_COMPARE_FAILED) {
			return;
		}
		if(cmdOperate()!=BT_OK)
			return;
		commGUi.cls_show_msg("请先将蓝牙底座重新上电，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为单通道（命令通道）回连，秒表计时确认蓝灯是否亮0.25s,灭1.75s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:命令通道回连时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
//		cmdConnect =false;
		AcceptThread cmdAccept1 = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdAccept1.start();
		
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIscmdback()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道回连成功");
				break;
			}
		}
		if(bean.isIscmdback() == false)
		{
			cmdAccept1.cancel();
			commGUi.cls_show_msg1_record(TAG, "cmd",g_keeptime, "line %d：命令通道回连失败(命令通道=%s)", Tools.getLineInfo(),bean.isIscmdback());
			return;
		}
		if(cmdOperate()!=BT_OK)
			return;
		//数据连接-->通讯-->回连-->数据连接-->通讯
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "cmdDataCh1", g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		// 发送1024长度内容
		if(dataSendRecv()!=BT_OK)
			return;
		commGUi.cls_show_msg("请先将蓝牙底座重新上电，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为单通道（数据通道）回连，秒表计时确认蓝灯是否亮0.25s,灭1.75s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:数据通道回连时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
//		dataConenct = false;
		AcceptThread dataThread1 = new AcceptThread(dataService,UUID_DATA,bean);
		dataThread1.start();
		
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIsdateback()==true)
			{
				dataGui.cls_show_msg1(2, "数据通道回连成功");
				break;
			}
		}
		if(bean.isIsdateback() == false)
		{
			dataThread1.cancel();
			dataGui.cls_show_msg1_record(TAG, "cmd",g_keeptime, "line %d：数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
			return;
		}
		// 发送1024长度内容
		if(dataSendRecv()!=BT_OK)
			return;
		
		//双通道-->通讯-->双回连-->双通道-->通讯
		authBit = getStatus();
		if (authBit == BT_CONNECT_FAILED || authBit == BT_COMPARE_FAILED) {
			return;
		}
		if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
			dataGui.cls_show_msg1_record(TAG, "cmdDataCh1", g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if (authBit == 1) {
			if (legalityComm(dataService, dataGui) != BT_OK)
				return;
		}
		if(dataSendRecv()!=BT_OK)
			return;
		if(cmdOperate()!=BT_OK)
			return;
		commGUi.cls_show_msg("请先将蓝牙底座重新上电，完成点任意键");
		if(commGUi.ShowMessageBox(("当前为双通道回连，秒表计时确认蓝灯是否亮0.25s,灭1.75s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:双通道回连时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
//		cmdConnect =false;
//		dataConenct = false;
		AcceptThread cmdAccept2 = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdAccept2.start();
		AcceptThread dataAccept2 = new AcceptThread(dataService,UUID_DATA,bean);
		dataAccept2.start();
		
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
				break;
			}
		}
		if(bean.isIsdateback() == false||bean.isIscmdback()==false)
		{
			cmdAccept2.cancel();
			dataAccept2.cancel();
			commGUi.cls_show_msg1_record(TAG, "cmdDataCh4",g_keeptime, "line %d：命令通道、数据通道回连失败(数据通道=%s，命令通道=%s)", Tools.getLineInfo(),bean.isIsdateback(),bean.isIscmdback());
			return;
		}
		if(dataSendRecv()!=BT_OK)
			return;
		if(cmdOperate()!=BT_OK)
			return;
		commGUi.cls_show_msg("状态灯切换测试通过");
	}
	
	// 功能-数据单通道 add by zhengxq 20170406
	/*指示灯从广播状态、数据路回连状态到只连接数据路状态，对数据通道收发数据*/
	private void dataSingle()
	{
		 /*private & local definition*/
		Gui dataGui = new Gui(myactivity, dataHandler);
		
		/*process body*/
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		// 断开命令路，连接数据路
		commGUi.cls_show_msg("请先将蓝牙底座复位，完成点任意键");
		// 建立数据通道连接
		if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "dataSingle", g_keeptime, "line %d:建立数据通道连接失败", Tools.getLineInfo());
			return;
		}
		// 是否进行合法性认证
		if(authBit==1)
		{
			if(legalityComm(dataService,dataGui)!=BT_OK)
				return;
		}
		if(commGUi.ShowMessageBox(("当前为数据通道，秒表计时确认蓝灯是否亮1.75s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		// 发送1024长度内容
		if(dataSendRecv()!=BT_OK)
			return;
		
//		dataConenct = false;
		AcceptThread dataThread = new AcceptThread(dataService,UUID_DATA,bean);
		dataThread.start();
		// 回连->通讯操作
		bluetoothManager.cancel(dataService);
		commGUi.cls_show_msg1(2, "数据单通道连接->通讯成功，现在进行回连->通讯测试");
		// 回连操作
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIsdateback()==true)
			{
				dataGui.cls_show_msg1(2, "数据通道回连成功");
				break;
			}
		}
		if(bean.isIsdateback()==false)
		{
			// 回连失败关闭该线程
			dataThread.cancel();
			dataGui.cls_show_msg1_record(TAG, "dataSingle",g_keeptime, "line %d：数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
			return;
		}
		if(commGUi.ShowMessageBox(("当前为数据通道，秒表计时确认蓝灯是否亮1.75s，灭0.25s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		if(dataSendRecv()!=BT_OK)
			return;
		commGUi.cls_show_msg1(2, "数据单通道连接->通讯->回连->通讯成功");
	}
	
	// 数据通道的数据收发 add by 20170407
	public int dataSendRecv()
	{
		// 收发100字节无问题，1024字节收发数据通道会断开
		byte[] sBuf = new byte[byteLength];
		byte[] rBuf = new byte[byteLength];
		Gui gui = new Gui(myactivity, dataHandler);
		
		Arrays.fill(sBuf, (byte) 0x38);
//		for (int j = 0; j < sBuf.length; j++) 
//			sBuf[j] = (byte) (Math.random()*256);
		gui.cls_show_msg1(1, "将发送"+byteLength+"字节数据给底座");
		if(bluetoothManager.writeComm(dataService,sBuf)==false)
		{
			gui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:数据通道发送数据失败(ret = %d)", Tools.getLineInfo(),BT_WRITE_FAILED);
			return BT_WRITE_FAILED;
		}
		gui.cls_show_msg1(1, "将从底座接收"+byteLength+"字节数据");
		if( bluetoothManager.readComm(dataService,rBuf)==false)
		{
			gui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:数据通道接收数据失败(rBuf=%s,ret = %d)", Tools.getLineInfo(),ISOUtils.hexString(rBuf),BT_READ_FAILED);
			return BT_READ_FAILED;
		}
		Log.v("wxy", "ISOUtils.hexString(sBuf)="+ISOUtils.hexString(sBuf));
		Log.v("wxy", "ISOUtils.hexString(rBuf)="+ISOUtils.hexString(rBuf));
		// 比较收发数据
		if(Tools.memcmp(sBuf, rBuf, byteLength)==false)
		{
			gui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
			return BT_COMPARE_FAILED;
		}
		gui.cls_show_msg1(1, "数据收发完毕");
		return BT_OK;
	}
	
	// 功能-命令单通道 ，全部命令过一遍 add by zhengxq 20170407
	/*指示灯从广播状态、命令路回连状态到只连接命令路状态，对命令通道收发数据*/
	public void cmdSingle()
	{
		 /*private & local definition*/
		// 测试一次发送1024字节，只能接收到794个字节长度
		Gui cmdGui = new Gui(myactivity, myHandler);
		
		/*process body*/
		// 命令路已经连接，直接进行发命令操作
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		if(commGUi.ShowMessageBox(("当前为命令通道，秒表计时确认蓝灯是否亮1s，灭1s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接命令通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		if(cmdOperate()!=BT_OK)
			return;
		
		// 回连->通讯操作
//		cmdConnect = false;
		AcceptThread cmdThread = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdThread.start();
		bluetoothManager.cancel(cmdService);
		commGUi.cls_show_msg1(2, "命令单通道连接->通讯成功，现在进行回连->通讯测试");
		// 回连操作
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<30)
		{
			if(bean.isIscmdback() == true)
			{
				cmdGui.cls_show_msg1(2, "命令通道回连成功");
				break;
			}
		}
		if(bean.isIscmdback() == false)
		{
			cmdGui.cls_show_msg1_record(TAG, "cmdSingle",g_keeptime, "line %d:命令通道回连失败(命令通道=%s)", Tools.getLineInfo(),bean.isIscmdback());
			return;
		}
		if(commGUi.ShowMessageBox(("当前为命令通道，秒表计时确认蓝灯是否亮1s，灭1s").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "lightTest", g_keeptime, "line %d:仅连接命令通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
		}
		if(cmdOperate()!=BT_OK)
			return;
		commGUi.cls_show_msg1(2, "命令单通道连接->通讯->回连->通讯成功");
	}
	
	/**
	 * 命令操作 add by zhengxq 20170410
	 * @param rBuf
	 * @param bluetoothService
	 * @param cmdSocket
	 * @return ret 返回具体的错误码，错误码详细值参考NDK.java
	 */
	public int cmdOperate()
	{
		byte[] rBuf = new byte[50];
		Gui gui = new Gui(myactivity, myHandler);
		int ret1;
		int status;
		
		if((ret1=getBtName(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取蓝牙名字失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		
		if((ret1=getBtMac(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取蓝牙MAC失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		
		if((ret1=openCash(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道开钱箱失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		
		if((ret1 = getBtVersion(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取蓝牙版本失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		
		if((ret1=getBps(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取波特率失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		
		if((ret1 = btBack(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道回连设置失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		//配置中已设置合法性验证的开启和关闭，由于该项的设置需reset底座才生效，否则异常，故去掉此处，避免造成代码混乱
		/*if((ret1 = btAuth(gui))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道认证控制失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}*/
		// 状态获取，目前应为03，状态获取变成了上一个命令操作
		if((ret1 = cmdSendRecv(Command.generateCommand(0x06, 0xE0, null).pack(), rBuf))!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取状态失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		Command statusComm = Command.decode(rBuf);
		status = statusComm.getData()[0]>>1;
		if((ret1 = statusComm.getCommand())!=0x06)//返回的命令中判断是否包含发送的命令
		{
			gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取状态返回错误(ret = 0x0%x)", Tools.getLineInfo(),ret1);
			return ret1;
		}
		gui.cls_show_msg1_record(TAG, "cmdOperate",uiTime, "获取状态操作成功");

		/*if(GlobalVariable.Auth_Control==0)
		{
			// 不开启认证控制
			if((ret1 = cmdSendRecv(Command.generateCommand(0x05, 0xE0, new byte[]{0x00}).pack(), rBuf))!=BT_OK)
			{
				gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道不开启认证控制失败(ret = 0x0%x)", Tools.getLineInfo(),ret1);
				return ret1;
			}
			Command authComm = Command.decode(rBuf);
			if((ret1 = authComm.getData()[0])==1||(ret2 = authComm.getCommand())!=0x05)
			{
				gui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道不开启认证控制返回错误(ret = 0x0%x)", Tools.getLineInfo(),ret1);
				return ret1;
			}
			gui.cls_show_msg1_record(TAG, "cmdOperate",uiTime, "配置中设置为不开启，不开启认证控制操作成功");
		}*/
		return BT_OK;
	}
	
	/**
	 * 获取蓝牙名字
	 * @param gui
	 * @return
	 */
	private int getBtName(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		
		// 获取蓝牙名字
		if((ret = cmdSendRecv(Command.generateCommand(0x0B, 0x00, new byte[]{0x01}).pack(), rBuf))!=BT_OK)
		{
			return ret;
		}
		Command btNameComm = Command.decode(rBuf);
		if((ret = btNameComm.getCommand())!=0x0B)
		{
			return ret;
		}
		if(gui!=null)
			gui.cls_show_msg1_record(TAG, "getBtName",uiTime, "BT_NAME:%s", ISOUtils.ASCII2String(btNameComm.getData()));
		else
			mBtName = ISOUtils.ASCII2String(btNameComm.getData()).trim();
		return BT_OK;
	}
	
	/**
	 * 获取蓝牙地址
	 * @param gui
	 * @return
	 */
	private int getBtMac(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		
		// 获取蓝牙mac地址，mac地址需要反序
		if((ret = cmdSendRecv(Command.generateCommand(0x0C, 0x00, null).pack(), rBuf))!=BT_OK)
		{
			return ret;
		}
		Command btMacComm = Command.decode(rBuf);
		if((ret = btMacComm.getCommand())!=0x0C)
		{
			return ret;
		}
		// 返回结果为小端格式，什么是小端格式，6个字节
		byte[] macInit = btMacComm.getData();
		 ByteArrayOutputStream bout = new ByteArrayOutputStream();
		 int len = macInit.length;
		 for (int i = 0; i < len; i++) 
		 {
			 bout.write(macInit[len-1-i]);
		}
		 if(gui!=null)
			 gui.cls_show_msg1_record(TAG, "getBtMac",uiTime, "BT_MAC:%s", Dump.getHexDump(bout.toByteArray()));
		 else
			 mBtMac = Dump.getHexDump(bout.toByteArray());
		// 关闭字节流
		try 
		{
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BT_OK;
	}
	
	/**
	 * 开启钱箱
	 * @param gui
	 * @return
	 */
	private int openCash(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		// 开启钱箱
		if((ret = cmdSendRecv(Command.generateCommand(0x01, 0xE0, new byte[]{0x01}).pack(), rBuf))!=BT_OK)
		{
			return ret;
				
		}
		Command cashComm = Command.decode(rBuf);
		if((ret = cashComm.getData()[0])!=0/*||(ret2 = cashComm.getCommand())!=0x01*/)
		{
			return ret;
		}
		gui.cls_show_msg1_record(TAG, "openCash",uiTime, "开启钱箱操作成功");
		return BT_OK;
	}
	
	/**
	 * 获取蓝牙版本
	 * @param gui
	 * @return
	 */
	private int getBtVersion(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		// 获取蓝牙版本
		if((ret = cmdSendRecv(Command.generateCommand(0x02, 0xE0, null).pack(), rBuf))!=BT_OK)
		{
			return ret;
		}
		Command btVerComm = Command.decode(rBuf);
		if((ret = btVerComm.getCommand())!=0x02)
		{
			return ret;
		}
		if(gui!=null)
			gui.cls_show_msg1_record(TAG, "getBtVersion",uiTime, "BT_VERSION:%s", ISOUtils.ASCII2String(btVerComm.getData()));
		else
			mBtVersion = ISOUtils.ASCII2String(btVerComm.getData()).trim();
		return BT_OK;
	}
	
	/**
	 * 获取串口波特率值
	 * @param gui
	 * @return
	 */
	private int getBps(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		// 获取蓝牙版本
		if((ret = cmdSendRecv(Command.generateCommand(0x03, 0xE0, null).pack(), rBuf))!=BT_OK)
		{
			return ret;
		}
		Command btBps = Command.decode(rBuf);
		if((ret = btBps.getCommand())!=0x03)
		{
			return ret;
		}
		if(gui!=null)
			gui.cls_show_msg1_record(TAG, "getBps",uiTime, "BT_BPS:%d", bpsTransmit(btBps.getData()));
		else
			mBtBps = bpsTransmit(btBps.getData())+"";
		return BT_OK;
	}
	
	/**
	 * 回连控制
	 * @param gui
	 * @return
	 */
	private int btBack(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		// 不开启回连控制
		if((ret = cmdSendRecv(Command.generateCommand(0x04, 0xE0, new byte[]{0x00}).pack(), rBuf))!=BT_OK)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道不开启回连控制失败(ret=%d)", Tools.getLineInfo(),ret);
			return ret;
		}
		Command backComm = Command.decode(rBuf);
		if((ret = backComm.getData()[0])==1/*||(ret2 = backComm.getCommand())!=0x04*/)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道不开启回连控制返回错误(ret = %d，Command = 0x0%x)", Tools.getLineInfo(),ret1,ret2);
			return ret;
		}
		gui.cls_show_msg1_record(TAG, "btBack",uiTime, "不开启回连控制命令操作成功");
		// 开启回连功能
		if((ret = cmdSendRecv(Command.generateCommand(0x04, 0xE0, new byte[]{0x01}).pack(), rBuf))!=BT_OK)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道开启回连控制失败(ret = %d)", Tools.getLineInfo(),ret1);
			return ret;
		}
		backComm = Command.decode(rBuf);
		if((ret = backComm.getData()[0])==1/*||(ret2 = backComm.getCommand())!=0x04*/)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道开启回连控制返回错误(ret =%d，Command = 0x0%x)", Tools.getLineInfo(),ret1,ret2);
			return ret;
		}
		gui.cls_show_msg1_record(TAG, "btBack",uiTime, "开启回连控制命令操作成功");
		return BT_OK;
	}
	
	private int btAuth(Gui gui)
	{
		int ret;
		byte[] rBuf = new byte[50];
		// 不开启认证控制
		if((ret = cmdSendRecv(Command.generateCommand(0x05, 0xE0, new byte[]{0x00}).pack(), rBuf))!=BT_OK)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道不开启认证控制失败(ret = %d)", Tools.getLineInfo(),ret1);
			return ret;
		}
		Command authComm1 = Command.decode(rBuf);
		if((ret = authComm1.getData()[0])==1/*||(ret2 = authComm1.getCommand())!=0x05*/)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道不开启认证控制返回错误(ret = %d，Command = 0x0%x)", Tools.getLineInfo(),ret1,ret2);
			return ret;
		}
		gui.cls_show_msg1_record(TAG, "btAuth",uiTime, "不开启认证控制操作成功");
		
		// 开启认证控制
		if((ret = cmdSendRecv(Command.generateCommand(0x05, 0xE0, new byte[]{0x01}).pack(), rBuf))!=BT_OK)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道开启认证控制失败(ret = %d)", Tools.getLineInfo(),ret1);
			return ret;
		}
		Command authComm2 = Command.decode(rBuf);
		if((ret = authComm2.getData()[0])==1/*||(ret2 = authComm2.getCommand())!=0x05*/)
		{
//			gui.cls_show_msg1(g_keeptime, "line %d:命令通道开启认证控制返回错误(ret = %d，Command= 0x0%x)", Tools.getLineInfo(),ret1,ret2);
			return ret;
		}
		gui.cls_show_msg1_record(TAG, "btAuth",uiTime, "开启认证控制操作成功");
		return BT_OK;
	}
	
	/**
	 * 将字节数组的波特率转换为字符串
	 * @param bps
	 */
	public int bpsTransmit(byte[] bps)
	{
		int bpsValue = (bps[0]&0x0f+((bps[0]&0xf0)>>4)*16)+((bps[1]&0x0f)*(16*16)+((bps[1]&0xf0)>>4)*(16*16*16))+(bps[2]&0x0f)*(16*16*16*16)+
				((bps[2]&0xf0)>>4)*(16*16*16*16*16)+(bps[3]&0x0f)*(16*16*16*16*16*16)+((bps[3]&0xf0)>>4)*(16*16*16*16*16*16*16);
				
		return bpsValue;
	}
	
	/**
	 * 命令数据通道1：命令通道和数据通道均为连接状态下进行数据的通讯操作 add by zhengxq 20170410
	 */
	public void cmdDataCh1(int num)
	{
		/* private & local definition */
		Gui dataGui = new Gui(myactivity, dataHandler);
		
		/* process body */
		// 获取合法性认证状态，命令通道已经建立
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		dataGui.cls_show_msg1(2, "建立数据通道链路中...");
		if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "cmdDataCh1",g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if(authBit==1)
		{
			if(legalityComm(dataService,dataGui)!= BT_OK)
				return;
		}
		
		switch (num) 
		{
		case 2:
			if(cmdOperate()!=BT_OK)
				return;
			if(dataSendRecv()!=BT_OK)
				return;
			commGUi.cls_show_msg1(2, "命令通道与数据通道均连接->通讯->断开成功");
			break;
			
		case 3:
			cmdDataCh2();
			break;
			
		case 4:
			cmdDataCh3();
			break;
			
		case 5:
			cmdDataCh4();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 命令数据通道2：命令通道回连状态，数据通道均为连接状态，进行数据的通讯操作 add by zhengxq 20170410
	 */
	public void cmdDataCh2()
	{
		/* private & local definition */
		Gui cmdGui = new Gui(myactivity, myHandler);
		
		/* process body */
//		cmdConnect = false;
		AcceptThread cmdThread = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdThread.start();
		bluetoothManager.cancel(cmdService);
		
		cmdGui.cls_show_msg1(2, "命令通道已断开，命令通道回连中...");
		// 回连操作
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<30)
		{
			if(bean.isIscmdback()==true)
			{
				cmdGui.cls_show_msg1(2, "命令通道回连成功");
				break;
			}
		}
		if(bean.isIscmdback()==false)
		{
			cmdThread.cancel();
			cmdGui.cls_show_msg1_record(TAG, "cmdDataCh2",g_keeptime, "line %d：命令通道回连失败(命令通道=%s)", Tools.getLineInfo(),cmdThread);
			return;
		}
		if(cmdOperate()!=BT_OK)
			return;
		
		if(dataSendRecv()!=BT_OK)
			return;
		cmdGui.cls_show_msg1(2, "命令通道回连->通讯成功，数据通道连接->通讯成功");
	}
	
	/**
	 * 命令数据通道3：数据通道回连状态，命令通道连接状态，进行数据的通讯操作 add by zhengxq 20170410
	 */
	public void cmdDataCh3()
	{
		/* private & local definition */
		Gui dataGui = new Gui(myactivity, dataHandler);
		long startTime;
		
		/* process body */
//		dataConenct = false;
		AcceptThread dataThread = new AcceptThread(dataService,UUID_DATA,bean);
		dataThread.start();
		bluetoothManager.cancel(dataService);
		dataGui.cls_show_msg1(2, "数据通道已断开，数据通道回连中......");
		// 回连操作
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIsdateback()==true)
			{
				dataGui.cls_show_msg1(2, "数据通道回连成功");
				break;
			}
		}
		if(bean.isIsdateback()==false)
		{
			dataThread.cancel();
			dataGui.cls_show_msg1_record(TAG, "cmdDataCh3",g_keeptime, "line %d：数据通道回连失败(数据通道=%s)", Tools.getLineInfo(),bean.isIsdateback());
			return;
		}
		if(cmdOperate()!=BT_OK)
			return;
		if(dataSendRecv()!=BT_OK)
			return;
		commGUi.cls_show_msg1(2, "数据通道回连->通讯成功，命令通道连接->通讯成功");
	}
	
	/**
	 * 命令数据通道4：数据和命令通道均为回连状态，进行数据的通讯操作 add by zhengxq 20170410
	 */
	public void cmdDataCh4()
	{
		/* private & local definition */
		long startTime;
		
		/* process body */
//		cmdConnect =false;
//		dataConenct = false;
		// 监听器
		AcceptThread cmdAccept = new AcceptThread(cmdService, UUID_CMD, bean);
		cmdAccept.start();
		AcceptThread dataAccept = new AcceptThread(dataService, UUID_DATA, bean);
		dataAccept.start();
		
		commGUi.cls_show_msg1(2, "命令通道、数据通道即将断开。。。");
		bluetoothManager.cancel(cmdService);
		bluetoothManager.cancel(dataService);
		
		commGUi.cls_show_msg1(2, "命令通道、数据通道回连中......");
		// (1)命令通道、数据通道回连操作
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
				break;
			}
		}
		if(bean.isIscmdback() == false||bean.isIsdateback()==false)
		{
			cmdAccept.cancel();
			dataAccept.cancel();
			commGUi.cls_show_msg1_record(TAG, "cmdDataCh4",g_keeptime, "line %d：命令通道、数据通道回连失败(数据通道=%s，命令通道=%s)", Tools.getLineInfo(),bean.isIsdateback(),bean.isIscmdback());
			return;
		}
		
		if(cmdOperate()!=BT_OK)
			return;
		/*if(cmdOther(rBuf)!=BT_OK)
			return;*/
		
		if(dataSendRecv()!=BT_OK)
			return;
		commGUi.cls_show_msg1(2, "命令通道回连->通讯成功，数据通道回连->通讯成功");
	}
	
	// 合法性共同部分代码，传入的gui用于判断目前是数据通道还是命令通道
	public int legalityComm(BluetoothService bluetoothService,Gui gui)
	{
		/* private & local definition */
		byte[] rbuf = new byte[8];
		byte[] backBuf = new byte[10];
		byte[] timeBuf = new byte[8];
		byte[] helloBuf = {0x68,0x65,0x6C,0x6C,0x6F,0x00,0x00,0x00};
		byte[] okBuf = {0x6F,0x6B,0x21,0x00,0x00,0x00,0x00,0x00};
		gui.cls_show_msg1(1, "等待底座主动发送数据");
		// 收发数据
		// 不需要合法性验证，直接返回
		if(bluetoothManager.readComm(bluetoothService,backBuf)==false)
		{
			return BT_READ_FAILED;
		}
			
		// 比较接收到的数据是不是以 0xfe 0x01
		if(!Tools.memcmp(backBuf, new byte[]{(byte) 0xFE,0x01}, 2))
		{
			gui.cls_show_msg1(g_keeptime, "line %d:从底座接收的数据格式错误ret = %s", Tools.getLineInfo(),Dump.getHexDump(backBuf));
			return BT_COMPARE_FAILED;
		}
		
		// 生成密钥以及加密的hello键
		System.arraycopy(backBuf, 2, timeBuf, 0, 4);
		byte[] desKey = generalDesKey(timeBuf);
		gui.cls_show_msg1(1, "发送加密的hello数据");
		byte[] helloData = DesTool.encrypt(desKey, helloBuf);
		byte[] sendData = new byte[9];
		System.arraycopy(helloData, 0, sendData, 0, 8);
		bluetoothManager.writeComm(bluetoothService,sendData);
		SystemClock.sleep(1000);
		bluetoothManager.readComm(bluetoothService,rbuf);
		// 加密的OK数据
		okBuf = DesTool.encrypt(desKey, okBuf);
		if(Tools.memcmp(okBuf, rbuf, okBuf.length)==false)
		{
			gui.cls_show_msg1_record(TAG, "legalityComm",g_keeptime, "line %d:%s比较数据失败，预期=%s,实际=%s", Tools.getLineInfo(),TESTITEM,
					Dump.getHexDump(okBuf),Dump.getHexDump(backBuf));
			return BT_COMPARE_FAILED;
		}
		return BT_OK;
	}
	
	public byte[] generalDesKey(byte[] timeBuf)
	{
//		deviceLogger.error("timeBuf:"+Dump.getHexDump(timeBuf));
		byte[] initKey = new byte[8];
		byte[] endKey = new byte[8];
		byte[] backTimeBuf = new byte[8];
		Arrays.fill(initKey, (byte) 0x11);
		byte[] encrypt1 = DesTool.encrypt(initKey, timeBuf);
//		deviceLogger.error("encrypt1:"+Dump.getHexDump(encrypt1));
		for (int i = 0; i < 4; i++) 
		{
			backTimeBuf[i] = (byte) ~timeBuf[i];
		}
//		deviceLogger.error("backTime:"+Dump.getHexDump(backTimeBuf));
		byte[] encrypt2 = DesTool.encrypt(initKey, backTimeBuf);
//		deviceLogger.error("encrypt2:"+Dump.getHexDump(encrypt2));
		for (int i = 0; i < 8; i++) 
		{
			endKey[i] = (byte) (encrypt1[i]^encrypt2[i]);
		}
//		deviceLogger.error("endKey:"+Dump.getHexDump(endKey));
		return endKey;
	}
	
	
	// 底座压力 modify by zhengxq 20170405
	public void preFun() 
	{
		int nkeyIn = commGUi.cls_show_msg("底座压力\n0.两路均连接+通讯(通讯压力)\n1.两路均回连+通讯(流程压力)\n2.POS重启回连压力\n3.底座上下电回连压力\n");
		switch (nkeyIn) 
		{
		// 数据通道、命令通道均为连接状态下进行通讯
		case '0':
			connCommPre();
			break;
		
		// 数据通道、命令通道均为回连状态下进行通讯
		case '1':
			backCommPre();
			break;
			
		// 多次重启回连 add by wangxy 20181214
		case '2':
			PosRebootPre();
			break;
			
		//设置波特率后，底座上下电后，波特率获取一致且数据通讯可用 add by wangxy 20181218
		case '3':
			DonglePowerPre();
			break;
			
		case ESC:
			return;
		}
	}
	

	/**
	 * 连接+通讯压力 add by zhengxq 20170410
	 */
	public void connCommPre()
	{
		/* private & local definition */
		int succ=0,ret = 0;
		int cnt,bak = 0;
		Gui cmdGui = new Gui(myactivity, myHandler);
		Gui dataGui = new Gui(myactivity, dataHandler);

		/* process body */
		// 压力次数设置
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(commGUi.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		// 获取合法性认证状态
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		// 数据通道建立
		dataGui.cls_show_msg1(2, "建立数据通道链路中...");
		if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "connCommPre",g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if(authBit==1)
		{
			if(legalityComm(dataService,dataGui)!= BT_OK)
				return;
		}
		// 此时数据通道、命令通道均为连接状态下，进行数据通讯、命令交互操作
		while(cnt>0)
		{
			if(commGUi.cls_show_msg1(3, "数据通道、命令通道通讯压力中...还剩%d次（已成功%d次），【取消】键退出测试...",cnt,succ)==ESC)
				break;
			
			cnt--;
			if((ret = cmdOperate())!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "connCommPre", g_keeptime, "line %d:第%d次命令通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((ret = dataSendRecv())!=BT_OK)
			{
				dataGui.cls_show_msg1_record(TAG, "connCommPre", g_keeptime, "line %d:第%d次数据通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		commGUi.cls_show_msg1_record(TAG, "connCommPre",g_keeptime, "数据通道、命令通道连接+通讯测试完成，已执行次数为%d，成功%d次", bak-cnt,succ);
	}
	
	/**
	 * 回连+通讯压力 add by zhengxq 20170410
	 */
	public void backCommPre()
	{
		/* private & local definition */
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		long startTime;
		int succ=0,ret = 0;
		int cnt,bak = 0;

		/* process body */
		// 获取合法性认证状态
		int authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		dataGui.cls_show_msg1(2, "建立数据通道链路中...");
		if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "backCommPre",g_keeptime, "line %d:数据通道建立连接失败", Tools.getLineInfo());
			return;
		}
		if(authBit==1)
		{
			if(legalityComm(dataService,dataGui)!= BT_OK)
				return;
		}
		// 压力次数设置
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(commGUi.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		// 进行回连+通讯压力测试
		while(cnt>0)
		{
			if(commGUi.cls_show_msg1(3, "数据通道、命令通道回连+通讯压力中...还剩%d次（已成功%d次），点击退出键退出...",cnt,succ)==ESC)
				break;
			cnt--;
			// 命令通道断开 回连操作
//			cmdConnect = false;
//			dataConenct = false;
			AcceptThread cmdThread = new AcceptThread(cmdService,UUID_CMD,bean);
			cmdThread.start();
			AcceptThread dataThread = new AcceptThread(dataService,UUID_DATA,bean);
			dataThread.start();
			
			commGUi.cls_show_msg1(2, "命令通道、数据通道即将断开。。。");
			bluetoothManager.cancel(cmdService);
			bluetoothManager.cancel(dataService);
			
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
			{
				if(bean.isIscmdback()==true&&bean.isIsdateback()==true)
				{
					commGUi.cls_show_msg1(2, "命令通道、数据通道回连成功");
					break;
				}
			}
			if(bean.isIscmdback()==false||bean.isIsdateback()==false)
			{
				cmdThread.cancel();
				dataThread.cancel();
				commGUi.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:第%d次命令通道、数据通道回连失败(命令通道=%s，数据通道=%s)", Tools.getLineInfo(),bak-cnt,bean.isIscmdback(),bean.isIsdateback());
				// 将监听的给断开
				continue;
			}

			if((ret = cmdOperate())!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:第%d次命令通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if(dataSendRecv()!=BT_OK)
			{
				dataGui.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:第%d次数据通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		commGUi.cls_show_msg1_record(TAG, "backCommPre",g_keeptime, "数据通道、命令通道回连+通讯测试完成，已执行次数为%d，成功%d次", bak-cnt,succ);
	}
	
	
	// add by 20160830
	// 蓝牙底座处于连接状态时复位不应异常
	public void dongleAbnormal() 
	{
		int returnValue=commGUi.cls_show_msg("底座异常\n0.连接-复位\n1.合法性验证\n2.休眠唤醒异常\n3.关闭回连\n4.多手机连接1底座\n5.1手机搜索多底座\n6.最大连接数");
		switch (returnValue) {
		case '0':
			abnormal1();
			break;
			
		case '1':
			abnormal2();
			break;
			
		case '2':
			abnormal3();
			break;
			
		case '3':
			abnormal4();
			break;
			
		case '4':
			abnormal5();
			break;
			
		case '5':
			abnormal6();
			break;
			
		case '6':// 最大连接数
			abnormal7();
			break;
			
		case ESC:
			break;

		}
	}
	
	public void cancelChoose()
	{
		int returnValue=commGUi.cls_show_msg("断开选择\n0.断开命令通道\n1.断开数据通道\n2.断开双通道");
		switch (returnValue) 
		{
		case '0':
			bluetoothManager.cancel(cmdService);
			break;
			
		case '1':
			bluetoothManager.cancel(dataService);
			break;
			
		case '2':
			bluetoothManager.cancel(cmdService);
			bluetoothManager.cancel(dataService);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 连接-复位异常（数据、命令通道两路连接、复位，复位进行压力操作应正常） add by zhengxq 20170412
	 */
	public void abnormal1()
	{
		// 配对的时候点击复位按钮
		long startTime;
		int authBit,ret;
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		
		commGUi.cls_show_msg1(2, "蓝牙底座异常1测试...");
		for (int i = 0; i < 2; i++) 
		{
			authBit = getStatus();
			if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
			{
				return;
			}
			// 数据路
			if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
			{
				dataGui.cls_show_msg1_record(TAG, "abnormal1",g_keeptime, "line %d:数据路建立连接失败", Tools.getLineInfo());
				return;
			}
			if(authBit==1)
				legalityComm(dataService,dataGui);
//			cmdConnect = false;
//			dataConenct = false;
			// 命令路回连
			AcceptThread cmdThread = new AcceptThread(cmdService,UUID_CMD,bean);
			cmdThread.start();
			// 数据路回连
			AcceptThread dataThread = new AcceptThread(dataService,UUID_DATA,bean);
			dataThread.start();
			
			commGUi.cls_show_msg("请将底座断电再上电，完成任意键继续");
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
			{
				if(bean.isIscmdback() == true&&bean.isIsdateback() == true)
				{
					commGUi.cls_show_msg1(1, "命令通道和数据通道均回连成功");
					break;
				}
			}
			if(bean.isIscmdback()==false||bean.isIsdateback()==false)
			{
				cmdThread.cancel();
				dataThread.cancel();
				commGUi.cls_show_msg1_record(TAG, "abnormal1",g_keeptime, "line %d:命令通道、数据通道回连失败(数据通道=%s，命令通道)",Tools.getLineInfo(),bean.isIsdateback(),bean.isIscmdback());
				return;
			}
			// 数据通讯
			if((ret = cmdOperate())!=BT_OK)
			{
				cmdGui.cls_show_msg1_record(TAG, "abnormal1",g_keeptime, "line %d:命令通道发送命令失败(ret = %d)", Tools.getLineInfo(),ret);
				return;
			}
			if((ret = dataSendRecv())!=BT_OK)
			{
				dataGui.cls_show_msg1_record(TAG, "abnormal1",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),ret);
				return;
			}
			if(commGUi.ShowMessageBox("重新建立连接成功，请将蓝牙底座复位，指示灯是否转变为闪烁状态".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
			{
				commGUi.cls_show_msg1_record(TAG, "abnormal1",g_keeptime, "line %d:%s连接状态转变为复位状态失败",Tools.getLineInfo(),TESTITEM);
				return;
			}
		}
		commGUi.cls_show_msg1_record(TAG, "abnormal1",g_keeptime, "蓝牙底座连接-复位异常测试通过");
	}
	
	/**
	 * 合法性验证异常（数据路异常、命令路正常） add by zhengxq 20170412
	 */
	public void abnormal2()
	{
		/* private & local definition */
		byte[] backBuf = new byte[10];
		byte[] timeBuf = new byte[8];
		byte[] rBuf = new byte[PACKLEN];
		byte[] helloBuf = {0x68,0x65,0x6C,0x6C,0x6F,0x00,0x00,0x00};
		int authBit,ret;
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		
		/* process body */
		// 测试前请开启合法性认证功能
		if (commGUi.ShowMessageBox("测试前请先确认底座是否开启合法性认证，否则请在配置中开启底座合法性".getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				GlobalVariable.WAITMAXTIME) != BTN_OK)
			return;
		
		authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		byte[] desKey;
		byte[] sendBuf = new byte[9];
		
		int returnValue=cmdGui.cls_show_msg("合法性异常\n0.5s后发数据\n1.发错误数据");
		switch (returnValue) 
		{
		case '0':
			dataGui.cls_show_msg1(1, "等待5s后发送加密的hello数据给底座");
			// 连接数据路
			if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false){
				dataGui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:数据路建立连接失败", Tools.getLineInfo());
				break;
			}
//			dataGui.cls_show_msg1(1, "数据通道等待底座主动发送数据");
			// 收发数据
			if (bluetoothManager.readComm(dataService, backBuf) == false)
				dataGui.cls_show_msg1_record(TAG, "abnormal2", g_keeptime, "line %d:数据路read失败", Tools.getLineInfo());
			SystemClock.sleep(5000);
			// 生成密钥以及加密的hello键
			System.arraycopy(backBuf, 2, timeBuf, 0, 4);
			desKey = generalDesKey(timeBuf);
			helloBuf = DesTool.encrypt(desKey, helloBuf);
			System.arraycopy(helloBuf, 0, sendBuf, 0, 8);
			bluetoothManager.writeComm(dataService,sendBuf);
			break;

		case '1':
			dataGui.cls_show_msg1(1, "发送错误数据给底座");
			// 连接数据路
			if (bluetoothManager.connComm(dataService, CHANEL_DATA) == false) {
				dataGui.cls_show_msg1_record(TAG, "abnormal2", g_keeptime, "line %d:数据路建立连接失败", Tools.getLineInfo());
				break;
			}
			// dataGui.cls_show_msg1(1, "数据通道等待底座主动发送数据");
			// 收发数据
			if (bluetoothManager.readComm(dataService, backBuf) == false)
				dataGui.cls_show_msg1_record(TAG, "abnormal2", g_keeptime, "line %d:数据路read失败", Tools.getLineInfo());
			SystemClock.sleep(5000);
			// 生成密钥以及加密的hello键
			System.arraycopy(backBuf, 2, timeBuf, 0, 4);
			desKey = generalDesKey(timeBuf);
			helloBuf = DesTool.encrypt(desKey, helloBuf);
			System.arraycopy(helloBuf, 0, sendBuf, 0, 8);
			bluetoothManager.writeComm(dataService,new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
			break;
		default:
			break;
		}
		// 命令路可通讯，数据路无法通讯
		if((ret = cmdOperate())!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:命令通道发送命令失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = dataSendRecv())==BT_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal2",g_keeptime, "line %d:数据通道断开连接失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if(GlobalVariable.FLAG_SYSTEM_SIGN)
			commGUi.cls_show_msg1(2, "蓝牙底座异常测试2测试通过，能看到测试通过提示语即为测试通过，前面的错误提示语请忽略，指示灯应快闪3下，灭一下");
		else
			commGUi.cls_show_msg1(1, "line %d:蓝牙底座异常测试2测试失败", Tools.getLineInfo());
	}
	
	/**
	 * 休眠唤醒异常（连接状态下，回连成功状态下） add by zhengxq 20170412
	 */
	public void abnormal3()
	{
		/* private & local definition */
		int authBit,ret;
		byte[] rBuf = new byte[PACKLEN];
		Gui cmdGui = new Gui(myactivity, myHandler);
		Gui dataGui = new Gui(myactivity, dataHandler);
		long startTime;
		
		/* process body */
		//case1:两个通道均连接成功时进入休眠，休眠唤醒后仍应连接成功
		commGUi.cls_show_msg1(2, "数据通道、命令通道连接状态进入休眠");
		authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道建立失败", Tools.getLineInfo());
			return;
		}
		if(authBit==1)
			legalityComm(dataService,dataGui);
		settingsManager.setScreenTimeout(ONE_MIN);
		for (int j = 0; j <= 30; j++) 
		{
			commGUi.cls_show_msg1(2, "1分钟倒计时，请不要点击屏幕，剩余时间%ds，休眠10s后请手动唤醒设备", 60-j*2);
		}
		// 休眠唤醒的标志
		GlobalVariable.isWakeUp=false;
		while(!GlobalVariable.isWakeUp);
		if((ret = cmdOperate())!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:命令通道发送命令失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = dataSendRecv())!=BT_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		commGUi.cls_show_msg1(2, "子用例1测试通过");
		
		// case2:回连状态进入休眠，唤醒后应回连成功并通讯成功
//		cmdConnect = false;
//		dataConenct = false;
		//断开连接
		bluetoothManager.cancel(cmdService);
		bluetoothManager.cancel(dataService);
		commGUi.cls_show_msg1(2, "命令通道、数据通道均回连状态进入休眠");
		
		// 一分钟后自动进入休眠，休眠唤醒后通讯应正常
		settingsManager.setScreenTimeout(ONE_MIN);
		for (int j = 0; j <= 30; j++) 
		{
			commGUi.cls_show_msg1(2, "1分钟倒计时，请不要点击屏幕，剩余时间%ds，休眠10s后请手动唤醒设备", 60-j*2);
		}
		// 休眠唤醒的标志
		GlobalVariable.isWakeUp=false;
		while(!GlobalVariable.isWakeUp);
		//唤醒后进行回连操作
		AcceptThread cmdThread = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdThread.start();
		AcceptThread dataThread = new AcceptThread(dataService,UUID_DATA,bean);
		dataThread.start();
		// 数据通道、命令通道回连
		startTime = System.currentTimeMillis();
		while (Tools.getStopTime(startTime) < BACK_TIMEOUT) 
		{
			if (bean.isIscmdback() == true && bean.isIsdateback() == true) 
			{
				commGUi.cls_show_msg1(2, "命令通道及数据通道回连成功");
				break;
			}
		}
		if (bean.isIscmdback() == false || bean.isIsdateback() == false) 
		{
			commGUi.cls_show_msg1_record(TAG, "abnormal3", g_keeptime, "line %d:命令通道、数据通道回连失败(命令通道=%s，数据通道=%s)",
					Tools.getLineInfo(), bean.isIscmdback(), bean.isIsdateback());
			openBack(cmdService, rBuf, authBit);
			return;
		}
		
		if((ret = cmdOperate())!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:命令通道发送命令失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = dataSendRecv())!=BT_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		
		commGUi.cls_show_msg1_record(TAG, "abnormal3",g_keeptime, "蓝牙底座休眠唤醒异常测试通过");
	}
	
	/**
	 * 关闭回连功能后回连应失败 add by zhengxq 20170411
	 */
	public void abnormal4()
	{
		/* private & local definition */
		byte[] rBuf = new byte[PACKLEN];
		Gui cmdGui = new Gui(myactivity, myHandler);
		Gui dataGui = new Gui(myactivity, dataHandler);
		long startTime;
		int ret;
		int authBit;
		
		/* process body */
		commGUi.cls_show_msg1(2, "蓝牙底座关闭回连测试...");
		// 命令通道已建立
		authBit = getStatus();
		if(authBit == BT_CONNECT_FAILED||authBit==BT_COMPARE_FAILED)
		{
			return;
		}
		// 不开启回连控制
		Arrays.fill(rBuf, (byte) 0x00);
		if((ret = cmdSendRecv(Command.generateCommand(0x04, 0xE0, new byte[]{0x00}).pack(), rBuf))!=BT_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal4",g_keeptime, "line %d:命令通道不开启回连控制失败(ret=%d)", Tools.getLineInfo(),ret);
			return;
		}
		
		// 建立数据通道，应无法回连成功
		if(bluetoothManager.connComm(dataService, CHANEL_DATA)==false)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal4",g_keeptime, "line %d:数据通道建立失败", Tools.getLineInfo());
			openBack(cmdService, rBuf,authBit);
			return;
		}
		if(authBit==1)
			legalityComm(dataService, dataGui);
		
//		cmdConnect = false;
//		dataConenct = false;
		// 建立命令通道，应无法回连成功
		AcceptThread cmdThread = new AcceptThread(cmdService,UUID_CMD,bean);
		cmdThread.start();
		
		AcceptThread dataThread = new AcceptThread(dataService,UUID_DATA,bean);
		dataThread.start();
		bluetoothManager.cancel(cmdService);
		bluetoothManager.cancel(dataService);
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(bean.isIscmdback()==true&&bean.isIsdateback() == true)
			{
				commGUi.cls_show_msg1(2, "命令通道及数据通道回连成功");
				break;
			}
			cmdThread.cancel();
			dataThread.cancel();
		}
		if(bean.isIscmdback()==true||bean.isIsdateback()==true)
		{
			commGUi.cls_show_msg1_record(TAG, "abnormal4",g_keeptime, "line %d:命令通道、数据通道回连失败(命令通道=%s，数据通道=%s)", Tools.getLineInfo(),bean.isIscmdback(),bean.isIsdateback());
			openBack(cmdService, rBuf,authBit);
			return;
		}
		// 测试后置：开启回连控制
		openBack(cmdService, rBuf,authBit);
		commGUi.cls_show_msg1_record(TAG, "abnormal4",g_keeptime, "蓝牙底座关闭回连异常测试通过");
	}
	
	/**
	 * 开启回连控制操作 
	 * @param cmdService
	 * @param rBuf
	 */
	public void openBack(BluetoothService cmdService,byte[] rBuf,int authBit)
	{
		// 命令通道gui
		Gui gui = new Gui(myactivity, myHandler);
		
		commGUi.cls_show_msg("请将蓝牙底座复位，任意键继续");
		if(bluetoothManager.connComm(cmdService, CHANEL_CMD)==false)
		{
			commGUi.cls_show_msg1_record(TAG, "openBack",g_keeptime, "line %d:命令通道建立失败", Tools.getLineInfo());
			return;
		}
		if(authBit==1)
			if(legalityComm(cmdService,gui)!=BT_OK)
				return;
		// 测试后置：重新开启回连控制
		Arrays.fill(rBuf, (byte) 0x00);
		if(cmdSendRecv(Command.generateCommand(0x04, 0xE0, new byte[]{0x01}).pack(), rBuf)!=BT_OK)
		{
			gui.cls_show_msg1_record(TAG, "openBack",g_keeptime, "line %d:命令通道开启回连控制失败", Tools.getLineInfo());
			return;
		}
		if(Command.decode(rBuf).getData()[0]==1)
		{
			gui.cls_show_msg1_record(TAG, "openBack",g_keeptime, "line %d:开启回连控制命令发送成功(ret = %d)", Tools.getLineInfo());
			return;
		}
		bluetoothManager.cancel(cmdService);
	}
	
	/**
	 * 两个手机连接底座，应只可一个手机连接成功 add by zhengxq 20170411
	 */
	public void abnormal5()
	{
		commGUi.cls_show_msg1(2, "蓝牙底座多手机连接底座测试...");
		Gui dataGui = new Gui(myactivity, dataHandler);
		
		/* process body */
		commGUi.cls_show_msg("准备好两台Android设备，进入测试用例配置同一个蓝牙底座后再进入本case，请先将蓝牙底座复位，已配置完毕可忽略，任意键继续");
		// 连接命令通路或数据通路
		if(bluetoothManager.connComm(cmdService, CHANEL_CMD)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "line %d:本Android设备建立连接失败", Tools.getLineInfo());
			return;
		}
		commGUi.cls_show_msg1_record(TAG, "abnormal5",g_keeptime, "本Android设备建立连接成功，一台Android设备建立连接成功，一台Android设备建立连接失败则视为测试通过，反之测试不通过");
	}
	
	/**
	 * 1个手机应可搜索到多个蓝牙底座设备 add by zhengxq 20170411
	 */
	public void abnormal6()
	{
		commGUi.cls_show_msg1(2, "蓝牙底座1手机搜索多底座测试...");
		
		/* process body */
		commGUi.cls_show_msg("请将2至多个蓝牙底座复位，任意键继续");
		config.btConfig(pairList, unPairList, bluetoothManager);
		commGUi.cls_show_msg1_record(TAG, "abnormal6",g_keeptime, "搜索到的蓝牙底座个数与开启的蓝牙底座个数一致视为通过，反之视为不通过");
	}
	
	/**
	 * 数据路、命令路最大连接数为2，进行第三路连接应失败 add by zhengxq 20170420
	 */
	public void abnormal7()
	{
		/* private & local definition */
		Gui dataGui = new Gui(myactivity, dataHandler);
		Gui cmdGui = new Gui(myactivity, myHandler);
		String btAddr = getBtAddr();

		/* process body */
		// 测试前请开启合法性认证功能
		if(commGUi.ShowMessageBox("测试前请先对底座复位并开启合法性认证".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
			return;
		// 分别建立1路命令路、1路数据路
		BluetoothService cmdService1 = new BluetoothService(btAddr);
		
		if(bluetoothManager.connComm(cmdService1,CHANEL_CMD)==false)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "line %d:命令路第1路建立失败", Tools.getLineInfo());
			return;
		}
		if(legalityComm(cmdService1, cmdGui)!=BT_OK)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "line %d:命令路第1路合法性认证失败", Tools.getLineInfo());
			return;
		}
		cmdGui.cls_show_msg1(1, "命令路第1路次连接成功");
		
		// 命令路第2路应失败
		BluetoothService cmdService2 = new BluetoothService(btAddr);
		
		if(bluetoothManager.connComm(cmdService2,CHANEL_CMD)==true)
		{
			cmdGui.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "line %d:命令路第2路建立成功", Tools.getLineInfo());
			return;
		}

		BluetoothService dataService1 = new BluetoothService(btAddr);
		// 建立数据通道第1路
		if(bluetoothManager.connComm(dataService1,CHANEL_DATA)==false)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "line %d:数据路第1路建立失败", Tools.getLineInfo());
			return;
		}
		if(legalityComm(dataService1, dataGui)!=BT_OK)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "line %d:数据路第1路合法性认证失败", Tools.getLineInfo());
			return;
		}
		dataGui.cls_show_msg1(1, "数据路第1路连接成功");
		
		// 数据路
		BluetoothService dataService2 = new BluetoothService(btAddr);
		// 建立数据通道第1路
		if(bluetoothManager.connComm(dataService2,CHANEL_DATA)==true)
		{
			dataGui.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "line %d:数据路第2路建立成功", Tools.getLineInfo());
			return;
		}
		commGUi.cls_show_msg1_record(TAG, "abnormal7",g_keeptime, "测试通过：数据路最大连接数为1，命令路最大连接数为1");	
	}
	

	// 重写返回键
	@Override
	public void onBackDown() 
	{
		commGUi.cls_show_msg1(2, "即将退出测试");
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		Log.d("zxq--destroy", "enter destroy");
	}
	
}
