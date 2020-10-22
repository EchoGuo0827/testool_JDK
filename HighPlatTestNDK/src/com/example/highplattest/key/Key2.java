package com.example.highplattest.key;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android设定APP获取KEY
 * file name 		: Key2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150623 
 * directory 		: 
 * description 		: 获取recentAPP的key
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *					 recentApp键的值修改为82		20200414		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Key2 extends UnitFragment 
{
	private String TESTITEM = "RECENTAPP_KEY";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName="Key2";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		myactivity.getWindow().addFlags(3);
		myactivity.getWindow().addFlags(5);
		return view;
	}
	
	public void key2() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"key2",gScreenTime,"%s不支持自动测试，请使用手动验证",TESTITEM);
			return;
		}
		/* private & local definition */
		int keyCode = -1;
	
		
		/*process body*/
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		gui.cls_show_msg("点击RECENTAPP键,点击完毕点[确认]继续");
		keyCode = GlobalVariable.virtualKey;
		if(keyCode!=82)
		{
			gui.cls_show_msg1_record(fileName,"key2",gKeepTimeErr,"line %d:%s测试失败(key=%d)", Tools.getLineInfo(),TESTITEM,keyCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"key2",gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
