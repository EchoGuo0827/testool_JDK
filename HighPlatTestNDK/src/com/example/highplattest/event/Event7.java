package com.example.highplattest.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.RF_CARD;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.newland.ndk.SecKcvInfo;

import android.R.integer;
import android.newland.SettingsManager;
/************************************************************************
 * 
 * module 			: 
 * file name 		: Event5.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20180321
 * directory 		: 
 * description 		: 超时时间60s，非永不超时，主CPU和辅CPU是不会进入休眠
 * related document :
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200819	 	    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event7 extends UnitFragment{
	private Gui gui = new Gui(myactivity, handler);
	public final String TAG = Event7.class.getSimpleName();
	private final String TESTITEM = "NDK_SysRegisterEvent";
	private SettingsManager settingsManager;
	
	private List<Mod_Enable> supportAll = new ArrayList<Mod_Enable>();
	private HashMap<Mod_Enable, Integer> k21Support = new HashMap<Mod_Enable, Integer>();
	
	private int flag = 0;
	private NotifyEventListener listener = new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int eventNum, int arg1, byte[] arg2) {
			flag = eventNum;
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			return SUCC;
		}
	};
	private StringBuffer str = new StringBuffer();
	byte[] szPinOut = new byte[9];
	private final int PINTIMEOUT_MAX = 200*1000;
	SecKcvInfo kcvInfo = new SecKcvInfo();
	
	public void event7()
	{
		kcvInfo.nCheckMode = 0;
		kcvInfo.nLen = 4;
		int ret = -1;
		// 测试前置，梳理当前的机型支持哪些模块
		k21Support.put(Mod_Enable.IccEnable, 0X00000008);
		k21Support.put(Mod_Enable.MagEnable, 0X00000004);
		k21Support.put(Mod_Enable.RfidEnable, 0X00000010);
		k21Support.put(Mod_Enable.PinEnable, 0X00000020);
		k21Support.put(Mod_Enable.PrintEnableReg, 0X00000040);
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IccEnable))
			supportAll.add(Mod_Enable.IccEnable);
		else
			k21Support.remove(Mod_Enable.IccEnable);
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.MagEnable))
			supportAll.add(Mod_Enable.MagEnable);
		else
			k21Support.remove(Mod_Enable.MagEnable);
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RfidEnable))
			supportAll.add(Mod_Enable.RfidEnable);
		else
			k21Support.remove(Mod_Enable.RfidEnable);
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable))
			supportAll.add(Mod_Enable.PinEnable);
		else
			k21Support.remove(Mod_Enable.PinEnable);
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnableReg))
			supportAll.add(Mod_Enable.PrintEnableReg);
		else
			k21Support.remove(Mod_Enable.PrintEnableReg);
		
		flag = k21Support.get(supportAll.get(0));
		settingsManager = (SettingsManager)myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		settingsManager.setScreenTimeout(30*1000);//设置系统休眠时间30s
		//case1:测试60s超时时间内设备的功耗值，计算平均功耗
		if(k21Support.containsKey(Mod_Enable.IccEnable) == true)
		{
			gui.cls_show_msg("按键后开始测试60s内的功耗值");
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg1(60, "功耗测试中……");
			gui.cls_show_msg("测试结束，计算平均功耗值，按任意键继续");
		}
		
		//case2:在30-60s时 刷卡|插卡|挥卡|输密码|打印 事件时能正常监听到事件
		if(k21Support.containsKey(Mod_Enable.MagEnable) == true)
		{
			JniNdk.JNI_Mag_Open();
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg1(30, "等待30s后请刷磁卡");
			gui.cls_show_msg("请刷磁卡，刷卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Mag_Close();
		}
		
		if(k21Support.containsKey(Mod_Enable.IccEnable) == true)
		{
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg1(30, "等待30s后请插IC卡");
			gui.cls_show_msg("请插入IC卡，插卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg1(30, "请在30s内拔掉IC卡后按任意键继续");
		}
		
		if(k21Support.containsKey(Mod_Enable.RfidEnable) == true)
		{
			if(JniNdk.JNI_Rfid_Init(null) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			if((ret = JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_AB.getValue())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg1(30, "等待30s后请在感应区放置A卡");
			gui.cls_show_msg("请放A卡，放卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_RFID.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg("移走A卡后任意键继续");
			//下电
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);
		}
		
		if(k21Support.containsKey(Mod_Enable.PinEnable) == true)
		{
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)0, (byte)1, 16, 
					ISOUtils.hex2byte("19191919191919191919191919191919"), kcvInfo)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			
			//初始化
			if((ret = touchscreen_getnum(str)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret=%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			gui.cls_show_msg1(30, "等待30s后请按确认键");
			gui.cls_printf("请尽快按确认键...".getBytes());//阻塞式可以用,非阻塞式也可以用
			Arrays.fill(szPinOut, (byte)0);
			if((ret = JniNdk.JNI_Sec_GetPin((byte)1, "0", null, szPinOut, (byte)4, PINTIMEOUT_MAX)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Sys_Delay(5);////延时0.5s,可能底层处理需要点时间
			if(flag != EM_SYS_EVENT.SYS_EVENT_PIN.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}	
		}
		
		if(k21Support.containsKey(Mod_Enable.PrintEnableReg) == true)
		{
			//前置:打印机初始化
			if((ret=JniNdk.JNI_Print_Init(0)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:打印初始化测试失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg1(30, "装纸后等待30s");
			JniNdk.JNI_Print_Str("打印新大陆NEWLAND打印测试页1:将打印1---80行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19"
					+ "\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45"
					+ "\n46\n47\n48\n49\n50\n51\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80\n");
			JniNdk.JNI_Sys_Delay(10);//延时0.3s , 没有这个延时flag值不能改变
			if(flag != EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()) 
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}
		
		//case3:在Android端熄屏后 刷卡|插卡|挥卡 能正常的监听到事件
		if(k21Support.containsKey(Mod_Enable.MagEnable) == true)
		{
			JniNdk.JNI_Mag_Open();
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg("短按电源键或等待30s进入休眠，休眠后请刷磁卡，刷卡后按电源键唤醒，按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Mag_Close();
		}
		
		if(k21Support.containsKey(Mod_Enable.IccEnable) == true)
		{
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg("短按电源键或等待30s进入休眠，休眠后插入IC卡，插卡后按电源键唤醒，按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg1(30, "请在30s内拔掉IC卡后按任意键继续");
		}
		
		if(k21Support.containsKey(Mod_Enable.RfidEnable) == true)
		{
			if(JniNdk.JNI_Rfid_Init(null) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			if((ret = JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_AB.getValue())) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, listener)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
				return;
			}
			gui.cls_show_msg("短按电源键或等待30s进入休眠，休眠后在感应区放置A卡，放卡后按电源键唤醒，按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_RFID.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event7", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg("移走A卡后任意键继续");
			//下电
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);
		}
		settingsManager.setScreenTimeout(-1);
		gui.cls_show_msg1_record(TAG, "event7", gScreenTime, "%s测试通过", TESTITEM);
	}
		
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// 测试后置：注销射频事件
		for(Mod_Enable name :supportAll)
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(name));
	}

}
