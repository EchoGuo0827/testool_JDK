package com.example.highplattest.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.ParaEnum.LinkStatus;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.LayerBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: DNS模块
 * file name 		: DNS1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180207
 * directory 		: 
 * description 		: DNS非法测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180207	 	    created
 *						变更记录			时间				变更人
 *					wifiutil改为单例模式	20200727        陈丁
 *					修复传入错误DNS仍能访问网页成功问题  20200813 陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net10 extends UnitFragment 
{
	private Gui gui;
	private WifiPara wifiPara = new WifiPara();
	private Config config;
	private final String TESTITEM ="DNS非法";
	private String fileName="DNS1";
	private String funName="dns_test";
	public void net10()
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		gui.cls_show_msg("测试前请把下拉状态栏打开，状态栏选项需包括飞行模式，已调出任意键进入用例");
		while(true)
		{
			int returnValue=gui.cls_show_msg("DNS非法测试\n0.wlan配置\n1.dns测试\n");
			switch (returnValue) 
			{
			case '0':/*要使用静态方式配置wifi，后续的设置DNS才可生效*/
				gui.cls_show_msg("请使用静态方式配置wifi，任意键继续");
				config.confConnWlan(wifiPara);
				break;
				
			case '1':
				dns_test();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	public void dns_test()
	{
		// 以下为局部变量
		LinkType linkType = LinkType.WLAN;
		String hostName = "www.sina.cn";// 新浪网
//		WifiUtil wifiUtil = new WifiUtil(myactivity);
		WifiUtil wifiUtil=WifiUtil.getInstance(myactivity,handler);
		String ipAddr;
		
		// 测试前置：清除DNS的缓存区，网上说开关飞行模式会清DNS缓存，飞行模式的操作需要系统APP才可以
		gui.cls_show_msg("请先对飞行模式进行开关下,完成任意键继续");
		gui.cls_printf("DNS非法性测试中...".getBytes());
		LayerBase layerBase = new Layer(myactivity, handler);
		//case1:设置错误的DNS，进行域名解析应该失败，预期获取到的Ip为空
		gui.cls_printf("case1:设置错误的DNS测试".getBytes());
		wifiPara.setDns1("100.100.100.100");
		wifiPara.setDns2("200.200.200.200");
		// 能连接上AP，但是不能上网
		if(layerBase.linkUP(wifiPara, linkType)!=SUCC)
		{
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr ,"line %d:wifi连接失败", Tools.getLineInfo());
			return;
		}
		Layer.linkStatus = LinkStatus.linkdown;
		// 访问新浪网页应失败
		ipAddr = parseHostGetIp(hostName);
		if(ipAddr.equals("UnknownHostException")==false)
		{
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr, "line %d:DNS错误,域名解析成功(ip =%s)", Tools.getLineInfo(),ipAddr);
			return;
		}
		
		// case2:设置正确的DNS，进行域名解析应该成功，其中DNS2是正确的  
		gui.cls_printf("case2:DNS1错误，DNS2正确测试".getBytes());
		wifiPara.setDns1("1.1.1.1");
		wifiPara.setDns2("8.8.8.8");
		try {
			wifiUtil.saveStaticWifiConfig(wifiPara, 24);
		} catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr, "line %d:wifi连接失败", Tools.getLineInfo());
			return;
		} 
		// 访问新浪网页应成功，有时会访问失败，3次访问只要有一次访问成功即可认为访问是成功的(responseCode==xxx代表之前缓存过该域名)
		/*responseCode=200:一切正常，对Get和POST请求的应答文档跟在后面
		 * responseCode = 204:没有新文档，浏览器应该继续显示原来的文档。如果用户定期地刷新页面，而servlet可以确定用户文档足够新
		 * **/
		ipAddr = parseHostGetIp(hostName);
		if(ipAddr.equals("UnknownHostException")==true)
		{
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr, "line %d:DNS错误,域名解析成功(ip =%s)", Tools.getLineInfo(),ipAddr);
			return;
		}
		
		// case4:DNS的具有缓存机制，正确访问了某个网址后，再次访问该网址应返回
		if((ipAddr = parseHostGetIp(hostName))==null)
		{
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr,"line %d:DNS错误，域名解析成功(ip =%s)", Tools.getLineInfo(),ipAddr);
			return;
		}
/*		// case3:设置NULL，预期不会对DNS列表进行修改，域名解析应该成功，会设置成功为：：1，：：1
		wifiPara.setDns1(null);
		wifiPara.setDns2(null); 
		if(layerBase.linkUP(wifiPara, linkType)!=SUCC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:wifi连接失败", Tools.getLineInfo());
			return;
		}
		// 访问新浪网页应成功
		if((respCode=get_request(DnsName))!=200)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:DNS为null，域名解析失败(ret = %d)", Tools.getLineInfo(),respCode);
			return;
		}
		if(layerBase.linkDown(wifiPara, linkType)!=SUCC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:wifi断开失败", Tools.getLineInfo());
		}*/
		/*//case4:DNS具有缓存机制，之前正确的域名解析应该能够保存在本地，下一次错误的DNS能直接从本地取对应域名的IP（根据镇江建议，该case在实际使用场景中不符，故屏蔽）
		// 比如新浪的应该能够对应为 www.sina.cn->36.51.254.236
		wifiPara.setDns1("1.1.1.1");
		wifiPara.setDns2("2.2.2.2");
		try {
			wifiUtil.saveStaticWifiConfig(wifiPara, 24);
		} catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:wifi连接失败", Tools.getLineInfo());
			return;
		} 
		if((ipAddr = parseHostGetIp(hostName))==null)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:DNS错误，域名解析成功(ip =%s)", Tools.getLineInfo(),ipAddr);
			return;
		}*/
		// 清空DNS缓存，进行动态设置后再静态设置操作说不定可以清DNS缓存
		// case5:连续设置应该是最后一次生效，设置正确再设置错误的DNS，进行域名解析应该失败
		wifiPara.setDns1("100.100.100.100");
		wifiPara.setDns2("200.200.200.200");
		try {
			wifiUtil.saveStaticWifiConfig(wifiPara, 24);
		} catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr, "line %d:wifi连接失败", Tools.getLineInfo());
			return;
		} 
		gui.cls_show_msg("请先对飞行模式进行开关下,完成任意键继续");
		gui.cls_printf("case3:连续设置DNS,应是最后一次生效".getBytes());
		try {
			wifiUtil.saveStaticWifiConfig(wifiPara, 24);
		} catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr, "line %d:wifi连接失败", Tools.getLineInfo());
			return;
		} 
		// 访问新浪网页 域名解析应失败
		ipAddr = parseHostGetIp(hostName);
		if(ipAddr.equals("UnknownHostException")!=true)
		{
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr,"line %d:DNS错误，域名解析成功(ip =%s)", Tools.getLineInfo(),ipAddr);
			return;
		}
		gui.cls_show_msg1_record(fileName, funName,gScreenTime, "%s测试通过", TESTITEM);	
		// 测试后置断开wifi
		if(layerBase.linkDown(wifiPara, linkType)!=SUCC)
		{
			gui.cls_show_msg1_record(fileName, funName,gKeepTimeErr,"line %d:wifi断开失败", Tools.getLineInfo());
			return;
		}
	}
	
	/**
	 * 解析域名获取IP
	 */
	public String parseHostGetIp(String hostName)
	{
		InetAddress ipInetAddr;
		String parseIp = null;
		try {
			ipInetAddr = InetAddress.getByName(hostName);
			parseIp = ipInetAddr.getHostAddress();
			Log.e("百度地址", parseIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			parseIp = "UnknownHostException";
		}
		return parseIp;

	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() { 
		/*// 测试后置，关闭wifi
		if(layerBase.linkDown(wifiPara, LinkType.WLAN)!=SUCC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:wifi断开失败", Tools.getLineInfo());
		}*/
	}
}
