package com.example.highplattest.android;

import java.lang.reflect.Method;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: Android原生接口设备信息模块 
 * file name 		: Android11.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180509 
 * directory 		: 
 * description 		: 测试Android原生设备信息接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180509 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android11 extends UnitFragment {
	public final String TAG = Android11.class.getSimpleName();
	private String TESTITEM = "设备信息接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private StringBuffer info=new StringBuffer();
		
	@SuppressWarnings("deprecation")
	public void android11(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		WifiManager wm = (WifiManager)myactivity.getSystemService(Context.WIFI_SERVICE); 
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		
		BluetoothAdapter m_BluetoothAdapter = null;// Local Bluetooth adapter 
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
		String m_szBTMAC = m_BluetoothAdapter.getAddress();
		String serialName=android.os.Build.SERIAL;  
		long startTime = SystemClock.elapsedRealtime();
		String basebandString=getProperty("gsm.version.baseband", "未获取到");
		info.append("设备型号:"+Build.MODEL+"\n设备SDK版本:"+Build.VERSION.SDK
				+"\n设备的系统版本:"+Build.VERSION.RELEASE+"\n版本号:"+Build.DISPLAY+"\n基带版本:"+basebandString
				+"\n内核版本:"+getKernelVersion()+"\n序列号:"+serialName+"\nWLAN MAC:"+m_szWLANMAC+"\n蓝牙地址:"
				+m_szBTMAC+"\n已开机时间:"+getTime(startTime));
		if (gui.cls_show_msg("查看设置--关于设备的信息是否如下：\n%s,[确认]是，[其他]否",info.toString()) != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, "android11", gKeepTimeErr,"line %d:%s获取设备信息错误", Tools.getLineInfo(), TESTITEM);
		}
		
		gui.cls_show_msg1_record(TAG, "android11", gScreenTime, "%s测试通过", TESTITEM);
	}

	public static String getTime(long time) {
		String str = "";
		time = time / 1000;
		int s = (int) (time % 60);
		int m = (int) (time / 60 % 60);
		int h = (int) (time / 3600);
		str = h + "小时" + m + "分" + s + "秒";
		return str;
	}

	/**
	 * 获取内核版本
	 * @return
	 */
    public static String getKernelVersion() {
        String kernelVersion = "";
        String info = "";
        String line = "";
        info=LinuxCmd.readDevNode("/proc/version");
        try {
            if (info != "") {
                final String keyword = "version ";
                int index = info.indexOf(keyword);
                line = info.substring(index + keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return kernelVersion;
    }
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getBaseband() {
		String back="未获取到基带版本";
		try {
			Class cl = Class.forName("android.os.SystemProperties");
			Object invoker = cl.newInstance();
			Method m = cl.getMethod("get", new Class[] { String.class, String.class });
			Object result = m.invoke(invoker, new Object[] { "gsm.version.baseband", "no message" });
			return (String) result;
		} catch (Exception e) {
			e.getMessage();
		}
		return back;
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
