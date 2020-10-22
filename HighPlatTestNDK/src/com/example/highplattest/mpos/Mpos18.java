package com.example.highplattest.mpos;

import java.util.concurrent.TimeUnit;
import android.newland.os.NlBuild;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.k21controller.util.Dump;
/************************************************************************
 * 
 * module 			: 安全模块
 * file name 		: sec9
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180521
 * directory 		: 
 * description 		: mpos指令集密码输入(mpos)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180521		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos18 extends UnitFragment
{
	private final String TESTITEM = "密码输入(mpos)";
	public final String CLASS_NAME = Mpos18.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	private StringBuffer str=new StringBuffer();
	
	public void mpos18()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",CLASS_NAME,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		byte[] numKey = new byte[122];
		byte[] emp = {0x00,0x00,0x00};
		byte[] szPinOut=new byte[8];
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		byte[] retContent;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 测试前置：擦除所有密钥，装载TLK和TMK、TPK、TAK、TDK
		// 擦除密钥
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		// 安装TMK,ID=5,密钥明文16个11
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02050016253C9D9D7C2FBBFA253C9D9D7C2FBBFAff000482E13665"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 明文安装TAK(16个13)，ID=1
		byte[] pack_TAK = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0305010016131313131313131313131313131313130004A8B7B5BD01"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 密文安装TPK1(16个17+16个19),ID=2
		byte[] pack_TPK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("020502001617171717171717171919191919191919000454BA082201"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:安装TPK1失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 明文安装TPK2(16个19),ID=4
		byte[] pack_TPK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("02050400161919191919191919191919191919191900049DA493AA01"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:安装TPK2失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 安装DUKPT，ID=7
		byte[] pack_dukpt1 = CalDataLrc.mposPack(new byte[]{0x1A,0x17}, ISOUtils.hex2byte("0207FFFFFFFFFFFFFFE000060016CB62659448AC8A721E28428CFDB2FD30010016CEB1BBCA24CFEADBE8FE4896E9D1228D"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_dukpt1), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:安装DUKPT密钥失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		
		// case1.1:ucKeyIdx不存在
		byte[] pack_getPin1 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("AA0000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000700000000040506ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("06")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1.2:异常测试，长度小于4，预期返回失败
		byte[] pack_getPin2 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000346464646464646464646033b000001010101010100000700000000040506ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("06")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case3:以SEC_PIN_ISO9564_0,pszExpPinLenIn=0,pszDataIn!=NULL,应成功
		touchscreen_getnum(numKey);
		byte[] pack_random = CalDataLrc.mposPack(new byte[]{0x1A,0x22},ISOUtils.concat(numKey, emp));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.append("请等待60s后按[确认],等待过程中不应进入休眠");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin4 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000700000000040506ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin4), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("0000000000000000"), 8) == false) {
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		if (gui.ShowMessageBox("看到本信息前是否进入过休眠".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK) 
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if (GlobalVariable.isContinue == false) 
				return;
		}

		// case17:验证输入长度限制出现重复"0,0,0"预期按确认返回NDK_OK，应用发现该情况下按确认会返回失败-1121的BUG，因此导入该用例
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("请尽快按确认键...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin5 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b0000010101010101000003000000ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin5),60,TimeUnit.SECONDS, null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("0000000000000000"), 8) == false) {
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case5:以SEC_PIN_ISO9564_0,pszExpPinLenIn=12,pszDataIn!=NULL,应成功
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("尽快输入123456789012并确认...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin3 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("040000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000304050Cff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin3), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("ACE02DA182550D7B"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case6:以SEC_PIN_ISO9564_0,pszExpPinLenIn=13,pszDataIn!=NULL,应失败
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:"+ISOUtils.hexString(retContent, 9, 15));
		str.append("尽快输入1234567890123并确认...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin6 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000D46464646464646464646033b000001010101010100000106ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin6), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("06")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		
		// case7:以SEC_PIN_ISO9564_0,pszExpPinLenIn=12,pszDataIn!=NULL
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("尽快输入123456(将不会显示输入按键)并按2次取消...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin7 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000700000000040506ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin7), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 按键保护
		gui.cls_show_msg1(gScreenTime, "子用例已通过！不要再按取消...", TESTITEM);
		
		// case8:以SEC_PIN_ISO9564_0,pszExpPinLenIn="4,5,12",pszDataIn!=NULL
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("尽快输入12345并确认....");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin8 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000304050Cff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin8), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("E77E0E00FDA495C0"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case11:以SEC_PIN_ISO9564_0,pszExpPinLenIn="4",pszDataIn!=NULL,psPinBlockOut=NULL,ucKeyIdx=TAK'sID
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		byte[] pack_getPin11 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("010000363232353838353931363136333135373436363600000646464646464646464646033b00000101010101010000070000000004050Cff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin11), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("06")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		

		// case13:以SEC_PIN_ISO9564_0,pszExpPinLenIn="4",pszDataIn!=NULL,psPinBlockOut=NULL,ucKeyIdx=DUKPT'sID
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		byte[] pack_getPin13 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("070000363232353838353931363136333135373436363600000646464646464646464646033b00000101010101010000070000000004050Cff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin13), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("06")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case14:以SEC_PIN_ISO9564_0,pszExpPinLenIn=4,pszDataIn!=NULL,应成功
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("输入1234并确认...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin14 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000446464646464646464646033b000001010101010100000104ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin14), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("C34455FF69B94E20"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case15: 在不为预期输入位数时候不让确认
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("尽快输入12345并确认,若无反应继续输6并按确认");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin15 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000106ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin15), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("F781CCFAA4C8A6D7"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case16: 在有预期输入位数限定后应不能超过最大值
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("请尝试输入123456789,应无法输入9,再按确认");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin16 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b0000010101010101000003080604ff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin16), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("99A9067E7FFA9D14"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case17:数字键固定，可输入123456789012
		byte[] num_fix = {0x01,0x00,0x10,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30};
		byte[] pack_num_fix = CalDataLrc.mposPack(new byte[]{0x1A,0x22},ISOUtils.concat(numKey, num_fix));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_num_fix), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n123\n456\n789\n  0\n");
		str.append("请尝试输入123456789012...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin17 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000308060Cff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin17), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("FC37F606F48887D3"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		// case18:数字键随机，功能键随机，可输入12345678
		byte[] num_random_all = {0x02,0x00,0x15,0x7E,0x7E,0x7E,0x7F,0x7E,0x7E,0x7E,0x7F,0x7E,0x7E,0x7E,0x7F,0x7E,0x7F,0x0D};
		byte[] pack_num_random = CalDataLrc.mposPack(new byte[]{0x1A,0x22},ISOUtils.concat(numKey, num_random_all));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_num_random), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:随机密码键盘布局失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		str.delete(0, str.length());
		str.append("密码键盘序列:\n"+ISOUtils.dumpString(retContent, 11, 3)+"\n"+ISOUtils.dumpString(retContent, 15, 3)+"\n"+ISOUtils.dumpString(retContent, 19, 3)+"\n"+ISOUtils.dumpString(retContent, 23, 1));
		str.append("请尝试输入12345678...");
		gui.cls_printf(str.toString().getBytes());
		byte[] pack_getPin18 = CalDataLrc.mposPack(new byte[]{0x1A,0x01},ISOUtils.hex2byte("020000363232353838353931363136333135373436364600000C46464646464646464646033b000001010101010100000308060Cff"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_getPin18), 60,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else
		{
			System.arraycopy(retContent, 11, szPinOut, 0, 8);
			if (Tools.memcmp(szPinOut, ISOUtils.hex2byte("99A9067E7FFA9D14"), 8) == false) 
			{
				gui.cls_show_msg1_record(CLASS_NAME, "sec10", gKeepTimeErr, "line %d:%s测试失败(szPinOut = %s)", Tools.getLineInfo(), TESTITEM,Dump.getHexDump(szPinOut));
				if (GlobalVariable.isContinue == false) 
					return;
			}
		}
		
		gui.cls_show_msg1_record(CLASS_NAME, "sec10", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	/**
	 *随机或固定密码键盘布局，密码键盘的位置显示在下半部分，修改为动态获取
	 * @param str
	 * @return
	 */
	public int touchscreen_getnum(byte[] numKey) 
	{
		int ret = 0;
		int x0 = 0, x1 = 0, x2 = 0, x3 = 0, x4 = 0, y0 = 0, y1 = 0, y2 = 0, y3 = 0, y4 = 0;
		int heightTap,widthTap=0,touchHeight = 0,statusBar = GlobalVariable.StatusHeight;
		
		if(Tools.isSupportTouch(NlBuild.VERSION.NL_FIRMWARE))
		{
			String touch = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
			int index = touch.indexOf('x');
			// 触屏值控制
			touchHeight = Integer.parseInt(touch.substring(0, index));
			widthTap = Integer.parseInt(touch.substring(index+1))/4;
		}
		else
		{
			return NDK_ERR;
		}
		
		heightTap = (touchHeight-GlobalVariable.StatusHeight-GlobalVariable.TitleBarHeight)/8;
		
		x0 = 0;x1 = widthTap;x2 = widthTap*2;x3 = widthTap*3;x4 = widthTap*4;
		y0 = statusBar+heightTap*4;y1 = statusBar+heightTap*5;y2 = statusBar+heightTap*6;y3 = statusBar+heightTap*7;y4 = statusBar+heightTap*8;
		
		LoggerUtil.e(String.format("x0 = %d,x1 = %d,x2= %d,x3 = %d,x4 = %d",x0,x1,x2,x3,x4));
		
		LoggerUtil.e(String.format("y0 = %d,y1 = %d,y2= %d,y3 = %d,y4 = %d",y0,y1,y2,y3,y4));
		
		int[] numInt = { 
				x0, y0, x1, y1, x1, y0, x2, y1, x2, y0, x3, y1, x3, y0, x4, y1,// 取消
				x0, y1, x1, y2, x1, y1, x2, y2, x2, y1, x3, y2, x3, y1, x4, y2,// 退格
				x0, y2, x1, y3, x1, y2, x2, y3, x2, y2, x3, y3, 
				x0, y3, x1, y4, x1, y3, x2, y4, x2, y3, x3, y4, x3, y2, x4,y4};

		numKey[0]=0x01;
		numKey[1]=0x20;
		for (int i = 0, j = 2; i < 60; i++, j++) 
		{
			numKey[j] = (byte) ((numInt[i] >> 8) & 0xff);
			j++;
			numKey[j] = (byte) (numInt[i] & 0xff);
		}
		
		return ret;
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		k21ControllerManager.close();
	}

}
