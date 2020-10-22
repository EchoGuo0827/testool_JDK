package com.example.highplattest.systest;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.MEMORY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest57.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150624
 * directory 		: 
 * description 		: Memory卡综合测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150906		created
 * 
 * 					变更										时间			人员
 * 修复N910欧洲1604卡读写错误问题。1604卡的测试地址是2005-2006，07D6写的是2006-2007，修改为07D5   20200824 	陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest57 extends DefaultFragment
{
	private final String TESTITEM = "Memory卡综合";
	private String TAG = SysTest57.class.getSimpleName();
	private MEMORY_TYPE type;
	private final int DEFAULT_COUNT = 100;
	private int succ = 0;
	private Gui gui = new Gui(myactivity, handler);
	private EM_ICTYPE[] cardId  = {EM_ICTYPE.ICTYPE_M_1_1,EM_ICTYPE.ICTYPE_M_1_2,EM_ICTYPE.ICTYPE_M_1_4,EM_ICTYPE.ICTYPE_M_1_8,
			EM_ICTYPE.ICTYPE_M_1_16,EM_ICTYPE.ICTYPE_M_1_32,EM_ICTYPE.ICTYPE_M_1_64,EM_ICTYPE.ICTYPE_M_2, 
			EM_ICTYPE.ICTYPE_M_3,EM_ICTYPE.ICTYPE_M_3,EM_ICTYPE.ICTYPE_M_4, EM_ICTYPE.ICTYPE_M_5, EM_ICTYPE.ICTYPE_M_6, EM_ICTYPE.ICTYPE_M_7};
	private K21DeviceResponse response;
	private K21ControllerManager k21ControllerManager;
	private String cmdUpDown,cmdRw;
	byte[] pack ,pack2,pack3;
	private byte[] retContent;
	
	public void systest57() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg("不支持mpos,JNI封装不需测试,只要测试NDK即可");
			intentSys();
			return;
		}
		//测试主入口
		while(true)
		{
			int returnVaule=gui.cls_show_msg("Memory卡综合\n0.综合压力测试\n1.读写压力测试\n3.配置\n");
			switch (returnVaule) 
			{	
			case '0': 
				memory_press(type);
				break;
				
			case '1': 
				memory_rw(type);
				break;
				
			/*case '2': 
				memory_abnormity(type);
				break;*/
				
			case '3': 
				type = memory_config();
				mpos_memory_config(type);
				gui.cls_show_msg("配置%s卡成功！", type);
				break;
				
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	//初始化构造对应mpos指令集
	private void mpos_memory_config(MEMORY_TYPE type) {
		cmdUpDown = "0000";
		switch (type) {
		case AT24C01:
		case AT24C02:
		case AT24C04:
		case AT24C08:
		case AT24C016:
		case AT24C032:
		case AT24C064:
			cmdUpDown="0006";
			cmdRw=cmdUpDown+"000900D000010431323334";
			break;
			
		case SLE4432_42:
			cmdUpDown="0007";
			cmdRw=cmdUpDown+"000900D000200401020304";
			break;
			
		case SLE4418_28:
			cmdUpDown="0008";
			cmdRw=cmdUpDown+"000900D002FF0431323334";
			break;
			
		case SLE5528:
			cmdUpDown="0008";
			cmdRw=cmdUpDown+"000900D000200432323334";
			break;
			
		case AT88SC102:
			cmdUpDown="0009";
			cmdRw=cmdUpDown+"000700D000B0023132";
			break;
			
		case AT88SC1604:
			cmdUpDown="000a";
			cmdRw=cmdUpDown+"000700D007D6023132";
			break;
			
		case AT88SC1608:
			cmdUpDown="000b";
			cmdRw=cmdUpDown+"000900D0000E0401020304";
			break;
			
		case AT88SC153:
			cmdUpDown="000d";
			cmdRw=cmdUpDown+"001700D000060C010203041112131421222324";
			break;

		default:
			break;
		}
		//Mpos IC上电指令
		pack = CalDataLrc.mposPack(new byte[]{(byte) 0xE1,0x03},ISOUtils.hex2byte(cmdUpDown));
		//下电
		pack2 = CalDataLrc.mposPack(new byte[]{(byte) 0xE1,0x04},ISOUtils.hex2byte(cmdUpDown));
		//读写
		pack3 = CalDataLrc.mposPack(new byte[]{(byte) 0xE1,0x05},ISOUtils.hex2byte(cmdRw));
	}
    
	//mpos指令集读写压力
	private void memory_mpos_rw(MEMORY_TYPE type) {
		int cnt = 0,bak =0; succ = 0;
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		//上电
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
		retContent = response.getResponse();
		String retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(TAG, "mpos_memory_rw", g_keeptime, "line %d:%s卡上电失败（%s）",Tools.getLineInfo(),type,retCode);
			return;
		}
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS, "%s读写压力测试,【取消】键退出测试...,还剩%d次(已成功%d次)，请耐心等待",type,cnt,succ)==ESC)
				break;
			cnt--;
			//读写
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack3), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "mpos_memory_rw", g_keeptime, "line %d:%s卡读写失败（%s）",Tools.getLineInfo(),type,retCode);
				continue;
			}
			succ++;
		}
		//下电
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack2), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(TAG, "mpos_memory_rw", g_keeptime, "line %d:%s卡下电失败（%s）",Tools.getLineInfo(),type,retCode);
			return;
		}
		gui.cls_show_msg1_record(TAG, "mpos_memory_rw",g_time_0, "MPos读写压力测试通过,总共进行%d次，成功%d次" ,bak-cnt, succ);
	}
	
	//Mpos指令集综合压力
	private void memory_mpos_press(MEMORY_TYPE type) {
		int cnt = 0,bak =0,succ = 0;
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS, "正在进行%s卡综合压力测试,还剩…%d次（已成功%d次），请耐心等待", type,cnt,succ)==ESC)
				break;
			cnt--;
			//上电
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
			retContent = response.getResponse();
			String retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "mpos_memory_press", g_keeptime, "line %d:%s卡上电失败（%s）",Tools.getLineInfo(),type,retCode);
				continue;
			}
			//读写
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack3), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "mpos_memory_press", g_keeptime, "line %d:%s卡读写失败（%s）",Tools.getLineInfo(),type,retCode);
				continue;
			}
			//下电
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack2), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "mpos_memory_press", g_keeptime, "line %d:%s卡下电失败（%s）",Tools.getLineInfo(),type,retCode);
				continue;
			}
			succ++;
		}
			
		gui.cls_show_msg1_record(TAG, "mpos_memory_press",g_time_0, "MPos综合压力测试通过,总共进行%d次，成功%d次",bak-cnt,succ);
	}
	
	/**
	 * memory卡压力：包括jni和mpos方式
	 * @param type
	 */
	private void memory_press(MEMORY_TYPE type)
	{
		int nkeyIn=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++nkeyIn == (!GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?'1':'2'))// Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "memory_press", 2, "%s连续压力测试结束", TESTITEM);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行连续memory压力测试,[取消]退出")==ESC)
					return;
			}
			else
			{
				nkeyIn=gui.cls_show_msg("MEMORY综合压力\n1.流程压力(mpos)");// Poynt和X5不支持mpos,直接进行jni压力测试
			}
			switch (nkeyIn) 
			{
				/*case '0': // 只需要测试NDK用例即可
					memory_jni_press(type);
					break;*/
				
				case '1':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						memory_mpos_press(type);
					break;

				case ESC:
					return;
			}
		}
	}

	public void memory_jni_press(MEMORY_TYPE type) 
	{
		int cnt = 0,bak =0,ret=-1;succ = 0;
		byte[] psAtrBuf = new byte[10];
		int[] pnAtrLen = new int[1];
		
		
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS, "正在进行%s卡综合压力测试,还剩…%d次（已成功%d次），请耐心等待", type,cnt,succ)==ESC)
				break;
			cnt--;
			if((ret = iccPowerOn(cardId[type.ordinal()],psAtrBuf,pnAtrLen))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "memory_press", g_keeptime, "line %d:上电失败（%d）",Tools.getLineInfo(),ret);
				continue;
			}
			functionMem(type);
			if((ret = icSamPowerOff(cardId[type.ordinal()]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "memory_press", g_keeptime, "line %d:下电失败（%d）",Tools.getLineInfo(),ret);
				continue;
			}
			
		}
			
		gui.cls_show_msg1_record(TAG, "memory_press",g_time_0, "综合压力测试通过,总共进行%d次，成功%d次",bak-cnt,succ);
	}
	
	/**
	 * memory卡读写压力：包括jni和mpos方式
	 * @param type
	 */
	private void memory_rw(MEMORY_TYPE type)
	{
		int nkeyIn=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++nkeyIn == (!GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?'1':'2'))// Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "memory_rw", 2, "%s连续读写压力测试结束", TESTITEM);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行连续压力测试,[取消]退出")==ESC)
					return;
					
			}
			else
			{
				nkeyIn=gui.cls_show_msg("MEMORY读写压力\n1.读写压力(mpos)");// Poynt和X5不支持mpos,直接进行jni压力测试
			}
			switch (nkeyIn) 
			{
				/*case '0':
					memory_jni_rw(type);
					break;*/
				
				case '1':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						memory_mpos_rw(type);
					break;

				case ESC:
					return;
			}
		}
	}
	
	private void memory_jni_rw(MEMORY_TYPE type) 
	{
		int cnt = 0,bak =0; succ = 0;
		byte[] psAtrBuf = new byte[10];
		int[] pnAtrLen = new int[1];
		
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		Log.e("type", cardId[type.ordinal()].ordinal()+"");
		iccPowerOn(cardId[type.ordinal()],psAtrBuf,pnAtrLen);
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS, "%s读写压力测试,【取消】键退出测试...,还剩%d次(已成功%d次)，请耐心等待",type,cnt,succ)==ESC)
				break;
			cnt--;
			functionMem(type);
		}
		
		icSamPowerOff(cardId[type.ordinal()]);
		gui.cls_show_msg1_record(TAG, "memory测试",g_time_0, "读写压力测试通过,总共进行%d次，成功%d次" ,bak-cnt, succ);
	}
	
	public void memory_abnormity(MEMORY_TYPE type) 
	{
		byte[] psAtrBuf = new byte[10];
		int[] pnAtrLen = new int[1];
		int ret=-1;
		
		EM_ICTYPE[] errCardId  = {EM_ICTYPE.ICTYPE_M_2,EM_ICTYPE.ICTYPE_M_3,EM_ICTYPE.ICTYPE_M_4,EM_ICTYPE.ICTYPE_M_5,
				EM_ICTYPE.ICTYPE_M_6,EM_ICTYPE.ICTYPE_M_2, EM_ICTYPE.ICTYPE_M_3,EM_ICTYPE.ICTYPE_M_4, EM_ICTYPE.ICTYPE_M_3,
				EM_ICTYPE.ICTYPE_M_5, EM_ICTYPE.ICTYPE_M_6, EM_ICTYPE.ICTYPE_M_7,EM_ICTYPE.ICTYPE_M_2,EM_ICTYPE.ICTYPE_M_2};
		
		MEMORY_TYPE[] errMEM = {MEMORY_TYPE.SLE4418_28,MEMORY_TYPE.SLE5528,MEMORY_TYPE.SLE4432_42,MEMORY_TYPE.AT88SC102,
				MEMORY_TYPE.AT88SC153,MEMORY_TYPE.AT88SC1608,MEMORY_TYPE.AT88SC1604,MEMORY_TYPE.AT24C01,MEMORY_TYPE.AT24C02,
				MEMORY_TYPE.AT24C04,MEMORY_TYPE.AT24C08,MEMORY_TYPE.AT24C016,MEMORY_TYPE.AT24C032,MEMORY_TYPE.AT24C064,
				};
		
		MEMORY_TYPE[] corrMEM = {MEMORY_TYPE.AT24C01,MEMORY_TYPE.AT24C02,MEMORY_TYPE.AT24C04,MEMORY_TYPE.AT24C08,
				MEMORY_TYPE.AT24C016,MEMORY_TYPE.AT24C032,MEMORY_TYPE.AT24C064,MEMORY_TYPE.SLE4432_42,
				MEMORY_TYPE.SLE4418_28,MEMORY_TYPE.SLE5528,MEMORY_TYPE.AT88SC102,MEMORY_TYPE.AT88SC1604,MEMORY_TYPE.AT88SC1608,
				MEMORY_TYPE.AT88SC153};
		
		//case1:测试在卡座上没有卡的情况下，上电应该是失败的
		gui.cls_show_msg("请将"+corrMEM[type.ordinal()]+"卡从卡座上移走，点任意键继续");
		
		//上电(未插卡上电应失败)
		if(iccPowerOn(cardId[type.ordinal()],psAtrBuf,pnAtrLen) == NDK_OK)
		{
			icSamPowerOff(cardId[type.ordinal()]);
			gui.cls_show_msg1(g_keeptime, "line %d:异常测试失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(2, "子用例测试通过");
		
		//case2:测试的上电的卡类型传为IC卡类型和插入的卡片类型不一致(上电一定失败)(插入memory上电ic应失败)
		gui.cls_show_msg("请将"+corrMEM[type.ordinal()]+"卡插入卡槽，点任意键继续");
		if(iccPowerOn(EM_ICTYPE.ICTYPE_IC,psAtrBuf,pnAtrLen) == NDK_OK)
		{
			icSamPowerOff(EM_ICTYPE.ICTYPE_IC);
			gui.cls_show_msg1_record(TAG, "memory_abnormity",g_keeptime, "line %d:异常测试失败，错误类型上电成功！", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1(2, "子用例测试通过");
		
		//case3:测试的卡类型传入错误插入的卡片为MEM卡，应该失败
		// 根据邱涛建议 sle4428/sle5528不需要经过严格的上电时序，就能正常读写卡片，故取消sle4428/sle5528卡在异常测试中“使用错误卡片上电，读写应该失败”这项测试，modify by zhengxq 20171020
		switch (type) {
		case SLE4418_28:
		case SLE5528:
			
			break;
		
		// 其他卡片需要进行该项测试
		default:
			if(iccPowerOn(errCardId[type.ordinal()],psAtrBuf,pnAtrLen) == NDK_OK)
			{
				ret = functionMem(errMEM[type.ordinal()]);
				if(ret==NDK_OK)
				{
					if(cardId[type.ordinal()]==EM_ICTYPE.ICTYPE_M_3||cardId[type.ordinal()]==EM_ICTYPE.ICTYPE_M_7)
					{
						icSamPowerOff(errCardId[type.ordinal()]);
						gui.cls_show_msg1_record(TAG, "memory测试",g_keeptime,  "line %d:读写数据应该失败", Tools.getLineInfo());
						return;
					}
				}
				icSamPowerOff(errCardId[type.ordinal()]);
			}
			gui.cls_show_msg1(2, "子用例测试通过");
			break;
		}

		// 保护动作，避免多次失败引起卡片锁死，程序中进行一次正常上下电读写测试
		iccPowerOn( cardId[type.ordinal()],psAtrBuf,pnAtrLen);
		functionMem(corrMEM[type.ordinal()]);
		icSamPowerOff(errCardId[type.ordinal()]);
		
		//case4:正常上电后，拔插memory卡，进行读写操作(应该失败)
		if(iccPowerOn( cardId[type.ordinal()],psAtrBuf,pnAtrLen)!=NDK_OK)
		{
			icSamPowerOff(errCardId[type.ordinal()]);
		    gui.cls_show_msg1_record(TAG, "memory测试",g_keeptime,  "line %d:上电失败！", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg("拔插"+corrMEM[type.ordinal()]+"，点任意键继续");
		// 读写,验证密码操作
		ret = functionMem(corrMEM[type.ordinal()]);
		if(ret ==NDK_OK)
		{
			icSamPowerOff(cardId[type.ordinal()]);
			gui.cls_show_msg1_record(TAG, "memory测试",g_keeptime,  "line %d:读写数据失败！", Tools.getLineInfo());
			return;
		}
		icSamPowerOff(cardId[type.ordinal()]);
		gui.cls_show_msg1(2, "子用例测试通过");
		// 保护动作
		iccPowerOn(cardId[type.ordinal()],psAtrBuf,pnAtrLen);
		functionMem(corrMEM[type.ordinal()]);
		icSamPowerOff(errCardId[type.ordinal()]);
		gui.cls_show_msg("异常测试通过！请执行正常测试");
	}
	
	public int functionMem(MEMORY_TYPE type) 
	{
		int ret = 0;
		switch(type) 
		{
		case AT24C01:
		case AT24C02:
		case AT24C04:
		case AT24C08:
		case AT24C016:
		case AT24C032:
		case AT24C064:
			ret = test24C(type);
			return ret;
			
		case SLE4432_42:	//SLE4432_42
			ret = test44x2();
			return ret;
			
		case SLE4418_28:	//SLE4418_28
			ret = test44x8();
			return ret;
			
		case SLE5528:
			ret = test5528();
			return ret;
			
		case AT88SC102:
			ret = test102();
			return ret;
			
		case AT88SC1604:
			ret = test1604();
			return ret;
			
		case AT88SC1608:
			ret = test1608();
			return ret;
			
		case AT88SC153:
			ret = test153();
			return ret;
			
		default:
			return FAIL;
		}
	}
	
	public MEMORY_TYPE memory_config() 
	{
		// 每页显示8个
		String[] memType = {"0.AT24C01\n1.AT24C02\n2.AT24C04\n3.AT24C08\n4.AT24C016\n5.AT24C032\n6.AT24C064\n7.SLE44x2\n",
				"0.SLE44x8\n1.SLE5528\n2.AT88SC102\n3.AT88SC1604\n4.AT88SC1608\n5.AT88SC153"};
		MEMORY_TYPE type[] = {MEMORY_TYPE.AT24C01,MEMORY_TYPE.AT24C02,MEMORY_TYPE.AT24C04,MEMORY_TYPE.AT24C08,MEMORY_TYPE.AT24C016,
				MEMORY_TYPE.AT24C032,MEMORY_TYPE.AT24C064,MEMORY_TYPE.SLE4432_42,MEMORY_TYPE.SLE4418_28,MEMORY_TYPE.SLE5528,
				MEMORY_TYPE.AT88SC102,MEMORY_TYPE.AT88SC1604,MEMORY_TYPE.AT88SC1608,MEMORY_TYPE.AT88SC153};
		gui.cls_show_msg1(2, "选择测试卡类型..\n↑键:往回翻页\n↓键:往后翻页\n");
		int currentPage=1,maxPage=2,ret;// 默认第一页
		while(true)
		{
			ret = gui.cls_show_msg(memType[currentPage-1]);
			switch (ret) {
			case KEY_UP:
				if(currentPage==1)
					break;
				else
					currentPage = currentPage-1;
				break;
				
			case KEY_DOWN:
				if(currentPage==maxPage)
					break;
				else
					currentPage = currentPage+1;
				break;

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
				int keyValue = (currentPage-1)*8+(ret-'0');
				if(keyValue<type.length)
				return type[keyValue];
			}
		}
	}
	
	//4432测试主函数(SLE44X2)
	public int test44x2() 
	{
		byte[] keydata = {(byte) 0xff, (byte) 0xff, (byte) 0xff};
		byte[] writedata = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
		byte[] recbuf = new byte[300];
		int recLen;
		Arrays.fill(recbuf, (byte) 0);
		
		if ((recLen = memoryCmdK(0x00, 0x00, keydata, keydata.length,recbuf,MEMORY_TYPE.SLE4432_42)) ==FAIL)
		{
			gui.cls_show_msg1_record(TAG, "test44x2",g_keeptime,  "line %d:校验KEY失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x2", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		if ((recLen = memoryCmdWrite(0x00, 0x00, 0x20, writedata, writedata.length, recbuf,MEMORY_TYPE.SLE4432_42)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x2",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x2", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		
		if ((recLen = memoryCmdRead(0x00, 0x00, 0x20, writedata.length, recbuf,MEMORY_TYPE.SLE4432_42)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x2",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x2", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		// 获取读到的数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x2",g_keeptime, "line %d:数据校验失败", Tools.getLineInfo());
			return FAIL;
		}
		
		//写保护区测试
		if ((recLen=memoryCmdWrite(0x00, 0x80, 0x00, writedata, writedata.length, recbuf,MEMORY_TYPE.SLE4432_42)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x2",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x2", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		//读保护区测试
		if ((recLen = memoryCmdRead(0x00, 0x80, 0x00, writedata.length, recbuf,MEMORY_TYPE.SLE4432_42)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x2",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x2", g_keeptime, "line %d:写数据失败（ff ff）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		succ++;
		return NDK_OK;
	}
	
	//1608测试主函数
	public int test1608() 
	{
		byte[] keydata = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
		
		byte[] akeydata = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 
						   (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
		
		byte[] writedata = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
		
		byte[] recbuf = new byte[300];
		
		int recLen = -1;
		
		//step1 认证测试
		if((recLen = memoryCmdA(0x00, 0x00, akeydata, akeydata.length, recbuf,MEMORY_TYPE.AT88SC1608)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:校验KEY失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1608", g_keeptime, "line %d:校验返回码失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		//step2 校验密钥
		if((recLen = memoryCmdK(0x03, 0x00, keydata, keydata.length,recbuf, MEMORY_TYPE.AT88SC1608)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:校验KEY失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1608", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		if((recLen = memoryCmdK(0x03, 0x08, keydata, keydata.length, recbuf, MEMORY_TYPE.AT88SC1608)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:校验KEY失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1608", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		//step3 用户页面选择操作
		if ((recLen = memoryCmdWrite(0x00, 0x02, 0x01, null, 0, recbuf,MEMORY_TYPE.AT88SC1608)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return NDK_ERR;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1608", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return NDK_ERR;
			}
		}
		
		//step4 写操作
		if ((recLen = memoryCmdWrite(0x00, 0x00, 0x0E, writedata, writedata.length, recbuf,MEMORY_TYPE.AT88SC1608)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1608", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		//step5 读操作
		if ((recLen = memoryCmdRead(0x00, 0x01, 0x0E, writedata.length, recbuf,MEMORY_TYPE.AT88SC1608)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1608", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return NDK_ERR;
			}
		}
		
		//step6 比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test1608",g_keeptime, "line %d:读数校验失败", Tools.getLineInfo());
			return FAIL;
		}
		succ++;
		return NDK_OK;
	}
	
	//sle4418_28测试 (SLE44X8)
	public int test44x8() 
	{
		byte[] keydata = {(byte) 0xff, (byte) 0xff};
		
		byte[] writedata = {(byte) 0x3E, (byte) 0x32, (byte) 0x33, (byte) 0x34};
		
		byte[] recbuf = new byte[12];
		byte[] recbufold = new byte[12];
		int recLen = -1,recfoldLen=-1;
		
		//step1:校验密钥
		if((recLen = memoryCmdK(0x00, 0x00, keydata, keydata.length, recbuf, MEMORY_TYPE.SLE4418_28)) ==FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:校验KEY失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:校验返回码失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		//step2:读卡上的原始数据,保护动作
		if ((recfoldLen = memoryCmdRead(0x00, 0x02, 0xff, writedata.length, recbufold,MEMORY_TYPE.SLE4418_28)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbufold,recfoldLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbufold,recfoldLen));
				return FAIL;
			}
		}
		
		// 获取读到的数据
		if (Tools.memcmp(writedata, recbufold, writedata.length)==true)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:原始数据和测试数据一致，请修改测试数据", Tools.getLineInfo());
			return FAIL;
		}
		
		//step3:写无保护位数据
		if ((recLen = memoryCmdWrite(0x00, 0x02, 0xff, writedata, writedata.length, recbuf,MEMORY_TYPE.SLE4418_28)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:写数据失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//step4:读无保护位数据
		if ((recLen = memoryCmdRead(0x00, 0x02, 0xff, writedata.length, recbuf,MEMORY_TYPE.SLE4418_28)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:读数校验失败", Tools.getLineInfo());
			return FAIL;
		}
		
		//step5:写回卡上的原始数据
		if ((recLen = memoryCmdWrite(0x00, 0x02, 0xff, recbufold, recfoldLen-2, recbuf,MEMORY_TYPE.SLE4418_28)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		succ++;
		return NDK_OK;
	}
	
	// sle5528的测试入口
	public int test5528()
	{
		byte[] keydata = {(byte) 0xff, (byte) 0xff};
		
		byte[] writedata = {(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x38};
		
		byte[] recbuf = new byte[12];
		byte[] recbufold = new byte[12];
		int recLen = -1,recfoldLen=-1;
		
		// case1:5528卡读写测试，写无保护位数据
		//step1:校验密钥
		if((recLen = memoryCmdK(0x00, 0x00, keydata, keydata.length, recbuf, MEMORY_TYPE.SLE5528)) ==FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test5528",g_keeptime, "line %d:校验KEY失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test5528", g_keeptime, "line %d:校验返回码失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf,recLen));
				return FAIL;
			}
		}
		
		//step2:读卡上的原始数据,保护动作
		if ((recfoldLen = memoryCmdRead(0x00, 0x02, 0xff, writedata.length, recbufold,MEMORY_TYPE.SLE5528)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test5528",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbufold,recfoldLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbufold,recfoldLen));
				return FAIL;
			}
		}
		
		// 获取读到的数据
		if (Tools.memcmp(writedata, recbufold, writedata.length)==true)
		{
			gui.cls_show_msg1_record(TESTITEM, "test5528",g_keeptime, "line %d:原始数据和测试数据一致，请修改测试数据", Tools.getLineInfo());
			return FAIL;
		}
		
		//step3:写无保护位数据
		if ((recLen = memoryCmdWrite(0x00, 0x02, 0xff, writedata, writedata.length, recbuf,MEMORY_TYPE.SLE5528)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test5528",g_keeptime, "line %d:写数据失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//step4:读无保护位数据
		if ((recLen = memoryCmdRead(0x00, 0x02, 0xff, writedata.length, recbuf,MEMORY_TYPE.SLE5528)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:读数校验失败", Tools.getLineInfo());
			return FAIL;
		}
		
		//step5:写回卡上的原始数据
		if ((recLen = memoryCmdWrite(0x00, 0x02, 0xff, recbufold, recfoldLen-2, recbuf,MEMORY_TYPE.SLE5528)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test44x8",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test44x8", g_keeptime, "line %d:写数据失败（%s）", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		succ++;
		return NDK_OK;
		
	}
	
	//AT24CXX卡的测试入口
	public int test24C(MEMORY_TYPE type) 
	{
		byte[] writedata = {(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34};
		
		byte[] recbuf = new byte[300];
		int recLen = -1;
		byte[] resultCode = new byte[2];
		
		//写数据
		if ((recLen = memoryCmdWrite(0x00, 0x00, 0x01, writedata, writedata.length, recbuf,type)) ==FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test24C",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test24C", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return NDK_ERR;
			}
		}
		
		//读数据
		if ((recLen = memoryCmdRead(0x00, 0x00, 0x01, writedata.length, recbuf,type)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test24C",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test24C", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test24C",g_keeptime, "line %d:读数校验失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(resultCode));
			return FAIL;
		}
		succ++;
		return NDK_OK;
	}
	
	public int test102() 
	{
		byte[] writedata = {(byte) 0x31, (byte) 0x32};
		byte[] keyData = {(byte) 0xf0,(byte) 0xf0};
		
		byte[] recbuf = new byte[300];
		int recLen = -1;
		
		// 对测试区进行读写数据
		if ((recLen=memoryCmdE(0x00, 0xB0, writedata.length, recbuf,MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//写数据
		if ((recLen = memoryCmdWrite(0x00, 0x00, 0xB0, writedata, writedata.length, recbuf,MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL; 
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//读数据
		if ((recLen = memoryCmdRead(0x00, 0x00, 0xB0, writedata.length, recbuf,MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:读数校验失败", Tools.getLineInfo());
			return FAIL;
		}
		
		// 对主密码区进行擦写，前后密码保持一致 校验密钥、擦除密钥、新旧密钥保持一致
		// 密钥校验
		if((recLen = memoryCmdK(0x00, 0x00, keyData, keyData.length, recbuf, MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:密钥校验失败",Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		// 擦除密钥
		if((recLen = memoryCmdE(0x00, 0x0A, writedata.length,recbuf, MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:擦除密钥失败", Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//写数据
		if ((recLen = memoryCmdWrite(0x00, 0x00, 0x0A, keyData, keyData.length, recbuf,MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return NDK_ERR;
			}
		}
		
		// 再次校验密钥应成功
		if((recLen = memoryCmdK(0x00, 0x00, keyData, keyData.length, recbuf, MEMORY_TYPE.AT88SC102)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test102",g_keeptime, "line %d:密钥校验失败",Tools.getLineInfo());
			return FAIL;
		}
		else
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test102", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		succ++;
		return NDK_OK;
	}
	
	//测试1604测试卡
	public int test1604() 
	{
		byte[] writedata = {(byte) 0x31, (byte) 0x32};
		
		byte[] recbuf = new byte[300];
		int recLen = -1;
		
		// step1:擦除测试区D5
		if ((recLen = memoryCmdE(0x07, 0xD4, writedata.length, recbuf, MEMORY_TYPE.AT88SC1604)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1604",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1604", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		//1604卡的测试地址是2005-2006  07 D6写的是2006-2007 所以修改为07 D5
		//写数据
		if ((recLen = memoryCmdWrite(0x00, 0x07, 0xD5, writedata, writedata.length, recbuf,MEMORY_TYPE.AT88SC1604)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1604",g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1604", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//读数据
		if ((recLen = memoryCmdRead(0x00, 0x07, 0xD5, writedata.length, recbuf,MEMORY_TYPE.AT88SC1604)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test1604",g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test1604", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test1604",g_keeptime, "line %d:读数校验失败", Tools.getLineInfo());
			return NDK_ERR;
		}
		succ++;
		return NDK_OK;
	}
	
	//测试AT88SC153测试卡
	public int test153() 
	{
		byte[] keydata = {(byte)0xFF, (byte) 0xFF, (byte) 0xFF};
		byte[] akeydata = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
						   (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] writedata = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x11, (byte)0x12,
							(byte)0x13, (byte)0x14, (byte)0x21, (byte)0x22, (byte)0x23, (byte)0x24};
		
		byte[] recbuf = new byte[300];
		int recLen = -1;
		
		
		//step1:认证测试(可以不认证)
		if ((recLen = memoryCmdA(0x00, 0x00, akeydata, akeydata.length, recbuf, MEMORY_TYPE.AT88SC153)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//step2:校验密钥
		if ((recLen = memoryCmdK(0x0B, 0x34, keydata, keydata.length, recbuf, MEMORY_TYPE.AT88SC153)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return NDK_ERR;
			}
		}
		
		if ((recLen = memoryCmdK(0x03, 0x30, keydata, keydata.length, recbuf, MEMORY_TYPE.AT88SC153)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//写数据
		if ((recLen = memoryCmdWrite(0x00, 0x00, 0x06, writedata, writedata.length, recbuf,MEMORY_TYPE.AT88SC153)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:写数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return NDK_ERR;
			}
		}
		
		//读数据
		if ((recLen = memoryCmdRead(0x00, 0x01, 0x06, writedata.length, recbuf,MEMORY_TYPE.AT88SC153)) == FAIL)
		{
			gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:读数据区失败(ff ff)", Tools.getLineInfo());
			return FAIL;
		}
		else 
		{
			if(ISOUtils.SMART_GETSW(recbuf,recLen).equals("9000")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:校验KEY失败(%s)", Tools.getLineInfo(),ISOUtils.SMART_GETSW(recbuf, recLen));
				return FAIL;
			}
		}
		
		//比较数据
		if (!Tools.memcmp(writedata, recbuf, writedata.length))
		{
			gui.cls_show_msg1_record(TESTITEM, "test153", g_keeptime, "line %d:读数校验失败", Tools.getLineInfo());
			return FAIL;
		}
		succ++;
		return NDK_OK;
	}
	
	// 读写数据，返回recbuf的长度指令格式
	public int rw_data(byte[] head,int headLen,byte[] data,int dataLen,byte[] recbuf,MEMORY_TYPE type)
	{
		byte[] sendBuf;
		// sendBuf的长度要根据实际的长度
		if(data!=null)
			sendBuf = new byte[headLen+1+dataLen];
		else
			sendBuf = new byte[headLen+1];
		
		int sendLen = 0;
		int[] rlen = new int[1];
		EM_ICTYPE cardId[] = {EM_ICTYPE.ICTYPE_M_1_1,EM_ICTYPE.ICTYPE_M_1_2,EM_ICTYPE.ICTYPE_M_1_4,EM_ICTYPE.ICTYPE_M_1_8,
				EM_ICTYPE.ICTYPE_M_1_16,EM_ICTYPE.ICTYPE_M_1_32,EM_ICTYPE.ICTYPE_M_1_64,EM_ICTYPE.ICTYPE_M_2,EM_ICTYPE.ICTYPE_M_3,
				EM_ICTYPE.ICTYPE_M_3,EM_ICTYPE.ICTYPE_M_4,EM_ICTYPE.ICTYPE_M_5,EM_ICTYPE.ICTYPE_M_6,EM_ICTYPE.ICTYPE_M_7};
		
		System.arraycopy(head, 0, sendBuf, 0, headLen);
		sendBuf[headLen] = (byte) (dataLen&0xff);
		if(data==null)
			sendLen = headLen+1;
		else
		{
			System.arraycopy(data, 0, sendBuf, headLen+1, dataLen);
			sendLen = headLen+1+dataLen;
		}
		Log.v("wxyhhh", "sendBuf="+ISOUtils.hexString(sendBuf));
		if(JniNdk.JNI_Icc_Rw(cardId[type.ordinal()].ordinal(), sendLen, sendBuf, rlen, recbuf)!=NDK_OK)
			return FAIL;
		else
		{
			return rlen[0];
		}
	}
	// 擦除
	public int memoryCmdE(int p1,int p2,int len,byte[] recbuf,MEMORY_TYPE type)
	{
		/*private & local definition*/
		byte[] head = new byte[300];
		
		head[0] = 0x00;
		head[1] = 0x0E;
		head[2] = (byte) p1;
		head[3] = (byte) p2;
//		head[4] = (byte) (len&0xff);
		return rw_data(head, 4,null,len,recbuf,type);
	}
	
	// memory写命令的封装
	public int memoryCmdWrite(int cla,int p1,int p2,byte[] data,int len,byte[] recbuf,MEMORY_TYPE type)
	{
		/*private & local definition*/
		byte[]	head = new byte[4];
		
		head[0] = (byte) cla;
		head[1] = (byte) 0xD0;
		head[2] = (byte) p1;
		head[3] = (byte) p2;
		
		return rw_data(head, 4, data, len, recbuf, type);
	}
	
	// memory读命令的封装
	public int memoryCmdRead(int cla,int p1,int p2,int len,byte[] recbuf,MEMORY_TYPE type)
	{
		/*private & local definition*/
		byte[] head = new byte[300];
		
		/*process body*/
		head[0] = (byte) cla;
		head[1] = (byte) 0xB0;
		head[2] = (byte) p1;
		head[3] = (byte) p2;
		return rw_data(head, 4, null, len, recbuf, type);
	}
	
	// 校验密钥命令
	public int memoryCmdK(int p1,int p2,byte[] data,int len,byte[] recbuf,MEMORY_TYPE type)
	{
		/*private & local definition*/
		byte[] head = new byte[4];
		
		head[0] = 0x00;
		head[1] = 0x20;
		head[2] = (byte) p1;
		head[3] = (byte) p2;
		return rw_data(head, 4, data, len, recbuf, type);
	}
	
	// 认证校验
	public int memoryCmdA(int p1,int p2,byte[] data,int len,byte[] recbuf,MEMORY_TYPE type)
	{
		/*private & local definition*/
		byte[] head = new byte[300];
		
		head[0] = 0x00;
		head[1] = (byte) 0x82;
		head[2] = (byte) p1;
		head[3] = (byte) p2;
//		head[4] = (byte) (len&0xff);
//		System.arraycopy(data, 0,head, 5, data.length);
		return rw_data(head, 4, data, len, recbuf, type);
	}
	
}
