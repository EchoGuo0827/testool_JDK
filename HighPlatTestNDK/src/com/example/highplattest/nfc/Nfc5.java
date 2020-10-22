package com.example.highplattest.nfc;

import java.io.IOException;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import com.example.highplattest.activity.IntentActivity.OnNfcCallBack;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: NFC模块
 * file name 		: Nfc5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180403 
 * directory 		: 
 * description 		: Android原生的对A、B卡取随机数，对M1卡读写操作(此种方式为Android4.4之前的调用方式)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq	   	 	20180403 	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Nfc5 extends UnitFragment implements OnNfcCallBack{
	
	private final String TESTITEM = "4.4之前NFC操作";
	private Gui gui = new Gui(myactivity, handler);
	private NfcAdapter mNfcAdapter;
	private String fileName="Nfc5";
	public void nfc5()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"nfc5",gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		long startTime = System.currentTimeMillis();
		
		/*process body*/
		gui.cls_printf("请放置射频B卡".getBytes());
		LoggerUtil.d(fileName+",nfc5==wait card");
		synchronized (g_lock) {
			try {
				g_lock.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LoggerUtil.d(fileName+",nfc5==end card");
		
		if(Tools.getStopTime(startTime)>=10)
			gui.cls_show_msg1_record(fileName,"nfc5",gKeepTimeErr,"未监听到NFC卡放置(长按确认键退出测试)");
		else
			gui.cls_show_msg1_record(fileName,"nfc5",gScreenTime, "%s测试通过(长按确认键退出测试)",TESTITEM);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LoggerUtil.e("nfc5 enter onResume");
		// 开启nfc的操作
		enableSystemNfcMessage();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LoggerUtil.e("nfc5 enter onPause");
		if(mNfcAdapter!=null)
		{
			// 关闭nfc检测
			mNfcAdapter.disableForegroundDispatch(myactivity);
		}
	}
	
	/**
	 * 开放检测NFC卡
	 * @return
	 */
	public boolean enableSystemNfcMessage()
	{
        try {
            if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) 
            {
                return false;
            }
            LoggerUtil.e("enableSystemNfcMessage");
            PendingIntent mPendingIntent = PendingIntent.getActivity(myactivity, 0, new Intent(myactivity, myactivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            mNfcAdapter.enableForegroundDispatch(myactivity, mPendingIntent, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	

	@Override
	public void onTestUp() {
		LoggerUtil.e("onTestUp------");
		mNfcAdapter = NfcAdapter.getDefaultAdapter(myactivity);
	}

	@Override
	public void onTestDown() {
	}


	@Override
	public void nfcStart(Intent intent) 
	{
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String[] techList = tag.getTechList();
		
		LoggerUtil.e("nfcStart->techList[0]"+techList[0]);
		if(techList!=null)
		{
	        if(techList[0].contains("NfcB"))// Type_B卡
	        {
	        	NfcB nfcB = NfcB.get(tag);
	        	try 
	        	{
					nfcB.connect();
					byte[] backBuf = nfcB.transceive(req);
					if(backBuf.length==10)// 支持取随机数
					{
						gui.cls_show_msg1(1,"Type_B取随机数成功");
					}
					else if(backBuf.length==2)
					{
						gui.cls_show_msg1(1,"Type_B不支持取随机数");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        else if(techList[0].contains("NfcA"))// Type_A卡
	        {
	        	NfcA nfcA = NfcA.get(tag);
	        	try 
	        	{
	        		nfcA.connect();
					byte[] backBuf = nfcA.transceive(req);
					if(backBuf.length==10)// 支持取随机数
					{
						gui.cls_show_msg1(1,"Type_A取随机数成功");
					}
					else if(backBuf.length==2)
					{
						gui.cls_show_msg1(1,"Type_A不支持取随机数");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
		synchronized (g_lock) {
			g_lock.notify();
		}
		LoggerUtil.d(fileName+",nfcStart==detect card");
	}

}
