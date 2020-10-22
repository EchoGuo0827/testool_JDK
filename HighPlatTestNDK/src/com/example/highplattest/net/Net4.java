package com.example.highplattest.net;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.newland.telephony.TelephonyManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: 4G网络限制
 * file name 		: Net4.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190624
 * directory 		: 指定APP禁用其使用4G网络【 银商专用接口】
 * description 		: setMonitor(int status)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  	zsh		  	 20190624     		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net4 extends UnitFragment
{
	private final String TESTITEM = "限制单个应用不能使用移动网络(setUIDwithMobileNetworkBlock)";
	private final String CLASS_NAME = Net4.class.getSimpleName();
//	private final String FUNCNAME="net4";
//	private String uid;
	private int[]uids;//存储屏蔽列表内应用的uid
	private int qqBrowserUid;
	private int qqMusicUid;
	Gui gui = new Gui(myactivity, handler);
	
	public void net4()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.ChinaUms)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "net4", 1, "银商专用接口,非银商版本不支持(%s)", GlobalVariable.gCustomerID);
			return;
		}
		
		gui.cls_show_msg("本案例需要4G网络,请插入上网卡,并将qq浏览器,qq音乐通过adb安装,安装完成后继续...");
		//获取QQ音乐和QQ浏览器的uid
		try {
		    PackageManager pm = myactivity.getPackageManager();
		    ApplicationInfo qqBrowser = pm.getApplicationInfo("com.tencent.mtt", PackageManager.GET_ACTIVITIES);
		    ApplicationInfo qqMusic = pm.getApplicationInfo("com.tencent.qqmusic", PackageManager.GET_ACTIVITIES);
		    qqBrowserUid= qqBrowser.uid;
		    qqMusicUid	= qqMusic.uid;
		    LoggerUtil.d("qq浏览器uid="+qqBrowser.uid);
		    LoggerUtil.d("qq音乐uid="+qqMusic.uid);
		} catch (NameNotFoundException e) {
			gui.cls_show_msg1_record(CLASS_NAME,TESTITEM,gScreenTime,"%suid获取失败",TESTITEM);
		    e.printStackTrace();
		    return;
		}
		/*测试主入口*/
		int nkeyIn = gui.cls_show_msg("%s\n0.屏蔽应用4G网络测试\n1.恢复应用4G网络测试\n",TESTITEM);
		switch (nkeyIn) 
		{
		case '0':
			setNetWorkBlock();
			break;
		case '1':
			releaseBlock();
			break;
			
		}
	}
	
	//设置禁止使用4G网络APP的uid，阻断该APP使用4G网络,其他APP不受影响
	private void setNetWorkBlock() 
	{
		boolean ret=false;
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		TelephonyManager telephonyManager  = new TelephonyManager(myactivity);
		//case1:异常测试:未设置过屏蔽进行屏蔽解除,应返回false,获取屏蔽接口,应为空
		ret=telephonyManager.setUIDwithMobileNetworkBlock(Integer.parseInt(Integer.toString(qqBrowserUid,10)),false);
		if(ret!=false){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:解除4G屏蔽异常测试失败(ret=%s)",Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		ret=telephonyManager.setUIDwithMobileNetworkBlock(Integer.parseInt(Integer.toString(qqMusicUid,10)),false);
		if(ret!=false){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:解除4G屏蔽异常测试失败(ret=%s)",Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		uids=telephonyManager.getUIDwithMobileNetworkBlock();
		if(uids!=null)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:uid屏蔽列表与实际设置不符(uid.length=%d)",Tools.getLineInfo(), uids.length);
			if (!GlobalVariable.isContinue)
				return;
		}
		//正常流程测试:设置屏蔽,应成功
		//case2:未设置屏蔽,应用可以正常通过4G和wifi访问网络
		if(gui.ShowMessageBox("分别在4G网络和wifi的情况下,使用qq音乐、qq浏览器、自检应用访问网络,是否正常访问,是[确定],否[取消]".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未设置屏蔽,应用访问网络异常",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case3:设置屏蔽,应用应可以正常访问网络(重启生效),接口返回true
		//case3.1:屏蔽QQ浏览器和QQ音乐的APP,接口返回true,获取屏蔽列表为设置值.
		ret=telephonyManager.setUIDwithMobileNetworkBlock(Integer.parseInt(Integer.toString(qqBrowserUid,10)),true);
		if(ret!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置4G屏蔽失败(ret=%s)",Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		ret=telephonyManager.setUIDwithMobileNetworkBlock(Integer.parseInt(Integer.toString(qqMusicUid,10)),true);
		if(ret!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:设置4G屏蔽失败(ret=%s)",Tools.getLineInfo(),ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//获取屏蔽列表,应包含刚才的设置
		uids=telephonyManager.getUIDwithMobileNetworkBlock();
		if(uids.length!=2){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取uid屏蔽列表失败(%d)",Tools.getLineInfo(), uids.length);
			if (!GlobalVariable.isContinue)
				return;
		}else {
			for(int i=0;i<uids.length;i++){
				if(uids[i]!=qqBrowserUid&&uids[i]!=qqMusicUid){
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取uid屏蔽列表失败,uid列表与实际设置不符(%d)",Tools.getLineInfo(), uids.length);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if(uids[0]==uids[1]){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:uid屏蔽列表错误(uid=%d)",Tools.getLineInfo(), uids[0]);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		//case3.2:设置屏蔽,未重启,应用4G网络使用正常,wifi使用正常,不在屏蔽列表的应用访问网络正常(屏蔽重启生效)
		if(gui.ShowMessageBox("分别在4G网络和wifi的情况下,使用qq音乐 qq浏览器 自检应用访问网络,是否正常访问,是[确定],否[取消]".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未重启屏蔽接口生效,与文档描述不符",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		// 【不需要重启就会生效 by 20200324 zhengxq】
		gui.cls_show_msg1(1,"已屏蔽qq浏览器和qq音乐的4G网络使用权限(wifi网络可以使用),请进入'恢复应用4G网络'继续测试");
		// case6:屏蔽自检应用，预期自检应用无法通过4G访问网络，因自检没有uid,并且第三方应用无法获取系统级uid,该项无法测试.
	}
	
	private void releaseBlock() 
	{
		boolean ret=false;
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		//case4: 重启设备,屏蔽设置生效,屏蔽列表符合设置的预期.
		//case4.1 重启设备,屏蔽列表的应用无法使用4G网络,可以使用wifi网络,其他应用可以正常使用4G和wifi网络
		TelephonyManager telephonyManager  = new TelephonyManager(myactivity);
		if(gui.ShowMessageBox("qq音乐和qq浏览器无法使用4G访问网络,可以通过wifi访问网络,自检应用可以正常访问网络,是否符合预期,是[确定],否[取消]".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:屏蔽4G网络未生效",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;		
		}
		//case4.2 获取屏蔽列表的uid,应与重启前设置的一致
		uids=telephonyManager .getUIDwithMobileNetworkBlock();
		if(uids.length!=2){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取uid屏蔽列表失败(%d)",Tools.getLineInfo(), uids.length);
			if (!GlobalVariable.isContinue)
				return;
		}else {
			for(int i=0;i<uids.length;i++){
				if(uids[i]!=qqBrowserUid&&uids[i]!=qqMusicUid){
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:获取uid屏蔽列表失败,uid列表与实际设置不符(%d)",Tools.getLineInfo(), uids.length);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if(uids[0]==uids[1]){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:uid屏蔽列表错误(uid=%d)",Tools.getLineInfo(), uids[0]);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		//case5:解除屏蔽,设置重启后生效,列表应为空.
		//case5.1 解除接口的返回值应为true.,获取屏蔽列表应为空
		ret=telephonyManager.setUIDwithMobileNetworkBlock(Integer.parseInt(Integer.toString(qqBrowserUid,10)),false);
		if(ret!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:解除4G屏蔽失败",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		ret=telephonyManager.setUIDwithMobileNetworkBlock(Integer.parseInt(Integer.toString(qqMusicUid,10)),false);
		if(ret!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:解除4G屏蔽失败",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//获取屏蔽列表,应为空
		int[]uids=telephonyManager.getUIDwithMobileNetworkBlock();
		if(uids!=null){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:uid屏蔽列表与实际设置不符(%d)",Tools.getLineInfo(), uids.length);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case4.2 未重启,应用无法使用4G网络,可以正常使用wifi,不在屏蔽列表的应用访问网络正常
		if(gui.ShowMessageBox("qq音乐和qq浏览器无法使用4G访问网络,可以通过wifi访问网络,自检应用可以正常访问网络,是否符合预期,是[确定],否[取消]".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:未重启解除屏蔽生效,与文档描述不符",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case6：重启设备,所有应用应均可以正常通过4G和wifi访问网络.【BUG2020032402148不需要重启就会生效 by 20200324 zhengxq】
		gui.cls_show_msg1(1,"已解除qq浏览器和qq音乐的4G网络屏蔽,请打开设备对应应用和自检验证,若使用4G网络,wifi均可以访问网络则本项测试通过");
	}
	
	
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
	}
	
}