package com.example.highplattest.androidport;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;
/************************************************************************
 * 
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort5.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141119
 * directory 		: 
 * description 		: 测试Android系统与外置串口通信模块的isValid
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20141119	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class AndroidPort5 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME=AndroidPort5.class.getSimpleName();
	private NLUART3Manager uart3Manager = null;
	String TESTITEM = "外置串口isValid";
	private Gui gui = new Gui(myactivity, handler);
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	
	public void androidport5()
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232)==false&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持物理串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		// X5设备有新的RS232和旧的RS232,需要让测试人员进行一次选择
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad))
		{
			int nkeyIn = gui.cls_show_msg("是否要测试新的PinPad串口,是[确认],否[取消]");
			isNewRs232 = nkeyIn==ENTER?true:false;
		}
		/*private & local definition*/
		boolean ret;
		int ret1,fd=-1;
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// 关闭串口
		uart3Manager.close();
		
		// case1:串口没有打开的时候，设备不可用
		if(gui.cls_show_msg1(gScreenTime, "串口没有打开的时候,设备不可用,[取消]退出测试")==ESC)
			return;
		if ((ret = uart3Manager.isValid()) == true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		
		// case2:打开串口，设备可用
		if(gui.cls_show_msg1(gKeepTimeErr, "打开串口,设备可用,[取消]退出测试")==ESC)
			return;
		
		fd = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if(fd  == -1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,fd);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret=uart3Manager.isValid()) == false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 测试后置
		if((ret1 = uart3Manager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(uart3Manager!=null)
			uart3Manager.close();
		uart3Manager = null;
		gui = null;
	}
}
