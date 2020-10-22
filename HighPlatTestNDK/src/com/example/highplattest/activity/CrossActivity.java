package com.example.highplattest.activity;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;
import com.example.highplattest.main.tools.MyView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;

public class CrossActivity extends Activity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		int[] value = getIntent().getIntArrayExtra("value");
		final MyView myView = new MyView(this, value[0], value[1], value[2], value[3], Paintter_ns.RECT_PATTERNS_SOLID_FILL, Color.BLACK);
		myView.setMinimumHeight(GlobalVariable.ScreenHeight);
		myView.setMinimumWidth(GlobalVariable.ScreenWidth);
		// 通知view组件重绘
		myView.invalidate();
		setContentView(myView);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode== KeyEvent.KEYCODE_BACK)
		{
			CrossActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
