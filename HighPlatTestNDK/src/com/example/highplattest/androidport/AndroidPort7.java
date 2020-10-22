package com.example.highplattest.androidport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.util.Log;
import android_serialport_api.SerialPort;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生串口收发测试 
 * file name 		: AndroidPort7.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190130 
 * directory 		: 
 * description 		: 自收自发,串口收发
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zsh		   	20190130	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class AndroidPort7 extends UnitFragment 
{
	private final String CLASS_NAME = AndroidPort7.class.getSimpleName();
	private String TESTITEM = "安卓原生串口测试";
    private String RS232_PATH = "/dev/ttyHSL0";
    private boolean mSerialPortStatus = false; //是否打开串口标志
    private boolean mThreadStatus; //线程状态，为了安全终止线程
    private SerialPort mSerialPort = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private final  int DATA_SIZE=1024;//每次读取DATASIZE位
    private byte[] mSendBuf=new byte[DATA_SIZE];
    private byte[] mRecBuf = new byte[DATA_SIZE];
    private int WriteTag=0;
	private Gui gui = new Gui(myactivity, handler);
	
	public void androidport7()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232)==false&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持物理串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		if (GlobalVariable.currentPlatform==Model_Type.X1||GlobalVariable.currentPlatform==Model_Type.F7||GlobalVariable.currentPlatform==Model_Type.F7) {
			RS232_PATH = "/dev/ttyHSL0";
		}else {
			RS232_PATH = "/dev/ttyHSL2";
		}
		///测试前置,打开串口,初始化校验数组
		openSerialPort();
		for(int i=0;i<DATA_SIZE;i++)
			mSendBuf[i]=8;
		Log.d(TESTITEM, "初始化mSendBUf成功,mSendBuf=:"+Arrays.toString(mSendBuf));
		//case1:自收自发
		gui.cls_show_msg("请先取消案例jni目录下Android.mk文件中libserial_port.so标签下的注释,重新安装后任意键继续");
		gui.cls_show_msg("即将进行自收自发测试,请短接RS232串口的23脚,点任意键继续");
		if(mSerialPortStatus==false){
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s串口打开失败,打开串口异常%s", Tools.getLineInfo(),TESTITEM,mSerialPort);
			if(GlobalVariable.isContinue==false)
				return;
		};
		if(sendSerialPort(mSendBuf)==false){//发送校验数据mSendBuf
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s校验数据发送失败,请查看日志信息", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		};
		if(mThreadStatus==false){
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s读数据线程异常,请查看日志信息", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue)
				return;
		}
		try {
            Thread.sleep(500);//主线程休眠等待数据读取
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		if(!Tools.memcmp(mSendBuf, mRecBuf, DATA_SIZE)){
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s数据校验失败,收发数据不一致", Tools.getLineInfo(),TESTITEM);
			Log.e("mRecBuf=", Arrays.toString(mRecBuf));
			return;
		}else{
			gui.cls_show_msg("数据校验成功,自收自发测试通过");
		}
		gui.cls_show_msg("自收自发测试结束,请连接pc串口,打开串口工具,任意键继续进入串口收发测试");
		
		//case2:串口收发
		Arrays.fill(mRecBuf, (byte) 0);//清空接收缓存区
		gui.cls_show_msg("请清空接收缓存区的数据,任意键继续");
		if(sendSerialPort(mSendBuf)==false){//发送校验数据mSendBuf
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s校验数据发送失败,请查看日志信息", Tools.getLineInfo(),TESTITEM);
			return;
		};
		gui.cls_show_msg("复制接收到的数据并发送,任意键继续");
		if(!Tools.memcmp(mSendBuf, mRecBuf, DATA_SIZE)){
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s数据校验失败,收发数据不一致",Tools.getLineInfo(),TESTITEM);
			Log.e("mRecBuf=", Arrays.toString(mRecBuf));
			return;
		}else{
			gui.cls_show_msg("数据校验成功,串口收发测试通过");
		}
		if(closeSerialPort()==false){
			gui.cls_show_msg1_record(CLASS_NAME,"androidport7",gKeepTimeErr,"line %d:%s串口关闭失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		gui.cls_show_msg("数据收发校验结束,长按确认退出测试");
	}
	
	//打开串口
	public SerialPort openSerialPort(){
        try {
            mSerialPort = new SerialPort(new File(RS232_PATH),BpsBean.bpsValue,0);
           
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();
            mSerialPortStatus = true;
            mThreadStatus = true;
            ReadData.start(); //开始线程监控是否有数据要接收   
        } catch (IOException e) {
        	mSerialPortStatus = false; 
            Log.e(TESTITEM, "openSerialPort: 打开串口异常：" + e.toString());
            return mSerialPort;
        }
        Log.d(TESTITEM, "openSerialPort: 打开串口");
        return mSerialPort;
    }
	 
	//关闭串口
	public boolean closeSerialPort(){
        try {
            mInputStream.close();
            mOutputStream.close();
            this.mSerialPortStatus = false;
            this.mThreadStatus = false; //线程状态
            mSerialPort.close();
            return true;
        } catch (IOException e) {
            Log.e(TESTITEM, "closeSerialPort: 关闭串口异常："+e.toString());
            return false;
        }
    }
	 
	//发送数据
	public  boolean sendSerialPort(byte[] data){
		Log.d(TESTITEM, "sendSerialPort: 发送数据");
		try {
			if (mOutputStream != null) {
				mOutputStream.write(data);
				Log.d(TESTITEM, "sendSerialPort: 串口数据发送成功");
				return true;
			} else 
				return false;
		} catch (IOException e) {
			Log.e(TESTITEM, "sendSerialPort: 串口数据发送失败：" + e.toString());
			return false;
		}
    }
	 
	//读取数据的线程
	Thread ReadData=new Thread(){
		public void run() {
			while (mThreadStatus) {
				Log.d(TESTITEM, "进入线程ReadData");
				int size; // 读取数据的大小
				byte[] buffer = new byte[DATA_SIZE];
				try 
				{
					if (mInputStream == null) 
					{
						Log.e(TESTITEM, "ReadData: inputStream == null");
						return;
					}
					size = mInputStream.read(buffer);
					Log.d(TESTITEM, "读取到数据的大小为:"+size);
					Log.d(TESTITEM, "读取到buffer的内容为:"+Arrays.toString(buffer));
			
					Thread.sleep(20);// 每20ms读一次
					if (size > 0) 
					{
						System.arraycopy(buffer, 0, mRecBuf, WriteTag, size);// 串口会分段读取
						WriteTag += size;
						Log.d(TESTITEM,"ReadThread: 监听到数据:" + Arrays.toString(mRecBuf));
						if(mRecBuf[1023]==8){//最后一位赋值成功,说明1024字节的数据读取完毕
							WriteTag=0;//初始化目标位置
						}
					}
				} catch (IOException e) {
					mThreadStatus=false;
					Log.e(TESTITEM, "run: 数据读取异常：" + e.toString());
				} catch (InterruptedException e) {
					mThreadStatus=false;
					Log.e(TESTITEM, "run: 数据读取异常：" + e.toString());
				}
			}
		};
	};
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// 测试后置
		closeSerialPort();
	}

}
