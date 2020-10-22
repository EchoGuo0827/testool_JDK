package com.example.highplattest.main.tools;


import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TouchView extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder holder;
	private MyThread myThread;
	
	public TouchView(Context context,int x,int y,int x_len,int y_len,Paintter_ns paintter_ns,int color) 
	{
		super(context);
		holder = this.getHolder();
		holder.addCallback(this);
		Log.e("myView", x+" "+y+" "+(x+x_len)+" "+(y+y_len));
		myThread = new MyThread(holder,x,y,x+x_len,y+y_len);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e("surfaceCreated", "surfaceCreated");
		myThread.isRun = true;
		myThread.start();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e("surfaceChanged", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		Log.e("surfaceDestroyed", "surfaceDestroyed");
		myThread.isRun = false;
	}
	
	class MyThread extends Thread
	{
		private SurfaceHolder holder;
		public boolean isRun;
		int x;
        int y;
        int x_end;
        int y_end;
		
		public  MyThread(SurfaceHolder holder,int x,int y,int x_end,int y_end)
		{    
            this.holder =holder; 
            this.x = x;
            this.y = y;
            this.x_end = x_end;
            this.y_end = y_end;
            isRun = true;    
        }    
		
        @Override    
        public void run()    
        {    
            Log.e("isRun", isRun+"");
            while(isRun)    
            {    
                Canvas c = null;    
                try 
                {    
                	Log.e("thread", "thread");
                    synchronized (holder)    
                    {    
                    	Log.e("enter", "enter");
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。     
                        c.drawColor(Color.WHITE);//设置画布背景颜色     
                        Paint p = new Paint(); //创建画笔     
                        p.setColor(Color.BLACK);    
                        Rect r = new Rect(x, y, x_end,y_end);  
                        c.drawRect(r, p);    
                        Thread.sleep(1000);//睡眠时间为1秒     
                    }    
                }    
                catch (Exception e) {    
                    e.printStackTrace();    
                }    
                finally    
                {    
                    if(c!= null)    
                    {    
                        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。  
                        surfaceDestroyed(holder);
                        isRun = false;
//                        if(x==GlobalVariable.ScreenHeight/2-10)
//                    	{
//                    		GlobalVariable.PORT_FLAG = false;
//                    		surfaceDestroyed(holder);
//                    	}
                    }    
                }    
           }    
        }    
	}
}
