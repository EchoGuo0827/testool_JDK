package com.example.highplattest.androidport;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import java.util.Arrays;
import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;

/************************************************************************
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort3.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141031
 * directory 		: 
 * description 		: 测试Android系统与外置串口通信模块的write
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20141031	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class AndroidPort3 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME=AndroidPort3.class.getSimpleName();
	public final int MAX_SIZE = 1024 * 3;
	public final int MAXWAITTIME = 30;
	private final float WUCHASEC = 0.03f;
	NLUART3Manager uart3Manager=null;
	String TESTITEM = "外置串口Write";
	private Gui gui = new Gui(myactivity, handler);
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	
	public void androidport3()
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
		int ret = -1, ret1 = -1;
		String[] para={"8N1NB","8N1NN"};
		float tmp=0.0f;
		String message;
		byte[] sendBuf = new byte[MAX_SIZE];
		byte[] recvBuf = new byte[MAX_SIZE];
		byte[] recv1Buf = new byte[MAX_SIZE - 2048];
		

		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// 测试前置，关闭串口
		uart3Manager.close();
		
		// case1:流程异常，对未打开的串口进行写操作，应返回相应错误
		if(gui.cls_show_msg1(gScreenTime, "流程异常,对未打开的串口进行写操作,应返回相应错误,[取消]退出测试")==ESC)
			return;
		Arrays.fill(sendBuf, (byte) 0);
		Arrays.fill(recvBuf, (byte) 0);
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}	
		if ((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME/ (BpsBean.bpsId + 1))) > 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s流程异常(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		message = "请短接RS232串口的23脚,点任意键继续";
		gui.cls_show_msg(message);
		
		// case2:参数异常，打开串口，传入非法参数NULL，非法长度
		if(gui.cls_show_msg1(gScreenTime, "参数异常,打开串口,传入非法参数NULL,非法长度,[取消]退出测试")==ESC)
			return;
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if ((ret == -1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s串口打开失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// 异常测试    ret重启,ret1通过(阻塞、非阻塞)	
		uart3Manager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());
		if(((ret = uart3Manager.write(null, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) > 0) 
				|| ((ret1 = uart3Manager.write(recvBuf, 0, MAXWAITTIME/(BpsBean.bpsId+1))) > 0))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s参数异常测试失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		uart3Manager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if((ret = uart3Manager.write(null, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) > 0) //导致重启
		{
			 gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s参数异常测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			 if(!GlobalVariable.isContinue)
				 return;
		}
		if((ret = uart3Manager.write(recvBuf, 0, MAXWAITTIME/(BpsBean.bpsId+1))) > 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s参数异常测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}


		// case3 串口阻塞，函数阻塞/非阻塞都应写成功(timeoutSec=0 & timeoutSec>0)
		if(gui.cls_show_msg1(gScreenTime, " 串口阻塞,函数阻塞/非阻塞都应写成功(timeoutSec=0 & timeoutSec>0),[取消]退出测试")==ESC)
			return;
		uart3Manager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());
		if (((ret = uart3Manager.write(sendBuf, MAX_SIZE,MAXWAITTIME/(BpsBean.bpsId+1))) != MAX_SIZE) 
				|| ((ret1 = uart3Manager.write(sendBuf, MAX_SIZE,0)) != MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		//以关闭串口方式来清空串口数据
		if ((ret = uart3Manager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if (ret == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//串口非阻塞情况下   timeoutSec > 0 && 超时时间测试
		uart3Manager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());	
		long startTime=System.currentTimeMillis();
		if(((ret = uart3Manager.write(sendBuf, MAX_SIZE,MAXWAITTIME/(BpsBean.bpsId+1))) != MAX_SIZE)
				||((tmp=Tools.getStopTime(startTime)-MAXWAITTIME/(BpsBean.bpsId+1)) > WUCHASEC))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d,tmp = %f)", Tools.getLineInfo(),TESTITEM,tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		//以关闭串口方式来清空串口数据
		if ((ret = uart3Manager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if (ret  == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//非阻塞情况下   timeoutSec = 0
		if((ret1 = uart3Manager.write(sendBuf, MAX_SIZE,0)) != MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		//以打开关闭串口方式来清空串口数据
		if (uart3Manager.close() != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if (ret == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4 普通数据包发送接收要比较一致(大于2K)
		if(gui.cls_show_msg1(gScreenTime, " 普通数据包发送接收要比较一致(大于2K),[取消]退出测试")==ESC)
			return;
		Arrays.fill(sendBuf, (byte) 0);
		for (int i = 0; i < sendBuf.length; i++)
		{
			sendBuf[i] = (byte) (Math.random() * 256);
		}
		if (((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME/ (BpsBean.bpsId + 1)))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 对3k的数据的读取（先读取2K，再读1k）
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = uart3Manager.read(recvBuf, 2048, MAXWAITTIME/ (BpsBean.bpsId + 1))) <= 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 读取剩余部分(1K)
		Arrays.fill(recv1Buf, (byte) 0);
		if ((ret1 = uart3Manager.read(recv1Buf, MAX_SIZE - 2048, MAXWAITTIME/ (BpsBean.bpsId + 1))) <= 0) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}	
		// 比较收发数据是否一致
		for (int j = 0; j < recv1Buf.length; j++) 
		{
			recvBuf[2048 + j] = recv1Buf[j];
		}
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case5:写完数据立刻关闭串口，不应有异常
		if(gui.cls_show_msg1(gScreenTime, "写完数据立刻关闭串口,不应有异常,[取消]退出测试")==ESC)
			return;
		if ((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = uart3Manager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		
		// case6:关闭串口重打开收发数据正常
		if(gui.cls_show_msg1(gScreenTime, "关闭串口重打开收发数据正常,[取消]退出测试")==ESC)
			return;
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if (((ret = uart3Manager.write(sendBuf, 2048, MAXWAITTIME/ (BpsBean.bpsId + 1)))) != 2048) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if (((ret = uart3Manager.read(recvBuf, 2048, MAXWAITTIME/ (BpsBean.bpsId + 1)))) != 2048) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(uart3Manager!=null)
			uart3Manager.close();
		uart3Manager = null;
		gui = null;
	}
	
}
