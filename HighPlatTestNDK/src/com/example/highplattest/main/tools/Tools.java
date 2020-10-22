package com.example.highplattest.main.tools;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21Status;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.Uri;
import android.os.PowerManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.util.Log;

/************************************************************************
 * module 			: main
 * file name 		: Tools.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141113
 * directory 		: 
 * description 		: 工具的使用
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Tools implements Lib,NDK
{
	/*---------------functions definition---------------------------*/
	// 控制超时时间的计算
	public static float getStopTime(long startTime)
	{
		long endTime = System.currentTimeMillis();
		float time =  (float) ((endTime-startTime)/1000.0);
		return time;
	}
	
	// 比较两个字节数据len长度的值是否相等
	public static boolean memcmp(byte[] data1, byte[] data2, int len) 
	{
		if (data1 == null && data2 == null) 
		{
			return true;
		}
		if (data1 == null || data2 == null) 
		{
			return false;
		}
		if (data1 == data2) 
		{
			return true;
		}
		boolean bEquals = true;
		int i;
		
		for (i = 0; i < data1.length && i < data2.length && i < len; i++) 
		{
			if (data1[i] != data2[i]) 
			{
				Log.e("i="+i, "==");
				bEquals = false;
				break;
			}
		}
		return bEquals;
	}
	
	// 比较两个字节数组的某一段数据是否相等
	public static boolean byteCompare(byte[] sendBuf,byte[] recvBuf,int start1,int end1,int start2,int end2)
	{
		Log.e("length", (end1-start1)+","+(end2-start2));
		byte [] sendBufPart = new byte[end1-start1];
		byte [] recvBufPart = new byte[end2-start2];
		int j=0;
		for (int i = start1-1; i <end1-1; i++) 
		{
			sendBufPart[j] = sendBuf[i];
			j++;
		}
		
		j=0;
		for (int i = start2-1; i <end2-1; i++) 
		{
			recvBufPart[j] = recvBuf[i];
			j++;
		}
		Log.e("number", Arrays.toString(sendBufPart));
		Log.e("name1", Arrays.toString(recvBufPart));
		return Arrays.equals(sendBufPart, recvBufPart);
	}
	
	// 卸载安装的app
	public static boolean unistallApp(String[] packageNames,Activity mActivity)
	{
		for (int j = 0; j < packageNames.length; j++) 
		{
			Uri packageURI = Uri.parse("package:"+packageNames[j]);
			if(isExistApp(mActivity,packageNames[j]))
			{
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);           
				mActivity.startActivity(uninstallIntent);  
			}
		}
		return true;
	}
	
	private static boolean isExistApp(Activity mActivity,String packageName)
	{
		PackageManager packageManager = mActivity.getPackageManager();
		List<PackageInfo> list = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		for (PackageInfo packageInfo : list) 
		{
			if(packageName.equals(packageInfo.packageName))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
	public static boolean IsContinuous(int[] compare)
	{
		/*private & local definition*/
		boolean ret = false;
		
		/*process body*/
		if(compare[0] == (compare[1]+1))
		{
			ret = true;
		}
		compare[1] = compare[0];
		return ret;
	}
	
	// 比较数据
	public static int MemCmp(byte[] sbuf,byte[] rbuf,int len,LinkType type)
	{
		/*private & local definition*/
		if(type == LinkType.ASYN)
			return (memcmp(sbuf, rbuf, 3)||byteCompare(sbuf, rbuf, 8, len, 8, len))? NDK_OK:NDK_ERR;
		else if(type == LinkType.SYNC)
			return byteCompare(sbuf, rbuf, SDLCPCKTHEADERLEN, len, SDLCPCKTHEADERLEN, len)?NDK_OK:NDK_ERR;
		else 
			return MemCmp(sbuf, rbuf, len,type);
	}
	
	public static byte makeLrc(byte[] buf,int len)
	{
		int i = 0;
		byte c = 0x00;
		
		for (; i < buf.length; i++) 
		{
			c^= buf[i];
		}
		return c;
	}
	
	
	
	// 获取行号
	public static int getLineInfo() {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		return ste.getLineNumber();
	}
	
	public static void reboot(Activity activity)
	{
		PowerManager pm = (PowerManager) activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
		pm.reboot(null); 
	}

	// 关机操作不对外开放，需要通过反射调用
	public static void shutdown(Activity activity)
	{
		try {

			// 获得ServiceManager类
			Class<?> ServiceManager = Class.forName("android.os.ServiceManager");

			// 获得ServiceManager的getService方法
			Method getService = ServiceManager.getMethod("getService",
					java.lang.String.class);

			// 调用getService获取RemoteService
			Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);

			// 获得IPowerManager.Stub类
			Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
			// 获得asInterface方法
			Method asInterface = cStub.getMethod("asInterface",android.os.IBinder.class);
			// 调用asInterface方法获取IPowerManager对象
			Object oIPowerManager = asInterface.invoke(null, oRemoteService);
			// 获得shutdown()方法
			Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class, boolean.class);
			// 调用shutdown()方法
			shutdown.invoke(oIPowerManager, false, true);

		} catch (Exception e) {
			e.printStackTrace();
			 Log.e("exception", e.toString(), e);
		}
	}
	
	/**
	 * 检测K21端目前的状态
	 * @param k21ControllerManager K21对象实例
	 * @param now 预期的状态
	 * @return
	 */
	public static K21Status k21StatusDetect(K21ControllerManager k21ControllerManager,
			K21Status now) 
	{
		long startTime = System.currentTimeMillis();
		long time;
		K21Status k21Status;

		while (true) {
			time = System.currentTimeMillis() - startTime;
			k21Status = k21ControllerManager.getK21Status();
			if (k21Status != now && time > 5000) {
				Log.d("status", k21Status + "");
				break;
			}
			if (k21Status == now) {
				time = System.currentTimeMillis() - startTime;
				Log.d("k21StatusDetect", time + "");
				break;
			}
		}
		return k21Status;
	}
	
	//获取pos当前时间
	public static String getSysNowTime() 
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
    /**
     *字节数据取反操作
     */
    public static byte[] backByte(byte[] buff)
    {
		for (int i = 0; i < buff.length; i++) 
		{
			int b = 0;
			for (int j = 0; j < 8; j++) 
			{
				int bit = (buff[i] >> j & 1) == 0 ? 1 : 0;
				b += (1 << j) * bit;
			}
			buff[i] = (byte) b;
		}
		return buff;
	}
    
    /**
     * 判断是否支持自动获取触屏接口的功能
     * @param version
     */
    public static boolean isSupportTouch(String version)
    {
    	version = version.replaceAll("V", "").replaceAll("T", "");
    	// 固件版本大于 2.0.18之后，支持自动获取触屏，否则不支持
    	if(version.compareTo("2.0.18")>=0)
    		return true;
    	else
    		return false;
    }
    
    /**
     * 判断传入的矩形框是否超出边界
     * @param rect
     * @return
     */
    public static boolean isRectBound(Rect rect)
    {
    	int left = rect.left;
    	int right = rect.right;
    	int bottom = rect.bottom;
    	int top = rect.top;
    	int width = GlobalVariable.ScreenWidth;
    	int height = GlobalVariable.ScreenHeight;
    	if(left>=0&&left<width&&right>0&&right>left&&right<=width&&top>=0&&top<height&&bottom>0&&bottom>top&&bottom<=height)
    	{
    		Log.d("isRectBound", "true");
    		return true;
    	}
    	else
    	{
    		Log.d("isRectBound", "false");
    		return false;
    	}
    }
    
    /**
     * 指令集执行结果的返回值
     * @param err  抛出的异常信息
     * @return     错误码
     */
    public static int retCode(String err)
    {
    	String code = err.substring(err.length()-2, err.length());
    	int ret = 0;
    	try {
    		ret = Integer.parseInt(code);
		} catch (NumberFormatException e) 
    	{
			ret = Integer.parseInt(code.substring(1));
		}
		return ret;
    }
    
    /**
     * 杀死目前正在运行的进程
     */
    public static void killPro()
    {
    	Log.e("Tools", "kill pro");
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    /**
     * 产生随机字符串
     * len   表示生成字符串的长度  
     */
    public static String getRandomString(int len)
    {
    	String base = "abcdefghijklmnopqrstuvwxyz0123456789";
    	Random random = new Random();
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < len; i++) 
    	{
    		int num = random.nextInt(base.length());
    		sb.append(base.charAt(num));
		}
		return sb.toString();
    	
    }
    
    /**
     * 字节数组转为int类型
     */
    public static int byte2int(byte[] res)
    {
    	Log.d("byte2int", res[0]+"-----"+res[1]);
    	// 一个字节数据左移24位变成0x？？0000000，再由移8位变成0x00??0000
    	int targets = (res[0]&0xff)|((res[1]<<8)&0xff00)/*|((res[2]<<24)>>>8)|(res[3]<<24)*/;  // |表示按位或
    	return targets;
    			
    }
    
	/**
	 * 存储数据，用于保存onvailble的调用次数
	 * @param context
	 * @param count
	 */
	public static void savaData(Context context,String key,String value)
	{
		SharedPreferences sp = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
		
	}
	
	public static String getData(Context context,String key)
	{
		SharedPreferences sp = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}
	
	// 获取字符串的字节长度
	public static int getWordCount(String s)
	{
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
	}
	
	// 取一个值的小数点后两位
	public static String getDecimal2(float value)
	{
		DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String endValue = decimalFormat.format(value);//format 返回的是字符串
		return endValue;
	}
	
	// 已测试时间
	public static String StandTime(long startTime)
	{
		long standTime = (System.currentTimeMillis()-startTime)/1000;
		int second = (int) (standTime%60);
		int min = (int) (standTime/60%60);
		int hour = (int) (standTime/3600);
		return convert(hour)+":"+convert(min)+":"+convert(second);
		
	}
	
	public static String convert(int value)
	{
		String format = value>=10?String.valueOf(value):"0"+String.valueOf(value);
		return format;
	}
	
    public static void setNdkStatus(Context context, boolean value)
    {
		SharedPreferences sp = context.getSharedPreferences("NdkShare", Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = sp.edit();
		mEditor.putBoolean("exitNdk", value);
		mEditor.commit();
    }
    
    public static boolean getNdkStatus(Context context)
    {
		SharedPreferences sp = context.getSharedPreferences("NdkShare", Context.MODE_PRIVATE);
		return sp.getBoolean("exitNdk", false);
    }
    
    
	public static String getDesignPath(Context context,DiskType diskType)
	{
		String diskPath = null;
		StorageManager mStorageManager= (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			
			
			Method getPath = storageVolumeClazz.getMethod("getPath");
//			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			Method getState = storageVolumeClazz.getMethod("getState");
			
			Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
			Object[] objs = null;
			try {
				objs = (Object[]) getVolumeList.invoke(mStorageManager);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < objs.length; i++) {
				Object storageVolumeElement = objs[i];
				String path = (String) getPath.invoke(storageVolumeElement);
				String state = (String) getState.invoke(storageVolumeElement);
				if(state.equals("mounted"))// 挂载
				{
					switch (diskType) {
					case SDDSK:
						if(path.equals("/storage/emulated/0"))
		           	 	{
		           	 		diskPath=path;
		           	 	}
						break;
						
					case TFDSK:
						if(path.equals("/storage/sdcard1"))
		           	 	{
		           	 		diskPath=path;
		           	 	}
						break;
						
					case UDISK:
						if(path.equals("/storage/emulated/0")==false&&path.equals("/storage/sdcard1")==false)
		           	 	{
		           	 		diskPath=path;
		           	 	}
						break;

					default:
						break;
					}
	           	 	if(path.equals("/storage/emulated/0")&&diskType==DiskType.SDDSK)
	           	 	{
	           	 		diskPath=path;
	           	 	}
	           	 	else if(path.equals("/storage/sdcard1")&&diskType==DiskType.TFDSK)
	           	 	{
	           	 		diskPath=path;
	           	 	}
	           	 	else if(path.equals(""))
	           	 		diskPath=path;
	           	 	LoggerUtil.e("getStoragePath="+path);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return diskPath;
	}
    
	public static List<List<String>> getStoragePath(Context context)
	{
		List<List<String>> totalStorages = new ArrayList<List<String>>();
		List<String> filePaths = new ArrayList<String>();
		List<String> deviceNames = new ArrayList<String>();
		StorageManager mStorageManager= (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			
			
			Method getPath = storageVolumeClazz.getMethod("getPath");
//			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			Method getState = storageVolumeClazz.getMethod("getState");
			
			Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
			Object[] objs = null;
			try {
				objs = (Object[]) getVolumeList.invoke(mStorageManager);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < objs.length; i++) {
				Object storageVolumeElement = objs[i];
				String path = (String) getPath.invoke(storageVolumeElement);
				String state = (String) getState.invoke(storageVolumeElement);
				if(state.equals("mounted"))// 挂载
				{
	           	 	if(path.equals("/storage/emulated/0"))
	           	 	{
	           	 		deviceNames.add("SD卡");
	           	 	}
	           	 	else if(path.equals("/storage/sdcard1"))
	           	 	{
	           	 		deviceNames.add("TF卡");
	           	 	}
	           	 	else
	           	 		deviceNames.add("U盘");
	           	 	LoggerUtil.e("getStoragePath="+path);
	           	 	filePaths.add(path+"/");
				}
			}
			totalStorages.add(deviceNames);
            totalStorages.add(filePaths);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalStorages;
	}
    
//    /**该方法适用于Android7之后*/
//	public static List<List<String>> getStoragePath(Context mContext, boolean is_removale) {
//		List<List<String>> totalStorages = new ArrayList<List<String>>();
//		List<String> filePaths = new ArrayList<String>();
//		List<String> deviceNames = new ArrayList<String>();
//        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
//        Class<?> storageVolumeClazz = null;
//        Object resList=new ArrayList<StorageVolume>();
//        String path="";
//        try {
//            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
//            Method getStorageVolumes  = mStorageManager.getClass().getMethod("getStorageVolumes");
//
//            Method getPath = storageVolumeClazz.getMethod("getPath");
//            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
//            Object result = getStorageVolumes.invoke(mStorageManager);
//            for(StorageVolume a : (List<StorageVolume>)result) {
//            	 path=(String) getPath.invoke(a)+"/";
//            	 if(path.equals("/storage/emulated/0/"))
//            	 {
//            		 deviceNames.add("SD卡");
//            	 }
//            	 else if(path.equals("/storage/sdcard1/"))
//            	 {
//            		 deviceNames.add("TF卡");
//            	 }
//            	 else
//            		 deviceNames.add("U盘");
//            	 LoggerUtil.e("getStoragePath="+path+"test.txt");
//            	 filePaths.add(path+"test.txt"); 
//            }
//            totalStorages.add(deviceNames);
//            totalStorages.add(filePaths);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return totalStorages;
//    }
	
	/**获取系统休眠时间*/
	public static int getSreenTimeout(Context context)
	{
		int result = 0;
		try {
			result = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		LoggerUtil.v("getSreenTimeout->"+result);
		return result;
	}
	
	public static void setSreenTimeout(Context context,int timeoutMs)
	{
		Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,  timeoutMs);
		Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
		context.getContentResolver().notifyChange(uri, null);
	}
	
	
	/*
	 * 判断是否安装
	* check the app is installed
	*/
	public static boolean isAppInstalled(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) 
			return false;
		 else 
			return true;
	}
	
	public static int staticSetChmod(String path) {
		Process p;
		int status = -1;
		try {
			p = Runtime.getRuntime().exec("chmod 777 " + path);
			status = p.waitFor();
			if (status == 0) {
				Log.d("chmod suc", "chmod suc");
			} else {
				Log.d("chmod fail", "chmod fail");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return status;
	}
	
	
}
