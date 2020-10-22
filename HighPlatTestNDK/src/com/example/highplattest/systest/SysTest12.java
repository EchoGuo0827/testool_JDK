package com.example.highplattest.systest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.DiskBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DiskInfo;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest12.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150205
 * directory 		: 
 * description 		: SD卡/TF卡/U盘综合
 * related document :
 * history 		 	: 变更点			   变更时间			变更人员
 *			  		  外置路径改为动态获取		 20200408	 	zhengxq
 *					写满设备改为动态写入多个文件写满。每次写入20M 20200710 陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest12 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest12.class.getSimpleName();
	private final String TESTITEM = "SD卡(U盘)性能、压力";
	private final String TESTFILE = "test.txt";
	// 一次写入200K
	private final int BUFFERSIZE = 1024*200;
	// 获取状态次数
	private final int GETSTATETIMES = 30;
	// 性能循环次数
	private final int ABILITYNUM = 20;
	private final int DEFAULT_COUNT = 100;
	private Gui gui = null;
	private DiskType diskType = DiskType.SDDSK;
	private String diskString;
	FileSystem fileSystem = new FileSystem();
	
	// Sd/U盘的主线程
	public void systest12() 
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			diskString = new Config(myactivity,handler).confSDU();
			String[] diskTypes=diskString.split(",");
		
			g_CycleTime=50;
			for (int i = 0; i < diskTypes.length; i++) {
				diskType=getDiskType(diskTypes[i]);
				LoggerUtil.e("systest12:"+diskType);
				sdPre();
				sdAbility();
			}
			
			return;
		}
		while(true)
		{
			int returnValue=gui.cls_show_msg("SD/U盘综合测试\n0.压力\n1.性能\n2.异常\n3.配置\n");
			switch (returnValue) 
			{
			
			case '0':
				sdPre();
				break;
				
			case '1':
				sdAbility();
				break;
				
			case '2':
				sdAbnormal();
				break;
				
			case '3':
				// 配置
			    diskString = new Config(myactivity,handler).confSDU();
			    diskType=getDiskType(diskString);
				break;
				
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	
	// 读写压力
	public void sdPre() 
	{
		int returnValue=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				// 写满设备操作不要放到自动化当中，会影响到其他用例的执行 modify by zhengxq 20180814
				if(++returnValue == '1')
				{
					gui.cls_show_msg1_record(TAG, "sdPre", 2, "%s连续压力测试结束", diskType);
					return;
				}
				if(gui.cls_show_msg1(3,"即将%s进行连续压力测试,[取消]退出",diskType)==ESC)
					return;
					
			}
			else
			{
				returnValue=gui.cls_show_msg("SD/U盘压力\n0.设备读写压力\n1.写满设备");
			}
			switch (returnValue) 
			{
				case '0':
					try {
						SD_press_wr();
					} catch (Exception e) {
						gui.cls_show_msg1_record(TAG, "压力异常", 2, e.getMessage());
					}
					
					break;
				
				case '1':
					SD_filesys_bigfile();
					break;

				case ESC:
					return;
			}
		}
	}
	
	// 性能
	public void sdAbility() 
	{
		/*private & local definition*/
		String fname = null;
		int testDataSize = 10*1024*1024;
		byte[] writebuf = new byte[BUFFERSIZE];
		byte[] readbuf = new byte[BUFFERSIZE];
		int i=0, wrlen = BUFFERSIZE,loop = 0;
		int cnt = testDataSize/BUFFERSIZE;
		long oldtime,ret=-1;
		float writetime=0.0f,readtime = 0.0f;
		
		gui.cls_show_msg("请确保已插上%s,任意键继续", diskType == DiskType.SDDSK?"SD卡":diskType == DiskType.UDISK?"U盘":"TF卡");

		
		while(true)
		{
			if(gui.cls_show_msg1(2, "开始第%d次%s读写,[取消]退出测试...", i+1,diskType == DiskType.SDDSK?"SD卡":diskType == DiskType.UDISK?"U盘":"TF卡")==ESC)
				break;
			
			if(i++>=ABILITYNUM)
				break;
			// 获取目录
			fname = Tools.getDesignPath(myactivity, diskType)+"/test.txt";
			if(fname.contains("null"))
			{
				gui.cls_show_msg("未获取到对应的路径，设备未挂载，任意键退出");
				return;
			}
			
			// 测试前置删除文件
			if(fileSystem.JDK_FsExist(fname)==JDK_OK)
			{
				fileSystem.JDK_FsDel(fname);
			}
			
			Log.d("eric", "diskType==="+diskType);
			Log.d("eric", "fname==="+fname);
//			gui.cls_show_msg("当前路径为:%s", fname);
			// 打开文件
			if((ret = fileSystem.JDK_FsOpen(fname, "w"))<0)
			{
				gui.cls_show_msg1_record(TAG, "sdAbility", g_keeptime,"line %d:第%d次创建测试文件失败(%d)",Tools.getLineInfo(),i,ret);
				break;
			}
			// 写文件
			gui.cls_show_msg1(2, "第%d次生成测试文件约%dKB", i,cnt*wrlen/1024);
			for (int j = 0; j < wrlen; j++) 
				writebuf[j] = (byte) (Math.random()*256);
			
			oldtime = System.currentTimeMillis();
			for (int j = 0; j < cnt; j++) 
			{
				// 此为有追加的模式
				if((ret = fileSystem.JDK_FsWrite(fname, writebuf, wrlen,2))!= wrlen)
				{
					gui.cls_show_msg1_record(TAG, "sdAbility", g_keeptime, "line %d:第%d次写测试文件失败(%d)",Tools.getLineInfo(),i,ret);
					break;
				}
			}
			if(ret !=wrlen)
				break;
			writetime=writetime+Tools.getStopTime(oldtime);
			
			// 文件大小是否发生改变
			if ((ret = fileSystem.JDK_FsFileSize(fname)) != cnt* wrlen) 
			{
				gui.cls_show_msg1_record(TAG,"sdAbility", g_keeptime,"%s, line %d:文件大小校验失败(实测:%dB, 预期:%dB)", TAG,Tools.getLineInfo(), ret, cnt * wrlen);
				break;
			}

			// 读文件
			gui.cls_printf(String.format("第%d次校验%s文件中(约%dKB),请稍后...", i,diskType == DiskType.UDISK ? "U盘": diskType == DiskType.SDDSK ? "SD卡" : "TF卡", cnt* wrlen / 1024).getBytes());
			oldtime = System.currentTimeMillis();
			// 循环读数据
			FileInputStream fileInput;
			try {
				fileInput = new FileInputStream(new File(fname));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			for (loop = 0; loop < cnt; loop++) 
			{
				// 注意偏移值的变化
				if ((ret = fileSystem.JDK_FsRead(fileInput, readbuf,wrlen)) != wrlen) 
				{
					gui.cls_show_msg1_record(TAG,"sdAbility", g_keeptime, "line %d:第%d次读文件失败(%d)",Tools.getLineInfo(), i, ret);
					break;
				}
			}
			readtime = readtime+Tools.getStopTime(oldtime);
			for (int j = 0; j < cnt; j++) 
			{
				if(!Tools.memcmp(writebuf, readbuf, wrlen))
				{
					gui.cls_show_msg1_record(TAG,"sdAbility", g_keeptime, "line %d:第%d次数据校验失败",Tools.getLineInfo(), i);
					break;
				}
			}
			// 关闭测试文件
			fileSystem.JDK_FsClose(fileInput);
			// 删除测试文件
			fileSystem.JDK_FsDel(fname);
		}
		
		LoggerUtil.d("readTime："+readtime+"\nwriteTime:"+writetime);
		// 每次读写数据为200K
		if(i == ABILITYNUM+1)
		{
			// 性能数据
			gui.cls_show_msg1_record(TAG,"sdAbility", g_time_0, "(%s)写时间：%3.2fs/次 读时间：%3.2fs/次  写数据速度%3.2fMB/s,读数据速度：%3.2fMB/s", diskType,
					writetime/ABILITYNUM,readtime/ABILITYNUM,cnt*200*ABILITYNUM/writetime/1024,cnt*200*ABILITYNUM/readtime/1024);
		}
		else
			gui.cls_show_msg1(2, "用户取消测试");
		
	}
	
	// 读写压力
	public int SD_press_wr() 
	{
		/*private & local definition*/
		String fname = null;
		byte[] writebuf = new byte[BUFFERSIZE];
		byte[] readbuf = new byte[BUFFERSIZE];
		int loop = 0,cnt = 0,wrlen = 0,i = 0;
		int bak = 0,nSuccNum = 0,ret = -1;
		long oldtime = 0;
		boolean restartFlag = true;
		
		/*process body*/
		PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_COUNT));
		bak = packet.getLifecycle();
		gui.cls_show_msg("请确保已插上%s,任意键继续", diskType == DiskType.UDISK?"U盘":diskType==DiskType.SDDSK?"SD卡":"TF卡");
		fname = Tools.getDesignPath(myactivity, diskType)+"/test.txt";
		if(fname.contains("null"))
		{
			gui.cls_show_msg("未获取到对应的路径，设备未挂载，任意键退出");
			return -1;
		}
		
		LoggerUtil.i("002,storage path="+fname);
		
		gui.cls_show_msg1(1, "正在打开%s路径", fname);
		// 测试前置
		if((ret = fileSystem.JDK_FsOpen(fname, "w"))<0)
		{
			gui.cls_show_msg1_record(TAG, "SD_press_wr", g_keeptime, "line %d:创建测试文件失败(%d,%s)",Tools.getLineInfo(),ret,fname);
			return ret;
		}
		while(true)
		{
			// 保护动作
			fileSystem.JDK_FsDel(fname);
			if(gui.cls_show_msg1(1, "总共%d次开始第%d次%s读写压力,[取消]退出测试...", bak,i+1,diskType == DiskType.UDISK? "U盘":diskType == DiskType.SDDSK? "SD卡":"TF卡")==ESC)
				break;
			// 达到设置次数后退出
			if(i == bak)
				break;
			// 测试次数计数
			i++;
			// 写文件
			cnt = (int) (Math.random()*1023+2);
			// 一次写文件约100k
			wrlen = (int) (BUFFERSIZE-Math.random()*8);
			for (loop = 0;  loop< wrlen; loop++) 
				writebuf[loop] = (byte) (Math.random()*256);
			for (loop = 0;  loop< cnt; loop++) 
			{
				gui.cls_printf(String.format("第%d次生成设备文件(约%dKB),已生成(%dKB)请稍后...", i,cnt*wrlen/1024,loop*wrlen/1024).getBytes());
				if((ret = (int) fileSystem.JDK_FsWrite(fname, writebuf, wrlen,2))!= wrlen)
				{
					gui.cls_show_msg1_record(TAG, "SD_press_wr", g_keeptime, "line %d:第%d次写测试文件失败(%d)",Tools.getLineInfo(),i,ret);
					restartFlag = false;
					break;
				}
			}
			if(restartFlag== false)
			{
				restartFlag = true;
				continue;
			}
			// 打开文件
			if((ret = fileSystem.JDK_FsOpen(fname, "r"))<0)
			{
				gui.cls_show_msg1_record(TAG, "SD_press_wr", g_keeptime, "line %d:第%d次创建测试文件失败(%d)",Tools.getLineInfo(),i,ret);
				continue;
			}
			// 文件大小是否发生改变
			if((ret = fileSystem.JDK_FsFileSize(fname))!=cnt*wrlen)
			{
				gui.cls_show_msg1_record(TAG, "SD_press_wr",g_keeptime, "%s, line %d:文件大小校验失败(实测:%dB, 预期:%dB)", TAG, Tools.getLineInfo(),
						ret, cnt * wrlen);
				continue;
			}
				
			// 读文件
			gui.cls_printf(String.format("第%d次校验%s文件中(约%dKB),请稍后...", i,diskType == DiskType.UDISK? "U盘":diskType == DiskType.SDDSK? "SD卡":"TF卡",cnt*wrlen/1024).getBytes());
			oldtime = System.currentTimeMillis();
			FileInputStream fileInput;
			try {
				fileInput = new FileInputStream(new File(fname));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return JDK_FS_NO_EXIST;
			}
			for (loop = 0;  loop< cnt; loop++) 
			{
				if((ret = fileSystem.JDK_FsRead(fileInput, readbuf,wrlen))!=wrlen||!Tools.memcmp(writebuf, readbuf, wrlen))
				{
					gui.cls_show_msg1_record(TAG, "SD_press_wr", g_keeptime, "line %d:第%d次读文件失败(%d)",Tools.getLineInfo(),i,ret);
					restartFlag = false;
					break;
				}
				Tools.getStopTime(oldtime);
			}
			if(restartFlag== false)
			{
				restartFlag = true;
				continue;
			}
			// 关闭输入流
			fileSystem.JDK_FsClose(fileInput);
			// 删除文件关闭设备
			fileSystem.JDK_FsDel(fname);
			nSuccNum++;
		}
		return gui.cls_show_msg1_record(TAG, "SD_press_wr", g_time_0,"%s读写压力测试完成,一共%d次测试已成功%d次", diskType == DiskType.UDISK? "U盘":diskType == DiskType.SDDSK? "SD卡":"TF卡",i,nSuccNum);
	}
	
	// 写满设备
	public int SD_filesys_bigfile() 
	{
		/*private & local definition*/
		// 一次写入20M
		int BUFFERSIZE = 1024*1024*20;
		long ret=0;
		long writelen = 0;
		int loop = 0,cnt = 0,i = 0;
		int j= 0,m = 0,nCnt,succ = 0;
		String fname = null;
		char[] filename = new char[4];
		byte[] writebuf = new byte[BUFFERSIZE];
		DiskBean diskBean = new DiskBean();
		int count;//一次最多4G.写入总文件数
		long remainingspace;
		//获取当前剩余空间
		fname=Tools.getDesignPath(myactivity, diskType);
		LoggerUtil.d("剩余空间="+new DiskInfo().getAvailableSize(fname));
		remainingspace=new DiskInfo().getAvailableSize(fname);
		if (remainingspace%(1024*1024*4)==0) {
			count=(int) (remainingspace/(1024*1024*4));
		}else {
			count=(int) (remainingspace/(1024*1024*4))+1;
		}
		Log.d("eric_chen", "写入文件个数为："+count);
		gui.cls_show_msg("请确保已插上%s,任意键继续", diskType == DiskType.UDISK?"U盘":diskType==DiskType.SDDSK?"SD卡":"TF卡");
		nCnt = 0;
		while(true)
		{
			if((ret = new DiskInfo().NDK_DiskGetstate())== NDK_OK)
				break;
			SystemClock.sleep(1000);
			if(++nCnt>=GETSTATETIMES)
			{
				gui.cls_show_msg1_record(TAG, "SD_filesys_bigfile", g_keeptime, "line %d:取%s状态失败", Tools.getLineInfo(),diskType== DiskType.SDDSK? "SD卡":"U盘");
				return NDK_ERR_QUIT;
			}
		}
		fname = Tools.getDesignPath(myactivity, diskType)+"/test.txt";
		if(fname.contains("null"))
		{
			gui.cls_show_msg("未获取到对应的路径，设备未挂载，任意键退出");
			return -1;
		}
		boolean flag=false;
		while (true) 
		{
			for (int k = 0; k < count; k++) {
				i++;
				fname = Tools.getDesignPath(myactivity, diskType)+"/test"+k+".txt";
				Log.d("eric_chen", "当前文件路径----"+fname);
				if(gui.cls_show_msg2(0.1f, "开始第%d次%s读写压力,[取消]键退出测试...", i,diskType == DiskType.UDISK?"U盘":diskType==DiskType.SDDSK?"SD卡":"TF卡")==ESC)
					break;
				if (fileSystem.JDK_FsWrite(fname, writebuf,writebuf.length, 2) != writebuf.length) 
				{    
					flag=true;
					LoggerUtil.d("剩余空间="+new DiskInfo().getAvailableSize(fname));
					break;
				}
				succ++;
			}
			if (flag) {
				break;
			}
	
		}
//		while(true)
//		{
//			// 保护动作
//			i++;
//			if(gui.cls_show_msg1(1, "开始第%d次%s读写压力,[取消]键退出测试...", i,diskType == DiskType.UDISK?"U盘":diskType==DiskType.SDDSK?"SD卡":"TF卡")==ESC)
//				break;
//			for (loop = 0; loop < 3; loop++) 
//				filename[loop] = (char) (Math.random()*9);
//			fname = String.format(Locale.CHINA,"%s%s",diskType == DiskType.UDISK? GlobalVariable.uPath:diskType == DiskType.SDDSK? GlobalVariable.sdPath:GlobalVariable.TFPath,TESTFILE);
//			if (GlobalVariable.gCurPlatVer==Platform_Ver.A9) {
//				
//				fname=getStoragePath(myactivity,true)+"/test.txt";
//			}
//			if((ret = fileSystem.JDK_FsOpen(fname, "w"))<0)
//			{
//				gui.cls_show_msg1_record(TAG, "SD_filesys_bigfile", g_keeptime, "line %d:文件打开失败", Tools.getLineInfo());
//				// 连续失败则退出
//				if(++j>=GETSTATETIMES)
//					break;
//			}
//			j=0;
//			Arrays.fill(writebuf, (byte) (Math.random()*256));
//			cnt = (int) (Math.random()*1023+2);
//			for (loop = 0; loop < cnt; loop++) 
//			{
//				if((ret = new DiskInfo().NDK_DiskGetInfo(GlobalVariable.sdPath, diskBean))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG, "SD_filesys_bigfile", g_keeptime, "line %d:获取%s信息失败", Tools.getLineInfo(),diskType==DiskType.SDDSK? "SD卡":"U盘");
//					if(++m>=GETSTATETIMES)
//					{
//						break;
//					}
//					break;
//				}
//				m=0;
//				if(diskType ==DiskType.SDDSK)// 内置TF卡
//					gui.cls_printf(String.format("%s总空间:[%d]k\n剩余空间:[%d]k\n此次写入[%d]k\n剩余[%d]k\n", 
//							diskType==DiskType.SDDSK? "SD盘":"U盘",diskBean.getTotalSpace(),diskBean.getFreeSpace(),cnt*writelen/1024,
//									(cnt*writelen/1024-loop*writelen/1024)).getBytes());
//				else
//					gui.cls_printf(String.format("%s此次写入[%d]k\n剩余[%d]k\n", diskType==DiskType.TFDSK?
//							"TF卡":"U盘",cnt*writelen/1024,(cnt*writelen/1024-loop*writelen/1024)).getBytes());
//				if((writelen = fileSystem.JDK_FsWrite(fname, writebuf, writebuf.length, 2))!= writebuf.length)
//				{
//					// 判断剩余空间小于100M这样可以认为是写满了
//					LoggerUtil.d("剩余空间="+new DiskInfo().getAllSize(GlobalVariable.sdPath));
//					gui.cls_show_msg1(2, "line %d:设备已写满,请重启机器,点击退出键退出...!(%d,%d)", Tools.getLineInfo(),loop,ret);
//					// 强制用户关机
//					while(true);
//				}
//			}
//			succ++;
//		}
		gui.cls_show_msg1_record(TAG, "SD_filesys_bigfile",g_time_0, "总共写文件%d次成功%d次", i,succ);
//		fileSystem.JDK_FsDel(fname);
		return NDK_OK;
	}
	
	// 异常测试
	public void sdAbnormal() 
	{
		/*private & local definition*/
		String fname;
		FileInputStream fileIn = null;
		byte[] writeBuf = new byte[BUFFERSIZE];
		byte[] readBuf = new byte[BUFFERSIZE];
		long ret = 0;
		
		/*process body*/
		gui.cls_show_msg("%s,任意键继续", diskType==DiskType.SDDSK? "SD卡":diskType == DiskType.TFDSK?"TF卡":"U盘");
		// case1:进入休眠在唤醒,读写正常
		gui.cls_show_msg("测试前确保开启休眠功能,完成任意键继续");
		gui.cls_show_msg("请等待Android端进入休眠,唤醒后点击是继续测试");
		fname = Tools.getDesignPath(myactivity, diskType)+"/test.txt";
		if(fname.contains("null"))
		{
			gui.cls_show_msg("未获取到对应的路径，设备未挂载，任意键退出");
			return;
		}
		
		fileSystem.JDK_FsDel(fname);
		while(true)
		{
			if((ret = fileSystem.JDK_FsOpen(fname, "w"))<0)
			{
				gui.cls_show_msg1_record(TAG, "sdAbnormal", g_keeptime, "line %d:创建测试文件失败(%d)",Tools.getLineInfo(),ret);
				break;
			}
			Arrays.fill(writeBuf, (byte) (Math.random()*256));
			if((ret = fileSystem.JDK_FsWrite(fname, writeBuf, BUFFERSIZE, 0))!=BUFFERSIZE)
			{
				gui.cls_show_msg1_record(TAG, "sdAbnormal",g_keeptime, "line %d:写测试文件失败(%d)", Tools.getLineInfo(),ret);
				break;
			}
			// 校验结果
			if((ret = fileSystem.JDK_FsOpen(fname, "r"))<0)
			{
				gui.cls_show_msg1_record(TAG, "sdAbnormal", g_keeptime, "line %d:打开文件失败(%d)",Tools.getLineInfo(),ret);
				break;
			}
			try {
				fileIn = new FileInputStream(new File(fname));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			// 读文件
			if((ret = fileSystem.JDK_FsRead(fileIn, readBuf,BUFFERSIZE))!=BUFFERSIZE)
			{
				gui.cls_show_msg1_record(TAG, "sdAbnormal",g_keeptime, "line %d:读文件失败(%d)",Tools.getLineInfo(),ret);
				break;
			}
			if(!Tools.memcmp(writeBuf, readBuf, BUFFERSIZE))
			{
				gui.cls_show_msg1_record(TAG, "sdAbnormal", g_keeptime, "line %d:校验文件失败",Tools.getLineInfo());
			}
			fileSystem.JDK_FsDel(fname);
			gui.cls_show_msg1_record(TAG, "sdAbnormal",g_time_0, "%s异常测试通过", diskType == DiskType.SDDSK?"SD卡":diskType == DiskType.TFDSK?"TF卡":"U盘");
			break;
		}
		// 关闭流
		fileSystem.JDK_FsClose(fileIn);
		fileSystem.JDK_FsDel(fname);
		return;
		
	}
	
//	public static List getUSBPaths(Context con) {//反射获取路径
//
//		String[] paths = null;
//
//		List data = new ArrayList(); // include sd and usb devices
//
//		StorageManager storageManager = (StorageManager) con .getSystemService(Context.STORAGE_SERVICE);
//
//		try {
//
//		paths = (String[]) StorageManager.class.getMethod("getVolumePaths", null).invoke( storageManager, null);
//
//		for (String path : paths) {
//
//		String state = (String) StorageManager.class.getMethod("getVolumeState", String.class).invoke(storageManager, path);
//
//		if (state.equals(Environment.MEDIA_MOUNTED) && !path.contains("emulated")) { data.add(path);
//
//		} } }
//
//		catch (Exception e) { e.printStackTrace(); }
//
//		return data;
//		}
}
