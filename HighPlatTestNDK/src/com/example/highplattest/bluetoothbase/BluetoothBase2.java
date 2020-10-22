package com.example.highplattest.bluetoothbase;

import java.util.ArrayList;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.control.BluetoothController;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

/************************************************************************
 * 
 * module 			: N910wifi蓝牙底座模块
 * file name 		: BluetoothBase2.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20180226
 * directory 		: 
 * description 		: 蓝牙底座的wifiAp模块
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess			   20180226	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class BluetoothBase2 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "wifiAp模块";
	public final String TAG = BluetoothBase2.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private BluetoothController nlBluetooth;
	// 蓝牙
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private Config config;
	public void bluetoothbase2()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase2",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		nlBluetooth = BluetoothController.getInstance();
		int ret=-1;	
		StringBuffer ssid = new StringBuffer();
		StringBuffer mSum = new StringBuffer();	//黑白名单个数(输出参数)
		StringBuffer outInfo = new StringBuffer();//黑白名单列表(输出参数)
		/*process body*/
		
		gui.cls_show_msg1(2, TESTITEM+"测试中。。。");
		// 测试前置
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		config = new Config(myactivity, handler);
		gui.cls_show_msg("确保蓝牙底座已连接电源，任意键开始配置蓝牙");
		config.btConfig(pairList, unPairList, bluetoothManager);
		if(unPairList.size()==0)	{
			gui.cls_show_msg1(1,"未搜索到蓝牙底座,长按确认退出");
			dialog.dismiss();
			return;	
		}
		gui.cls_show_msg1(1,"连接蓝牙...");
		if (!nlBluetooth.startBluetoothConnA(getBtName(),getBtAddr())){ 
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:连接蓝牙失败（%d）,长按确认退出", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1(1, "初始化wifi");
		if ((ret = nlBluetooth.wifiApInit("192.168.2.1", "255.255.255.0")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:初始化wifi出错(%d)", Tools.getLineInfo(), ret);
			return;
		}
		if ((ret = nlBluetooth.wifiApGetSsid(ssid)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:获取wifi名称出错(%d,%s)", Tools.getLineInfo(), ret,ssid.toString());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case1：参数异常测试
		gui.cls_show_msg1(1,"参数异常测试...");
		//case1.1wifiAP初始化
		if ((ret = nlBluetooth.wifiApInit(null, "255.255.255.0")) != NDK_ERR_PARA) { 
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:初始化wifi出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApInit("192.168.2.1",null)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:初始化wifi出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApInit("","")) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:初始化wifi出错(%d)", Tools.getLineInfo(), ret);
		}
		//case1.2 设置ap信息
		if ((ret = nlBluetooth.wifiApSetInfo("", "","", "8.8.8.8",6)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo(null, "","", "8.8.8.8",6)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("newland", "","", null,6)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("newland", "","", "",6)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("newland", "","12345", "",6)) != NDK_ERR_WIFI_INVDATA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("newland", "","", "8.8.8.8",-1)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("newland", null,null, "8.8.8.8",6)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi密码、名称、dns、信道出错(%d)",Tools.getLineInfo(), ret);
		}
		//case1.3 修改wifi名称 新密码和旧密码为空 则修改名称
		if ((ret = nlBluetooth.wifiApSetInfo(null, "", "")) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi名称出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("", "", "")) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi名称出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo("~!@#$%^&*{}:|<>?[];''()_+", "", "")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi名称出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetInfo(ssid.toString(), "", "")) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:修改wifi名称出错(%d)", Tools.getLineInfo(), ret);
		}
		//case1.4修改信道
		if ((ret = nlBluetooth.wifiApSetChannel(-1)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置信道出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetChannel(14)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置信道出错(%d)", Tools.getLineInfo(), ret);
		}
		//case1.5 修改DNS
		if ((ret = nlBluetooth.wifiApSetDns(null)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置DNS出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetDns("")) != NDK_ERR_WIFI_INVDATA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置DNS出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetDns("12345")) != NDK_ERR_WIFI_INVDATA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置DNS出错(%d)", Tools.getLineInfo(), ret);
		}
		//case1.6 设置是否隐藏 0-不隐藏 1-隐藏
		if ((ret = nlBluetooth.wifiApSetHideSsid(-1)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置是否隐藏出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetHideSsid(2)) != NDK_ERR_PARA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置是否隐藏出错(%d)", Tools.getLineInfo(), ret);
		}
		//case1.7 修改加密模式
		if ((ret = nlBluetooth.wifiApSetWpa(-1)) != NDK_ERR_WIFI_INVDATA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置加密模式出错(%d)", Tools.getLineInfo(), ret);
		}
		if ((ret = nlBluetooth.wifiApSetWpa(4)) != NDK_ERR_WIFI_INVDATA) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置加密模式出错(%d)", Tools.getLineInfo(), ret);
		}
		//case1.8获取黑白名单列表 0白名单,1黑名单
		if((ret=nlBluetooth.wifiApGetBlackWhiteList(-1,967,mSum,outInfo))!=NDK_ERR_WIFI_INVDATA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:获取黑名单出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApGetBlackWhiteList(0,-1,mSum,outInfo))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:获取黑名单出错(%d)",Tools.getLineInfo(),ret);
		}
		//case1.9设备黑白名单功能
		if((ret=nlBluetooth.wifiApSetBlackList(-1,0,1,"11:22:33:44:55:66"))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:添加MAC到白名单出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApSetBlackList(0,-1,1,"11:22:33:44:55:66"))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:添加MAC到白名单出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApSetBlackList(0,0,-1,"11:22:33:44:55:66"))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:添加MAC到白名单出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApSetBlackList(0,0,1,"12345"))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:添加MAC到白名单出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApSetBlackList(0,0,1,""))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:添加MAC到白名单出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApSetBlackList(0,0,0,null))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:添加MAC到白名单出错(%d)",Tools.getLineInfo(),ret);
		}
		//case1.10设置过滤模式为禁止黑名单里的设备上网 0黑名单,1白名单，2关闭
		if((ret=nlBluetooth.wifiApSetFilter(-1))!=NDK_ERR_WIFI_INVDATA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置过滤模式为禁止黑名单里的设备上网出错(%d)",Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.wifiApSetFilter(3))!=NDK_ERR_WIFI_INVDATA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"line %d:设置过滤模式为禁止黑名单里的设备上网出错(%d)",Tools.getLineInfo(),ret);
		}
		//case2：流程场景异常测试
		//与潘浩确认，wifiAP的各种设置与获取均是文件操作，即使不初始化也可以调用成功，故暂无流程异常
		
	
		//测试后置
		gui.cls_show_msg1_record(TAG, "BluetoothBase2", gKeepTimeErr,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		gui = null;
	}
}

