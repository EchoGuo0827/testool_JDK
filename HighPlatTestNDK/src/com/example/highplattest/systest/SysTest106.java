package com.example.highplattest.systest;

import java.util.Arrays;
import java.util.Random;
import android.newland.NLUART3Manager;
import android.newland.NlManager;
import android.newland.content.NlContext;
import android.util.Log;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_ALG;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
import com.newland.ndk.SecRsaKey;
import com.newland.uartport.Node;
import com.newland.uartport.UartPort;
import com.example.highplattest.main.tools.BCDUtil;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest106.java
 * Author 			: chending
 * version 			: 
 * DATE 			: 20191220
 * directory 		: 
 * description 		: 外接SP100密码键盘测试(海外版)X5支持
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  chending		   		20191220	 	created
 *					变更说明				变更时间			变更人
 *				脱机Pin案例新增USB通讯方式。去除明文安装SP100TMK密钥。改为测试人员自行安装NLP文件				20200805		陈丁
 *				新增小屏SP100分支			20200922			陈丁
 *				修改PINPAD通讯方式实现		20200922			陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest106 extends DefaultFragment {
	//test
	//0379会显示
	private final String TAG = SysTest106.class.getSimpleName();
	private final String TESTITEM = " 外接SP100密码键盘测试(海外版_X5支持)";
	JniNdk jniNdk;
	private Gui gui;
	private NLUART3Manager uart3Manager=null;
//	private NlManager nlManager = null;
	int comValue;
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	String[] comName = {"PINPAD","RS232"};
	SecRsaKey secRsaKey;
	String[] para={"8N1NB","8N1NN"};// para[0]:阻塞      para[1]:非阻塞
	private final int DEFAULT_CNT_VLE = 50;
	private boolean isInit=false;
	private final String TRANSMIT_KEY = "13131313131313131313131313131313";// 传输密钥 16个0X31
//	private final String TRANSMIT_KEY = "131313131313131313131313131313131313131313131313";// 传输密钥 24个0X13
	byte[] retContent = new byte[128];
	private static final byte[] STX = new byte[] { 0x02 };  //正文开头 0X02
	private static final byte[] ETX = new byte[] { 0x03 }; //结束符 0X03
	private static final byte[] RTX = new byte[] { 0x06 }; //应答帧 0X06
	private static final byte[] SEPAR = new byte[] { 0x2F }; //结束符 0X2F  固定帧0X2F  在指令类型之后 。数据帧之前
 	private static final int LEN_STX = STX.length;
	private static final int LEN_ETX = ETX.length;
	private static final int LEN_SEPAR = SEPAR.length;
	private static final int LEN_LRC = 1;
	private String SP100_ERR="47";
	private  byte[] Clearrecv = new byte[1024];  
	int ret = -1;
	int rettest;
	private SecKcvInfo kcvInfo = new SecKcvInfo();
	private byte[] testr=new byte[1024];
	private byte[] recvBuf;
	private String resString;
	byte[] ICbuf=new byte[256];
	int[] protocol = new int[2];
	byte[]PINBlock_des=new byte[8];   //指令返回数据的Pinblock
	byte[]PINBlock_aes=new byte[16];   //指令返回数据的Pinblock
	byte[]DES_moudel1_lenth=new byte[1];
	byte[]AES_moudel1_lenth=new byte[1];
	 byte[]panbyte_des=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
	 byte[]panbyte_aes=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
	byte[]DES_Tpk=  new byte[]{(byte) 0xF2,0x45,(byte) 0xB3,(byte) 0x8E,(byte) 0xA3,0x44,(byte) 0xF1,0x25,(byte) 0xF2,0x45,(byte) 0xB3,(byte) 0x8E,(byte) 0xA3,0x44,(byte) 0xF1,0x25};
	byte[]DES_Tpk_24=  new byte[]{(byte) 0xF2,0x45,(byte) 0xB3,(byte) 0x8E,(byte) 0xA3,0x44,(byte) 0xF1,0x25,(byte) 0xF2,0x45,(byte) 0xB3,(byte) 0x8E,(byte) 0xA3,0x44,(byte) 0xF1,0x25,(byte) 0xF2,0x45,(byte) 0xB3,(byte) 0x8E,(byte) 0xA3,0x44,(byte) 0xF1,0x25};
	//	2A 1E C6 B2 A3 60 14 D0 E0 6F 84 33 CB A1 75 39 2A 1E C6 B2 A3 60 14 D0 E0 6F 84 33 CB A1 75 39 
//	byte[]AES_Tpk=  new byte[]{(byte)0x2A,(byte) 0x1E,(byte) 0xC6,(byte) 0xB2,(byte) 0xA3, 0x60,0x14,(byte) 0xD0,(byte) 0xE0,0x6F,(byte) 0x84,0x33,(byte) 0xCB,(byte) 0xA1, 0x75,0x39,(byte)0x2A,(byte) 0x1E,(byte) 0xC6,(byte) 0xB2,(byte) 0xA3, 0x60,0x14,(byte) 0xD0,(byte) 0xE0,0x6F,(byte) 0x84,0x33,(byte) 0xCB,(byte) 0xA1, 0x75,0x39};
	byte[]AES_Tpk=new byte[]{0x2A,0x1E,(byte) 0xC6,(byte) 0xB2,(byte) 0xA3,0x60,0x14,(byte) 0xD0,(byte) 0xE0,0x6F,(byte) 0x84,0x33,(byte) 0xCB,(byte) 0xA1,0x75,0x39,0x2A,0x1E,(byte) 0xC6,(byte) 0xB2,(byte) 0xA3,0x60,0x14,(byte) 0xD0,(byte) 0xE0,0x6F,(byte) 0x84,0x33,(byte) 0xCB,(byte) 0xA1,0x75,0x39 };
	private byte[]psIccRespOut=new byte[100];
	byte[]ICC_Out=new byte[]{(byte) 0x90,0x00};
	 //RSA数据
	private byte[] RSAKEY=new byte[]{
			   (byte) 0x96, (byte) 0x92, (byte) 0xF2, (byte) 0xDC, 0x0D, (byte) 0xFE, (byte) 0xA1, 0x34, (byte) 0xF6, (byte) 0xD5, (byte) 0xDA, (byte) 0xF7, 0x56, 0x34, (byte) 0xCA, (byte) 0xEC,
				(byte) 0xC8, 0x55, (byte) 0xEC, 0x77, 0x4F, 0x4B, (byte) 0xFE, 0x6F, (byte) 0x90, (byte) 0xDD, (byte) 0xD4, 0x32, (byte) 0xB5, (byte) 0xDB, 0x1C, (byte) 0xDC, (byte) 0xFB, (byte) 0xAC, (byte) 0x96,
				(byte) 0x98, 0x02, (byte) 0xC6, 0x4A, 0x60, 0x69, (byte) 0xFD, 0x26, 0x7C, 0x41, (byte) 0xC1, (byte) 0xAC, (byte) 0x82, (byte) 0xE3, (byte) 0x8E, (byte) 0xA2, 0x54, (byte) 0xF4, (byte) 0xAA, 
				0x4B, (byte) 0xD0, (byte) 0x9B, 0x04, 0x51, 0x6C, 0x19, (byte) 0xE1, (byte) 0x8A, (byte) 0xC5
	};
	private int USBCOMNODE=-1;
	String text="null";
	public void systest106()
	{
		jniNdk=new JniNdk();
		secRsaKey=new SecRsaKey();
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gSequencePressFlag)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
//		uart3Manager=(NLUART3Manager) myactivity.getSystemService(NlContext.K21_SERVICE);
//		nlManager = (NlManager) myactivity.getSystemService(NlContext.K21_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("外接SP100密码键盘测试\n0.配置\n1.脱机Ping测试(DES)\n2.脱机Ping测试(AES)\n");
			switch (returnValue) 
			{
				case '0':
					int value1=gui.cls_show_msg("配置\n0.RS232\n1.PINPAD\n2.USB");
					switch (value1) {
					case '0':
						comValue=1;
						if(GlobalVariable.currentPlatform==Model_Type.X5){
							isNewRs232=true;
						}
						conf_test_bps();
						break;
					case '1':
						comValue=0;
						conf_test_bps();
						break;
					case '2':
						comValue=2;
						conf_test_bps();
						break;
						
					default:
						break;
					}
					boolean isbig=true;
					if ((gui.cls_show_msg("当前测试sp100是否为大屏？是【确定】否【其他】"))==ENTER) {
						gui.cls_show_msg1(1, "当前选择大屏");
					}else {
						gui.cls_show_msg1(1, "当前选择小屏");
						isbig=false;
					}
					if (isbig) {
						text="密码键盘第一行应显示A,第二行显示为空,第三行显示为please enter,第四行显示为BBBBB";
					}else {
						text="密码键盘第一行应显示A,第二行显示为空";
					}
					break;
				case ESC:
					intentSys();
					return;
					
				case '1':
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						return;
					}
					DES_initJudge();
					break;
				case '2':
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						return;
					}
					AES_initJudge();
					break;
				case '3':
//					pctest();
					break;
				case '4':
//					NDKtest();
					break;
				case '5':
					gui.cls_show_msg("确认要清除sp100密钥----按任意键清除");
					test();
					break;
				default:
					Log.d("eric_chen", "串口被关闭。。。。");
					portClose();
					break;
			}
		}
	}

	private void test() {
		// TODO Auto-generated method stub
		//获取版本指令
		if(!((resString=test_sp100_cmd(new byte[]{0x36,0x30},null)).equals("00"))){
		gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:清空sp100密钥(%s)", Tools.getLineInfo(), resString);
			return;
	}
		gui.cls_show_msg("清除成功---按任意键");
	}

	//AES算法模式验证
	private void AES_initJudge() {
		// TODO Auto-generated method stub
		PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE));
		int cnt=packet.getLifecycle();
		int succ=0;
		int bak=packet.getLifecycle();
		int randNumber=0;
		int ret = -1;
		Arrays.fill(ICbuf, (byte)0);
		byte[]ICCtem=new byte[2];
		gui.cls_show_msg1(2, "正在安装上位机密钥");
		
		//测试前置 擦除所有密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		kcvInfo.nCheckMode=0;
		//AES
		int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
		//安装32个0x32的 TLK  AES   索引01
		if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|algMode[2]), (byte)0, (byte)1, 32, ISOUtils.hex2byte("3232323232323232323232323232323232323232323232323232323232323232"), kcvInfo))!=NDK_OK){
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
		}
		
		//安装TMK  索引03 密文安装   AES  32个0x32对32个0x14加密                                           																	F4 F3 4A 43 3F C9 F6 5A 60 3E E4 D8 3F 48 18 39 F4 F3 4A 43 3F C9 F6 5A 60 3E E4 D8 3F 48 18 39
		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)3, 32, ISOUtils.hex2byte("F4F34A433FC9F65A603EE4D83F481839F4F34A433FC9F65A603EE4D83F481839"), kcvInfo)!=NDK_OK){
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
		}

		//安装TPK  索引05 密文安装  AES 32个0x14对32个0x39加密                                     																							2A1EC6B2A36014D0E06F8433CBA175392A1EC6B2A36014D0E06F8433CBA17539
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[2]), (byte)(2|algMode[2]), (byte)3, (byte)5, 32, ISOUtils.hex2byte("2A1EC6B2A36014D0E06F8433CBA175392A1EC6B2A36014D0E06F8433CBA17539"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1(2, "安装上位机密钥完成");
		
		
		//10位以上的密码会返回错误
		String[] pinNumber={"1234","12345","123456","1234567","12345678","123456789","1234567890","12345678901","123456789012"};
		gui.cls_show_msg("请确保POS与SP100密码键盘已连接!且SP100已安装好NLP文件。任意键继续");
		
		//获取版本指令
		if(!((resString=test_sp100_cmd(new byte[]{0x32,0x30},null)).equals("00"))){
		gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:获取版本号指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
	}
		
		/**
		 * 由于多数版本不支持明文安装TMK，故屏蔽安装。改成NLP文件，测试前置时由测试人员自行下载
		 */
//		//TMK密钥为32个0x14   索引03  算法AES    非标准版无法明文安装
//		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥。。。");
//		if(!((resString=test_sp100_cmd(new byte[]{0x35,0x30},new byte[]{0x00,0x01,0x20,0x03,0x03,0x00,0x20, 0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14,0x14})).equals("00"))){
//			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:安装sp100密钥指令执行失败(%s)", Tools.getLineInfo(), resString);
//			return;
//		}
		
	//发送指令安装sp100密钥     	//32个0x14对32个0x39进行AES加密得到的数据为2A 1E C6 B2 A3 60 14 D0 E0 6F 84 33 CB A1 75 39 2A 1E C6 B2 A3 60 14 D0 E0 6F 84 33 CB A1 75 39   索引为05
		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥（AES）");
		if(!((resString=test_sp100_cmd(new byte[]{0x35,0x30},ISOUtils.hex2byte("030120000500202A1EC6B2A36014D0E06F8433CBA175392A1EC6B2A36014D0E06F8433CBA17539"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:安装sp100密钥指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
		
		while(cnt>0){

			if(gui.cls_show_msg1(2,"压力测试中...\n还剩%d次（已成功%d次）,[取消]退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			//测试前置：下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			//case1 AES算法验证,直接使用现有的pin密钥模式(模式0),密文(01)pin密钥
			//设置脱机pin指令    pan数据"62258859161631570"
			Random rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----AES算法验证,直接使用现有的pin密钥模式(模式0),密文(01),TPK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0005011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, AES_Tpk, panbyte_aes, PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case1通过---");
			//case2 AES算法验证,直接使用现有的pin密钥模式(模式0),密文(01)TMK密钥
			 rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----AES算法验证,直接使用现有的pin密钥模式(模式0),密文(01),TMK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0005011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, 32, AES_Tpk, panbyte_aes, PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case2通过---");
			//case3 AES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)TMK密钥
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----AES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0005011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, 32, AES_Tpk, panbyte_des, PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case3通过---");
			//case4 AES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)TPK密钥
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----AES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0005011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, AES_Tpk, panbyte_des, PINBlock_aes.length, PINBlock_aes,secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case4通过---");
			//模式1只能用上位机TMK来解密
//			//case5 AES算法验证,使用SP100返回的随机密钥(模式1),明文(00)TPK密钥    
//			randNumber = rand.nextInt(pinNumber.length);
//			gui.cls_show_msg1(2,"脱机ping案例测试----使用SP100返回的随机密钥(模式1),明文(00)");
//			gui.cls_show_msg1(1,"请在密码键盘上输入%s,密码键盘第一行应显示A,第二行显示为空,第三行显示为please enter,第四行显示为BBBBB",pinNumber[randNumber]);
//			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0105011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C0379"))).equals("00"))){
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
//				return;
//			}
//			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
//			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
//			
//			
//			//IC卡上电
//			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//			}
//			int len=ISOUtils.hexInt(recvBuf, 18, 1);
//			byte[]aes_pinblock=new byte[len];
//			System.arraycopy(recvBuf, 19,aes_pinblock , 0, aes_pinblock.length);
//			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, aes_pinblock, panbyte_des, PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//				return;
//			}
//			//比对IC卡返回值
//			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
//			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
//			if (!(Arrays.equals(ICCtem, ICC_Out))) {
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
//				return;
//			}
			//case6 AES算法验证,使用SP100返回的随机密钥(模式1),明文(00)TMK密钥
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----使用SP100返回的随机密钥(模式1),明文(00)");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0103011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
				return;
			}
			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			
			
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			int len=ISOUtils.hexInt(recvBuf, 26, 1);
			Log.d("eric_chen", "len==="+len);
			byte[]	aes_pinblock=new byte[len];
			System.arraycopy(recvBuf, 27,aes_pinblock , 0, aes_pinblock.length);
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, 32, aes_pinblock, panbyte_des, PINBlock_aes.length, PINBlock_aes,secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case6通过---");
//			//case7 AES算法验证,使用SP100返回的随机密钥(模式1),密文(01)TPK密钥
//			randNumber = rand.nextInt(pinNumber.length);
//			gui.cls_show_msg1(2,"脱机ping案例测试----使用SP100返回的随机密钥(模式1),密文(01");
//			gui.cls_show_msg1(1,"请在密码键盘上输入%s,密码键盘第一行应显示A,第二行显示为空,第三行显示为please enter,第四行显示为BBBBB",pinNumber[randNumber]);
//			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0105011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C0379"))).equals("00"))){
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
//				return;
//			}
//			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
//			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
//			
//			
//			//IC卡上电
//			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//			}
//			 len=ISOUtils.hexInt(recvBuf, 18, 1);
//			aes_pinblock=new byte[len];
//			System.arraycopy(recvBuf, 19,aes_pinblock , 0, aes_pinblock.length);
//			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, aes_pinblock, panbyte_des, PINBlock_aes.length, PINBlock_aes,secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x01))!=NDK_OK){	
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//				return;
//			}
//			//比对IC卡返回值
//			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
//			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
//			if (!(Arrays.equals(ICCtem, ICC_Out))) {
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
//				return;
//			}
			//case8 AES算法验证,使用SP100返回的随机密钥(模式1),密文(01)TMK密钥
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----使用SP100返回的随机密钥(模式1),密文(01");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0103011136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
				return;
			}
			System.arraycopy(recvBuf, 10,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			
			
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			 len=ISOUtils.hexInt(recvBuf, 26, 1);
			aes_pinblock=new byte[len];
			System.arraycopy(recvBuf, 27,aes_pinblock , 0, aes_pinblock.length);
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, 32, aes_pinblock, panbyte_des, PINBlock_aes.length, PINBlock_aes,secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x01))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case8通过---");
			succ++;
		}
		gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"测试通过,总次数%d次，成功%d次",bak,succ);
	}
	private void conf_test_bps() {
		// TODO Auto-generated method stub
		// 进行波特率的选择
		gui.cls_show_msg("请确保POS与SP100密码键盘已连接(目前只支持波特率设置为115200)!任意键继续");
		int nkeyIn = gui.cls_show_msg("波特率选择\n0.9600\n1.19200\n2.38400\n3.57600\n4.115200\n");
		int[] bps = {9600, 19200, 38400, 57600, 115200 };
		switch (nkeyIn) 
		{
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
			BpsBean.bpsValue = bps[nkeyIn-'0'];
			break;
		default:
			gui.cls_show_msg1(2, "输入有误,请重新配置!");
			return;
		}
		//初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "conf_test_bps", g_keeptime,"line %d:串口初始化失败(%d)", Tools.getLineInfo(),ret);
			portClose();
			return;
		}
		gui.cls_show_msg1(3, "设置波特率成功。设置为%d.", BpsBean.bpsValue);
//		//测试前置 擦除所有密钥
//		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
//		//DES
//		//安装24个0x31的 TLK  DES 01
//		kcvInfo.nCheckMode=0;
//		if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 24, ISOUtils.hex2byte("313131313131313131313131313131313131313131313131"), kcvInfo)!=NDK_OK) {
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//
//		//安装TMK  索引02 密文安装   DES  24个0x31对24个0x13加密																																								DC 2C C5 54 AF F5 0A 25 DC 2C C5 54 AF F5 0A 25 DC 2C C5 54 AF F5 0A 25
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)2, 24, ISOUtils.hex2byte("DC2CC554AFF50A25DC2CC554AFF50A25DC2CC554AFF50A25"), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
//
//		//安装TPK  索引04 密文安装  DES 24个0x13对24个0x38加密 																																																															F2 45 B3 8E A3 44 F1 25 F2 45 B3 8E A3 44 F1 25 F2 45 B3 8E A3 44 F1 25 
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)2, (byte)4, 24, ISOUtils.hex2byte("F245B38EA344F125F245B38EA344F125F245B38EA344F125"), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
//		//AES
//		int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
//		//安装32个0x32的 TLK  AES   索引01
//		if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|128), (byte)0, (byte)1, 32, ISOUtils.hex2byte("3232323232323232323232323232323232323232323232323232323232323232"), kcvInfo))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//		
//		//安装TMK  索引03 密文安装   AES  32个0x32对32个0x14加密                                           																	F4 F3 4A 43 3F C9 F6 5A 60 3E E4 D8 3F 48 18 39 F4 F3 4A 43 3F C9 F6 5A 60 3E E4 D8 3F 48 18 39
//		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)3, 32, ISOUtils.hex2byte("F4F34A433FC9F65A603EE4D83F481839F4F34A433FC9F65A603EE4D83F481839"), kcvInfo)!=NDK_OK){
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//
//		//安装TPK  索引05 密文安装  AES 32个0x14对32个0x39加密                                     																							2A1EC6B2A36014D0E06F8433CBA175392A1EC6B2A36014D0E06F8433CBA17539
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[2]), (byte)(2|algMode[2]), (byte)3, (byte)5, 32, ISOUtils.hex2byte("2A1EC6B2A36014D0E06F8433CBA175392A1EC6B2A36014D0E06F8433CBA17539"), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
//		gui.cls_show_msg1(3, "上位机安装密钥完毕。");
		isInit = true;
	}
	//DES算法模式验证
	private void DES_initJudge() {
		// TODO Auto-generated method stub
		PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE));
		int cnt=packet.getLifecycle();
		int succ=0;
		int bak=packet.getLifecycle();
		int randNumber=0;
		StringBuffer str = new StringBuffer();
		byte[] cmdData; 
		int codeLen = 0;
		int ret = -1;
		Arrays.fill(ICbuf, (byte)0);
		byte[]ICCtem=new byte[2];
		
		gui.cls_show_msg1(1, "正在安装上位机密钥");
		//DES
		//测试前置 擦除所有密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//安装24个0x31的 TLK  DES 01
		kcvInfo.nCheckMode=0;
		if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 24, ISOUtils.hex2byte("313131313131313131313131313131313131313131313131"), kcvInfo)!=NDK_OK) {
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
		}

		//安装TMK  索引02 密文安装   DES  24个0x31对24个0x13加密																																								DC 2C C5 54 AF F5 0A 25 DC 2C C5 54 AF F5 0A 25 DC 2C C5 54 AF F5 0A 25
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)2, 24, ISOUtils.hex2byte("DC2CC554AFF50A25DC2CC554AFF50A25DC2CC554AFF50A25"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}

		//安装TPK  索引04 密文安装  DES 24个0x13对24个0x38加密 																																																															F2 45 B3 8E A3 44 F1 25 F2 45 B3 8E A3 44 F1 25 F2 45 B3 8E A3 44 F1 25 
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)2, (byte)4, 24, ISOUtils.hex2byte("F245B38EA344F125F245B38EA344F125F245B38EA344F125"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
//		
		gui.cls_show_msg1(1, "上位机密钥安装完毕");
		
		//每次随机输入密码 最短4位 最长12位
		String[] pinNumber={"1234","12345","123456","1234567","12345678","123456789","1234567890","12345678901","123456789012"};
		gui.cls_show_msg("请确保POS与SP100密码键盘已连接!且SP100已安装好NLP文件。任意键继续");
		
		//获取版本指令
		if(!((resString=test_sp100_cmd(new byte[]{0x32,0x30},null)).equals("00"))){
		gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:获取版本号指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
	}

		/**
		 * 由于多数版本不支持明文安装TMK，故屏蔽安装。改成NLP文件，测试前置时由测试人员自行下载
		 */
//		//TMK密钥为24个0x13   索引01  算法DES   非标准版无法明文安装
//		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥。。。");
//		if(!((resString=test_sp100_cmd(new byte[]{0x35,0x30},ISOUtils.hex2byte("000203010018131313131313131313131313131313131313131313131313"))).equals("00"))){
//			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:安装sp100密钥指令执行失败(%s)", Tools.getLineInfo(), resString);
//			return;
//		}
		Log.d("eric_chen","DES");
	//发送指令安装sp100密钥     	//24个0x13对24个0x38进行DES加密得到的数据为F2 45 B3 8E A3 44 F1 25 F2 45 B3 8E A3 44 F1 25  索引为01
		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥（DES）");             

		if(!((resString=test_sp100_cmd(new byte[]{0x35,0x30},ISOUtils.hex2byte("010200010018F245B38EA344F125F245B38EA344F125F245B38EA344F125"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:安装sp100密钥指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
		
		
		while(cnt>0){

			if(gui.cls_show_msg1(2,"压力测试中...\n还剩%d次（已成功%d次）,[取消]退出测试...",cnt,succ)==ESC)
				break;
			cnt--;

				
			
	
			//测试前置：下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			//case1 DES算法验证,直接使用现有的pin密钥模式(模式0),密文(01)pin密钥
			//设置脱机pin指令    pan数据"6225885916163157123"
			Random rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----DES算法验证,直接使用现有的pin密钥模式(模式0),密文(01),使用TPK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
																																											//6225885916163157110
			// byte[]panbyte_des=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0001001136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case1。。通过");
		

			//case2 DES算法验证,直接使用现有的pin密钥模式(模式0),密文(01)  TMK密钥
			//设置脱机pin指令    pan数据"6225885916163157"
			//测试前置IC卡下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----DES算法验证,直接使用现有的pin密钥模式(模式0),密文(01),使用TMK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0001001136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
				return;
			}
			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}

			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, DES_Tpk_24, panbyte_des, PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case2。。通过");
			
			
			//case3 DES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)  pin密钥
			//测试前置IC卡下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----DES算法验证,直接使用现有的pin密钥模式(模式0),明文(00),TPK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0001001136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_24, panbyte_des, PINBlock_des.length, PINBlock_des, secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case3。。通过");
			
			//case4 DES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)  TMK密钥
			//测试前置IC卡下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----DES算法验证,直接使用现有的pin密钥模式(模式0),明文(00)");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0001001136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, DES_Tpk_24, panbyte_des, PINBlock_des.length, PINBlock_des, secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case4。。通过");
  //模式1 返回上来的pinblock只支持用上位机的TMK来解密---NDK接口就不测TPK解密的方式 所以屏蔽
//			//case5 DES算法验证,使用sp100生成的pin密钥(模式1),密文(01)  TPK密钥
//			//测试前置IC卡下电
//			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//			{				
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//				
//			}
//			randNumber = rand.nextInt(pinNumber.length);
//			gui.cls_show_msg1(2,"脱机ping案例测试----使用sp100生成的pin密钥(模式1),密文(01),TPK解密");
//			gui.cls_show_msg1(1,"请在密码键盘上输入%s,密码键盘第一行应显示A,第二行显示为空,第三行显示为please enter,第四行显示为BBBBB",pinNumber[randNumber]);
////			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0101001301020304050607080901020304050607080901080006506C656173652020656E7465721C1C1C1C"))).equals("00"))){
////			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
////			return;
////		}
//			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0101001136323235383835393136313633313537000C001E506C656173652020656E7465721C1C1C1C"))).equals("00"))){
//			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
//			return;
//		}
//			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
//			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
//			//IC卡上电
//			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//			}
//
//			int len=ISOUtils.hexInt(recvBuf, 18, 1);
//			Log.d("eric_chen", "len==="+len);
//			byte[]des_pinblock=new byte[len];
//			System.arraycopy(recvBuf, 19,des_pinblock , 0, des_pinblock.length);
//			LoggerUtil.e("des_pinblock:"+Dump.getHexDump(des_pinblock));
//			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0,24, des_pinblock, panbyte_des, 8, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x01))!=NDK_OK){	
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//				return;
//			}
//			//比对IC卡返回值
//			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
//			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
//			if (!(Arrays.equals(ICCtem, ICC_Out))) {
//				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
//				return;
//			}
//			gui.cls_show_msg("case5。。通过");
			
			//case6 DES算法验证,使用sp100生成的pin密钥(模式1),密文(01)  TMK密钥
			//测试前置IC卡下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----使用sp100生成的pin密钥(模式1),密文(01),TMK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0101001136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			int len=ISOUtils.hexInt(recvBuf, 18, 1);
		     byte[]	des_pinblock=new byte[len];
			System.arraycopy(recvBuf, 19,des_pinblock , 0, des_pinblock.length);
			LoggerUtil.e("des_pinblock:"+Dump.getHexDump(des_pinblock));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, des_pinblock, panbyte_des, 8, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x01))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				return;
			}	
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			gui.cls_show_msg1(1,"case6。。通过");
			
			
			//case7 DES算法验证,使用sp100生成的pin密钥(模式1),明文(00)  TMK密钥
			//测试前置IC卡下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2,"脱机ping案例测试----使用sp100生成的pin密钥(模式1),明文(00),TMK解密");
			gui.cls_show_msg1(1,"请在密码键盘上输入%s,%s",pinNumber[randNumber],text);
			if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0101001136323235383835393136313633313537000C001E411C1C506C656173652020656E7465721C42424242421C"))).equals("00"))){
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
			return;
		}
			System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			 len=ISOUtils.hexInt(recvBuf, 18, 1);
				des_pinblock=new byte[len];
				System.arraycopy(recvBuf, 19,des_pinblock , 0, des_pinblock.length);
				LoggerUtil.e("des_pinblock:"+Dump.getHexDump(des_pinblock));
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, des_pinblock, panbyte_des, 8, PINBlock_des, secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
					return;
				}	
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				gui.cls_show_msg1(1,"case7。。通过");
//				//case8 DES算法验证,使用sp100生成的pin密钥(模式1),明文(00)  TPK密钥
//				//测试前置IC卡下电
//				if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//				{				
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//					
//				}
//				randNumber = rand.nextInt(pinNumber.length);
//				gui.cls_show_msg1(2,"脱机ping案例测试----使用sp100生成的pin密钥(模式1),明文(00),TPK解密");
//				gui.cls_show_msg1(1,"请在密码键盘上输入%s,密码键盘第一行应显示A,第二行显示为空,第三行显示为please enter,第四行显示为BBBBB",pinNumber[randNumber]);
//				if(!((resString=test_sp100_cmd(ISOUtils.hex2byte("6438"),ISOUtils.hex2byte("0101001136323235383835393136313633313537000C001E506C656173652020656E7465721C1C1C1C"))).equals("00"))){
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:设置脱机pin指令执行失败(%s)", Tools.getLineInfo(), resString);
//					return;
//				}
//				System.arraycopy(recvBuf, 10,PINBlock_des , 0, PINBlock_des.length);
//				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
//				//IC卡上电
//				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//				}
//				 len=ISOUtils.hexInt(recvBuf, 18, 1);
//					des_pinblock=new byte[len];
//					System.arraycopy(recvBuf, 19,des_pinblock , 0, des_pinblock.length);
//					LoggerUtil.e("des_pinblock:"+Dump.getHexDump(des_pinblock));
//					if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 24, des_pinblock, panbyte_des, 8, PINBlock_des, secRsaKey.usBits , null,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0x00))!=NDK_OK){	
//						gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//						return;
//					}	
//					//比对IC卡返回值
//					System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
//					LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
//					if (!(Arrays.equals(ICCtem, ICC_Out))) {
//						gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
//						return;
//					}
				succ++;
		}
		gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"脱机pingDES算法验证通过,总次数%d次，成功%d次",bak,succ);		
	}
	
	/**
	 * 整合串口初始化
	 */
	private int portOpen() {
		int fd1 = -1;
		int fd2 = -1,ret = -1;
		int fd3=-1;
		//pinpad   //不传参数就是pinpad口
		if (comValue==0) {
			Log.d("eric_chen", "进入pinpad");
			if ((fd1 = uart3Manager.open()) != 0) {
				gui.cls_show_msg1_record(TAG, "pinpad_portOpen", 2,"line %d:串口打开失败，fd=%d...", Tools.getLineInfo(),fd1);
				return NDK_ERR;
			}
			gui.cls_show_msg1(1, "正在设置波特率.............波特率==%d,参数为%s",BpsBean.bpsValue,para[1]);
			//波特率115200  非阻塞
			if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,"8N1NN".getBytes())) != ANDROID_OK) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:波特率设置失败，ret=%d", Tools.getLineInfo(), ret);
				return NDK_ERR;
			}

		}
		//rs232
		else if (comValue==1) {
			Log.d("eric_chen", "进入Rs232");
			fd2 =uart3Manager.open(62);
			gui.cls_show_msg1(1, "正在打开串口.............fd===%d",fd2);
			if (fd2  == -1) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:打开串口失败，fd=%d", Tools.getLineInfo(), fd2);
				return NDK_ERR;
			}
			gui.cls_show_msg1(1, "正在设置波特率.............波特率==%d,参数为%s",BpsBean.bpsValue,para[1]);
			//波特率115200  非阻塞
			if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,"8N1NN".getBytes())) != ANDROID_OK) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:波特率设置失败，ret=%d", Tools.getLineInfo(), ret);
				return NDK_ERR;
			}
		}else if (comValue==2) {
			Log.d("eric_chen", "进入USB");
			byte[] data = new byte["8N1NN".getBytes().length+1];
			System.arraycopy("8N1NN".getBytes(), 0, data, 0, data.length-1);
			if ((fd3=UartPort.JNI_openPort(3, 115200, data))<0) {
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:串口打开失败（%d）", Tools.getLineInfo(),ret);
				return NDK_ERR;
			}
			USBCOMNODE=fd3;
			Log.d("eric_chen", "USBCOMNODE=="+USBCOMNODE);
		}
		return NDK_OK;
	}
	
	/**
	 * 整合串口关闭
	 */
	private void portClose() {
		switch (comValue) {
		case 0:
			uart3Manager.close();
			break;

		case 1:
			uart3Manager.close();
			break;
		case 2:
			int rettem;
			if ((rettem=UartPort.JNI_close(USBCOMNODE))!=0) {
				gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:关闭串口异常（%d）", Tools.getLineInfo(),ret);
			}
			break;

		default:
			break;
		}
	}
private byte[] cmdFrame_sp100(byte[] data,int datalen){
	byte[] DATALEN = new byte[2];
//	DATALEN = ISOUtils.intToBytes(datalen-1, 2, true);
  DATALEN =BCDUtil.DecToBCDArray(datalen-1, 2);
	Log.d("eric_chen", "DATALEN="+Arrays.toString(DATALEN));
	int offsettem=0;
	byte[] temresult=new byte[datalen+2];
	//将长度也封装在data头中 
	System.arraycopy(DATALEN, 0, temresult, 0, DATALEN.length);
	offsettem+=DATALEN.length;
	//将数据添加
	System.arraycopy(data, 0, temresult, offsettem, datalen);
	LoggerUtil.e("eric_chen_temresult:"+Dump.getHexDump(temresult));
	byte[] lrc = caculateLRC(temresult);
	//拼装命令
	int offset = 0;
	byte[] rslt = new byte[LEN_STX+temresult.length+LEN_LRC];
	System.arraycopy(STX, 0, rslt, 0, LEN_STX);
	offset+=LEN_STX;
	System.arraycopy(temresult, 0, rslt, offset, temresult.length);
	offset+= temresult.length;
	System.arraycopy(lrc, 0, rslt, offset, lrc.length);
	LoggerUtil.e("eric_endcmdPack:"+Dump.getHexDump(rslt));
	return rslt;
	
}
	private int cmd_frame_factory(byte[] data,int datalen){
		byte[] temp = new byte[256];
		byte[] buf = new byte[256];
		byte[]buftem=new byte[256];
		byte[]bcdlen=new byte[]{0x18,0x00};
		byte[] cmdPack;
		boolean first=true;
		int i=0;
		int data_len=0;
		cmdPack=cmdFrame_sp100(data,datalen);
		//发包收包根据串口

		switch (comValue) 
		{
		case 0:	
//
//			if ((ret = nlManager.write(cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
//			{
//				gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:pinpad写失败(ret=%d)", Tools.getLineInfo(), ret);
//				return ret;
//			}
//			for(i=0;i<256;i++){
//				if ((ret = nlManager.read(temp, 1, MAXWAITTIME)) < 0){
//					gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:pinpad读失败(ret=%d)", Tools.getLineInfo(),ret);
//					return ret;
//				}
//				Log.d("eric_chen", "ret=="+ret);
//				buf[i] = temp[0];
//				if(Arrays.equals(temp, ETX))
//					break;
//			}
//			Log.d("eric_chen", "pinpad--------"+Arrays.toString(cmdPack));
//			nlManager.ioctl(0x540B, new byte[]{(byte)2});//清串口缓存
//			recvBuf = ISOUtils.trim(buf, i+1);
//			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//			break;	
		case 1:
			LoggerUtil.e("打印下发的指令-----(getHexDump):"+Dump.getHexDump(cmdPack));
			if ((ret = uart3Manager.write(cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
			{
				gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:RS232写失败(ret=%d)", Tools.getLineInfo(),ret);
				return ret;
			}

			for(i=0;i<256;i++){

				Log.d("eric_chen", "开始第"+i+"次读数据---------");
				
				if ((ret = uart3Manager.read(temp, 1, 30)) < 0){
					LoggerUtil.e("串口无数据-------:");
				    break;
				}					
				buf[i] = temp[0];
				if (first) {
					if (temp[0]==STX[0]||Arrays.equals(temp, STX)) {
						Log.d("eric_chen", "收到02帧");
						uart3Manager.read(temp, 1, 30);
						buf[i+1] = temp[0];
						uart3Manager.read(temp, 1, 30);
						buf[i+2] = temp[0];
						bcdlen[0]=temp[0];
						data_len=BCDUtil.bcd2Int(bcdlen,2);
						LoggerUtil.e("data_len:"+data_len);
						i=i+2;
						first=false;
					}
				}

				LoggerUtil.e("接收Buf:"+Dump.getHexDump(temp));
	
				
				if((Arrays.equals(temp, ETX)||temp[0]==ETX[0])&&i>=(data_len+4)){
					Log.d("eric_chen", "i===="+i+"   "+"data_len==="+data_len);
					uart3Manager.write(RTX, RTX.length, MAXWAITTIME) ;
					break;
				}
			}
			//如果包含应答帧
			if (buf[0]==RTX[0]) {
				LoggerUtil.e("包含应答帧---执行去除操作");
				System.arraycopy(buf, 1, buftem, 0, buf.length-1);
				recvBuf = ISOUtils.trim(buftem, i+1);
			}else {
				recvBuf = ISOUtils.trim(buf, i+1);
			}
			while(true){
				Log.d("eric_chen", "开始清空串口数据-----------");
				rettest=uart3Manager.read(Clearrecv, Clearrecv.length, 1);
//				LoggerUtil.e("Clearrecv:"+Dump.getHexDump(Clearrecv));
				if (rettest<=0) {
					Log.d("eric_chen", "串口无数据-----------");
					break;
				}
			}
			uart3Manager.ioctl(0x540B, new byte[]{(byte)2});//清串口缓存
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			break;
			
		case 2:
			LoggerUtil.e("打印下发的指令-----(getHexDump):"+Dump.getHexDump(cmdPack));
			Log.d("eric_chen", "USB数据下发");
			
			if ((ret = UartPort.JNI_write(USBCOMNODE,cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
			{
				gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:USB写失败(ret=%d)", Tools.getLineInfo(),ret);
				return ret;
			}

			for(i=0;i<256;i++){

				Log.d("eric_chen", "开始第"+i+"次读数据---------");
				
				if ((ret = UartPort.JNI_read(USBCOMNODE,temp, 1, 30)) < 0){
					LoggerUtil.e("串口无数据-------:");
				    break;
				}					
				buf[i] = temp[0];
				if (first) {
					if (temp[0]==STX[0]||Arrays.equals(temp, STX)) {
						Log.d("eric_chen", "收到02帧");
						UartPort.JNI_read(USBCOMNODE,temp, 1, 30);
						buf[i+1] = temp[0];
						UartPort.JNI_read(USBCOMNODE,temp, 1, 30);
						buf[i+2] = temp[0];
						bcdlen[0]=temp[0];
						data_len=BCDUtil.bcd2Int(bcdlen,2);
						LoggerUtil.e("data_len:"+data_len);
						i=i+2;
						first=false;
					}
				}

				LoggerUtil.e("接收Buf:"+Dump.getHexDump(temp));
	
				
				if((Arrays.equals(temp, ETX)||temp[0]==ETX[0])&&i>=(data_len+4)){
					Log.d("eric_chen", "i===="+i+"   "+"data_len==="+data_len);
					UartPort.JNI_write(USBCOMNODE,RTX, RTX.length, MAXWAITTIME) ;
					break;
				}
			}
			//如果包含应答帧
			if (buf[0]==RTX[0]) {
				LoggerUtil.e("包含应答帧---执行去除操作");
				System.arraycopy(buf, 1, buftem, 0, buf.length-1);
				recvBuf = ISOUtils.trim(buftem, i+1);
			}else {
				recvBuf = ISOUtils.trim(buf, i+1);
			}
			while(true){
				Log.d("eric_chen", "开始清空串口数据-----------");
				rettest=UartPort.JNI_read(USBCOMNODE,Clearrecv, Clearrecv.length, 1);
				if (rettest<=0) {
					Log.d("eric_chen", "串口无数据-----------");
					break;
				}
			}
			UartPort.JNI_clearBuf(USBCOMNODE, 0);//清串口缓存
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			break;

		default:
			break;
		}
		
		return NDK_OK;
		
	}
	
	private String test_sp100_cmd(byte[]cmd,byte[]data){
		byte[] cmdtemData;
		cmdtemData = cmdPack_sp100(cmd,data);
		if((ret = cmd_frame_factory(cmdtemData,cmdtemData.length)) == NDK_OK){
			byte[]rescode=new byte[2];
			if (recvBuf!=null) {
				System.arraycopy(recvBuf,6, rescode, 0, rescode.length);
				LoggerUtil.e("rescode:"+Dump.getHexDump(rescode));
				String rescodesString=new String(rescode);
				return rescodesString;
			}else {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:未接收到sp100响应", Tools.getLineInfo());
				return SP100_ERR;
			}
	
	
		} else{
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:sp100指令传输失败(%s,%d)", Tools.getLineInfo(),ISOUtils.hexString(cmdtemData, 0, 2), ret);
			return SP100_ERR;
		}
		
	}
	private byte[] cmdPack_sp100(byte[] cmd, byte[] data) {
		// TODO Auto-generated method stub
		int len=0;
		//判断是否有data
		if(data != null){
			len = data.length;
		}
		//拼装命令
		int offset = 0;
		//指令类型的长度是2 再加上内容的长度
		byte[] rslt = new byte[2 + LEN_SEPAR+len+LEN_ETX];
		System.arraycopy(cmd, 0, rslt, 0, 2);
		//0x2F在数据之前
		offset +=2;
		System.arraycopy(SEPAR, 0, rslt, offset, LEN_SEPAR);
		offset+=LEN_SEPAR;
		
		if(data != null){
			System.arraycopy(data, 0, rslt, offset, len);
			offset += len;
		}
		//将结束符03组包进去
		System.arraycopy(ETX,0,rslt,offset,LEN_ETX);
		LoggerUtil.e("cmdPack_sp100:"+Dump.getHexDump(rslt));
		return rslt;
	}
	
	/**
	 * 计算lrc
	 * 
	 * @param payload
	 * @return lrc
	 */
	private byte[] caculateLRC(byte[] payload) {
		int offset = 0;
		byte lrc = payload[0];
		do {
			offset++;
			lrc ^= payload[offset];
		} while (offset < payload.length - 1);

		return new byte[] { lrc };
	}	
}
