package com.example.highplattest.systest;

import java.io.IOException;
import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest68.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160902
 * directory 		: 
 * description 		: NFC/无线交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160902	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest68 extends DefaultFragment
{
	private final String TAG = SysTest68.class.getSimpleName();
	private final String TESTITEM = "NFC/WLM";
	Nfc_Card nfc_card = Nfc_Card.NFC_B;
	private MobilePara mobilePara = new MobilePara();
	private Gui gui = null;
	private Config config;
	
	public void systest68() 
	{
		gui = new Gui(myactivity, handler);
		// 无线初始化
		initLayer();
		config = new Config(myactivity, handler);
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		boolean mobilestate=mobileUtil.getMobileDataState(myactivity);
		mobileUtil.closeOther();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			nfc_card = nfc_config(handler, TESTITEM);
			config.confConnWLM(true, mobilePara);
			try 
			{
				cross_test();
			} catch (Exception e) {
				gui.cls_show_msg1(2, "抛出%s异常", e.getMessage());
			}
			mobileUtil.setMobileData(myactivity, mobilestate);
			return;
		}
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/WLM\n0.NFC配置\n1.无线配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// nfc配置
				nfc_card = nfc_config(handler, TESTITEM);
				break;
				
			case '1':
				// 无线配置
				switch (config.confConnWLM(true, mobilePara)) 
						{
				case NDK_OK:
					gui.cls_show_msg1(2, "网络配置成功!!!");
					break;

				case NDK_ERR:
					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime,"line %d:网络未接通!!!",Tools.getLineInfo());
					break;

				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
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
	
	
	/**
	 * nfc/无线交叉测试
	 */
	public void cross_test() 
	{
		/*private & local definition*/
		int i = 0,succ = 0, ret = -1, slen = 0,rlen = 0;
		PacketBean sendPacket = new PacketBean();
		NfcTool nfcTool = new NfcTool(myactivity);
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		
		setWireType(mobilePara);
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		
		//提示信息
		if(GlobalVariable.gAutoFlag != ParaEnum.AutoFlag.AutoFull)
			gui.cls_show_msg("请放入sim卡以及"+nfc_card+"卡，完成任意键继续");
		while(true)
		{
			//保护动作
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(3,"%s/WLM交叉测试，已执行%d次，成功%d次，【取消】退出测试", nfc_card, i, succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!=NDK_OK)
				break;
			i++;
			
			//Netup
			if((ret = layerBase.netUp(mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：NetUp失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//TransUp
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：TransUp失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//nfc连接
			if((ret = nfcTool.nfcConnect(reader_flag)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：%s卡激活失败（%d）", Tools.getLineInfo(),i,nfc_card,ret);
				continue;
			}
			
			//发送数据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：数据发送失败（%d）", Tools.getLineInfo(),i,slen);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：数据接收失败（ %d）", Tools.getLineInfo(),i,rlen);
				continue;
			}
			
			//nfc读写
			try {
				if((ret = nfcTool.nfcRw(nfc_card)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：%s卡APDU失败（%d）", Tools.getLineInfo(),i,nfc_card,ret);
					continue;
				}
			} catch (IOException e) {
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：%s卡APDU失败（%d）", Tools.getLineInfo(),i,nfc_card,ret);
				continue;
			}
			
			//比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：校验数据失败", Tools.getLineInfo(),i);
				continue;
			}
			
			//smart下电
			nfcTool.nfcDisEnableMode();
			
			//TransDown
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：TransDown失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//NetDown
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：NetDown失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG,"cross_test", g_time_0,"%s/WLM交叉测试完成,已执行次数为%d,成功为%d次",nfc_card, i,succ);
	}
}
