package com.example.highplattest.systemconfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;


/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig46.java 
 * Author 			: zhangxinj	
 * version 			: 
 * DATE 			: 20170221
 * directory 		: 
 * description 		: 禁止App连网、获取被禁用网络的App
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhangxinj		   20170221		    created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig46 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "禁止App连网";
	private String fileName="SystemConfig46";
	private Gui gui = null;
	private SettingsManager settingsManager=null;
	
	public void systemconfig46()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig46",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
	
		boolean flag = false;
		String[] appList= new String[]{"com.UCMobile","com.tencent.qqmusic"};
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		/*process body*/
		// 测试前置：获取目前禁用的app包名
		String[] disabledAppList=settingsManager.getDisabledApps();
		
		gui.cls_show_msg1(2,"目前禁用的app包名为："+Arrays.toString(disabledAppList));
		if(!Arrays.equals(disabledAppList,appList)){
			
				SharedPreferences preferences=myactivity.getSharedPreferences("disabledAppList",Context.MODE_PRIVATE);
				 Editor editor=preferences.edit();
				 HashSet<String> set = new HashSet<String>();
				 if(disabledAppList!=null){
					 for(String value:disabledAppList){
						 set.add(value);
					 }
					
					 editor.putStringSet("disabledAppList", set);
					 editor.commit();
				 }
			 
		}
		
		//case1:参数异常测试
		String[] errpackNames={"err1","err2"};
		if((flag = settingsManager.disableAppCommunication(errpackNames))!=false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig46",gKeepTimeErr, "line %d:参数异常测试(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2:设置UC浏览器和自检禁用网络，预期UC浏览器网络不可用，自检网络不可用，无法上网
		gui.cls_show_msg("从SVN测试apk文件夹下安装UC浏览器和qq音乐，打开数据流量或者wifi，确保UC浏览器和qq音乐可以上网，任意键继续");
		if((flag = settingsManager.disableAppCommunication(appList))==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig46",gKeepTimeErr, "line %d:禁止app连网失败(%s)", Tools.getLineInfo(),flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		//获取返回的禁用app，应该与被禁用的列表一致
		String[] compre_app=settingsManager.getDisabledApps();
		if(!Arrays.equals(appList,compre_app)){
			gui.cls_show_msg1_record(fileName,"systemconfig46",gKeepTimeErr, "line %d:获取目前禁用的app包名失败(%s)", Tools.getLineInfo(),Arrays.toString(compre_app));
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(2, "");
		if(gui.ShowMessageBox(("是否立即重启，重启后再次打开UC浏览器和qq音乐，确认是否可以上网，不能上网即测试通过，选择否将还原设置" +
				"需重启后才能看到效果").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			 Tools.reboot(myactivity);
		}else
		{
			//把存入SharedPreferences的数据取出来
			SharedPreferences preferences=myactivity.getSharedPreferences("disabledAppList", Context.MODE_PRIVATE);
			Set<String> set = new HashSet<String>();
			set=preferences.getStringSet("disabledAppList", set);
			
			if(set.size()>0){
				//set集合转成String数组
				String[] array = set.toArray(new String[set.size()]);

				if((flag = settingsManager.disableAppCommunication(array))==false)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig46",gKeepTimeErr, "line %d:还原解禁失败(%s)", Tools.getLineInfo(),flag);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			else
			{

				if((flag = settingsManager.disableAppCommunication(null))==false)
				{
					gui.cls_show_msg1_record(fileName,"systemconfig46",gKeepTimeErr, "line %d:还原解禁失败(%s)", Tools.getLineInfo(),flag);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			if(gui.ShowMessageBox("是否立即重启，重启后解禁才能看到效果，或稍后手动重启".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			{
				 Tools.reboot(myactivity);
			}
			gui.cls_show_msg1_record(fileName,"systemconfig46",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		}
	}
	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);

	}
	@Override
	public void onTestDown() 
	{
		if(settingsManager!=null)
		{
			// 测试后置：所有app均可联网，重启后生效
			settingsManager.disableAppCommunication(null);
		}
		settingsManager = null;
		gui = null;
	}
}
