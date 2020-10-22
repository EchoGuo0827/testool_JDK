package com.example.highplattest.main.btutils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.example.highplattest.main.tools.LoggerUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/************************************************************************
 * 
 * module 			: main
 * file name 		: ClsUtils.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 蓝牙工具类
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class ClsUtils {

	/**
	 * 与设备配对 
	 */
	static public boolean createBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
//		Log.e("createBond", "" + returnValue);
		return returnValue.booleanValue();
	}

	/**
	 * 与设备解除配对 
	 */
	static public boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
//		Log.e("removeBond", "" + returnValue);
		
		return returnValue.booleanValue();
	}

	static public boolean setPin(Class<?> btClass, BluetoothDevice btDevice, String str) throws Exception {
		try {
			Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[] { byte[].class });
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[] { str.getBytes()});
//			Log.e("setPin", "" + returnValue);
			return returnValue;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 取消用户输入
	static public boolean cancelPairingUserInput(Class<?> btClass, BluetoothDevice device)throws Exception {
		Method createBondMethod = btClass.getMethod("cancelPairingUserInput", boolean.class);
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
//		Log.e("cancelPairingUserInput", "" + returnValue);
		return returnValue.booleanValue();
	}

	// 取消配对
	static public boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device)throws Exception {
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
//		Log.e("cancelBondProcess", "" + returnValue);
		return returnValue.booleanValue();
	}
	//配对确认
	static public boolean setPairingConfirmation(Class<?> btClass, BluetoothDevice device)throws Exception {
		Boolean returnValue=(Boolean) btClass.getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
//		Log.e("setPairingConfirmation", "" + returnValue);
		return returnValue.booleanValue();
	}
	
	
	/**
	 * 
	 * @param clsShow
	 */
	static public void printAllInform(Class<?> clsShow) {
		try {
			// 取得所有方法
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				LoggerUtil.e( hideMethod[i].getName() + ";and the i is:" + i);
			}
			// 取得所有常量
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++) {
				LoggerUtil.e(allFields[i].getName());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//	private BluetoothDevice remoteDevice;
	private boolean result = false;
	
	public boolean pair(String strAddr, String strPsw) {

		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);
		if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
			try {
				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				ClsUtils.cancelPairingUserInput(device.getClass(), device);
//				remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} else {
			try {
				ClsUtils.removeBond(device.getClass(), device);
				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				ClsUtils.cancelPairingUserInput(device.getClass(), device);
//				remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}