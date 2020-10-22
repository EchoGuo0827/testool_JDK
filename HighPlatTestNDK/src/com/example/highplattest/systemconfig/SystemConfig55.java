package com.example.highplattest.systemconfig;

import java.util.ArrayList;
import java.util.List;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig55.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180530 
 * directory 		: 
 * description 		: 设置应用的备份和恢复（该接口只支持农行固件）
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20180530	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig55 extends UnitFragment
{
	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "backupAppData和restoreAppData";
	private String fileName="SystemConfig55";
	private Gui gui = new Gui(myactivity, handler);
	private String filePath = "";
	private String filePath2 = "";
	private SettingsManager settingsManager = null;
	
	/**返回值说明：0-成功 2-path参数是个目录 3-目录创建失败 4-空间不足  -1-未知意外*/
	public void systemconfig55()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.ABC)
		{
			gui.cls_show_msg("该用例只支持农行版本，任意键退出测试");
			unitEnd();
			return;
		}
		boolean isConfig = false;
		settingsManager = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s测试\n0.配置\n1.单元测试\n", TESTITEM);
			switch (nkeyIn) {
			case '0':
				int configKey = gui.cls_show_msg("配置\n0.内置SD卡\n1.外置TF卡");
				switch (configKey) 
				{
				case '0':
					filePath = GlobalVariable.sdPath+"backupApp/back";
					filePath2 = GlobalVariable.sdPath+"backupApp/test";
					isConfig = true;
					break;
					
				case '1':
					filePath = GlobalVariable.TFPath+"/backupApp/back";
					filePath2 = GlobalVariable.TFPath+"/backupApp/test";
					isConfig = true;
					break;

				default:
					break;
				}
				break;
				
			case '1':
				if(isConfig==false)
					gui.cls_show_msg("请先配置,任意键继续!!!");
				else
					testRun();
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	public void testRun()
	{
		int iRet1=-1,iRet2 = -1,iRet3=-1,iRet4=-1,iRet5=-1;
		List<String> packList = new ArrayList<String>();
		packList.add("com.tencent.qqmusic");// QQ音乐
		packList.add("com.tencent.mtt");// QQ浏览器应用
		packList.add("com.android.settings");// 设置应用
		packList.add("com.newland.detectapp");// 自检应用
		
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 测试前置：清除backupApp目录以及安装QQ音乐和墨迹天气
		// 删除备份文件路径
		if(gui.cls_show_msg("[测试apk位于SVN的test_apk]测试前请确保%s目录无任何文件,完成[确认],退出[取消]",filePath.substring(0,filePath.lastIndexOf('/')))==ESC)
			return;
		gui.cls_show_msg("请先安装并登录QQ音乐和QQ浏览器后点击任意键继续");
		// case4.1:备份操作的参数异常测试
		gui.cls_printf("异常测试中,请耐心等待...".getBytes());
		if((iRet1 = settingsManager.backupAppData(null, filePath))==NDK_OK||(iRet2 = settingsManager.backupAppData(packList, null))==NDK_OK
				||(iRet3 = settingsManager.backupAppData(packList, ""))==NDK_OK||
				(iRet4 = settingsManager.backupAppData(new ArrayList<String>(), filePath))==NDK_OK||(iRet5 = settingsManager.backupAppData(packList, "abc"))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s参数异常测试失败(ret1=%d,ret2=%d,ret3=%d,ret4=%d,ret5=%d)", Tools.getLineInfo(),TESTITEM,iRet1,iRet2,iRet3,iRet4,iRet5);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 构造多个应用其中一个包名错误的情况
		List<String> errList = new ArrayList<String>();
		errList.add("com.tencent.qqmusic");// QQ音乐
		errList.add("com.tecent.mm");// QQ浏览器应用
		if((iRet1 = settingsManager.backupAppData(packList, "/mnt/sdcard/errMk/"))==NDK_OK||(iRet2 = settingsManager.backupAppData(errList, filePath))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s参数异常测试失败(ret1=%d,ret2=%d)", Tools.getLineInfo(),TESTITEM,iRet1,iRet2);
			if(GlobalVariable.isContinue==false)
				return;
		}

		// case4.2恢复备份的异常测试
		if((iRet1 = settingsManager.restoreAppData(null))==NDK_OK||(iRet2 = settingsManager.restoreAppData(""))==NDK_OK||
				(iRet3 = settingsManager.restoreAppData("/err/dir/back"))==NDK_OK||(iRet4 = settingsManager.restoreAppData(filePath.substring(0,filePath.lastIndexOf("/"))))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s参数异常测试失败(ret1=%d,ret2=%d,ret3=%d,ret4=%d)", Tools.getLineInfo(),TESTITEM,iRet1,iRet2,iRet3,iRet4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// modify 陈阵反馈没有包名不存在,会产生一个空文件(但是有长度的)并返回1,此属于第三方的软件，建议不测试 20180605
		/*if(gui.cls_show_msg("%s路径下应无备份文件生成,是[确认],否[取消]",filePath)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		
		// case1:SD卡路径备份普通应用、系统应用、自检应用
		// case5:外置TF卡的备份普通应用、系统应用、自检应用
		gui.cls_printf("正在第一次备份应用数据,请耐心等待".getBytes());
		if((iRet1 = settingsManager.backupAppData(packList, filePath))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case6:多次备份应正常
		gui.cls_printf("正在第二次备份应用数据,请耐心等待".getBytes());
		if((iRet1 = settingsManager.backupAppData(packList, filePath2))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("是否生成%s.zip和%s.zip文件,请将%s.zip文件导出到电脑查看，是否已备份了QQ音乐、QQ浏览器、自检、设置应用的数据,是[确认],否[取消]",filePath,filePath2,filePath)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s备份数据测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		/*// case7:后门方式删除备份文件（包名传null，路径合法）,陈阵建议不测试该dian modify by 20180625
		if(gui.cls_show_msg("文件系统下是否存在%s.zip文件", filePath2)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((iRet1 = settingsManager.backupAppData(null, filePath2))!=-1)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("文件系统是否不存在%s.zip文件",filePath2 )!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		// case2:卸载普通应用、K21通讯应用、系统应用、自检应用的数据
		gui.cls_show_msg("请确保先卸载了QQ音乐和QQ浏览器再重新安装QQ音乐和QQ浏览器应用,操作完毕点击任意键继续");
			
		// case3:恢复备份的数据并查看是否真的恢复
		gui.cls_printf("正在恢复备份数据请耐心等待".getBytes());
		if((iRet1 = settingsManager.restoreAppData(filePath))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(gui.cls_show_msg("QQ音乐和QQ浏览器是否保留之前的登录信息,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case8:空间不足测试，确保要测试的存储卡只剩下100M左右的空间后进行备份，备份应该返回4
		if(gui.cls_show_msg("是否立即进行写满设备操作,写满设备操作时间要等待5分钟以上,是[确认],否[取消]")==ENTER)
		{
			FileSystem fileSystem = new FileSystem();
			gui.cls_printf("正在进行写满设备操作,请耐心等待".getBytes());
			String fname = filePath.substring(0, filePath.lastIndexOf("/"))+"test.txt";
			byte[] writebuf = new byte[1024*200];
			while (true) 
			{
				if (fileSystem.JDK_FsWrite(fname, writebuf,writebuf.length, 2) != writebuf.length) 
				{
					break;
				}
			}
			if((iRet1 = settingsManager.backupAppData(packList, filePath2))!=4)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig55",gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet1);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 测试完毕后删除测试文件
			fileSystem.JDK_FsDel(fname);
		}

		gui.cls_show_msg1_record(fileName,"systemconfig55",gScreenTime, "需确保内置和外置SD均测试通过才可视为测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
