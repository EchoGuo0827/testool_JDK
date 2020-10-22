package com.example.highplattest.main.bean;

import java.util.List;

/**
 * 用于保存模块以及对应的用例号
 * @author zhengxq
 * 2018年2月27日 下午4:52:30
 */
public class GroupBean {
	private String moduleName;/**模块名*/
	
	private String clsName;/**要运行的类名*/
	
	private List<String> caseNums;/**本模块支持的用例：因为存在用例虽然存在但是本固件该用例被屏蔽掉的情况，所以要增加移除用例的功能*/
	
//	private List<String> caseDetail = new ArrayList<String>();/**本模块支持的用例说明*/
	private boolean[] ckStatus;/**CheckBox的选中状态*/
	
	public void setModuleName(String name)
	{
		moduleName = name;
		int index_start = name.indexOf("(");
		int index_end = name.indexOf(")");
		clsName = name.substring(index_start+1, index_end);
	}
	
	public String getModuleName()
	{
		return moduleName;
	}
	
	public void setCaseNums(List<String> caseNumbers)
	{
		caseNums = caseNumbers;
		ckStatus = new boolean[caseNumbers.size()];
	}
	
	public List<String> getCaseNums()
	{
		return caseNums;
	}
	
	/*public void setCkInit(int size)
	{
		ckStatus = new boolean[size];
	}*/
	
	public void setCkStatus(int position,boolean status)
	{
		ckStatus[position] = status;
	}
	
	public boolean getCkStatus(int position)
	{
		return ckStatus[position];
	}
	
	public String getRunName()
	{
		// toLowerCase用于将字符串转化为小写
		return "com.example.highplattest."+clsName.toLowerCase()+"."+clsName;
	}
	
	/*// 描述用例详细作用的字符串
	public void setCaseDetail(int positon,String testim)
	{
		caseDetail.add(caseNums.get(positon)+"、"+testim);
	}
	
	public String getCaseDetail(int position)
	{
		return caseDetail.get(position);
	}
	
	public int childCount()
	{
		return caseDetail.size();
	}*/
}
