package com.example.highplattest.main.btutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Pack200.Unpacker;
import java.util.regex.Pattern;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.Pair_Result;
import com.example.highplattest.main.tools.LoggerUtil;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 蓝牙工具类
 * @author zhengxq
 * 2016-4-6 下午3:53:27
 */
public class BluetoothManager implements NDK
{

	public static final int REQUEST_ENABLE_BT = 0;
	
	private static BluetoothManager instance;
	
	private Context activity;
	
	private Service service;
	
	private BluetoothAdapter bluetoothAdapter;
	
	private Set<BluetoothDevice> foundedDevices = new HashSet<BluetoothDevice>();
	
	private ArrayList<BluetoothDevice> mUnpairDevices;
		
	private String mConnStatus;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() 
	{
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			LoggerUtil.e(action);
			if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
			{
			}
			else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))/**蓝牙扫描广播完毕*/
			{
//				// 扫描完毕也刷新一次界面
//				DefaultFragment.unpairAdapter.notifyDataSetChanged();
			}
			else if(action.equals(BluetoothDevice.ACTION_FOUND))/**搜索到蓝牙设备会接收到此广播*/
			{
				// 判断搜索到的蓝牙设备是否是已配对的
				boolean flag = false;
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 不添加重名的蓝牙
				for(BluetoothDevice blueDevice:foundedDevices)
				{
					if(device.getAddress().equals(blueDevice.getAddress()))
					{
						flag = true;
					}
				}
				if(!flag)
				{
					foundedDevices.add(device);
					//蓝牙打印的内置打印机改成取消保存不了，并且在前面已经做过解绑操作，这里添加就不重复 2019/1/21 zhangixnj
//					if(device.getBondState() == BluetoothDevice.BOND_NONE)
//					{
						if(GlobalVariable.isDongle)
						{
							if(device.getName()!=null)
							{
							/**去除之前的对底座地址过滤的操作20200519 魏美杰*/
//								if(Pattern.matches("N9[0-1]{1}0-BTDESK-[0-9]{8}", device.getName())||Pattern.matches("N7NL[0-9]{8}", device.getName()))
//								{
									mUnpairDevices.add(device);
//								}
							}
						}
						else
						{
							// 添加未配对的蓝牙，蓝牙Dongle的时候只搜索底座蓝牙
							mUnpairDevices.add(device);				
						}
						if(GlobalVariable.BtAbility==false)
							DefaultFragment.unpairAdapter.notifyDataSetChanged();// 蓝牙底座如果搜索到就每个都刷新界面
						
						
//					}
				}
			}
			else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                switch (device.getBondState()) 
                {  
                case BluetoothDevice.BOND_BONDING:  
                    break;  
                case BluetoothDevice.BOND_BONDED: 
                    GlobalVariable.pairResult = Pair_Result.BOND_BONDED;
                    break;  
                case BluetoothDevice.BOND_NONE:  
                    GlobalVariable.pairResult = Pair_Result.BOND_NONE;
                default:  
                    break;  
                } 
			}
			else if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
			{
				// 蓝牙链路连接上
				mConnStatus = BluetoothDevice.ACTION_ACL_CONNECTED;
			}
			else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
			{
				// 正在断开蓝牙链路
				mConnStatus = BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED;
			}
			else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
			{
				// 蓝牙链路已经断开
				mConnStatus = BluetoothDevice.ACTION_ACL_DISCONNECTED;
			}
		}
	};
	
	
	public boolean pair(String strAddr, String strPsw)
	{
		boolean result = false;

		if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
		{ // 检查蓝牙地址是否有效
			LoggerUtil.d("devAdd un effient!");
		}

		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);

		if (device.getBondState() != BluetoothDevice.BOND_BONDED)
		{
			try
			{
				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				result = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			} 

		}
		else
		{
			try
			{
				ClsUtils.createBond(device.getClass(), device);
				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				result = true;
			}
			catch (Exception e)
			{
				LoggerUtil.d("setPiN failed!");
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 构造方法
	private BluetoothManager(Context activity) 
	{
		this.activity = activity;
		// 得到蓝牙适配器
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	private BluetoothManager(Service service)
	{
		this.service = service;
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public static BluetoothManager getInstance(Context activity) 
	{
		if(instance == null){
			instance = new BluetoothManager(activity);
		}
		return instance;
	}
	
	public static BluetoothManager getInstance(Activity activity,DefaultFragment context)
	{
		if(instance == null){
			instance = new BluetoothManager(activity);
		}
		return instance;
	}
	
	public static BluetoothManager getInstance(Service service) 
	{
		if(instance == null){
			instance = new BluetoothManager(service);
		}
		return instance;
	}
	
	/**
	 * 注册广播
	 */
	public void regist() 
	{
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		// 连接方面的广播
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		if(activity != null){
			
			activity.registerReceiver(broadcastReceiver, intentFilter);
		}
		
		if(service != null){
			service.registerReceiver(broadcastReceiver, intentFilter);
		}
	}
	
	/**
	 * 取消广播注册
	 */
	public void unRegist() 
	{
		if(broadcastReceiver != null)
		{
			if(activity != null)
			{
				activity.unregisterReceiver(broadcastReceiver);
			}
			
			if(service != null)
			{
				service.unregisterReceiver(broadcastReceiver);
			}
		}
	}
	
	/**
	 * 启动蓝牙
	 * @throws BluetoothException
	 */
/*	public void setUpBluetooth() throws BluetoothException
	{
		
		if(bluetoothAdapter != null)
		{
			if(!bluetoothAdapter.isEnabled())
			{
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				if(activity != null)
				{
					activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
				if(service != null)
				{
					bluetoothAdapter.enable(); 
				}
				
			}
		}
		else
		{
			throw new BluetoothException(BluetoothExceptionType.UnSupportException);
		}
	}*/
	
	/**
	 * 关闭蓝牙
	 */
	public void setDownBluetooth() 
	{
		if(bluetoothAdapter != null)
			bluetoothAdapter.disable();
	}
	
	/**
	 * 获取已配对蓝牙设备
	 * @return
	 */
	public Set<BluetoothDevice> queryPairedDevices() 
	{
		Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
		return pairedDevices;
	}
	
	/**
	 * 获取搜索到的所有蓝牙设备
	 * <p>
	 * 改方法为阻塞方法，需要耗时12s左右
	 * @return	蓝牙设备集合
	 */
	public Set<BluetoothDevice> queryFoundedDevice(ArrayList<BluetoothDevice> unPairList) 
	{
		this.foundedDevices.clear();
		bluetoothAdapter.startDiscovery();
		this.mUnpairDevices = unPairList;
		return this.foundedDevices;
	}
	
	/**
	 * 获取搜索到的所有未配对蓝牙设备
	 * @return
	 */
	public Set<BluetoothDevice> queryUnpairedDevices() {
		bluetoothAdapter.startDiscovery();
//		unpairedDevices.clear();
		return null;
	}
	
	/**
	 * 获取蓝牙状态
	 * @return
	 */
	public int getBluetoothState() {
		return this.bluetoothAdapter.getState();
	}
	
//	/**
//	 * 根据给定的蓝牙设备名称，获取对应的远端蓝牙设备
//	 * @param name	给定的蓝牙设备名称
//	 * @return
//	 */
//	public BluetoothDevice getDeviceByName(String name) {
//		
//		Set<BluetoothDevice> pairedDevices = this.queryPairedDevices();
//		for(BluetoothDevice device : pairedDevices){
//			if(device.getName().equals(name)){
//				return device;
//			}
//		}
//		
//		Set<BluetoothDevice> foundedDevices = this.queryFoundedDevice();
//		for(BluetoothDevice device : foundedDevices){
//			if(device.getName().equals(name)){
//				return device;
//			}
//		}
//		
//		return null;
//	}
	
	/**
	 * 连接远端蓝牙设备
	 * @param device	远端蓝牙设备
	 * @throws BluetoothException
	 */
	public int connect(BluetoothDevice device) throws BluetoothException {
//		if(bluetoothService == null){
//			bluetoothService = new BluetoothService(device);
//			return bluetoothService.connect(device);
//		}
		return NDK_OK;
	}
	
	public void communicate(BluetoothDevice device) throws BluetoothException
	{
//		if(bluetoothService == null)
//		{
//			bluetoothService = new BluetoothService(device);
//			bluetoothService.communicate();
//		}
//		else
//			bluetoothService.communicate();
	}
	
	/**
	 * 数据通道建立，得到的socket就是传进来的socket
	 * @param address
	 */
	public boolean connComm(BluetoothService bluetoothService,int chanel)
	{
		return bluetoothService.ConnectChanel(chanel);
	}
	
	/**
	 * 连接通讯同时建立
	 *//*
	public BluetoothSocket connCommOnly(String address)
	{
//		GlobalVariable.gBtConnect = false;
//		boolean isConn;
		if(bluetoothService == null)
		{
			bluetoothService = new BluetoothService(address);
		}
		return bluetoothService.dataConnect(null);
	}*/
	
	/**
	 * 读数据线程
	 * @param address
	 */
	public boolean readComm(BluetoothService bluetoothService,byte[] rbuf)
	{
		return bluetoothService.readComm(rbuf);
	}
	
	/**
	 * 写数据线程
	 * @param wbuf
	 */
	public boolean writeComm(BluetoothService bluetoothService,byte[] wbuf)
	{
		return bluetoothService.writeComm(wbuf);
	}
	
	
	/**
	 * 断开蓝牙连接
	 */
	public void cancel(BluetoothService bluetoothService) 
	{
		bluetoothService.cancel();
		/*if(defaultFragment!=null)
			defaultFragment.appendInteractiveInfoAndShow("蓝牙链路主动断开连接", MessageTag.ERROR);*/
	}

//	public Set<BluetoothDevice> getUnpairedDevices() {
//		return unpairedDevices;
//	}

	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}
	
	public Set<BluetoothDevice> getFoundDevices()
	{
		return foundedDevices;
	}
	
	public int getBondState(String address)
	{
		BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
		return bluetoothDevice.getBondState();
	}
	
	public String getConnStatus()
	{
		return mConnStatus;
	}
}
