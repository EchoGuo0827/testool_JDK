package com.example.highplattest.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.R.integer;
import android.newland.SettingsManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class Net5 extends UnitFragment{
	private final String TESTITEM = "setPreferredNetworkType和getPreferredNetworkType";
	private final String CLASS_NAME = Net5.class.getSimpleName();
	private int cardchoose=100;   //联通卡为1  移动卡为2  电信卡为3
	Gui gui = new Gui(myactivity, handler);
	int getNetMode;
	boolean ret=false;
	String mUrl="https://www.baidu.com";//用于检验网络是否可用
	public void net5(){
		while(true)
		{
			int returnValue=gui.cls_show_msg("网络模式切换测试\n0.网络模式测试\n1.网络模式重启测试\n");
			switch (returnValue) 
			{
			case '0':
				normalNetModeTest();
				break;
			case '1':
				rebootNetModeTest();
				break;	
			case ESC:
				return;
			}
		}

		
		
	}
	private void rebootNetModeTest() {
		while(true)
		{
			String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
			SettingsManager mSettingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
			final int returnValue=gui.cls_show_msg("测试重启后,网络模式是否保持不变,2G,3G/2G,4G/3G/2G,三种模式均需测试,请选择需要测试的网络模式\n0.4G/3G/2G\n1.3G/2G\n2.2G");
			if(returnValue==ESC){//增加可选择返回
				return;
			}
			if((ret=mSettingsManager.setPreferredNetworkType(52-returnValue))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}
			for(int i=0;i<=10;i++){
				gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
			}
			if(gui.cls_show_msg("右上角和设置中网络模式是否显示为%dG,是[确定],否[其他]",52-returnValue)!=ENTER){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,实际显示与设置不符",Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=52-returnValue){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}
			if(gui.cls_show_msg("测试重启后网络模式是否保持不变,若重启后网络为%dG,则测试通过,是否立即重启,是[确认],否[其他]",52-returnValue)==ENTER){
				Tools.reboot(myactivity);
			}
		}
	}
	private void normalNetModeTest() {
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		SettingsManager mSettingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		gui.cls_show_msg("本案例测试网络模式切换,需要4G移动,联通,电信卡均通过测试,请先插入sim卡并重启,任意键继续..");
		//case 1:手动设置,网络模式切换正确,接口返回正确
		gui.cls_show_msg1(2,"case1:手动设置,网络模式切换正确,接口返回正确");
			
		
	
			gui.cls_show_msg("请进入设置,手动切换网络模式至2G,待右上角和设置中显示网络切换成功后,任意键继续....");
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=2){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:手动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}
	
		
		gui.cls_show_msg("请进入设置,手动切换网络模式至3G/2G,待右上角和设置中显示网络切换成功后,任意键继续....");
	
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=3){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:手动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}




		gui.cls_show_msg("请进入设置,手动切换网络模式至4G/3G/2G,待右上角和设置中显示网络切换成功后,任意键继续....");
		
		if((getNetMode=mSettingsManager.getPreferredNetworkType())!=4){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:手动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
		if((ret=isNetAvailable(mUrl))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case 2:正常流程测试,接口设置网络模式,网络模式应正常切换,接口返回值正确
		gui.cls_show_msg1(2,"case2:正常流程测试,接口设置网络模式,网络模式应正常切换,接口返回值正确");
		//2G
		gui.cls_show_msg1(1,"即将切换网络模式为2G");
		if((ret=mSettingsManager.setPreferredNetworkType(2))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
		}
	
			if(gui.cls_show_msg("右上角和设置中网络模式是否显示为2G,是[确定],否[其他]")!=ENTER){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,实际显示与设置不符",Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=2){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}

	
		//3G/2G
		gui.cls_show_msg1(1,"即将切换网络模式为3G/2G");
		
			
			if((ret=mSettingsManager.setPreferredNetworkType(3))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}
			for(int i=0;i<=10;i++){
				gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
			}
			if(gui.cls_show_msg("右上角和设置中网络模式是否显示为3G,是[确定],否[其他]")!=ENTER){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,实际显示与设置不符",Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=3){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}

		
		//4G/3G/2G
		gui.cls_show_msg1(1,"即将切换网络模式为4G/3G/2G");
		if((ret=mSettingsManager.setPreferredNetworkType(4))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
		}
		if(gui.cls_show_msg("右上角和设置中网络模式是否显示为4G,是[确定],否[其他]")!=ENTER){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,实际显示与设置不符",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((getNetMode=mSettingsManager.getPreferredNetworkType())!=4){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口自动切换网络模式测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
		if((ret=isNetAvailable(mUrl))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case 3:异常测试
		//case 3.1:异常测试1,设置错误的参数,应设置失败,网络模式保持不变
		gui.cls_show_msg1(2,"case3.1:异常测试1,设置错误的参数,应设置失败,网络模式保持不变");
		if((ret=mSettingsManager.setPreferredNetworkType(-1))==true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s,剩余%d秒",10-i);
		}
		if((getNetMode=mSettingsManager.getPreferredNetworkType())!=4){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
		if((ret=isNetAvailable(mUrl))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		//case 3.2:异常测试2,连续设置,应设置成功,网络模式为最后一次设置的值
		gui.cls_show_msg1(2,"case 3.2:异常测试2,连续设置,应设置成功,网络模式为最后一次设置的值");
		if((ret=mSettingsManager.setPreferredNetworkType(2))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(1000);//休眠一秒,模拟实际操作中的停顿
		if((ret=mSettingsManager.setPreferredNetworkType(3))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		SystemClock.sleep(1000);
		if((ret=mSettingsManager.setPreferredNetworkType(4))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
		}
		if(gui.cls_show_msg("右上角和设置中网络模式是否显示为4G,是[确定],否[其他]")!=ENTER){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,实际显示与设置不符",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((getNetMode=mSettingsManager.getPreferredNetworkType())!=4){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:异常测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
		if((ret=isNetAvailable(mUrl))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case 4:wifi开启 切换移动网络 wifi关闭之后 移动网络应为切换之后
		gui.cls_show_msg1(2,"case4:wifi开启 切换移动网络 wifi关闭之后 移动网络应为切换之后");
		gui.cls_show_msg("请连入任意wifi,完成后任意键继续...");
		//2G
		if((ret=mSettingsManager.setPreferredNetworkType(2))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口设置2G失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
		}
		gui.cls_show_msg("请断开wifi,完成后任意键继续...");
		
	
			if(gui.cls_show_msg("右上角和设置中网络模式是否显示为2G,是[确定],否[其他]")!=ENTER){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,实际显示与设置不符",Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=2){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}


		gui.cls_show_msg("请连入任意wifi,完成后任意键继续...");
		//3G/2G
		if((ret=mSettingsManager.setPreferredNetworkType(3))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口设置3G失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
		}
		gui.cls_show_msg("请断开wifi,完成后任意键继续...");
	
			if(gui.cls_show_msg("右上角和设置中网络模式是否显示为3G,是[确定],否[其他]")!=ENTER){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,实际显示与设置不符",Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
			if((getNetMode=mSettingsManager.getPreferredNetworkType())!=3){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				if (!GlobalVariable.isContinue)
					return;
			}

	
		gui.cls_show_msg("请连入任意wifi,完成后任意键继续...");
		//4G/3G/2G
		if((ret=mSettingsManager.setPreferredNetworkType(4))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:接口设置4G失败,(ret=%s)",Tools.getLineInfo(),ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
		}
		gui.cls_show_msg("请断开wifi,完成后任意键继续...");
		if(gui.cls_show_msg("右上角和设置中网络模式是否显示为4G,是[确定],否[其他]")!=ENTER){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,实际显示与设置不符",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		if((getNetMode=mSettingsManager.getPreferredNetworkType())!=4){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,接口获取的网络模式为(getNetMode=%d)",Tools.getLineInfo(),getNetMode);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
		if((ret=isNetAvailable(mUrl))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
	}

}
