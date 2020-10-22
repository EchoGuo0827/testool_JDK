package com.example.highplattest.main.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;

/**
 * socket工具类
 * @author zhengxq
 * 2016-4-6 下午4:53:23
 */
public class SocketUtil 
{
	private final String TAG = SocketUtil.class.getSimpleName();
	Socket socket;
	DatagramSocket datagramSocket;
	DataInputStream input;
	DataOutputStream out;
	String mServerIP;
	int mPort;
	// 创建实例化
	public SocketUtil(String serverIP,int port)
	{
		this.mServerIP = serverIP;
		this.mPort = port;
		
		
	}
	
	// add by 20150330
	// 建立socket的网络层
	public int setSocket(Sock_t sock_t,Gui gui)
	{
		int ret = NDK.NDK_OK;
		
		switch (sock_t) {
		case SOCK_TCP:
			try 
			{
//				socket = new Socket(mServerIP, mPort);
				// 换另外一种方式
				socket = new Socket();
				/* 据赵明权建议wifi如果传入IP地址为String要用第一种接口方式
				 * public Socket(java.net.InetAddress dstAddress,int dstPort)
				 * 这一种适用于IP地址的
				 * public Socket(java.lang.String dstName,int dstPort)
				 * 这一种是用于url的
				*/
				String[] ipString = mServerIP.split("\\.");
				byte[] ipByte = new byte[ipString.length];
				for (int i = 0; i < ipString.length; i++) 
				{
					int value = Integer.valueOf(ipString[i]);
					ipByte[i] = (byte) value;
				}
				InetAddress inetAddress = InetAddress.getByAddress("", ipByte);
				InetSocketAddress address = new InetSocketAddress(inetAddress, mPort);
				socket.connect(address,8000);
				socket.setKeepAlive(true);  
				/*// 设置Socket的超时时间
				try {
					setSoTimeout(sock_t, 40*1000);
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, "setSocket", 2, "line %d:%s", Tools.getLineInfo(),e.getMessage());
				}*/
			} catch (IOException e1) 
			{
				ret = NDK.NDK_ERR;
				e1.printStackTrace();
				gui.cls_show_msg1_record(TAG, "setSocket", 2, "line %d:%s", Tools.getLineInfo(),e1.getMessage());
			} 
			break;
			
		case SOCK_UDP:
			 try 
			 {
				datagramSocket = new DatagramSocket();
			} catch (SocketException e) 
			{
				ret = NDK.NDK_ERR;
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "setSocket", 2, "line %d:%s", Tools.getLineInfo(),e.getMessage());
			}    
			break;

		default:
			break;
		}
		return ret;
	}
	
	
	/**
	 * 设置超时时间，该方法必须在bind方法之后使用.
	 * 
	 * @param timeout
	 *            超时时间
	 * @throws Exception
	 */
	public final void setSoTimeout(Sock_t sock_t,final int timeout) throws Exception 
	{
		switch (sock_t) 
		{
		case SOCK_TCP:
			socket.setSoTimeout(timeout);
			break;
			
		case SOCK_UDP:
			datagramSocket.setSoTimeout(timeout);
			break;

		default:
			break;
		}
	}

	/**
	 * 获得超时时间.
	 * 
	 * @return 返回超时时间
	 * @throws Exception
	 */
	public final int getSoTimeout() throws Exception {
		return datagramSocket.getSoTimeout();
	}

	public final DatagramSocket getSocket() {
		return datagramSocket;
	}

	/**
	 * 向指定服务器发送数据
	 * @param sock_t TCP/UDP
	 * @param bytes	 发送的数据
	 * @param len	发送的数据的长度
	 * @param timeout	发送的超时时间
	 * @throws IOException
	 */
	public final int send(final Sock_t sock_t,final byte[] bytes,int len,int timeout) throws IOException 
	{
		// 设置超时时间
		final Timer timer = new Timer(); 
		if(timeout>0){
	        timer.schedule(new TimerTask() {  
	            public void run() {  
	                LoggerUtil.e("-------设定要指定任务--------"); 
	                close(sock_t);
	            }  	
	        },timeout);  
		}
		switch (sock_t)
		{
		case SOCK_TCP:
            out = new DataOutputStream(socket.getOutputStream()); 
            out.write(bytes, 0, len);
            out.flush();
			break;
			
		case SOCK_UDP:
			DatagramPacket dp = new DatagramPacket(bytes, len,InetAddress.getByName(mServerIP), mPort);
			datagramSocket.send(dp);
			break;

		default:
			break;
		}
		timer.cancel();
		return len;
		
	}
	/**
	 * 接收从指定的服务端发回的数据.
	 * 
	 * @param lhost
	 *            服务端主机
	 * @param lport
	 *            服务端端口
	 * @return 返回从指定的服务端发回的数据.
	 * @throws Exception
	 */
	public final int receive(final Sock_t sock_t,byte[] rbuf,int len,int timeout)throws Exception {
		
		final Timer timer = new Timer(); 
		if(timeout>0){
		
	        timer.schedule(new TimerTask() {  
	            public void run() {  
	                System.out.println("-------设定要指定任务--------"); 
	                close(sock_t);
	            }  	
	        },timeout);  
		}
		switch (sock_t) 
		{
		case SOCK_TCP:
			int readLen = 0;
			int tmplen = 0;
//			Log.e(TAG, "receive"+len);
			input = new DataInputStream(socket.getInputStream());
			// 循环读操作
			do
			{
				// 角标有点问题
				tmplen=input.read(rbuf, readLen, len-readLen);
				if(tmplen==-1) 
					break;
				readLen+=tmplen;
				LoggerUtil.e("readLen:"+readLen);
				
			}
			while(readLen<len);
			len = readLen;
			break;
			
		case SOCK_UDP:
			DatagramPacket dp = new DatagramPacket(rbuf, rbuf.length);
			datagramSocket.receive(dp);
			System.arraycopy(dp.getData(), 0, rbuf, 0, dp.getLength());
			break;

		default:
			break;
		}
		timer.cancel();
		return len;
	}

	/**
	 * 接收从指定的服务端发回的数据.
	 * 
	 * @param lhost
	 *            服务端主机
	 * @param lport
	 *            服务端端口
	 * @return 返回从指定的服务端发回的数据.
	 * @throws Exception
	 */
	public int receiveScan(final Sock_t sock_t,byte[] rbuf)throws Exception 
	{
		int readLen = 0;
		switch (sock_t) 
		{
		case SOCK_TCP:
			readLen = 0;
//			Log.e(TAG, "receive"+len);
			input = new DataInputStream(socket.getInputStream());
			// 循环读操作
				// 角标有点问题
			readLen = readLen+input.read(rbuf,0,200);
			LoggerUtil.e("readLen:"+readLen);
//			input.close();
			break;
			
		case SOCK_UDP:
			DatagramPacket dp = new DatagramPacket(rbuf, rbuf.length);
			datagramSocket.receive(dp);
			System.arraycopy(dp.getData(), 0, rbuf, 0, dp.getLength());
			break;

		default:
			break;
		}
		return readLen;
	}

	/**
	 * 关闭udp连接.
	 */
	public final int close(Sock_t sock_t) 
	{
		switch (sock_t) 
		{
		case SOCK_TCP:
			try {
				if(out!=null)
				{
					out.close();
				}
				if(input!=null)
				{
					input.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if (socket != null) 
			{  
                try 
                {  
                    socket.close();  
                } catch (IOException e) 
                {  
                    socket = null;   
                    return NDK.NDK_ERR;
                }  
            } 
			return NDK.NDK_OK;
			
		case SOCK_UDP:
			try 
			{
				datagramSocket.close();
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
				return NDK.NDK_ERR;
			}
			return NDK.NDK_OK;

		default:
			break;
		}
		return NDK.NDK_OK;
	}

	public void setServerIP(String serverIP)
	{
		this.mServerIP = serverIP;
	}
	
	public void setPort(int port)
	{
		this.mPort = port;
	}
	
	public String getServerIP()
	{
		return this.mServerIP;
	}
	
	public int getmPort()
	{
		return this.mPort;
	}
	
//	/**
//	 * 测试客户端发包和接收回应信息的方法.
//	 * 
//	 * @param args
//	 * @throws Exception
//	 */
//	public static void main(String[] args) throws Exception {
//		UdpClientSocket client = new UdpClientSocket();
//		String serverHost = "192.168.5.237";
//		int serverPort = 3456;
//		client.send(serverHost, serverPort, ("你好，阿蜜果!").getBytes());
//		String info = client.receive(serverHost, serverPort);
//		System.out.println("服务端回应数据：" + info);
//	}
}
