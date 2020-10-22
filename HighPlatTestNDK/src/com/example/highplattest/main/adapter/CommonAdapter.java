package com.example.highplattest.main.adapter;

import java.util.List;

import android.widget.BaseAdapter;

public abstract class CommonAdapter extends BaseAdapter{
	 protected List<?> data;
	    public CommonAdapter(List<?> data){
	        this.data = data;
	    }
	    @Override
	    public int getCount() {
	        return data == null ? 0 : data.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return data.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

}
