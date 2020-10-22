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
 * file name 		: Scan17.java 
 * Author 			: wangxiaoyu
 * version 			: 
 * DATE 			: 20170301
 * directory 		: NLS配置码制接口
 * description 		: setNlsScan(String pram1,String pram2,String pram3)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20170301    	    created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan17 extends UnitFragment
{
	private final String TESTITEM =  "(ScanUtil)setNlsScan";
	// 自动识别软解码
	private ScanUtil softManager3;
	private String fileName=Scan17.class.getSimpleName();
	private HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
	private HashMap<Code_Type, String> codeMap2 = new HashMap<Code_Type, String>();
	private String [] arr=new String[]{"QR","DM","PDF417","CODEBAR","CODE39","CODE93","CODE128","EAN8","EAN13","UPCA","UPCE","ISBN","ISSN","UCCEAN128"};//支持的码制
	private int ret = NDK_ERR;
	private Gui gui = new Gui(myactivity, handler);
	
	@SuppressLint("NewApi")
	public void scan17()
	{
		/*private & local definition*/
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		/**二维码*/
		codeMap.put(Code_Type.QR_UTF8_1, "方式");
		codeMap.put(Code_Type.QR_UTF8_2, QR_UTF8);
		codeMap2.put(Code_Type.QR_UTF8_2, QR_UTF8);
		codeMap.put(Code_Type.QR_GBK, QR_GBK);
		codeMap.put(Code_Type.QR_ECI, "QR Code(UTF-8,带ECI前缀):中国1A2B3C4D5F");
		codeMap.put(Code_Type.DataMatrix, "Data Matrix:1A2B3C4D");
		codeMap.put(Code_Type.PDF417, "PDF417:1A2B3C4D");
		/**条形码*/
		codeMap.put(Code_Type.CodeBar, "9876543210321");
		codeMap.put(Code_Type.Code39, "Co39");
		codeMap.put(Code_Type.Code93, "ABCabc123");
		codeMap.put(Code_Type.Code128, "code128(a)*%,");
		codeMap.put(Code_Type.EAN_8, "12345670");
		codeMap.put(Code_Type.EAN_8_ADD, "1234567012345");
		codeMap.put(Code_Type.EAN_13, "1234567890128");
		codeMap.put(Code_Type.EAN_13_ADD, "123456789012812345");
		codeMap.put(Code_Type.UPC_A, "123456789012");
		codeMap.put(Code_Type.UPC_A_ADD, "12345678901212345");
		
		ScanUtil tempScanUtil = new ScanUtil(myactivity);
		String nlsVersion = tempScanUtil.getNLSVersion();
		nlsVersion = nlsVersion.substring(nlsVersion.indexOf("SoftEngine:")-4,nlsVersion.indexOf("SoftEngine:")-1);
		LoggerUtil.e("scan8->nlsVer="+nlsVersion+"=========");
		int nlsVersionDig = Integer.parseInt(nlsVersion.substring(1));
		LoggerUtil.e("scan8->nlsVersionDig="+nlsVersionDig+"=========");
		
		codeMap.put(Code_Type.UPC_E, nlsVersionDig>=17?"1234565":"01234565");//扫码库在B17之后UPC-E是不输出的 20200623
		codeMap.put(Code_Type.UPC_E_ADD, "0123456512345");
		codeMap.put(Code_Type.ISBN_ISSN, "9780194315104");
		codeMap.put(Code_Type.UCC_EAN_128, "83979222");
		
		/**二维码*/
		codeMap2.put(Code_Type.DataMatrix, "Data Matrix:1A2B3C4D");
		codeMap2.put(Code_Type.PDF417, "PDF417:1A2B3C4D");
		/**条形码*/
		codeMap2.put(Code_Type.CodeBar, "9876543210321");
		codeMap2.put(Code_Type.Code128, "code128(a)*%,");
		codeMap2.put(Code_Type.EAN_8, "12345670");
		codeMap2.put(Code_Type.UPC_A_ADD, "12345678901212345");
		codeMap2.put(Code_Type.ISBN_ISSN, "9780194315104");
		codeMap2.put(Code_Type.UCC_EAN_128, "83979222");
		
		int cameraId=0;
		ScanDefineInfo scanInfo = getCameraInfo();
		String tipMsg = scanInfo.getCameraInfo();
		cameraId = scanInfo.getCameraId();
		if(cameraId==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		/*process body*/
		gui.cls_show_msg("请用02文档内的《测试用各码制条码.doc》、《测试用各码制二维码.docx》进行扫码测试,任意键继续");
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		try 
		{
			//初始化
			softManager3 = new ScanUtil(myactivity, surfaceView, cameraId, true, 10*1000,1);
			softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
			
			if (gui.ShowMessageBox("是否已重启再次执行本用例？".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
			{
				gui.cls_show_msg1(2, "重启后,NLS配置库恢复为初始状态(默认所有支持的码制均使能),扫所有码制应成功");
				for (Code_Type key : codeMap2.keySet()) 
				{
					if (key.toString().contains("ADD")) 
					{
						softManager3.setNlsScn("UPC/EAN", "Add-On", "Enable");
					} else {
						softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
					}
					if((ret = scanUtilCheck(softManager3,String.format("请在%s摄像头放置%s码", tipMsg,key), gui, codeMap.get(key)))!=NDK_SCAN_OK)
					{
						gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫%s码失败(ret = %d,预期code=%s,实际code = %s)", 
								Tools.getLineInfo(),TESTITEM,key,ret,codeMap.get(key),mCodeResult);
						if(!GlobalVariable.isContinue)
							return;
					}
				}
				softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
				softManager3.release();
				
			}
			else{
				//第一次进入用例
				// case1:参数异常测试,预期应返回-1,实际返回1,目前自动识别那边还没实现
				/*if(gui.cls_show_msg(2, "参数异常测试：参数设置为空字符串,退出键退出测试")==true)
				{
					unitEnd();
					return;
				}
				if((ret = softManager3.setNlsScn("", "Enable", "0"))!=-1)
				{
					gui.cls_show_msg1(gKeepTimeErr, SERIAL,"line%d:%s参数异常测试失败ret = %d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
					{
						softManager3.release();
						return;
					}
				}
				if((ret = softManager3.setNlsScn("CODE128", "", "0"))!=-1)
				{
					gui.cls_show_msg1(gKeepTimeErr, SERIAL,"line%d:%s参数异常测试失败ret = %d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
					{
						softManager3.release();
						return;
					}
				}
				if((ret = softManager3.setNlsScn("CODE128", "Enable", ""))!=-1)
				{
					gui.cls_show_msg1(gKeepTimeErr, SERIAL,"line%d:%s参数异常测试失败ret = %d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
					{
						softManager3.release();
						return;
					}
				}
				softManager3.release();*/
			
				//case 2关闭全部码制,预期扫任何码制均失败 
//				softManager3 = new ScanUtil(myactivity, surfaceView, 0, true, 10*1000,1);
				
				if(gui.cls_show_msg1(2, "关闭全部码制,扫任何码制应均失败,[取消]退出测试")==ESC)
					return;
				   //遍历关闭所有码制
				for (int i = 0; i < arr.length; i++) 
				{
					if((ret = softManager3.setNlsScn(arr[i], "Enable", "0"))!=1)
					{
						gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭%s码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,arr[i],ret);
						if(!GlobalVariable.isContinue)
							return;
					}
				}
				
				//关闭后,遍历codeMap2扫码应失败
				for (Code_Type key : codeMap2.keySet()) 
				{
					if (key.toString().contains("ADD")) 
					{
						softManager3.setNlsScn("UPC/EAN", "Add-On", "Enable");
					} else 
					{
						softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
					}
					if ((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置%s码", tipMsg,key), gui,codeMap.get(codeMap.get(key)))) != NDK_SCAN_FAULT) 
					{
						gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫%s码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(), TESTITEM,key, ret,mCodeResult);
						if (!GlobalVariable.isContinue) 
							return;
					}
				}
				
				//case3.1  只开启QRCode,放置QR_code应扫码成功
				if(gui.cls_show_msg1(2, "只开启QRCode码制进行扫码,预期扫QR码成功,扫其他码制失败,[取消]退出测试")==ESC)
					return;
				if((ret = softManager3.setNlsScn("QR", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开QR码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}

				//扫QR以外的码,应该失败,DM,code128,EAN8
				if((ret = scanUtilCheck(softManager3,String.format("请在%s摄像头放置DataMatrix码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.DataMatrix))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫DataMatrix码预期失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫QR相关的码应成功
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR_GBK码", tipMsg), gui, codeMap.get(Code_Type.QR_GBK)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR_GBK码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_GBK),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置code128码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.Code128))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫code128码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置EAN8码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.EAN_8))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫EAN8码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//关闭QR
				if((ret = softManager3.setNlsScn("QR", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭QR码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case3.2.1只开启Code128码制
				if(gui.cls_show_msg1(2, "只开启Code128码制进行扫码,预期扫Code128码成功,扫其他码制失败,[取消]退出测试")==ESC)
					return;
				//打开Code128
				if((ret = softManager3.setNlsScn("CODE128", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开code128码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫code128,应成功
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置Code128码", tipMsg), gui, codeMap.get(Code_Type.Code128)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫Code128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code128),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫code128以外的其他码制,应失败,QR,PDF417,UPCA_ADD
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR(UTF8带前缀ECI)码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.QR_ECI))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR(UTF8带前缀ECI)码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3,String.format("请在%s摄像头放置PDF417码", tipMsg) , gui, codeMap.get(codeMap.get(Code_Type.PDF417))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫PDF417码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				softManager3.setNlsScn("UPC/EAN", "Add-On", "Enable");
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置UPCA_ADD码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.UPC_A_ADD))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫UPCA_ADD码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
					{
						softManager3.release();
						softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
						return;
					}
				}
				softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
				//关闭CODE128
				if((ret = softManager3.setNlsScn("CODE128", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭code128码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case3.2.2只开启UCC/EAN128码制
				if(gui.cls_show_msg1(2, "只开启UCC/EAN128码制进行扫码,预期扫UCC/EAN128码成功,扫其他码制失败,[取消]退出测试")==ESC)
					return;
				//打开UCC_EAN_128
				if((ret = softManager3.setNlsScn("UCCEAN128", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开UCCEAN128码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫UCC_EAN_128,应成功
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置UCC/EAN128码", tipMsg), gui, codeMap.get(Code_Type.UCC_EAN_128)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫UCC/EAN128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UCC_EAN_128),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫UCC_EAN_128之外的其他码制,应失败,QR,ISBN_ISSN,EAN13
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR(GBK)码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.QR_GBK))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR(GBK)码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3,  String.format("请在%s摄像头放置ISBN_ISSN码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.ISBN_ISSN))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫ISBN_ISSN码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置EAN13码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.EAN_13))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫EAN13码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//关闭UCC_EAN_128
				if((ret = softManager3.setNlsScn("UCCEAN128", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭UCC_EAN_128码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case3.2.3只开启DM码制
				if(gui.cls_show_msg1(2, "只开启DataMatrix码制进行扫码,预期扫DataMatrix码成功,扫其他码制失败,[取消]退出测试")==ESC)
					return;
				//打开DataMatrix
				if((ret = softManager3.setNlsScn("DM", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开DataMatrix码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫DataMatrix,应成功
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置DataMatrix码", tipMsg), gui, codeMap.get(Code_Type.DataMatrix)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫DataMatrix码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.DataMatrix),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫DataMatrix以外的其他码制,应失败,QR,Code93,UCCEAN128
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR(UTF)方式码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.QR_UTF8_1))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR(UTF)方式码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置Code93码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.Code93))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫Code93码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置UCCEAN128码", tipMsg), gui, codeMap.get(codeMap.get(Code_Type.UCC_EAN_128))))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫UCCEAN128码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//关闭DataMatrix
				if((ret = softManager3.setNlsScn("DM", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭DataMatrix码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				
				//case4 开启全部码制
				if(gui.cls_show_msg1(2, "开启全部码制,扫任何码制均成功,[取消]退出测试")==ESC)
					return;
				   //遍历开启所有码制

				for (int i = 0; i < arr.length; i++) {
					if((ret = softManager3.setNlsScn(arr[i], "Enable", "1"))!=1)
					{
						gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s开启%s码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,arr[i],ret);
						if(!GlobalVariable.isContinue)
							return;
					}
				}
				//关闭后,遍历扫码,应成功
				for (Code_Type key : codeMap2.keySet()) 
				{
					String str="Disable";
					if (key.toString().contains("ADD")) 
					{
						str="Enable";
					} else 
					{
						str="Disable";
					}
					if((ret=softManager3.setNlsScn("UPC/EAN", "Add-On", str))!=1)
					{
						gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s预期附加码状态为%s,实际操作失败(ret=%d)", Tools.getLineInfo(),TESTITEM,str,ret);
						if(!GlobalVariable.isContinue)
							return;
					}
					if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置%s码", tipMsg,key), gui, codeMap2.get(key)))!=NDK_SCAN_OK)
					{
						gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫%s码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,key,ret,codeMap2.get(key),mCodeResult);
						if(!GlobalVariable.isContinue)
							return;
					}
				}
				//case5.1只关闭QR码
				if(gui.cls_show_msg1(2, "只关闭QR相关码制,预期扫QR码失败,扫其他码制均成功,[取消]退出测试")==ESC)
					return;
				if((ret = softManager3.setNlsScn("QR", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭QR码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫QR,应失败
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR_UTF8_1方式码", tipMsg), gui, codeMap.get(Code_Type.QR_UTF8_1)))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR_UTF8_1码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				
				}
				//扫QR以外的码制,应成功,CODE128,UCCEAN128,PDF417
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置CODE128码", tipMsg), gui, codeMap.get(Code_Type.Code128)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫CODE128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code128),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置UCCEAN128码", tipMsg), gui, codeMap.get(Code_Type.UCC_EAN_128)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫UCCEAN128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UCC_EAN_128),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置PDF417码", tipMsg), gui, codeMap.get(Code_Type.PDF417)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫PDF417码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.PDF417),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//打开QR码制
				if((ret = softManager3.setNlsScn("QR", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开QR码制,返回ret=%d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//case5.2只关闭CODE93码
				if(gui.cls_show_msg1(2, "只关闭CODE93相关码制,预期扫CODE93码失败,扫其他码制均成功,[取消]退出测试")==ESC)
					return;
				if((ret = softManager3.setNlsScn("CODE93", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭CODE93码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫CODE93,应失败
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置CODE93码", tipMsg), gui, codeMap.get(Code_Type.Code93)))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫CODE93码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫CODE93以外的码制,应成功,QR,Code39,DM
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR(GBK)码", tipMsg), gui, codeMap.get(Code_Type.QR_GBK)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR(GBK)码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_GBK),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置Code39码", tipMsg), gui, codeMap.get(Code_Type.Code39)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫Code39码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code39),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置DataMatrix码", tipMsg), gui, codeMap.get(Code_Type.DataMatrix)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫DataMatrix码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.DataMatrix),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//打开code93码制
				if((ret = softManager3.setNlsScn("CODE93", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开Code93码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
					
				//case5.3只关闭DM码制
				if(gui.cls_show_msg1(2, "只关闭DataMatrix码,预期扫DataMatrix码失败,扫其他码制均成功,[取消]退出测试")==ESC)
					return;
				if((ret = softManager3.setNlsScn("DM", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭DataMatrix码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;						
				}
				//扫DataMatrix,应失败
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置DataMatrix码", tipMsg), gui, codeMap.get(Code_Type.DataMatrix)))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫DataMatrix码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫DataMatrix以外的码制,应成功,QR,UPCE,EAN13_ADD
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR(UTF)方式码", tipMsg), gui, codeMap.get(Code_Type.QR_UTF8_1)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR（UTF）方式码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_1),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置UPCE码", tipMsg), gui, codeMap.get(Code_Type.UPC_E)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫UPC_E方式码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_E),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				softManager3.setNlsScn("UPC/EAN", "Add-On", "Enable");
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置EAN13_ADD方式码", tipMsg), gui, codeMap.get(Code_Type.EAN_13_ADD)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫EAN_13_ADD方式码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_13_ADD),mCodeResult);
					if(!GlobalVariable.isContinue)
					{
						softManager3.release();
						softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
						return;
					}
				}
				softManager3.setNlsScn("UPC/EAN", "Add-On", "Disable");
				//打开DataMatrix码制
				if((ret = softManager3.setNlsScn("DM", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开DataMatrix码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
							
				//case5.4只关闭PDF417码制
				if(gui.cls_show_msg1(2, "只关闭PDF417码,预期扫PDF417码失败,扫其他码制均成功,[取消]退出测试")==ESC)
					return;
				if((ret = softManager3.setNlsScn("PDF417", "Enable", "0"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s关闭PDF417码制失败(ret=%d)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫PDF417,应失败
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置PDF417码", tipMsg), gui, codeMap.get(Code_Type.PDF417)))!=NDK_SCAN_FAULT)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫PDF417码失败(预期=-603,ret=%d,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//扫PDF417以外的码制,应成功,QR,CODE128,ISBN_ISSN
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置QR(GBK)码", tipMsg), gui, codeMap.get(Code_Type.QR_GBK)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫QR_GBK码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_GBK),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置CODE128码", tipMsg), gui, codeMap.get(Code_Type.Code128)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫Code128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code128),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				if((ret = scanUtilCheck(softManager3, String.format("请在%s摄像头放置ISBN_ISSN码", tipMsg), gui, codeMap.get(Code_Type.ISBN_ISSN)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s扫ISBN_ISSN码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.ISBN_ISSN),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
				//打开PDF417码制
				if((ret = softManager3.setNlsScn("PDF417", "Enable", "1"))!=1)
				{
					gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr, "line %d:%s打开PDF417码制,返回ret=%d", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg("需重启才能恢复初始NLS配置库,任意键[重启]");	
				//重启
				Tools.reboot(myactivity);
			}
			gui.cls_show_msg1_record(fileName, "scan17", gScreenTime, "%s测试通过", TESTITEM);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan17", gKeepTimeErr,"抛出%s异常", e.getMessage());
		}
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(softManager3!=null)
			softManager3.release();
		softManager3 = null;
		gui = null;
	}
}
