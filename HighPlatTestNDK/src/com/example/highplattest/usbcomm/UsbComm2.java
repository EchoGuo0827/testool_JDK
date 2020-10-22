package com.example.highplattest.usbcomm;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150619 
 * directory 		: 
 * description 		: usb模块串口模块的read
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150619	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm2 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = UsbComm2.class.getCanonicalName();
	private final int MAX_SIZE = 1024*4;
	private final int MAXWAITTIME = 30;
	private AnalogSerialManager analogSerialManager = null;
	private final String TESTITEM = "虚拟串口read";
	long oldTime;
	float time;
	int retBack;
	int ret1 =-1;
	long otherTime;
	private Gui gui = new Gui(myactivity, handler);
	
	public void usbcomm2() 
	{
		String funcName="usbcomm2";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
	
		/*private & local definition*/
		// 实例化接口对象
		int ret = -1,ret2=-1,ret3 = -1;
		//开发反馈受性能等因素影响 A9误差需要稍微调大一些 改为0.05f；A5不变，在子线程中性能会受到一些影响，统一调整为500ms
		float WUCHASEC= 500;// 可允许300ms的误差
		// 设备节点描述符
		byte[] sendBuf = new byte[MAX_SIZE];
		final byte[] recvBuf = new byte[MAX_SIZE];
//		String[] para={"8N1NB","8N1NN"};
		byte[] testbuf = new byte[0];
		
		/*process body*/
		gui.cls_show_msg1(2, TESTITEM+"测试中，请别开启自动发送...");
		// 测试前置，关闭串口
		analogSerialManager.close();
		
		// case1:流程异常，对未打开的串口进行读操作，应返回ANDROID_PORT_FD_ERR(-2)
		Arrays.fill(recvBuf, (byte) 0);
		if (((ret = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) != ANDROID_PORT_FD_ERR)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		String message = "请确保POS和PC已通过USB线连接,并开启PC端的AccessPort工具,后点[确认]继续";
		gui.cls_show_msg(message);
		Log.e("2", "2");
		// case2:参数错误，传入非法参数，非法长度，应该返回ANDROID_PORT_READ_FAIL(-1)(巫丙亮反馈USB串口没有阻塞模式)
		// 设置为阻塞的时候，还是会卡死
		if (analogSerialManager.open() == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//开发意见：null 返回-6 (<参数非法*/)  0返回-1(<错误参数*/) read函数如此  setconfig函数 错误参数返回-14
		if ( (ret = analogSerialManager.read(null, MAX_SIZE, MAXWAITTIME))!= NDK_ERR_PARA|
				(ret1 = analogSerialManager.read(testbuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId + 1)))!=NDK_ERR)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d,%d,%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2,ret3);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		
		// case4:串口阻塞/非阻塞  读：阻塞/非阻塞，自发自收是否正常
		// case6:串口接收限制长度为2K
		// case7:测试读完数据马上关闭串口，不应该出现异常(跑飞或者死机)
		// para[0]:阻塞    para[1]:非阻塞
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes());
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME/ (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 串口非阻塞 读阻塞
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.e("read1", ret1+"");
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d，%d)", Tools.getLineInfo(),TESTITEM,ret1,ret2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 串口非阻塞 读非阻塞 读到的数据不确定
		message = "请清空发送接收缓冲区的数据,后点[确认]继续";
		gui.cls_show_msg(message);

		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.e("write1", ret+"");
		
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		Arrays.fill(recvBuf, (byte) 0);
		ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, 0);
		if( ret1 != ANDROID_PORT_READ_FAIL && ret1<=0) 
		{
			analogSerialManager.close();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.d("zxq", ret1+" 00");
		
		message = "请清空发送接收缓冲区的数据,后点[确认]继续";
		gui.cls_show_msg(message);
		
		// 之前的数据没被读走，最好清空接收缓冲区确保
		analogSerialManager.close();
		analogSerialManager.open();
 
//		Log.e("4", "4");
//		// para[0]:阻塞
//		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes()))!= ANDROID_OK)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		Log.e("write3", ret+"");
//		
//		// 串口阻塞 读阻塞
//		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
//		gui.cls_show_msg(message);
//		
//		Arrays.fill(recvBuf, (byte) 0);
//		ret1 = -1;
//		
//		new Thread()
//		{
//			public void run() 
//			{
//				otherTime = System.currentTimeMillis();
//				if ((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) !=MAX_SIZE) 
//				{
//					synchronized (g_lock) {
//						g_lock.notify();
//					}
//					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
//					if(GlobalVariable.isContinue==false)
//						return;
//				}
//				else
//				{
//					synchronized (g_lock) {
//						g_lock.notify();
//				}
//				LoggerUtil.d(TAG+",usbcomm2===notify");
//				}
//			};
//		}.start();
//		LoggerUtil.d(TAG+",usbcomm2===start wait");
//		synchronized (g_lock) {
//			try {
//				g_lock.wait(MAXWAITTIME*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		LoggerUtil.d(TAG+",usbcomm2===wait end");
//		
//		if(Tools.getStopTime(otherTime)>=MAXWAITTIME) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败超时失败", Tools.getLineInfo(),TESTITEM);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d，%d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
////		LoggerUtil.d(TAG+",usbcomm2===write4");
//		// 串口阻塞 读非阻塞，实际应为阻塞
//		message = "请清空接收缓冲区的数据,后点[确认]继续";
//		gui.cls_show_msg(message);
//		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
//		gui.cls_show_msg(message);
//		Log.e("write4", ret+" "+ret1);
//		Arrays.fill(recvBuf, (byte) 0);
//		
//		new Thread()
//		{
//			public void run() 
//			{
//				otherTime = System.currentTimeMillis();
//				if ((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
//				{
//					synchronized (g_lock) {
//						g_lock.notify();
//					}
//					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
//					if(GlobalVariable.isContinue==false)
//						return;
//				}
//				synchronized (g_lock) {
//					g_lock.notify();
//				}
//			};
//		}.start();
//		synchronized (g_lock) {
//			try {
//				g_lock.wait(MAXWAITTIME*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		if(Tools.getStopTime(otherTime)>MAXWAITTIME) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败超时失败", Tools.getLineInfo(),TESTITEM);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE))
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d，%d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		message = "请清空接收缓冲区的数据,后点[确认]继续";
//		gui.cls_show_msg(message);
//		if((ret = analogSerialManager.close())!=ANDROID_OK)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s串口关闭失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
		// 目前对读写大小没有限制
		/*// case8:读数据长度超过缓冲区大小应读失败
		if((ret = analogSerialManager.open()) == -1)
		{
			if(!new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s串口打开失败ret = %d", Tools.getLineInfo(),TESTAPI,ret))
				return;
		}
		if((ret = analogSerialManager.setconfig(BpsSetting.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			analogSerialManager.close();
			if(!new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s设置串口失败ret = %d", Tools.getLineInfo(),TESTAPI,ret))
				return;
		}
		
		byte[] sendBuf1 = new byte[1024*5];
		byte[] recvBuf1 = new byte[1024*5];
		
		for (int j = 0; j < sendBuf1.length; j++) 
		{
			sendBuf1[j] = (byte) (Math.random() * 256);
		}
		
		if((ret = analogSerialManager.write(sendBuf1, 1024*5, MAXWAITTIME))!= 1024*5)
		{
			analogSerialManager.close();
			if(!new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s写串口数据失败ret = %d", Tools.getLineInfo(),TESTAPI,ret))
				return;
		}
		
		message = "请将AccessPort接收到的数据复制到发送框并发送，点击是继续";
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, message));
		GlobalVariable.PORT_FLAG = true;
		while(GlobalVariable.PORT_FLAG);
		
		if((ret1 = analogSerialManager.read(recvBuf1, 1024*5, MAXWAITTIME))!= ANDROID_PORT_READ_FAIL)
		{
			analogSerialManager.close();
			if(!new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s写串口数据失败ret = %d", Tools.getLineInfo(),TESTAPI,ret1))
				return;
		}
		
		if((ret = analogSerialManager.close())!=ANDROID_OK)
		{
			analogSerialManager.close();
			if(!new Gui(getActivity(), handler).cls_show_msg1(2, SERIAL,"line %d:%s串口关闭失败ret = %d", Tools.getLineInfo(),TESTAPI,ret))
				return;
		}
*/
		// case5:串口非阻塞情况下，测试读超时时间（USB没有阻塞）
		if((ret = analogSerialManager.open()) == -1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s串口打开失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			analogSerialManager.close();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		Log.e("5", "5");
		oldTime = System.currentTimeMillis();
		if((ret = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1)))!=ANDROID_PORT_READ_FAIL)
		{
			analogSerialManager.close();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		float endtime=System.currentTimeMillis()-oldTime;
		if(endtime-WUCHASEC>=MAXWAITTIME*1000)// 使用ms值对比更精准
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%f)", Tools.getLineInfo(),TESTITEM,endtime);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		analogSerialManager = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
	}

	@Override
	public void onTestDown() {
		if(analogSerialManager!=null)
			analogSerialManager.close();
		gui = null;
		analogSerialManager = null;
	}
}
