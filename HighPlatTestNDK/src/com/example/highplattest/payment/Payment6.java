package com.example.highplattest.payment;

import android.annotation.SuppressLint;
import android.newland.security.CertificateInfo;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android更新支付证书
 * file name 		: Payment6.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20170109
 * directory 		: 
 * description 		: 是否安装新签名证书
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20170109 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Payment6 extends UnitFragment
{
	private final String TESTITEM = "是否使用新签名证书";
	private String fileName=Payment6.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);

	public void payment6()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "systemversion6", gKeepTimeErr,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/*private & local definition*/
		
		CertificateInfo certificateInfo = new CertificateInfo(myactivity);
		boolean isNewAppSign = false;
		
		// 返回值说明 返回true:新证书  false:旧证书
		try 
		{
			/*process body*/
			gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
			// case1:未放入任何证书，应为旧签名体系
			gui.cls_show_msg("请确保设备未安装证书，后点【确认】继续");
			if((isNewAppSign = certificateInfo.isNewAppSign())==true)
			{
				gui.cls_show_msg1_record(fileName, "systemversion6", gKeepTimeErr,"line %d:%s获取安装证书错误（%s）", Tools.getLineInfo(),TESTITEM,isNewAppSign);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case2:请放入旧签名证书，此时获得的证书方案应为旧签名方案
			gui.cls_show_msg("请确保设备已安装旧签名证书");
			if((isNewAppSign = certificateInfo.isNewAppSign())==true)
			{
				gui.cls_show_msg1_record(fileName, "systemversion6", gKeepTimeErr,"line %d:%s获取安装证书错误（%s）", Tools.getLineInfo(),TESTITEM,isNewAppSign);
				if(!GlobalVariable.isContinue)
					return;
			}
			// case3:请放入新签名证书，此时获得的证书方案应为新签名方案
			gui.cls_show_msg("请确保设备已安装新签名证书");
			if((isNewAppSign = certificateInfo.isNewAppSign())==false)
			{
				gui.cls_show_msg1_record(fileName, "systemversion6", gKeepTimeErr,"line %d:%s获取安装证书错误（%s）", Tools.getLineInfo(),TESTITEM,isNewAppSign);
				if(!GlobalVariable.isContinue)
					return;
			}
			/*// case4:清证书后应为旧签名证书，此时获得的证书方案应为旧签名方案，清证书操作会重启无法测试
			isUseDilog(HandlerMsg.DIALOG_COM_SYSTEST_SINGLE, "清除设备证书后点击是", SERIAL);
			if((isNewAppSign = certificateInfo.isNewAppSign())==true)
			{
				gui.cls_show_msg1_record(fileName, "systemversion6", gKeepTimeErr,"line %d:%s获取安装证书错误（%s）", Tools.getLineInfo(),TESTITEM,isNewAppSign);
				if(!GlobalVariable.isContinue)
					return;
			}*/
			gui.cls_show_msg1_record(fileName, "systemversion6",gScreenTime,"%s测试通过", TESTITEM);
		} catch (Exception e) 
		{
			gui.cls_show_msg1(1, "该固件版本本用例不支持");
		}
		gui = null;
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}
