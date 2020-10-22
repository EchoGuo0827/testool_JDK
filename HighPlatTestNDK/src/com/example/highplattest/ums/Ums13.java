package com.example.highplattest.ums;

import java.util.ArrayList;
import java.util.List;

import android.newland.ums.UmsApi;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums13.java 
 * history 		 	: 变更点													变更时间				变更人员
 * 					  新增：查询被禁止联网的ip地址getDisableIpAddressList		   	    20200813	 		郑薛晴	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums13 extends UnitFragment{
	private final String TESTITEM = "getDisableIpAddressList(银商)";
	public final String FILE_NAME = Ums13.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums13()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元测试\n1.重启测试\n",TESTITEM);
			switch (nkeyIn) {
			case '0':
				try {
					testUms13();
				} catch (Exception e) {
					gui.cls_show_msg1_record(FILE_NAME, "ums13", 0, "line %d:抛出异常%s", Tools.getLineInfo(),e.getMessage());
				}
				
				break;
				
			case '1':
				try {
					int size = umsApi.getDisableIpAddressList().size();
					gui.cls_show_msg( "重启后黑名单个数=%d，应与重启前一致", size);
				} catch (Exception e) {
					gui.cls_show_msg1_record(FILE_NAME, "ums13", 0, "line %d:抛出异常%s", Tools.getLineInfo(),e.getMessage());
				}

				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	public void testUms13()
	{
		String funcName = "testUms13";
		List<String> blackList;
		boolean iRet;
		
		gui.cls_show_msg("请先保证设备可正常联网,安装/SVN/Tool/银商安全工具/pingtools.apk,完成后任意键继续");
		// case3.3：清除所有的之前的黑名单列表，获取到的黑名单列表个数应该0
		gui.cls_show_msg1(1, "case3.3:移除所有的之前的黑名单列表，获取到的黑名单列表个数应该0");
		List<String> oldBlackList = umsApi.getDisableIpAddressList();
		for (String str:oldBlackList) {
			umsApi.enableApplicationNetwork(str);
		}
		
		oldBlackList = umsApi.getDisableIpAddressList();
		if(oldBlackList!=null&&oldBlackList.size()>1)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.3测试失败(size=%d)", Tools.getLineInfo(),oldBlackList.size());
			if(!GlobalVariable.isContinue)
				return;
		}
				
		// case3.1:将有道的IP添加到黑名单，获取黑名单列表
		gui.cls_show_msg1(1, "case3.1:将有道翻译的IP添加到黑名单，获取黑名单列表");
		if((iRet = umsApi.disableApplicationNetwork("103.72.47.248"))==false)  //fanyi.youdao.com
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		blackList = umsApi.getDisableIpAddressList();
		String blackName = blackList==null||blackList.size()==0?"NULL":blackList.get(blackList.size()-1);
		if(blackName.equals("103.72.47.248")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败(blakNamet=%s)", Tools.getLineInfo(),blackName);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.2:重复添加同一个黑名单地址，获取的黑名单列表
		gui.cls_show_msg1(1, "case4.2:重复添加同一个黑名单地址，获取的黑名单列表");
		int count=0;// 用于计数 fanyi.youdao.com这个域名被找到几次
		if((iRet = umsApi.disableApplicationNetwork("103.72.47.248"))==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		blackList = umsApi.getDisableIpAddressList();
		if(blackList!=null)
		{
			for(String str:blackList)
			{
				if(str.equals("103.72.47.248"))
					count++;
			}
		}
		if(count!=1)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case4.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.2:添加100个左右的黑名单，获取黑名单列表个数
		gui.cls_show_msg1(1, "case3.2:添加100个左右的黑名单，获取黑名单列表个数");
		int oldSize = umsApi.getDisableIpAddressList().size();
		List<String> newBlackList = new ArrayList<String>();
		newBlackList.add("news.163.com");// 167.177.151.109
		newBlackList.add("note.youdao.com");
		//newBlackList.add("www.toutiao.com");
		newBlackList.add("news.ifeng.com");
		newBlackList.add("www.qq.com");// www.qq.com
		for (int i = 1; i < 97; i++) {
			newBlackList.add("203.208.39."+i);
		}
		for (String str:newBlackList) {
			umsApi.disableApplicationNetwork(str);
		}
		int newSize = umsApi.getDisableIpAddressList().size();
		if((newSize-oldSize)!=100)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败(oldSize=%d,newSize=%d)", Tools.getLineInfo(),oldSize,newSize);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4.1:重启后获取黑名单列表
		if(gui.cls_show_msg("case4.1:重启后获取黑名单列表,是否进行该case测试,该case需要重启,测试[确认],不测试[其他]")==ENTER)
		{
			gui.cls_show_msg1(3, "即将重启,重启前的黑名单个数=%d,重启后的个数应与重启前一致",umsApi.getDisableIpAddressList().size());
			Tools.reboot(myactivity);
		}
		
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		umsApi = new UmsApi(myactivity);
	}

	@Override
	public void onTestDown() {
		
	}

}
