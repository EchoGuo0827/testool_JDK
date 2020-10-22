package com.example.highplattest.android;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.util.Log;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android8.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180411 
 * directory 		: 
 * description 		: 测试Android原生存储内存接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180411 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 * @param <VolumeInfo>
 ************************************************************************/
public class Android8<VolumeInfo> extends UnitFragment {
	public final String TAG = Android8.class.getSimpleName();
	private String TESTITEM = "存储内存接口测试(A9)";
	private Gui gui = new Gui(myactivity, handler);
	
	long to1;
	long to2;
	public static final String UUID_PRIVATE_INTERNAL = null;
    public static final String UUID_PRIMARY_PHYSICAL = "primary_physical";
    public static final String UUID_SYSTEM = "system";
    public static final UUID UUID_DEFAULT = UUID
            .fromString("41217664-9172-527a-b3d5-edabb50a7d69");
    public static final UUID UUID_PRIMARY_PHYSICAL_ = UUID
            .fromString("0f95a519-dae7-5abf-9519-fbd6209e05fd");
    public static final UUID UUID_SYSTEM_ = UUID
            .fromString("5d258386-e60d-59e3-826d-0089cdd42cc0");

		
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void android8()
	{
		if(GlobalVariable.gCurPlatVer!=Platform_Ver.A9)// 该接口只支持A8平台以上的 by 20200319 zhengxq
		{
			gui.cls_show_msg("该用例只支持Android8平台以上，点击任意键退出测试");
			unitEnd();
			return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		String state = Environment.getExternalStorageState();//获取内置sd卡状态        
		
		StorageManager storageManager = myactivity.getSystemService(StorageManager.class);
		StorageStatsManager stats = myactivity.getSystemService(StorageStatsManager.class);
		  try {
			  to1=(stats.getTotalBytes(convert(storageManager.getPrimaryStorageVolume().getUuid())))/1000000000;
			  to2=stats.getFreeBytes(convert(storageManager.getPrimaryStorageVolume().getUuid()))/1000000000;
	            Log.d("cd", stats.getTotalBytes(convert(storageManager.getPrimaryStorageVolume().getUuid())) + "");
	            Log.d("cd", stats.getFreeBytes(convert(storageManager.getPrimaryStorageVolume().getUuid())) + "");
	        } catch (IOException e) {
	            Log.d("cd", e.toString());
	        }



		//case1：内部SD卡的总大小和可用大小
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			File sdcardDir = Environment.getExternalStorageDirectory();//内置sd卡
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			
			double f1 = (double) blockSize * blockCount / (1024 * 1024 * 1024);
			double f2 = (double) availCount * blockSize / (1024 * 1024 * 1024);
			int scale = 2;// 设置位数
			BigDecimal bd1 = new BigDecimal(f1);
			bd1 = bd1.setScale(2,BigDecimal.ROUND_HALF_UP);
			f1 = bd1.floatValue();
			BigDecimal bd2 = new BigDecimal(f2);
			bd2 = bd2.setScale(scale,BigDecimal.ROUND_HALF_UP);
			f2 = bd2.floatValue(); 
			
			
			
			String f3=getRomTotalSize();
			String f4=getRomAvailableSize();
			String f5=getSDTotalSize();
			String f6= getSDAvailableSize();
//			if (gui.cls_show_msg("查看设置--存储中的内部存储空间总大小为%.2fGB，内部存储空间剩余大小为%.2fGB,[确认]是，[其他]否",f1,f2) != ENTER) 
//			{
//				gui.cls_show_msg1_record(TAG, "android8", gKeepTimeErr, "line %d:%s获取内部SD卡内存大小异常", Tools.getLineInfo(), TESTITEM);
//			}
			
			if (gui.cls_show_msg("查看设置--存储中的内部存储空间总大小为%dGB，内部存储空间剩余大小为%s[确认]是，[其他]否",to1,f4) != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "android8", gKeepTimeErr, "line %d:%s获取内部SD卡内存大小异常", Tools.getLineInfo(), TESTITEM);
			}
			
       }else{
    	   if (gui.cls_show_msg("查看设置--存储中的未挂载内部SD，[确认]是，[其他]否") != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "android8", gKeepTimeErr, "line %d:%s获取内部SD卡内存大小异常", Tools.getLineInfo(), TESTITEM);
			}
       }
		
		//case2:外置SD卡
		float total=0f;
		if((total=tfcardDetect())>0f)//是否挂载tf
		{
			if (gui.cls_show_msg("查看设置--存储中的已挂载外部SD卡，存储空间总容量为%.2fGB，[确认]是，[其他]否",total) != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "android8", gKeepTimeErr, "line %d:%s获取外部SD卡状态异常，总容量为%f", Tools.getLineInfo(), TESTITEM,total);
			}
		}else{
			if (gui.cls_show_msg("查看设置--存储中的未挂载外部SD卡，[确认]是，[其他]否") != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "android8", gKeepTimeErr, "line %d:%s获取外部SD卡状态异常", Tools.getLineInfo(), TESTITEM);
			}
		}
	      
		gui.cls_show_msg1_record(TAG, "android8",gScreenTime,"%s测试通过", TESTITEM);
	}
	
	// 获取TF是否挂载及大小
	public float tfcardDetect() {
		String tfcardPath = GlobalVariable.TFPath;/*Tools.getStoragePath(myactivity,true);*///"/storage/sdcard1/";
		if(tfcardPath==null)
			return 0f;
		File tfcardDir = new File(tfcardPath);
		// TF卡已经挂载
		if (tfcardDir.getTotalSpace() > 0) {
			double f = (double) tfcardDir.getTotalSpace() / (1000 * 1000 * 1000);
			BigDecimal b = new BigDecimal(f);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			float f1 = b.floatValue();
			return f1;
		} else {
			return 0f;
		}

	}
	
	 /** 
     * 获得机身内存总大小 
     *  
     * @return 
     */  
    private String getRomTotalSize() {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return Formatter.formatFileSize(myactivity, blockSize * totalBlocks);  
    }  
  
    /** 
     * 获得机身可用内存 
     *  
     * @return 
     */  
    private String getRomAvailableSize() {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
        return Formatter.formatFileSize(myactivity, blockSize * availableBlocks);  
    }  
    private String getSDTotalSize() {  
        File path = Environment.getExternalStorageDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return Formatter.formatFileSize(myactivity, blockSize * totalBlocks);  
    }  
    
    private String getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(myactivity, blockSize * availableBlocks);
	}

    public static UUID convert(String uuid) {
        if (Objects.equals(uuid, UUID_PRIVATE_INTERNAL)) {
            return UUID_DEFAULT;
        } else if (Objects.equals(uuid, UUID_PRIMARY_PHYSICAL)) {
            return UUID_PRIMARY_PHYSICAL_;
        } else if (Objects.equals(uuid, UUID_SYSTEM)) {
            return UUID_SYSTEM_;
        } else {
            return UUID.fromString(uuid);
        }
    }

	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
