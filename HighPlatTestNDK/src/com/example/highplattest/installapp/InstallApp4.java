package com.example.highplattest.installapp;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.newland.os.NlRecovery;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 农行恢复出厂卸载指定的app
 * file name 		: InstallApp4.java 
 * Author 			:  zhangxinj
 * version 			: 
 * DATE 			: 20180112
 * directory 		: 
 * description 		: 农行恢复出厂卸载指定的app
 * related document : 
 * history 		 	: author			date			remarks
 * 					 zhangxinj		   20180112	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp4 extends UnitFragment
{
	private final String TESTITEM = "农行恢复出厂卸载指定的app";
	public final String TAG = InstallApp4.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	NlRecovery nl=null;
	boolean ret=false;
	public void installapp4() 
	{
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp4", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.ABC)
		{
			gui.cls_show_msg("非农行固件不支持本案例,任意键退出测试");
			unitEnd();
			return;
		}
		nl = new NlRecovery(myactivity);
		String[] appsErrString = new String[] { "err1", "err2" };
		String[] appsErrDataString = new String[] { "err1", "err2" };
		String qqMusicDataPath = "/mnt/sdcard/qqmusic";
		String UCDataPath = GlobalVariable.sdPath + "UCDownloads";
		String highPlatTestDataPath = GlobalVariable.sdPath + "result.txt";
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		// 测试前置
		if (gui.ShowMessageBox("请安装服务器上的qq音乐和UC浏览器，且运行过qq音乐、UC浏览器和HighPlatTest，使各个app有数据，即Sd卡目录下有qqmusic文件夹、UCDownloads文件夹和reuslt.txt文件"
						.getBytes(), (byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME) != BTN_OK) {
			return;
		}
		File qqFile = new File(qqMusicDataPath);
		File UCFile = new File(UCDataPath);
		File highPlatTestFile = new File(highPlatTestDataPath);

		if (!qqFile.exists() || !UCFile.exists() || !highPlatTestFile.exists()) {
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:qq音乐或者UC浏览器或者highPlatTest没有数据，请打开相应程序运行一下",Tools.getLineInfo());
			return;
		}
		//卸载设备中的指定APP，保留应用数据
		//case1:传入包名为null，应返回失败
		if((ret=nl.uninstallApps(null)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2:传入包名数据为null，应返回失败
		if((ret=nl.uninstallApps(appsErrString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case3:传入包名和数据路径为null，应返回失败
		if((ret=nl.uninstallApps(null,null)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case4:传入包名和数据都错误字符串，应返回失败
		if((ret=nl.uninstallApps(appsErrString,appsErrDataString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case5:传入包名正确，app数据路径错误，应返回失败
		String[] appString=new String[]{"com.tencent.qqmusic","com.UCMobile"};
		if((ret=nl.uninstallApps(appString,appsErrDataString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case6:传入包名错误，app数据路径正确，应返回失败
		String[] appDataString=new String[]{qqMusicDataPath,UCDataPath};
		if((ret=nl.uninstallApps(appsErrString,appDataString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case9:传入系统级app 自检com.newland.detectapp，应用不应被卸载
		String[] detectString=new String[]{"com.newland.detectapp"};
		if(gui.ShowMessageBox("卸载自检应用， 恢复出厂后不应被删除，继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			if((ret=nl.uninstallApps(detectString)!=true)){
				gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
			else{
				gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
			}
		}
		
		
		//case7:传入正确包名 qq音乐，qq音乐要被卸载，数据将被保留
		if(gui.ShowMessageBox("进行恢复出厂操作，qq音乐和UC浏览器会被卸载，但数据会被保留（即SD卡下qqmusic和UCDownloads文件夹还【存在】），继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			if((ret=nl.uninstallApps(appString)!=true)){
				gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}else{
				gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
			}
		}
		//case8:传入正确包名和正确app数据路径
			if(gui.ShowMessageBox("进行恢复出厂操作，qq音乐和UC浏览器会被卸载，数据也会被清除（即SD卡下qqmusic和UCDownloads文件夹【不存在】，但result.txt还要【存在】），继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			{
				if((ret=nl.uninstallApps(appString,appDataString)!=true)){
					gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				else{
					gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
				}
				
			}
			//case9:卸载/data/share/路径下数据

			String dirName = "/data/share/highPlatTest";  
			File dir = new File(dirName);  
			//创建目录  
		    if(!dir.exists()) {  
		        dir.mkdirs();
		    } 
		    String fileName="/data/share/highPlatTxt.txt";
			File file = new File(fileName);  
		    if(!file.exists()){
		    	try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    if(gui.ShowMessageBox("进行恢复出厂操作，highPlatTest会被卸载，数据也会被清除（即/data/share/下的highPlatTxt.txt和highPlatTest文件夹【不存在】，result.txt也【不存在】），同时注意下UC浏览器和qq音乐的应用和数据不应被删除，继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			{
		    	String[] appHighPlatTestString=new String[]{"com.example.highplattest"};
		    	String[] dataShareString=new String[]{"/data/share/highPlatTest","/data/share/highPlatTxt.txt",highPlatTestDataPath};
				if((ret=nl.uninstallApps(appHighPlatTestString,dataShareString)!=true)){
					gui.cls_show_msg1_record(TAG, "installapp4", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				else{
					gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
				}
				
			}
		    gui.cls_show_msg1_record(TAG, "installapp4", gScreenTime, "根据提示判断效果，都正确则测试通过");
		
	}

	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
	}
	
	
	
}
