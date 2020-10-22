package com.example.highplattest.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.annotation.SuppressLint;
import android.newland.scan.ScanUtil;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.netutils.NetworkUtil;
import com.example.highplattest.main.tools.BitmapDeal;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan9.java 
 * Author 			: zhangxj
 * version 			: 
 * DATE 			: 20160323
 * directory 		:  图片解析扫码
 * description 		: doScan(InputStream is) 
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		  Zxing接口,A7和A9不支持图片解析		   20200408     	zhengxq
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan9 extends UnitFragment
{
	private String fileName=Scan9.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+软)doNlsScan(InputStream stream,Context context)";
	StringBuffer result = new StringBuffer();
	Gui gui = new Gui(myactivity, handler);
	public void scan9() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		
		// 测试前置,确保网络通
		int netConnType = NetworkUtil.checkNet(myactivity);
		if(netConnType==-1)
		{
			gui.cls_show_msg1(1, "无网络,请先打开网络后再进入该用例");
			return;
		}
		int nkeyin = gui.cls_show_msg("配置扫码方式\n1.Nls方式\n");//0.zxing方式\n
		
		switch(nkeyin)
		{
		/*case '0':
			if(GlobalVariable.gCurPlatVer!=Platform_Ver.A5)
			{
				gui.cls_show_msg("A9/A7平台不支持zxing旧接口测试");
				return;
			}
			scan_zxing();
			break;*/
		case '1':
			scan_Nls();
			break;
		default:
				
		}
		
		gui.cls_show_msg1_record(fileName, "scan9", gScreenTime, "%s测试通过", TESTITEM);	
	}
	
	public void scan_zxing(){
		/*private & local definition*/
		
		//网络图片路径
		String urlpath = "http://hiphotos.baidu.com/zhidao/pic/item/7aec54e75ce60f44b93820c5.jpg";
		String urlErrPath = "http://pic1.win4000.com/wallpaper/270*185/22301.jpg";
		
		//本地图片路径，各种图片格式的二维码都应该能够解析
		String corPath_Png = GlobalVariable.sdPath+"scan/correct.PNG";
		String corPath_webp = GlobalVariable.sdPath+"scan/correct.webp";
		String corPath_jpg = GlobalVariable.sdPath+"scan/correct.jpg";
		String corPath_jpeg = GlobalVariable.sdPath+"scan/correct.jpeg";
		String corPath_gif = GlobalVariable.sdPath+"scan/correct.gif";
		String corPath_bmp = GlobalVariable.sdPath+"scan/correct.bmp";
		String errPath = GlobalVariable.sdPath+"scan/errFile.txt";
		String errPic = GlobalVariable.sdPath+"scan/errPic.jpg";
		
		gui.cls_show_msg1(2, "zxing方式测试中...");
		// case1:传入的输入流为null,应解析失败
		result.append(ScanUtil.doScan(null));
		if(!result.toString().equals("null"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.1:对本地非图片文件进行解析应失败
		InputStream errIn;
		try 
		{
			errIn = new FileInputStream(new File(errPath));
			result.delete(0, result.length());
			result.append(ScanUtil.doScan(errIn));
		} catch (FileNotFoundException e) 
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s文件不存在", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!result.toString().equals("null"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:传入非码型图片文件,进行解析应失败
		InputStream errPicIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(errPic));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(errPicIn));
		if(!result.toString().equals("null"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.1:传入本地码型二维码图片(PNG格式),进行解析应成功
		InputStream corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_Png));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(corrIn));
		if(!result.toString().equals("285614263499135991"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：285614263499135991,实际码值：%s)", 
					Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.2:传入本地码型二维码图片(webp格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_webp));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(corrIn));
		if(!result.toString().equals("中国"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：中国,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.3:传入本地码型二维码图片(jpg格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_jpg));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(corrIn));
		if(!result.toString().equals("方式"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：方式,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.4:传入本地码型二维码图片(jpeg格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_jpeg));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(corrIn));
		if(!result.toString().equals("code128(a)*%,"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：code128(a)*%,,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.5:传入本地码型二维码图片(gif格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_gif));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(corrIn));
		if(!result.toString().equals("9780194315104"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：9780194315104,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.6:传入本地码型二维码图片(bmp格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_bmp));
		result.delete(0, result.length());
		result.append(ScanUtil.doScan(corrIn));
		if(!result.toString().equals("01234567890123"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：01234567890123,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1:传入外网非码型文件,进行解析应失败
		InputStream errNetIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getImage(urlErrPath));
		if(errNetIn==null)
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s网络资源获取失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		else
		{
			result.delete(0, result.length());
			result.append(ScanUtil.doScan(errNetIn));
			if(!result.toString().equals("null"))
			{
				gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case3.2:传入外网码型文件,进行解析应成功
		InputStream netIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getImage(urlpath));
		if(netIn==null)
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s网络资源获取失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		else
		{
			result.delete(0, result.length());
			result.append(ScanUtil.doScan(netIn));
			if(!result.toString().equals("922313813712"))
			{
				gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码失败(预期码值：922313813712,实际码值：%s)", 
						Tools.getLineInfo(),TESTITEM,result.toString());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
	}
	//by weimj 20190915
	public void scan_Nls(){
		/*private & local definition*/
		
		//网络图片路径
		String urlpath = "http://image5.huangye88.cn/2013/07/08/dc3bcc7ded48029b.jpg";
		String urlErrPath = "http://bbs-fd.zol-img.com.cn/t_s800x5000/g3/M03/0D/0C/Cg-4V1FB__-INiaQAAHfqDkwZi8AAF2mAJoO6EAAd_A061.jpg ";
		
		//本地图片路径，各种图片格式的二维码都应该能够解析
		String corPath_Png = GlobalVariable.sdPath+"scan/correctNls.PNG";
		String corPath_webp = GlobalVariable.sdPath+"scan/correctNls.webp";
		String corPath_jpg = GlobalVariable.sdPath+"scan/correctNls.jpg";
		String corPath_jpeg = GlobalVariable.sdPath+"scan/correctNls.jpeg";
		String corPath_gif = GlobalVariable.sdPath+"scan/correctNls.gif";
		String corPath_bmp = GlobalVariable.sdPath+"scan/correctNls.bmp";
		String errPath = GlobalVariable.sdPath+"scan/errFile.txt";
		String errPic = GlobalVariable.sdPath+"scan/errPicNls.jpg";
		
		gui.cls_show_msg1(2, "nls方式测试中...");
		// case1:传入的输入流为null,应解析失败
		result.append(ScanUtil.doNlsScan(null,myactivity));
		if(!result.toString().equals("null"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2.1:对本地非图片文件进行解析应失败
		InputStream errIn;
		try 
		{
			errIn = new FileInputStream(new File(errPath));
			result.delete(0, result.length());
			result.append(ScanUtil.doNlsScan(errIn, myactivity));
		} catch (FileNotFoundException e) 
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s文件不存在", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!result.toString().equals("null"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:传入非码型图片文件,进行解析应失败
		InputStream errPicIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(errPic));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(errPicIn, myactivity));
		if(!result.toString().equals("null"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.1:传入本地码型二维码图片(PNG格式),进行解析应成功
		InputStream corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_Png));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(corrIn, myactivity));
		if(!result.toString().equals("285614263499135991"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：285614263499135991,实际码值：%s)", 
					Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.2:传入本地码型二维码图片(webp格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_webp));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(corrIn, myactivity));
		if(!result.toString().equals("中国"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：中国,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.3:传入本地码型二维码图片(jpg格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_jpg));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(corrIn, myactivity));
		if(!result.toString().equals("方式"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：方式,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.4:传入本地码型二维码图片(jpeg格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_jpeg));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(corrIn, myactivity));
		if(!result.toString().equals("code128(a)*%,"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.5:传入本地码型二维码图片(gif格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_gif));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(corrIn, myactivity));
		if(!result.toString().equals("9780194315104"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：9780194315104,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.3.6:传入本地码型二维码图片(bmp格式),进行解析应成功
		corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath_bmp));
		result.delete(0, result.length());
		result.append(ScanUtil.doNlsScan(corrIn, myactivity));
		if(!result.toString().equals("01234567890123"))
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码错误(预期码值：01234567890123,实际码值：%s)", Tools.getLineInfo(),TESTITEM,result.toString());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case3.1:传入外网非码型文件,进行解析应失败
		InputStream errNetIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getImage(urlErrPath));
		if(errNetIn==null)
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s网络资源获取失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		else
		{
			result.delete(0, result.length());
			result.append(ScanUtil.doNlsScan(errNetIn, myactivity));
			if(!result.toString().equals("null"))
			{
				gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case3.2:传入外网码型文件,进行解析应成功
		InputStream netIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getImage(urlpath));
		if(netIn==null)
		{
			gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s网络资源获取失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		else
		{
			result.delete(0, result.length());
			result.append(ScanUtil.doNlsScan(netIn, myactivity));
			if(!result.toString().equals("800012348765412349513"))
			{
				gui.cls_show_msg1_record(fileName, "scan9", gKeepTimeErr,"line %d:%s扫码失败(预期码值：800012348765412349513,实际码值：%s)", 
						Tools.getLineInfo(),TESTITEM,result.toString());
				if(!GlobalVariable.isContinue)
					return;
			}
		}
	}
	
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		gui = null;
	}
}
