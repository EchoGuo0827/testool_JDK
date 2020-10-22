package com.example.highplattest.psbc;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.gsc.mdm.system.ExDevicePolicyManager;
import com.gsc.mdm.system.SystemPolicy;

import android.app.enterpriseadmin.RestrictionPolicy;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.newland.os.NlBuild;

/************************************************************************
 * 
 * module 			: 邮储固件专用
 * file name 		: Psbc4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180508
 * directory 		: 
 * description 		: 邮储固件--系统管理
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20180508		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Psbc4 extends UnitFragment{
	/*private & local definition*/
	private final String CLASS_NAME = Psbc4.class.getSimpleName();
	private final String TESTITEM = "系统管理";
	private Gui gui = new Gui(myactivity, handler);
	private ExDevicePolicyManager mExDevicePolicyManager;
	
	public void psbc4()
	{
		mExDevicePolicyManager = ExDevicePolicyManager.getInstance(myactivity);
		while(true)
		{
			int nKeyIn = gui.cls_show_msg("%s\n0.蓝牙相关\n1.系统升级\n2.OTA版本获取\n3.获取SDK服务版本\n", TESTITEM);
			switch (nKeyIn) 
			{
			case '0':
				systemBlue();
				break;
				
			case '1':
				systemUpdate();
				break;
				
			case '2':
				systemVersion();
				break;
				
			case '3':
				getAppVersionCode("cc.psbc.service");
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	private void systemBlue()
	{
		String funcName="systemBlue";
		boolean ret = false;
		SystemPolicy systemPolicy = mExDevicePolicyManager.getSystemPolicy();
		// 测试前置：开启蓝牙
		android.app.enterpriseadmin.ExDevicePolicyManager mExDevicePolicyManager2 = (android.app.enterpriseadmin.ExDevicePolicyManager)myactivity.getSystemService("ex_device_policy");
		RestrictionPolicy resPolicy = mExDevicePolicyManager2.getRestrictionPolicy();
		resPolicy.allowBluetooth(true);
		
		// case1:设置为允许蓝牙传输文件，应可正常的配对及文件传输,获取此时的蓝牙是否允许传输文件为true
		if((ret = systemPolicy.allowBluetoothTransFile(true))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = systemPolicy.isBluetoothTransFileAllowed())==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请将本设备的蓝牙与另外一个设备的蓝牙进行配对、连接、文件接收、发送,是否成功\n是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:设置为禁止蓝牙传输文件，应可配对但不可进行文件传输及数据收发，获取此时的蓝牙是否允许传输文件为false,要重新开关设置中的蓝牙
		if((ret = systemPolicy.allowBluetoothTransFile(false))==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((ret = systemPolicy.isBluetoothTransFileAllowed())==true)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请将本设备的蓝牙与另外一个设备的蓝牙进行配对、连接后，进行蓝牙文件接收、发送,是否不成功\n是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "蓝牙相关测试通过");
	}
	
	/**
	 * 固件升级
	 */
	private void systemUpdate()
	{
		String funcName="systemUpdate";
		boolean ret = false;
		String otaPath = null;
		SystemPolicy systemPolicy = mExDevicePolicyManager.getSystemPolicy();
		//case4：是否需要系统升级接口测试wangxy20180514
		//case4.1异常测试
		if((ret=systemPolicy.isUpdate(""))!=false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret=systemPolicy.isUpdate(null))!=false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret=systemPolicy.isUpdate("T.unexist"))!=false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//case4.2仅传入版本号高于当前版本 NlBuild.VERSION.NL_FIRMWARE时系统需要升级
		String version=NlBuild.VERSION.NL_FIRMWARE;//系统版本号
		if((ret=systemPolicy.isUpdate( version))!=false)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s版本号%s是否更新，测试失败(true)", Tools.getLineInfo(),TESTITEM,version);
			if (GlobalVariable.isContinue == false)
				return;
		}
		//取出版本号的后两位,如T2.1.52中的52;
		int myVersion=Integer.valueOf( version.subSequence(version.length()-2, version.length()).toString());
		String higherVersion=version.substring(0, version.length()-2)+(myVersion+1);
		String lowerVersion=version.substring(0, version.length()-2)+(myVersion-1);
		if((ret=systemPolicy.isUpdate(higherVersion))!=true)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(true)", Tools.getLineInfo(),TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if(systemPolicy.isUpdate(lowerVersion)){//系统是否需要升级
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(true)", Tools.getLineInfo(),TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		
		// startUpdateOS测试前置：放置测试文件
		if(gui.cls_show_msg("请确保/mnt/sdcard/Psbc/目录下已放置已解压的OTA升级包下的带有版本号的文件夹,未放置请先放置,继续[确认],退出[取消]")==ESC)
		{
			return;
		}
		// /mnt/sdcard/update/下寻找是否有匹配格式的OTA包
		File file = new File("/mnt/sdcard/Psbc/");
		File[] fileList= file.listFiles();
		
		if(fileList.length<=0)
		{
			gui.cls_show_msg("未找到/mnt/sdcard/Psbc/目录，请新建Psbc文件夹并放置已解压的OTA升级包下的带有版本号的文件夹后再进入本用例,任意键退出");
			return;
		}
		for(File fileS:fileList)
		{
			if(fileS.isDirectory()==true&&(fileS.getAbsolutePath().startsWith("/mnt/sdcard/Psbc/T")||fileS.getAbsolutePath().startsWith("/mnt/sdcard/Psbc/V"))==true)// 是文件非目录
			{
				otaPath = fileS.getAbsolutePath();
				break;
			}
		}
		if(otaPath==null)
		{
			gui.cls_show_msg("未找到.zip后缀的OTA包,请先放置已解压的OTA升级包下的带有版本号的文件夹包于/mnt/sdcard/Psbc/目录下再进入本用例,任意键退出");
			return;
		}else{
			//第一次进入进行异常测试和向上更新OTA包测试，第二次再次更新同一个OTA包测试
			if (gui.cls_show_msg("本用例需完整测试两次，是否第一次测试本用例\n是[确认],否[取消]") != ENTER)
			{
				// case4:多次升级同一个版本的OTA包不应出现异常
				if((ret = systemPolicy.startUpdateOS(otaPath))==false)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(false),文件路径为：%s", Tools.getLineInfo(),TESTITEM,otaPath);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}else{
				// case1:异常测试,传入错误的String,null,以及不存在的OTA包
				if((ret = systemPolicy.startUpdateOS(null))==true)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
					if(GlobalVariable.isContinue==false)
						return;
				}
				
				if((ret = systemPolicy.startUpdateOS(""))==true)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
					if(GlobalVariable.isContinue==false)
						return;
				}
				
				if((ret = systemPolicy.startUpdateOS("/mnt/sdcard/Psbc/errOta.zip"))==true)//不存在
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
					if(GlobalVariable.isContinue==false)
						return;
				}
				
				// case2.1:非OTA格式的包进行测试应失败
				if((ret = systemPolicy.startUpdateOS("/mnt/sdcard/Psbc/err.png"))==true)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
					if(GlobalVariable.isContinue==false)
						return;
				}
				// case2.2:不符合OTA格式的zip包进行测试应失败
				if((ret = systemPolicy.startUpdateOS("/mnt/sdcard/Psbc/Uiautomator.zip"))==true)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s异常测试失败(true)", Tools.getLineInfo(),TESTITEM);
					if(GlobalVariable.isContinue==false)
						return;
				}
				
				// case3:正常测试:根据补丁号以及补丁路径进行系统升级
				if((ret = systemPolicy.startUpdateOS(otaPath))==false)
				{
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(false),文件路径为：%s", Tools.getLineInfo(),TESTITEM,otaPath);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "系统升级测试通过,升级完成请去2.OTA版本获取查看OTA版本号");
	}
	
	
	private void systemVersion()
	{
		String funcName="systemVersion";
		SystemPolicy systemPolicy = mExDevicePolicyManager.getSystemPolicy();
		
		// case1:获取OTA升级的目前版本
		String version = systemPolicy.getPosVerInfo();
		if(gui.cls_show_msg("获取补丁版本号=%s,请与设备的补丁版本号对比,是否一致\n是[确认],否[取消]",version)!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(ver=%s)", Tools.getLineInfo(),TESTITEM,version);
			return;
		}
		// case2:OTA升级后获取版本
		if(gui.cls_show_msg("请进行OTA升级后进入case,是否已升级过OTA\n是[确认],否[取消]")==ENTER)
		{
			version = systemPolicy.getPosVerInfo();
			if(gui.cls_show_msg("获取补丁版本号,请与设备的补丁版本号对比,是否一致\n是[确认],否[取消]",version)!=ENTER)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(ver=%s)",Tools.getLineInfo(),TESTITEM,version);
				return;
			}
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"获取补丁版本号测试通过");
	}
	
	private void getAppVersionCode(String packageName)
	{
		gui.cls_show_msg("测试前先确保已安装SVN上的systemservice.apk,完成后任意键继续");
		int versioncode = -1;
		try {
			PackageManager pm = myactivity.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);
			if(pi!=null)
			{
				versioncode = pi.versionCode;
				if(gui.cls_show_msg("获取到服务的版本为:%d,与设置-关于设备-SDK设备服务版本号是否一致,是[确认],否[取]", versioncode)!=ENTER)
				{
					gui.cls_show_msg1_record(CLASS_NAME,"getAppVersionCode",gKeepTimeErr, "line %d:获取到服务的版本错误(%d)", Tools.getLineInfo(),versioncode);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		SystemPolicy systemPolicy = mExDevicePolicyManager.getSystemPolicy();
		// 测试后置，选项全部打开
		systemPolicy.allowBluetoothTransFile(true);
	}

}
