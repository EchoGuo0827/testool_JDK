package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest26.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: SMART/LAN交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 *					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest26 extends DefaultFragment
{
	private final String TAG = SysTest26.class.getSimpleName();
	private final String TESTITEM = "SMART/LAN";
	private _SMART_t type = _SMART_t.CPU_A;
	private List<_SMART_t> typeList = new ArrayList<_SMART_t>();
	private EthernetPara ethernetPara = new EthernetPara();
	private WifiPara wifiPara = new WifiPara();
	private NetWorkingBase[] netWorkingBases = {ethernetPara,wifiPara};
	private SocketUtil socketUtil;
	private Gui gui = null;
	private Config config;
	private int felicaChoose=0;
	private int ret=-1;
	public void systest26()
	{
		gui = new Gui(myactivity, handler);
//		if (GlobalVariable.gModuleEnable.get(Mod_Enable.EthEnable)) {
//			ethernetUtil = EthernetUtil.getInstance(myactivity, handler);
//		}
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		initLayer();
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			typeList = config.smart_config();
			
			config.confConnLAN(wifiPara, ethernetPara);
			for(_SMART_t typeChoose:typeList){
				if(GlobalVariable.sdkType==SdkType.SDK3 && typeChoose==_SMART_t.IC)
					continue;
				type=typeChoose;
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				try 
				{
					// 交叉测试
					cross_test();
				} catch (Exception e) {
					gui.cls_show_msg1_record(TAG, TAG,g_time_0, "line %d:抛出异常（%s）", e.getMessage());
				}
			}
			
			return;
		}
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("SMART/LAN\n0.SMART配置\n1.LAN配置\n2.交叉测试");
			switch (returnValue) 
			{
			
			case '0':
				type = new Config(myactivity,handler).smart_config().get(0);
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(smartInit(type)!=NDK_OK)
				{
					gui.cls_show_msg1(g_time_0, "line %d:smart卡初始化失败", Tools.getLineInfo());
				}
				break;
				
			case '1':
				//调用LAN配置函数
				config.confConnLAN(wifiPara, ethernetPara);
				break;
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
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
		int ret = -1, i = 0, succ = 0, send_len = 0, rec_len = 0;
		int j = GlobalVariable.chooseConfig;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		LinkType[] types = {ethernetPara.getType(),wifiPara.getType()};
		Sock_t[] sock_t = {ethernetPara.getSock_t(),wifiPara.getSock_t()};
		socketUtil = new SocketUtil(netWorkingBases[j].getServerIp(), netWorkingBases[j].getServerPort());
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,types[j]);
		gui.cls_show_msg("测试前请先插入或放置%s卡,完成任意键继续",type);
		// 注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:smart事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
//		// 链路连接
//		if((ret=layerBase.netUp(socketUtil,netWorkingBases[j],types[j])) != NDK_OK)
//		{
//			gui.cls_show_msg1(g_keeptime, "line %d:netUp失败（%d）", Tools.getLineInfo(), ret);
//			return;
//		}
		while(true)
		{
			//保护动作
			smartDeactive(type);
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],types[j]);
			//测试退出点
			if(gui.cls_show_msg1(2, "%s/%s交叉测试,已执行%d次,成功%d次,[取消]退出测试" ,type,types[j],i, succ)==ESC)
				break;
			if(update_snd_packet(sendPacket,types[j])!= NDK_OK)
				break;
			i++;
			if(GlobalVariable.sdkType==SdkType.SDK3&&type==_SMART_t.IC)
			   gui.cls_show_msg("请插拔或重新放置"+type+"卡,完成任意键继续");
			// smart初始化
			if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s检测失败(%d)", Tools.getLineInfo(),i,type,ret);
				continue;
			}
			if((ret = layerBase.netUp(netWorkingBases[j],types[j])) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s NetUp失败(%d)", Tools.getLineInfo(),i,types[j],ret);
				continue;
			}
			// 传输层建立
			if((ret=layerBase.transUp(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s transUp失败(%d)", Tools.getLineInfo(), i, types[j],ret);
				continue;
			}
			//上电
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK )
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡激活失败(%d)", Tools.getLineInfo(), i,type, ret);
				continue;
			}
			//数据通讯
			//发送数据
			if ((send_len = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,netWorkingBases[j])) != sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, sendPacket.getLen(), send_len);
				continue;
			}
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if ((rec_len = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,netWorkingBases[j])) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, sendPacket.getLen(), rec_len);
				continue;
			}
			//读写
			if ((ret=smartApduRw(type,req,UidBuf)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡APDU失败(%d)", Tools.getLineInfo(), i,type, ret);
				continue;
			}
			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(),i);
				continue;
			}
			//下电
			if((ret = smartDeactive(type))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡下电失败(%d)",Tools.getLineInfo(),i, type,ret);
				continue;
			}
			//挂断
			if((ret=layerBase.transDown(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s transDown失败(%d)", Tools.getLineInfo(),i,types[j],ret);
				continue;
			}
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],types[j]);
			smartDeactive(type);
			succ++;
		}
		postEnd();
		// 下电操作
		layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],types[j]);
		smartDeactive(type);
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "(%s/%s)交叉测试完成,已执行次数为%d,成功为%d次", type,types[j],i,succ);
	}
	
	public void postEnd()
	{
		// 解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:%s事件解绑失败(%d)",Tools.getLineInfo(),type, ret);
			return;
		}
	}
}
