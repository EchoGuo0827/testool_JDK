package com.example.highplattest.main.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.quectel.jni.QuecJNI;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class ReceiverTracker  
{

	/**
	 * 电池相关广播
	 * @author zhengxq
	 * 2017年6月21日 下午2:13:44
	 */
	public class BatteryReceiver extends BroadcastReceiver
	{
		String batMsg;
		String  chargeType;	// 充电类型
		int batLevel;		// 电池电量
		int batVol;			// 电池电压
		String batHealth;	// 电池健康情况
		String batStatus;	// 电池状态
		int batTemp;		// 电池温度
		String batTech;		// 电池技术
		
		boolean isCharge = false;
		boolean isPresent = true;
		
		
		
		public int getBatVol() {
			return batVol;
		}

		public void setBatVol(int batVol) {
			this.batVol = batVol;
		}

		public String getBatMsg()
		{
			return batMsg;
		}
		
		public void setCharge()
		{
			isCharge = false;
		}
		
		/**
		 * 是否充电
		 * @return
		 */
		public boolean getCharge()
		{
			return isCharge;
		}
		
		/**
		 * 获取目前充电类型
		 * @param plugType
		 * @return
		 */
	    private void SetPlugType(int plugType) 
	    {
	        switch (plugType) {
	        case BatteryManager.BATTERY_PLUGGED_AC:
	        	chargeType = BatteryManager.BATTERY_PLUGGED_AC + "(AC)";
	            return;

	        case BatteryManager.BATTERY_PLUGGED_USB:
	        	chargeType = BatteryManager.BATTERY_PLUGGED_USB + "(USB)";
	            return;
	        case BatteryManager.BATTERY_PLUGGED_WIRELESS:// 无线方式
	        	chargeType = BatteryManager.BATTERY_PLUGGED_WIRELESS + "(WIRELESS)";
	            return;

	        default:
	        	chargeType = "未充电";
	            return;
	        }
	    }
	    
	    public String getPlugType()
	    {
			return chargeType;
	    }
	    
	    
	    public void SetBatHealth(int health)
	    {
	    	switch (health) {
			case BatteryManager.BATTERY_HEALTH_COLD:// 电池过冷
				batHealth = "COLD";
				break;
				
			case BatteryManager.BATTERY_HEALTH_DEAD:
				batHealth = "DEAD";
				break;
				
			case BatteryManager.BATTERY_HEALTH_GOOD:// 健康状态良好
				batHealth = "GOOD";
				break;
				
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:// 电压过高
				batHealth = "OVER_VOLTAGE";
				break;
				
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:// 过热
				batHealth = "OVERHEAT";
				break;
				
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				batHealth = "UNKNOWN";
				break;
				
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				batHealth = "UNSPECIFIED_FAILURE";
				break;

			default:
				break;
			}
	    }
	    
	    /**
	     * 电池健康状况
	     * @return
	     */
	    public String getBatHealth()
	    {
	    	return batHealth;
	    }
	    
	    /**
	     * 设置电池状态
	     * @param status
	     */
	    public void SetBatStatus(int status)
	    {
	    	switch (status) 
	    	{
			case BatteryManager.BATTERY_STATUS_CHARGING:
				batStatus= "电池正在充电(CHARGING)";
				break;
				
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				batStatus = "电池正在放电(DISCHARGING)";
				break;
				
			case BatteryManager.BATTERY_STATUS_FULL:
				batStatus = "电池已充满(FULL)";
				break;
				
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				batStatus = "电池未充电(NOT_CHARGING)";
				break;
				
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				batStatus = "电池状态未知(UNKNOWN)";
				break;

			default:
				break;
			}
	    }
	    
	    /**
	     * 获取电池状态
	     * @return
	     */
	    public String getBatStatus()
	    {
	    	return batStatus;
	    }
	    
	    
        @Override
        public void onReceive(Context context, Intent intent) 
        {
        	final String action = intent.getAction();
        	if(action.equals(Intent.ACTION_BATTERY_CHANGED))
        	{
               batLevel			= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);//获得当前电量
               int total		= intent.getIntExtra(BatteryManager.EXTRA_SCALE,1);//获得总电量
               int plugType		= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0); // 充电类型
               batVol 			= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);// 电池电压
               int health		= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);// 电池的健康状况
//             int iconSmall 	= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);// 电池图标的id值
               isPresent 		= intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT,false);// 电池是否存在的额外值
               int status		= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);// 电池状态
               batTemp 			= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);// 电池温度
               batTech			= intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);// 电池技术
               
               SetPlugType(plugType);
               SetBatHealth(health);
               SetBatStatus(status);
               /**专项组审核电池温度无实际意义，去除 zhengxq 20200310*/
               batMsg  = "电池信息\n目前电池的电量："+(int)(batLevel*1.0/total*total)+"\n目前电池的电压："+batVol+"mv"+"\n充电方式："+getPlugType()
            		   +"\n电池健康："+getBatHealth()+"\n电池在位："+isPresent+"\n电池状态："+getBatStatus()+"\n电池技术："
            		   +batTech;
        	}
        	if(action.equals(Intent.ACTION_POWER_CONNECTED)) // POS定制需求
        	{
        		// 连接圆孔充电器会接收到该广播
        		isCharge = true;
        	}
        }
    }
	
	
    /**
     * apk安装相关广播
     * @author zhengxq
     * 2017年6月21日 下午2:15:12
     */
	 @SuppressLint("UseSparseArrays")
    public class ApkBroadCastReceiver extends BroadcastReceiver implements Lib
	{
        private String installName="default";
        private String unintallName="default";
        private int installResp;
        private int uninstallResp;
        private int count=-1;
		private Map<Integer,String> nameMap=new HashMap<Integer,String>();
		private Map<Integer,Integer> resultMap=new HashMap<Integer,Integer>();
		ArrayList<Integer>installResplist=new ArrayList<>();
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			Log.e("ApkBroadCastReceiver",  "进入广播count="+count);
			// 安装操作
			if (intent.getAction().equals("android.intent.action.INSTALL_APP")) 
			{
				if(installName!=null)
				{
					Log.d("INSTALL_APP", installName);
				}
				else
				{
					Log.d("INSTALL_APP", "????");
				}
				Log.d("eric_chen", "installName==="+installName);
				Log.d("eric_chen", "intent.getStringExtra==="+intent.getStringExtra("file"));
//				if(installName==null||installName.equals(intent.getStringExtra("file"))==false)
//				{
					//美团固件且安装了美团证书时，安装app返回广播的respCode类型为Int,邮储类似
					if(GlobalVariable.gCustomerID==CUSTOMER_ID.MeiTuan&&GlobalVariable.gCN.equals("MeiTuan")
					       ||GlobalVariable.gCustomerID==CUSTOMER_ID.PSBC&&GlobalVariable.gCN.equals("root")){
						installResp = intent.getIntExtra("respCode", -10086);//美团固件
					}
					else{
						installResp = Integer.parseInt(intent.getStringExtra("respCode"));
						installResplist.add(installResp);
					}
						
					// 目前安装apk的包名
					installName = intent.getStringExtra("file");
					Log.e("INSTALL_APP", installName + "  "+installResp);
					Log.e("INSTALL_APP", "installResplist[0]=="+installResplist.get(0));
					Log.e("INSTALL_APP", "installResplist=="+installResplist.size());
//				}
			} else if (intent.getAction().equals("android.intent.action.DELETE_APP")) 
			{
				if(unintallName==null||unintallName.equals(intent.getStringExtra("packageName"))==false)
				{
					// 目前卸载apk的包名
					unintallName = intent.getStringExtra("packageName");
					uninstallResp = Integer.parseInt(intent.getStringExtra("respCode"));
					// 接收一次的值就好了
					Log.e("DELETE_APP", unintallName + ""+  uninstallResp);
				}
			}
			
			// 安装操作
			if(intent.getAction().equals("android.intent.action.INSTALL_APP_HIDE"))
			{
				count++;
				Log.i("ApkBroadCastReceiver",  "进入隐式安装广播count="+count);
//				if (installName == null || installName.equals(intent.getStringExtra("file")) == false) 
//				{
				//美团固件且安装了美团证书时，安装app返回广播的respCode类型为Int,邮储类似
				if(GlobalVariable.gCustomerID==CUSTOMER_ID.MeiTuan&&GlobalVariable.gCN.equals("MeiTuan")
				       ||GlobalVariable.gCustomerID==CUSTOMER_ID.PSBC&&GlobalVariable.gCN.equals("root"))
					installResp = intent.getIntExtra("respCode", -100086);// 美团固件
				else
				{
					LoggerUtil.d("respCode="+intent.getStringExtra("respCode"));
					LoggerUtil.d("installName="+intent.getStringExtra("file"));					
					installResp = Integer.parseInt(intent.getStringExtra("respCode"));
					installResplist.add(installResp);
					Log.e("INSTALL_APP", "installResplist[0]=="+installResplist.get(0));
					Log.e("INSTALL_APP", "installResplist=="+installResplist.size());
				}
					
				// 目前隐式安装apk的包名
				installName = intent.getStringExtra("file");
				
				Log.e("INSTALL_APP_HIDE", installName + "  "+installResp);
//				Log.e("INSTALL_APP_HIDE", "count="+count);
				resultMap.put(count, installResp);
				nameMap.put(count, installName);
//				}
				// 1表示第一次，2表示第二次
			}
			else if(intent.getAction().equals("android.intent.action.DELETE_APP_HIDE"))
			{
				Log.i("ApkBroadCastReceiver",  "进入隐式卸载广播");
				if(unintallName==null||unintallName.equals(intent.getStringExtra("packageName"))==false)
				{
					// 目前隐式卸载apk的包名
					unintallName = intent.getStringExtra("packageName");
					uninstallResp = Integer.parseInt(intent.getStringExtra("respCode"));
					Log.e("INSTALL_APP_HIDE", unintallName +"");
				}
			}
		}
		
		public void setUnintallName(String unintallName) {
			this.unintallName = unintallName;
		}

		public int getResp(int flag)
	    {
			int resp=-10086;
			LoggerUtil.i("getResp,flag="+flag+",||||"+installResp+","+uninstallResp);
	    	switch (flag) {
			case APK_INSTALL:
				resp=installResplist.get(0);
				installResplist.clear();
				return resp;

			case APK_UNINSTALL:
				return uninstallResp;
			}
	    	return installResp;
	    }
	    
	    public int getResp(int flag,int count)
	    {
	    	switch (flag) {
			case APK_INSTALL:
			    return resultMap.get(count);

			case APK_UNINSTALL:
				return uninstallResp;
			}
	    	return resultMap.get(count);
	    }
	    
	    public String getPackName(int flag)
	    {
	    	switch (flag) {
			case APK_INSTALL:
					return installName;

			case APK_UNINSTALL:
				return unintallName;
			}
			return installName;
	    }
	    public void resetPackName()
	    {
	    	installName = "default";
	    	unintallName = "default";
	    }
	    
	    public String getPackName(int flag,int count)
	    {
	    	switch (flag) {
			case APK_INSTALL:
				return nameMap.get(count);

			case APK_UNINSTALL:
				return unintallName;
			}
			return nameMap.get(count);
	    }
	    public int getHideInstallCount()
	    {
			return count;
	    }
	    public void setHideInstallCount(int count)
	    {
			this.count=count;
			resultMap.clear();
			nameMap.clear();
			
	    }
	    
	    /*public void setPara(int flag)
	    {
	    	switch (flag) {
			case APK_INSTALL:
				installName=null;
//				installResp=-1;

			case APK_UNINSTALL:
				unintallName=null;
//				uninstallResp=-1;
			}
	    }*/
	}
    
    public static Lock lockListener = new ReentrantLock();
    
    public class WifiStateBroad extends BroadcastReceiver
    {
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
			{
				int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (state) {
				case WifiManager.WIFI_STATE_DISABLED:
					if(GlobalVariable.isWifiNode==true)
					{
						QuecJNI.closeNode();// 关闭节点
						GlobalVariable.isWifiNode = false;
						synchronized (lockListener) {
							lockListener.notify();
						}
						Toast.makeText(context, "节点已关闭", Toast.LENGTH_LONG).show();
					}
					break;
					
				case WifiManager.WIFI_STATE_ENABLED:
					if(GlobalVariable.isWifiNode==false)
					{
						QuecJNI.openNode();// 打开节点
						GlobalVariable.isWifiNode = true;
						synchronized (lockListener) {
							lockListener.notify();
						}
						Toast.makeText(context, "节点已打开", Toast.LENGTH_LONG).show();
					}
					break;
					
				case WifiManager.WIFI_STATE_DISABLING:// 监听到进行节点的关闭
					break;
					
				case WifiManager.WIFI_STATE_ENABLING:// 监听到进行节点的开启

					break;
					
				case WifiManager.WIFI_STATE_UNKNOWN:
					break;

				default:
					break;
				}
			}
		}
    }
}
