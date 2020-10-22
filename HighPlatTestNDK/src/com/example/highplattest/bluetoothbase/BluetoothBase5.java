package com.example.highplattest.bluetoothbase;

import java.util.ArrayList;
import java.util.Arrays;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.control.BluetoothController;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

/************************************************************************
 * 
 * module 			: N910wifi蓝牙底座模块
 * file name 		: BluetoothBase5.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180226
 * directory 		: 
 * description 		: 蓝牙底座的扫描枪模块
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180226	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class BluetoothBase5 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "扫描枪模块";
	public final String TAG = BluetoothBase5.class.getSimpleName();
	private final int MAX_SIZE = 1024*16;
	private final int OVER_SIZE = 1024*18;
	private Config config;
	private int ret=-1;
	// 蓝牙
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private BluetoothController nlBluetooth;
	private Gui gui = new Gui(myactivity, handler);
	byte[] buf = new byte[MAX_SIZE];
	byte[] rbuf = new byte[MAX_SIZE];
	int portType=10;//扫描枪
	StringBuffer  outReceiveLen1 = new StringBuffer();
	StringBuffer  outReceiveLen2 = new StringBuffer();
	public void bluetoothbase5()
	{
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase5",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*process body*/
		nlBluetooth = BluetoothController.getInstance();	
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		config = new Config(myactivity, handler);
		Arrays.fill(buf, (byte) 0);
//		for (int j = 0; j < buf.length; j++) 
//			buf[j] = (byte) (Math.random()*256);
		
		gui.cls_show_msg1(2, TESTITEM+"测试中,配置蓝牙。。。");
		config.btConfig(pairList, unPairList, bluetoothManager);
		if(unPairList.size()==0)	{
			gui.cls_show_msg1(2, "未搜索到蓝牙底座");
			dialog.dismiss();
			return;	
		}	
		//连接蓝牙,MAC地址getBtAddr()
		if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())){ 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:连接蓝牙失败（%d）", Tools.getLineInfo(),ret);
				return;
		}
		
		// 测试前置,0代表RS232，8代表usb模拟串口，10代表扫描枪usb主设备，0代表扫描枪COM口同RS232
		nlBluetooth.portClose(portType);
		StringBuffer  out = new StringBuffer();
		byte[] outBuf = new byte[MAX_SIZE];
		gui.cls_show_msg1(30,"请在30秒内接入usb口的扫描枪,接入后按任意键继续");
		gui.cls_show_msg1(2, TESTITEM+"参数异常测试中。。。");
		//case1：参数异常测试
		//portOpen
		if((ret=nlBluetooth.portOpen(-1, "115200,8,N,1"))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portOpen(12, "115200,8,N,1"))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portOpen(portType, "8,N,1"))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portOpen(portType, "参数配置"))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portOpen(portType, ""))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portOpen(portType, null))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//开启串口后，其他接口做异常参数测试
		if((ret=nlBluetooth.portOpen(portType, "115200,8,N,1"))!=NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:串口打开失败", Tools.getLineInfo());
		gui.cls_show_msg1(2, "打开串口后马上做写全0操作");
		if((ret=nlBluetooth.portWrite(portType, MAX_SIZE, buf))!=NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:写全0失败,ret=%d", Tools.getLineInfo(),ret);
		
		//portTxSendOver由于NDK接口直接返回0，无实际实现，故该接口不测试
		
		//portClose
		if((ret=nlBluetooth.portClose(-1))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portClose(12))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//portClrBuf
		if((ret=nlBluetooth.portClrBuf(-1))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portClrBuf(12))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//portReadLen
		if((ret=nlBluetooth.portReadLen(portType,null))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portReadLen(-1,out))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portReadLen(12,out))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//portRead
		if ((ret = nlBluetooth.portRead(-1, MAX_SIZE, 30, out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(12, MAX_SIZE, 30, out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, -1, 30,out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, 0, 30, out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, OVER_SIZE, 30, out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, MAX_SIZE, -1, out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, MAX_SIZE, 0, out, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, MAX_SIZE, 0, null, outBuf)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if ((ret = nlBluetooth.portRead(portType, MAX_SIZE, 0, out, null)) == NDK_OK) 
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//portWrite
		gui.cls_show_msg1(2, "经过其他接口异常测试后，做写全0操作");
		if((ret=nlBluetooth.portWrite(portType, MAX_SIZE, buf))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:写全0失败,ret=%d", Tools.getLineInfo(),ret);
			return;
		}
		if((ret=nlBluetooth.portWrite(-1, MAX_SIZE, buf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portWrite(12, MAX_SIZE, buf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portWrite(portType, -1, buf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portWrite(portType, 0, buf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portWrite(portType, OVER_SIZE, buf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.portWrite(portType, MAX_SIZE, null))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//case2：流程场景异常测试，串口未打开
		gui.cls_show_msg1(2, "扫描枪USB口未打开，流程异常测试...");
		nlBluetooth.portClose(portType);
		
		if((ret=nlBluetooth.portWrite(portType, BUFSIZE_SERIAL, buf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:写数据失败（%d）", Tools.getLineInfo(),ret);
		
		gui.cls_show_msg1(30,"请在30秒内扫条形码,按任意键继续");
		//portTxSendOver判断指定串口发送缓冲区是否为空,由于NDK接口直接返回0，无实际实现，故该接口不测试
		
		// 取输入缓存,最大4095,所以循环取出
		if ((ret = nlBluetooth.portReadLen(portType, outReceiveLen2)) == NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr, "line %d:USB串口取缓存区数据失败（%d）", Tools.getLineInfo(), ret);
		
		// 读数据（接收数据）
		Arrays.fill(rbuf, (byte) 0);
		if((ret=nlBluetooth.portRead(portType, BUFSIZE_SERIAL, MAXWAITTIME, outReceiveLen1, rbuf))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:USB串口读数据失败（%d）", Tools.getLineInfo(),ret);
		
		//清USB串口缓存
		if((ret=nlBluetooth.portClrBuf(portType))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"line %d:USB串口清缓存失败（%d）", Tools.getLineInfo(),ret);
		
		//测试后置
		gui.cls_show_msg1_record(TAG, "BluetoothBase5", gKeepTimeErr,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		gui=null;
	}

}

