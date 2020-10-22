package com.example.highplattest.scan;

import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan13.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160722
 * directory 		: 读取芯片SN
 * description 		: getThk88ID(byte[] SnBuff)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20160722    	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan13 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil)getThk88ID(byte[] SnBuff)";
	private ScanUtil softManager1;
	private ScanUtil softManager2;
	private String fileName=Scan13.class.getSimpleName();
	Gui gui = new Gui(myactivity, handler);
	@SuppressLint("NewApi")
	public void scan13() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		/*private & local definition*/
		int ret = -1;
		softManager1 = new ScanUtil(myactivity);
		softManager2 = new ScanUtil(myactivity, null, 0, true, 10*1000, 1);
		byte[] sn0 = new byte[0];
		byte[] sn16 = new byte[16];
		byte[] sn33 = new byte[33];
		byte[] sn32_1 = new byte[32];
		byte[] sn32_2 = new byte[32];
		/*process body*/
		gui.cls_show_msg1(1, "sn=null异常测试");
		// case1.1：硬解码实例化，参数异常测试，sn = null，应返回NLS_ERR_PARAM
		if((ret = softManager1.getThk88ID(null))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case1.2:软解码实例化，参数异常测试，sn = null，应返回NLS_ERR_PARAM          
		if((ret = softManager2.getThk88ID(null))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "sn = \"\"异常测试");
		// case2.1：硬解码实例化，参数异常测试，sn = "",应返回NLS_ERR_PARAM
		if((ret = softManager1.getThk88ID("".getBytes()))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.2：软解码实例化，参数异常测试，sn = "",应返回NLS_ERR_PARAM
		if((ret = softManager2.getThk88ID("".getBytes()))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%s参数异常测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "sn长度=0异常测试");
		// case3.1：硬解码实例化，参数异常测试，sn长度=0,应返回NLS_ERR_PARAM
		if((ret = softManager1.getThk88ID(sn0))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=0测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3.2：软解码实例化，参数异常测试，sn长度= 0,应返回NLS_ERR_PARAM
		if((ret = softManager2.getThk88ID(sn0))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=0测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "sn长度=16异常测试");
		// case4.1：硬解码实例化，参数异常测试，sn长度=16,应返回NLS_ERR_PARAM
		if((ret = softManager1.getThk88ID(sn16))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=16测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case4.2：软解码实例化，参数异常测试，sn长度= 16,应返回NLS_ERR_PARAM
		if((ret = softManager2.getThk88ID(sn16))!=ScanUtil.NLS_ERR_PARAM)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=16测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "sn长度=33异常测试");
		// case5.1：硬解码实例化，参数异常测试，sn长度=33,应返回NLS_SUCCESS
		if((ret = softManager1.getThk88ID(sn33))!=ScanUtil.NLS_SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=33测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5.2：软解码实例化，参数异常测试，sn长度= 33,应返回NLS_SUCCESS
		if((ret = softManager2.getThk88ID(sn33))!=ScanUtil.NLS_SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=33测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1(1, "sn长度=32正常测试");
		// case6.1:硬解码实例化，获取到SN号，并且两种方式获取的SN号应一致
		if((ret = softManager1.getThk88ID(sn32_1))!=ScanUtil.NLS_SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=32测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case6.2:硬解码实例化，获取到SN号，并且两种方式获取的SN号应一致
		if((ret = softManager2.getThk88ID(sn32_2))!=ScanUtil.NLS_SUCCESS)
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%ssn长度=32测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if(!Tools.memcmp(sn32_1, sn32_2, sn32_1.length))
		{
			gui.cls_show_msg1_record(fileName, "scan13", gKeepTimeErr,"line %d:%s两次sn值获取不一致", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "scan13", gScreenTime, "%s测试通过", TESTITEM);
	}
	


	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(softManager1!=null)
			softManager1.release();
		if(softManager2!=null)
			softManager2.release();
		gui = null;
		
	}
}
