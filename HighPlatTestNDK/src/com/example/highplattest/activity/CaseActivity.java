package com.example.highplattest.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.main.*;
import com.example.highplattest.main.adapter.ListDefineAdapter;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;

@SuppressWarnings("deprecation")
public class CaseActivity extends Activity implements OnClickListener,OnItemClickListener,NDK,DefineListener.BackListener
{
	private ListView listItem;
	// 每个模块有自己的测试用例个数
	private String TESTAPI;
	private String mClsName;
	private List<String> mListName = new ArrayList<String>();
	private Map<Integer, Boolean> mCheckMap = new HashMap<Integer, Boolean>();
	private Button btnRun;
	private List<String> testNumList = new ArrayList<String>();
	private DefineListener.BackListener mTestConfigListener;
	// fragment的跳转
	private FragmentManager fm;
	private FragmentTransaction ft;
	
	// 菜单切换
	// 从activity切花到fragment
	public void switchFragment(final int viewContainer, final Fragment newFragment) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				fm = getFragmentManager();
				ft = fm.beginTransaction();
				ft.replace(viewContainer, newFragment);
				ft.commit();
			}
		}).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		TESTAPI = intent.getStringExtra("TESTAPI");// 从MainActivity通过Intent获取类名
		// 传递具体的配置文件到CaseActivity
		String moduleConfig = intent.getStringExtra("moduleConfig");
		
		setContentView(R.layout.main_show_activity);
		// 绑定View
		initView();
		// 初始化监听
		setListener();
		GlobalVariable.gAutoFlag = AutoFlag.HandFull;

		
		// 读取模块的用例号
		try 
		{
			String all = XmlResourceParserTool.getModuleContent(getAssets().open(moduleConfig+"_module_list.xml"), TESTAPI, "all");
			String[] testNum = all.split(",");
			for(String i:testNum)
			{
				testNumList.add(i);
			}
			FragmentCollector.finishAll();
		} catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		// 全自动操作
		switch (GlobalVariable.gAutoFlag) // 全自动不会走到这个Activity
		{
		case HandFull:
		case AutoHand:
			int index_start = TESTAPI.indexOf("(");
			int index_end = TESTAPI.indexOf(")");
			String clsName = TESTAPI.substring(index_start+1, index_end);
			mClsName = "com.example.highplattest."+clsName.toLowerCase()+"."+clsName;
			// 手自动或手动
			for (int i = 0; i < testNumList.size(); i++) 
			{
				// 这边要获取到用例的包名+反射获取字段名
				String workerClassName = mClsName + testNumList.get(i);
				Class<?> workerClass;
				try {
					workerClass = Class.forName(workerClassName);
					Field mStudentField;
					mStudentField = workerClass.getDeclaredField("TESTITEM");
					mStudentField.setAccessible(true);
					mListName.add(testNumList.get(i) + "、"+ mStudentField.get(workerClass.newInstance()));
				} catch (Exception e) {
					Toast.makeText(CaseActivity.this,"找不到" + testNumList.get(i) + "号用例",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 初始化View
	 */
	public void initView()
	{
		listItem = (ListView) findViewById(R.id.lv_frame);
		btnRun = (Button) findViewById(R.id.btn_run);
	}
	
	/**
	 * 设置监听
	 */
	public void setListener()
	{
		listItem.setAdapter(new ListDefineAdapter(this,mCheckMap, mListName));
		listItem.setOnItemClickListener(this);
		btnRun.setOnClickListener(this);
		setBackListener(this);
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		// 某些用例
		case R.id.btn_run:
			GlobalVariable.gAutoFlag = AutoFlag.MulAuto;
			// 清空及添加
			FragmentCollector.finishAll();
			
			for(int position=0;position<mListName.size();position++)
			{
				if(mCheckMap.get(position)==null)
					continue;
				else if(mCheckMap.get(position)==true)
				{
					FragmentCollector.addFragmentSingle(mClsName, mListName.get(position).split("、")[0]);
				}
			}
			// 根据checkBox的勾选进行用例运行
			if(FragmentCollector.fragments.size()>0)
			{
				intentCase();
			}
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
	{
		// 跳到相应用例的方法
		FragmentCollector.finishAll();
		FragmentCollector.addFragmentSingle(mClsName, mListName.get(position).split("、")[0]);
		
		intentCase();
	}
	
	/**
	 * 跳转到intentActivity，再从intentActivity跳到fragment
	 */
	public void intentCase()
	{
		// 跳转到intentActivity
		Intent intent = new Intent(this, IntentActivity.class);
		startActivity(intent);
	}
	
	public DefineListener.BackListener getBackListener() {  
        return mTestConfigListener;  
    }  
  
    public void setBackListener(DefineListener.BackListener testConfigListener) {  
        this.mTestConfigListener = testConfigListener;  
    }  
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// 返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			if(mTestConfigListener!=null)
				mTestConfigListener.onBackDown();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		this.finish();
		FragmentCollector.finishAll();
	}

	@Override
	public void onBackDown() 
	{
		new BaseDialog(this, "测试中断", "真的要退出测试吗？", "是", "否", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					CaseActivity.this.finish();
				}
				
			}
		}).show();
	}

	@Override
	protected void onPause() {
		//将当前Activity推入栈中
		 ActivityManager.getActivityManager().pushActivity(this);
		super.onPause();
	}
}
