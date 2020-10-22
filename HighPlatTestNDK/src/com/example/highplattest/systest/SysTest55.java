package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_ALG;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest55.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150624
 * directory 		: 
 * description 		: 安全综合测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 huangjianb			20150624		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest55 extends DefaultFragment
{
	private final String TAG = "SysTest55";
	private final String TESTITEM = "安全综合测试";
	private int DESCNT = 200;
	private final String TRANSMIT_KEY = "3131313131313131313131313131313131313131313131313131313131313131";// 传输密钥
	
	private String WORKINGKEY_DATA_MAC2 = "F084E49F01F80FAFBC62C9A79605E21C";// mac密钥，明文4DCDDF9DE5E8B8A9DF9DE5E88A9DF9DE
	private Gui gui = null;
	private SecKcvInfo kcvInfo = new SecKcvInfo();
	private int mKeyLenIndex = 4;
	private int mKcvLen=4;/**默认的kcv校验长度为4，海外版本的kcv校验长度为3，add by zhengxq 20190102*/
	private int ret=-1;
	
	/***
	 * 海外的母POS下发数据为DES：明文+24字节的0x38得到的密文,AES：明文+32字节0x58得到的密文
	 */
	String[][] keyData = 
		{
			/**8字节的密钥*/
			{ "1111111111111111","82E13665","253C9D9D7C2FBBFA","B0EE2BCCD4834C81"},/**8字节的TMK,明文，KCV，密文，DES海外密文,AES海外密文*/
			{ "DBFE96D0A5F09D24","5B4C8BED"},/**8字节TAK明文4DE5E8B8A9DCDDF9*/
			{ "DBFE96D0A5F09D24","5B4C8BED"},/**8字节的TDK明文4DE5E8B8A9DCDDF9*/
			{ "563E884E285E1350","9DA493AA"},/**8字节的TPK明文1818181818181818*/
			/**16字节的密钥*/
			/**DBFE96D0A5F09D24   F7EAC58BC4EA2865*/
			{ "11111111111111112222222222222222","D2B91CC5","253C9D9D7C2FBBFA9BC9FB82A5925726","B0EE2BCCD4834C81EA863506E41F85F8","64956BB3D8B40A9094487D8CFEF609D6"},/**16字节的TMK*/
			{ "43668564CE7F6198E2E2F4834C77E7F2","65EBB214"},/**16字节的TAK明文4DE5E8B8A9DCDDF91111111111111111*/
			{ "713E86C4BF6D84691C6E35A1DF8A5496","1B619F0F"},/**16字节的TDK明文23232323232323231212121212121212*/
			{ "C42D7DD28A15554ADEF60B1F05E39846","59559269"},/**16字节的TPK明文56565656565656567878787878787878*/
			/**24字节的密钥*/
			{ "111111111111111122222222222222223333333333333333","656B04F9","253C9D9D7C2FBBFA9BC9FB82A59257264BF6E91B1E3A9D81","B0EE2BCCD4834C81EA863506E41F85F85F9BF038314AF7E2","64956BB3D8B40A9094487D8CFEF609D6E0A1BF5BFF1057BF0E1FA991A972E05B"},/**24字节的TMK*/
			{ "83A165365E31D6333BA9E4262634E75523F13706B5812370","043A9E70"},/**24字节的TAK明文4DE5E8B8A9DCDDF911111111111111111259B7E1FEC34B9D*/
			{ "32AA073D515ABDFA0A532DCF34303C80E47CF7A72B8F095F","A8B7B5BD"},/**24字节的TDK明文121212121212121218181818181818181919191919191919*/
			{ "B6621D1954036141D9950566F5FCEEFAE14647E8A135061A","5720A50E"},/**24字节的TPK明文161616161616161610101010101010101717171717171717*/
		};
	
	String[][] mposKeyData = 
		{
			/**8字节的密钥*/
			{ "0008253C9D9D7C2FBBFA","000882E13665"},/**8字节的TMK,明文8字节的11*/
			{ "0008DBFE96D0A5F09D24","00045B4C8BED"},/**8字节TAK明文4DE5E8B8A9DCDDF9*/
			{ "0008DBFE96D0A5F09D24","00045B4C8BED"},/**8字节的TDK明文4DE5E8B8A9DCDDF9*/
			{ "0008563E884E285E1350","00049DA493AA"},/**8字节的TPK明文1818181818181818*/
			/**16字节的密钥*/
			/**DBFE96D0A5F09D24   F7EAC58BC4EA2865*/
			{ "0016253C9D9D7C2FBBFA9BC9FB82A5925726","0004D2B91CC5"},/**16字节的TMK*/
			{ "001643668564CE7F6198E2E2F4834C77E7F2","000465EBB214"},/**16字节的TAK明文4DE5E8B8A9DCDDF91111111111111111*/
			{ "0016713E86C4BF6D84691C6E35A1DF8A5496","00041B619F0F"},/**16字节的TDK明文23232323232323231212121212121212*/
			{ "0016C42D7DD28A15554ADEF60B1F05E39846","000459559269"},/**16字节的TPK明文56565656565656567878787878787878*/
			/**24字节的密钥*/
			{ "0024253C9D9D7C2FBBFA9BC9FB82A59257264BF6E91B1E3A9D81","0004656B04F9"},/**24字节的TMK*/
			{ "002483A165365E31D6333BA9E4262634E75523F13706B5812370","0004043A9E70"},/**24字节的TAK明文4DE5E8B8A9DCDDF911111111111111111259B7E1FEC34B9D*/
			{ "002432AA073D515ABDFA0A532DCF34303C80E47CF7A72B8F095F","0004A8B7B5BD"},/**24字节的TDK明文121212121212121218181818181818181919191919191919*/
			{ "0024DEDB34A9E73D72EFED1EB0926F12DB94081FA4DE6CC980A1","00045720A50E"},/**24字节的TPK明文161616161616161610101010101010101717171717171717*/
		};
	
	public void systest55() 
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SecEnable)==false)
		{
			gui.cls_show_msg1(1, "该X3无安全模块，不支持该用例");
			return;
		}
		gui = new Gui(myactivity, handler);
		
		JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo);
		// SM4和AES要先安装TLK
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI)){// 海外产品的KCV长度是3
			mKcvLen=3;
		}
		LoggerUtil.i("002,KcvLen="+mKcvLen);
		// 默认的setkeyowner为K21端，但N700和N850的密钥全部保存在Android端（固件处理）
		JniNdk.JNI_Sec_SetKeyOwner("*");
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			g_CycleTime=200;
			//压力
			sec_press();
			//性能
			sec_ability();
//			//空间检测
//			sec_keyspace();
			//随机数性能
			sec_random_ab();
			return;
		}
		//测试主入口
		while(true)
		{
//			int returnValue=gui.cls_show_msg("安全综合测试\n0.密钥属主配置\n1.压力测试\n2.性能测试\n3.异常测试\n4.空间检测\n5.自检-密钥检测\n6.随机数性能");
			int returnValue=gui.cls_show_msg("安全综合测试\n0.密钥属主配置\n1.压力测试\n2.性能测试\n5.自检-密钥检测\n6.随机数性能");
			switch (returnValue) 
			{	
			
			case '0':// 配置
//				if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)// 海外不需要设置密钥属主
//					gui.cls_show_msg1(2, "%s机型无需设置密钥属主",GlobalVariable.currentPlatform);
//				else
					sec_keyowner_config();
				break;
				
			case '1': //压力
				sec_press();
				break;
				
			case '2': //性能
				sec_ability();
				break;
				
			case '3': //异常
				sec_abnormity();
				break;
//				
//			case '4': //空间检测
//				sec_keyspace();
//				break;
				
			case '5':// 自检-密钥检测
				selfKey_check();
				break;
				
			case '6':// 8字节随机数性能
				sec_random_ab();
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	private void selfKey_check()
	{
		int keyLen=16;// 默认值为16
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("自检密钥检测\n0.NDK测试方式\n1.mpos测试方式\n2.密钥长度配置\n3验证星pos缺陷");
			switch (nkeyIn) 
			{
			case '0':
				ndk_selfcheck_test(keyLen);
				break;
				
			case '1':
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))
				{
					if(keyLen==32)
					{
						gui.cls_show_msg1(1, "mpos不支持安装32字节的密钥");
						break;
					}
					else
						sec_mpos_keyload(keyLen);
				}
				else
					gui.cls_show_msg1(2, "该产品不支持mpos用例");
				break;
			
			case '2':// 海外版本不考虑8字节，要16字节以上 modify by zhengxq 20190111
				String tipMsg="0.8字节\n1.16字节\n2.24字节\n3.32字节\n";
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI))
				{
					tipMsg="1.16字节\n2.24字节\n3.32字节";
				}
				int lenChoose = gui.cls_show_msg("密钥长度配置\n%s",tipMsg);
				switch (lenChoose) {
				case '0':
					// 海外和forh不支持8字节密钥的安装
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI))
					{
						gui.cls_show_msg1(2, "该产品不支持8字节密钥安装");
						break;
					}	
					keyLen=8;
					break;
					
				case '1':
					keyLen=16;
					break;
					
				case '2':
					keyLen=24;
					break;
					
				case '3':
					keyLen=32;
					break;

				default:
					break;
				}
				break;
			case'3':
				installTLK();
				break;
				
			case ESC:
				return;
				
			default:
				break;
			}
		}
	}
	
	private void installTLK() {
		if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo)!=NDK_OK) {
			gui.cls_show_msg1_record(TAG, "installTLK", 2, "TLK安装失败");
			
		}
		gui.cls_show_msg1(2,"TLK安装成功");
	}

	private void ndk_selfcheck_test(int keyLen)
	{
		while(true)
		{
			int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
			String tipMsg = "0.DES算法\n1.SM4算法\n2.AES算法(SDK2.0的设备不支持)\n3.CBC模式\n4.dukpt密钥\n";
			
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)/**海外不支持SM4*/
				tipMsg = "0.DES算法\n2.AES算法(SDK2.0的设备不支持)\n3.CBC模式\n4.dukpt密钥\n";
			
			int ndkKey = gui.cls_show_msg("算法模式选择\n%s",tipMsg);
			switch (ndkKey) {
			case '0':// Des算法不支持32字节的密钥装载
				if(keyLen==32)
				{
					gui.cls_show_msg("DES算法不支持32字节的密钥装载,任意键继续");
					break;
				}
				else
					sec_keyload(keyLen,algMode[ndkKey-'0']);
				break;
			case '1':// SM4算法支持16字节的密钥装载
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)// 海外不支持国密
					break;
				if(keyLen!=16)
				{
					gui.cls_show_msg("SM4算法只支持16字节的密钥装载,任意键继续");
					break;
				}
				else
					sec_keyload(keyLen,algMode[ndkKey-'0']);
				break;
			case '2':
				// AES算法支持16字节和24字节、32字节密钥装载
				if(keyLen==8)
				{
					gui.cls_show_msg("AES算法不支持8字节密钥装载,任意键继续");
					break;
				}
				else
					sec_keyload(keyLen,algMode[ndkKey-'0']);
				break;
			case '3':// CBC模式
				if(keyLen==8||keyLen==32)
				{
					gui.cls_show_msg("CBC模式不支持8字节密钥和32字节密钥的装载,任意键继续");
					break;
				}
				else
					sec_keyload(keyLen,algMode[ndkKey-'0']);
				break;
				
			case '4':// Dukpt
				// forth产品的dukpt只支持16字节 by 20200327
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)&&keyLen!=16)
				{
					gui.cls_show_msg1(2, "Forth的dukpt只支持16字节");
					break;
				}
				sec_keyloadTIK(keyLen);
				break;
				
			case ESC:
				mKeyLenIndex = 4;// 16字节密钥
				return;
				
			default:
				break;
			}
		}
	}
	// 密钥属主配置
	private void sec_keyowner_config()
	{
		int iRet = -1;
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)// 海外产品
		{
			int nKeyValue = gui.cls_show_msg("密钥属主配置\n0.配置为K21端\n【其他】默认");
			switch (nKeyValue) {
			case '0':
				if((iRet = JniNdk.JNI_Sec_SetKeyOwner("*"))!=0)
					gui.cls_show_msg1_record(TAG, "sec_keyowner_config",g_keeptime , "line %d:设置密钥属主失败(%d)", Tools.getLineInfo(),iRet);
				else
					gui.cls_show_msg1(g_keeptime, "密钥属主设置为K21端成功!!!");
				break;

			default:
				break;
			}
		}else{
			int nKeyValue = gui.cls_show_msg("密钥属主配置\n0.配置为K21端\n1.配置为Android端\n【其他】默认");
			switch (nKeyValue) {
			case '0':
				if((iRet = JniNdk.JNI_Sec_SetKeyOwner("*"))!=0)
					gui.cls_show_msg1_record(TAG, "sec_keyowner_config",g_keeptime , "line %d:设置密钥属主失败(%d)", Tools.getLineInfo(),iRet);
				else
					gui.cls_show_msg1(g_keeptime, "密钥属主设置为K21端成功!!!");
				break;
				
			case '1':
				if((iRet = JniNdk.JNI_Sec_SetKeyOwner(packName))!=0)
					gui.cls_show_msg1_record(TAG, "sec_keyowner_config",g_keeptime , "line %d:设置密钥属主失败(%d)", Tools.getLineInfo(),iRet);
				else
					gui.cls_show_msg1(g_keeptime, "密钥属主设置为Android端成功!!!");
				kcvInfo.nCheckMode=0;
				JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo);
				break;
	
			default:
				break;
			}
		}
	}
	
	/**
	 * 安全压力：包括jni和mpos方式
	 */
	private void sec_press()
	{
		int nkeyIn=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++nkeyIn == (GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?'2':'3'))// Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "sec_press", 2, "%s连续压力测试结束", TESTITEM);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行连续压力测试,[取消]退出")==ESC)
					return;
			}
			else
			{
					nkeyIn=gui.cls_show_msg("安全综合压力\n1.主密钥各种模式安装压力\n%s",
							(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"2.综合压力(mpos)\n":""));// Poynt和X5不支持mpos,直接进行jni压力测试
			}
			switch (nkeyIn) 
			{
//				case '0':
//					sec_jni_pre();
//					break;
					
				case '1':
					sec_LoadMk_Pre();
					break;
				
				case '2':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						sec_mpos_pre();
					break;

				case ESC:
					return;
			}
		}
	}
	
//	/**压力测试，NDK那边有测试，可屏蔽 by 20200327 zhengxq*/
//	private void sec_jni_pre() 
//	{
//		/*private & local definition*/
//		int ret = -1,bak = 0, cnt = 0, succ = 0;
//		byte[] psMacOut = new byte[16];
//		
//		/*process body*/
////		// 测试前置，擦除全部密钥操作
////		JniNdk.JNI_Sec_KeyErase();
////		if(GlobalVariable.currentPlatform!=Model_Type.N850)
////			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo);
//		
//		//设置压力次数
//		final PacketBean packet = new PacketBean();
//		// 设置压力次数
//		if(GlobalVariable.gSequencePressFlag)
//			packet.setLifecycle(getCycleValue());
//		else
//			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DESCNT));
//		bak = cnt = packet.getLifecycle();//交叉次数获取
//		
//		//密文下装主密钥,index =1
//		kcvInfo.nCheckMode = 1;
//		kcvInfo.nLen = mKcvLen;
//		kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex][1]);
//		// poynt不支持明文安装密钥，安装密钥方式全部改成密文,通过母POS下发TMK modify by 20190111
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
//		{
//			gui.cls_show_msg("请使用母POS下发TMK:ID=1,加密方式:DES,16字节密文=B0EE2BCCD4834C81EA863506E41F85F8,下发完毕点击任意键继续");
//		}
//		else
//		{
//			if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA9BC9FB82A5925726"), kcvInfo ))!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%s装载主密钥失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
//				return;
//			}
//		}
//
//		//装载MAC工作密钥,index = 3
//		kcvInfo.sCheckBuf =  ISOUtils.hex2byte(keyData[mKeyLenIndex+1][1]);
//		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)3, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+1][0]), kcvInfo))!=NDK_OK)
//		{
//			gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%s装载MAC密钥失败(%d)",Tools.getLineInfo(), TESTITEM,ret);
//			return;
//		}
//		
//		while(cnt > 0)
//		{
//			if(gui.cls_show_msg2(0.1f, "正在进行第%d次%s压力测试(已成功%d次),[取消]退出测试...", bak-cnt+1, TESTITEM, succ)==ESC)
//				break;
//			cnt--;
//			
//			//MAC_9606运算
//			if((ret = JniNdk.JNI_Sec_GetMac((byte)3, ISOUtils.hex2byte("00052D0A"), 4, psMacOut, (byte)3))!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%sMAC运行校验错误(%d)",Tools.getLineInfo(), TESTITEM,ret);
//				continue;
//			}
//			if(Tools.memcmp(ISOUtils.hex2byte("5BD32044482F47BE"), psMacOut, 8)==false)
//			{
//				gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%sMAC运行校验错误(%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.hexdump(psMacOut));
//				continue;
//			}
//			succ++;
//		}
//		
//		gui.cls_show_msg1_record(TAG, "sec_press",g_time_0, "压力测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
//		// 测试后置，擦除全部密钥操作
//		eraseLoadTLK();
//	}
	
	// 主密钥各种模式安装压力 add by 20190725 zhengxq 根据汇付客户报调用K21端ccm350国密芯片硬件算法时出现了失败，表现为某次调用NDK_Sec类接口执行国密密钥运算时失败，但实际密钥未丢失，后续可恢复正常
	public void sec_LoadMk_Pre()
	{
		int ret1,ret2,ret3,ret4;
		// 后续很多产品会限制不允许安装8字节密钥
		List<Integer> desArray=new ArrayList<Integer>(Arrays.asList(8,16,24));/**默认情况的des密钥长度*/
//		List<Integer> desArray=new ArrayList<Integer>(Arrays.asList(16,24));/**默认情况的des密钥长度*/
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI))// PCI规范都不允许安装8字节密钥
		{
			LoggerUtil.d("sec_LoadMk_Pre,Forth平台");
			desArray.remove(0);
		}
		int[] aesArray = {16,24,32};
		int[] algMode = {EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_AES.seckeyalg(),EM_SEC_KEY_ALG.SEC_KEY_CBC.seckeyalg()};
		String tipMsg = "0.DES算法\n1.SM4算法\n2.AES算法(SDK2.0的设备不支持)\n";
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)/**海外产品不支持SM4*/
			tipMsg = "0.DES算法\n2.AES算法(SDK2.0的设备不支持)\n";
		
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)/**Forth平台的customer_Id是oversea，但是forth又支持SM4，先暂时这样处理*/
//			tipMsg = "0.DES算法\n1.SM4算法\n2.AES算法(SDK2.0的设备不支持)\n";
		
		// 存在角标越界的异常
		int realAlgMode;
		int ndkKey = gui.cls_show_msg("算法模式选择\n%s",tipMsg);
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI))
		{
			if (ndkKey=='2'||ndkKey=='0')
				realAlgMode = algMode[ndkKey-'0'];
			else 
			{
				gui.cls_show_msg("输入错误,任意键继续");
				return;
			}
				
		}
		else
		{
			if (ndkKey<='2'&&ndkKey>='0')
				realAlgMode = algMode[ndkKey-'0'];
			else 
			{
				gui.cls_show_msg("输入错误,任意键继续");
				return;
			}
				
		}

		LoggerUtil.d("sec_LoadMk_Pre,realMode="+realAlgMode);
		
		// 传输密钥方式安装主密钥
		kcvInfo.nCheckMode = 0;// 不校验KCV的返回值
		
		int keyLen = 0;
		switch (ndkKey) {
		case '0':// DES模式
			keyLen = desArray.get((int)(new Random().nextInt(desArray.size())));
			break;
			
		case '1'://SM4模式
			keyLen=16;
			break;
			
		case '2'://AES模式
			keyLen = aesArray[(int)(new Random().nextInt(aesArray.length))];
			break;

		default:
			break;
		}
		LoggerUtil.d("sec_LoadMk_Pre,keyLen="+keyLen);
		
		/**测试前置擦除密钥*/
		if((ret1=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "sec_LoadMk_Pre",g_keeptime , "line %d:%s擦除密钥失败(ret=%d)",Tools.getLineInfo(), TESTITEM,ret1);
			return;
		}
		
		int TLKLen = keyLen==8?16:keyLen;
		// 安装传输密钥
		if((ret1=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|realAlgMode), (byte)0, (byte)1, TLKLen, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=NDK_OK)//16字节的31
		{
			gui.cls_show_msg1_record(TAG, "sec_LoadMk_Pre",g_keeptime , "line %d:%s擦除密钥失败(ret=%d)",Tools.getLineInfo(), TESTITEM,ret1);
		}
		
		
		StringBuffer strAppend = new StringBuffer();
		byte[] keyTempData = new byte[keyLen];
		int cnt = gui.JDK_ReadData(TIMEOUT_INPUT, 20, "主密钥安装压力测试,默认次数20,可手动修改");
		int succ=0,bak = cnt;
		while(cnt>0)
		{
			cnt--;
			/**forth密文安装只能装251的KEK，PIN，MAC，DATA只能装到250 by 20200327 开发对接陈嘉健*/
			int keyIndex=0;
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)){
				keyIndex = new Random().nextInt(231)+1;
			}
			else{
				keyIndex = new Random().nextInt(225)+1;
			}
			
			/**Forth不允许安装的密钥值相同，每次的密钥值都要不同*/
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// 传输密钥方式安装主密钥
			if((ret1=JniNdk.JNI_Sec_LoadKey((byte)(0|realAlgMode), (byte)(1|realAlgMode), (byte)1, (byte)keyIndex, keyLen, keyTempData, kcvInfo))==NDK_OK)
				strAppend.append("传输密钥安装主密钥成功index="+keyIndex+"、");
			else
				strAppend.append("传输密钥安装主密钥失败(index="+keyIndex+"、ret="+ret1+")\n");
			
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// 主密钥方式安装主密钥
			if((ret2=JniNdk.JNI_Sec_LoadKey((byte)(1|realAlgMode), (byte)(1|realAlgMode), (byte)keyIndex, (byte)(keyIndex+10), keyLen, keyTempData, kcvInfo))==NDK_OK)
				strAppend.append("主密钥安装主密钥成功index="+(keyIndex+10)+"、");
			else
				strAppend.append("主密钥安装主密钥失败(index="+(keyIndex+10)+"、ret="+ret2+")\n");
			
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// 明文安装主密钥方式
			if((ret3=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(1|realAlgMode), (byte)0, (byte)(keyIndex+20),keyLen, keyTempData, kcvInfo))==NDK_OK)
				strAppend.append("明文安装主密钥成功index="+(keyIndex+20)+"、");
			else
				strAppend.append("明文安装主密钥失败(index="+(keyIndex+20)+"、ret="+ret3+")\n");
			
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI))// PCI规范只能安装一次，因为是相同的密钥值
			{
				if(succ==0)
				{
					// TR31方式安装主密钥
					JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, 16, ISOUtils.hex2byte("13131313131313131313131313131313"), kcvInfo);
					if((ret4=JniNdk.JNI_Sec_LoadKey_TMK_TR31((byte)1,(byte)(keyIndex+30),kcvInfo))==NDK_OK)
						strAppend.append("TR31格式安装主密钥成功index="+(keyIndex+30));
					else
						strAppend.append("TR31格式安装主密钥失败(index="+(keyIndex+30)+"、ret="+ret4+")\n");
				}
				// 安装传输密钥
				if((ret1=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|realAlgMode), (byte)0, (byte)1, TLKLen, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=NDK_OK)//16字节的31
				{
					gui.cls_show_msg1_record(TAG, "sec_LoadMk_Pre",g_keeptime , "line %d:%s擦除密钥失败(ret=%d)",Tools.getLineInfo(), TESTITEM,ret1);
				}
			}
			else
			{
				// TR31方式安装主密钥
				JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, 16, ISOUtils.hex2byte("13131313131313131313131313131313"), kcvInfo);
				if((ret4=JniNdk.JNI_Sec_LoadKey_TMK_TR31((byte)1,(byte)(keyIndex+30),kcvInfo))==NDK_OK)
					strAppend.append("TR31格式安装主密钥成功index="+(keyIndex+30));
				else
					strAppend.append("TR31格式安装主密钥失败(index="+(keyIndex+30)+"、ret="+ret4+")\n");
				// 安装传输密钥
				if((ret1=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|realAlgMode), (byte)0, (byte)1, TLKLen, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=NDK_OK)//16字节的31
				{
					gui.cls_show_msg1_record(TAG, "sec_LoadMk_Pre",g_keeptime , "line %d:%s擦除密钥失败(ret=%d)",Tools.getLineInfo(), TESTITEM,ret1);
				}
			}


			if(strAppend.toString().contains("失败"))
			{
				gui.cls_show_msg("line %d:第%d次测试,%s,请去自检查看两个索引的主密钥值是否存在,重启后是否存在,重启前后应表现一致", Tools.getLineInfo(),bak-cnt,strAppend.toString());
				// 测试后置，重新安装传输密钥，因为有修改该密钥值
				JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|realAlgMode), (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo);
//				gui.cls_show_msg1(1, strAppend.toString());
//				strAppend.delete(0, strAppend.length());
				return;
			}
			gui.cls_show_msg1(1, strAppend.toString());
			strAppend.delete(0, strAppend.length());
			succ++;
		}

		// 测试后置，重新安装传输密钥，因为有修改该密钥值
		JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|realAlgMode), (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo);
		gui.cls_show_msg1_record(TAG, "sec_LoadMk_Pre",g_time_0, "主密钥安装压力测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
	}
	
	//mpos指令集压力测试
	public void sec_mpos_pre()
	{
		/*private & local definition*/
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		int bak = 0, cnt = 0, succ = 0;
		K21DeviceResponse response;
		byte[] retContent = new byte[128];
		
		try {
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		/*process body*/
		// 测试前置，擦除全部密钥操作
//		mK21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
//		mK21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null);
		//设置压力次数
		final PacketBean packet = new PacketBean();
		// 设置压力次数
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DESCNT));
		bak = cnt = packet.getLifecycle();//交叉次数获取
		
		//传输密钥加密主密钥,index =1
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TMK), null);
		if(ISOUtils.dumpString(response.getResponse(), 9, 2).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec_mpos_pre",g_keeptime , "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			return;
		}
		
		//装载MAC工作密钥,index = 3
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A05_TAK), 10,TimeUnit.SECONDS,null);
		if(ISOUtils.dumpString(response.getResponse(), 9, 2).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec_mpos_pre",g_keeptime , "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
			return;
		}
		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(3, "正在进行第%d次%s压力测试(已成功%d次),[取消]退出测试...", bak-cnt+1, TESTITEM, succ)==ESC)
				break;
			cnt--;
			
			//MAC_9606运算
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A04_MAC), null);
			retContent = response.getResponse();
			if(ISOUtils.dumpString(retContent, 10, 2).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec_mpos_pre",g_keeptime , "line %d:%s测试失败",Tools.getLineInfo(), TESTITEM);
				return;
			}
			if(ISOUtils.hexString(retContent, 12, 8).equals("95D2B7B12C03B05C")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%sMAC运行校验错误(%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.hexString(retContent, 12, 8));
				continue;
			}
			succ++;
		}
		
		gui.cls_show_msg1_record(TAG, "sec_press",g_time_0, "压力测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
		// 测试后置，擦除全部密钥操作
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		
	}
	
	
	// sec性能
	// 获取随机数的性能测试
	public void sec_random_ab()
	{
		/*private&local definition*/
		int bak = 20,ret = -1;
		long startTime;
		float time;
		byte[] random = new byte[8];
		// 获取8字节随机数
		gui.cls_show_msg1(2, "获取8字节随机数执行时间中...");
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			if((ret = JniNdk.JNI_Sec_GetRandom(8, random))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,"sec_random_ab", g_keeptime,"line %d:取随机数测试失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		time = Tools.getStopTime(startTime)/bak;
		gui.cls_show_msg1_record(TAG, "sec_random_ab",g_time_0, "获取统计值每次时间:"+time+"s");
	}
	
	/**
	 * 安全性能测试:包括jni和mpos方式
	 */
	private void sec_ability()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))
		{
			sec_mpos_ability();
		}
		else
			gui.cls_show_msg1(2, "该产品不支持mpos测试");
//		int nkeyIn=47;
//		while(true)
//		{
//			if(GlobalVariable.gSequencePressFlag)
//			{ 
//				if(++nkeyIn == (GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?'1':'2'))// Poynt和X5不支持mpos
//				{
//					gui.cls_show_msg1_record(TAG, "sec_ability", 2, "%s连续压力测试结束", TESTITEM);
//					return;
//				}
//				if(gui.cls_show_msg1(3,"即将进行安全性能测试,[取消]退出")==ESC)
//					return;
//			}
//			else
//			{
//				nkeyIn=gui.cls_show_msg("安全综合性能\n0.综合性能(jni)\n%s",
//						GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"1.综合性能(mpos)":"");// Poynt和X5不支持mpos,直接进行jni压力测试
//			}
//			switch (nkeyIn) 
//			{
//				case '0':
//					sec_jni_ability();
//					break;
//				
//				case '1':
//					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
//						sec_mpos_ability();
//					break;
//
//				case ESC:
//					return;
//			}
//		}
	}
	
	// sec性能测试
	// 明文load各类key（TMK（双倍长）/TIK），密文load各类key（TDK（单倍长）/TDK（双倍长）/TDK（三倍长））
//	private void sec_jni_ability()
//	{
//		/*private&local definition*/
//		int bak = 20,ret = -1;
//		long startTime;
//		float time;
//		String buf1_tmp="3672C2BC7F17F29C65873586BC7F17F23672C2BC7F17F29C";
//		byte[] ksnIn = new byte[17];
//		byte[] keyValueIn = new byte[17];
//		byte[] mac_result = new byte[16];
//		byte[] key8 = new byte[8];
//		byte[] keyBuf = {37,60,-99,-99,124,47,-69,-6};
//		
//		kcvInfo.nCheckMode = 0;
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)// Poynt是通过母POS下发TMK，不需要测TMK的性能
//		{
//			// 明文安装 TMK(双倍长) index = 1,len = 16
//			gui.cls_printf("计算TMK明文装载执行时间中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i < bak; i++) 
//			{
//				// 需要判断下装载成功或失败
//				if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex][0]), kcvInfo))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TMK测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//			}	
//			time =  Tools.getStopTime(startTime)*1000/bak;
//			gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0,"TMK统计值每次时间:%3.3fms",time);
//		}
//		else// Poynt
//		{
//			gui.cls_show_msg("请使用母POS下发TMK:ID=1,加密方式:DES,%d字节密文=%s,下发完毕点击任意键继续",16,keyData[mKeyLenIndex][3]);
//		}
//
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)// Poynt是通过母POS下发dukpt，不需要测TMK的性能
//		{
//			// 明文load TIK密钥
//			gui.cls_printf("计算TIK装载执行时间中...".getBytes());
//			Arrays.fill(ksnIn, (byte) 0xff);
//			Arrays.fill(keyValueIn, (byte) 0x11);
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i < bak; i++) 
//			{
//				// 需要判断下装载成功或失败
//				if((ret = JniNdk.JNI_Sec_LoadTIK((byte)10, (byte)1, (byte)16, ISOUtils.hex2byte(buf1_tmp), ksnIn, kcvInfo))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:第%d次:装载TIK测试失败(%d)", Tools.getLineInfo(),i+1,ret);
//					return;
//				}
//			}	
//			time = Tools.getStopTime(startTime)*1000/bak;
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "TIK统计值每次时间:%3.3fms",time);
//		}
//		
//		// 密文安装TAK index = 7，len = 16
//		gui.cls_printf("计算TAK装载执行时间中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)7, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+1][0]), kcvInfo))!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TAK测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//		}	
//		time =  Tools.getStopTime(startTime)*1000/bak;
//		gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0,"TAK统计值每次时间:%3.3fms",time);
//		// 密文安装TPK index = 8，len = 16
//		gui.cls_show_msg1(1, "计算TPK装载执行时间中...");
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)8, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+3][0]), kcvInfo))!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TPK测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//		}	
//		time =  Tools.getStopTime(startTime)*1000/bak;
//		gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "TPK统计值每次时间:%3.3fms",time);
//		
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)
//		{
//			// 密文安装TLK index = 9，len = 16
//			gui.cls_printf("计算TLK装载执行时间中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i < bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载TLK测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//			}	
//			time =  Tools.getStopTime(startTime)*1000/bak;
//			gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0,"TLK统计值每次时间:%3.3fms",time);
//		}
//		
//		// 密文load (TDK单倍长),index=4,len=8
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)&&GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)==false)// 海外产品不支持8字节密钥装载
//		{
//			gui.cls_printf("计算装载密文单倍长TDK执行时间中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i < bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)4, 8, ISOUtils.hex2byte(keyData[mKeyLenIndex-4+2][0]), kcvInfo))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载单倍长TDK测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//			}
//			time = Tools.getStopTime(startTime)*1000/bak;
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "单倍长TDK统计值每次时间:%3.3fms",time);
//		}
//
//		
//		// 密文load(TDK双倍长),index=5,len=16
//		gui.cls_printf("计算装载密文双倍长TDK执行时间中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)5, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+2][0]), kcvInfo))!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载双倍长TDK测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//		}
//		time = Tools.getStopTime(startTime)*1000/bak;
//		gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0,"双倍长TDK统计值每次时间:%1.3fms",time);
//		
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct))
//		{
//			// 密文load (TDK双倍长),index=6,len=24
//			gui.cls_printf("计算装载密文三倍长TDK执行时间中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i < bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)6, 24, ISOUtils.hex2byte(keyData[mKeyLenIndex+4+2][0]), kcvInfo))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:装载三倍长TDK测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//			}
//			time = Tools.getStopTime(startTime)*1000/bak;
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "三倍长TDK统计值每次时间:%1.3fms",time);
//		}
//		
//		// 加解密测试次数为200次
//		bak = 200;
//		Arrays.fill(key8, (byte) 0x11);
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct))
//		{
//			// 加解密测试CBC ECB
//			gui.cls_printf("8字节密钥ECB模式DES加密执行中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for(int i = 0;i < bak;i++)
//			{
//				//SEC_DES_ENCRYPT(0)|SEC_DES_KEYLEN_8(1<<1),用8字节密钥长度，使用ECB模式进行加密运算
//				if((ret = JniNdk.JNI_Sec_CalcDes((byte)4, (byte)4, keyBuf, 8, key8, (byte)(0|1<<1)))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:8字节DES加密测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//			}
//			time = bak/Tools.getStopTime(startTime);
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "8字节密钥ECB模式DES加密性能:%3.0f次/s",time);
//		}
//
//		
//		gui.cls_printf("16字节密钥ECB模式DES加密执行中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for(int i = 0;i < bak;i++)
//		{
//			//SEC_DES_ENCRYPT(0)|SEC_DES_KEYLEN_16(2<<1),用16字节密钥长度，使用ECB模式进行加密运算
//			if((ret = JniNdk.JNI_Sec_CalcDes((byte)4, (byte)5, keyBuf, 8, key8, (byte)(0|2<<1)))!=NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:16字节DES加密测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//		}
//		time = bak/Tools.getStopTime(startTime);
//		gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "16字节密钥ECB模式DES加密性能:%3.0f次/s",time);
//		
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct))
//		{
//			gui.cls_printf("24字节密钥ECB模式DES加密执行中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for(int i = 0;i < bak;i++)
//			{
//				//SEC_DES_ENCRYPT(0)|SEC_DES_KEYLEN_24(3<<1),用24字节密钥长度，使用ECB模式进行加密运算
//				if((ret = JniNdk.JNI_Sec_CalcDes((byte)4, (byte)6, keyBuf, 8, key8, (byte)(0|3<<1)))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:24字节DES加密测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//			}
//			time = bak/Tools.getStopTime(startTime);
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "24字节密钥ECB模式DES加密性能:%3.0f次/s",time);
//		}
//		
//		// 装载TAK密钥，index=10，len = 8
//		bak=50;
//		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct))
//		{
////			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex][0]), kcvInfo);
//			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)10, 8, ISOUtils.hex2byte("43668564CE7F6198"), kcvInfo);
//			byte[] macCal=new byte[1024];
//			Arrays.fill(macCal, (byte)0x34);
//			//MAC_9606运算
//			gui.cls_printf("8字节的MAC_9606运算执行中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i <bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_GetMac((byte)10, macCal, macCal.length, mac_result, (byte)3))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//				if(Tools.memcmp(ISOUtils.hex2byte("5B4C8BED6C112E7A"), mac_result, 8)==false)
//				{
//					gui.cls_show_msg1_record(TAG, "sec_ability",g_keeptime, "line %d:%sMAC运行校验错误(%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.hexString(mac_result));
//					return;
//				}
//			}
//			time = Tools.getStopTime(startTime);
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "8字节的MAC_9606运算性能:%3.3fs",time);
//			
//			//MAC_ECB运算
//			gui.cls_printf("8字节的MAC_ECB运算执行中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i <bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_GetMac((byte)10, macCal, macCal.length, mac_result, (byte)2))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//				if(Tools.memcmp(ISOUtils.hex2byte("3742313342423241"), mac_result, 8)==false)
//				{
//					gui.cls_show_msg1_record(TAG, "sec_ability",g_keeptime, "line %d:%sMAC运行校验错误(%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.hexString(mac_result));
//					return;
//				}
//			}
//			time = Tools.getStopTime(startTime);
//			gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0,"8字节的MAC_ECB运算性能:%3.3fs",time);
//			
//			//MAC_X919运算
//			gui.cls_printf( "8字节的MAC_X919运算执行中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i <bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_GetMac((byte)10, macCal, macCal.length, mac_result, (byte)1))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//				if(Tools.memcmp(ISOUtils.hex2byte("24E3358B30361BB4"), mac_result, 8)==false)
//				{
//					gui.cls_show_msg1_record(TAG, "sec_ability",g_keeptime, "line %d:%sMAC运行校验错误(%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.hexString(mac_result));
//					return;
//				}
//			}
//			time = Tools.getStopTime(startTime);
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "8字节的MAC_X919运算性能:%3.3fs",time);
//			
//			//MAC_X99运算
//			gui.cls_printf("8字节的MAC_X99运算执行中...".getBytes());
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i <bak; i++) 
//			{
//				if((ret = JniNdk.JNI_Sec_GetMac((byte)10, macCal, macCal.length, mac_result, (byte)0))!=NDK_OK)
//				{
//					gui.cls_show_msg1_record(TAG,"sec_ability", g_keeptime,"line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//					return;
//				}
//				if(Tools.memcmp(ISOUtils.hex2byte("24E3358B30361BB4"), mac_result, 8)==false)
//				{
//					gui.cls_show_msg1_record(TAG, "sec_ability",g_keeptime, "line %d:%sMAC运行校验错误(%s)",Tools.getLineInfo(), TESTITEM,ISOUtils.hexString(mac_result));
//					return;
//				}
//			}
//			time = Tools.getStopTime(startTime);
//			gui.cls_show_msg1_record(TAG, "sec_ability",g_time_0, "8字节的MAC_X99运算性能:%3.3fs",time);
//		}
//
//		
//		//3DES算法连续50次计算mac耗时  add by wangxy20180912
//		// 装载TAK密钥，index=10，len = 16
//		JniNdk.JNI_Sec_LoadKey((byte) 1, (byte) 3, (byte) 1, (byte) 10, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC2),kcvInfo);
//		byte[] b=new byte[1024];
//		Arrays.fill(b, (byte)0x8a);
//		
//		// 3DES算法+MAC_9606运算
//		gui.cls_printf("3DES算法+MAC_9606运算执行连续计算50次MAC中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if ((ret = JniNdk.JNI_Sec_GetMac((byte) 10, b, b.length, mac_result,(byte) 3)) != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//			if (Tools.memcmp(ISOUtils.hex2byte("F65A5286D3771D5E"), mac_result, 8) == false) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:%sMAC运行校验错误(%s)", Tools.getLineInfo(),TESTITEM, ISOUtils.hexString(mac_result));
//				return;
//			}
//		}
//		time = Tools.getStopTime(startTime);
//		gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0, "3DES算法+MAC_9606运算连续计算50次MAC耗时:%3.3fs",time);
//				
//		// 3DES算法+MAC_ECB运算
//		gui.cls_printf("3DES算法+MAC_ECB运算执行连续计算50次MAC中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if ((ret = JniNdk.JNI_Sec_GetMac((byte) 10, b, b.length, mac_result,(byte) 2)) != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//			if (Tools.memcmp(ISOUtils.hex2byte("3043393734384137"), mac_result, 8) == false) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:%sMAC运行校验错误(%s)", Tools.getLineInfo(),TESTITEM, ISOUtils.hexString(mac_result));
//				return;
//			}
//		}
//		time = Tools.getStopTime(startTime);
//		gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0, "3DES算法+MAC_ECB运算连续计算50次MAC耗时:%3.3fs",time);
//				
//		// 3DES算法+MAC_X919运算
//		gui.cls_printf("3DES算法+MAC_X919运算执行连续计算50次MAC中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if ((ret = JniNdk.JNI_Sec_GetMac((byte) 10, b, b.length, mac_result,(byte) 1)) != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//			
//			if (Tools.memcmp(ISOUtils.hex2byte("AB667BA89425F380"), mac_result, 8) == false)
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:%sMAC运行校验错误(%s)", Tools.getLineInfo(),TESTITEM, ISOUtils.hexString(mac_result));
//				return;
//			}
//		}
//		time =Tools.getStopTime(startTime);
//		gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0, "3DES算法+MAC_X919运算连续计算50次mac耗时:%3.3fs",time);
//				
//		// 3DES算法+MAC_X99运算
//		gui.cls_printf("3DES算法+MAC_X99运算执行连续计算50次MAC中...".getBytes());
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < bak; i++) 
//		{
//			if ((ret = JniNdk.JNI_Sec_GetMac((byte) 10, b, b.length, mac_result,(byte) 0)) != NDK_OK) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),ret);
//				return;
//			}
//			//方法返回0DE66A19C1B7F2B8；工具计算结果A123E6A74FE21F35
//			if (Tools.memcmp(ISOUtils.hex2byte("0DE66A19C1B7F2B8"), mac_result, 8) == false) 
//			{
//				gui.cls_show_msg1_record(TAG, "sec_ability", g_keeptime, "line %d:%sMAC运行校验错误(%s)", Tools.getLineInfo(),TESTITEM, ISOUtils.hexString(mac_result));
//				return;
//			}
//		}
//		time = Tools.getStopTime(startTime);
//		gui.cls_show_msg1_record(TAG, "sec_ability", g_time_0, "3DES算法+MAC_X99运算连续计算50次mac耗时:%3.3fs",time);
//		// 测试后置，擦除全部密钥操作
//		eraseLoadTLK();
//	}
	
	
	// sec性能测试
	// 明文load各类keyTMK(双倍长)/TIK），密文load各类key（TDK（单倍长）/TDK（双倍长）/TDK（三倍长））
	private void sec_mpos_ability()
	{
		/*private&local definition*/
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode,mac;
		K21DeviceResponse response;
		byte[] retContent = new byte[128];
		int bak = 20;
		long startTime;
		float time;
		
		try {
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}

		
//		// 测试前置，擦除所有密钥
//		response = mK21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
//		retContent = response.getResponse();
//		if((retCode = ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)
//		{
//			gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_keeptime, "line %d:删除密钥测试失败(%s)", Tools.getLineInfo(),retCode);
//			return;
//		}
//		
//		response = mK21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null);
//		retContent = response.getResponse();
//		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
//		{
//			gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_keeptime,"line %d:装载TLK测试失败(%s)", Tools.getLineInfo(),retCode);
//			return;
//		}
		// 密文安装 TMK(双倍长) index = 2,len = 16
		gui.cls_show_msg1(1, "计算TMK密文装载执行时间中...");
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			// 需要判断下装载成功或失败
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TMK), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载TMK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}	
		time =  Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_time_0,"TMK统计值每次时间:%3.3fms",time);
		// 明文load TIK密钥
		gui.cls_show_msg1(2, "计算TIK装载执行时间中...");
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			// 需要判断下装载成功或失败
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A17_TIK), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载TIK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}	
		time = Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "TIK统计值每次时间：%3.3fms",time);
		
		// 密文安装TAK index = 7,len = 16(明文8个13+8个15)
		gui.cls_show_msg1(1, "计算TAK装载执行时间中...");
		byte[] pack_TAK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302070016C0B757413B847925145F5C6E3D9144570004E6513BFE00"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			// 需要判断下装载成功或失败
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载TAK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}	
		time =  Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_time_0,"TAK统计值每次时间:%3.3fms",time);
		// 密文安装TPK index = 7，len = 16
		gui.cls_show_msg1(1, "计算TPK装载执行时间中...");
		byte[] pack_TPK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202070016C0B757413B847925145F5C6E3D9144570004E6513BFE00"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载TPK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}	
		time =  Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "TPK统计值每次时间：%3.3fms",time);
		
		// 密文安装TLK index = 9,len = 16
		gui.cls_show_msg1(1, "计算TLK装载执行时间中...");
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载TLK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}	
		time =  Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_time_0,"TLK统计值每次时间:%3.3fms",time);
		
		// 密文load (TDK单倍长),index=4,len=8(8个44)
		gui.cls_show_msg1(1, "计算装载密文单倍长TDK执行时间中...");
		byte[] pack_TDK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102040008A0C45C59F1E549BB0004E2F2434000"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK1), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载单倍长TDK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}
		time = Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "单倍长TDK统计值每次时间:%3.3fms",time);
		
		// 密文load （TDK双倍长）,index=5,len=16(8个44+8个55)
		gui.cls_show_msg1(1, "计算装载密文双倍长TDK执行时间中...");
		byte[] pack_TDK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102050016A0C45C59F1E549BBDD600F71D757FBAC00046B94181900"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK2), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载双倍长TDK测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}
		time = Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_time_0,"双倍长TDK统计值每次时间:%1.3fms",time);
		
		// 密文load (TDK双倍长),index=6,len=24(8个12+8个13+8个14)
		gui.cls_show_msg1(1, "计算装载密文三倍长TDK执行时间中...");
		byte[] pack_TDK3 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102060024A929DB826A498C15C0B757413B84792524EDBCFB230CFC4A0004265A7ABF00"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i < bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK3), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:装载三倍长TDK测试失败(%d)", Tools.getLineInfo(),retCode);
				return;
			}
		}
		time = Tools.getStopTime(startTime)*1000/bak;
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "三倍长TDK统计值每次时间:%3.3fms",time);
		
		// 加解密测试次数为200次
		bak = 200;
		// 加解密测试CBC ECB
		gui.cls_show_msg1(1, "8字节密钥ECB模式DES加密执行中...");
		byte[] pack_calc1 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("04020008202020202020202000000101010101010101"));
		startTime = System.currentTimeMillis();
		for(int i = 0;i < bak;i++)
		{
			//SEC_DES_ENCRYPT(0)|SEC_DES_KEYLEN_8(1<<1),用8字节密钥长度，使用ECB模式进行加密运算
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc1), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:8字节DES加密测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "8字节密钥ECB模式DES加密性能：%3.0f次/s",time);
		
		gui.cls_show_msg1(1, "16字节密钥ECB模式DES加密执行中...");
		byte[] pack_calc2 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("050200162020202020202020202020202020202000000101010101010101"));
		startTime = System.currentTimeMillis();
		for(int i = 0;i < bak;i++)
		{
			//SEC_DES_ENCRYPT(0)|SEC_DES_KEYLEN_16(2<<1),用16字节密钥长度，使用ECB模式进行加密运算
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc2), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:16字节DES加密测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "16字节密钥ECB模式DES加密性能：%3.0f次/s",time);
		
		gui.cls_show_msg1(1, "24字节密钥ECB模式DES加密执行中...");
		byte[] pack_calc3 = CalDataLrc.mposPack(new byte[]{0x1A,0x03},ISOUtils.hex2byte("0602002420202020202020202020202020202020202020202020202000000101010101010101"));
		startTime = System.currentTimeMillis();
		for(int i = 0;i < bak;i++)
		{
			//SEC_DES_ENCRYPT(0)|SEC_DES_KEYLEN_24(3<<1),用24字节密钥长度，使用ECB模式进行加密运算
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_calc3), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:24字节DES加密测试失败(%d)", Tools.getLineInfo(),retCode);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "24字节密钥ECB模式DES加密性能：%3.0f次/s",time);
		
		
		//MAC_9606运算
		gui.cls_show_msg1(1, "MAC_9606运算执行中...");
		byte[] pack_MAC1 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("07000303000400052D0A00000000"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i <bak; i++) 
		{
			
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC1), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:MAC计算测试失败(%d)", Tools.getLineInfo(),retCode);
				return;
			}
			if((mac=ISOUtils.hexString(retContent, 12, 8)).equals("081040602B7F7329")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_keeptime, "line %d:MAC运行校验错误(%s)",Tools.getLineInfo(),mac);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "MAC_9606运算性能：%3.0f次/s",time);
		
		//MAC_ECB运算
		gui.cls_show_msg1(1, "MAC_ECB运算执行中...");
		byte[] pack_MAC2 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("07000203000400052D0A00000000"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i <bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC2), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:MAC计算测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			if((mac = ISOUtils.hexString(retContent, 12, 8)).equals("3541363742333144")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_keeptime, "line %d:MAC运行校验错误(%s)",Tools.getLineInfo(),mac);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability", g_time_0,"MAC_ECB运算性能：%3.0f次/s",time);
		
		//MAC_X919运算
		gui.cls_show_msg1(1, "MAC_X919运算执行中...");
		byte[] pack_MAC3 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("07000103000400052D0A00000000"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i <bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC3), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:MAC计算测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			if((mac = ISOUtils.hexString(retContent, 12, 8)).equals("081040602B7F7329")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_keeptime, "line %d:MAC运行校验错误(%s)",Tools.getLineInfo(),mac);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "MAC_X919运算性能：%3.0f次/s",time);
		
		//MAC_X99运算
		gui.cls_show_msg1(1, "MAC_X99运算执行中...");
		byte[] pack_MAC4 = CalDataLrc.mposPack(new byte[]{0x1A,0x04},ISOUtils.hex2byte("07000003000400052D0A00000000"));
		startTime = System.currentTimeMillis();
		for (int i = 0; i <bak; i++) 
		{
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_MAC4), null);
			retContent = response.getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 10, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG,"sec_mpos_ability", g_keeptime,"line %d:MAC计算测试失败(%s)", Tools.getLineInfo(),retCode);
				return;
			}
			if((mac = ISOUtils.hexString(retContent, 12, 8)).equals("081040602B7F7329")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_keeptime, "line %d:MAC运行校验错误(%s)",Tools.getLineInfo(),mac);
				return;
			}
		}
		time = bak/Tools.getStopTime(startTime);
		gui.cls_show_msg1_record(TAG, "sec_mpos_ability",g_time_0, "MAC_X99运算性能：%3.0f次/s",time);
		// 测试后置，擦除全部密钥操作
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		// 断开连接
		k21ControllerManager.close();
	}
	
	//异常测试菜单
	public void sec_abnormity() 
	{
		while(true)
		{
			int nkeyIn=gui.cls_show_msg("异常测试菜单\n0.安全触发\n1.掉电测试\n2.ksn值校验\n3.海外固件不同索引安装相同密钥值\n");
			switch (nkeyIn) 
			{	
			case '0': //安全触发
				sec_tamper();
				break;
				
			case '1': //掉电测试
				sec_powerdown();
				break;
				
			case '2': //ksn值校验
				sec_ksn();
				break;
				
			case '3':
				sec_pci_abnormal();
				break;
				
			case ESC:
				return;
				
			default:
				break;
			}
		}
	}
	
	//安全触发
	private void sec_tamper() 
	{
		/*private & local definition*/
		int ret = -1;
		
		while(true)
		{
			int nkeyIn=gui.cls_show_msg("安全触发\n0.密钥保存\n1.安全运算");
			switch (nkeyIn) 
			{	
				
			case '0': 
				eraseLoadTLK();
				// 安装主密钥
				if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA9BC9FB82A5925726"), kcvInfo ))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%s装载主密钥失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
					return;
				}
				//data密钥
				kcvInfo.nCheckMode = 1;
				kcvInfo.nLen = mKcvLen;
				kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex+2][1]);// 明文安装23232323232323231212121212121212
				if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)1, 16, ISOUtils.hex2byte("713E86C4BF6D84691C6E35A1DF8A5496"), kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_tamper",g_keeptime, "line %d:%sdata密钥下装校验错误(%d)", Tools.getLineInfo(), TESTITEM,ret);
					return;
				}
				gui.cls_show_msg1_record(TAG, "sec_tamper",g_keeptime, "密钥保存成功");
				break;
				
			case '1': 
				//计算kcv
				kcvInfo.nCheckMode = 1;
				kcvInfo.nLen = mKcvLen;
				if((ret = JniNdk.JNI_Sec_GetKcv((byte)4, (byte)1,kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime,"line %d:%s计算工作密钥失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
					return;
				}
				LoggerUtil.d("kcvInfo:"+ISOUtils.hexString(kcvInfo.sCheckBuf,4));
				if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte(keyData[mKeyLenIndex+2][1]), kcvInfo.nLen)==false)
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime, "line %d:%sdata密钥下装校验错误", Tools.getLineInfo(), TESTITEM);
					return;
				}
				gui.cls_show_msg1_record(TAG, "sec_tamper", g_keeptime,"计算KCV成功");
				break;
				
			case ESC:
				return;
				
			}
		}
	}
	
	//掉电测试
	private void sec_powerdown() 
	{
		/*private & local definition*/
//		byte[] kcv = new byte[4];
		//工作密钥
		int pownSign = 0,ret = -1;
		
		while(true)
		{
			int nkeyIn=gui.cls_show_msg("掉电测试\n0.掉电方式\n1.密钥保存\n2.安全运算");
			switch (nkeyIn) 
			{	
			case '0': 
				//掉电方式
				int returnType=gui.cls_show_msg("请选择掉电方式\n0.手动\n1.自动");
				switch (returnType) 
				{	
				case '0': 
					pownSign = 1;//手动
					break;
					
				case '1': 
					pownSign = 0;//自动
					break;
					
				default:
					break;
				}
				break;
				
			case '1': 
				//　清全部密钥操作
				eraseLoadTLK();
				if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA9BC9FB82A5925726"), kcvInfo ))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_press",g_keeptime , "line %d:%s装载主密钥失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
					return;
				}
				gui.cls_show_msg1(2, "开机完运行安全运算验证掉电是否会保存密钥");

				// 安装数据加密 解密工作密钥
				kcvInfo.nCheckMode = 1;
				kcvInfo.nLen = mKcvLen;
				kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex+2][1]);// 明文安装23232323232323231212121212121212
				if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)1, 16, ISOUtils.hex2byte("713E86C4BF6D84691C6E35A1DF8A5496"), kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime, "line %d:%sdata密钥下装校验错误(%d)", Tools.getLineInfo(), TESTITEM,ret);
					return;
				}
				if(pownSign == 0)
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime, "自动断电中请稍候");
					Tools.reboot(myactivity);
				}
				else 
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime, "请立即关机");
				}
				break;
				
			case '2': 
				kcvInfo.nCheckMode = 1;
				//计算kcv
				if((ret = JniNdk.JNI_Sec_GetKcv((byte)4, (byte)1, kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime,"line %d:%s计算工作密钥失败（%d）", Tools.getLineInfo(),TESTITEM,ret);
					return;
				}
				if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte(keyData[mKeyLenIndex+2][1]), kcvInfo.nLen)==false)
				{
					gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime, "line %d:%sdata密钥下装校验错误", Tools.getLineInfo(), TESTITEM);
					return;
				}
				gui.cls_show_msg1_record(TAG, "sec_powerdown",g_keeptime, "计算TDK密钥的kcv成功");
				break;
				
			case ESC:
				return;
				
			}
		}
	}
	
	//ksn值校验
	private void sec_ksn() 
	{
		int ret=-1;
		byte[] ksn = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xEE,0x00,0x06};
		byte[] initKey ={0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x05, 0x08, 0x00, 0x01, 0x02,0x03, 0x04, 0x05, 0x05, 0x08};
		
		while(true)
		{
			int nkeyIn=gui.cls_show_msg("ksn值校验\n0.安装DUKPT\n1.ksn检测");
			switch (nkeyIn) 
			{			
			case '0': 
				//明文装载TLK
				kcvInfo.nCheckMode = 0;
				kcvInfo.nLen = mKcvLen;
				if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, 16, ISOUtils.hex2byte(TRANSMIT_KEY),kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_ksn",g_keeptime,"line %d:装载TLK失败（ret = %d）", Tools.getLineInfo(),ret);
					return;
				}
			    //DUKPT装载
				if((ret = JniNdk.JNI_Sec_LoadTIK((byte)10, (byte)1, (byte)16, initKey, ksn, kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_ksn",g_keeptime,"line %d:装载TIK失败（ret = %d）", Tools.getLineInfo(),ret);
					return;
				}
				gui.cls_show_msg1_record(TAG, "sec_ksn",g_keeptime, "请退出程序或者重启,然后在进入本案例1.ksn检测");
				break;
				
			case '1':
				// add by zhengxq 修改获取ksn值的代码
				String str=new String();
				String ksnPreValue = new String();
				// dukpt的索引安装在s索引10
				byte[] psKsnOut = new byte[10];
				if((ret = JniNdk.JNI_Sec_GetDukptKsn((byte)10, psKsnOut))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_ksn",g_keeptime,"line %d:获取的ksn值失败（ret = %d）", Tools.getLineInfo(),ret);
					return;
				}
				if(psKsnOut!=null)
				{
					for (int i = 0; i < psKsnOut.length; i++) 
			    	{
			    		str = str+String.format("%02x", psKsnOut[i]);
					}
				}
				
				if(ksn!=null)
				{
					for (int i = 0; i < ksn.length; i++) 
			    	{
						ksnPreValue = ksnPreValue+String.format("%02x", ksn[i]);
					}
				}
				// modify by zhengxq 詹可祥分析最后两个字节会被重置为0x0001，倒数第三字节的bit0~bit4重置为0
				gui.cls_show_msg1_record(TAG, "sec_ksn",g_time_0, "设置的ksn号为%s，获取到的ksn号：%s，两者一致视为测试通过（最后五位不用看，前面几位相同即可，最后两字节被重置为0x0001，倒数第三字节低位被重置为0）",ksnPreValue==null?"":ksnPreValue,psKsnOut==null?"":str);
				break;
				
			case ESC:
				return;
				
			}
		}
		
	}
	
	// PCI规范测试，不同索引不能安装相同密钥值
	private void sec_pci_abnormal()
	{
		String funcName = "sec_pci_abnormal";
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPCI)==false)// 国内支持不同索引安装相同密钥值
		{
			gui.cls_show_msg("该客户不支持该测试项,任意键退出");
			return;
		}
		JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo);
		kcvInfo.nCheckMode = 0;
		if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)1, 16, ISOUtils.hex2byte("253C9D9D7C2FBBFA9BC9FB82A5925726"), kcvInfo ))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime , "line %d:%s装载主密钥失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		
		kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex+2][1]);// 明文安装23232323232323231212121212121212
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)1, 16, ISOUtils.hex2byte("713E86C4BF6D84691C6E35A1DF8A5496"), kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:%sdata密钥下装校验错误(%d)", Tools.getLineInfo(), TESTITEM,ret);
			return;
		}
		if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)2, 16, ISOUtils.hex2byte("713E86C4BF6D84691C6E35A1DF8A5496"), kcvInfo))==NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName,g_keeptime, "line %d:%sdata密钥下装校验错误(%d)", Tools.getLineInfo(), TESTITEM,ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, funcName, g_time_0, "PCI规范测试通过");
	}
	
	//空间检测测试菜单
	private void sec_keyspace() //装多组容易超时退出
	{
		/*private & local definition*/
		int MAXGN = 5, i = 0, j = 0, k = 0, l = 0,ret = -1;
		
		//校验数据
		gui.cls_show_msg1(1,  "空间检测测试中...");
		
		//明文密钥
		kcvInfo.nCheckMode = 1;
		kcvInfo.nLen = mKcvLen;
		kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex][1]);
		for(i = 0;i < MAXGN;i++)
		{
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex][0]), kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sec_keyspace", g_keeptime, "line %d:%sTMK装载失败(%d)",Tools.getLineInfo(), TESTITEM,ret);
				return;
			}
		}
		gui.cls_show_msg1(2, "测的主密钥空间组数为:" + i);
		
		//MAC密钥
		kcvInfo.nCheckMode = 1;
		kcvInfo.nLen = mKcvLen;
		kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex+1][1]);
		for(j = 0;j < MAXGN;j++)
		{
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)1, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+1][0]), kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sec_keyspace", g_keeptime, "line %d:%sTAK密钥装载(%d)",Tools.getLineInfo(), TESTITEM,ret);
				return;
			}
		}
		gui.cls_show_msg1(2, "测的Mac密钥空间组数为:" + j);
		
		//pin密钥
		kcvInfo.nCheckMode = 1;
		kcvInfo.nLen = mKcvLen;
		kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex+3][1]);
		for(k = 0;k < MAXGN;k++)
		{
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)1, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+3][0]), kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sec_keyspace", g_keeptime, "line %d:%sTPK密钥装载失败(%d)", Tools.getLineInfo(), TESTITEM,ret);
				return;
			}
		}
		gui.cls_show_msg1(2, "测的Pin密钥空间组数为:" + k);
		
		//data密钥
		kcvInfo.nCheckMode = 1;
		kcvInfo.nLen = mKcvLen;
		kcvInfo.sCheckBuf = ISOUtils.hex2byte(keyData[mKeyLenIndex+2][1]);
		for(l = 0;l < MAXGN;l++)
		{
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)1, 16, ISOUtils.hex2byte(keyData[mKeyLenIndex+2][0]), kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sec_keyspace", g_keeptime, "line %d:%sTDK密钥下装校验错误(%d)", Tools.getLineInfo(), TESTITEM,ret);
				return;
			}
		}
		gui.cls_show_msg1(2, "测的Data密钥空间组数为:" + l);
		gui.cls_show_msg1_record(TAG, "sec_keyspace", g_time_0, "密钥空间测试通过,检测到密钥存储空间主密钥%d组发散PIN%d组,MAC%d组,DATA%d组",i,j,k,l);
	
	}
	
	// 密钥安装  新增：20160930 根据教授提出的测试执行人员差异，修改用例 modify by 20170413 zhengxq
	private void sec_keyload(int keyLen,int algMode)
	{
		/*private & local definition*/
		LoggerUtil.d("sec_keyload:"+keyLen);
		int keyIndex;
		/**forth密文安装只能装251的KEK，PIN，MAC，DATA只能装到250 by 20200327 开发对接陈嘉健*/
		int endIndex = GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)?250:255;
		String secMode = algMode==0?"DES算法":algMode==(1<<6)?"SM4算法":algMode==(1<<7)?"AES算法":"CBC模式";
		/*process body*/
		while(true)
		{
			int returnValue=gui.cls_show_msg("安全综合测试(NDK方式,%s)\n0.安装所有密钥\n1.安装索引密钥\n2.擦除密钥\n3.密钥检测\n",secMode);
			switch (returnValue) 
			{
			case '0':
				for (int i = 1; i <= endIndex; i++) 
				{
					if(ndk_install_key(keyLen,i,algMode)==ESC)// 根据测试人员建议，添加中途可退出的按钮 modify by zhengxq 20181130
					{
						gui.cls_show_msg("已退出安装所有密钥,任意键继续");
						return;
					}
				}
				break;
				
			case '1':// 安装特定索引密钥
				gui.cls_printf("请输入索引值:".getBytes());
				keyIndex=gui.JDK_ReadData(TIMEOUT_INPUT,1);
				if(keyIndex<0||keyIndex>endIndex)
				{
					gui.cls_show_msg1(1, "密钥索引输入范围错误");
					return;
				}
				Log.d("eric", "安装特定索引-----");
				ndk_install_key(keyLen,keyIndex,algMode);
				break;
				
			case '2':// 擦除密钥
				gui.cls_show_msg1(2, "即将擦除1-%d索引的密钥",endIndex);
				eraseLoadTLK();
				gui.cls_show_msg1(2, "1-%d索引密钥擦除成功",endIndex);
				break;
				
			case '3':
				StringBuffer strBuffer = new StringBuffer();
				gui.cls_printf("请输入索引值:".getBytes());
				keyIndex=gui.JDK_ReadData(TIMEOUT_INPUT,1);
				if(keyIndex<0||keyIndex>endIndex)
				{
					gui.cls_show_msg1(1, "密钥索引输入范围错误");
					return;
				}
				kcvInfo.nCheckMode = 1;
				// 主密钥
				if(JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?1:(1|algMode)), (byte)keyIndex, kcvInfo)==0)
					strBuffer.append("索引"+keyIndex+"：主密钥存在\n");
				else
					strBuffer.append("索引"+keyIndex+"：主密钥不存在\n");
				// pin密钥
				if(JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?2:(2|algMode)), (byte)keyIndex, kcvInfo)==0)
					strBuffer.append("索引"+keyIndex+"：Pin密钥存在\n");
				else
					strBuffer.append("索引"+keyIndex+"：Pin密钥不存在\n");
				// mac密钥
				if(JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?3:(3|algMode)), (byte)keyIndex, kcvInfo)==0)
					strBuffer.append("索引"+keyIndex+"：Mac密钥存在\n");
				else
					strBuffer.append("索引"+keyIndex+"：Mac密钥不存在\n");
				// data密钥
				if(JniNdk.JNI_Sec_GetKcv((byte)(algMode==10?4:(4|algMode)), (byte)keyIndex, kcvInfo)==0)
					strBuffer.append("索引"+keyIndex+"：Data密钥存在\n");
				else
					strBuffer.append("索引"+keyIndex+"：Data密钥不存在\n");
				gui.cls_show_msg(strBuffer.toString()+"任意键继续测试");
				break;

			case ESC:
				return;
			}
		}
	}
	
	// 擦除密钥之后安装TLK
	private void eraseLoadTLK()
	{
		if((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "eraseLoadTLK", g_keeptime, "line %d:密钥擦除失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		kcvInfo.nCheckMode=0;
		// Poynt和Forth产品不需要安装TLK by 20200327
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth))
			return;
		
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)// 巴西的固件擦除密钥之后不会自动安装TLK，需要手动安装 modify by zhengxq
		{
			LoggerUtil.d("安装TLK");
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo);
		}
		else
		{
			byte[] pszOwner=new byte[100];
			JniNdk.JNI_Sec_GetKeyOwner(pszOwner.length, pszOwner);
			if( !ISOUtils.byteToStr(pszOwner).equals("*")){
				JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo);
			}
		}
	}
	
	/**
	 * algMode:
	 * 0:DES算法
	 * (1<<6):SM4算法
	 * :AES算法
	 * 
	 */
	private int ndk_install_key(int keyLen,int keyIndex,int algMode)
	{			
		StringBuffer strAppend = new StringBuffer();
		gui.cls_printf(String.format("即将安装索引%d的主密钥、pin密钥、mac密钥、data密钥", keyIndex).getBytes());
		kcvInfo.nCheckMode = 0;
//		byte[] keyTempData=ISOUtils.hex2byte("11223344556677881122334455667788");
		byte[] keyTempData = new byte[keyLen];
		for(int i=0;i<keyLen;i++)// 因海外固件的各个密钥索引的密钥值要不一样，故安装这边全部采用随机的方式
			keyTempData[i] = (byte) (Math.random()*255);
		
		if(algMode==10)// CBC模式
		{	
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, keyLen, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo);
			// 安装DES模式的主密钥
			if(JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)1, (byte)keyIndex, keyLen, keyTempData, kcvInfo)==NDK_OK)// 密文方式安装
				strAppend.append("CBC模式主密钥成功、");
			else
				strAppend.append("CBC模式主密钥失败、");
			String iv = "1122334455667788";
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// TPK
			if(JniNdk.JNI_Sec_LoadKey_CBC((byte)1, (byte)2, (byte)keyIndex, (byte)keyIndex, ISOUtils.hex2byte(iv), keyTempData, keyLen, kcvInfo)==NDK_OK)
				strAppend.append("CBC模式pin密钥成功、");
			else
				strAppend.append("CBC模式pin密钥失败、");
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			//TAK
			if(JniNdk.JNI_Sec_LoadKey_CBC((byte)1, (byte)3, (byte)keyIndex, (byte)keyIndex, ISOUtils.hex2byte(iv), keyTempData, keyLen, kcvInfo)==NDK_OK)
				strAppend.append("CBC模式mac密钥成功、");
			else
				strAppend.append("CBC模式mac密钥失败、");
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// TDK
			if(JniNdk.JNI_Sec_LoadKey_CBC((byte)1, (byte)4, (byte)keyIndex, (byte)keyIndex, ISOUtils.hex2byte(iv), keyTempData, keyLen,kcvInfo)==NDK_OK)
				strAppend.append("CBC模式data密钥成功");
			else
				strAppend.append("CBC模式data密钥失败");
		}
		else
		{	
//			//手动安装TLK
//			if (JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), kcvInfo)!=NDK_OK) {
//				gui.cls_show_msg1_record(TAG, "installTLK", 2, "TLK安装失败");
//				
//			}
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(0|algMode), (byte)0, (byte)1, keyLen, ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo);
			// SM4和AES要先安装TLK
			
			if(JniNdk.JNI_Sec_LoadKey((byte)(0|algMode), (byte)(1|algMode), (byte)1, (byte)keyIndex, keyLen, keyTempData, kcvInfo)==NDK_OK)
				strAppend.append("主密钥成功、");
			else
				strAppend.append("主密钥失败、");
			
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// TPK
			if(JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(2|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, keyTempData, kcvInfo)==NDK_OK)
				strAppend.append("pin密钥成功、");
			else
				strAppend.append("pin密钥失败、");
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// TAK
			if(JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(3|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, keyTempData, kcvInfo)==NDK_OK)
				strAppend.append("mac密钥成功、");
			else
				strAppend.append("mac密钥失败、");
			for(int i=0;i<keyLen;i++)
				keyTempData[i] = (byte) (Math.random()*255);
			// TDK
			if(JniNdk.JNI_Sec_LoadKey((byte)(1|algMode), (byte)(4|algMode), (byte)keyIndex, (byte)keyIndex, keyLen, keyTempData, kcvInfo)==NDK_OK)
				strAppend.append("data密钥成功");
			else
				strAppend.append("data密钥失败");
		}
		return gui.cls_show_msg1(1, "【点击[取消]退出安装】，安装索引%d的%s",keyIndex,strAppend);
	}
	
	// mpos方式自检-密钥检测
	private void sec_mpos_keyload(int keyLen)
	{
		/*private & local definition*/
		int keyIndex;
		K21DeviceResponse response = null;
		byte[] retContent = new byte[128];
		String index;
		
		/*process body*/
		while(true)
		{
			K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
			try {
				k21ControllerManager.connect();
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			// 测试前置mpos安装TLK密钥
			byte[] pack_TLK = CalDataLrc.mposPack(new byte[]{0x1A,0x02},ISOUtils.hex2byte("020500"+keyLen+TRANSMIT_KEY.substring(0, keyLen*2)+"0100040000000000"));
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TLK), null);
			
			int returnValue=gui.cls_show_msg("自检-密钥检测(mpos方式)\n0.安装所有密钥\n1.安装索引密钥\n2.擦除密钥\n3.密钥检测\n");
			switch (returnValue) 
			{
			case '0':
				for (int i = 1; i <= 255; i++) 
				{
					mpos_install_key(keyLen,i, retContent, response);
				}
				break;
				
			case '1':// 安装特定索引密钥
				gui.cls_printf("请输入索引值:".getBytes());
				keyIndex=gui.JDK_ReadData(TIMEOUT_INPUT,1);
				if(keyIndex<0||keyIndex>255)
				{
					gui.cls_show_msg1(1, "密钥索引输入范围错误");
					return;
				}
				mpos_install_key(keyLen,keyIndex, retContent, response);
				break;
				
			case '2':// 擦除密钥
				gui.cls_show_msg1(2, "即将擦除1-200索引的密钥");
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
				retContent = response.getResponse();
				if(ISOUtils.hexString(retContent, 9, 2).equals("00")==true)
				{
					gui.cls_show_msg1(2, "1-255索引密钥擦除成功");
				}
				break;
				
			case '3':
				StringBuffer strBuffer = new StringBuffer();
				gui.cls_printf("请输入索引值:".getBytes());
				keyIndex=gui.JDK_ReadData(TIMEOUT_INPUT,1);
				if(keyIndex<0||keyIndex>255)
				{
					gui.cls_show_msg1(1, "密钥索引输入范围错误");
					return;
				}
				index = Integer.toHexString(keyIndex).length()==1?("0"+Integer.toHexString(keyIndex)):Integer.toHexString(keyIndex);
				// pin密钥
				byte[] TPK_check = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("02"+index+index+"0000000002"));
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(TPK_check), null);
				retContent = response.getResponse();
				if(ISOUtils.dumpString(retContent, 9, 2).equals("00")==true)
					strBuffer.append("索引"+keyIndex+"：Pin密钥存在\n");
				else
					strBuffer.append("索引"+keyIndex+"：Pin密钥不存在\n");
				// mac密钥
				byte[] TAK_check = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("03"+index+index+"0000000002"));
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(TAK_check), null);
				retContent = response.getResponse();
				if(ISOUtils.dumpString(retContent, 9, 2).equals("00")==true)
					strBuffer.append("索引"+keyIndex+"：Mac密钥存在\n");
				else
					strBuffer.append("索引"+keyIndex+"：Mac密钥不存在\n");
				gui.cls_show_msg(strBuffer.toString()+"任意键继续测试");
				break;

			case ESC:
				k21ControllerManager.close();
				return;
			}
			k21ControllerManager.close();
		}
	}
	
	// 巴西海外要求每个索引的密钥值是不相同的，要修改mpos的安装密钥值
	private void mpos_install_key(int keyLen,int keyIndex,byte[] retContent,K21DeviceResponse response)
	{
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try {
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
			gui.cls_show_msg("line %d:抛出异常,任意键退出",Tools.getLineInfo());
			return;
		}
		
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		StringBuffer strAppend = new StringBuffer();
		String index,strKeyLen;
		
		gui.cls_show_msg1(1, "即将安装索引%d的主密钥、pin密钥、mac密钥、data密钥", keyIndex);
		index = Integer.toHexString(keyIndex).length()==1?("0"+Integer.toHexString(keyIndex)):Integer.toHexString(keyIndex);
		LoggerUtil.v("index:"+index);
		LoggerUtil.v("len:"+keyLen);
		strKeyLen = Integer.toString(keyLen).length()==1?("0"+keyLen):Integer.toString(keyLen);
		LoggerUtil.v("mpos:"+strKeyLen);
		byte[] tempData = new byte[keyLen];
		// TMK-主密钥
		for (int i = 0; i < keyLen; i++) {
			tempData[i] = (byte) (Math.random()*255);
		}
		LoggerUtil.v(ISOUtils.hexString(tempData,keyLen));
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02},ISOUtils.hex2byte("02"+index+"00"+strKeyLen+ISOUtils.hexString(tempData,keyLen)+"0100040000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if(ISOUtils.dumpString(retContent, 9, 2).equals("00")==true)
			strAppend.append("主密钥成功、");
		else
			strAppend.append("主密钥失败、");
		// TPK
		for (int i = 0; i < tempData.length; i++) {
			tempData[i] = (byte) (Math.random()*255);
		}
		byte[] pack_TPK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("02"+index+index+"00"+strKeyLen+ISOUtils.hexString(tempData,keyLen)+"00040000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK), null);
		retContent = response.getResponse();
		if(ISOUtils.dumpString(retContent, 9, 2).equals("00")==true)
			strAppend.append("pin密钥成功、");
		else
			strAppend.append("pin密钥失败、");
		// TAK
		for (int i = 0; i < tempData.length; i++) {
			tempData[i] = (byte) (Math.random()*255);
		}
		byte[] pack_TAK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("03"+index+index+"00"+strKeyLen+ISOUtils.hexString(tempData,keyLen)+"00040000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK), null);
		retContent = response.getResponse();
		if(ISOUtils.dumpString(retContent, 9, 2).equals("00")==true)
			strAppend.append("mac密钥成功、");
		else
			strAppend.append("mac密钥失败、");
		// TDK
		for (int i = 0; i < tempData.length; i++) {
			tempData[i] = (byte) (Math.random()*255);
		}
		byte[] pack_TDK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("01"+index+index+"00"+strKeyLen+ISOUtils.hexString(tempData,keyLen)+"00040000000000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK), null);
		retContent = response.getResponse();
		if(ISOUtils.dumpString(retContent, 9, 2).equals("00")==true)
			strAppend.append("data密钥成功");
		else
			strAppend.append("data密钥失败");
		gui.cls_show_msg1(2, "安装索引%d的%s(mpos无法安装Data密钥，自检检测该密钥不存在属于正常的)",keyIndex,strAppend);
		k21ControllerManager.close();
	}
	
	private void  sec_keyloadTIK(int keyLen)
	{
		/*private & local definition*/
		int ret = -1,keyIndex;
		byte[] keyValueIn = new byte[32];
		byte[] ksnIn = new byte[17];
		/*process body*/
		while(true)
		{
			int returnValue=gui.cls_show_msg("安全综合测试(dukpt密钥)\n0.安装所有密钥\n1.安装索引密钥\n2.擦除密钥\n");
			switch (returnValue) 
			{
			case '0':
				Arrays.fill(ksnIn, (byte) 0xff);
				for (int i = 1; i <= 21; i++) 
				{
					for (int j = 0; j < keyValueIn.length; j++) {
						keyValueIn[j] = (byte) (Math.random()*255);
					}
					// 明文安装方式
					if((ret = JniNdk.JNI_Sec_LoadTIK((byte)i, (byte)0, (byte)keyLen, keyValueIn, ksnIn, kcvInfo))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "sec_keyloadTIK", g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
						return;
					}
					else
					{
						gui.cls_printf(("第"+i+"组dukpt密钥安装成功").getBytes());
					}
				}
				break;
				
			case '1':// 安装特定索引密钥
				gui.cls_printf("请输入索引值:".getBytes());
				keyIndex=gui.JDK_ReadData(TIMEOUT_INPUT,1);
				if(keyIndex<0||keyIndex>21)
				{
					gui.cls_show_msg1(1, "密钥组输入范围错误");
					return;
				}
				for (int j = 0; j < keyValueIn.length; j++) {
					keyValueIn[j] = (byte) (Math.random()*255);
				}
				// 明文安装方式
				if((ret = JniNdk.JNI_Sec_LoadTIK((byte)keyIndex, (byte)0, (byte)16, keyValueIn, ksnIn, kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "sec_keyloadTIK", g_keeptime, "line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					return;
				}
				else
					gui.cls_show_msg("第%d组dukpt密钥安装成功", keyIndex);
				break;
				
			case '2':// 擦除密钥
				gui.cls_show_msg1(2, "即将擦除1-255索引的密钥");
				eraseLoadTLK();
				gui.cls_show_msg1(2, "1-255索引密钥擦除成功");
				break;
				
			case ESC:
				return;
			}
		}
	}
}
