package com.example.highplattest.systest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest11.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150305
 * directory 		: 
 * description 		: Reboot重启压力
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150305	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest11 extends DefaultFragment 
{
	private final String TAG = SysTest11.class.getSimpleName();
	private final String TESTITEM = "reboot压力";
	private Gui gui = null;
	
	public void systest11() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("reboot压力0.压力测试");
			switch (nkeyIn) 
			{
			case '0':
				gui.cls_show_msg1(2, "%s测试中...",TESTITEM);
				if(gui.ShowMessageBox("是否已安装RebootApk.apk和RebootMachine.apk".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
				{
					gui.cls_show_msg1(2, "请先安装相应的apk");
					return;
				}
				else
				{
					PackageManager packageManager =myactivity.getPackageManager();
			        Intent intent = packageManager.getLaunchIntentForPackage("com.example.rebootapk");
			        startActivity(intent);
				}
				break;

			case ESC:
				intentSys();
				return;
			}
		}

	}
}
