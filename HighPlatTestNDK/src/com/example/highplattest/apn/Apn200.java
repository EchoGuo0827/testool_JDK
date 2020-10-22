package com.example.highplattest.apn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.netutils.MobileUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;
/************************************************************************
 * module 			: Apn模块
 * file name 		: Apn5.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170824 
 * directory 		: 
 * description 		: 模块内随机测试
 * related document : 
 * history 		 	: 变更内容										变更时间				变更人员
 *			  		     新建		   								20170824	 		wangxy
 *					 BUG2020081302810测试后置修改为不要判断返回值 		20200824			郑薛晴
 *					 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Apn200 extends UnitFragment{
	private final String TESTITEM = "APN模块内随机";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils;
	ApnEntity apnDefaultEntity ;
	private Random random = new Random();
	List<ApnEntity> currentApnList = new ArrayList<ApnEntity>();
	int addId;
	ApnEntity removeEntity;
	List<ApnEntity> apnAllList = new ArrayList<ApnEntity>();//测试前置得到的list
	List<ApnEntity> apnAllList2 = new ArrayList<ApnEntity>();
	private String funcStr1,funcStr2 ;
	private List<ApnEntity> list=new ArrayList<ApnEntity>();
	private String[] type={"","default,supl","mms"};
	private String[] mnc={"00","02","04","07","08","01","06","09","03","05","11"};
	private List<ApnEntity> removeEntityList=new ArrayList<ApnEntity>();//曾删除的entity集合
	private List<Integer> addApnIdList=new ArrayList<Integer>();//曾新增的id集合
	private String fileName=Apn200.class.getSimpleName();
	public void apn200()
	{
		switch (GlobalVariable.currentPlatform) 
		{
		case N900_3G://不支持APN4的getCurrentApnList方法
			String ApnFunArr2[] = {"getPreferApn","addNewApn","getAllApnList","removeApn","setDefault"};
			Random_Test(ApnFunArr2);
			break;
		
		default:
			String ApnFunArr[] = {"getPreferApn","addNewApn","getAllApnList","removeApn","setDefault","getCurrentApnList"};
			Random_Test(ApnFunArr);
			break;
		}
		
	}
	public void Random_Test( String ApnFunArr[]){
		gui.cls_show_msg("该用例需在插入网络可用的sim卡，插卡后，进入自检-网络，确认移动网络可用后点击确定");
		MobileUtil mobileUtil = MobileUtil.getInstance(myactivity,handler);
		if(mobileUtil.getSimState()==NDK_ERR_SIM_NO_USE)
		{
			gui.cls_show_msg1(gKeepTimeErr, "未插入sim卡，请先插卡");
			return;
		}
		
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		// 实例化apn
		try {
			apnUtils = new ApnUtils(myactivity);
		} catch (NoClassDefFoundError e) {
			gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(), e.getMessage());
			return;
		}
		//设置次数
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
		//测试前置，获取默认apn
		ApnEntity DefaultEntity = apnUtils.getPreferApn();
		//获取所有apn
		apnAllList = apnUtils.getAllApnList();
		
		
		
		if(DefaultEntity!=null){
//			mnc=DefaultEntity.getMnc();
//			mcc=DefaultEntity.getMcc();
			//新增apn的测试数据
			for (int i = 0; i < mnc.length; i++) {
				ApnEntity apnEntity = new ApnEntity();
				apnEntity.setMcc("460");//中国
				apnEntity.setMnc(mnc[random.nextInt(mnc.length)]);
				apnEntity.setName("my define"+i);
				apnEntity.setApn("my apn"+i);
				apnEntity.setType(type[random.nextInt(type.length)]);
				list.add(apnEntity);
			}
			
		}else{
			gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,  "line %d:%s获取默认apn失败(false)", Tools.getLineInfo(), TESTITEM);
            return;
		}
		apnAllList2 = apnUtils.getAllApnList();
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "APN模块内随机组合测试中...\n还剩%d次（已成功%d次），按【取消】退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=ApnFunArr[random.nextInt(ApnFunArr.length)];
			}
			funcStr1 = "";
			funcStr2 = "";
			for(int i=0;i<g_RandomCycle;i++){
				if(i<10){
					funcStr1 = funcStr1 + func[i] + "-->\n";
				}else{
					funcStr2 = funcStr2 + func[i] + "-->\n";
				}
				
			}
			gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr1,bak-cnt+1);
			gui.cls_show_msg1(gScreenTime, funcStr2);
			cnt--;
			boolean ret=false;
			 addId = -1;
			 removeEntity=null;
//			 addFlag=false;
//			 defaultFlag=false;
			 apnDefaultEntity=new ApnEntity();
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"正在测试%s",func[i]);
				ApnFuncName fname = ApnFuncName.valueOf(func[i]);
				if(!(ret=RandomTest(fname,apnUtils)))
					break;
			}
			if(ret)
			succ++;
		}
		//测试后置，恢复默认设置
		//删除过程中新增的apn
		for(ApnEntity list1:removeEntityList){
			apnUtils.addNewApn(list1);// 需要增加的ID会因为后续被增加过而导致失败，后置测试不需判断返回值 20200824
//			if ((addId = apnUtils.addNewApn(list1))==-1) {
//				gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,  "line %d:%s测试后置添加被删除的默认Apn(false),id=%d", Tools.getLineInfo(), TESTITEM,list1.getId());
//			}
		}
		for(Integer list2:addApnIdList){
			apnUtils.removeApn(list2);// 需要移除的apn,会因为在测试过程已经移除过了而导致测试失败 20200824
//			if (!(apnUtils.removeApn(list2))) {
//				gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,  "line %d:%s测试后置，删除测试过程中增加的apn失败(false),id=%d", Tools.getLineInfo(), TESTITEM,list2);
//			}

		}
		
		if(DefaultEntity==null||apnUtils.setDefault(DefaultEntity.getId()) ==-1)
		{
			gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr, "line %d:%s恢复为默认的apn失败", Tools.getLineInfo(),TESTITEM);
		}
		gui.cls_show_msg("再次查看当前移动网络是否可用，进入自检-网络，确认移动网络可用后点击确定");
		gui.cls_show_msg1_record(fileName, "apn4", gScreenTime, "APN模块内随机组合测试测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
	}
	private boolean RandomTest(ApnFuncName fname, ApnUtils apnUtils) {
		int ret;
		boolean flag = false;
		boolean is =true;
		
		ApnEntity entity;
		switch(fname){
		case getPreferApn:
			 apnDefaultEntity = apnUtils.getPreferApn();
			 if(apnDefaultEntity==null){
				 gui.cls_only_write_msg(fileName, "apn200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				 gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr, "line %d:%s获取默认apn失败", Tools.getLineInfo(),TESTITEM);
					is=false;
			 }
			break;
		case addNewApn:
			entity=list.get(random.nextInt(list.size()));
			if((addId = apnUtils.addNewApn(entity))==-1)
			{
				gui.cls_only_write_msg(fileName, "apn200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				 gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,"line %d:%s新增apn失败(%s)", Tools.getLineInfo(),TESTITEM,addId);
				is=false;
			}else{
				gui.cls_only_write_msg(fileName, "apn200", "增加的apn列表值="+addId);
				addApnIdList.add(addId);//插入的apn列表
			}
			break;
		case getAllApnList:
			apnAllList2 = apnUtils.getAllApnList();
			if(apnAllList2==null||apnAllList2.size()==0){
				gui.cls_only_write_msg(fileName, "apn200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				 gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr, "line %d:%s获取apn列表失败(%s)", Tools.getLineInfo(), TESTITEM, flag);
				is=false;
			}
			break;
		case removeApn:
			apnAllList2 = apnUtils.getAllApnList();
			removeEntity=apnAllList2.get(random.nextInt(apnAllList2.size()));
			if (!(flag = apnUtils.removeApn(removeEntity.getId()))) {
				gui.cls_only_write_msg(fileName, "apn200", "%s模块内测试顺序为：\n"+ funcStr1 + funcStr2, TESTITEM);// 只写不显示
				gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,"line %d:%s删除apn失败(%s)", Tools.getLineInfo(), TESTITEM,flag);
				is = false;
			} else {
				gui.cls_only_write_msg(fileName, "apn200", "移除的apn值="+removeEntity.getId());
				removeEntityList.add(removeEntity);
			}
			
			break;
		case setDefault:
			apnAllList2 = apnUtils.getAllApnList();// 重新获取列表，从中任意选取一个
			if ((ret = apnUtils.setDefault(apnAllList2.get(random.nextInt(apnAllList2.size())).getId())) == -1) {
				gui.cls_only_write_msg(fileName, "apn200", "%s模块内测试顺序为：\n"+ funcStr1 + funcStr2, TESTITEM);// 只写不显示
				gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,
						"line %d:%s设置默认的apn失败(%d)", Tools.getLineInfo(),TESTITEM, ret);
				is = false;
			}
			break;
		case getCurrentApnList:
			currentApnList=apnUtils.getCurrentApnList();
			if(currentApnList==null||currentApnList.size()==0){
				gui.cls_only_write_msg(fileName, "apn200","%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				 gui.cls_show_msg1_record(fileName, "apn200", gKeepTimeErr,"line %d:%s获取当前apn列表失败(%s)", Tools.getLineInfo(),TESTITEM,currentApnList);
				is=false;
			}
				
			break;
		default:
			break;
		}
		return is;
	}

	private enum ApnFuncName {
		getPreferApn, addNewApn, getAllApnList, removeApn, setDefault, getCurrentApnList
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
		apnUtils = null;
	}

}
