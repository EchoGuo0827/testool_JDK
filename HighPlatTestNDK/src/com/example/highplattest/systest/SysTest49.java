package com.example.highplattest.systest;

import java.util.Arrays;

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
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.BatteryReceiver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest49.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150606
 * directory 		: 
 * description 		: 取电量/无线交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150415	 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest49 extends DefaultFragment
{
	final String TAG = SysTest49.class.getSimpleName();
	final String TESTITEM = "电池信息/WLM";
	final IntentFilter intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private MobilePara mobilePara = new MobilePara();
	private BatteryReceiver mBatteryReceiver;
	private Gui gui = null;
	private Config config;
	
	public void systest49() 
	{
		gui = new Gui(myactivity, handler);
		//无线初始化
		initLayer();
		mBatteryReceiver = new ReceiverTracker().new BatteryReceiver();
		config = new Config(myactivity, handler);
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		boolean mobilestate=mobileUtil.getMobileDataState(myactivity);
		mobileUtil.closeOther();
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			config.confConnWLM(true,mobilePara);
			try {
				cross_test();
			} catch (Exception e) {
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("取电量/WLM\n0.无线配置\n1.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 无线配置
				switch (config.confConnWLM(true,mobilePara)) 
				{
				case NDK_OK:
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
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
			
			case ESC:
				mobileUtil.setMobileData(myactivity, mobilestate);
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
		int i = 0, slen = 0, succ = 0, rlen = 0;
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		if(setWireType(mobilePara)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:%s未插入sim卡", Tools.getLineInfo(),TAG);
			return;
		}
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
		while(true)
		{
			//保护动作
			layerBase.transDown(socketUtil, mobilePara.getSock_t());
			layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type);
			SystemClock.sleep(5000);//每次挂断之后重新连接要等待5秒,减轻绎芯片的压力
			
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，【取消】退出测试",TESTITEM,i,succ)==ESC)
				break;
			
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			//Netup
			if((ret = layerBase.netUp( mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：NetUp失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//TransUp
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次：TransUp失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			myactivity.registerReceiver(mBatteryReceiver, intent);
			SystemClock.sleep(1000L);
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			
			//发送数据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：发送数据失败（%d）", Tools.getLineInfo(),i,slen);
				continue;
			}
			// 取电量
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：接收数据失败（%d）", Tools.getLineInfo(),i,rlen);
				continue;
			}
			
			// 取电量和电压
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			
			//比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：校验数据失败", Tools.getLineInfo(),i);
				continue;
			}
			//TransDown
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：TransDown失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			// 取电量
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			//NetDown
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：NetDown失败 （%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			// 取电量
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			succ++;
			myactivity.unregisterReceiver(mBatteryReceiver);
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s交叉测试完成，已执行次数为%d，成功为%d次",TESTITEM,i,succ);
	}
}
