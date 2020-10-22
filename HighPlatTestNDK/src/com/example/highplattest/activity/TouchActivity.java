package com.example.highplattest.activity;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;
import com.example.highplattest.main.tools.TouchView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class TouchActivity extends Activity
{
	public static Object lockTouch = new Object();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bundle bundle = getIntent().getExtras();
		int point_x = bundle.getInt("point_x");
		int point_y = bundle.getInt("point_y");
		int width=GlobalVariable.ScreenWidth/11,height=GlobalVariable.ScreenWidth/11;
		TouchView touchView = new TouchView(this,point_x, point_y, width, height, Paintter_ns.RECT_PATTERNS_SOLID_FILL, Color.WHITE);
		setContentView(touchView);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		synchronized (lockTouch) {
			lockTouch.notify();
		}
		GlobalVariable.gScreenX = event.getX();
		GlobalVariable.gScreenY = event.getY();
		this.finish();
		return super.onTouchEvent(event);
	}
}
