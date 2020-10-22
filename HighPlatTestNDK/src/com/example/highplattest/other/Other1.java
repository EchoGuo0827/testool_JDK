package com.example.highplattest.other;

import android.annotation.SuppressLint;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other1.java 
 * history 		 	: 变更点			  	变更时间			变更人员
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Other1 extends UnitFragment
{
	public final String TAG = Other1.class.getSimpleName();
	private final String TESTITEM = "特殊符号显示";
	private Gui gui = new Gui(myactivity, handler);

	public void other1()
	{
		/* private & local definition */
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other1", gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
		}
		/*process body*/
		gui.cls_show_msg1(2, "本行显示美元符号：$$$$$$$$$$$$\n");
		gui.cls_show_msg1(2, "本行显示人民币符号：￥￥￥￥￥￥￥\n");
		gui.cls_show_msg1(2, "本行显示英镑符号：££££££££££££££\n");
		gui.cls_show_msg1(2, "本行显示欧元符号：€€€€€€€€€€€€€\n");
		gui.cls_show_msg1(2, "本行显示伊朗符号：﷼﷼﷼﷼﷼﷼﷼﷼﷼﷼\n");
		gui.cls_show_msg1(2, "本行显示印尼盾符号：₨₨₨₨₨₨₨₨₨₨₨₨₨₨\n");
		gui.cls_show_msg1(2, "本行显示瑞士符号：FrFrFrFrFrFrFrFr\n");
		gui.cls_show_msg1(2, "本行显示泰铢符号：฿฿฿฿฿฿฿฿฿฿฿฿฿฿\n");
		gui.cls_show_msg1(2, "本行显示老挝符号：₭₭₭₭₭₭₭₭₭₭₭₭₭₭\n");
		gui.cls_show_msg1(2, "本行显示百分号：%s\n", "%");
		gui.cls_show_msg1(2, "本行显示千分号：‰ ‰ ‰ ‰ ‰\n");
		gui.cls_show_msg1(2, "本行显示无穷大：∞ ∞ ∞ ∞ ∞ ∞ ∞ ∞\n");
		gui.cls_show_msg1(2, "%s显示完毕", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
		
	}
}
