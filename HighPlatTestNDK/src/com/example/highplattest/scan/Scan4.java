package com.example.highplattest.scan;

import java.util.HashMap;
import android.newland.scan.ScanUtil;
import android.view.View;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan4.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150725 
 * directory 		: 开始扫码和结束扫码
 * description 		:  doScan()和stopScan()
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150725     	created
 *					扫码库在B17之后UPC-E是不输出的 		20200623		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan4 extends UnitFragment 
{
	private final String TESTITEM = "(ScanUtil+硬)doScan和stopScan";
	private int MAXWAITTIME = 10*1000;
	private String fileName=Scan4.class.getSimpleName();	
	ScanUtil scanUtil = null;
	String stopResult="";
	private String resultCode;
	private Gui gui = new Gui(myactivity, handler);
	
	public void scan4() 
	{
		/*private & local definition*/
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		// 获取扫描的工具包
		int ret = NDK_ERR;
//		String[] values;
		HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
		codeMap.put(Code_Type.QR_UTF8_1, "方式");
		codeMap.put(Code_Type.QR_UTF8_2, QR_UTF8);
		codeMap.put(Code_Type.QR_GBK, QR_GBK);
		codeMap.put(Code_Type.QR_ECI, "QR Code(UTF-8,带ECI前缀):中国1A2B3C4D5F");
		codeMap.put(Code_Type.CodeBar, "9876543210321");
		codeMap.put(Code_Type.Code39, "Co39");
		codeMap.put(Code_Type.Code93, "ABCabc123");
		codeMap.put(Code_Type.Code128, "code128(a)*%,");
		codeMap.put(Code_Type.EAN_8, "12345670");
		codeMap.put(Code_Type.EAN_13, "1234567890128");
		codeMap.put(Code_Type.UPC_A, "123456789012");
		
		ScanUtil tempScanUtil = new ScanUtil(myactivity);
		String nlsVersion = tempScanUtil.getNLSVersion();
		nlsVersion = nlsVersion.substring(nlsVersion.indexOf("SoftEngine:")-4,nlsVersion.indexOf("SoftEngine:")-1);
		LoggerUtil.e("scan8->nlsVer="+nlsVersion+"=========");
		int nlsVersionDig = Integer.parseInt(nlsVersion.substring(1));
		LoggerUtil.e("scan8->nlsVersionDig="+nlsVersionDig+"=========");
		
		codeMap.put(Code_Type.UPC_E, nlsVersionDig>=17?"1234565":"01234565");//扫码库在B17之后UPC-E是不输出的 20200623
		codeMap.put(Code_Type.EAN_128, "00000174571740159067");
		codeMap.put(Code_Type.UCC_EAN_128, "83979222");
		codeMap.put(Code_Type.ISBN_ISSN, "9780194315104");
		codeMap.put(Code_Type.PDF417, "PDF417:1A2B3C4D");
		codeMap.put(Code_Type.Code11, "123456789");
		codeMap.put(Code_Type.Interleaved_2OF5, "012345678905");
		codeMap.put(Code_Type.Industrial_2OF5, "1234568");
		codeMap.put(Code_Type.Matrix_2OF5, "9876543210");
		codeMap.put(Code_Type.GSI_Databar, "1234567890123");
		codeMap.put(Code_Type.MSI_Plessey, "1234567890123");
		codeMap.put(Code_Type.DataMatrix, "1A2B3C4D");
		codeMap.put(Code_Type.ITF_14, "12345678901231");
//		codeMap.put(Code_Type.China_Code, value); /*二维码中没看到汉信码*/
		
		/*process body*/
		// 将按钮设置为停止扫码
		//handler.sendMessage(handler.obtainMessage(HandlerMsg.SCAN_BTN_SET_TEXT, "停止扫码"));
		if(gui.cls_show_msg1(1, "流程异常测试：未初始化开始扫码操作应失败,[取消]可退出测试")==ESC)
			return;
		try 
		{
			// case1:流程异常,硬件未初始化进行扫描,应为null
			if(gui.cls_show_msg1(1, "流程异常：未初始化扫码应失败,[取消]可退出测试")==ESC)
				return;
			scanUtil = new ScanUtil(myactivity);
			scanUtil.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, ScanUtil.CONTINUOUS_COUNT);
			resultCode = (String) scanUtil.doScan();
			if(resultCode!=null)
			{
				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s流程异常失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
//			// case2.1:初始化单次扫码,硬解码支持的各种条码都应扫码成功
//			/*if(gui.cls_show_msg("单次扫码测试:timeout = 10s,【取消】退出,【其他】继续".getBytes())==ESC)
//				return;*/
//			if((ret = scanUtil.init(ScanUtil.MODE_ONCE, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!=ScanUtil.SUCCESS)
//			{
//				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			if(gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30处,【取消】退出,【其他】完成")==ESC)
//				return;
//			if((ret = scanTip(scanUtil, "", handler))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s扫码失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			scanUtil.release();
//			
//			// case3:初始化为连续扫码
//			/*if(gui.cls_show_msg("连续扫码测试：扫码10次,每次timeout = 10s,【取消】退出,【其他】继续")==true)
//				return;*/
//			if((ret = scanUtil.init(ScanUtil.MODE_CONTINUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!=ScanUtil.SUCCESS)
//			{
//				gui.cls_show_msg1(2, SERIAL,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			
//			if(gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30处,【取消】退出,【其他】完成")==ESC)
//				return;
//			values = (String[]) scanUtil.doScan();
//			for (int i = 0; i < values.length; i++) 
//			{
//				if(values[i].startsWith("F"))
//				{
//					gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s第%d次扫码失败（%s）", Tools.getLineInfo(),TESTITEM,i,values[i]==null?"null":values[i]);
//					if(!GlobalVariable.isContinue)
//						return;
//				}
//				else
//					values[i] = values[i].substring(1);
//			}
//			
//			if (gui.ShowMessageBox((Arrays.toString(values) + "与条形码或二维码是否一致？").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK) 
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL,"line %d:%s扫码错误", Tools.getLineInfo(), TESTITEM);
//				if (!GlobalVariable.isContinue)
//					return;
//			}
//			// case4:连续扫码,扫码失败会返回F并继续下次扫码
//			if(gui.cls_show_msg1(1, "连续扫码测试:扫码失败后可继续扫码,【取消】退出测试")==ESC)
//				return;
//			gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30处,扫码两次后将手机移开,完成任意键继续");
//			values = (String[]) scanUtil.doScan();
//			for (int i = 2; i < values.length; i++) 
//			{
//				
//				if(!values[i].startsWith("F"))
//				{
//					gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s第%d次扫码超时失败（%s）", Tools.getLineInfo(),TESTITEM,i,values[i]==null?"null":values[i]);
//					if(!GlobalVariable.isContinue)
//						return;
//				}
//				else
//					values[i] = values[i].substring(1);
//			}
//			scanUtil.release();
			
			// case5:手动扫码
			/*if(gui.cls_show_msg(0, "手动扫码测试,退出键退出测试")==true)
				return;*/
			if((ret = scanUtil.init(ScanUtil.MODE_MANUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if(gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30处,[取消]退出,【其他】完成")==ESC)
				return;
			if((ret = scanUtilDialog(scanUtil, "", handler))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s扫码失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			scanUtil.release();
			
			// case6:用stopScan结束手动扫码,返回值应为null add zhegnxq 20161213
			gui.cls_show_msg1(1, "正在进行手动扫码测试,一分钟后长按[取消]停止扫码,应能结束手动扫码");
			if((ret = scanUtil.init(ScanUtil.MODE_MANUALLY, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!= ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			stopResult = (String) scanUtil.doScan();
			if(stopResult!=null)
			{
				gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr, "line %d:%s扫码错误%s", Tools.getLineInfo(),TESTITEM,stopResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			scanUtil.release();
			
			// case7:硬解码各种支持的码进行测试,初始化为单次扫码
			/*if(gui.cls_show_msg(0, "单次扫码,退出键可退出测试")==true)
				return;*/
//			gui.cls_show_msg("请用02文档内的《测试用各码制条码.doc》、《测试用各码制二维码.docx》进行码制测试,任意键继续");
//			if((ret = scanUtil.init(ScanUtil.MODE_ONCE, MAXWAITTIME, ScanUtil.FOCUS_READING, true))!=ScanUtil.SUCCESS)
//			{
//				gui.cls_show_msg1(2, SERIAL,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫CodeBar
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫CodeBar,放置完毕点任意键", gui, codeMap.get(Code_Type.CodeBar)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫CodeBar码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.CodeBar),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫Code11
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Code11,放置完毕点任意键", gui, codeMap.get(Code_Type.Code11)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Code11码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code11),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫Code39
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Code39,放置完毕点任意键", gui, codeMap.get(Code_Type.Code39)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Code39码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code39),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫Code93
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Code93,放置完毕点任意键", gui, codeMap.get(Code_Type.Code93)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Code93码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code93),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫Code128
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Code128,放置完毕点任意键", gui, codeMap.get(Code_Type.Code128)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Code128码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code128),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫EAN8
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫EAN8,放置完毕点任意键", gui, codeMap.get(Code_Type.EAN_8)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫EAN8码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_8),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			
//			// 扫EAN13
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫EAN13,放置完毕点任意键", gui, codeMap.get(Code_Type.EAN_13)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫EAN13码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_13),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫ITF
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫ITF,放置完毕点任意键", gui, codeMap.get(Code_Type.ITF)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫ITF码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.ITF),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// 扫ITF14
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫ITF-14,放置完毕点任意键", gui, codeMap.get(Code_Type.ITF_14)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫ITF-14码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.ITF_14),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// MSI/plessey
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫MSI/plessey,放置完毕点任意键", gui, codeMap.get(Code_Type.MSI_Plessey)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫MSI/plessey码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.MSI_Plessey),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// UPC_A
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫UPC(A),放置完毕点任意键", gui, codeMap.get(Code_Type.UPC_A)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫UPC(A)码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_A),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// UPC(E)
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫UPC(E),放置完毕点任意键", gui, codeMap.get(Code_Type.UPC_E)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫UPC(E)码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UPC_E),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// Interleaved 2 of 5
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Interleaved 2 of 5,放置完毕点任意键", gui, codeMap.get(Code_Type.Interleaved_2OF5)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Interleaved 2 of 5码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Interleaved_2OF5),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			
//			// Industrial 2 of 5
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Industrial 2 of 5,放置完毕点任意键", gui, codeMap.get(Code_Type.Industrial_2OF5)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Industrial 2 of 5码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Industrial_2OF5),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			// Matrix 2 of 5
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Matric 2 of 5,放置完毕点任意键", gui, codeMap.get(Code_Type.Matrix_2OF5)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫MSI/plessey码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.MSI_Plessey),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//Gsi DataBar
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Gsi DataBar,放置完毕点任意键", gui, codeMap.get(Code_Type.GSI_Databar)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫MSI/plessey码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.MSI_Plessey),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//ISBN/ISSN
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫ISBN/ISSN,放置完毕点任意键", gui, codeMap.get(Code_Type.ISBN_ISSN)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫ISBN/ISSN码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.ISBN_ISSN),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//Standard 2 of 5
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Standard 2 of 5,放置完毕点任意键", gui, codeMap.get(Code_Type.Standard_2OF5)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Standard 2 of 5码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Standard_2OF5),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//Data Matrix 
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫Data Matrix,放置完毕点任意键", gui, codeMap.get(Code_Type.DataMatrix)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫Data Matrix码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.DataMatrix),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//QR(UTF_8)
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫QR(UTF-8),码值为方式,放置完毕点任意键", gui, codeMap.get(Code_Type.QR_UTF8_1)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫QR(UTF-8)码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_1),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//QR(UTF_8)
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫QR(UTF-8),码值:"+QR_UTF8+",放置完毕点任意键", gui, codeMap.get(Code_Type.QR_UTF8_2)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫QR(UTF-8)码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_2),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//QR(GBK)
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫QR(GBK),放置完毕点任意键", gui, codeMap.get(Code_Type.QR_GBK)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫QR(GBK)码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_GBK),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//QR(UTF_8,带ECI前缀)
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫QR(UTF-8,带ECI前缀),放置完毕点任意键", gui, codeMap.get(Code_Type.QR_ECI)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫QR(UTF-8,ECI)码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_ECI),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
//			//PDF417
//			if((ret=scanTip(scanUtil, "请用前置摄像头扫PDF 417,码值为方式,放置完毕点任意键", gui, codeMap.get(Code_Type.PDF417)))!=NDK_SCAN_OK)
//			{
//				gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:%s扫PDF 417码失败ret = %d,预期code=%s,实际code = %s", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.PDF417),mCodeResult);
//				if(!GlobalVariable.isContinue)
//					return;
//			}
			gui.cls_show_msg1_record(fileName, "scan4", gScreenTime, "%s测试通过", TESTITEM);
		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan4", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	


	@Override
	public boolean onLongClick(View view) {
		super.onLongClick(view);
		switch (view.getId()) 
		{
		case R.id.btn_key_esc:
			new Thread()
			{
				public void run() 
				{
					scanUtil.stopScan();
				};
			}.start();
			break;

		default:
			break;
		}
		return true;
	}



	@Override
	public void onTestUp() 
	{
		
	}

	@Override
	public void onTestDown() 
	{
		if(scanUtil!=null)
			scanUtil.release();
		gui = null;
		scanUtil = null;
	}
}
