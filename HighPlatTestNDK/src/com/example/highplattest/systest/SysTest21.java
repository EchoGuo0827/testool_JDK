package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.bean.ModemBean;
import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest21.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: 磁卡/MDM交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  linwl
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest21 extends DefaultFragment 
{
	private final String TAG = SysTest21.class.getSimpleName();
	private final String TESTITEM = "磁卡/MDM";
	private NlModemManager nlModemManager;
	private Gui gui = new Gui(myactivity, handler);
	private Config config=new Config(myactivity,handler);
	public void systest21() 
	{
		nlModemManager =  (NlModemManager) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			int returnValue=gui.cls_show_msg("磁卡/MDM交叉\n0.MDM配置\n1.交叉测试");
			switch (returnValue) 
			{
			case '0':
				//调用MDM配置函数
				config.config_para();	
				break;
				
			case '1':
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
		PacketBean sendPacket = new PacketBean();
		LinkType type =ModemBean.type_MDM;
		Layer layer = new Layer(myactivity, handler);
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type);
	
		gui.cls_show_msg("测试前请连接好MDM线路并准备一张正常磁卡，完成点击是");
		// 测试前置，复位modme操作
		if((ret=mdm_reset(nlModemManager))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "line %d:第%d次:MDM复位失败(%d)", Tools.getLineInfo(), i, ret);
			return;
		}
		// 磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), maglistener)) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(true)
		{
			//保护动作
			mdm_clrportbuf_all(nlModemManager);
			layer.mdm_hangup(nlModemManager);
			
			Log.e("i"+"  len", i+" "+sendPacket.getLifecycle());
			//测试退出点
			if(gui.cls_show_msg1(3,"正在进行第%d次%s交叉测试(已成功%d次),【取消】键退出测试", i+1, TESTITEM, succ)==ESC)
				break;
			if(update_snd_packet(sendPacket, type)!= NDK_OK)
				break;
			i++;
			
			//初始化MODEM
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", i);
			if((ret=mdm_init(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:MDM初始化失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK1_2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//拨号
			gui.cls_show_msg1(2, "MODEM拨%s中（第%d次）...", ModemBean.MDMDialStr, i);
			if((ret=layer.mdm_dial(ModemBean.MDMDialStr, nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:MDM拨号失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK1_2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//MDM数据通讯
			//发送数据
			if((send_len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:MDM发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), send_len);
				continue;
			}
			SystemClock.sleep(3000);
			//接收数据
			if((rec_len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 20, type))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:MDM接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(), i, sendPacket.getLen(), rec_len);
				continue;
			}
			
			//比较数据
			if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(), type)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:MDM数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			
			//刷卡测试
			if((ret = MagcardReadTest(TK1_2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:刷卡失败(%d)", Tools.getLineInfo(), i, ret);
				continue;
			}
			
			//挂断
			gui.cls_show_msg1(1,"MODEM挂断中（第%d次）...", i);
			if((ret=layer.mdm_hangup(nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:MDM挂断失败(%d)", Tools.getLineInfo(), i, ret);
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
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"交叉测试完成,已执行次数为%d,成功为%d次", i,succ);
	}
}
