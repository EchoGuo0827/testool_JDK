package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest41.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150415
 * directory 		: 
 * description 		: 无线/MDM交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest41 extends DefaultFragment
{
	private final String TAG = SysTest41.class.getSimpleName();
	private final String TESTITEM = "无线/MDM";
	private MobilePara mobilePara = new MobilePara();
	private NlModemManager nlModemManager;
	private Gui gui = null;
	
	public void systest41() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		setWireType(mobilePara);
		initLayer();
		nlModemManager =  (NlModemManager) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("无线/MDM交叉\n0.无线配置\n1.MDM配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 无线配置
				switch (new Config(myactivity,handler).confConnWLM(true,mobilePara)) //20170116wangxiaoyu
				{
				case NDK_OK:
					gui.cls_show_msg1(2,"网络配置成功!");
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1(2,"line %d:网络未接通！！！",Tools.getLineInfo());
					break;
					
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				//调用MDM配置函数
				new Config(myactivity,handler).config_para();
				systest41();
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
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int ret = -1;
		int i = 0, slen = 0, succ = 0, send_len = 0, rec_len = 0, rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		LinkType type = ModemBean.type_MDM;
		LinkType type2 = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
//		Layer layer = new Layer(myActivity, handler);
		// 测试前置，复位动作
		if((ret = mdm_reset(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:MDM复位失败（ret = %d）", Tools.getLineInfo(),ret);
			return;
		}
		while(true)
		{
			//保护动作
			mdm_clrportbuf_all(nlModemManager);
			layerBase.mdm_hangup(nlModemManager);
			/*if((ret = layerBase.mdm_hangup(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:MDM挂断失败（ret = %d）", Tools.getLineInfo(),ret);
				return;
			}*/
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
			SystemClock.sleep(5000);//每次挂断之后重新连接要等待5秒,减轻绎芯片的压力
			
			Log.e("i"+"  len", i+" "+sendPacket.getLifecycle());
			if(gui.cls_show_msg1(3,"正在进行第%d次%s测试中\n（已成功%d次）,【取消】键退出测试", i+1, TESTITEM, succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			//初始化MODEM
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", i);
			if((ret=mdm_init(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM初始化失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//拨号
			gui.cls_show_msg1(1,"MODEM拨%s中（第%d次）...", ModemBean.MDMDialStr, i);
			if((ret=layerBase.mdm_dial(ModemBean.MDMDialStr, nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM拨号失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//MDM数据通讯
			//发送数据
			if((send_len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
				continue;
			}
			//接收数据
			if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 20, type))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:第%d次:MDM接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}
			
			//比较数据
			if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(), type)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			
			//挂断
			gui.cls_show_msg1(2, "MODEM挂断中（第%d次）...", i);
			if((ret= layerBase.mdm_hangup(nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:第%d次:MDM挂断失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//无线---
			//Netup
			if((ret = layerBase.netUp( mobilePara, type2)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo(),ret);
				continue;
			}
			
			//TransUp
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:TransUp失败(ret = %d)", Tools.getLineInfo(),ret);
				continue;
			}
			
			//发送数据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:发送数据失败(ret = %d)", Tools.getLineInfo(),slen);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:接收数据失败(ret = %d)", Tools.getLineInfo(),rlen);
				continue;
			}
			
			//比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:校验数据失败(ret = %d)", Tools.getLineInfo());
				continue;
			}
			
			//TransDown
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:TransDown失败(ret = %d)", Tools.getLineInfo(),ret);
				continue;
			}
			
			//NetDown
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type2)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:NetDown失败(ret = %d)", Tools.getLineInfo(),ret);
				continue;
			}
			succ++;
		}
		
		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "交叉测试完成,已执行次数为%d,成功为%d次", i,succ);
	}
	
}
