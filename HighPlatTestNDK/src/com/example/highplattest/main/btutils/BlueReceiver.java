package com.example.highplattest.main.btutils;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 取消蓝牙配对的对话框
 * @author 56484
 *
 */
public class BlueReceiver extends BroadcastReceiver{
	String strPsw = "0";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		if (intent.getAction().equals(
				"android.bluetooth.device.action.PAIRING_REQUEST"))
		{
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			// byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
			// device.setPin(pinBytes);
			Log.i("tag11111", "ddd");
			try
			{
				ClsUtils.setPin(btDevice.getClass(), btDevice, strPsw); // �ֻ�������ɼ������
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
