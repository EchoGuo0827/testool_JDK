package com.example.highplattest.net;

import android.annotation.SuppressLint;
import android.app.Service;
import android.newland.telephony.TelephonyManager;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 网络
 * file name 		: Net1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160613 
 * directory 		: 
 * description 		: 获取当前设备的IMEI号、meid号、iccid
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20160613	 		created
 * *  history 		 	: 变更记录												变更时间			变更人员
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200527                          陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Net1 extends UnitFragment
{
	private final String TESTITEM = "获取IMEI、MEID号、iccid";
	private final String CLASS_NAME = Net1.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	boolean mobilestate;
	MobileUtil mobileUtil;
	
	public void net1()
	{
		String funcName="net1";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		/* private & local definition */
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"未插入sim卡，请先插卡");
			return;
		}
		System.out.println("sim2");
		TelephonyManager teleManager = new  TelephonyManager(myactivity);
		String imei = teleManager.getImei();
		String meid = teleManager.getMeid();
		String message = String.format("获取imei：%s，获取meid：%s", imei,meid);
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s测试失败(imei = %s,meid = %s)", Tools.getLineInfo(),TESTITEM,imei,meid);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//获取SIM卡唯一标识，原生api方法，add by 20171103
		android.telephony.TelephonyManager mTm = (android.telephony.TelephonyManager) myactivity.getSystemService(Service.TELEPHONY_SERVICE);
		String iccid = mTm.getSimSerialNumber();
		if(gui.ShowMessageBox(("获取iccid：" + iccid).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(iccid = %s)", Tools.getLineInfo(),TESTITEM,iccid);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
		gui = null;
	}

	@Override
	public void onTestUp() {
		mobileUtil = MobileUtil.getInstance(myactivity,handler);
		mobilestate=mobileUtil.getMobileDataState(myactivity);
		
	}

	@Override
	public void onTestDown() {
		mobileUtil.setMobileData(myactivity, mobilestate);
		
	}
}
