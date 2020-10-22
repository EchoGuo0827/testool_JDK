package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest32.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150423
 * directory 		: 
 * description 		: WIFI/BT交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest32 extends DefaultFragment
{
	private final String TAG = SysTest32.class.getSimpleName();
	private final String TESTITEM = "WLAN/BT";
	private WifiPara wifiPara = new WifiPara();
	private final int BUFSIZE_BT = BT_BUF_SIZE;
	private Gui gui = null;
	
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	Config config;
	
	public void systest32() throws Exception
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		//初始化layer对象
		initLayer();
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			//bt配置
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr());
			//wifi配置
			config.confConnWlan(wifiPara);
			//交叉测试
			try {
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("WLAN/BT\n0.WIFI配置\n1.BT配置\n2.交叉测试");
			switch (returnValue) 
			{
			
			case '0':
				// 扫描Ap的操作
				switch (config.confConnWlan(wifiPara)) 
				{
				case NDK_OK:
					break;
					
				case NDK_ERR:
					break;
				
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				//蓝牙配置
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				break;
			
			case ESC:
				intentSys();
				return;
			}
		}
		
	}
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0,succ = 0,ret = -1,bak = 0;
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		PacketBean sendPacket = new PacketBean();
		
		int send_len = 0,rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufwifi = new byte[PACKMAXLEN];
		SocketUtil socketUtil_wifi = new SocketUtil( wifiPara.getServerIp(), wifiPara.getServerPort());
		
		/*process body*/
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,wifiPara.getType());
		bak = cnt = sendPacket.getLifecycle();
		
		while(cnt>0)
		{
			
			if(gui.cls_show_msg1(1, "请打开BluetoothServer工具，%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			// 传输层建立
			if((ret = layerBase.netUp(wifiPara,wifiPara.getType())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetUp失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if ((ret = layerBase.transUp(socketUtil_wifi, wifiPara.getSock_t())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:transUp失败(ret = %d)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			
			//BT可用检测
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(3000);
			}
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：BT建立连接失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			byte[] tempBuf = new byte[5];
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "rwPre",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
				continue;
			}
			
			//发送数据
			if ((send_len = sockSend(socketUtil_wifi,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,wifiPara))!= sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), bak-cnt, sendPacket.getLen(), send_len);
				postEnd(bluetoothAdapter);
				continue;
			}
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbufwifi, (byte) 0);
			if ((rec_len = sockRecv(socketUtil_wifi,rbufwifi, sendPacket.getLen(), SO_TIMEO,wifiPara)) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), bak-cnt, sendPacket.getLen(), rec_len);
				postEnd(bluetoothAdapter);
				continue;
			}
			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbufwifi, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(), bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line:%d:第%d次数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}

			if((ret = layerBase.transDown(socketUtil_wifi,wifiPara.getSock_t()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:transDown失败（%d）", Tools.getLineInfo(), bak-cnt,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			if((ret = layerBase.netDown(socketUtil_wifi,wifiPara,wifiPara.getSock_t(),wifiPara.getType()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:netDown失败（%d）", Tools.getLineInfo(), bak-cnt,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			succ++;
			postEnd(bluetoothAdapter);
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		bluetoothManager.cancel(dataService);
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
	}
}
