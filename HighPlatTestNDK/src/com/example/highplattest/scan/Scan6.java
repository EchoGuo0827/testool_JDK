package com.example.highplattest.scan;

import android.annotation.SuppressLint;
import android.view.View;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan6.java 
 * Author 			: zhangxj
 * version 			: 
 * DATE 			: 20160323
 * directory 		:  可设置预览模式，前后摄像头，解码成功提示音，超时时间
 * description 		: ScanUtil(Context context, SurfaceView surfaceView, int cameraId,  boolean soundEnable, int millisecond)
 * 					  ScanUtil(Context context, SurfaceView surfaceView, int cameraId,  boolean soundEnable, int millisecond, int engineID)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxj		   20160323     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan6  extends UnitFragment
{
	private String fileName=Scan6.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+软)scanlUtil";
	final int MAXTAPTIME = 200;
	private String result;
	private Gui gui;
	private String[] scanModeStr = {"zxing旧接口","Nls兼容zxing扫码","Nls自动识别扫码"};
//	int cameraId;
	int fontCameraId=-1;
	int backCameraId=-1;
	
	private ScanDefineInfo scanInfo=null;
	String fontTip="";
	String backTip="";

	/**
	 * 测试用例入口
	 */
	public void scan6() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan6", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/**测试前置，根据硬件配置码配置扫描头*/
		scanInfo = getCameraInfo();
		if(scanInfo.getCameraId()==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		initCameraConf();
		if(scanInfo.cameraReal.get(FONT_CAMERA)!=-1&&scanInfo.getCameraCnt()>=2)
		{
			LoggerUtil.d("scan6,前置无预览画面");
		}
			
		
		while(true)
		{
			// 将按钮设置为停止扫码
			//handler.sendMessage(handler.obtainMessage(HandlerMsg.SCAN_BTN_SET_TEXT, "停止扫码"));
			int nkeyIn;
			nkeyIn = gui.cls_show_msg("扫码配置\n2.自动识别接口测试自动识别扫码\n3.自动识别手动扫码模式\n");//0.zxing旧接口\n1.自动识别接口兼容zxing扫码\n
			switch (nkeyIn) 
			{
			/**zxing接口不需要测试 20200710*/
			/*case '0':
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
				{
					gui.cls_show_msg("A9平台不支持zxing旧接口测试");
					break;
				}
				scan6Test(scanModeStr[0],Scan_Mode.ZXING);
				break;*/
				
			/*case '1':
				scan6Test(scanModeStr[1],Scan_Mode.NLS_0);
				break;*/
				
			case '2':
				scan6Test(scanModeStr[2],Scan_Mode.NLS_1);
				break;
				
			case '3':
				NlsManual();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	private void initCameraConf()
	{
		if(scanInfo.cameraReal.get(FONT_CAMERA)!=-1)
		{
			fontCameraId = scanInfo.cameraReal.get(FONT_CAMERA);
			fontTip="前置摄像头";
		}
		else if(scanInfo.cameraReal.get(USB_CAMERA)!=-1)
		{
			fontCameraId = scanInfo.cameraReal.get(USB_CAMERA);
			fontTip="USB摄像头";
		}
		// 如果F7的mini和人脸摄像头都存在呢
		if(scanInfo.cameraReal.get(FONT_CAMERA)!=-1&&scanInfo.cameraReal.get(USB_CAMERA)!=-1)
		{
			fontCameraId=scanInfo.cameraReal.get(FONT_CAMERA);
			backCameraId=scanInfo.cameraReal.get(USB_CAMERA);
			fontTip="前置摄像头";
			backTip="USB摄像头";
		}
		
		if(scanInfo.cameraReal.get(BACK_CAMERA)!=-1)
		{
			backCameraId = scanInfo.cameraReal.get(BACK_CAMERA);
			backTip="后置摄像头";
		}
		else if(scanInfo.cameraReal.get(EXTERNAL_CAMERA)!=-1)
		{
			backCameraId = scanInfo.cameraReal.get(EXTERNAL_CAMERA);
			backTip="支付摄像头";
		}
	}
	
	/**
	 * NLS、ZXING及兼容，修改20180123
	 */
	public void scan6Test(String scanModeStr,Scan_Mode scan_Mode) 
	{
		/*private & local definition*/
		// 获取扫描的工具包
		long startTime;
		long time;
		int ret = -1;
		/*process body*/
		
		//F7无后置摄像头，后续测试camera id设置为后置摄像头的均不测
		/**有后置摄像头才添加预览画面,A9是前置的人脸识别*/
//		if(mIsPreview)
//			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		
		
		gui.cls_show_msg1(1, "%s测试中...",scanModeStr);
		gui.cls_show_msg("测试过程中机具不应进入休眠,请先将休眠时间设大,完成任意键继续");
		try 
		{
			//case1:初始化软解码，参数错误
			//case1.1:摄像头参数错误，有错误处理机制，摄像头参数错误默认为前置摄像头，可以正常扫码
			//920无前置摄像头 zhangxinj 2019/4/22   F7现在无错误处理机制。故不测该项 20200623
			//BUG2020043002914:A7 A9平台上目前无默认摄像头id。除了X5系列有明确的扫码头配置会设置外，其他无默认的cameraid
			if(GlobalVariable.gCurPlatVer==Platform_Ver.A5||GlobalVariable.currentPlatform==Model_Type.X5)
			{
				initScanMode(scan_Mode,myactivity, null,-1,true, 15000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在出错默认摄像头20-30cm处,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
						if(!GlobalVariable.isContinue)
							return;
				}
				releaseScan();
			}

			
			//case1.2:最小时间参数错误，有错误处理机制，默认为15秒，可以正常扫码
			if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, -1);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,fontTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, -1);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,backTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			
			//case1.3:最大时间参数错误，有错误处理机制，默认为15s，可以正常扫码
			if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, backCameraId, true, 61000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,backTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, 61000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,fontTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}

			
			/*// case1.4:context=null，错误处理机制，默认15s，可以正常扫码
			scanUtil = initScanMode(scan_Mode,null, null, 0, true, 15*1000,0);
			if((ret = scanDialog(scanUtil, "zxing软解，请将条形码或二维码放在后置摄像头20-30cm处","", gui))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1(g_keeptime, SERIAL,"line %d:%s扫码测试失败ret= %d，code = %s", Tools.getLineInfo(),TESTITEM,ret,codeResult);
				if(!GlobalVariable.isContinue)
				{
					scanUtil.release();
					return;
				}
			}
			scanUtil.release();*/
			
			// case2:初始化各种情况测试
			// case2.1:初始化为无界面预览，前置摄像头，解码成功有提示音，应扫码成功
			if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, 15000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,fontTip)==ESC)
					return;
				if((ret = scanDialog("解码成功应有提示音并且",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, backCameraId, true, 15000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,backTip)==ESC)
					return;
				if((ret = scanDialog("解码成功应有提示音并且",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			
			// case2.2:初始化为无界面预览，后置摄像头，解码成功无提示音，应扫码成功   
			if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, backCameraId, false, 15000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,backTip)==ESC)
					return;
				if((ret = scanDialog("解码成功无提示音并且",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, false, 15000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,fontTip)==ESC)
					return;
				if((ret = scanDialog("解码成功无提示音并且",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			
			// case2.3:初始化为无界面预览，前置摄像头，解码成功有提示音，应扫码成功
			if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, 15 * 1000);
				if(gui.cls_show_msg("%s,无预览界面请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,fontTip)==ESC)
					return;
				if((ret = scanDialog("解码成功应有提示音并且",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, backCameraId, true, 15 * 1000);
				if(gui.cls_show_msg("%s,无预览界面请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,backTip)==ESC)
					return;
				if((ret = scanDialog("解码成功应有提示音并且",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			

			// case2.4:初始化为有界面预览，后置摄像头，解码成功无提示音，应解码成功
			if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, backCameraId, false, 15000);
				if(gui.cls_show_msg("%s,有预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,backTip)==ESC)
					return;
				if((ret = scanDialog("解码成功无提示音",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, fontCameraId, false, 15000);
				if(gui.cls_show_msg("%s,有预览界面,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",scanModeStr,fontTip)==ESC)
					return;
				if((ret = scanDialog("解码成功无提示音",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			
			
			
			// case3:初始化时间测试
			// case3.1:超时时间设置3s
			if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, 3000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码移开,测试超时时间3秒,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				startTime = System.currentTimeMillis();
				result = (String) scan_Domestic.doScan();
				time = System.currentTimeMillis() - startTime;
				if (result==null||!result.startsWith("F") || time > 3000 + MAXTAPTIME|| time < 3000 - MAXTAPTIME) 
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s超时时间错误%s",Tools.getLineInfo(), TESTITEM,result==null?"null":result.substring(1));
					if (!GlobalVariable.isContinue) 
						return;
				}
				releaseScan();
			}
			else if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, null, backCameraId, true, 3000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码移开,测试超时时间3秒,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				startTime = System.currentTimeMillis();
				result = (String) scan_Domestic.doScan();
				time = System.currentTimeMillis() - startTime;
				if (result==null||!result.startsWith("F") || time > 3000 + MAXTAPTIME|| time < 3000 - MAXTAPTIME) 
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s超时时间错误%s",Tools.getLineInfo(), TESTITEM,result==null?"null":result.substring(1));
					if (!GlobalVariable.isContinue) 
						return;
				}
				releaseScan();
			}
			
			// case3.2:超时时间设置最大60s
			if(backCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, backCameraId, true, 60000);
				if(gui.cls_show_msg("%s,有预览界面,请将条形码或二维码移开,测试超时时间60秒,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				startTime = System.currentTimeMillis();
				
				result = (String) scan_Domestic.doScan();
				time = System.currentTimeMillis() - startTime;
				if (!result.startsWith("F") | time > 60000 + MAXTAPTIME| time < 60000 - MAXTAPTIME) 
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s超时时间错误%s",Tools.getLineInfo(), TESTITEM,result==null?"null":result.substring(1));
					if (!GlobalVariable.isContinue) 
						return;
				}
				releaseScan();
			}
			else if(fontCameraId!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, fontCameraId, true, 60000);
				if(gui.cls_show_msg("%s,有预览界面,请将条形码或二维码移开,测试超时时间60秒,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				startTime = System.currentTimeMillis();
				
				result = (String) scan_Domestic.doScan();
				time = System.currentTimeMillis() - startTime;
				if (!result.startsWith("F") | time > 60000 + MAXTAPTIME| time < 60000 - MAXTAPTIME) 
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s超时时间错误%s",Tools.getLineInfo(), TESTITEM,result==null?"null":result.substring(1));
					if (!GlobalVariable.isContinue) 
						return;
				}
				releaseScan();
			}
			
			
			// case4:选择前置摄像头而将条码放置与后置摄像头，应扫码超时    F7只测一个人脸识别摄像头，故不测这类项
			if(fontCameraId!=-1&&scanInfo.getCameraCnt()>1)
			{
				initScanMode(scan_Mode,myactivity, null, fontCameraId, true, 15*1000);
				if(gui.cls_show_msg("%s,无预览界面,请将条形码或二维码放置于后置摄像头,应超时,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				startTime = System.currentTimeMillis();
				
				result = (String)scan_Domestic.doScan();
				time = System.currentTimeMillis() - startTime;
				if (!result.startsWith("F") | time > 15000 + MAXTAPTIME| time < 15000 - MAXTAPTIME) 
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s超时时间错误%s",Tools.getLineInfo(), TESTITEM,result==null?"null":result.substring(1));
					if (!GlobalVariable.isContinue) 
						return;
				}
				releaseScan();
			}
			// case5:选择后置摄像头而将条码放置与前置摄像头，应扫码超时F7暂时不测这个
			if(backCameraId!=-1&&scanInfo.getCameraCnt()>1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, backCameraId, true, 15*1000);
				if(gui.cls_show_msg("%s,有预览界面,请将条形码或二维码放置于前置摄像头,应超时,[其他]完成,[取消]退出",scanModeStr)==ESC)
					return;
				startTime = System.currentTimeMillis();
			
				result = (String)scan_Domestic.doScan();
				time = System.currentTimeMillis() - startTime;
				if (!result.startsWith("F") | time > 15000 + MAXTAPTIME| time < 15000 - MAXTAPTIME) 
				{
					gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"line %d:%s超时时间错误%s",Tools.getLineInfo(), TESTITEM,result==null?"null":result.substring(1));
					if (!GlobalVariable.isContinue) 
						return;
				}
				releaseScan();
			}
			
			gui.cls_show_msg1_record(fileName, "scan6Test", gScreenTime,"%s测试通过",scanModeStr);
		} 
		// 退出的时候记得要对扫码进行释放
		catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			releaseScan();
			gui.cls_show_msg1_record(fileName, "scan6Test", gKeepTimeErr,"抛出%s异常", e.getMessage());
		}
	}
	
	
	/**
	 * NLS的手动扫码测试，新增 20160923
	 */
	public void NlsManual()
	{
		/*private & local definition*/
		// 获取扫描的工具包
		int ret = -1;
//		int cameraId = 0;
		
		/*process body*/
		/**支持后置摄像头或F7的前置摄像头均带有预览画面*/
//		if(mIsPreview)
//			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		gui.cls_show_msg("测试过程中机具不应进入休眠,请先将休眠时间设大,完成任意键继续");
		try 
		{
			// case1:摄像头参数错误，有错误处理机制，摄像头参数错误默认为前置摄像头，可以正常扫码
			// BUG2020043002914 F7和F10现在无错误处理机制。故不测该项 
			if(GlobalVariable.gCurPlatVer==Platform_Ver.A5||GlobalVariable.currentPlatform==Model_Type.X5)
			{
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, -1, true, 0);
				if(gui.cls_show_msg("扫码参数异常测试:无预览界面,请将条形码或二维码放在前置摄像头20-30cm处,[其他]完成,[取消]退出")==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			// case2.1:设置无预览前置摄像头扫码，有提示音，应正常
			// case2.3:设置有预览前置摄像头扫码，无提示音，应正常
			if(fontCameraId!=-1)
			{
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, fontCameraId, true, 0);
				if(gui.cls_show_msg("扫码测试:无预览界面,有提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",fontTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
				
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, fontCameraId, false, 0);
				if(gui.cls_show_msg("扫码测试:无前置预览界面,无提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",fontTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(backCameraId!=-1)
			{
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, backCameraId, true, 0);
				if(gui.cls_show_msg("扫码测试:无预览界面,有提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",backTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
				
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, backCameraId, false, 0);
				if(gui.cls_show_msg("扫码测试:无后置预览界面,无提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",backTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			// case2.2:设置无预览后置摄像头扫码，有提示音，应正常
			// case2.4:设置有预览后置摄像头扫码，无提示音，应正常
			if(backCameraId!=-1)
			{
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, backCameraId, true, 0);
				if(gui.cls_show_msg("扫码测试:无预览界面,有提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",backTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
				
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, surfaceView, backCameraId, false, 0);
				if(gui.cls_show_msg("扫码测试:有后置预览界面,无提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",backTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else if(fontCameraId!=-1)
			{
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, null, fontCameraId, true, 0);
				if(gui.cls_show_msg("扫码测试:无预览界面,有提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",fontTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
				
				initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, surfaceView, fontCameraId, false, 0);
				if(gui.cls_show_msg("扫码测试:有后置预览界面,无提示音,请将条形码或二维码放在%s20-30cm处,[其他]完成,[取消]退出",fontTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}

			String camTip = scanInfo.getCameraInfo();
			int cameraId = scanInfo.getCameraId();
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			// case3.1:一分钟未放置条码不应扫码超时失败，放置条码应能扫到条码
			initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, surfaceView, cameraId, true, 0);
			if(gui.cls_show_msg("扫码测试:将条码移开,一分钟后放置条码于%s摄像头,[其他]完成,[取消]退出",camTip)==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan();

			// case3.2:一分钟未放置条码不应扫码超时失败，调用stopScan结束扫码动作，返回值为null，软硬解手动扫码统一返回null add zhengxq 20161213
			initScanMode(Scan_Mode.ZXING_MANUALLY, myactivity, surfaceView, cameraId, true, 0);
			// 预期返回null
			if(gui.cls_show_msg("扫码测试:将条码移开,一分钟后长按[取消]停止扫码,[其他]完成,[取消]退出")==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_COTINUE_NULL)
			{
				gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
				releaseScan();
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//测试后置关闭预览
			releaseScan();
			gui.cls_show_msg1_record(fileName, "NlsManual", gScreenTime,"NLS的手动扫码测试通过");
		} 
		catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "NlsManual", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	
	@Override
	public boolean onLongClick(View view) 
	{
		super.onLongClick(view);
		switch (view.getId()) 
		{
		case R.id.btn_key_esc:
			new Thread()
			{
				public void run() 
				{
					stopScan();
				};
			}.start();
			break;

		default:
			break;
		}
		return true;
	}



	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
	}
}
