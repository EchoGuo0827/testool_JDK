package com.example.highplattest.other;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other22.java 
 * history 		 	: 变更记录					变更时间			变更人员
 *			  	     N920_A7增加AutoService自动重启 		20200515  		魏美杰
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other22 extends UnitFragment{
	
	public final String TAG = Other22.class.getSimpleName();
	private final String TESTITEM = "AutoService异常停止后自动重启(N920_A7)";
	private Gui gui = new Gui(myactivity, handler);
    private static final String KEY_SERVICE_CLASSNAME = "com.yltxdz.jfpos.ui.auto.AutoService";
	
	public void other22()
	{
		String funcName = "other22";
		/*process body*/	
		gui.cls_show_msg("测试前请先安装SVN上的TestService并打开运行,完成后任意键继续");
		if(isAccessibilitySettingsOn()!=true){
			gui.cls_show_msg1_record(TAG, funcName, gScreenTime,"TestService服务未正常开启,长按退出用例");
			return;
		}
		gui.cls_show_msg("点击TestService的停止服务按键,完成后任意键继续");
		if(isAccessibilitySettingsOn()!=true){
			gui.cls_show_msg1_record(TAG, funcName, gScreenTime,"%s测试失败，TestService的运行状态为%s",  TESTITEM,isAccessibilitySettingsOn());
			return;
		}
		gui.cls_show_msg1(1,"TestService已自动重启成功");
		gui.cls_show_msg1_record(TAG, funcName, gScreenTime,"%s测试通过",TESTITEM);
		
	}
	
    private boolean isAccessibilitySettingsOn() {
        ActivityManager am = (ActivityManager) myactivity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().equals(KEY_SERVICE_CLASSNAME)) {
                return true;
            }
        }
        return false;
    }

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
