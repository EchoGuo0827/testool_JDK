package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.newland.AnalogSerialManager;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest16.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150618
 * directory 		: 
 * description 		: USB串口综合测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20150618		create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest16 extends DefaultFragment 
{
	private final String TAG = SysTest16.class.getSimpleName();
	private final int MAXWAITTIME = 30;
	private final String TESTITEM = "USB串口性能、压力";
	private final int DEFAULT_COUNT = 100;
	// USB串口对象s
	private AnalogSerialManager analogSerialManager;
	private Gui gui = new Gui(myactivity, handler);
	
	//usb综合测试主程序
	public void systest16() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		// USB串口对象实例化
		analogSerialManager = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("USB综合测试\n0.usb从模式测试\n1.客户端操作");
			switch (returnValue) 
			{	
			case '0':
				fromModel();
				break;
				
			case '1' :
				clientOperation();
				break;
				
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	// add by 20150618
	// USB串口开关压力
	public void usbSwitchPre() 
	{
		/* private & local definition */
		int ret = 0,i = 0,cnt = 0,succ = 0;
		PacketBean packet = new PacketBean();
		
		/* Process body */
		// 设置压力次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		cnt = packet.getLifecycle();
//		String message = "请确保POS和PC已通过USB线连接,任意键继续";
		gui.cls_show_msg("请确保POS和PC已通过USB线连接,任意键继续");
		
		// 测试前置,关闭串口
		analogSerialManager.close();
		
		while(true)
		{
			if(gui.cls_show_msg1(3, "USB串口开关压力测试中\n总共:%d次,已执行:%d次,已成功:%d次,[取消]键退出测试...", cnt,i,succ)==ESC)
				break;
			// 循环次数的控制
			if(i++>=cnt)
				break;
			// 打开串口返回-1表示失败
			if((ret = analogSerialManager.open()) == -1)
			{
				if(gui.cls_show_msg1_record(TAG, "usbSwitchPre", g_keeptime, "line %d:第%d次打开USB串口失败(%d)", Tools.getLineInfo(),i,ret) == NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				if(gui.cls_show_msg1_record(TAG, "usbSwitchPre", g_keeptime, "line %d:第%d次配置USB串口参数失败(%d)", Tools.getLineInfo(),i,ret) == NDK_ERR_QUIT)
				{
					break;
				}
				else
					continue;
			}
			// 关闭串口
			if((ret = analogSerialManager.close())!=NDK_OK)
			{
				if(gui.cls_show_msg1_record(TAG, "usbSwitchPre", g_keeptime, "line %d:第%d次关闭USB串口失败(%d)", Tools.getLineInfo(),i,ret) == NDK_ERR_QUIT)
				{
					break;
				}
				else
					continue;
			}
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "usbSwitchPre",g_time_0, "总共进行%d次开关USB压力测试,已成功%d次", i,succ);
		// 关闭串口
		analogSerialManager.close();
	}
	// end by 20150618
	
	// 异常测试缺少接口
	// add by 20150618
	//USB异常测试
	public void usbAbnormal() 
	{
		int ret = 0,rlen = 0,slen =0;
		byte[] buf = new byte[BUFSIZE_SERIAL+1];
		byte[] rbuf = new byte[BUFSIZE_SERIAL+1];
		long oldTime;
		
		/*process body*/
		gui.cls_show_msg1(2, "正在进行"+TESTITEM+"异常测试");
		// 测试前置,关闭串口
		analogSerialManager.close();
		for (int j = 0; j < buf.length; j++) 
			buf[j] = (byte) (Math.random()*256);
		
		// case1:打开串口,拔插USB线不影响后面的通讯,不应出现异常
		gui.cls_show_msg("请将POS和PC通过USB线连接,并开启PC端的AccessPort工具,任意键继续");
		
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败ret = %d", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		gui.cls_show_msg("请先插拔一下USB线,再打开PC端的AccessPort工具,任意键继续");
		
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		// 清空缓存区？？没有相应的函数
		if((slen = analogSerialManager.write(buf, BUFSIZE_SERIAL, MAXWAITTIME))!=BUFSIZE_SERIAL)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:发送数据失败(ret=%d)", Tools.getLineInfo(),slen);
			analogSerialManager.close();
			return;
		}
		SystemClock.sleep(500);
		gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并发送,任意键继续");
		Arrays.fill(rbuf, (byte) 0);
		if((rlen = analogSerialManager.read(rbuf, BUFSIZE_SERIAL, MAXWAITTIME))!= BUFSIZE_SERIAL)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:接收数据失败(ret = %d)", Tools.getLineInfo(),rlen);
			analogSerialManager.close();
			return;
		}
		if(!Tools.memcmp(buf, rbuf, BUFSIZE_SERIAL))
		{
			gui.cls_show_msg1(g_keeptime, "line %d:数据校验失败", Tools.getLineInfo());
			analogSerialManager.close();
			return;
		}
		analogSerialManager.close();
		gui.cls_show_msg1(2, "1号子用例测试通过");
		
		// case2:测试连续发送过程中插拔USB线,不应出现异常
		gui.cls_show_msg("取消自动发送并清空发送接收缓冲区完成点任意键继续");
		
		if((ret = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:测试失败(ret = %d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败ret = %d", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		gui.cls_show_msg("PC打开串口,POS输出数据3S后拔出USB线,点任意键继续");
		oldTime = System.currentTimeMillis();
		
		while(true)
		{
			if(Tools.getStopTime(oldTime)>MAXWAITTIME)
				break;
			// 根据返回值判断状态,不确定插拔的返回值为多少
			ret = analogSerialManager.write(buf, BUFSIZE_SERIAL, MAXWAITTIME);
			switch (ret) 
			{
			//开发反馈如果在write之前拔掉usb线的情况，就会返回-2（会先去判断usb句柄是否存在）20200417陈丁
			case -1:
			case -2:
				analogSerialManager.close();
				gui.cls_show_msg("关闭PC端串口后,连接USB线,任意键继续");
				if((ret = analogSerialManager.open()) == -1)
				{
					gui.cls_show_msg1(g_keeptime, "line %d:测试失败(ret = %d", Tools.getLineInfo(),ret);
					analogSerialManager.close();
					return;
				}
				
				if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
				{
					gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
					analogSerialManager.close();
					return;
				}
				gui.cls_show_msg("打开PC端串口,点任意键继续");
				break;
				
			case BUFSIZE_SERIAL:
				gui.cls_show_msg1(2, "发送成功");
				break;

			default:
				gui.cls_show_msg1(2,"line %d:未知的返回值（ret=%d）", Tools.getLineInfo(), ret);
				analogSerialManager.close();
				return;
			}
		}
		analogSerialManager.close();
		gui.cls_show_msg1(2, "2号子用例测试通过");
		
		// case3:测试连续接收过程中拔插USB线
		gui.cls_show_msg("请将POS和PC通过USB线连接,并重启PC端的AccessPort工具,点任意键继续");
		if((ret = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:测试失败(ret = %d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		gui.cls_show_msg("PC打开串口,点击是将输入出%dB数据到PC,点任意键继续", BUFSIZE_SERIAL);
		gui.cls_show_msg1(2, "POS-->PC(%dB)\n", buf.length-1);
		if((slen = analogSerialManager.write(buf, BUFSIZE_SERIAL, MAXWAITTIME))!=BUFSIZE_SERIAL)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:发送数据失败(ret=%d)", Tools.getLineInfo(),slen);
			analogSerialManager.close();
			return;
		}
		SystemClock.sleep(500);
		gui.cls_show_msg("请复制数据开始自动发送后拔USB,点任意键继续");
		oldTime = System.currentTimeMillis();
		while(true)
		{
			if(Tools.getStopTime(oldTime)>MAXWAITTIME)
				break;
			Arrays.fill(rbuf, (byte) 0);
			ret = analogSerialManager.read(rbuf, BUFSIZE_SERIAL, MAXWAITTIME);
			switch (ret) 
			{
			case -1:
				analogSerialManager.close();
				gui.cls_show_msg("停止发送,关闭串口后连接USB,点任意键继续");
				if((ret = analogSerialManager.open()) == -1)
				{
					gui.cls_show_msg1(g_keeptime, "line %d:测试失败(ret = %d)", Tools.getLineInfo(),ret);
					analogSerialManager.close();
					return;
				}
				
				if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
				{
					gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
					return;
				}
				gui.cls_show_msg("打开PC端串口,点任意键继续");
				break;
				
			case BUFSIZE_SERIAL:
				gui.cls_show_msg1(2, "接收成功");
				break;
				
			default:
				gui.cls_show_msg1(2,"line %d:未知的返回值(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
		}
		analogSerialManager.close();
		gui.cls_show_msg1(2, "3号子用例测试通过");
		
		// case4:测试USB串口打开之后开关PC或者重启PC,POS不应该出现死机等异常
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败ret = %d", Tools.getLineInfo(),ret);
			return;
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口失败(ret = %d", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg("请把pc关机再开机或重启后,点任意键继续");
		gui.cls_show_msg1(2, "测试通过");
		analogSerialManager.close();
	}
	// end by 20150618
	
	//usb从串口
	public void fromModel() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("USB从模式测试\n0.usb开关压力\n1.POS<->PC\n2.PC->POS\n3.异常\n4.流程\n5.POS->PC");
			switch (returnValue) 
			{
			
			case '0':
				usbSwitchPre();
				break;
				
			case '1':
				posCommPc();
				break;
				
			case '2':
				pcToPos();
				break;
				
			case '3':
				usbAbnormal();
				break;
				
			case '4':
				usbOpenCommClose();
				break;
				
			case '5':
				usbSendData();
				break;
				
			case ESC:
				return;
			}
		}
	}
	
	// add by 20150908
	// POS->POS
	public void usbSendData() 
	{
		int ret = -1,cnt = 0;
		byte[] buf = new byte[1024+1];
		PacketBean packet = new PacketBean();
		
		/*process body*/
		// 设置默认次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		cnt = packet.getLifecycle();
		
		// 测试前置,关闭串口
		analogSerialManager.close();
		gui.cls_show_msg("请将POS和PC通过USB线连接,点任意键继续");
		// 打开串口操作
		if((ret = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败ret=%d", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		// 清空缓存的操作没有？？
		gui.cls_show_msg("请把AccessPort工具打开串口,点任意键继续");
		Arrays.fill(buf, (byte) 0);
		
		for (int j = 0; j < buf.length; j++) 
			buf[j] = (byte) (Math.random()*256);
		
		// 写数据
		for (int j = 0; j < cnt; j++) 
		{
			if((ret = analogSerialManager.write(buf, 1024, MAXTRACKLEN))!=1024)
			{
				gui.cls_show_msg1(g_keeptime, "line %d:第%d次发送失败%d", Tools.getLineInfo(),j+1,ret);
				analogSerialManager.close();
				return;
			}
			SystemClock.sleep(1000);
		}
		if(gui.ShowMessageBox(("PC端接收到的数据是否为"+cnt*1024).getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:连续发送数据测试失败", Tools.getLineInfo());
			analogSerialManager.close();
			return;
		}
		gui.cls_show_msg("测试完毕,请断开AccessPort连接,完成任意键继续");
		analogSerialManager.close();
	}
	// end by 20150908
	
	// add by 20150618
	//PC<->POS
	public void posCommPc() 
	{
		/*private & local definition*/
		int ret = -1,i = 0,rlen = 0,slen = 0,succ = 0,cnt = 0,nFirstRun = 0;
		// 2K的数据
		byte[] buf = new byte[BUFSIZE_SERIAL+1];
		byte[] rbuf = new byte[BUFSIZE_SERIAL+1];
		PacketBean packet = new PacketBean();
		
		/*process body*/
		// 设置默认次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		cnt = packet.getLifecycle();
		
		// 测试前置,关闭串口
		analogSerialManager.close();
		gui.cls_show_msg("请将POS和PC通过USB线连接,点任意键继续");
		
		// 打开串口
		if((ret = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败(ret=%d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败(ret=%d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		
		gui.cls_show_msg("请把AccessPort工具打开串口,点任意键继续");
		gui.cls_show_msg1(2, "发送数据中");
		for (int j = 0; j < buf.length; j++) 
		{
			buf[j] = (byte) (Math.random()*256);
		}
		
		i = 0;
		while(true)
		{
			if(gui.cls_show_msg1(3, "USB串口读写压力中\n总共:%d次\n已进行:%d次\n,取消键退出测试", cnt,i)==ESC)
				break;
			if(i++>=cnt)
				break;
			// 清空缓存区??没有该接口
			// 写数据到串口
			if((slen = analogSerialManager.write(buf, BUFSIZE_SERIAL, MAXWAITTIME))!=BUFSIZE_SERIAL)
			{
				if(gui.cls_show_msg1_record(TAG, "posCommPc", g_keeptime, "line %d:第%d次发送失败ret=%d", Tools.getLineInfo(),i,slen)==NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			SystemClock.sleep(500);
			if(nFirstRun==0)
			{
				nFirstRun++;
				gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并开启自动发送,点任意键继续");
			}
			Arrays.fill(rbuf, (byte) 0);
			if ((rlen = analogSerialManager.read(rbuf, BUFSIZE_SERIAL,
					MAXWAITTIME)) != BUFSIZE_SERIAL) 
			{
				if (gui.cls_show_msg1_record(TAG, "posCommPc",g_keeptime, "line %d:第%d次接收错(实际%d,预期%d)",
						Tools.getLineInfo(), i, rlen, BUFSIZE_SERIAL) == NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			// 比较收发数据
			if (!Tools.memcmp(buf, rbuf, BUFSIZE_SERIAL)) 
			{
				if (gui.cls_show_msg1_record(TAG, "posCommPc",
						g_keeptime, "line %d:第%d次数据校验失败", Tools.getLineInfo(), i) == NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			succ++;
		}
		
		gui.cls_show_msg("测试完毕,请断开AccessPort连接并停止发送,点任意键继续");
		gui.cls_show_msg1_record(TAG, "posCommPc",g_time_0, "USB串口接收压力测试完成,执行次数:%d次,成功%d次", cnt,succ);
		analogSerialManager.close();
	}
	// end by 20150618
	
	// add by 20150618
	//pc-->pos
	public void pcToPos() 
	{
		/*private & local definition*/
		int ret = -1,i = 0,rlen = 0,slen = 0,succ = 0,cnt = 0;
		// 2K的数据
		byte[] buf = new byte[BUFSIZE_SERIAL+1];
		byte[] rbuf = new byte[BUFSIZE_SERIAL+1];
		PacketBean packet = new PacketBean();
		
		/*process body*/
		// 设置默认次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		cnt = packet.getLifecycle();
		
		// 测试前置,关闭串口
		analogSerialManager.close();
		gui.cls_show_msg("请将POS和PC通过USB线连接,点任意键继续");
		// 打开串口操作
		if((ret = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:打开USB串口失败(ret=%d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:设置USB串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
			analogSerialManager.close();
			return;
		}
		// 清空缓存的操作没有？？
		gui.cls_show_msg("请把AccessPort工具打开串口,点任意键继续");
		gui.cls_show_msg1(2, "发送数据中");
		for (int j = 0; j < buf.length; j++) 
		{
			buf[j] = (byte) (Math.random()*256);
		}
		// 写数据到串口
		if((slen = analogSerialManager.write(buf, BUFSIZE_SERIAL, MAXWAITTIME))!=BUFSIZE_SERIAL)
		{
			gui.cls_show_msg1(g_keeptime, "line %d:发送失败(ret=%d)", Tools.getLineInfo(),slen);
			analogSerialManager.close();
			return;
		}
		gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并开启自动发送,完成点任意键继续");
		// 接收压力测试
		i = 0;
		while(true)
		{
			if(gui.cls_show_msg1(3, "USB串口接收压力中\n总共:%d次\n已进行:%d次\n,[取消]退出测试", cnt,i)==ESC)
				break;
			if(i++>=cnt)
				break;
			SystemClock.sleep(500);
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = analogSerialManager.read(rbuf, BUFSIZE_SERIAL, MAXWAITTIME))!=BUFSIZE_SERIAL)
			{
				if(gui.cls_show_msg1_record(TAG, "posCommPc", g_keeptime, "line %d:第%d次接收错(实际%d,预期%d)", Tools.getLineInfo(),i,rlen,BUFSIZE_SERIAL)==NDK_ERR_QUIT)
					break;	
				else
					continue;
			}
			// 比较收发数据
			if(!Tools.memcmp(buf, rbuf, BUFSIZE_SERIAL))
			{
				if(gui.cls_show_msg1_record(TAG, "posCommPc", g_keeptime, "line %d:第%d次数据校验失败", Tools.getLineInfo(),i)==NDK_ERR_QUIT)
					break;	
				else
					continue;
			}
			succ++;
		}
		
		gui.cls_show_msg("测试完毕,请断开AccessPort连接并停止发送,点任意键继续");
		gui.cls_show_msg1_record(TAG, "posCommPc", g_time_0,"USB串口接收压力测试完成,执行次数:%d次,成功%d次", cnt,succ);
		analogSerialManager.close();
	}
	// end by 20150618
	
	// add by 20150618
	// pc<->poc 流程压力
	public void usbOpenCommClose() 
	{
		/*private & local definition*/
		int ret = -1,i = 0,rlen = 0,slen = 0,succ = 0,cnt = 0,nFirstRun = 0;
		PacketBean packet = new PacketBean();
		byte[] rbuf = new byte[BUFSIZE_SERIAL+1];
		byte[] buf = new byte[BUFSIZE_SERIAL+1];
		
		/*process body*/
		// 设置压力次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		cnt = packet.getLifecycle();
		
		//测试i前置,关闭串口 
		analogSerialManager.close();
		for (int j = 0; j < buf.length; j++) 
		{
			buf[j] = (byte) (Math.random()*256);
		}
		gui.cls_show_msg("请将POS和PC通过USB线连接,点任意键继续");
		// 读写压力测试
		i = 0;
		while(true)
		{
			if(gui.cls_show_msg1(3, "USB串口读写压力中\n总共:%d次\n已进行:%d次\n,[取消]键退出测试", cnt,i)==ESC)
				break;
			if(i++>=cnt)
				break;
			if((ret = analogSerialManager.open())==-1)
			{
				gui.cls_show_msg1_record(TAG, "usbOpenCommClose", 1,"line %d:打开USB串口失败(ret = %d)", Tools.getLineInfo(),ret);
				return;
			}
			if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "usbOpenCommClose", 1,"line %d:设置USB串口参数失败(ret = %d)", Tools.getLineInfo(),ret);
				return;
			}
			
			if(nFirstRun == 0)
			{
				gui.cls_show_msg("请把AccessPort工具打开连接串口,点任意键继续");
			}
			else
			{
				gui.cls_show_msg("请把AccessPort打开串口并发送,点任意键继续");
			}
			// 清空接收缓冲区？？
			// 收发数据操作
			if((slen = analogSerialManager.write(buf, BUFSIZE_SERIAL, MAXWAITTIME))!= BUFSIZE_SERIAL)
			{
				if(gui.cls_show_msg1_record(TAG, "usbOpenCommClose", g_keeptime, "line %d:第%d次发送失败(ret = %d)", Tools.getLineInfo(),slen)== NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			SystemClock.sleep(500);
			gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并开启自动发送,点任意键继续");
			Arrays.fill(rbuf, (byte) 0);
			if((rlen = analogSerialManager.read(rbuf, BUFSIZE_SERIAL, MAXWAITTIME))!= BUFSIZE_SERIAL)
			{
				if(gui.cls_show_msg1_record(TAG, "usbOpenCommClose", g_keeptime, "line %d:第%d次接收错(ret = %d)", Tools.getLineInfo(),i,rlen)==NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			if(!Tools.memcmp(buf, rbuf, BUFSIZE_SERIAL))
			{
				if(gui.cls_show_msg1_record(TAG, "usbOpenCommClose", g_keeptime, "line %d:第%d次数据校验失败", Tools.getLineInfo(),i)==NDK_ERR_QUIT)
					break;
				else
					continue;
			}
			succ++;
			analogSerialManager.close();
			gui.cls_show_msg("关闭AccessPort串口并清空数据,点任意键继续");
		}
		gui.cls_show_msg("测试完毕,请断开AccessPort连接并停止发送,完成任意键继续");
		gui.cls_show_msg1_record(TAG, "usbOpenCommClose", g_time_0,"USB串口读写压力测试完成,执行次数:%d次,成功%d次", cnt,succ);
		analogSerialManager.close();
	}
	// end by 20150618
	
	// add by 20150618
	//客户端操作
	public void clientOperation() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("客户端操作\n0.打开usb串口\n1.收发数据\n2.连续发数据\n3.关闭usb串口");
			int ret = -1,slen = 0,rlen = 0;
			byte[] sbuf = new byte[BUFSIZE_SERIAL+1];
			byte[] rbuf = new byte[BUFSIZE_SERIAL+1];
			
			switch (returnValue) 
			{
			
			case '0':
				gui.cls_show_msg1(1, "打开usb串口");
				if((ret = analogSerialManager.open())==-1)
				{
					gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s串口打开失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					analogSerialManager.close();
					break;
				}
				if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s串口参数设置失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					analogSerialManager.close();
					break;
				}
				gui.cls_show_msg1(1, "打开串口成功");
				clientOperation();
				break;
				
			case '1':
				gui.cls_show_msg1(1, "收发数据");
				Arrays.fill(rbuf, (byte) 0);
				gui.cls_show_msg("请把AccessPort工具打开连接串口,点任意键继续");
				// 发数据
				if((slen = analogSerialManager.write(sbuf, rbuf.length, MAXWAITTIME))!= rbuf.length)
				{
					gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,slen);
					analogSerialManager.close();
					break;
				}
				 gui.cls_show_msg("请将AccessPort接收到的数据复制到发送框并开启自动发送,点任意键继续");
				// 清空缓存区？？
				int i=0;
				while(true)
				{
					i++;
					if(gui.cls_show_msg1(3, "第"+i+"次收发数据中,数据大小为"+slen+"B,[取消]键退出测试")==ESC)
						break;
					// 收数据
					if((rlen = analogSerialManager.read(rbuf, rbuf.length, MAXWAITTIME))!= rbuf.length)
					{
						gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s测试失败(ret=%d,预期=%d)", Tools.getLineInfo(),TESTITEM,rlen,rbuf.length);
						analogSerialManager.close();
						break;
					}
					
					// 发数据
					if((slen = analogSerialManager.write(sbuf, rlen, MAXWAITTIME))!= rlen)
					{
						gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,slen);
						analogSerialManager.close();
						break;
					}
					
				}
				gui.cls_show_msg1(1,  "收发数据成功");
				clientOperation();
				break;
				
			case '2':
				gui.cls_show_msg1(1, "连续发数据");
				// 连续发数据
				Arrays.fill(sbuf, (byte) 0x38);
				int j=0;
				gui.cls_show_msg("请把AccessPort工具打开连接串口,点任意键继续");
				while(true)
				{
					j++;
					if(gui.cls_show_msg1(3, "第"+j+"次数据连续发送中,数据大小为"+sbuf.length+"B,[取消]键退出测试")==ESC)
						break;
					if((ret=analogSerialManager.write(sbuf, sbuf.length, MAXWAITTIME))!= sbuf.length)
					{
						gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s测试失败(ret=%d,预期=%d)", Tools.getLineInfo(),TESTITEM,ret,sbuf.length);
						analogSerialManager.close();
						break;
					}
				}
				gui.cls_show_msg1(1, "连续发数据成功");
				clientOperation();
				break;
				
			case '3':
				gui.cls_show_msg1(1, "关闭usb串口");
				if((ret = analogSerialManager.close())!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "clientFun", g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					break;
				}
				gui.cls_show_msg1(1, "关闭USB串口成功");
				clientOperation();
				break;
				
			case ESC:
				return;
			}
		}
	}
}
