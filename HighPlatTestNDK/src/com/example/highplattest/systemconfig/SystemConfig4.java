package com.example.highplattest.systemconfig;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.newland.SettingsManager;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141205
 * directory 		: 
 * description 		: 测试系统设置setAllApkVerifyEnable和setAllApkVerifyDisable
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20141205			created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig4 extends UnitFragment
{
	private boolean ret;
	private SettingsManager settingsManager=null;
	private final String TESTITEM = "Apk验签控制(setAllApkVerifyEnable和setAllApkVerifyDisable)";
	private String fileName="SystemConfig4";
	Gui gui = null;
	String[] appName = {GlobalVariable.sdPath+"apk/A1.apk",GlobalVariable.sdPath+"apk/A2.apk",GlobalVariable.sdPath+"apk/B1.apk",GlobalVariable.sdPath+"apk/B2.apk"};
	
	
	public void systemconfig4()
	{
		// 海外不支持该接口，海外是全验签
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N910)
		{
			gui.cls_show_msg1_record(fileName, "systemconfig60", 1, "N910海外不支持setAllApkVerifyEnable和setAllApkVerifyDisable");
			return;
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元测试\n1.验签开关控制\n", fileName);
			switch (nkeyIn) {
			case '0':
				unitTest();
				break;
				
			case '1':
				verifyControl();
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	public void unitTest() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig4",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		/*private & local definition*/
		String message;
	
		/*process body*/
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		// 测试前置，更新支付证书
		message = "测试前请确保证书已更新与服务器一致，需关闭控制台并把服务器上的测试apk放置到"+GlobalVariable.sdPath+"apk/目录下，完成点击任意键";
		gui.cls_show_msg(message);
		
		// 测试前置，将测试的apk先卸载干净
		message = "测试前先把A1、A2、B1、B2的apk卸载，A1(与K21通信，签名错误)、A2(与K21通信，签名正确)、B1(不与K21通信，签名错误)、B2(不与K21通信，签名正确)";

		gui.cls_show_msg(message);
		// case1:设置系统是对所有待安装的APK进行签名验证，只有含有支付签名的apk可以安装
		// 预期结果:A1.apk不能安装成功，A2.apk能够安装成功，B1.apk不能安装成功，B2.apk能安装成功
		try
		{
			if((ret = settingsManager.setAllApkVerifyEnable())==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig4",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
				{
					settingsManager.setAllApkVerifyEnable();
					return;
				}
			}
		}catch(NoSuchMethodError e)
		{
			gui.cls_show_msg1(2, "该用例不支持");
			return;
		}
		
		message = "手动安装A1、A2、B1、B2，预期A1.apk安装失败，A2.apk安装成功，B1.apk安装失败，B2.apk安装成功，结果如上所诉点击是，不一致点击否";

		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig4",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				settingsManager.setAllApkVerifyEnable();
				return;
			}
		}
		
		message = "请先卸载A2、B2，卸载完成点击任意键";
		gui.cls_show_msg(message);
		// case2:设置系统只对与K21通信的APK进行签名验证，只有与K21通信的非支付签名的apk不能安装，其他的apk都是能安装的
		// 预期结果:A1.apk安装失败，A2.apk能够安装成功，B1.apk能安装成功，B2.apk能安装成功
		if((ret = settingsManager.setAllApkVerifyDisable()) == false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig4",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				settingsManager.setAllApkVerifyEnable();
				return;
			}
		}
		// 隐形安装
		
		message = "手动安装A1、A2、B1、B2，预期：A1.apk安装失败，A2.apk安装成功，B1.apk安装成功，B2.apk安装成功，结果如上所诉点击是，不一致点击否";

		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig4",gKeepTimeErr,"line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
			{
				settingsManager.setAllApkVerifyEnable();
				return;
			}
		}
			
		message = "请卸载A2、B1、B2，卸载完成点击任意键";
		gui.cls_show_msg(message);
		gui.cls_show_msg1_record(fileName,"systemconfig4",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	// 验签控制 add by 20190729
	private void verifyControl()
	{
		boolean isSucc;
		int nkey = gui.cls_show_msg("%s\n0.开启统一验签\n1.关闭统一验签\n2.(仅X5支持)删除白名单", fileName);
		switch (nkey) {
		case '0':
			isSucc=settingsManager.setAllApkVerifyEnable();
			gui.cls_show_msg1(2, "开启统一验签%s", isSucc?"成功":"失败");
			break;
			
		case '1':
			isSucc = settingsManager.setAllApkVerifyDisable();
			gui.cls_show_msg1(2, "关闭统一验签%s", isSucc?"成功":"失败");
			break;
			
		case '2':
			isSucc = settingsManager.setAllowReplaceApp(null);
			gui.cls_show_msg1(2, "清空白名单%s", isSucc?"成功":"失败");
			break;

		default:
			break;
		}
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
