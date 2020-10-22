package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.WifiPara;
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
 * file name 		: SysTest50.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150606
 * directory 		: 
 * description 		: 取电量/WIFI交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20150606		create
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest50 extends DefaultFragment
{
	private final String TAG = SysTest50.class.getSimpleName();
	private final String TESTITEM = "电池信息/WIFI";
	private BatteryReceiver mBatteryReceiver;
	private WifiPara wifiPara = new WifiPara();
	protected IntentFilter intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);// 电量变化广播
	private Gui gui = null;
	private Config config;
	
	public void systest50()
	{
		gui = new Gui(myactivity, handler);
		//初始化layer对象
		initLayer();
		mBatteryReceiver = new ReceiverTracker().new BatteryReceiver();
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			config.confConnWlan(wifiPara);
			try 
			{
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("取电量/WIFI\n0.WIFI配置\n1.交叉测试");
			switch (returnValue) 
			{
			
			case '0':
				switch (config.confConnWlan(wifiPara)) 
				{
				case NDK_OK:
					gui.cls_show_msg1(2, "网络配置成功!!!");
					break;
					
				case NDK_ERR:
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:网络配置失败!!!", Tools.getLineInfo());
					break;
				
				case NDK_ERR_QUIT:
				default:
					break;
				}
				break;
				
			case '1':
				try {
					cross_test();
				} catch (Exception e) {
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
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
		int i = 0,succ = 0,ret = -1;
		int slen = 0,rlen = 0;
		byte[] buf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		LinkType type = wifiPara.getType();
		Sock_t sock_t = wifiPara.getSock_t();
		SocketUtil socketUtil = new SocketUtil(wifiPara.getServerIp(), wifiPara.getServerPort());
		
		/*process body*/
		sendPacket.setLifecycle(20);
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
		byte[] rbuf = new byte[sendPacket.getLen()];
		
		layerBase.netUp( wifiPara, type);
		while(true)
		{
			layerBase.transDown(socketUtil, sock_t);
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，【取消】退出测试" ,TESTITEM,i,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			// 注册取电量广播
			myactivity.registerReceiver(mBatteryReceiver, intent);
			SystemClock.sleep(1000L);
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			
			// transUp
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:TransUp失败（%d）", Tools.getLineInfo(),ret);
				continue;
			}
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			// 发送数据
			if((slen = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,wifiPara))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:发送失败（实际%d，预期%d）", Tools.getLineInfo(),TAG,slen,sendPacket.getLen());
				continue;
			}
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			// 接收
			if((rlen = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,wifiPara))!=sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:接收失败（实际%d，预期%d）", Tools.getLineInfo(),rlen,sendPacket.getLen());
				continue;
			}
			// 比较收发
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen()))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime,"line %d:校验失败" , Tools.getLineInfo());
				continue;
			}
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			// 挂断
			if ((ret = layerBase.transDown(socketUtil,sock_t)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime,"line %d:TransDown失败（%d）", Tools.getLineInfo(),ret);
				continue;
			}
			
			// 取电量广播注销
			gui.cls_show_msg1(2, mBatteryReceiver.getBatMsg());
			succ++;
			myactivity.unregisterReceiver(mBatteryReceiver);
		}
		layerBase.netDown(socketUtil, wifiPara, sock_t, type);
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次", TESTITEM,i, succ);
	}
}
