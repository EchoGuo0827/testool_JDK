package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.os.SystemClock;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest20.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150415
 * directory 		: 
 * description 		: 无线/磁卡交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150415	 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest20 extends DefaultFragment
{
	private final String TAG = SysTest20.class.getSimpleName();
	private final String TESTITEM = "WLM/磁卡";
	private MobilePara mobilePara = new MobilePara();
	private Gui gui = new Gui(myactivity, handler);
	SocketUtil socketUtil;
	private Config config;
	private MobileUtil moblieUtil;
	boolean mobilestate;
	
	public void systest20()
	{
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_time_0,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		//初始化处理器，连接K21设备
		initLayer();
		moblieUtil=MobileUtil.getInstance(myactivity,handler);
		mobilestate=moblieUtil.getMobileDataState(myactivity);
		moblieUtil.closeOther();
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("WLM/磁卡\n0.无线配置\n1.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 无线配置
				switch (config.confConnWLM(true,mobilePara)) 
				{
				case NDK_OK:
					socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
					gui.cls_show_msg1(g_keeptime, "网络配置成功!!!");
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1(0,"line %d:网络未接通!!!");
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				try 
				{
					//交叉测试
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG,TAG,2, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				moblieUtil.setMobileData(myactivity, mobilestate);
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int ret = -1;
		int i = 0, slen = 0, succ = 0, rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		
		setWireType(mobilePara);
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
		
		//磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(true)
		{
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
			//保护动作
			SystemClock.sleep(5000);//每次挂断之后重新连接要等待5秒,减轻绎芯片的压力
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，【取消】退出测试", TESTITEM,i,succ)==ESC)
				break;
			
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			//Netup
			if((ret = layerBase.netUp(mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:NetUp失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//TransUp
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:TransUp失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//发送数据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:发送数据失败(%d)", Tools.getLineInfo(),i,slen);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:接收数据失败(%d)", Tools.getLineInfo(),i,rlen);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:校验数据失败", Tools.getLineInfo());
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//TransDown
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:TransDown失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//NetDown
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:NetDown失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			succ++;
		}
		//解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM, g_time_0,"%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,i,succ);
	}
}
