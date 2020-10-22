package com.example.highplattest.payment;

import android.annotation.SuppressLint;
import android.newland.security.SignatureComparison;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android更新支付证书
 * file name 		: updatepayment1.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141212 
 * directory 		: 
 * description 		: Android更新支付证书init_x509函数
 * related document : 
 * history 		 	: 变更记录									变更时间			变更人员
 *			  		  A7平台以上不支持init_x509，不支持统一返回-2		20200506	 	郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Payment1 extends UnitFragment	//需要系统权限或者mtms
{
	
	/**
	 * -1：表示需要更新的证书文件不存在
	 * -2：表示旧的证书文件不存在
	 * -3：表示签名不正确
	 * 0：表示新旧证书相同
	 * 1：表示更新成功
	 * 2：表示更新失败
	 */
	private final String TESTITEM = "更新支付证书init_x509函数";
	private String path;
	Gui gui = new Gui(myactivity, handler);
	private String fileName="Payment1";
	@SuppressLint("SdCardPath")
	public void payment1() 
	{
		
		/* private & local definition */
		path = GlobalVariable.sdPath;
		int ret = 3;
		String PaymentCertificateSign = path+"Payment.x509.pem_new.sig", newPaymentCertificate = path+"Payment.x509.pem_new";
		String FalsePaymentCertificateSign = path+"Payment.x509.pem_false.sig",FalsenewPaymentCertificate = path+"Payment.x509_false.pem_new";
		String no_exitPaymentCertificateSign = path+"Payment.x509.pem_new_noexit.sig", no_exitnewPaymentCertificate = path+"Payment.x509_noexit.pem_new";
		
		
		/* Process body */
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);

		if (GlobalVariable.gCurPlatVer != Platform_Ver.A5) {
			if(gui.cls_show_msg("init_x509属于A7和A9平台保留接口，返回值全为-2，仍要测试[确认]，其他[退出]")!=ENTER)
			{
				unitEnd();
				return;
			}
		}
		//case1:在签名不存在(PaymentCertificateSign不存在)的情况下预期返回-1
		if ((ret = SignatureComparison.init_x509(no_exitPaymentCertificateSign,newPaymentCertificate)) != -1)
		{
			gui.cls_show_msg1_record(fileName,"payment1",gKeepTimeErr,"line %d:测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2:在 签名不正确(PaymentCertificateSign不正确)的情况下预期返回-3
		if ((ret = SignatureComparison.init_x509(FalsePaymentCertificateSign,newPaymentCertificate)) != -3)
		{
			gui.cls_show_msg1_record(fileName,"payment1",gKeepTimeErr,"line %d:测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3:在需要更新的证书文件不存在(newPaymentCertificate不存在)的情况下预期返回-1
		if ((ret = SignatureComparison.init_x509(PaymentCertificateSign,no_exitnewPaymentCertificate)) != -1)
		{
			gui.cls_show_msg1_record(fileName,"payment1",gKeepTimeErr,"line %d:测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case4:证书不正确(newPaymentCertificate不正确)的情况下预期返回-3
		if ((ret = SignatureComparison.init_x509(PaymentCertificateSign,FalsenewPaymentCertificate)) != -3)
		{
			gui.cls_show_msg1_record(fileName,"payment1",gKeepTimeErr,"line %d:测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		Log.e("PaymentCertificateSign", PaymentCertificateSign+" ");
		Log.e("newPaymentCertificate", newPaymentCertificate+" ");
		//case5:更新成功，证书只能更新一次，再次更新前需要还原签名证书
		if ((ret = SignatureComparison.init_x509(PaymentCertificateSign,newPaymentCertificate)) != 1)
		{
			gui.cls_show_msg1_record(fileName,"payment1",gKeepTimeErr, "line %d:测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case6:更新成功后，用原有的签名信息再更新一次，预期应该返-3
		if ((ret = SignatureComparison.init_x509(PaymentCertificateSign,newPaymentCertificate)) != -3)
		{
			gui.cls_show_msg1_record(fileName,"payment1",gKeepTimeErr,"line %d:测试失败(%d)", Tools.getLineInfo(),ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case7:在 新旧证书相同 的情况下预期应该返回0
//		if ((ret = SignatureComparison.init_x509(自签签名信息,newPaymentCertificate)) != 0)
//		{
//			new Gui(getActivity(), handler).cls_show_msg1(2, "line %d:测试失败(ret = %d)", Tools.getLineInfo(),ret);
//		}
		
		gui.cls_show_msg1_record(fileName,"payment1",gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
	}
}
