package com.example.highplattest.main.adapter;

import java.util.List;
import java.util.Map;
import com.example.highplattest.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
/**
 * 
 * @author zhengxq
 * 2018年3月6日 下午2:20:54 用于CaseActivity的ListView的Adapter
 */
public class ListDefineAdapter extends BaseAdapter{
	
	private Context mContext;
	private Map<Integer, Boolean> mCheckMap;
	private List<String> mListContent;
	
	public ListDefineAdapter(Context context,Map<Integer,Boolean> checkMap,List<String> listContent)
	{
		mContext = context;
		mCheckMap = checkMap;
		mListContent = listContent;
	}

	@Override
	public int getCount() {
		return mListContent.size();
	}

	@Override
	public Object getItem(int position) {
		return mListContent.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		ChildHolder childHodler = null;
		if(convertView!=null)
		{
			view = convertView;
			childHodler = (ChildHolder)view.getTag();
		}
		else
		{
			view = View.inflate(mContext, R.layout.expand_child, null);
			childHodler = new ChildHolder();
			childHodler.mCheckBox = (CheckBox) view.findViewById(R.id.ck_child);
			childHodler.mTvContent = (TextView) view.findViewById(R.id.tv_child);
			view.setTag(childHodler);
		}
		childHodler.mTvContent.setText(mListContent.get(position));
		childHodler.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				mCheckMap.put(position, isChecked);
			}
		});
		if(mCheckMap.get(position)==null)
		{
			childHodler.mCheckBox.setChecked(false);
		}
		else
		{
			childHodler.mCheckBox.setChecked(mCheckMap.get(position));
		}
		return view;
	}

	class ChildHolder{
		CheckBox mCheckBox;
		TextView mTvContent;
	}
}
