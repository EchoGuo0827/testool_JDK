package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150303
 * directory 		: 
 * description 		: ICSAM压力、性能
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20150303
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest3 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest3.class.getSimpleName();
	private final String TESTITEM = "IC性能、压力";
	private final int testTime = 10;
	private final int DEFAULT_CNT_VLE = 15000;//读写压力
	private final int DEFAUL_CNT_VLE2 = 3000;//综合压力
	
	/*----------global variables declaration------------------------*/
	private List<EM_ICTYPE> type = GlobalVariable.cardNo;
	private int ICSAM_NUM;
	private EM_ICTYPE[] slot = null;
	Gui gui = null;
	
	ArrayList<byte[]> powerUpList = new ArrayList<byte[]>();
	ArrayList<byte[]> powerDownList = new ArrayList<byte[]>();
	ArrayList<byte[]> rwList = new ArrayList<byte[]>();
	private Config config;
	public void systest3()
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		powerUpList.add(DataConstant.Icc_E103);
		powerUpList.add(DataConstant.Icc_E103_sam1);
		powerDownList.add(DataConstant.Icc_E104);
		powerDownList.add(DataConstant.Icc_E104_sam1);
		rwList.add(DataConstant.Icc_E105);
		rwList.add(DataConstant.Icc_E105_sam1);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			List<EM_ICTYPE> icSamList=new ArrayList<EM_ICTYPE>();
			ICSAM_NUM=icSamList.size();
			slot = new EM_ICTYPE[ICSAM_NUM];
			icSamList = config.conf_icsam();
			int i=0;
			for (EM_ICTYPE icSamChoose:icSamList) 
			{
				slot[i] = icSamChoose;
				i++;
			}
			g_CycleTime =DEFAUL_CNT_VLE2;
			icPre();
			g_CycleTime =DEFAULT_CNT_VLE;
			icRwPre();
			icFunction();
			return;
		}
		ICSAM_NUM = GlobalVariable.cardNo.size();
		slot = new EM_ICTYPE[ICSAM_NUM];
		for (int i = 0; i < ICSAM_NUM; i++) 
		{
			slot[i] = GlobalVariable.cardNo.get(i);
		}
		
		// 插上所有的ic/sam卡提示
		gui.cls_show_msg("请插上所有IC/SAM卡，完成任意键继续");
		while(true)
		{
			int returnValue=gui.cls_show_msg("IC/SAM综合测试\n0.IC/SAM综合压力\n1.IC/SAM读写压力\n2.IC/SAM性能测试\n3.IC/SAM异常测试\n4.IC/SAM深交叉\n");
			switch (returnValue) 
			{
					
			case '0':
				icPre();
				break;

			case '1':
				icRwPre();
				break;
				
			case '2':
				icFunction();
				break;
				
			case '3':
				icAbnormal();
				break;
				
			case '4':
				icSam();
				break;
				
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	
	//icsam深交叉，暂时没确定各种机器支持的icsam数量，后续通过宏定义来修改
	private void icSam() 
	{
		int returnValue='0';
		if(type.size()>1)
		{
			StringBuffer strBuffer = new StringBuffer();
			for (int i = 1; i < type.size(); i++) 
			{
				strBuffer.append(i+".IC/"+type.get(i).toString().substring(7)+"\n");
			}
			returnValue = gui.cls_show_msg("IC/SAM深交叉\n%s",strBuffer.toString());
		}
		else
		{
			gui.cls_show_msg("只支持%s卡,无法进行IC/SAM深交叉测试,任意键继续",type.get(0));
			return;
		}
			
		
		switch (returnValue) 
		{
		case '1':
			icSAM1();
			break;

		case '2':
			icSAM2();
			break;
			
		case ESC:
			return;
		}
	}


	public void icSAM1() 
	{
		int ret = 0;
		byte[] psAtrBuf = new byte[10];
		int[] pnAtrLen = new int[1];
		//ic、sam上电
		//注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), icclistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "icSAM1", g_keeptime, "line %d:icc事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		if((ret = iccPowerOn(EM_ICTYPE.ICTYPE_IC,psAtrBuf,pnAtrLen)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM1",g_keeptime,"line %d:IC上电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = iccPowerOn(EM_ICTYPE.ICTYPE_SAM1,psAtrBuf,pnAtrLen)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1);
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM1",g_keeptime, "line %d:SAM1上电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		//ic卡读写，下电
		if((ret = iccRw(EM_ICTYPE.ICTYPE_IC,req,null)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1);
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM1",g_keeptime, "line %d:IC读写失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_IC)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1);
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM1",g_keeptime,"line %d:下电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "icSAM1", g_keeptime, "line %d:icc事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		//sam卡读写、下电
		if((ret = iccRw(EM_ICTYPE.ICTYPE_SAM1,req,null)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1);
			gui.cls_show_msg1_record(TAG, "icSAM1",g_keeptime, "line %d:读写失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM1);
			gui.cls_show_msg1_record(TAG, "icSAM1",g_keeptime, "line %d:下电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "icSAM1",g_time_0, "ICSAM1深交叉测试通过");
	}
	
	public void icSAM2() 
	{
		int ret = 0;
		byte[] psAtrBuf = new byte[10];
		int[] pnAtrLen = new int[1];
		// 注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), icclistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "icSAM2", g_keeptime, "line %d:icc事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		//ic、sam上电
		if((ret = iccPowerOn(EM_ICTYPE.ICTYPE_IC,psAtrBuf,pnAtrLen)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM2",g_keeptime, "line %d:上电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = iccPowerOn(EM_ICTYPE.ICTYPE_SAM2, psAtrBuf,pnAtrLen)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2);
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM2",g_keeptime, "line %d:上电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		//ic卡读写，下电
		if((ret = iccRw(EM_ICTYPE.ICTYPE_IC,req,null)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2);
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM2",g_keeptime,"line %d:读写失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_IC)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2);
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			gui.cls_show_msg1_record(TAG, "icSAM2",g_keeptime, "line %d:下电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "IccUnRegisterEventTask", g_keeptime, "line %d:icc事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		//sam卡读写、下电
		if((ret = iccRw(EM_ICTYPE.ICTYPE_SAM2,req,null)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2);
			gui.cls_show_msg1_record(TAG, "icSAM2",g_keeptime, "line %d:读写失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2)) != NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_SAM2);
			gui.cls_show_msg1_record(TAG, "icSAM2",g_keeptime,"line %d:下电失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "icSAM2",g_time_0, "ICSAM2深交叉测试通过");
	}
	
	private void icPre()
	{
		int nkeyIn=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++nkeyIn == (!GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?'1':'2'))// Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "icPre", 2, "%s连续综合压力测试结束",type );
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行%s连续综合压力测试,[取消]退出",type)==ESC)
					return;
			}
			else
			{
				//Poynt和X5不支持mpos,直接进行jni压力测试
				nkeyIn=gui.cls_show_msg("IC/SAM压力\n0.流程压力(jni)\n%s",GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"1.流程压力(mpos)":"");
			}
			switch (nkeyIn) 
			{
				case '0':
					icJniPre();
					break;
				
				case '1':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						icMposPre();
					break;

				case ESC:
					return;
			}
		}
	}
	
	//IC/SAM综合压力
	private void icJniPre() 
	{
		int i = 0,ret = 0,succ = 0;
		byte[] buf = new byte[128];
		int cnt = DEFAUL_CNT_VLE2,bak = 0;
		byte[] result1 = {0x6d,0x00};
		byte[] result2 = {(byte) 0x90,0x00};
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		byte[] resultCode = new byte[2];
		
		/*process body*/
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag){
			packet.setLifecycle(getCycleValue());
		}else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAUL_CNT_VLE2));
		bak = cnt = packet.getLifecycle();
		
		do
		{
			// 再次初始化
			cnt = bak;
			succ = 0;
			while(cnt>0)
			{
				icSamPowerOff(type.get(i));
				if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "%s综合压力测试中，还剩%d次(已成功%d次),[取消]退出测试...", type.get(i),cnt,succ)==ESC)
					return;
				cnt--;
				//注册
				if(type.get(i)==EM_ICTYPE.ICTYPE_IC)
				{
					if((ret=RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),icclistener))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "icSyPpre",g_keeptime, "line %d:icc事件注册失败(%d)", Tools.getLineInfo(),ret);
						return;
					}
				}
				// 上电
				if((ret=iccPowerOn(type.get(i), psAtrBuf,pnAtrLen)) != NDK_OK)
				{
					icSamPowerOff(type.get(i));
					if(type.get(i)==EM_ICTYPE.ICTYPE_IC)
					    UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
					gui.cls_show_msg1_record(TAG, "icSyPpre", g_keeptime,"line %d:第%d次：%s上电失败（%d）", Tools.getLineInfo(),bak-cnt, type.get(i),ret);
					continue;
				}
				Arrays.fill(buf, (byte) 0);
				// 读写测试
				if((ret = iccRw( type.get(i),req,resultCode))!=NDK_OK|| (Arrays.equals(resultCode, result1)==false&& Arrays.equals(resultCode, result2)==false))
				{
					icSamPowerOff(type.get(i));
					if(type.get(i)==EM_ICTYPE.ICTYPE_IC)
					    UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
					gui.cls_show_msg1_record(TAG, "icSyPpre", g_keeptime,"line %d:第%d次：%s读卡失败"+ISOUtils.hexString(resultCode), Tools.getLineInfo(),bak-cnt, type.get(i));
					continue;
				}
				// 下电
				if((ret = icSamPowerOff(type.get(i)))!=NDK_OK)
				{
					icSamPowerOff(type.get(i));
					if(type.get(i)==EM_ICTYPE.ICTYPE_IC)
					    UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
					gui.cls_show_msg1_record(TAG, "icSyPpre",g_keeptime, "line %d:第%d次：%s下电失败（%d）", Tools.getLineInfo(), bak-cnt,type.get(i),ret);
					continue;
				}
				//解绑事件
				if(type.get(i)==EM_ICTYPE.ICTYPE_IC)
				{
					if((ret=UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue()))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "icSyPpre",g_keeptime, "line %d:icc事件解绑失败(%d)", Tools.getLineInfo(),ret);
						return;
					}
				}
				succ++;
			}
			ret = gui.cls_show_msg1_record(TAG, "icSyPpre", 2,"%s压力测试完成，已执行次数为%d，成功%d次",  type.get(i),bak-cnt,succ);
			if(ret == ESC)
			{
				return;
			}
		}
		while(++i!=ICSAM_NUM);
	}
	
	private void icRwPre()
	{
		int nkeyIn=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++nkeyIn == (!GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?'1':'2'))// Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "icRwPre", 2, "%s连续读写压力测试结束", type);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行%s连续读写压力测试,[取消]退出",type)==ESC)
					return;
					
			}
			else
			{
				nkeyIn=gui.cls_show_msg("IC读写压力\n0.读写压力(jni)\n%s",
						GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"1.读写压力(mpos)":"");// Poynt和X5不支持mpos,直接进行jni压力测试
			}
			switch (nkeyIn) 
			{
				case '0':
					icJniRwPre();
					break;
				
				case '1':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						icMposRwPre();
					break;

				case ESC:
					return;
			}
		}
	}
	
	//IC/SAM读写压力
	private void icJniRwPre() 
	{
		/*private & local definition*/
		int i = 0,ret = 0,succ = 0;
		int cnt = DEFAULT_CNT_VLE,bak = 0;
		byte[] result1 = {0x6d,0x00};
		byte[] result2 = {(byte) 0x90,0x00};
		byte[] resultCode = new byte[2];
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		//gui.setOverFlag(true);
		
		/*process body*/
		// 压力次数设置
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag){
			packet.setLifecycle(getCycleValue());
		}else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE));
		bak = cnt = packet.getLifecycle();
		
		do
		{
			// 再次初始化
			cnt = bak;
			succ = 0;
			//注册
			if(slot[i]==EM_ICTYPE.ICTYPE_IC)
			{
				if((ret=RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),icclistener))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "IccRegisterEventTask",g_keeptime, "line %d:%s事件注册失败(%d)", Tools.getLineInfo(),slot[i],ret);
					return;
				}
			}
			// 上电
			if ((ret = iccPowerOn(slot[i], psAtrBuf,pnAtrLen)) != NDK_OK) 
			{
				cnt--;
				icSamPowerOff(slot[i]);
				if(slot[i]==EM_ICTYPE.ICTYPE_IC)
				    UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icRwPre",g_keeptime,"line %d:第%d次：%s上电失败（%d）", Tools.getLineInfo(), bak - cnt,slot[i], ret);
				continue;
			}
			while (cnt > 0) 
			{
				if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS,"%s读写压力测试中，还剩%d次(已成功%d次),[取消]退出测试...", slot[i],cnt,succ)==ESC)
					break;
			
				cnt--;
				if ((ret = iccRw(slot[i],req,resultCode))!=NDK_OK|| (!Arrays.equals(resultCode, result1) && !Arrays.equals(resultCode, result2))) 
				{
//					icSamPowerOff(slot[i]);
					gui.cls_show_msg1_record(TAG, "icRwPre",g_keeptime,"line %d:第%d次：%s读卡失败（%d,%s）", Tools.getLineInfo(), bak- cnt,  slot[i], ret,ISOUtils.hexString(resultCode));
					break;
				}
				succ++;
			}
//			if(GlobalVariable.gSequencePressFlag)
//				--i;
			// 下电
			if((ret = icSamPowerOff(slot[i]))!=NDK_OK)
			{
				icSamPowerOff(slot[i]);
				if(slot[i]==EM_ICTYPE.ICTYPE_IC)
				    UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icRwPre",g_keeptime, "line %d:%s下电失败（%d）", Tools.getLineInfo(), slot[i],ret);
				continue;
			}
			//解绑事件
			if(slot[i]==EM_ICTYPE.ICTYPE_IC)
			{
				if((ret=UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue()))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "IccUnRegisterEventTask",g_keeptime, "line %d：%s事件解绑失败(%d)", Tools.getLineInfo(),slot[i],ret);
					return;
				}
			}
			gui.cls_show_msg1_record(TAG, "icRwPre",2, "%s读写压力完成,已执行次数为%d,成功%d次",  slot[i],bak-cnt,succ);
			i++;
		}while(i<ICSAM_NUM);
	}
	
	//IC/SAM性能测试
	public void icFunction() 
	{
		/*private & local definition*/
		int ret = -1,i = 0,count = 0;
		byte[] buf = new byte[128];
		long oldTime;
		float time;
//		byte[] sendBuf = {0x00,(byte) 0x84,0x00,0x00,0x08};
		byte[] result1 = {0x6d,0x00};
		byte[] result2 = {(byte) 0x90,0x00};
		byte[] resultCode = new byte[2];
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		
		/*process body*/
		do
		{
			//注册
			if(slot[i]==EM_ICTYPE.ICTYPE_IC)
			{
				if((ret=RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),icclistener))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "icFunction",g_keeptime, "line %d:%s事件注册失败(%d)", Tools.getLineInfo(),slot[i],ret);
					return;
				}
			}
			// 上电
			if((ret = iccPowerOn(slot[i], psAtrBuf,pnAtrLen))!=NDK_OK)
			{
				icSamPowerOff(slot[i]);
				if(slot[i]==EM_ICTYPE.ICTYPE_IC)
				    UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icFunction", g_keeptime,"line %d:%s上电失败(%d)", Tools.getLineInfo(),slot[i],ret);
				continue;
			}
			
			// 读取随机数
			gui.cls_show_msg1(2, "正在测试call对%s读写速度...", slot[i]);
			count = 0;
			time = 0;
			Arrays.fill(buf, (byte) 0);
			oldTime = System.currentTimeMillis();
			while((ret = iccRw( slot[i],req,resultCode)) ==NDK_OK&& (Arrays.equals(resultCode, result1) || Arrays.equals(resultCode, result2)))
			{
				count++;
				if((time = Tools.getStopTime(oldTime))>testTime)
					break;
			}
			// 下电
			if ((ret = icSamPowerOff(slot[i])) != NDK_OK) 
			{
				icSamPowerOff(slot[i]);
				if (slot[i] == EM_ICTYPE.ICTYPE_IC)
					UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icFunction", g_keeptime, "line %d:%s下电失败(%d)", Tools.getLineInfo(),slot[i], ret);
				continue;
			}
			// 解绑事件
			if (slot[i] == EM_ICTYPE.ICTYPE_IC) 
			{
				if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "icFunction", g_keeptime, "line %d：%s事件解绑失败(%d)", Tools.getLineInfo(),slot[i], ret);
					return;
				}
			}
			//计算性能值
			if(time>testTime)
			{
				// 修改性能取值，原先默认为8，现修改为IC卡实际获取到的值 by zhengxq 20161116
				time = count*apduLen/time;
				gui.cls_show_msg1_record(TAG, "icFunction",g_time_0, "call每秒读%s卡：%4.2f字节",slot[i],time);
			}
			else
				gui.cls_show_msg1_record(TAG, "icFunction", g_keeptime, "line %d:累积成功读写时间不足10秒（%s卡，time = %f）", Tools.getLineInfo(),slot[i],time);
			
		}while(++i != ICSAM_NUM);
		
	}
	
	//IC/SAM异常测试
	public void icAbnormal()
	{
		int i = 0,ret = 0;
		byte[] result1 = {0x6d,0x00};
		byte[] result2 = {(byte) 0x90,0x00};
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		int[] pnSta = new int[1];
		byte[] resultCode = new byte[2];
		
		gui.cls_show_msg1(2, "正在进行IC卡异常测试");
		// 初始化
		for (i = 0; i < 2; i++) 
		{
			gui.cls_show_msg("请正向（正确方向）插入IC卡...完成任意键继续");
			// 插入IC卡
			while(true)
			{
				
				if((ret = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime, "line %d:第%d次：探测失败（ret = %d）", Tools.getLineInfo(),i+1,ret);
					return;
				}
				if(pnSta[0] == 0x01)
					break;
				SystemClock.sleep(1000);
			}
			//注册
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), icclistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal", g_keeptime, "line %d:%s事件注册失败(%d)",Tools.getLineInfo(), slot[i], ret);
				return;
			}
			if ((ret = iccPowerOn(EM_ICTYPE.ICTYPE_IC,psAtrBuf,pnAtrLen)) != NDK_OK) 
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime, "line %d:第%d次：IC卡上电失败（%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}
			SystemClock.sleep(1000);
			// 发随机数
			if ((ret = iccRw(EM_ICTYPE.ICTYPE_IC,req,resultCode)) != NDK_OK|| (!Arrays.equals(resultCode, result1) && !Arrays.equals(resultCode, result2))) 
			{
				icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime,"line %d:第%d次：IC读卡失败（%d，%s）", Tools.getLineInfo(), i + 1,ret, ISOUtils.hexString(resultCode));
				return;
			}
			// 下电操作
			if ((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_IC)) != NDK_OK) 
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime, "line %d:第%d次：IC卡下电失败（%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}
			// 解绑事件
			if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal", g_keeptime, "line %d：%s事件解绑失败(%d)",Tools.getLineInfo(), slot[i], ret);
				return;
			}
			gui.cls_show_msg("请将卡拔出，按任意键继续");
			if ((ret=JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK||pnSta[0]!=0x00) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime,"line %d:第%d次：IC卡未拔出（ret=%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}
			if ((ret = icSamPowerOff(EM_ICTYPE.ICTYPE_IC)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime, "line %d:第%d次：IC卡下电失败（%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}
			if ((ret = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK||pnSta[0]!=0x00) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime,"line %d:第%d次：IC卡未拔出（ret=%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}

			// 错误插卡上电
			gui.cls_show_msg("请反向插入IC卡...点任意键继续");
			while (true) 
			{
				if ((ret=JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime,"line %d:第%d次：探测失败（ret=%d）",Tools.getLineInfo(), i + 1, ret);
					return;
				}
				if(pnSta[0]==0x01)
					break;
				SystemClock.sleep(1000);
			}
			//注册
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), icclistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal", g_keeptime, "line %d:%s事件注册失败(%d)",Tools.getLineInfo(), slot[i], ret);
				return;
			}
			if ((ret = iccPowerOn(EM_ICTYPE.ICTYPE_IC,psAtrBuf,pnAtrLen)) == NDK_OK) 
			{
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime, "line %d:第%d次：IC上电应失败（%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}
			// 错误插卡下电
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			// 解绑事件
			if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal", g_keeptime, "line %d：%s事件解绑失败(%d)",Tools.getLineInfo(), slot[i], ret);
				return;
			}
			gui.cls_show_msg("请将卡拔出，完成点任意键继续");
			if ((ret = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK||pnSta[0]!=0x00) 
			{
				gui.cls_show_msg1_record(TAG, "icAbnormal",g_keeptime,"line %d:第%d次：IC卡未拔出（%d）",Tools.getLineInfo(), i + 1, ret);
				return;
			}
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
		}
		gui.cls_show_msg1_record(TAG, "icAbnormal",g_time_0, "IC卡异常测试通过");
	}
	
	//IC/SAM综合压力(mpos)
	public void icMposPre() 
	{
		/*private & local definition*/
		int i = 0,ret = 0,succ = 0,len=0;
		int cnt = DEFAUL_CNT_VLE2,bak = 0;
		
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		K21DeviceResponse response;
		byte[] retContent;
		String retCode;
		
		/*process body*/
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAUL_CNT_VLE2));
		bak = cnt = packet.getLifecycle();
		
		do
		{
			// 再次初始化
			cnt = bak;
			succ = 0;
			
			while(cnt>0)
			{
				if(gui.cls_show_msg1(1, "%s综合压力测试中，还剩%d次(已成功%d次),[取消]退出测试...", type.get(i),cnt,succ)==ESC)
					break;
				cnt--;
				// 上电
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(powerUpList.get(i)), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{
					gui.cls_show_msg1_record(TAG, "icSyPpre_mpos", g_keeptime,"line %d:第%d次：%s上电失败（%s）", Tools.getLineInfo(),bak-cnt, type.get(i),retCode);
					continue;
				}
				// 读写测试
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(rwList.get(i)), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{	
					k21ControllerManager.sendCmd(new K21DeviceCommand(powerDownList.get(i)), null);
					gui.cls_show_msg1_record(TAG, "icSyPpre_mpos", g_keeptime,"line %d:第%d次：%s读卡失败（%s）", Tools.getLineInfo(),bak-cnt, type.get(i),retCode);
					continue;
				}
				//获取应答数据长度
				try {
				    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
				} catch (NumberFormatException e) {
				    e.printStackTrace();
				}
				if(ISOUtils.hexString(retContent, 9+len, 2).equals("9000")==false && ISOUtils.hexString(retContent, 9+len, 2).equals("6D00")==false)
				{
					gui.cls_show_msg1_record(TAG, "icSyPpre_mpos", g_keeptime,"line %d:第%d次：%s取随机数校验失败(sw = %s)", Tools.getLineInfo(),bak-cnt, 
							type.get(i),ISOUtils.hexString(retContent, 11, 2));
					return;
				}
				// 下电
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(powerDownList.get(i)), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{
					gui.cls_show_msg1_record(TAG, "icSyPpre_mpos",g_keeptime, "line %d:第%d次：%s下电失败（%s）", Tools.getLineInfo(),bak-cnt, type.get(i),retCode);
					continue;
				}
				succ++;
			}
			ret = gui.cls_show_msg1_record(TAG, "icSyPpre_mpos", 2,"%s压力测试完成，已执行次数为%d，成功%d次",  type.get(i),bak-cnt,succ);
			if(ret == ESC)
			{
				k21ControllerManager.close();
				return;
			}
		}while(++i<powerUpList.size());
		k21ControllerManager.close();
	}
	
	//IC/SAM读写压力(mpos)
	public void icMposRwPre() 
	{
		/*private & local definition*/
		int i = 0,ret = 0,succ = 0,len=0;
		int cnt = DEFAULT_CNT_VLE,bak = 0;
		
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		K21DeviceResponse response;
		byte[] retContent;
		String retCode;
		
		/*process body*/
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		// 压力次数设置
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag){
			packet.setLifecycle(getCycleValue());
		}else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE));
		bak = cnt = packet.getLifecycle();
		
		do
		{
			// 再次初始化
			cnt = bak;
			succ = 0;
			while (cnt > 0) {
				// 上电
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(powerUpList.get(i)), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{
					cnt--;
					gui.cls_show_msg1_record(TAG, "icRwPre_mpos", g_keeptime,"line %d:第%d次：%s上电失败（%s）", Tools.getLineInfo(),bak-cnt, type.get(i),retCode);
					continue;
				}
				while (cnt > 0) 
				{
					if(gui.cls_show_msg1(1, "%s读写压力测试中，还剩%d次(已成功%d次),[取消]退出测试...", slot[i],cnt,succ)==ESC){
						k21ControllerManager.sendCmd(new K21DeviceCommand(powerDownList.get(i)), null);
						k21ControllerManager.close();
						gui.cls_show_msg1_record(TAG, "icRwPre_mpos",2, "%s读写压力完成,已执行次数为%d,成功%d次",  slot[i],bak-cnt,succ);
						return;
					}
				
					cnt--;
					// 读写测试
					response = k21ControllerManager.sendCmd(new K21DeviceCommand(rwList.get(i)), null);
					retContent = response.getResponse();
					retCode = ISOUtils.dumpString(retContent, 7, 2);
					if(!retCode.equals("00"))
					{	
						k21ControllerManager.sendCmd(new K21DeviceCommand(powerDownList.get(i)), null);
						gui.cls_show_msg1_record(TAG, "icRwPre_mpos", g_keeptime,"line %d:第%d次：%s读卡失败（%s）", Tools.getLineInfo(),bak-cnt, type.get(i),retCode);
						continue;
					}
					//获取应答数据长度
					try {
					    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
					} catch (NumberFormatException e) {
					    e.printStackTrace();
					}
					if(ISOUtils.hexString(retContent, 9+len, 2).equals("9000")==false && ISOUtils.hexString(retContent, 9+len, 2).equals("6D00")==false)
					{
						gui.cls_show_msg1_record(TAG, "icRwPre_mpos", g_keeptime,"line %d:第%d次：%s取随机数校验失败(sw = %s)", Tools.getLineInfo(),bak-cnt, 
								type.get(i),ISOUtils.hexString(retContent, 11, 2));
						return;
					}
					succ++;
				}
				// 下电
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(powerDownList.get(i)), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{
					gui.cls_show_msg1_record(TAG, "icRwPre_mpos",g_keeptime, "line %d:第%d次：%s下电失败（%s）", Tools.getLineInfo(),bak-cnt, type.get(i),retCode);
					continue;
				}
			}
			gui.cls_show_msg1_record(TAG, "icRwPre_mpos",2, "%s读写压力完成,已执行次数为%d,成功%d次",  slot[i],bak-cnt,succ);
			if(ret == ESC)
			{
				k21ControllerManager.close();
				return;
			}
		}while(++i<powerUpList.size());
		k21ControllerManager.close();
	}
}

