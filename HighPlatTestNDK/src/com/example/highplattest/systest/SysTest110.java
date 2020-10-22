package com.example.highplattest.systest;


import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.uartport.UartPort;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest110.java
 * Author 			: yanglj
 * version 			: 
 * DATE 			: 20200907
 * directory 		: 
 * description 		: 外设SP100压力测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  yanglj			20200907	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest110 extends DefaultFragment {
	private final String TAG = SysTest106.class.getSimpleName();
	private final String TESTITEM = " 外接SP100密码键盘测试(海外版_X5支持)";
	JniNdk jniNdk;
	private Gui gui;		
	private boolean isInit=false;
	private boolean re=false;
	private int ret = -1;		
	private int USBCOMNODE=-1;

	public void systest110(){
		jniNdk=new JniNdk();
		gui = new Gui(myactivity, handler);
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("【测试前要先把/SVN/Tool/外设SP100压力测试相关的NLP文件安装到密码键盘】\n" +
					"1.外接SP100密码键盘压力测试\n2.带关闭再打开串口的压力测试\n按ESC键退出");
			/*
			 * 选项1若是运行程序后第一次测试需配置串口，然后配置压力测试的参数进行测试，测试结束后串口不会自动关闭
			 * 选项2在串口发送数据达到1M后会关闭串口再打开，再继续未完成的测试
			 */
			switch (returnValue) 
			{
				case '1':
					conf_test_bps();
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						continue;
					}
					re=false;
					test();
					break;
				case '2':
					conf_test_bps();
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						continue;
					}
					re=true;
					test();
					break;
				case ESC:
					intentSys();
					return;
					
				default:
					portClose();//其他按键会使串口关闭，若此时串口不是打开状态进行关闭会报异常
					break;
			}
		}
		
	}
	
	private byte[] generateArray(int len,int max){
		byte[] arr=new byte[len];
		for(int i=0;i<arr.length;i++){
			arr[i]=(byte)(Math.random()*max);
		}
		return arr;
	}
	
	private void test() {
		/*
		 * 本测试需要SP100刷特定固件，可到...\TestCase\高端\testool_JDK\Tool\目录下”外设SP100压力测试相关.rar“中获取
		 * 对CPOS机，不同的固件版本允许与SP100通讯时的字节数上限不同，请确认使用的固件版本最多支持的单次通讯的字节数的上限，并在测试时对压力测试参数进行合理配置
		 */
		int len = 64;
		byte[] temp = new byte[4096];
		byte[] cmdPack;
		int totalTimes = 100;
		int successTimes = 0;
		int failureTimes = 0;
		int times = 0;
		int cbuf=0;
		int j=0;
		String bufSize = "每次传输数据默认大小为：";
		String testTimes = "压力测试次数默认为：";
		len = gui.JDK_ReadData(TIMEOUT_INPUT, 64, bufSize);
		totalTimes = gui.JDK_ReadData(TIMEOUT_INPUT, 100, testTimes);
		while(times++ != totalTimes){
			if(cbuf >= 10240){
				portClose();
				portOpen();
				cbuf = 0;
			}
			cmdPack = generateArray(len, 255);
			if ((ret = UartPort.JNI_write(USBCOMNODE,cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
			{
				gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 1,"line %d:USB写失败(ret=%d)", Tools.getLineInfo(),ret);
				failureTimes++;
				gui.cls_show_msg1_record(TAG, "PressureTest:", 1,"第%d次测试，已成功%d/%d次，失败%d/%d次", times, successTimes, totalTimes, failureTimes, totalTimes);
				continue;
			}
			while((ret = UartPort.JNI_read(USBCOMNODE, temp, len, 800))==0);
			if (ret != len){
				failureTimes++;
				gui.cls_show_msg1_record(TAG, "PressureTest:", 1,"第%d次测试，已成功%d/%d次，失败%d/%d次:ret=%d", times, successTimes, totalTimes, failureTimes, totalTimes,ret);
				continue;
			}
			for(j=0;j<len;j++)
				if(cmdPack[j]!=temp[j]){
					gui.cls_show_msg1_record(TAG, "PressureTest:", 1,"收发数据不符合,第%d个",j);
					failureTimes++;
					break;
				}
			if(j == len)
				successTimes++;
			else
				continue;
			gui.cls_show_msg1_record(TAG, "PressureTest:", 1,"第%d次测试，已成功%d/%d次，失败%d/%d次:ret=%d", times, successTimes, totalTimes, failureTimes, totalTimes,ret);
			if(re)
				cbuf += len;	
		}//end of while
		
		if(successTimes == totalTimes)
			gui.cls_show_msg("测试通过，共成功%d次.按任意键继续...", successTimes);
		else
			gui.cls_show_msg("测试未通过，共成功%d/%d次, 失败%d/%d次.按任意键继续...", successTimes, totalTimes, failureTimes, totalTimes);
		
	}
	
	
	private void conf_test_bps() {
		// 进行波特率的选择
		isInit=false;
		if(!re){
			gui.cls_show_msg("请确保POS与SP100密码键盘已连接(目前只支持波特率设置为115200)!按任意键继续...");
			BpsBean.bpsValue = 115200;
			//初始化串口
		}
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "conf_test_bps", g_keeptime,"line %d:串口初始化失败(%d)", Tools.getLineInfo(),ret);
			portClose();
			return;
		}
		gui.cls_show_msg1(1, "串口已初始化，通讯波特率为%d.", BpsBean.bpsValue);
		isInit = true;
	}
	
	/**
	 * 整合串口初始化
	 */
	private int portOpen() {
		int fd3=-1;
		
		Log.d("eric_chen", "进入USB");
		byte[] data = new byte["8N1NN".getBytes().length+1];
		System.arraycopy("8N1NN".getBytes(), 0, data, 0, data.length-1);
		if ((fd3=UartPort.JNI_openPort(3, BpsBean.bpsValue, data))<0) {
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:串口打开失败（%d）", Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
		gui.cls_show_msg1(1, "串口已打开.");
		USBCOMNODE=fd3;
		Log.d("eric_chen", "USBCOMNODE=="+USBCOMNODE);
		
		return NDK_OK;
	}
	
	/**
	 * 整合串口关闭
	 */
	private void portClose() {
		
		int rettem;
		if ((rettem=UartPort.JNI_close(USBCOMNODE))!=0) {
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:关闭串口异常（%d）", Tools.getLineInfo(),rettem);
		}
		gui.cls_show_msg1(1, "串口已关闭.");
	}
	
	
	

}
