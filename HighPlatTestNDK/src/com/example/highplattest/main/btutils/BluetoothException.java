package com.example.highplattest.main.btutils;


/**
 * 蓝牙异常控制类
 * @author zhengxq
 * 2016-4-6 下午3:52:23
 */
public class BluetoothException extends Exception 
{

	private static final long serialVersionUID = 6905908637910314222L;
	
	private BluetoothExceptionType bluetoothExceptionType;

	public BluetoothException(BluetoothExceptionType bluetoothExceptionType, String detailMessage, Throwable throwable) 
	{
		super(detailMessage, throwable);
		this.bluetoothExceptionType = bluetoothExceptionType;
	}
	
	public BluetoothException(BluetoothExceptionType bluetoothExceptionType, String detailMessage) 
	{
		super(detailMessage);
		this.bluetoothExceptionType = bluetoothExceptionType;
	}
	
	public BluetoothException(BluetoothExceptionType bluetoothExceptionType, Throwable throwable) 
	{
		super(throwable);
		this.bluetoothExceptionType = bluetoothExceptionType;
	}
	
	public BluetoothException(BluetoothExceptionType bluetoothExceptionType) 
	{
		super();
		this.bluetoothExceptionType = bluetoothExceptionType;
	}
	
	public BluetoothExceptionType getBluetoothExceptionType() 
	{
		return bluetoothExceptionType;
	}

	public void setBluetoothExceptionType(
			BluetoothExceptionType bluetoothExceptionType) 
	{
		this.bluetoothExceptionType = bluetoothExceptionType;
	}
	
	@Override
	public String getMessage() 
	{
		if(super.getMessage() == null || super.getMessage().length() == 0 || super.getMessage().getBytes().length == super.getMessage().length()){
			return this.getBluetoothExceptionType().toString();
		}
		return super.getMessage();
	}
}
