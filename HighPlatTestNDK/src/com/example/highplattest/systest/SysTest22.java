package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest22.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20151222
 * directory 		: 
 * description 		: 磁卡/触屏交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * 					  zhengxq          20151222         created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)*/
public class SysTest22 extends DefaultFragment 
{
	private final String TAG = SysTest22.class.getSimpleName();
	private final String TESTITEM = "磁卡/触屏";
	Gui gui = new Gui(myactivity, handler);
	
	public void systest22()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while (true) 
		{
			int returnValue=gui.cls_show_msg("磁卡/触屏\n0.交叉测试");
			switch (returnValue) 
			{
			case '0':
				try 
				{
					//交叉测试
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
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
		int ret = -1;
		int bak = 0,succ = 0, cnt;
		// 设置压力次数
		PacketBean sendPacket = new PacketBean();
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		//磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		/*process body*/
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，【取消】退出测试", TESTITEM, bak-cnt,succ)==ESC)
				break;
			cnt--;
			// 触屏测试
			ret = systestTouch();
			if(ret!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：触屏测试失败（实际（%d，%d）",
						Tools.getLineInfo(),bak-cnt,(int) GlobalVariable.gScreenX,(int) GlobalVariable.gScreenY);
				continue;
			}
			
			// 磁卡测试
			if((ret = MagcardReadTest(TK2_3, true, TIMEOUT_CARDREADER))!=STRIPE)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",5, "line %d:第%d次：刷卡失败（ret = %d）", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			succ++;
		}
		//解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
	}
	
}
