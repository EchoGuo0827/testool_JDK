package com.example.highplattest.android;

import android.annotation.TargetApi;
import android.os.Build;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android17.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180821
 * directory 		: QuickSettingService
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20180821	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android17 extends UnitFragment
{
	public final String TAG = Android17.class.getSimpleName();
	private final String TESTITEM = "QuickSettingService(A7)";
	Gui gui = new Gui(myactivity, handler);
	
	public void android17()
	{
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N)
		{
			try {
				testAndroid17();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android17", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
		}
		else
		{
			gui.cls_show_msg1_record(TAG, "android17", gKeepTimeErr, "SDK版本低于24，不支持该案例");
		}
	}
	
	@TargetApi(24)
	public void testAndroid17()
	{
		gui.cls_show_msg("测试前先把下拉状态栏调出来,完成后任意键继续");
		gui.cls_show_msg("请点击下拉状态栏顶部的编辑图标,将底部的[用例定义]图标添加到wifi等图标中后点击返回,完成任意键继续");
		if(gui.cls_show_msg("点击[快速设定图标]会变成向下箭头的图标并有悬挂通知提示,再次点击该图标又变为向右的箭头,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(TAG, "android17", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("点击悬挂通知栏应可自动从跳转到设置页面,是[确认],否[取消]")==ESC)
		{
			gui.cls_show_msg1_record(TAG, "android17", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(TAG, "android17",gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
