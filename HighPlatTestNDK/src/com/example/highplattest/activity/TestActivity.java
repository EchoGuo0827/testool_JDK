package com.example.highplattest.activity;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;
import com.example.highplattest.main.tools.SurfaceGhosting;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;

public class TestActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		GlobalVariable.isBackkey = false;// 重置返回键的标志位
		final SurfaceGhosting surfaceGhosting = new SurfaceGhosting(this,Paintter_ns.RECT_PATTERNS_SOLID_FILL, Color.BLACK);
		surfaceGhosting.setMinimumHeight(GlobalVariable.ScreenHeight);  
		surfaceGhosting.setMinimumWidth(GlobalVariable.ScreenWidth);
		surfaceGhosting.invalidate();  
		setContentView(surfaceGhosting);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode== KeyEvent.KEYCODE_BACK)
		{
			GlobalVariable.isBackkey = true;
			TestActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
