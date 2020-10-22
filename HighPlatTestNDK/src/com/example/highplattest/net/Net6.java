package com.example.highplattest.net;

import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 网络
 * file name 		: Net6.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190723 
 * directory 		: 
 * description 		: 以太网开关操作
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20190723	 		created
 * history 		 	: 变更点										        变更人员				变更时间
 * 					  N850 F10   以太网打开1  关闭-1  状态未知返回0  				陈丁			        20200602
 * 					  F7以太网状态返回值修改							            陈丁					20200710
 *					  N550状态返回值修改									郑薛晴				20200910
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net6 extends UnitFragment
{
	private final String TESTITEM = "以太网开关操作";
	private final String CLASS_NAME = Net6.class.getSimpleName();
//	private final int ETH_STATE_UNKNOWN = 0;
	//by2020 0107  以太网打开变成1  关闭变成0  原（打开2 关闭1）
	//N850 F10   以太网打开1  关闭-1  状态未知返回0
	private  int ETH_STATE_DISABLED = 0;
	private  int ETH_STATE_ENABLED = 1;
	int  time=0;
	
//	private NlEthernetManager nlEthernetManager = null;
	
	public void net6()
	{
		String funcName ="net6";
		Gui gui = new Gui(myactivity, handler);
		
		if (GlobalVariable.currentPlatform==Model_Type.N550||GlobalVariable.currentPlatform==Model_Type.N850||GlobalVariable.currentPlatform==Model_Type.F10||GlobalVariable.currentPlatform==Model_Type.F7) {
			ETH_STATE_DISABLED=-1;
			ETH_STATE_ENABLED=1;
		}
		// case1:打开以太网获取到状态值应为1
		gui.cls_show_msg1(1, "即将进行以太网的打开操作");
		EthernetUtil ethernetUtil = EthernetUtil.getInstance(myactivity, handler);
		ethernetUtil.openNet();
		
		int status=-10086;
		long outtime=System.currentTimeMillis();
		while (time<15) {
			time=(int) Tools.getStopTime(outtime);
			status = GlobalVariable.gCurPlatVer==Platform_Ver.A9?ethernetUtil.getEthernetStatus():ethernetUtil.getStatus();
			Log.d("eric_chen", "open status: "+status);
			if (status==ETH_STATE_ENABLED) {
				break;
				
			}
			SystemClock.sleep(10);					
		}
		if (status!=ETH_STATE_ENABLED) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(),status);
			return;
		}
		if(gui.cls_show_msg("设置中的以太网是否为打开状态且接入网线可正常上网")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:以太网测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:关闭以太网获取到状态值为0
		time=0;
		ethernetUtil.closeNet();
		long outtime2=System.currentTimeMillis();
		while (time<15) {
			time=(int) Tools.getStopTime(outtime2);
			status = GlobalVariable.gCurPlatVer==Platform_Ver.A9?ethernetUtil.getEthernetStatus():ethernetUtil.getStatus();
			Log.d("eric_chen", "close status: "+status);
			if (status==ETH_STATE_DISABLED) {
				break;
				
			}
			SystemClock.sleep(10);					
		}
		
//		if(gui.cls_show_msg("以太网状态是否为0（status=%d）,是按确认，否则按其他",status)!=ENTER){
//			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(),status);
//			return;
//		}
		if (status!=ETH_STATE_DISABLED) {
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(),status);
			return;
		}
		if(gui.cls_show_msg("设置中的以太网是否为关闭状态且接入网线无法上网")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:以太网测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:修改以太网状态节点值，应修改失败
		BaseFragment.setNodeFile("/data/share/tmp/ethernet_status", "10");
	
		status = GlobalVariable.gCurPlatVer==Platform_Ver.A9?ethernetUtil.getEthernetStatus():ethernetUtil.getStatus();
//		status = LinuxCmd.readDevNode("/data/share/tmp/ethernet_status").replace("\r\n", "");
		if(status==10)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:写节点成功(status=%d)", Tools.getLineInfo(),status);
			if(!GlobalVariable.isContinue)
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
