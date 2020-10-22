package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlModemManager;
import android.newland.content.NlContext;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest29.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: SMART/MDM交叉测试
 * related document :
 * history 		 	: 变更记录			变更时间			变更人员
 *			  		 测试前置解绑事件		20200415		郑薛晴
 * 					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest29 extends DefaultFragment 
{
	private final String TAG = SysTest29.class.getSimpleName();
	private final String TESTITEM = "SMART/MDM";
	private _SMART_t type = _SMART_t.CPU_A;
	private NlModemManager nlModemManager;
	private Gui gui = new Gui(myactivity, handler);
	private Config config;
	private int felicaChoose=0;
	private int ret=-1;
	public void systest29() 
	{
		config=new Config(myactivity,handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		nlModemManager =  (NlModemManager) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("SMART/MDM交叉\n0.SMART配置\n1.MDM配置\n2.交叉测试\n");
			switch (nkeyIn) 
			{
			
			case '0':
				type = config.smart_config().get(0);//20170116wangxiaoyu
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(smartInit(type)!=NDK_OK)
				{
					gui.cls_show_msg1(g_time_0, "line %d:smart卡初始化失败", Tools.getLineInfo());
				}
				break;
				
			case '1':
				//调用MDM配置函数
				new Config(myactivity,handler).config_para();
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
		int i = 0,succ = 0, send_len = 0, rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		PacketBean sendPacket = new PacketBean();
		LinkType mdmtype = ModemBean.type_MDM;
		Layer layer = new Layer(myactivity, handler);
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket, mdmtype);
		gui.cls_show_msg("测试前请确保，已安装"+type+"卡，完成任意键继续");
		// 测试前置：进行复位操作
		if((ret = mdm_reset(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:modem复位失败（ret = %d）", Tools.getLineInfo(),ret);
			return;
		}
		// 注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件注册失败(%d)",Tools.getLineInfo(), type,ret);
			return;
		}
		while(true)
		{
			//保护动作
			smartDeactive(type);
			mdm_clrportbuf_all(nlModemManager);
			layer.mdm_hangup(nlModemManager);
			//测试退出点
			if(gui.cls_show_msg1(3, "正在进行第%d次%s/MDM交叉测试(已成功%d次),【取消】退出测试", i+1, type, succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, mdmtype)!= NDK_OK)
				break;
			i++;
			if(GlobalVariable.sdkType==SdkType.SDK3&&type==_SMART_t.IC)
			    gui.cls_show_msg("请插拔或重新放置"+type+"卡，完成任意键继续");
			// 检测smart卡
			if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:%s初始化失败(%d)", Tools.getLineInfo(), i, type,ret);
				continue;
			}
			//初始化MODEM
			gui.cls_show_msg1(1, "初始化MODEM中（第%d次）...", i);
			if((ret = mdm_init(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM初始化失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			//上电
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK )
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			//拨号
			gui.cls_show_msg1(2, "MODEM拨%s中（第%d次）...", ModemBean.MDMDialStr, i);
			if((ret = layer.mdm_dial(ModemBean.MDMDialStr, nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM拨号失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			//读写
			if ((ret = smartApduRw(type,req,UidBuf)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:卡读写失败(0x%x)",Tools.getLineInfo(), i, ret);
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
			if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 20, mdmtype))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}
			
			//比较数据
			if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(), mdmtype)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			
			//下电
			if((ret = smartDeactive(type))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:卡下电失败(0x%x)",Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//挂断
			gui.cls_show_msg1(2, "MODEM挂断中（第%d次）...", i);
			if((ret = layer.mdm_hangup(nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次:MDM挂断失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			succ++;
		}
		postEnd();
		smartDeactive(type);
		layer.mdm_hangup(nlModemManager);
		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "%s/MDM交叉测试完成,已执行次数为%d,成功为%d次", type,i,succ);
	}
	
	public void postEnd()
	{
		// 解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件解绑失败(%d)",Tools.getLineInfo(), type,ret);
			return;
		}
	}
	
}
