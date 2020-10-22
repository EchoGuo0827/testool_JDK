package com.example.highplattest.main.btutils;

/**
 * 蓝牙异常类型
 * @author zhengxq
 * 2016-4-6 下午3:52:38
 */
public enum BluetoothExceptionType 
{

	UnSupportException
	{
		@Override
		public String toString() 
		{
			return "设备不支持蓝牙";
		}
	},
	DeviceNotFoundException
	{
		@Override
		public String toString() 
		{
			return "没有找到对应的设备";
		}
	},
	ConnectException
	{
		@Override
		public String toString() 
		{
			return "蓝牙连接失败";
		}
	},
	CloseException{
		@Override
		public String toString() 
		{
			return "关闭蓝牙连接失败";
		}
	},
	WriteException{
		@Override
		public String toString() 
		{
			return "发送数据失败";
		}
	}
	
}
