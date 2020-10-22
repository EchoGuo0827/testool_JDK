package com.example.highplattest.digled;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.digled.Digled;
/************************************************************************
 * 
 * module 			: N550的数码管显示模块
 * file name 		: DigLed2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180815
 * directory 		: 
 * description 		: 显示应用传下来的字符或数字
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180815         create
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class DigLed2 extends UnitFragment
{
	private final String CLASS_NAME = "DigLed2";
	private final String TESTITEM = "showDigLed(N550)";
	
	/***
	 * 错误值定义showDigled：-1:打开设备失败
	 * 			-2:输入字符超过
	 * 			-3:设备驱动读写失败
	 * 			-4:输入字符串异常
	 */
	public void digled2()
	{
		/*private & local definition*/
		Digled digled = new Digled();
		Gui gui = new Gui(myactivity, handler);
		int ret = -1;
		
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		// case1:传入非法的字符串，应返回参数错误,如：null，"",
		if((ret = digled.showDigLed(null))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = digled.showDigLed(""))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 非法字符
		if((ret = digled.showDigLed("+-,.lH"))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 不符合金额格式，输入字符串异常
		if((ret = digled.showDigLed("1.22.45"))!=-4)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:长度异常测试：传入合法的数字字符过长
		if((ret = digled.showDigLed("1234567890"))!=-2)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3.1:传入合法的数字字符
		if((ret = digled.showDigLed("1.23"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为1.23,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3.2:连续显示合法的字符串，最大的数字位数为6位置
		if((ret = digled.showDigLed("9.87654"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为9.87654,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4:熄灭数码管之后再次显示应正常
		if((ret=digled.clrDigLed())!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = digled.showDigLed("123456"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为123456,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5:调节数码管的亮度并开启扫码后进行显示数码管应正常
		if((ret = digled.brightDigLed(3))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = digled.showDigLed("565656"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为565656,循环显示888888是无问题的,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case6:客诉，第一位显示小数点死机的问题 add by 20190513
		if((ret = digled.showDigLed(".12345"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled1", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("数码管是否显示为.12345,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "digled2", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "digled1", gScreenTime ,"%s测试通过", TESTITEM);
		// 测试后置，熄灭数码管
		digled.clrDigLed();
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
