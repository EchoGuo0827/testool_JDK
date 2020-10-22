package com.example.highplattest.bluetoothbase;

import java.util.ArrayList;
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
 * file name 		: BluetoothBase6.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180226
 * directory 		: 
 * description 		: 蓝牙底座的蓝牙、pos模块
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180226	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class BluetoothBase6 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "蓝牙、POS模块";
	public final String TAG = BluetoothBase6.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private BluetoothController nlBluetooth;
	StringBuffer outInfo = new StringBuffer();
	private Config config;
	private int ret=-1;
	// 蓝牙
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
		
	public void bluetoothbase6()
	{
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase6",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*process body*/
		nlBluetooth = BluetoothController.getInstance();	
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		config = new Config(myactivity, handler);
		
		gui.cls_show_msg1(2, TESTITEM+"测试中,配置蓝牙。。。");
		config.btConfig(pairList, unPairList, bluetoothManager);
		if(unPairList.size()==0)	{
			gui.cls_show_msg1(2, "未搜索到蓝牙底座");
			dialog.dismiss();
			return;	
		}	
		// 测试前置
		//case1：参数异常测试
		gui.cls_show_msg1(2, TESTITEM+"参数异常测试中。。。");
		//connect
		gui.cls_show_msg1(2, "0");
		if(nlBluetooth.startBluetoothConnA(null,null))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		gui.cls_show_msg1(2, "1");
		if(nlBluetooth.startBluetoothConnA("",""))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		gui.cls_show_msg1(2, "3");
		if(nlBluetooth.startBluetoothConnA("~!@#$%^&*()_+","~!@#$%^&*()_+"))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		gui.cls_show_msg1(2, "2");
		if(nlBluetooth.startBluetoothConnA("qw:er:Ty:ui:op:LK","qw:er:Ty:ui:op:LK"))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		gui.cls_show_msg1(2, "4");
		if(nlBluetooth.startBluetoothConnA("中文","中文"))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		// 连接蓝牙,MAC地址getBtAddr()
		gui.cls_show_msg1(2, "3");
		if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())){
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr, "line %d:连接蓝牙失败（%d）", Tools.getLineInfo(), ret);
			return;
		}
		
		//sysGetPosInfo
		if((ret=nlBluetooth.sysGetPosInfo(-1, outInfo))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.sysGetPosInfo(14, outInfo))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.sysGetPosInfo(0, null))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());

		//btGetMacAddr
		if((ret=nlBluetooth.btGetMacAddr(null))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		
		//btSetLocalName
		if((ret=nlBluetooth.btSetLocalName(null))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.btSetLocalName(""))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败ret=%d", Tools.getLineInfo(),ret);
		if((ret=nlBluetooth.btSetLocalName("~!@#$%^&*{}:|<>?[];''()_+"))!=NDK_OK)//特殊符号
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败ret=%d", Tools.getLineInfo(),ret);
		if((ret=nlBluetooth.btSetLocalName("蓝牙底座名称最大为29个字节;蓝牙底座名称最大为29个字节;字节"))==NDK_OK)//不超过29字节
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:参数异常测试失败ret=%d", Tools.getLineInfo(),ret);

		//case2：流程场景异常测试，蓝牙底座断开后，其他接口应失败
		nlBluetooth.disconnect();
		
		if((ret=nlBluetooth.sysGetPosInfo(0, outInfo))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:流程异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.btGetMacAddr(outInfo))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:流程异常测试失败", Tools.getLineInfo());
		if((ret=nlBluetooth.btSetLocalName("流程异常蓝牙名称"))==NDK_OK)
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:流程异常测试失败ret=%d", Tools.getLineInfo(),ret);
		if(!((nlBluetooth.getConnectedDeviceAddressA()).equals("")))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:流程异常测试失败", Tools.getLineInfo());
		if(!((nlBluetooth.getConnectedDeviceNameA()).equals("")))
			gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"line %d:流程异常测试失败", Tools.getLineInfo());
		
		//测试后置
		gui.cls_show_msg1_record(TAG, "BluetoothBase6", gKeepTimeErr,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui=null;
	}
}

