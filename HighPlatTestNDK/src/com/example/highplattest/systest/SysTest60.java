package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest60.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20151102
 * directory 		: 
 * description 		: 磁卡/键盘
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20151102		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest60 extends DefaultFragment
{
	private final String TAG = SysTest60.class.getSimpleName();
	private final String TESTITEM = "磁卡/键盘";//物理键盘
	Gui gui = new Gui(myactivity, handler);
	
	public void systest60() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("磁卡/键盘\n0.运行");
			switch (returnValue) 
			{
			case '0':
				try
				{
					mag_input();
				}catch(Exception e){
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, TAG, g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	public void mag_input() 
	{
		gui.cls_show_msg1(2, "%s测试中", TESTITEM);
		
		/*private & local definition*/
		int cnt = 0,bak = 0,succ = 0;
		PacketBean packet = new PacketBean();
		byte[] pszBuf = new byte[20];
		int[] pnLen = new int[1];
		int ret = -1;
		
		/*process body*/
		// 设置压力次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		cnt =bak =  packet.getLifecycle();
		// 磁卡注册
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(), maglistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件注册失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		while(cnt>0)
		{
			if(gui.cls_show_msg1(3, "%s交叉测试，已执行%d次，成功%d次，【取消】退出测试", TESTITEM,bak-cnt,succ)==ESC)
				break;
			// 进行磁卡操作
			cnt--;
			//磁卡刷卡
			if((ret = MagcardReadTest(TK1_2_3, true, TIMEOUT_CARDREADER)) != STRIPE)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次：磁卡刷卡测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			gui.cls_printf("请在30s内输入123456789012345后按确认".getBytes());
			if((ret = getKeyInput(pszBuf, pnLen))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次：物理按键测试失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if(new String(pszBuf).substring(0, pnLen[0]).equals("123456789012345")==false)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次：物理按键输入与输出错误(out=%s)", Tools.getLineInfo(),bak-cnt,new String(pszBuf).substring(0, pnLen[0]));
				continue;
			}                       
			succ++;
		}
		//解绑磁卡事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM, g_time_0, "%s测试完成,总共测试%d次成功%d次\n", TESTITEM, bak, succ);
	}
}
