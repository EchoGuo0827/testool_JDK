package com.example.highplattest.activity;

import com.example.highplattest.R;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


@SuppressLint("SetJavaScriptEnabled") public class WebViewActivity extends Activity{
	private WebView webView;
	public final static int FILECHOOSER_RESULTCODE = 2;
	public ValueCallback<Uri[]> mUploadMessage;
	 //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
        }

		@Override
		@Deprecated //Android7.0 以下使用
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
	        if(url.equals("http://www.google.com/")){
	            Toast.makeText(WebViewActivity.this,"国内不能访问google,拦截该url",Toast.LENGTH_LONG).show();
	            return true;//表示我已经处理过了
	        }
			return super.shouldOverrideUrlLoading(view, url);
		}

		@SuppressLint("NewApi") @Override//Android7.0使用
		public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request) {
			// TODO Auto-generated method stub
	        if(request.getUrl().equals("http://www.google.com/")){
	            Toast.makeText(WebViewActivity.this,"国内不能访问google,拦截该url",Toast.LENGTH_LONG).show();
	            return true;//表示我已经处理过了
	         }
			return super.shouldOverrideUrlLoading(view, request);
		}

    };
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            
            BaseDialog dialog=new BaseDialog(webView.getContext(), "提示框", message, "确定", "取消", new OnDialogButtonClickListener() {
				
				@Override
				public void onDialogButtonClick(View view, boolean isPositive) {
					
				}
			});
    		dialog.show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }

		@Override
		public boolean onShowFileChooser(WebView webView,ValueCallback<Uri[]> filePathCallback,FileChooserParams fileChooserParams) {
			LoggerUtil.e("onShowFileChooser");
			openFileChooser(filePathCallback);
			return true;
		}
        
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("file:///android_asset/html/test.html");
		webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
	}
	
    /**
     * JS调用android的方法
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void  getClient(String str){
        LoggerUtil.e("html调用客户端:"+str);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (null == mUploadMessage)
            return;
        Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
        if (result != null) {
            mUploadMessage.onReceiveValue(new Uri[]{result});
        } else {
            mUploadMessage.onReceiveValue(new Uri[]{});
        }
        mUploadMessage = null;
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		LoggerUtil.e("是否有上一个页面:"+webView.canGoBack());
	    if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
	            webView.goBack(); // goBack()表示返回webView的上一页面
	            return true;
	    }
		return super.onKeyDown(keyCode, event);
	}

	private void openFileChooser(ValueCallback<Uri[]> uploadMsg) {
		mUploadMessage = uploadMsg;
		Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
		contentSelectionIntent.setType("image/*");

		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
		chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

		startActivityForResult(chooserIntent,FILECHOOSER_RESULTCODE);
	}

}
