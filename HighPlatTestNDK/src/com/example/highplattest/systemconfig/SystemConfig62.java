package com.example.highplattest.systemconfig;

import android.newland.SettingsManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig62.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 
 * directory 		: 设置和获取客户标记
 * description 		: 
 * related document : 
 * history 		 	: 变更记录			变更时间			变更人员
 *			  		  X5产品导入			20190717			weimj		     
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class SystemConfig62 extends UnitFragment{
	
	private final String TESTITEM = "设置和获取客户标记";
	private String fileName = "SystemConfig62";
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager settingsManager = null;
	
	
	public void systemconfig62() 
	{
		String funcName = "systemconfig62";
		String packageFlag;
		String[] custormerTips = {"标准客户标记","银商客户标记","微信人脸识别客户标记","第三方客户标记"};
		String[] customerFlags = {SettingsManager.FLAG_STANDARD/*标准客户标记*/,SettingsManager.FLAG_UNIONPAY_SIGN/*银商客户标记*/
				,SettingsManager.FLAG_WX_FACEDETECT/*微信客户标记*/,SettingsManager.FLAG_OPEN_TYPE_CUSTOMER/*第三方客户标记*/};
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig62",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}

		/*private & local definition*/
     	boolean ret = false;
		settingsManager = 	(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		String flag = null;

		gui.cls_show_msg1(gScreenTime, "%s测试中...",TESTITEM);
		/*process body*/
		
		//case1:设置为各个支持的客户标记，获取应为各个支持的客户标记
		for (int i=0;i<customerFlags.length;i++) {
			gui.cls_show_msg1(1,"case%d:设置%s",i+1,custormerTips[i]);
			if ((ret = settingsManager.setPackageFlag(customerFlags[i]))==false) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:标记写入失败(%s)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;

			}
			if (!((packageFlag = settingsManager.getPackageFlag()).equals(customerFlags[0]))) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:读出状态不一致(%s)", Tools.getLineInfo(), packageFlag);
				if (!GlobalVariable.isContinue)
					return;
			}
			
			if ((gui.ShowMessageBox(String.format("请确认%s是否写入成功", custormerTips[i]).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s标记写入失败",Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
		}

		// case3:多次设置应为最后一次设置值
		gui.cls_show_msg1(1, "case3:多次设置客户标记应为最后一次设置的客户标记");
		ret = settingsManager.setPackageFlag(customerFlags[1]);
		ret = settingsManager.setPackageFlag(customerFlags[2]);
		if((packageFlag = settingsManager.getPackageFlag()).equals(customerFlags[2])==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s标记写入失败(%s)",Tools.getLineInfo(), TESTITEM,packageFlag);
			if (!GlobalVariable.isContinue)
				return;
		}

		
		// case4:参数异常测试，设置客户标记为null时和""，非支持字段应写入失败，不应改变状态
		flag = settingsManager.getPackageFlag();
		gui.cls_show_msg1(1,"case5:异常测试，设置客户标记为null，不应改变状态");
		boolean ret1 = settingsManager.setPackageFlag(null);
		boolean ret2 = settingsManager.setPackageFlag("");
		boolean ret3 = settingsManager.setPackageFlag("-1");
		boolean ret4 = settingsManager.setPackageFlag("9");
		if(ret1||ret2||ret3||ret4)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:参数异常测试失败(%s,%s,%s,%s)", Tools.getLineInfo(), ret1,ret2,ret3,ret4);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((packageFlag = settingsManager.getPackageFlag()).equals(flag)==false)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:状态不改变(%s)", Tools.getLineInfo(), packageFlag);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case5:重启测试，设置正确的标记值之后重启，重启后的值保持不变
		// case2:从支持的客户标记中随机选取一个设置，应成功
		int randomIndex = (int) (Math.random()*customerFlags.length);
		gui.cls_show_msg1(1,"case2:设置%s",custormerTips[randomIndex]);
		if ((ret = settingsManager.setPackageFlag(customerFlags[randomIndex]))==false) 
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:标记写入失败(%s)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;

		}
		if (!(packageFlag = settingsManager.getPackageFlag()).equals(customerFlags[randomIndex])) 
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:读出状态不一致(%s)", Tools.getLineInfo(), packageFlag);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		if ((gui.ShowMessageBox(String.format("请确认%s是否写入成功", custormerTips[randomIndex]).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr, "line %d:%s标记写入失败",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("是否立即重启，重启后客户标记仍应为%s才可视为测试通过", custormerTips[randomIndex])==ENTER)
			Tools.reboot(myactivity);
		gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}

}
