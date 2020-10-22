package com.example.highplattest.apn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Apn模块
 * file name 		: Apn4.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20170317 
 * directory 		: 获取目前的apn列表
 * description 		: getCurrentApnList
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj		   20170317     	created
 *   history 		 	: 变更记录												变更时间			变更人员
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200527                          陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Apn4 extends UnitFragment 
{
	private final String TESTITEM = "getCurrentApnList";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils = null;
	private String fileName=Apn4.class.getSimpleName();
	boolean mobilestate;
	MobileUtil mobileUtil;
	
	public void apn4() 
	{
		if(GlobalVariable.gAutoFlag== ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		/* private & local definition */
		try 
		{
			apnUtils = new ApnUtils(myactivity);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		int id,id2;
		boolean flag = false;
		List<ApnEntity> currentApnList = new ArrayList<ApnEntity>();
		List<ApnEntity> apnNameList=new ArrayList<ApnEntity>();
		String mcc="";
		String mnc="";
		String message="";
		StringBuffer apnNameString = new StringBuffer();
		/*process body*/
		
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		// case1:异常测试，不放置sim卡，预期返回为空
		if(gui.cls_show_msg1(2, "请根据有无sim卡进行测试，有无sim卡情况均要测试通过，【取消】退出测试")==ESC)
			return;
		
		int guiCode = gui.ShowMessageBox("进行无sim卡测试选择是，进行有sim卡测试选择否".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME);
		if(guiCode==BTN_OK)
		{
			if(mobileUtil.getSimState()!=NDK_ERR_SIM_NO_USE)
			{
				gui.cls_show_msg1(1, "请先拔掉sim卡,重启后再进入此用例");
				return;
			}
			if((currentApnList=apnUtils.getCurrentApnList())!=null)
			{
				gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s获取当前apn列表失败(%s)", Tools.getLineInfo(),TESTITEM,currentApnList);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr, "子用例测试通过");
			return;
		}
		//case2:尽可能多的放置不同的sim卡，包括但不限于4G、3G、物联卡等，预期返回数据列表	       
		if(gui.cls_show_msg1(2, "请多次进入此用例，尽可能多的放置不同的sim卡，包括但不限于移动、联通、电信、4G、3G、物联卡等，【取消】退出测试")==ESC)
			return;
		if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg1(1, "未插sim卡，请先插卡");
			return;
		}
		if((currentApnList=apnUtils.getCurrentApnList())==null)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s获取当前apn列表失败(%s)", Tools.getLineInfo(),TESTITEM,currentApnList);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		for(ApnEntity apn:currentApnList)
		{
			mcc=apn.getMcc();
			mnc=apn.getMnc();
//			if(apn.getName().equals(""))
//			{
//				noNameApnList.add(apn);
//			}else
				apnNameList.add(apn);
		}
//		for(ApnEntity apn:noNameApnList)
//		{
//			noNameApnString.append(apn.getName()+"("+apn.getApn()+")"+"\n");
//		}
		for(ApnEntity apn:apnNameList)
		{
			apnNameString.append(apn.getName()+"("+apn.getApn()+")"+"\n");
		}
		
		message = String.format(Locale.CHINA,"请进入设置-》移动网络-》接入点名称（APN）查看apn列表\napn个数为：%d个,分别是:\n%s"
				+ "进入每个apn查看，mcc为：%s,mnc为：%s", apnNameList.size(),apnNameString,mcc,mnc);
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%获取的当前apn列表不正确", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case3:新增一个apn，再次获取,应获取的到
		ApnEntity apnEntity=new ApnEntity();
		apnEntity.setName("myApn");
		apnEntity.setApn("myapn");
		apnEntity.setMcc(mcc);
		apnEntity.setMnc(mnc);
		apnEntity.setType("default,supl");
		if((id = apnUtils.addNewApn(apnEntity))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,id);
			if(!GlobalVariable.isContinue)
				return;
		}
		currentApnList = apnUtils.getCurrentApnList();
		
		// 自动查看
		int findFlag=-1;
		for(ApnEntity apn:currentApnList){
			if(apn.getId()==id)
			{
				findFlag=0;
				break;
			}
				
		}
		if(findFlag==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s新增apn在当前列表中找不到,测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//进入设置中 当前apn无删除操作
		message = "请进入设置-》移动网络-》接入点名称（APN） 查看新增名称为“myApn”的 apn，点击进入“myApn”查看，" +
				"点击界面右上角三点处，是否有删除Apn选项操作，不用删除apn";
		//如果存在删除操作，则出错
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), MAXWAITTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s设置read_only属性失败(%s)", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case4:新增一个设置为不可修改的apn，设置界面不应有修改按钮
		ApnEntity apnEntity2=new ApnEntity();
		apnEntity2.setName("myApn2");
		apnEntity2.setApn("myapn2");
		apnEntity2.setMcc(mcc);
		apnEntity2.setMnc(mnc);
		apnEntity2.setType("default,supl");
		//设置为不可以删除
		apnEntity2.setReadOnly(1);
		if((id2 = apnUtils.addNewApn(apnEntity2))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,id);
			if(!GlobalVariable.isContinue)
				return;
		}
	
		message = "请再次进入设置-》移动网络-》接入点名称（APN） 查看新增名称为“myApn2”的 apn，点击进入“myApn2”查看，" +
				"点击界面右上角三点处，是否有删除Apn选项操作，不用删除apn";
		//如果存在删除操作，则出错
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s设置read_only属性失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//测试后置
		if(!(flag=apnUtils.removeApn(id)) || !apnUtils.removeApn(id2) )
		{
			gui.cls_show_msg1_record(fileName, "apn4", gKeepTimeErr,"line %d:%s删除新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "apn4", gScreenTime, "%s测试通过", TESTITEM);

	}

	@Override
	public void onTestUp() {
		mobileUtil = MobileUtil.getInstance(myactivity,handler);
		mobilestate=mobileUtil.getMobileDataState(myactivity);
		
	}

	@Override
	public void onTestDown() {
		mobileUtil.setMobileData(myactivity, mobilestate);
		gui = null;
		apnUtils = null;
	}
}
