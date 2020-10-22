package com.example.highplattest.scan;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan14.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160722
 * directory 		: 设置UPC_EAN的附加码开关
 * description 		: setNlsUPCEANSwitch(int key)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20160722    	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan14 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil+软)setNlsUPCEANSwitch(int key)";
	// 硬解码
	private ScanUtil softManager1;
	// zxing软解码
	private ScanUtil softManager2;
	// 自动识别软解码
	private ScanUtil softManager3;
	private String fileName=Scan14.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	@SuppressLint("NewApi")
	public void scan14()
	{
		/*private & local definition*/
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		int ret = -1,cameraId=0;
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		cameraId = scanInfo.getCameraId();
		
		LoggerUtil.i("scan14,cameraId="+cameraId);
		if(cameraId==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		
		HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
		codeMap.put(Code_Type.EAN_8, "12345670");
		codeMap.put(Code_Type.EAN_8_ADD, "1234567012345");
		codeMap.put(Code_Type.EAN_13, "1234567890128");
		codeMap.put(Code_Type.EAN_13_ADD, "123456789012812345");
		codeMap.put(Code_Type.UPC_A, "123456789012");
		codeMap.put(Code_Type.UPC_A_ADD, "12345678901212345");
		codeMap.put(Code_Type.UPC_E, "01234565");
		codeMap.put(Code_Type.UPC_E_ADD, "0123456512345");
		
		/*process body*/
		gui.cls_show_msg("请用02文档内的《测试用各码制条码.doc》进行扫码测试,任意键继续");
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		try 
		{
			softManager1 = new ScanUtil(myactivity);
			softManager3 = new ScanUtil(myactivity, surfaceView, cameraId, true, 10*1000, 1);
			// case1:参数异常测试，参数设置为-1，应返回
			if(gui.cls_show_msg1(1, "参数异常测试:参数设置为-1,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(-1))!=ScanUtil.NLS_ERR_PARAM)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s参数异常测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
					
			}
			softManager3.release();
			
//			// case2.1:zxing软解码实例化操作，参数设置为1，UPC_A不应输出附加码
//			if(gui.cls_show_msg1(1, "附加码测试:UPC_A不输出附加码,[取消]退出测试")==ESC)
//				return;
//			softManager2 = new ScanUtil(myactivity, surfaceView, cameraId, true, 10*1000,1);
//			if((ret = softManager2.setNlsUPCEANSwitch(1))!=ScanUtil.NLS_SUCCESS)
//			{
//				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			if((ret = scanTip(softManager2, String.format("zxing软解,请在%s摄像头横向放置带附加码UPC_A码", cameraId==0?"后置":"前置"), gui, codeMap.get(Code_Type.UPC_A)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫UPC_A码失败(ret = %d,预期code=%s,实际code = %s)", 
//						Tools.getLineInfo(),TESTITEM,ret,"123456789012",mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			softManager2.release();
			
			// case2.2:硬解码实例化方式，参数设置为1，应返回成功
			if((ret = softManager1.setNlsUPCEANSwitch(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			softManager1.release();
			
			softManager3 = new ScanUtil(myactivity, surfaceView, cameraId, true, 10*1000, 1);
			// case3.1:自动识别解码实例化测试，参数设置为1，EAN13强制输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:EAN13输出附加码,[取消]退出")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码的EAN_13码", tipMsg), gui, codeMap.get(Code_Type.EAN_13_ADD)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫EAN_13码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_13_ADD),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case3.2:自动识别解码实例化测试，参数设置为0，EAN13不输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:EAN13不输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(0))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置不输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码的EAN_13码,放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.EAN_13)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫EAN_13码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_13),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case4.1:自动识别解码实例化测试，参数设置为1，EAN8强制输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:EAN8输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码EAN_8码，放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.EAN_8_ADD)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫EAN_8码失败(ret = %d,预期code=%s，实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_8_ADD),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case4.2:自动识别解码实例化测试，参数设置为0，EAN8不输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:EAN8不输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(0))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置不输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码EAN_8码,放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.EAN_8)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫EAN_8码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_8),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case5.1:自动识别解码实例化测试，参数设置为1，UPC_A强制输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:UPC_A输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置不输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码的UPC_A码,放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.UPC_A_ADD)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫UPC_A码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_A_ADD),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case5.2:自动识别解码实例化测试，参数设置为0，UPC_A不输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:UPC_A不输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(0))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置不输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码的UPC_A码,放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.UPC_A)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫UPC_A码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_A),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case6.1:自动识别解码实例化测试，参数设置为1，UPC_E强制输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:UPC_E输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(1))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码的UPC_E码,放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.UPC_E_ADD)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫ISSN/ISBN码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_E_ADD),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case6.2:自动识别解码实例化测试，参数设置为0，UPC_E不输出附加码
			if(gui.cls_show_msg1(1, "附加码测试:UPC_E不输出附加码,[取消]退出测试")==ESC)
				return;
			if((ret = softManager3.setNlsUPCEANSwitch(0))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"line%d:%s设置不输出附加码失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置带附加码的UPC_E码,放置完毕任意键继续", tipMsg), gui, codeMap.get(Code_Type.UPC_E)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr, "line %d:%s扫UPC_E码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_E),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			softManager3.setNlsUPCEANSwitch(0);
			scanRelease();
			gui.cls_show_msg1_record(fileName, "scan14", gScreenTime, "%s测试通过", TESTITEM);
		} catch (NoClassDefFoundError e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan14", gKeepTimeErr,"案例抛出异常，请查看具体日志");
		}
	}
	


	@Override
	public void onTestUp() {
		
	}


	@Override
	public void onTestDown() {
		scanRelease();
	}
	
	private void scanRelease()
	{
		if(softManager1!=null)
			softManager1.release();
		if(softManager2!=null)
			softManager2.release();
		if(softManager3!=null)
			softManager3.release();
		softManager1 = null;
		softManager2 = null;
		softManager3 = null;
	}
}
