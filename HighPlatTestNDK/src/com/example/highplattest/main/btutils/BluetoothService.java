package com.example.highplattest.main.btutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;
/**
 * 蓝牙连接、收发数据类
 * @author zhengxq
 * 2016-4-6 下午3:54:59
 */
public class BluetoothService implements NDK 
{

	private BluetoothAdapter bluetoothAdapter;

	private BluetoothDevice bluetoothDevice;

	// 连接通道的socket
	private BluetoothSocket socket;
	private int chanelValue;
	private ConnectChanel connectChanel;
	
	// 客户端读写线程
	private WriteThread writeThread;
	private ReadThread readThread;
	// 读写的数组
//	private byte[] rbuf;
//	private byte[] wbuf;
	private boolean isConnect;
	private boolean isWrite;
	private boolean isRead;
	private int MAXWAITTIME = 15*1000;

	// chanel代表数据通道，chanel2代表命令通道
	public BluetoothService(String address) 
	{
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
	}
	
	
	/**
	 * 关闭blueSocket
	 */
	protected void cancel() 
	{
		if(socket!=null)
		{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 连接通道建立 modify by zhengxq 20170405
	public boolean ConnectChanel(int chanel) 
	{
		chanelValue = chanel;
		connectChanel= new ConnectChanel(socket);
		connectChanel.start();
		// 连接成功后调用中断
		try 
		{
			connectChanel.join(MAXWAITTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return isConnect;
	}
	
	// 用于回连操作
	public void setblueSocket(BluetoothSocket socket)
	{
		this.socket = socket;
	}
	
	
	public boolean writeComm(byte[] wbuf)
	{
		if(socket!=null)
		{
//			this.wbuf = wbuf;
			try {
				writeThread = new WriteThread(socket.getOutputStream(),wbuf);
				writeThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writeThread.join(MAXWAITTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*if(isWrite == false)
			cancel();*/
		return isWrite;
	}

	public boolean readComm(byte[] rbuf) 
	{
		if(socket!=null)
		{
//			this.rbuf = rbuf;
			try {
				readThread = new ReadThread(socket.getInputStream(),rbuf);
				readThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			readThread.join(MAXWAITTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*if(isRead == false)
			cancel();*/
		return isRead;
	}
	
	public void closeRw() {
		readThread.interrupt();
		readThread = null;
	}

	protected void connectDevice() 
	{  
        try {  
            // 连接建立之前的先配对  
            if (this.bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {  
            	Method createBondMethod = bluetoothDevice.getClass().getMethod("createBond");
        		createBondMethod.invoke(bluetoothDevice); 
            } else {  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
	
	// 数据通道的UUID为：00001101-0000-1000-8000-00805F9B34FB
	private class ConnectChanel extends Thread 
	{
		
		public ConnectChanel(BluetoothSocket transsocket)
		{
			socket = transsocket;
		}
		
		@Override
		public void run() 
		{
//			if(socket == null)
//			{
				if (bluetoothDevice != null) 
				{
					
					try {
						if(chanelValue==1)
						{
							socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
						}
						else
						{
							socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("0000D5D5-0000-1000-8000-00805F9B34FB"));
						}
						socket.connect();
						isConnect = true;
						connectChanel.interrupt();
					} catch (Exception e) 
					{
						e.printStackTrace();
						isConnect = false;
						connectChanel.interrupt();
						return;
					}
				}
//			}
//			else
//			{
//				connectChanel.interrupt();
//			}
		}
	}
	
	private class ReadThread extends Thread 
	{
		int readByte;
		byte[] readBuf;
		InputStream inputStream;

		public ReadThread(InputStream inputStream,byte[] rbuf) 
		{
			this.inputStream = inputStream;
			// 这不是一种地址的传递吗
			readBuf = rbuf;
			isRead = false;
		}

		@Override
		public void run() 
		{
			try {
				if (GlobalVariable.isBluetoothReboot) 
				{
					SystemClock.sleep(1000);// 分包发送，需停留,用于POS重启回连压力测试
					readByte = inputStream.read(readBuf, 0, readBuf.length);
				} else {
					// 数据通道
					if (chanelValue == 1) 
					{
						// 超时时间到就应退出
						while (readByte < readBuf.length) {
							readBuf[readByte] = (byte) inputStream.read();
							readByte = readByte + 1;
						}
					}
					// 命令通道，读一次即可
					else {
						readByte = inputStream.read(readBuf, 0, readBuf.length);
					}
				}
				isRead = true;
				readThread.interrupt();
//				GlobalVariable.gReadThread = false;
			} catch (IOException e) {
				Log.e("zxq--close", "close read socket");
				e.printStackTrace();
//				GlobalVariable.gReadThread = false;
				try {
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				isRead = false;
				readThread.interrupt();
			}
		}
	}

	// 写数据线程
	private class WriteThread extends Thread 
	{
		OutputStream outputStream;
		byte[] wbuf;

		public WriteThread(OutputStream outputStream,byte[] wbuf) 
		{
			this.outputStream = outputStream;
			this.wbuf = wbuf;
			isWrite = false;
		}

		@Override
		public void run() 
		{
			try 
			{
				outputStream.write(wbuf);
				isWrite = true;
				writeThread.interrupt();
			} catch (Exception e) {
				try {
					outputStream.close();
					isWrite = false;
					writeThread.interrupt();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
