package com.example.highplattest.main.netutils;

import com.example.highplattest.main.constant.ParaEnum.LinkType;
/************************************************************************
 * 
 * module 			: main
 * file name 		: EthernetPara.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 以太网动/静IP配置
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class EthernetPara extends NetWorkingBase
{
	// 动态/静态IP，默认是用静态
	private boolean DHCPenable = false;
	
	public EthernetPara()
	{
		setType(LinkType.ETH);
	}

	public boolean isDHCPenable() 
	{
		return DHCPenable;
	}

	public void setDHCPenable(boolean dHCPenable) 
	{
		DHCPenable = dHCPenable;
	}
	
	
}
