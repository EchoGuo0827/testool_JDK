package com.example.highplattest.apn;

import java.util.ArrayList;
import java.util.List;

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
 * file name 		: Apn2.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150615 
 * directory 		: 获取默认apn和设置默认apn
 * description 		: getPreferApn和setDefault
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150615     	created
 * * history 		 	: 变更记录												变更时间			变更人员
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200527                          陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Apn2 extends UnitFragment 
{
	private final String TESTITEM = "getPreferApn和setDefault";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils = null;
	private String fileName=Apn2.class.getSimpleName();
	MobileUtil mobileUtil;
	boolean mobilestate;
	
	public void apn2() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		
		/* private & local definition */
		try 
		{
			apnUtils = new ApnUtils(myactivity);
		} catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
		}
		
		List<ApnEntity> listEntity = new ArrayList<ApnEntity>();
		String[] strName = null;
		int ret = -1;
		int id = -1,defaultId = 0;
		boolean flag;
		
		/*process body*/
		if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg1(gKeepTimeErr, "未插入sim卡，请先插卡");
			return;
		}
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		//参数异常
		if((ret = apnUtils.setDefault(-1)) !=-1)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:%s设置默认的apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		// 测试前置，获取默认apn
		// case1:设置其他apn为默认apn，默认apn应修改成功
		if(gui.cls_show_msg1(2, "设置其他apn为默认apn，默认apn应修改成功，【取消】退出测试")==ESC)
			return;
		ApnEntity apnDefaultEntity = apnUtils.getPreferApn();
		if(apnDefaultEntity==null)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:获取默认apn失败(%s)", Tools.getLineInfo(),apnDefaultEntity);
			if(!GlobalVariable.isContinue)
				return;
		}
		else
			gui.cls_show_msg1(2, "默认apn%s", apnDefaultEntity.getApn());
		listEntity = apnUtils.getAllApnList();
		strName = new String[listEntity.size()];
		for (int i = 0; i < listEntity.size(); i++) 
		{
			strName[i] = listEntity.get(i).getApn();
		}
		
		//自动选择个默认apn
		defaultId = (int) (Math.random()*listEntity.size());
		gui.cls_show_msg1(2,"默认apn将设置为%s", listEntity.get(defaultId).getApn());
		
		if((ret = apnUtils.setDefault(listEntity.get(defaultId).getId()))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:%s添加默认apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:新增apn，设置为默认apn应成功
		if(gui.cls_show_msg1(2, "新增apn，设置为默认apn应成功，【取消】退出测试")==ESC)
			return;
		ApnEntity apnEntity=new ApnEntity();
		//name,apn,mnc,mcc必须设置
		apnEntity.setName("自定义apn");
		apnEntity.setApn("myapn");
		apnEntity.setMcc("460");
		apnEntity.setMnc("01");
		apnEntity.setType("default,supl");
		if((id = apnUtils.addNewApn(apnEntity))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:%s新增apn失败(%d)", Tools.getLineInfo(),TESTITEM,id);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		if((ret = apnUtils.setDefault(id))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:%s添加默认apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3:默认的apn应该能够被删除
		if(gui.cls_show_msg1(2, "默认的apn应该能够被删除，【取消】退出测试")==ESC)
			return;
		if(!(flag = apnUtils.removeApn(id)))
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:%s删除默认apn失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 查看设置的默认apn是否被删除成功
		if((ret=listEntity.get(listEntity.size()-1).getId()) == id)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr, "line %d:%s删除默认apn失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 测试后置将默认apn重新设置
		if((ret = apnUtils.setDefault(apnDefaultEntity.getId()))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn2", gKeepTimeErr,"line %d:%s恢复为默认apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "apn2", gScreenTime,"%s测试通过", TESTITEM);

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
