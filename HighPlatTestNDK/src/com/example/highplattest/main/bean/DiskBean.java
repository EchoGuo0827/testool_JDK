package com.example.highplattest.main.bean;
/************************************************************************
 * 
 * module 			: main
 * file name 		: DiskBean.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 获取存储空间大小
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class DiskBean 
{
	private long freeSpace;
	private long totalSpace;
	
	public long getFreeSpace() 
	{
		return freeSpace;
	}
	
	public void setFreeSpace(long freeSpace) 
	{
		this.freeSpace = freeSpace;
	}
	
	public long getTotalSpace() 
	{
		return totalSpace;
	}
	
	public void setTotalSpace(long totalSpace) 
	{
		this.totalSpace = totalSpace;
	}
	
}
