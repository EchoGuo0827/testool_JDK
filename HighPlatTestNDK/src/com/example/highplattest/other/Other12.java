package com.example.highplattest.other;

import android.content.ComponentName;
import android.content.Intent;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other12.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20181029
 * directory 		: 
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20181029	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other12 extends UnitFragment
{
	public final String TESTITEM = "IC授权跳转";
	public final String TAG = Other12.class.getSimpleName();
	private final String OPEN_ADB="open_adb";
	private final String CLOSE_ADB="close_adb";
	private final String CLEAN_SEC="clean_sec";
	private final String CLEAN_PUBKEY = "clean_pubkey";
	private final String LOWER_VERSION="lower_version";
	private final String UNLOCKED = "unlocked";
	private Gui gui = new Gui(myactivity, handler);
	
	public void other12()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("IC授权测试\n0.打开ADB\n1.关闭ADB\n2.清安全\n3.清证书\n4.降版本\n5.解锁设备\n");
			switch (nkeyIn) {
			case '0':
				intentIcAuth(OPEN_ADB);
				break;
				
			case '1':
				intentIcAuth(CLOSE_ADB);
				break;
				
			case '2':
				intentIcAuth(CLEAN_SEC);
				break;
				
			case '3':
				intentIcAuth(CLEAN_PUBKEY);
				break;
				
			case '4':
				intentIcAuth(LOWER_VERSION);
				break;

				
			case '5':
				intentIcAuth(UNLOCKED);
				break;
				
			case ESC:
				unitEnd();
				return;
				
			default:
				break;
			}
		}

	}
	
	private void intentIcAuth(String name)
	{
		Intent intentAdbClose = new Intent(Intent.ACTION_MAIN);
		intentAdbClose.addCategory(Intent.CATEGORY_DEFAULT);
		intentAdbClose.putExtra("flag", name);
		ComponentName cn = new ComponentName("com.newland.icauth","com.newland.iccardauth.IcCardActivity");
		intentAdbClose.setComponent(cn);
		myactivity.startActivity(intentAdbClose);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
