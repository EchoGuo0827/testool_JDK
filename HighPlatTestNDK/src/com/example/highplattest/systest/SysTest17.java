package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest17.java
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150317
 * directory 		: 
 * description 		: 磁卡/smart卡交叉测试用例
 * related document : 
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		  测试前置添加所有事件解绑		20200415	 	郑薛晴
 *					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest17 extends DefaultFragment
{
	_SMART_t type = _SMART_t.CPU_A;
	private  String TESTITEM = "磁卡/SMART";
	private final String TAG = SysTest17.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	private Config config;
	private int felicaChoose=0;
	private int ret=-1;
	
	public void systest17() 
	{
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 测试前置:解绑事件
		UnRegistAllEvent(new EM_SYS_EVENT[]
				{EM_SYS_EVENT.SYS_EVENT_MAGCARD,EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("磁卡/SMART\n0.配置\n1.交叉压力");
			switch (returnValue) 
			{	
			case '0':
				int ret = -1;
				//smart配置
				type = config.smart_config().get(0);
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if((ret  = smartInit(type))!=NDK_OK)
				{
					gui.cls_show_msg1(g_keeptime, "line %d:初始化smart卡失败(%d)", Tools.getLineInfo(),ret);
				}
				break;
				
			case '1':
				try
				{
					mag_smart(type);
				}catch(Exception e){
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				
				break;
				
			case ESC:
				intentSys();
				return;
				
			}
		}
		
	}
	
	//磁卡 smart交叉测试
	public void mag_smart(_SMART_t type) 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
	
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT,ABILITY_VALUE));;
		bak = cnt = packet.getLifecycle();
		
		//提示信息
		gui.cls_show_msg("测试前请确保已经配置过,并已安装smart卡,完成任意键继续");
		//samrt注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER) 
		{
			gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:%s事件注册失败(%d)",type,Tools.getLineInfo(), ret);
			return;
		}
		//mag注册事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(3, "%s/磁卡交叉测试,已执行%d次,成功%d次,[取消]退出测试...", type,bak-cnt,succ)==ESC)
				break;
			//保护动作
			smartDeactive(type);
			if(GlobalVariable.sdkType==SdkType.SDK3&&type==_SMART_t.IC)
			    gui.cls_show_msg("请插拔IC卡，完成任意键继续");
			if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:第%d次:%s卡寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//射频卡激活
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK )
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:第%d次:%s卡激活失败（%d）", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//磁卡刷卡
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:第%d次:磁卡刷卡测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			while(cnt > 0)
			{
				//测试退出点
				if(gui.cls_show_msg1(3, "磁卡/SMART交叉测试,已执行%d次,成功%d次,[取消]退出测试....",bak-cnt,succ)==ESC)
					break;
				cnt--;
				//射频卡读写
				if((ret = smartApduRw(type,req,UidBuf)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:第%d次:%s读写失败(%d)",Tools.getLineInfo(),bak-cnt,type,ret);
					break;
					
				}
				//磁卡刷卡
				if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
				{
					gui.cls_show_msg1_record(TAG, "mag_smart", g_keeptime, "line %d:第%d次:磁卡刷卡测试失败(%d)",Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				succ++;
			}	
		}
		
		//射频卡下电
		smartDeactive(type);
		postEnd();
		gui.cls_show_msg1_record(TAG, "mag_smart", g_time_0,"%s/磁卡交叉测试完成,已执行次数为%d,成功为%d次",type, bak-cnt,succ);
	}
	
	private void postEnd()
	{
		// 测试后置，解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:%s事件解绑失败(%d)",Tools.getLineInfo(), type,ret);
			return;
		}
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
	}
}
