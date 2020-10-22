package com.example.highplattest.net;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.systemversion.SystemVersion1;

/************************************************************************
 * 
 * file name 		: Net10.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20190926 
 * description 		:   获取国家码MCC
 * history 		 	: 变更点								变更人员			变更时间
 * 					将原systemversion12案例搬移到net10    	陈丁			     20200609
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net11 extends UnitFragment {
	private final String TESTITEM = "获取国家码MCC";
	private String fileName=Net11.class.getSimpleName();
	private String CellInfoGsm="";
	private int mcc=-1234;
	Context context;
	int cnt ; 
	int bak;
	int flag=0;
	int Longitudetem = 0;
	int Latitudetem = 0;
	String type="";
	private Gui gui = new Gui(myactivity, handler);
	TelephonyManager TelephonyManager=null;
	
	public void net11(){	
		SharedPreferences sharedPreferences = myactivity.getSharedPreferences("eric", Context.MODE_PRIVATE); //私有数据
		Editor editor = sharedPreferences.edit();//获取编辑器
		cnt=bak=1;
		TelephonyManager telephonyManager= (TelephonyManager)myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		gui.cls_show_msg1(1, "%s获取中........", TESTITEM);
		while (true) {
			List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
			if(cellInfoList!=null){
			 for(CellInfo cellInfo:cellInfoList){
					if (cellInfo instanceof android.telephony.CellInfoGsm) {
						mcc =((android.telephony.CellInfoGsm) cellInfo).getCellIdentity().getMcc();
//						if(gui.cls_show_msg1_record(fileName, "获取国家码MCC", 1,"当前类型是Gsm,MCC的值为:%d,当前第%d次", mcc,cnt)==ESC){
//							return;
//						}
						type="Gsm";
							break;
						
					}else if(cellInfo instanceof CellInfoWcdma){
						 	mcc = ((CellInfoWcdma) cellInfo).getCellIdentity().getMcc();
//						 	gui.cls_show_msg1_record(fileName, "获取国家码MCC", 1,"当前类型是cdma,MCC的值为:%d,当前第%d次", mcc,cnt);
//							mcc=-1234;
						 	type="Wcdm";
							break;
					}else  if(cellInfo instanceof CellInfoLte){
						mcc = ((CellInfoLte) cellInfo).getCellIdentity().getMcc();
//						gui.cls_show_msg1_record(fileName, "获取国家码MCC", 1,"当前类型是Lte,MCC的值为:%d,当前第%d次", mcc,cnt);
						type="Lte";
						break;
						
					}else if (cellInfo instanceof CellInfoCdma){
						 	int Latitude = ((CellInfoCdma) cellInfo).getCellIdentity().getLatitude();
			                int Longitude = ((CellInfoCdma) cellInfo).getCellIdentity().getLongitude();
//			                gui.cls_show_msg1_record(fileName, "获取国家码MCC", 1,"当前类型是Cdma,无法获取MCC值。获取小区经纬度为：%d---%d,当前第%d次", Latitude,Longitude,cnt);
			                Latitudetem=Latitude;
			                Longitudetem=Longitude;
			                type="Cdma";
			                mcc=-1234;
			                continue;
					}
				 }
			}else {
				type="null";
				mcc=-10086;
				
			}
			 if (flag==0) {
				editor.putInt("mccvalue", mcc);
				editor.commit();
				flag=1;
				gui.cls_show_msg1_record(fileName, "获取国家码MCC", 2,"本次为第一次进入程序获取的状态（记录）。。。当前类型是%s,MCC的值为:%d,当前第%d次....若类型为Cdma，则经度为：%d,纬度为：%d",type,mcc,cnt,Longitudetem,Latitudetem);
				
			}
			 Log.d("flag===", flag+"");
			 int mcct=sharedPreferences.getInt("mccvalue", 0);
			 if (mcc==mcct) {
				 if(gui.cls_show_msg1(1,"本次的的mcc值未改变。当前类型是%s,MCC的值为:%d,当前第%d次...若类型为Cdma，则经度为：%d,纬度为：%d",type,mcc,cnt,Longitudetem,Latitudetem)==ESC){
					 return;
				 }
				
			}else {
				if(gui.cls_show_msg1_record(fileName, "获取国家码MCC", 2,"本次MCC值发生变化。。。当前类型是%s,MCC的值为:%d,当前第%d次...若类型为Cdma，则经度为：%d,纬度为：%d",type,mcc,cnt,Longitudetem,Latitudetem)==ESC){
					return;
				}
				editor.putInt("mccvalue", mcc);
				editor.commit();
			}
			cnt++;
			SystemClock.sleep(10000);
			
		}

		
		
		
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
