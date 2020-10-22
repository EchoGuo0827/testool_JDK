package com.example.highplattest.systest;

import java.io.IOException;
import java.util.Arrays;

import android.annotation.SuppressLint;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest69.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160902
 * directory 		: 
 * description 		: NFC/WLAN交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160902	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest69 extends DefaultFragment
{
	private final String TAG = SysTest69.class.getSimpleName();
	private final String TESTITEM = "NFC/WLAN";
	Nfc_Card nfc_card = Nfc_Card.NFC_B;
	WifiPara wifiPara = new WifiPara();
	SocketUtil socketUtil;
	private Gui gui = null;
	private Config config;
	
	public void systest69()
	{
		gui = new Gui(myactivity, handler);
		// wifi初始化
		initLayer();
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			nfc_card = nfc_config(handler, TESTITEM);
			config.confConnWlan(wifiPara);
			try {
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG, g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/WLAN\n0.NFC配置\n1.WLAN配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 配置nfc
				nfc_card = nfc_config(handler, TESTITEM);
				break;
				
			case '1':
				// 配置wifi
				wifiPara.setType(LinkType.WLAN);
				config.confConnWlan(wifiPara);
				break;
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					e.printStackTrace();
					gui.cls_show_msg1(1, "抛出%s异常", e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;

			default:
				break;
			}
		}
	}
	
	
	/**
	 * nfc/wifi交叉测试
	 */
	public void cross_test() 
	{
		/*private & local definition*/
		int ret = -1, i = 0, succ = 0, send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		NfcTool nfcTool = new NfcTool(myactivity);
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
	
		gui.cls_show_msg("测试前请确保，已安装好测试卡...");
		// 链路连接
		if((ret=layerBase.netUp(wifiPara,type)) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:netUp失败（%d）", Tools.getLineInfo(),ret);
			return;
		}
		while(true)
		{
			//保护动作
			nfcTool.nfcDisEnableMode();
			layerBase.transDown(socketUtil,sock_t);
			
			//测试退出点
			if(gui.cls_show_msg1(3,"%s/WLAN交叉测试，已执行%d次，成功%d次，【取消】退出测试",nfc_card,i,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket,type)!= NDK_OK)
				break;
			i++;
			// 传输层建立
			if((ret=layerBase.transUp(socketUtil,sock_t))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:transUp失败（%d）", Tools.getLineInfo(), i, ret);
				continue;
			}
			//上电
			if((ret = nfcTool.nfcConnect(reader_flag)) != NDK_OK )
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s卡连接失败（%d）", Tools.getLineInfo(), i, nfc_card,ret);
				continue;
			}
			
			//数据通讯
			//发送数据
			if ((send_len = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,wifiPara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:发送数据失败（预期:%d,实际:%d）", Tools.getLineInfo(),i, sendPacket.getLen(), send_len);
				continue;
			}
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if ((rec_len = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,wifiPara)) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:接收数据失败（预期:%d,实际:%d）", Tools.getLineInfo(),i, sendPacket.getLen(), rec_len);
				continue;
			}
			//读写
			try {
				if ((ret=nfcTool.nfcRw(nfc_card)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s卡APDU失败（%d）", Tools.getLineInfo(), i,nfc_card, ret);
					continue;
				}
			} catch (IOException e) {
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s卡APDU失败（%d）", Tools.getLineInfo(), i, nfc_card,ret);
				continue;
			}
			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(),i);
				continue;
			}
			//下电
			nfcTool.nfcDisEnableMode();
			//挂断
			if((ret=layerBase.transDown(socketUtil,sock_t))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:transDown失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			succ++;
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		nfcTool.nfcDisEnableMode();
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s/WLAN交叉测试完成,已执行次数为%d,成功为%d次",nfc_card, i,succ);
	}
}
