package com.example.highplattest.main.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.highplattest.R;
import com.example.highplattest.main.bean.CheckBean;

public class CheckAdapter extends BaseAdapter
{
	private List<CheckBean> mModule;
	private Context mContext;
	
	public CheckAdapter(List<CheckBean> modules,Context context) 
	{
		this.mModule = modules;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		if(mModule==null)
		{
			return 0;
		}
		return mModule.size();
	}

	@Override
	public Object getItem(int position) {
		return mModule.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHold holder=null;
		if (null == convertView) {
			holder=new ViewHold();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.aysnc_item, parent,false);
			holder.cb=(CheckBox) convertView.findViewById(R.id.cb_aysnc_task);
			// 为view设置标签
            convertView.setTag(holder);
		}else{
			 // 取出holder
            holder = (ViewHold) convertView.getTag();
		}
		holder.cb.setText((CharSequence) mModule.get(position).getItem1());
		holder.cb.setChecked(mModule.get(position).isChecked());
		return convertView;
	}
	
}
class ViewHold {
	CheckBox cb;
}
