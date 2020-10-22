package com.example.highplattest.systest;

import java.util.Arrays;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest7.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150309
 * directory 		: 
 * description 		: WLM性能、压力
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest7 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest7.class.getSimpleName();
	private Gui gui = null;
	private final String TESTITEM = "无线性能,压力";
	private final int DEFAULT_COUTN_VLE = 100;
	private Config config;
	
	/*----------global variables declaration------------------------*/
	private MobilePara mobilePara = new MobilePara();
	SocketUtil socketUtil;
	boolean mobilestate;
	
	//无线综合测试主程序
	public void systest7() 
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		setWireType(mobilePara);
		TelephonyManager mTelephonyManager  = (TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		initLayer();
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		mobilestate=mobileUtil.getMobileDataState(myactivity);
		
		mobileUtil.closeOther();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			config.confConnWLM(true,mobilePara);
			socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
			g_CycleTime = DEFAULT_COUTN_VLE;
			wire_press();
			mobileUtil.setMobileData(myactivity, mobilestate);
			return;
		}
		while(true)
		{
			int returnValue=gui.cls_show_msg("无线综合测试\n0.WIRELESS配置\n1.WIRELESS压力\n2.WIRELESS性能\n3.WIRELESS异常");
			switch (returnValue) 
			{	
			case '0':
				// 无线配置
				switch (config.confConnWLM(true,mobilePara)) 
				{
				case NDK_OK:
					socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
					gui.cls_show_msg1(2, "无线网络配置成功!");
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1_record(TAG, "systest7", g_keeptime,"line %d:网络未接通！！！",Tools.getLineInfo());
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				wire_press();
				break;
				
			case '2':
				wireAbility();
				break;
				
			case '3':
				wirelessAbnormal();
				break;	

			case ESC:
				mobileUtil.setMobileData(myactivity, mobilestate);
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
//	private void createWlmResetPacket(PacketBean packet,byte[] buf)
//	{
//		packet.setHeader(buf);
//		packet.setLen(PACKMAXLEN);
//		packet.setOrig_len(PACKMAXLEN);
//		packet.setLifecycle(0);
//		packet.setForever(true);
//		packet.setIsLenRec(false);
//		packet.setIsDataRnd(true);
//	}
	
	//无线压力listview
	private void wire_press() 
	{
	
		int dialType =-1;
//	
//		if(GlobalVariable.gSequencePressFlag){
//			while(true){
//				if(++dialType == 2){
//					break;
//				}else{
//					gui.cls_show_msg1_record(TAG, "wire_press",g_keeptime, dialType==1?"普通型测试开始":"增强型测试开始");
//					wire_press_choose(dialType);
//				}
//			}
//		}else
//		{
//			int ret = gui.ShowMessageBox("是:普通型 ;否:增强型".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME);
//			dialType = ret==ENTER?1:0;
			//去掉增强和普通，2者一致 zhangxinj 2019/2/20 
			wire_press_choose(dialType);
//		}	
		
		
		
	}
	public void wire_press_choose(int dialType){
		wireCommPre();// modify by zhengxq	去除无线测试的短连接和拨号测试
//		int returnValue = 47;
//		while(true)
//		{
//			if(GlobalVariable.gSequencePressFlag)// 连续压力
//			{
//				if(++returnValue == '2')
//				{
//					gui.cls_show_msg1_record(TAG, "wire_press",g_keeptime, "无线压力测试结束");
//					return;
//				}
//				if(gui.cls_show_msg1(3, "即将进行连续压力测试,[取消]退出测试")==ESC)
//					return;
//			}
//			else
//			{
//				returnValue=gui.cls_show_msg("无线压力测试\n0.长连接压力\n1.短链接压力");
//			}
//			switch (returnValue) 
//			{
//			
////			case '0':
////				dialPre(dialType);
////				break;
//				
//			case '0':
//				wireCommPre();
//				break;
//				
//			case '1':
//				wireDialCommPress(dialType);
//				break;
//				
//			case ESC:
//				return;
//			}
//		}
	}
	// add by 20150327
	//拨号压力
	private void dialPre(int dialType) 
	{
		/*private & local definition*/
		int cnt;
		int i = 0,succ = 0;
		
		/*process body*/
		if(GlobalVariable.gSequencePressFlag) // 连续压力测试,设置默认压力次数
			cnt = getCycleValue();
		else
		{
			final PacketBean packet = new PacketBean();
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUTN_VLE));
			cnt = packet.getLifecycle();
		}
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "无线拨号压力中...\n还剩%d次（已成功%d次）,[取消]退出测试", cnt,succ)==ESC)
				break;
			cnt--;
			i++;
			if(layerBase.netUp(mobilePara, mobilePara.getType())!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "dialPre", g_keeptime, "line %d:第%d次无线拔号失败", Tools.getLineInfo(),i);
				continue;
			}
			if(layerBase.netDown(socketUtil,mobilePara,mobilePara.getSock_t(), mobilePara.getType())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "dialPre", g_keeptime, "line %d:第%d次无线挂断失败", Tools.getLineInfo(),i);
				continue;
			}
			succ++;
			int time = (int)(Math.random()*35)+5;
			if(dialType == 1)// 正常拨号时才要等待
			{
				gui.cls_show_msg1(time, "模块休息中,按任意键不休息继续压力...");
			}
		}
		layerBase.netDown(socketUtil,mobilePara,mobilePara.getSock_t(), mobilePara.getType());
		gui.cls_show_msg1_record(TAG, "dialPre",g_time_0, "无线拨号压力测试完成,已执行次数为%d,成功%d次",i, succ);
	}
	
	// add by 20150330
	// 数据通讯
	private void wireCommPre() 
	{
		/*private & local definition*/
		int i = 0,succ = 0;
		PacketBean sendPacket = new PacketBean();
		int[] compare = {0,-1};
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		int slen = 0,rlen = 0;
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		
		while(true)
		{
			if(layerBase.netUp(mobilePara, type)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "wireCommPre",g_keeptime, "line %d:NetUp失败", Tools.getLineInfo());
				layerBase.netDown(socketUtil, mobilePara, sock_t, type);
				gui.cls_show_msg1_record(TAG, "wireCommPara",g_keeptime, "长链接通讯压力测试完成,执行次数为%d,成功%d次", i, succ);
				return;
			}
			while(true)
			{
				if(gui.cls_show_msg1(3, "开始第%d次长链接通讯(已成功%d次),[取消]退出测试...", i+1,succ)==ESC)
					return;

				if(update_snd_packet(sendPacket, type)!=NDK_OK)
					break;
				i++;
				compare[0]++;
				
				if(layerBase.transUp(socketUtil,sock_t)!=SUCC)
				{
					gui.cls_show_msg1_record(TAG, "wireCommPre", g_keeptime, "line %d:第%d次TransUp失败", Tools.getLineInfo(),i);
					if(Tools.IsContinuous(compare))
					{
						layerBase.netDown(socketUtil, mobilePara, sock_t, type);
						break;
					}
					else
						continue;
				}
				// 收发数据
				if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara))!= sendPacket.getLen())
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG, "wireCommPre", g_keeptime, "line %d:第%d次发送数据失败(实际%d, 预期%d)", Tools.getLineInfo(),i,slen,sendPacket.getLen());
					if(Tools.IsContinuous(compare))
					{
						layerBase.netDown(socketUtil, mobilePara, sock_t, type);
						break;
					}
					else
						continue;
				}
				
				Arrays.fill(rbuf, (byte) 0);
				if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara))!= sendPacket.getLen())
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG, "wireCommPre", g_keeptime, "line %d:第%d次接收数据失败(实际%d, 预期%d)", Tools.getLineInfo(),i,rlen,sendPacket.getLen());
					if(Tools.IsContinuous(compare))
					{
						layerBase.netDown(socketUtil, mobilePara, sock_t, type);
						break;
					}
					else
						continue;
				}
				if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
				{
					layerBase.transDown(socketUtil, sock_t);
					gui.cls_show_msg1_record(TAG, "wireCommPre", g_keeptime, "line %d:第%d次校验失败", Tools.getLineInfo(),i);
					if(Tools.IsContinuous(compare))
					{
						layerBase.netDown(socketUtil, mobilePara, sock_t, type);
						break;
					}
					else
						continue;
				}
				
				// 传输层挂断
				if(layerBase.transDown(socketUtil, sock_t)!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "wireCommPre", g_keeptime, "line %d:第%d次TransDown失败", Tools.getLineInfo(),i);
					if(Tools.IsContinuous(compare))
					{
						layerBase.netDown(socketUtil, mobilePara, sock_t, type);
						break;
					}
					else
						continue;
				}
				succ++;
			}
			break;
		}
		layerBase.netDown(socketUtil, mobilePara, sock_t, type);
		gui.cls_show_msg1_record(TAG, "wireCommPre",g_time_0, "无线长链接通讯压力测试完成,执行次数为%d,成功%d次", i,succ);
	}
	
	// 短链接压力测试
	private void wireDialCommPress(int dialType) 
	{
		/*private & local definition*/
		int i =0,succ = 0;
		PacketBean sendPacket = new PacketBean();
		byte[] buf = new byte[PACKMAXLEN];
		LinkType type = mobilePara.getType();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		
		while(true)
		{
//			setWireType(mobilePara);
			if(gui.cls_show_msg1(3, "开始第%d次短链接通讯(已成功%d次),[取消]退出测试...当前信号量:%d", i+1, succ, mobilePara.getSignStrength())==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!=SUCC)
				break;
			i++;
			if(wireDialComm(socketUtil, mobilePara, sendPacket, RESET_NO)==SUCC)
				succ++;
			else
				continue;
			int time = (int)(Math.random()*35)+5;
			if(dialType == 1)
			{
				gui.cls_show_msg1(time, "模块休息中,按任意键不休息继续压力...");
			}
		}
		gui.cls_show_msg1_record(TAG, "wireDialCommPress",g_time_0, "无线短链接通讯压力测试完成,执行次数为%d,成功%d次", i,succ);
	}
	
//	// add by 20150330
//	// 无线复位测试
//	private void wireReset()
//	{
//		/*private & local definition*/
//		PacketBean sendPacket = new PacketBean();
//		byte[] buf = new byte[PACKMAXLEN];
//		int rstFlag = RESET_NO;
//		
//		/*process body*/
//		if((rstFlag = select_rst_flag())== RESET_NO)
//			return;
//		createPacket(sendPacket, buf);
//		update_snd_packet(sendPacket, mobilePara.getType());
//		try 
//		{
//			wireDialComm(socketUtil, mobilePara, sendPacket, rstFlag);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		gui.cls_show_msg1(2, "无线复位测试失败");
//	}
//	// end by 20150330
	
	// add by 20150330
	// ppp->AT
	private void wirePPPAT() 
	{
		/*private & local definition*/
//		int muxSupport;
		LinkType type = mobilePara.getType();
		
		/*process body*/
		if(layerBase.netUp(mobilePara, type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wirePPPAT", g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(2, "已处于数通态,尝试切换到AT命令态中,请耐心等待...");
		if(((type == LinkType.GPRS||type == LinkType.CDMA)&&mobilePara.getSignStrength()<SQ_2G_MAX&&mobilePara.getSignStrength()>SQ_2G_MIN)&&
				((type == LinkType.TD)&&mobilePara.getSignStrength()<SQ_3G_MAX&&mobilePara.getSignStrength()>SQ_3G_MIN))
		{
			gui.cls_show_msg1_record(TAG, "wirePPPAT", g_keeptime,"line %d:多路复用测试失败", Tools.getLineInfo());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
			return;
		}
		
		if(layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wirePPPAT", g_keeptime,"line %d:NetDown失败", Tools.getLineInfo());
			return;
		}
		if(layerBase.netUp(mobilePara, type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wirePPPAT", g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		if(layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wirePPPAT", g_keeptime,"line %d:NetDown失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG,"wirePPPAT",g_time_0, "PPP->AT测试成功");
	}
	// end by 20150330
	
//	// add by 20160815
//	// 休眠测试
//	private void wireSleep() 
//	{
//		/*private & local definition*/
//		int i = 2;
//		byte[] buf = new byte[PACKMAXLEN];
//		int slen = 0;
//		PacketBean sendPacket = new PacketBean();
//		LinkType type = mobilePara.getType();
//		int sleepFlag = 0;
//		SettingsManager settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
//		
//		/*process body*/
//		init_snd_packet(sendPacket, buf);
//		sendPacket.setForever(false);
//		sendPacket.setLen(1000);
//		byte[] tmp = new byte[sendPacket.getLen()];
//		for (int j = 0; j < sendPacket.getLen(); j++) 
//		{
//			tmp[j] = (byte) (Math.random()*256);
//		}
//		sendPacket.setHeader(tmp);
//		
//		sleepFlag = gui.cls_show_msg("无线休眠异常测试\n0.深休眠\n1.浅休眠");
//		
//		// 深休眠接口只有3G机器支持
//		switch (GlobalVariable.currentPlatform) 
//		{
//		case N900_3G:
//			if(sleepFlag=='0')
//				settingsManager.setDeepSleepEnabled(true);
//			else
//				settingsManager.setDeepSleepEnabled(false);
//			break;
//
//		case N900_4G:
//		case N910:
//		case IM81_New:
//		case IM81_Old:
//			break;
//		}
//		
//		while(i>=0)
//		{
//			if(layerBase.netUp( mobilePara, type)!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG, "wireSleep", g_keeptime,"line %d:NetUp失败", Tools.getLineInfo());
//				return;
//			}
//			if(layerBase.transUp(socketUtil, mobilePara.getSock_t())!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG, "wireSleep", 2,"line %d:TransUp(%d)", Tools.getLineInfo(),mobilePara.getSock_t());
//				layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
//				return;
//			}
//			if(i==2)
//			{
//				gui.cls_show_msg1(1, "网络层已建立！确保未有任何应用跟K21通信.电源键进入休眠,若是深休眠,请休眠4分钟后唤醒,若是浅休眠,请休眠2分钟后唤醒");
//			}
//			else 
//			{
//				settingsManager.setScreenTimeout(ONE_MIN);
//				gui.cls_show_msg1(1, "网络层已建立！等待POS自动进入休眠,若是深休眠,请休眠4分钟后唤醒,若是浅休眠,请休眠2分钟后唤醒");
//			}
//			gui.cls_show_msg("已退出休眠,完成点击任意键");
//			// 深休眠
//			if(sleepFlag == 0)
//			{
//				// 收发数据
//				if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), g_keeptime, mobilePara))!= 0)
//				{
//					layerBase.transDown(socketUtil, mobilePara.getSock_t());
//					gui.cls_show_msg1_record(TAG, "wireSleep", 2,"line %d:数据发送失败,预期%d,实际%d", Tools.getLineInfo(),0,slen);
//					layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
//					return;
//				}
//				if(layerBase.transDown(socketUtil, mobilePara.getSock_t())!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG, "wireSleep", 2,"line %d:transDown失败", Tools.getLineInfo());
//					return;
//				}
//				if(layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG, "wireSleep", 2,"line %d:netDown失败", Tools.getLineInfo());
//					return;
//				}
//			}
//			// 浅休眠
//			else if(sleepFlag == 1)
//			{
//				send_recv_press(socketUtil, type, mobilePara);
//				if(layerBase.transDown(socketUtil, mobilePara.getSock_t())!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG, "wireSleep", 2,"line %d:transDown失败", Tools.getLineInfo());
//					return;
//				}
//				if(layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG, "wireSleep", 2,"line %d:netDown失败", Tools.getLineInfo());
//					return;
//				}
//			}
//			i--;
//		}
//		
//		gui.cls_show_msg1_record(TAG, "wireSleep", g_time_0, "无线休眠测试成功");
//	}
//	// end by 20160815
	
//	// add by 20150330
//	// 协议挂断测试
//	public void tcpAbnormal() throws Exception
//	{
//		/*private & local definition*/
//		byte[] buf = new byte[PACKMAXLEN];
//		int j = 0,timeout = 1;
//		int slen = 0;
//		LinkType type = mobilePara.getType();
//		
//		// 只能进行协议挂断
//		for (j = 0; j < buf.length; j++) 
//			buf[j] = (byte) (Math.random()*256);
//		
//		if(layerBase.netUp(socketUtil, mobilePara, type)!=NDK_OK)
//		{
//			new Gui().cls_show_msg("line %d:NetUp失败", getLineInfo());
//			return;
//		}
//		
//		if(layerBase.transUp(socketUtil, mo))
//			
//	}
//	// add by 20150330
	
//	private void createWlmAbilityPacket(PacketBean packet,byte[] buf)
//	{
//		packet.setHeader(buf);
//		packet.setLen(1024*3);
//		packet.setOrig_len(1024*3);
//		packet.setLifecycle(PACKETLIFE);
//		packet.setForever(false);
//		packet.setIsLenRec(false);
//		packet.setIsDataRnd(true);
//		
//	}
	private void wire_ability_send(LinkType type){
		int i;
		float writeTime = 0;
		long startTime = 0;
		int startLen=mobilePara.getType()==LinkType.GPRS? 2*1024*1024:mobilePara.getType()==LinkType.CDMA? 10*1024*1024:20*1024*1024;
		int slen=0;
		byte[] buf = new byte[8*1024];
		//配置单向发送IP和端口号
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().configSendRecv(myactivity, mobilePara);
			}
		});
		synchronized (mobilePara) {
			try {
				mobilePara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return;
		int time = startLen / buf.length;//1280次
		//绑定服务器IP和端口
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(), mobilePara.getServerPort());
		if(layerBase.netUp(mobilePara, type)!=NDK_OK)
		{
			layerBase.linkDown(mobilePara, type);
			gui.cls_show_msg1_record(TAG, "wire_ability_send", g_keeptime,"line %d:单向发送NetUp失败", Tools.getLineInfo());
			return;
		}
		if(layerBase.transUp(socketUtil, mobilePara.getSock_t())!=NDK_OK)
		{
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
			gui.cls_show_msg1_record(TAG, "wire_ability_send", g_keeptime,"line %d:单向发送TransUp失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(2, "正在进行单向发送性能测试...%dM数据测试时间较长,请耐心等待",startLen/1024/1024);
		// 性能测试过程中不允许退出
		startTime = System.currentTimeMillis();
		for (int j = 0; j < time; j++) {
			// 初始化发送数据
			byte[] ptr = new byte[buf.length];
			for (i = 0; i < buf.length; i++) {
				ptr[i] = (byte) (Math.random() * 128);
			}
			// 收发数据
			if ((slen = sockSend(socketUtil, ptr, buf.length, SO_TIMEO,mobilePara)) != buf.length) {
				layerBase.transDown(socketUtil, mobilePara.getSock_t());
				gui.cls_show_msg1_record(TAG, "wire_ability_send", g_keeptime,"line %d:发送数据失败（实际len=%d,预期len=%d）",Tools.getLineInfo(), slen, buf.length);
				return;
			}
		}
		writeTime=writeTime+Tools.getStopTime(startTime);
		float sendRate=startLen/writeTime/1024;
		if(layerBase.transDown(socketUtil, mobilePara.getSock_t())!=NDK_OK)
		{
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
			gui.cls_show_msg1_record(TAG, "wire_ability_send", g_keeptime,"line %d单向发送TransDown失败", Tools.getLineInfo());
			return;
		}
		if(layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "wire_ability_send", g_keeptime,"line %d:NetDown失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "wire_ability_send", g_time_0,"%dM发送数率:%fKB/s",startLen/1024/1024, sendRate);
	}
	private void wire_ability_recv(LinkType type){
		int j;
		long startTime;
		float readTime = 0;
		int startLen=mobilePara.getType()==LinkType.GPRS? 2*1024*1024:mobilePara.getType()==LinkType.CDMA? 10*1024*1024:50*1024*1024;
		int rlen=0;
		float recvRate=0;
	
		//配置单向发送IP和端口号
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().configSendRecv(myactivity, mobilePara);
			}
		});
		synchronized (mobilePara) {
			try {
				mobilePara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return;
		//单向接收数据的时候,第一次发送给服务器的数据
		byte[] buf = new byte[4*1024];
		byte[] rbuf = new byte[4*1024];
		for(j=0;j<buf.length;j++){
			buf[j] = (byte) (Math.random()*128);
		}
		//绑定服务器IP和端口
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		
		if(layerBase.netUp(mobilePara, type)!=NDK_OK)
		{
			layerBase.linkDown(mobilePara, type);
			gui.cls_show_msg1_record(TAG, "wire_ability_recv", g_keeptime,"line %d:单向发送NetUp失败", Tools.getLineInfo());
			return;
		}
		
		if (layerBase.transUp(socketUtil, mobilePara.getSock_t()) != NDK_OK) {
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(),type);
			gui.cls_show_msg1_record(TAG, "wire_ability_recv", g_keeptime,"line %d:单向接收TransUp失败", Tools.getLineInfo());
			return;
		}
		// 开始通讯
		// 先发送数据到服务器,服务器会回数据回来
		sockSend(socketUtil, buf, buf.length, SO_TIMEO, mobilePara);

		gui.cls_show_msg1(1, "正在进行单向接收接收性能测试...测试%dM数据时间较长,请耐心等待",startLen/1024/1024);
		int onceTime = startLen / rbuf.length;
		int onceLeft = startLen % rbuf.length;
		Arrays.fill(rbuf, (byte) 0);
		gui.cls_show_msg1(1, "数据接收中...\n%s:%d<-%s:%d", mobilePara.getLocalIp(),mobilePara.getLocalPort(), mobilePara.getServerIp(),mobilePara.getServerPort());
		startTime = System.currentTimeMillis();
		for (j = 0; j < onceTime; j++) {

			// 接收
			if ((rlen = sockRecv(socketUtil, rbuf, rbuf.length, SO_TIMEO,mobilePara)) != rbuf.length) {
				layerBase.netDown(socketUtil, mobilePara,mobilePara.getSock_t(), type);
				gui.cls_show_msg1_record(TAG, "wire_ability_recv", g_keeptime,"line %d:接收数据失败（实际len = %d,预期len = %d）",Tools.getLineInfo(), rlen, startLen);
				return;
			}

		}

		if (onceLeft > 0) {
			byte[] left = new byte[onceLeft];
			sockRecv(socketUtil, left, left.length, SO_TIMEO, mobilePara);
		}
		readTime = readTime + Tools.getStopTime(startTime);
		recvRate = startLen / readTime / 1024;
		if (layerBase.transDown(socketUtil, mobilePara.getSock_t()) != NDK_OK) {
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(),type);
			gui.cls_show_msg1_record(TAG, "wire_ability_recv", g_keeptime,"line %d:单向接收TransDown失败", Tools.getLineInfo());
			return;
		}
		if (layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(),type) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "wire_ability_recv", g_keeptime,"line %d:NetDown失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "wire_ability_recv", g_time_0,"%dM接收速率:%fKB/s\n", startLen/1024/1024,recvRate);
	}
	private void wireAbility() 
	{
		if(gui.cls_show_msg("如有操作4G切换到3G等切换网络操作,请重新进入本用例再测试性能,[确认]继续,[取消]退出")==ESC)
			return;
		//获取WLM type
		LinkType type = mobilePara.getType();
		
		//无线普通性能,赵明权反馈8K的收发数据无法测试出真正的无线性能,建议将收发数据修改为20M,发送和接收性能值采用单向测试
		int nKeyIn = gui.cls_show_msg("WLM性能测试\n0.发送速率\n1.接收速率");
		switch (nKeyIn) 
		{
			case '0':
				wire_ability_send(type);
				break;
				
			case '1':
				wire_ability_recv(type);
				break;
				
			case ESC:
				return;
		}
		
		
	}
	
	// end by 20150331
	
//	// add by 20160720
//	// 无线预连接性能
//	private void wirelessFunction2() 
//	{
//		/*private & local definition*/
//		int ret = 0;
//		long startTime;
//		float inittimes = 0.0f;
//		LinkType type = mobilePara.getType();
//		MobileUtil mobileUtil = MobileUtil.getInstance(myactivity);
//		
//		/*process body*/
//		
//		if (gui.ShowMessageBox("是否为机器上电后首次运行该用例".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK) 
//		{
//			gui.cls_show_msg1(2, "请重新开机后直接运行本用例");
//			return;
//		}
//		mobileUtil.closeNet();
//		mobileUtil.closeOther();
//		startTime = System.currentTimeMillis();
//		// 连接无线网络
//		if ((ret = layerBase.linkUP(mobilePara, type)) != NDK_OK) {
//			gui.cls_show_msg1_record(TAG, "wirelessFunction2", g_keeptime,"line %d:打开无线失败%s", Tools.getLineInfo(), ret);
//			return;
//		}
//		inittimes = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
//		gui.cls_show_msg1_record(TAG, "wirelessFunction2", g_time_0,"（首次开机）预连接耗时%fs\n",inittimes);
//	}
//	// end by 20160720
	
	//无线异常
	private void wirelessAbnormal() 
	{
		while(true)
		{
			
			int returnValue=gui.cls_show_msg("wire异常测试\n0.PPP->AT");
			switch (returnValue) 
			{
			case '0':
				wirePPPAT();
				break;
			/*case 1:
				wireSleep();
				break;*/
			case ESC:
				return;
			}
		}
	}
	
//	// 连接测试
//	private void keepLineTest() throws Exception
//	{
//		/*private & local definition*/
//		
//		/*process body*/
//		int returnValue=gui.cls_show_msg("链接测试\n0.维持长链接\n1.短链接");
//		switch (returnValue) 
//		{
//		case '0':
//			// 长链接
//			inKeepTest();
//			break;
//			
//		case '1':
//			// 短链接
//			upKeepTest();
//			break;
//			
//		default:
//			break;
//		}
//	}
	
//	// 长链接
//	private void inKeepTest() throws Exception
//	{
//		/*private & local definition*/
//		int timeout = SO_TIMEO,i = 0;
//		byte[] buf = new byte[100];
//		byte[] rbuf = new byte[100];
//		int slen = 0,rlen = 0;
//		int ret;
//		long startTime;
//		LinkType type = mobilePara.getType();
//		Sock_t sock_t = mobilePara.getSock_t();
//		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(), mobilePara.getServerPort());
//		
//		
//		/*process body*/
//		startTime = System.currentTimeMillis();
//		for (i = 0; i < buf.length; i++) 
//			buf[i] = (byte) (Math.random()*256);
//		
//		// 建立连接
//		if((ret = layerBase.netUp( mobilePara, type))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime,"line %d:无线初始化失败%d", Tools.getLineInfo(),ret);
//			return;
//		}
//		if(layerBase.transUp(socketUtil, sock_t)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		// 收发数据
//		if((slen = sockSend(socketUtil, buf, buf.length, timeout, mobilePara))!=buf.length)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:发送数据失败(实际%d, 预期%d)", slen,buf.length);
//			return;
//		}
//		Arrays.fill(rbuf, (byte) 0);
//		if((rlen = sockRecv(socketUtil, rbuf, buf.length, timeout, mobilePara))!=buf.length)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:接收数据失败(实际%d, 预期%d)", Tools.getLineInfo(),rlen,buf.length);
//			return;
//		}
//		if(!Tools.memcmp(buf, rbuf, rlen))
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:校验失败", Tools.getLineInfo());
//			return;
//		}
//		if(layerBase.transDown(socketUtil, sock_t)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		gui.cls_show_msg1(2, "请等待60秒");
//		while(true)
//		{
//			if(Tools.getStopTime(startTime)>60)
//				break;
//			// 显示剩余时间
//			gui.cls_show_msg1(2, "还剩"+(60-Tools.getStopTime(startTime))+"秒");
//		}
//		
//		if(layerBase.transUp(socketUtil, sock_t)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		if((slen = sockSend(socketUtil, buf, buf.length, timeout, mobilePara))!=buf.length)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:发送数据失败(实际%d, 预期%d)", Tools.getLineInfo(),slen,buf.length);
//			return;
//		}
//		Arrays.fill(rbuf, (byte) 0);
//		if((rlen = sockRecv(socketUtil, rbuf, buf.length, timeout, mobilePara)) != buf.length)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:接收数据失败(实际%d, 预期%d)", Tools.getLineInfo(),rlen,buf.length);
//			return;
//		}
//		if(!Tools.memcmp(buf, rbuf, rlen))
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:校验失败", Tools.getLineInfo());
//			return;
//		}
//		if(layerBase.transDown(socketUtil, sock_t)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "inKeepTest", g_keeptime, "line %d:TransDown失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//		gui.cls_show_msg1_record(TAG, "inKeepTest",g_keeptime, "长链接维持时间测试通过");
//	}
	
//	// 短链接
//	private void upKeepTest() 
//	{
//		/*private & local definition*/
//		int timeout = SO_TIMEO,i =0;
//		byte[] buf = new byte[100];
//		byte[] rbuf = new byte[100];
//		int slen,rlen,ret = -1;
//		long startTime;
//		LinkType type = mobilePara.getType();
//		Sock_t sock_t = mobilePara.getSock_t();
//		
//		/*process body*/
//		startTime = System.currentTimeMillis();
//		for (i = 0; i < buf.length; i++) 
//			buf[i] = (byte) (Math.random()*256);
//		
//		// 建立网络
//		if(layerBase.netUp( mobilePara, type)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "upKeepTest", 2,"line %d:NetUp失败", Tools.getLineInfo());
//			return;
//		}
//		if(layerBase.transUp(socketUtil, sock_t)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		
//		// 收发数据
//		if((slen = sockSend(socketUtil, buf, buf.length, timeout, mobilePara)) != buf.length)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:发送数据失败(实际%d, 预期%d)", Tools.getLineInfo(),slen,buf.length);
//			return;
//		}
//		
//		Arrays.fill(rbuf, (byte) 0);
//		if((rlen = sockRecv(socketUtil, rbuf, buf.length, timeout, mobilePara))!=buf.length)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:接收数据失败(实际%d, 预期%d)", Tools.getLineInfo(),rlen,buf.length);
//			return;
//		}
//		if(!Tools.memcmp(buf, rbuf, rlen))
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:校验失败", Tools.getLineInfo());
//			return;
//		}
//		if(layerBase.transDown(socketUtil, sock_t)!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		gui.cls_show_msg1(2, "请等待60秒");
//		while(true)
//		{
//			if(Tools.getStopTime(startTime)>60)
//				break;
//			// 显示时间
//			gui.cls_show_msg1(2, "还剩"+(60-Tools.getStopTime(startTime)+"秒"));
//		}
//		// 检测网络状态
//		MobileUtil.getInstance(getActivity()).closeNet();
//		startTime = System.currentTimeMillis();
//		if((ret = wireDetect(getActivity(),startTime, timeout))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "upKeepTest", 2, "line %d:拨号失败%d", Tools.getLineInfo(),ret);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		
//		if(layerBase.transUp(socketUtil, sock_t)==NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			return;
//		}
//		if((slen = sockSend(socketUtil, buf, buf.length, timeout, mobilePara))>0)
//		{
//			layerBase.transDown(socketUtil, sock_t);
//			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//			gui.cls_show_msg1_record(TAG, "upKeepTest", g_keeptime, "line %d:发送数据失败(实际%d)", Tools.getLineInfo(),slen);
//			return;
//		}
//		layerBase.transDown(socketUtil, sock_t);
//		layerBase.netDown(socketUtil, mobilePara, sock_t, type);
//		gui.cls_show_msg1(2, "短连接自动挂断测试通过");
//	}
	PhoneStateListener phoneStateListener = new PhoneStateListener()
	{
		public void onSignalStrengthsChanged(SignalStrength signalStrength) 
		{
			mobilePara.setSignStrength(signalStrength.getGsmSignalStrength());
		};
	};

}
