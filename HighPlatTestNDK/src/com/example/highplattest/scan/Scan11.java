package com.example.highplattest.scan;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.CalRect;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan11.java 
 * Author 			: zhangxj
 * version 			: 
 * DATE 			: 20160510
 * directory 		: 设置软解码解析框的大小
 * description 		: setDecodeScreenResolution(Rect decodeRect)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20160510     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan11 extends UnitFragment 
{
	private final String TESTITEM =  "(ScanUtil+软)setDecodeScreenResolution";
	private String fileName=Scan11.class.getSimpleName();
	private Rect rect;
	private Gui gui;
	private String[] scanModeStr = {"zxing旧接口","Nls兼容zxing扫码","Nls自动识别扫码"};
	private ScanDefineInfo mScanDefineInfo;
	private int mCameraId;
	
	@SuppressLint("NewApi")
	public void scan11() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan11", gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		mScanDefineInfo = getCameraInfo();
		mCameraId = mScanDefineInfo.getCameraId();
		while(true)
		{	
			int nkeyIn = gui.cls_show_msg("扫码配置\n2.Nls自动识别扫码\n");//0.zxing旧接口\n1.自动识别接口兼容zxing扫码\n
			switch (nkeyIn) 
			{
			/**zxing的接口不需测试 20200710*/
			/*case '0':
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
				{
					gui.cls_show_msg("A9平台不支持zxing旧接口测试");
					break;
				}
				scan11Test(scanModeStr[0],Scan_Mode.ZXING);
				break;*/
				
			/*case '1':
				scan11Test(scanModeStr[1],Scan_Mode.NLS_0);
				break;*/
			case '2':
				scan11Test2(scanModeStr[2],Scan_Mode.NLS_1);
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	private void scan11Test2(String scanStr, Scan_Mode nls1) 
	{
		int ret = -1,cameraId=0;
		//scan11增加场景：自动识别软解码方式下，调用setDecodeScreenResolution设置解析框大小应无效，因为该设置接口只针对zxing，自动识别方式的解析框为全屏； by 20191014
		gui.cls_show_msg1(2, "%s测试中...下面解析框大小应该设置失败。应为全屏", scanStr);
//		// case4.2:设置解析框位于左上角，大小为屏幕的1/2，进行解析应成功
		
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		cameraId = scanInfo.getCameraId();
		if(cameraId==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		initScanMode(nls1,myactivity, surfaceView, cameraId, true, 15*1000);
		CalRect.RATIO = 1f/2;
		CalRect.leftOffset = 0;
		CalRect.topOffset = 0;
		rect = CalRect.getDecodeRect(myactivity);
		if(Tools.isRectBound(rect))
		{
			scanDecodeScreenResolution(rect);
			// 刷新界面
			if(gui.cls_show_msg("%s,有预览画面,解析框位于左上角,应只可在解析框内部解析,放置条码于%s摄像头15cm处,[其他]完成,[取消]退出", scanStr,tipMsg)==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "line %d:设置的值超出边界范围",Tools.getLineInfo());
			return;
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		if(gui.cls_show_msg("请确认解析框是否为全屏。。。。。是[确认],否[其他]")!=ENTER){
			gui.cls_show_msg1_record("scan11", "scan11Test2",5, "line %d:设置解析框成功", Tools.getLineInfo());
			return;
		}
	}

	/**
	 * ZXING、NLS兼容ZXING，修改20180123
	 * @throws IOException
	 */
	public void scan11Test(String scanModeStr,Scan_Mode scan_Mode) 
	{
		/*private & local definition*/
		int ret = -1;
		String cameraTip = mScanDefineInfo.getCameraInfo();
		/*process body*/
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		gui.cls_show_msg1(2, "%s测试中...", scanModeStr);
		int screenWidth = GlobalVariable.ScreenWidth;
		int screenHeight = GlobalVariable.ScreenHeight;
		try 
		{
			// case1:未设置解析框画面无解析框
			initScanMode(scan_Mode,myactivity, null, mCameraId, true, 15*1000);
			scanDecodeScreenResolution(CalRect.getDecodeRect(myactivity));
			if(gui.cls_show_msg("%s,无预览画面,放置条码于%s摄像头15cm处,[其他]完成,[取消]退出", scanModeStr,cameraTip)==ESC)
				return;
			if((ret = scanDialog( "",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
			
			// case2:rect = null，应全屏显示解析框
			initScanMode(scan_Mode,myactivity, surfaceView, mCameraId, true, 15*1000);
			scanDecodeScreenResolution(null);
			if(gui.cls_show_msg("%s,有预览画面,全屏幕均可解析,放置条码于%s摄像头15cm处,[其他]完成,[取消]退出", scanModeStr,cameraTip)==ESC)
				return;
			if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
				if (!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
			
			// case3:设置有预览界面，解析框大小设置为全屏应为全屏
			initScanMode(scan_Mode,myactivity, surfaceView, mCameraId, true, 15*1000);
			CalRect.RATIO = 1f;
			CalRect.leftOffset = 0;
			CalRect.topOffset = 0;
			rect = CalRect.getDecodeRect(myactivity);
			if(Tools.isRectBound(rect))
			{
				scanDecodeScreenResolution(rect);
				if(gui.cls_show_msg("%s,有预览画面,全屏幕均可解析,放置条码于%s摄像头15cm处,[其他]完成,[取消]退出", scanModeStr,cameraTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else
			{
				gui.cls_show_msg1(2, "line %d:设置的值超出边界范围(left=%d,right=%d,bottom=%d,top=%d)",Tools.getLineInfo(),rect.left,rect.right,rect.bottom,rect.top);
				return;
			}
			int fontCameraId;
			// case4.1:设置解析框位于正中，大小为屏幕的1/2，进行解析应成功
			if((fontCameraId=mScanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, fontCameraId, true, 15*1000);
				CalRect.RATIO = 1f/2;
				CalRect.leftOffset = (int) ((1f/4)*screenWidth);
				CalRect.topOffset = (int) ((1f/4)*screenHeight);
				rect = CalRect.getDecodeRect(myactivity);
				if(Tools.isRectBound(rect))
				{
					scanDecodeScreenResolution(rect);
					if(gui.cls_show_msg("%s,无预览画面,解析框位于正中,放置条码于前置摄像头15cm处,[其他]完成,[取消]退出", scanModeStr)==ESC)
						return;
					if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
					{
						gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
						if (!GlobalVariable.isContinue)
							return;
					}
					releaseScan();
				}
				else
				{
					gui.cls_show_msg1(2, "line %d:设置的值超出边界范围",Tools.getLineInfo());
					return;
				}
			}
			
			// case4.2:设置解析框位于左上角，大小为屏幕的1/2，进行解析应成功
			initScanMode(scan_Mode,myactivity, surfaceView, mCameraId, true, 15*1000);
			CalRect.RATIO = 1f/2;
			CalRect.leftOffset = 0;
			CalRect.topOffset = 0;
			rect = CalRect.getDecodeRect(myactivity);
			if(Tools.isRectBound(rect))
			{
				scanDecodeScreenResolution(rect);
				// 刷新界面
				if(gui.cls_show_msg("%s,有预览画面,解析框位于左上角,应只可在解析框内部解析,放置条码于%s摄像头15cm处,[其他]完成,[取消]退出", scanModeStr,cameraTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else
			{
				gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "line %d:设置的值超出边界范围",Tools.getLineInfo());
				return;
			}
			
			// case4.3:设置解析框大小位于右上角，大小为屏幕的1/2，进行解析应成功
			if((fontCameraId=mScanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, fontCameraId, true, 15*1000);
				CalRect.RATIO = 1f/2;
				CalRect.leftOffset = (int) (screenWidth*(1f/2));
				CalRect.topOffset = 0;
				rect = CalRect.getDecodeRect(myactivity);
				if(Tools.isRectBound(rect))
				{
					scanDecodeScreenResolution(rect);
					if(gui.cls_show_msg("%s,无预览画面,解析框位于右上角,应只可在解析框内部解析,放置条码于前置摄像头15cm处,[其他]完成,[取消]退出", scanModeStr)==ESC)
						return;
					if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
					{
						gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
						if (!GlobalVariable.isContinue)
							return;
					}
					releaseScan();
				}
				else
				{
					gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "line %d:设置的值超出边界范围",Tools.getLineInfo());
					return;
				}
			}
			
			// case4.4:设置解析框大小位于左下角，大小为屏幕的1/2，进行解析应成功
			initScanMode(scan_Mode,myactivity, surfaceView, mCameraId, true, 15*1000);
			CalRect.RATIO = 1f/2;
			CalRect.leftOffset = 0;
			CalRect.topOffset = (int) ((screenHeight)*(1f/2));
			rect = CalRect.getDecodeRect(myactivity);
			if(Tools.isRectBound(rect))
			{
				scanDecodeScreenResolution(rect);
				if(gui.cls_show_msg("%s,有预览画面,解析框位于左下角,应只可在解析框内部解析,放置条码于%s摄像头15cm处,[其他]完成,[取消]退出", scanModeStr,cameraTip)==ESC)
					return;
				if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			else
			{
				gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "line %d:设置的值超出边界范围",Tools.getLineInfo());
				return;
			}
			
			// case4.5:设置解析框大小位于右下角，大小为屏幕的1/2，进行解析应成功
			if((fontCameraId=mScanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
			{
				initScanMode(scan_Mode,myactivity, surfaceView, fontCameraId, true, 15*1000);
				CalRect.RATIO = 1f/2;
				CalRect.leftOffset = (int) ((1f/2)*screenWidth);
				CalRect.topOffset = (int) ((1f/2)*screenHeight);
				rect = CalRect.getDecodeRect(myactivity);
				if(Tools.isRectBound(rect))
				{
					scanDecodeScreenResolution(rect);
					if(gui.cls_show_msg("%s,无预览画面,解析框位于右下角,应只可在解析框内部解析,放置条码于前置摄像头15cm处,[其他]完成,[取消]退出", scanModeStr)==ESC)
						return;
					if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
					{
						gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"line %d:%s测试失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
						if (!GlobalVariable.isContinue)
							return;
					}
					releaseScan();
				}
				else
				{
					gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "line %d:设置的值超出边界范围",Tools.getLineInfo());
					return;
				}
			}
			
			// 测试后置
			CalRect.leftOffset =100;
			CalRect.topOffset =166;
			// 测试后置
			releaseScan();
			gui.cls_show_msg1_record(fileName, "scan11Test", gScreenTime,"%s测试通过", TESTITEM);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr, "%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan11Test", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
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
