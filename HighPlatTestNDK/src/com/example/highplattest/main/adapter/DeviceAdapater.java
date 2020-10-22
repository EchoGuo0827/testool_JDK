package com.example.highplattest.main.adapter;

import java.util.ArrayList;

import com.example.highplattest.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapater extends BaseAdapter
{
	ArrayList<BluetoothDevice>  mDevices;
	Context mContext;
	
	public DeviceAdapater(Context context,ArrayList<BluetoothDevice> devices)
	{
		mDevices = devices;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return mDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHoldNew holder=null;
		if (null == convertView) {
			holder=new ViewHoldNew();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.device_item, parent,false);
			holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_addr=(TextView) convertView.findViewById(R.id.tv_uuid);
			// 为view设置标签
            convertView.setTag(holder);
		}else{
			 // 取出holder
            holder = (ViewHoldNew) convertView.getTag();
		}
		if(mDevices.get(position).getName()==null)
		{
			holder.tv_name.setText("unkown device");
		}
		else
			holder.tv_name.setText(mDevices.get(position).getName());
		holder.tv_addr.setText(mDevices.get(position).getAddress());
		return convertView;
	}
}

class ViewHoldNew {
	TextView tv_name;
	TextView tv_addr;
}
