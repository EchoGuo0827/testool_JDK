package com.example.highplattest.usbcomm;

import java.util.Arrays;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;
import android.util.Log;

/************************************************************************
 * 
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm7.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170602
 * directory 		: 
 * description 		: 测试Usb模拟串口模块的缓冲区操作
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20150602	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm7 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "虚拟串口ioctl缓冲区操作";
	private final String CLASS_NAME = UsbComm7.class.getSimpleName();
	private final int MAX_SIZE = 1024*2;//由于收发的FIFO限制,数据大小有限制
	private final int MAXWAITTIME = 30;
	boolean isTrue = true;
	private AnalogSerialManager analogSerialManager = null;
	int retFd=0;
	int ret = -1;
	long startTime;
	float time;
	int ret1 = -1;
	long otherTime = 0;
	boolean threadStart = false;
	private Gui gui = new Gui(myactivity, handler);
	public void usbcomm7()
	{
		String funcName="usbcomm7";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
			
		int ret2=-1;
		// 设备节点描述符
		byte[] sendBuf = new byte[MAX_SIZE];
		final byte[] recvBuf = new byte[MAX_SIZE];
		byte[] arg1=new byte [1];
		int[] cmd={0x540B,0x541B};
		/*process body*/
		gui.cls_show_msg1(2, TESTITEM+"测试中,请别开启自动发送。。。");
		// 测试前置
		analogSerialManager.close();
		
		//case1流程异常测试
		
		//case1.1关闭串口后,进行清缓冲和检测缓冲区数据量,预期失败,返回-12
		for (int i = 0; i < 3; i++) {//0,1,2
			arg1[0]=(byte) i;
			if((ret=analogSerialManager.ioctl(cmd[0], arg1))!=-12){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		for (int i = 0; i < 2; i++) {//0,1
			arg1[0]=(byte) i;
			if((ret=analogSerialManager.ioctl(cmd[1], arg1))!=-12){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		//case1.2 非法参数测试,打开open且配置,串口可正常使用
		analogSerialManager.open();
		analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes());//8N1NN非阻塞
		
		//参数异常
		for (int i = 0; i < 2; i++) {
			//清缓冲或检测缓冲区数据量
			if((ret=analogSerialManager.ioctl(cmd[i], null))==NDK_OK){//arg1为null
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
			//arg1参数数据错误,仍返回成功
			//开发说,目前机制也控制不了返回值,都是底层驱动返回的,除非我们上层再加一次参数判断
			/*arg1[0]=(byte)15;
			if((ret=analogSerialManager.ioctl(cmd[i], arg1))==NDK_OK){//arg1数据错误
				if(!gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i))
					return;
			}*/
			arg1[0]=(byte)i;
			if((ret=analogSerialManager.ioctl(0x1111, arg1))==NDK_OK){//cmd数据错误
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case2.1串口输入有缓冲或无缓冲,清输入串口后,检测输入串口数据量应等于0
        
		//case2.1.1输入串口无缓冲数据
		arg1[0]=0;//0输入,1输出,2输入输出
		for (int i = 0; i < 2; i++) {
			if((ret=analogSerialManager.ioctl(cmd[i], arg1))!=NDK_OK){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case2.1.2输入串口有缓冲
		//写数据
		String message = "请确保POS和PC已通过USB线连接,并开启PC端的AccessPort工具,后点[确认]继续";
		gui.cls_show_msg(message);
		
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME
				/ (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 串口非阻塞 读阻塞
		 message = "请将AccessPort接收到的数据复制到发送框并发送,点[确认]继续";
		 gui.cls_show_msg(message);

		// 串口的缓冲区数据量,只有在未做read操作时,输入串口缓冲区有数据量,输出串口缓冲区量永远为0（已和开发确认）
		if ((ret = analogSerialManager.ioctl(0x541B, new byte[] { 0 })) != MAX_SIZE) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.e("read1", ret1+"");
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret1,ret2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//清数据是否成功,检测输入串口缓冲区数据量是否为0
		arg1[0]=0;
		for (int i = 0; i < 2; i++) {
			if((ret=analogSerialManager.ioctl(cmd[i], arg1))!=NDK_OK){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case2.2串口输出有缓冲或无缓冲,清输出串口后,检测输出串口数据量应等于0
		
		//case2.2.1输出串口有缓冲
		
		//写数据
		message = "请清空发送接收缓冲区的数据,点[确认]继续";
		gui.cls_show_msg(message);
		
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME
				/ (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			analogSerialManager.close();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//清缓冲和检测缓冲区数据量,输出的缓冲区数据量永远为0
		arg1[0]=1;
		for (int i = 0; i < 2; i++) {
			if((ret=analogSerialManager.ioctl(cmd[i], arg1))!=NDK_OK){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case2.2.2输出串口无缓冲（上一步已清除）,预期应成功
		arg1[0]=1;
		for (int i = 0; i < 2; i++) {
			if((ret=analogSerialManager.ioctl(cmd[i], arg1))!=NDK_OK){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case2.3 串口输入输出均有数据或一方有数据或均无数据,清输入输出缓冲后,检测输入输出串口缓冲数据量应均等于0
		
		//case2.3.1输入输出均有数据
		 message = "请清空发送接收缓冲区的数据,点[确认]继续";
		 gui.cls_show_msg(message);
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME
				/ (BpsBean.bpsId + 1))) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s设置串口失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 串口非阻塞 读阻塞
		 message = "请将AccessPort接收到的数据复制到发送框并发送,点[确认]继续";
		 gui.cls_show_msg(message);

		// 串口的缓冲区数据量,只有在未做read操作时,输入串口缓冲区有数据量,输出串口缓冲区量永远为0（已和开发确认）
		if ((ret = analogSerialManager.ioctl(0x541B, new byte[] { 0 })) != MAX_SIZE) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s写数据失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.e("read1", ret1+"");
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s数据校验失败(%d,%d)", Tools.getLineInfo(),TESTITEM,ret1,ret2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//输入输出均清空
		arg1[0]=2;
		if((ret=analogSerialManager.ioctl(cmd[0], arg1))!=NDK_OK){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//验证缓冲区数据量
		for (int i = 0; i < 2; i++) {
			arg1[0]=(byte) i;
			if((ret=analogSerialManager.ioctl(cmd[1], arg1))!=NDK_OK){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(),TESTITEM,ret,i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case2.3.2仅输出有数据
		message = "请清空发送接收缓冲区的数据,点[确认]继续";
		gui.cls_show_msg(message);
		for (int j = 0; j < sendBuf.length; j++) 
		{
			sendBuf[j] = (byte) (Math.random() * 256);
		}
		if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s设置串口失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 输入输出均清空
		arg1[0] = 2;
		if ((ret = analogSerialManager.ioctl(cmd[0], arg1)) != NDK_OK) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 验证缓冲区数据量
		for (int i = 0; i < 2; i++) {
			arg1[0] = (byte) i;
			if ((ret = analogSerialManager.ioctl(cmd[1], arg1)) != NDK_OK) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(), TESTITEM,
						ret, i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		//case2.3.3仅输入有数据（上一步已清空输出）
		// 串口非阻塞 读阻塞
		message = "请将AccessPort接收到的数据复制到发送框并发送,后点[确认]继续";
		gui.cls_show_msg(message);
		
		// 串口的缓冲区数据量,只有在未做read操作时,输入串口缓冲区有数据量,输出串口缓冲区量永远为0（已和开发确认）
		if ((ret = analogSerialManager.ioctl(0x541B, new byte[] { 0 })) != MAX_SIZE) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret1 = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME)) != MAX_SIZE) {
			analogSerialManager.close();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s写数据失败(%d)", Tools.getLineInfo(), TESTITEM, ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		Log.e("read1", ret1 + "");
		if (!Tools.memcmp(sendBuf, recvBuf, MAX_SIZE)) {
			analogSerialManager.close();
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s数据校验失败(%d,%d)", Tools.getLineInfo(), TESTITEM, ret1,
					ret2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 输入输出均清空
		arg1[0] = 2;
		if ((ret = analogSerialManager.ioctl(cmd[0], arg1)) != NDK_OK) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 验证缓冲区数据量
		for (int i = 0; i < 2; i++) {
			arg1[0] = (byte) i;
			if ((ret = analogSerialManager.ioctl(cmd[1], arg1)) != NDK_OK) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(), TESTITEM,ret, i);
			if(GlobalVariable.isContinue==false)
				return;
			}
		}
		
		//case 2.3.4输入输出均无数据（上一步骤使输入输出均无数据）
		// 输入输出均清空
		arg1[0] = 2;
		if ((ret = analogSerialManager.ioctl(cmd[0], arg1)) != NDK_OK) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 验证缓冲区数据量
		for (int i = 0; i < 2; i++) {
			arg1[0] = (byte) i;
			if ((ret = analogSerialManager.ioctl(cmd[1], arg1)) != NDK_OK) {
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d),i=%d", Tools.getLineInfo(), TESTITEM,ret, i);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//测试后置
		//清空输入输出串口（上一步已做）
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

