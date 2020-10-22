package com.example.highplattest.net;

import android.newland.net.ethernet.NlEthernetManager;
import android.os.SystemClock;
import android.util.Log;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 网络
 * file name 		: Net13.java 
 * description 		: getEthernetStatus(N550)
 * history 		 	: 变更点										        变更人员					变更时间
 * 					  N550增加以太网的getEthernetStatus接口  				郑薛晴			     20200910
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net13 extends UnitFragment
{
	private final String TESTITEM = "getEthernetStatus(N550)";
	private final String FILE_NAME = Net13.class.getSimpleName();
	private final int ETH_STATE_UNKNOWN = 0;
	private final int ETH_STATE_DISABLED = -1;
	private final int ETH_STATE_ENABLED = 1;
	int  time=0;
	
	public void net13()
	{
		String funcName ="net13";
		Gui gui = new Gui(myactivity, handler);
		if(GlobalVariable.currentPlatform!=Model_Type.N550)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "非N550产品不支持该案例");
			return;
		}

		
		// case1:打开以太网获取到状态值应为1
		gui.cls_show_msg1(1, "case1:即将进行以太网的打开操作");
		EthernetUtil ethernetUtil = EthernetUtil.getInstance(myactivity, handler);
		ethernetUtil.openNet();
		
		int status=-10086;
		long outtime=System.currentTimeMillis();
		while (time<15) {
			time=(int) Tools.getStopTime(outtime);
			status = ethernetUtil.getEthernetStatus();
			Log.d("eric_chen", "open status: "+status);
			if (status==ETH_STATE_ENABLED) {
				break;
			}
			SystemClock.sleep(10);					
		}
		if (status!=ETH_STATE_ENABLED) {
			gui.cls_show_msg1_record(FILE_NAME,funcName,gKeepTimeErr, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(),status);
			return;
		}
		if(gui.cls_show_msg("设置中的以太网是否为打开状态且接入网线可正常上网")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME,funcName,gKeepTimeErr, "line %d:以太网测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:关闭以太网获取到状态值为0
		time=0;
		ethernetUtil.closeNet();
		long outtime2=System.currentTimeMillis();
		while (time<15) {
			time=(int) Tools.getStopTime(outtime2);
			status =ethernetUtil.getEthernetStatus();
			Log.d("eric_chen", "close status: "+status);
			if (status==ETH_STATE_DISABLED) {
				break;
			}
			SystemClock.sleep(10);					
		}
		
		if (status!=ETH_STATE_DISABLED) {
			gui.cls_show_msg1_record(FILE_NAME,funcName,gKeepTimeErr, "line %d:获取以太网状态失败(status = %d)", Tools.getLineInfo(),status);
			return;
		}
		if(gui.cls_show_msg("设置中的以太网是否为关闭状态且接入网线无法上网")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME,funcName,gKeepTimeErr, "line %d:以太网测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:修改以太网状态节点值，应修改失败
		BaseFragment.setNodeFile("/data/share/tmp/ethernet_status", "10");
	
		status = ethernetUtil.getEthernetStatus();
		if(status==10)
		{
			gui.cls_show_msg1_record(FILE_NAME,funcName,gKeepTimeErr, "line %d:写节点成功(status=%d)", Tools.getLineInfo(),status);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(FILE_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
