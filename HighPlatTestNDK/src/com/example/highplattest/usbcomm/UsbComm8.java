package com.example.highplattest.usbcomm;

import java.util.Arrays;
import android.newland.AnalogSerialManager;
import android.os.SystemClock;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm8.java 
 * Author 			: zhushaoh
 * version 			: 
 * DATE 			: 20190602
 * directory 		: 
 * description 		: 测试Usb模拟串口模块数据回显功能
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhushaoh		   20190602	 		created
 *					变更记录					变更时间			变更人
 *					修改提示语，添加退出死循环判断	20200727		陈丁
 *					
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class UsbComm8 extends UnitFragment  {
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = UsbComm8.class.getSimpleName();
	public final int MAX_SIZE = 1024 * 5;
	public final int SERIAL_SIZE = 1024*4;
	public final int MAXWAITTIME = 30;
	private AnalogSerialManager analogSerialManager = null;
	private final String TESTITEM = "usb通信(数据回显)";
	private Gui gui = new Gui(myactivity, handler);
	
	public void usbcomm8()
	{
		String funcName="usbcomm8";
		int ret = -1, ret1 = -1,ret2 = -1;
		String[] para={"8N1NB","8N1NN"};
		byte[] sendBuf = new byte[MAX_SIZE];
		byte[] recvBuf = new byte[MAX_SIZE];
		byte[] testbuf = new byte[0];
		
		String message = "请确保POS和PC已通过USB线连接,并开启PC端串口工具并打开对应的串口,后点[确认]继续进入前置异常测试";
		gui.cls_show_msg(message);
		
		// case1:流程异常，对未打开的串口进行写操作，应返回相应错误
		analogSerialManager.close();
		Arrays.fill(sendBuf, (byte) 0);
		Arrays.fill(recvBuf, (byte) 0);
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}	
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) > 0)
		{	analogSerialManager.read(recvBuf, MAX_SIZE, 0);
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case2:参数异常，打开串口，传入非法参数NULL，非法长度
		if (((ret = analogSerialManager.open()) == -1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if(((ret = analogSerialManager.write(null, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) ==0)|| ((ret1 = analogSerialManager.write(recvBuf, 0, MAXWAITTIME/(BpsBean.bpsId+1))) == 0)||(ret2 = analogSerialManager.write(testbuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1)))==0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s写失败(%d,%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		 // case3:串口非阻塞情况下   timeoutSec > 0 && 超时时间测试
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if (((ret = analogSerialManager.write(sendBuf, SERIAL_SIZE,MAXWAITTIME/(BpsBean.bpsId+1))) != SERIAL_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		SystemClock.sleep(200);
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if (((ret1 = analogSerialManager.write(sendBuf, SERIAL_SIZE,0)) != SERIAL_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		SystemClock.sleep(200);
		
		// case4:写完数据立刻关闭串口，不应有异常
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if ((ret = analogSerialManager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		} 
	
		//case5 数据回显

		gui.cls_show_msg("%s测试中...请在pc端发送数据..发送数据大小为5K数据不足会用0填充,只需要对比PC串口工具接收buf是否包含发送的数据即可,按任意键继续", "数据回显");
		analogSerialManager.open();
		int count=0;
		while(true)
		{
			count++;
			Arrays.fill(recvBuf, (byte) 0);
			//回发数据
			if((ret=(analogSerialManager.read(recvBuf, MAX_SIZE, 0)))>0)
				{
				if ((ret1 = analogSerialManager.write(recvBuf, MAX_SIZE, 0)) != MAX_SIZE) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
			if (gui.cls_show_msg("第%d次数据回显测试完毕，是否退出该循环,确定[是]，其他[否],若继续测试请继续再PC端发送数据",count)==ENABLE) {
				break;
			}
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
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
