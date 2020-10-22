package com.example.highplattest.other;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class Other19 extends UnitFragment 
{
	private Gui gui = new Gui(myactivity, handler);
	public final String FILE_NAME = Other19.class.getSimpleName();
	private final String TESTITEM = "开机获取TUSN测试";/**N550 N920*/
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;
	public void other19(){
		mSharedPreferences = myactivity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("other19", true);
		mEditor.commit();
		gui.cls_show_msg1(gKeepTimeErr, "%s测试中...", TESTITEM);
		// case1:设备开机后才能接收到系统广播
		if(gui.ShowMessageBox("开机获取TUSN测试，重启后查看弹出的通知,若获取到则测试通过,否则测试不通过,是否立即重启，".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			Tools.reboot(myactivity);
		}
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
		// 测试后置，清空SharedPreferences中的数据
		Tools.savaData(myactivity, "ACTION_COMMING_NOISY", "");
	}

}
