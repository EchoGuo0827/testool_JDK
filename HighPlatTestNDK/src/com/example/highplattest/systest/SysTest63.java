package com.example.highplattest.systest;

import java.io.IOException;
import java.util.Locale;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: 射频卡综合
 * file name 		: Systest63.java
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20160902 
 * 
 * directory 		: 
 * description 		: NFC综合性能、压力
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20160902	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest63 extends DefaultFragment
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest63.class.getSimpleName();
	private final String TESTITEM = "NFC性能、压力";
	private final int TEST_TIME = 10;
	private final int DEFAULT_COUNT_VLE = 3000;
	private Gui gui = null;
	/*----------global variables declaration------------------------*/
	// 目前只支持B卡
	private Nfc_Card nfc_card = Nfc_Card.NFC_B;

	public void systest63() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			// 配置自动化测试
			nfc_card = nfc_config(handler, TESTITEM);
			g_CycleTime = 5000;
			NFC_Pre();
			NFC_Ability();
			g_CycleTime = 15000;
			NFC_RW_Pre();
			return;
		}
		while (true)
		{
			int returnValue=gui.cls_show_msg("NFC综合测试\n0.NFC配置\n1.NFC综合压力\n2.NFC性能测试\n3.NFC读写压力\n4.挥卡测试\n5.异常测试");
			switch (returnValue) 
			{
			// 配置
			case '0':
				nfc_card = nfc_config(handler, TESTITEM);
				break;

			// 综合压力
			case '1':
				NFC_Pre();
				break;

			// 性能
			case '2':
				NFC_Ability();
				break;

			// 读写压力
			case '3':
				NFC_RW_Pre();
				break;
				
			// 挥卡测试
			case '4':
				NFC_Wave_Pre();
				break;
			
			// 异常测试
			case '5':
				NFC_Abnormal();
				break;

			// 退出
			case ESC:
				intentSys();
				return;

			default:
				break;
			}
		}
	}

	/**
	 * NFC综合压力
	 */
	public void NFC_Pre() 
	{
		/* private & local definition */
		int succ = 0, cnt = 0, bak = 0;// 成功次数，剩余测试次数，总次数
		int ret = NDK_ERR;// ret是读写成功失败标志
		// 初始化nfcAdapter
		NfcTool nfcTool = new NfcTool(myactivity);

		/* process body */
		// 弹出框设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT_VLE));
		// 标志位设为TRUE，一直等待，直到弹出框发生点击事件，使标志位变为false，结束while循环，继续执行while之后的程序，此时也可得到弹出框中用户设置的压力测试次数，通过packet.setLifecycle();
		bak = cnt = packet.getLifecycle();
		gui.cls_show_msg("请在感应区放置支持取随机数的%s卡，完成任意键继续", nfc_card);
		// 进行一次综合压力测试，上电-读写-下电流程测试
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(1, "压力测试中...\n还剩%d次（已成功%d次），【取消】退出测试...",cnt, succ)==ESC)
			{
				nfcTool.nfcDisEnableMode();
				break;
			}
				
			cnt--;
			// nfc连接操作，上电,READER_FLAG为不同类型卡A,B,M1的标志
			if(nfcTool.nfcConnect(reader_flag)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "NFC_Pre", g_keeptime, "line %d:第%d次：上电失败ret=%s", Tools.getLineInfo(),bak - cnt, ret);
				continue;
			}
			// NFC读写操作
			try {
				if ((ret = nfcTool.nfcRw(nfc_card)) !=NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "NFC_Pre", g_keeptime, "line %d:第%d次：读写失败ret=%s", Tools.getLineInfo(),bak - cnt, ret);
					continue;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "NFC_Pre", g_keeptime, "line %d:第%d次：读写失败ret=%s", Tools.getLineInfo(),bak - cnt, ret);
				nfcTool.nfcDisEnableMode();
				continue;
			}
			nfcTool.nfcDisEnableMode();// 下电
			succ++;
		}
		nfcTool.nfcDisEnableMode();// 为防止进行while中的退出操作，进行下电操作，保护卡
		gui.cls_show_msg1_record(TAG, "NFC_Pre", g_time_0,"%s综合压力测试完成，已执行次数为%d，成功为%d次", nfc_card,bak - cnt, succ);
	}

	/**
	 * NFC读写压力
	 */
	public void NFC_RW_Pre()  
	{
		/* private & local definition */
		int succ = 0, cnt = 0, bak = 0;// 成功次数，剩余测试次数，总次数
		int ret = NDK_ERR;// ret是读写成功失败标志
		// 初始化nfcAdapter
		NfcTool nfcTool = new NfcTool(myactivity);

		/* process body */
		// 弹出框设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT_VLE));
		// 标志位设为TRUE，一直等待，直到弹出框发生点击事件，使标志位变为false，结束while循环，继续执行while之后的程序，此时也可得到弹出框中用户设置的压力测试次数，通过packet.setLifecycle();
		bak = cnt = packet.getLifecycle();
		gui.cls_show_msg("请在感应区放置支持取随机数的%s卡，完成任意键继续", nfc_card);
		/* 等待弹出框发生点击事件后继续执行 */
		// 进行读写压力测试，上电-读写（循环）-下电测试
		// nfc连接操作，上电,READER_FLAG为不同类型卡A,B,M1的标志
		if(nfcTool.nfcConnect(reader_flag)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "NFC_Pre", g_keeptime, "line %d:第%d次：上电失败ret=%s", Tools.getLineInfo(),bak - cnt, ret);
			nfcTool.nfcDisEnableMode();
			return;
		}
		while (cnt > 0) 
		{
			if(gui.cls_show_msg1(1, "读写压力测试中...\n还剩%d次（已成功%d次），【取消】退出测试...", cnt, succ)==ESC)
			{
				nfcTool.nfcDisEnableMode();
				break;
			}
			cnt--;
			// NFC读写操作
			try {
				if ((ret = nfcTool.nfcRw(nfc_card)) !=NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "NFC_RwPress", g_keeptime, "line %d:第%d次：读写失败ret=%s", Tools.getLineInfo(),bak - cnt, ret);
					continue;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "NFC_RwPress", g_keeptime, "line %d:第%d次：读写失败ret=%s", Tools.getLineInfo(),bak - cnt, ret);
				nfcTool.nfcDisEnableMode();
				continue;
			}
			succ++;
		}
		nfcTool.nfcDisEnableMode();// 下电
		gui.cls_show_msg1_record(TAG, "NFC_RwPress",g_time_0, "%s读写压力测试完成，已执行次数为%d，成功为%d次", nfc_card,bak - cnt, succ);
	}

	/**
	 * NFC性能测试
	 */
	public void NFC_Ability() 
	{
		/* private & local definition */
		int count = 0;
		float fTotalTime = 0;
		long oldTime = 0;
		String str;
		// 初始化nfcAdapter
		NfcTool nfcTool = new NfcTool(myactivity);
		gui.cls_show_msg("请在感应区放置支持取随机数的%s卡，完成任意键继续", nfc_card);
		if(nfcTool.nfcConnect(reader_flag)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "NFC_Ability", g_keeptime, "line %d:上电失败", Tools.getLineInfo());
			nfcTool.nfcDisEnableMode();
			return;
		}
		count = 0;
		// 进行读性能测试，得到读写状态的时间
		while(true)
		{
			oldTime = System.currentTimeMillis();//System.currentTimeMillis()方法一般用于获取某个方法或其它的执行时间差，在开始前获取一次，在结束时获取一次，结束时间减去开始时间，得到执行时间
			try {
				if(nfcTool.nfcRw(nfc_card) == NDK_OK)
				{
					count++;
					// 成功时间累加
					fTotalTime = fTotalTime + (float)((System.currentTimeMillis()-oldTime)/1000.0);
					if(fTotalTime>TEST_TIME)
					{
						nfcTool.nfcDisEnableMode();
						break;
					}
				}
				else
				{
					//如果读写失败，则下电重新上电，再次进行读写操作
					nfcTool.nfcDisEnableMode();
					nfcTool.nfcConnect(reader_flag);
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "NFC_Ability",g_keeptime, "line %d:%s读写测试失败",Tools.getLineInfo(),TAG);
				nfcTool.nfcDisEnableMode();
				return;
			}
		}
		// 下电
		nfcTool.nfcDisEnableMode();
		
		// 修改A、B卡随机数获取的长度值，改为根据卡片获取 by zhengxq 20161116
		float value = count*(nfc_card == Nfc_Card.NFC_M1?LEN_BLKDATA:nfcTool.getApduLen())/fTotalTime;
		str = String.format(Locale.CHINA,"%d.%06d", (int)value,(int)((value-(int)value)*1000000));//%06d是指保留6位数
		if(fTotalTime>TEST_TIME)
			gui.cls_show_msg1_record(TAG, "NFC_Ability",g_time_0,"%s每秒读卡：%s字节",nfc_card ,str);
		else 
			gui.cls_show_msg1_record(TAG, "NFC_Ability",g_time_0,"line %d：%s累计成功读写时间不足10s（fTotalTime = %f）", Tools.getLineInfo(),nfc_card,fTotalTime);
	}
	
	/**
	 * NFC挥卡测试
	 */
	public void NFC_Wave_Pre()
	{
		/*private & local definition*/
		int ret = 0,cnt = 200,bak = 0,succ = 0;
		boolean  flag = false;
		NfcTool nfcTool = new NfcTool(myactivity);
		
		/*process body*/
		final PacketBean packet = new PacketBean();
		
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 200));
		bak = cnt = packet.getLifecycle();
		gui.cls_show_msg1(2, "挥卡测试时尝试将卡片从不同的方向移入感应区");
		while(cnt>0)
		{
			// 保护动作
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(1, "请在3秒内挥%s卡，还剩%d次（已成功%d次），【取消】退出测试...", nfc_card,cnt,succ)==ESC)
			{
				nfcTool.nfcDisEnableMode();
				break;
			}
			cnt--;
			// 上电
			if((ret = nfcTool.nfcConnect(reader_flag)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "NFC_Wave_Pre",g_keeptime, "line %d：第%d次：连接失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			for (int j = 0; j < 3; j++) 
			{
				// apdu读写
				try {
					if((ret = nfcTool.nfcRw(nfc_card))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "NFC_Wave_Pre",g_keeptime, "line %d：第%d次：读写失败（%d）（j = %d）", Tools.getLineInfo(),bak-cnt,ret,j);
						flag  = true;
						break;
					}
				} catch (IOException e) 
				{
					gui.cls_show_msg1_record(TAG, "NFC_Wave_Pre",g_keeptime, "line %d：第%d次：读写失败（%d）（j = %d）", Tools.getLineInfo(),bak-cnt,ret,j);
					nfcTool.nfcDisEnableMode();
					flag  = true;
					break;
				}
			}
			if(flag)
			{
				flag= false;
				continue;
			}
			// 下电操作
			nfcTool.nfcDisEnableMode();
			gui.cls_show_msg1(1, "请在3秒内将%s卡移出感应区", nfc_card);
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "NFC_Wave_Pre",g_time_0, "挥卡压力测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
	}

	/**
	 * NFC异常测试，K21端深休眠唤醒后使用NFC模块
	 */
	public void NFC_Abnormal() 
	{
		/* private & local definition */
		NfcTool nfcTool = new NfcTool(myactivity);
		int ret = -1;
		
		/* process body */
		// 休眠之前进行2次取随机数操作
		gui.cls_show_msg("放置支持取随机数的B卡或身份证B卡，完成任意键继续");
		if((ret = nfcTool.nfcConnect(reader_flag))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "NFC_Abnormal", g_keeptime, "line %d:连接失败ret=%d", Tools.getLineInfo(), ret);
			nfcTool.nfcDisEnableMode();
			return;
		}
		for (int i = 0; i < 2; i++) 
		{
			try 
			{
				if((ret = nfcTool.nfcRw(nfc_card))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "NFC_Abnormal", g_keeptime, "line %d:测试失败ret=%s", Tools.getLineInfo(), ret);
					return;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "NFC_Abnormal", g_keeptime, "line %d:测试失败ret=%s", Tools.getLineInfo(), ret);
				nfcTool.nfcDisEnableMode();
				return;
			}
		}
		gui.cls_show_msg("操作步骤：按电源键进入休眠，确保设备进入深休眠再唤醒设备，点任意键开始测试");
		GlobalVariable.isWakeUp = false;
		while(!GlobalVariable.isWakeUp);
		// 休眠唤醒后进行2次取随机数操作
		for (int i = 0; i < 2; i++) 
		{
			try 
			{
				if((ret = nfcTool.nfcRw(nfc_card))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "NFC_Abnormal", g_keeptime, "line %d:测试失败(ret=%s)", Tools.getLineInfo(), ret);
					nfcTool.nfcDisEnableMode();
					return;
				}
			} catch (IOException e) 
			{
				gui.cls_show_msg1_record(TAG, "NFC_Abnormal", g_keeptime, "line %d:测试失败(ret=%s)", Tools.getLineInfo(), ret);
				nfcTool.nfcDisEnableMode();
				return;
			}
		}
		nfcTool.nfcDisEnableMode();// 下电
		gui.cls_show_msg1_record(TAG, "NFC_Abnormal",g_time_0, "NFC异常测试通过");
	}
}
