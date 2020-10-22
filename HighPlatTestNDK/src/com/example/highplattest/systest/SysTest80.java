package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.crypto.spec.GCMParameterSpec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.highplattest.R;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.adapter.DeviceAdapater;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.SampleGattAttributes;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
/**
* related document :
* history 		 	: 变更记录								变更时间			变更人员
*			  		 新增欧洲版N910关闭BLE蓝牙扫描验证				20200812		陈丁
* 
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
@SuppressLint("NewApi")
public class SysTest80 extends DefaultFragment {
	private final String TAG = SysTest80.class.getSimpleName();
	private final String TESTITEM = "BT性能、压力(BLE蓝牙)";
	private String blutoothName;
	Gui gui = null;
	private BluetoothAdapter mBluetoothAdapter;
	/** 蓝牙适配器 */
	private BluetoothLeScanner mScanner;
	/** BLE蓝牙扫描 */
	private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	/** 扫描到的蓝牙设备 */
	private List<BluetoothGattService> mGattServices = new ArrayList<BluetoothGattService>();
	/** 获取到的蓝牙服务 */
//    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private BluetoothGattService mGattDeivceService;
	private HashMap<String, BluetoothGattCharacteristic> mCharateristicMap;
	private DeviceAdapater mDeviceAdapater;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothGatt mBluetoothGatt;

	private final int WAITIMEOUT = 10 * 1000;

	private boolean isServiceCom = false;
	private boolean isReadStart = false;
	private boolean isWriteStart = false;
	private boolean isNotifyStart = false;

	private int BLE_BUFF_SIZE = 20;
	private byte[] rBuf = new byte[BLE_BUFF_SIZE];/**目前Android的低功耗蓝牙最多只能支持20字节*/
	
	private int BLEerrcode=0;

	public void systest80() {
		gui = new Gui(myactivity, handler);
		if (GlobalVariable.gSequencePressFlag) {
			// maybe auto
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime, "%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		// 测试前置
		mDeviceAdapater = new DeviceAdapater(myactivity, devices);
		getBlueAdapter();
		boolean isScan = false;
		while (true) {
			/** 配置需要进行蓝牙服务的配置以及service的UUID和characteristic的UUID */
			String msg="";
			if (GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false) {
				msg="BT综合测试(BLE蓝牙)\n0.Service选择\n1.BT数据交互\n2.BT性能\n3.验证BLE关闭";
			}else {
				msg="BT综合测试(BLE蓝牙)\n0.Service选择\n1.BT数据交互\n2.BT性能\n";
			}
			int returnValue = gui.cls_show_msg(msg);
			switch (returnValue) {
			case '0':
				// gui.cls_show_msg2(1.0f,"正在进行BLE蓝牙扫描...");
				close();
				mCharateristicMap = new HashMap<String, BluetoothGattCharacteristic>();
				mGattServices = null;
				startScan();
				myactivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showBleScan(myactivity);
					}
				});
//				synchronized (g_lock) {
//					try {
//						g_lock.wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}

				isScan = true;
				break;

//			case '1':
//				if(isScan==true)
//				{
//					/**配置操作需要等待,如果设备已经连接上直接显示即可*/
//					gui.cls_printf("正在进行service和characteristic的UUID配置".getBytes());
//					while (isServiceCom == false) 
//					{
//						mBluetoothGatt = mBluetoothDevice.connectGatt(myactivity, false, mBluetoothCallBack);
//						synchronized (g_lock) {
//							try {
//								g_lock.wait(WAITIMEOUT);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//						if(isServiceCom == false)
//							break;
//					}
//					if(isServiceCom == true)/**已搜索到服务列表*/
//					{
//						myactivity.runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								setServiceUUID(myactivity, mGattServices);
//							}
//						});
//						synchronized (g_lock) {
//							try {
//								g_lock.wait(WAITIMEOUT);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//				else
//					gui.cls_show_msg("请先选择服务器,点任意键继续");
//
//				break;
//				
//				
			case '1':
				/** 开始BLE蓝牙的连接读取Service的内容 */
				if (isScan == true && preTest() == true) {
					int chooseKey = gui.cls_show_msg("压力测试\n0.流程压力\n1.读写压力\n");
					switch (chooseKey) {
					case '0':
						bleProPress();
						break;

					case '1':
						bleReadPress();
						break;

					default:
						break;
					}
				}
				break;

			case '2':
				gui.cls_show_msg("目前Android手机的BLE收发数据只支持20字节,故暂时不测试性能值");
				break;
			case '3':
				gui.cls_show_msg("按任意键开始扫描---");
				close();
				mCharateristicMap = new HashMap<String, BluetoothGattCharacteristic>();
				mGattServices = null;
				startScan();
				long oldtime;
				int time=0;
				oldtime=System.currentTimeMillis();
				while(time<5){
					time=(int) Tools.getStopTime(oldtime);
					if (BLEerrcode!=0) {
						gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "扫描失败,该固件不支持BLE蓝牙扫描", Tools.getLineInfo());
						break;
						
					}
				}
				if (time>=5) {
					
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "扫描成功,该固件支持BLE蓝牙扫描", Tools.getLineInfo());
				}
				mScanner.stopScan(scanCallback);
				break;

			case ESC:
				intentSys();
				return;
			}
		}
	}

	
	BluetoothGattCallback mBluetoothCallBack = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			Log.v("conn", newState + "");
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				gatt.discoverServices();// 连接成功，开始搜索服务，一定要调用此方法，否则获取不到服务
				gui.cls_printf("已连接上设备".getBytes());
			} 
//			if(newState == BluetoothProfile.STATE_DISCONNECTED)
//			{
//				Log.d(TAG, "disconn");
//			}
		};
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				isServiceCom = true;
				mGattServices = gatt.getServices();
				synchronized (g_lock) {
					g_lock.notify();
				}
				LoggerUtil.v("onServicesDiscovered->"+mGattServices.size());
			} else {
				Log.v(TAG, "onServicesDiscovered received:" + status);
				gui.cls_show_msg("line %d:获取BLE状态错误(ret = %d)", Tools.getLineInfo(), status);
				synchronized (g_lock) {
					g_lock.notify();
				}
			}

		};

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			LoggerUtil.v("onCharacteristicRead:" + status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// rBuf = characteristic.getValue().clone();
//				mGattCharacteristic = characteristic;
				isReadStart = true;
			} else {
				Log.v(TAG, "onServicesDiscovered received:" + status);
				gui.cls_show_msg("line %d:获取BLE状态错误(ret = %d)", Tools.getLineInfo(), status);
				synchronized (g_lock) {
					g_lock.notify();
				}
			}
		};

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			LoggerUtil.v("onCharacteristicWrite:" + status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
//				mGattCharacteristic = characteristic;
				isWriteStart = true;

			}
		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			LoggerUtil.v("onCharacteristicChanged");
			rBuf = characteristic.getValue();
			isNotifyStart = true;
		};
	};

	/** 测试前置确保各个特征已经配置 */
	private boolean preTest() {
		if (mCharateristicMap.get("WRITE_CH") == null || mCharateristicMap.get("READ_CH") == null) {
			gui.cls_show_msg("请先配置好[写特征]、[读特征],任意键继续");
			return false;
		}

		return true;
	}

	/** BLE流程压力 */
	private void bleProPress() {
		/* private & local definition */

		int cnt = 0, bak = 0, succ = 0;
		final int TIMEOUT = 10;
		long startTime = 0;
		byte[] sBuf = new byte[BLE_BUFF_SIZE];/** 目前android系统只能发送16字节,发送数据1K */
		BluetoothGattCharacteristic writeCharacteristic = mCharateristicMap.get("WRITE_CH");
		BluetoothGattCharacteristic readCharacteristic = mCharateristicMap.get("READ_CH");
		/* process body */
		/** 设置压力次数 */
		bak = cnt = gui.JDK_ReadData(TIMEOUT_INPUT, 20);
		close();/** 测试前置,关闭 */
		SystemClock.sleep(5000);
		while (cnt > 0) {
			// SystemClock.sleep(5000);
			if (gui.cls_show_msg1(1, "BLE流程压力测试中,已执行%d次,成功%d次,[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;
			SystemClock.sleep(5000);
			isServiceCom = false;
			startTime = System.currentTimeMillis();
			mBluetoothGatt = mBluetoothDevice.connectGatt(myactivity, false, mBluetoothCallBack);
			while (isServiceCom == false && Tools.getStopTime(startTime) < TIMEOUT) {
				SystemClock.sleep(100);
			}
			if (Tools.getStopTime(startTime) > TIMEOUT) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:连接设备超时", Tools.getLineInfo(),
						bak - cnt);
				close();
				SystemClock.sleep(5000);
				continue;
			}

//			/**1.先读取设备信息*/
//			isReadStart = false;
//			startTime = System.currentTimeMillis();
//			mBluetoothGatt.readCharacteristic(readCharacteristic);
//			/**超时时间为10s*/
//			while(isReadStart==false&&Tools.getStopTime(startTime)<TIMEOUT)
//			{
//				SystemClock.sleep(100);
//			}
//			if(Tools.getStopTime(startTime)>TIMEOUT)
//			{
//				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:读设备数据超时", Tools.getLineInfo(),bak-cnt);
//				close();
//				continue;
//			}
//	        byte[] data = readCharacteristic.getValue();
//	        if (data != null && data.length > 0) 
//	        {
//	            gui.cls_show_msg1(2, "%s",ISOUtils.hexString(data));
//	        }
//	      /*  else
//	        {
//	        	gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:读设备数据失败", Tools.getLineInfo(),bak-cnt);
//				continue;
//	        }*/

			/**
			 * (1).先判断该mGattCharacteristic是否支持写特征，若支持写特征写操作完毕之后再判断是否支持通知特征，若支持通知特征则从串口读数据
			 * (2).若不支持写特征则直接进行读特征操作
			 */
			isWriteStart = false;
			// 1.写1K数据到串口
			Arrays.fill(sBuf, (byte) 0x31);
			startTime = System.currentTimeMillis();
			writeCharacteristic.setValue(sBuf);
			// mBluetoothGatt.writeDescriptor(writeCharacteristic.getDescriptor(UUID_DATA));
			SystemClock.sleep(2000);
			mBluetoothGatt.writeCharacteristic(writeCharacteristic);
			// mBluetoothCallBack.onCharacteristicWrite(mBluetoothGatt, writeCharacteristic,
			// 0);
			/** 超时时间为10s */
			while (isWriteStart == false && Tools.getStopTime(startTime) < TIMEOUT) {
				SystemClock.sleep(100);
			}
			if (Tools.getStopTime(startTime) > TIMEOUT) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:写数据到设备超时", Tools.getLineInfo(),
						bak - cnt);
				close();
				SystemClock.sleep(5000);
				continue;
			}
			// 2.从串口读取1K的数据,换成通知的Characteristic
			Arrays.fill(rBuf, (byte) 0x00);
			isNotifyStart = false;
			startTime = System.currentTimeMillis();
//			mBluetoothGatt.readCharacteristic(readCharacteristic);
//			Log.d("sBuf",sBuf.toString());
//			Log.d("rBuf", rBuf.toString());
			// SystemClock.sleep(2000);
			mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true);
			// mBluetoothCallBack.onCharacteristicRead(mBluetoothGatt, readCharacteristic,
			// 0);
			gui.cls_printf("请先把串口接收到的数据拷到发送区并发送".getBytes());
			/** 超时时间为10s */
			while (isNotifyStart == false && Tools.getStopTime(startTime) < TIMEOUT) {
				SystemClock.sleep(100);
			}
			if (Tools.getStopTime(startTime) > TIMEOUT) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:读数据到设备超时", Tools.getLineInfo(),
						bak - cnt);
				close();
				SystemClock.sleep(5000);
				continue;
			}
			// 3.比较读写数据
			if (rBuf.length != sBuf.length || Tools.memcmp(rBuf, sBuf, sBuf.length) == false) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:数据比较失败(%d,%s)", Tools.getLineInfo(),
						bak - cnt, rBuf.length, rBuf == null ? "null" : ISOUtils.hexString(rBuf));
				close();
				SystemClock.sleep(5000);
				continue;
			}
			close();
			succ++;
		}
		// 测试后置,断开连接
		close();
		gui.cls_show_msg1_record(TAG, "bleReadPress", g_time_0, "BLE流程压力测试完成，已执行次数为%d,成功为%d次", bak - cnt, succ);
	}

	/** BLE读压力 */
	private void bleReadPress() {
		/* private & local definition */
		int cnt = 0, bak = 0, succ = 0;
		long startTime;
		final int TIMEOUT = 10;
		byte[] sBuf = new byte[BLE_BUFF_SIZE];/** 目前android系统只能发送16字节,发送数据1K */
		BluetoothGattCharacteristic writeCharacteristic = mCharateristicMap.get("WRITE_CH");
		BluetoothGattCharacteristic readCharacteristic = mCharateristicMap.get("READ_CH");

		/* process body */
		/** 设置压力次数 */
		bak = cnt = gui.JDK_ReadData(TIMEOUT_INPUT, 20);
		/** 测试前置,连接设备,mBluetoothGatt为空才需要重新连接设备 */
		startTime = System.currentTimeMillis();
		mBluetoothGatt = mBluetoothDevice.connectGatt(myactivity, false, mBluetoothCallBack);
		synchronized (g_lock) {
			try {
				g_lock.wait(WAITIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (Tools.getStopTime(startTime) > TIMEOUT) {
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:连接设备超时", Tools.getLineInfo(), bak - cnt);
			close();
			return;
		}
		while (cnt > 0) {
			if (gui.cls_show_msg1(2, "BLE读写压力测试中,已执行%d次,成功%d次,[取消]退出测试", bak - cnt, succ) == ESC)
				break;
			cnt--;

//			/**1.先读取设备信息*/
//			isReadStart = false;
//			startTime = System.currentTimeMillis();
//			mBluetoothGatt.readCharacteristic(readCharacteristic);
//			/**超时时间为10s*/
//			while(isReadStart==false&&Tools.getStopTime(startTime)<TIMEOUT)
//			{
//				SystemClock.sleep(100);
//			}
//			if(Tools.getStopTime(startTime)>TIMEOUT)
//			{
//				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:读设备数据超时", Tools.getLineInfo(),bak-cnt);
//				close();
//				continue;
//			}
//	        byte[] data = readCharacteristic.getValue();
//	        if (data != null && data.length > 0) 
//	        {
//	            gui.cls_show_msg1(1, "%s",ISOUtils.hexString(data));
//	        }
////	        else
////	        {
////	        	gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:读设备数据失败", Tools.getLineInfo(),bak-cnt);
////				continue;
////	        }

			/**
			 * 1.先判断该mGattCharacteristic是否支持写特征，若支持写特征写操作完毕之后再判断是否支持通知特征，若支持通知特征则从串口读数据
			 * 2.若不支持写特征则直接进行读特征操作
			 */
			isWriteStart = false;
			// 1.写1K数据到串口
			Arrays.fill(sBuf, (byte) 0x31);
			startTime = System.currentTimeMillis();
			writeCharacteristic.setValue(sBuf);
			mBluetoothGatt.writeCharacteristic(writeCharacteristic);
			/** 超时时间为10s */
			while (isWriteStart == false && Tools.getStopTime(startTime) < TIMEOUT) {
				SystemClock.sleep(100);
			}
			if (Tools.getStopTime(startTime) > TIMEOUT) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:写数据到设备超时", Tools.getLineInfo(),
						bak - cnt);
				close();
				continue;
			}

			// 2.从串口读取1K的数据,换成通知的Characteristic
			// Arrays.fill(rBuf,(byte)0x00);
			isNotifyStart = false;
			startTime = System.currentTimeMillis();
			// mBluetoothGatt.readCharacteristic(readCharacteristic);
			mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true);
			// mBluetoothCallBack.onCharacteristicRead(mBluetoothGatt, readCharacteristic,
			// 0);
			gui.cls_printf("请先把串口接收到的数据拷到发送区并发送".getBytes());
			/** 超时时间为10s */
			while (isNotifyStart == false && Tools.getStopTime(startTime) < TIMEOUT) {
				SystemClock.sleep(100);
			}
			if (Tools.getStopTime(startTime) > TIMEOUT) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:读数据到设备超时", Tools.getLineInfo(),
						bak - cnt);
				close();
				continue;
			}
			// Log.d("rBuf",rBuf[0] + " ");
			// 3.比较读写数据
			if (rBuf.length != sBuf.length || Tools.memcmp(rBuf, sBuf, sBuf.length) == false) {
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:第%d次:数据比较失败(%d,%s)", Tools.getLineInfo(),
						bak - cnt, rBuf.length, rBuf == null ? "null" : ISOUtils.hexString(rBuf));
				continue;
			}

			succ++;
		}
		// 测试后置,断开连接
		close();
		gui.cls_show_msg1_record(TAG, "bleReadPress", g_time_0, "BLE读写压力测试完成，已执行次数为%d,成功为%d次", bak - cnt, succ);
	}

	private void getBlueAdapter() {
		// 与标准蓝牙略有不同，标准蓝牙是直接new
		BluetoothManager bluetoothManager = (BluetoothManager) myactivity.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 隐式打开蓝牙
		if (!mBluetoothAdapter.isEnabled())
			mBluetoothAdapter.enable();

	}

	/** 蓝牙扫描操作 */
	public void startScan() {
		mScanner = mBluetoothAdapter.getBluetoothLeScanner();
		mScanner.startScan(scanCallback);
	}

	ScanCallback scanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			super.onScanResult(callbackType, result);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				BluetoothDevice device = result.getDevice();
				if (devices.contains(device) == false) {
					devices.add(device);
					Log.e("device", device.getAddress() + "===" + device.getName());
				}
				mDeviceAdapater.notifyDataSetChanged();
			}
		}

		@Override
		public void onScanFailed(int errorCode) {
			super.onScanFailed(errorCode);
			Log.d("eric_chen", "errorCode=="+errorCode);
			if (errorCode!=0) {
				BLEerrcode=errorCode;
			}
			Log.e("search", "search fail");
		}
	};

	// 读取信息
//	public void getCharactacteristic(BluetoothGattCharacteristic characteristic, boolean isRead) {
//		if (characteristic != null) {
//			if (isRead == true) {
//				mBluetoothGatt.readCharacteristic(characteristic);
//			} else {
//				mBluetoothGatt.writeCharacteristic(characteristic);
//			}
//			mBluetoothGatt.setCharacteristicNotification(characteristic, true);
//		}
//	}

	// b.获取服务和获取特征
//	private BluetoothGattCharacteristic getCharcteristic(UUID characteristicUUID) {
//		BluetoothGattService service = mBluetoothGatt.getService(mGattDeivceService.getUuid());
//		if (service == null) {
//			Log.e(TAG, "can not find BluetoothGattService");
//			return null;
//		}
//		// 得到此服务节点下Characteristic对象
//		BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(characteristicUUID);
//		if (gattCharacteristic != null) {
//			return gattCharacteristic;
//		} else {
//			Log.e(TAG, "can not find BluetoothGattCharacteristic");
//			return null;
//		}
//	}

	public void close() {
		if (mBluetoothGatt == null)
			return;
		LoggerUtil.e("close");
		mBluetoothGatt.close();
		mBluetoothGatt.disconnect();
		mBluetoothGatt = null;
	}

	/** 显示蓝牙列表 */
	public Dialog showBleScan(Context context) {
		isServiceCom = false;
		ListView listView = new ListView(context);
		listView.setAdapter(mDeviceAdapater);
		final BaseDialog dialog = new BaseDialog(BaseFragment.myactivity, listView, "蓝牙显示列表");
		dialog.show();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/** 选中BLE的服务之后要停止扫描 */
				// mCharateristicMap = new HashMap<String, BluetoothGattCharacteristic>();
				boolean writeReady = false;
				boolean readReady = false;
				mBluetoothDevice = devices.get(position);
				blutoothName = mBluetoothDevice.getName();
				mScanner.stopScan(scanCallback);
				dialog.dismiss();

				mBluetoothGatt = mBluetoothDevice.connectGatt(myactivity, false, mBluetoothCallBack);
				long startTime = System.currentTimeMillis();
				while(isServiceCom==false&&Tools.getStopTime(startTime)<10)
				{
					SystemClock.sleep(100);
				}
				
				// mGattServices = mBluetoothGatt.getServices();
				if(mGattServices != null) {
					LoggerUtil.v("showBleScan->PROPERTY_WRITE and PROPERTY_NOTIFY");
					for (BluetoothGattService mGattDeivceService : mGattServices) {
						for (BluetoothGattCharacteristic mCharacteristic : mGattDeivceService.getCharacteristics()) {
							if (mCharacteristic.getProperties() == (BluetoothGattCharacteristic.PROPERTY_WRITE +
									BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
								mCharateristicMap.put("WRITE_CH", mCharacteristic);
								writeReady = true;
							}
							if (mCharacteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
								mCharateristicMap.put("READ_CH", mCharacteristic);
								readReady = true;
							}

						}
					}
				}
				
				if (readReady == true && writeReady == true)
					gui.cls_printf(("蓝牙："+ blutoothName +"读写特征配置完毕").getBytes());	
				if(readReady == false || writeReady == false) 
					gui.cls_printf(("蓝牙："+ blutoothName + "不支持读写特征请重新选择蓝牙").getBytes());
					
				writeReady = false;
				readReady = false;
				synchronized (g_lock) {
					g_lock.notify();
				}
			}

		});
		dialog.setOnKeyListener(keylistener);
		return dialog;
	}

	/** 显示Service和Charateristic的UUID列表 */
//	public void setServiceUUID(final Context context, final List<BluetoothGattService> gattServices) {
//		LoggerUtil.i("setServiceUUID");
//		if (gattServices == null)
//			return;
//
//		ListView listView = new ListView(context);
//		listView.setBackgroundColor(Color.WHITE);
//		BleAdapter<BluetoothGattService> bleAdapter = new BleAdapter<BluetoothGattService>(context, gattServices, true);
//		listView.setAdapter(bleAdapter);
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setView(listView);
//		builder.setTitle("BLE服务选择，请选择第四个");
//		final AlertDialog dialog = builder.show();
//
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//				LoggerUtil.d("setServiceUUID position:" + position);
//				dialog.dismiss();
//				myactivity.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						mGattDeivceService = mGattServices.get(position);
//						setCharaUUID(myactivity, mGattServices, position);
//					}
//				});
//			}
//		});
//
//	}
//
//	private void setCharaUUID(final Context context, final List<BluetoothGattService> gattServices, int f_position) {
//		LoggerUtil.d("position:" + f_position);
//		List<BluetoothGattCharacteristic> gattCharacteristics = gattServices.get(f_position).getCharacteristics();
//		LoggerUtil.i("setCharaUUID");
//		if (gattServices == null)
//			return;
//
//		ListView listView = new ListView(context);
//		listView.setBackgroundColor(Color.WHITE);
//		BleAdapter<BluetoothGattCharacteristic> bleAdapter = new BleAdapter<BluetoothGattCharacteristic>(context,
//				gattCharacteristics, false);
//		listView.setAdapter(bleAdapter);
//		new BaseDialog(context, listView, "特征选择,读选3，写选4", "确定", new OnDialogButtonClickListener() {
//
//			@Override
//			public void onDialogButtonClick(View view, boolean isPositive) {
//				synchronized (g_lock) {
//					g_lock.notify();
//				}
//			}
//		}).show();
//	}

	// 对back键和home键进行监听
	static OnKeyListener keylistener = new DialogInterface.OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_HOME) {
				Log.e("dialoghome", "home");
				return true;
			} else {
				return false;
			}
		}
	};

//	class BleAdapter<E> extends BaseAdapter {
//		private List<E> mListServices;
//		private Context mContext;
//		private boolean mIsService;
//
//		private CompoundButton tempReadButton;
//		private CompoundButton tempWriteButton;
//
//		public BleAdapter(Context context, List<E> listServices, boolean isService) {
//			this.mContext = context;
//			this.mListServices = listServices;
//			mIsService = isService;
//		}
//
//		@Override
//		public int getCount() {
//			return mListServices.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			ViewHolder viewHolder = null;
//			if (convertView == null) {
//				viewHolder = new ViewHolder();
//				LayoutInflater inflater = LayoutInflater.from(mContext);
//				convertView = inflater.inflate(R.layout.layout_wifi_list_item, null);
//				viewHolder.wifiName = (TextView) convertView.findViewById(R.id.tv_ssid);
//				viewHolder.wifiProtected = (TextView) convertView.findViewById(R.id.tv_sec_mod);
//				viewHolder.linLay = (LinearLayout) convertView.findViewById(R.id.lay_rb);
//				viewHolder.rb_read = (RadioButton) convertView.findViewById(R.id.rb_read);
//				viewHolder.rb_write = (RadioButton) convertView.findViewById(R.id.rb_write);
//				convertView.setTag(viewHolder);
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//			// 修改设置的字体大小
//			viewHolder.wifiName.setTextSize(15);
//			viewHolder.wifiProtected.setTextSize(15);
//			if (mIsService == true) {
//				viewHolder.linLay.setVisibility(View.GONE);
//				BluetoothGattService gattService = (BluetoothGattService) mListServices.get(position);
//				String uuid = gattService.getUuid().toString();
//				String unknownServiceString = getResources().getString(R.string.unknown_service);
//				viewHolder.wifiName.setText(SampleGattAttributes.lookup(uuid, unknownServiceString));
//				viewHolder.wifiProtected.setText(uuid);
//			} else {
//				viewHolder.linLay.setVisibility(View.VISIBLE);
//				final BluetoothGattCharacteristic gattCharacter = (BluetoothGattCharacteristic) mListServices
//						.get(position);
//				String uuid = gattCharacter.getUuid().toString();
//				String unknownCharacter = getResources().getString(R.string.unknown_characteristic);
//				viewHolder.wifiName.setText(SampleGattAttributes.lookup(uuid, unknownCharacter));
//				viewHolder.wifiProtected.setText(uuid);
//				// 读写特征只能有一个
//				viewHolder.rb_read.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//						if (isChecked) {
//							if (tempReadButton != null) {
//								tempReadButton.setChecked(false);
//							}
//							tempReadButton = buttonView;
//							if(gattCharacter.getProperties() != gattCharacter.PERMISSION_WRITE ||
//									gattCharacter.getProperties() != gattCharacter.PROPERTY_WRITE_NO_RESPONSE ) {
//								new AlertDialog.Builder(myactivity)
//								.setMessage("不支持写特征请重新选择")
//								.setPositiveButton("确定" ,  null ) 
//								.show();
//							}else
//							mCharateristicMap.put("READ_CH", getCharcteristic(gattCharacter.getUuid()));
//							Log.d("read", gattCharacter.getProperties() + " ");
//						}
//					}
//				});
//
//				viewHolder.rb_write.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//						if (isChecked) {
//							if (tempWriteButton != null) {
//								tempWriteButton.setChecked(false);
//							}
//							tempWriteButton = buttonView;
//							mCharateristicMap.put("WRITE_CH", getCharcteristic(gattCharacter.getUuid()));
//							Log.d("write", gattCharacter.getProperties() + " ");
//						}
//					}
//				});
//
//			}
//			return convertView;
//		}
//
//		class ViewHolder {
//			TextView wifiName;
//			TextView wifiProtected;
//			LinearLayout linLay;
//			RadioButton rb_read;
//			RadioButton rb_write;
//		}

	}

