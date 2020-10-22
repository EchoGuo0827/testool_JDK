package com.example.highplattest.systest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.NfcTool;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest64.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160901
 * directory 		: 
 * description 		: NFC/ICSAM交叉
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		  20160901	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest64 extends DefaultFragment
{
	private final String TAG = SysTest64.class.getSimpleName();
	private final String TESTITEM = "NFC/ICSAM";
	private Nfc_Card nfc_Card = Nfc_Card.NFC_B;
	private EM_ICTYPE ic_sam = EM_ICTYPE.ICTYPE_IC;
	private List<EM_ICTYPE> icSamList = new ArrayList<EM_ICTYPE>();
	private Gui gui = null;
	private Config config;
	
	//测试主程序
	public void systest64() 
	{
		gui = new Gui(myactivity, handler);
		// 初始化处理器，连接K21设备
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			nfc_Card = nfc_config(handler, TESTITEM);
			icSamList=config.conf_icsam();
			for(EM_ICTYPE icSamChoose:icSamList){
				if (GlobalVariable.sdkType==SdkType.SDK3&&icSamChoose == EM_ICTYPE.ICTYPE_IC)
					continue;
				ic_sam=icSamChoose;
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
			}
			
			return;
		}
		// 测试主入口
		while (true) 
		{
			int returnValue=gui.cls_show_msg("NFC/ICSAM\n0.ICSAM配置\n1.NFC配置\n2.交叉测试");
			switch (returnValue) 
			{	
			case '0':
				ic_sam_config();
				break;
				
			case '1':
				nfc_Card = nfc_config(handler, TESTITEM);
				break;
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
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
	 * NFC/RF交叉测试
	 */
	public void cross_test()
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		byte[] psAtrBuf=new byte[20];
		int[] pnAtrLen = new int[1];
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		NfcTool nfcTool = new NfcTool(myactivity);
		if (ic_sam == EM_ICTYPE.ICTYPE_IC)
		{
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), icclistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:icc事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
		}
		gui.cls_show_msg("请确保插入" + ic_sam + "卡及放入" + nfc_Card + "卡，完成任意键继续");
		
		while(cnt>0)
		{
			// 保护动作
			icSamPowerOff(ic_sam);
			nfcTool.nfcDisEnableMode();
			if(gui.cls_show_msg1(3, "%s/%s交叉测试，已执行%d次，成功%d次，【取消】退出测试", ic_sam,nfc_Card,bak-cnt,succ)==ESC)
				break;
			if (GlobalVariable.sdkType==SdkType.SDK3&&ic_sam == EM_ICTYPE.ICTYPE_IC)
			    gui.cls_show_msg("请插拔"+ic_sam+"卡，完成任意键继续");
			cnt--;
			// 上电
			if((ret =nfcTool.nfcConnect(reader_flag))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡连接失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			if((ret = iccPowerOn(ic_sam,psAtrBuf,pnAtrLen))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡上电失败（%d）",Tools.getLineInfo(),bak-cnt, ic_sam,ret);
				continue;
			}
			// 读写
			try 
			{
				if((ret = nfcTool.nfcRw(nfc_Card))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡APDU测试失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
					continue;
				}
			} catch (IOException e) {
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡APDU测试失败（%d）", Tools.getLineInfo(),bak-cnt,nfc_Card,ret);
				continue;
			}
			
			if((ret = iccRw(ic_sam, req,null))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡APDU测试失败（%d）", Tools.getLineInfo(),bak-cnt,ic_sam,ret);
				continue;
			}
			nfcTool.nfcDisEnableMode();
			icSamPowerOff(ic_sam);
			succ++;
		}
		//解绑事件
		if(ic_sam==EM_ICTYPE.ICTYPE_IC)
		{
			if((ret=UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d：%s事件解绑失败(%d)", Tools.getLineInfo(),ic_sam,ret);
				return;
			}
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "(%s/%s)交叉测试完成，已执行次数为%d，成功为%d次", ic_sam,nfc_Card,bak-cnt,succ);
	}
	
	/**
	 * ic_sam配置
	 */
	public void ic_sam_config() 
	{
		//smart配置
		ic_sam = config.conf_icsam().get(0);
	}
}
