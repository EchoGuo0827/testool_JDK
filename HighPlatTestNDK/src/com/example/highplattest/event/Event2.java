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
/************************************************************************
 * 
 * module 			: 事件注册
 * file name 		: Event2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180116
 * directory 		: 
 * description 		: 重启事件监听
 * related document :
 * history 		 	: 变更点											变更时间			变更人员
 *			  		 A7以上平台NDK_SYS_ResumeEvent接口不再支持		   	20200410	 	zhengxq
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event2 extends UnitFragment{
	
	private Gui gui = new Gui(myactivity, handler);
	public final String TAG = Event2.class.getSimpleName();
	private final String TESTITEM = "NDK_SYS_ResumeEvent";
	private int mNotifyNum = 0;
	private StringBuffer str=new StringBuffer();
	private final int PINTIMEOUT_MAX = 200*1000;
	
	private List<Mod_Enable> supportAll = new ArrayList<Mod_Enable>();
	private HashMap<Mod_Enable, Integer> k21Support = new HashMap<Mod_Enable,Integer>();
	
	private Object lockObj = new Object();
	
	private NotifyEventListener listener=new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int eventNum, int arg1, byte[] arg2) {
			LoggerUtil.e("receive:"+eventNum);
			mNotifyNum = eventNum;
			synchronized (lockObj) {
				lockObj.notify();
			}
			return SUCC;
		}
	};
	
	public void event2()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth))
		{
			gui.cls_show_msg("Forth平台不支持NDK_SYS_ResumeEvent接口，任意键退出");
			unitEnd();
			return;
		}
		// 测试前置，梳理当前的机型支持哪些模块
		// 开发反馈重启事件目前只支持射频模块，其他模块暂时不测 modify by 20190201 zhengxq
//		k21Support.put("IccEnable", 0X00000008);
//		k21Support.put("MagEnable", 0X00000004);
		k21Support.put(Mod_Enable.RfidEnable, 0X00000010);
//		k21Support.put("PinEnable", 0X00000020);
//		k21Support.put("PrintEnableReg", 0X00000040);
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RfidEnable))
			supportAll.add(Mod_Enable.RfidEnable);
		else
			k21Support.remove(Mod_Enable.RfidEnable);
//		for (int i = 0; i < GlobalVariable.gAllK21Module.size(); i++) 
//		{
//			if(GlobalVariable.gAllK21Module.get(i).equals("PrintEnable")||GlobalVariable.gAllK21Module.get(i).equals("SecAndroidEnable"))
//				continue;
//			if(GlobalVariable.gModuleEnable.get(GlobalVariable.gAllK21Module.get(i))==false)
//				k21Support.remove(GlobalVariable.gAllK21Module.get(i));
//			else
//				supportAll.add(GlobalVariable.gAllK21Module.get(i));
//		}
		if(supportAll.size()<=0)
		{
			gui.cls_show_msg("遍历后无支持的事件");
			return;
		}
		
		
		int nKeyIn = gui.cls_show_msg("%s\n0.单元测试\n1.权限测试\n2.时间测试\n", TESTITEM);
		switch (nKeyIn) {
		case '0':
			unitTest();
			break;
			
		case '1':
			permitTest();
			break;
			
		case '2':
			timeTest();
			break;
			
		case ESC:
			unitEnd();
			return;

		default:
			break;
		}
	}
	
	/**
	 * 权限测试
	 */
	private void permitTest()
	{
		int iRet = -1;
		// case1:异常测试，未声明权限，应返回NDK_ERR_POSNDK_PERMISSION_UNDEFINED
		if(gui.cls_show_msg("请确保进入本case前Manifest.xml文件未申明android.permission.MANAGE_NEWLAND,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1(5, "请先确保Manifest.xml文件未申明任何权限再进入本测试用例");
			return;
		}
		if((iRet = JniNdk.JNI_SYSResumeEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue()))!=NDK_ERR_POSNDK_PERMISSION_UNDEFINED)
		{
			gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		gui.cls_show_msg1(1, "权限异常测试完毕，测试通过");
	}
	
	/**
	 * 单元测试
	 */
	private void unitTest()
	{
		 /*private & local definition */
		int iRet = -1;
		int eventNum;
		SecKcvInfo kcvInfo = new SecKcvInfo();
		byte[] szPinOut=new byte [9];
		
		// 参数相关的异常
		gui.cls_printf("参数相关异常测试".getBytes());
		// case1.1:参数异常测试，应返回NDK_ERR_PARA
		// case1.2:传入错误的事件号(不存在的事件号或大于取值范围的事件号)，应返回NDK_ERR_POSNDK_EVENT_NUM
		LoggerUtil.v("unitTest case1 start===");
		if((iRet = JniNdk.JNI_SYSResumeEvent(-1))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = JniNdk.JNI_SYSResumeEvent(6))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = JniNdk.JNI_SYSResumeEvent(0X000100100))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		LoggerUtil.v("unitTest case1 end===");
		
		// case2:流程相关的异常
		gui.cls_printf("流程相关异常测试".getBytes());
		
		// case2.1:未注册过事件，无法重启事件(未注册过事件调用重启事件都是返回-4006)
		LoggerUtil.v("unitTest case2.1 start===");
		eventNum = k21Support.get(supportAll.get(0));
		if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_ERR_POSNDK_EVENT_NUM)
		{
			gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		LoggerUtil.v("unitTest case2.1 end===");
		
		// case2.2:注册了一个事件，在事件还未返回时调用重启事件，应返回NDK_EVENT_BUSY(resumeEvent没有互斥机制 modify 20180126)
		// 修改为注册事件后立即重启事件，仍应能够监听到事件触发
		if (k21Support.containsKey(Mod_Enable.MagEnable)==true) 
		{
			LoggerUtil.v("unitTest case2.2 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_Mag_Open();
			if ((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 30 * 1000, listener)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, iRet);
				return;
			}
			if ((iRet = JniNdk.JNI_SYSResumeEvent(eventNum)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s流程异常测试失败(%d)", Tools.getLineInfo(), TESTITEM, iRet);
				if (GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg("请在30s内刷磁卡，刷卡完毕任意键继续");
			if (mNotifyNum != eventNum) {
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:监听磁卡事件失败(%d)", Tools.getLineInfo(), mNotifyNum);
				return;
			}
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			LoggerUtil.v("unitTest case2.2 end===");
		}
		
		// case2.3:多次重启事件，应返回NDK_ERR_POSNDK_BUSY，据严彬反馈不会有问题，多次重启事件均返回成功
		if(k21Support.containsKey(Mod_Enable.RfidEnable)==true)
		{
			LoggerUtil.v("unitTest case2.3 start===");
			mNotifyNum = 0;
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			gui.cls_show_msg("请在感应区放置任意射频卡后点击任意键继续");
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			// 等待事件回调完毕
			synchronized (lockObj) {
				try {
					lockObj.wait(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s未监听到射频事件(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				return;
			}
			gui.cls_show_msg("请移开射频卡，完毕点击任意键继续");
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s流程异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s流程异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			LoggerUtil.v("unitTest case2.3 end===");
			
			// case3:正常测试
			LoggerUtil.v("unitTest case3 start===");
			if(JniNdk.JNI_Rfid_Init(null)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			if((iRet=JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_AB.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			
			// case3:原先在register注册事件后，回调操作完毕后调用Deactivate会再次注册事件，新固件该功能已去除
			mNotifyNum = 0;
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			gui.cls_show_msg("请在感应区放置射频卡后点击任意键继续");
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 60*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			synchronized (lockObj) {
				try {
					lockObj.wait(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s监听射频事件失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				return;
			}
			mNotifyNum = 0;
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 10);
			if(mNotifyNum==eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:射频卡下电后再次寻卡成功", Tools.getLineInfo());
				return;
			}
			if((iRet = JniNdk.JNI_SYSUnRegisterEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			LoggerUtil.v("unitTest case3 end===");
		}
		
		if(k21Support.containsKey(Mod_Enable.MagEnable))
		{
			// case4:测试磁卡的重启事件，重启事件仍应被监听到，注销监听后重启事件应失败，并且无法监听到事件
			// case4.1:注册mag监听事件
			LoggerUtil.v("unitTest case4.1 start===");
			JniNdk.JNI_Mag_Open();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 60*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			gui.cls_show_msg("请在30s内刷磁卡，刷卡完后按任意键继续");
			if(mNotifyNum == eventNum)
				gui.cls_show_msg1(1, "检测刷卡事件，本case测试成功");
			LoggerUtil.v("unitTest case4.2 end===");
			
			// case4.2:重启mag事件仍应被监听到(根据王震懿确认，只读走磁卡数据是不会重启事件，需要关闭磁卡再开启磁卡才可以 modify 20180126)
			LoggerUtil.v("unitTest case4.2 start===");
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_Mag_Open();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请在30s内刷磁卡，刷卡完后按任意键继续");
			if(mNotifyNum == eventNum)
				gui.cls_show_msg1(1, "重启事件监听到检测刷卡事件");
			else
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:重启事件未监听到刷卡事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case4.2 end===");
			
			// case4.3:mag事件注销后重启事件无效并且监听不到任何事件（严彬反馈说返回值要修改为-4006，注销事件之后都返回-4006）
			LoggerUtil.v("unitTest case4.3 start===");
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			JniNdk.JNI_Mag_Close();
			JniNdk.JNI_Mag_Open();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_ERR_POSNDK_EVENT_NUM)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请在30s内刷磁卡，刷卡完后按任意键继续");
			if(mNotifyNum==eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:注销事件后仍可以监听到事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Mag_Close();
			LoggerUtil.v("unitTest case4.2 end===");
		}
		
		if(k21Support.containsKey(Mod_Enable.IccEnable)==true)
		{
			// case5:测试IC卡的重启事件，重启事件后应可监听到事件发生，注销监听后重启事件应失败，并且无法监听到事件
			// case5.1:注册IC监听事件
			LoggerUtil.v("unitTest case5.1 start===");
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			gui.cls_show_msg("请在30秒插入IC卡后按任意键继续");
			if(mNotifyNum==eventNum)
				gui.cls_show_msg("监听到IC卡插入，请拔出IC卡，完成后任意键");
			LoggerUtil.v("unitTest case5.1 end===");
			
			// case5.2:重启IC卡事件后仍应被监听到
			LoggerUtil.v("unitTest case5.2 start===");
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请在30S内插入IC卡后按任意键");
			if(mNotifyNum==eventNum)
				gui.cls_show_msg1(1, "重启事件监听到IC卡事件");
			else
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:重启事件未监听到IC卡事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case5.2 end===");
			
			// case5.3:IC注销后重启事件应无效并且监听不到任何事件
			LoggerUtil.v("unitTest case5.2 start===");
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_ERR_POSNDK_EVENT_NUM)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请在30s内插入IC卡后按任意键");
			if(mNotifyNum==eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:注销事件后仍可以监听到事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case5.2 end===");
		}

		
		// case6:测试非接卡的重启事件，重启事件后应可监听到事件发生，注销监听后重启事件应失败，并且无法监听到事件
		// case6.1:注册非接卡事件
		if(k21Support.containsKey(Mod_Enable.RfidEnable)==true)
		{
			LoggerUtil.v("unitTest case6 start===");
			if(JniNdk.JNI_Rfid_Init(null)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			if((iRet=JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_AB.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 60*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			gui.cls_show_msg("请在30秒内放置射频卡后按任意键");
			if(mNotifyNum==eventNum)
				gui.cls_show_msg1(1, "检测到射频卡放置");
			LoggerUtil.v("unitTest case6.1 end===");
			
			// case6.2:重启事件，应可监听到非接卡放置
			LoggerUtil.v("unitTest case6.2 start===");
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请在30秒内放置射频卡后按任意键");
			JniNdk.JNI_Sys_Delay(10);
			if(mNotifyNum==eventNum)
				gui.cls_show_msg1(1, "重启事件监听到非接卡放置");
			else
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:重启事件未监听到非接卡放置(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case6.2 end===");
			
			// case6.3:注销非接卡事件，注销后重启事件无效并且监听不到事件发生
			LoggerUtil.v("unitTest case6.3 start===");
			if((iRet=JniNdk.JNI_SYSUnRegisterEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:注销非接事件失败(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
				
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_ERR_POSNDK_EVENT_NUM)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请在30秒内放置射频卡后按任意键");
			if(mNotifyNum==eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:注销事件后仍可以监听到非接事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case6.3 end===");
		}

		
		// case7:测试PIN输入事件，重启事件后应可监听到事件发生，注销监听后重启事件应失败，并且无法监听到事件
		//前置://安装TPK1(8bytes), ID=1 明文安装
		if(k21Support.containsKey(Mod_Enable.PinEnable)==true)
		{
			LoggerUtil.v("unitTest case7.1 start===");
			if((iRet = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)0, (byte)1, 8, 
					ISOUtils.hex2byte("19191919191919191919191919191919"),kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			if ((iRet = touchscreen_getnum(str)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(), TESTITEM,iRet);
					return;
			}
			// case7.1:注册PIN事件
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_PIN.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 120*100, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			if ((iRet = touchscreen_getnum(str)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(), TESTITEM,iRet);
					return;
			}
			gui.cls_printf("请尽快按确认键...".getBytes());//阻塞式可以用,非阻塞式也可以用 
			Arrays.fill(szPinOut, (byte)0);
			if((iRet=JniNdk.JNI_Sec_GetPin((byte)1, "0", null, szPinOut, (byte)4, PINTIMEOUT_MAX))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			JniNdk.JNI_Sys_Delay(5);////延时0.5s,可能底层处理需要点时间
			if(mNotifyNum != eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				return;
			}
			else
				gui.cls_show_msg1(1, "监听到密码键盘按键");
			LoggerUtil.v("unitTest case7.1 end===");
			
			// case7.2:重启PIN事件，仍可以监听到PIN事件
			LoggerUtil.v("unitTest case7.2 start===");
			if ((iRet = touchscreen_getnum(str)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(), TESTITEM,iRet);
					return;
			}
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_PIN.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_printf("请尽快按确认键...".getBytes());//阻塞式可以用,非阻塞式也可以用 
			Arrays.fill(szPinOut, (byte)0);
			if((iRet=JniNdk.JNI_Sec_GetPin((byte)1, "0", null, szPinOut, (byte)4, PINTIMEOUT_MAX))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			JniNdk.JNI_Sys_Delay(5);////延时0.5s,可能底层处理需要点时间
			if(mNotifyNum == eventNum)
				gui.cls_show_msg1(1, "重启事件监听到密码键盘按键");
			else
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				return;
			}
			LoggerUtil.v("unitTest case7.2 end===");
			
			// case7.3:PIN事件注销，重启事件无效并且监听不到事件触发（严彬返回返回值要修改为-4006）
			LoggerUtil.v("unitTest case7.3 start===");
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_ERR_POSNDK_EVENT_NUM)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if ((iRet = touchscreen_getnum(str)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(), TESTITEM,iRet);
					return;
			}
			gui.cls_printf("请尽快按确认键...".getBytes());
			Arrays.fill(szPinOut, (byte)0);
			if((iRet=JniNdk.JNI_Sec_GetPin((byte)1, "0", null, szPinOut, (byte)4, PINTIMEOUT_MAX))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Sys_Delay(5);////延时0.5s,可能底层处理需要点时间
			if(mNotifyNum == eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:注销事件后仍可以监听到PIN事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case7.3 end===");
		}

		
		if(k21Support.containsKey(Mod_Enable.PrintEnableReg)==true)//支持打印模块
		{
			// case8:测试打印事件，重启事件后应可监听到事件发生，注销监听后重启事件应失败，并且无法监听到事件（打印从busy到ok会触发事件）
			LoggerUtil.v("unitTest case8.1 start===");
			if((iRet=JniNdk.JNI_Print_Init(0)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:打印初始化测试失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}	
			gui.cls_show_msg("请确保装不足10cm的打印纸后按任意键继续...");
			// case8.1:注册打印事件
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			JniNdk.JNI_Print_Str("打印新大陆NEWLAND打印测试页1:将打印1---80行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19"
					+ "\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45"
					+ "\n46\n47\n48\n49\n50\n51\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80\n");
			
			if(mNotifyNum==eventNum)
				gui.cls_show_msg("监听到打印机状态变化，请确保装不足10cm的打印纸后按任意键继续");
			LoggerUtil.v("unitTest case8.1 end===");
			
			// case8.2:重启打印事件
			LoggerUtil.v("unitTest case8.2 start===");
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Print_Str("打印新大陆NEWLAND打印测试页1:将打印1---80行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19"
					+ "\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45"
					+ "\n46\n47\n48\n49\n50\n51\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80\n");
			
			if(mNotifyNum==eventNum)
				gui.cls_printf("重启事件监听到打印状态变化".getBytes());
			else
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:重启事件未监听到打印状态变化(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case8.2 end===");
			
			// case8.3:注销打印事件后，重启打印事件应无效并且监听不到事件触发
			LoggerUtil.v("unitTest case8.3 start===");
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			gui.cls_show_msg("请确保装不足10cm的打印纸后按任意键");
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_ERR_POSNDK_EVENT_NUM)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Print_Str("打印新大陆NEWLAND打印测试页1:将打印1---80行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19"
					+ "\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45"
					+ "\n46\n47\n48\n49\n50\n51\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80\n");
			
			if(mNotifyNum==eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:注销事件后仍可以监听到打印事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("unitTest case8.3 end===");
		}

		gui.cls_show_msg1_record(TAG, "event2",gScreenTime, "%s测试通过", TESTITEM);
	}
	
	/**
	 * 注册时间的时间以及重启时间的时间测试
	 */
	public void timeTest()
	{
		 /*private & local definition */
		int iRet = -1;
		int eventNum;
		
		// 测试前置：注销所有事件
		for(Mod_Enable name :supportAll)
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(name));
		
		if(k21Support.containsKey(Mod_Enable.RfidEnable)==true)
		{
			// case1:射频卡的重启时间时间的测试
			// case1.1:射频卡的重启事件后超时未触发事件，应超时
			gui.cls_printf("case1.1:非接卡重启事件后超时未触发事件".getBytes());
			JniNdk.JNI_Rfid_Init(null);
			JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_AB.getValue());
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_SYSRegisterEvent(eventNum, 60*1000, listener);
			JniNdk.JNI_Sys_MsDelay(25*1000);// 延时25s
			gui.cls_show_msg("请在5s内放置非接卡，放置完毕后任意键继续");
			if(eventNum!=mNotifyNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:未监听到非接卡放置事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			else
				gui.cls_show_msg("已检测到非接卡放置，请移开非接卡后按任意键继续");
			// 该case暂时有问题，开发还未修复;NDK_SysResumeEvent接口会重新计时，重启后超时时间刷新为60s 20200630
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s重启非接卡事件失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Sys_MsDelay(60*1000);// 延时60s应超时
			gui.cls_show_msg("请在2s内放置射频卡，放置完毕后任意键继续");
			if(eventNum==mNotifyNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:重启事件超时后监听到非接卡放置事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			else
				gui.cls_show_msg("请移开非接卡后任意键继续");
			
			// case1.2:射频卡的重启事件后在最后几s触发事件，应能正常接收事件
			JniNdk.JNI_SYSRegisterEvent(eventNum, 30*1000, listener);// 需注册事件之后才能进行重启事件操作
			gui.cls_printf("case3.2:非接卡重启事件在最后5s触发事件，请耐心等待".getBytes());
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSResumeEvent(eventNum))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:%s重启非接卡事件失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Sys_MsDelay(23*1000);// 延时23s应超时
			gui.cls_show_msg("请在7s内放置非接卡，放置完毕后任意键继续");
			if(eventNum!=mNotifyNum)
			{
				gui.cls_show_msg1_record(TAG, "event2", gKeepTimeErr, "line %d:重启事件超时未监听到非接卡放置事件(%d)", Tools.getLineInfo(),mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 0);
			gui.cls_show_msg1_record(TAG, "event2",gScreenTime,"%s时间测试测试通过",TESTITEM);
		}
		else
			gui.cls_show_msg("不支持射频事件");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// 测试后置：注销所有事件
		for(Mod_Enable name :supportAll)
			JniNdk.JNI_SYSUnRegisterEvent(k21Support.get(name));
	}

}
