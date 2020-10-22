package com.example.highplattest.event;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.RF_CARD;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: 事件注册
 * file name 		: Event1.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170927
 * directory 		: 
 * description 		: 事件注册监听和退出监听
 * related document :
 * history 		 	: 变更点											变更时间			变更人员
 *			  		 NDK_SYS_RegisterEvent空事件返回-4006，
 *					监听事件超时时间范围为0<timeOutMs<82800000		   	20200410	 	zhengxq
 * 					A7事件机制时不允许安全模块进入休眠跟A5一致				20200908		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event1 extends UnitFragment 
{
	private final String TESTITEM = "注册和退出事件监听";
	public final String TAG = Event1.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private Object lockObj = new Object();
	//事件监听回调
	private int flag=0;
	private NotifyEventListener listener=new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int arg0, int arg1, byte[] arg2) {
			LoggerUtil.v("事件触发="+arg0);
			flag = arg0;
			JniNdk.JNI_SYSUnRegisterEvent(arg0);
			return SUCC;
		}
	};
	private NotifyEventListener listener1=new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int arg0, int arg1, byte[] arg2) {
			flag = arg0;
			return SUCC;
		}
	};
	
	
	private StringBuffer str=new StringBuffer();
	byte[] szPinOut=new byte [9];
	private final int PINTIMEOUT_MAX = 200*1000;
	byte[] pszTk1 = new byte[128];
	byte[] pszTk2 = new byte[128];
	byte[] pszTk3 = new byte[128];
	int[] errCode = new int[1];
	SecKcvInfo kcvInfo = new SecKcvInfo();
	
	List<Mod_Enable> supportAll = new ArrayList<Mod_Enable>();
	HashMap<Mod_Enable, Integer> k21Support = new HashMap<Mod_Enable,Integer>();
	
	
	public void event1()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "event1",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元测试\n1.注册事件不允许安全模块进入休眠\n", TESTITEM);
			switch (nkeyIn) {
			case '0':
				try {
					unitTest();
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case '1':
				try {
					sleepTest();
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	
	private void unitTest() 
	{

		kcvInfo.nCheckMode =0;
		kcvInfo.nLen=4;
		int ret=-1;
		
		// 测试前置，梳理当前的机型支持哪些模块
		k21Support.put(Mod_Enable.IccEnable, 0X00000008);
		k21Support.put(Mod_Enable.MagEnable, 0X00000004);
		k21Support.put(Mod_Enable.RfidEnable, 0X00000010);
		k21Support.put(Mod_Enable.PinEnable, 0X00000020);
		k21Support.put(Mod_Enable.PrintEnableReg, 0X00000040);
		
		LoggerUtil.i("IccEnable="+GlobalVariable.gModuleEnable.get(Mod_Enable.IccEnable));
		LoggerUtil.i("MagEnable="+GlobalVariable.gModuleEnable.get(Mod_Enable.MagEnable));
		LoggerUtil.i("RfidEnable="+GlobalVariable.gModuleEnable.get(Mod_Enable.RfidEnable));
		LoggerUtil.i("PinEnable="+GlobalVariable.gModuleEnable.get(Mod_Enable.PinEnable));
		LoggerUtil.i("PrintEnableReg="+GlobalVariable.gModuleEnable.get(Mod_Enable.PrintEnableReg));
		
		
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
		
		if(supportAll.size()<=0)
		{
			gui.cls_show_msg("遍历后无支持的事件");
			return;
		}
		
		LoggerUtil.d("=====support module size="+k21Support.size()+"=====");
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		 /*private & local definition */
		// 测试前置，注销事件机制以及下电
		for(Mod_Enable name :supportAll)
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(name));
		
		if(k21Support.containsKey(Mod_Enable.MagEnable))/**磁卡模块存在才操作*/
			JniNdk.JNI_Mag_Close();//磁卡关闭
		
		if(k21Support.containsKey(Mod_Enable.RfidEnable))
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);//射频卡下电
		
		// case1.x: 事件超时监听范围为0<timeOutMs<82800000,无效等价类测试(A5上超时时间没有范围，A7上超时时间才有范围,20200709)
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth))
		{
			gui.cls_show_msg1(1, "JNI_SYSRegisterEvent参数异常测试");
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),0,listener))!=NDK_ERR_PARA)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),82800001,listener))!=NDK_ERR_PARA)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
		}

		//case1:参数异常测试
		LoggerUtil.v("case1 start===");
		if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_NONE.getValue()-1,MAXWAITTIME,listener))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAX.getValue()+1,MAXWAITTIME,listener))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		if(k21Support.containsKey(Mod_Enable.IccEnable)==true)
		{
			if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),MAXWAITTIME,null))!=NDK_ERR_PARA)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
		}
		LoggerUtil.v("case1 end===");

		//case2: 注册事件同时设置多项时应该返回NDK_ERR_POSNDK_EVENT_NUM 错误的事件号
		LoggerUtil.v("case2:注册错误的事件号开始===");
		if((ret = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue()|EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),MAXWAITTIME,listener))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		LoggerUtil.v("case2 start===");
		
		//case3:相同事件反复注册应该返回NDK_ERR_ POSNDK EVENT_REG_TWICE
		LoggerUtil.v("case3 start===");
		if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(0)),MAXWAITTIME,listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(0)),MAXWAITTIME,listener))!=NDK_ERR_POSNDK_EVENT_REG_TWICE)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(supportAll.get(0)));
		LoggerUtil.v("case3 end===");
		
		//case4:不同事件反复注册会成功:要想测事件被占用需要2个相同的进程,这里单进程无法实现
		LoggerUtil.v("case4 start===");
		if(k21Support.size()>=2)
		{
			if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(0)),MAXWAITTIME,listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}

			if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(1)),MAXWAITTIME,listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(supportAll.get(0)));
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(supportAll.get(1)));
			SystemClock.sleep(1000);//等待系统处理完  需要一点点的延时 与超时时间无关  
		}
		LoggerUtil.v("case4 end===");
		
		//case5:测试监听超时时间<=0,不超时永不退出 需要调用退出监听函数,延时1s后再监听相同事件时应该会失败,说明之前的事件未注销	
		LoggerUtil.v("case5 start===");
		if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(0)),MAXWAITTIME,listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		SystemClock.sleep(1000);
		if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(0)),MAXWAITTIME,listener))!=NDK_ERR_POSNDK_EVENT_REG_TWICE)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(supportAll.get(0)));
		LoggerUtil.v("case5 end===");

		//case6:测试注册监听事件后超时退出应该返回SYS_EVENT_NONE，超时退出则自动注销事件
		LoggerUtil.v("case6 start===");
		flag = k21Support.get(supportAll.get(0));
		if((ret = JniNdk.JNI_SYSRegisterEvent(k21Support.get(supportAll.get(0)), 1*1000,listener1))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		gui.cls_show_msg1(2,"等待2s");
		if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
			if(GlobalVariable.isContinue == false)
				return;
		}	
		LoggerUtil.v("case6 end===");
		
		//case7:正常测试:监听无事件，回调函数标志位应该返回SYS_EVENT_NONE   A7 A9平台返回值统一为-4006，旧平台不修改 
		LoggerUtil.v("case7 start===");
		int case7Ret = GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)?NDK_ERR_POSNDK_EVENT_NUM:NDK_ERR_POSNDK_PERMISSION_UNDEFINED;
		if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_NONE.getValue(), 1*1000, listener))!=case7Ret)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		
		LoggerUtil.v("case7 end===");
		
		//case8:正常测试:磁卡打开,监听磁卡事件
		if(k21Support.containsKey(Mod_Enable.MagEnable)==true)//支持磁卡
		{
			//打开磁卡
			LoggerUtil.v("case8 start===");
			JniNdk.JNI_Mag_Open();
			//case8.1:注册监听磁卡事件,提示刷卡,刷卡后事件应该被监听到,并调用回调函数将标志位设置成磁卡事件,然后退出监听事件应该成功
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			
			gui.cls_show_msg1(30,"请在30秒内刷磁卡,刷完卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//case8.2:退出监听事件后再刷卡,应该不会调用回调函数,flag保持0;
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg1(30,"请再刷磁卡,刷完卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Mag_Close();
			LoggerUtil.v("case8 end===");
		}
		
		if(k21Support.containsKey(Mod_Enable.IccEnable)==true)//支持IC卡
		{
			//case9:监听IC卡插入事件
			//case9.1:注册监听IC卡事件,提示插入IC卡,插卡后事件应该被监听到,并调用回调函数将标志位设置成IC卡事件,然后退出监听事件应该成功
			LoggerUtil.v("case9 start===");
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			gui.cls_show_msg1(30,"请在30秒插入IC卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}	
			//case9.2:退出监听事件后拔IC卡(插拔IC卡都会触发事件的),应该不会调用回调函数,flag保持0;
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg1(30,"请在30s内拔掉IC卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v("case9 end===");
		}
		
		//case10:监听非接卡事件
		if(k21Support.containsKey(Mod_Enable.RfidEnable)==true)
		{
			LoggerUtil.v("case10 start===");
			if(JniNdk.JNI_Rfid_Init(null)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			if((ret=JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_AB.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//case10.1:注册监听非接卡事件,提示放射频卡后事件应该被监听到,并调用回调函数将标志位设置成非接卡事件,然后退出监听事件应该成功
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			gui.cls_show_msg1(30,"请在30秒内在感应区放置A卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_RFID.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}	
			gui.cls_show_msg("移走A卡后任意键继续");
			//case10.2:退出监听事件后再放非接卡,应该不会调用回调函数,flag保持0;
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg1(30,"请在感应区放置A卡后按任意键继续"); 
			if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//下电
			JniNdk.JNI_Rfid_PiccDeactivate((byte)0);
			gui.cls_show_msg("移走A卡后任意键继续");
			LoggerUtil.v("case10 end===");
		}

		//case11:监听pin输入事件 
		if(k21Support.containsKey(Mod_Enable.PinEnable)==true)
		{
			LoggerUtil.v("case11 start===");
			//前置://安装TPK1(16bytes), ID=1 巴西固件要安装16字节的密钥
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)0, (byte)1, 16, 
					ISOUtils.hex2byte("19191919191919191919191919191919"),kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			//初始化
			if ((ret = touchscreen_getnum(str)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(), TESTITEM,ret);
					return;
			}
			//case11.1:注册监听pin输入事件,提示输入之后会调用回调函数标志位返回
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
//			str.append("请尽快按确认键...");
			gui.cls_printf("请尽快按确认键...".getBytes());//阻塞式可以用,非阻塞式也可以用 
			Arrays.fill(szPinOut, (byte)0);
			if((ret=JniNdk.JNI_Sec_GetPin((byte)1, "0", null, szPinOut, (byte)4, PINTIMEOUT_MAX))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Sys_Delay(5);////延时0.5s,可能底层处理需要点时间
			if(flag != EM_SYS_EVENT.SYS_EVENT_PIN.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}	
			
			//case11.2:退出监听事件后再调用pin输入,应该不会调用回调函数,flag保持0;
			flag =EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((ret=touchscreen_getnum(str))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(), TESTITEM,ret);
			    return;
			} 
//			str.append("请尽快按确认键...");
			gui.cls_printf("请尽快再次按确认键...".getBytes());//这里使用非阻塞式改成使用阻塞式(关注点是事件机制而不是getpin使用阻塞还是非阻塞)
			if((ret=JniNdk.JNI_Sec_GetPin((byte)1, "0", null, szPinOut,  (byte)4, PINTIMEOUT_MAX))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue == false)
					return;
			}	
			if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v("case11 end===");
		}

		//case12:监听打印机状态(状态改变才会有返回值,其中正常状态到忙的状态检测不到)
		if(k21Support.containsKey(Mod_Enable.PrintEnableReg)==true)// 不支持打印不需进入该case
		{
			LoggerUtil.v("case12 start===");
			//前置:打印机初始化
			gui.cls_show_msg("请确保有纸后按任意键继续...");
			
			if(lib_initprn(0) != NDK_OK)//打印边送边打开关,默认关闭
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				JniNdk.JNI_Print_Init(0);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//case12.1:注册监听打印机状态,当打印机从有纸状态变成无纸状态时能够让回调函数标志位返回打印机状态
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			gui.cls_show_msg1(30,"请在30s内取出打印纸后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}	
			//case12.2:退出监听事件后,打印机从缺纸状态到有纸状态并不能使回调函数起作用,标志位保持为0
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg1(30,"请在30s内给打印机装纸后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//case12.3:打印机状态从ok到busy,不会触发事件不好构造,打印完又成了ok,即从busy到ok会触发事件
			
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			
			JniNdk.JNI_Print_Str("打印新大陆NEWLAND打印测试页1:将打印1---80行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19"
					+ "\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45"
					+ "\n46\n47\n48\n49\n50\n51\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80\n");
			//if(flag != SYS_EVENT_NONE)
			//打印函数NDK_PrnStr是非阻塞,会直接往下执行,返回busy状态,需要等到到ok状态后继续20170912
			
			// 另外开一个线程去获取打印状态
			new Thread()
			{
				public void run() 
				{
					int cnt = 10;
					int printerStatus;
					while(cnt-->0)
					{
						printerStatus=JniNdk.JNI_Print_GetStatus();
						if(printerStatus==EM_PRN_STATUS.PRN_STATUS_OK.getValue())
						{
							synchronized (lockObj) {
								lockObj.notify();
							}
							break;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
			synchronized (lockObj) {
				try {
					lockObj.wait(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			JniNdk.JNI_Sys_Delay (3);//延时0.3s , 没有这个延时flag值不能改变
			if(flag != EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()) 
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}

			//case12.4:打印机状态从busy到缺纸状态改变会触发事件
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg("装不足10cm的纸后任意键继续");
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			JniNdk.JNI_Print_Str("打印测试页1:将打印1---120行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20\n21\n22"
					+ "\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45\n46\n47\n48\n49\n50\n51"
					+ "\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80"
					+ "\n81\n82\n83\n84\n85\n86\n87\n88\n89\n90\n91\n92\n93\n94\n95\n96\n97\n98\n99\n100\n101\n102\n103\n104\n105\n106\n107"
					+ "\n108\n109\n110\n111\n112\n113\n114\n115\n116\n117\n118\n119\n120\n");
			JniNdk.JNI_Sys_Delay (3);
			
			if(flag != EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v("case12 end===");
		}

		if(k21Support.containsKey("IccEnable")==true)
		{
			//case13:注册的事件是IC卡事件,事件提示进行磁卡刷卡,flag保持不变	
			LoggerUtil.v("case13:注册事件与操作的事件不一致开始===");
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_Mag_Open();
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			gui.cls_show_msg1(30,"请在30秒内刷磁卡,刷完卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_NONE.getValue())  
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			LoggerUtil.v("case13 end===");

			//case14:注册磁卡事件,close重新open后刷卡可再次监听到20180226
			LoggerUtil.v("case14:多次监听磁卡事件开始===");
			JniNdk.JNI_Mag_Open();
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), 60*1000, listener1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			gui.cls_show_msg("请在20秒内刷磁卡,刷完卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())  
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//开发确认需要调用NDK_MagReadNormal才会触发磁卡20170912
			if((ret=JniNdk.JNI_Mag_ReadNormal(pszTk1, pszTk2, pszTk3, errCode))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret,errCode);
				if(GlobalVariable.isContinue == false)
					return;
			}
			//close重新open后刷卡可再次监听到磁卡事件20180226
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_Mag_Open();
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg("请等待5秒后继续刷磁卡,刷完卡后按任意键继续");
			if(flag != EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())  
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,flag);
				if(GlobalVariable.isContinue == false)
					return;
			}
			flag = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();//清零
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());//退出监听事件
			LoggerUtil.v("case14:多次监听磁卡事件结束===");
		}

		//case15:反复注销(射频模块)会失败返回NDK_ERR_POSNDK_EVENT_UNREG_TWICE  -4008
		if(k21Support.containsKey("RfidEnable")==true)
		{
			LoggerUtil.v("case15:反复注销会失败开始===");
			if((ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), 60*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			if((ret=JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			JniNdk.JNI_Sys_Delay(50);//延时50ms保证已经退出成功后 再次调用退出才会返回失败
			if((ret=JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_ERR_POSNDK_EVENT_UNREG_TWICE)
			{
				gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			LoggerUtil.v("case15:反复注销会失败结束===");
		}

		testErr(supportAll, k21Support);
		gui.cls_show_msg1_record(TAG, "event1",gScreenTime, "%s测试通过", TESTITEM);
	}
	
	private void sleepTest()
	{
		String funcName ="sleepTest";
		int regNum = gui.JDK_ReadData(30, 0x04, "请输入要注册是事件号(04 磁卡,08 IC卡,16 非接卡,64 打印)");
		int ret=-1;
		
		JniNdk.JNI_SYSUnRegisterEvent(regNum);
		int sleepValue = Tools.getSreenTimeout(myactivity);
		
		if(regNum==0x04)
		{
			JniNdk.JNI_Mag_Close();
			if((ret=JniNdk.JNI_Mag_Open())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:打开磁卡失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		
		if((ret=JniNdk.JNI_SYSRegisterEvent(regNum, 80*1000, listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:事件注册失败(%d)", Tools.getLineInfo(),ret);
			JniNdk.JNI_SYSUnRegisterEvent(regNum);
			JniNdk.JNI_Mag_Close();
			return;
		}
		Tools.setSreenTimeout(myactivity, 30*1000);
		StringBuffer strBuf = new StringBuffer();
		if(regNum==0x04)
			strBuf.append("【设备唤醒后刷卡点任意键继续】");
		if(regNum==0x08)
			strBuf.append("【设备唤醒后插卡或拔卡后点任意键继续】");
		if(regNum==0x10)
			strBuf.append("【设备唤醒后挥卡后点任意键继续】");
		if(regNum==0x40)
			strBuf.append("【设备唤醒后构造打印机缺纸后任意键继续】");
			
		gui.cls_show_msg("已设置设备的休眠时间为30s,等待设备自动休眠后等待5s唤醒设备,%s", strBuf);
		gui.cls_show_msg("监听到%d事件号,与注册的事件号一致视为测试通过(04 磁卡,08 IC卡,16 非接卡,32 密码键盘,64 打印)",flag);
		LoggerUtil.v("恢复默认休眠时间===");
		Tools.setSreenTimeout(myactivity, sleepValue);
		JniNdk.JNI_Mag_Close();
	}
	
	public int lib_initprn(int unSwitch)
	{
		int ret = 0;

		//初始化
		if((ret=JniNdk.JNI_Print_Init(unSwitch)) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:打印初始化测试失败(%d)", Tools.getLineInfo(),ret);
			return ret;
		}	
		//设置下划线属性
		if ((ret=JniNdk.JNI_Print_SetUnderLine(1))!=NDK_OK)//0开，1关
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:设置打印下划线功能1失败(%d)", Tools.getLineInfo(),ret);
			return ret;
		}
		//设置灰度
		if ((ret=JniNdk.JNI_Print_SetGrey(3))!=NDK_OK)//默认灰度3
		{
			gui.cls_show_msg1_record(TAG, "event1", gKeepTimeErr, "line %d:设置打印灰度3失败(%d)", Tools.getLineInfo(),ret);
			return ret;
		}
		return ret;
	}
	
	private void testErr(List<Mod_Enable> supportAll,HashMap<Mod_Enable, Integer> k21Support)
	{
		for(Mod_Enable name :supportAll)
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(name));
	}
	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
		// 测试后置注销不影响其他测试
		testErr(supportAll, k21Support);
	}
}
