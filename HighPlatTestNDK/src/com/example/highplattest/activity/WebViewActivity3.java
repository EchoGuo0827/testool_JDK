package com.example.highplattest.activity;

import com.example.highplattest.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity3 extends Activity {
	
	private WebView webview;
	WebChromeClient  webChromeClient;
	 WebViewClient webViewClient;
	@Override
	protected void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		webview = (WebView) findViewById(R.id.webview);
		webview.loadUrl("file:///android_asset/html/index.html");
//		webview.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
//		webview.setWebChromeClient(webChromeClient);
//		webview.setWebViewClient(webViewClient);
		WebSettings webSettings=webview.getSettings();
	    webSettings.setJavaScriptEnabled(true);//允许使用js
	    webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
		
	}
	
	

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		webview.destroy();
		webview=null;
	}

}
