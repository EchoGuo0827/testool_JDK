package com.example.highplattest.systest;

import java.util.Arrays;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import android.newland.NLUART3Manager;
import android.newland.NlManager;
import android.newland.content.NlContext;
import android.util.Log;

/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest45.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200313
 * directory 		: 
 * description 		: 物理串口/ETH交叉测试
 * related document :
 * history 		 	: 变更点                              变更时间		案例人员
 *			  		     串口/以太网交叉        20200313		wangkai
 *					  N850 F10   以太网打开1  关闭-1  状态未知返回0  				陈丁			  20200602
 *					F7以太网状态值修改					陈丁					20200710
 *					根据测试人员需求，修改为自动测试		20200930			陈丁
 *					 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest45 extends DefaultFragment implements NDK
{
	/*---------------constants/macro definition---------------------*/
	public final String TAG = SysTest45.class.getSimpleName();
	private final String TESTITEM = "物理串口/ETH交叉测试";
	
	/**枚举识别串口*/
	enum PortType{RS232,PinPad};
	
	/**全局变量*/
	public final int MAXWAITTIME = 30;
	private final int RS232_NEW_PORT = 62;
	/**默认设置为旧RS232*/
	PortType mCurPortType = PortType.RS232;
	
	/**全局变量*/
	EthernetPara ethernetPara = new EthernetPara();
	Gui gui = new Gui(myactivity, handler);
	
	public void systest45() 
	{
		/*局部变量*/ 
		initLayer();
		Config config = new Config(myactivity, handler);
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			int returnValue = gui.cls_show_msg("串口/ETH交叉测试\n0.串口配置\n1.ETH配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':// 波特率配置
				StringBuffer strBuffer = new StringBuffer("串口选择\n");
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232))
					strBuffer.append("0.RS232\n");
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad))
					strBuffer.append("1.PinPad\n");
				int nkeyIn = gui.cls_show_msg(strBuffer.toString())-'0';
				if(nkeyIn>=0&&nkeyIn<=1)
					mCurPortType=PortType.values()[nkeyIn];
				else// 按键值错误
				{
					gui.cls_show_msg1(2, "按键值超出范围,请重新配置");
					break;
				}
				config.confLinkR(LinkType.SERIAL, null);
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
					gui.cls_show_msg1_record(TAG, TAG, 2, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
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
	
	//交叉测试具体实现函数
	public void cross_test() throws Exception
	{
		/**局部变量*/
		NLUART3Manager uart3Manager = null;
		NlManager nlManager = null;
		String[] para = {"8N1NN", "8N1NB"};
		
		int i = 0, succ = 0, ret = 0;
		int send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbufeth = new byte[PACKMAXLEN];
		byte[] recvBuf = new byte[BUFSIZE_SERIAL];
		PacketBean sendPacket = new PacketBean();
		SocketUtil socketUtil_eth = new SocketUtil(ethernetPara.getServerIp(), ethernetPara.getServerPort());
		
		/*process body*/

		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, LinkType.SERIAL);
		//byte[] recvBuf = new byte[sendPacket.getLen()];
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime, "%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		  
		switch (mCurPortType) 
		{
		case RS232:
		case PinPad:
			uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
			//测试前置，保护动作
			uart3Manager.close();
			layerBase.netDown(socketUtil_eth, ethernetPara, ethernetPara.getSock_t(), ethernetPara.getType());

			gui.cls_show_msg("测试前先将RS232串口跟PC端连接，利用【ComTest工具】并开启自动发送和查看串口数据，完成后任意键继续");
			while (true)
			{
				//测试退出点
				if (update_snd_packet(sendPacket, LinkType.SERIAL) != NDK_OK)//测试次数完成自动退出
					break;
				if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，点击退出键退出测试", TESTITEM, i, succ) == ESC)//手动退出
					break;
				i++;
				ret = mCurPortType==PortType.PinPad?uart3Manager.open(RS232_NEW_PORT):uart3Manager.open();
				if(ret == -1)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:打开(%s, %d)失败(ret=%d)", Tools.getLineInfo(), mCurPortType, BpsBean.bpsValue, ret);
					return;
				}
				
				/*//eth
				gui.cls_show_msg1(1, "即将进行以太网的打开操作");
				netHandler.sendEmptyMessage(0);
				SystemClock.sleep(1000);
				nlEthernetManager.start();
				SystemClock.sleep(500);
				int status = -10086;
				long outtime = System.currentTimeMillis();
				int time = 0;
				Log.d("eric", "status==" + status);
				while (time < 15) 
				{
					time = (int) Tools.getStopTime(outtime);
					status = GlobalVariable.gCurPlatVer==Platform_Ver.A9?nlEthernetManager.getEthernetStatus():nlEthernetManager.getStatus();
	
					if (status == ETH_STATE_ENABLED)
						break;
					SystemClock.sleep(10);					
				}
				LoggerUtil.d("status==" + status);
				if (status != ETH_STATE_ENABLED) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(), status);
					return;
				}*/
				gui.cls_show_msg1(2, "连接以太网中……");
	
				if((ret = layerBase.netUp(ethernetPara, ethernetPara.getType())) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:NetUp失败(%d)", Tools.getLineInfo(), ret);
					continue;
				}
				if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes())) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:初始化(%s, %d)失败(ret=%d)", Tools.getLineInfo(), mCurPortType, BpsBean.bpsValue, ret);
					return;
				}
				if((ret = layerBase.transUp(socketUtil_eth, ethernetPara.getSock_t())) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:transUp失败(%d)", Tools.getLineInfo(), i, ret);
					continue;
				}
				// 清空缓存区的操作没有，需要补充
				gui.cls_show_msg1(1, "开始第%d次PC<->POS通讯(已成功%d次)\nPOS-->PC", i, succ);
				if ((ret = uart3Manager.write(sendPacket.getHeader(), sendPacket.getHeader().length, MAXWAITTIME)) != sendPacket.getHeader().length) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
					continue;
				}
				if ((send_len = sockSend(socketUtil_eth, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, ethernetPara)) != sendPacket.getLen()) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
					continue;
				}
	
				// 提示信息
//				gui.cls_show_msg1(2, "开始第%d次PC<->POS通讯(已成功%d次)\nPOS<--PC", i, succ);
//				gui.cls_show_msg("请将AccessPort接收到的数据复制到发送缓冲区并开启自动发送,完成任意键继续");
//				gui.cls_show_msg1(2, "开始第%d次PC<->POS通讯(已成功%d次)\nPOS<--PC", i, succ);
				
				Arrays.fill(recvBuf, (byte) 0);
				gui.cls_show_msg1(1, "开始第%d次PC<->POS通讯(已成功%d次)\nPc-->Pos", i, succ);
				if ((ret = uart3Manager.read(recvBuf, sendPacket.getHeader().length, MAXWAITTIME)) != sendPacket.getHeader().length) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次接收错%d (实际%d, 预期%d)", Tools.getLineInfo(), i, ret, ret, sendPacket.getHeader().length);
					continue;
				}
				Arrays.fill(rbufeth, (byte) 0);
				if ((rec_len = sockRecv(socketUtil_eth, rbufeth, sendPacket.getLen(), SO_TIMEO, ethernetPara)) != sendPacket.getLen()) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
					continue;
				}
	
				Log.e("send", Arrays.toString(sendPacket.getHeader()));
				Log.e("recv", Arrays.toString(recvBuf));
				//串口收发数据检验
				if (!Tools.memcmp(sendPacket.getHeader(), recvBuf, sendPacket.getLen())) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:第%d次数据校验失败", Tools.getLineInfo(), i);
					continue;
				}
				//以太网收发数据校验
				if (!Tools.memcmp(sendPacket.getHeader(), rbufeth, sendPacket.getLen())) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(), i);
					continue;
				}
				
				if((ret = uart3Manager.close()) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:关闭(%s, %d)失败(ret=%d)", Tools.getLineInfo(), mCurPortType, BpsBean.bpsValue, ret);
					return;
				}
				
				// 挂断
				if((ret = layerBase.transDown(socketUtil_eth, ethernetPara.getSock_t())) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:transDown失败(%d)", Tools.getLineInfo(), i, ret);
					continue;
				}
				/*
				time = 0;
				nlEthernetManager.stop();
				SystemClock.sleep(500);
				long outtime2 = System.currentTimeMillis();
				while (time < 15) 
				{
					time = (int) Tools.getStopTime(outtime2);
					status = GlobalVariable.gCurPlatVer==Platform_Ver.A9?nlEthernetManager.getEthernetStatus():nlEthernetManager.getStatus();
					if (status == ETH_STATE_DISABLED)
						break;

					SystemClock.sleep(10);					
				}
	
				if (status != ETH_STATE_DISABLED) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(), status);
					return;
				}*/
				if((ret = layerBase.netDown(socketUtil_eth, ethernetPara, ethernetPara.getSock_t(), ethernetPara.getType())) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:netDown失败(%d)", Tools.getLineInfo(), i, ret);
					continue;
				}
				succ++;
			}
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "RS232串口/ETH交叉测试完成(共进行%d次), 成功了%d次", i, succ);
			break;
		}
	}
}