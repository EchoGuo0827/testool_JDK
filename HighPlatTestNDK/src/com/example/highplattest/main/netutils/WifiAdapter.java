package com.example.highplattest.main.netutils;

import java.util.List;
import com.example.highplattest.R;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * wifi适配器
 * @author zhengxq
 * 2016-4-6 下午4:52:53
 */
public class WifiAdapter extends BaseAdapter
{
	private List<ScanResult> mListScan;
	private Context mContext;

	public WifiAdapter(Context context,List<ScanResult> listScan)
	{
		this.mContext = context;
		this.mListScan = listScan;
	}
	
	@Override
	public int getCount() 
	{
		return mListScan.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolderk viewHolder = null;
		if(convertView == null)
		{
			viewHolder = new ViewHolderk();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.layout_wifi_list_item, null);
			viewHolder.wifiName = (TextView) convertView.findViewById(R.id.tv_ssid);
			viewHolder.wifiProtected = (TextView) convertView.findViewById(R.id.tv_sec_mod);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolderk) convertView.getTag();
		}
		viewHolder.wifiName.setText(mListScan.get(position).SSID);
		viewHolder.wifiProtected.setText(mListScan.get(position).capabilities);
		return convertView;
	}
	
	class ViewHolderk
	{
		TextView wifiName;
		TextView wifiProtected;
	}

}
