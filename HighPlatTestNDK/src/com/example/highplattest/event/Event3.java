package com.example.highplattest.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.RF_CARD;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
/************************************************************************
 * 
 * module 			: 事件机制之复位卡片
 * file name 		: Event3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180321
 * directory 		: 
 * description 		: 复位卡片(NDK_RfidPiccResetCard(uchar usDelayms)):传入0为关闭场强，传入6-10为合法值
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   	20180321	 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event3 extends UnitFragment{
	private Gui gui = new Gui(myactivity, handler);
	public final String TAG = Event3.class.getSimpleName();
	private final String TESTITEM = "NDK_RfidPiccResetCard";
	private int mNotifyNum;
	
	private List<Mod_Enable> supportAll = new ArrayList<Mod_Enable>();
	private HashMap<Mod_Enable, Integer> k21Support = new HashMap<Mod_Enable,Integer>();
	
	private NotifyEventListener listener=new NotifyEventListener() {
		
		@Override
		public int notifyEvent(int eventNum, int arg1, byte[] arg2) {
			LoggerUtil.e("receive:"+eventNum);
			mNotifyNum = eventNum;
			return SUCC;
		}
	};
	
	public void event3()
	{
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
		
//		for (int i = 0; i < GlobalVariable.gAllK21Module.size(); i++) 
//		{
//			if(GlobalVariable.gAllK21Module.get(i).equals("PrintEnable")||GlobalVariable.gAllK21Module.get(i).equals("SecAndroidEnable"))
//				continue;
//			if(GlobalVariable.gModuleEnable.get(GlobalVariable.gAllK21Module.get(i))==false)
//				k21Support.remove(GlobalVariable.gAllK21Module.get(i));
//			else
//				supportAll.add(GlobalVariable.gAllK21Module.get(i));
//		}
		/*private & local definition*/
		int iRet = -1,eventNum;
		byte[] errPara = {0,-1,11,8};
		byte[] piccDetect = new byte[1];
		byte[] psPiccType = new byte[1];
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		
		/*process body*/
		// case1:传入非法参数以及6-10范围内的参数，以及0，据严彬反馈会修改为正确的值，传入的值<6时，修改值为6，传入的值>10时，修改为10 modify 20180413
		if(k21Support.containsKey(Mod_Enable.RfidEnable)==true)
		{
			LoggerUtil.v("case1 start===");
			gui.cls_show_msg("请放置A，放置完毕点击任意键继续");
			JniNdk.JNI_Rfid_Init(null);
			JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_A.getValue());
			for (int i = 0; i < errPara.length; i++) 
			{
				JniNdk.JNI_Rfid_PiccDetect(piccDetect);
				if ((iRet = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen,psDataBuf)) !=NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,iRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
				if((iRet = JniNdk.JNI_Rfid_PiccResetCard(errPara[i]))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:第%d次:%s参数异常测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,iRet);
					if(GlobalVariable.isContinue == false)
						return;
				}
				// 下电后进行APDU操作应失败，返回-7，此为驱动底下的返回值
				if((iRet =JniNdk.JNI_Rfid_PiccApdu(req.length, req, pnDataLen, psDataBuf))!=-7)
				{
					gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),i+1,TESTITEM,iRet);
					if(GlobalVariable.isContinue == false)
						return;
				}
			}
			LoggerUtil.v("case1 end===");
		}

		
		if(k21Support.containsKey(Mod_Enable.IccEnable)==true)
		{
			// case7:注册其他事件后进行[复位卡片]操作，不影响其他事件监听(例如IC卡的监听)
			LoggerUtil.v("case7 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), 30*1000, listener);
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 100))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg("请在30s内插入IC卡，完成任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v("case7 end===");
		}
		
		// case2:非接卡未上电前进行[复位卡片]应返回成功(参数传入0与非0)
		if(k21Support.containsKey(Mod_Enable.RfidEnable)==true)
		{
			LoggerUtil.v("case2 start===");
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 0))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 100))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("case2 end===");
			
			// case3:非接卡上电后再进行[复位卡片]，此时进行APDU操作应失败
			LoggerUtil.v("case3 start===");
			gui.cls_show_msg("请放置射频卡，放置完毕点击任意键继续(不支持B卡)");
			JniNdk.JNI_Rfid_Init(null);
			JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_A.getValue());
			JniNdk.JNI_Rfid_PiccDetect(piccDetect);
			if((iRet = rfid_powerUp(30))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("case3 end===");
			
			// case4.1:事件机制注册前进行[复位卡片]操作，不影响事件机制流程
			LoggerUtil.v("case4.1 start===");
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 0))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), 30*1000, listener);
			gui.cls_show_msg("请在30s内放置射频卡，放置完毕点击任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请移开放置的射频卡，完成任意键继续");
			LoggerUtil.v("case4.2 end===");
			
			// case4.2:事件机制注册后进行[复位卡片]操作，不影响事件机制流程(JNI_Rfid_PiccResetCard下电后要重启事件才可以监听到非接卡事件)
			LoggerUtil.v("case4.2 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), 30*1000, listener);
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 6))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)==false)
			{
				if((iRet = JniNdk.JNI_SYSResumeEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}

			gui.cls_show_msg("请在30s内放置射频卡，放置完毕点击任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请移开放置的射频卡，完成任意键继续");
			LoggerUtil.v("case4.2 end===");
			
			// case5:监听到事件后进行[复位卡片]操作应正常
			LoggerUtil.v("case5 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 0))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("case5 end===");
			
			// case6.1:重启事件前进行[复位卡片]操作应正常，应可正常监听到事件
			LoggerUtil.v("case6.1 start===");
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)==false)
			{
				JniNdk.JNI_SYSResumeEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			}
			gui.cls_show_msg("请放置射频卡，放置完毕点击任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请移开放置的射频卡，完成任意键继续");
			LoggerUtil.v("case6.1 end===");
			
			// case6.2:重启事件后进行[复位卡片]操作应正常，应可正常监听到事件(非接卡下电之后应无法监听到射频事件，不影响其他事件的监听)
			LoggerUtil.v("case6.2 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)==false)
			{
				JniNdk.JNI_SYSResumeEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			}
			if((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 0))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("请放置射频卡，放置完毕点击任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			LoggerUtil.v("case6.2 end===");
			// 测试后置，下电
			JniNdk.JNI_Rfid_PiccDeactivate((byte) 0);
		}

		gui.cls_show_msg1_record(TAG, "event3",gScreenTime, "%s测试通过", TESTITEM);
	}
	
	
	/**
	 * 射频卡应用的流程
	 */
	private int rfid_powerUp(int nSec)
	{
		int iRet = -1;
		byte[] psPiccType = new byte[1];
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		
		
		// M1卡参数
		byte[] psSakBuf = new byte[1];
		byte[] key = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
		byte[] out=new byte[256];
		
		
		// M1卡的流程
		if ((iRet = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen,psDataBuf)) == NDK_ERR_RFID_PROTOCOL) 
		{

			if ((iRet = JniNdk.JNI_Rfid_M1Request((byte) 1, pnDataLen,psDataBuf)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr,"line %d:M1卡request失败(%d)", Tools.getLineInfo(), iRet);
				return iRet;
			}
			if ((iRet = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr,"line %d:M1卡Anti失败(%d)", Tools.getLineInfo(), iRet);
				return iRet;
			}
			if ((iRet = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], psDataBuf,psSakBuf)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr,"line %d:M1卡select失败(%d)", Tools.getLineInfo(), iRet);
				return iRet;
			}

			if ((iRet = JniNdk.JNI_Rfid_M1ExternalAuthen(pnDataLen[0],psDataBuf, (byte) 0x61, key, (byte) 0x01)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr,"line %d:M1卡外部认证失败(%d)", Tools.getLineInfo(), iRet);
				return iRet;
			}

			if ((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 100)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM, iRet);
				return iRet;
			}

			// 之前认证的块为01，应该01-04块都可进行读写操作
			if ((iRet = JniNdk.JNI_Rfid_M1Read((byte) 0x01, pnDataLen, out)) == NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(), TESTITEM, iRet);
				return iRet;
			}
		} else if (iRet == NDK_OK)// CPU卡的流程
		{
			if ((iRet = JniNdk.JNI_Rfid_PiccResetCard((byte) 100)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:复位卡片失败(%d)",Tools.getLineInfo(), iRet);
				return iRet;
			}
			if ((iRet = JniNdk.JNI_Rfid_PiccApdu(req.length,req, pnDataLen, psDataBuf)) == NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(), iRet);
				return iRet;
			}
		} else {
			gui.cls_show_msg1_record(TAG, "event3", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(), iRet);
			return iRet;
		}
		return NDK_OK;
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
