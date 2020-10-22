package com.example.highplattest.systemconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.newland.SettingsManager;
import android.newland.os.NlBuild;
import android.os.Build;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 配置产品型号模块
 * file name 		: SystemConfig52.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170605
 * directory 		: 
 * description 		: 配置产品型号
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20170605     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig52 extends UnitFragment
{

	private final String TESTITEM = "配置产品型号";
	private String fileName="SystemConfig52";
	private Gui gui;
	private SettingsManager settingsManager = null;
	// 获取产品型号
    private String model = Build.MODEL;
	// 获取产品型号 add by 20170515
	private String model_2 = NlBuild.VERSION.MODEL;
	//保留初始型号
	private  String product;
	private String str3,str4;
	private SharedPreferences sp ;
	private Editor editor ;
	private String[] strArray={",福建新大陆支付技术有限公司","AaBbCcABCabc","123","N900","NL-N900","NL-N910"
			,"`中文ABCabc~!\"@$#[^%]*() -_=+|\\&{}:;?,/><.",Tools.getRandomString(90),Tools.getRandomString(91)};

	
	public void systemconfig52() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gScreenTime,"line %d:未找到该类，抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
			return;
		}
	
		
		//测试前置，保留初始型号
		gui.cls_show_msg("请先在manifest配置文件中添加mtms权限，完成点任意键继续");
		sp = myactivity.getSharedPreferences("my_product", Context.MODE_PRIVATE);
		editor = sp.edit();
		if(gui.cls_show_msg("是否首次进入本用例，是[确认]，否[其他]")==ENTER)
		{
			editor.putString("product", Build.MODEL);
			editor.commit();
		}
		product=sp.getString("product", null);
		
		if(gui.cls_show_msg("是否出现异常需要恢复初始型号？是【确定】否【其他】")==ENTER)
		{
			if(settingsManager.setProductModel(product)==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr,"line %d:设置产品型号为null，预期(false)，实际(true)", Tools.getLineInfo());
				return;
			}
			Tools.reboot(myactivity);
			
		}
		
		
		//case1.1 参数异常
		gui.cls_printf("参数异常测试，产品型号设置为null，预期失败".getBytes());
		if(settingsManager.setProductModel(null)==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr,"line %d:设置产品型号为null，预期(false)，实际(true)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		/*// case1.2 str长度边界测试，长度设置为92，预期应返回设置失败，但实际抛出异常，故屏蔽//by 20180705wnagxy
		if (settingsManager.setProductModel(Tools.getRandomString(92)) == true) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr,"line %d:设置产品型号长度设置大于91，预期(false)，实际(true)", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}*/
		
		//case2 参数为“”，预期成功，为初始默认型号，已与开发确认，如N910等
		gui.cls_printf(("产品型号设置为空字符串，预期为"+product).getBytes());
		if(settingsManager.setProductModel("")==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr,"line %d:设置产品型号为空，预期(true),实际(false)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("是否立即重启，重启后，执行用例到此，产品型号应显示为%s[确认]重启，[取消]继续",product)==ENTER)
		{
			Tools.reboot(myactivity);
			return;
		}	
		MyProduct();
		
		for (int i = 0; i < strArray.length; i++) 
		{
			if (settingsManager.setProductModel(strArray[i]) == false) 
			{
				gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr, "line %d设置产品型号%s测试失败(false)", Tools.getLineInfo(), strArray[i]);
				continue;
			}
			if (gui.cls_show_msg("是否立即重启，重启后，执行用例到此，产品型号应显示为%s，[确认]重启，[取消]继续", strArray[i]) == ENTER) 
			{
				Tools.reboot(myactivity);
				return;
			}
			MyProduct();
		}
		
		//测试后置，恢复为原有型号
		gui.cls_printf(("测试后置，恢复初始默认产品型号，设置为"+product+"，预期成功").getBytes());
		if (settingsManager.setProductModel(product)==false) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr, "line %d设置产品型号测试失败(false)", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
			{
				settingsManager.setProductModel(product);
				return;
			}
		}
		if (gui.cls_show_msg("测试后置，是否立即重启，重启后，执行用例到此，产品型号应显示为初始默认型号"+product+"，[确认]重启，[取消]继续")==ENTER)
		{
			Tools.reboot(myactivity);
			return;
		}	
		MyProduct();
		gui.cls_show_msg1_record(fileName,"systemconfig52",gScreenTime,"以上测试均通过则%s测试通过(长按确认键退出测试)", TESTITEM);
	}
	
	
	public void MyProduct() {
		model = Build.MODEL;
		model_2 = NlBuild.VERSION.MODEL;
		str3 = model == null ? "null" : model;
		if (gui.cls_show_msg("(1)获取产品型号=%s?[确认]是，[取消]否", str3) != ENTER) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr, "line %d:获取产品型号测试失败(%s)", Tools.getLineInfo(), str3);
			if (!GlobalVariable.isContinue)
				return;
		}
		str4 = model_2 == null ? "null" : model_2;
		if (gui.cls_show_msg("(2)获取产品型号=%s?[确认]是，[取消]否", str4) != ENTER) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig52",gKeepTimeErr, "line %d:获取产品型号测试失败(%s)", Tools.getLineInfo(), str4);
			if (!GlobalVariable.isContinue)
				return;
		}
	}
	
	@Override
	public void onTestUp() 
	{
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() 
	{
//		if(settingsManager!=null)
//			settingsManager.setProductModel(product);
//		settingsManager = null;
//		gui = null;
	}
}
