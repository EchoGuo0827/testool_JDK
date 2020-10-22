package com.example.highplattest.main.btutils;

import java.io.IOException;
import java.util.UUID;

import com.example.highplattest.main.constant.HandlerMsg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
/**
 * 蓝牙回连功能类
 * @author zhengxq
 * 2016-4-6 下午3:50:47
 */
public class AcceptThread extends Thread 
{
	// 数据通道的UUID
	private final UUID DATA_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//	private final UUID CMD_UUID = UUID.fromString("0000D5D5-0000-1000-8000-00805F9B34FB");
	private final String TAG = AcceptThread.class.getSimpleName();
	private BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	private BluetoothServerSocket mmServerSocket;
	private BluetoothSocket bluetoothSocket = null;
	private BluetoothService service = null;
	private BlueBean bean;
	private UUID uuid;

	public AcceptThread(BluetoothService service,UUID uuid,BlueBean bean) 
	{
		BluetoothServerSocket tmp = null;
			try {
				if(uuid.toString().equals(DATA_UUID.toString()))
					mmServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothChat", uuid);
				else 
					mmServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothChat2", uuid);
				this.service = service;
				bean.setIscmdback(false);
				bean.setIsdateback(false);
				this.bean = bean;
				this.uuid = uuid;
			} catch (IOException e) {
				e.printStackTrace();
				mmServerSocket = tmp;
			}
	}

	@Override
	public void run() {
			try {
				sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				Log.e("AcceptThread1", "中断");
			}
			Log.e("AcceptThread", "enter accept run");
			try {
				bluetoothSocket = mmServerSocket.accept();
				Log.d("zxq---"+TAG, "回连成功");
				// 将socket传给blueService中的socket
				service.setblueSocket(bluetoothSocket);
				// 判断目前的链路通道
				if(uuid.toString().equals(DATA_UUID.toString()))
				{
					bean.setIsdateback(true);
//					myHandler.sendEmptyMessage(HandlerMsg.DONGLE_DATA_BACK);
				}
				else{
					bean.setIscmdback(true);
//					myHandler.sendEmptyMessage(HandlerMsg.DONGLE_CMD_BACK);
				}
				Log.d("zxq---"+TAG, "AcceptThread结束"+uuid);

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("AcceptThread2", "Exception");
			}finally {
               cancel();
            }
		}
	
	public void cancel() 
	{
		try {
			mmServerSocket.close();
			System.out.println("close socket");
		} catch (IOException e) {
		}
	}
}
