package com.example.highplattest.systemnode;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

public class SystemNode9 extends UnitFragment{
	private final String TESTITEM ="k21休眠节点获取";
	private String fileName=SystemNode9.class.getSimpleName();

	public void systemnode9()
	{
		Gui gui = new Gui(myactivity, handler);
		String isK21Sleep = getProperty("persist.sys.nl_suspend", "-10086");
		gui.cls_show_msg1(1,"K21是否休眠:%s,关闭休眠该值为off,开启休眠该值为on或具体的休眠时间",isK21Sleep);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
