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
 * file name 		: PaymentPort5.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141128
 * directory 		: 
 * description 		: 测试Android系统与外置串口通信模块的write
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20141031	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort5 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = PaymentPort5.class.getSimpleName();
	public final int MAX_SIZE = 1024;
	public final int MAXWAITTIME = 30;
	private final float WUCHASEC = 0.03f;
	private NlManager nlManager = null;
	private String TESTITEM = "通信串口Write函数";
	private Gui gui = new Gui(myactivity, handler);
	
	public void paymentport5()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName="paymentport5";
		// 实例化接口对象
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = -1, ret1 = -1;
		String[] para={"8N1NB","8N1NN"};
		float tmp = 0.0f;
		boolean fd = false;

		
		//对1k数据初始化,并计算LRC值
		final byte[] recvBuf = new byte[MAX_SIZE];
		byte[] sendBuf =new byte[MAX_SIZE - 2];
		sendBuf[0] = 0x02; sendBuf[1] = 0x10; sendBuf[2] = 0x17; sendBuf[3] = 0x1D;
		sendBuf[4] = 0x0A; sendBuf[5] = 0x2F; sendBuf[6] = 0x01; sendBuf[7] = 0x10;
		sendBuf[8] = 0x11; sendBuf[sendBuf.length-2] = 0x03; sendBuf[sendBuf.length-1] = 0x00 ;
		for (int i = 0; i < sendBuf.length - 11; i++) 
		{
			sendBuf[9 + i] = (byte) (Math.random() * 128);
		}
		for (int i = 0; i < sendBuf.length - 2; i++) 
		{
			sendBuf[sendBuf.length - 1] = (byte) (sendBuf[sendBuf.length - 1] ^ sendBuf[1 + i]);
		}
		
		/* process body */
		gui.cls_show_msg1(2,  "%s测试中...",TESTITEM);
		gui.cls_show_msg("请短接PINPAD串口的23脚,完成点[确认]继续");
		
		// 测试前置，关闭串口
		nlManager.disconnect();
		// case1:对未打开的串口进行写操作，应返回相应错误(被其他应用中的支付服务抢占)
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.write(sendBuf, MAX_SIZE, MAXWAITTIME/ (BpsBean.bpsId + 1))) != PAYMENT_PORT_BE_OTHER_OCCUPY)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口成功(预期=%d,实际=%d)",Tools.getLineInfo(), PAYMENT_PORT_BE_OTHER_OCCUPY,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:打开串口，传入非法参数NULL，非法长度
		if((fd = nlManager.connect(false))!=true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口失败(%s)", Tools.getLineInfo(),fd);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 异常测试    ret重启,ret1通过	——测试失败
//		nlManager.setconfig(BpsSetting.bpsValue, 0, para[0].getBytes());
//		 if(((ret = nlManager.write(null, MAX_SIZE, MAXWAITTIME/(BpsSetting.bpsId+1))) > 0) || 
//			((ret1 = nlManager.write(recvBuf, 0, MAXWAITTIME/(BpsSetting.bpsId+1))) > 0))
//		 {
//			postEnd(getLineInfo(), ret + "," + ret1);
//		 }
 

		// case3: 串口阻塞情况下，函数阻塞/非阻塞都应写成功(timeoutSec=0 & timeoutSec>0)
		nlManager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());
		if (((ret = nlManager.write(sendBuf, sendBuf.length,MAXWAITTIME/(BpsBean.bpsId+1))) != sendBuf.length) || ((ret1 = nlManager.write(sendBuf, sendBuf.length,0)) != sendBuf.length))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(%d,%d)", Tools.getLineInfo(),ret,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		//以断开连接串口方式来清空串口数据
		if ((ret = nlManager.disconnect()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		if((fd = nlManager.connect(false))!=true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:串口连接失败(%s)", Tools.getLineInfo(),fd);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//非阻塞情况下   timeoutSec > 0 测试 && 超时时间测试
		nlManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());	
		long startTime=System.currentTimeMillis();
		if(((ret = nlManager.write(sendBuf, sendBuf.length,MAXWAITTIME/(BpsBean.bpsId+1))) != sendBuf.length)||((tmp=Tools.getStopTime(startTime)-MAXWAITTIME/(BpsBean.bpsId+1)) > WUCHASEC))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口超时时间测试失败(%d,"+tmp+")", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//以断开连接串口方式来清空串口数据
		if ((ret = nlManager.disconnect()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口连接失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		if((fd = nlManager.connect(false))!=true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口失败(%s)", Tools.getLineInfo(),fd);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//阻塞情况下   timeoutSec = 0 测试
		if((ret1 = nlManager.write(sendBuf, sendBuf.length,0)) != sendBuf.length)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(预期=%d,实际=%d)", Tools.getLineInfo(),sendBuf.length,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		//以打开关闭串口方式来清空串口数据
		if ((ret = nlManager.disconnect() )!= ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口连接失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		if((fd = nlManager.connect(false))!=true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口失败(%s)", Tools.getLineInfo(),fd);
			if(!GlobalVariable.isContinue)
				return;
		}
		
//		// case4 普通数据包发送接收要比较一致(2K) (不支持2k数据的读写，写2k分次读取后得到的数据收发不一致)
//		//对1k数据初始化,并计算LRC值
//		final byte[] recvBuf2k = new byte[2*MAX_SIZE];
//		byte[] sendBuf2k =new byte[2*MAX_SIZE - 2];
//		sendBuf2k[0] = 0x02; sendBuf2k[1] = 0x20; sendBuf2k[2] = 0x41; sendBuf2k[3] = 0x1D;
//		sendBuf2k[4] = 0x0A; sendBuf2k[5] = 0x2F; sendBuf2k[6] = 0x01; sendBuf2k[7] = 0x20;
//		sendBuf2k[8] = 0x35; sendBuf2k[sendBuf2k.length-2] = 0x03; sendBuf2k[sendBuf2k.length-1] = 0x00 ;
//		for (int i = 0; i < sendBuf2k.length - 11; i++) 
//		{
//			sendBuf2k[9 + i] = (byte) (Math.random() * 128);
//		}
//		for (int i = 0; i < sendBuf2k.length - 2; i++) 
//		{
//			sendBuf2k[sendBuf2k.length - 1] = (byte) (sendBuf2k[sendBuf2k.length - 1] ^ sendBuf2k[1 + i]);
//		}
//		
//		if ((nlManager.write(sendBuf2k,sendBuf2k.length, MAXWAITTIME
//				/ (BpsSetting.bpsId + 1))) != sendBuf2k.length) 
//		{
//			postEnd(getLineInfo(), ret);
//		}
//
//		// 对2k的数据的读取(分两次读取)
//		Arrays.fill(recvBuf2k, (byte) 0);
//		if ((ret = nlManager.read(recvBuf2k, MAX_SIZE, MAXWAITTIME
//				/ (BpsSetting.bpsId + 1))) != MAX_SIZE) 
//		{
//			postEnd(getLineInfo(), ret);
//		}
//		// 读取剩余部分(1K)
//		Arrays.fill(recvBuf, (byte) 0);
//		if ((ret1 = nlManager.read(recvBuf, MAX_SIZE, MAXWAITTIME
//				/ (BpsSetting.bpsId + 1))) != MAX_SIZE) 
//		{
//			postEnd(getLineInfo(), ret);
//		}	
//		// 比较收发数据是否一致
//		for (int j = 0; j < recvBuf.length; j++) 
//		{
//			recvBuf2k[1024 + j] = recvBuf[j];
//		}
//		if ((ret + ret1 - 2) != sendBuf2k.length || 
//				!Tools.byteCompare(sendBuf2k, recvBuf2k, 10,sendBuf2k.length - 1, 12,recvBuf2k.length - 1))
//		{
//			postEnd(getLineInfo(), (ret + ret1 - 2)+","+sendBuf2k.length);
//		} 
		
		// case5写完数据立刻关闭串口，不应有异常(由于没有清空缓冲操作的函数，所以在测试结束前需要读完串口函数)
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(预期=%d,实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(预期=%d,实际=%d)", Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.disconnect()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口连接失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过",TESTITEM);
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
