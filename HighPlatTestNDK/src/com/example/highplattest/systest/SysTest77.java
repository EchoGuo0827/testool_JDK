package com.example.highplattest.systest;

import java.util.Random;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest.java
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180306
 * directory 		: 
 * description 		: pin压力测试用例
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj		   20180306	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest77 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest77.class.getSimpleName();
	private final String TESTITEM = "pin压力";
	private final int MAXWAITTIME = 30*1000;
	Gui gui = new Gui(myactivity, handler);
	private final int DEFAULT_CNT_VLE2 = 50;
	/*----------global variables declaration------------------------*/
	int tk = 0, keyin = 0;
	PacketBean packet = new PacketBean();
	private SecKcvInfo kcvInfo ;
	public void systest77() 
	{
		if(GlobalVariable.gSequencePressFlag)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			//安全
			kcvInfo = new SecKcvInfo();
			int nkeyIn = gui.cls_show_msg("pin综合测试\n0.pin随机密码键盘压力\n1.密码键盘快速测试");
			switch (nkeyIn) 
			{

			case '0':
				pin_random_press();
				break;
				
			case '1':
				pin_quick_test();
				break;
				

			case ESC:
				JniNdk.JNI_Sec_KeyErase();
				byte[] pszOwner=new byte[100];
				JniNdk.JNI_Sec_GetKeyOwner(pszOwner.length, pszOwner);
			
				//设置成共享密钥 擦除密钥后会自动装TLK
				if(!ISOUtils.byteToStr(pszOwner).equals("*")){
					JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), new SecKcvInfo());
				}
				intentSys();
				return;

			}
		}
	}

	/**
	 * 随机密码键盘压力
	 */
	private void pin_random_press() 
	{
		/*private & local definition*/
		int ret = 0;
		StringBuffer str = new StringBuffer();
		String mainKey = "11111111111111111111111111111111"; //String tmkCmp = "82E13665B4624DF5";// 密钥明文为32个1
		String tpk1Key = "145F5C6E3D91445782E13665B4624DF5";// 明文为8个15
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2));
		// 测试前置,擦除所有密钥
		/*if((ret = JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime,"line %d:%s测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
			return;
		}
		handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_BACK_ID, R.drawable.keyboard));*/
		// 安装TMK, ID=5
		kcvInfo.nCheckMode = 0;
		if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte) 0,
				(byte) 5, 16, ISOUtils.hex2byte(mainKey), kcvInfo)) != NDK_OK) 
			gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:getPin初始化,安装TMK失败(%d)", Tools.getLineInfo(),ret);

		// 安装TPK1(8bytes), ID=2,密文安装/**原先TPK是按照8字节安装，A7安装密钥要至少16字节*/
		if ((ret = JniNdk.JNI_Sec_LoadKey((byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(),
				(byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 5, (byte) 6, 16,ISOUtils.hex2byte(tpk1Key), kcvInfo)) != NDK_OK) 
			gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:getPin初始化,安装TPK1失败(%d)", Tools.getLineInfo(),ret);

		// 表示明文安装
		if ((ret = JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 0,
				(byte) 7, 16, ISOUtils.hex2byte("17171717171717171919191919191919"), kcvInfo)) != NDK_OK) 
			gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:getPin初始化,明文安装TPK2失败(%d)", Tools.getLineInfo(),ret);
		int cnt=packet.getLifecycle();
		int succ=0;
		int bak=packet.getLifecycle();
		byte[] szPinOut=new byte [9];
		byte[] PinDesOut=new byte [9];
		for (int j = 0; j < szPinOut.length; j++) 
		{
			szPinOut[j]=0;
			PinDesOut[j]=0;
		}
		int PINTIMEOUT_MAX = 200*1000;
		String szPan = "6225885916163157";
		String[] pinNumber={"0","1234","12345","123456","1234567","12345678","123456789","1234567890","12345678901","123456789012"};
		String[] pinResult={"0000000000000000","2E4000EFF86DCA6A",
				"E9F2EDDD59857FD0","98ECAB1BC863DF00","868B93397A1D59EB","6B76244371639AF9","C6451832C2533C9C",
				"45444B8B0E35DC6F","8A4338D8EEAB9446","2DACBCC593732BB8"};
		int randNumber=0;
		/*process body*/
		while(cnt>0)
		{
			if(gui.cls_show_msg1(2,"压力测试中...\n还剩%d次（已成功%d次）,[取消]退出测试...",cnt,succ)==ESC)
				break;
			Random rand = new Random();
			randNumber = rand.nextInt(9 - 0 + 1) + 0;
			LoggerUtil.e(""+randNumber);
			pinFlag=-1;
			cnt--;
			
			if((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(),pinlistener))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:第%d次:PIN事件注册失败(%d)", Tools.getLineInfo(), bak-cnt,ret);
				break;
			}
			if((ret = touchscreen_getnum(str))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime,"line %d:随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(),ret);
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue());
				continue;
			}
			
			if(randNumber==0){
				str.append("请尽快按确认键");
			}else
			{
				str.append("尽快输入");
				str.append(pinNumber[randNumber]);
				str.append("并确认...");
			}
		
			gui.cls_printf(str.toString().getBytes());
			String pinLength=randNumber==0?"0":Integer.toString(pinNumber[randNumber].length());
			if ((ret = JniNdk.JNI_Sec_GetPin((byte) 7, pinLength, szPan, null, (byte) 3, PINTIMEOUT_MAX)) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:第%d次:获取PIN Block失败(ret = %d)", Tools.getLineInfo(), bak-cnt, ret);
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue());
				continue;
			}
			JniNdk.JNI_Sys_Delay(5);
			if(GlobalVariable.sdkType==SdkType.SDK3)
			{
				if(pinFlag!=EM_SYS_EVENT.SYS_EVENT_PIN.getValue())
				{
					gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:第%d次：没有监听到pin事件（%d）", Tools.getLineInfo(),bak-cnt,pinFlag);
					UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue());
					continue;
				}
			}
			szPinOut = getPinInput(str.toString(),handler);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte(pinResult[randNumber]), 8) == false) 
			{
				gui.cls_show_msg1_record(TAG, "pin_random_press",g_keeptime, "line %d:第%d次:校验失败(PinDesOut = %s)", Tools.getLineInfo(), bak-cnt, Dump.getHexDump(szPinOut));
				UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue());
				continue;
			}
			succ++;
			// 解绑事件
			if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue())) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "pin_random_press", g_keeptime, "line %d:pin事件解绑失败(%d)",Tools.getLineInfo(), ret);
				break;
			}
		}
		
		gui.cls_show_msg1_record(TAG, "pin_random_press", g_time_0,"%s压力测试完成,已执行次数为%d,成功为%d次",TESTITEM,bak-cnt,succ);
	}
	
	/**
	 * 固定密码键盘压力
	 */
	private void pin_quick_test()
	{
		/*private & local definition*/
		int ret = 0;
		StringBuffer str = new StringBuffer();
		
		// 表示明文安装
		JniNdk.JNI_Sec_LoadKey((byte) 0, (byte) EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte) 0,(byte) 7, 16, ISOUtils.hex2byte("17171717171717171919191919191919"), kcvInfo);
		
		byte[] szPinOut=new byte [9];
		byte[] PinDesOut=new byte [9];
		for (int j = 0; j < szPinOut.length; j++) 
		{
			szPinOut[j]=0;
			PinDesOut[j]=0;
		}
		int PINTIMEOUT_MAX = 200*1000;
		String szPan = "6225885916163157";
		pinFlag=-1;
		if((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue(),pinlistener))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "pin_quick_test",g_keeptime, "line %d:PIN事件注册失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = touchscreen_getnum(null))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "pin_quick_test",g_keeptime,"line %d:随机数字键盘初始化失败(ret = %d)", Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_printf("请输入6位密码".getBytes());
		if ((ret = JniNdk.JNI_Sec_GetPin((byte) 7, "6", szPan, null, (byte) 3, PINTIMEOUT_MAX)) != NDK_OK) {
			gui.cls_show_msg1_record(TAG, "pin_quick_test",g_keeptime, "line %d:获取PIN Block失败(ret = %d)", Tools.getLineInfo(), ret);
			return;
		}
		JniNdk.JNI_Sys_Delay(5);
		if(GlobalVariable.sdkType==SdkType.SDK3)
		{
			if(pinFlag!=EM_SYS_EVENT.SYS_EVENT_PIN.getValue())
			{
				gui.cls_show_msg1_record(TAG, "pin_quick_test",g_keeptime, "line %d:没有监听到pin事件（%d）", Tools.getLineInfo(),pinFlag);
				return;
			}
		}
		szPinOut = getPinInput(str.toString(),handler);
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PIN.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "pin_quick_test", g_keeptime, "line %d:pin事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
	}
	
	
}
