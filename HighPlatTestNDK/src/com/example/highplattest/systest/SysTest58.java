package com.example.highplattest.systest;

import android.annotation.SuppressLint;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest58.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20151030
 * directory 		: 
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20151030		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest58 extends DefaultFragment
{	
	private final String TAG = SysTest1.class.getSimpleName();
	private final String TESTITEM = "菲波纳契数列";
	private Gui gui = null;
	
	public void systest58() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gSequencePressFlag)
		{
			sysFb();
			return;
		}
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("菲波纳契数列\n0.运行\n");
			switch (nkeyIn) 
			{
			case '0':
				sysFb();
				break;
				
			case ESC:
				intentSys();
				return;

			}
		}
	}
	
	public void sysFb() 
	{
		/*private & local definition*/
		long startTime,time;
		int i = 0;
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中", TESTITEM);
		
		startTime = System.currentTimeMillis();
		for (i = 0; i < 27; i++) 
		{
			// 进行循环计算菲波纳契数列的前27个元素
			fs(i);
		}
		time = System.currentTimeMillis() - startTime;
		
		gui.cls_show_msg1_record(TAG, "sysFb", g_time_0, "计算菲波纳契数列的前27元素耗时："+time+"ms");
	}
	
	public int fs(int num)
	{
		return num<2?num:fs(--num)+fs(--num);
	}
}
