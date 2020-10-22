package com.example.highplattest.systemnode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: SysteNode10.java 
 * description 		: 以太网mac地址功能变更点测试
 * related document :
 * history 		 	: 变更记录			变更时间				变更人员
 *			  	      以太网mac地址功能变更点测试		   	20190124			朱少辉
 *					从Other15搬移过来	20200609			郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SystemNode10 extends UnitFragment {
	private final String TESTITEM = "以太网mac地址测试";
	public final String TAG = SystemNode10.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	public String message;
	File file1 = new File("/sdcard/macaddr");
	File file2 = new File("/sdcard/errormac");
	File file3 = new File("/newland/factory/macaddr");

	public void systemnode10() 
	{
		String funcName = "systemnode10";
		int status = -100;
		// case1 导入正常的mac地址文件,应生效
		gui.cls_show_msg("请将SVN上mac目录下的macaddr errormac文件push到/sdcard目录下,任意键继续");
		if((status = FileMove(file1,file3))!=0)// 传入正确的mac地址文件
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:文件复制失败(status=%d)",Tools.getLineInfo(), status);
			if (GlobalVariable.isContinue == false)
				return;
		}
		message = LinuxCmd.readDevNode("/newland/factory/macaddr");
		if (gui.cls_show_msg("测试导入正常的mac地址文件,mac应生效....请重启后进入本项,在设置以太网下查看mac地址是否为"+ message + ",是[确定],否[取消],如测试过本项,请点击[确定]进入后续测试") == ESC) {
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试失败,写入的mac地址与读取的mac地址不一致,写入的mac地址为%s",
					Tools.getLineInfo(), TESTITEM, message);
			if (GlobalVariable.isContinue == false)
				return;
		};

		// case2 导入异常的mac地址文件, 应为默认的mac地址00:80:0f:11:70:00
		file3.delete();
		FileMove(file2,file3);// 传入错误的mac地址文件
		if (gui.cls_show_msg("测试导入异常的mac地址文件,mac地址应为默认地址(新版本机器为随机地址)....请重启后进入本项,打开以太网,再次重进以太网查看mac地址是否为00:80:0f:11:70:00,是[确认],否[取消],如测试过本项,请点击[确定]进入后续测试") == ESC) {
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试失败,未写入mac地址时,系统的mac地址不是默认地址",
					Tools.getLineInfo(), TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}

		// case3 未写入时,应为默认的mac地址00:80:0f:11:70:00
		file3.delete();
		if (gui.cls_show_msg("测试未导入mac地址文件,应为默认mac地址(新版本机器为随机地址)....请重启后进入本项,请打开以太网,查看mac地址是否为00:80:0f:11:70:00,是[确认],否[取消]") == ESC) {
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试失败,未写入mac地址时,系统的mac地址不是默认地址",Tools.getLineInfo(), TESTITEM);
			if (GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"测试结束,所有子案例测试通过视为测试通过,否则为不通过");
	}

	public int FileMove(File source, File dest) {
		int status = -1;
		LoggerUtil.v("source="+dest.getPath());
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			inStream = new FileInputStream(source);
			fs = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = inStream.read(buf)) > 0) {
				fs.write(buf, 0, bytesRead);
			}
			status = Tools.staticSetChmod(dest.getPath());
		} catch (Exception e) {
			e.printStackTrace();
//			gui.cls_show_msg("文件copy失败,请检查导入的路径是否错误");
			status = -10;
		} finally {
			try {
				inStream.close();//关闭流
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return status;
	}
	
	// 读取本机的mac(暂时不用)
	// public static String getMachineHardwareAddress() {
	// Enumeration<NetworkInterface> interfaces = null;
	// try {
	// interfaces = NetworkInterface.getNetworkInterfaces();
	// } catch (SocketException e) {
	// e.printStackTrace();
	// }
	// String hardWareAddress = null;
	// NetworkInterface iF = null;
	// if (interfaces == null) {
	// return null;
	// }
	// while (interfaces.hasMoreElements()) {
	// iF = interfaces.nextElement();
	// try {
	// hardWareAddress = bytesToString(iF.getHardwareAddress());
	// if (hardWareAddress != null)
	// break;
	// } catch (SocketException e) {
	// e.printStackTrace();
	// }
	// }
	// return hardWareAddress;
	// }

	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {

	}
}
