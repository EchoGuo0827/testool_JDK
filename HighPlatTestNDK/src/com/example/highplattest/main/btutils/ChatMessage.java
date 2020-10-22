package com.example.highplattest.main.btutils;

/************************************************************************
 * 
 * module 			: main
 * file name 		: ChatMessage.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 显示蓝牙列表
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class ChatMessage 
{
	private String message;
	private boolean isSiri;
	
	public ChatMessage(String message, boolean siri) 
	{
		this.message = message;
		this.isSiri = siri;
	}

	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}

	public boolean isSiri() 
	{
		return isSiri;
	}

	public void setSiri(boolean isSiri) 
	{
		this.isSiri = isSiri;
	}
}
