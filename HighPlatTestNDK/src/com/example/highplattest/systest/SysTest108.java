package com.example.highplattest.systest;

import android.content.Intent;

import com.example.highplattest.activity.Touch2Activity;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: SysTest108.java 
 * description 		: 触屏综合测试
 * history 		 	: 变更记录					变更人员			变更时间
 *			  		 增加触屏压力测试案例			陈丁				20200513
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest108 extends DefaultFragment  {
	private final String TAG = SysTest108.class.getSimpleName();
	private final String TESTITEM = "触屏综合测试";
	private Gui gui;
	
	public void systest108(){
		gui = new Gui(myactivity, handler);
		while(true)
		{
			int returnValue=gui.cls_show_msg("触屏综合测试\n0.触屏压力测试\n1.触屏边缘测试");
			switch (returnValue) 
			{	
			case '0':
				TouchPre();
				break;
				
			case '1':
				TouchEdge();
				break;
				
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	//触屏边缘测试
	private void TouchEdge() {
		gui.cls_show_msg("按任意键开始触屏边缘测试。");
		Intent intent=new Intent(myactivity,Touch2Activity.class);
		myactivity.startActivity(intent);
		
		if (gui.cls_show_msg("是否所有边缘的格子均可以触碰，且改变颜色,【确定】是  【其他】否")==ENTER) {
			
			gui.cls_show_msg1_record(TAG, "TouchEdge",g_time_0, "触屏边缘测试通过。");

		}else{
			gui.cls_show_msg1_record(TAG, "TouchEdge",g_time_0, "触屏边缘测试失败。");
		}
	}

	//触屏压力
	private void TouchPre() {
		int ret = 0;
		int bak = 0,succ = 0, cnt;
		// 设置压力次数
		PacketBean sendPacket = new PacketBean();
		sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		
		while(cnt>0){
			if(gui.cls_show_msg1(2, "触屏压力测试执行中，已执行%d次，成功次数：%d次，【取消】退出测试" ,bak-cnt, succ)==ESC){
				break;
			}
			cnt--;
			// 触屏测试
			ret = systestTouch();
			if(ret!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "TouchPre",g_keeptime, "line %d:第%d次:触屏测试失败(实际(%d,%d)",Tools.getLineInfo(),bak-cnt,(int)GlobalVariable.gScreenX,(int)GlobalVariable.gScreenY);
				continue;
			}else{
				succ++;
			}
		}
		gui.cls_show_msg1_record(TAG, "TouchPre",g_time_0, "触屏压力测试完成,已执行次数为%d,成功为%d次",bak-cnt,succ);
		
		
	}
}
