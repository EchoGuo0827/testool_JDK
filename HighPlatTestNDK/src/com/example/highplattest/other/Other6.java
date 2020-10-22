package com.example.highplattest.other;

import android.os.SystemClock;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

public class Other6 extends UnitFragment
{
	private final String TESTITEM = "系统开机时间";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Other6.class.getSimpleName();
	
	public void other6()
	{
		long realTime = SystemClock.elapsedRealtime()/1000;//从系统开机到现在经理过的时间
		int hour = (int) (realTime/3600);
		int min = (int) (realTime/60%60);
		int sec = (int) (realTime%60);
		String hms = hour+":"+min+":"+sec;
//		String hms = new SimpleDateFormat("HH时mm分ss").format(realTime);
		gui.cls_show_msg1(1, "从系统开机到现在经过的时间="+hms);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
