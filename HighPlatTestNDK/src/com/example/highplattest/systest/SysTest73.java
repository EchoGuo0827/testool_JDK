package com.example.highplattest.systest;

import android.newland.ndk.security.NdkSecurityManager;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class SysTest73 extends DefaultFragment
{
	private final String TAG = SysTest73.class.getSimpleName();
	private final String TESTITEM = "获取安全寄存器状态压力";
	private Gui gui = null;
	int[] status = new int[1];
	int currentStatus=0;
	private final int DEFAULT_COUNT_VLE = 1000;
	
	public void systest73()
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gSequencePressFlag)
		{
			g_CycleTime=DEFAULT_COUNT_VLE;
			press();
			return;
		}
		while(true)
		{
			int returnValue=gui.cls_show_msg("综合测试\n0.状态配置\n1.压力测试");
			switch (returnValue) 
			{
			
			case '0':
				returnValue=gui.cls_show_msg("当前机器所处状态\n0.无安全攻击\n1.安全攻击");
				switch (returnValue) 
				{
				case '0':
					currentStatus=0;
					break;

				case '1':
					currentStatus=1;
					break;

				default:
					break;
				}
				break;
				
			case '1':
				press();
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	public void press(){
		int cnt = 0, bak = 0, succ = 0;
		int ret=-1;
		NdkSecurityManager ndkSecurityManager = new NdkSecurityManager();
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT_VLE));
		bak = cnt = packet.getLifecycle();//压力次数
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(1, "正在进行第%d次%s(已成功%d次),【取消】退出测试", bak-cnt, TESTITEM,succ)==ESC)
				break;
			cnt--;
			// 返回0操作成功，返回-1操作失败，返回-6参数错误
			if((ret=ndkSecurityManager.getSecTamperStatus(status))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "press", g_keeptime, "line %d:第%d次:获取寄存器状态返回值错误 (ret = %d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			if(status[0]!=currentStatus)
			{
				gui.cls_show_msg1_record(TAG, "press", g_keeptime, "line %d:第%d次：获取寄存器返回状态错误(ret = %d)", Tools.getLineInfo(),bak-cnt,status[0]);
				continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "press",g_time_0, "%s压力测试完成，已执行次数为%d，成功为%d次", TESTITEM,bak-cnt,succ);
	}
	
	
}
