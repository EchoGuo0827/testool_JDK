package com.example.highplattest.androidport;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;

/************************************************************************
 * 
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort4.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141029
 * directory 		: 
 * description 		: 测试Android系统与外置串口通信模块的getVersion
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20141029	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class AndroidPort4 extends UnitFragment 
{
	private final String CLASS_NAME=AndroidPort4.class.getSimpleName();
	NLUART3Manager uart3Manager=null;
	private String TESTITEM = "外置串口getVersion";
	private Gui gui = new Gui(myactivity, handler);
	
	public void androidport4()
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232)==false&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持物理串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		String message;
		// 实例化接口对象
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// 获取JIN的版本信息，判断获取到的版本号是否正确
		message = "JNI版本信息="+uart3Manager.getVersion();
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过(%s)", TESTITEM,message);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		if(uart3Manager!=null)
			uart3Manager.close();
		gui = null;
		uart3Manager = null;
	}
}
