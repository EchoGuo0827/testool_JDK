package com.example.highplattest.activity;

import com.example.highplattest.R;
import com.example.highplattest.main.tools.Touch2View;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class Touch2Activity extends Activity {
	
	 private int timeoutms = 60*1000;
	 
		private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			};
		};
	@Override
	protected void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去除标题 状态栏 并且保持常量
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(new Touch2View(this));
		 handler.sendEmptyMessageDelayed(0, timeoutms );
		
	}

}
