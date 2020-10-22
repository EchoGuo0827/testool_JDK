package com.example.highplattest.systest;

import java.text.SimpleDateFormat;
import java.util.Locale;
import android.content.Context;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.telephony.TelephonyManager;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PushNoticeBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.LinkStatus;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netty.HeartBeatsClient;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.LayerBase;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest76.java
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180207
 * directory 		: 
 * description 		: 推送延迟测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj			20171220	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest76 extends DefaultFragment
{
	private Gui gui = null;
	private  String TESTITEM = "网络推送延迟稳定性测试";
	private final String TAG = "SysTest76";
	private PushNoticeBean pushNotice;
	SocketUtil socketUtil;
	private WifiPara wifiPara;
	private MobilePara mobilePara;
	private Config config;
	private String FILE;
	private SettingsManager settingsManager;
	private String netInfo;

	public void systest76()
	{
		//测试时间长 不放在自动化测试
		if(GlobalVariable.gSequencePressFlag)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		pushNotice=new PushNoticeBean();
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddhhmmss",Locale.CHINA);
		String timeStr = time.format(new java.util.Date());
		FILE = GlobalVariable.sdPath+"NioPushNoticeTest"+timeStr+".txt";
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		initLayer();
	
		while(true)
		{
			int nKeyIn = gui.cls_show_msg("推送延迟测试\n1.配置\n2.推送测试\n");
			switch (nKeyIn) {
			case '1':
				config();
				break;
				
			case '2':
				pushTest();
				break;
				

			case ESC:
				intentSys();
				return;
			}
		}

	}
	public void config(){
	
		int nKeyIn = gui.cls_show_msg("网络配置\n1.无线\n2.Wifi\n");
		switch (nKeyIn) {
		case '1':
			if(configWLM()!=NDK_OK)
				return;
			break;
			
		case '2':
			if(confWifi()!=NDK_OK)
				return;
			break;
			
		case ESC:
			return;
		}
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().set_push_notice(myactivity,pushNotice);		
			}
		});
		synchronized (pushNotice) {
			try {
				pushNotice.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=GlobalVariable.SUCC){
			return;
		}
		settingsManager.setScreenTimeout(pushNotice.isSleep()==true?15000:-1);
		gui.cls_show_msg("1.请确保每次测试前，pos端时间与服务器端时间校准\n2.请使用测试【推送的服务器】\n3.测试过程中，如需充电，请勿接电脑USB充电，会导致无法进入休眠\n任意键继续");
		gui.cls_show_msg1(2, "配置已完成");
	}
	public int configWLM(){
		MobileUtil mobileUtil=MobileUtil.getInstance(myactivity,handler);
		mobileUtil.closeOther();
		if(mobileUtil.getSimState()!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "configWLM", g_keeptime,"line %d:未插入sim卡，请插入sim卡",Tools.getLineInfo());
			return NDK_ERR;
		}
		
		mobilePara=new MobilePara();
		setWireType(mobilePara);
		// 无线配置
		switch (config.confConnWLM(true,mobilePara)) 
		{
		case NDK_OK:
			socketUtil = new SocketUtil(mobilePara.getServerIp(),mobilePara.getServerPort());
			gui.cls_show_msg1(2, "无线网络配置成功!");
			break;
			
		case NDK_ERR:
			gui.cls_show_msg1_record(TAG, "configWLM", g_keeptime,"line %d:网络未接通！！！",Tools.getLineInfo());
			return NDK_ERR;
			
		case NDK_ERR_QUIT:
		default:
			break;
		}
		pushNotice.setServiceIp(mobilePara.getServerIp());
		pushNotice.setServicePort(mobilePara.getServerPort());

		netInfo=mobilePara.getType()==LinkType.GPRS? "2G":mobilePara.getType()==LinkType.CDMA? "3G":"4G";
		TelephonyManager mTelephonyManager  = (TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		pushNotice.setNetInfo(mTelephonyManager.getSimOperatorName()+netInfo);
		layerBase.netDown(socketUtil, mobilePara, Sock_t.SOCK_TCP,  mobilePara.getType());
		Layer.linkStatus =LinkStatus.linkdown;
		if(layerBase.netUp(mobilePara, mobilePara.getType())!= NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "configWLM", g_keeptime, "line %d:连接无线失败", Tools.getLineInfo());
			return NDK_ERR;
		}
		return NDK_OK;
	}
	public int confWifi(){
		wifiPara=new WifiPara();
		int nWlanInput = gui.cls_show_msg("Wlan配置\n0.Wlan手动输入\n1.Wlan自动扫描\n");
		switch (nWlanInput) 
		{
		case '0':
			wifiPara.setInput_way(true);
			break;
			
		case '1':
			wifiPara.setInput_way(false);
			break;
		default:
			break;
		}
		switch (new Config(myactivity,handler).confConnWlan(wifiPara)) 
		{
		case NDK_OK:
			socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
			gui.cls_show_msg1(1, "wlan参数配置完毕！！！");
			break;

		case NDK_ERR:
			return NDK_ERR;

		case NDK_ERR_QUIT:
		default:
			break;
		}
		pushNotice.setServiceIp(wifiPara.getServerIp());
		pushNotice.setServicePort(wifiPara.getServerPort());
		pushNotice.setNetInfo("wifi");
		layerBase.netDown(socketUtil, wifiPara, Sock_t.SOCK_TCP,  wifiPara.getType());
		Layer.linkStatus =LinkStatus.linkdown;
		if (layerBase.netUp(wifiPara, wifiPara.getType())!= SUCC) 
		{
			gui.cls_show_msg1_record(TAG,"confWifi",g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo());
			return NDK_ERR;
		}
		return NDK_OK;
	}
	public void pushTest() {
		try {
			new HeartBeatsClient(gui,myactivity, pushNotice, FILE).connect(pushNotice.getServicePort(), pushNotice.getServiceIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
