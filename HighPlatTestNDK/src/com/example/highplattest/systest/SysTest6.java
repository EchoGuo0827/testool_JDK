package com.example.highplattest.systest;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.TransStatus;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest6.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150309
 * directory 		: 
 * description 		: ETH综合测试
 * related document :
 * history 		 	: author			date			remarks
 * history 		 	: 变更点										     变更人员				变更时间
 * 					  N850 F10   以太网打开1  关闭-1  状态未知返回0  				陈丁			  20200602
 * 					  F7以太网状态值修改								陈丁					20200710
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest6 extends DefaultFragment implements NDK
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest6.class.getSimpleName();
	private final int DEFAULT_CNT_VLE = 100;// 默认压力次数值
	private final String TESTITEM = "ETH压力、性能";
	private static final String usbmode_switch = "/sys/class/usb_ctrl/otg_mode";
	
	/*----------global variables declaration------------------------*/
	WifiPara wifipara = new WifiPara();
	EthernetPara ethernetPara = new EthernetPara();
	NetWorkingBase[] netWorkingBases = {wifipara,ethernetPara};
	SocketUtil socketUtil;
	private Gui gui = new Gui(myactivity, handler);
	
	public void systest6() 
	{
		// 初始化LayBase
		initLayer();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{	
			//2.ETH性能\n 移到SysTest300中
			int returnValue=gui.cls_show_msg("ETH综合测试(确保以太网认证成功)\n0.ETH配置\n1.ETH压力\n3.ETH异常\n4.验证以太网连接与Usb_Host的冲突(X5)\n5.开关压力");
			switch (returnValue) 
			{	
			case '0':
				switch (new Config(myactivity,handler).confConnEth(ethernetPara)) 
				{
				case NDK_OK:
					socketUtil = new SocketUtil(ethernetPara.getServerIp(), ethernetPara.getServerPort());
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"line %d:网络未连通！", Tools.getLineInfo());
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				ethPrecess();
				break;
				
			/*case '2':
				ethFunction();
				break;*/
				
			case '3':
				ethAbnormal();
				break;	
				
			case '4':
				gui.cls_show_msg1(1, "压测不要超过100次");
				ethX5test();
				break;
				
			case '5':
				gui.cls_show_msg1(1, "压测不要超过100次");
				powerPress();
				break;
				
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	/***
	 * 以太网的开关压力  by 胡慧婕 2019年
	 */
	private void powerPress() 
	{
		/*private & local definition*/
		int ret = 0,succ = 0,i= 0,cnt = DEFAULT_CNT_VLE;
		LinkType type = ethernetPara.getType();
		Sock_t sock_t = ethernetPara.getSock_t();
		
		/*process body*/
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE));
		cnt = packet.getLifecycle();
		
		while(cnt>0)
		{
			if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "以太网开关压力测试中,还剩%d次(已成功%d次),【取消】退出测试", cnt,succ)==ESC)
				break;
			cnt--;
			i++;
			if((ret = layerBase.netUp(ethernetPara, LinkType.ETH))!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, "powerProcess",g_keeptime, "line %d:以太网打开失败(ret = %d,已成功%d次)", Tools.getLineInfo(),ret,succ);
				layerBase.netDown(socketUtil, ethernetPara, sock_t, type);
				break;
			}

			if((ret = layerBase.netDown(socketUtil, ethernetPara, sock_t, type))!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, "powerProcess",g_keeptime, "line %d:以太网打开失败(ret = %d,已成功%d次)", Tools.getLineInfo(),ret,succ);
				break;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "powerProcess", g_time_0,"开关压力测试%d次，成功%d次", i,succ);
		
	}

	/**
	 * 验证以太网连接和Usb_Host的冲突
	 */
	private void ethX5test() {
		String funcName = "ethX5test";
		int ret=-1000;
		int count=1,succ = 0,i=0;
		LinkType type = ethernetPara.getType();
		Sock_t sock_t = ethernetPara.getSock_t();
		
		
		byte[] buf = new byte[PACKMAXLEN];
		int slen = 0,rlen = 0,startLen = 0;
		PacketBean sendPacket = new PacketBean();
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
		startLen = sendPacket.getLen();
		byte[] rbuf = new byte[startLen];
		
		while (true) 
		{	
			if(NetworkUtil.checkNet(myactivity)==ConnectivityManager.TYPE_ETHERNET)
			{
				gui.cls_show_msg1(100, "【测试前置,断开以太网中】");
				layerBase.netDown(socketUtil, ethernetPara, sock_t, type);
			}
			if(gui.cls_show_msg1(2,"正在进行第%d次X5eth与usb_host异常测试(已成功%d次),【取消】退出测试",count, succ)==ESC)
				break;
			count++;
			
			if(layerBase.netUp(ethernetPara,type)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetUp失败", Tools.getLineInfo());
				continue;
			}
			
			if(layerBase.transUp(socketUtil,sock_t)!=NDK_OK)
			{
				layerBase.netDown(socketUtil,ethernetPara,ethernetPara.getSock_t(),type);
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
				continue;
			}
			if(update_snd_packet(sendPacket,type)!=NDK_OK)
				break;
			// 发送数据
			if((slen = sockSend(socketUtil,sendPacket.getHeader(), startLen, SO_TIMEO,ethernetPara))!= startLen)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次发送数据失败(实际len = %d,预期len = %d)", Tools.getLineInfo(),i,slen,startLen);
				// 重连socket
				Layer.transStatus = TransStatus.TRANSDOWN;
				new Layer(myactivity,handler).transUp(socketUtil, ethernetPara.getSock_t());
				continue;
					
			}
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil,rbuf,startLen, SO_TIMEO,ethernetPara))!= startLen)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:第%d次接收数据失败(实际%d,预期%d)", Tools.getLineInfo(),i,rlen,startLen);
				continue;
			}
			// 比较数据
			if(Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())==false)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:第%d次检验收发数据失败", Tools.getLineInfo(),i);
				continue;
			}
			gui.cls_show_msg2(0.1f, "通讯成功开始断开以太网和Usbhost");
			//断开以太网
			if ((ret=layerBase.transDown(socketUtil,sock_t))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:transDown失败", Tools.getLineInfo(),ret);
				continue;
			}
			if ((layerBase.netDown(socketUtil,ethernetPara,sock_t,type))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:netDown失败", Tools.getLineInfo(),ret);
				continue;
			}
			succ++;
			//关闭usbhost
			setUSBMode(0);
		}
		gui.cls_show_msg1_record(TAG, funcName, g_time_0,"验证以太网连接和Usb_Host的冲突压力测试通过,测试次数%d次,成功%d次",count-1,succ);
	}

	//ETH压力
	public void ethPrecess() 
	{
		while(true)
		{
			int returnValue=-1;
			// 连续压力测试
			if(GlobalVariable.gSequencePressFlag)
			{
				if (++returnValue == '5') 
				{
					gui.cls_show_msg1(2, "%s连续压力测试结束", TESTITEM);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行连续压力测试，【取消】键退出")==ESC)
					return;
			}
			else 
			{
				returnValue = gui.cls_show_msg("ETH压力测试\n0.数传\n1.连接\n2.数传+连接\n3.建链压力\n4.ping压力");
			}
			switch (returnValue) 
			{

			case '0':
				dataTransPress();
				break;

			case '1':
				connectPress();
				break;

			case '2':
				dataConnPress();
				break;

			case '3':
				ethUpDownPress();
				break;

			case '4':
				pingPress();
				break;

			case ESC:
				return;

			}
		}
	}
	
	
	/**
	 * 数传 
	 */
	public void dataTransPress() 
	{
		/*private & local definition*/
		String funcName = "dataTransPress";
		int ret=-1;
		LinkType type = ethernetPara.getType();
		Sock_t sock_t = ethernetPara.getSock_t();
		
		/*process body*/
		while(true)
		{
			int returnValue=-1;
			if(GlobalVariable.gSequencePressFlag)//20170116wangxiaoyu
			{
				if (++returnValue == 1)
					return;
				gui.cls_show_msg1(2, "要测试单向发送、单向接收压力需关闭连续压力开关进行");
			}
			else
			{
				returnValue=gui.cls_show_msg("_ETH收发压力\n0.双向收发压力\n1.单向接收压力\n2.单向发送压力");
			}
			
			if(returnValue == ESC)
				break;
			// 链路连接
			if((ret=layerBase.netUp(ethernetPara,type))!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetUp失败(%d)", Tools.getLineInfo(),ret);
				continue;
			}
			
			if((ret=layerBase.transUp(socketUtil,sock_t))!=SUCC)
			{
				layerBase.netDown(socketUtil,ethernetPara,ethernetPara.getSock_t(),type);
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:TransUp失败(%d)", Tools.getLineInfo(),ret);
				continue;
			}
			if(returnValue=='0')
			{
				send_recv_press(socketUtil,type,ethernetPara);
			}
			else if(returnValue == '1')
			{
				recvOnlyPress(socketUtil,type,ethernetPara);
			}
			else if(returnValue == '2')
			{
				sendOnlyPress(socketUtil,type,ethernetPara);
			}
			if((ret=layerBase.transDown(socketUtil,sock_t))!=SUCC)
			{
				layerBase.netDown(socketUtil,ethernetPara,ethernetPara.getSock_t(),type);
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:transDown失败(%d)", Tools.getLineInfo(),ret);
				continue;
			}
			if((ret = layerBase.netDown(socketUtil,ethernetPara,sock_t,type))!=SUCC)
			{
				gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:netDown失败(%d)", Tools.getLineInfo(),ret);
				continue;
			}
		}
	}
	
	/**
	 * 连接压力  打开以太网(->建立socket连接->断开socket连接->)关闭以太网
	 */
	public void connectPress() 
	{
		/*private & local definition*/
		String funcName = "connectPress";
		int i = 0,succ_count = 0,ret = -1;
		LinkType type = ethernetPara.getType();
		Sock_t sock_t = ethernetPara.getSock_t();
		
		/*process body*/
		if(layerBase.netUp(ethernetPara,type)!=NDK_OK)
		{
			ret = gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetUp失败", Tools.getLineInfo());
			if(ret != NDK_OK)
				return;
		}
		while(true)
		{
			if(!GlobalVariable.gSequencePressFlag)//20170116wangxiaoyu
			{
				if(getCycleValue() == i)
					break;
				gui.cls_show_msg2( 0.1f,"正在进行第%d次连接压力(已成功%d次)", i+1,succ_count);
			}
			else
			{
				if(gui.cls_show_msg2(0.1f,String.format("正在进行第%d次连接压力(已成功%d次),【取消】键退出测试", i+1,succ_count))==ESC)
					break;
			}
			i++;
			if(layerBase.transUp(socketUtil,sock_t) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:TransUp失败", Tools.getLineInfo());
				continue;
			}
			if(layerBase.transDown(socketUtil,sock_t)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"line %d:TransDown失败", Tools.getLineInfo());
				continue;
			}
			succ_count++;
		}
		layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
		gui.cls_show_msg1_record(TAG, "dataConnect",g_time_0, "连接压力测试完成->通讯次数:%d,成功次数:%d", i,succ_count);
		
	}
	
	//连接+数传压力
	@SuppressLint("NewApi")
	public void dataConnPress() 
	{
		/*private & local definition*/
		int i = 0,succ_count = 0,j = 0,loop = 0;
		Sock_t sock_t = ethernetPara.getSock_t();
		LinkType type = ethernetPara.getType();
		int slen = 0,rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
		if(layerBase.netUp(ethernetPara,type) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "dataConnPress",g_keeptime, "line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		while(true)
		{
			if(gui.cls_show_msg2(0.1f,"正在进行第%d次建链/通讯混合压力（已成功%d次），本次将收发%d回，【取消】退出测试", i+1,succ_count,(loop = (int) (Math.random()*10+1)))==ESC)
				break;
			// 控制循环次数
			if(update_snd_packet(sendPacket,type)!=NDK_OK)
				break;
			i++;
			// 传输层建立
			if(layerBase.transUp(socketUtil,sock_t)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "dataConnPress",g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
				continue;
			}
			for (j = 0; j < loop; j++) 
			{
				// 发送
				if((slen = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,ethernetPara))!= sendPacket.getLen())
				{
					layerBase.transDown(socketUtil,sock_t);
					gui.cls_show_msg1_record(TAG, "dataConnPress",g_keeptime, "line %d:发送失败（实际%d，预期%d）", Tools.getLineInfo(),slen,sendPacket.getLen());
					break;
				}
				
				// 接收
				if((rlen = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,ethernetPara))!=sendPacket.getLen())
				{
					layerBase.transDown(socketUtil,sock_t);
					gui.cls_show_msg1_record(TAG, "dataConnPress", g_keeptime,"line %d:接收失败（实际%d，预期%d）", Tools.getLineInfo(),rlen,sendPacket.getLen());
					break;
				}
				// 比较收发
				if(!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen()))
				{
					layerBase.transDown(socketUtil,sock_t);
					gui.cls_show_msg1_record(TAG, "dataConnPress",g_keeptime, "line %d:校验失败" , Tools.getLineInfo());
					break;
				}
			}
			if(slen!= sendPacket.getLen()||rlen!= sendPacket.getLen())
			{
				continue;
			}
			// 挂断
			if(layerBase.transDown(socketUtil,sock_t)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "dataConnPress", g_keeptime,"line %d:TransDown失败", Tools.getLineInfo());
				continue;
			}
			succ_count++;
		}
		layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
		gui.cls_show_msg1_record(TAG, "connTransPre", g_time_0,"通讯次数:%d,成功次数:%d\n", i, succ_count);
	}
	
	// 建链压力(打开以太网-》socket连接-》socket断开-》关闭以太网)
	public int ethUpDownPress() 
	{
		/*private & local definition*/
		String funcName = "ethUpDownPress";
		int i = 0,succ = 0,iRet=-1;
		Sock_t sock_t = ethernetPara.getSock_t();
		LinkType type = ethernetPara.getType();
		
		/*process body*/
		while(true)
		{
			if(!GlobalVariable.gSequencePressFlag)//20170116wangxiaoyu
			{
				if(getCycleValue() == i)
					break;
					gui.cls_show_msg2(0.1f, "正在进行第%d次以太网芯片压力（已成功%d次）", i+1,succ);
			}
			else
			{
				if(gui.cls_show_msg2(0.1f,"正在进行第%d次以太网芯片压力（已成功%d次），【取消】退出测试", i + 1, succ)==ESC)
					break;
			}
			i++;
			if((iRet = layerBase.netUp(ethernetPara,type))!=NDK_OK)
			{
					gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次NetUp失败(ret = %d)", Tools.getLineInfo(),i,iRet);
					continue;
			}
			
			if((iRet = layerBase.transUp(socketUtil,sock_t))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次TransUp失败(ret = %d)", Tools.getLineInfo(),i,iRet);
				layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
				continue;
			}
			if(layerBase.transDown(socketUtil,sock_t)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次TransDown失败", Tools.getLineInfo(),i);
				continue;
			}

			if(layerBase.netDown(socketUtil,ethernetPara,sock_t,type)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime, "line %d:第%d次NetDown失败", Tools.getLineInfo(),i);
				continue;
			}
			succ++;
		}
		// 测试后置，关闭以太网
		layerBase.netDown(socketUtil, ethernetPara, sock_t, type);
		gui.cls_show_msg1_record(TAG, funcName,g_time_0, "压力次数：%d，成功次数：%d", i,succ);
		return NDK_OK;
	}
	
	//ping压力
	@SuppressLint("NewApi")
	public void pingPress() 
	{
		/*private & local definition*/
		String funcName = "pingPress";
		int ret = 0,succ = 0,i= 0,cnt = DEFAULT_CNT_VLE;
		LinkType type = ethernetPara.getType();
		Sock_t sock_t = ethernetPara.getSock_t();
		
		/*process body*/
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE));
		cnt = packet.getLifecycle();
		
		if(layerBase.netUp(ethernetPara,type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		
		while(cnt>0)
		{
			if(gui.cls_show_msg2(0.1f, "ping(202.108.22.5)压力测试中,还剩%d次(已成功%d次),【取消】退出测试", cnt,succ)==ESC)
				break;
			cnt--;
			i++;
			try 
			{
				NetworkUtil.pingTest("202.108.22.5", TIMEOUT_NET);
				succ++;
			} catch (Exception e) 
			{
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, funcName, g_keeptime,"第%d次:测试失败,错误返回%d", i,ret);
			}
		}
		// 关闭以太网
		layerBase.netDown(socketUtil, ethernetPara, sock_t, type);
		gui.cls_show_msg1_record(TAG, funcName, g_time_0,"ping压力测试%d次，成功%d次", i,succ);
	}
	
	
	//ETH性能测试
	public void ethFunction() 
	{
		/*private & local definition*/
		int cnt = 0,i = 0;
		float commtimes = 0.0f;
		int slen = 0;
		PacketBean sendPacket = new PacketBean();
		long startTime;
		Sock_t sock_t = ethernetPara.getSock_t();
		LinkType type = ethernetPara.getType();
		
		/*process body*/
		cnt = sendPacket.getLifecycle();
		byte[] rbuf = new byte[sendPacket.getLen()];
		// 建立网络连接
		if(layerBase.netUp(ethernetPara,type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		
		if(layerBase.transUp(socketUtil,sock_t)!=NDK_OK)
		{
			layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:TransUp失败", Tools.getLineInfo());
			return;
		}
		while(true)
		{
			gui.cls_show_msg2(0.1f, "共%d次，第%d次测试", cnt,i+1);
			if(update_snd_packet(sendPacket,type)!=NDK_OK)
				break;
			i++;
			startTime = System.currentTimeMillis();
			slen = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,ethernetPara);
			Arrays.fill(rbuf, (byte) 0);
			sockRecv(socketUtil,rbuf, slen, SO_TIMEO,ethernetPara);
			commtimes = commtimes + Tools.getStopTime(startTime);
			// 比较收发
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen()))
			{
				layerBase.transDown(socketUtil,sock_t);
				gui.cls_show_msg1_record(TAG, "ethFunction",g_keeptime, "line %d:校验失败" , Tools.getLineInfo());
				break;
			}
		}
		
		layerBase.transDown(socketUtil,sock_t);
		layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
		gui.cls_show_msg1_record(TAG, TESTITEM, g_time_0,"通讯速率%fKB/s", (sendPacket.getLen()*2/1024)/(commtimes/cnt));
	}
	
	//ETH异常:只支持强制挂断
	public void ethAbnormal() 
	{
		/*private & local definition*/
		Sock_t sock_t = ethernetPara.getSock_t();
		LinkType type = ethernetPara.getType();
		byte[] buf = new byte[PACKMAXLEN];
		int j = 0;
		int slen = 0;
		
		/*process body*/
		for ( j = 0; j < buf.length; j++) {
			buf[j] = (byte) (Math.random()*256);
		}
		
		if(layerBase.netUp(ethernetPara,type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		
		if(layerBase.transUp(socketUtil,sock_t)!=NDK_OK)
		{
			layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:TransUp失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg("请拔掉Android端的网线,等待10s后点任意键");
		if((slen = sockSend(socketUtil,buf, PACKMAXLEN, SO_TIMEO,ethernetPara))!=NDK_ERR)
		{
			layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:发送成功，预期（-1），（实际%d）", Tools.getLineInfo(),slen);
			return;
		}
		layerBase.transDown(socketUtil,sock_t);
	
		gui.cls_show_msg("重新插上Android端的网线，完成点任意键继续");
	
		
		if (gui.ShowMessageBox("重新插上Android后台的网线后，等待20s查看后台是否有收到数据".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)==ENTER) 
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:强制挂断测试失败",
					Tools.getLineInfo());
		} 
		else 
			gui.cls_show_msg1(1,"强制挂断测试通过", Tools.getLineInfo());
		layerBase.netDown(socketUtil,ethernetPara,sock_t,type);
	}
	
	// x5测试适用
	private void setUSBMode(int mode) {
		File f = new File(usbmode_switch);
		if (f.exists()) {
			if (mode == 1) {
				BaseFragment.setNodeFile(usbmode_switch, "1");
			} else if (mode == 0) {
				BaseFragment.setNodeFile(usbmode_switch, "0");
			}
		}
	}
	
}
