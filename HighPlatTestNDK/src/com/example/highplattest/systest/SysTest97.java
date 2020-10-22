package com.example.highplattest.systest;

import java.util.Arrays;
import android.util.Log;
import com.example.highplattest.R;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_DES;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_ALG;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_MAC;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.EmvUtils;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest89.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20191030
 * directory 		: 
 * description 		: EMV优化综合案例
 * related document :
 * history 		 	: author			date			remarks
 * 					变更说明						变更时间			变更人
 * 				根据开发建议修改案例加解密算法为SM4。		20200811	陈丁
 * 				根据开发建议增加海外加解密算法为DES。使用CalcDes接口 		20200821 	陈丁
 * 				去除MAC_SM4_UNIONPAY运算，不再区分国内和海外版本  20201009    陈丁	
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest97 extends DefaultFragment {
	private final String TESTITEM = "EMV优化综合案例";
	private final String TAG = SysTest97.class.getSimpleName();
	private Gui gui;
	private String fileName="SysTest97";
	private _SMART_t type = _SMART_t.CPU_A;
	private Config config;
	private int felicaChoose=0;
	int ret=-1;
	//蜂鸣器节点
	private final String beepnode="/sys/class/paymodule_k21/beep";
	//安全
	private SecKcvInfo secKcvInfo = new SecKcvInfo();
	byte[] buf1_tmp = ISOUtils.hex2byte("31313131313131313131313131313131");
	byte[] buf1_in = ISOUtils.hex2byte("90929CA41B8DD3B287090DD56F3C388D");
	byte[] buf_tak= ISOUtils.hex2byte("1626E401BFF11B1B64F74D2139EF27FA");
	byte[] udesin = new byte[32];
	byte[] udesout = new byte[32];
	public void systest97(){
		gui=new Gui(myactivity, handler);
		config=new Config(myactivity, handler);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("EMV优化案例\n0.前置配置\n1.加解密和蜂鸣器测试\n2.射频卡和蜂鸣器测试\n3.Beep测试");
			switch (nkeyIn) 
			{
			case '0':
				int nkeyin2=gui.cls_show_msg("前置配置\n0.密钥安装\n1.射频卡配置\n");
				switch(nkeyin2){
				case '0':
//					int nkeyin3=gui.cls_show_msg("海内外\n0.国内\n1.海外\n");
//					switch (nkeyin3) {
//					case '0':
//						isoverseas=false;
//						gui.cls_show_msg1(1, "当前选择国内--安装SM4");
//						break;
//
//					case '1':
//						isoverseas=true;
//						gui.cls_show_msg1(1, "当前选择海外--安装DES");
//						break;
//					default:
//						break;
//					}
					if ((ret=JniNdk.JNI_Sec_KeyErase())!=NDK_OK) {
						
						gui.cls_show_msg1_record(TAG, "systest2",g_keeptime, "line %d:清除密钥失败，ret=%d", Tools.getLineInfo(),ret);	
					}
					//国内
//					if (!isoverseas) {
						secKcvInfo.nCheckMode=0;
						secKcvInfo.nLen=4;
						if((ret=JniNdk.JNI_Sec_LoadKey((byte)(0|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), 
								(byte)0, (byte)1, 16, buf1_tmp, secKcvInfo))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, "systest109", g_keeptime, "line %d:安装TMK密钥失败,ret=%d", Tools.getLineInfo(),ret);
							return;
						}
						//TDK
						if((ret=JniNdk.JNI_Sec_LoadKey((byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte)(EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal()|EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), 
								(byte)1, (byte)5, 16, buf1_in, secKcvInfo))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, "systest109", g_keeptime, "line %d:安装TDK密钥失败,ret=%d", Tools.getLineInfo(),ret);
							return;
						}
//					}
//					//海外
//					else {
						byte[] buf1_tmp = ISOUtils.hex2byte("3672c2bc7f17f29c65873586bc7f17f23672c2bc7f17f29c");
						byte[] buf1_in = ISOUtils.hex2byte("313131313131313131313131313131313131313131313131");
						byte[] buf3_in = ISOUtils.hex2byte("1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B");
				
						secKcvInfo.nCheckMode=0;
						secKcvInfo.nLen=4;
						if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, (byte)(byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)0, (byte)1, 24, buf1_tmp, secKcvInfo))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, "systest109", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
							return;
						}
						//双倍长TDK
						if((ret=JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)1, (byte)5, 
								16, buf1_in, secKcvInfo))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, "systest109", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
							return;
						}
						//三倍长TDK
						if((ret=JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)1, (byte)6, 
								24, buf3_in, secKcvInfo))!=NDK_OK)
						{
							gui.cls_show_msg1_record(TAG, "systest109", g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
							return;
						}
						
						
						
//					}
					
                	gui.cls_show_msg1(2,"密钥初始化成功!!!");
					break;
				case '1':
					type = config.rfid_config();
					if(type==_SMART_t.FELICA){
						felicaChoose=config.felica_config(); //普通返回0 八达通返回1
					}
					if(rfidInit(type)!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "systest2",g_keeptime, "line %d:初始化失败！请检查配置是否正确ret=%d", Tools.getLineInfo(),ret);
					}
					else
					{
						gui.cls_show_msg1(2,"%s初始化成功!!!", type);
					}
					break;
				}
				break;
			case '1':
				SecBeeptest();
				break;
			case '2':
				RfBeeptest();
				break;
			case '3':
				beeptest();
				break;
			
			default:
				break;
			case ESC:
				intentSys();
				return;
				
			}
		}
	}

	//加解密和蜂鸣器的测试
	private void SecBeeptest() {
		boolean flag=false;   //true 在调用加解密过程中去修改节点
		/**
		 * case1因为安卓端和K21端均卡住。无法往下执行，故屏蔽。换成新增并发测试
		 */
//		//case1 蜂鸣器响的过程中，调用加解密。应不响  //现存在修改Beep节点后。安卓端和K21端均卡住。无法往下执行。只有Beep结束后才继续  
//		gui.cls_show_msg("在蜂鸣器响的过程中,调用加解密，应不响。按任意键继续");
//		Log.d("eric_chen", "start--");
//		EmvUtils.setNodeString(beepnode, "2000");
//		oldtime=System.currentTimeMillis();
//		while (time<2) {
//			gui.cls_printf("正在进行2s的加解密计算。。".getBytes());
//			time=(int) Tools.getStopTime(oldtime);
//			if ((ret=secenc(flag))!=NDK_OK) {
//				gui.cls_show_msg1_record(TAG, "systest89", g_keeptime, "line %d:加解密失败", Tools.getLineInfo());
//				break;
//			}
//		}
//		gui.cls_show_msg1(1,"case1通过");
		//case2 调用加解密过程中去修改蜂鸣器。应正常响  //现存在修改Beep节点后。安卓端和K21端均卡住。无法往下执行。只有Beep结束后才继续
		gui.cls_show_msg("在调用加解密过程中去修改蜂鸣器。应正常响 。按任意键继续");
		flag=true;
		for (int i = 0; i < 10; i++) {
			gui.cls_printf("正在进行加解密操作和蜂鸣器操作。。".getBytes());
			if ((ret=secenc(flag))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "systest89", g_keeptime, "line %d:加解密失败", Tools.getLineInfo());
			}
			
		}
		gui.cls_show_msg1(1,"case2通过");
		//case3 先调用加解密，加解密结束后修改蜂鸣器节点。 预期加解密和蜂鸣器都正常
		gui.cls_show_msg("调用加解密，加解密结束后修改蜂鸣器节点。 预期加解密和蜂鸣器都正常 。按任意键继续");
		flag=false;
		for (int i = 0; i < 50; i++) {
			gui.cls_printf("正在进行加解密操作。。".getBytes());
			if ((ret=secenc(flag))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "systest89", g_keeptime, "line %d:加解密失败", Tools.getLineInfo());
			}
			
		}
		gui.cls_show_msg1(1,"蜂鸣器即将响---");
		EmvUtils.setNodeString(beepnode, "200");
		
		gui.cls_show_msg1_record(TAG, "systest89", g_time_0, "加解密和蜂鸣器测试通过");
		
		
		
}

	// 加解密方法封装
	private int secenc(boolean flag) 
	{
		ret = -1;
		// if (!isoverseas) {
		if ((ret = JniNdk.JNI_Sec_CalcDes((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(),(byte) 5, udesin, 16, udesout,
						(byte) EM_SEC_DES.SEC_SM4_ENCRYPT.secdes())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,"line %d:加解密失败", Tools.getLineInfo());
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		// 16字节MAC_SM4运算耗时
		if ((ret = JniNdk.JNI_Sec_LoadKey((byte) (EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal() | EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()),
						(byte) (EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal() | EM_SEC_KEY_ALG.SEC_KEY_SM4.seckeyalg()), (byte) 1, (byte) 2, 16, buf_tak,secKcvInfo)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,"line %d:密钥安装失败", Tools.getLineInfo());
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		byte[] szDataIn = new byte[1024];
		byte[] szMac = new byte[16];
		Arrays.fill(szDataIn, (byte) 0x20);
		if ((ret = JniNdk.JNI_Sec_GetMac((byte) 2, szDataIn, 1024, szMac,
				(byte) EM_SEC_MAC.SEC_MAC_SM4.ordinal())) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,"line %d:16字节MAC_SM4失败", Tools.getLineInfo());
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		// 欧洲版本不支持MAC_SM4_UNIONPAY运算

		// if((ret=JniNdk.JNI_Sec_GetMac((byte)2, szDataIn, 16, szMac,
		// (byte)6))!=NDK_OK)
		// {
		// gui.cls_show_msg1_record(TAG, "systest109", g_keeptime,
		// "line %d:16字节MAC_SM4_UNIONPAY失败", Tools.getLineInfo());
		// return ret;
		// }
		// if (flag) {
		// EmvUtils.setNodeString(beepnode, "200");
		// }

		// }
		// else {

		if ((ret = JniNdk.JNI_Sec_CalcDes((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte) 5,udesin, (byte) 8, udesout, (byte) (0 | (2 << 1)))) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,"line %d:%s加密测试失败(%d)", Tools.getLineInfo(), TESTITEM, ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		if ((ret = JniNdk.JNI_Sec_CalcDes((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte) 6,udesin, (byte) 8, udesout, (byte) (0 | (3 << 1)))) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,"line %d:%s加密测试失败(%d)", Tools.getLineInfo(), TESTITEM);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		// }
		return ret;

	}

	// 射频卡和蜂鸣器测试
	private void RfBeeptest() {
		boolean flag = false;
		/**
		 * case1因为安卓端和K21端均卡住。无法往下执行，故屏蔽。换成新增并发测试
		 */
		// //case1 蜂鸣器响的过程中，进行射频卡操作。应不响
		// //现存在修改Beep节点后。安卓端和K21端均卡住。无法往下执行。只有Beep结束后才继续
		// gui.cls_show_msg("在蜂鸣器响的过程中,进行射频卡操作。应不响。请放置卡片,按任意键继续");
		// Log.d("eric_chen", "start--");
		// EmvUtils.setNodeString(beepnode, "2000");
		// oldtime=System.currentTimeMillis();
		// while (time<2) {
		// gui.cls_printf("正在进行2s的射频卡操作。。".getBytes());
		// time=(int) Tools.getStopTime(oldtime);
		// if ((ret=RFtest(flag))!=NDK_OK) {
		// gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,
		// "line %d:射频卡操作失败", Tools.getLineInfo());
		// break;
		// }
		// }
		// gui.cls_show_msg1(1,"case1通过");
		// case2 进行射频卡操作过程中去修改蜂鸣器。应正常响
		// //现存在修改Beep节点后。安卓端和K21端均卡住。无法往下执行。只有Beep结束后才继续
		gui.cls_show_msg("在调用射频卡操作过程中去修改蜂鸣器。应正常响 。请放置卡片,按任意键继续");
		flag = true;
		for (int i = 0; i < 10; i++) {
			gui.cls_printf("正在进行射频卡操作和蜂鸣器操作。。".getBytes());
			if ((ret = RFtest(flag)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,
						"line %d:射频卡操作失败", Tools.getLineInfo());
			}

		}
		gui.cls_show_msg1(1, "case2通过");
		// case3 先射频卡操作，操作结束后修改蜂鸣器节点。 预期加解密和蜂鸣器都正常
		gui.cls_show_msg("先射频卡操作，操作结束后修改蜂鸣器节点。 预期加解密和蜂鸣器都正常 。按任意键继续");
		flag = false;
		for (int i = 0; i < 20; i++) {
			gui.cls_printf("正在进行射频卡操作。。".getBytes());
			if ((ret = RFtest(flag)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "systest89", g_keeptime,
						"line %d:射频卡操作失败", Tools.getLineInfo());
			}
		}
		gui.cls_show_msg1(1, "蜂鸣器即将响---");
		EmvUtils.setNodeString(beepnode, "200");

		gui.cls_show_msg1_record(TAG, "systest89", g_time_0, "射频卡和蜂鸣器测试通过");
	}

	// 封装射频卡操作
	private int RFtest(boolean flag) {
		int ret = -1;
		int[] cpuRevLen = new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];

		if ((ret = SmartRegistEvent(type)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:rf事件注册失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		if ((ret = rfid_detect(type, UidLen, UidBuf)) != NDK_OK) {
			rfidDeactive(type, 0);
			UnRegistAllEvent(new EM_SYS_EVENT[] { EM_SYS_EVENT.SYS_EVENT_RFID });
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d: %s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_detect_fail), ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		if ((ret = rfidActive(type, felicaChoose, UidLen, UidBuf)) != NDK_OK) {
			rfidDeactive(type, 0);
			UnRegistAllEvent(new EM_SYS_EVENT[] { EM_SYS_EVENT.SYS_EVENT_RFID });
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_active_fail), ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		if ((ret = rfidApduRw(type, cpuRevLen, UidBuf)) != NDK_OK) {
			rfidDeactive(type, 0);
			UnRegistAllEvent(new EM_SYS_EVENT[] { EM_SYS_EVENT.SYS_EVENT_RFID });
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_code_err), ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		// 关闭场
		if ((ret = rfidDeactive(type, 0)) != NDK_OK) {
			rfidDeactive(type, 0);
			UnRegistAllEvent(new EM_SYS_EVENT[] { EM_SYS_EVENT.SYS_EVENT_RFID });
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_close_fail), ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		// 解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:smart事件解绑失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		if (flag) {
			EmvUtils.setNodeString(beepnode, "200");
		}
		return ret;

	}

	//beep 节点测试
	private void beeptest() 
	{
		String beepnode="/sys/class/paymodule_k21/beep";
		gui.cls_show_msg("能听到蜂鸣器声音且时间约为3S左右即测试通过。按任意键继续");
		Log.d("eric_chen", "start------beep");
		EmvUtils.setNodeString(beepnode, "3000");
		Log.d("eric_chen", "end------beep");
		gui.cls_show_msg1_record(fileName,"systest89",g_time_0, "%s测试通过(长按确认键退出测试)", TESTITEM);
	}

}
