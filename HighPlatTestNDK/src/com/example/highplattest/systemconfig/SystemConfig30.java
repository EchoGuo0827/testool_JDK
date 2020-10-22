package com.example.highplattest.systemconfig;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.newland.SettingsManager;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig30.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20161014
 * directory 		: 
 * description 		: 语言中语言种类显示接口
 * 					  setSettingWallpaperDisplay(int value)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20161014		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig30 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "语言中语言种类显示接口";
	private String fileName="SystemConfig30";
	private Gui gui = null;
	private SettingsManager settingsManager;
	String[] language = {"zh-CN","zh-HK","ja-JP","ko-KR"};
	//增加一种语言
	String[] fiveLanguage={"zh-CN","zh-HK","ja-JP","ko-KR","zh-TW"};
	
	public void systemconfig30()
	{
		gui=new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gScreenTime,"%s自动测试不能作为最终测试结果，请结合手动测试验证",TESTITEM);
		}
		settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		//获取所有语言
//		Locale[] locales = Locale.getAvailableLocales();
//		List<String> allLanguage = new ArrayList<String>();
//		for (int i = 0; i < locales.length; i++) {
//			allLanguage.add(locales[i] + "");
//			LoggerUtil.e("zhangxinj:"+locales[i] );
//		}
//		allLa = new String[allLanguage.size()];
//		allLanguage.toArray(allLa);
//		
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.异常测试\n1.设置为简体中文\n2.设置为繁体\n3.设置为日语\n4.设置为韩文\n6.恢复正常项", TESTITEM);
			switch (nkeyIn) 
			{
			case '0':
				abnormal();
				break;
				
			case '1':
			case '2':
			case '3':
			case '4':
				setLan(nkeyIn-'1');
				Tools.killPro();
				break;
				//接口设计导致应用activity重启。故不测该测试项
			case '5':
				OtherTest();
				Tools.killPro();
				break;
			case '6':
				test();
				Tools.killPro();
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	private void test() {
		// TODO Auto-generated method stub
		boolean flag =false;
		gui.cls_show_msg( "恢复语言为默认状态，任意键继续");
		
		if((flag = settingsManager.setSettingLocales(language)==false))
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}

		gui.cls_show_msg( "已恢复,请去设置-语言和输入法中查看");
	}

	private void abnormal()
	{
		boolean flag,flag1;
		String[] errLan = {"zz","pp","oo","zh"};
		gui.cls_printf("参数异常测试，设置-语言和输入法，语言中应包含多种语言".getBytes());
		// case1:参数异常测试，设置为null，以及不存在的字符串，应不影响原先添加的语言
		//根据开发马鑫汶说，设置为null时预期返回true且设置语言栏中的是系统默认语言  add bywangxy 20180807
		if((flag = settingsManager.setSettingLocales(null))==false|(flag1 = settingsManager.setSettingLocales(errLan))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s参数异常测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,flag,flag1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.ShowMessageBox("设置-语言和输入法选项，语言中是否包含多种语言且不会影响到系统设置的默认语言".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName,"systemconfig30",gScreenTime, "%s异常测试通过", TESTITEM);
	}
	
	private void setLan(int i)
	{
		boolean flag;
		String[] showLan = {"简体中文","繁体中文","日语","韩语"};
		List<String[]> languages = new ArrayList<String[]>();
		languages.add(new String[]{"zh-CN"});
		languages.add(new String[]{"zh-HK"});
		languages.add(new String[]{"ja-JP"});
		languages.add(new String[]{"ko-KR"});
		// case2:添加中文，应只显示中文
		// case3:添加繁体，应只显示繁体
		// case4:添加日文
		// case5:添加韩文
		gui.cls_show_msg1(1, "设置-语言和输入法，语言中只包含%s",showLan[i]);
		if((flag = settingsManager.setSettingLocales(languages.get(i)))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1, "语言测试为%s,请去设置-语言和输入法中查看,即将杀死进程并非闪退", showLan[i]);
	}
	
	
	private void OtherTest()
	{
		boolean flag;
		gui=new Gui(myactivity, handler);
//		// case6:添加全部，系统当前的语言是其他语言，则应默认选择添加的第一个语言
//		//case6.1:系统当前的语言是其他语言，设置后应变成第一个中文
//		if((flag = settingsManager.setSettingLocales(fiveLanguage))==false)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s添加所有语言失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		Log.d("eric_chen", "1");
		gui.cls_show_msg("请将系统当前语言设置为除中文、香港、日文、韩文以外的其他语言，按任意键继续");
		gui.cls_show_msg1(1, "设置-语言和输入法，语言中包含中文、香港、日文、韩文");
		if((flag = settingsManager.setSettingLocales(language))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.d("eric_chen", "2");
		if(gui.ShowMessageBox("设置-语言和输入法选项，语言中包含中文、香港、日文、韩文且系统当前选中的语言为中文,请去设置-语言和输入法中查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.d("eric_chen", "3");
		//case6.2系统中的语言是其中一种语言香港，设置后仍为香港
		gui.cls_show_msg("请将系统当前语言设置为香港，按任意键继续");
		gui.cls_show_msg1(1, "设置-语言和输入法，语言中包含中文、香港、日文、韩文");
		if((flag = settingsManager.setSettingLocales(language))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.d("eric_chen", "4");
		if(gui.ShowMessageBox("设置-语言和输入法选项，语言中包含中文、香港、日文、韩文且系统当前选中的语言为香港,请去设置-语言和输入法中查看".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig30",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		Log.d("eric_chen", "5");
		gui.cls_show_msg1(1, "语言测试为中文、香港、日文、韩文,请去设置-语言和输入法中查看,即将杀死进程并非杀退");
	}
	

	@Override
	public void onTestUp() {
	
	
	}

	@Override
	public void onTestDown() 
	{
//		if(settingsManager!=null)
//		{
//			settingsManager.setSettingLocales(language);
//		}
//		settingsManager = null;
//		gui = null;
//		language = null;
	}
}
