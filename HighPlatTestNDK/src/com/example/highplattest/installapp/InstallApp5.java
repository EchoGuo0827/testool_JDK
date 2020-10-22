package com.example.highplattest.installapp;

import java.util.ArrayList;
import java.util.List;

import android.newland.os.NlRecovery;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: 农行恢复出厂下载设备密钥
 * file name 		: InstallApp5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180117
 * directory 		: 经过马鑫汶修改接口修改为只能删除K21端密钥，无法删除Android端密钥
 * description 		: uninstallSecData():卸载设备的所有密钥(农行专用)
 * 					  uninstallSecData(boolean secModule,boolean androidModule):卸载设备特定端的密钥数据
 * related document : 
 * history 		 	: author			date			remarks
 * 					 zhangxinj		   20180117	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class InstallApp5 extends UnitFragment{
	private Gui gui = new Gui(myactivity, handler);
	public final String TAG = InstallApp5.class.getSimpleName();
	private final String TESTITEM = "uninstallSecData";
	/**密钥相关的密钥值*/
	private final String MAINKEY = "11111111111111111111111111111111";// 主密钥
	private final String WORKINGKEY_DATA_MAC = "DBFE96D0A5F09D24";// mac密钥，明文4DE5E8B8A9DCDDF9
	private final String WORKINGKEY_DATA_PIN = "D2CEEE5C1D3AFBAF00374E0CC1526C86";// pin密钥，明文2A288F61348FEE93FE9C0FC714BCDD73
	
	public void installapp5()
	{
		if(GlobalVariable.gCustomerID!=CUSTOMER_ID.ABC)
		{
			gui.cls_show_msg("非农行固件不支持本案例,任意键退出测试");
			unitEnd();
			return;
		}
		boolean iRet = false;
		int secRet = -1;
		NlRecovery nlRecovery = new NlRecovery(myactivity);
		SecKcvInfo kcvInfo = new SecKcvInfo();
		List<Byte> indexList = new ArrayList<Byte>();
		byte[] sDstKeyValue = {(byte) 0xDB,(byte) 0xFE,(byte) 0x96,(byte) 0xD0,(byte) 0xA5,(byte) 0xF0,(byte) 0x9D,0x24,(byte) 0xDB,(byte) 0xFE,(byte) 0x96,(byte) 0xD0,(byte) 0xA5,(byte) 0xF0,(byte) 0x9D,0x24};
		// case1:未安装任何密钥的情况下，卸载设备的所有密钥（包括android端和K21端），校验任一索引的密钥都应失败
		// case1.1:卸载设备的所有密钥
		if(gui.cls_show_msg("case1测试，调用成功后会立即重启，是否【不跳过】本case测试，是【确认】，否【取消】")==ENTER)
		{
			if((iRet = nlRecovery.uninstallSecData())!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		// case1.2:校验所有索引的密钥
		if(gui.cls_show_msg("case1重启后需【校验密钥】，是否进行case1密钥校验，是【确认】，否【取消】")==ENTER)
		{
			gui.cls_printf("正在进行case1密钥校验，请耐心等待...".getBytes());
			if(checkKey("case1.2")==0)
			{
				if(GlobalVariable.isContinue==false)
					return;
			}
		}


		// case2:只安装K21端索引的密钥，进行卸载所有密钥操作，校验任一索引的密钥都失败
		// case2.1:安装K21密钥
		if(gui.cls_show_msg("case2测试，调用成功会立即重启，是否【不跳过】本case测试，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引1的主密钥、索引10的pin密钥、索引100的mac密钥、索引255的data密钥");
			// TMK
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			// TPK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)10, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			// TAK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			// TDK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)255, 16, sDstKeyValue, kcvInfo);
			// case2.2:删除设备的所有密钥
			if((iRet = nlRecovery.uninstallSecData())!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case2.3:校验全部所以都不存在密钥
		if(gui.cls_show_msg("case2重启后需【校验密钥】，是否进行case2密钥校验，是【确认】，否【取消】")==ENTER)
		{
			gui.cls_printf("正在进行case2密钥校验，请耐心等待...".getBytes());
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if(checkKey("case2.3")==0)
			{
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		/*// case3:只安装Android端索引的密钥，进行卸载密钥操作，校验任一索引的密钥都失败(不支持删除Android端密钥，屏蔽该case modify 20180118)
		// case3.1:安装Android索引的密钥
		JniNdk.JNI_Sec_SetKeyOwner("my app space");
		gui.cls_show_msg1(1, "即将在Android端安装索引1的主密钥、索引10的pin密钥、索引100的mac密钥、索引255的data密钥");
		// TMK
		kcvInfo.nCheckMode = 0;
		JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
		// TPK
		JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)10, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
		// TAK
		JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
		// TDK
		JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)255, 16, sDstKeyValue, kcvInfo);
		// case3.2:删除设备的所有密钥
		if((iRet = nlRecovery.uninstallSecData())!=true)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case3.3:校验Android端的所有索引密钥
		if(checkKey("case3.3")==0)
		{
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		
		// case4:安装Android端和K21端索引的密钥，进行卸载密钥操作，校验任一所以的密钥都失败（删除Android端密钥功能不支持 modify 20180118）
		// case4.1:安装Android端和K21端索引的密钥
		if(gui.cls_show_msg("case4测试，调用成功会立即重启，是否【不跳过】本case测试，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引1的主密钥、索引10的pin密钥、索引100的mac密钥、索引255的data密钥");
			// TMK
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			// TPK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)10, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			// TAK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			// TDK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)255, 16, sDstKeyValue, kcvInfo);
			
			JniNdk.JNI_Sec_SetKeyOwner("my app space");
			gui.cls_show_msg1(1, "即将在Android端安装索引10的主密钥、索引50的pin密钥、索引150的mac密钥、索引200的data密钥");
			// TMK
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)10, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			// TPK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)10, (byte)50, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			// TAK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)10, (byte)150, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			// TDK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)10, (byte)200, 16, sDstKeyValue, kcvInfo);
			// case4.2:删除设备的所有密钥（删除K21端密钥）
			if((iRet = nlRecovery.uninstallSecData())!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case4.3:校验Android端的所有索引密钥
		if(gui.cls_show_msg("case4重启后需【密钥校验】，是否进行case4密钥校验，是【确认】，否【取消】")==ENTER)
		{
			gui.cls_printf("正在进行case4的K21端密钥校验，请耐心等待...".getBytes());
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if(checkKey("case4.3+*")==0)
			{
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_printf("正在进行case4的Android端密钥校验，请耐心等待...".getBytes());
			JniNdk.JNI_Sec_SetKeyOwner("my app space");
			kcvInfo.nCheckMode = 1;
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)10, kcvInfo))!=NDK_OK)// 主密钥
			{		
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)50, kcvInfo))!=NDK_OK)// 主密钥
			{		
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case5:安装密钥后进行两次删除密钥操作，校验任一索引的密钥都失败(只支持删除K21端密钥功能 modify 20180118)
		// case5.1:安装Android端和K21端的密钥
		if(gui.cls_show_msg("case5测试，调用成功后立即重启，是否【不跳过】本case测试，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引1的主密钥、索引10的pin密钥、索引100的mac密钥、索引255的data密钥");
			// TMK
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			// TPK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)10, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			// TAK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			// TDK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)255, 16, sDstKeyValue, kcvInfo);
			
			JniNdk.JNI_Sec_SetKeyOwner("my app space");
			gui.cls_show_msg1(1, "即将在Android端安装索引10的主密钥、索引50的pin密钥、索引150的mac密钥、索引200的data密钥");
			// TMK
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)10, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			// TPK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)10, (byte)50, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			// TAK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)10, (byte)150, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			// TDK
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)10, (byte)200, 16, sDstKeyValue, kcvInfo);
			// case5.2:删除设备的所有密钥
			if((iRet = nlRecovery.uninstallSecData())!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if((iRet = nlRecovery.uninstallSecData())!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		// case5.3:分别检验K21端与Android的密钥是否存在
		if(gui.cls_show_msg("case5重启后需【校验密钥】，是否进行case5密钥检验，是【确认】，否【取消】")==ENTER)
		{
			gui.cls_printf("正在进行case5的K21密钥校验，请耐心等待...".getBytes());
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if(checkKey("case5.3+*")==0)
			{
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_printf("正在进行case5的Android密钥校验，请耐心等待...".getBytes());
			JniNdk.JNI_Sec_SetKeyOwner("my app space");
			kcvInfo.nCheckMode = 1;
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)10, kcvInfo))!=NDK_OK)// 主密钥
			{		
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)50, kcvInfo))!=NDK_OK)// 主密钥
			{		
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		gui.cls_show_msg1(1, "子用例 1测试通过");
		
		// public boolean uninstallSecData(boolean secModule,boolean androidModule) 卸载设备的密钥数据
		// case6.1:在K21端安装了密钥，卸载了Android端的密钥，K21端的密钥仍应存在（不支持卸载Android端密钥）
		if(gui.cls_show_msg("case6.1测试，调用成功后会立即重启，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引1的主密钥、索引10的pin密钥、索引100的mac密钥、索引255的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)1, (byte)10, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)1, (byte)255, 16, sDstKeyValue, kcvInfo);
			// 卸载Android密钥
			if((iRet = nlRecovery.uninstallSecData(false, true))!=false)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			JniNdk.JNI_Sec_SetKeyOwner("*");
			// 校验K21的主密钥和工作密钥是否存在
			indexList.removeAll(indexList);
			indexList.add((byte) 1);
			indexList.add((byte) 10);
			indexList.add((byte) 100);
			indexList.add((byte) 255);
			for (int i = 1; i <= indexList.size(); i++) 
			{
				kcvInfo.nCheckMode = 1;
				if((secRet = JniNdk.JNI_Sec_GetKcv((byte)i, (byte)indexList.get(i-1), kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
		}

		/*// case6:卸载两次并不改变原先密钥存在的状态（设置完毕会重启不支持删除两次密钥操作）
		// 卸载Android密钥
		if((iRet = nlRecovery.uninstallSecData(false, true))!=true)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		kcvInfo.nCheckMode = 1;
		if((secRet = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)1, kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		// case6.2:在K21端安装了密钥，卸载了K21端的密钥，K21端的密钥应不存在
		if(gui.cls_show_msg("case6.2测试，调用成功后会立即重启，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引100的主密钥、索引101的pin密钥、索引102的mac密钥、索引103的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)100, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)100, (byte)101, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)100, (byte)102, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)100, (byte)103, 16, sDstKeyValue, kcvInfo);
			// 卸载K21端密钥
			if((iRet = nlRecovery.uninstallSecData(true, false))!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
		}

		if(gui.cls_show_msg("case6.2重启后需【密钥校验】，是否进行密钥检验，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("*");
			indexList.removeAll(indexList);
			indexList.add((byte) 100);
			indexList.add((byte) 101);
			indexList.add((byte) 102);
			indexList.add((byte) 103);
			for (int i = 1; i <= indexList.size(); i++) 
			{
				kcvInfo.nCheckMode = 1;
				if((secRet = JniNdk.JNI_Sec_GetKcv((byte)i, (byte)indexList.get(i-1), kcvInfo))==NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
		}

		/*// case6:卸载两次并不改变原先密钥存在的状态
		if((iRet = nlRecovery.uninstallSecData(true, false))!=true)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		kcvInfo.nCheckMode = 1;
		if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)101, kcvInfo))==NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		// case7.1:在Android端安装了密钥，卸载了K21端的密钥，Android端的密钥仍应存在
		// 给Android端安装密钥
		if(gui.cls_show_msg("case7.1测试，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			gui.cls_show_msg1(1, "即将在Android端安装索引200的主密钥、索引201的pin密钥、索引202的mac密钥、索引203的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)200, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)200, (byte)201, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)200, (byte)202, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)200, (byte)203, 16, sDstKeyValue, kcvInfo);
			// 卸载K21端密钥
			if((iRet = nlRecovery.uninstallSecData(true, false))!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
		}

		if(gui.cls_show_msg("case7.1重启后需【密钥校验】，是否进行密钥校验，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			indexList.removeAll(indexList);
			indexList.add((byte) 200);
			indexList.add((byte) 201);
			indexList.add((byte) 202);
			indexList.add((byte) 203);
			for (int i = 1; i <= indexList.size(); i++) 
			{
				kcvInfo.nCheckMode = 1;
				if((secRet = JniNdk.JNI_Sec_GetKcv((byte)i, (byte)indexList.get(i-1), kcvInfo))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s密钥类型%d，密钥索引%d，测试失败(%d)", Tools.getLineInfo(),TESTITEM,i,indexList.get(i-1),secRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
		}

		/*// case6:卸载两次并不改变原先密钥存在的状态
		if((iRet = nlRecovery.uninstallSecData(true, false))!=true)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		kcvInfo.nCheckMode = 1;
		if((secRet = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)200, kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		// case7.2:在Android端安装了密钥，卸载了Android端的密钥，Android端的密钥应不存在（不支持卸载Android端密钥）
		if(gui.cls_show_msg("case7.2测试，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			gui.cls_show_msg1(1, "即将在Android端安装索引50的主密钥、索引51的pin密钥、索引52的mac密钥、索引53的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)50, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)50, (byte)51, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)50, (byte)52, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)50, (byte)53, 16, sDstKeyValue, kcvInfo);
			// 卸载Android端密钥
			if((iRet = nlRecovery.uninstallSecData(false, true))!=false)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			JniNdk.JNI_Sec_SetKeyOwner("test");
			indexList.removeAll(indexList);
			indexList.add((byte) 50);
			indexList.add((byte) 51);
			indexList.add((byte) 52);
			indexList.add((byte) 53);
			for (int i = 1; i <= indexList.size(); i++) 
			{
				kcvInfo.nCheckMode = 1;
//				if((secRet = JniNdk.JNI_Sec_GetKcv((byte)i, (byte)indexList.get(i-1), kcvInfo))==NDK_OK)//(支持删除Android端密钥)
				if((secRet = JniNdk.JNI_Sec_GetKcv((byte)i, (byte)indexList.get(i-1), kcvInfo))!=NDK_OK)//(不支持删除Android端密钥)
				{
					gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
		}



		/*// case6:卸载两次并不改变原先密钥存在的状态
		if((iRet = nlRecovery.uninstallSecData(false, true))!=true)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		kcvInfo.nCheckMode = 1;
		if((secRet = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)50, kcvInfo))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		// case8:在Android端与K21端分别安装了密钥，这时setKeyOwner在android端，卸载K21端密钥，预期：K21端密钥应卸载成功，Android密钥不应被卸载
		if(gui.cls_show_msg("case8测试，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			gui.cls_show_msg1(1, "即将在Android端安装索引60的主密钥、索引60的pin密钥、索引60的mac密钥、索引60的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)60, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)60, (byte)60, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)60, (byte)60, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)60, (byte)60, 16, sDstKeyValue, kcvInfo);
			
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引70的主密钥、索引70的pin密钥、索引70的mac密钥、索引70的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)70, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)70, (byte)70, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)70, (byte)70, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)70, (byte)70, 16, sDstKeyValue, kcvInfo);
			JniNdk.JNI_Sec_SetKeyOwner("test");
			// 卸载K21端密钥
			if((iRet = nlRecovery.uninstallSecData(true, false))!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
		}

		// 校验Android密钥情况
		if(gui.cls_show_msg("case8重启后需【密钥校验】，是否进行密钥校验，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			kcvInfo.nCheckMode = 1;
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)60, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 校验K21端密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)70, kcvInfo))==NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case9:在Android端和K21端分别安装了密钥，这时SetKeyOwner在K21端，卸载Android密钥，预期：Android密钥应卸载成功，K21端密钥不应被卸载（不支持卸载Anroid端密钥）
		if(gui.cls_show_msg("case9测试，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			gui.cls_show_msg1(1, "即将在Android端安装索引80的主密钥、索引80的pin密钥、索引80的mac密钥、索引80的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)80, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)80, (byte)80, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)80, (byte)80, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)80, (byte)80, 16, sDstKeyValue, kcvInfo);
			
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引90的主密钥、索引90的pin密钥、索引90的mac密钥、索引90的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte) 90, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)90, (byte)90, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)90, (byte)90, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)90, (byte)90, 16, sDstKeyValue, kcvInfo);
			// 卸载Android端密钥
			if((iRet = nlRecovery.uninstallSecData(false, true))!=false)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			// 校验Android密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("test");
			kcvInfo.nCheckMode = 1;
//			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)80, kcvInfo))==NDK_OK)//(支持删除Android密钥)
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)80, kcvInfo))!=NDK_OK)//(不支持删除Android密钥)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 校验K21端密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)90, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case10:在Android端和K21端分别安装了密钥，不卸载Android端和K21端的密钥，预期：Android端和K21端的密钥均应存在
		if(gui.cls_show_msg("case10测试，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			gui.cls_show_msg1(1, "即将在Android端安装索引100的主密钥、索引100的pin密钥、索引100的mac密钥、索引100的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)100, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)100, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)100, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)100, (byte)100, 16, sDstKeyValue, kcvInfo);
			
			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引110的主密钥、索引110的pin密钥、索引110的mac密钥、索引110的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)110, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)110, (byte)110, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)110, (byte)110, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)110, (byte)110, 16, sDstKeyValue, kcvInfo);
			// 未卸载Android端和K21端密钥
			if((iRet = nlRecovery.uninstallSecData(false, false))!=true)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			// 校验Android密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("test");
			kcvInfo.nCheckMode = 1;
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)100, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 校验K21端密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)110, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}


		// case11:在Android端和K21端分别安装了密钥，分别卸载K21端和Android的密钥，预期：Android端密钥卸载成功，K21端密钥卸载成功（不支持卸载Android端的密钥 modify 20180118）
		// 卸载Android端和K21端密钥
		if(gui.cls_show_msg("case11测试，是否【不跳过】本case，是【确认】，否【取消】")==ENTER)
		{
			JniNdk.JNI_Sec_SetKeyOwner("test");
			gui.cls_show_msg1(1, "即将在Android端安装索引100的主密钥、索引100的pin密钥、索引100的mac密钥、索引100的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)100, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)100, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)100, (byte)100, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)100, (byte)100, 16, sDstKeyValue, kcvInfo);

			JniNdk.JNI_Sec_SetKeyOwner("*");
			gui.cls_show_msg1(1, "即将在K21端安装索引110的主密钥、索引110的pin密钥、索引110的mac密钥、索引110的data密钥");
			kcvInfo.nCheckMode = 0;
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)110, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)2, (byte)110, (byte)110, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)110, (byte)110, 16, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
			JniNdk.JNI_Sec_LoadKey((byte)1, (byte)4, (byte)110, (byte)110, 16, sDstKeyValue, kcvInfo);
			if((iRet = nlRecovery.uninstallSecData(true, true))!=false)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
				return;
			}
			// 校验Android密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("test");
			kcvInfo.nCheckMode = 1;
//			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)100, kcvInfo))==NDK_OK)//(支持删除Android端密钥)
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)100, kcvInfo))!=NDK_OK)//(不支持删除Android端密钥)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 校验K21端密钥情况
			JniNdk.JNI_Sec_SetKeyOwner("*");
			if((secRet = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)110, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}

		/*// case6:卸载两次并不改变原先密钥存在的状态
		if((iRet = nlRecovery.uninstallSecData(true, true))!=true)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,iRet);
			return;
		}
		// 校验Android端密钥情况
		JniNdk.JNI_Sec_SetKeyOwner("test");
		if((secRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)100, kcvInfo))==NDK_OK)//(支持删除Android端密钥)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 校验K21端密钥情况
		JniNdk.JNI_Sec_SetKeyOwner("*");
		if((secRet = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)110, kcvInfo))==NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,secRet);
			if(GlobalVariable.isContinue==false)
				return;
		}*/
		gui.cls_show_msg1_record(TAG, "installapp5", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	public int checkKey(String caseNum)
	{
		int iRet = -1;
		SecKcvInfo kcvInfo = new SecKcvInfo();
		kcvInfo.nCheckMode = 1;
		for (int keyIndex = 0; keyIndex < 256; keyIndex++) 
		{
			if((iRet=JniNdk.JNI_Sec_GetKcv((byte)1, (byte)keyIndex, kcvInfo))==NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s错误，索引%d的主密钥存在(%d)", Tools.getLineInfo(),caseNum,keyIndex,iRet);
				if(GlobalVariable.isContinue==false)
					return iRet;
				else
					continue;
			}
			// pin密钥
			if((iRet = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)keyIndex, kcvInfo))==NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s错误，索引%d的Pin密钥存在(%d)", Tools.getLineInfo(),caseNum,keyIndex,iRet);
				if(GlobalVariable.isContinue == false)
					return iRet;
				else
					continue;
			}
			// mac密钥
			if((iRet = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)keyIndex, kcvInfo))==NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s错误，索引%d的Mac密钥存在(%d)", Tools.getLineInfo(),caseNum,keyIndex,iRet);
				if(GlobalVariable.isContinue==false)
					return iRet;
				else
					continue;
			}
			// data密钥
			if((iRet = JniNdk.JNI_Sec_GetKcv((byte)4, (byte)keyIndex, kcvInfo))==NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "installapp5", gKeepTimeErr, "line %d:%s错误，索引%d的Data密钥存在(%d)", Tools.getLineInfo(),caseNum,keyIndex,iRet);
				if(GlobalVariable.isContinue==false)
					return iRet;
				else 
					continue;
			}
		}
		return iRet;
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
