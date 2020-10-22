package com.example.highplattest.main.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.TimeUnit;

import com.example.highplattest.main.bean.PushNoticeBean;
import com.example.highplattest.main.tools.Gui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

public class HeartBeatsClient {
	protected final HashedWheelTimer timer = new HashedWheelTimer();  
    
    private Bootstrap boot;  
  
    private PushNoticeBean noticeInfo;
    private String fileName;
    private Context myactivity;
    private Gui gui;
    public HeartBeatsClient(Gui gui,Context myactivity, PushNoticeBean noticeInfo,String fileName){
    	this.noticeInfo=noticeInfo;
    	this.fileName=fileName;
    	this.myactivity=myactivity;
    	this.gui=gui;
    }
  
    public void  connect(int port, String host) throws Exception {  
          
        EventLoopGroup group = new NioEventLoopGroup();    
          
        boot = new Bootstrap();  
        boot.group(group)
        	.channel(NioSocketChannel.class)
        	.handler(new LoggingHandler(LogLevel.INFO));  
        
        final ConnectionWatchdog watchdog = new ConnectionWatchdog(boot, timer, port,host, true,noticeInfo,fileName,myactivity,gui)
        {  
  
                public ChannelHandler[] handlers() {  
                    return new ChannelHandler[] {  
                            this,  
                            new StringDecoder(),  
                            new StringEncoder(),  
                            new HeartBeatClientHandler(gui,noticeInfo,fileName)
                            
                    };  
                }  
		};
	
		boot.handler(new ChannelInitializer<Channel>() {

			// 初始化channel
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(watchdog.handlers());
			}
		});

		ChannelFuture future;
		// 进行连接
		try {
			synchronized (boot) {
				boot.handler(new ChannelInitializer<Channel>() {

					// 初始化channel
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(watchdog.handlers());
					}
				});

				future = boot.connect(host, port);
			}
			// 以下代码在synchronized同步块外面是安全的
			future.sync();
		} catch (Throwable t) {
			gui.cls_show_msg1_record("systest76", "NoticePush", 2,"无法连接后台");
			throw new Exception("connects to  fails", t);
			
		}

    }
  
}
