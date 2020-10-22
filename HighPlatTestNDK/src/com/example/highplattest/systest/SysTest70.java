package com.example.highplattest.systest;

import java.io.IOException;

import android.annotation.SuppressLint;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest70.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160902
 * directory 		: 
 * description 		: NFC/SD(U盘)交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160902	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest70 extends DefaultFragment
{
	private final String TAG = SysTest70.class.getSimpleName();
	private final String TESTITEM = "NFC/SD(U盘)";
	Nfc_Card nfc_card = Nfc_Card.NFC_B;
	DiskType diskType = DiskType.SDDSK;
	private Gui gui = null;
	private Config config;
	private String diskString;
	public void systest70()
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		if(GlobalVariable.gSequencePressFlag)
		{
			nfc_card = nfc_config(handler, TESTITEM);
			diskString = config.confSDU();
			String[] diskTypes=diskString.split(",");
			for (int i = 0; i < diskTypes.length; i++) {
				diskType=getDiskType(diskTypes[i]);
				try {
					cross_test();
				} catch (Exception e) {
					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:抛出异常（%s）",Tools.getLineInfo(), e.getMessage());
				}
			}	
			return;
		}
		// 测试主入口
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/SD(U盘)\n0.NFC配置\n1.SD卡盘配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// 配置nfc
				nfc_card = nfc_config(handler, TESTITEM);
				break;
				
			case '1':
				// 配置SD卡U盘
				diskType =getDiskType(config.confSDU());
				break;
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;

			}
		}
	}
	
	
	/**
	 * NFC/SD(U盘)
	 */
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		NfcTool nfcTool = new NfcTool(myactivity);
		
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		
		//提示信息
		gui.cls_show_msg("测试前请确保，已安装"+diskType +"以及"+nfc_card+"卡，完成任意键继续");
		
		while(cnt > 0)
		{
			// 保护动作
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(3, "%s/%s交叉测试，已执行%d次，成功%d次，【取消】退出测试",nfc_card,diskType,bak-cnt,succ)==ESC)
				break;
			cnt--;
			//NFC卡连接
			if((ret = nfcTool.nfcConnect(reader_flag)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：%s卡激活失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_card,ret);
				continue;
			}
			
			//NFC卡读写
			try {
				if((ret = nfcTool.nfcRw(nfc_card)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：%s卡APDU失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_card,ret);
					continue;
				}
			} catch (IOException e) {
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次：%s卡APDU失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_card,ret);
				continue;
			}
			
			//NFC下电
			nfcTool.nfcDisEnableMode();
			//SD卡U盘
			if((ret = systestSdCard(diskType)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"cross_test",g_keeptime, "line %d:第%d次:%s测试失败（%d）", Tools.getLineInfo(),diskType,bak-cnt,ret);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG,"cross_test",g_time_0, "(%s/%s)交叉测试完成，已执行次数为%d，成功为%d次", nfc_card,diskType,bak-cnt,succ);
	}
}
