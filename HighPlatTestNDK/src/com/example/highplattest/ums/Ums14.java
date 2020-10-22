package com.example.highplattest.ums;

import java.util.ArrayList;
import java.util.List;

import android.newland.ums.UmsApi;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums14.java 
 * history 		 	: 变更点													变更时间				变更人员
 * 					  新增： 擦除数据,恢复出厂设置至银商版状态restoreFactory		   	    20200812	 		郑薛晴	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums14 extends UnitFragment{
	private final String TESTITEM = "restoreFactory(银商)";
	public final String FILE_NAME = Ums14.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums14()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.擦除数据后置验证\n1.擦除数据测试(恢复出厂设置操作)\n",TESTITEM);
			switch (nkeyIn) {
			case '0':
				List<String> sizeList = umsApi.getDisableIpAddressList();
				int size = sizeList==null?0:sizeList.size();
				gui.cls_show_msg("擦除数据后黑名单列表个数=%d,camera状态=%s,锁机状态=%s",size,umsApi.isCameraDisable(),umsApi.isLockMachine());
				break;
				
			case '1':
				restoreTest();
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	private void restoreTest()
	{
		
		// case1:设置黑名单IP 10个，设置禁用Camera等，擦除数据,预期 擦除数据成功，恢复出厂后获取IP黑名单为空，camera为开启状态
		List<String> disableBlackList = new ArrayList<String>();
		disableBlackList.add("www.baidu.com");
		disableBlackList.add("note.youdao.com");
		disableBlackList.add("www.toutiao.com");
		disableBlackList.add("news.ifeng.com");
		disableBlackList.add("www.qq.com");// www.qq.com
		disableBlackList.add("112.80.225.227");
		disableBlackList.add("218.66.48.230");
		disableBlackList.add("203.208.39.226");// www.google.cn
		disableBlackList.add("103.72.47.249");// fanyi.youdao.com
		disableBlackList.add("203.208.43.66");// translate.google.cn
		
		for (String str:disableBlackList) {
			umsApi.disableApplicationNetwork(str);
		}
		umsApi.disableCamera();
		
		umsApi.lockMachine("12345678");
		
		gui.cls_show_msg1(1, "即将开始擦除,擦除数据会进行恢复出厂设置，之前设备已设置了10个白名单，禁用camera，锁定机器，擦除成功之后状态会变化才可视为测试通过");
		umsApi.restoreFactory();
	}

	@Override
	public void onTestUp() {
		umsApi = new UmsApi(myactivity);
	}

	@Override
	public void onTestDown() {
		
	}

}
