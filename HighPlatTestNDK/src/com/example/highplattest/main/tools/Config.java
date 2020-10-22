package com.example.highplattest.main.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.main.adapter.ViewAdapter;
import com.example.highplattest.main.bean.AnuoParaBean;
import com.example.highplattest.main.bean.ApplicationExceptionBean;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.WifiApBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.WIFI_SEC;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Create;
import com.example.highplattest.main.constant.ParaEnum.Wifi_Ap_Enctyp;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.netutils.WifiUtil;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: main
 * file name 		: Config.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 修改通讯配置信息
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("ValidFragment")
public class Config implements NDK,Lib
{
	private IntentActivity activity;
	private WifiUtil wifiUtil;
	private final String TAG = Config.class.getSimpleName();
	Gui gui;
	
	public Config(Context context,Handler handler)
	{	
		wifiUtil=WifiUtil.getInstance(context,handler);
		this.activity = (IntentActivity) context;
		gui = new Gui(context, handler);
	}
	
	public int confLink(ParaEnum.LinkType type)
	{
		/*private & local definition*/
		// int ret=-1;
		
		/*process body*/
		// 先设置下通讯类型
		switch (type) 
		{
		// 暂无需配置，直接返回SUCC
		case ETH:
			return GlobalVariable.SUCC;
		// 需要配置的都放到此行，再次确认后，转各自处理
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case ASYN:
		// 同步参数
		case SYNC:
			// 配置链路参数
			moduleConfig();
			break;
			
		case SERIAL:
		case BT:
		case WLAN:
			break;
		default:
			return GlobalVariable.FAIL;
		}
		return GlobalVariable.SUCC;
		
	}
	
	/**
	 * 配置SD/U盘/TF卡
	 * @return
	 */
	public String confSDU() 
	{
		while(true)
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				String diskString = activity.getParaInit().getDiskType();
				
				return diskString;
			}
			int nkeyIn = gui.cls_show_msg("SD/U盘配置\n0.U盘\n1.内部SD卡\n2.外部TF卡\n");
			switch (nkeyIn) 
			{
			case '0':
				gui.cls_show_msg1(2, "选择设备为U盘");
				return "UDISK";
				
			case '1':
				gui.cls_show_msg1(2, "选择设备为内部SD卡");
				return "SDDSK";
				
			case '2':
				gui.cls_show_msg1(2, "选择设备为外部TF卡");
				return "TFDSK";
				
			case ESC:
				gui.cls_show_msg1(2, "选择设备为内部SD卡（默认）");
				return "SDDSK";
			default:
				break;
			}
		}
	}
	
	// IC/SAM的配置(交叉)
	public List<EM_ICTYPE> conf_icsam() 
	{
		List<EM_ICTYPE> icSamList=new ArrayList<EM_ICTYPE>();
		while(true)
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				String icSamString= activity.getParaInit().getIcSamType();
				String[] icSam=icSamString.split(",");
				LoggerUtil.e("string size:"+icSam.length);
				for(String s:icSam){
					if (s.equals("IC")) {
						icSamList.add(EM_ICTYPE.ICTYPE_IC);
					}
//					if (s.equals("IC2")) {
//						icSamList.add(EM_ICTYPE.ICTYPE_IC);
//					}
					if (s.equals("SAM1")) {
						icSamList.add(EM_ICTYPE.ICTYPE_SAM1);
					}
					if (s.equals("SAM2")) {
						icSamList.add(EM_ICTYPE.ICTYPE_SAM2);
					}
				}
				return icSamList;
			}
			int nkeyIn = gui.cls_show_msg("IC/SAM配置\n0.IC卡\n1.SAM1卡\n2.SAM2卡\n");
			switch (nkeyIn) 
			{
			case '0':
				icSamList.add(EM_ICTYPE.ICTYPE_IC);
				return icSamList;
				
			case '1':
				icSamList.add(EM_ICTYPE.ICTYPE_SAM1);
				return icSamList;
			case '2':
				icSamList.add(EM_ICTYPE.ICTYPE_SAM2);
				return icSamList;
		
			default:
				gui.cls_show_msg1(2,"选择为IC卡(默认)");
				icSamList.add(EM_ICTYPE.ICTYPE_IC);
				return icSamList;
			}
		}
	}
	
	/****************************************************************
	* function name 	 		: input()
	* functional description 	: 显示配置参数的对话框，进行参数配置
	* input parameter	 		:
	* output parameter	 		: 
	* return value		 		:
	* history 		 			: author			date			remarks
	*			  				  zhengxq		   20141027		    created
	*****************************************************************/
	// 以太网配置弹框
	public void netTransConfig(final Object object) 
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().setConfigNetTrans(activity, (NetWorkingBase) object);
			}
		});
		synchronized (object) {
			try {
				object.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void moduleConfig() 
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().showTip(activity);
			}
		});
		synchronized (activity) {
			try {
				activity.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int config_para()
	{
		final Object lockObj = new Object();
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				conf_conn_MDM(lockObj);
			}
		});
		synchronized (lockObj) {
			try {
				lockObj.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		switch (GlobalVariable.RETURN_VALUE+1) 
		{
		case 1:
		case 2:
			confLink(ModemBean.type_MDM);
			break;
			
		case 3:
			GlobalVariable.RETURN_VALUE = NDK_ERR_QUIT;
			break;

		default:
			break;
		}
		return GlobalVariable.RETURN_VALUE;
	}
	
//	public void show_config_conn_MDM() 
//	{
//		mActivity.runOnUiThread(new Runnable() 
//		{
//			@Override
//			public void run() 
//			{
//				conf_conn_MDM(mActivity, mInstrumentation);
//				if(GlobalVariable.IS_THREAD_OVER)
//				{
//					Log.e("IS_THREAD_OVER1", GlobalVariable.IS_THREAD_OVER+"");
//					return;
//				}
//					
//			}
//		});
//		mInstrumentation.waitForIdleSync();
//	}
	
	public void conf_conn_MDM(final Object object)
	{
		/*process body*/
		final ParaEnum.LinkType[] type = {LinkType.SYNC,LinkType.ASYN};
		// 选择拨号连接方式，用适配器
		//String[] str = {"同步MODEM","异步MODEM","返回"};
		ArrayList<String> str=new ArrayList<String>();
		str.add("同步MODEM");
		str.add("异步MODEM");
		str.add("返回");
		String title = "选择连接方式";
		final Dialog dialog = new Dialog(activity);
		ListView listView = new ListView(activity);
		listView.setBackgroundColor(Color.WHITE);
		listView.setDividerHeight(0);
		ViewAdapter listViewAdapter = new ViewAdapter(str,activity);
		listView.setAdapter(listViewAdapter);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(listView);
		dialog.setTitle(title);
		listView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				dialog.dismiss();
				Log.e("position", position+"");
				GlobalVariable.RETURN_VALUE = position;
				if(position<=1)
				{
					ModemBean.type_MDM = type[position];
				}
				synchronized (object) {
					object.notify();
				}
			}
		});
		GlobalVariable.RETURN_VALUE = GlobalVariable.SUCC;
		dialog.show();
	}
	
	/**
	 * 波特率选择
	 * @param activity
	 * @param handler
	 * @return 返回波特率的值
	 */
	public int testBps1()
	{
		while(true)
		{
			// 进行波特率的选择
			int nkeyIn = gui.cls_show_msg("波特率选择\n0.300  1.1200  2.1400\n3.4800  4.9600  5.19200\n6.38400  7.57600  8.115200\n9.230400\n");
			int[] bps = { 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400 };
			switch (nkeyIn) 
			{
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return BpsBean.bpsValue = bps[nkeyIn-'0'];
				
			case ESC:
				return BpsBean.bpsValue = 300;

			default:
				break;
			}
		}
	}


	// 射频卡的配置
	public _SMART_t rfid_config()
	{
		while(true)
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				return activity.getParaInit().getRfType();
			}
			
			String  rfChip = NlBuild.VERSION.NL_HARDWARE_CONFIG.substring(12,14);
			LoggerUtil.v("rfid_config="+rfChip);
			/**非接芯片是1608的不支持felica*/
			_SMART_t[] type = {_SMART_t.CPU_A,_SMART_t.CPU_B,_SMART_t.MIFARE_1,_SMART_t.ISO15693,_SMART_t.FELICA,_SMART_t.MIFARE_0,_SMART_t.MIFARE_0_C};
			int nkeyIn = gui.cls_show_msg("射频卡配置\n0.CPU_A卡\n1.CPU_B卡\n2.M1卡\n3.ISO15693卡\n4.FELICA\n5.MIFARE_0\n6.MIFARE_0_C");
			switch (nkeyIn) 
			{
			case '0':
			case '1':
			case '2':
			case '3':
			case '5':
			case '6':
				return type[nkeyIn-'0'];
				
			case '4':
				if(rfChip.equals("02")||rfChip.equals("03"))
				{
					gui.cls_show_msg("非接芯片1608不支持Felica，请重新选择");
					break;
				}
				return type[nkeyIn-'0'];
			case ESC:
				gui.cls_show_msg1(2,"将设置为CPU_A（默认）");
				return type[0];

			default:
				break;
			}

		}
	}
	public int felica_config(){
		while(true)
		{
			int felicaReturnValue=gui.cls_show_msg("felica选择\n0.普通felica\n1.八达通felica\n");
			switch (felicaReturnValue) {
			case '0':
				return 0;
			case '1':
				return 1;
			default:
				break;
			}
			return 0;
		}
	}
	/**
	 * 设置IC/SAM卡
	 * @param handler
	 * @return
	 */
	public _SMART_t icSam_config()
	{
		while(true)
		{
			_SMART_t[] type = {_SMART_t.IC, _SMART_t.SAM1, _SMART_t.SAM2};
			int nkeyIn = gui.cls_show_msg("0.IC卡\n1.SAM卡\n2.SAM2卡\n");
			switch (nkeyIn) {
			case '0':
			case '1':
			case '2':
				return type[nkeyIn-'0'];
				
			case ESC:
				gui.cls_show_msg1(2, "将设置为IC卡（默认）");
				return _SMART_t.IC;
			}
		}
	}
	
	public List<_SMART_t> smart_config()
	{
		List<_SMART_t> typeList=new ArrayList<_SMART_t>();
		while(true)
		{
			// 自动化配置
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				String typeString = activity.getParaInit().getSmartType();
				String[] typeArray=typeString.split(",");
				for(String s:typeArray){
					if (s.equals("IC")) 
						typeList.add(_SMART_t.IC);
					if (s.equals("SAM1")) 
						typeList.add(_SMART_t.SAM1);
					if (s.equals("SAM2")) 
						typeList.add(_SMART_t.SAM2);
					if (s.equals("CPU_A")) 
						typeList.add(_SMART_t.CPU_A);
					if (s.equals("CPU_B")) 
						typeList.add(_SMART_t.CPU_B);
					if (s.equals("MIFARE_1")) 
						typeList.add(_SMART_t.MIFARE_1);
					if (s.equals("ISO15693")) 
						typeList.add(_SMART_t.ISO15693);
					if(s.equals("MIFARE_0"))
						typeList.add(_SMART_t.MIFARE_0);
					if(s.equals("FELICA"))
						typeList.add(_SMART_t.FELICA);
				}
				return typeList;
//				return type;
			}
		
			// 修改为动态编码方式，改善原先的硬编码方式
			StringBuffer strBuffer = new StringBuffer();
			List<_SMART_t> type = new ArrayList<ParaEnum._SMART_t>();
			type.add(_SMART_t.CPU_A);
			type.add(_SMART_t.CPU_B);
			type.add(_SMART_t.MIFARE_1);
			type.add(_SMART_t.ISO15693);
			type.add(_SMART_t.MIFARE_0);
			type.add(_SMART_t.FELICA);
			type.add(_SMART_t.MIFARE_0_C);
			for (EM_ICTYPE typeIC: GlobalVariable.cardNo) {
				switch (typeIC) 
				{
				case ICTYPE_IC:
					strBuffer.append(type.size()+".IC卡\n");
					type.add(_SMART_t.IC);
					
					break;
					
				case ICTYPE_SAM1:
					strBuffer.append(type.size()+".SAM1卡\n");
					type.add(_SMART_t.SAM1);
					break;
					
				case ICTYPE_SAM2:
					strBuffer.append(type.size()+".SAM2卡\n");
					type.add(_SMART_t.SAM2);
					break;
					
				default:
					break;
				}
			}
			
			int nkeyIn = gui.cls_show_msg("SMART卡配置\n0.CPU_A卡\n1.CPU_B卡\n2.M_1卡\n3.ISO15693卡\n4.M0卡\n5.felica卡\n6.M0带C卡\n%s", strBuffer.toString());
			switch (nkeyIn) 
			{
			default:
				if((nkeyIn-'0')<type.size())
				{
					gui.cls_show_msg1(1, "设置为%s卡", type.get(nkeyIn-'0'));
					typeList.add(type.get(nkeyIn-'0'));
					return typeList;
				}
				else
					break;

			case ESC:
				gui.cls_show_msg1(2, "将设置为CPU_A(默认)");
				typeList.add(_SMART_t.CPU_A);
				return typeList;
			}
		}
	}
	
	/**
	 * NFC卡配置
	 * @param handler
	 * @param title
	 * @return
	 */
	public Nfc_Card nfc_card_config()
	{
		while(true)
		{
			// 自动设置类型
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				Nfc_Card nfc_Card = activity.getParaInit().getNfcType();
				return nfc_Card;
			}
		
			Nfc_Card[] type = {Nfc_Card.NFC_A,Nfc_Card.NFC_B,Nfc_Card.NFC_M1};
			int nkeyIn = gui.cls_show_msg("NFC卡配置\n0.NFC_A卡\n1.NFC_B卡\n2.NFC_M1卡\n");
			switch (nkeyIn) 
			{
			case '0':
			case '1':
			case '2':
				return type[nkeyIn-'0'];
				
			case ESC:
				gui.cls_show_msg1(2, "将设置为NFC_A(默认)...");
				return Nfc_Card.NFC_A;
				
			default:
				break;
			}
		}
	}
	
	//20150324
	public int confConnLAN(WifiPara wifiPara,EthernetPara ethernetPara) 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			wifiPara.setType(LinkType.WLAN);
			AnuoParaBean paraInit = activity.getParaInit();
			wifiPara.setDHCPenable(paraInit.isWifiDHCPenable());
			wifiPara.setGateWay(paraInit.getWifiLocalGateWay());
			wifiPara.setLocalIp(paraInit.getWifiLocalIP());
			wifiPara.setServerIp(activity.getParaInit().getWifiSvrIP());
			wifiPara.setServerPort(activity.getParaInit().getWifiSvrPort());
//			wifiPara.setLocalPort(sysTest.getParaInit().getWifiSvrPort());
			wifiPara.setNetMask(paraInit.getWifiLocalMask());
			wifiPara.setPasswd(paraInit.getWlanPwd());
			wifiPara.setSsid(paraInit.getWlanSsid());
			wifiPara.setSock_t(paraInit.getSockType());
			// 添加sec的配置 add 20161216
			wifiPara.setSec(paraInit.getWlanSec());
			GlobalVariable.chooseConfig = 1;
			return NDK_OK;
		}
		int nkeyIn = gui.cls_show_msg("网络方式\n%s1.WLAN\n",GlobalVariable.gModuleEnable.get(Mod_Enable.EthEnable)?"0.ETH\n":"");
		GlobalVariable.chooseConfig = nkeyIn-'0';
		switch(nkeyIn)
		{
			case '0':
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.EthEnable)==false)
				{
					gui.cls_show_msg2(0.5f, "该产品不支持以太网通讯");
					break;
				}
				ethernetPara.setType(LinkType.ETH);
				return confChooseLan(ethernetPara);
				
			case '1':
				wifiPara.setDHCPenable(false);
				wifiPara.setType(LinkType.WLAN);
//				wifiUtil.startScan();
				return confConnWlan(wifiPara);
				
			default:
				break;
		
		}
		
		return NDK_OK;
	}
	
	// add by 20150327
	// 以太网的配置操作
	public int confChooseLan(final EthernetPara ethernetPara)
	{
		/*process body*/
		//手工配置链路参数
		if(confLink(LinkType.ETH) != NDK_OK)
			return NDK_ERR;
	
//		gui.cls_show_msg("请确保打开TCP/UDP后台!!!，完成任意键继续");
		
		//手工配置网络参数
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().setConfigNetTrans(activity, (NetWorkingBase) ethernetPara);
			}
		});
		synchronized (ethernetPara) {
			try {
				ethernetPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return NDK_OK;
	}
	// end by 20150327
	
	public String[] wifi_config()
	{
		String[] wifiConfig = {"12345","12345678"};
		
		return wifiConfig;
	}
	
	// add by 20150414
	// wifi配置
	public int confConnWlan(final WifiPara wifiPara)
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			wifiPara.setType(LinkType.WLAN);
			AnuoParaBean paraInit = activity.getParaInit();
			wifiPara.setDHCPenable(paraInit.isWifiDHCPenable());
			wifiPara.setGateWay(paraInit.getWifiLocalGateWay());
			wifiPara.setLocalIp(paraInit.getWifiLocalIP());
			wifiPara.setServerIp(activity.getParaInit().getWifiSvrIP());
			wifiPara.setServerPort(activity.getParaInit().getWifiSvrPort());
			wifiPara.setSec(activity.getParaInit().getWlanSec());
//			wifiPara.setLocalPort(sysTest.getParaInit().getWifiSvrPort());
			wifiPara.setNetMask(paraInit.getWifiLocalMask());
			wifiPara.setPasswd(paraInit.getWlanPwd());
			wifiPara.setSsid(paraInit.getWlanSsid());
			wifiPara.setSock_t(paraInit.getSockType());
			return NDK_OK;
		}
		
		// 开启wifi
		wifi_Open_Close(wifiUtil,WIFI_STATE_ENABLED);
		//清除wifi以保存的列表,需要在wifi打开后清楚才有效果
//		wifiUtil.clearAllSavaWifi();
		if(wifiPara.isInput_way()==false)// 自动方式要开启扫描
		{
			gui.cls_printf("正在扫描wifi中，请耐心等待...".getBytes());
			wifiUtil.startScan(wifiPara);
			wifiUtil.registWifiScan();
		
		}
		// 配置链路参数
		if(confLinkR(LinkType.WLAN, wifiPara)!= NDK_OK)
		{
			LoggerUtil.d("Config->confConnWlan exit");
			return NDK_ERR;
		}
			
		
		// 设置wifi的ssid
//		gui.cls_show_msg("请确保打开TCP/UDP后台！！！，完成任意键继续");
		// 配置网络参数
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().setConfigNetTrans(activity, (NetWorkingBase) wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return NDK_ERR;
		// 关闭wifi
		wifi_Open_Close(wifiUtil, WIFI_STATE_DISABLED);
		return NDK_OK;
	}
	// end by 20150414
	
	// 以太网
	public int confConnEth(final EthernetPara ethernetPara)
	{
		// 配置链路参数
		if(confLink(LinkType.ETH)!=NDK_OK)
			return NDK_ERR;
		gui.cls_show_msg("请确保打开TCP/UDP后台，只需配置服务器参数，完成任意键继续");
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().setConfigNetTrans(activity, (NetWorkingBase) ethernetPara);
			}
		});
		synchronized (ethernetPara) {
			try {
				ethernetPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return NDK_ERR;
		return NDK_OK;
		
	}
	
	// 无线配置
	public int confConnWLM(boolean isCnncted,MobilePara mobilePara)
	{
//		/*private & local definition*/
//		MobileUtil mobileUtil = MobileUtil.getInstance(mActivity);
//		TelephonyManager mTelephonyManager  = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
//		
//		/*process body*/
//		mobileUtil.openMobile();
//		mobileUtil.closeOther();
//		long startTime = System.currentTimeMillis();
//		while(Tools.getStopTime(startTime)<20)
//		{
//			SystemClock.sleep(5000);
//			if(MobileUtil.isNetworkAvailable(mActivity))
//			{
//				break;
////				try 
////				{
////					new Gui().cls_show_msg1(10, "%s,line %d:无线初始化失败（%s）", TAG,getLineInfo(),"false");
////				} catch (IOException e) 
////				{
////					e.printStackTrace();
////				}
//			}
//			else
//				SystemClock.sleep(5000);
//		}
//		typeSign = mTelephonyManager.getNetworkType();
//		Log.e("config", typeSign+"");
//		mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//		while(wlmType!=null)
//		{
//			mobilePara.setType(wlmType);
//			mobilePara.setSignStrength(signStren);
//		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			AnuoParaBean paraInit = activity.getParaInit();
			mobilePara.setServerIp(paraInit.getWlmSvrIP());
			mobilePara.setServerPort(paraInit.getWlmSvrPORT());
			return NDK_OK;
		}
		
		if(confLinkR(LinkType.GPRS,mobilePara)!=NDK_OK)
			return NDK_ERR;
		
		if(isCnncted)
		{
			gui.cls_show_msg("请确保打开TCP/UDP后台!!!完成任意键继续");
		}
		return NDK_OK;
			
	}
	
	public int confLinkR(LinkType type,final Object object)
	{
		/*private & local definition*/
		
		/*process body*/
		switch (type) 
		{
		case ETH:
			
			return NDK_OK;
			
		case GPRS:
		case WCDMA:
		case CDMA:
		case TD:
		case ASYN:
		case SYNC:
		case SERIAL:
		case BT:
		case WLAN:
			switch (type) 
			{
			case GPRS:
			case WCDMA:
			case CDMA:
			case TD:
				// 显示配置对话框，刷新UI操作
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						new ShowDialog().set_wire_config(activity, (MobilePara) object);
					}
				});
				synchronized (object) {
					try {
						object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(GlobalVariable.RETURN_VALUE!=GlobalVariable.SUCC)
					return NDK_ERR;
				break;
				
			case WLAN:
				// 前提是已经进行wifi_AP的扫描操作
				final WifiPara wifiPara = (WifiPara) object;
				if(wifiPara.isInput_way()==true)// 手动输入
				{
					// 显示对话框操作
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new ShowDialog().inputApMsg(activity, (WifiPara) wifiPara);
						}
					});
					synchronized (wifiPara) {
						try {
							wifiPara.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				else if(wifiPara.isInput_way()==false)
				{
					LoggerUtil.d(TAG+",confLinkR===scan wifi...");
					synchronized (wifiPara) {
						try {
							wifiPara.wait(60*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					wifiUtil.unRegistWifiBroadCast();
					List<ScanResult> scanResult = new ArrayList<ScanResult>();
					if(wifiUtil.getWifiList()==null)
					{
						gui.cls_show_msg("未扫描到任何AP,任意键退出");
						return NDK_ERR;
					}
					scanResult.addAll(wifiUtil.getWifiList());
					// 显示对话框操作
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							new ShowDialog().setConfWlanInk(activity,  (List<ScanResult>)  wifiUtil.getWifiList());
						}
					});
					synchronized (activity) {
						try {
							activity.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(GlobalVariable.RETURN_VALUE!=GlobalVariable.SUCC)
						return NDK_ERR;
					wifiPara.setSsid(scanResult.get(GlobalVariable.Position).SSID);
					String sec = wifiUtil.getWifiList().get(GlobalVariable.Position).capabilities; //描述了身份验证、密钥管理和访问点支持的加密方案
					//动态连接wifi设置安全模式
					ParaEnum.WIFI_SEC wifiSec = null;
					Log.d("eric_chen", "设置安全模式----sec=="+sec);
					Log.d("eric_chen", "gCustomerID=="+GlobalVariable.gCustomerID);
					//海外版-欧洲版需求安全模式禁止NONE和WEP模式。 20200811
					if (GlobalVariable.gCustomerID==CUSTOMER_ID.overseas&&GlobalVariable.currentPlatform==Model_Type.N910) {
							wifiSec = WIFI_SEC.WPA;
					}
					else {
						Log.d("eric_chen", "非欧洲版------"+sec);
						if(sec.contains("WPA")){
							Log.d("eric_chen", "设置安全模式----WPA");
							wifiSec = WIFI_SEC.WPA;
						}
						else if(sec.contains("WEP"))
						{
							Log.d("eric_chen", "设置安全模式----WEP");
							wifiSec = WIFI_SEC.WEP;
						}
						else{
							Log.d("eric_chen", "设置安全模式----NOPASS");
							wifiSec = WIFI_SEC.NOPASS;
						}
					}
				
						
					wifiPara.setSec(wifiSec);
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							new ShowDialog().wifiPassword(activity, (WifiPara) wifiPara);
						}
					});
					synchronized (wifiPara) {
						try {
							wifiPara.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if(GlobalVariable.RETURN_VALUE!=GlobalVariable.SUCC)
					return NDK_ERR;
				break;
				
			case SERIAL:
				// 串口配置波特率操作
				testBps1();
				break;
				
			default:
				break;
			}
		default:
			break;
		}
		return NDK_OK;
	}
	public final void set_wifi_excelsheet(final Object object) 
	{
		final WifiPara wifiPara = (WifiPara) object;
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().wifisheet(activity, (WifiPara) wifiPara);
			}
		});
		synchronized (wifiPara) {
			try {
				wifiPara.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	// add by 20150227
	// 磁卡参数配置
	public final void set_mag_config(final PacketBean packet,Activity activity) 
	{
		final Dialog dialog = new Dialog(activity);
		dialog.setTitle("磁卡参数配置");
		dialog.setCanceledOnTouchOutside(false);
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.mag_config, null);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = GlobalVariable.ScreenWidth; // 宽度
		lp.height = GlobalVariable.ScreenHeight / 2; // 高度
		dialogWindow.setAttributes(lp);

		// 设置的磁卡测试张数  每张卡测试次数
		final EditText et_card_numbers = (EditText) view.findViewById(R.id.et_card_number);
		final EditText et_times = (EditText) view.findViewById(R.id.et_test_times);
		RadioGroup dataGroup = (RadioGroup) view.findViewById(R.id.group_mag_data);
		RadioButton tk123Button = (RadioButton) view.findViewById(R.id.rb_123_data);
		RadioButton tk23Button = (RadioButton) view.findViewById(R.id.rb_23_data);
		RadioGroup dataChoose = (RadioGroup) view.findViewById(R.id.group_display);
		RadioButton displayButton = (RadioButton) view.findViewById(R.id.rb_display);
		RadioButton noDisplayButton = (RadioButton) view.findViewById(R.id.rb_no_display);
		Button btnSure = (Button) view.findViewById(R.id.btn_sure);
		
		// 设置默认的磁卡测试张数  每张卡测试次数
		et_card_numbers.setText(packet.getCard_number()+"");
		et_times.setText(packet.getLifecycle()+"");
		
		// 设置默认的选中状态
		switch (GlobalVariable.selTK) 
		{
		case TK1_2_3:
			tk123Button.setChecked(true);
			break;
			
		case TK2_3:
			tk23Button.setChecked(true);
			break;
		}
		
		// 设置默认的显示状态
		if(GlobalVariable.isdisp)
		{
			displayButton.setChecked(true);
		}
		else
		{
			noDisplayButton.setChecked(true);
		}
		
		// 控制显示23磁道还是123磁道
		dataGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
				{
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) 
					{
						// 将磁卡上次设置的数据保存下来
						if (checkedId == R.id.rb_123_data) 
						{
							GlobalVariable.selTK = TK1_2_3;
						} else if (checkedId == R.id.rb_23_data) 
						{
							GlobalVariable.selTK = TK2_3;
						}
					}
				});
		// 控制磁道数据显示与否
		dataChoose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
				{

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) 
					{
						if (checkedId == R.id.rb_display) 
						{
							GlobalVariable.isdisp = true;
						} else if (checkedId == R.id.rb_no_display) 
						{
							GlobalVariable.isdisp = false;
						}
					}
				});
		// 设置每张卡的刷卡次数以及总共要刷几张卡
		btnSure.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				if (et_times.getText().length() != 0) 
				{
					dialog.dismiss();
					// 设置每张卡刷卡次数
					packet.setLifecycle(Integer.parseInt(et_times.getText().toString()));
					// 设置总共要刷几张卡
					packet.setCard_number(Integer.parseInt(et_card_numbers.getText().toString()));
					synchronized (packet) {
						packet.notify();
					}
				}
			}

		});
//		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOME_KEY_EVENT);
//		dialog.setOnKeyListener(ShowDialog.keylistener);
		dialog.show();
	}
	// end add 20150227
	/**
	 * 多种验签参数设置  by zhangxj 20170303
	 */
	public void showScanConfig(final int values)
	{
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().ScanDialog(activity,values);
			}
		});
		synchronized (activity) {
			try {
				activity.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	// add by 20150612
	// wifi ap的配置
	public int confConnAp(WifiApBean WifiApSetting, EthernetPara ethernetPara,MobilePara mobilePara)  
	{
		/* private & local definition */
		ParaEnum.Wifi_Ap_Create[] tip = { Wifi_Ap_Create.ETH,Wifi_Ap_Create.WLM };

		/* process body */
		// 1.WiFi热点接入的网络方式选择
		int	nkeyIn = gui.cls_show_msg("0.ETH方式\n1.WLM方式\n"); 
		switch (nkeyIn) {
		case '0':
		case '1':
			WifiApSetting.setWifiApShareDev(tip[nkeyIn-48]);
			gui.cls_show_msg1(2, "将设置Wifi Ap接入方式为%s",WifiApSetting.getWifiApShareDev());
			break;

		default:
			gui.cls_show_msg1(2, "将使用Wifi Ap计入方式为%s",WifiApSetting.getWifiApShareDev());
			return NDK_ERR;
		}


		// 根据选择的共享网络方式设置链路层
		if (WifiApSetting.getWifiApShareDev() == Wifi_Ap_Create.ETH) {
			// 以太网配置
			switch (confConnEth(ethernetPara)) 
			{
			case NDK_OK:
				gui.cls_show_msg1(2, "网络配置成功");
				break;

			case NDK_ERR:
				gui.cls_show_msg1(2,"line %d:网络未连通", Tools.getLineInfo());
				return NDK_ERR;

			case NDK_ERR_QUIT:
			default:
				break;
			}
		} else if (WifiApSetting.getWifiApShareDev() == Wifi_Ap_Create.WLM) 			
		{
			Log.e("type", mobilePara.getType() + "");
			// wlm配置
			switch (confConnWLM(true, mobilePara)) {
			case NDK_OK:
				gui.cls_show_msg1(2, "网络配置成功");
				break;

			case NDK_ERR:
				gui.cls_show_msg1(2,"网络未连通");
				return NDK_ERR;
				
			default:
				break;
			}
		}
		return NDK_OK;
	}
	
	// add by 20150612
	public void confWifiAp(final WifiApBean WifiApSetting)  
	{
		/* private & local definition */
		ParaEnum.Wifi_Ap_Enctyp[] tip = { Wifi_Ap_Enctyp.WIFI_NET_SEC_WEP_OPEN,
				Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA,
				Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA2 };

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {    
			 if(gui.cls_show_msg("请到设置中配置热点，任意键继续，[取消]退出")==ESC)
               return ;
		}else
		{
			// 加密模式选择
			int nkeyIn = gui.cls_show_msg("0.OPEN\n1.WPA\n2.WPA2\n");
			switch (nkeyIn) 
			{
			case '0':
			case '1':
			case '2':
				WifiApSetting.setWifiApSecMode(tip[nkeyIn-48]);
				gui.cls_show_msg1(2, "将设置%s加密模式",WifiApSetting.getWifiApSecMode());
				break;
	
			default:
				gui.cls_show_msg1(2, "将使用%s加密模式",WifiApSetting.getWifiApSecMode());
				break;
			}
			// 写死相应长度的密码
			if (WifiApSetting.getWifiApSecMode() == Wifi_Ap_Enctyp.WIFI_NET_SEC_WEP_OPEN)
				WifiApSetting.setWifiApKey("0000");
			if (WifiApSetting.getWifiApSecMode() == Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA
					|| WifiApSetting.getWifiApSecMode() == Wifi_Ap_Enctyp.WIFI_NET_SEC_WPA2)
				WifiApSetting.setWifiApKey("0123456789");
	
			// 设置wifi的SSid
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					new ShowDialog().setWifiApSsid(activity, (WifiApBean) WifiApSetting);
				}
			});
			synchronized (WifiApSetting) {
				try {
					WifiApSetting.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(GlobalVariable.RETURN_VALUE!=SUCC){
			 return;
			}
			// 设置是否隐藏ssid
			nkeyIn = gui.cls_show_msg("AP设置\n0.不隐藏\n1.隐藏\n");
			switch (nkeyIn) {
			case 0:
				WifiApSetting.setWifiApHidden(false);
				gui.cls_show_msg1(1, "将设置为SSID不隐藏");
				break;
	
			case 1:
				WifiApSetting.setWifiApHidden(true);
				break;
	
			case 2:
				WifiApSetting.setWifiApHidden(false);
				gui.cls_show_msg1(1, "将设置为SSID不隐藏");
				break;
	
			default:
				break;
			}
		}

	}
	
	public int wifi_Open_Close(WifiUtil wifiUtil,int preStatus)
	{
		int ret = NDK_OK;
		long startTime;
		// 先关闭其他网络后后再进行打开wifi的操作
		wifiUtil.closeOther();
		if(preStatus==WIFI_STATE_DISABLED){
			Log.d("eric_chen", "closeNet");
			wifiUtil.closeNet();
		}
		else{ 
			Log.d("eric_chen", "openNet");
			wifiUtil.openNet();
		}
		ret = wifiUtil.checkState();
		startTime = System.currentTimeMillis();
		// 循环检测wifi是否打开成功
		if (ret != preStatus) {
			SystemClock.sleep(1000);
			Log.d("eric_chen", "ret=="+ret);
			// 正在打开wifi
			if(preStatus==WIFI_STATE_DISABLED)
			{	
				Log.d("eric_chen", "preStatus1----");
				if ((ret = wifiUtil.checkState()) == WIFI_STATE_DISABLING) {
					Log.d("eric_chen", "preStatus2----");
					while (Tools.getStopTime(startTime) < 20) {
						if ((ret = wifiUtil.checkState()) == preStatus)
							break;
					}
				}
			}
			else
			{
				Log.d("eric_chen", "checkState1----");
				if ((ret = wifiUtil.checkState()) == WIFI_STATE_ENABLING||(ret = wifiUtil.checkState()) == WIFI_STATE_DISABLED) {
					Log.d("eric_chen", "checkState2----");
					while (Tools.getStopTime(startTime) < 20) {
						if ((ret = wifiUtil.checkState()) == preStatus)
							break;
					}
				}
			}
			Log.d("eric_chen", "ret=="+ret+"preStatus=="+preStatus);
			if (ret != preStatus) 
			{
				gui.cls_show_msg1(10,"%s,line %d:wifi操作失败(%d)", TAG,Tools.getLineInfo(), ret);
			}
		}
		return ret;
	}
	
	
	
	/**
	 * 打印灰度设置
	 * @return 打印设置灰度值
	 */
	public int print_config()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			JniNdk.JNI_Print_SetGrey(activity.getParaInit().getPrinterDentisty());
			return activity.getParaInit().getPrinterDentisty();
		}
		
		final int[] grey = new int[1];
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().set_print_density(activity, grey);
			}
		});
		synchronized (grey) {
			try {
				grey.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		int grey = gui.JDK_ReadData(10, 3);
		if(grey[0]>5 || grey[0]<0){
			gui.cls_show_msg1(2, "打印浓度输入不合法，灰度将设置为3");
			JniNdk.JNI_Print_SetGrey(3);
			return 3;
		}
		JniNdk.JNI_Print_SetGrey(grey[0]);
		return grey[0];
	}
	
	public void Bt_Mul_Config(final StringBuffer macAddr)
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().set_bt_mac(activity, macAddr);
			}
		});
		synchronized (macAddr) {
			try {
				macAddr.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	Dialog btDialog;
	// 蓝牙配置操作
	public void btConfig(ArrayList<BluetoothDevice> pairList,final ArrayList<BluetoothDevice> unPairList,BluetoothManager bluetoothManager) 
	{
		gui.cls_printf("正在进行蓝牙扫描，请稍后...".getBytes());
		pairList.clear();
		unPairList.clear();
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
		bluetoothAdapter.enable();
		/**配置的时候移除原先已连接的蓝牙吗，全部变为未配对的状态*/
		SystemClock.sleep(2000);
		Set<BluetoothDevice> pairedDevices = bluetoothManager.queryPairedDevices();
		for (BluetoothDevice device : pairedDevices) {
			//内置打印机清除掉后需要重新开启蓝牙才会重新出现在已配对的设备中
			if(!device.getName().equals("内置打印机")){
				try {
					ClsUtils.removeBond(device.getClass(), device);
					SystemClock.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//修改成搜索完毕再显示列表 zhangxinj 20180319
		try {
			bluetoothDetect(bluetoothManager,unPairList,12);
		} catch (ApplicationExceptionBean e1) {
			e1.printStackTrace();
		}
		// 显示蓝牙列表
		activity.runOnUiThread(new Runnable() {
				
			@Override
			public void run() {
				btDialog=new ShowDialog().showBtList(activity,unPairList);
			}
		});
		synchronized (activity) {
			try {
					activity.wait(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		btDialog.dismiss();
		LoggerUtil.e("bt config end");
		bluetoothManager.unRegist();// 蓝牙注销操作
	}
	
//	// add by 20150417
//	// 蓝牙设备的检测
//	public int detectBTDevice(BluetoothManager bluetoothManager)
//	{
//		try 
//		{
//			bluetoothDetect(bluetoothManager, 12);
//		} catch (ApplicationExceptionBean e) 
//		{
////			e.printStackTrace();
//			return NDK_BT_DETECT_FAILED;
//		}
//		return NDK_OK;
//	}
	
	// 蓝牙检测功能
	public void bluetoothDetect(BluetoothManager bluetoothManager,ArrayList<BluetoothDevice> unPairList, int enableTime) throws ApplicationExceptionBean {
		try {
			BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
			Set<BluetoothDevice> bluetoothDevices;
			if(bluetoothAdapter.isEnabled()){
				bluetoothManager.regist();
				bluetoothDevices = bluetoothManager.queryFoundedDevice(unPairList);
				if(bluetoothDevices == null)
				{
					throw new ApplicationExceptionBean(DEVICE_MODULE_DETECT_EXCEPTION, "未检测到蓝牙设备");
				}
			}else{
				throw new ApplicationExceptionBean(DEVICE_MODULE_DETECT_EXCEPTION, "蓝牙启动失败");
			}
			
		} catch (Exception e) {
			throw new ApplicationExceptionBean(DEVICE_MODULE_DETECT_EXCEPTION, e.getMessage());
		} 
	}
	
	// 对back键和home键进行监听
	static OnKeyListener keylistener = new DialogInterface.OnKeyListener() 
	{

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
		{
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
			{
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_HOME) 
			{
				Log.e("dialoghome", "home");
				return true;
			} else {
				return false;
			}
		}
	};

	public int M0_config() {
		// TODO Auto-generated method stub
		while(true)
		{
			int M0ReturnValue=gui.cls_show_msg("M0选择\n0.M0卡不带C\n1.M0卡带C\n");
			switch (M0ReturnValue) {
			case '0':
				return 0;
			case '1':
				return 1;
			default:
				break;
			}
			return 0;
		}
	}
}
