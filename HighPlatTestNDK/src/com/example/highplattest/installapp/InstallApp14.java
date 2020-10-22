package com.example.highplattest.installapp;

import java.util.Arrays;

import android.newland.SettingsManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 验签方案测试
 * file name 		: InstallApp14.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20190805
 * directory 		: 
 * description 		: 白名单验签安装第三方应用
 * related document : 
 * history 		 	: 变更记录				                                    变更时间			变更人员
 * 					 CPOS支持 增加getAllowReplaceApp()接口的测试           20200526           weimj
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class InstallApp14 extends UnitFragment {
	private final String CLASS_NAME =InstallApp14.class.getSimpleName();
	private final String TESTITEM = "白名单验签安装第三方应用";
	private  Gui gui = new Gui(myactivity, handler);
	
	
	//installtest1.apk对应的包名和公钥
	final String mAllowReplaceApp_Right[] = {
	            "com.example.installtest1:OpenSSLRSAPublicKey{modulus=d6931904dec60b24b1edc762e0d9d8253e3ecd6ceb1de2ff068ca8e8bca8cd6bd3786ea70aa76ce60ebb0f993559ffd93e77a943e7e83d4b64b8e4fea2d3e656f1e267a81bbfb230b578c20443be4c7218b846f5211586f038a14e89c2be387f8ebecf8fcac3da1ee330c9ea93d0a7c3dc4af350220d50080732e0809717ee6a053359e6a694ec2cb3f284a0a466c87a94d83b31093a67372e2f6412c06e6d42f15818dffe0381cc0cd444da6cddc3b82458194801b32564134fbfde98c9287748dbf5676a540d8154c8bbca07b9e247553311c46b9af76fdeeccc8e69e7c8a2d08e782620943f99727d3c04fe72991d99df9bae38a0b2177fa31d5b6afee91f,publicExponent=3}"
	    };
	private String appList[];
	
	public void installapp14()
	{
		if(GlobalVariable.currentPlatform!=Model_Type.X5)
		{
			gui.cls_printf("该案例不支持非CPOS产品".getBytes());
			return;
		}
		String funcName="installapp14";
		boolean ret = false;
		gui.cls_show_msg("按任意键开启统一验签。。。。。" );
		SettingsManager settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		
		//case1 开启统一验签
		if(!(ret=settingsManager.setAllApkVerifyEnable())){
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:开启统一验签失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg("开启统一验签成功。。。。。请安装InstalltestV1.apk，应安装失败。按任意键关闭统一验签。。。" );
		//case2 关闭统一验签
		if (!(ret=settingsManager.setAllApkVerifyDisable())){
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:关闭统一验签失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
			
		}
		gui.cls_show_msg("关闭统一验签成功。。。。。请安装InstalltestV1.apk，应安装成功。按任意键设置第三方白名单应用。。。请将InstalltestV2.apk放置在sdcard目录下" );
		//case3 设置第三方白名单应用
		if( !(ret=settingsManager.setAllowReplaceApp(mAllowReplaceApp_Right))){
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:设置第三方白名单应用失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		appList = settingsManager.getAllowReplaceApp();
		if(!(ret = Arrays.equals(appList, mAllowReplaceApp_Right))){
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:获取第三方白名单与设置不一致(预期=%s,实际=%s)", Tools.getLineInfo(),mAllowReplaceApp_Right[0],appList[0]);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("设置第三方白名单应用成功。。。请在文件管理器中点击InstalltestV2.apk安装。如果是静默安装，且InstalltestV1.apk显示界面升级为InstalltestV2.apk界面。按任意键删除第三方白名单应用" );
		
		//case4.1 删除第三方白名单应用
		settingsManager.setAllowReplaceApp(null);
		//case4.2  删除第三方白名单应用后再次开启统一验签并且下载第三方apk应失败
		gui.cls_show_msg("删除第三方白名单应用成功。。。。。请将InstalltestV1.apk卸载。按任意键再次开启统一验签" );
		if(!(ret=settingsManager.setAllApkVerifyEnable())){
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d:开启统一验签失败(ret=%s)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg("开启统一验签成功。。。。。请再次安装InstalltestV1.apk，如果安装失败，测试通过" );
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
