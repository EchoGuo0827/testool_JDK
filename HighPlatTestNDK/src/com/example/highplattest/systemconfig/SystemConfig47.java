package com.example.highplattest.systemconfig;

import java.util.Arrays;
import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ShowDialog;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig47.java 
 * Author 			: zhangxinj	
 * version 			: 
 * DATE 			: 20170222
 * directory 		: 
 * description 		: 设置当前设备安装App时使用的签名验证方案
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhangxinj			20170222		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig47 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "设置当前设备安装App时使用的签名验证方案";
	private String fileName="SystemConfig47";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig47()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig47",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}

		boolean flag = false;
		 settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		/*process body*/
		// 测试前置
		gui.cls_show_msg("确保本机上没有安装任何证书、卸载所有相关测试apk，用升级工具安装SVN上验签测试apk文件夹下的apk，完成任意键继续");
		//case1:异常测试
		String errorValue[]=new String[]{"err1","err2"};
		if((flag = settingsManager.setAppSignatureVerificationScheme(errorValue))!=false){
			gui.cls_show_msg1_record(fileName,"systemconfig47",gKeepTimeErr, "line %d: 异常测试失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2：设置为null，默认使用新大陆验签体系
		if((flag = settingsManager.setAppSignatureVerificationScheme(null))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig47",gKeepTimeErr, "line %d: 设置默认新大陆验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("安装新大陆验签.apk应成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"systemconfig47",gKeepTimeErr, "line %d:新大陆验签下安装失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		//case3:自选测试
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new ShowDialog().configSignaturePara(myactivity,"验签参数设置");
			}
		});
		synchronized (myactivity) {
			try {
				myactivity.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GlobalVariable.RETURN_VALUE!=SUCC)
			return;
		if((flag = settingsManager.setAppSignatureVerificationScheme(GlobalVariable.signatureList))==false){
			gui.cls_show_msg1_record(fileName,"systemconfig47",gKeepTimeErr, "line %d: 设置验签流程失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(gui.ShowMessageBox(("验签设置为"+Arrays.toString(GlobalVariable.signatureList)+",请手动安装相关apk" +
				",设置成功的验签体系对应apk应安装成功，未设置的对应apk应验签失败").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig47",gKeepTimeErr, "line %d:安装失败(%s)", Arrays.toString(GlobalVariable.signatureList));
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig47",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		
	}

	@Override
	public void onTestUp() {
		 gui = new Gui(myactivity, handler);
	
	}

	@Override
	public void onTestDown() {
		if(settingsManager!=null)
		{
			//测试后置
			if(settingsManager.setAppSignatureVerificationScheme(null)==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig47",gKeepTimeErr, "line %d: 还原设置失败", Tools.getLineInfo());
			}
		}
		settingsManager = null;
		gui = null;
	}
}
