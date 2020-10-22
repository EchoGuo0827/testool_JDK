package com.example.highplattest.usbcomm;

import java.util.Arrays;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;
import android.util.Log;

/************************************************************************
 * 
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20140619
 * directory 		: 
 * description 		: 测试Usb模拟串口模块的setconfig和open、close
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20140619	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm1 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = UsbComm1.class.getSimpleName();
	private final String TESTITEM = "虚拟串口setconfig和open、close";
	private final int BPS_NUM = 10;
	private final int DATABIT_NUM = 2;/**不测数据位5和6的情况，只剩下7和8两种情况*/
	private final int CHECKBIT_NUM = 3;
	private final int STOPBIT_NUM = 2;
	private final int IR_EN = 2;
	private final int BLOCK_EN = 2;
	private final int MAX_SIZE = 1024*4;
	private final int MAXWAITTIME = 30;
	private AnalogSerialManager analogSerialManager = null;
	int retFd=0;
	int ret = -1;
	long startTime;
	float time;
	int ret1 = -1;
	long otherTime = 0;
//	boolean threadStart = false;
	private Gui gui = new Gui(myactivity, handler);
	public void usbcomm1()
	{
		String funcName="usbcomm1";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
			
		int ret2=-1,ret3=-1,ret4=-1,ret5=-1,ret6=-1,ret7=-1,ret8 = 0;
		// 设备节点描述符
		int fd=-1;
		byte[] sendBuf = new byte[MAX_SIZE];
		final byte[] recvBuf = new byte[MAX_SIZE];
		String[] dataBit = { "8", "7"/*, "6", "5"*/ };/**与开发沟通过统一测7和8即可，单片机才有5、6、7，芯片都是32位和64位，不需要测试5、6 by 巫丙亮*/
		String[] checkBit = { "N", "S","O", "E" };
		String[] stopBit = { "1", "2" };
		String[] irEn = { "I", "Y","N" };
		String[] blockEn = { "B", "Y","N" };
		byte[] testbuf = new byte[0];
		
		/*process body*/
		
		gui.cls_show_msg1(2, TESTITEM+"测试中，请别开启自动发送...");
		// 测试前置，关闭串口设备
		analogSerialManager.close();
		
		// case1:流程异常，未打开串口，设置串口参数应应该返回-12|-13,fd错
		ret = analogSerialManager.setconfig(115200, 0, "8N1NB".getBytes());
		if (ret != ANDROID_FD_ERR1&& ret != ANDROID_FD_ERR2) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 打开串口
		if ((fd = analogSerialManager.open()) <0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,fd);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		Log.e("fd", fd+"");
		//开发意见：null 返回-6 (<参数非法*/)  0返回-1(<错误参数*/) read函数如此  setconfig函数 错误参数返回-14
		// case2:传入非法参数，应该返回ANDROID_PORT_PARA_REE(-2)；
		// 波特率不对未修改
		// (ret = analogSerialManager.setconfig(400, 0, "8N1NB".getBytes())) != ANDROID_PORT_PARA_REE
		if ((ret = analogSerialManager.setconfig(400, 0, "8N1NB".getBytes())) != NDK_OK
				| (ret1 = analogSerialManager.setconfig(115200, 0, null)) != NDK_ERR_PARA
				| (ret2 = analogSerialManager.setconfig(115200, 0, "9N1NB".getBytes())) != NDK_OK
				| (ret3 = analogSerialManager.setconfig(115200, 0, "8A1NB".getBytes())) != NDK_OK
				| (ret4 = analogSerialManager.setconfig(115200, 0, "8N3NB".getBytes())) != NDK_OK
				| (ret5 = analogSerialManager .setconfig(115200, 0, "8N1AB".getBytes())) != NDK_OK
				| (ret6 = analogSerialManager.setconfig(115200, 0, "8N1NA".getBytes())) != NDK_OK
				| (ret7 = analogSerialManager.setconfig(115200, 0, "8N1N".getBytes())) != NDK_OK
				| (ret8 = analogSerialManager.setconfig(115200, 0, testbuf))!=-14)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d,%d,%d,%d,%d,%d,%d,%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2,ret3,ret4,ret5,ret6,ret7,ret8);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		Log.d("return code", ret +","+ret1+","+ret2+","+ret3+","+ret4+","+ret5+","+ret6+","+ret7+","+ret8);
		String message = "请确保POS和PC已通过USB线连接，并开启PC端的AccessPort工具后点[确认]继续";
		gui.cls_show_msg(message);
		// case3:进行跟PC端收发数据测试（阻塞和非阻塞）
		// case4:多次设置串口参数，设置结果为最后一次的串口参数
		// 自发自收（非阻塞）
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}	
		for (int i = 0; i < sendBuf.length; i++) 
		{
			sendBuf[i] = (byte) (Math.random() * 256);
		}
		
		if((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		
		Arrays.fill(recvBuf, (byte) 0);
		if((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		message = "请清空发送 接收缓冲区的数据,后点[确认]继续";
		gui.cls_show_msg(message);
		// 自发自收（阻塞）
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NB".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}	
		
		if((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		
		// 阻塞卡死要通过子线程结束
		Log.d("read", "start read data");
		
		Arrays.fill(recvBuf, (byte) 0);
		
		ret1 = -1;
		
		new Thread()
		{
			public void run() 
			{
				otherTime = System.currentTimeMillis();
				if((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
				{
					synchronized (g_lock) {
						g_lock.notify();
					}
					gui.cls_show_msg1_record(CLASS_NAME,"usbcomm1",gKeepTimeErr, "line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
					if(GlobalVariable.isContinue==false)
						return;
				}
				synchronized (g_lock) {
					g_lock.notify();
				}
			};
		}.start();
		synchronized (g_lock) {
			try {
				g_lock.wait(MAXWAITTIME*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(Tools.getStopTime(otherTime)>=MAXWAITTIME) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败超时失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		message = "请清空发送-接收缓冲区的数据,后点[确认]继续";
		gui.cls_show_msg(message);
		
		// case5:串口无数据，分别设置为阻塞和非阻塞的模式，应该返回ANDROID_PORT_READ_FAIL(-1)
		// 设置非阻塞
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((ret = analogSerialManager.read(recvBuf, MAX_SIZE, 1))!= ANDROID_PORT_READ_FAIL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}

//		// 设置阻塞（巫丙亮返回USB串口没有阻塞模式，该测试点去除）
//		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NB".getBytes()))!= ANDROID_OK)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		Thread thread = new Thread(new Runnable() 
//		{
//			@Override
//			public void run() 
//			{
//				startTime = System.currentTimeMillis();
//				retFd = analogSerialManager.read(recvBuf, MAX_SIZE, 5);
//				synchronized (g_lock) {
//					g_lock.notify();
//				}
//			}
//		});
//		thread.start();
//		
//		synchronized (g_lock) {
//			try {
//				g_lock.wait(5*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
//		// 超时时间到还未结束读就是阻塞
//		if(Tools.getStopTime(startTime)>=5)
//		{
//			thread = null;
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s阻塞读串口失败", Tools.getLineInfo(),TESTITEM);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		if ((ret = analogSerialManager.close()) != ANDROID_OK) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}

		// case6:测试各种波特率、数据位、停止位、校验位串口的初始化
		for (int i = 0; i < BPS_NUM; i++) {
			for (int j = 0; j < DATABIT_NUM; j++) {
				for (int k = 0; k < CHECKBIT_NUM; k++) {
					for (int n = 0; n < STOPBIT_NUM; n++) {
						for (int l = 0; l < IR_EN; l++) {
							for (int m = 0; m < BLOCK_EN; m++) {
								String szTemp = dataBit[j] + checkBit[k]
										+ stopBit[n] + irEn[l] + blockEn[m];
								if((fd=analogSerialManager.open())==-1)
								{
									gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,fd);
									if(GlobalVariable.isContinue==false)
										return;
								}
								if((ret=analogSerialManager.setconfig(BpsBean.bpsValue, 0,
										szTemp.getBytes()) )!= ANDROID_OK)
								{
									gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
									if(GlobalVariable.isContinue==false)
										return;
								}
								if((ret=analogSerialManager.close())!=ANDROID_OK)
								{
									
									gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
									if(GlobalVariable.isContinue==false)
										return;
								}
							}
						}
					}
				}
			}
		}
		gui.cls_show_msg1(2, "初始化串口成功");
		
		 //case7:关闭串口，读写都应返回失败
		if((ret=analogSerialManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId+1)))>0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1)))>0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	
	
	
	
	@Override
	public void onTestUp() 
	{
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

