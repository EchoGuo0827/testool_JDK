package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import android.newland.NLUART3Manager;
import android.newland.NlManager;
import android.newland.content.NlContext;
import android.os.SystemClock;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest78.java
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20180608
 * directory 		: 
 * description 		: 外接密码键盘功能，压力
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		   		20180608	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest78 extends DefaultFragment {
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest78.class.getSimpleName();
	private final String TESTITEM = "外接密码键盘功能，压力";
	private final int DEFAULT_CNT_VLE = 50;
	private final int MAXWAITTIME = 10;//<=0表示永不超时
	private static final byte[] STX = new byte[] { 0x02 };
	private static final byte[] ETX = new byte[] { 0x03 };
	private static final int LEN_STX = STX.length;
	private static final int LEN_ETX = ETX.length;
	private static final int LEN_LENGTH = 2;
	private static final int LEN_STATUS = 2;
	private static final int LEN_LRC = 1;
	private static final int LEN_DATALEN = 2;
	private static final int LEN_CMD_NL829 = 2;
	private static final int LEN_RECV_STATIC = LEN_STX + LEN_LENGTH + LEN_STATUS + LEN_LRC + LEN_ETX; //=7，829指令返回帧的固定部分长度为7，减去7则是附加数据长度
	private static final int MAXLEN = 512;
	private final int NL829_SUCC = 0x0000;
	private final int NL829_ERR = 0x0001;
	private final int NL829_CARD_T0 = 0x00;
	private final int NL829_CARD_T1 = 0x01;
	private final byte[] PACK_HEAD = new byte[] { 0x1B };//以ESC开头
	private final byte[] PACK_TAIL = new byte[] { 0x0D, 0x0A };
	private final int LEN_PACK_HEAD = PACK_HEAD.length;
	private final int LEN_PACK_TAIL = PACK_TAIL.length;
	private final int LEN_CMD_PP60 = 1;
	private final int PP60_SUCC = 0xAA;
	private final int PP60_ERR = 0x55;
	
	private String KEY_0x38 =  ISOUtils.hexString(PP60_code(ISOUtils.hex2byte("38383838383838383838383838383838")),32);//16个0x38,转换格式后为32个字节
	private String KEY_0x31 = ISOUtils.hexString(PP60_code(ISOUtils.hex2byte("31313131313131313131313131313131")),32);//16个0x31
	private String CHECKSIGN = "4D494E49383239";//“MINI829”  7字节ascii码
	//中国银联logo,244bytes原始数据
	private byte[] logo = new byte[] {
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0F,0x08,0x08,0x08,0x08,(byte)0xFF,0x08,
			0x08,0x08,0x08,0x0F,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x7F,
			0x40,0x48,0x49,0x49,0x49,0x4F,0x49,0x49,0x49,0x48,0x40,0x7F,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x04,0x0C,0x34,(byte)0xE7,
			0x24,0x24,0x00,0x7F,0x49,0x49,0x49,0x49,0x7F,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x40,0x40,0x7F,0x48,0x48,0x7F,0x40,0x08,(byte)0x88,0x68,0x0F,0x08,0x28,(byte)0xC8,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte)0xE0,0x40,0x40,0x40,
			0x40,(byte)0xFF,0x40,0x40,0x40,0x40,(byte)0xE0,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,(byte)0xFF,0x02,0x12,0x12,0x12,0x12,(byte)0xF2,0x12,0x52,0x32,0x12,0x02,(byte)0xFF,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte)0x80,
			(byte)0x80,(byte)0x80,(byte)0xFE,(byte)0x84,(byte)0x88,(byte)0x80,(byte)0xFF,0x02,(byte)0xC4,0x30,0x18,0x24,0x46,0x04,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x04,0x06,(byte)0xFC,(byte)0x88,(byte)0x88,(byte)0xFF,0x09,(byte)0x82,(byte)0x8C,(byte)0xB0,(byte)0xC0,
			(byte)0xB0,(byte)0x8C,(byte)0x83,(byte)0x82,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00};
	//字母和汉字"b请"的点阵数据，已进行转换
	private String bqing_dot = "4145504d504d434147414d414941414141414150415041494149415041484141" +//字符’b’点阵数据
			"434143434f4d41414541454546454645" +//汉字“请”点阵数据第1-8字节
			"41414141485043414241414150504246" +//汉字“请”点阵数据第17-24字节
			"46454850464546454645454545414141" +//汉字“请”点阵数据第9-16字节
			"4246424646464a464850414141414141";//汉字“请”点阵数据第25-32字节
	
	private byte[]  sCUPBMP48x51 =new byte[] {
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x20,0x04,0x00,0x00,0x00,0x00,
		0x60,0x06,0x00,0x00,0x00,0x00,(byte)0xC7,(byte)0xE3,0x00,0x00,0x00,0x03,(byte)0x8F,(byte)0xF3,(byte)0x80,0x00,
		0x00,0x07,0x1F,(byte)0xF9,(byte)0xC0,0x00,0x00,0x0E,0x1F,(byte)0xF8,(byte)0xE0,0x00,0x00,0x1E,0x1E,0x00,
		0x70,0x00,0x00,0x3C,0x3C,0x00,0x78,0x00,0x00,0x38,0x3C,0x38,0x3C,0x00,0x00,0x78,
		0x3E,0x78,0x3E,0x00,0x00,(byte)0xF8,0x1F,(byte)0xF8,0x3E,0x00,0x00,(byte)0xF8,0x1F,(byte)0xF8,0x3F,0x00,
		0x01,(byte)0xFC,0x0F,(byte)0xF0,0x7F,(byte)0x80,0x03,(byte)0xFE,0x07,(byte)0xC0,(byte)0xFF,(byte)0x80,0x07,(byte)0xFF,0x00,0x01,
		(byte)0xFF,(byte)0xC0,0x07,(byte)0xFF,(byte)0x80,0x03,(byte)0xFF,(byte)0xE0,0x07,(byte)0xFF,(byte)0xC0,0x07,(byte)0xFF,(byte)0xE0,0x07,(byte)0xFF,
		(byte)0xE0,0x0F,(byte)0xFF,(byte)0xC0,0x03,(byte)0xFF,(byte)0xF0,0x1F,(byte)0xFF,(byte)0x80,0x01,(byte)0xFF,(byte)0xF8,0x3F,(byte)0xFF,0x00,
		0x00,(byte)0xFF,(byte)0xFC,0x7F,(byte)0xFE,0x00,0x00,0x7F,(byte)0xFE,(byte)0xFF,(byte)0xFC,0x00,0x00,0x3F,(byte)0xFE,(byte)0xFF,
		(byte)0xF8,0x00,0x00,0x1F,(byte)0xFC,0x7F,(byte)0xF0,0x00,0x00,0x0F,(byte)0xFC,0x7F,(byte)0xF0,0x00,0x00,0x0F,
		(byte)0xF8,0x3F,(byte)0xF0,0x00,0x00,0x0F,(byte)0xF8,0x1F,(byte)0xF0,0x00,0x00,0x0F,(byte)0xF8,0x1F,(byte)0xF0,0x00,
		0x00,0x1F,(byte)0xF0,0x0F,(byte)0xF8,0x00,0x00,0x3F,(byte)0xE0,0x07,(byte)0xFC,0x00,0x00,(byte)0xFF,(byte)0xC0,0x07,
		(byte)0xFE,0x00,0x00,(byte)0xFF,(byte)0x80,0x03,(byte)0xFF,0x00,0x00,(byte)0xFF,0x00,0x01,(byte)0xFF,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x04,0x07,(byte)0xFE,0x21,0x00,0x40,
		0x04,0x04,0x02,0x3F,(byte)0xE0,(byte)0xA0,0x7F,(byte)0xC5,(byte)0xFA,0x20,0x01,0x10,0x44,0x44,0x42,0x4F,
		(byte)0xC2,0x08,0x44,0x45,(byte)0xFA,0x40,0x0D,(byte)0xF6,0x44,0x44,0x42,(byte)0xCF,(byte)0xC0,0x00,0x7F,(byte)0xC4,
		0x52,0x40,0x00,0x00,0x04,0x04,0x4A,0x4F,(byte)0xC3,(byte)0xF8,0x04,0x07,(byte)0xFE,0x48,0x42,0x08,
		0x04,0x04,0x02,0x4F,(byte)0xC2,0x08,0x04,0x07,(byte)0xFE,0x48,0x43,(byte)0xF8,0x00,0x00,0x00,0x00,
		0x00,0x00, 
		};
	private Gui gui;
	
	/*----------global variables declaration------------------------*/
	private NLUART3Manager uart3Manager=null;
	private NlManager nlManager = null;
	String[] para={"8N1NB","8N1NN"};// para[0]:阻塞      para[1]:非阻塞
	String[] comName = {"PINPAD","RS232"};
	int comValue;
	int ret = -1;
	private byte[] cmdPack;
	private byte[] recvBuf;
	private boolean isInit = false;
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/
	
	public void systest78()
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gSequencePressFlag)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
		nlManager = (NlManager) myactivity.getSystemService(NlContext.K21_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("外接密码键盘综合测试\n0.配置\n1.825指令集(PP60)\n2.829指令集\n3.密码输入压力测试\n");
			switch (returnValue) 
			{
				case '0':
					if(GlobalVariable.currentPlatform==Model_Type.X5)
					{
						int nkeyIn;
						if((nkeyIn=gui.cls_show_msg("串口选择\n0.旧RS232\n1.新RS232\n")) > '1')
						{
							gui.cls_show_msg1(2, "输入有误,请重新配置!");
							break;
						}
						comValue = '1';
						isNewRs232 = nkeyIn==0?false:true;
					}
					else
					{
						if((comValue=gui.cls_show_msg("串口选择\n0.PINPAD\n1.RS232\n")) > '1'){
							gui.cls_show_msg1(2, "输入有误,请重新配置!");
							break;
						} else if(comValue == '0'){
							gui.cls_show_msg1(2, "PINPAD串口请使用NDK Systest35、36号用例测试!");
							break;
						}
					}
					conf_test_bps();
					break;
					
				case ESC:
					intentSys();
					return;
					
				case '1':
				case '2':
				case '3':
					initJudge(returnValue);
					break;
			}
			portClose();
		}
	}
	
	public void conf_test_bps(){
		// 进行波特率的选择
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
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
			gui.cls_show_msg1_record(TAG, "conf_test_bps", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		gui.cls_printf("设置外设BPS中...".getBytes());
		//设置NL-829mini与上位机通讯的波特率(0x30 0x01)
		if((ret = NL829_SetBPS(nkeyIn-'0')) != NL829_SUCC){
			//若不成功,对所有可能的BPS进行尝试
			int i;
			for(i = 0; i<bps.length; i++)
			{
				gui.cls_printf("外设BPS自动适配中...请耐心等待".getBytes());
				BpsBean.bpsValue = bps[i];
				portOpen();
				if((ret = NL829_SetBPS(nkeyIn-'0')) == NL829_SUCC)
					break;
			}
			if(i==bps.length)
			{
				gui.cls_show_msg1_record(TAG, "conf_test_bps", g_keeptime,"line %d:设置NL829的BPS失败,请重启后再尝试", Tools.getLineInfo());
				return;
			}
		}
		BpsBean.bpsValue = bps[nkeyIn-'0'];
		gui.cls_show_msg1(2, "配置通讯波特率为%d成功!",BpsBean.bpsValue);
		isInit = true;
	}
	
	public void initJudge(int value) 
	{
		if(!isInit)
		{
			gui.cls_show_msg("请先进行配置,点任意键继续");
			return;
		}
		switch (value) 
		{
		case '1':
			NL825cmdTest();
			break;
			
		case '2':
			NL829cmdTest();
			break;
			
		case '3':
			keyBoard_press();
			break;
		
		}
	}
	
	public void NL825cmdTest() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("825指令集(PP60)\n0.密钥及密码输入功能测试\n1.屏幕显示及其他功能\n");
			switch (returnValue)
			{
			
			case '0':
				getPinFunc_PP60();
				break;
				
			case '1':
				otherFunc_PP60();
				break;
				
			case ESC:
				return;
			}
		}
			
	}
	
	public void NL829cmdTest() 
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("829指令集\n0.非卡类功能测试\n1.M1卡测试\n2.非接CPU卡测试\n3.接触CPU卡测试\n4.扫描头测试");
			switch (returnValue)
			{
			
			case '0':
				otherFunc_NL829();
				break;
				
			case '1':
				rf_M1_NL829();
				break;
				
			case '2':
				rf_CPU_NL829();
				break;
				
			case '3':
				icsam_NL829();
				break;
				
			case '4':
				scan_NL829();
				break;
			case ESC:
				return;
			}
		}
	}
	
	// PP60指令密钥及密码输入功能测试
	private void getPinFunc_PP60()  
	{
		/*private & local definition*/
		int ret = -1;
		byte[] cmdData; 
		byte [] codeBuf = new byte[16];
		String CARDNO = "373530313233343536373839";//750123456789的ASCII码值
		byte[] result1 = ISOUtils.hex2byte("EDAFE47C2EFA336E");//数据为PIN:12345(05 12 34 5F FF FF FF FF) 异或上 CARDNO(00 00 75 01 23 45 67 89)  DES加密,密钥为全0x38
		byte[] result2 = ISOUtils.hex2byte("7158511ADFB688EB");//数据为PIN:11个1(0B 11 11 11 11 11 1F FF) 异或上 CARDNO(00 00 75 01 23 45 67 89)  DES加密,密钥为全0x38
		
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		gui.cls_show_msg1(1, "密码输入测试...");
		//C指令，清空屏幕
		if((ret = test_PP60_cmd('C',null)) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case1:R指令，重置第0、1组主,用密钥,重置后主密钥16个0x38，用户密钥16个0x00
		if((ret = test_PP60_cmd('R',ISOUtils.hex2byte("4141"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret = test_PP60_cmd('R',ISOUtils.hex2byte("4142"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case2:M指令，修改0号主密钥为16个0x31
		if((ret = test_PP60_cmd('M',ISOUtils.hex2byte("00" + KEY_0x38 + KEY_0x31))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case3:U指令，修改0号主密钥的0号用户密钥为16个0x38
		if((ret = test_PP60_cmd('U',ISOUtils.hex2byte("0000" + KEY_0x31 + KEY_0x38))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case4:A指令，激活第0组主密钥第0号工作密钥，DES方式
		if((ret = test_PP60_cmd('A',ISOUtils.hex2byte("0000"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case5:L、N指令，设置输入密码的最长最小位，这里限制为6位
		if((ret = test_PP60_cmd('L',ISOUtils.hex2byte("06"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret = test_PP60_cmd('N',ISOUtils.hex2byte("06"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case6.1:E0指令，输入密码，保存密文在密码键盘(E0~E3指令仅在屏幕显示有不同，只测E0)
		if((ret = test_PP60_cmd('E',ISOUtils.hex2byte("30"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请在外接密码键盘随机输入6位密码并按确认键,完成后按任意键继续");
		//G指令，在E指令之后使用，取密码密文,期望的返回值为2位长度+16字节密文(转换前是8字节)
		if((ret = test_PP60_cmd('G',null)) != (LEN_LENGTH+16)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//取返回值中的16字节密文
		String str = ISOUtils.hexString(recvBuf, 11, 16);
		//case7:K指令，传入字符串str用第1组主密钥解密(与当前工作密钥相同，为16为0x38),期望的返回值为2位长度+16字节密文(转换前是8字节)
		if((ret = test_PP60_cmd('K',ISOUtils.hex2byte("01"+str))) != (LEN_LENGTH+16)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//处理密码明文
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(gui.cls_show_msg("输入的密码是否为:%s,是[确认],否[其他]", getPWD(codeBuf,6)) != ENTER){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(codeBuf));
		}
		//E0指令end 
		
		//case6.2:E5指令，输入密码，保存明文在密码键盘(E4、E5指令仅在屏幕显示有不同，只测E4)
		test_PP60_cmd('C',null);
		if((ret = test_PP60_cmd('E',ISOUtils.hex2byte("35"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//G指令取存在键盘的明文,6位密码明文期望的返回值为2位长度+12字节密文(转换前是6字节)
		gui.cls_show_msg("请在外接密码键盘随机输入6位密码并按确认键,完成后按任意键继续");
		if((ret = test_PP60_cmd('G',null)) != (LEN_LENGTH+12)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//处理密码明文
		System.arraycopy(recvBuf,11,codeBuf,0,12);
		if(gui.cls_show_msg("输入的密码是否为:%s,是[确认],否[其他]", getPWD(codeBuf,6)) != ENTER){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(codeBuf));
		}
		//E5指令end
		
		//case6.3:E6指令，允许输入字母密码，保存明文在密码键盘(E6、E7指令仅在屏幕显示有不同，只测E6)
		test_PP60_cmd('C',null);
		if((ret = test_PP60_cmd('E',ISOUtils.hex2byte("36"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请在外接密码键盘随机输入6位[字母]并按确认键,字母输入方式为多次按同一个键选择,大小写均可,完成后按任意键继续");
		//G指令取存在键盘的明文,6位密码明文期望的返回值为2位长度+12字节密文(转换前是6字节)
		if((ret = test_PP60_cmd('G',null)) != (LEN_LENGTH+12)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//处理密码明文
		System.arraycopy(recvBuf,11,codeBuf,0,12);
		if(gui.cls_show_msg("输入的密码是否为:%s,是[确认],否[其他]", getPWD(codeBuf,6)) != ENTER){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(codeBuf));
		}
		//E6指令end
		
		//case8:F5指令，输入密码以键号显示，无语音，返回密文(F0~F5指令仅在屏幕显示和语音提示与否有不同，只测F5)
		test_PP60_cmd('C',null);
		cmdData = dataPack_NL829ToPP60('F',ISOUtils.hex2byte("35"));
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") ){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:PP60命令包(F指令)转发失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(recvBuf, 5, 2));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("无语音+明文显示,请在外接密码键盘随机输入6位密码并按确认键,完成后按任意键继续");
		//期望的返回值为2位长度+16字节密文(转换前是8字节)
		cmdData = dataPack_PP60ToNL829(0,LEN_LENGTH+16);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") || (recvBuf.length==12 && ISOUtils.hexString(recvBuf, 9, 1).equals("55"))){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:读取PP60命令(F指令)返回失败(%s,%s)", Tools.getLineInfo(), 
						ISOUtils.hexString(recvBuf, 5, 2),ISOUtils.hexString(recvBuf, 9, 1));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//取返回值中的16字节密文
		str = ISOUtils.hexString(recvBuf, 11, 16);
		//K指令，传入字符串str用第1组主密钥解密(与当前工作密钥相同，为16为0x38),期望的返回值为2位长度+16字节密文(转换前是8字节)
		if((ret = test_PP60_cmd('K',ISOUtils.hex2byte("01"+str))) != (LEN_LENGTH+16)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//处理密码明文
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(gui.cls_show_msg("输入的密码是否为:%s,是[确认],否[其他]", getPWD(codeBuf,6)) != ENTER){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%s)", Tools.getLineInfo(),ISOUtils.hexString(codeBuf));
		}
		//F5指令end
		
		//L、N指令，设置输入密码的最长最小位，这里限制为4-11位
		if((ret = test_PP60_cmd('L',ISOUtils.hex2byte("0B"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret = test_PP60_cmd('N',ISOUtils.hex2byte("05"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case9.1:I2指令,输入PIN与传入的CARDNO进行异或后经工作密钥做DES运算返回密文,(I0~I2指令仅在屏幕显示和语音提示有不同，只测I2)
		test_PP60_cmd('C',null);
		cmdData = dataPack_NL829ToPP60('I',ISOUtils.hex2byte("32"+CARDNO));
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") ){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:PP60命令包(F指令)转发失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(recvBuf, 5, 2));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("无语音提示,请在外接密码键盘输入12345并按确认键,完成后按任意键继续");
		//期望的返回值为2位长度+16字节密文(转换前是8字节)
		cmdData = dataPack_PP60ToNL829(0,LEN_LENGTH+16);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") || (recvBuf.length==12 && ISOUtils.hexString(recvBuf, 9, 1).equals("55"))){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:读取PP60命令(F指令)返回失败(%s,%s)", Tools.getLineInfo(), 
						ISOUtils.hexString(recvBuf, 5, 2),ISOUtils.hexString(recvBuf, 9, 1));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//校验
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(!Tools.memcmp(result1, PP60_decode(codeBuf), 8)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:I指令输入PIN校验失败(%s)", Tools.getLineInfo(), Dump.getHexDump((PP60_decode(codeBuf))));
		}
		//I2end
		
		//case9.2:X指令，与I指令完全相同，增加直接按确认会返回0x56
		test_PP60_cmd('C',null);
		cmdData = dataPack_NL829ToPP60('X',ISOUtils.hex2byte("30"+CARDNO));
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") ){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:PP60命令包(F指令)转发失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(recvBuf, 5, 2));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请在外接密码键盘语音提示后直接按确认键,完成后按任意键继续");
		//期望的返回值为2位长度+16字节密文(转换前是8字节)
		cmdData = dataPack_PP60ToNL829(0,LEN_LENGTH+16);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") || !(recvBuf.length==12 && ISOUtils.hexString(recvBuf, 9, 1).equals("56"))){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:读取PP60命令(F指令)返回失败(%s,%s)", Tools.getLineInfo(), 
						ISOUtils.hexString(recvBuf, 5, 2),ISOUtils.hexString(recvBuf, 9, 1));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//X指令end
		
		//case9.3:J指令，与I指令相比增加了显示一行指定字符的功能，只测J1
		test_PP60_cmd('C',null);
		cmdData = dataPack_NL829ToPP60('J',ISOUtils.hex2byte("31"+CARDNO+"92939495"));
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") ){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:PP60命令包(F指令)转发失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(recvBuf, 5, 2));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("应在第1行显示\"欢迎光临\",请输入11个1并按确认键,完成后按任意键继续");
		//期望的返回值为2位长度+16字节密文(转换前是8字节)
		cmdData = dataPack_PP60ToNL829(0,LEN_LENGTH+16);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") || (recvBuf.length==12 && ISOUtils.hexString(recvBuf, 9, 1).equals("55"))){
				gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:读取PP60命令(F指令)返回失败(%s,%s)", Tools.getLineInfo(), 
						ISOUtils.hexString(recvBuf, 5, 2),ISOUtils.hexString(recvBuf, 9, 1));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//校验
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(!Tools.memcmp(result2, PP60_decode(codeBuf), 8)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:J指令输入PIN校验失败(%s)", Tools.getLineInfo(), Dump.getHexDump(PP60_decode(codeBuf)));
		}
		//J1指令end
		
		//case10.1:H0指令，传入字符串做与工作密钥做des加密，需要G指令读取
		String key = ISOUtils.hexString(PP60_code(ISOUtils.hex2byte("3031323334353637")),16);
		if((ret = test_PP60_cmd('H',ISOUtils.hex2byte("3010"+key))) != PP60_SUCC){ //长度为0x10,字符串为ASCII码3031323334353637
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//G指令读取,期望的返回值为2位长度+16字节密文(转换前是8字节)
		if((ret = test_PP60_cmd('G',null)) != (LEN_LENGTH+16)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//校验
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(!Tools.memcmp(PP60_decode(codeBuf), ISOUtils.hex2byte(""), 8)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:H0指令校验失败(%s)", Tools.getLineInfo(), Dump.getHexDump(PP60_decode(codeBuf)));
		}
		
		//case10.2:H1指令，传入字符串做与工作密钥做des加密，直接返回密文
		if((ret = test_PP60_cmd('H',ISOUtils.hex2byte("3110"+key))) != LEN_LENGTH+16){ //长度为0x10,字符串为ASCII码3031323334353637
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//校验
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(!Tools.memcmp(PP60_decode(codeBuf), ISOUtils.hex2byte("88BE04DCABEB0C65"), 8)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:H1指令校验失败(%s)", Tools.getLineInfo(), Dump.getHexDump(PP60_decode(codeBuf)));
		}
		
		//case11:S指令,修改密码键盘主密钥号为M用户密钥号为N的用户密钥,修改1号主密钥的0号用户密钥为8个0x31,需要传入8个0x31经过1号主密钥加密的结果041619B0794CF560
		key = ISOUtils.hexString(PP60_code(ISOUtils.hex2byte("041619B0794CF560")),16);
		if((ret = test_PP60_cmd('S',ISOUtils.hex2byte("0100"+"10"+key))) != PP60_SUCC){ //M为0x01,N为0x00,长度为0x10
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
		}
		
		//case12:T指令,MAC计算
		String macStr = ISOUtils.hexString(PP60_code(new byte[]{0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39}),18);
		//方法一,X99
		if((ret = test_PP60_cmd('T',ISOUtils.hex2byte("00"+macStr))) != LEN_LENGTH+16){ //M为0x01,N为0x00,长度为0x10
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//校验
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(!Tools.memcmp(PP60_decode(codeBuf), ISOUtils.hex2byte("40A9255932E487F0"), 8)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:T指令校验失败(%s)", Tools.getLineInfo(), Dump.getHexDump(PP60_decode(codeBuf)));
		}
		//方法二,MAC9606
		if((ret = test_PP60_cmd('T',ISOUtils.hex2byte("01"+macStr))) != LEN_LENGTH+16){ //M为0x01,N为0x00,长度为0x10
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//校验
		System.arraycopy(recvBuf,11,codeBuf,0,16);
		if(!Tools.memcmp(PP60_decode(codeBuf), ISOUtils.hex2byte("CB80E49D39F140B4"), 8)){
			gui.cls_show_msg1_record(TAG, "getPinFunc_PP60", g_keeptime,"line %d:T指令校验失败(%s)", Tools.getLineInfo(), Dump.getHexDump(PP60_decode(codeBuf)));
		}
		//T指令end
		
		//后置
		test_PP60_cmd('Z',null);
		//R指令，重置第0、1组主,用密钥,重置后主密钥16个0x38，用户密钥16个0x00
		test_PP60_cmd('R',ISOUtils.hex2byte("4141"));
		test_PP60_cmd('R',ISOUtils.hex2byte("4142"));
		portClose();
		gui.cls_show_msg1_record(TAG,"getPinFunc_PP60",g_time_0,"密钥及密码输入功能测试通过");
	}
	
	
	// 外接密码键盘压力测试
	private void keyBoard_press()  
	{
		/*private & local definition*/
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
		
		//10位以上的密码会返回错误
		String[] pinNumber={"1234","12345","123456","1234567","12345678","123456789"/*,"1234567890","12345678901","123456789012"*/};
		String[] pinResult={"4642434249494A43424249424750444B","434E41484249464F4F46444B45454549",
				"5042464C414E48424150444C45454B46","4A414D454846504D4B424C4B494A4944","444C434D484D484F4749434A4B4F4E4B",
				"444C434D484D484F4749434A4B4F4E4B50464D494B45414943444A47414F4747",
				"444C434D484D484F4749434A4B4F4E4B41454E4E424A4A4A4B4E4D44414A4743",
				"444C434D484D484F4749434A4B4F4E4B484A4C504C4E50484B42414F4F4C494E",
				"444C434D484D484F4749434A4B4F4E4B454141484E50444C48424243434F4F42"};
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
		//前置操作
		//R指令，重置第0组主,用密钥,主密钥16个0x38，用户密钥16个0x00
		if((ret = test_PP60_cmd('R',ISOUtils.hex2byte("4141"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			return;
		}
		//M指令，修改0号主密钥为16个0x31
		if((ret = test_PP60_cmd('M',ISOUtils.hex2byte("00" + KEY_0x38 + KEY_0x31))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			return;
		}
		//U指令，修改0号主密钥的0号用户密钥为16个0x38
		if((ret = test_PP60_cmd('U',ISOUtils.hex2byte("0000" + KEY_0x31 + KEY_0x38))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			return;
		}
		//A指令，激活第0组主密钥第0号工作密钥，DES方式
		if((ret = test_PP60_cmd('A',ISOUtils.hex2byte("0000"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			return;
		}
		
		while(cnt>0){
			if(gui.cls_show_msg1(2,"压力测试中...\n还剩%d次（已成功%d次）,[取消]退出测试...",cnt,succ)==ESC)
				break;
			Random rand = new Random();
			randNumber = rand.nextInt(pinNumber.length);
			LoggerUtil.e(""+randNumber);
			cnt--;
			str.delete(0, str.length());
			
			str.append("请尽快在外接密码键盘输入");
			str.append(pinNumber[randNumber]);
			str.append("并确认,完成后按任意键继续");
			
			//C指令，清空屏幕
			if((ret = test_PP60_cmd('C',null)) != PP60_SUCC){
				gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:C清屏指令测试失败(0x%x)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			
			//L、N指令，设置输入密码的最长最小位，这里限制为随机到的密码的位数
			String pinLength = Integer.toString(pinNumber[randNumber].length());
			if((ret = test_PP60_cmd('L',ISOUtils.hex2byte(pinLength))) != PP60_SUCC){
				gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:L设置最大密码位数测试失败(0x%x)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			if((ret = test_PP60_cmd('N',ISOUtils.hex2byte(pinLength))) != PP60_SUCC){
				gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:N设置最小密码位数测试失败(0x%x)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			//F指令，输入密码,直接返回密文
			cmdData = dataPack_NL829ToPP60('F',ISOUtils.hex2byte("30"));
			if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
				if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") ){
					gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:PP60命令包(F指令)转发失败(%s)", Tools.getLineInfo(), bak-cnt, ISOUtils.hexString(recvBuf, 5, 2));
					continue;
				}
			} else{
				gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:测试失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			gui.cls_show_msg(str.toString());
			//期望的返回值为2位长度+16字节或32字节密文(转换前是8字节或16字节)
			codeLen = pinNumber[randNumber].length() > 8 ? 32 : 16;
			cmdData = dataPack_PP60ToNL829(0,LEN_LENGTH+codeLen);
			if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
				if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") || (recvBuf.length==12 && ISOUtils.hexString(recvBuf, 9, 1).equals("55"))){
					gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:读取PP60命令(F指令)返回失败(%s,%s)", bak-cnt, Tools.getLineInfo(), 
							ISOUtils.hexString(recvBuf, 5, 2),ISOUtils.hexString(recvBuf, 9, 1));
					continue;
				}
			} else{
				gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:测试失败(%d)", Tools.getLineInfo(), bak-cnt, ret);
				continue;
			}
			//取返回值中的16字节或32字节密文进行校验
			String codeStr = ISOUtils.hexString(recvBuf, 11, codeLen);
			if(!codeStr.equals(pinResult[randNumber])){
				gui.cls_show_msg1_record(TAG, "keyBoard_press", g_keeptime,"line %d:第%d次:校验失败(PinNumber = %s, PinDesOut = %s)", 
						Tools.getLineInfo(), bak-cnt, pinNumber[randNumber],codeStr);
				continue;
			}
			succ++;
		}
		//后置
		test_PP60_cmd('Z',null);
		//R指令，重置第0组主,用密钥,重置后主密钥16个0x38，用户密钥16个0x00
		test_PP60_cmd('R',ISOUtils.hex2byte("4141"));
		portClose();
	}
	
	//PP60指令屏幕显示及其他功能
	private void otherFunc_PP60()  
	{
		/*private & local definition*/
		int ret = -1;
		byte[] cmdData; 
		Random random = new Random();
		
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		gui.cls_show_msg1(1, "屏幕显示测试...");
		//case1:C指令，清空屏幕
		if((ret = test_PP60_cmd('C',null)) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘屏幕是否已清空,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:清空指令测试失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case2:D指令，在指定行显示字符串
		int line = random.nextInt(7)+1;
		if((ret = test_PP60_cmd('D',ISOUtils.hex2byte((line+30)+"8081828384"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘屏幕是否在第%d行显示\"请输入密码\",是[确认],否[其他]",line) != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:在指定行显示字符串测试失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case3:P指令，在第2行明文显示字符串，并接收确认键或取消键
		gui.cls_show_msg("即将在外接密码键盘屏幕第2行显示\"您的可用总积分\",请在看到显示后尽快按键盘的[确认]键，任意键继续");
		test_PP60_cmd('C',null);
		if((ret = test_PP60_cmd('P',ISOUtils.hex2byte("8889AEAFB1B2B3"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("即将在外接密码键盘屏幕第2行显示\"abcXYZ123-\",请在看到显示后尽快按键盘的[取消]键，任意键继续"); 
		test_PP60_cmd('C',null);
		if((ret = test_PP60_cmd('P',ISOUtils.hex2byte("61626358595A3132332D"))) != PP60_ERR){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case4:W指令,下载LOGO显示点阵
		cmdData = new byte[logo.length*2+2];
		cmdData[0] = 0x00;//从左边开始点阵数据显示的起始位置
		cmdData[1] = 0x01;//上面一行1，底下一行0
		System.arraycopy(PP60_code(logo), 0, cmdData, 2,logo.length*2);
		LoggerUtil.e(cmdData.length+"");
		if((ret = test_PP60_cmd('W',ISOUtils.trim(cmdData, logo.length+2))) != PP60_ERR){//上下行权限：要先修改下行才能修改上行，先修改上行会失败
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		cmdData[1] = 0x00;//底下一行0
		if((ret = test_PP60_cmd('W',ISOUtils.trim(cmdData, logo.length+2))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		cmdData[1] = 0x01;//上面一行1
		if((ret = test_PP60_cmd('W',ISOUtils.trim(cmdData, logo.length+2))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘屏幕是否花屏,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:下载LOGO显示点阵测试失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		cmdData[1] = 0x00;//底下一行0
		if((ret = test_PP60_cmd('W',cmdData)) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		cmdData[1] = 0x01;//上面一行1
		if((ret = test_PP60_cmd('W',cmdData)) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘屏幕是否显示两行\"中国银联\",是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:下载LOGO显示点阵测试失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//W指令end
		
		//case5:h指令,按行以点阵显示ASCII或汉字
		line = random.nextInt(7)+1;
		test_PP60_cmd('C',null);
		if((ret = test_PP60_cmd('h',ISOUtils.hex2byte(String.format("%02d%s", line,bqing_dot)))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘屏幕是否在第%d行显示\"b请\",是[确认],否[其他]",line) != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:按行以点阵显示字符测试失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case6:Z指令，复位显示
		if((ret = test_PP60_cmd('Z',null)) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘屏幕显示是否已复位,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:复位显示指令测试失败", Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		//case7:s指令，播发语音
		for(int i = 0;i<10;i++){
			if((ret = test_PP60_cmd('s',ISOUtils.intToBytes(i,1,true))) != PP60_SUCC){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			if(gui.cls_show_msg("外接密码键盘是否播报语音,语音编号%d,是[确认],否[其他]",i) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:播发语音测试失败", Tools.getLineInfo());
				break;
			}
		}
		
		//case8:V指令,取版本信息
		ret = test_PP60_cmd('V',null);
		if(ret == PP60_ERR || ret <0){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("外接密码键盘版本号为%s,是[确认],否[其他]",ISOUtils.dumpString(recvBuf, 9, ret)) != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:获取版本号测试失败", Tools.getLineInfo());
		}
		
		//case9:x指令,机器序列号下载与读取，序列号只能下载一次，只测试x2读取指令
		int len = 0;
		byte[] pnsn; 
		if((ret = test_PP60_cmd('x',ISOUtils.hex2byte("32"))) == PP60_ERR){
			if(gui.cls_show_msg("外接密码键盘未下载序列号SN,是[确认],否[其他]") != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:获取序列号SN测试失败", Tools.getLineInfo());
			}
		} else if(ret>12){
			len = ISOUtils.hexInt(recvBuf, 10, 1);
			pnsn = new byte[len];
			System.arraycopy(recvBuf, 11, pnsn, 0,len);
			if(gui.cls_show_msg("外接密码键盘序列号SN为%s,是[确认],否[其他]",ISOUtils.dumpString(PP60_decode(pnsn), 0, len/2)) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:获取序列号SN测试失败", Tools.getLineInfo());
			}
		}
		
		//case9:p指令,机器机号下载与读取，机号只能下载一次，只测试p2读取指令
		if((ret = test_PP60_cmd('p',ISOUtils.hex2byte("32"))) == PP60_ERR){
			if(gui.cls_show_msg("外接密码键盘未下载机号PN,是[确认],否[其他]") != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:获取机号PN测试失败", Tools.getLineInfo());
			}
		} else if(ret>12){
			len = ISOUtils.hexInt(recvBuf, 10, 1);
			pnsn = new byte[len];
			System.arraycopy(recvBuf, 11, pnsn, 0,len);
			if(gui.cls_show_msg("外接密码键盘机号PN为%s,是[确认],否[其他]",ISOUtils.dumpString(PP60_decode(pnsn), 0, len/2)) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:获取机号PN测试失败", Tools.getLineInfo());
			}
		}
		
		//后置
		test_PP60_cmd('Z',null);
		portClose();
		gui.cls_show_msg1_record(TAG,"otherFunc_PP60",g_time_0,"屏幕显示及其他功能测试通过");
	}
	
	//非卡类功能测试
	private void otherFunc_NL829()  
	{
		/*private & local definition*/
		int ret = -1;
		Map<String, String> keyTable = new HashMap<String, String>();
		keyTable.put("0D","确认");
		keyTable.put("0A","退格");
		keyTable.put("1B","取消");
		keyTable.put("01","F1/向上箭头");
		keyTable.put("02","F2/菜单");
		keyTable.put("03","F3/向下箭头");
		String key,keyValue;
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		//case1:查看NL-829mini软件版本(0x31 0x10)
		gui.cls_printf("获取版本测试...".getBytes());
		if((ret = test_NL829_cmd(new byte[]{0x31,0x10},null)) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取版本指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		} else{
			if(gui.cls_show_msg("NL-829mini软件版本号为%s,是[确认],否[其他]",ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC)) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:软件版本号获取错误(%s)", Tools.getLineInfo(),ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC));
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case2:按键相关,开始接收按键(0x31 0x25),获取一个按键值(0x31 0x26),结束接收按键(0x31 0x27)
		gui.cls_printf("按键测试...".getBytes());
		//开始接收
		if((ret = test_NL829_cmd(new byte[]{0x31,0x25},null)) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:开始接收按键指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//接收一个键值
		gui.cls_show_msg("请在密码键盘按下任意按键后,点击屏幕任意键继续");
		if((ret = test_NL829_cmd(new byte[]{0x31,0x26},null)) == NL829_SUCC){
			key = ISOUtils.hexString(recvBuf, 5, 1);
			keyValue = keyTable.containsKey(key) ? keyTable.get(key) : ISOUtils.dumpString(recvBuf, 5, 1);
			if(gui.cls_show_msg("获取到的键为%s,是[确认],否[其他]",keyValue) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取键值错误(%s)", Tools.getLineInfo(),ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else if(ret == NL829_ERR){
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:未接收到按键键值", Tools.getLineInfo());
		} else{
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:接收一个键值指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//结束接收
		if((ret = test_NL829_cmd(new byte[]{0x31,0x27},null)) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:结束接收按键指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case3:蜂鸣器控制(0x31 0x13)
		gui.cls_printf("蜂鸣器控制测试...".getBytes());
		if((ret = test_NL829_cmd(new byte[]{0x31,0x13},ISOUtils.hex2byte("000f03"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:蜂鸣器控制指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		} else{
			if(gui.cls_show_msg("该外设应发出3声beep,是[确认],否[其他]") != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:蜂鸣器控制测试失败", Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		//case4:LED控制(0x31 0x14/0x16)
		gui.cls_printf("LED灯控制测试...".getBytes());
		byte[] led = new byte[]{(byte) 0x8f,0x4f,0x2f,0x1f,(byte) 0xff,0x0f};//bit7~bit4分别对应绿、红、黄、蓝灯控制，这六个byte分别控制绿、红、黄、蓝、全亮、全灭
		gui.cls_show_msg1(2,"LED控制亮灭测试,LED将按绿、红、黄、蓝顺序逐一亮灭后全亮全灭...");
		for(int i = 0;i<led.length;i++){
			if((ret = test_NL829_cmd(new byte[]{0x31,0x14},new byte[]{led[i]})) == NL829_ERR){
				gui.cls_show_msg1(1,"line %d:该机型无LED灯");
				break;
			}
			SystemClock.sleep(2000);
		}
		gui.cls_show_msg1(2,"LED控制闪烁测试,LED将按绿、红、黄、蓝顺序逐一闪烁后全闪全灭...");
		for(int i = 0;i<led.length;i++){
			if((ret = test_NL829_cmd(new byte[]{0x31,0x16},new byte[]{led[i]})) != NL829_SUCC){
				gui.cls_show_msg1(1,"line %d:该机型无LED灯");
				break;
			}
			SystemClock.sleep(2000);
		}
		if(gui.cls_show_msg("该外设的LED灯状态是否与预期相符,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "otherFunc_PP60", g_keeptime,"line %d:LED灯状态控制测试失败", Tools.getLineInfo());
		}
		
		//case5:设备(NL-892/MR)序列号下载(0x31 0x20)与设备(NL-892/MR)序列号读取(0x31 0x21),设备只能下载一次SN/PN，故只测读取
		//获取SN
		gui.cls_printf("获取SN/PN测试...".getBytes());
		if((ret = test_NL829_cmd(new byte[]{0x31,0x21},ISOUtils.hex2byte(CHECKSIGN+"00"))) == NL829_SUCC){
			if(gui.cls_show_msg("该外设的序列号SN为%s,是[确认],否[其他]",ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC)) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取序列号SN测试失败(%s)", Tools.getLineInfo(),ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else if(ret == 0x0002){
			if(gui.cls_show_msg("该外设未下载序列号SN,是[确认],否[其他]") != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取序列号SN测试失败", Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取SN指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//获取PN
		if((ret = test_NL829_cmd(new byte[]{0x31,0x21},ISOUtils.hex2byte(CHECKSIGN+"01"))) == NL829_SUCC){
			if(gui.cls_show_msg("该外设的机号PN为%s,是[确认],否[其他]",ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC)) != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取机号PN测试失败(%s)", Tools.getLineInfo(),ISOUtils.dumpString(recvBuf, 5, recvBuf.length-LEN_RECV_STATIC));
				if (!GlobalVariable.isContinue)
					return;
			}
		} else if(ret == 0x0002){
			if(gui.cls_show_msg("该外设未下载机号PN,是[确认],否[其他]") != ENTER){
				gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取机号PN测试失败", Tools.getLineInfo());
				if (!GlobalVariable.isContinue)
					return;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "otherFunc_NL829", g_keeptime,"line %d:获取PN指令执行失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case6 加载与显示图片(0x31 0x24)
		testShowImage();
		
		//后置
		test_PP60_cmd('Z',null);
		portClose();
		gui.cls_show_msg1_record(TAG,"otherFunc_NL829",g_time_0,"非卡类功能测试通过");
	}
	
	//M1卡测试，用3个流程把相关指令都测试到
	private void rf_M1_NL829()
	{
		/*private & local definition*/
		String BLK02DATA_ORI = "6745230198badcfe6745230102fd02fd";
		String BLK02DATA_INC1 = "6845230197badcfe6845230102fd02fd";
		String KEY_A = "60";
		String KEY_B = "61";
		String AUTHKEY = "ffffffffffff";
		byte [] buf = new byte[16];
		int ret = -1,len1,len2;
		String REQA = "26";
		String WUPA = "52";
		String SNR;
		
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		//完全关闭场
		test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("ffff"));
		
		/**流程1，寻卡-外部认证-块写块读-增量减量*/
		gui.cls_show_msg("请确保NL829感应区有1张M-1卡,按任意键继续...");
		//激活，包括 "寻卡-防冲突-选卡"，(0x33 0x21),超时时间10s(000a)
		if((ret = test_NL829_cmd(new byte[]{0x33,0x21},ISOUtils.hex2byte(REQA+"000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡激活指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//激活成功后，会接收到寻卡返回数据ATQ、防冲突返回数据SNR、选卡返回数据SAK，后续认证操作需要取SNR作为参数
		len1 = ISOUtils.hexInt(recvBuf, 5, 1);//计算ATQ的数据长度
		len2 = ISOUtils.hexInt(recvBuf, 6+len1, 1);//计算SNR的数据长度
		SNR = ISOUtils.hexString(recvBuf, 7+len1, len2);
		
		//外部认证(0x33 0x26),用keyA认证01块
		if((ret = test_NL829_cmd(new byte[]{0x33,0x26},ISOUtils.hex2byte(KEY_A + SNR + "01" + AUTHKEY))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡外部认证指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//写块数据(0x33 0x28)，在01块写符合钱包格式的数据，作为增量减量操作的前置
		if((ret = test_NL829_cmd(new byte[]{0x33,0x28},ISOUtils.hex2byte("01" + BLK02DATA_ORI))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡写块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//读块数据(0x33 0x27)，读01块
		if((ret = test_NL829_cmd(new byte[]{0x33,0x27},ISOUtils.hex2byte("01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//读校验
		System.arraycopy(recvBuf,5,buf,0,16);
		if(!Tools.memcmp(buf, ISOUtils.hex2byte(BLK02DATA_ORI),16)){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(buf));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//增量操作(0x33 0x29),对01块做增量操作
		if((ret = test_NL829_cmd(new byte[]{0x33,0x29},ISOUtils.hex2byte("01"+"01000000"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡增量操作指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//读数据校验增量是否成功
		if((ret = test_NL829_cmd(new byte[]{0x33,0x27},ISOUtils.hex2byte("01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		System.arraycopy(recvBuf,5,buf,0,16);
		if(!Tools.memcmp(buf, ISOUtils.hex2byte(BLK02DATA_INC1),16)){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(buf));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//减量操作(0x33 0x2a),对01块做减量操作
		if((ret = test_NL829_cmd(new byte[]{0x33,0x2a},ISOUtils.hex2byte("01"+"01000000"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡减量操作指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//读数据校验增量是否成功
		if((ret = test_NL829_cmd(new byte[]{0x33,0x27},ISOUtils.hex2byte("01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		System.arraycopy(recvBuf,5,buf,0,16);
		if(!Tools.memcmp(buf, ISOUtils.hex2byte(BLK02DATA_ORI),16)){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(buf));
			if (!GlobalVariable.isContinue)
				return;
		}
		//下电一下，10ms
		if((ret = test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:关闭场指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("请拿开M-1卡,按任意键继续...");
		
		/**流程2，寻卡-存储密钥-加载密钥-使用加载密钥认证-块写块读*/
		gui.cls_show_msg("请确保NL829感应区有1张M-1卡,按任意键继续...");
		//激活，包括 "寻卡-防冲突-选卡"，(0x33 0x21),超时时间10s(000a)
		if((ret = test_NL829_cmd(new byte[]{0x33,0x21},ISOUtils.hex2byte(REQA+"000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡激活指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//激活成功后，会接收到寻卡返回数据ATQ、防冲突返回数据SNR、选卡返回数据SAK，后续认证操作需要取SNR作为参数
		len1 = ISOUtils.hexInt(recvBuf, 5, 1);//计算ATQ的数据长度
		len2 = ISOUtils.hexInt(recvBuf, 6+len1, 1);//计算SNR的数据长度
		SNR = ISOUtils.hexString(recvBuf, 7+len1, len2);
		
		//存储密钥(0x33 0x23),存储keyB
		if((ret = test_NL829_cmd(new byte[]{0x33,0x23},ISOUtils.hex2byte(KEY_B + "00" + AUTHKEY))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡存储密钥指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//加载密钥(0x33 0x24),装载keyB
		if((ret = test_NL829_cmd(new byte[]{0x33,0x24},ISOUtils.hex2byte(KEY_B + "00"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡加载密钥指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//内部认证(0x33 0x25),用装载的keyBA认证01块
		if((ret = test_NL829_cmd(new byte[]{0x33,0x25},ISOUtils.hex2byte(KEY_B + SNR + "01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡内部认证指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//写块数据(0x33 0x28)，在01块写符合钱包格式的数据，作为增量减量操作的前置
		if((ret = test_NL829_cmd(new byte[]{0x33,0x28},ISOUtils.hex2byte("01" + BLK02DATA_ORI))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡写块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//读块数据(0x33 0x27)，读01块
		if((ret = test_NL829_cmd(new byte[]{0x33,0x27},ISOUtils.hex2byte("01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//读校验
		System.arraycopy(recvBuf,5,buf,0,16);
		if(!Tools.memcmp(buf, ISOUtils.hex2byte(BLK02DATA_ORI),16)){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(buf));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//下电一下，10ms
		if((ret = test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:关闭场指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		/**流程3，REQA寻卡-Halt指令中止-WUPA寻卡重新唤醒-外部认证-块读*/
		//REQA激活，包括 "寻卡-防冲突-选卡"，(0x33 0x21),超时时间10s(000a)
		if((ret = test_NL829_cmd(new byte[]{0x33,0x21},ISOUtils.hex2byte(REQA+"000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡激活指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//Halt 指令(0x33 0x22),不关闭天线，使卡进入HALT状态。可以用WUPA寻卡命令重新唤醒
		if((ret = test_NL829_cmd(new byte[]{0x33,0x22},null)) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡Halt指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//WUPA激活，包括 "寻卡-防冲突-选卡"，(0x33 0x21),超时时间10s(000a)
		if((ret = test_NL829_cmd(new byte[]{0x33,0x21},ISOUtils.hex2byte(WUPA+"000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡激活指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//激活成功后，会接收到寻卡返回数据ATQ、防冲突返回数据SNR、选卡返回数据SAK，后续认证操作需要取SNR作为参数
		len1 = ISOUtils.hexInt(recvBuf, 5, 1);//计算ATQ的数据长度
		len2 = ISOUtils.hexInt(recvBuf, 6+len1, 1);//计算SNR的数据长度
		SNR = ISOUtils.hexString(recvBuf, 7+len1, len2);
		
		//内部认证(0x33 0x25),用前面装载的keyBA认证01块
		if((ret = test_NL829_cmd(new byte[]{0x33,0x25},ISOUtils.hex2byte(KEY_B + SNR + "01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡内部认证指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//读块数据(0x33 0x27)，读01块，确认是否激活认证成功
		if((ret = test_NL829_cmd(new byte[]{0x33,0x27},ISOUtils.hex2byte("01"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//读校验
		System.arraycopy(recvBuf,5,buf,0,16);
		if(!Tools.memcmp(buf, ISOUtils.hex2byte(BLK02DATA_ORI),16)){
			gui.cls_show_msg1_record(TAG, "rf_M1_NL829", g_keeptime,"line %d:M1卡读块数据指令校验失败(%s)", Tools.getLineInfo(), ISOUtils.hexString(buf));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//后置，完全关闭场
		test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("ffff"));
		gui.cls_show_msg1_record(TAG,"rf_M1_NL829",g_time_0,"M1卡测试通过");
	}
	
	//非接CPU卡测试，用几个流程把相关指令都测试到
	private void rf_CPU_NL829() 
	{
		/*private & local definition*/
		ArrayList<_SMART_t> typelist = new ArrayList<_SMART_t>();
		typelist.add(_SMART_t.CPU_A);
		typelist.add(_SMART_t.CPU_B);
		String apdustr = "0084000008";//取随机数指令
		String REQA = "26";
		String apduRecv;
		int ret = -1,len1,len2,len3;
		byte[] SAK;
		
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接!任意键继续");
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		//完全关闭场
		test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("ffff"));
		
		/**A、B卡激活读写流程*/
		for(_SMART_t type : typelist ){
			gui.cls_show_msg("请确保NL829感应区有1张%s卡,按任意键继续...",type);
			//射频卡寻卡(0x32 0x24)，超时等待10s(000a)
			if((ret = test_NL829_cmd(new byte[]{0x32,0x24},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
				gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡寻卡指令测试失败(%04x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			
			//接触、非接触卡APDU命令(0x32 0x26)，0xff为非接触式IC卡
			if((ret = test_NL829_cmd(new byte[]{0x32,0x26},ISOUtils.hex2byte("ff"+apdustr))) != NL829_SUCC){
				gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡APDU指令测试失败(%04x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			apduRecv = ISOUtils.hexString(recvBuf, recvBuf.length-4, 2);
			if(!apduRecv.equals("9000")&& !apduRecv.equals("6D00"))
			{
				gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡APDU指令校验失败(%s)", Tools.getLineInfo(), apduRecv);
			}
			
			//等待射频卡移开(0x32 0x25)，超时等待1s(0001),未移开卡应返回0x3006超时
			if((ret = test_NL829_cmd(new byte[]{0x32,0x25},ISOUtils.hex2byte("0001"))) != 0x3006){
				gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:等待射频卡移开指令超时测试失败(%04x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			gui.cls_show_msg("请移开感应区的%s卡,按任意键继续...",type);
			//等待射频卡移开(0x32 0x25)，超时等待10s(000a)
			if((ret = test_NL829_cmd(new byte[]{0x32,0x25},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
				gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:等待射频卡移开指令测试失败(%04x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			
			//关闭场(0x32,0x27)，下电10ms
			if((ret = test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
				gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:关闭场指令测试失败(%04x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		/**在M1卡寻卡防冲突和选卡的基础上激活A卡进行读写操作*/
		gui.cls_show_msg("请确保NL829感应区有1张A卡,按任意键继续...");
		//激活，包括 "寻卡-防冲突-选卡"，(0x33 0x21),超时时间10s(000a)
		if((ret = test_NL829_cmd(new byte[]{0x33,0x21},ISOUtils.hex2byte(REQA+"001e"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:M1卡激活指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//激活成功后，会接收到寻卡返回数据ATQ、防冲突返回数据SNR、选卡返回数据SAK，后续认证操作需要取SNR作为参数
		len1 = ISOUtils.hexInt(recvBuf, 5, 1);//计算ATQ的数据长度
		len2 = ISOUtils.hexInt(recvBuf, 6+len1, 1);//计算SNR的数据长度
		len3 = ISOUtils.hexInt(recvBuf, 7+len1+len2, 1);//计算SAK的数据长度
		SAK = new byte[len3];
		System.arraycopy(recvBuf,8+len1+len2,SAK,0,len3);
		
		//根据pssakbuf 值判断是A卡还是M卡若是A卡执行获取A卡的ATS,bit5为1时可判断为A卡,然后执行A卡激活 apdu
		if((SAK[0] & 0x20) != 0x20){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:测试失败,未检测到A卡(%s)", Tools.getLineInfo(),ISOUtils.hexString(SAK));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//RATS指令获取ats值(0x32 0x28)
		if((ret = test_NL829_cmd(new byte[]{0x32,0x28},ISOUtils.hex2byte("00"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:RATS指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//接触、非接触卡APDU命令(0x32 0x26)，0xff为非接触式IC卡
		if((ret = test_NL829_cmd(new byte[]{0x32,0x26},ISOUtils.hex2byte("ff"+apdustr))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡APDU指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		apduRecv = ISOUtils.hexString(recvBuf, recvBuf.length-4, 2);
		if(!apduRecv.equals("9000")&& !apduRecv.equals("6D00"))
		{
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡APDU指令校验失败(%s)", Tools.getLineInfo(), apduRecv);
		}
		//关闭场(0x32,0x27)，下电10ms
		if((ret = test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:关闭场指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		/**射频卡寻卡和按键输入轮询测试*/
		//射频卡寻卡和按键输入轮询指令(0x32 0x29)
		if((ret = test_NL829_cmd(new byte[]{0x32,0x29},ISOUtils.hex2byte("000a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡寻卡和按键输入轮询指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//接触、非接触卡APDU命令(0x32 0x26)，0xff为非接触式IC卡
		if((ret = test_NL829_cmd(new byte[]{0x32,0x26},ISOUtils.hex2byte("ff"+apdustr))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡APDU指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		apduRecv = ISOUtils.hexString(recvBuf, recvBuf.length-4, 2);
		if(!apduRecv.equals("9000")&& !apduRecv.equals("6D00"))
		{
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡APDU指令校验失败(%s)", Tools.getLineInfo(), apduRecv);
		}
		
		gui.cls_show_msg("请移开感应区的A卡,按任意键继续...");
		//PP60的L、N指令，设置输入密码的最长最小位，这里限制为6位
		if((ret = test_PP60_cmd('L',ISOUtils.hex2byte("06"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret = test_PP60_cmd('N',ISOUtils.hex2byte("06"))) != PP60_SUCC){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:测试失败(0x%x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_printf("请在[30s内]在外接密码键盘输入123456并按确认键".getBytes());
		test_NL829_cmd(new byte[]{0x32,0x29},ISOUtils.hex2byte("001e"));
		if(!ISOUtils.hexString(recvBuf, 3, 1).equals("AA") || !ISOUtils.dumpString(recvBuf, 4, 6).equals("123456")){
			gui.cls_show_msg1_record(TAG, "rf_CPU_NL829", g_keeptime,"line %d:射频卡寻卡和按键输入轮询测试失败", Tools.getLineInfo());
		} 
		
		//后置
		test_PP60_cmd('Z',null);//恢复屏幕默认显示
		test_NL829_cmd(new byte[]{0x32,0x27},ISOUtils.hex2byte("ffff"));//完全关闭场
		gui.cls_show_msg1_record(TAG,"rf_CPU_NL829",g_time_0,"非接CPU卡测试通过");
	}
	
	//接触CPU卡测试
	private void icsam_NL829()
	{
		/*private & local definition*/
		_SMART_t[] typelist = {_SMART_t.SAM1,_SMART_t.SAM2};
		_SMART_t type;
		Map<_SMART_t, String> samMap = new HashMap<_SMART_t, String>();
		samMap.put(_SMART_t.SAM1,"10");
		samMap.put(_SMART_t.SAM2,"11");
		int cardType;
		String apdustr = "0084000008";//取随机数指令
		String apduRecv;
		/*process body*/
		int nkeyIn = gui.cls_show_msg("请确保POS与外接密码键盘已连接,配置\n0.SAM1卡\n1.SAM2卡\n");
		switch (nkeyIn) 
		{
		case '0':
		case '1':
			type = typelist[nkeyIn-'0'];
			gui.cls_show_msg1(1, "已配置为%s卡", type);
			break;

		default:
			type = typelist[0];
			gui.cls_show_msg1(1, "默认配置为SAM1卡");
			break;
		}
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		//SAM1卡下电
		if((ret = test_NL829_cmd(new byte[]{0x32,0x23},ISOUtils.hex2byte("10"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:SAM1卡下电指令测试失败(%04x)", Tools.getLineInfo(), ret);
			return;
		}
		//SAM2卡下电
		if((ret = test_NL829_cmd(new byte[]{0x32,0x23},ISOUtils.hex2byte("11"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:SAM2卡下电指令测试失败(%04x)", Tools.getLineInfo(), ret);
			return;
		}
		
		//case1:SAM卡激活读写流程
		//SAM/PSAM卡上电 (0x32 0x22)
		if((ret = test_NL829_cmd(new byte[]{0x32,0x22},ISOUtils.hex2byte("000000"+samMap.get(type)))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:%s卡上电指令测试失败(%04x)", Tools.getLineInfo(), type,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		cardType = ISOUtils.hexInt(recvBuf, 3, 1);
		if(cardType == NL829_CARD_T0 || cardType == NL829_CARD_T1 ){
			gui.cls_show_msg1(2, "%s(T%d)上电成功", type,cardType);
		}else{ //未知卡类型
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:上电指令获取卡类型错误(%02x)", Tools.getLineInfo(),cardType);
			if (!GlobalVariable.isContinue)
				return;
		}
		//接触、非接触卡APDU命令(0x32 0x26)
		if((ret = test_NL829_cmd(new byte[]{0x32,0x26},ISOUtils.hex2byte(samMap.get(type)+apdustr))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:射频卡APDU指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		apduRecv = ISOUtils.hexString(recvBuf, recvBuf.length-4, 2);
		if(!apduRecv.equals("9000")&& !apduRecv.equals("6D00"))
		{
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:射频卡APDU指令校验失败(%s)", Tools.getLineInfo(), apduRecv);
		}
		
		//SAM/PSAM卡下电 (0x32 0x23)
		if((ret = test_NL829_cmd(new byte[]{0x32,0x23},ISOUtils.hex2byte(samMap.get(type)))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "icsam_NL829", g_keeptime,"line %d:%s卡下电指令测试失败(%04x)", Tools.getLineInfo(),type, ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(TAG,"icsam_NL829",g_time_0,"接触CPU卡测试通过,配置的所有卡类型均需要测试");
	}
	
	//扫描头测试
	private void scan_NL829()
	{
		/*private & local definition*/
		int len = 0,ret = -1;
		
		/*process body*/
		gui.cls_show_msg("请确保POS与外接密码键盘已连接,且该外设支持扫描头,任意键继续");
		//前置，初始化串口
		if((ret = portOpen())!= NDK_OK){
			gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:%s串口初始化失败(%d)", Tools.getLineInfo(), comName[comValue-'0'],ret);
			portClose();
			return;
		}
		
		//case1:正常调用阻塞式扫描,实际扫描后应该能够返回成功,并获取到扫描数据,默认情况下支持Code39码的扫描
		//阻塞扫描头指令（0x31 0x22）
		gui.cls_show_msg("即将开始扫码,请在10秒内扫描Code39码,任意键继续");
		if((ret = test_NL829_cmd(new byte[]{0x31,0x22},ISOUtils.hex2byte("0a"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:阻塞式扫描指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		len = recvBuf.length-LEN_RECV_STATIC;//829指令返回帧的固定部分长度为7，减去7则是附加数据长度
		if(gui.cls_show_msg("扫描到的长度为:%d的内容:%s,是[确认],否[其他]",len,ISOUtils.dumpString(recvBuf, 5, len)) != ENTER){
			gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:阻塞式扫描指令结果错误(%s)", Tools.getLineInfo(),ISOUtils.dumpString(recvBuf, 5, len));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case2:正常调用非阻塞式扫描测试,扫描到的数据应该和预期一致
		while(true)
		{
			//扫描头开始扫描（0x31 0x17）
			if((ret = test_NL829_cmd(new byte[]{0x31,0x17},null)) != NL829_SUCC){
				gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:开始扫描指令测试失败(%04x)", Tools.getLineInfo(), ret);
				test_NL829_cmd(new byte[]{0x31,0x19},null);//关闭扫描头
				if (!GlobalVariable.isContinue)
					return;
			}
			if(gui.cls_show_msg1(20,"请在20秒内扫任意码,听到扫码成功的滴声后按任意键继续,[取消]退出")==ESC){
				test_NL829_cmd(new byte[]{0x31,0x19},null);//关闭扫描头
				break;
			}
			//获取扫描头条码数据（0x31 0x18）
			if((ret = test_NL829_cmd(new byte[]{0x31,0x18},null)) == NL829_ERR){
				gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:正在扫描或未获取到扫码数据(%04x)", Tools.getLineInfo(), ret);
				test_NL829_cmd(new byte[]{0x31,0x19},null);//关闭扫描头
				continue;
			} else if(ret == 0x0002){
				gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:设备不支持扫描头(%04x)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			len = recvBuf.length-LEN_RECV_STATIC;//829指令返回帧的固定部分长度为7，减去7则是附加数据长度
			if(gui.cls_show_msg("扫描到的长度为:%d的内容:%s,是[确认],否[其他]",len,ISOUtils.dumpString(recvBuf, 5, len)) != ENTER){
				gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:非阻塞式扫描指令结果错误(%s)", Tools.getLineInfo(),ISOUtils.dumpString(recvBuf, 5, len));
			}
			//关闭扫描头（0x31 0x19）
			if((ret = test_NL829_cmd(new byte[]{0x31,0x19},null)) != NL829_SUCC){
				gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:关闭扫描头指令测试失败(%04x)", Tools.getLineInfo(), ret);
				break;
			}
		}
		
		//case3： 扫描中动画控制指令(0x31 0x28)
		testShowAnimation();
		
		//后置
		test_PP60_cmd('Z',null);//恢复屏幕默认显示
		gui.cls_show_msg1_record(TAG,"scan_NL829",g_time_0,"扫描头测试通过");
	}
	
	private void testShowImage(){
		/*private & local definition*/
		int x,y;
		int cnum,snum,num;
		int len;
		int w=48,h=51;//要显示的图片的长宽
		byte[] sdata;
		/*process body*/
		gui.cls_printf("签名板上连续显示图片测试...".getBytes());
		len = sCUPBMP48x51.length;
		int cnt = len/256;
		int value = len%256;
		if(value != 0)
			num=cnt;
		else
			num=cnt-1;	
		for(x=0,y=0;x<=320-48 & y<=240-51;x+=7,y+=7)
		{
			for(int i=0;i<cnt;i++)
			{
				cnum = i;
				snum = num - i;
				sdata = new byte[256];
				System.arraycopy(sCUPBMP48x51,256*i,sdata,0,256);
				if((ret=NL829_Show_Image(x,y,w,h,cnum,snum,sdata,256)) !=NL829_SUCC) {	
					gui.cls_show_msg1_record(TAG, "testShowImage", g_keeptime,"line %d:加载与显示图片指令测试失败(%04x)", Tools.getLineInfo(), ret);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if(value != 0)
			{
				cnum = cnt;
				snum = 0;
				sdata = new byte[value];
				System.arraycopy(sCUPBMP48x51,256*cnt,sdata,0,value);
				if((ret=NL829_Show_Image(x,y,w,h,cnum,snum,sdata,value)) !=NL829_SUCC) 
				{	
					gui.cls_show_msg1_record(TAG, "testShowImage", g_keeptime,"line %d:加载与显示图片指令测试失败(%04x)", Tools.getLineInfo(), ret);
					if (!GlobalVariable.isContinue)
						return;
				}
			}	
		}
		if(gui.cls_show_msg("签名板上是否连续显示正确的图片,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "testShowImage", g_keeptime,"line %d:加载显示图片测试失败", Tools.getLineInfo());
		}
	}
	
	private void testShowAnimation(){
		/*private & local definition*/
		byte[] dispaly_icons = new byte[]{0x01,0x02,0x03,0x04};
		int icon_num = dispaly_icons.length;
		
		/*process body*/
		gui.cls_show_msg1(1,"扫描中显示动画测试...");
		//case1采用阻塞式扫描，将扫描条码超时时间设为10s，开启动画，扫描过程中签名板上将显示动画
		if((ret = NL829_Scan_Animation((byte)0x01,icon_num,0,0, dispaly_icons)) != 0x0001){//0x0001这里表示动画开启
			if(ret == 0xfffd)
				gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:要显示的动画图片不存在(%04x)", Tools.getLineInfo(), ret);
			else
				gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:扫描动画控制指令测试失败(%04x)", Tools.getLineInfo(), ret);
			return;
		}
		if((ret = NL829_Scan_Animation((byte)0x02,icon_num,0,0, dispaly_icons)) != 0x0001)
		{
			gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:扫描动画控制指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("即将开始扫码,请在20秒内扫描Code39码,扫描过程中签名板上将显示动画,任意键继续");
		if((ret = test_NL829_cmd(new byte[]{0x31,0x22},ISOUtils.hex2byte("14"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:阻塞式扫描指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("扫描过程中签名板上是否显示动画,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:扫描动画控制测试失败", Tools.getLineInfo());
		}

		//case2:采用阻塞式扫描，将扫描条码超时时间设为10s，关闭动画，扫描过程中签名板上将不显示动画
		if((ret = NL829_Scan_Animation((byte)0x00,icon_num,0,0, dispaly_icons)) != 0x0000){//0x0000这里表示动画关闭
			gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:扫描动画控制指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret = NL829_Scan_Animation((byte)0x02,icon_num,0,0, dispaly_icons)) != 0x0000){
			gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:扫描动画控制指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg("即将开始扫码,请在20秒内扫描Code39码,扫描过程中签名板上将不显示动画,任意键继续");
		if((ret = test_NL829_cmd(new byte[]{0x31,0x22},ISOUtils.hex2byte("14"))) != NL829_SUCC){
			gui.cls_show_msg1_record(TAG, "scan_NL829", g_keeptime,"line %d:阻塞式扫描指令测试失败(%04x)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("扫描过程中签名板上是否不显示动画,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "testShowAnimation", g_keeptime,"line %d:扫描动画控制测试失败", Tools.getLineInfo());
		}
	}
	
	
	/**
	 * 封装与发送加载与显示图片指令(0x31 0x24)
	 * 
	 * @param bpsNum 0：9600BPS、1：19200BPS、2：38400BPS、3：57600BPS (开机默认)、4：115200BPS
	 * @return 
	 */
	private int NL829_SetBPS(int bpsNum){
		/*private & local definition*/
		byte[] cmd = new byte[]{0x30,0x01};//设置NL-829mini与上位机通讯的波特率(0x30 0x01)
		int offset = 0;
		byte[] cmdPack = new byte[LEN_CMD_NL829 + 1];
		
		/*process body*/
		//拼装指令
		System.arraycopy(cmd, 0, cmdPack, 0, LEN_CMD_NL829);
		offset += LEN_CMD_NL829;
		
		cmdPack[offset] = (byte)bpsNum;
		
		//发送指令
		if((ret = cmd_frame_factory(cmdPack,cmdPack.length)) == NDK_OK){
			return ISOUtils.hexInt(recvBuf, 3, 2);
		} else{
			return ret;
		}
	}
	
	/**
	 * 封装与发送加载与显示图片指令(0x31 0x24)
	 * 
	 * @param x 图片显示的左上角横坐标（像素）
	 * @param y 图片显示的左上角纵坐标（像素）
	 * @param width 图片的宽（像素）
	 * @param height 图片显示的高（像素）
	 * @param cnum 当前第几个包(00 00 开始)
	 * @param snum 后续还有几个包
	 * @param data 图片数据 (前面每包固定为256字节 最后一包按实际长度)
	 * @param datalen 图片数据长度
	 * @return 
	 */
	private int NL829_Show_Image(int x, int y, int width, int height, int cnum, int snum, byte[] data, int datalen){
		/*private & local definition*/
		byte[] cmd = new byte[]{0x31,0x24};//加载与显示图片
		int offset = 0;
		byte[] cmdPack = new byte[LEN_CMD_NL829 + 2*6 + datalen];
		
		/*process body*/
		//拼装指令
		System.arraycopy(cmd, 0, cmdPack, 0, LEN_CMD_NL829);
		offset += LEN_CMD_NL829;
		
		//拼装参数
		//转换左上角横坐标数据x为2字节byte
		byte[] xBuf = ISOUtils.intToBytes(x, 2, true);
		System.arraycopy(xBuf, 0, cmdPack, offset, 2);
		offset += 2;
		//转换左上角纵坐标数据y为2字节byte
		byte[] yBuf = ISOUtils.intToBytes(y, 2, true);
		System.arraycopy(yBuf, 0, cmdPack, offset, 2);
		offset += 2;
		//转换图片的宽width为2字节byte
		byte[] wBuf = ISOUtils.intToBytes(width, 2, true);
		System.arraycopy(wBuf, 0, cmdPack, offset, 2);
		offset += 2;
		//转换图片的宽width为2字节byte
		byte[] hBuf = ISOUtils.intToBytes(height, 2, true);
		System.arraycopy(hBuf, 0, cmdPack, offset, 2);
		offset += 2;
		//转换当前第几个包cnum为2字节byte
		byte[] cnumBuf = ISOUtils.intToBytes(cnum, 2, true);
		System.arraycopy(cnumBuf, 0, cmdPack, offset, 2);
		offset += 2;
		//转换后续还有几个包snum为2字节byte
		byte[] snumBuf = ISOUtils.intToBytes(snum, 2, true);
		System.arraycopy(snumBuf, 0, cmdPack, offset, 2);
		offset += 2;
		
		//拼装图片数据
		System.arraycopy(data, 0, cmdPack, offset, datalen);
		
		//发送指令
		if((ret = cmd_frame_factory(cmdPack,cmdPack.length)) == NDK_OK){
			return ISOUtils.hexInt(recvBuf, 3, 2);
		} else{
			gui.cls_show_msg1_record(TAG, "test_NL829_cmd", g_keeptime,"line %d:NL829指令传输失败(%s,%d)", Tools.getLineInfo(),ISOUtils.hexString(cmdPack, 0, 2), ret);
			return ret;
		}
	}
	
	/**
	 * 封装与发送扫描动画控制指令(0x31 0x28)
	 * 
	 * @param ctrl 开关动画。0x00关闭、0x01开启、0x02获取当前开关状态
	 * @param icon_num 动画张数，空间足够的话，最多可存256张
	 * @param speed每张图片显示时间。0x00 保持默认值
	 * @param displaytime 动画显示时间。0x00 0x00 没有时间限制，直到扫描成功。其他值：动画显示时间（n*0.1s）,到时间将关闭动画 
	 * @param dispaly_icons 要显示图片：如0x01 0x02
	 * @return 
	 */
	private int NL829_Scan_Animation(byte ctrl, int icon_num, int speed, int displaytime ,byte[] dispaly_icons){
		/*private & local definition*/
		byte[] cmd = new byte[]{0x31,0x28};//扫描动画控制指令(0x31 0x28)
		int offset = 0;
		byte[] cmdPack = new byte[LEN_CMD_NL829 + 1 + 1 + 1 + 2 + icon_num];
		
		/*process body*/
		//拼装指令
		System.arraycopy(cmd, 0, cmdPack, 0, LEN_CMD_NL829);
		offset += LEN_CMD_NL829;
		
		//拼装参数
		//开关动画命令
		cmdPack[offset] = ctrl;
		offset += 1;
		//转换动画张数icon_num为1字节byte
		byte[] iconNumBuf = ISOUtils.intToBytes(icon_num, 1, true);
		System.arraycopy(iconNumBuf, 0, cmdPack, offset, 1);
		offset += 1;
		//转换每张图片显示时间speed为1字节byte
		byte[] speedBuf = ISOUtils.intToBytes(speed, 1, true);
		System.arraycopy(speedBuf, 0, cmdPack, offset, 1);
		offset += 1;
		//转换动画显示时间displaytime为2字节byte
		byte[] displaytimeBuf = ISOUtils.intToBytes(speed, 2, true);
		System.arraycopy(displaytimeBuf, 0, cmdPack, offset, 2);
		offset += 2;
		
		//拼装要显示图片数据
		System.arraycopy(dispaly_icons, 0, cmdPack, offset, icon_num);
		
		//发送指令
		if((ret = cmd_frame_factory(cmdPack,cmdPack.length)) == NDK_OK){
			return ISOUtils.hexInt(recvBuf, 3, 2);
		} else{
			gui.cls_show_msg1_record(TAG, "test_NL829_cmd", g_keeptime,"line %d:NL829指令传输失败(%s,%d)", Tools.getLineInfo(),ISOUtils.hexString(cmdPack, 0, 2), ret);
			return ret;
		}
	}
	
	//NL829指令的打包与发送，返回2字节的状态标识，不做判断
	private int test_NL829_cmd(byte[]cmd, byte[] data){
		byte[] cmdData;
		cmdData = cmdPack_NL829(cmd,data);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			return ISOUtils.hexInt(recvBuf, 3, 2);
		} else{
			gui.cls_show_msg1_record(TAG, "test_NL829_cmd", g_keeptime,"line %d:NL829指令传输失败(%s,%d)", Tools.getLineInfo(),ISOUtils.hexString(cmdData, 0, 2), ret);
			return ret;
		}
	}
	
	//整合PP60命令的转发与接收返回值校验
	//只有一位返回值（0xAA成功或0x55失败）或没有返回值（E指令）的指令将返回成功与否
	//返回值有附加数据的指令若成功将返回附加数据的长度，若失败将返回0x55
	private int test_PP60_cmd(char cmdChar, byte[] data){
		byte[] cmdData; 
		cmdData = dataPack_NL829ToPP60(cmdChar,data);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(!ISOUtils.hexString(recvBuf, 5, 2).equals("0000") ){
				gui.cls_show_msg1_record(TAG, "test_PP60_cmd", g_keeptime,"line %d:PP60命令包转发失败(%s,%s)", Tools.getLineInfo(), cmdChar, ISOUtils.hexString(recvBuf, 5, 2));
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "test_PP60_cmd", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		//为防止异常返回造成接收不完整，预期返回值设为最大
		cmdData = dataPack_PP60ToNL829(0,MAXLEN);
		if((ret = cmd_frame_factory(cmdData,cmdData.length)) == NDK_OK){
			if(ISOUtils.hexString(recvBuf, 5, 2).equals("0000")){
				if(recvBuf.length == 11){
					//成功后没有返回值（E指令）的指令,直接返回0xAA
					return PP60_SUCC;
				} else if(recvBuf.length == 12){
					//返回值为一位字节,0xAA或0x55(成功或失败),个别指令(X指令)会返回0x56
					return ISOUtils.hexInt(recvBuf, 9, 1);
				} else if(recvBuf.length > 12){
					//返回值有附加数据的指令,返回附加数据的长度便于后续计算
					return ISOUtils.hexInt(recvBuf, 7, LEN_LENGTH);
				} else {
					gui.cls_show_msg1_record(TAG, "test_PP60_cmd", g_keeptime,"line %d:PP60指令返回值错误(%s)", Tools.getLineInfo(), ISOUtils.hexString(recvBuf));
					return NDK_ERR;
				}
			} else{
				gui.cls_show_msg1_record(TAG, "test_PP60_cmd", g_keeptime,"line %d:读取PP60命令返回失败(%s,%s)", Tools.getLineInfo(), cmdChar, ISOUtils.hexString(recvBuf, 5, 2));
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "test_PP60_cmd", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
	}
	
	
	/**
	 * 封装与发送NL829命令帧
	 * 
	 * @param data 需传输的数据单元
	 * @param datalen 数据单元长度
	 * @return 
	 */
	private int cmd_frame_factory(byte[] data,int datalen){
		/*private & local definition*/
		byte[] temp = new byte[1];
		byte[] buf = new byte[MAXLEN];
		int i=0;
		/*process body*/
		//组包
		cmdPack = cmdFrame_NL829(data, datalen);
		
		//发包收包根据串口
		switch (comValue) 
		{
		case '0':
			if ((ret = nlManager.write(cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
			{
				gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:pinpad写失败(ret=%d)", Tools.getLineInfo(), ret);
				return ret;
			}
			for(i=0;i<MAXLEN;i++){
				if ((ret = nlManager.read(temp, 1, MAXWAITTIME)) < 0){
					gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:pinpad读失败(ret=%d)", Tools.getLineInfo(),ret);
					return ret;
				}
				buf[i] = temp[0];
				if(Arrays.equals(temp, ETX))
					break;
			}
			nlManager.ioctl(0x540B, new byte[]{(byte)2});//清串口缓存
			recvBuf = ISOUtils.trim(buf, i+1);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			break;
			
		case '1':
			if ((ret = uart3Manager.write(cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
			{
				gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:RS232写失败(ret=%d)", Tools.getLineInfo(),ret);
				return ret;
			}
			for(i=0;i<MAXLEN;i++){
				if ((ret = uart3Manager.read(temp, 1, MAXWAITTIME)) < 0){
					gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:RS232读失败(ret=%d)", Tools.getLineInfo(),ret);
					return ret;
				}
				buf[i] = temp[0];
				if(Arrays.equals(temp, ETX) && i>=LEN_RECV_STATIC-1)
					break;
			}
			uart3Manager.ioctl(0x540B, new byte[]{(byte)2});//清串口缓存
			recvBuf = ISOUtils.trim(buf, i+1);
			LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
			break;

		default:
			break;
		}
		
		return NDK_OK;
	}
	
	/**
	 * "转发给PP60的命令数据打包"命令构造
	 * 
	 * @param cmdChar PP60指令字符
	 * @param data PP60指令附加数据，没有可以为null
	 * @return 
	 */
	private byte[] dataPack_NL829ToPP60(char cmdChar, byte[] data){
		int offset = 0;
		//转发PP60指令的命令C0 01 01
		byte[] forwardCmd = new byte[] {(byte)0xC0, 0x01, 0x01};
		//PP60的命令包
		byte[] pack_PP60 = cmdPack_PP60(cmdChar,data);
		
		//组包
		byte[] rslt = new byte[forwardCmd.length + pack_PP60.length];
		
		System.arraycopy(forwardCmd, 0, rslt, 0, forwardCmd.length);
		offset += forwardCmd.length;
		
		System.arraycopy(pack_PP60, 0, rslt, offset, pack_PP60.length);
		
		return rslt;
	}
	
	/**
	 * "读取PP60的返回数据"命令构造
	 * 
	 * @param delayTime 等待读取PP60返回数据的时间（秒）；等0 ：读取现有缓冲区即返回；大于300，按300算
	 * @param len 希望收到的PP60返回数据的长度；大于512，按512算
	 * @return 
	 */
	private byte[] dataPack_PP60ToNL829(int delayTime,int len){
		int offset = 0;
		//读取PP60返回数据的命令C0 02
		byte[] recvCmd = new byte[] {(byte)0xC0, 0x02};
		//计算2位的delaytime数据
		byte[] dalayTimeBuf = ISOUtils.intToBytes(delayTime, 2, true);
		//计算2位的期望返回长度数据
		byte[] lenBuf = ISOUtils.intToBytes(len, 2, true);
		
		//组包
		byte[] rslt = new byte[recvCmd.length + 2 + 2];
		
		System.arraycopy(recvCmd, 0, rslt, 0, recvCmd.length);
		offset += recvCmd.length;
		
		System.arraycopy(dalayTimeBuf, 0, rslt, offset, 2);
		offset += 2; 
		
		System.arraycopy(lenBuf, 0, rslt, offset, 2);
		
		return rslt;
	}
	
	
	
	/**
	 * PP60指令组包
	 * 
	 * @param cmdChar PP60指令字符
	 * @param data PP60附加数据，没有可以为null
	 * @return 
	 */
	private byte[] cmdPack_PP60(char cmdChar, byte[] data){
		/*private & local definition*/
		int dataLen = 0;
		/*process body*/
		//判断是否有data
		if(data != null)
			dataLen = data.length;
		//将指令转换为byte[]
		byte[] cmd = new byte[] {(byte)cmdChar};
		//拼装命令
		int offset = 0;
		byte[] rslt = new byte[LEN_PACK_HEAD + LEN_PACK_TAIL + LEN_CMD_PP60 + dataLen];
		
		System.arraycopy(PACK_HEAD, 0, rslt, 0, LEN_PACK_HEAD);
		offset += LEN_PACK_HEAD;

		System.arraycopy(cmd, 0, rslt, offset, LEN_CMD_PP60);
		offset += LEN_CMD_PP60;

		if(data != null){
			System.arraycopy(data, 0, rslt, offset, dataLen);
			offset += dataLen;
		}
		
		System.arraycopy(PACK_TAIL, 0, rslt, offset, LEN_PACK_TAIL);
		
		LoggerUtil.e("cmdPack_PP60:"+Dump.getHexDump(rslt));
		
		return rslt;
	}
	
	/**
	 * NL829数据单元(指令部分)打包
	 * 
	 * @param cmd 2字节的指令标识
	 * @param data 附加数据，没有可以为null
	 * @return 
	 */
	private byte[] cmdPack_NL829(byte[] cmd, byte[] data){
		/*private & local definition*/
		int dataLen = 0;
		/*process body*/
		//判断是否有data
		if(data != null)
			dataLen = data.length;
		//拼装命令
		int offset = 0;
		byte[] rslt = new byte[LEN_CMD_NL829 + dataLen];
		
		System.arraycopy(cmd, 0, rslt, 0, LEN_CMD_NL829);
		offset += LEN_CMD_NL829;

		if(data != null){
			System.arraycopy(data, 0, rslt, offset, dataLen);
			offset += dataLen;
		}
		
		LoggerUtil.e("cmdPack_NL829:"+Dump.getHexDump(rslt));
		
		return rslt;
	}
	
	/**
	 * 构造NL829命令帧格式
	 * 
	 * @param data 需传输的数据单元
	 * @param datalen 数据单元长度
	 * @return rslt 构造好的NL829命令帧
	 */
	private byte[] cmdFrame_NL829(byte[] data,int datalen){
		/*private & local definition*/
		byte[] DATALEN = new byte[LEN_DATALEN];
		/*process body*/
		//计算2位的数据长度
		DATALEN = ISOUtils.intToBytes(datalen, LEN_DATALEN, true);
		//计算校验位
		byte[] lrc = caculateLRC(data);
		//拼装命令
		int offset = 0;
		byte[] rslt = new byte[LEN_STX + LEN_DATALEN + datalen + LEN_LRC + LEN_ETX];
		
		System.arraycopy(STX, 0, rslt, 0, LEN_STX);
		offset += LEN_STX;
		
		System.arraycopy(DATALEN, 0, rslt, offset, LEN_DATALEN);
		offset += LEN_DATALEN;
		
		System.arraycopy(data, 0, rslt, offset, datalen);
		offset += datalen;
		
		System.arraycopy(lrc, 0, rslt, offset, LEN_LRC);
		offset += LEN_LRC;
		
		System.arraycopy(ETX, 0, rslt, offset, LEN_ETX);
		
		LoggerUtil.e("cmdPack:"+Dump.getHexDump(rslt));
		
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
	
	/**
	 * 将经过格式转换的ASCII密钥明文还原到10进制密码明文
	 * 
	 * @param code 经过格式转换的密钥明文,形如0x44 0x41 ...,说明文档定义的转换规则为一个字节的高四位 、低四位分别+41H成为两个个新的字节
	 * @param returnLen 希望返回的10进制明文长度，例如6位密码解密后长度8位，不足补0，但实际只需取6位
	 * @return 10进制密码明文
	 */
	private String getPWD(byte[] code, int returnLen){
		byte[] buf = PP60_decode(code);
		byte[] buf2 = ISOUtils.trim(buf,returnLen);
		return ISOUtils.dumpString(buf2);
	}
	
	/**
	 * 将byte数组按照指令说明文档进行格式转换
	 * 
	 * @param data 需要进行格式转换的byte数组
	 * @return buf 经过格式转换的的byte数组,形如0x44 0x41 ...,说明文档定义的转换规则为一个字节的高四位 、低四位分别+41H成为两个个新的字节,长度为原先的2倍
	 */
	private byte[] PP60_code(byte[] data){
		byte[] buf = new byte[data.length*2];
		int high;
		int low;
		for(int i = 0; i<data.length; i++){
			//取字节的高四位+41H
			high = ((data[i] & 0xf0) >> 4) + (0x41);
			//取字节的低四位+41H
			low = ((data[i] & 0x0f)) + (0x41);
			//放入新byte[]
			buf[2*i] = (byte)high;
			buf[2*i+1] = (byte)low;
		}
		return buf;
	}
	
	/**
	 * 还原按照指令说明文档进行格式转换后的byte数组
	 * 
	 * @param data 经过格式转换的的byte数组,形如0x44 0x41 ...,说明文档定义的转换规则为一个字节的高四位 、低四位分别+41H成为两个个新的字节
	 * @return buf 还原后的byte数组,长度为原先的1/2
	 */
	private byte[] PP60_decode(byte[] data){
		int len = data.length;
		byte[] buf = new byte[len/2];
		int high;
		int low;
		for(int i = 0; i<len/2; i++){
			high = ((data[2*i] - 0x41) << 4) & 0xf0;
			low = ((data[2*i+1] - 0x41)) & 0x0f;
			buf[i] = (byte) (high + low);
		}
		return buf;
	}
	
	/**
	 * 整合串口初始化
	 */
	private int portOpen() {
		boolean fd1 = false;
		int fd2 = -1,ret = -1;
		switch (comValue) {
		case '0':
			if ((fd1 = nlManager.connect(false)) != true) {
				gui.cls_show_msg1_record(TAG, "pinpad_portOpen", 2,"line %d:串口连接失败，fd=%s...", Tools.getLineInfo(),fd1 ? "true" : "false");
				return NDK_ERR;
			}
			if ((ret = nlManager.setconfig(BpsBean.bpsValue, 0,para[1].getBytes())) != ANDROID_OK) {
				gui.cls_show_msg1_record(TAG, "pinpad_portOpen", 2,"line %d:波特率设置失败，ret=%d...", Tools.getLineInfo(), ret);
				return NDK_ERR;
			}

			break;

		case '1':
			fd2 = isNewRs232==true?uart3Manager.open(62):uart3Manager.open();
			if (fd2  == -1) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:打开串口失败，fd=%d", Tools.getLineInfo(), fd2);
				return NDK_ERR;
			}

			if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,para[1].getBytes())) != ANDROID_OK) {
				gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:波特率设置失败，ret=%d", Tools.getLineInfo(), ret);
				return NDK_ERR;
			}
			break;

		default:
			break;
		}

		return NDK_OK;
	}

	/**
	 * 整合串口关闭
	 */
	private void portClose() {
		switch (comValue) {
		case 0:
			nlManager.disconnect();
			break;

		case 1:
			uart3Manager.close();
			break;

		default:
			break;
		}
	}
	
}
