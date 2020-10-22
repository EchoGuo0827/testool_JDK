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
 * module 			: 事件机制之事件挂起
 * file name 		: Event4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180322
 * directory 		: 
 * description 		: 事件挂起(NDK_SysSuspenedEvent(EM_SYS_EVENT event)) (据严彬反馈该接口只支持射频卡事件，其他事件注册调用挂起事件都会返回-3303)
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   	20180322	 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Event4 extends UnitFragment
{
	private Gui gui = new Gui(myactivity, handler);
	private final String TESTITEM = "NDK_SysSuspenedEvent";
	public final String TAG = Event4.class.getSimpleName();
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
	
	public void event4()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth))
		{
			gui.cls_show_msg("Forth平台不支持NDK_SysSuspenedEvent接口，任意键退出");
			unitEnd();
			return;
		}
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
		
		int iRet = -1;
		int eventNum = 0;
		int[] pnSta = new int[1];
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		byte[] mPsRecvBuf = new byte[10];
		int[] mPnRecvLen = new int[1];
		
		int[] pnDataLen = new int[1];
		byte[] psDataBuf = new byte[10];
		// case1:参数异常测试，传入错误的事件号，应返回错误
		LoggerUtil.v("case1 start===");
		if((iRet = JniNdk.JNI_SYSSuspenedEvent(0X00000016))!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		LoggerUtil.v("case1 end===");
		
		// case2.1:流程异常，未有其他事件正在运行，调用挂起磁卡事件，应返回事件不支持（-3303）
		LoggerUtil.v("case2.1 start===");
		int[] events = {EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),EM_SYS_EVENT.SYS_EVENT_PIN.getValue(),EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()};
		for(int i=0;i<events.length;i++)
		{
			if((iRet = JniNdk.JNI_SYSSuspenedEvent(events[i]))!=NDK_ERR_EVENT_UNREALIZED)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(event=%d,ret = %d)", Tools.getLineInfo(),TESTITEM,events[i],iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		LoggerUtil.v("case2.1 end===");

		// case2.2:流程异常，未有其他事件正在运行，调用非接卡的挂起事件，应返回事件未注册（-3301）
		LoggerUtil.v("case2.2 start===");
		if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_ERR_EVENT_UNREGISTER)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		LoggerUtil.v("case2.2 end===");
		
		// case5:注册非接事件(超时时间要尽可能的大)，未挂起该事件，进行M1卡的request操作，会返回SPI总线冲突，驱动调用错误(-5)(实际测试情况，因为在服务那边已经添加了suspend挂起事件操作，所以无法构造SPI冲突的情况)
		LoggerUtil.v("case5 start===");
		JniNdk.JNI_Rfid_Init(null);
		JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_A.getValue());
		if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((iRet = JniNdk.JNI_Rfid_M1Request((byte)1, pnDataLen, psDataBuf))!=NDK_ERR_MI_NOTAGERR)
		{
			gui.cls_show_msg1(gKeepTimeErr, "line %d:M1卡request失败(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
		LoggerUtil.v("case5 end===");
		
		// case7:注册射频卡事件后多次挂起射频事件，不应影响到其他事件的操作(原先设置射频卡为AB现在修改为A)
		LoggerUtil.v("case7 start===");
		JniNdk.JNI_Rfid_Init(null);
		JniNdk.JNI_Rfid_PiccType(RF_CARD.TYPE_A.getValue());
		if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg("请插入IC卡，任意键继续");
		if((iRet = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK&&pnSta[0]!=0x01)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((iRet = JniNdk.JNI_Icc_PowerUp(0, psAtrBuf, pnAtrLen))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((iRet = JniNdk.JNI_Icc_PowerDown(0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		if((iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		LoggerUtil.v("case7 end===");
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IccEnable))
		{
			// case3.1:注册射频卡事件，挂起射频卡事件后应监听不到射频事件，进行IC卡或磁卡操作，应能正常操作IC卡或磁卡
			LoggerUtil.v("case3.1 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			gui.cls_show_msg("请放置射频卡，放置完毕任意键继续");
			if(mNotifyNum==eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			gui.cls_show_msg("请插入IC卡，任意键继续");
			if((iRet = JniNdk.JNI_Icc_Detect(pnSta))!=NDK_OK&&pnSta[0]!=0x01)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			if((iRet = JniNdk.JNI_Icc_PowerUp(0, psAtrBuf, pnAtrLen))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			if((iRet = JniNdk.JNI_Icc_Rw(0, req.length, req, mPnRecvLen, mPsRecvBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			if((iRet = JniNdk.JNI_Icc_PowerDown(0))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			if((iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v("case3.1 end===");
		}
		
		// case6:注册射频卡事件，挂起射频卡卡事件后立即注销射频卡卡事件，不影响后续操作
		LoggerUtil.v("case6 start===");
		if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), 30*1000, listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		LoggerUtil.v("case6 end===");
		
		// case3.2:注册IC卡事件，挂起射频卡事件后进行IC卡操作，应能正常操作IC卡
		if(k21Support.containsKey(Mod_Enable.IccEnable)==true)
		{
			LoggerUtil.v("case3.2 start===");
			eventNum = EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue();
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_ERR_EVENT_UNREGISTER)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg("请放置射频卡，放置完毕任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue == false)
					return;
			}
			
			if((iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			LoggerUtil.v("case3.2 end===");
			
			// case4:注册射频卡事件，挂起射频事件后进行开启IC卡事件，应能正常监听到IC卡插入事件
			LoggerUtil.v("case4 start===");
			if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			gui.cls_show_msg("请在30s内插入IC卡，完成任意键继续");
			if(mNotifyNum!=eventNum)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
				if(GlobalVariable.isContinue == false)
					return;
			}
			// 注销射频和IC卡事件
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
			JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
			LoggerUtil.v("case4 end===");
		}

		// case5.1:注册射频卡事件，挂起密码键盘事件，应不影响到射频卡事件的监听
		LoggerUtil.v("case5.1 start===");
		mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
		eventNum = EM_SYS_EVENT.SYS_EVENT_RFID.getValue();
		if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 30*1000, listener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue()))!=NDK_ERR_EVENT_UNREALIZED)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请在30s内放置射频卡，放置完毕任意键继续");
		if(mNotifyNum!=eventNum)
		{
			gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,mNotifyNum);
			if(GlobalVariable.isContinue == false)
				return;
		}
		JniNdk.JNI_SYSUnRegisterEvent(eventNum);
		LoggerUtil.v("case5.1 end===");
		
		if(k21Support.containsKey(Mod_Enable.PrintEnableReg)==true)
		{
			// case5.2:注册打印事件，挂起射频事件，应不影响到打印事件的监听
			LoggerUtil.v("case5.2 start===");
			if((iRet=JniNdk.JNI_Print_Init(0)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:打印初始化测试失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}	
			gui.cls_show_msg("请确保装不足10cm的打印纸后按任意键继续...");
			mNotifyNum = EM_SYS_EVENT.SYS_EVENT_NONE.getValue();
			eventNum = EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue();
			if((iRet = JniNdk.JNI_SYSRegisterEvent(eventNum, 30*1000, listener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			if((iRet = JniNdk.JNI_SYSSuspenedEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_ERR_EVENT_UNREGISTER)
			{
				gui.cls_show_msg1_record(TAG, "event4", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
			JniNdk.JNI_Print_Str("打印新大陆NEWLAND打印测试页1:将打印1---80行\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19"
					+ "\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n39\n40\n41\n42\n43\n44\n45"
					+ "\n46\n47\n48\n49\n50\n51\n52\n53\n54\n55\n56\n57\n58\n59\n60\n61\n62\n63\n64\n65\n66\n67\n68\n69\n70\n71\n72\n73\n74\n75\n76\n77\n78\n79\n80\n");
			
			if(mNotifyNum==eventNum)
				gui.cls_show_msg("监听到打印机状态变化，请确保装不足10cm的打印纸后按任意键继续");
			
			JniNdk.JNI_SYSUnRegisterEvent(eventNum);
			LoggerUtil.v("case5.2 end");
		}
		
		gui.cls_show_msg1_record(TAG, "event4",gScreenTime, "%s测试通过", TESTITEM);
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
