package com.example.highplattest.systemconfig;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.SettingsManager;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig39.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170504
 * directory 		: 
 * description 		: 测试触控虚拟按键的LED模式setLedMode和getLedMode
 * related document : 
 * history 		 	: author			date			remarks
 *			  		   wangxy		   20170504 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig39 extends UnitFragment
{
	SettingsManager settingsManager=null;
	int ret = -1;
	boolean ret1 = false;
	private final String TESTITEM = "setLedMode和getLedMode";
	private String fileName="SystemConfig39";
	private Gui gui = null;
	
	public void systemconfig39() 
	{
		/*switch (GlobalVariable.currentPlatform) 
		{
		case IM81:
			if(GlobalVariable.AUTOHANDFLAG == 1)
			{
				gui.cls_show_msg1(gScreenTime,SERIAL, "%s用例不支持自动化测试，请手动验证",TESTITEM);
				return;
			}
			
			private & local definition
			支持的驱动为FT5X06和GT9XX
			原有模式为0-3，后开发说模式3省电模式删除，故不验证模式3省电模式
			int[] values = {0,1,2};
			String[] tips = {"触摸home键，观察触摸时home是否亮","观察home键是否常亮","观察home键是否常暗","等待屏保出现，观察屏保出现时，home键灯是否会灭"};	
			String[] tips = {"触摸home键，观察触摸时home是否亮","观察home键是否常亮","观察home键是否常暗"};
			try 
			{
				settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			} 
			catch (NoClassDefFoundError e) 
			{
				gui.cls_show_msg(2, "line %d:未找到该类，抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
				return;
			}
			
			process body
			gui.cls_show_msg(gScreenTime, "%s测试中...",TESTITEM);
			// 测试前置，确保软件和硬件上支持触摸虚拟按键led模式
			gui.cls_show_msg("确保软件和硬件上支持触摸虚拟按键led灯模式，任意键继续".getBytes());
			// case1:非法参数，（-1,4），获取到的值应该是之前设置的模式
			try
			{
				settingsManager.setLedMode(-1);
			}catch(NoSuchMethodError e)
			{
				gui.cls_show_msg(2, "line %d:未找到该方法，抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
				return;
			}
			if((ret = settingsManager.getLedMode()) == -1)
			{
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
				{
					settingsManager.setLedMode(0);
					return;
				}
			}
			settingsManager.setLedMode(4);
			if((ret = settingsManager.getLedMode()) == 4)
			{
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
				{
					settingsManager.setLedMode(0);
					return;
				}
			}
			// case2:设置每种正常模式（0,1,2），应该会有不同的效果，原有模式为0-3，后开发说模式3省电模式删除，故不验证模式3省电模式
			for (int i = 0; i < values.length; i++) 
			{
				settingsManager.setLedMode(values[i]);
				if(i == 3)
				{
					message = "设置->显示->互动屏保，设置完成点击是";
					handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, message));
					GlobalVariable.PORT_FLAG = true;
					while(GlobalVariable.PORT_FLAG);
				}
				if((ret = settingsManager.getLedMode()) != values[i])
				{
					gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
					{
						settingsManager.setLedMode(0);
						return;
					}
				}
				// 点击home，看设置的效果
				if(gui.ShowMessageBox(tips[i].getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
				{
					gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
					if(!GlobalVariable.isContinue)
					{
						settingsManager.setLedMode(0);
						return;
					}
				}
			}
			
			// 测试后置
			settingsManager.setLedMode(0);
			if((ret = settingsManager.getLedMode()) != 0)
			{
				gui.cls_show_msg1(2, SERIAL,"line %d:%s测试失败%d", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue);
				{
					settingsManager.setLedMode(0);
					return;
				}
			}
//			show_flag(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, "设置->显示->互动屏保，取消互动屏保，设置完成点击是");
			gui.cls_show_msg1(gScreenTime, SERIAL,"%s测试通过", TESTITEM);
			break;
		case N900_3G:
		case N900_4G:
		case N910:
		case N700:
			gui.cls_show_msg1(2, SERIAL,"%s不支持%s用例", GlobalVariable.currentPlatform,TESTITEM);
			break;
		default:
			break;
		}*/
		
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
		
	}
}
