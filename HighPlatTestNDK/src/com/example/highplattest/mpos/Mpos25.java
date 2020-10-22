package com.example.highplattest.mpos;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
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
 * module 			: 设备认证模块
 * file name 		: Auth5
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集获取随机数
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180517		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos25 extends UnitFragment
{
	private final String TESTITEM = "设备认证(mpos)";
	private String fileName=Mpos25.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	private String MAINKEY = "11111111111111111111111111111111";// 主密钥
	private String WORKINGKEY_DATA_MAC = "DBFE96D0A5F09D24";// mac密钥
	
	public void mpos25()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		int iRet1=-1,iRet2= -1;
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		SecKcvInfo kcvInfo = new SecKcvInfo();
		kcvInfo.nCheckMode =0;
		kcvInfo.nLen = 4;
		String retCode;
		byte[] retContent;
		
		// case1:设备认证操作
		/*byte[] ysIndex = {0x02,0x00,0x07,0x1A,0x23,0x2F,0x03,0x00,0x00,0x04,0x03,0x15};
		K21DeviceResponse response = k21ControllerManager.sendCmd(new K21DeviceCommand(ysIndex), null);
		if(response.getInvokeResult() != CommandInvokeResult.SUCCESS)
		{
			gui.cls_show_msg1(2, SERIAL,"line %d:设置银商认证密码索引失败(%s)", Tools.getLineInfo(),TESTITEM,response.getInvokeResult());
			if(!GlobalVariable.isContinue)
				return;
		}*/
		// 主密钥、mac密钥
		JniNdk.JNI_Sec_LoadKey((byte)0, (byte)1, (byte)0, (byte)1, 16, ISOUtils.hex2byte(MAINKEY), kcvInfo);
		JniNdk.JNI_Sec_LoadKey((byte)1, (byte)3, (byte)1, (byte)4, 8, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), kcvInfo);
		/**设备认证要先存在/appfs/yssn.in文件*/
		if((iRet1= JniNdk.JNI_FsExist("/appfs/yssn.in"))!=NDK_OK||(iRet2 = JniNdk.JNI_FsExist("/appfs/pinksn.in"))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, "auth5", gKeepTimeErr, "line %d:(%d,%d)请先把SVN的device_cert目录下yssn.in和pinksn.in文件使用CopyFileSystem.apk放置到K21端的/appfs/的根目录下", Tools.getLineInfo(),iRet1,iRet2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Auth_F103), null);
		LoggerUtil.v("response:"+ISOUtils.hexString(response.getResponse()));
		retContent = response.getResponse();
		retCode = ISOUtils.dumpString(retContent, 7, 2);
		if(retCode.equals("00"))
		{
			int outIndex = 11;
			int outLen = retContent[10]/16*10+retContent[10]%16;
			LoggerUtil.v("outLen:"+outLen);
			gui.cls_show_msg1_record(fileName, "auth5", gKeepTimeErr, "认证输出数据:%s", ISOUtils.hexString(retContent, outIndex, outLen));
			int addIndex = 11+outLen+2;
			int addLen = retContent[addIndex-1]/16*10+retContent[addIndex-1]%16;
			gui.cls_show_msg1_record(fileName, "auth5", gKeepTimeErr, "附加数据:%s", ISOUtils.hexString(retContent, addIndex, addLen));
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "auth5", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, "auth5", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.close();
	}

}
