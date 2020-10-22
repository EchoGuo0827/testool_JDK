package com.example.highplattest.main.tools;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/** 
* description 		: Ota升级广播的接收
* related document : 
* history 		 	: 变更记录															变更时间			变更人员
*			  		  新支付架构平台获取K21升级结果的属性值改为sys.secUpdateResult，A5和F7/F10不变		20200507	 	郑薛晴
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class OtaUpdateBroadcastReceiver extends BroadcastReceiver{

	private final String ACTION_OTA_UPDATE = "android.intent.extra.ota.silent.installation.result";
	
	private static int state = -1;
	private static int errCode = -1;
	private static String failedName = "default";
	private static String gOtaTime = "";
	private static String gK21Status = "";
	private static SharedPreferences sharedPreferences;
	private static SharedPreferences sharedPreferences2;
	private static SharedPreferences.Editor editor;
	private static SharedPreferences.Editor editor2;
	private boolean isforth;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		Log.e("---------------------------------", action);
		
		String str = new String();
		
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		sharedPreferences2=context.getSharedPreferences("ISForth", Context.MODE_PRIVATE);
		editor2=sharedPreferences2.edit();
		
		isforth=sharedPreferences2.getBoolean("isforth", false);
		Log.d("eric_chen", "BroadcastReceiver isforth: "+isforth);
//		if (intent.getAction().equals(ACTION_OTA_UPDATE)) 
		if(ACTION_OTA_UPDATE.equals(intent.getAction()))
		{
			Log.e("eric_chen", "action: "+action);
			state = intent.getIntExtra("state", -1);//状态说明： 0-成功 2-继续  其他全部失败
			editor.putInt("state", state);
			Log.e("eric_chen","state: "+ state);
			errCode = intent.getIntExtra("errCode", 0);//错误码说明：100-OTA包解析失败 ;101-电量低， OTA升级失败
			editor.putInt("errCode", errCode);
			Log.e("eric_chen", "errCode:  "+errCode);
			str = intent.getStringExtra("failedOTAPackageName");
			Log.e("eric_chen", "str:  "+str);
			if (str!=null) {
				failedName = str.equals("") ? "null" : str;
			}else {
				failedName="null";
			}
			editor.putString("failedOTAPackageName", failedName);
			Log.e("eric_chen", "failedOTAPackageName:  "+failedName);
			str = Tools.getSysNowTime();
			if (str!=null) {
				gOtaTime = str.equals("")?"null":str;
			}else {
				gOtaTime="null";
			}
		
			Log.e("eric_chen", "otaTime:  "+gOtaTime);
			editor.putString("otaTime", gOtaTime);
			
			// str=BaseFragment.getSystemProperty("sys.k21UpdateStatus");
			//解决空指针异常崩溃 广播比acvitity先启动。所以获取会空指针，改为在案例中存入数据库后判断是否为forth固件
			if(isforth)
				str = BaseFragment.getProperty("sys.secUpdateResult", "-10086");/**A7平台获取K21升级结果的属性值修改20200507*/
			else
				str = BaseFragment.getProperty("sys.k21UpdateStatus", "-10086");
			gK21Status = str.equals("")?"null":str;
			editor.putString("k21Status", gK21Status);
			
			editor.commit();//提交修改
			 Toast.makeText(context, "当前时间:"+Tools.getSysNowTime()+"OTA升级广播,状态码为："+state, Toast.LENGTH_SHORT).show();
			Log.e("sss", "state:"+state);
		}
	}
	
	public static int getState(Context context)
	{
		//return state;
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		return sharedPreferences.getInt("state", state);
	}
	
	public static int getErrCode(Context context)
	{
		//return errCode;
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		return sharedPreferences.getInt("errCode", errCode);
	}
	
	public static String getFailedName(Context context)
	{
		//return failedName;
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		return sharedPreferences.getString("failedOTAPackageName", failedName);
	}
	
	public static String getOtaTime(Context context)
	{
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		return sharedPreferences.getString("otaTime", gOtaTime);
	}
	
	public static String getK21Status(Context context)
	{
		//return failedName;
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		return sharedPreferences.getString("k21Status", gK21Status);
	}
	
	public static void reset(Context context)
	{
		sharedPreferences = context.getSharedPreferences("OtaUpdateBroadcastReceiver", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.putInt("state", -1);
		editor.putInt("errCode", -1);
		editor.putString("failedOTAPackageName", "default");
		editor.putString("otaTime", "");
		editor.putString("k21Status","");
		editor.commit();//提交修改
	}
	
}
