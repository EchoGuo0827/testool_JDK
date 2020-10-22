package com.example.highplattest.usbcomm;

import java.util.Arrays;

import android.newland.AnalogSerialManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm9.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190130
 * directory 		: 
 * description 		: 打开指定设备节点open(String portname)
 * 					    获取可用设备节点getPortName()
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20150602	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class UsbComm9 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "打开指定设备节点open";
	private final String CLASS_NAME = UsbComm9.class.getSimpleName();
	private final int MAX_SIZE = 1024*2;//由于收发的FIFO限制,数据大小有限制
	private final int MAXWAITTIME = 30;
	private AnalogSerialManager analogSerialManager = null;/**USB串口操作句柄*/
	private Gui gui = new Gui(myactivity, handler);
	
	public void usbcomm9()
	{
		if(GlobalVariable.currentPlatform!=Model_Type.N850)
		{
			gui.cls_show_msg("%s产品不支持该用例测试", GlobalVariable.currentPlatform);
			return;
		}
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,"usbcomm9",gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		// open操作支持两种方式，第一种是打开USB串口，第二种是打开ota线
	
		while(true)
		{
			String title="";
			if (GlobalVariable.currentPlatform==Model_Type.F7) {
				 title="";
			}else {
				 title="2.OTG方式\n3.切换测试\n4.不带参数的open接口切换测试\n5.不带参数的open接口otg测试";
			}
			int nkeyIn = gui.cls_show_msg("%s\n1.UBS串口测试\n%s", TESTITEM,title);
			switch (nkeyIn) {
			case '1':
				usbcomm_test();
				analogSerialManager.close();
				break;
				
			case '2':
				otg_test();
				analogSerialManager.close();
				break;
				
			case '3':
				usb_otg();
				analogSerialManager.close();
				break;
				
			case '4':
				old_otg_test();
				analogSerialManager.close();
				break;
				
			case '5':
				old_usb_otg();
				analogSerialManager.close();
				break;
				
//			case '6':
//				open_usb();
//				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	
	public void usbcomm_test()
	{
		int fd,ret1=0,ret2 = 0,ret3=0,ret4=0;
		byte[] rBuf = new byte[MAX_SIZE];
		byte[] wBuf = new byte[MAX_SIZE];
		// 测试前置，关闭串口
		analogSerialManager.close();
		
		gui.cls_show_msg("请插入USB串口线,操作完毕任意键继续");
		// case2:获取可用的设备节点，将该传入，应可成功打开串口设备
		String portName = analogSerialManager.getPortName();
		if((fd = analogSerialManager.open(portName))<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s打开设备节点失败(fd=%d)", Tools.getLineInfo(),TESTITEM,fd);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case7:这个时候获取的串口值为ttyGs0
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyGS0,一致[确认],不一致[其他]",portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "otg_test", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3:打开指定设备节点成功后，未配置串口参数进行串口收发数据应失败
		gui.cls_show_msg("请将USB串口与PC设备相连,并打开AccessPort串口工具,清空接收缓冲区,波特率设置为:%d,操作完毕任意键继续",BpsBean.bpsValue);
		Arrays.fill(wBuf, (byte)0x33);
		if((ret1 = analogSerialManager.write(wBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)// 未配置波特率参数发送应成功,有默认配置
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4:打开指定设备节点成功后,正确配置串口参数后进行串口收发数据应成功
		if((ret1 = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret1=analogSerialManager.write(wBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(实际=%d,预期=%d)", Tools.getLineInfo(),TESTITEM,ret1,MAX_SIZE);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg("请将AccessPort接收到的数据发送给POS,操作完毕任意键继续");
		if((ret1 = analogSerialManager.read(rBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(实际=%d,预期=%d)", Tools.getLineInfo(),TESTITEM,ret1,MAX_SIZE);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.memcmp(rBuf, wBuf, MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s收发数据检验不一致", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5:未关闭串口，再次打开串口也是成功的
		if((fd = analogSerialManager.open(portName))<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s打开设备节点成功(fd=%d)", Tools.getLineInfo(),TESTITEM,fd);
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case6:原先的open无参方式打开串口仍是可以正常使用的
		analogSerialManager.close();
		if((fd = analogSerialManager.open())<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s打开设备节点失败(fd=%d)", Tools.getLineInfo(),TESTITEM,fd);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg("请将USB串口与PC设备相连,并打开AccessPort串口工具,清空接收缓冲区,波特率设置为:%d,操作完毕任意键继续",BpsBean.bpsValue);
		Arrays.fill(wBuf, (byte)0x38);
		if((ret1 = analogSerialManager.write(wBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)// 未配置波特率参数发送成功，有默认配置
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg("清空PC端的接收缓冲区后点击任意键继续测试");
		if((ret1 = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret1=analogSerialManager.write(wBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(实际=%d,预期=%d)", Tools.getLineInfo(),TESTITEM,ret1,MAX_SIZE);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg("请将AccessPort接收到的数据发送给POS,操作完毕任意键继续");
		if((ret1 = analogSerialManager.read(rBuf, MAX_SIZE, MAXWAITTIME))!=MAX_SIZE)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(实际=%d,预期=%d)", Tools.getLineInfo(),TESTITEM,ret1,MAX_SIZE);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.memcmp(rBuf, wBuf, MAX_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s收发数据检验不一致", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1:传入的设备节点不存在或为null等情况，打开设备节点应失败
		//(一般COM1是留给物理串口)
		if((ret1 = analogSerialManager.open("ttyUSB1"))>0||(ret2 = analogSerialManager.open("any string"))>0
				||(ret3 = analogSerialManager.open(""))>0||(ret4 = analogSerialManager.open(null))>0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s测试失败(%d,%d,%d,%d)", Tools.getLineInfo(),TESTITEM,ret1,ret2,ret3,ret4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gScreenTime, "usb串口方式测试通过");
	}
	
	/**带参数的open接口,otg方式测试*/
	public void otg_test()
	{
		int ret =-1;
		
		gui.cls_show_msg("请插入OTG线,操作完毕任意键继续");
		// case1:插入OTG线后得到的portName是为ttyUSBx
		String portName = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyUSBx,x的范围0~999,是否与预期格式一致,一致[确认],不一致[其他]",portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "otg_test", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open(portName))<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "otg_test", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:多次对OTG线插拔再次open得到的值应为插拔次数
		gui.cls_show_msg("自行进行多次otg线的热插拔,操作完毕任意键继续");
		String portName1 = analogSerialManager.getPortName();
		if(gui.cls_show_msg("本次获取的PortName=%s,上一次获取的PortName=%s,两者之间的X差应为1,是[确认],否[其他]", portName1,portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "otg_test", gKeepTimeErr, "line %d:%s测试失败(本次portName=%s,上次PortName=%s)", Tools.getLineInfo(),TESTITEM,portName1,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3:多次插拔OTAG线后仍是可以open成功
		if((ret = analogSerialManager.open(portName1))<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "otg_test", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "otg_test", gScreenTime, "带参数的open接口,otg方式测试通过");
	}
	
	public void usb_otg()
	{
		int ret=0;
		
		// case1:插入了otg线，并进行了多次的热插拔，应不影响后续case测试
		gui.cls_show_msg("请插入otg线,操作完毕任意键继续");
		String portName = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyUSBx,x的范围0~999,是否与预期格式一致,一致[确认],不一致[其他]",portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usb_otg", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open(portName))<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usb_otg", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 添加setConfig的测试,陈振龙返回之前setConfig会奔溃
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:切换为usb串口线,应可正常切换
		gui.cls_show_msg("测试人员自行多次插拔otg线后换为USB串口线,操作完毕任意键继续");
		String portName1 = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyGS0,一致[确认],不一致[其他]",portName1)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usb_otg", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open(portName1))<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usb_otg", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NB".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "usb_otg", gScreenTime,"带参数的open接口,usb和otg切换测试通过");
	}
	
	/**不带参数open接口多次插拔测试**/
	private void old_otg_test()
	{
		int ret =-1;
		
		gui.cls_show_msg("请插入OTG线,操作完毕任意键继续");
		// case1:旧的open接口otg线插拔之后仍是可以打开成功
		String portName = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyUSBx,x的范围0~999,是否与预期格式一致,一致[确认],不一致[其他]",portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_otg_test", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open())<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_otg_test", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg("自行进行多次otg线的热插拔,操作完毕任意键继续");
		portName = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyUSBx,x的范围0~999,是否与预期格式一致,一致[确认],不一致[其他]",portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_otg_test", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open())<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_otg_test", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "old_otg_test", gScreenTime, "不带参数的open接口,otg方式测试通过");
	}
	
	/**不带参数的open接口,usb和otg方式转换测试**/
	private void old_usb_otg()
	{
		int ret=0;
		
		// case1:插入了otg线，并进行了多次的热插拔，应不影响后续case测试
		gui.cls_show_msg("请插入otg线,操作完毕任意键继续");
		String portName = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyUSBx,x的范围0~999,是否与预期格式一致,一致[确认],不一致[其他]",portName)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_usb_otg", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open())<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_usb_otg", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 添加setConfig的配置,据陈振龙反馈之前setConfig会奔溃
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm9", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:切换为usb串口线,应可正常切换
		gui.cls_show_msg("测试人员自行多次插拔otg线后换为USB串口线,操作完毕任意键继续");
		String portName1 = analogSerialManager.getPortName();
		if(gui.cls_show_msg("获取的PortName=%s,预期格式为ttyGS0,一致[确认],不一致[其他]",portName1)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_usb_otg", gKeepTimeErr, "line %d:%s测试失败(portName=%s)", Tools.getLineInfo(),TESTITEM,portName1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.open())<0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_usb_otg", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NB".getBytes()))!=ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "old_usb_otg", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "old_usb_otg", gScreenTime,"不带参数的open接口,usb和otg切换测试通过");
	}

	
//	private void open_usb()
//	{
//		int fd;
//		analogSerialManager.close();
//		if((fd=analogSerialManager.open())<0)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME, "open_usb", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,fd);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		
//		if((fd=analogSerialManager.open())<0)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME, "open_usb", gKeepTimeErr, "line %d:%s串口配置失败(%d)", Tools.getLineInfo(),TESTITEM,fd);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//	}
	
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
