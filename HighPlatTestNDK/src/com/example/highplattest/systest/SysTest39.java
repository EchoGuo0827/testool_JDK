package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest39.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: MDM/LAN交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest39 extends DefaultFragment
{
	private final String TAG = SysTest39.class.getSimpleName();
	private final String TESTITEM = "MDM/LAN交叉";
	private WifiPara wifiPara = new WifiPara();
	private EthernetPara ethernetPara = new EthernetPara();
	NetWorkingBase[] netWorkingBases = {ethernetPara,wifiPara};
	private NlModemManager nlModemManager;
	private Gui gui = null;
	
	public void systest39() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		initLayer();
		nlModemManager =  (NlModemManager) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("MDM/LAN交叉\n0.MDM配置\n1.LAN配置\n2.交叉测试");
			switch (returnValue) 
			{
			
			case '0':
				new Config(myactivity,handler).config_para();
				break;
				
			case '1':
				//调用LAN配置函数
				new Config(myactivity,handler).confConnLAN(wifiPara,ethernetPara);//20170116wangxiaoyu
				break;
				
			case '2':
				cross_test();
				break;
			
			case ESC:
				intentSys();
				return;
			}
		}
		
	}
	
	
	public int lan_dial_comm(PacketBean sendPacket) 
	{
		/*private & local definition*/
		LinkType[] type = {ethernetPara.getType(),wifiPara.getType()};
		int j = GlobalVariable.chooseConfig;
		Log.e(TAG+" j", j+"");
		Sock_t[] sock_t = {ethernetPara.getSock_t(),wifiPara.getSock_t()};
		int ret = 0, send_len = 0,rec_len = 0;
		byte[] rbuf = new byte[PACKMAXLEN];
		SocketUtil socketUtil = new SocketUtil( netWorkingBases[j].getServerIp(), netWorkingBases[j].getServerPort());
		
		/*process body*/
		//建立网络连接
		if(layerBase.netUp(netWorkingBases[j],type[j]) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			return NDK_ERR;
		}
			
		// 传输层建立
		if((ret=layerBase.transUp(socketUtil,sock_t[j]))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:transUp失败(%d)", Tools.getLineInfo(), ret);
			layerBase.transDown(socketUtil,sock_t[j]);
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
			
		//发送数据
		if ((send_len = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, netWorkingBases[j]))!= sendPacket.getLen()) 
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), sendPacket.getLen(), send_len);
			layerBase.transDown(socketUtil,sock_t[j]);
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
			
		//接收数据
		Arrays.fill(rbuf, (byte) 0);
		if ((rec_len = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO, netWorkingBases[j])) != sendPacket.getLen()) 
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), sendPacket.getLen(), rec_len);
			layerBase.transDown(socketUtil,sock_t[j]);
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
			
		//比较收发
		if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:数据校验失败", Tools.getLineInfo());
			layerBase.transDown(socketUtil,sock_t[j]);
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}

		// 挂断
		if(layerBase.transDown(socketUtil,sock_t[j])!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:transDown失败", Tools.getLineInfo());
			layerBase.transDown(socketUtil,sock_t[j]);
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
		
		layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
		return NDK_OK;
	}
	
	private int mdm_dial_comm(PacketBean sendPacket) 
	{
		LinkType type = ModemBean.type_MDM;
		int ret = -1, send_len = 0, rec_len = 0;
		byte[] rbuf = new byte[PACKMAXLEN];
		
		/*process body*/
		mdm_clrportbuf_all(nlModemManager);
		layerBase.mdm_hangup(nlModemManager);
		//初始化MODEM
		gui.cls_show_msg1(2, "初始化MODEM中...");
		if((ret=mdm_init(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_comm",g_keeptime, "line %d:MDM初始化失败(%d)", Tools.getLineInfo(), ret);
			return NDK_ERR;
		}
		//拨号
		gui.cls_show_msg1(2, "MODEM拨%s中...",ModemBean.MDMDialStr);
		if((ret = layerBase.mdm_dial(ModemBean.MDMDialStr, nlModemManager))!= NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_comm",g_keeptime, "line %d:MDM拨号失败(%d)", Tools.getLineInfo(), ret);
			layerBase.mdm_hangup(nlModemManager);
			return NDK_ERR;
		}
		//数据通讯
		//发送数据
		if((send_len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_comm",g_keeptime, "line %d:MDM发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), sendPacket.getLen(), send_len);
			layerBase.mdm_hangup(nlModemManager);
			return NDK_ERR;
		}
		//接收数据
		if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 20, type))!= sendPacket.getLen())
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_comm",g_keeptime, "line %d:MDM接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), sendPacket.getLen(), rec_len);
			layerBase.mdm_hangup(nlModemManager);
			return NDK_ERR;
		}	
		//比较数据
		if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(), type)!= NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_comm",g_keeptime, "line %d:MDM数据校验失败", Tools.getLineInfo());
			layerBase.mdm_hangup(nlModemManager);
			return NDK_ERR;
		}
		//挂断
		gui.cls_show_msg1(2, "MODEM挂断中...");
		if((ret=layerBase.mdm_hangup(nlModemManager))!= NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "mdm_dial_comm",g_keeptime, "line %d:MDM挂断失败(%d)", Tools.getLineInfo(), ret);
			layerBase.mdm_hangup(nlModemManager);
			return NDK_ERR;
		}
		return NDK_OK;
	}

	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		LinkType[] type = {ethernetPara.getType(),wifiPara.getType()};
		LinkType mdmtype = ModemBean.type_MDM;
		int j = GlobalVariable.chooseConfig;
		int ret;
		Log.e(TAG+" j", j+"");
		int i = 0, lansucc = 0, mdmsucc = 0, times = 0;
		byte[] buf = new byte[PACKMAXLEN];
		PacketBean sendPacketMdm = new PacketBean();
		PacketBean sendPacketLan = new PacketBean();
		
		/*process body*/
		init_snd_packet(sendPacketMdm, buf);
		set_snd_packet(sendPacketMdm, mdmtype);
		mdm_clrportbuf_all(nlModemManager);
		
		init_snd_packet(sendPacketLan, buf);
		set_snd_packet(sendPacketLan, type[j]);
		
		//临时修改包的生命周期为较小的生命周期
		if(sendPacketLan.getLifecycle()>=sendPacketMdm.getLifecycle())
			times = sendPacketMdm.getLifecycle();
		else
			times = sendPacketLan.getLifecycle();
		sendPacketMdm.setLifecycle(times);
		sendPacketLan.setLifecycle(times);
		// 测试前置，复位操作
		if((ret = mdm_reset(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:MDM复位失败（ret = %d）", Tools.getLineInfo(),ret);
			return;
		}
		while(true)
		{
			
			Log.e("i"+"  len", i+" "+sendPacketMdm.getLifecycle());
			//测试退出点
			if(gui.cls_show_msg1(3, "正在进行第%d次MDM/%s交叉测试(已成功%d次),【取消】退出测试", i+1, type[j], lansucc)==ESC)
				break;
			
			i++;
			
			//MDM拨号通讯测试
			if(update_snd_packet(sendPacketMdm, mdmtype)!= NDK_OK)
				break;
			if(mdm_dial_comm(sendPacketMdm)==NDK_OK)
				mdmsucc++;
			
			//lan拨号通讯测试
			if(update_snd_packet(sendPacketLan, type[j])!= NDK_OK)
				break;
			if(lan_dial_comm(sendPacketLan)==NDK_OK)
				lansucc++;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "MDM/%s测试完成,执行次数%d,MDM成功%d次,%s成功:%d次", type[j], i-1, mdmsucc, type[j],lansucc);
	}
}

