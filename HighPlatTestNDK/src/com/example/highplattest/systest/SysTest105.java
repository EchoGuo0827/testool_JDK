package com.example.highplattest.systest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_MODE;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.DiskInfo;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest105.java 
 * Author 			: chending
 * version 			: 
 * DATE 			    : 20191218
 * directory 		: 
 * description 		: 打印/文件系统交叉(复现610客诉)
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20150305	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest105 extends DefaultFragment {
	private final String TAG = SysTest105.class.getSimpleName();
	private final String TESTITEM = "打印/文件系统交叉(复现610客诉)";
	private DiskType diskType;
	private final int MAXWAITTIME = 10;
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	private String diskString;
	String rootDir, fName;
	String data="*feedline 3\n"+
			"!NLFONT 3 13 3\n"+
			"!yspace 6\n"+
			" *text c 中国银联特约商户签购单\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text c 持卡人存单 请妥善保管\n"+
			" *line\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			" *text l 商户名称 测试一下99\n"+
			" !NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 商户编码 829581148160502\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 终端编号 60845516\n"+
			"*line\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 发卡银行 招商银行\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 卡    号 621483******6284\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 卡有效期 2405\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 凭 证 号 010204\n"+
			" !NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 交易类型 消费\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 参 考 号 231812316870\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 批 次 号 000100\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 日期时间 20180423181240\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 金    额 RMB 0.01\n"+
			"*line\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 备    注\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AQRC:\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AID:\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l CSN: CUM:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l TSI: TUR:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l ATC: UNPR NO:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AIP: TermCap:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l IAD:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l APPLAB:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l APPNAME:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			" *text l 打印时间 2018-04-23 18:12:41\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 持卡人签名 \n"+
			"*feedline 3\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 本人确认以上交易，同意将其计入本卡账户\n "+
			"*feedline 7\n"+"*feedline 3\n"+
			"!NLFONT 3 13 3\n"+
			"!yspace 6\n"+
			" *text c 中国银联特约商户签购单\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text c 持卡人存单 请妥善保管\n"+
			" *line\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			" *text l 商户名称 测试一下99\n"+
			" !NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 商户编码 829581148160502\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 终端编号 60845516\n"+
			"*line\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 发卡银行 招商银行\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 卡    号 621483******6284\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 卡有效期 2405\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 凭 证 号 010204\n"+
			" !NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 交易类型 消费\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 参 考 号 231812316870\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 批 次 号 000100\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 日期时间 20180423181240\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 金    额 RMB 0.01\n"+
			"*line\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 备    注\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AQRC:\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AID:\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l CSN: CUM:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l TSI: TUR:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l ATC: UNPR NO:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AIP: TermCap:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l IAD:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l APPLAB:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l APPNAME:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			" *text l 打印时间 2018-04-23 18:12:41\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 持卡人签名 \n"+
			"*feedline 3\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 本人确认以上交易，同意将其计入本卡账户\n "+
			"*feedline 7\n"+"*feedline 3\n"+
			"!NLFONT 3 13 3\n"+
			"!yspace 6\n"+
			" *text c 中国银联特约商户签购单\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text c 持卡人存单 请妥善保管\n"+
			" *line\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			" *text l 商户名称 测试一下99\n"+
			" !NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 商户编码 829581148160502\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 终端编号 60845516\n"+
			"*line\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 发卡银行 招商银行\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 卡    号 621483******6284\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 卡有效期 2405\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 凭 证 号 010204\n"+
			" !NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 交易类型 消费\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 参 考 号 231812316870\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 批 次 号 000100\n"+
			"!NLFONT 1 12 3\n"+
			"!yspace 6\n"+
			"*text l 日期时间 20180423181240\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 金    额 RMB 0.01\n"+
			"*line\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 备    注\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AQRC:\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AID:\n"+
			" !NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l CSN: CUM:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l TSI: TUR:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l ATC: UNPR NO:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l AIP: TermCap:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l IAD:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l APPLAB:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l APPNAME:\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			" *text l 打印时间 2018-04-23 18:12:41\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 持卡人签名 \n"+
			"*feedline 3\n"+
			"!NLFONT 6 1 3\n"+
			"!yspace 6\n"+
			"*text l 本人确认以上交易，同意将其计入本卡账户\n "+
			"*feedline 7\n";
	private final String TESTFILE = "test.txt";
	public void systest105() 
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		printUtil = new PrintUtil( myactivity, handler,true);
		while (true) {
			int returnValue=gui.cls_show_msg("打印/文件系统交叉(复现610客诉)\n0.打印配置\n1.SD/U盘配置\n2.交叉测试");
			switch (returnValue) 
			{
			
			case '0':
				//调用打印配置函数
				config.print_config();
				break;
				
			case '1':
				// SD卡U盘配置
				diskType =getDiskType(config.confSDU());
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				
				break;
			
			case ESC:
				intentSys();
				return;
				
			}
			
			
			
		}
		
		
		
		
	}
	//交叉测试
	private void cross_test() {
		// TODO Auto-generated method stub
		int fd;
		int iRet = -1;
		long oldTime, oldTime1;
		DiskInfo diskInfo = new DiskInfo();
		oldTime = System.currentTimeMillis();
		
		byte[] sBuf = new byte[1024];
		byte[] defineBuf = new byte[1024*5];
		byte[] memBuf = new byte[1024*5];
		byte[] sn_32 = new byte[32];
		
		final PacketBean packet = new PacketBean();
		int bak,cnt;
		int ret;
		int succ=0;
		int printerStatus;
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		//判断SD卡或U盘是否存在
		while (true) 
		{
			ret = diskInfo.NDK_DiskGetstate();
			// 代表SD卡挂载成功
			if (ret == NDK_OK)
				break;
			if (Tools.getStopTime(oldTime) > 30) 
			{
				gui.cls_show_msg1( g_keeptime, "%s, line %d:获取状态超时(%d)",TAG, Tools.getLineInfo(), ret);
				return ;
			}
			SystemClock.sleep(1000);
		}
		gui.cls_show_msg("测试前请确保导入测试图片---路径为sdcard/picture/IHDR1.png");
		//测试前置
		//创建一个文件使用JNI封装的NDK接口创建
//		rootDir=GlobalVariable.TFPath;
//		fName = rootDir + TESTFILE;
		fName="/appfs/test";
		if((fd = JniNdk.JNI_FsOpen(fName, "w"))<0)
		{
			gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:创建%s文件失败(%d)", Tools.getLineInfo(),fName,fd);
			return;
		}
		//写入文件
		for(int i=0;i<5;i++)
		{
			Arrays.fill(sBuf, (byte)(i+0x30));

			if((iRet = JniNdk.JNI_FsWrite(fd, sBuf, sBuf.length))!=1024)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:写文件失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}
			System.arraycopy(sBuf, 0, defineBuf, i*1024, 1024);
		}
		//关闭文件
		if((iRet = JniNdk.JNI_FsClose(fd))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:关闭文件失败(%d)", Tools.getLineInfo(),iRet);
			return;
		}
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		//交叉测试
		while (cnt>0) {
			//测试退出点
			if(gui.cls_show_msg1(3,"打印/文件系统交叉测试,已执行%d次,成功%d次,[取消]退出测试",bak-cnt,succ)==ESC)
				break;
			cnt--;
//			//检测文件是否还存在
//			if((iRet = JniNdk.JNI_FsExist(fName))==NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:测试文件未删除(%d)",Tools.getLineInfo(), ret);
//				return;
//			}
			//发送大数据
			if((ret=JniNdk.JNI_Print_Script(data, Tools.getWordCount(data)))!=NDK_OK){
				
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送单据数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
			if((ret=JniNdk.JNI_Print_Png(gPicPath+"IHDR1.png",0,1))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送图片数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//再次打开文件
			if((fd = JniNdk.JNI_FsOpen(fName, "w"))<0)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:创建%s文件失败(%d)", Tools.getLineInfo(),fName,fd);
				return;
			}
			//开始打印
			if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:打印失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//检测打印机状态
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				return;
			}
			//再次发送数据
			if((ret=JniNdk.JNI_Print_Script(data, Tools.getWordCount(data)))!=NDK_OK){
				
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送单据数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
			if((ret=JniNdk.JNI_Print_Png(gPicPath+"IHDR1.png",0,1))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送图片数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//再次写入文件
			for(int i=0;i<5;i++)
			{
				Arrays.fill(sBuf, (byte)(i+0x30));

				if((iRet = JniNdk.JNI_FsWrite(fd, sBuf, sBuf.length))!=1024)// 文件写操作是追加的方式 不需要seek
				{
					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:写文件失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				System.arraycopy(sBuf, 0, defineBuf, i*1024, 1024);
			}
			//开始打印
			if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:打印失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//检测打印机状态
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				return;
			}
			
			//再次发送数据
			if((ret=JniNdk.JNI_Print_Script(data, Tools.getWordCount(data)))!=NDK_OK){
				
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送单据数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
			if((ret=JniNdk.JNI_Print_Png(gPicPath+"IHDR1.png",0,1))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送图片数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//读取文件
			Arrays.fill(memBuf, (byte)0x00);
			if((iRet = JniNdk.JNI_FsSeek(fd, 0l, 1))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:Seek文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
				JniNdk.JNI_FsClose(fd);
				continue;
			}
			if((iRet = JniNdk.JNI_FsRead(fd, memBuf, memBuf.length))!=memBuf.length)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:读文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
				JniNdk.JNI_FsClose(fd);
				continue;
			}
			
			// 比较文件的内容
			if(Tools.memcmp(defineBuf, memBuf, 5*1024)==false)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:比较读写文件内容错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.ASCII2String(memBuf));
				continue;
			}
			//开始打印
			if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:打印失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			
			//检测打印机状态
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				return;
			}
			
			//再次发送数据
			if((ret=JniNdk.JNI_Print_Script(data, Tools.getWordCount(data)))!=NDK_OK){
				
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送单据数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
			if((ret=JniNdk.JNI_Print_Png(gPicPath+"IHDR1.png",0,1))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送图片数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			
			//关闭文件
			if((iRet = JniNdk.JNI_FsClose(fd))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:关闭文件失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}
			
			//开始打印
			if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:打印失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//检测打印机状态
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				return;
			}
			
			//再次发送数据
			if((ret=JniNdk.JNI_Print_Script(data, Tools.getWordCount(data)))!=NDK_OK){
				
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送单据数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
			if((ret=JniNdk.JNI_Print_Png(gPicPath+"IHDR1.png",0,1))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送图片数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			
			//检测文件是否还存在
			if((iRet = JniNdk.JNI_FsExist(fName))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:测试文件未删除(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//开始打印
			if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:打印失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//检测打印机状态
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				return;
			}
			
			//再次发送数据
			if((ret=JniNdk.JNI_Print_Script(data, Tools.getWordCount(data)))!=NDK_OK){
				
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送单据数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0);
			if((ret=JniNdk.JNI_Print_Png(gPicPath+"IHDR1.png",0,1))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:发送图片数据失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//删除文件
			if((iRet = JniNdk.JNI_FsDel(fName))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:删除%s文件失败(%d)", Tools.getLineInfo(),fName,iRet);
				return;
			}
			
			//开始打印
			if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:打印失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//检测打印机状态
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次获取打印机状态异常(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				return;
			}
			succ++;
			
		}
		gui.cls_show_msg1_record(TAG, "打印/文件系统交叉(复现610客诉)",g_keeptime, "打印/文件系统交叉(复现610客诉)测试通过-------------");
	}
}
