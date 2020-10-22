package com.example.highplattest.mpos;

import java.util.Arrays;

import android.app.Activity;
import android.newland.NlManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_HWINFO;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.newland.ndk.JniNdk;

/************************************************************************
 * 
 * module 			: sys模块
 * file name 		: K21Sys.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180815
 * directory 		: 
 * description 		: PN/SN/CSN/KSN/机器号存储方案变更
 * related document : 
 * history 		 	: 变更记录			                                                               变更时间			变更人员
 *			  		     设置SN号和设置PN号增加手动输入的案例		    20200509                      魏美杰
 *                    整理合并SystemConfig66的案例                                       20200604        魏美杰
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos2 extends UnitFragment
{
	public final String TAG = Mpos2.class.getSimpleName();
	private final String TESTITEM = "PN/SN/CSN/KSN/机器号存储变更";
	private Gui gui = new Gui(myactivity, handler);
	NlManager mNlManager;
	private String csnFile = "/appfs/yssn.in";
	private final String[] snStr = {"N600001NL001001","N600001NL001009"};
	private final String[] pnStr = {"N6NL99990100","N6NL99990101"};
	private final String ksnStr = "12345678";
	private final String csnStr = "12345678";
	private final String jqhStr = "12345678";
	private byte[] mReadBuf = new byte[30];
	private final int READWAITTIME = 5;
	byte[] ioctrl = {2};
	private final int BUFLEN = 1024*5;
	
	public String sn = "";
	public String pn = "";
	public String input = "";
	private IntentActivity activity;
	public volatile boolean stop = true;
	
	public enum NumType
	{
		SN,PN,KSN,CSN,JQH;
	}
	
	public void mpos2()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n1.擦除PN/SN/KSN/CSN/机器号\n2.设置PN/SN/KSN/CSN/机器号\n3.测试用例\n4.获取PN/SN/CSN/机器号\n", TESTITEM);
			switch (nkeyIn) {
			case '1':
				delFile();
				break;
				
			case '2':
				setFile();
				break;
				
			case '3':
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
				{
					gui.cls_show_msg1(1, "%s该平台不支持mpos案例",GlobalVariable.currentPlatform);
					break;
				}
				fileTest();
				break;
				
			case '4':
				getFile();
				break;

			case ESC:
				unitEnd();
				return;
			}
		}
		
	}
	
	private void getFile() {
		// TODO Auto-generated method stub
		String retCode;
		byte[] result = new byte[30];
		int fd = -1;
		byte[] readbuf = new byte[BUFLEN+1];
		while(true)
		{
			int keyIn = gui.cls_show_msg("获取\n0.SN号\n1.PN号\n2.CSN号\n3.机器号\n");
			switch (keyIn) {
			case '0':
				gui.cls_printf("正在获取SN号".getBytes());
				JniNdk.JNI_Sys_GetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), 0, result);
				gui.cls_show_msg("获取SN为%s", ISOUtils.ASCII2String(result,snStr[0].length()));
				break;
				
			case '1':
				gui.cls_printf("正在获取PN号".getBytes());
				JniNdk.JNI_Sys_GetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN.secsyshwinfo(),0, result);
				gui.cls_show_msg("获取PN为%s", ISOUtils.ASCII2String(result,pnStr[0].length()));
				break;
			
			case '2':
				gui.cls_printf("正在获取CSN号".getBytes());
				byte[] readbuf2 = new byte[BUFLEN];
				Arrays.fill(readbuf2, (byte)0);
				fd = JniNdk.JNI_FsOpen(csnFile, "r");
				JniNdk.JNI_FsRead(fd, readbuf2, BUFLEN);
				JniNdk.JNI_FsClose(fd);
				gui.cls_show_msg("获取CSN为%s", ISOUtils.ASCII2String(readbuf2,csnStr.length()));
				break;
			
			case '3':
				gui.cls_printf("正在获取机器号".getBytes());
				JniNdk.JNI_Sys_GetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_BOARD_VER.secsyshwinfo(), 0, result);
				for (int i = 0;i<30;i++){
					LoggerUtil.e("byte "+i+":"+result[i]);
				}
				gui.cls_show_msg("获取机器号为%s", ISOUtils.ASCII2String(result,jqhStr.length()));
				break;
				
			case ESC:
				return;
			}
		}
		
	}
	
	private void setFile()
	{
		String retCode;
		String title="";
		if (GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false) {
			title="";
		}else {
			title="2.KSN号(mpos)\n3.CSN号(mpos)\n4.机器号(mpos)\n";
		}
		int keyIn = gui.cls_show_msg("设置\n0.SN号\n1.PN号\n%s\n",title);
		switch (keyIn) {
		case '0':
			int keySN = gui.cls_show_msg("设置SN号\n0.设置默认值\n1.手动设置\n");
			switch(keySN){
			case '0':
				gui.cls_printf("正在设置SN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), snStr[0]);
				gui.cls_show_msg("已设置SN为%s", snStr[0]);
				break;
			case '1':
				stop = true;
				input = null;
				activity = (IntentActivity)myactivity;
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						setConfig(activity);
					}
				});
				while(stop){
					SystemClock.sleep(10);
				}
				if(input==null)
					return;
				sn = input;
				Log.e("sn",sn+"");
				gui.cls_printf("正在设置SN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), sn);
				gui.cls_show_msg("已设置SN为%s", sn);
				break;
			}

			break;
			
		case '1':
			int keyPN = gui.cls_show_msg("设置PN号\n0.设置默认值\n1.手动设置\n");
			switch(keyPN){
			case '0':
				gui.cls_printf("正在设置PN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN.secsyshwinfo(),pnStr[0]);
				gui.cls_show_msg("已设置PN为%s", pnStr[0]);
				break;
			
			case '1':
				stop = true;
				input = null;
				activity = (IntentActivity)myactivity;
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						setConfig(activity);
					}
				});
				while(stop){
					SystemClock.sleep(10);
				}
				if(input==null)
					return;
				pn = input;
				Log.e("pn",pn+"");
				gui.cls_printf("正在设置PN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN.secsyshwinfo(),pn);
				gui.cls_show_msg("已设置PN为%s", pn);
				break;
			
			}
			break;
			
		case '2':
			if (GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false) {
				gui.cls_show_msg1(1, "%s该平台不支持mpos案例",GlobalVariable.currentPlatform);
				return;
			}
			gui.cls_printf("正在设置KSN号".getBytes());
			byte[] facKsnCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02}, ISOUtils.hex2byte("1000083132333435363738"));
			mNlManager.write(facKsnCmd, facKsnCmd.length, 30);
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(mReadBuf, 30, READWAITTIME);
			LoggerUtil.d("mReadBuf:"+ISOUtils.dumpString(mReadBuf));
			if((retCode=ISOUtils.dumpString(mReadBuf, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "setFile", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("已设置KSN号为%s", ksnStr);
			break;
			
		case '3':
			int keyCSN = gui.cls_show_msg("设置CSN号\n0.JNI设置\n1.Mpos设置\n");
			switch(keyCSN){
			case '0':
				gui.cls_printf("正在设置CSN号".getBytes());
				int fd = -1;
				fd = JniNdk.JNI_FsOpen(csnFile, "w");
				LoggerUtil.e("JNI_FsOpen:"+fd);
				LoggerUtil.e("打开文件成功");
				JniNdk.JNI_FsWrite(fd, csnStr.getBytes(), csnStr.getBytes().length);// 文件写操作是追加的方式 不需要seek
				LoggerUtil.e("写文件成功");
				JniNdk.JNI_FsClose(fd);
				LoggerUtil.e("关闭文件成功");
				gui.cls_show_msg("已设置CSN为%s", csnStr);
				break;
				
			case '1':
				if (GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false) {
					gui.cls_show_msg1(1, "%s该平台不支持mpos案例",GlobalVariable.currentPlatform);
					return;
				}
				gui.cls_printf("正在设置CSN号".getBytes());
				byte[] facCsnCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02}, ISOUtils.hex2byte("0400083132333435363738"));
				mNlManager.write(facCsnCmd, facCsnCmd.length, 30);
				mNlManager.ioctl(0x540B, ioctrl);
				mNlManager.read(mReadBuf, 30, READWAITTIME);
				LoggerUtil.d("mReadBuf:"+ISOUtils.dumpString(mReadBuf));
				if((retCode=ISOUtils.dumpString(mReadBuf, 7, 2)).equals("00")==false)
				{
					gui.cls_show_msg1_record(TAG, "setFile", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
					if(GlobalVariable.isContinue==false)
						return;
				}
				gui.cls_show_msg("已设置CSN号为%s", csnStr);
				break;
			}

			break;
			
		case '4':
			int keyMN = gui.cls_show_msg("设置机器号\n0.JNI设置\n1.Mpos设置\n");
			switch(keyMN){
			case '0':
				gui.cls_printf("正在设置机器号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_BOARD_VER.secsyshwinfo(),jqhStr);
				gui.cls_show_msg("已设置机器号为%s", jqhStr);
				break;
			case '1':
				if (GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false) {
					gui.cls_show_msg1(1, "%s该平台不支持mpos案例",GlobalVariable.currentPlatform);
					return;
				}
				gui.cls_printf("正在设置机器号".getBytes());
				byte[] facJqhCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02},ISOUtils.hex2byte("0800083132333435363738"));
				mNlManager.write(facJqhCmd, facJqhCmd.length, 30);
				mNlManager.ioctl(0x540B, ioctrl);
				mNlManager.read(mReadBuf, 30, READWAITTIME);
				if((retCode=ISOUtils.dumpString(mReadBuf, 7, 2)).equals("00")==false)
				{
					gui.cls_show_msg1_record(TAG, "setFile", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
					if(GlobalVariable.isContinue==false)
						return;
				}
				gui.cls_show_msg("已设置机器号为%s", jqhStr);
				break;
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 擦除PN/SN/CSN/KSN号
	 */
	private void delFile()
	{
		while(true)
		{
			int ret = -1;
			String title="";
			if (GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)) {
				title="";
			}else {
				title="3.擦除KSN号";
			}
			int nkeyIn = gui.cls_show_msg("擦除\n1.擦除PN和SN号\n2.擦除CSN\n%s",title);
			switch (nkeyIn) {
			case '1':
				gui.cls_show_msg("使用SVN的擦除PN和SN的master固件,下载擦除的master固件后要重新下载测试固件才可正常使用K21端,操作成功之后可使用自检查看PN和SN是否已擦除,任意键继续");
				break;
				
			case '2':
				if((ret = JniNdk.JNI_FsDel("/appfs/yssn.in"))==NDK_OK)
					gui.cls_show_msg("擦除CSN成功,可使用自检查看CSN号是否已被擦除,任意键继续");
				else
				{
					gui.cls_show_msg1_record(TAG, "delFile", gKeepTimeErr, "line %d:擦除CSN号失败(%d)", Tools.getLineInfo(),ret);
					return;
				}
				break;
				
			case '3':
				if((ret=JniNdk.JNI_FsDel("/appfs/ksn.in"))==NDK_OK)
					gui.cls_show_msg("擦除KSN号成功,可使用自检查看KSN号是否已被擦除,任意键继续");
				else
				{
					gui.cls_show_msg1_record(TAG, "delFile", gKeepTimeErr, "line %d:擦除KSN号失败(%d)", Tools.getLineInfo(),ret);
					return;
				}
				break;

			case ESC:
				return;
				
			default:
				break;
			}
		}

	}
	
	public void fileTest()
	{
		final String FUN_NAME="fileTest";
		int ret = -1,succ=0;
		String retCode;
		byte[] result = new byte[30];
//		/**暂时由测试人员手动创建，后续服务改好之后，由服务端创建，后续要删除*/
//		gui.cls_show_msg("测试前请先确保/newland/appFsLocal/appfs/目录已存在,没有该目录请自行创建,完成任意键继续");
		
		
		/**测试前置，要先创建/newland/appfs/目录*/
		int count = (int) (Math.random()*20+1);// 确保至少能测试1次
		for(int i=0;i<count;i++)
		{
			int time = i+1;
			if(gui.cls_show_msg1(1, "正在进行第%d次测试,共%d次,已成功%d次,取消键退出测试...", time,count,succ)==ESC)
				return;
			/**Android端生成的文件位于/newland/posinfo*/
			// case1:分别使用setPosInfo和生产指令方式设置SN
			if((ret = JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), snStr[0]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),time,TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.SN, snStr[0]))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			// 生产指令方式设置SN号
			byte[] facSnCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02},ISOUtils.hex2byte("0100154E3630303030314E4C303031303039"));
			mNlManager.write(facSnCmd, facSnCmd.length, 30);
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(result, 30, READWAITTIME);
			if((retCode=ISOUtils.dumpString(result, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),time,TESTITEM,retCode);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.SN, snStr[1]))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			// case2:分别使用setPosInfo方式和生产指令方式设置PN
			if((ret = JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN.secsyshwinfo(), pnStr[0]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),time,TESTITEM,ret);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.PN, pnStr[0]))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			// 生产指令方式设置PN号
			byte[] facPnCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02},ISOUtils.hex2byte("0200124E364E4C3939393930313031"));
			mNlManager.write(facPnCmd, facPnCmd.length, 30);
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(result,30, READWAITTIME);
			LoggerUtil.e("read pn:"+ISOUtils.hexString(result));
			if((retCode=ISOUtils.dumpString(result, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),time,TESTITEM,retCode);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.PN, pnStr[1]))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			// case3:生产指令方式设置CSN号(CSN已搬移到Android端)
			byte[] facCsnCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02}, ISOUtils.hex2byte("0400083132333435363738"));
			mNlManager.write(facCsnCmd, facCsnCmd.length, 30);
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(result, 30, READWAITTIME);
			LoggerUtil.e("result line 269:"+ISOUtils.hexString(result));
			if((retCode=ISOUtils.dumpString(result, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),time,TESTITEM,retCode);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.CSN, csnStr))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			// case4:生产指令方式设置KSN号
			byte[] facKsnCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02}, ISOUtils.hex2byte("1000083132333435363738"));
			mNlManager.write(facKsnCmd, facKsnCmd.length, 30);
			LoggerUtil.d("ksn line 283:"+ISOUtils.hexString(facKsnCmd));
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(result, 30, READWAITTIME);
			LoggerUtil.e("result line 286:"+ISOUtils.hexString(result));
			if((retCode=ISOUtils.dumpString(result, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),time,TESTITEM,retCode);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.KSN, ksnStr))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			// case5:生产指令方式设置机器号
			byte[] facJqhCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x02},ISOUtils.hex2byte("0800083132333435363738"));
			mNlManager.write(facJqhCmd, facJqhCmd.length, 30);
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(result, 30, READWAITTIME);
			if((retCode=ISOUtils.dumpString(result, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, FUN_NAME, gKeepTimeErr, "line %d:第%d次:%s测试失败(%s)", Tools.getLineInfo(),time,TESTITEM,retCode);
				if(GlobalVariable.isContinue==false)
					continue;
			}
			
			if((ret = getTestNum(FUN_NAME,Tools.getLineInfo(),time,NumType.JQH, jqhStr))!=NDK_OK)
			{
				if(GlobalVariable.isContinue==false)
					continue;
			}
			// case5:多次设置SN、PN等信息，存储的位置应一致
			succ++;
		}
		gui.cls_show_msg1_record(TAG, FUN_NAME, 0,"共测试%d次,成功%d次(需要至少构造K21端只存在PN/SN/CSN/KSN/机器号或全部不存在的情况进入本用例测试)", count,succ);
	}
	
	
	private int getTestNum(String funName,int funLine,int time,NumType type,String preValue)
	{
		ParaEnum.EM_SYS_HWINFO posType = null; 
		byte[] factCmd = null;
		byte[] result = new byte[30];
		String retCode;
		
		switch (type) {
		case SN:
			posType = EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN;
			factCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x01},ISOUtils.hex2byte("01"));
			break;
			
		case PN:
			posType = EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN;
			factCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x01},ISOUtils.hex2byte("02"));
			break;
			
		case KSN:
			factCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x01},ISOUtils.hex2byte("10"));
			break;
			
		case CSN:
			factCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x01},ISOUtils.hex2byte("04"));
			break;
			
		case JQH:
			factCmd = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x01},ISOUtils.hex2byte("08"));
			break;
		}
		
		// getPosInfo的方式获取SN或PN
		if(posType==EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN||posType==EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN)
		{
			JniNdk.JNI_Sys_GetPosInfo(posType.secsyshwinfo(), 0, result);
			if(ISOUtils.ASCII2String(result,preValue.length()).equals(preValue)==false)
			{
				gui.cls_show_msg1_record(TAG, funName, gKeepTimeErr, "line %d:第%d次:%s获取不一致(%s)", funLine,time,type,ISOUtils.ASCII2String(result,preValue.length()));
				if(GlobalVariable.isContinue==false)
					return -1;
			}
		}
		
		
		// 生产指令获取SN/PN/CSN/KSN/JQH号
		mNlManager.write(factCmd, factCmd.length, 30);
		// 读最好修改为循环读并用线程的方式
		mNlManager.ioctl(0x540B, ioctrl);
		mNlManager.read(result, 30, READWAITTIME);
		LoggerUtil.d("result line 370:"+ISOUtils.hexString(result));
		if((retCode=ISOUtils.dumpString(result, 7, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, funName, gKeepTimeErr, "line %d:第%d次:%s返回报文错误(%s)",funLine,time,type,retCode);
			if(GlobalVariable.isContinue==false)
				return -1;
		}
		String factRet=null;
		// KSN如果设置数字和大小写字母就是会被转成HEX add by zhengxq 20180911
		if(type==NumType.KSN)
		{
			 factRet = ISOUtils.hexString(result,12,4);
		}
		else
		{
			// 对比生产指令和getPosInfo获取到的SN/PN/CSN/KSN/JQH号
			factRet = ISOUtils.dumpString(result,12,preValue.length());
		}
		
		if(preValue.equals(factRet)==false)
		{
			gui.cls_show_msg1_record(TAG, funName, gKeepTimeErr, "line %d:第%d次:%s生产获取不一致(%s)",funLine,time,type,factRet);
			if(GlobalVariable.isContinue==false)
				return -1;
		}
		
		return NDK_OK;
	}
	
	public final void setConfig(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.input_config, null);

		final EditText inputTest = (EditText) view.findViewById(R.id.pn_config);

		new BaseDialog(activity, view, "输入", "确定","取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					// 静态
					Log.e(TAG, " "+inputTest.getText().toString());
					if(!inputTest.getText().toString().equals("")){
						input = inputTest.getText().toString();
					}
				}
				stop = false;
			}
		}).show();
	}

	@Override
	public void onTestUp() {
		// 测试前置，连接K21端
		if (!(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)||(GlobalVariable.currentPlatform==Model_Type.X1)) ){
			mNlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
			mNlManager.setconfig(115200, 0, "8N1NN".getBytes());
			mNlManager.connect(false);
		}
	}

	@Override
	public void onTestDown() {
		if (!(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)||(GlobalVariable.currentPlatform==Model_Type.X1)) ){
		mNlManager.disconnect();
		}
	}

}
