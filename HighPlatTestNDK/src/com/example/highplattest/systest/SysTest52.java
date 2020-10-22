package com.example.highplattest.systest;

import java.lang.reflect.Method;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.WifiApBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Create;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest52.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150611
 * directory 		: 
 * description 		: wifi ap综合的测试用例
 * related document : 
 * history 		 	: 变更记录													变更时间			变更人员
 *			  		  修改wifiAP封装方法,增加参数判断显示不同提示语,区分打开和关闭。		   20200426	 		陈丁
 *					 修复7.1机器无法获取到ssid和密码的问题							  20200617         	陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest52 extends DefaultFragment
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest52.class.getSimpleName();
	private final String TESTITEM = "Wifi_Ap性能、压力";
	
	/*----------global variables declaration------------------------*/
//	 private WifiApBean WifiApSetting = new WifiApBean();
	 private EthernetPara ethernetPara = new EthernetPara();
	 private MobilePara mobilePara = new MobilePara();
	 private WifiManager wifiManager = null;
	 private Gui gui = null;
	 
	public void systest52() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 获取wifi管理服务
		wifiManager = (WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE);
		initLayer();
		while(true)
		{
			int returnValue=gui.cls_show_msg("AP综合测试\n0.Ap压力测试\n1.Ap异常测试\n2.Ap参数设置\n3.共享网络设置");
			switch (returnValue) 
			{
			case '0':
				try {
					wifiAp();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
				
			case '1':
				try 
				{
					apAbnormal();
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
				break;
				
			case '2':
				new Config(myactivity,handler).confWifiAp(WifiApSetting);
				break;
				
			case '3':
				new Config(myactivity,handler).confConnAp(WifiApSetting, ethernetPara, mobilePara);
				break;
				
			case ESC:
				intentSys();
				return;

			}
		}
	}
	
	public void wifiAp() 
	{
		/*private &local definition*/
		boolean apcommFlag = false;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中", TESTITEM);
		while(true)
		{
			int returnValue=gui.cls_show_msg("0.打开Ap共享网络\n1.关闭Ap共享网络\n2.设置Ap数通标志");
			switch (returnValue) 
			{
			case '0':
				openWifiAp(apcommFlag);
				return;

			case '1':
				closeWifiAp();
				return;

			case '2':
				if (gui.ShowMessageBox("是否进行Ap本地数据收发测试".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)==BTN_OK)
					apcommFlag = true;
				else
					apcommFlag = false;
				break;

			default:
				gui.cls_show_msg1(2, "退出测试程序前先进行WIFI AP的关闭");
				break;
			}
		}
	}
	
	// add by 20150612
	public void apAbnormal() 
	{
		/*private & local definition*/
		
		/*process body*/
		int returnValue=gui.cls_show_msg("0.多台设备接入\n1.休眠测试\n2.长时间静止\n3.断电或重启");
		switch (returnValue) 
		{
		case '0':
			handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "正在打开Ap，在Ap打开成功界面使用多台设备接入Ap，均应能正常数据传输", 5);
			break;
			
		case '1':
			handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "正在打开Ap，在Ap打开成功界面等待Ap进入休眠，休眠唤醒后使用其他设备连接Ap应该能正常数据传输", 5);
			break;
			
		case '2':
			handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "正在打开Ap，在Ap打开成功界面长时间放置（至少30分钟）后使用其他设备连接Ap应该能够正常数据传输", 5);
			break;
			
		case '3':
			handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "正在打开Ap，在Ap打开成功界面使用其他设备连接Ap后将机器重启或断电后再进行Ap正常测试应该成功", 5);
			break;

		default:
			break;
		}
		// 打开wifi ap
		openWifiAp(false);
		handlerShowTime(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "关闭Ap共享网络中", 2);
		// 关闭wifi ap
		closeWifiAp();
	}
	
	public void openWifiAp(boolean flag) 
	{
		/*private & local definition*/
		boolean back = false;
		LinkType type = null;
		Object object = null;
		SocketUtil socketUtil = null;
		Sock_t sock_t = null;
		
		/*process body*/
		// 先关闭wifiap
		
		setWifiApEnabled(false, WifiApSetting.getWifiApSsid(), WifiApSetting.getWifiApKey(), WifiApSetting.getWifiApSecMode(), WifiApSetting.isWifiApHidden(),false);
		
		if(WifiApSetting.getWifiApShareDev() == Wifi_Ap_Create.ETH)
		{
			gui.cls_show_msg("确保以太网已可连接上网，完成任意键继续");
			type = LinkType.ETH;
			object = ethernetPara;
			socketUtil = new SocketUtil(ethernetPara.getServerIp(), ethernetPara.getServerPort());
			sock_t = ethernetPara.getSock_t();
		}
		else if(WifiApSetting.getWifiApShareDev() == Wifi_Ap_Create.WLM)
		{
			gui.cls_show_msg("确保移动数据网络已打开！！！，完成任意键继续");
			type = LinkType.CDMA;
			object = mobilePara;
			socketUtil = new SocketUtil(mobilePara.getServerIp(), mobilePara.getServerPort());
			sock_t = mobilePara.getSock_t();
		}
		if(layerBase.netUp( object, type)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "openWifiAp",g_keeptime, "line %d:NetUp失败", Tools.getLineInfo());
			return;
		}
		
		// 打开wifi ap
		if(!(back = setWifiApEnabled(true,WifiApSetting.getWifiApSsid(),WifiApSetting.getWifiApKey(),WifiApSetting.getWifiApSecMode(),WifiApSetting.isWifiApHidden(),true)))
		{
			gui.cls_show_msg1_record(TAG, "openWifiAp",g_keeptime,  "line %d:%s失败ret=%d", Tools.getLineInfo(),TAG,back);
			return;
		}
		
		gui.cls_show_msg1(2, "WIFI AP打开成功\nAPSSID:%s\nAPKEY:%s\n", WifiApSetting.getWifiApSsid(),WifiApSetting.getWifiApKey());
		gui.cls_show_msg("点任意键继续");
		Log.e("apFlag", flag+"");
		if(flag)
		{
			if(layerBase.transUp(socketUtil, sock_t)!=NDK_OK)
			{
				layerBase.netDown(socketUtil, object, sock_t, type);
				gui.cls_show_msg1_record(TAG, "openWifiAp",g_keeptime, "line %d:TransUp失败", Tools.getLineInfo());
				return;
			}
			send_recv_press(socketUtil, type, (NetWorkingBase) object);
			layerBase.transDown(socketUtil, sock_t);
		}
		gui.cls_show_msg("wifi ap打开成功\nAPSSID:%s\nAPKEY:%s\n，点任意键退出", WifiApSetting.getWifiApSsid(),WifiApSetting.getWifiApKey());
	}
	
	// 关闭wifi的操作
	public void closeWifiAp() 
	{
		/*private & local definition*/
		boolean back = false;
		LinkType type = null;
		Object object = null;
		
		/*process body*/
		//关闭WIFI热点
		if(!(back = setWifiApEnabled(false, WifiApSetting.getWifiApSsid(), WifiApSetting.getWifiApKey(), WifiApSetting.getWifiApSecMode(),WifiApSetting.isWifiApHidden(),false)))
		{
			gui.cls_show_msg1_record(TAG, "closeWifiAp",g_keeptime, "line %d:%s失败ret=%d", Tools.getLineInfo(),TAG,back);
			return;
		}
		if(WifiApSetting.getWifiApShareDev() == Wifi_Ap_Create.ETH)
		{
			type = LinkType.ETH;
			object = ethernetPara;
		}
		else if(WifiApSetting.getWifiApShareDev() == Wifi_Ap_Create.WLM)
		{
			type = mobilePara.getType();
			object = mobilePara;
		}
		layerBase.netDown(null, object, null, type);
		gui.cls_show_msg("WIFI AP关闭成功，点任意键退出");
		return;
	}
	
	// 判断wifi ap的状态
	public boolean isWifiApEnabled() 
	{
		return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
	}

	private WIFI_AP_STATE getWifiApState()
	{
		int tmp;
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			tmp = ((Integer) method.invoke(wifiManager));
			// Fix for Android 4
			if (tmp > 10) {
				tmp = tmp - 10;
			}
			return WIFI_AP_STATE.class.getEnumConstants()[tmp];
		} catch (Exception e) {
			e.printStackTrace();
			return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
		}
	}

	public enum WIFI_AP_STATE 
	{
		WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
	}
}
