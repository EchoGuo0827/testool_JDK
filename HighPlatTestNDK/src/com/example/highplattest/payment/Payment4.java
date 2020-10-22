package com.example.highplattest.payment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.newland.SettingsManager;
import android.newland.content.NlIntent;
import android.os.SystemClock;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.ApkBroadCastReceiver;
/************************************************************************
 * 
 * module 			: 阿里千牛需求，动态增加删除固件中白名单公钥
 * file name 		: payment4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180226
 * directory 		: 
 * description 		: 动态增加删除固件中的白名单公钥(阿里巴巴接口不需要测试)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj		   20160302	 	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Payment4 extends UnitFragment{
	private final String TESTITEM = "增加、删除固件的白名单公钥(阿里千牛)";
	private final String SETTING = "setting_alibaba_app_key";
	private final String SETTING_UPDATE="setting_app_update_configuration";
	private final String CMD = "adb shell settings  get system setting_alibaba_app_key";
//	private final String CMD_UPDATE = "adb shell settings  get system setting_app_update_configuration";
	private SettingsManager mSettingsManager;
	private Gui gui = new Gui(myactivity, handler);
	private String mStrInit = "";/**初始的白名单公钥*/
	private Intent intent ;
	private ApkBroadCastReceiver apkReceiver;
	private String fileName="Payment4";
	public void payment4()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.AliBaBa)
		{
			gui.cls_show_msg("该固件非阿里版本，任意键退出本案例");
			unitEnd();
			return;
		}
		if(gui.cls_show_msg("本用例测试需添加mtms权限，[取消]退出，[确认]继续")==ESC){
			return;
		}
		while(true)
		{
			int nKeyIn = gui.cls_show_msg("%s\n0.覆盖白名单测试\n1.增加白名单测试\n2.删除白名单测试\n3.自更新开关测试", TESTITEM);
			mSettingsManager.setSystemSetting(SETTING, "setlastkey");// 预置一个key值
			switch (nKeyIn) 
			{
			case '0':
				setKeyTest();
				break;
				
			case '1':
				addKeyTest();
				break;
				
			case '2':
				deleteKeyTest();
				break;
			case '3':
				updateTest();
			case ESC:
				// 如果是完全手动的时候杀死本activity，多模块的时候直接为return即可
//				if(GlobalVariable.gAutoFlag.get("unit") == ParaEnum.AutoFlag.MulAuto)
//					return;
//				else
//				{
					unitEnd();
//				}
				return;
			}
		}
	}
	
	

	/**
	 * setSystemSetting(String setting,String setttingvalue)
	 */
	private void setKeyTest()
	{
		boolean retValue1 = false,retValue4 = false,retValue5 = false;
		// case1:参数异常测试，字符串的格式非法，如没有使用';'结束，传入，""，null（备注：刘坤坤说setSystemSetting的第一个参数不做限制）
		if(/*(retValue1 = mSettingsManager.setSystemSetting(SETTING, ""))!=false||
				(retValue2 = mSettingsManager.setSystemSetting(SETTING, null))!=false||
				(retValue3 = mSettingsManager.setSystemSetting("err string", "test one key;"))!=false||*/
				(retValue4 = mSettingsManager.setSystemSetting("", "test one key"))!=false||
				(retValue5 = mSettingsManager.setSystemSetting(null, "test one key"))!=false)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,retValue4,retValue5);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case7:使用超长或超短字符串覆盖应成功
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, "a"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在单个a字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee"
				+ "ffffffffffgggggggggghhhhhhhhhhjjjjjjjjjjkkkkkkkkkklllllllllloooooooopppppppppp"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在超长字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:新增单个白名单公钥后，用其他字符串覆盖，应可覆盖白名单公钥成功（备注：刘坤坤建议单个字符串不要添加分号，因为底层已经添加了）
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfirstkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, "covercase2key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否只有covercase2key字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3:新增多个（多次）白名单公钥后，用其他字符串覆盖，应可覆盖白名单公钥成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfirstkey;addsecondkey;addthirdkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfourkey;addfivekey;addsixkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, "covercase3key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否只有covercase3key字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4:多次设置同一个白名单公钥后，用其他字符串覆盖，应可覆盖白名单公钥成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfirstkey;addsecondkey;addthirdkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfirstkey;addsecondkey;addthirdkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfirstkey;addsecondkey;addthirdkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, "covercase4key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否只有covercase4key字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5:覆盖白名单公钥后，再次进行增加和删除的操作应正常
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addcase5key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在addcase5key字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addcase5key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请[再次]使用%s命令查看是否存在addcase5key字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case7:设置特殊字符串
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, "~!@#$%^&*. ()<>?:_+{}[]|"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在特殊字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case6.1:使用""方式清空覆盖的白名单应设置成功（传""或者null情况都是清空白名单公钥）
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, ""))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否不存在任何字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case7:清空后增加白名单公钥并删除应设置成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addonekey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否[只存在]addonekey字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addonekey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(gui.cls_show_msg("请使用%s命令查看是否[不存在]addonekey字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case6.2:使用null方式清空覆盖的白名单应成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addtestonekey;addtestonekey;addtesttwokey;addtestthreekey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在addtestonekey;addtesttwokey;addtestthreekey字符串，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.setSystemSetting(SETTING, null))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否不存在任何字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addonekey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否[只存在]addonekey字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addonekey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(gui.cls_show_msg("请使用%s命令查看是否[不存在]addonekey字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		/*// case8:因为对setSystemSetting的第一个参数不限制，测试任意字符串均可生成(坤坤反馈该字符串在缓存里没有测试意义，故屏蔽该case)
		if((retValue1 = mSettingsManager.setSystemSetting("errstring", "errvalue"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用adb shell settings  get system errstring命令查看是否只存在errvalue字符串，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:覆盖白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.addSystemSetting("errstring", "errtestvallue"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用adb shell settings  get system errstring命令查看是否存在errtestvallue字符串，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting("errstring", "errtestvallue"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用adb shell settings  get system errstring命令查看是否[不存在]errtestvallue字符串，是[确认]，否[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		gui.cls_show_msg1_record(fileName,"setKeyTest",gScreenTime, "setSystemSetting测试通过");
	}
	
	/**
	 * addSystemSetting(String setting,String settingvalue)
	 */
	private void addKeyTest()
	{
		boolean retValue1 = false,retValue2 = false,retValue4 = false,retValue5 = false,retValue6 = false;
		// 测试前置，获取目前设备当中的白名单公钥（adb shell settings  get system setting_alibaba_app_key）
		
		// case1:字符串的格式非法，如没有使用';'结束，传入"",null，部分字符串有';'结束，最后一个字符串没有;结束等（刘坤坤说addSystemSetting接口的第一个参数不要求，只要是字符串都可以）
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, ""))!=false||
				(retValue2 = mSettingsManager.addSystemSetting(SETTING, null))!=false||
				/*(retValue4 = mSettingsManager.addSystemSetting("errstring", "addnewwhitekey"))!=false||*/
				(retValue5 = mSettingsManager.addSystemSetting("", "addnewwhitekey"))!=false||
				(retValue6 = mSettingsManager.addSystemSetting(null, "addnewwhitekey"))!=false)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s,%s,%s,%s)", Tools.getLineInfo(),TESTITEM,retValue1,retValue2,retValue5,retValue6);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case10:边界值测试，测试超长以及超短的字符串，设置128和1长度的字符串应可设置成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee"
				+ "ffffffffffgggggggggghhhhhhhhhhjjjjjjjjjjkkkkkkkkkklllllllllloooooooo1111111111"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在个超长字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "a"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(gui.cls_show_msg("请使用%s命令查看是否存在单个a的字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2:该字符串已存在于原先的公钥值中，应返回false，并且单个公钥是否添加;的分隔符效果应一致（刘坤坤反馈多次增加已存在的返回true）
		// case3:多次增加同一字符串的密钥值，不应出现异常情况
		mSettingsManager.addSystemSetting(SETTING, "addfirstkey");
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfirstkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		mSettingsManager.addSystemSetting(SETTING, "addfirstkey");
		if(gui.cls_show_msg("请使用%s命令查看是否仅含一个addfirstkey字符串，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case4:该字符串合法，应返回true，且公钥值中应增加该字符串
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addsecondkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 需要实际查看是否设置成功
		if(gui.cls_show_msg("请使用%s命令查看是否有addsecondkey的字符串，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5:多个字符串中存在相同的字符串，应可设置成功，且该重复的字符串应只能设置成功一次
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addthirdkey;addfourkey;addfivekey;addfourkey;addsixkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 需要实际查看是否设置成功
		if(gui.cls_show_msg("请使用%s命令查看是否有addthirdkey;addfourkey;addfivekey;addsixkey字符串，且每个字符串只出现一次，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case6:多个字符砖中存在之前已设置过的字符串，应可设置成功，已设置过的字符串不会增加
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addsevenkey;addfivekey;addeightkey;addninekey;addtenkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否有addsevenkey;addfivekey;addeightkey;addninekey;addtenkey，且每个字符串只出现一次，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case7:多个字符串中不存在相同的字符串，应全部都可设置成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addsystemfirstkey;addsystemsecondkey;addsystemthird key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否有addsystemfirstkey;addsystemsecondkey;addsystemthirdkey，且每个字符串只出现一次，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case8:删除某个白名单公钥后再进行增加应成功
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addsystemfirstkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addcase8key"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在addcase8key字符串，是[确认]，否[其他]", CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case9:特殊字符串测试，应能增加白名单成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "~!@#$%^&*. ()<>?:_+{}[]|"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在特殊字符的字符串，是[确认]，否[其他]",CMD)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"addKeyTest",gKeepTimeErr, "line %d:增加白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(fileName,"addKeyTest",gScreenTime, "addSystemSetting测试通过");
	}
	
	/**
	 * deleteSystemSetting(String setting,String settingvalue)
	 */
	private void deleteKeyTest()
	{
		boolean retValue1 = false,retValue2 = false,retValue3 = false,retValue4 = false,retValue5 =false;
		// case1:deleteSystemSetting接口异常测试，如没有使用';'结束，传入"",null等（刘坤坤反馈对deleteSystemSetting的第一个参数不做要求）
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, ""))!=false||
				(retValue2 = mSettingsManager.deleteSystemSetting(SETTING, null))!=false||
				(retValue3 = mSettingsManager.deleteSystemSetting("err_str", "com.example"))!=false||
				(retValue4 = mSettingsManager.deleteSystemSetting("", "com.example"))!=false||
				(retValue5 = mSettingsManager.deleteSystemSetting(null, "com.example"))!=false)
		{
			gui.cls_show_msg1_record(fileName,"setKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s,%s,%s,%s)", Tools.getLineInfo(),TESTITEM,retValue1,retValue2,retValue4,retValue5);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case9:边界测试，超长以及超短字符串测试，长度为128和1，删除该白名单公钥应成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee"
				+ "ffffffffffgggggggggghhhhhhhhhhjjjjjjjjjjkkkkkkkkkklllllllllloooooooo2222222222"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "a"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee"
				+ "ffffffffffgggggggggghhhhhhhhhhjjjjjjjjjjkkkkkkkkkklllllllllloooooooo2222222222"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "a"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在超长或单个a的字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2:删除不存在的字符串，应删除失败
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "deleteunExsitkey"))!=false)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3:多次删除一个不存在的字符串，不应跑飞
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "deletefirstkey"))!=false)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		mSettingsManager.deleteSystemSetting(SETTING, "deletefirstkey");
		mSettingsManager.deleteSystemSetting(SETTING, "deletefirstkey");
		// case3:删除已存在的公钥字符串，应删除成功，使用adb shell settings  get system setting_alibaba_app_key命令查看该字符串应被删除
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "setlastkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case4:新增一个不存在的公钥值后删除，应删除成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addnewtestkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addnewtestkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在addnewtestkey字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case5:新增多个不存在的公钥值后删除，应删除成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addfisrtnewkey;addsecondnewkey;addthirdnewkey;addfirstnewkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addfisrtnewkey;addsecondnewkey;addthirdnewkey;addfirstnewkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在addfisrtnewkey;addsecondnewkey;addthirdnewkey字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case6:新增一个白名单公钥，多次删除相同的公钥，该公钥应被删除
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "addtennewkey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		mSettingsManager.deleteSystemSetting(SETTING, "addtennewkey");
		mSettingsManager.deleteSystemSetting(SETTING, "addtennewkey");
		if(gui.cls_show_msg("请使用%s命令查看是否存在addtennewkey字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		mSettingsManager.deleteSystemSetting(SETTING, "addtennewkey");
		if(gui.cls_show_msg("请使用%s命令查看是否存在addtennewkey字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case7:多次新增一个白名单公钥，删除一次该公钥，应被删除
		mSettingsManager.addSystemSetting(SETTING, "addtwotwokey");
		mSettingsManager.addSystemSetting(SETTING, "addtwotwokey");
		mSettingsManager.addSystemSetting(SETTING, "addtwotwokey");
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "addtwotwokey"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在addtwotwokey字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case8:删除特殊字符串应成功
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, "~!@#$%^&*. ()<>?:_+{}[]|"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.deleteSystemSetting(SETTING, "~!@#$%^&*. ()<>?:_+{}[]|;"))!=true)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("请使用%s命令查看是否存在特殊字符的字符串，是[确认]，否[其他]", CMD)==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"deleteKeyTest",gKeepTimeErr, "line %d:删除白名单公钥失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(fileName,"deleteKeyTest",gScreenTime,"deleteSystemSetting测试通过");
	}
	
	private void updateTest() {
		boolean retValue1=false,retValue2=false;
		/*需求：
		1.优化阿里白名单，默认允许自更新
		2.限制应用更新渠道，只允许通过MTMS客户端进行更新。
		3.增加开关路口，只给MTMS开放权限做“开关”可以控制限制应用更新渠道的开和关，限制应用更新渠道的功能关则只能通过mtms更新，限制应用更新渠道的开则应用可以自更新*/
		gui.cls_show_msg("导入qq音乐和qq浏览器以及他们的高版本apk在sd卡路径下的apk文件夹下，并且关闭控制台，并且安装阿里证书，确保各个应用均未安装，任意键继续");
		String[][] apkPara = 
			{
				{GlobalVariable.sdPath+"apk/QQBrowser.apk","com.tencent.mtt"},
				{GlobalVariable.sdPath+"apk/QQyinle_586.apk","com.tencent.qqmusic"},
				{GlobalVariable.sdPath+"apk/QQliulanqi_8704340.apk","com.tencent.mtt"}
				
			};
		
	
		intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	
		String qqMusic="OpenSSLRSAPublicKey{modulus=8b9a5bb7760d1488dcc47c1d9ada2e4b3f098d3960b313f753770ea97b9052898a43c72021373201f84935e9aff63f4c55534ded620258a659ca6" +
				"50a036f83c8fcd1393be386d10ca7144dc2044447f92af35cc406f79e316fdbb6ac3719be5133fa6b4df3f654a1000999df09436d3c144b7dac2aa4fd0f4c32af2c0516b41f,publicExponent=10001}";
		String QQBrowser="OpenSSLRSAPublicKey{modulus=c209077044bd0d63ea00ede5b839914cabcc912a87f0f8b390877e0f7a2583f0d5933443c40431c35a4433bc4c965800141961adc44c9625b1d32" +
				"1385221fd097e5bdc2f44a1840d643ab59dc070cf6c4b4b4d98bed5cbb8046e0a7078ae134da107cdf2bfc9b440fe5cb2f7549b44b73202cc6f7c2c55b8cfb0d333a021f01f,publicExponent=10001}";
		//加入到阿里白名单公钥中
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, QQBrowser))!=true)
		{
			gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:qq浏览器加入到白名单公钥失败(%s)", Tools.getLineInfo(),retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((retValue1 = mSettingsManager.addSystemSetting(SETTING, qqMusic))!=true)
		{
			gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:qq音乐加入到白名单公钥失败(%s)", Tools.getLineInfo(),retValue1);
			if(GlobalVariable.isContinue==false)
				return;
		}

	
		//case1:恢复出厂，默认自更新，mtms也可以更新
		if(gui.cls_show_msg("是否恢复出厂进入此用例，是[确认]，否[其他]", CMD)==ENTER)
		{
			installApp(apkPara[0][0]);
			installApp(apkPara[1][0]);
			//静默安装更高版本的qq浏览器，应成功过
			installApp(apkPara[2][0]);
			if(gui.cls_show_msg("[mtms更新]检查是否成功安装更高版本的qq浏览器（版本8.7.0.4340），是[确认]，否[其他]", CMD)!=ENTER){
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:恢复出厂自更新失败", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if(gui.cls_show_msg("[自更新]进入qq音乐应用，找到更高版本的qq音乐，应更新成功，是[确认]，否[其他]", CMD)!=ENTER){
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:恢复出厂mtms更新失败", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
			uninstallApp(apkPara[2][1]);
			uninstallApp(apkPara[1][1]);
			
		}else{
			installApp(apkPara[0][0]);
			installApp(apkPara[1][0]);
			//case2：设置开关为false，只有mtms可以安装
			gui.cls_show_msg1(2, "设置开关为false，只能mtms更新");
			if((retValue1 = mSettingsManager.setSystemSetting(SETTING_UPDATE, "false"))!=true)
			{
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue1);
				if(GlobalVariable.isContinue==false)
					return;
			}
			installApp(apkPara[2][0]);
			if(gui.cls_show_msg("[mtms更新]更高版本的qq浏览器是否安装成功（版本8.7.0.4340），是[确认]，否[其他]", CMD)!=ENTER){
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:开关路口设置为false，自更新应失败，实际成功", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if(gui.cls_show_msg("[自更新]进入qq音乐应用，找到更高版本的qq音乐，应更新失败，是[确认]，否[其他]", CMD)!=ENTER){
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:开关路口设置为false，mtms更新失败", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
			uninstallApp(apkPara[2][1]);
			uninstallApp(apkPara[1][1]);
			//case3:设置开关为开，自更新和mtms都可以安装，与恢复出厂一样
			installApp(apkPara[0][0]);
			installApp(apkPara[1][0]);
			gui.cls_show_msg1(2, "设置开关为true，mtms更新和自更新都可以安装");
			if((retValue2 = mSettingsManager.setSystemSetting(SETTING_UPDATE, "true"))!=true)
			{
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retValue2);
				if(GlobalVariable.isContinue==false)
					return;
			}
			installApp(apkPara[2][0]);
			if(gui.cls_show_msg("[mtms更新]检查是否成功安装更高版本的qq浏览器（版本8.7.0.4340），是[确认]，否[其他]", CMD)!=ENTER){
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:开关路口设置为true，自更新失败", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if(gui.cls_show_msg("[自更新]进入qq音乐应用，找到更高版本的qq音乐，应更新成功，是[确认]，否[其他]", CMD)!=ENTER){
				gui.cls_show_msg1_record(fileName,"updateTest",gKeepTimeErr, "line %d:开关路口设置为true，mtms更新失败", Tools.getLineInfo(),TESTITEM);
				if(GlobalVariable.isContinue==false)
					return;
			}
			uninstallApp(apkPara[2][1]);
			uninstallApp(apkPara[1][1]);
		}
		
		gui.cls_show_msg1_record(fileName,"updateTest",gScreenTime,"是否恢复出厂都测试通过，则自更新开关测试通过");
	}


	 /** 
     * 获取签名公钥 
     * @param mContext 
     * @return 
     */  
    protected  String getSignInfo(Context mContext,String packageName) {  
        String signcode = "";  
        try {  
        	//包名
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName,PackageManager.GET_SIGNATURES);  
            Signature[] signs = packageInfo.signatures;  
            Signature sign = signs[0];  
            signcode = parseSignature(sign.toByteArray());  
            signcode = signcode.toLowerCase();  
        } catch (Exception e) {  
           
        }  
        return signcode;  
    }  
   
    protected  String parseSignature(byte[] signature) {  
        String sign = "";  
        try {  
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");  
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));  
            String pubKey = cert.getPublicKey().toString();  
            String ss = subString(pubKey);  
            ss = ss.replace(",", "");  
            ss = ss.toLowerCase();  
            int aa = ss.indexOf("modulus");  
            int bb = ss.indexOf("publicexponent");  
            sign = ss.substring(aa + 8, bb);  
        } catch (CertificateException e) {  
           
        }  
        return sign;  
    }  
   
    public  String subString(String sub) {  
        Pattern pp = Pattern.compile("\\s*|\t|\r|\n");  
        Matcher mm = pp.matcher(sub);  
        return mm.replaceAll("");  
    }
    public void installApp(String expPackName){
    	gui.cls_show_msg1_record(fileName,"installApp",gKeepTimeErr, "进行%s安装，安装时间略久，请耐心等待",expPackName);
		intent.setDataAndType(Uri.fromFile(new File(expPackName)),"application/vnd.android.package-archive");
		intent.putExtra(intent.EXTRA_INSTALLER_PACKAGE_NAME, "com.example.highplattest");
		myactivity.startActivity(intent);
		String currentName;
		int time = 0;
		long startTime;
		// 循环等待2分钟
		startTime = System.currentTimeMillis();
		while(time<60*3)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			currentName = apkReceiver.getPackName(APK_INSTALL);
			if(currentName!=null&&currentName.equals(expPackName))
				break;
		}
		if((currentName = apkReceiver.getPackName(APK_INSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_INSTALL))!=PACKAGE_INSTALL_SUCCESS)
		{
			gui.cls_show_msg1_record(fileName,"installApp",gKeepTimeErr,"line %d:静默安装app失败（apk = %s,%s，%d）", Tools.getLineInfo(),expPackName,currentName,respCode);
			if(!GlobalVariable.isContinue)
				return;
		}
//		gui.cls_show_msg1(2, "等待%s安装完毕",expPackName);
//		while(true)
//		{
//			SystemClock.sleep(2000);
//			if(isAppInstalled(myactivity.getApplicationContext(),packageName))
//				break;
//		}
    }
    public void uninstallApp(String expPackName){
    
		Uri uri2 = Uri.parse("package:"+expPackName);
		Intent intentDel2 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri2);
		myactivity.startActivity(intentDel2);
		int time = 0;
		String currentName="";
		// 循环等待1分钟
		long startTime = System.currentTimeMillis();
		while(time<60*2)
		{
			time = (int) Tools.getStopTime(startTime);
			SystemClock.sleep(1000);
			if((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName))
				break;
		}
		if ((currentName = apkReceiver.getPackName(APK_UNINSTALL)).equals(expPackName)==false||(respCode=apkReceiver.getResp(APK_UNINSTALL))!=PACKAGE_DELETE_SUCCESS) 
		{
			gui.cls_show_msg1_record(fileName,"uninstallApp",gKeepTimeErr,"line %d:静默卸载app测试失败(apk = %s,%s,ret = %d)", Tools.getLineInfo(),expPackName,currentName,respCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "卸载%s成功",expPackName);
    }
   
	@Override
	public void onTestUp() {
		// 测试前置
		mSettingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		apkReceiver = new ReceiverTracker().new ApkBroadCastReceiver();
		registApk();
	}
	private void registApk()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.INSTALL_APP_HIDE");
		intentFilter.addAction("android.intent.action.DELETE_APP_HIDE");
		myactivity.registerReceiver(apkReceiver, intentFilter);
	}
	
	private void unRegistApk()
	{
		if(this != null)
		{
			myactivity.unregisterReceiver(apkReceiver);
		}
	}
	@Override
	public void onTestDown() {
		// 测试后置
		mSettingsManager.setSystemSetting(SETTING, mStrInit);
		unRegistApk();
		gui = null;
		apkReceiver = null;
	}

}
