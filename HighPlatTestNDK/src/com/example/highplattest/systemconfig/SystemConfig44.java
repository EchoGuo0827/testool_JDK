package com.example.highplattest.systemconfig;

import android.annotation.SuppressLint;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlCashBoxManager;
import android.newland.content.NlContext;
/************************************************************************
 * 
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig44.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150505
 * directory 		: 设置钱箱工作电压，延迟
 * description 		: 测试系统设置函数OpenCashBox
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 huangjianb		   20150505 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemConfig44 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private String fileName="SystemConfig44";
	NlCashBoxManager nlCashBoxManager;
	String TESTITEM = "OpenCashBox";
	private Gui gui = null;
	
	public void systemconfig44() 
	{
		String funcName = "systemconfig44";
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.CashBox))
		{
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
				return;
			}
			
			/*private & local definition*/
			int ret;
			long lgt;
			nlCashBoxManager = (NlCashBoxManager) myactivity.getSystemService(NlContext.CASHBOX_SERVICE);
			
			/*process body*/
			gui.cls_show_msg1(gScreenTime, TESTITEM + "测试中...");
			
			//提示信息
			if(gui.ShowMessageBox("请将钱箱连接到LINE口，点击是继续测试".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s测试失败，请外接好钱箱继续测试", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case1:获取默认的钱箱工作电压与延迟值
			if((ret = nlCashBoxManager.getVoltage()) != 0)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s获取到的钱箱工作电压与预期不符（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue);
					return;
			}
			if((lgt = nlCashBoxManager.getTimeSec()) != 500)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s获取到的当前的延时与预期不符"+lgt, Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case2:OpenCashBox()函数测试
			//设置默认的钱箱工作电压与延迟值
			if((ret = nlCashBoxManager.OpenCashBox()) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			//查看钱箱是否正常打开 
			if(gui.ShowMessageBox("钱箱是否正常打开？若正常请关闭钱箱后点击是".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数测试失败，未能正常打开钱箱（%d）", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if((ret = nlCashBoxManager.OpenCashBox()) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case3:OpenCashBox(int voltage)函数测试
			//自定义钱箱工作电压为24v
			if((ret = nlCashBoxManager.OpenCashBox(1)) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//查看钱箱是否正常打开 
			if(gui.ShowMessageBox("钱箱是否正常打开？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数测试失败，未能正常打开钱箱（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//自定义钱箱工作电压为12v
			if((ret = nlCashBoxManager.OpenCashBox(0)) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//查看钱箱是否正常打开 
			if(gui.ShowMessageBox("钱箱是否正常打开？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数测试失败，未能正常打开钱箱。（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//正常参数为 0、1设置为其他值时预期返回-2（表示参数设置不合法）
			if((ret = nlCashBoxManager.OpenCashBox(2)) != -2)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case4:OpenCashBox(int voltage, long timesec)函数测试
			//钱箱工作电压为24v,延迟200
			if((ret = nlCashBoxManager.OpenCashBox(1,200)) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//查看钱箱是否正常打开 
			if(gui.ShowMessageBox("钱箱是否正常打开？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数测试失败，未能正常打开钱箱（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//钱箱工作电压为12v,延迟1000
			if((ret = nlCashBoxManager.OpenCashBox(0,1000)) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//查看钱箱是否正常打开 
			if(gui.ShowMessageBox("钱箱是否正常打开？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数测试失败，未能正常打开钱箱（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//钱箱工作电压为非法,延迟500,预期返回-2（表示参数设置不合法）
			if((ret = nlCashBoxManager.OpenCashBox(5,500)) != -2)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
//			//钱箱工作电压为非法,延迟500,预期返回-2 实际返回1
//			if((ret = nlCashBoxManager.OpenCashBox(-1,500)) != -2)
//			{
//				assertEqualsSub("line " + getLineInfo() + ":" + TESTAPI + "打开钱箱OpenCashBox函数调用失败(" + ret + ")");
//			}
			
			//参数设置回默认值（工作电压12V ，默认延迟500ms）
			if((ret = nlCashBoxManager.OpenCashBox(0,500)) != 1)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:%s打开钱箱OpenCashBox函数调用失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			//查看钱箱是否正常打开 
			if(gui.ShowMessageBox("钱箱是否正常打开？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
			{
				gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:打开钱箱OpenCashBox函数测试失败，未能正常打开钱箱（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg1_record(fileName,funcName,gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		}
		else
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"%s不支持%s用例", GlobalVariable.currentPlatform,TESTITEM);		
	}

	@Override
	public void onTestUp() {
		gui = new Gui(myactivity, handler);
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
