package com.example.highplattest.main.btutils;

import java.util.ArrayList;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.highplattest.R;
import com.example.highplattest.main.adapter.CommonAdapter;
import com.example.highplattest.main.adapter.ViewHolder;

public class ClientAdapter extends CommonAdapter {

	private ArrayList<BluetoothDevice> list;
	private LayoutInflater mInflater;

	public ClientAdapter(ArrayList<BluetoothDevice> messages,Context context) {
		super(messages);
		this.list = messages;
		this.mInflater = LayoutInflater.from(context);


	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = ViewHolder.getHolder(convertView, mInflater, position,R.layout.bt_list_item);
		TextView tvName = holder.getView(R.id.chat_msg);
		// 如果蓝牙名字为null就显示mac地址，有值存在就显示蓝牙名字
		if(list.get(position).getName()==null)
		{
			tvName.setText(list.get(position).getAddress());
		}
		else
		{
			tvName.setText(list.get(position).getName());
		}
		
		return holder.getConvertView();
	}

}