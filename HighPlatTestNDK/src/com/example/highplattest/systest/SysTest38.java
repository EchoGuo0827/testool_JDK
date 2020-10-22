package com.example.highplattest.systest;

import java.util.Arrays;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.EthernetPara;
//import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest38.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: LAN/WLM交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest38 extends DefaultFragment
{
	private final String TAG = SysTest38.class.getSimpleName();
	private final String TESTITEM = "LAN/WLM";
	private EthernetPara ethernetPara = new EthernetPara();
	private MobilePara mobilePara = new MobilePara();
	private WifiPara wifiPara = new WifiPara();
	private NetWorkingBase[] netWorkingBases = {ethernetPara,wifiPara};
	private SocketUtil socketUtilLan, socketUtilWlm;
	SocketUtil socketUtil;
	private Config config;
	private Gui gui = null;
	private boolean Wlmflag=false;  //sim卡状态的flag
	private int ret=-1;
	
	public void systest38 () 
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		//无线配置
		initLayer();
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		//测试前获取网络状态 by chending 20200426
		Wlmflag =mobileUtil.getMobileDataState(myactivity);
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			//WLM配置
			if(config.confConnWLM(true,mobilePara)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime,"line %d:网络未接通!!!");
				return;
			}
			//LAN配置
			config.confConnLAN(wifiPara, ethernetPara);
			//交叉测试
			try 
			{
				cross_test();
			} catch (Exception e) 
			{
				e.printStackTrace();
				gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
			}
			mobileUtil.setMobileData(myactivity, Wlmflag);
			return;
			
		}
		while(true)
		{
			int returnValue=gui.cls_show_msg("LAN/WLM\n0.WLM配置\n1.LAN配置\n2.交叉测试");
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
					gui.cls_show_msg1(0,"line %d:网络未接通!!!",Tools.getLineInfo());
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				//调用LAN配置函数
				config.confConnLAN(wifiPara, ethernetPara);
				break;
				
			case '2':
				cross_test();
				break;
			
			case ESC:
				mobileUtil.setMobileData(myactivity, Wlmflag);
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
		socketUtilLan = new SocketUtil( netWorkingBases[j].getServerIp(), netWorkingBases[j].getServerPort());
		
		/*process body*/
		//保护动作
		layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
		
		//建立网络连接
		if((ret = layerBase.netUp(netWorkingBases[j],type[j])) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:NetUp失败（%d）", Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
			
		// 传输层建立
		if((ret=layerBase.transUp(socketUtilLan,sock_t[j]))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:transUp失败（%d）", Tools.getLineInfo(),ret);
			layerBase.transDown(socketUtilLan,sock_t[j]);
			layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
			
		//发送数据
		if ((send_len = sockSend(socketUtilLan,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, netWorkingBases[j]))!= sendPacket.getLen()) 
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:发送数据失败（预期:%d,实际:%d）", Tools.getLineInfo(),sendPacket.getLen(),send_len);
			layerBase.transDown(socketUtilLan,sock_t[j]);
			layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
			
		//接收数据
		Arrays.fill(rbuf, (byte) 0);
		if ((rec_len = sockRecv(socketUtilLan,rbuf, sendPacket.getLen(), SO_TIMEO, netWorkingBases[j])) != sendPacket.getLen()) 
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:接收数据失败（预期:%d,实际:%d）", Tools.getLineInfo(), sendPacket.getLen(), rec_len);
			layerBase.transDown(socketUtilLan,sock_t[j]);
			layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
			
		//比较收发
		if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:数据校验失败", Tools.getLineInfo());
			layerBase.transDown(socketUtilLan,sock_t[j]);
			layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}

		// 挂断
		if((ret = layerBase.transDown(socketUtilLan,sock_t[j]))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "lan_dial_comm",g_keeptime, "line %d:transDown失败（%d）", Tools.getLineInfo(),ret);
			layerBase.transDown(socketUtilLan,sock_t[j]);
			layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
			return NDK_ERR;
		}
		
		layerBase.netDown(socketUtilLan,netWorkingBases[j],sock_t[j],type[j]);
		
		return NDK_OK;
	}
	
	private int wlm_dial_comm(PacketBean sendPacket) 
	{
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		int ret = 0, send_len = 0,rec_len = 0;
		byte[] rbuf = new byte[PACKMAXLEN];
		socketUtilWlm = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		
		/*process body*/
		//保护动作
		layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
		//建立网络连接
		if((ret = layerBase.netUp(mobilePara,type)) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:NetUp失败（%d）", Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
			
		// 传输层建立
		if((ret=layerBase.transUp(socketUtilWlm,sock_t))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:transUp失败（%d）", Tools.getLineInfo(),ret);
			layerBase.transDown(socketUtilWlm,sock_t);
			layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
			return NDK_ERR;
		}
			
		//发送数据
		if ((send_len = sockSend(socketUtilWlm,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara))!= sendPacket.getLen()) 
		{
			gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:发送数据失败（预期:%d,实际:%d）", Tools.getLineInfo(),sendPacket.getLen(), send_len);
			layerBase.transDown(socketUtilWlm,sock_t);
			layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
			return NDK_ERR;
		}
			
		//接收数据
		Arrays.fill(rbuf, (byte) 0);
		if ((rec_len = sockRecv(socketUtilWlm,rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen()) 
		{
			gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:接收数据失败（预期:%d,实际:%d）", Tools.getLineInfo(),sendPacket.getLen(), rec_len);
			layerBase.transDown(socketUtilWlm,sock_t);
			layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
			return NDK_ERR;
		}
			
		//比较收发
		if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
		{
			gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:数据校验失败", Tools.getLineInfo());
			layerBase.transDown(socketUtilWlm,sock_t);
			layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
			return NDK_ERR;
		}

		// 挂断
		if((ret = layerBase.transDown(socketUtilWlm,sock_t))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:transDown失败（%d）", Tools.getLineInfo(),ret);
			layerBase.transDown(socketUtilWlm,sock_t);
			layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
			return NDK_ERR;
		}
		
		layerBase.netDown(socketUtilWlm,mobilePara,sock_t,type);
		return NDK_OK;
	}

	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		setWireType(mobilePara);
		LinkType[] type = {ethernetPara.getType(),wifiPara.getType()};
		LinkType wlmtype = mobilePara.getType();
		int j = GlobalVariable.chooseConfig;
		Log.e(TAG+" j", j+"");
		int i = 0, lansucc = 0, Wlmsucc = 0, times = 0;
		byte[] buf = new byte[PACKMAXLEN];
		PacketBean sendPacketWlm = new PacketBean();
		PacketBean sendPacketLan = new PacketBean();
		
		/*process body*/
		init_snd_packet(sendPacketWlm, buf);
		set_snd_packet(sendPacketWlm, wlmtype);
		Gui gui = new Gui(myactivity,handler);
		
		init_snd_packet(sendPacketLan, buf);
		set_snd_packet(sendPacketLan, type[j]);
		
		//临时修改包的生命周期为较小的生命周期
		if(sendPacketLan.getLifecycle()>=sendPacketWlm.getLifecycle())
			times = sendPacketWlm.getLifecycle();
		else
			times = sendPacketLan.getLifecycle();
		sendPacketWlm.setLifecycle(times);
		sendPacketLan.setLifecycle(times);
		
		while(true)
		{
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，WLM成功%d次，%s成功:%d次，【取消】退出测试",TESTITEM,i,Wlmsucc,type[j],lansucc)==ESC)
				break;
			
			//wlm通讯测试
			if(update_snd_packet(sendPacketWlm, wlmtype)!= NDK_OK)
				break;
			
			i++;
			if(wlm_dial_comm(sendPacketWlm)==NDK_OK)
				Wlmsucc++;
			
			//lan通讯测试
			if(update_snd_packet(sendPacketLan, type[j])!= NDK_OK)
				break;
			if(lan_dial_comm(sendPacketLan)==NDK_OK)
				lansucc++;
		}
		
		//测试后置   如果测试前是开启状态就恢复成开启
		if (Wlmflag) {
			if((ret = layerBase.netUp(mobilePara,wlmtype)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "wlm_dial_comm",g_keeptime, "line %d:NetUp失败（%d）", Tools.getLineInfo(),ret);
			}
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s交叉测试完成,执行次数%d,WLM成功%d次,%s成功:%d次",TESTITEM, i, Wlmsucc, type[j],lansucc);
	}
}
