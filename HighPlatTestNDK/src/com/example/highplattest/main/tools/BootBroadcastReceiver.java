package com.example.highplattest.main.tools;

import java.util.Arrays;
import java.util.Random;
import com.example.highplattest.activity.BtBackActivity;
import com.example.highplattest.activity.WlanrebootActivity;
import com.example.highplattest.main.btutils.BlueBean;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_HWINFO;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BootBroadcastReceiver extends BroadcastReceiver implements Lib
{
	static {
		System.loadLibrary("LoadTlk");
	}
	private final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private final String ACTION_COMMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
	private final String ACTION_ALIBABA= "android.intent.action.custom.BOOT_COMPLETED";//这个是阿里智管家的action
	private final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	long startTime;
	BlueBean bean;
	private SharedPreferences sp;
	private SharedPreferences.Editor mEditor;
	
	private SharedPreferences sp2;
	private SharedPreferences.Editor mEditor2;
	Gui gui = new Gui();
	SecKcvInfo kcvInfo = new SecKcvInfo();/**add by zhengxq用于测试重启-安装密钥*/
	private final String TRANSMIT_KEY = "31313131313131313131313131313131";// 传输密钥
	private Context mContext;
	private boolean gIsExist=false;;
	final  int NDK_OK=0;
	final int NDK_ERR=-1;
	final int NDK_ERR_PARA = -6;//参数非法
	int wifireboottem=0;
	int wifirebootfirst=0;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		sp = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		mEditor = sp.edit();
		mContext = context;
		sp2 = context.getSharedPreferences("WifiReboot", Context.MODE_PRIVATE);
		mEditor2 = sp2.edit();
		Log.e("---------------------------------", action);
		// 接收系统开机广播
		if(action.equals(ACTION_BOOT_COMPLETED))
		{
//			mContext = context;
//			Tools.savaData(context, "ACTION_BOOT_COMPLETED", action);
//			final PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
//			// 安装TLK和TMK
//			int iRet=-1;
//			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg()), (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=0)
//			{
//				gui.cls_only_write_msg("BootBroadcastReceiver", "onReceive", "line %d:%d安装TLK密钥失败(%d)", Tools.getLineInfo(),ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),iRet);
//			}
//			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()), (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=0)
//			{
//				gui.cls_only_write_msg("BootBroadcastReceiver", "onReceive", "line %d:%d安装TLK密钥失败(%d)", Tools.getLineInfo(),ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),iRet);
//			} 
//			new Thread(new SecLoadRun()).start();
////			//定时重启,间隔20us重启一次
////			LoggerUtil.e("sec time:"+sec);
//			final Timer timer = new Timer();
//			 timer.schedule(new TimerTask(){
//			      public void run(){
//			    	  if(gIsExist==false)
//			    	  {
//				          System.out.println("POS-reboot!!!!");
//				          gui.cls_only_write_msg("BootBroadcastReceiver", "onReceive",System.currentTimeMillis()+"重启时间");
//				          pm.reboot(null); 
//				          timer.cancel();
//			    	  }
//			       }
//			   }, 100);// 间隔10ms重启一次
			// 接收到系统的开机广播
			Tools.savaData(context, "ACTION_System_BOOT", action);
		}
		else if(action.equals(ACTION_ALIBABA))
		{
			// 接收到阿里的开机广播
			Tools.savaData(context, "ACTION_ALIBABA", action);
		}
		else if(action.equals(ACTION_COMMING_NOISY))
		{
			Tools.savaData(context, "ACTION_COMMING_NOISY", action);
		}
		
		// 关机广播
		if(action.equals(ACTION_SHUTDOWN))
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "onReceive",System.currentTimeMillis()+"关机广播时间");
		}
//		//接收系统开机广播,POS判断wifi连接状态等信息
//		if ((action.equals(ACTION_BOOT_COMPLETED)||action.equals(ACTION_COMMING_NOISY))&&sp2.getInt("wifireboot", -100)>0) 
//		{
//			if (wifirebootfirst==0) {
//				wifireboottem=sp2.getInt("wifireboot", -100);
//				wifireboottem=wifireboottem-1;
//				Log.d("eric_chen", "执行压测减一----------------");
//				mEditor2.putInt("wifireboot", wifireboottem);
//				mEditor2.commit();
//			}
//			wifirebootfirst=1;
//			Log.d("eric_chen", "wifireboottem==="+wifireboottem);
////			mEditor2.putInt("wifireboot", wifireboottem);
////			mEditor2.commit();
//			Intent toIntent = new Intent(mContext, WlanrebootActivity.class);
//			toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mContext.startActivity(toIntent);
//		}
		// 接收系统开机广播，POS重启回连蓝牙底座
		if ((action.equals(ACTION_BOOT_COMPLETED)||action.equals(ACTION_COMMING_NOISY))&&sp.getBoolean("isReboot", false)) 
		{
			Intent toIntent = new Intent(mContext, BtBackActivity.class);
			toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(toIntent);
		}
		// 接收系统开机广播,pos安全测试 add by 20190201 zhengxq
		if ((action.equals(ACTION_BOOT_COMPLETED)||action.equals(ACTION_COMMING_NOISY))&&sp.getBoolean("sec34", false)) 
		{
			key_location_test(mContext);
		}
		//开机获取TUSN add by 20190628 zsh
		if ((action.equals(ACTION_BOOT_COMPLETED)||action.equals(ACTION_COMMING_NOISY))&&sp.getBoolean("other19", true)) 
		{
			getTUSNtest(mContext);
		}
		
	}
	/**开机获取TUSN测试*/
	private void getTUSNtest(Context context){
		int ret;
		EM_SYS_HWINFO emflag=EM_SYS_HWINFO.SYS_HWINFO_GET_POS_TUSN;
		byte[]tusn=new byte[100];
		if((ret = JniNdk.JNI_Sys_GetPosInfo(15, 100,tusn))!=NDK_OK)
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "getTUSNtest", "line %d:获取TUSN失败(%d)", Tools.getLineInfo(), ret);
			Toast.makeText(context, "获取TUSN失败:ret=%d"+ret, Toast.LENGTH_LONG).show();
			mEditor.putBoolean("other19", false);
			mEditor.commit();
			return;
		}else{
			Toast.makeText(context, "获取TUSN成功", Toast.LENGTH_LONG).show();
			Log.d("TUSN=",Arrays.toString(tusn));
		}
		mEditor.putBoolean("other19", false);
		mEditor.commit();
		
	}
	/**密钥存储位置测试*/
	private void key_location_test(Context context)
	{
		int bak,cnt,ret;
		byte[] pszKeyOwner = new byte[32];
		byte[] flag = new byte[2];
		cnt = sp.getInt("sec_test", 0);
		bak = sp.getInt("sec_cnt", 0);
		String location = sp.getString("owner", "");
		int flag_sp = sp.getInt("flag", 10);
		cnt--;
		mEditor.putInt("sec_test", cnt);
		mEditor.commit();
		if((ret = JniNdk.JNI_Sec_GetMposKeyOwner(pszKeyOwner, flag))!=0)
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "key_location_test", "line %d:第%d次:获取密钥值失败(%d)", Tools.getLineInfo(),bak-cnt, ret);
			Toast.makeText(context, "获取密钥位置错误:"+ret, Toast.LENGTH_LONG).show();
			return;
		}
		String realOwn;
		if(!(realOwn=ISOUtils.byteToStr(pszKeyOwner)).equals(location)||flag_sp!=flag[0])
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "key_location_test", "line %d:第%d次:密钥位置值比较失败(owner:%s,preOwner:%s,flag:%d,sp_flag:%d)", Tools.getLineInfo(), bak-cnt, realOwn,location,flag[0],flag_sp);
			Toast.makeText(context, "比较密钥位置值错误:"+realOwn, Toast.LENGTH_LONG).show();
			return;
		}
		gui.cls_only_write_msg("BootBroadcastReceiver", "key_location_test", "第%d次重启获取密钥位置测试通过(owner=%s,flag=%d)", bak-cnt,realOwn,flag[0]);
		Toast.makeText(context, (bak-cnt)+"重启获取密钥位置测试通过", Toast.LENGTH_LONG).show();
		if(cnt>0)
		{
			PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
			pm.reboot(null);
		}
		else
		{
			mEditor.putBoolean("sec34", false);
			mEditor.commit();
		}
	}
	
	final Handler uiHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case 0:
				showDialogTip(mContext, (String) msg.obj);
				break;

			default:
				break;
			}
		};
	};
	
	class SecLoadRun implements Runnable
	{

		@Override
		public void run() 
		{
			int cnt=0,succ=0,countErr=0;
			int iRet;
			int[] modes = {ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg()};
			// 安装密钥->校验KCV,如果出现校验失败的情况,退出全部的线程
			while(gIsExist==false)
			{
				cnt++;
				int index = (int) (Math.random()*2);
				gui.cls_only_write_msg("BootBroadcastReceiver", "onReceive",System.currentTimeMillis()+"Sec第%d次测试,已成功%d次", cnt,succ);
				// 安装密钥允许返回20次的-1311
				// DES 
				int work_mode = (int) (Math.random()*4+1);
				// 密钥索引改成随机
				int keyIndex = 10;
				if((iRet=ndk_install_key(cnt,16, keyIndex, modes[index],work_mode))!=0)
				{
					LoggerUtil.d("DES1_iRet:"+iRet);
					if(iRet==-1302&&countErr<20)
					{
						gIsExist=true;
						// 退出的时候弹框
						Message msg = Message.obtain();
						msg.what=0;
						msg.obj="安装密钥失败:"+iRet;
						uiHandler.sendMessage(msg);
						return;
					}
				}
				else//校验DES的KCV
				{
					LoggerUtil.d("DES GETKCV");
					if((iRet=getKcv(cnt,16,work_mode,modes[index], 10))!=0)
					{
						if(iRet==-1302)
						{
							gIsExist=true;
							// 退出的时候弹框
							Message msg = Message.obtain();
							msg.what=0;
							msg.obj="安装密钥失败:"+iRet;
							uiHandler.sendMessage(msg);
							return;
						}

					}
				}
				succ++;
			}
		}
	}
	
	/**
	 * algMode:
	 * 0:DES算法
	 * (1<<6):SM4算法
	 * :AES算法
	 * 
	 */
	private int ndk_install_key(int cnt,int keyLen,int keyIndex,int algMode,int work_mode)
	{			
		LoggerUtil.d("ndk_install_key:"+algMode);
//		int keyLen = mKeyLenIndex==0?8:mKeyLenIndex==4?16:24;
		int index = keyLen/8==1?0:keyLen/8==2?4:8;
		byte[] loadKey = new byte[keyLen];
//		gui.cls_printf(String.format("即将安装索引%d的主密钥、pin密钥、mac密钥、data密钥", keyIndex).getBytes());
		kcvInfo.nCheckMode = 0;
		int iRet=-1;
		int retValue=0;
		Random random = new Random();
		
		// 安装索引密钥全部修改为随机
		random.nextBytes(loadKey);
		if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(0|algMode), (byte)(1|algMode), (byte)1, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=0)
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d安装TMK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
			retValue = iRet;
			return retValue;
		}
		// TPK 
		random.nextBytes(loadKey);
		if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(work_mode|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=0)
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d安装工作密钥失败(mode=%d,%d)", Tools.getLineInfo(),cnt,algMode,work_mode,iRet);
			retValue = iRet;
			return retValue;
		}
//		// TAK
//		random.nextBytes(loadKey);
//		if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(3|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=0)
//		{
//			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d安装TAK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			retValue = iRet;
//			return retValue;
//		}
//		// TDK
//		random.nextBytes(loadKey);
//		if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(4|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=0)
//		{
//			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d安装TDK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			retValue = iRet;
//			return retValue;
//		}
		return retValue;
	}
	
	public int getKcv(int cnt,int keyLen,int keyMode,int algMode,int keyIndex)
	{
		kcvInfo.nCheckMode = 1;
		int iRet=-1;
		int index = keyLen/8==1?0:keyLen/8==2?4:8;
		int kcvIndex;
		if(algMode==0||algMode==(1<<7))
			kcvIndex=2;
		else
			kcvIndex=3;
		// 主密钥
		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(1|algMode), (byte)keyIndex, kcvInfo))!=0)
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d获取TMK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			gIsExist=true;
			return iRet;
		}
		// pin密钥
		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(keyMode|algMode), (byte)keyIndex, kcvInfo))!=0)
		{
			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d获取TPK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			gIsExist=true;
			return iRet;
		}
//		// mac密钥
//		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?3:(3|algMode)), (byte)keyIndex, kcvInfo))!=0)
//		{
//			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d获取TAK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
////			gIsExist=true;
//			return iRet;
//		}
//		// data密钥
//		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?4:(4|algMode)), (byte)keyIndex, kcvInfo))!=0)
//		{
//			gui.cls_only_write_msg("BootBroadcastReceiver", "ndk_install_key", "line %d:第%d次:%d获取TDK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
////			gIsExist=true;
//			return iRet;
//		}m 
		return 0;
	}
	
	// add by 20150306
	// wifi选择
	public void showDialogTip(Context context,String tip)
	{
		TextView tvTip = new TextView(context);
		tvTip.setText(tip);
		BaseDialog dialog=new BaseDialog(context,tvTip,"错误提示","确定","取消",new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View View, boolean isPositive) {
				
			}
		});
		dialog.show();
	}
}
