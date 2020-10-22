package com.example.highplattest.main.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.newland.ndk.JniNdk;

import android.content.Context;
import android.newland.os.NlBuild;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;

/************************************************************************
 * 
 * module 			: main
 * file name 		: Gui.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 
 * directory 		: 
 * description 		: 提示语类
 *history 		 	: author			date			remarks
 *			  		  zhengxq			20141028	 	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Gui implements Lib,NDK
{
	private final String TAG = Gui.class.getSimpleName();
	private final int MAXDISPBUFSIZE = 4096;
	private boolean overFlag = true;
	private Handler handler;
//	private static String LOGFILE = GlobalVariable.sdPath+"result.txt";
	private static String LOGFILE = "/sdcard/result.txt";
	// 编码格式
	private String CODE_FORMAT = "GB2312"; // 还有一种GB2312
//	private static boolean statusFile=false;
	private int handlerMsg = HandlerMsg.TEXTVIEW_SHOW_PUBLIC;	//默认的Handler控制参数  	add by xuess 20171013
	private FileSystem fileSystem = new FileSystem();
	
	
	public Gui(Context context,Handler handler)
	{
		this.handler = handler;
		CODE_FORMAT = "UTF-8";
//		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull && !statusFile)
//		{
//			SimpleDateFormat time = new SimpleDateFormat("yyyyMMddhhmmss",Locale.CHINA);
//			String str = time.format(new java.util.Date());
//			LOGFILE = GlobalVariable.sdPath+"result"+str+".txt";
//			statusFile=true;
//			//存入sp，记录自动化测试记录文件名字
//			SharedPreferences preferences=context.getSharedPreferences("AutoFileName",Context.MODE_PRIVATE);
//			Editor editor=preferences.edit();
//			editor.putString("antoFileName", LOGFILE);
//			editor.commit();
//		}	
	}
	
	/**构造方法给多模块并发用例使用*/		//add by xuess 20171013
	public Gui(Context context,Handler handler,int handlerMsg)
	{
		this.handler = handler;
		CODE_FORMAT = "UTF-8";
		this.handlerMsg = handlerMsg;
		this.overFlag = false;
//		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull && !statusFile)
//		{
//			SimpleDateFormat time = new SimpleDateFormat("yyyyMMddhhmmss",Locale.CHINA);
//			String str = time.format(new java.util.Date());
//			LOGFILE = GlobalVariable.sdPath+"result"+str+".txt";
//			statusFile=true;
//		}	
	}
	
	/**空的构造方法给NDK使用*/
	public Gui(){}
	
	public int cls_show_msg1_record(int color,String fileName,String funName,int time,String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		//byte[] szBuffer = new byte[MAXDISPBUFSIZE+512];
		StringBuffer szBuffer=new StringBuffer();
		String sBuffer = null,nowTime = null;
		
		/*process body*/
		String format = String.format(msg, args);
		cnt = format.length();
		
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg1(5, "%s,line %d:信息太长，点击否退出...", fileName,Tools.getLineInfo());
		}
		else
		{
			// 这里进行界面的显示
			String textSource = null;
			if(color==RED_COLOR)
				textSource = "<font color='#ff0000'>"+String.format("%s", format)+"</font>";
			else
				textSource = "<font color='#ff0000'>"+String.format("%s", format)+"</font>";
			handler.sendMessage(handler.obtainMessage(handlerMsg, Html.fromHtml(textSource)));
			//SystemClock.sleep(time*1000);
		}
		nowTime = getNowTime();
		sBuffer = String.format("%s 文件名:%s 函数名：%s %s\r\n", nowTime,fileName,funName,format);
		Is_testresult_exist();
		//Arrays.fill(szBuffer, (byte) 0);
		LoggerUtil.d("file_path:"+LOGFILE);
		if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
		{
			cls_show_msg1(2,"%s,line %d: 打开文件失败",TAG,Tools.getLineInfo());
		}
		else
		{
			while(true)
			{
				if(overFlag)
				{
					if(fileName.contains("SysTest"))//只有综合模块显示该条
						szBuffer.append(String.format("-------------------------%s :模块测试开始-----------------------------------\r\n", fileName));
					
					if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), szBuffer.toString().getBytes().length, 2)!= szBuffer.toString().getBytes().length)
					{
						cls_show_msg1(2,"%s, line %d: 写入文件失败", TAG,Tools.getLineInfo());
						break;
					}
					overFlag = false;
				}
				// 总是会包写入文件失败？？
				if((int) fileSystem.JDK_FsWrite(LOGFILE, sBuffer.getBytes(), sBuffer.getBytes().length, 2)!=sBuffer.getBytes().length)
				{
					cls_show_msg1(2,"%s, line %d: 写入文件失败...", TAG,Tools.getLineInfo());
					break;
				}
				break;
			}
		}
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull||GlobalVariable.gSequencePressFlag==true)
			return wait_key(3);
		else
			return wait_key(time);
	}
	
	public int cls_show_msg1_record(String fileName,String funName,int time,String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		//byte[] szBuffer = new byte[MAXDISPBUFSIZE+512];
		StringBuffer szBuffer=new StringBuffer();
		String sBuffer = null,nowTime = null;
		
		/*process body*/
		String format = String.format(msg, args);
		cnt = format.length();
		
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg1(5, "%s,line %d:信息太长，点击否退出...", fileName,Tools.getLineInfo());
		}
		else
		{
			// 这里进行界面的显示
			handler.sendMessage(handler.obtainMessage(handlerMsg, String.format("%s", format)));
			//SystemClock.sleep(time*1000);
		}
		nowTime = getNowTime();
		sBuffer = String.format("%s 文件名:%s 函数名：%s %s\r\n", nowTime,fileName,funName,format);
		Is_testresult_exist();
		//Arrays.fill(szBuffer, (byte) 0);
		LoggerUtil.d("file_path:"+LOGFILE);
		if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
		{
			cls_show_msg1(2,"%s,line %d: 打开文件失败",TAG,Tools.getLineInfo());
		}
		else
		{
			while(true)
			{
				if(overFlag)
				{
					if(fileName.contains("SysTest"))//只有综合模块显示该条
						szBuffer.append(String.format("-------------------------%s :模块测试开始-----------------------------------\r\n", fileName));
					
					if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), szBuffer.toString().getBytes().length, 2)!= szBuffer.toString().getBytes().length)
					{
						cls_show_msg1(2,"%s, line %d: 写入文件失败", TAG,Tools.getLineInfo());
						break;
					}
					overFlag = false;
				}
				// 总是会包写入文件失败？？
				if((int) fileSystem.JDK_FsWrite(LOGFILE, sBuffer.getBytes(), sBuffer.getBytes().length, 2)!=sBuffer.getBytes().length)
				{
					cls_show_msg1(2,"%s, line %d: 写入文件失败...", TAG,Tools.getLineInfo());
					break;
				}
				break;
			}
		}
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull||GlobalVariable.gSequencePressFlag==true)
			return wait_key(3);
		else
			return wait_key(time);
	}
	
	/**
	 * 只具有写文件的操作
	 * @param time
	 * @param serial
	 * @param msg
	 * @param args
	 * @return
	 */
	public void cls_only_write_msg(String fileName,String funName,String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		String sBuffer = null,nowTime = null;
		
		/*process body*/
		String format = String.format(msg, args);
		cnt = format.length();
		
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg1(5, "%s,line %d:信息太长，点击否退出...", fileName,Tools.getLineInfo());
		}
		
		nowTime = getNowTime();
		sBuffer = String.format("%s 文件名:%s 函数名：%s %s\r\n", nowTime,fileName,funName,format);
		Is_testresult_exist();
		//Arrays.fill(szBuffer, (byte) 0);
		if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
		{
			cls_show_msg1(2,"%s,line %d: 打开文件失败",TAG,Tools.getLineInfo());
		}
		else
		{
			while(true)
			{
				if((int) fileSystem.JDK_FsWrite(LOGFILE, sBuffer.getBytes(), sBuffer.getBytes().length, 2)!=sBuffer.getBytes().length)
				{
					cls_show_msg1(2,"%s, line %d: 写入文件失败...", TAG,Tools.getLineInfo());
					break;
				}
				break;
			}
		}
	}
//	/**
//	 * 返回按键值 具有写文件的操作
//	 * @param time
//	 * @param serial
//	 * @param msg
//	 * @param args
//	 * @return
//	 */
//	public int cls_show_msg1(int time,int serial,String msg,Object ...args) 
//	{
//		/*private & local definition*/
//		int cnt = 0;
//		byte[] tips = new byte[MAXDISPBUFSIZE];
//		// 格式化字符串
//		String format = String.format(msg, args);
//		cnt = format.length();
//		StringBuffer szBuffer=new StringBuffer();
//		
//		/*process body*/
//		// 清屏操作？？
//		if(cnt>tips.length-1)
//		{
//			cls_show_msg1(2,"%s,line %d:信息太长，任意键退出...", TAG,Tools.getLineInfo());
//		}
//		else
//		{
//			handler.sendMessage(handler.obtainMessage(handlerMsg, String.format(Locale.CHINA, "%s", format)));
//		}
//		
//		// 结果字符串
//		szBuffer.append(String.format( "%d                                             %s\r\n",serial,format));
//		while(true)
//		{
//			if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
//			{
//				cls_show_msg1(2,"%s, line %d: 打开文件失败,请查看文件系统是否异常", TAG,Tools.getLineInfo());
//				break;
//			}
//			else
//			{
//			
//					if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), szBuffer.toString().getBytes().length, 2)!= szBuffer.toString().getBytes().length)
//					{
//						cls_show_msg1(2,"%s, line %d:写入文件失败...", TAG,Tools.getLineInfo());
//						break;
//					}
//				
//			}
//			break;
//		}
//		return wait_key(time);
//	}
	
	/**
	 * 返回按键值
	 * @param time 超时时间
	 * @param msg
	 * @param args
	 * @return
	 */
	public int cls_show_msg1(float time,TimeUnit timeUnit,String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		byte[] tips = new byte[MAXDISPBUFSIZE];
		// 格式化字符串
		String format = String.format(msg, args);
		cnt = format.length();
		
		LoggerUtil.d("cls_show_msg1:"+format);// 日志信息 by zhengxq 20170912
		/*process body*/
		// 清屏操作？？
		if(cnt>tips.length-1)
		{
			cls_show_msg1(2,"%s,line %d:信息太长，任意键退出...", TAG,Tools.getLineInfo());
		}
		else
		{
			handler.sendMessage(handler.obtainMessage(handlerMsg, String.format(Locale.CHINA, "%s", format)));
		}
		// 转化为s的单位再进入该方法 modify by zhengxq 20171106
		if(timeUnit==TimeUnit.MILLISECONDS)
			time = (float) (time/1000.0);
		return wait_key(time);
	}
	
	/**
	 * 返回按键值
	 * @param time 超时时间
	 * @param msg
	 * @param args
	 * @return
	 */
	public int cls_show_msg1(int time,String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		byte[] tips = new byte[MAXDISPBUFSIZE];
		// 格式化字符串
		String format = String.format(msg, args);
		cnt = format.length();
		
		LoggerUtil.d("cls_show_msg1:"+format);// 日志信息 by zhengxq 20170912
		/*process body*/
		// 清屏操作？？
		if(cnt>tips.length-1)
		{
			cls_show_msg1(2,"%s,line %d:信息太长，任意键退出...", TAG,Tools.getLineInfo());
		}
		else
		{
			handler.sendMessage(handler.obtainMessage(handlerMsg, String.format(Locale.CHINA, "%s", format)));
		}
		return wait_key((float) time);
	}
	
	
	public int cls_show_msg2(Float time,String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		byte[] tips = new byte[MAXDISPBUFSIZE];
		// 格式化字符串
		String format = String.format(msg, args);
		cnt = format.length();
		
		LoggerUtil.d("cls_show_msg1:"+format);// 日志信息 by zhengxq 20170912
		/*process body*/
		// 清屏操作？？
		if(cnt>tips.length-1)
		{
			cls_show_msg1(2,"%s,line %d:信息太长，任意键退出...", TAG,Tools.getLineInfo());
		}
		else
		{
			handler.sendMessage(handler.obtainMessage(handlerMsg, String.format(Locale.CHINA, "%s", format)));
		}
		return wait_key((float) time);
	}
	
	/**
	 * 长时间等待操作
	 * @param msg
	 * @param args
	 * @return
	 */
	public int cls_show_msg(String msg,Object ...args) 
	{
		/*private & local definition*/
		int cnt = 0;
		byte[] tips = new byte[MAXDISPBUFSIZE];
		// 格式化字符串
		String format = null;
		try
		{
			if(msg.contains("%s")||msg.contains("%d"))
				format = String.format(msg, args);
			else
				format = msg;// 当字符内容存在%号时，有格式化的问题，不支持使用string.format去格式化
		}catch(Exception e)
		{
			e.printStackTrace();
			LoggerUtil.d("002,字符串转换异常="+e.getMessage());
		}
		
		cnt = format.length();
//		LoggerUtil.d("cls_show_msg:"+format);// 日志信息 by zhengxq 20170912
		
		/*process body*/
		// 清屏操作？？
		if(cnt>tips.length-1)
		{
			cls_show_msg("%s,line %d:信息太长，任意键退出...", TAG,Tools.getLineInfo());
		}
		else
		{
//			LoggerUtil.d("002,即将显示内容");
			handler.sendMessage(handler.obtainMessage(handlerMsg, String.format(Locale.CHINA, "%s", format)));
		}
		if(GlobalVariable.gAutoFlag == AutoFlag.AutoFull||GlobalVariable.gSequencePressFlag==true)// 如果是连续压力或自动化测试修改为等待3s，以防界面卡住
			return wait_key(3);
		else
			return wait_key(0);
	}
	
	public void Is_testresult_exist() 
	{
		StringBuffer szBuffer=new StringBuffer();
		//String szBuffer = null;
		// 报告文件的存在性
		if(fileSystem.JDK_FsExist(LOGFILE)!= NDK_OK)
		{
			szBuffer.append(String.format(REPORTHEAD,getNowTime()));
			if(fileSystem.JDK_FsOpen(LOGFILE, "w")!=JDK_OK)
			{
				cls_show_msg1(2,"%s,line %d:打开文件失败",TAG,Tools.getLineInfo());
				return;
			}
			else
			{
				if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), szBuffer.toString().getBytes().length,0)!= szBuffer.toString().getBytes().length)
				{
					cls_show_msg1(2,"%s,line:%d:写入文件失败...", TAG,Tools.getLineInfo());
				}
				
			}
		}
		
	}
	
	
	/**
	 * 写入模块名
	 * author：zhengxq
	 */
	public void moduleName(String fileName) throws IOException
	{
		
		/*private & local definition*/
		//byte[] szBuffer = new byte[MAXDISPBUFSIZE+512];
		StringBuffer szBuffer=new StringBuffer();
		Is_testresult_exist();
	//	Arrays.fill(szBuffer, (byte) 0);
		if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
		{
			cls_show_msg1(2,"%s,line %d: 打开文件失败",TAG,Tools.getLineInfo());
		}
		else
		{
			szBuffer.append(String.format("%s -      %s -用例号|         预期结果      |    实际测试结果    |\r\n",fileName, fileName));
			if (fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(),szBuffer.toString().getBytes().length, 2) != szBuffer.toString().getBytes().length) 
			{
				cls_show_msg1(2, "%s, line %d: 写入文件失败", TAG,Tools.getLineInfo());
			}
		}
	}
	
	public boolean isOverFlag() 
	{
		return overFlag;
	}

	public void setOverFlag(boolean overFlag) 
	{
		this.overFlag = overFlag;
	}

	private String getNowTime()
	{
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		String str = time.format(new java.util.Date());
		return str;
	}
	
//	public int keyValue = 0;// 返回给NDK在java这边的按键值
	
	/**
	 * NDK接口
	 * @param msg
	 * @return
	 */
	public int cls_printf(byte[] msg)
	{
		/*private &local definition*/
		int cnt = 0;
		
		/*process body*/
		NDK_ScrClrs();
		String format = null;
		try {
			format = new String(msg, CODE_FORMAT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LoggerUtil.d(format);
		cnt = format.length();
		if(cnt>MAXDISPBUFSIZE-1)
		{
			Tools.reboot(BaseFragment.myactivity);
			return (-1);
		}
		else
		{
			pszBuffer.append(format);
//			LoggerUtil.d("cls_printf:"+format);// 日志信息 by zxq 20170912
			if(handler!=null){
				handler.sendMessage(handler.obtainMessage(handlerMsg, String.format(Locale.CHINA, "%s", format)));
			}else{
				NDK_ScrRefresh();
			}
		}
		return cnt;
	}
	
	/**
	 * 弹出显示对话框
	 * @param pMsg 提示信息
	 * @param cStyle btn_ok:确认，btn_cancel:取消
	 * @param iWaitTime
	 * @return btn_ok:选择了“确定”
	 * 		   btn_cancel:选择了“取消”
	 */
	public int ShowMessageBox(byte[] pMsg,byte cStyle,int iWaitTime)
	{
		/*private &local definition*/
		int nkeyIn = 0;
		StringBuffer endMsg = new StringBuffer();
		String tipMsg = null;
		
		/*process body*/
		
		// 默认编码为UTF-8
		try {
			tipMsg = new String(pMsg, CODE_FORMAT);
//			LoggerUtil.d("ShowMessageBox:"+tipMsg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		endMsg.append(tipMsg+"\n");
		
		if((BTN_OK&cStyle)!=0)
		{
			endMsg.append("确认：是\n");
		}
		if((BTN_CANCEL&cStyle)!=0)
		{
			endMsg.append("取消：否\n");
		}
		// 显示界面
		BaseFragment.uiHandle.sendMessage(BaseFragment.uiHandle.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, endMsg));
		while(true)
		{
			nkeyIn = wait_key(iWaitTime);
			if(((cStyle&BTN_OK)==BTN_OK&&nkeyIn==ENTER)||nkeyIn == 0)
			{
				nkeyIn = BTN_OK; 
				return BTN_OK;
			}
			if((cStyle&BTN_CANCEL)==BTN_CANCEL&&nkeyIn==ESC)
			{
				nkeyIn = BTN_CANCEL;
				return BTN_CANCEL;
			}
			if(cStyle==0)
			{
				return nkeyIn;
			}
		}
	}
	
	/**
	 * 用于NDK底层的测试
	 * @param initMsg
	 * @return
	 */
	public int cls_show_msg(byte[] initMsg,int auto_flag,int sequencePressFlag)
	{
		/*private & local definition*/
		int cnt = 0;
		byte[] tips = new byte[MAXDISPBUFSIZE];
		String msg = null;
		try {
			msg = new String(initMsg, CODE_FORMAT);
			LoggerUtil.d("cls_show_msg:"+msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		cnt = msg.length();
		/*process body*/
		if(cnt>tips.length-1)
		{
			cls_show_msg(String.format("%s,line %d:信息太长，任意键退出(重启)...", TAG,Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
		}
		else
		{
			// 这边的handler要使用静态
			BaseFragment.uiHandle.sendMessage(BaseFragment.uiHandle.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, msg));
		}
		if(auto_flag==1||sequencePressFlag==1)
			return wait_key(3);
		else
			return wait_key(0);
	}
	
	/**
	 * 用于NDK底层的测试
	 * @param time
	 * @param initMsg
	 * @return
	 */
	public int cls_show_msg1(int time,byte[] initMsg,int auto_flag,int sequencePressFlag) 
	{
		/*private &local definition*/
		int cnt = 0;
		String format = null;
		try 
		{
			format = new String(initMsg, CODE_FORMAT);
			LoggerUtil.d("cls_show_msg1:"+format);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		cnt = format.length();
		/*process body*/
		NDK_ScrClrs();
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg(String.format("%s,line %d:信息太长，任意键退出(重启)...", "cls_show_msg1",Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
			Tools.reboot(BaseFragment.myactivity);
		}
		else
		{
			pszBuffer.append(format);
			NDK_ScrRefresh();
		}
		return wait_key(time);
	}
	
	/**
	 * 用于NDK底层的测试
	 * @param sec 超时时间
	 * @return
	 */
	public int wait_key(float sec)
	{
		/*private &local definition*/
		long oldTime;
		float diff =0.0f,diffold = 0.0f;
		boolean beepflag = false;
		int nkey_in = 0;
		
		GlobalVariable.g_nkeyIn = 0;
		/*process body*/
		if(sec<=0)
		{
			while(GlobalVariable.g_nkeyIn==0)
			{
				SystemClock.sleep(100);
			}
			nkey_in = GlobalVariable.g_nkeyIn;
			return nkey_in;
				
		}
		else
		{
			oldTime = System.currentTimeMillis();
			while((diff=Tools.getStopTime(oldTime))<sec&&GlobalVariable.g_nkeyIn==0)
			{
				if(diffold!=diff)
				{
					diffold = diff;
				}
				if(diff%5==0)
				{
					if(beepflag==false)
					{
						beepflag = true;
					}
				}
				else
					beepflag = false;
			}
		}
		nkey_in = GlobalVariable.g_nkeyIn;
	
		return nkey_in;
	}
	
	/**
	 * NDK记录文件操作
	 * @param filename
	 * @param funname
	 * @param time
	 * @param msg
	 * @return
	 */
	public int cls_show_msg1_record(int time,byte[] msg,int auto_flag,int sequencePressFlag)
	{
		/*private & local definition*/
		int cnt;
		
		/*process body*/
		String format = null;
		try 
		{
			format = new String(msg, CODE_FORMAT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		cnt = format.length();
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg(String.format("%s,line %d:信息太长，任意键退出(重启)...", "cls_show_msg1",Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
			Tools.reboot(BaseFragment.myactivity);
		}
		else
		{
			pszBuffer.append(format);
			NDK_ScrRefresh();
		}
		
		// 构造结果字符串
		StringBuffer szBuffer = new StringBuffer();
		szBuffer.append(String.format("%s 文件名：%s 函数名：%s %s\r\n", getNowTime(),TAG,"cls_show_msg1_record",format));
//		LoggerUtil.d("cls_show_msg1_record:"+szBuffer.toString());// 日志信息 by zhengxq
		Is_testresult_exist();
		while(true)
		{
			if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
			{
				cls_show_msg1(5,String.format("%s, line %d: 打开文件失败,请查看文件系统是否异常", TAG,Tools.getLineInfo()));
				break;
			}
			else
			{
				if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), szBuffer.toString().getBytes().length, 2)!= szBuffer.toString().getBytes().length)
				{
					cls_show_msg1(2,String.format("%s, line %d:写入文件失败...", TAG,Tools.getLineInfo()));
					break;
				}
			}
			break;
		}
		return wait_key(time);	
	}
	
	/**
	 * NDK接口，显示操作
	 * @param msg
	 * @param auto_flag 自动化测试的标志位
	 * @param int g_SequencePressFlag = 0; //综合测试 连续压力的开关  0:不进行连续压力测试  1:进行连续压力测试
	 * @return
	 */
	public int cls_show_msg_record(byte[] msg,int auto_flag,int sequencePressFlag)
	{
		/*private &local definition*/
		int cnt = 0;
		/*process body*/
		NDK_ScrClrs();
		// 构造结果字符串
		StringBuffer szBuffer = new StringBuffer();
		String format = null;
		try 
		{
			format = new String(msg, CODE_FORMAT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LoggerUtil.d(format);
		cnt = format.length();
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg(String.format("%s,line %d:信息太长，任意键退出(重启)...", "cls_show_msg1",Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
			Tools.reboot(BaseFragment.myactivity);
		}
		else
		{
			pszBuffer.append(format);
			NDK_ScrRefresh();
		}
		szBuffer.append(String.format("%s 文件名：%s 函数名：%s %s\r\n", getNowTime(),TAG,"cls_show_msg1_record",format));
//		LoggerUtil.d("cls_show_msg_record:"+szBuffer.toString());// 日志信息 by zxq 20170912
		Is_testresult_exist();
		while(true)
		{
			if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
			{
				cls_show_msg1(5,String.format("%s,line %d: 打开文件失败,请查看文件系统是否异常", TAG,Tools.getLineInfo()));
				break;
			}
			else
			{
				if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), szBuffer.toString().getBytes().length, 2)!= szBuffer.toString().getBytes().length)
				{
					cls_show_msg1(2,String.format("%s, line %d:写入文件失败...", TAG,Tools.getLineInfo()));
					break;
				}
			}
			break;
		}
		if(auto_flag==1||sequencePressFlag==1)
			return wait_key(3);
		else
			return wait_key(0);
	}
	
	public String pszBufNDK=null;
	
	// 增加退格键的回退功能 modify by 20170810
	// 储存pszBuf中一开始的默认值
	public int lib_kbgetinput(byte[] pszBuf,int unMaxLen,int unWaittime)
	{
		int ret = -1;
		int len = 0;
		int endLen = 0;
		byte[] tempbuf=new byte[unMaxLen];
		
		// 备份数据
		System.arraycopy(pszBuf, 0, tempbuf, 0, pszBuf.length);
		pszBuf = new byte[unMaxLen];
		
		cls_printf(tempbuf);
		// 此时进行按键操作
		len = NDK_ReadData(unMaxLen, pszBuf,5);// 复制原先的字符串
		if(len ==0)
		{
			pszBuf = tempbuf;// 如果没有输入使用默认的，对应超时的情况
			return NDK_ERR;
		}
		else if(len ==1)
		{
			if(pszBuf[0]==0x0d||pszBuf[0]==0x1B)// 这里功能键只支持ESC和Enter键，其他功能键不做处理
			{
				pszBuf = tempbuf;
				return NDK_ERR;
			}
			ret = NDK_OK;
		}
		else
			ret = NDK_OK;
		
		for (int i = 0; i < len; i++) // 为避免PC键盘的功能键被读入后当成有效字符而进行取功能键处理
		{
			if(pszBuf[i]==0x0d||pszBuf[i]==0x0a||pszBuf[i]==0x1b)
				pszBuf[i]=0;
			else
			{
				endLen++;
			}
		}
		pszBufNDK = null;
		if(endLen!=0)
		{
			byte[] endBuf = new byte[endLen];
			System.arraycopy(pszBuf, 0, endBuf, 0, endLen);
			pszBufNDK = new String(endBuf);
		}
		return ret;
	}
	
	/**
	 * 返回读到的长度，读快选的用例号
	 * @param unLen
	 * @param pszOutBuf
	 * @param nTimeOutMs
	 */
	public int NDK_ReadData(int unLen,byte[] pszOutBuf,int sec)
	{
		int i,iTime=1;
		int ji_cnt=0;
		byte[] destBuf = new byte[100];
		
		
		do
		{
			i = wait_key(sec);// 返回的是按键值，一次按键只按一个值
			if(i>0)
			{
				if(ji_cnt>0&&i == 0x0A)//【退格】键回删一个键
				{
					pszOutBuf[ji_cnt-1] =0;
					destBuf[ji_cnt-1] = 0;
					ji_cnt = ji_cnt-1;
					cls_printf(destBuf);
					continue;
				}
				pszOutBuf[ji_cnt] = (byte) i;
				System.arraycopy(pszOutBuf, 0, destBuf, 0, ji_cnt+1);
				cls_printf(destBuf);// 显示操作
				ji_cnt = ji_cnt+1;
				unLen = unLen-1;
				// 如果返回的是【确定】【取消】立即退出返回按键个数
				if(i==0x0D||i==0x1B)
				{
					return ji_cnt-1;// 不要计算确认键以及取消键的值
				}
					
			}
			else
			{
				if(sec<=0)
					break;
				SystemClock.sleep(1000);
				iTime = iTime+1;
				if((++iTime)>sec)
					break;
			}
		}while((unLen>0));
		return ji_cnt;
	}
	
	public int lib_getkeycode(int sec)
	{
		/*private &local definition*/
		int nKeyin = 0;
		
		/*process body*/
		nKeyin = wait_key(sec);
		return nKeyin;
	}
	
	// JDK这边输入压力次数，休眠时间等 add by 20171213
	/**
	 * 输入次数/间隔时间/密钥索引
	 * @param timeoutSec 按键的超时时间
	 * @param defaultValue 默认的按键值
	 * @return 输入的按键值
	 */
	public int JDK_ReadData(float timeoutSec,int defaultValue,String...msg)
	{
		if(GlobalVariable.gAutoFlag  == AutoFlag.AutoFull)
		{
			return defaultValue;
		}
		List<Byte> keyCodeList = new ArrayList<Byte>();
		int keyCount=0,endKeyCode = 0;// 默认值为1
		long startTime;
		if(msg.length != 0){
			cls_printf((msg[0]+defaultValue+",要修改请按键(取消键可退出)").getBytes());
		} else
			cls_printf(("默认按键值："+defaultValue+",要修改请按键(取消键可退出)").getBytes());
		startTime = System.currentTimeMillis();
		while(Tools.getStopTime(startTime)<timeoutSec)
		{
			int value = wait_key(timeoutSec);
			switch (value) {
			case ESC:// 取消键
				break;
				
			case ENTER:// 确认键
				break;
				
			case BACKSPACE:// 退格键
				if(keyCount>=1)
				{
					keyCount--;// 按键个数减1
					keyCodeList.remove(keyCount);// 按键值移除一个
					endKeyCode = endKeyCode/10;
					cls_printf((""+endKeyCode).getBytes());
				}
				break;
				
			case KEY_UP:// 翻页处理要加上，一页显示10行即可
			case KEY_DOWN:
			case 0:// 无按键
				 break;
				
			default:// 数字键
				keyCodeList.add((byte) (value-'0'));
				keyCount++;
				endKeyCode = endKeyCode*10+keyCodeList.get(keyCount-1);
				cls_printf(("按键值："+endKeyCode).getBytes());
				break;
			}
			if(value==ENTER)
				break;
			if(value==ESC)
				return ESC;
		}
		if(endKeyCode==0)// 真的按键了就返回0
		{
			if(keyCount>0)
				return endKeyCode;
			else
				return defaultValue;// 默认值
		}
		return endKeyCode;
	}
	
	/**
	 * 输出到测试报告当中
	 * @param fmt
	 */
	public void send_result(int id,byte[] fmt,int auto_flag,int sequencePressFlag)
	{
		/*private &local definition*/
		int cnt = 0;
		String format = null;
		
		NDK_ScrClrs();
		try 
		{
			format = new String(fmt, CODE_FORMAT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LoggerUtil.d(format);
		cnt = format.length();
		if(cnt>MAXDISPBUFSIZE-1)
		{
			cls_show_msg(String.format("%s,line %d:信息太长，任意键退出...", TAG,Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
			Tools.reboot(BaseFragment.myactivity);
		}
		// 构造结果字符串以及写文件操作
		StringBuffer szBuffer = new StringBuffer();
		// 目前执行的是几号用例，需要从哪里获得
		szBuffer.append(String.format( "%d号用例：             %s\r\n",id,format));
		// 调试信息
//		LoggerUtil.d("send_result:"+szBuffer.toString());
		int len = szBuffer.toString().getBytes().length;
		while(true)
		{
			if(fileSystem.JDK_FsOpen(LOGFILE, "w")<0)
			{
				cls_show_msg1(2,String.format("%s, line %d: 打开文件失败,请查看文件系统是否异常\n", TAG,Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
				break;
			}
			else
			{
				if(fileSystem.JDK_FsWrite(LOGFILE, szBuffer.toString().getBytes(), len, 2)!= len)
				{
					cls_show_msg1(2,String.format("%s, line %d:写入文件失败...\n", TAG,Tools.getLineInfo()).getBytes(),auto_flag,sequencePressFlag);
					break;
				}
			}
			break;
		}	
		pszBuffer.append(szBuffer);
		LoggerUtil.d("send_result:"+pszBuffer.toString());
		NDK_ScrRefresh();
		SystemClock.sleep(2000);
	}
	
	static StringBuffer pszBuffer = new StringBuffer();
	
	/**
	 * 用于液晶清屏
	 * @return
	 */
	public int NDK_ScrClrs()
	{
		pszBuffer.delete(0, pszBuffer.length());
		return NDK_OK;
	}
	
	/**
	 * 用于液晶内容显示，不刷新
	 * @param fmt
	 * @return
	 */
	public int NDK_ScrPrintf(byte[] fmt)
	{
		String format = null;
		try 
		{
			format = new String(fmt, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		pszBuffer.append(format);
		BaseFragment.uiHandle.sendMessage(BaseFragment.uiHandle.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, pszBuffer));
		return NDK_OK;
	}
	
	/**
	 * 用于液晶屏幕刷新
	 * @return
	 */
	public int NDK_ScrRefresh()
	{
		BaseFragment.uiHandle.sendMessage(BaseFragment.uiHandle.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, pszBuffer));
		return NDK_OK;
	}
	
	/**
	 * 退出NDK测试
	 * @return
	 */
	public void exitNDK()      
	{
		BaseFragment.uiHandle.sendMessage(BaseFragment.uiHandle.obtainMessage(HandlerMsg.TEXTVIEW_SHOW_PUBLIC, "即将杀死进程退回到菜单界面"));
		SystemClock.sleep(2*1000);
		System.exit(0);
	}
	
	/**
	 *随机或固定密码键盘布局，密码键盘的位置显示在下半部分，修改为动态获取
	 * @param str
	 * @return
	 */
	public int ndk_touchscreen_getnum(int[] touchWidth,int[] touchHeight,int[] headValue,int[] bottomValue) 
	{	
		Log.v("Gui", "ndk_touchscreen_getnum");
	
		try {
			String touch = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
			LoggerUtil.d(TAG+",touchscreen_getnum:"+touch);
			int index = touch.indexOf('x');
			// 触屏值控制
			LoggerUtil.d(TAG+",touchHeight:"+touchHeight.length);
			touchHeight[0] = Integer.parseInt(touch.substring(0, index));
			touchWidth[0] = Integer.parseInt(touch.substring(index+1));
		} catch (Exception e) {
			e.printStackTrace();
			return NDK_ERR;
		}
		bottomValue[0] = GlobalVariable.TitleBarHeight;
		headValue[0] = GlobalVariable.StatusHeight;
		Log.v("Gui", "||"+headValue[0]+"|||"+bottomValue[0]);
		return SUCC;
	}
	
	

	/**
	 * testTool的入口
	 * @return 返回值
	 */
	public native int testMain();
	
}
