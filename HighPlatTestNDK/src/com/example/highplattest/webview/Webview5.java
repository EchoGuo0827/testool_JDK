package com.example.highplattest.webview;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

public class Webview5 extends UnitFragment
{
	public final String TAG = Webview5.class.getSimpleName();
	private final String TESTITEM = "WebView+Js显示图片(A7)";
	private Gui gui;
	
	public void webview5()
	{
		gui = new Gui(myactivity, handler);
		gui.cls_show_msg("请将/SVN/Tool/WebView+JS案例/NewlandImage.apk安装到设备里,1-1596200408.png放置到SD卡根目录,操作完毕任意键继续");
		gui.cls_show_msg1(1, "请使用webview5.bat进行自动测试,按照测试说明修改后开始测试");
	}

	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
		
	}

}
