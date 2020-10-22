package com.example.highplattest.systest;

import java.io.IOException;

import android.os.SystemClock;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest67.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160902
 * directory 		: 
 * description 		: NFC/触屏交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160902	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest67 extends DefaultFragment
{
	private final String TAG = SysTest67.class.getSimpleName();
	private final String TESTITEM = "NFC/触屏";
	private Nfc_Card nfc_Card = Nfc_Card.NFC_B;
	Gui gui = null;
	
	public void systest67() 
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
			int nkeyIn = gui.cls_show_msg("NFC/触屏交叉\n0.NFC配置\n1.交叉测试\n");
			switch (nkeyIn) 
			{
			case '0':
				nfc_Card = nfc_config(handler, TESTITEM);
				break;
				
			case '1':
				try {
					cross_test();
				} catch (IOException e) 
				{
					e.printStackTrace();
					gui.cls_show_msg1(2, "抛出%s异常", e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;

			}
		}
	}
	
	
	/**
	 * NFC/触屏交叉测试
	 */
	public void cross_test() throws IOException
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
		
		while(cnt>0)
		{
			// 保护动作
			nfcTool.nfcDisEnableMode();

			if(gui.cls_show_msg1(3, "%s/触屏交叉测试，已执行%d次，成功%d次，【取消】退出测试", nfc_Card, bak-cnt, succ)==ESC)
				break;
			cnt--;
			if((ret = nfcTool.nfcConnect(reader_flag))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡连接失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			SystemClock.sleep(1000);
			ret = systestTouch();
			if (ret != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：触屏测试失败，实际（%d，%d）",
						Tools.getLineInfo(),bak-cnt,(int)GlobalVariable.gScreenX,(int)GlobalVariable.gScreenY);
				continue;
			}
			
			if((ret = nfcTool.nfcRw(nfc_Card))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡APDU失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			nfcTool.nfcDisEnableMode();
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "%s/触屏测试完成，已执行次数为%d，成功为%d次", nfc_Card,bak-cnt,succ);
	}
}
