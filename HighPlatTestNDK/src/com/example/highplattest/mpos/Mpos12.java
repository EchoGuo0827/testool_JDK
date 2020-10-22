package com.example.highplattest.mpos;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
/************************************************************************
 * 
 * module 			: 安全模块
 * file name 		: sec4
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180517
 * directory 		: 
 * description 		: mpos指令集安装主密钥
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180517		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos12 extends UnitFragment
{
	private final String TESTITEM = "主密钥安装(mpos)";
	public final String TAG = Mpos12.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	public void mpos12()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		byte[] retContent;
		String kcv;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		
		/*process body*/
		// 测试前置：擦除密钥
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		
		// case1.1:密钥校验值错误,应返回41
		byte[] pack_TMK_err1 = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02020016A67E197C00818E9BA67E197C00818E9Bff000411111111"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK_err1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("41")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case1.2:主密钥数据长度错误,应返回45
		byte[] pack_TMK_err2 = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02020016A67E197C00818E9BA67E197C0081111111111111111"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK_err2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("45")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.1:传输密钥安装传输密钥，ID = 1
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:安装TLK密钥失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 校验返回的kcv值
		if((kcv = ISOUtils.hexString(retContent,11,6)).equals("40826A580060")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:kcv校验错误(%s)", Tools.getLineInfo(),kcv);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.2:传输密钥加密主密钥,ID = 2(明文E14647E8A135061AE14647E8A135061A)
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02020016A67E197C00818E9BA67E197C00818E9Bff0004FBDD2EC5"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.3:主密钥加密主密钥，ID = 5
		byte[] pack_TMK2=CalDataLrc.mposPack(new byte[]{0x1A,0x02},ISOUtils.hex2byte("0405001689BBD66A20CD322789BBD66A20CD32270200049F815527"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK2), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.4:使用TR31格式
		byte[] pack_tr31_tmk = CalDataLrc.mposPack(new byte[]{0x1A,0x02},ISOUtils.hex2byte("01010016253c9d9d7c2fbbfa253c9d9d7c2fbbfaff000482e13665"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_tr31_tmk), null);
		retContent = response.getResponse();
		if((retCode = ISOUtils.dumpString(retContent,9,2)).equals("00")==false)
		{
			gui.cls_show_msg1_record(TAG, "sec4", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case2.5:主密钥采用发行方私钥加密的格式
		
		gui.cls_show_msg1_record(TAG, "sec4", gScreenTime, "%s测试通过", TESTITEM);
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
