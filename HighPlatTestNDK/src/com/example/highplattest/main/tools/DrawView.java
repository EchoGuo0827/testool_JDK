package com.example.highplattest.main.tools;


import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View 
{
	private int start_x1_point;
	private int start_y1_point;
	private int start_x_len;
	private int start_y_len;
	private Paintter_ns paintter_ns;
	private int color;

	public DrawView(Context context,
			int start_x1_point,int start_y1_point,int start_x_len,int start_y_len,Paintter_ns paintter_ns,int color) {
		super(context);
		this.start_x1_point = start_x1_point;
		this.start_y1_point = start_y1_point;
		this.start_x_len = start_x_len;
		this.start_y_len = start_y_len;
		this.paintter_ns = paintter_ns;
		this.color = color;
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint p = new Paint();

		p.setColor(color);
		if(paintter_ns == Paintter_ns.RECT_PATTERNS_NO_FILL)
		{
			p.setStyle(Paint.Style.STROKE);
		}
		else
		{
			p.setStyle(Paint.Style.FILL);
		}
		canvas.drawRect(start_x1_point, start_y1_point, start_x1_point+start_x_len, start_y1_point+start_y_len, p);
	}
}
