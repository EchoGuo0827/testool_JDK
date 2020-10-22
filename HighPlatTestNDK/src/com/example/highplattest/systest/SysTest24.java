package com.example.highplattest.systest;

import java.util.List;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest24.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150415
 * directory 		: 
 * description 		: RF/ICSAM交叉测试
 * related document :
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		 测试前置添加解绑事件			20200415	 	郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest24 extends DefaultFragment
{
	private final String TAG = SysTest24.class.getSimpleName();
	private final String TESTITEM = "RF/ICSAM";
	_SMART_t rfType = _SMART_t.CPU_A;
	private List<EM_ICTYPE> icSamList ;
	private EM_ICTYPE icSam ;
	private Gui gui = null;
	private Config config;
	private int felicaChoose=0;
	private int ret=-1;
	public void systest24() 
	{
		gui = new Gui(myactivity, handler);
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		//初始化处理器，连接K21设备
		config = new Config(myactivity, handler);
		// 自动测试
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			// 若要修改交叉次数可修改g_CycleTime的值
			icSamList = config.conf_icsam();
			rfType = config.rfid_config();
			if(rfType==_SMART_t.FELICA){
				felicaChoose=config.felica_config();
			}
			for(EM_ICTYPE icSamChoose:icSamList){
				if(GlobalVariable.sdkType==SdkType.SDK3 && icSamChoose==EM_ICTYPE.ICTYPE_IC)
					continue;
				icSam=icSamChoose;
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
			}
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("RF/ICSAM\n0.ICSAM配置\n1.RF配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				//ICSAM配置
				icSam = config.conf_icsam().get(0);
				break;
				
			case '1':
				//RF配置
				rfType = config.rfid_config();
				if(rfType==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				int ret = -1;
				if((ret = rfidInit(rfType))!=NDK_OK)
					gui.cls_show_msg1(0, "line %d:初始化失败！请检查配置是否正确(%d)", Tools.getLineInfo(),ret);
				else
					gui.cls_show_msg1(1,"%s初始化成功!!!", rfType);
				break;
				
			case '2':
				try 
				{
					//交叉测试
					cross_test();
				} catch (Exception e) 
				{
					e.printStackTrace();
					gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		final PacketBean packet = new PacketBean();
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		int[] cpuRevLen = new int[1];
		// 设置交叉次数20次
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 2));
		bak = cnt = packet.getLifecycle();
		//提示信息
		gui.cls_show_msg("测试前请确保已经配置过，并已安装smart卡，完成点任意键继续");
		// 注册事件
		if ((ret = SmartRegistEvent(rfType)) != NDK_OK&&(ret = SmartRegistEvent(rfType)) != NDK_NO_SUPPORT_LISTENER) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s卡事件注册失败(%d)",rfType,Tools.getLineInfo(), ret);
			return;
		}
		if (icSam == EM_ICTYPE.ICTYPE_IC)
		{
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(), icclistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:ic事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
		}
		while(cnt > 0)
		{
			//保护动作
			rfidDeactive(rfType,0);
			icSamPowerOff(icSam);
			
			/*process body*/
			//测试退出点
			if(gui.cls_show_msg1(2, "%s/%s交叉测试,已执行%d次,成功次数:%d次,【取消】退出测试" , rfType,icSam,bak-cnt,succ)==ESC)
				break;
			if(GlobalVariable.sdkType==SdkType.SDK3&&icSam==EM_ICTYPE.ICTYPE_IC)
			    gui.cls_show_msg("请插拔IC卡，完成任意键继续");
			
			if((ret = rfid_detect(rfType,UidLen,UidBuf))!=NDK_OK)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s寻卡失败（%d）", Tools.getLineInfo(),bak-cnt,rfType,ret);
				continue;
			}
			// 寻卡上电
			if((ret = rfidActive(rfType,felicaChoose,UidLen,UidBuf)) != NDK_OK)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s激活失败（%d）", Tools.getLineInfo(),bak-cnt,rfType,ret);
				continue;
			}
			// ICSAM上电
			if((ret = iccPowerOn(icSam,psAtrBuf,pnAtrLen)) != NDK_OK)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s上电失败（ %d）",Tools.getLineInfo(),bak-cnt,icSam,ret);
				continue;
			}
			
			// APDU读写
			while(cnt>0)
			{
				if(gui.cls_show_msg1(3, "%s/%s交叉测试,已执行%d次,成功次数:%d次,【取消】退出测试" , rfType,icSam,bak-cnt,succ)==ESC)
					break;
				cnt--;
				if((ret = rfidApduRw(rfType,cpuRevLen,UidBuf)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次：%sAPDU失败（%d）",Tools.getLineInfo(),bak-cnt,rfType, ret);
					break;
				}
				if((ret = iccRw(icSam,req,null)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次：%sAPDU失败（%d）",Tools.getLineInfo(),bak-cnt, icSam,ret);
					break;
				}
				succ++;
			}
			// 下电
			if((ret = rfidDeactive(rfType,0)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次：%s关闭场失败（%d）", Tools.getLineInfo(),bak-cnt,rfType,ret);
				continue;
			}
			if((ret = icSamPowerOff(icSam)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次：%s下电失败（%d）",Tools.getLineInfo(),bak-cnt, icSam,ret);
				continue;
			}
		}
		postEnd();
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"(%s/%s)交叉测试完成，已执行次数为%d，成功为%d次" ,rfType,icSam,bak-cnt,succ);
	}
	
	/*//交叉测试具体实现函数
	private void ic_rf_active(final _SMART_t type,int[] UidLen,byte[] UidBuf) 
	{
		int cnt = 0, bak = 0, ret = 0, succ=0;
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
		int[] cpuRevLen = new int[1];
		
		//提示信息
		if(gui.cls_show_msg("此测试需要一张即有IC卡又有射频卡功能的多功能卡，并插入卡槽，确认键继续测试" + type)==ENTER)
		{
			handler.sendEmptyMessage(HandlerMsg.DIALOG_SYSTEST_BACK);
		}
				
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		while(cnt-- > 0)
		{
			//测试退出点
			if(gui.cls_show_msg1(3, "第%d次交叉测试(已成功%d次),返回键退出测试",bak-cnt,succ)==ESC)
				break;
			
			switch(type)
			{
				case CPU_A:
				case CPU_B:
					rfidActive(type,felicaChoose,UidLen,UidBuf);
					break;
				case MIFARE_1:
				case ISO15693:
					rfidActive(type,felicaChoose,UidLen,UidBuf);
					rfidApduRw(type,cpuRevLen,UidBuf);
					break;
				default:
					break;
			}
			if((ret = iccPowerOn(icSam,psAtrBuf,pnAtrLen)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "ic_rf_active",g_keeptime, "line %d:第%d次：icsam上电失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((ret = icSamPowerOff(icSam)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "ic_rf_active",g_keeptime, "line %d:第%d次：icsam下电失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "ic_rf_active",g_time_0, "%s交叉测试完成，已执行次数为%d，成功为%d次", TESTITEM,bak-cnt,succ);
	}*/
	
	public void postEnd()
	{
		// 解绑事件
		if ((ret = SmartUnRegistEvent(rfType)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s卡事件解绑失败(%d)",Tools.getLineInfo(),rfType, ret);
			return;
		}
		// 解绑事件
		if (icSam == EM_ICTYPE.ICTYPE_IC)
		{
			if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:ic事件解绑失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
		}
	}
}
