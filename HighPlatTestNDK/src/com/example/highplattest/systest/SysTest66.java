package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest66.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160901
 * directory 		: 
 * description 		: NFC/磁卡交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160901	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest66 extends DefaultFragment
{
	private final String TAG = SysTest66.class.getSimpleName();
	private final String TESTITEM = "NFC/磁卡";
	private Nfc_Card nfc_Card = Nfc_Card.NFC_B;
	private Gui gui = null;
	
	public void systest66() 
	{
		gui = new Gui(myactivity, handler);
		// 初始化处理器，连接K21设备
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 测试主入口
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/磁卡\n0.NFC配置\n1.交叉测试");
			switch (returnValue) 
			{
			case '0':
				nfc_config(handler, TESTITEM);
				break;
				
			case '1':
				try {
					cross_test();
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;

			default:
				break;
			}
		}
	}
	

	/**
	 * NFC/磁卡交叉测试
	 */
	public void cross_test() throws Exception
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		NfcTool nfcTool = new NfcTool(myactivity);
		gui.cls_show_msg("请放置"+nfc_Card+"卡，完成任意键继续");
		// 磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(cnt>0)
		{
			// 保护动作
			nfcTool.nfcDisEnableMode();
			
			if(gui.cls_show_msg1(3, "%s/磁卡交叉测试，已执行%d次，成功%d次，【取消】退出测试", nfc_Card,bak-cnt,succ)==ESC)
				break;
			if((ret = nfcTool.nfcConnect(reader_flag))!=NDK_OK)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次%s连接失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER))!=STRIPE)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次刷卡失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			while(cnt>0)
			{
				//测试退出点
				if(gui.cls_show_msg1(3, "%s/磁卡交叉测试，已执行%d次，成功%d次，【取消】键退出测试", nfc_Card,bak-cnt,succ)==ESC)
					break;
				cnt--;
				if((ret = nfcTool.nfcRw(nfc_Card))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次%s读写失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
					continue;
				}
				if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER))!=STRIPE)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:第%d次刷卡失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
					continue;
				}
				succ++;
			}
		}
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		nfcTool.nfcDisEnableMode();
		gui.cls_show_msg1_record(TAG, TESTITEM,g_time_0, "%s/磁卡交叉测试完成，已执行次数为%d，成功为%d次", nfc_Card,bak-cnt,succ);
	}
	
}
