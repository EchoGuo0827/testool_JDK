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
 * file name 		: Scan12.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160722
 * directory 		: 设置芯片上下电状态
 * description 		: setThk88Power(int nMode)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20160722    	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan12 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil)setThk88Power";
	private ScanUtil softManager1;
	private ScanUtil softManager2;
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Scan12.class.getSimpleName();
	@SuppressLint("NewApi")
	public void scan12() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		/*private & local definition*/
		try 
		{
			softManager1 = new ScanUtil(myactivity);
			softManager2 = new ScanUtil(myactivity, null, 0, true, 10*1000, 1);
			
			int ret;
			/*process body*/
			gui.cls_show_msg1(1, "setThk88Power(-1)异常测试");
			// case1.1：硬解码实例化方式，参数设置为-1，应返回NLS_ERR_PARAM
			if((ret = softManager1.setThk88Power(-1))!=ScanUtil.NLS_ERR_PARAM)
			{
				gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"line %d:%s参数异常测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case1.2:软解码实例化方式，参数设置为-1，应返回NLS_ERR_PARAM
			if((ret = softManager2.setThk88Power(-1))!=ScanUtil.NLS_ERR_PARAM)
			{
				gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"line %d:%s参数异常测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg1(1, "setThk88Power(1)上电测试");
			// case2.1：硬解码实例化方式，参数设置为1为上电，应返回ScanUtil.NLS_SUCCESS
			if((ret = softManager1.setThk88Power(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"line %d:%s芯片上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case2.2：软解码实例化方式，参数设置为1为上电，应返回ScanUtil.NLS_SUCCESS
			if((ret = softManager2.setThk88Power(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"line %d:%s芯片上电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg1(1, "setThk88Power(0)下电测试");
			// case3.1:硬解码实例化方式，参数设置为0为下电，应返回ScanUtil.NLS_SUCCESS
			if((ret = softManager1.setThk88Power(0))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"line %d:%s芯片下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case3.2:软解码实例化方式，参数设置为0为下电，应返回ScanUtil.NLS_SUCCESS
			if((ret = softManager2.setThk88Power(0))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"line %d:%s芯片下电失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// 芯片上下电会清除设备认证状态，需要休眠唤醒或重启后才可使用扫码
			gui.cls_show_msg1_record(fileName, "scan12", gScreenTime,"%s测试通过，认证状态被清除，请休眠唤醒后或重启后再使用扫码(NLS)",TESTITEM);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan12", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		if(softManager1!=null)
			softManager1.release();
		if(softManager2!=null)
			softManager2.release();
		gui = null;
		softManager1 = null;
		softManager2 = null;
		
	}
}
