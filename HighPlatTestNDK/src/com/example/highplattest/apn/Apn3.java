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
 * file name 		: Apn3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150615 
 * directory 		: 增加新的apn
 * description 		: addNewApn
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150615     	created
 *  history 		 	: 变更记录												变更时间			变更人员
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200527                          陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Apn3 extends UnitFragment 
{
	private final String TESTITEM = "addNewApn";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils = null;
	private String fileName=Apn3.class.getSimpleName();
	MobileUtil mobileUtil;
	boolean mobilestate;
	
	public void apn3() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		/* private & local definition */
		try 
		{
			apnUtils = new ApnUtils(myactivity);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		ApnEntity apnDefaultEntity = apnUtils.getPreferApn();
		ApnEntity tempApn;
		int ret;
		int id,id2;
		boolean flag = false;
		List<Integer> deleteList = new ArrayList<Integer>();
		
		/*process body*/
		if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg1(1, "未插入sim卡，请先插卡");
			return;
		}
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		gui.cls_show_msg("提示：该案例最好选用46001的sim卡来测试。且后面随机新增3个apn时有可能会新增到参数不全的apn，导致新增失败。这是正常现象");
		// case1:参数异常测试，各种参数缺省测试应失败
		if(gui.cls_show_msg1(2, "参数异常测试，各种参数缺省测试应失败，【取消】退出测试")==ESC)
			return;
		ApnEntity apnEntity=new ApnEntity();
		// name不设置的情况
		apnEntity.setName(null);
		apnEntity.setApn("myapn");
		apnEntity.setMcc("460");
		apnEntity.setMnc("01");
		apnEntity.setType("default,supl");
		if((ret = apnUtils.addNewApn(apnEntity))>=0)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// name,apn,mnc,mcc必须设置
		// apn不设置
		apnEntity.setName("my 1");
		apnEntity.setApn(null);
		apnEntity.setMcc("460");
		apnEntity.setMnc("01");
		apnEntity.setType("default,supl");
		if((ret = apnUtils.addNewApn(apnEntity))>=0)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// mnc不设置
		apnEntity.setName("my 1");
		apnEntity.setApn("my apn");
		apnEntity.setMcc(null);
		apnEntity.setMnc("01");
		apnEntity.setType("default,supl");
		if((ret = apnUtils.addNewApn(apnEntity))>=0)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// mcc 不设置
		apnEntity.setName("my 1");
		apnEntity.setApn("my apn");
		apnEntity.setMcc("460");
		apnEntity.setMnc(null);
		apnEntity.setType("default,supl");
		if ((ret = apnUtils.addNewApn(apnEntity)) >= 0) 
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case2:正常新增apn
		if(gui.cls_show_msg1(2, "正常新增apn，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg1(2, "新增my apn");
		apnEntity.setName("my define");
		apnEntity.setApn("my apn");
		apnEntity.setMcc("460");
		apnEntity.setMnc("01");
		apnEntity.setType("mms");
		if((id = apnUtils.addNewApn(apnEntity))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,id);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		
		// 列出目前的apn进行查看是否新增成功
		List<ApnEntity> listEntity = apnUtils.getAllApnList();
		
		// 自动查看
		if((ret=listEntity.get(listEntity.size()-1).getId()) != id)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//authtype、roamingProtocol、Protocol、bearer 、mvnoType 5个参数不设置为默认
		/*优化参数点：add by zhangxinj 20170317
		1、authtype（-1为未设置，0为无，1为PAP，2为CHAP，3为PAP或CHAP）如果参数传不对或不传，会设置为-1
		2、roamingProtocol和Protocol（IP  , IPV6,  IPV4V6）传不对或不传，会设置为IP
		3、bearer （0未指定，13为eHRPD，14LTE）传不对或不传，设置为0
		4、mvnoType（“”，“sip”，“imsi”，“gid”）传不对或不传，为“”无*/
		tempApn=listEntity.get(listEntity.size()-1);
		if(!tempApn.getAuthType().equals("-1") || !tempApn.getRoamingProtocol().equals("IP") || !tempApn.getProtocol().equals("IP") || !tempApn.getBearer().equals("0")|| !tempApn.getMvnoType().equals("")){
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn参数校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case6:authtype、roamingProtocol、Protocol、bearer 、mvnoType参数异常
		gui.cls_show_msg1(2, "新增my apn2");
		apnEntity.setName("my define");
		apnEntity.setApn("my apn2");
		apnEntity.setMcc("460");
		apnEntity.setMnc("01");
		apnEntity.setType("mms");
		apnEntity.setAuthType("authtype");
		apnEntity.setRoamingProtocol("roamingProtocol");
		apnEntity.setProtocol("Protocol");
		apnEntity.setBearer("bearer");
		apnEntity.setMvnoType("mvnoType");
		
		
		// 自动查看
		
		if((id2 = apnUtils.addNewApn(apnEntity))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,id);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 列出目前的apn进行查看是否新增成功
	    List<ApnEntity> listEntity2 = apnUtils.getAllApnList();
				
		// 自动查看
		if((ret=listEntity2.get(listEntity2.size()-1).getId()) != id2)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		tempApn=listEntity2.get(listEntity2.size()-1);
		if(!tempApn.getAuthType().equals("-1") || !tempApn.getRoamingProtocol().equals("IP") || !tempApn.getProtocol().equals("IP") || !tempApn.getBearer().equals("0")|| !tempApn.getMvnoType().equals("")){
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s新增apn参数校验失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case3:将新增的apn设置为默认apn,应成功
		if(gui.cls_show_msg1(2, "将新增的apn设置为默认apn,应成功，【取消】退出测试")==ESC)
			return;
		if((ret = apnUtils.setDefault(id))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s设置为默认apn失败(%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret=apnUtils.getPreferApn().getId())!=id)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s获取默认apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case4:删除新增apn应成功
		if(gui.cls_show_msg1(2, "删除新增apn应成功，【取消】退出测试")==ESC)
			return;
		if(!(flag = apnUtils.removeApn(id)) || !(flag = apnUtils.removeApn(id2)) )
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s删除新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 列出目前的apn进行查看是否删除成功
		List<ApnEntity> listEntityTwo = apnUtils.getAllApnList();
		if((ret=listEntityTwo.get(listEntityTwo.size()-1).getId()) == id)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s删除新增apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		int count=0;
		// case5:选取几个APN新增
		if(gui.cls_show_msg1(2, "选取几个APN新增，【取消】退出测试")==ESC)
			return;
		gui.cls_show_msg1(1, "正在进行新增apn的操作");
		for (int i = 0; i < 3; i++) 
		{
			if ((ret = apnUtils.addNewApn(listEntity.get((listEntity.size() - 1) * i / 2))) == -1) 
			{
				gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr,  "line %d:%s添加Apn失败(%d)",Tools.getLineInfo(), TESTITEM, ret);
				if (!GlobalVariable.isContinue)
					return;
			} else{
				count++;
				deleteList.add(ret);
			}
		}
	
		// 测试后置，删除新增的apn
		for (int i = 0; i <count; i++) 
		{
			if(!(flag = apnUtils.removeApn(deleteList.get(i))))
			{
				gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s删除apn失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// 测试后置，重新设置为默认apn
		if((ret = apnUtils.setDefault(apnDefaultEntity.getId()))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn3", gKeepTimeErr, "line %d:%s恢复为默认apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "apn3", gScreenTime,  "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		mobileUtil = MobileUtil.getInstance(myactivity,handler);
		mobilestate=mobileUtil.getMobileDataState(myactivity);
		
	}

	@Override
	public void onTestDown() {
		mobileUtil.setMobileData(myactivity,mobilestate);
		gui = null;
		apnUtils = null;
	}
}
