package com.example.highplattest.mpos;

import java.util.concurrent.TimeUnit;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;

/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer1.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20160603
 * directory 		: 
 * description 		: 打印mpos指令
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhangxinj			20180522		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos11 extends UnitFragment
{
	
	public final String TESTITEM = "打印(mpos)";
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	private PrintUtil printUtil;
	private String fileName=Mpos11.class.getSimpleName();
	private String funcName="mpos11";
	
	public void mpos11()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
//		String path=GlobalVariable.sdPath+"picture/";
		String retCode;
		gui.cls_show_msg("请将xdl.png图片放在/data/share/路径下，完成后按任意键继续");
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		printUtil=new PrintUtil(myactivity,handler,false);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:抛出异常", Tools.getLineInfo());
		}
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
//		if (new File(path).exists() == false) 
//		{
//			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:未导入picture测试文件,请先放置测试文件", Tools.getLineInfo());
//			return;
//		}
		
		// 打印机初始化
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printInitBuf), null);
		byte[] retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印初始化失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//获取打印机状态
		gui.cls_show_msg("确保打印机里【无纸】，任意键继续");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printStatesBuf), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			if(ISOUtils.hexInt(retContent, 9, 1)!=4)// 长度1字节
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:获取打印机状态失败(%s)", Tools.getLineInfo(),ISOUtils.hexInt(retContent, 9, 1));
				if(GlobalVariable.isContinue==false)
					return;
			}
			
		}
		else
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:获取打印机状态失败(%s)", Tools.getLineInfo(),retCode);
			return;
		}
		gui.cls_show_msg("确保打印机里【有纸】，任意键继续");
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printStatesBuf), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			
			if(ISOUtils.hexInt(retContent, 9, 1)!=0)// 长度1字节
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:获取打印机状态失败(%s)", Tools.getLineInfo(),ISOUtils.hexInt(retContent, 9, 1));
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		else
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:获取打印机状态失败(%s)", Tools.getLineInfo(),retCode);
			return;
		}
		//设置字库
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printSetFontLibrary), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:设置字库失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//行走纸
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printFeedingHang), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:行走纸测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//步走纸
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printFeedingBu), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:步走纸测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//行间距设置
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printVerticalSpacing), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:设置行间距测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//设置打印浓度
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printDensity), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:设置打印浓度失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//设置字体
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printFontType), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:设置字体失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//打印字符
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printBuf), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印字符测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//设置二值化阈值
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printYuZhi), null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:设置二值化阈值失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//打印图片
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printPicture), 10, TimeUnit.SECONDS, null);// 因mpos打印图片只在/data/share目录下有权限，更改图片的目录 20190809 by zhengxq
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印图片测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//打印一维码
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printOneDimensionCode), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印一维码测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//打印二维码
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printTwoDimensionCode), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印二维码测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//打印票据
		response =k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.printScript), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印票据测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//打印图片
		Bitmap bit = BitmapFactory.decodeFile("/mnt/sdcard/picture/xdl.png");
		bit = printUtil.gray2Binary(bit);
		int height = bit.getHeight();
		int width = bit.getWidth();
		byte[] imgBuf = printUtil.calcBuffer(bit, height, width);
		int position  = 0;
		byte[] print_1B50=ISOUtils.hex2byte("4DFF000000FFFFFFFFFFFFFFFF2236020500860000");//21
		byte[] print_zuhe=new byte[imgBuf.length+21+1];
		byte[] endByte={0x00};
		System.arraycopy(print_1B50, 0, print_zuhe, 0, print_1B50.length);
		position+=print_1B50.length;
		System.arraycopy(imgBuf, 0, print_zuhe, position, imgBuf.length);
		position+=imgBuf.length;
		System.arraycopy(endByte,0,print_zuhe,position,endByte.length);
		byte[] pack = CalDataLrc.mposPack(new byte[]{0x1B,0x50},print_zuhe);
		response =k21ControllerManager.sendCmd(new K21DeviceCommand(pack), 10, TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印图片测试失败(%s)", Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"文字和图片打印清晰可见，二维码和条码可扫描则测试通过");
	}


	@Override
	public void onTestUp() {}



	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}
}
