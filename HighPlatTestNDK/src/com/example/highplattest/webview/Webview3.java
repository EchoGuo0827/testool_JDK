package com.example.highplattest.webview;

import android.content.Intent;
import com.example.highplattest.activity.WebViewActivity2;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;

public class Webview3 extends UnitFragment {
	public final String TAG = Webview3.class.getSimpleName();
	private final String TESTITEM = "白名单功能webview测试(巴西)";
	private Gui gui = new Gui(myactivity, handler);
	
	public void webview3()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.BRASIL)
		{
			gui.cls_show_msg1(1, "非巴西固件，长按确认键退出");
			return;
		}
		gui.cls_show_msg("第一次测试本案例," +
				"请取消AndroidManifest.xml文件<application>标签下" +
				"'配置仅搜狗域名和OAipv4在白名单内'的<meta-data>注释,重新安装案例后点击继续," +
				"\n第二次测试本案例," +
				"请恢复'配置仅搜狗域名和OAipv4在白名单内'的注释,并取消'配置仅百度域名和上网ipv4在白名单内'的注释,重新安装后点击继续,"+
				"\n第三次测试本案例" +
				"请恢复'配置仅百度域名和上网ipv4在白名单内'注释(即两个'仅....在白名单内均被注释掉'),重新安装后点击继续");
		gui.cls_show_msg("即将跳转到webview," +
				"\n第一次测试时前两个检验按钮应成功,后两个应失败,白名单外地址输出后点击check均失败," +
				"\n第二次测试时前两个检验按钮应失败,后两个应成功,白名单外地址输入后点击check均失败" +
				"\n第三次测试时,四个检验按钮应成功,白名单外地址输入后点击check均成功");
		Intent intent = new Intent(myactivity, WebViewActivity2.class);
		myactivity.startActivity(intent);
		gui.cls_show_msg1_record(TAG, "webview3",gScreenTime, "%s测试结束", TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
}
