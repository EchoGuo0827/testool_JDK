package com.example.highplattest.scan;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/**
* description 		: /newland/factory/ScanCommon/license目录移除
* related document : 
* history 		 	: 变更点                              																变更时间			变更时间
*			  		  增加移除/newland/factory/ScanCommon/license构造扫码初始化失败情况，配置文件A7平台添加        		20200508		郑薛晴
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class Scan29 extends UnitFragment
{
	private String fileName=Scan28.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil)扫码初始化失败再初始化验证";
	private Gui gui ;
	private int mScanTimeOut = 30*1000;

	public void scan29()
	{
		gui = new Gui(myactivity, handler);
		while(true)
		{
			int nkey = gui.cls_show_msg("%s[测试步骤->测试完1再测试2]\n0.设备是否存在thk88.lic文件\n1.剪切.../license/thk88.lic文件到/sdcard/thk88.lic\n2./sdcard/thk88.lic文件到.../license/thk88.lic\n",TESTITEM);
			switch (nkey) {
			case '0':
				if(FileSystem.dirIsExists("/newland/factory/ScanCommon/license/thk88.lic")==false)
					gui.cls_show_msg("/newland/factory/ScanCommon/license/thk88.lic文件不存在");
				else
					gui.cls_show_msg("/newland/factory/ScanCommon/license/thk88.lic文件存在");
				break;
				
			case '1':
				if(FileSystem.copyFileToDir("/newland/factory/ScanCommon/license/thk88.lic", "/sdcard")==false)
				{
					gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:移动文件失败", Tools.getLineInfo());
					return;
				}
				if(FileSystem.deleteFile(new File("/newland/factory/ScanCommon/license/thk88.lic"))!=true)
				{
					gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:删除/newland/factory/ScanCommon/license/thk88.lic文件失败", Tools.getLineInfo());
					return;
				}
				gui.cls_show_msg("/newland/factory/ScanCommon/license/thk88.lic已被剪切，手动重启后去自检进行扫码应失败则初始化失败构造成功");
				break;
				
			case '2':
				if(FileSystem.moveFile("/sdcard/thk88.lic", "/newland/factory/ScanCommon/license")==false)
				{
					gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:移动文件失败", Tools.getLineInfo());
					return;
				}
				gui.cls_show_msg("/newland/factory/ScanCommon/license/thk88.lic已存在，手动重启后去自检进行扫码成功，【1】选项测试完后进行【2】选项均通过才可视为测试通过");
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
		
		
	}
	
	/*public void scan()
	{
		gui = new Gui(myactivity, handler);
		int ret=-1;
		
		// 判断设备是否存在/newland/factory/ScanCommon目录
		if(FileSystem.dirIsExists("/newland/factory/ScanCommon/license/thk88.lic")==false)
		{
			gui.cls_show_msg1_record("Scan32", "scan32", gKeepTimeErr,"line %d:扫码证书文件不存在,可以先通过CopyFileSytem的APP放置thk88.lic文件后再进入案例测试", Tools.getLineInfo());
			return;
		}
		ScanDefineInfo scanDefineInfo = getCameraInfo();
		int cameraId = scanDefineInfo.getCameraId();
		
		// case1：构造扫码初始化的条件,移除/newland/factory/ScanCommon/license目录
		gui.cls_show_msg1(1,"case1：正在剪切/newland/factory/ScanCommon/license/thk88.lic文件到/sdcard/thk88.lic");
		if(FileSystem.copyFileToDir("/newland/factory/ScanCommon/license/thk88.lic", "/sdcard")==false)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:移动文件失败", Tools.getLineInfo());
			return;
		}
		if(FileSystem.deleteFile(new File("/newland/factory/ScanCommon/license/thk88.lic"))!=true)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:删除/newland/factory/ScanCommon/license/thk88.lic文件失败", Tools.getLineInfo());
			return;
		}
		
		if((ret=initScanMode(Scan_Mode.NLS_1, myactivity, surfaceView, cameraId, true, mScanTimeOut))==1)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:扫码初始化成功，预期失败(ret=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("初始化失败后进行扫码".getBytes());
		if((ret=scanDialog("扫码预期失败", handler))!=NDK_SCAN_COTINUE_NULL)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:扫码成功，预期失败(ret=%d，code=%s)", Tools.getLineInfo(),ret,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		releaseScan();
		// case2：初始化失败之后再初始化应扫码成功
		gui.cls_show_msg1(1,"case1：正在复制/sdcard/thk88.lic文件到/newland/factory/ScanCommon/license/thk88.lic");
		if(FileSystem.moveFile("/sdcard/thk88.lic", "/newland/factory/ScanCommon/license")==false)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:移动文件失败", Tools.getLineInfo());
			return;
		}
		
		if((ret=initScanMode(Scan_Mode.NLS_1, myactivity, surfaceView, 0, true, mScanTimeOut))!=1)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:扫码初始化失败，预期成功(ret=%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("初始化成功后进行扫码".getBytes());
		if((ret=scanDialog(String.format("放置二维码到%s20-30cm位置", scanDefineInfo.getCameraInfo()), handler))!=0)
		{
			gui.cls_show_msg1_record(fileName, "scan32", gKeepTimeErr,"line %d:扫码失败(ret=%d，code=%s)", Tools.getLineInfo(),ret,mCodeResult);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(fileName, "scan31", gScreenTime,"%s测试通过", TESTITEM);
	}*/
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
	}

}
