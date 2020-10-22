package com.example.highplattest.activity;

import com.example.highplattest.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("SetJavaScriptEnabled")
 public class WebViewActivity2 extends Activity implements View.OnClickListener 
{

    private EditText weburl;
    private WebView webshow;
    private Button searchurl;
    private Button RdmIP;//这里是公司上网的ip,经开发反馈白名单不支持带端口号的ip.
    private Button OAIP;
    private Button baidu;
    private Button  sogou;
    
   @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview2);
        weburl = (EditText)findViewById(R.id.weburl);
        webshow= (WebView)findViewById(R.id.webshow);
        webshow.getSettings().setJavaScriptEnabled(true);
        webshow.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webshow.getSettings().setSupportMultipleWindows(true);
        webshow.getSettings().setBuiltInZoomControls(true);
        webshow.setWebViewClient(new WebViewClient());
        webshow.setWebChromeClient(new WebChromeClient());
        webshow.setOnTouchListener(new View.OnTouchListener()
        {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
        });
        searchurl = (Button) findViewById(R.id.searchurl);
        RdmIP=(Button)findViewById( R.id.RdmIP );
        OAIP=(Button)findViewById( R.id.OAIP );
        baidu=(Button)findViewById( R.id.baidu );
        sogou=(Button)findViewById( R.id.sogou );
        searchurl.setOnClickListener( this );
        RdmIP.setOnClickListener( this );
        OAIP.setOnClickListener( this );
        baidu.setOnClickListener( this );
        sogou.setOnClickListener( this );
        };
        @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchurl:
                webshow.clearHistory();
                webshow.clearFormData();
                webshow.loadUrl("about:blank");
                String url = weburl.getText().toString();
                Log.d("browser", "url:" + url);
                webshow.getSettings().setJavaScriptEnabled(true);
                webshow.loadUrl("https://"+url);
                break;
            case R.id.RdmIP:
                webshow.clearHistory();
                webshow.clearFormData();
                webshow.loadUrl("about:blank");
                String RdmIP="192.168.254.251/";
                webshow.getSettings().setJavaScriptEnabled( true );
                webshow.loadUrl("http://"+RdmIP );
                break;
            case R.id.OAIP:
            	webshow.clearHistory();
                webshow.clearFormData();
                webshow.loadUrl("about:blank");
                String OAIP="192.168.30.32/";
                webshow.getSettings().setJavaScriptEnabled( true );
                webshow.loadUrl("http://"+OAIP );
                break;
            case R.id.baidu:
                webshow.clearHistory();
                webshow.clearFormData();
                webshow.loadUrl("about:blank");
                String baiduhostname="www.baidu.com";
                webshow.getSettings().setJavaScriptEnabled( true );
                webshow.loadUrl( "https://"+baiduhostname );
                break;
            case R.id.sogou:
                webshow.clearHistory();
                webshow.clearFormData();
                webshow.loadUrl("about:blank");
                String sogouhostname="www.sogou.com";
                webshow.getSettings().setJavaScriptEnabled( true );
                webshow.loadUrl( "https://"+sogouhostname );
                break;
        }
        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER)
        {
            String url = weburl.getText().toString();
            Log.d("browser", "url:" + url);
            webshow.getSettings().setJavaScriptEnabled(true);
            webshow.loadUrl(url);
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webshow.canGoBack())
        {
            webshow.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
