package com.example.highplattest.mpos;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: 安全模块
 * file name 		: sec33
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190129
 * directory 		: 
 * description 		: 共享密钥表检测(mpos)
 * 					: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180129		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos20 extends UnitFragment
{
	Gui gui = new Gui(myactivity, handler);
	public final String FILE_NAME = Mpos20.class.getSimpleName();
	private final String TESTITEM = "共享密钥表检测(mpos)";
	private K21ControllerManager k21ControllerManager;
	
	// case1:获取存量设备进行验证若共享密钥表有密钥值，应该存在K21端，升级固件后获取表还应该在K21端
	public void get_old_ver_table()
	{
		int nKeyIn =0,ret=-1;
		byte[] owner=new byte[32];
		byte[] secflag = new byte[2];
		
		while(true)
		{
			nKeyIn = gui.cls_show_msg("选择合适的动作\n1.使用JDK-SysTest55设置*表用[mpos方式]安装多组密钥\n2.升级固件\n3.获取共享密钥表\n");
			switch (nKeyIn) {
			case '1':
				gui.cls_show_msg("安装密钥后任意键继续");
				break;
				
			case '2':
				gui.cls_show_msg("升级固件版本后任意键继续");
				break;
				
			case '3':
				// 获取到的共享表应该在K21端
				if((ret = JniNdk.JNI_Sec_GetMposKeyOwner(owner, secflag))!=NDK_OK)
				{
					gui.cls_show_msg1_record(FILE_NAME,"get_old_ver_table",gKeepTimeErr,"line %d:%s测试失败(获取密钥表失败,ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
					return;
				}
				if(ISOUtils.byteToStr(owner).equals("*")==false||secflag[0]!=0)
				{
					gui.cls_show_msg1_record(FILE_NAME,"get_old_ver_table",gKeepTimeErr,"line %d:%s测试失败(owner:%s,secflag:%d)",Tools.getLineInfo(),TESTITEM,ISOUtils.byteToStr(owner),secflag[0]);
					return;
				}
				gui.cls_show_msg1_record(FILE_NAME, "get_old_ver_table", gScreenTime, "存量固件%s测试通过", TESTITEM);
				break;
				
			case ESC:
				return;

			default:
				break;
			}
		}
	}
	
	// case2:新设备直接获取共享密钥表应该在安卓端，或者设置*安装tlk后获取也应该在安卓端
	public void get_new_ver_table()
	{
		int ret =-1;
		byte[] secflag = new byte[2];
		byte[] owner = new byte[32];
		byte[] retContent;
		String retCode;
		
		gui.cls_show_msg1(2, "测试%s", TESTITEM);
		// 测试前置:设置*表，擦除所有密钥
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,7,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "sec30", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((ret = JniNdk.JNI_Sec_GetMposKeyOwner(owner, secflag))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(获取密钥表失败ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.byteToStr(owner).equals("Android_Share_Key_Table")==false||secflag[0]!=1)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(owner:%s,secflag:%d)",Tools.getLineInfo(),TESTITEM,ISOUtils.byteToStr(owner),secflag[0]);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// case2:安装TLK后无其他密钥获取到共享密钥表应该是在安卓端,mpos的擦除密钥会自动安装TLK
//		// 明文安装TLK,ID=1
//		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null).getResponse();
//		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
//		{
//			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
//			if(GlobalVariable.isContinue == false)
//				return;
//		}
		if((ret = JniNdk.JNI_Sec_GetMposKeyOwner(owner, secflag))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(获取密钥表失败ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.byteToStr(owner).equals("Android_Share_Key_Table")==false||secflag[0]!=1)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(owner:%s,secflag:%d)",Tools.getLineInfo(),TESTITEM,ISOUtils.byteToStr(owner),secflag[0]);
			if(GlobalVariable.isContinue == false)
				return;
		}
		
		// case3:设置NULL，进行密钥安装，密钥是安装在安卓端，获取共享密钥表预期在安卓端
		if((ret = JniNdk.JNI_Sec_SetKeyOwner(""))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 安装TMK，ID=2(16字节,明文为16字节11)
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TMK), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 密文安装TPK，ID=3(TMK发散：16字节0x19与16字节0x11加密的结果)
		byte[] Sec_1A05_TPK=ISOUtils.hex2byte("0200361A052FDD02020300164BCE5825E910F99E4BCE5825E910F99E00089DA493AA4B21B8D10003C6");
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(Sec_1A05_TPK), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 密文安装TAK(16字节)，ID=4(TMK发散，8字节0x21+8字节0x23与16字节0x11加密的结果)
		byte[] Sec_1A05_TAK=ISOUtils.hex2byte("0200361A052FDD03020400160A3F3FFCADDA992A8D6ADBB0AFED87DA000864678F1B22ED761200035E");
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(Sec_1A05_TAK), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((ret = JniNdk.JNI_Sec_GetMposKeyOwner(owner, secflag))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(获取密钥表失败ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.byteToStr(owner).equals("Android_Share_Key_Table")==false||secflag[0]!=1)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(owner:%s,secflag:%d)",Tools.getLineInfo(),TESTITEM,ISOUtils.byteToStr(owner),secflag[0]);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// case4:设置*表(共享密钥表),安装TLK，再安装TMK，TDK后获取共享密钥表应该在安卓端
		if((ret = JniNdk.JNI_Sec_SetKeyOwner("*"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 安装TMK,ID=2
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TMK), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 密文安装TPK,ID=3
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(Sec_1A05_TPK), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 密文安装TAK,ID=4
		retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(Sec_1A05_TAK), null).getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gKeepTimeErr,"line %d:%s测试失败(ret = %s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((ret = JniNdk.JNI_Sec_GetMposKeyOwner(owner, secflag))!=NDK_OK)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(获取密钥表失败ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if(ISOUtils.byteToStr(owner).equals("Android_Share_Key_Table")==false||secflag[0]!=1)
		{
			gui.cls_show_msg1_record(FILE_NAME,"get_new_ver_table",gKeepTimeErr,"line %d:%s测试失败(owner:%s,secflag:%d)",Tools.getLineInfo(),TESTITEM,ISOUtils.byteToStr(owner),secflag[0]);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg1_record(FILE_NAME, "get_new_ver_table", gScreenTime, "新版本固件%s测试通过", TESTITEM);
	}
	
	public void mpos20()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",FILE_NAME,GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(FILE_NAME, "sec33", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			int nKeyIn = gui.cls_show_msg("1.存量固件升级后获取共享密钥表\n2.新版本固件获取共享密钥表");
			switch (nKeyIn) {
			case '1':
				get_old_ver_table();
				break;

			case '2':
				get_new_ver_table();
				break;
				
			case ESC:
				return;
				
			default:
				break;
			}
		}
	}

	@Override
	public void onTestUp() {
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try {
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
			gui.cls_show_msg("mpos方式连接K21端失败");
		}
	}

	@Override
	public void onTestDown() {
		// 测试后置
		JniNdk.JNI_Sec_KeyErase();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SecAndroidEnable)==true)
			JniNdk.JNI_Sec_SetKeyOwner("");
		else
			JniNdk.JNI_Sec_SetKeyOwner("*");
	}

}
