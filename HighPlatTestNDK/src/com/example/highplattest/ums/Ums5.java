package com.example.highplattest.ums;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.newland.SettingsManager;
import android.newland.ums.UmsApi;
import android.os.SystemClock;

/************************************************************************
 * 
 * module 			: 银商安全模块
 * file name 		: Ums5.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200813
 * directory 		: 
 * description 		: 测试unlockMachine允许操作机器
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200813	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums5 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "unlockMachine(银商)";
	public final String FILE_NAME = Ums5.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	private SettingsManager settingsManager;
	
	boolean mCameraStatus = true;
	
	public void ums5()
	{
		try {
			testUms5();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums1", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
		}
	}
	
	public void testUms5()
	{
		boolean iRet = false;
		String funcName = "testUms5";
		umsApi = new UmsApi(myactivity);
		
		while(true)
		{
			int nkey = gui.cls_show_msg("%s\n0.正常测试\n1.重启测试\n2.休眠测试", TESTITEM);
			switch (nkey) {
			case '0':
				//case3.1：正常测试:设备未锁定，解锁设备，预期返回false，设备未锁定
				gui.cls_show_msg1(2, "case3.1：设备未锁定，解锁设备");
				if((iRet = umsApi.unlockMachine()) == true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(1, "case3.1测试完毕");
				
				//case3.2：正常测试:设备锁定后解锁设备，预期返回true，可正常操作设备
				gui.cls_show_msg1(2, "case3.2：设备锁定后解锁设备");
				if((iRet = umsApi.lockMachine("12345678")) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "设备已锁定，3s后解锁");
				if((iRet = umsApi.unlockMachine()) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}				
				break;
			case '1':
				//case4.1：设备锁定，重启后解锁设备，预期返回true，可正常操作设备
				gui.cls_show_msg1(2, "case4.1：设备锁定，重启后手动执行命令解锁设备");
				if((iRet = umsApi.lockMachine("12345678")) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "3s后重启，重启后手动执行命令解锁设备");
				//重启
				Tools.reboot(myactivity);
				break;
			case '2':
				settingsManager = (SettingsManager)myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
				long startTime = System.currentTimeMillis();
				GlobalVariable.isWakeUp = false;
				//case4.2：设备锁定，设备休眠放置5min后解锁设备，预期返回true，可正常操作设备
				gui.cls_show_msg1(2, "case4.2：设备锁定，设备休眠放置5min后设备自动解锁");
				if((iRet = umsApi.lockMachine("12345678")) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				settingsManager.setScreenTimeout(1000*1);
				gui.cls_show_msg1(3, "等待机器进入休眠，放置5min或手动唤醒");
				while(Tools.getStopTime(startTime) < 5*60)
				{
					if (GlobalVariable.isWakeUp != false)
						break;
					SystemClock.sleep(100);
				}
				if((iRet = umsApi.unlockMachine()) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				settingsManager.setScreenTimeout(-1);
				break;

			default:
				break;
			}
			gui.cls_show_msg1_record(FILE_NAME, funcName, gScreenTime,"%s测试通过", TESTITEM);
		}
	}
	
	@Override
	public void onTestUp() 
	{
		
	}
	@Override
	public void onTestDown() {
		gui = null;
		umsApi = null;
	}
}

