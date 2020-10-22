package com.example.highplattest.systest;

import java.util.Arrays;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;

import android.util.Log;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest54.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150614
 * directory 		: 
 * description 		: 三界面卡测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150614	 		created
 *					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest54 extends DefaultFragment
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest54.class.getSimpleName();
	private final String TESTITEM = "三界面卡测试";
	
	/*----------global variables declaration------------------------*/
	// 设置选择的射频卡，默认设置位为A卡
	private _SMART_t type = _SMART_t.CPU_A;
	Gui gui=null;
	private Config config;
	private int felicaChoose=0;
	public void systest54() 
	{
		gui=new Gui(myactivity,handler);
		config = new Config(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		while (true) 
		{
			int nkeyIn=gui.cls_show_msg("三界面卡测试\n0.三界面卡测试\n1.射频卡配置");
			switch (nkeyIn) 
			{
			case '0':
				try 
				{
					threecardTest(type);
				} catch (Exception e) {
					gui.cls_show_msg1(0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
				
			case '1':
				// 在配置中已经对射频卡进行了初始化操作
				type = config.rfid_config();
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(rfidInit(type)!=NDK_OK)
				{
					gui.cls_show_msg1(2, "line %d:射频初始化失败！请检查配置是否正确", Tools.getLineInfo());
					break;
				}
				else
					gui.cls_show_msg1(2, "%s初始化成功！", type);
				break;

			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	
	// add by 20150615
	// 三界面卡测试
	public void threecardTest(_SMART_t type)
	{
		/*private & local definition*/
		int ret = 0,ret1 = -1,ret2=-1,ret3=-1,bak = 0,cnt = 0,succ = 0;
//		byte[] apdu = {0x00,(byte) 0x84,0x00,0x00,0x08};
		byte[] pszTk1 = new byte[MAXTRACKLEN];
		byte[] pszTk2 = new byte[MAXTRACKLEN];
		byte[] pszTk3 = new byte[MAXTRACKLEN];
		int[] errorCode = new int[1];
		PacketBean packet = new PacketBean();
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		int[] cpuLen = new int[1];
		int[] pnSta = new int[1];
		byte[] swiped = new byte[1];
		
		// icc
		byte[] psAtrBuf = new byte[20];
		int[] pnAtrLen = new int[1];
//		int[] pnRecvLen = new int[1];
//		byte[] psRecvBuf = new byte[8];
		
		/*process body*/
		// 设置交叉次数
		packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		
		testEnd();
		while(cnt>0)
		{
			// 打开磁卡设备
			if((ret = JniNdk.JNI_Mag_Open())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,ret);
				return;
			}
			
			//磁卡注册
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),maglistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "threecardTest", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}

			// IC注册事件
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue(),icclistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "threecardTest", g_keeptime, "line %d:IC事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			//射频注册
			if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue(),rflistener)) != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "threecardTest", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
				return;
			}
			if(gui.cls_show_msg2(0.1f, "%s测试，已执行%d次，成功%d次，请刷卡/插卡/挥卡，【取消】退出测试...", TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			
			ret1 = -1;
			ret2 = -1;
			ret3 = -1;
			pnSta[0] = -1;
			swiped[0] = -1;
			
			if(GlobalVariable.sdkType==SdkType.SDK2)
			{
				while(true)// SDK2.0方式
				{
					// 寻卡函数
					ret3 = JniNdk.JNI_Icc_Detect(pnSta);
					ret2 = JniNdk.JNI_Mag_Swiped(swiped);
					// 激活
					ret1 = rfid_detect(type, UidLen, UidBuf);
					if(ret1==SUCC||(ret2==NDK_OK&&swiped[0]==1)||(ret3==NDK_OK&&pnSta[0]==0x01))
						break;
				}
			}
			else
			{
				while(true)
				{
					if(magFlag>0||rfFlag>0||iccFlag>0)
						break;
				}
				LoggerUtil.i("magFlag="+magFlag+"|||rfFlag="+rfFlag+"|||iccFlag="+iccFlag);
				if(magFlag>0)
					gui.cls_show_msg1(1, "监听到磁卡事件");
				if(iccFlag>0)
					gui.cls_show_msg1(1, "监听到IC事件");
				if(rfFlag>0)
					gui.cls_show_msg1(1, "监听到射频事件");
			}

			
			if((ret1 == SUCC)||rfFlag>0)
			{
				Log.v("wangxy", "监听到1");
				// 激活
				if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:第%d次:激活失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				// 读写
				if((ret = rfidApduRw(type,cpuLen,UidBuf))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime,"line %d:第%d次:读写失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				Log.v("wangxy", "监听到2");
				// 下电
				if((ret = rfidDeactive(type,0))!= NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:第%d次:下电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				Log.v("wangxy", "监听到3");
				gui.cls_show_msg1(5, "%s卡读写成功，请移走卡片", type);
				Log.v("wangxy", "监听到3.1");
				succ++;
			}
			
			if((ret2 == NDK_OK&&swiped[0]==1)||magFlag>0)
			{
				//磁卡寻卡成功
				Arrays.fill(pszTk1, (byte) 0);
				Arrays.fill(pszTk2, (byte) 0);
				Arrays.fill(pszTk3, (byte) 0);
				if((ret = JniNdk.JNI_Mag_ReadNormal(pszTk1, pszTk2, pszTk3, errorCode))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:第%d次:读卡失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				// 显示磁道数据
				String str_TK1 = new String(pszTk1);
//				int index = str_TK1.indexOf("\0");
				
//				if(index == -1)
//					gui.cls_show_msg1(2, "1道无数据!", TAG,Tools.getLineInfo());
//				else
//				{
//					String end_TK1 = str_TK1.substring(0, index);
					gui.cls_show_msg1(2, "1道数据:%s", str_TK1==null?"null":str_TK1);
//				}
				
				// 显示磁道数据
				String str_TK2 = new String(pszTk2);
//				index = str_TK1.indexOf("\0");
				
//				if(index == -1)
//					gui.cls_show_msg1(2, "2道无数据!", TAG,Tools.getLineInfo());
//				else
//				{
//					String end_TK2 = str_TK2.substring(0, index);
					gui.cls_show_msg1(2, "2道数据:%s", str_TK2==null?"null":str_TK2);
//				}
				
				// 显示磁道数据
				String str_TK3 = new String(pszTk3);
//				index = str_TK1.indexOf("\0");
//				
//				if(index == -1)
//					gui.cls_show_msg1(2, "3道无数据!", TAG,Tools.getLineInfo());
//				else
//				{
//					String end_TK3 = str_TK3.substring(0, index);
					gui.cls_show_msg1(2, "3道数据:%s",str_TK3==null?"null":str_TK3);
//				}
				succ++;
			}
			
			if((ret3 == NDK_OK&&pnSta[0]==1)||iccFlag>0)
			{
				// IC卡
				if((ret = iccPowerOn(EM_ICTYPE.ICTYPE_IC,psAtrBuf,pnAtrLen)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:第%d次:IC上电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				// 取随机数
				if((ret = iccRw(EM_ICTYPE.ICTYPE_IC,req,null)) != NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:第%d次:IC卡APDU失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				// 下电
				if((ret = JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal()))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "threecardTest",g_keeptime, "line %d:第%d次:IC卡下电失败(%d)", Tools.getLineInfo(),bak-cnt,ret);
					testEnd();
					continue;
				}
				gui.cls_show_msg1(5, "IC卡读写成功，请拔出IC卡");
				succ++;
			}
			testEnd();
		}

		gui.cls_show_msg1_record(TAG, "threecardTest",g_time_0,"%s测试完成，总共测试%d次成功%d次\n", TESTITEM,bak-cnt,succ);
	}
	// end by 20150615
	
	private void testEnd()
	{
		rfFlag=-1;
		magFlag=-1;
		iccFlag=-1;
		//磁卡解绑事件
		UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
		//IC解绑事件
		UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_ICCARD.getValue());
		//rfid解绑事件
		UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_RFID.getValue());
		// 保护动作
		rfidDeactive(type,0);
		JniNdk.JNI_Icc_PowerDown(EM_ICTYPE.ICTYPE_IC.ordinal());
		JniNdk.JNI_Mag_Reset();
		JniNdk.JNI_Mag_Close();
	}
}
