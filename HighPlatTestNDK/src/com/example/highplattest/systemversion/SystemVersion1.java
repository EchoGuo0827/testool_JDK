package com.example.highplattest.systemversion;

import android.newland.os.NlBuild;
import android.os.Build;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android版本号获取模块
 * file name 		: SystemVersion1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141226 
 * directory 		: 
 * description 		: 系统版本信息
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20141226     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemVersion1 extends UnitFragment
{
	private final String TESTITEM = "系统版本信息获取";
	private String fileName=SystemVersion1.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void systemversion1() 
	{
		gui.cls_show_msg("新大陆自定义接口获取到的bootloader版本，应校验与Android原生接口获取到的一致;\n没有CSN的情况下，获取SN；CSN和SN都没有的情况下，跟Android原生接口获取的一致");
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
	
		while(true)
		{
			int nKey ='0';
			nKey = gui.cls_show_msg("%s\n0.Android原生信息获取\n%s",TESTITEM,GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)?"":"1.新大陆自定义版本获取");
			switch (nKey) {
			case '0':
				testAndroid();
				break;

			case '1':
				testNewland();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	private void testNewland()
	{
		// 获取基带版本
		String baseBand = NlBuild.VERSION.BASEBAND;
		// 获取bootloader版本 add by zhengxq 20181127
		String bootloader = NlBuild.VERSION.BOOTLOADER_VERSION;

		// 获取产品型号 add by 20170515
		String model_2 = NlBuild.VERSION.MODEL;
		// 获取软件版本号
		String systemVersion = NlBuild.VERSION.NL_FIRMWARE;
		// 获取产品配置码信息
		String hardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG;
		// 获取硬件识别码
		String hardwareID = NlBuild.VERSION.NL_HARDWARE_ID;
		// 获取产品信息 add by zhengxq 20181127
		String processInfo = NlBuild.VERSION.PROCESSOR_INFO;
		// 获取序列号 add by zhengxq 20181127  也是唯一固件识别码
		String serialNum = NlBuild.VERSION.SERIAL_NUMBER;

		// 获取内核版本信息
//		String kernel = NlBuild.VERSION.KERNEL_VERSION;


		//给应用获取TUSN号 add by 20170810 wangxy
		String tusn=NlBuild.TUSN;
		// 客户识别码，使用客户英文缩写 add by zhengxq 20171201
		String customerID = null;
		try
		{
			customerID = NlBuild.CUSTOMER_ID;
		} catch (NoSuchFieldError e) 
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr,"此版本不支持获取客户识别码");
		}
		
		// 获取厂家信息
		String manuFlag = Build.MANUFACTURER;
		// 获取设备唯一识别码，返回TUSN
		String serial = Build.SERIAL;
		// 获取产品型号
		String model = Build.MODEL;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
		// case1:获取基带信息
		String str1 = baseBand==null?"null":baseBand;
		if((gui.ShowMessageBox(("获取到基带版本="+str1).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:%s测试失败(version = %s)",Tools.getLineInfo(), TESTITEM,str1);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case2:获取bootloader版本
		String str2 = bootloader==null?"null":bootloader;
		if((gui.ShowMessageBox(("获取bootloader版本="+str2).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:%s测试失败(bootloader = %s)",Tools.getLineInfo(), TESTITEM,str2);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3:获取软件版本号接口
		if ((gui.ShowMessageBox(("请确认获取软件版本号=" + systemVersion).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:%s测试失败(version = %s)",Tools.getLineInfo(), TESTITEM,systemVersion);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case4:获取硬件识别码
		if ((gui.ShowMessageBox(("请确认获取的硬件识别码=" + hardwareID).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:%s测试失败(version=%s)",Tools.getLineInfo(), TESTITEM,hardwareID);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case5:获取产品配置码信息
		if ((gui.ShowMessageBox(("请确认获取的产品配置码信息=" + hardwareConfig).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:%s测试失败(version = %s)",Tools.getLineInfo(), TESTITEM,hardwareConfig);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case6:获取厂商标识符
		String str6=manuFlag == null ? "null": manuFlag;
		if ((gui.ShowMessageBox(("获取到厂商标识符="+ str6).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:%s测试失败(version = %s)",Tools.getLineInfo(), TESTITEM,str6);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		
		// case7: 获取设备唯一识别码，返回TUSN 
		//android方式获取
		String str7 = serial == null?"null":serial;
		if((gui.ShowMessageBox((String.format("获取设备唯一识别码(TUSN)=%s(阿里巴巴体系下预期值为00000304+SN号)",str7)).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr,"line %d:获取设备唯一识别码测试失败(version = %s)", Tools.getLineInfo(),str7);
			if(!GlobalVariable.isContinue)
				return;
		}
//		//新大陆方式获取 唯一设备识别码；有CSN的情况下，获取的为CSN；没有CSN的情况下，获取SN；CSN和SN都没有的情况下，跟Android原生接口获取的一致
//		String str100=serialNum==null?"null":serialNum;
//		if((gui.ShowMessageBox((String.format("获取设备唯一识别码(TUSN)(新大陆获取-CSN和SN都没有的情况下，跟Android原生接口获取的一致)=%s(阿里巴巴体系下预期值为00000304+SN号)",str100)).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr,"line %d:获取设备唯一识别码测试失败(version = %s)", Tools.getLineInfo(),str100);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		
		// case7.1：获取产品型号
		String str3 = model == null?"null":model;
		if((gui.ShowMessageBox(("获取产品型号="+str3).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr,"line %d:获取产品型号测试失败(version = %s)", Tools.getLineInfo(),str3);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case7.2:获取产品型号第二种方式
		String str4 = model_2 == null?"null":model_2;
		if((gui.ShowMessageBox(("获取产品型号2="+str4).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr,"line %d:获取产品型号测试失败(verison = %s)", Tools.getLineInfo(),str4);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case8:获取TUSN号
		String str5 = tusn == null ? "null" : tusn;
		if ((gui.ShowMessageBox(("获取TUSN号=" + str5).getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				WAITMAXTIME)) != BTN_OK) {
			gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:获取TUSN号测试失败(tusn = %s)", Tools.getLineInfo(), str5);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case9:获取客户识别码 
		String str9 = customerID == null ? "null" : customerID;
		if ((gui.ShowMessageBox(("获取客户识别码=" + str9).getBytes(), (byte) (BTN_OK | BTN_CANCEL),
					WAITMAXTIME)) != BTN_OK) {
				gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:获取TUSN号测试失败(CUSTOMER_ID = %s)", Tools.getLineInfo(), str9);
				if (!GlobalVariable.isContinue)
					return;
		}
		// case10:获取产品信息
		String str10 = processInfo == null ? "null" : processInfo;
		if ((gui.ShowMessageBox(("获取产品信息=" + str6).getBytes(), (byte) (BTN_OK | BTN_CANCEL),
					WAITMAXTIME)) != BTN_OK) {
				gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:获取产品信息测试失败(PROCESSOR_INFO = %s)", Tools.getLineInfo(), str10);
				if (!GlobalVariable.isContinue)
					return;
		}
		// case11:获取产品序列号
		String str11 = serialNum == null ? "null" : serialNum;
		if ((gui.ShowMessageBox(("获取产品序列号=" + str11).getBytes(), (byte) (BTN_OK | BTN_CANCEL),
					WAITMAXTIME)) != BTN_OK) {
				gui.cls_show_msg1_record(fileName, "testNewland", gKeepTimeErr, "line %d:获取产品信息测试失败(SERIAL_NUM = %s)", Tools.getLineInfo(), str11);
				if (!GlobalVariable.isContinue)
					return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"基带版本="+str1);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"bootloader版本="+str2);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"软件版本号="+systemVersion);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"硬件识别码="+hardwareID);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"产品配置码信息="+hardwareConfig);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"厂商标识符="+str6);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"设备唯一识别码(TUSN)="+str7);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"产品型号="+str3);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"产品型号2="+str4);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"TUSN="+str5);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"客户识别码="+str9);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"产品信息="+str10);
			gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"产品序列号="+str11);
		}
		
		
		gui.cls_show_msg1_record(fileName, "testNewland", gScreenTime,"以上获取都正确则%s测试通过", TESTITEM);
	}
	
	private void testAndroid()
	{
		// The name of the underlying board,add by 20181126
		String board = Build.BOARD;
		// The System bootloader version number,add by 20181126
		String bootLoader = Build.BOOTLOADER;
		// The brand the software is customized for,if any;add by 20181126
		String brand = Build.BRAND;
//		// The name of the instruction set of native code
//		String CPU_ABI = Build.CPU_ABI;
		// The name of the industrial design
		String device = Build.DEVICE;
		// a build ID String meant for displaying for user
		String display = Build.DISPLAY;
		// A String that uniquely identifies the build.Do not attempt to pasrse this value
		String fingerPrinter = Build.FINGERPRINT;
		// The name of the hardware
		String hardware = Build.HARDWARE;
		
		// 获取厂家信息
		String manuFlag = Build.MANUFACTURER;
		// 获取设备唯一识别码，返回TUSN
		String serial = Build.SERIAL;
		// 获取产品型号
		String model = Build.MODEL;
		// The name of the overall product
		String product = Build.PRODUCT;
		// Comma-separated tags describing the build
		// the type of build,like 'user' or 'eng'
		String tags = Build.TAGS;
		
		
		// case1:获取主板号 add by zhengxq 20181126
		String str1 = board==null?"null":board;
		if ((gui.ShowMessageBox(("获取主板号="+ str1).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(board = %s)",Tools.getLineInfo(), TESTITEM,str1);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case2:获取bootloader add by zhengxq 20181126
		String str2=bootLoader==null?"null":bootLoader;
		if ((gui.ShowMessageBox(("获取到bootloader版本="+ str2).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(bootloader = %s)",Tools.getLineInfo(), TESTITEM,str2);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case3:软件的品牌  add by zhengxq 20181126
		String str3=brand==null?"null":brand;
		if ((gui.ShowMessageBox(("获取到软件版本="+ str3).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(brand = %s)",Tools.getLineInfo(), TESTITEM,str3);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case4:工业设计的名称 add by zhengxq 20181127
		String str4=device==null?"null":device;
		if ((gui.ShowMessageBox(("获取到工业设计的名称="+ str4).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(device = %s)",Tools.getLineInfo(), TESTITEM,str4);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case5:ID字符串 add by zhengxq 20181127
		String str5=display==null?"null":display;
		if ((gui.ShowMessageBox(("获取到ID字符串="+ str5).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(display = %s)",Tools.getLineInfo(), TESTITEM,display);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case6:唯一标识符 add by zhengxq 20181127
		String str6=fingerPrinter==null?"null":fingerPrinter;
		if ((gui.ShowMessageBox(("获取到唯一标识符="+ str6).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(fingerPrinter = %s)",Tools.getLineInfo(), TESTITEM,str6);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case7：硬件版本 add by zhengxq 20181127
		String str7=hardware==null?"null":hardware;
		if ((gui.ShowMessageBox(("获取到硬件版本="+ str7).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(hardware = %s)",Tools.getLineInfo(), TESTITEM,str7);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case8:获取厂商标识符 
		String str8=manuFlag == null ? "null": manuFlag;
		if ((gui.ShowMessageBox(("获取到厂商标识符="+ str8).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr, "line %d:%s测试失败(version = %s)",Tools.getLineInfo(), TESTITEM,str8);
			if (!GlobalVariable.isContinue)
				return;
		}
		// case9: 获取设备唯一识别码，返回TUSN
		String str9 = serial == null?"null":serial;
		if((gui.ShowMessageBox((String.format("获取设备唯一识别码(TUSN)=%s",str9)).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr,"line %d:获取设备唯一识别码测试失败(version = %s)", Tools.getLineInfo(),str9);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case10：获取产品型号
		String str10 = model == null?"null":model;
		if((gui.ShowMessageBox(("获取产品型号="+str10).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr,"line %d:获取产品型号测试失败(version = %s)", Tools.getLineInfo(),str10);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case11:获取产品完整型号 add by zhengxq 20181127
		String str11 = product == null?"null":product;
		if((gui.ShowMessageBox(("获取产品型号="+str11).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr,"line %d:获取产品型号测试失败(version = %s)", Tools.getLineInfo(),str11);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case12:用户版本还是开发版本 add by zhengxq	20181127
		String str12 = tags==null?"null":tags;
		if((gui.ShowMessageBox(("获取版本模式="+str12).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gKeepTimeErr,"line %d:获取版本模式测试失败(version = %s)", Tools.getLineInfo(),str12);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"主板号="+str1);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"bootloader版本="+str2);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"软件版本="+str3);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"工业设计的名称="+str4);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"ID字符串="+str5);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"唯一标识符="+str6);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"硬件版本="+str7);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"厂商标识符="+str8);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"设备唯一识别码="+str9);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"产品型号="+str10);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"产品完整型号="+str11);
			gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"版本模式="+str12);
		}
		gui.cls_show_msg1_record(fileName, "testAndroid", gScreenTime,"以上获取都正确则%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
