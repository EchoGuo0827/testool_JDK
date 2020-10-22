package com.example.highplattest.scan;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: 扫码模块
 * file name 		: scan31.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200417 
 * directory 		: 
 * description 		: USB摄像头参数验证(F7)
 * related document : 
 * history 		 	: 变更点                              变更时间		案例人员
 *			  		  F7 V1.0.06导入        20200417		chending
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan28 extends UnitFragment  {
	private String fileName=Scan28.class.getSimpleName();
	//by chending
	private final String TESTITEM = "USB摄像头参数验证(F7)";
	private Gui gui ;
	int ret=-1;
	String getpro;
	String usbcam_pro="persist.usbcam.params.enable";
	public void scan28(){
		gui=new Gui(myactivity, handler);
		int cameraid=0;
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		
		cameraid = scanInfo.getCameraId();
		if(cameraid==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		
		String sysPro = BaseFragment.getProperty(usbcam_pro, "-1");
		gui.cls_show_msg1(3,"当前USB摄像头参数系统属性值为%s", sysPro);
		
		gui.cls_show_msg("设置参数为1.调用扫码接口。扫码效果比较暗。按任意键继续。。。。。。");
		//case1:设置为1 调用扫码会比较暗
		if(BaseFragment.setProperty(usbcam_pro, "1")!=0)
		{
			gui.cls_show_msg1_record(fileName, "scan31", 2, "line %d:设置usbcam属性=1失败", Tools.getLineInfo());
		}
		getpro=BaseFragment.getProperty(usbcam_pro, "-1");
		if (!getpro.equalsIgnoreCase("1")) {
			gui.cls_show_msg1_record(fileName, "scan31", 2, "line %d:设置usbcam属性=1失败", Tools.getLineInfo());
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		initScanMode(Scan_Mode.NLS_1,myactivity, surfaceView, cameraid, true, 15000);
		if(gui.cls_show_msg("请将条形码或二维码放在%s摄像头20-30cm处,[其他]完成,[取消]退出",tipMsg)==ESC)
			return;
		if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan31", gKeepTimeErr,"line %d:%s扫码测试失败(ret = %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
		
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseScan();
		
		
		gui.cls_show_msg("设置参数为0.调用扫码接口。扫码效果比较亮。按任意键继续。。。。。。");
		//case1:设置为0  调用扫码会比较亮
		if(BaseFragment.setProperty(usbcam_pro, "0")!=0)
		{
			gui.cls_show_msg1_record(fileName, "scan31", 2, "line %d:设置usbcam属性=0失败", Tools.getLineInfo());
		}
		getpro=BaseFragment.getProperty(usbcam_pro, "-1");
		if (!getpro.equalsIgnoreCase("0")) {
			gui.cls_show_msg1_record(fileName, "scan31", 2, "line %d:设置usbcam属性=0失败", Tools.getLineInfo());
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		initScanMode(Scan_Mode.NLS_1,myactivity, surfaceView, cameraid, true, 15000);
		if(gui.cls_show_msg("请将条形码或二维码放在%s摄像头20-30cm处,[其他]完成,[取消]退出",tipMsg)==ESC)
		return;
		if((ret = scanDialog("",handler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan31", gKeepTimeErr,"line %d:%s扫码测试失败(ret = %d,code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
	
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		releaseScan();
		gui.cls_show_msg1_record(fileName, "scan31", gScreenTime,"%s测试通过", TESTITEM);
	
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		/**测试后置*/
		releaseScan();
	}

}
