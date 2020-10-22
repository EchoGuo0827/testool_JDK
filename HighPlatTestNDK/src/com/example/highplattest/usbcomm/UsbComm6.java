package com.example.highplattest.usbcomm;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Usb虚拟串口模块
 * file name 		: UsbComm6.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160119
 * directory 		: 
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160119	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm6 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = UsbComm6.class.getSimpleName();
	private AnalogSerialManager analogSerialManager = null;
	private final String TESTITEM = "USB高速串口";
	private final int BUFSIZE_SERIAL = 1024*4;
	private final int MAXWAITTIME = 30;
	private final int SNDTIMES = 10;
	private final int SNDCNT = 71;
	private final int SNDCNT1 = 128;
	private Gui gui = new Gui(myactivity, handler);
	public void usbcomm6()
	{
		String funcName="usbcomm6";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		int ret = 0;
		byte[] sendBuf = new byte[BUFSIZE_SERIAL];
		byte[] recvBuf = new byte[BUFSIZE_SERIAL];
		int j = 0;
		long oldTime = 0;
		
		
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		// 关闭usb串口
		analogSerialManager.close();
		
		// case1:pos与pc连接时,未打开串口工具进行发送操作,应返回相应失败
		if (gui.cls_show_msg("是否测试case1?pos与pc连接时,为打开串口工具进行发送操作,应返回相应失败,按取消键跳过") != ESC) {
			
		
		gui.cls_show_msg("请将POS和PC通过USB线连接,后点击[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		Arrays.fill(sendBuf, (byte) 0x38);
		
		if((ret = analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME)) == NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "子用例1测试通过");
		
	}
		
		// case2:pos与pc连接时,未打开就进行接收,应该返回错误
		if (gui.cls_show_msg("是否测试case2?pos与pc连接时,未打开就进行接收,应该返回错误,按取消键跳过") != ESC) {
		Arrays.fill(recvBuf, (byte) 0);
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))==NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "子用例2测试通过");
		}
		
		// case3:pos和pc连接后接收超时,返回相应错误
		if (gui.cls_show_msg("是否测试case3?pos和pc连接后接收超时,返回相应错误,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过usb线连接,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))!= ANDROID_PORT_READ_FAIL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		analogSerialManager.close();
		gui.cls_show_msg1(gScreenTime, "子用例3测试通过");
		}
		// case4：拔掉USB线后将发送数据时应失败
		if (gui.cls_show_msg("是否测试case4?拔掉USB线后将发送数据时应失败,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过usb线连接,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
			
		gui.cls_show_msg("请拔USB线,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		Arrays.fill(sendBuf, (byte) 0x38);
		if((ret = analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME))==NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		analogSerialManager.close();
		gui.cls_show_msg1(gScreenTime, "子用例4测试通过");
		}
		
		// case5:拔掉USB线后接收数据时应返回失败
		if (gui.cls_show_msg("是否测试case5?拔掉USB线后接收数据时应返回失败,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过usb线连接,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open()) == NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		
		gui.cls_show_msg("请拔usb线,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		Arrays.fill(recvBuf, (byte) 0);
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))==NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		analogSerialManager.close();
		gui.cls_show_msg1(gScreenTime, "子用例5测试通过");
		}
		// case14:测试usb打开后,pc端工具未打开,发送数据应失败
		if (gui.cls_show_msg("是否测试case14?测试usb打开后,pc端工具未打开,发送数据应失败,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过USB线连接,关闭PC工具, 后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		Arrays.fill(sendBuf, (byte) 0x38);
		
		for (j = 0; j < SNDTIMES; j++) 
		{
			//write设置成-1会无限等待。read会直接返回
			if((ret = analogSerialManager.write(sendBuf, sendBuf.length, 10))<=0)
				break;
		}
		/**write接口应该会直接返回*/
		if(j==SNDTIMES)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		analogSerialManager.close();
		gui.cls_show_msg1(gScreenTime, "子用例14测试通过");
		}
		
		// case6:打开usb设备,在发送数据时拔下,应返回失败
		if (gui.cls_show_msg("是否测试case6?打开usb设备,在发送数据时拔下,应返回失败,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过usb线连接,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open()) == NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		

		gui.cls_show_msg("打开PC端串口工具,Pos端输出数据3s后拔下usb线,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		Arrays.fill(sendBuf, (byte) 0x38);
		oldTime = System.currentTimeMillis();
		//开发反馈如果在write之前拔掉usb线的情况，就会返回-2（会先去判断usb句柄是否存在）20200417陈丁
		while(true)
		{
			if(((System.currentTimeMillis()-oldTime)/1000.0)>MAXWAITTIME)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
				{
					analogSerialManager.close();
					return;
				}
			}
			ret = analogSerialManager.write(sendBuf, 128, 5);
			switch (ret) 
			{
			case 128:
				gui.cls_show_msg1(gScreenTime, "发送成功");
				break;
				
			case -1:
				break;
			case -2:
				break;

			default:
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s未知的返回值(%d)", Tools.getLineInfo(),TESTITEM,ret);
				analogSerialManager.close();
				break;
			}
			
			if(ret == -1||ret==-2)
				break;
		}
		
		analogSerialManager.close();
		gui.cls_show_msg1(gScreenTime, "子用例6测试通过");
		}
		
		// case7:打开usb设备,在接收数据时拔下,应返回失败
		if (gui.cls_show_msg("是否测试case7?打开usb设备,在接收数据时拔下,应返回失败,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过USB线连接,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
	
		gui.cls_show_msg("打开PC端串口工具后拔下usb线,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		Arrays.fill(recvBuf, (byte) 0);
		
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))>0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		analogSerialManager.close();
		gui.cls_show_msg1(gScreenTime, "子用例7测试通过");
		}
		// case8:要求打开设备后,进入休眠
		if (gui.cls_show_msg("是否测试case8?要求打开设备后,进入休眠,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过usb线连接,并重启PC端的AccessPort工具,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		// 电源键休眠
	
		gui.cls_show_msg("按电源键进入休眠,休眠五秒唤醒后点[确认]继续");
		gui.cls_show_msg("打开PC串口工具,点[确认]后POS将输出%dKB数据到PC", sendBuf.length/1024);
		gui.cls_show_msg1(1, "POS-->PC(%dKB)", sendBuf.length/1024);
		
		Arrays.fill(sendBuf, (byte) 0x38);
		
		if((ret = analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME))!=sendBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "完毕");
		
		
		gui.cls_show_msg("PC端发送数据,点[确认]后POS将在%ds内接受PC的数据", MAXWAITTIME);
		gui.cls_show_msg1(gScreenTime,"POS<--PC(%dB)", sendBuf.length);
		Arrays.fill(recvBuf, (byte) 0);
		
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))!= recvBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(3, "完毕");
		
		if(!Tools.memcmp(sendBuf, recvBuf, sendBuf.length))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,sendBuf.length);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		analogSerialManager.close();
		gui.cls_show_msg1(3, "case8通过");
		}
		// case9:pos与pc连接,打开usb口,进行正常收发4K数据
/*		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, "请将pos和pc通过usb线连接,并重启PC端的AccessPort工具,完成点击是继续"));
		GlobalVariable.PORT_FLAG = true;
		while(GlobalVariable.PORT_FLAG);
		
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s测试失败ret = %d", Tools.getLineInfo(),TESTAPI,ret);
			analogSerialManager.close();
			return;
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s测试失败ret = %d", Tools.getLineInfo(),TESTAPI,ret);
			analogSerialManager.close();
			return;
		}
		
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, String.format("打开PC串口工具,点击是POS将输出%dKB数据到PC", sendBuf.length/1024)));
		GlobalVariable.PORT_FLAG = true;
		while(GlobalVariable.PORT_FLAG);
		
		new Gui(getActivity(), handler).cls_show_msg(0, "POS-->PC(%dKB)", sendBuf.length/1024);
		
		Arrays.fill(sendBuf, (byte) 0x38);
		
		if((ret = analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME))!=sendBuf.length)
		{
			new Gui(getActivity(), handler).cls_show_msg1(1, SERIAL,"line %d:%s测试失败ret = %d", Tools.getLineInfo(),TESTAPI,ret);
			analogSerialManager.close();
			return;
		}
		
		new Gui(getActivity(), handler).cls_show_msg(1, "完毕");
		
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, String.format("PC端发送数据,点击是POS将在%ds内接受PC的数据", MAXWAITTIME)));
		GlobalVariable.PORT_FLAG = true;
		while(GlobalVariable.PORT_FLAG);
		
		new Gui(getActivity(), handler).cls_show_msg(1,"POS<--PC(%dB)", sendBuf.length);
		Arrays.fill(recvBuf, (byte) 0);
		
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))!= recvBuf.length)
		{
			new Gui(getActivity(), handler).cls_show_msg1(1, SERIAL,"line %d:%s测试失败ret = %d", Tools.getLineInfo(),TESTAPI,ret);
			analogSerialManager.close();
			return;
		}
		
		new Gui(getActivity(), handler).cls_show_msg(3, "完毕");
		
		if(!Tools.memcmp(sendBuf, recvBuf, sendBuf.length))
		{
			new Gui(getActivity(), handler).cls_show_msg1(1, SERIAL,"line %d:%s测试失败len = %d", Tools.getLineInfo(),TESTAPI,sendBuf.length);
			analogSerialManager.close();
			return;
		}*/
		
		// case10:正常收发非整8字节数据
		if (gui.cls_show_msg("是否测试case10?正常收发非整8字节数据,按取消键跳过") != ESC) {
			if((ret = analogSerialManager.open())==NDK_ERR)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
				{
					analogSerialManager.close();
					return;
				}
			}
		gui.cls_show_msg("PC清空数据,点[确认]后POS将输出%dB数据到PC", SNDCNT);
		gui.cls_show_msg1(1, "POS-->PC(%dB)", SNDCNT);
		Arrays.fill(sendBuf, (byte) 0x38);
		
		if((ret = analogSerialManager.write(sendBuf, SNDCNT, MAXWAITTIME))!=SNDCNT)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "完毕");
		
	
		gui.cls_show_msg("PC端发送数据,点[确认]后POS将在%ds内接受PC的数据", MAXWAITTIME);
		gui.cls_show_msg1(1,"POS<--PC(%dB)", SNDCNT);
		Arrays.fill(recvBuf, (byte) 0);
		
		if((ret = analogSerialManager.read(recvBuf, SNDCNT, MAXWAITTIME))!= SNDCNT)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(3, "完毕");
		
		if(!Tools.memcmp(sendBuf, recvBuf, SNDCNT))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(len = %d)", Tools.getLineInfo(),TESTITEM,SNDCNT);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		analogSerialManager.close();
		gui.cls_show_msg1(3, "case10通过");
		}
		// case11:发送64整数倍但又不超过4K的数据
		if (gui.cls_show_msg("是否测试case11?发送64整数倍但又不超过4K的数据,按取消键跳过") != ESC) {
			if((ret = analogSerialManager.open())==NDK_ERR)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
				{
					analogSerialManager.close();
					return;
				}
			}
		gui.cls_show_msg("PC清空数据,点[确认]后POS将输出%dB数据到PC", SNDCNT1);
		gui.cls_show_msg1(1, "POS-->PC(%dB)", SNDCNT1);
		Arrays.fill(sendBuf, (byte) 0x38);
		
		if((ret = analogSerialManager.write(sendBuf, SNDCNT1, MAXWAITTIME))!=SNDCNT1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "完毕");
	
		gui.cls_show_msg("PC端发送数据,点[确认]后POS将在%ds内接受PC的数据", MAXWAITTIME);
		gui.cls_show_msg1(1,"POS<--PC(%dB)", SNDCNT1);
		Arrays.fill(recvBuf, (byte) 0);
		
		if((ret = analogSerialManager.read(recvBuf, SNDCNT1, MAXWAITTIME))!= SNDCNT1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "完毕");
		
		if(!Tools.memcmp(sendBuf, recvBuf, SNDCNT1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(len = %d)", Tools.getLineInfo(),TESTITEM,SNDCNT1);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		gui.cls_show_msg1(gScreenTime, "case11通过");
		}
		// case12:写完数据马上关闭串口,不应出现异常
		if (gui.cls_show_msg("是否测试case12?写完数据马上关闭串口,不应出现异常,按取消键跳过") != ESC) {
			
			if((ret = analogSerialManager.open())==NDK_ERR)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
				{
					analogSerialManager.close();
					return;
				}
			}
		for (j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random()*256);
		}
		
		if((ret = analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME))!=sendBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.close())!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
			analogSerialManager.close();
			gui.cls_show_msg1(gScreenTime, "case12通过");
		}
		}
		// case13:重新打开串口进行数据收发应成功
		if (gui.cls_show_msg("是否测试case13?重新打开串口进行数据收发应成功,按取消键跳过") != ESC) {
		gui.cls_show_msg("请将pos和pc通过usb线连接,并重启PC端的AccessPort工具,后点[确认]继续");
		gui.cls_show_msg1(gScreenTime, "测试中");
		if((ret = analogSerialManager.open())==NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg("打开PC串口工具,后点[确认]后POS将输出%dKB数据到PC", sendBuf.length/1024);
		gui.cls_show_msg1(1, "POS-->PC(%dKB)", sendBuf.length/1024);
		
		Arrays.fill(sendBuf, (byte) 0x38);
		
		if((ret = analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME))!=sendBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(1, "完毕");
		
		gui.cls_show_msg("PC端发送数据,点[确认]后POS将在%ds内接受PC的数据", MAXWAITTIME);
		gui.cls_show_msg1(1,"POS<--PC(%dB)", sendBuf.length);
		Arrays.fill(recvBuf, (byte) 0);
		
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME))!= recvBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		
		gui.cls_show_msg1(gScreenTime, "完毕");
		
		if(!Tools.memcmp(sendBuf, recvBuf, sendBuf.length))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(len = %d)", Tools.getLineInfo(),TESTITEM,sendBuf.length);
			if(GlobalVariable.isContinue==false)
			{
				analogSerialManager.close();
				return;
			}
		}
		gui.cls_show_msg1(gScreenTime, "case13通过");
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() {
		analogSerialManager =  (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
	}
	@Override
	public void onTestDown() {
		if(analogSerialManager!=null)
			analogSerialManager.close();
		gui = null;
		analogSerialManager = null;
		
	}
}
