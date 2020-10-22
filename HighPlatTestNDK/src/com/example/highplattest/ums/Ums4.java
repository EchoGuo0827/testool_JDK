package com.example.highplattest.ums;

import java.io.IOException;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.R.string;
import android.annotation.SuppressLint;
import android.newland.ums.UmsApi;

/************************************************************************
 * 
 * module 			: 银商安全模块
 * file name 		: Ums4.java 
 * Author 			: wangkai
 * version 			: 
 * DATE 			: 20200813
 * directory 		: 
 * description 		: 测试lockMachine禁止操作机器，禁止终端屏幕操作
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangkai		  20200813	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums4 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "lockMachine(银商)";
	public final String FILE_NAME = Ums4.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private UmsApi umsApi;
	
	boolean mCameraStatus = true;
	
	public void ums4()
	{
		try {
			testUms4();
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(FILE_NAME, "ums4", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
		}
	}
	
	public void testUms4()
	{
		boolean iRet = false;
		String funcName = "testUms4";
		umsApi = new UmsApi(myactivity);
		
		//case3.1：正常测试:设置密码为重复的数字，预期返回true，机器被锁定，输入正确的密码可打开
		gui.cls_show_msg1(2, "case3.1：设置密码为重复的数字");
		if((iRet = umsApi.lockMachine("00000000")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入00000000解锁，按任意键继续");
		
		//case3.2：正常测试:修改密码，预期返回true
		gui.cls_show_msg1(2, "case3.2：修改密码为：12345678");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入12345678解锁，按任意键继续");
		
		//case2.1：参数异常测试:unlock=NULL，预期返回false，实际抛异常，目前没有对null做处理
		/*gui.cls_show_msg1(2, "case2.1：参数异常测试:unlock=NULL");
		if((iRet = umsApi.lockMachine(null)) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		
		//case2.2：参数异常测试:unlock=""，预期返回false
		gui.cls_show_msg1(2, "case2.2：参数异常测试:unlock=空");
		if((iRet = umsApi.lockMachine("")) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.3：参数异常测试:unlock="abcdefgh&*()"，预期返回false
		gui.cls_show_msg1(2, "case2.3：参数异常测试:unlock=abcdefgh&*()");
		if((iRet = umsApi.lockMachine("abcdefgh&*()")) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.3测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.4：参数异常测试:unlock="1234abcd"，预期返回false
		gui.cls_show_msg1(2, "case2.4：参数异常测试:unlock=1234abcd");
		if((iRet = umsApi.lockMachine("1234abcd")) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.4测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.5：参数异常测试:unlock="1"，非8位数字，预期返回false
		gui.cls_show_msg1(2, "case2.5：参数异常测试:unlock=1");
		if((iRet = umsApi.lockMachine("1")) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.5测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.6：参数异常测试:unlock="1234567"，非8位数字，预期返回false
		gui.cls_show_msg1(2, "case2.6：参数异常测试:unlock=1234567");
		if((iRet = umsApi.lockMachine("1234567")) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.6测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.7：参数异常测试:unlock="123456789"，非8位数字，预期返回false
		gui.cls_show_msg1(2, "case2.7：参数异常测试:unlock=123456789");
		if((iRet = umsApi.lockMachine("123456789")) == true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case2.7测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.1：输入错误的密码10次，预期不能再输密码，需通过接口解锁
		gui.cls_show_msg1(2, "case4.1：输入错误密码10次");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入10次错误密码，预期不能再输入密码，需要通过接口解锁，解锁后进入该应用，按任意键继续");
		
		//case4.2：设备锁定，输入错误的密码9次后输入正确的密码，预期解锁成功
		gui.cls_show_msg1(2, "case4.2：输入错误的密码9次，再输入正确的密码12345678");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入错误的密码9次，再输入正确的密码，解锁后按任意键继续");
		
		//case4.3：设备锁定，输入错误的密码5次，重启后再输入5次错误的密码后输入正确的密码，预期解锁失败，需通过接口解锁
		gui.cls_show_msg1(2, "case4.3：输入5次错误密码，重启，再输入5次错误密码");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.3测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入5次错误密码，重启后再输入5次错误的密码，预期不能再输入密码，需要通过接口解锁，解锁后进入该应用，按任意键继续");
		
		//case4.4：设备锁定，输入错误的密码3次，异常断电后开机，锁机弹框密码次数剩余7，输入正确的密码解锁，预期解锁成功
		gui.cls_show_msg1(2, "case4.4：输入3次错误密码，异常断电后开机，锁机弹框密码次数剩余7，再输入正确的密码12345678");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case4.4测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入3次错误密码，异常断电后开机，锁机弹框密码次数剩余7，输入正确密码解锁，按任意键继续");
		
		//case5.1：设备锁定，重启后设备仍是锁定状态，输入正确密码，预期解锁成功
		gui.cls_show_msg1(2, "case5.1：设备锁定，手动重启设备，仍是锁定状态，输入正确密码解锁");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("手动重启，重启后，设备仍是锁定状态，输入正确密码解锁");
		
		//case5.2：设备休眠放置5min后仍是锁定状态，输入正确密码，预期解锁成功
		gui.cls_show_msg1(2, "case5.2：设备休眠放置5min后仍是锁定状态，输入正确密码解锁");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case5.2测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("休眠唤醒后设备仍是锁定状态，输入正确密码解锁，按任意键继续");
		
		//case5.3：设备锁定，输入错误的密码3次，解锁之后再次锁定设备，查看锁机弹框的可输入密码次数，预期为10次
		gui.cls_show_msg1(2, "case5.3：设备锁定，输入错误的密码3次，解锁之后再次锁定设备，查看锁机弹框的可输入密码次数");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case5.3测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("输入错误的密码3次后输入12345678解锁，按任意键继续");
		if((iRet = umsApi.lockMachine("12345678")) != true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case5.3测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("再次锁定设备，锁机弹框的可输入密码次数为10。是【确认】，否【其他】") != ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "line %d:case5.3测试失败(ret=%s)", Tools.getLineInfo(), iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gScreenTime,"%s测试通过", TESTITEM);
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

