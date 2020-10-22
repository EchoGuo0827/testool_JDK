package com.example.highplattest.ums;

import java.io.IOException;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.R.integer;
import android.annotation.SuppressLint;
import android.newland.ums.UmsApi;

/************************************************************************
 * 
 * module 			: 银商安全模块
 * file name 		: Ums6.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200813
 * directory 		: 
 * description 		: 测试isLockMachine查询锁机状态
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200813	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums6 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "isLockMachine(银商)";
	public final String FILE_NAME = Ums6.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	boolean mCameraStatus = true;
	
	public void ums6()
	{
		try {
			testUms6();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums1", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
		}
	}
	
	public void testUms6()
	{
		boolean iRet = false;
		String funcName = "testUms6";
		umsApi = new UmsApi(myactivity);
		while(true)
		{
			int nkey = gui.cls_show_msg("%s\n0.设备锁定，获取设备状态\n1.多次获取设备状态\n2.设备未锁定，获取设备状态\n3.设备锁定后重启，重启后获取设备状态\n4.设备未锁定后重启，重启后获取设备状态", TESTITEM);
			switch (nkey) {
			case '0':
				//case3.1：设备锁定，获取设备状态，预期返回true，锁机状态
				gui.cls_show_msg1(2, "case3.1：设备锁定，获取设备状态");
				if((iRet = umsApi.lockMachine("12345678")) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((iRet = umsApi.isLockMachine()) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "获取到的状态为锁定。");
				umsApi.unlockMachine();
				break;
			case '1':
				//case3.2：多次获取设备状态，预期每次返回的设备状态一致
				gui.cls_show_msg1(2, "case3.2：锁机后多次获取设备状态");
				if((iRet = umsApi.lockMachine("12345678")) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((iRet = umsApi.isLockMachine()) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "第1次获取到的状态为锁定。");
				if((iRet = umsApi.isLockMachine()) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "第2次获取到的状态为锁定。");
				//解锁后多次获取状态
				gui.cls_show_msg1(2, "case3.2：解锁后多次获取设备状态");
				if((iRet = umsApi.unlockMachine()) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((iRet = umsApi.isLockMachine()) == true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "第1次获取到的状态为未锁定。");
				if((iRet = umsApi.isLockMachine()) == true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "第2次获取到的状态为未锁定。");
				break;
			case '2':
				//case3.3：设备未锁定，获取设备状态，预期返回false，设备未锁机
				gui.cls_show_msg1(2, "case3.3：设备未锁定获取设备状态");
				if((iRet = umsApi.isLockMachine()) == true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "获取到的状态为未锁定。");
				break;
			case '3':
				//case4.1：设备锁定后重启，重启后获取设备状态，预期返回true，锁机状态
				gui.cls_show_msg1(2, "case4.1：设备锁定后重启，重启后获取设备状态");
				if((iRet = umsApi.lockMachine("12345678")) != true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(2, "即将重启");
				//重启
				Tools.reboot(myactivity);
				break;
			case '4':
				//case4.2：设备未锁定后重启，重启后获取设备状态，预期返回false，设备未锁机
				gui.cls_show_msg1(2, "case4.2：设备未锁定后重启，重启后获取设备状态");
				if(gui.cls_show_msg("重启后再次进入此case测试。是否重启，是【确认】，否【其他】") == ENTER)
				{
					gui.cls_show_msg1(2, "即将重启");
					//重启
					Tools.reboot(myactivity);
				}
				if((iRet = umsApi.isLockMachine()) == true)
				{
					gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg1(3, "获取到的状态为未锁定。");
				break;
			default:
				unitEnd();
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

