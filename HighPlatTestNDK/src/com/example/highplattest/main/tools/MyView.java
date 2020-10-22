package com.example.highplattest.main.tools;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder holder;
	private MyThread myThread;
//	private Paintter_ns paintterNs;
//	private int color;
	private Context context;
//	private Handler handler;
	
	public MyView(Context context,int x,int y,int x_len,int y_len,Paintter_ns paintter_ns,int color) 
	{
		super(context);
		this.context = context;
//		this.paintterNs = paintter_ns;
//		this.color = color;
		holder = this.getHolder();
		holder.addCallback(this);
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
		private int flag = 1;
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
//            int count = 0;  
            while(isRun)    
            {    
                Canvas c = null;    
                try 
                {    
                    synchronized (holder)    
                    {    
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。     
                        c.drawColor(Color.WHITE);//设置画布背景颜色     
                        Paint p = new Paint(); //创建画笔     
                        if(flag>=1)// LCD的拖影测试，小方块
                        {
                        	p.setColor(Color.BLACK);    
                            Rect r = new Rect(x, y, x_end,y_end);  
                            if(x-- == 0)
            			    {
                            	flag = 0;
            			    	++x;
            			    	new Gui(context, null).wait_key(3);
            			    }
            			    y--;
            			    x_end = x_end + 1;
            			    y_end = y_end + 1;
                            c.drawRect(r, p);    
                        }
                        if(flag<1)// LCD的残影测试
                        {
                        	p.setColor(Color.BLACK); 
                        	Rect r = new Rect(x, y, x_end,y_end);  
                        	if((x++)==((GlobalVariable.ScreenHeight>=GlobalVariable.ScreenWidth)? (GlobalVariable.ScreenWidth/2-1):(GlobalVariable.ScreenHeight/2-1)))
                        	{
                        		flag = 1;
                        		x--;
                        		new Gui(context, null).wait_key(3);
                        	}
                        	y++;
                        	x_end = x_end-1;
                        	y_end = y_end-1;
                        	c.drawRect(r, p);   
//                        	Log.e("run", x+"  "+ (GlobalVariable.ScreenHeight/2-1));
//                        	if(x==GlobalVariable.ScreenHeight/2-1)
//                        	{
//                        		GlobalVariable.PORT_FLAG = false;
//                        	}
                        }
//                        c.drawText("这是第"+(count++)+"秒", 100, 310, p);    
//                        Thread.sleep(1000);//睡眠时间为1秒     
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
//                        Log.e("MyView", x+"");
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
