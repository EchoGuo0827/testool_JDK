package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
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
 * file name 		: SysTest35.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150420
 * directory 		: 
 * description 		: 无线/蓝牙交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest35 extends DefaultFragment
{
	private final String TAG = SysTest35.class.getSimpleName();
	private final String TESTITEM = "WLM/BT";
	private MobilePara mobilePara = new MobilePara();
	SocketUtil socketUtil;
	private final int BUFSIZE_BT = BT_BUF_SIZE;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private Config config;
	private Gui gui = null;
	
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	
	public void systest35() 
	{
		gui = new Gui(myactivity, handler);

		config = new Config(myactivity, handler);
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		//无线初始化
		initLayer();
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		boolean mobilestate=mobileUtil.getMobileDataState(myactivity);
		mobileUtil.closeOther();
		//自动化测试
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			//无线配置
			if(config.confConnWLM(true,mobilePara)!=NDK_OK){
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime,"line %d:网络未接通!!!");
				return;
			}
			socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
			//BT配置
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr());
			//交叉测试
			try 
			{
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			mobileUtil.setMobileData(myactivity, mobilestate);
			return;
			
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("WLM/BT\n0.无线配置\n1.BT配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 无线配置
				switch (config.confConnWLM(true,mobilePara)) 
				{
				case NDK_OK:
					socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
					gui.cls_show_msg1(2, "网络配置成功!!!");
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:网络未接通!!!");
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				// 蓝牙配置
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				break;
				
			case '2':
				cross_test();
				break;
			
			case ESC:
				mobileUtil.setMobileData(myactivity, mobilestate);
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
		
		/*process body*/	
		
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		/*private & local definition*/
		int slen = 0,rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufwlm = new byte[PACKMAXLEN];
		
		setWireType(mobilePara);
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		if(GlobalVariable.gSequencePressFlag)
			sendPacket.setLifecycle(getCycleValue());
	
		bak = cnt = sendPacket.getLifecycle();
		while(cnt>0)
		{
			//无线保护动作
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
			
			if(gui.cls_show_msg1(1, "请打开BluetoothServer工具，%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", TESTITEM,bak-cnt,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!=NDK_OK)
				break;
			cnt--;
			//无线Netup、TransUp
			if((ret = layerBase.netUp( mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetUp失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransUp失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			//蓝牙可用检测
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
			//蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT建立连接失败", Tools.getLineInfo(),bak-cnt);
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

			//无线发送数据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,slen);
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

			//无线接收数据
			Arrays.fill(rbufwlm, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbufwlm, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,rlen);
				postEnd(bluetoothAdapter);
				continue;
			}
			//无线比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbufwlm, rlen))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:校验数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line:%d:第%d次数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			//无线TransDown、NetDown
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransDown失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:NetDown失败(ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			succ++;
			postEnd(bluetoothAdapter);
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		bluetoothManager.cancel(dataService);
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
	}
}
