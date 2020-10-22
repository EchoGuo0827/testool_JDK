package com.example.highplattest.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other29.java 
 * history 		 	: 变更点			  														变更时间			变更人员
 * 				                通过APK向系统发送pin码的方式自动解除SIM密码，不再需要手动输入SIM密码解锁(N850海外V2.3.03)        20201015		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other29 extends UnitFragment {
	
	public final String TAG = Other29.class.getSimpleName();
	private final String TESTITEM = "Pin码自动解除测试";
	private Gui gui = new Gui(myactivity, handler);
	private SharedPreferences sp;
	private SharedPreferences.Editor mEditor;
	
	public void other29(){
		sp = myactivity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		mEditor = sp.edit();
		gui.cls_show_msg("请确保设置了Sim卡的pin码为1234后按任意键即将重启终端");
		mEditor.putBoolean("other29", true);
		mEditor.commit();
		Tools.reboot(myactivity);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
