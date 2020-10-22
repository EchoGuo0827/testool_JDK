package com.example.highplattest.main.tools;

import com.example.highplattest.main.bean.DiskBean;
import com.example.highplattest.main.constant.NDK;

import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Log;
/************************************************************************
 * 
 * module 			: main
 * file name 		: DiskInfo.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 获取SD卡信息
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class DiskInfo implements NDK
{
	
	/**
	 * MEDIA_BAD_REMOVAL 直接拔U盘
	 * MEDIA_CHECKING	检测存储
	 * MEDIA_MOUNTED 外存储
	 * MEDIA_MOUNTE_READ_ONLY 写保护
	 * MEDIA_UNMOUNTABLE 系统不能挂载
	 * MEDIA_UNMOUNT 未挂载
	 * return 	NDK_OK 操作成功
	 * 			NDK_ERR_USDDISK_NONUSPPORTTYPE 不支持类型
	 */
	public int NDK_DiskGetstate()
	{
		String status = Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED))
		{
			return NDK_OK;
		}
		else if(status.equals(Environment.MEDIA_UNMOUNTED))
		{
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	/**
	 * 获取U盘或SD卡信息
	 * @param rootdir 	U盘或SD卡根目录
	 * @param diskBean	磁盘结构信息
	 * 			NDK_OK	操作成功
	 * 			NDK_ERR_USDDISK_PARAM 无效参数
	 * 			NDK_ERR	操作失败
	 */
	public int NDK_DiskGetInfo(String rootdir,DiskBean diskBean)
	{
		if(rootdir == null || diskBean == null)
		{
			return NDK_ERR_USDDISK_PARAM;
		}
		// 检查SD卡的状态
		while(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			SystemClock.sleep(2000);
		}
		diskBean.setFreeSpace(getAvailableSize(rootdir));
		diskBean.setTotalSpace(getAllSize(rootdir));
		return NDK_OK;
		
	}
	
	/**
	 * SD空闲的空间的大小
	 * @param path U盘或SD卡根目录
	 * @return 空闲的空间的大小
	 */
	public long getAvailableSize(String path)
	{
		StatFs statFs = new StatFs(path);
		@SuppressWarnings("deprecation")
		double size = (statFs.getAvailableBlocks()/1024.0)*statFs.getBlockSize();
		Log.e("DiskInfo--getAvailableSize", size+"");
		return (long) size;
	}
	
	/**
	 * U盘或SD总空间的大小
	 * @param path U盘或SD卡根目录
	 * @return U盘或SD总空间的大小
	 */
	public long getAllSize(String path)
	{
		StatFs statFs = new StatFs(path);
		@SuppressWarnings("deprecation")
		double size = (statFs.getBlockSize()/1024.0)*statFs.getBlockCount();
		Log.e("DiskInfo--getAllSize", size+"");
		return (long) size;
	}

}
