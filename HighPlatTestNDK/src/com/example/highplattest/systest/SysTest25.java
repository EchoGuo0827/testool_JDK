package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest25.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20151222
 * directory 		: 
 * description 		: 触屏/RF交叉测试
 * related document :
 * history 		 	: 变更记录			变更时间				变更人员
 * 					  测试前置添加解绑事件          20200415         	郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)*/
public class SysTest25 extends DefaultFragment
{
	private final String TAG = SysTest25.class.getSimpleName();
	private final String TESTITEM = "触屏/RF";
	_SMART_t type = _SMART_t.CPU_A;
	Gui gui = new Gui(myactivity, handler);
	private Config config;
	private int felicaChoose=0;
	public void systest25()
	{
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
		while (true) 
		{
			int returnValue=gui.cls_show_msg("触屏/RF\n0.RF配置\n1.交叉测试");
			switch (returnValue) 
			{
			case '0':
				type = config.rfid_config();
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(smartInit(type)!=NDK_OK)
				{
					gui.cls_show_msg1(g_time_0, "line %d:smart卡初始化失败", Tools.getLineInfo());
				}
				break;
				
			case '1':
				try
				{
					cross_test();
				}catch(Exception e){
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime,"line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;

			}
		}
	}
	
	public void cross_test() 
	{
		/*private & local definition*/
		int ret = 0;
		int bak = 0,succ = 0, cnt;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		// 设置压力次数
		PacketBean sendPacket = new PacketBean();
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		gui.cls_show_msg("请确保已安装%s卡，完成任意键继续", type);
		// 注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:smart事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}		

		/*process body*/
		while(cnt>0)
		{
			// 保护动作
			smartDeactive(type);
			if(gui.cls_show_msg1(2, "触屏/%s交叉测试，已执行%d次，成功次数：%d次，【取消】退出测试" , type,bak-cnt, succ)==ESC)
				break;
			cnt--;
			// smart初始化
			if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s检测失败(%d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			// 触屏测试
			ret = systestTouch();
			if(ret!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:触屏测试失败(实际(%d,%d)",Tools.getLineInfo(),bak-cnt,(int)GlobalVariable.gScreenX,(int)GlobalVariable.gScreenY);
				continue;
			}
			//射频卡激活
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡激活失败(%d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//射频卡读写
			if((ret = smartApduRw(type,req,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡APDU失败(%d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//射频卡下电
			if((ret = smartDeactive(type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s卡关闭场失败(%d)",Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			succ++;
		}
		// 解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件解绑失败(%d)",Tools.getLineInfo(), type,ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0, "触屏/%s交叉测试完成,已执行次数为%d,成功为%d次", type,bak-cnt,succ);
	}
}
