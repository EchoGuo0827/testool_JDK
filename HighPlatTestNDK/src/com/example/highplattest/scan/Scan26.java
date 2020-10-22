package com.example.highplattest.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.R.integer;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.hardware.Camera;

import android.newland.scan.ScanUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.util.LogUtil;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan26.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20190923
 * directory 		: 扫码兼容各个分辨率测试
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class Scan26 extends UnitFragment {
	private String fileName=Scan26.class.getSimpleName();
	private final String TESTITEM = "扫码兼容各个分辨率测试";
//	private Gui gui;
	private Scan_Mode softMode = Scan_Mode.NLS_1;
	private HashMap<String, Boolean> mCameraConfig;
	/*重写界面部分*/
	private TextView textView1,data_textview,cmd_textview;
	private ImageView image_back;
	private Button btn_key_0,btn_key_1,btn_key_2,btn_key_3,btn_key_4,btn_key_5,btn_key_6,
					btn_key_7,btn_key_8,btn_key_9,btn_key_esc,btn_key_back,btn_key_point,
					btn_key_on,btn_key_under,btn_key_enter2;
	private SurfaceView surface_main;
	int cameraid;
	int maxh;
	int maxw;
	int minh;
	int minw;
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch(msg.what){
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				textView1.setText((CharSequence) msg.obj==null?"null":(CharSequence) msg.obj);
				break;
			case HandlerMsg.SURFACEVIEW_VIEW:
				surface_main.setVisibility(View.VISIBLE);
				break;
			case HandlerMsg.SURFACEVIEW_GONE:
				surface_main.setVisibility(View.GONE);
				break;
			case HandlerMsg.SCAN_SURFACE_FLUSH:
				Log.d("chen------", "setLayoutParams"+lp.width+"---"+lp.height);
				surface_main.setLayoutParams(lp);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("Test*------------","继承onCreateView");
		myactivity =  (IntentActivity) getActivity();
		Log.d("Test*------------","获取当前Activity");
//		Gui gui = new Gui(myactivity, handler);
		View view = inflater.inflate(R.layout.unit_layout, container, false);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		data_textview = (TextView) view.findViewById(R.id.data_textview);
		cmd_textview = (TextView) view.findViewById(R.id.cmd_textview);
		image_back = (ImageView) view.findViewById(R.id.image_back);
		btn_key_0 = (Button) view.findViewById(R.id.btn_key_0);
		btn_key_1 = (Button) view.findViewById(R.id.btn_key_1);
		btn_key_2 = (Button) view.findViewById(R.id.btn_key_2);
		btn_key_3 = (Button) view.findViewById(R.id.btn_key_3);
		btn_key_4 = (Button) view.findViewById(R.id.btn_key_4);
		btn_key_5 = (Button) view.findViewById(R.id.btn_key_5);
		btn_key_6 = (Button) view.findViewById(R.id.btn_key_6);
		btn_key_7 = (Button) view.findViewById(R.id.btn_key_7);
		btn_key_8 = (Button) view.findViewById(R.id.btn_key_8);
		btn_key_9 = (Button) view.findViewById(R.id.btn_key_9);
		btn_key_esc = (Button) view.findViewById(R.id.btn_key_esc);
		btn_key_back = (Button) view.findViewById(R.id.btn_key_back);
		btn_key_point = (Button) view.findViewById(R.id.btn_key_point);
		btn_key_on = (Button) view.findViewById(R.id.btn_key_on);
		btn_key_under = (Button) view.findViewById(R.id.btn_key_under);
		btn_key_enter2 = (Button) view.findViewById(R.id.btn_key_enter2);
		surface_main = (SurfaceView) view.findViewById(R.id.surface_main);
		lp = surface_main.getLayoutParams();
		layScanView = (RelativeLayout) view.findViewById(R.id.lay_scan_view);
		view.findViewById(R.id.btn_key_esc).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_enter2).setOnClickListener(listener);
		
		
		return view;
		
	}
	private Gui gui = new Gui(myactivity, myHandler);
	
	@SuppressWarnings("deprecation")
	public void scan26()
	{	
		
		Camera  camera=Camera.open(0);
		Camera.Parameters params = camera.getParameters();

//        List<android.hardware.Camera.Size> pictureSizes = params.getSupportedPictureSizes();
//        int length = pictureSizes.size();
//        for (int i = 0; i < length; i++) {
//            Log.e("SupportedPictureSizes","SupportedPictureSizes : " + pictureSizes.get(i).width + "x" + pictureSizes.get(i).height);
//        }
//
        List<android.hardware.Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        int length2 = previewSizes.size();
        for (int i = 0; i < length2; i++) {
            Log.e("SupportedPreviewSizes","SupportedPreviewSizes : " + previewSizes.get(i).width + "x" + previewSizes.get(i).height);
        }
        
        camera.release();
        
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		cameraid = scanInfo.getCameraId();
		if(cameraid==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		
		int iRet = -1;	
//		myHandler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		for (int i = 0; i <length2; i++) {
			myHandler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			if (i==0) {
				maxh=previewSizes.get(i).height;
				maxw=previewSizes.get(i).width;
				minh=previewSizes.get(i).height;
				minw=previewSizes.get(i).width;
			}else{
				if (previewSizes.get(i).height>=maxh) {
					maxh=previewSizes.get(i).height;				
				}
				if (previewSizes.get(i).width>=maxw) {
					maxw=previewSizes.get(i).width;				
				}
				
				if (previewSizes.get(i).height<=minh) {
					minh=previewSizes.get(i).height;	
				}
				
				if (previewSizes.get(i).width<=minw) {
					minw=previewSizes.get(i).width;	
				}
			}		
			lp.height=previewSizes.get(i).height;
			lp.width=previewSizes.get(i).width;
			Log.d("lp.height", "lp.height"+lp.height);
			Log.d("lp.width", "lp.width"+lp.width);
			
			if(lp.height>=200&&lp.width>=200){
				myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
				initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
				if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
					return;
				}
				if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败ret= %d,code = %s。分辨率为%d*%d", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult,lp.width,lp.height);
					if(!GlobalVariable.isContinue)
						return;
				}
				myHandler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
				releaseScan();
			}
			
		}
		
		
		myHandler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		
		//case1：设置surfaceview的大小为最大分辨率加1,应能正常扫码
		lp.height = maxh+1;
		lp.width = maxw+1;
		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		// 初始化扫码
		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
			return;
		}
		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败ret= %d,code = %s。分辨率为%d*%d", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult,lp.width,lp.height);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan();
	
		// case2:设置surfaceview的大小为最小分辨率减1,应能正常扫码   低于200*200测试无意义
		if (minh>=200&&minw>=200) {
			lp.height = minh-1;
			lp.width = minw-1;
			myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
			// 初始化扫码

			initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
			if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
				return;
			}
			if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)。分辨率为%d*%d", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult,lp.width,lp.height);
				if(!GlobalVariable.isContinue)
					return;
			}
			releaseScan();
		}
//		lp.height = minh-1;
//		lp.width = minw-1;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)。分辨率为%d*%d", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult,lp.width,lp.height);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
		
		// case3: 设置surfaceview的大小为全屏,应能正常扫码
		lp.height = GlobalVariable.ScreenHeight;
		lp.width = GlobalVariable.ScreenWidth;
		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
		// 初始化扫码
		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
			return;
		}
		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)。分辨率为%d*%d", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult,lp.width,lp.height);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan();
//		// case4: 设置surfaceview的大小为864*480,应能正常扫码
//		lp.height = 480;
//		lp.width = 864;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case5: 设置surfaceview的大小为800*480,应能正常扫码
//		lp.height =480;
//		lp.width = 800;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		Log.d("cheneric", "1280*960");
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		// case6: 设置surfaceview的大小为768*432,应能正常扫码
//		lp.height = 432;
//		lp.width = 768;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		// case7: 设置surfaceview的大小为720*480,应能正常扫码
//		lp.height = 480;
//		lp.width = 720;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		// case8: 设置surfaceview的大小为640*480,应能正常扫码
//		lp.height = 480;
//		lp.width = 640;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		// case9: 设置surfaceview的大小为576*432,应能正常扫码
//		lp.height = 432;
//		lp.width = 576;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case10: 设置surfaceview的大小为480*360,应能正常扫码
//		lp.height = 360;
//		lp.width = 480;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case11: 设置surfaceview的大小为384*288,应能正常扫码
//		lp.height = 288;
//		lp.width = 384;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case12: 设置surfaceview的大小为352*288,应能正常扫码
//		lp.height = 288;
//		lp.width = 352;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case13: 设置surfaceview的大小为320*240,应能正常扫码
//		lp.height = 240;
//		lp.width = 320;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case14: 设置surfaceview的大小为240*160,应能正常扫码
//		lp.height = 160;
//		lp.width = 240;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case15: 设置surfaceview的大小为176*144,应能正常扫码
//		lp.height = 144;
//		lp.width = 176;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case16: 设置surfaceview的大小为160*120,应能正常扫码
//		lp.height = 120;
//		lp.width = 160;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//
//		
//		// case17: 设置surfaceview的大小为144*176,应能正常扫码
//		lp.height = 176;
//		lp.width = 144;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case18: 设置surfaceview的大小为480*640,应能正常扫码
//		lp.height = 640;
//		lp.width = 480;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		
//		// case19: 设置surfaceview的大小为240*320,应能正常扫码
//		lp.height = 320;
//		lp.width = 240;
//		myHandler.sendEmptyMessage(HandlerMsg.SCAN_SURFACE_FLUSH);
//		// 初始化扫码
//		initScanMode(softMode, myactivity, surface_main, cameraid, true, TIMEOUT_SCAN);
//		if(gui.cls_show_msg("请放置条形码/二维码于后置摄像头,当前分辨率%d*%d。[取消]退出,[其他]完成",lp.width,lp.height)==ESC){
//			return;
//		}
//		if((iRet = scanDialog("",myHandler))!=NDK_SCAN_OK)
//		{
//			gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr,"line %d:%s扫码测试失败(ret= %d,code = %s)", Tools.getLineInfo(),TESTITEM,iRet,mCodeResult);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		releaseScan();
//		// case20: 设置surfaceview的大小为240*320,应能正常扫码
		
		gui.cls_show_msg1_record(fileName, "scanTest", gKeepTimeErr, "%s测试通过", TESTITEM);
		
		gui.cls_show_msg1(3,"测试通过。。3秒后自动退出");
		
		getActivity().finish();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
