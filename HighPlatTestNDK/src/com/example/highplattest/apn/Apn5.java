package com.example.highplattest.apn;

import java.util.ArrayList;
import java.util.List;

import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Apn模块
 * file name 		: Apn1.java 
 * Author 			: weimj
 * version 			: 
 * DATE 			: 20190827
 * directory 		: 
 * description 		: 获取伊朗APN
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  weimj 		   20190828     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Apn5 extends UnitFragment
{
	private final String TESTITEM = "获取伊朗APN(海外版)";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils;
	private String fileName=Apn5.class.getSimpleName();

	public void apn5() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		/* private & local definition */
		gui.cls_show_msg1(2,  TESTITEM+"测试中...");
		// 实例化apn
		try 
		{
			apnUtils = new ApnUtils(myactivity);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "apn5", gKeepTimeErr,"line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}

		List<ApnEntity> apnAllList = new ArrayList<ApnEntity>();
		List<ApnEntity> apnNameList=new ArrayList<ApnEntity>();
		StringBuffer apnNameString = new StringBuffer();
		String message="";
		
		//case1:查询伊朗APN列表，应该有Irancell-GPRSMCI-GPRSMCI-MMS RighTel 4种
		if(gui.cls_show_msg1(2, "查询伊朗APN列表，应该有Irancell-GPRS MCI-GPRS MCI-MMS RighTel 4种，【取消】退出测试")==ESC)
			return;
		apnAllList = apnUtils.getAllApnList();
		for(ApnEntity apn:apnAllList){
			if(apn.getMcc()!=null && apn.getMcc().equalsIgnoreCase("432"))
				apnNameList.add(apn);
		}
		for(ApnEntity apn:apnNameList)
		{
			apnNameString.append(apn.getName()+"("+apn.getApn()+")"+"\n");
		}
		message = String.format("查看APN是否符合,[确认]是,[其他]否\n apn个数为：%d个,分别是:\n%s", apnNameList.size(),apnNameString);
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), MAXWAITTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "apn5", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "apn5", gScreenTime,"%s测试通过", TESTITEM);
	}
	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}
	
}
