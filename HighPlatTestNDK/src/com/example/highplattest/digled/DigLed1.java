package com.example.highplattest.digled;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.digled.Digled;
/************************************************************************
 * 
 * module 			: N550的数码管显示模块
 * file name 		: DigLed1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180815
 * directory 		: 
 * description 		: 获取驱动信息
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180815         create
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class DigLed1 extends UnitFragment
{
	private final String CLASS_NAME = "DigLed1";
	private final String TESTITEM = "digLedVer(N550)";
	
	/**
	 * 错误值定义:-2参数错误
	 */
	public void digled1()
	{
		/*private & local definition*/
		Digled digled = new Digled();
		byte[] buf = new byte[128];
		Gui gui = new Gui(myactivity, handler);
		int ret = -1;
		
		// case1:参数异常测试，传入null
		if((ret = digled.digLedVer(null))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:参数异常测试，传入的字节数组的长度小于获取的驱动信息
		byte[] errByte= new byte[1];
		if((ret = digled.digLedVer(errByte))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3:获取驱动信息
		if((ret = digled.digLedVer(buf))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("驱动信息:%s,与开发确认是否一致,是[确认],否[取消]", ISOUtils.ASCII2String(buf))==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ISOUtils.ASCII2String(buf));
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "digled1", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
