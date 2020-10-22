package com.example.highplattest.other;

import android.content.Intent;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: 其他相关
 * file name 		: other17.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20190327
 * directory 		: 
 * description 		: 提供代码实现关机功能
 * related document : 
 * history 		 	: 变更记录						变更时间				变更人员
 *			  		  created		    		20190327	 		zhangxinj
 *					  N700_巴西导入(V2.3.05)		20200623			郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class Other17 extends UnitFragment {
	public final String TAG = Other17.class.getSimpleName();
	private final String TESTITEM = "关机功能";/**需要系统权限*/
	private Gui gui = new Gui(myactivity, handler);
	public void other17()
	{
		if(!(GlobalVariable.currentPlatform==Model_Type.N700&&GlobalVariable.gCustomerID==CUSTOMER_ID.BRASIL))
		{
			gui.cls_show_msg1(1,"非N700的巴西固件，不支持该用例");
			return;
		}
		if(gui.cls_show_msg("即将测试关机功能，是否进行关机是[确认],否[其他],可正常关机即为测试通过")==ENTER){
			String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
			String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
			Intent i = new Intent(ACTION_REQUEST_SHUTDOWN);
			i.putExtra(EXTRA_KEY_CONFIRM, false);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			myactivity.startActivity(i);
		}
//		if(gui.cls_show_msg("即将测试关机功能，是否进行关机是[确认],否[其他],可正常关机即为测试通过")==ENTER)
//			Tools.shutdown(myactivity);
		gui.cls_show_msg1_record(TAG, "Other17",gScreenTime, "%s测试结束", TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
}
