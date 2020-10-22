package com.example.highplattest.scan;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import android.util.Log;

import com.example.highplattest.activity.ServiceActivity.MsgBinder;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan25.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190605
 * directory 		: 对原生扫码数据进行校验
 * description 		: doScanWithRawByte()-不支持Zxing解码
 * related document : 
 * history 		 	: 变更记录							变更时间				变更人员
 *			  		  N910、N700海外支持，A7和A9支持		   	20200415    		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan25 extends UnitFragment{
	private final String TESTITEM = "(Nls)doScanWithRawByte";
	private String fileName=Scan25.class.getSimpleName();
	
	private int MAXWAITTIME=60*1000;// 设置最大的超时时间
	long time;
	
	
	/**
	 * 扫码结果存储 
	 */
	byte[] result_once;
	byte[][]result_CONTINUALLY;
	int  cameraid;
	
	HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
	Gui gui = new Gui(myactivity, handler);
	public void scan25() 
	{ 
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan25", gScreenTime,"%s用例不支持自动化测试,请手动验证",  TESTITEM);
			return;
		}
		
		/**局部变量*/
		
		/**二维码*/
		codeMap.put(Code_Type.QR_UTF8_2, QR_UTF8);//UTF-8
		codeMap.put(Code_Type.QR_GBK, QR_GBK);//GBK
		/**条码*/
		codeMap.put(Code_Type.Code93, "ABCabc123");
		
		/**测试前置，根据硬件配置码获取扫描头信息*/
		ScanDefineInfo scanInfo = getCameraInfo();
	
		/*process body*/
		gui.cls_show_msg("请用02文档内的《测试用各码制条码.doc》、《测试用各码制二维码.docx》进行扫码测试,任意键继续");
		// 设置为永不休眠
		gui.cls_show_msg("测试过程中机具不应进入休眠,请先将休眠时间设大,完成任意键继续");
		
		if((cameraid=scanInfo.getCameraId())==-1)
		{
			gui.cls_show_msg1(3, "无扫描头");
			return;
		}
		
		// case1:前置摄像头扫码测试
		if((cameraid=scanInfo.cameraReal.get(FONT_CAMERA))!=-1)//支持前置
		{
			if(scanInfo.getCameraCnt()==1)//说明是N550或F7产品
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			testCameraAny(Scan_Mode.NLS_1,cameraid,"前置摄像头");
		}
		
		// case2:后置摄像头扫码测试
		if((cameraid=scanInfo.cameraReal.get(BACK_CAMERA))!=-1)//后置摄像头
		{
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			testCameraAny(Scan_Mode.NLS_1,cameraid, "后置摄像头");
		}
		
		// case3:支付扫描头扫码测试
		if((cameraid=scanInfo.cameraReal.get(EXTERNAL_CAMERA))!=-1)
		{
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			testCameraAny(Scan_Mode.NLS_1,cameraid, "支付摄像头");
		}
		// case4:USB摄像头扫码测试
		if((cameraid=scanInfo.cameraReal.get(USB_CAMERA))!=-1)
		{
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
			testCameraAny(Scan_Mode.NLS_1,cameraid, "USB摄像头");
		}
		
		// 测试前置
		releaseScan();
			
		//case4: 连续扫码,校验原生数据(后置暂时不支持初始化连续扫码,代码保留)
		ScanUtil scanUtil = new ScanUtil(myactivity);
		//二维码 UTF-8连续扫码
		if(scanInfo.cameraReal.get(FONT_CAMERA)!=-1&&scanInfo.getCameraCnt()>=2)/**因为硬件的cameraId系统里默认为1，所以要求Camera要至少两个才可以*/
		{
			releaseScan();
			//初始化为前置连续扫码
			gui.cls_show_msg("即将测试前置连续扫码测试(无画面预览),任意键继续");
			
			scanUtil.init(ScanUtil.MODE_CONTINUALLY, 5000, ScanUtil.FOCUS_ON, true);
			setNlsUpcEnable(0);
			//二维码UTF-8扫码
			if(gui.cls_show_msg("UTF-8编码格式连续扫码,扫码次数10次,请将QR_UTF8_2放在前置摄像头20-30处,[取消]退出测试,其他任意键继续")==ESC)
				return;
				
			try{
				result_CONTINUALLY = (byte[][]) scanUtil.doScanWithRawByte();
				LoggerUtil.d("result_CONTINUALLY=="+result_CONTINUALLY);
			}catch(Exception e){
				e.printStackTrace();
			}
			for (byte[] a:result_CONTINUALLY) 
			{
				Log.d("eric", "a=="+Arrays.toString(a));
				Log.d("eric", "codeMap=="+Arrays.toString(codeMap.get(Code_Type.QR_UTF8_2).getBytes()));
				if(a==null)
				{
					gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:扫码失败,未扫出码值", Tools.getLineInfo());
					if (!GlobalVariable.isContinue)
						return;
					else
						continue;
				}
				if (!(Arrays.equals(codeMap.get(Code_Type.QR_UTF8_2).getBytes(),a))) 
				{
					try {
						gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:扫码失败,实际码值=%s", Tools.getLineInfo(),new String(a,"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (!GlobalVariable.isContinue)
						return;
				}
					
			}

			// GBK连续扫码
			if(gui.cls_show_msg("GBK编码格式连续扫码,扫码次数10次,请将QR_GBK放在前置摄像头20-30处,[取消]退出测试,其他任意键继续")==ESC)
				return;
			result_CONTINUALLY = (byte[][]) scanUtil.doScanWithRawByte();
				
			for (byte[] a:result_CONTINUALLY) 
			{
				Log.d("eric", "a=="+Arrays.toString(a));
				if(a==null)
				{
					gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:扫码失败,未扫出码值", Tools.getLineInfo());
					if (!GlobalVariable.isContinue)
						return;
					else
						continue;
				}
				try {
					if (!(Arrays.equals(codeMap.get(Code_Type.QR_GBK).getBytes("GBK"),a))) {
						gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:扫码失败,实际码值=%s", Tools.getLineInfo(),new String(a,"GBK"));
						if (!GlobalVariable.isContinue)
						return;
					}
				} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
				}
					
			}
			//条码UTF-8连续扫码
			if(gui.cls_show_msg("UTF-8编码格式连续扫码,扫码次数10次,请将Code93放在前置摄像头20-30处,[取消]退出测试,其他任意键继续")==ESC)
				return;
			result_CONTINUALLY = (byte[][]) scanUtil.doScanWithRawByte();
				
			for (byte[] a:result_CONTINUALLY) 
			{
				Log.d("eric", "a=="+Arrays.toString(a));
				Log.d("eric", "codeMap=="+Arrays.toString(codeMap.get(Code_Type.Code93).getBytes()));
				if(a==null)
				{
					gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:扫码失败,未扫出码值", Tools.getLineInfo());
					if (!GlobalVariable.isContinue)
						return;
					else
						continue;
				}
				if (!(Arrays.equals(codeMap.get(Code_Type.Code93).getBytes(),a))) {
					try {
						gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:扫码失败,实际码值=%s", Tools.getLineInfo(),new String(a,"UTF-8"));
						if (!GlobalVariable.isContinue)
							return;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							}
				}
			}
		}
		releaseScan();
		gui.cls_show_msg1_record(fileName, "scan25", gScreenTime,"%s测试通过", TESTITEM);
	}
	
	private void testCameraAny(Scan_Mode scanMode,int cameraId,String camereMsg)
	{
		/**局部变量*/
		String message ="";
		int ret;
		
		/**测试前置*/
		releaseScan();
		initScanMode(scanMode,myactivity, surfaceView, cameraid, true, MAXWAITTIME);
		// 关闭附加码开关
		setNlsUpcEnable(0);
		
		//caseX.1:单次扫码,校验原生数据
		//原生数据为二维码UTF-8,获取原生数据后校验
		message="请将QR_UTF8_2,放在"+camereMsg+"20-30处,完成后任意键继续";
		ret=scanRaw(message, gui,codeMap.get(Code_Type.QR_UTF8_2).getBytes(),"GBK");
		if(ret!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:%s扫码测试失败(cameraId=%d,ret=%d,实际码值=%s)", Tools.getLineInfo(),TESTITEM,cameraId,ret,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		//原生数据为条码UTF-8,获取原生数据后校验(条码没有GBK的数据编码格式)
		message="请将Code93,放在"+camereMsg+"20-30处,完成后任意键继续";
		ret=scanRaw( message, gui,codeMap.get(Code_Type.Code93).getBytes(),"UTF-8");
		if(ret!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:%s扫码测试失败(cameraId=%d,ret=%d,实际码值=%s)", Tools.getLineInfo(),TESTITEM,cameraId,ret,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		//原生数据为二维码GBK,获取原生数据后校验
		message="请将QR_GBK,放在"+camereMsg+"20-30处,完成后任意键继续";
		try {
			ret=scanRaw( message, gui,codeMap.get(Code_Type.QR_GBK).getBytes("GBK"),"GBK");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(ret!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:%s扫码测试失败(cameraId=%d,ret=%d,实际码值=%s)", Tools.getLineInfo(),TESTITEM,cameraId,ret,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// caseX.2:未进行释放操作，等待2分钟，摄像头不应有卡卡卡的声音，休眠唤醒后扫码仍应正常
		message="休眠唤醒后仍可以扫码,等待10s后手动休眠,休眠等待10s后手动唤醒并"+camereMsg+"放置 QR_UTF8_2 的二维码在摄像头处,点任意键开始测试";
		ret=scanRaw( message, gui,codeMap.get(Code_Type.QR_UTF8_2).getBytes(),"UTF-8");
		if(ret!=NDK_SCAN_OK)
		{
			gui.cls_show_msg1_record(fileName, "scan25", gKeepTimeErr,"line %d:%s扫码测试失败(cameraId=%d,ret=%d,实际码值=%s)", Tools.getLineInfo(),TESTITEM,cameraId,ret,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
		
	}

}
