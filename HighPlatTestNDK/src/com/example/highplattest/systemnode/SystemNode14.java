package com.example.highplattest.systemnode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * file name 		: SystemNode1.java 
 * description 		: fuse标识获取
 * history 		 	: 变更点								变更时间			变更人员
 * 					 读取RSA密钥内容(N910_欧洲)	      	    20200821			陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode14 extends UnitFragment {
	private final String TESTITEM = "读取RSA密钥内容";
	private Gui gui = null;
	private String fileName="SystemNode14";
	String RSAPATH="/newland/factory/Add_info.txt";
	
	public void systemnode14()
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gCustomerID==CUSTOMER_ID.overseas)
		{
			gui.cls_show_msg("读取RSA密钥内容需要mtms权限,签名的apk位于/SVN/Tool/部分案例APK/HighPlatTestNDK_mtms_N910Europe.apk,安装测试即可");
		}
		else
		{
			gui.cls_show_msg("非欧洲固件,不支持该案例");
		}
		
		/*gui.cls_show_msg1(2,"%s测试中",TESTITEM);
		gui.cls_show_msg("读取RSA密钥内容需要mtms权限。若当前应用未申请mtms权限则无法读取。若申请了mtms权限则可以读取。按任意键继续");
		String txtinfo="null";
		
		txtinfo=BaseFragment.getNodeFile(RSAPATH,"-1");
		gui.cls_show_msg("当前读取到的内容为：%s",txtinfo);*/
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
