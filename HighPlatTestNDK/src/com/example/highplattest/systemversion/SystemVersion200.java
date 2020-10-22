package com.example.highplattest.systemversion;

import java.util.Random;

import android.newland.os.NlBuild;
import android.newland.security.CertificateInfo;
import android.os.Build;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android版本号获取模块
 * file name 		: SystemVersion200.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20170824
 * directory 		: 
 * description 		: 系统版本号获取模块内随机
 * history 		 	: author			date			remarks
 *			  		  xuess		   		20170824		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemVersion200 extends UnitFragment
{
	private final String TESTITEM = "系统版本号获取模块内随机";
	private String fileName=SystemVersion200.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);	 
    Random random = new Random();
	CertificateInfo certificateInfo = new CertificateInfo(myactivity);
	boolean isNewAppSign = false;
	private String path = "/sys/class/graphics/fb0/msm_fb_panel_info";	//液晶型号接口节点解析路径
	private String path2 = "/sys/class/touchscreen/sensor_info";		//触摸屏厂家及sensor id接口节点解析路径
	private String path3 = "/sys/class/front_camera/camera_name";		//前置摄像头接口节点解析路径
	private String path4 = "/sys/class/back_camera/camera0_name";		//后置摄像头接口节点解析路径
	
	/**新大陆自定义*/
	String getSystemVersion = null;
	String getHardwareID = null;
	String getHardwareConfig = null;
	String getManuFlag = null;
	String getBaseBand = null;
	String getSerial = null;
	
	String getModel_NL = null;
	String getTusn = null;
	String getTouchScreen = null;
	String getTouchName = null;
	String getTouchVersion = null;
	String getPanel_name = null;
	String getSensor_info = null;
	String getFront_camera = null;
	String getBack_camera = null;
	String getWifiProbe = null;
	String getHardwareAcceleration = null;
	String getProcessorInfo=null;
	String getBootLoader = null;
	String getSerialNum = null;
	String getCustomerId = null;
	
	/**Android原生*/
	String getModel_AN = null;
	String getBoard_AN = null;
	String getBootLoader_AN = null;
	String getBrand_AN=null;
	String getDevice_AN=null;
	String getDisplay_AN = null;
	String getFingerPrinter_AN=null;
	String getHardware_AN = null;
	String getProduct_AN = null;
	String getTags_AN = null;
	
	public void systemversion200()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		String[] SystemVersionFunArr=null;
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
		{
			String[] SystemVersionFunArr1= {"AN_BOARD","AN_BOOTLOADER","AN_BRAND","AN_DEVICE","AN_DISPLAY","AN_FINGERPRINTER","AN_HARDWARE","AN_PRODUCT",
					"AN_TAGS","AN_MANUFACTURER","AN_SERIAL","AN_MODEL",
					/**硬件相关*/
					"Panel_name","Sensor_info","Front_camera","Back_camera","wifi_probe","hardwareAcceleration"};
			SystemVersionFunArr = SystemVersionFunArr1;
		}
		else
		{
			String[] SystemVersionFunArr1={				/**新大陆自定义*/
					"NL_BASEBAND","NL_BOOTLOADER","NL_FIRMWARE","NL_HARDWARE_ID","NL_HARDWARE_CONFIG","NL_PROCESSOR_INFO","NL_SERIAL_NUMBER",
					"NL_MODEL","NL_TUSN","NL_CUSTOMER_ID","TOUCHSCREEN_RESOLUTION","TOUCHSCREEN_NAME","TOUCHSCREEN_VERSION","isNewAppSign",
					"Panel_name","Sensor_info","Front_camera","Back_camera","wifi_probe","hardwareAcceleration","NL_HARDWARE_CONFIG_NODE",
					
					/**Android原生*/"AN_BOARD","AN_BOOTLOADER","AN_BRAND","AN_DEVICE","AN_DISPLAY","AN_FINGERPRINTER","AN_HARDWARE","AN_PRODUCT",
					"AN_TAGS","AN_MANUFACTURER","AN_SERIAL","AN_MODEL"};
			SystemVersionFunArr = SystemVersionFunArr1;
		}
		int len1 = SystemVersionFunArr.length;
		appSign();
		systemversion_test(SystemVersionFunArr,len1);
		
//		switch (GlobalVariable.currentPlatform) 
//		{
//		case N900_3G:
//			String  SystemVersionFunArr2[] = {"NL_BASEBAND","NL_BOOTLOADER","NL_FIRMWARE","NL_HARDWARE_ID","NL_HARDWARE_CONFIG","NL_MODEL","NL_TUSN",
//					"TOUCHSCREEN_RESOLUTION","TOUCHSCREEN_NAME","TOUCHSCREEN_VERSION","Panel_name","Sensor_info","Front_camera",
//					"Back_camera","wifi_probe","hardwareAcceleration",
//					"AN_BOARD","AN_BOOTLOADER","AN_BRAND","AN_DEVICE","AN_DISPLAY","AN_FINGERPRINTER","AN_HARDWARE","AN_PRODUCT",
//					"AN_TAGS","AN_MANUFACTURER","AN_SERIAL","AN_MODEL"};
//			int len2 = SystemVersionFunArr2.length;
//			systemversion_test(SystemVersionFunArr2,len2);
//			break;
//		case IM81_New:
//		case IM81_Old:
//			String  SystemVersionFunArr3[] = {"NL_BASEBAND","NL_BOOTLOADER","NL_FIRMWARE","NL_HARDWARE_ID","NL_HARDWARE_CONFIG",
//					"NL_MODEL","TOUCHSCREEN_RESOLUTION","TOUCHSCREEN_NAME","TOUCHSCREEN_VERSION","isNewAppSign",
//					"Panel_name","Sensor_info","Front_camera","Back_camera","wifi_probe","hardwareAcceleration",
//					"AN_BOARD","AN_BOOTLOADER","AN_BRAND","AN_DEVICE","AN_DISPLAY","AN_FINGERPRINTER","AN_HARDWARE","AN_PRODUCT","AN_TAGS",
//					"AN_MANUFACTURER","AN_SERIAL","AN_MODEL"};
//			int len3 = SystemVersionFunArr3.length;
//			systemversion_test(SystemVersionFunArr3,len3);
//			break;
//		default:
//			break;
//		}
		
	}
	
	private void systemversion_test(String[] SystemVersionFunArr,int len) {
		/*private & local definition*/
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
		getVersion();
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "Android版本号获取模块内随机组合测试中...\n还剩%d次（已成功%d次），按【取消】退出测试...",cnt,succ)==ESC)
				break;			
			String[] func = new String[g_RandomCycle];	
			StringBuilder funcStr = new StringBuilder();			
			for(int i=0;i<g_RandomCycle;i++){			
				func[i] = SystemVersionFunArr[random.nextInt(len)];
				funcStr.append(func[i]).append("-->\n");	
				if((i+1)%10 == 0 || i == g_RandomCycle-1){
					gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr.toString(),bak-cnt+1);
					funcStr.setLength(0);
				}
			}
			cnt--;
			boolean ret=false;
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"随机第%d组第%d项，正在测试%s",bak-cnt,i+1,func[i]);
				SystemVersionFuncName fname = SystemVersionFuncName.valueOf(func[i]);
				if(!(ret=test(fname))){
					gui.cls_only_write_msg(fileName, "systemversion200","%s第%d组第%d项,%s方法测试失败",TESTITEM,bak-cnt,i+1,func[i]);
					break;
				}
			}
			if(!ret){
				for(int i=0;i<g_RandomCycle;i++){
					funcStr.append(func[i]).append("-->");
				}
				gui.cls_only_write_msg(fileName, "systemversion200","第%d组随机测试失败，测试顺序为：%s",bak-cnt,funcStr.toString());
				funcStr.setLength(0);
			} else{
				succ++;
			}
		}
		gui.cls_show_msg1_record(fileName, "systemversion200", gKeepTimeErr, "%s测试完成，已执行次数为%d，成功为%d次。\n请检查SystemVersion模块内其他用例是否能正常使用！", TESTITEM, bak-cnt,succ);
	}
	
	public boolean test(SystemVersionFuncName fname){
		boolean ret =true;
		switch(fname){
		case NL_BASEBAND:// 新大陆自定义
			// 获取基带版本
			String baseBand = NlBuild.VERSION.BASEBAND;
			if(baseBand == null||!(baseBand.equals(getBaseBand)))
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取基带版本错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getBaseBand),strShift(baseBand));
				return false;
			}
			gui.cls_show_msg1(1, "基带版本="+baseBand);
			break;
		case NL_BOOTLOADER:// 新大陆自定义 add by zhengxq 20181128
			String bootloader = NlBuild.VERSION.BOOTLOADER_VERSION;
			if(bootloader == null||!(bootloader.equals(getBootLoader)))
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取bootloader版本错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getBootLoader),strShift(bootloader));
				return false;
			}
			gui.cls_show_msg1(1, "bootloader版本="+bootloader);
			break;
			
		case NL_MODEL:// 新大陆自定义和Android原生
			// 获取产品型号 
			String model_NL = NlBuild.VERSION.MODEL;
			if(model_NL==null || !(model_NL.equals(getModel_NL))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取产品型号不相等(mode11:%s,model2:%s)", Tools.getLineInfo(),strShift(getModel_NL),strShift(model_NL));
				return false;
			}
			gui.cls_show_msg1(1, "产品型号="+model_NL);
			break;
		case NL_FIRMWARE:// 新大陆自定义
			// 获取软件版本号
			String systemVersion = NlBuild.VERSION.NL_FIRMWARE;
			if(systemVersion == null||!(systemVersion.equals(getSystemVersion))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取软件版本号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getSystemVersion),strShift(systemVersion));
				return false;
			}
			gui.cls_show_msg1(1,"软件版本号="+systemVersion);
			break;
		case NL_HARDWARE_ID:// 新大陆自定义
			// 获取硬件识别码
			String hardwareID = NlBuild.VERSION.NL_HARDWARE_ID;
			if(hardwareID == null||!(hardwareID.equals(getHardwareID))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取硬件识别码错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getHardwareID),strShift(hardwareID));
				return false;
			}
			gui.cls_show_msg1(1, "硬件识别码="+hardwareID);
			break;
		case NL_HARDWARE_CONFIG:// 新大陆自定义
			// 获取产品配置码信息
			String hardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG;
			if(hardwareConfig == null||!(hardwareConfig.equals(getHardwareConfig))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取产品配置码信息错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getHardwareConfig),strShift(hardwareConfig));
				return false;
			}
			gui.cls_show_msg1(1, "产品配置码信息="+hardwareConfig);
			break;
			
		case NL_PROCESSOR_INFO:// 新大陆自定义 add by 20181128
			// 获取产品信息
			String processorInfo = NlBuild.VERSION.PROCESSOR_INFO;
			if(processorInfo==null||!(processorInfo.equals(getProcessorInfo)))
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取产品信息错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getProcessorInfo),strShift(processorInfo));
				return false;
			}
			gui.cls_show_msg1(1, "产品信息="+processorInfo);
			break;
			
		case NL_SERIAL_NUMBER:// 新大陆自定义 add by zhengxq 20181128
			String serialNum = NlBuild.VERSION.SERIAL_NUMBER;
			if(serialNum==null||!(serialNum.equals(getSerialNum)))
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取序列号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getSerialNum),strShift(serialNum));
				return false;
			}
			gui.cls_show_msg1(1, "序列号="+serialNum);
			break;
			
		case NL_HARDWARE_CONFIG_NODE:// 新大陆自定义
			String hardConfig1 = BaseFragment.getProperty("ro.epay.hardwareconfig","-10086");
			if(hardConfig1 == null||!(hardConfig1.equals(getHardwareConfig))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取产品配置码信息错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getHardwareConfig),strShift(hardConfig1));
				return false;
			}
			gui.cls_show_msg1(1, "产品配置码信息="+hardConfig1);
			break;

		case NL_TUSN:// 新大陆自定义
			// 给应用获取TUSN号 
			String tusn = null;
			try
			{
				tusn = NlBuild.TUSN;
				if(tusn == null||!(tusn.equals(getTusn))){
					gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取TUSN号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getTusn),strShift(tusn));
					return false;
				}
				gui.cls_show_msg1(1, "TUSN号="+ tusn);
			} catch (NoSuchFieldError e) 
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"此版本不支持获取TUSN");
			}
			break;
			
		case NL_CUSTOMER_ID:// 新大陆自定义，add by 20181128
			String customerId = NlBuild.CUSTOMER_ID;
			if(customerId == null||!(customerId.equals(getCustomerId)))
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取客户识别码错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getCustomerId),strShift(customerId));
				return false;
			}
			gui.cls_show_msg1(1, "客户识别码="+customerId);
			break;
		case TOUCHSCREEN_RESOLUTION:
			// 获取触屏分辨率
			String touchScreen = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
			if(touchScreen == null||!(touchScreen.equals(getTouchScreen))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取触屏分辨率错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getTouchScreen),strShift(touchScreen));
				return false;
			}
			gui.cls_show_msg1(1, "触屏分辨率="+touchScreen);
			break;
		case TOUCHSCREEN_NAME:
			// 获取触屏名称
			String touchName = NlBuild.VERSION.TOUCHSCREEN_NAME;
			if(touchName == null||!(touchName.equals(getTouchName))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取触屏名称错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getTouchName),strShift(touchName));
				return false;
			}
			gui.cls_show_msg1(1, "触屏名称="+touchName);
			break;
		case TOUCHSCREEN_VERSION:
			// 获取触屏版本号
			String touchVersion =  NlBuild.VERSION.TOUCHSCREEN_VERSION;
			if(touchVersion == null||!(touchVersion.equals(getTouchVersion))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取触屏版本号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getTouchVersion),strShift(touchVersion));
				return false;
			}
			gui.cls_show_msg1(1, "触屏版本号="+touchVersion);
			break;
		case isNewAppSign:
			if((ret = certificateInfo.isNewAppSign()) != isNewAppSign)
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取安装证书错误(%s)", Tools.getLineInfo(),ret);
				return false;
			}
			break;
		case Panel_name:
			//获取液晶型号
			String panel_info = LinuxCmd.readDevNode(path);
			String panel_name = readField(panel_info,"panel_name");
			if(panel_name == null||!(panel_name.equals(getPanel_name))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取液晶型号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getPanel_name),strShift(panel_name));
				return false;
			}
			gui.cls_show_msg1(1, "液晶型号:"+panel_name);
			break;
		case Sensor_info:
			//获取触摸屏厂家及sensor id
			String sensor_info = LinuxCmd.readDevNode(path2);
			if(sensor_info == null ||!(sensor_info.equals(getSensor_info))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取触摸屏厂家及sensor id错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getSensor_info),strShift(sensor_info));
				return false;
			}
			gui.cls_show_msg1(1, "触摸屏厂家及sensor id:"+sensor_info);
			break;
		case Front_camera:
			//获取前置摄像头接口
			String temp1 = LinuxCmd.readDevNode(path3);
			String front_camera = temp1.equals("") ? "未接前置摄像头" : temp1;
			if(front_camera == null||!(front_camera.equals(getFront_camera)) ){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取前置摄像头接口错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getFront_camera),FONT_CAMERA);
				return false;
			}
			gui.cls_show_msg1(1, "前置摄像头接口:"+front_camera);
			break;
		case Back_camera:
			//获取后置摄像头接口
			String temp2 = LinuxCmd.readDevNode(path4);
			String back_camera = temp2.equals("") ? "未接前置摄像头" : temp2;
			if(back_camera == null||!(back_camera.equals(getBack_camera)) ){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取后置摄像头接口错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getBack_camera),back_camera);
				return false;
			}
			gui.cls_show_msg1(1, "后置摄像头接口:"+back_camera);
			break;
		case wifi_probe:
			//是否支持wifi探针
			String result1 = BaseFragment.getProperty("sys.epay.wifiprobe","-10086");
			if(!result1.equalsIgnoreCase(getWifiProbe)){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:多次获取固件是否支持wifi探针结果不一致(%s,%s)", Tools.getLineInfo(),getWifiProbe,result1);
				return false;
			}
			if(result1.equalsIgnoreCase("true"))
			{
				gui.cls_show_msg1(1,"本固件支持wifi探针功能");
			}
			else if(result1.equalsIgnoreCase("false"))
			{
				gui.cls_show_msg1(1,"本固件不支持wifi探针功能");
			}
			else if(result1.equals(""))// 固件不支持该属性
			{
				gui.cls_show_msg1(1,"本固件不支持wifi探针属性，属于老固件");
			}
			break;
		case hardwareAcceleration:
			//是否支持硬件加速
			String result2 = BaseFragment.getProperty("persist.sys.ui.hw","-10086");
			if(!result2.equalsIgnoreCase(getHardwareAcceleration)){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:多次获取固件是否支持wifi探针结果不一致(%s,%s)", Tools.getLineInfo(),getHardwareAcceleration,result2);
				return false;
			}
			if(result2.equalsIgnoreCase("true"))
			{
				gui.cls_show_msg1_record(fileName, "test", gScreenTime,"本固件【已开启】硬件加速器");
			}
			else if(result2.equalsIgnoreCase("false"))
			{
				gui.cls_show_msg1_record(fileName, "test", gScreenTime,"本固件【未开启】硬件加速器");
			}
			else
			{
				gui.cls_show_msg1_record(fileName, "test", gScreenTime,"本固件不支持硬件加速器");
			}
			break;
			
		case AN_BOARD:// Android 原生 add by zhengxq 20181128
			// 获取主板号
			String board = Build.BOARD;
			if(board == null ||!(board.equals(getBoard_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取主板号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getBoard_AN),strShift(board));
				return false;
			}
			gui.cls_show_msg1(1, "主板号="+board);
			break;
		case AN_BOOTLOADER:
			// 获取bootloader版本
			String bootloader_AN = Build.BOOTLOADER;
			if(bootloader_AN == null ||!(bootloader_AN.equals(getBootLoader_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取bootloader版本错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getBootLoader_AN),strShift(bootloader_AN));
				return false;
			}
			gui.cls_show_msg1(1, "bootloader版本="+bootloader_AN);
			break;
		case AN_BRAND:
			// 获取软件品牌
			String brand_AN = Build.BRAND;
			if(brand_AN == null ||!(brand_AN.equals(getBrand_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取软件品牌错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getBrand_AN),strShift(brand_AN));
				return false;
			}
			gui.cls_show_msg1(1, "软件品牌="+brand_AN);
			break;
		case AN_DEVICE:
			// 工业设计的名称
			String device_AN = Build.DEVICE;
			if(device_AN == null ||!(device_AN.equals(getDevice_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取工业设计的名称错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getDevice_AN),strShift(device_AN));
				return false;
			}
			gui.cls_show_msg1(1, "工业设计的名称="+device_AN);
			break;
			
		case AN_DISPLAY:
			// ID字符串
			String display_AN = Build.DISPLAY;
			if(display_AN == null ||!(display_AN.equals(getDisplay_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取ID字符串错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getDisplay_AN),strShift(display_AN));
				return false;
			}
			gui.cls_show_msg1(1, "ID字符串="+display_AN);
			break;
			
		case AN_FINGERPRINTER:
			// 唯一标识符
			String fingerprinter_AN = Build.FINGERPRINT;
			if(fingerprinter_AN == null ||!(fingerprinter_AN.equals(getFingerPrinter_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取ID字符串错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getFingerPrinter_AN),strShift(fingerprinter_AN));
				return false;
			}
			gui.cls_show_msg1(1, "唯一标识符="+fingerprinter_AN);
			break;
		case AN_HARDWARE:
			// 硬件版本
			String hardware_AN = Build.HARDWARE;
			if(hardware_AN == null ||!(hardware_AN.equals(getHardware_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取硬件版本错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getHardware_AN),strShift(hardware_AN));
				return false;
			}
			gui.cls_show_msg1(1, "硬件版本="+hardware_AN);
			break;
		case AN_PRODUCT:
			// 产品完整型号
			String product_AN = Build.PRODUCT;
			if(product_AN == null ||!(product_AN.equals(getProduct_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取产品完整型号错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getProduct_AN),strShift(product_AN));
				return false;
			}
			gui.cls_show_msg1(1, "产品完整型号="+product_AN);
			break;
		case AN_TAGS:
			// 版本模式
			String tags_AN = Build.TAGS;
			if(tags_AN == null ||!(tags_AN.equals(getTags_AN))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取版本模式错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getTags_AN),strShift(tags_AN));
				return false;
			}
			gui.cls_show_msg1(1, "版本模式="+tags_AN);
			break;
		case AN_MANUFACTURER:// Android原生
			// 获取厂家厂商标识符
			String manuFlag = Build.MANUFACTURER;
			if(manuFlag == null ||!(manuFlag.equals(getManuFlag))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取厂家信息错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getManuFlag),strShift(manuFlag));
				return false;
			}
			gui.cls_show_msg1(1, "厂商标识符="+manuFlag);
			break;

		case AN_SERIAL:
			// 获取设备唯一识别码，返回TUSN
			String serial = Build.SERIAL;
			if(serial == null||!(serial.equals(getSerial))){
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:获取设备唯一识别码错误(预期:%s,实际:%s)", Tools.getLineInfo(),strShift(getSerial),strShift(serial));
				return false;
			}
			gui.cls_show_msg1(1, "设备唯一识别码="+serial);
			break;
			
		default:
			break;
		}
		return true;
	}
	
	public enum SystemVersionFuncName
	{
		/**新大陆自定义*/
		NL_BASEBAND,NL_BOOTLOADER,NL_FIRMWARE,NL_HARDWARE_ID,NL_HARDWARE_CONFIG,NL_PROCESSOR_INFO,NL_SERIAL_NUMBER,NL_MODEL,NL_TUSN,NL_CUSTOMER_ID,TOUCHSCREEN_RESOLUTION,TOUCHSCREEN_NAME,TOUCHSCREEN_VERSION,
		isNewAppSign,Panel_name,Sensor_info,Front_camera,Back_camera,wifi_probe,hardwareAcceleration,NL_HARDWARE_CONFIG_NODE,
		
		/**Android原生*/AN_BOARD,AN_BOOTLOADER,AN_BRAND,AN_DEVICE,AN_DISPLAY,AN_FINGERPRINTER,AN_HARDWARE,AN_PRODUCT,AN_TAGS,AN_MANUFACTURER,AN_SERIAL,AN_MODEL
	}
	
	public void appSign(){
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("请选择设备证书情况：\n0.未安装证书\n1.已安装旧签名证书\n2.已安装新签名证书\n");
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
				nkeyIn = '0';
			switch (nkeyIn) 
			{
			case '0':
				isNewAppSign = false;
				return;
			case '1':
				isNewAppSign = false;
				return;				
			case '2':
				isNewAppSign = true;
				return;
			default:
				break;
			}
		}
	}
	
	public void getVersion(){
		// 获取软件版本号
		getSystemVersion = NlBuild.VERSION.NL_FIRMWARE;
		if(getSystemVersion == null ) getSystemVersion = "null" ;
		// 获取硬件识别码
		getHardwareID = NlBuild.VERSION.NL_HARDWARE_ID;
		// 获取产品配置码信息
		getHardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG;
		// 获取厂家信息
		getManuFlag = Build.MANUFACTURER;
		// 获取基带版本
		if(GlobalVariable.currentPlatform != Model_Type.N900_3G){
			getBaseBand = NlBuild.VERSION.BASEBAND;
		}
		// 获取设备唯一识别码，返回TUSN
		getSerial = Build.SERIAL;
		// 获取产品型号 
		getModel_NL = NlBuild.VERSION.MODEL;
		//给应用获取TUSN号 
		try
		{
			getTusn = NlBuild.TUSN;
		} catch (NoSuchFieldError e) 
		{
			gui.cls_show_msg1_record(fileName, "getVersion", gScreenTime,"此版本不支持获取TUSN");
		}
		// 获取触屏分辨率
		getTouchScreen = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
		// 获取触屏名称
		getTouchName = NlBuild.VERSION.TOUCHSCREEN_NAME;
		// 获取触屏版本号
		getTouchVersion =  NlBuild.VERSION.TOUCHSCREEN_VERSION;
		// 液晶型号
		String panel_info = LinuxCmd.readDevNode(path);
		getPanel_name = readField(panel_info,"panel_name");
		// 触摸屏厂家及sensor id
		getSensor_info = LinuxCmd.readDevNode(path2);
		// 前置摄像头接口
		String temp = LinuxCmd.readDevNode(path3);
		getFront_camera = temp.equals("") ? "未接前置摄像头" : temp;
		// 后置摄像头接口
		temp = LinuxCmd.readDevNode(path4);
		getBack_camera = temp.equals("") ? "未接后置摄像头" : temp;
		// 是否支持wifi探针
		getWifiProbe = BaseFragment.getProperty("sys.epay.wifiprobe","-10086");
		//是否支持硬件加速
		getHardwareAcceleration = BaseFragment.getProperty("persist.sys.ui.hw","-10086");
		// 获取产品信息 add by zhengxq 20181128
		getProcessorInfo = NlBuild.VERSION.PROCESSOR_INFO;
		// 获取bootloader版本 add by zhengxq 20181128
		getBootLoader = NlBuild.VERSION.BOOTLOADER_VERSION;
		// 获取序列号 add by zhengxq 20181127
		getSerialNum = NlBuild.VERSION.SERIAL_NUMBER;
		// 获取customerId，add by zhengxq 20181127
		getCustomerId = NlBuild.CUSTOMER_ID;
		/**Android原生*/
		// 获取产品型号
		getModel_AN = Build.MODEL;
		// 获取主板号   以下部分add by zhengxq20181128
		getBoard_AN = Build.BOARD;
		// 获取bootLoader版本 
		getBootLoader_AN = Build.BOOTLOADER;
		// 软件品牌
		getBrand_AN = Build.BRAND;
		// 工业设计的名称
		getDevice_AN = Build.DEVICE;
		// ID字符串
		getDisplay_AN = Build.DISPLAY;
		// 唯一标识符
		getFingerPrinter_AN = Build.FINGERPRINT;
		// 获取硬件版本
		getHardware_AN = Build.HARDWARE;
		// 获取产品完整型号
		getProduct_AN = Build.PRODUCT;
		// 获取用户版本还是开发版本
		getTags_AN = Build.TAGS;
	}
	
	//读取字段内容
	public  String readField(String source, String fieldname){
		String result = null;
		int index_start = source.indexOf(fieldname);	//查找需要的字段第一次出现的index
		if(index_start == -1) //没找到
			return result;
		String str = source.substring(index_start);
		int index_end = str.indexOf("\r\n");		//查找本行结束的位置
		if(index_end == -1) //没找到
			return result;
		result = str.substring(0,index_end);
		return result;
	}
	
	private String strShift(String in)
	{
		String out = in==null?"null":in;
		return out;
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
