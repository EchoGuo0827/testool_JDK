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
 * file name 		: AndroidPort2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141010 
 * directory 		: 
 * description 		: 测试Android系统与外置串口通信模块的read
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20141014	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class AndroidPort2 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME=AndroidPort2.class.getSimpleName();
	private final int MAX_SIZE = 1024*2;
	private final int MAXWAITTIME = 30;
	private NLUART3Manager uart3Manager=null;
	private String TESTITEM = "外置串口read";
	private Gui gui = new Gui(myactivity, handler);
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	
	public void androidport2()
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
		// 实例化接口对象
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		int ret = -1, ret1 = -1,ret2 = -1;
		float WUCHASEC=0.03f;
		//开发回复：A9 阻塞读失败都返回-1 by 20200323 chendin
		int mPortBlockFail = GlobalVariable.gCurPlatVer==Platform_Ver.A9?-1:RS232_TIMEOUT;
		// 设备节点描述符
		int fd=-1;
		byte[] sendBuf = new byte[MAX_SIZE];
		byte[] recvBuf = new byte[MAX_SIZE];
		String[] para={"8N1NB","8N1NN"};

		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		// 测试前置，关闭串口
		uart3Manager.close();
		
		// case1:流程异常，对未打开的串口进行读操作，应返回ANDROID_PORT_FD_ERR(-2)
		if(gui.cls_show_msg1(gScreenTime, "流程异常,对未打开的串口进行读操作,应返回ANDROID_PORT_FD_ERR(-2),[取消]退出测试")==ESC)
			return;
		gui.cls_show_msg1(1, "测试中。。。。该case等待时间较长请耐心等待");
		Arrays.fill(recvBuf, (byte) 0);
		if (((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) != ANDROID_PORT_FD_ERR)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s流程异常(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("请短接RS232串口的23脚,点任意键继续");
		// case2:参数错误，传入非法参数，非法长度，应该返回NDK_PARA_ERR
		if(gui.cls_show_msg1(gScreenTime, "参数错误,传入非法参数,非法长度,应该返回NDK_PARA_ERR,[取消]退出测试")==ESC)
			return;
		
		fd = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if (fd == -1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,fd);
			if(!GlobalVariable.isContinue)
				return;
		}
	
		for (int i = 0; i < para.length; i++) 
		{
			if((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0, para[i].getBytes()))!=ANDROID_OK)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
//			ret = uart3ManagerRead(null, MAX_SIZE, MAXWAITTIME); 导致关机重启
			// 长度为0，应返回-1，设置为阻塞有时候会卡死
			if ( (ret1 = uart3ManagerRead(uart3Manager,recvBuf, 0, MAXWAITTIME/ (BpsBean.bpsId + 1))) != NDK_ERR) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d,i=%d)", Tools.getLineInfo(),TESTITEM,ret1,i);
				if(!GlobalVariable.isContinue)
					return;
			}
			//长度最大+1 一直处于收不全状态 返回超时
			if(para[i]=="8N1NB")
			{
				if ((ret2 = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE + 1,MAXWAITTIME / (BpsBean.bpsId + 1))) != mPortBlockFail) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret2);
					if(!GlobalVariable.isContinue)
						return;
				}
//				if ((ret2 = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE + 1,MAXWAITTIME / (BpsBean.bpsId + 1))) != RS232_TIMEOUT) 
//				{
//					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret2);
//					if(!GlobalVariable.isContinue)
//						return;
//				}
			}
			else
			{
				if ((ret2 = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE + 1,MAXWAITTIME / (BpsBean.bpsId + 1))) != NDK_ERR) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret2);
					if(!GlobalVariable.isContinue)
						return;
				}
			}

		}
		
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		
		// case4:串口阻塞/非阻塞  读：阻塞/非阻塞，自发自收是否正常
		// case6:串口接收限制长度为2K
		// case7:测试读完数据马上关闭串口，不应该出现异常(跑飞或者死机)
		// para[0]:阻塞      para[1]:非阻塞
		if(gui.cls_show_msg1(gScreenTime, "串口阻塞/非阻塞的读、自发自收测试,接收限制长度为2K,读完数据马上关闭串口,不应该出现异常,[取消]退出测试")==ESC)
			return;
		uart3Manager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if ((ret = uart3Manager.write(sendBuf, MAX_SIZE,MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口参数失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		long time1 = System.currentTimeMillis();
		if ((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME))!= MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s读串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("read时长："+Tools.getStopTime(time1));
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据检验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		

		if ((ret = uart3Manager.write(sendBuf, MAX_SIZE,MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		long time2 = System.currentTimeMillis();
		if ((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("read时长："+Tools.getStopTime(time2));
		if (!Tools.memcmp(sendBuf, recvBuf, ret1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		uart3Manager.close();
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
 
		uart3Manager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());
		if ((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		long time3 = System.currentTimeMillis();
		if ((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("read时长："+Tools.getStopTime(time3));
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}

		if ((ret = uart3Manager.write(sendBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		long time4 = System.currentTimeMillis();
		if ((ret1 = uart3Manager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(!GlobalVariable.isContinue)
				return;
		}
		LoggerUtil.e("read时长："+Tools.getStopTime(time4));
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName, gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if((ret = uart3Manager.close())!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}

		// case5:串口非阻塞情况下，测试读超时时间；串口没数据，读会卡死
		if(gui.cls_show_msg1(gScreenTime, "串口阻塞情况下,测试读超时时间,[取消]退出测试")==ESC)
			return;
		gui.cls_show_msg1(1, "测试中。。。。该case等待时间较长请耐心等待");
		ret = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
		if(ret == -1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0, "8N1NB".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		Arrays.fill(recvBuf, (byte) 0);
		long startTime=System.currentTimeMillis();
		//开发回复：A9 阻塞读失败都返回-1 by 20200323 chendin
		if ((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, 10)) != mPortBlockFail)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
//		if ((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, 10)) != RS232_TIMEOUT)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		float endTime = Tools.getStopTime(startTime);
		if(endTime-10>WUCHASEC){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s超时时间错误(%f)", Tools.getLineInfo(),TESTITEM,endTime);
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
