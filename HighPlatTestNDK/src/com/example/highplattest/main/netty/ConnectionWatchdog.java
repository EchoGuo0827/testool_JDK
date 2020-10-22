package com.example.highplattest.main.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.CharsetUtil;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.example.highplattest.main.bean.Delayinfo;
import com.example.highplattest.main.bean.PushNoticeBean;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.util.Log;

@ChannelHandler.Sharable
public class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements ChannelHandlerHolder{
	 	private final Bootstrap bootstrap;  

	    private final Timer timer;  
	    private final int port;  
	    private final String host;  
	    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("e", CharsetUtil.UTF_8));
	    public static boolean reconnect = true;  
	    private int reconnectTime=0;
	    private PushNoticeBean noticeInfo;
	    private String fileName;
		private AlarmManager am = null;
		private PendingIntent sender;
		private HeartBeatReceiver heatBeatReceiver;
		private Context myactivity;
		public  boolean alarmSet = false; 
		private FileSystem file;
		private Gui gui;
	    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port,String host, boolean reconnect,PushNoticeBean noticeInfo,String fileName,Context myactivity,Gui gui) {  
	        this.bootstrap = bootstrap;  
	        this.timer = timer;  
	        this.port = port;  
	        this.host = host;  
	        this.noticeInfo=noticeInfo;
	        this.fileName=fileName;
	        this.myactivity=myactivity;
	        this.gui=gui;
	        file=new FileSystem();
	    } 
	    /** 
	     * channel链路每次active的时候，将其连接的次数重新☞ 0 
	     */    
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub
			super.channelActive(ctx);
			System.out.println("当前链路已经激活了，重连尝试次数重新置为0");  
	        //开启定时器
	        initialize(ctx);
	        int temp=noticeInfo.getCurrentSeq();
	        if(temp<noticeInfo.getTotalTime()){
	        	
	        	 if(temp==0){
	        		 ctx.writeAndFlush(sendMessage(noticeInfo.getTotalTime()-noticeInfo.getLastSeq()));
	        		 noticeInfo.setLastSeq(temp);
	        	 }
	        	 else{
	        	
	        		 noticeInfo.setLastSeq(temp+1);
	        		 int leftTime=noticeInfo.getTotalTime()-noticeInfo.getLastSeq();
	        	     file.JDK_FsWriteToFile(fileName,"发送剩下的"+leftTime+"次\n");
	        		 ctx.writeAndFlush(sendMessage(leftTime));
	        	 }
	  	         ctx.fireChannelActive(); 
	        }
	       
		}
	private void initialize(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		Log.v("开启心跳", "开启定时器");
		am = (AlarmManager) myactivity.getSystemService(Context.ALARM_SERVICE);
		heatBeatReceiver = new HeartBeatReceiver(ctx);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.example.nettytest.heartbeat");
		myactivity.registerReceiver(heatBeatReceiver, intentFilter);
		// 开启心跳定时器
		long firstime = SystemClock.elapsedRealtime();
		Intent intentAlarm = new Intent("com.example.nettytest.heartbeat");
		sender = PendingIntent.getBroadcast(myactivity, 0, intentAlarm, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, 270*1000, sender);//从firstTime才开始执行，每隔270秒再执行
		alarmSet=true;
	}
	 public class HeartBeatReceiver extends BroadcastReceiver{  
		ChannelHandlerContext ctx;
    	public HeartBeatReceiver(ChannelHandlerContext ctx){
    		this.ctx=ctx;
    	}
        @Override  
        public void onReceive(Context context, Intent intent) { 
        	Log.v("心跳", "4.5分钟一个心跳");
        	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    		String str = df.format(System.currentTimeMillis());
    		file.JDK_FsWriteToFile(fileName, "心跳"+str+"\n");
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
        }  
    }
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
	
		//设置了定时就去掉
		if(alarmSet){
			Log.v("关闭心跳", "关闭定时器");
			am.cancel(sender);
			myactivity.unregisterReceiver(heatBeatReceiver);
			alarmSet=false;
		}
	
		if (reconnect) {
			// file.writeResult("链接断开，将进行重连\n");
			if (reconnectTime < 10) {
				reconnectTime=reconnectTime+1;
				// 重连的间隔时间2s
				SystemClock.sleep(5000);
				LoggerUtil.e("进入定时"+reconnectTime);
//				timer.newTimeout(this, timeout, TimeUnit.SECONDS);
				reConnetRun();
			}
		} else {
			LoggerUtil.e("整理数据");
			dealWithDate();

		}
		ctx.fireChannelInactive();
	}

	public byte[] intToByteArray(int a) {
		return new byte[] { (byte) (a & 0xFF), (byte) ((a >> 8) & 0xFF),
				(byte) ((a >> 16) & 0xFF), (byte) ((a >> 24) & 0xFF) };
	}

	
	public ByteBuf sendMessage(int number){
		 /**
         * magic 16:SUSPEND_REQUEST
         * mac 128:
         * interval 4:
         * total_count 4:
         * IcountPerHour 4:
         * bool bIntervalRandom 4:
         * contentLengthByte 4：
         * send_buf 自定义
         */
		String magic = "SUSPEND_REQUEST";
	
        //通道建立时发送消息给服务器
        ByteBuf buf = Unpooled.buffer(16 + 128 + 4 + 4 + 4 + 4 + 4 + noticeInfo.getContentLength());
        Log.v("推送内容长度", noticeInfo.getContentLength()+"");
        //写数据到buffer
        buf.writeBytes(magic.getBytes());
        byte[] mac=new byte[128+1];
        buf.writeBytes(mac);
        byte[] intervalByte=intToByteArray(noticeInfo.getInterval());
        buf.writeBytes(intervalByte);
        byte[] totalCountByte=intToByteArray(number);
        buf.writeBytes(totalCountByte);
        byte[] countPerHour=intToByteArray(noticeInfo.getInterval());
        buf.writeBytes(countPerHour);
        if(noticeInfo.isRandOrRegular()){
			//是否随机
			byte[] bIntervalRadom={0x01,0x00,0x00,0x00};
			buf.writeBytes(bIntervalRadom);
		}else{
			byte[] bIntervalRegular={0x00,0x00,0x00,0x00};
			buf.writeBytes(bIntervalRegular);
			
		}
        byte[] contentLengthByte=intToByteArray(noticeInfo.getContentLength());
        buf.writeBytes(contentLengthByte);
        buf.writeBytes(noticeInfo.getContent().getBytes());
        
      return buf;
	}
	@Override
	public ChannelHandler[] handlers() {
		// TODO Auto-generated method stub
		return null;
	}
	public void reConnetRun() throws Exception {
		// TODO Auto-generated method stub
		ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host,port);
        }
        //future对象
        future.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();

                //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
                if (!succeed) {
                
                	if(reconnectTime>=10)
                	{
                		LoggerUtil.e("重连失败1111"+reconnectTime);
                		file.JDK_FsWriteToFile(fileName,"重连失败\n");
                		reconnect=false;
                	}else
                	{
                		LoggerUtil.e("重连失败"+reconnectTime);
                	}
                    f.channel().pipeline().fireChannelInactive();
                }else{
                	file.JDK_FsWriteToFile(fileName,"链接断开,第"+reconnectTime+"次重连成功\n");
                	reconnectTime=0;
                }
            }
        });
	}
	public void dealWithDate(){
		int missTime = 0;
		int oneStep = 0;
		int twoStep = 0;
		int threeStep = 0;
		int fourStep = 0;
		int j=1;
		int currentSeq=0;
		long dif;
//		String receiveTime;
//		String sendTime;
		List<Delayinfo> delayInfoList=noticeInfo.getDelayInfoList();
		for(int i=0;i<delayInfoList.size();i++){
			currentSeq=delayInfoList.get(i).getSeq();
			dif=delayInfoList.get(i).getDif();
			if(currentSeq!=j)
			{
				
				missTime+=currentSeq-j;
				j=currentSeq;
			}
		
			if (dif == -1) {
				dif = 0;
			}
			if (0 <= dif && dif <= 3) {
				oneStep=oneStep+1;
			} else if (dif > 3 && dif <= 20) {
				twoStep=twoStep+1;
			} else if (dif > 20 && dif <= 180) {
				threeStep=threeStep+1;
			} else if (dif > 180) {
				fourStep=fourStep+1;
			}
			j+=1;
		}
		gui.cls_show_msg1_record("systest76", "NoticePush", 1,"%d次推送已完成，请查看sd卡下%s文件分析数据",noticeInfo.getCurrentSeq(),fileName);
		String sleepStatus=noticeInfo.isSleep()==true?"休眠状态":"不休眠状态";
		String intervalType;
		if(noticeInfo.isRandOrRegular()){
			intervalType="随机间隔推送，每小时推送"+noticeInfo.getInterval()+"次";
		}else
		{
			intervalType="固定"+noticeInfo.getInterval()+"s间隔推送";
		}
		file.JDK_FsWriteToFile(fileName,"\n("+noticeInfo.getNetInfo()+")推送测试已完成，总次数:" + currentSeq +"，"+ intervalType+"，"+sleepStatus+"\n");
		double succPR=(float) (currentSeq - missTime)/ (float) currentSeq ;
		file.JDK_FsWriteToFile(fileName,"推送成功:"+(currentSeq - missTime)+"次，推送失败："+missTime+"次，成功率："+numberFormat(succPR)+"\n\n");
		file.JDK_FsWriteToFile(fileName,"推送延迟级别分类数据如下:\n");
		int suc = currentSeq - missTime;
		double onePR=(float) oneStep/ (float) suc ;
		double twoPR=(float) twoStep/ (float) suc ;
		double threePR=(float) threeStep/ (float) suc ;
		double fourPR= (float) fourStep/ (float) suc  ;
		file.JDK_FsWriteToFile(fileName,"3秒内："+oneStep+"次，占比："+numberFormat(onePR)+"\n");
		file.JDK_FsWriteToFile(fileName,"4秒-20秒："+twoStep+"次，占比："+numberFormat(twoPR)+"\n");
		file.JDK_FsWriteToFile(fileName,"21秒-3分钟："+threeStep+"次，占比："+numberFormat(threePR)+"\n");
		file.JDK_FsWriteToFile(fileName,"3分钟以上："+fourStep+"次，占比："+numberFormat(fourPR)+"\n");
	}
	/**
	 * 保留2位小数
	 * @param value
	 * @return
	 */
	public String numberFormat(double value){
		  NumberFormat nt = NumberFormat.getPercentInstance();  
	      //设置百分数精确度2即保留两位小数  
	      nt.setMinimumFractionDigits(2); 
	      String s= nt.format(value);
	      return s;
	}
}
