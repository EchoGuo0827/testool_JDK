
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
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
import com.newland.ndk.SecRsaKey;
import com.newland.uartport.UartPort;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest107.java
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200413
 * directory 		: 
 * description 		: 外接SP100密码键盘测试(国内版)
 * related document : 
 * history 		 	: 变更点						变更时间			变更人员
 *			  		 CPOS_SP100密码键盘国内版脱机Pin测试              20200413		陈丁
 *				
 *					变更说明			变更时间			变更人
 *		脱机Pin案例新增USB通讯方式。		20200805		陈丁
*				修改PINPAD通讯方式实现		20200922			陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest107 extends DefaultFragment {
	private final String TAG = SysTest107.class.getSimpleName();
	private final String TESTITEM = "外接SP100密码键盘测试(国内版_X5支持)";
	private Gui gui;
	private NLUART3Manager uart3Manager=null;
//	private NlManager nlManager = null;
	SecRsaKey secRsaKey;
	int comValue;
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	private boolean isInit=false;
	private SecKcvInfo kcvInfo; 
	String[] para={"8N1NB","8N1NN"};// para[0]:阻塞      para[1]:非阻塞
	byte[] ICbuf=new byte[256];
	private static final byte[] STX = new byte[] { 0x02 };  //正文开头 0X02
	private static final byte[] ETX = new byte[] { 0x03 }; //结束符 0X03
	private static final byte[] RTX = new byte[] { 0x06 }; //应答帧 0X06
	private byte[] recvBuf;
	private byte[] SP100_ERR=new byte[]{0x43,0x44};
	private int control=-10086; //0代表第一次发取6 7位   1代表第二次发 取12 13位
	int[] protocol = new int[2];
	private int ret=-1;
	String text="null";
	 //RSA数据
	private byte[] RSAKEY=new byte[]{
			   (byte) 0x96, (byte) 0x92, (byte) 0xF2, (byte) 0xDC, 0x0D, (byte) 0xFE, (byte) 0xA1, 0x34, (byte) 0xF6, (byte) 0xD5, (byte) 0xDA, (byte) 0xF7, 0x56, 0x34, (byte) 0xCA, (byte) 0xEC,
				(byte) 0xC8, 0x55, (byte) 0xEC, 0x77, 0x4F, 0x4B, (byte) 0xFE, 0x6F, (byte) 0x90, (byte) 0xDD, (byte) 0xD4, 0x32, (byte) 0xB5, (byte) 0xDB, 0x1C, (byte) 0xDC, (byte) 0xFB, (byte) 0xAC, (byte) 0x96,
				(byte) 0x98, 0x02, (byte) 0xC6, 0x4A, 0x60, 0x69, (byte) 0xFD, 0x26, 0x7C, 0x41, (byte) 0xC1, (byte) 0xAC, (byte) 0x82, (byte) 0xE3, (byte) 0x8E, (byte) 0xA2, 0x54, (byte) 0xF4, (byte) 0xAA, 
				0x4B, (byte) 0xD0, (byte) 0x9B, 0x04, 0x51, 0x6C, 0x19, (byte) 0xE1, (byte) 0x8A, (byte) 0xC5
	};
	int USBCOMNODE=-1;
	public void systest107()
	{
		secRsaKey=new SecRsaKey();
		kcvInfo= new SecKcvInfo();
		gui = new Gui(myactivity, handler);
		
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
//		nlManager = (NlManager) myactivity.getSystemService(NlContext.K21_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("外接SP100密码键盘测试(国内版)\n0.配置\n1.指令测试\n2.场景测试\n3.脱机pinNDK接口测试\n4.sp100初始化(无需测试该项)");
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
				break;
				
			case '1':
				command_test();
				break;
			case '2':
				int returnValue2=gui.cls_show_msg("外接SP100密码键盘测试(国内版)\n0.DES场景测试\n1.AES场景测试\n2.SM4场景测试");
				switch (returnValue2) 
				{
				case '0': //DES算法
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						return;
					}
					DES_initJudge();
					break;
				case '1': //AES算法
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						return;
					}
					AES_initJudge();
					break;
				case '2': //SM4算法
					if (!isInit) {
						gui.cls_show_msg1(2, "请先进行配置");
						return;
					}
					SM4_initJudge();
					break;
				default:
					break;
				}
				break;
			case '3':
				NDKPin_test();
				break;
			case '4':
				Init_Sp100();
				break;
			case ESC:
				intentSys();
				return;
			default:
				Log.d("eric_chen", "串口被关闭。。。。");
				portClose();
				break;
			
			}
			
		}
		
		
	}
	private void Init_Sp100() {
		byte[] rest1;
		byte[] rest2;
		gui.cls_show_msg("按任意键开始初始化sp100密钥，该测试项只是将sp100密钥环境初始化成适合脱机Pin测试");
		
		//清除所有密钥
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B6E0D0A"),null,2);
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,2);
		//初始化
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B6C0D0A"),null,2);
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,2);
		
		gui.cls_show_msg1(5,"初始化完毕");
		
		
	}
	//NDK_SecVerifyPIN接口测试
	private void NDKPin_test() {
		byte[] TPKdata = null;
		byte[] pandata = new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		byte[] pinblockdata = null;
		byte[] psIccRespOut = null; 
	
		
		gui.cls_show_msg1(2, "正在安装上位机匹配密钥");
		//测试前置 擦除所有密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//安装24个0x38的 TLK  DES 01
		kcvInfo.nCheckMode=0;
		if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 24, ISOUtils.hex2byte("383838383838383838383838383838383838383838383838"), kcvInfo)!=NDK_OK) {
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
		}

		//安装TMK1  索引02 密文安装   DES    明文为8个0x13 8个0x14 8个0x15																																							
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)2, 24, ISOUtils.hex2byte("AE0F956A74C8EAF2144DD64D5B80774F48243FE971CB2A29"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		
		//安装TPK  索引04 密文安装  DES  明文为8个0x31 8个0x32 8个0x33		 TMK1																																																											
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)2, (byte)4, 24, ISOUtils.hex2byte("037BE101498F0E7449CEA629F5B219DC9692458948BED2FD"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1(2, "安装上位机匹配密钥安装完毕");
		
		gui.cls_show_msg("请先确保已插入EMV_IC卡,按任意键继续");
		//测试前置：下电
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.1  参数非法:不在密钥索引范围的ID
		gui.cls_show_msg1(2,"验证不在密钥索引范围的ID");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)0, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.2 参数非法:密钥索引传-1000
		gui.cls_show_msg1(2,"验证密钥索引传-1000");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)-1000, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.3 异常测试:传入未安装密钥索引
		gui.cls_show_msg1(2,"验证传入未安装密钥索引");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)10, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.4参数非法:传入不在范围内的算法类型
		gui.cls_show_msg1(2,"传入不在范围内的算法类型");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)10, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.5 参数非法:算法类型传-1000
		gui.cls_show_msg1(2,"算法类型传-1");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)-1000, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.6异常测试:TSKLen的长度与传入的psTSK不匹配
		gui.cls_show_msg1(2,"TSKLen的长度与传入的psTSK不匹配");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("AE71309A1DAE67B6");
		psIccRespOut=new byte[100];
		TPKdata=ISOUtils.hex2byte("9076FF0F63AEFBF8A0B5D4C25CB274A6F5D29B4103E194F2");
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 23, TPKdata, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.7参数非法:传入非法的TSKLen值
		gui.cls_show_msg1(2,"传入非法的TSKLen值");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("AE71309A1DAE67B6");
		psIccRespOut=new byte[100];
		TPKdata=ISOUtils.hex2byte("9076FF0F63AEFBF8A0B5D4C25CB274A6F5D29B4103E194F2");
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, -1, TPKdata, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.8异常测试:psTSK与TSKlen不匹配
		gui.cls_show_msg1(2,"psTSK与TSKlen不匹配");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("AE71309A1DAE67B6");
		psIccRespOut=new byte[100];
		TPKdata=ISOUtils.hex2byte("9076FF0F63AEFBF8A0B5D4C25CB274A6F5D29B4103E194");
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, TPKdata, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PIN_OFFLINE){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.9异常测试:psTSK为null,psTSK的长度不为0
		gui.cls_show_msg1(2,"psTSK为null,psTSK的长度不为0");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("AE71309A1DAE67B6");
		psIccRespOut=new byte[100];
		TPKdata=ISOUtils.hex2byte("9076FF0F63AEFBF8A0B5D4C25CB274A6F5D29B4103E194F2");
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.10参数非法：pan数据传Null  应用崩溃 暂时屏蔽 待评审后恢复
//		gui.cls_show_msg1(2,"pan数据传Null");
//		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
//		secRsaKey.usBits=64*8;
//		secRsaKey.sModulus=RSAKEY;
//		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
//		psIccRespOut=new byte[100];
//		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, null,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//		}
//		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//		{				
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//		}
		//case2.11异常测试:pan数据不符合格式要求
		gui.cls_show_msg1(2,"pan数据不符合格式要求");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=ISOUtils.hex2byte("1111111111111111111111111111111100");
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PIN_OFFLINE){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.12参数非法：pinblocklen长度为-1
		gui.cls_show_msg1(2,"pinblocklen长度为-1");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,-1, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.13参数非法：pinblocklen与传入的pinblock不匹配
		gui.cls_show_msg1(2,"pinblocklen与传入的pinblock不匹配");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,7, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PIN_OFFLINE){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
//		//case2.14异常测试：pinblock传null    应用崩溃 待评审后决定是否保留
//		gui.cls_show_msg1(2,"pinblock传null");
//		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
//		secRsaKey.usBits=64*8;
//		secRsaKey.sModulus=RSAKEY;
//		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
//		psIccRespOut=new byte[100];
//		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
//		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, null, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//		}
//		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//		{				
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//		}
		//case2.15异常测试:pinblock与pinblocklen不匹配
		gui.cls_show_msg1(2,"pinblock与pinblocklen不匹配");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F3");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_PIN_OFFLINE){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
//		//case2.16参数非法：结构体中的usbits值传-1
//		gui.cls_show_msg1(2,"结构体中的usbits值传-1");
//		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
//		secRsaKey.usBits=-1;
//		secRsaKey.sModulus=RSAKEY;
//		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
//		psIccRespOut=new byte[100];
//		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
//		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_K21_COLLAPSE){	
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//		}
//		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//		{				
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//		}
		//case2.17参数非法:结构体中的sModulus值传null
		gui.cls_show_msg1(2,"结构体中的sModulus值传null");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=null;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECP_PARAM){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
//		//case2.18参数非法:结构体中的sExponent值传null  应用崩溃待评审决定是否保留
//		gui.cls_show_msg1(2,"结构体中的sExponent值传null");
//		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		secRsaKey.sExponent=null;
//		secRsaKey.usBits=64*8;
//		secRsaKey.sModulus=RSAKEY;
//		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
//		psIccRespOut=new byte[100];
//		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
//		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//		}
//		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//		{				
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		//case2.19参数非法:结构体中的reverse值传null   应用崩溃待评审决定是否保留
//		gui.cls_show_msg1(2,"结构体中的reverse值传null");
//		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
//		secRsaKey.usBits=64*8;
//		secRsaKey.sModulus=RSAKEY;
//		secRsaKey.reverse=null;
//		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
//		psIccRespOut=new byte[100];
//		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
//		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_ERR_SECCR_GET_KEY){	
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//		}
//		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
//		{				
//			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
//		}
//		secRsaKey.reverse=new byte[4];
		//case2.20参数非法:psIccRespOut传Null
		gui.cls_show_msg1(2,"psIccRespOut传Null");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,null, (byte)1))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.21参数非法: ucMode传-1000
		gui.cls_show_msg1(2,"ucMode传-1000");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)-1000))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.22参数非法: ucMode传入范围外的值
		gui.cls_show_msg1(2,"ucMode传入范围外的值");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)20))!=NDK_ERR_PARA){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.23正常测试:使用上位机Pin密钥解密，模式密文
		gui.cls_show_msg1(2,"正常测试:使用上位机Pin密钥解密，模式密文");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.24正常测试:使用上位机Pin密钥解密，模式明文
		gui.cls_show_msg1(2,"正常测试:使用上位机Pin密钥解密，模式明文");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.25正常测试:使用上位机主密钥解密，模式密文
		gui.cls_show_msg1(2,"正常测试:使用上位机主密钥解密，模式密文");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("AE71309A1DAE67B6");
		psIccRespOut=new byte[100];
		TPKdata=ISOUtils.hex2byte("54527E366B34E137C5437358014A32A27C13FD7A039DD99C");
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, TPKdata, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		//case2.26正常测试:使用上位机主密钥解密，模式明文
		gui.cls_show_msg1(2,"正常测试:使用上位机主密钥解密，模式明文");
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("AE71309A1DAE67B6");
		psIccRespOut=new byte[100];
		TPKdata=ISOUtils.hex2byte("54527E366B34E137C5437358014A32A27C13FD7A039DD99C");
		
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, TPKdata, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
		{				
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
		}
		
		//case2.27 场景测试：未插入IC卡,接口应无法解密成功
		gui.cls_show_msg("请拔出IC卡,按任意键继续");
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		//case2.28 场景测试：插入IC卡，未上电，接口应无法解密成功
		gui.cls_show_msg("请插入IC卡,按任意键继续");
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		gui.cls_show_msg("请拔出IC卡,按任意键继续");
		//case2.29 场景测试：插入IC卡,上电成功后，拔插IC卡。接口应无法解密成功
		gui.cls_show_msg("请再次插入IC卡,按任意键继续");
		
		if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
		}
		gui.cls_show_msg("请拔插IC卡,按任意键继续");
		secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
		secRsaKey.usBits=64*8;
		secRsaKey.sModulus=RSAKEY;
		pinblockdata=ISOUtils.hex2byte("769962BD7506F32D");
		psIccRespOut=new byte[100];
		pandata=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, null, pandata,8, pinblockdata, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT){	
			gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
		}
		
		gui.cls_show_msg1_record(TAG, "NDKPin_test", g_keeptime, "脱机Pin接口测试通过", Tools.getLineInfo(),ret);
		
		
		
		
	}
	//指令测试
	private void command_test() {
		byte[] rest1,rest2;
		// 0000050310363232353838353931363136333135370C003C300D0A
		//SP100 TMK密钥为8个0x13 8个0x14 8个0x15   索引01  算法DES   非标准版无法明文安装    
		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥。。。");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("0100020301000018AE0F956A74C8EAF2144DD64D5B80774F48243FE971CB2A290D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		//sp100 TPK1密钥     	 8个0x13 8个0x14 8个0x15对8个0x31 8个0x32 8个0x33加密     索引05
		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("0101020005000018037BE101498F0E7449CEA629F5B219DC9692458948BED2FD0D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		gui.cls_show_msg("sp100密钥安装完毕。按任意键开始测试脱机pin密钥");
		//case 1.1  参数非法:传入异常密钥类型
		gui.cls_show_msg1(2,"验证传入异常密钥类型，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("1200050310363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		
		//case 1.2 参数非法:传入异常模式
		gui.cls_show_msg1(2,"验证传入异常模式，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0022050310363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case 1.3  异常测试:传入未安装密钥索引
		gui.cls_show_msg1(2,"验证传入未安装密钥索引，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000330310363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3032"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.4  异常测试:传入索引范围之外的值
		gui.cls_show_msg1(2,"验证传入索引范围之外的值，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000000310363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.5 异常测试:传入已安装密钥索引，但与算法不对应
		gui.cls_show_msg1(2,"验证传入已安装密钥索引，但与算法不对应，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050C10363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3032"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.6 异常测试:pinblock模式与算法模式不对应，但又是有效参数
		gui.cls_show_msg1(2,"验证传入pinblock模式与算法模式不对应，但又是有效参数，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050810363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3032"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.7  参数非法：pinblock模式传入异常参数
		gui.cls_show_msg1(2,"验证传入pinblock模式传入异常参数，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000052410363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.8  异常测试:传入与算法模式不对应，但又是有效参数
		gui.cls_show_msg1(2,"验证传入密钥长度与算法模式不对应，但又是有效参数，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101032010363232353838353931363136333135370C003C310D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3034"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.9  参数非法：密钥长度传入异常参数
		gui.cls_show_msg1(2,"验证传入密钥长度传入异常参数，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101032110363232353838353931363136333135370C003C310D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.10 异常测试：卡号长度合法，但与卡号不对应
		gui.cls_show_msg1(2,"验证传入卡号长度合法，但与卡号不对应，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000005031036323235383835393136313633313537333231300C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.11 参数非法：卡号长度非法最小边界值减一
		gui.cls_show_msg1(2,"验证传入卡号长度非法最小边界值减一，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000005030736323235383835390C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.12 参数非法：卡号长度非法最大边界值加一
		gui.cls_show_msg1(2,"验证传入卡号长度非法最大边界值加一，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00000503213632323538383539313631363331353736323235383835393136313633313537370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.13 异常测试：卡号合法，但与卡号长度不对应
		gui.cls_show_msg1(2,"验证传入卡号合法，但与卡号长度不对应，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000005030F363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.14 异常测试:卡号非法
		gui.cls_show_msg1(2,"验证传入卡号非法，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310121212121212121212121212121212120C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.15 异常测试:卡号和卡号长度均非法
		gui.cls_show_msg1(2,"验证传入卡号和卡号长度均非法，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000005030512121212120C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.16 参数非法:输pin长度非法最小边界值减1
		gui.cls_show_msg1(2,"验证传入输pin长度非法最小边界值减1，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00000503103632323538383539313631363331353703003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.17 参数非法:输pin长度非法最大边界值加1
		gui.cls_show_msg1(2,"验证传入输pin长度非法最大边界值加1，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370D003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//评审后开发回复该字段是预留字段。无需传其他参数。故屏蔽 by20200521 chending
//		//case1.18 参数非法：是否使用enter键来终止输入参数非法
//		gui.cls_show_msg1(2,"验证传入是否使用enter键来终止输入参数非法，若可以输入密码。请在密码键盘上输入123456");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C053C300D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
//		}
//		//case1.19 异常测试:传入合法但不支持的参数
//		gui.cls_show_msg1(2,"验证传入合法但不支持的参数，若可以输入密码。请在密码键盘上输入123456");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C013C300D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
//		}
		//case1.20 参数非法:超时时间参数非法最小边界值减1
		gui.cls_show_msg1(2,"验证传入超时时间参数非法最小边界值减1，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C0004300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.21 参数非法:超时时间参数非法最大边界值加1
		gui.cls_show_msg1(2,"验证传入超时时间参数非法最大边界值加1，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C00C9300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.22 参数非法:显示参数非法最小边界值减1
		gui.cls_show_msg1(2,"验证传入显示参数非法最小边界值减1，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C290D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.23 参数非法:显示参数非法最大边界值加1
		gui.cls_show_msg1(2,"验证传入显示参数非法最大边界值加1，若可以输入密码。请在密码键盘上输入123456");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C340D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3031"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.24  发送正常指令，输入密码后点击取消
		gui.cls_show_msg1(2,"验证发送正常指令，输入密码后点击取消，若可以输入密码。请在密码键盘上输入1234后按取消键");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3035"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		//case1.25 下发正确的指令，输入允许最大pin长度+1的密码应无法输入最后一位
		gui.cls_show_msg1(2,"验证下发正确的指令，输入允许最大pin长度+1的密码应无法输入最后一位，若可以输入密码。请在密码键盘上输入123456789012后尝试输入3，预期无法输入3。(本条需人工确认),最后点击确认键");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C300D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest1=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest1));
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "command_test", g_keeptime, "line %d:%s测试失败(rest2=%s)", Tools.getLineInfo(),TESTITEM,Dump.getHexDump(rest2));
		}
		
		gui.cls_show_msg1_record(TAG, "command_test", g_keeptime,"指令测试通过");
		
	}
	//SM4 TLK 16个0x35
	private void SM4_initJudge() {
		byte[]rest1;
		byte[]rest2;
		PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 10));
		int cnt=packet.getLifecycle();
		byte[]SM4_Tpk_16=  new byte[16];
		byte[]SM4_Tpk_Test16=new byte[16];
		SM4_Tpk_16=ISOUtils.hex2byte("7DEBC248F52FBAE2D6F2F5495705E555");
		byte[]panbyte_des=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		int succ=0;
		int bak=packet.getLifecycle();
		int randNumber=0;
		String rescodesString;
		byte[]psIccRespOut=new byte[100];
		byte[]ICC_Out=new byte[]{(byte) 0x90,0x00};
		byte[]ICCtem=new byte[2];
		byte[]PINBlock_sm4=new byte[16];
		int datalen;
		byte[]datalenbyte=new byte[1];
		gui.cls_show_msg1(3, "上位机安装匹配的密钥中。");
		//测试前置 擦除所有密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//  上位机密钥需要与Sp100安装的一致   TMK TLK TPK
		//TLK 16个0X35
		int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
		kcvInfo.nCheckMode=0;
		if ((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|algMode[1]), (byte)0, (byte)1, 16, ISOUtils.hex2byte("35353535353535353535353535353535"), kcvInfo))!=NDK_OK) {
			gui.cls_show_msg1_record(TAG,"SM4_initJudge", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//安装TMK   索引08   密文安装   SM4   明文为8个0x50 8个0x51                                     															
		if((ret=JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[1]), (byte)(1|algMode[1]), (byte)1, (byte)8, 16, ISOUtils.hex2byte("1959F7A33B8C6A2773BA8E1EAA97351D"), kcvInfo))!=NDK_OK){
			gui.cls_show_msg1_record(TAG,"SM4_initJudge", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
		}
		
		//安装TPK1    索引 09   密文安装  SM4   明文为8个0x60 8个0x61                                     																						
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[1]), (byte)(2|algMode[1]), (byte)8, (byte)9, 16, ISOUtils.hex2byte("7DEBC248F52FBAE2D6F2F5495705E555"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"SM4_initJudge", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		
		gui.cls_show_msg1(3, "上位机密钥安装完毕。");
		String[] pinNumber={"1234","12345","123456","1234567","12345678","123456789","1234567890","12345678901","123456789012"};
		//TMK  id 20  明文为8个0x50 8个0x51
		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥。。。");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("01000303140000101959F7A33B8C6A2773BA8E1EAA97351D0D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		//sp100 TPK1密钥      id 30	明文为8个0x60 8个0x61 
		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥（SM4）");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("140103001E0000107DEBC248F52FBAE2D6F2F5495705E5550D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
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
			//case1 模式0使用现有的pin密钥模式,键盘显示请输入密码，有语音提示，密文,pin密钥解密
			Random rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请输入密码，有语音提示，密文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00001E0810363232353838353931363136333135370C003C300D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)9, (byte)2, 0, SM4_Tpk_16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case2  模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，密文,pin密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，密文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00001E0810363232353838353931363136333135370C003C320D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)9, (byte)2, 0, SM4_Tpk_16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case3  模式0使用现有的pin密钥模式,键盘显示请再输入密码，有语音提示，明文,pin密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请再输入密码，有语音提示，明文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00001E0810363232353838353931363136333135370C003C310D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)9, (byte)2, 0, SM4_Tpk_16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case4  模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,pin密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00001E0810363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)9, (byte)2, 0, SM4_Tpk_16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case5  模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，明文,TMK密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，明文,TMK密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00001E0810363232353838353931363136333135370C003C320D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)8, (byte)2, 16, SM4_Tpk_16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case6  模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，密文,TMK密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，密文,TMK密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("00001E0810363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)8, (byte)2, 16, SM4_Tpk_16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case7 模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,16字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000114081010363232353838353931363136333135370C003C300D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			
			System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 31,SM4_Tpk_Test16 , 0, datalen);
			
			
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)8, (byte)2, datalen, SM4_Tpk_Test16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case8  模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,16字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000114081010363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_sm4 , 0, PINBlock_sm4.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_sm4));
			
			System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 31,SM4_Tpk_Test16 , 0, datalen);
			
			
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)8, (byte)2, datalen, SM4_Tpk_Test16, panbyte_des,PINBlock_sm4.length, PINBlock_sm4, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			
			
			
		}
		gui.cls_show_msg1_record(TAG, "SM4_initJudge", g_keeptime,"SM4场景通过");
		
	}

	//AES算法  初始化KEK为32字节 0x37
	private void AES_initJudge() {
		PacketBean packet = new PacketBean();
		byte[]PINBlock_aes=new byte[16];   //指令返回数据的Pinblock
		byte[]AES_Tpk_32=  new byte[32];
		byte[]AES_Tpk_24=  new byte[32];
		byte[]AES_Tpk_16=  new byte[16];
		byte[]AES_Tpk_test32=  new byte[32];
		byte[]AES_Tpk_test24=  new byte[24];
		byte[]AES_Tpk_test16=  new byte[16];
		byte[]datalenbyte=new byte[1];
		int datalen;
		AES_Tpk_32=ISOUtils.hex2byte("7A03BEC234388350669E165EDA5BD903277FB29FE3C5AD9190073B6B320539D9");
		AES_Tpk_24=ISOUtils.hex2byte("C33A01E89933F78EE3E22FC10998FDFA62FEC8261F233F664BC089EF1E6CC451");
		AES_Tpk_16=ISOUtils.hex2byte("C62E784527F8C6F86D8E6BFD8C1BA74E");
		byte[]panbyte_des=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		byte[]rest1;
		byte[]rest2;
		byte[]psIccRespOut=new byte[100];
		byte[]ICC_Out=new byte[]{(byte) 0x90,0x00};
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 10));
		int cnt=packet.getLifecycle();
		int succ=0;
		int bak=packet.getLifecycle();
		int randNumber=0;
		String rescodesString;
		int ret = -1;
		Arrays.fill(ICbuf, (byte)0);
		byte[]ICCtem=new byte[2];
		
		gui.cls_show_msg1(3, "上位机安装匹配的密钥中。");
		//测试前置 擦除所有密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//AES  上位机密钥需要与Sp100安装的一致   TMK TLK TPK
		//TLK 32个0X37
		int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
		kcvInfo.nCheckMode=0;
		
		if ((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|algMode[2]), (byte)0, (byte)1, 32, ISOUtils.hex2byte("3737373737373737373737373737373737373737373737373737373737373737"), kcvInfo))!=NDK_OK) {
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
		}
		//安装TMK   索引03  密文安装   AES    明文为8个0x16 8个0x17 8个0x18 8个0x19                                      															
		if((ret=JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)3, 32, ISOUtils.hex2byte("B88597DB23BA30E28D836C45EF017FD6D0868D6B7BA6D1A9F73DA8969B0171FA"), kcvInfo))!=NDK_OK){
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
		}
		
//		//安装TMK2    索引20   密文安装   AES    明文为8个0x70 8个0x71 8个0x72                                      															
//		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)20, 32, ISOUtils.hex2byte("C33A01E89933F78EE3E22FC10998FDFA62FEC8261F233F664BC089EF1E6CC451"), kcvInfo)!=NDK_OK){
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//		
//		//安装TMK3    索引21   密文安装   AES    明文为8个0x73 8个0x74                                    															
//		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)21, 16, ISOUtils.hex2byte("0DFC53359F474C8C63F1E6308CF33BF3"), kcvInfo)!=NDK_OK){
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//		
																																		//7A 03 BE C2 34 38 83 50 66 9E 16 5E DA 5B D9 03 27 7F B2 9F E3 C5 AD 91 90 07 3B 6B 32 05 39 D9
		//安装TPK1    索引 05   密文安装  AES   明文为8个0x21 8个0x22 8个0x23 8个0x24                                     																						
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[2]), (byte)(2|algMode[2]), (byte)3, (byte)5, 32, ISOUtils.hex2byte("7A03BEC234388350669E165EDA5BD903277FB29FE3C5AD9190073B6B320539D9"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		
		gui.cls_show_msg1(3, "上位机安装密钥完成。");
		String[] pinNumber={"1234","12345","123456","1234567","12345678","123456789","1234567890","12345678901","123456789012"};
		//AES TMK密钥  id 02   明文为8个0x16 8个0x17 8个0x18 8个0x19
		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥。。。");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("010001200302000020B88597DB23BA30E28D836C45EF017FD6D0868D6B7BA6D1A9F73DA8969B0171FA0D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		
//		//TMK2   id 20  明文为8个0x70 8个0x71 8个0x72 
//		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥2。。。");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("010001180314000020C33A01E89933F78EE3E22FC10998FDFA62FEC8261F233F664BC089EF1E6CC4510D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		
//		//TMK3   id 21  明文为8个0x73 8个0x74  
//		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥3。。。");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("0100011803150000100DFC53359F474C8C63F1E6308CF33BF30D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
		//sp100 TPK1密钥      id 03	明文为8个0x21 8个0x22 8个0x23 8个0x24
		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥（AES）");                                     
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("0201012000030000207A03BEC234388350669E165EDA5BD903277FB29FE3C5AD9190073B6B320539D90D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
//		//sp100 TPK2密钥      id 07	明文为8个0x25 8个0x26 
//		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥2（AES）");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("020101100007000010C62E784527F8C6F86D8E6BFD8C1BA74E0D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		
//		
//		//sp100 TPK3密钥      id 30	明文为8个0x27 8个0x28 8个0x29 
//		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥3（AES）");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("02010120001E0000201AA19977D6A034457AC5D2214D2DE5973BB0576EA42A782C643267F4BC0608E70D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
		
		while(cnt>0){

			if(gui.cls_show_msg1(2,"压力测试中...\n还剩%d次（已成功%d次）,[取消]退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			//测试前置：下电
			if ((ret=JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
			{				
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡下电失败(%d)", Tools.getLineInfo(),ret);
				
			}
			//case1 模式0使用现有的pin密钥模式,键盘显示请输入密码，有语音提示，密文,pin密钥解密
			Random rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请输入密码，有语音提示，密文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000030C10363232353838353931363136333135370C003C300D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, AES_Tpk_32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			 //case2 模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，密文,pin密钥解密
			 	rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，密文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000030C10363232353838353931363136333135370C003C320D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));

				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, AES_Tpk_32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				
				 //case3 模式0使用现有的pin密钥模式,键盘显示请再输入密码，有语音提示，明文,pin密钥解密
			 	rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请再输入密码，有语音提示，明文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000030C10363232353838353931363136333135370C003C310D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, AES_Tpk_32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				
				//case4  模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,pin密钥解密
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000030C10363232353838353931363136333135370C003C330D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)5, (byte)1, 0, AES_Tpk_32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				
				//case5  模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，明文,TMK密钥解密
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，明文,TMK密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000030C10363232353838353931363136333135370C003C330D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, 32, AES_Tpk_32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))==NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				//case6  模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，密文,TMK密钥解密
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2," 模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，密文,TMK密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000030C10363232353838353931363136333135370C003C330D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, 32, AES_Tpk_32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))==NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}

				
				//case7 模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,32字节
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,32字节。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0001020C2010363232353838353931363136333135370C003C300D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				
				System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
				LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
				datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
				System.arraycopy(recvBuf, 31,AES_Tpk_test32 , 0, datalen);
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, datalen, AES_Tpk_test32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				
				//case8 模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,32字节
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2,"  模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,32字节。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0001020C2010363232353838353931363136333135370C003C330D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
				LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
				datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
				System.arraycopy(recvBuf, 31,AES_Tpk_test32 , 0, datalen);
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, datalen, AES_Tpk_test32, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				
				//case9  模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,16字节
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2,"  模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0001020C1010363232353838353931363136333135370C003C300D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
				LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
				datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
				System.arraycopy(recvBuf, 31,AES_Tpk_test16 , 0, datalen);
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, datalen, AES_Tpk_test16, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
				//case10  模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,16字节
				rand = new Random();
				randNumber = rand.nextInt(pinNumber.length);
				//IC卡上电
				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
				}
				EMVIC_Test();
				gui.cls_show_msg1(2,"  模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0001020C1010363232353838353931363136333135370C003C330D0A"),0);
				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				
				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
				}
				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
				secRsaKey.usBits=64*8;
				secRsaKey.sModulus=RSAKEY;
				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
				System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
				LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
				datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
				System.arraycopy(recvBuf, 31,AES_Tpk_test16 , 0, datalen);
				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, datalen, AES_Tpk_test16, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
				}
				//比对IC卡返回值
				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
				if (!(Arrays.equals(ICCtem, ICC_Out))) {
					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
					return;
				}
//开发回复：24字节不用支持				
//				//case11  模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,24字节
//				rand = new Random();
//				randNumber = rand.nextInt(pinNumber.length);
//				//IC卡上电
//				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//				}
//				EMVIC_Test();
//				gui.cls_show_msg1(2,"  模式1生成随机PIN秘钥加密,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,24字节。请在密码键盘上输入%s",pinNumber[randNumber]);
//				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0001020C1810363232353838353931363136333135370C003C300D0A"),0);
//				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//				}
//				
//				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//					gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//				}
//				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
//				secRsaKey.usBits=64*8;
//				secRsaKey.sModulus=RSAKEY;
//				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
//				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
//				System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
//				LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
//				datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
//				System.arraycopy(recvBuf, 31,AES_Tpk_test24 , 0, datalen);
//				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, datalen, AES_Tpk_test24, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//				}
//				//比对IC卡返回值
//				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
//				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
//				if (!(Arrays.equals(ICCtem, ICC_Out))) {
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
//					return;
//				}
//				gui.cls_show_msg1(1,"case11。。通过");
//				
//				//case12  模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,24字节
//				rand = new Random();
//				randNumber = rand.nextInt(pinNumber.length);
//				//IC卡上电
//				if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
//				}
//				EMVIC_Test();
//				gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,24字节。请在密码键盘上输入%s",pinNumber[randNumber]);
//				rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0001020C1810363232353838353931363136333135370C003C330D0A"),0);
//				if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//				}
//				
//				rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//				if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//					gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//				}
//				secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
//				secRsaKey.usBits=64*8;
//				secRsaKey.sModulus=RSAKEY;
//				System.arraycopy(recvBuf, 14,PINBlock_aes , 0, PINBlock_aes.length);
//				LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
//				LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_aes));
//				System.arraycopy(recvBuf, 30,datalenbyte , 0, 1);
//				LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
//				datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
//				System.arraycopy(recvBuf, 31,AES_Tpk_test24 , 0, datalen);
//				if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)3, (byte)1, datalen, AES_Tpk_test24, panbyte_des,PINBlock_aes.length, PINBlock_aes, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
//				}
//				//比对IC卡返回值
//				System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
//				LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
//				if (!(Arrays.equals(ICCtem, ICC_Out))) {
//					gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
//					return;
//				}
//				gui.cls_show_msg1(1,"case12。。通过");

			
			
			
		}
		gui.cls_show_msg1_record(TAG, "AES_initJudge", g_keeptime,"AES场景测试通过");
		
		
	}


	//DES算法  初始化KEK为24字节 0x38
	private void DES_initJudge() {
		PacketBean packet = new PacketBean();
		byte[]PINBlock_des=new byte[8];   //指令返回数据的Pinblock
		byte[]DES_Tpk_24=  new byte[24];
		byte[]DES_Tpk_16=  new byte[16];
		byte[]DES_Tpk_test24=  new byte[24];
		byte[]DES_Tpk_test16=  new byte[16];
		//模式1 datalen 
		int datalen;
		byte[]datalenbyte=new byte[1];
		//03 7B E1 01 49 8F 0E 74 49 CE A6 29 F5 B2 19 DC 96 92 45 89 48 BE D2 FD
		DES_Tpk_24=ISOUtils.hex2byte("037BE101498F0E7449CEA629F5B219DC9692458948BED2FD");
		DES_Tpk_16=ISOUtils.hex2byte("64C4186E08006CC815F17DA084AE0542");
		byte[]panbyte_des=new byte[]{0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00}; //pan数据
		byte[]rest1;
		byte[]rest2;
		byte[]psIccRespOut=new byte[100];
		byte[]ICC_Out=new byte[]{(byte) 0x90,0x00};
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, 10));
		int cnt=packet.getLifecycle();
		int succ=0;
		int bak=packet.getLifecycle();
		int randNumber=0;
		String rescodesString;
		int ret = -1;
		Arrays.fill(ICbuf, (byte)0);
		byte[]ICCtem=new byte[2];
		
		gui.cls_show_msg1(2, "正在安装上位机匹配密钥");
		//测试前置 擦除所有密钥
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:1-255索引密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//DES  上位机密钥需要与Sp100安装的一致   TMK TLK TPK
		//安装24个0x38的 TLK  DES 01
		kcvInfo.nCheckMode=0;
		if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 24, ISOUtils.hex2byte("383838383838383838383838383838383838383838383838"), kcvInfo)!=NDK_OK) {
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
		}

		//安装TMK1  索引02 密文安装   DES    明文为8个0x13 8个0x14 8个0x15																																							
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)2, 24, ISOUtils.hex2byte("AE0F956A74C8EAF2144DD64D5B80774F48243FE971CB2A29"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		
		//安装TPK  索引04 密文安装  DES  明文为8个0x31 8个0x32 8个0x33		 TMK1																																																											
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)2, (byte)4, 24, ISOUtils.hex2byte("037BE101498F0E7449CEA629F5B219DC9692458948BED2FD"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1(2, "正在安装上位机匹配密钥安装完毕");
	
		String[] pinNumber={"1234","12345","123456","1234567","12345678","123456789","1234567890","12345678901","123456789012"};
		//SP100 TMK密钥为8个0x13 8个0x14 8个0x15   索引01  算法DES   非标准版无法明文安装    
		
		gui.cls_show_msg1(2,"正在给密码键盘安装TMK主密钥。。。");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("0100020301000018AE0F956A74C8EAF2144DD64D5B80774F48243FE971CB2A290D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
//		// TMK2密钥为8个0x16 8个0x17    非标准版无法明文安装     索引11
//		gui.cls_show_msg1(2,"正在给密码键盘安装TMK2主密钥。。。");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("010002030B00001070B7C0090C797508A3E1FDE451E9D7750D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		
		//sp100 TPK1密钥     	 8个0x13 8个0x14 8个0x15对8个0x31 8个0x32 8个0x33加密     索引05
		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥（DES）");
		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("0101020005000018037BE101498F0E7449CEA629F5B219DC9692458948BED2FD0D0A"),0);
		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
		}
		
//		//sp100 TPK2密钥     	 8个0x13 8个0x14 8个0x15对8个0x41 8个0x43      索引08
//		gui.cls_show_msg1(2,"正在安装密文的TPK工作密钥2（DES）");
//		rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B64"),ISOUtils.hex2byte("010102000800001064C4186E08006CC815F17DA084AE05420D0A"),0);
//		if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
//			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
//		rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
//		if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
//			gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//		}
	
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
			//0x36,0x32,0x32,0x35,0x38,0x38,0x35,0x39,0x31,0x36,0x31,0x36,0x33,0x31,0x35,0x37,0x00
			//设置脱机pin指令    pan数据"6225 8859 1616 3157 "
			Random rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请输入密码，有语音提示，密文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C300D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte) 0, 0, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case2 模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，密文,pin密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，密文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C320D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));

			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);

			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case3  模式0使用现有的pin密钥模式,键盘显示请再输入密码，有语音提示，明文,pin密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请再输入密码，有语音提示，明文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C310D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);

			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case4  模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,pin密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,pin密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case5  模式0使用现有的pin密钥模式,键盘显示请输入密码，无语音提示，明文,TMK密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C320D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));

			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);

			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case6   模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，密文,TMK密钥解密
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式0使用现有的pin密钥模式,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("0000050310363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));

			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, 24, DES_Tpk_24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);

			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case7    模式1生成随机PIN秘钥加密模式,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,24字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密模式,键盘显示请输入密码，有语音提示，密文,TMK密钥解密,24字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031810363232353838353931363136333135370C003C300D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test24 , 0, datalen);
			LoggerUtil.e("DES_Tpk_test:"+Dump.getHexDump(DES_Tpk_test24));
			
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, datalen, DES_Tpk_test24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);

			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case8    模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,24字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码，无语音提示，明文,TMK密钥解密,24字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031810363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test24 , 0, datalen);
			
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, datalen, DES_Tpk_test24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
	
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			//case9    模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,有语音提示，明文,TPK密钥解密,24字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,有语音提示，明文,TPK密钥解密,24字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031810363232353838353931363136333135370C003C310D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test24 , 0, datalen);
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_test24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case10    模式1生成随机PIN秘钥加密模式,键盘显示请输入密码,无语音提示，密文,TPK密钥解密,24字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密模式,键盘显示请输入密码,无语音提示，密文,TPK密钥解密,24字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031810363232353838353931363136333135370C003C320D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test24 , 0, datalen);
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_test24, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case11    模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,有语音提示，密文,TMK密钥解密,16字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,有语音提示，密文,TMK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031010363232353838353931363136333135370C003C310D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test16 , 0, datalen);
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, datalen, DES_Tpk_test16, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case12    模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,有语音提示，明文,TMK密钥解密,16字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			gui.cls_show_msg1(2,"模式1生成随机PIN秘钥加密模式,键盘显示请输入密码,无语音提示，密文,TMK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031010363232353838353931363136333135370C003C320D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test16 , 0, datalen);
			LoggerUtil.e("DES_Tpk_test16:"+Dump.getHexDump(DES_Tpk_test16));
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)2, (byte)0, datalen, DES_Tpk_test16, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			//case13    模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,无语音提示，明文,TPK密钥解密,16字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密模式,键盘显示请再输入密码,无语音提示，明文,TPK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031010363232353838353931363136333135370C003C330D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test16 , 0, datalen);
			LoggerUtil.e("DES_Tpk_test16:"+Dump.getHexDump(DES_Tpk_test16));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_test16, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)0))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}

		
			//case14    模式1生成随机PIN秘钥加密模式,键盘显示请输入密码,有语音提示，密文,TPK密钥解密,16字节
			rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			gui.cls_show_msg1(2," 模式1生成随机PIN秘钥加密模式,键盘显示请输入密码,有语音提示，密文,TPK密钥解密,16字节。请在密码键盘上输入%s",pinNumber[randNumber]);
			rest1=test_sp100_cmd(ISOUtils.hex2byte("C001011B71"),ISOUtils.hex2byte("000101031010363232353838353931363136333135370C003C300D0A"),0);
			if (!Tools.memcmp(rest1, ISOUtils.hex2byte("0000"), rest1.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			
			rest2=test_sp100_cmd(ISOUtils.hex2byte("C002000A00FF"),null,1);
			if (!Tools.memcmp(rest2, ISOUtils.hex2byte("3030"), rest2.length)){
				gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			}
			secRsaKey.sExponent=new byte[]{0x00,0x00,0x03};
			secRsaKey.usBits=64*8;
			secRsaKey.sModulus=RSAKEY;
			System.arraycopy(recvBuf, 14,PINBlock_des , 0, PINBlock_des.length);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			LoggerUtil.e("PINBlock:"+Dump.getHexDump(PINBlock_des));
			System.arraycopy(recvBuf, 22,datalenbyte , 0, 1);
			LoggerUtil.e("datalenbyte:"+Dump.getHexDump(datalenbyte));
			datalen=ISOUtils.bytesToInt(datalenbyte, 0, 1, true);
			System.arraycopy(recvBuf, 23,DES_Tpk_test16 , 0, datalen);
			LoggerUtil.e("DES_Tpk_test16:"+Dump.getHexDump(DES_Tpk_test16));
			//IC卡上电
			if ((ret=JniNdk.JNI_Icc_PowerUp(EM_ICTYPE.ICTYPE_IC.ordinal(),ICbuf,protocol))!=NDK_OK){
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", 5, "line %d:IC卡上电失败(%d)", Tools.getLineInfo(),ret);
			}
			EMVIC_Test();
			if((ret=JniNdk.JNI_Sec_VerifyPIN((byte)4, (byte)0, 0, DES_Tpk_test16, panbyte_des,PINBlock_des.length, PINBlock_des, secRsaKey.usBits , secRsaKey.sModulus,secRsaKey.sExponent,secRsaKey.reverse,psIccRespOut, (byte)1))==NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:JNI_Sec_VerifyPIN执行失败(ret=%d)", Tools.getLineInfo(), ret);
			}
			//比对IC卡返回值
			System.arraycopy(psIccRespOut, 0, ICCtem, 0, ICCtem.length);
			LoggerUtil.e("ICCtem:"+Dump.getHexDump(ICCtem));
			if (!(Arrays.equals(ICCtem, ICC_Out))) {
				gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:脱机IC卡返回值错误", Tools.getLineInfo(), ret);
				return;
			}
			
			
			
		}
		
		gui.cls_show_msg1_record(TAG, "DES_initJudge", g_keeptime,"DES场景测试通过");
		
		
		
		
		
		
	}


	
	
	private byte[] test_sp100_cmd(byte[] cmd, byte[] data,int control) {
		byte[] cmdtemData;
		cmdtemData = cmdPack_sp100(cmd,data);
		byte[]rescode=new byte[2];
		if((ret = cmd_frame_factory(cmdtemData,cmdtemData.length)) == NDK_OK){
			if (control==0) {
				Log.d("eric_chen", "取6 7位返回值");
				System.arraycopy(recvBuf,5, rescode, 0, rescode.length);
				LoggerUtil.e("rescode:"+Dump.getHexDump(rescode));
			}else if (control==1) {
				Log.d("eric_chen", "取12 13位返回值");
				System.arraycopy(recvBuf,11, rescode, 0, rescode.length);
				LoggerUtil.e("rescode:"+Dump.getHexDump(rescode));
			}
			return rescode;
		}else{
			gui.cls_show_msg1_record(TAG, "test_sp100_cmd", g_keeptime,"line %d:sp100指令传输失败(%s,%d)", Tools.getLineInfo(),ISOUtils.hexString(cmdtemData, 0, 2), ret);
			return SP100_ERR;
		}
		
		
	}


	
	private int cmd_frame_factory(byte[] cmdtemData, int length) {
		byte[] temp = new byte[256];
		byte[] buf = new byte[256];
		byte[]buftem=new byte[256];
		byte[]bcdlen=new byte[]{0x18,0x00};
		byte[] Clearrecv = new byte[1024];  
		byte[] cmdPack;
		int ret=-1;
		int rettest;
		boolean first=true;
		int i=0;
		int data_len=0;
		cmdPack=cmdFrame_sp100(cmdtemData,length);
		switch (comValue) 
		{
		case 0:
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
						byte[] datalenbyte=new byte[2];
						int offest = 0;
						uart3Manager.read(temp, 1, 30);
						buf[i+1] = temp[0];
						System.arraycopy(temp, 0, datalenbyte, offest, 1);
						offest=offest+1;
						
						uart3Manager.read(temp, 1, 30);
						buf[i+2] = temp[0];
						System.arraycopy(temp, 0, datalenbyte, offest, 1);
//						bcdlen[0]=temp[0];
//						DATALEN =ISOUtils.intToBytes(datalen, 2, true);
//						data_len=BCDUtil.bcd2Int(bcdlen,2);
						
						data_len=ISOUtils.bytesToInt(datalenbyte,0, 2, true);
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
						byte[] datalenbyte=new byte[2];
						int offest = 0;
						UartPort.JNI_read(USBCOMNODE,temp, 1, 30);
						buf[i+1] = temp[0];
						System.arraycopy(temp, 0, datalenbyte, offest, 1);
						offest=offest+1;
						
						UartPort.JNI_read(USBCOMNODE,temp, 1, 30);
						buf[i+2] = temp[0];
						System.arraycopy(temp, 0, datalenbyte, offest, 1);
//						bcdlen[0]=temp[0];
//						DATALEN =ISOUtils.intToBytes(datalen, 2, true);
//						data_len=BCDUtil.bcd2Int(bcdlen,2);
						
						data_len=ISOUtils.bytesToInt(datalenbyte,0, 2, true);
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


	private byte[] cmdFrame_sp100(byte[] data, int datalen) {
		//计算长度
		byte[] DATALEN = new byte[2];
//		DATALEN =BCDUtil.DecToBCDArray(datalen, 2);
		DATALEN =ISOUtils.intToBytes(datalen, 2, true);
		Log.d("eric_chen", "DATALEN="+Arrays.toString(DATALEN));
		//计算LRC
		byte[] lrc = caculateLRC(data);
		//指令总长度
		byte[] result=new byte[STX.length+DATALEN.length+datalen+lrc.length+ETX.length];
		//开始封装指令
		int offset=0;
		System.arraycopy(STX, 0, result, 0, STX.length);
		offset+=STX.length;
		System.arraycopy(DATALEN, 0, result, offset, DATALEN.length);
		offset+=DATALEN.length;
		System.arraycopy(data, 0, result, offset, datalen);
		offset+=datalen;
		System.arraycopy(lrc, 0, result, offset, lrc.length);
		offset+=lrc.length;
		System.arraycopy(ETX, 0, result, offset, ETX.length);
		LoggerUtil.e("eric_endcmdPack:"+Dump.getHexDump(result));
		return result;
	}


	private byte[] cmdPack_sp100(byte[] cmd, byte[] data) {
		int len=0;
		//判断是否有data
		if(data != null){
			len = data.length;
		}
		//拼装命令
		int offset = 0;
		//指令类型的长度是2 再加上内容的长度
		byte[] rslt = new byte[cmd.length+len];
		
		System.arraycopy(cmd, 0, rslt, 0, cmd.length);
		offset +=cmd.length;
		if(data != null){
			System.arraycopy(data, 0, rslt, offset, len);
			offset += len;
		}
		LoggerUtil.e("cmdPack_sp100:"+Dump.getHexDump(rslt));
		return rslt;
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
			gui.cls_show_msg1(2, "正在打开串口.............fd===%d",fd2);
			if (fd2  == -1) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:打开串口失败，fd=%d", Tools.getLineInfo(), fd2);
				return NDK_ERR;
			}
			gui.cls_show_msg1(2, "正在设置波特率.............波特率==%d,参数为%s",BpsBean.bpsValue,para[1]);
			//波特率115200  非阻塞
			if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,"8N1NN".getBytes())) != ANDROID_OK) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:波特率设置失败，ret=%d", Tools.getLineInfo(), ret);
				return NDK_ERR;
			}
		}else if (comValue==2) {
			Log.d("eric_chen", "进入usb");
			
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
	private void conf_test_bps() {
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
//		//DES  上位机密钥需要与Sp100安装的一致   TMK TLK TPK
//		//安装24个0x38的 TLK  DES 01
//		kcvInfo.nCheckMode=0;
//		if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 24, ISOUtils.hex2byte("383838383838383838383838383838383838383838383838"), kcvInfo)!=NDK_OK) {
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//
//		//安装TMK1  索引02 密文安装   DES    明文为8个0x13 8个0x14 8个0x15																																							
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)2, 24, ISOUtils.hex2byte("AE0F956A74C8EAF2144DD64D5B80774F48243FE971CB2A29"), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
//		
////		//安装TMK2   索引07 密文安装   DES    明文为8个0x16 8个0x17 																																							
////		if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)7, 16, ISOUtils.hex2byte("70B7C0090C797508A3E1FDE451E9D775"), kcvInfo))!=NDK_OK)
////		{
////			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
////			return;
////		}
//		
//
//		//安装TPK  索引04 密文安装  DES  明文为8个0x31 8个0x32 8个0x33		 TMK1																																																											
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)2, (byte)4, 24, ISOUtils.hex2byte("037BE101498F0E7449CEA629F5B219DC9692458948BED2FD"), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
////		//安装TPK2   索引08 密文安装  DES  明文为8个0x41 8个0x43 		 TMK1																																																										
////		if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)2, (byte)8, 16, ISOUtils.hex2byte("64C4186E08006CC815F17DA084AE0542"), kcvInfo))!=NDK_OK)
////		{
////			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
////			return;
////		}
//		
//		
//		//AES  上位机密钥需要与Sp100安装的一致   TMK TLK TPK
//		//TLK 32个0X37
//		int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
//		//安装32个0x37的 TLK  AES   索引01
//		if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|128), (byte)0, (byte)1, 32, ISOUtils.hex2byte("3737373737373737373737373737373737373737373737373737373737373737"), kcvInfo))!=NDK_OK){
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//		
//		//安装TMK   索引03  密文安装   AES    明文为8个0x16 8个0x17 8个0x18 8个0x19                                      															
//		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)3, 32, ISOUtils.hex2byte("B88597DB23BA30E28D836C45EF017FD6D0868D6B7BA6D1A9F73DA8969B0171FA"), kcvInfo)!=NDK_OK){
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//		}
//		
////		//安装TMK2    索引20   密文安装   AES    明文为8个0x70 8个0x71 8个0x72                                      															
////		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)20, 32, ISOUtils.hex2byte("C33A01E89933F78EE3E22FC10998FDFA62FEC8261F233F664BC089EF1E6CC451"), kcvInfo)!=NDK_OK){
////			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
////		}
////		
////		//安装TMK3    索引21   密文安装   AES    明文为8个0x73 8个0x74                                    															
////		if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode[2]), (byte)(1|algMode[2]), (byte)1, (byte)21, 16, ISOUtils.hex2byte("0DFC53359F474C8C63F1E6308CF33BF3"), kcvInfo)!=NDK_OK){
////			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
////		}
////		
//																																		//7A 03 BE C2 34 38 83 50 66 9E 16 5E DA 5B D9 03 27 7F B2 9F E3 C5 AD 91 90 07 3B 6B 32 05 39 D9
//		//安装TPK1    索引 05   密文安装  AES   明文为8个0x21 8个0x22 8个0x23 8个0x24                                     																						
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[2]), (byte)(2|algMode[2]), (byte)3, (byte)5, 32, ISOUtils.hex2byte("7A03BEC234388350669E165EDA5BD903277FB29FE3C5AD9190073B6B320539D9"), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
//			return;
//		}
//		
////		//安装TPK2    索引 06    密文安装  AES   明文为8个0x25 8个0x26                                       																							
////		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[2]), (byte)(2|algMode[2]), (byte)3, (byte)6, 16, ISOUtils.hex2byte("C62E784527F8C6F86D8E6BFD8C1BA74E"), kcvInfo))!=NDK_OK)
////		{
////			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
////			return;
////		}
////		
////		//安装TPK3    索引 30    密文安装  AES   明文为8个0x27 8个0x28  8个0x29                                       																							
////		if((ret = JniNdk.JNI_Sec_LoadKey((byte)(1|algMode[2]), (byte)(2|algMode[2]), (byte)3, (byte)30, 32, ISOUtils.hex2byte("1AA19977D6A034457AC5D2214D2DE5973BB0576EA42A782C643267F4BC0608E7"), kcvInfo))!=NDK_OK)
////		{
////			gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
////			return;
////		}
//		gui.cls_show_msg1(3, "上位机安装密钥完毕。");
		isInit = true;
	}
	//模拟EMV ic流程
	private void EMVIC_Test(){
		byte[] T=new byte[256];
		int[] Clen=new int[1];
		byte[] C=new byte[256];
		byte[] Ctem=new byte[256];
		int apdulen=0;
		
		//1
		T=ISOUtils.hex2byte("00A404000E315041592E5359532E444446303100");
		C=ISOUtils.hex2byte("6F24840E315041592E5359532E4444463031A5128801015F2D086573656E667264659F1101019000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		
		//2
		T=ISOUtils.hex2byte("00B2010C00");
		C=ISOUtils.hex2byte("702A61284F07A0000000031010500A564953414352454449548701019F120D4352454449544F4445564953419000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		
		
		//4
		T=ISOUtils.hex2byte("00B2020C00");
		C=ISOUtils.hex2byte("6A83");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		
		//5
		T=ISOUtils.hex2byte("00A4040007A000000003101000");
		C=ISOUtils.hex2byte("6F328407A0000000031010A5278701019F38129F1A029F33039F40059F1B049F09029F35015F2D086573656E667264659F1101019000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		//6
		T=ISOUtils.hex2byte("80A800001383110840E0F8C8FF80F0A0010001500000962200");
		C=ISOUtils.hex2byte("800E5C000801010010010301180104009000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		//7
		T=ISOUtils.hex2byte("00B2010C00");
		C=ISOUtils.hex2byte("703E5F200F46554C4C2046554E4354494F4E414C57114761739001010010D201220101234567899F1F16303130323033303430353036303730383039304130429000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		
		//9
		T=ISOUtils.hex2byte("00B2011400");
		C=ISOUtils.hex2byte("700E5A0847617390010100105F3401019000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		//11
		T=ISOUtils.hex2byte("00B2021400");
		C=ISOUtils.hex2byte("704C8C179F340395059B029F02069F1A025F2A029A039C019F37048D1995059B028A029F02069F03069F1A025F2A029A039C019F37049F0E0500508800009F0F05F0200498009F0D05F0200400009000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		
		//13
		T=ISOUtils.hex2byte("00B2031400");
		C=ISOUtils.hex2byte("70335F25039507015F24032012315F280208409F0702FFC08E0C0000000000000000010304039F0802008C5F300202019F420208409000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		
		//15
		T=ISOUtils.hex2byte("00B2011C00");
		C=ISOUtils.hex2byte("70658F015790605669914320B96CB54425EF7841C875C767DD82B35FE5C1352BA28D15C1AC99F7A68D38D9DC25F56E4A76E8EF7965C628EBF083455B680E62AE98ED24A0804A7A76C5989825772B225BB7EEA57D204CC67039D45012B8FBA313E55A487BBACCC19000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
	
		//17
		T=ISOUtils.hex2byte("00B2021C00");
		C=ISOUtils.hex2byte("701A9F3201039214CFB8D4885D960967179F982D42CE54ECC20546839000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		//19
		T=ISOUtils.hex2byte("00B2031C00");
		C=ISOUtils.hex2byte("70749F46505CE7E00039D040B49C63C18EA8D358FB967BBD840DC87F01E4BC5D6CD4CCF673781C0473FC3B6D4F78E978E6B50D8528C1391671281347717F914C38737736BD953A78F59536D5AEB610E96DC9E50CED9F481A4A6069FD267C41C1AC82E38EA254F4AA4BD09B04516C19E18AC59F4701039000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		//21
		T=ISOUtils.hex2byte("00B2041C00");
		C=ISOUtils.hex2byte("70529350110BB9DF2D21981906B29A301411F9FA60CF494DBABABF54B1797C9C4B5D99B5E67AB73049E771FC5FDC23E58350B781005324D31DC87AD0FBF636733808056D66074632711E7CBF14073796E1B60D4D9000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
		}
		//23
		T=ISOUtils.hex2byte("80CA9F1700");
		C=ISOUtils.hex2byte("9F1701039000");
		if ((ret=JniNdk.JNI_Icc_Rw(EM_ICTYPE.ICTYPE_IC.ordinal(), T.length, T, Clen, Ctem))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", 5, "line %d:EMV流程失败(%d)", Tools.getLineInfo(),ret);
		}
		apdulen=Clen[0];
		Log.d("eric_chen", "apdulen :"+apdulen);
		LoggerUtil.e("T-------:"+Dump.getHexDump(T));
		LoggerUtil.e("C-------:"+Dump.getHexDump(C));
		LoggerUtil.e("Ctem----------"+Dump.getHexDump(Ctem));
		if (!Tools.memcmp(C, Ctem, apdulen)){
			gui.cls_show_msg1_record(TAG, "EMVIC_Test", g_keeptime, "line %d:%s返回数据错误", Tools.getLineInfo(),TESTITEM);
			
		}
		
		gui.cls_show_msg1(2,"EMV流程通过------");
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
