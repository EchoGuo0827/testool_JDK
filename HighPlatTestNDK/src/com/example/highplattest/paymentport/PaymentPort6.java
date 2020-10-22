package com.example.highplattest.paymentport;

import java.io.File;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import java.util.Arrays;

import android.content.Intent;
import android.net.Uri;
import android.newland.NlManager;
import android.newland.content.NlIntent;
import android.util.Log;
/************************************************************************
 * 
 * module 			: Android系统和支付模块通信串口
 * file name 		: PaymentPort6.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141210 
 * directory 		: 
 * description 		: 测试Android系统和支付模块通信串口connect函数测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  huangjianb		20141210	 	created
 *
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort6 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String CLASS_NAME = PaymentPort6.class.getSimpleName();
	private NlManager nlManager = null;
	private final int MAXWAITTIME = 30;
	private final int MAX_SIZE = 1024;
	private String TESTITEM = "通信串口connect函数";
	private Gui gui = new Gui(myactivity, handler);
	
	public void paymentport6()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName = "paymentport6";
		//实例化接口对象
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		String Apptest = GlobalVariable.sdPath+"apk/App.apk";
		boolean tmp = false, thp = false;
		int ret = -1;
		int pszAttr = BpsBean.bpsValue;
		
		//对1k数据初始化,并计算LRC值
		byte[] sendBuf =new byte[MAX_SIZE - 2];
		byte[] recvBuf = new byte[MAX_SIZE];
		sendBuf[0] = 0x02; sendBuf[1] = 0x10; sendBuf[2] = 0x17; sendBuf[3] = 0x1D;
		sendBuf[4] = 0x0A; sendBuf[5] = 0x2F; sendBuf[6] = 0x01; sendBuf[7] = 0x10;
		sendBuf[8] = 0x11; sendBuf[sendBuf.length-2] = 0x03; sendBuf[sendBuf.length-1] = 0x00 ;
		for (int i = 0; i < sendBuf.length - 11; i++) 
		{
			sendBuf[9 + i] = (byte) (Math.random() * 256);
		}
		for (int i = 0; i < sendBuf.length - 2; i++) 
		{
			sendBuf[sendBuf.length - 1] = (byte) (sendBuf[sendBuf.length - 1] ^ sendBuf[1 + i]);
		}
		
		

		/* process body */
		gui.cls_show_msg1(2,"%s测试中...",TESTITEM);
		gui.cls_show_msg("请确保测试App.apk在Android端的/mnt/sdcard/apk/路径下后,短接PINPAD串口的23脚,完成点【确认】继续");
		
		//case1:设置成抢占情况下连接测试,预期允许被抢占连接
		if ((tmp = nlManager.connect(false)) != true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:建立串口连接失败(%s)", Tools.getLineInfo(),tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 非阻塞情况下自发自收
		if ((ret = nlManager.setconfig(pszAttr, 0, "8N1NN".getBytes())) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//串口无数据预期应该读不到数据
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口测试失败(预期=%d,实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 清空接收缓存区
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/(BpsBean.bpsId+1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(预期=%d,实际=%d)", Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断是否收发一致
		if ((ret-2) != sendBuf.length ||!Tools.byteCompare( sendBuf, recvBuf, 10,sendBuf.length - 1, 12,recvBuf.length - 1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:串口读写数据校验失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		for (int i = 0; i < sendBuf.length - 1; i++) 
		{
			Log.e("sendBuf" + i, " " + sendBuf[i]);
			
		}
		for(int i = 0;i<recvBuf.length - 1;i++)
		{
			Log.e("recvBuf" + i, recvBuf[i] + "");
		}
		
		//case2:抢占测试
		//安装测试apk
		Intent intent = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(Apptest)),
				"application/vnd.android.package-archive");
		myactivity.startActivity(intent);
		
		//预期成功
		if (gui.ShowMessageBox("判断后台程序是否运行成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
//		//case3:被应用抢占后预期读写失败
//		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME
//				/ (BpsSetting.bpsId + 1))) != PAYMENT_PORT_BE_OTHER_OCCUPY)
//		{
//			postEnd(getLineInfo(), ret);
//		}

		if ((tmp = nlManager.connect(true)) != true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d：串口建立连接失败(%s)", Tools.getLineInfo(),tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		//抢占模式结束断开连接
		if ((ret = nlManager.disconnect()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口连接失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		
		// case4:设置成独占情况下连接测试,预期不允许被抢占连接
		if ((tmp = nlManager.connect(true)) != true) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:建立串口连接成功(%s)", Tools.getLineInfo(),tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.setconfig(pszAttr, 0, "8N1NN".getBytes())) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:串口配置测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//对串口进行写操作
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME / (BpsBean.bpsId + 1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(预期=%d,实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//清空接收缓存区,读操作
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:从串口读数据失败(预期=%d,实际=%d)", Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断是否收发一致
		if ((ret-2) != sendBuf.length ||!Tools.byteCompare( sendBuf, recvBuf, 10,sendBuf.length - 1, 12,recvBuf.length - 1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:串口读写校验数据失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
			{
				nlManager.disconnect();
				return;
			}
		}
		
		//case5:预期抢占失败
		//安装测试apk
		Intent intent1 = new Intent(NlIntent.ACTION_VIEW_HIDE);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent1.setDataAndType(Uri.fromFile(new File(Apptest)),
				"application/vnd.android.package-archive");
		myactivity.startActivity(intent1);
		
		//预期失败,实际成功了
		if (gui.ShowMessageBox("判断后台程序是否运行成功".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,thp);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case6:抢占失败后原应用预期还能进行正常的读写操作
		if ((ret = nlManager.setconfig(pszAttr, 0, "8N1NN".getBytes())) != ANDROID_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d：串口配置失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//对串口进行写操作
		if ((ret = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME / (BpsBean.bpsId + 1))) != sendBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口失败(预期=%d,实际=%d)", Tools.getLineInfo(),sendBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//清空接收缓存区,读操作
		Arrays.fill(recvBuf, (byte) 0);
		if ((ret = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != recvBuf.length) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据失败(预期=%d,实际=%d)", Tools.getLineInfo(),recvBuf.length,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		// 判断是否收发一致
		if ((ret-2) != sendBuf.length ||!Tools.byteCompare( sendBuf, recvBuf, 10,sendBuf.length - 1, 12,recvBuf.length - 1))
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:串口读写数据比较失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case7:预期抢占失败
		if ((tmp = nlManager.connect(false)) != false) //true
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口失败(%s)", Tools.getLineInfo(),tmp);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((ret = nlManager.disconnect()) != ANDROID_OK)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口连接失败(%s)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		} 
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(nlManager!=null)
			nlManager.disconnect();
		gui = null;
		nlManager = null;
	}
}
