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
 * file name 		: Scan40.java 
 * directory 		: (ScanUtil)ScanSet支持micro QR码的打开关闭
 * description 		: 
 * related document : 
 * history 		 	: 变更点														变更时间			变更人员
 *			  		  支持micro QR的打开与关闭(N910_A7_V1.0.05)				       20200909     	郑薛晴
 *					 默认MICROQR是关闭状态
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan40 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil)setNlsScn";
	private String fileName=Scan40.class.getSimpleName();
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
	
	
	private Object mLockObj = new Object();
	private String mCodeResult="";
	private String mCodeType="UTF-8";
	ScanDefineInfo scanDefineInfo;
	int mCameraId=-1;
	int mGetTimeout = 0;
	public void scan40()
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
			int nkeyIn = gui.cls_show_msg("%s\n0.startYuv扫码方式正常测试\n1.doScan扫码方式验证\n2.重启验证", TESTITEM);
			
			if(nkeyIn=='1'||nkeyIn=='2')
				mScanUtil = new ScanUtil(myactivity,surfaceView,mCameraId,true,15*1000,1);
			else if(nkeyIn=='0')
			{
				mScanUtil = new ScanUtil(myactivity);
				mScanUtil.getNLSVersion();
			}
			switch (nkeyIn) {
				
			case '0':
				unitTest(mResultCallback,0);
				testEnd();
				break;
				
			case '1':
				unitTest(mResultCallback,1);
				break;
				
			case '2':
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
		
		// case1:打开mirco QR的开关，可正常扫到micro的QR码值（1打开 0关闭）
		
		if((mRet = mScanUtil.setNlsScn("MICROQR", "Enable", "1"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("点任意键继续测试,请放置micro QR码于后置摄像头");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("扫码结果:%s;与实际码值一致？,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2:关闭micro QR的开关，无法扫码micro QR的码值
		if((mRet = mScanUtil.setNlsScn("MICROQR", "Enable", "0"))!=1)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：测试失败(%d)", Tools.getLineInfo(),mRet);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg("点任意键继续测试,请放置micro QR码于后置摄像头");
		testCodeType(camTip, scanDefineInfo,resultCallBack,doScanWay);
		if(gui.cls_show_msg("无法扫到micro QR码,是[确认],否[其他]", mCodeResult)!=ENTER)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, gKeepTimeErr, "line %d：扫码结果错误(%s)", Tools.getLineInfo(),mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4.2 重启后再进入扫码，CODEBAR是为关闭传送起止终止符模式
		if(gui.cls_show_msg("重启后再进入扫码，MICROQR为默认关闭状态,是否立即重启(重启后要进入2.重启验证测试),[确认键]重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		gui.cls_show_msg1_record(fileName, funcName, 0, "子用例1：测试通过");
	}                                        
	
	public void rebootVerfity(ResultCallBack resultCallBack)
	{
		gui.cls_show_msg("重启验证:MICROQR码于摄像头处,放置完毕任意键继续");
		String camTip = scanDefineInfo.getCameraInfo();
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);//调用解析框
		testCodeType(camTip, scanDefineInfo,resultCallBack,1);
		gui.cls_show_msg1_record(fileName, "rebootVerfity", 0, "无法扫出MICROQR码才可视为测试通过");	
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