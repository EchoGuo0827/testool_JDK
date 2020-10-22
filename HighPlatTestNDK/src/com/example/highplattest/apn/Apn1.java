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
 * file name 		: Apn1.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150615 
 * directory 		: 获取全部的apn和移除单个apn操作
 * description 		: getAllApnList和removeApn
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150615     	created
 * history 		 	: 变更记录												变更时间			变更人员
*					新增前置判断，如果测试前移动网络打开，则测试后置也恢复打开状态                                20200527                          陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Apn1 extends UnitFragment 
{
	private final String TESTITEM = "getAllApnList和removeApn";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils;
	private String fileName=Apn1.class.getSimpleName();
	boolean Mobilestate;
	MobileUtil mobileUtil;
	public void apn1() 
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
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr,"line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		ApnEntity apnEntity = new ApnEntity();
		// 测试前获取好默认的apn为测试后置做准备
		ApnEntity apnDefaultEntity = apnUtils.getPreferApn();
		List<ApnEntity> apnAllList = new ArrayList<ApnEntity>();
//		List<ApnEntity> apnNameList=new ArrayList<ApnEntity>();
//		StringBuffer apnNameString = new StringBuffer();
		String[] listName ;
		int ret = 0,id;
		boolean flag = false;
		
		/*process body*/
		if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg1(gKeepTimeErr, "未插入sim卡，请先插卡");
			return;
		}
		//case 参数异常测试，wangxy20170829
		// 删除id=-1
		if (flag = apnUtils.removeApn(-1)) {
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr, "line %d:%s删除id=-1的apn失败(%s)", Tools.getLineInfo(), TESTITEM, flag);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case1:移除某个自定义id的apn，再获取全部apn，某id的apn应消失
		if(gui.cls_show_msg1(2, "移除某个自定义id的apn，再获取全部apn，某id的apn应消失，【取消】退出测试")==ESC)
			return;
		// 新增自定义的apn
		apnEntity.setName("my define");
		apnEntity.setApn("my apn");
		apnEntity.setMcc("460");
		apnEntity.setMnc("01");
		apnEntity.setType("mms");
		if((id = apnUtils.addNewApn(apnEntity))==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr,"line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,id);
			if(!GlobalVariable.isContinue)
				return;
		}
		apnAllList = apnUtils.getAllApnList();
		listName = new String[apnAllList.size()];
		for (int i = 0; i < apnAllList.size(); i++) 
		{
			listName[i] = String.valueOf(apnAllList.get(i).getId());
		}
		
		if((ret=apnAllList.get(apnAllList.size()-1).getId())!= id)
		{
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr, "line %d:%s新增apn失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// 删除特定的id
		if(!(flag = apnUtils.removeApn(id)))
		{
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr,"line %d:%s删除apn失败(%s)", Tools.getLineInfo(),TESTITEM,flag);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 重新获取apn，原先的那个apn应被删除
		if((ret=apnUtils.getAllApnList().get(apnUtils.getAllApnList().size()-1).getId())==id)
		{
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr,"line %d:删除apn失败(%s)",Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
//		//case2:插入伊朗SIM卡时，查询APN列表，应该有Irancell-GPRSMCI-GPRSMCI-MMS RighTel 4种
//				if(gui.cls_show_msg1(2, "插入伊朗SIM卡，查询APN列表，应该有Irancell-GPRS MCI-GPRS MCI-MMS RighTel 4种，【取消】退出测试")==ESC)
//					return;
//				apnAllList = apnUtils.getAllApnList();
//				for(ApnEntity apn:apnAllList){
//					if(apn.getMcc()!=null && (apn.getMcc().equalsIgnoreCase("432")||apn.getMcc().equalsIgnoreCase("460")))
//						apnNameList.add(apn);
//				}
//				for(ApnEntity apn:apnNameList)
//				{
//					apnNameString.append(apn.getName()+"("+apn.getApn()+")"+"\n");
//				}
//				message = String.format("查看APN是否符合,[确认]是,[其他]否\n apn个数为：%d个,分别是:\n%s", apnNameList.size(),apnNameString);
//				if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), MAXWAITTIME)!=BTN_OK)
//				{
//					gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr,"line %d:%s查询伊朗APN失败", Tools.getLineInfo(),TESTITEM);
//					if(!GlobalVariable.isContinue)
//						return;
//				}
		
		// 测试后置设为原先默认的apn
		ret = apnUtils.setDefault(apnDefaultEntity.getId());
		if(ret ==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn1", gKeepTimeErr,"line %d:%s恢复为默认的apn失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "apn1", gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() 
	{
		mobileUtil = MobileUtil.getInstance(myactivity,handler);
		Mobilestate=mobileUtil.getMobileDataState(myactivity);
	}

	@Override
	public void onTestDown() {
		mobileUtil.setMobileData(myactivity, Mobilestate);
		gui = null;
		apnUtils = null;
	}
}
