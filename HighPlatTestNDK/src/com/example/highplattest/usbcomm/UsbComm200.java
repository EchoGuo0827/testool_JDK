package com.example.highplattest.usbcomm;

import java.util.Arrays;
import java.util.Random;

import android.annotation.SuppressLint;
import android.newland.AnalogSerialManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;


/************************************************************************
 * 
 * module 			: Usb模拟串口模块
 * file name 		: UsbComm200.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20170823
 * directory 		: 
 * description 		: UsbComm模块内随机
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		        20170823	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class UsbComm200 extends UnitFragment {
	
	private final String CLASS_NAME = UsbComm200.class.getSimpleName();
	private String TESTITEM = "UsbComm模块内随机";
	private Gui gui = new Gui(myactivity, handler);	
    private AnalogSerialManager analogSerialManager = null;
    private final int MAX_SIZE = 1024*4;
    private byte[] sendBuf = new byte[MAX_SIZE];
    private byte[] recvBuf = new byte[MAX_SIZE];
    private boolean openflag = false;
    private boolean dataflag = false;
    Random random = new Random();
    
	public void usbcomm200()
	{
		String funcName="usbcomm200";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		switch (GlobalVariable.currentPlatform) 
		{
		case IM81_Old:
		case N900_3G:
			String usbFuncArr2[] = {"open","close","getVersion","isvalid","setconfig","read","write"};
		    int len2 = usbFuncArr2.length;
		    usbcomm8_test(usbFuncArr2,len2);
			break;
		default:
			String usbFuncArr1[] = {"open","close","getVersion","isvalid","setconfig","read","write","ioctl"};
		    int len1 = usbFuncArr1.length;
		    usbcomm8_test(usbFuncArr1,len1);
			break;
		}
		
	}
	
	public void usbcomm8_test(String[] usbFuncArr,int len)
	{
		String funcName="usbcomm8_test";
		/*private & local definition*/
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
		// 设备节点描述符
		int fd=-1;
		/*process body*/		
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中，请别开启自动发送。。。");
		
		
		String message = "请确保POS和PC已通过USB线连接，并开启PC端的AccessPort工具，后点【确认】继续";
		gui.cls_show_msg(message);
		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "UsbComm模块内随机组合测试中...\n还剩%d次（已成功%d次），按【取消】退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			StringBuilder funcStr = new StringBuilder();				
			for(int i=0;i<g_RandomCycle;i++){
				func[i] = usbFuncArr[random.nextInt(len)];
				funcStr.append(func[i]).append("-->\n");	
				if((i+1)%10 == 0 || i == g_RandomCycle-1){
					gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr.toString(),bak-cnt+1);
					funcStr.setLength(0);
				}
			}
			cnt--;
			boolean ret=false;
			
			//每次测试前置，打开串口设备
			if ((fd = analogSerialManager.open()) == -1) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%打开串口失败(%d)", Tools.getLineInfo(),TESTITEM,fd);
				if (!GlobalVariable.isContinue)
					return;
			}
			openflag = true;
			LoggerUtil.d("fd:"+fd);
			
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"随机第%d组第%d项，正在测试%s",bak-cnt,i+1,func[i]);
				UsbCommFuncName fname = UsbCommFuncName.valueOf(func[i]);
				if(!(ret=test(fname))){
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s第%d组第%d项,%s方法测试失败",TESTITEM,bak-cnt,i+1,func[i]);
					break;
				}
			}
			if(!ret){
				for(int i=0;i<g_RandomCycle;i++){
					funcStr.append(func[i]).append("-->");
				}
				gui.cls_only_write_msg(CLASS_NAME,funcName,"第%d组随机测试失败，测试顺序为：%s",bak-cnt,funcStr.toString());
				funcStr.setLength(0);
			} else{
				succ++;
			}
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "%s测试完成，已执行次数为%d，成功为%d次。\n请检查UsbComm模块内其他用例是否能正常使用！", TESTITEM, bak-cnt,succ);
		
	}
	
	public boolean test(UsbCommFuncName fname)
	{
		String funcName="test";
		int ret = -1;
		boolean bret = false;
		byte[] arg1=new byte [1];
		int[] cmd={0x540B,0x541B};
		// 设备节点描述符
		int fd=-1;
		
		switch(fname){
		case open:
			if ((fd = analogSerialManager.open()) == -1) 
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:打开串口测试失败(%d)", Tools.getLineInfo(),fd);
				return false;
			}
			openflag = true;
			LoggerUtil.d("fd:"+fd);
			break;
		case close:
			if(openflag){
				if ((ret = analogSerialManager.close()) != ANDROID_OK) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:关闭串口测试失败(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
				openflag = false;
			} else{
				if ((ret = analogSerialManager.close()) == ANDROID_OK) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:关闭串口流程异常失败(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
			}
			break;
		case getVersion:
			String version = analogSerialManager.getVersion();
			gui.cls_show_msg1(gScreenTime,"获取版本成功(ver = %s)",version);
			break;
		case isvalid:
			if(openflag){
				if((bret=analogSerialManager.isValid()) == false)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:检查设备是否可用失败(%s)", Tools.getLineInfo(),bret);
					return false;
				}	
			} else{
				//串口没有打开的时候，设备不可用
				if ((bret = analogSerialManager.isValid()) == true) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:检查设备是否可用失败(%s)", Tools.getLineInfo(),bret);
					return false;
				} 
			}
			break;
		case setconfig:
			if(openflag){
				//未阻塞
				if((ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NN".getBytes()))!=ANDROID_OK)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:设置串口失败(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}	
			} else{
				//流程异常，未打开串口，设置串口参数应应该返回-12|-13,fd错
				ret = analogSerialManager.setconfig(BpsBean.bpsValue, 0, "8N1NB".getBytes());
				if (ret != ANDROID_FD_ERR1 && ret != ANDROID_FD_ERR2) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:设置串口流程异常测试失败(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
			}
			break;
		case read:
			Arrays.fill(recvBuf, (byte) 0);
			if(openflag && !dataflag){
				//串口里面如果没有写数据，会一直卡在那里，故直接跳过
				break;
			}
			if( openflag && dataflag){		
				String message = "请将AccessPort接收到的数据复制到发送框并发送，后点【确认】继续";
				gui.cls_show_msg(message);
				if ((ret = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读数据失败(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
			} else{
				ret = analogSerialManager.read(recvBuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1));
				if (ret != ANDROID_PORT_FD_ERR && ret != ANDROID_PORT_READ_FAIL) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:读数据流程异常返回值错误(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
			}
			break;
		case write:
			Arrays.fill(sendBuf, (byte) 0);
			for (int j = 0; j < sendBuf.length; j++) 
			{
				sendBuf[j] = (byte) (Math.random() * 256);
			}			
			if(openflag){
				String message = "请清空发送接收缓冲区的数据，后点【确认】继续";
				gui.cls_show_msg(message);
				if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) 
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据失败(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
				dataflag = true;
			} else{
				if ((ret = analogSerialManager.write(sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) > 0)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据流程异常返回值错误(ret=%d)", Tools.getLineInfo(),ret);
					return false;
				}
			}						
			break;
		case ioctl:
			if(openflag){
				for (int i = 0; i < 3; i++) {//0输入，1输出，2输入输出，cmd[0]清缓冲
					arg1[0]=(byte) i;
					if((ret=analogSerialManager.ioctl(cmd[0], arg1))!=NDK_OK){
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:虚拟串口ioctl清缓冲测试失败(ret=%d)", Tools.getLineInfo(),ret);
						return false;
					}
				}
				for (int i = 0; i < 2; i++) {//0输入，1输出，2输入输出，cmd[0]检测缓冲区数据量
					arg1[0]=(byte) i;
					if((ret=analogSerialManager.ioctl(cmd[1], arg1))!=NDK_OK){
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:虚拟串口ioctl检测缓冲区数据量测试失败(ret=%d)", Tools.getLineInfo(),ret);
						return false;
					}
				}
			} else{
				//串口未打开，进行清缓冲和检测缓冲区数据量，预期失败，返回-12
				for (int i = 0; i < 3; i++) {//0输入，1输出，2输入输出，cmd[0]清缓冲
					arg1[0]=(byte) i;
					if((ret=analogSerialManager.ioctl(cmd[0], arg1))!=-12){
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:虚拟串口ioctl流程异常测试失败(ret=%d)", Tools.getLineInfo(),ret);
						return false;
					}
				}
				
				for (int i = 0; i < 2; i++) {//0,1，cmd[1]检测缓冲区数据量
					arg1[0]=(byte) i;
					if((ret=analogSerialManager.ioctl(cmd[1], arg1))!=-12){
						gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:虚拟串口ioctl流程异常测试失败(ret=%d)", Tools.getLineInfo(),ret);
						return false;
					}
				}
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	public enum UsbCommFuncName
	{
		open,
		close,
		getVersion,
		isvalid,
		setconfig,
		read,
		write,
		ioctl
	}
	
	@Override
	public void onTestUp() 
	{
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
