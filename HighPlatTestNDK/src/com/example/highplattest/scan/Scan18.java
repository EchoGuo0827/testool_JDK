package com.example.highplattest.scan;

import java.util.Random;
import android.graphics.Rect;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan18.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171109
 * directory 		: 复现surfaceview 宽比长大是会保留上次正确扫码数据的BUG
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20171109     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan18 extends UnitFragment
{
	private String fileName=Scan18.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil)解析框大小变化";
	private Gui gui;
	private Scan_Mode softMode = Scan_Mode.NLS_1;
	int cameraid;
	
	public void scan18()
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan18", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		cameraid = scanInfo.getCameraId();
		if(cameraid==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		Log.d("eric", cameraid+"cameraid");
		while(true)
		{
			// 将按钮设置为停止扫码
		//	handler.sendMessage(handler.obtainMessage(HandlerMsg.SCAN_BTN_SET_TEXT, "停止扫码"));
			int nkeyIn = gui.cls_show_msg("扫码配置\n0.nls解析框大小测试\n1.zxing解析框大小测试\n");
			switch (nkeyIn) 
			{
			case '0':
				softMode = Scan_Mode.NLS_1;
				scanTest(tipMsg);
				break;
				
			case '1':
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
				{
					gui.cls_show_msg("A9平台不支持zxing旧接口测试");
					break;
				}
				softMode = Scan_Mode.ZXING;
				scanTest(tipMsg);
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	/**
	 * 测试nls/zxing的surfaceview大小变化的BUG
	 */
	private void scanTest(String camTip)
	{
		/*private & local definition*/
		int iRet = -1;
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		// case1:设置surfaceview的大小为480*640,应能正常扫码
		lp.height = 640;
		lp.width = 480;
		handler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		// 初始化扫码
		initScanMode(softMode, myactivity, surfaceView, cameraid, true, TIMEOUT_SCAN);
		if(gui.cls_show_msg("请放置条形码/二维码于%s,[取消]退出,[其他]完成",camTip)==ESC)
			return;
		if((iRet = scanDialog("",handler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败ret= %d,code = %s", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan();
		// case2:设置surfaceview的大小为640*480,应能正常扫码
		lp.height = 480;
		lp.width = 640;
		handler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		// 初始化扫码
		initScanMode(softMode, myactivity, surfaceView, cameraid, true, TIMEOUT_SCAN);
		if(gui.cls_show_msg("[取消]退出,[其他]完成,点[其他]后移开条形码/二维码3s再放置条形码/二维码于%s",camTip)==ESC)
			return;
		switch (softMode) 
		{
		case NLS_1://nls修改后各种大小的surfaceview均能扫码成功
			if((iRet = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			break;
			
		case ZXING://zxing中surfaceview宽比长大的情况应扫码失败
			if((iRet = scanDialog("",handler))!=NDK_SCAN_FAULT)
			{
				gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			break;

		default:
			break;
		}
		releaseScan();
		// case3:设置surfaceview的大小为全屏,应能正常扫码
		lp.height = GlobalVariable.ScreenHeight;
		lp.width = GlobalVariable.ScreenWidth;
		handler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		// 初始化扫码
		initScanMode(softMode, myactivity, surfaceView, cameraid, true, TIMEOUT_SCAN);
		if(gui.cls_show_msg("请放置条形码/二维码于%s,[取消]退出,[其他]完成",camTip)==ESC)
			return;
		if((iRet = scanDialog("",handler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan();
		// case4:设置surfaceview的大小为xx*xx（随机,设定模式为宽比高大）,应能正常扫码
		Rect rect = getRect(GlobalVariable.ScreenWidth, GlobalVariable.ScreenHeight);
		lp.width = rect.width();
		lp.height = rect.height();
		handler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		// 初始化扫码
		initScanMode(softMode, myactivity, surfaceView, cameraid, true, TIMEOUT_SCAN);
		if(gui.cls_show_msg("[取消]退出,[其他]完成,点[其他]后移开条形码/二维码3s再放置条形码/二维码于%s",camTip)==ESC)
			return;
		
		switch (softMode) 
		{
		case NLS_1:
			if((iRet = scanDialog("",handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			break;
			
		case ZXING:
			if((iRet = scanDialog("",handler))!=NDK_SCAN_FAULT)
			{
				gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			break;

		default:
			break;
		}
		releaseScan();
		gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr, "%s测试通过", TESTITEM);
		
	}
	
	/**
	 * 获取宽比长大的矩形框
	 * @param maxWidth 宽最大的值
	 * @param maxHeight
	 * @return
	 */
	public Rect getRect(int maxWidth,int maxHeight)
	{
		Random rand = new Random();
		int width = 0,height = 0;
		while(true)
		{
			width = rand.nextInt(maxWidth-480)+480;// 宽的取值范围为 480-maxWidth
			height = rand.nextInt(maxHeight-480)+480;// 高的取值范围为460-maxHeight
			if(width>height)
				break;
		}
		Rect rect = new Rect(0, 0, width, height);
		return rect;
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
	}
}
