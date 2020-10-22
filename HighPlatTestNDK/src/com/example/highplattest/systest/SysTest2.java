package com.example.highplattest.systest;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import com.example.highplattest.R;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Config;
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.newland.AnalogSerialManager;
import android.util.Log;

/************************************************************************
 * module 			: 射频卡综合
 * file name 		: Systest2.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160902 
 * directory 		: 
 * description 		: 射频卡（压力、性能、读写压力、挥卡测试）
 * related document : 
 * history 		 	: 变更记录																	变更时间			变更人员
 *			  		  根据测试人员建议修改增加文字区别带C不带C，普通Felica和八达通。							20200426	 	陈丁
 *					修改射频卡综合场景压力测试。增加失败标志位，在案例失败解绑事件机制后，重新注册事件机制，确保案例继续执行         20200520                        陈丁
 *					新增全局变量区分M0带认证和不带认证。相关案例修改										20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest2 extends DefaultFragment
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest2.class.getSimpleName();
	public final String TESTITEM = "RF Card Capability and Press";
	private final int DEFAULT_CNT_VLE1 = 15000;//读写压力测试最小次数！一般若不是任务紧急不应小于该数值，否则可能测试不出问题
	private final int DEFAULT_CNT_VLE2 = 5000;// 流程压力测试最小次数!一般若不是任务紧急不应小于该数值，否则坑你测试不出问题
	private final int DEFAULT_CNT_VLE3 = 4000;// 跨扇区压力测试最小次数！一般若不是任务紧急不应小于该数值，否则可能测试不出问题
	private final int DEFAULT_WAVE_NUM = 100;
	private final int TEST_TIME = 10;
	private Config config;
	private Gui gui;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	/*----------global variables declaration------------------------*/
	private _SMART_t type = _SMART_t.CPU_A;
	private boolean isInit = false;
	//felica选择zhangxinj 2019/2/25
	private int felicaChoose=0;
	private int ret=-1;
	
	public void systest2()
	{
		// 与K21端连接初始化，修改初始化方式
	    sharedPreferences = myactivity.getSharedPreferences("RFCard", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		config = new Config(myactivity, handler);
		/**进入用例注销事件机制*/
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
		gui = new Gui(myactivity, handler);
		// 自动运行
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			type = config.rfid_config();
			if(type==_SMART_t.FELICA){
				felicaChoose=config.felica_config();
			}
			
			if(rfidInit(type)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "systest2",g_time_0, "line %d:"+myactivity.getString(R.string.systest_rf_init_fail), Tools.getLineInfo());
				return;
			}
			//流程压力
			g_CycleTime = 100;
			rfidPress();
			rfidAbility();
			//读写压力
			g_CycleTime = 100;
			rfidRwPress();
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			type = config.rfid_config();
			if(rfidInit(type)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "systest2",g_time_0, "line %d:"+myactivity.getString(R.string.systest_rf_init_fail), Tools.getLineInfo());
				return;
			}
			//流程压力
			g_CycleTime = DEFAULT_CNT_VLE2;
			rfidPress();
			rfidAbility();
			//读写压力
			g_CycleTime = DEFAULT_CNT_VLE1;
			rfidRwPress();
			//场景压力
			if(GlobalVariable.sdkType==SdkType.SDK3){
				g_CycleTime = DEFAULT_CNT_VLE2;
				sceneRfidPress();
			}
			else
				gui.cls_show_msg("场景压力仅支持配置为SDK3.0,可在assets配置文件中配置,点任意键继续");
			//跨扇区压力
			if(type==_SMART_t.MIFARE_1){
				g_CycleTime = DEFAULT_CNT_VLE3;
				M1_multisection_rw(_SMART_t.MIFARE_1);
			}
			return;
		}
		while(true)
		{	
			
			int returnValue=gui.cls_show_msg("射频卡综合测试\n0.配置\n1.流程压力\n2.性能测试\n3.读写压力\n4.挥卡测试\n5.机器人测试\n6.场景压力测试\n7.M1卡跨扇区\n8.异常测试\n9.A、B、Felica卡事件机制测试\n");
			//int returnValue=gui.cls_show_msg("射频卡综合测试\n0.配置\n1.流程压力\n2.性能测试\n3.读写压力\n4.挥卡测试\n5.机器人测试\n6.场景压力测试\n7.M1卡跨扇区\n8.异常测试\n9.初始化---");
			switch (returnValue) 
			{
			case '0':
				type = config.rfid_config();
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config(); //普通返回0 八达通返回1
				}
				if(rfidInit(type)!=NDK_OK)
				{
					isInit = false;
					gui.cls_show_msg1_record(TAG, "systest2",g_keeptime, "line %d:初始化失败！请检查配置是否正确ret=%d", Tools.getLineInfo(),ret);
				}
				else
				{
					isInit = true;
					gui.cls_show_msg1(2,"%s初始化成功!!!", type);
				}
				break;
				
			case ESC:
				intentSys();
				return;

			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
				initJudge(returnValue);
				break;
			case '8':
				abnormal();
				break;
			case '9':
				if (GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
					gui.cls_show_msg("A7平台不支持该组合寻卡");
					break;
				}
				Multiplecardtests();
				break;
				
			}
		}
	}
	
	//M1寻卡重启压力
	private void M1reboot() {
		int ret=-1,s = 1,k=0,succ = 0,i=0;
		byte[] UID = new byte[LEN_UID];
		int[] pUIDLen = new int[1];
		int[] rcvLen = new int[1];
		int[] pnDataLen = new int[1];
		byte[] out = new byte[16];
		byte[] out1 = new byte[16];
		int cnt = DEFAULT_CNT_VLE3,bak = 0;
		byte[][] RF_bak = new byte[64][LEN_BLKDATA+8];
		_SMART_t type = _SMART_t.MIFARE_1;
		gui.cls_show_msg( "进行M1卡寻卡重启压力,请将M1卡放置按任意键。该测试项只有寻不到M1卡才会停止。。。");
		//注册事件
		if((ret=SmartRegistEvent(type))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfidPress",g_keeptime, "line %d:rf事件注册失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		//寻卡
		if((ret = rfid_detect(type, pUIDLen, UID))!=SUCC)
		{
			 gui.cls_show_msg1(g_keeptime, "line %d:寻卡失败(%d)", Tools.getLineInfo(),ret);

		}
		gui.cls_show_msg1(1, "M1寻卡成功");
		
		//将数据存入数据库
		editor.putInt("RFrboot", 0);
		editor.putBoolean("isRFrboot", true);
		editor.commit();
		
      for (int z = 5; k >0; k--) {
		 gui.cls_show_msg1(1, "即将自动重启-----还有%d秒",k);
      }
		Tools.reboot(myactivity);
	}


	private void Multiplecardtests() {
		//测试前置  
		JniNdk.JNI_Rfid_PiccDeactivate((byte) 0); //下电
		
		JniNdk.JNI_Rfid_PiccType((byte) 0xc8);//设置只识别 A卡 B卡  F卡
		ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, rflistener);
		gui.cls_show_msg("初始化射频卡获取类型为ABF卡成功，请先放置A卡。等待两秒后点击。。。。。");
		Log.d("Multiplecardtests", rfFlag+" ");
		if(rfFlag!=16)
		{
			gui.cls_show_msg1_record(TAG, "Multiplecardtests",g_keeptime, "line %d:rf事件注册失败(%d)", Tools.getLineInfo(),ret);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			return;
		}
		rfFlag=-1;
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;//解绑事件
		Log.d("Multiplecardtests", rfFlag+" ");
		JniNdk.JNI_Rfid_PiccDeactivate((byte) 0); //下电	
		gui.cls_show_msg("请移卡。。。。。。。。移卡后点击任意键");
		ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, rflistener);
		gui.cls_show_msg("初始化射频卡获取类型为ABF卡成功，请放置B卡。等待三秒后点击。。。。。");
		Log.d("Multiplecardtests", rfFlag+" ");
		if(rfFlag!=16)
		{
			gui.cls_show_msg1_record(TAG, "Multiplecardtests",g_keeptime, "line %d:rf事件注册失败(%d)", Tools.getLineInfo(),ret);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			return;
		}
		rfFlag=-1;
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});//解绑事件
		
		JniNdk.JNI_Rfid_PiccDeactivate((byte) 0); //下电
		gui.cls_show_msg("请移卡。。。。。。。。移卡后点击任意键");
		ret=JniNdk.JNI_SYSRegisterEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(), MAXWAITTIME, rflistener);
		gui.cls_show_msg("初始化射频卡获取类型为ABF卡成功，请放置Felica卡。等待三秒后点击。。。。。");
		Log.d("Multiplecardtests", rfFlag+" ");
		if(rfFlag!=16)
		{
			gui.cls_show_msg1_record(TAG, "Multiplecardtests",g_keeptime, "line %d:rf事件注册失败(%d)", Tools.getLineInfo(),ret);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			return;
		}
		rfFlag=-1;
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});//解绑事件
		gui.cls_show_msg1_record(TAG, "Multiplecardtests",g_keeptime, "A卡B卡Felica卡事件机制测试通过", Tools.getLineInfo());
		
	}


	public void initJudge(int value) 
	{
		if(!isInit)
		{
			gui.cls_show_msg("射频卡未初始化,请先进行初始化,点任意键继续");
			return;
		}
		switch (value) 
		{
		
		case '1':
			rfidPress();  //流程压力
			break;
			
		case '2':
			rfidAbility();
			break;
			
		case '3':
			rfidRwPress();
			break;
			
		case '4':
			RFID_wave_press(false);
			break;
			
		case '5':
			rfid_robot_menu();
			break;
			
		case '6':
			if(GlobalVariable.sdkType==SdkType.SDK3)
			   sceneRfidPress();
			else
				gui.cls_show_msg("仅支持配置为SDK3.0,可在assets配置文件中配置,点任意键继续");
			break;
			
		case '7':
			M1_multisection_rw(_SMART_t.MIFARE_1);
			break;
			
		}
	}
	
	// NDK_RfidPiccResetCard+resume组合使用的场景
	private void sceneRfidPress() 
	{
		/*private & local definition*/
		int succ = 0,cnt=0,bak =0,ret = -1;
		int[] cpuRevLen = new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		int errflag=0;   //0 代表正常   1代表失败
		
		/*process body*/
		gui.setOverFlag(true);
		if(GlobalVariable.gSequencePressFlag){
			cnt = getCycleValue();
		}else
		{
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		}
		bak = cnt ;
		String typetem=" ";
		
		if (type==_SMART_t.FELICA) {
		 
			if (felicaChoose==1) {
				typetem="_8";
			}
			
		}
		gui.cls_show_msg("请在感应区放置%s卡,完成任意键继续",type);
		//注册事件,先监听到一次再在while中使用resume方法
		if((ret=SmartRegistEvent(type))!=NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER)
		{
			gui.cls_show_msg1_record(TAG, "SmartRegisterEventTask",g_keeptime, "line %d:第%d次:rf事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
			return;
		}
		JniNdk.JNI_Sys_Delay(3);
		if (rfFlag != EM_SYS_EVENT.SYS_EVENT_RFID.getValue()) 
		{
			gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime, "line %d:没有监听到rf事件(%d)",Tools.getLineInfo(), rfFlag);
		}
		rfFlag = -1;
		// M1卡寻卡方式进行选择
		while(cnt>0)
		{	
			//报错后，会解绑事件，在循环开头判断。如果解绑过事件就重新注册，确保不会失败
			if (errflag==1) {
				errflag=0;
				if((ret=SmartRegistEvent(type))!=NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER)
				{
					gui.cls_show_msg1_record(TAG, "SmartRegisterEventTask",g_keeptime, "line %d:第%d次:rf事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					return;
				}
				
			}
			//下电（单纯下电，不包括resume等）
			JniNdk.JNI_Rfid_PiccResetCard((byte) 0);
			// 长威反馈M1卡等待时间修改为100ms，会影响M1卡的成功率，测试过程中遇到一次卡住的情况，其他时候正常
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"压力测试中...\n还剩%d次(已成功%d次),[取消]退出测试...",cnt,succ)==ESC)
				break;
			cnt--;
			
			//rf一次操作后resumeEvent重启监听
			if((ret=SmartResume(type))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sceneRfidPress",g_keeptime, "line %d:第%d次:smartresumeEvent失败（%d）", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			if((ret = rfid_detect(type,UidLen,UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime, "line %d:第%d次:寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				errflag=1;
				continue;
			}
			if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				errflag=1;
				continue;
			}
			
			if((ret = rfidApduRw(type,cpuRevLen,UidBuf))!=NDK_OK)
			{
				JniNdk.JNI_Rfid_PiccResetCard((byte)0);
				if(ret == NDK_ERR)
					gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime,"line %d:第%d次:返回码错误或数据校验失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				else
					gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime,"line %d:第%d次:取随机数或读块数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				errflag=1;
				continue;
			}
			
			// 关闭场
			if((ret = JniNdk.JNI_Rfid_PiccResetCard((byte)0))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime, "line %d:第%d次:关闭场失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}

			succ++;
		}
		JniNdk.JNI_Rfid_PiccResetCard((byte)0);
		//解绑事件，注册和解绑事件要配套使用
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "sceneRfidPress", g_keeptime, "line %d:第%d次:smart事件解绑失败(%d)",Tools.getLineInfo(),bak-cnt, ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "sceneRfidPress",g_time_0, "%s%s压力测试完成,已执行次数为%d,成功为%d次",type,typetem, bak-cnt,succ);
	}

	/**
	 * 射频综合压力，包括jni和mpos方式
	 */
	public void rfidPress()
	{
		int returnValue=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++returnValue == (!GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos) || type==_SMART_t.FELICA || type==_SMART_t.MIFARE_0?'1':'2'))//Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "rfidPress", 2, "%s综合压力连续测试结束", type);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行%s综合压力连续测试,[取消]退出",type)==ESC)
					return;
					
			}
			else
			{
				//Poynt和X5不支持mpos,直接进行jni压力测试
				returnValue=gui.cls_show_msg("射频流程压力\n0.流程压力(Jni)\n%s2.M1卡重启寻卡压力",
						GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"1.流程压力(mpos)\n":" \n");
			}
			switch (returnValue) 
			{
				case '0':
					rfidJniPress();
					break;
				
				case '1':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						rfidMposPress();
					break;
				case '2':
					M1reboot();
					break;

				case ESC:
					return;
			}
		}
	}

	// add by 20150227
	// 射频卡流程压力测试
	public void rfidJniPress() 
	{
		/*private & local definition*/
		int succ = 0,cnt=0,bak =0,ret = -1;
		int[] cpuRevLen = new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
	
		
		/*process body*/
		gui.setOverFlag(true);
		if(GlobalVariable.gSequencePressFlag){
			cnt = getCycleValue();
		}else
		{
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		}
		bak=cnt;
		String typetem=" ";
		if (type==_SMART_t.FELICA) {
			 
			if (felicaChoose==1) {
				typetem="_8";
			}
			
		}
		gui.cls_show_msg("Please place the %s card in the sensing area,click any button after the operation is completed.",type);
		
		// M1卡寻卡方式进行选择
		while(cnt>0)
		{
			//保护措施,下电
			 rfidDeactive(type,0);
			// 长威反馈M1卡等待时间修改为100ms，会影响M1卡的成功率，测试过程中遇到一次卡住的情况，其他时候正常
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"%s card process stress test...\n%d times are left(Success %d times),Cancel button to exit the test",type,cnt,succ)==ESC)
				break;
			cnt--;
			//注册事件
			if((ret=SmartRegistEvent(type))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidPress",g_keeptime, "line %d:第%d次:rf事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				return;
			}
			if((ret = rfid_detect(type,UidLen,UidBuf))!=NDK_OK)
			{
				rfidDeactive(type,0);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime, "line %d: the %d times:%s(%d)", Tools.getLineInfo(),bak-cnt,myactivity.getString(R.string.systest_rf_detect_fail),ret);
				continue;
			}
			
			if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf))!= NDK_OK)
			{
				rfidDeactive(type,0);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime, "line %d:the %d times:%s(%d)", Tools.getLineInfo(),bak-cnt,myactivity.getString(R.string.systest_rf_active_fail),ret);
				continue;
			}
			
			if((ret = rfidApduRw(type,cpuRevLen,UidBuf))!=NDK_OK)
			{
				rfidDeactive(type,0);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				if(ret == NDK_ERR)
					gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:the %d times:%s(%d)", Tools.getLineInfo(),bak-cnt,myactivity.getString(R.string.systest_rf_code_err),ret);
				else
					gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime,"line %d:the %d times:%s(%d)", Tools.getLineInfo(),bak-cnt,myactivity.getString(R.string.systest_rf_random_err),ret);
				
				continue;
			}
			
			// 关闭场
			if((ret = rfidDeactive(type,0))!= NDK_OK)
			{
				rfidDeactive(type,0);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime, "line %d:the %d times:%s(%d)", Tools.getLineInfo(),bak-cnt,myactivity.getString(R.string.systest_rf_close_fail),ret);
				continue;
			}

			//解绑事件
			if((ret = SmartUnRegistEvent(type)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime, "line %d:the %d times:smart事件解绑失败(%d)",Tools.getLineInfo(),bak-cnt, ret);
				return;
			}
			succ++;
		}
		rfidDeactive(type,0);
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
		gui.cls_show_msg1_record(TAG, "rfidPress",g_time_0, "%s%s card process stress test completed,test count:%d,success count:%d",type,typetem, bak-cnt,succ);
	}
	// end by 20150227
	
	// add by 20180522
	public void rfidMposPress()
	{
		/*private & local definition*/
		int succ = 0,cnt=0,bak =0,ret = -1,len=0;
		
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		K21DeviceResponse response;
		byte[] retContent;
		byte[] pack;
		String retCode;
		String uidStr;
		
		/*process body*/
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		if(GlobalVariable.gSequencePressFlag){
			cnt = getCycleValue();
		}else
		{
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2);
		}
		bak = cnt;
		gui.cls_show_msg("请在感应区放置%s卡,完成任意键继续",type);
		
		while(cnt>0)
		{
			k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E202), null);
			
			if(gui.cls_show_msg1(100, TimeUnit.MILLISECONDS,"%s流程压力测试中...\n还剩%d次(已成功%d次),[取消]退出测试...",type,cnt,succ)==ESC)
				break;
			cnt--;
			//寻卡上电
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E201), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime, "line %d:第%d次:寻卡上电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//M1卡外部密钥认证
			if(type == _SMART_t.MIFARE_1){
				//获取序列号长度
				try {
				    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
				} catch (NumberFormatException e) {
				    e.printStackTrace();
				}
				//取寻到的m1卡序列号
				uidStr = ISOUtils.hexString(retContent, 12, len);
				pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x08},ISOUtils.hex2byte("60"+uidStr+"01ffffffffffff"));
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{
					gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime, "line %d:M1卡外部密钥认证失败(%s)", Tools.getLineInfo(),retCode);
					continue;
				}
			}
			LoggerUtil.e("card type:"+type);
			//读写
			if((ret = rfidApduRw_mpos(k21ControllerManager))!=NDK_OK)
			{
				k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E202), null);
//				gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime,"line %d:第%d次:%s读写失败(%d)", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			
			// 下电
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E202), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime, "line %d:第%d次:下电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}

			succ++;
		}
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E202), null);
		k21ControllerManager.close();
		gui.cls_show_msg1_record(TAG, "rfidPress_mpos",g_time_0, "%s流程压力测试完成，已执行次数为%d,成功为%d次",type, bak-cnt,succ);
	}
	// end by 20180522
	
	// add by 20150227
	// 非接触卡，性能测试
	public void rfidAbility()
	{
		/*private & local definition*/
		int count = 0,ret;
		float fTotalTime = 0;
		long oldsearchtime=0;  //寻卡时间
		float searchtime=0;
		long oldTime = 0;
		int[] cpuRevLen = new int[1];
		byte[] UidBuf = new byte[20];
		int[] UidLen = new int[1];
		String typetem=" ";
		
		if (type==_SMART_t.FELICA) {
			 
			if (felicaChoose==1) {
				typetem="_8";
			}
			
		}
		/*process body*/
		gui.cls_show_msg("Please place the %s card in the sensing area,click any button after the operation is completed.",type);
		
		gui.cls_printf(String.format("%scard performance test...",type).getBytes());
		//保护措施,下电
		rfidDeactive(type,0);
		if(GlobalVariable.sdkType==SdkType.SDK3)
		{
			oldsearchtime=System.currentTimeMillis();
			ret = SmartRegistEvent(type);
			
			if (ret == NDK_NO_SUPPORT_LISTENER) // 该卡不支持事件机制,采用SDK2.0的寻卡方式  //M0M1已支持事件机制 
			{
				oldsearchtime=System.currentTimeMillis();
				if((ret = rfid_detect(type, UidLen, UidBuf))!=NDK_OK)
				{
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
					rfidDeactive(type,0);
					gui.cls_show_msg1_record(TAG, "rfidAbility",g_keeptime,"line %d:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_detect_fail),ret);
					return;
				}
				searchtime = System.currentTimeMillis()-oldsearchtime;
			}
			else if(ret!=NDK_OK)
			{
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				gui.cls_show_msg1_record(TAG, "rfidAbility", g_keeptime, "line %d:smart事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			if(ret==NDK_OK)
			{
				synchronized (mRfObj) {
					try {
						mRfObj.wait(5*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(rfFlag==16){
					searchtime = System.currentTimeMillis()-oldsearchtime;
				}
				else
				{
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
					gui.cls_show_msg1_record(TAG, "rfidAbility", g_keeptime, "line %d:事件机制寻卡失败(rfFlag=%d)",Tools.getLineInfo(), rfFlag);
					return;
				}
				Log.d("eric","RF事件机制-----------------------:"+rfFlag);
			}
		}
		else
		{
			oldsearchtime=System.currentTimeMillis();
			if((ret = rfid_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
				rfidDeactive(type,0);
				gui.cls_show_msg1_record(TAG, "rfidAbility",g_keeptime,"line %d:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_detect_fail),ret);
				return;
			}
			searchtime = System.currentTimeMillis()-oldsearchtime;
		}
		if((ret = rfid_detect(type,UidLen,UidBuf))!=NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			gui.cls_show_msg1_record(TAG, "rfidPress", g_keeptime, "line %d: the %d times:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_detect_fail),ret);
			return;
		}
		// 上电
		Log.d("测试。。。。。", type+"");
		if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf))!=NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			gui.cls_show_msg1_record(TAG, "rfidAbility",g_keeptime,"line %d:%s(%d)", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_active_fail),ret);
			return;
		}
		count = 0;
		oldTime = System.currentTimeMillis();
		
		Log.d("eric", "运行-----------");
		while((ret = rfidApduRw(type,cpuRevLen,UidBuf)) == NDK_OK)
		{
			count++;
			// 成功时间累加
			fTotalTime = Tools.getStopTime(oldTime);
			if(fTotalTime>TEST_TIME) //成功时间>10s
				break;
		}
		//下电
		if(rfidDeactive(type,0)!=NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			gui.cls_show_msg1_record(TAG, "rfidAbility",g_keeptime,"line %d：%s", Tools.getLineInfo(),myactivity.getString(R.string.systest_rf_close_fail));
			return;
		}
		Log.v("wxy", "Event:2");
		// 解绑事件
		if((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "rfidAbility", g_keeptime, "line %d:smart事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		//计算性能值
		// 修改A、B卡随机数获取的长度值，改为根据卡片获取 by zhengxq 20161116
//		gui.cls_show_msg("searchtime"+searchtime);
		float value = count * (type == _SMART_t.MIFARE_1 ? LEN_BLKDATA : apduLen) / fTotalTime;
		Log.d("cdcdcderic", "apduLen====="+apduLen+"fTotalTime======"+fTotalTime);
		if (fTotalTime > TEST_TIME)
			gui.cls_show_msg1_record(TAG, "rfidAbility", g_time_0, "%s%s卡每秒读卡速率=%4.2fbyte,寻卡时间=%4.0fms", type,typetem, value,searchtime);
			
		else
			gui.cls_show_msg1_record(TAG, "rfidAbility", g_time_0, "line %d:%s(fTotalTime = %f),寻卡时间=%4.0fms",Tools.getLineInfo(), myactivity.getString(R.string.systest_rf_time_succ),fTotalTime,searchtime);
	
	}
	// end by 20150227
	
	/**
	 * 射频读写压力，包括jni和mpos方式
	 */
	public void rfidRwPress()
	{
		int returnValue=47;
		while(true)
		{
			if(GlobalVariable.gSequencePressFlag)
			{
				if(++returnValue == (!GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)|| type==_SMART_t.FELICA || type==_SMART_t.MIFARE_0?'1':'2'))//Poynt和X5不支持mpos
				{
					gui.cls_show_msg1_record(TAG, "rfidRwPress", 2, "%s读写连续压力测试结束", type);
					return;
				}
				if(gui.cls_show_msg1(3,"即将进行%s读写连续压力测试,[取消]退出",type)==ESC)
					return;
					
			}
			else
			{
				//Poynt和X5不支持mpos,直接进行jni压力测试
				returnValue=gui.cls_show_msg("射频读写压力\n0.读写压力(jni)\n%s",GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)?"1.读写压力(mpos)":"");
			}
			switch (returnValue) 
			{
				case '0':
					rfidJniRwPress();
					break;
				
				case '1':
					if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos))// 双重保障测试人员误操作
						rfidMposRwPress();
					break;

				case ESC:
					return;
			}
		}
	}
	
	// add by 20150227
	// 非接卡读写压力
	public void rfidJniRwPress()
	{
		/*private & local definition*/
		int ret = 0,succ = 0,cnt = DEFAULT_CNT_VLE1,bak = 0;
		int[] cpuRevLen = new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		
		/*process body*/
		if(GlobalVariable.gSequencePressFlag){
			cnt = getCycleValue();
		}else
		{
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE1);
		}
		bak = cnt;
		String typetem=" ";
		if (type==_SMART_t.FELICA) {
			 
			if (felicaChoose==1) {
				typetem="_8";
			}
			
		}
		gui.cls_show_msg("请在感应区放置%s卡，完成任意键继续", type);
		//保护措施,下电
		rfidDeactive(type,0);
		while(cnt>0)
		{
			//注册事件
			if((ret=SmartRegistEvent(type))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfidRwPress",g_keeptime, "line %d:第%d次：smart事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				return;
			}
			if((ret = rfid_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				rfidDeactive(type,0);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
				gui.cls_show_msg1_record(TAG, "rfidRwPress",g_keeptime,"line %d:第%d次：寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			// 上电
			if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf))!=NDK_OK)
			{
				cnt--;
				rfidDeactive(type,0);
				UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
				gui.cls_show_msg1_record(TAG, "rfidRwPress", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			
			while(cnt>0)
			{
				// ms的单位
				if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "%s读写压力测试中...\n还剩%d次(已成功%d次),[取消]退出测试...",type, cnt, succ)==ESC)
					break;
				cnt--;
				if((ret = rfidApduRw(type,cpuRevLen,UidBuf))!=NDK_OK)
				{
					rfidDeactive(type,0);
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					if(ret == NDK_ERR)
						gui.cls_show_msg1_record(TAG, "rfidRwPress", g_keeptime,"line %d:第%d次:返回码错误或数据校验失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					else
						gui.cls_show_msg1_record(TAG, "rfidRwPress", g_keeptime, "line %d:第%d次:取随机数或读块数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						
					break;
				}
				succ++;
			}
		}
		//下电
		if((ret = rfidDeactive(type,0))!=NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
			gui.cls_show_msg1_record(TAG, "rfidRwPress", g_keeptime,"line %d:关闭场失败%d", Tools.getLineInfo(), ret);
			return;
		}
		// 解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "rfidRwPress", g_keeptime, "line %d:smart事件解绑失败(%d)",Tools.getLineInfo(), ret);
		    return;
		}
		gui.cls_show_msg1_record(TAG, "rfidRwPress", g_time_0,"%s%s读写压力测试完成,已执行次数为%d,成功为%d次", type,typetem,bak-cnt,succ);
	}
	// end by 20150227
	
	// add by 20180521
	public void rfidMposRwPress()
	{
		/*private & local definition*/
		int ret = 0,succ = 0,cnt = DEFAULT_CNT_VLE1,bak = 0,len=0;
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		K21DeviceResponse response;
		byte[] retContent = new byte[1024];
		byte[] pack;
		String retCode;
		String uidStr;
		
		/*process body*/
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if(GlobalVariable.gSequencePressFlag){
			cnt = getCycleValue();
		}else
		{
			cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE1);
		}
		bak = cnt;
		gui.cls_show_msg("请在感应区放置%s卡，完成任意键继续", type);
		
		while(cnt>0)
		{
			//寻卡上电
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E201), null);
			Arrays.fill(retContent, (byte) 0);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				cnt--;
				gui.cls_show_msg1_record(TAG, "rfidRwPress_mpos", g_keeptime, "line %d:第%d次:寻卡上电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
				continue;
			}
			//M1卡外部密钥认证
			if(type == _SMART_t.MIFARE_1){
				//获取序列号长度
				try {
				    len = Integer.parseInt(ISOUtils.hexString(retContent, 10, 2));
				} catch (NumberFormatException e) {
				    e.printStackTrace();
				}
				//取寻到的m1卡序列号
				uidStr = ISOUtils.hexString(retContent, 12, len);
				pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x08},ISOUtils.hex2byte("60"+uidStr+"01ffffffffffff"));
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
				retContent = response.getResponse();
				retCode = ISOUtils.dumpString(retContent, 7, 2);
				if(!retCode.equals("00"))
				{
					gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime, "line %d:M1卡外部密钥认证失败(%s)", Tools.getLineInfo(),retCode);
					return;
				}
			}
			
			while(cnt>0)
			{
				// ms的单位
				if(gui.cls_show_msg1(100,TimeUnit.MILLISECONDS, "%s读写压力测试中...\n还剩%d次(已成功%d次),[取消]退出测试...",type, cnt, succ)==ESC)
					break;
				cnt--;
				//读写
				if((ret = rfidApduRw_mpos(k21ControllerManager))!=NDK_OK)
				{
					k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E202), null);
					gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime,"line %d:第%d次:%s读写失败(%d)", Tools.getLineInfo(),bak-cnt,type,ret);
					continue;
				}
				succ++;
			}
			
		}
		
		// 下电
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E202), null);
		Arrays.fill(retContent, (byte) 0);
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(!retCode.equals("00"))
		{
			gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_keeptime, "line %d:第%d次:下电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
			return;
		}
		k21ControllerManager.close();
		gui.cls_show_msg1_record(TAG, "rfidPress_mpos", g_time_0,"%s读写压力测试完成,已执行次数为%d,成功为%d次", type,bak-cnt,succ);
	}
	// end by 20180522
	
	// 射频卡挥卡测试
	public void RFID_wave_press(boolean isRobot) 
	{
		/*private & local definition*/
		int ret = 0,cnt = 200,bak = 0,succ = 0;
		boolean  flag = false;
		byte[] recbuf = new byte[16];
		int[] cpuRevLen = new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		byte sendbuf[] = {0x02,0x01,(byte) 0xDF,0x00,0x03}  , ok_buf[] = {0x02,0x02,(byte) 0xDF,0x01};
//		byte fail_buf[] = {0x02,0x02,(byte) 0xDF,0x00};
		AnalogSerialManager analogSerialManager = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
		/*process body*/
		/*
		 * 机器人测试的时候需要进行串口通讯操作
		 * pos先发指令给机器人，同时进行寻卡，机器人进行挥卡（过程中射频卡寻到卡后进行激活读写等操作），
		 * 机器人到位后回传指令给pos，pos读到数据后再发指令给机器人让其移出，pos读到指令后再继续执行发送指令，
		 * 让机器人移入（同时进行寻卡操作，寻卡超时时间改成10s），如此循环
		 */
		String typetem=" ";
		
		if (type==_SMART_t.FELICA) {
			 
			if (felicaChoose==1) {
				typetem="_8";
			}
			
		}
		bak = cnt = gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_WAVE_NUM);
		
		if(isRobot)
		{
			// 打开USB串口
			if((ret = analogSerialManager.open())==-1)
			{
				gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
			if((ret = analogSerialManager.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:设置USB波特率失败(%d)", Tools.getLineInfo(),ret);
				return;
			}
		}
		else
			gui.cls_show_msg("挥卡测试时请尝试将卡片从不同的方向移入感应区，点任意键开始测试");
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "请在3秒内从较远距离挥入%s卡，还剩%d次(已成功%d次),[取消]退出测试...", type,cnt,succ)==ESC)
				break;
			cnt--;
			if(isRobot)
			{
				// 往USB发送“0201DF0003”
				if((ret = analogSerialManager.write(sendbuf, sendbuf.length, 30))!=sendbuf.length)
				{
					gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:第%d次串口发送数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					analogSerialManager.close();
					return;
				}
			}
			// 需要出错处理
			// 寻卡
			while(true)
			{
				//注册事件
				if((ret=SmartRegistEvent(type))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "RFID_wave_press",g_keeptime, "line %d:第%d次:smart事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					return;
				}
				if((ret = rfid_detect(type, UidLen, UidBuf))!=NDK_OK)
				{
					rfidDeactive(type,0);
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					gui.cls_show_msg1_record(TAG,"RFID_wave_press",g_keeptime, "line %d:第%d次:寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK)
				{
					rfidDeactive(type,0);
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					gui.cls_show_msg1_record(TAG,"RFID_wave_press",g_keeptime, "line %d:第%d次:上电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				for (int j = 0; j < 3; j++) 
				{
					// apdu读写
					if((ret = rfidApduRw(type,cpuRevLen,UidBuf))!=NDK_OK)
					{
						rfidDeactive(type,0);
						UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
						if(ret == NDK_ERR)
							gui.cls_show_msg1_record(TAG,"RFID_wave_press",g_keeptime,"line %d:第%d次:返回码错误或数据校验失败(%d)(j = %d)", Tools.getLineInfo(),bak-cnt,ret,j);
						else
							gui.cls_show_msg1_record(TAG,"RFID_wave_press",g_keeptime, "line %d:第%d次:取随机数或读块数据失败(%d)(j = %d)", Tools.getLineInfo(),bak-cnt,ret,j);
						
						flag  = true;
						break;
					}
				}
				if(flag)
				{
					flag= false;
					break;
				}
				// 下电操作
				if((ret = rfidDeactive(type,0))!=NDK_OK)
				{
					rfidDeactive(type,0);
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					gui.cls_show_msg1_record(TAG,"RFID_wave_press",g_keeptime, "line %d:第%d次:关闭场失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				//解绑事件
				if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:第%d次:smart事件解绑失败(%d)",Tools.getLineInfo(),bak-cnt, ret);
					return;
				}
				
				succ++;
				break;
			}
			
			if(isRobot)
			{ 
				if((ret = analogSerialManager.read(recbuf, 6, 15))!=6)
				{
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:串口读取数据失败(%s)", Tools.getLineInfo(),Dump.getHexDump(recbuf));
					analogSerialManager.close();
					return;
				}
				if(Tools.memcmp(recbuf, ok_buf, 4))
				{;}
				else
				{
					// 接收数据错误
					gui.cls_show_msg1_record(TAG, "RFID_wave_press",g_keeptime, "line %d:接收数据错误，请重新发送",Tools.getLineInfo());
				}
				if((ret = analogSerialManager.write(sendbuf, sendbuf.length, 30))!=sendbuf.length)
				{
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:第%d次串口发送数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					analogSerialManager.close();
					return;
				}
				if((ret = analogSerialManager.read(recbuf, 6, 15))!=6)
				{
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					gui.cls_show_msg1_record(TAG, "RFID_wave_press", g_keeptime, "line %d:串口读取数据失败(%s)", Tools.getLineInfo(),Dump.getHexDump(recbuf));
					analogSerialManager.close();
					return;
				}
				
				if(Tools.memcmp(recbuf, ok_buf, 4))
					continue;
				else
				{
					// 接收数据错误
					gui.cls_show_msg1(2, "line %d:接收数据错误，请重新发送",Tools.getLineInfo());
					continue;
				}
			}
			else
				gui.cls_show_msg1(1, "请在3秒内将%s卡移出感应区",type);
		}
		if(isRobot)
			analogSerialManager.close();
		rfidDeactive(type,0);
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;// 解绑事件
		gui.cls_show_msg1_record(TAG, "RFID_wave_press",g_time_0, "%s%s挥卡压力测试完成,已执行次数为%d,成功为%d次", type,typetem,bak-cnt,succ);
	}
	// end by 20150227
	
	public void rfid_robot_menu()
	{
		while(true)
		{
			int returnValue=gui.cls_show_msg("机器人测试\n0.25个点位测试\n1.挥卡测试\n2.定点综合压力测试\n3.定点读写压力测试");
			switch (returnValue) 
			{
			case '0':
				rfid_Robot_25();
				break;
				
			case '1':
				RFID_wave_press(true);
				break;
				
			case '2':
				rfidJniPress();
				break;
				
			case '3':
				rfidRwPress();
				break;

			case ESC:
				return;
			}
		}

	}
	
	/**
	 * 根据jiangym需求，增加25点位测试
	 */
	public void rfid_Robot_25()
	{
		/*private & local definition*/
		int succ = 0,cnt=0,bak =0,ret = -1;
		byte[] recbuf = new byte[16];
		byte sendbuf[] = {0x02,0x01,(byte) 0xDF,0x00,0x03}  , ok_buf[] = {0x02,0x02,(byte) 0xDF,0x01};
		int[] cpuRevLen = new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		AnalogSerialManager analogSerialManager = (AnalogSerialManager) myactivity.getSystemService(ANALOG_SERIAL_SERVICE);
		
		/*process body*/
		gui.cls_show_msg("请在感应区放置"+type+"卡，任意键继续");
//		gui.cls_show_msg(0, "每个点位的压力测试次数：\n");
		gui.setOverFlag(true);
		final PacketBean packet = new PacketBean();
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE2));
		bak = cnt = packet.getLifecycle();
		//USB串口的收发数据
		if((ret = analogSerialManager.open())==-1)
		{
			gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:打开usb串口失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = analogSerialManager.setconfig(115200, 0, "8N1NB".getBytes()))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:设置USB波特率失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		for (int i = 0; i < 25; i++) // 直接先固定25点，后续需要修改再改
		{
			// 为下一次循环重新初始化
			cnt = bak;
			// 往USB发送“0201E00003”
			if((ret = analogSerialManager.write(sendbuf, sendbuf.length, 30))!=sendbuf.length)
			{
				gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:串口发送数据失败(%d)", Tools.getLineInfo(),ret);
				analogSerialManager.close();
				return;
			}
			// 返回长度??
			if((ret = analogSerialManager.read(recbuf, 6, 30))!=6)
			{
				gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:串口读取数据失败(%s)", Tools.getLineInfo(),Dump.getHexDump(recbuf));
				analogSerialManager.close();
				return;
			}
			if(Tools.memcmp(ok_buf, recbuf, ok_buf.length))
			{
				while(cnt>0)
				{
					if(gui.cls_show_msg1(3, "压力测试中...\n还剩%d次(已成功%d次),[取消]键退出测试...",cnt,succ)==ESC)
						break;
					cnt--;
					//注册事件
					if((ret=SmartRegistEvent(type))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "rfid_Robot",g_keeptime, "line %d:第%d次:smart事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf))!= NDK_OK)
					{
						rfidDeactive(type,0);
						UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
						gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					
					if((ret = rfidApduRw(type,cpuRevLen,UidBuf))!=NDK_OK)
					{
						rfidDeactive(type,0);
						UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
						if(ret == NDK_ERR)
							gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime,"line %d:第%d次:返回码错误或数据校验失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						else
							gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime,"line %d:第%d次:取随机数或读块数据失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						
						continue;
					}
					
					// 关闭场
					if((ret = rfidDeactive(type,0))!= NDK_OK)
					{
						rfidDeactive(type,0);
						UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
						gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:第%d次:关闭场失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
						continue;
					}
					//解绑事件
					if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, "rfid_Robot", g_keeptime, "line %d:第%d次:smart事件解绑失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
						return;
					}
					succ++;
				}
			}
			else
			{
				gui.cls_show_msg1(2, "第%d位置接收出错", i+1);
			}
		}
		analogSerialManager.close();
		rfidDeactive(type,0);
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
		gui.cls_show_msg1_record(TAG, "RFID_press",g_time_0, "%s压力测试完成，已执行次数为%d，成功为%d次", type,25*bak,succ);
	}
	
	/**
	 * 根据中低端的方法增加M1卡跨扇区流程压力 by xuess
	 */
	public void M1_multisection_rw(_SMART_t type)
	{
		/*private & local definition*/
		int ret=-1,s = 1,k=0,succ = 0,i=0;
		byte[] UID = new byte[LEN_UID];
		int[] pUIDLen = new int[1];
		int[] rcvLen = new int[1];
		int[] pnDataLen = new int[1];
		byte[] out = new byte[16];
		byte[] out1 = new byte[16];
		int cnt = DEFAULT_CNT_VLE3,bak = 0;
		byte[][] RF_bak = new byte[64][LEN_BLKDATA+8];
		
		//给各块数据赋初始值
		for(i=0;i<64;i++)
		{
			System.arraycopy(DATA16, 0, RF_bak[i], 0, LEN_BLKDATA);
		}
		
		/*process body*/
		gui.setOverFlag(true);
		if (GlobalVariable.gSequencePressFlag)
			bak = cnt = getCycleValue();
		else {
			bak = cnt =gui.JDK_ReadData(TIMEOUT_INPUT, DEFAULT_CNT_VLE3);
		}
		gui.cls_show_msg1(g_keeptime,"请将M-1卡放置在感应区,按任意键开始测试");
		
		//注册事件
		if((ret=SmartRegistEvent(type))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "rfidPress",g_keeptime, "line %d:第%d次:rf事件注册失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
			return;
		}
		
		while(cnt>0)
		{
			// 寻卡
			if((ret = rfid_detect(type, pUIDLen, UID))!=SUCC)
			{
				cnt--;
				if(ESC == gui.cls_show_msg1(g_keeptime, "line %d:第%d次:寻卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret))
				{
					UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
					break;
				}
				else
					continue;
			}
			while(cnt>0)
			{
				gui.cls_printf(String.format("M1跨扇区读写压力测试中...\n还剩%d次(已成功%d次),请耐心等待",cnt,succ).getBytes());
				cnt--;
				s = (int) ((Math.random()*16)%15);// 随机选取读取的扇区
				// 激活
				if((ret = JniNdk.JNI_Rfid_M1ExternalAuthen(pUIDLen[0], UID, AUTHKEY_TYPE_A, AUTHKEY, (byte) (4*s)))!=NDK_OK)
				{
					rfidDeactive(type,0);
					gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				// 顺序读取该扇区前两个块号
				for(k=4*s;k<4*s+2;k++)
				{
					if((ret = JniNdk.JNI_Rfid_M1Read((byte) k, rcvLen, out))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:M-1块读失败(%d)", Tools.getLineInfo(),ret);
						break;
					}
					// 预期rcvlen应等于LEN_BLKDATA
					if(Tools.memcmp(RF_bak[k], out, rcvLen[0])==false)
					{
						System.arraycopy(out, 0, RF_bak[k], 0, rcvLen[0]);
					}
					else if(Tools.memcmp(RF_bak[k], out, rcvLen[0])==false)
					{
						gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:M1卡读数据校验失败(%d)",Tools.getLineInfo(),rcvLen[0]);
						break;
					}
				}
				// 跨下一个扇区第一块写操作，进行写的数据校验
				// 激活
				if((ret = JniNdk.JNI_Rfid_M1ExternalAuthen(pUIDLen[0], UID, AUTHKEY_TYPE_A, AUTHKEY, (byte) (4*(s+1))))!=NDK_OK)
				{
					rfidDeactive(type, 0);
					gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					break;
				}
				// 读出该块原有数据
				if((ret = JniNdk.JNI_Rfid_M1Read((byte) (4*(s+1)), rcvLen, out))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:M-1块读失败(%d)", Tools.getLineInfo(),ret);
					break;
				}
				pnDataLen[0] = 16;
				// 写入原有数据
				if((ret = JniNdk.JNI_Rfid_M1Write((byte) (4*(s+1)), pnDataLen, out))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:M-1卡数据写失败(%d)", Tools.getLineInfo(),ret);
					break;
				}
				// 读取所写的数据
				if((ret = JniNdk.JNI_Rfid_M1Read((byte) (4*(s+1)), rcvLen, out1))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:M-1块读失败(%d)", Tools.getLineInfo(),ret);
					break;
				}
				// 对比数据
				if(Tools.memcmp(out, out1, 16)==false)// 比较实际写入数据是否与预期写入数据一致
				{
					gui.cls_show_msg1_record(TAG, "M1_multisection_rw", g_keeptime, "line %d:M-1卡写数据校验失败(%d)", Tools.getLineInfo(),ret);
					break;
				}
				succ++;
			}
		}
		rfidDeactive(type,0);// 退出前关闭场
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;// 解绑事件
		gui.cls_show_msg1_record(TAG, "m1_CrossSector_Press",g_time_0, "%s压力测试完成，已执行次数为%d，成功为%d次",type, bak-cnt,succ);
	}
	
	
	public int rfidApduRw_mpos(K21ControllerManager k21ControllerManager,String...uidStr){
		K21DeviceResponse response;
		byte[] retContent;
		byte[] pack;
		String retCode;
		String writeStr;
		int len=0;
	
		switch (type) 
		{
		case CPU_A:
		case CPU_B:
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E203), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			try {
			    len = Integer.parseInt(ISOUtils.hexString(retContent, 9, 2));
			} catch (NumberFormatException e) {
			    e.printStackTrace();
			}
			// 综合部分不需要严格的判断取随机数命令一定要但会9000 或6D 00只要是6X00也是可以接受的 modify by zhengxq
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw_mpos", g_keeptime, "line %d:%s读写失败(%s)",Tools.getLineInfo(),type,retCode);
				return FAIL;
			}
			else // 要先确保APDU返回
			{
				if(ISOUtils.hexString(retContent, 9+len, 2).equals("9000")==false && (retContent[9+len]&0xf0)!=0x60)
				{
					gui.cls_show_msg1_record(TAG, "rfidApduRw_mpos", g_keeptime, "line %d:%s读写失败(%s,sw = %s)",
							Tools.getLineInfo(),type,retCode,ISOUtils.hexString(retContent, 9+len, 2));
					return FAIL;
				}
			}
			return SUCC;
			
		case MIFARE_1:
			//读01块
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E209), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw_mpos", g_keeptime, "line %d:%s块读失败(%s)",Tools.getLineInfo(),type,retCode);
				return FAIL;
			} 
			//读到的写入01块
			writeStr = ISOUtils.hexString(retContent, 9, 16);
			pack = CalDataLrc.mposPack(new byte[]{(byte) 0xe2,0x0a},ISOUtils.hex2byte("01"+writeStr));
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw_mpos", g_keeptime, "line %d:%s块写失败(%s)",Tools.getLineInfo(),type,retCode);
				return FAIL;
			} 
			//再次读并校验
			response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Rfid_E209), null);
			retContent = response.getResponse();
			retCode = ISOUtils.dumpString(retContent, 7, 2);
			if(!retCode.equals("00"))
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw_mpos", g_keeptime, "line %d:%s块读失败(%s)",Tools.getLineInfo(),type,retCode);
				return FAIL;
			} 
			if(ISOUtils.hexString(retContent, 9, 16).equals(writeStr)==false)
			{
				gui.cls_show_msg1_record(TAG, "rfidApduRw_mpos", g_keeptime, "line %d:%s数据校验失败(%s)",Tools.getLineInfo(),type,ISOUtils.hexString(retContent, 9, 16));
				return FAIL;
			}
			return SUCC;
		default:
			return FAIL;
		}
	}
	/**
	 * 异常测试 
	 * add by zhangxinj 2019.3.6
	 */
	public void abnormal()
	{
		//开机立即调用NDK_RfidPiccDetect，由于NDK_RfidPiccDetect调用该接口之前，未进行卡片类型设置（NDK_RifdPiccType），则报寻不到卡，返回-2012（未设置卡）
		//正常现象为，如果没有设置卡片类型，驱动会有默认卡类型  
		type=_SMART_t.CPU_A;
		byte[] psPiccType = new byte[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		int[] cpuRevLen = new int[1];
		if(gui.cls_show_msg("本用例需重启pos机后进入,并且不进行卡配置进入,[确认]继续,[取消]退出")==ESC){
			return;
		}
		if(gui.cls_show_msg("请在射频区域放置A卡,[确认]继续,[取消]退出")==ESC){
			return;
		}

		if((ret=SmartRegistEvent(type))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "abnormal",g_keeptime, "line %d:rf事件注册失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		if((ret = rfid_detect(type,UidLen,UidBuf))!=NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});;
			gui.cls_show_msg1_record(TAG, "abnormal", g_keeptime, "line %d:寻卡失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
//		(psPiccType[0]!=(byte)0xCC)
		if((ret = JniNdk.JNI_Rfid_PiccActivate(psPiccType, UidLen, UidBuf))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "abnormal", g_keeptime, "%s,line %d:射频卡激活失败(%s)", TAG,Tools.getLineInfo(),ret);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			return;
		}
		if((ret = rfidApduRw(type,cpuRevLen,UidBuf))!=NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			if(ret == NDK_ERR)
				gui.cls_show_msg1_record(TAG, "abnormal", g_keeptime,"line %d:第%d次:返回码错误或数据校验失败(%d)", Tools.getLineInfo(),ret);
			else
				gui.cls_show_msg1_record(TAG, "abnormal", g_keeptime,"line %d:第%d次:取随机数或读块数据失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		
		// 关闭场
		if((ret = rfidDeactive(type,0))!= NDK_OK)
		{
			rfidDeactive(type,0);
			UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_RFID});
			gui.cls_show_msg1_record(TAG, "abnormal", g_keeptime, "line %d:关闭场失败(%d)", Tools.getLineInfo(),ret);
			return;
		}

		//解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "abnormal", g_keeptime, "line %d:smart事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "abnormal",g_time_0, "异常测试通过");
	}
	
//	//重启方法
//	public void reboot(Context context)
//	
//	{	
//		PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
//		pm.reboot(null); 
//
//	}
}
