package com.example.highplattest.systemnode;

import java.util.HashMap;

import android.provider.Settings;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.SysCfg;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;

/************************************************************************
 * file name 		: SystemNode10.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20200407 
 * directory 		: 
 * description 		: 系统属性获取
 * related document : 
 * history 		 	: 变更点                             				 	变更时间		案例人员
 *			  		  F7 V1.0.06导入       			 	20200407		zhengxq
 *					  X1新增获取副屏亮度	 				20200511      	 郑薛晴
 *					  N920_A7增加获取addr_size的属性		20200519		郑薛晴
 *					  CPOS_X5增加产品型号的属性			20200521		郑薛晴
 *					  N910_A7增加ro.serialno属性		20200601		郑薛晴
 *				                 由原Battery8搬移					20200604                       陈丁
 *					  N920_A7增加customId				20200628		郑薛晴
 *					 N920_A7增加PCI变更需求:增加sys.self_reboot 20200628         陈丁
 *					CPOS_V1.0.43增加日志缓冲区默认4M，解决日志丢失问题		20201020 郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode8 extends UnitFragment {
	
	private final String TESTITEM = "系统属性获取";
	private String CLASS_NAME = SystemNode8.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	String usbcam_pro="persist.usbcam.params.enable";
	
	/**	曾智彬：usbcam库添加persist.usbcam.params.enable属性支持（缺省值为0），
	 * 若配置为1，则usb摄像头使用Android传下来的参数；若配置为0，则使用默认参数*/
	
	HashMap<ParaEnum.SysCfg, String> hashSysCfgs = new HashMap<>();
	ParaEnum.SysCfg[] sysCfgs = 
		{SysCfg.Volume_Stream_Music,SysCfg.Screen_Brightness,SysCfg.Acceleromter_Rotation,SysCfg.Alarm_Alert,
			SysCfg.Persist_Sys_Nltest,SysCfg.Sys_Nltest,SysCfg.Persist_UsbCam,SysCfg.Screen_Vice_Brightness,SysCfg.Addr_Size,
			SysCfg.Manufacturer_type,SysCfg.Serial_No,SysCfg.Custom_Id,SysCfg.Pci_reboot,SysCfg.Log_Size};
	public void systemnode8 (){

		hashSysCfgs.put(SysCfg.Volume_Stream_Music, "volume_stream_music");
		hashSysCfgs.put(SysCfg.Screen_Brightness, "screen_brightness");
		hashSysCfgs.put(SysCfg.Acceleromter_Rotation, "accelerometer_rotation");
		hashSysCfgs.put(SysCfg.Alarm_Alert, "alarm_alert");
		hashSysCfgs.put(SysCfg.Persist_Sys_Nltest, "persist.sys.nltest");
		hashSysCfgs.put(SysCfg.Sys_Nltest, "sys.nltest");
		hashSysCfgs.put(SysCfg.Persist_UsbCam, "persist.usbcam.params.enable");
		hashSysCfgs.put(SysCfg.Screen_Vice_Brightness, "screen_vice_brightness");/**X1新增获取副屏亮度20200511*/
		hashSysCfgs.put(SysCfg.Addr_Size,"ro.boot.boot_ver");/**N920_A7增加获取addr_size的大小*/
		hashSysCfgs.put(SysCfg.Manufacturer_type, "ro.product.manufacturer_type");/**CPOS_X5增加产品型号，取出的产品型号判断不加入新大陆标识20200521*/
		hashSysCfgs.put(SysCfg.Serial_No, "ro.serialno");/**N910_A7新增获取SN值*/
		hashSysCfgs.put(SysCfg.Custom_Id, "sys.pos_customid");/**N920_A7增加获取Customid的属性20200628*/
		hashSysCfgs.put(SysCfg.Pci_reboot, "sys.self_reboot");/**N920_A7增加Pci变更需求属性，23小时50分重启。1打开0关闭 20200628*/
		hashSysCfgs.put(SysCfg.Log_Size, "ro.logd.size");/** CPOS_V1.0.43,ro.logd.size=4M*/
		
		while(true)
		{
			// 1.usbcam属性配置为0\n2.usbcam属性配置为1
			int nkeyIn = gui.JDK_ReadData(30, 0, "系统属性获取(\n0.volume_stream_music\n1.screen_brightness\n" +
					"2.accelerometer_rotation\n3.alarm_alert\n4.persist.sys.nltest\n5.sys.nltest\n6.persist.usbcam.params.enable\n7.screen_vice_brightness(X1)\n" +
					"8.addr_size(N920_A7)\n9.manufacturer_type(CPOS_X5)\n10.SN(N910_A7)\n11.custom_id(N920_A7)\n12.Pci_reboot(N920_A7)\n13.日志缓存区大小4M(CPOS)\n");
			LoggerUtil.d("key="+nkeyIn);
			if(nkeyIn==ESC)
			{
				unitEnd();
				return;
			}
			ParaEnum.SysCfg curSys = sysCfgs[nkeyIn];
			switch (curSys) {
			case Volume_Stream_Music:
			case Screen_Vice_Brightness:
			case Screen_Brightness:
			case Acceleromter_Rotation:
			case Alarm_Alert:
				String sysPro1 = Settings.System.getString(myactivity.getContentResolver(), hashSysCfgs.get(curSys));
				gui.cls_show_msg("%s属性值=%s", curSys,sysPro1);
				break;
				
			case Persist_Sys_Nltest:
			case Sys_Nltest:
			case Persist_UsbCam:
			case Addr_Size:
			case Manufacturer_type:
			case Serial_No:
			case Custom_Id:
			case Pci_reboot:
			case Log_Size:
				LoggerUtil.i("002="+curSys);
				String sysPro2 = BaseFragment.getProperty(hashSysCfgs.get(curSys), "-1");
				gui.cls_show_msg("%s属性值=%s", curSys,sysPro2);
				if (hashSysCfgs.get(curSys).equals("sys.self_reboot")) {
					if (sysPro2.equals("1")) {
						gui.cls_show_msg("属性打开，机器23小时50分重启");
					}else if (sysPro2.equals("0")) {
						gui.cls_show_msg("属性关闭");
					}else {
						gui.cls_show_msg("属性未设置");
					}
					
				}
				break;
				
			default:
				break;
			}
//			switch (nkeyIn) {
//			case '0':
//				String value = BaseFragment.getProperty(usbcam_pro, "0");
//				gui.cls_show_msg1(3,"获取到的usbcam属性=%s", value);
//				break;
				
//				
//			case '1':
//				if(BaseFragment.setProperty(usbcam_pro, "0")!=0)
//				{
//					gui.cls_show_msg1_record(CLASS_NAME, "battery8", 2, "line %d:设置usbcam属性=0失败", Tools.getLineInfo());
//					break;
//				}
//				else
//					gui.cls_show_msg1(2, "设置usbcam属性=0成功");
//				break;
//				
//			case '2':
//				if(BaseFragment.setProperty(usbcam_pro, "1")!=0)
//				{
//					gui.cls_show_msg1_record(CLASS_NAME, "battery8", 2, "line %d:设置usbcam属性=1失败", Tools.getLineInfo());
//					break;
//				}
//				else
//					gui.cls_show_msg1(2, "设置usbcam属性=1成功");
//				break;
				
//			case ESC:
//				unitEnd();
//				return;
//
//			default:
//				break;
//			}
		}
	
		
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
