package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest28.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150416
 * directory 		: 
 * description 		: SMART/无线交叉测试
 * related document :
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		   测试前置添加解绑事件 		20200415		郑薛晴
 * 					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest28 extends DefaultFragment
{
	private final String TAG = SysTest28.class.getSimpleName();
	private final String TESTITEM = "SMART/WLM";
	private _SMART_t card = _SMART_t.CPU_A;
	private List<_SMART_t> cardList = new ArrayList<_SMART_t>();
	private MobilePara mobilePara = new MobilePara();
	private Gui gui = null;
	private Config config;
	private int felicaChoose=0;
	private int ret=-1;
	
	public void systest28() 
	{
		gui = new Gui(myactivity, handler);
		//无线初始化
		initLayer();
		config = new Config(myactivity, handler);
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		boolean mobilestate=mobileUtil.getMobileDataState(myactivity);
		mobileUtil.closeOther();
		
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		if(GlobalVariable.gSequencePressFlag)
		{
			cardList = config.smart_config();
			if(config.confConnWLM(true,mobilePara)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TAG,g_time_0,"line %d:网络未接通！！！",Tools.getLineInfo());
				return;
			}
			for(_SMART_t chooseCard:cardList){
				if(GlobalVariable.sdkType==SdkType.SDK3&&chooseCard==_SMART_t.IC)
					continue;
				card=chooseCard;
				if(card==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TAG,g_time_0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
			}
			mobileUtil.setMobileData(myactivity, mobilestate);
			return;
		}
		//测试程序入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("SMART/WLM\n0.SMART配置\n1.无线配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				card = config.smart_config().get(0);
				if(card==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(smartInit(card)!=NDK_OK)
				{
					gui.cls_show_msg1(g_time_0, "line %d:smart卡初始化失败", Tools.getLineInfo());
				}
				break;
				
			case '1':
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
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					e.printStackTrace();
					gui.cls_show_msg1(0,"line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
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
		int i = 0,succ = 0, ret = -1, slen = 0,rlen = 0;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		PacketBean sendPacket = new PacketBean();
		SocketUtil socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		if(setWireType(mobilePara)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:%s未插入sim卡", Tools.getLineInfo(),TAG);
			return;
		}
		LinkType type = mobilePara.getType();
		Sock_t sock_t = mobilePara.getSock_t();
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, type);
		
		//提示信息
		gui.cls_show_msg("确保已安装Smart卡：" + card+"，完成任意键继续");	
		if((ret=SmartRegistEvent(card))!=NDK_OK&&(ret=SmartRegistEvent(card))!=NDK_NO_SUPPORT_LISTENER)
		{
			gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:%s事件注册失败(%d)", Tools.getLineInfo(),card,ret);
			return;
		}
	
		while(true)
		{
			smartDeactive(card);
			layerBase.transDown(socketUtil, sock_t);
			layerBase.netDown(socketUtil, mobilePara, sock_t, type);
			//测试退出点
			if(gui.cls_show_msg1(3, "%s/WLM交叉测试，已执行%d次，成功%d次，【取消】退出测试", card,i,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!=NDK_OK)
				break;
			i++;
			if(GlobalVariable.sdkType==SdkType.SDK3&&card==_SMART_t.IC)
			    gui.cls_show_msg("请插拔或重新放置"+card+"卡，完成任意键继续");
			if((ret = smart_detect(card, UidLen, UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡检测失败（%d）", Tools.getLineInfo(),i,card,ret);
				continue;
			}
			//Netup
			if((ret = layerBase.netUp(mobilePara, type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：NetUp失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			//TransUp
			if((ret = layerBase.transUp(socketUtil,sock_t)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：TransUp失败（%d）", Tools.getLineInfo(),i,ret);
				continue;
			}
			//smart激活
			if((ret = smartActive(card,felicaChoose,UidLen,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡激活失败（%d）",Tools.getLineInfo(),i,card,ret);
				continue;
			}
			
			//发送数据
			if((slen = sockSend(socketUtil, sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：发送数据失败（%d）", Tools.getLineInfo(),i,slen);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = sockRecv(socketUtil, rbuf, sendPacket.getLen(), SO_TIMEO, mobilePara)) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：接收数据失败（%d）", Tools.getLineInfo(),i,rlen);
				continue;
			}
			
			//smart读写
			if((ret = smartApduRw(card,req,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡APDU失败（%d）",Tools.getLineInfo(),i,card,ret);
				continue;
			}
			
			//比较数据
			if(!Tools.memcmp(sendPacket.getHeader(), rbuf, rlen))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：数据校验失败", Tools.getLineInfo(),i);
				continue;
			}
			
			//smart下电
			if((ret = smartDeactive(card)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡关闭场失败（%d）", Tools.getLineInfo(),i,card,ret);
				continue;
			}
			
			//TransDown
			if((ret = layerBase.transDown(socketUtil, mobilePara.getSock_t())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:TransDown失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			//NetDown
			if((ret = layerBase.netDown(socketUtil, mobilePara, mobilePara.getSock_t(), type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：NetDown失败(%d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			succ++;
		}
		postEnd();
		smartDeactive(card);
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "(%s/WLM)交叉测试完成,已执行次数为%d,成功为%d次", card,i,succ);
	}
	
	public  void postEnd()
	{
		// 解绑事件
		if ((ret = SmartUnRegistEvent(card)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:%s事件解绑失败(%d)",Tools.getLineInfo(), card,ret);
			return;
		}
	}
}
