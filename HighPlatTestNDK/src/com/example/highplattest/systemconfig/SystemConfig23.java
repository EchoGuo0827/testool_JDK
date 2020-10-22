package com.example.highplattest.systemconfig;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig23.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160920
 * directory 		: 
 * description 		: 获取wifi的mac地址和imei号
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20160920		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig23 extends UnitFragment 
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "获取wifi的MAC地址";
//	private String wifiMac;
//	private String imei;
	private Gui gui = null;
	private String fileName="SystemConfig23";
	/*Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case 0:
				// 显示配置mac和imei的对话框
				mac_config_dialog();
				break;

			default:
				break;
			}
		};
	};*/
	
	public void systemconfig23() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gScreenTime, "%s用例的接口已废弃", TESTITEM);
			return;
		}
		/*private & local definition*/
		WifiManager wifiManager = (WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE);
//		ModemHelper modemHelper = new ModemHelper();
		
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		
		// 测试前置：确保wifi已连接
		gui.cls_show_msg("请确保wifi已关闭，完成任意键继续");
		// 据坤坤反馈获取wifi的mac与imei要使用android标准协议 modify by zhengxq 20171013
		WifiInfo wifiInfo_close = wifiManager.getConnectionInfo();
		// case1:wifi关闭下获取本机的MAC地址
		// 变更点评审优化MAC地址获取，关闭wifi仍可获取MAC
		String wifiMac = wifiInfo_close.getMacAddress();
		if(gui.cls_show_msg("获取到系统WIFI的MAC地址为:%s，查看与设置是否一致，一致[确认]，不一致[取消]", wifiMac)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gKeepTimeErr,"line %d:获取到的MAC地址与系统不符(MAC=%s)",Tools.getLineInfo(),wifiMac);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:wifi打开下获取本机的MAC地址
		gui.cls_show_msg("请确保wifi已开启，完成任意键继续");
		WifiInfo wifiInfo_open = wifiManager.getConnectionInfo();
		wifiMac = wifiInfo_open.getMacAddress();
		if(gui.cls_show_msg("获取到系统WIIF的MAC地址为：%s，查看与设置是否一致，一致[确认]，不一致[否]", wifiMac)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gKeepTimeErr,"line %d:获取到的MAC地址与系统不符(MAC=%s)",Tools.getLineInfo(),wifiMac);
			if(!GlobalVariable.isContinue)
				return;
		}
		
	/*	// case1:获取系统wifi的mac地址和IMEI号，去设置查看是否一致 据魏春霞反馈，获取imei和获取mac地址属于耗时操作，获取mac后延时操作后再获取imei
		wifiMac = modemHelper.getMacAddress();
		if(gui.cls_show_msg("获取到系统WIFI的MAC地址:%s，查看与设置是否一致，一致【确认】，不一致【其他】",wifiMac)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gKeepTimeErr,"line %d:获取到的MAC地址与系统不符(MAC=%s)",Tools.getLineInfo(),wifiMac);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		imei = modemHelper.getImei();
		if(gui.cls_show_msg("获取到系统IMEI值:%s，查看与设置是否一致，一直【确认】，不一致【其他】",imei)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gKeepTimeErr,"line %d:获取到的IMEI号与系统不符(imei = %s)",Tools.getLineInfo(),imei);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:重新设置系统wfi的mac地址和IMEI号，去设置查看是否一致
		// 提供手动设置对话框，不然可能导致设置的mac无法上网
		gui.cls_show_msg(0, "手动设置MAC地址");
		myHandler.sendEmptyMessage(0);
		GlobalVariable.PORT_FLAG = true;
		while(GlobalVariable.PORT_FLAG);
		if(wifiMac.equals("null"))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gKeepTimeErr,"line %d:输入的MAC格式错误",Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// 设置mac和imei号
		gui.cls_show_msg(2, "设置MAC地址");
//		modemHelper.setImei(imei.trim());
//		gui.cls_show_msg(1, "设置IMEI号");
		modemHelper.setMacAddress(wifiMac.trim());
//		gui.cls_show_msg(1, "获取IMEI号...");
//		String imeiOther = modemHelper.getImei();
		gui.cls_show_msg(2, "获取MAC地址...");
		String macOther = modemHelper.getMacAddress();
		if(imei.trim().equals(imeiOther)==false||wifiMac.trim().equals(macOther)==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig23",gKeepTimeErr,"line %d:设置的MAC和IMEI号与预期不一致(预期MAC=%s，实际MAC = %s)",Tools.getLineInfo(),wifiMac,macOther);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(3, "MAC地址将保存到测试报告，mac = %s",wifiMac);*/
		gui.cls_show_msg1_record(fileName,"systemconfig23",gScreenTime,"%s测试通过(长按确认键退出测试)",TESTITEM);
	}
	
/*	private void mac_config_dialog()
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("设置MAC地址");
		LayoutInflater layout = LayoutInflater.from(getActivity());
		View view = layout.inflate(R.layout.mac_config, null);
		final EditText macEt = (EditText) view.findViewById(R.id.et_set_mac);
//		final EditText imeiEt = (EditText) view.findViewById(R.id.et_set_imei);
		Button btnSure = (Button) view.findViewById(R.id.btn_mac_sure);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = GlobalVariable.ScreenWidth; // 宽度
        lp.height = GlobalVariable.ScreenHeight/3; // 高度
        dialogWindow.setAttributes(lp);
		btnSure.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View view) 
			{
				wifiMac = macEt.getText().toString().length()!=12?"null":macEt.getText().toString();
//				imei = imeiEt.getText().toString().length()!=15?"null":imeiEt.getText().toString();
				dialog.dismiss();
				GlobalVariable.PORT_FLAG = false;
			}
		});
		dialog.show();
	}*/

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
