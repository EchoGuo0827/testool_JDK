package com.example.highplattest.other;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.newland.os.DeviceStatisticsManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.XmlResourceParserTool;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other7.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171218
 * directory 		: 获取设备的统计信息
 * description 		: 
 * related document :
 * 		 	                       变更记录								变更时间			变更人员
 * 					创建									20171218		zhengxq
 * 					 磁卡增加最后一次状态信息					20200514		魏美杰
 * 					N910_A7_V1.0.05导入统计IP只取200条		20200908		郑薛晴
 * 					开关机总次数变为只统计开机总次数				20200918		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other7 extends UnitFragment{
	private final String TESTITEM = "统计服务";
	public final String CLASS_NAME = Other7.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private int mKey;
	private final static String IPSTAT_ADD_URI="content://telephony/ipstat/add";
	private final static String URL_IPSTAT_RESET="content://telephony/ipstat/reset";
	
	
	public void other7()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			// 统计的各个信息显示在result.txt
			return;
		}
		
		gui.cls_show_msg("若固件有导入去除统计服务权限问题,将AndroidManifest.xml文件的android.permission.BATTERY_STATS权限删除后再进入本案例,已操作完毕任意键开始测试");
		
		while(true)
		{
//			deviceInfoMaps = XmlResourceParserTool.getNodeContent(ins, nodeNames);
			if(gui.cls_show_msg("服务统计(任意键继续)\n" +
					"0.全部服务测试  1.基本硬件信息\n" +
					"2.开机信息      3.电池信息\n" +
					"4.打印信息          5.软件安全攻击记录\n" +
					"6.硬件攻击记录  7.IC卡信息\n" +
					"8.非接卡信息      9.USB信息\n" +
					"10.存储器信息   11.摄像头信息\n" +
					"12.物理按键       13.触摸屏信息\n" +
					"14.液晶屏信息   15.GPS信息\n"+ 
					"16.wifi信息    17.蓝牙信息\n" +
					"18.移动网络信息19.异常关机信息\n" +
					"20.固件更新信息21.IC卡授权信息\n"+
					"22.磁卡使用信息23.补丁信息\n" +
					"24.应用安装信息25.禁用IP地址记录\n" +
					"26.获取应用电量统计\n" +
					"28.音频头信息   29.密码键盘信息\n" +
					"30.SAM信息      31.访问网址信息\n" +
					"32.测试访问网址\n" +
					"33.新增100001条网址记录测试\n" +
					"34.删除ip网址记录测试\n" +
					"35.ip形式访问百度")==ESC)
			{
				unitEnd();
				return;
			}
			mKey = gui.JDK_ReadData(30, 0);
			
			DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
			String xmlInfo = null;
			try {
				xmlInfo = service.getDeviceStatisticsInfo();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1(1, "line %d:抛出异常%s",Tools.getLineInfo(),e.getMessage());
				return;
			}
			
			InputStream ins = new ByteArrayInputStream(xmlInfo.getBytes());
			
			switch (mKey) 
			{
			case 0:
				// case1:获取基本硬件信息
				getbasicHardInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "device"));
				// case2:开机
				getPowerInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "power"));
				// case3:电池
				getBatInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "battery"));
				// case4:打印
				getPrintInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "print"));
				// case5:软件安全攻击记录
				getSoftAttackInfo();
				// case6:硬件安全
				getHardAttackInfo();
				// case7:IC卡使用
				getIcInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "ic_card"));
				// case8:非接卡使用
				getRfcInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "rfc_card"));
				// case9:USB拔插
				getUsbInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "usb"));
				// case10:存储器
				getStorageInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "storage"));
				// case11:摄像机使用
				getCameraInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "camera"));
				// case12:物理按键
				getPhysicalInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "physical_key"));
				// case13:触摸屏
				getTouchInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "touchscreen"));
				// case14:液晶屏
				getLcdInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "lcd"));
				// case15:GPS
				getGpsInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "gps"));
				// case16:异常关机
				getCrashInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "crash"));
				// case17:wifi使用
				getWifiInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "wlan"));
				// case18:蓝牙使用
				getBtInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "bluethooth"));
				// case19:移动网络使用
				getCellInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "cellular_data"));
				// case20:固件更新
				getfirmWareInfo();
				// case21:IC卡授权
				getIcAuthInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "ic_auth"));
				// case22:磁卡信息
				getMagInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "magnetic_card"));
				// case23:补丁信息
				getPatchInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "patch_info"));
				// case24:app信息
				getAppInfo();
				// case25:IP地址访问记录
				getIpInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "ip_info"));
				// case26:获取应用电量统计
				getPowerConsumption();
				// case28:获取音频头信息
				getEarPhoneInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "earphone"));
				// case29:密码键盘信息
				getPinPadInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "pinpad"));
				// case30:SAM信息
				getSAMInfo(XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "SAM"));
				// case31:访问网址信息
				getAccessSiteInfo();
				gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "%s测试通过，请进行各种服务相关操作后获取相应信息值变化才可视为测试通过", TESTITEM);
				break;
				
			case 1:
				getbasicHardInfo(XmlResourceParserTool.getNodeContent(ins, "device"));
				break;
				
			case 2:
				getPowerInfo(XmlResourceParserTool.getNodeContent(ins, "power"));
				break;
				
			case 3:
				getBatInfo(XmlResourceParserTool.getNodeContent(ins, "battery"));
				break;
				
			case 4:
				getPrintInfo(XmlResourceParserTool.getNodeContent(ins, "print"));
				break;
				
			case 5:
				getSoftAttackInfo();
				break;
				
			case 6:
				getHardAttackInfo();
				break;
				
			case 7:
				getIcInfo(XmlResourceParserTool.getNodeContent(ins, "ic_card"));
				break;
				
			case 8:
				getRfcInfo(XmlResourceParserTool.getNodeContent(ins, "rfc_card"));
				break;
				
			case 9:
				getUsbInfo(XmlResourceParserTool.getNodeContent(ins, "usb"));
				break;
				
			case 10:
				getStorageInfo(XmlResourceParserTool.getNodeContent(ins, "storage"));
				break;
				
			case 11:
				getCameraInfo(XmlResourceParserTool.getNodeContent(ins, "camera"));
				break;
				
			case 12:
				getPhysicalInfo(XmlResourceParserTool.getNodeContent(ins, "physical_key"));
				break;
				
			case 13:
				getTouchInfo(XmlResourceParserTool.getNodeContent(ins, "touchscreen"));
				break;
				
			case 14:
				getLcdInfo(XmlResourceParserTool.getNodeContent(ins, "lcd"));
				break;
				
			case 15:
				getGpsInfo(XmlResourceParserTool.getNodeContent(ins, "gps"));
				break;
				
			case 16:
				getWifiInfo(XmlResourceParserTool.getNodeContent(ins, "wlan"));
				break;
				
			case 17:
				getBtInfo(XmlResourceParserTool.getNodeContent(ins, "bluethooth"));
				break;
				
			case 18:
				getCellInfo(XmlResourceParserTool.getNodeContent(ins, "cellular_data"));
				break;
				
			case 19:
				getCrashInfo(XmlResourceParserTool.getNodeContent(ins, "crash"));
				break;
				
			case 20:
				getfirmWareInfo();
				break;
				
			case 21:
				getIcAuthInfo(XmlResourceParserTool.getNodeContent(ins, "ic_auth"));
				break;
				
			case 22:
				getMagInfo(XmlResourceParserTool.getNodeContent(ins, "magnetic_card"));
				break;
				
			case 23:
				getPatchInfo(XmlResourceParserTool.getNodeContent(ins, "patch_info"));
				break;
				
			case 24:
				getAppInfo();
				break;
				
			case 25:
				getIpInfo(XmlResourceParserTool.getNodeContent(ins, "ip_info"));
				break;
				
			case 26:
				getPowerConsumption();
				break;
				
			case 28:
				getEarPhoneInfo(XmlResourceParserTool.getNodeContent(ins, "earphone"));
				break;
				
			case 29:
				getPinPadInfo(XmlResourceParserTool.getNodeContent(ins, "pinpad"));
				break;
				
			case 30:
				getSAMInfo(XmlResourceParserTool.getNodeContent(ins, "sam"));
				break;
			
			case 31:
				getAccessSiteInfo();
				break;
			case 32:
				testAccessSite();
				break;
			case 33:
				addipinfo();
				break;
			case 34:
				deleipinfo();
				break;
			case 35:
				socketAccessSite();
				break;
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	//删除ip信息
	private void deleipinfo() {
		gui.cls_show_msg("点击任意键将清除统计服务中IP访问记录。。。。。。。");
		ContentResolver mResolver=myactivity.getContentResolver();
		final Uri deleIpStatUri=Uri.parse(URL_IPSTAT_RESET);
		mResolver.delete(deleIpStatUri, null, null);
		gui.cls_show_msg("删除成功...请手动查看newland/factory/DeviceStatisticsInfo.xml文件。");
	}

	//循环添加100001条信息
	private void addipinfo() {
		ContentResolver mResolver=myactivity.getContentResolver();
		final Uri addIpStatUri=Uri.parse(IPSTAT_ADD_URI);
		Log.d("eric_chen", "进入-----");
		gui.cls_show_msg("下面开始添加100001条网址访问记录。预期添加完毕后，访问网址记录第一条为www.2eric.com(该测试耗时较久，建议晚上测试。。)");
		for (int i = 1; i < 100002; i++) {
		  gui.cls_show_msg2(0.1f, "当前添加第%d次-----", i);
			String ip ="https://www."+i+"eric.com";
			ContentValues contentValues=new ContentValues();
			contentValues.put("ip", ip);
			mResolver.update(addIpStatUri, contentValues, null, null);
		}
		gui.cls_show_msg("添加完毕...请手动查看newland/factory/DeviceStatisticsInfo.xml文件。N910_A7只看查看到最新的200条");
		
		
		
	}

	// 基本硬件信息
	private void getbasicHardInfo(Map<String, String> deviceInfo)
	{
		// case1.1:硬件配置码（hardware_config）
		// case1.2:IMEI号（15位数字）
		// case1.3:出厂时间（manufacture_date，YYYYMMDDhhmmss）
		// case1.4:品牌（manufacturer，字符串）
		// case1.5:MEID（meid，14位十六进制格式数字）
		// case1.6:型号（model，字符串）
		// case1.7:pn（PN号，字符串）
		// case1.8:序列号（serial_num，字符串）
		// case1.9:SN号（字符串）
		// case1.10:固件版本（version，字符串）
		// case1.11:基带版本（baseband，字符串）
		// case1.12:EMMC参数信息（emmc，字符串）
		// case1.13:安全模块型号（security，字符串）
		// case1.14:非接芯片型号（noncontact，字符串）
		// case1.15:国密芯片型号（ncc，字符串）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.获取到硬件配置码为：%s\n2.获取到IMEI号：%s\n3.出厂时间：%s\n4.品牌：%s\n5.MEID:%s\n6.型号：%s\n7.PN号：%s\n8.序列号：%s\n9.SN号：%s\n10.固件版本：%s\n11.基带版本：%s\n" +
				"12.EMMC参数信息：%s\n13.安全模块型号：%s\n14.非接芯片型号:%s\n15.国密芯片型号:%s\n请于实际值对比，一致[确认]，不一致[取消]",
				deviceInfo.get("hardware_config"),deviceInfo.get("imei"),deviceInfo.get("manufacture_date"),deviceInfo.get("manufacturer"),
				deviceInfo.get("meid"),deviceInfo.get("model"),deviceInfo.get("pn"),deviceInfo.get("serial_num"),deviceInfo.get("sn"),
				deviceInfo.get("version"),deviceInfo.get("baseband"),deviceInfo.get("emmc"),deviceInfo.get("security"),deviceInfo.get("noncontact"),
				deviceInfo.get("ncc"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "获取基本硬件信息测试通过");
	}
	
	// 获取开机信息
	private void getPowerInfo(Map<String, String> powerInfo)
	{
		// case2.1:开关机总次数（operate，数字）开关机总次数改为开机总次数 20200918 只统计开机次数
		// case2.2:当前开机时间（on_time,YYYYMMDDhhmmss）
		// case2.3:累计开机时间（up_time,hh:mm:ss）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.开机总次数：%s\n2.当前开机时间：%s\n3.累计开机时间：%s\n请于实际值对比，一致[确认]，不一致[取消]", powerInfo.get("operate"),powerInfo.get("on_time"),powerInfo.get("up_time"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr, "line %d:测试失败", Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取开机信息测试通过");
	}
	
	// 获取电池信息
	private void getBatInfo(Map<String, String> batInfo)
	{
		// case3.1:充电总次数（operate,数字）
		// case3.2:当前电量（level，数字）
		// case3.3:当前充放电电量（charge，数字）
		// case3.4:充电时长/秒（charge_duration，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.充电总次数：%s\n2.当前电量：%s\n3.当前充放电电量：%s\n4.充电时长：%s秒\n请于实际值对比，一致[确认]，不一致[取消]", batInfo.get("operate"),batInfo.get("level"),batInfo.get("charge"),batInfo.get("charge_duration"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "获取电池信息测试通过");

	}
	
	// 获取打印信息
	private void getPrintInfo(Map<String, String> prnInfo)
	{
		// case4.1:打印米数（miter_total）
		// case4.2:加热时长（heating_time）
		// case4.3:最后一次状态（last_status）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.打印米数：%s\n2.加热时长：%s\n3.最后一次状态：%s\n请于实际值对比，一致[确认]，不一致[取消]", prnInfo.get("miters_total"),prnInfo.get("heating_time"),prnInfo.get("last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取打印信息测试通过");
	}
	
	// 获取软件攻击的信息
	private void getSoftAttackInfo()
	{
		// case5.1:攻击总次数（count，数字）
		// case5.2:攻击时间（time，YYYYMMDDhhmmss，可多个）
		// case5.3:攻击地点（place，预留参数）
		// case5.4:software（type，固定值“software”）
		DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
		String softwareAttackInfo = null;
		try
		{
			softwareAttackInfo = service.getDeviceStatisticsInfo("software_attack");
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1(gScreenTime,"此版本不支持获取硬件攻击信息");
			return;
		}
		if(softwareAttackInfo.equals("")){
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到硬件攻击信息的<software_attack>节点");
			return;
		}
		//将appInfo解析为Map
		Document doc = null;
        try {
            doc = DocumentHelper.parseText(softwareAttackInfo);
        } catch (DocumentException e) {
        	LoggerUtil.e("parse text error : " + e);
        }
        Element rootElement = doc.getRootElement();
        Map<String,Object> attackInfoMap = new HashMap<String,Object>();
        element2Map(attackInfoMap,rootElement);

		if (gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"总的软件攻击计数：%s\n请于实际值对比，一致[确认]，不一致[取消]",attackInfoMap.get("count")) != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr, "line %d:测试失败",Tools.getLineInfo());
			if (mKey != 0)
				return;
		}
		// 若统计应用数不为空且不为0，进一步显示详细的应用安装信息条目
		if (attackInfoMap.get("count") != null&& !attackInfoMap.get("count").equals("0")) {
			gui.cls_show_msg("将依次显示%s个软件攻击条目,任意键继续", attackInfoMap.get("count"));
			List<Map> itemList = new ArrayList<Map>();
			itemList = (List<Map>) attackInfoMap.get("item");
			for (Map attackItem : itemList) {
				StringBuffer attackInfoString = new StringBuffer();
				attackInfoString.append("攻击时间:" + attackItem.get("time")+ ",攻击地点:" + attackItem.get("place") + ",攻击类型:"+ attackItem.get("type") + "\n");
				if (gui.cls_show_msg1_record(CLASS_NAME, "other7", 30, "%s一致[确认]，不一致[取消]",attackInfoString.toString()) != ENTER) {
					gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:%s硬件攻击信息获取错误", Tools.getLineInfo(),attackItem.get("name"));
					continue;
				}
			}

		}

		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取软件攻击的信息测试通过");
	}
	
	// 获取硬件攻击的信息
	private void getHardAttackInfo()
	{
		// case6.1:攻击总次数（count，数字）
		// case6.2:攻击时间（YYYYMMDDhhmmss，可多个）
		// case6.3:攻击地点（place）
		// case6.4:hardware（type，固定值“hardware”）
		DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
		String hardWareAttackInfo = null;
		try
		{
			hardWareAttackInfo = service.getDeviceStatisticsInfo("hardware_attack");
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1(gScreenTime,"此版本不支持获取硬件攻击信息");
			return;
		}
		if(hardWareAttackInfo.equals("")){
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到硬件攻击信息的<hardware_attack>节点");
			return;
		}
		//将appInfo解析为Map
		Document doc = null;
        try {
            doc = DocumentHelper.parseText(hardWareAttackInfo);
        } catch (DocumentException e) {
        	LoggerUtil.e("parse text error : " + e);
        }
        Element rootElement = doc.getRootElement();
        Map<String,Object> attackInfoMap = new HashMap<String,Object>();
        element2Map(attackInfoMap,rootElement);

		if (gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"总的硬件攻击计数：%s\n请于实际值对比，一致[确认]，不一致[取消]",attackInfoMap.get("count")) != ENTER) {
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr, "line %d:测试失败",Tools.getLineInfo());
			if (mKey != 0)
				return;
		}
		// 若统计应用数不为空且不为0，进一步显示详细的应用安装信息条目
		if (attackInfoMap.get("count") != null&& !attackInfoMap.get("count").equals("0")) {
			gui.cls_show_msg("将依次显示%s个硬件攻击条目,任意键继续", attackInfoMap.get("count"));
			List<Map> itemList = new ArrayList<Map>();
			itemList = (List<Map>) attackInfoMap.get("item");
			for (Map attackItem : itemList) {
				StringBuffer attackInfoString = new StringBuffer();
				attackInfoString.append("攻击时间:" + attackItem.get("time")+ ",攻击地点:" + attackItem.get("place") + ",攻击类型:"+ attackItem.get("type") + "\n");
				if (gui.cls_show_msg1_record(CLASS_NAME, "other7", 30, "%s一致[确认]，不一致[取消]",attackInfoString.toString()) != ENTER) {
					gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:%s硬件攻击信息获取错误", Tools.getLineInfo(),attackItem.get("name"));
					continue;
				}
			}

		}

		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "获取硬件攻击信息测试通过");
	}
	
	// 获取IC卡信息
	private void getIcInfo(Map<String, String> icInfo)
	{
		// case7.1:拔插次数（operate，数字）
		// case7.2:最后一次状态（last_status，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.IC卡拨插次数：%s\n2.最后一次状态：%s请于实际值对比，一致[确认]，不一致[取消]", icInfo.get("operate"),icInfo.get("last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取IC卡信息测试通过");
	}
	
	// 获取非接卡信息
	private void getRfcInfo(Map<String, String> rfcInfo)
	{
		// case8.1:非接卡使用次数（operate，寻卡总次数，数字）
		// case8.2:激活成功次数（success，数字）
		// case8.3:激活失败次数（failed，数字）
		// case8.4:最后一次状态（last_status，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.非接卡使用次数：%s\n2.非接卡激活成功次数：%s\n3.非接卡激活失败次数：%s\n4.最后一次状态：%s\n请于实际值对比，一致[确认]，不一致[取消]",rfcInfo.get("operate"),rfcInfo.get("success"),rfcInfo.get("failed"),rfcInfo.get("last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取非接卡信息测试通过");
	}
	
	// 获取USB信息
	private void getUsbInfo(Map<String, String> UsbInfo)
	{
		// case9.1:次数（operate，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"USB拔插次数：%s\n请于实际值对比，一致[确认]，不一致[取消]", UsbInfo.get("operate"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取USB信息测试通过");
	}
	
	// 获取存储器情况
	private void getStorageInfo(Map<String, String> storageInfo)
	{
		// case10.1:存储器剩余容量（available,单位：B）
		// case10.2:擦写总次数（主要记录ARD刷机，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.存储器剩余容量：%sB\n2.擦写总次数：%s\n请于实际值对比，一致[确认]，不一致[取消]", storageInfo.get("available"),storageInfo.get("brush"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取存储器情况测试通过");
	}
	
	// 获取摄像头信息，modify 修改camera的型号和分辨率
	private void getCameraInfo(Map<String, String> cameraInfo)
	{
		// case11.1:开启次数（open，数字）
		// case11.2:后置摄像头开启次数（open_back，数字）
		// case11.3:闪光灯开启次数（flashlight，数字）
		// case11.4:后置摄像头型号（back_model，字符串）
		// case11.5:前置摄像头型号（front_model，字符串）
		// case11.6:后置摄像头分辨率（back_resolution，字符串）
		// case11.7:前置摄像头分辨率（front_resolution，字符串）
		// case11.8:前置摄像最后一次状态（front_last_status，字符串）
		// case11.9:后置摄像最后一次状态（back_last_status，字符串）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.摄像头开启次数:%s\n2.后置摄像头开启次数:%s\n3.闪光灯开启次数:%s\n4.后置摄像头型号:%s\n5.前置摄像头型号:%s\n6.后置摄像头分辨率:%s\n7.前置摄像头分辨率:%s\n8.前置摄像最后一次状态:%s\n9.后置摄像最后一次状态:%s\n" +
				"请于实际值对比，一致[确认]，不一致[取消]", cameraInfo.get("open"),cameraInfo.get("open_back"),cameraInfo.get("flashlight"),cameraInfo.get("back_model"),cameraInfo.get("front_model"),cameraInfo.get("back_resolution"),cameraInfo.get("front_resolution"),
				cameraInfo.get("front_last_status"),cameraInfo.get("back_last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取摄像头信息测试通过");
	}
	
	// 获取物理按键信息
	private void getPhysicalInfo(Map<String, String> physicalInfo)
	{
		// case12.1:电源键次数（power，数字）
		// case12.2:音量+次数（vol_up,数字）
		// case12.3:音量－次数（vol_dn,数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.电源键次数：%s\n2.音量+次数：%s\n3.音量－次数：%s\n请于实际值对比，一致[确认]，不一致[取消]",physicalInfo.get("power"),physicalInfo.get("vol_up"),physicalInfo.get("vol_dn"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取物理按键信息测试通过");
	}
	
	// 获取触摸屏信息
	private void getTouchInfo(Map<String, String> touchInfo)
	{
		// case13.1:点击次数（count，数字）
		// case13.2:分辨率（resolution，字符串）
		// case13.3:版本号（version，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.触摸屏点击次数：%s\n2.触摸屏分辨率：%s\n3.触摸屏版本：%s\n请于实际值对比，一致[确认]，不一致[取消]", touchInfo.get("count"), touchInfo.get("resolution"),touchInfo.get("version"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取触摸屏信息测试通过");
	}
	
	// 获取液晶屏信息
	private void getLcdInfo(Map<String, String> lcdInfo)
	{
		// case14.1:厂家信息（factory，字符串）
		// case14.2:分辨率（resolution，字符串）
		// case14.3:屏幕背光点亮累计时间（lighten，字符串）
		// case14.4:按键背光点亮累计时间（button_lighten，字符串）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.液晶屏厂家信息：%s\n2.液晶屏分辨率：%s\n3.屏幕背光点亮累计时间：%s\n4.按键背光点亮累计时间:%s\n请于实际值对比，一致[确认]，不一致[取消]", lcdInfo.get("factory"),lcdInfo.get("resolution"),lcdInfo.get("lighten"),lcdInfo.get("button_lighten"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取液晶屏信息测试通过");
	}
	
	// 获取gps信息
	private void getGpsInfo(Map<String, String> gpsInfo)
	{
		// case15.1:经纬度（location，精度（长整型）、纬度（长整型））
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"经纬度：%s\n请于实际值对比，一致[确认]，不一致[取消]", gpsInfo.get("location"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取GPS信息测试通过");
	}
	
	// 异常关机信息
	private void getCrashInfo(Map<String, String> crashInfo)
	{	
		gui.cls_show_msg("请不要插USB的情况下进行异常关机测试,按任意键继续");
		// case16.1:异常关机次数（count）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"异常关机次数：%s\n请于实际值对比，一致[确认]，不一致[取消]", crashInfo.get("count"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取异常关机信息测试通过");
	}
	
	// 获取wifi信息
	private void getWifiInfo(Map<String, String> wifiInfo)
	{
		// case17.1:是否开启（status）
		// case17.2:热点名称（hotspots）
		// case17.3:加密类型（security）
		// case17.4:连接时长（duration）
		// case17.5:连接次数（link）
		// case17.6:失败次数（failed）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.wifi是否开启：%s\n2.wifi热点名称：%s\n3.wifi加密类型：%s\n4.wifi连接时长：%s\n5.wifi连接次数：%s\n6.wifi连接失败次数：%s\n，请于实际值对比，一致[确认]，不一致[取消]", 
				wifiInfo.get("status"),wifiInfo.get("hotspots"),wifiInfo.get("security"),wifiInfo.get("duration"),wifiInfo.get("link"),wifiInfo.get("failed"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "获取wifi信息测试通过");
	}
	
	// 获取蓝牙信息
	private void getBtInfo(Map<String, String> btInfo)
	{
		// case18.1:是否开启（status）
		// case18.2:打开次数（operate，数字）
		// case18.3:已配对信息（pairing）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.蓝牙是否开启：%s\n2.打开次数：%s\n3.蓝牙已配对信息：%s\n请于实际值对比，一致[确认]，不一致[取消]",btInfo.get("status"),btInfo.get("operate"),btInfo.get("pairing"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "获取蓝牙信息测试通过");
	}
	
	// 获取移动网络信息
	private void getCellInfo(Map<String, String> cellInfo)
	{
		// case19.1:运营商名称（operator）
		// case19.2:月流量统计（traffic_monthly，数字，单位B）
		// case19.3:apn信息（apn）
		// case19.4:网络模式（mode）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.运营商名称：%s\n2.月流量统计：%sB\n3.apn信息：%s\n4.网络类型：%s\n请与实际值对比，一致[确认]，不一致[取消]",cellInfo.get("operators"),cellInfo.get("traffic_monthly"),cellInfo.get("apn"),cellInfo.get("mode"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime, "获取移动网络信息测试通过");
	}
	
	// 获取固件升级信息
	private void getfirmWareInfo()
	{
		// case20.1:更新迭代状态（state）
		// case20.2:更新时间（time）
		// case20.3:更新方式OTA（mode）
		// case20.4:安全模块更新时间（secure_time）
		DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
		String firmWareInfo = null;
		try
		{
			firmWareInfo = service.getDeviceStatisticsInfo("firmware_update");
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1(gScreenTime,"此版本不支持获取固件升级信息");
			return;
		}
		if(firmWareInfo.equals("")){
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到固件升级信息的<firmware_update>节点");
			return;
		}
		//将appInfo解析为Map
		Document doc = null;
        try {
            doc = DocumentHelper.parseText(firmWareInfo);
        } catch (DocumentException e) {
        	LoggerUtil.e("parse text error : " + e);
        }
        Element rootElement = doc.getRootElement();
        Map<String,Object> firmWareInfoMap = new HashMap<String,Object>();
        element2Map(firmWareInfoMap,rootElement);

        List<Map> itemList = new ArrayList<Map>();
        itemList = (List<Map>) firmWareInfoMap.get("item");
        
        //若item不为空，进一步显示每个应用电量统计
		if(firmWareInfoMap.get("item") != null ){
			gui.cls_show_msg("将依次显示%d个固件更新统计条目,任意键继续",itemList.size());
			for(Map item:itemList){
				StringBuffer iteminfo = new StringBuffer();
				iteminfo.append("更新状态迭代:"+item.get("state")+"\n");
				iteminfo.append("更新时间:"+item.get("time")+"\n");
				iteminfo.append("更新方式:"+item.get("mode")+"\n");
				iteminfo.append("安全模块更新时间:"+item.get("secure_time")+"\n");
				
				if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"%s一致[确认]，不一致[取消]", iteminfo.toString())!=ENTER)
				{
					gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:固件更新信息获取错误",Tools.getLineInfo());
					continue;
				}
			}
			if(mKey!=0)
				gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取固件更新信息测试通过");
		} else{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到固件更新信息");
		}
	}
	//
	// 获取IC授权信息
	private void getIcAuthInfo(Map<String, String> icAuthInfo)
	{
		// case21.1:开控制台次数（adb）
		// case21.2:开控制台时间（adb_time）
		// case21.3:清证书次数（certificate）
		// case21.4:清证书时间（certificate_time）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.app信息：%s\n2.开控制台次数：%s\n3.开控制台时间：%s\n4.开控制台地点：%s\n5.清证书次数：%s\n6.清证书时间：%s\n7.清证书地点：%s\n请于实际值对比，一致[确认]，不一致[取消]",
				icAuthInfo.get("pckgname"), icAuthInfo.get("adb"),icAuthInfo.get("adb_time"),icAuthInfo.get("adb_site"),
				icAuthInfo.get("certificate"),icAuthInfo.get("certificate_time"),icAuthInfo.get("certificate_site"))!=ENTER
)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取IC授权信息测试通过");
	}
	
	// 获取磁卡使用信息
	private void getMagInfo(Map<String, String> magInfo)
	{
		// case22.1:刷卡次数（operate，数字）
		// case22.2:最后一次状态（last_status，数字）
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"磁卡刷卡次数：%s\n磁卡最后一次状态信息%s\n请于实际值对比，一致[确认]，不一致[取消]", magInfo.get("operate"),magInfo.get("last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取磁卡使用信息测试通过");
	}
	
	//获取补丁信息
	private void getPatchInfo(Map<String, String> patchInfo)
	{
		// case23.1:补丁级别列表（patch_level_list，数字）
		// case23.2:补丁级别哈希值（hash，字符串）
		gui.cls_show_msg("设置-关于设备中连续点击三次“Android版本”后返回键退出关于设备,再次进入则可在版本号中可查看到设备的补丁信息,此为实际值,请与接下来获取的值作对比,任意键继续");
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.补丁级别列表：%s\n2.补丁级别哈希值：%s\n请于实际值对比，一致[确认]，不一致[取消]", patchInfo.get("patch_level_list"),patchInfo.get("hash"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取补丁信息测试通过");
	}
	
	//获取应用安装信息
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getAppInfo()
	{
		// case24.1:总的统计应用计数（app_cnt，数字）
		// case24.2:每个应用的安装信息条目（app_item，有多条且有包含项，包含项如下）
		// case24.2.1:应用包名（name）
		// case24.2.2:应用的安装位置（codepath）
		// case24.2.3:应用的摘要值（hash）
		// case24.2.4:应用数字证书名称（cer_name）
		// case24.2.5:应用证书的颁发者名称（ca_name）
		// case24.2.6:应用总安装次数（install_cnt）
		// case24.2.7:历次应用安装升级详细记录（item，有多条且有包含项，包含项如下）
		// case24.2.7.1:安装时间（time）
		// case24.2.7.2:安装途径（way）
		DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
		String appInfo = null;
		try
		{
			appInfo = service.getDeviceStatisticsInfo("app_info");
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1(gScreenTime,"此版本不支持获取应用安装信息");
			return;
		}
		if(appInfo.equals("")){
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到应用安装信息的<app_info>节点");
			return;
		}
		//将appInfo解析为Map
		Document doc = null;
        try {
            doc = DocumentHelper.parseText(appInfo);
        } catch (DocumentException e) {
        	LoggerUtil.e("parse text error : " + e);
        }
        Element rootElement = doc.getRootElement();
        Map<String,Object> appInfoMap = new HashMap<String,Object>();
        element2Map(appInfoMap,rootElement);
        
        if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"总的统计应用计数：%s\n请于实际值对比，一致[确认]，不一致[取消]", appInfoMap.get("app_cnt"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
        //若统计应用数不为空且不为0，进一步显示详细的应用安装信息条目
		if(appInfoMap.get("app_cnt") != null && !appInfoMap.get("app_cnt").equals("0")){
			gui.cls_show_msg("将依次显示%s个应用的安装信息条目,任意键继续",appInfoMap.get("app_cnt"));
			List<Map> appItemList = new ArrayList<Map>();
			appItemList = (List<Map>) appInfoMap.get("app_item");
			for(Map appitem:appItemList){
				StringBuffer appiteminfo = new StringBuffer();
				appiteminfo.append("应用包名:"+appitem.get("name")+"\n");
				appiteminfo.append("应用的安装位置:"+appitem.get("codepath")+"\n");
				appiteminfo.append("应用的摘要值:"+appitem.get("hash")+"\n");
				appiteminfo.append("应用数字证书名称:"+appitem.get("cer_name")+"\n");
				appiteminfo.append("应用证书的颁发者名称:"+appitem.get("ca_name")+"\n");
				appiteminfo.append("应用总安装次数:"+appitem.get("install_cnt")+"\n");
				appiteminfo.append("历次应用安装升级详细记录如下:\n");
				if(Integer.parseInt((String) appitem.get("install_cnt"))>1){
					Log.d("eric", "if---");
					List<Map> installList = new ArrayList<Map>();
					installList = (List<Map>) appitem.get("item");
					for(Map installitem:installList){
						appiteminfo.append("安装时间:"+installitem.get("time")+",安装途径:"+installitem.get("way")+"\n");
					}
				}
				else
				{
					Log.d("eric", "else---");
					HashMap<String,String> itemInfo=new HashMap<String, String>();
					itemInfo = (HashMap<String, String>) appitem.get("item");
					appiteminfo.append("安装时间:"+itemInfo.get("time")+",安装途径:"+itemInfo.get("way")+"\n");

				}
				if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"%s一致[确认]，不一致[取消]", appiteminfo.toString())!=ENTER)
				{
					gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:%s应用信息获取错误",Tools.getLineInfo(),appitem.get("name"));
					continue;
				}
			}
			
		}

		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取应用安装信息测试通过");
	}
	
	//IP地址访问记录
	private void getIpInfo(Map<String, String> inInfo)
	{
		// case25.1:IP地址访问历史记录（ip_history，字符串,以”,”分割,未实现） 被取缔 2018/11/5
		// case25.2:被禁用的IP地址（ip_forbidden，字符串,以”,”分割） 
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.被禁用的IP地址：%s\n请于实际值对比，一致[确认]，不一致[取消]", inInfo.get("ip_forbidden"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取禁用IP地址记录测试通过");
	}
	
	//获取应用电量统计
	private void getPowerConsumption()
	{
		//获取app应用耗电统计，需调用应用uid为mtms
		// 应用电量统计条目item，有多个且有包含项，包含项如下：
		// case26.1:耗电占比（percent，浮点数）
		// case25.2:用户id（uid，数字字符串）
		// case25.3:程序包名（packages，字符串，如果多个用","隔开）
		// case25.4:cpu总运行时间（cpu，时间，单位毫秒）
		// case25.5:CPU(前台)	时间（cpu_foreground，时间，单位毫秒）
		// case25.6:唤醒时间（wake_lock，时间，单位毫秒）
		// case25.7:GPS使用时间（gps_time，时间，单位毫秒）
		// case25.8:WiFi使用时间（wifi_running，时间，单位毫秒）
		// case25.9:接收的移动数据包（data_recv，数字）
		// case25.10:发送的移动数据包（data_send，数字）
		// case25.11:移动无线装置运行时间（radio_active，时间，单位毫秒）
		// case25.12:接收的wlan数据包（data_wifi_recv，数字）
		// case25.13:发送的wlan数据包（data_wifi_send，数字）
		if(gui.cls_show_msg("获取app应用耗电统计需添加mtms权限，银商版本需要验签，[取消]退出，[确认]继续")==ESC){
			return;
		}
		DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
		String powerConsuInfo = null;
		try
		{
			powerConsuInfo = service.getDeviceStatisticsInfo("power_consumption");
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1(gScreenTime,"此版本不支持获取应用电量统计");
			return;
		}
		if(powerConsuInfo.equals("")){
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到应用电量统计信息的<power_consumption>节点");
			return;
		}
		//将powerConsuInfo解析为Map
		Document doc = null;
        try {
            doc = DocumentHelper.parseText(powerConsuInfo);
        } catch (DocumentException e) {
        	LoggerUtil.e("parse text error : " + e);
        }
        Element rootElement = doc.getRootElement();
        Map<String,Object> powerConsuInfoMap = new HashMap<String,Object>();
        element2Map(powerConsuInfoMap,rootElement);
        
        List<Map> itemList = new ArrayList<Map>();
        itemList = (List<Map>) powerConsuInfoMap.get("item");
        
        //若item不为空，进一步显示每个应用电量统计
		if(powerConsuInfoMap.get("item") != null ){
			gui.cls_show_msg("将依次显示%d个应用电量统计条目,任意键继续",itemList.size());
			for(Map item:itemList){
				StringBuffer iteminfo = new StringBuffer();
				iteminfo.append("耗电占比:"+item.get("percent")+"\n");
				iteminfo.append("用户id:"+item.get("uid")+"\n");
				iteminfo.append("程序包名:"+item.get("packages")+"\n");
				iteminfo.append("cpu总运行时间:"+item.get("cpu")+"ms\n");
				iteminfo.append("CPU(前台)时间:"+item.get("cpu_foreground")+"ms\n");
				iteminfo.append("唤醒时间:"+item.get("wake_lock")+"ms\n");
				iteminfo.append("GPS使用时间:"+item.get("gps_time")+"ms\n");
				iteminfo.append("WiFi使用时间:"+item.get("wifi_running")+"ms\n");
				iteminfo.append("接收的移动数据包:"+item.get("data_recv")+"\n");
				iteminfo.append("发送的移动数据包:"+item.get("data_send")+"\n");
				iteminfo.append("移动无线装置运行时间:"+item.get("radio_active")+"mss\n");
				iteminfo.append("接收的wlan数据包:"+item.get("data_wifi_recv")+"\n");
				iteminfo.append("发送的wlan数据包:"+item.get("data_wifi_send")+"\n");
				if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"%s一致[确认]，不一致[取消]", iteminfo.toString())!=ENTER)
				{
					gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:%s应用信息获取错误",Tools.getLineInfo(),item.get("packages"));
					continue;
				}
			}
			
			if(mKey!=0)
				gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取应用电量统计信息测试通过");
		} else{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到应用电量统计信息");
		}

		
	}
	private void getEarPhoneInfo(Map<String, String> earPhoneInfo){
		//获取音频头信息
		// case28.1:音频头插拔次数
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.音频头插拔次数：%s\n请于实际值对比，一致[确认]，不一致[取消]", earPhoneInfo.get("count"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取音频头信息测试通过");
	}
	private void getPinPadInfo(Map<String, String> pinPadInfo){
		//密码键盘信息
		// case29.1:最后一次状态
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.最后一次状态：%s\n请于实际值对比，一致[确认]，不一致[取消]", pinPadInfo.get("last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取密码键盘信息测试通过");
	}
	private void getSAMInfo(Map<String, String> sanInfo){
		//SAM信息
		// case30.1:最后一次状态
		if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"1.最后一次状态：%s\n请于实际值对比，一致[确认]，不一致[取消]", sanInfo.get("last_status"))!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:测试失败",Tools.getLineInfo());
			if(mKey!=0)
				return;
		}
		if(mKey!=0)
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取SAM信息测试通过");
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getAccessSiteInfo(){
		DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(myactivity);
		String accessSiteInfo = null;
		try
		{
			accessSiteInfo = service.getDeviceStatisticsInfo("access_site");
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1(gScreenTime,"此版本不支持获取访问网址统计");
			return;
		}
		if(accessSiteInfo.equals("")){
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到访问网址统计信息的<access_site>节点");
			return;
		}
		//将powerConsuInfo解析为Map
		Log.d("accessSiteInfo", "accessSiteInfo====="+accessSiteInfo);
		Document doc = null;
        try {
            doc = DocumentHelper.parseText(accessSiteInfo);
        } catch (DocumentException e) {
        	LoggerUtil.e("parse text error : " + e);
        }
        Element rootElement = doc.getRootElement();
        Map<String,Object> accessInfoMap = new HashMap<String,Object>();
        element2Map(accessInfoMap,rootElement);
        
        boolean ifhashmap = false;
        List<Map> itemList = new ArrayList<Map>();
        if(accessInfoMap.get("item") != null)
        {
	        if (accessInfoMap.get("item").getClass() == ArrayList.class)
	            itemList = (List<Map>) accessInfoMap.get("item");
	        else if (accessInfoMap.get("item").getClass() == HashMap.class)
	            ifhashmap = true;
        }
        
//        List<Map> itemList = new ArrayList<Map>();
//        itemList = (List<Map>) accessInfoMap.get("item");
        
        //
		if(accessInfoMap.get("item") != null&&!ifhashmap ){
			gui.cls_show_msg("将依次显示%d个访问网址统计条目,任意键继续",itemList.size());
			for(Map item:itemList){
				StringBuffer iteminfo = new StringBuffer();
				iteminfo.append("网址:"+item.get("ip")+"\n");
				iteminfo.append("访问总次数:"+item.get("count")+"\n");
				if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"%s一致[确认]，不一致[取消]", iteminfo.toString())!=ENTER)
				{
					gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:访问网址信息获取错误",Tools.getLineInfo());
					continue;
				}
			}
			
			if(mKey!=0)
				gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"获取访问网址统计信息测试通过");
		}
		else if(accessInfoMap.get("item") != null && ifhashmap){
			HashMap<String,String> itemInfo=new HashMap<String, String>();
			itemInfo = (HashMap<String, String>) accessInfoMap.get("item");
			StringBuffer iteminfo = new StringBuffer();
			iteminfo.append("网址:"+itemInfo.get("ip")+"\n");
			iteminfo.append("访问总次数:"+itemInfo.get("count")+"\n");
			if(gui.cls_show_msg1_record(CLASS_NAME, "other7", 30,"%s一致[确认]，不一致[取消]", iteminfo.toString())!=ENTER)
			{
				gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:访问网址信息获取错误",Tools.getLineInfo());
						return;
			}
		}

		else{
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"未获取到访问网址统计信息");
		}
	}
	
	private void testAccessSite(){
		if(gui.cls_show_msg("将以HTTP的形式进行网络连接，访问网址为http://m.baidu.com，成功后access_site节点对应网页访问次数应增加，[取消]退出，[确认]继续")==ESC){
			return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"正在进行网络连接···");	
		HttpURLConnection connection =  null;
		URL url = null;
		String resultData="";
		try{
			url = new URL("http://m.baidu.com");
		}catch (MalformedURLException e) {
			System.out.println (e.getMessage ());//打印出异常信息
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:构造URL出错",Tools.getLineInfo());
			return;
		}
		if(url != null){
			try{
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true); 
				connection.setUseCaches(false);
				int code = connection.getResponseCode();
				connection.connect();
				LoggerUtil.e("connection.getResponseCode():"+connection.getResponseCode());
				while(connection.getResponseCode()==301||connection.getResponseCode()==302)
				{
					connection=(HttpURLConnection)reload(connection);
					code = connection.getResponseCode();
					LoggerUtil.e("301、302");
				}
				if(connection.getResponseCode()==200){
					LoggerUtil.e("200");
				}
			}catch (Exception e) {
				gui.cls_show_msg1_record(CLASS_NAME, "other7", gKeepTimeErr,"line %d:访问网址出错",Tools.getLineInfo());
				return;
            }finally{
            	if(connection != null){
            		connection.disconnect();
            		LoggerUtil.e("关闭连接");
            	}
            }
		}
		gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"网络连接测试通过，access_site节点对应网页访问次数应增加");
	}
	
	private static URLConnection reload(URLConnection uc) throws Exception
	{
		HttpURLConnection huc = (HttpURLConnection) uc;
		if (huc.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || huc.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM)
			return reload(new URL(huc.getHeaderField("location")).openConnection());
		return uc;
	}
	
	
	/**
	 * 使用递归调用将多层级xml转为map
	 * @param map
	 * @param rootElement
	 */
	public static void element2Map(Map<String, Object> map, Element rootElement) {

		// 获得当前节点的子节点
		List<Element> elements = rootElement.elements();
		
		LoggerUtil.d(",element2Map:"+elements.size());
		if (elements.size() == 0) {
			// 没有子节点说明当前节点是叶子节点，直接取值
			map.put(rootElement.getName(), rootElement.getText());
		}/* else if (elements.size() == 1) {
			// 只有一个子节点说明不用考虑list的情况，继续递归
			Map<String, Object> tempMap = new HashMap<String, Object>();
			element2Map(tempMap, elements.get(0));
			map.put(rootElement.getName(), tempMap);
		}*/ else {
			// 多个子节点的话就要考虑list的情况了，特别是当多个子节点有名称相同的字段时
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (Element element : elements) {
				tempMap.put(element.getName(), null);
			}
			Set<String> keySet = tempMap.keySet();
			for (String string : keySet) {
				Namespace namespace = elements.get(0).getNamespace();
				List<Element> sameElements = rootElement.elements(new QName(
						string, namespace));
				// 如果同名的数目大于1则表示要构建list
				if (sameElements.size() > 1) {
					List<Map> list = new ArrayList<Map>();
					for (Element element : sameElements) {
						Map<String, Object> sameTempMap = new HashMap<String, Object>();
						element2Map(sameTempMap, element);
						list.add(sameTempMap);
					}
					map.put(string, list);
				} else {
					// 同名的数量不大于1直接递归
					if (sameElements.get(0).elements().size() == 0) {
						// 没有子节点说明当前节点是叶子节点，直接取值,不再递归
						map.put(string, sameElements.get(0).getText());
					} else {
						// 接下来还有子节点则继续递归
						Map<String, Object> sameTempMap = new HashMap<String, Object>();
						element2Map(sameTempMap, sameElements.get(0));
						map.put(string, sameTempMap);
					}
				}
			}
		}
    }

	private void socketAccessSite(){
		if(gui.cls_show_msg("请先确保可以上网。将以Socket的形式进行网络连接，将访问百度的ip地址，成功后access_site节点对应网页访问次数应增加，[取消]退出，[确认]继续")==ESC){
			return;
		}
		gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"正在进行网络连接···");	
		try {
			InetAddress inetAddress = InetAddress.getByName("www.baidu.com");
			Socket socket = new Socket(inetAddress.getHostAddress(), 80);
			if (socket.isConnected()) {
				System.out.println("连接建立,远程地址:" + socket.getRemoteSocketAddress());
			}
			// 关键！此处在Socket的输出流写入HTTP的GET报文，请服务器做出响应。
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET / HTTP/1.1\r\n");
			bw.write("Host: www.baidu.com\r\n");
			bw.write("\r\n");
			bw.flush();
			// 开始读取远程服务器的响应数据。
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			byte[] buffer = new byte[1024];
			int count = 0;
			while (true) {
				count = bis.read(buffer);
				if (count == -1) {
					break;
				}
				System.out.println(new String(buffer, 0, count, "UTF-8"));
			}
			bw.close();
			bis.close();
			socket.close();
			gui.cls_show_msg1_record(CLASS_NAME, "other7", gScreenTime,"网络连接测试通过");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
