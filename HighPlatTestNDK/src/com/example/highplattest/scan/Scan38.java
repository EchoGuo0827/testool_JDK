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
import android.newland.scan.ScanUtil.ResultCallBack;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.btutils.ClsUtils;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
 /***********************************************************************
 * 
 * file name 		: Scan38.java 
 * directory 		: 
 * description 		: 
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		     新建				        20200806     	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan38 extends UnitFragment
{	
	private final String TESTITEM = "(SoftEngine)scanSet";	
	private String fileName =Scan38.class.getSimpleName();	
	private Gui gui = new Gui(myactivity, handler);
	ScanDefineInfo scanDefineInfo;
	private SoftEngine mSoftEngine;
	private Camera mCamera;
	private SurfaceHolder mHolder;
	int mCameraId=-1;
	int mGetTimeout = 0;
	private final int  SCAN_TIMEOUT = 15*1000;
	private String mCodeType="UTF-8";
	private Object mLockObj = new Object();
	private boolean isTheTimeEnd = false;
	private boolean isScan = false;
	private int previewWidth=1280,previewHeight=960;
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
	
	private ScanningCallback mScanningCallback = new ScanningCallback() {
		
		@Override
		public void onScanningCallback(int eventCode, int codeType, byte[] data1, int length) {
			LoggerUtil.e("eventCode = " + eventCode + ",codeType=" + codeType + ",data=" + new String(data1) + ",length=" + length);
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
	
	public void scan38()
	{
		mGetTimeout = Tools.getSreenTimeout(myactivity);
		// 设置超时时间为永不休眠
		Tools.setSreenTimeout(myactivity, 30*1000*1000);
		/**测试前置*/
		scanDefineInfo = getCameraInfo();
		mCameraId = scanDefineInfo.getCameraId();
		if(mCameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}
		mSoftEngine = SoftEngine.getInstance(myactivity);

		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元参数异常测试\n1.单元测试\n2.长度边界测试\n3.重启验证", TESTITEM);
			switch (nkeyIn) {
				
			case '0':
				unitParaTest();
				break;
				
			case '1':
				unitTest();
				testEnd();
				break;
				
			case '2':
				unitLengthTest();
				break;
				
			case '3':
				rebootVerfity();
				break;
				
			case ESC:
				Tools.setSreenTimeout(myactivity, mGetTimeout);// 测试后置：设置回原来的时间
				isScan=false;
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
		private void unitParaTest()
		{
			int mRet=-1;
			String funcName = "unitParaTest";			
		//异常测试		
		//case2.1: TrsmtStasrtStop param2=-1,设置失败，返回值不为1
			gui.cls_printf("param2=-1参数异常测试...".getBytes());
			if((mRet=mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "-1"))==1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}

		//case2.2: TrsmtStasrtStop param2=2,设置失败，返回值不为1
			gui.cls_printf("param2=2参数异常测试...".getBytes());
			if((mRet=mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "2"))==1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}

		//case2.3: TrsmtStasrtStop param2=NULL,设置失败，返回值不为1
			gui.cls_printf("param2=NULL参数异常测试...".getBytes());
			if((mRet=mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", null))==1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
		//case2.4: TrsmtStasrtStop param2="",设置失败，返回值不为1	
			gui.cls_printf("param2=“”参数异常测试...".getBytes());
			if((mRet=mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", ""))==1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
		//case2.5: param1=NULL,设置失败，返回值不为1	
			gui.cls_printf("param1=NULL参数异常测试...".getBytes());
			if((mRet=mSoftEngine.scanSet("CODERBAR", null, "0"))==1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
		//case2.6: param1="",设置失败，返回值不为1		
			gui.cls_printf("param1=“”参数异常测试...".getBytes());
			if((mRet=mSoftEngine.scanSet("CODERBAR", "", "1"))==1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName, funcName, 0, "子用例1：参数异常测试通过");
		}	
		
		private void unitTest()
		{
			int mRet=-1;
			String funcName = "unitTest";
			String camTip = scanDefineInfo.getCameraInfo();
			LoggerUtil.d("unitTest->camTip"+camTip);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			// 测试前置:
			mHolder = surfaceView.getHolder();
			mHolder.addCallback(mHolderCallback);
			initCamera(camTip,scanDefineInfo);
			// 初始化扫码
			mSoftEngine.setScanningCallback(mScanningCallback);						
						
		//case3.1: 默认测试，开启传送起始终止符，应为默认的ABCD模式，设置成功，返回1，扫AxA码扫码成功，扫axa码扫码失败					
			gui.cls_show_msg1(2, "case3.1:默认测试，开启传送起始终止符，应为默认的ABCD模式，扫AxA码扫码成功，扫axa码扫码失败");
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
	
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_AxA");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="A1234567890A")
				gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_axa");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="a1234567890a")
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			else
			{
				gui.cls_show_msg("case3.1测试通过,点任意键继续...");
			}

			
		//正常测试
		//case3.2: 开启传送起始终止符，模式为TNXE,设置成功，返回1，扫TxT码扫码成功，扫exe码扫码失败		
			gui.cls_show_msg1(2, "case3.2:开启传送起始终止符，模式为TNXE,扫TxT码扫码成功，扫exe码扫码失败");
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((mRet = mSoftEngine.scanSet("CODERBAR", "startStopMode", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_TxT");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="T1234567890T")
				gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_exe");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="e1234567890e")
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			else
			{
				gui.cls_show_msg("case3.2测试通过,点任意键继续...");
			}


		//case3.3: 开启传送起始终止符，模式为ABCD,设置成功，返回1，扫DxD码扫码成功，扫bxb码扫码失败	
			gui.cls_show_msg1(2, "case3.3:开启传送起始终止符，模式为ABCD，扫DxD码扫码成功，扫bxb码扫码失败");
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((mRet = mSoftEngine.scanSet("CODERBAR", "startStopMode", "0"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_DxD");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="D1234567890D")
				gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="b1234567890b")
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			else
			{
				gui.cls_show_msg("case3.3测试通过,点任意键继续...");
			}

		//case3.4: 开启传送起始终止符，模式为abcd,设置成功，返回1，扫bxb码扫码成功，扫CxC码扫码失败		
			gui.cls_show_msg1(2, "case3.4:开启传送起始终止符，模式为abcd,扫bxb码扫码成功，扫CxC码扫码失败");
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((mRet = mSoftEngine.scanSet("CODERBAR", "startStopMode", "3"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="b1234567890b")
				gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_CxC");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="C1234567890C")
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			else
			{
				gui.cls_show_msg("case3.4测试通过,点任意键继续...");
			}

		//case3.5: 开启传送起始终止符，模式为tnxe,设置成功，返回1，扫nxn码扫码成功，扫TxT码扫码失败		
			gui.cls_show_msg1(2, "case3.5:开启传送起始终止符，模式为tnxe，扫nxn码扫码成功，扫TxT码扫码失败");
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((mRet = mSoftEngine.scanSet("CODERBAR", "startStopMode", "4"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_nxn");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="n1234567890n")
				gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_TxT");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="T1234567890T")
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			else
			{
				gui.cls_show_msg("case3.5测试通过,点任意键继续...");
			}

		//case3.6: 关闭传送起始终止符，设置成功，返回1，无法扫出起始和终止符
			gui.cls_show_msg1(2, "case3.6:关闭传送起始终止符，无法扫出起始和终止符");
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "0"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_nxn");
			testCodeType(camTip, scanDefineInfo);
			if(mCodeResult=="1234567890")
				gui.cls_show_msg("case3.6测试通过,扫码结果:%s,点任意键继续...",mCodeResult);
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			//case4.1 开启传送起始终止符模式，退出扫码后再次进入应保持上次设置的模式
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			gui.cls_printf("扫码退出后再次进入扫码，会保持上次设置的值".getBytes());
			String camTip1 = scanDefineInfo.getCameraInfo();
			if((mRet = mSoftEngine.scanSet("CODERBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((mRet = mSoftEngine.scanSet("CODERBAR", "startStopMode", "3"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("第一次扫码:请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb,放置完毕点任意键继续测试");
			testCodeType(camTip, scanDefineInfo);
			String mCodeResult1=mCodeResult;
			gui.cls_show_msg("第二次扫码:请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb,放置完毕点任意键继续测试");
			testCodeType(camTip, scanDefineInfo);
			if( mCodeResult1.equals(mCodeResult))
				gui.cls_show_msg("case4.1测试通过,点任意键继续...");
			else
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		
			
			//case4.2 重启后再进入扫码，CODEBAR是为关闭传送起止终止符模式
			if(gui.cls_show_msg("重启后再进入扫码，CODEBAR是为关闭传送起止终止符模式,是否立即重启(重启后要进入2.重启验证测试),[确认键]重启")==ENTER)
			{
				Tools.reboot(myactivity);
			}
			gui.cls_show_msg1_record(fileName, funcName, 0, "子用例2：测试通过");
		
		}
		
		private void unitLengthTest()
		{
			String funcName = "unitLengthTest";
			int mRet=-1;
			String camTip = scanDefineInfo.getCameraInfo();
			LoggerUtil.d("unitTest->camTip"+camTip);
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
			// 测试前置
			mHolder = surfaceView.getHolder();
			mHolder.addCallback(mHolderCallback);
			initCamera(camTip,scanDefineInfo);
			// 初始化扫码
			mSoftEngine.setScanningCallback(mScanningCallback);
			
			   //打开使能开关
			if((mRet = mSoftEngine.scanSet("CODEBAR", "Enable", "0"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((mRet = mSoftEngine.scanSet("CODEBAR", "Enable", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			
		    //长度边界测试，默认ABCD模式
			//case5.1: 异常测试：码长为3时，扫码失败
			gui.cls_show_msg1(3, "case5.1: 异常测试：码长为3时，扫码失败");
			if((mRet = mSoftEngine.scanSet("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_3");
			testCodeType(camTip, scanDefineInfo);
			if((mCodeResult.length()-2)==3)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case5.2: 异常测试：码长为128时，扫码失败	
			gui.cls_show_msg1(3, "case5.2: 异常测试：码长为128时，扫码失败");
			gui.cls_show_msg("点任意键继续测试,请放置码长为128的AxA码于后置摄像头");
			testCodeType(camTip, scanDefineInfo);
			if((mCodeResult.length()-2)==128)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}	
			
			//case5.3: 异常测试：码长为129时，扫码失败
			gui.cls_show_msg1(3, "case5.3: 异常测试：码长为129时，扫码失败");
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_129");
			testCodeType(camTip, scanDefineInfo);
			if((mCodeResult.length()-2)==129)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case5.4: 正常测试：码长为4时，应该扫码成功
			gui.cls_show_msg1(3, "case5.4: 正常测试：码长为4时，扫码成功");
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_4");
			testCodeType(camTip, scanDefineInfo);
			if((mCodeResult.length()-2)!=4)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			//case5.5: 正常测试：码长为127时，应该扫码成功
			gui.cls_show_msg1(3, "case5.5: 正常测试：码长为127时，扫码成功");
			gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_127");
			testCodeType(camTip, scanDefineInfo);		
			if((mCodeResult.length()-2)!=127)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		private void rebootVerfity()
		{
			gui.cls_show_msg("重启验证:请放置图片TestCase/高端/testool_JDK/others/scan/codebar_AxA,放置完毕任意键继续");
			String camTip = scanDefineInfo.getCameraInfo();
			testCodeType(camTip, scanDefineInfo);
			gui.cls_show_msg1_record(fileName, "rebootVerfity", 0, "无法扫出起始终止符才可视为测试通过");
			
		}

		/**
		 * 测试码
		 * @param isCompare 是否比较码值
		 * @param codeType 扫码的类型
		 * @param camTip 扫码的摄像头
		 * @param scanDefineInfo 摄像头信息
		 * @param codeValue 比较的码值
		 */
		private void testCodeType(String camTip,ScanDefineInfo scanDefineInfo)
		{
			gui.cls_printf("正在扫码".getBytes());
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
			mSoftEngine.stopDecode();
//			gui.cls_show_msg("扫码结果=%s", mCodeResult);
			LoggerUtil.v("testCodeType|||"+isScan+",isStartYuvDecode="+isTheTimeEnd);
//			testEnd();
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
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_RED);
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(HandlerMsg.TEXTVIEW_COLOR_BLACK);
	}
	








};
