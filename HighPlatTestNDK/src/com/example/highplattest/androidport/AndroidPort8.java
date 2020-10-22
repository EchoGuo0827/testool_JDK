package com.example.highplattest.androidport;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import android.newland.NLUART3Manager;
import android.os.Handler;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort8.java 
 * Author 			: huhuij
 * version 			: 
 * DATE 			: 20200103
 * directory 		: 
 * description 		: 多个串口打开，多个串口均可使用
 * related document : 
 * history 		 	: author			date			remarks
 *		
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class AndroidPort8 extends UnitFragment
{
	private NLUART3Manager mRS32Manager=null;
	private NLUART3Manager mPinPadManager = null;
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = AndroidPort6.class.getSimpleName();
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
	
	public void androidport8()
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
		
//		Thread th_rs232 = new Thread(new RS232Run());
//		Thread th_pinpad = new Thread(new PinPadRun());
//		th_rs232.start();
//		try {
//			th_rs232.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		th_pinpad.start();
		
		Thread th = new Thread(new RS232toPinPadRun());
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	class MyService{
		private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		public void update (NLUART3Manager rwManager,int fd){
			rwManager.update(fd);
		}
		public int read(NLUART3Manager rwManager,byte[] rBuf) {
			int result = -1;
			try {
				try {
					lock.readLock().lock();
					result = rwManager.read(rBuf, rBuf.length, 30);
					Thread.sleep(500);
				} finally {
					lock.readLock().unlock();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		public int write(NLUART3Manager rwManager,byte[] wBuf) {
			int result = -1;
			try {
				try {
					lock.writeLock().lock();
					result = rwManager.write(wBuf, wBuf.length, 30);
					Thread.sleep(500);
				} finally {
					lock.writeLock().unlock();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	class RS232Run implements Runnable
	{
		final MyService service = new MyService();

		@Override
		public void run() 
		{
			
			int fd=-1,ret=-1,len;
			int j = 3;
			byte[] wBuf = new byte[1024];
			byte[] rBuf = new byte[1024];
			if((fd=mRS32Manager.open(62))<0)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口打开失败(fd=%d)", Tools.getLineInfo(),fd);
				if(!GlobalVariable.isContinue)
					return;
			}
//			mRS32Manager.update(fd);
			service.update(mRS32Manager,fd);//mj
			if((ret = mRS32Manager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口设置串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
				LoggerUtil.e("RS232串口设置串口参数失败："+ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			while (j>0){
				j--;
				
				Arrays.fill(wBuf, (byte)0x38);
//				service.update(mRS32Manager,fd);//mj
				if((len = service.write(mRS32Manager, wBuf))!=wBuf.length)
				{
					mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口发送数据失败(len=%d)", Tools.getLineInfo(),len);
					LoggerUtil.e("RS232串口发送数据失败");
					LoggerUtil.e("RS232串口发送数据长度："+wBuf.length);
					LoggerUtil.e("RS232串口发送数据:"+bytesToHex(wBuf));
					if(!GlobalVariable.isContinue)
						return;
				}
				else {
					LoggerUtil.e("RS232串口发送数据成功");
					LoggerUtil.e("RS232串口发送数据长度："+wBuf.length);
					LoggerUtil.e("RS232串口发送数据:"+bytesToHex(wBuf));
				}
				mRS232Gui.cls_show_msg("已经第%d次往RS232串口发送了1K的数据，请将串口工具收到的1K数据发送给RS232串口,操作完毕后清除接收框的数据，任意键继续",3-j);
//				service.update(mRS32Manager,fd);//mj
				if((len = service.read(mRS32Manager,rBuf))!=rBuf.length)//mj
				{//mj
					mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口接收数据失败(len=%d)", Tools.getLineInfo(),len);//mj
					LoggerUtil.e("RS232串口接收数据失败");
					LoggerUtil.e("RS232串口接收数据长度："+rBuf.length);
					LoggerUtil.e("RS232串口接收数据:"+bytesToHex(rBuf));
					if(!GlobalVariable.isContinue)//mj
						return;//mj
				}//mj
				else{
					LoggerUtil.e("RS232串口接收数据成功");
					LoggerUtil.e("RS232串口接收数据长度："+rBuf.length);
					LoggerUtil.e("RS232串口接收数据:"+bytesToHex(rBuf));
				}
				if(Tools.memcmp(wBuf, rBuf, wBuf.length)==false)
				{
					mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口收发数据校验失败", Tools.getLineInfo());
					LoggerUtil.e("RS232串口收发数据校验失败");
					if(!GlobalVariable.isContinue)
						return;
				}
				else LoggerUtil.e("RS232串口收发数据校验成功");
			}
			mRS32Manager.close();
			mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"RS232串口收发数据测试通过");//mj
		}
		
	}
	
	class PinPadRun implements Runnable
	{
		final MyService service = new MyService();
		
		@Override
		public void run() 
		{
			int fd=-1,ret=-1,len;
			byte[] wBuf = new byte[1024];
			byte[] rBuf = new byte[1024];
			int i = 3;

			if((fd=mPinPadManager.open())<0)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口打开失败(fd=%d)", Tools.getLineInfo(),fd);
				if(!GlobalVariable.isContinue)
					return;
			}
			
//			mPinPadManager.update(fd);
			service.update(mPinPadManager,fd);
			if((ret = mPinPadManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口设置串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
				LoggerUtil.e("PinPad串口设置串口参数失败: "+ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			while(i>0){
			i--;
			Arrays.fill(wBuf, (byte)0x31);
//			service.update(mPinPadManager,fd);
			if((len = service.write(mPinPadManager,wBuf))!=wBuf.length)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口发送数据失败(len=%d)", Tools.getLineInfo(),len);
				LoggerUtil.e("PinPad串口发送数据失败");
				if(!GlobalVariable.isContinue)
					return;
			}
			else {
				LoggerUtil.e("PinPad串口发送数据成功");
				LoggerUtil.e("PinPad串口发送数据长度："+wBuf.length);
				LoggerUtil.e("PinPad串口发送数据:"+bytesToHex(wBuf));
			}
			mPinpadGui.cls_show_msg("已经第%d次往PinPad串口发送了1K的数据，请将串口工具收到的1K数据发送给PinPad串口,操作完毕后清除接收框的数据，任意键继续",3-i);
//			service.update(mPinPadManager,fd);
			if((len = service.read(mPinPadManager,rBuf))!=rBuf.length)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口接收数据失败(len=%d)", Tools.getLineInfo(),len);
				LoggerUtil.e("PinPad串口接收数据失败");
				LoggerUtil.e("PinPad串口接收数据长度："+rBuf.length);
				LoggerUtil.e("PinPad串口接收数据："+bytesToHex(rBuf));
				if(!GlobalVariable.isContinue)
					return;
			}
			else{
				LoggerUtil.e("PinPad串口接收数据成功");
				LoggerUtil.e("PinPad串口接收数据长度："+rBuf.length);
				LoggerUtil.e("PinPad串口接收数据:"+bytesToHex(rBuf));
			}
			if(Tools.memcmp(wBuf, rBuf, wBuf.length)==false)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口收发数据校验失败", Tools.getLineInfo());
				LoggerUtil.e("PinPad串口收发数据校验失败");
				if(!GlobalVariable.isContinue)
					return;
			}
			else LoggerUtil.e("PinPad串口收发数据校验成功");
			}
			mPinPadManager.close();
			mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"PinPad串口收发数据测试通过");
		}
		
	}
	
	class RS232toPinPadRun implements Runnable
	{
		final MyService service = new MyService();
		@Override
		public void run() {
			int rs232_fd=-1,pinpad_fd=-1,ret=-1,len;
			byte[] rs232_wBuf = new byte[1024];
			byte[] pinpad_wBuf = new byte[1024];
			byte[] rBuf = new byte[1024];
			int i = 3;
			if((rs232_fd=mRS32Manager.open(62))<0)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口打开失败(fd=%d)", Tools.getLineInfo(),rs232_fd);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = mRS32Manager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口设置串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
				LoggerUtil.e("RS232串口设置串口参数失败："+ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if((pinpad_fd=mPinPadManager.open())<0)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口打开失败(fd=%d)", Tools.getLineInfo(),pinpad_fd);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = mPinPadManager.setconfig(115200, 0, "8N1NN".getBytes()))!=NDK_OK)
			{
				mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口设置串口参数失败(ret=%d)", Tools.getLineInfo(),ret);
				LoggerUtil.e("PinPad串口设置串口参数失败: "+ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			while(i>0){
				i--;
				Arrays.fill(pinpad_wBuf, (byte)0x31);
				Arrays.fill(rs232_wBuf, (byte)0x38);
				
				
				service.update(mRS32Manager,rs232_fd);//mj
				if((len = service.write(mRS32Manager, rs232_wBuf))!=rs232_wBuf.length)
				{
					mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口发送数据失败(len=%d)", Tools.getLineInfo(),len);
					LoggerUtil.e("RS232串口发送数据失败");
					LoggerUtil.e("RS232串口发送数据长度："+rs232_wBuf.length);
					LoggerUtil.e("RS232串口发送数据:"+bytesToHex(rs232_wBuf));
					if(!GlobalVariable.isContinue)
						return;
				}
				else {
					LoggerUtil.e("RS232串口发送数据成功");
					LoggerUtil.e("RS232串口发送数据长度："+rs232_wBuf.length);
					LoggerUtil.e("RS232串口发送数据:"+bytesToHex(rs232_wBuf));
				}
				mRS232Gui.cls_show_msg("已经第%d次往RS232串口发送了1K的数据，请将串口工具收到的1K数据发送给RS232串口,操作完毕后清除接收框的数据，任意键继续",3-i);
				if((len = service.read(mRS32Manager,rBuf))!=rBuf.length)//mj
				{//mj
					mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口接收数据失败(len=%d)", Tools.getLineInfo(),len);//mj
					LoggerUtil.e("RS232串口接收数据失败");
					LoggerUtil.e("RS232串口接收数据长度："+rBuf.length);
					LoggerUtil.e("RS232串口接收数据:"+bytesToHex(rBuf));
					if(!GlobalVariable.isContinue)//mj
						return;//mj
				}//mj
				else{
					LoggerUtil.e("RS232串口接收数据成功");
					LoggerUtil.e("RS232串口接收数据长度："+rBuf.length);
					LoggerUtil.e("RS232串口接收数据:"+bytesToHex(rBuf));
				}
				if(Tools.memcmp(rs232_wBuf, rBuf, rs232_wBuf.length)==false)
				{
					mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:RS232串口收发数据校验失败", Tools.getLineInfo());
					LoggerUtil.e("RS232串口收发数据校验失败");
					if(!GlobalVariable.isContinue)
						return;
				}
				else mRS232Gui.cls_show_msg("第%d次RS232串口收发数据校验成功，任意键继续",3-i);
				
				
				service.update(mPinPadManager,pinpad_fd);
				if((len = service.write(mPinPadManager,pinpad_wBuf))!=pinpad_wBuf.length)
				{
					mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口发送数据失败(len=%d)", Tools.getLineInfo(),len);
					LoggerUtil.e("PinPad串口发送数据失败");
					if(!GlobalVariable.isContinue)
						return;
				}
				else {
					LoggerUtil.e("PinPad串口发送数据成功");
					LoggerUtil.e("PinPad串口发送数据长度："+pinpad_wBuf.length);
					LoggerUtil.e("PinPad串口发送数据:"+bytesToHex(pinpad_wBuf));
				}
				mPinpadGui.cls_show_msg("已经第%d次往PinPad串口发送了1K的数据，请将串口工具收到的1K数据发送给PinPad串口,操作完毕后清除接收框的数据，任意键继续",3-i);
				if((len = service.read(mPinPadManager,rBuf))!=rBuf.length)
				{
					mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口接收数据失败(len=%d)", Tools.getLineInfo(),len);
					LoggerUtil.e("PinPad串口接收数据失败");
					LoggerUtil.e("PinPad串口接收数据长度："+rBuf.length);
					LoggerUtil.e("PinPad串口接收数据："+bytesToHex(rBuf));
					if(!GlobalVariable.isContinue)
						return;
				}
				else{
					LoggerUtil.e("PinPad串口接收数据成功");
					LoggerUtil.e("PinPad串口接收数据长度："+rBuf.length);
					LoggerUtil.e("PinPad串口接收数据:"+bytesToHex(rBuf));
				}
				if(Tools.memcmp(pinpad_wBuf, rBuf, pinpad_wBuf.length)==false)
				{
					mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"line %d:PinPad串口收发数据校验失败", Tools.getLineInfo());
					LoggerUtil.e("PinPad串口收发数据校验失败");
					if(!GlobalVariable.isContinue)
						return;
				}
				else mPinpadGui.cls_show_msg("第%d次PinPad串口收发数据校验成功，任意键继续",3-i);
			}
			mRS32Manager.close();
			mRS232Gui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"RS232串口收发数据测试通过");//mj
			mPinPadManager.close();
			mPinpadGui.cls_show_msg1_record(CLASS_NAME, "run",gScreenTime,"PinPad串口收发数据测试通过");
		}
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	public static String bytesToHex(byte[] bytes) {  
		StringBuffer sb = new StringBuffer();  
		for(int i = 0; i < bytes.length; i++) {  
			String hex = Integer.toHexString(bytes[i] & 0xFF);  
			if(hex.length() < 2){  
				sb.append(0);  
			}  
			sb.append(hex);  
		}  
		return sb.toString();  
	} 
}
