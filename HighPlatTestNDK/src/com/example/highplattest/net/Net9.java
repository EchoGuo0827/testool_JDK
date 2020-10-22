package com.example.highplattest.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import android.content.Context;
import android.newland.SettingsManager;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/**
* file name 		: Net9.java
* history 		 	: 变更记录												变更时间			变更人员
*			  		  新增双卡切换功能,还有特定的双卡设备才支持（N910_A7）	       		20200519   		郑薛晴
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200525                          陈丁
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class Net9 extends UnitFragment implements Net0
{
	private final String TESTITEM = "移动网络双卡切换";
	public final String TAG = Net9.class.getSimpleName();
	private TelephonyManager mTeleManager;
	private SettingsManager mSettingsManager;
	private Gui gui = new Gui(myactivity, handler);
	
	public final int SLOT_1=0;
	public final int SLOT_2=1;
	private Object mLockObject=new Object();
	private int DDS_TIMEOUT=120*1000;
	
	private int mDdsResultCode=0;
//	private int mGetMobileMode=0;
	
	public void net9()
	{
		try {
			testNet9();
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(TAG, "net9", 1, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			return;
		}
	}
	
	public void testNet9() throws IOException
	{
		String funcName="testNet9";
		
		/**测试前置：打开移动网络，关闭wifi*/
		MobileUtil mobileUtil = new MobileUtil(myactivity);
		//测试前置 新增获取当前移动网络状态，测试结束后恢复初始状态
		boolean mobileDataState=mobileUtil.getMobileDataState(myactivity);
		
		// case3.1:参数异常测试，setDds(-1)20200713
//		gui.cls_show_msg1(1,"case3.1:参数异常测试");
//		setDDS(mTeleManager, -1);
//		// case3.2:参数异常测试,setDds(2)20200713
//		gui.cls_show_msg1(1,"case3.2:参数异常测试");
//		setDDS(mTeleManager, 2);
		
		mobileUtil.closeOther();
		mobileUtil.openNet();
		gui.cls_show_msg1(2, "测试前置：打开移动网络，耐心等待2s");
		// 备注:如果副卡为电信卡（电信无GSM模式） 或 联通卡（联通2G基站基本没有了），将表现为副卡无信号。
		
		// case1.1:双卡情况下，设置卡槽1为主卡，卡槽2自动为副卡，副卡会自动设置为GSM ONLY，设置完后默认为4G/3G/2G模式，正常数据首发
		gui.cls_show_msg1(1, "case1.1:双卡情况下，设置卡槽1为主卡，卡槽2自动为副卡，副卡会自动设置为GSM ONLY，设置完后默认为4G/3G/2G模式，正常数据首发");
		slotDds("case1.1",SLOT_1);
		
		/*// case1.2:通过SettingsManager的xx接口设置网络模式(3G/2G),可正常数据首发
		gui.cls_show_msg1(1, "case1.2:通过SettingsManager的setPreferredNetworkType接口设置网络模式(3G/2G),可正常数据首发");
		setAndGetNet(SLOT_1, Mobile_3G_2G);
		
		// case1.3:通过SettingsManager的xx接口设置网络模式(4G/3G/2G),可正常数据首发
		gui.cls_show_msg1(1, "case1.3:通过SettingsManager的setPreferredNetworkType接口设置网络模式(4G/3G/2G),可正常数据首发");
		setAndGetNet(SLOT_1, Mobile_4G_3G_2G);
		
		// case1.3:通过SettingsManager的xx接口设置网络模式(2G Only),可正常数据首发，选测，很多卡2G已经淘汰了
		if(gui.cls_show_msg("是否进行切换到2G模式的测试，联通卡2G基站基本没有了，电信卡无GSM模式")==ENTER)
		{
			setAndGetNet(SLOT_1, Mobile_GSM_ONLEY);
		}	
		
		*//**测试后置，卡槽2设置回4G/3G/2G网络*//*
		setAndGetNet(SLOT_1, Mobile_4G_3G_2G);*/
			
		gui.cls_show_msg1(1,"case1.1:卡槽1测试完毕");
		// case2.1:双卡情况下，设置卡槽2为主卡，卡槽1自动为副卡，副卡会自动设置为GSM ONLY，设置完后默认为4G/3G/2G模式，正常数据首发
		gui.cls_show_msg1(1, "case2.1:双卡情况下，设置卡槽2为主卡，卡槽1自动为副卡，副卡会自动设置为GSM ONLY，设置完后默认为4G/3G/2G模式，正常数据首发");
		slotDds("case1.2",SLOT_2);
		/*// case2.2:通过SettingsManager的xx接口设置网络模式(3G/2G),可正常数据首发
		setAndGetNet(SLOT_2, Mobile_3G_2G);
		// case2.3:通过SettingsManager的xx接口设置网络模式(4G/3G/2G),可正常数据首发
		setAndGetNet(SLOT_2, Mobile_4G_3G_2G);
		// case2.3:通过SettingsManager的xx接口设置网络模式(2G Only),可正常数据首发，选测，很多卡2G已经淘汰了
		if(gui.cls_show_msg("是否进行切换到2G模式的测试，联通卡2G基站基本没有了，电信卡无GSM模式【确认键测试】")==ENTER)
		{
			setAndGetNet(SLOT_2, Mobile_GSM_ONLEY);
		}*/
		gui.cls_show_msg1(1, "case1.2:卡槽2测试完毕");
		
		// case1.3:双卡情况下，设置卡槽1为主卡，卡槽2自动为副卡，副卡会自动设置为GSM ONLY，设置完后默认为4G/3G/2G模式，正常数据首发，因为存在第一次进入已经是该卡槽的情况
		gui.cls_show_msg1(1, "case1.3:双卡情况下，设置卡槽1为主卡，卡槽2自动为副卡，副卡会自动设置为GSM ONLY，设置完后默认为4G/3G/2G模式，正常数据首发");
		slotDds("case1.3",SLOT_1);
		gui.cls_show_msg1(1, "case1.3:卡槽1测试完毕");
		
		/**测试后置，卡槽2设置回4G/3G/2G网络*/
//		setAndGetNet(SLOT_2, Mobile_4G_3G_2G);
//		mobileUtil.setMobileData(myactivity, mobileDataState);
		
		gui.cls_show_msg1_record(TAG,funcName,gKeepTimeErr, "%s测试通过",TESTITEM);
		
	}
	
	String mUrl="http://www.baidu.com/";//用于检验网络是否可用
	private void slotDds(String caseTime,int slot) throws IOException
	{
		String funcName="slotDds";
		String slotMsg = slot==0?"卡槽1":"卡槽2";
		String ret;
		gui.cls_printf(String.format("设置%s为主卡...耐心等待dds设置结果",slotMsg).getBytes());
		setDDS(mTeleManager, slot);
		
		if(mDdsResultCode!=DDS_SUCCESS&&mDdsResultCode!=DDS_FAIL_REASON_SLOT_IS_ALREADY_DDS){
        	gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "%s line %d:%s设置DDS接口失败(ddsResultCode = %s)",caseTime,Tools.getLineInfo(),slotMsg,mDdsResultCode);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		LoggerUtil.v("设置卡槽1为主卡成功");
		
		gui.cls_show_msg1(1,"获取主卡卡槽...");
		if((ret=getDDS(mTeleManager))!=String.valueOf(slot)){
        	gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "%s line %d:获取主卡卡槽错误(dds卡槽实际 = %s，dds卡槽预期=%d)",caseTime,Tools.getLineInfo(),ret,slot);
			if (!GlobalVariable.isContinue)
				return;
		}
		
//		if((mGetMobileMode=mSettingsManager.getPreferredNetworkType())!=Mobile_4G_3G_2G){
//			gui.cls_show_msg1_record(TAG,funcName,gKeepTimeErr, "line %d:%s获取网络模式错误(MobileMode=%d)",Tools.getLineInfo(),slotMsg,mGetMobileMode);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
		gui.cls_show_msg1(1, "测试当前网络是否可用...");
		if(isNetAvailable(mUrl)!=true){
			gui.cls_show_msg1_record(TAG,funcName,gKeepTimeErr, "%s line %d:测试失败,当前网络不可用",caseTime,Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
	}
	
//	private void setAndGetNet(int slot,int setType) throws IOException
//	{
//		String funcName = "setAndGetNet";
//		String slotMsg = slot==0?"卡槽1":"卡槽2";
//		String typeMsg = setType==Mobile_4G_3G_2G?"4G/3G/2G":setType==Mobile_3G_2G?"3G/2G":"2G";
//		gui.cls_printf(String.format("设置%s的网络类型的%s",slotMsg,typeMsg).getBytes());
//		if(mSettingsManager.setPreferredNetworkType(setType)!=true){
//			gui.cls_show_msg1_record(TAG,funcName,gKeepTimeErr, "line %d:%s设置网络模式错误(setType=%d)",Tools.getLineInfo(),slotMsg,setType);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//		gui.cls_show_msg1(10,"%s->切换为%s移动网络模式中,耐心等待10s...不要按键",slotMsg,typeMsg);
//		
//		
//		if((mGetMobileMode=mSettingsManager.getPreferredNetworkType())!=setType){
//			gui.cls_show_msg1_record(TAG,funcName,gKeepTimeErr, "line %d:%s获取网络模式错误(setType=%d,getType=%d)",Tools.getLineInfo(),slotMsg,setType,mGetMobileMode);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		gui.cls_show_msg1(1, "%s->测试当前%s网络是否可用...",slotMsg,typeMsg);
//		if(NetworkUtil.ping(mUrl)!=true){
//			gui.cls_show_msg1_record(TAG,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d)",Tools.getLineInfo(),mGetMobileMode);
//			if (!GlobalVariable.isContinue)
//				return;
//		}
//		
//	}
	
	@Override
	public void onTestUp() {
		mTeleManager = (TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		mSettingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
	}

	@Override
	public void onTestDown() {
	}
	
	/**
	 * 
	 * @param slot: 想要设置的卡槽。卡槽1请设0；卡槽2请设1.
	 * @return
	 */
	private String setDDS(TelephonyManager telephonyManager,int slot)
	{
		String ret="SUCCESS";
		try {
			Method method = telephonyManager.getClass().getMethod("setDDS", int.class,ResultReceiver.class);
			method.invoke(telephonyManager, slot,mResultReceiver);
			
			synchronized (mLockObject) {
				try {
					mLockObject.wait(DDS_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 设置完毕后要建立网络，休眠10s,等待设置完毕
			SystemClock.sleep(60*1000);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			ret = "NoSuchMethodException";
		} catch (SecurityException e) {
			e.printStackTrace();
			ret = "NoSuchMethodException";
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			ret = "NoSuchMethodException";
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			ret = "NoSuchMethodException";
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			ret = "NoSuchMethodException";
		}
		return ret;
	}
	
	/*0 if the current DDS is slot1. and 1 if slot2.*/
	private String getDDS(TelephonyManager telephonyManager)
	{
		String ret ="0";
		try {
			Method method = telephonyManager.getClass().getMethod("getDDS");
			int dds = (int)method.invoke(telephonyManager);
			LoggerUtil.i("002-getDDD="+dds);
			ret = String.valueOf(dds);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			ret = "IllegalAccessException";
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			ret = "IllegalArgumentException";
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			ret = "InvocationTargetException";
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			ret = "NoSuchMethodException";
		} catch (SecurityException e) {
			e.printStackTrace();
			ret = "SecurityException";
		}
		return ret;
	}
	
	ResultReceiver mResultReceiver = new ResultReceiver(null)
	{
		protected void onReceiveResult(int resultCode, android.os.Bundle resultData) 
		{
			super.onReceiveResult(resultCode, resultData);
			mDdsResultCode = resultCode;
			SystemClock.sleep(2000);
			LoggerUtil.i("接收到ResultReceiver的回调，setDDS的结果="+resultCode);
			synchronized (mLockObject) {
				mLockObject.notify();
			}
		};
	};

}
