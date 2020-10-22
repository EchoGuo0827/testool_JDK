package com.example.highplattest.scan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
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
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.CameraManagerUtil;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Scan37.java 
 * directory 		: (ScanUtil)测试预览框两个码能扫出两个码值 (A7)
 * description 		: 
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		  新建							20200713     	zhengxq
 *					  修复BUG2020091403237			20200917		郑佳雯
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan37 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil+Camera1)setNlsScn+QR";
	private String fileName=Scan37.class.getSimpleName();
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

	
	public void scan37()
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
		LoggerUtil.e("Scan37,cameraId:"+mCameraId);	
		if(mCameraId==-1)
		{
			gui.cls_show_msg1(1, "该设备无可扫码的摄像头");
			return;
		}
		// 旧的Camera方式测试，只支持带预览画面的摄像头
//		
//		mScanUtil.setThk88Power(1);

		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.单元异常测试\n1.startYuv扫码方式正常测试\n2.doScan扫码方式验证(F7和F10不测该项)\n3.重启验证", TESTITEM);
			
			if(nkeyIn=='0'||nkeyIn=='2'||nkeyIn=='3')
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
				if(GlobalVariable.currentPlatform==Model_Type.F7||GlobalVariable.currentPlatform==Model_Type.F10)
				{
					gui.cls_show_msg1_record(fileName, "scan37", gKeepTimeErr, "F7和F10不支持ScanUtil的doScan方式");
					return;
				}
				unitTest(mResultCallback,1);
				break;
				
			case '3':
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
	
	private void unitParaTest(ResultCallBack resultCallBack)
	{
		int mRet=-1;
		String funcName = "unitParaTest";
		
		// case2.1:Id=null参数异常测试
		gui.cls_printf("Id=null参数异常测试...".getBytes());
		if((mRet = mScanUtil.setNlsScn(null, "CodeNum", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:Id="err Code"参数异常测试
		gui.cls_printf("Id=err Code参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("err Code", "CodeNum", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.3:Id="Q"参数异常测试
		gui.cls_printf("Id=Q参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("Q", "CodeNum", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.4:Param1=NULL参数异常测试
		gui.cls_printf("Param1=NULL参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", null, "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.5:Param1="CodeCount"参数异常测试
		gui.cls_printf("Param1=CodeCount参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "CodeCount", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.6:Param1="C"参数异常测试
		gui.cls_printf("Param1=C参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "C", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.7:Param2=NULL参数异常测试
		gui.cls_printf("Param2=NULL参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "CodeNum", null))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.8:Param1="CodeNum",Param2="0"参数异常测试
		gui.cls_printf("Param2=0参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "CodeNum", "0"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.9:Param1="CodeNum",Param2="3"参数异常测试
		gui.cls_printf("Param2=3参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "CodeNum", "3"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.10:Param1="NumFixed",Param2="-1"参数异常测试
		gui.cls_printf("Param2=-1参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "NumFixed", "-1"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.11:Param1="NumFixed",Param2="2"参数异常测试
		gui.cls_printf("Param2=2参数异常测试...".getBytes());
		if((mRet=mScanUtil.setNlsScn("QR", "NumFixed", "2"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, funcName, 0, "子用例1：参数异常测试通过");
		
		// case4.2:扫码退出后再次进入扫码，多图参数会保持上次设置的值
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		String camTip = scanDefineInfo.getCameraInfo();
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(mHolderCallback);
		initCamera(camTip,scanDefineInfo);
		// 初始化扫码
		mScanUtil.initDecode(resultCallBack);
		gui.cls_printf("扫码退出后再次进入扫码，多图参数会保持上次设置的值".getBytes());		
		if((mRet=mScanUtil.setNlsScn("QR", "CodeNum", "2"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("第一次扫码:请将/SVN/scan/mul_qr.png图片放置在预览框,让预览框里有多码");
		testCodeType(camTip, scanDefineInfo,resultCallBack,0);
		if(gui.cls_show_msg("扫码结果:%s,可扫到两个码值,码值结果相连,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("第二次扫码:请将/SVN/scan/mul_qr.png图片放置在预览框,让预览框里有多码");
		testCodeType(camTip, scanDefineInfo,resultCallBack,0);
		if(gui.cls_show_msg("扫码结果:%s,可扫到两个码值,码值结果相连,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：参数异常测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		// case4.1:设备重启后恢复默认"CodeNum"=1
		if(gui.cls_show_msg("重启后恢复默认多图模式扫一个码,是否立即重启(重启后要进入3.重启验证测试),[确认键]重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		gui.cls_show_msg1_record(fileName, funcName, 0, "子用例2：错误推到测试通过");
	}
	
	/**场景测试*/
	public void unitTest(ResultCallBack resultCallBack,int doScanWay)
	{
		String funcName = "unitTest";
		int mRet=-1;
		String camTip = scanDefineInfo.getCameraInfo();
		String singQrValue = "方式";
		String singCodeValue = "9876543210321";
		String[] qrPics = {"/SVN/scan/mul_qr.png","/SVN/scan/QR_UTF8_1.png.png"};
		String[] codePics={"/SVN/scan/mul_code.png","/SVN/scan/CodeBar.png"};
		
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
		
		// case5.1:CodeNum=2,NumFixed=0(码图个数不固定)，预览框放置多个QR码可解析出多个QR码，预览框放置单个QR码，可解析出单个QR码
		gui.cls_show_msg1(2, "case5.1:预览框多码同图的图片，可解析出多个QR码");
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "2"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("QR", "NumFixed", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		for(int j=0;j<2;j++)
		{
			gui.cls_show_msg("请放置%s图片于预览框位置,放置完毕点任意键继续测试",qrPics[j]);
			testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
			if(j==0)
			{
				if(gui.cls_show_msg("扫码结果:%s;码值为图片的两个码值合并,是[确认],否[其他]", mCodeResult)!=ENTER)
				{
					gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(j=%d,%s)", Tools.getLineInfo(),j,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			else
			{
				if(mCodeResult.equals(singQrValue)==false)
				{
					gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(j=%d,%s)", Tools.getLineInfo(),j,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		// case5.9:CodeNum=2,NumFixed=0(码图个数不固定)，预览框放置一个条形码，一个QR码，只能扫出QR码
		gui.cls_show_msg1(2, "case5.9:预览框放置一个条形码，一个QR码，只能扫出QR码");
		gui.cls_show_msg("请放置/SVN/scan/code_qr.png图片于预览框位置,放置完毕点任意键继续测试");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(mCodeResult.equals("285614263499135991")==false&&mCodeResult.equals("code128(a)*%,")==false)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}

		
		// case5.2:CodeNum=2,NumFixed=0,预览框有多个条形码，只能解析出一个条形码
		gui.cls_show_msg1(2, "case5.2:预览框有多个条形码，只能解析出一个条形码");
		for(int j=0;j<2;j++)
		{
			gui.cls_show_msg("请放置%s图片于预览框位置",codePics[j]);
			testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
			if(j==0)
			{
				if(gui.cls_show_msg("扫码结果:%s;扫码结果为单个条码值,是[确认],否[其他]", mCodeResult)!=ENTER)
				{
					gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(j=%d,%s)", Tools.getLineInfo(),j,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
			else
			{
				if(mCodeResult.equals(singCodeValue)==false)
				{
					gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(j=%d,%s)", Tools.getLineInfo(),j,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		
		// case5.3:CodeNum=2,NumFixed=1（码图个数固定）,预览框放置多个QR码可解析出多个QR码，预览框放置单个QR码，无法解析出单个QR码
		gui.cls_show_msg1(2, "case5.3:预览框多码同图的图片，可解析出多个QR码");
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "2"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("QR", "NumFixed", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		for(int j=0;j<2;j++)
		{
			gui.cls_show_msg("请放置%s图片于预览框位置",qrPics[j]);
			testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
			if(gui.cls_show_msg("扫码结果:%s;%s,是[确认],否[其他]", mCodeResult,j==0?"码值为图片的两个码值合并":"无法解出码")!=ENTER)
			{
				gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(j=%d,%s)", Tools.getLineInfo(),j,mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		// case5.4:CodeNum=2,NumFixed=1,预览框有多个条形码，只能解析出一个条形码
		gui.cls_show_msg1(2, "case5.4:预览框有多个条形码，只能解析出一个条形码");
		gui.cls_show_msg("请放置%s图片于预览框位置",codePics[0]);
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;扫码结果为单个条码值,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case5.5:设置CodeNum=1, 预览框多个QR码，只能解析出一个QR码
		gui.cls_show_msg1(2, "case5.5:预览框有多个QR码，只能解析出一个QR码");
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("QR", "NumFixed", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请放置%s图片于预览框位置",qrPics[0]);
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;扫码结果为单个QR码，,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5.6:设置CodeNum=1, 预览框多个条形码，只能解析出一个条形码
		gui.cls_show_msg1(2, "case5.6:预览框多个条形码，只能解析出一个条形码");
		gui.cls_show_msg("请放置%s图片于预览框位置",codePics[0]);
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;扫码结果为单个条形码,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case5.7:setNlsScn的QR多图值设置为2，setNlsScn传入错误参数，预览框放置多个QR码，能解析出两个QR码
		gui.cls_show_msg1(2, "case5.7:setNlsScn传入错误参数，预览框放置多个QR码，能解析出两个QR码");
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "2"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "-1"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请放置%s图片于预览框位置",qrPics[0]);
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;扫码结果为多个QR码，,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case5.10：休眠唤醒后scanSet模式不应变化
		Tools.setSreenTimeout(myactivity, 15*1000);
		gui.cls_show_msg1(15,"case5.10：休眠唤醒后scanSet模式不应变化,15s后自动休眠,休眠5s后手动唤醒,【无需按键！！！】");
		while (!GlobalVariable.isWakeUp);						
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
/*		if(doScanWay==0)// 休眠的时候camera会被释放，重新唤醒的时候需要手动开启预览画面
		{
			
			initCamera(camTip, scanDefineInfo);
		}*/
		gui.cls_show_msg("设备已唤醒,请放置%s图片于预览框位置",qrPics[0]);
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;扫码结果为多个QR码，,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case5.8:setNlsScn的QR多图值设置为1，setNlsScn传入错误参数，预览框放置多个QR码，只能解析出一个QR码
		gui.cls_show_msg1(2, "case5.8:预览框有多个QR码，只能解析出一个QR码");
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((mRet = mScanUtil.setNlsScn("QR", "CodeNum", "100"))==1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请放置%s图片于预览框位置",qrPics[0]);
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;扫码结果为单个QR码，,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);//调用解析框
		gui.cls_show_msg1_record(fileName, funcName, 0, "%s测试通过", funcName);
	}
	
	/**重启验证*/
	public void rebootVerfity(ResultCallBack resultCallBack)
	{
		gui.cls_show_msg("重启验证:请放置/SVN/scan/mul_qr.png图于摄像头处,放置完毕任意键继续");
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		String camTip = scanDefineInfo.getCameraInfo();
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(mHolderCallback);
		initCamera(camTip,scanDefineInfo);
		// 初始化扫码
		mScanUtil.initDecode(resultCallBack);
		testCodeType(camTip, scanDefineInfo,resultCallBack,0);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
		if(gui.cls_show_msg("扫码结果:%s,扫码结果为单个QR码,是[确认],否[其他]",mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, "rebootVerfity", gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "rebootVerfity", 0, "重启后多码同图时只能扫出一个码才可视为测试通过");
		
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
			LoggerUtil.d("testCodeType========doScan");
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

}
