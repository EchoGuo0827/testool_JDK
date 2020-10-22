package com.example.highplattest.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;

import com.example.highplattest.main.tools.LoggerUtil;

public class FragmentCollector 
{
	public static List<String> fragments = new ArrayList<String>();
	public static List<Fragment> fragmentList = new ArrayList<Fragment>();
	public static HashMap<String, List<String>> mAutoCase = new HashMap<String, List<String>>();
	public static List<String> mModuleNames = new ArrayList<String>();
	
	//final List<String> test_name = FragmentCollector.fragments;
	public static List<String> real_test_name = new ArrayList<String>();
	public static List<Class<? extends Fragment>> listTestClass = new ArrayList<Class<? extends Fragment>>();
	
	public static void addFragmentSingle(String path,String singleName)
	{
		fragments.add(path+singleName);
	}
	
	public static void addFragmentSingleName(String name)
	{
		fragments.add(name);
	}
	
	/**
	 * 这个添加的是具体需要运行的子用例
	 */
	public static void addFragmentName(String path,List<String> name)
	{
		for (int i = 0; i < name.size(); i++) 
		{
			fragments.add(path+name.get(i));
			LoggerUtil.e(path+name.get(i));
		}
	}
	
	public static void removeFragmentName(int i)
	{
		fragments.remove(i);
	}
	
	public static void finishAll()
	{
		fragments.removeAll(fragments);
	}
	
	/**
	 * 保存每个模块需要执行的用例号和模块名
	 * @param moduleName  模块名
	 * @param caseNum	  模块用例号
	 */
	public static void addAutoModule(String moduleName)
	{
		// 保存模块名
		mModuleNames.add(moduleName);
	}
	
	public static void addTestData(){
		listTestClass.clear();
		real_test_name.clear();
		fragmentList.clear();
		// 实例化测试用例
 		// 将testName有问题的用例号移除
 		for (int i = 0; i < fragments.size(); i++) 
 		{
 			try {
 				listTestClass.add((Class<? extends Fragment>) Class.forName(fragments.get(i)));
 				real_test_name.add(fragments.get(i));
 			} catch (ClassNotFoundException e) 
 			{
 				e.printStackTrace();
 			} 
 		}
 		// 为了解决用例不存在的问题，要与forName分开
 		for (int i = 0; i < listTestClass.size(); i++) 
 		{
 			try 
 			{
 				Fragment newInstance = listTestClass.get(i).newInstance();
 				fragmentList.add(newInstance);
 			} catch (InstantiationException e) {
 				e.printStackTrace();
 			} catch (IllegalAccessException e) {
 				e.printStackTrace();
 			}
 		}
	}
}
