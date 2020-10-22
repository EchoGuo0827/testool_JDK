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
 * file name 		: PaymentPort4.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141127 
 * directory 		: 
 * description 		: 测试Android系统和支付模块通信串口read
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20141127	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort4 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = PaymentPort4.class.getSimpleName();
	public final int MAX_SIZE = 1024;
	public final int MAXWAITTIME = 30;
	private final float WUCHASEC = 0.03f;
	private NlManager nlManager = null;
	String TESTITEM = "通信串口Read";
	private Gui gui = new Gui(myactivity, handler);
	
	public void paymentport4()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName="paymentport4";
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		int ret = -1, ret1 = -1, ret2 = -1;
		float tmp=0.0f;
		boolean fd = false;
		String[] para={"8N1NB","8N1NN"};
		byte[] recvBuf = new byte[MAX_SIZE];
		//对1k数据初始化,并计算LRC值
		byte[] sendBuf =new byte[MAX_SIZE - 2];
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

		
		/* process body */
		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		// 测试前置，关闭串口
		if ((ret = nlManager.disconnect()) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口连接失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case1:对未打开的串口进行读操作，应返回相应错误(被其他应用中的支付服务抢占)
		Arrays.fill(recvBuf, (byte) 0);
		if (((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != PAYMENT_PORT_BE_OTHER_OCCUPY)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口异常测试失败（预期=%d,实际=%d）", Tools.getLineInfo(),PAYMENT_PORT_BE_OTHER_OCCUPY,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("请短接PINPAD串口的23脚，完成点【确认】继续");
		if((fd = nlManager.connect(false))!=true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d：连接串口测试失败(%s)", Tools.getLineInfo(),fd);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = nlManager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2:阻塞情况下参数测试
		//设置成阻塞情况下,参数timeoutSec = 0 测试不通过,预期设置成功实际读失败
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(预期=%d，实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//阻塞情况下,异常测试
		if((ret = nlManager.read(null, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) > 0 || (ret2 = nlManager.read(recvBuf, 0, MAXWAITTIME/(BpsBean.bpsId+1))) > 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(%d,%d)", Tools.getLineInfo(),ret,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 串口阻塞,timeout=0
		if ((ret = nlManager.read(recvBuf, recvBuf.length, 0)) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//阻塞情况下,参数 timeoutSec > 0  测试
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3:非阻塞情况下参数测试
		if((ret = nlManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//非阻塞情况下异常测试
		if((ret = nlManager.read(null, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) > 0 
				|| (ret2 = nlManager.read(recvBuf, 0, MAXWAITTIME)) > 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(%d,%d)", Tools.getLineInfo(),ret,ret2);
			if(!GlobalVariable.isContinue)
				return;
		}
		//timeoutSec = 0 测试不通过 (读失败)
		if ((ret = nlManager.read(recvBuf, recvBuf.length, 0)) <= 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据成功(ret = %d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		//非阻塞情况下 读超时测试
		Arrays.fill(recvBuf, (byte) 0);
		long startTime=System.currentTimeMillis();
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != ANDROID_PORT_READ_FAIL
				||(tmp=Tools.getStopTime(startTime)-MAXWAITTIME/(BpsBean.bpsId+1)) > WUCHASEC) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读超时时间测试失败(%d,"+tmp+")", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4:大数据读取（1K）--最大只支持1k数据读写  (timeoutSec > 0)
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(预期=%d,实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(预期=%d,实际=%d)", Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret-2) != sendBuf.length ||!Tools.byteCompare( sendBuf, recvBuf, 10,sendBuf.length - 1, 12,recvBuf.length - 1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:串口读写数据校验失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5:测试读完数据马上关闭串口，不应该出现异常
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d：写数据到串口失败(预期=%d，实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(ret = %d)", Tools.getLineInfo(),ret);
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
