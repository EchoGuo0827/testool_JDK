package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.EthernetPara;
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
 * file name 		: SysTest18.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: 磁卡/LAN交叉测试
 * related document :
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		 测试前置添加解绑磁卡操作		20200415		郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest18 extends DefaultFragment
{
	private final String TAG = SysTest18.class.getSimpleName();
	private final String TESTITEM = "磁卡/LAN";
	private EthernetPara ethernetPara = new EthernetPara();
	private WifiPara wifiPara = new WifiPara();
	NetWorkingBase[] netWorkingBases = {ethernetPara,wifiPara};
	Gui gui = new Gui(myactivity, handler);
	
	private Config config;
	private int ret=-1;
	public void systest18() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		config = new Config(myactivity, handler);
		// 测试前置：解绑磁卡事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_MAGCARD});
		initLayer();
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("磁卡/LAN\n0.LAN配置\n1.交叉测试");
			switch (returnValue) 
			{
				
			case '0':
				//调用LAN配置函数 
				config.confConnLAN(wifiPara, ethernetPara);
				break;
				
			case '1':
				try
				{
					cross_test();
				}catch(Exception e){
					gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
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
		int j = GlobalVariable.chooseConfig;
		Log.e(TAG+" j", j+"");
		Sock_t[] sock_t = {ethernetPara.getSock_t(),wifiPara.getSock_t()};
		int i = 0, succ = 0, ret = 0;
		int send_len = 0,rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		LinkType[] type = {ethernetPara.getType(),wifiPara.getType()};
		SocketUtil socketUtil = new SocketUtil( netWorkingBases[j].getServerIp(), netWorkingBases[j].getServerPort());
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type[j]);
		/*if((ret=layerBase.netUp(socketUtil,netWorkingBases[j],type[j])) != NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:NetUp失败(%d)", Tools.getLineInfo(),ret);
			return;
		}*/
		//磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(true)
		{
			//保护动作
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			//测试退出点
			if(gui.cls_show_msg1(3, "%s/磁卡交叉测试，已执行%d次，成功%d次，【取消】退出测试", type[j],i, succ)==ESC)
				break;
			if(update_snd_packet(sendPacket,type[j])!=NDK_OK)
				break;
			i++;
			if((ret = layerBase.netUp(netWorkingBases[j],type[j])) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s NetUp失败（%d）", Tools.getLineInfo(),i,type[j],ret);
				continue;
			}
			// 传输层建立
			if((ret=layerBase.transUp(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s transUp失败(%d)",Tools.getLineInfo(),i,type[j],  ret);
				continue;
			}
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//发送数据
			if ((send_len = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,netWorkingBases[j]))!= sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, type[j],sendPacket.getLen(), send_len);
				continue;
			}
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if ((rec_len = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,netWorkingBases[j])) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s接收数据失败(预期:%d,实际:%d)",Tools.getLineInfo(),i, type[j], sendPacket.getLen(), rec_len);
				continue;
			}
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s数据校验失败", Tools.getLineInfo(), i,type[j]);
				continue;
			}
			//刷卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			// 挂断
			if((ret=layerBase.transDown(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s transDown失败(%d)", Tools.getLineInfo(), i,type[j],ret);
				continue;
			}
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			succ++;
		}
		postEnd();
//		layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s/磁卡交叉测试完成,已执行次数为%d,成功为%d次", type[j],i, succ);
	}
	
	private void postEnd()
	{
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
	}
	
}
