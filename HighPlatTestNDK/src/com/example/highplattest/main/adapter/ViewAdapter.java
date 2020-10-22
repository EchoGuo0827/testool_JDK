package com.example.highplattest.main.adapter;

import java.util.List;

import com.example.highplattest.R;
import com.example.highplattest.main.tools.LoggerUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewAdapter extends CommonAdapter{
	protected List<String> data;
	private LayoutInflater mInflater;
	public ViewAdapter(List<String> list,Context context) {
		super(list);
		this.data=list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getHolder(convertView, mInflater, position,R.layout.list_tv_layout);
		TextView tvName = holder.getView(R.id.tv_list_item);
		tvName.setText(data.get(position));
		return holder.getConvertView();
	}

}
