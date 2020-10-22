package com.example.highplattest.systest;

import java.util.Arrays;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;
import android.newland.content.NlContext;
import android.util.Log;
/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest10.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150205
 * directory 		: 
 * description 		: RS232、PinPad串口压力、性能
 * related document :
 * history 		 	: 变更记录									变更人员			变更时间
 *			  		 修改RS232串口波特率轮询操作，新增串口关闭。
 *					防止打开次数过多导致崩溃黑屏		   				陈丁	 			20200426
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest10 extends DefaultFragment implements NDK
{
	/*---------------constants/macro definition---------------------*/
	public final String TAG = SysTest10.class.getSimpleName();
	public final int MAX_SIZE = 1024;
	public final int MAXWAITTIME = 30;
	public final int DEFAULT_COUNT = 100;
	
	/*------------global variables definition-----------------------*/
	private NLUART3Manager uart3Manager=null;
	PacketBean packetTemp = new PacketBean();
	int time = 0, count = 0, ret = 0, cnt = 0, ret1 = 0;
	String[] para={"8N1NN","8N1NB"};
	enum PortType{RS232, PinPad};
	PortType comValue = PortType.RS232;
	private final String TESTITEM = "物理串口压力、性能";
	private Gui gui = null;
	private Config config;
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	private final int RS232_NEW_PORT = 62;
	
	public void systest10() 
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("串口综合测试\n0.串口配置\n1.波特率轮回\n2.POS<->PC\n3.POS<--PC\n4.自发自收\n");
			switch (returnValue) 
			{
			
			case '0':// 波特率配置
				int nKeyIn = -1;
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad))
				{
					nKeyIn = gui.cls_show_msg("串口选择\n0.RS232\n1.PinPad\n");
					isNewRs232 = nKeyIn=='0'?false:true;
				}
				else
					nKeyIn = gui.cls_show_msg("串口选择\n0.RS232\n");
				if(nKeyIn=='0'||nKeyIn=='1')
				{
					comValue = PortType.values()[nKeyIn-'0'];
				}
				else
				{
					gui.cls_show_msg("输入超出范围");
					break;
				}
				config.confLinkR(LinkType.SERIAL, null);
				break;
				
			case '1':
				autoBpsinit();
				break;
				
			case '2':
				comPcPos();
				break;
				
			case '3':
				comPcToPos();
				break;
				
			case '4':
				autoCommSelf();
				break;
				
			case ESC:
				intentSys();
				return;

			default:
				break;
			}
		}
	}
	
	// add by 20150408
	// 波特率的轮询操作
	public void autoBpsinit() 
	{
		/*private & local definition*/
		int i = 0,ret = -1;
		int[] bps = {300,1200,2400,4800,9600,19200,57600,115200,230400};
		
		/*process body*/
		while(true)
		{
			if(gui.cls_show_msg1(2, "%s串口波特率轮回中(%s)...[取消]键退出测试", comValue,bps[i])==ESC)
				break;
			
			switch (comValue) 
			{
				
			case RS232:
			case PinPad:
				ret = isNewRs232==true?uart3Manager.open(RS232_NEW_PORT):uart3Manager.open();
				if(ret ==-1)
				{
					gui.cls_show_msg1_record(TAG, "autoBpsinit",g_keeptime, "line %d:打开串口(%s, %d)失败(ret=%d)", Tools.getLineInfo(),comValue,bps[i],ret);
					uart3Manager.close();
					continue;
				}
				if((ret = uart3Manager.setconfig(bps[i], 0, para[0].getBytes()))!=NDK_OK)
				{
					uart3Manager.close();
					gui.cls_show_msg1_record(TAG, "autoBpsinit", g_keeptime,"line %d:初始化串口(%s, %d)失败(ret=%d)", Tools.getLineInfo(),comValue,bps[i],ret);
					continue;
				}
				
				i++;
				//增加关闭操作,防止打开次数过多导致崩溃黑屏
				if((ret = uart3Manager.close())!=ANDROID_OK)
				{
					gui.cls_show_msg1_record(TAG,"autoBpsinit",g_keeptime,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				if(i == bps.length)
					i = 0;
				break;

			default:
				break;
			}
		}
		uart3Manager.close();// 关闭串口操作
	}
	// end by 20150408
	
	// add by 20150408
	// PC->POS
	public void comPcToPos() 
	{
		/*private & local definition*/
		int ret = -1,datalen = 0;
		long dataSum = 0;
		byte[] recvBuf = new byte[BUFSIZE_SERIAL];
		byte chkSum = 0x00;
		
		/*process body*/
		gui.cls_show_msg("测试前先将%s串口跟PC端连接,利用AccessPort工具发送和查看串口数据，完成任意键继续",comValue);
		uart3Manager.close();
		ret = isNewRs232==true?uart3Manager.open(RS232_NEW_PORT):uart3Manager.open();
		if (ret == -1) 
		{
			gui.cls_show_msg1_record(TAG, "comPcToPos",g_keeptime,"line %d:打开(%s, %d)失败(ret=%d)", Tools.getLineInfo(),
					comValue, BpsBean.bpsValue, ret);
			return;
		}
		if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,para[0].getBytes())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "comPcToPos",g_keeptime,"line %d:初始化(%s, %d)失败(ret=%d)", Tools.getLineInfo(),
					comValue, BpsBean.bpsValue, ret);
			return;
		}
		gui.cls_show_msg("从Accessport的发送区发送任意数据并开启自动发送,发送1分钟后停止发送,完成任意键继续");
		
		do {
			if(gui.cls_show_msg1(3, "%s POS<---PC\n已收到数据长度:%d，校验码:%x，退出键可中断接收...",comValue, dataSum,chkSum)==ESC)
				break;
			if ((datalen = uart3Manager.read(recvBuf, recvBuf.length,MAXWAITTIME)) >= 0) {
				Log.e(TAG, Arrays.toString(recvBuf));
				dataSum = dataSum + datalen;
				chkSum = Tools.makeLrc(recvBuf, datalen);
			}
		} while (datalen >= 0);
		gui.cls_show_msg1_record(TAG, "comPcToPos",g_time_0,"RS232 POS<---PC\n共收到数据长度:%d,校验码:%x\n",dataSum,chkSum);
	}
	// end by 20150408
	
	// add by 20150408
	// PC<->POS
	public void comPcPos() 
	{
		/*private & local definition*/
		int ret = -1,i = 0,succ = 0;
		byte[] sendBuf = new byte[BUFSIZE_SERIAL];
//		byte[] recvBuf = new byte[BUFSIZE_SERIAL];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/	
		// 测试前置
		gui.cls_show_msg("测试前先将%s串口跟PC端连接,利用AccessPort工具发送和查看串口数据,完成任意键继续",comValue);
		uart3Manager.close();
		ret = isNewRs232==true?uart3Manager.open(RS232_NEW_PORT):uart3Manager.open();
		if (ret == -1) 
		{
			gui.cls_show_msg1_record(TAG, "comPcToPos",g_keeptime,"line %d:打开(%s, %d)失败(ret=%d)", Tools.getLineInfo(),
					comValue, BpsBean.bpsValue, ret);
			return;
		}
		if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,para[0].getBytes())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "comPcToPos",g_keeptime,"line %d:初始化(%s, %d)失败(ret=%d)", Tools.getLineInfo(),
					comValue, BpsBean.bpsValue, ret);
			return;
		}
		init_snd_packet(sendPacket, sendBuf);
		set_snd_packet(sendPacket, LinkType.SERIAL);
		byte[] recvBuf = new byte[sendPacket.getLen()];
		while (true) {
			gui.cls_show_msg1(2,"开始第%d次PC<->POS通讯(已成功%d次)",  i + 1, succ);
			gui.cls_show_msg("清空AccessPort接收区的数据，任意键继续");
			if (update_snd_packet(sendPacket, LinkType.SERIAL) != NDK_OK)
				break;
			i++;
			// 清空缓存区的操作没有，需要补充
			gui.cls_show_msg1(2, "开始第%d次PC<->POS通讯(已成功%d次)\nPOS-->PC", i, succ);
			if ((ret = uart3Manager.write(sendPacket.getHeader(),sendPacket.getHeader().length, MAXWAITTIME)) != sendPacket
					.getHeader().length) {
				gui.cls_show_msg1_record(TAG, "comPcPos", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
				continue;
			}
			// 提示信息
			gui.cls_show_msg1(2, "开始第%d次PC<->POS通讯(已成功%d次)\nPOS<--PC", i,succ);
			gui.cls_show_msg("请将AccessPort接收到的数据复制到发送缓冲区并开启自动发送,完成任意键继续");
			gui.cls_show_msg1(2, "开始第%d次PC<->POS通讯(已成功%d次)\nPOS<--PC", i,succ);
			
			Arrays.fill(recvBuf, (byte) 0);
			if ((ret = uart3Manager.read(recvBuf,sendPacket.getHeader().length, MAXWAITTIME)) != sendPacket.getHeader().length) {
				gui.cls_show_msg1_record(TAG, "comPcPos", g_keeptime,"line %d:第%d次接收错%d (实际%d, 预期%d)", Tools.getLineInfo(), i,
						ret, ret,sendPacket.getHeader().length);
				continue;
			}
			
			Log.e("send", Arrays.toString(sendPacket.getHeader()));
			Log.e("recv", Arrays.toString(recvBuf));
			gui.cls_show_msg("停止自动发送，完成任意键继续");
			if (!Tools.memcmp(sendPacket.getHeader(), recvBuf,sendPacket.getLen())) {
				gui.cls_show_msg1_record(TAG, "comPcPos", g_keeptime,"line %d:第%d次数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "comPcPos", g_time_0,"通讯压力测试完成,执行次数为%d,成功%d次", i,succ);
	}
	// end by 20150408
	
	// add by 20150408
	// 串口的自发自收
	public void autoCommSelf() 
	{
		/*private & local definition*/
		int ret = -1,i = 0,succ = 0;
		byte[] sendBuf = new byte[BUFSIZE_SERIAL];
		byte[] recvBuf = new byte[BUFSIZE_SERIAL];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/
		switch (comValue) 
		{
		case RS232:
		case PinPad:
			uart3Manager.close();
			ret = isNewRs232==true?uart3Manager.open(RS232_NEW_PORT):uart3Manager.open();
			if(ret==-1)
			{
				gui.cls_show_msg1_record(TAG, "autoCommSelf", 2,"line %d:打开(%s)失败\n错误码:%d\nBPS:%d", Tools.getLineInfo(),comValue,ret,BpsBean.bpsValue);
				return;
			}
			if(uart3Manager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "autoCommSelf", 2,"line %d:初始化(%s)失败\n错误码:%d\nBPS:%d", Tools.getLineInfo(),comValue,ret,BpsBean.bpsValue);
				return;
			}
		
			gui.cls_show_msg("短接所选%s串口的23脚，任意键继续",comValue);
			init_snd_packet(sendPacket, sendBuf);
			set_snd_packet(sendPacket, LinkType.SERIAL);
			while(true)
			{
				if(gui.cls_show_msg1(3, "正在进行(%s)第%d次通讯,已成功%d次,[取消]键退出测试", comValue,i+1,succ)==ESC)
					break;
				if(update_snd_packet(sendPacket, LinkType.SERIAL)!=NDK_OK)
					break;
				i++;
				
				// 收发数据
				if((ret = uart3Manager.write(sendPacket.getHeader(), sendPacket.getHeader().length, MAXWAITTIME))!= sendPacket.getHeader().length)
				{
					gui.cls_show_msg1_record(TAG, "autoCommSelf", g_keeptime, "line %d:第%d次发送失败(%d)", Tools.getLineInfo(),i,ret);
					continue;
				}
				Arrays.fill(recvBuf, (byte) 0);
				if((ret = uart3Manager.read(recvBuf, sendPacket.getHeader().length, MAXWAITTIME))!= sendPacket.getHeader().length)
				{
					gui.cls_show_msg1_record(TAG, "autoCommSelf", g_keeptime, "line %d:第%d次接收错%d (实际%d, 预期%d)", Tools.getLineInfo(),i,ret,ret,sendPacket.getHeader().length);
					continue;
				}
				
				Log.e("send"+sendPacket.getHeader().length, Arrays.toString(sendPacket.getHeader()));
				Log.e("recv", Arrays.toString(recvBuf));
				if(!Tools.memcmp(sendPacket.getHeader(), recvBuf, sendPacket.getHeader().length))
				{
					gui.cls_show_msg1_record(TAG, "autoCommSelf", g_keeptime, "line %d:第%d次数据校验失败", Tools.getLineInfo(),i);
					continue;
				}
				succ++;
			}
			gui.cls_show_msg1_record(TAG, "autoCommSelf", g_time_0,"串口自检完成(共进行%d次),成功了%d次", i,succ);
			break;

		default:
			break;
		}
	}
	// end by 20150408
	
	// add by 20150408
	//串口选择函数线程
	public void port_choose() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("PORT压力\n0.RS232串口读写压力\n1.PINPAD串口读写压力\n");
			switch (returnValue) 
			{
				case '0':
					rs232_press(true);
					break;
					
				case '1':
					rs232_press(false);
					break;
					
				case ESC:
					return;
			}
		}

	}
	// end by 20150408
	
	//RS232串口读写压力
	public void rs232_press(boolean isRs232) 
	{
		int fd = -1;
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
		byte[] sendBuf = new byte[MAX_SIZE];
		byte[] recvBuf = new byte[MAX_SIZE];
		Arrays.fill(sendBuf, (byte) 0);
		Arrays.fill(recvBuf, (byte) 0);
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		PacketBean packet = new PacketBean();

		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		
		fd = isRs232?uart3Manager.open():uart3Manager.open(RS232_NEW_PORT);
		if (fd == -1) 
		{
			gui.cls_show_msg1_record(TAG, "rs232_press",2,"line %d:打开%s串口失败(fd=%d)", Tools.getLineInfo(), comValue,fd);
			return;
		}
	
		gui.cls_show_msg("请短接%s串口的23脚，任意键继续",comValue);
		if((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes())) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(TAG, "rs232_press", 2,"line %d:%s串口波特率设置失败(ret=%d)",  Tools.getLineInfo(),comValue, ret);
			return;
		}
		
		time = count = packet.getLifecycle();
		while(time-- > 0)
		{
			handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "第" + (count - time) + "次数据收发测试中..", 2);
			
			if ((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME
					/ (BpsBean.bpsId + 1))) != MAX_SIZE) 
			{
				gui.cls_show_msg1_record(TAG, "rs232_press", 2,"line %d:第%d次写失败(ret=%d)", Tools.getLineInfo(), (count - time), ret);
				continue;
			}
			Log.e("write1", ret+"");
			Arrays.fill(recvBuf, (byte) 0);
			if ((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) <= 0) 
			{
				gui.cls_show_msg1_record(TAG, "rs232_press", 2,"line %d:第%d次读失败(ret=%d)",  Tools.getLineInfo(), (count - time), ret);
				continue;
			}
			Log.e("read1", ret1+"");
			if (ret1 != ret || !Arrays.equals(sendBuf, recvBuf)) 
			{
				gui.cls_show_msg1_record(TAG, "rs232_press",2,"line %d:第%d次数据读写不一致,ret=%d...", Tools.getLineInfo(), (count - time), ret);
				continue;
			}
			
			cnt++;
		}
		
		gui.cls_show_msg1_record(TAG, "rs232_press",g_time_0,"测试完成,已执行次数为:%d,成功次数:%d", count, cnt);
	}	
}
