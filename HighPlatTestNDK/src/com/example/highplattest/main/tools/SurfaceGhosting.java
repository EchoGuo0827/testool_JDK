package com.example.highplattest.main.tools;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Paintter_ns;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceGhosting extends SurfaceView implements SurfaceHolder.Callback
{
	private final String TAG = "SurfaceGhosting";
	private SurfaceHolder holder;
	private MyThread myThread;
	
	public SurfaceGhosting(Context context,Paintter_ns paintter_ns,int color) 
	{
		super(context);
		holder = this.getHolder();
		holder.addCallback(this);
		myThread = new MyThread(holder);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
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
		private int flagx = 1;
		private int flagy = 1;
		int x = 0;
        int y = 0;
        int len = 10;
		
		public  MyThread(SurfaceHolder holder)
		{    
            this.holder =holder; 
            isRun = true;    
        }    
		
        @Override    
        public void run()    
        {    
//            Log.e("isRun", isRun+"");
            while(isRun)    
            {    
                Canvas c = null;    
                try 
                {    
                    synchronized (holder)    
                    {    
                    	LoggerUtil.d(TAG+",run===position"+ x+" "+y);
//                    	Log.e("enter", "enter");
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。     
                        c.drawColor(Color.BLACK);//设置画布为全黑
                        Paint p = new Paint(); //创建画笔     
                        p.setColor(Color.WHITE);
                        // 画白色小矩形
                        Rect r = new Rect(x, y, x+len,y+len);
                        c.drawRect(r, p);
                        p.setColor(Color.BLACK);
                        c.drawLine(x, y, x+len-1, y+len-1, p);
                        SystemClock.sleep(10);
                        // 修改方向
                        if(x==0)
                        	flagx = 1;
                        if(x == (GlobalVariable.ScreenWidth-len))
                        	flagx = 0;
                        if(y == 0)
                        	flagy =1;
                        if(y == (GlobalVariable.ScreenHeight-len))
                        	flagy = 0;
                        // 左到右
                        if(flagx == 1&& flagy == 1)
                        {
                        	x++;
                        	y++;
                        }
                        // 右上到左下
                        if(flagx == 0&&flagy == 1)
                        {
                        	x--;
                        	y++;
                        }
                        // 右下到左上
                        if(flagx == 0&&flagy == 0)
                        {
                        	x--;
                        	y--;
                        }
                        if(flagx==1&&flagy==0)
                        {
                        	x++;
                        	y--;
                        }
                        
                    }    
                }    
                catch (Exception e) 
                {    
                    e.printStackTrace();    
                }    
                finally    
                {    
                	LoggerUtil.d(TAG+",finally");
                    if(c!= null)    
                    {    
                        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。  
                        if(GlobalVariable.isBackkey)
                        {
                        	surfaceDestroyed(holder);
                        }
                    }    
                }    
           }    
        }    
	}
}
