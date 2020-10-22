package com.example.highplattest.androidport;

import java.util.Arrays;

import android.newland.NLUART3Manager;
import android.os.Handler;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort6.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190116 
 * directory 		: 
 * description 		: 多个串口打开，多个串口均可使用
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20190116	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class AndroidPort6 extends UnitFragment
{
	private NLUART3Manager mRS32Manager=null;
	private NLUART3Manager mPinPadManager = null;
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = AndroidPort1.class.getSimpleName();
	private String TESTITEM = "多串口异常测试";
	
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{	
			case HandlerMsg.TEXTVIEW_DONGLE_CMD:// 命令通道显示界面
				mTvCmd.setText("PINPAD串口->"+(CharSequence) msg.obj);
				break;
				
			case HandlerMsg.TEXTVIEW_DONGLE_DATA:
				mTvData.setText("RS232串口->"+(CharSequence) msg.obj);
				break;
				
			default:
				break;
			}
		};
	};
	Gui mCommGui = new Gui(myactivity, handler);
	Gui mRS232Gui = new Gui(myactivity, myHandler,HandlerMsg.TEXTVIEW_DONGLE_DATA);
	Gui mPinpadGui = new Gui(myactivity, myHandler,HandlerMsg.TEXTVIEW_DONGLE_CMD);
	
	public void androidport6()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232)==false&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			mCommGui.cls_show_msg1(1, "%s产品不支持物理串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		mRS32Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		mPinPadManager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		// 应用同时打开PINPAD和RS232串口，两个串口都是可以操作
		mCommGui.cls_show_msg("往X5设备的RS232串口和PinPad串口与PC相连,并打开两个AccessPort工具,配置对应的串口,波特率配置为115200,操作完毕任意键继续");
		mCommGui.cls_printf("RS232串口和PinPad串口收发测试均通过才可视为测试通过".getBytes());
		new Thread(new RS232Run()).start();
		new Thread(new PinPadRun()).start();


	}
	
	class RS232Run implements Runnable
	{

		@Override
		public void run() 
		{
			int fd=-1,ret=-1,len;
			byte[] wBuf = new byte[1024];
			byte[] rBuf = new byte[1024];
			if((fd=mRS32Manager.open())<0)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口打开失败(fd=%d)", Tools.getLineInfo(),fd);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = mRS32Manager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口设置串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			Arrays.fill(wBuf, (byte)0x38);
			if((len=mRS32Manager.write(wBuf, wBuf.length, 30))!=wBuf.length)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口发送数据失败(len=%d)", Tools.getLineInfo(),len);
				if(!GlobalVariable.isContinue)
					return;
			}
			mRS232Gui.cls_show_msg("已经往RS232串口发送了1K的数据，请将串口工具收到的1K数据发送给RS232串口,操作完毕任意键继续");
			if((len = mRS32Manager.read(rBuf, rBuf.length, 30))!=rBuf.length)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口接收数据失败(len=%d)", Tools.getLineInfo(),len);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(Tools.memcmp(wBuf, rBuf, wBuf.length)==false)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口收发数据校验失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"RS232串口收发数据测试通过");
		}
		
	}
	
	class PinPadRun implements Runnable
	{

		@Override
		public void run() 
		{
			int fd=-1,ret=-1,len;
			byte[] wBuf = new byte[1024];
			byte[] rBuf = new byte[1024];
			if((fd=mPinPadManager.open(62))<0)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口打开失败(fd=%d)", Tools.getLineInfo(),fd);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = mPinPadManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口设置串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			Arrays.fill(wBuf, (byte)0x31);
			if((len=mPinPadManager.write(wBuf, wBuf.length, 30))!=wBuf.length)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口发送数据失败(len=%d)", Tools.getLineInfo(),len);
				if(!GlobalVariable.isContinue)
					return;
			}
			mPinpadGui.cls_show_msg("已经往PinPad串口发送了1K的数据，请将串口工具收到的1K数据发送给PinPad串口,操作完毕任意键继续");
			if((len = mPinPadManager.read(rBuf, rBuf.length, 30))!=rBuf.length)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口接收数据失败(len=%d)", Tools.getLineInfo(),len);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(Tools.memcmp(wBuf, rBuf, wBuf.length)==false)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口收发数据校验失败", Tools.getLineInfo());
				if(!GlobalVariable.isContinue)
					return;
			}
			mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"PinPad串口收发数据测试通过");
		}
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
