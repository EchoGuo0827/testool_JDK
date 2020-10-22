package com.example.highplattest.android;

import java.util.ArrayList;
import java.util.List;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android2.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180409 
 * directory 		: 
 * description 		: 测试Android原生sim接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180409	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("InlinedApi")
public class Android2 extends UnitFragment {
	public final String TAG = Android2.class.getSimpleName();
	private String TESTITEM = "sim信息原生接口测试";
	private Gui gui = new Gui(myactivity, handler);
	private TelephonyManager tm;
	private StringBuffer sb = new StringBuffer();
	private Config config;
	private MobilePara mobilePara = new MobilePara();
	public static final String RESTORE_CARRIERS_URI = "content://telephony/carriers/restore";
	public static final String PREFERRED_APN_URI = "content://telephony/carriers/preferapn";
	public static final String APN_ID = "apn_id";
	public static final String APN_ID_ONE = "apn_id.0";
	public static final String APN_ID_TWO = "apn_id.1";
	private static final Uri PREFERAPN_URI = Uri.parse(PREFERRED_APN_URI);
	private static final int ID_INDEX = 0;
	private static final int NAME_INDEX = 1;
	private static final int APN_INDEX = 2;
	private static final int TYPES_INDEX = 3;
	List<ApnEntity> apnAllList = new ArrayList<ApnEntity>();
	List<ApnEntity> apnNameList=new ArrayList<ApnEntity>();
	StringBuffer apnNameString = new StringBuffer();
	String message="";
	@SuppressLint("NewApi") @TargetApi(19)
	public void android2(){
		config = new Config(myactivity, handler);
		ApnUtils apnUtils = new ApnUtils(myactivity);
		ApnEntity apn = apnUtils. getPreferApn();
		
		gui.cls_show_msg("测试前请确保已经安装可用的sim卡，完成任意键继续");
		if(config.confConnWLM(true,mobilePara)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "android2", gKeepTimeErr, "line %d:%s网络未接通!!!", Tools.getLineInfo(), TESTITEM);
            return;
		}
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		//获取sim卡信息
		tm = (TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
        sb.append("\nLine1Number = " + tm.getLine1Number());//电话号码
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());//移动运营商编号
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());//移动运营商名称
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber()); //获取sim卡iccid
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());//IMSI
        sb.append("\nImei = " + tm.getImei());//Imei
        sb.append("\nSimState = " + tm.getSimState());//SimState
//        sb.append("\n所设置的默认APN="+getSelectedApn());//获取APN
        if (gui.cls_show_msg("查看sim卡信息是否如下：\n%s,[确认]是，[其他]否",sb.toString()) != ENTER) 
		{
        	gui.cls_show_msg1_record(TAG, "android20", gKeepTimeErr, "line %d:%s获取sim卡信息错误", Tools.getLineInfo(), TESTITEM);
		}
        apnAllList = apnUtils.getAllApnList();
    	for(ApnEntity apn1:apnAllList){
				apnNameList.add(apn1);
		}
		for(ApnEntity apn2:apnNameList)
		{
			apnNameString.append(apn2.getName()+"("+apn2.getApn()+")"+"\n");
		}
		
		message = String.format("查看APN是否符合,[确认]是,[其他]否\n apn个数为：%d个,分别是:\n%s", apnNameList.size(),apnNameString);
		if(gui.ShowMessageBox(message.getBytes(), (byte) (BTN_OK|BTN_CANCEL), MAXWAITTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, "android2", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
        gui.cls_show_msg1_record(TAG, "android2",gScreenTime,"%s测试通过", TESTITEM);
	}
	
//	private  String getSelectedApn() {
////		ApnInfo selectedApn = new ApnInfo();
////		Cursor cursor = myactivity.getContentResolver().query(PREFERAPN_URI
////				,new String[] { "_id", "name", "apn", "type" }, String.valueOf(1), null,Telephony.Carriers.DEFAULT_SORT_ORDER);
////		if (cursor.getCount() > 0) {
////			cursor.moveToFirst();
////			String id = cursor.getString(ID_INDEX);
////			String name = cursor.getString(NAME_INDEX);
////			String apn = cursor.getString(APN_INDEX);
////			String type = cursor.getString(TYPES_INDEX);
////			selectedApn.setId(id);
////			selectedApn.setName(name);
////			selectedApn.setApn(apn);
////			selectedApn.setType(type);
////		}
////		cursor.close();
////		String apn=selectedApn.getName()+" ("+selectedApn.getApn()+")";
////		return apn;
//		
//	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
class ApnInfo{
	private String id,name,apn,type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApn() {
		return apn;
	}

	public void setApn(String apn) {
		this.apn = apn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}