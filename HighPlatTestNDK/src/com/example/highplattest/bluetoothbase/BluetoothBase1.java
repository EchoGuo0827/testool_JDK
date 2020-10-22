package com.example.highplattest.bluetoothbase;

import java.util.ArrayList;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.control.BluetoothController;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

/************************************************************************
 * 
 * module 			: N910wifi蓝牙底座模块
 * file name 		: BluetoothBase1.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20180226
 * directory 		: 
 * description 		: 蓝牙底座的以太网模块
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess			   20180226	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class BluetoothBase1 extends UnitFragment
{
	
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "以太网模块";
	public final String TAG = BluetoothBase1.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private BluetoothController nlBluetooth ;
	private EthernetPara ethernetPara = new EthernetPara();
	// 蓝牙
	private BluetoothManager bluetoothManager;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private Config config;
	public void bluetoothbase1()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		nlBluetooth = BluetoothController.getInstance();	
		int ret=-1;
		int tcp;
		byte[] sendByte={0x38,0x38,0x38,0x38,0x38,0x38,0x38,0x38,0x38,0x38};
		byte[] sendByte0=new byte[10];
		byte[] recByte=new byte[10];
		StringBuffer sendLen=new StringBuffer();
		StringBuffer recLen=new StringBuffer();
		StringBuffer tcpHandle=new StringBuffer();
		StringBuffer ipAddr=new StringBuffer();
		StringBuffer gateway=new StringBuffer();
		StringBuffer subnetMask=new StringBuffer();
		StringBuffer dns=new StringBuffer();
		StringBuffer baiduIpAddr=new StringBuffer();
		StringBuffer isConnected=new StringBuffer();
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
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line %d:连接蓝牙失败（%d）,长按确认退出", Tools.getLineInfo(),ret);
			return;
		}
		config.netTransConfig(ethernetPara);
		gui.cls_show_msg("请确保连接以太网，完成任意键继续");
		//case1：参数异常测试
		//case1.1 设置以太网MAC地址
		gui.cls_show_msg1(1,"参数异常测试,耗时较久...");
		if((ret=nlBluetooth.ethSetMacAddr(null))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置以太网MAC地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.ethSetMacAddr(""))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置以太网MAC地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.ethSetMacAddr("12345"))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置以太网MAC地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.2 静态设置网络地址
		if(nlBluetooth.ethSetAddress(null, null, null,null)!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,静态设置网络地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if(nlBluetooth.ethSetAddress("", "", "","")!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,静态设置网络地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if(nlBluetooth.ethSetAddress("1", "2", "3","4")!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,静态设置网络地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.3 解析域名
		if((ret=nlBluetooth.getDnsIp(null, baiduIpAddr))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,解析域名参数异常测试失败(%d,%s)", Tools.getLineInfo(),ret,baiduIpAddr.toString());
		}
		if((ret=nlBluetooth.getDnsIp("", baiduIpAddr))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,解析域名参数异常测试失败(%d,%s)", Tools.getLineInfo(),ret,baiduIpAddr.toString());
		}
		if((ret=nlBluetooth.getDnsIp("0123", baiduIpAddr))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,解析域名参数异常测试失败(%d,%s)", Tools.getLineInfo(),ret,baiduIpAddr.toString());
		}
		if((ret=nlBluetooth.getDnsIp("aaaa", baiduIpAddr))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,解析域名参数异常测试失败(%d,%s)", Tools.getLineInfo(),ret,baiduIpAddr.toString());
		}
		//case1.4 ping
		if((ret=nlBluetooth.netPing(null))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,ping操作参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netPing(""))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,ping操作参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netPing("0123"))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,ping操作参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.5 绑定本地IP地址
		//先打开TCP通讯通道
		if((ret=nlBluetooth.tcpOpen(tcpHandle))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,打开TCP通讯通道参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		tcp=Integer.parseInt(tcpHandle.toString());
		//开始参数异常
		if((ret=nlBluetooth.tcpBind(-1, "192.168.34.11",(short)8888))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,绑定本地IP地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpBind(tcp, null,(short)8888))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,绑定本地IP地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpBind(tcp, "",(short)8888))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,绑定本地IP地址参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.6 连接服务器
		if((ret=nlBluetooth.tcpConnect(-1, "218.66.48.230",(short)3459, 15000))!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpConnect(tcp, "0123",(short)3459, 15000))!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpConnect(tcp, null,(short)3459, 15000))!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpConnect(tcp, "",(short)3459, 15000))!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpConnect(tcp, "218.66.48.230",(short)-1, 15000))!=-5110)//-5110 - TCP远程端口错误
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpConnect(tcp, "218.66.48.230",(short)3459, -1))!=NDK_ERR_PARA)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.7 发送数据
		if((ret=nlBluetooth.tcpWrite(-1, sendByte.length, sendByte, 15000, sendLen))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,发送数据参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpWrite(tcp, -1, null, 15000, sendLen))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,发送数据失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpWrite(tcp, -1, sendByte0, 15000, sendLen))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,发送数据失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpWrite(tcp, sendByte.length, sendByte, -1, sendLen))!=NDK_ERR_TCP_SEND){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,发送数据参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.8接收数据
		if((ret=nlBluetooth.tcpRead(-1, 10, 15000, recLen, recByte))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,接收数据参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpRead(tcp, 10, -1, recLen, recByte))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,接收数据参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.tcpRead(tcp,-1, 15000, null, null))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,接收数据参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.9 DNS域名解析通讯接口
		if((ret=nlBluetooth.netDnsResolv(-1,"www.baidu.com", baiduIpAddr))!=-404)//-404 - 未知通讯方式类型
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,DNS域名解析通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netDnsResolv(0,null, baiduIpAddr))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,DNS域名解析通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netDnsResolv(0,"", baiduIpAddr))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,DNS域名解析通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.10 设置数据转发使用的通讯接口
		if((ret=nlBluetooth.netAddRouterTable(-1,"218.66.48.230"))!=-404)//-404 - 未知通讯方式类型
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置数据转发使用的通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netAddRouterTable(0,null))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置数据转发使用的通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netAddRouterTable(0,""))!=-405)//-405 - 无效IP字符串
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置数据转发使用的通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netAddRouterTable(0,"0123"))!=-405)//-405 - 无效IP字符串
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置数据转发使用的通讯接口参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//case1.11设置DNS
		if((ret=nlBluetooth.netSetDns(-1,"8.8.8.8"))!=-404)//-404 - 未知通讯方式类型
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置DNS参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netSetDns(0,null))!=NDK_ERR_PARA){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置DNS参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netSetDns(0,""))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置DNS参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		if((ret=nlBluetooth.netSetDns(0,"0123"))!=NDK_ERR){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置DNS参数异常测试失败(%d)", Tools.getLineInfo(),ret);
		}
		
		//关闭tcp
		if((ret=nlBluetooth.tcpReset(tcp))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,关闭tcp失败(%d)", Tools.getLineInfo());
			return;
		}
		
		//case2：流程场景异常测试
		//case2.1 未连接网线，应能检测到未连接，并且设置MAC失败、设置、获取网络地址应失败，解析域名、PING百度应失败，打开tcp应失败
		gui.cls_show_msg1(1,"流程异常测试...");
		gui.cls_show_msg("请确未连接以太网，完成任意键继续");
		if((ret=nlBluetooth.ethGetConnected(isConnected))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,获取网线是否连接失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!isConnected.toString().equals("0")){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,网线连接状态获取失败(%s)", Tools.getLineInfo(),isConnected.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		// 断开以太网
		gui.cls_show_msg1(2, "断开以太网");
		if ((ret = nlBluetooth.ethDisConnect()) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"WifiApEthPre",gScreenTime, "line %d:断开以太网失败(%d)",Tools.getLineInfo(), ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//设置网络的mac地址应失败
		if((ret=nlBluetooth.ethSetMacAddr("11:22:33:44:55:66"))==NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,设置以太网MAC地址参数失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
				return;
		}
		if(ethernetPara.isDHCPenable()){
			//动态
			//使用DHCP获取网络地址
			if((ret=nlBluetooth.netDHCP())==NDK_OK){
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,使用DHCP获取网络地址失败(%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		else{
			//静态	 "192.168.30.1;192.168.30.4;192.168.30.5"	
			ret=nlBluetooth.ethSetAddress(ethernetPara.getLocalIp(), ethernetPara.getNetMask(), ethernetPara.getGateWay(),ethernetPara.getDns1());
			if(ret == NDK_OK){
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,静态设置网络地址失败(%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		//域名解析
		gui.cls_show_msg1(1, "域名解析百度地址");
		if((ret=nlBluetooth.getDnsIp("www.baidu.com", baiduIpAddr))==NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,解析域名失败(%d,%s)", Tools.getLineInfo(),ret,baiduIpAddr.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.v("域名", baiduIpAddr.toString());
		
		//ping
		gui.cls_show_msg1(1, "ping百度地址");
		if((ret=nlBluetooth.netPing("14.215.177.37"))==NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,ping操作失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.2满足前置网络条件后，未打开tcp，后续的绑定服务器，连接服务器，发送、接收数据均应失败
		gui.cls_show_msg("请确保连接以太网，完成任意键继续");
		if((ret=nlBluetooth.ethGetConnected(isConnected))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,获取网线是否连接失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!isConnected.toString().equals("1")){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,网线连接状态获取失败(%s)", Tools.getLineInfo(),isConnected.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		if(ethernetPara.isDHCPenable()){
			//动态
			//使用DHCP获取网络地址
			if((ret=nlBluetooth.netDHCP())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,使用DHCP获取网络地址失败(%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//获取网络地址
			if((ret=nlBluetooth.ethGetNetAddr(ipAddr,gateway,subnetMask , dns))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,获取网络地址失败(%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		else{
			//静态	 "192.168.30.1;192.168.30.4;192.168.30.5"	
			ret=nlBluetooth.ethSetAddress(ethernetPara.getLocalIp(), ethernetPara.getNetMask(), ethernetPara.getGateWay(),ethernetPara.getDns1());
			if(ret!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,静态设置网络地址失败(%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//获取网络地址
			if((ret=nlBluetooth.ethGetNetAddr(ipAddr,gateway ,subnetMask , dns))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,获取网络地址失败(%d)", Tools.getLineInfo(),ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//校验
			if(!ipAddr.toString().equals(ethernetPara.getLocalIp()) || !subnetMask.toString().equals( ethernetPara.getNetMask())
					|| !gateway.toString().equals(ethernetPara.getGateWay()) || !dns.toString().equals(ethernetPara.getDns1()))
			{
				gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,获取网络地址错误(%s,%s,%s,%s)", Tools.getLineInfo(),ipAddr,gateway,subnetMask,dns);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		//通讯 打开TCP通讯通道
		gui.cls_show_msg1(1, "打开TCP通讯通道");
		if((ret=nlBluetooth.tcpOpen(tcpHandle))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,打开TCP通讯通道失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		tcp=Integer.parseInt(tcpHandle.toString());
		//关闭TCp
		gui.cls_show_msg1(1, "关闭TCP通讯通道");
		if((ret=nlBluetooth.tcpReset(tcp))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,关闭tcp失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//绑定本地IP地址
		gui.cls_show_msg1(1, "绑定本地IP地址");
		if((ret=nlBluetooth.tcpBind(tcp, ipAddr.toString(),(short)8888))==NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,绑定本地IP地址失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//连接服务器
		gui.cls_show_msg1(1, "连接服务器");
		if((ret=nlBluetooth.tcpConnect(tcp, ethernetPara.getServerIp(),(short)ethernetPara.getServerPort(), 15000))==NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,连接服务器失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//发送数据
		gui.cls_show_msg1(1, "发送数据");
		if((ret=nlBluetooth.tcpWrite(tcp, sendByte.length, sendByte, 15000, sendLen))==NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,发送数据失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//接收数据
		gui.cls_show_msg1(1, "接收数据");
		if((ret=nlBluetooth.tcpRead(tcp, 10, 15000, recLen, recByte))==NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,接收数据失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//关闭TCp
		if((ret=nlBluetooth.tcpReset(tcp))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "BluetoothBase1", gKeepTimeErr,"line:%d,关闭tcp失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//测试后置
		// 关闭蓝牙
		nlBluetooth.disconnect();
		gui.cls_show_msg1_record(TAG, "BluetoothBase1", gScreenTime,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() 
	{
		
	}
	@Override
	public void onTestDown() {
		gui = null;
	}
}

