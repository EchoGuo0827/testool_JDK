package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;
import android.newland.net.ethernet.NlEthernetManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest33.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: WIFI/ETH交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * history 		 	: 变更点										     变更人员				变更时间
 * 					  N850 F10   以太网打开1  关闭-1  状态未知返回0  				陈丁			  20200602
 * 						F7以太网状态值修改								陈丁					20200710
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest33 extends DefaultFragment
{
	private final String TAG = SysTest33.class.getSimpleName();
	private final String TESTITEM = "WLAN/ETH";
	private WifiPara wifiPara = new WifiPara();
	EthernetUtil ethernetUtil;
	private EthernetPara ethernetPara = new EthernetPara();
	SocketUtil socketUtil;
	Gui gui = new Gui(myactivity, handler);
	//by2020 0107  以太网打开变成1  关闭变成0  原（打开2 关闭1）
	//N850 F10   以太网打开1  关闭-1  状态未知返回0
	private Config config;
	
	public void systest33() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		config = new Config(myactivity, handler);
		ethernetUtil = EthernetUtil.getInstance(myactivity, handler);

		initLayer();
		while(true)
		{
			int returnValue=gui.cls_show_msg("WLAN/ETH\n0.WIFI配置\n1.ETH配置\n2.交叉测试");
			switch (returnValue) 
			{
			
			case '0':
				// 扫描Ap的操作
				switch (config.confConnWlan(wifiPara)) 
				{
				case NDK_OK:
					socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
					break;
					
				case NDK_ERR:
					break;
				
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				ethernetPara.setType(LinkType.ETH);
				config.confChooseLan(ethernetPara);
				break;
				
			case '2':
				try
				{
					cross_test();
				}catch(Exception e){
					gui.cls_show_msg1_record(TAG, TAG,2, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
			
			case ESC:
				intentSys();
				return;
				
			}
		}
		
	}
	
	//交叉测试具体实现函数
	public void cross_test() throws Exception
	{
		/*private & local definition*/
		int i = 0, succ = 0, ret = 0;
		int send_len = 0,rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufeth = new byte[PACKMAXLEN];
		byte[] rbufwifi = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		SocketUtil socketUtil_eth, socketUtil_wifi;
		socketUtil_eth = new SocketUtil( ethernetPara.getServerIp(), ethernetPara.getServerPort());
		socketUtil_wifi = new SocketUtil( wifiPara.getServerIp(), wifiPara.getServerPort());
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,wifiPara.getType());
		
//		//保护动作
//		layerBase.transDown(socketUtil_eth,ethernetPara.getSock_t());
//		layerBase.transDown(socketUtil_wifi,wifiPara.getSock_t());
//		layerBase.netDown(socketUtil_wifi,wifiPara,wifiPara.getSock_t(),wifiPara.getType());
//		layerBase.netDown(socketUtil_eth,ethernetPara,ethernetPara.getSock_t(),ethernetPara.getType());
		while(true)
		{
			//保护动作
			layerBase.netDown(socketUtil_wifi,wifiPara,wifiPara.getSock_t(),wifiPara.getType());
			layerBase.netDown(socketUtil_eth,ethernetPara,ethernetPara.getSock_t(),ethernetPara.getType());
			//测试退出点
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，点击退出键退出测试", TESTITEM,i,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket,wifiPara.getType())!= NDK_OK)
				break;
			i++;
			// wifi
			if((ret=layerBase.netUp(wifiPara,wifiPara.getType())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次：NetUp失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			// 传输层建立
			if ((ret = layerBase.transUp(socketUtil_wifi, wifiPara.getSock_t())) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:transUp失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//发送数据
			if ((send_len = sockSend(socketUtil_wifi,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,wifiPara))!= sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
				continue;
			}
			//接收数据
			Arrays.fill(rbufwifi, (byte) 0);
			if ((rec_len = sockRecv(socketUtil_wifi,rbufwifi, sendPacket.getLen(), SO_TIMEO,wifiPara)) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}

			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbufwifi, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			
			if((ret=layerBase.transDown(socketUtil_wifi,wifiPara.getSock_t()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:transDown失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			layerBase.netDown(socketUtil_wifi,wifiPara,wifiPara.getSock_t(),wifiPara.getType());
			
			if((ret=layerBase.netUp(ethernetPara,ethernetPara.getType())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:NetUp失败(%d)", Tools.getLineInfo(),ret);
				continue;
			}
			
			if((ret=layerBase.transUp(socketUtil_eth,ethernetPara.getSock_t()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:transUp失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			if ((send_len = sockSend(socketUtil_eth,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,ethernetPara))!= sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
				continue;
			}
			
			Arrays.fill(rbufeth, (byte) 0);
			if ((rec_len = sockRecv(socketUtil_eth,rbufeth, sendPacket.getLen(), SO_TIMEO,ethernetPara)) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}
			
			if (!Tools.memcmp(sendPacket.getHeader(), rbufeth, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			
			// 挂断
			if((ret=layerBase.transDown(socketUtil_eth,ethernetPara.getSock_t()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:transDown失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			layerBase.netDown(socketUtil_eth,ethernetPara,ethernetPara.getSock_t(),ethernetPara.getType());
			
			
			succ++;
		}

		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次",TESTITEM,i, succ);
	}
}
