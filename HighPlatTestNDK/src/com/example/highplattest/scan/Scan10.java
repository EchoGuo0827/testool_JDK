package com.example.highplattest.scan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.BitmapDeal;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan10.java 
 * Author 			: zhangxj
 * version 			: 
 * DATE 			: 20160323
 * directory 		: 软解码释放扫码，即中断扫码操作
 * description 		: release()
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxj		   20160328     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan10 extends UnitFragment
{
	private String fileName=Scan10.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+软)release";
	final int MAXTAPTIME = 200;
	private String result;
	public TextView mtvShow;
	public ImageView linShow;
	boolean threadFlag = false;
	long time;
	private Gui gui = new Gui(myactivity, handler);
	private String[] scanModeStr = {"zxing旧接口","Nls兼容zxing扫码","Nls自动识别扫码"};
	
	private ScanDefineInfo mScanDefineInfo;
	public void scan10()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan10", gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		mScanDefineInfo = getCameraInfo();
		while(true)
		{	
			int nkeyIn = gui.cls_show_msg("扫码配置\n2.自动识别接口测试自动识别扫码\n");//0.zxing旧接口\n1.自动识别接口兼容zxing扫码\n
			switch (nkeyIn) 
			{
			/**不需测试Zxing旧接口*/
			/*case '0':
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
				{
					gui.cls_show_msg("A9平台不支持zxing旧接口测试");
					break;
				}
				scan10Test(scanModeStr[0],Scan_Mode.ZXING);
				break;*/
				
			/*case '1':
				scan10Test(scanModeStr[1],Scan_Mode.NLS_0);
				break;*/
				
			case '2':
				scan10Test(scanModeStr[2],Scan_Mode.NLS_1);
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	/**
	 * NLS、ZXING及兼容，修改20180123
	 * @throws IOException
	 */
	public void scan10Test(String scanModeStr,Scan_Mode scan_Mode) 
	{
		/*private & local definition*/
		// 获取扫描的工具包
		String urlpath = GlobalVariable.sdPath+"scan/correct.PNG";
		int ret = -1,cameraId=0;
		
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		
		cameraId = scanInfo.getCameraId();
		if(cameraId==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		gui.cls_show_msg1(2, "%s,%s测试中...", TESTITEM,scanModeStr);
		if(new File(urlpath).exists()==false)
		{
			gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr, "line %d:未放置测试文件,请先放置测试文件",Tools.getLineInfo());
			return;
		}
		
		releaseScan();
		try {
			//case0:传入错误的摄像头应该返回失败
			if (scan_Mode==Scan_Mode.NLS_1) {
				gui.cls_show_msg1(2,"验证传入错误的摄像头应该返回失败------请注意日志是否有打开失败的提示.A7和A9平台有该导入。A5仍有纠错功能");
				scan_Domestic = new ScanUtil(myactivity, surfaceView, 20, true, 30,1);
				
			}
			releaseScan();
			//case1:释放后再次扫码无法扫码
			initScanMode(scan_Mode,myactivity, null, cameraId, true, 15*1000);
			if(gui.cls_show_msg("请将条形码或二维码放在%s摄像头20-30cm处,[其他]完成,[取消]退出",tipMsg)==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret = %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
			
			result = (String)scan_Domestic.doScan();
			
			// 释放后扫码结果应为null
			if(result!=null)
			{
				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%释放过后依然能扫码,释放测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
			//case2:释放后重新初始化应能扫码成功
			initScanMode(scan_Mode,myactivity, null, cameraId, true, 61000);
			if(gui.cls_show_msg("已经重新初始化,请将条形码或二维码放在%s摄像头20-30cm处,[其他]完成,[取消]退出",tipMsg)==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret = %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
			
			//case3:后置摄像头扫码过程中释放扫码，释放后扫码动作被释放，不能扫码正确
			initScanMode(scan_Mode,myactivity, null, cameraId, true, 15000);
			if(gui.cls_show_msg("请将条形码或二维码移开,[其他]完成,[取消]退出") == ESC)
				return;
			new Thread()
			{
				public void run() 
				{
					threadFlag = true;
					long startTime = System.currentTimeMillis();
					result = (String) scan_Domestic.doScan();
					time = System.currentTimeMillis()-startTime;
				};
			}.start();
			while(!threadFlag);
			threadFlag = false;
			SystemClock.sleep(1000);
			releaseScan();
			if(gui.cls_show_msg("请将条形码或二维码放在%s摄像头20-30cm处,[其他]完成,[取消]退出",tipMsg)==ESC)
				return;
			if(result==null||!result.startsWith("F"))
			{
				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr, "line %d:%s中断失败(%s)", Tools.getLineInfo(),TESTITEM,result==null?"null":result);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case4:扫码释放摄像头不影响图片解析功能 ,F7不支持图片解析和A7都不支持该方法
			if (GlobalVariable.gCurPlatVer==Platform_Ver.A5) {
				InputStream netIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(urlpath));
				result =ScanUtil.doScan(netIn);
				if(result==null||!result.equals("285614263499135991"))
				{
					gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%s扫码失败,预期码值：285614263499135991,实际码值：%s", 
							Tools.getLineInfo(),TESTITEM,result==null?"null":result);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
//			InputStream netIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(urlpath));
//			result =ScanUtil.doScan(netIn);
//			if(result==null||!result.equals("285614263499135991"))
//			{
//				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%s扫码失败,预期码值：285614263499135991,实际码值：%s", 
//						Tools.getLineInfo(),TESTITEM,result==null?"null":result);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
			
			// case5:超时后未释放应能正常扫码
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			initScanMode(scan_Mode,myactivity, surfaceView, cameraId, true, 15*1000);
			if(gui.cls_show_msg("将条形码/二维码在%s摄像头移开,请耐心等待超时时间15s,[其他]完成,[取消]退出",tipMsg)==ESC)
				return;
			result = (String)scan_Domestic.doScan();
			// 据徐昊反馈该点超时返回F无问题，经过逻辑判断此点扫码超时应返回F
			if(result==null||!result.startsWith("F"))
			{
				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%s扫码失败(%s)", Tools.getLineInfo(),TESTITEM,result==null?"null":result.substring(1));
				if(!GlobalVariable.isContinue)
					return;
			}
			if(gui.cls_show_msg("请将条形码或二维码放在%s摄像头20-30cm处,[其他]完成,[取消]退出",tipMsg)==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"line %d:%s扫码测试失败(ret = %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan();

			// case6:初始化后立即释放不应出错 add by 20170925 xuess 
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			gui.cls_show_msg("以下进行初始化后立即释放,请注意观察,不应出错造成用例闪退,任意键继续");
			if(!(mScanDefineInfo.cameraReal.get(FONT_CAMERA)!=-1&&mScanDefineInfo.getCameraCnt()>=2)){
				LoggerUtil.d("该产品带预览画面");
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			}

			initScanMode(scan_Mode,myactivity, surfaceView, cameraId, true, 15000);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			SystemClock.sleep(3000);
			releaseScan();
			
			
//			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			
			gui.cls_show_msg1_record(fileName, "scan10Test", gScreenTime,"%s,%s测试通过", TESTITEM,scanModeStr);
		} catch (NoClassDefFoundError e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"%s抛出NoClassDefFoundError异常", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"%s抛出NoSuchMethodError异常", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan10Test", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
		gui = null;
	}
}
