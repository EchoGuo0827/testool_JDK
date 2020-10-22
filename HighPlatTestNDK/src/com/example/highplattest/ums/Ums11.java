package com.example.highplattest.ums;

import java.io.IOException;

import android.newland.ums.UmsApi;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums11.java 
 * history 		 	: 变更点											变更时间				变更人员
 * 					   禁止应用联网disableApplicationNetwork		   	    20200812	 		郑薛晴	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums11 extends UnitFragment
{
	private final String TESTITEM = "disableApplicationNetwork(银商)";
	public final String FILE_NAME = Ums11.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums11()
	{
		try {
			testUms11();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums11", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
	}
	
	public void testUms11() throws IOException
	{
		boolean iRet=false;
		String funcName = "testUms11";
		
		gui.cls_show_msg("测试前置:请确保网络可用,安装/SVN/Tool/银商安全工具/pingtools.apk,确保完毕任意键继续");
		
		// case3.1:禁止应用访问百度域名格式地址(www.baidu.com)，设备的系统、第三方应用访问百度地址,预期 任何一种网络方式（wifi、无线、以太网）多个应用均无法访问百度
		gui.cls_show_msg1(1, "case3.1:禁止访问百度域名地址");
		if((iRet = umsApi.disableApplicationNetwork("www.baidu.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Ping www.baidu.com,无法访问[确认],正常访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3.2:禁止所有应用访问TCP后台地址(175.43.124.234),预期 任何一种网络方式（wifi、无线、以太网）多个应用均无法访问TCP后台服务
		gui.cls_show_msg1(1, "case3.2:禁止所有应用访问IP地址 凤凰网(175.43.124.234)");
		if((iRet = umsApi.disableApplicationNetwork("175.43.124.234"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		iRet = NetworkUtil.ping("175.43.124.234");
		if(iRet==true)// 本app使用网络
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Ping 175.43.124.234,无法访问[确认],正常访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
	/*	// case3.3:设置允许访问百度地址，禁止IPV6格式的IP地址（百度2400:da00::dbf:0:100），多个应用访问该地址,预期 所有应用无法访问百度地址
	 * ipv6本身无法ping通，该案例不测
		gui.cls_show_msg1(1, "case3.3:设置允许访问百度地址,禁止IPV6格式的IP地址(2400:da00::dbf:0:100)");
		if((iRet = umsApi.enableApplicationNetwork("www.baidu.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet = umsApi.disableApplicationNetwork("2400:da00::dbf:0:100"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		iRet = NetworkUtil.ping("2400:da00::dbf:0:100");
		if(iRet ==true)// 本app使用网络
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3-1" +
					"测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具 Ping www.baidu.com,无法访问[确认],正常访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
	*/	
		// case3.4:访问未被禁用的域名地址（note.youdao.com），可正常访问该域名地址
		gui.cls_show_msg1(1, "case3.4:访问未被禁用的域名地址(note.youdao.com)");
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用自检、浏览器等访问note.youdao.com网址,正常访问[确认],无法访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.4测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.1:参数异常测试：address=NULL,预期返回false
		gui.cls_show_msg1(1, "case2.1:参数异常测试：address=NULL");
		if((iRet = umsApi.disableApplicationNetwork(null))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:参数异常测试：address="",预期返回false
		gui.cls_show_msg1(1, "case2.2:参数异常测试：address=空");
		if((iRet = umsApi.disableApplicationNetwork(""))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3:参数异常测试:参数异常测试:address="&()*$",预期返回false
		gui.cls_show_msg1(1, "case2.3:参数异常测试：address=错误字符串");
		if((iRet = umsApi.disableApplicationNetwork("&()*$\n"))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.4:参数异常测试:参数异常测试:address="a.b.c.d",预期返回false
		gui.cls_show_msg1(1, "case2.3:参数异常测试：address=a.b.c.d");
		if((iRet = umsApi.disableApplicationNetwork("a.b.c.d"))==true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.4测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.1:禁止应用访问百度域名格式地址，重启后应用访问百度地址,预期所有应用无法访问百度
		// case4.2:禁止应用访问TCP后台地址，重启后应用访问TCP后台地址,预期所有应用无法访问TCP后台服务
		if((iRet = umsApi.disableApplicationNetwork("www.baidu.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet = umsApi.disableApplicationNetwork("175.43.124.234"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("是否进行重启测试,重启后请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Ping www.baidu.com和175.43.124.234均无法访问才可视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
		
	/*	// case5.1:断网时使用域名方式设置禁止访问今日头条，设置完毕打开网络访问今日头条地址,预期不论哪一种网络方式，所有应用无法访问今日头条
	 * 断网时本身无法用域名访问，该案例不测
		gui.cls_show_msg("请测试人员确保设备未联网,确认完毕任意键继续");
		if((iRet = umsApi.disableApplicationNetwork("www.toutiao.com"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping测试工具Ping www.toutiao.com网址,无法访问[确认],正常访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
	*/	
		// case5.2:断网时使用IP方式设置禁止访问百度手机助手，设置完毕打开网络访问百度手机助手，预期不论哪一种网络方式，所有应用无法访问百度手机助手
		gui.cls_show_msg("请测试人员确保设备未联网,确认完毕任意键继续");
		if((iRet = umsApi.disableApplicationNetwork("112.80.225.227"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case5.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请分别使用wifi、无线、以太网通讯方式,使用Ping 测试工具Ping 112.80.225.227网址,无法访问[确认],正常访问[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
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
