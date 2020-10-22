package com.example.highplattest.androidport;

import java.util.Arrays;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;

/************************************************************************
 * 
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141010 
 * directory 		: 
 * description 		: 测试Android系统与外置串口通信模块的setconfig和open、close
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20141010	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class AndroidPort1 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = AndroidPort1.class.getSimpleName();
	private String TESTITEM = "外置串口setconfig和open、close";
	private final int BPS_NUM = 10;
	private final int DATABIT_NUM = 4;
	private final int CHECKBIT_NUM = 3;
	private final int STOPBIT_NUM = 2;
	private final int IR_EN = 2;
	private final int BLOCK_EN = 2;
	private final int MAX_SIZE = 1024*2;
	private final int MAXWAITTIME = 30;
	private NLUART3Manager uart3Manager=null;
	private Object lockObj = new Object();
	int retFd=-1;
	int ret = -1;
	private Gui gui = new Gui(myactivity, handler);
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	
	public void androidport1()
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232)==false&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持物理串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		// X5设备有新的RS232和旧的RS232,需要让测试人员进行一次选择
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad))
		{
			int nkeyIn = gui.cls_show_msg("是否要测试PinPad串口,是[确认],否[取消]");
			isNewRs232 = nkeyIn==ENTER?true:false;
		}
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		int ret1=-1,ret2=-1,ret3=-1,ret4=-1,ret5=-1,ret6=-1,ret7=-1;
		
		// 设备节点描述符
		int fd=-1;
		byte[] sendBuf = new byte[MAX_SIZE];
		final byte[] recvBuf = new byte[MAX_SIZE];
		String[] dataBit = { "8", "7", "6", "5" };
		String[] checkBit = { "N", "S","O", "E" };
		String[] stopBit = { "1", "2" };
		String[] irEn = { "I", "Y","N" };
		String[] blockEn = { "B", "Y","N" };
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// 测试前置，关闭串口设备
		uart3Manager.close();
		
		// case1:流程异常，未打开串口，设置串口参数应应该返回-12|-13
		if(gui.cls_show_msg1(gScreenTime, "流程异常,未打开串口,设置串口参数应应该返回-12|-13,[取消]退出测试")==ESC)
			return;
		ret = uart3Manager.setconfig(115200, 0, "8N1NB".getBytes());
		if (ret != ANDROID_FD_ERR1&& ret != ANDROID_FD_ERR2) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s流程异常(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 打开串口
		fd = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if (fd == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,fd);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:传入非法参数，应该返回ANDROID_PORT_PARA_REE(-2)；测试失败，参数设置错误的情况还是返回成功
		if(gui.cls_show_msg1(gScreenTime, "传入非法参数,应该返回ANDROID_PORT_PARA_REE(-2),[取消]退出测试")==ESC)
			return;
		//开发反馈  参数设置错误 会自动设置为默认115200 8N1NN  始终返回正确 
		//开发意见：null 返回-6 (<参数非法*/)  0返回-1(<错误参数*/) read函数如此  setconfig函数 错误参数返回-14
		if ((ret = uart3Manager.setconfig(400, 0, "8N1NN".getBytes())) != NDK_OK
				| (ret1 = uart3Manager.setconfig(115200, 0, null)) != -6
				| (ret2 = uart3Manager.setconfig(115200, 0, "9N1NB".getBytes())) != NDK_OK
				| (ret3 = uart3Manager.setconfig(115200, 0, "8A1NB".getBytes())) != NDK_OK
				| (ret4 = uart3Manager.setconfig(115200, 0, "8N3NB".getBytes())) != NDK_OK
				| (ret5 = uart3Manager.setconfig(115200, 0, "8N1AB".getBytes())) != NDK_OK
				| (ret6 = uart3Manager.setconfig(115200, 0, "8N1NA".getBytes())) != NDK_OK
				| (ret7 = uart3Manager.setconfig(115200, 0, "8N1N".getBytes())) != NDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,gScreenTime,"line %d:%s异常参数测试失败(ret=%d,ret1=%d,ret2=%d,ret3=%d,ret4=%d,ret5=%d,ret6=%d,,ret7=%d)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2,ret3,ret4,ret5,ret6,ret7);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("请短接RS232串口的23脚，点任意键继续");
		// case3:进行自发自收的测试（现在串口只有阻塞方式）
		// case4:多次设置串口参数，设置结果为最后一次的串口参数
		if(gui.cls_show_msg1(gScreenTime, "进行自发自收的测试,且多次设置串口参数,设置结果为最后一次的串口参数,[取消]退出测试")==ESC)
			return;
		// 自发自收（非阻塞）
		if((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}	
		for (int i = 0; i < sendBuf.length; i++) 
		{
			sendBuf[i] = (byte) (Math.random() * 256);
		}
		
		if((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME, funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 清空接收缓存区
		// MAXWAITTIME/(BpsSetting.bpsId+1)
		long a1 = System.currentTimeMillis();
		Arrays.fill(recvBuf, (byte) 0);
		if((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName, gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e(CLASS_NAME+",androidport1===read时长："+Tools.getStopTime(a1));
		if(!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 自发自收（阻塞），
		if((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0, "8N1NB".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}	
		
		if((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 清空接收缓存区
		Arrays.fill(recvBuf, (byte) 0);
		long a2 = System.currentTimeMillis();
		if((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("read时长："+Tools.getStopTime(a2));
		if(!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case5:串口无数据，分别设置为阻塞和非阻塞的模式，应该返回ANDROID_PORT_READ_FAIL(-1)
		if(gui.cls_show_msg1(gScreenTime, "串口无数据,分别设置为阻塞和非阻塞的模式,应该返回ANDROID_PORT_READ_FAIL(-1),[取消]退出测试")==ESC)
			return;
		LoggerUtil.e("设置为非阻塞");
		// 设置非阻塞
		if((ret = uart3Manager.setconfig(115200, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if((ret = uart3Manager.read(recvBuf, MAX_SIZE, 1))!= ANDROID_PORT_READ_FAIL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("设置为阻塞");//设置为阻塞会卡死，手动30s后断开
		// 设置阻塞
		if((ret = uart3Manager.setconfig(115200, 0, "8N1NB".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//开发回复：A9 阻塞读失败都返回-1 by 20200323 chendin
		int mPortBlockFail = GlobalVariable.gCurPlatVer==Platform_Ver.A9?-1:RS232_TIMEOUT;
		if((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, 1))!=mPortBlockFail)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
//		if((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, 1))!=RS232_TIMEOUT)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}

		Thread thread = new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				retFd = uart3Manager.read(recvBuf, MAX_SIZE, 1);
				synchronized (lockObj) {
					lockObj.notify();
				}
			}
		});
		thread.start();
		
		synchronized (lockObj) {
			try {
				lockObj.wait(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(retFd!=ANDROID_PORT_READ_FAIL)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读超时时间测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,retFd);
			if(!GlobalVariable.isContinue)
				return;
		}
		thread = null;
				
		  
		if ((ret = uart3Manager.close()) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case6:测试各种波特率、数据位、停止位、校验位串口的初始化
		if (gui.cls_show_msg1(gScreenTime, "测试各种波特率、数据位、停止位、校验位串口的初始化,[取消]退出测试") == ESC) 
			return;
		for (int i = 0; i < BPS_NUM; i++) {
			for (int j = 0; j < DATABIT_NUM; j++) {
				for (int k = 0; k < CHECKBIT_NUM; k++) {
					for (int n = 0; n < STOPBIT_NUM; n++) {
						for (int l = 0; l < IR_EN; l++) {
							for (int m = 0; m < BLOCK_EN; m++) {
								String szTemp = dataBit[j] + checkBit[k] + stopBit[n] + irEn[l] + blockEn[m];
								fd = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
								if(fd==-1)
								{
									gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,fd);
									if(!GlobalVariable.isContinue)
										return;
								}
								if((ret=uart3Manager.setconfig(BpsBean.bpsValue, 0,
										szTemp.getBytes()) )!= ANDROID_OK)
								{
									gui.cls_show_msg1_record(CLASS_NAME,funcName, gKeepTimeErr,"line %d:%s设置串口参数失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
									if(!GlobalVariable.isContinue)
										return;
								}
								if((ret=uart3Manager.close())!=ANDROID_OK)
								{
									gui.cls_show_msg1_record(CLASS_NAME,funcName, gKeepTimeErr,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
									if(!GlobalVariable.isContinue)
										return;
								}
							}
						}
					}
				}
			}
		}
		
		 //case7:关闭串口，读写都应返回失败
		if (gui.cls_show_msg1(gScreenTime, "关闭串口,读写都应返回失败,[取消]退出测试") == ESC) 
			return;
		if((ret=uart3Manager.write(sendBuf, sendBuf.length, MAXWAITTIME))>0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = uart3Manager.read(recvBuf, recvBuf.length, MAXWAITTIME))>0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}


	@Override
	public void onTestUp() 
	{
		
	}


	@Override
	public void onTestDown() {
		if(uart3Manager!=null)
			uart3Manager.close();
		uart3Manager = null;
		gui = null;
	}
}
