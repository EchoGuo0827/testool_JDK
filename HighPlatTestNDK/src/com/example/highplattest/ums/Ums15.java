package com.example.highplattest.ums;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.newland.ums.UmsApi;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums10.java 
 * history 		 	: 变更点											变更时间				变更人员
 * 					  获取当前系统打开的端口的列表，不区分端口的状态：getOpenedPorts		   	    20200814	 		郑薛晴	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums15 extends UnitFragment{
	private final String TESTITEM = "getOpenedPorts(银商)";
	public final String FILE_NAME = Ums11.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums15()
	{
		try {
			testUms15();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testUms15() throws IOException
	{
		final String funcName = "testUms15";
		int oldSize=0,newSize=0;
		
		gui.cls_show_msg("请确保设备联网后按任意键继续");
		// case1:获取当前系统打开的端口的列表，不区分端口的状态
		gui.cls_show_msg1(1, "case1:获取当前系统打开的端口的列表，不区分端口的状态");
		List<Integer> portList = umsApi.getOpenedPorts();
		if(portList!=null)
			oldSize = portList.size();
		if(portList==null||portList.size()==0)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case1测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.d("openPorts="+portList.size());
		
		// case2:增加一个Socket通讯服务器端口，获取系统打开的端口列表会增加,服务端的端口(使用 netstat -apntu命令可查看)
		gui.cls_show_msg1(1, "case2:增加一个Socket通讯服务器端口，获取系统打开的端口列表会增加");
		SocketUtil socketUtil = new SocketUtil("218.66.48.230", 3459);
		socketUtil.setSocket(Sock_t.SOCK_TCP, gui);
		portList = umsApi.getOpenedPorts();
		SystemClock.sleep(3000);
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令可查看到3459的端口号,状态是ESTABLISHED,是[确认],否[其他]")!=ENTER)
		{
			socketUtil.close(Sock_t.SOCK_TCP);
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1:增加一个Socket通讯的本地端口,获取系统打开的㐰列表会增加12345的端口
		gui.cls_show_msg1(1, "case3.1:增加一个Socket通讯的本地端口,获取系统打开的㐰列表会增加12345端口");
		final ServerSocket serverSocket = new ServerSocket(12345, 2);
		Thread thread1 = new Thread()
		{
			public void run() 
			{
				try {
					LoggerUtil.d("开始监听");
					serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
//					gui.cls_show_msg1_record(FILE_NAME, funcName, 5, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
				}
			};
		};
		thread1.start();
		SystemClock.sleep(500);
		portList = umsApi.getOpenedPorts();
		SystemClock.sleep(3000);
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令可查看到12345的端口号,状态为LISTEN,是[确认],否[其他]")!=ENTER)
		{
			serverSocket.close();
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.2:关闭socket通讯的本地端口,该端口状态会变为close
		gui.cls_show_msg1(1, "case3.2:关闭socket通讯的本地端口,该端口状态会变为close");
		serverSocket.close();
		SystemClock.sleep(3000);
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令无法查看到12345的端口号,是[确认],否[其他]")!=ENTER)
		{
			serverSocket.close();
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4:在断网的时候建立Socket失败,可以获取到新建的端口号,需要通过网络连接的端口会消失
		gui.cls_show_msg("请确保设备未连接网络后任意键继续");
		gui.cls_show_msg1(1, "在断网的时候建立本地Socket成功,可以获取到新建的端口号");
		final ServerSocket serverSocketClose = new ServerSocket(11111, 2);
		Thread thread2 = new Thread()
		{
			public void run() 
			{
				try {
					LoggerUtil.d("开始监听2222");
					serverSocketClose.accept();
				} catch (IOException e) {
					e.printStackTrace();
//					gui.cls_show_msg1_record(FILE_NAME, funcName, 5, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
				}
			};
		};
		thread2.start();
		SystemClock.sleep(500);
		portList = umsApi.getOpenedPorts();
		SystemClock.sleep(3000);
		if(gui.cls_show_msg("使用adb shell netstat -apntu命令可查看到11111的端口号,状态为LISTEN，无法查看到3459的端口号,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
			{
				socketUtil.close(Sock_t.SOCK_TCP);
				serverSocketClose.close();
				return;
			}
		}
		
		
		// 测试后置
		socketUtil.close(Sock_t.SOCK_TCP);
		serverSocketClose.close();
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
