package com.example.highplattest.apn;

import java.util.ArrayList;
import java.util.List;

import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/**
* file name 		: Apn7.java
* history 		 	: 变更记录													变更时间			变更人员
*			  		  添加一些A5预置过的物联网APN(N910_A7_V1.0.05导入)	       		20200908   		郑薛晴
* isAuto(Y)
************************************************************************ 
* log : Revision no message(created for Android platform)
************************************************************************/
public class Apn7 extends UnitFragment{
	
	private final String  TESTITEM = "添加A5预制过的APN";
	private final String FILE_NAME = Apn7.class.getSimpleName();
	private Gui gui;

	public void apn7()
	{
		gui = new Gui(myactivity, handler);
		// 预置的物联网APN,开发提供的说明
		/*1.  <apn carrier="China Mobile"
				    apn="GZEYLK02.GD"
				    mcc="460"
				    mnc="00"
				    type="default,net,supl"
				    protocol="IPV4V6"
				    roaming_protocol="IPV4V6"
				  />*/
		
		/*2.  <apn carrier="China Unicom 3G"
				    apn="GZEYLK.GD"
				    mcc="460"
				    mnc="01"
				    proxy=""
				    port="80"
				    type="default"
				  />*/
		
		/* 3. <apn carrier="CMIOT"
				    apn="CMIOT"
				    mcc="460"
				    mnc="04"
				    type="default,net,supl"
				    protocol="IPV4V6"
				    roaming_protocol="IPV4V6"
				  />*/
		/*4.  <apn carrier="CMMTM"
				    apn="CMMTM"
				    mcc="460"
				    mnc="04"
				    type="default,net,supl"
				    protocol="IPV4V6"
				    roaming_protocol="IPV4V6"
				  />*/
		/*5.  <apn carrier="TLGX"
				    apn="CMIOTTLPOS.GX"
				    mcc="460"
				    mnc="04"
				    type="default,net,supl"
				    protocol="IPV4V6"
				    roaming_protocol="IPV4V6"
				  />*/
		
		/*6.  <apn carrier="China Unicom"
				    apn="M2M.NJM2MAPN"
				    mcc="460"
				    mnc="06"
				    type="default"
				  />*/
		
		/*  <apn carrier="China Unicom"
				    apn="unim2m.njm2mAPN"
				    mcc="460"
				    mnc="06"
				    type="default"
				  />*/
		if(gui.cls_show_msg("请将设备恢复出厂设置或APN重置为默认设置后成功(需要插入sim卡)后再进入本用例,已操作过点确认键测试")!=ENTER)
		{
			return;
		}
		gui.cls_show_msg1(1, "%s测试中",TESTITEM);
		List<ApnTestBean> apnTestBeans = new ArrayList<ApnTestBean>();
		
		ApnTestBean apnTestBean1 = new ApnTestBean();
		apnTestBean1.setTemApn("GZEYLK02.GD");
		apnTestBean1.setTemMcc("460");
		apnTestBean1.setTemMnc("00");
		apnTestBean1.setTemType("default,net,supl");
		apnTestBean1.setTemProtocal("IPV4V6");
		apnTestBean1.setTemRoamingPro("IPV4V6");
		
		ApnTestBean apnTestBean2 = new ApnTestBean();
		apnTestBean2.setTemApn("GZEYLK.GD");
		apnTestBean2.setTemMcc("460");
		apnTestBean2.setTemMnc("01");
		apnTestBean2.setTemProxy("");
		apnTestBean2.setTemPort("80");
		apnTestBean2.setTemType("default");
		
		ApnTestBean apnTestBean3 = new ApnTestBean();
		apnTestBean3.setTemApn("CMIOT");
		apnTestBean3.setTemMcc("460");
		apnTestBean3.setTemMnc("04");
		apnTestBean3.setTemType("default,net,supl");
		apnTestBean3.setTemProtocal("IPV4V6");
		apnTestBean3.setTemRoamingPro("IPV4V6");
		
		ApnTestBean apnTestBean4 = new ApnTestBean();
		apnTestBean4.setTemApn("CMMTM");
		apnTestBean4.setTemMcc("460");
		apnTestBean4.setTemMnc("04");
		apnTestBean4.setTemType("default,net,supl");
		apnTestBean4.setTemProtocal("IPV4V6");
		apnTestBean4.setTemRoamingPro("IPV4V6");
		
		ApnTestBean apnTestBean5 = new ApnTestBean();
		apnTestBean5.setTemApn("CMIOTTLPOS.GX");
		apnTestBean5.setTemMcc("460");
		apnTestBean5.setTemMnc("04");
		apnTestBean5.setTemType("default,net,supl");
		apnTestBean5.setTemProtocal("IPV4V6");
		apnTestBean5.setTemRoamingPro("IPV4V6");
		
		ApnTestBean apnTestBean6 = new ApnTestBean();
		apnTestBean6.setTemApn("M2M.NJM2MAPN");
		apnTestBean6.setTemMcc("460");
		apnTestBean6.setTemMnc("06");
		apnTestBean6.setTemType("default");
		
		ApnTestBean apnTestBean7 = new ApnTestBean();
		apnTestBean7.setTemApn("unim2m.njm2mAPN");
		apnTestBean7.setTemMcc("460");
		apnTestBean7.setTemMnc("06");
		apnTestBean7.setTemType("default");
		
		apnTestBeans.add(apnTestBean1);
		apnTestBeans.add(apnTestBean2);
		apnTestBeans.add(apnTestBean3);
		apnTestBeans.add(apnTestBean4);
		apnTestBeans.add(apnTestBean5);
		apnTestBeans.add(apnTestBean6);
		apnTestBeans.add(apnTestBean7);
		
		int findCount=0;
		ApnUtils apnUtils = new ApnUtils(myactivity);
		List<ApnEntity> apnEntitys =  apnUtils.getAllApnList();
		for (ApnEntity apnEntity:apnEntitys) {
			for (ApnTestBean apnTestBean:apnTestBeans) {
//				LoggerUtil.d(apnEntity.getApn()+"|||"+apnEntity.getMcc()+"|||"+apnEntity.getMnc()+"|||"+apnEntity.getType()+"|||");
				// 预期的apn值不存在该属性可继续往下走，存在该属性值就需要比对属性值的正确性
					if(apnTestBean.getTemApn()==null||(apnTestBean.getTemApn()!=null&&apnEntity.getApn().equals(apnTestBean.getTemApn())))
					{
						LoggerUtil.d(apnEntity.getApn()+"|||"+apnEntity.getMcc()+"|||"+apnEntity.getMnc()+"|||"+apnEntity.getType()+"|||");
						if(apnTestBean.getTemMcc()==null||(apnTestBean.getTemMcc()!=null&&apnEntity.getMcc().equals(apnTestBean.getTemMcc())))
						{
							if(apnTestBean.getTemMnc()==null||(apnTestBean.getTemMcc()!=null&&apnEntity.getMnc().equals(apnTestBean.getTemMnc())))
							{
								if(apnTestBean.getTemType()==null||(apnTestBean.getTemType()!=null&&apnEntity.getType().equals(apnTestBean.getTemType())))
								{
									if(apnTestBean.getTemProtocal()==null||(apnTestBean.getTemProtocal()!=null&&apnEntity.getProtocol().equals(apnTestBean.getTemProtocal())))
									{
										if(apnTestBean.getTemRoamingPro()==null||(apnTestBean.getTemRoamingPro()!=null&&apnEntity.getRoamingProtocol().equals(apnTestBean.getTemRoamingPro())))
										{
											if(apnTestBean.getTemProxy()==null||(apnTestBean.getTemProxy()!=null&&apnEntity.getProxy().equals(apnTestBean.getTemProxy())))
											{
												if(apnTestBean.getTemPort()==null||(apnTestBean.getTemPort()!=null&&apnEntity.getPort().equals(apnTestBean.getTemPort())))
												{
													LoggerUtil.v(apnEntity.getPort());
													findCount++;
													LoggerUtil.v("find apn="+findCount);
													if(findCount==7)
														break;
												}	
											}
										}
									}
								}
							}
						}
					}
				continue;
			}
			if(findCount==7)
				break;
		}
		
		// 全部找到视为测试通过
		if(findCount!=7)
		{
			gui.cls_show_msg1_record(FILE_NAME, "apn7", gKeepTimeErr, "line %d:%s查找新增的7个apn失败(%d)",Tools.getLineInfo(),TESTITEM,findCount);
			if(!GlobalVariable.isContinue)
				return;
		}
		gui.cls_show_msg1_record(FILE_NAME, "apn7", gKeepTimeErr, "%s测试通过",TESTITEM);
	}
	
	private class ApnTestBean
	{
		String temApn;
		String temMcc;
		String temMnc;
		String temProxy;
		String temPort;
		String temType;
		String temProtocal;
		String temRoamingPro;
		
		
		
		public String getTemProxy() {
			return temProxy;
		}
		public void setTemProxy(String temProxy) {
			this.temProxy = temProxy;
		}
		public String getTemPort() {
			return temPort;
		}
		public void setTemPort(String temPort) {
			this.temPort = temPort;
		}
		public String getTemApn() {
			return temApn;
		}
		public void setTemApn(String temApn) {
			this.temApn = temApn;
		}
		public String getTemMcc() {
			return temMcc;
		}
		public void setTemMcc(String temMcc) {
			this.temMcc = temMcc;
		}
		public String getTemMnc() {
			return temMnc;
		}
		public void setTemMnc(String temMnc) {
			this.temMnc = temMnc;
		}
		public String getTemType() {
			return temType;
		}
		public void setTemType(String temType) {
			this.temType = temType;
		}
		public String getTemProtocal() {
			return temProtocal;
		}
		public void setTemProtocal(String temProtocal) {
			this.temProtocal = temProtocal;
		}
		public String getTemRoamingPro() {
			return temRoamingPro;
		}
		public void setTemRoamingPro(String temRoamingPro) {
			this.temRoamingPro = temRoamingPro;
		}
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
