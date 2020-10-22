package com.example.highplattest.ums;

import java.io.IOException;
import java.util.List;

import android.newland.ums.UmsApi;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums12.java 
 * history 		 	: 变更点																变更时间				变更人员
 * 					  新增：enableApplicationNetwork(String address)：允许应用联网		   	    20200813	 		郑薛晴	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums12 extends UnitFragment{
	private final String TESTITEM = "enableApplicationNetwork(银商)";
	public final String FILE_NAME = Ums12.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums12()
	{
		try {
			testUms12();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums12", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
	}

	private void testUms12() throws IOException
	{
		boolean iRet = false;
		String funcName = "testUms12";
		
		gui.cls_show_msg("请先保证设备可正常联网,安装/SVN/Tool/银商安全工具/pingtools.apk,完成后任意键继续");
		// 测试前置：清除所有的之前的黑名单列表，获取到的黑名单列表个数应该0
		List<String> oldBlackList = umsApi.getDisableIpAddressList();
		for (String str:oldBlackList) {
			umsApi.enableApplicationNetwork(str);
		}
		oldBlackList = umsApi.getDisableIpAddressList();
		if(oldBlackList!=null&&oldBlackList.size()>1)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:移除黑名单失败(size=%d)", Tools.getLineInfo(),oldBlackList.size());
			if(!GlobalVariable.isContinue)
				return;
		}
	
		// case3.1:禁止应用访问百度域名格式地址后，允许应用访问百度,预期 任何一种网络方式（wifi、无线、以太网）A应用均可访问百度
		gui.cls_show_msg1(1, "case3.1:禁止应用访问百度域名格式地址后，允许应用访问百度,预期 任何一种网络方式(wifi、无线、以太网)应用均可访问百度");
		if((iRet = umsApi.disableApplicationNetwork("www.baidu.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet = umsApi.enableApplicationNetwork("www.baidu.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Pingwww.baidu.com网址,正常访问[确认],无法访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3.2:禁止应用访问有道翻译的IP地址，允许应用访问有道翻译的IP地址  103.72.47.249
		gui.cls_show_msg1(1, "case3.2:禁止应用访问有道翻译的IPV4地址，允许应用访问有道翻译,预期 任何一种网络方式(wifi、无线、以太网)应用均可访问有道翻译");
		
		if((iRet = umsApi.disableApplicationNetwork("103.72.47.249"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet = umsApi.enableApplicationNetwork("103.72.47.249"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		iRet = NetworkUtil.ping("103.72.47.249");
		if(iRet==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Ping103.72.47.249网址,正常访问[确认],无法访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.3:允许访问未被禁用的域名地址，应用访问该地址
		gui.cls_show_msg1(1, "case3.3:允许应用访问qq,预期 任何一种网络方式(wifi、无线、以太网)应用均可访问qq");
		if((iRet = umsApi.enableApplicationNetwork("www.qq.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Ping www.qq.com网址,正常访问[确认],无法访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.1:参数异常测试：address=NULL，预期返回false
		gui.cls_show_msg1(1, "case2.1:参数异常测试：address=NULL");
		if((iRet = umsApi.enableApplicationNetwork(null))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:参数异常测试：address=""，预期返回false
		gui.cls_show_msg1(1, "case2.2:参数异常测试：address=错误字符串");
		if((iRet = umsApi.enableApplicationNetwork(""))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.3:参数异常测试:address="&()*$"，预期返回false
		gui.cls_show_msg1(1, "case2.3:参数异常测试：address=错误字符串");
		if((iRet = umsApi.enableApplicationNetwork("&()*$"))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.1:允许应用访问之前被禁止的百度地址，重启后应用访问百度地址
		// case4.2:允许应用访问之前被禁止TCP后台地址，重启后应用访问TCP后台地址
		if(gui.cls_show_msg("case4:允许应用访问之前被禁止的百度地址和头条IP,【重启后仍然可正常访问百度网址和头条网址,测试人员手动验证】,该case要进行重启,是否测试,是[确认],否[其他]")==ENTER)
		{
			gui.cls_show_msg1(1, "允许应用访问之前被禁止的百度地址和头条IP,重启后仍然可正常Ping通www.baidu.com和103.72.47.249");
			if((iRet = umsApi.disableApplicationNetwork("www.baidu.com"))==false)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((iRet = umsApi.disableApplicationNetwork("103.72.47.249"))==false)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((iRet = umsApi.enableApplicationNetwork("www.baidu.com"))==false)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((iRet = umsApi.enableApplicationNetwork("103.72.47.249"))==false)
			{
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1, "即将重启");
			Tools.reboot(myactivity);
		}

		
		// case5.1:断网时开启允许应用访问被禁止的百度地址，设置完毕网络开启,预期 不论哪一种网络方式，应用无法访问头条
		if((iRet = umsApi.disableApplicationNetwork("www.baidu.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请将网络断开,操作完毕任意键继续");
		gui.cls_show_msg1(1, "case5.1:断网时开启允许应用访问被禁止的百度地址");
		if((iRet = umsApi.enableApplicationNetwork("www.baidu.com"))!=false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping工具Ping www.baidu.com网址,无法访问[确认],正常访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
		
	}
	
	
	@Override
	public void onTestUp() {
		umsApi = new UmsApi(myactivity);
	}

	@Override
	public void onTestDown() {
		
	}

}
