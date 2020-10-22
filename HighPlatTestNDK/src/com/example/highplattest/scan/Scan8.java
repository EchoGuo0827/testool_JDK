package com.example.highplattest.scan;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.highplattest.R;
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
 * file name 		: Scan8.java 
 * Author 			: zhangxj
 * version 			: 
 * DATE 			: 20160323
 * directory 		: 需要依次扫不同种可支持的码型
 * description 		: doScan()
 * related document : 
 * history 		 	: 变更记录						date			remarks
 *			  		  增加可跳过某种码制的选项		   20200426     	陈丁
 *					扫码库在B17之后UPC-E是不输出的 		20200623		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan8 extends UnitFragment
{
	private String fileName=Scan8.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+软)doScan和stopScan";
	final int MAXTAPTIME = 200;
	private int MAXWAITTIME=15*1000;
	public TextView mtvShow;
	public ImageView linShow;
	private Gui gui = new Gui(myactivity, handler);
	private String[] scanModeStr = {"zxing旧接口","Nls兼容zxing扫码","Nls自动识别扫码"};
	
	public void scan8() 
	{
		/*private & local definition*/
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		HashMap<Code_Type, String> codeMap = new HashMap<Code_Type, String>();
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
		nlsVersion = nlsVersion.substring(nlsVersion.indexOf("SoftEngine:")-4,nlsVersion.indexOf("SoftEngine:")-1);
		LoggerUtil.e("scan8->nlsVer="+nlsVersion+"=========");
		int nlsVersionDig = Integer.parseInt(nlsVersion.substring(1));
		LoggerUtil.e("scan8->nlsVersionDig="+nlsVersionDig+"=========");
		
		codeMap.put(Code_Type.UPC_E, nlsVersionDig>=17?"1234565":"01234565");//扫码库在B17之后UPC-E是不输出的 20200623
		codeMap.put(Code_Type.ISBN_ISSN, "9780194315104");
		codeMap.put(Code_Type.UCC_EAN_128, "83979222");
		
		while(true)
		{	
			int nkeyIn = gui.cls_show_msg("扫码配置\n2.自动识别接口测试自动识别扫码\n");//0.zxing旧接口\n1.自动识别接口兼容zxing扫码\n
			switch (nkeyIn) 
			{
			/**zxing接口不需要测试20200710*/
			/*case '0':
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A9||GlobalVariable.gCurPlatVer==Platform_Ver.A7)
				{
					gui.cls_show_msg("A9/A7平台不支持zxing旧接口测试");
					return;
				}
				scan8Test(codeMap,scanModeStr[0],Scan_Mode.ZXING);
				break;*/
				
			/*case '1':
				scan8Test(codeMap,scanModeStr[1],Scan_Mode.NLS_0);
				break;*/
				
			case '2':
				scan8Test(codeMap,scanModeStr[2],Scan_Mode.NLS_1);
				
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	
	/**
	 * NLS、ZXING及兼容,修改20180123
	 */
	public void scan8Test(HashMap<Code_Type, String> codeMap,String scanModeStr,Scan_Mode scan_Mode) 
	{	
		int ret = -1,cameraId=0;
		
		ScanDefineInfo scanInfo = getCameraInfo();
		cameraId=scanInfo.getCameraId();
		String cameraInfo = scanInfo.getCameraInfo();
		if(cameraId==-1)
		{
			gui.cls_show_msg("当前设备无扫描头和摄像头,任意键退出");
			return;
		}
		LoggerUtil.i("002,cameraId="+cameraId);
			
		LoggerUtil.e("Scan8,cameraId:"+cameraId);
		/*process body*/
		gui.cls_show_msg1(2, "%s测试中...",scanModeStr);
		handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		
		gui.cls_show_msg("请用02文档内的《测试用各码制条码.doc》、《测试用各码制二维码.docx》进行扫码测试,任意键继续");
		
		try {
			releaseScan();
			initScanMode(scan_Mode,myactivity, surfaceView, cameraId, true, MAXWAITTIME);
			// 关闭附加码开关
			if(scan_Mode == Scan_Mode.NLS_1)
				setNlsUpcEnable(0);
			
			// case1.1:应支持各种编码格式的QR码 
			// UTF-8编码的QR码
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:UTF-8)?码值为方式。按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫QR Code(字符编码格式:UTF-8),码值为方式,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.QR_UTF8_1)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫QR码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_1),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:UTF-8)?码值为%s。按取消键跳过",QR_UTF8) != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫QR Code(字符编码格式:UTF-8),码值:%s放置完毕任意键继续", scanModeStr,cameraInfo,QR_UTF8), gui,codeMap.get(Code_Type.QR_UTF8_2)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫QR码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_UTF8_2),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// GBK编码的QR码
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:GBK)?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫QR Code(字符编码格式:GBK),放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.QR_GBK)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫QR码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_GBK),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		if (gui.cls_show_msg("是否测试QR Code(字符编码格式:UTF-8,带ECI前缀)?按取消键跳过") != ESC) {
			// 带ECI前缀的QR_Code
			if((ret = scanTip(String.format("%s,请用%s摄像头扫QR Code(字符编码格式:UTF-8,带ECI前缀),放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.QR_ECI)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫QR码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.QR_ECI),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.2:应支持PDF417码
		if(scan_Mode == Scan_Mode.NLS_1){
				
			if (gui.cls_show_msg("是否测试PDF417码?按取消键跳过") != ESC) {
				if((ret = scanTip(String.format("%s,请用%s摄像头扫PDF417码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.PDF417)))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫PDF417码失败ret = %d,预期code=%s,实际code = %s", 
							Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.PDF417),mCodeResult);
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
			
			// case1.3:放Codebar码进行扫码
		if (gui.cls_show_msg("是否测试Codebar码?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫Codebar码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.CodeBar)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫CodeBar码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.CodeBar),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
			// case1.4:应支持code39码
		if (gui.cls_show_msg("是否测试code39码?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫code39码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.Code39)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫code39码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code39),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.5:应支持Code93
		if (gui.cls_show_msg("是否测试Code93码?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫Code93码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.Code93)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫Code93码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code93),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.6:应支持Code128
		if (gui.cls_show_msg("是否测试code128码(ASCII编码)?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫code128码(ASCII编码),放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.Code128)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫code128码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.Code128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.7:应支持EAN8无附加码
		if (gui.cls_show_msg("是否测试EAN8码?按取消键跳过") != ESC) {
			if ((ret = scanTip(String.format("%s,请用%s摄像头扫EAN8码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.EAN_8))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr,"line %d:%s扫EAN8码失败ret = %d,预期code=%s,实际code = %s",
						Tools.getLineInfo(), TESTITEM, ret,codeMap.get(Code_Type.EAN_8), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.8:应支持EAN13无附加码
		if (gui.cls_show_msg("是否测试EAN13码?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫EAN13码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.EAN_13)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫EAN13码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_13),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		if (gui.cls_show_msg("是否测试EAN-128码?按取消键跳过") != ESC) {
			// case1.9:应支持EAN-128码
			if((ret = scanTip(String.format("%s,请用%s摄像头扫EAN-128码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.EAN_128)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫EAN-128码失败ret = %d,预期code=%s,实际code = %s", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.EAN_128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}	
			// case1.10:放ITF码进行扫码
		if (gui.cls_show_msg("是否测试ITF-14码?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫ITF-14码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.ITF_14)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫ITF码失败ret = %d,预期code=%s,实际code = %s",
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.ITF_14),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.11:应支持UPC_A无附加码
		if (gui.cls_show_msg("是否测试UPC_A码?按取消键跳过") != ESC) {
			if ((ret = scanTip(String.format("%s,请用%s摄像头扫UPC_A码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.UPC_A))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr,"line %d:%s扫UPC_A码失败ret = %d,预期code=%s,实际code = %s",
						Tools.getLineInfo(), TESTITEM, ret,
						codeMap.get(Code_Type.UPC_A), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.12:应支持UPC_E码
		if (gui.cls_show_msg("是否测试UPC_E码?按取消键跳过") != ESC) {
			if ((ret = scanTip(String.format("%s,请用%s摄像头扫UPC_E码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.UPC_E))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr,"line %d:%s扫UPC_E码失败ret = %d,预期code=%s,实际code = %s",
						Tools.getLineInfo(), TESTITEM, ret,
						codeMap.get(Code_Type.UPC_E), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.13:应支持IBSN/ISSN码
		if (gui.cls_show_msg("是否测试IBSN/ISSN码?按取消键跳过") != ESC) {
			if ((ret = scanTip(String.format("%s,请用%s摄像头扫IBSN/ISSN码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.ISBN_ISSN))) != NDK_SCAN_OK) 
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr,"line %d:%s扫IBSN/ISSN码失败(ret = %d,预期code=%s,实际code = %s)",
						Tools.getLineInfo(), TESTITEM, ret,
						codeMap.get(Code_Type.ISBN_ISSN), mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			// case1.14:应支持GS1-128(UCC/EAN-128)码
		if (gui.cls_show_msg("是否测试UCC/EAN 128码?按取消键跳过") != ESC) {
			if((ret = scanTip(String.format("%s,请用%s摄像头扫UCC/EAN 128码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,codeMap.get(Code_Type.UCC_EAN_128)))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "line %d:%s扫UCC/EAN 128码失败(ret = %d,预期code=%s,实际code = %s)", 
						Tools.getLineInfo(),TESTITEM,ret,codeMap.get(Code_Type.UCC_EAN_128),mCodeResult);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
			// case1.15:扫上海地铁码应该不会导致崩溃
			// 扫码操作 
		if (gui.cls_show_msg("是否测试上海地铁码?按取消键跳过") != ESC) {
			String string="该测试项不对比码值，不崩溃，能扫出即可";
			gui.cls_show_msg("请将上海地铁码准备好。扫码不应该崩溃。完成按任意键");
			if((ret = scanTip(String.format("%s,请用%s摄像头扫上海地铁码,放置完毕任意键继续", scanModeStr,cameraInfo), gui,string))!=NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr,"声明： %s,扫出码值= %s)", string,mCodeResult);
				if (!GlobalVariable.isContinue) 
					return;
			}
		}
			gui.cls_show_msg1_record(fileName, "scan8", gScreenTime, "%s测试通过",scanModeStr);
			//后置
			releaseScan();
		} catch (NoClassDefFoundError e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, "scan8", gKeepTimeErr, "抛出异常(%s)", e.getMessage());
		}
	}
	
	@Override
	public void onClick(View view) 
	{
		super.onClick(view);
		switch (view.getId()) 
		{
		case R.id.btn_scan_light:
			new Thread()
			{
				public void run() 
				{
					stopScan();
				};
			}.start();
			break;

		default:
			break;
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
