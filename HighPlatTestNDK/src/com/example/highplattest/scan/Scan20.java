package com.example.highplattest.scan;

import android.newland.scan.ScanUtil;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan21.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180330
 * directory 		: 控制led灯使能和控制led灯对焦(目前没有限制,任何一种解码方式均可使用到,正常是给nls前置使用)目前支持给N700用
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180330     	created
 *					  郑薛晴			   20200914         NLS方式是可以控制LED灯和RED灯，不需要规定是前置摄像头
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan20 extends UnitFragment
{
	/**对焦灯（红条）对应节点/sys/class/scan_ctrl/led
	 * led灯（闪光灯）对应节点/sys/class/scan_ctrl/wled*/
	private String fileName=Scan20.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil)setLED和setRedLED(N700)";
	private Gui gui = new Gui(myactivity, handler);
	
	public void scan20()
	{
		
		/*private & local definition*/
		int iRet = -1;
		String msg;
		ScanUtil hardScanUtil = null;/*new ScanUtil(myactivity);*//**硬解扫码方式*/
		ScanUtil zxingScanUtil = null;/*new ScanUtil(myactivity, surfaceView, 1, true);*//**zxing前置扫码方式*/
		ScanUtil nlsFontUtil = null;/*new ScanUtil(myactivity, null, 1, true, TIMEOUT_SCAN, 1);*//**nls前置扫码方式*/
		/**根据春霞反馈，只要是NLS的方式都可以控制LED灯和RED灯 20200914*/
		//		ScanUtil nlsBackUtil = null;/*new ScanUtil(myactivity, surfaceView, 0, true, TIMEOUT_SCAN, 1);*//**nls后置扫码方式*/
		
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		
		// case4:参数异常测试,不抛出任何异常以及跑非即可
		hardScanUtil = new ScanUtil(myactivity);
		hardScanUtil.setLED(-1);
		hardScanUtil.setRedLED(-1);
		hardScanUtil.release();
		
		zxingScanUtil = new ScanUtil(myactivity, surfaceView, 1, true,TIMEOUT_SCAN);
		zxingScanUtil.setLED(-255);
		zxingScanUtil.setRedLED(-255);
		zxingScanUtil.release();
		
		nlsFontUtil = new ScanUtil(myactivity, null, 1, true, TIMEOUT_SCAN, 1);
		nlsFontUtil.setLED(255);
		nlsFontUtil.setRedLED(255);
		nlsFontUtil.release();
		
//		nlsBackUtil = new ScanUtil(myactivity, surfaceView, 0, true, TIMEOUT_SCAN, 1);
//		nlsBackUtil.setLED(2);
//		nlsBackUtil.setRedLED(2);
//		nlsBackUtil.release();
		
		// case5:流程异常测试,开启非NLS前置方式的ScanUtil,应控制LED灯使能与对焦灯使能失败
		hardScanUtil = new ScanUtil(myactivity);
		hardScanUtil.setLED(1);
		hardScanUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("case5.1:LED灯与对焦灯(红灯)是否开启,是[确认],否[取消]"))==ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case5失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		hardScanUtil.setLED(0);
		hardScanUtil.setRedLED(0);
		if(iRet==ENTER)// 在LED已经开启的情况下看是否关闭
		{
			if((iRet = gui.cls_show_msg("case5.1:LED灯与对焦灯(红灯)是否关闭,是[确认],否[取消]"))==ENTER)
			{
				gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case5失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}
		hardScanUtil.release();
		
		zxingScanUtil = new ScanUtil(myactivity, surfaceView, 1, true,TIMEOUT_SCAN);
		zxingScanUtil.setLED(1);
		zxingScanUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("case5.2:LED灯与对焦灯(红灯)是否开启,是[确认],否[取消]"))==ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case5失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		zxingScanUtil.setLED(0);
		zxingScanUtil.setRedLED(0);
		if(iRet==ENTER)
		{
			if((iRet = gui.cls_show_msg("case5.2:LED灯与对焦灯(红灯)是否关闭,是[确认],否[取消]"))==ENTER)
			{
				gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case5失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}
		zxingScanUtil.release();

		/*nlsBackUtil = new ScanUtil(myactivity, surfaceView, 0, true, TIMEOUT_SCAN, 1);
		nlsBackUtil.setLED(1);
		nlsBackUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("case5.3:LED灯与对焦灯(红灯)是否开启,是[确认],否[取消]"))==ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case5失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		nlsBackUtil.setLED(0);
		nlsBackUtil.setRedLED(0);
		if(iRet == ENTER)
		{
			if((iRet = gui.cls_show_msg("case5.3:LED灯与对焦灯(红灯)是否关闭,是[确认],否[取消]"))==ENTER)
			{
				gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case5失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}
		nlsBackUtil.release();*/
		
		// case1.1:开启LED灯,LED灯应开启成功
		nlsFontUtil = new ScanUtil(myactivity, null, 1, true, TIMEOUT_SCAN, 1);
		nlsFontUtil.setLED(1);
		if((iRet = gui.cls_show_msg("LED灯是否开启,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case1.1失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case1.1失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case1.2:关闭LED灯,LED灯应关闭成功
		nlsFontUtil.setLED(0);
		if((iRet = gui.cls_show_msg("LED灯是否关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case1.2失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case1.2失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// case2.1:打开对焦灯,对焦灯应开启成功
		nlsFontUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("对焦灯(红灯)是否开启,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case2.1失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,并用对焦灯对准码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case2.1失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case2.2:关闭对焦灯,对焦灯应关闭成功
		nlsFontUtil.setRedLED(0);
		if((iRet = gui.cls_show_msg("对焦灯(红灯)是否关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case2.2失败line %d:扫码失败(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case2.2失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case3.1:组合方式,开启LED灯与对焦灯,均应开启成功
		nlsFontUtil.setLED(1);
		nlsFontUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("LED灯与对焦灯是否开启,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.1失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,并用对焦灯对准码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.1失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case3.2:组合方式,开启LED灯,关闭对焦灯,均应开启关闭成功
		nlsFontUtil.setLED(1);
		nlsFontUtil.setRedLED(0);
		if((iRet = gui.cls_show_msg("LED灯开启,对焦灯(红灯)关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.2失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.2失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case3.3:组合方式,关闭LED灯,开启对焦灯,均应关闭开启成功
		nlsFontUtil.setLED(0);
		nlsFontUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("LED灯关闭,对焦灯(红灯)开启,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.3失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,并用对焦灯对准码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.3失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case3.4:组合方式,关闭LED灯与对焦灯,均应关闭成功
		nlsFontUtil.setLED(0);
		nlsFontUtil.setRedLED(0);
		
		if((iRet = gui.cls_show_msg("LED灯与对焦灯(红灯)是否关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case3.4失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("请在前置摄像头放置一维或二维码,放置完毕后任意键继续");
		if((iRet = scanUtilDialog(nlsFontUtil, "", handler))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case2.1失败line %d:扫码测试(%d)", Tools.getLineInfo(),iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case6:多次开启或关闭LED灯与对焦灯为最后一次的状态
		nlsFontUtil.setLED(1);
		nlsFontUtil.setLED(1);
		if((iRet = gui.cls_show_msg("LED灯是否开启,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case6失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		nlsFontUtil.setRedLED(1);
		nlsFontUtil.setRedLED(1);
		if((iRet = gui.cls_show_msg("对焦灯是否开启,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case6失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		nlsFontUtil.setLED(0);
		nlsFontUtil.setLED(0);
		if((iRet = gui.cls_show_msg("LED灯是否关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case6失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		nlsFontUtil.setRedLED(0);
		nlsFontUtil.setRedLED(0);
		if((iRet = gui.cls_show_msg("对焦灯是否关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case6失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case7:开关LED灯与对焦灯交叉,灯状态应为最后一次的状态
		nlsFontUtil.setRedLED(1);
		nlsFontUtil.setLED(0);
		nlsFontUtil.setLED(1);
		nlsFontUtil.setRedLED(0);
		if((iRet = gui.cls_show_msg("LED灯开启,对焦灯关闭,是[确认],否[取消]"))!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "scan21", gKeepTimeErr,  "case7失败line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		nlsFontUtil.release();
		gui.cls_show_msg1_record(fileName, "scan21", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		// 测试后置 关闭对焦灯和LED
		ScanUtil nlsBackUtil = new ScanUtil(myactivity, surfaceView, 0, true, TIMEOUT_SCAN, 1);
		nlsBackUtil.setLED(0);
		nlsBackUtil.setRedLED(0);
	}
	
}
