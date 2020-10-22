package com.example.highplattest.key;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android设定APP获取KEY
 * file name 		: Key1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141226 
 * directory 		: 
 * description 		: 获取HOME_KEY
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20141226	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Key1 extends UnitFragment
{
	private final String TESTITEM = "HOME_KEY";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName="Key1";
	
	@Override
	//by2016
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		myactivity.getWindow().addFlags(3);
//		myactivity.getWindow().addFlags(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);//int TYPE_KEYGUARD_DIALOG = 2009
//		myactivity.getWindow().addFlags(2004);//int TYPE_KEYGUARD = 2004;
		return view;
	}
	
	public void key1() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"key1",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		/* private & local definition */
		int keyCode = -1;
		
		/*process body*/
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		/**X5设备屏蔽了home键之后在锁屏时候会导致无法解锁 add by 20190605*/
		if(gui.cls_show_msg("让设备处于锁屏界面，设备是否能正常解锁")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"key1",gKeepTimeErr,"line %d:屏蔽home后,在锁屏界面解锁失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请点击home键,点击完毕后[确认]继续");
		keyCode = GlobalVariable.virtualKey;
		if(keyCode!=KeyEvent.KEYCODE_HOME)
		{
			gui.cls_show_msg1_record(fileName,"key1",gKeepTimeErr,"line %d:%s测试失败(key = %d)", Tools.getLineInfo(),TESTITEM,keyCode);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"key1",gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
