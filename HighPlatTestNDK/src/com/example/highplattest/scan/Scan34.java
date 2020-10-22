package com.example.highplattest.scan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.newland.scan.ScanUtil;
import android.newland.scan.SoftEngine;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Scan15.java 
 * directory 		: startYUVDecode (美团专用，A7和A9支持)
 * description 		: initDecode(ResultCallBack callback)、startYUVDecode(byte[] yuv, int width, int height)、
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		  Camera1方式预览				20200416     	zhengxq
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan34 extends UnitFragment
{
	private final String TESTITEM =  "(SoftEngine+Camera1)startDecode";
	private String fileName=Scan31.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private final int  SCAN_TIMEOUT = 15*1000;
	
	private SoftEngine mSoftEngine;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private int previewWidth=1280,previewHeight=960;
	private boolean isScan = false;
	private boolean isTheTimeEnd = false;
	
	enum ScanErrMode  {Yuv_Err,Width_Err1,Width_Err2,Width_Err3,Height_Err1,Height_Err2,Height_Err3,/*Pic_Test,*/Data_Err};
	
	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mCamera != null) {
				try {
					mCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	public PreviewCallback MyPreviewCallback = new PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

			LoggerUtil.v("onPreviewFrame|||"+isScan);
			if (mSoftEngine != null&&isScan==true) {
//				LoggerUtil.d("data's length=" + data.length + ",width="
//						+ previewWidth + ",height=" + previewHeight);
				mSoftEngine.startDecode(data, previewWidth, previewHeight);
			}
		}
	};
	
	
	private int mEventCode=100;
	
	private ScanningCallback mScanningCallback = new ScanningCallback() {
		
		@Override
		public void onScanningCallback(int eventCode, int codeType, byte[] data1, int length) {
			LoggerUtil.e("eventCode = " + eventCode + ",codeType=" + codeType + ",data=" + new String(data1) + ",length=" + length);
			mEventCode=eventCode;
			if(eventCode==1)
			{
				
				try {
					mCodeResult = new String(data1,mCodeType);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					mCodeResult="UnsupportedEncodingException异常抛出";
				}
				synchronized (mLockObj) {
					mLockObj.notify();
				}
			}
			if(eventCode==-1&&isScan==false)
			{
				LoggerUtil.e("ResultCallBack:the time scan end="+eventCode);
				isTheTimeEnd=true;
			}
		}
	};
	
	private Object mLockObj = new Object();
	private String mCodeResult="";
	private String mCodeType="UTF-8";
	
	
	ScanDefineInfo scanDefineInfo;
	
	HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
	int mCameraId=-1;

	
	public void scan34()
	{
		/**二维码*/
		codeMap.put(Code_Type.QR_UTF8_1, "方式");
		codeMap.put(Code_Type.QR_UTF8_2, QR_UTF8);
		codeMap.put(Code_Type.QR_GBK, QR_GBK);
		codeMap.put(Code_Type.QR_ECI, "QR Code(UTF-8,带ECI前缀):中国1A2B3C4D5F");
		codeMap.put(Code_Type.PDF417, "PDF417:1A2B3C4D");
		/**条形码*/
		codeMap.put(Code_Type.CodeBar, "9876543210321");
		codeMap.put(Code_Type.Code39, "Co39");/**自动识别库更新最短为4字节*/
		codeMap.put(Code_Type.Code93, "ABCabc123");
		codeMap.put(Code_Type.Code128, "code128(a)*%,");
		codeMap.put(Code_Type.EAN_8, "12345670");
		codeMap.put(Code_Type.EAN_13, "1234567890128");
		codeMap.put(Code_Type.EAN_128, "00000174571740159067");
		codeMap.put(Code_Type.ITF_14, "1234567890123");
		codeMap.put(Code_Type.UPC_A, "123456789012");
		
		ScanUtil tempScanUtil = new ScanUtil(myactivity);
		String nlsVersion = tempScanUtil.getNLSVersion();
		
		LoggerUtil.d("scan34->:nlsVersion"+nlsVersion);
		nlsVersion = nlsVersion.substring(nlsVersion.indexOf("SoftEngine:")-4,nlsVersion.indexOf("SoftEngine:")-1);
		LoggerUtil.e("scan8->nlsVer="+nlsVersion+"=========");
		int nlsVersionDig = Integer.parseInt(nlsVersion.substring(1));
		LoggerUtil.e("scan8->nlsVersionDig="+nlsVersionDig+"=========");
		
		codeMap.put(Code_Type.UPC_E, nlsVersionDig>=17?"1234565":"01234565");//扫码库在B17之后UPC-E是不输出的 20200623
		codeMap.put(Code_Type.ISBN_ISSN, "9780194315104");
		codeMap.put(Code_Type.UCC_EAN_128, "83979222");
		
		/**测试前置*/
		scanDefineInfo = getCameraInfo();
		mCameraId = scanDefineInfo.getCameraId();
		if(mCameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}
		// 旧的Camera方式测试，只支持带预览画面的摄像头
		
//		mSoftEngine = SoftEngine.getInstance(myactivity);
		mSoftEngine = SoftEngine.getInstance();

		LoggerUtil.e("BACK_CAMERA,"+scanDefineInfo.cameraReal.get(BACK_CAMERA));
		LoggerUtil.e("FONT_CAMERA,"+scanDefineInfo.cameraReal.get(FONT_CAMERA));
		LoggerUtil.e("EXTERNAL_CAMERA,"+scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA));
		LoggerUtil.e("USB_CAMERA,"+scanDefineInfo.cameraReal.get(USB_CAMERA));
		
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.摄像头切换测试\n1.码制测试\n2.单元异常测试\n3.大数据测试\n", TESTITEM);
			switch (nkeyIn) {
			case '0':// case4.3：若支持多个摄像头
				tranlateCamera();
				break;
				
			case '1':// case3.2 在camera位置放置不同码制的一维码或二维码图片
				codeTypeTest();
				break;
				
			case '2':
				unitTest();
				break;
				
			case '3':
				bigDataTest();
				break;
				
			case ESC:
				isScan=false;
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	private void bigDataTest()
	{
		String funcName="bigDataTest";
		String camTip = scanDefineInfo.getCameraInfo();
		LoggerUtil.e("codeTypeTest|||camTip="+camTip);
		// case1:UTF-8编码的QR码
		if (gui.cls_show_msg("请扫《大数据测试码.pdf》的2048字节二维码，任意键开始测试") != ESC) 
		{
			testCodeType(false,Code_Type.QR_UTF8_1, camTip, scanDefineInfo,codeMap.get(Code_Type.QR_UTF8_1));
			gui.cls_show_msg("扫码结果=%s", mCodeResult);
		}
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "大数据测试通过");
	}
	
	
	private void unitTest()
	{
		// case2.1:yuv=null测试，so直接奔溃
		gui.cls_show_msg2(0.5f,"异常测试:yuvdata=null");
		scanParamAbnormal(ScanErrMode.Yuv_Err);
		
		// case2.2:widht=-2147483648异常测试
		gui.cls_show_msg2(0.5f,"异常测试:width=-2147483648");
		scanParamAbnormal(ScanErrMode.Width_Err1);
		
		// case2.3:width=2147483647异常测试 会报数据角标越界
		gui.cls_show_msg2(0.5f,"异常测试:width=2147483647");
		scanParamAbnormal(ScanErrMode.Width_Err2);
		
		// case2.4:width=500异常测试
		gui.cls_show_msg2(0.5f,"异常测试:width=500");
		scanParamAbnormal(ScanErrMode.Width_Err3);
		
		// case2.5:height=-2147483648测试
		gui.cls_show_msg2(0.5f,"异常测试:height=-2147483648");
		scanParamAbnormal(ScanErrMode.Height_Err1);
		
		// case2.6:height=2147483647测试 数组角标越界
		gui.cls_show_msg2(0.5f,"异常测试:height=2147483647");
		scanParamAbnormal(ScanErrMode.Height_Err2);
		
		// case2.7:height=500测试
		gui.cls_show_msg2(0.5f,"异常测试:height=500");
		scanParamAbnormal(ScanErrMode.Height_Err3);
		
		/*// case2.7:正常图片解析,无法测试
		gui.cls_show_msg2(0.5f,"正常图片解析测试");
		scanParamAbnormal(ScanErrMode.Pic_Test);*/
		
		// case2.8:任意data数据测试
		gui.cls_show_msg2(0.5f,"异常测试：yuv=任意数据");
		scanParamAbnormal(ScanErrMode.Data_Err);
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		gui.cls_show_msg1_record(TESTITEM, "unitTest", gKeepTimeErr, "单元测试通过,无异常抛出视为测试通过");
		
	}
	
	public void tranlateCamera()
	{
		String funcName="tranlateCamera";
		// case4.3：若支持多个摄像头，摄像头切换也可正常扫码
		//若支持后置摄像头，需要测试后置摄像头
		if((mCameraId=scanDefineInfo.cameraReal.get(BACK_CAMERA))!=-1)
		{
			gui.cls_show_msg2(0.5f, "tranlateCamera->后置摄像头解码测试,请放置QR Code(字符编码格式:UTF-8),码值为方式于后置摄像头处...");
			testCodeType(true,Code_Type.QR_UTF8_1, "后置摄像头", scanDefineInfo,codeMap.get(Code_Type.QR_UTF8_1));
		}
		//若支持前置摄像头要测试前置摄像头，该case仅支持F7
		if((mCameraId=scanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
		{
			gui.cls_show_msg2(0.5f, "tranlateCamera->前置摄像头解码测试,请放置QR Code(字符编码格式:UTF-8)?码值为%s于前置摄像头处...",codeMap.get(Code_Type.QR_UTF8_2));
			testCodeType(true,Code_Type.QR_UTF8_2, "前置摄像头", scanDefineInfo,codeMap.get(Code_Type.QR_UTF8_2));
			
			// F系列前置摄像头支持CODE128码 V1.0.07导入
			
			gui.cls_show_msg2(0.5f, "tranlateCamera->前置摄像头解码测试,请放置Code128?码值为%s于前置摄像头处...",codeMap.get(Code_Type.Code128));
			testCodeType(true,Code_Type.Code128, "前置摄像头", scanDefineInfo,codeMap.get(Code_Type.Code128));
		}
		//若支持支付摄像头，要测试支付摄像头，X5系列支持
		if((mCameraId=scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA))!=-1)
		{
			gui.cls_show_msg2(0.5f, "tranlateCamera->支付摄像头解码测试,请放置Codebar码于支付摄像头处...");
			testCodeType(true,Code_Type.CodeBar, "支付摄像头", scanDefineInfo,codeMap.get(Code_Type.CodeBar));
		}
		//若支持USB摄像头，要测试USB摄像头，目前仅F7支持
		if((mCameraId=scanDefineInfo.cameraReal.get(USB_CAMERA))!=-1)
		{
			gui.cls_show_msg2(0.5f, "tranlateCamera->USB摄像头解码测试,请放置Code39码于USB摄像头处...");
			testCodeType(true,Code_Type.Code39, "USB摄像头", scanDefineInfo,codeMap.get(Code_Type.Code39));
		}
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"摄像头切换解码测试通过", TESTITEM);
	}
	
	private void codeTypeTest()
	{
		String funcName="codeTypeTest";
		
		String camTip = scanDefineInfo.getCameraInfo();
		LoggerUtil.e("codeTypeTest|||camTip="+camTip);
		// case1:UTF-8编码的QR码
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:UTF-8)?码值为方式。按取消键跳过") != ESC) 
		{
			testCodeType(false,Code_Type.QR_UTF8_1, camTip, scanDefineInfo,codeMap.get(Code_Type.QR_UTF8_1));
			if(mCodeResult.equals("方式")==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫QR码失败,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.QR_UTF8_1),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:UTF-8)?码值为%s。按取消键跳过",QR_UTF8) != ESC) {
			testCodeType(false,Code_Type.QR_UTF8_2, camTip, scanDefineInfo,codeMap.get(Code_Type.QR_UTF8_2));
			if(mCodeResult.equals(QR_UTF8)==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫QR码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.QR_UTF8_2),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		// GBK编码的QR码
		mCodeType="GBK";
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:GBK)?按取消键跳过") != ESC) {
			testCodeType(false,Code_Type.QR_GBK, camTip, scanDefineInfo,codeMap.get(Code_Type.QR_GBK));
			if(mCodeResult.equals(codeMap.get(Code_Type.QR_GBK))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫QR码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.QR_GBK),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// 带ECI前缀的QR_Code
		mCodeType="UTF-8";
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:UTF-8,带ECI前缀)?按取消键跳过") != ESC) {
			
			testCodeType(false, Code_Type.QR_ECI, camTip, scanDefineInfo, codeMap.get(Code_Type.QR_ECI));
			if(mCodeResult.equals(codeMap.get(Code_Type.QR_ECI))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫QR码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.QR_ECI),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		// case1.2:应支持PDF417码
		if (gui.cls_show_msg("是否测试PDF417码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.PDF417, camTip, scanDefineInfo, codeMap.get(Code_Type.PDF417));
			if(mCodeResult.equals(codeMap.get(Code_Type.PDF417))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫PDF417码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.PDF417),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			
			// case1.3:放Codebar码进行扫码
		if (gui.cls_show_msg("是否测试Codebar码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.CodeBar, camTip, scanDefineInfo, codeMap.get(Code_Type.CodeBar));
			if(mCodeResult.equals(codeMap.get(Code_Type.CodeBar))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫CodeBar码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.CodeBar),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
			// case1.4:应支持code39码
		if (gui.cls_show_msg("是否测试code39码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.Code39, camTip, scanDefineInfo, codeMap.get(Code_Type.Code39));
			if(mCodeResult.equals(codeMap.get(Code_Type.Code39))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫code39码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.Code39),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.5:应支持Code93
		if (gui.cls_show_msg("是否测试Code93码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.Code93, camTip, scanDefineInfo, codeMap.get(Code_Type.Code93));
			if(mCodeResult.equals(codeMap.get(Code_Type.Code93))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫Code93码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.Code93),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.6:应支持Code128
		if (gui.cls_show_msg("是否测试code128码(ASCII编码)?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.Code128, camTip, scanDefineInfo, codeMap.get(Code_Type.Code128));
			if(mCodeResult.equals(codeMap.get(Code_Type.Code128))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫code128码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.Code128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.7:应支持EAN8无附加码
		if (gui.cls_show_msg("是否测试EAN8码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.EAN_8, camTip, scanDefineInfo, codeMap.get(Code_Type.EAN_8));
			if (mCodeResult.equals(codeMap.get(Code_Type.EAN_8))==false) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s扫EAN8码失败(预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM,codeMap.get(Code_Type.EAN_8), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.8:应支持EAN13无附加码
		if (gui.cls_show_msg("是否测试EAN13码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.EAN_13, camTip, scanDefineInfo, codeMap.get(Code_Type.EAN_13));
			if(mCodeResult.equals(codeMap.get(Code_Type.EAN_13))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫EAN13码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.EAN_13),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		if (gui.cls_show_msg("是否测试EAN-128码?按取消键跳过") != ESC) {
			// case1.9:应支持EAN-128码
			testCodeType(false, Code_Type.EAN_128, camTip, scanDefineInfo, codeMap.get(Code_Type.EAN_128));
			if(mCodeResult.equals(codeMap.get(Code_Type.EAN_128))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫EAN-128码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.EAN_128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}	
			// case1.10:放ITF码进行扫码
		if (gui.cls_show_msg("是否测试ITF_14码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.ITF_14, camTip, scanDefineInfo, codeMap.get(Code_Type.ITF_14));
			if(mCodeResult.equals(codeMap.get(Code_Type.ITF_14))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫ITF码失败(预期code=%s,实际code = %s)",Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.ITF_14),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.11:应支持UPC_A无附加码
		if (gui.cls_show_msg("是否测试UPC_A码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.UPC_A, camTip, scanDefineInfo, codeMap.get(Code_Type.UPC_A));
			if (mCodeResult.equals(codeMap.get(Code_Type.UPC_A))==false) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s扫UPC_A码失败(预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM,codeMap.get(Code_Type.UPC_A), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.12:应支持UPC_E码
		if (gui.cls_show_msg("是否测试UPC_E码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.UPC_E, camTip, scanDefineInfo, codeMap.get(Code_Type.UPC_E));
			if (mCodeResult.equals(codeMap.get(Code_Type.UPC_E))==false) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s扫UPC_E码失败(预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM,codeMap.get(Code_Type.UPC_E), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.13:应支持IBSN/ISSN码
		if (gui.cls_show_msg("是否测试IBSN/ISSN码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.ISBN_ISSN, camTip, scanDefineInfo, codeMap.get(Code_Type.IBSN_ISSN_ADD));
			if (mCodeResult.equals(codeMap.get(Code_Type.ISBN_ISSN))==false) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s扫IBSN/ISSN码失败(预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM,codeMap.get(Code_Type.ISBN_ISSN), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.14:应支持GS1-128(UCC/EAN-128)码
		if (gui.cls_show_msg("是否测试UCC/EAN 128码?按取消键跳过") != ESC) {
			testCodeType(false, Code_Type.UCC_EAN_128, camTip, scanDefineInfo, codeMap.get(Code_Type.UCC_EAN_128));
			if(mCodeResult.equals(codeMap.get(Code_Type.UCC_EAN_128))==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s扫UCC/EAN 128码失败(预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,codeMap.get(Code_Type.UCC_EAN_128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.15:扫上海地铁码应该不会导致崩溃
			// 扫码操作 
		if (gui.cls_show_msg("是否测试上海地铁码?按取消键跳过") != ESC) {
			gui.cls_show_msg("该测试项不对比码值，不奔溃，能扫出码值即可；请将上海地铁码准备好，任意键继续");
			testCodeType(false, Code_Type.QR_UTF8_1, camTip, scanDefineInfo, "");
			gui.cls_show_msg("上海地铁码扫码结果=%s", mCodeResult);
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "%s测试通过", funcName);
	}
	
	/**
	 * 测试码
	 * @param isCompare 是否比较码值
	 * @param codeType 扫码的类型
	 * @param camTip 扫码的摄像头
	 * @param scanDefineInfo 摄像头信息
	 * @param codeValue 比较的码值
	 */
	private void testCodeType(Boolean isCompare,Code_Type codeType,String camTip,ScanDefineInfo scanDefineInfo,String codeValue)
	{
		String funcName="testCodeType";
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(mHolderCallback);
		initCamera(camTip,scanDefineInfo);
		// 初始化扫码
		mSoftEngine.setScanningCallback(mScanningCallback);
		
		isScan = true;
		mCodeResult="";
		synchronized (mLockObj) {
			try {
				mLockObj.wait(SCAN_TIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isScan=false;
		
		long startTime = System.currentTimeMillis();
		// 时间要小于2s
		while(isTheTimeEnd==false&&Tools.getStopTime(startTime)<=2)
		{
			SystemClock.sleep(100);
		}
		if(isCompare)
		{
			if(mCodeResult.equals(codeValue)==false)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s码解析失败(实际=%s,预期=%s)", Tools.getLineInfo(),codeType,mCodeResult,codeValue);
				if(GlobalVariable.isContinue==false)
				{
					testEnd();
					return;
				}
			}
		}
		LoggerUtil.v("testCodeType|||"+isScan+",isStartYuvDecode="+isTheTimeEnd);
		testEnd();
	}
	
	private void scanParamAbnormal(ScanErrMode errMode)
	{
		int testWidth = 1280;
		int testHeight=960;
		String funcName="scanParamAbnormal";
		int ret = 0;
		boolean isRet=false;
		
		/**测试前置：要获取data数据*/
		byte[] data = new byte[testWidth*testHeight];
//		LoggerUtil.v("data="+data[data.length-1]);
		// 初始化扫码
		mSoftEngine.setScanningCallback(mScanningCallback);
		
		LoggerUtil.d("initDecode:"+ret);
		isScan = true;
		
		switch (errMode) {
		case Yuv_Err:
			isRet = mSoftEngine.startDecode(null, testWidth, testHeight);
			break;
			
		case Width_Err1:
			isRet = mSoftEngine.startDecode(data, -2147483648, testHeight);
			break;
			
		case Width_Err2:
			isRet = mSoftEngine.startDecode(data, 2147483647, testHeight);
			break;
			
		case Width_Err3:
			isRet = mSoftEngine.startDecode(data, 500, testHeight);
			break;
			
		case Height_Err1:
			isRet = mSoftEngine.startDecode(data, testWidth,-2147483648);
			break;
			
		case Height_Err2:
			isRet = mSoftEngine.startDecode(data, testWidth,2147483647);
			break;
			
		case Height_Err3:
			isRet = mSoftEngine.startDecode(data, testWidth, 500);
			break;
			
		/*case Pic_Test:
			isRet = mSoftEngine.startDecode(data, testWidth, testHeight);
//			expecetValue="134777508699818674";
			break;*/
			
		case Data_Err:
			Arrays.fill(data, (byte)0x11);
			isRet = mSoftEngine.startDecode(data, testWidth, testHeight);
			break;

		default:
			break;
		}
		
		SystemClock.sleep(3000);
		if(isRet==true)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：%s异常测试失败(%d)", Tools.getLineInfo(),errMode,mEventCode);
		}
		/*else
		{
			if(mCodeResult.equals("134777508699818674")==false)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：%s,码制错误", Tools.getLineInfo(),errMode);
			}
		}*/
	}
	
	private void testEnd()
	{
		LoggerUtil.v("testEnd||stopDecode");
		if(mSoftEngine!=null)
		{
			// 停止扫码操作
			mSoftEngine.stopDecode();
		}

		if(mCamera!=null)
		{
			// 测试后置：释放扫码
			mHolder.removeCallback(mHolderCallback);
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera=null;
		}
	}
	
	public void initCamera(String camTip,ScanDefineInfo scanDefineInfo)
	{
		LoggerUtil.e("initCamera,camTip="+camTip);
		if(camTip.contains("USB"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(USB_CAMERA));
		}
		else if(camTip.contains("前置"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(FONT_CAMERA));
		}
		else if(camTip.contains("后置"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(BACK_CAMERA));
		}
		else if(camTip.contains("支付"))
		{
			mCamera = Camera.open(scanDefineInfo.cameraReal.get(EXTERNAL_CAMERA));
		}
		
		CameraManagerUtil cameraManagerUtil = new CameraManagerUtil(myactivity, mCamera);
		Size previewSize = cameraManagerUtil.setCameraParams(camTip, scanDefineInfo);
		
		previewWidth = previewSize.width;
		previewHeight = previewSize.height;

		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.setPreviewCallback(MyPreviewCallback);
		mCamera.startPreview();
	}
	
	@Override
	public void onTestUp() {
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_RED);
		
	}

	@Override
	public void onTestDown() {
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_BLACK);
		
	}

}
