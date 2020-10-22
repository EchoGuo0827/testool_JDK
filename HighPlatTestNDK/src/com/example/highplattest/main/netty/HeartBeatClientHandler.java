package com.example.highplattest.main.netty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.highplattest.main.bean.Delayinfo;
import com.example.highplattest.main.bean.PushNoticeBean;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;



import android.util.Log;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
	private PushNoticeBean noticeinfo;
	private String fileName;
	private Gui gui;
	public HeartBeatClientHandler(Gui gui,PushNoticeBean noticeinfo,String fileName){
		this.noticeinfo=noticeinfo;
		this.fileName=fileName;
		this.gui=gui;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
		System.out.println("激活时间是："+new Date());  
        System.out.println("HeartBeatClientHandler channelActive");  
     
        ctx.fireChannelActive(); 
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		 System.out.println("停止时间是："+new Date());  
	     System.out.println("HeartBeatClientHandler channelInactive"); 
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
		String message = (String) msg;  
        Log.v("接收到的推送消息",message);  
       
        //处理推送消息
        int seq= decode(message.getBytes());
        if(seq>=noticeinfo.getTotalTime() || seq==-1){
        	ConnectionWatchdog.reconnect=false;
        
        	ctx.close();
        	Log.v("关闭", "可以成功吗");
        }else
        	ReferenceCountUtil.release(msg); 
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		 Channel channel = ctx.channel();
	        //……
	    if(channel.isActive())
	    {	
	    	ctx.close();
	    	LoggerUtil.e("异常断开");
	    }
	}
	
	 
	public int decode(byte[] rbuf){
		//解析序号
		byte[] seqByte = new byte[4];
		System.arraycopy(rbuf, 0, seqByte, 0, 4);
		int seq = noticeinfo.getLastSeq()+byteArrayToInt(seqByte);
		//解析发送时间
		byte[] timeByte = new byte[64];
		System.arraycopy(rbuf, 4, timeByte, 0, 64);
		//解析推送内容
		int contentLength=noticeinfo.getContentLength();
		byte[] sendBuf = new byte[contentLength];
		System.arraycopy(rbuf, 68, sendBuf, 0, contentLength);
		//处理发送时间
		String timeString = new String(timeByte);
		String[] timeCut = timeString.split("\n");

		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String str = df.format(System.currentTimeMillis());
		long dif = 0;
		try {
			Date nowTime = df.parse(str);
			Date receiveTime = df.parse(timeCut[0]);
			dif = (nowTime.getTime() - receiveTime.getTime()) / 1000;
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Delayinfo info=new Delayinfo(seq, dif, str, timeCut[0]);
		noticeinfo.addItem(info);
		new FileSystem().JDK_FsWriteToFile(fileName,"第" + noticeinfo.getCurrentSeq() + "次 dif=" + info.getDif() + " pos接收时间：" + info.getReceiveTime()+ " 服务器发送时间：" + info.getSendTime().substring(0,19) + "\n");
		if(gui.cls_show_msg1(2, "接收到第%d次推送消息", noticeinfo.getCurrentSeq())==0x1B)
				return -1;
		return seq;
	}
	public int byteArrayToInt(byte[] b) {
		return b[0] & 0xFF | (b[1] & 0xFF) << 8 | (b[2] & 0xFF) << 16
				| (b[3] & 0xFF) << 24;
	}
}
