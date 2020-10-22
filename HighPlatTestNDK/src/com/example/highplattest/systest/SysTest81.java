package com.example.highplattest.systest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import android.newland.scan.ScanUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.NotifyEventListener;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest81.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20181221
 * directory 		: 
 * description 		: 农行客诉问题复现
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq			20181221		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest81 extends DefaultFragment
{
	private final String TESTITEM = "农行客诉问题复现";
	private final String TAG = SysTest81.class.getSimpleName();
	SecKcvInfo kcvInfo = new SecKcvInfo();
	ScanUtil softManager1;
	private Gui commGui;
	private Gui mRfidGui;
	private Gui mThkGui;
	private Gui mSecGui;
	private Gui mK21Gui;
	private Gui mFileGui;
	private int rfstatus = 0;
	private Object object = new Object();
	int mRfidCnt=0;
	int mThkCnt=0;
	int mFileCnt=0;
	int mSecCnt =0;
	private boolean gIsExist = false;
	private TextView mtvShow;/**公共界面显示*/
	private TextView mtvShow_rfid;/**Rfid模块显示*/
	private TextView mtvThk;/**THK88模块显示*/
	private TextView mtvSec;/**Sec模块显示*/
	private TextView mtvK21;/**K21的上下电模块显示*/
	private TextView mtvFile;/**文件模块显示*/
	
	public static final int TEXTVIEW_SHOW_K21 = 200;
	public static final int TEXTVIEW_SHOW_RFIDTASK = TEXTVIEW_SHOW_K21+1;
	public static final int TEXTVIEW_SHOW_THKTASK = TEXTVIEW_SHOW_K21+2;
	public static final int TEXTVIEW_SHOW_SecTASK = TEXTVIEW_SHOW_K21+3;
	public static final int TEXTVIEW_SHOW_BootTASK = TEXTVIEW_SHOW_K21+4;
	public static final int TEXTVIEW_SHOW_FILETASK = TEXTVIEW_SHOW_K21+5;
	
	Handler defineHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case TEXTVIEW_SHOW_K21:// 命令通道显示界面
				mtvShow.setText((CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_RFIDTASK:
				mtvShow_rfid.setText("Rfid通道："+(CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_THKTASK:
				mtvThk.setText("THK88通道："+(CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_SecTASK:
				mtvSec.setText("Sec通道："+(CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_BootTASK:
				mtvK21.setText("K21通道："+(CharSequence) msg.obj);
				break;
				
			case TEXTVIEW_SHOW_FILETASK:
				mtvFile.setText("文件通道："+(CharSequence) msg.obj);
				break;
				
			default:
				break;
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		myactivity =  (IntentActivity) getActivity();
		View view = inflater.inflate(R.layout.aysnctask_view, container, false);
		mtvShow = (TextView) view.findViewById(R.id.textView1);
		mtvShow_rfid = (TextView) view.findViewById(R.id.textView_rfid);
		mtvThk = (TextView) view.findViewById(R.id.textView_icc);
		mtvSec = (TextView) view.findViewById(R.id.textView_mag);
		mtvK21 = (TextView) view.findViewById(R.id.textView_prn);
		mtvFile = (TextView) view.findViewById(R.id.textView_sec);
		mtvShow_rfid.setVisibility(View.VISIBLE);
		mtvThk.setVisibility(View.VISIBLE);
		mtvSec.setVisibility(View.VISIBLE);
		mtvK21.setVisibility(View.VISIBLE);
		mtvFile.setVisibility(View.VISIBLE);
		view.findViewById(R.id.btn_key_0).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_1).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_2).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_3).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_4).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_5).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_6).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_7).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_8).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_9).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_point).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_on).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_under).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_esc).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_back).setOnClickListener(listener);
		view.findViewById(R.id.btn_key_enter2).setOnClickListener(listener);
		return view;
	}
	
	public void systest81() 
	{
		commGui = new Gui(myactivity, defineHandler,TEXTVIEW_SHOW_K21);
		mRfidGui = new Gui(myactivity, defineHandler, TEXTVIEW_SHOW_RFIDTASK);
		mThkGui = new Gui(myactivity, defineHandler, TEXTVIEW_SHOW_THKTASK);
		mSecGui = new Gui(myactivity, defineHandler, TEXTVIEW_SHOW_SecTASK);
		mK21Gui = new Gui(myactivity, defineHandler, TEXTVIEW_SHOW_BootTASK);
		mFileGui = new Gui(myactivity, defineHandler, TEXTVIEW_SHOW_FILETASK);
		softManager1 = new ScanUtil(myactivity);
		while(true)
		{
			int nKeyIn = commGui.cls_show_msg("%s\n0.%s并发测试", TESTITEM,TESTITEM);
			switch (nKeyIn) 
			{
			case '0':
				commGui.cls_printf("测试过程中去自检-密钥检测查看密钥文件是否损坏,损坏则视为测试不通过".getBytes());
				gIsExist=false;
//				mRfidGui.cls_show_msg("请先放置好射频卡,放置完毕任意键继续");
//				mRfidCnt = mRfidGui.JDK_ReadData(10, 100000, "设置射频压力次数,设置的次数至少要比默认次数大,默认:");
//				mFileCnt = mFileGui.JDK_ReadData(10, 10000, "设置文件压力次数,设置的次数至少要比默认次数大,默认:");
//				mThkCnt = mThkGui.JDK_ReadData(10, 100000, "设置THK88压力次数,设置的次数至少要比默认次数大,默认:");
//				new Thread(new THK88Run()).start();
//				new Thread(new RfidRun()).start();
//				new Thread(new FileRun()).start();
				new Thread(new SecRun()).start();
				new Thread(new K21PowerRun()).start();
				break;
				
			case ESC:
				intentSys();
				break;

			default:
				break;
			}
		}
	}
	
	class SecRun implements Runnable
	{

		@Override
		public void run() 
		{
			int cnt=0,succ=0,countErr=0;
			int iRet;
			// 安装密钥->校验KCV,如果出现校验失败的情况,退出全部的线程
			while(gIsExist==false)
			{
				cnt++;
				mSecGui.cls_printf(String.format("Sec第%d次测试,已成功%d次", cnt,succ).getBytes());
				// 安装密钥允许返回20次的-1311
				// DES 
				if((iRet=ndk_install_key(cnt,16, 10, ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg()))!=NDK_OK)
				{
					LoggerUtil.d("DES1_iRet:"+iRet);
					if(iRet==-1302||iRet==-1305)
					{
						gIsExist=true;
						mSecGui.cls_show_msg("sec测试失败,请去自检-密钥检测查看密钥文件是否损坏");
						return;
					}
				}
				else//校验DES的KCV
				{
					LoggerUtil.d("DES GETKCV");
					if((iRet=getKcv(cnt,16,ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(), 10))!=NDK_OK)
					{
						if(iRet==-1302||iRet==-1305)
						{
							gIsExist=true;
							mSecGui.cls_show_msg("sec测试失败,请去自检-密钥检测查看密钥文件是否损坏");
							return;
						}
					}
				}
				// SM4
				if((iRet=ndk_install_key(cnt,16, 55, ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()))!=NDK_OK)
				{
					if(iRet==-1302||iRet==-1305)
					{
						gIsExist=true;
						mSecGui.cls_show_msg("sec测试失败,请去自检-密钥检测查看密钥文件是否损坏");
						return;
					}
				}
				else//校验SM4的KCV
				{
					LoggerUtil.d("SM4 GETKCV");
					if((iRet=getKcv(cnt,16,ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(), 55))!=NDK_OK)
					{
						if(iRet==-1302||iRet==-1305)
						{
							gIsExist=true;
							mSecGui.cls_show_msg("sec测试失败,请去自检-密钥检测查看密钥文件是否损坏");
							return;
						}
					}
				}
				succ++;
//				// AES,先不测，KCV好像会变
//				if(ndk_install_key(24, 99, ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg())!=NDK_OK)
//				{
//					gIsExist = true;
//					return;
//				}
//				else// 检验AES的KCV
//				{
//					LoggerUtil.d("AES KCV");
//					getKcv(24,ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(), 99);
//				}
			}
			mSecGui.cls_show_msg("已测试了%d次", cnt);
		}
	}
	String[][] keyData = 
		{
			/**8字节的密钥*/
			{ "1111111111111111","82E13665","253C9D9D7C2FBBFA"},/**8字节的TMK*/
			{ "DBFE96D0A5F09D24","5B4C8BED"},/**8字节TAK明文4DE5E8B8A9DCDDF9*/
			{ "DBFE96D0A5F09D24","5B4C8BED"},/**8字节的TDK明文4DE5E8B8A9DCDDF9*/
			{ "563E884E285E1350","9DA493AA"},/**8字节的TPK明文1818181818181818*/
			/**16字节的密钥*/
			/**DBFE96D0A5F09D24   F7EAC58BC4EA2865 DES的KCV SM4的KCV*/
			{ "11111111111111112222222222222222","253C9D9D7C2FBBFA9BC9FB82A5925726","D2B91CC5","5E73DDAB"},/**16字节的TMK*/
			{ "43668564CE7F6198E2E2F4834C77E7F2","","65EBB214","D12A74E9"},/**16字节的TAK明文4DE5E8B8A9DCDDF91111111111111111*/
			{ "713E86C4BF6D84691C6E35A1DF8A5496","","1B619F0F","8D14E7A0"},/**16字节的TDK明文23232323232323231212121212121212*/
			{ "C42D7DD28A15554ADEF60B1F05E39846","","59559269","9CE71DD8"},/**16字节的TPK明文56565656565656567878787878787878*/
			/**24字节的密钥 AES的KCV*/
			{ "111111111111111122222222222222223333333333333333","253C9D9D7C2FBBFA9BC9FB82A59257264BF6E91B1E3A9D81","BA0BD2FD",},/**24字节的TMK*/
			{ "83A165365E31D6333BA9E4262634E75523F13706B5812370","","63E9D422"},/**24字节的TAK明文4DE5E8B8A9DCDDF911111111111111111259B7E1FEC34B9D*/
			{ "32AA073D515ABDFA0A532DCF34303C80E47CF7A72B8F095F","","50FD57E6"},/**24字节的TDK明文121212121212121218181818181818181919191919191919*/
			{ "B6621D1954036141D9950566F5FCEEFAE14647E8A135061A","","C3538AAC"},/**24字节的TPK明文161616161616161610101010101010101717171717171717*/
		};
	private final String TRANSMIT_KEY = "31313131313131313131313131313131";// 传输密钥
	
	/**
	 * algMode:
	 * 0:DES算法
	 * (1<<6):SM4算法
	 * :AES算法
	 * 
	 */
	private int ndk_install_key(int cnt,int keyLen,int keyIndex,int algMode)
	{			
		LoggerUtil.d("ndk_install_key:"+algMode);
//		int keyLen = mKeyLenIndex==0?8:mKeyLenIndex==4?16:24;
		int index = keyLen/8==1?0:keyLen/8==2?4:8;
		byte[] loadKey = new byte[keyLen];
//		gui.cls_printf(String.format("即将安装索引%d的主密钥、pin密钥、mac密钥、data密钥", keyIndex).getBytes());
		kcvInfo.nCheckMode = 0;
		int iRet=-1;
		int retValue=NDK_OK;
		Random random = new Random();
		if(algMode==10)//CBC模式忽略
		{
			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(1), (byte)1, (byte)keyIndex, keyLen, ISOUtils.hex2byte(keyData[index][1]), kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:CBC模式安装TMK密钥失败(%d)", Tools.getLineInfo(),iRet);
				retValue = iRet;
			}
			String iv = "1122334455667788";
			// TPK
			if((iRet=JniNdk.JNI_Sec_LoadKey_CBC((byte)1, (byte)2, (byte)keyIndex, (byte)keyIndex, ISOUtils.hex2byte(iv), ISOUtils.hex2byte(keyData[index+3][0]), keyLen, kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:CBC模式安装TPK密钥失败(%d)", Tools.getLineInfo(),iRet);
				retValue = iRet;
			}
			//TAK
			if((iRet=JniNdk.JNI_Sec_LoadKey_CBC((byte)1, (byte)3, (byte)keyIndex, (byte)keyIndex, ISOUtils.hex2byte(iv), ISOUtils.hex2byte(keyData[index+1][0]), keyLen, kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:CBC模式安装TAK密钥失败(%d)", Tools.getLineInfo(),iRet);
				retValue = iRet;
			}
			// TDK
			if((iRet=JniNdk.JNI_Sec_LoadKey_CBC((byte)1, (byte)4, (byte)keyIndex, (byte)keyIndex, ISOUtils.hex2byte(iv), ISOUtils.hex2byte(keyData[index+2][0]), keyLen,kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:CBC模式安装TDK密钥失败(%d)", Tools.getLineInfo(),iRet);
				retValue = iRet;
			}
		}
		else
		{
			// SM4和AES要先安装TLK
			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|algMode), (byte)0, (byte)1, keyLen, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d安装TLK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
				retValue = iRet;
				return retValue;
			}
			// 安装索引密钥全部修改为随机
			random.nextBytes(loadKey);
			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(0|algMode), (byte)(1|algMode), (byte)1, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d安装TMK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
				retValue = iRet;
				return retValue;
			}
			// TPK
			random.nextBytes(loadKey);
			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(2|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d安装TPK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
				retValue = iRet;
				return retValue;
			}
			// TAK
			random.nextBytes(loadKey);
			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(3|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d安装TAK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
				retValue = iRet;
				return retValue;
			}
			// TDK
			random.nextBytes(loadKey);
			if((iRet=JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(4|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, loadKey, kcvInfo))!=NDK_OK)
			{
				mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d安装TDK密钥失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
				retValue = iRet;
				return retValue;
			}
		}
		return retValue;
	}
	
	public int getKcv(int cnt,int keyLen,int algMode,int keyIndex)
	{
		kcvInfo.nCheckMode = 1;
		int iRet=-1;
		int index = keyLen/8==1?0:keyLen/8==2?4:8;
		int kcvIndex;
		if(algMode==0||algMode==(1<<7))
			kcvIndex=2;
		else
			kcvIndex=3;
		// 主密钥
		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?1:(1|algMode)), (byte)keyIndex, kcvInfo))!=NDK_OK)
		{
			mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d获取TMK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			gIsExist=true;
			return iRet;
		}
		// pin密钥
		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?2:(2|algMode)), (byte)keyIndex, kcvInfo))!=NDK_OK)
		{
			mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d获取TPK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			gIsExist=true;
			return iRet;
		}
		// mac密钥
		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?3:(3|algMode)), (byte)keyIndex, kcvInfo))!=NDK_OK)
		{
			mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d获取TAK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			gIsExist=true;
			return iRet;
		}
		// data密钥
		if((iRet=JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?4:(4|algMode)), (byte)keyIndex, kcvInfo))!=NDK_OK)
		{
			mSecGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:%d获取TDK的KCV失败(%d)", Tools.getLineInfo(),cnt,algMode,iRet);
//			gIsExist=true;
			return iRet;
		}
		return NDK_OK;
	}
	
	class K21PowerRun implements Runnable
	{

		@Override
		public void run() 
		{
			while(gIsExist==false)
			{
				mK21Gui.cls_printf("K21的boot测试".getBytes());
				try {
					LinuxCmd.execCmd("/sys/class/paymodule_k21/" , "echo 1 > /sys/class/paymodule_k21/bootup");
					Thread.sleep(10*1000);
					LinuxCmd.execCmd("/sys/class/paymodule_k21/" , "echo 0 > /sys/class/paymodule_k21/bootup");
					Thread.sleep(200);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					LoggerUtil.e("k21 boot failed");
					gIsExist=true;
					mK21Gui.cls_show_msg("已退出测试");
				}
			}
			// 测试后置会重新给K21上电
			try {
				LinuxCmd.execCmd("/sys/class/paymodule_k21/" , "echo 1 > /sys/class/paymodule_k21/bootup");
			} catch (IOException e) {
				e.printStackTrace();
			}
			mK21Gui.cls_printf("已退出boot测试".getBytes());

		}
		
	}
	
	class FileRun implements Runnable
	{

		@Override
		public void run() 
		{
			int iRet = -1,cnt=0,bak=0,succ=0,fd;
			String path = "/appfs/test";
			byte[] sBuf = new byte[1024];
			byte[] defineBuf = new byte[1024*5];
			byte[] memBuf = new byte[1024*5];
			byte[] sn_32 = new byte[32];
			if(GlobalVariable.gSequencePressFlag){
				bak=getCycleValue();
			}else
				bak = mFileCnt;
			cnt = bak;
			
			// 测试前置：写入5K的文件
			if((iRet = JniNdk.JNI_FsExist(path))==NDK_OK)
			{
				if((iRet = JniNdk.JNI_FsDel(path))!=NDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:删除%s文件失败(%d)", Tools.getLineInfo(),path,iRet);
					return;
				}
				
				if((iRet = JniNdk.JNI_FsExist(path))==NDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:%s文件仍存在(%d)", Tools.getLineInfo(),path,iRet);
					return;
				}
			}


			if((fd = JniNdk.JNI_FsOpen(path, "w"))<0)
			{
				mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,path,fd);
				return;
			}
			for(int i=0;i<5;i++)
			{
				Arrays.fill(sBuf, (byte)(i+0x30));
//				if((iRet = JniNdk.JNI_FsSeek(fd, 100/*i*1024*/, 3))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:Seek文件失败(%d)", Tools.getLineInfo(),iRet);
//					return;
//				}
				if((iRet = JniNdk.JNI_FsWrite(fd, sBuf, sBuf.length))!=1024)// 文件写操作是追加的方式 不需要seek
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:写文件失败(%d)", Tools.getLineInfo(),iRet);
					return;
				}
				System.arraycopy(sBuf, 0, defineBuf, i*1024, 1024);
			}
			
			if((iRet = JniNdk.JNI_FsClose(fd))!=NDK_OK)
			{
				mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:关闭文件失败(%d)", Tools.getLineInfo(),iRet);
				return;
			}
			
			while(cnt>0)
			{
				if(mFileGui.cls_printf(String.format("%s交叉测试,已执行%d次,成功%d次,[取消]退出测试",TESTITEM,bak-cnt,succ).getBytes())==ESC)
					break;
				cnt--;
				
				if((fd = JniNdk.JNI_FsOpen(path, "w"))<0)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,path,fd);
					continue;
				}
				// 当前位置
				int offset = 100;/*(int) (Math.random()*1024);*/
				if((iRet = JniNdk.JNI_FsSeek(fd, offset, 2))!=NDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:Seek文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				// 重新写100B的数据到特定位置
				byte[] tempW = new byte[100];
				for (int i = 0; i < tempW.length; i++) {
					tempW[i] = (byte) (Math.random()*256);
				}
				if((iRet = JniNdk.JNI_FsWrite(fd, tempW, tempW.length))!=100)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:写文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				System.arraycopy(tempW, 0, defineBuf, offset, 100);
//				if((iRet = JniNdk.JNI_FsSeek(fd, offset, 2))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:Seek文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
//					JniNdk.JNI_FsClose(fd);
//					continue;
//				}
//				Arrays.fill(tempW, (byte)0x49);
//				if((iRet = JniNdk.JNI_FsWrite(fd, tempW, tempW.length))!=100)
//				{
//					gui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:写文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
//					JniNdk.JNI_FsClose(fd);
//					continue;
//				}
				JniNdk.JNI_FsClose(fd);// 关闭写模式的文件
				
				if((fd = JniNdk.JNI_FsOpen(path, "r"))<0)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:打开%s文件失败(%d)", Tools.getLineInfo(),bak-cnt,path,iRet);
					continue;
				}
				// case1:thk88上电并读取SN号后进行文件的读操作
				if((iRet = softManager1.setThk88Power(1))!=SDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88上电失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				
				if((iRet = softManager1.getThk88ID(sn_32))!=SDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88读取SN号失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				mFileGui.cls_show_msg1(1, "THK88读取到的SN号为:%s", ISOUtils.hexString(sn_32));
				
				Arrays.fill(memBuf, (byte)0x00);

				if((iRet = JniNdk.JNI_FsRead(fd, memBuf, memBuf.length))!=memBuf.length)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:读文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				
				// 比较文件的内容
				if(Tools.memcmp(defineBuf, memBuf, 5*1024)==false)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:比较读写文件内容错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.ASCII2String(memBuf));
					continue;
				}
				// case2:thk88下电后并读取SN号进行文件的读操作
				if((iRet = softManager1.setThk88Power(0))!=SDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88上电失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				
				if((iRet = softManager1.getThk88ID(sn_32))!=SDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88读取SN号失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				mFileGui.cls_printf(String.format("THK88读取到的SN号为:%s", ISOUtils.hexString(sn_32)).getBytes());
				
				Arrays.fill(memBuf, (byte)0x00);
				if((iRet = JniNdk.JNI_FsSeek(fd, 0l, 1))!=NDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:Seek文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				if((iRet = JniNdk.JNI_FsRead(fd, memBuf, memBuf.length))!=memBuf.length)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:读文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					JniNdk.JNI_FsClose(fd);
					continue;
				}
				
				// 比较文件的内容
				if(Tools.memcmp(defineBuf, memBuf, 5*1024)==false)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:比较读写文件内容错误(%s)", Tools.getLineInfo(),bak-cnt,ISOUtils.ASCII2String(memBuf));
					continue;
				}
				
				// 测试后置
				if((iRet = JniNdk.JNI_FsClose(fd))!=NDK_OK)
				{
					mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:关闭文件失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				succ++;
			}
			mFileGui.cls_show_msg1_record(TAG,TESTITEM,g_time_0, "%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
		}
		
	}
	
	
	class THK88Run implements Runnable
	{

		@Override
		public void run() {
			int iRet,bak,cnt,succ=0;
			byte[] sn_32=new byte[32];
			if(GlobalVariable.gSequencePressFlag){
				bak=getCycleValue();
			}else
				bak = mThkCnt;
			cnt = bak;
			while(cnt>0&&gIsExist==false)
			{
				cnt--;
				mThkGui.cls_printf(String.format("THK88第%d次测试,已成功%d次",bak-cnt,succ).getBytes());
				if((iRet = softManager1.setThk88Power(1))!=SDK_OK)
				{
					mThkGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88上电失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				
				if((iRet = softManager1.getThk88ID(sn_32))!=SDK_OK)
				{
					mThkGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88读取SN号失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				
				// case2:thk88下电后并读取SN号进行文件的读操作
				if((iRet = softManager1.setThk88Power(0))!=SDK_OK)
				{
					mThkGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88上电失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				
				if((iRet = softManager1.getThk88ID(sn_32))!=SDK_OK)
				{
					mThkGui.cls_show_msg1_record(TAG,TESTITEM,g_keeptime, "line %d:第%d次:THK88读取SN号失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				succ++;
			}
			mThkGui.cls_show_msg1_record(TAG,TESTITEM,g_time_0, "THK88Run测试完成,已执行次数为%d,成功为%d次",bak-cnt,succ);
		}
	}
	
	class RfidRun implements Runnable
	{

		@Override
		public void run() 
		{
			int iRet,cnt,bak,succ=0;
			byte[] psPiccType = new byte[1];
			int[] pnDataLen = new int[1];
			byte[] psDataBuf = new byte[10];
			// M1卡参数
			byte[] psSakBuf = new byte[1];
			byte[] key = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
			byte[] out=new byte[256];
			if (GlobalVariable.gSequencePressFlag) {
				bak = getCycleValue();
			} else
				bak = mRfidCnt;
			cnt = bak;
			LoggerUtil.d("RfidRun:"+gIsExist);
			while (cnt > 0&&gIsExist==false) 
			{
				LoggerUtil.d("while RfidRun:"+gIsExist);
				cnt--;
				mRfidGui.cls_printf(String.format("Rfid3.0第%d次测试,已成功%d次",bak-cnt,succ).getBytes());
				// 注册事件机制
				JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
				if((iRet = JniNdk.JNI_Rfid_Init(null))!=0)
				{
					mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:射频模块初始化失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				// 只考虑A和M1卡
				if((iRet = JniNdk.JNI_Rfid_PiccType((byte) 0xCC))!=NDK_OK)
				{
					mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:射频模块设置寻卡策略失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
					
				if((iRet = JniNdk.JNI_Rfid_M1Request((byte)1, pnDataLen, psDataBuf))!=NDK_OK)
				{
					mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:M1Request失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
					
				if((iRet = JniNdk.JNI_Rfid_M1Anti(pnDataLen, psDataBuf))!=NDK_OK)
				{
					mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:M1Anti失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
					
				if((iRet = JniNdk.JNI_Rfid_M1Select(pnDataLen[0], psDataBuf, psSakBuf))!=NDK_OK)
				{
					mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:M1Select失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
					continue;
				}
				if((psSakBuf[0]&0x20)==0x20)//A卡
				{
					if((iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte)10))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					if((iRet = JniNdk.JNI_Rfid_CloseRf())!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "rfidRead", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					// 注册事件机制
					if((iRet = JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), TIMEOUT_REGISTER, eventListener))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:注册射频事件机制失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
						continue;
					}
					if((iRet = JniNdk.JNI_Rfid_PiccType((byte) 0xCC))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					if(rfstatus!=1)
					{
						synchronized (object) {
							try {
								object.wait(30*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					// 使用事件机制的方式
//					if((iRet = JniNdk.JNI_Rfid_PiccDetect(psPiccType))!=NDK_OK)
//					{
//						rfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iRet);
//						continue;
//					}
					if((iRet = JniNdk.JNI_Rfid_Init(null))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					if((iRet = JniNdk.JNI_Rfid_OpenRf())!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					if((iRet = JniNdk.JNI_Rfid_PiccActivate(psPiccType, pnDataLen, psDataBuf))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					if((iRet = JniNdk.JNI_Rfid_PiccApdu(req.length, req, pnDataLen, psDataBuf))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:CPU卡APDU失败（%d）", Tools.getLineInfo(),bak-cnt,iRet);
						continue;
					}
					if((iRet = JniNdk.JNI_Rfid_PiccDeactivate((byte)10))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:%s测试失败(%d)", Tools.getLineInfo(),bak-cnt,TESTITEM,iRet);
						continue;
					}
					if(pnDataLen[0]==10)
					{
						// CPU卡操作
						mRfidGui.cls_printf("CPU卡上电成功+取随机数操作成功".getBytes());
					}
					else
					{
						mRfidGui.cls_printf("CPU卡上电成功,不支持取随机数".getBytes());
					}
					// 注册事件机制
					if((iRet = JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue()))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:注销射频事件机制失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
						continue;
					}
					succ++;
				}
				else// M1卡
				{
					if((iRet = JniNdk.JNI_Rfid_M1ExternalAuthen(pnDataLen[0], psDataBuf, (byte)0x61, key, (byte)0x01))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:M1卡外部认证失败(%d)", Tools.getLineInfo(),bak-cnt,iRet);
						continue;
					}
							
					// 之前认证的块为01，应该01-04块都可进行读写操作
					if((iRet = JniNdk.JNI_Rfid_M1Read((byte)0x01, pnDataLen, out))!=NDK_OK)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:M1卡块读失败(%d)",Tools.getLineInfo(),bak-cnt,iRet);
						continue;
					}
					LoggerUtil.d("out:"+ISOUtils.hexString(out, 16));
					if(Tools.memcmp(DATA16, out, pnDataLen[0])==false)// 首次
						System.arraycopy(out, 0, DATA16, 0, pnDataLen[0]);
					else if(Tools.memcmp(DATA16, out, pnDataLen[0])==false)
					{
						mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "line %d:第%d次:M1卡数据校验失败(%d)",Tools.getLineInfo(),bak-cnt,pnDataLen[0]);
						continue;
					}
					{
						mRfidGui.cls_printf("M1卡寻卡+读写操作成功,任意键继续测试".getBytes());
					}
					succ++;
				}
			}
			mRfidGui.cls_show_msg1_record(TAG, "RfidRun", g_keeptime, "Rfid3.0共测试%d次,成功%d次",bak-cnt,succ);
		}
	}

	NotifyEventListener eventListener = new NotifyEventListener() 
	{

		@Override
		public int notifyEvent(int eventNum, int msgLen, byte[] ms) 
		{
			if (eventNum == EM_SYS_EVENT.SYS_EVENT_RFID.getValue()) 
			{
				rfstatus = 1;
				LoggerUtil.e("--------rfstatus=1");
				synchronized (object) {
					object.notify();
				}
			}
			return 0;
		}
	};
}
