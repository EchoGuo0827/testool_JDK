package com.example.highplattest.main.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.example.highplattest.R;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.NlsPara;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.bean.WifiApBean;
import com.example.highplattest.main.bean.PushNoticeBean;
import com.example.highplattest.main.btutils.ClientAdapter;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.ParaEnum.WIFI_SEC;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiAdapter;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
/************************************************************************
 * module 			: main
 * file name 		: ShowDialog.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141027
 * directory 		: 
 * description 		: 测试工程中所有要显示的对话框都在这里
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("InflateParams")
public class ShowDialog implements NDK,Lib
{
	private final String  TAG = ShowDialog.class.getSimpleName();
	Button btnLeft,btnRight;
	int VIEW_COUNT = 6;//每页显示个数
	int ret;
	
	// 设计配置链路的对话框
	public void showTip(final Activity activity)
	{
		new BaseDialog(activity, R.layout.dialog_config,"同步拨号设置", "确定", "取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View View, boolean isPositive) {
				if(isPositive){
					confMdmLnk(activity);
				}
				
			}
		}).show();
		
		
	}

	// LCD综合参数配置
	public void show_times_press(Activity activity, final PacketBean packet) 
	{
		new BaseDialog(activity, R.layout.show_time_press, "LCD参数配置", "完成", "否", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				 EditText etCross = (EditText) view.findViewById(R.id.et_cross_times);
				 EditText etImg = (EditText) view.findViewById(R.id.et_img_times);
				if(isPositive){
					if (!etCross.getText().toString().equals("")&& !etImg.getText().toString().equals("")) {
						// 将输入的链路参数的值全部保存到LinkSetting里面
						Log.e("yes", "yes");
						packet.setShow_time(Integer.parseInt(etImg.getText()
								.toString()));
						packet.setLifecycle(Integer.parseInt(etCross.getText()
								.toString()));
					} else {
						// 设置默认值
						packet.setShow_time(2);
						packet.setLifecycle(100);
					}
					synchronized (packet) {
						packet.notify();
					}
				}
				
			}
		}).show();
		
	}
	
	// 同步Modem链路参数的配置
	public void confMdmLnk(final Activity activity)
	{
		new BaseDialog(activity, R.layout.main, "modem链路参数配置", "完成", "取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				EditText et1=(EditText) view.findViewById(R.id.et_dialog_patch);
				EditText et2= (EditText) view.findViewById(R.id.et_dial_num);
				if(isPositive){
					if(!et1.getText().toString().equals("")&&!et2.getText().toString().equals(""))
					{
						// 将输入的链路参数的值全部保存到LinkSetting里面
						ModemBean.MDMPatchType=Integer.parseInt(et1.getText().toString());
						ModemBean.MDMDialStr = et2.getText().toString();
						Log.e("yes", "yes");
						synchronized (activity) {
							activity.notify();
						}
					}
				}
			}
		}).show();
	
	}

	

	 // 对back键和home键进行监听
	static OnKeyListener keylistener = new DialogInterface.OnKeyListener() 
	{
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
		{
			if (keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0) 
			{
				return true;
            }
			else if(keyCode == KeyEvent.KEYCODE_HOME)
			{
				Log.e("dialoghome", "home");
				return true;
			}
			else
			{
				return false;
			}
		}
	};
	
	

	
	// add by 20150306
	// wifi选择
	public void setConfWlanInk(final Context context,final List<ScanResult> scanResult)
	{
		ListView listView = new ListView(context);
		listView.setBackgroundColor(Color.WHITE);
		WifiAdapter wifiAdapter = new WifiAdapter(context, scanResult);
		listView.setAdapter(wifiAdapter);
	
		new BaseDialog(context, listView, "wifiAp选择", "取消",new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View View, boolean isPositive) {
				synchronized (context) {
					context.notify();
				}
			}
			
		}).show();
		

	}
	
	// 输入wifi的密码 
	public void wifiPassword(Context context,final WifiPara wifiPara)
	{
		final EditText etPsw = new EditText(context);
		BaseDialog dialog=new BaseDialog(context,etPsw,"输入wifi密码","确定","取消",new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View View, boolean isPositive) {
				if(isPositive){
					wifiPara.setPasswd(etPsw.getText().toString());
					GlobalVariable.RETURN_VALUE=NDK_OK;
				}
				else// 点击了取消按键要退出当前流程 modify by zhengxq 20181130
				{
					GlobalVariable.RETURN_VALUE=NDK_ERR;
				}
				synchronized (wifiPara) {
					wifiPara.notify();
				}
			}
		});
		dialog.show();

	}
	
	
	// 输入sheet
	public void wifisheet(Context context,final WifiPara wifiPara)
	{
		final EditText etPsw = new EditText(context);
		BaseDialog dialog=new BaseDialog(context,etPsw,"输入wifi的ip地址","确定","取消",new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View View, boolean isPositive) {
				if(isPositive){
					wifiPara.setother24wifi((etPsw.getText().toString()));
					GlobalVariable.RETURN_VALUE=NDK_OK;
				}
				else// 点击了取消按键要退出当前流程 modify by zhengxq 20181130
				{
					GlobalVariable.RETURN_VALUE=NDK_ERR;
				}
				synchronized (wifiPara) {
					wifiPara.notify();
				}
			}
		});
		dialog.show();

	}
	
	
	// 手动输入AP的信息 
	public void inputApMsg(Context context,final WifiPara wifiPara)
	{
		
		boolean layoutflag=false;
//		//N910海外欧洲版隐藏另外两个按钮 20200811
//		if (GlobalVariable.gCustomerID==CUSTOMER_ID.overseas&&GlobalVariable.currentPlatform==Model_Type.N910) {
//			Log.d("eric_chen", "隐藏按钮---");
//			layoutid=R.layout.input_ap_msg_overseas;
//		}
		//代表海外产品 wifi隐藏安全模式
		if (GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false) {
			layoutflag=true;
		}
		BaseDialog dialog=new BaseDialog(layoutflag,context, R.layout.input_ap_msg,"输入AP信息","确定", "取消", new OnDialogButtonClickListener() {
			
			
	
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				LoggerUtil.d("positive:"+isPositive);
				if(isPositive){
					EditText et_SSID = (EditText) view.findViewById(R.id.et_ap_ssid);
					EditText et_PWD = (EditText) view.findViewById(R.id.et_ap_pwd);
					RadioGroup rg_sec = (RadioGroup) view.findViewById(R.id.rg_ap_sec);
					RadioGroup rg_ssid = (RadioGroup) view.findViewById(R.id.rg_ssid_broad);
					RadioButton rb_nopass=view.findViewById(R.id.rb_sec_nopass);
					RadioButton rb_wep=view.findViewById(R.id.rb_sec_wep);
					wifiPara.setSec(rg_sec.getCheckedRadioButtonId()==R.id.rb_sec_nopass?WIFI_SEC.NOPASS:
						rg_sec.getCheckedRadioButtonId()==R.id.rb_sec_wpa?WIFI_SEC.WPA:WIFI_SEC.WEP);
					Log.d("eric_chen", "当前手动选择---sec"+wifiPara.getSec());
					wifiPara.setScan_ssid(rg_ssid.getCheckedRadioButtonId()==R.id.rb_ssid_zero?false:true);
					wifiPara.setSsid(et_SSID.getText().toString());
					wifiPara.setPasswd(et_PWD.getText().toString());
					synchronized (wifiPara) {
						wifiPara.notify();
					}
				}
			}
		});
		dialog.show();
	}
	
	
	// add by 20150612
	// wifi ap的ssid设置
	public void setWifiApSsid(Context context, final WifiApBean WifiApSetting) 
	{
		final EditText etPsw = new EditText(context);
		new BaseDialog(context, etPsw, "输入wifi_ap的ssid", "确定", "取消",new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive)
				{
					WifiApSetting.setWifiApSsid(etPsw.getText().toString());
					synchronized (WifiApSetting) {
						WifiApSetting.notify();
						
					}
				}
					
			}
			
		}).show();
		
	}

	// end by 20150612
	
	// add by 20150325
	// 配置无线参数
	public void set_wire_config(final Context context,final MobilePara mobilePara)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.wire_config, null);
		LinkType type = mobilePara.getType();
		LinearLayout linAPN = (LinearLayout) view.findViewById(R.id.lay_local_apn);
		LinearLayout linPPP = (LinearLayout) view.findViewById(R.id.lay_ppp_config);
		final EditText localApn = (EditText) view.findViewById(R.id.et_wire_local_APN);
		final EditText localPPPName = (EditText) view.findViewById(R.id.et_wire_ppp_name);
		final EditText localPPPPsw = (EditText) view.findViewById(R.id.et_wire_ppp_pwd);
		final EditText serverIp = (EditText) view.findViewById(R.id.et_server_ip);
		final EditText serverPort = (EditText) view.findViewById(R.id.et_server_port);
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.group_trans_data);
		localApn.setText(mobilePara.getLocalApn());
		localPPPName.setText(mobilePara.getPppName());
		localPPPPsw.setText(mobilePara.getPppPsw());
		serverIp.setText(mobilePara.getServerIp());
		serverPort.setText(String.valueOf(mobilePara.getServerPort()));
		linAPN.setVisibility(View.VISIBLE);
		if(type != LinkType.ASYN)
			linPPP.setVisibility(View.VISIBLE);
		else
			linPPP.setVisibility(View.GONE);
		new BaseDialog(context, view, "无线链路配置参数", "确定", "取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					mobilePara.setLocalApn(localApn.getText().toString());
					mobilePara.setPppName(localPPPName.getText().toString());
					mobilePara.setPppPsw(localPPPPsw.getText().toString());
					mobilePara.setServerIp(serverIp.getText().toString());
					mobilePara.setServerPort(Integer.parseInt(serverPort.getText().toString()));
					int checkedId=radioGroup.getCheckedRadioButtonId();
					mobilePara.setSock_t(checkedId == R.id.rb_tcp?Sock_t.SOCK_TCP:checkedId == R.id.rb_udp?Sock_t.SOCK_UDP:Sock_t.SOCK_SSL);
					synchronized (mobilePara) {
						mobilePara.notify();
					}
				}
				
			}
		}).show();

	}
	// end by 20150325
	
	public final void setConfigNetTrans(final Activity activity,final NetWorkingBase netWorkingBase) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.eth_config, null);

		final EditText local_ip = (EditText) view.findViewById(R.id.et_local_ip);
		final EditText local_mask = (EditText) view.findViewById(R.id.et_local_mask);
		final EditText local_gata = (EditText) view.findViewById(R.id.et_local_gata);
		final EditText server_ip = (EditText) view.findViewById(R.id.et_server_ip);
		final EditText server_port = (EditText) view.findViewById(R.id.et_server_port);
		//20150515
		final RadioGroup networkData = (RadioGroup) view.findViewById(R.id.group_network_data);
		final RadioGroup transData = (RadioGroup) view.findViewById(R.id.group_trans_data);
//		Log.e(TAG, netWorkingBase.getLocalIp() +" "+netWorkingBase.getNetMask()+" "+netWorkingBase.getGateWay()+" "+netWorkingBase.getServerIp()+" "+netWorkingBase.getServerPort());
		local_ip.setText(netWorkingBase.getLocalIp());
		local_mask.setText(netWorkingBase.getNetMask());
		local_gata.setText(netWorkingBase.getGateWay());
		server_ip.setText(netWorkingBase.getServerIp());
		server_port.setText(netWorkingBase.getServerPort()+"");

		networkData
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.rb_static) // 静态
						{
							Log.e(TAG, "set static");
							netWorkingBase.setDHCPenable(false);
							local_ip.setEnabled(true);
							local_mask.setEnabled(true);
							local_gata.setEnabled(true);
							local_ip.setText(netWorkingBase.getLocalIp());
							local_mask.setText(netWorkingBase.getNetMask());
							local_gata.setText(netWorkingBase.getGateWay());
							server_ip.setText(netWorkingBase.getServerIp());
							server_port.setText(netWorkingBase.getServerPort()+"");

						} else if (checkedId == R.id.rb_trend) // 动态
						{
							Log.e(TAG, "set dhcp");
							netWorkingBase.setDHCPenable(true);
							local_ip.setEnabled(false);
							local_mask.setEnabled(false);
							local_gata.setEnabled(false);
							local_ip.setText("");
							local_mask.setText("");
							local_gata.setText("");
						}
					}
				});

		new BaseDialog(activity, view, "链路参数", "确定","取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					// 静态
					Log.e(TAG, networkData.getCheckedRadioButtonId() + " "+local_ip.getText().toString());
					if((networkData.getCheckedRadioButtonId()==R.id.rb_static&&!local_ip.getText().toString().equals("")&&!local_gata.getText().toString().equals("")&&!local_mask.getText().toString().equals("")
							&&!server_ip.getText().toString().equals("")&&!server_port.getText().equals(""))||(networkData.getCheckedRadioButtonId()==R.id.rb_trend
									&&!server_ip.getText().toString().equals("")&&!server_port.getText().toString().equals("")))
					{
							netWorkingBase.setLocalIp(local_ip.getText().toString());
							netWorkingBase.setGateWay(local_gata.getText().toString());
							netWorkingBase.setNetMask(local_mask.getText().toString());
							netWorkingBase.setServerIp(server_ip.getText().toString());
							netWorkingBase.setServerPort(Integer.parseInt(server_port.getText().toString()));
							int transCheckId=transData.getCheckedRadioButtonId();
							netWorkingBase.setSock_t(transCheckId == R.id.rb_tcp?Sock_t.SOCK_TCP:transCheckId == R.id.rb_udp?Sock_t.SOCK_UDP:Sock_t.SOCK_SSL);
					}
					synchronized (netWorkingBase) {
						netWorkingBase.notify();
					}
				}
			}
		}).show();
	}
	// end add 20150312

	
	// 对话框显示蓝牙
	// add by 20150417
	// 显示未配对蓝牙设备列表
	public Dialog showBtList(final Context context,final ArrayList<BluetoothDevice> unPairList) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.scan_bt, null);
		ListView unpairView = (ListView) view.findViewById(R.id.lv_bt_unpair);
		DefaultFragment.unpairAdapter = new ClientAdapter(unPairList,context);
		unpairView.setAdapter(DefaultFragment.unpairAdapter);
		final BaseDialog dialog=new BaseDialog(context, view, "蓝牙显示列表");
		dialog.show();
		unpairView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				view.setBackgroundColor(Color.GRAY);
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
				DefaultFragment.g_btAddr=unPairList.get(position).getAddress();
				DefaultFragment.g_btName=unPairList.get(position).getName();
				LoggerUtil.d("bt config ===== click");
				dialog.dismiss();
				synchronized (context) {
					context.notify();
				}
			}
		});
		dialog.setOnKeyListener(keylistener);
		return dialog;
	}
	
	/**
	 * NLS解码参数配置
	 * @param activity
	 * @param nlsPara
	 * @param cameraConfig 摄像头个数
	 */
	public void configNlsPara(Activity activity,final NlsPara nlsPara,final HashMap<String, Integer> cameraConfig)
	{
		
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.nls_config, null);
		RadioButton rbBack = (RadioButton) view.findViewById(R.id.rd_camera_back);
		RadioButton rbFront = (RadioButton) view.findViewById(R.id.rd_camera_font);
		RadioButton rbExternal = view.findViewById(R.id.rd_camera_external);
		RadioButton rbUsb = view.findViewById(R.id.rd_camera_usb);
		final RadioGroup rgCamera = (RadioGroup) view.findViewById(R.id.rg_config_camera);
		RadioButton rbUpcClose = (RadioButton) view.findViewById(R.id.rd_upc_close);
		RadioButton rbUpcOpen = (RadioButton) view.findViewById(R.id.rd_upc_open);
		final RadioGroup rgUpcBtn = (RadioGroup) view.findViewById(R.id.rg_upc_btn);
//		Log.d("nlsPara enter", nlsPara.getCameraPara()+"----"+nlsPara.getUpcBtn());
		
		
		if(cameraConfig.get(FONT_CAMERA)==-1)// 前置
			rbFront.setVisibility(View.GONE);
		else
			rbFront.setChecked(true);
		
		if(cameraConfig.get(BACK_CAMERA)==-1)// 后置
			rbBack.setVisibility(View.GONE);
		else
			rbBack.setChecked(true);
		
		if(cameraConfig.get(EXTERNAL_CAMERA)==-1)// 支付扫描头
			rbExternal.setVisibility(View.GONE);
		else
			rbExternal.setChecked(true);
		
		if(cameraConfig.get(USB_CAMERA)==-1)//USB摄像头
			rbUsb.setVisibility(View.GONE);
		else
			rbUsb.setChecked(true);
			
		if(nlsPara.getUpcBtn()==0)
			rbUpcOpen.setChecked(true);
		else
			rbUpcClose.setChecked(true);
		
		/**保存上次的配置*/
		if(nlsPara.isConfig())
		{
			int preCameraId = nlsPara.getCameraId();
			if(preCameraId==cameraConfig.get(FONT_CAMERA))
			{
				rbFront.setChecked(true);
			}
			else if(preCameraId==cameraConfig.get(BACK_CAMERA))
			{
				rbBack.setChecked(true);
			}
			else if(preCameraId==cameraConfig.get(USB_CAMERA))
			{
				rbUsb.setChecked(true);
			}
			else if(preCameraId==cameraConfig.get(EXTERNAL_CAMERA))
			{
				rbExternal.setChecked(true);
			}
			
		}
			
		BaseDialog dialog=new BaseDialog(activity, view, "参数配置", "确定",false,new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					/**N550的cameraId跟其他的设备是相反的，modify by zhengxq 20181030*/
					//0是后置 1是前置
					if(rgCamera.getCheckedRadioButtonId() == R.id.rd_camera_font){
						nlsPara.setCameraId(cameraConfig.get(FONT_CAMERA));
						nlsPara.setCameraMsg("前置摄像头");
					}else if(rgCamera.getCheckedRadioButtonId() == R.id.rd_camera_back)
					{
						nlsPara.setCameraId(cameraConfig.get(BACK_CAMERA));
						nlsPara.setCameraMsg("后置摄像头");
					}else if(rgCamera.getCheckedRadioButtonId() == R.id.rd_camera_external)
					{
						nlsPara.setCameraId(cameraConfig.get(EXTERNAL_CAMERA));
						nlsPara.setCameraMsg("支付摄像头");
					}else if(rgCamera.getCheckedRadioButtonId() == R.id.rd_camera_usb)
					{
						nlsPara.setCameraId(cameraConfig.get(USB_CAMERA));
						nlsPara.setCameraMsg("USB摄像头");
					}
					
					nlsPara.setUpcBtn(rgUpcBtn.getCheckedRadioButtonId() ==R.id.rd_upc_close ? 1:0);
					synchronized (nlsPara) {
						nlsPara.notify();
					}
				}
				
			}
		});
		
		dialog.show();
	}
	
	/**
	 * 安卓串口扫描枪
	 */
	public void ScanDialog(final Activity activity,int values)
	{
		
		final   Dialog dialog = new Dialog(activity, R.style.edit_AlertDialog_style);
		dialog.setCanceledOnTouchOutside(true);
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.activity_scan_config_dialog, null);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		lp.width = GlobalVariable.ScreenWidth; // 宽度
		Log.v("GlobalVariable.ScreenWidth=", GlobalVariable.ScreenWidth+"");
		lp.width = 600; // 宽度
		lp.height = GlobalVariable.ScreenHeight ; // 高度
		dialogWindow.setAttributes(lp);
		final  ImageView imageView = (ImageView) view.findViewById(R.id.scan_config_img);
		imageView.setBackgroundResource(values);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				synchronized (activity) {
					activity.notify();
				}
			}
		});
		dialog.show();
	}

	/**
	 * 多种验签参数设置  by zhangxj 20170303
	 */
	public void configSignaturePara(final Activity activity,String title)
	{
		LayoutInflater layout = LayoutInflater.from(activity);
		View view = layout.inflate(R.layout.signature_config, null);
		final List<String> signatureList = new ArrayList<String>();

		final CheckBox newland = (CheckBox) view.findViewById(R.id.newland);
		final CheckBox allinpay = (CheckBox) view. findViewById(R.id.allinpay);
		final CheckBox meituan = (CheckBox) view. findViewById(R.id.meituan);
		final CheckBox liandong = (CheckBox) view. findViewById(R.id.liandong);
		final CheckBox yinsheng = (CheckBox) view. findViewById(R.id.yinsheng);
		final CheckBox landi_joint = (CheckBox) view. findViewById(R.id.landi_joint);
		final CheckBox landi_infix = (CheckBox) view. findViewById(R.id.landi_infix);
		new BaseDialog(activity, view, title,  "确定", "取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					if (newland.isChecked()) {
						signatureList.add("newland");
					} 
					if (allinpay.isChecked()) {
						signatureList.add("allinpay");
					} 
					if (meituan.isChecked()) {
						signatureList.add("meituan");
					} 
					if (liandong.isChecked()) {
						signatureList.add("liandong");
					} 
					if (yinsheng.isChecked()) {
						signatureList.add("yinsheng");
					}
					if (landi_joint.isChecked()) {
						signatureList.add("landi_joint");
					}
					if (landi_infix.isChecked()) {
						signatureList.add("landi_infix");
					}
					GlobalVariable.signatureList = signatureList.toArray(new String[signatureList.size()]);
					synchronized (activity) {
						activity.notify();
					}
				}
			
			}
		}).show();
		
	
	}
	
	/**
	 * 是否开启合法性认证
	 * @param activity
	 * @param title 对话框标题
	 */
	public void configAuthChoose(final Activity activity,String title)
	{
		LayoutInflater layout = LayoutInflater.from(activity);
		View view = layout.inflate(R.layout.auth_choose, null);
		
		final RadioGroup authGroup = (RadioGroup) view.findViewById(R.id.group_legal_auth);
		final RadioButton rbCloseAuth = (RadioButton) view.findViewById(R.id.rb_auth_close);
		final RadioButton rbOpenAuth = (RadioButton) view. findViewById(R.id.rb_auth_open);
		
		// 设置默认的显示状态
		if(GlobalVariable.Auth_Control==0)
		{
			rbCloseAuth.setChecked(true);
		}
		else
		{
			rbOpenAuth.setChecked(true);
		}
		new BaseDialog(activity, view, title, "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				GlobalVariable.Auth_Control=authGroup.getCheckedRadioButtonId()==R.id.rb_auth_close ? 0:1;
				synchronized (activity) {
					activity.notify();
				}
			}
			
		}).show();
	}
	/**
	 * 单向发送接收性能测试 2个后台配置
	 */
	public void configSendRecv(Activity activity,final NetWorkingBase netPara)
	{
		LayoutInflater layout = LayoutInflater.from(activity);
		View view = layout.inflate(R.layout.set_send_recv_port, null);
		
		final EditText editSendPort=(EditText)view.findViewById(R.id.et_port);
		final EditText editIpAddress=(EditText)view.findViewById(R.id.et_ip_address);
		editSendPort.setText(netPara.getServerPort()+"");
		editIpAddress.setText(netPara.getServerIp());
		new BaseDialog(activity, view, "性能测试", "确定","取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				netPara.setServerIp(editIpAddress.getText().toString());
				netPara.setServerPort(Integer.parseInt(editSendPort.getText().toString()));
				synchronized (netPara) {
					netPara.notify();
				}
			}
		}).show();
	}
	
	public void snd_packet(Context context,final PacketBean packet,final LinkType type)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.set_snd_packet, null);
		final EditText et_times = (EditText) view.findViewById(R.id.et_snd_times);
		final EditText et_len = (EditText) view.findViewById(R.id.et_snd_packet_len);
		final RadioGroup chooseRadio = (RadioGroup) view.findViewById(R.id.group_choose);
		final RadioGroup dataGroup = (RadioGroup) view.findViewById(R.id.group_send_data);
		final EditText etFixData = (EditText) view.findViewById(R.id.et_data_fix);
		new BaseDialog(context, view, "参数配置", "确定", false, new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					packet.setIsLenRec(chooseRadio.getCheckedRadioButtonId()==R.id.rb_yes?true:false);
					if(!et_times.getText().toString().equals("")&&!et_len.getText().toString().equals(""))
					{
						// 是否随机
						if((!packet.isIsDataRnd()&&!etFixData.getText().toString().equals(""))||packet.isIsDataRnd())
						{
							packet.setLifecycle(Integer.parseInt(et_times.getText().toString()));
							packet.setLen(Integer.parseInt(et_len.getText().toString()));
							packet.setOrig_len(Integer.parseInt(et_len.getText().toString()));
							// 固定数据
							Log.e("isIsDataRnd", packet.isIsDataRnd()+"");
							Log.e("len", packet.getLen()+"");
							// 固定
							if(!packet.isIsDataRnd())
							{
								byte[] buf = new byte[packet.getLen()];
								packet.setDataFix((byte) Integer.parseInt(etFixData.getText().toString()));
								Arrays.fill(buf, 0, packet.getLen(), (byte) Integer.parseInt(etFixData.getText().toString()));
								packet.setHeader(buf);
								Log.e("固定数据", Arrays.toString(packet.getHeader()));
							}
							// 随机
							else
							{
								byte[] tmp = new byte[packet.getLen()];
								for (int i = 0; i < packet.getLen(); i++) 
								{
									tmp[i] = (byte) (Math.random()*128);
								}
								packet.setHeader(tmp);
							}
							if(packet.getLen()>PACKMAXLEN)
							{
								packet.setLen(PACKMAXLEN);
								packet.setOrig_len(PACKMAXLEN);
							}
							if((type == LinkType.ASYN || type== LinkType.SYNC)&&packet.getLen()<5)
							{
								packet.setLen(5);
								packet.setOrig_len(5);
							}
							if(type == LinkType.SYNC && packet.getLen()>SDLCPCKTMAXLEN)
							{
								packet.setLen(SDLCPCKTMAXLEN);
								packet.setOrig_len(SDLCPCKTMAXLEN);
							}
							if(type == LinkType.ASYN && packet.getLen()>ASYNPCKTMAXLEN)
							{
								packet.setLen(ASYNPCKTMAXLEN);
								packet.setOrig_len(ASYNPCKTMAXLEN);
							}
							if(type == LinkType.BT && packet.getLen()>BUFSIZE_BT)
							{
								packet.setLen(BUFSIZE_BT);
								packet.setOrig_len(BUFSIZE_BT);
							}
							
							synchronized (type) {
								type.notify();
							}
						}
					}
				}
				
				
			}
		}).show();
		dataGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				if(checkedId == R.id.rb_fix_data)
				{
					etFixData.setVisibility(View.VISIBLE);
					packet.setIsDataRnd(false);
				}
				else if(checkedId == R.id.rb_random_data)
				{
					etFixData.setVisibility(View.GONE);
					packet.setIsDataRnd(true);
				}
			}
		});
	}
	//设置交叉次数线程的listview
	public void set_cross_time(Context context,final PacketBean packet)
	{

		final EditText cross_times = new EditText(context);
		cross_times.setText(String.valueOf(packet.getLifecycle())+"");
		cross_times.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
	    new BaseDialog(context, cross_times, "压力次数设置", "确定","取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					if(cross_times.getText().length() != 0)
					{
						Log.e("times", cross_times.getText().toString());
						packet.setLifecycle(Integer.parseInt(cross_times.getText().toString()));
					}
				}else
					packet.setLifecycle(0);
				
			}
		}).show(); 
	}
	//打印配置线程的listview
	public void set_print_density(Context context,final int[] dentisty)
	{
		final EditText cross_times =  new EditText(context);
		cross_times.setText("0");//0为默认打印浓度
		cross_times.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
		
		new BaseDialog(context, cross_times, "打印灰度设置", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				Log.e("times", cross_times.getText().toString());
				dentisty[0]=Integer.parseInt(cross_times.getText().toString());
				synchronized (dentisty) {
					dentisty.notify();
				}
			}
			
		}).show();
	}
	
	/**
	 * 蓝牙MAC设置框
	 * @param context
	 * @param dentisty
	 */
	public void set_bt_mac(Context context,final StringBuffer mac)
	{
		final EditText et_mac =  new EditText(context);
		et_mac.setText("00:00:00:00:00:00");//0为默认打印浓度
		
		new BaseDialog(context, et_mac, "蓝牙MAC地址", "确定", false, new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) 
			{
				if(isPositive==true)
				{
					mac.append(et_mac.getText().toString());
					synchronized (mac) {
						mac.notify();
					}
				}
			}
			
		}).show();
	}
	
	//推送消息配置框
	public void set_push_notice(Context context,final PushNoticeBean notice){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.push_notice_config, null);
		 final EditText sendMsg=(EditText) view.findViewById(R.id.et_content);
		
		 final TextView lengthMsg=(TextView) view.findViewById(R.id.et_content_show);
		 final TextView intervalMsg=(TextView) view.findViewById(R.id.et_interval_text);
		
		 final EditText serverTotalNumber=(EditText) view.findViewById(R.id.et_totalNumber);;
		 final EditText serverInterval=(EditText) view.findViewById(R.id.et_interval);;
		 final RadioGroup sleepGroup=(RadioGroup) view.findViewById(R.id.group_sleep);
		 final RadioGroup randOrRegularGroup=(RadioGroup) view.findViewById(R.id.random_regular_choose);
		 RadioButton sleepButton=(RadioButton) view.findViewById(R.id.rb_sleep);
		 RadioButton wakeButton=(RadioButton) view.findViewById(R.id.rb_wake);
		 
		 
		 serverInterval.setText(notice.getInterval()+"");
		 serverTotalNumber.setText(notice.getTotalTime()+"");
		 sendMsg.setText(notice.getContent());
		 if(notice.isSleep()){
			 sleepButton.isChecked();
		 }else
			 wakeButton.isChecked();
		 //动态显示多少字节
		 sendMsg.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// 输入的内容变化的监听
					byte[] content=sendMsg.getText().toString().getBytes();
					lengthMsg.setText("推送消息内容：当前"+content.length+"个字节");
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		 randOrRegularGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.random_time){
					intervalMsg.setText("输入每小时推送次数（推送间隔为随机）");
					serverInterval.setText("4");
					
				}else
				{
					intervalMsg.setText("输入固定间隔时间（单位秒）");
					serverInterval.setText("900");
				}
				
			}
		 });
		new BaseDialog(context, view, "推送消息配置", "确定", "取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					notice.setContent(sendMsg.getText().toString());
					notice.setInterval(Integer.parseInt(serverInterval.getText().toString()));
					notice.setTotalTime(Integer.parseInt(serverTotalNumber.getText().toString()));
					notice.setSleep(sleepGroup.getCheckedRadioButtonId()==R.id.rb_sleep?true:false);
					notice.setRandOrRegular(randOrRegularGroup.getCheckedRadioButtonId()==R.id.random_time?true:false);
					LoggerUtil.e("设置进去的长度："+sendMsg.getText().toString().getBytes().length);
					notice.setContentLength(sendMsg.getText().toString().getBytes().length);
					synchronized (notice) {
						notice.notify();
					}
				}
				
			}
		}).show();
	}
}
