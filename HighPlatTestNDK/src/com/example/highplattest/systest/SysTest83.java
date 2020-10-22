package com.example.highplattest.systest;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.DefineListener;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DesTool;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.aidl.OnSearchListener;
import com.newland.NlBluetooth.control.BluetoothController;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest83.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 
 * directory 		: 
 * description 		: Dongle综合测试-纯蓝牙底座(双通道)
 * related document :
 * history 		 	: author			date			remarks
 * 					  zhengxq			20181229
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest83 extends DefaultFragment implements DefineListener.BackListener
{
	/*---------------constants/macro definition---------------------*/
	private final String TESTITEM = "纯蓝牙底座(双通道)";
	public final String TAG = SysTest83.class.getSimpleName();
	private final int PACKLEN = 50;
	
	/*----------global variables declaration------------------------*/
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private final int BACK_TIMEOUT = 15;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	 // 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Config config;
	// 公共界面显示
	private Gui commGUi = new Gui(myactivity, handler);
	private SharedPreferences sharedPreferences;
	private Editor editor;
	int[] bps_list = { 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400 };
	//NlBluetooth(蓝牙底座工具类) add by 20181229 zhengxq
	private BluetoothController mNlBluetooth=null;
	private String mBtName;
	private String mBtVersion;
	private String mBtMac;
	private String mBtBps;
	public final int SEND_DATA_LEN=1024;
	public final int DATA_BUFFER=4096;//透传的最大数据量4k
	private final int DATA_TIMEOUT = 15000;//透传接收超时时间
	private byte[] rbuf=new byte[DATA_BUFFER];//全功能底座透传最大4k
	private int i=0,len=0;
	private int ret=-1;
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{	
			case HandlerMsg.TEXTVIEW_DONGLE_CMD:// 命令通道显示界面
				mTvCmd.setText("命令通道->"+(CharSequence) msg.obj);
				break;
				
			case HandlerMsg.TEXTVIEW_DONGLE_DATA:
				mTvData.setText("数据通道->"+(CharSequence) msg.obj);
				break;
				
			default:
				break;
			}
		};
	};
	Gui mDataGui = new Gui(myactivity, myHandler,HandlerMsg.TEXTVIEW_DONGLE_DATA);
	Gui mCmdGui = new Gui(myactivity, myHandler,HandlerMsg.TEXTVIEW_DONGLE_CMD);
	
	public void systest83()
	{ 
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			commGUi.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		// 全局变量初始化操作
		
		mNlBluetooth=BluetoothController.getInstance();
		// addby wangxy 20190123,开发新增服务初始化接口
		mNlBluetooth.initService();
		OnSearchListener onSearchListener = new OnSearchListener.Stub()
		{
			@Override
			
			public void onDataReceive(int length,byte[] data) throws RemoteException 
			{
				LoggerUtil.e("监听器:接受数据长度length="+length);
				LoggerUtil.e("监听器:接收数据广播:"+data.length);
				length=length+data.length;
	        	len=len+length;
	        	LoggerUtil.e("监听器:总数据长度:"+len);
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
		mNlBluetooth.init(myactivity, onSearchListener);// 不使用此处OnSearchListener搜索蓝牙、蓝牙状态改变的监听
		sharedPreferences = myactivity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		GlobalVariable.isDongle = true;
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		config = new Config(myactivity, handler);

		while(true)
		{
//			mNlBluetooth.setLog(true);
			int returnValue=commGUi.cls_show_msg("蓝牙底座测试\n0.配置\n1.功能\n2.压力\n3.OTA升级");
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
			// OTA升级测试add by zsh 20190617
			case '3':
				OTAupdata();
				break;
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	// OTA升级测试add by zsh 20190617
	public void OTAupdata() {
		while(true)
		{
			int returnValue=commGUi.cls_show_msg("OTA测试\n1.手动升级测试\n2.半自动升级测试\n3.全自动升级测试");
			switch (returnValue) 
			{
			case '1':
				//case 1:工厂模式下,发送AT#BO进入OTA升级
				if(commGUi.ShowMessageBox(("进入工厂模式,发送AT#BO命令进入OTA升级,在pc端通过蓝牙Dangle,运行OTA软件升级,应升级成功,成功[确认],失败[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
				{
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime, "line %d:手动升级失败", Tools.getLineInfo());
					return;
				}else{
					commGUi.cls_show_msg("手动升级测试通过,请继续测试其他升级方式");
					}
				break;
			case '2':
				//半自动升级,底座发命令进入OTA:底座与N900连接,命令通道发送HCI_CONTROL_COMMAND_ENTER_OTA_DESK命令:19 07 E0 00 00,设备重启后会进入OTA模式,此时可以升级成功
				if("".equals(getBtAddr()))
				{
					commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
					return;
				}
				commGUi.cls_show_msg("请先使底座进入工厂模式,完成后任意键继续...");
				mNlBluetooth.pEnterOtaDesk();//发送命令进入OTA
				//case 1:工厂模式下,发送AT#BO进入OTA升级
				if(commGUi.ShowMessageBox(("底座重新上下电,黄蓝灯均常亮,在pc端通过蓝牙Dangle连接底座,运行OTA软件升级,应升级成功,成功[确认],失败[取消]").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
				{
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime, "line %d:半自动升级测试失败", Tools.getLineInfo());
					return;
				}else{
					commGUi.cls_show_msg("半自动升级测试通过,请继续测试其他升级方式");
				}
				break;
			case '3':
				//全自动升级,底座发送升级包
				int ret=0;
				if("".equals(getBtAddr()))
				{
					commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
					return;
				}
				commGUi.cls_show_msg("请将OTA升级文件导入SDK根目录下,完成后任意键继续...");
				String filepath="/mnt/shell/emulated/0/"+"N900-BTDESK-20190612-V1.1.11.bin";
				ret=mNlBluetooth.pUpgradeOta(filepath);
				switch(ret){
				case 0:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime, "OTA升级成功,全自动升级测试通过,请测试其他升级方式");
					break;
				case -3:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime,  "line %d:自动升级测试失败,发送OTA_CONTROL失败,ret=%d",Tools.getLineInfo(),ret);
					break;
				case -4:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime, "line %d:自动升级测试失败,发送文件长度失败,ret=%d",Tools.getLineInfo(),ret);
					break;
				case -5:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime,  "line %d:自动升级测试失败,发送升级文件失败,ret=%d",Tools.getLineInfo(),ret);
					break;
				case -6:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime,  "line %d:自动升级测试失败,参数错误,ret=%d",Tools.getLineInfo(),ret);
					break;
				case -7:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime,  "line %d:自动升级测试失败,发送CRC检验失败,ret=%d",Tools.getLineInfo(),ret);
					break;
				case -8:
					commGUi.cls_show_msg1_record(TAG, "OTAupdata", g_keeptime,  "line %d:自动升级测试失败,发送升级成功消息失败,ret=%d",Tools.getLineInfo(),ret);
					break;
				}
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
		int returnValue=commGUi.cls_show_msg("底座配置\n0.蓝牙选择\n1.合法性控制\n2.回连控制\n3.底座从模式连接\n4.数据收发长度设置\n5.蓝牙配对后自动连接\n");
		switch (returnValue) 
		{
		case '0':// 配对之后会自动连接 modify by 20190620
			config.btConfig(pairList, unPairList, bluetoothManager);
			if(unPairList.size()!=0)	{
				// 这个时候已经选择好蓝牙底座了，配置完毕直接进行配对
				bluetoothManager.pair(getBtAddr(), "0");	
				editor.putString("btAddr", getBtAddr());
				editor.putBoolean("isReboot", false);
				editor.commit();//提交修改
			}	
			else
			{
				commGUi.cls_show_msg1(2, "未搜索到蓝牙底座");
				return;
			}
			break;
			
		// 合法性控制
		case '1':
			if ("".equals(getBtAddr())) {
				commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				return;
			}
			if(mNlBluetooth.isConnectedA()==false){
				commGUi.cls_show_msg("请先进行蓝牙底座从模式连接操作");
				return;
			}
			int authKey = commGUi.cls_show_msg("合法性选择\n0.开启合法性认证\n其他.关闭合法性认证\n");
			if(authKey==ESC)
				return;
			if(mNlBluetooth.pSetAuthEnabled(authKey=='0'?true:false)==false)
			{
				commGUi.cls_show_msg1_record(TAG, "dongleConfig",g_keeptime, "line %d:底座设置合法性控制失败(false)", Tools.getLineInfo());
				return;
			}
			commGUi.cls_show_msg("蓝牙底座合法性认证:%s,任意键继续", authKey=='0'?"开启":"关闭");
			GlobalVariable.Auth_Control=authKey=='0'?1:0;
			break;
			
		case '2':
			if ("".equals(getBtAddr())) 
			{
				commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				break;
			}
			if(mNlBluetooth.isConnectedA()==false)
			{
				commGUi.cls_show_msg("请先进行蓝牙底座从模式连接操作");
				return;
			}
			int backKey = commGUi.cls_show_msg("回连选择\n0.开启回连\n其他.关闭回连\n");
			if(backKey==ESC)
				return;
			if(mNlBluetooth.pSetBackConnectEnabled(backKey=='0'?true:false)==false)
			{
				commGUi.cls_show_msg1_record(TAG, "dongleConfig",g_keeptime, "line %d:底座设置回连控制失败(false)", Tools.getLineInfo());
				return;
			}
			commGUi.cls_show_msg("蓝牙底座回连控制:%s,任意键继续", backKey=='0'?"打开":"关闭");
			break;
			
		case '3':
			if ("".equals(getBtAddr())) {
				commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				break;
			}
//			if (!mNlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
//			{
//				commGUi.cls_s how_msg1_record(TAG, "dongleConfig",g_keeptime, "line %d:连接蓝牙失败（false）", Tools.getLineInfo());
//				break;
//			}
			long startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<30)
			{
				if(mNlBluetooth.isConnectedA())
					break;
			}
			// add by wangxy 20181228 判断POS与底座的连接状态
			if(!mNlBluetooth.isConnectedA())
			{
				commGUi.cls_show_msg1_record(TAG,"dongleConfig",g_keeptime, "line %d:获取蓝牙连接状态失败(false)", Tools.getLineInfo());
				break;
			}
			StringBuffer sb=new StringBuffer();
			if((ret=mNlBluetooth.btGetTransPortA(sb))!=NDK_OK)
			{
				commGUi.cls_show_msg1_record(TAG, "dongleConfig",g_keeptime, "line %d:获取蓝牙波特率失败(ret=%d)", Tools.getLineInfo(),ret);
			}
			// 获取蓝牙底座各种信息保存下来
			mBtBps=sb.toString();//获取底座此时的波特率
			mBtMac = mNlBluetooth.getConnectedDeviceAddressA();
			mBtName = mNlBluetooth.getConnectedDeviceNameA();
			mBtVersion = mNlBluetooth.pGetBluetoothVersion();
			GlobalVariable.Auth_Control=(mNlBluetooth.pGetDeviceStatus()&0x02)==0x02?1:0;
			LoggerUtil.d("bps:"+mBtBps+"  mbtMac:"+mBtMac+" mbtName:"+mBtName+" mbtVersion:"+mBtVersion);
			//设置透传接收监听
			//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
			//mNlBluetooth.setDataListener(listener);
			commGUi.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,其他POS搜索到该底座视为【测试不通过】,任意键继续测试");
			break;
		
		//数据收发长度设置
		case '4':
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
			
		case '5':
			if ("".equals(getBtAddr())) 
			{
				commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作");
				break;
			}
			commGUi.cls_show_msg("请长按复位并在设置-蓝牙中清除底座的配对信息，完成后点击任意键继续");
			commGUi.cls_printf("正在配对中。。。".getBytes());
			if(bluetoothManager.pair(getBtAddr(), "0")==false)
			{
				commGUi.cls_show_msg1_record(TAG, "dongleConfig", g_keeptime, "line %d:蓝牙配对失败（ret=%s）", Tools.getLineInfo(),ret);
				return;
			}
			SystemClock.sleep(5*1000);
			if (mNlBluetooth.isConnectedA() == false) 
			{
				commGUi.cls_show_msg1_record(TAG, "dongleConfig", g_keeptime, "line %d:连接状态与预期不符（ret=%s）", Tools.getLineInfo(),ret);			
				return;
			}
			commGUi.cls_show_msg("底座蓝牙已连接成功,其他POS应搜索不到该底座,其他POS搜索到该底座视为【测试不通过】,任意键继续测试");
			break;
			
		default:
			break;
		}
	}
	//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
	/*OnDataReceiveListener listener = new OnDataReceiveListener() {
        @Override
        public void onDataReceive(byte[] data) {
        	LoggerUtil.e("接收数据广播:"+data.length);
        	len=len+data.length;
    		System.arraycopy(data, 0,rbuf, i, data.length);  
    		i=i+data.length;
        }
	};*/
     
	
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
		int returnValue =commGUi.cls_show_msg("底座功能\n1.数据路连接、命令路连接\n2.数据路回连、命令路回连\n7.波特率轮询\n");
		int num=returnValue-48;
		switch (returnValue) 
		{
	
		// 数据、命令双通道1
		case '1':
		// 数据、命令双通道2
		case '2':
			cmdDataCh1(num);
			break;

		// 轮询波特率进行数据通讯
		case '7':// 双通道底座不支持设置波特率，双通道的设置波特率是写到flash的，对flash频繁操作会有问题 add by zhengxq 20190102
			allBpsTest();
			break;
			
		case ESC:
			break;
			
		}
	}
	// 轮询波特率进行数据通讯
	private void allBpsTest() 
	{
		long startTime;
		if("".equals(mNlBluetooth.getConnectedDeviceAddressA()))
		{
			commGUi.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		for (int bps:bps_list)
		{
			commGUi.cls_show_msg1(2,"即将将波特率设置为"+bps);
		    if((ret=mNlBluetooth.btSetTransPort(bps+""))!=NDK_OK)
		    {
		    	commGUi.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:设置波特率%d失败(%d)", Tools.getLineInfo(),bps,ret);
		    	return;
		    }
			commGUi.cls_show_msg("当前波特率已设置为:"+bps+",请打开AccessPort设置成对应的波特率,设置完毕并接入串口线后按任意键继续");
			if(dataSendRecv()!=BT_OK)
			{
				mCmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s数据收发测试失败(bps=%s)", Tools.getLineInfo(),TESTITEM,bps);
				continue;
			}
			
			commGUi.cls_show_msg("请手动上下电底座(预期底座上下电后波特率恢复为默认值115200),完成后按任意键继续");
			// 回连操作，燕清那边会主动去回连，给5s的时间等待
			startTime=System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<5)
			{
				if(mNlBluetooth.isConnectedA()==true)
					break;
				SystemClock.sleep(1000);
			}
			
			if(commGUi.ShowMessageBox(("当前为双通道,确认蓝灯是否常亮").getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
			{
				commGUi.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:仅连接数据通道时，指示灯亮灭时间与预期不符", Tools.getLineInfo());
				return;
			}
			//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
			/*//注册透传接收数据广播,重新上下电后需重新注册
			mNlBluetooth.setDataListener(new OnDataReceiveListener() {
	            @Override
	            public void onDataReceive(byte[] data) {
	            	LoggerUtil.e("接收数据广播2");
	            	len=len+data.length;
	        		System.arraycopy(data, 0,rbuf, i, data.length);  
	        		i=i+data.length;
	            }
			});*/
			// 读取波特率值
			StringBuffer bpsValue = new StringBuffer();
			mNlBluetooth.btGetTransPortA(bpsValue);
			commGUi.cls_show_msg("当前波特率已恢复为默认值:"+bpsValue+",请打开AccessPort设置成对应的波特率,设置完毕并接入串口线后按任意键继续");
			if(dataSendRecv()!=BT_OK)
			{
				mCmdGui.cls_show_msg1_record(TAG, "allBpsTest", g_keeptime, "line %d:%s数据收发测试失败(bps=%s)", Tools.getLineInfo(),TESTITEM,bpsValue);
				continue;
			}
		}
		commGUi.cls_show_msg("轮询设置波特率进行数据通道通讯测试通过");
	}

	//底座设置波特率后，重新上下电后，波特率值是否保存且生效 addby wangxy20181213
	private void DonglePowerPre() {
		StringBuffer strBps = new StringBuffer();
		int cnt,succ = 0,bak;
		cnt=bak=commGUi.JDK_ReadData(10, 200, "请输入压力次数");
		// 先恢复为广播状态
		commGUi.cls_show_msg("先将蓝牙底座复位并数字信号发生器间隔输出高-低电平给底座,高电平1min,低电平15s,完毕按任意键继续");
		//建立双通道
		if (!mNlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
		{
			commGUi.cls_show_msg1_record(TAG, "dongleConfig",g_keeptime, "line %d:连接蓝牙失败（false）", Tools.getLineInfo());
			return;
		}
		long startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<30)
		{
			if(mNlBluetooth.isConnectedA())
				break;
		}
		// add by wangxy 20181228 判断POS与底座的连接状态
		if(!mNlBluetooth.isConnectedA())
		{
			commGUi.cls_show_msg1_record(TAG,"dongleConfig",g_keeptime, "line %d:获取蓝牙连接状态失败(false)", Tools.getLineInfo());
			return;
		}
		
		// 将当前的蓝牙mac、名字、波特率、版本记录在数据库中，后续底座断电回连成功后进行比较,
		String btMac = mNlBluetooth.getConnectedDeviceAddressA();
		String btName=mNlBluetooth.getConnectedDeviceNameA();
		String btVer = mNlBluetooth.pGetBluetoothVersion();
		editor.putString("btMac", btMac);
		editor.commit();
		editor.putString("btName", btName);
		editor.commit();
		editor.putString("btBps", "115200");//掉电后bps恢复为默认值115200
		editor.commit();
		editor.putString("btVersion", btVer);
		editor.commit();
		int timeout = 200;
		
		while(true)
		{
			cnt--;
			commGUi.cls_show_msg1(1, "底座上下电回连正在进行第%d次测试,成功%d次", bak-cnt,succ);
			// 等待底座的断开
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<60)
			{
				if(mNlBluetooth.isConnectedA()==false)
					break;
			}
			commGUi.cls_printf("底座已断开,等待回连...".getBytes());
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<40)
			{
				if(mNlBluetooth.isConnectedA()==true)
					break;
			}
			if(mNlBluetooth.isConnectedA()==false)
			{
				commGUi.cls_show_msg1_record(TAG, "DonglePowerPre",g_keeptime, "line %d:第%d次:命令通道、数据通道回连失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 燕清判断的回连成功是数据路回连成功就算回连上，命令通道需要再经过1-2s的延时 modify by zhengxq
			SystemClock.sleep(2000);
			if(cnt==0)
				break;
			//底座断电回连成功后，与之前的蓝牙底座数据进行比较应一致
			//case1:命令通道通讯 ，获取蓝牙版本
			btVer = mNlBluetooth.pGetBluetoothVersion();
			String version = sharedPreferences.getString("btVersion", "");
			LoggerUtil.d("version:"+version);
			if(version.equals(btVer)==false)
			{
				commGUi.cls_show_msg1_record(TAG,"DonglePowerPre", g_keeptime,"line %d:上下电获取蓝牙版本不一致(curVer=%s,version=%s)", Tools.getLineInfo(),btVer,version);
				continue;
			}
			// case2:获取蓝牙底座mac地址
			btMac = mNlBluetooth.getConnectedDeviceAddressA();
			String mac = sharedPreferences.getString("btMac", "");
			if(mac.equals(btMac)==false)
			{
				commGUi.cls_show_msg1_record(TAG,"DonglePowerPre", g_keeptime,"line %d:上下电获取蓝牙mac不一致(curMac=%s,btMac=%s)", Tools.getLineInfo(),btMac,mac);
				continue;
			}
			// case3:获取蓝牙底座的名字
			btName =mNlBluetooth.getConnectedDeviceNameA();
			String name = sharedPreferences.getString("btName", "");
			if(name.equals(btName)==false)
			{
				commGUi.cls_show_msg1_record(TAG,"DonglePowerPre", g_keeptime,"line %d:上下电获取蓝牙名字不一致(curName=%s,btName=%s)",Tools.getLineInfo(),btName,name);
				continue;
			}
			// case4:获取蓝牙底座的波特率,应该为115200，不管之前设置为多少，重新上下电后均为默认值115200
			mNlBluetooth.btGetTransPortA(strBps);
			String bps = sharedPreferences.getString("btBps", "");
			if(bps.equals(strBps.toString())==false)
			{
				commGUi.cls_show_msg1_record(TAG,"DonglePowerPre", g_keeptime,"line %d:上下电获取蓝牙底座波特率不一致，掉电后默认波特率为115200(curBps=%s,btBps=%s)",Tools.getLineInfo(),strBps,bps);
				continue;
			}
			succ++;
		}
		mCmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "底座上下电回连共测试%d次,成功%d次", bak,succ);
		
		//上下电压测后置确认波特率不受影响
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<timeout)
		{
			if(mNlBluetooth.isConnectedA()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道以及数据通道回连成功");
				break;
			}
		}
		if(mNlBluetooth.isConnectedA()==false)
		{
			commGUi.cls_show_msg1_record(TAG, "DonglePowerPre",g_keeptime, "line %d:第%d次:命令通道、数据通道回连失败", Tools.getLineInfo(),bak-cnt);
			return;
		}
		// 添加2s的延时
		SystemClock.sleep(2000);
		mNlBluetooth.btGetTransPortA(strBps);
		if(strBps.equals("115200")==false)
		{
			commGUi.cls_only_write_msg(TAG,"DonglePowerPre","line %d:上下电获取蓝牙底座波特率应为默认值115200(curBps=%s)",Tools.getLineInfo(),strBps);
		}
		commGUi.cls_show_msg("当前波特率为:%s（由于底座上下电后，波特率恢复为默认值115200）,请打开AccessPort设置成对应的波特率",strBps);
		if(dataSendRecv()!=BT_OK)
		{
			mCmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "line %d:%s数据收发测试失败(bps=%s)", Tools.getLineInfo(),TESTITEM,strBps);
			return;
		}
		mCmdGui.cls_show_msg1_record(TAG, "DonglePowerPre", g_keeptime, "底座上下电回连测试通过");
	}

	private void PosRebootPre() {
		String mac=null;
		
		if((mac=sharedPreferences.getString("btAddr","")).equals(""))
		{
			mDataGui.cls_show_msg1_record(TAG, "reboot", g_keeptime, "line %d:数据库获取蓝牙mac地址失败(%s)", Tools.getLineInfo(),mac);
			return;
		}
		
		// 先恢复为广播状态
		if(mNlBluetooth.isConnectedA()==false)
		{
			commGUi.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		
		//将当前的蓝牙mac、名字、波特率、版本记录在数据库中，后续重启后进行比较
		StringBuffer btBps = new StringBuffer();
		String btMac = mNlBluetooth.getConnectedDeviceAddressA();
		mNlBluetooth.btGetTransPortA(btBps);
		String version=mNlBluetooth.pGetBluetoothVersion();
		String btName=mNlBluetooth.getConnectedDeviceNameA();
		editor.putString("btMac", btMac);
		editor.commit();
		editor.putString("btName", btName);
		editor.commit();
		editor.putString("btBps", btBps.toString());
		editor.commit();
		editor.putString("btVersion", version);
		editor.commit();
		//确认POS重启后进行回连操作
		editor.putBoolean("isReboot", true);
		editor.commit();//提交修改
		commGUi.cls_show_msg("建议将本用例用于夜间压力测试，期间POS将自动间隔一段时间（30-60min）后重启POS回连底座，请确保底座一直处于上电状态且接入RS232线并短接2/3口\n"
				+ "之后请将POS接入充电设备后测试一晚，第二天查看result文件，第一次须手动重启POS，测试结束后请卸载HighPlatTest应用避免后续继续重启测试");
		
	}
	
	// 数据通道的数据收发 add by 20170407
	public int dataSendRecv()
	{
		
		mDataGui.cls_printf("".getBytes());
		byte[] sBuf = new byte[SEND_DATA_LEN];
		Arrays.fill(sBuf, (byte) 0x38);
		rbuf=new byte[DATA_BUFFER];//将recbufer设大一点,避免越界的异常,串口工具偶尔会发换行
		if(mNlBluetooth.sendDataA(sBuf)==false)
		{
			mDataGui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:数据通道发送数据失败", Tools.getLineInfo());
			return BT_WRITE_FAILED;
		}
		/*synchronized (lockObj) {
			try {
				lockObj.wait(30*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		//接收数据
		int time=0;
		i=0;len=0;
		long startTime = System.currentTimeMillis();
		mDataGui.cls_show_msg1(1, "将从底座接收1024字节数据");
		while(time<DATA_TIMEOUT)
		{
			
			SystemClock.sleep(1000);
			time+=1000;
			System.out.println("数据传输中...."+len);
			if(len==SEND_DATA_LEN)
				{
				System.out.println("数据传输完成"+len);
				break;
			}
		}
		if(len!=SEND_DATA_LEN)
		{
			LoggerUtil.e("len="+len);
			LoggerUtil.e("sbuf="+ISOUtils.hexString(sBuf));
			LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
			mDataGui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:数据通道接收数据失败(实际接收到的长度=%d)", Tools.getLineInfo(),len);
			return BT_READ_FAILED;	
		}else{
			// 比较收发数据
			if(Tools.memcmp(sBuf, rbuf, SEND_DATA_LEN)==false)
			{
				LoggerUtil.e("sbuf="+ISOUtils.hexString(sBuf));
				LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
				mDataGui.cls_show_msg1_record(TAG, "dataSendRecv",g_keeptime, "line %d:数据通道收发数据失败(ret = %d)", Tools.getLineInfo(),BT_COMPARE_FAILED);
				return BT_COMPARE_FAILED;
				
			}
		}
		mDataGui.cls_printf("数据收发成功".getBytes());
		return BT_OK;
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
		mCmdGui.cls_printf("".getBytes());
		int ret;
		String btName=mNlBluetooth.getConnectedDeviceNameA();
		if(mBtName.equals(btName)==false)
		{
			mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取蓝牙名字失败(预期=%s,实际=%s)", Tools.getLineInfo(),mBtName,btName);
			return -1;
		}
		String btMac=mNlBluetooth.getConnectedDeviceAddressA();
		if(mBtMac.equals(btMac)==false)
		{
			mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取蓝牙mac地址失败(预期=%s,实际=%s)", Tools.getLineInfo(),mBtMac,btMac);
			return -1;
		}
		if((ret = mNlBluetooth.openBoxA())!=0)
		{
			mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道打开钱箱失败(ret = %d)", Tools.getLineInfo(),ret);
			return -1;
		}
		
		String btVersion = mNlBluetooth.pGetBluetoothVersion();
		if(mBtVersion.equals(btVersion)==false)
		{
			mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取底座版本失败(预期=%s,实际=%s)", Tools.getLineInfo(),mBtVersion,btVersion);
			return -1;
		}
		StringBuffer bpsBuffer = new StringBuffer();
		mNlBluetooth.btGetTransPortA(bpsBuffer);
		if(mBtBps.equals(bpsBuffer.toString())==false)
		{
			mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:命令通道获取底座版本失败(预期=%s,实际=%s)", Tools.getLineInfo(),mBtBps,bpsBuffer.toString());
			return -1;
		}
		// 获取状态应跟认证控制对上
		int status = mNlBluetooth.pGetDeviceStatus();
		if(GlobalVariable.Auth_Control==0)//不开启认证控制
		{
			if((status&0x02)==0x02)
			{
				mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:状态获取错误(status = %d)", Tools.getLineInfo(),status);
				return -1;
			}
		}
		else
		{
			if((status&0x02)!=0x02)
			{
				mCmdGui.cls_show_msg1_record(TAG, "cmdOperate",g_keeptime, "line %d:状态获取获取错误(status = %d)", Tools.getLineInfo(),status);
				return -1;
			}
		}
		return BT_OK;
	}
	
	
	/**
	 * 命令数据通道1：命令通道和数据通道均为连接状态下进行数据的通讯操作 add by zhengxq 20170410
	 */
	public void cmdDataCh1(int num)
	{
		/* private & local definition */
		
		/* process body */
		if(mNlBluetooth.isConnectedA()==false)// 未连接上蓝牙底座则先进行连接操作
		{
			commGUi.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		switch (num) 
		{
		case 1:// 命令通道均连接测试
			commGUi.cls_show_msg("请确保POS和PC已通过RS232线连接，并开启PC端的AccessPort工具设置波特率为%s,完成任意键继续",mBtBps);
			if(cmdOperate()!=BT_OK)
				return;
			if(dataSendRecv()!=BT_OK)
				return;
			commGUi.cls_show_msg("命令通道与数据通道均连接->通讯->断开成功");
			break;
			
		case 2:
			commGUi.cls_show_msg("请确保POS和PC已通过RS232线连接，并开启PC端的AccessPort工具设置波特率为%s,完成任意键继续",mBtBps);
			cmdDataCh4();
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 命令数据通道4：数据和命令通道均为回连状态，进行数据的通讯操作 add by zhengxq 20170410
	 */
	public void cmdDataCh4()
	{
		/* private & local definition */
		long startTime;
		/* process body */
		
		commGUi.cls_show_msg1(2, "命令通道、数据通道即将断开...");
		commGUi.cls_show_msg("请对底座上下电,操作完毕点击任意键");
		commGUi.cls_show_msg1(1, "命令通道、数据通道回连中......");
		// (1)命令通道、数据通道回连操作
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<BACK_TIMEOUT)
		{
			if(mNlBluetooth.isConnectedA()==true)
			{
				commGUi.cls_show_msg1(2, "命令通道、数据通道回连成功");
				break;
			}
		}
		if(mNlBluetooth.isConnectedA()==false)
		{
			commGUi.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:命令通道、数据通道回连失败", Tools.getLineInfo());
			return;
		}
		// 回连之后添加2s的延时
		SystemClock.sleep(2000);
		//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
		//mNlBluetooth.setDataListener(listener);
		if(cmdOperate()!=BT_OK)
			return;
		if(dataSendRecv()!=BT_OK)
			return;
		commGUi.cls_show_msg("命令通道回连->通讯成功，数据通道回连->通讯成功");
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
		int nkeyIn = commGUi.cls_show_msg("底座压力\n0.两路均连接+通讯(通讯压力)\n1.两路均回连+通讯(流程压力)\n2.POS重启回连压力\n3.底座上下电回连压力\n4.数据-命令并发压力\n");
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
			
		case '4':
			CmdDataPre();
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

		/* process body */
		// 压力次数设置
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(commGUi.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		if(mNlBluetooth.isConnectedA()==false)
		{
			mCmdGui.cls_show_msg("请先进行蓝牙底座从模式连接操作");
			return;
		}
		// 此时数据通道、命令通道均为连接状态下，进行数据通讯、命令交互操作
		while(cnt>0)
		{
			if(commGUi.cls_show_msg1(3, "数据通道、命令通道通讯压力中...还剩%d次(已成功%d次),[取消]键退出测试...",cnt,succ)==ESC)
				break;
			
			cnt--;
			if((ret = cmdOperate())!=BT_OK)
			{
				mCmdGui.cls_show_msg1_record(TAG, "connCommPre", g_keeptime, "line %d:第%d次命令通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((ret = dataSendRecv())!=BT_OK)
			{
				mDataGui.cls_show_msg1_record(TAG, "connCommPre", g_keeptime, "line %d:第%d次数据通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
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
		long startTime;
		int succ=0,ret = 0;
		int cnt,bak = 0;

		/* process body */
		// 获取合法性认证状态
		// 压力次数设置
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(commGUi.JDK_ReadData(TIMEOUT_INPUT, PACKLEN));
		bak = cnt = packet.getLifecycle();
		commGUi.cls_show_msg("底座接入串口线并短接23脚,先将蓝牙底座复位并数字信号发生器间隔输出高-低电平给底座,高电平1min,低电平10s,完毕按任意键继续");
		//建立双通道
		if (!mNlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())) 
		{
			commGUi.cls_show_msg1_record(TAG, "backCommPre",g_keeptime, "line %d:连接蓝牙失败(false)", Tools.getLineInfo());
			return;
		}
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<30)
		{
			if(mNlBluetooth.isConnectedA())
				break;
		}
		// add by wangxy 20181228 判断POS与底座的连接状态
		if(!mNlBluetooth.isConnectedA())
		{
			commGUi.cls_show_msg1_record(TAG,"backCommPre",g_keeptime, "line %d:获取蓝牙连接状态失败(false)", Tools.getLineInfo());
			return;
		}
		// 进行回连+通讯压力测试
		while(cnt>0)
		{
			if(commGUi.cls_show_msg1(1, "数据通道、命令通道回连+通讯压力中...还剩%d次(已成功%d次),点击退出键退出...",cnt,succ)==ESC)
				break;
			cnt--;
			// 命令通道断开 回连操作
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<60)
			{
				SystemClock.sleep(1000);
				if(mNlBluetooth.isConnectedA()==false)
					break;
			}
			commGUi.cls_show_msg1(1, "命令通道、数据通道已断开...");
			startTime = System.currentTimeMillis();
			while(Tools.getStopTime(startTime)<40)
			{
				if(mNlBluetooth.isConnectedA()==true)
				{
					commGUi.cls_show_msg1(2, "命令通道、数据通道回连成功");
					break;
				}
			}
			if(mNlBluetooth.isConnectedA()==false)
			{
				commGUi.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:第%d次命令通道、数据通道回连失败", Tools.getLineInfo(),bak-cnt);
				// 将监听的给断开
				continue;
			}
			//20190124开发燕清新输出的1.0.8arr包可以无需多次设置监听
			//mNlBluetooth.setDataListener(listener);
			if((ret = cmdOperate())!=BT_OK)
			{
				mCmdGui.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:第%d次命令通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if(dataSendRecv()!=BT_OK)
			{
				mDataGui.cls_show_msg1_record(TAG, "backCommPre", g_keeptime, "line %d:第%d次数据通道通讯失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		commGUi.cls_show_msg1_record(TAG, "backCommPre",g_keeptime, "数据通道、命令通道回连+通讯测试完成，已执行次数为%d，成功%d次", bak-cnt,succ);
	}
	
	// 双通道并发通讯压力测试add by zhengxq20190102
	private void CmdDataPre() 
	{
		// 设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(commGUi.JDK_ReadData(TIMEOUT_INPUT, getCycleValue()));
		int cnt= packet.getLifecycle();// 交叉次数获取
		if ("".equals(mNlBluetooth.getConnectedDeviceAddressA())) 
		{
			commGUi.cls_show_msg1(2, "请先进行蓝牙底座选择-配置操作,使之处于已连接状态");
			return;
		}
		StringBuffer sb = new StringBuffer();
		if ((ret = mNlBluetooth.btGetTransPortA(sb)) != NDK_OK) 
		{
			mCmdGui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime,"line %d:获取波特率失败(ret=%d)", Tools.getLineInfo(), ret);
			return;
		}
		LoggerUtil.e("nlBluetooth.btGetTransPortA()=" + sb);
		if (mBtBps.equals(sb.toString())==false) 
		{
			mCmdGui.cls_show_msg1_record(TAG, "CmdDataPre", g_keeptime,"line %d:当前波特率与预期的不一致(预期=%d,实际%s)", Tools.getLineInfo(),mBtBps, sb.toString());
			return;
		}
		mDataGui.cls_show_msg("请确保POS和PC已通过RS232线连接,并开启PC端的AccessPort工具设置波特率为%s,完成任意键继续",mBtBps);
		new Thread(new CmdRun(cnt)).start();
		new Thread(new DataRun(cnt)).start();
	}

	class CmdRun implements Runnable
	{	
		int mTotalCnt;
		public CmdRun(int cnt)
		{
			mTotalCnt = cnt;
		}
		
		@Override
		public void run() 
		{
			int bak,cnt,succ=0;
			bak=cnt=mTotalCnt;
			while (cnt > 0) {
				mCmdGui.cls_show_msg1(1, "双通道通讯并发压力,命令通道正在进行第%d次测试(已成功%d次)",bak - cnt, succ);
				cnt--;
				cmdOperate();
				succ++;
			}
			mCmdGui.cls_show_msg1_record(TAG, "CmdRun", g_time_0,"命令通道并发压力测试完成，已执行次数为%d,成功为%d次", bak - cnt,succ);
		}
		
	};
	
	class DataRun implements Runnable
	{
		int mTotalCnt;
		public DataRun(int cnt)
		{
			mTotalCnt = cnt;
		}
		
		@Override
		public void run() {
			int cnt = 0, bak = 0, succ = 0;
			byte[] sbuf = new byte[DATA_BUFFER];// 开发潘浩说数据透传最大为4k
			bak = cnt = mTotalCnt;
			int time=0;
			long startTime;
			for (int j = 0; j < sbuf.length; j++)
				sbuf[j] = (byte) (Math.random() * 256);
			while (cnt > 0) {
				mDataGui.cls_show_msg1(1,"双通道通讯并发压力,数据通道通道正在进行第%d次测试(已成功%d次)", bak- cnt, succ);
				cnt--;
				mDataGui.cls_printf(("将发送" + DATA_BUFFER+ "字节数据给底座").getBytes());
				if (!mNlBluetooth.sendDataA(sbuf)) 
				{
					mDataGui.cls_show_msg1_record(TAG, "CmdDataPre",g_keeptime,"line %d:第%d次，数据透传失败（false,%s）",Tools.getLineInfo(), bak - cnt,ISOUtils.hexString(sbuf));
					continue;
				}
				//接收数据
			    time=0;
				i=0;len=0;
				rbuf=new byte[DATA_BUFFER];
				startTime = System.currentTimeMillis();
				mDataGui.cls_show_msg1(1, "将从底座接收"+DATA_BUFFER+"字节数据");
				while(time<DATA_TIMEOUT)
				{
					time = (int) Tools.getStopTime(startTime);
					SystemClock.sleep(1000);
					if(len==DATA_BUFFER)
						break;
				}
				if(len!=DATA_BUFFER)
				{
					LoggerUtil.e("len="+len);
					LoggerUtil.e("sbuf="+ISOUtils.hexString(sbuf));
					LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
					mDataGui.cls_show_msg1_record(TAG, "CmdDataPre",g_keeptime,"line %d:第%d次:未接收到数据(false)",Tools.getLineInfo(), bak - cnt);
					continue;
				}else{
					// 比较收发数据
					if(Tools.memcmp(sbuf, rbuf, DATA_BUFFER)==false)
					{
						LoggerUtil.e("sbuf="+ISOUtils.hexString(sbuf));
						LoggerUtil.e("rbuf="+ISOUtils.hexString(rbuf));
						mDataGui.cls_show_msg1_record(TAG,"CmdDataPre",g_keeptime,
								"line %d:第%d次，数据通道收发数据失败(ret = %d,sbuf=%s,rbuf=%s)",
								Tools.getLineInfo(), bak - cnt,BT_COMPARE_FAILED,ISOUtils.hexString(sbuf),
								ISOUtils.hexString(rbuf));
						continue;
					}
				}
				/*synchronized (lockObj) {
					try {
						lockObj.wait(30*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}*/
				succ++;
			}
			mDataGui.cls_show_msg1_record(TAG, "CmdDataPre", g_time_0,"数据透传通道并发压力测试完成，已执行次数为%d,成功为%d次", bak - cnt,succ);
		}
	};

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
