package com.example.highplattest.usbcomm;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150619
 * directory 		: 
 * description 		: usb模拟串口模块的write
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150619	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm3 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = UsbComm3.class.getSimpleName();
	public final int MAX_SIZE = 1024 * 5;
	public final int SERIAL_SIZE = 1024*4;
	public final int MAXWAITTIME = 30;
	private AnalogSerialManager analogSerialManager = null;
	private final String TESTITEM = "虚拟串口Write";
	private Gui gui = new Gui(myactivity, handler);
	public void usbcomm3() 
	{
		String funcName="usbcomm3";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/*private & local definition*/
		int ret = -1, ret1 = -1,ret2 = -1;
		String[] para={"8N1NB","8N1NN"};
		byte[] sendBuf = new byte[MAX_SIZE];
		byte[] recvBuf = new byte[MAX_SIZE];
		byte[] recv1Buf = new byte[MAX_SIZE - SERIAL_SIZE];
		byte[] testbuf = new byte[0];
		byte[] sendBuf_1=new byte[1] ;//只写入一个0x6C
//		byte[] ASCLLbuf= {};
		/*process body*/
		
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		// 测试前置，关闭串口
		analogSerialManager.close();
		
		// case1:流程异常，对未打开的串口进行写操作，应返回相应错误
		Arrays.fill(sendBuf, (byte) 0);
		Arrays.fill(recvBuf, (byte) 0);
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}	
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) > 0)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		String message = "请确保POS和PC已通过USB线连接,并开启PC端的AccessPort工具,后点[确认]继续";
		gui.cls_show_msg(message);
		
		
		// case2:参数异常，打开串口，传入非法参数NULL，非法长度
		if (((ret = analogSerialManager.open()) == -1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 异常测试 
		// 参数异常就应该返回-6
		// 巫丙亮返回USB串口不支持阻塞方式
//		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());
//		if (((ret = analogSerialManager.write(null, MAX_SIZE, MAXWAITTIME/ (BpsBean.bpsId + 1))) == 0 
//				| (ret1 = analogSerialManager.write(recvBuf, 0, MAXWAITTIME / (BpsBean.bpsId + 1))) == 0)
//				| (ret2 = analogSerialManager.write(testbuf, MAX_SIZE,MAXWAITTIME / (BpsBean.bpsId + 1))) == 0) 
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d,%d,%d)",Tools.getLineInfo(), TESTITEM, ret, ret1, ret2);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		//开发意见：null 返回-6 (<参数非法*/)  0返回-1(<错误参数*/)
		if(((ret = analogSerialManager.write(null, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1))) !=-6)|| ((ret1 = analogSerialManager.write(recvBuf, 0, MAXWAITTIME/(BpsBean.bpsId+1))) != -1)||(ret2 = analogSerialManager.write(testbuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1)))!=-1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s写失败(%d,%d,%d)", Tools.getLineInfo(),TESTITEM,ret,ret1,ret2);
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case3 串口阻塞，函数阻塞/非阻塞都应写成功(timeoutSec=0 & timeoutSec>0)
		 // para[0]阻塞 para[1] 非阻塞
		 // 串口非阻塞情况下   timeoutSec > 0 && 超时时间测试
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if (((ret = analogSerialManager.write(sendBuf, SERIAL_SIZE,MAXWAITTIME/(BpsBean.bpsId+1))) != SERIAL_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		SystemClock.sleep(1000);
		
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		if (((ret1 = analogSerialManager.write(sendBuf, SERIAL_SIZE,0)) != SERIAL_SIZE))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		SystemClock.sleep(1000);
		
//		//串口阻塞情况下   timeoutSec > 0 && 超时时间测试(巫丙亮反馈USB串口不支持阻塞方式)
//		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());	
////		long startTime=System.currentTimeMillis();
////		||((tmp=Tools.getStopTime(startTime)-MAXWAITTIME/(BpsSetting.bpsId+1)) > WUCHASEC)
//		if(((ret = analogSerialManager.write(sendBuf, SERIAL_SIZE,MAXWAITTIME/(BpsBean.bpsId+1))) != SERIAL_SIZE))
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写串口超时时间错误(%d，tmp = %f)", Tools.getLineInfo(),TESTITEM,ret,tmp);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		SystemClock.sleep(1000);
//		
//		//阻塞情况下   timeoutSec = 0
//		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[0].getBytes());	
//		if((ret1 = analogSerialManager.write(sendBuf, SERIAL_SIZE,0)) != SERIAL_SIZE)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		SystemClock.sleep(1000);

		
		// case4 普通数据包发送接收比较一致(大于4K)
		message = "清空接收缓冲区,后点[确认]继续";
		gui.cls_show_msg(message);
		// 设置串口为非阻塞
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		Arrays.fill(sendBuf, (byte) 0);
		for (int i = 0; i < sendBuf.length; i++)
		{
			sendBuf[i] = (byte) (Math.random() * 256);
		}
		if ((ret1 = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		
		// 对5k的数据的读取（先读取4K，再读1k）
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = analogSerialManager.read(recvBuf, SERIAL_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != SERIAL_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		Log.e("sendBuf", Arrays.toString(sendBuf));
		Log.e("recvBuf", Arrays.toString(recvBuf));
		
		// 读取剩余部分(1K)
		Arrays.fill(recv1Buf, (byte) 0);
		if ((ret1 = analogSerialManager.read(recv1Buf, MAX_SIZE - SERIAL_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE - SERIAL_SIZE) 
		{
			if(ret==-1)
			{
				//PC工具未准确发送5K数据
				gui.cls_show_msg("PC工具未准确发送5K数据，请测试人员发送准确的数据后再测试");
				return;
			}
			gui.cls_show_msg1(gKeepTimeErr, "line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 比较收发数据是否一致
		for (int j = 0; j < recv1Buf.length; j++) 
		{
			recvBuf[SERIAL_SIZE + j] = recv1Buf[j];
		}
		
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		message = "(1)请清空发送接收缓冲区,后点[确认]继续";
		gui.cls_show_msg(message);
		
		// case5:写完数据立刻关闭串口，不应有异常
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
		
		// case6:关闭串口后重打开串口可进行正常数据收发
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}

		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
 		message = "(2)请清空接收缓冲区的数据,后点[确认]继续";
 		gui.cls_show_msg(message);

		Arrays.fill(sendBuf, (byte) 0);
		for (int i = 0; i < sendBuf.length; i++)
		{
			sendBuf[i] = (byte) (Math.random() * 256);
		}
		if ((ret1 = analogSerialManager.write(sendBuf, SERIAL_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != SERIAL_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = analogSerialManager.read(recvBuf, SERIAL_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != SERIAL_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if (!Tools.memcmp(sendBuf, recvBuf, SERIAL_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		} 
		
		if ((ret = analogSerialManager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		} 
		
		// case7:只发送一个字节0x6C,校验是否有异常  by zsh 190124
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		message = "(3)请清空接收缓冲区的数据,后点[确认]继续";
		gui.cls_show_msg(message);
		sendBuf_1[0]=0x6C;
		if ((ret1 = analogSerialManager.write(sendBuf_1, 1, MAXWAITTIME / (BpsBean.bpsId + 1))) != 1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = analogSerialManager.read(recvBuf, 1, MAXWAITTIME / (BpsBean.bpsId + 1))) != 1) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if (!Tools.memcmp(sendBuf_1, recvBuf, 1)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		} 	
		if ((ret = analogSerialManager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//case 8:传入ASCLL码的所有字符,检验是否有异常 	by zsh 190124
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes());
		message = "(4)请清空接收缓冲区的数据,后点[确认]继续";
		gui.cls_show_msg(message);
		char s1[] = new char[256] ;// 保存ASCII字符的字符数组
		for (int j=0; j<256; j++)
		{
		s1[j] = (char) j;
		}
		byte[]sendASCLL=getBytes(s1);
		if ((ret1 = analogSerialManager.write(sendASCLL, 256, MAXWAITTIME / (BpsBean.bpsId + 1))) != 256) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = analogSerialManager.read(recvBuf, 256, MAXWAITTIME / (BpsBean.bpsId + 1))) != 256) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if (!Tools.memcmp(sendASCLL, recvBuf, 256)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		} 	
		if ((ret = analogSerialManager.close()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s关闭串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}	
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
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
