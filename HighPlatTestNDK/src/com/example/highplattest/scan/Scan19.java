package com.example.highplattest.scan;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan19.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20180124 
 * directory 		: 硬解码制支持测试
 * description 		: init()
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess			   20180124     	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan19 extends UnitFragment
{
	
	private String fileName=Scan19.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+硬)doSccan";
	private Gui gui;
	
	public void scan19() 
	{
		/*private & local definition*/
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
		codeMap.put(Code_Type.QR_UTF8_1, "方式");
		codeMap.put(Code_Type.QR_UTF8_2, QR_UTF8);
		codeMap.put(Code_Type.QR_GBK, QR_GBK);
		codeMap.put(Code_Type.QR_ECI, "QR Code(UTF-8,带ECI前缀):中国1A2B3C4D5F");
		codeMap.put(Code_Type.PDF417, "PDF417:1A2B3C4D");
		
		codeMap.put(Code_Type.CodeBar, "9876543210321");
		codeMap.put(Code_Type.Code39, "Co39");
		codeMap.put(Code_Type.Code93, "ABCabc123");
		codeMap.put(Code_Type.Code128, "code128(a)*%,");
		codeMap.put(Code_Type.EAN_8, "12345670");
		codeMap.put(Code_Type.EAN_13, "1234567890128");
		codeMap.put(Code_Type.ITF_14, "1234567890123");//设备扫出的码值1234567890123
		codeMap.put(Code_Type.UPC_A, "123456789012");
		
		ScanUtil tempScanUtil = new ScanUtil(myactivity);
		String nlsVersion = tempScanUtil.getNLSVersion();
		nlsVersion = nlsVersion.substring(nlsVersion.indexOf("SoftEngine:")-4,nlsVersion.indexOf("SoftEngine:")-1);
		LoggerUtil.e("scan8->nlsVer="+nlsVersion+"=========");
		int nlsVersionDig = Integer.parseInt(nlsVersion.substring(1));
		LoggerUtil.e("scan8->nlsVersionDig="+nlsVersionDig+"=========");
		
		codeMap.put(Code_Type.UPC_E, nlsVersionDig>=17?"1234565":"01234565");//扫码库在B17之后UPC-E是不输出的 20200623
		codeMap.put(Code_Type.EAN_128, "00000174571740159067");
		codeMap.put(Code_Type.ISBN_ISSN, "9780194315104");
		codeMap.put(Code_Type.UCC_EAN_128, "83979222");
		// 获取扫描的工具包
		int ret = NDK_ERR;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		gui.cls_show_msg("请用02文档内的《测试用各码制条码.doc》、《测试用各码制二维码.docx》进行扫码测试,任意键继续");
		try 
		{
			// case1:进行扫码操作
			if((ret=initScanMode(Scan_Mode.MODE_ONCE, myactivity, surfaceView, 1, true, 5000))!=ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"line %d:%s初始化失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case1.1:应支持各种编码格式的QR码
			// UTF-8编码的QR码
			if((ret = scanTip("硬解,请用前置摄像头扫QR Code(字符编码格式:UTF-8),码值为方式,放置完毕任意键继续", gui,codeMap.get(Code_Type.QR_UTF8_1)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫QR码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_1),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if((ret = scanTip("硬解,请用前置摄像头扫QR Code(字符编码格式:UTF-8),码值："+QR_UTF8+",放置完毕任意键继续", gui,codeMap.get(Code_Type.QR_UTF8_2)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫QR码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_2),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			// GBK编码的QR码
			if((ret = scanTip("硬解,请用前置摄像头扫QR Code(字符编码格式:GBK),放置完毕任意键继续", gui,codeMap.get(Code_Type.QR_GBK)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫QR码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_GBK),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// 带ECI前缀的QR_Code
			if((ret = scanTip("硬解,请用前置摄像头扫QR Code(字符编码格式:UTF-8,带ECI前缀),放置完毕任意键继续", gui,codeMap.get(Code_Type.QR_ECI)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫QR码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_ECI),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case1.2:应支持PDF417码
			if((ret = scanTip("硬解,请用前置摄像头扫PDF417码,放置完毕任意键继续", gui,codeMap.get(Code_Type.PDF417)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫PDF417码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.PDF417),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.3:放Codebar码进行扫码
			if((ret = scanTip("硬解,请用前置摄像头扫Codebar码,放置完毕任意键继续", gui,codeMap.get(Code_Type.CodeBar)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫CodeBar码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.CodeBar),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.4:应支持code39码
			if((ret = scanTip("硬解,请用前置摄像头扫code39码,放置完毕任意键继续", gui,codeMap.get(Code_Type.Code39)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫code39码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code39),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.5:应支持Code93
			if((ret = scanTip("硬解,请用前置摄像头扫Code93码,放置完毕任意键继续", gui,codeMap.get(Code_Type.Code93)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫Code93码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code93),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.6：应支持Code128
			if((ret = scanTip("硬解,请用前置摄像头扫code128码,放置完毕任意键继续", gui,codeMap.get(Code_Type.Code128)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫code128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.7:应支持EAN8无附加码
			if ((ret = scanTip("硬解,请用前置摄像头扫EAN8码,放置完毕任意键继续", gui,codeMap.get(Code_Type.EAN_8))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"line %d:%s扫EAN8码失败(ret = %d,预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM, ret,codeMap.get(Code_Type.EAN_8), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
			
			// case1.8:应支持EAN13无附加码
			if((ret = scanTip("硬解,请用前置摄像头扫EAN13码,放置完毕任意键继续", gui,codeMap.get(Code_Type.EAN_13)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫EAN13码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_13),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.9:应支持EAN-128码
			if((ret = scanTip("硬解,请用前置摄像头扫EAN-128码,放置完毕任意键继续", gui,codeMap.get(Code_Type.EAN_128)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫EAN-128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.10:放ITF码进行扫码
			if((ret = scanTip("硬解,请用前置摄像头扫ITF_14码,放置完毕任意键继续", gui,codeMap.get(Code_Type.ITF_14)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫ITF码失败(ret = %d,预期code=%s,实际code = %s)",Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.ITF_14),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			// case1.11:应支持UPC_A无附加码
			if ((ret = scanTip("硬解,请用前置摄像头扫UPC_A码,放置完毕任意键继续", gui,codeMap.get(Code_Type.UPC_A))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"line %d:%s扫UPC_A码失败(ret = %d,预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM, ret,codeMap.get(Code_Type.UPC_A), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
			
			// case1.12:应支持UPC_E码
			if ((ret = scanTip("硬解,请用前置摄像头扫UPC_E码,放置完毕任意键继续", gui,codeMap.get(Code_Type.UPC_E))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"line %d:%s扫UPC_E码失败(ret = %d,预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM, ret,codeMap.get(Code_Type.UPC_E), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
			
			// case1.13:应支持IBSN/ISSN码
			if ((ret = scanTip("硬解,请用前置摄像头扫IBSN/ISSN码,放置完毕任意键继续", gui,codeMap.get(Code_Type.ISBN_ISSN))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"line %d:%s扫IBSN/ISSN码失败(ret = %d,预期code=%s,实际code = %s)",Tools.getLineInfo(), TESTITEM, ret,codeMap.get(Code_Type.ISBN_ISSN), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}

			// case1.14:应支持GS1-128(UCC/EAN-128)码
			if((ret = scanTip("硬解,请用前置摄像头扫UCC/EAN 128码,放置完毕任意键继续", gui,codeMap.get(Code_Type.UCC_EAN_128)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr, "line %d:%s扫UCC/EAN 128码失败(ret = %d,预期code=%s,实际code = %s)", Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UCC_EAN_128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg1_record(fileName, "scan19", gScreenTime,"%s测试通过", TESTITEM);

		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan19", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
		gui = null;
		
	}
}
