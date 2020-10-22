package com.example.highplattest.systest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.netutils.WifiProbe;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quectel.jni.QuecJNI;
/************************************************************************
 * module 			: SysTest交叉模块
 * file name 		: SysTest13.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171206
 * directory 		: 
 * description 		: Wifi开关与Wifi探针的交叉
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20171206	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest13 extends DefaultFragment{

	private final String TAG = "wifi开关与wifi探针";
	private final String TESTITEM = "wifi探针压力";
	private Gui gui = new Gui(myactivity, handler);
	private WifiManager mWifiManager;
	private android.newland.net.wifi.WifiManager mWifiProbe;
	private WifiP2pManager mWifiP2PManager;
	private Channel mChannel;
	/*标志位*/
	private boolean isTimeStop = false;
	private boolean isCompatable = false;// 是否为兼容性测试
	/*handler操作的MSG*/
	private final int MSG_DISCOVERY = 1;
	private final int MSG_STOP_DISCOVERY = 2;
	private FileSystem fileSystem = new FileSystem();
	private String filePath;
	private int tapTime=1;// 读数据的间隔时间，默认为1s
	private WakeLock mWakeLock;
	private ReceiverTracker.WifiStateBroad mWifiStateBroad = new ReceiverTracker().new WifiStateBroad();;
	
	Handler timeHandler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) {
			case MSG_DISCOVERY:
				discovery();
				break;
				
			case MSG_STOP_DISCOVERY:
				stopDiscovery();
				break;

			default:
				break;
			}
		};
	};
	
	
//	public void onResume() 
//	{
//		super.onResume();
//		if(GlobalVariable.gCustomerID==CUSTOMER_ID.ChinaUms)
//			registWifiState();
//	};
	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		if(GlobalVariable.gCustomerID==CUSTOMER_ID.ChinaUms)
//			unregistWifiState();
//	}
//	
	/**
	 * 注册wifi变化，用于改变探针节点的开关状态
	 */
	private void registWifiState()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		myactivity.registerReceiver(mWifiStateBroad, intentFilter);
	}
	
	/**
	 * 注销wifi状态变化的广播
	 */
	private void unregistWifiState()
	{
		if(mWifiStateBroad!=null)
		{
			if(GlobalVariable.gCustomerID==CUSTOMER_ID.ChinaUms)
				unregistWifiState();
		}

	}

	
	public void systest13()
	{
		if(GlobalVariable.gAutoFlag==AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 根据刘坤坤反馈新增的机型均有导入wifi探针功能 modify 20180329
		String result = BaseFragment.getProperty("sys.epay.wifiprobe","-10086");
		if(result.equalsIgnoreCase("true")==false)
		{
			gui.cls_show_msg("本固件不支持wifi探针功能,任意键退出");
			intentSys();
			return;
		}
		registWifiState();

        mWifiManager = (WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE);
        mWifiProbe = new android.newland.net.wifi.WifiManager(myactivity);// wifi探针的控制类
		mWifiP2PManager = (WifiP2pManager) myactivity.getSystemService(Context.WIFI_P2P_SERVICE);
		PowerManager powerManager=(PowerManager) myactivity.getSystemService(Context.POWER_SERVICE);
        if(mChannel==null)
        {
            // 打开p2p模式
            mChannel = mWifiP2PManager.initialize(myactivity,myactivity.getMainLooper(), null);
         }
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"bright");
		if(mWakeLock!=null)
			mWakeLock.acquire();
		while(true)
		{
			int returnValue=gui.cls_show_msg("wifi开关与wifi探针交叉\n0.配置\n1.开wifi和探针\n2.关wifi及探针\n3.循环读探针数据\n4.健壮性测试\n5.兼容性测试");
			switch (returnValue) 
			{
			
			case '0':
				probeConfig();
				break;
				
			case '1':
				wifiProbeOpen();
				break;
				
			case '2':
				wifiProbeClose();
				break;
				
			case '3':
				isCompatable = false;
				readJNI();
				break;
				
			case '4':// 健壮性测试
				isCompatable = true;
				probe_Strong();
				break;
				
			case '5':// 探针对手机的兼容性测试
				isCompatable = true;
				probe_Compatibility();
				break;
				
			case ESC:
				if(mWakeLock!=null)
					mWakeLock.release();
				intentSys();
				unregistWifiState();
				return;
				
			default:
				break;
			}
		}
	}

	
	
	// wifi探针读间隔时间设置
	private void probeConfig()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("配置\n0.间隔时间\n1.总开关打开\n");
			switch (nkeyIn) {
			case '0':
				gui.cls_printf("请输入读数据间隔时间，默认时间为1s,设置0为持续读数据，取消为默认时间".getBytes());
				tapTime = gui.JDK_ReadData(30,1);
				break;
				
			case '1':
				mWifiProbe.setGlobalMonitor(0);
				gui.cls_show_msg1(2, "wifi探针总开关已开启");
				break;

			case ESC:
				return;
			}
		}

	}
	
    // wifi探针的开启
    private int wifiProbeOpen()
    {
    	gui.cls_printf("正在开启wifi探针，请耐心等待...".getBytes());
        if(mWifiProbe.setMonitor(0)==-5)
        {
        	gui.cls_show_msg1(5, "探针总开关已关闭，请先开启总开关");
        	return -1;
        }
        mWifiManager.setWifiEnabled(false);// 用锁去判断是否完毕
        synchronized(ReceiverTracker.lockListener)
        {
        	try {
				ReceiverTracker.lockListener.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        mWifiManager.setWifiEnabled(true);
        synchronized(ReceiverTracker.lockListener)
        {
        	try {
				ReceiverTracker.lockListener.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
         gui.cls_show_msg1(g_keeptime, "wifi探针开启成功");
         return 0;
    }
    
    // wifi探针的关闭
    private void wifiProbeClose()
    {
    	gui.cls_printf("正在关闭wifi探针，请耐心等待...".getBytes());
    	mWifiManager.setWifiEnabled(false);// 关闭wifi
        synchronized(ReceiverTracker.lockListener)
        {
        	try {
				ReceiverTracker.lockListener.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        mWifiProbe.setMonitor(1);
        if(mChannel!=null){
			stopDiscovery();// 关闭P2P搜索
		}
		mWifiManager.setWifiEnabled(true);
        synchronized(ReceiverTracker.lockListener)
        {
        	try {
				ReceiverTracker.lockListener.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		gui.cls_show_msg1(g_keeptime, "wifi探针关闭成功");
    }
    
    private long readStartTime;
    private String standTime;
    
    // 读取探针数据 多个线程读数据,这个操作之前要先打开探针
    private void readJNI()
    {
    	// 持续读数据
    	gui.cls_printf("wifi探针数据采集中,请耐心等待,若长时间未采集到数据,请确认wifi驱动是否已奔溃...".getBytes());
    	if(mWifiProbe.getMonitor()==0)
    	{
    		byte[] readWifi = new byte[2048];
    		// 新建文件
    		filePath = Environment.getExternalStorageDirectory()+"/probePre.txt";
    		fileSystem.JDK_FsOpen(filePath, "w");
    		new Thread(new ThreadShow()).start();
    		
    		// X5这个port是29
    		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
    			QuecJNI.setNetPort(29);
    		else
    			QuecJNI.setNetPort(30);
    		LoggerUtil.e(TAG+":setNetPort===29===");
        	QuecJNI.openNode();// 打开节点
        	readStartTime = System.currentTimeMillis();
    		while(true)
    		{
//            	StringBuffer strBuffer = new StringBuffer();
                QuecJNI.transferSwitch(1);// 打开接收
                QuecJNI.wifiProbe(readWifi);
                String strProbe = new String(readWifi);
                String[] data = strProbe.split(" ");
                QuecJNI.transferSwitch(2);// 关闭接收
                standTime = Tools.StandTime(readStartTime);
                // 间隔10s读一次数据
                // 将读到的数据个数以及时间点记录到文件中
                StringBuffer strBuffer = new StringBuffer(Tools.getSysNowTime()+":本次读到"+data.length+"个数据\n");
                fileSystem.JDK_FsWrite(filePath, strBuffer.toString().getBytes(), strBuffer.toString().getBytes().length, 2);
                if(gui.cls_show_msg1(10,"本次读到的数据:%d个数据\n已读%s时间,[取消键]退出测试",data.length,standTime)==ESC)
                {
                	isTimeStop = true;// 停止定时器的操作
                	QuecJNI.closeNode();
                	break;
                }
                	
    		}
    		 gui.cls_show_msg("测试已持续%s,任意键退出", Tools.StandTime(readStartTime));
    		 QuecJNI.closeNode();
    	}
    	else
    		gui.cls_show_msg1(g_keeptime, "wifi探针已关闭,无法读取数据!");

    }
    
    /**
     * wifi探针的健壮性测试
     */
    private void probe_Strong()
    {
    	while(true)
    	{
    		int nkeyIn = gui.cls_show_msg("健壮性\n0.wifi探针分开关压力\n1.wifi开关压力\n2.wifi探针总开关压力");
    		switch (nkeyIn) {
    		
    		case '0':
    			wifi_Probe_Switch();
    			break;
    			
			case '1':
				wifi_Switch();
				break;
				
			case '2':
				wifi_Global_Switch();
				break;

			case ESC:
				return;
				
			default:
				break;
			}
    	}
    	
    }
    
    /**
     * 在探针读数据的时候进行wifi探针开关应该只能在wifi探针开启的情况下读数据
     */
    private void wifi_Probe_Switch()
    {
    	gui.cls_show_msg("本项测试wifi探针读数据时开关wifi探针，开探针时应读到数据，关探针时无法读取数据，任意键继续测试");
    	if(wifiProbeOpen()!=0)// 开启wifi探针的操作
    		return;
    	new Thread(new ThreadShow()).start();// 开启一个线程进行循环P2P的discory
    	new Thread(mReadThread).start();// 开启一个线程进行探针数据的循环读取
    	new Thread(new probeSwithThread()).start();// 进行wifi总探针的开关操作
    }
    
    /**
     * 在探针读数据的时候进行wifi开关应该仍可以读数据
     */
    private void wifi_Switch()
    {
    	gui.cls_show_msg("本项测试wifi探针在wifi开关压力下仍可正常读数据，测试过程请多次进行手动wifi开关，开关后探针仍可正常读数据视为测试通过，任意键继续测试");
    	if(wifiProbeOpen()!=0)// 开启wifi探针的操作
    		return;
    	new Thread(new ThreadShow()).start();// 开启一个线程进行循环P2P的discory
    	new Thread(new ReadThread()).start();// 开启一个线程进行探针数据的循环读取
    }
    
    ReadThread mReadThread = new ReadThread();
    /**
     * wifi探针总开关的时候不可读取到数据
     */
    private void wifi_Global_Switch()
    {
    	gui.cls_show_msg("本项wifi探针读数据时关闭总开关，分开关应为关闭，无法读取数据，任意键继续测试");
    	if(wifiProbeOpen()!=0)// 开启wifi探针的操作
    		return;
    	new Thread(new ThreadShow()).start();// 开启一个线程进行循环P2P的discory
    	new Thread(mReadThread).start();// 开启一个线程进行探针数据的循环读取
    	new Thread(new GlobalSwithThread()).start();// 进行wifi总探针的开关操作
    }
    
    // 探针兼容性测试
    private void probe_Compatibility()
    {
    	// 第一步：读取要采集的特定手机的MAC地址
    	List<String> wifiMacList = new ArrayList<String>();
    	// 新建文件
		filePath = Environment.getExternalStorageDirectory()+"/wifi_compatibility.txt";
		File file = new File(filePath);
		if(file.exists()==false)
			gui.cls_show_msg("请在SD卡根目录下放置wifiprobe.json,完成任意键继续");
    	//从json文件中获取所有wifiPare对象
		String jsonString = ReadFile(GlobalVariable.sdPath+"wifiprobe.json");// 获得json配置文件的内容
		JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
		JsonArray macList = jsonObject.get("MacList").getAsJsonArray();
		for(JsonElement element : macList)
		{  
			 JsonObject macObject = element.getAsJsonObject();  
			 wifiMacList.add(macObject.get("MAC").getAsString());
        }
		wifiProbeOpen();// 开启wifi探针的操作
		// 读数据测试
    	if(mWifiProbe.getMonitor()==0)
    	{
    		fileSystem.JDK_FsOpen(filePath, "w");
    		// 间隔30s discovery
    		new Thread(new ThreadShow()).start();
    		// X5这个port是29
    		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
    			QuecJNI.setNetPort(29);
    		else
    			QuecJNI.setNetPort(30);
        	QuecJNI.openNode();// 打开节点
        	readStartTime = System.currentTimeMillis();
    		while(true)
    		{
    			int iRet = -1;
    			List<WifiProbe> wifiProbeList= new ArrayList<WifiProbe>();
            	byte[] readWifi = new byte[2048];
            	if((iRet = QuecJNI.transferSwitch(1))!=0)// 打开开关
            	{
            		gui.cls_show_msg1(1, "line %d:打开探针开关失败，兼容性测试退出(%d)", Tools.getLineInfo(),iRet);
            		return;
            	}
                
                QuecJNI.wifiProbe(readWifi);
                String strProbe = new String(readWifi);
                if((iRet = QuecJNI.transferSwitch(2))!=0)// 关闭接收
                {
                	gui.cls_show_msg1(1, "line %d:关闭探针开关失败，兼容性测试退出(%d)", Tools.getLineInfo(),iRet);
                	return;
                }
                String[] data = strProbe.split(" ");// 按照空格划分
                for(String macR:data)
                {
                	String[] temp = macR.split(",");
                	if(temp.length==2)// 无mac和rssi完整的数据剔除
                		wifiProbeList.add(new WifiProbe(temp[0],temp[1]));
                }
                // 比较数据
                for(int i =0;i<wifiMacList.size();i++)
                {
                	for (WifiProbe wifiProbe:wifiProbeList) {
						if(wifiProbe.getMacAddr().equalsIgnoreCase(wifiMacList.get(i)))
						{
							StringBuffer macRecorde = new StringBuffer();
							macRecorde.append("MAC:"+wifiProbe.getMacAddr()+"    ");
							macRecorde.append(Tools.getSysNowTime()+"    ");
							macRecorde.append((int)Tools.getStopTime(readStartTime)+"    ");
							macRecorde.append("RSSI:"+wifiProbe.getRssi()+"\n");
							fileSystem.JDK_FsWrite(filePath, macRecorde.toString().getBytes(), macRecorde.toString().getBytes().length, 2);
							gui.cls_printf(("已搜索到"+wifiProbe.getMacAddr()+"地址").getBytes());// 搜索该地址就将其移除到就开始下一次比较
							wifiMacList.remove(i);
							i--;
							break;
						}
					}
                }
                if(wifiMacList.size()==0)// 全部搜索到了
                {
                	gui.cls_show_msg("wifiprobe.json文件的手机已全部搜索到,任意键退出兼容性测试");
                	break;
                }
                if(tapTime!=0)
                {
                	if(gui.cls_show_msg1(tapTime, "本次读到%d个数据,[取消]键退出测试", wifiProbeList.size())==ESC)
                		return;
                }
    		}
    		 QuecJNI.closeNode();// 关闭节点
    	}
    	else
    		gui.cls_show_msg1(g_keeptime, "wifi探针已关闭,无法读取数据!");
    	// 关闭wifi探针
    	wifiProbeClose();
    }
    
	public void discovery() {
		mWifiP2PManager.discoverPeers(mChannel,new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {
						if(isCompatable==false)
						{
							StringBuffer strBuffer = new StringBuffer(Tools.getSysNowTime()+":已开启discovery\n");
							fileSystem.JDK_FsWrite(filePath, strBuffer.toString().getBytes(), strBuffer.toString().getBytes().length, 2);
							Log.e(TESTITEM, "discovery onSuccess !");
						}
					}

					@Override
					public void onFailure(int reasonCode) {
						Log.e(TESTITEM, "discovery onFailure !");
					}
				});
	}
	
	public void stopDiscovery() {
		mWifiP2PManager.stopPeerDiscovery(mChannel,new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				if(isCompatable==false)
				{
					Log.e(TESTITEM, "stopDiscovery onSuccess !");
					StringBuffer strBuffer1 = new StringBuffer(Tools.getSysNowTime()+":已停止discovery\n");
					fileSystem.JDK_FsWrite(filePath, strBuffer1.toString().getBytes(), strBuffer1.toString().getBytes().length, 2);
				}

			}

			@Override
			public void onFailure(int reasonCode) {
				Log.e(TESTITEM, "ostopDiscovery onFailure !");
			}
		});
	}
	
	/**
	 *进行P2P模式切换的线程
	 * @author zhengxq
	 * 2017年12月27日 上午11:33:47
	 */
	class ThreadShow implements Runnable
	{
		int timeCount = 0;
		@Override
		public void run() {
			while(isTimeStop==false||mReadThread.isReadStop==false){
				try {
					Message msg = new Message();
					if(isCompatable==true)
						msg.what = MSG_DISCOVERY;//开启P2P
					else
					{
						if(timeCount%2==0)
							msg.what = MSG_DISCOVERY;//开启P2P
						else
							msg.what = MSG_STOP_DISCOVERY;// 停止P2P
					}
					timeHandler.sendMessage(msg);
					Thread.sleep(30*1000);
					timeCount++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 读探针数据的线程
	 * @author zhengxq
	 * 2017年12月27日 上午11:33:36
	 */
	class ReadThread implements Runnable
	{
		boolean isReadStop = false;
		
		@Override
		public void run() {
			int readCount = 0;
			isReadStop = false;
			while(true)
			{
				if(mWifiProbe.getMonitor()==0&&GlobalVariable.isWifiNode==true)// 探针是开启就一直读数据
				{
		        	byte[] readWifi = new byte[2048];
		        	// X5这个port是29
		    		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
		    			QuecJNI.setNetPort(29);
		    		else
		    			QuecJNI.setNetPort(30);
		    		QuecJNI.openNode();
		            QuecJNI.transferSwitch(1);// 打开接收
		            QuecJNI.wifiProbe(readWifi);
		            String strProbe = new String(readWifi);
		            String[] data = strProbe.split(" ");
		            QuecJNI.transferSwitch(2);// 关闭接收
		            readCount++;
		            if(gui.cls_show_msg1(10, "第%d次读到%d个数据,测试过程中可去设置进行开关压力,ESC键退出测试", readCount,data.length)==ESC)
		            {
		            	isReadStop = true;
		            	QuecJNI.closeNode();
		            	break;
		            }
		            QuecJNI.closeNode();
				}
//				else if(mWifiProbe.getMonitor()==1)// 探针已关闭
//	            {
//					isReadStop = true;
//	            	break;
//	            }
			}
		}
		
		public boolean isReadStop()
		{
			return isReadStop;
		}
		
	}
	
	/**
	 * 进行探针总开关的线程
	 * @author zhengxq
	 * 2017年12月27日 上午11:34:38
	 */
	class GlobalSwithThread implements Runnable
	{
		int switchCount = 0;
		@Override
		public void run() {
			while(true)// 读退出的话总开关也退出
			{
				if(mReadThread.isReadStop==true)
				{
					mWifiProbe.setGlobalMonitor(0);//开启探针总开关
					break;
				}
				if(switchCount%2==0)
				{
					mWifiProbe.setGlobalMonitor(0);//开启探针总开关
					mWifiProbe.setMonitor(0);
					gui.cls_printf("探针总开关已开启".getBytes());
				}
				else
				{
					mWifiProbe.setGlobalMonitor(1);// 关闭探针总开关，总开关关闭会把分开关也关闭了
					gui.cls_printf("探针总开关已关闭".getBytes());
				}
				try {
					Thread.sleep(20*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				switchCount++;
			}
		}
		
	}
	
	
	/**
	 * 进行wifi探针开关的线程
	 * @author zhengxq
	 * 2018年1月3日
	 */
	class probeSwithThread implements Runnable
	{
		int switchCount = 0;
		@Override
		public void run() {
			while(true)// 读退出的话总开关也退出
			{
				if(mReadThread.isReadStop==true)
				{
					mWifiProbe.setMonitor(0);//开启wifi探针分开关
					break;
				}
				if(switchCount%2==0)
				{
					mWifiProbe.setMonitor(0);// 开启wifi探针分开关
					gui.cls_printf("wifi探针分开关已开启".getBytes());
				}
				else
				{
					mWifiProbe.setMonitor(1);// 关闭探针分开关
					gui.cls_printf("wifi探针分开关已关闭".getBytes());
				}
				try {
					Thread.sleep(20*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				switchCount++;
			}
		}
		
	}
}
