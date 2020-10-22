package com.example.highplattest.scan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.newland.scan.ScanUtil;
import android.newland.scan.ScanUtil.ResultCallBack;
import android.newland.scan.SoftEngine.ScanningCallback;
import android.view.SurfaceHolder;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Scan39.java 
 * directory 		: (ScanUtil)setNlsScn 传送CODEBAR起始终止符
 * description 		: 
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		        新建				       20200812     	郑佳雯
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan39 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil)setNlsScn";
	private String fileName=Scan39.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private final int  SCAN_TIMEOUT = 15*1000;
	
	private ScanUtil mScanUtil;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private int previewWidth=1280,previewHeight=960;
	private boolean isScan = false;
	private boolean isTheTimeEnd = false;
	
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
			if (mScanUtil != null&&isScan==true) {
//				LoggerUtil.d("data's length=" + data.length + ",width="
//						+ previewWidth + ",height=" + previewHeight);
				mScanUtil.startYUVDecode(data, previewWidth, previewHeight);
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
	int mCameraId=-1;
	int mGetTimeout = 0;
	public void scan39()
	{
		// 获取屏幕休眠时间
		mGetTimeout = Tools.getSreenTimeout(myactivity);
		// 将休眠的超时时间设大
		Tools.setSreenTimeout(myactivity, 30*1000*1000);
		/**放到方法里，放在公共区域 850会报错*/
		ResultCallBack mResultCallback = new ResultCallBack() {

			@Override
			public void onResult(int eventCode, int codeType, byte[] data1,
					byte[] data2, int length) {
				LoggerUtil.e("eventCode = " + eventCode + ",codeType=" + codeType + ",data=" + new String(data1) + ",length=" + length);
				mEventCode = eventCode;
				if(eventCode==1)// 解码成功进行码值对比
				{
					try {
						mCodeResult = new String(data1,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						mCodeResult="UnsupportedEncodingException异常抛出";
					}
				}
				if(eventCode==1||eventCode==-1)
				{
					synchronized (mLockObj) {
						mLockObj.notify();
					}
				}
			}
		};
		/**测试前置*/
		scanDefineInfo = getCameraInfo();
		mCameraId = scanDefineInfo.getCameraId();
		if(mCameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}

		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元参数异常测试\n1.startYuv扫码方式正常测试\n2.doScan扫码方式验证\n3.长度边界测试\n4.重启验证", TESTITEM);
			
			if(nkeyIn=='0'||nkeyIn=='2'||nkeyIn=='3'||nkeyIn=='4')
				mScanUtil = new ScanUtil(myactivity,surfaceView,mCameraId,true,15*1000,1);
			else if(nkeyIn=='1')
			{
				mScanUtil = new ScanUtil(myactivity);
				mScanUtil.getNLSVersion();
			}
			switch (nkeyIn) {
				
			case '0':
				unitParaTest(mResultCallback);
				break;
				
			case '1':
				unitTest(mResultCallback,0);
				testEnd();
				break;
				
			case '2':
				unitTest(mResultCallback,1);
				break;
				
			case '3':
				unitLengthTest(mResultCallback,1);
				break;
				
			case '4':
				rebootVerfity(mResultCallback);
				break;
				
			case ESC:
				Tools.setSreenTimeout(myactivity, mGetTimeout);
				isScan=false;
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	public void unitParaTest(ResultCallBack mResultCallback)
	{
		int mRet=-1;
		String funcName = "unitParaTest";
		gui.cls_show_msg2(1f,"打开使能开关");
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "Enable", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}		
		
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "Enable", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}			
		//异常测试		
		//case2.1: TrsmtStasrtStop param2=-1,设置失败，返回值不为1
		gui.cls_show_msg2(1f,"case2.1:param2=-1参数异常测试...");
		
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "-1"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}

		//case2.2: TrsmtStasrtStop param2=2,设置失败，返回值不为1
		gui.cls_show_msg2(1f,"case2.2:param2=2参数异常测试...");
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}

		//case2.3: TrsmtStasrtStop param2=NULL,设置失败，返回值不为1
		gui.cls_show_msg2(1f,"case2.3:param2=NULL参数异常测试...");
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", null))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2.4: TrsmtStasrtStop param2="",设置失败，返回值不为1	
		gui.cls_show_msg2(1f,"case2.4:param2=“”参数异常测试...");
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", ""))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2.5: param1=NULL,设置失败，返回值不为1	
		gui.cls_show_msg2(1f,"case2.5:param1=NULL参数异常测试...");
		if((mRet=mScanUtil.setNlsScn("CODEBAR", null, "0"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				
				return;
		}
		//case2.6: param1="",设置失败，返回值不为1		
		gui.cls_show_msg2(1f,"case2.6:param1=“”参数异常测试...");
		if((mRet=mScanUtil.setNlsScn("CODEBAR", "", "1"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, funcName, 0, "子用例1：参数异常测试通过");
			
	}
	
	public void unitTest(ResultCallBack resultCallBack,int doScanWay)
	{
		String funcName = "unitTest";
		int mRet=-1;
		String camTip = scanDefineInfo.getCameraInfo();
		LoggerUtil.d("unitTest->camTip"+camTip);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		
		// 测试前置
		if(doScanWay==0)
		{
			mHolder = surfaceView.getHolder();
			mHolder.addCallback(mHolderCallback);
			initCamera(camTip,scanDefineInfo);
			// 初始化扫码
			mScanUtil.initDecode(resultCallBack);
		}
		
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "Enable", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "Enable", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3.1: 默认测试，开启传送起始终止符，应为默认的ABCD模式，设置成功，返回1，扫AxA码扫码成功，扫axa码扫码失败					
		gui.cls_show_msg1(3, "case3.1:默认测试，开启传送起始终止符，应为默认的ABCD模式，扫AxA码扫码成功，扫axa码扫码失败");
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_AxA");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(mCodeResult=="A1234567890A")
			gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
		else
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_axa");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "StartStopMode", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_TxT");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(mCodeResult=="T1234567890T")
			gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
		else
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_exe");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "StartStopMode", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_DxD");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(mCodeResult=="D1234567890D")
			gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
		else
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "StartStopMode", "3"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(mCodeResult=="b1234567890b")
			gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
		else
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_CxC");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "StartStopMode", "4"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_nxn");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(mCodeResult=="n1234567890n")
			gui.cls_show_msg("扫码成功,扫码结果:%s,点任意键继续...",mCodeResult);
		else
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_TxT");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_nxn");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
		gui.cls_show_msg("case4.1:两次扫码结果相同（扫码退出后再次进入扫码，应会保持上次设置的值）。按任意键继续....");
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "StartStopMode", "3"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("第一次扫码:请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb,放置完毕点任意键继续测试");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		String mCodeResult1=mCodeResult;
		gui.cls_show_msg("第二次扫码:请放置图片TestCase/高端/testool_JDK/others/scan/codebar_bxb,放置完毕点任意键继续测试");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
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
	
	private void unitLengthTest(ResultCallBack resultCallBack, int doScanWay)
	{
		String funcName = "unitLengthTest";
		int mRet=-1;
		String camTip = scanDefineInfo.getCameraInfo();
		LoggerUtil.d("unitTest->camTip"+camTip);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		
		// 测试前置
		if(doScanWay==0)
		{
			mHolder = surfaceView.getHolder();
			mHolder.addCallback(mHolderCallback);
			initCamera(camTip,scanDefineInfo);
			// 初始化扫码
			mScanUtil.initDecode(resultCallBack);
		}
		   //打开使能开关
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "Enable", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "Enable", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
	    //长度边界测试，默认ABCD模式
		//case5.1: 异常测试：码长为3时，扫码失败
		gui.cls_show_msg1(3, "case5.1: 异常测试：码长为3时，扫码失败");
		if((mRet = mScanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_3,放置完毕任意键继续");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if((mCodeResult.length()-2)==3)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case5.2: 异常测试：码长为128时，扫码失败	
		gui.cls_show_msg1(3, "case5.2: 异常测试：码长为128时，扫码失败");
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_128,放置完毕任意键继续");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if((mCodeResult.length()-2)==128)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}	
		
		//case5.3: 异常测试：码长为129时，扫码失败
		gui.cls_show_msg1(3, "case5.3: 异常测试：码长为129时，扫码失败");
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_129,放置完毕任意键继续");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if((mCodeResult.length()-2)==129)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case5.4: 正常测试：码长为4时，应该扫码成功
		gui.cls_show_msg1(3, "case5.4: 正常测试：码长为4时，扫码成功");
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_4,放置完毕任意键继续");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if((mCodeResult.length()-2)!=4)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case5.5: 正常测试：码长为127时，应该扫码成功
		gui.cls_show_msg1(3, "case5.5: 正常测试：码长为127时，扫码成功");
		gui.cls_show_msg("点任意键继续测试,请放置图片TestCase/高端/testool_JDK/others/scan/codebar_127,放置完毕任意键继续");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);		
		if((mCodeResult.length()-2)!=127)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
	}
	
	public void rebootVerfity(ResultCallBack resultCallBack)
	{
		gui.cls_show_msg("重启验证:请放置图片TestCase/高端/testool_JDK/others/scan/codebar_AxA,放置完毕任意键继续");
		String camTip = scanDefineInfo.getCameraInfo();
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		testCodeType(camTip, scanDefineInfo,resultCallBack,1);
		gui.cls_show_msg("扫码结果=%s", mCodeResult);
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
	private void testCodeType(String camTip,ScanDefineInfo scanDefineInfo,ResultCallBack resultCallBack,int doScanWay)
	{
		LoggerUtil.d("我的分割线==============");
		gui.cls_printf("正在扫码".getBytes());
		String funcName="testCodeType";
		if(doScanWay==0)// startYuv方式
		{
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
			LoggerUtil.v("testCodeType|||"+isScan+",isStartYuvDecode="+isTheTimeEnd);
		}
		else// doScan方式
		{
			mCodeResult = (String) mScanUtil.doScan();
			mCodeResult = mCodeResult==null?"null":mCodeResult.substring(1);
		}
	}
	
	private void testEnd()
	{
		LoggerUtil.v("testEnd||stopDecode");
		if(mScanUtil!=null)
		{
			// 停止扫码操作
			mScanUtil.stopDecode();
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

};
