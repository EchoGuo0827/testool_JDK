package com.example.highplattest.paymentport;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import java.util.Arrays;

import android.newland.NlManager;

/************************************************************************
 * 
 * module 			: Android系统和支付模块通信串口
 * file name 		: paymentport3.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141127 
 * directory 		: 
 * description 		: 测试Android系统和支付模块通信串口的setconfig和disconnect
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  huangjianb		20141127 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort3 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = PaymentPort3.class.getSimpleName();
	private final int BPS_NUM = 10;
	private final int DATABIT_NUM = 4;
	private final int CHECKBIT_NUM = 3;
	private final int STOPBIT_NUM = 2;
	private final int IR_EN = 2;
	private final int BLOCK_EN = 2;
	private final int MAX_SIZE = 1024;
	private final int MAXWAITTIME = 30;
	private NlManager nlManager = null;
	private boolean isTrue = true;
	private int retFd = -1;
	String TESTITEM = "外置串口setconfig和disconnect";
	private Gui gui = new Gui(myactivity, handler);
	
	public void paymentport3()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName="paymentport3";
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = -1;
		int ret1 = -1, ret2 = -1, ret3 = -1, ret4 = -1, ret5 = -1, ret6 = -1, ret7 = -1;
		boolean tmp = false;
		int pszAttr = BpsBean.bpsValue;
		String[] dataBit = { "8", "7", "6", "5" };
		String[] checkBit = { "N", "S", "O", "E" };
		String[] stopBit = { "1", "2" };
		String[] irEn = { "I", "Y", "N" };
		String[] blockEn = { "B", "Y", "N" };
		
		//对1k数据初始化,并计算LRC值
		byte[] sendBuf =new byte[MAX_SIZE - 2];
		final byte[] recvBuf = new byte[MAX_SIZE];
		sendBuf[0] = 0x02; sendBuf[1] = 0x10; sendBuf[2] = 0x17; sendBuf[3] = 0x1D;
		sendBuf[4] = 0x0A; sendBuf[5] = 0x2F; sendBuf[6] = 0x01; sendBuf[7] = 0x10;
		sendBuf[8] = 0x11; sendBuf[sendBuf.length-2] = 0x03; sendBuf[sendBuf.length-1] = 0x00 ;
		for (int i = 0; i < sendBuf.length - 11; i++) 
		{
			sendBuf[9 + i] = (byte) (Math.random() * 256);
		}
		for (int i = 0; i < sendBuf.length - 2; i++) 
		{
			sendBuf[sendBuf.length - 1] = (byte) (sendBuf[sendBuf.length - 1] ^ sendBuf[1 + i]);
		}

		/* Process body */
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		// 测试前置，关闭串口设备
		nlManager.disconnect();

		// case1:流程异常，未打开串口，设置串口参数应失败,返回被其他应用中的支付服务抢占
		if ((ret = nlManager.setconfig(115200, 0, "8N1NB".getBytes())) != PAYMENT_PORT_BE_OTHER_OCCUPY) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(ret = %d)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
			
		}
		// 打开串口
		if ((tmp = nlManager.connect(false)) != true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口测试失败(%s)",Tools.getLineInfo(),tmp);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case2:传入非法参数，由于接口存在问题，测试失败，参数设置错误的情况预期应该设置失败，实际还是返回成功
		if ((ret = nlManager.setconfig(400, 0, "8N1NB".getBytes())) != ANDROID_PORT_PARA_REE
				| (ret1 = nlManager.setconfig(115200, 0, null)) != ANDROID_PORT_PARA_REE
				|| (ret2 = nlManager.setconfig(115200, 0, "9N1NB".getBytes())) != ANDROID_PORT_PARA_REE
				|| (ret3 = nlManager.setconfig(115200, 0, "8A1NB".getBytes())) != ANDROID_PORT_PARA_REE
				|| (ret4 = nlManager.setconfig(115200, 0, "8N3NB".getBytes())) != ANDROID_PORT_PARA_REE
				|| (ret5 = nlManager.setconfig(115200, 0, "8N1AB".getBytes())) != ANDROID_PORT_PARA_REE
				|| (ret6 = nlManager.setconfig(115200, 0, "8N1NA".getBytes())) != ANDROID_PORT_PARA_REE
				|| (ret7 = nlManager.setconfig(115200, 0, "8N1N".getBytes())) != ANDROID_PORT_PARA_REE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(%d，%d,%d,%d,%d，%d,%d,%d)",Tools.getLineInfo(),ret,ret1,ret2,ret3,ret4,ret5,ret6,ret7);
			if(!GlobalVariable.isContinue)
				return;
		} 
		
		gui.cls_show_msg("请短接PINPAD串口的23脚,完成点[确认]继续");
		// case3:打开串口并短接PINPAD串口的23脚，进行自发自收的测试（阻塞和非阻塞）
		// case4:没关闭串口的时候多次设置串口参数，设置结果应为最后一次的串口参数
		// 非阻塞情况下自发自收
		if ((ret = nlManager.setconfig(pszAttr, 0, "8N1NN".getBytes())) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(ret = %d)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//对串口进行写操作
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME / (BpsBean.bpsId + 1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口测试失败(预期=%d，实际=%d)",Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//清空接收缓存区，读操作
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据测试失败(预期=%d，实际=%d)",Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断是否收发一致
		if ((ret-2) != sendBuf.length ||!Tools.byteCompare( sendBuf, recvBuf, 10,sendBuf.length - 1, 12,recvBuf.length - 1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:比较串口读写数据校验失败",Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}

		// 阻塞情况下自发自收
		if ((ret = nlManager.setconfig(pszAttr, 0, "8N1NB".getBytes())) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(ret = %d)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口测试失败(预期=%d，实际=%d)",Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//清空接收缓存区，读操作
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != recvBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d读串口数据测试失败(预期=%d，实际=%d)",Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断是否收发一致
		if ((ret-2) != sendBuf.length ||!Tools.byteCompare( sendBuf, recvBuf, 10,sendBuf.length - 1, 12,recvBuf.length - 1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:比较串口读写数据校验失败",Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}

		// case5:串口无数据，分别设置为阻塞和非阻塞的模式读是否正常
		// 设置非阻塞
		if ((ret = nlManager.setconfig(115200, 0, "8N1NN".getBytes())) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(ret = %d)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if ((ret = nlManager.read(recvBuf, MAX_SIZE, 1)) != ANDROID_PORT_READ_FAIL) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据测试失败(预期=%d，实际=%d)",Tools.getLineInfo(),ANDROID_PORT_READ_FAIL,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置成阻塞读不到数据的话，K21那边会卡死，无法进行下个用例的测试
		// 设置阻塞
		if ((ret = nlManager.setconfig(115200, 0, "8N1NB".getBytes())) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(ret = %d)",Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Thread thread = new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				while (isTrue) 
				{
					retFd = nlManager.read(recvBuf, MAX_SIZE, 1);
				}
			}
		});
		thread.start();

		long startTime = System.currentTimeMillis();
		while (isTrue)
		{
			long endTime = System.currentTimeMillis();
			if ((endTime - startTime) / 1000 == 5) 
			{
				if (retFd != ANDROID_PORT_READ_FAIL) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d读串口数据测试失败(预期=%d，实际=%d)",Tools.getLineInfo(),ANDROID_PORT_READ_FAIL,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				isTrue = false;
				thread.interrupt();
				thread = null;
			}
		}
		
		if ((ret = nlManager.disconnect()) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((tmp = nlManager.connect(false)) != true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口失败(ret = %s)", Tools.getLineInfo(),tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case6:测试各种波特率、数据位、停止位、校验位串口的初始化
		for (int i = 0; i < BPS_NUM; i++) 
		{
			for (int j = 0; j < DATABIT_NUM; j++)
			{
				for (int k = 0; k < CHECKBIT_NUM; k++) 
				{
					for (int n = 0; n < STOPBIT_NUM; n++) 
					{
						for (int l = 0; l < IR_EN; l++)
						{
							for (int m = 0; m < BLOCK_EN; m++) 
							{
								String szTemp = dataBit[j] + checkBit[k]
										+ stopBit[n] + irEn[l] + blockEn[m];
								if ((ret = nlManager.setconfig(pszAttr, 0,szTemp.getBytes())) != ANDROID_OK) 
								{
									gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(ret = %d)",Tools.getLineInfo(),ret);
									if(!GlobalVariable.isContinue)
										return;
								}
							}
						}
					}
				}
			}
		}
		if((ret = nlManager.disconnect()) != ANDROID_OK)
		{
			gui.cls_show_msg1(gKeepTimeErr, "line %d:断开串口连接失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		 //case7:关闭串口，读写都应返回失败
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) > 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) > 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读串口数据测试失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(nlManager!=null)
			nlManager.disconnect();
		gui = null;
		nlManager = null;
	}
}
