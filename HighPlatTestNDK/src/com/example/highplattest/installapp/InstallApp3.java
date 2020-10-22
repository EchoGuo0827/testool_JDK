package com.example.highplattest.installapp;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.newland.os.NlRecovery;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 农行恢复出厂保留指定的app
 * file name 		: InstallApp3.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180108
 * directory 		: 
 * description 		: 农行恢复出厂保留指定的app(农行专用)
 * related document : 
 * history 		 	: author			date			remarks
 * 					 zhangxinj		   20141226	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp3 extends UnitFragment
{
	private final String TESTITEM = "农行恢复出厂保留指定的app";
	public final String TAG = InstallApp3.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	NlRecovery nl=null;
	boolean ret=false;
	public void installapp3() 
	{
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp3", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.ABC)
		{
			gui.cls_show_msg("非农行固件不支持本案例,任意键退出测试");
			unitEnd();
			return;
		}
		nl=new NlRecovery(myactivity);
		String[] appsErrString=new String[]{"err1","err2"};
		String[] appsErrDataString=new String[]{"err1","err2"};
		String qqMusicDataPath=GlobalVariable.sdPath+"qqmusic";
		String UCDataPath="/mnt/sdcard/UCDownloads";
		String highPlatTestDataPath="/mnt/sdcard/result.txt";
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		
		//测试前置
		if(gui.ShowMessageBox("请安装服务器上的qq音乐、UC浏览器，且运行过qq音乐、UC浏览器和HighPlatTest，使各个app有数据，即Sd卡目录下有qqmusic文件夹、UCDownloads文件夹和reuslt.txt文件".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			return;
		}
		File qqFile=new File(qqMusicDataPath);
		File UCFile=new File(UCDataPath);
		File highPlatTestFile=new File(highPlatTestDataPath);
		
		if(!qqFile.exists() || !UCFile.exists() || !highPlatTestFile.exists()){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:qq音乐或者UC浏览器或者highPlatTest没有数据，请打开相应程序运行一下", Tools.getLineInfo());
			return;
		}
		//case1:appName为null，预期返回false
		if((ret=nl.keepApps(null)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case2:appName为错误包名err，预期返回false
	
		if((nl.keepApps(appsErrString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case3:appName为null，APP数据的存储路径为null，预期返回false
		if((ret=nl.keepApps(null,null)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case4:appName为错误包名err，app数据的存储路径错误，预期返回false
		
		if((ret=nl.keepApps(appsErrString,appsErrDataString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case5:appName包名正确，app数据的存储路径错误
		String[] appsString_UC=new String[]{"com.UCMobile","com.example.highplattest"};
		if((ret=nl.keepApps(appsString_UC,appsErrDataString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case6:appName为错误包名err，app数据的存储路径正确s
		String[] appDateString=new String[]{UCDataPath,highPlatTestDataPath};
		if((ret=nl.keepApps(appsErrString,appsErrDataString)!=false)){
			gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s异常测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case7:传入正确的包名，保留设备中的指定APP，卸载掉其他所有非系统APP，并删除所有应用数据
		//qq音乐、UC浏览器"com.UCMobile",
		String[] appsString_qq=new String[]{"com.tencent.qqmusic","com.example.highplattest"};
		if(gui.ShowMessageBox("进行恢复出厂操作，将保留qq音乐和HighPlatTest应用，并删除所有非系统app应用数据（即SD卡qqmusic文件夹和result.txt文件【不存在】），继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			if((nl.keepApps(appsString_qq)!=true)){
				gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}else{
				gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
			}
		}
			//case8:appName包名正确，app数据的存储路径也正确
			if(gui.ShowMessageBox("进行恢复出厂操作，将保留UC浏览器和HighPlatTest应用和数据，并删除其他非系统app应用数据（即SD卡UCDownloads文件夹和result.txt文件【存在】），继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			{
				if((nl.keepApps(appsString_UC,appDateString)!=true)){
					gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				else{
					gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
				}
			}
			//case9:安装较大apk
		
			gui.cls_show_msg("测试超大apk和数据的保留，请安装服务器上game_400M.apk，并运行，完成后任意键继续");
			if(gui.ShowMessageBox("进行恢复出厂操作，将保留乱斗西游2、HighPlatTest的应用和数据(即sd卡下backups、baidu、duoku和netease4个文件夹和result.txt文件【存在】)，并删除其他非系统app应用数据，继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			{
				String[] appsString_game400=new String[]{"com.netease.ldxy.baidu","com.example.highplattest"};
				String[] game400Datas=new String[]{GlobalVariable.sdPath+"result.txt",GlobalVariable.sdPath+"backups",GlobalVariable.sdPath+"baidu",GlobalVariable.sdPath+"duoku",GlobalVariable.sdPath+"netease"};
				if((nl.keepApps(appsString_game400,game400Datas)!=true)){
					gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
					if(!GlobalVariable.isContinue)
						return;
				}
				else{
					gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
				}
			}
		
			//case10:/data/share目录测试
		
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
		    if(gui.ShowMessageBox("进行恢复出厂操作，HighPlatTest的应用和数据(即sd卡result.txt文件【存在】,/data/share路径底下highPlatTest文件夹【存在】和/data/share路径底下highPlatTxt.txt文件【存在】)，并删除其他非系统app应用数据，继续按确认，跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			{
					String[] appString=new String[]{"com.example.highplattest"};
					String[] dataShareDir=new String[]{highPlatTestDataPath,"/data/share/highPlatTest","/data/share/highPlatTxt.txt"};
					if((nl.keepApps(appString,dataShareDir)!=true)){
						gui.cls_show_msg1_record(TAG, "installapp3", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,ret);
						if(!GlobalVariable.isContinue)
							return;
					}
					else{
						gui.cls_show_msg1(2, "正在恢复出厂，请耐心等待....");
					}
			}
		    gui.cls_show_msg1_record(TAG, "installapp3", gScreenTime, "根据提示判断效果，都正确则测试通过");
	}

	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
		//gui = null;
	}

	
}
