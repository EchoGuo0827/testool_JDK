package com.example.highplattest.psbc;

import java.util.HashMap;

import android.app.enterpriseadmin.ApplicationPolicy;
import android.app.enterpriseadmin.ExDevicePolicyManager;
import android.app.enterpriseadmin.RestrictionPolicy;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 邮储固件专用
 * file name 		: Psbc2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180504
 * directory 		: 
 * description 		: 邮储固件--权限测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20180504		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Psbc2 extends UnitFragment
{
	/*private & local definition*/
	private final String CLASS_NAME = Psbc2.class.getSimpleName();
	private final String TESTITEM = "权限测试";
	private Gui gui = new Gui(myactivity, handler);
	private ExDevicePolicyManager mExDevicePolicyManager;
	private ApplicationPolicy mAppPolicy;
	private HashMap<String, String[]> interAppPath = new HashMap<String, String[]>();
	private HashMap<String, String> exAppPath = new HashMap<String, String>();
	private SettingsManager settingsManager=null;

	public void psbc2()
	{
		String funcName="psbc2";
		mExDevicePolicyManager = (ExDevicePolicyManager)myactivity.getSystemService("ex_device_policy");
		mAppPolicy = mExDevicePolicyManager.getApplicationPolicy();
		boolean ret = com.gsc.mdm.system.ExDevicePolicyManager.getInstance(myactivity).getSystemPolicy().isBluetoothTransFileAllowed();
		//测试前置:设置可以传输蓝牙文件
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		if(settingsManager.setBluetoothFileTransfer(0)!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line:%d:%s测试前置失败(%s)", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		while(true)
		{
			int nKeyIn = gui.cls_show_msg("厂商私有接口\n0.系统设置配置参数管理\n1.设备接口控制\n2.应用静默安装卸载\n3.接口重启测试\n");
			switch (nKeyIn) {
			case '0':
				system_config();
				break;
				
			case '1':
				device_interface();
				break;
				
			case '2':
				interAppPath.put("mapGD", new String[]{"/mnt/sdcard/Psbc/mapGD.apk","com.autonavi.minimap.custom"});
				interAppPath.put("alipay", new String[]{"/mnt/sdcard/Psbc/alipay.apk","com.eg.android.AlipayGphone"});
				interAppPath.put("meituan_low", new String[]{"/mnt/sdcard/Psbc/meituan_low.apk","com.sankuai.meituan.takeoutnew"});
				interAppPath.put("meituan_high", new String[]{"/mnt/sdcard/Psbc/meituan_high.apk","com.sankuai.meituan.takeoutnew"});
				interAppPath.put("elme", new String[]{"/mnt/sdcard/Psbc/elme.apk","me.ele"});
				interAppPath.put("unExist", new String[]{"/mnt/sdcard/Psbc/unExsit.apk","com.newland.unexist"});
				interAppPath.put("damage", new String[]{"/mnt/sdcard/Psbc/damage.apk",""});
				interAppPath.put("errFormat", new String[]{"/mnt/sdcard/Psbc/err.png",""});
				interAppPath.put("bigApp", new String[]{"/mnt/sdcard/Psbc/game_400M.apk","com.netease.ldxy.baidu"});
				
				exAppPath.put("mapGD", "/storage/sdcard1/Psbc/mapGD.apk");
				exAppPath.put("alipay", "/storage/sdcard1/Psbc/alipay.apk");
				exAppPath.put("elme", "/storage/sdcard1/Psbc/elme.apk");
				int key = gui.cls_show_msg("0.单线程安装卸载APP\n1.多线程安装APP\n");
				switch (key) {
				case '0':
					app_install();
					break;
					
				case '1':
					mul_thread_intall();
					break;

				default:
					break;
				}
				
				break;
				
			case '3':
				reboot_test();
				break;
								
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
			// 测试后置，全部开启
			RestrictionPolicy resPolicy = mExDevicePolicyManager.getRestrictionPolicy();
			resPolicy.allowAutoTime(true);
			resPolicy.allowBluetooth(true);
			resPolicy.allowCellularData(true);
			resPolicy.allowLocationService(true);
			resPolicy.allowMassStorage(true);
			resPolicy.allowMTP(true);
			resPolicy.allowPTP(true);
			resPolicy.allowSDCard(true);
			resPolicy.allowUserEditApn(true);
			resPolicy.allowWiFi(true);
		}
	}
	
	public void system_config()
	{
		String funcName="system_config";
		/* private & local definition */
		boolean ret = false;
		RestrictionPolicy resPolicy = mExDevicePolicyManager.getRestrictionPolicy();
		
		/*process body*/
		// 测试前置
		if(gui.cls_show_msg("测试前请确保设置的VPN选项已调出\n是[确认],否[取消]")==ESC)
		{
			return;
		}
		// case1.1:设置为false不允许用户使用蓝牙
		if((ret = resPolicy.allowBluetooth(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用设置的蓝牙,是否为关闭、无法操作蓝牙且其他设备无法搜索到该蓝牙设备\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.2:设置为true用户可以正常使用蓝牙
		if((ret = resPolicy.allowBluetooth(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置的蓝牙,是否可以打开、关闭、扫描、配对、传输文件,蓝牙最后状态请保持开启\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.3:设置为false不允许用户使用蓝牙
		if((ret = resPolicy.allowBluetooth(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置的蓝牙,是否为关闭、无法操作蓝牙且其他设备无法搜索到该蓝牙设备\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.1:设置为true用户可以正常使用wifi
		if((ret = resPolicy.allowWiFi(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置的wlan,是否可以打开、扫描、连接AP、上网、关闭,wlan最后状态请保持开启\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.2:设置为false不允许用户使用wifi
		if((ret = resPolicy.allowWiFi(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置的wlan,是否关闭、无法操作且自检-网络无法上网\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3.1:设置为true允许用户使用定位服务
		if((ret = resPolicy.allowLocationService(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置的位置信息,是否可开关位置信息,开启位置信息后,是否可使用自检定位、高德定位\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3.2:设置为false禁止用户使用定位服务
		if((ret = resPolicy.allowLocationService(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请进入设置的位置信息,是否为关闭状态且无法操作位置信息,自检-GPS无法搜索到卫星\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case4.1:设置为false不允许运营商提供的3G数据
		if((ret = resPolicy.allowCellularData(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置的移动网络,是否无法设置移动网络\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4.2:设置为true用户可以正常使用3G数据
		if((ret = resPolicy.allowCellularData(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置的移动网络,是否可以设置移动网络4G、3G、2G并且可以使用自检上网\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case5.1:设置为true表示允许用户正常编辑APN
		if((ret = resPolicy.allowUserEditApn(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-移动网络-APN,是否能编辑APN配置文件\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5.2:设置为false表示不允许用户编辑APN
		if((ret = resPolicy.allowUserEditApn(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-移动网络-APN,是否无法编辑APN配置文件\n是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case6.1:设置为false表示不允许用户修改自动时间
		if((ret = resPolicy.allowAutoTime(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-日期和时间,自动确定时间与日期是否关闭并且可以手动设置日期与设置时间\n是[确定],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case6.2:设置为true表示允许用户正常修改自动时间
		if((ret = resPolicy.allowAutoTime(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-日期和时间,是否能取消自动确定时间并使用手动设置时间\n是[确定],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case7.1:设置为false,禁止用户编辑VPN,VPN的编辑置灰
		if((ret = resPolicy.allowUserEditVpn(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-VPN,是否无法编辑VPN配置文件\n是[确定],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case7.2:允许用户编辑VPN,VPN可编辑
		if((ret = resPolicy.allowUserEditVpn(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("进入设置-VPN,是否能编辑VPN配置文件\n是[确定],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "移动网络需分别测试移动、联通、电信则系统设置配置参数管理测试通过");
	}
	
	private void device_interface()
	{
		/* private & local definition */
		String funcName="device_interface";
		boolean ret = false;
		RestrictionPolicy resPolicy = mExDevicePolicyManager.getRestrictionPolicy();
		
		/*process body*/
		// SD卡和MTP的设置需要重启
		// case1.1:设置true表示用户可以使用TF卡
		gui.cls_printf("允许用户使用TF卡,重启生效".getBytes());
		if((ret = resPolicy.allowSDCard(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("是否立即重启,是[确认],否[其他]\n重启后对TF卡进行查看文件、创建文件、删除文件成功则视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
		// case1.2:设置false表示不允许用户使用TF卡
		if((ret = resPolicy.allowSDCard(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("是否立即重启,是[确认],否[其他]\n重启后对TF卡进行查看文件、创建文件、删除文件失败则视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
		// case2.1:设置为false不允许用户使用MTP(媒体传输协议，连电脑自动生效)
		gui.cls_show_msg1(1, "不允许用户使用MTP测试...");
		if((ret = resPolicy.allowMTP(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("是否立即重启,是[确认],否[其他],重启后使用USB线连接PC与POS,PC不能访问POS内的信息则视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		// case2.2:设置为true表示用户可以使用MTP
		gui.cls_show_msg1(1, "允许用户使用MTP测试...");
		if((ret = resPolicy.allowMTP(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("是否立即重启,是[确认],否[其他],重启后使用USB线连接PC与POS,PC能访问POS内的信息则视为测试通过")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		gui.cls_show_msg1(2, "各个case重启后操作均测试通过才可视为测试通过");
	}
	
	/**根据马鑫汶建议，判断一个应用是否安装卸载成功使用/data/data/包名的方式去判断*/
	private void app_install()
	{
		/* private & local definition */
		String funcName="app_install";
		boolean ret = false;
		int iRet = -1,iRet1=-1;
		FileSystem fileSystem = new FileSystem();

		
		// 测试前置
		if(gui.cls_show_msg("请确保已安装TF卡,并把SVN服务器上的Psbc文件夹push到内置SD卡Psbc目录和外置TF的Psbc目录下\n是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1(2, "line %d:请先完成测试前置操作再进入本测试用例",Tools.getLineInfo());
			return;
		}
		// case1:异常测试
		// case1.1:安装不存在的apk文件，应返回失败
		gui.cls_printf("安装不存在apk文件测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("unExist")[0], false))==true)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.2:安装破损的apk文件在内置的SD卡，应失败
		gui.cls_printf("安装破损文件测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("damage")[0], true))==true)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.3:安装非apk格式文件，应返回失败
		gui.cls_printf("安装非apk格式文件测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("errFormat")[0], true))==true)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:安装低版本美团外卖应用在内置的SD卡，应成功
		gui.cls_printf("安装美团外卖应用于内置SD卡测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("meituan_low")[0], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.isAppInstalled(myactivity,interAppPath.get("meituan_low")[1]))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s未找到【%s】包名",Tools.getLineInfo(),TESTITEM,interAppPath.get("meituan_low")[1]);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3:安装美团外卖应用在外置的TF卡，应报已存在该应用(此case会返回true)
		gui.cls_printf("重复安装低版本美团外卖应用测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("meituan_low")[0], true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(!Tools.isAppInstalled(myactivity,interAppPath.get("meituan_low")[1]))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s未找到【%s】包名",Tools.getLineInfo(),TESTITEM,interAppPath.get("meituan_low")[1]);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4:安装高版本的
		gui.cls_printf("安装高版本美团外卖应用测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("meituan_high")[0], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.isAppInstalled(myactivity,interAppPath.get("meituan_high")[1]))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s未找到【%s】包名",Tools.getLineInfo(),TESTITEM,interAppPath.get("meituan_high")[1]);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否存在美团外卖应用,请点击美团外卖应用使其产生缓存数据,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 判断缓存文件是否存在
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("meituan_high")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case4:安装支付宝应用在外置的TF卡
		gui.cls_printf("安装支付宝应用于TF卡测试...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("alipay")[0], true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.isAppInstalled(myactivity,interAppPath.get("alipay")[1]))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s未找到【%s】包名",Tools.getLineInfo(),TESTITEM,interAppPath.get("alipay")[1]);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否存在支付宝应用,请点击支付宝应用使其产生缓存数据,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("alipay")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case5:高德地图.apk文件位于TF卡，安装在内置SD卡应成功
		gui.cls_printf("安装高德地图于内置SD卡测试...".getBytes());
		if((ret = mAppPolicy.installApplication(exAppPath.get("mapGD"), false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.isAppInstalled(myactivity,interAppPath.get("mapGD")[1]))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s未找到【%s】包名",Tools.getLineInfo(),TESTITEM,interAppPath.get("mapGD")[1]);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否存在高德地图应用,请点击高德地图应用使其产生缓存数据,是[确认],否[其他]",interAppPath.get("mapGD")[1],interAppPath.get("mapGD")[1])!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("mapGD")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case6:饿了么.apk文件位于TF卡，安装在外置TF卡应成功
		gui.cls_printf("安装饿了么在TF卡测试...".getBytes());
		if((ret =mAppPolicy.installApplication(exAppPath.get("elme"), true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!Tools.isAppInstalled(myactivity,interAppPath.get("elme")[1]))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s未找到【%s】包名",Tools.getLineInfo(),TESTITEM,interAppPath.get("elme")[1]);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否存在饿了么应用,请点击饿了么应用使其产生缓存数据,是[确认],否[其他]",interAppPath.get("elme")[1],interAppPath.get("elme")[1])!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败",Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("elme")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
//		// case7:安装400M左右的apk,安装在内置SD卡的应成功
//		gui.cls_printf("安装400M的APP在内置SD卡测试...".getBytes());
//		if((ret =mAppPolicy.installApplication(interAppPath.get("bigApp")[0], false))==false)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
//		if(gui.cls_show_msg("应用界面是否存在乱斗西游2应用,请点击乱斗西游2应用使其产生缓存数据后,内置SD卡的/Android/data/目录下存在%s文件夹,外置TF卡的/Android/data/目录下不存在%s文件夹\n是[确认],否[其他]",interAppPath.get("bigApp")[1],interAppPath.get("bigApp")[1])!=ENTER)
//		{
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
//			if(GlobalVariable.isContinue==false)
//				return;
//		}
		
		// case8:卸载不存在的应用，应失败
		gui.cls_printf("卸载不存在的APP测试...".getBytes());
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("unExist")[1], true))==true)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case9.1:卸载高德地图的APP,保留缓存
		gui.cls_printf("卸载高德地图APP测试...".getBytes());
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("mapGD")[1], true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否已卸载高德地图应用,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("mapGD")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case9.2:卸载支付宝的APP,不保留缓存
		gui.cls_printf("卸载支付宝APP测试...".getBytes());
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("alipay")[1], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否已卸载支付宝应用,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("alipay")[1]))==SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case10.1:卸载美团外卖应用,保留缓存后,再次安装美团外卖应用应该成功
		gui.cls_printf("卸载美团外卖APP测试...".getBytes());
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("meituan_high")[1], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((ret = mAppPolicy.installApplication(interAppPath.get("meituan_high")[0], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否已存在美团外卖应用,请点击美团外卖应用使其产生缓存数据,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("meituan_high")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case10.2:卸载位于TF卡的饿了么应用,清除缓存后,再次安装饿了吗应用应该成功
		gui.cls_printf("卸载饿了么APP测试...".getBytes());
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("elme")[1], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("应用界面是否已卸载饿了么应用,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("elme")[1]))==SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case11.1:连续安装2-3个应用，应能顺序安装或卸载
		gui.cls_printf("连续安装饿了么和支付宝应用...".getBytes());
		if((ret = mAppPolicy.installApplication(interAppPath.get("elme")[0], true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((ret = mAppPolicy.installApplication(interAppPath.get("alipay")[0], true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(gui.cls_show_msg("应用界面是否已存在饿了么和支付宝应用,请点击饿了么和支付宝应用使其产生缓存数据,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("elme")[1]))!=SDK_OK||(iRet1 = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("alipay")[1]))!=SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d,%d)", Tools.getLineInfo(),TESTITEM,iRet,iRet1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case11.2:连续卸载多个应用，应能成功卸载
		gui.cls_printf("连续卸载饿了么和支付宝应用...".getBytes());
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("elme")[1],true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((ret = mAppPolicy.uninstallApplication(interAppPath.get("alipay")[1], false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(gui.cls_show_msg("应用界面是否已卸载饿了么和支付宝应用,是[确认],否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("elme")[1]))!=SDK_OK||(iRet1 = fileSystem.JDK_FsExist("/data/data/"+interAppPath.get("alipay")[1]))==SDK_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%d,%d)", Tools.getLineInfo(),TESTITEM,iRet,iRet1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "应用静默安装卸载测试通过");
	}
	
	private void reboot_test()
	{
		boolean value = false;
		RestrictionPolicy resPolicy = mExDevicePolicyManager.getRestrictionPolicy();
		value = gui.cls_show_msg("接口设置为true,设置选项全部允许用户操作,接口设置为false,设置选项部分被禁止用户操作\ntrue[确认],false[其他]")==ENTER?true:false;
		resPolicy.allowBluetooth(value);
		resPolicy.allowWiFi(value);
		resPolicy.allowLocationService(value);
		resPolicy.allowCellularData(value);
		resPolicy.allowUserEditApn(value);
		resPolicy.allowAutoTime(value);
		resPolicy.allowSDCard(value);
		resPolicy.allowMTP(value);
		Tools.reboot(myactivity);
		// case2:不论设置为true还是false重启后设备自动进入休眠后唤醒正常
		gui.cls_show_msg("设备即将重启,重启后用户%s操作设置的蓝牙、wifi、移动网络、时间与日期等选项且设备自动进入休眠后可正常唤醒才可视为测试通过",value==true?"能":"不能");
	}
	
	/**
	 * 多线程安装APP测试
	 */
	private void mul_thread_intall()
	{
		// 测试前置:卸载即将要安装的应用
		gui.cls_printf("多线程安装APP测试...".getBytes());
		mAppPolicy.uninstallApplication(interAppPath.get("meituan_high")[1], false);
		mAppPolicy.uninstallApplication(interAppPath.get("elme")[1], false);
		mAppPolicy.uninstallApplication(interAppPath.get("bigApp")[1], false);
		mAppPolicy.uninstallApplication(interAppPath.get("alipay")[1], false);
		mAppPolicy.uninstallApplication(interAppPath.get("mapGD")[1], false);
		// 线程1安装美团外卖，安装在内置SD卡
		mtThread.start();
		// 线程2安装饿了么，安装在外置TF卡
		eleThread.start();
		// 线程3安装大应用乱斗西游2，安装在外置TF卡
		baiduThread.start();
		// 线程4安装支付宝，安装在内置SD卡
		aliThread.start();
		// 线程5安装高德地图，安装在内置SD卡
		GDThread.start();
		gui.cls_show_msg1(2,"已开启5个线程安装美团外卖、饿了吗、支付包、乱斗西游2、高德地图,过2-3分钟去应用桌面查看应存在这几个APP才可视为测试通过");
	}
	
	// 安装美团外卖
	Thread mtThread = new Thread()
	{
		public void run() 
		{
			mAppPolicy.installApplication(interAppPath.get("meituan_high")[0], false);
		};
	};
	
	Thread eleThread = new Thread()
	{
		public void run() 
		{
			mAppPolicy.installApplication(interAppPath.get("elme")[0], true);
		};
	};
	
	Thread baiduThread = new Thread()
	{
		public void run() 
		{
			mAppPolicy.installApplication(interAppPath.get("bigApp")[0], true);
		};
	};
	
	Thread aliThread = new Thread()
	{
		public void run() 
		{
			mAppPolicy.installApplication(interAppPath.get("alipay")[0], false);
		};
	};
	
	Thread GDThread = new Thread()
	{
		public void run() 
		{
			mAppPolicy.installApplication(interAppPath.get("mapGD")[0], false);
		};
	};
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// 测试后置，全部开启
		RestrictionPolicy resPolicy = mExDevicePolicyManager.getRestrictionPolicy();
		resPolicy.allowAutoTime(true);
		resPolicy.allowBluetooth(true);
		resPolicy.allowCellularData(true);
		resPolicy.allowLocationService(true);
		resPolicy.allowMassStorage(true);
		resPolicy.allowMTP(true);
		resPolicy.allowPTP(true);
		resPolicy.allowSDCard(true);
		resPolicy.allowUserEditApn(true);
		resPolicy.allowWiFi(true);
	}

}
